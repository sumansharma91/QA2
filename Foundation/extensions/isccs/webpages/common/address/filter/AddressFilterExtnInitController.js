


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/address/filter/AddressFilterExtn","scbase/loader!sc/plat/dojo/controller/IdentifierController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddressFilterExtn
			 ,
			    _scIdentifierController
){

return _dojodeclare("extn.common.address.filter.AddressFilterExtnInitController", 
				[_scIdentifierController], {

			
			 screenId : 			'extn.common.address.filter.AddressFilterExtn'
,
			 className : 			'AddressFilterExtn'
,
			 identifierTemplatesRootPath : 			'extn.common.address.filter.identifiers'
,
			 baseTemplateFolder : 			'extn.common.address.filter.templates'

			
			
			
}
);
});

