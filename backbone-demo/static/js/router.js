define(['models/layouts', 'jquery', 'underscore', 'backbone'],
function(layouts) {

    Backbone.View.prototype.remove = function() {
        console.log(this.el.id, ': null');
        this.undelegateEvents();
        if (this.collection) this.collection.off(null, null, this);
        if (this.model) this.model.off(null, null, this);
        this.$el.empty();
    };

    var Router = Backbone.Router.extend({
        routes: {
            '': 'showMenu',
        },
        initialize: function(options) {
            this.views = {};
            this.route(/route(\d)(\/\S*)?/, 'showRouteNumber');

            this.route(/activity_list/, 'activityList');
            this.route(/activity_display(\/\S+)?/, 'activityDisplay');
            this.route(/activity_edit(\/\S+)?/, 'activityEdit');
        },

        // default
        showMenu: function() {
            this.showRoute('menu');
        },
        // route[n]
        showRouteNumber: function(route_num, args) {
            if (args) args = args.substring(1);
            this.showRoute('route' + route_num, args);
        },

        // activity_list
        activityList: function() {
            this.showRoute('activity_list');
        },
        // activity_display
        activityDisplay: function(args) {
            if (args) args = args.substring(1);
            this.showRoute('activity_display', args);
        },
        // activity_edit
        activityEdit: function(args) {
            if (args) args = args.substring(1);
            this.showRoute('activity_edit', args);
        },

        showRoute: function(route, args) {
            console.info(route, '(', args, ')');
            var layout_names = _.functions(layouts);

            _.each(layout_names, function(layout_name) {
                var layout_function = layouts[layout_name];
                var view_path = layout_function(route);
                this.fillLayout(layout_name, view_path, args);
            }, this);
        },

        fillLayout: function(layout_name, view_path, route_args) {
            var view = this.views[layout_name];

            if (view_path && view && view.path == view_path) {
                return;
            } else if (view) {
                view.obj.remove();
                delete this.views[layout_name];
            }

            if (view_path) {
                console.log(layout_name, ': ', view_path);

                require([view_path], _.bind(function(View) {
                        var view_obj = new View({
                            el: '#' + layout_name,
                            args: route_args
                        }).render();

                        this.views[layout_name] = {
                            'path': view_path,
                            'obj': view_obj
                        };

                        return view_obj;
                    }, this)
                );
            }
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
