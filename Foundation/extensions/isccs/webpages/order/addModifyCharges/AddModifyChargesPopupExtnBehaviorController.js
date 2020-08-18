


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/addModifyCharges/AddModifyChargesPopupExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddModifyChargesPopupExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.order.addModifyCharges.AddModifyChargesPopupExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.order.addModifyCharges.AddModifyChargesPopupExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCompleteOrderDetails'
,
		 mashupId : 			'addModifyCharges_getCompleteOrderDetails'
,
		 extnType : 			'MODIFY'

	}
,
	 		{
		 mashupRefId : 			'extn_prorateLineCharges'
,
		 mashupId : 			'extn_prorateLineCharges'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_VSICreateAppeasementApprovalAlert'
,
		 mashupId : 			'extn_VSICreateAppeasementApprovalAlert'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

