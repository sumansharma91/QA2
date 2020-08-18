


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/customer/CustomerIdentificationExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerIdentificationExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.create.customer.CustomerIdentificationExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.create.customer.CustomerIdentificationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'MODIFY'
,
		 mashupId : 			'customerSearch_modifyFulfillmentOptions'
,
		 mashupRefId : 			'modifyFulfillmentOptions'

	},
		{
		 mashupRefId : 			'manageCustomer'
,
		 mashupId : 			'customerIdentification_manageCustomer'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'getCompleteCustomerList'
,
		 mashupId : 			'customerIdentification_getCompleteCustomerList'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

