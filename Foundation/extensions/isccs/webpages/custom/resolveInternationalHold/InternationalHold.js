/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine([
	"dojo/text!./templates/InternationalHold.html",
	"scbase/loader!dojo/_base/declare", 
	"scbase/loader!isccs/utils/BaseTemplateUtils", 
	"scbase/loader!isccs/utils/EventUtils", 
	"scbase/loader!isccs/utils/UIUtils", 
	"scbase/loader!isccs/utils/WidgetUtils", 
	"scbase/loader!sc/plat/dojo/utils/BaseUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!isccs/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/GridxUtils"
], function(
	templateText,
	_dojodeclare, 
	_isccsBaseTemplateUtils, 
	_isccsEventUtils, 
	_isccsUIUtils, 
	_isccsWidgetUtils, 
	_scBaseUtils,  
	_scModelUtils, 
	_scScreenUtils, 
	_scWidgetUtils,
	_scScreen,
	_isccsModelUtils,
	_scGridxUtils) {
    return _dojodeclare("extn.custom.resolveInternationalHold.InternationalHold", [_scScreen], {
		templateString: templateText,
		uId: "InternationalHold",
		packageName: "extn.custom.InternationalHold",
		className: "InternationalHold",
		title: "InternationalHold",
		screen_description: "This is the InternationalHold Screen for displaying quantities to be cancelled",
		isDirtyCheckRequired: false,
		namespaces: {
			targetBindingNamespaces: [
			
			],
			sourceBindingNamespaces: [
				{
					description: "Holds the output of getAlternateStoreAvailability which is used to paint the grid.",
					value: 'getOrderList_output'
				}			
			]
		},
		events: [{
            name: 'onEnterEvent'
        }],
		subscribers: {
			local: [{
				eventId: "afterScreenInit",
				sequence: "25",
				description: "Subscriber for after Screen Init event for AvailabilityDetails",
				handler: {
					methodName: "initializeScreen"
				}
			},{
				eventId: 'Popup_btnNo_onClick',
				sequence: '25',
				description: '',
				listeningControlUId: 'Popup_btnNo',
				handler: {
					methodName: "onPopupClose",
					description: ""
				}
			},{
				eventId: 'Popup_btnYes_onClick',
				sequence: '25',
				description: '',
				listeningControlUId: 'Popup_btnYes',
				handler: {
					methodName: "onPopupConfirm",
					description: ""
				}
			},{
                eventId: 'afterScreenInit',
                sequence: '50',
                description: 'Subscriber for after screen is initialized',
                handler: {
                    methodName: "setInitialized"
                }
            }, {
                eventId: 'afterScreenInit',
                sequence: '25',
                description: 'Subscriber for after the screen is initialized',
                handler: {
                    methodName: "initScreen"
                }
            }]
		},
		initializeScreen: function(event, bEvent, ctrlId, args) {
			//var inputData = _scScreenUtils.getModel(this, "screenInput");
			var paramsOCI = _scModelUtils.getStringValueFromPath("originatingControlInstance.params.scEditorInput", args);
			console.log("paramsOCI",paramsOCI);


			_scScreenUtils.setModel(this, "getOrderList_output", paramsOCI, null);					
			var inputData = _scScreenUtils.getModel(this, "getOrderList_output");
			console.log("inputData",inputData);
            var refGrid = this.getWidgetByUId("listOfOrderLines");
			refGrid.body.refresh();
		},
        initScreen: function(event, bEvent, ctrl, args) {
            return true;
        },
		onPopupClose: function(event, bEvent, ctrl, args) {
            _scWidgetUtils.closePopup(this, "CLOSE", false);            
            var res = _scModelUtils.createNewModelObjectWithRootKey("response");
            _scModelUtils.addStringValueToModelObject("statusCode", "failure", res);
            return res;
        },
		onPopupConfirm: function(event, bEvent, ctrl, args) {
			var inputData = _scScreenUtils.getModel(this, "getOrderList_output");
					console.log("inputData",inputData);
			_scScreenUtils.setPopupOutput(this, inputData);
            _scWidgetUtils.closePopup(this, "APPLY", false);
        },
        setInitialized: function(event, bEvent, ctrl, args) {
            this.isScreeninitialized = true;
        }
	});		
});