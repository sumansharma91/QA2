


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/search/OrderSearchExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnOrderSearchExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.search.OrderSearchExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.search.OrderSearchExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getShipmentContainerList_RefID'
,
		 mashupId : 			'extn_getShipmentContainerList'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_getAJBSettlementList_RefID'
,
		 mashupId : 			'extn_getAJBSettlementList'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

