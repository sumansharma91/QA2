
scDefine(["dojo/text!./templates/CustomerDetailsExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!extn/customer/loyalty/CustomerLoyaltyDetailsInitController","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/ControllerWidget","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget"]
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
			    _extnCustomerLoyaltyDetailsInitController
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scAdvancedTableLayout
			 ,
			    _scBaseUtils
			 ,
			    _scControllerWidget
			 ,
			    _scDataLabel
			 ,
			    _scIdentifierControllerWidget
){
return _dojodeclare("extn.customer.details.CustomerDetailsExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	
	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

{
	  eventId: 'afterScreenInit'

,	  sequence: '51'




,handler : {
methodName : "extn_init"

 
}
}
,
{
	  eventId: 'extn_pnlLoyaltyDetails_onShow'

,	  sequence: '51'




,handler : {
methodName : "extn_loadLoyaltyData"

 
}
}

]
}

});
});


