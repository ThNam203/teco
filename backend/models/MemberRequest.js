const mongoose = require('mongoose')

const memberRequestSchema = new mongoose.Schema(
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
        projectId: {
            type: String,
            required: true,
            validate: {
                validator: mongoose.Types.ObjectId.isValid,
                message: `Project's id is not valid`,
            },
        },
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('MemberRequest', memberRequestSchema)
