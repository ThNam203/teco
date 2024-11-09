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

router
    .route('/:projectId')
    .get(projectController.getProjectById)
    .post(projectController.updateProject)
    .delete(projectController.deleteProjectById)

router.route('/get-member/:projectId').get(projectController.getMemberOfProject)

router.route('/:projectId/board').post(projectController.createAndGetNewBoard)

router.route('/:projectId/activity-log').get(projectController.getActivityLogs)

router
    .route('/request-member/:projectId/:receiverId')
    .post(projectController.requestMemberToJoinProject)

router.route('/request-admin/:projectId').post(projectController.requestAdmin)
router
    .route('/make-admin/:projectId/:memberId')
    .post(projectController.makeAdmin)
router
    .route('/change-admin-to-member/:projectId/:adminId')
    .post(projectController.changeAdminToMember)
router
    .route('/reply-to-admin-request/:projectId/:memberId/:response')
    .post(projectController.replyToAdminRequest)

router
    .route('/reply-join-project/:projectId/:receiverId/:response')
    .post(projectController.replyToJoinProject)

router
    .route('/delete-member/:projectId/:memberId')
    .delete(projectController.deleteMember)

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
    .get(projectController.getCellsInARow)
    .put(projectController.updateRow)
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
