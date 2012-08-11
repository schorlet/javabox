define(['models/events'], function(events) {

    var Filters = Backbone.View.extend({
        events: {
            'change select': 'onChange',
            'keyup input': 'onChange'
        },

        onChange: function(event) {
            var target = event.target;
            var th = $(target).parent('th');
            events.trigger('filter:change', $(target).val(), $(th).index());
        }
    });

    return Filters;
});
