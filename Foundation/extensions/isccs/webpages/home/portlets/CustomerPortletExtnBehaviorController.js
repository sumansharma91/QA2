


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/home/portlets/CustomerPortletExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerPortletExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.home.portlets.CustomerPortletExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.home.portlets.CustomerPortletExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'getCustomerList'
,
		 mashupId : 			'home_getCustomerList'
,
		 extnType : 			'MODIFY'

	}

	]

}
);
});

