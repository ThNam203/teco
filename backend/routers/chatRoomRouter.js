const express = require('express')
const chatRoomController = require('../controllers/chatroomController')

const router = express.Router({
    mergeParams: true,
})

router.route('').post(chatRoomController.createNewChatRoom)
router.route('/:userId').get(chatRoomController.getAllChatroomsOfUser)

module.exports = router
