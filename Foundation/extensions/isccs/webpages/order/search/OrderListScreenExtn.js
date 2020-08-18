scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/search/OrderListScreenExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare,
			    _extnOrderListScreenExtnUI,
				_scBaseUtils,
				_scScreenUtils,
				_scModelUtils,
				_scWidgetUtils
				
){ 
	return _dojodeclare("extn.order.search.OrderListScreenExtn", [_extnOrderListScreenExtnUI],{
	// custom code here
	
	getFulfillmentMethod: function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue,value,gridRowRecord,colConfig){
					var returnValue ="";						
					var orderLine = _scModelUtils.getStringValueFromPath("OrderLines.OrderLine",gridRowJSON);
					if(!_scBaseUtils.isVoid(orderLine)){
						returnValue = _scModelUtils.getStringValueFromPath("DeliveryMethod",orderLine[0]);
						if(returnValue == "PICK")
						{
							returnValue = "Pick Up";
						}
						else if (returnValue == "SHP")
						{
							returnValue = "Shipping";
						}
					}
					return returnValue;

	}

});
});