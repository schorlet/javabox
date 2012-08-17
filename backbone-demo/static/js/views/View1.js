define(['text!templates/view1.jst'], function(view1_jst) {

    var View1 = Backbone.View.extend({
        initialize: function(options) {
            this.js_template = _.template(view1_jst);
        },

        render: function() {
            this.$el.html(this.js_template());
            return this;
        }
    });

    return View1;
});
