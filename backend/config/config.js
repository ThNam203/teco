const dotenv = require('dotenv')
const fs = require('fs')

// instead of the product_config which is hidden because of security,
// use dev_config.env file at your own risk
if (process.env.NODE_ENV === 'development' || process.env.NODE_ENV === undefined) {
    const devConfigPath = __dirname.concat('/.env')
    if (fs.existsSync(devConfigPath))
        dotenv.config({ path: devConfigPath })
    else {
        dotenv.config({ path: __dirname.concat('/product.env') })
    }
}
