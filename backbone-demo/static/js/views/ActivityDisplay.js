define(['models/Activity', 'text!templates/activity_display.jst'],
function(Activity, display_jst) {

    var Page = Backbone.View.extend({
        events: {
            'click #delete': 'onDelete',
            'click #edit': 'onEdit'
        },

        initialize: function(options) {
            this.js_template = _.template(display_jst);
            var model_id = options.args;

            this.model = new Activity({ id: model_id });
            this.model.on('change', this.reset, this);
        },

        render: function() {
            if (!this.model.id) {
                this.reset();
                this.$('#delete').hide();

            } else {
                this.model.fetch();
            }

            return this;
        },

        reset: function() {
            this.$el.html(this.js_template(this.model.toJSON()));
        },

        onDelete: function(event) {
            if (this.model.id && confirm('delete ?')) {

                var options = {
                    success: function(model, response) {
                        Backbone.history.navigate('activity_list', {trigger: true});
                    }
                };

                this.model.destroy(options);
            }
        },

        onEdit: function(event) {
            Backbone.history.navigate('activity_edit/' + (this.model.id || ''),
                {trigger: true});
        }
    });

    return Page;
});
