define(function() {

    var Activity = Backbone.Model.extend({
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
