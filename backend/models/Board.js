const mongoose = require('mongoose')

const BOARD_COLUMN_TYPES = {
    TEXT: 'Text',
    DATE: 'Date',
    CHECKBOX: 'Checkbox',
    USER: 'User',
    UPDATE: 'Update',
    TIMELINE: 'Timeline',
    NUMBER: 'Number',
    STATUS: 'Status',
    MAP: 'Map',
}

const columnCellModel = new mongoose.Schema(
    {
        columnType: {
            type: String,
            required: true,
        },
        title: {
            type: String,
            required: true,
        },
        description: {
            type: String,
            default: '',
        },
    },
    {
        _id: false,
    }
)

const rowCellModel = new mongoose.Schema(
    {
        title: {
            type: String,
            required: true,
        },
        isDone: {
            type: Boolean,
            default: false,
        },
    },
    {
        _id: false,
    }
)

// const cellRowSchema = new mongoose.Schema(
//     {
//         type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Cell' }],
//     },
//     { _id: false }
// )

const boardSchema = new mongoose.Schema({
    boardTitle: {
        type: String,
        required: true,
    },
    deadlineColumnIndex: {
        type: Number,
        default: -1,
    },
    rowCells: [rowCellModel],
    columnCells: [columnCellModel],
    cells: [[{ type: mongoose.Schema.Types.ObjectId, ref: 'Cell' }]],
})

module.exports = {
    Board: mongoose.model('Board', boardSchema),
    BOARD_COLUMN_TYPES,
}
