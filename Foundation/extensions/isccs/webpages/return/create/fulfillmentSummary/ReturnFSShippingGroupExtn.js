
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ModelUtils",  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils",  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",  "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",  "scbase/loader!sc/plat/dojo/utils/ControllerUtils","scbase/loader!extn/return/create/fulfillmentSummary/ReturnFSShippingGroupExtnUI"]
,
function(			 
			    _dojodeclare
			 ,
				_scModelUtils
			,
				_isccsUIUtils
			,
				_scBaseUtils
			,
				_scWidgetUtils
			,
				_isccsBaseTemplateUtils
			,
				_scScreenUtils
			,
				_scControllerUtils
			,
			    _extnReturnFSShippingGroupExtnUI
){ 
	return _dojodeclare("extn.return.create.fulfillmentSummary.ReturnFSShippingGroupExtn", [_extnReturnFSShippingGroupExtnUI],{
	// custom code here
	
	toggleButtonsBasedOnData: function(orderLine){
    		var isCustomerKeepAllowed = _scModelUtils.getStringValueFromPath("IsCustomerKeepAllowed",orderLine);
        	var IsSchedulePickupAllowed = _scModelUtils.getStringValueFromPath("IsSchedulePickupAllowed",orderLine);
        	var modificationList = _scModelUtils.getModelListFromPath("Modifications.Modification", orderLine);
			var sIsDeliveryMethodChangeAllowed = _isccsUIUtils.getModificationTypeValueFromList(modificationList, "CHANGE_DELIVERY_METHOD");
        	/*if(_scBaseUtils.equals(isCustomerKeepAllowed,"N") || _scBaseUtils.equals(sIsDeliveryMethodChangeAllowed,"N")){
        		_scWidgetUtils.disableWidget(this, "changeToCustomerKeep");
        		_isccsBaseTemplateUtils.showMessage(this, "ReturnModificationNotAllowed", "error", null);
        	}        	
        	if(_scBaseUtils.equals(IsSchedulePickupAllowed,"N") || _scBaseUtils.equals(sIsDeliveryMethodChangeAllowed,"N")){
        		_scWidgetUtils.disableWidget(this, "changeToPickup");
        		_isccsBaseTemplateUtils.showMessage(this, "ReturnModificationNotAllowed", "error", null);
        	}   */     	       	
    	}
	
	
});
});

