


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/home/portlets/OrderPortletExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnOrderPortletExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.home.portlets.OrderPortletExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.home.portlets.OrderPortletExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getOrderList'
,
		 mashupId : 			'orderPortletList_getOrderList'
,
		 extnType : 			'MODIFY'

	}
,
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

