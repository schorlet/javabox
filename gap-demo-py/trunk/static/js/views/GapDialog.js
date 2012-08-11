define(['models/events'], function(events) {

    var GapDialog = Backbone.View.extend({
        initialize: function(options) {
            events.on('dialog:create', this.onOpen, this);
            events.on('dialog:edit', this.onOpen, this);
            events.on('dialog:close', this.onClose, this);

            this.dialog = this.$el.dialog({
                autoOpen: false,
                buttons: {
                    'Cancel': function() {
                        events.trigger('dialog:close');
                    },
                    'Save': function() {
                        events.trigger('dialog:submit');
                    }
                },
                width: 340,
                height: 280,
                modal: true
            });
        },

        remove: function() {
            Backbone.View.prototype.remove.call(this);
            // remove the jquery dialog
            this.$el.remove();
        },

        // events.on('dialog:create'), events.on('dialog:edit')
        onOpen: function() {
            this.dialog.dialog('open');
        },

        // events.on('dialog:close')
        onClose: function() {
            this.dialog.dialog('close');
        }
    });

    return GapDialog;
});
