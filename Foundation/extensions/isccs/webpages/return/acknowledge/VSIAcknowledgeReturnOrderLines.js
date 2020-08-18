/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/VSIAcknowledgeReturnOrderLines.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!gridx/Grid", "scbase/loader!idx/layout/ContentPane", "scbase/loader!extn/return/acknowledge/VSIAcknowledgeReturnArticleDispositionInitController", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/OrderLineUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/ReturnUtils", "scbase/loader!isccs/utils/SearchUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/GridxDataBinder", "scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout", "scbase/loader!sc/plat/dojo/plugins/gridx/Pagination", "scbase/loader!sc/plat/dojo/plugins/gridx/PaginationBar", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/PaginationUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/ControllerWidget", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Screen"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _gridxGrid, _idxContentPane, _isccsVSIAcknowledgeReturnArticleDispositionInitController, _isccsBaseTemplateUtils, _isccsOrderLineUtils, _isccsOrderUtils, _isccsReturnUtils, _isccsSearchUtils, _isccsUIUtils, _scplat, _scCurrencyDataBinder, _scGridxDataBinder, _scAdvancedTableLayout, _scPagination, _scPaginationBar, _scBaseUtils, _scControllerUtils, _scEventUtils, _scGridxUtils, _scModelUtils, _scPaginationUtils, _scScreenUtils, _scWidgetUtils, _scControllerWidget, _scDataLabel, _scIdentifierControllerWidget, _scLabel, _scScreen) {
    var strEnterpriseCode="";
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnOrderLines", [_scScreen], {
        templateString: templateText,
        postMixInProperties: function() {
            if (this.getScreenMode() != "default") {
                var origArgs = arguments;
                var htmlName = "templates/VSIAcknowledgeReturnOrderLines_" + this.getScreenMode() + ".html";
                this.templateString = dojo.cache("extn.return.acknowledge", htmlName);
                var modeUIJSClassString = "extn.return.acknowledge.VSIAcknowledgeReturnOrderLines_" + this.getScreenMode();
                var that = this;
                var _scUtil = _dojolang.getObject("dojo.utils.Util", true, _scplat);
                _scUtil.getInstance(modeUIJSClassString, null, null, function(instance) {
                    _scBaseUtils.screenModeMixin(that, instance);
                    that.inherited(origArgs);
                });
            }
        },
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.return.acknowledge.templates", "VSIAcknowledgeReturnOrderLines.html"),
            shared: true
        },
        uId: "returnOrderlinelistscreen",
        packageName: "extn.return.acknowledge",
        className: "VSIAcknowledgeReturnOrderLines",
        title: "",
        screen_description: "Shows all return order lines associated with an order.",
        showRelatedTask: false,
        lstOutputNamespace: 'getCompleteOrderLineList_output',
        isDirtyCheckRequired: false,
        lstOrderNamespace: 'getCompleteOrderDetails_output',
        staticBindings: [
		],
        namespaces: {
            targetBindingNamespaces: [
			
			
			],
            sourceBindingNamespaces: [{
                description: 'This namespace is used to store the input used to load the data in the grid. It is used when the grid is refreshed.',
                value: 'LST_listAPIInput'
            }, {
                description: 'This is the selected order line used by the preview.',
                value: 'selectedOrderLine'
            },{
                description: 'This is the order intial input to the screen.',
                value: 'screenInput'
            }, {
                description: 'These are the return Disposition reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnDispositionList_output'
            }, {
                description: 'These are the return reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnReasonList_output'
            }, {
                description: 'This holds the value of Return receiving node in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'acknowledgeReturn_getReturnDCList_input'
            }, {
                description: 'Holds the Order info painted on the title view.',
                value: 'getOrderHeaderInfo'
            }, {
                description: 'This is the output namespaces used by the screen',
                value: 'getCompleteOrderLineList_output'
            }]
        },
        hotKeys: [],
        events: [{
            name: 'callListApi'
        }, {
            name: 'afterScreenInit'
        }],
        subscribers: {
            local: [{
                eventId: 'callListApi',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "LST_executeApi",
                    description: ""
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '50',
                description: 'Subscriber for after screen is initialized',
                handler: {
                    methodName: "setInitialized"
                }
            },{
                eventId: 'OLST_listGrid_ScRowSelect',
                sequence: '25',
                description: 'Listens for Row Change',
                handler: {
                    methodName: "onSingleRowSelect",
                    description: "Handles the single Click from List"
                }
            } ],
        },
       
        handleMashupOutput: function(mashupRefId, modelOutput, modelInput, mashupContext, applySetModel) {
            
			if (_scBaseUtils.equals(mashupRefId, "getCompleteOrderLineList")) {
			if (!(_scBaseUtils.equals(false, applySetModel))) {
				console.log("This is in HandleMashup of ReturnOrderlines",modelOutput);
				
				this.strEnterpriseCode = modelOutput.Order.EnterpriseCode;
				var isMigrated = _scModelUtils.getStringValueFromPath("Order.ExtnIsMigrated", modelOutput);
				console.log("This is in HandleMashup of ReturnOrderlines for ExtnIsMigrated",isMigrated);
				if(!(_scBaseUtils.isVoid(isMigrated))){
					var warningmessage = _scScreenUtils.getString(this, "extn_migrated_return_order_message");
					_isccsBaseTemplateUtils.showMessage(this,warningmessage, "warning", null);
				
				} else {
					var orderLineList = _scModelUtils.createNewModelObjectWithRootKey("OrderLines");
					var tempOrderLines = modelOutput.Order.OrderLines;
					orderLineList.OrderLines = tempOrderLines;
					
					console.log("This is in HandleMashup of tempOrderLines",tempOrderLines);
					console.log("This is in HandleMashup of orderLineList",orderLineList);
					
					_scScreenUtils.setModel(this, "getOrderHeaderInfo", orderLineList, null);
					var finalModel = _scScreenUtils.getModel(this, "getOrderHeaderInfo", null);
					
					console.log("This is in HandleMashup of finalModel",finalModel);
					
					//Clear child fields
			var scrChild = this._allChildScreens[0];
			
			var quantityTextbox = scrChild.getWidgetByUId("quantity");
				quantityTextbox.reset();
			var cmbDispReasonFilterbox = scrChild.getWidgetByUId("cmbDispReason");
				cmbDispReasonFilterbox.reset();
			var cmbReturnReasonFilterbox = scrChild.getWidgetByUId("cmbReturnReason");
				cmbReturnReasonFilterbox.reset();
			var itemDescLabel = scrChild.getWidgetByUId("itemDesc");
				itemDescLabel.set('value',"");
			_scWidgetUtils.hideWidget(scrChild, "addItemPanelMainContainer", false);
			
			_scWidgetUtils.showWidget(this, "pnlNoReturnLines", true, null);
			_scWidgetUtils.showWidget(this, "lblNoCriteriaSelected", true, null);
			
			if(_scBaseUtils.isVoid(tempOrderLines)){
			console.log("This is in HandleMashup of void");
			_scWidgetUtils.showWidget(this, "pnlNoReturnLines", true, null);
			_scWidgetUtils.showWidget(this, "lblNoCriteriaSelected", true, null);
			
			//Clear child fields
			var scrChild = this._allChildScreens[0];
			
			var quantityTextbox = scrChild.getWidgetByUId("quantity");
				quantityTextbox.reset();
			var cmbDispReasonFilterbox = scrChild.getWidgetByUId("cmbDispReason");
				cmbDispReasonFilterbox.reset();
			var cmbReturnReasonFilterbox = scrChild.getWidgetByUId("cmbReturnReason");
				cmbReturnReasonFilterbox.reset();
			var itemDescLabel = scrChild.getWidgetByUId("itemDesc");
				itemDescLabel.set('value',"");
			_scWidgetUtils.hideWidget(scrChild, "addItemPanelMainContainer", false);
			
								
			}
			var strOrderType = modelOutput.Order.OrderType;
			var inputCommonCode = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
			_scModelUtils.setStringValueAtModelPath("CommonCode.CallingOrganizationCode", modelOutput.Order.EnterpriseCode, inputCommonCode);
				if(!(_scBaseUtils.isVoid(strOrderType)) && _scBaseUtils.equals('WHOLESALE',strOrderType)){
					_scModelUtils.setStringValueAtModelPath("CommonCode.OrderHeaderKey", modelOutput.Order.OrderLines.OrderLine[0].OrderHeaderKey, inputCommonCode);
					_isccsUIUtils.callApi(this, inputCommonCode, "getReturnReasonCodesForWholesale", null);
				}
				else{
					_isccsUIUtils.callApi(this, inputCommonCode, "getInnerReturnReasonList", null);
				}
		}
                }
            }
               else if (_scBaseUtils.equals(mashupRefId, "getReturnReasonCodesForWholesale") || _scBaseUtils.equals(mashupRefId, "getInnerReturnReasonList") ) {
            	console.log("This is in HandleMashup of getReturnReasonCodesForWholesale",modelOutput);
				_scScreenUtils.setModel(this, "getReturnReasonList_output", modelOutput, null);
			}
        },
		
        onSingleRowSelect: function(event, bEvent, ctrl, args) {
            if (_scBaseUtils.isBooleanTrue(false)) {
				console.log("This is in onsingleRowselect");
                _scGridxUtils.selectRowUsingUId(this, "OLST_listGrid", _isccsOrderUtils.getCurrentRowIndex(this));
                _scGridxUtils.refreshGridUsingUId(this, "OLST_listGrid");
            } else {
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
                    selectedTargetModel = _scGridxUtils.getSelectedTargetRecordsUsingUId(this, "OLST_listGrid");
                    var selectedTargetList = null;
                    selectedTargetList = _scModelUtils.getModelListFromPath("OrderLines.OrderLine", selectedTargetModel);
                    if (!(_scBaseUtils.isVoid(selectedTargetList))) {
                        orderline = _scBaseUtils.mergeModel(orderline, _scModelUtils.getModelFromList(selectedTargetList, 0), true);
                    }
                    var model = null;
                    model = _scBaseUtils.getNewModelInstance();
                    _scModelUtils.addModelObjectAsChildToModelObject("OrderLine", orderline, model);
					
					
                    this.onSingleRowSelectCustom(rowIndex);
                    _scScreenUtils.setModel(this, "selectedOrderLine", model, null);
					var scrChild = this._allChildScreens[0];
					
					_scWidgetUtils.setWidgetMandatory(scrChild, "quantity");
					_scWidgetUtils.showWidget(scrChild, "addItemPanelMainContainer", true, null);
					
					// Clear text boxes
					var quantityTextbox = scrChild.getWidgetByUId("quantity");
					quantityTextbox.reset();
					var cmbDispReasonFilterbox = scrChild.getWidgetByUId("cmbDispReason");
					cmbDispReasonFilterbox.reset();
					var cmbReturnReasonFilterbox = scrChild.getWidgetByUId("cmbReturnReason");
					cmbReturnReasonFilterbox.reset();
						
                }
            }
        },
        onSingleRowSelectCustom: function(rowSelected) {
            _scWidgetUtils.hideWidget(this, "pnlNoReturnLines", false);
            _scWidgetUtils.showWidget(this, "pnlReturnOrderLines", true, null);
        },
        
        LST_executeApi: function(event, bEvent, ctrl, args) {
            var scr = null;
            var inputData = null;
            scr = _scEventUtils.getScreenFromEventArguments(args);
            inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
            _scEventUtils.fireEventInsideScreen(this, "addExtraHandlers", null, args);
            _scScreenUtils.setModel(this, "LST_listAPIInput", inputData, null);
            
			//_scGridxUtils.refreshGridUsingUId(this, "OLST_listGrid");
			
			_isccsUIUtils.callApi(
                scr, inputData, "getCompleteOrderLineList", null);
			
        },

		setInitialized: function(
        event, bEvent, ctrl, args) {
            this.isScreeninitialized = true;
        },

        handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
		_isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        }
    });
});