const Message = require('../models/Message')
const socketIO = require('./socket')
const ChatRoom = require('../models/ChatRoom')

const io = socketIO.getIO()

io.on('connection', (socket) => {
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

        // newChatMessage is for 2 users chatting to each other
        io.in(chatRoomId).emit('newMessage', newChatMessage)
        // newMessage is used for updating the chat room list if other user is
        // not in the chat room but in an activity that show list of chat rooms
        // (and then update the order of chatroom base on date)
        chatRoom.members.forEach((memberId) =>
            io.in(memberId).emit('newMessageNotify', newChatMessage)
        )
    })

    socket.on('offerVideoCall', (data) => {
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

    socket.on('disconnect', () => {})
})
