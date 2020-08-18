


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/alternateStore/StoreSelectionExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnStoreSelectionExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.alternateStore.StoreSelectionExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.alternateStore.StoreSelectionExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getFulfillmentOptionForLines'
,
		 mashupId : 			'extn_getFulfillmentOptionForLines'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

