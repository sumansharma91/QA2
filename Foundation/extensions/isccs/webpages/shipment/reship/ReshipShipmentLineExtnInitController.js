


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/reship/ReshipShipmentLineExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnReshipShipmentLineExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.shipment.reship.ReshipShipmentLineExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.shipment.reship.ReshipShipmentLineExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_getTrackingNo_CommonCode'
,
		 mashupRefId : 			'extn_trackingNoCommonCode_RefID'
,
		 sequence : 			''
,
		 mashupId : 			'extn_TrackingNo_CommonCode'
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

