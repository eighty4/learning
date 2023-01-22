const {Configuration, PlaidApi, PlaidEnvironments} = require('plaid')

const plaidEnvs = {
    sandbox: 'SANDBOX_SECRET_GOES_HERE',
    development: 'DEVELOPMENT_SECRET_GOES_HERE',
}
const plaidEnv = process.env.BANK_PLAID_ENV || 'sandbox'
const plaidClientId = process.env.BANK_PLAID_CLIENT_ID || 'CLIENT_ID_GOES_HERE'
const plaidSecret = process.env.BANK_PLAID_SECRET || plaidEnvs[plaidEnv]
const plaidLinkTokenRequestTemplate = {
    client_name: 'Banking Buddy',
    products: ['auth', 'transactions'],
    country_codes: ['US'],
    language: 'en',
    account_filters: {
        depository: {
            account_subtypes: ['checking', 'savings'],
        },
    },
}

class PlaidClient {

    plaid = new PlaidApi(new Configuration({
        basePath: PlaidEnvironments[plaidEnv],
        baseOptions: {
            headers: {
                'PLAID-CLIENT-ID': plaidClientId,
                'PLAID-SECRET': plaidSecret,
                'Plaid-Version': '2020-09-14',
            },
        },
    }))

    async createSandboxPublicToken(institution_id) {
        const request = {
            institution_id,
            initial_products: plaidLinkTokenRequestTemplate.products,
        }
        try {
            const response = await this.plaid.sandboxPublicTokenCreate(request)
            return response.data.public_token
        } catch (e) {
            throw new Error('failed creating sandbox public token because ' + e.response.data.error_message)
        }
    }

    async createLinkToken(client_user_id) {
        const request = {
            ...plaidLinkTokenRequestTemplate,
            user: {
                client_user_id,
            },
            webhook: process.env.BANK_PLAID_WEBHOOK_ADDRESS + '/webhook/item',
        }
        let response
        try {
            response = await this.plaid.linkTokenCreate(request)
        } catch (e) {
            throw new Error('failed creating link token because ' + e.response.data.error_message)
        }
        const linkToken = response.data.link_token
        const expiration = response.data.expiration
        return {linkToken, expiration}
    }

    async accessTokenExchange(public_token) {
        const exchangeResponse = await this.plaid.itemPublicTokenExchange({public_token})
        const accessToken = exchangeResponse.data.access_token
        const linkedBankId = exchangeResponse.data.item_id
        const requestId = exchangeResponse.data.request_id
        return {accessToken, linkedBankId, requestId}
    }

    async getBankDetails(bankId) {
        const institutionResponse = await this.plaid.institutionsGetById({
            institution_id: bankId,
            country_codes: ['US'],
            options: {
                include_optional_metadata: true
            }
        })
        let bank = institutionResponse.data.institution
        if (bank.primary_color) {
            bank.primaryColor = bank.primary_color.substring(1).trim()
        }
        bank = {
            bankId,
            name: bank.name,
            logo: bank.logo,
            primaryColor: bank.primaryColor,
        }
        console.log('plaid client retrieved bank details', JSON.stringify(bank))
        return bank
    }

    async getAccounts(access_token) {
        try {
            const accounts = await this.plaid.accountsGet({access_token})
            console.log('plaid get accounts for access token', access_token, accounts.data)
            return accounts.data.accounts.map(acct => ({
                accountId: acct.account_id,
                mask: acct.mask,
                displayName: acct.name,
                officialName: acct.official_name,
                type: acct.type,
                subtype: acct.subtype,
                balances: {
                    available: acct.balances.available,
                    current: acct.balances.current,
                    limit: acct.balances.limit,
                    currencyCode: acct.balances.iso_currency_code || acct.balances.unofficial_currency_code,
                    lastUpdated: acct.balances.last_updated_datetime,
                }
            }))
        } catch (e) {
            console.log('error plaid get accounts for access token', access_token, e.message)
        }
    }

    async updateLinkWebhook(access_token, webhook) {
        await this.plaid.itemWebhookUpdate({access_token, webhook})
    }

    async pageTransactions(access_token, {start_date, end_date, count}, cb) {
        let offset = 0
        let transactionsResponse
        do {
            console.log('fetching transactions offset', offset)
            try {
                transactionsResponse = await this.plaid.transactionsGet({
                    access_token,
                    start_date,
                    end_date,
                    options: {count, offset},
                })
                console.log(`received transactions ${offset}-${offset + transactionsResponse.data.transactions.length} out of ${transactionsResponse.data.total_transactions}`)
                // transactionsResponse.data.accounts
                const transactions = transactionsResponse.data.transactions.map(tx => ({
                    transactionId: tx.transaction_id,
                    accountId: tx.account_id,
                    amount: tx.amount,
                    name: tx.name,
                    date: tx.date,
                }))
                await cb(transactions)
            } catch (e) {
                console.log('error fetching transactions: ' + e.message)
                throw e
            }
            offset += 100
        } while (transactionsResponse.data.total_transactions > offset)
    }
}

module.exports = {
    PlaidClient,
}
