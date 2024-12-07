const express = require('express')

const router = express.Router()
const friendController = require('../controllers/friendController')

// TODO: rework the endpoints
router
    .route('/:userId/friend/:unfriendUserId')
    .delete(friendController.unFriend)
router.route('/create-request').post(friendController.createNewFriendRequest)
router
    .route('/reply-request/:response')
    .post(friendController.replyFriendRequest)
router.route('/get-friend/:userId').get(friendController.getFriendById)
module.exports = router
