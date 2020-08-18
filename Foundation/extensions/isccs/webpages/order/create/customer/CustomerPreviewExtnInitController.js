


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/customer/CustomerPreviewExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerPreviewExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.create.customer.CustomerPreviewExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.create.customer.CustomerPreviewExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_getOrgList_Output'
,
		 mashupRefId : 			'extn_getCarrierListRefID'
,
		 sequence : 			''
,
		 mashupId : 			'getCarrierList'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	},
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			'2'
,
		 mashupId : 			'customerIdentification_getCompleteCustomerDetails'
,
		 sourceNamespace : 			'Customer'
,
		 mashupRefId : 			'getCompleteCustomerDetails'
,
		 extnType : 			'ADD'
,
		 callSequence : 			'2'

	}
,
	 		{
		 sourceNamespace : 			'extn_getCompleteCustomerDetails_output'
,
		 mashupRefId : 			'extn_getCompleteCustomerDetails'
,
		 sequence : 			''
,
		 mashupId : 			'customerIdentification_getCompleteCustomerDetails'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

