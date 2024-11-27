/* eslint-disable node/no-unsupported-features/es-syntax */
const Board = require('../models/Board')
const Project = require('../models/Project')
const User = require('../models/User')
const Notification = require('../models/Notification')
const cellModels = require('../models/Cell')
const UpdateTask = require('../models/UpdateTask')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')
const s3Controller = require('./awsS3Controllers')
const MemberRequest = require('../models/MemberRequest')
const ActivityLog = require('../models/ActivityLog')
const UpdateTaskComment = require('../models/UpdateTaskComment')

const removeUpdateTaskFunc = async (updateTaskId) => {
    const deletedUpdateTask = await UpdateTask.findByIdAndDelete(updateTaskId)
    if (!deletedUpdateTask) return
    if (deletedUpdateTask.files)
        deletedUpdateTask.files.forEach((file) =>
            s3Controller.deleteAnObject(file.location)
        )

    UpdateTaskComment.find({
        updateTaskId: deletedUpdateTask._id,
    }).then((documents) => {
        documents.forEach((document) => {
            if (document && document.files) {
                document.files.forEach((file) =>
                    s3Controller.deleteAnObject(file.location)
                )
            }
        })
    })

    return deletedUpdateTask
}

const removeUpdateTaskCommentFunc = async (commendId) => {
    const deletedComment = await UpdateTaskComment.findByIdAndDelete(commendId)
    if (deletedComment && deletedComment.files) {
        deletedComment.files.forEach((file) =>
            s3Controller.deleteAnObject(file.location)
        )
    }
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
            newCell = await cellModels.CellUser.create(cell)
            break
        case 'CellCheckbox':
            newCell = await cellModels.CellCheckbox.create(cell)
            break
        case 'CellMap':
            newCell = await cellModels.CellMap.create(cell)
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
            ).populate('users', '_id name profileImagePath')
            break
        case 'CellCheckbox':
            newCell = await cellModels.CellCheckbox.findByIdAndUpdate(
                cell._id,
                cell,
                { new: true }
            )
            break
        case 'CellMap':
            newCell = await cellModels.CellMap.findByIdAndUpdate(
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

const ACTIVITY_LOG_TYPES = {
    UPDATE: 'Update',
    CHANGE: 'Change',
    REMOVE: 'Remove',
    NEW: 'New',
}
const createActivityLog = async (
    userId,
    projectId,
    boardId,
    cellId,
    description,
    type
) => {
    ActivityLog.create({
        creator: userId,
        description: description,
        project: projectId,
        board: boardId,
        cell: cellId,
        type: type,
    })
}

exports.updateACell = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId, cellId } = req.params
    const user = await User.findById(userId)
    const cell = req.body

    const updatedCell = await updateACell(cell)

    // create notificaton for user who is appointed to the tasks (whose id does not equal to userId)
    if (cell.cellType === 'CellUser') {
        // users who were appointed to the task before update
        const oldUserIds = cell.users.map((u) => u._id)
        const newUserIds = updatedCell.users.map((u) => u._id)
        newUserIds.forEach((newUserId) => {
            // it means a new user has been appointed the task
            // TODO: add projectid, boardid, cellId for navigation
            if (
                newUserId.toString() !== userId &&
                !oldUserIds.contains(newUserId)
            ) {
                Notification.create({
                    senderId: userId,
                    receiverId: newUserId,
                    notificationType: 'TaskAppointed',
                    title: 'New task',
                    content: `${user.name} has appointed you to a new task`,
                })
            }
        })
    }

    if (!updatedCell)
        return next(new AppError('Unable to update the cell', 500))

    // create activity log
    Board.findById(boardId).then((board) => {
        board.cells.forEach((cellsRow, rowIdx) => {
            cellsRow.forEach((aCell, columnIdx) => {
                if (aCell._id.toString() === cellId) {
                    createActivityLog(
                        user._id,
                        projectId,
                        boardId,
                        cellId,
                        `${user.name} has updated a cell in column ${columnIdx}, row ${rowIdx}`,
                        ACTIVITY_LOG_TYPES.UPDATE
                    )
                }
            })
        })
    })

    res.status(204).end()
})

exports.createAndGetNewBoard = asyncCatch(async (req, res, next) => {
    const { userId, projectId } = req.params
    const project = await Project.findById(projectId)
    const newBoard = await Board.create({
        boardTitle: 'New board',
        rowCells: [],
        columnCells: [],
        cells: [],
    })

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            null,
            null,
            `${user.name} created new board`,
            ACTIVITY_LOG_TYPES.NEW
        )
    })

    project.boards.push(newBoard._id)
    await project.save()

    res.status(200).json(newBoard)
})

exports.updateBoard = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId } = req.params
    const { boardTitle } = req.body.nameValuePairs

    const board = await Board.findByIdAndUpdate(boardId, {
        boardTitle: boardTitle,
    })

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has renamed "${board.boardTitle}" board to "${boardTitle}"`,
            ACTIVITY_LOG_TYPES.CHANGE
        )
    })

    res.status(204).end()
})

exports.removeBoard = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId } = req.params

    const project = await Project.findById(projectId)
    const deletedBoard = await Board.findById(boardId)
    deleteABoard(deletedBoard)

    const idx = project.boards.indexOf(deletedBoard._id)
    project.boards.splice(idx, 1)
    project.markModified('boards')
    await project.save()

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has removed "${deletedBoard.boardTitle}" board`,
            ACTIVITY_LOG_TYPES.REMOVE
        )
    })

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
    const { userId, projectId, boardId, cellId } = req.params

    const fileDescriptions = []
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

            fileDescriptions.push(newFile)
        })
    }

    const task = JSON.parse(taskContent)

    const newTask = await UpdateTask.create({
        author: task.author._id,
        projectId: projectId,
        boardId: boardId,
        cellId: cellId,
        content: task.content,
        files: fileDescriptions,
    })

    if (!newTask) return next(new AppError('Unable to send update', 500))

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            // todo:get the row's title and add it to the description below
            `${user.name} has added a new update task`,
            ACTIVITY_LOG_TYPES.NEW
        )
    })

    res.status(204).end()
})

exports.addNewCommentToUpdateTask = asyncCatch(async (req, res, next) => {
    const { commentContent } = req.body
    const { userId, projectId, boardId, cellId, updateTaskId } = req.params

    const user = await User.findById(userId)

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

    const comment = JSON.parse(commentContent)

    const newComment = await UpdateTaskComment.create({
        author: userId,
        projectId: projectId,
        boardId: boardId,
        cellId: cellId,
        updateTaskId: updateTaskId,
        content: comment.content,
        files: fileLocations,
    })

    // notify the user who owns the update task
    UpdateTask.findById(updateTaskId).then((updateTask) => {
        if (updateTask.author.toString() !== userId) {
            Notification.create({
                senderId: userId,
                receiverId: updateTask.author,
                notificationType: 'Comment',
                title: `${user.name} has commented in your update task`,
            })
        }
    })

    if (!newComment) return next(new AppError('Unable to send comment', 500))

    await newComment.populate('author', '_id name email profileImagePath')

    res.status(200).json(newComment)
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

exports.toggleUpdateTaskCommentLike = asyncCatch(async (req, res, next) => {
    const { userId, commentId } = req.params
    const comment = await UpdateTaskComment.findById(commentId)

    const indexOfTheLiked = comment.likedUsers.indexOf(userId)
    if (indexOfTheLiked === -1) comment.likedUsers.push(userId)
    else comment.likedUsers.splice(indexOfTheLiked, 1)
    await comment.save()
    res.status(204).end()
})

exports.deleteComment = asyncCatch(async (req, res, next) => {
    const { userId, commentId } = req.params
    removeUpdateTaskCommentFunc(commentId)
    // todo: send notification for user who follows the update task

    res.status(204).end()
})

exports.getUpdateTaskAndComment = asyncCatch(async (req, res, next) => {
    const { userId, updateTaskId } = req.params

    const updateTask = await UpdateTask.findById(updateTaskId).populate(
        'author',
        '_id name email imageProfilePath'
    )

    const objectTask = updateTask.toObject()
    if (updateTask.likedUsers.includes(userId)) objectTask.isLiked = true
    else objectTask.isLiked = false
    delete updateTask.likedUsers

    const commentsRaw = await UpdateTaskComment.find({
        updateTaskId: updateTaskId,
    })
        .sort({ createdAt: -1 })
        .populate('author', '_id name email imageProfilePath')
    commentsRaw.reverse()

    const comments = commentsRaw.map((comment) => {
        const commentObj = comment.toObject()
        if (
            commentObj.likedUsers.findIndex((likedUserId) =>
                likedUserId.equals(userId)
            ) !== -1
        ) {
            commentObj.isLiked = true
        } else {
            commentObj.isLiked = false
        }
        delete commentObj.likedUsers
        return commentObj
    })

    res.status(200).json({ updateTask: objectTask, comments: comments })
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
    const { userId, projectId, boardId, cellId, updateTaskId } = req.params
    removeUpdateTaskFunc(updateTaskId)

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            cellId,
            `${user.name} has removed an update task`,
            ACTIVITY_LOG_TYPES.REMOVE
        )
    })

    res.status(204).end()
})

exports.addNewRow = asyncCatch(async (req, res, next) => {
    const { rowHeaderModel, cells } = req.body
    const { userId, projectId, boardId } = req.params

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

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has added "${rowHeaderModel.title}" row`,
            ACTIVITY_LOG_TYPES.NEW
        )
    })

    res.status(200).json(newCellIds)
})

exports.addNewColumn = asyncCatch(async (req, res, next) => {
    const { columnHeaderModel, cells } = req.body
    const { userId, projectId, boardId } = req.params

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

    await User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has added a new column "${columnHeaderModel.title}"`,
            ACTIVITY_LOG_TYPES.NEW
        )
    })
    res.status(200).json(newCellIds)
})

exports.removeColumn = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId, columnPosition } = req.params
    const board = await Board.findById(boardId).populate('cells', 'Cell')
    if (!board) throw new AppError('Unable to find board', 404)

    const deletedColumn = board.columnCells.splice(columnPosition, 1)[0]
    board.markModified('columnCells')

    await Promise.all(
        board.cells.map(async (row) => {
            await removeACell(row.splice(columnPosition, 1)[0])
        })
    )
    board.markModified('cells')
    await board.save()

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has removed "${deletedColumn.title}" column`,
            ACTIVITY_LOG_TYPES.REMOVE
        )
    })

    res.status(204).end()
})

exports.updateColumn = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId, columnPosition } = req.params
    const { description, title } = req.body.nameValuePairs

    const board = await Board.findById(boardId)
    if (!board) throw new AppError('Unable to find board', 404)

    if (description) {
        board.columnCells[columnPosition].description = description
        // create activity log
        User.findById(userId).then((user) => {
            createActivityLog(
                user._id,
                projectId,
                boardId,
                null,
                `${user.name} has changed column "${board.columnCells[columnPosition].title}" description`,
                ACTIVITY_LOG_TYPES.CHANGE
            )
        })
    }
    if (title) {
        const oldTitle = board.columnCells[columnPosition].title
        board.columnCells[columnPosition].title = title
        // create activity log
        User.findById(userId).then((user) => {
            createActivityLog(
                user._id,
                projectId,
                boardId,
                null,
                `${user.name} has renamed "${oldTitle}" column to "${title}`,
                ACTIVITY_LOG_TYPES.CHANGE
            )
        })
    }
    board.markModified('columnCells')
    await board.save()

    res.status(204).end()
})

exports.updateRow = asyncCatch(async (req, res, next) => {
    const { userId, projectId, boardId, rowPosition } = req.params
    const { newTitle } = req.body.nameValuePairs

    const board = await Board.findById(boardId)
    if (!board) throw new AppError('Unable to find board', 404)

    if (newTitle) {
        const oldTitle = board.rowCells[rowPosition]
        board.rowCells[rowPosition] = newTitle
        board.markModified('rowCells')
        await board.save()

        User.findById(userId).then((user) => {
            createActivityLog(
                user._id,
                projectId,
                boardId,
                null,
                `${user.name} has renamed "${oldTitle}" row to "${newTitle}`,
                ACTIVITY_LOG_TYPES.CHANGE
            )
        })
    }

    res.status(204).end()
})

exports.removeRow = asyncCatch(async (req, res, next) => {
    const { userId, projectId, rowPosition, boardId } = req.params
    const board = await Board.findById(boardId).populate('cells', 'Cell')
    if (!board) throw new AppError('Unable to find board', 404)

    const deletedRow = board.rowCells.splice(rowPosition, 1)[0]
    board.markModified('rowCells')

    const deletedCells = board.cells.splice(rowPosition, 1)[0]
    await Promise.all(deletedCells.map((cell) => removeACell(cell)))
    board.markModified('cells')
    await board.save()

    User.findById(userId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            boardId,
            null,
            `${user.name} has removed "${deletedRow}" row`,
            ACTIVITY_LOG_TYPES.REMOVE
        )
    })

    res.status(204).end()
})

exports.saveNewProject = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const { boards, chosenPosition, memberIds, adminIds, creatorId, title } =
        req.body

    const promises = await Promise.all(
        boards.map(async (board) => await saveABoard(board))
    )

    const newProject = await Project.create({
        chosenPosition,
        boards: promises,
        memberIds,
        adminIds,
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

    User.findById(userId).then((user) => {
        createActivityLog(
            userId,
            newProject._id,
            null,
            null,
            `${user.name} has created the project`,
            ACTIVITY_LOG_TYPES.NEW
        )
    })

    res.status(200).json(newProject)
})

exports.getCellsInARow = asyncCatch(async (req, res, next) => {
    const { boardId, rowPosition } = req.params

    const board = await Board.findById(boardId).populate({
        path: 'cells',
        model: 'Cell',
    })

    const cellsInRow = []
    const columnTitles = []
    for (let i = 0; i < board.columnCells.length; i += 1)
        columnTitles.push(board.columnCells[i].title)

    let idx = 0
    // eslint-disable-next-line no-restricted-syntax, guard-for-in
    for (const column in board.columnCells) {
        if (board.cells[rowPosition][idx].cellType === 'CellUser') {
            // eslint-disable-next-line no-await-in-loop
            await board.cells[rowPosition][idx].populate('users')
        }
        cellsInRow.push(board.cells[rowPosition][idx])
        idx += 1
    }

    res.status(200).json({
        columnTitles: columnTitles,
        cells: cellsInRow,
        rowTitle: board.rowCells[rowPosition],
    })
})

// exports.getUserWork = asyncCatch(async (req, res, next) => {
//     const { userId } = req.params
//     const projects = await Project.find({
//         memberIds: { $in: [userId] },
//     })

//     const works = []

//     await Promise.all(
//         projects.map(async (project) => {
//             await project.populate({
//                 path: 'boards',
//                 model: 'Board',
//                 populate: {
//                     path: 'cells',
//                     model: 'Cell',
//                 },
//             })

//             project.boards.forEach((board, boardPosition) => {
//                 const work = {}
//                 board.cells.forEach((cellRow, cellRowPosition) => {
//                     for (
//                         let cellIdx = 0;
//                         cellIdx < cellRow.length;
//                         cellIdx += 1
//                     ) {
//                         const cell = cellRow[cellIdx]
//                         if (cell.cellType === 'CellUser') {
//                             const isInArray = cell.users.some((user) =>
//                                 user.equals(userId)
//                             )

//                             if (isInArray) {
//                                 work.projectId = project._id
//                                 work.projectTitle = project.title
//                                 work.boardId = board._id
//                                 work.boardTitle = board.boardTitle
//                                 work.boardPosition = boardPosition
//                                 work.rowTitle = board.rowCells[cellRowPosition]
//                                 work.cellRowPosition = cellRowPosition
//                                 work.cellCreatedDate = cell.createdAt
//                                 works.push(work)
//                                 break
//                             }
//                         }
//                     }
//                 })
//             })
//         })
//     )

//     console.log(works)
//     res.status(200).json(works)
// })

exports.getUserWork = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const projects = await Project.find({
        memberIds: { $in: [userId] },
    }).populate({
        path: 'boards',
        model: 'Board',
        populate: {
            path: 'cells',
            model: 'Cell',
        },
    })

    const works = projects.reduce((result, project) => {
        project.boards.forEach((board, boardPosition) => {
            board.cells.forEach((cellRow, cellRowPosition) => {
                const workCell = cellRow.find(
                    (cell) =>
                        cell.cellType === 'CellUser' &&
                        cell.users.some((user) => user.equals(userId))
                )

                if (workCell) {
                    result.push({
                        projectId: project._id,
                        projectTitle: project.title,
                        boardId: board._id,
                        boardTitle: board.boardTitle,
                        boardPosition: boardPosition,
                        rowTitle: board.rowCells[cellRowPosition],
                        cellRowPosition: cellRowPosition,
                        cellCreatedDate: workCell.createdAt,
                    })
                }
            })
        })

        return result
    }, [])
    res.status(200).json(works)
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

    // populate "user" cell
    await Promise.all(
        populatedProject.boards.map(async (board) => {
            await Promise.all(
                board.cells.map(async (cellRow) => {
                    await Promise.all(
                        cellRow.map(async (cell) => {
                            if (cell.cellType === 'CellUser')
                                await cell.populate('users')
                        })
                    )
                })
            )
        })
    )

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

exports.deleteProjectById = asyncCatch(async (req, res, next) => {
    const { projectId } = req.params

    const project = await Project.findById(projectId)
    const promises = project.boards.map((boardId) => Board.findById(boardId))
    const listBoard = await Promise.all(promises)
    listBoard.forEach(async (board) => {
        await deleteABoard(board)
    })

    await Project.findByIdAndDelete(projectId)
    res.status(204).end()
})

exports.getMemberOfProject = asyncCatch(async (req, res, next) => {
    const { projectId } = req.params
    const project = await Project.findById(projectId)
    if (!project) return next(new AppError('Unable to find project', 404))

    const promises = project.memberIds.map((memberId) =>
        User.findById(memberId)
    )

    const listMember = await Promise.all(promises)
    res.status(200).json(listMember)
})

const sendNotificationOnMemberRequest = async (sender, receiver, projectId) => {
    const project = await Project.findById(projectId)
    const message = `${sender.name} has sent you a request to join project ${project.title}`

    await Notification.create({
        senderId: sender._id,
        receiverId: receiver._id,
        notificationType: 'MemberRequest',
        title: sender.name,
        content: message,
        timestamp: Date.now(),
        link: projectId,
    })
}

const sendNotificationOnReplyMemberRequest = async (
    sender,
    receiver,
    projectId,
    isAccept
) => {
    let message
    const project = await Project.findById(projectId)

    if (isAccept) {
        message = `${sender.name} was added to project ${project.title}`

        // add member to project
        project.memberIds.push(sender._id)
        await project.save()
        // add activity log
        createActivityLog(
            receiver._id,
            projectId,
            null,
            null,
            `${receiver.name} joined the project`,
            ACTIVITY_LOG_TYPES.UPDATE
        )
    } else message = `${sender.name} denied to joined project ${project.title}`

    await Notification.create({
        senderId: sender._id,
        receiverId: receiver._id,
        notificationType: 'NewMessage',
        title: sender.name,
        content: message,
        timestamp: Date.now(),
    })

    await Notification.findOneAndDelete({
        senderId: receiver._id,
        receiverId: sender._id,
        notificationType: 'MemberRequest',
    })
}

exports.requestMemberToJoinProject = asyncCatch(async (req, res, next) => {
    const { userId, projectId, receiverId } = req.params

    const senderId = userId
    if (senderId === receiverId)
        return next(new AppError('Unable to request to yourself', 400))

    const sender = await User.findOne({ _id: senderId })
    const receiver = await User.findOne({ _id: receiverId })
    if (!receiver || !sender) return next(new AppError(`User not found`, 400))

    // check if member request is pending
    const isExisted = await MemberRequest.findOne({
        senderId: sender._id,
        receiverId: receiver._id,
        projectId: projectId,
    })

    if (isExisted)
        return next(new AppError('The request is already on pending', 400))

    // check if receiver is a member of the project
    const isMember = await Project.findOne({
        _id: projectId,
        memberIds: { $in: [receiver._id] },
    })

    if (isMember) return next(new AppError('Already member of project', 400))

    // create the request in db
    const newMemberRequest = await MemberRequest.create({
        senderId: sender._id,
        receiverId: receiver._id,
        projectId: projectId,
    })

    if (!newMemberRequest)
        return next(new AppError('Unable to create new member request', 500))

    sendNotificationOnMemberRequest(sender, receiver, projectId)

    res.status(200).end()
})

exports.replyToJoinProject = asyncCatch(async (req, res, next) => {
    const { userId, projectId, receiverId, response } = req.params
    const senderId = userId

    if (response !== 'Accept' && response !== 'Deny')
        return next(new AppError('False response format', 400))

    const replier = await User.findOne({ _id: senderId })
    const requestSender = await User.findOne({ _id: receiverId })
    if (!replier || !requestSender)
        return next(new AppError(`User not found`, 400))

    const project = await Project.findOne({ _id: projectId })
    if (!project) {
        await Notification.findOneAndDelete({
            link: projectId,
            notificationType: 'MemberRequest',
        })
        return next(new AppError(`Project not found`, 400))
    }

    // check if member request still existed or not
    const isExisted = await MemberRequest.findOne({
        senderId: requestSender._id,
        receiverId: replier._id,
        projectId: projectId,
    })
    if (isExisted) {
        sendNotificationOnReplyMemberRequest(
            replier,
            requestSender,
            projectId,
            response === 'Accept'
        )

        await MemberRequest.findOneAndDelete({
            senderId: { $in: [requestSender._id, replier._id] },
            receiverId: { $in: [requestSender._id, replier._id] },
            projectId: projectId,
        })
    } else {
        await Notification.findOneAndDelete({
            link: projectId,
            senderId: requestSender._id,
            receiverId: replier._id,
            notificationType: 'MemberRequest',
        })
        return next(new AppError('The request is not existed', 400))
    }

    res.status(204).end()
})

// currently only update project's title
exports.updateProject = asyncCatch(async (req, res, next) => {
    const { userId, projectId } = req.params
    const { title } = req.body

    const project = await Project.findById(projectId)

    if (!project) return next(new AppError('Unable to find project', 404))
    const oldTitle = project.title
    project.title = title
    await project.save()

    User.findById(userId).then((user) => {
        createActivityLog(
            userId,
            projectId,
            null,
            null,
            `${user.name} has changed project title from "${oldTitle}" to "${title}"`,
            ACTIVITY_LOG_TYPES.CHANGE
        )
    })

    res.status(204).end()
})

exports.deleteMember = asyncCatch(async (req, res, next) => {
    const { projectId, memberId } = req.params
    const project = await Project.findById(projectId)
    if (!project) return next(new AppError('Unable to find project', 404))

    const indexMember = project.memberIds.indexOf(memberId)
    const indexAdmin = project.adminIds.indexOf(memberId)
    if (indexMember > -1) project.memberIds.splice(indexMember, 1)
    if (indexAdmin > -1) project.adminIds.splice(indexAdmin, 1)

    User.findById(memberId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            null,
            null,
            `${user.name} has been removed from project`,
            ACTIVITY_LOG_TYPES.REMOVE
        )
    })

    await project.save()
    res.status(204).end()
})

const sendNotificationOnAdminRequest = async (senderId, projectId) => {
    const project = await Project.findById(projectId)
    const sender = await User.findById(senderId)

    const message = `${sender.name} has sent a request to be an admin of project ${project.title}`

    project.adminIds.forEach(async (adminId) => {
        const isExisted = await Notification.findOne({
            senderId: sender._id,
            receiverId: adminId,
            notificationType: 'AdminRequest',
            link: projectId,
        })

        if (!isExisted) {
            await Notification.create({
                senderId: sender._id,
                receiverId: adminId,
                notificationType: 'AdminRequest',
                title: sender.name,
                content: message,
                timestamp: Date.now(),
                link: projectId,
            })
        }
    })
}

exports.requestAdmin = asyncCatch(async (req, res, next) => {
    const { userId, projectId } = req.params
    const senderId = userId

    await sendNotificationOnAdminRequest(senderId, projectId)
    res.status(200).end()
})

const sendNotificationOnReplyAdminRequest = async (
    sender,
    receiver,
    projectId,
    isAccept
) => {
    let message
    const project = await Project.findById(projectId)

    if (isAccept) {
        message = `You was accepted to be an admin of project ${project.title}`

        // add member to project
        project.adminIds.push(receiver._id)
        await project.save()

        // add activity log
        createActivityLog(
            receiver._id,
            projectId,
            null,
            null,
            `${receiver.name} has become an admin`,
            ACTIVITY_LOG_TYPES.UPDATE
        )
    } else message = `You was denied to be an admin of project ${project.title}`

    await Notification.create({
        senderId: sender._id,
        receiverId: receiver._id,
        notificationType: 'NewMessage',
        title: 'Admin',
        content: message,
        timestamp: Date.now(),
    })

    await Notification.findOneAndDelete({
        senderId: receiver._id,
        notificationType: 'AdminRequest',
    })
}

exports.replyToAdminRequest = asyncCatch(async (req, res, next) => {
    const { userId, projectId, memberId, response } = req.params

    if (response !== 'Accept' && response !== 'Deny')
        return next(new AppError('False response format', 400))

    const replier = await User.findOne({ _id: userId })
    const requestSender = await User.findOne({ _id: memberId })
    if (!replier || !requestSender)
        return next(new AppError(`User not found`, 400))

    const project = await Project.findById(projectId)
    if (!project) {
        await Notification.findOneAndDelete({
            notificationType: 'AdminRequest',
            link: projectId,
        })
        return next(new AppError('Unable to find project', 404))
    }
    // check if admin request still existed or not
    const isExisted = await Notification.findOne({
        senderId: requestSender._id,
        notificationType: 'AdminRequest',
        link: projectId,
    })

    if (isExisted) {
        sendNotificationOnReplyAdminRequest(
            replier,
            requestSender,
            projectId,
            response === 'Accept'
        )
    } else return next(new AppError('The request is not existed', 400))

    res.status(204).end()
})

exports.makeAdmin = asyncCatch(async (req, res, next) => {
    const { projectId, memberId } = req.params
    const project = await Project.findById(projectId)
    if (!project) return next(new AppError('Unable to find project', 404))

    project.adminIds.push(memberId)
    await project.save()

    User.findById(memberId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            null,
            null,
            `${user.name} has become an admin`,
            ACTIVITY_LOG_TYPES.UPDATE
        )
    })

    res.status(204).end()
})

exports.changeAdminToMember = asyncCatch(async (req, res, next) => {
    const { projectId, adminId } = req.params
    const project = await Project.findById(projectId)
    if (!project) return next(new AppError('Unable to find project', 404))

    const index = project.adminIds.indexOf(adminId)
    if (index > -1) project.adminIds.splice(index, 1)

    User.findById(adminId).then((user) => {
        createActivityLog(
            user._id,
            projectId,
            null,
            null,
            `${user.name} is no longer an admin`,
            ACTIVITY_LOG_TYPES.UPDATE
        )
    })

    await project.save()
    res.status(204).end()
})

exports.getActivityLogs = asyncCatch(async (req, res, next) => {
    const { projectId } = req.params

    const activityLogs = await ActivityLog.find({
        project: projectId,
    })
        .populate('creator', '_id name profileImagePath')
        .populate('project', '_id title')
        .populate('board', '_id boardTitle')
        .sort({ createdAt: -1 })

    res.status(200).json(activityLogs)
})
