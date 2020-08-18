
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/fulfillmentSummary/FSUnavailablePickupListScreenExtnUI",
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
	_extnFSUnavailablePickupListScreenExtnUI,
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
	return _dojodeclare("extn.order.fulfillmentSummary.FSUnavailablePickupListScreenExtn", [_extnFSUnavailablePickupListScreenExtnUI],{
	// custom code here
	initializeScreen: function(
        event, bEvent, ctrl, args) {
			var Data1 = null;
            Data1 = _scModelUtils.getModelObjectFromPath("inputData", args);
            _scPaginationUtils.hidePaginationBar(
            this, "OLST_listGrid", false);
            _scScreenUtils.setModel(
            this, "getCompleteOrderLineList_output", Data1, null);
            _scScreenUtils.setModel(
            this, "getCompleteOrderLineList_output", Data1, null);
            var Data = null;
            Data = _scScreenUtils.getModel(
            this, "getCompleteOrderLineList_output");
            var isLargeOrder = null;
            isLargeOrder = _scModelUtils.getStringValueFromPath("Order.UnavailableLines.PickupLines.IsLargeOrder", Data);
            var options = null;
            options = {};
            options["screen"] = this;
            if (
            _scBaseUtils.negateBoolean(
            _scBaseUtils.isVoid(
            isLargeOrder)) && _scBaseUtils.equals(
            isLargeOrder, "N")) {
                _scPaginationUtils.startPaginationWithOutputData(
                this, "OLST_listGrid", Data, _scModelUtils.getModelObjectFromPath("Order", Data), options);
            }
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
                        _scEventUtils.fireEventToParent(
                        this, "SaveOrder", temp);
                    }
                   // _scGridxUtils.deselectAllRowsInGridUsingUId(
                   // this, "OLST_listGrid");
                },
    handlePaginationActionConfirmation: function(
            res, args) {
                var paginationContext = null;
                var pageToShow = null;
                var pageSize = null;
                if (
                _scBaseUtils.equals(
                res, "Ok")) {
                    //_scGridxUtils.deselectAllRowsInGridUsingUId(
                    //this, "OLST_listGrid");
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
                    //this, "OLST_listGrid");
                    var mashupInput = null;
                    var mashupContext = null;
                    mashupInput = _scPaginationUtils.getInitialInputForPaginatedGrid(
                    this, "OLST_listGrid");
                    mashupContext = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
                    this, "OLST_listGrid", pageSize, pageToShow, null, null, null);
                    _scWidgetUtils.hideWidget(
                    this, "gridXParentMessagePanel", false);
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
    changeToShipping: function(
            event, bEvent, ctrl, args) {
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
                    this, "getCompleteOrderDetails_output")), "Change_Address", "callBackChangeToShipping");
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
		console.log("grid>>>>",grid);
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
                this, "changeToShipping");
                _scWidgetUtils.enableWidget(
                this, "changeToDelivery");
                _scWidgetUtils.enableWidget(
                this, "changeStoreForUnavailablePickupLines");
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
                this, "SelectOrUnselectRowInUnavailablePickupPanelWithoutShipnode", argument);
                _scEventUtils.fireEventToParent(
                this, "SelectOrUnselectRowInPickupPanel", argument); */
				_scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRowData.rowIndex);
				if (i == 0) {
					var errorMessage = _scScreenUtils.getString(this, "extn_Header_Row_Deselected_Msg");
					_scScreenUtils.showErrorMessageBox(this, errorMessage, "error",null,null);
				}
            }
        }
});
});

