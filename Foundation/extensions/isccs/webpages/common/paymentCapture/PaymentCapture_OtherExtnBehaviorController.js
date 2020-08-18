


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/paymentCapture/PaymentCapture_OtherExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPaymentCapture_OtherExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.paymentCapture.PaymentCapture_OtherExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.paymentCapture.PaymentCapture_OtherExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_VSICheckVoucher_RefID'
,
		 mashupId : 			'extn_checkVoucher'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

