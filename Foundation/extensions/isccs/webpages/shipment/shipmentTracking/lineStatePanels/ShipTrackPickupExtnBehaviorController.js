


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/shipmentTracking/lineStatePanels/ShipTrackPickupExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipTrackPickupExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.shipmentTracking.lineStatePanels.ShipTrackPickupExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.shipmentTracking.lineStatePanels.ShipTrackPickupExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getOrderLineList'
,
		 mashupId : 			'lineTrack_getOrderLineList'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

