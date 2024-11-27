const mongoose = require('mongoose')

const friendSchema = new mongoose.Schema(
    {
        firstId: {
            type: String,
            required: true,
            validate: {
                validator: mongoose.Types.ObjectId.isValid,
                message: `user's id is not valid`,
            },
        },
        secondId: {
            type: String,
            required: true,
            validate: {
                validator: mongoose.Types.ObjectId.isValid,
                message: `user's id is not valid`,
            },
        },
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('Friend', friendSchema)
