define(function() {

    var Versions = Backbone.View.extend({
        initialize: function(options) {
            this.collection.on('reset', this.reset, this);
        },

        // collection.on('reset')
        reset: function() {
            var selected_option = $('option:selected', this.el).val();
            $('option', this.el).remove();

            this.$el.append($('<option></option>'));

            this.collection.each(function(model) {
                this.$el.append($('<option></option>').attr('value', model.get('version')).text(
                        model.get('version')));
            }, this);

            this.$el.val(selected_option);
        }
    });

    return Versions;
});
