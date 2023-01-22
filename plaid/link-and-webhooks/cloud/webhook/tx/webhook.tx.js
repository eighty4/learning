const {BankDao, TxDao, TxSync, Postgres, ElasticSearch} = require('bank-api')

exports.txWebhook = (req, res) => {
    const webhookType = req.body.webhook_type
    if (webhookType !== 'TRANSACTIONS') {
        console.log('rejecting webhook_type=' + webhookType)
        res.status(400).send()
        return
        // or throw error?
    }

    const {item_id, webhook_code, new_transactions, removed_transactions} = req.body
    const request = {
        linkedBankId: item_id,
        webhookCode: webhook_code,
        txCount: new_transactions,
        txIdsRemoved: removed_transactions,
    }

    const pg = Postgres.createClient()
    const es = ElasticSearch.createClient()
    const bankDao = new BankDao(pg)
    const txDao = new TxDao(es)
    const txSync = new TxSync(bankDao, txDao)
    txSync.handleTxWebhook(request).then(async () => {
        await pg.end()
        console.log('pg conn closed')
    })
}
