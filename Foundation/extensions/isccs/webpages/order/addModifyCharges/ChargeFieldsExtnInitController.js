


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/addModifyCharges/ChargeFieldsExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnChargeFieldsExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.addModifyCharges.ChargeFieldsExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.addModifyCharges.ChargeFieldsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			'5'
,
		 mashupId : 			'order_getChargeCategoryList'
,
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getChargeCategoryList'
,
		 extnType : 			'MODIFY'
,
		 callSequence : 			'1'

	}
,
	 		{
		 sourceBindingOptions : 			''
,
		 sequence : 			''
,
		 mashupId : 			'extn_TrackingNo_CommonCode'
,
		 sourceNamespace : 			'extn_TrackingNo_CommonCode_Output'
,
		 mashupRefId : 			'extn_getTrackingNo_CommonCode'
,
		 extnType : 			'ADD'
,
		 callSequence : 			''

	}

	]

}
);
});

