
scDefine(["dojo/text!./templates/PaymentCaptureExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxCheckBox
			 ,
			    _idxFilteringSelect
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scComboDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtnUI",
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

,	  description: 'getCommonCode'



,handler : {
methodName : "getCommonCode"

 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '52'




,handler : {
methodName : "extn_init"

 
}
}

]
}

});
});


