
scDefine(["dojo/text!./templates/AddItemsExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/DateTextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/DateDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxDateTextBox
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scDateDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.create.additems.AddItemsExtnUI",
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
	  eventId: 'extn_datetextbox_PromotionCodeDateOverride_onChange'

,	  sequence: '51'




,handler : {
methodName : "onPromoOverrideDateChange"

 
}
}

]
}

});
});


