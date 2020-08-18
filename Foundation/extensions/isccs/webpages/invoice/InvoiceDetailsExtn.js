
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/invoice/InvoiceDetailsExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils"]
,
function(			 
			    _dojodeclare,
			    _extnInvoiceDetailsExtnUI,
				_scScreenUtils,
				_scModelUtils,
				_scBaseUtils
){ 
	return _dojodeclare("extn.invoice.InvoiceDetailsExtn", [_extnInvoiceDetailsExtnUI],{
	// custom code here
	getGiftCardNumber: function(dataValue, screen, widget, namespace, modelObject, options){
		//OMS-1166 start
		/*
		var refrence1 = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.Reference1", modelObject);
		var returnValue = null;
		if(!_scBaseUtils.isVoid(refrence1)){
			var str_array = refrence1.split('_');	
			returnValue = str_array[0];
		}//end of if
		return returnValue;
		*/
		//OMS-1626 start
		var InvoiceType = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.InvoiceType", modelObject);
		if(InvoiceType === "INFO"){
				
		var refrence1 = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.Reference1", modelObject);
		var returnValue = null;
		if(!_scBaseUtils.isVoid(refrence1)){
			var str_array = refrence1.split('_');	
			returnValue = str_array[0];
		}//end of if
		return returnValue;
		}
		//OMS-1626 end
		else{
		var returnValue = null;
		var totalLines = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.CollectionDetails.TotalLines",modelObject);
        if(totalLines && totalLines !== "0"){
			for(var i=0; i< totalLines; i++){
				var collectionDetail = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.CollectionDetails.CollectionDetail",modelObject)[i];
				if(collectionDetail && collectionDetail.PaymentMethod.PaymentType === "ONLINE_GIFT_CARD"){
					return collectionDetail.PaymentMethod.SvcNo;
				}
			}
        }
		return returnValue;
		//OMS-1166 End   
		}		
	},
	getGiftCardPin: function(dataValue, screen, widget, namespace, modelObject, options){
		//OMS-1166 start
		/*
		var refrence1 = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.Reference1", modelObject);
		var returnValue = null;
		if(!_scBaseUtils.isVoid(refrence1)){
			var str_array = refrence1.split('_');
			returnValue = str_array[1];
		}//end of if
		return returnValue;
		*/
		//OMS-1626 start
		var InvoiceType = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.InvoiceType", modelObject);
		if(InvoiceType === "INFO"){
		var refrence1 = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.Reference1", modelObject);
		var returnValue = null;
		if(!_scBaseUtils.isVoid(refrence1)){
			var str_array = refrence1.split('_');	
			returnValue = str_array[1];
		}//end of if
		return returnValue;
		}
       //OMS-1626 end
		else{
			
        var returnValue = null;
        var totalLines = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.CollectionDetails.TotalLines",modelObject);
        if(totalLines && totalLines !== "0"){
			for(var i=0; i< totalLines; i++){
				var collectionDetail = _scModelUtils.getStringValueFromPath("InvoiceDetail.InvoiceHeader.CollectionDetails.CollectionDetail",modelObject)[i];
				if(collectionDetail && collectionDetail.PaymentMethod.PaymentType === "ONLINE_GIFT_CARD"){
					return collectionDetail.PaymentMethod.PaymentReference2;
				}
			}
        }
        return returnValue;
		}
		//OMS-1166 End 
	}
});
});

