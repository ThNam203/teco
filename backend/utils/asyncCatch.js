const asyncCatch = (fn) =>
    function (req, res, next) {
        fn(req, res, next).catch(next)
    }

module.exports = asyncCatch
