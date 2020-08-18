
scDefine(["dojo/text!./templates/CreateContactExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/DateTextBox","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/RadioButtonSet","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/DateDataBinder","scbase/loader!sc/plat/dojo/binding/RadioSetDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxDateTextBox
			 ,
			    _idxFilteringSelect
			 ,
			    _idxRadioButtonSet
			 ,
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scDateDataBinder
			 ,
			    _scRadioSetDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLink
){
return _dojodeclare("extn.customer.create.CreateContactExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_billToAddress_input'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						
			}
			
		],
		sourceBindingNamespaces :
		[
			{
	  value: 'extn_getCountryList'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_9'
						
			}
			,
			{
	  value: 'extn_ShipToAddress'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_10'
						
			}
			,
			{
	  value: 'extn_BillToAddress'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_11'
						
			}
			,
			{
	  description: "GenderList"
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_12'
						,
	  value: 'extn_getGenderList_output'
						
			}
			,
			{
	  value: 'extn_getCarrierPreferenceList_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_13'
						
			}
			,
			{
	  value: 'extn_get_OrganizationList_ouput'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_14'
						
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
	  eventId: 'afterScreenInit'

,	  sequence: '51'




,handler : {
methodName : "extn_init"

 
}
}
,
{
	  eventId: 'extn_checkboxBillTo_onChange'

,	  sequence: '51'




,handler : {
methodName : "showBillToAddress"

 
}
}
,
{
	  eventId: 'txtMobilePhone_onBlur'

,	  sequence: '51'




,handler : {
methodName : "validateMobilePhone"

 
}
}
,
{
	  eventId: 'txtDayPhone_onBlur'

,	  sequence: '51'




,handler : {
methodName : "validateDayPhone"

 
}
}
,
{
	  eventId: 'txtEveningPhone_onBlur'

,	  sequence: '51'




,handler : {
methodName : "validateEveningPhone"

 
}
}
,
{
	  eventId: 'extn_AddAddress_onClick'

,	  sequence: '51'

,	  description: 'openEditAddress'



,handler : {
methodName : "openEditAddress"

 
}
}
,
{
	  eventId: 'extn_EditAddress_onClick'

,	  sequence: '51'

,	  description: 'openEditAddress'



,handler : {
methodName : "openEditAddress"

 
}
}
,
{
	  eventId: 'extn_AddAddressLink_onClick'

,	  sequence: '51'

,	  description: 'openEditAddress'



,handler : {
methodName : "openEditAddress"

 
}
}
,
{
	  eventId: 'extn_EditAddressLink_onClick'

,	  sequence: '51'

,	  description: 'openEditAddress'



,handler : {
methodName : "openEditAddress"

 
}
}

]
}

});
});


