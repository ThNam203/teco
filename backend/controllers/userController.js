const uuid = require('uuid')
const { S3Client, DeleteObjectCommand } = require('@aws-sdk/client-s3')
const multerS3 = require('multer-s3')
const multer = require('multer')

const User = require('../models/User')
const asyncCatch = require('../utils/asyncCatch')
const AppError = require('../utils/AppError')

const s3Client = new S3Client({
    region: 'ap-southeast-1',
    credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
    },
})

const deleteOldProfileImage = (path) => {
    const command = new DeleteObjectCommand({
        Bucket: 'workwise',
        Key: path.substring(path.lastIndexOf('/') + 1, path.length),
    })

    s3Client.send(command).catch(() => {})
}

exports.uploadProfileImage = multer({
    storage: multerS3({
        s3: s3Client,
        bucket: 'workwise',
        acl: 'public-read',
        contentType: multerS3.AUTO_CONTENT_TYPE,
        key: function (req, file, cb) {
            cb(null, uuid.v4())
        },
    }),
})

exports.updateProfileImage = asyncCatch(async (req, res, next) => {
    if (!req.file || !req.file.location)
        return next(new AppError('Unable to upload profile image', 500))

    const { userId } = req.params
    const user = await User.findById(userId)
    if (user.profileImagePath) deleteOldProfileImage(user.profileImagePath)

    user.profileImagePath = req.file.location
    await user.save()

    res.status(200).json({ imagePath: req.file.location })
})

exports.getUserById = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const user = await User.findById(userId)
    if (!user) return next(new AppError('No user found!', 400))

    res.status(200).json(user)
})

exports.getAllUsers = asyncCatch(async (req, res, next) => {
    const user = await User.find({})
    if (!user) return next(new AppError('No email found!', 400))

    res.status(200).json(user)
})

exports.getUserByEmail = asyncCatch(async (req, res, next) => {
    const { email } = req.params
    const user = await User.findOne({ email })
    if (!user) return next(new AppError('No email found!', 400))

    res.status(200).json(user)
})

exports.updateUserById = asyncCatch(async (req, res, next) => {
    const { id } = req.params
    const updatedUser = await User.findByIdAndUpdate(id, req.body, {
        new: true,
        runValidators: true,
    })
    if (!updatedUser) return next(new AppError('No user found!', 400))

    res.status(200).json(updatedUser)
})

exports.addNewUser = asyncCatch(async (req, res, next) => {
    const newUser = new User({
        name: req.body.name,
        email: req.body.email,
        password: req.body.password,
        phoneNumber: req.body.phoneNumber,
        profileImagePath: req.body.profileImagePath,
        location: req.body.location,
    })

    const savedUser = await newUser.save()
    res.status(200).json({
        message: 'user was saved!!',
    })

    if (!savedUser) return next(new AppError('Save new user ERROR!', 400))

    res.status(200).json(savedUser)
})
