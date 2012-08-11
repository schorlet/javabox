require.config({
    paths: {
        jquery: 'libs/jquery/1.7.2/jquery.min',
        jqueryui: 'libs/jqueryui/1.8.22/jquery-ui.custom.min',
        underscore: 'libs/underscore/1.3.3/underscore.min',
        backbone: 'libs/backbone/0.9.2/backbone.min',
        text: 'libs/require/2.0.4/text',
        ready: 'libs/require/2.0.4/domReady',
        // --
        datatable: 'libs/datatables/1.9.0/jquery.dataTables.min',
        fixedcolumns: 'libs/datatables/1.9.0/jquery.dataTables.FixedColumns.min',
        jeditable: 'libs/jeditable/1.7.1/jquery.jeditable',
        h5validate: 'libs/h5validate/0.8.2/jquery.h5validate',
        moment: 'libs/moment/1.7.0/moment.min',
        // --
        activities_pagination: 'libs/activities.pagination'
    },
    shim: {
        jquery: {
            exports: '$'
        },
        underscore: {
            exports: '_'
        },
        backbone: {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        },
        jqueryui: ['jquery'],
        datatable: ['jqueryui'],
        fixedcolumns: ['datatable'],
        activities_pagination: ['datatable']
    },
    deps: ['jquery', 'underscore', 'backbone'],
    urlArgs: "bust=" + (new Date()).getTime()
});

require(['app'], function(app) {
    app.initialize();
});
