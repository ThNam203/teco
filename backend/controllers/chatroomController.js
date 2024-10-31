const ChatRoom = require('../models/ChatRoom')
const User = require('../models/User')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

exports.createNewChatRoom = asyncCatch(async (req, res, next) => {
    const { memberIds } = req.body

    const members = await Promise.all(
        memberIds.map(async (memberId) => {
            const member = await User.findById(memberId)
            if (!member) throw new AppError('Unable to find users', 404)
        })
    )

    const newChatRoom = await ChatRoom.create({
        members: memberIds,
    })

    if (newChatRoom) {
        members.forEach((member) => member.chatRooms.push(newChatRoom._id))
    } else throw new AppError('Unable to create new chat room', 500)

    res.status(204).end()
})
