const mongoose = require('mongoose')

const updateTaskCommentSchema = new mongoose.Schema(
    {
        author: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        projectId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Project',
            required: 'true',
        },
        boardId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Board',
            required: 'true',
        },
        cellId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'CellUpdate',
            required: true,
        },
        updateTaskId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'UpdateTask',
            required: true,
        },
        content: String,
        files: [
            {
                location: String,
                name: String,
                fileType: String,
            },
        ],
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

updateTaskCommentSchema.virtual('likeCount').get(function () {
    return this.likedUsers ? this.likedUsers.length : 0
})

module.exports = mongoose.model('TaskComment', updateTaskCommentSchema)
