
scDefine(["scbase/loader!dojo/_base/declare",
	      "scbase/loader!extn/payment/inquiry/PaymentInquiryExtnUI",
		  "scbase/loader!isccs/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils"]
,
function(			 
			    _dojodeclare,
			    _extnPaymentInquiryExtnUI,
				_isccsWidgetUtils,
				_scModelUtils,
				_scScreenUtils,
				_scBaseUtils
){ 
	return _dojodeclare("extn.payment.inquiry.PaymentInquiryExtn", [_extnPaymentInquiryExtnUI],{
	// custom code here
	
        getDisplayPaymentType: function(grid, rowIndex, colIndex, modelObject, namespace) {

            var screen = null;
            screen = _isccsWidgetUtils.getOwnerScreen(grid);
            var inputArray = null;
            inputArray = [];
            var sDisplayPaymentType = null;
            sDisplayPaymentType = _scModelUtils.getStringValueFromPath("PaymentMethod.DisplayPaymentType", modelObject);			
            inputArray.push(sDisplayPaymentType);
            var sDisplayPaymentAccount = null;
            //sDisplayPaymentAccount = _scModelUtils.getStringValueFromPath("PaymentMethod.DisplayPaymentAccount", modelObject);
            sCreditCardNo = _scModelUtils.getStringValueFromPath("PaymentMethod.CreditCardNo", modelObject);

			var s = sCreditCardNo;
			s = sCreditCardNo.substr(0,6)+"******"+sCreditCardNo.substr(sCreditCardNo.length -4);
            inputArray.push(s);
            var returnValue = "";
            returnValue = _scScreenUtils.getFormattedString(screen, "Payment_Reference_Format", inputArray);
            return returnValue;
        }
});
});

