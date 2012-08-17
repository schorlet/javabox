define(['models/events', 'jquery', 'underscore', 'backbone'], function(events) {

    Backbone.View.prototype.remove = function() {
        console.log('remove: ', this.cid);

        this.undelegateEvents();
        if (this.collection) this.collection.off(null, null, this);
        if (this.model) this.model.off(null, null, this);
        events.off(null, null, this);

        if (this.views) {
            _.each(this.views, function(view) {
                view.remove();
            });
        }

        this.$el.empty();
    };

    var Router = Backbone.Router.extend({
        routes: {
            '': 'showIndex',
            'gaps': 'showGaps',
            'activities': 'showActivities'
        },

        initialize: function(options) {
            this.view = null;
        },

        showIndex: function() {
            this.loadModule('main_index');
        },

        showGaps: function() {
            this.loadModule('main_gaps');
        },

        showActivities: function() {
            this.loadModule('main_activities');
        },

        loadModule: function(module) {
            var place = 'wrapper1';
            console.log('loadModule:', module, ' at ', place);

            if (this.view) {
                this.view.remove();
                this.view = null;
            }

            require([module], _.bind(function(View) {
                var view = new View({
                    el: '#' + place
                }).render();
                this.view = view;
                return view;
            }, this));
        }
    });

    var initialize = function() {
        new Router();
        Backbone.history.start();
    };

    return {
        initialize: initialize
    };
});
