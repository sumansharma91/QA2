
scDefine(["dojo/text!./templates/StoreSelectionExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!gridx/Grid","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/GridxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scGridxDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.common.alternateStore.StoreSelectionExtnUI",
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
	  eventId: 'listStore_Link_ScGetLinkText'

,	  sequence: '51'

,	  description: 'getLinkText'



,handler : {
methodName : "getLinkText"

 
}
}
,
{
	  eventId: 'listStore_Link_ScHandleLinkClicked'

,	  sequence: '51'

,	  description: 'handleLinkClicked'



,handler : {
methodName : "handleLinkClicked"

 
}
}

]
}

});
});


