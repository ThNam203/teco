const uuid = require('uuid')
const { S3Client, DeleteObjectCommand } = require('@aws-sdk/client-s3')
const multerS3 = require('multer-s3')
const multer = require('multer')

const User = require('../models/User')
const Project = require('../models/Project')
const RecentAccess = require('../models/RecentAccess')
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
        Bucket: 'squadsense',
        Key: path.substring(path.lastIndexOf('/') + 1, path.length),
    })

    s3Client.send(command).catch(() => {})
}

exports.uploadProfileImage = multer({
    storage: multerS3({
        s3: s3Client,
        bucket: 'squadsense',
        acl: 'public-read',
        contentType: multerS3.AUTO_CONTENT_TYPE,
        key: function (req, file, cb) {
            cb(null, uuid.v4())
        },
    }),
})

exports.updateProfileImage = asyncCatch(async (req, res, next) => {
    if (!req.file) {
        return next(new Error('Unable to upload profile image'))
    }

    const { userId } = req.params
    const user = await User.findById(userId)
    if (user.profileImagePath) {
        deleteOldProfileImage(user.profileImagePath)
    }

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
    const user = await User.findOne({ email: email })
    if (!user) return next(new AppError('No email found!', 400))

    res.status(200).json(user)
})

exports.updateUser = asyncCatch(async (req, res, next) => {
    const { _id } = req.body
    const updatedUser = await User.findByIdAndUpdate(_id, req.body, {
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

const saveAccess = async (userId, projectId, timeAccessed) => {
    const recentAccess = await RecentAccess.findOne({ userId: userId })
    if (recentAccess) {
        const index = recentAccess.recentProjectIds.indexOf(projectId)

        if (index > -1) {
            recentAccess.timeAccessed[index] = timeAccessed
            await recentAccess.save()
        } else {
            recentAccess.recentProjectIds.push(projectId)
            recentAccess.timeAccessed.push(timeAccessed)
            await recentAccess.save()
        }
    } else {
        await RecentAccess.create({
            userId: userId,
            recentProjectIds: [projectId],
            timeAccessed: [timeAccessed],
        })
    }
}

exports.saveRecentProjectId = asyncCatch(async (req, res, next) => {
    const { userId, projectId } = req.params
    const user = User.findById(userId)
    if (!user) return next(new AppError('No user found!', 400))

    const project = Project.findById(projectId)
    if (!project) return next(new AppError('No project found!', 400))

    const date = new Date()
    saveAccess(userId, projectId, date)

    res.status(200).end()
})

exports.getRecentProjectId = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const user = User.findById(userId)
    if (!user) return next(new AppError('No user found!', 400))

    const recentAccess = await RecentAccess.findOne({ userId: userId })
    if (!recentAccess) res.status(200).json([])

    //filter out projects that no longer exist or user is no longer a member of the project
    recentAccess.recentProjectIds.forEach(async (projectId) => {
        const project = await Project.findById(projectId)
        const index = recentAccess.recentProjectIds.indexOf(projectId)
        if (!project) {
            recentAccess.recentProjectIds.pull(projectId)
            recentAccess.timeAccessed.splice(index, 1)
            await recentAccess.save()
        } else if (!project.memberIds.includes(userId)) {
            recentAccess.recentProjectIds.pull(projectId)
            recentAccess.timeAccessed.splice(index, 1)
            await user.save()
        } else {
            //filter project that have timeAccessed more than 1 week
            const timeAccessed = recentAccess.timeAccessed[index]
            const date = new Date()
            //total time of a week in milliseconds
            const AWeekTime = new Date(date.getTime() - 7 * 24 * 60 * 60 * 1000)
            if (date - timeAccessed > AWeekTime) {
                recentAccess.recentProjectIds.pull(projectId)
                recentAccess.timeAccessed.splice(index, 1)
                await recentAccess.save()
            }
        }
    })

    if (recentAccess.recentProjectIds.length === 0) res.status(200).json([])
    else res.status(200).json(recentAccess.recentProjectIds)
})

exports.getMyOwnProjectIds = asyncCatch(async (req, res, next) => {
    const { userId } = req.params
    const projects = await Project.find({ creatorId: userId })

    const projectIds = []
    projects.forEach((project) => {
        projectIds.push(project._id.toString())
    })
    res.status(200).json(projectIds)
})
