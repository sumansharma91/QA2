


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/paymentCapture/PaymentCaptureExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPaymentCaptureExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.common.paymentCapture.PaymentCaptureExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getPaymentTypeListMashup'
,
		 sequence : 			'5'
,
		 mashupId : 			'paymentCapturePanel_getPaymentTypeList'
,
		 callSequence : 			''
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}
,
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getCustomerPaymentMethodList'
,
		 sequence : 			'5'
,
		 mashupId : 			'paymentCapture_getCustomerPaymentMethodList'
,
		 callSequence : 			''
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

