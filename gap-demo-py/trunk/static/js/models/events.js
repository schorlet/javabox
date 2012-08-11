define(['underscore', 'backbone'], function(_, Backbone) {
    var events = {};
    _.extend(events, Backbone.Events);
    return events;
});
