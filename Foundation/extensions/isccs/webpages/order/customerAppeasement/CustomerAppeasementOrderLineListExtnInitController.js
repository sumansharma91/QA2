


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/customerAppeasement/CustomerAppeasementOrderLineListExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerAppeasementOrderLineListExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementOrderLineListExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.customerAppeasement.CustomerAppeasementOrderLineListExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getCompleteOrderLineList_Init'
,
		 sequence : 			''
,
		 mashupId : 			'customerAppeasement_getCompleteOrderLineList'
,
		 callSequence : 			''
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

