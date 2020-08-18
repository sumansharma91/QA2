


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/common/notes/CustomerNoteDisplayExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerNoteDisplayExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.common.notes.CustomerNoteDisplayExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.common.notes.CustomerNoteDisplayExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'MODIFY'
,
		 mashupId : 			'customerNoteCapture_getNoteList'
,
		 mashupRefId : 			'getNoteList'

	}
,
	 		{
		 extnType : 			'MODIFY'
,
		 mashupId : 			'customerIdentification_manageCustomer'
,
		 mashupRefId : 			'saveNote'

	}
,
	 		{
		 extnType : 			'MODIFY'
,
		 mashupId : 			'customerIdentification_manageCustomer'
,
		 mashupRefId : 			'deleteNote'

	}

	]

}
);
});

