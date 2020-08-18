
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ModelUtils",  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils",  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",  "scbase/loader!isccs/utils/BaseTemplateUtils","scbase/loader!extn/return/create/fulfillmentSummary/ReturnFSCustomerKeepGroupExtnUI"]
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
			    _extnReturnFSCustomerKeepGroupExtnUI
){ 
	return _dojodeclare("extn.return.create.fulfillmentSummary.ReturnFSCustomerKeepGroupExtn", [_extnReturnFSCustomerKeepGroupExtnUI],{
	// custom code here
	
	
	toggleButtonsBasedOnData: function(orderLine){    		
        	var IsSchedulePickupAllowed = _scModelUtils.getStringValueFromPath("IsSchedulePickupAllowed",orderLine);
        	var IsReturnToStoreAllowed = _scModelUtils.getStringValueFromPath("IsReturnToStoreAllowed",orderLine);
        	var modificationList = _scModelUtils.getModelListFromPath("Modifications.Modification", orderLine);
			var sIsDeliveryMethodChangeAllowed = _isccsUIUtils.getModificationTypeValueFromList(modificationList, "CHANGE_DELIVERY_METHOD");        	
        	/*if(_scBaseUtils.equals(IsSchedulePickupAllowed,"N") || _scBaseUtils.equals(sIsDeliveryMethodChangeAllowed,"N")){
        		_scWidgetUtils.disableWidget(this, "changeToPickup");
        		_isccsBaseTemplateUtils.showMessage(this, "ReturnModificationNotAllowed", "error", null);
        	}
        	if(_scBaseUtils.equals(IsReturnToStoreAllowed,"N") || _scBaseUtils.equals(sIsDeliveryMethodChangeAllowed,"N")){
        		_scWidgetUtils.disableWidget(this, "changeToShipping");
        		_isccsBaseTemplateUtils.showMessage(this, "ReturnModificationNotAllowed", "error", null);
        	}*/
    	}
	
});
});

