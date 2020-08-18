
scDefine(["dojo/text!./templates/HomeExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!extn/home/portlets/CouponPortletInitController","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/ControllerWidget","scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget"]
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
			    _extnCouponPortletInitController
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scBaseUtils
			 ,
			    _scControllerWidget
			 ,
			    _scIdentifierControllerWidget
){
return _dojodeclare("extn.home.HomeExtnUI",
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
	  eventId: 'afterScreenLoad'

,	  sequence: '19'

,handler : {
methodName : "getTNSSession" 
}
}

]
}

});
});


