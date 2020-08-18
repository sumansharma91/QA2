
scDefine(["dojo/text!./templates/CustomerSearchExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/RadioButtonSet","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scComboDataBinder
			 ,
			    _scRadioSetDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.customer.search.CustomerSearchExtnUI",
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




,handler : {
methodName : "hideValue"

 
}
}
,
{
	  eventId: 'extn_textfield_HealthyAwardsAdvanced_onKeyUp',
	  sequence: '25',
      listeningControlUId: 'extn_textfield_HealthyAwardsAdvanced',
      handler: {
          methodName: "SST_invokeApiOnEnter"
      }
}

]

}
});
});


