exports.linkWebhook = (req, res) => {
    if (req.body.webhook_type !== 'ITEM') {
        console.log('rejecting webhook_type=' + req.body.webhook_type)
        res.status(400).send()
        return
        // or throw error?
    }
    switch (req.body.webhook_code) {
        case 'ERROR':
        case 'NEW_ACCOUNTS_AVAILABLE':
        case 'PENDING_EXPIRATION':
        case 'USER_PERMISSION_REVOKED':
        case 'WEBHOOK_UPDATE_ACKNOWLEDGED':
        default:
            console.log('rejecting item webhook_code=' + req.body.webhook_code)
            res.status(400).send()
            return
    }
}
