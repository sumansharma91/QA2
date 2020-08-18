
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/details/OrderLineSummaryExtnUI",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	]
,
function(			 
	_dojodeclare,
	_extnOrderLineSummaryExtnUI,
	_scScreenUtils,
	_scWidgetUtils,
	_isccsBaseTemplateUtils,
	_scEditorUtils
){ 
	return _dojodeclare("extn.order.details.OrderLineSummaryExtn", [_extnOrderLineSummaryExtnUI],{
	
	checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
        mCompleteOrdLineDetails = this.getModel("getCompleteOrderLineDetails_output");
		if (mCompleteOrdLineDetails.OrderLine.Extn.ExtnIsMigrated == "Y") {
			_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
		}
	}
});
});

