define(function() {

    var Selection = Backbone.View.extend({
        initialize: function(options) {
            this.versions = this.$('#versions_select');
            this.users = this.$('#users_select');

            this.versions.on('change', this.onVersionChange.bind(this));
            this.users.on('change', this.onUserChange.bind(this));
        },

        onVersionChange: function() {
            this.collection.version = this.versions.val();
            this.collection.fetch();
        },

        onUserChange: function() {
            this.collection.user = this.users.val();
            this.collection.fetch();
        },

        remove: function() {
            this.versions.off('change', this.onVersionChange);
            this.users.off('change', this.onUserChange);
            Backbone.View.prototype.remove.call(this);
        }
    });

    return Selection;
});
