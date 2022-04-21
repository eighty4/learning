const jwt = require('jsonwebtoken')
const fs = require('fs')

module.exports = {

    sign: (email, expires) => jwt.sign(
        {email},
        fs.readFileSync('./jwt.private.pem'),
        {expiresIn: expires || '1 week', algorithm: 'RS256'}
    ),

    verify: token => jwt.verify(
        token,
        fs.readFileSync('./jwt.public.pem'),
        {algorithms: ['RS256']}
    ),
}
