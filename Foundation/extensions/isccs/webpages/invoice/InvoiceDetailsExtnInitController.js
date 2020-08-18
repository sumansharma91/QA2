


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/invoice/InvoiceDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnInvoiceDetailsExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.invoice.InvoiceDetailsExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.invoice.InvoiceDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'getOrderInvoiceDetails_output'
,
		 mashupRefId : 			'getOrderInvoiceDetails'
,
		 sequence : 			'5'
,
		 mashupId : 			'invoiceDetails_getOrderInvoiceDetails'
,
		 callSequence : 			'1'
,
		 extnType : 			''
,
		 sourceBindingOptions : 			''

	}
,
	 		{
		 sourceNamespace : 			'getOrderInvoiceDetails_output'
,
		 mashupRefId : 			'getOrderInvoiceDetails'
,
		 sequence : 			'5'
,
		 mashupId : 			'invoiceDetails_getOrderInvoiceDetails'
,
		 callSequence : 			'1'
,
		 extnType : 			'ADD'
,
		 sourceBindingOptions : 			''

	}

	]

}
);
});

