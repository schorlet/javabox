define(['models/Gap'], function(Gap) {

    var Gaps2 = Backbone.Collection.extend({
        model: Gap,

        url: function() {
            var resource = '/resource/gaps/' + this.version + '/' + this.user;
            if (this.from_day) {
                resource += '/' + this.from_day;
                if (this.to_day)
                    resource += '/' + this.to_day;
            }
            return resource;
        },

        fetch: function(options) {
            if (this.user && this.version) {
                Backbone.Collection.prototype.fetch.call(this, options);
            }
        },

        parse: function(response) {
            return response.gaps;
        },

        user: null,
        version: null,
        from_day: null,
        to_day: null
    });

    return Gaps2;
});
