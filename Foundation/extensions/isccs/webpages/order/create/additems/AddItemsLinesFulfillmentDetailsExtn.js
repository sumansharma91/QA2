
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/create/additems/AddItemsLinesFulfillmentDetailsExtnUI",
	"scbase/loader!isccs/utils/ContextUtils", 
	"scbase/loader!isccs/utils/OrderUtils", 
	"scbase/loader!isccs/utils/SharedComponentUtils", 
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils", 
	"scbase/loader!sc/plat/dojo/utils/EventUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/ControllerUtils"
	]
,
function(			 
	_dojodeclare,
	_extnAddItemsLinesFulfillmentDetailsExtnUI,
	_isccsContextUtils, 
	_isccsOrderUtils, 
	_isccsSharedComponentUtils, 
	_isccsUIUtils, 
	_scBaseUtils, 
	_scEventUtils, 
	_scModelUtils, 
	_scScreenUtils, 
	_scWidgetUtils, 
	_scControllerUtils
){ 
	return _dojodeclare("extn.order.create.additems.AddItemsLinesFulfillmentDetailsExtn", [_extnAddItemsLinesFulfillmentDetailsExtnUI],{
	// custom code here
	 callbackSelectStorePopup: function(actionPerformed, model, popupParams) {
            var shipnode = null;
            var shipNodeFullDesc = null;
            var tmodel = null;
            var shipnodeInOl = null;
            var orderLineModel = null;
            var sApply = null;
            if (_scBaseUtils.equals(actionPerformed, "APPLY")) {
            	_scWidgetUtils.showWidget(this, "lblSelectedStore", true);
            	_scWidgetUtils.showWidget(this, "lblSelectedStoreAvailability", true);
                orderLineModel = _scScreenUtils.getModel(this, "OrderLine");
                _scModelUtils.setStringValueAtModelPath("DeliveryMethod", "PICK", orderLineModel);
                shipnode = _scBaseUtils.getValueFromPath("Node.Availability.ShipNode", model);
                shipNodeFullDesc = this.getFormattedShipNodeFullDesc(model);
                shipnodeInOl = _scModelUtils.getStringValueFromPath("ShipNode", orderLineModel);
                if (!(_scBaseUtils.equals(shipnode, shipnodeInOl))) {
                    var newModelData = null;
                    newModelData = {};
                    _scModelUtils.addStringValueToModelObject("ShipnodeFullDescription", shipNodeFullDesc, newModelData);
                    _scModelUtils.setStringValueAtModelPath("ShipNode", shipnode, newModelData);
                    _scModelUtils.addStringValueToModelObject("DeliveryMethod", "PICK", newModelData);
                    this.fireEventToUpdateParentScreen(this, newModelData);
                }
                _scModelUtils.setStringValueAtModelPath("ShipNode", shipnode, orderLineModel);
                _scModelUtils.setStringValueAtModelPath("ShipnodeFullDescription", shipNodeFullDesc, orderLineModel);
                if(_scBaseUtils.equals(this.showLineAvailability,true)){
                    var availabiltyDate = null;
                    availabiltyDate = _scBaseUtils.getValueFromPath("Node.Availability.AvailableDate", model);
                    var isAvailable = null;
                    isAvailable = _scBaseUtils.getValueFromPath("Node.Availability.IsAvailable", model);
                    var availablityInfo = null;
                    var isAvailableToday = null;
                    if (_scBaseUtils.equals(isAvailable, "Y")) {
                        isAvailableToday = _isccsOrderUtils.compareWithCurrentDate(availabiltyDate);
						var itemId = _scModelUtils.getStringValueFromPath("Item.ItemID", orderLineModel);
						var key = itemId+"_"+shipnode;
                        if (isAvailableToday) {
							_isccsContextUtils.addToContext(key, "N");
                            availablityInfo = _scScreenUtils.getString(this, "Today");
                            _scModelUtils.setStringValueAtModelPath("PickupAvailabilityInfo", availablityInfo, orderLineModel);
                        } else {							
							_isccsContextUtils.addToContext(key, "Y");
                            var inputArray = null;
                            var formattedDate = null;
                            formattedDate = _scBaseUtils.formatDateToUserFormat(
                            availabiltyDate);
                            inputArray = [];
                            inputArray.push(
                            formattedDate);
                            availablityInfo = _scScreenUtils.getFormattedString(this, "AvailableOn", inputArray);
                        }
                    } else {
                        availablityInfo = _scScreenUtils.getString(this, "OutOfStock");
                    }
                    _scModelUtils.setStringValueAtModelPath("PickupAvailabilityInfo", availablityInfo, orderLineModel);
                }else{
                    _scModelUtils.setStringValueAtModelPath("PickupAvailabilityInfo", "", orderLineModel);
                }
                this.updateChangesInOrderLine(orderLineModel);
            }
        },
		// openStoreSelectionPopup OOB method override for change the title in Store Selection Pop Up at ADD Item Page
		openStoreSelectionPopup: function(event, bEvent, ctrl, args) {
            var itemModel = null;
            itemModel = _scScreenUtils.getModel(
            this, "OrderLine");
            var getCompleteOrderDetails_output = null;
            getCompleteOrderDetails_output = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
			if(_scBaseUtils.isVoid(getCompleteOrderDetails_output))
			{
			
			var parentScreen = null;
			parentScreen = _isccsUIUtils.getParentScreen(this, false);
			
			getCompleteOrderDetails_output = _scScreenUtils.getModel(
            parentScreen, "getCompleteOrderDetails_output");
			
			}
            var personInfoShipTo = null;
            personInfoShipTo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", getCompleteOrderDetails_output);
            if (
            _scBaseUtils.isVoid(
            personInfoShipTo)) {
                var sCountry = null;
                personInfoShipTo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoBillTo", getCompleteOrderDetails_output);
            }
            var enterpriseCode = null;
            enterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", getCompleteOrderDetails_output);
            var itemId = null;
            itemId = _scModelUtils.getStringValueFromPath("ItemDetails.ItemID", itemModel);
            var uom = null;
            uom = _scModelUtils.getStringValueFromPath("ItemDetails.UnitOfMeasure", itemModel);
            var productClass = null;
            productClass = _scModelUtils.getStringValueFromPath("ItemDetails.PrimaryInformation.DefaultProductClass", itemModel);
            var extendedDisplayDesc = null;
            extendedDisplayDesc = _scModelUtils.getStringValueFromPath("ItemDetails.PrimaryInformation.ExtendedDisplayDescription", itemModel);
            var innerItemModel = null;
            innerItemModel = {};
            _scModelUtils.addStringValueToModelObject("ItemID", itemId, innerItemModel);
            _scModelUtils.addStringValueToModelObject("UnitOfMeasure", uom, innerItemModel);
            _scModelUtils.addStringValueToModelObject("ProductClass", productClass, innerItemModel);
            _scModelUtils.addStringValueToModelObject("ItemGroupCode", _scModelUtils.getStringValueFromPath("ItemDetails.ItemGroupCode", itemModel), innerItemModel);
            var innerOrderModel = null;
            innerOrderModel = {};
            var outerModel = null;
            outerModel = {};
            _scModelUtils.addModelToModelPath("Item", innerItemModel, outerModel);
            _scModelUtils.addModelToModelPath("Order", innerOrderModel, outerModel);
            var inputModel = null;
            inputModel = {};
            _scModelUtils.addModelToModelPath("OrderLine", outerModel, inputModel);
            _scModelUtils.setStringValueAtModelPath("OrderLine.RequiredQty", _scModelUtils.getStringValueFromPath("OrderLineTranQuantity.OrderedQty", itemModel), inputModel);
            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", getCompleteOrderDetails_output)))) {
                _scModelUtils.setStringValueAtModelPath("OrderLine.Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", getCompleteOrderDetails_output), inputModel);
            }
			//Code Modified for fixing Defect SU-31 - START
			// Title changed from OOB Code as part of Defect SU-31.
            var sPopupTitle = null;
            sPopupTitle = _scScreenUtils.getString(this, "ViewStore");
            _isccsSharedComponentUtils.openStoreSelectionForOrderLine(
            this, inputModel, personInfoShipTo, enterpriseCode, sPopupTitle, "callbackSelectStorePopup");
			//Code Modified for fixing Defect SU-31 - END
        }
});
});

