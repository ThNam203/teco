const mongoose = require('mongoose')

const messageSchema = new mongoose.Schema(
    {
        chatRoomId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'ChatRoom',
            required: true,
        },
        message: {
            type: String,
        },
        sender: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        files: [
            {
                location: String,
                name: String,
                fileType: String,
            },
        ],
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('Message', messageSchema)
