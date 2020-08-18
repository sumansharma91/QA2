
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/orderHold/ResolveHoldExtnUI", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnResolveHoldExtnUI, _isccsBaseTemplateUtils, _isccsModelUtils, _isccsOrderUtils, _isccsUIUtils, _isccsWizardUtils, _scBaseUtils, _scControllerUtils, _scEventUtils, _scGridxUtils, _scModelUtils, _scPaginationUtils, _scScreenUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.order.orderHold.ResolveHoldExtn", [_extnResolveHoldExtnUI],{
	// custom code here
	        save: function(
        event, bEvent, ctrl, args) {
            var getSelectedRecordeModel = null;
            var orderHeaderKey = null;
            var holdResolutionModel = null;
			var confirmBoxOptions = null;
			var hasIntHold = false;
            confirmBoxOptions = {};
            holdResolutionModel = _scScreenUtils.getModel(
            this, "getOrderHoldResolutionList_output");
            orderHeaderKey = _scModelUtils.getStringValueFromPath("Page.OrderHeaderKey", holdResolutionModel, true);
				
            getSelectedRecordeModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(
            this, "activeHolds");
console.log("getSelectedRecordeModel",getSelectedRecordeModel);
_scBaseUtils.setAttributeValue("Order.OrderHeaderKey", orderHeaderKey, getSelectedRecordeModel);
            var mashupContext = null;
            var inputModelList = null;
            var mashupRefIdList = null;
            var pageSize = null;
            var pageToShow = 1;
            inputModelList = [];
            mashupRefIdList = [];
            var action = null;
            action = _scBaseUtils.getAttributeValue("Action", false, args);
            mashupRefIdList.push("resolveHolds_changeOrder");
            mashupRefIdList.push("getOrderHoldResolutionList");
            mashupRefIdList.push("getResolvedHolds");
            inputModelList.push(
            getSelectedRecordeModel);
            mashupInputActiveHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
            this, "activeHolds");
            pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
            this, "activeHolds");
            pageToShow = this.getPageToShow(
            pageToShow);
            mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
            this, "activeHolds", pageSize, pageToShow, "getOrderHoldResolutionList", mashupContext, null);
            inputModelList.push(
            mashupInputActiveHolds);
            mashupInputResolvedHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
            this, "resolvedHolds");
			console.log("mashupInputResolvedHolds>>>",mashupInputResolvedHolds);
            pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
            this, "resolvedHolds");
            mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
            this, "resolvedHolds", pageSize, pageToShow, "getResolvedHolds", mashupContext, null);
            inputModelList.push(
            mashupInputResolvedHolds);
            if (
            _scBaseUtils.equals(
            action, "CONFIRM")) {
                _scControllerUtils.setMashupContextIdenitier(
                mashupContext, "CONFIRM");
            } else {
                _scControllerUtils.setMashupContextIdenitier(
                mashupContext, "SAVE_CLICKED");
            }
			
			var modelSelectedHolds =[]; 
			modelSelectedHolds = getSelectedRecordeModel.Order.OrderHoldTypes.OrderHoldType;
			for(var i=0;i<modelSelectedHolds.length;i++){
			strHoldType = modelSelectedHolds[i].HoldType;
console.log("strHoldType",strHoldType);
				if(_scBaseUtils.equals( strHoldType, "VSI_INTERNAT_HOLD")){
					hasIntHold=true;
				}
			}
			if(hasIntHold){
				var Order = _scModelUtils.createNewModelObjectWithRootKey("Order");
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, Order);
				
				_isccsUIUtils.callApi(this, Order, "extn_getOrderList_RefID");
			} else {
            _isccsUIUtils.callApis(
            this, inputModelList, mashupRefIdList, mashupContext, null);
			}

        },
		
	    handleInternationalHoldWarning: function(
        actionPerformed, model, popupParams) {
			
			console.log("actionPerformed",actionPerformed);
			console.log("model",model);
			console.log("popupParams",popupParams);
            var paginationContext = null;
            var pageToShow = null;
            var pageSize = null;
            if (
            _scBaseUtils.equals(
            actionPerformed, "APPLY")) {
				
				var getSelectedRecordeModel = null;
				var orderHeaderKey = null;
				var holdResolutionModel = null;
				var confirmBoxOptions = null;
				var hasIntHold = false;
				confirmBoxOptions = {};
				holdResolutionModel = _scScreenUtils.getModel(
				that, "getOrderHoldResolutionList_output");
console.log("holdResolutionModel",holdResolutionModel);				
				orderHeaderKey = _scModelUtils.getStringValueFromPath("Page.OrderHeaderKey", holdResolutionModel, true);
				getSelectedRecordeModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(
				that, "activeHolds");
								_scBaseUtils.setAttributeValue("Order.OrderHeaderKey", orderHeaderKey, getSelectedRecordeModel);
				var mashupContext = null;
				var inputModelList = null;
				var mashupRefIdList = null;
				var pageSize = null;
				var pageToShow = 1;
				inputModelList = [];
				mashupRefIdList = [];
				var action = null;
				action = "CONFIRM";//_scBaseUtils.getAttributeValue("Action", false, args);
				mashupRefIdList.push("resolveHolds_changeOrder");
				mashupRefIdList.push("getOrderHoldResolutionList");
				mashupRefIdList.push("getResolvedHolds");
				inputModelList.push(
				getSelectedRecordeModel);
				mashupInputActiveHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
				that, "activeHolds");
				pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
				that, "activeHolds");
				pageToShow = that.getPageToShow(
				pageToShow);
				mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
				that, "activeHolds", pageSize, pageToShow, "getOrderHoldResolutionList", mashupContext, null);
				inputModelList.push(
				mashupInputActiveHolds);
				mashupInputResolvedHolds = _scPaginationUtils.getInitialInputForPaginatedGrid(
				that, "resolvedHolds");
				console.log("mashupInputResolvedHolds>>>",mashupInputResolvedHolds);
				pageToShow = _scPaginationUtils.getCurrentPageNumberForPaginatedGrid(
				that, "resolvedHolds");
				mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
				that, "resolvedHolds", pageSize, pageToShow, "getResolvedHolds", mashupContext, null);
				inputModelList.push(
				mashupInputResolvedHolds);
				if (
				_scBaseUtils.equals(
				action, "CONFIRM")) {
					_scControllerUtils.setMashupContextIdenitier(
					mashupContext, "CONFIRM");
				} else {
					_scControllerUtils.setMashupContextIdenitier(
					mashupContext, "SAVE_CLICKED");
				}
				_isccsUIUtils.callApis(
				that, inputModelList, mashupRefIdList, mashupContext, null); 
            }
        },
		handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {			
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getOrderList_RefID")) {
console.log("modelOutput",modelOutput);		
				var popupInput = {};
				popupInput.OrderLines={};
				popupInput.OrderLines.OrderLine = [];
				
				var bHasLinesToCancel = false;
				var order = modelOutput.OrderList.Order[0];
				console.log("order",order);
				var count=0;
				var OrderLines = _scModelUtils.getStringValueFromPath("OrderLines.OrderLine", order);
				for(j=0;j<OrderLines.length;j++){
					var bIsCancelable = false;
					var orderLine = OrderLines[j];
					var item =  _scModelUtils.getStringValueFromPath("Item", orderLine);
					//_scScreenUtils.setModel(this, "getOrderLineList_output", item, null);
					//_scModelUtils.setStringValueAtModelPath("Schedules.Schedule",item, OrderLine[j]);
					var schedule = _scModelUtils.getStringValueFromPath("Schedules.Schedule", orderLine);
					console.log("schedule",schedule);
					var quantity = 0;
					
					if(!(_scBaseUtils.isVoid(schedule))){
						
						for(k=0;k<schedule.length;k++){
							
							var productAvailability = _scModelUtils.getStringValueFromPath("ProductAvailabilityDate", schedule[k]);
							var date = _scBaseUtils.getDate();
							//var formattedAvailabilityDate = _scBaseUtils.convertToUserFormat(productAvailability,"DATE");
							var str_array = productAvailability.split('T');
							var formattedDate = _scBaseUtils.convertToServerFormat(date,"DATE");
							//console.log("formattedAvailabilityDate",formattedAvailabilityDate);
							if(_scBaseUtils.dateGreaterThan(str_array[0],formattedDate)){
								var availCancelQuantity = _scModelUtils.getStringValueFromPath("Quantity", schedule[k]);
								quantity = quantity + availCancelQuantity;
								bHasLinesToCancel = true;
								bIsCancelable = true;
							}
						}
						_scModelUtils.setStringValueAtModelPath("OrderedQty",parseInt(quantity).toString(), orderLine);
						if(bIsCancelable){
							popupInput.OrderLines.OrderLine[count] = orderLine;
							count++;
						}
					} else{
						//do nothing
					}
					console.log("popupInput",popupInput);
				}
				
				var screenInput = null;
				var popupParams = null;
				screenInput = {};
				screenInput = popupInput;
						
				popupParams = {};
				popupParams["screenInput"] = screenInput;
				var dialogParams = null;
				dialogParams = {};
				_scBaseUtils.setAttributeValue("closeCallBackHandler", "handleInternationalHoldWarning", dialogParams);
				
				that = this;
				if(bHasLinesToCancel){
				_isccsUIUtils.openSimplePopup("extn.custom.resolveInternationalHold.InternationalHold", "Unavailable Product(s) awaiting cancellation", this, popupParams, dialogParams);                
				}else{
					this.handleInternationalHoldWarning("APPLY",modelOutput,null);
				}
			}
            if (
            _scBaseUtils.equals(
            mashupRefId, "resolveHolds_changeOrder")) {
				
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getOrderHoldResolutionList")) {
                var mashupIdentifier = null;
                mashupIdentifier = _scControllerUtils.getMashupContextIdenitier(
                mashupContext);
                if (
                _scBaseUtils.equals(
                mashupIdentifier, "SAVE_CLICKED")) {
                    _scPaginationUtils.loadPageFromMashupContext(
                    this, "activeHolds", mashupContext, mashupRefId);
                    _isccsBaseTemplateUtils.showMessage(
                    this, "HoldsResolvedSuccess", "success", null);
                } else {
                    _scPaginationUtils.loadPageFromMashupContext(
                    this, "activeHolds", mashupContext, mashupRefId);
                }
                if (
                _scBaseUtils.getAttributeCount(
                _scModelUtils.getModelListFromPath("Page.Output.OrderHoldTypes.OrderHoldType", modelOutput)) < 1) {
                    _scWidgetUtils.disableWidget(
                    this, "update_holds", false);
                }
                if (
                _scBaseUtils.equals(
                this.showPageReloadMessage, "Y")) {
                    this.showPageReloadMessage = "N";
                    var inputArray = null;
                    var pageNum = null;
                    pageNum = _scBaseUtils.getValueFromPath("Page.PageNumber", modelOutput);
                    inputArray = [];
                    inputArray.push(
                    pageNum);
                    var msg = null;
                    msg = _scScreenUtils.getFormattedString(
                    this, "Same_Page_Reloaded", inputArray);
                    this.showGridMessage(
                    msg, "information", null);
                } else {
                    this.hideGridMessage();
                }
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getResolvedHolds")) {
                var mashupIdentifier = null;
                mashupIdentifier = _scControllerUtils.getMashupContextIdenitier(
                mashupContext);
                if (
                _scBaseUtils.equals(
                mashupIdentifier, "SAVE_CLICKED")) {
                    _scPaginationUtils.loadPageFromMashupContext(
                    this, "resolvedHolds", mashupContext, mashupRefId);
                } else {
                    _scPaginationUtils.loadPageFromMashupContext(
                    this, "resolvedHolds", mashupContext, mashupRefId);
                }
                this.determineIfLineHoldsArePresent(
                null, modelOutput);
            }
        }
});
});

