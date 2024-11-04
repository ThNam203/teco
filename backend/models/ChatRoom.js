const mongoose = require('mongoose')

const chatRoomSchema = new mongoose.Schema(
    {
        title: {
            type: String,
            trim: true,
        },
        members: {
            type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
            required: true,
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
