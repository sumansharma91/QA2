


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/shipmentTracking/ShipmentTrackingExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentTrackingExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.shipmentTracking.ShipmentTrackingExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.shipmentTracking.ShipmentTrackingExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCompleteOrderLineList'
,
		 mashupId : 			'ShipmentTracking_getCompleteOrderLineList'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

