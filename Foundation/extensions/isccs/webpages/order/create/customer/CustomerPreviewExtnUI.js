
scDefine(["dojo/text!./templates/CustomerPreviewExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxTextBox
			 ,
			    _scplat
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
			 ,
			    _scLink
){
return _dojodeclare("extn.order.create.customer.CustomerPreviewExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_1'
						,
	  description: "Extended model to capture input"
						,
	  value: 'extn_selectedRow_input'
						
			}
			
		],
		sourceBindingNamespaces :
		[
			{
	  value: 'extn_getOrgList_Output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						
			}
			,
			{
	  value: 'extn_getCompleteCustomerDetails_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_8'
						,
	  description: "customer details additional adresses"
						
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
	  eventId: 'extn_filteringselect_ShipPreference_onBlur'

,	  sequence: '51'

,	  description: 'updateShipPreference'



,handler : {
methodName : "updateShipPreference"

 
}
}
,
{
	  eventId: 'extn_textfield_Tax_Exempt_onChange'

,	  sequence: '51'




,handler : {
methodName : "updateTaxExamptID"

 
}
}
,
{
	  eventId: 'extn_link_loyaltyDetails_onClick'

,	  sequence: '51'




,handler : {
methodName : "showLoyaltyDetails"

 
}
}

]
}

});
});


