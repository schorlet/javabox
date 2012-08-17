define(['models/Activity', 'text!templates/activity_edit.jst', 'h5validate'],
function(Activity, edit_jst) {

    var Page = Backbone.View.extend({
        events: {
            'submit #edit_activity': 'onSubmit',
            'reset #edit_activity': 'onReset'
        },

        initialize: function(options) {
            this.js_template = _.template(edit_jst);
            var model_id = options.args;

            this.model = new Activity({ id: model_id });
            this.model.on('change', this.reset, this);
        },

        render: function() {
            if (!this.model.id) {
                this.reset();

            } else {
                this.model.fetch();
            }

            return this;
        },

        reset: function() {
            this.$el.html(this.js_template(this.model.toJSON()));

            this.form = this.$('form#edit_activity');
            this.form.h5Validate({
                debug: false
            });
        },

        onSubmit: function(event) {
            event.preventDefault();

            if (this.form.h5Validate('allValid')) {
                var serializeArray = this.form.serializeArray();

                var attributes = {};
                _.each(serializeArray, function(field) {
                    attributes[field.name] = field.value;
                });
                if (this.model.id) {
                    attributes['id'] = this.model.id;
                }

                var options = {
                    success: function(model, response) {
                        Backbone.history.navigate('activity_list', {trigger: true});
                    }
                };

                this.model.save(attributes, options);
            }
        },

        onReset: function(event) {
            Backbone.history.navigate('activity_display/' + (this.model.id || ''),
                {trigger: true});
        }
    });

    return Page;
});
