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

friendSchema.methods.createNewNotification = (req) =>
    this.create({
        firstId: req.body.firstId,
        secondId: req.body.secondId,
    })

module.exports = mongoose.model('Friend', friendSchema)
