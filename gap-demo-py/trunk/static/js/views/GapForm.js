define(['models/events'], function(events) {

    var GapForm = Backbone.View.extend({
        events: {
            'submit': 'onSubmit'
        },

        initialize: function(options) {
            events.on('dialog:create', this.onCreate, this);
            events.on('dialog:edit', this.onEdit, this);
            events.on('dialog:submit', this.onValidate, this);

            // gap form validation
            this.$el.h5Validate({
                click: true,
                debug: false
            });
        },

        // events.on('dialog:create')
        onCreate: function() {
            this.tr_index = null;
            this.$('#version, #description').removeClass('ui-state-error');
            this.$('#gapid, #version, #description').val('');
        },

        // events.on('dialog:edit')
        onEdit: function(gap, tr_index) {
            this.tr_index = tr_index;
            this.$('#version, #description').removeClass('ui-state-error');
            this.$('#gapid').val(gap.id);
            this.$('#version').val(gap.version);
            this.$('#description').val(gap.description);
        },

        // events.on('dialog:submit')
        onValidate: function(event) {
            this.$el.submit();
        },

        // submit form
        onSubmit: function(event) {
            event.preventDefault();

            if (this.$el.h5Validate('allValid')) {
                var gapid = this.$('#gapid').val();
                var gap = this.collection.get(gapid);

                var attributes = {
                    'version': this.$('#version').val(),
                    'description': this.$('#description').val()
                };

                var options = {
                    wait: true,
                    tr_index: this.tr_index,
                    success: function() {
                        events.trigger('dialog:close');
                    }
                };

                if (gap !== undefined) {
                    gap.save(attributes, options);
                } else {
                    this.collection.create(attributes, options);
                }
            }
        }
    });

    return GapForm;
});
