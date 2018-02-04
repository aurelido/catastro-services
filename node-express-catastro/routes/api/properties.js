var router = require('express').Router();
var passport = require('passport');
var mongoose = require('mongoose');
var User = mongoose.model('User');
var Property = mongoose.model('Property');
var auth = require('../auth');

/**
 * Parameter PROPERTY
 */
router.param('property', function(req, res, next, rc){
    console.log('Retriving Property ' + rc);
    Property.findOne( {rc: rc} )
        .then(function(property) {
            if(!property) { return res.sendStatus(404); }
            console.log('Property ' + property.rc + ' founded!');
            req.property = property;
            return next();
        }).catch(next);
});

/**
 * Add property in User's properties list
 */
router.post('/:property/archive', auth.required, function(req, res, next){
    var propertyId = req.property._id;
    console.log('Adding ' + req.property.rc + ' to ' + req.payload.username + ' properties.');
    User.findById(req.payload.id).then(function(user){
      if (!user) { return res.sendStatus(401); }
  
      return user.addProperty(propertyId).then(function(){
        return req.property.updateUserCount().then(function(property){
            return res.json({property: req.property.toGeoJSONFor()});
        });
        
      });
    }).catch(next);
});

/**
 * Remove property from User's properties list
 */
router.delete('/:property/archive', auth.required, function(req, res, next){
    var profileId = req.profile._id;
  
    User.findById(req.payload.id).then(function(user){
      if (!user) { return res.sendStatus(401); }
  
      return user.removeProperty(propertyId).then(function(){
        return res.json({property: req.property.toGeoJSONFor()});
      });
    }).catch(next);
});

/**
 * CRUD Properties Methods
 */
router.get('/:property', auth.optional, function(req, res, next){
    var format = 'json';

    if(typeof req.query.format === 'geojson'){
        limit = req.query.format;
    }

    return (format === 'json') ? res.json({property:req.property.toJSONFor()}) : res.json({property:req.property.toGeoJSONFor()}) ;
});

router.post('/', auth.required, function(req, res, next) {
    User.findById(req.payload.id).then(function(user){
        if(!user) { return res.sendStatus(401); }

        var property = new Property(req.body.property);
        updateProperty(property,function(){
            console.log('Adding ' + property.rc + ' to the repository.');
            return res.json({property: property});
        });
    }).catch(next);
});

function updateProperty(property,callback){
    Property.find({rc : property.rc}, function (err, properties) {
        if (properties.length){
            callback('Property exists already', null);
        } else {
            property.save(function(err){
                callback(err,property);
            });
        }
    });
}

router.delete('/:property', auth.required, function(req, res, next) {
    User.findById(req.payload.id).then(function(user){
        if(!user){ return res.sendStatus(401); }
		if(!user.isAdmin){ return res.sendStatus(401); }
        
        /* Also references in user's properties should be removed */
        return req.property.remove().then(function(){
            return res.sendStatus(204);
        });
    });
});

router.get('/', auth.required, function(req, res, next) {
    var query = {};
    var limit = 5;
    var offset = 0;
    
    if(typeof req.query.limit !== 'undefined'){
        limit = req.query.limit;
    }
    if(typeof req.query.offset !== 'undefined'){
        offset = req.query.offset;
    }

    console.log('Retriving Properties of ' + req.payload.username);
    User.findById(req.payload.id)
        .populate('properties')
        .limit(Number(limit))
        .skip(Number(offset))
        .exec(function (err, user) {
            if (err) return handleError(err);
            if(!user) { return res.sendStatus(401); }
            res.json( user.toPropertyJSONFor() );
        });
});

// router.get('/', auth.optional, function(req, res, next){
//     var query = {};
//     var limit = 5;
//     var offset = 0;
    
//     if(typeof req.query.limit !== 'undefined'){
//         limit = req.query.limit;
//     }
//     if(typeof req.query.offset !== 'undefined'){
//         offset = req.query.offset;
//     }

//     return Promise.all([
//         Property.find(query)
//           .limit(Number(limit))
//           .skip(Number(offset))
//           .sort({createdAt: 'desc'})
//           //.populate('author')
//           .exec(),
//         Property.count(query).exec(),
//         req.payload ? User.findById(req.payload.id) : null,
//       ]).then(function(results){
//         var properties = results[0];
//         var propertiesCount = results[1];
//         var user = results[2];
    
//         return res.json({
//             properties: properties.map(function(property){
//                 return property.toGeoJSONFor();
//             }),
//             propertiesCount: propertiesCount
//         });
//       }).catch(next);
// });

module.exports = router;