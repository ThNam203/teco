require('./utils/setUpServer')
const express = require('express')
const http = require('http')

const io = require('./socket/socket')
const errorHandlers = require('./controllers/errorControllers/genericErrorController')
const authRouter = require('./routers/authRouter')
const userRouter = require('./routers/userRouter')
const friendRouter = require('./routers/friendRouter')
const notificationRouter = require('./routers/notificationRouter')
const projectRouter = require('./routers/projectRouter')
const chatRoomRouter = require('./routers/chatRoomRouter')
const s3Controller = require('./controllers/awsS3Controllers')

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

app.post('/upload-files', s3Controller.s3Upload.array('files'), (req, res) => {
    if (req.files) {
        const fileDescriptions = []
        req.files.forEach((file) => {
            let fileType
            if (!file.mimetype) fileType = 'Document'
            else if (file.mimetype.startsWith('image/')) fileType = 'Image'
            else if (file.mimetype.startsWith('video/')) fileType = 'Video'
            else fileType = 'Document'

            const newFile = {
                location: file.location,
                name: file.originalname,
                fileType: fileType,
            }

            fileDescriptions.push(newFile)
        })
        res.status(200).json(fileDescriptions)
    } else res.status(500).json('Unable to upload image')
})
app.use('/', authRouter)
app.use('/', userRouter)
app.use('/', friendRouter)
app.use('/', notificationRouter)
app.use('/:userId/chatroom', chatRoomRouter)
app.use('/:userId/project', projectRouter)
app.use('*', errorHandlers.invalidUrlHandler)
app.use(errorHandlers.globalErrorHandler)

server.listen(process.env.PORT || 3000, () => {
    console.log(`server is listening on port ${process.env.PORT || 3000}`)
})
