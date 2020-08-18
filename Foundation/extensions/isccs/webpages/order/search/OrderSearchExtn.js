
scDefine(["scbase/loader!dojo/_base/declare",
	      "scbase/loader!extn/order/search/OrderSearchExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!isccs/utils/ModelUtils",
		  "scbase/loader!isccs/utils/OrderUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils"],
function( _dojodeclare,
		  _extnOrderSearchExtnUI,
		  _isccsBaseTemplateUtils,
		  _isccsModelUtils,
		  _isccsOrderUtils,
		  _isccsUIUtils,
		  _scBaseUtils,
		  _scEventUtils,
		  _scScreenUtils,
		  _scModelUtils
		  
){ 
	return _dojodeclare("extn.order.search.OrderSearchExtn", [_extnOrderSearchExtnUI],{
	// custom code here

        handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {

            if (_scBaseUtils.equals(mashupRefId, "getHoldTypeList")) {
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "getHoldTypeList_output", modelOutput, null);
                }
                var sEnterpriseCode = null;
                sEnterpriseCode = _scModelUtils.getStringValueFromPath("HoldType.CallingOrganizationCode", mashupInput);
                var enterpriseSpecificHoldsList = null;
                enterpriseSpecificHoldsList = this.getHoldsListFromMap(sEnterpriseCode);
                if (_scBaseUtils.isVoid(enterpriseSpecificHoldsList)) {
                    this.saveHoldTypeListInMap(sEnterpriseCode, modelOutput);
                    _scScreenUtils.setModel(this, "getHoldTypeList_output", modelOutput, null);
                } else {
                    _scScreenUtils.setModel(this, "getHoldTypeList_output", enterpriseSpecificHoldsList, null);
                }
		// Handles VSIGetAJBSettlementList Service Call
			}else  if (_scBaseUtils.equals(mashupRefId, "extn_getAJBSettlementList_RefID")) {
			
				model = modelOutput.AJBSettlementList;
console.log("ORDER_SEARCH:	output model after VSIGetAJBSettlementList Service Call",model);
		   
				if(!_scBaseUtils.isVoid(model)){
					if(!_scBaseUtils.isVoid(this.uIdMap.extn_textfield_PaymentTechOrder.displayedValue)){
						_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("OrderHeaderKey",model.AJBSettlement[0]),mashupInput);	
						_isccsModelUtils.removeAttributeFromModel("AJBSettlement", mashupInput); 
				}
				}else{
					_scModelUtils.setStringValueAtModelPath("Order.OrderNo","_",mashupInput);
				}
				var eventDefn = null;
				var args = null;
				eventDefn = {};
				args = {};
console.log("ORDER_SEARCH:	getOrderList input created after VSIGetAJBSettlementList Service Call: ",mashupInput);			   
														
				_scBaseUtils.setAttributeValue("inputData", mashupInput, args);
				_scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
				_scEventUtils.fireEventToChild(this, "orderListScreen", "callListApi", eventDefn);

		// Handles VSIGetShipmentContainerList Service Call
			}else  if (_scBaseUtils.equals(mashupRefId, "extn_getShipmentContainerList_RefID")) {

				model = modelOutput.Containers;
                if(!_scBaseUtils.isVoid(_scModelUtils.getStringValueFromPath("Shipment.OrderHeaderKey",model.Container[0])) ||  
				   !_scBaseUtils.isVoid(_scModelUtils.getStringValueFromPath("Shipment.OrderNo",model.Container[0]))){

					_scModelUtils.setStringValueAtModelPath("Order.OrderNo", _scModelUtils.getStringValueFromPath("Shipment.OrderNo",model.Container[0]),mashupInput);
				}else{

					_scModelUtils.setStringValueAtModelPath("Order.OrderNo","blank",mashupInput);
				}
				var eventDefn = null;
				var args = null;
				eventDefn = {};
				args = {};
			 	_scBaseUtils.setAttributeValue("inputData", mashupInput, args);
			 	_scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
			 	_scEventUtils.fireEventToChild(this, "orderListScreen", "callListApi", eventDefn);
			}
        },
		SST_search: function() {
            var targetModel = null;

            if (!(_isccsOrderUtils.validateSearchInput(this, this.SST_getSearchNamespace()))) {
                _isccsBaseTemplateUtils.showMessage(this, "Date_Validation_Error_Order_Search", "error", null);
                return;
            }
            if (!(
            _scScreenUtils.isValid(this, this.SST_getSearchNamespace()))) {
                _isccsBaseTemplateUtils.showMessage(this, "InvalidSearchCriteria", "error", null);
                return;
            } else {
                _isccsBaseTemplateUtils.hideMessage(this);
                targetModel = _scBaseUtils.getTargetModel(this, this.SST_getSearchNamespace(), null);
            }
            _scBaseUtils.removeBlankAttributes(targetModel);

			var payTechAttr = _scModelUtils.hasAttributeInModelPath("Order.PaymentTechOrderId",targetModel);					
			var trackingNoAttr = _scModelUtils.hasAttributeInModelPath("Order.TrackingNo",targetModel);
			var customerPONo = _scModelUtils.hasAttributeInModelPath("Order.CustomerPONo",targetModel);
            var eventDefn = null;
            var args = null;
            eventDefn = {};
            args = {};
			
			if(trackingNoAttr){
				var trackingInput = {};
				trackingInput.Container = {};
				trackingInput.Container.TrackingNo =  _scModelUtils.getStringValueFromPath("Order.TrackingNo", targetModel);;
				
				_isccsUIUtils.callApi(this, trackingInput, "extn_getShipmentContainerList_RefID");
				
			}else if(payTechAttr){
				var paymentTechId = targetModel.Order.PaymentTechOrderId;
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
					_isccsUIUtils.callApi(this, ajbInput, "extn_getAJBSettlementList_RefID");

				}else{
					var paymentTechNot20Digits = null;
					paymentTechNot20Digits = _scScreenUtils.getString(this, "PaymentTechNot20DigitsMessage");
					_scScreenUtils.showErrorMessageBox(this,paymentTechNot20Digits ,"error",null,null);

				}
			}else{
				
				if(customerPONo) 
				 {
					targetModel.Order.OrderLine=[];
					targetModel.Order.OrderLine[0] = {};
					targetModel.Order.OrderLine[0].CustomerPONo=targetModel.Order.CustomerPONo;
					delete targetModel.Order["CustomerPONo"];
				 }
				if (!(
			            _scBaseUtils.isVoid(
			             targetModel.Order.MarketPlaceCustPONo))) 
				 {
					 targetModel.Order.CustomerPONo=targetModel.Order.MarketPlaceCustPONo;
					delete targetModel.Order["MarketPlaceCustPONo"];
				 }
					_scBaseUtils.setAttributeValue("inputData", targetModel, args);
					_scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
					_scEventUtils.fireEventToChild(this, "orderListScreen", "callListApi", eventDefn);
			}
        },
	
});
});

