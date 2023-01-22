const Consul = require('consul')
const http = require('http')

const consul = new Consul({
    host: '192.168.56.8',
})

const server = http.createServer((req, res) => {
    res.writeHead(200)
    res.end('Hello, Consul service-discovered client!')
})

server.listen(8080, () => {
    console.log('listening on port', 8080)
    registerWithConsul()
        .then(() => {
            console.log('registered with consul')
        })
        .catch(e => {
            console.log('error registering with consul:', e.message)
        })
})

async function registerWithConsul() {
    await consul.agent.service.register({
        name: 'nodejs-app',
        address: '192.168.56.9',
        port: 8080,
        tags: ['api', 'js-api'],
    })
}

async function deregisterFromConsul() {
    await consul.agent.service.deregister({
        id: 'nodejs-app',
    })
    console.log('deregistered from consul')
}

process.on('exit', () => deregisterFromConsul().then())

process.on('SIGINT', () => deregisterFromConsul().then(() => process.exit()))

process.on('SIGTERM', () => deregisterFromConsul().then(() => process.exit()))
