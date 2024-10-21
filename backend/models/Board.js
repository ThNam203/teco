const mongoose = require('mongoose')

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
    rowCells: [{ type: String }],
    columnCells: [columnCellModel],
    cells: [[{ type: mongoose.Schema.Types.ObjectId, ref: 'Cell' }]],
})

module.exports = mongoose.model('Board', boardSchema)
