define(['models/activityStore'], function(activityStore) {

    var Activity = Backbone.Model.extend({
        localStorage: activityStore,

        defaults: {
            version: '',
            date: '',
            description: ''
        },

        urlRoot: '/resource/activity',
        url: function() {
            var id = this.get('id');
            if (id)
                return this.urlRoot + '/' + id;
            else
                return this.urlRoot;
        }
    });

    return Activity;
});
