const { S3Client, DeleteObjectCommand } = require('@aws-sdk/client-s3')
const multerS3 = require('multer-s3')
const multer = require('multer')
const uuid = require('uuid')

const s3Client = new S3Client({
    region: 'ap-southeast-1',
    credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY,
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
    },
})

exports.deleteOldProfileImage = (path) => {
    const command = new DeleteObjectCommand({
        Bucket: 'workwise',
        Key: path.substring(path.lastIndexOf('/') + 1, path.length),
    })

    s3Client.send(command).catch(() => {})
}

exports.s3Upload = multer({
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
