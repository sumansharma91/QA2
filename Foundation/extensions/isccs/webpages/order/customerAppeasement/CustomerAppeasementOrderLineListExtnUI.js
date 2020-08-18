
scDefine(["dojo/text!./templates/CustomerAppeasementOrderLineListExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!gridx/Grid","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/GridxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _gridxGrid
			 ,
			    _scplat
			 ,
			    _scGridxDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementOrderLineListExtnUI",
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
	  eventId: 'afterScreenInit'

,	  sequence: '51'

,	  description: 'This method will store the Customer Email ID to Context on Page Load.'



,handler : {
methodName : "storeCustomerEmailIDToContext"

 
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'

,	  description: 'extnMashupCompletion'



,handler : {
methodName : "extnMashupCompletion"

 
}
}

]
}

});
});


