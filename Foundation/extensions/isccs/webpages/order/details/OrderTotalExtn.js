
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/details/OrderTotalExtnUI",
		   "scbase/loader!isccs/utils/OrderUtils",
		   "scbase/loader!isccs/utils/SharedComponentUtils",
		   "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		   "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		   "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		   "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		   "scbase/loader!isccs/utils/UIUtils"]
,
function(			 
			    _dojodeclare,
			    _extnOrderTotalExtnUI,
				_isccsOrderUtils,
				_isccsSharedComponentUtils, 
				_scBaseUtils,
				_scModelUtils,
				_scScreenUtils,
				_scWidgetUtils,
				_isccsUIUtils
				
){ 
	return _dojodeclare("extn.order.details.OrderTotalExtn", [_extnOrderTotalExtnUI],{
	// custom code here
		openTotalAmount: function() {
            var orderModel = null;
            orderModel = _scScreenUtils.getModel(this, "OrderPrice");
            _isccsSharedComponentUtils.openPriceSummary(orderModel, this);
        },
	        setLabel: function(dataValue, screen, widget, namespace, modelObject, options) {
            var returnValue = dataValue;
            var sReturnDocumentType = "";
            sReturnDocumentType = _isccsOrderUtils.getReturnOrderDocumentType();
            var sOrderDocumentType = "";
            sOrderDocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", modelObject);
            if (
            _scBaseUtils.equals(
            sOrderDocumentType, sReturnDocumentType)) {
                var label = "";
                label = _scScreenUtils.getLabelString(
                this, "ReturnTotal");
                _scWidgetUtils.setLabel(
                this, "lblTotal", label);
                _scWidgetUtils.setLabel(
                this, "lnkTotal", label);
            }
           // var orderReprice = null;
	       // orderReprice = _scScreenUtils.getModel(this, "OrderPrice");
	       // _scScreenUtils.setModel(
	               // this, "OrderPrice", orderReprice, null);
            return returnValue;
        },
	getTax: function(dataValue, screen, widget, namespace, modelObject, options){
		var returnValue = "";
		/* var orderModel = null;
            orderModel = _scScreenUtils.getModel(this, "OrderPrice"); */
		var HeaderTaxes = _scModelUtils.getStringValueFromPath("Order.HeaderTaxes", modelObject);
		var grandTax = _scModelUtils.getStringValueFromPath("Order.OverallTotals.GrandTax", modelObject);
		
		if(!_scBaseUtils.isVoid(HeaderTaxes)){
		var HeaderTax = _scModelUtils.getStringValueFromPath("HeaderTax", HeaderTaxes);
			
			for(i=0;i<HeaderTax.length;i++){
				if(_scBaseUtils.equals("Shipping Tax", _scModelUtils.getStringValueFromPath("TaxName", HeaderTax[i]))){
					
					returnValue = _scModelUtils.getStringValueFromPath("Tax", HeaderTax[i]);
					if(!_scBaseUtils.isVoid(returnValue) && !_scBaseUtils.isVoid(grandTax) && grandTax >= returnValue)
					{
						//_scModelUtils.setStringValueAtModelPath("Order.OverallTotals.GrandTax",grandTax - returnValue, modelObject);

					  _scWidgetUtils.setValue(this, "lblTaxes", grandTax - returnValue, false);  
					}
				}				
			}	   			
		}
		//_scScreenUtils.setModel(
	               // this, "OrderPrice", modelObject, null);
	        
		return returnValue;
	},
	 setInitialized: function(
		        event, bEvent, ctrl, args) {
		 var parentScreen = null;
			parentScreen = _isccsUIUtils.getParentScreen(this, false);
		            this.isScreeninitialized = true;
		            var orderReprice = null;
			        orderReprice = _scScreenUtils.getModel(parentScreen, "paymentConfirmation_getCompleteOrderDetails_Output");
			        console.log('lets see'+orderReprice);
			        _scScreenUtils.setModel(
			                this, "OrderPrice", orderReprice, null);
		}
});
});

