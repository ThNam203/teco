const mongoose = require('mongoose')

const activityLogModel = new mongoose.Schema({
    creator: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
    },
    projectId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Project',
    },
    boardId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Board',
    },
    cellId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Cell',
    },
    description: {
        type: String,
        required: true,
    },
})

module.exports = mongoose.model('ActivityLog', activityLogModel)
