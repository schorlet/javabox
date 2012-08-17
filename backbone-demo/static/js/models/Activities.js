define(['models/Activity', 'models/activityStore'],
function(Activity, activityStore) {

    var Activities = Backbone.Collection.extend({
        model: Activity,
        localStorage: activityStore,
        url: '/resource/activities'
    });

    return Activities;
});
