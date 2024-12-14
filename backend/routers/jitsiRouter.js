const express = require('express')

const router = express.Router()
const jitsiController = require('../controllers/jitsiController')

router
    .route('/jitsi')
    .get(jitsiController.GenerateJisitJWT)
    
module.exports = router
