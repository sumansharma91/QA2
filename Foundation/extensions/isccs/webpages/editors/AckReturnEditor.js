/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/AckReturnEditor.html", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/EditorRelatedTaskUtils", "scbase/loader!isccs/utils/EditorScreenUtils", "scbase/loader!isccs/utils/ItemUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder", "scbase/loader!sc/plat/dojo/binding/ImageDataBinder", "scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/widgets/Editor", "scbase/loader!sc/plat/dojo/widgets/Image", "scbase/loader!sc/plat/dojo/widgets/Label", "scbase/loader!sc/plat/dojo/widgets/Link"], function(
templateText, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxContentPane, _isccsBaseTemplateUtils, _isccsEditorRelatedTaskUtils, _isccsEditorScreenUtils, _isccsItemUtils, _isccsUIUtils, _isccsWidgetUtils, _scplat, _scCurrencyDataBinder, _scImageDataBinder, _scAdvancedTableLayout, _scBaseUtils, _scControllerUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _scEditor, _scImage, _scLabel, _scLink) {
    return _dojodeclare("extn.editors.AckReturnEditor", [_scEditor], {
        templateString: templateText,
        uId: "AckReturnEditor",
        packageName: "extn.editors",
        className: "AckReturnEditor",
        title: "AcknowledgeReturn",
        namespaces: {
            targetBindingNamespaces: [],
            sourceBindingNamespaces: [{
                value: 'InitialEditorInput',
                description: "Holds the initial editor input passed into the editor on open."
            }, {
                value: 'getOrganizationList_output',
                description: "Holds a list of organizations used to create a new order."
            }, {
                value: 'getEnterpriseList',
                description: "Holds a list of enterprises the user can access."
            }, {
                value: 'enterpriseContext',
                description: "Used to display the enterprise context information."
            }]
        },
        isRTscreenLoaded: 'null',
        newScreenData: null,
        isRTscreeninitialized: 'null',
        isWizardinitialized: 'null',
        comparisonAttributes: [],
        hotKeys: [],
        events: [{
            name: 'setSystemMessage'
        }, {
            name: 'resizeEditor'
        }, {
            name: 'setEditorInput'
        }, {
            name: 'setScreenTitle'
        }, {
            name: 'createNewOrder'
        }, {
            name: 'createNewConsumer'
        }, {
            name: 'createNewBusiness'
        }, {
            name: 'beforeEditorClosed'
        }, {
            name: 'showOrHideRelatedTask'
        }, {
            name: 'afterRTScreenStartup'
        }],
        subscribers: {
            local: [{
                eventId: 'closeSystemMessage_onClick',
                sequence: '25',
                handler: {
                    methodName: "closeMessagePanel",
                    className: "BaseTemplateUtils",
                    packageName: "isccs.utils"
                }
            }, {
                eventId: 'setEditorInput',
                sequence: '25',
                handler: {
                    methodName: "setEditorInput"
                }
            }, {
                eventId: 'setScreenTitle',
                sequence: '25',
                handler: {
                    methodName: "setScreenTitle",
                    description: ""
                }
            }, {
                eventId: 'showOrHideRelatedTask',
                sequence: '25',
                handler: {
                    methodName: "showOrHideRelTask",
                    description: ""
                }
            }, {
                eventId: 'afterRTScreenStartup',
                sequence: '25',
                handler: {
                    methodName: "showRelatedTaskScreenHolder",
                    description: ""
                }
            }, {
                eventId: 'beforeEditorClosed',
                sequence: '25',
                handler: {
                    methodName: "handleEditorClose",
                    className: "UIUtils",
                    packageName: "isccs.utils",
                    description: ""
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '25',
                handler: {
                    methodName: "onScreenInit"
                }
            }, {
                eventId: 'linkclose_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'linkclose',
                handler: {
                    methodName: "closeCustomerMessagePanel",
                    className: "BaseTemplateUtils",
                    packageName: "isccs.utils"
                }
            }, {
                eventId: 'AcknowledgeReturnLink_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'AcknowledgeReturnLink',
                handler: {
                    methodName: "openAcknowledgeReturn"
                }
            }]
        },
        showRelatedTaskScreenHolder: function(
        event, bEvent, ctrl, args) {
            _isccsEditorRelatedTaskUtils.showRelatedTaskScreenHolder(
            this);
            this.initRelatedTasks(
            event, bEvent, ctrl, args);
        },
        showOrHideRelTask: function(
        event, bEvent, ctrl, args) {
            _isccsEditorRelatedTaskUtils.showOrHideRelatedTaskScreen(
            this);
        },
        onScreenInit: function(
        event, bEvent, ctrl, args) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            this.setInitialEditorInput(
            screenInput);
            _isccsUIUtils.getEnterpriseList(
            this);
        },
		
		openAcknowledgeReturn: function(){
			//var currentEditor = scEditorUtils.getCurrentEditor();
			//var rtScreen=scScreenUtils.getChildScreen(currentEditor,"RelatedTaskScreenHolder");
			//isccsUIUtils.openWizardInEditor("isccs.editors.wizards.homeEditorRT.HomeEditorRTWizard", {}, "isccs.editors.BlankEditor", currentEditor);
			scControllerUtils.openScreenInEditor("isccs.editors.HomeEditorRT", {}, null, {}, {}, "isccs.editors.AckReturnEditor");

		},
		
		
        setInitialEditorInput: function(
        screenInput) {
            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getModelObjectFromPath("", screenInput)))) {
                //_scWidgetUtils.showWidget(
                //this, "pnlCustomerContext", false, null);
            }
            _scScreenUtils.setModel(
            this, "InitialEditorInput", screenInput, null);
        },
        setScreenTitle: function(
        event, bEvent, ctrl, args) {
            _isccsBaseTemplateUtils.updateScreenTitleOnEditor(
            this, bEvent);
        },
        getScreenTitle: function() {
            var returnVal = "";
            returnVal = _scScreenUtils.getString(
            this, "Acknowledge Return");
            return returnVal;
        },
        beforeOpenScreenInEditor: function(
        data) {
            this.newScreenData = data;
            _isccsUIUtils.openScreenInEditorAfterCheck(
            data);
        },
        handleResponseForReplaceScreen: function(
        res, args) {
            if (
            _scBaseUtils.equals(
            res, "Ok")) {
                _scScreenUtils.clearScreen(
                _isccsUIUtils.getCurrentWizardInstance(
                this), null);
                if (
                _scBaseUtils.isVoid(
                args)) {
                    _scControllerUtils.continueOpeningInEditor(
                    this.newScreenData);
                } else {
                    _scControllerUtils.continueOpeningInEditor(
                    args);
                }
            }
        },
        setEditorInput: function(
        event, bEvent, ctrl, args) {
            var model = null;
            model = _scBaseUtils.getAttributeValue("model", false, args);
            if (!(
            _scBaseUtils.isVoid(
            model))) {
                var wiz = null;
                _scScreenUtils.setInitialInputData(
                this, model);
                this.setInitialEditorInput(
                model);
                var eventDefn = null;
                eventDefn = {};
                _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
                _isccsEditorScreenUtils.updateEditorTab(
                this);
                wiz = _isccsUIUtils.getCurrentWizardInstance(
                this);
                if (!(
                _scBaseUtils.isVoid(
                wiz))) {
                    _scEventUtils.fireEventInsideScreen(
                    wiz, "setWizardInput", eventDefn, args);
                }
                this.handleEnterpriseContextDisplay();
            }
        },
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext) {
		
		if (
            _scBaseUtils.equals(
            mashupRefId, "flushCache")) {
                if (
                _scModelUtils.getBooleanValueFromPath("Cache.IsSuccessful", modelOutput, false)) {
                    _isccsBaseTemplateUtils.showMessage(
                    this, "System_Cache_Flushed", "success", null);
                } else {
                    _isccsBaseTemplateUtils.showMessage(
                    this, "System_Cache_Failed", "error", null);
                }
            }
			
			if (
            _scBaseUtils.equals(
            mashupRefId, "getEnterpriseList")) {
                _scScreenUtils.setModel(
                this, "getEnterpriseList", modelOutput, null);
                this.handleEnterpriseContextDisplay();
            }
            
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _isccsBaseTemplateUtils.handleMashupCompletion(
            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
        
        
        
        
        initRelatedTasks: function(
        event, bEvent, ctrl, args) {},
        handleEnterpriseContextDisplay: function() {
            var screenInput = null;
            screenInput = _scScreenUtils.getModel(
            this, "InitialEditorInput");
            if (!(
            _isccsWidgetUtils.hideOrganizationWithOneResult(
            this, "pnlEnterpriseHolder", _scScreenUtils.getModel(
            this, "getEnterpriseList")))) {
                if (!(
                _isccsUIUtils.initContextEnterprise(
                this, "", screenInput))) {
                    _scWidgetUtils.hideWidget(
                    this, "pnlEnterpriseHolder");
                } else {
                    _scWidgetUtils.showWidget(
                    this, "pnlEnterpriseHolder", false, null);
                    _scScreenUtils.setModel(
                    this, "enterpriseContext", screenInput, null);
                }
            }
        } 
    });
});