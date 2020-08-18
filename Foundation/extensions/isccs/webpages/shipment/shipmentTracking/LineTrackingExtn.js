scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/shipmentTracking/LineTrackingExtnUI",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/BundleUtils"
	]
,
function(			 
	_dojodeclare,
	_extnLineTrackingExtnUI,
	_scBaseUtils,
	_scModelUtils,
	_scScreenUtils,
	_isccsUIUtils,
	_scBundleUtils
){ 
	return _dojodeclare("extn.shipment.shipmentTracking.LineTrackingExtn", [_extnLineTrackingExtnUI],{
	// custom code here
	loadTrackingData: function(
	event, bEvent, ctrl, args) {
		console.log("** loadTrackingData **");
		console.log("args",args);
		var olk = "";
		var inputData = null;
		var oLine = null;
		inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
		oLine = _scBaseUtils.getAttributeValue("OrderLine", false, inputData);
		console.log("inputData", inputData);
		olk = _scBaseUtils.getAttributeValue("OrderLineKey", false, oLine);
		var input = null;
		var oLineElem = null;
		var lineType = _scBaseUtils.getAttributeValue("LineType", false, oLine);
		console.log("LineType: ", lineType);
		console.log("olk", olk);
		if (_scBaseUtils.isVoid(lineType)) {
			input = _scModelUtils.createNewModelObjectWithRootKey("OrderLine");
			oLineElem = _scModelUtils.getModelObjectFromPath("OrderLine", input);
			_scModelUtils.addStringValueToModelObject("OrderLineKey", olk, oLineElem);
			_scModelUtils.addStringValueToModelObject("NeedLineType", "Y", oLineElem);
			var modelList = null;
			var mashupRefList = null;
			modelList = [];
			mashupRefList = [];
			modelList.push(
			input);
			console.log("loadTrackingData ** input", input);
			mashupRefList.push("extn_getOrderLineList");
			_scScreenUtils.setModel(
					this, "extn_SalesOrderLine", input, null);
			_isccsUIUtils.callApis(
			this, modelList, mashupRefList, null, null);
		} else if (_scBaseUtils.equals("SHIP_TO_STORE", lineType)) {
			input = _scModelUtils.createNewModelObjectWithRootKey("OrderLine");
			oLineElem = _scModelUtils.getModelObjectFromPath("OrderLine", input);
			_scModelUtils.addStringValueToModelObject("ChainedFromOrderLineKey", olk, oLineElem);
			var modelList = null;
			var mashupRefList = null;
			modelList = [];
			mashupRefList = [];
			modelList.push(
			input);
			console.log("loadTrackingData ** input", input);
			mashupRefList.push("extn_getOrderLineList");
			_scScreenUtils.setModel(
					this, "extn_TransferOrderLine", input, null);
			_isccsUIUtils.callApis(
			this, modelList, mashupRefList, null, null);
		} else {
			input = _scModelUtils.createNewModelObjectWithRootKey("OrderLineDetail");
			oLineElem = _scModelUtils.getModelObjectFromPath("OrderLineDetail", input);
			_scModelUtils.addStringValueToModelObject("OrderLineKey", olk, oLineElem);
			var modelList = null;
			var mashupRefList = null;
			modelList = [];
			mashupRefList = [];
			modelList.push(
			input);
			console.log("loadTrackingData ** input", input);
			mashupRefList.push("getCompleteOrderLineDetails");
			// ARH-150 : Added to stamp the correct Expected date range on Shipment Tracking and Order Line Summary screens : BEGIN
			_scScreenUtils.setModel(
					this, "extn_orderLineList_output", inputData, null);
			// ARH-150 : Added to stamp the correct Expected date range on Shipment Tracking and Order Line Summary screens : END
			_isccsUIUtils.callApis(
			this, modelList, mashupRefList, null, null);
		}
	},
	handleMashupOutput: function(
	mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		console.log("---------------------------------------------");
		console.log("mashupRefId",mashupRefId);
		console.log("modelOutput",modelOutput);
		console.log("mashupInput",mashupInput);
		console.log("mashupContext",mashupContext);
		console.log("applySetModel",applySetModel);
		console.log("---------------------------------------------");
		if (
		_scBaseUtils.equals(
		mashupRefId, "getCompleteOrderLineDetails")) {
			var lineType = _scModelUtils.getStringValueFromPath("OrderLine.LineType", modelOutput);
			if (_scBaseUtils.equals("SHIP_TO_STORE", lineType)) {
				console.log("SHIP_TO_STORE");
				var trackingInfoModel = _scScreenUtils.getModel(this, "extn_TransferOrderLine");
				var lineBreakupArr = _scModelUtils.getModelListFromPath("OrderLine.LineTracking.LineBreakups.LineBreakup", modelOutput);
				console.log("lineBreakupArr", lineBreakupArr);
				if (!_scBaseUtils.isVoid(trackingInfoModel) && !_scBaseUtils.isVoid(lineBreakupArr)) {
					console.log("NOT VOID", trackingInfoModel);
					var trackingInfoArr = _scModelUtils.getModelListFromPath("TrackingInfoList.TrackingInfo", trackingInfoModel.OrderLineList.OrderLine[0]);
					var shipNode = _scModelUtils.getStringValueFromPath("ShipNode", trackingInfoModel.OrderLineList.OrderLine[0]);
					var shipnodeDesc = _scModelUtils.getStringValueFromPath("ShipnodeDescription", trackingInfoModel.OrderLineList.OrderLine[0]);
					console.log("trackingInfoModel", trackingInfoModel);
					console.log("trackingInfoArr", trackingInfoArr);
					var nLength = _scModelUtils.getNumberValueFromPath("length", trackingInfoArr);
					console.log("length", nLength);
					if (!_scBaseUtils.isVoid(trackingInfoArr)) {
						if ((nLength === 1) && (nLength === 1)) {
							// Stamps the STS tracking number when the transfer order is shipped in one shipment
							console.log("SINGLE");
							trackingInfoArr.forEach(function(trackingInfo, i) {
								var trackingNo = _scModelUtils.getStringValueFromPath("TrackingNo", trackingInfo);
								var url = _scModelUtils.getStringValueFromPath("TrackingUrl", trackingInfo);
								var lineBreakup = lineBreakupArr[0];
								lineBreakup.ContainerDetail = {};
								lineBreakup.ContainerDetail.Container = {};
								console.log("lineBreakupArr", lineBreakupArr);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.TrackingNo", trackingNo, lineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.URL", url, lineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.TrackingNo", trackingNo, lineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.URL", url, lineBreakup);
								_scModelUtils.setStringValueAtModelPath("ShipNode.FromNode", shipNode, lineBreakup);
								_scModelUtils.setStringValueAtModelPath("ShipNode.FromNodeDescription", shipnodeDesc, lineBreakup);
							});
						} else if (nLength > 0) {
							// Stamps the STS tracking number when the transfer order is shipped in multiple shipments
							console.log("MULTIPLE");
							var newLineBreakupArr = _scBaseUtils.getNewArrayInstance();
							var newLineBreakup = null;
							var oldLineBreakup = null;
							
							// Create an array of shipped quantities
							var shipLineArr = _scModelUtils.getModelListFromPath("ShipmentLines.ShipmentLine", trackingInfoModel.OrderLineList.OrderLine[0]);
							var shippedQtyArr = {};
							for(var i=0; i < shipLineArr.length; i++) {
								var shipLine = shipLineArr[i];
								var shipmentModel = _scModelUtils.getModelObjectFromPath("Shipment", shipLine);
								console.log("shipmentModel", shipmentModel);
								var trackingNoIndex = _scModelUtils.getStringValueFromPath("TrackingNo", shipmentModel[0]);
								var quantity = _scModelUtils.getStringValueFromPath("Quantity", shipLine);
								shippedQtyArr[trackingNoIndex] = quantity;
							}
							console.log("shippedQtyArr", shippedQtyArr);
							console.log("lineBreakupArr", lineBreakupArr);
							for(var i=0; i < lineBreakupArr.length; i++) {
								oldLineBreakup = lineBreakupArr[i];
								console.log("Status: **** ", _scModelUtils.getNumberValueFromPath("Status", oldLineBreakup));
								if (_scModelUtils.getNumberValueFromPath("Status", oldLineBreakup) >= 2160.200
									&& _scModelUtils.getStringValueFromPath("Status", oldLineBreakup) !== "2160.20") {
									newLineBreakup = _scBaseUtils.cloneModel(oldLineBreakup);
								} else {
									_scBaseUtils.appendToArray(newLineBreakupArr, oldLineBreakup);
								}
							}
							for(var i=0; i < trackingInfoArr.length; i++) {
								var trackingInfo = trackingInfoArr[i];
								var trackingNo = _scModelUtils.getStringValueFromPath("TrackingNo", trackingInfo);
								var url = _scModelUtils.getStringValueFromPath("TrackingUrl", trackingInfo);
								newLineBreakup.ContainerDetail = {};
								newLineBreakup.ContainerDetail.Container = {};
								console.log("lineBreakupArr", lineBreakupArr);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.TrackingNo", trackingNo, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.URL", url, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.TrackingNo", trackingNo, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("ContainerDetail.Container.URL", url, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("ShipNode.FromNode", shipNode, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("ShipNode.FromNodeDescription", shipnodeDesc, newLineBreakup);
								_scModelUtils.setStringValueAtModelPath("Quantity", shippedQtyArr[trackingNo], newLineBreakup);
								console.log("shippedQtyArr[trackingNo]", shippedQtyArr[trackingNo]);
								_scBaseUtils.appendToArray(newLineBreakupArr, newLineBreakup);
								newLineBreakup = _scBaseUtils.cloneModel(oldLineBreakup);
							}
							_scModelUtils.addListToModelPath("OrderLine.LineTracking.LineBreakups.LineBreakup", newLineBreakupArr, modelOutput);
						}
					}
				}
			}
			// ARH-150 : Added to stamp the correct Expected date range on Shipment Tracking and Order Line Summary screens : BEGIN
			if (_scBaseUtils.equals("SHIP_TO_HOME", lineType)) {
				var orderLineInput = _scScreenUtils.getModel(this, "extn_orderLineList_output");
				var strExpectedStartDate = _scModelUtils.getStringValueFromPath("OrderLine.ExpectedStartDate", orderLineInput);
				var strExpectedEndDate = _scModelUtils.getStringValueFromPath("OrderLine.ExpectedEndDate", orderLineInput);
				_scModelUtils.setStringValueAtModelPath("OrderLine.ExpectedStartDate", strExpectedStartDate, modelOutput);
				_scModelUtils.setStringValueAtModelPath("OrderLine.ExpectedEndDate", strExpectedEndDate, modelOutput);
				
				var inputArray = _scBaseUtils.getNewArrayInstance();
				if(!_scBaseUtils.isVoid(strExpectedStartDate)){
					strExpectedStartDate = strExpectedStartDate.split("T")[0];
				}
				if(!_scBaseUtils.isVoid(strExpectedEndDate)){
					strExpectedEndDate = strExpectedEndDate.split("T")[0];
				}
				if (_scBaseUtils.equals(strExpectedStartDate, strExpectedEndDate)) {
					var formattedServerStartDate = _scBaseUtils.formatDateToUserFormat(strExpectedStartDate);
					_scModelUtils.setStringValueAtModelPath("OrderLine.DeliveryDateRange", formattedServerStartDate, modelOutput);
				} else {
					var formattedServerStartDate = _scBaseUtils.formatDateToUserFormat(strExpectedStartDate);
					var formattedServerEndDate = _scBaseUtils.formatDateToUserFormat(strExpectedEndDate);
					_scBaseUtils.appendToArray(inputArray, formattedServerStartDate);
					_scBaseUtils.appendToArray(inputArray, formattedServerEndDate);
					var strDeliveryDateRange = _scBundleUtils.getFormattedString("ExpectedDateRangeValue", inputArray);
					_scModelUtils.setStringValueAtModelPath("OrderLine.DeliveryDateRange", strDeliveryDateRange, modelOutput);
				}
			}
			// ARH-150 : Added to stamp the correct Expected date range on Shipment Tracking and Order Line Summary screens : END
			console.log("AFTER");
			var setModelOptions = null;
			setModelOptions = {};
			_scBaseUtils.setAttributeValue("clearOldVals", true, setModelOptions);
			if (!(
			_scBaseUtils.equals(
			false, applySetModel))) {
				_scScreenUtils.setModel(
				this, "getCompleteOrderLineDetails_Output", modelOutput, null);
			}
			var alterationCount = null;
			alterationCount = _scModelUtils.getNumberValueFromPath("OrderLine.AlterationCount", modelOutput);
			var isOnHold = null;
			isOnHold = _scBaseUtils.getValueFromPath("OrderLine.Order.HoldFlag", modelOutput);
			if (!(
			_scBaseUtils.equals(
			this.skipCSRMessages, true))) {
				if (
				alterationCount > 0) {
					var test1 = null;
				} else if (
				_scBaseUtils.equals("Y", isOnHold)) {
					var test2 = null;
				} else {
					var test3 = null;
				}
			}
			/* } else {
				_scScreenUtils.setModel(
					this, "extn_TransferOrderLine", modelOutput, null);
				
			} */
		} else if (
		_scBaseUtils.equals(
		mashupRefId, "extn_getOrderLineList")) {
			var orderLineIn = _scScreenUtils.getModel(this, "extn_SalesOrderLine");
			console.log("extn_SalesOrderLine", orderLineIn);
			var needLineType = _scModelUtils.getStringValueFromPath("OrderLine.NeedLineType", orderLineIn);
			console.log("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			var olk = "";
			var oLine = null;
			var orderLine = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", modelOutput);
			if (_scBaseUtils.equals(needLineType, "Y")) {
				var evtDef = null;
				evtDef = {};
				var argList = null;
				argList = {};
				_scBaseUtils.setAttributeValue("inputData.OrderLine", orderLine[0], argList);
				_scBaseUtils.setAttributeValue("argumentList", argList, evtDef);
				console.log("HERE");
				_scScreenUtils.setModel(
					this, "extn_SalesOrderLine", "", null);
				this.loadTrackingData(null, null, null, argList);
			} else if (!_scBaseUtils.isVoid(orderLine)) {
				console.log("orderLine NOT VOID");
				olk = _scBaseUtils.getAttributeValue("ChainedFromOrderLineKey", false, orderLine[0]);
				var input = null;
				var oLineElem = null;
				input = _scModelUtils.createNewModelObjectWithRootKey("OrderLineDetail");
				oLineElem = _scModelUtils.getModelObjectFromPath("OrderLineDetail", input);
				_scModelUtils.addStringValueToModelObject("OrderLineKey", olk, oLineElem);
				var modelList = null;
				var mashupRefList = null;
				modelList = [];
				mashupRefList = [];
				modelList.push(
				input);
				console.log("extn_getOrderLineList ** input", input);
				mashupRefList.push("getCompleteOrderLineDetails");
				_scScreenUtils.setModel(
					this, "extn_TransferOrderLine", modelOutput, null);
				_isccsUIUtils.callApis(
				this, modelList, mashupRefList, null, null);
			} else {
				console.log("orderLine ELSE");
				var orderLine = orderLineIn;
				var olk = _scModelUtils.getStringValueFromPath("OrderLine.ChainedFromOrderLineKey", orderLine);
				var input = _scModelUtils.createNewModelObjectWithRootKey("OrderLineDetail");
				var oLineElem = _scModelUtils.getModelObjectFromPath("OrderLineDetail", input);
				_scModelUtils.addStringValueToModelObject("OrderLineKey", olk, oLineElem);
				var modelList = null;
				var mashupRefList = null;
				modelList = [];
				mashupRefList = [];
				modelList.push(
				input);
				console.log("loadTrackingData ** input", input);
				mashupRefList.push("getCompleteOrderLineDetails");
				_isccsUIUtils.callApis(
				this, modelList, mashupRefList, null, null);
			}
		}
	}
});
});