define(['models/Activities', 'text!templates/activity_list.jst'],
function(Activities, list_jst) {

    var Page = Backbone.View.extend({
        initialize: function(options) {
            this.js_template = _.template(list_jst);

            this.collection = new Activities();
            this.collection.comparator = function(activity) {
                return activity.get("version");
            };

            this.collection.on('reset', this.reset, this);
        },

        render: function() {
            this.collection.fetch();
            return this;
        },

        reset: function() {
            this.$el.html(this.js_template({
                activities: this.collection.toJSON()
                })
            );
        }
    });

    return Page;
});
