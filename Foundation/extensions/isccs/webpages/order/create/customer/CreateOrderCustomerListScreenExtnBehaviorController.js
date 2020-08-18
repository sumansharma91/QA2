


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/customer/CreateOrderCustomerListScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCreateOrderCustomerListScreenExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.create.customer.CreateOrderCustomerListScreenExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.create.customer.CreateOrderCustomerListScreenExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCustomerList'
,
		 mashupId : 			'customerIdentification_getCustomerList'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

