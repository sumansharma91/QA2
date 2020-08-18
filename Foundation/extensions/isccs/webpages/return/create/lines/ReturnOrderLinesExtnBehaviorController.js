


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/return/create/lines/ReturnOrderLinesExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnReturnOrderLinesExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.return.create.lines.ReturnOrderLinesExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.return.create.lines.ReturnOrderLinesExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getCommonCode_referenceid'
,
		 mashupId : 			'extn_getCommonCode'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_massageOutput_ProcessReturnOrder'
,
		 mashupId : 			'processReturnOrder_massageOutput'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

