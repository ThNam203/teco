const jwt = require('jsonwebtoken')

const User = require('../models/User')
// const JWTBlacklist = require('../models/JWTBlacklist')
const AppError = require('../utils/AppError')
const asyncCatch = require('../utils/asyncCatch')

const getJWTToken = (userId) =>
    jwt.sign({ id: userId }, process.env.JWT_SECRET, {
        expiresIn: '30 days',
    })

const verifyAndGetJWTToken = async (req, next) => {
    if (
        !req.headers.authorization ||
        !req.headers.authorization.startsWith('Bearer')
    )
        return next(new AppError('Missing authorization header', 401))

    const token = req.headers.authorization.split(' ')[1]
    if (!token) return next(new AppError('Missing JWT token', 401))

    // const freshToken = await JWTBlacklist.findOne({ jwtData: token })
    // if (freshToken)
    //     return next(
    //         new AppError('Invalid request, this token is already revoked', 401)
    //     )

    jwt.verify(token, process.env.JWT_SECRET)
    return token
}

exports.signUp = asyncCatch(async (req, res, next) => {
    const newUser = await User.create(req.body)
    if (!newUser) return next(new AppError('Unable to create new user', 500))
    res.status(204).end()
})

exports.logIn = asyncCatch(async (req, res, next) => {
    const { email, password } = req.body

    if (!email || !password)
        return next(new AppError('Missing email or password', 400))

    const freshUser = await User.findOne({ email }).select('+password')
    if (!freshUser)
        return next(
            new AppError('Invalid username or password. Please try again', 404)
        )

    if (!(await freshUser.checkPassword(password, freshUser.password)))
        return next(
            new AppError('Invalid username or password. Please try again', 404)
        )

    const jwtToken = getJWTToken(freshUser.id)
    res.status(200).json(jwtToken)
})

exports.isUser = asyncCatch(async (req, res, next) => {
    const token = await verifyAndGetJWTToken(req, next)
    if (!token) return next(new AppError('Invalid JWT', 401))

    const data = jwt.decode(token)
    const userId = data.id
    const user = await User.findById(userId)
    if (!user) return next(new AppError('Invalid JWT', 401))
    next()
})

// check if :user_id is the same as the jwt token
exports.isOwnerOfThePath = asyncCatch(async (req, res, next) => {
    const jwtToken = req.headers.authorization.split(' ')[1]
    const { id: userId } = jwt.decode(jwtToken)
    const { userId: idParam } = req.params
    if (userId !== idParam)
        return next(
            new AppError(
                `Authorization header and user's id is not match together`,
                400
            )
        )

    next()
})

// exports.logOut = asyncCatch(async (req, res, next) => {
//     const token = await verifyAndGetJWTToken(req, next)

//     const jwtBlacklist = await JWTBlacklist.create({ jwtData: token })
//     if (!jwtBlacklist)
//         return next(new AppError('Unable to logout, try again', 500))

//     res.status(204).json()
// })

// exports.validateJwt = asyncCatch(async (req, res, next) => {
//     try {
//         await verifyAndGetJWTToken(req, next)
//     } catch (error) {
//         return next(error)
//     }

//     res.status(204).json()
// })
