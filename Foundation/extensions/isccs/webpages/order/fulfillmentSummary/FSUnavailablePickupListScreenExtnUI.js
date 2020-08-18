
scDefine(["dojo/text!./templates/FSUnavailablePickupListScreenExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!gridx/Grid","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/GridxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
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
			    _scButtonDataBinder
			 ,
			    _scGridxDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.fulfillmentSummary.FSUnavailablePickupListScreenExtnUI",
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
	  eventId: 'OLST_listGrid_afterPagingload'

,	  sequence: '51'

,	  description: 'selectAllRows'



,handler : {
methodName : "selectAllRows"

 
}
}

]
}

});
});


