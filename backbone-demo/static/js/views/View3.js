define(['text!templates/view3.jst'], function(view3_jst) {

    var View2 = Backbone.View.extend({
        initialize: function(options) {
            this.js_template = _.template(view3_jst);
        },

        render: function() {
            this.$el.html(this.js_template());
            return this;
        }
    });

    return View2;
});
