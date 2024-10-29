const mongoose = require('mongoose')

const updateTaskSchema = new mongoose.Schema(
    {
        author: {
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
                location: String,
                name: String,
                fileType: String,
            },
        ],
        likedUsers: [
            {
                type: mongoose.Schema.Types.ObjectId,
                ref: 'User',
                required: true,
                default: [],
            },
        ],
    },
    {
        timestamps: true,
        toObject: {
            virtuals: true,
        },
        toJSON: {
            virtuals: true,
        },
    }
)

updateTaskSchema.virtual('likeCount').get(function () {
    return this.likedUsers ? this.likedUsers.length : 0
})

module.exports = mongoose.model('UpdateTask', updateTaskSchema)
