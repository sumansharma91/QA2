


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/customerAppeasement/CustomerAppeasementOptionsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerAppeasementOptionsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementOptionsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.customerAppeasement.CustomerAppeasementOptionsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_VSICreateAppeasementApprovalAlert'
,
		 mashupId : 			'extn_VSICreateAppeasementApprovalAlert'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_VSIIssueEGiftCardForAppeasementsSyncService'
,
		 mashupId : 			'extn_VSIIssueEGiftCardForAppeasementsSyncService'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

