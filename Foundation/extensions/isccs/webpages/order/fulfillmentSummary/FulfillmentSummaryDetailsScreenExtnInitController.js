


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/fulfillmentSummary/FulfillmentSummaryDetailsScreenExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnFulfillmentSummaryDetailsScreenExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getFulfillmentSummaryDetails'
,
		 sequence : 			''
,
		 mashupId : 			'fulfillmentSummary_getFulfillmentSummaryDetails'
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

