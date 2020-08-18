
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/fulfillmentSummary/FSPickupLinesWithNoShipnodeExtnUI",
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
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils"
]
,
function(			 
	_dojodeclare,
	_extnFSPickupLinesWithNoShipnodeExtnUI,
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
	_scWidgetUtils,
	_isccsBaseTemplateUtils
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FSPickupLinesWithNoShipnodeExtn", [_extnFSPickupLinesWithNoShipnodeExtnUI],{
	// custom code here
	// Overriding method for adding OrderLineKey in the input xml for ChangeStore PopUp to Fix SU74
	changeStoreForUnavailableLines: function(event, bEvent, ctrl, args) {
		var selectedRecords = null;
		selectedRecords = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(this, "OLST_listGrid");
		var processedRecords = null;
		processedRecords = this.processSelectedRecordsForStoreSelection(this, "OLST_listGrid", selectedRecords);
		var noOfOrderLine = null;
		noOfOrderLine = _scBaseUtils.getAttributeCount(processedRecords);
		if (noOfOrderLine > 0) {
			var fulfillmentModel = null;
			fulfillmentModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			var callingOrgCode = null;
			callingOrgCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", fulfillmentModel);
			var orderHeaderKey = null;
			orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", fulfillmentModel);
			var ePersonInfo = null;
			ePersonInfo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", fulfillmentModel);
			var orderInfoModel = null;
			var orderModel = null;
			orderInfoModel = {};
			orderModel = {};
			_scModelUtils.addStringValueToModelObject("OrderHeaderKey", orderHeaderKey, orderInfoModel);
			_scModelUtils.addStringValueToModelObject("EnterpriseCode", callingOrgCode, orderInfoModel);
			_scModelUtils.addModelObjectAsChildToModelObject("Order", orderInfoModel, orderModel);
			_isccsSharedComponentUtils.openStoreSelectionForMultipleLines(this, processedRecords, orderModel, ePersonInfo, callingOrgCode, "ViewStores", "changeStoreCallbackForUnavailableLines");
		} else {
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
		}
	},
	changeStoreCallbackForUnavailableLines: function(actionPerformed, model, popupParams) {
		var shipnode = null;
		var tmodel = null;
		var shipNodeDesc = null;
		var orderLineModel = null;
		var sApply = null;
		var currentContainer = null;
		var newModelData = null;
		var gridScreens = null;		
		var argument = null;
		argument = {};
		var mapModelData = {};
		//_scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
		if (_scBaseUtils.equals(actionPerformed, "APPLY")) {
			var orderModel = _scScreenUtils.getModel(
			this, "getCompleteOrderDetails_output");
			var pickupGroups = orderModel.Order.PickupGroups;
			var existingShipNode = null;
			if(!_scBaseUtils.isVoid(pickupGroups))
			{
				existingShipNode = orderModel.Order.PickupGroups.PickupGroup[0].ShipNode;
			}
			if (!(_scBaseUtils.isVoid(model))) {
				shipnode = _scModelUtils.getStringValueFromPath("Node.Availability.ShipNode", model);
				if(!_scBaseUtils.isVoid(existingShipNode) && !_scBaseUtils.isVoid(existingShipNode) && !_scBaseUtils.equals(shipnode,existingShipNode))
				{
				
					_isccsBaseTemplateUtils.showMessage(
                            this, "Pickup at multiple locations is not allowed", "error", null); 
					return;
				}				
				shipNodeDesc = _scModelUtils.getStringValueFromPath("Node.Description", model);				
				// Updated code for ARR7 - START				
				var orderLineList = _scModelUtils.getStringValueFromPath("Node.OrderLines.OrderLine", model);
				for (var j = 0; j < _scBaseUtils.getAttributeCount(orderLineList); j = j + 1) {
					newModelData = {};
					var orderLine = orderLineList[j];
							
					var isAvailable = _scModelUtils.getStringValueFromPath("IsAvailable", orderLine);
					var orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", orderLine);
					var isFutureAvailability = _scModelUtils.getStringValueFromPath("IsFutureAvailability", orderLine);
					// isAvailableOnStore variable added for fix the Defect SU-74. 
					var isAvailableOnStore = _scModelUtils.getStringValueFromPath("IsAvailableOnStore", orderLine);
					var availabilityDate = _scModelUtils.getStringValueFromPath("AvailableDate", orderLine);
					if (_scBaseUtils.equals(isAvailable, "Y") && _scBaseUtils.equals(isFutureAvailability, "Y")) {
						_scModelUtils.setStringValueAtModelPath("IsFutureAvailability", isFutureAvailability, newModelData);
						_scModelUtils.setStringValueAtModelPath("IsAvailableOnStore", isAvailableOnStore, newModelData);
					}
					// Updated code for ARR7 and SU-74- END				
					_scModelUtils.setStringValueAtModelPath("EarliestShipDate", availabilityDate, newModelData);
					_scModelUtils.setStringValueAtModelPath("ReqShipDate", "", newModelData);
					_scModelUtils.setStringValueAtModelPath("ShipNode", shipnode, newModelData);
					
					mapModelData[orderLineKey] = newModelData;
				}
				var selectedLineZero = false;
				var orderLines = null;
				orderLines = [];
				var argumentForStore = null;
				argumentForStore = {};
				this.updateStore(mapModelData);
				var temp = null;
				temp = {};
				_scEventUtils.fireEventToParent(this, "ScreenChanged", temp);
				_scEventUtils.fireEventToParent(this, "SaveOrder", temp);
			}
		}
	},
    updateStore: function(mapModelData) {
		var selectedRecordsList = null;
		selectedRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(this, "OLST_listGrid");
		var noOfOrderLine = null;
		noOfOrderLine = _scBaseUtils.getAttributeCount(selectedRecordsList);
		var model = null;
        model = _scScreenUtils.getModel(
        this, "getCompleteOrderDetails_output");
        
		for (var i = 0; i < noOfOrderLine; i = i + 1) {
			var beanItem = null;
			beanItem = selectedRecordsList[i];
			var lineIndex = null;
			lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
			var srcRowData = null;
			srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(this, "OLST_listGrid", lineIndex);
			// Updated code for adding OrderLineKey in Map for future inventory : ARR7 and SU-74 - START
			var orderLineKeyArr = _scModelUtils.getModelListFromKey("OrderLineKey", srcRowData);
			var orderLineKey = orderLineKeyArr[0];
			// pass the orderlinekey and fetch all required data respective to that orderline from mapdata
			var newModelData = mapModelData[orderLineKey]; 
			this.ownerScreen.hashMapFuturePickUpLines["APPLY"] = "Y"
			var isFutureAvailability = _scModelUtils.getStringValueFromPath("IsFutureAvailability", newModelData);
			// isAvailableOnStore variable added to fix Defect SU-74.
			var isAvailableOnStore = _scModelUtils.getStringValueFromPath("IsAvailableOnStore", newModelData);
			if (_scBaseUtils.equals(isFutureAvailability, "Y") && !_scBaseUtils.equals(isAvailableOnStore, "Y")) {
				// Set each OrderLineKey as key and the flag = 'Y' as value.
				this.ownerScreen.hashMapFuturePickUpLines[orderLineKey] = "Y";
				this.ownerScreen.hashMapFuturePickUpLines['itemid_'+orderLineKey] = srcRowData._dataItem.Item.ItemID;
			}
			
			// Updated code for adding OrderLineKey in Map for future inventory : ARR7 and SU-74 - END
			_scGridxUtils.updateRecordToGridUsingUId(this, "OLST_listGrid", srcRowData, newModelData, true, true);
		}
		//_scGridxUtils.deselectAllRowsInGridUsingUId(this, "OLST_listGrid");
	},
	changeToShipping: function(
	        event, bEvent, ctrl, args) 
	{
	            var selectedRecords = null;
	            selectedRecords = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
	            this, "OLST_listGrid");
	            var noOfOrderLine = null;
	            noOfOrderLine = _scBaseUtils.getAttributeCount(
	            selectedRecords);
	            var model = null;
	            model = _scScreenUtils.getModel(
	            this, "getCompleteOrderDetails_output");
	            var pickupGroups = model.Order.PickupGroups;
	            if(!_scBaseUtils.isVoid(pickupGroups))
            	{
					_isccsBaseTemplateUtils.showMessage(
                            this, "extn_delivery_method_message", "error", null); 
					return;
					
            	}
	            _scModelUtils.setStringValueAtModelPath("Order.Edit", "N", model);
	            var orderLine = null;
	            if (
	            noOfOrderLine > 0) {
	                _isccsSharedComponentUtils.openAddressCapture(
	                this, model, null, _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", _scScreenUtils.getModel(
	                this, "getCompleteOrderDetails_output")), "Change_To_Shipping", "callBackChangeToShipping");
	            } else {
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
	            }
	 },
	// Adding method for adding OrderLineKey in the input xml for ChangeStore PopUp to Fix SU74
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
		var fulfillmentScreen = null;
		fulfillmentScreen = _isccsUIUtils.getParentScreen(this, true);
		var Data1 = null;
		Data1 = _scScreenUtils.getModel(fulfillmentScreen, "getFulfillmentSummaryDetails_output");
		var isLargeOrder = null;
		isLargeOrder = _scModelUtils.getStringValueFromPath("Order.UnavailableLines.PickupLinesWithNoShipnode.IsLargeOrder", Data1);
		if (_scBaseUtils.negateBoolean(_scBaseUtils.isVoid(isLargeOrder)) && _scBaseUtils.equals(isLargeOrder, "Y")) {
			_isccsOrderUtils.setLargeOrderProperty(this, "orderlinestitlepane", "Y");
			_scPaginationUtils.showPaginationBar(this, "OLST_listGrid", false);
		} else {
			var options = null;
			options = {};
			options["screen"] = this;
			_scPaginationUtils.startPaginationWithOutputData(this, "OLST_listGrid", Data1, _scModelUtils.getModelObjectFromPath("Order", Data1), options);
			_scPaginationUtils.hidePaginationBar(this, "OLST_listGrid", true);
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

			_scScreenUtils.showErrorMessageBox(
			this, "Action not allowed for individual Pickup line", "waringCallback", textObj, null);

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
		console.log("grid>>>>",grid);
		_scGridxUtils.selectAllRowsUsingUId(this,"OLST_listGrid");
	}
});
});

