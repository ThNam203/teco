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
            enum: [
                'FriendRequest',
                'NewMessage',
                'Comment',
                'Like',
                'Share',
                'MemberRequest',
                'AdminRequest',
                'TaskAppointed',
            ],
        },
        title: {
            type: String,
            required: true,
        },
        content: {
            type: String,
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

module.exports = mongoose.model('Notification', notificationSchema)
