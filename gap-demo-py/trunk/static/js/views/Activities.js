define(['models/Activity', 'text!templates/activities_datatable.jst'], function(Activity,
        activities_jst) {

    var Activities = Backbone.View.extend({
        events: {
            'draw': 'onDraw'
        },

        initialize: function(options) {
            this.js_template = _.template(activities_jst);

            this.collection.on('reset', this.resetGaps, this);
            this.initializeDays();
        },

        initializeDays: function() {
            var day = new Date(Date.now());
            if (0 < day.getDay() && day.getDay() <= 6) {
                this.to_day = 5 - day.getDay();
            } else if (0 == day.getDay()) {
                this.to_day = -2;
            }
            this.from_day = this.to_day - 11;
        },

        // event draw on datatable
        onDraw: function(oSettings) {
            $('label.editable_select', this.el).editable(this.onEditable, {
                data: {
                    '0': '',
                    '0.1': '0.1',
                    '0.2': '0.2',
                    '0.3': '0.3',
                    '0.4': '0.4',
                    '0.5': '0.5',
                    '0.6': '0.6',
                    '0.7': '0.7',
                    '0.8': '0.8',
                    '0.9': '0.9',
                    '1': '1'
                },
                type: 'select',
                thisview: this,
                submit: 'ok',
                width: '1%',
                height: '12px',
                cssclass: 'select_editable',
                placeholder: '&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;',
                tooltip: 'Click to edit'
            });
        },

        // editable cell handler
        onEditable: function(newValue, settings) {
            var thisview = settings.thisview;
            var datatable = thisview.datatable;

            var td = $(this).parent('td').get(0);
            var aPos = datatable.fnGetPosition(td);

            var tr = $(td).parent('tr').get(0);
            var th = $(tr).parents('table').find('th').eq(aPos[1]);

            var activity_id = $(this).attr('activity_id');

            // activity.save(attributes, options)
            var attributes = {};

            if (activity_id) {
                // update or delete
                attributes = {
                    'id': activity_id,
                    'time': newValue
                };
            } else {
                // create
                attributes = {
                    'time': newValue,
                    'day': th.attr('day'),
                    'gap_id': $(tr).attr('gap_id'),
                    'user': thisview.collection.user
                };
            }

            // activity.save(attributes, options)
            var options = {
                wait: true,
                success: function(model, response) {
                    var label = '<label class="editable_select"></label>';

                    if (model.has('time')) {
                        label = '<label class="editable_select" activity_id="%id%">%time%</label>';
                        label = thisview._replaceString(label, {
                            id: model.get('id'),
                            time: model.get('time')
                        });
                    }

                    datatable.fnUpdate(label, aPos[0], aPos[2], false);
                    datatable.fnDraw();

                    $(td).effect('highlight', 1000);
                    $(tr).removeClass('empty');
                }
            };

            activity = new Activity();
            if (newValue > 0) {
                // create or update
                activity.save(attributes, options);
            } else {
                // delete
                activity.set('id', activity_id);
                activity.destroy(options);
            }
        },

        // _replaceString
        _replaceString: function(template, data) {
            return template.replace(/%(\w+)%/g, function(str, p1) {
                return data.hasOwnProperty(p1) ? data[p1] : "";
            });
        },

        // pagination handler
        onPagination: function(oSettings, action) {
            if (action == 'previous')
                this.to_day -= 14;
            else
                this.to_day += 14;
            this.from_day = this.to_day - 11;

            this.collection.from_day = this.from_day;
            this.collection.to_day = this.to_day;
            this.collection.fetch();
        },

        // collection.on('reset')
        resetGaps: function() {
            this._destroyDatatable();

            // from [day] to [day]
            var moment0 = moment().add('days', this.from_day);
            var moment1 = moment().add('days', this.to_day);
            $('#date_range').text('from ' + moment0.format('LL') + ' to ' + moment1.format('LL'));

            // days = [{title, day, dayofweek}, ...]
            var days = [];
            while (moment1.diff(moment0, 'days') >= 0) {
                if (0 < moment0.day() && moment0.day() < 6) {
                    days.push({
                        'title': moment0.format('MMM-DD'),
                        'day': moment0.format('YYYY-MM-DD'),
                        'dayofweek': moment0.day()
                    });
                }
                moment0.add('days', 1);
            }

            // replace dom content with the template
            this.$el.html(this.js_template({
                days: days,
                gaps: this.collection.toJSON()
            }));

            // create datatable
            this.datatable = this._createDatatable();

            // adapt datatable body height
            var wrapper_height = $('#gaps_wrapper').height();
            var wrapper_top = $('#gaps_wrapper').position().top;
            $('.dataTables_scrollBody').height(wrapper_height - wrapper_top);

            // ~ new FixedColumns(this.datatable, {
            // ~ iLeftColumns : 1,
            // ~ iLeftWidth : 80,
            // ~ sLeftWidth : 'fixed',
            // ~ sHeightMatch: 'auto'
            // ~ });

            return this;
        },

        // _destroyDatatable
        _destroyDatatable: function() {
            // destroy datatable
            if (this.datatable) {
                this.datatable.fnDestroy();
                this.datatable = null;
            }
            this.$el.empty();
        },

        // _createDatatable
        _createDatatable: function() {
            var dataTable = this.$el.dataTable({
                aoColumnDefs: [{
                    bSortable: false,
                    aTargets: ['_all']
                }],
                aaSortingFixed: [[1, 'asc'], [0, 'asc']],
                bJQueryUI: true,
                bAutoWidth: true,
                bDestroy: true,
                bFilter: false,
                bInfo: true,
                bLengthChange: false,
                bPaginate: true,
                bRetrieve: false,
                bScrollAutoCss: true,
                bScrollCollapse: false,
                bSort: true,
                iDisplayLength: 25,
                iDisplayStart: 25,
                iScrollLoadGap: 0,
                sPaginationType: 'two_button_activities',
                sScrollX: '90%',
                sScrollY: '1'
            });

            dataTable.fnSettings().oApi._fnPageChange = this.onPagination.bind(this);
            return dataTable;
        }

    });

    return Activities;
});
