
scDefine(["dojo/text!./templates/AddressEntryExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.order.create.address.AddressEntryExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_1'
						,
	  description: "To hold the value of Generate Call Tag Attribute"
						,
	  value: 'extn_changeOrderHeaderToGenerateTag_Input'
						
			}
			
		],
		sourceBindingNamespaces :
		[
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
	  eventId: 'extn_Generate_Call_Tag_Alert_onClick'

,	  sequence: '51'

,	  description: 'This method Calls Change Order to update the Order Header for generating a Call Tag'



,handler : {
methodName : "manageGenerateCallTagAlert"

 
}
}

]
}

});
});


