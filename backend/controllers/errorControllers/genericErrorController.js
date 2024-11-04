const AppError = require('../../utils/AppError')

const sendErrorInDevelopmentEnv = function (err, res) {
    res.status(err.statusCode).json({
        devErrorData: {
            error: err,
            errorName: err.name,
            message: err.message,
            stack: err.stack,
        },
    })
}

const sendErrorInProductionEnv = function (err, res) {
    res.status(err.statusCode).json({
        message: err.message,
    })
}

exports.globalErrorHandler = (err, req, res, next) => {
    err.statusCode = err.statusCode || 500
    err.message = err.message || 'Internal server error'

    // if (process.env.NODE_ENV === 'development')
    //     sendErrorInDevelopmentEnv(err, res)
    // else
    console.log(err)
    console.log(`requested url ${req.url}`)
    sendErrorInProductionEnv(err, res)
}

exports.invalidUrlHandler = (req, res, next) => {
    next(new AppError('Invalid request url', 404))
}
