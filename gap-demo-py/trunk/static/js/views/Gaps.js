define(['models/events'], function(events) {

    var Gaps = Backbone.View.extend({
        events: {
            'click a.edit-gap': 'onEdit',
            'click span.ui-icon-trash': 'onDelete',
            'draw': 'onDraw'
        },

        initialize: function(options) {
            this.datatable = this.$el;
            this.versions = options.versions;

            this.collection.on('reset', this.resetGaps, this);
            this.collection.on('destroy', this.destroyGap, this);
            this.collection.on('change', this.changeGap, this);
            this.collection.on('add', this.addGap, this);

            events.on('filter:change', this.onFilter, this);
        },

        // event click on a.edit-gap
        onEdit: function(event) {
            event.preventDefault();

            var target = event.target;
            var tr = $(target).parents('tr').get(0);

            var tr_data = this.datatable.fnGetData(tr);
            var tr_index = this.datatable.fnGetPosition(tr);

            events.trigger('dialog:edit', tr_data, tr_index);
        },

        // event click on span.ui-icon-trash
        onDelete: function(event) {
            var target = event.target;
            var tr = $(target).parents('tr').get(0);

            var tr_data = this.datatable.fnGetData(tr);
            var tr_index = this.datatable.fnGetPosition(tr);

            var model = this.collection.get(tr_data.id);
            if (confirm('Delete gap ' + model.id + ' ?')) {
                model.destroy({
                    'tr_index': tr_index
                });
            }
        },

        // event draw on datatable
        onDraw: function() {
            // editable_text
            this.datatable.$('label.editable_text').editable(this.onEditable, {
                type: 'text',
                thisview: this,
                cssclass: 'input_editable',
                placeholder: '&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;',
                tooltip: 'Click to edit'
            });

            // editable_select
            this.datatable.$('label.editable_select').editable(this.onEditable, {
                data: this._versions.bind(this),
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

        // transform [{version:1}, {version:2}, ...] to {1:1, 2:2, ...}
        _versions: function() {
            var obj = {};
            this.versions.each(function(model) {
                var version = model.get('version');
                obj[version] = version;
            });
            return obj;
        },

        // editable cell handler
        onEditable: function(newValue, settings) {
            var td = $(this).parent('td').get(0);

            var thisview = settings.thisview;
            var datatable = thisview.datatable;
            var aPos = datatable.fnGetPosition(td);

            // aPos[0] -> rowid
            var tr_data = datatable.fnGetData(aPos[0]);
            // aPos[1] -> colid
            var td_prop = datatable.dataTableSettings[0].aoColumns[aPos[1]].mDataProp;

            // save the gap
            var gap = thisview.collection.get(tr_data.id);
            var attributes = {};
            attributes[td_prop] = newValue;
            gap.save(attributes, {
                wait: true,
                tr_index: aPos[0]
            });
            return newValue;
        },

        // events.on('filter:change')
        onFilter: function(val, th_index) {
            this.datatable.fnFilter(val, th_index);
        },

        // collection.on('add')
        addGap: function(model, collection) {
            this.datatable.fnAddData(model.toJSON());
            this.datatable.fnDraw();
        },

        // collection.on('destroy')
        destroyGap: function(model, collection, options) {
            this.datatable.fnDeleteRow(options.tr_index);
        },

        // collection.on('change')
        changeGap: function(model, options) {
            if (_.has(options, 'tr_index')) {
                this.datatable.fnUpdate(model.toJSON(), options['tr_index'], 0, true);
                var tr = this.datatable.fnGetNodes(options['tr_index']);
                $(tr).effect('highlight', 1000);
            } else {
                this.datatable.fnAddData(model.toJSON());
            }
            this.datatable.fnDraw();
        },

        // collection.on('reset')
        resetGaps: function() {
            this.datatable.fnClearTable(false);
            this.datatable.fnAddData(this.collection.toJSON());

            // adapt datatable body height
            var gaps_wrapper = $('#gaps_wrapper');
            var wrapper_height = gaps_wrapper.height();
            var wrapper_top = gaps_wrapper.position().top;

            $('.dataTables_scrollBody').height(wrapper_height - wrapper_top - 100);
            this.datatable.fnDraw();
        }
    });

    return Gaps;
});
