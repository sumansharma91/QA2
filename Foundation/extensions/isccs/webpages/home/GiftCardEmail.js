scDefine(["dojo/text!./templates/GiftCardEmail.html", 
		  "scbase/loader!dojo/_base/declare", 
		  "scbase/loader!isccs/utils/BaseTemplateUtils",  
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/widgets/Screen",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
		  ], 
function( templateText,
		  _dojodeclare, 
		  _isccsBaseTemplateUtils, 
		  _isccsUIUtils,
		  _scBaseUtils, 
		  _scModelUtils,
		  _scScreen,
		  _scScreenUtils,
		  _scWidgetUtils
) {  

    return _dojodeclare("extn.home.GiftCardEmail", [_scScreen], {
        templateString: templateText,
        uId: "GiftCardEmail",
        packageName: "extn.home",
        className: "GiftCardEmail",
        title: "GiftCardEmail",
        screen_description: "Screen for sending gift card email.",
        namespaces: {
            targetBindingNamespaces: [{
                description: "Values to initialize Payment Capture for Other when editing payment method.",
				value: 'giftCard_input'
				
            }
			],
            sourceBindingNamespaces: [
			]
        },
        hotKeys: [],
        events: [],
        subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '30',
                handler: {
                    methodName: "initializeScreen"
                }
            }, {
				eventId: 'afterBehaviorMashupCall',
				sequence: '51',
				handler: {
					methodName: "extn_afterBehaviorMashupCall"
				}
			}, {
                eventId: 'Popup_btnConfirm_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'Popup_btnConfirm',
                handler: {
                    methodName: "confirmPopup",
                    description: ""
                }
            },{
                eventId: 'Popup_btnClose_onClick',
                sequence: '30',
                description: '',
                listeningControlUId: 'Popup_btnClose',
                handler: {
                    methodName: "closePopUp",
                    description: ""
                }
            },]
        },
		
		confirmPopup: function(){
			var isValid = true;
			isValid = _scScreenUtils.validate(this);

            if (_scBaseUtils.equals(isValid,true)) {
				var gcInput = null;
				gcInput = _scBaseUtils.getTargetModel(this, "giftCard_input", null);
				var callCenter = "Call Center";

				_scModelUtils.addStringValueToModelObject("EntryType" ,callCenter ,gcInput.Order);
				_isccsUIUtils.callApi(this, gcInput, "extn_triggerEmailOnVGCRefund_Ref_Id");
				
			}	
		},
		closePopUp :function(){
			_scWidgetUtils.closePopup(this, "", false);

		}, 
		extn_afterBehaviorMashupCall: function(event, bEvent, ctrl, args){
			_scWidgetUtils.closePopup(this, "", false);

		},
		initializeScreen: function(event, bEvent, ctrl, args) {	
		}



    });
});