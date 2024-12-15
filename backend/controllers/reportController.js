const { compare } = require('bcrypt')
const { BOARD_COLUMN_TYPES } = require('../models/Board')
const Project = require('../models/Project')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

exports.getReportForProject = asyncCatch(async (req, res, next) => {
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

    const report = []
    populatedProject.boards.forEach((board) => {
        const newReportForBoard = {
            id: board.id,
            title: board.boardTitle,
            checkbox: [],
            timeline: [],
            user: [],
            status: [],
        }

        report.push(newReportForBoard)

        board.columnCells.forEach((col, colIndex) => {
            if (col.columnType === BOARD_COLUMN_TYPES.CHECKBOX) {
                let checkedCount = 0
                let uncheckedCount = 0

                board.cells.forEach((row) => {
                    const cell = row[colIndex]
                    if (cell) {
                        if (cell.isChecked) {
                            checkedCount += 1
                        } else {
                            uncheckedCount += 1
                        }
                    }
                })

                newReportForBoard.checkbox.push({
                    title: col.title,
                    checked: checkedCount,
                    unchecked: uncheckedCount,
                })
            } else if (col.columnType === BOARD_COLUMN_TYPES.TIMELINE) {
                const textValues = {
                    before: 0,
                    during: 0,
                    after: 0,
                    undefinedValue: 0,
                }

                board.cells.forEach((row) => {
                    const cell = row[colIndex]
                    const compareValue = compareTimeline(cell, new Date());

                    if (compareValue === 1) {
                        textValues.before += 1
                    } else if (compareValue === 0) {
                        textValues.during += 1
                    } else if (compareValue === -1) {
                        textValues.after += 1
                    } else {
                        textValues.undefinedValue += 1
                    }
                })

                newReportForBoard.timeline.push({
                    title: col.title,
                    values: textValues,
                })
            } else if (col.columnType === BOARD_COLUMN_TYPES.USER) {
                const userCounts = {}

                board.cells.forEach((row) => {
                    const cell = row[colIndex]
                    if (cell && Array.isArray(cell.users)) {
                        cell.users.forEach((user) => {
                            userCounts[user.id] = userCounts[user.id] || {
                                count: 0,
                                user,
                            }

                            userCounts[user.id].count += 1
                        })
                    }
                })

                newReportForBoard.user.push({
                    title: col.title,
                    users: userCounts,
                })
            } else if (col.columnType === BOARD_COLUMN_TYPES.STATUS) {
                const statusCounts = {}

                // Initialize counts for each status type if it exists in the first row
                const firstCell = board.cells[0][colIndex]
                if (firstCell && Array.isArray(firstCell.contents)) {
                    firstCell.contents.forEach((status, index) => {
                        statusCounts[status] = {
                            count: 0,
                            color: firstCell.colors[index],
                        }
                    })
                }

                // Count each status in all rows
                board.cells.forEach((row) => {
                    const cell = row[colIndex]
                    if (cell && cell.content) {
                        const status = cell.content
                        statusCounts[status].count =
                            (statusCounts[status].count || 0) + 1
                    }
                })

                newReportForBoard.status.push({
                    title: col.title,
                    statuses: statusCounts,
                })
            }
        })
    })

    res.status(200).json(report)
})

function compareTimeline(timeline, currentDate) {
    const { startYear, startMonth, startDay, endYear, endMonth, endDay } = timeline;
  
    // Handle -1 as not chosen
    if (startYear === -1 || startMonth === -1 || startDay === -1 ||
        endYear === -1 || endMonth === -1 || endDay === -1) {
      return null;
    }
  
    const startDate = new Date(startYear, startMonth - 1, startDay); // month is 0-indexed
    const endDate = new Date(endYear, endMonth - 1, endDay);
  
    if (currentDate < startDate) {
      return 1;
    } else if (currentDate > endDate) {
      return -1;
    } else {
      return 0;
    }
  }