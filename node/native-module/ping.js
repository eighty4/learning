let addon = require('bindings')('ping');
module.exports = {
    ping: addon.ping,
}
