


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/address/AddressEntryExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddressEntryExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.create.address.AddressEntryExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.create.address.AddressEntryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_changeOrderHeader_CallTag'
,
		 mashupId : 			'extn_changeOrderHeaderToGenerateTag'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

