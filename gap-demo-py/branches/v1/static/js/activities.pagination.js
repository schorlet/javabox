$.fn.dataTableExt.oPagination.two_button_activities = {
   fnInit : function(oSettings, nPaging, fnCallbackDraw) {

	   var fnClickHandler = function(e) {
		   if (e.data.action == 'previous') {
		   	AppActivity.pagination_refresh(-14);
		   } else {
		   	AppActivity.pagination_refresh(14);
		   }
	   };

	   var sAppend = '';
	   if (!oSettings.bJUI) {
		   var oLang = oSettings.oLanguage.oPaginate;
		   sAppend = '<a class="' + oSettings.oClasses.sPagePrevEnabled + '" tabindex="'
		         + oSettings.iTabIndex + '" role="button">' + oLang.sPrevious + '</a>' + '<a class="'
		         + oSettings.oClasses.sPageNextEnabled + '" tabindex="' + oSettings.iTabIndex
		         + '" role="button">' + oLang.sNext + '</a>';

	   } else {
		   sAppend = '<a class="' + oSettings.oClasses.sPagePrevEnabled + '" tabindex="'
		         + oSettings.iTabIndex + '" role="button"><span class="'
		         + oSettings.oClasses.sPageJUIPrev + '"></span></a>' + '<a class="'
		         + oSettings.oClasses.sPageNextEnabled + '" tabindex="' + oSettings.iTabIndex
		         + '" role="button"><span class="' + oSettings.oClasses.sPageJUINext
		         + '"></span></a>';
	   }

	   $(nPaging).append(sAppend);
	   var els = $('a', nPaging);
	   var nPrevious = els[0], nNext = els[1];

	   oSettings.oApi._fnBindAction(nPrevious, {
		   action : "previous"
	   }, fnClickHandler);

	   oSettings.oApi._fnBindAction(nNext, {
		   action : "next"
	   }, fnClickHandler);

	   /* ID the first elements only */
	   if (!oSettings.aanFeatures.p) {
		   nPaging.id = oSettings.sTableId + '_paginate';
		   nPrevious.id = oSettings.sTableId + '_previous';
		   nNext.id = oSettings.sTableId + '_next';

		   nPrevious.setAttribute('aria-controls', oSettings.sTableId);
		   nNext.setAttribute('aria-controls', oSettings.sTableId);
	   }
   },

   fnUpdate : function(oSettings, fnCallbackDraw) {
	   // nothing to do: buttons are always enabled
   }
};
