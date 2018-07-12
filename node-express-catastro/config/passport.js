var passport = require('passport');
var LocalStrategy = require('passport-local').Strategy;
var mongoose = require('mongoose');
var User = mongoose.model('User');

passport.use(new LocalStrategy({
  usernameField: 'user[email]',
  passwordField: 'user[password]'
}, function(email, password, done) {
  User.findOne({email: email}).then(function(user){
    if(!user || !user.validPassword(password)){
      return done(null, false, {errors: {'email or password': '(password) no es válido'}});
      // or you could create a new account
    }

    return done(null, user);
    // Optional info can be passed, typically including associated scope (all, viewer...), 
	// which will be set by Passport at req.authInfo to be used by later 
	// middleware for authorization and access control.
	// return done (null, user, { scope: user.scope});

  }).catch(done);
}));


/* @see https://github.com/jaredhanson/passport-http-bearer 
		--> npm install passport-http-bearer */
/*
var BearerStrategy = require('passport-http-bearer').Strategy;
password.use(new BearerStrategy( 
	function(token, done) {
		User.findOne({ token: token }, function(err, user) {
			if (err) { return done(err); }
			if (!user) {return done(null, false); }
			return done (null, user, {scope: 'all'})
		})
	}));
*/

/* @see https://github.com/themikenicholson/passport-jwt
		--> npm install passport-jwt */
/*
var JwtStrategy = require('passport-jwt').Strategy,
	ExtractJwt = require('passport-jwt').ExtractJwt;
var secret = require('../config').secret;
passport.use(new JwtStrategy({
		jwtFromRequest = ExtractJwt.fromAuthHeaderAsBearerToken(),
		secret: secret,
		issuer: 'aragonesbanegas.catastro.com',
		audience: 'catastro-app.com' 
	}, function (jwt_payload, done) {
		User.findOne({id: jwt_payload.sub}, function(err, user)) {
			if (err) { return done(err, false); }
			if (user) {
				return done (null, user);
				// Optional info can be passed, typically including associated scope (all, viewer...), 
				// which will be set by Passport at req.authInfo to be used by later 
				// middleware for authorization and access control.
				// return done (null, user, { scope: user.scope});
			} else {
				return done(null, false, {errors: {'JWT token': 'is invalid'}});
	            // or you could create a new account
			}

	}
});
*/
