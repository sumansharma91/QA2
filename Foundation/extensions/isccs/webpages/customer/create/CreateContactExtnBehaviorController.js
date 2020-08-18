


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/customer/create/CreateContactExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCreateContactExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.customer.create.CreateContactExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.customer.create.CreateContactExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCustomerDetails'
,
		 mashupId : 			'manageContact_getCustomerDetails'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'extn_modifyCustomer_RefID'
,
		 mashupId : 			'modifyCustomer_manageCustomer'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

