
scDefine(["dojo/text!./templates/PaymentConfirmationExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/DateTextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/DateDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
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
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scDateDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.payment.confirmation.PaymentConfirmationExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	


, staticBindings : [

		{
				
				

			 	targetBinding : 	{
		 path : 			'Order.OrderHeaderKey'
,
		 namespace : 			'extn_changeOrderLineToAdvanceReturns_input'

	}
,			 
			 	sourceBinding : 	{
		 path : 			'Order.OrderHeaderKey'
,
		 namespace : 			'extn_ScreenInput'

	}
			 
		
		}	
	]
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_changeOrderLineToAdvanceReturns_input'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_3'
						,
	  description: "This is the Input for calling the Service"
						
			}
			
		],
		sourceBindingNamespaces :
		[
			{
	  value: 'extn_ScreenInput'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_13'
						,
	  description: "This is the value for Advance Returns on Return Order"
						
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
	  eventId: 'afterScreenLoad'

,	  sequence: '51'




,handler : {
methodName : "checkMigratedOrderFlag"

 
}
}
,
{
	  eventId: 'extn_Advance_returns_onClick'

,	  sequence: '51'




,handler : {
methodName : "manageAdvanceReturn"

 
}
}
,
{
	  eventId: 'extn_issueRefundToEGiftCard_onClick'

,	  sequence: '51'

,	  description: 'This method is to issue a gift card to customer for the Order'



,handler : {
methodName : "onEGiftCardButtonClick"

 
}
}
,
{
	  eventId: 'extn_find_coupon_payment_onClick'

,	  sequence: '51'




,handler : {
methodName : "findCoupon"

 
}
}

]
}

});
});


