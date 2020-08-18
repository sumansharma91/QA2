/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/VSIAcknowledgeReturnWizard.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/ReturnUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!isccs/utils/WizardUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!sc/plat/dojo/widgets/Wizard"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxContentPane, _isccsBaseTemplateUtils, _isccsOrderUtils, _isccsReturnUtils, _isccsUIUtils, _isccsWidgetUtils, _isccsWizardUtils, _scplat, _scButtonDataBinder, _scBaseUtils, _scControllerUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _scWizardUtils, _scWizard) {
    return _dojodeclare("extn.return.wizards.acknowledge.VSIAcknowledgeReturnWizard", [_scWizard], {
        templateString: templateText,
        uId: "acknowledge",
        packageName: "extn.return.wizards.acknowledge",
        className: "VSIAcknowledgeReturnWizard",
        extensible: true,
        title: "TITLE_AcknowledgeReturn",
        isSinglePageWizard: false,
        isOrderEntry: 'Y',
        ActionPerformed: '',
        showRelatedTaskInWizard: false,
        closeTab: true,
        isDirtyCheckRequired: true,
        namespaces: {
            targetBindingNamespaces: [
			{
                description: 'Model used to say when to go to Gift Options',
                value: 'ScreenTypeInput_input',
                alwaysPass: 'true'
            }, {
                description: 'Model used to say whether capturePayment API needs to be called or not',
                value: 'defaultPaymentMethod_Output',
                alwaysPass: 'true'
            }],
            sourceBindingNamespaces: [
			{
                description: 'This holds the current order in context',
                value: 'currentReceipt'
            }, {
                description: 'Tells the wizard to open the exchange order',
                value: 'openExchange_output'
            }, {
                description: 'Model used to set the target model',
                value: 'NoCustomer_output'
            }, {
                description: 'Model used to set item model to add to order.',
                value: 'addToOrder_input'
            }, {
                description: 'Model which contains the updated model',
                value: 'updatedReturnOrderModel'
            }, {
                description: 'This is to check that any of the orderLine is returned by the Gift Recipient.',
                value: 'giftRecipient_input'
            }, {
                description: 'Model used to say when to go to Gift Options',
                value: 'GoToGiftOptions_output'
            }, {
                description: 'Model used to hold initial input data',
                value: 'ScreenTypeInput'
            }, {
                description: 'Model used to hold data if returns is created by the customer',
                value: 'ifLineReturnedByCustomer'
            }, {
                description: 'Holds information if customer is skipped during return creation flow',
                value: 'SkipCustomer_output'
            }, {
                description: 'Model used to default payment method information',
                value: 'defaultPaymentMethod_Input'
            }]
        },
        staticBindings: [{
            targetBinding: {
                path: 'ScreenTypeInput.sScreenType',
                namespace: 'ScreenTypeInput_input'
            },
            sourceBinding: {
                path: 'ScreenTypeInput.sScreenType',
                namespace: 'ScreenTypeInput'
            }
        }, {
            targetBinding: {
                path: 'Default.PaymentMethod',
                namespace: 'defaultPaymentMethod_Output'
            },
            sourceBinding: {
                path: 'Default.PaymentMethod',
                namespace: 'defaultPaymentMethod_Input'
            }
        }],
        hotKeys: [{
            id: "closebtn",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "closeBttn2",
            invocationContext: "Editor",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }, {
            id: "closebtn",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "closeBttn",
            invocationContext: "Editor",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
        events: [{
            name: 'onSaveSuccess'
        }, {
            name: 'showNextPage'
        }, {
            name: 'saveSuccessOnConfirm'
        }, {
            name: 'setScreenTitle'
        }, {
            name: 'handleTabClose'
        }, {
            name: 'showNotesButton'
        }, {
            name: 'hidePreviousButtons'
        }, {
            name: 'showPreviousButtons'
        }, {
            name: 'enableNextButton'
        }, {
            name: 'disableNextButton'
        }, {
            name: 'setWizardModel'
        }, {
            name: 'setWizardInput'
        }, {
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }],
        subscribers: {
            local: [{
                eventId: 'addNoteBtn2_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "onAddNotes",
                    description: ""
                }
            }, {
                eventId: 'addNoteBtn_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "onAddNotes",
                    description: ""
                }
            }, {
                eventId: 'nextBttn2_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleNext",
                    description: ""
                }
            }, {
                eventId: 'nextBttn_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleNext",
                    description: ""
                }
            }, {
                eventId: 'showNextPage',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "showNextPage",
                    description: ""
                }
            }, {
                eventId: 'prevBttn2_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handlePrevious",
                    description: ""
                }
            }, {
                eventId: 'prevBttn_onClick',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handlePrevious",
                    description: ""
                }
            }, {
                eventId: 'internalbeforenext',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleBeforeNext",
                    description: ""
                }
            }, {
                eventId: 'confirmBttn2_onClick',
                sequence: '25',
                handler: {
                    methodName: "handleConfirm",
                    description: ""
                }
            }, {
                eventId: 'confirmBttn_onClick',
                sequence: '25',
                handler: {
                    methodName: "handleConfirm",
                    description: ""
                }
            }, {
                eventId: 'closeBttn2_onClick',
                sequence: '25',
                handler: {
                    methodName: "handleClose",
                    description: ""
                }
            }, {
                eventId: 'closeBttn_onClick',
                sequence: '25',
                handler: {
                    methodName: "handleClose",
                    description: ""
                }
            }, {
                eventId: 'afterconfirm',
                sequence: '25',
                handler: {
                    methodName: "afterWizardConfirm",
                    description: ""
                }
            }, {
                eventId: 'screenchanged',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleScreenChanged",
                    description: ""
                }
            }, {
                eventId: 'onSaveSuccess',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "onSaveSuccess",
                    description: ""
                }
            }, {
                eventId: 'setScreenTitle',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "setScreenTitle",
                    description: ""
                }
            }, {
                eventId: 'saveSuccessOnConfirm',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "afterSaveSuccessOnConfirm",
                    description: ""
                }
            }, {
                eventId: 'handleTabClose',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleTabClose",
                    description: ""
                }
            }, {
                eventId: 'start',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleRelatedTasks",
                    description: ""
                }
            }, {
                eventId: 'start',
                sequence: '26',
                description: '',
                handler: {
                    methodName: "onStartWizard",
                    description: ""
                }
            }, {
                eventId: 'previous',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "afterPrevious",
                    description: ""
                }
            }, {
                eventId: 'cancelnext',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "aftercancelnext",
                    description: ""
                }
            }, {
                eventId: 'hidePreviousButtons',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleHidePrevious",
                    description: ""
                }
            }, {
                eventId: 'showPreviousButtons',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handleShowPrevious",
                    description: ""
                }
            }, {
                eventId: 'showNotesButton',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "showNotesButton",
                    description: ""
                }
            }, {
                eventId: 'enableNextButton',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "enableNextButton",
                    description: ""
                }
            }, {
                eventId: 'disableNextButton',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "disableNextButton",
                    description: ""
                }
            }, {
                eventId: 'setWizardModel',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "setWizardModel",
                    className: "WizardUtils",
                    packageName: "isccs.utils",
                    description: "to do set model"
                }
            }, {
                eventId: 'start',
                sequence: '30',
                handler: {
                    methodName: "onStart"
                }
            }, {
                eventId: 'setWizardInput',
                sequence: '30',
                handler: {
                    methodName: "updateEditorInput"
                }
            }, {
                eventId: 'addItemToOrder',
                sequence: '30',
                handler: {
                    methodName: "handleAddItemToOrder",
                    className: "OrderUtils",
                    packageName: "isccs.utils"
                }
            }, {
                eventId: 'resetAddToOrderInput',
                sequence: '30',
                handler: {
                    methodName: "resetAddToOrderInput",
                    className: "OrderUtils",
                    packageName: "isccs.utils"
                }
            }, {
                eventId: 'screenchanged',
                sequence: '50',
                description: '',
                handler: {
                    methodName: "resetWizardModels",
                    description: ""
                }
            }, {
                eventId: 'saveCurrentPage',
                sequence: '25',
                handler: {
                    methodName: "save"
                }
            }],
        },
        
        resetWizardModels: function(uiEvent, businessEvent, control, args) {
            var input = null;
            input = _scModelUtils.createNewModelObjectWithRootKey("Order");
            _scModelUtils.setStringValueAtModelPath("Order.Action", "", input);
            var sScreenType = null;
            sScreenType = _scWizardUtils.getCurrentPageScreenType(this);
            var screenTypeInput = null;
            screenTypeInput = _scModelUtils.createNewModelObjectWithRootKey("ScreenTypeInput");
            _scModelUtils.setStringValueAtModelPath("ScreenTypeInput.sScreenType", sScreenType, screenTypeInput);
            _scScreenUtils.setModel(this, "ScreenTypeInput", screenTypeInput, null);
        },
        
        handleInfoMsgConfirmation: function(uiEvent, businessEvent, control, args) {},
        onPreviousDialogCallback: function(actionPerformed, model, popupParams) {
            var currentPage = null;
            var parentScreenAction = null;
            this.parentScreenAction = _scBaseUtils.getValueFromPath("screenInput.parentScreenAction", popupParams);
            currentPage = _scWizardUtils.getCurrentPageByUId(this);
            if (_scBaseUtils.equals(actionPerformed, "SAVEONPREVIOUS")) {
                this.sendEventForSavePage("SAVEONPREVIOUS");
            }
            if (_scBaseUtils.equals(actionPerformed, "ADDTOORDER")) {
                this.sendEventForSavePage("ADDTOORDER");
            }
            if (_scBaseUtils.equals(actionPerformed, "DISCARD")) {
                if (_scBaseUtils.equals(this.parentScreenAction, "addToOrder")) {
                    this.parentScreenAction = "";
                    _scWizardUtils.showPreviousPage(this, "AddItems");
                } else {
                    this.parentScreenAction = "";
                    _scWizardUtils.previousScreen(this);
                }
            }
            if (_scBaseUtils.equals(actionPerformed, "CANCEL")) {
                if (_scBaseUtils.equals(this.parentScreenAction, "addToOrder")) {
                    var eventDefn = null;
                    var args = null;
                    args = _scBaseUtils.getNewBeanInstance();
                    eventDefn = _scBaseUtils.getNewBeanInstance();
                    _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
                    _scEventUtils.fireEventInsideScreen(this, "resetAddToOrderInput", eventDefn, args);
                }
            }
        },
        onSaveSuccess: function(uiEvent, businessEvent, control, args) {
            if (_scBaseUtils.equals(this.ActionPerformed, "NEXT")) {
                _scWizardUtils.nextScreen(this);
                this.ActionPerformed = "";
            }
            if (_scBaseUtils.equals(this.ActionPerformed, "CONFIRM")) {
                _scWizardUtils.confirmWizard(this);
                this.ActionPerformed = "";
            }
            if (_scBaseUtils.equals(this.ActionPerformed, "CLOSE")) {
                _scWizardUtils.closeWizard(this);
                this.ActionPerformed = "";
            }
            if (_scBaseUtils.equals(this.ActionPerformed, "ADDTOORDER")) {
                this.parentScreenAction = "";
                _scWizardUtils.showPreviousPage(this, "AddItems");
            }
            if (_scBaseUtils.equals(this.ActionPerformed, "SAVEONPREVIOUS")) {
                _scWizardUtils.previousScreen(this);
                this.ActionPerformed = "";
                this.parentScreenAction = "";
            }
        },
        customError: function(screen, data, code) {
            if (_scBaseUtils.equals(code, "YFS10003")) {
                return true;
            }
            return false;
        },
        onStart: function(uiEvent, businessEvent, control, args) {
            
			
        },
        afterWizardConfirm: function(uiEvent, businessEvent, control, args) {
			_isccsWizardUtils.handleWizardCloseConfirmation(this, "Ok", args);            
        },
        updateEditorInput: function(uiEvent, businessEvent, control, args) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(this);
        },
        
        handleCustomPrevious: function(uiEvent, businessEvent, control, args) {
            var ScreenID = null;
            ScreenID = _scWizardUtils.getCurrentPageByUId(this);
            if (_scBaseUtils.equals("VSIAcknowledgeReturnHeader", ScreenID)) {
                var isPageDirty = false;
                _isccsBaseTemplateUtils.hideMessage(this);
                isPageDirty = _isccsWidgetUtils.isWizardPageDirty(this);
                if (_scBaseUtils.isBooleanTrue(isPageDirty)) {
                    var emptyObject = null;
                    emptyObject = _scBaseUtils.getNewBeanInstance();
                    var popupParams = null;
                    popupParams = _scBaseUtils.getNewBeanInstance();
                    _scBaseUtils.setAttributeValue("Screen", null, popupParams);
                    _scBaseUtils.setAttributeValue("screenId", "isccs.order.create.orderEntryDialog.DirtyCheckDialog", popupParams);
                    var dialogParams = null;
                    dialogParams = _scBaseUtils.getNewBeanInstance();
                    _scBaseUtils.setAttributeValue("closeCallBackHandler", "onLocalPreviousDialogCallback", dialogParams);
                    _isccsUIUtils.openSimplePopup("isccs.order.create.orderEntryDialog.OnPreviousDialog", "Save", this, popupParams, dialogParams);
                } else {
                    this.localPreviousAction(uiEvent, businessEvent, control, args, false);
                }
                return true;
            }
            return false;
        },
        onLocalPreviousDialogCallback: function(actionPerformed, model, popupParams) {
            if (_scBaseUtils.equals(actionPerformed, "SAVEONPREVIOUS")) {
                this.ActionPerformed = "PREVIOUS";
                this.sendEventForSavePage("SAVEONPREVIOUS");
            } else if (_scBaseUtils.equals(actionPerformed, "DISCARD")) {
                this.localPreviousAction(null, null, null, null, true);
            }
        },
        localPreviousAction: function(uiEvent, businessEvent, control, args, updateReturnLines) {
            var ScreenID = null;
            ScreenID = _scWizardUtils.getCurrentPageByUId(this);
            var eventDefn = null;
            eventDefn = _scBaseUtils.getNewBeanInstance();
            var eventDetails = null;
            eventDetails = _scBaseUtils.getNewBeanInstance();
            if (_scBaseUtils.isBooleanTrue(updateReturnLines)) {
                _scBaseUtils.setAttributeValue("updateReturnLinesRequired", "Y", eventDetails);
            }
            _scBaseUtils.setAttributeValue("argumentList", eventDetails, eventDefn);
            _scEventUtils.fireEventToChild(this, ScreenID, "toggleReturnLineView", eventDefn);
            this.handleHidePrevious(uiEvent, businessEvent, control, args);
        },
        sendEventForSavePage: function(actionPerformed) {
            var wizscreen = null;
            var wizscreenUId = null;
            var isScreenValid = null;
            this.ActionPerformed = actionPerformed;
            wizscreen = _scWizardUtils.getCurrentPage(this);
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(this);
            var tempModel = null;
            tempModel = _scBaseUtils.getNewBeanInstance();
            var eventArg = null;
            eventArg = _scBaseUtils.getNewBeanInstance();
            if (_scBaseUtils.equals(wizscreenUId, "vsiAcknowledgeReturnHeader")) {
                this.enableDisableHiddenFields(wizscreen, "disable");
            }
            if (isScreenValid = _scScreenUtils.validate(wizscreen)) {
                if (_scBaseUtils.equals(wizscreenUId, "vsiAcknowledgeReturnHeader")) {
                    this.enableDisableHiddenFields(wizscreen, "enable");
                }
                if (_scBaseUtils.equals(this.ActionPerformed, "NEXT")) {
                    _scBaseUtils.addStringValueToBean("Action", "NEXT", tempModel);
                } else if (_scBaseUtils.equals(this.ActionPerformed, "SAVEONPREVIOUS")) {
                    _scBaseUtils.addStringValueToBean("Action", "SAVEONPREVIOUS", tempModel);
                } else if (_scBaseUtils.equals(this.ActionPerformed, "CONFIRM")) {
                    _scBaseUtils.addStringValueToBean("Action", "CONFIRM", tempModel);
                }
                _scBaseUtils.addBeanValueToBean("argumentList", tempModel, eventArg);
                _scEventUtils.fireEventToChild(this, wizscreenUId, "saveCurrentPage", eventArg);
            } else {
                _isccsBaseTemplateUtils.showMessage(this, "Message_screenHasErrors", "error", null);
                if (_scBaseUtils.equals(wizscreenUId, "vsiAcknowledgeReturnHeader")) {
                    this.enableDisableHiddenFields(wizscreen, "enable");
                }
            }
        },
        enableDisableHiddenFields: function(wizscreen, action) {
            var salesOrderLineListScr = null;
            salesOrderLineListScr = _scScreenUtils.getChildScreen(wizscreen, "salesOrderLineList");
            var returnOrderLineListScr = null;
            returnOrderLineListScr = _scScreenUtils.getChildScreen(wizscreen, "returnOrderLineList");
            var existingLineSearchScr = null;
            existingLineSearchScr = _scScreenUtils.getChildScreen(wizscreen, "existingLineSearch");
            if (!(_scWidgetUtils.isWidgetVisible(wizscreen, "pnlSalesOrderLinesForReturn"))) {
                if (!(_scBaseUtils.isVoid(salesOrderLineListScr))) {
                    this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(salesOrderLineListScr, "previewPanel"), action);
                }
            }
            if (!(_scWidgetUtils.isWidgetVisible(wizscreen, "pnlReturnLines"))) {
                if (!(_scBaseUtils.isVoid(returnOrderLineListScr))) {
                    this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(returnOrderLineListScr, "previewPanel"), action);
                }
            }
            if (!(_scBaseUtils.isVoid(existingLineSearchScr))) {
                if (!(_scWidgetUtils.isWidgetVisible(wizscreen, "pnlFindReturnLines"))) {
                    this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "previewPanel"), action);
                    this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "previewPanelList"), action);
                } else {
                    if (!(_scWidgetUtils.isWidgetVisible(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "pnlOrderLineHolder"))) {
                        this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "previewPanel"), action);
                    }
                    if (!(_scWidgetUtils.isWidgetVisible(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "pnlOrderLineListHolder"))) {
                        this.enableDisableFieldsForExistingLine(_scScreenUtils.getChildScreen(_scScreenUtils.getChildScreen(existingLineSearchScr, "previewPanel"), "previewPanelList"), action);
                    }
                }
            }
        },
        enableDisableFieldsForExistingLine: function(extLineScr, action) {
            if (_scBaseUtils.equals(action, "enable")) {
                _scWidgetUtils.enableWidget(extLineScr, "quantity");
                _scWidgetUtils.enableWidget(extLineScr, "cmbReturnReason");
            } else {
                _scWidgetUtils.disableWidget(extLineScr, "quantity", false);
                _scWidgetUtils.disableWidget(extLineScr, "cmbReturnReason", false);
            }
        },
        handleClose: function(uiEvent, businessEvent, control, args) {
            this.ActionPerformed = "CLOSE";
            _isccsWizardUtils.handleWizardCloseConfirmation(this, "Ok", args);
            var returnObject = null;
            returnObject = _scBaseUtils.getNewBeanInstance();
            _isccsUIUtils.addBooleanValueToBean("closeEditor", false, returnObject);
            return returnObject;
        },
        handleStartReceipt: function(modelOutput) {
            var modelToAdd = null;
            var args = null;
            var currentPage = null;
            currentPage = _scWizardUtils.getCurrentPageByUId(this);
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(this);
            //screenInput = _scBaseUtils.mergeModel(screenInput, modelOutput, true);
            args = _scBaseUtils.getNewBeanInstance();
            modelToAdd = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.addModelValueToBean("model", modelOutput, modelToAdd);
            _scBaseUtils.addBeanValueToBean("argumentList", modelToAdd, args);
            //_scEventUtils.fireEventToParent(this, "setEditorInput", args);
            _scScreenUtils.setModel(this, "currentReceipt", modelOutput, null);
            _scScreenUtils.setInitialInputData(this, screenInput);
			if(!_scBaseUtils.isVoid(currentPage)){
				_scEventUtils.fireEventToChild(this, currentPage, "onReturnReceiptCreation", args);
			}
            
            this.updateScreenTitle(this, screenInput);
        },
        
        handleMashupOutput: function(mashupRefId, modelOutput, modelInput, mashupContext) {
            
			
        },
        previousDialogCallback: function(actionPerformed, model, popupParams) {
            var currentPage = null;
            currentPage = _scWizardUtils.getCurrentPageByUId(this);
            if (_scBaseUtils.equals(actionPerformed, "SAVE")) {
                this.ActionPerformed = "PREVIOUS";
                _scEventUtils.fireEventToChild(this, currentPage, "saveCurrentPage", null);
            }
            if (_scBaseUtils.equals(actionPerformed, "DISCARD")) {
                _scWizardUtils.previousScreen(this);
            }
            if (_scBaseUtils.equals(actionPerformed, "CANCEL")) {}
        },
        closeDialogCallback: function(actionPerformed, model, popupParams) {
            
			
			if (_scBaseUtils.equals(actionPerformed, "DISCARD-CHANGE")) {
                
				
				_scWizardUtils.closeWizard(this);
				
            } else if (_scBaseUtils.equals(actionPerformed, "CLOSE")) {
                _scWizardUtils.closeWizard(this);
            } else if (_scBaseUtils.equals(actionPerformed, "SAVE-ORDER")) {
                if (_isccsWidgetUtils.isWizardPageDirty(this)) {
                    var eventArg = null;
                    eventArg = _scBaseUtils.getNewBeanInstance();
                    var tempModel = null;
                    tempModel = _scBaseUtils.getNewBeanInstance();
                    _scBaseUtils.addStringValueToBean("Action", "CLOSE", tempModel);
                    _scBaseUtils.addModelValueToBean("Order", orderModel, tempModel);
                    _scBaseUtils.addBeanValueToBean("argumentList", tempModel, eventArg);
                    var currentScreen = null;
                    currentScreen = _scWizardUtils.getCurrentPage(this);
                    var currentPage = null;
                    currentPage = _scWizardUtils.getCurrentPageByUId(this);
                    _scEventUtils.fireEventToChild(this, currentPage, "saveCurrentPage", eventArg);
                } else {
                    
					
					_scWizardUtils.closeWizard(this);
					
					
                }
            }
        },
        
        updateScreenTitle: function(screen, mashupRefOutput) {
            var returnVal = null;
            var args = null;
            returnVal = _isccsReturnUtils.getTitleForReturnEditor(mashupRefOutput);
            args = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.setAttributeValue("title", returnVal, args);
            _scEventUtils.fireEventGlobally(this, "changeEditorTitle", null, args);            
        },
        
		handleScreenChanged: function(uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.handleScreenChanged(this, "");
            var ScreenID = null;
            ScreenID = _scWizardUtils.getCurrentPageByUId(this);
            if (_scBaseUtils.equals("vsiAcknowledgeReturnHeader", ScreenID)) {
                _scEventUtils.fireEventToChild(this, ScreenID, "onCustomReloadScreen", null);
            }
        },
        handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
        save: function(event, bEvent, ctrl, args) {
            var eventDefinition = null;
            eventDefinition = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.setAttributeValue("argumentList", args, eventDefinition);
            _scEventUtils.fireEventToParent(this, "onSaveSuccess", eventDefinition);
        },
        onAddNotes: function(uiEvent, businessEvent, control, args) {
            _scBaseUtils.addBeanValueToBean("argumentList", _scBaseUtils.getNewBeanInstance(), args);
            _scEventUtils.fireEventToParent(this, "openNote", args);
        },
        enableNextButton: function(uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.enableNavigationalWidget(this, "nextBttn");
        },
        disableNextButton: function(uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.disableNavigationalWidget(this, "nextBttn");
        },
        handleTabClose: function(uiEvent, businessEvent, control, args) {
            var isPageDirty = false;
            _scBaseUtils.setAttributeValue("argumentList.closeTab", true, args);
            var returnObject = null;
            returnObject = this.handleClose(uiEvent, businessEvent, control, args);
            return returnObject;
        },
        handleNext: function(uiEvent, businessEvent, control, args) {
            this.sendEventForSavePage("NEXT");
        },
        customEntityExists: function() {
            return false;
        },
        handleWizardCloseConfirmation: function(res, args) {
            var argumentList = null;
            var closeTab = false;
            argumentList = _scBaseUtils.getAttributeValue("argumentList", false, args);
            if (!(_scBaseUtils.isVoid(argumentList))) {
                closeTab = _scBaseUtils.getAttributeValue("closeTab", false, argumentList);
            }
            if (_scBaseUtils.or(this.customEntityExists(), closeTab)) {
                _scWizardUtils.closeWizard(this);
            } else {
                _scScreenUtils.clearScreen(this, null);
                _isccsWizardUtils.handleWizardCloseConfirmation(this, res, args);
            }
        },
        handleConfirm: function(uiEvent, businessEvent, control, args) {
            this.sendEventForSavePage("CONFIRM");
        },
        handlePrevious: function(uiEvent, businessEvent, control, args) {
            if (!(this.handleCustomPrevious(uiEvent, businessEvent, control, args))) {
                var isPageDirty = false;
                _isccsBaseTemplateUtils.hideMessage(this);
                isPageDirty = _isccsWidgetUtils.isWizardPageDirty(this);
                if (_scBaseUtils.isBooleanTrue(isPageDirty)) {
                    _isccsOrderUtils.openOrderEntryOnPreviousPopup(this, "handlePrevious");
                } else {
                    _scWizardUtils.previousScreen(this);
                }
            }
        },
        showNextPage: function(uiEvent, businessEvent, control, args) {
            _scWizardUtils.nextScreen(this);
        },
        handleBeforeNext: function(uiEvent, businessEvent, control, args) {
            _isccsBaseTemplateUtils.hideMessage(this);
        },
        afterPrevious: function(uiEvent, businessEvent, control, args) {
            var wizscreenUId = null;
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(this);
            _scEventUtils.fireEventToChild(this, wizscreenUId, "reloadScreen", null);
        },
        aftercancelnext: function(uiEvent, businessEvent, control, args) {
            var wizscreenUId = null;
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(this);
            _scEventUtils.fireEventToChild(this, wizscreenUId, "reloadScreen", null);
        },
        afterSaveSuccessOnConfirm: function(uiEvent, businessEvent, control, args) {
            _scWizardUtils.confirmWizard(this);
        },
        setScreenTitle: function(event, bEvent, ctrl, args) {
            var argumentList = null;
            argumentList = _scBaseUtils.getAttributeValue("argumentList", false, bEvent);
            var inputData = null;
            inputData = _scBaseUtils.getAttributeValue("inputData", false, argumentList);
            var subTitle = null;
            subTitle = _scModelUtils.getStringValueFromPath("ScreenTitle", inputData);
            _isccsBaseTemplateUtils.updateTitle(this, "TITLE_AcknowledgeReturn", subTitle);
        },
        handleShowPrevious: function(event, bEvent, ctrl, args) {
            _isccsWizardUtils.showNavigationalWidget(this, "prevBttn", false, null);
        },
        handleHidePrevious: function(event, bEvent, ctrl, args) {
            _isccsWizardUtils.hideNavigationalWidget(this, "prevBttn", false);
        },
        handleRelatedTasks: function(uiEvent, businessEvent, control, args) {
            var eventDefn = null;
            eventDefn = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.setAttributeValue("argumentList.isWizardinitialized", "true", eventDefn);
            _scEventUtils.fireEventToParent(this, "showOrHideRelatedTask", eventDefn);
        },
        onStartWizard: function(uiEvent, businessEvent, control, args) {
            
			
        },
        handleDisplayOfCloseButton: function(nodeName) {
            var msg = null;
            if (_scBaseUtils.isBooleanTrue(this.showBackButton)) {
                if (_scBaseUtils.equals(nodeName, "Order")) {
                    msg = _isccsWizardUtils.getGoBackButtonText(this);
                    _isccsWizardUtils.setLabelOnNavigationalWidget(this, "closeBttn", msg);
                    _isccsWizardUtils.removeClassToNavigationalWidget(this, "closeBttn", "idxSecondaryButton");
                }
            }
        },
        showNotesButton: function(uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.showNavigationalWidget(this, "addNoteBtn", false, null);
        }
    });
});