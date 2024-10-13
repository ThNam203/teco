const express = require('express')

const router = express.Router()
const friendController = require('../controllers/friendController')

router.route('/create-request').post(friendController.createNewFriendRequest)
router
    .route('/reply-request/:response')
    .post(friendController.replyFriendRequest)

module.exports = router
