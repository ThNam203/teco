require('./utils/setUpServer')
const express = require('express')

const errorHandlers = require('./controllers/errorControllers/genericErrorController')

const server = express()

server.use('*', errorHandlers.invalidUrlHandler)
server.use(errorHandlers.globalErrorHandler)

server.listen(3000, () => {
    console.log('server is listening on port 3000')
})
