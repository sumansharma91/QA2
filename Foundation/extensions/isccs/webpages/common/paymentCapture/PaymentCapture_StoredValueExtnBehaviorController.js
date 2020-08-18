


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/paymentCapture/PaymentCapture_StoredValueExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPaymentCapture_StoredValueExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.paymentCapture.PaymentCapture_StoredValueExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.paymentCapture.PaymentCapture_StoredValueExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_VSICheckGCBalance_RefID'
,
		 mashupId : 			'extn_giftCard'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

