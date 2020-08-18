
scDefine(["dojo/text!./templates/OrderLineSummaryExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!gridx/Grid","scbase/loader!gridx/modules/ColumnResizer","scbase/loader!gridx/modules/ColumnWidth","scbase/loader!gridx/modules/HLayout","scbase/loader!gridx/modules/select/Row","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/GridxDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel"]
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
			    _gridxGrid
			 ,
			    _gridxColumnResizer
			 ,
			    _gridxColumnWidth
			 ,
			    _gridxHLayout
			 ,
			    _gridxRow
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scGridxDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
){
return _dojodeclare("extn.order.details.OrderLineSummaryExtnUI",
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

,	  sequence: '51'




,handler : {
methodName : "checkMigratedOrderFlag"

, description :  "Checks whether this order is migrated. If yes, disables any actionable links"  
}
}

]
}

});
});


