const jwt = require('jsonwebtoken')
const {readKey} = require('./jwt-keys')

if (process.argv.length < 3) {
    throw new Error('use `node jwt-sign.js EMAIL`')
}

const email = process.argv[2]
const expiresIn = process.length > 3 ? process.argv[3] : '1 week'
const token = jwt.sign(
    {email},
    readKey('./jwt.private.pem'),
    {expiresIn, algorithm: 'RS256'}
)

console.log(token)
