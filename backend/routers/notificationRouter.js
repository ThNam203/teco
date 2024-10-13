const express = require('express')

const router = express.Router()
const notificationController = require('../controllers/notificationController')

router
    .route('/notification/:receiverId')
    .get(notificationController.getNotificationByReceiverId)

router
    .route('/delete-notification/:notificationId')
    .get(notificationController.deleteNotificationById)

router
    .route('/update-notification')
    .post(notificationController.updateReadNotification)

module.exports = router
