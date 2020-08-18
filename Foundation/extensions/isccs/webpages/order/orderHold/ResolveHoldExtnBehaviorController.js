


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/orderHold/ResolveHoldExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnResolveHoldExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.orderHold.ResolveHoldExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.orderHold.ResolveHoldExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getOrderList_RefID'
,
		 mashupId : 			'extn_getOrderList'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

