
scDefine([ "scbase/loader!dojo/_base/declare",
		   "scbase/loader!extn/order/details/OrderSummaryExtnUI",
		   "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		   "scbase/loader!isccs/utils/CustomerUtils",
		   "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		   "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		   "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		   "scbase/loader!isccs/utils/BaseTemplateUtils",
		   "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		   "scbase/loader!isccs/utils/UIUtils",
		   "scbase/loader!sc/plat/dojo/utils/EventUtils"
		   ]
,
function(_dojodeclare,
		 _extnOrderSummaryExtnUI,
		 _scBaseUtils,
		 _isccsCustomerUtils,
		 _scModelUtils,
		 _scScreenUtils,
		 _scWidgetUtils,
		 _isccsBaseTemplateUtils,
		 _scEditorUtils,
		 _isccsUIUtils,
		 _scEventUtils
){ 
	return _dojodeclare("extn.order.details.OrderSummaryExtn", [_extnOrderSummaryExtnUI],{
	// custom code here
	
	getFirstLastName: function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue) { 	
		var getFullName = null;
		var fName = _scModelUtils.getModelObjectFromPath("Order.CustomerFirstName", unformattedValue);
		var lName = _scModelUtils.getModelObjectFromPath("Order.CustomerLastName", unformattedValue);
            if (!(_scBaseUtils.isVoid(fName)) && !(_scBaseUtils.isVoid(lName))) {
				getFullName = fName + " " + lName;
			}	
        return getFullName;
    },
	
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
		console.log("OrderSummary.handleAddressChange: ", args);
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
		if (_scBaseUtils.equals(args.AddressPanelTitle, "Bill To")) {
			_scModelUtils.addModelToModelPath("Order.PersonInfoBillTo", temp, changeOrderInputModel);
			
			console.log("changeOrderInputModel: ", changeOrderInputModel);
			_isccsUIUtils.callApi(this, changeOrderInputModel, "extn_changeOrder", null);
		}
	},
	updateEditorHeader: function(
	        event, bEvent, ctrl, args) 
    {
        _isccsBaseTemplateUtils.updateCustomerMessage(
        this, "CUST_OrderSummary", true);
        _isccsBaseTemplateUtils.updateTitle(
        this, "TITLE_OrderSummary", null);
        var temp = _scScreenUtils.getModel(
        this, "getCompleteOrderDetails_output");
      
        if(!_scBaseUtils.isVoid(temp) && !_scBaseUtils.isVoid(temp.Order))
        {
           _scBaseUtils.setAttributeValue("title", "Order "+temp.Order.OrderNo, args);
        }
        _scEventUtils.fireEventGlobally(
        this, "changeEditorTitle", null, args);
        _scScreenUtils.focusFirstEditableWidget(
        this);
        return true;
    },
	setDisplayChannel: function(
        event, bEvent, ctrl, args) {
			// ARH-106 : Added to set the enterprise name as the displayEntryType for Marketplace orders : BEGIN
			var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
			
			console.log("******* orderModel *******", orderModel);
			var displayEntryType = _scModelUtils.getStringValueFromPath("Order.DisplayEntryType", orderModel);
			if (_scBaseUtils.equals(displayEntryType, "Marketplace")) {
				var entName = _scModelUtils.getStringValueFromPath("Order.EnterpriseName", orderModel);
				_scModelUtils.setStringValueAtModelPath("Order.DisplayEntryType", entName, orderModel);
				_scScreenUtils.setModel(this, "getCompleteOrderDetails_output", orderModel, null);
			}
			// ARH-106 : Added to set the enterprise name as the displayEntryType for Marketplace orders : END
	}
	
});
});