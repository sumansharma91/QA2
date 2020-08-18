
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/customerAppeasement/CustomerAppeasementOrderLineListExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!isccs/utils/ContextUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils"
]
,
function(			 
		_dojodeclare,
		_extnCustomerAppeasementOrderLineListExtnUI,
		_scScreenUtils,
		_scModelUtils,
		_scBaseUtils,
		_scEventUtils,
		_isccsUIUtils,
		_scWidgetUtils,
		_isccsUIUtils,
		_isccsContextUtils,
		_scEditorUtils
				
				
){ 
	return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementOrderLineListExtn", [_extnCustomerAppeasementOrderLineListExtnUI],{
		
		// This method will store the Customer Email ID to Context on Page Load.
		storeCustomerEmailIDToContext : function(){
			var getCompleteOrderLineList = null;
			getCompleteOrderLineList = _scScreenUtils.getModel(this, "getCompleteOrderLineList_output");
			//console.log("getCompleteOrderLineList>>>",getCompleteOrderLineList);
			// For add the Customer Email ID in the context 
			var customerEMailID = null;
			customerEMailID = _scModelUtils.getStringValueFromPath("Page.Output.OrderLineList.Order.CustomerEMailID", getCompleteOrderLineList);
			_isccsContextUtils.addToContext("customerEMailID",customerEMailID);
		}
});
});

