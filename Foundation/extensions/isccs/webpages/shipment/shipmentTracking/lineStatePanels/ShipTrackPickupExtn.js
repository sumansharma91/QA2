
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/shipmentTracking/lineStatePanels/ShipTrackPickupExtnUI",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!isccs/utils/UIUtils"
	]
,
function(			 
	_dojodeclare,
	_extnShipTrackPickupExtnUI,
	_scScreenUtils,
	_scModelUtils,
	_scBaseUtils,
	_isccsUIUtils
){ 
	return _dojodeclare("extn.shipment.shipmentTracking.lineStatePanels.ShipTrackPickupExtn", [_extnShipTrackPickupExtnUI],{
	// custom code here
	setTrackingInfo : function(event, bEvent, ctrl, args) {
		var trackingData = _scScreenUtils.getModel(this, "TrackingData");
		console.log("TrackingData: ", trackingData);
		//_scModelUtils.setStringValueAtModelPath("TrackingNo", "1ZA3Y4861300049326", trackingData);
		//_scModelUtils.setStringValueAtModelPath("FromNode", "4555", trackingData);
		_scScreenUtils.setModel(this, "TrackingData", trackingData, null);
		var shipmentTrackingScreen = _isccsUIUtils.getParentScreen(this, true);
		var model = _scScreenUtils.getModel(shipmentTrackingScreen, "getCompleteOrderLineDetails_Output");
		console.log("parent", shipmentTrackingScreen);
		console.log("model", model);
		console.log("this", this);
		
		// Call getCompleteOrderLineDetails
		var input = _scModelUtils.createNewModelObjectWithRootKey("OrderLine");
        var olk = _scModelUtils.getStringValueFromPath("OrderLine.OrderLineKey", model);
		_scModelUtils.setStringValueAtModelPath("OrderLine.ChainedFromOrderLineKey", olk, input);
		
		console.log("olk", olk);
		console.log("input", input);
		//_isccsUIUtils.callApi(this, input, "extn_getOrderLineList", null);
	},
	openTrackingUrl: function(event, bEvent, ctrl, args) {
		var model = null;
		var trackingUrl = null;
		model = _scScreenUtils.getModel(
		this, "TrackingData");
		console.log("TrackingData: ", model);
		console.log("linesMode?", this.linesMode);
		trackingUrl = _scBaseUtils.getValueFromPath("ContainerDetail.Container.URL", model);
		var options = null;
		options = {};
		options["destination"] = "window";
		_isccsUIUtils.openURL(
		trackingUrl, options);
	},
	handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (mashupRefId, "extn_getOrderLineList") {
                console.log("handleMashupOutput : extn_getOrderLineList", modelOutput);
				var trackingData = _scScreenUtils.getModel(this, "TrackingData");
				console.log("TrackingData: ", trackingData);
				var orderLine = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput);
				console.log("OrderLine:", orderLine[0]);
				var fromNode = _scModelUtils.getStringValueFromPath("ShipNode", orderLine[0]);
				var fromNodeDesc = _scModelUtils.getStringValueFromPath("ShipnodeDescription", orderLine[0]);
				var trackingInfoList = _scModelUtils.getStringValueFromPath("TrackingInfoList.TrackingInfo", orderLine[0]);
				trackingInfoList.forEach(function (trackingInfo, i) {
					
				});
				var trackingNo = _scModelUtils.getStringValueFromPath("TrackingInfoList.TrackingInfo", orderLine[0]);
				_scModelUtils.setStringValueAtModelPath("ShipNode.FromNode", fromNode, trackingData);
				_scModelUtils.setStringValueAtModelPath("ShipNode.FromNodeDescription", fromNodeDesc, trackingData);
				_scModelUtils.setStringValueAtModelPath("TrackingNo", fromNodeDesc, trackingData);
				_scScreenUtils.setModel(this, "TrackingData", trackingData, null);
            }
        }
});
});

