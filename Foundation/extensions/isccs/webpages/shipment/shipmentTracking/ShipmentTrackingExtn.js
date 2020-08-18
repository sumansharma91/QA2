
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/shipmentTracking/ShipmentTrackingExtnUI",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/GridxUtils",
	"scbase/loader!isccs/utils/OrderUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!isccs/utils/UIUtils"
	]
,
function(			 
	_dojodeclare,
	_extnShipmentTrackingExtnUI,
	_scBaseUtils,
	_scScreenUtils,
	_scWidgetUtils,
	_scModelUtils,
	_scGridxUtils,
	_isccsOrderUtils,
	_isccsBaseTemplateUtils,
	_scEventUtils,
	_isccsUIUtils
){ 
	return _dojodeclare("extn.shipment.shipmentTracking.ShipmentTrackingExtn", [_extnShipmentTrackingExtnUI],{
	// custom code here
	handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
			console.log("mashupRefId",mashupRefId);
			console.log("modelOutput",modelOutput);
			console.log("mashupInput",mashupInput);
			console.log("mashupContext",mashupContext);
			console.log("applySetModel",applySetModel);
			console.log("NEW");
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineList")) {
				console.log("ONE");
                var isOnHold = null;
                isOnHold = _scBaseUtils.getValueFromPath("Page.Output.OrderLineList.Order.HoldFlag", modelOutput);
                if (
                _scBaseUtils.equals("Y", isOnHold)) {
                    _scWidgetUtils.showWidget(
                    this, "pnlOrderHold", false, null);
                }
                if (
                _scBaseUtils.equals(
                true, this.hideViewAll)) {
                    _scWidgetUtils.hideWidget(
                    this, "lnkViewAllLines", false);
                } else {
                    _scWidgetUtils.showWidget(
                    this, "lnkViewAllLines", false, null);
                }
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineList")) {
				console.log("TWO");
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
            }
        },
        onPagingload: function(
        event, bEvent, ctrl, args) {
            var modelOutput = null;
            modelOutput = _scBaseUtils.getValueFromPath("result.Page.Output", args);
			console.log("onPagingload ** modelOutput: ", modelOutput);
            _scScreenUtils.setModel(
            this, "getCompleteOrderLineList_output", modelOutput, null);
            if (
            _isccsUIUtils.isFunction(
            this, "LST_handleSingleRecord")) {
                this.LST_handleSingleRecord();
            }
            if (!(
            _scBaseUtils.isEmptyArray(
            _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput)))) {
                _isccsSearchUtils.updateParentScreen(
                this, "collapseSearchResults", modelOutput);
            }
            if (
            _isccsUIUtils.isFunction(
            this, "handleScreenSpecificAction")) {
                this.handleScreenSpecificAction(
                null, modelOutput, null, null);
            }
        },
        afterPagingload: function(
        event, bEvent, ctrl, args) {
            var modelOutput = null;
            modelOutput = _scBaseUtils.getValueFromPath("result.Page.Output", args);
			console.log("afterPagingload modelOutput", modelOutput);
            if (!(
            _scBaseUtils.isEmptyArray(
            _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput)))) {
                _scGridxUtils.selectRowUsingUId(
                this, "OLST_listGrid", 0);
            }
        },
        OLST_afterPagingload: function(
        event, bEvent, ctrl, args) {
			console.log("OLST_afterPagingload");
            var modelOutput = null;
            var hasEditableGrid = "false";
            if (!(
            _scBaseUtils.equals(
            hasEditableGrid, "true"))) {
                modelOutput = _scBaseUtils.getValueFromPath("result", args);
				console.log("OLST_afterPagingload modelOutput", modelOutput);
                this.handleMashupOutput("getCompleteOrderLineList", modelOutput, null, null, false);
            }
        },
		onSingleRowSelect: function(
        event, bEvent, ctrl, args) {
			console.log("** onSingleRowSelect **");
			console.log("event",event);
			console.log("bEvent",bEvent);
			console.log("ctrl",ctrl);
			console.log("args",args);
            var orderline = null;
            var rowIndex = 0;
            rowIndex = _scBaseUtils.getAttributeValue("rowIndex", false, args);
            _isccsOrderUtils.setCurrentRowIndex(
            this, rowIndex);
            var selectedRecordList = null;
            selectedRecordList = _scBaseUtils.getAttributeValue("selectedRow", false, args);
			console.log("selectedRecordList", selectedRecordList);
            if (
            _isccsUIUtils.isArray(
            selectedRecordList)) {
                orderline = selectedRecordList[
                0];
            } else {
                orderline = selectedRecordList;
            }
            if (!(
            _scBaseUtils.isVoid(
            orderline))) {
                var model = null;
                model = {};
                _scModelUtils.addModelObjectAsChildToModelObject("OrderLine", orderline, model);
                _isccsBaseTemplateUtils.setSelectedRow(
                orderline, "SingleClick", this);
                this.onSingleRowSelectCustom(
                rowIndex);
                if (!(
                _scScreenUtils.isPopup(
                this))) {
                    var evtDef = null;
                    evtDef = {};
                    var argList = null;
                    argList = {};
                    _scBaseUtils.setAttributeValue("inputData", model, argList);
                    _scBaseUtils.setAttributeValue("argumentList", argList, evtDef);
					console.log("HERE")
                    _scEventUtils.fireEventToChild(
                    this, "LineTracking", "loadTrackingData", evtDef);
                }
            }
        }
});
});

