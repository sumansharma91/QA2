
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/order/fulfillmentSummary/CarrierServicePopupExtnUI",
          "scbase/loader!isccs/utils/UIUtils", 
          "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
          "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnCarrierServicePopupExtnUI,
			    _isccsUIUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,_scEventUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.CarrierServicePopupExtn", [_extnCarrierServicePopupExtnUI],{
	// custom code here
		
		onApply: function(
		        event, bEvent, ctrl, args) 
        {
           var gerCarrierServiceOutput = _scBaseUtils.getTargetModel(
            this, "gerCarrierServiceOutput");
            if(_scBaseUtils.isVoid(gerCarrierServiceOutput.CarrierServiceCode))
            {
               var popupMessage = _scScreenUtils.getString(this, "Level of service cannot be blank");
					_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
			   return;
            }
           
            if (
            this.IsLOSChanged) {
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
            } else {
                _scWidgetUtils.closePopup(
                this, "APPLY_NOCHANGE", false);
            }
        },
        
        handleMashupOutput: function(
                mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
                    if (
                    _scBaseUtils.equals(
                    mashupRefId, "getCarrierServiceOptionsForOrdering")) {
                            console.log(modelOutput);
                        _scBaseUtils.setModel(
                        this, "CarrierServiceData", modelOutput, null);
                        _scBaseUtils.setModel(
                        this, "CarrierServiceSourceData", modelOutput, null);
                        var carrierServiceCode = null;
                        this.InitialCarrierServiceCode = _scModelUtils.getStringValueFromPath("CarrierServiceList.CarrierServiceCode", modelOutput);

                        var carrierServiceModel = null;
                        carrierServiceModel = _scModelUtils.getModelObjectFromPath("CarrierServiceList", modelOutput);
                        
                        if(_scBaseUtils.isVoid(this.InitialCarrierServiceCode))
                        {
                            _scWidgetUtils.setValue(this, "carrierService", "STANDARD", false);    
                        }
                        var carrierServiceArgs = null;
                        carrierServiceArgs = {};
                        _scBaseUtils.setAttributeValue("argumentList.CarrierServiceData", carrierServiceModel, carrierServiceArgs);
                        _scEventUtils.fireEventGlobally(
                        this, "SetCarrierServiceModelInParent", null, carrierServiceArgs);
                    }
                },   
			        
});
});

