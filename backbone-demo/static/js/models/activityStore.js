define(['localstorage'], function() {
    var storage = new Backbone.LocalStorage("Activity");
    return storage;
});
