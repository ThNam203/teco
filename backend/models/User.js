const mongoose = require('mongoose')
const validator = require('validator')
const bcrypt = require('bcrypt')
const AppError = require('../utils/AppError')

const userSchema = new mongoose.Schema(
    {
        _id: {
            type: mongoose.Schema.Types.ObjectId,
            auto: true,
        },
        name: {
            type: String,
            required: [true, 'Missing name property'],
            trim: true,
        },
        email: {
            type: String,
            required: [true, 'Missing email property'],
            unique: true,
            validator: [validator.isEmail, 'Email is invalid'],
            trim: true,
        },
        password: {
            type: String,
            required: [true, 'Missing password property'],
            min: [8, 'Password length must be longer or equal to 8 characters'],
            select: false,
        },
        phoneNumber: {
            type: String,
            min: [10, 'Phone number must be longer or equal to 10 digits'],
            trim: true,
        },
        profileImagePath: {
            type: String,
            trim: true,
        },
        location: {
            type: String,
            trim: true,
        },
        introduction: {
            type: String,
            trim: true,
        },
        birthday: {
            type: String,
            trim: true,
        },
        teams: {
            type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
        },
        chatRooms: {
            type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'ChatRoom' }],
        },
        lastPasswordChangedTime: {
            type: Date,
            select: false,
        },
    },
    {
        timestamps: true,
    }
)

userSchema.methods.checkPassword = async function (
    comingPassword,
    realPassword
) {
    return await bcrypt.compare(comingPassword, realPassword)
}

userSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next()

    if (this.password.length < 8)
        throw new AppError(
            'Password length must be longer than 8 characters',
            400
        )

    this.password = await bcrypt.hash(this.password, 12)
    next()
})

module.exports = mongoose.model('User', userSchema)
