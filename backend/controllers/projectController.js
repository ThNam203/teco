/* eslint-disable node/no-unsupported-features/es-syntax */
const Board = require('../models/Board')
const Project = require('../models/Project')
const cellModels = require('../models/Cell')
const UpdateTask = require('../models/UpdateTask')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')
const s3Controller = require('./awsS3Controllers')

const removeUpdateTaskFunc = async (updateTaskId) => {
    const deletedUpdateTask = await UpdateTask.findByIdAndDelete(updateTaskId)
    if (deletedUpdateTask.files)
        deletedUpdateTask.files.forEach((file) =>
            s3Controller.deleteAnObject(file.location)
        )

    return deletedUpdateTask
}

const createACell = async (cell) => {
    let newCell
    const { cellType } = cell
    // must delete, if not we cant create
    // the reasone is the cellType overlap with the discriminator key
    delete cell.cellType
    if (cell._id) delete cell._id
    switch (cellType) {
        case 'CellStatus':
            newCell = await cellModels.CellStatus.create(cell)
            break
        case 'CellUpdate':
            newCell = await cellModels.CellUpdate.create(cell)
            break
        case 'CellText':
            newCell = await cellModels.CellText.create(cell)
            break
        case 'CellNumber':
            newCell = await cellModels.CellNumber.create(cell)
            break
        case 'CellTimeline':
            newCell = await cellModels.CellTimeline.create(cell)
            break
        case 'CellDate':
            newCell = await cellModels.CellDate.create(cell)
            break
        case 'CellUser':
            newCell = await cellModels.CellUser.create({
                ...cell,
                userId: cell.userId ? cell.userId : null,
            })
            break
        case 'CellCheckbox':
            newCell = await cellModels.CellCheckbox.create(cell)
            break
        default:
            throw new AppError('Unable to save a cell', 500)
    }
    return newCell
}

const updateACell = async (cell) => {
    let newCell
    switch (cell.cellType) {
        case 'CellStatus':
            newCell = await cellModels.CellStatus.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellUpdate':
            newCell = await cellModels.CellUpdate.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellText':
            newCell = await cellModels.CellText.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellNumber':
            newCell = await cellModels.CellNumber.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellTimeline':
            newCell = await cellModels.CellTimeline.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellDate':
            newCell = await cellModels.CellDate.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellUser':
            newCell = await cellModels.CellUser.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellCheckbox':
            newCell = await cellModels.CellCheckbox.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        default:
            throw new AppError('Unable to update the cell', 500)
    }
    return newCell
}

const removeACell = async (cell) => {
    if (cell.cellType === 'CellUpdate') {
        await removeUpdateTaskFunc(cell._id)
    } else {
        await cellModels.CellBase.findByIdAndDelete(cell._id)
    }
}

const saveABoard = async (board) => {
    const { cells } = board

    const boardCellIds = []
    await Promise.all(
        cells.map(async (cellsRow) => {
            const promises = cellsRow.map(
                async (cell) => await createACell(cell)
            )
            const createdCells = await Promise.all(promises)
            const cellIds = createdCells.map((createdCell) => createdCell.id)
            boardCellIds.push(cellIds)
        })
    )

    const createdBoard = await Board.create({
        boardTitle: board.boardTitle,
        rowCells: board.rowCells,
        columnCells: board.columnCells,
        cells: boardCellIds,
    })

    if (!createdBoard) throw new AppError('Unable to save board', 500)
    return createdBoard._id
}

const deleteABoard = async (board) => {
    await Promise.all(
        board.cells.map(
            async (cellRow) =>
                await Promise.all(
                    cellRow.map(
                        async (cell) =>
                            await cellModels.CellBase.findByIdAndDelete(
                                cell._id
                            )
                    )
                )
        )
    )
    await Board.findByIdAndDelete(board._id)
}

exports.updateACell = asyncCatch(async (req, res, next) => {
    const cell = req.body

    const updatedCell = await updateACell(cell)

    if (!updatedCell)
        return next(new AppError('Unable to update the cell', 500))

    res.status(204).end()
})

exports.createAndGetNewBoard = asyncCatch(async (req, res, next) => {
    const { projectId } = req.params
    const project = await Project.findById(projectId)
    const newBoard = await Board.create({
        boardTitle: 'New board',
        rowCells: [],
        columnCells: [],
        cells: [],
    })

    project.boards.push(newBoard._id)
    await project.save()

    res.status(200).json(newBoard)
})

exports.updateBoard = asyncCatch(async (req, res, next) => {
    const { boardId } = req.params
    const { boardTitle } = req.body.nameValuePairs

    await Board.findByIdAndUpdate(boardId, {
        boardTitle: boardTitle,
    })

    res.status(204).end()
})

exports.removeBoard = asyncCatch(async (req, res, next) => {
    const { projectId, boardId } = req.params

    const project = await Project.findById(projectId)
    const deletedBoard = await Board.findById(boardId)
    deleteABoard(deletedBoard)

    const idx = project.boards.indexOf(deletedBoard._id)
    project.boards.splice(idx, 1)
    project.markModified('boards')
    await project.save()

    res.status(204).end()
})

exports.deleteRow = asyncCatch(async (req, res, next) => {
    const { boardId, deletedRowPosition } = req.body
    const board = await Board.findById(boardId)
    board.rowCells.splice(deletedRowPosition, 1)
    const deletedCellIds = board.cells.splice(deletedRowPosition, 1)[0]
    await Promise.all(
        deletedCellIds.map(
            async (cellId) =>
                await cellModels.CellBase.findByIdAndDelete(cellId)
        )
    )
    await board.save()
    res.status(200).json(board)
})

exports.deleteColumn = asyncCatch(async (req, res, next) => {
    const { boardId, deletedColumnPosition } = req.body
    const board = await Board.findById(boardId)
    board.columnCells.splice(deletedColumnPosition, 1)

    await Promise.all(
        board.cells.map(async (cellRow) => {
            const cellId = cellRow.splice(deletedColumnPosition, 1)[0]
            await cellModels.CellBase.findByIdAndDelete(cellId)
        })
    )

    res.status(204).end()
})

exports.addNewUpdateTask = asyncCatch(async (req, res, next) => {
    const { taskContent } = req.body
    const { cellId } = req.params

    const fileLocations = []
    if (req.files) {
        req.files.forEach((file) => {
            let fileType
            if (!file.mimetype) fileType = 'Document'
            else if (file.mimetype.startsWith('image/')) fileType = 'Image'
            else if (file.mimetype.startsWith('video/')) fileType = 'Video'
            else fileType = 'Document'

            const newFile = {
                location: file.location,
                name: file.originalname,
                fileType: fileType,
            }

            fileLocations.push(newFile)
        })
    }

    const task = JSON.parse(taskContent)

    const newTask = await UpdateTask.create({
        author: task.author._id,
        cellId: cellId,
        content: task.content,
        files: fileLocations,
    })

    if (!newTask) return next(new AppError('Unable to send update', 500))

    res.status(204).end()
})

exports.toggleUpdateTaskLike = asyncCatch(async (req, res, next) => {
    const { userId, updateTaskId } = req.params
    const task = await UpdateTask.findById(updateTaskId)

    const indexOfTheLiked = task.likedUsers.indexOf(userId)
    if (indexOfTheLiked === -1) task.likedUsers.push(userId)
    else task.likedUsers.splice(indexOfTheLiked, 1)
    await task.save()
    res.status(204).end()
})

exports.getAllUpdateTasksOfACell = asyncCatch(async (req, res, next) => {
    const { userId, cellId } = req.params

    const updateTasks = await UpdateTask.find({ cellId: cellId })
        .populate('author', '_id name email imageProfilePath')
        .sort({
            createdAt: -1,
        })

    const objectTasks = updateTasks.map((task) => {
        const objectTask = task.toObject()
        if (task.likedUsers.includes(userId)) objectTask.isLiked = true
        else objectTask.isLiked = false
        delete task.likedUsers
        return objectTask
    })

    res.status(200).json(objectTasks)
})

exports.removeUpdateTask = asyncCatch(async (req, res, next) => {
    const { updateTaskId } = req.params
    removeUpdateTaskFunc(updateTaskId)
    res.status(204).end()
})

exports.addNewRow = asyncCatch(async (req, res, next) => {
    const { rowHeaderModel, cells } = req.body
    const { projectId, boardId } = req.params

    // update the updatedAt in project
    Project.findById(projectId).then((project) => {
        project.updatedAt = new Date().toISOString()
        project.save()
    })

    const board = await Board.findById(boardId)
    board.rowCells.push(rowHeaderModel.title)

    const newCells = await Promise.all(
        cells.map(async (cell) => await createACell(cell))
    )

    const newCellIds = newCells.map((cell) => cell._id)

    board.cells.push(newCellIds)
    board.markModified('cells')
    await board.save()
    res.status(200).json(newCellIds)
})

exports.addNewColumn = asyncCatch(async (req, res, next) => {
    const { columnHeaderModel, cells } = req.body
    const { projectId, boardId } = req.params

    // update the updatedAt in project
    Project.findById(projectId).then((project) => {
        project.updatedAt = new Date().toISOString()
        project.save()
    })

    const board = await Board.findById(boardId)
    board.columnCells.push(columnHeaderModel)

    const newCells = await Promise.all(
        cells.map(async (cell) => await createACell(cell))
    )

    const newCellIds = newCells.map((cell) => cell._id)

    for (let i = 0; i < board.cells.length; i += 1) {
        board.cells[i].push(newCellIds[i])
    }

    board.markModified('cells')
    await board.save()
    res.status(200).json(newCellIds)
})

exports.removeColumn = asyncCatch(async (req, res, next) => {
    const { boardId, columnPosition } = req.params
    const board = await Board.findById(boardId).populate('cells', 'Cell')
    if (!board) throw new AppError('Unable to find board', 404)

    board.columnCells.splice(columnPosition, 1)
    board.markModified('columnCells')

    await Promise.all(
        board.cells.map(async (row) => {
            await removeACell(row.splice(columnPosition, 1)[0])
        })
    )
    board.markModified('cells')
    await board.save()

    res.status(204).end()
})

exports.updateColumn = asyncCatch(async (req, res, next) => {
    const { boardId, columnPosition } = req.params
    const { description, title } = req.body.nameValuePairs

    const board = await Board.findById(boardId)
    if (!board) throw new AppError('Unable to find board', 404)

    if (description) board.columnCells[columnPosition].description = description
    if (title) board.columnCells[columnPosition].title = title
    board.markModified('columnCells')
    await board.save()
    res.status(204).end()
})

exports.updateRow = asyncCatch(async (req, res, next) => {
    const { boardId, rowPosition } = req.params
    const { newTitle } = req.body.nameValuePairs

    const board = await Board.findById(boardId)
    if (!board) throw new AppError('Unable to find board', 404)

    if (newTitle) board.rowCells[rowPosition] = newTitle
    board.markModified('rowCells')
    await board.save()
    res.status(204).end()
})

exports.removeRow = asyncCatch(async (req, res, next) => {
    const { rowPosition, boardId } = req.params
    const board = await Board.findById(boardId).populate('cells', 'Cell')
    if (!board) throw new AppError('Unable to find board', 404)

    board.rowCells.splice(rowPosition, 1)
    board.markModified('rowCells')

    const deletedCells = board.cells.splice(rowPosition, 1)[0]
    await Promise.all(deletedCells.map((cell) => removeACell(cell)))
    board.markModified('cells')
    await board.save()

    res.status(204).end()
})

exports.saveNewProject = asyncCatch(async (req, res, next) => {
    const { boards, chosenPosition, memberIds, creatorId, title } = req.body
    const promises = await Promise.all(
        boards.map(async (board) => await saveABoard(board))
    )

    const newProject = await Project.create({
        chosenPosition,
        boards: promises,
        memberIds,
        creatorId,
        title,
    })

    if (!newProject)
        return next(new AppError('Unable to save the project', 500))

    await newProject.populate({
        path: 'boards',
        model: 'Board',
        populate: {
            path: 'cells',
            model: 'Cell',
        },
    })

    res.status(200).json(newProject)
})

exports.deleteProjectById = asyncCatch(async (req, res, next) => {
    const { projectId } = req.body
    const project = await Project.findById(projectId)
    const populatedProject = await project.populate({
        path: 'boards',
    })

    await Promise.all(
        populatedProject.boards.map(async (board) => await deleteABoard(board))
    )

    await Project.findByIdAndDelete(projectId)
    res.status(204).end()
})

exports.getCellsInARow = asyncCatch(async (req, res, next) => {
    const { boardId, rowPosition } = req.params

    const board = await Board.findById(boardId).populate({
        path: 'cells',
        model: 'Cell',
    })

    const cellsInRow = []
    const columnTitles = []
    for (let i = 0; i < board.columnCells.length; i += 1) {
        cellsInRow.push(board.cells[rowPosition][i])
        columnTitles.push(board.columnCells[i].title)
    }

    res.status(200).json({
        columnTitles: columnTitles,
        cells: cellsInRow,
        rowTitle: board.rowCells[rowPosition],
    })
})

exports.getProjectById = asyncCatch(async (req, res, next) => {
    const { projectId } = req.params
    const project = await Project.findById(projectId)

    if (!project) return next(new AppError('Unable to find project', 404))

    const populatedProject = await project.populate({
        path: 'boards',
        model: 'Board',
        populate: {
            path: 'cells',
            model: 'Cell',
        },
    })

    if (!populatedProject)
        return next(new AppError('Unable to get the project', 500))
    res.status(200).json(populatedProject)
})

exports.getAllProjectOfUser = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const projects = await Project.find({
        memberIds: { $in: [userId] },
    }).select('_id title updatedAt')
    res.status(200).json(projects)
})
