
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/common/paymentCapture/PaymentCapture_StoredValueExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EventUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/PADSSUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
],
function( _dojodeclare,
		_extnPaymentCapture_StoredValueExtnUI,
		_isccsBaseTemplateUtils, 
		_isccsOrderUtils, 
		_isccsUIUtils, 
		_isccsWidgetUtils, 
		_scBaseUtils, 
		_scEventUtils, 
		_scModelUtils, 
		_scPADSSUtils, 
		_scScreenUtils, 
		_scWidgetUtils
		  
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCapture_StoredValueExtn", [_extnPaymentCapture_StoredValueExtnUI],{
	// custom code here
	
	initializeScreen: function(event, bEvent, ctrl, args) {
		var screenInput = null;
		screenInput = _scScreenUtils.getInitialInputData(this);
		_scScreenUtils.setModel(this, "PaymentCapture_output", screenInput, null);
        //_scPADSSUtils.isAlive(this, "PADSSUp", "PADSSDown");
		if (_scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false)) {
			_scWidgetUtils.disableWidget(this, "txtDisplaySvcNo", false);
			_scWidgetUtils.showWidget(this, "txtDisplaySvcNo", false, null);
        }
        var sReturnDocumentType = "";
        sReturnDocumentType = _isccsOrderUtils.getReturnOrderDocumentType();
        if (_scBaseUtils.equals(sReturnDocumentType, _scModelUtils.getStringValueFromPath("Order.DocumentType", screenInput, false))) {
			_scWidgetUtils.hideWidget(this, "txtMaxChargeLimit", false);
        }
		_scWidgetUtils.hideWidget(this, "txtPaymentReference3", false);

		
	},
	handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {

		_isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);

		if(!_scBaseUtils.isVoid(mashupRefList)){
			if (_scBaseUtils.equals(mashupRefList[0].mashupRefId, "extn_VSICheckGCBalance_RefID")) {

				if(_scBaseUtils.equals("Y",_scModelUtils.getStringValueFromPath("IsValid",mashupRefList[0].mashupRefOutput.GiftCard))){
					var balance = _scModelUtils.getNumberValueFromPath("Balance",mashupRefList[0].mashupRefOutput.GiftCard);
					//var paymentModel = _scScreenUtils.getModel(this, "extn_GiftCard_input", null);
					_scScreenUtils.setModel(this, "extn_giftCard_output", mashupRefList[0].mashupRefOutput, null);
					
					_scWidgetUtils.setValue(this,"extn_datalabelGiftCardBalance", balance, false);
					_scWidgetUtils.setValue(this,"txtMaxChargeLimit", balance, false);
					
					var paymentCaptureInputModel = _scBaseUtils.getTargetModel(this.getOwnerScreen(true), "PaymentCapture_input", null);
					console.log("paymentCaptureInputModel",paymentCaptureInputModel);
				}else{
					_scScreenUtils.showErrorMessageBox(this,"Invalid Gift Card","error",null,null);
				}
			}
		} 
    },	
	getGiftCardValue: function(){

		var paymentModel = _scBaseUtils.getTargetModel(this, "PaymentCapture_input", null);	
		var gcInput = {};
		gcInput.GiftCard  = {};
		var cardNumber = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference1",paymentModel);
		var pin = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2",paymentModel);
		
		
		if(!_scBaseUtils.isVoid(cardNumber)){
			
			_scModelUtils.addStringValueToModelObject("cardNumber" ,cardNumber ,gcInput.GiftCard);
			_isccsUIUtils.callApi(this, gcInput, "extn_VSICheckGCBalance_RefID");	
					
		}else{
			_isccsBaseTemplateUtils.showMessage(this, "Missing Gift Card # and/or PIN", "error", null);
		}
		
	}
        
});
});

