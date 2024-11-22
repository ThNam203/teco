const express = require('express')
const projectController = require('../controllers/projectController')
const s3Controller = require('../controllers/awsS3Controllers')

const router = express.Router({
    mergeParams: true,
})

router.route('/work').get(projectController.getUserWork)

router
    .route('/')
    .get(projectController.getAllProjectOfUser)
    .post(projectController.saveNewProject)

router
    .route('/:projectId')
    .get(projectController.getProjectById)
    .delete(projectController.deleteProjectById)
    .post(projectController.updateProject)

router.route('/:projectId/get-member').get(projectController.getMemberOfProject)

router.route('/:projectId/board').post(projectController.createAndGetNewBoard)

router.route('/:projectId/activity-log').get(projectController.getActivityLogs)

router
    .route('/:projectId/request-member/:receiverId')
    .post(projectController.requestMemberToJoinProject)

router.route('/:projectId/request-admin').post(projectController.requestAdmin)
router
    .route('/:projectId/make-admin/:memberId')
    .post(projectController.makeAdmin)
router
    .route('/:projectId/change-admin-to-member/:adminId')
    .post(projectController.changeAdminToMember)
router
    .route('/:projectId/reply-to-admin-request/:memberId/:response')
    .post(projectController.replyToAdminRequest)

router
    .route('/:projectId/reply-join-project/:receiverId/:response')
    .post(projectController.replyToJoinProject)

router
    .route('/:projectId/delete-member/:memberId')
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
    .route('/:projectId/board/:boardId/update-task/:cellId')
    .get(projectController.getAllUpdateTasksOfACell)
    .post(
        s3Controller.s3Upload.array('files'),
        projectController.addNewUpdateTask
    )

router
    .route('/:projectId/board/:boardId/update-task/:cellId/:updateTaskId')
    .get(projectController.getUpdateTaskAndComment)
    .patch(projectController.toggleUpdateTaskLike)
    .post(
        s3Controller.s3Upload.array('files'),
        projectController.addNewCommentToUpdateTask
    )
    .delete(projectController.removeUpdateTask)

router
    .route(
        '/:projectId/board/:boardId/update-task/:cellId/:updateTaskId/:commentId'
    )
    .patch(projectController.toggleUpdateTaskCommentLike)
    .delete(projectController.deleteComment)

module.exports = router
