
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/common/alternateStore/StoreSelectionExtnUI",
	"scbase/loader!isccs/utils/BaseTemplateUtils", 
	"scbase/loader!isccs/utils/EventUtils", 
	"scbase/loader!isccs/utils/UIUtils", 
	"scbase/loader!isccs/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/GridxUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils"
	]
,
function(			 
	_dojodeclare,
	_extnStoreSelectionExtnUI,
	_isccsBaseTemplateUtils, 
	_isccsEventUtils,
	_isccsUIUtils, 
	_isccsWidgetUtils, 
	_scBaseUtils, 
	_scGridxUtils, 
	_scModelUtils, 
	_scScreenUtils, 
	_scWidgetUtils
){ 
	return _dojodeclare("extn.common.alternateStore.StoreSelectionExtn", [_extnStoreSelectionExtnUI],{
	// custom code here
	findStores: function(event, bEvent, ctrlId, args) {
		if (_scScreenUtils.validate(this)) {
			var popupData = _isccsBaseTemplateUtils.getPopupOptions(this);
			var nodeInput = null;
			nodeInput = _scScreenUtils.getTargetModel(this, "getAlternateStoreAvailability_input", null);
			var screenInput = null;
			screenInput = _scScreenUtils.getModel(this, "screenInput");
			//Code Modification for Defect SU-31 - START
			//Added below title condition for disable the apply button at Store Selection Pop Up
			var order = _scModelUtils.getModelObjectFromPath("AlternateStore.Order", screenInput);
			var title = popupData["title"];
			if(!_scBaseUtils.equals(title, "Select/View Store")){
				_scWidgetUtils.hideWidget(this, "Popup_btnNext", false);
                var close = "Close";
                close = _scScreenUtils.getString(this, close);
                _scWidgetUtils.setLabel(this, "Popup_btnCancel", close);
				delete order["InvokedFromPickUpScreen"];
			}
			//Code Modification for Defect SU-31 - END
			// Updated code for stamping ProductClass at Item Level to call getAlternateStoreAvailability : ARR7 - START
			var orderLines = _scModelUtils.getModelObjectFromPath("AlternateStore.OrderLines", screenInput);
			var getOrderLineList = _scModelUtils.getStringValueFromPath("OrderLine", orderLines);
			for (var i = 0; i < _scBaseUtils.getAttributeCount(getOrderLineList); i = i + 1) {
				var orderLine = getOrderLineList[i];
				_scModelUtils.setStringValueAtModelPath("Item.ProductClass", "GOOD", orderLine);
			}
			_scModelUtils.addModelToModelPath("AlternateStore.OrderLines", orderLines, nodeInput);
			// Updated code for stamping ProductClass at Item Level to call getAlternateStoreAvailability : ARR7 - END
			_scModelUtils.addModelToModelPath("AlternateStore.Order", order, nodeInput);
			var options = null;
			options = {};
			_scBaseUtils.setAttributeValue("screen", this, options);
			_scBaseUtils.setAttributeValue("mashupRefId", "getAlternateStoreAvailability", options);
			_isccsUIUtils.startPagination(this, "listStore", nodeInput, options);
		} else {
			_isccsBaseTemplateUtils.showMessage(this, "Please_Provide_Search_Criteria", "error", null);
			this.blankSearchResults();
		}
	},
	afterPagingload: function(event, bEvent, ctrl, args) {
		var modelOutput = null;
		modelOutput = _scBaseUtils.getValueFromPath("result.Page.Output", args);
		if (!(_scBaseUtils.isEmptyArray(_scModelUtils.getModelListFromPath("AlternateStores.NodeList.Node", modelOutput)))) {
			var orderlines = _scModelUtils.getModelObjectFromPath("AlternateStores.OrderLines", modelOutput);
			this.setModel("extn_OrderLines", orderlines);
			_scGridxUtils.selectRowUsingUId(this, "listStore", 0);
		} else {
			this.blankSearchResults();
		}		
	},
	onApply: function(event, bEvent, ctrl, args) {
		var selectedShipNode = null;
		selectedShipNode = _scScreenUtils.getModel(this, "selectedShipNode_output");
		if (!(_scBaseUtils.isVoid(selectedShipNode))) {
			if (_scModelUtils.getBooleanValueFromPath("Node.Availability.IsAvailable", selectedShipNode, false)) {
				// Code Fix for Defect SU-74 - START
				// Adding new node OrderLines in selectedShipNode model
				var shipNode = _scModelUtils.getStringValueFromPath("Node.ShipNode", selectedShipNode);
				var newOrderLines = _scModelUtils.getOrCreateChildModelObject("OrderLines", _scModelUtils.getModelObjectFromPath("Node", selectedShipNode));
				var newOrderLineList = _scModelUtils.createModelListFromKey("OrderLine", newOrderLines);
				
				// extn_OrderLines model is set afterPaging, which will contain output as per the extended template of getAlternateStoreAvailability API.
				var orderLineList = _scModelUtils.getStringValueFromPath("OrderLine", _scScreenUtils.getModel(this, "extn_OrderLines"));
				if(!_scBaseUtils.isVoid(orderLineList)){
					//Iterate over lines
					for (var i = 0; i < _scBaseUtils.getAttributeCount(orderLineList); i = i + 1) {
						var orderLine = orderLineList[i];
						var orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey",orderLine);
						var availabilityList = _scModelUtils.getStringValueFromPath("AvailabilityList.Availability", orderLine);
						//Iterate over Availability to the node
						for (var j = 0; j < _scBaseUtils.getAttributeCount(availabilityList); j = j + 1) {
							var availability = availabilityList[j];
							var orderLineShipNode = _scModelUtils.getStringValueFromPath("ShipNode",availability);
							if (_scBaseUtils.equals(orderLineShipNode, shipNode)){
								
								var isFutureAvailability = _scModelUtils.getStringValueFromPath("IsFutureAvailability",availability);
								var isAvailabile= _scModelUtils.getStringValueFromPath("IsAvailable",availability);
								var availableDate = _scModelUtils.getStringValueFromPath("AvailableDate",availability);
								// Added below attributes for fixing SU-74 
								var quantity = _scModelUtils.getStringValueFromPath("Quantity",availability);
								var procuredQty = _scModelUtils.getStringValueFromPath("ProcuredQty",availability);
								var isAvailableOnStore = _scModelUtils.getStringValueFromPath("IsAvailableOnStore",availability);
								// set the all attributes for each orderline (each item) for selected node.
								var node = {};
								_scModelUtils.setStringValueAtModelPath("OrderLineKey", orderLineKey, node);
								_scModelUtils.setStringValueAtModelPath("IsFutureAvailability", isFutureAvailability, node);
								_scModelUtils.setStringValueAtModelPath("IsAvailable", isAvailabile, node);
								_scModelUtils.setStringValueAtModelPath("AvailableDate", availableDate, node);
								_scModelUtils.setStringValueAtModelPath("IsAvailableOnStore", isAvailableOnStore, node);
								_scModelUtils.setStringValueAtModelPath("Quantity", quantity, node);
								_scModelUtils.setStringValueAtModelPath("ProcuredQty", procuredQty, node);
								_scModelUtils.addModelObjectToModelList(node, newOrderLineList);
							}
						}
					}
				}
				// Code Fix for Defect SU-74 - END
				_scScreenUtils.setPopupOutput(this, selectedShipNode);
				_scWidgetUtils.closePopup(this, "APPLY", false);
			} else {
				_scScreenUtils.showErrorMessageBox(
				this, _scScreenUtils.getString(
				this, "The_Selected_Store_Has_No_Availability"), "waringCallback", null);
			}
		}
	},
	getLinkText: function(event, bEvent, ctrlId, args) {
		//console.log("args", args);
		var isAvailable = _scModelUtils.getStringValueFromPath("item.Availability.IsAvailable", args);
		var dateText;
		//console.log("isAvailable", isAvailable);
		if (_scBaseUtils.equals(isAvailable, "Y")){
			var isFutureAvailable = _scModelUtils.getStringValueFromPath("item.Availability.IsFutureAvailability", args);
			//console.log("isFutureAvailable", isFutureAvailable);
			if (_scBaseUtils.equals(isFutureAvailable, "Y")){
				dateText =  _scModelUtils.getStringValueFromPath("item.Availability.AvailableDate", args);
			}else{
				dateText = "Today";
			}
		}else {
			dateText = "Not Available";
		}
		var linkText={};
		_scModelUtils.setStringValueAtModelPath("response", dateText, linkText);
		return linkText;
	},
	handleLinkClicked: function(event, bEvent, ctrlId, args) {
		var refGrid = this.getWidgetByUId("listStore");
		var popupParams = null;
		var orderContent = null;
		popupParams = {};
		var dialogParams = null;
		dialogParams = {};
		dialogParams["closeCallBackHandler"] = null;
		dialogParams["applyButton"] = "Y";
		//Code Added for fix of Defect SU-31 - START
		var node = _scModelUtils.createNewModelObjectWithRootKey("Node");
		var items = _scModelUtils.getOrCreateChildModelObject("Items", _scModelUtils.getModelObjectFromPath("Node", node));
		var itemList = _scModelUtils.createModelListFromKey("Item", items);
		 
		//get the ship node which is selected by user
		var selectedShipNode = _scModelUtils.getStringValueFromPath("Node.ShipNode", _scScreenUtils.getModel(this, "selectedShipNode_output"));
		
		// fetch the order lines from the extn_OrderLines model(set from page output model) to get all required data from AvailabilityList level
		//console.log("extn_OrderLines>>>>>",_scScreenUtils.getModel(this, "extn_OrderLines"));
		var orderLineList = _scModelUtils.getStringValueFromPath("OrderLine", _scScreenUtils.getModel(this, "extn_OrderLines"));
		if(!_scBaseUtils.isVoid(orderLineList)){
			//Iterate Order lines
			for (var i = 0; i < _scBaseUtils.getAttributeCount(orderLineList); i = i + 1) {
				var orderLine = orderLineList[i];
				var ordLnItem = _scModelUtils.getStringValueFromPath("Item", orderLine);
				var itemID = _scModelUtils.getStringValueFromPath("ItemID", ordLnItem);
				var availabilityList = _scModelUtils.getStringValueFromPath("AvailabilityList.Availability", orderLine);
				//Iterate over Availability to the node
				for (var j = 0; j < _scBaseUtils.getAttributeCount(availabilityList); j = j + 1) {
					var availability = availabilityList[j];
					var orderLineShipNode = _scModelUtils.getStringValueFromPath("ShipNode",availability);
					// set the all attributes for each orderline (each item) for selected ship node only.
					if(_scBaseUtils.equals(orderLineShipNode, selectedShipNode)){
						var onHandQty = _scModelUtils.getStringValueFromPath("OnHandQuantity",availability);
						var onStoreAvaialbleDate = _scModelUtils.getStringValueFromPath("OnStoreAvaialbleDate",availability);
						var futureQty = _scModelUtils.getStringValueFromPath("ProcuredQty",availability);
						var futureAvaialbleDate = _scModelUtils.getStringValueFromPath("FutureAvaialbleDate",availability);
						var item = {};
						_scModelUtils.setStringValueAtModelPath("ItemID", itemID, item);
						_scModelUtils.setStringValueAtModelPath("OnStoreAvailableQty", onHandQty, item);
						_scModelUtils.setStringValueAtModelPath("OnStoreAvaialbleDate", onStoreAvaialbleDate, item);
						_scModelUtils.setStringValueAtModelPath("FutureQuantity", futureQty, item);
						_scModelUtils.setStringValueAtModelPath("FutureAvaialbleDate", futureAvaialbleDate, item);
						
						_scModelUtils.addModelObjectToModelList(item, itemList);
					}
				}
			}
		}
		//console.log("newPopupData >> ", node);
		_scBaseUtils.addModelValueToBean("screenInput", node, popupParams);
		//Code Added for fix of Defect SU-31 - END
		_isccsUIUtils.openSimplePopup("extn.custom.availabilityDetails.AvailabilityDetails", "Item Availability Details for ShipNode : "+selectedShipNode, this, popupParams, dialogParams);
	}
});
});

