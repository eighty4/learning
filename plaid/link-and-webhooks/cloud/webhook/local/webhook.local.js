const bodyParser = require('body-parser')
const express = require('express')
const ngrok = require('ngrok')
const {assetWebhook} = require('bank-webhook-asset')
const {linkWebhook} = require('bank-webhook-link')
const {txWebhook} = require('bank-webhook-tx')
const {PlaidClient, Postgres} = require('bank-api')

const WEBHOOK_PORT = 8111

const startWebhookServer = () => {
    return new Promise((resolve) => {
        const app = express()
        app.use(bodyParser.json())
        app.post('/plaid/webhook', (req, res) => {
            switch (req.body.webhook_type) {
                case 'ASSETS':
                    assetWebhook(req, res)
                    break
                case 'ITEM':
                    linkWebhook(req, res)
                    break
                case 'TRANSACTIONS':
                    txWebhook(req, res)
                    break
                default:
                    throw new Error('unhandled webhook_type')
            }
        })
        app.listen(WEBHOOK_PORT, () => {
            console.log('webhook api started on port', WEBHOOK_PORT)
            resolve()
        })
    })
}

const startNgrokProxy = async () => {
    const url = await ngrok.connect(WEBHOOK_PORT)
    console.log(`ngrok url ${url} maps to port ${WEBHOOK_PORT}`)
    return url
}

const updateWebhookUrls = async (ngrokUrl) => {
    const pc = new PlaidClient()
    const pg = Postgres.createClient()
    await pg.connect()
    try {
        const query = await pg.query('select access_token from eighty4_bank.linked_banks')
        const tokens = query.rows.map(row => row.access_token)
        if (tokens.length) {
            console.log('updating webhook for access tokens \n ' + tokens.join('\n '))
            return Promise.all(tokens.map(token => pc.updateLinkWebhook(token, ngrokUrl + '/plaid/webhook')))
        }
    } catch (e) {
        console.log('error updating webhook urls with local proxy address')
        throw e
    } finally {
        await pg.end()
    }
}

module.exports.start = async () => {
    const url = await startNgrokProxy()
    await Promise.all([
        startWebhookServer(),
        updateWebhookUrls(url).then(() => console.log('webhooks urls updated')),
    ])
    return url
}
