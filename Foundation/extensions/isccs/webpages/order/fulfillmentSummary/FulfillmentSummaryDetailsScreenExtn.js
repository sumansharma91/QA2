
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/fulfillmentSummary/FulfillmentSummaryDetailsScreenExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/PaymentUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ControllerUtils",
		  "scbase/loader!isccs/utils/ContextUtils"
		 ],
function(			 
			    _dojodeclare,
			    _extnFulfillmentSummaryDetailsScreenExtnUI,
				_isccsBaseTemplateUtils, 
				_isccsModelUtils, 
				_isccsOrderUtils, 
				_isccsPaymentUtils, 
				_isccsWidgetUtils, 
				_scBaseUtils, 
				_scGridxUtils,
				_scModelUtils, 
				_scScreenUtils, 
				_scWidgetUtils,
				_isccsUIUtils,
				_scEventUtils,
				_scControllerUtils,
				_isccsContextUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FulfillmentSummaryDetailsScreenExtn", [_extnFulfillmentSummaryDetailsScreenExtnUI],{
	// custom code here
	hashMapFuturePickUpLines: {},
	checkFulfillmentOptions: function(
		        event, bEvent, ctrl, args){
		
		/*var getFulfillmentDetailsModel = _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output");
		console.log("GetFulfillmentDetailsModel",getFulfillmentDetailsModel);
		
		var inputModel = {};
		inputModel.Order = {};
		_scModelUtils.addStringValueToModelObject("OrderHeaderKey", getFulfillmentDetailsModel.Order.OrderHeaderKey, inputModel.Order);
		_isccsUIUtils.callApi(this, inputModel, "extn_getOrderFulfillmentDetails_referenceid");*/
	},
	/*getStoreRespondByDate: function(strEnterpriseCode,eleOrderLine){
		var currentDate = _scBaseUtils.getDate();
		var eleShipNode = _scModelUtils.getStringValueFromPath("Shipnode",eleOrderLine);		
		if (!_scBaseUtils.isVoid(eleShipNode)) {
			var strShipNode = "";
			strShipNode = _scModelUtils.getStringValueFromPath("ShipNode",eleShipNode);
			var inputModel = {};
			inputModel.Organization = {};
			_scModelUtils.setStringValueAtModelPath("OrganizationCode", "9001", inputModel);
			_scModelUtils.setStringValueAtModelPath("CurrentDate", _scBaseUtils.convertToServerFormat(currentDate,"DATETIME"), inputModel);
			_scModelUtils.setStringValueAtModelPath("OrderLineKey", _scModelUtils.getStringValueFromPath("OrderLineKey",eleOrderLine), inputModel);
			
			console.log("InputModel",inputModel);
			_isccsUIUtils.callApi(this, inputModel, "extn_vsiPromiseDate_referenceid");
		} else {
			currentDate = _scBaseUtils.convertToUserFormat(currentDate,"DATE");
			
			_scModelUtils.setStringValueAtModelPath("EarliestProductShipDate", currentDate, eleOrderLine);
			_scModelUtils.setStringValueAtModelPath("ReqDeliveryDate", currentDate, eleOrderLine);
			_scModelUtils.setStringValueAtModelPath("ReqShipDate", currentDate, eleOrderLine);

		}
		return eleOrderLine;
	},
	callChangeOrder: function(inputModelChangeOrder){
		console.log("CallingChangeOrder",inputModelChangeOrder);
		_isccsUIUtils.callApi(this, inputModelChangeOrder, "extn_changeOrder_referenceid");
	},
	handleMashupOutput: function(
        mashupRefId, modelOutput, modelInput, mashupContext) {
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getOrderFulfillmentDetails_referenceid")){
				var callChangeOrderFlag = "N";
				console.log("getOrderFulfillmentDetails",modelOutput);
				
				var strCurrentDate = _scBaseUtils.getDate();
				console.log("CurrentDate",strCurrentDate);
				strCurrentDate = _scBaseUtils.convertToServerFormat(strCurrentDate,"DATETIME");
				console.log("AFTER: ",strCurrentDate);
				var strEnterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", modelOutput);
				var orderLineList = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine", modelOutput);
				for(var i = 0;i<orderLineList.length;i++){
					console.log("Starting For loop");
					var eleOrderLine=orderLineList[i];
					var strDeliveryMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod", eleOrderLine);
					if(_scBaseUtils.equals(
						strDeliveryMethod, "PICK")){ 
							var strHasAnyUnavailableQty = _scModelUtils.getStringValueFromPath("HasAnyUnavailableQty", eleOrderLine);
							if(_scBaseUtils.equals(
								strHasAnyUnavailableQty,"Y")){									
									var dblQtyAvailable = 0.0;
									var dblQtyUnAvailable = 0.0;
									var elePromiseLine = _scModelUtils.getStringValueFromPath("PromiseLine", modelOutput);
									if(!_scBaseUtils.isVoid(elePromiseLine)){
										var ndlAssignment = _scModelUtils.getStringValueFromPath("Assignment",elePromiseLine);
										for(var j = 0; j<ndlAssignment.getLength(); j++){
											var eleAssignment = ndlAssignment[j];
											if(!_scBaseUtils.isVoid(eleAssignment)){
												var strShipNode = _scModelUtils.getStringValueFromPath("ShipNode",eleAssignment);

												if(_scBaseUtils.isVoid(strShipNode)){
													dblQtyUnAvailable = dblQtyUnAvailable + _scModelUtils.getStringValueFromPath("Quantity",eleAssignment);
												}else{
													dblQtyAvailable = dblQtyAvailable + _scModelUtils.getStringValueFromPath("Quantity",eleAssignment);
												}
											}
										}
									}else{
										dblQtyUnAvailable = _scModelUtils.getStringValueFromPath("Quantity",eleOrderLine);
									}

									if(dblQtyAvailable != 0){
										_scModelUtils.setStringValueAtModelPath("OrderedQty",dblQtyAvailable,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("Quantity",dblQtyAvailable,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("FulfillmentType", "PICK_IN_STORE",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("LineType", "PICK_IN_STORE",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("HasAnyUnavailableQty", "N",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("IsReturnable","Y",eleOrderLine);
										
										_scModelUtils.setStringValueAtModelPath("EarliestProductShipDate",strCurrentDate,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("ReqDeliveryDate",strCurrentDate,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("ReqShipDate",strCurrentDate,eleOrderLine);
										var eleOrderLineTranQty = _scModelUtils.getStringValueFromPath("OrderLineTranQuantity",eleOrderLine);
										if(!_scBaseUtils.isVoid(eleOrderLineTranQty)){
											_scModelUtils.setStringValueAtModelPath("OrderedQty",dblQtyAvailable,eleOrderLineTranQty);
										}

										if(dblQtyUnAvailable != 0){
											var orderLineTemp = eleOrderLine;
											_scModelUtils.setStringValueAtModelPath("Action", "CREATE",orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("OrderedQty",dblQtyUnAvailable,orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("FulfillmentType", "SHIP_TO_STORE",orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("LineType", "SHIP_TO_STORE",orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("ProcureFromNode","9001",orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("OrderLineKey", "",orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("Quantity",dblQtyUnAvailable,orderLineTemp);
											_scModelUtils.setStringValueAtModelPath("HasAnyUnavailableQty", "N",eleOrderLine);
											var eleOrderLineTempTranQty = _scModelUtils.getStringValueFromPath("OrderLineTranQuantity",orderLineTemp);
											if(!_scBaseUtils.isVoid(eleOrderLineTranQty)){
												_scModelUtils.setStringValueAtModelPath("OrderedQty",dblQtyUnAvailable,eleOrderLineTempTranQty);
											}
											eleOrderLine = orderLineTemp
											eleOrderLine = getStoreRespondByDate(strEnterpriseCode,eleOrderLine);
										}
									}else{

										_scModelUtils.setStringValueAtModelPath("FulfillmentType", "SHIP_TO_STORE",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("LineType", "SHIP_TO_STORE",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("ProcureFromNode","9001",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("HasAnyUnavailableQty", "N",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("IsReturnable","Y",eleOrderLine);
										
										
										eleOrderLine = getStoreRespondByDate(strEnterpriseCode,eleOrderLine);

									}
								}else{
									if(_scBaseUtils.isVoid(_scModelUtils.getStringValueFromPath("FulfillmentType",eleOrderLine))){
										_scModelUtils.setStringValueAtModelPath("FulfillmentType", "PICK_IN_STORE",eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("LineType", "PICK_IN_STORE",eleOrderLine);
										
										_scModelUtils.setStringValueAtModelPath("EarliestProductShipDate",strCurrentDate,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("ReqDeliveryDate",strCurrentDate,eleOrderLine);
										_scModelUtils.setStringValueAtModelPath("ReqShipDate",strCurrentDate,eleOrderLine);
										callChangeOrderFlag = "Y";
									}
								}
						}
				}
				_scScreenUtils.setModel(
		                this, "extn_getOrderFulfillmentDetails_output", modelOutput, null);
				console.log("Final: ",modelOutput);
				
				if(_scBaseUtils.equals(callChangeOrderFlag, "Y")){
					_scModelUtils.setStringValueAtModelPath("Order.SelectMethod","WAIT",modelOutput);
					this.callChangeOrder(modelOutput);
				}
				
			}
			if(
            _scBaseUtils.equals(
            mashupRefId, "extn_vsiPromiseDate_referenceid")){
				console.log("PromiseDate",modelOutput);
				var eleOutput = modelOutput;
				var strOrderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey",eleOutput);
				var strStoreRespondByDt = _scModelUtils.getStringValueFromPath("PromisedDate",eleOutput);
							
				var eleModel = _scBaseUtils.getTargetModel(this, "extn_getOrderFulfillmentDetails_output", null);
				var ndlOrderLines = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine",eleModel);
				for (var i = 0; i < ndlOrderLines.getLength(); i++) {
					var eleOrderLine = ndlOrderLines[0];
					var strstrOrderLineKeyLine = _scModelUtils.getStringValueFromPath("OrderLineKey",eleOrderLine);
					var strAction = _scModelUtils.getStringValueFromPath("Action",eleOrderLine);
					var strLineType = _scModelUtils.getStringValueFromPath("LineType",eleOrderLine);
					if ((_scBaseUtils.equals(strstrOrderLineKeyLine, strOrderLineKey) 
						|| _scBaseUtils.equals("CREATE",strAction)) && _scBaseUtils.equals("SHIP_TO_STORE",strLineType)) {

						_scModelUtils.setStringValueAtModelPath("EarliestProductShipDate", strStoreRespondByDt, eleOrderLine);
						_scModelUtils.setStringValueAtModelPath("ReqDeliveryDate", strStoreRespondByDt, eleOrderLine);
					}
					else{
					}
				}
				_scScreenUtils.setModel(
		                this, "extn_getOrderFulfillmentDetails_output", eleModel, null);
				this.callChangeOrder(eleModel);
			}
			if(
            _scBaseUtils.equals(
            mashupRefId, "extn_changeOrder_referenceid")){
				console.log("Its Done");
			}
		}
		,*/
        callSaveOrder: function(model, mashupContext) 
        {
            var action = null;
            if (!(_scBaseUtils.isVoid(mashupContext))) {
                action = _scBaseUtils.getStringValueFromBean("Action", mashupContext);
            } else {
                mashupContext = _scControllerUtils.getMashupContext(this);
            }
            var conditionsArray = null;
            conditionsArray = [];
            conditionsArray.push(_scBaseUtils.equals(action, "ADDTOORDER"));
            conditionsArray.push(_scBaseUtils.equals(action, "GOTOGIFTOPTIONS"));
            conditionsArray.push(_scBaseUtils.equals(action, "SAVEONPREVIOUS"));
            conditionsArray.push(_scBaseUtils.equals(action, "PREVIOUS"));
            conditionsArray.push(_scBaseUtils.equals(action, "CLOSE"));
            var modelList = null;
            var mashupRefList = null;
            if (_isccsUIUtils.or(conditionsArray)) {
                _isccsUIUtils.callApi(this, model, "modifyFulfillmentOptions", mashupContext);
            } else if (_scBaseUtils.equals(action, "NEXT")) {
                if (_scBaseUtils.equals(this.YCDReservationRule, "Y")) {
                    this.Action = "NEXT";
                    modelList = [];
                    mashupRefList = [];
                    modelList.push(
                    model);
                    mashupRefList.push("modifyFulfillmentOptions");
                    modelList.push(_scBaseUtils.getTargetModel(this, "reserveOrder_input", null));
                    mashupRefList.push("reserveOrderConditionalCall");
                    var options = null;
                    options = {};
                    _scBaseUtils.setAttributeValue("aggregate", false, options);
                    _isccsUIUtils.callApis(this, modelList, mashupRefList, mashupContext, options);
                } else {
                    if (_scWidgetUtils.isWidgetDisabled(this, "update_order")) {
                        var isLargeOrder = null;
                        isLargeOrder = _scBaseUtils.getAttributeValue("Order.IsLargeOrder", false, _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output"));
                        if (_scBaseUtils.negateBoolean(_scBaseUtils.isVoid(isLargeOrder)) && _scBaseUtils.negateBoolean(_scBaseUtils.equals(isLargeOrder, "Y"))) {
                            var ftcDateInput = null;
                            ftcDateInput = _scBaseUtils.getTargetModel(
                            this, "getFulfillmentSummaryDetails_input", null);
                            var massageFTCModel = null;
                            massageFTCModel = _isccsOrderUtils.prepareModelForFtcInput(this, ftcDateInput, null);
                            if (!(_scBaseUtils.isVoid(massageFTCModel))) {
                                var updateOrderModelList = null;
                                updateOrderModelList = [];
                                var mashupRefObj = null;
                                mashupRefObj = {};
                                _scModelUtils.addStringValueToModelObject("mashupRefId", "modifyFulfillmentOptions", mashupRefObj);
                                _scModelUtils.addStringValueToModelObject("mashupInput", massageFTCModel, mashupRefObj);
                                updateOrderModelList.push(
                                mashupRefObj);
                                var screenId = null;
                                screenId = _scBaseUtils.getValueFromPath("declaredClass", this);
                                var args = null;
                                args = _isccsUIUtils.formatArgForUpdateAndNextCall(screenId, updateOrderModelList);
                                _scEventUtils.fireEventToParent(this, "combinedAPICallOnNext", args);
                            } else {
                                _scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
                            }
                        } else {
                            _scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
                        }
                    } else {
                        var updateOrderModelList = null;
                        updateOrderModelList = [];
                        var mashupRefObj = null;
                        mashupRefObj = {};
                        _scModelUtils.addStringValueToModelObject("mashupRefId", "modifyFulfillmentOptions", mashupRefObj);
                        _scModelUtils.addStringValueToModelObject("mashupInput", model, mashupRefObj);
                        updateOrderModelList.push(mashupRefObj);
                        var screenId = null;
                        screenId = _scBaseUtils.getValueFromPath("declaredClass", this);
                        var args = null;
                        args = _isccsUIUtils.formatArgForUpdateAndNextCall(screenId, updateOrderModelList);
                        _scEventUtils.fireEventToParent(this, "combinedAPICallOnNext", args);
                    }
                }
            } else {
                modelList = [];
                mashupRefList = [];
				
				var orderLineModel = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine",model);
				for(i=0;i<orderLineModel.length;i++){
					var orderLine = orderLineModel[i];
					// Updated for BOSTS ARR7 - START
					var isApplyClicked = this.hashMapFuturePickUpLines["APPLY"];
					if(_scBaseUtils.equals(isApplyClicked, "Y")){
						var orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey",orderLine);
						var isFutureAval = this.hashMapFuturePickUpLines[orderLineKey];	
						if(_scBaseUtils.equals(isFutureAval, "Y")){
							_isccsContextUtils.addToContext(orderLineKey, "Y");
							_scModelUtils.setStringValueAtModelPath("LineType","SHIP_TO_STORE", orderLine);
							_scModelUtils.setStringValueAtModelPath("FulfillmentType","SHIP_TO_STORE", orderLine);
							//No need to set the procure from node as sourcing will determine the node
							//_scModelUtils.setStringValueAtModelPath("ProcureFromNode","9001", orderLine);
							//_scModelUtils.setStringValueAtModelPath("IsProcurementAllowed","Y", orderLine);
						} else {					
							_scModelUtils.setStringValueAtModelPath("LineType","PICK_IN_STORE", orderLine);
							_scModelUtils.setStringValueAtModelPath("ProcureFromNode","", orderLine);
							_scModelUtils.setStringValueAtModelPath("IsProcurementAllowed","", orderLine);
							_isccsContextUtils.addToContext(orderLineKey, "N");
						}
						_scModelUtils.setStringValueAtModelPath("ItemID",this.hashMapFuturePickUpLines['itemid_'+orderLineKey], orderLine);					
					}					
					// Updated for BOSTS ARR7 - END
				}
				this.hashMapFuturePickUpLines = {};
                modelList.push(model);
                mashupRefList.push("modifyFulfillmentOptions");
                modelList.push(_scBaseUtils.getTargetModel(this, "behavior_getFulfillmentSummaryDetails_input", null));
                mashupRefList.push("postCall_getFulfillmentSummaryDetails");
                _scControllerUtils.setMashupContextIdenitier(mashupContext, "OrderUpdated");
                var options = null;
                options = {};
                _scBaseUtils.setAttributeValue("aggregate", false, options);
				var mOrderLines = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", model);
				mOrderLines.forEach (function(mOrderLine, index) {
					var lineType = _scModelUtils.getStringValueFromPath("LineType", mOrderLine);
					if (_scBaseUtils.equals(lineType, "PICK_IN_STORE")) {
						_scModelUtils.setStringValueAtModelPath("FulfillmentType","", mOrderLine);
					}
				});
                _isccsUIUtils.callApis(this, modelList, mashupRefList, mashupContext, options);
            }
        },
        save: function(
                bEvent, arguments) {
        var isPageDirty = false;
        var actionPerformed = null;
        actionPerformed = _scModelUtils.getStringValueFromPath("argumentList.Action", arguments);
        if (
        _scBaseUtils.equals(
        actionPerformed, "CLOSE")) {
            if (
            _isccsWidgetUtils.isDirty(
            this)) {
                var mashupContext1 = null;
                mashupContext1 = _scControllerUtils.getMashupContext(
                this);
                mashupContext1["Action"] = "CLOSE";
                this.Action = "CLOSE";
                var args = null;
                args = {};
                args["mashupContext"] = mashupContext1;
                this.updateOrder(
                null, null, null, args);
            } else {
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
        }
        if (
        _scBaseUtils.equals(
        actionPerformed, "NEXT")) {
        	_isccsBaseTemplateUtils.hideMessage(this);
			
			var getFulfillmentDetailsModel = _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output");
			console.log("GetFulfillmentDetailsModel",getFulfillmentDetailsModel);
			
			var pickupGroups = getFulfillmentDetailsModel.Order.PickupGroups;
			var shippingGroups = getFulfillmentDetailsModel.Order.ShippingGroups;
			console.log("pickupGroups", pickupGroups);
			console.log("shippingGroups", shippingGroups);
			
			if (!_scBaseUtils.isVoid(pickupGroups) && !_scBaseUtils.isVoid(shippingGroups)) {
				console.log("ERROR in checkFulfillmentOptions");
				_isccsBaseTemplateUtils.showMessage(this, "extn_delivery_method_message", "error", null);
			} else {
		
				if (!_scBaseUtils.or(
				this.isUnavailbleShpVisible, _scBaseUtils.or(
				this.isUnavailblePickVisible, this.isPickWithoutNodeVisible))) {
					if (!(
					_scWidgetUtils.isWidgetVisible(
					this, "emptyOlMessage"))) {
						var mashupContext2 = null;
						mashupContext2 = _scControllerUtils.getMashupContext(
						this);
						mashupContext2["Action"] = "NEXT";
						this.Action = "NEXT";
						var targetModel = null;
						targetModel = _scBaseUtils.getTargetModel(
						this, "getFulfillmentSummaryDetails_input", null);
						var optimizationType = null;
						var optimizationTypeOutput = null;
						optimizationTypeOutput = _scScreenUtils.getModel(
						this, "optimizationTypeOutput");
						if (!(
						_scBaseUtils.isVoid(
						optimizationTypeOutput))) {
							optimizationType = _scModelUtils.getStringValueFromPath("Order.OptimizationType", optimizationTypeOutput);
							_scModelUtils.setStringValueAtModelPath("Order.OptimizationType", optimizationType, targetModel);
						}
						var targetModel1 = null;
						targetModel1 = _isccsOrderUtils.prepareModel(
						this, targetModel, mashupContext2);
						var options = null;
						options = {};
						options["modified"] = true;
						options["deleted"] = true;
						options["allowEmpty"] = true;
						var addedModifiedtargetModel = null;
						addedModifiedtargetModel = _scBaseUtils.getTargetModel(
						this, "getFulfillmentSummaryDetails_input", options);
						var modelForMFO = null;
						modelForMFO = _isccsOrderUtils.prepareModel(
						this, addedModifiedtargetModel, mashupContext2);
						var orderlines = null;
						orderlines = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", modelForMFO);
						var length = null;
						length = _scBaseUtils.getAttributeCount(
						orderlines);
						var orderLineListArray = null;
						orderLineListArray = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", targetModel1);
						if (!(
						_scBaseUtils.isVoid(
						orderLineListArray))) 
						{
							 for (
									 var i = 0;
									 i < orderLineListArray.length;
									 i = i + 1) 
							 {

								 if(_scBaseUtils.isVoid(orderLineListArray[i].CarrierServiceCode) && _scBaseUtils.equals("SHP",orderLineListArray[i].DeliveryMethod))
								 {
									
								   //_isccsBaseTemplateUtils.showMessage     
									_isccsBaseTemplateUtils.showMessage(
												this, "Level of Service is missing for some or all of the orderline(s)", "error", null); 
									 return;
								 }

							  }
							for (
							var i = 0;
							i < length;
							i = i + 1) {
								var orderLine = null;
								orderLine = orderlines[
								i];
								_scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", targetModel1).push(
								orderLine);
							}
						} else {
							_scModelUtils.addListToModelPath("Order.OrderLines.OrderLine", orderlines, targetModel1);
						}
						this.callSaveOrder(
						targetModel1, mashupContext2);
					} else {
						_isccsBaseTemplateUtils.showMessage(
						this, "cancelledLines", "error", null);
					}
				} else {
					_isccsBaseTemplateUtils.showMessage(
					this, "unavailableLines", "error", null);
				}
			}
        }
        if (
        _scBaseUtils.equals(
        actionPerformed, "SAVEONPREVIOUS") || _scBaseUtils.equals(
        actionPerformed, "ADDTOORDER")) {
            if (
            isPageDirty = _isccsWidgetUtils.isDirty(
            this)) {
                var mashupContext3 = null;
                mashupContext3 = _scControllerUtils.getMashupContext(
                this);
                if (
                _scBaseUtils.equals(
                actionPerformed, "ADDTOORDER")) {
                    mashupContext3["Action"] = "ADDTOORDER";
                    this.Action = "ADDTOORDER";
                } else {
                    mashupContext3["Action"] = "SAVEONPREVIOUS";
                    this.Action = "SAVEONPREVIOUS";
                }
                var args2 = null;
                args2 = {};
                args2["mashupContext"] = mashupContext3;
                this.updateOrder(
                null, null, null, args2);
            } else {
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
        }
    },
    checkIfOrderIsCancelled: function(
    fulfillmentModel) {
        if (
        _scBaseUtils.equals(
        _scModelUtils.getStringValueFromPath("Order.MinOrderStatus", fulfillmentModel), "9000") && _scBaseUtils.equals(
        _scModelUtils.getStringValueFromPath("Order.MaxOrderStatus", fulfillmentModel), "9000")) {
            _scWidgetUtils.showWidget(
            this, "emptyOlMessage", true, null);
        } else {
            _scWidgetUtils.hideWidget(
            this, "emptyOlMessage", true);
        }
    },
		handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "reserveOrderConditionalCall")) {
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "modifyFulfillmentOptions")) {
                this.orderLineList = [];
                var initialInputData = null;
                initialInputData = _scScreenUtils.getModel(
                this, "initialOrder");
                var IsOpenedFrom = null;
                IsOpenedFrom = _scModelUtils.getStringValueFromPath("Order.IsOpenedFrom", initialInputData);
                var isDraftOrder = null;
                isDraftOrder = _scModelUtils.getStringValueFromPath("Order.DraftOrderFlag", modelOutput);
                _scScreenUtils.clearScreen(
                this, null);
                _scWidgetUtils.disableWidget(
                this, "update_order", false);
                this.orderLineList = null;
                this.orderLineList = [];
                _scEventUtils.fireEventToChild(
                this, "orderTotalPanel", "OrderTotalUpdated", null);
                this.loadOrderModelInFulfillmentPanels();
                var action = null;
                action = _scBaseUtils.getStringValueFromBean("Action", mashupContext);
                var isOrderLinesPresent = false;
                if (
                _scBaseUtils.equals(
                isDraftOrder, "Y")) {
                    if (
                    isOrderLinesPresent = _scModelUtils.getNumberValueFromPath("Order.OrderLines.TotalNumberOfRecords", modelOutput) > 0) {
                        this.IsOrderLinesPresent = isOrderLinesPresent;
                    }
                } else {
                    if (
                    _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("Order.MinOrderStatus", modelOutput), "9000") && _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("Order.MaxOrderStatus", modelOutput), "9000")) {
                        isOrderLinesPresent = false;
                        this.IsOrderLinesPresent = isOrderLinesPresent;
                    } else {
                        isOrderLinesPresent = true;
                        this.IsOrderLinesPresent = isOrderLinesPresent;
                    }
                }
                this.handleAfterChangeOrder(
                isDraftOrder, modelOutput, action, isOrderLinesPresent, IsOpenedFrom);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "workOrderApptScreen_getNonDelLines")) {
                var numberOfRecords = 0;
                numberOfRecords = _scModelUtils.getNumberValueFromPath("OrderLineList.TotalNumberOfRecords", modelOutput);
                if (
                _scBaseUtils.equals(
                numberOfRecords, 0)) {
                    _scWizardUtils.showPreviousPage(
                    _isccsUIUtils.getWizardForScreen(
                    this), "AddItems");
                } else {
                    _scWizardUtils.previousScreen(
                    _isccsUIUtils.getWizardForScreen(
                    this));
                }
            }
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_applyBOGO_referenceid")){
				console.log("HERE::: ",modelOutput);
			}
        },
		applyBOGO: function(event, bEvent, ctrl, args){
			var model = _scBaseUtils.getTargetModel(
                        this, "getFulfillmentSummaryDetails_input", null);
			console.log("modifyFulfillmentOptions_input",model);
			_isccsUIUtils.callApi(this, model, "extn_applyBOGO_referenceid");
		},
		extn_InitScreen: function(event, bEvent, ctrl, args) {
			_scWidgetUtils.hideWidget(this, "gift_option", true);
			_scWidgetUtils.disableWidget(this, "shipping_option", true);
			_scWidgetUtils.hideWidget(this, "shipping_option", true);
		
	}
});
});

