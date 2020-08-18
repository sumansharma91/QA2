
scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils", 
          "scbase/loader!extn/order/addModifyCharges/ChargeFieldsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!isccs/utils/ModelUtils"
          ],
function(			 
			    _dojodeclare,
				_scBaseUtils,
				_scScreenUtils,
				_scWidgetUtils,
			    _extnChargeFieldsExtnUI,
			    _scModelUtils,
				_isccsModelUtils
){ 
	return _dojodeclare("extn.order.addModifyCharges.ChargeFieldsExtn", [_extnChargeFieldsExtnUI],{
	// custom code here
	
	handleChargeNameChange: function(
        event, bEvent, ctrl, args) {
            var displayedVal = null;
            displayedVal = _scBaseUtils.getValueFromPath("originatingControlInstance.displayedValue", args);
            var argArray = null;
            argArray = [];
            argArray.push(
            displayedVal);
            var ariaLabelValue = null;
			var model =  _scScreenUtils.getModel(this, "extn_TrackingNo_CommonCode_Output");
			
			var commonCode =  _scBaseUtils.getValueFromPath("CommonCodeList.CommonCode", model);
			for(var i=0; i< commonCode.length; i++){
				var codeValue = _scBaseUtils.getValueFromPath("CodeValue", commonCode[i]);
					if(_scBaseUtils.equals(displayedVal,codeValue)){
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
			
			var popupMessage = _scScreenUtils.getString(this, "extn_Shipping_Line_Error");
			var popupMessage1 = _scScreenUtils.getString(this, "extn_WH_Shipping_Line_Error");
			
			if (_scBaseUtils.equals(displayedVal, "Coupon - Expired") || _scBaseUtils.equals(displayedVal, "Coupon - Issue")) {
			_scWidgetUtils.showWidget(this, "extn_coupon_number", false, null);
			} else {
			_scWidgetUtils.hideWidget(this, "extn_coupon_number", false);
			}
			if (_scBaseUtils.equals(this.LineMode, "Y") && _scBaseUtils.equals(displayedVal, "Shipping Appeasement")) {
			_scScreenUtils.showErrorMessageBox(this, popupMessage,null, null, null);
			}
			if (_scBaseUtils.equals(this.LineMode, "Y") && (_scBaseUtils.equals(displayedVal, "Freight Charge") || _scBaseUtils.equals(displayedVal, "Freight Discount"))) {
			_scScreenUtils.showErrorMessageBox(this, popupMessage1,null, null, null);
			}
			
            ariaLabelValue = _scScreenUtils.getFormattedString(
            this, "arialabel_addModChrDel_chrName_Link", argArray);
            _scWidgetUtils.setLinkAriaLabel(
            this, "btndeleteCharges", ariaLabelValue);
        },
        initializeScreen: function(
        event, bEvent, ctrl, args) {
            this.initCurrencyModel();
			
			var hdrModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			var displayedVal = null;
            displayedVal = _scBaseUtils.getValueFromPath("HeaderCharge.ChargeName", hdrModel);
            var strChargeCategory = _scBaseUtils.getValueFromPath("HeaderCharge.ChargeCategory", hdrModel);
            if(!_scBaseUtils.isVoid(hdrModel) 
            		&& (_scBaseUtils.equals(strChargeCategory, "Discount")
            		|| _scBaseUtils.equals(strChargeCategory, "Shipping"))){
            	hdrModel.HeaderCharge.IsManual="N";
            }
            
			if (_scBaseUtils.equals(displayedVal, "Coupon - Expired") || _scBaseUtils.equals(displayedVal, "Coupon - Issue")) {
			_scWidgetUtils.showWidget(this, "extn_coupon_number", false, null);
			} else {
			_scWidgetUtils.hideWidget(this, "extn_coupon_number", false);
			}
			
			var mode = this.getScreenMode(event, bEvent, ctrl, args);
			_scWidgetUtils.hideWidget(this, "extn_Apply_To", false);
			//_scWidgetUtils.showWidget(this, "extn_Apply_To", false, null);
			
			//if (_scBaseUtils.equals(mode, "LineMode")){
			//_scWidgetUtils.showWidget(this, "extn_Apply_To", false, null);
			//} else {
			//_scWidgetUtils.hideWidget(this, "extn_Apply_To", false);
			//}

            var inputModel = null;
            if (!(
            _scBaseUtils.isVoid(
            inputModel = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output")))) {
                _scScreenUtils.setModel(
                this, "optionsSource_output", inputModel, null);
                this.DisableChargeAmount(
                inputModel);
				
            } else {
                this.constructCmbOptions();
            }
			
			
        },
        
       DisableChargeAmount: function(
                orderModel) {
        var isManual = null;
        isManual = _scModelUtils.getStringValueFromPath("HeaderCharge.IsManual", orderModel);
        var chargeAmount = _scModelUtils.getStringValueFromPath("HeaderCharge.ChargeAmount", orderModel);
        //alert(chargeAmount);
        if (
        _scBaseUtils.equals(
        isManual, "N")) {
			console.log("disabling...");
            /* _scWidgetUtils.disableWidget(
            this, "txt_chargeAmount", false); */
            _scWidgetUtils.enableReadOnlyWidget(
            this, "txt_chargeAmount", false);
            _scWidgetUtils.disableWidget(
            this, "cmbApplyTo", false);
            _scWidgetUtils.disableWidget(
            this, "cmbChargeName", false);
            _scWidgetUtils.disableWidget(
            this, "cmbChargeCategory", false);
            _scWidgetUtils.hideWidget(this, "extn_Apply_To", false);
            _scWidgetUtils.setValue(this, "txt_chargeAmount",chargeAmount , false);

        } else {
			console.log("enabling...");
            _scWidgetUtils.enableWidget(
            this, "txt_chargeAmount");
            _scWidgetUtils.enableWidget(
            this, "cmbApplyTo");
            _scWidgetUtils.enableWidget(
            this, "cmbChargeName");
            _scWidgetUtils.enableWidget(
            this, "cmbChargeCategory");
        }
    },
	validateChargeAmount: function(event, bEvent, ctrl, args) {
		console.log("setIsNewFlag", event);
		console.log("bEvent", bEvent);
		console.log("ctrl", ctrl);
		console.log("args", args);
		
		if (_scBaseUtils.equals(this.LineMode, "Y")) {
			var chargeName = this.getWidgetByUId("cmbChargeName");
			if (_scBaseUtils.isVoid(chargeName.value)) {
				chargeName = this.getWidgetByUId("lblChargeName");
			}
			
			var parentScreen = this.getOwnerScreen();
			if (!parentScreen.validateChargeAmount(chargeName, event, this)) {
				_scWidgetUtils.setValue(this, "txt_chargeAmount","" , false);
			}
		}
	}
	
});
});
