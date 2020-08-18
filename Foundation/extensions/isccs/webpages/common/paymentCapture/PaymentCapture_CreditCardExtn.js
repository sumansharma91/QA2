
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/common/paymentCapture/PaymentCapture_CreditCardExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/CustomerUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/PADSSUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
		  ]
,
function( _dojodeclare,
	      _extnPaymentCapture_CreditCardExtnUI,
		  _isccsBaseTemplateUtils, 
		  _isccsCustomerUtils, 
		  _isccsOrderUtils, 
		  _isccsUIUtils, 
		  _scBaseUtils, 
		  _scModelUtils, 
		  _scPADSSUtils, 
		  _scScreenUtils, 
		  _scWidgetUtils
		  
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCapture_CreditCardExtn", [_extnPaymentCapture_CreditCardExtnUI],{
	// custom code here
});
});

