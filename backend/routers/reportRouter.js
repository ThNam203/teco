const express = require('express')

const router = express.Router()
const reportController = require('../controllers/reportController')

router
    .route('/report/:projectId')
    .get(reportController.getReportForProject)

module.exports = router
