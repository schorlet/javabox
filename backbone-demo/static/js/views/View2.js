define(['text!templates/view2.jst'], function(view2_jst) {

    var View2 = Backbone.View.extend({
        initialize: function(options) {
            this.js_template = _.template(view2_jst);
        },

        render: function() {
            this.$el.html(this.js_template());
            return this;
        }
    });

    return View2;
});
