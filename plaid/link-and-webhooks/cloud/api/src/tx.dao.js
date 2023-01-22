const txIndex = userId => 'bank.tx.' + userId

const txMappings = {
    transactionId: {type: 'keyword'},
    accountId: {type: 'keyword'},
    amount: {type: 'float'},
    name: {type: 'text'},
    date: {type: 'date'},
}

class TxDao {
    es

    constructor(es) {
        this.es = es
    }

    async initializeIndex(userId) {
        const index = txIndex(userId)
        try {
            await this.es.indices.create({index})
        } catch (e) {
            // todo code smell
        }

        await this.es.indices.putMapping({index, body: {properties: txMappings}})
        return index
    }

    async searchTransactions(userId, searchTerm) {
        const index = txIndex(userId)
        const query = {}
        if (isNaN(searchTerm)) {
            query.match = {name: searchTerm}
        } else {
            query.multi_match = {query: searchTerm, fields: ['amount', 'name']}
        }
        try {
            const result = await this.es.search({index, body: {query}})
            return result.body.hits.hits.map(tx => tx._source)
        } catch (e) {
            console.log('error searching es', e.message)
            throw e
        }
    }
}

module.exports = {
    TxDao,
}
