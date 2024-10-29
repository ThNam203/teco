const mongoose = require('mongoose')

const taskCommentSchema = new mongoose.Schema(
    {
        author: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        updateTaskId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'UpdateTask',
            required: true,
        },
        content: String,
        mediaFiles: [{ type: String }],
        likedUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    },
    {
        timestamps: true,
        toJSON: {
            virtuals: true,
        },
        toObject: {
            virtuals: true,
        },
    }
)

module.exports = mongoose.model('TaskComment', taskCommentSchema)
