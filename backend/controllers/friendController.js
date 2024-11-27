const FriendRequest = require('../models/FriendRequest')
const Friend = require('../models/Friend')
const Notification = require('../models/Notification')
const User = require('../models/User')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

const sendNotificationOnReply = async (sender, receiver, isAccept) => {
    let message
    if (isAccept) {
        message = `${sender.name} accepted your friend request`

        await Friend.create({ firstId: sender._id, secondId: receiver._id })
    } else message = `${sender.name} denied your friend request`

    await Notification.create({
        senderId: sender._id,
        receiverId: receiver._id,
        notificationType: 'NewMessage',
        title: sender.name,
        content: message,
    })

    await Notification.findOneAndDelete({
        senderId: receiver._id,
        receiverId: sender._id,
        notificationType: 'FriendRequest',
    })
}

const sendNotificationOnRequest = async (sender, receiver) => {
    const message = `${sender.name} has sent you a friend request`

    await Notification.create({
        senderId: sender._id,
        receiverId: receiver._id,
        notificationType: 'FriendRequest',
        title: sender.name,
        content: message,
    })
}

exports.createNewFriendRequest = asyncCatch(async (req, res, next) => {
    const { senderId, receiverId } = req.body

    if (senderId === receiverId)
        return next(new AppError('Unable to add friend to yourself', 400))

    const sender = await User.findOne({ _id: senderId })
    const receiver = await User.findOne({ _id: receiverId })
    if (!receiver || !sender) return next(new AppError(`User not found`, 400))

    // check if friend request is pending
    const isExisted = await FriendRequest.findOne({
        senderId: sender._id,
        receiverId: receiver._id,
    })

    if (isExisted)
        return next(new AppError('The request is already on pending', 400))

    // check if they are already friend
    const userIds = [sender._id, receiver._id]

    const isFriended = await Friend.findOne({
        $or: [{ firstId: { $in: userIds }, secondId: { $in: userIds } }],
    })
    if (isFriended) return next(new AppError('Already friend', 400))

    // create the request in db
    const newFriendRequest = await FriendRequest.create({
        senderId: sender._id,
        receiverId: receiver._id,
    })

    if (!newFriendRequest)
        return next(new AppError('Unable to create new friend request', 500))

    sendNotificationOnRequest(sender, receiver)

    res.status(200).json(newFriendRequest)
})

exports.replyFriendRequest = asyncCatch(async (req, res, next) => {
    const { senderId, receiverId } = req.body
    const { response } = req.params

    if (response !== 'Accept' && response !== 'Deny')
        return next(new AppError('False response format', 400))

    const replier = await User.findOne({ _id: senderId })
    const requestSender = await User.findOne({ _id: receiverId })
    if (!replier || !requestSender)
        return next(new AppError(`User not found`, 400))

    // check if friend request still existed or not
    const isExisted = await FriendRequest.findOne({
        senderId: requestSender._id,
        receiverId: replier._id,
    })
    if (isExisted) {
        sendNotificationOnReply(replier, requestSender, response === 'Accept')
        await FriendRequest.findOneAndDelete({
            senderId: requestSender._id,
            receiverId: replier._id,
        })
    } else return next(new AppError('The request is not existed', 400))

    res.status(204).end()
})

exports.getFriendById = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const listFriend = await Friend.find({
        $or: [{ firstId: userId }, { secondId: userId }],
    })
    if (!listFriend) return next(new AppError('No notification found!', 400))

    const otherIds = []

    listFriend.forEach((friend) => {
        if (friend.firstId !== userId) {
            otherIds.push(friend.firstId)
        } else {
            otherIds.push(friend.secondId)
        }
    })

    const listUser = await User.find({ _id: { $in: otherIds } })
    res.status(200).json(listUser)
})
