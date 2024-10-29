const mongoose = require('mongoose')

const projectSchema = new mongoose.Schema(
    {
        creatorId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        memberIds: [
            {
                type: mongoose.Schema.Types.ObjectId,
                ref: 'User',
                required: true,
            },
        ],
        title: {
            type: String,
            required: true,
        },
        boards: {
            type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Board' }],
        },
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('Project', projectSchema)
