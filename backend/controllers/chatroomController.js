const ChatRoom = require('../models/ChatRoom')
const Message = require('../models/Message')
const User = require('../models/User')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

exports.createNewChatRoom = asyncCatch(async (req, res, next) => {
    const { memberIds, isGroup = false, title } = req.body.nameValuePairs

    const members = await Promise.all(
        memberIds.map(async (memberId) => {
            const member = await User.findById(memberId)
            if (!member) throw new AppError('Unable to find users', 404)
            return member
        })
    )

    const newChatRoom = await ChatRoom.create({
        title: title,
        members: memberIds,
        isGroup: isGroup,
    })

    if (newChatRoom) {
        await Promise.all(
            members.map(async (member) => {
                member.chatRooms.push(newChatRoom._id)
                return member.save()
            })
        )
    } else throw new AppError('Unable to create new chat room', 500)

    await newChatRoom.populate('members', '_id name profileImagePath')
    res.status(200).json(newChatRoom)
})

exports.getAllChatroomsOfUser = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    // const chatrooms = await ChatRoom.find({ members: { $in: [userId] } })
    const chatRooms = await ChatRoom.find({
        members: { $in: [userId] },
    }).populate('members', '_id name profileImagePath')

    res.status(200).json(chatRooms)
})

exports.getAChatRoom = asyncCatch(async (req, res, next) => {
    const { userId, chatRoomId } = req.params
    const chatRoom = await ChatRoom.findById(chatRoomId).populate(
        'members',
        '_id name profileImagePath'
    )

    res.status(200).json(chatRoom)
})

exports.addMemberToGroupChat = asyncCatch(async (req, res, next) => {
    const { chatRoomId } = req.params
    const { newMemberId } = req.body.nameValuePairs

    const chatRoom = await ChatRoom.findById(chatRoomId)
    chatRoom.members.push(newMemberId)
    chatRoom.markModified('members')
    await chatRoom.save()

    res.status(204).end()
})

exports.deleteMemberFromGroupChat = asyncCatch(async (req, res, next) => {
    const { chatRoomId, memberId } = req.params

    const chatRoom = await ChatRoom.findById(chatRoomId)
    const idx = chatRoom.members.findIndex((x) => x.toString() === memberId)
    if (idx === -1) return next(new AppError('Member not found', 404))
    chatRoom.members.splice(idx, 1)
    chatRoom.markModified('members')
    await chatRoom.save()
    res.status(204).end()
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
