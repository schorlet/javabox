define([
// models
'models/Gaps2', 'models/Versions', 'models/Users',
// views
'views/Activities', 'views/Selection', 'views/Versions', 'views/Users',
// template
'text!templates/activities.jst',
// plugins
'jqueryui', 'datatable', 'fixedcolumns', 'jeditable', 'moment', 'activities_pagination'],
        function(Gaps, Versions, Users, ActivitiesView, SelectionView, VersionView, UserView,
                activities_jst) {

            var Page = Backbone.View.extend({
                initialize: function(options) {
                    this.js_template = _.template(activities_jst);

                    this.gaps = new Gaps();
                    this.versions = new Versions();
                    this.users = new Users();
                },

                render: function() {
                    this.$el.html(this.js_template());
                    _.defer(this.postRender.bind(this));
                    return this;
                },

                postRender: function() {
                    this.createViews();

                    this.versions.fetch();
                    this.users.fetch();
                },

                createViews: function() {
                    this.views = [new VersionView({
                        'el': '#versions_select',
                        'collection': this.versions
                    }), new UserView({
                        'el': '#users_select',
                        'collection': this.users
                    }), new SelectionView({
                        'el': '#selection_view',
                        'collection': this.gaps
                    }), new ActivitiesView({
                        'el': '#gaps_table',
                        'collection': this.gaps
                    })];
                }
            });

            return Page;
        });
