var jsonwebtoken = require('jsonwebtoken');
var uuid = require('uuid-random');
const asyncCatch = require('../utils/asyncCatch');
const fs = require('fs')

exports.GenerateJisitJWT = asyncCatch(async (req, res, next) => {
    const privateKeyPath = process.env.JITSI_JWT_PRIVATE_KEY_PATH;
    const appId = process.env.JITSI_APP_ID;
    const kid = process.env.JITSI_KID;
    const jitsiPrivateKey = fs.readFileSync("keys/jitsi.key").toString()

    // const { name } = req.body;
    const jwt = generate(jitsiPrivateKey, { id: "namdeptrai", appId, kid })
    res.status(200).json(jwt);
});

const generate = (privateKey, { name, appId, kid }) => {
  const now = new Date()

  let jwt 
  try {
    jwt = jsonwebtoken.sign({
        aud: 'jitsi',
        context: {
          user: {
            id: uuid(),
            name,
            moderator: 'true'
          },
          features: {
            livestreaming: 'true',
            recording: 'false',
            transcription: 'false',
            "outbound-call": 'true'
          }
        },
        iss: 'chat',
        room: 'abcd',
        sub: appId,
        exp: Math.round(now.setHours(now.getHours() + 3) / 1000),
        nbf: (Math.round((new Date).getTime() / 1000) - 10)
      }, {key: privateKey, passphrase: "906090"}, { algorithm: 'RS256', header: { kid } })
  } catch (error) {
    console.log(error)
  }

  return jwt;
}