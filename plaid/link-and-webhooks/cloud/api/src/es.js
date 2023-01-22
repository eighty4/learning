const {Client} = require('@elastic/elasticsearch')

const createClient = () => new Client({node: 'http://localhost:9200'})

module.exports = {
    createClient,
}
