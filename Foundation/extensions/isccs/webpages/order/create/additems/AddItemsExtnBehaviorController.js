


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/additems/AddItemsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddItemsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.create.additems.AddItemsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.create.additems.AddItemsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCompleteItemList'
,
		 mashupId : 			'additems_getCompleteItemList'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'modifyFulfillmentOptions'
,
		 mashupId : 			'additems_modifyFulfillmentOptions'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

