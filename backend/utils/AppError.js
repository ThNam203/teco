class AppError extends Error {
    constructor(message, statusCode) {
        super()
        this.message = message
        this.statusCode = statusCode
        this.isExpected = true
    }
}

module.exports = AppError
