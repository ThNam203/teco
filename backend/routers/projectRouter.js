const express = require('express')
const projectController = require('../controllers/projectController')
const s3Controller = require('../controllers/awsS3Controllers')

const router = express.Router({
    mergeParams: true,
})

router
    .route('/')
    .get(projectController.getAllProjectOfUser)
    .post(projectController.saveNewProject)

router.route('/:projectId').get(projectController.getProjectById)

router.route('/:projectId/board').post(projectController.createAndGetNewBoard)

router
    .route('/:projectId/board/:boardId')
    .put(projectController.updateBoard)
    .delete(projectController.removeBoard)

router
    .route('/:projectId/board/:boardId/column')
    .put(projectController.addNewColumn)

router
    .route('/:projectId/board/:boardId/column/:columnPosition')
    .put(projectController.updateColumn)
    .delete(projectController.removeColumn)

router.route('/:projectId/board/:boardId/row').put(projectController.addNewRow)

router
    .route('/:projectId/board/:boardId/row/:rowPosition')
    .put(projectController.updateRow)
    .get(projectController.getCellsInARow)
    .delete(projectController.removeRow)

router
    .route('/:projectId/board/:boardId/cell/:cellId')
    .put(projectController.updateACell)

router
    .route('/:projectId/board/:boardId/cell-update/:cellId')
    .get(projectController.getAllUpdateTasksOfACell)
    .post(
        s3Controller.s3Upload.array('files'),
        projectController.addNewUpdateTask
    )

router
    .route('/:projectId/board/:boardId/cell-update/:cellId/:updateTaskId')
    .patch(projectController.toggleUpdateTaskLike)
    .delete(projectController.removeUpdateTask)

module.exports = router
