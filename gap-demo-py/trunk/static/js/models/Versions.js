define(function() {

    var Versions = Backbone.Collection.extend({
        url: '/resource/versions',
        parse: function(response) {
            return response.versions;
        }
    });

    return Versions;
});
