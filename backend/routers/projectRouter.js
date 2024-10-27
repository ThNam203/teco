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
    .route('/:projectId/board/:boardId/column')
    .put(projectController.addNewColumn)

router.route('/:projectId/board/:boardId/row').put(projectController.addNewRow)

router
    .route('/:projectId/board/:boardId/row/:rowPosition')
    .get(projectController.getCellsInARow)

router
    .route('/:projectId/board/:boardId/cell/:cellId')
    .put(projectController.updateACell)
    .post(
        s3Controller.s3Upload.array('files'),
        projectController.addNewUpdateTask
    )

module.exports = router
