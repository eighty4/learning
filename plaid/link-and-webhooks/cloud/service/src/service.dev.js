require('bank-webhook-local').start()
    .then(url => {
        process.env.BANK_PLAID_WEBHOOK_ADDRESS = url
        require('./service')
    })
    .catch(e => {
        console.log('failed starting local webhook', e)
    })
