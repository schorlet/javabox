var AppActivity = {
	oTable : null,

	/**
	 * replace_str
	 */
	replace_str: function(template, data) {
		return template.replace(/%(\w+)%/g, function(str, p1) {
			return data.hasOwnProperty(p1) ? data[p1] : "";
		});
	},

	/**
	 * format_date
	 */
	format_date: function(day) {
		var month = day.getMonth() + 1;
		month = month < 10 ? '0' + month : month;
		var date = day.getDate();
		date = date < 10 ? '0' + date : date;
		return day.getFullYear() + "-" + month + "-" + date;
	},

	/**
	 * refresh_date_range
	 */
	refresh_date_range: function() {
		var values = $('#date_slider').slider("option", "values");

		var fromDate = moment().add('days', values[0]);
		var toDate = moment().add('days', values[1]);

		$('#date_range').text('from ' + fromDate.format('LL') + ' to ' + toDate.format('LL'));
		return true;
	},

	/**
	 * init_users
	 */
	init_users: function() {
		var users_select = $('#users_select');
		$('option', users_select).remove();
		users_select.append($('<option value=""></option>'));

		$.ajax({
		   url : '/resource/gaps/users',
		   dataType : 'json'

		}).done(
		      function(data) {
			      if (data != null) {
				      $.each(data.users, function(index, user) {
					      users_select.append($('<option></option>').attr('value', user.user).text(
					            user.user));
				      });
			      }

		      }).fail(function(error) {
			console.error(error.statusText);
		});
	},

	/**
	 * init_versions
	 */
	init_versions: function() {
		var versions_select = $('#versions_select');
		$('option', versions_select).remove();
		versions_select.append($('<option value=""></option>'));

		$.ajax({
		   url : '/resource/gaps/versions',
		   dataType : 'json'

		}).done(
		      function(data) {
			      if (data != null) {
				      $.each(data.versions, function(index, version) {
					      versions_select.append($('<option></option>').attr('value', version.version)
					            .text(version.version));
				      });
			      }
			      ;

		      }).fail(function(error) {
			console.error(error.statusText);
		});
	},

	/**
	 * user_refresh
	 */
	user_refresh: function() {
		var start = Date.now();
		var user = $('#users_select option:selected').val();
		if (user == null || user == '')
			return;

		var version = $('#versions_select option:selected').val();
		version = (version == '') ? null : version;

		var values = $('#date_slider').slider("option", "values");

		var data = {
		   from : values[0],
		   to : values[1],
		};
		if (version != null) {
			data.version = version;
		}

		var dataType = $('input[type=radio][name=dataType]:checked').attr('value');

		if (dataType == 'json') {
			AppActivity.request_json({
			   url : '/resource/gaps/' + user,
			   dataType : 'json',
			   data : data
			}, start, values);
			
		} else if (dataType == 'html') {
			AppActivity.request_html({
			   url : '/resource/gaps/' + user + '/html',
			   dataType : 'html',
			   data : data
			}, start, values);
		}
	},

	/**
	 * request_json
	 */
	request_json: function(obj, start, values) {
		$.ajax(obj).done(function(data) {
			$('#request_ms').text(Date.now() - start);
			
			if (data != null) {
				AppActivity.template_gaps_json(values, data);
			} else {
				AppActivity.template_gaps_json(values, {
				   gaps : [],
				   activities : []
				});
			}

		}).fail(function(error) {
			$('#request_ms').text(Date.now() - start);
			console.error(error.statusText);

		}).always(function() {
			$('#time_ms').effect('highlight', 500);
		});
	},

	/**
	 * template_gaps_json
	 */
	template_gaps_json: function(values, aData) {
		var start = Date.now();
		var aDays = [];
		
		for ( var i = values[0]; i <= values[1]; i++) {
			var day = new Date(Date.now() + i * 86400000);
			if (0 < day.getDay() && day.getDay() < 6)
				aDays.push([ AppActivity.format_date(day), day.getDay() ]);
		}

		var jqote = $('#gaps-table-template').jqote({
		   days : aDays,
		   gaps : aData.gaps,
		   activities : aData.activities
		});

		if (AppActivity.oTable != null) {
			AppActivity.oTable.fnDestroy();
		}
		$('#gaps-table').replaceWith(jqote);

		$('#template_gaps_ms').text(Date.now() - start);
		AppActivity.create_datatable();
	},

	/**
	 * request_html
	 */
	request_html: function(obj, start, values) {
		$.ajax(obj).done(function(data) {
			$('#request_ms').text(Date.now() - start);
			if (data != null) {
				AppActivity.template_gaps_html(data);
			} else {
				AppActivity.template_gaps_json(values, {
				   gaps : [],
				   activities : []
				});
			}

		}).fail(function(error) {
			$('#request_ms').text(error.statusText);
			console.error(error.statusText);

		}).always(function() {
			$('#time_ms').effect('highlight', 500);
		});
	},

	/**
	 * template_gaps_html
	 */
	template_gaps_html: function(html) {
		var start = Date.now();
		if (AppActivity.oTable != null) {
			AppActivity.oTable.fnDestroy();
		}
		$('#gaps-table').replaceWith(html);

		$('#template_gaps_ms').text(Date.now() - start);
		this.create_datatable();
	},

	/**
	 * create_datatable
	 */
	create_datatable: function() {
		var start = Date.now();

		this.oTable = $('#gaps-table').dataTable({
		   aoColumnDefs : [ {
		      bSortable : false,
		      aTargets : [ '_all' ]
		   } ],
		   aaSortingFixed : [ [ 1, 'asc' ], [ 0, 'asc' ] ],
		   bJQueryUI : true,
		   bAutoWidth : false,
		   bDestroy : true,
		   bFilter : false,
		   bInfo : true,
		   bLengthChange : false,
		   bPaginate : false,
		   bRetrieve : false,
		   bScrollAutoCss : true,
		   bScrollCollapse : false,
		   bSort : true,
		   sScrollX : '100%',
		   sScrollY : '240'
		});

		this.make_datatable_editable();
		$('#create_datatable_ms').text(';' + (Date.now() - start));
		start = Date.now();

		new FixedColumns(this.oTable, {
		   iLeftColumns : 1,
		   iLeftWidth : 80,
		   sLeftWidth : 'fixed'
		});
		$('#create_datatable_ms').append(';' + (Date.now() - start));
	},

	/**
	 * edit_activity_cell
	 */
	edit_activity_cell: function(value) {
		var user = $('#users_select option:selected').val();
		if (user == null || user == '')
			return;

		var link = $(this).attr('activity');
		if (link == undefined && value == '0') {
			return null;
		}

		var td = $(this).parent('td').get(0);
		var aPos = AppActivity.oTable.fnGetPosition(td);

		var tr = $(td).parent('tr').get(0);
		var gap = $(tr).attr('gap');

		var th = $(tr).parents('table').find('th').eq(aPos[1]);
		var day = th.attr('day');

		var obj = {
		   username : user,
		   day : day,
		   time : value,
		   gap : gap
		};

		AppActivity.edit_activity_obj(link, obj).done(function(data) {
			console.debug(data);
			var label = '<label class="editable_select"></label>';

			if (data != null) {
				label = '<label class="editable_select" activity="%link%">%time%</label>';
				label = AppActivity.replace_str(label, {
				   link : data.link,
				   time : data.time
				});
			}

			AppActivity.oTable.fnUpdate(label, aPos[0], aPos[2], false);
			AppActivity.oTable.fnDraw();

			$(td).effect('highlight', 1000);
			$(tr).removeClass('empty');
		});
	},

	/**
	 * edit_activity_obj
	 */
	edit_activity_obj: function(link, obj) {
		console.debug('link:', link, obj);

		var method = 'post'; // create
		if (link == null || link == '')
			link = '/resource/activity';
		else
			method = 'put'; // update

		if (obj.time == '0')
			method = 'delete';

		return $.ajax({
		   type : method,
		   url : link,
		   data : obj,
		   dataType : 'json'

		}).fail(function(error) {
			console.error(error.statusText);
		});

	},

	/**
	 * make_datatable_editable
	 */
	make_datatable_editable: function() {
		this.oTable.on('draw', function() {
			$('label.editable_select', AppActivity.oTable).editable(AppActivity.edit_activity_cell, {
			   data : {
			      '0' : '',
			      '0.1' : '0.1',
			      '0.2' : '0.2',
			      '0.3' : '0.3',
			      '0.4' : '0.4',
			      '0.5' : '0.5',
			      '0.6' : '0.6',
			      '0.7' : '0.7',
			      '0.8' : '0.8',
			      '0.9' : '0.9',
			      '1' : '1'
			   },
			   type : 'select',
			   submit : 'ok',
			   width : '1%',
			   height : '12px',
			   cssclass : 'select_editable',
			   placeholder : '&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;',
			   tooltip : 'Click to edit'
			});
		});
	},
	
	/**
	 * init
	 */
	init : function() {
		$('#date_slider').slider({
		   animate : true,
		   range : true,
		   min : -60,
		   max : 10,
		   values : [ -5, 1 ],
		   slide : function(event, ui) {
		   	AppActivity.refresh_date_range();
		   },
		   change : function(event, ui) {
			   if (AppActivity.refresh_date_range()) {
			   	AppActivity.user_refresh();
			   }
		   }
		});

		$('#users_select, #versions_select').on('change', function() {
			AppActivity.user_refresh();
		});

		$('input[type=radio][name=dataType]').click(function() {
			AppActivity.user_refresh();
		});

		this.refresh_date_range();
		this.init_users();
		this.init_versions();
	}
};

/**
 * document ready
 */
$(document).ready(function() {
	AppActivity.init();
});
