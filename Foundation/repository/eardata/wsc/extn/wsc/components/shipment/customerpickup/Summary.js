scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/PrintUtils", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/RepeatingPanelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!wsc/components/common/utils/CommonUtils", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils", "scbase/loader!wsc/components/shipment/customerpickup/SummaryUI"], function(
_dojodeclare, _iasPrintUtils, _iasRepeatingScreenUtils, _iasContextUtils, _iasScreenUtils, _iasUIUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scRepeatingPanelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils, _wscCommonUtils, _wscShipmentUtils, _wscSummaryUI) {
    return _dojodeclare("wsc.components.shipment.customerpickup.Summary", [_wscSummaryUI], {
        // custom code here
        initialize: function(
        event, bEvent, ctrl, args) {
            var customerVerification = null;
            customerVerification = _iasUIUtils.getWizardModel(
            this, "customerVerificationModel");
            _scScreenUtils.setModel(
            this, "CustomerVerificationNotes_Input", customerVerification, null);
            var shipmentDetailModel = null;
            var numberZero = 0;
            shipmentDetailModel = _scScreenUtils.getModel(
            this, "ShipmentDetails");
            var originalShipmentKey = null;
            originalShipmentKey = _scModelUtils.getStringValueFromPath("Shipment.OriginalShipmentKey", shipmentDetailModel);
			var tabCount = 4;
            if (
            _scBaseUtils.isVoid(
            originalShipmentKey)) {
                _iasScreenUtils.hideView(
                this, "previouslyPicked");
                this.previouslyPicked_show = false;
				tabCount--;
            }
            if (
            _scBaseUtils.equals(
            numberZero, _scModelUtils.getNumberValueFromPath("ShipmentLines.TotalNumberOfRecords", _scScreenUtils.getModel(
            this, "currentPickupLinesCount")))) {
                _iasScreenUtils.hideView(
                this, "currentPickup");
                this.currentPickup_show = false;
				tabCount--;
            }
            if (
            _scBaseUtils.equals(
            numberZero, _scModelUtils.getNumberValueFromPath("ShipmentLines.TotalNumberOfRecords", _scScreenUtils.getModel(
            this, "remainingProductsLinesCount")))) {
                _iasScreenUtils.hideView(
                this, "remainingProducts");
                this.remainingProducts_show = false;
				tabCount--;
            }
            if (
            _scBaseUtils.equals(
            numberZero, _scModelUtils.getNumberValueFromPath("ShipmentLines.TotalNumberOfRecords", _scScreenUtils.getModel(
            this, "cancelledProductsLinesCount")))) {
                _iasScreenUtils.hideView(
                this, "cancelledProducts");
                this.cancelledProducts_show = false;
				tabCount--;
            }
            if (
            this.currentPickup_show) {
                this.currentView = "currentPickup";
            } else if (
            this.remainingProducts_show) {
                this.currentView = "remainingProducts";
            } else if (
            this.cancelledProducts_show) {
                this.currentView = "cancelledProducts";
            } else if (
            this.previouslyPicked_show) {
                this.currentView = "previouslyPicked";
            } else {
                this.currentView = "";
            }
			
			if (_iasContextUtils.isMobileContainer() && tabCount > 1) {				
				this.currentView = "allProducts";
				
			}
			else{
				_iasScreenUtils.hideView(this, "allProducts");
				
			}
			
			_iasScreenUtils.hideTabForSingleView(this,'tabPnl');
			if (_iasContextUtils.isMobileContainer()) {
                _scWidgetUtils.hideWidget(
                this, "lblOrderNo", false);
				var blankModel = {};
				var eventBean = {};
				_scBaseUtils.setAttributeValue("argumentList", blankModel, eventBean);
				_scBaseUtils.setAttributeValue("argumentList.Shipment", shipmentDetailModel, eventBean);
				_scEventUtils.fireEventToParent(this, "setWizardDescription", eventBean);
			}
        },
		initializeProductTabs : function(event, bEvent, ctrl, args){
			var productTabsModel = { "Tabs":{"Tab":[]}};			
			var producttabs = _scScreenUtils.getWidgetByUId(this,"tabPnl").getChildren();
			for(var i=0; i<producttabs.length;i++){
				var tab = {
					"TabUId" : producttabs[i].uId,
					"TabDescription" : producttabs[i].value					
				};
				if(!producttabs[i].isHidden){
					productTabsModel.Tabs.Tab.push(tab);
				}			
			}
			if(productTabsModel.Tabs.Tab.length===1){
				_scWidgetUtils.addClass(this,"tabFilterSelectContainer","singleTab");
			}	
			_scScreenUtils.setModel(this, "ProductTabs", productTabsModel, null);
			_scScreenUtils.setModel(this, "currentProductTab", {"initTabUId":this.currentView}, null);						
		},
        getFormattedNameDisplay: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            var nameModel = null;
            var formattedName = null;
            nameModel = {};
            _scModelUtils.setStringValueAtModelPath("FirstName", _scModelUtils.getStringValueFromPath("Shipment.FirstNameForDisplay", modelObj), nameModel);
            _scModelUtils.setStringValueAtModelPath("LastName", _scModelUtils.getStringValueFromPath("Shipment.LastNameForDisplay", modelObj), nameModel);
            formattedName = _wscShipmentUtils.getNameDisplay(
            this, nameModel);
            return formattedName;
        },
        getDisplayOrderNo: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            var pickupOrderNo = null;
            pickupOrderNo = _wscCommonUtils.getDisplayOrderNumber(
            dataValue, "COMMAS");
            return pickupOrderNo;
        },
        save: function(
        event, bEvent, ctrl, args) {
            var shipmentModel = null;
            shipmentModel = _scScreenUtils.getTargetModel(
            this, "recordCustomerPick_input", null);
            shipmentModel = _scBaseUtils.cloneModel(
            shipmentModel);
            var customerVerification = null;
            customerVerification = _iasUIUtils.getWizardModel(
            this, "customerVerificationModel");
            shipmentModel = _scBaseUtils.mergeModel(
            shipmentModel, customerVerification, false);
            _iasUIUtils.callApi(
            this, shipmentModel, "recordCustomerPick", null);
        },
        handlePaymentMethod: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            if (
            _scBaseUtils.isVoid(
            dataValue)) {
                _scWidgetUtils.hideWidget(
                this, "lblPaymentMethod", true);
            } else {
                var formattedPaymentMtd = null;
                var inputArray = null;
                inputArray = [];
                inputArray.push(
                _scModelUtils.getStringValueFromPath("Shipment.CreditCardTypeDesc", modelObj));
                inputArray.push(
                _scModelUtils.getStringValueFromPath("Shipment.PaymentMethod", modelObj));
                formattedPaymentMtd = _scScreenUtils.getFormattedString(
                this, "PaymentMethod", inputArray);
                return formattedPaymentMtd;
            }
            return dataValue;
        },
		allProducts_onShow: function(
        event, bEvent, ctrl, args) {
            if (!(
            this.allProducts_loaded)) {
                this.allProducts_loaded = true;
                var shipmentLineModel = null;
                var shipmentDetailModel = null;
                var shipmentKey = null;
                shipmentDetailModel = _scScreenUtils.getModel(
                this, "ShipmentDetails");
                shipmentLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
                shipmentKey = _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", shipmentKey, shipmentLineModel);
                _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "allProductsLineList", "allProductLines", shipmentLineModel, "allShipmentLineList");
            }
        },
        currentPickup_onShow: function(
        event, bEvent, ctrl, args) {
            if (!(
            this.currentPickup_loaded)) {
                this.currentPickup_loaded = true;
                var shipmentLineModel = null;
                var shipmentDetailModel = null;
                var shipmentKey = null;
                shipmentDetailModel = _scScreenUtils.getModel(
                this, "ShipmentDetails");
                shipmentLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
                shipmentKey = _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", shipmentKey, shipmentLineModel);
                _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "currentPickupLineList", "currentPickupLines", shipmentLineModel, "currentPickupShipmentLineList");
            }
        },
        remainingProducts_onShow: function(
        event, bEvent, ctrl, args) {
            if (!(
            this.remainingProducts_loaded)) {
                this.remainingProducts_loaded = true;
                var shipmentLineModel = null;
                var shipmentDetailModel = null;
                var shipmentKey = null;
                shipmentDetailModel = _scScreenUtils.getModel(
                this, "ShipmentDetails");
                shipmentLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
                shipmentKey = _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", shipmentKey, shipmentLineModel);
                _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "remainingProductsLineList", "remainingProductsLines", shipmentLineModel, "remainingProductsShipmentLineList");
            }
        },
        cancelledProducts_onShow: function(
        event, bEvent, ctrl, args) {
            if (!(
            this.cancelledProducts_loaded)) {
                this.cancelledProducts_loaded = true;
                var shipmentLineModel = null;
                var shipmentDetailModel = null;
                var shipmentKey = null;
                shipmentDetailModel = _scScreenUtils.getModel(
                this, "ShipmentDetails");
                shipmentLineModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
                shipmentKey = _scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentDetailModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", shipmentKey, shipmentLineModel);
                _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "cancelledProductsLineList", "cancelledProductsLines", shipmentLineModel, "cancelledProductsShipmentLineList");
            }
        },
        previouslyPicked_onShow: function(
        event, bEvent, ctrl, args) {
            if (!(
            this.previouslyPicked_loaded)) {
                this.previouslyPicked_loaded = true;
                var shipmentDetailModel = null;
                var previouslyPickedupShipmentModel = null;
                var originalShipmentKey = null;
                shipmentDetailModel = _scScreenUtils.getModel(
                this, "ShipmentDetails");
                previouslyPickedupShipmentModel = _scModelUtils.createNewModelObjectWithRootKey("ShipmentLine");
                originalShipmentKey = _scModelUtils.getStringValueFromPath("Shipment.OriginalShipmentKey", shipmentDetailModel);
                _scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentKey", originalShipmentKey, previouslyPickedupShipmentModel);
                _scRepeatingPanelUtils.startPaginationUsingUId(
                this, "previouslyPickedLineList", "previouslyPickedLines", previouslyPickedupShipmentModel, "previouslyPickedShipmentLineList");
            }
        },
        handleRecordCustomerPick: function(
        modelOutput) {
            var shipmentStatus = null;
            shipmentStatus = _scModelUtils.getStringValueFromPath("Shipment.Status", modelOutput);
            if (
            _scBaseUtils.contains(shipmentStatus, "9000")) {
                _iasScreenUtils.showInfoMessageBoxWithOk(
                this, _scScreenUtils.getString(
                this, "Message_RecordCustomerPickCancel"), "gotoNextScreen", null);
            } else if (
            _scBaseUtils.contains(shipmentStatus, "1400")) {
                _iasScreenUtils.showInfoMessageBoxWithOk(
                this, _scScreenUtils.getString(
                this, "Message_RecordCustomerPickSuccess"), "gotoNextScreen", null);
            } else {
                _iasScreenUtils.showInfoMessageBoxWithOk(
                this, _scScreenUtils.getString(
                this, "Message_RecordCustomerPickNotProcessed"), "gotoNextScreen", null);
            }
        },
        printPickupAcknowledgement: function(
        res, args) {
            if (
            _scBaseUtils.equals(
            res, "Ok")) {
                var printAckModel = null;
                printAckModel = _scScreenUtils.getTargetModel(
                this, "printAcknowledgement_input", null);
                _iasUIUtils.callApi(
                this, printAckModel, "printAcknowledgement", null);
            } else {
                this.gotoNextScreen();
            }
        },
        handlePrintAcknowledgement: function(
        modelOutput) {
            _iasPrintUtils.printHtmlOutput(
            modelOutput);
            this.gotoNextScreen();
        },
        gotoNextScreen: function() {
            _scEventUtils.fireEventToParent(
            this, "onSaveSuccess", null);
        },
        getDayPhone: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            if (
            _scBaseUtils.isVoid(
            dataValue)) {
                _scWidgetUtils.hideWidget(
                this, "pnlPhoneHolder", true);
            }
            return dataValue;
        },
        getEmailID: function(
        dataValue, screen, widget, namespace, modelObj, options) {
            if (
            _scBaseUtils.isVoid(
            dataValue)) {
                _scWidgetUtils.hideWidget(
                this, "pnlEmailHolder", true);
            }
            return dataValue;
        }
    });
});