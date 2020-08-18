
scDefine(["dojo/text!./templates/ReshipShipmentLineExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/TextBox","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.shipment.reship.ReshipShipmentLineExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_4'
						,
	  description: "This holds the carrierServiceCode of reship line"
						,
	  value: 'extn_changeOrderLineCarrier_input'
						
			}
			
		],
		sourceBindingNamespaces :
		[
			{
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_6'
						,
	  description: "This is to hold Carrier Service data for Ordering"
						,
	  value: 'extn_getCarrierServiceOptionsForOrdering_output'
						
			}
			,
			{
	  value: 'extn_levelOfService_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						,
	  description: "This is used to show the default Level of Service on Screen"
						
			}
			,
			{
	  value: 'extn_getTrackingNo_CommonCode'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_8'
						
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
	  eventId: 'cmb_reshipReason_onChange'

,	  sequence: '51'




,handler : {
methodName : "showReshipReasonCode"

 
}
}

]
}

});
});


