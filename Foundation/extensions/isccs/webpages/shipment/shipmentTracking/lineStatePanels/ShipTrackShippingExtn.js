
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/shipmentTracking/lineStatePanels/ShipTrackShippingExtnUI",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils"
	]
,
function(			 
	_dojodeclare,
	_extnShipTrackShippingExtnUI,
	_isccsUIUtils,
	_scScreenUtils,
	_scWidgetUtils,
	_isccsBaseTemplateUtils,
	_scBaseUtils,
	_scModelUtils
){ 
	return _dojodeclare("extn.shipment.shipmentTracking.lineStatePanels.ShipTrackShippingExtn", [_extnShipTrackShippingExtnUI],{
	
	checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
		
		var parentScreen = _isccsUIUtils.getParentScreen(this, false);
		var grandParentScreen = _isccsUIUtils.getParentScreen(parentScreen, false);
        //var granddParentScreen = _isccsUIUtils.getParentScreen(grandParentScreen, false);
        mCompleteOrdLineDetails = grandParentScreen.getModel("getCompleteOrderLineDetails_output");
		if(!_scBaseUtils.isVoid(mCompleteOrdLineDetails))
		{
			if (mCompleteOrdLineDetails.OrderLine.Extn.ExtnIsMigrated === "Y") {
			_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
			
			_scWidgetUtils.disableWidget(this, "lnkReship", true);
			}
		}
		mCompleteOrdLineDetails = parentScreen.getModel("getCompleteOrderLineDetails_Output");
		//console.log('dddff',mCompleteOrdLineDetails);
		if(!_scBaseUtils.isVoid(mCompleteOrdLineDetails))
		{
			if (mCompleteOrdLineDetails.OrderLine.Order.OrderName === "MigratedOrder") {
			_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
			
			_scWidgetUtils.disableWidget(this, "lnkReship", true);
			}
		}
		
	},
	//OMS-1005: handleReshipClick method has been extended for OMS-1005 changes//
	 handleReshipClick: function(
                event, bEvent, ctrl, args) {
            var getCompleteOrderLineDetailsOutput = _scScreenUtils.getModel(
            this, "getCompleteOrderLineDetails_Output");
            console.log(getCompleteOrderLineDetailsOutput);
            var orderLineKey = getCompleteOrderLineDetailsOutput.OrderLine.OrderLineKey;
            var personInfoShipTo=getCompleteOrderLineDetailsOutput.OrderLine.PersonInfoShipTo;
 

            var reshipPopupModel = _scScreenUtils.getTargetModel(
                    this, "reshipPopup_input", null);
            if(orderLineKey && reshipPopupModel.Order){
				reshipPopupModel.Order.OrderLine.OrderLineKey = orderLineKey;
				reshipPopupModel.Order.OrderLine.PersonInfoShipTo=personInfoShipTo;
				var popupParams = null;
	            popupParams = {};
	            popupParams["screenInput"] = reshipPopupModel;
	            popupParams["constructorData"] = null;
	            var dialogParams = null;
	            dialogParams = {};
	            _scBaseUtils.setAttributeValue("closeCallBackHandler", "reshipProductCallBack", dialogParams);
	            _isccsUIUtils.openSimplePopup("isccs.shipment.reship.ReshipShipmentLine", "Title_Reship_Product", this, popupParams, dialogParams);
            }
            else{
            	var noReshipError = null;
            	noReshipError = _scScreenUtils.getString(
                this, "Error_ReshipNotAvailable");
                _scScreenUtils.showErrorMessageBox(
                this, noReshipError, "errorMessageBoxCallback", null, null);
            }
      
                    
        }
});
});