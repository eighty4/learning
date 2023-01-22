const {DateTime} = require('luxon')

class TxSync {
    bankDao
    txDao

    constructor(bankDao, plaidClient, txDao) {
        this.bankDao = bankDao
        this.plaidClient = plaidClient
        this.txDao = txDao
    }

    async handleTxWebhook(req) {
        const {linkedBankId, webhookCode, txCount, txIdsToRemove} = req
        return (() => {
            switch (webhookCode) {
                case 'INITIAL_UPDATE':
                    return this.initialImport(req.linkedBankId)
                case 'HISTORICAL_UPDATE':
                    return this.historicalImport(req.linkedBankId)
                case 'DEFAULT_UPDATE':
                    return this.updateImport(req.linkedBankId, txCount)
                case 'TRANSACTIONS_REMOVED':
                    return this.removeTransactions(linkedBankId, txIdsToRemove)
            }
        })()
            .then(() => {
                console.log('tx', webhookCode, linkedBankId, 'complete')
            })
            .catch(e => {
                console.log('tx', webhookCode, linkedBankId, 'error', e.message)
            })
    }

    async initialImport(linkedBankId) {
        const now = DateTime.now()
        const startDate = now.minus({days: 31}).toFormat('yyyy-MM-dd')
        const endDate = now.toFormat('yyyy-MM-dd')
        return await this.txImport(linkedBankId, {startDate, endDate})
    }

    async historicalImport(linkedBankId) {
        const now = DateTime.now()
        const startDate = now.minus({years: 2, days: 1}).toFormat('yyyy-MM-dd')
        const endDate = now.minus({days: 29}).toFormat('yyyy-MM-dd')
        return this.txImport(linkedBankId, {startDate, endDate})
    }

    async updateImport(linkedBankId, count) {
        const now = DateTime.now()
        const startDate = now.minus({years: 2, days: 1}).toFormat('yyyy-MM-dd')
        const endDate = now.toFormat('yyyy-MM-dd')
        return this.txImport(linkedBankId, {startDate, endDate, count})
    }

    async txImport(linkedBankId, {startDate, endDate, count}) {
        const linkedBank = await this.bankDao.fetchLinkedBank(linkedBankId)
        if (!linkedBank) {
            throw new Error('no linked bank with id ' + linkedBankId)
        }

        const {accessToken, userId} = linkedBank
        const index = await this.txDao.initializeIndex(userId)

        await this.plaidClient.pageTransactions(accessToken, {startDate, endDate, count}, async (txs) => {
            const body = txs.flatMap(doc => [{index: {}}, doc])
            const result = await this.txDao.es.bulk({index, body})
            console.log(`saved ${txs.length} transactions to elasticsearch`)
            if (result.errors) {
                console.log('tx es save errors for user', userId)
                result.items.forEach(action => {
                    const op = Object.keys(action)[0]
                    if (action[op].error) {
                        console.log('tx es save error', userId, action[op].status, action[op].status)
                    }
                })
            }
        })
    }

    async removeTransactions(linkedBankId, txIds) {
        // todo delete tx from db
    }
}

module.exports = {
    TxSync,
}
