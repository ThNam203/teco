const Notification = require('../models/Notification')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

exports.getNotificationByReceiverId = asyncCatch(async (req, res, next) => {
    const { receiverId } = req.params
    const listNotifications = await Notification.find({
        receiverId: receiverId,
    })
    if (!listNotifications)
        return next(new AppError('No notification found!', 400))

    res.status(200).json(listNotifications)
})

exports.deleteNotificationById = asyncCatch(async (req, res, next) => {
    const { notificationId } = req.params
    const toDelete = await Notification.find({
        _id: notificationId,
    })
    if (!toDelete) return next(new AppError('No notification found!', 400))

    await Notification.findByIdAndDelete({
        _id: notificationId,
    })

    res.status(200).end()
})

exports.updateReadNotification = asyncCatch(async (req, res, next) => {
    const { notificationId } = req.params
    const notification = await Notification.findById(notificationId)
    if (!notification)
        return next(new AppError('Unable to find the notification'))
    notification.isRead = req.body.isRead
    res.status(204).json(notification)
})
