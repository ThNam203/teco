const { v4 } = require('uuid')
const Message = require('../models/Message')
const socketIO = require('./socket')

const io = socketIO.getIO()

// const joinChatRooms = async (userId, socket) => {
//     const chatrooms = await ChatRoom.find({ members: { $in: [userId] } })
//     chatrooms.forEach((chatroom) => {
//         socket.join(chatroom._id.toString())
//     })
// }

io.on('connection', (socket) => {
    //joinChatRooms(socket.handshake.auth.userId, socket)
    socket.on('joinMessageRoom', (data) => {
        const chatRoomId = data
        if (!socket.rooms.has(chatRoomId)) {
            socket.join(chatRoomId)
        }
    })

    socket.on('leaveMessageRoom', (data) => {
        const { chatRoomId } = data
        if (socket.rooms.has(chatRoomId)) {
            socket.leave(chatRoomId)
        }
    })

    socket.on('offerVideoCall', (data) => {
        const { offerDescription, chatRoomId } = data
        socket.to(chatRoomId).emit('offerVideoCall', {
            offerDescription,
        })
    })

    socket.on('answerOfferVideoCall', (data) => {
        const { answerDescription, chatRoomId } = data
        socket.to(chatRoomId).emit('answerOfferVideoCall', answerDescription)
    })

    socket.on('iceCandidate', (data) => {
        const { iceCandidate, chatRoomId } = data
        socket.to(chatRoomId).emit('iceCandidate', iceCandidate)
    })

    socket.on('newMessage', async (data) => {
        const { chatRoomId, message, senderId } = JSON.parse(data)
        // const chatRoom = await ChatRoom.findById(chatRoomId)
        // if (!chatRoom)
        //     return socket
        //         .in(chatRoomId)
        //         .emit('roomNotFound', 'Unable to find the chat room')

        // const newChatMessage = await Message.create({
        //     chatRoomId,
        //     message,
        //     senderId,
        // })

        // if (!newChatMessage)
        //     return socket.emit('messageError', 'Unable to send new message')

        io.in(chatRoomId).emit(
            'newMessage',
            JSON.stringify({
                _id: v4(),
                chatRoomId,
                message,
                senderId,
                createdAt: Date.now(),
            })
        )
    })

    socket.on('disconnect', () => {
        console.log('co mot thang disconnect ne')
    })
})
