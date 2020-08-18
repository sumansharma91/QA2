


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/customer/create/CreateContactExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCreateContactExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.customer.create.CreateContactExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.customer.create.CreateContactExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			''
,
		 mashupId : 			'common_getCountryList'
,
		 sourceNamespace : 			'extn_getCountryList'
,
		 mashupRefId : 			'extn_getCountryListRefID'
,
		 extnType : 			'ADD'
,
		 callSequence : 			''

	}
,
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			''
,
		 mashupId : 			'extn_getGenderList'
,
		 sourceNamespace : 			'extn_getGenderList_output'
,
		 mashupRefId : 			'extn_getGenderListRefID'
,
		 extnType : 			'ADD'
,
		 callSequence : 			''

	}
,
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			''
,
		 mashupId : 			'getCarrierList'
,
		 sourceNamespace : 			'extn_get_OrganizationList_ouput'
,
		 mashupRefId : 			'extn_getCarrierList'
,
		 extnType : 			'ADD'
,
		 callSequence : 			''

	}

	]

}
);
});

