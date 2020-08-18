
scDefine(["dojo/text!./templates/AddReturnLinesExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.return.create.lines.AddReturnLinesExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	


, staticBindings : [

		{
				
				

			 	targetBinding : 	{
		 namespace : 			'changeOrder_input'
,
		 path : 			'Order.OrderType'

	}
,			 
			 	sourceBinding : 	{
		 namespace : 			'createdOrder'
,
		 path : 			'Order.OrderType'

	}
			 
		
		}	
	]
	
	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {

local : [

]
}

});
});


