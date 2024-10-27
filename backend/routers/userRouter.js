const express = require('express')

const router = express.Router()
const userController = require('../controllers/userController')

router.route('/user-id/:userId').get(userController.getUserById)
router.route('/user-email/:email').get(userController.getUserByEmail)
router.route('/add-user').post(userController.addNewUser)
router.route('/update-user').post(userController.updateUser)
router.route('/all-user').get(userController.getAllUsers)
router
    .route('/upload-avatar/:userId')
    .post(
        userController.uploadProfileImage.single('avatar-file'),
        userController.updateProfileImage
    )

module.exports = router
