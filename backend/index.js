require('./utils/setUpServer')
const express = require('express')
const http = require('http')

const io = require('./socket/socket')
const errorHandlers = require('./controllers/errorControllers/genericErrorController')
const authRouter = require('./routers/authRouter')
const userRouter = require('./routers/userRouter')
const friendRouter = require('./routers/friendRouter')
const notificationRouter = require('./routers/notificationRouter')

const app = express()
const server = http.createServer(app)

io.initialize(server)
require('./socket/chat')

app.use(express.json())
app.use(
    express.urlencoded({
        extended: true,
    })
)

app.use('/', authRouter)
app.use('/', userRouter)
app.use('/', friendRouter)
app.use('/', notificationRouter)
app.use('*', errorHandlers.invalidUrlHandler)
app.use(errorHandlers.globalErrorHandler)

server.listen(process.env.PORT || 3000, () => {
    console.log(`server is listening on port ${process.env.PORT || 3000}`)
})
