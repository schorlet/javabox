define(['models/events', 'jquery', 'underscore', 'backbone'], function(events) {

    Backbone.View.prototype.remove = function() {
        console.log('remove: ', this.cid);

        this.undelegateEvents();
        if (this.collection)
            this.collection.off(null, null, this);
        if (this.model)
            this.model.off(null, null, this);
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
            this.places = {};
            _.bindAll(this);
        },

        showIndex: function() {
            this.loadModule('main_index', 'wrapper1');
        },

        showGaps: function() {
            this.loadModule('main_gaps', 'wrapper1');
        },

        showActivities: function() {
            this.loadModule('main_activities', 'wrapper1');
        },

        loadModule: function(module, place) {
            console.log('loadModule:', module, ' at ', place);

            if (this.places[place]) {
                this.places[place].remove();
                delete this.places[place];
            }

            this._place = place;
            require([module], this.createView);
        },

        createView: function(View) {
            var view = new View({
                el: '#' + this._place
            }).render();
            this.places[this._place] = view;
            return view;
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
