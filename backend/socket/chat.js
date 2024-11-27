const Message = require('../models/Message')
const socketIO = require('./socket')
const ChatRoom = require('../models/ChatRoom')
const User = require('../models/User')

const io = socketIO.getIO()

io.on('connection', (socket) => {
    socket.join(socket.handshake.query.userId)

    socket.on('joinChatRoom', (data) => {
        const chatRoomId = data
        socket.join(chatRoomId)
    })

    socket.on('leaveChatRoom', (data) => {
        const chatRoomId = data
        socket.leave(chatRoomId)
    })

    //
    socket.on('newMessage', async (data) => {
        const { chatRoomId, message, senderId, files } = JSON.parse(data)
        const chatRoom = await ChatRoom.findById(chatRoomId)
        if (!chatRoom)
            return socket
                .in(chatRoomId)
                .emit('roomNotFound', 'Unable to find the chat room')

        let newChatMessage = await Message.create({
            chatRoomId,
            message,
            sender: senderId,
            files: files,
        })

        if (!newChatMessage)
            return socket.emit('messageError', 'Unable to send new message')

        newChatMessage = await newChatMessage.populate(
            'sender',
            '_id name profileImagePath'
        )

        // newChatMessage is for 2 users chatting to each other
        io.in(chatRoomId).emit('newMessage', newChatMessage)
        // newMessage is used for updating the chat room list if other user is
        // not in the chat room but in an activity that show list of chat rooms
        // (and then update the order of chatroom base on date)
        chatRoom.members.forEach((memberId) => {
            io.in(memberId.toString()).emit('newMessageNotify', newChatMessage)
        })
    })

    socket.on('offerVideoCall', async (data) => {
        const { sdp, chatRoomId, callerId, isVideoCall } = data
        const user = await User.findById(callerId)
        const offer = JSON.stringify({
            chatRoomId: chatRoomId,
            sdp: sdp,
            callerName: user.name,
            callerImagePath: user.profileImagePath,
            callerId: callerId,
            isVideoCall: isVideoCall,
        })

        const chatRoom = await ChatRoom.findById(chatRoomId)
        chatRoom.members.forEach((memberId) => {
            if (memberId.toString() !== callerId)
                io.in(memberId.toString()).emit('offerVideoCall', offer)
        })

        // socket.to(chatRoomId).emit('offerVideoCall', offer)
    })

    socket.on('answerOfferVideoCall', async (data) => {
        const { sdp, chatRoomId } = data
        const chatRoom = await ChatRoom.findById(chatRoomId)
        chatRoom.members.forEach((memberId) => {
            io.in(memberId.toString()).emit('answerOfferVideoCall', sdp)
        })
        // socket.to(chatRoomId).emit('answerOfferVideoCall', sdp)
    })

    socket.on('iceCandidate', async (data) => {
        const chatRoom = await ChatRoom.findById(data.chatRoomId)
        chatRoom.members.forEach((memberId) => {
            io.in(memberId.toString()).emit('iceCandidate', data)
        })
        // socket.to(data.chatRoomId).emit('iceCandidate', data)
    })

    socket.on('callDeny', async (data) => {
        // data is userId, which is the user who is denied :D
        io.in(data).emit('callDeny')
    })

    socket.on('disconnect', () => {})
})
