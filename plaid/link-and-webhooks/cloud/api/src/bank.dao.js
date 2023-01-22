const SELECT_ACCOUNTS = `
    select *
    from eighty4_bank.linked_accounts la
             left join eighty4_bank.banks lb on la.bank_id = lb.bank_id
    where user_id = $1
    order by lb.name, la.display_name
`

const SELECT_LINKED_BANK = `
    select *
    from eighty4_bank.linked_banks
    where linked_bank_id = $1
`

const SELECT_BANK = `
    select *
    from eighty4_bank.banks
    where bank_id = $1
`

const SAVE_BANK = `
    insert into eighty4_bank.banks (bank_id, logo, name, primary_color)
    values ($1, $2, $3, $4)
`

const SAVE_LINKED_BANK = `
    insert into eighty4_bank.linked_banks (linked_bank_id, access_token, bank_id, user_id)
    values ($1, $2, $3, $4)
`

const SAVE_LINKED_ACCOUNTS = `
    insert into eighty4_bank.linked_accounts (linked_account_id, linked_bank_id, user_id, bank_id, display_name,
                                              official_name, mask, currency_code, balance_available, balance_current,
                                              balance_limit, balances_updated_when, account_type, account_subtype)
    values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14)
`

class BankDao {
    pg

    constructor(pg) {
        this.pg = pg
    }

    async fetchLinkedBank(linkedBankId) {
        const selectLinkedBankResult = await this.pg.query(SELECT_LINKED_BANK, [linkedBankId])
        if (selectLinkedBankResult.rows && selectLinkedBankResult.rows.length) {
            return {
                accessToken: selectLinkedBankResult.rows[0].access_token,
                userId: selectLinkedBankResult.rows[0].user_id,
            }
        }
    }

    async fetchAccounts(userId) {
        const {rows} = await this.pg.query(SELECT_ACCOUNTS, [userId])
        if (!rows.length) {
            console.log(`no accounts found userId=${userId}`)
            return []
        }

        const resultByBankId = {}

        rows.forEach(row => {
            const accountSubtype = row['account_subtype']
            if (accountSubtype === 'savings' || accountSubtype === 'checking' || accountSubtype === 'credit card') {
                const bankId = row['bank_id']
                if (!resultByBankId[bankId]) {
                    resultByBankId[bankId] = {
                        bankId,
                        name: row['name'],
                        logo: row['logo'],
                        primaryColor: row['primary_color'],
                        accounts: {
                            credit: [],
                            checking: [],
                            savings: [],
                        },
                    }
                }
                const account = {
                    accountId: row['linked_account_id'],
                    name: row['display_name'],
                    description: row['official_name'],
                    mask: row['mask'],
                    balance: {
                        available: row['balance_available'],
                        current: row['balance_current'],
                        limit: row['balance_limit'],
                    },
                }
                if (accountSubtype === 'savings') {
                    resultByBankId[bankId].accounts.savings.push(account)
                } else if (accountSubtype === 'checking') {
                    resultByBankId[bankId].accounts.checking.push(account)
                } else if (accountSubtype === 'credit card') {
                    resultByBankId[bankId].accounts.credit.push(account)
                }
            }
        })

        const result = []
        Object.keys(resultByBankId).forEach(bankId => {
            const bank = resultByBankId[bankId]
            result.push(bank)
        })
        return result
    }

    async saveLinkedBank(userId, bankId, linkedBankId, accessToken, {fetchBank, fetchAccounts}) {
        const conn = await this.pg.connect()
        try {
            const selectBankResults = await conn.query(SELECT_BANK, [bankId])
            let bank
            if (selectBankResults.rows.length === 1) {
                bank = {
                    bankId,
                    primaryColor: selectBankResults.rows[0].primary_color,
                    logo: selectBankResults.rows[0].logo,
                    name: selectBankResults.rows[0].name,
                }
            } else if (selectBankResults.rows.length === 0) {
                console.log(`bank ${bankId} not found in db, fetching from api`)
                bank = await fetchBank()
                await conn.query(SAVE_BANK, [bankId, bank.logo, bank.name, bank.primaryColor])
            }

            try {
                await conn.query(SAVE_LINKED_BANK, [linkedBankId, accessToken, bankId, userId])
            } catch (e) {
                throw new Error(`error saving linked bank linkedBankId=${linkedBankId} bankId=${bankId} userId=${userId}: ${e.message}`)
            }

            const accounts = await fetchAccounts()
            console.log(`syncing ${accounts.length} accounts to db`)
            let insertAccountsQuery = SAVE_LINKED_ACCOUNTS
            const insertAccountsParams = []
            for (let i = 0; i < accounts.length; i++) {
                const account = accounts[i]
                insertAccountsParams.push(
                    account.accountId, linkedBankId, userId, bankId,
                    account.displayName, account.officialName, account.mask,
                    account.balances.currencyCode, account.balances.available,
                    account.balances.current, account.balances.limit,
                    account.balances.lastUpdated || new Date(),
                    account.type, account.subtype,
                )
            }
            if (accounts.length > 1) {
                for (let i = 1; i < accounts.length; i++) {
                    const ph = (i * 14) + 1
                    insertAccountsQuery += `, ($${ph}, $${ph + 1}, $${ph + 2}, $${ph + 3}, $${ph + 4}`
                    insertAccountsQuery += `, $${ph + 5}, $${ph + 6}, $${ph + 7}, $${ph + 8}, $${ph + 9}`
                    insertAccountsQuery += `, $${ph + 10}, $${ph + 11}, $${ph + 12}, $${ph + 13})`
                }
                await conn.query(insertAccountsQuery, insertAccountsParams)
            } else {
                await conn.query(insertAccountsQuery, insertAccountsParams)
            }
            bank.accounts = []
            return bank
        } finally {
            conn.release()
        }
    }
}

module.exports = {
    BankDao,
}
