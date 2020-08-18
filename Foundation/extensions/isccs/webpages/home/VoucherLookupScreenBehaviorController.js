scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!sc/plat/dojo/controller/ServerDataController"], 
function(
	_dojodeclare,
	scServerDataController
){
	return _dojodeclare(
		"extn.home.VoucherLookupScreenBehaviorController", [scServerDataController], {
		screenId : 	'extn.home.VoucherLookupScreen',
		mashupRefs: [{
             mashupRefId : 			'extn_VSICheckVoucher_RefID'
,
		 mashupId : 			'extn_checkVoucher'
,
		 extnType : 			'ADD'
        }]
	});
});

