const dotenv = require('dotenv')
const fs = require('fs')

// instead of the product_config which is hidden because of security,
// use dev_config.env file at your own risk
if (process.env.NODE_ENV === 'development') {
    const prodConfigPath = __dirname.concat('/product_config.env')
    if (fs.existsSync(prodConfigPath))
        dotenv.config({ path: __dirname.concat('/product_config.env') })
    else dotenv.config({ path: __dirname.concat('/dev_config.env') })
}
