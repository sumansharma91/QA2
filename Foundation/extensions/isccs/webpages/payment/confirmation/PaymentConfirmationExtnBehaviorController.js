


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/payment/confirmation/PaymentConfirmationExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPaymentConfirmationExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.payment.confirmation.PaymentConfirmationExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.payment.confirmation.PaymentConfirmationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[

	 		{
		 mashupRefId : 			'extn_getProperty_referenceid'
,
		 mashupId : 			'extn_getProperty'
,
		 extnType : 			'ADD'

	}
,
{
	 extnType : 			'ADD'
,
	 mashupId : 			'extn_paymentconfirmgetOrderList'
,
	 mashupRefId : 			'extn_payconfirmgetOrderList'

},

{
	 extnType : 			'MODIFY'
,
	 mashupId : 			'modifyCustomer_manageCustomer'
,
	 mashupRefId : 			'manageCustomer'

},
{
	 mashupRefId : 			'getCompleteOrderDetails'
,
	 mashupId : 			'paymentConfirmation_getCompleteOrderDetails'
,
	 extnType : 			'MODIFY'

}
,
	 		{
		 mashupRefId : 			'extn_applyCoupon_referenceid'
,
		 mashupId : 			'extn_applyCoupon'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_changeOrder_referenceid'
,
		 mashupId : 			'extn_changeOrder'
,
		 extnType : 			'ADD'

	}
,
		{
		mashupRefId : 			'extn_changeOrderLine'
,
		 mashupId : 			'extn_changeOrderLineToAdvanceReturns'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_ChangePaymentMethod_IssueeGiftCard'
,
		 mashupId : 			'extn_ReturnOrderLines_SuspendAndAddeGiftCard'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'capturePayment'
,
		 mashupId : 			'extn_paymentConfirmation_capturePayment'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'extn_getCouponDetails_payment'
,
		 mashupId : 			'extn_getCouponDetails'
,
		 extnType : 			'ADD'

	}	

	]

}
);
});

