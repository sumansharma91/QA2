/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Order Management Store (5725-D10)
 *(C) Copyright IBM Corp. 2014 , 2015 All Rights Reserved. , 2015 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!ias/utils/UOMUtils", "scbase/loader!ias/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!wsc/components/shipment/common/screens/ShortageReasonPopupUI", "scbase/loader!wsc/components/shipment/common/utils/ShipmentUtils"], function(
_dojodeclare, _iasScreenUtils, _iasUOMUtils, _iasUIUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _wscShortageReasonPopupUI, _wscShipmentUtils) {
    return _dojodeclare("wsc.components.shipment.common.screens.ShortageReasonPopup", [_wscShortageReasonPopupUI], {
        // custom code here
        initializeScreen: function(
        event, bEvent, ctrl, args) {
            var shortageCodeModel = null;
            var numOfShortageCode = null;
            var zero = 0;
            shortageCodeModel = _scScreenUtils.getModel(
            this, "getShortageReasonCode_output");
            numOfShortageCode = _scModelUtils.getNumberValueFromPath("CommonCodeList.TotalNumberOfRecords", shortageCodeModel);
            if (!(
            _iasUIUtils.isValueNumber(
            numOfShortageCode))) {
                numOfShortageCode = zero;
            }
            if (
            _scBaseUtils.equals(
            0, numOfShortageCode)) {
                _scWidgetUtils.hideWidget(
                this, "InnerContainer", false);
                _scWidgetUtils.hideWidget(
                this, "Popup_btnNext", false);
                _scWidgetUtils.setLabel(
                this, "Popup_btnCancel", _scScreenUtils.getString(
                this, "Ok"));
                _scWidgetUtils.showWidget(
                this, "lblNoShortageConfigured", false, null);
            } else {
                this.hideShortageRadio(
                numOfShortageCode);
                if (
                _scBaseUtils.equals(
                this.flowName, "ContainerPack")) {
                    _scWidgetUtils.setLabel(
                    this, "lblPickedQty", _scScreenUtils.getString(
                    this, "Label_Packed"));
                    _scWidgetUtils.setLabel(
                    this, "chkMarkAllLines", _scScreenUtils.getString(
                    this, "Option_MarkAllLinesWithShortageForPack"));
                }
                if (
                _scBaseUtils.equals(
                this.flowName, "CustomerPick")) {
                    _scWidgetUtils.setLabel(
                    this, "lblPickedQty", _scScreenUtils.getString(
                    this, "Label_Pickedup"));
                    _scWidgetUtils.showWidget(
                    this, "chkMarkAllLines", false, null);
                }
                var eventArgs = null;
                var eventDefn = null;
                eventDefn = {};
                eventArgs = {};
                _scEventUtils.fireEventInsideScreen(
                this, "radSelectShortRea_onChange", eventDefn, eventArgs);
            }
        },
        hideShortageRadio: function(
        numOfShortageCode) {
            if (
            _scBaseUtils.equals(
            1, numOfShortageCode)) {
                _scWidgetUtils.addClass(
                this, "radSelectShortRea", "showRadioSetAsLabel");
            }
        },
        showCancellationWidget: function(
        cancelReasonCodeModel) {
            _scScreenUtils.setModel(
            this, "CancellationReasonCodeData", cancelReasonCodeModel, null);
            _scWidgetUtils.showWidget(
            this, "cancellationReasonCode", false, null);
            _scWidgetUtils.setWidgetMandatory(
            this, "cancellationReasonCode");
        },
        onApply: function(
        event, bEvent, ctrl, args) {
            if (
            _scBaseUtils.equals(
            this.flowName, "CustomerPick")) {
                var cancelReasonModel = null;
                var shortageReasonModel = null;
                shortageReasonModel = _scBaseUtils.getTargetModel(
                this, "getShortageReasonOutput", null);
                cancelReasonModel = _scBaseUtils.getTargetModel(
                this, "getCancellationReasonCodeOutput", null);
                if (
                _scBaseUtils.equals(
                _scModelUtils.getStringValueFromPath("ShortageReason", shortageReasonModel), "Cancel") && _scBaseUtils.isVoid(
                _scModelUtils.getStringValueFromPath("CancelReasonCode", cancelReasonModel))) {
                    _iasScreenUtils.showErrorMessageBoxWithOk(
                    this, "Message_CancellationReasonCode");
                } else {
                    _scWidgetUtils.closePopup(
                    this, "APPLY", false);
                }
            } else {
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
            }
        },
        getPopupOutput: function(
        event, bEvent, ctrl, args) {
            var shortageReasonTargetModel = null;
            var shipmentLineModel = null;
            var shortageReasonCode = null;
            shortageReasonTargetModel = {};
            shipmentLineModel = _scScreenUtils.getModel(
            this, "ShipmentLine");
            var shortageReasonModel = null;
            shortageReasonModel = _scBaseUtils.getTargetModel(
            this, "getShortageReasonOutput", null);
            var shortedShipmentLineModel = null;
            shortedShipmentLineModel = {};
            shortedShipmentLineModel = _scModelUtils.createModelObjectFromKey(this.entity, shortedShipmentLineModel);
            _scModelUtils.setStringValueAtModelPath(
            this.shortageReasonPath, _scModelUtils.getStringValueFromPath("ShortageReason", shortageReasonModel), shortedShipmentLineModel);
            if(!_scBaseUtils.equals("StoreBatchLine",this.entity)) {
            	_scModelUtils.setStringValueAtModelPath("ShipmentLine.ShipmentLineKey", _scModelUtils.getStringValueFromPath("ShipmentLine.ShipmentLineKey", shipmentLineModel), shortedShipmentLineModel);
                _scModelUtils.setStringValueAtModelPath(
                this.markAllBindingPath, _scModelUtils.getStringValueFromPath("MarkAllShortLineWithShortage", shortageReasonModel), shortedShipmentLineModel);
            }
            if (
            _scBaseUtils.equals(
            this.flowName, "CustomerPick")) {
                var cancelReasonModel = null;
                cancelReasonModel = _scBaseUtils.getTargetModel(
                this, "getCancellationReasonCodeOutput", null);
                _scModelUtils.setStringValueAtModelPath(
                this.cancelReasonPath, _scModelUtils.getStringValueFromPath("CancelReasonCode", cancelReasonModel), shortedShipmentLineModel);
            }
            return shortedShipmentLineModel;
        },
        shortageReasonOnChange: function(
        value, bEvent, ctrl, args) {
            var shortageModel = null;
            shortageModel = _scScreenUtils.getTargetModel(
            this, "getShortageReasonOutput", null);
            if (
            _scBaseUtils.equals(
            _scModelUtils.getStringValueFromPath("ShortageReason", shortageModel), "Cancel") && _scBaseUtils.equals(
            this.flowName, "CustomerPick")) {
                var cancelReasonCodeInput = null;
                cancelReasonCodeInput = {};
                _scModelUtils.setStringValueAtModelPath("CommonCode.CodeType", "CUST_PICK_CANCEL_RES", cancelReasonCodeInput);
                var initialInputData = null;
                initialInputData = _scScreenUtils.getInitialInputData(
                this);
                _scModelUtils.setStringValueAtModelPath("CommonCode.CallingOrganizationCode", _scModelUtils.getStringValueFromPath("CommonCode.CallingOrganizationCode", initialInputData), cancelReasonCodeInput);
                _iasUIUtils.callApi(
                this, cancelReasonCodeInput, "getCancellationReasonList", null);
            } else {
                _scWidgetUtils.hideWidget(
                this, "cancellationReasonCode", false);
                _scWidgetUtils.setWidgetNonMandatory(
                this, "cancellationReasonCode");
            }
        },
        getFormattedDisplayQuantity: function(
        dataValue, screen, widget, namespace, modelObject, options) {
        	
        	if(_scBaseUtils.equals(this.entity,"StoreBatchLine")) {
        		var displayQty = _scModelUtils.getStringValueFromPath("StoreBatchLine.DisplayQty",modelObject);
        		return this.getFormattedDisplayQtyForBatch(displayQty, modelObject);
        	}
        	
            var displayQty = null;
            if (
            _scBaseUtils.equals(
            this.flowName, "ContainerUnpack")) {
                _scWidgetUtils.setLabel(
                this, "lblPickedQty", _scScreenUtils.getLabelString(
                this, "Label_Packed"));
            }
            displayQty = _wscShipmentUtils.getFormattedDisplayQuantity(
            dataValue, this, null, null, modelObject, null);
            return displayQty;
        },
        getFormattedDisplayShortQuantity: function(
        dataValue, screen, widget, namespace, modelObject, options) {
        	
        	if(_scBaseUtils.equals(this.entity,"StoreBatchLine")) {
        		var shortQty = _scModelUtils.getStringValueFromPath("StoreBatchLine.DisplayShortQty",modelObject);
        		return this.getFormattedDisplayQtyForBatch(shortQty,modelObject);
        	}
        	
            var displayQty = null;
            displayQty = _wscShipmentUtils.getFormattedDisplayQuantity(
            dataValue, this, null, null, modelObject, null);
            return displayQty;
        },
        getItemImageLocation:function(screen, widget, namespace, modelObject, options) {
        	var dataValue = "";
        	if(_scBaseUtils.equals(this.entity,"StoreBatchLine")) {
        		dataValue = _scModelUtils.getStringValueFromPath("StoreBatchLine.ItemDetails.PrimaryInformation",modelObject);
        	}
        	
        	return _iasScreenUtils.getItemImageLocation(screen, widget, namespace, modelObject, options, dataValue);
        },
        getItemImageId:function(screen, widget, namespace, modelObject, options) {
        	var dataValue = "";
        	if(_scBaseUtils.equals(this.entity,"StoreBatchLine")) {
        		dataValue = _scModelUtils.getStringValueFromPath("StoreBatchLine.ItemDetails.PrimaryInformation",modelObject);
        	}
        	return _iasScreenUtils.getItemImageId(screen, widget, namespace, modelObject, options,dataValue);
        },
        getItemDisplayDescription: function(
                dataValue, screen, widget, namespace, modelObject, options) {
        	if(_scBaseUtils.equals(this.entity,"StoreBatchLine")) {
        		return _scModelUtils.getStringValueFromPath("StoreBatchLine.ItemDetails.PrimaryInformation.ExtendedDisplayDescription",modelObject);
        	}
        	
        	return dataValue;
        },
        getFormattedDisplayQtyForBatch:function(quantity,modelObject) {
        	var uom = _scModelUtils.getStringValueFromPath("StoreBatchLine.ItemDetails.DisplayUnitOfMeasure", modelObject);
			var uomDisplayFormat = _scModelUtils.getStringValueFromPath("StoreBatchLine.ItemDetails.UOMDisplayFormat", modelObject);
			return _iasUOMUtils.getFormatedQuantityWithUom(quantity, uom, uomDisplayFormat);
        }
    });
});