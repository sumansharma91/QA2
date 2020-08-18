
scDefine(["dojo/text!./templates/LineTrackingExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _scplat
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.shipment.shipmentTracking.LineTrackingExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  description: "The order line containing ChainedFromOrderLineKey as input to getCompleteOrderLineList to get the transfer order line"
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_0'
						,
	  value: 'extn_TransferOrderLine'
						
			}
			,
			{
	  description: "Output from getCompleteOrderLineList"
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_1'
						,
	  value: 'extn_orderLineList_output'
						
			}
			,
			{
	  description: "The original sale order line with OrderLineKey"
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						,
	  value: 'extn_SalesOrderLine'
						
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

]
}

});
});


