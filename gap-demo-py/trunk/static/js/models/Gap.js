define(function() {

    var Gap = Backbone.Model.extend({
        activities: [],
        urlRoot: '/resource/gap',

        initialize: function(attributes) {
            if (attributes.activities) {
                this.activities = attributes.activities;
            }
        },

        url: function() {
            var link = this.get('link');
            return link || this.urlRoot;
        }
    });

    return Gap;
});
