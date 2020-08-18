


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/customerAppeasement/CustomerAppeasementSelectionExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerAppeasementSelectionExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementSelectionExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.customerAppeasement.CustomerAppeasementSelectionExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			''
,
		 mashupId : 			'extn_TrackingNo_CommonCode'
,
		 sourceNamespace : 			'extn_TrackingNumber_CommonCode'
,
		 mashupRefId : 			'extn_getTrackingNo_CommonCode_RefID'
,
		 extnType : 			'ADD'
,
		 callSequence : 			''

	}

	]

}
);
});

