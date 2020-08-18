


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/fulfillmentSummary/FulfillmentSummaryDetailsScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnFulfillmentSummaryDetailsScreenExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_getOrderFulfillmentDetails'
,
		 mashupRefId : 			'extn_getOrderFulfillmentDetails_referenceid'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_vsiPromiseDate'
,
		 mashupRefId : 			'extn_vsiPromiseDate_referenceid'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_changeOrder'
,
		 mashupRefId : 			'extn_changeOrder_referenceid'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_applyBOGO'
,
		 mashupRefId : 			'extn_applyBOGO_referenceid'

	}

	]

}
);
});

