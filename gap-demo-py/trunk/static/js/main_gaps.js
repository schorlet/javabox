define([
// models
'models/events', 'models/Gaps', 'models/Versions',
// views
'views/Gaps', 'views/GapForm', 'views/GapDialog', 'views/Versions', 'views/Filters',
// template
'text!templates/gaps.jst',
// plugins
'jqueryui', 'datatable', 'jeditable', 'h5validate'], function(events, Gaps, Versions, GapView,
        GapFormView, GapDialogView, VersionView, FilterView, gaps_jst) {

    var Page = Backbone.View.extend({
        initialize: function(options) {
            this.js_template = _.template(gaps_jst);

            this.gaps = new Gaps();
            this.versions = new Versions();
        },

        render: function() {
            this.$el.html(this.js_template());
            _.defer(this.postRender.bind(this));
            return this;
        },

        postRender: function() {
            this.createDatatable();
            this.createViews();
            this.createButtons();

            this.gaps.fetch();
            this.versions.fetch();
        },

        createDatatable: function() {
            this.datatable = $('#gaps_table').dataTable(
                    {
                        aaSorting: [[2, 'asc']],
                        aoColumnDefs: [
                            {
                                aTargets: [0],
                                bSortable: false,
                                bSearchable: false,
                                mDataProp: null,
                                sDefaultContent: '<span class="ui-icon ui-icon-trash"></span>'
                            },
                            {
                                aTargets: [1],
                                mDataProp: 'id',
                                bUseRendered: false,
                                fnRender: function(oObj, sVal) {
                                    return '<a class="edit-gap" href="' + oObj.aData.link + '">'
                                            + sVal + '</a>';
                                },
                            }, {
                                aTargets: [2],
                                mDataProp: 'version',
                                aDataSort: [2, 1],
                                bUseRendered: false,
                                fnRender: function(oObj, sVal) {
                                    return '<label class="editable_select">' + sVal + '</label>';
                                }
                            }, {
                                aTargets: [3],
                                mDataProp: 'description',
                                aDataSort: [3, 2],
                                bUseRendered: false,
                                fnRender: function(oObj, sVal) {
                                    return '<label class="editable_text">' + sVal + '</label>';
                                }
                            }],
                        bJQueryUI: true,
                        bAutoWidth: false,
                        bDeferRender: true,
                        bFilter: true,
                        bInfo: true,
                        bLengthChange: true,
                        bProcessing: true,
                        bPaginate: true,
                        bRetrieve: true,
                        bScrollAutoCss: true,
                        bScrollCollapse: false,
                        bServerSide: false,
                        bSort: true,
                        fnCreatedRow: function(nRow, aData, iDataIndex) {
                            $(nRow).data('gapid', aData.id);
                            $(nRow).css('height', '21px');
                        },
                        iDisplayLength: 25,
                        iScrollLoadGap: 0,
                        sPaginationType: 'two_button',
                        sScrollY: '1'
                    });
        },

        createViews: function() {
            this.views = [new VersionView({
                'el': '#versions_select',
                'collection': this.versions
            }), new GapDialogView({
                'el': '#gap_dialog',
            }), new GapFormView({
                'el': '#gap_form',
                'collection': this.gaps
            }), new GapView({
                'el': this.datatable,
                'collection': this.gaps,
                'versions': this.versions
            }), new FilterView({
                'el': 'div.dataTables_scrollFootInner table.dataTable tfoot'
            })];

            $('div#gaps_table_filter.dataTables_filter label input').attr('placeholder',
                    'Search all');
            $('div.dataTables_scroll div.dataTables_scrollHead').height('30px');

        },

        createButtons: function() {
            $('#reset-button').button().click(this.onReset);
            $('#reload-button').button().click(this.onReload.bind(this));
            $('#create-gap-button').button().click(this.onCreateGap);
        },

        // #reset-button
        onReset: function() {
            $.ajax({
                url: '/resource/gaps',
                type: 'delete'
            }).done(function() {
                $('#reload-button').trigger('click');
            });
        },

        // #reload-button
        onReload: function() {
            this.versions.fetch();
            this.gaps.fetch();
        },

        // #create-gap-button
        onCreateGap: function() {
            events.trigger('dialog:create');
        }

    });

    return Page;
});
