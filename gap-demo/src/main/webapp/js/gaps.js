var AppGaps = {
   gapsTable : null,
   versions : [],

   /**
	 * init_versions
	 */
   init_versions : function() {
	   AppGaps.versions = [];

	   $.ajax({
	      url : '/resource/gaps/versions',
	      dataType : 'json'

	   }).done(function(data) {
		   if (data != null) {
			   $.each(data.versions, function(index, version) {
				   AppGaps.versions.push(version.version);
			   });
		   }
		   AppGaps.refresh_versions();

	   }).fail(function(error) {
		   console.error(error.statusText);
	   });
   },

   /**
    * refresh_versions
    */
   refresh_versions : function() {
	   var versions_select = $('#versions_select');
	   AppGaps.versions.sort();

	   $('option', versions_select).remove();
	   versions_select.append($('<option value=""></option>'));

	   $.each(AppGaps.versions, function(index, version) {
		   versions_select.append($('<option></option>').attr('value', version).text(version));
	   });
   },

   /**
	 * init_gaps_table
	 */
   init_gaps_table : function() {
	   this.gapsTable = $('#gaps-table').dataTable({
	      aaSorting : [ [ 2, 'asc' ] ],
	      aoColumnDefs : [ {
	         aTargets : [ 0 ],
	         bSortable : false,
	         bSearchable : false,
	         mDataProp : null,
	         sDefaultContent : '<span class="ui-icon ui-icon-trash"></span>'
	      }, {
	         aTargets : [ 1 ],
	         mDataProp : 'id',
	         bUseRendered : false,
	         fnRender : function(oObj, sVal) {
		         return '<a class="edit-gap" href="' + oObj.aData.link + '">' + sVal + '</a>';
	         },
	      }, {
	         aTargets : [ 2 ],
	         mDataProp : 'version',
	         bUseRendered : false,
	         fnRender : function(oObj, sVal) {
		         return '<label class="editable_select">' + sVal + '</label>';
	         }
	      }, {
	         aTargets : [ 3 ],
	         mDataProp : 'description',
	         aDataSort : [ 3, 2 ],
	         bUseRendered : false,
	         fnRender : function(oObj, sVal) {
		         return '<label class="editable_text">' + sVal + '</label>';
	         }
	      } ],
	      bJQueryUI : true,
	      bAutoWidth : false,
	      bDeferRender : true,
	      bFilter : true,
	      bInfo : true,
	      bLengthChange : true,
	      bProcessing : true,
	      bPaginate : true,
	      bRetrieve : true,
	      bScrollAutoCss : true,
	      bScrollCollapse : false,
	      bServerSide : false,
	      bSort : true,
	      fnCreatedRow : function(nRow, aData, iDataIndex) {
		      $(nRow).data('gapid', aData.id);
		      $(nRow).css('height', '21px');
	      },
	      iDisplayLength : 10,
	      sAjaxDataProp : 'gaps',
	      sAjaxSource : '/resource/gaps',
	      sPaginationType : 'two_button', // full_numbers
	      sScrollY : '240'
	   });

	   // toggle gap details image
	   this.gapsTable.on('click', 'tbody td span.ui-icon-trash', function(event) {
		   var tr = $(this).parents('tr').get(0);
		   var tr_index = AppGaps.gapsTable.fnGetPosition(tr);
		   var tr_data = AppGaps.gapsTable.fnGetData(tr_index);

		   if (confirm('Delete gap ' + tr_data.id + ' ?')) {
			   $.ajax({
			      type : 'delete',
			      url : tr_data.link

			   }).done(function() {
				   AppGaps.gapsTable.fnDeleteRow(tr_index);

			   }).fail(function(error) {
				   console.error(error.statusText);
			   });
		   }
	   });
   },

   /**
	 * init_gap_edit_dialog
	 */
   init_gap_edit_dialog : function() {
	   // edit gap dialog
	   $('#gap-dialog').dialog({
	      autoOpen : false,
	      close : function() {
		      $('#gap-form').off('submit');
		      $('#gapid,#version,#description').removeClass('ui-state-error');
	      },
	      buttons : {
	         'Post' : function() {
		         if ($('#gap-form').h5Validate('allValid')) {
			         $('#gap-form').submit();
		         }
	         },
	         Cancel : function() {
		         $(this).dialog('close');
	         }
	      },
	      width : 340,
	      height : 280,
	      modal : true
	   });
   },

   /**
	 * refresh_gaps_table
	 */
   refresh_gaps_table : function() {
	   AppGaps.gapsTable.fnClearTable();

	   $.ajax({
	      type : 'get',
	      url : '/resource/gaps',
	      dataType : 'json'

	   }).done(function(data) {
		   if (data != null) {
			   AppGaps.gapsTable.fnAddData(data.gaps);
			   AppGaps.init_versions();
		   }

	   }).fail(function(data) {
		   console.error(error.statusText);

	   }).always(function(data) {
		   AppGaps.gapsTable.fnDraw();
	   });
   },

   /**
	 * show_dialog_create_gap
	 */
   show_dialog_create_gap : function() {
	   $('#gap-form').one('submit', function(event) {
		   event.preventDefault();

		   $.ajax({
		      type : 'post',
		      url : '/resource/gap',
		      data : $(this).serialize(),
		      dataType : 'json'

		   }).done(function(data) {
			   $('#gap-dialog').dialog('close');
			   
			   // update gaps table
			   AppGaps.gapsTable.fnAddData(data);

			   // refresh_versions
			   var exists = AppGaps.versions.some(function(version) {
				   return version == data.version;
			   });

			   if (!exists) {
				   AppGaps.versions.push(data.version);
				   AppGaps.refresh_versions();
			   }

		   }).fail(function(error) {
			   console.error(error.statusText);
		   });
	   });

	   $('#gapid, #version, #description').val('');
	   $('#gap-dialog').dialog('open');
   },

   /**
	 * show_dialog_edit_gap
	 */
   show_dialog_edit_gap : function(href, tr) {
	   $('#gap-form').one('submit', function(event) {
		   event.preventDefault();

		   $.ajax({
		      type : 'put',
		      url : href,
		      data : $(this).serialize(),
		      dataType : 'json'

		   }).done(function(data) {
			   $('#gap-dialog').dialog('close');

			   // update gaps table
			   var tr_index = AppGaps.gapsTable.fnGetPosition(tr);
			   AppGaps.gapsTable.fnUpdate(data, tr_index, 0, false);
			   AppGaps.gapsTable.fnDraw();

			   $(tr).effect('highlight', 1000);

		   }).fail(function(error) {
			   console.error(error.statusText);
		   });
	   });

	   $('#gap-dialog').dialog('open');
   },

   /**
	 * init_filters
	 */
   init_filters : function() {
	   $('div#gaps-table_filter.dataTables_filter label input').attr('placeholder', 'Search all');
	   var tfoot = $('div.dataTables_scrollFootInner table.dataTable tfoot');

	   // tfoot select change
	   $('select', tfoot).change(function() {
		   var th = $(this).parent('th');
		   AppGaps.gapsTable.fnFilter($(this).val(), $(th).index());
	   });

	   // tfoot input keyup
	   $('input', tfoot).keyup(function() {
		   var th = $(this).parent('th');
		   AppGaps.gapsTable.fnFilter(this.value, $(th).index());
	   });
   },

   /**
	 * edit_cell
	 */
   edit_cell : function(value) {
	   var td = $(this).parent('td').get(0);
	   var aPos = AppGaps.gapsTable.fnGetPosition(td);

	   // aPos[0] -> rowid
	   var tr_data = AppGaps.gapsTable.fnGetData(aPos[0]);
	   // aPos[0] -> colid
	   var td_prop = AppGaps.gapsTable.dataTableSettings[0].aoColumns[aPos[1]].mDataProp;
	   // tr_data *is* the gap object
	   tr_data[td_prop] = value;

	   $.ajax({
	      type : 'put',
	      contentType : 'application/json',
	      url : tr_data.link,
	      data : JSON.stringify(tr_data),
	      dataType : 'json'

	   }).done(function(data) {
		   AppGaps.gapsTable.fnUpdate(data, aPos[0], 0, false);
		   AppGaps.gapsTable.fnDraw();
		   $(td).effect('highlight', 1000);

	   }).fail(function(error) {
		   console.error(error.statusText);
	   });
   },

   /**
    * init
    */
   init : function() {
	   // init_gaps_table
	   this.init_gaps_table();

	   // init_gap_edit_dialog
	   this.init_gap_edit_dialog();

	   // init_filters
	   this.init_filters();

	   // init_versions
	   this.init_versions();

	   // reset gaps button
	   $('#reset-button').button().click(function() {
		   $.ajax({
		      url : '/resource/gaps',
		      type : 'delete'
		   }).done(function() {
			   $('#reload-button').trigger('click');
		   });
	   });

	   // reload gaps button
	   $('#reload-button').button().click(this.refresh_gaps_table);

	   // create a gap button
	   $('#create-gap-button').button().click(this.show_dialog_create_gap);

	   // gap form validation
	   $('#gap-form').h5Validate({
	      click : true,
	      debug : false
	   });

	   // edit a gap anchor
	   this.gapsTable.on('click', 'tbody a.edit-gap', function(event) {
		   event.preventDefault();
		   var href = $(this).attr('href');

		   var tr = $(this).parents('tr').get(0);
		   var tr_data = AppGaps.gapsTable.fnGetData(tr);

		   $('#gapid').val(tr_data.id);
		   $('#version').val(tr_data.version);
		   $('#description').val(tr_data.description);

		   AppGaps.show_dialog_edit_gap(href, tr);
	   });

	   /*
		 * jEditable handler
		 */
	   var that = this;
	   this.gapsTable.on('draw', function(event) {
		   // editable_text
		   $('#gaps-table label.editable_text').editable(that.edit_cell, {
		      type : 'text',
		      cssclass : 'input_editable',
		      placeholder : '&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;',
		      tooltip : 'Click to edit'
		   });

		   // editable_select
		   $('#gaps-table label.editable_select').editable(that.edit_cell, {
		      data : AppGaps.versions2,
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

   versions2 : function() {
	   var o = {};
	   AppGaps.versions.forEach(function(e) {
		   o[e] = e;
	   });
	   return o;
   }
};

/**
 * document ready
 */
$(document).ready(function() {
	AppGaps.init();
});
