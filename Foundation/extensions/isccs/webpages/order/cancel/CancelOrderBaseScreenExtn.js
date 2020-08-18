
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/cancel/CancelOrderBaseScreenExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ControllerUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/OrderUtils"
          ]
,
function(_dojodeclare,
	     _extnCancelOrderBaseScreenExtnUI,
		 _scBaseUtils,
		 _scScreenUtils,
		 _isccsBaseTemplateUtils,
		 _scModelUtils,
		 _scGridxUtils,
		 _scEventUtils,
		 _scControllerUtils,
		 scWidgetUtils,
		 _isccsOrderUtils
){ 
	return _dojodeclare("extn.order.cancel.CancelOrderBaseScreenExtn", [_extnCancelOrderBaseScreenExtnUI],{
					
	// custom code here
	
	 errorAlert: function(event, bEvent, ctrl, args) {
		 
		var getCompleteOrderDetails_output = null;
		var maxOrderStatus = null;
		var cancelOrderItems = null;
		var getCancelQty = null;
		var getOrderQty = null;
		var getLineType = null;
		var orderLineLength = 0;	
		var cancelOrderListScreenObj = null;
		
        cancelOrderListScreenObj = _scScreenUtils.getChildScreen(this, "cancelOrderListScreen");
        getCompleteOrderDetails_output = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");  
		maxOrderStatus = _scModelUtils.getStringValueFromPath("Order.MaxOrderStatus", getCompleteOrderDetails_output);
		cancelOrderItems = _scGridxUtils.getSelectedTargetRecordsUsingUId(cancelOrderListScreenObj, "OLST_listGrid");
		orderLineLength = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine.length", getCompleteOrderDetails_output);
		
		for(var i=0; i < orderLineLength;i++) {
			getLineType = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine."+i+".LineType", getCompleteOrderDetails_output);
//			getCancelQty = _scModelUtils.getStringValueFromPath("OrderLineList.OrderLine."+i+".CancelQuantity", cancelOrderItems);
//			getOrderQty = _scModelUtils.getStringValueFromPath("OrderLineList.OrderLine."+i+".OrderedQty", cancelOrderItems);	
			
			if(_scBaseUtils.equals(getLineType,"PICK_IN_STORE") && _scBaseUtils.numberGreaterThan(maxOrderStatus,1500)){


				this.canOrderBeModified = false;
				_isccsBaseTemplateUtils.showMessage(this, "Cannot cancel orderLine partially! Please cancel the entire order.", "error", null);
			
				/* if(maxOrderStatus > 1000 && (getCancelQty !== getOrderQty)){
//_isccsBaseTemplateUtils.showMessage(this, "Message_screenHasErrors", "error", null);
//_isccsBaseTemplateUtils.showMessage(this, "Cannot cancel orderLine partially! Please cancel the entire order.", "error", null);
    	    	} */
			} 
					
		}
			          
    },
		
	 save: function(event, bEvent, ctrl, args) {
		 
		 //this.errorAlert(); //implements the RCP Logic
		 
				if (_scBaseUtils.equals(this.canOrderBeModified, false) ) {
					//_scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
				} else {
					if (isScreenValid = _scScreenUtils.validate(this)) {
						var actionOnPage = null;
						if (_scBaseUtils.equals(ctrl, "update_order")) {
							var actionOnPage = ctrl;
						} else {
							actionOnPage = _scBaseUtils.getStringValueFromBean("Action", args);
						}
						this.action = actionOnPage;
						_scBaseUtils.setAttributeValue("actionOnPage", actionOnPage, args);
						this.callCancelOrder(null, args);
					} else {
                    _isccsBaseTemplateUtils.showMessage(this, "Message_screenHasErrors", "error", null);
					}
				} 
				
//console.log("this.canOrderBeModified",this.canOrderBeModified);
            
        },
		updateQtyToCancel: function(
        selectedOrderLinesList) {
            var cancelOrderLineList = null;
            cancelOrderLineList = [];
            var selectedRowsCount = 0;
            selectedRowsCount = _scBaseUtils.getAttributeCount(
            selectedOrderLinesList);
            for (
            var i = 0;
            i < selectedRowsCount;
            i = i + 1) {
                var selectedIndexObj = null;
                selectedIndexObj = selectedOrderLinesList[
                i];
                var orderLineObj = null;
                orderLineObj = {};
                var orderLineKey = null;
                orderLineKey = _scModelUtils.getStringValueFromKey("OrderLineKey", selectedIndexObj);
                _scModelUtils.addStringValueToModelObject("OrderLineKey", orderLineKey, orderLineObj);
                var primeLineNo = null;
                primeLineNo = _scModelUtils.getStringValueFromKey("PrimeLineNo", selectedIndexObj);
                _scModelUtils.addStringValueToModelObject("PrimeLineNo", primeLineNo, orderLineObj);
                var enteredCancelQty = null;
                var orderedQuantity =  _scModelUtils.getNumberValueFromKey("OrderedQty", selectedIndexObj);               
                enteredCancelQty = _scModelUtils.getNumberValueFromKey("CancelQuantity", selectedIndexObj);				           
                _scModelUtils.setNumberValueAtModelPath("QuantityToCancel", enteredCancelQty, orderLineObj);								
				if(_scBaseUtils.equals(orderedQuantity, enteredCancelQty))
				{
					
					_scModelUtils.setStringValueAtModelPath("Action", "CANCEL", orderLineObj);
				}
				
                _scModelUtils.addModelObjectToModelList(
                orderLineObj, cancelOrderLineList);
            }
            return cancelOrderLineList;
        },	
        
        validateCancelingQty: function(
                cancelOrderItems) {
                    var isCancelQtyValid = false;
                    var errorTextArray = null;
                    errorTextArray = [];
                    var selectedOrderLinesList = null;
                    selectedOrderLinesList = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", cancelOrderItems);
                    var selectedRowsCount = 0;
                    selectedRowsCount = _scBaseUtils.getAttributeCount(
                    selectedOrderLinesList);
                    for (
                    var i = 0;
                    i < selectedRowsCount;
                    i = i + 1) {
                        var selectedIndexObj = null;
                        selectedIndexObj = selectedOrderLinesList[
                        i];
                        console.log("selectedIndexObj",selectedIndexObj);
                        var delMethod=_scModelUtils.getStringValueFromKey("DeliveryMethod", selectedIndexObj);
                        var availableCancelQty = 0;
                        availableCancelQty = _isccsOrderUtils.calculateOrder_CancelQuantityWOFormat(
                        this.txQtyRuleSetValue, selectedIndexObj, false);
                        var enteredCancelQty = null;
                        enteredCancelQty = _scModelUtils.getNumberValueFromKey("CancelQuantity", selectedIndexObj);
                        var errorText = null;
                        var primeLineNo = 0;
                        primeLineNo = _scModelUtils.getNumberValueFromKey("PrimeLineNo", selectedIndexObj);
                        var errorTextNLSArray = null;
                        if (
                        enteredCancelQty <= 0) {
                            errorTextNLSArray = [];
                            errorTextNLSArray.push(
                            primeLineNo);
                            errorText = _scScreenUtils.getFormattedString(
                            this, "cancelOrder_cancelQty_less_error", errorTextNLSArray);
                        } else if (
                        enteredCancelQty > availableCancelQty) {
                            errorTextNLSArray = [];
                            errorTextNLSArray.push(
                            primeLineNo);
                            errorTextNLSArray.push(
                            enteredCancelQty);
                            errorTextNLSArray.push(
                            availableCancelQty);
                            errorText = _scScreenUtils.getFormattedString(
                            this, "cancelOrder_cancelQty_more_error", errorTextNLSArray);
                        }
                        else if (
                        enteredCancelQty < availableCancelQty && _scBaseUtils.equals(delMethod,"Pick Up")) {
                            errorTextNLSArray = [];
                            errorTextNLSArray.push(
                            primeLineNo);
                            errorTextNLSArray.push(
                            enteredCancelQty);
                            errorTextNLSArray.push(
                            availableCancelQty);
                            errorText = _scScreenUtils.getFormattedString(
                            this, "cancelOrder_cancelQty_less_custom_error", errorTextNLSArray);
                        }
                        if (!(
                        _scBaseUtils.isVoid(
                        errorText))) {
                            var messageModel = null;
                            messageModel = {};
                            messageModel["message"] = errorText;
                            errorTextArray.push(
                            messageModel);
                        }
                    }
                    var errorListCount = 0;
                    errorListCount = _scBaseUtils.getAttributeCount(
                    errorTextArray);
                    if (
                    errorListCount > 0) {
                        var inputMessageModel = null;
                        inputMessageModel = {};
                        inputMessageModel["messageList"] = errorTextArray;
                        _isccsBaseTemplateUtils.showMessage(
                        this, "cancelOrder_cancelQty_header_error", "error", inputMessageModel);
                    } else {
                        isCancelQtyValid = true;
                    }
                    return isCancelQtyValid;
                },
		
		
	
});
});

