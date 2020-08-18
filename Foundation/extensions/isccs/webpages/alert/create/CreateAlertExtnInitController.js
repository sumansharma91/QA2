


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/alert/create/CreateAlertExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCreateAlertExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.alert.create.CreateAlertExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.alert.create.CreateAlertExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_exceptionType'
,
		 mashupRefId : 			'extn_ExceptionTypeList'
,
		 sequence : 			''
,
		 mashupId : 			'getCommonCodeList_exceptionType'
,
		 callSequence : 			''
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

