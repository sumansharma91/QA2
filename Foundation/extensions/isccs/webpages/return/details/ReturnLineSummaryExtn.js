
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/return/details/ReturnLineSummaryExtnUI",		"scbase/loader!isccs/utils/BaseTemplateUtils",
		"scbase/loader!sc/plat/dojo/utils/EditorUtils",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnLineSummaryExtnUI,
				_isccsBaseTemplateUtils,
	_scEditorUtils,
	_scWidgetUtils
){ 
	return _dojodeclare("extn.return.details.ReturnLineSummaryExtn", [_extnReturnLineSummaryExtnUI],{
	// custom code here
		checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
	        mCompleteOrdLineDetails = this.getModel("getCompleteOrderLineDetails_output");
			//console.log(mCompleteOrdLineDetails);
			if (mCompleteOrdLineDetails.OrderLine.Extn.ExtnIsMigrated === "Y") {
				_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
			}
		}
});
});

