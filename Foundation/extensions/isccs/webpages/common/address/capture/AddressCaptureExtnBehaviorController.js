


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/address/capture/AddressCaptureExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddressCaptureExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.address.capture.AddressCaptureExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.address.capture.AddressCaptureExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_Address_Capture_Verification_Mashup_RefId'
,
		 mashupId : 			'extn_Address_Capture_Verification_Mashup_Id'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

