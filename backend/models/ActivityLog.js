const mongoose = require('mongoose')

const activityLogModel = new mongoose.Schema(
    {
        creator: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        project: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Project',
        },
        board: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Board',
        },
        cell: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Cell',
        },
        description: {
            type: String,
            required: true,
        },
        type: {
            type: String,
            required: true,
            enum: ['Update', 'Change', 'Remove', 'New'],
        },
    },
    { timestamps: true }
)

module.exports = mongoose.model('ActivityLog', activityLogModel)
