
scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/order/fulfillmentSummary/FSShippingScreenExtnUI",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!isccs/utils/OrderUtils",
		"scbase/loader!sc/plat/dojo/utils/EventUtils",
		"scbase/loader!dijit",
		"scbase/loader!sc/plat/dojo/utils/GridxUtils",
		"scbase/loader!isccs/utils/UIUtils",
		"scbase/loader!isccs/utils/SharedComponentUtils"],
function( 
		_dojodeclare,
		_extnFSShippingScreenExtnUI,
		_scWidgetUtils,
		_scScreenUtils,
		_scModelUtils,
		_scBaseUtils,
		_isccsOrderUtils,
		_scEventUtils,
		_dijit,
		_scGridxUtils,
		_isccsUIUtils,
		_isccsSharedComponentUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FSShippingScreenExtn", [_extnFSShippingScreenExtnUI],{
	// custom code here
	
	extn_InitScreen: function(event, bEvent, ctrl, args) {
		//_scWidgetUtils.disableWidget(this, "changeToPickup", true);
		//_scWidgetUtils.hideWidget(this, "changeToDelivery", true);
		//_scWidgetUtils.enableWidget(this, "changeAddress", true);
		//_scWidgetUtils.enableWidget(this, "changeLOS", true);
		
		//this.checkOrderStatusforAddressChange();

	},
	selectAllRows: function(){
		var grid = this.getWidgetByUId("OLST_listGrid");
		
		_scGridxUtils.selectAllRowsUsingUId(this,"OLST_listGrid");
		this.checkOrderStatusforAddressChange();
	},
	
 checkAndDisplayLOS: function(
	        carrierServiceCode) {
	            var Data = null;
	            Data = _scScreenUtils.getModel(
	            this, "getFulfillmentSummaryDetails_output");
	            var isLargeOrder = null;
	            isLargeOrder = _scModelUtils.getStringValueFromPath("IsLargeOrder", Data);
	            if (
	            _scBaseUtils.negateBoolean(
	            _scBaseUtils.isVoid(
	            isLargeOrder)) && _scBaseUtils.equals(
	            isLargeOrder, "N")) {
	                if (
	                _scBaseUtils.isVoid(
	                carrierServiceCode)) {
	                    _scWidgetUtils.hideWidget(
	                    this, "level_of_Service", true);
	                    _scWidgetUtils.hideWidget(
	                    this, "expected_Date", true);
	                    _scWidgetUtils.setLabel(this,"changeLOS","Assign Level of Service");
	                } else {
	                    _scWidgetUtils.showWidget(
	                    this, "level_of_Service", true, null);
	                    _scWidgetUtils.showWidget(
	                    this, "expected_Date", true, null);
	                   _scWidgetUtils.setLabel(this,"changeLOS","Change Level of Service");
	                }
	            } else {
	                _scWidgetUtils.hideWidget(
	                this, "level_of_Service", true);
	                _scWidgetUtils.hideWidget(
	                this, "expected_Date", true);
	                _scWidgetUtils.setLabel(this,"changeLOS","Assign Level of Service");
	            }
	        },
	
	 updateCarrierService: function(
        newModelData) {
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var selectedTargetRecordsList = null;
            selectedTargetRecordsList = _scGridxUtils.getSelectedTargetRecordsWithRowIndex(
            this, "OLST_listGrid");
            var listOfMergedModel = null;
            listOfMergedModel = this.mergeModel(
            selectedSourceRecordsList, selectedTargetRecordsList);
            var length = null;
            length = _scBaseUtils.getAttributeCount(
            listOfMergedModel);
            for (
            var z = 0;
            z < length;
            z = z + 1) {
                var beanItem = null;
                beanItem = listOfMergedModel[
                z];
                var lineIndex = null;
                lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                var srcRowData = null;
                srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(
                this, "OLST_listGrid", lineIndex);
                var rowJson = null;
                rowJson = _scGridxUtils.getRowJsonFromRowIndex(
                this, "OLST_listGrid", lineIndex);
                var orderLineKey = null;
                orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", rowJson);
                this.handleBundleItems(
                rowJson, newModelData, "CARRIERSERVICE");
                _scGridxUtils.updateRecordToGridUsingUId(
                this, "OLST_listGrid", srcRowData, newModelData, true, true);
            }
            //_scGridxUtils.deselectAllRowsInGridUsingUId(
           // this, "OLST_listGrid");
            _scGridxUtils.refreshGridxLayout(
            this, "OLST_listGrid");
            var temp = null;
            temp = {};
            _scEventUtils.fireEventToParent(
            this, "SaveOrder", temp);
        },
	
	checkOrderStatusforAddressChange: function()
	{
		var model = _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output");
		var orderLine = _scModelUtils.getStringValueFromPath("Page.Output.OrderLines.OrderLine", model);
		var flag = false;
		for(i=0;i<orderLine.length;i++){
			var orderStatus = _scModelUtils.getStringValueFromPath("OrderStatuses.OrderStatus", orderLine[i]);		
			if(_scBaseUtils.isVoid(orderStatus))
			{
				return;
			}
			for(j=0;j<orderStatus.length;j++){				
				var status = _scModelUtils.getNumberValueFromPath("Status", orderStatus[j]);
				//console.log('ss',status);
				var b = 3200;
				if (parseFloat(status.toFixed(2)) > parseFloat(b.toFixed(2)))
				 {				
					flag = true;
					_scWidgetUtils.disableWidget(this, "changeAddress", true);
					_scWidgetUtils.disableWidget(this, "changeLOS", true);
					break;
				}
			}
			if(flag){
				break;
			}
			
		} 
	},
    changeToPickupCallback: function(
            actionPerformed, model, popupParams) {
                var shipnode = null;
                var tmodel = null;
                var shipNodeDesc = null;
                var orderLineModel = null;
                var sApply = null;
                var newModelData = null;
    			var mapModelData = {};
                var availabilityDate = null;
                var argumentForStore = null;
                argumentForStore = {};
                var options = null;
                options = {};
                options["modified"] = true;
                options["deleted"] = true;
                options["allowEmpty"] = true;
                var fulfillmentScreen = null;
                fulfillmentScreen = _isccsUIUtils.getParentScreen(
                this, true);
                var addedModifiedtargetModel = null;
                addedModifiedtargetModel = _scBaseUtils.getTargetModel(
                fulfillmentScreen, "getFulfillmentSummaryDetails_input", options);
                if (_scBaseUtils.equals(actionPerformed, "APPLY")) {
                    shipnode = _scModelUtils.getStringValueFromPath("Node.Availability.ShipNode", model);
                    shipNodeDesc = _scModelUtils.getStringValueFromPath("Node.Description", model);
    				// Updated code for ARR7 - START
    				var orderLineList = _scModelUtils.getStringValueFromPath("Node.OrderLines.OrderLine", model);
    				for (var j = 0; j < _scBaseUtils.getAttributeCount(orderLineList); j = j + 1) {
    					newModelData = {};
    					var orderLine = orderLineList[j];
    							
    					var isAvailable = _scModelUtils.getStringValueFromPath("IsAvailable", orderLine);
    					var orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", orderLine);
    					var isFutureAvailability = _scModelUtils.getStringValueFromPath("IsFutureAvailability", orderLine);
    					// Added isAvailableOnStore flag and condition check to fix Defect SU-74.
    					var isAvailableOnStore = _scModelUtils.getStringValueFromPath("IsAvailableOnStore", orderLine);
    					var availabilityDate = _scModelUtils.getStringValueFromPath("AvailableDate", orderLine);
    					if (_scBaseUtils.equals(isAvailable, "Y") && _scBaseUtils.equals(isFutureAvailability, "Y")) {
    						_scModelUtils.setStringValueAtModelPath("IsFutureAvailability", isFutureAvailability, newModelData);
    						_scModelUtils.setStringValueAtModelPath("IsAvailableOnStore", isAvailableOnStore, newModelData);
    					}
    					// Updated code for ARR7 - END
    					_scModelUtils.setStringValueAtModelPath("EarliestShipDate", availabilityDate, newModelData);
    					_scModelUtils.setStringValueAtModelPath("ShipNode.Description", shipNodeDesc, newModelData);
    					_scModelUtils.setStringValueAtModelPath("ReqShipDate", "", newModelData);
    					_scModelUtils.setStringValueAtModelPath("ShipNode", shipnode, newModelData);
    					_scModelUtils.setStringValueAtModelPath("DeliveryMethod", "PICK", newModelData);
    					mapModelData[orderLineKey] = newModelData;
                    }
    				this.updateStoreData(mapModelData);
                }
    	},
            
    	changeToPickup: function(
    	        event, bEvent, ctrl, args) {
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var lengthOfSourceRecord = null;
            lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
            selectedSourceRecordsList);
            var orderLines = null;
            orderLines = [];
            if (
            _scBaseUtils.equals(
            lengthOfSourceRecord, 0)) {
                var warningString = null;
                warningString = _scScreenUtils.getString(
                this, "WARNING_selecteOneItem");
                var textObj = null;
                textObj = {};
                var textOK = null;
                textOK = _scScreenUtils.getString(
                this, "OK");
                textObj["OK"] = textOK;
                _scScreenUtils.showErrorMessageBox(
                this, warningString, "waringCallback", textObj, null);
            } else {
                var selectedRecords = null;
                selectedRecords = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
                this, "OLST_listGrid");
                var processedRecords = null;
                processedRecords = this.processSelectedRecordsForStoreSelection(
                this, "OLST_listGrid", selectedRecords);
                var noOfOrderLine = null;
                noOfOrderLine = _scBaseUtils.getAttributeCount(
                processedRecords);
                if (
                noOfOrderLine > 0) {
                    orderLines.push(
                    processedRecords);
                }
                var fulfillmentModel = null;
                fulfillmentModel = _scScreenUtils.getModel(
                this, "getCompleteOrderDetails_output");
                var callingOrgCode = null;
                callingOrgCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", fulfillmentModel);
                var orderHeaderKey = null;
                orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", fulfillmentModel);
                var ePersonInfo = null;
                ePersonInfo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", fulfillmentModel);
                var orderInfoModel = null;
                orderInfoModel = {};
                var orderModel = null;
                orderModel = {};
                _scModelUtils.addStringValueToModelObject("OrderHeaderKey", orderHeaderKey, orderInfoModel);
                _scModelUtils.addStringValueToModelObject("EnterpriseCode", callingOrgCode, orderInfoModel);
                _scModelUtils.addModelObjectAsChildToModelObject("Order", orderInfoModel, orderModel);
                var orderLineArray = null;
                orderLineArray = orderLines[
                0];
                var orderLineList = orderLineArray;
                _isccsSharedComponentUtils.openStoreSelectionForMultipleLines(
                this, orderLineList, orderModel, ePersonInfo, callingOrgCode, "ViewStores", "changeToPickupCallback");
            }
        },
     // Added below method as part of SU74 Fix
		processSelectedRecordsForStoreSelection: function(screen,uId,selectedRecords){
			/*This util will take the array of records, fetch the OrderLineKey & Qty and return an array back which contain orderLines which have only these two attributes.
						  This is used to prepare the model to send the orderLines to the select storePopup.*/
			var orderLines = [];
			for(var i=0;i<selectedRecords.length;i++){
				// to clean up and add a model despite of removing
				var currentRecord = selectedRecords[i].rowData;
				var orderlineKey = _scBaseUtils.getAttributeValue("OrderLineKey",false,currentRecord);
				var giftWrap = _scBaseUtils.getAttributeValue("GiftWrap",false,currentRecord);
				
				
				
				var itemObj = _scModelUtils.getModelObjectFromPath("Item", currentRecord);
				itemObj.UOMDisplayFormat = "";
				itemObj.DisplayUnitOfMeasure = "";
				_scBaseUtils.removeBlankAttributes(itemObj);
				var reqQty = _scBaseUtils.getAttributeValue("OrderedQty",false,currentRecord);
				var orderLine = {};
				
				if(itemObj){
					orderLine.Item = {};
					orderLine.Item=currentRecord.Item;
				}
				orderLine.OrderLineKey= orderlineKey;
				orderLine.GiftWrap= giftWrap;
				orderLine.RequiredQty = reqQty;
				orderLines.push(orderLine);
			}
			return orderLines;
		},
            formInputForLOS: function(
                    orderLines, orderModel, groupData) {
                        var apiInput = null;
                        apiInput = {};
                        var input = null;
                        input = {};
                        _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel), input);
                        _scModelUtils.addModelToModelPath("Order.PersonInfoShipTo", _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", orderModel), input);
                        var length = null;
                        length = _scBaseUtils.getAttributeCount(
                        orderLines);
                        var orderLinesArray = null;
                        orderLinesArray = [];
                        for (
                        var i = 0;
                        i < length;
                        i = i + 1) {
                            var orderLine = null;
                            orderLine = {};
                            var orderLineElem = null;
                            orderLineElem = orderLines[
                            i];
                            _scModelUtils.setStringValueAtModelPath("ItemID", _scModelUtils.getStringValueFromPath("Item.ItemID", orderLineElem), orderLine);
                            _scModelUtils.setStringValueAtModelPath("IsParcelShippingAllowed", _scModelUtils.getStringValueFromPath("ItemDetails.PrimaryInformation.IsParcelShippingAllowed", orderLineElem), orderLine);
                            _scModelUtils.setStringValueAtModelPath("OrderLineKey", _scModelUtils.getStringValueFromPath("OrderLineKey", orderLineElem), orderLine);
                            _scModelUtils.setStringValueAtModelPath("EarliestShipDate", _scModelUtils.getStringValueFromPath("EarliestShipDate", orderLineElem), orderLine);
                             _scModelUtils.setStringValueAtModelPath("ShipNode",groupData.Node, orderLine);
                            if (!(
                            _scBaseUtils.isVoid(
                            _scModelUtils.getModelObjectFromKey("PersonInfoShipTo", orderLineElem)))) {
                                _scModelUtils.addModelToModelPath("PersonInfoShipTo", _scModelUtils.getModelObjectFromKey("PersonInfoShipTo", orderLineElem), orderLine);
                            }
                            orderLinesArray.push(
                            orderLine);
                        }
                        _scModelUtils.addListToModelPath("Order.OrderLines.OrderLine", orderLinesArray, input);
                        return input;
                    },
                    
           
            updateShippingAddress: function(
                    orderModel) {
                        var selectedSourceRecordsList = null;
                        selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
                        this, "OLST_listGrid");
                        var selectedTargetRecordsList = null;
                        selectedTargetRecordsList = _scGridxUtils.getSelectedTargetRecordsWithRowIndex(
                        this, "OLST_listGrid");
                        var listOfMergedModel = null;
                        listOfMergedModel = this.mergeModel(
                        selectedSourceRecordsList, selectedTargetRecordsList);
                        var newModelData = null;
                        newModelData = {};
                        _scModelUtils.addModelToModelPath("PersonInfoShipTo", _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", orderModel), newModelData);
                        _scModelUtils.addStringValueToModelObject("DeliveryMethod", "SHP", newModelData);
                        _scModelUtils.addStringValueToModelObject("ShipNode", " ", newModelData);
                        var orderLineKeyList = null;
                        orderLineKeyList = _scModelUtils.getModelListFromPath("Order.OrderLineKeyList", orderModel);
                        var lengthOfSourceRecord = null;
                        lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
                        listOfMergedModel);
                        for (
                        var z = 0;
                        z < lengthOfSourceRecord;
                        z = z + 1) {
                            var beanItem = null;
                            beanItem = listOfMergedModel[
                            z];
                            var lineIndex = null;
                            lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                            var srcRowData = null;
                            srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(
                            this, "OLST_listGrid", lineIndex);
                            var rowJson = null;
                            rowJson = _scGridxUtils.getRowJsonFromRowIndex(
                            this, "OLST_listGrid", lineIndex);
                            var orderLineKey = null;
                            orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", rowJson);
                            var isInArray = false;
                            var isAnyRowUpdated = false;
                            isInArray = this.isInArray(
                            orderLineKey, orderLineKeyList);
                            if (
                            isInArray) {
                                _scGridxUtils.updateRecordToGridUsingUId(
                                this, "OLST_listGrid", srcRowData, newModelData, true, true);
                                isAnyRowUpdated = true;
                            }
                        }
                        if (
                        isAnyRowUpdated) {
                            var temp = null;
                            temp = {};
                            _scEventUtils.fireEventToParent(
                            this, "ScreenChanged", temp);
                        }
                      
                    },
                    
                    updateStoreData: function(mapModelData) {
                        var selectedSourceRecordsList = null;
                        selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(this, "OLST_listGrid");
                        var selectedTargetRecordsList = null;
                        selectedTargetRecordsList = _scGridxUtils.getSelectedTargetRecordsWithRowIndex(this, "OLST_listGrid");
                        var listOfMergedModel = null;
                        listOfMergedModel = this.mergeModel(selectedSourceRecordsList, selectedTargetRecordsList);
                        var lengthOfSourceRecord = null;
                        lengthOfSourceRecord = _scBaseUtils.getAttributeCount(listOfMergedModel);
                        var countToCallUpdateOrder = 0;
                        for (var z = 0; z < lengthOfSourceRecord; z = z + 1) {
                            var beanItem = null;
                            beanItem = selectedSourceRecordsList[z];
                            var lineIndex = null;
                            lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                            var srcRowData = null;
                            srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(this, "OLST_listGrid", lineIndex);
                            var shipNodeColData = null;
                            shipNodeColData = _scModelUtils.getModelListFromKey("ShipNode", srcRowData);
                            var currentShipNode = null;
                            currentShipNode = shipNodeColData[0];
                            var currentShipNodeStr = currentShipNode;
                            var newShipNode = null;
            				// Updated code for adding OrderLineKey in Map for future inventory : ARR7 and SU -74 - START
            				var orderLineKeyArr = _scModelUtils.getModelListFromKey("OrderLineKey", srcRowData);
            				var orderLineKey = orderLineKeyArr[0];
            				// pass the orderlinekey and fetch all required data respective to that orderline from mapdata
            				var newModelData = mapModelData[orderLineKey];
                            newShipNode = _scModelUtils.getStringValueFromPath("ShipNode", newModelData);
                            if (!(_scBaseUtils.equals(currentShipNodeStr, newShipNode))) {
            					this.ownerScreen.hashMapFuturePickUpLines["APPLY"] = "Y"
            					var isFutureAvailability = _scModelUtils.getStringValueFromPath("IsFutureAvailability", newModelData);
            					// Added isAvailableOnStore flag and condition check to fix Defect SU-74.
            					var isAvailableOnStore = _scModelUtils.getStringValueFromPath("IsAvailableOnStore", newModelData);
            					if (_scBaseUtils.equals(isFutureAvailability, "Y") && !_scBaseUtils.equals(isAvailableOnStore, "Y")) {
            						// Set each OrderLineKey as key and the flag = 'Y' as value.
            						this.ownerScreen.hashMapFuturePickUpLines[orderLineKey] = "Y";
            						this.ownerScreen.hashMapFuturePickUpLines['itemid_'+orderLineKey] = srcRowData._dataItem.Item.ItemID;
            					}
            					
                                _scGridxUtils.updateRecordToGridUsingUId(this, "OLST_listGrid", srcRowData, newModelData, true, true);
                                countToCallUpdateOrder = countToCallUpdateOrder + 1;
                            }
            				// Updated code for adding OrderLineKey in Map for future inventory : ARR7 and SU-74 - END
                        }
                        //_scGridxUtils.deselectAllRowsInGridUsingUId(this, "OLST_listGrid");
                        if (countToCallUpdateOrder > 0) {
                            var temp2 = null;
                            temp2 = {};
                            _scEventUtils.fireEventToParent(this, "SaveOrder", temp2);
                        }
            		},
                            
                            OLST_handlePaginationAction: function(
                                    pageToShow, pageSize) {
                            var isGridDirty = false;
                            isGridDirty = _scGridxUtils.isGridDirty(
                            this, "OLST_listGrid", true);
                            if (
                            (
                            isGridDirty)) {
                                var msg = null;
                                var eventArg = null;
                                eventArg = {};
                                _scBaseUtils.setAttributeValue("pageToShow", pageToShow, eventArg);
                                _scBaseUtils.setAttributeValue("pageSize", pageSize, eventArg);
                                msg = _scScreenUtils.getString(
                                this, "PaginationConfirmationMessage");
                                _scScreenUtils.showConfirmMessageBox(
                                this, msg, "handlePaginationActionConfirmation", null, eventArg);
                            } else {
                               // _scGridxUtils.deselectAllRowsInGridUsingUId(
                               // this, "OLST_listGrid");
                            	this.checkOrderStatusforAddressChange();
                                var mashupInput = null;
                                var mashupContext = null;
                                mashupInput = _scPaginationUtils.getInitialInputForPaginatedGrid(
                                this, "OLST_listGrid");
                                mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
                                this, "OLST_listGrid", pageSize, pageToShow, null, null, null);
                                _scWidgetUtils.hideWidget(
                                this, "pnlMessage", false);
                                var lastModifiedDate = null;
                                var wizdata = null;
                                wizdata = _isccsUIUtils.getWizardModel(
                                this, "updateDateTimeOnOrderHeader_output");
                                lastModifiedDate = _scBaseUtils.getValueFromPath("Order.LastModfiedDate", wizdata);
                                _scModelUtils.setStringValueAtModelPath("OrderLine.UIModifyts", lastModifiedDate, mashupInput);
                                _isccsUIUtils.callApi(
                                this, mashupInput, "getCompleteOrderLineList", mashupContext);
                            }
                        },
                        closeOfUnavailableLinesPoup: function(
                                actionPerformed, model, popupParams) {
                                    if (
                                    _scBaseUtils.equals(
                                    actionPerformed, "APPLY")) {
                                        var temp = null;
                                        temp = {};
                                        var orderModel = null;
                                        orderModel = this.formModelForShippingAddress(
                                        model);
                                        this.updateShippingAddress(
                                        orderModel);
                                        _scEventUtils.fireEventToParent(
                                        this, "SaveOrder", temp);
                                    } else if (
                                    _scBaseUtils.equals(
                                    actionPerformed, "APPLYTOALL")) {
                                        var temp = null;
                                        temp = {};
                                        this.updateShippingAddressToAllLines(
                                        model);
                                        _scEventUtils.fireEventToParent(
                                        this, "SaveOrder", temp);
                                    } else {
                                       // _scGridxUtils.deselectAllRowsInGridUsingUId(
                                       // this, "OLST_listGrid");
                                    	this.checkOrderStatusforAddressChange();
                                    }
                                },
                    onRowSelect: function(
                            event, bEvent, ctrl, args) 
                      {
                                var selectedRow = null;
                                selectedRow = _scBaseUtils.getModelValueFromBean("selectedRow", args);
                                var argument = null;
                                argument = {};
                                var orderLineKey = null;
                                orderLineKey = _scModelUtils.getStringValueFromKey("OrderLineKey", selectedRow);
                                _scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
                                _scBaseUtils.setAttributeValue("argumentList.Select", "Y", argument);
                                this.disableOptionsBasedOnLinePermission(
                                selectedRow);
                                _scEventUtils.fireEventToParent(
                                this, "SelectOrUnselectRowInUnavailableShippingPanel", argument);
                                _scEventUtils.fireEventToParent(
                                this, "SelectOrUnselectRowInShippingPanel", argument);
                                this.checkOrderStatusforAddressChange();
                        },
                    handlePaginationActionConfirmation: function(
                            res, args) {
                                var paginationContext = null;
                                var pageToShow = null;
                                var pageSize = null;
                                if (
                                _scBaseUtils.equals(
                                res, "Ok")) {
                                   // _scGridxUtils.deselectAllRowsInGridUsingUId(
                                   // this, "OLST_listGrid");
                                	this.checkOrderStatusforAddressChange();
                                    pageToShow = _scBaseUtils.getAttributeValue("pageToShow", false, args);
                                    pageSize = _scBaseUtils.getAttributeValue("pageSize", false, args);
                                    paginationContext = {};
                                    _scBaseUtils.setAttributeValue("pageToShow", pageToShow, paginationContext);
                                    _scBaseUtils.setAttributeValue("pageSize", pageSize, paginationContext);
                                    var eventArg = null;
                                    eventArg = {};
                                    _scBaseUtils.setAttributeValue("appPaginatedContext", paginationContext, eventArg);
                                    _scBaseUtils.setAttributeValue("Action", "SAVE_ORDER", eventArg);
                                    _scEventUtils.fireEventInsideScreen(
                                    this, "saveCurrentPage", null, eventArg);
                                }
                            },
                    onRowDeselect: function(
                    event, bEvent, ctrl, args) {
                        var deselectedRow = null;
                        deselectedRow = _scBaseUtils.getBeanValueFromBean("rowIndex", args);
                        var argument = null;
                        argument = {};
                        var orderLineKey = null;
                        orderLineKey = _scBaseUtils.getStringValueFromBean("OrderLineKey", deselectedRow);
                        _scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
                        _scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
                        //alert('hiii');
                       // _scEventUtils.fireEventToParent(
                       // this, "SelectOrUnselectRowInUnavailableShippingPanel", argument);
                       // _scEventUtils.fireEventToParent(
                       // this, "SelectOrUnselectRowInShippingPanel", argument);
                        this.checkOrderStatusforAddressChange();
                        var warningString = null;
            			warningString = _scScreenUtils.getString(
            			this, "WARNING_selecteOneItem");
            			var textObj = null;
            			textObj = {};
            			var textOK = null;
            			textOK = _scScreenUtils.getString(
            			this, "OK");
            			textObj["OK"] = textOK;
            			_scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRow);
						var errorMessage = _scScreenUtils.getString(this, "extn_Row_Deselected_Msg_Shipping");
            			_scScreenUtils.showErrorMessageBox(
            				this, errorMessage, "waringCallback", textObj, null);
            			
                    },
                    getCarrierServiceDateRange: function(
                    gridReference, rowNumber, columnIndex, gridRowJSON, unformattedValue) {
                        var length = 0;
                        var descField = null;
                        var shippingGrp = null;
                        shippingGrp = _scScreenUtils.getModel(
                        this, "getFulfillmentSummaryDetails_output");
                        var inputArray = null;
                        inputArray = [];
                        var formattedEndDate = null;
                        formattedEndDate = _scBaseUtils.getStringValueFromBean("ExpectedDeliveryEndDate", shippingGrp);
                        var formattedStartDate = null;
                        formattedStartDate = _scBaseUtils.getStringValueFromBean("ExpectedDeliveryStartDate", shippingGrp);
                        
                        if(!_scBaseUtils.isVoid(formattedEndDate)){
                        	formattedEndDate = new Date(formattedEndDate);
                            formattedEndDate = 
                            	formattedEndDate.getUTCMonth() + 1 + '/' + formattedEndDate.getUTCDate() + '/' 
                            	+ formattedEndDate.getUTCFullYear().toString().substr(-2);
                        }
                        if(!_scBaseUtils.isVoid(formattedStartDate)){
                        	formattedStartDate = new Date(formattedStartDate);
                        	formattedStartDate = 
                        		formattedStartDate.getUTCMonth() + 1 + '/' + formattedStartDate.getUTCDate() + '/' 
                        		+ formattedStartDate.getUTCFullYear().toString().substr(-2);
                        }
                        console.log("formattedEndDate",formattedEndDate);
                        console.log("formattedStartDate",formattedStartDate);
                        var formattedStartServerDate = null;
                        var formattedEndServerDate = null;
                        if (
                        _scBaseUtils.equals(
                        formattedStartDate, formattedEndDate)) {
                            formattedStartServerDate = formattedStartDate;
                            inputArray.push(
                            formattedStartServerDate);
                            descField = _scScreenUtils.getFormattedString(
                            this, "ExpectedDateValue", inputArray);
                        } else {
                            formattedStartServerDate = formattedStartDate;
                            formattedEndServerDate = formattedEndDate;
                            inputArray.push(
                            formattedStartServerDate);
                            inputArray.push(
                            formattedEndServerDate);
                            descField = _scScreenUtils.getFormattedString(
                            this, "ExpectedDateRangeValue", inputArray);
                        }
                        return descField;
                    },
        onRowOrHeaderDeselect: function(
        event, bEvent, ctrl, args) {
            var argument = null;
            argument = {};
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var lengthOfSourceRecord = null;
            lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
            selectedSourceRecordsList);
            if (
            _scBaseUtils.equals(
            lengthOfSourceRecord, 0)) {
                _scWidgetUtils.enableWidget(
                this, "changeToPickup");
                _scWidgetUtils.enableWidget(
                this, "changeLOS");
                _scWidgetUtils.enableWidget(
                this, "changeToDelivery");
                _scWidgetUtils.enableWidget(
                this, "changeAddress");
            } else {
                this.validateSelectedRecordsForPermission(
                selectedSourceRecordsList, lengthOfSourceRecord);
            }
            var deselectedRowsList = null;
            deselectedRowsList = _scBaseUtils.getListFromBean("deselectedRows", args);
            var rowCount = 0;
            rowCount = _scBaseUtils.getAttributeCount(
            deselectedRowsList);
            for (
            var i = 0;
            i < rowCount;
            i = i + 1) {
                var deselectedRowData = null;
                deselectedRowData = deselectedRowsList[
                i];
                /* var rowData = null;
                rowData = _scBaseUtils.getAttributeValue("rowData", false, deselectedRowData);
                var orderLineKey = null;
                orderLineKey = _scModelUtils.getStringValueFromKey("OrderLineKey", rowData);
                _scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
                _scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
                _scEventUtils.fireEventToParent(
                this, "SelectOrUnselectRowInUnavailableShippingPanel", argument);
                _scEventUtils.fireEventToParent(
                this, "SelectOrUnselectRowInShippingPanel", argument); */
				_scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRowData.rowIndex);
				if (i == 0) {
					var errorMessage = _scScreenUtils.getString(this, "extn_Header_Row_Deselected_Msg");
					_scScreenUtils.showErrorMessageBox(this, errorMessage, "error",null,null);
				}
            }
        }
                    /* ARH-55 : Added to initialize model used to check LOS on the lines for large orders : BEGIN
                    ,
					titlePaneOnHide: function(
						event, bEvent, ctrl, args) {
							
							var argsBean = null;
							var input = null;
							input = _scModelUtils.getModelObjectFromPath("Input", _scScreenUtils.getModel(
							this, "getFulfillmentSummaryDetails_output"));
							argsBean = {};
							_scBaseUtils.setAttributeValue("inputData", input, argsBean);
							_scBaseUtils.setAttributeValue("screen", this, argsBean);
							console.log("argsBean", argsBean);
							this.LST_executeApi(
							null, null, null, argsBean);
						}
					ARH-55 : Added to initialize model used to check LOS on the lines for large orders : END */
});
});

