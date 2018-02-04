var mongoose = require('mongoose');
var uniqueValidator = require('mongoose-unique-validator');
var crypto = require('crypto');
var jwt = require('jsonwebtoken');
var secret = require('../config').secret;

var UserSchema = new mongoose.Schema({
	username: {type: String, lowercase: true, unique: true, required: [true, "no puede ser blanco"], match: [/^[a-zA-Z0-9]+$/, 'no es válido'], index: true},
	email: {type: String, lowercase: true, unique: true, required: [true, "no puede ser blanco"], match: [/\S+@\S+\.\S+/, 'no es válido'], index: true},
	image: String,
	properties: [ {type: mongoose.Schema.Types.ObjectId, ref: 'Property'}],
	role: {type: String, default: 'VIEWER', match: [/ADMIN|VIEWER|REGISTERED/, 'no es válido']},
	hash: String,
	salt: String
}, {timestamp: true});

UserSchema.plugin(uniqueValidator, {message: 'ya existe.'});

/**
 * Properties CRUD methods
 */
UserSchema.methods.addProperty = function(id){
	if(this.properties.indexOf(id) === -1) {
		console.log(id + ' added!');
		this.properties.push(id);
	}
	console.log(this.properties.length + " properties");
	return this.save();
};

UserSchema.methods.removeProperty = function(id){
	this.properties.remove(id);
	return this.save();
};

UserSchema.methods.isProperty = function(id){
	return this.properties.some(function(propertyId){
	  return propertyId.toString() === id.toString();
	});
};

UserSchema.methods.toPropertyJSONFor = function(){
	return {
		username: this.username,
		properties: this.properties.map(function(property){
			return property.toGeoJSONFor();
		}),
		propertiesCount: this.properties.length
	}
};

/**
 * Aunthetication methods
 */
UserSchema.methods.setPassword = function(password){
	this.salt = crypto.randomBytes(16).toString('hex');
	this.hash = crypto.pbkdf2Sync(password, this.salt, 10000, 512, 'sha512').toString('hex');
};

UserSchema.methods.validPassword = function(password){
	var hash = crypto.pbkdf2Sync(password, this.salt, 10000, 512, 'sha512').toString('hex');
	return this.hash = hash;
};

UserSchema.methods.generateJWT = function() {
	var today = new Date();
	var exp = new Date(today);
	exp.setDate(today.getDate() + 60);

	return jwt.sign({
		id: this._id,
		username: this.username,
		role: this.role,
		exp: parseInt(exp.getTime() / 1000),
	}, secret);	
};

UserSchema.methods.toAuthJSON = function(){
	return {
		username: this.username,
		email: this.email,
		role: this.role,
		token: this.generateJWT()
	}
};

UserSchema.methods.isAdmin = function(){
	return this.role.toString() === 'ADMIN';
};

mongoose.model('User', UserSchema);


