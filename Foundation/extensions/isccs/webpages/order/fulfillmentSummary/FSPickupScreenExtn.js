
scDefine(["scbase/loader!dojo/_base/declare",		
		  "scbase/loader!extn/order/fulfillmentSummary/FSPickupScreenExtnUI",
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/OrderLineUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/SharedComponentUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ControllerUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EventUtils", 
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/PaginationUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function( _dojodeclare,
		  _extnFSPickupScreenExtnUI,
		  _isccsModelUtils, 
		  _isccsOrderLineUtils, 
		  _isccsOrderUtils, 
		  _isccsSharedComponentUtils, 
		  _isccsUIUtils, 
		  _isccsWidgetUtils, 
		  _scBaseUtils, 
		  _scControllerUtils, 
		  _scEventUtils, 
		  _scGridxUtils, 
		  _scModelUtils, 
		  _scPaginationUtils, 
		  _scScreenUtils, 
		  _scWidgetUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FSPickupScreenExtn", [_extnFSPickupScreenExtnUI],{
	// custom code here
	
		changeStore: function(event, bEvent, ctrl, args) {
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var lengthOfSourceRecord = null;
            lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
            selectedSourceRecordsList);
            var orderLines = null;
            orderLines = [];
            if (_scBaseUtils.equals(lengthOfSourceRecord, 0)) {
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
                selectedRecords = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(this, "OLST_listGrid");
                var processedRecords = null;
                processedRecords = this.processSelectedRecordsForStoreSelection(this, "OLST_listGrid", selectedRecords);
                var noOfOrderLine = null;
                noOfOrderLine = _scBaseUtils.getAttributeCount(processedRecords);
                if (noOfOrderLine > 0) {
                    orderLines.push(processedRecords);
                }
                var fulfillmentModel = null;
                fulfillmentModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
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
                orderLineArray = orderLines[0];
                var orderLineList = orderLineArray;
                _isccsSharedComponentUtils.openStoreSelectionForMultipleLines(this, orderLineList, orderModel, ePersonInfo, callingOrgCode, "ViewStores", "changeStoreCallback");
            }
        },
        changeStoreCallback: function(actionPerformed, model, popupParams) {
            var shipnode = null;
            var tmodel = null;
            var shipNodeDesc = null;
            var orderLineModel = null;
            var sApply = null;
			var newModelData = null;
			var mapModelData = {};
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
				
					mapModelData[orderLineKey] = newModelData;
                }
				this.updateStoreData(mapModelData);
            } else {
                /* var argumentForUnselect = null;
                argumentForUnselect = {};
                _scBaseUtils.setAttributeValue("argumentList.Select", false, argumentForUnselect);
                var argument = null;
                argument = {};
                _scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
                _scEventUtils.fireEventToParent(this, "SelectOrUnselectRowInUnavailablePickupPanel", argument);*/
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
        initializeScreen: function(event, bEvent, ctrl, args) {
            this.getOrderModelFromParent();
            var Data = null;
            Data = _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output");
            var shipnodeDesc = null;
            var shipnodeAddress = null;
            shipnodeDesc = _scModelUtils.getStringValueFromPath("Shipnode.Description", Data);
            shipnodeAddress = _scModelUtils.getModelObjectFromPath("Shipnode.ShipNodePersonInfo", Data);
            var sCountry = "";
            sCountry = _scModelUtils.getStringValueFromPath("Country", shipnodeAddress);
            if (!(_scBaseUtils.isVoid(sCountry))) {
                var sAddressKey = "";
                sAddressKey = _scBaseUtils.stringConcat(
                sCountry, "_CityStateZip");
                returnValue = _scScreenUtils.getFormattedString(
                this, sAddressKey, shipnodeAddress);
                var argsList = null;
                argsList = [];
                argsList.push(
                shipnodeDesc);
                argsList.push(
                returnValue);
                var returnValue = null;
                returnValue = _scScreenUtils.getFormattedString(
                this, "StoreAddress", argsList);
                _isccsOrderUtils.setTitle(
                this, returnValue, "orderlinestitlepane");
            } else {
                var noStore = null;
                noStore = _scScreenUtils.getString(this, "NoStore");
                _isccsOrderUtils.setTitle(this, noStore, "orderlinestitlepane");
            }
            var isLargeOrder = null;
            isLargeOrder = _scModelUtils.getStringValueFromPath("IsLargeOrder", Data);
            var options = null;
            options = {};
            options["screen"] = this;
            if (_scBaseUtils.negateBoolean(_scBaseUtils.isVoid(isLargeOrder)) && _scBaseUtils.equals(isLargeOrder, "N")) {
                _scPaginationUtils.startPaginationWithOutputData(this, "OLST_listGrid", Data, Data, options);
                _scGridxUtils.hideTableColumn(this, "OLST_listGrid", "FSStatus", "Product_AddItems");
                _scPaginationUtils.hidePaginationBar(this, "OLST_listGrid", true);
                _scGridxUtils.refreshGridxLayout(this, "OLST_listGrid");
            } else {
                _scPaginationUtils.showPaginationBar(this, "OLST_listGrid", false);
                _isccsOrderUtils.setLargeOrderProperty(this, "orderlinestitlepane", "N");
            }
        },
	onRowDeselect: function(
		event, bEvent, ctrl, args) {
			var deselectedRow = null;
			console.log(args);
			deselectedRow = _scBaseUtils.getBeanValueFromBean("rowIndex", args);

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

			var errorMessage = _scScreenUtils.getString(this, "extn_Row_Deselected_Msg_Pickup");
			_scScreenUtils.showErrorMessageBox(
				this, errorMessage, "waringCallback", textObj, null);

			/**var argument = null;
			argument = {};
			var orderLineKey = null;
			orderLineKey = _scBaseUtils.getStringValueFromBean("OrderLineKey", deselectedRow);
			_scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
			_scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
			_scEventUtils.fireEventToParent(
			this, "SelectOrUnselectRowInPickupPanel", argument);
			_scEventUtils.fireEventToParent(
			this, "SelectOrUnselectRowInUnavailablePickupPanel", argument); **/
			//_scGridxUtils.deselectAllRowsUsingUId(this,"OLST_listGrid");
			//_scGridxUtils.deselectAllRowsInGridUsingUId(this,"OLST_listGrid");
	},
	selectAllRows: function(){
		var grid = this.getWidgetByUId("OLST_listGrid");
		
		_scGridxUtils.selectAllRowsUsingUId(this,"OLST_listGrid");
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
			this, "changeStore");
			_scWidgetUtils.enableWidget(
			this, "changePickupOptions");
			_scWidgetUtils.enableWidget(
			this, "changeToShipping");
			_scWidgetUtils.enableWidget(
			this, "changeToDelivery");
			_scWidgetUtils.enableWidget(
			this, "pickupDate");
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
			this, "SelectOrUnselectRowInUnavailablePickupPanel", argument);
			_scEventUtils.fireEventToParent(
			this, "selectOrUnselectRowInPickupPanel", argument); */
			_scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRowData.rowIndex);
			if (i == 0) {
				var errorMessage = _scScreenUtils.getString(this, "extn_Header_Row_Deselected_Msg");
				_scScreenUtils.showErrorMessageBox(this, errorMessage, "error",null,null);
			}
		}
	}
});
});

