const tls = require('tls')
const fs = require('fs')

const options = {
    key: fs.readFileSync('learning.eighty4.local.key'),
    cert: fs.readFileSync('learning.eighty4.local.crt'),
    ca: fs.readFileSync('ca.key'),
}

const server = tls.createServer(options, (socket) => {
    console.log('socket connected')
    socket.write('socket connected')
    socket.on('data', function (data) {
        console.log('socket received %s bytes', data.length)
    })
    socket.on('end', function () {
        console.log('socket closed')
    })
})

server.listen(8080, '127.0.0.1', () => {
    console.log('listening on port', 8080)
})

server.on('error', function (error) {
    console.error(error)
    server.destroy()
})
