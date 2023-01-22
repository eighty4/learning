const {PlaidClient} = require('./plaid.client')

class PlaidApi {
    bankDao
    plaidClient = new PlaidClient()

    constructor(bankDao) {
        this.bankDao = bankDao
    }

    createLinkToken(userId) {
        return this.plaidClient.createLinkToken(userId)
    }

    async linkBank(userId, bankId, publicToken) {
        try {
            await this.#linkBank(userId, bankId, publicToken)
        } catch (e) {
            console.log('error linking bank', e.message)
            throw e
        }
    }

    async sandboxLinkBank(userId) {
        const bankId = 'ins_109508'
        const publicToken = await this.plaidClient.createSandboxPublicToken(bankId)
        try {
            await this.#linkBank(userId, bankId, publicToken)
        } catch (e) {
            console.log('error sandbox linking bank', e.message)
            throw e
        }
    }

    async #linkBank(userId, bankId, publicToken) {
        const {accessToken, linkedBankId} = await this.plaidClient.accessTokenExchange(publicToken)
        console.log(`exchanged link token for access token accessToken=${accessToken} linkedBankId=${linkedBankId}`)
        await this.bankDao.saveLinkedBank(userId, bankId, linkedBankId, accessToken, {
            fetchAccounts: () => this.plaidClient.getAccounts(accessToken),
            fetchBank: () => this.plaidClient.getBankDetails(bankId),
        })
    }
}

module.exports = {
    PlaidApi,
}
