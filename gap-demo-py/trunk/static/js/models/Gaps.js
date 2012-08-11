define(['models/Gap'], function(Gap) {

    var Gaps = Backbone.Collection.extend({
        model: Gap,
        url: '/resource/gaps',

        parse: function(response) {
            return response.gaps;
        }
    });

    return Gaps;
});
