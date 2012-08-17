require.config({
    paths: {
        jquery: 'libs/jquery/1.7.2/jquery.min',
        underscore: 'libs/underscore/1.3.3/underscore.min',
        backbone: 'libs/backbone/0.9.2/backbone.min',
        localstorage: 'libs/backbone/0.9.2/backbone.localStorage-min',
        text: 'libs/require/2.0.4/text',
        h5validate: 'libs/h5validate/0.8.2/jquery.h5validate',
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
        }
    },
    deps: ['jquery', 'underscore', 'backbone'],
    urlArgs: "bust=" + (new Date()).getTime()
});

require(['app'], function(app) {
    app.initialize();
});
