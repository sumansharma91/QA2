/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/CouponPortlet.html",
"scbase/loader!dojo/_base/declare",
"scbase/loader!isccs/home/portlets/ReturnsPortletUI",
"scbase/loader!isccs/utils/EventUtils",
"scbase/loader!isccs/utils/OrderUtils",
"scbase/loader!isccs/utils/ReturnUtils",
"scbase/loader!isccs/utils/UIUtils",
"scbase/loader!sc/plat/dojo/utils/BaseUtils",
"scbase/loader!sc/plat/dojo/widgets/Screen",
"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
"scbase/loader!sc/plat/dojo/utils/ModelUtils"], function(
templateText, _dojodeclare, _isccsReturnsPortletUI, _isccsEventUtils, _isccsOrderUtils, _isccsReturnUtils,
 _isccsUIUtils, _scBaseUtils,_scScreen,_scWidgetUtils,_scScreenUtils,_scModelUtils) {
    return _dojodeclare("extn.home.portlets.CouponPortlet", [_scScreen], {
        // custom code here
		templateString: templateText,
		uId: "CouponPortlet",
		packageName: "extn.home.portlets",
		className: "CouponPortlet",
		title: "CouponPortlet",
		screen_description: "This is the CouponPortlet for Home Page",
		isDirtyCheckRequired: false,
		namespaces: {
			targetBindingNamespaces: [{
					seq: '1',
					value: 'getCoupon',
					description: "The input to the getCoupon mashup."
			}],
			sourceBindingNamespaces: [
			
		]
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
                eventId: 'bFindCoupon_onClick',
                sequence: '25',
                description: 'user clicks order to pack',
                listeningControlUId: 'btnFindCoupon',
                handler: {
                    methodName: "openCouponscreen",
                    description: ""
                }
            }]
		},
	   /********
		*	The initializeScreen method calls the extn_inProgressPackOrders_Ref_Id and 
		*   extn_inProgressPickOrders_Ref_Id mashups to get the number of Pack orders and the number
		*   of Pick orders.
		*
		*********/
		initializeScreen: function(event, bEvent, ctrl, args) {

		} ,
		openCouponscreen: function(){
			var root = null;
            root = _scBaseUtils.getTargetModel(this, "getCoupon", null);
			var couponId = _scModelUtils.getStringValueFromPath("Coupon.CouponID", root);
            if (_scBaseUtils.isVoid(couponId)){
					
				var popupMessage = _scScreenUtils.getString(this, "Enter a valid coupon code");
				_scScreenUtils.showErrorMessageBox(this, popupMessage,null, null, null);
            }
                else{
                	_isccsUIUtils.callApi(this, root, "extn_getCouponDetails_RefID");
            }
			
	
	
		},
		handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
			

				if(!_scBaseUtils.isVoid(mashupRefList)){

				// Handles VSIValidateCouponUE Service Call
					if (_scBaseUtils.equals(mashupRefList[0].mashupRefId, "extn_getCouponDetails_RefID")) {
					
							var bindings = null;
							var screenInput = null;
							screenInput = {};
							screenInput = mashupRefList[0].mashupRefOutput;
							var isCallSuccessfull = _scModelUtils.getStringValueFromPath("Coupon.IsCallSuccessfull", screenInput);
							var promoID = _scModelUtils.getStringValueFromPath("Coupon.PromoId", screenInput);
							if(!_scBaseUtils.isVoid(isCallSuccessfull) && _scBaseUtils.equals(isCallSuccessfull, "N") ){
								
								_scScreenUtils.showErrorMessageBox(this, "Entered Coupon is Invalid or Coupon Lookup is unavailable at this time.Please try again after sometime",null, null, null);
							}
							
							else{
							bindings = {};
							popupParams = {};
							popupParams["screenInput"] = screenInput;
							var dialogParams = null;
							dialogParams = {};
							//dialogParams["closeCallBackHandler"] = "onGiftCardEmailSend";
							_scWidgetUtils.setValue(this, "txtCouponNo", "", true );
							_isccsUIUtils.openSimplePopup("extn.home.portlets.CouponLookup", "Coupon Details", this, popupParams, dialogParams);
							}
							
							
					}
				}
		}
	});
});