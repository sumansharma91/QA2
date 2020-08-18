/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013, 2016 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/VoucherDetails.html","scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/widgets/Screen","scbase/loader!isccs/order/details/OrderLineSummaryUI", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/DeliveryUtils"], function(
templateText,_dojodeclare,_scScreen, _isccsOrderLineSummaryUI, _isccsOrderUtils, _isccsUIUtils, _isccsWidgetUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _isccsDeliveryUtils) {
    return _dojodeclare("extn.home.VoucherDetails", [_scScreen], {
        // custom code here
		templateString: templateText,
		uId: "VoucherDetails",
		packageName: "extn.home",
		className: "VoucherDetails",
		title: "Voucher Details",
		screen_description: "This is Voucher Details screen on Home Page",
		isDirtyCheckRequired: false,
		namespaces: {
			targetBindingNamespaces: [{
					value: 'getVoucher',
			}],
			sourceBindingNamespaces: [{	
			
					value: 'voucherDetails',
			
			}]
		},
		hotKeys: [],
		events: [],
		subscribers: {
			local: [{
				eventId: "afterScreenInit",
				sequence: "25",
				description: "Subscriber for after Screen Init event for RT",
				handler: {
					methodName: "initializeScreen"
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
            }]
		},
		
		initializeScreen: function(event, bEvent, ctrl, args) {

			var flag = false;

			var paramsOCI = _scModelUtils.getStringValueFromPath("originatingControlInstance.params.scEditorInput", args);
			var paramsScreen = _scModelUtils.getStringValueFromPath("screen.params.scEditorInput", args);
			//var voucherScreen = _scModelUtils.getStringValueFromPath("Voucher", paramsScreen);
			_scScreenUtils.setModel(this, "voucherDetails", paramsScreen, null);
		} ,
		
		closePopUp :function(){
			_scWidgetUtils.closePopup(this, "", false);

		}
    });
});