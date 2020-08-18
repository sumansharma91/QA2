
scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/return/details/ReturnSummaryExtnUI",
		"scbase/loader!isccs/utils/BaseTemplateUtils",
		"scbase/loader!sc/plat/dojo/utils/EditorUtils",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/utils/ModelUtils",
		"scbase/loader!isccs/utils/UIUtils"
		],
function(			 
	_dojodeclare,
	_extnReturnSummaryExtnUI,
	_isccsBaseTemplateUtils,
	_scEditorUtils,
	_scWidgetUtils,
	_scScreenUtils,
	_scBaseUtils,
	_scModelUtils,
	_isccsUIUtils
){ 
	return _dojodeclare("extn.return.details.ReturnSummaryExtn", [_extnReturnSummaryExtnUI],
	{
	
	 dataMigration: function() {
		  mCompleteOrdDetails = this.getModel("getCompleteOrderDetails_output");
		  if(mCompleteOrdDetails.Order.OrderName=='MigratedOrder')
		  {	
			_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
		  }
		  //console.log(mCompleteOrdDetails);
	 },
	handleAddressChange: function(
	event, bEvent, ctrl, args) {
		console.log("ReturnSummary.handleAddressChange: ", args);
		var addressModel = null;
		var orderModel = null;
		var temp = null;
		addressModel = _scBaseUtils.getAttributeValue("model", false, args);
		temp = _scModelUtils.getModelObjectFromPath("PersonInfo", addressModel);
		console.log("temp: ", temp);
		orderModel = _scBaseUtils.getAttributeValue("old", false, args);
		console.log("orderModel: ", orderModel);
		
		var sOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel);
		var changeOrderInputModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
		_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", sOrderHeaderKey, changeOrderInputModel);
		
		console.log("args.AddressPanelTitle", args.AddressPanelTitle);
		if (_scBaseUtils.equals(args.AddressPanelTitle, "Refund To")) {
			_scModelUtils.addModelToModelPath("Order.PersonInfoBillTo", temp, changeOrderInputModel);
			
			console.log("changeOrderInputModel: ", changeOrderInputModel);
			_isccsUIUtils.callApi(this, changeOrderInputModel, "extn_changeOrder", null);
		} else if (_scBaseUtils.equals(args.AddressPanelTitle, "Return From")) {
			_scModelUtils.addModelToModelPath("Order.PersonInfoShipTo", temp, changeOrderInputModel);
			
			console.log("changeOrderInputModel: ", changeOrderInputModel);
			_isccsUIUtils.callApi(this, changeOrderInputModel, "extn_changeOrder", null);
		}
	}
});
});