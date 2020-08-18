/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/GiftCardFulfillmentWizard.html", "scbase/loader!dijit/form/Button", "scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/_base/lang", "scbase/loader!dojo/text", "scbase/loader!idx/layout/ContentPane", "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/ContextUtils", "scbase/loader!isccs/utils/MobileUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!isccs/utils/WizardUtils", "scbase/loader!sc/plat", "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ControllerUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WizardUtils", "scbase/loader!sc/plat/dojo/widgets/Wizard"], function(
templateText, _dijitButton, _dojodeclare, _dojokernel, _dojolang, _dojotext, _idxContentPane, _isccsBaseTemplateUtils, _isccsContextUtils, _isccsMobileUtils, _isccsOrderUtils, _isccsUIUtils, _isccsWidgetUtils, _isccsWizardUtils, _scplat, _scButtonDataBinder, _scBaseUtils, _scControllerUtils, _scEventUtils, _scScreenUtils, _scWizardUtils, _scWizard) {
    return _dojodeclare("extn.order.wizards.giftCard.GiftCardFulfillmentWizardUI", [_scWizard], {
        templateString: templateText,
        uId: "GiftCardFulfillmentWizard",
        packageName: "extn.order.wizards.giftCard",
        className: "GiftCardFulfillmentWizard",
        title: "extn_Fulfill_Gift_Cards",
        isSinglePageWizard: false,
        showBackButton: true,
        showRelatedTaskInWizard: false,
        closeTab: false,
        isDirtyCheckRequired: true,
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
            name: 'saveCurrentPage'
        }, {
            name: 'reloadScreen'
        }],
        subscribers: {
            global: [{
                eventId: 'mobileBackButtonClicked',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "handlePrevious",
                    description: ""
                }
            }],
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
                eventId: 'showNextPage',
                sequence: '25',
                description: '',
                handler: {
                    methodName: "showNextPage",
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
                eventId: 'saveCurrentPage',
                sequence: '25',
                description: 'Subscriber for save current page event for wizard',
                handler: {
                    methodName: "save"
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '50',
                description: 'Subscriber for after screen is initialized',
                handler: {
                    methodName: "setInitialized"
                }
            }]
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _isccsBaseTemplateUtils.handleMashupCompletion(
            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
        setInitialized: function(
        event, bEvent, ctrl, args) {
            this.isScreeninitialized = true;
        },
        save: function(
        event, bEvent, ctrl, args) {
            var eventDefinition = null;
            eventDefinition = {};
            _scBaseUtils.setAttributeValue("argumentList", args, eventDefinition);
            _scEventUtils.fireEventToParent(
            this, "onSaveSuccess", eventDefinition);
        },
        onAddNotes: function(
        uiEvent, businessEvent, control, args) {
            args["argumentList"] = _scBaseUtils.getNewBeanInstance();
            _scEventUtils.fireEventToParent(
            this, "openNote", args);
        },
        enableNextButton: function(
        uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.enableNavigationalWidget(
            this, "nextBttn");
        },
        disableNextButton: function(
        uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.disableNavigationalWidget(
            this, "nextBttn");
        },
        handleTabClose: function(
        uiEvent, businessEvent, control, args) {
            var isPageDirty = false;
            _scBaseUtils.setAttributeValue("argumentList.closeTab", true, args);
            var returnObject = null;
            returnObject = this.handleClose(
            uiEvent, businessEvent, control, args);
            return returnObject;
        },
        handleNext: function(
        uiEvent, businessEvent, control, args) {
            this.sendEventForSavePage("NEXT");
        },
        handleClose: function(
        uiEvent, businessEvent, control, args) {
            var isPageDirty = false;
            var returnObject = null;
            isPageDirty = _isccsWidgetUtils.isWizardPageDirty(
            this);
            if (
            isPageDirty) {
                var msg = null;
                msg = _scScreenUtils.getString(
                this, "DirtyCloseConfirmationMessage");
                _scScreenUtils.showConfirmMessageBox(
                this, msg, "handleWizardCloseConfirmation", null, args);
                returnObject = {};
                returnObject["closeEditor"] = false;
                return returnObject;
            } else if (
            this.customEntityExists()) {
                _scWizardUtils.closeWizard(
                this);
            } else {
                _isccsWizardUtils.handleWizardCloseConfirmation(
                this, "Ok", args);
                returnObject = {};
                returnObject["closeEditor"] = false;
                return returnObject;
            }
        },
        customEntityExists: function() {
            return false;
        },
        handleWizardCloseConfirmation: function(
        res, args) {
            var argumentList = null;
            var closeTab = false;
            argumentList = _scBaseUtils.getAttributeValue("argumentList", false, args);
            if (!(
            _scBaseUtils.isVoid(
            argumentList))) {
                closeTab = _scBaseUtils.getAttributeValue("closeTab", false, argumentList);
            }
            if (
            this.customEntityExists() || closeTab) {
                _scWizardUtils.closeWizard(
                this);
            } else {
                _scScreenUtils.clearScreen(
                this, null);
                _isccsWizardUtils.handleWizardCloseConfirmation(
                this, res, args);
            }
        },
        handleConfirm: function(
        uiEvent, businessEvent, control, args) {
            this.sendEventForSavePage("CONFIRM");
        },
        handleCustomPrevious: function(
        uiEvent, businessEvent, control, args) {
            return false;
        },
        handlePrevious: function(
        uiEvent, businessEvent, control, args) {
            if (!(
            this.handleCustomPrevious(
            uiEvent, businessEvent, control, args))) {
                var isPageDirty = false;
                _isccsBaseTemplateUtils.hideMessage(
                this);
                isPageDirty = _isccsWidgetUtils.isWizardPageDirty(
                this);
                if (
                isPageDirty) {
                    _isccsOrderUtils.openOrderEntryOnPreviousPopup(
                    this, "handlePrevious");
                } else {
                    if (
                    _scWizardUtils.isCurrentPageFirstEntity(
                    this) && _isccsContextUtils.isMobileContainer()) {
                        _scControllerUtils.openScreenInEditor("wsc.home.MobileHome", null, this, null, null, "wsc.editors.MobileEditor");
                    } else {
                        _scWizardUtils.previousScreen(
                        this);
                    }
                }
            }
        },
        showNextPage: function(
        uiEvent, businessEvent, control, args) {
            _scWizardUtils.nextScreen(
            this);
        },
        sendEventForSavePage: function(
        actionPerformed) {
            var wizscreen = null;
            var wizscreenUId = null;
            var isScreenValid = null;
            this.ActionPerformed = actionPerformed;
            wizscreen = _scWizardUtils.getCurrentPage(
            this);
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(
            this);
            if (
            isScreenValid = _scScreenUtils.validate(
            wizscreen)) {
                var tempModel = null;
                tempModel = {};
                var eventArg = null;
                eventArg = {};
                if (!(
                _scBaseUtils.isVoid(
                this.ActionPerformed))) {
                    tempModel["Action"] = this.ActionPerformed;
                }
                eventArg["argumentList"] = tempModel;
                _scEventUtils.fireEventToChild(
                this, wizscreenUId, "saveCurrentPage", eventArg);
            } else {
                _isccsBaseTemplateUtils.showMessage(
                this, "Message_screenHasErrors", "error", null);
            }
        },
        onSaveSuccess: function(
        uiEvent, businessEvent, control, args) {
            if (
            _scBaseUtils.equals(
            this.ActionPerformed, "NEXT")) {
                _scWizardUtils.nextScreen(
                this);
                this.ActionPerformed = "";
            }
            if (
            _scBaseUtils.equals(
            this.ActionPerformed, "CONFIRM")) {
                _scWizardUtils.confirmWizard(
                this);
                this.ActionPerformed = "";
            }
            if (
            _scBaseUtils.equals(
            this.ActionPerformed, "CLOSE")) {
                _scWizardUtils.closeWizard(
                this);
                this.ActionPerformed = "";
            }
            if (
            _scBaseUtils.equals(
            this.ActionPerformed, "SAVEONPREVIOUS")) {
                _scWizardUtils.previousScreen(
                this);
                this.ActionPerformed = "";
                this.parentScreenAction = "";
            }
        },
        handleBeforeNext: function(
        uiEvent, businessEvent, control, args) {
            _isccsBaseTemplateUtils.hideMessage(
            this);
        },
        afterPrevious: function(
        uiEvent, businessEvent, control, args) {
            var wizscreenUId = null;
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(
            this);
            var eventArg = null;
            eventArg = {};
            var argsBean = null;
            argsBean = {};
            _scBaseUtils.setAttributeValue("ReloadContext", "WizardPrevious", argsBean);
            eventArg["argumentList"] = argsBean;
            _scEventUtils.fireEventToChild(
            this, wizscreenUId, "reloadScreen", eventArg);
        },
        aftercancelnext: function(
        uiEvent, businessEvent, control, args) {
            var wizscreenUId = null;
            wizscreenUId = _scWizardUtils.getCurrentPageByUId(
            this);
            var eventArg = null;
            eventArg = {};
            var argsBean = null;
            argsBean = {};
            _scBaseUtils.setAttributeValue("ReloadContext", "WizardCancelNext", argsBean);
            eventArg["argumentList"] = argsBean;
            _scEventUtils.fireEventToChild(
            this, wizscreenUId, "reloadScreen", eventArg);
        },
        afterSaveSuccessOnConfirm: function(
        uiEvent, businessEvent, control, args) {
            _scWizardUtils.confirmWizard(
            this);
        },
        afterWizardConfirm: function(
        uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.handleWizardCloseConfirmation(
            this, "Ok", args);
        },
        onPreviousDialogCallback: function(
        actionPerformed, model, popupParams) {
            var currentPage = null;
            var parentScreenAction = null;
            this.parentScreenAction = _scBaseUtils.getValueFromPath("screenInput.parentScreenAction", popupParams);
            currentPage = _scWizardUtils.getCurrentPageByUId(
            this);
            if (
            _scBaseUtils.equals(
            actionPerformed, "SAVEONPREVIOUS")) {
                this.sendEventForSavePage("SAVEONPREVIOUS");
            }
            if (
            _scBaseUtils.equals(
            actionPerformed, "DISCARD")) {
                this.parentScreenAction = "";
                _scWizardUtils.previousScreen(
                this);
            }
            if (
            _scBaseUtils.equals(
            actionPerformed, "CANCEL")) {}
        },
        setScreenTitle: function(
        event, bEvent, ctrl, args) {
            var argumentList = null;
            argumentList = _scBaseUtils.getAttributeValue("argumentList", false, bEvent);
            var inputData = null;
            inputData = _scBaseUtils.getAttributeValue("inputData", false, argumentList);
            var subTitle = null;
            _isccsBaseTemplateUtils.updateTitle(
            this, "extn_Fulfill_Gift_Cards", subTitle);
        },
        handleShowPrevious: function(
        event, bEvent, ctrl, args) {
            _isccsWizardUtils.showNavigationalWidget(
            this, "prevBttn", false, null);
        },
        handleHidePrevious: function(
        event, bEvent, ctrl, args) {
            _isccsWizardUtils.hideNavigationalWidget(
            this, "prevBttn", false);
        },
        handleScreenChanged: function(
        event, bEvent, ctrl, args) {
            _isccsWizardUtils.handleScreenChanged(
            this, "");
        },
        handleRelatedTasks: function(
        uiEvent, businessEvent, control, args) {
            var eventDefn = null;
            eventDefn = {};
            _scBaseUtils.setAttributeValue("argumentList.isWizardinitialized", "true", eventDefn);
            _scEventUtils.fireEventToParent(
            this, "showOrHideRelatedTask", eventDefn);
        },
        onStartWizard: function(
        uiEvent, businessEvent, control, args) {
            var screenInput = null;
            var nodeName = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            nodeName = _isccsUIUtils.getRootName(
            screenInput);
            _isccsWizardUtils.handleDisplayOfAddNoteButton(
            this);
            this.handleDisplayOfCloseButton(
            nodeName);
            _isccsMobileUtils.showMobileBackButton();
        },
        handleDisplayOfCloseButton: function(
        nodeName) {
            var msg = null;
            if (
            this.showBackButton) {
                if (
                _scBaseUtils.equals(
                nodeName, "Order")) {
                    msg = _isccsWizardUtils.getGoBackButtonText(
                    this);
                    _isccsWizardUtils.setLabelOnNavigationalWidget(
                    this, "closeBttn", msg);
                    _isccsWizardUtils.removeClassToNavigationalWidget(
                    this, "closeBttn", "idxSecondaryButton");
                }
            }
        },
        showNotesButton: function(
        uiEvent, businessEvent, control, args) {
            _isccsWizardUtils.showNavigationalWidget(
            this, "addNoteBtn", false, null);
        }
    });
});