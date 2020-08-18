/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine([
	"dojo/text!./templates/AvailabilityDetails.html",
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
    return _dojodeclare("extn.custom.availabilityDetails.AvailabilityDetails", [_scScreen], {
		templateString: templateText,
		uId: "AvailabilityDetails",
		packageName: "extn.custom.availabilityDetails",
		className: "AvailabilityDetails",
		title: "AvailabilityDetails",
		screen_description: "This is the AvailabilityDetails Screen for displaying Items inventory on Node",
		isDirtyCheckRequired: false,
		namespaces: {
			targetBindingNamespaces: [
			
			],
			sourceBindingNamespaces: [{
					value: 'screenInput',
					description: "Input provided when the screen is loaded used to default values."
				},
				{
					description: "Holds the output of getAlternateStoreAvailability which is used to paint the grid.",
					value: 'getItemAvailability_output'
				}			
			]
		},
		hotKeys: [{
            id: "Popup_btnCancel",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_btnCancel",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
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
				eventId: 'Popup_btnCancel_onClick',
				sequence: '25',
				description: '',
				listeningControlUId: 'Popup_btnCancel',
				handler: {
					methodName: "onPopupClose",
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
			var inputData = _scScreenUtils.getModel(this, "screenInput");
            if (_scBaseUtils.isVoid(inputData)) {
                var screenInput = _scScreenUtils.getInitialInputData(this);
                if (!(_scBaseUtils.isVoid(screenInput))) {
					var options = {};
					_scBaseUtils.setAttributeValue("screen", this, options);
					_scScreenUtils.setModel(this, "screenInput", screenInput, null);
					_scScreenUtils.setModel(this, "getItemAvailability_output", screenInput, null);
					
					var refGrid = this.getWidgetByUId("listItemAvailability");
					refGrid.body.refresh();
				}
			}
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
        setInitialized: function(event, bEvent, ctrl, args) {
            this.isScreeninitialized = true;
        }
	});		
});