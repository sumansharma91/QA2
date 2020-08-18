
scDefine([
			"scbase/loader!dojo/_base/declare",
			"scbase/loader!extn/order/details/OrderSummaryLinesExtnUI",
			"scbase/loader!sc/plat/dojo/utils/BundleUtils",
			"scbase/loader!sc/plat/dojo/utils/ModelUtils",
			"scbase/loader!sc/plat/dojo/utils/BaseUtils",
			"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
			"scbase/loader!dojox/html/entities",
			"scbase/loader!dojo/_base/lang",
			"scbase/loader!isccs/utils/DeliveryUtils",
			"scbase/loader!sc/plat/dojo/utils/GridxUtils",
			"scbase/loader!isccs/utils/BaseTemplateUtils",
			"scbase/loader!sc/plat/dojo/utils/EditorUtils",
			"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
			"scbase/loader!isccs/utils/UIUtils",
			"scbase/loader!sc/plat/dojo/utils/PaginationUtils"
],
function(
			_dojodeclare,
			_extnOrderSummaryLinesExtnUI,
			_scBundleUtils,
			_scModelUtils,
			_scBaseUtils,
			_scScreenUtils,
			_dHtmlEntities,
			_dLang,
			_isccsDeliveryUtils,
			_scGridxUtils,
			_isccsBaseTemplateUtils,
			_scEditorUtils,
			_scWidgetUtils,
			_isccsUIUtils,
			_scPaginationUtils
){ 
	return _dojodeclare("extn.order.details.OrderSummaryLinesExtn", [_extnOrderSummaryLinesExtnUI],{
	// custom code here
	
		
        checkMigratedOrderFlag: function() {
			mCompleteOrdDetails = this.getModel("getCompleteOrderLineList_output");
			//console.log("return mCompleteOrdDetails: ", mCompleteOrdDetails);
			if (!_scBaseUtils.isVoid(mCompleteOrdDetails) && mCompleteOrdDetails.Page.Output.OrderLineList.OrderLine[0].Extn.ExtnIsMigrated == "Y") {
				_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
				
				var type = "isccs.editors.OrderEditorRT";
				var editor = _isccsUIUtils.getEditorFromScreen(this);		
				var rtScreen = editor.getWidgetByUId("RelatedTaskScreenHolder");
				var listOfChildren = rtScreen.getChildren(rtScreen);
				
				for (var i=0;i<listOfChildren.length;i++) {
			var child = listOfChildren[i];
			//console.log("Iteration : ",(i+1));
			if (_isccsUIUtils.instanceOf(child,type) || (child.screenId && child.screenId == type)) {
					if(child instanceof scScreen )
					{
						//console.log('the child is',child);
						_scWidgetUtils.disableWidget(child, "cancelOrder", true);
				_scWidgetUtils.disableWidget(child, "addRemovePromotion", true);
				_scWidgetUtils.disableWidget(child, "AddLinesToOrderWizard", true);
				_scWidgetUtils.disableWidget(child, "changeOrderAddress", true);
				_scWidgetUtils.disableWidget(child, "ResolveHoldWizard", true);
				_scWidgetUtils.disableWidget(child, "ApplyHoldWizard", true);
				_scWidgetUtils.disableWidget(child, "AddModifyChargesWizard", true);
				_scWidgetUtils.disableWidget(child, "customerAppeasement", true);
				_scWidgetUtils.disableWidget(child, "createReturn", true);
				_scWidgetUtils.disableWidget(child, "changeFulfillmentOptions", true);
				_scWidgetUtils.disableWidget(child, "extn_giftCardFulfillment", true);
				_scWidgetUtils.disableWidget(child, "customerOptions", true);
				_scWidgetUtils.disableWidget(child, "emailOrder", true);
				_scWidgetUtils.disableWidget(child, "viewAllInvoices", true);
						break;
					}
				}
				//console.log('rtScreen',rtScreen);
				//console.log('rtScreen1',rtScreen1);
				//_scWidgetUtils.disableWidget(rtScreen, "lnk_RT_CreateAlert", true);
			
		}
				//_scWidgetUtils.disableWidget(rtScreen, "lnk_RT_CreateAlert", true);
			}
		},
		LST_loadPaginatedGridOnLoad: function(
				event, bEvent, ctrl, args) {
			var modelOutput = null;
			var requiresPagination = "true";
			var isListMashupInit = "true";
			var options = null;
			
			if (
			_scBaseUtils.equals(
			requiresPagination, "true") && _scBaseUtils.equals(
			isListMashupInit, "true")) {
				modelOutput = _scScreenUtils.getModel(
				this, "getCompleteOrderLineList_output");
				console.log("modelOutput",modelOutput);
				var orderCustomerPONO = modelOutput.Page.Output.OrderLineList.Order.CustomerPONo;
				var nlOrderLine = modelOutput.Page.Output.OrderLineList.OrderLine;
				console.log("nlOrderLine",nlOrderLine);
				for(var i=0;i<nlOrderLine.length;i++){
					var eleOrderLine = nlOrderLine[i];
					console.log("eleOrderLine",eleOrderLine);
					if(_scBaseUtils.isVoid(eleOrderLine.CustomerPONo) 
							&& !_scBaseUtils.isVoid(orderCustomerPONO)){
						eleOrderLine.CustomerPONo = orderCustomerPONO;
					}
					var strExpectedStartDate = eleOrderLine.ExpectedStartDate;
					var strExpectedEndDate = eleOrderLine.ExpectedEndDate;
					console.log("strExpectedStartDate",strExpectedStartDate);
					console.log("strExpectedEndDate",strExpectedEndDate);
					
					if(!_scBaseUtils.isVoid(strExpectedStartDate)){
						strExpectedStartDate = strExpectedStartDate.split("T")[0];
					}
					if(!_scBaseUtils.isVoid(strExpectedEndDate)){
						strExpectedEndDate = strExpectedEndDate.split("T")[0];
					}
					
					console.log("strExpectedStartDate",strExpectedStartDate);
					console.log("strExpectedEndDate",strExpectedEndDate);
					eleOrderLine.ExpectedStartDate = strExpectedStartDate;
					eleOrderLine.ExpectedEndDate = strExpectedEndDate;
				}
				if ((nlOrderLine.length > 0)
					&& (_scBaseUtils.equals(_scModelUtils.getStringValueFromPath("DeliveryMethod", nlOrderLine[0]), "PICK"))) {
					_isccsUIUtils.callApi(this, modelOutput, "extn_getSTSTrackingNo");
				} else {
					options = {};
	                _scBaseUtils.setAttributeValue("screen", this, options);
	                _scBaseUtils.setAttributeValue("mashupRefId", "getCompleteOrderLineList", options);
	                modelOutput = _scScreenUtils.getModel(
	                this, "getCompleteOrderLineList_output");
	                _scPaginationUtils.startPaginationWithOutputData(
	                this, "OLST_listGrid", null, modelOutput, options);
	                }
			}
		},
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineList")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "getCompleteOrderLineList_output", modelOutput, null);
                }
                var orderLine = null;
                var requiresPagination = "true";
                if (
                _scBaseUtils.equals(
                requiresPagination, "true")) {
                    orderLine = _scModelUtils.getModelListFromPath("Page.Output.OrderLineList.OrderLine", modelOutput);
                } else {
                    orderLine = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput);
                }
                if (!(
                _scBaseUtils.isVoid(
                orderLine))) {
                    _scGridxUtils.selectRowUsingUId(
                    this, "OLST_listGrid", 0);
                }
            } else if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getSTSTrackingNo")) {
				options = {};
				_scBaseUtils.setAttributeValue("screen", this, options);
				_scBaseUtils.setAttributeValue("mashupRefId", "getCompleteOrderLineList", options);
                console.log("handleMashupOutput ## extn_getSTSTrackingNo", modelOutput);
				_scScreenUtils.setModel(
                    this, "getCompleteOrderLineList_output", modelOutput, null);
				
				_scPaginationUtils.startPaginationWithOutputData(
				this, "OLST_listGrid", null, modelOutput, options);
            }
        }
});
});
