const ChatRoom = require('../models/ChatRoom')
const Message = require('../models/Message')
const User = require('../models/User')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

exports.createNewChatRoom = asyncCatch(async (req, res, next) => {
    const { memberIds } = req.body.nameValuePairs

    const members = await Promise.all(
        memberIds.map(async (memberId) => {
            const member = await User.findById(memberId)
            if (!member) throw new AppError('Unable to find users', 404)
            return member
        })
    )

    const newChatRoom = await ChatRoom.create({
        members: memberIds,
    })

    if (newChatRoom) {
        await Promise.all(
            members.map(async (member) => {
                member.chatRooms.push(newChatRoom._id)
                return member.save()
            })
        )
    } else throw new AppError('Unable to create new chat room', 500)

    res.status(204).end()
})

exports.getAllChatroomsOfUser = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    // const chatrooms = await ChatRoom.find({ members: { $in: [userId] } })
    const user = await User.findById(userId).populate({
        path: 'chatRooms',
        populate: {
            path: 'members',
            select: '_id name profileImagePath',
        },
    })

    res.status(200).json(user.chatRooms)
})

//TODO: check(verify) if user is in the chat room by userId from params
exports.getAllMessage = asyncCatch(async (req, res, next) => {
    const { chatRoomId } = req.params
    const messages = await Message.find({ chatRoomId: chatRoomId })
        .populate('sender', '_id name profileImagePath')
        .sort({
            createdAt: -1,
        })

    const reverseMessages = messages.reverse()

    res.status(200).json(reverseMessages)
})
