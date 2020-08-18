


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/details/OrderSummaryLinesExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnOrderSummaryLinesExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.details.OrderSummaryLinesExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.details.OrderSummaryLinesExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCompleteOrderLineList'
,
		 mashupId : 			'OrderSummaryLines_getCompleteOrderLineList'
,
		 extnType : 			''

	}
,
	 		{
		 mashupRefId : 			'extn_getSTSTrackingNo'
,
		 mashupId : 			'extn_getSTSTrackingNo'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

