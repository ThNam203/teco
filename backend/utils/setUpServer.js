const mongoose = require('mongoose')

// run config file
require('../config/config')

mongoose.connect(process.env.MONGODB_CONNECT).then(() => {
    console.log(
        `connected to database with path ${process.env.MONGODB_CONNECT}`
    )
})
