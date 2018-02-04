var mongoose = require('mongoose');
var uniqueValidator = require('mongoose-unique-validator');
var User = mongoose.model('User');

var PropertySchema = new mongoose.Schema({
    rc: {type: String, trim: true, uppercase: true, required: true, unique: true, length: 20},
    coordX: Number,
    coordY: Number,
    location: String,
    type: String,
    use: String,
    surface: Number,
    yoc: Number,
    userCounter: { type: Number, default: 0 }
}, {timestamps: true});

PropertySchema.plugin(uniqueValidator, {message: 'La referencia ya existe'});

PropertySchema.methods.updateUserCount = function() {
    var property = this;
  
    return User.count({properties: {$in: [property._id]}}).then(function(count){
      property.userCounter = count;
      return property.save();
    });
};

PropertySchema.methods.toJSONFor = function(){
    return {
        rc: this.rc,
        coordX: this.coordX,
        coordY: this.coordY,
        location: this.location,
        type: this.type,
        use: this.use,
        surface: this.surface,
        yoc: this.yoc,
        createdAt: this.createdAt,
        updatedAt: this.updatedAt,
        userCounter: this.userCounter
    };
};

PropertySchema.methods.toGeoJSONFor = function(){
    return {
        type: 'Feature',
        geometry: {
            type: 'Point',
            coordinates: [this.coordX, this.coordY]
        },
        properties: {
            rc: this.rc,
            location: this.location,
            type: this.type,
            use: this.use,
            surface: this.surface,
            yoc: this.yoc
        }
    };
};

mongoose.model('Property', PropertySchema);