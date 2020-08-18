
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/payment/confirmation/PaymentMethodExtnUI",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!isccs/utils/OrderUtils"
	
	]
,
function(			 
	_dojodeclare,
	_extnPaymentMethodExtnUI,
	_isccsUIUtils,
	_scWidgetUtils,
	_scScreenUtils,
	_scBaseUtils,
	_scEventUtils,
	_scModelUtils,
	_isccsBaseTemplateUtils,
	_scEditorUtils,
	_isccsOrderUtils
){ 
	return _dojodeclare("extn.payment.confirmation.PaymentMethodExtn", [_extnPaymentMethodExtnUI],{
	
	checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
		/*console.log("PAYMENT METHOD")
		var parentScreen = _isccsUIUtils.getParentScreen(this, false);
		console.log("parentScreen", parentScreen);
        mCompleteOrdDetails = parentScreen.getModel("paymentConfirmation_getCompleteOrderDetails_Output");
		console.log("mCompleteOrdDetails: ", mCompleteOrdDetails)
		if (mCompleteOrdDetails.Order.Extn.ExtnIsMigrated === "Y") {
			_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
			
		}*/
	},
	handleRemovePaymentMethod: function(res) {
            if (_scBaseUtils.equals(res, "Ok")) {
                var paymentMethodModel = null;
                paymentMethodModel = _scScreenUtils.getModel(this, "PaymentMethod");
				console.log("This is in PaymentMethod of handleRemovePaymentMethod for paymentMethodModel",paymentMethodModel);
				var paymentType = _scModelUtils.getStringValueFromPath("PaymentType",paymentMethodModel);
				if(_scBaseUtils.equals(paymentType, "ONLINE_GIFT_CARD") ||  _scBaseUtils.equals(paymentType, "GIFT_CARD")){
				var scrn = this.ownerScreen;
				//var issueRefundToEGiftCardButton = scrn.getWidgetByUId("extn_issueRefundToEGiftCard");
				_scWidgetUtils.enableWidget(scrn, "extn_issueRefundToEGiftCard");
				}
                var args = null;
                args = {};
                var modelToEdit = null;
                modelToEdit = {};
                modelToEdit["model"] = paymentMethodModel;
                args["argumentList"] = modelToEdit;
                _scEventUtils.fireEventToParent(
                this, "deletePayment", args);
            }
        },
		getPaymentTypeDisplay: function(
        dataValue, screen, widget, namespace, modelObject, options) {
            var paymentMethodModel = null;
            paymentMethodModel = _scScreenUtils.getModel(
            this, "PaymentMethod");
			
			// SUH-53 : Added to display the full gift card number on the payment confirmation page : BEGIN
			if (_scModelUtils.getStringValueFromPath("PaymentType" , paymentMethodModel ) === "GIFT_CARD" || 
					_scModelUtils.getStringValueFromPath("PaymentType" , paymentMethodModel ) === "ONLINE_GIFT_CARD") {
				console.log("PaymentType: ", "GIFT_CARD");
				var svcNo =  _scModelUtils.getStringValueFromPath("SvcNo" , paymentMethodModel );
				_scModelUtils.setStringValueAtModelPath("DisplayPrimaryAccountNo", svcNo, paymentMethodModel);
			}
			// SUH-53 : Added to display the full gift card number on the payment confirmation page : END
			
			if(!_scBaseUtils.equals(
					_scModelUtils.getStringValueFromPath("PaymentTypeGroup" , 
							paymentMethodModel ),"OTHER")){
				_scWidgetUtils.hideWidget(this, "lnkEdit", false);
			}
			
            var returnValue = null;
            returnValue = _isccsOrderUtils.getPaymentDescription(
            paymentMethodModel, this);
            var argArray = null;
            argArray = [];
            argArray.push(
            returnValue);
            var ariaLabelValueDel = null;
            ariaLabelValueDel = _scScreenUtils.getFormattedString(
            this, "arialabel_Del_paymentType_Link", argArray);
            var ariaLabelValueEdit = null;
            ariaLabelValueEdit = _scScreenUtils.getFormattedString(
            this, "arialabel_Edit_paymentType_Link", argArray);
            _scWidgetUtils.setLinkAriaLabel(
            this, "lnkDelete", ariaLabelValueDel);
            _scWidgetUtils.setLinkAriaLabel(
            this, "lnkEdit", ariaLabelValueEdit);
            return returnValue;
        },
        resumePaymentMethod: function(
        event, bEvent, ctrl, args) {
            var paymentMethodModel = null;
            paymentMethodModel = _scScreenUtils.getModel(
            this, "PaymentMethod");
			console.log("resumePaymentMethod paymentMethodModel: ", paymentMethodModel);
            var newPaymentMethodModel = null;
            newPaymentMethodModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethod");
            _scModelUtils.setStringValueAtModelPath("PaymentMethod.ResetSuspensionStatus", "Y", newPaymentMethodModel);
            _scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentKey", _scModelUtils.getStringValueFromPath("PaymentKey", paymentMethodModel), newPaymentMethodModel);
            // ARE-493 : Added to check that there is not already another active credit card on the order when resuming a credit card payment type : BEGIN
            _scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentType", _scModelUtils.getStringValueFromPath("PaymentType", paymentMethodModel), newPaymentMethodModel);
            // ARE-493 : Added to check that there is not already another active credit card on the order when resuming a credit card payment type : END
            args = {};
            var modelToResume = null;
            modelToResume = {};
            modelToResume["model"] = newPaymentMethodModel;
            args["argumentList"] = modelToResume;
            _scEventUtils.fireEventToParent(
            this, "resumePayment", args);
        },
});
});