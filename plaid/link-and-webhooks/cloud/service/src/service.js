const express = require('express')
const bodyParser = require('body-parser')
const cors = require('cors')
const {isDev, BankDao, ElasticSearch, PlaidApi, Postgres, TxDao, UserApi, verifyToken} = require('bank-api')

const pg = Postgres.createConnectionPool()
const es = ElasticSearch.createClient()
const bankDao = new BankDao(pg)
const txDao = new TxDao(es)
const plaidApi = new PlaidApi(bankDao)
const userApi = new UserApi(pg)

const API_PORT = 8000
const IS_DEV = isDev()

const app = express()
app.use(bodyParser.json())
app.use(cors())

// app.use((req, res, next) => {
//     console.log(`${req.method} ${req.path}`)
//     res.on('finish', () => {
//         console.log(`${req.method} ${req.path} ${res.statusCode}`)
//     })
//     next()
// })

app.use((req, res, next) => {
    const oldWrite = res.write,
        oldEnd = res.end,
        chunks = []
    let body

    res.write = function (chunk) {
        chunks.push(chunk)
        return oldWrite.apply(res, arguments)
    }

    res.end = function (chunk) {
        if (chunk) chunks.push(chunk)
        body = Buffer.concat(chunks).toString('utf8')
        oldEnd.apply(res, arguments)
    }

    res.on('finish', () => {
        if (body.length && res.hasHeader('content-type') && res.getHeader('content-type').startsWith('application/json')) {
            try {
                body = JSON.parse(body)
            } catch (e) {
                console.log('ERROR', req.method, req.path, 'could not parse json res body `', body, '`')
                process.exit(1)
            }
        }
        console.log(`${req.method} ${req.path} ${res.statusCode}`, body)
    })

    next()
})

const authTokenMiddleware = (req, res, next) => {
    const authToken = authTokenFromRequest(req)
    if (!authToken) {
        res.status(403).send()
    } else {
        verifyToken(authToken)
            .then(({userId, email}) => {
                req.authedUser = {userId, email}
                next()
            })
            .catch(err => {
                console.log('error decoding jwt', authToken, err.message)
                res.status(403).send()
            })
    }
}

function authTokenFromRequest(req) {
    const authHeader = req.header('Authorization')
    if (authHeader && authHeader.startsWith('Bearer ')) {
        try {
            return authHeader.substring(7)
        } catch (e) {
        }
    }
}

function errorMessage(req, e) {
    const user = req.authedUser ? (' userId=' + req.authedUser.userId) : ''
    const log = `${req.method} ${req.path} error${user}: ${e.message}`
    console.log(log)
    if (IS_DEV) {
        return log
    }
}

app.post('/user/identity', async function (req, res) {
    const {email} = req.body
    if (!email) {
        res.status(400).send()
    } else {
        try {
            const {token} = await userApi.emailLogin({email})
            res.status(200).json({token})
        } catch (e) {
            res.status(500).send(errorMessage(req, e))
        }
    }
})

app.get('/plaid/link', authTokenMiddleware, async function (req, res) {
    try {
        const linkTokenResponse = await plaidApi.createLinkToken(req.authedUser.userId)
        res.status(201).json(linkTokenResponse).send()
    } catch (e) {
        res.status(500).send(errorMessage(req, e))
    }
})

app.post('/plaid/link', authTokenMiddleware, async function (req, res) {
    const {institutionId, publicToken} = req.body
    if (!institutionId || !publicToken) {
        res.status(400).send('must set institutionId and publicToken')
    } else {
        try {
            const bank = await plaidApi.linkBank(req.authedUser.userId, institutionId, publicToken)
            res.status(201).json(bank).send()
        } catch (e) {
            res.status(500).send(errorMessage(req, e))
        }
    }
})

app.get('/plaid/link/sandbox', authTokenMiddleware, async function (req, res) {
    try {
        const bank = await plaidApi.sandboxLinkBank(req.authedUser.userId)
        res.status(201).json(bank).send()
    } catch (e) {
        res.status(500).send(errorMessage(req, e))
    }
})

app.get('/accounts', authTokenMiddleware, async function (req, res) {
    let accounts
    try {
        accounts = await bankDao.fetchAccounts(req.authedUser.userId)
    } catch (e) {
        console.log(`error fetching accounts userId=${req.authedUser.userId}: ${e.message}`)
        res.status(500).send()
        return
    }

    if (!accounts || !accounts.length) {
        res.status(404).send()
    } else {
        res.json(accounts)
    }
})

app.post('/transactions/search', authTokenMiddleware, async function (req, res) {
    if (!req.body || !req.body.searchTerm) {
        res.status(400).send()
    } else {
        const transactions = await txDao.searchTransactions(req.authedUser.userId, req.body.searchTerm)
        res.json(transactions).send()
    }
})

// app.get('/events', async function (req, res) {
//     res.setHeader('Content-Type', 'text/event-stream')
//     res.setHeader('Cache-Control', 'no-cache')
//     res.setHeader('Connection', 'keep-alive')
//     res.flushHeaders()
//
//     res.on('close', () => {
//         // stop sending events
//     })
// })

app.listen(API_PORT, function () {
    console.log('api started on ' + API_PORT)
})
