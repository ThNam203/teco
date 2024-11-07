const mongoose = require('mongoose')

const recentAccessSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
    },
    recentProjectIds: [
        {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Project',
            required: true,
        },
    ],
    timeAccessed: [
        {
            type: Date,
            required: true,
        },
    ],
})

recentAccessSchema.methods.create = (req) =>
    this.create({
        userId: req.body.userId,
        recentProjectIds: req.body.recentProjectIds,
        timeAccessed: req.body.timeAccessed,
    })

module.exports = mongoose.model('recentAccess', recentAccessSchema)
