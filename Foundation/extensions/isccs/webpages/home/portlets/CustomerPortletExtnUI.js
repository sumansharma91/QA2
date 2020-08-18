
scDefine(["dojo/text!./templates/CustomerPortletExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/RadioButtonSet","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxFilteringSelect
			 ,
			    _idxRadioButtonSet
			 ,
			    _idxTextBox
			 ,
			    _scplat
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scRadioSetDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLink
){
return _dojodeclare("extn.home.portlets.CustomerPortletExtnUI",
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

,	  sequence: '56'




,handler : {
methodName : "initScreen"

 
}
},
{
	  eventId: 'extn_textfield_HealthyAwards_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "customerSearchOnEnter"


}
}

]
}

});
});


