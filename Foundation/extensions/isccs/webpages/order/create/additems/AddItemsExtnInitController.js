


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/order/create/additems/AddItemsExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAddItemsExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.order.create.additems.AddItemsExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.order.create.additems.AddItemsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			''
,
		 mashupRefId : 			'getCompleteOrderDetails'
,
		 sequence : 			''
,
		 mashupId : 			'additems_getCompleteOrderDetails'
,
		 callSequence : 			''
,
		 extnType : 			'MODIFY'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

