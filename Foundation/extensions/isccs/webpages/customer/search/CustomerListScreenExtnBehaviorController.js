


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/customer/search/CustomerListScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerListScreenExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.customer.search.CustomerListScreenExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.customer.search.CustomerListScreenExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCustomerList'
,
		 mashupId : 			'customerSearch_getCustomerList'
,
		 extnType : 			''

	}

	]

}
);
});

