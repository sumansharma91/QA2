
scDefine(["dojo/text!./templates/AddressCaptureExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxCheckBox
			 ,
			    _idxFilteringSelect
			 ,
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scComboDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.common.address.capture.AddressCaptureExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
		],
		sourceBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_1'
						,
	  description: "extn_QASRefineResponse"
						,
	  value: 'extn_QASRefineResponse'
						
			}
			
		]
	}

	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'txtZipCode_onBlur'

,	  sequence: '51'




,handler : {
methodName : ""

 
}
}
,
{
	  eventId: 'afterScreenLoad'

,	  sequence: '51'

,	  description: 'This method is used to focus on zip code text box.'



,handler : {
methodName : "extnOnFocusZipCode"

 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '51'

,	  description: 'This method is used to focus on zip code text box.'



,handler : {
methodName : "extnOnFocusZipCode"

 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '52'




,handler : {
methodName : "changeAddressOnLoad"

 
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'

,	  description: 'This method will handle the QAS Web Service Output at UI.'



,handler : {
methodName : "onExtnMashupCompletion"

 
}
}
,
{
	  eventId: 'extn_addressLine1_onBlur'

,	  sequence: '51'

,	  description: 'on blur'



,handler : {
methodName : ""

 
}
}
,
{
	  eventId: 'extn_addressLine1_onChange'

,	  sequence: '51'

,	  description: 'Extn On Change '



,handler : {
methodName : "extnOnChangeFS"

 
}
},
{
	  eventId: 'extn_addressLine1_onKeyUp'

,	  sequence: '51'

,	  description: 'Extn On Key Up '



,handler : {
methodName : "onKeyUp_extnAddressLine1"

 
}
}
,
{
	  eventId: 'txtAddressLine1_onBlur'

,	  sequence: '51'




,handler : {
methodName : ""

 
}
}

]
}

});
});


