
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/customerAppeasement/CustomerAppeasementSelectionExtnUI",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/GridxUtils",	
	"scbase/loader!isccs/utils/ContextUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!dijit"
],
function(			 
	_dojodeclare,
	_extnCustomerAppeasementSelectionExtnUI,
	_scWidgetUtils,
	_scBaseUtils,
	_scScreenUtils,
	_scModelUtils,
	_isccsUIUtils,
	_scEventUtils,
	_scGridxUtils,
	_isccsContextUtils,
	_isccsBaseTemplateUtils,
	_dijit
				
){ 
	return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementSelectionExtn", [_extnCustomerAppeasementSelectionExtnUI],{
	// custom code here
		// Add this custom method to control the display of Coupon Code Pane based on selected Customer Appeasment Reason Code.
		// It will handle the radio button display based on selected Customer Appeasment Reason Code.
		showCopunCodeContentPane :function() {
			var displayedValue = this.getWidgetByUId("cmbReasoncode").displayedValue;
			var trackingCommonCodeModel =  _scScreenUtils.getModel(this, "extn_TrackingNumber_CommonCode");

			var commonCode =  _scBaseUtils.getValueFromPath("CommonCodeList.CommonCode", trackingCommonCodeModel);

			for(i=0; i< commonCode.length; i++){
				var codeValue = _scBaseUtils.getValueFromPath("CodeValue", commonCode[i]);
					if(_scBaseUtils.equals(displayedValue,codeValue)){
						_scWidgetUtils.showWidget(
						this, "extn_textfield_TrackingNumber", false);
						_scWidgetUtils.setWidgetMandatory(this,"extn_textfield_TrackingNumber");
						break;
					}
					else{
						_scWidgetUtils.hideWidget(
						this, "extn_textfield_TrackingNumber", false);
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_textfield_TrackingNumber");
					}
				
			}



			
			var appeasmentLevel = this.getWidgetByUId("radAppeasementLevel");
			//Get the OrderLine radio button and label
			var radioItem1 = "_RadioItem1";
			var radioID = appeasmentLevel.id;
			radioID = radioID + radioItem1;
			var radioItem2 = _dijit.byId(radioID);
			var btn = radioItem2.domNode;
			var lbl=btn.nextSibling;
			////If Reason Code Coupon selected then Coupon Code contentpane will displayed else not.  
			if(_scBaseUtils.contains(displayedValue, "Coupon")){
				_scWidgetUtils.showWidget(this, "extn_contentpane_CouponCode", false);
			}else{
				_scWidgetUtils.hideWidget(this, "extn_contentpane_CouponCode", false);
				var textCouponCode = this.getWidgetByUId("extn_txt_Coupon_Code");
				textCouponCode.textbox.value="";			
			}
			//If Reason Code Shipping Appeasement selected then By default Order Radio Button will be selected and OrderLine Radio button will be hide.
			//else both radio button will be display on the screen.
			if(_scBaseUtils.contains(displayedValue, "Shipping Appeasement")){
				appeasmentLevel.options[0].selected=true;
				// Makes the orderline option invisible in the radio button
				dojo.style(btn, {visibility:'hidden'});
				if (lbl){
					lbl.style.display = "none";
				}
			}else{
				dojo.style(btn, {visibility:''});
				if (lbl){
					lbl.style.display = "";
				}
			}
		}
	
		
});
});

