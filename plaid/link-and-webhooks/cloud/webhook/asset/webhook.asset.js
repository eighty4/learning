exports.assetWebhook = (req, res) => {
    if (req.body.webhook_type !== 'ASSET') {
        console.log('rejecting webhook_type=' + req.body.webhook_type)
        res.status(400).send()
        return
        // or throw error?
    }
    switch (req.body.webhook_code) {
        default:
            console.log('rejecting asset webhook_code=' + req.body.webhook_code)
            res.status(400).send()
            return
    }
}
