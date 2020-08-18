/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013, 2016 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/CouponLookup.html","scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/widgets/Screen","scbase/loader!isccs/order/details/OrderLineSummaryUI", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/DeliveryUtils"], function(
templateText,_dojodeclare,_scScreen, _isccsOrderLineSummaryUI, _isccsOrderUtils, _isccsUIUtils, _isccsWidgetUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils, _isccsDeliveryUtils) {
    return _dojodeclare("extn.home.portlets.CouponLookup", [_scScreen], {
        // custom code here
		templateString: templateText,
		uId: "CouponLookup",
		packageName: "extn.home.portlets",
		className: "CouponLookup",
		title: "Coupon Details",
		screen_description: "This is Coupon Details screen for CouponPortlet on Home Page",
		isDirtyCheckRequired: false,
		namespaces: {
			targetBindingNamespaces: [{
					value: 'getCoupon',
			}],
			sourceBindingNamespaces: [{	
			
					value: 'couponDetails',
			
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
			var promotionScreen = _scModelUtils.getStringValueFromPath("Coupon.Promotions.Promotion", paramsScreen);
			_scScreenUtils.setModel(this, "couponDetails", paramsScreen, null);
			/*
			var isCouponValid = _scModelUtils.getStringValueFromPath("Coupon.IsCouponValid", paramsScreen);
			var promotionId = "";
			var promotionDescription = "";
			
			var promotionScreenOCI = _scModelUtils.getStringValueFromPath("Coupon.Promotions.Promotion", paramsOCI);
			var isCouponValidOCI = _scModelUtils.getStringValueFromPath("Coupon.IsCouponValid", paramsOCI);

			
			if(_scBaseUtils.equals(isCouponValid,"Y")){
				_scModelUtils.setStringValueAtModelPath("Coupon.Valid", "Yes", paramsScreen);
			}else{
				_scModelUtils.setStringValueAtModelPath("Coupon.Valid", "No", paramsScreen);
			}

			if(_scBaseUtils.equals(isCouponValidOCI,"Y")){
				_scModelUtils.setStringValueAtModelPath("Coupon.Valid", "Yes", paramsOCI);
			}else{
				_scModelUtils.setStringValueAtModelPath("Coupon.Valid", "No", paramsOCI);
			}

			
			if(!_scBaseUtils.isVoid(promotionScreen)){
			
				for(i=0;i<promotionScreen.length;i++){
						promotionId = _scModelUtils.getStringValueFromPath("PromotionId",promotionScreen[i]);
						promotionDescription = _scModelUtils.getStringValueFromPath("PromotionDescription",promotionScreen[i]);

				}
			}
			
			var promotionIdOCI = "";
			var promotionDescriptionOCI = "";

			
			if(!_scBaseUtils.isVoid(promotionScreenOCI)){
			
				for(i=0;i<promotionScreenOCI.length;i++){
						promotionIdOCI = _scModelUtils.getStringValueFromPath("PromotionId",promotionScreenOCI[i]);
						promotionDescriptionOCI = _scModelUtils.getStringValueFromPath("PromotionDescription",promotionScreenOCI[i]);

				}
			}

			 
			if(!_scBaseUtils.isVoid(paramsOCI)){
				
				_scModelUtils.setStringValueAtModelPath("Coupon.PromoId",promotionIdOCI,paramsOCI);
				_scModelUtils.setStringValueAtModelPath("Coupon.PromoDesc",promotionDescriptionOCI,paramsOCI);
				
				_scScreenUtils.setModel(this, "couponDetails", paramsOCI, null);
				flag = true;
			}
			
			if(!_scBaseUtils.isVoid(paramsScreen) && _scBaseUtils.equals(flag,false)){
				_scModelUtils.setStringValueAtModelPath("Coupon.PromoId",promotionId,paramsOCI);
				_scModelUtils.setStringValueAtModelPath("Coupon.PromoDesc",promotionDescription,paramsOCI);
				
				_scScreenUtils.setModel(this, "couponDetails", paramsScreen, null)
		
			}
			*/
		} ,
		
		closePopUp :function(){
			_scWidgetUtils.closePopup(this, "", false);

		}
    });
});