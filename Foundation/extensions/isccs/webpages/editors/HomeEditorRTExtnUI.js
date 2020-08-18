
scDefine(["dojo/text!./templates/HomeEditorRTExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLink
){
return _dojodeclare("extn.editors.HomeEditorRTExtnUI",
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
	  eventId: 'extn_linkSendGiftCardEmail_onClick'

,	  sequence: '51'




,handler : {
methodName : "giftCardEmailScreen"

 
}
}
,
{
	  eventId: 'extn_Return_Acknowledgement_onClick_onClick'

,	  sequence: '51'




,handler : {
methodName : "extn_Return_Acknowledgement_onClickHandler"

 
}
}
,
{
	  eventId: 'extn_linkVocuherLookup_onClick'

,	  sequence: '51'




,handler : {
methodName : "voucherLookupScreen"

 
}
}

]
}

});
});


