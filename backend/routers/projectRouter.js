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
router.route('/get-member/:projectId').get(projectController.getMemberOfProject)

router.route('/:projectId/board').post(projectController.createAndGetNewBoard)
router
    .route('/request-member/:projectId/:receiverId')
    .post(projectController.requestMemberToJoinProject)

router.route('/request-admin/:projectId').post(projectController.requestAdmin)
router
    .route('/reply-to-admin-request/:projectId/:memberId/:response')
    .post(projectController.replyToAdminRequest)

router
    .route('/reply-join-project/:projectId/:receiverId/:response')
    .post(projectController.replyToJoinProject)

router.route('/update-project/:projectId').post(projectController.updateProject)
router
    .route('/delete-member/:projectId/:memberId')
    .delete(projectController.deleteMember)

router
    .route('/:projectId/board/:boardId')
    .put(projectController.updateBoard)
    .delete(projectController.removeBoard)

router.route('/delete/:projectId').delete(projectController.deleteProjectById)

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
