


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/reship/ReshipShipmentLineExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnReshipShipmentLineExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.reship.ReshipShipmentLineExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.reship.ReshipShipmentLineExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'reshipOrderLines'
,
		 mashupId : 			'reshipProduct_reshipOrderLines'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_changeOrderLineCarrier'
,
		 mashupRefId : 			'extn_changeCarrieronReshipOrderLines'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'extn_reshipShipmentLine_getCarrierServiceOptionsForOrdering'
,
		 mashupRefId : 			'extn_getCarrierServiceOptionsForOrdering'

	}

	]

}
);
});

