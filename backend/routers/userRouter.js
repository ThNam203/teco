const express = require('express')

const router = express.Router()
const userController = require('../controllers/userController')

router.route('/:userId/get-user-by-id').get(userController.getUserById)
router
    .route('/:userId/get-user-by-email/:email')
    .get(userController.getUserByEmail)
router.route('/add-user').post(userController.addNewUser)
router.route('/update-user').post(userController.updateUser)
router.route('/all-user').get(userController.getAllUsers)
router
    .route('/:userId/upload-avatar')
    .post(
        userController.uploadProfileImage.single('avatar-file'),
        userController.updateProfileImage
    )
router
    .route('/:userId/save-recent-project-id/:projectId')
    .post(userController.saveRecentProjectId)
router
    .route('/:userId/get-recent-project-id')
    .get(userController.getRecentProjectId)
router
    .route('/:userId/get-my-own-project-id')
    .get(userController.getMyOwnProjectIds)
module.exports = router
