


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/address/capture/AddressCaptureExtn","scbase/loader!sc/plat/dojo/controller/IdentifierController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddressCaptureExtn
			 ,
			    _scIdentifierController
){

return _dojodeclare("extn.common.address.capture.AddressCaptureExtnInitController", 
				[_scIdentifierController], {

			
			 screenId : 			'extn.common.address.capture.AddressCaptureExtn'
,
			 className : 			'AddressCaptureExtn'
,
			 identifierTemplatesRootPath : 			'extn.common.address.capture.identifiers'
,
			 baseTemplateFolder : 			'extn.common.address.capture.templates'

			
			
			
}
);
});

