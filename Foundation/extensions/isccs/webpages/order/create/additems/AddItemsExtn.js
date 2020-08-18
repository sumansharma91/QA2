
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/create/additems/AddItemsExtnUI","scbase/loader!isccs/order/create/additems/AddItemsUI", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ContextUtils", "scbase/loader!isccs/utils/DeliveryUtils", "scbase/loader!isccs/utils/EventUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!isccs/utils/OrderLineUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!isccs/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!dijit"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnAddItemsExtnUI,_isccsAddItemsUI, _isccsBaseTemplateUtils, _isccsContextUtils, _isccsDeliveryUtils, _isccsEventUtils, _isccsModelUtils, _isccsOrderLineUtils, _isccsOrderUtils, _isccsUIUtils, _isccsWidgetUtils, _isccsWizardUtils, _scBaseUtils, _scControllerUtils, _scEditorUtils, _scEventUtils, _scGridxUtils, _scModelUtils, _scPaginationUtils, _scScreenUtils, _scWidgetUtils,_dijit
){ 
	return _dojodeclare("extn.order.create.additems.AddItemsExtn", [_extnAddItemsExtnUI],{
		
		callGetCompleteItemListApi: function(
		        apiInput, mashupIdentifier) {
		            if (!(
		            _scBaseUtils.isVoid(
		            mashupIdentifier))) {
		                var mashupContext = null;
		                mashupContext = _scControllerUtils.getMashupContext(
		                this);
		                _scControllerUtils.setMashupContextIdenitier(
		                mashupContext, mashupIdentifier);
		            }
		            if (!(
		            _scBaseUtils.isVoid(
		            apiInput))) {
		                _scModelUtils.setStringValueAtModelPath("Item.CustomerInformation.BuyerUserId", _scModelUtils.getStringValueFromPath("Order.BuyerUserId", _scScreenUtils.getInitialInputData(
		                _scEditorUtils.getCurrentEditor())), apiInput);
		                _scModelUtils.setStringValueAtModelPath("Item.CallingOrganizationCode", "VSI", apiInput);
		                if (!(
		                _scBaseUtils.isVoid(
		                mashupContext))) {
		                    _isccsUIUtils.callApi(
		                    this, apiInput, "getCompleteItemList", mashupContext);
		                } else {
                    		// ARE-244 : Clearing BarCode to allow searching for products by item id or ACT SKU : BEGIN
							apiInput.Item.BarCode = "";
                    		// ARE-244 : Clearing BarCode to allow searching for products by item id or ACT SKU : END
		                    _isccsUIUtils.callApi(
		                    this, apiInput, "getCompleteItemList", null);
		                }
		            }
		        },
		        
		handleGetCompleteItemList: function(
        mashupRefOutput, mashupContext, relatedItemDetails) {
            var itemlist = null;
            var varlist = null;
            var mashupIdentifier = null;
            mashupIdentifier = _scControllerUtils.getMashupContextIdenitier(
            mashupContext);
            var strMashupRefId=mashupContext.mashupArray[0].mashupRefId;
            itemlist = _scBaseUtils.getAttributeValue("ItemList.Item", false, mashupRefOutput);
            varlist = _scBaseUtils.getAttributeValue("ItemList.VariationList", false, mashupRefOutput);
            var strExtnIsSignReqdItem=itemlist[0].Extn.ExtnIsSignReqdItem
             var apiInputModel = null;
            var orderDetails = null;
            apiInputModel = {};
            orderDetails = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
            strCountry= _scModelUtils.getStringValueFromPath("Order.PersonInfoShipTo.Country", orderDetails)
            if (
            _scBaseUtils.isVoid(
            itemlist)) {
                var sMessage = null;
                if (!(
                _scBaseUtils.isVoid(
                mashupIdentifier))) {
                    if (
                    _scBaseUtils.equals(
                    mashupIdentifier, "itemFromProductBrowsing")) {
                        sMessage = _scScreenUtils.getString(
                        this, "CSR_AddItems_customerEntitlementError");
                        this.itemIdFieldErrorMsg = sMessage;
                    }
                    
                } 
				else
				{
                    sMessage = _scScreenUtils.getString(
                    this, "CSR_AddItems_invalidProduct");
                }
                _scWidgetUtils.markFieldinError(
                this, "txt_ItemID", sMessage, true);
                this.itemIdFieldErrorMsg = sMessage;
            } 
			
			else {
				
				if
				(_scBaseUtils.equals(
                    strMashupRefId, "getCompleteItemList") && (!_scBaseUtils.isVoid(strCountry))&& (_scBaseUtils.equals("Y",strExtnIsSignReqdItem)) && (!_scBaseUtils.equals("US",strCountry))&& (!_scBaseUtils.equals("USA",strCountry)))
                    	{
                    		console.log("Start");
                    		_scWidgetUtils.markFieldinError( this, "txt_ItemID", "SignatureRequired Items can't be shipped to Non-US Address.", true);
							this.itemIdFieldErrorMsg ="SignatureRequired Items can't be shipped to Non-US Address.";
							Console.log("End");
                    	}
                    
				
				else
				{
                var numItems = 0;
                var currentItemId = null;
                if (
                _scWidgetUtils.isWidgetVisible(
                this, "emptyOlMessage")) {
                    _scWidgetUtils.hideWidget(
                    this, "emptyOlMessage", true);
                }
                numItems = _scBaseUtils.getAttributeCount(
                itemlist);
                if (
                _scBaseUtils.equals(
                numItems, 1)) {
                    currentItemId = {};
                    _scScreenUtils.setModel(
                    this, "currentItemId", currentItemId, null);
                    var itemElem = null;
                    itemElem = itemlist[
                    0];
                    var isValid = null;
                    isValid = _scBaseUtils.getAttributeValue("PrimaryInformation.IsValid", false, itemElem);
                    var condIsValidItem = true;
                    if (
                    _scBaseUtils.equals(
                    isValid, "N")) {
                        condIsValidItem = !condIsValidItem;
                    }
                    var isSubOnOrderAllowed = null;
                    var condIsSubOnOrderAllowed = false;
                    isSubOnOrderAllowed = _scBaseUtils.getAttributeValue("PrimaryInformation.IsSubOnOrderAllowed", false, itemElem);
                    if (
                    _scBaseUtils.equals(
                    isSubOnOrderAllowed, "N")) {
                        condIsSubOnOrderAllowed = !condIsSubOnOrderAllowed;
                    }
                    var isItemSuperseded = null;
                    var condIsItemSuperseded = false;
                    isItemSuperseded = _scBaseUtils.getAttributeValue("PrimaryInformation.IsItemSuperseded", false, itemElem);
                    if (
                    _scBaseUtils.equals(
                    isItemSuperseded, "Y")) {
                        condIsItemSuperseded = !condIsItemSuperseded;
                    }
                    if (!(
                    condIsValidItem)) {
                        if (
                        condIsSubOnOrderAllowed) {
                            this.handleItemSubstitution(
                            itemElem, relatedItemDetails, varlist);
                        } else {
                            _scWidgetUtils.showWidget(
                            this, "bottomPanel", true, null);
                            this.addItemToGrid(
                            itemElem, relatedItemDetails, varlist);
                            this.clearItemIdField();
                            _scWidgetUtils.enableWidget(
                            this, "update_order");
                        }
                    } else {
                        _scWidgetUtils.showWidget(
                        this, "bottomPanel", true, null);
                        this.addItemToGrid(
                        itemElem, relatedItemDetails, varlist);
                        this.clearItemIdField();
                        _scWidgetUtils.enableWidget(
                        this, "update_order");
                    }
                } else {
                    this.handleMultipleItems(
                    itemlist);
                }
            }
		}
        },
		handleUpdateOrder: function(
        screen, mashupIndentifier, tmodel, args) {
			var orderLineModel = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine",tmodel);
			console.log(tmodel);
			var isShipLine = false;
			var isPickLine = false;

			var orderLineRemovedModel = {};
			var orderLineExistingModel = {};
			//console.log(varDelMethod);
			//return;
			//var map = [];
			console.log(orderLineModel);
			//return;
			
			if(!(_scBaseUtils.isVoid(orderLineModel))){
				for(i=0;i<orderLineModel.length;i++){
					//var deliMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod",orderLineModel[0]);
					orderLineModel[i].ValidateItem = "N";
					console.log(orderLineModel[i]);
					var delMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod",orderLineModel[i]);
					console.log(delMethod);
					//return;
					//orderLineRemovedModel =[];
					if(_scBaseUtils.equals("0",orderLineModel[i].OrderedQty) || _scBaseUtils.equals("REMOVE",orderLineModel[i].Action))
					{
						if(!_scBaseUtils.isVoid(orderLineModel[i].OrderLineKey))
						{
							//alert('removing line');
							orderLineRemovedModel[orderLineModel[i].OrderLineKey] = "Y";
						}
						//console.log('orderLineRemovedModel',orderLineRemovedModel[orderLineModel[i].OrderLineKey]);
						//return;
						continue;
					}
					
					//if(!_scBaseUtils.equals(deliMethod,delMethod)){
						//_isccsBaseTemplateUtils.showMessage(this, "extn_delivery_method_message", "error", null);
						//return;
					//}
					var orderLineForPrice = orderLineModel[i];
					var elePersonInfoShipTo = _scModelUtils.getStringValueFromPath("PersonInfoShipTo",orderLineForPrice);
					var strIsNew = _scModelUtils.getStringValueFromPath("NewItem",orderLineForPrice);
					console.log("orderLineBefore",orderLineForPrice);
					var itemId = _scModelUtils.getStringValueFromPath("Item.ItemID",orderLineForPrice);
					// Updated code for BOSTOS flow - ARR7 - START
					var shipNode = _scModelUtils.getStringValueFromPath("ShipNode",orderLineForPrice);
					console.log(shipNode);
					//return;
					var key = itemId+"_"+shipNode;
					var isFutureAval = _isccsContextUtils.getFromContext(key);
					var orderLinekey = orderLineModel[i].OrderLineKey;
					var isFutureAvalFromOrderLine = "";
					if(!_scBaseUtils.isVoid(orderLinekey))
					{
						isFutureAvalFromOrderLine = _isccsContextUtils.getFromContext(orderLinekey);
					}
					
					console.log("isFutureAval",isFutureAval);
					// Code Updated for Defect SU-72 - START

					// Code Updated for Defect SU-72 - END	
					if(!_scBaseUtils.isVoid(shipNode) && (_scBaseUtils.equals(isFutureAval, "Y") || _scBaseUtils.equals(isFutureAvalFromOrderLine, "Y")))
					{
						_scModelUtils.setStringValueAtModelPath("LineType","SHIP_TO_STORE", orderLineForPrice);
						_scModelUtils.setStringValueAtModelPath("FulfillmentType","SHIP_TO_STORE", orderLineForPrice);
						//No need to set the procure from node as sourcing will determine the node
						//_scModelUtils.setStringValueAtModelPath("ProcureFromNode","9001", orderLineForPrice);
						//_scModelUtils.setStringValueAtModelPath("IsProcurementAllowed","Y", orderLineForPrice);
					} else if(!_scBaseUtils.isVoid(shipNode)  && (_scBaseUtils.equals(isFutureAval, "N") || _scBaseUtils.equals(isFutureAvalFromOrderLine, "N"))){
						_scModelUtils.setStringValueAtModelPath("ProcureFromNode","", orderLineForPrice);
						_scModelUtils.setStringValueAtModelPath("IsProcurementAllowed","", orderLineForPrice);
						_scModelUtils.setStringValueAtModelPath("LineType","PICK_IN_STORE", orderLineForPrice);
					}
					else if (_scBaseUtils.isVoid(shipNode) && _scBaseUtils.equals("PICK", delMethod))
					{
						//alert('dd');	
						_scModelUtils.setStringValueAtModelPath("LineType","PICK_IN_STORE", orderLineForPrice);
						_scModelUtils.setStringValueAtModelPath("FulfillmentType","", orderLineForPrice);
					}
					
					
					//_isccsContextUtils.addToContext(key, null);
					//_isccsContextUtils.addToContext(orderLinekey, null);
					// Updated code for BOSTOS flow - ARR7 - END
					if(_scBaseUtils.isVoid(elePersonInfoShipTo)){
						console.log("personInfoShipTo not present");
					}
					var varDeliveryMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod",orderLineForPrice);
					if(_scBaseUtils.equals("PICK", varDeliveryMethod)){
						//_scModelUtils.setStringValueAtModelPath("Order.EnteredBy", "8001", tmodel);				
						var varShipNode = _scModelUtils.getStringValueFromPath("ShipNode",orderLineForPrice);
						if(!_scBaseUtils.isVoid(varShipNode)){
							_scModelUtils.setStringValueAtModelPath("ShipNode", varShipNode, orderLineForPrice);
						}
						
						isPickLine = true;
						
						if(!_scBaseUtils.isVoid(orderLinekey))
						
						{
							orderLineExistingModel[orderLinekey] = "PICK";
						}	
					}
					if(_scBaseUtils.equals("SHP", varDeliveryMethod)){
						
						isShipLine = true;
							
						if(!_scBaseUtils.isVoid(orderLinekey))
						{
							orderLineExistingModel[orderLinekey] = "SHP";
						}
					}
					//START - Fix for SU-22
					var displayUnitPrice = _scModelUtils.getStringValueFromPath("LinePriceInfo.DisplayUnitPrice",orderLineForPrice);
					console.log("displayUnitPrice",displayUnitPrice);
					if(_scBaseUtils.isVoid(displayUnitPrice)){
						_scModelUtils.setStringValueAtModelPath("LinePriceInfo.DisplayUnitPrice", 
							_scModelUtils.getStringValueFromPath("LineOverallTotals.DisplayUnitPrice",orderLineForPrice), orderLineForPrice);
					}
					//END - Fix for SU-22
					console.log("orderLineAfter",orderLineForPrice);	
					//alert('hiii');
					//return;			
				}
			}
			//alert(isShipLine);
			//alert(isPickLine);
			var OrderDetails = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			var OrderLine = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine", OrderDetails);
			if(!_scBaseUtils.isVoid(OrderLine))
			{
				for(i=0;i<OrderLine.length;i++)
				{
					if(!_scBaseUtils.isVoid(orderLineRemovedModel[OrderLine[i].OrderLineKey])
					|| !_scBaseUtils.isVoid(orderLineExistingModel[OrderLine[i].OrderLineKey]))
					{
						//alert('already removed line');
						continue;
					}
					var DeliveryMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod",OrderLine[i]);
					if(DeliveryMethod =="PICK")
					{
						isPickLine = true;
					}
					if(DeliveryMethod =="SHP")
					{
						isShipLine = true;
					}
				}
			}
			//alert('hiii');
			//alert(isShipLine);
			//alert(isPickLine);
			//console.log(map['PICK']);
			if(isShipLine && isPickLine){
					_isccsBaseTemplateUtils.showMessage(this, "extn_delivery_method_message", "error", null);
					return;
				}
			
			//return;		
			//console.log("tmodell243: ",tmodel);
			
            var sOrderHeaderKey = null;
            var length = 0;
            sOrderHeaderKey = _scBaseUtils.getValueFromPath("Order.OrderHeaderKey", tmodel);
            var mashupContextBean = null;
            mashupContextBean = _scControllerUtils.getMashupContext(
            this);
            _scControllerUtils.setMashupContextIdenitier(
            mashupContextBean, mashupIndentifier);
            var orderLine = null;
            var isPriceOverridenForOrder = "N";
            orderLine = _scBaseUtils.getValueFromPath("Order.OrderLines.OrderLine", tmodel);
            if (!(
            _scBaseUtils.isVoid("orderLine"))) {
                length = _scBaseUtils.getAttributeCount(
                orderLine);
                var orderHasValidationError = false;
                for (
                var index = 0;
                index < length;
                index = index + 1) {
                    var currOrderLineBean = null;
                    currOrderLineBean = orderLine[
                    index];
                    var currOrderLine = currOrderLineBean;
                    if (
                    _scBaseUtils.equals(
                    isPriceOverridenForOrder, "N") && _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("LinePriceInfo.IsPriceOverridenInPreview", currOrderLine), "Y")) {
                        isPriceOverridenForOrder = "Y";
                    }
                    if (
                    _scBaseUtils.isVoid(
                    _scModelUtils.getStringValueFromPath("OrderLineTranQuantity.OrderedQty", currOrderLine))) {
                        _isccsBaseTemplateUtils.showMessage(
                        this, "screenHasErrors", "error", null);
                        return -1;
                    }
                    var isModelItem = "";
                    isModelItem = _scModelUtils.getStringValueFromPath("ItemDetails.PrimaryInformation.IsModelItem", currOrderLine);
                    if (
                    _scBaseUtils.equals(
                    isModelItem, "Y")) {
                        var modelItemErrorString = null;
                        _isccsBaseTemplateUtils.showMessage(
                        this, "ModelItemErrorMessage", "error", null);
                        return -1;
                    }
                }
            }
            var orderName = null;
            orderName = _scBaseUtils.getValueFromPath("OrderName", this);
            _scModelUtils.setStringValueAtModelPath("Order.OrderName", orderName, tmodel);
            _isccsDeliveryUtils.handleRelatedItemsAndServices(
            this, tmodel);
            if (
            _scBaseUtils.equals("updateButtonClick", mashupIndentifier)) {
                _scModelUtils.setStringValueAtModelPath("Order.UIOperation", mashupIndentifier, tmodel);
            }
            var mashupRefId = null;
            if (
            _scBaseUtils.isVoid(
            sOrderHeaderKey)) {
                mashupRefId = "createOrder";
                _isccsUIUtils.callApi(
                this, tmodel, mashupRefId, mashupContextBean);
            } else if (
            _scBaseUtils.equals("updateButtonClick", mashupIndentifier)) {
                this.callMultiApi(
                tmodel, mashupContextBean, isPriceOverridenForOrder, args);
            } else if (
            _scBaseUtils.equals(
            isPriceOverridenForOrder, "Y")) {
                _isccsUIUtils.callApi(
                this, tmodel, "modifyFulfillmentOptionsWithOvp", mashupContextBean);
            } else if (
            _scBaseUtils.equals("onNext", mashupIndentifier)) {
                var mashupRefs = null;
                var screenId = null;
                var args = null;
                mashupRefs = [];
                newModelData = {};
                _scModelUtils.addStringValueToModelObject("mashupRefId", "modifyFulfillmentOptions", newModelData);
                _scModelUtils.addStringValueToModelObject("mashupInput", tmodel, newModelData);
                mashupRefs.push(
                newModelData);
                screenId = _scBaseUtils.getValueFromPath("declaredClass", this);
                args = _isccsUIUtils.formatArgForUpdateAndNextCall(
                screenId, mashupRefs);
                _scEventUtils.fireEventToParent(
                this, "combinedAPICallOnNext", args);
            } else {
                _isccsUIUtils.callApi(
                this, tmodel, "modifyFulfillmentOptions", mashupContextBean);
            }
			console.log("Complete Execution");
        },
		onPromoOverrideDateChange: function(event, bEvent, ctrl, args){
			var orderModel = _scBaseUtils.getTargetModel(
            this, "getCompleteOrderLineList_input", null);
			console.log("orderModel",orderModel);
			if(!_scBaseUtils.isVoid(event)){
				var date = _scBaseUtils.convertToServerFormat(event,"DATETIME");
				console.log("date",date);
				orderModel.Order.Extn = {};
				orderModel.Order.Extn.ExtnPricingDate = date;	
				var isDirty = _scScreenUtils.isDirty(this,"getCompleteOrderLineList_input",true);
				if(isDirty){				
				 _scWidgetUtils.enableWidget(
					this, "update_order");
				}
			}
		},
		        callUpdateOrder: function(
        args) {
            var mashupIndentifier = null;
            if (
            _scBaseUtils.negateBoolean(
            _scBaseUtils.isVoid(
            _scBaseUtils.getAttributeValue("Action", false, args))) && _scBaseUtils.equals("NEXT", _scBaseUtils.getAttributeValue("Action", false, args))) {
                mashupIndentifier = "onNext";
            } else if (
            _scBaseUtils.negateBoolean(
            _scBaseUtils.isVoid(
            _scBaseUtils.getAttributeValue("Action", false, args))) && _scBaseUtils.equals("SAVEONPREVIOUS", _scBaseUtils.getAttributeValue("Action", false, args))) {
                mashupIndentifier = "SAVEONPREVIOUS";
            } else {
                mashupIndentifier = "updateButtonClick";
            }
            var tmodel = null;
            var tmodelForDeletedLines = null;
            var options = null;
            options = {};
            options["added"] = "true";
            options["modified"] = "true";
            options["deleted"] = "true";
            _scBaseUtils.setAttributeValue("allowEmpty", true, options);
            tmodel = _scBaseUtils.getTargetModel(
            this, "getCompleteOrderLineList_input", options);
            this.removeLinesFromDraftOrder(
            tmodel);
			

            var orderLines = null;
            orderLines = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", tmodel);
			var extnPricingDate = _scModelUtils.getStringValueFromPath("Order.Extn.ExtnPricingDate",tmodel);

			
            if (!(_scBaseUtils.isVoid(orderLines))) {
                var success = 0;
                success = this.handleUpdateOrder(
                this, mashupIndentifier, tmodel, args);
                if (
                _scBaseUtils.equals(
                success, -1)) {
                    return;
                }
            } else if(!(_scBaseUtils.isVoid(extnPricingDate)) && (_scBaseUtils.isVoid(orderLines))){
				_isccsModelUtils.removeAttributeFromModel("Order.OrderLines", tmodel);
				console.log("tmodel#####",tmodel);
				  var success = 0;
                success = this.handleUpdateOrder(
                this, mashupIndentifier, tmodel, args);
                if (
                _scBaseUtils.equals(
                success, -1)) {
                    return;
                }
				/* var mashupContextBean = null;
				 _isccsUIUtils.callApi(
                this, tmodel, "modifyFulfillmentOptions", mashupContextBean); */
				
			}

			else {
                this.toggleOrderTotalChangeIcon(
                false);
                if (
                _scBaseUtils.equals(
                mashupIndentifier, "onNext")) {
                    this.validateScreenAndProceed();
                }
                else if(_scBaseUtils.equals(mashupIndentifier, "SAVEONPREVIOUS")){
                	var args = _scBaseUtils.getNewArrayInstance();
                    _scEventUtils.fireEventToParent(
                    this, "onSaveSuccess", args);
                }else {
                    var childScreen = null;
                    childScreen = _scScreenUtils.getChildScreen(
                    this, "orderLineList");
                    _scScreenUtils.clearScreen(
                    childScreen, null);
                    _scGridxUtils.refreshGridUsingUId(
                    childScreen, "OLST_listGrid");
                    if (
                    _scGridxUtils.isGridEmpty(
                    childScreen, "OLST_listGrid")) {
                        _scEventUtils.fireEventToChild(
                        this, "orderLineList", "hidePreview", null);
                        _scWidgetUtils.hideWidget(
                        childScreen, "togglePreview", true);
                        _scWidgetUtils.hideWidget(
                        childScreen, "OLST_listGrid", true);
                        _scWidgetUtils.showWidget(
                        this, "emptyOlMessage", true, null);
                        _scWidgetUtils.hideWidget(
                        childScreen, "relatedItemsContainer", true);
                    }
                    _scWidgetUtils.disableWidget(
                    this, "update_order", false);
                }
            }
        },
        addItem: function(
        event, bEvent, ctrl, args) {
			var targetModel = null;
            var mashupRefObj = null;
            var mashupContext = null;
            var ordermodel = null;
            var enterpriseCode = null;
            var itemID = null;
            this.itemIdFieldErrorMsg = null;
            _scWidgetUtils.clearFieldinError(
            this, "txt_ItemID");
            if (!(
            _scWidgetUtils.isWidgetVisible(
            this, "bottomPanel"))) {
                _scWidgetUtils.showWidget(
                this, "bottomPanel", true, null);
            }
            targetModel = _scBaseUtils.getTargetModel(
            this, "getCompleteItemList_input", null);
            itemID = _scModelUtils.getStringValueFromPath("Item.BarCode.BarCodeData", targetModel);
            if (
            _scBaseUtils.isVoid(
            itemID)) {
                var sMessage = null;
                sMessage = _scScreenUtils.getString(
                this, "CSR_AddItems_productIdRequired");
                _scWidgetUtils.markFieldinError(
                this, "txt_ItemID", sMessage, true);
                _scWidgetUtils.setFocusOnWidgetUsingUid(this,"txt_ItemID");
            } else {
                var currentItemId = null;
                var isItemIdFieldInError = true;
                currentItemId = {};
                _scModelUtils.setStringValueAtModelPath("Item.ItemID", itemID, currentItemId);
                _scScreenUtils.setModel(
                this, "currentItemId", currentItemId, null);
                isItemIdFieldInError = _scScreenUtils.isValid(
                this, "getCompleteItemList_input");
                if (
                _scBaseUtils.equals(
                isItemIdFieldInError, true)) {
                    _scModelUtils.setStringValueAtModelPath("Item.ItemIDQryType", "FLIKE", targetModel);
                    if (
                    _isccsOrderUtils.checkNoOfNewLines(
                    this)) {
                    	// ARE-244 : Adding complex query to search for items using item id or alpha/ACT SKU id : BEGIN
						var orArr = _scBaseUtils.getNewArrayInstance();
						var or1 = {};
						var or2 = {};
						or1.Name = "ItemID";
						or1.Value = itemID;
						or2.Name = "Extn_ExtnActSkuID";
						or2.Value = itemID.toUpperCase();
						_scBaseUtils.appendToArray(orArr, or1);
						_scBaseUtils.appendToArray(orArr, or2);
						_scModelUtils.addListToModelPath("Item.ComplexQuery.And.Or.Exp", orArr, targetModel);
                    	// ARE-244 : Adding complex query to search for items using item id or alpha/ACT SKU id : END
						
						this.callGetCompleteItemListApi(
                        targetModel, null);
                    }
                }
            }
        }
		
	});
});

