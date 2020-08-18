/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/lang","dojo/text!./templates/VSIAcknowledgeReturnArticleDisposition.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/form/CheckBox", "scbase/loader!idx/form/FilteringSelect", "scbase/loader!idx/form/NumberTextBox", "scbase/loader!idx/form/TextBox", "scbase/loader!idx/layout/ContentPane", "scbase/loader!idx/layout/TitlePane", "scbase/loader!isccs/return/create/preview/ReturnViolationDisplay", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ReturnUtils", "scbase/loader!isccs/utils/SharedComponentUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder", "scbase/loader!sc/plat/dojo/binding/ComboDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", "scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!sc/plat/dojo/widgets/Screen"], function(
dLang,templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxCheckBox, _idxFilteringSelect, _idxNumberTextBox, _idxTextBox, _idxContentPane, _idxTitlePane, _isccsReturnViolationDisplay, _isccsBaseTemplateUtils, _isccsReturnUtils, _isccsSharedComponentUtils, _isccsUIUtils, _scplat, _scButtonDataBinder, _scCheckBoxDataBinder, _scComboDataBinder, _scCurrencyDataBinder, _scSimpleDataBinder, _scAdvancedTableLayout, _scBaseUtils, _scEventUtils, _scGridxUtils, _scModelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils, _scDataLabel, _scLabel, _scLink, _isccsModelUtils, _scScreen) {
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnArticleDisposition", [_scScreen], {
        templateString: templateText,
        postMixInProperties: function() {
		
		     
        },
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.return.acknowledge.templates", "VSIAcknowledgeReturnArticleDisposition.html"),
            shared: true
        },
        uId: "VSIAcknowledgeReturnArticleDisposition",
        packageName: "extn.return.acknowledge",
        className: "VSIAcknowledgeReturnArticleDisposition",
        extensible: true,
        title: "",
        screen_description: "This screen shows the details of an order line associated with an order which can be returned.",
        namespaces: {
            targetBindingNamespaces: [{
                description: 'This is the target namespace for the return line data.',
                value: 'returnLine'
            },  {
                description: 'Used in a mode to not include data in the returnLine model.',
                value: 'dummyTarget'
            },  {
                description: 'This is used to create an exchange order using the data from the return order.',
                value: 'createExchange_input'
            }],
            sourceBindingNamespaces: [{
                description: 'Stores the selected order line that is displayed.',
                value: 'selectedOrderLine'
            }, {
                description: 'If multiple lines have been passed, this stores the lines data.',
                value: 'selectedOrderLineList'
            }, {
                description: 'These are the return Disposition reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnDispositionList_output'
            }, {
                description: 'These are the return reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnReasonList_output'
            }, {
                description: 'This will hold the value of Return Receiving Node',
                value: 'acknowledgeReturn_getReturnDCList_input'
            }, {
                description: 'This is a way to default data in a few fields outside of selectedOrderLine.',
                value: 'defaultReturn'
            }, {
                description: 'Return Violation Messages',
                value: 'returnViolations'
            }, {
                description: 'This is used to paint the reason codes in the drop down.',
                value: 'getReturnReasonList_output'
            }, {
                description: 'This is used to get the complete Order details from parent screen',
                value: 'getCompleteOrderLineList_output'
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'OrderLine.Item.ItemDesc',
                namespace: 'returnLine'
            },
            sourceBinding: {
                path: 'OrderLine.Item.ItemDesc',
                namespace: 'selectedOrderLine'
            }
        }],
        showRelatedTask: false,
        isDirtyCheckRequired: false,
        hotKeys: [],
        events: [{
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }],
        subscribers: {
            local: [
			
			{
                eventId: 'btnAddToReturn_onClick',
                sequence: '51',
                handler: {
                    methodName: "manageReceiveQuantity"
                }
            }, {
                eventId: 'quantity_onKeyUp',
                sequence: '30',
                handler: {
                    methodName: "validateQuantity"
                }
            }, {
                eventId: 'cmbReturnReason_onChange',
                sequence: '30',
                handler: {
                    methodName: "cmbReturnReasonOnChange"
                }
            }],
        },
		
		cmbReturnReasonOnChange: function() {
			_scWidgetUtils.setFocusOnWidgetUsingUid(this, "btnAddToReturn");
		},
		
		manageReceiveQuantity: function (event, bEvent, ctrl, args) {
		
		var vselectedLine = _scScreenUtils.getModel(this, "selectedOrderLine", null);
		console.log("This is in manageReceiveQuantity to get the selectedline object",vselectedLine);
		
		
		var vreturnLine = _scScreenUtils.getTargetModel(this, "returnLine", null);
		console.log("This is in manageReceiveQuantity to get the return line object",vreturnLine);
		
		var receivableQty = 0;
		var receivedQty = 0;
		var shippedQty = 0;
		var qtyToReturn = 0;
		var qtyCanReturn = 0;
			
		receivableQty = _scModelUtils.getNumberValueFromPath("OrderLine.ReturnableQty", vselectedLine);
		shippedQty = _scModelUtils.getNumberValueFromPath("OrderLine.ShippedQuantity", vselectedLine);
		receivedQty = _scModelUtils.getNumberValueFromPath("OrderLine.ReceivedQty", vselectedLine);
		qtyToReturn = _scModelUtils.getNumberValueFromPath("Receipt.ReceiptLines.ReceiptLine.Quantity", vreturnLine);
		var sOrderedQty = _scModelUtils.getStringValueFromPath("Receipt.ReceiptLines.ReceiptLine.Quantity", vreturnLine);
		var sReasonCode = _scModelUtils.getStringValueFromPath("Receipt.ReceiptLines.ReceiptLine.ReasonCode", vreturnLine);
		var sDispoCode = _scModelUtils.getStringValueFromPath("Receipt.ReceiptLines.ReceiptLine.DispositionCode", vreturnLine);
		
		qtyCanReturn = shippedQty - receivedQty;
		
		if (_scBaseUtils.isVoid(sOrderedQty) || _scBaseUtils.isVoid(sReasonCode) || _scBaseUtils.isVoid(sDispoCode)) {
		_scScreenUtils.showErrorMessageBox(this, "Mandatory Fields are missing",null, null, null);
		} else if(qtyToReturn > qtyCanReturn) {
			var sMessage = "";
            sMessage = _scScreenUtils.getString(this, "You_Are_Attempting_To_Return_More_Items_Than_Are_Available_To_Return");
			_scScreenUtils.showErrorMessageBox(this, sMessage,null, null, null);
            
		} else if(qtyToReturn <= 0) {
			_scScreenUtils.showErrorMessageBox(this, "Quantity entered should be greater than zero",null, null, null);
		}else {
		
		var sDocumentType = _scModelUtils.getStringValueFromPath("OrderLine.DocumentType", vselectedLine);
		var sOrderHeaderKey = _scModelUtils.getStringValueFromPath("OrderLine.OrderHeaderKey", vselectedLine);
		var sItemID = _scModelUtils.getStringValueFromPath("OrderLine.ItemDetails.ItemID", vselectedLine);
		var sPrimeLineNo = _scModelUtils.getStringValueFromPath("OrderLine.PrimeLineNo", vselectedLine);
		var sSubLineNo = _scModelUtils.getStringValueFromPath("OrderLine.SubLineNo", vselectedLine);
		
		//var sShipNode = this.ownerScreen.ownerScreen.uIdMap.txtWarehouseDC.value;
		
		var platUIUtils = dLang.getObject("dojo.utils.PlatformUIFmkImplUtils", true,
				_scplat);
		//console.log(platUIUtils);
		//_scWidgetUtils.setWidgetMandatory(this, "txt_orderReturnNo2");
		 var orgObject = null;
         orgObject = platUIUtils.getUserOrganization();
         //console.log('organization is',orgObject.Organization.OrganizationName);
         var sShipNode = orgObject.Organization.OrganizationCode;
		_scModelUtils.setStringValueAtModelPath("OrderLine.ShipNode", sShipNode , vselectedLine);
		_scModelUtils.setStringValueAtModelPath("Receipt.ReceivingNode", sShipNode , vreturnLine);
		_scModelUtils.setStringValueAtModelPath("Receipt.Shipment.OrderHeaderKey", sOrderHeaderKey , vreturnLine);
		_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.OrderHeaderKey", sOrderHeaderKey , vreturnLine);
		
		_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.ItemID", sItemID , vreturnLine);
		
		
		/*if (_scBaseUtils.equals(sDocumentType, "0003")) {
		
				
		_scModelUtils.setStringValueAtModelPath("Receipt.DocumentType", sDocumentType , vreturnLine);
		_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.PrimeLineNo", sPrimeLineNo , vreturnLine);
		_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.SubLineNo", sSubLineNo , vreturnLine);
		
		_isccsModelUtils.removeAttributeFromModel("Receipt.ReceiptLines.ReceiptLine.ReasonCode", vreturnLine);
		
		console.log("This is in manageReceiveQuantity to get the Document Type object",sDocumentType);
		console.log("This is in manageReceiveQuantity to get the OrderHeaderKey object",sOrderHeaderKey);
		
		console.log("This is in manageReceiveQuantity to get the vreceiveOrderLine object",vreturnLine);
		
		_isccsUIUtils.callApi(this, vreturnLine, "returns_receiveOrder_service", null);
		
		} else {*/
		
		
		var sReturnOrderLines = _scModelUtils.getStringValueFromPath("OrderLine.ReturnOrderLines", vselectedLine);
		
				if (!_scBaseUtils.isVoid(sReturnOrderLines)) {
				
				_scModelUtils.setStringValueAtModelPath("Receipt.DocumentType", "0003" , vreturnLine);			
				_scModelUtils.setStringValueAtModelPath("Receipt.SalesOrderHeaderKey", sOrderHeaderKey , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.ReturnOrderLines", sReturnOrderLines , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.ReturnOrderHeaderKey", sReturnOrderLines.OrderLine.OrderHeaderKey , vreturnLine);
				_isccsUIUtils.callApi(this, vreturnLine, "returns_checkAndreceive_service", null);
				
				} else {
				
				var sReasonCode = _scModelUtils.getStringValueFromPath("Receipt.ReceiptLines.ReceiptLine.ReasonCode", vreturnLine);
				var sOrderedQty = _scModelUtils.getStringValueFromPath("Receipt.ReceiptLines.ReceiptLine.Quantity", vreturnLine);
				var sOrderLineKey = _scModelUtils.getStringValueFromPath("OrderLine.OrderLineKey", vselectedLine);
					
				_scModelUtils.setStringValueAtModelPath("Receipt.DocumentType", "0003" , vreturnLine);			
				_scModelUtils.setStringValueAtModelPath("Receipt.SalesOrderHeaderKey", sOrderHeaderKey , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.DocumentType", "0003" , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.DraftOrderFlag", "Y" , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.EnterpriseCode", this.ownerScreen.strEnterpriseCode , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.EntryType", "Call Center" , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.SellerOrganizationCode", this.ownerScreen.strEnterpriseCode , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.OrderLines.OrderLine.OrderedQty", sOrderedQty , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.OrderLines.OrderLine.ReturnReason", sReasonCode , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.OrderLines.OrderLine.ShipNode", sShipNode , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.OrderLines.OrderLine.Item.ItemID", sItemID , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.Order.OrderLines.OrderLine.DerivedFrom.OrderLineKey", sOrderLineKey , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.PrimeLineNo", "1" , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.SubLineNo", "1" , vreturnLine);
				_scModelUtils.setStringValueAtModelPath("Receipt.ReceiptLines.ReceiptLine.Extn.ExtnWHRtnRsnCode", sReasonCode , vreturnLine);
				_isccsModelUtils.removeAttributeFromModel("Receipt.ReceiptLines.ReceiptLine.ReasonCode", vreturnLine);
				
				
				console.log("This is in manageReceiveQuantity to set the draft Order attributes",vreturnLine);
					
				_isccsUIUtils.callApi(this, vreturnLine, "returns_createAndreceive_service", null);
				
				}
		
		
		
		//}
		}
		
		},
		
        validateQuantity: function(event, bEvent, ctrl, args) {

			_scWidgetUtils.setWidgetMandatory(this, "quantity");
			
			var vselectedLine = _scScreenUtils.getModel(this, "selectedOrderLine", null);
			console.log("This is in validateQuantity to get the selectedline object",vselectedLine);
			
			var vreturnLine = _scScreenUtils.getTargetModel(this, "returnLine", null);
			console.log("This is in manageReceiveQuantity to get the return line object",vreturnLine);
			
			var receivableQty = 0;
			var qtyToReturn = 0;
			var receivedQty = 0;
			var shippedQty = 0;
			var qtyCanReturn = 0;
			
			shippedQty = _scModelUtils.getNumberValueFromPath("OrderLine.ShippedQuantity", vselectedLine);
			receivedQty = _scModelUtils.getNumberValueFromPath("OrderLine.ReceivedQty", vselectedLine);
			
			qtyCanReturn = shippedQty - receivedQty;
			
			receivableQty = _scModelUtils.getNumberValueFromPath("OrderLine.ReturnableQty", vselectedLine);
			qtyToReturn = _scModelUtils.getNumberValueFromPath("Receipt.ReceiptLines.ReceiptLine.Quantity", vreturnLine);
			
			if( qtyToReturn <= 0) {
			
			console.log("This is in if of negative in ValidateQuantity");
			var widget = _scEventUtils.getOriginatingControlUId(bEvent);
             _scWidgetUtils.markFieldinError(this, widget, "Quantity to Return cannot be zero", true);
			} else if(qtyToReturn > qtyCanReturn) {
			console.log("This is in else if of greaterthan in ValidateQuantity");
			var sMessage = "";
            sMessage = _scScreenUtils.getString(this, "You_Are_Attempting_To_Return_More_Items_Than_Are_Available_To_Return");
            var widget = "";
            widget = _scEventUtils.getOriginatingControlUId(bEvent);
            _scWidgetUtils.markFieldinError(this, widget, sMessage, true);
			} else{
			
			_scWidgetUtils.clearFieldinError(this, "quantity");
			
			}

        },
       
        handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (_scBaseUtils.equals(mashupRefId, "getReturnDispositionList")) {
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "getReturnDispositionList_output", modelOutput, null);
                }
            }
			if (_scBaseUtils.equals(mashupRefId, "getInnerReturnReasonList")) {
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "getReturnReasonList_output", modelOutput, null);
                }
            }
			if (_scBaseUtils.equals(mashupRefId, "returns_createAndreceive_service") || _scBaseUtils.equals(mashupRefId, "returns_checkAndreceive_service")) {
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    
					
					// Clear text boxes
					_scWidgetUtils.hideWidget(this, "addItemPanelMainContainer", false);
					
				/*	var quantityTextbox = this.getWidgetByUId("quantity");
					quantityTextbox.reset();
					var cmbDispReasonFilterbox = this.getWidgetByUId("cmbDispReason");
					cmbDispReasonFilterbox.reset();
					var cmbReturnReasonFilterbox = this.getWidgetByUId("cmbReturnReason");
					cmbReturnReasonFilterbox.reset();
					*/
					_scEventUtils.fireEventToParent(this, "SST_SearchButton_onClick", null);
					_isccsBaseTemplateUtils.showMessage(this, "Item(s) received successfully.", "success", null);
                }
            }
        },
        handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        }
    });
});