
scDefine(["dojo/text!./templates/GiftCardFulfillmentScreen.html",
		  "scbase/loader!dojo/_base/declare",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/PaymentUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/widgets/Screen",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ControllerUtils",
		  "scbase/loader!dojo/_base/lang",
		  "scbase/loader!isccs/utils/EventUtils"
		 ],
function(
		templateText,
		_dojodeclare,
		_isccsBaseTemplateUtils, 
		_isccsModelUtils, 
		_isccsOrderUtils, 
		_isccsPaymentUtils, 
		_isccsWidgetUtils, 
		_scBaseUtils, 
		_scGridxUtils,
		_scModelUtils, 
		_scScreenUtils, 
		_scWidgetUtils,
		_isccsUIUtils,
		_scScreen,
		_scEventUtils,
		_scControllerUtils,
		dLang,
		_isccsEventUtils
){ 
	var trackData1= "";
	var trackData2= "";
	return _dojodeclare("extn.order.giftCard.GiftCardFulfillmentScreen", [_scScreen],{
		templateString: templateText,
		uId: "GiftCardFulfillment",
		packageName: "extn.order.giftCard",
		className: "GiftCardFulfillmentScreen",
		title: "GiftCardFulfillmentScreen",
		screen_description: "This is the Gift Card Fulfillment Screen for activating and fulfilling gift card orders",
		isDirtyCheckRequired: false,
		showRelatedTask: false,
		namespaces: {
			targetBindingNamespaces: [{
				description: 'Holds the gift card info',
				value: 'giftCardInfo'
			},{
				description: 'Holds the tracking number',
				value: 'trackingInfo'
			}],
			sourceBindingNamespaces: [{
				description: 'Initial input to screen',
				value: 'screenInput'
			},{
				description: 'Holds the order lines with gift cards that have not been activated',
				value: 'giftCardActivationList'
			},{
				description: 'Holds the lines of the given order that are included in a shipment, but not yet shipped',
				value: 'includedInShipmentLineList'
			},{
				description: 'Holds the lines of the given order that have been added to a container',
				value: 'containerizedLineList'
			},{
				description: 'Holds the shipped shipment lines for the given order',
				value: 'shipmentLineList'
			},{
				description: 'Holds the input to getOrderReleaseList',
				value: 'getOrderReleaseList_input'
			},{
				description: 'Holds the input to getShipmentList',
				value: 'getShipmentLineList_input'
			}]
		},
		subscribers: {
			local: [{
				eventId: "afterScreenInit",
				sequence: "25",
				description: "Subscriber for after Screen Init event for GiftCardFulfillment",
				handler: {
					methodName: "initializeScreen"
				}
			},{
                eventId: 'afterScreenInit',
                sequence: '50',
                description: 'Subscriber for after screen is initialized',
                handler: {
                    methodName: "setInitialized"
                }
            }, {
                eventId: 'afterScreenLoad',
                sequence: '25',
                description: 'Subscriber for after the screen loads',
                handler: {
                    methodName: "updateEditorHeader"
                }
            },{
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'Subscriber for after the screen is initialized',
                handler: {
                    methodName: "initScreen"
                }
            },{
                eventId: 'GCF_listGrid_ScRowSelect',
                sequence: '25',
                description: 'Listens for Row Change',
                handler: {
                    methodName: "onSingleRowSelect",
                    description: "Handles the single Click from List"
                }
            },{
                eventId: 'activatedGC_listGrid_ScRowSelect',
                sequence: '25',
                description: 'Listens for Row Change',
                handler: {
                    methodName: "onSingleRowSelect_tracking",
                    description: "Handles the single row select from List"
                }
            },{
                eventId: 'activatedGC_listGrid_ScRowDeselect',
                sequence: '25',
                description: 'Listens for Row Change',
                handler: {
                    methodName: "onSingleRowDeselect_tracking",
                    description: "Handles the single row deselect from List"
                }
            },{
				eventId: 'activateBttn_onClick',
				sequence: '52',
				handler: {
					methodName: "activateGiftCard"
				}
			},{
				eventId: 'applyBttn_onClick',
				sequence: '52',
				handler: {
					methodName: "applyTrackingNo"
				}
			},{
				eventId: 'shipWithoutTrackingBttn_onClick',
				sequence: '52',
				handler: {
					methodName: "shipWithoutTracking"
				}
			},{
				eventId: 'activatedGC_listGrid_ScHeaderSelect',
				sequence: '52',
				handler: {
					methodName: "onHeaderSelect"
				}
			},{
				eventId: 'activatedGC_listGrid_ScHeaderDeselect',
				sequence: '55',
				handler: {
					methodName: "onRowOrHeaderDeselect"
				}
			},
			{
				  eventId: 'txtGiftCardNo_onKeyUp'

			,	  sequence: '55'




			,handler : {
			methodName : "gcOnEnter"

			 
			}
			}]
		},
		
		initializeScreen: function(event, bEvent, ctrlId, args) {			
			var inputData = _scScreenUtils.getModel(this, "screenInput");
            if (_scBaseUtils.isVoid(inputData)) {
                var screenInput = _scScreenUtils.getInitialInputData(this);
                if (!(_scBaseUtils.isVoid(screenInput))) {
					var options = {};
					_scBaseUtils.setAttributeValue("screen", this, options);
					
					var getOrderLineReleaseInput = _scModelUtils.createNewModelObjectWithRootKey("OrderRelease");
					var orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", screenInput);
					getOrderLineReleaseInput.OrderRelease.OrderHeaderKey = orderHeaderKey;
					//OMS-1134 : changes
					//getOrderLineReleaseInput.OrderRelease.OrderLine = {};
					//getOrderLineReleaseInput.OrderRelease.OrderLine.ShipNode = "CALL_CENTER";
					getOrderLineReleaseInput.OrderRelease.ShipNode = "CALL_CENTER";
					var getShipmentLineListInput = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
					getShipmentLineListInput.ShipmentLine.Shipment = {};
					getShipmentLineListInput.ShipmentLine.OrderHeaderKey = orderHeaderKey;
					getShipmentLineListInput.ShipmentLine.Shipment = {};
					getShipmentLineListInput.ShipmentLine.Shipment.Status = "1100";
					getShipmentLineListInput.ShipmentLine.Shipment.ShipmentType = "PHY_GC";
					
					var getShipmentContainerListInput = _scModelUtils.createNewModelObjectWithRootKey("Container");
					getShipmentContainerListInput.Container.Shipment = {};
					getShipmentContainerListInput.Container.Shipment.OrderHeaderKey = orderHeaderKey;
					getShipmentContainerListInput.Container.Shipment.ShipmentType = "PHY_GC",
					
					// Set input models
					_scScreenUtils.setModel(this, "screenInput", screenInput, null);
					_scScreenUtils.setModel(this, "getOrderReleaseList_input", getOrderLineReleaseInput, null);
					_scScreenUtils.setModel(this, "getShipmentLineList_input", getShipmentLineListInput, null);
					
					_isccsUIUtils.callApi(this, getOrderLineReleaseInput, "giftCardFulfillment_getOrderReleaseList_RefId");
					_isccsUIUtils.callApi(this, getShipmentLineListInput, "giftCardFulfillment_getShipmentLineList_RefId");
					_isccsUIUtils.callApi(this, getShipmentContainerListInput, "giftCardFulfillment_getShipmentContainerList_RefId");
					that = this;
					setTimeout(function()
					{
						that.getWidgetByUId("txtGiftCardNo").focus();
					}, 1);
                }
			}
			var activateTextbox = this.getWidgetByUId("txtGiftCardNo");
			//_scWidgetUtils.setFocusOnWidget(activateTextbox);
			activateTextbox.focus();
			//activateTextbox.input.select();
		},
        initScreen: function(event, bEvent, ctrl, args) {
            return true;
        },
        setInitialized: function(event, bEvent, ctrl, args) {
            this.isScreeninitialized = true;
        },
        gcOnEnter: function(event, bEvent, ctrl, args) 
        {
        	
        	 if (
	            _isccsEventUtils.isEnterPressed(
	            event)) {
	        		 var swipedData = _scWidgetUtils.getValue(this, 'txtGiftCardNo').trim();
	        		
	        		 var ss = swipedData.split("%B");
	        		 if(!_scBaseUtils.isVoid(ss[1]))
	    			 {
	        			 _scWidgetUtils.setValue(this, "txtGiftCardNo", ss[1].substring(0,19), false);
	        			 trackData1 ="";
	        			 trackData2 ="";
	        			 trackData1 = swipedData.substring(1,swipedData.indexOf('?'));
	    			 }
	    			 else
	    			 {
	    			 	 trackData2 ="";
	    				 swipedData = _scWidgetUtils.getValue(this, 'txtGiftCardNo').trim();
	    				 if(swipedData.length > 20)
    					 {
	    					 //trackData2 ="";
		        			 trackData2 = swipedData.substring(20,swipedData.indexOf('?'));
		        			 //_scWidgetUtils.setValue(this, "txtGiftCardNo", swipedData.substring(0,19), false); 
    					 }	
	    				 if(swipedData.length >= 19)
    					 {
	    					 //trackData2 ="";
		        			 //trackData2 = swipedData.substring(21,swipedData.length);
		        			 _scWidgetUtils.setValue(this, "txtGiftCardNo", swipedData.substring(0,19), false); 
    					 }	
	    			 }
	            }
            //this.isScreeninitialized = true;
        },
        updateEditorHeader: function(
			event, bEvent, ctrl, args) {
				_isccsBaseTemplateUtils.updateTitle(
				this, "extn_Fulfill_Gift_Cards", null);
				var refGrid = this.getWidgetByUId("GCF_listGrid");
					_scGridxUtils.refreshGrid(refGrid);
				return true;
		},
		handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
			if(hasError)
			{
				//alert('ddd');
				_scWidgetUtils.setValue(this, "txtGiftCardNo", "", false);
				data.response.em = data.response.Errors.Error[0].ErrorDescription;
			}
            _isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
		handleMashupOutput: function(callingDetails, mashupOutput, mashupInput, mashupContext, applySetModel) {
			if (!(_scBaseUtils.isVoid(mashupOutput))) {
				if (_scBaseUtils.equals(callingDetails, "giftCardFulfillment_getOrderReleaseList_RefId")) {
					var orderReleaseList = mashupOutput.OrderReleaseList;
					var orderReleaseList = orderReleaseList.OrderRelease;
					var orderLineList = _scModelUtils.createNewModelObjectWithRootKey("OrderLines");
					var orderLinesArr = _scBaseUtils.getNewArrayInstance(); 
					if(!_scBaseUtils.isVoid(orderReleaseList))
					{
						orderReleaseList.forEach (function(orderRelease, index) {
						// Get the lines for this release
						var orderReleaseKey = orderRelease.OrderReleaseKey;
						var orderNo = null;
						if (_scBaseUtils.isVoid(orderRelease.Order[0])) {
							orderNo = orderRelease.Order.OrderNo;
						} else {
							orderNo = orderRelease.Order[0].OrderNo;
						}
						var orderLines = orderRelease.OrderLines.OrderLine;
						orderLines.forEach (function(orderLine, i2) {
							orderStatus = orderLine.OrderStatuses.OrderStatus;
							// Initialize the ActivatedQty to 0
							orderLine.ActivatedQty = 0;
							orderStatus.forEach( function(oStatus, i3) {
								if (parseInt(oStatus.Status) >= 3350) {
									// Add ActivatedQty to order line before adding the line to the model
									orderLine.ActivatedQty = parseInt(oStatus.StatusQty) + orderLine.ActivatedQty;
								}
								orderLine.QtyToActivate = parseInt(orderLine.OrderedQty) - orderLine.ActivatedQty;
								if (oStatus.Status === "3200.900") {
									orderLine.OrderReleaseKey = orderReleaseKey;
									orderLine.OrderNo = orderNo;
									_scBaseUtils.appendToArray(orderLinesArr, orderLine);
								}
								
							});
						});
					});
					}
					
					_scModelUtils.addListToModelPath("OrderLines.OrderLine", orderLinesArr, orderLineList);
					// Set Model to namespace
					_scScreenUtils.setModel(this, "giftCardActivationList", orderLineList, null);
					
				} else if (_scBaseUtils.equals(callingDetails, "extn_activateGiftCard_RefId")) {
					// Clear selected line
					this.onSingleRowDeselect(null, null, null, null);
					
					// get shipment line and shipment key from output
					var shipmentLine = mashupOutput.Shipment.ShipmentLines.ShipmentLine;
					var shipmentKey = mashupOutput.Shipment.ShipmentKey;
					// Call mashups to update the grids
					//var getOrderLineReleaseInput = _scScreenUtils.getModel(this, "getOrderReleaseList_input");
					// Add new shipment line to includedInShipmentLineList
					var shipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
					var orderLineModel = _scScreenUtils.getModel(this, "giftCardActivationList");
					var shipLineList = _scModelUtils.getModelListFromPath("ShipmentLines.ShipmentLine", shipLineModel);
					var orderLineList = _scModelUtils.getModelListFromPath("OrderLines.OrderLine", orderLineModel);
					orderLineList.forEach( function(orderLine, i8) {
						if (orderLine.OrderLineKey === shipmentLine[0].OrderLineKey) {
							var activatedQty = parseInt(orderLine.ActivatedQty) + 1;
							if (parseInt(orderLine.OrderedQty) === activatedQty) {
								rowIndex = orderLineList.indexOf(orderLine);
								if (rowIndex > -1) {
									orderLineList.splice(rowIndex, 1);
								}
							} else {
								orderLine.ActivatedQty = activatedQty;
								orderLine.QtyToActivate = parseInt(orderLine.OrderedQty) - orderLine.ActivatedQty;
							}
						}
					});
					if (!(_scBaseUtils.isVoid(shipLineList))) {
						_scBaseUtils.appendToArray(shipLineList, shipmentLine[0]);
						shipLineModel.ShipmentKey = shipmentKey;
						_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipLineList, shipLineModel);
						_scScreenUtils.setModel(this, "includedInShipmentLineList", shipLineModel, null);
					} else {
						// Add shipment line to list
						var openShipmentLineArr = null;
						openShipmentLineArr = _scBaseUtils.getNewArrayInstance();
						_scBaseUtils.appendToArray(openShipmentLineArr, shipmentLine[0]);
					
						// Add shipment line to namespace
						var shipLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
						shipLineModel.ShipmentKey = shipmentKey;
						_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", openShipmentLineArr, shipLineModel);
						_scScreenUtils.setModel(this, "includedInShipmentLineList", shipLineModel, null);
					}
					//_isccsUIUtils.callApi(this, getOrderLineReleaseInput, "giftCardFulfillment_getOrderReleaseList_RefId");
					//_isccsUIUtils.callApi(this, getShipmentLineListInput, "giftCardFulfillment_getShipmentList_RefId");
					_scScreenUtils.setModel(this, "giftCardActivationList", orderLineModel, null);
					
					this.refreshGrids();
					_isccsBaseTemplateUtils.showMessage(this, "Gift Card has been activated successfully", "success", null);
					that = this;
					setTimeout(function()
					{
						that.getWidgetByUId("txtGiftCardNo").focus();
					}, 1);
					
				} else if (_scBaseUtils.equals(callingDetails, "giftCardFulfillment_getShipmentLineList_RefId")) {
					var shipmentLineList = mashupOutput.ShipmentLines;
					var openShipmentLineArr = null;
					var shipmentKey = "";
					// Set Model to namespace
					if (!(_scBaseUtils.isVoid(shipmentLineList.ShipmentLine))) {
						shipmentKey = shipmentLineList.ShipmentLine[0].ShipmentKey;
						openShipmentLineArr = _scBaseUtils.getNewArrayInstance();
						shipmentLineList.ShipmentLine.forEach (function(shipmentLine, i5) {
							if (shipmentLine.ContainerDetails.TotalNumberOfRecords === "0") {
								_scBaseUtils.appendToArray(openShipmentLineArr, shipmentLine);
							}
						});
					}
					// Add shipment lines to namespace
					var shipmentModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
					shipmentModel.ShipmentKey = shipmentKey;
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", openShipmentLineArr, shipmentModel);
					_scScreenUtils.setModel(this, "includedInShipmentLineList", shipmentModel, null);
					var shipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
					
				} else if (_scBaseUtils.equals(callingDetails, "extn_containerizeGiftCard_RefId")) {
					// Clear selected lines
					this.onRowOrHeaderDeselect(null,null,null,null);
					var shipLineModel = _scScreenUtils.getModel(this, "containerizedLineList");
					var includedInShipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
					var shipmentLineList = mashupOutput.ShipmentLines;
					var shipLineArr = null;
					var shipLineKeyArr = _scBaseUtils.getNewArrayInstance();
					if (_scBaseUtils.isVoid(shipLineModel)) {
						shipLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
						shipLineArr = _scBaseUtils.getNewArrayInstance();
					} else {
						shipLineArr = _scModelUtils.getModelListFromPath("ShipmentLines.ShipmentLine", shipLineModel);
					}
					var shipKey = "";
					shipmentLineList.ShipmentLine.forEach (function(shipmentLine, i5) {
						// Save the ShipmentLineKey
						_scBaseUtils.appendToArray(shipLineKeyArr, shipmentLine.ShipmentLineKey);
						if (shipmentLineList.Status === "1100") {
							shipKey = shipmentLine.ShipmentKey;
							shipmentLine.Status = "Created";
						}
						shipmentLine.TrackingNo = shipmentLineList.TrackingNo;
						_scBaseUtils.appendToArray(shipLineArr, shipmentLine);
					});
					if (shipmentLineList.Status === "1400") {
						shipLineArr.forEach (function(shipmentLine, i5) {
							shipmentLine.Status = "Shipped";
						});
					}
					includedInShipArr = _scModelUtils.getModelListFromPath("ShipmentLines.ShipmentLine", includedInShipLineModel);
					for ( var b = 0; b < includedInShipArr.length; b++) {
						var shipLine = includedInShipArr[b];
						var index = shipLineKeyArr.indexOf(shipLine.ShipmentLineKey);
						var rowIndex = includedInShipArr.indexOf(shipLine);
						if (index > -1 && rowIndex > -1) {
							includedInShipArr.splice(rowIndex, 1);
							b--;
						}
					}
					// Add shipment lines to includedInShipmentLineList namespace
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", includedInShipArr, includedInShipLineModel);
					includedInShipLineModel.ShipmentKey = shipKey;
					_scScreenUtils.setModel(this, "includedInShipmentLineList", includedInShipLineModel, null);
					
					// Add shipment lines to containerizedLineList namespace
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipLineArr, shipLineModel);
					_scScreenUtils.setModel(this, "containerizedLineList", shipLineModel, null);
					
					this.refreshGrids();
					_isccsBaseTemplateUtils.showMessage(this, "Giftcard(s) have been shipped successfully", "success", null);
					
				}  else if (_scBaseUtils.equals(callingDetails, "giftCardFulfillment_getShipmentContainerList_RefId")) {
					var shipmentContainerList = mashupOutput.Containers;
					var shipmentLineArr = _scBaseUtils.getNewArrayInstance();
					// Set Model to namespace
					if (!(_scBaseUtils.isVoid(shipmentContainerList))) {
						shipmentContainerList.Container.forEach (function(container, i5) {
							if (container.Shipment.Status === "1400") {
								container.Status = "Shipped";
							} else if (container.Shipment.Status === "1100") {
								container.Status = "Created";
							}
							container.ContainerDetails.ContainerDetail.forEach( function(containerDetail, i9) {
								containerDetail.ShipmentLine.TrackingNo = container.TrackingNo
								containerDetail.ShipmentLine.Status = container.Status
								_scBaseUtils.appendToArray(shipmentLineArr, containerDetail.ShipmentLine);
							});
						});
					}
					
					// Add shipment lines to namespace
					var shipLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipmentLineArr, shipLineModel);
					_scScreenUtils.setModel(this, "containerizedLineList", shipLineModel, null);
				}
			}
		},
		activateGiftCard: function (event, bEvent, ctrl, args) {
			var selectedLines = _scScreenUtils.getModel(this, "selectedOrderLine");
			var shipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
			// call service to activate gift card
			if (!(_scBaseUtils.isVoid(selectedLines))) {
				var giftCardModel = _scBaseUtils.getTargetModel(this, "giftCardInfo", null);
				// Set GiftCardNo and ShipmentKey
				if (!(_scBaseUtils.isVoid(giftCardModel))) {
					selectedLines.OrderLine.GiftCardNo = giftCardModel.OrderLine.GiftCardNo;
					//selectedLines.OrderLine.TrackNo = trackData;
					
					if(!_scBaseUtils.isVoid(trackData2))
					{
						selectedLines.OrderLine.TrackData2 = trackData2;
					}
					else
					{
						if(!_scBaseUtils.isVoid(trackData1))
						{
							selectedLines.OrderLine.TrackData1 = trackData1;
						}
					}
					console.log(trackData1);
					//return;
					trackData2="";
					trackData1="";
					//selectedLines.OrderLine.TrackData2 = trackData2;
					selectedLines.OrderLine.GiftCardNo = giftCardModel.OrderLine.GiftCardNo;
					if(!_scBaseUtils.isVoid(shipLineModel.ShipmentKey))
					{
						selectedLines.OrderLine.ShipmentKey = shipLineModel.ShipmentKey;
					}
					
					_isccsUIUtils.callApi(this, selectedLines, "extn_activateGiftCard_RefId", null);
				} else {
					var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_GC_No");
					 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
					//_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
				}
			} else {
				var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_Activation_Lines");
				 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
				//_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
			}
        },
		onSingleRowSelect: function (event, bEvent, ctrl, args) {
			var orderline = null;
			var rowIndex = 0;
			rowIndex = _scBaseUtils.getAttributeValue("rowIndex", false, args);
			_isccsOrderUtils.setCurrentRowIndex(this, rowIndex);
			var selectedRecordList = null;
			selectedRecordList = _scBaseUtils.getAttributeValue("selectedRow", false, args);
			if (_isccsUIUtils.isArray(selectedRecordList)) {
				orderline = _scModelUtils.getModelFromList(selectedRecordList, 0);
			} else {
				orderline = selectedRecordList;
			}
			if (!(_scBaseUtils.isVoid(orderline))) {
				var selectedTargetModel = null;
				selectedTargetModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(this, "GCF_listGrid");
				var selectedTargetList = null;
				selectedTargetList = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", selectedTargetModel);
				if (!(_scBaseUtils.isVoid(selectedTargetList))) {
					orderline = _scBaseUtils.mergeModel(orderline, _scModelUtils.getModelFromList(selectedTargetList, 0), true);
				}
				var model = null;
				model = _scBaseUtils.getNewModelInstance();
				_scModelUtils.addModelObjectAsChildToModelObject("OrderLine", orderline, model);
				_scScreenUtils.setModel(this, "selectedOrderLine", model, null);
			}
        },
		onSingleRowDeselect: function (event, bEvent, ctrl, args) {
			var rowIndex = _scBaseUtils.getAttributeValue("rowIndex", false, args);
			var selectedLines = _scScreenUtils.getModel(this, "selectedOrderLine");
			selectedLines = null;
			_scScreenUtils.setModel(this, "selectedOrderLine", selectedLines, null);
		},
		applyTrackingNo: function (event, bEvent, ctrl, args) {
			var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
			var shipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
			// call service to activate gift card
			if (!(_scBaseUtils.isVoid(selectedLines))) {
				var shipmentLines = selectedLines.ShipmentLines.ShipmentLine;
				if (!(_scBaseUtils.isVoid(shipmentLines))) {
					var trackingModel = _scBaseUtils.getTargetModel(this, "trackingInfo", null);
					
					var shipmentLines = selectedLines.ShipmentLines.ShipmentLine;
					var activatedLines = shipLineModel.ShipmentLines.ShipmentLine;
					
					if (_scBaseUtils.isVoid(trackingModel)) 
					{
					
						var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_Tracking_No");
						 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
						//_scScreenUtils.showErrorMessageBox(this, popupMessage, null, null);
						return;
					}

					if(shipmentLines.length === activatedLines.length)
					{
						var trackingNo = trackingModel.OrderLine.TrackNo;
						selectedLines.ShipmentLines.TrackingNo = trackingNo;
						_isccsUIUtils.callApi(this, selectedLines, "extn_containerizeGiftCard_RefId", null);

					}					
					else
					{
						var msg = _scScreenUtils.getString(this, "extn_partial_selection");
						this.showConfirmMessageBox(
									this, msg, "handleShipNow", null, trackingModel);
					}
				} else {
					var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_Lines_To_Containerize");
					 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
					//_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
				}
			} else {
				var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_Lines_To_Containerize");
				 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
				//_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
			}
        },
        showConfirmMessageBox: function(screen,message,screenMethodName, textObj,args){
    		// summary: 
    		//		Use this method to open a confirm message box with the passed arguments on the screen.
    		// description:
    		//		Use this method to open a confirm message box with the passed arguments on the screen.
    		// screen: Screen
    		//		The screen instance for which the method should be invoked.
    		// message: String
    		//		The bundle key for which localized value is to be obtained.
    		// screenMethodName: Function
    		//		The method to call on the screen after the dialog is opened.		
    		// textObj: Object
    		//		The contains the button text to display as textObj.OK and textObj.CANCEL.
    		// args: Object
    		//		Arguments to invoke the callBackHandler with.
    		
    		var textOk = "Proceed anyway";
    		var textCancel = "Go back";
    		idx.dialogs.confirm(message,dLang.hitch(screen, function(){
    			screen[screenMethodName]("Ok",args);		
    		}),dLang.hitch(screen, function(){
    			screen[screenMethodName]("Cancel",args);		
    		}),textOk,textCancel);
    	},
        //Custom giftCardPopUphandler method trigger once Gift Card Pop Up is displayed on screen. If Ok is Selected on Pop Up,
		// It triggers the OOB functionality(override from save method).		
		handleShipNow: function(res,trackingModel){
			if (_scBaseUtils.equals(res, "Ok")) 
			{
				var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
				if(!_scBaseUtils.isVoid(trackingModel))
				{
					var trackingNo = trackingModel.OrderLine.TrackNo;
					selectedLines.ShipmentLines.TrackingNo = trackingNo;
				}
				_isccsUIUtils.callApi(this, selectedLines, "extn_containerizeGiftCard_RefId", null);
            }//end of if ok selected
		},
		shipWithoutTracking: function (event, bEvent, ctrl, args) {
			var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
			var shipLineModel = _scScreenUtils.getModel(this, "includedInShipmentLineList");
			//console.log(selectedLines.length);
			//console.log(shipLineModel.length);
			// call service to activate gift card
			if (!(_scBaseUtils.isVoid(selectedLines))) {
				var shipmentLines = selectedLines.ShipmentLines.ShipmentLine;
				var activatedLines = shipLineModel.ShipmentLines.ShipmentLine;				
				if(shipmentLines.length === activatedLines.length)
				{
					_isccsUIUtils.callApi(this, selectedLines, "extn_containerizeGiftCard_RefId", null);

				}
				else
				{
					var msg = _scScreenUtils.getString(this, "extn_partial_selection");
					this.showConfirmMessageBox(
								this, msg, "handleShipNow", null, null);
				}
				return;
				
			} else {
				var popupMessage = _scScreenUtils.getString(this, "extn_Error_No_Lines_To_Containerize");
				 _isccsBaseTemplateUtils.showMessage(this, popupMessage, "error", null);
				//_scScreenUtils.showErrorMessageBox(this, popupMessage, "waringCallback", null);
			}
        },
		onSingleRowSelect_tracking: function (event, bEvent, ctrl, args) {
			var shipmentLine = null;
			var rowIndex = 0;
			rowIndex = _scBaseUtils.getAttributeValue("rowIndex", false, args);
			_isccsOrderUtils.setCurrentRowIndex(this, rowIndex);
			var selectedRecordList = null;
			selectedRecordList = _scBaseUtils.getAttributeValue("selectedRow", false, args);
			if (_isccsUIUtils.isArray(selectedRecordList)) {
				shipmentLine = _scModelUtils.getModelFromList(selectedRecordList, 0);
			} else {
				shipmentLine = selectedRecordList;
			}
			if (!(_scBaseUtils.isVoid(shipmentLine))) {
				var selectedTargetModel = null;
				selectedTargetModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(this, "activatedGC_listGrid");
				var selectedTargetList = null;
				selectedTargetList = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine", selectedTargetModel);
				if (!(_scBaseUtils.isVoid(selectedTargetList))) {
					shipmentLine = _scBaseUtils.mergeModel(shipmentLine, _scModelUtils.getModelFromList(selectedTargetList, 0), true);
				}
				var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
				if (_scBaseUtils.isVoid(selectedLines)) {
					// Add shipment line to namespace
					var shipmentLineArr = _scBaseUtils.getNewArrayInstance(); 
					_scBaseUtils.appendToArray(shipmentLineArr, shipmentLine);
					var shipmentLineList = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipmentLineArr, shipmentLineList);
					_scScreenUtils.setModel(this, "selectedShipmentLines", shipmentLineList, null);
				} else {
					// Add line to current shipment line
					var shipmentLineArr = _scModelUtils.getModelListFromPath("ShipmentLines.ShipmentLine", selectedLines); 
					_scBaseUtils.appendToArray(shipmentLineArr, shipmentLine);
					_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipmentLineArr, shipmentLineList);
				}
			}
        },
		onSingleRowDeselect_tracking: function (event, bEvent, ctrl, args) {
			var rowIndex = _scBaseUtils.getAttributeValue("rowIndex", false, args);
			var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
			var shipLineArr = selectedLines.ShipmentLines.ShipmentLine;
			if (rowIndex > -1) {
				shipLineArr.splice(rowIndex, 1);
			}
		},
		onHeaderSelect: function(
        event, bEvent, ctrl, args) {
			//this.onRowOrHeaderDeselect(event, bEvent, ctrl, args);
			// Add shipment line to namespace
			var selectedRows = args.selectedRows;
			var selectedLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
			var shipmentLineArr = _scBaseUtils.getNewArrayInstance(); 
			selectedRows.forEach(function(shipmentLine, i8) {
				_scBaseUtils.appendToArray(shipmentLineArr, shipmentLine.rowData);
			});
			var shipmentLineList = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLines");
			_scModelUtils.addListToModelPath("ShipmentLines.ShipmentLine", shipmentLineArr, shipmentLineList);
			_scScreenUtils.setModel(this, "selectedShipmentLines", shipmentLineList, null);
		},
		onRowOrHeaderDeselect: function(
        event, bEvent, ctrl, args) {
			var shipmentLines = _scScreenUtils.getModel(this, "selectedShipmentLines");
			if (!(_scBaseUtils.isVoid(shipmentLines))) {
				// Clear the selected lines model
				shipmentLines = null;
				selectedLines = null;
				_scScreenUtils.setModel(this, "selectedShipmentLines", shipmentLines, null);
			}
		},
		selectOrUnselectRowInPickupPanel: function(
        event, bEvent, ctrl, args) {
            var option = null;
            option = _scBaseUtils.getStringValueFromBean("Select", args);
            var orderLineKey = null;
            orderLineKey = _scBaseUtils.getStringValueFromBean("OrderLineKey", args);
            if (
            _scBaseUtils.equals(
            option, "Y")) {
                var rowIndex = null;
                rowIndex = _scGridxUtils.returnUniqueRowIndexUsingHiddenColumn(
                this, "OLST_listGrid", orderLineKey);
                var isRowSelected = true;
                if (!(
                _scBaseUtils.equals(
                rowIndex, -1))) {
                    isRowSelected = _scGridxUtils.isRowSelectedUsingUId(
                    this, "OLST_listGrid", rowIndex);
                }
                if (!(
                isRowSelected)) {
                    _scGridxUtils.selectUniqueRow(
                    this, "OLST_listGrid", orderLineKey);
                }
            } else {
                _scGridxUtils.deselectRowUsingHiddenColumn(
                this, "OLST_listGrid", orderLineKey);
            }
        },
		refreshGrids: function() {
			// Refresh grids
			var GCFGrid = this.getWidgetByUId("GCF_listGrid");
			_scGridxUtils.refreshGrid(GCFGrid);
			var activatedGCGrid = this.getWidgetByUId("activatedGC_listGrid");
			_scGridxUtils.refreshGrid(activatedGCGrid);
			var shipStatusGrid = this.getWidgetByUId("shipStatus_listGrid");
			_scGridxUtils.refreshGrid(shipStatusGrid);
			// Clear text boxes
			var activateTextbox = this.getWidgetByUId("txtGiftCardNo");
			activateTextbox.set('value',"");
			var trackingTextbox = this.getWidgetByUId("txtTrackingNo");
			trackingTextbox.set('value',"");
		},
	});
});