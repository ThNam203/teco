const express = require('express')
const chatRoomController = require('../controllers/chatroomController')

const router = express.Router({
    mergeParams: true,
})

router
    .route('')
    .get(chatRoomController.getAllChatroomsOfUser)
    .post(chatRoomController.createNewChatRoom)

router.route('/:chatRoomId').get(chatRoomController.getAChatRoom)

router
    .route('/:chatRoomId/member')
    .patch(chatRoomController.addMemberToGroupChat)

router
    .route('/:chatRoomId/member/:memberId')
    .delete(chatRoomController.deleteMemberFromGroupChat)

router.route('/:chatRoomId/message').get(chatRoomController.getAllMessage)

module.exports = router
