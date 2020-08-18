
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/home/portlets/OrderPortletExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!isccs/utils/OrderUtils",
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!isccs/utils/BaseTemplateUtils"
]
,
function(
		_dojodeclare,
		_extnOrderPortletExtnUI,
		_scBaseUtils,
		_isccsOrderUtils,
		_isccsUIUtils, 
		_scBaseUtils,
		_scModelUtils,
		_scScreenUtils,
		_isccsBaseTemplateUtils
){ 
	return _dojodeclare("extn.home.portlets.OrderPortletExtn", [_extnOrderPortletExtnUI],{
		
	// custom code here
	
	orderSearch: function() {
            var root = null;
            root = _scBaseUtils.getTargetModel(this, "getOrderList", null);

           
            
			var orderAttr = _scModelUtils.hasAttributeInModelPath("Order.OrderNo",root) ||
							_scModelUtils.hasAttributeInModelPath("Order.CustomerPhoneNo",root)	 ||
							_scModelUtils.hasAttributeInModelPath("Order.CustomerFirstName",root) ||	
							_scModelUtils.hasAttributeInModelPath("Order.CustomerEMailID",root)	 ||
							_scModelUtils.hasAttributeInModelPath("Order.CustomerLastName",root) ||	
							_scModelUtils.hasAttributeInModelPath("Order.CustomerZipCode",root);	
								
			var payTechAttr = _scModelUtils.hasAttributeInModelPath("Order.PaymentTechOrderId",root);					
			var trackingNoAttr = _scModelUtils.hasAttributeInModelPath("Order.TrackingNo",root);
			var mkpCustPOAttr = _scModelUtils.hasAttributeInModelPath("Order.MarketPlaceCustPONo",root);

			 if(mkpCustPOAttr) 
			 {
				 root.Order.CustomerPONo=root.Order.MarketPlaceCustPONo;
				delete root.Order["MarketPlaceCustPONo"];
			 }					

			if(trackingNoAttr){

				var trackingInput = {};
				trackingInput.Container = {};
				trackingInput.Container.TrackingNo = _scModelUtils.getStringValueFromPath("Order.TrackingNo", root);
				
				_isccsUIUtils.callApi(this, trackingInput, "extn_getShipmentContainerList_RefID");
			}
			else if(payTechAttr){
				var paymentTechId = root.Order.PaymentTechOrderId;
				var ajbInput = {};
				ajbInput.AJBSettlement = {};
				if(_scBaseUtils.equals(20,_scBaseUtils.getStringLength(paymentTechId))){
					var terminalName = paymentTechId.substring(0, 3);
					var transactionNumber = paymentTechId.substring(3, 8)
					var storeNumber = paymentTechId.substring(8, 12);
					var transactionAqDate = paymentTechId.substring(12);
					
					ajbInput.AJBSettlement.ComplexQuery = {};
					ajbInput.AJBSettlement.ComplexQuery.Operator = "OR"; 
					ajbInput.AJBSettlement.ComplexQuery.And = {}; 
					
					var andExp = [
						{Name: "TerminalName", QryType:  "LIKE", Value:  terminalName},
						{Name: "TransactionNumber", QryType:  "LIKE", Value:  transactionNumber},
						{Name: "StoreNumber", QryType:  "LIKE", Value:  storeNumber},
						{Name: "TransactionAqDate", QryType:  "EQ", Value:  transactionAqDate},
						{Name: "TransactionType", QryType:  "EQ", Value:  "Sale"},

					];
					_scModelUtils.addListToModelPath("AJBSettlement.ComplexQuery.And.Exp", andExp, ajbInput);
console.log("ORDERPORTLET:	input model for VSIGetAJBSettlementList Service Call",ajbInput);
					_isccsUIUtils.callApi(this, ajbInput, "extn_getAJBSettlementList_RefID");

				}else{
					_scScreenUtils.showErrorMessageBox(this,"PaymentTech Order # should have exact 20 digits!",null,null,null);

				}
			}
			else{
console.log("root",root);
	            _isccsOrderUtils.openOrderSearch(this, root, "getOrderList");
			}
	},
	handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
		
		var root = null;
		root = {};
		root.Order = {};
		var model = {};	

			if(!_scBaseUtils.isVoid(mashupRefList)){

			// Handles VSIGetAJBSettlementList Service Call
				if (_scBaseUtils.equals(mashupRefList[0].mashupRefId, "extn_getAJBSettlementList_RefID")) {

					model = mashupRefList[0].mashupRefOutput.AJBSettlementList;
					if(!_scBaseUtils.isVoid(model)){
	console.log("ORDERPORTLET:	output model after VSIGetAJBSettlementList Service Call",model);			   
						 root.Order.OrderHeaderKey = model.AJBSettlement[0].OrderHeaderKey;
					}

					root.Order.OrderBy = {};
					root.Order.OrderBy.Attribute = {};
					root.Order.OrderBy.Attribute.Desc = "N";
					root.Order.OrderBy.Attribute.Name = "OrderName";

	console.log("ORDERPORTLET:	getOrderList input created after VSIGetAJBSettlementList Service Call: ",root);			   
	
					_isccsOrderUtils.openOrderSearch(this, root, "getOrderList");	

				}
			// Handles VSIGetShipmentContainerList Service Call
				else if (_scBaseUtils.equals(mashupRefList[0].mashupRefId, "extn_getShipmentContainerList_RefID")) {

					model = mashupRefList[0].mashupRefOutput.Containers;
					
					if(!_scBaseUtils.isVoid(_scModelUtils.getStringValueFromPath("Shipment.OrderHeaderKey",model.Container[0])) ||  
					   !_scBaseUtils.isVoid(_scModelUtils.getStringValueFromPath("Shipment.OrderNo",model.Container[0]))){
						
						root.Order.OrderHeaderKey = _scModelUtils.getStringValueFromPath("Shipment.OrderHeaderKey",model.Container[0]);
						root.Order.OrderNo =  _scModelUtils.getStringValueFromPath("Shipment.OrderNo",model.Container[0]);
					
					}else{
						root = {};
						
					}

					_isccsOrderUtils.openOrderSearch(this, root, "getOrderList");	
				}else{
					_isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
				}
			}
		
		


	} 
});
});

