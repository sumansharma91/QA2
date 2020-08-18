
scDefine(["dojo/text!./templates/CustomerIdentificationExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxTextBox
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLink
){
return _dojodeclare("extn.order.create.customer.CustomerIdentificationExtnUI",
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
methodName : "extnInit"

 
}
},
{
	  eventId: 'extn_textfieldHealthAwardNoCustomerIdentification_onKeyUp'

,	  sequence: '52'




,handler : {
methodName : "SST_invokeApiOnEnter"


}
}
,
{
	  eventId: 'extn_taxExamptUpdated'

,	  sequence: '51'

,	  description: 'handleTaxExemptUpdate'



,handler : {
methodName : "handleTaxExemptUpdate"

 
}
}
,
{
	  eventId: 'extn_shipPreferenceUpdated'

,	  sequence: '51'

,	  description: 'handleShipPreferenceUpdate'



,handler : {
methodName : "handleShipPreferenceUpdate"

 
}
}

]
}

});
});
