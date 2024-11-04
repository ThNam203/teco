const { v4 } = require('uuid')
const Message = require('../models/Message')
const socketIO = require('./socket')
const ChatRoom = require('../models/ChatRoom')

const io = socketIO.getIO()

const joinChatRooms = async (userId, socket) => {
    const chatrooms = await ChatRoom.find({ members: { $in: [userId] } })
    chatrooms.forEach((chatroom) => {
        socket.join(chatroom._id.toString())
    })
}

io.on('connection', (socket) => {
    joinChatRooms(socket.handshake.query.userId, socket)

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
        console.log('onOfferVideoCall')
        const { sdp, chatRoomId } = data
        const offer = JSON.stringify({
            chatRoomId: chatRoomId,
            sdp: sdp,
        })

        socket.to(chatRoomId).emit('offerVideoCall', offer)
    })

    socket.on('answerOfferVideoCall', (data) => {
        const { sdp, chatRoomId } = data
        socket.to(chatRoomId).emit('answerOfferVideoCall', sdp)
    })

    socket.on('iceCandidate', (data) => {
        socket.to(data.chatRoomId).emit('iceCandidate', data)
    })

    socket.on('newMessage', async (data) => {
        const { chatRoomId, message, senderId } = JSON.parse(data)
        const chatRoom = await ChatRoom.findById(chatRoomId)
        if (!chatRoom)
            return socket
                .in(chatRoomId)
                .emit('roomNotFound', 'Unable to find the chat room')

        let newChatMessage = await Message.create({
            chatRoomId,
            message,
            sender: senderId,
        })

        if (!newChatMessage)
            return socket.emit('messageError', 'Unable to send new message')

        newChatMessage = await newChatMessage.populate(
            'sender',
            '_id name profileImagePath'
        )

        io.in(chatRoomId).emit('newMessage', newChatMessage)
    })

    socket.on('disconnect', () => {})
})
