


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/shipment/summary/ShipmentRTExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentRTExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.components.shipment.summary.ShipmentRTExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.components.shipment.summary.ShipmentRTExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_Print_Customer_Receipt'
,
		 mashupId : 			'extn_Print_Customer_Receipt'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_printpickpackreceipt'
,
		 mashupId : 			'extn_printpickpackreceipt'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

