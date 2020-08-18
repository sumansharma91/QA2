


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/address/capture/AddressCapturePageExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddressCapturePageExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.address.capture.AddressCapturePageExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.address.capture.AddressCapturePageExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'MODIFY'
,
		 mashupId : 			'selectOrEditAddress_getCompleteOrderDetails'
,
		 mashupRefId : 			'getPersonInfoList'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupId : 			'customerIdentification_getCompleteCustomerDetails'
,
		 mashupRefId : 			'extn_getShipTo'

	}

	]

}
);
});

