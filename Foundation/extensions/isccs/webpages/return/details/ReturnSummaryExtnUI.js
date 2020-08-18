
scDefine(["dojo/text!./templates/ReturnSummaryExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _scplat
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.return.details.ReturnSummaryExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'afterScreenLoad'

,	  sequence: '51'




,handler : {
methodName : "dataMigration"

 
}
}
,
{
	  eventId: 'addressChanged'

,	  sequence: '51'




,handler : {
methodName : "handleAddressChange"

 
}
}

]
}

});
});


