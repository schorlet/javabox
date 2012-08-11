define(function() {

    var Users = Backbone.Collection.extend({
        url: '/resource/users',
        parse: function(response) {
            return response.users;
        }
    });

    return Users;
});
