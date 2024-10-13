const mongoose = require('mongoose')

const notificationSchema = new mongoose.Schema(
    {
        senderId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        receiverId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        notificationType: {
            type: String,
            required: true,
            enum: ['FriendRequest', 'NewMessage', 'Comment', 'Like', 'Share'],
        },
        title: {
            type: String,
            required: true,
        },
        content: {
            type: String,
            required: true,
        },
        isRead: {
            type: Boolean,
            default: false,
        },
        link: {
            type: String,
        },
    },
    {
        timestamps: true,
    }
)

notificationSchema.methods.createNewNotification = (req) =>
    this.create({
        userId: req.body.userId,
        notificationType: req.body.notificationType,
        title: req.body.title,
        content: req.body.content,
        isRead: req.body.isRead,
        link: req.body.link,
        timestamps: req.body.timestamps,
    })

module.exports = mongoose.model('Notification', notificationSchema)
