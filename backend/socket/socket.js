const socketIO = require('socket.io')

let io

function initialize(server) {
    io = socketIO(server)
}

function getIO() {
    return io
}

module.exports = {
    initialize,
    getIO,
}
