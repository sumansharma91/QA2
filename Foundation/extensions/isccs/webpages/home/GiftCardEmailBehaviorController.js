scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!sc/plat/dojo/controller/ServerDataController"], 
function(
	_dojodeclare,
	scServerDataController
){
	return _dojodeclare(
		"extn.home.GiftCardEmailBehaviorController", [scServerDataController], {
		screenId : 	'extn.home.GiftCardEmail',
		mashupRefs: [{
            mashupId: 'extn_triggerEmailOnVGCRefund', 
            mashupRefId: 'extn_triggerEmailOnVGCRefund_Ref_Id'
        }]
	});
});

