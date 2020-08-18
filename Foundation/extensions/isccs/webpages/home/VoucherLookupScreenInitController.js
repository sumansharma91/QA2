scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!sc/plat/dojo/controller/ScreenController"],
function(			 
	_dojodeclare,
    scScreenController
){
	return _dojodeclare(
		"extn.home.HomeExtnInitController", [scScreenController], {
		 screenId : 'extn.home.VoucherLookupScreen'
	});
});


