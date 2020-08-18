


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/customer/create/address/ManageCustomerAddressesExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnManageCustomerAddressesExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.customer.create.address.ManageCustomerAddressesExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.customer.create.address.ManageCustomerAddressesExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCustomerDetails'
,
		 mashupId : 			'manageCustomerAddresses_getCustomerDetails'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'manageCustomer'
,
		 mashupId : 			'modifyCustomer_manageCustomer'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

