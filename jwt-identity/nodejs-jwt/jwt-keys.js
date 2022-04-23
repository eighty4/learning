const fs = require('fs')

const readKey = filename => fs.readFileSync(filename)

module.exports = {readKey}
