const mongoose = require('mongoose')

const chatRoomSchema = new mongoose.Schema(
    {
        title: {
            type: String,
            trim: true,
        },
        admins: [
            {
                type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
                default: [],
            },
        ],
        members: {
            type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
            required: true,
        },
        isGroup: {
            type: Boolean,
            default: false,
        },
        logoPath: {
            type: String,
        },
        lastMessage: {
            type: String,
        },
        lastMessageTime: {
            type: String,
        },
    },
    { timestamps: true }
)

module.exports = mongoose.model('ChatRoom', chatRoomSchema)
