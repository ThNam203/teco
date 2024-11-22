/* eslint-disable prettier/prettier */
const mongoose = require('mongoose')

const cellBaseSchema = new mongoose.Schema(
    {
        content: {
            type: String,
        },
    },
    {
        discriminatorKey: 'cellType',
        timestamps: true,
    }
)

const cellStatusSchema = new mongoose.Schema({
    contents: {
        type: [{ type: String }],
    },
    colors: {
        type: [{ type: String }],
    },
})

const cellUpdateSchema = new mongoose.Schema()

const cellTextSchema = new mongoose.Schema()

const cellNumberSchema = new mongoose.Schema()

const cellTimelineSchema = new mongoose.Schema({
    startYear: Number,
    startMonth: Number,
    startDay: Number,
    endYear: Number,
    endMonth: Number,
    endDay: Number,
})

const cellDateSchema = new mongoose.Schema({
    year: Number,
    month: Number,
    day: Number,
    hour: Number,
    minute: Number,
})

const addressSchema = new mongoose.Schema({
    title: {
      type: String,
      required: true
    },
    description: String,
    latitude: Number,
    longitude: Number,
}, {_id: false});

const cellMapSchema = new mongoose.Schema({
    addresses: [addressSchema]
})

const cellUserSchema = new mongoose.Schema({
    users: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        default: [],
    }],
})

const cellCheckboxSchema = new mongoose.Schema({
    isChecked: Boolean,
})

const CellBase = mongoose.model('Cell', cellBaseSchema)

// Create sub-schemas for each cell type
const CellStatus = CellBase.discriminator('CellStatus', cellStatusSchema)
const CellUpdate = CellBase.discriminator('CellUpdate', cellUpdateSchema)
const CellText = CellBase.discriminator('CellText', cellTextSchema)
const CellNumber = CellBase.discriminator('CellNumber', cellNumberSchema)
const CellTimeline = CellBase.discriminator('CellTimeline', cellTimelineSchema)
const CellDate = CellBase.discriminator('CellDate', cellDateSchema)
const CellUser = CellBase.discriminator('CellUser', cellUserSchema)
const CellCheckbox = CellBase.discriminator('CellCheckbox', cellCheckboxSchema)
const CellMap = CellBase.discriminator('CellMap', cellMapSchema)

module.exports = {
    CellBase,
    CellText,
    CellStatus,
    CellNumber,
    CellDate,
    CellTimeline,
    CellUpdate,
    CellUser,
    CellCheckbox,
    CellMap
}
