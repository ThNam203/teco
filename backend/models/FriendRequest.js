const mongoose = require('mongoose')

const friendRequestSchema = new mongoose.Schema(
    {
        senderId: {
            type: String,
            required: true,
            validate: {
                validator: mongoose.Types.ObjectId.isValid,
                message: `Sender's id is not valid`,
            },
        },
        receiverId: {
            type: String,
            required: true,
            validate: {
                validator: mongoose.Types.ObjectId.isValid,
                message: `Receiver's id is not valid`,
            },
        },
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('FriendRequest', friendRequestSchema)
