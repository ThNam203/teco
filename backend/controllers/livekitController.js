// const { AccessToken, RoomServiceClient } = require('livekit-server-sdk')

// const Notification = require('../models/Notification')
// const AppError = require('../utils/AppError')
// const asyncCatch = require('../utils/asyncCatch')

// const livekitHost = process.env.LIVEKIT_HOST
// const livekitServerUrl = process.env.LIVEKIT_URL
// const apiKey = process.env.LIVEKIT_API_KEY
// const apiSecret = process.env.LIVEKIT_API_SECRET

// const svc = new RoomServiceClient(livekitHost, apiKey, apiSecret)

// // Start the live meeting
// exports.CreateSession = asyncCatch(async (req, res, next) => {
//     const { meetingID } = req.params

//     if (!meetingID) {
//         return next(new AppError('Missing information', 400))
//     }

//     const opts = {
//         name: meetingID,
//         emptyTimeout: 10 * 60,
//         maxParticipants: 100,
//     }

//     svc.createRoom(opts)
//         .then(() => {
//             res.status(204).send()
//         })
//         .catch((error) => {
//             return next(new AppError(error, 500))
//         })
// })

// // Delete a live meeting room
// exports.DeleteRoom = asyncCatch(async (req, res, next) => {
//     const { meetingID } = req.params

//     if (!meetingID) {
//         return next(new AppError('Missing information', 400))
//     }

//     svc.deleteRoom(meetingID)
//         .then(() => {
//             res.status(200).send()
//         })
//         .catch((error) => {
//             return next(new AppError(error, 500))
//         })
// })

// // Get Join Connection Details
// exports.GetJoinDetails = asyncCatch(async (req, res, next) => {
//     const { meetingID } = req.params
//     const username = req.query.name

//     if (!username || !meetingID) {
//         console.error(meetingID, username)
//         return next(new AppError('Missing information', 400))
//     }

//     try {
//         const rooms = await svc.listRooms()
//         const roomExists = rooms.some((room) => room.name === meetingID)

//         if (!roomExists) {
//             console.log("rooms: ", rooms)
//             return next(new AppError('Room not found', 404))
//         }

//         const token = new AccessToken(apiKey, apiSecret, {
//             identity: username,
//             ttl: '1h',
//         })

//         token.addGrant({
//             room: meetingID,
//             roomJoin: true,
//         })

//         const participantToken = token.toJwt()

//         res.status(200).json({
//             participantToken,
//             participantName: username,
//             serverUrl: livekitServerUrl,
//             roomName: meetingID,
//         })
//     } catch (error) {
//         res.status(500).json({ error: error.message })
//     }
// })