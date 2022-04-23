const jwt = require('jsonwebtoken')
const {readKey} = require('./jwt-keys')

if (process.argv.length !== 3) {
    throw new Error('use `node jwt-verify.js EMAIL`')
}

const token = process.argv[2]
const claims = jwt.verify(
    token,
    readKey('./jwt.public.pem'),
    {algorithms: ['RS256']}
)

console.log(claims)
