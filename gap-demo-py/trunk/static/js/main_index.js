define(['text!templates/index.jst'], function(index_jst) {
    var Page = Backbone.View.extend({
        events: {
            'click a[class=delete]': 'onClick'
        },

        initialize: function(options) {
            this.js_template = _.template(index_jst);
        },

        render: function() {
            this.$el.html(this.js_template());
            return this;
        },

        onClick: function(event) {
            event.preventDefault();
            var url = $(event.target).attr('href');
            $.ajax({
                type: 'delete',
                url: url
            });
        }
    });

    return Page;
});
