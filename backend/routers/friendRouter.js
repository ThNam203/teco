const express = require('express')

const router = express.Router()
const friendController = require('../controllers/friendController')

router.route('/create-request').post(friendController.createNewFriendRequest)
router
    .route('/reply-request/:response')
    .post(friendController.replyFriendRequest)
router.route('/get-friend/:userId').get(friendController.getFriendById)
module.exports = router
