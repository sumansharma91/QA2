
scDefine(["dojo/text!./templates/OrderSearchExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/DateTextBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/DateDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxDateTextBox
			 ,
			    _idxFilteringSelect
			 ,
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scComboDataBinder
			 ,
			    _scDateDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.search.OrderSearchExtnUI",
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
	  eventId: 'extn_textfieldHAN_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
}
}
,
{
	  eventId: 'extn_textfield_PaymentTechOrder_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
}
}
,
{
	  eventId: 'extn_textfield_TrackingNo_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"

 
}
},
{
	  eventId: 'extn_textfield_Marketplace_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "SST_invokeApiOnEnter"


}
}

]
}

});
});


