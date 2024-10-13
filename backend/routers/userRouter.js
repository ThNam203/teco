const express = require('express')

const router = express.Router()
const userController = require('../controllers/userController')

router.route('/user/:userId').get(userController.getUserById)
router.route('/adduser').post(userController.addNewUser)
router.route('/updateuser').post(userController.updateUserById)
router.route('/alluser').get(userController.getAllUsers)

module.exports = router
