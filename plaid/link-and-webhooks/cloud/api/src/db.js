const {Client} = require('pg')
const Pool = require('pg-pool')

const connOpts = {
    database: 'eighty4',
    host: 'localhost',
    user: 'admin',
    password: 'admin',
    max: 20,
    idleTimeoutMillis: 30000,
    connectionTimeoutMillis: 2000,
}

const createClient = () => {
    return new Client(connOpts)
}

const createConnectionPool = () => {
    return new Pool(connOpts)
}

module.exports = {
    createClient,
    createConnectionPool,
}
