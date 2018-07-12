var jwt = require('express-jwt');
var secret = require('../config').secret;


function getTokenFromHeader(req){
	if(req.headers.authorization && req.headers.authorization.split(' ')[0] === 'Token') {
		return req.headers.authorization.split(' ')[1];
	}
	return null;
}

var auth = {
	required: jwt({
		secret: secret,
		userProperty: 'payload', // req.payload = {"id":"5a6dd3da609927b1a3fc026b","username":"johnjacob","exp":1522872098,"iat":1517691698}
		getToken: getTokenFromHeader
	}),
	optional: jwt({
		secret: secret,
		userProperty: 'payload', // req.payload = {"id":"5a6dd3da609927b1a3fc026b","username":"johnjacob","exp":1522872098,"iat":1517691698}
		credentialsRequired: false,
		getToken: getTokenFromHeader
	})
};

module.exports = auth;