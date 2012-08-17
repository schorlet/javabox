define(function() {

    var layout1 = function(place) {
        if (place == 'route1') return 'views/View1';
        else if (place == 'route2') return 'views/View2';
        else if (place == 'route3') return 'views/View3';
        return null;
    };

    var layout2 = function(place) {
        if (place == 'route1') return 'views/View2';
        else if (place == 'route2') return 'views/View3';
        return null;
    };

    var layout3 = function(place) {
        if (place == 'route1') return 'views/View3';
        else if (place == 'activity_list') return 'views/ActivityList';
        else if (place == 'activity_display') return 'views/ActivityDisplay';
        else if (place == 'activity_edit') return 'views/ActivityEdit';
        return null;
    };

    return {
        layout1: layout1,
        layout2: layout2,
        layout3: layout3
    };
});
