const fs = require('fs')
const jwt = require('jsonwebtoken')

const readKey = path => {
    if (!path) {
        throw new Error('no key')
    }
    return fs.readFileSync(path).toString()
}

const privKey = readKey(process.env.BANK_JWT_PRIVATE_KEY)

const pubKey = readKey(process.env.BANK_JWT_PUBLIC_KEY)

const SIGN_OPTS = {expiresIn: '1 week', algorithm: 'RS256'}

const createToken = ({userId, email}) => new Promise((res, rej) => {
    jwt.sign(
        {userId, email},
        privKey,
        SIGN_OPTS,
        (err, token) => {
            if (err) {
                rej(err)
            } else {
                res(token)
            }
        }
    )
})

const VERIFY_OPTS = {algorithms: ['RS256']}

const verifyToken = token => new Promise((res, rej) => {
    jwt.verify(
        token,
        pubKey,
        VERIFY_OPTS,
        (err, decoded) => {
            if (err) {
                rej(err)
            } else {
                res(decoded)
            }
        }
    )
})

module.exports = {
    createToken,
    verifyToken,
}
