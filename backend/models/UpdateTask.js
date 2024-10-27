const mongoose = require('mongoose')

const updateTaskSchema = new mongoose.Schema(
    {
        authorId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        cellId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'CellUpdate',
            required: true,
        },
        content: {
            type: String,
        },
        files: [
            {
                type: String,
            },
        ],
    },
    {
        timestamps: true,
    }
)

module.exports = mongoose.model('UpdateTask', updateTaskSchema)
