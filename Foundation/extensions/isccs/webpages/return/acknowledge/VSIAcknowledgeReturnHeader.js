/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/lang","dojo/text!./templates/VSIAcknowledgeReturnHeader.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!isccs/utils/EventUtils", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/common/orderTotal/OrderTotalPanelInitController", "scbase/loader!extn/return/acknowledge/VSIAcknowledgeReturnOrderLinesInitController", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/ReturnUtils",  "scbase/loader!isccs/utils/ModelUtils","scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/ControllerWidget", "scbase/loader!sc/plat/dojo/widgets/DataLabel", "scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget", "scbase/loader!sc/plat/dojo/widgets/Link", "scbase/loader!sc/plat/dojo/widgets/Screen",
          "scbase/loader!sc/plat/dojo/info/ApplicationInfo","scbase/loader!sc/plat/dojo/utils/PlatformUIFmkImplUtils"], function(
dLang,templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _isccsEventUtils, _idxContentPane, _isccsOrderTotalPanelInitController, _isccsReturnOrderLinesInitController, _isccsBaseTemplateUtils, _isccsOrderUtils, _isccsReturnUtils,_isccsModelUtils, _isccsUIUtils, _scGridxUtils, _scplat, _scButtonDataBinder, _scCurrencyDataBinder, _scBaseUtils, _scControllerUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _scControllerWidget, _scDataLabel, _scIdentifierControllerWidget, _scLink, _scScreen, _scControllerWidget, _scEditorUtils, 
_scApplicationInfo,_scPlatformUIFmkImplUtils) {
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnHeader", [_scScreen], {
        templateString: templateText,
        postMixInProperties: function() {
            if (this.getScreenMode() != "default") {
                var origArgs = arguments;
                var htmlName = "templates/VSIAcknowledgeReturnHeader_" + this.getScreenMode() + ".html";
                this.templateString = dojo.cache("extn.return.acknowledge", htmlName);
                var modeUIJSClassString = "extn.return.acknowledge.VSIAcknowledgeReturnHeader_" + this.getScreenMode();
                var that = this;
               
            }
        },
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.return.acknowledge.templates", "VSIAcknowledgeReturnHeader.html"),
            shared: true
        },
        uId: "vsiAcknowledgeReturnHeader",
        packageName: "extn.return.acknowledge",
        className: "VSIAcknowledgeReturnHeader",
        extensible: true,
        title: "",
        screen_description: "This screen is used to add more lines to an existing return order.",
        namespaces: {
            targetBindingNamespaces: [
		
			{
				value: 'getAdvancedOrderList_input',
                description: "The search criteria provided to the return list screen.                 "
            },{
                description: 'This is the target namespace for the return receivng node data.',
                value: 'acknowledgeReturn_getReturnDCList_input'
            }
			],
            sourceBindingNamespaces: [
			
			{
                description: 'This is the initial screen input',
                value: 'screenInput'
            }, {
                description: 'These are the return Disposition reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnDispositionList_output'
            }, {
                description: 'These are the return reason codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'getReturnReasonList_output'
            }, {
                description: 'These are the return DC codes used in the VSIAcknowledgeReturnArticleDisposition screen.',
                value: 'acknowledgeReturn_getReturnDCList_output'
            }
			]
        },
        staticBindings: [
		
		],
        
        showRelatedTask: false,
        nextCheck: '',
        isDirtyCheckRequired: false,
        hotKeys: [],
        events: [
		
		{
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }],
        subscribers: {
            local: [
				{
                eventId: 'afterScreenInit',
                sequence: '50',
                description: 'Subscriber for after screen is initialized',
                handler: {
                    methodName: "setInitialized"
                }
            },	{
                eventId: 'txt_orderReturnNo2_onKeyUp',
                sequence: '25',
                listeningControlUId: 'txt_orderReturnNo2',
                handler: {
                    methodName: "SST_invokeApiOnEnter"
                }
            }, {
                eventId: 'SST_SearchButton_onClick',
                sequence: '25',
                description: '',
                listeningControlUId: 'SST_SearchButton',
                handler: {
                    methodName: "SST_invokeApi",
                    description: ""
                }
            }
			
			],
        },
		
		
		setInitialized: function(
        event, bEvent, ctrl, args) {
		
		var platUIUtils = dLang.getObject("dojo.utils.PlatformUIFmkImplUtils", true,
					_scplat);
		//console.log(platUIUtils);
			//_scWidgetUtils.setWidgetMandatory(this, "txt_orderReturnNo2");
			 var orgObject = null;
             orgObject = platUIUtils.getUserOrganization();
             //console.log('organization is',orgObject.Organization.OrganizationName);
			_scWidgetUtils.setValue(this, "txtWarehouseDC",orgObject.Organization.OrganizationName, true );
			that = this;
			setTimeout(function()
			{
				that.getWidgetByUId("txt_orderReturnNo2").focus();
			}, 1);
			this.isScreeninitialized = true;
        },
		receiveNodeOnChange: function() {
		
			if (_isccsEventUtils.isEnterPressed(event)) {
                this.SST_search();
            }
			_scWidgetUtils.setFocusOnWidgetUsingUid(this, "SST_SearchButton");
		},
		
		SST_invokeApiOnEnter: function(
        event, bEvent, ctrl, args) {
            if (_isccsEventUtils.isEnterPressed(event)) {
                this.SST_search();
            }
        },
		
		SST_invokeApi: function(
        event, bEvent, ctrl, args) {
            this.SST_search();
        },
		
		SST_search: function() {
            var targetModel = null;
			targetModel = _scBaseUtils.getTargetModel(this, this.SST_getSearchNamespace(), null);
			var orderNo = _scModelUtils.getStringValueFromPath("Order.OrderNo", targetModel);
			
			var receiveTargetModel = _scBaseUtils.getTargetModel(this, "acknowledgeReturn_getReturnDCList_input", null);
			//var receivingNode = _scModelUtils.getStringValueFromPath("Order.ReceivingNode", receiveTargetModel);
			
			if (_scBaseUtils.isVoid(orderNo)) {
			
			// Clear Grid
			var scrChild = this._allChildScreens[0];
			_scWidgetUtils.hideWidget(scrChild, "previewPanel", false);
			_scWidgetUtils.hideWidget(scrChild, "OLST_listGrid", false);
			_scWidgetUtils.hideWidget(scrChild, "pnlNoReturnLines", false);
			_isccsBaseTemplateUtils.showMessage(this, "Please provide Return / Sales Order number to continue", "error", null);
			
			} else {
			
            var eventDefn = null;
            var args = null;
            eventDefn = {};
            args = {};
            _scBaseUtils.setAttributeValue("inputData", targetModel, args);
            _scBaseUtils.setAttributeValue("argumentList", args, eventDefn);
            
			var scrChild = this._allChildScreens[0];
			
			_scWidgetUtils.showWidget(scrChild, "previewPanel", true, null);
			_scWidgetUtils.showWidget(scrChild, "OLST_listGrid", true, null);
			
			_scEventUtils.fireEventToChild(
            this, "returnOrderlinelistscreen", "callListApi", eventDefn);
			
			}
        },
		
		SST_getSearchNamespace: function() {
            var targetNamespace = "getAdvancedOrderList_input";
					
            return targetNamespace;
        }
		
    });
});