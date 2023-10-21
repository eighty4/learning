const tls = require('tls')
const fs = require('fs')

const options = {
    // key: fs.readFileSync('learning.eighty4.local.key'),
    // cert: fs.readFileSync('learning.eighty4.local.crt'),
    // ca: fs.readFileSync('ca.key'),
    // rejectUnauthorized: true,
}

const socket = tls.connect(8080, '127.0.0.1', options, () => {
    console.log('socket connected', socket.authorized ? 'authorized' : 'not authorized')
    socket.write('yo, dawg, i heard you like sockets')
    socket.on('data', (data) => {
        console.log('socket received %s bytes', data.length)
    })
    socket.on('end', () => {
        console.log('socket closed')
    })
})
