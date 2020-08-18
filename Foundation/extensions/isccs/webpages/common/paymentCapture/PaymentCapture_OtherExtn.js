scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/common/paymentCapture/PaymentCapture_OtherExtnUI",
		"scbase/loader!isccs/utils/BaseTemplateUtils",
		"scbase/loader!isccs/utils/OrderUtils",
		"scbase/loader!isccs/utils/UIUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!sc/plat/dojo/utils/ModelUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils"
		]
,
function(			 
		_dojodeclare,
		_extnPaymentCapture_OtherExtnUI,
		_isccsBaseTemplateUtils,
		_isccsOrderUtils,
		_isccsUIUtils,
		_scBaseUtils,
		_scModelUtils,
		_scScreenUtils,
		_scWidgetUtils
		
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCapture_OtherExtn", [_extnPaymentCapture_OtherExtnUI],{
	// custom code here
	        
        initializeScreen: function(event, bEvent, ctrl, args) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(this);
            console.log(screenInput);
            _scScreenUtils.setModel(this, "PaymentCapture_output", screenInput, null);

            if (_scBaseUtils.equals("Customer", this.screenMode)) {
                if (!(_scBaseUtils.isVoid( _scModelUtils.getStringValueFromPath("CustomerPaymentMethod.CustomerPaymentMethodKey", screenInput)))) {
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference1");
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference2");
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference3");
                }
            } else {
                if (_scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", screenInput, false)) {
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference1");
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference2");
                    _scWidgetUtils.enableReadOnlyWidget(this, "txtPaymentReference3");
                }else{
					_scWidgetUtils.hideWidget(this, "txtPaymentReference2", false);
					_scWidgetUtils.hideWidget(this, "txtPaymentReference3", false);
				}
                var sReturnDocumentType = "";
                sReturnDocumentType = _isccsOrderUtils.getReturnOrderDocumentType();
                if(!_scBaseUtils.equals("VOUCHERS", _scModelUtils.getStringValueFromPath("Order.PaymentMethod.PaymentType", screenInput, false))) {
					//_scWidgetUtils.hideWidget(this, "txtMaxChargeLimit", false);
					_scWidgetUtils.setLabel(this,"txtMaxChargeLimit","Amount:");
					_scWidgetUtils.setLabel(this,"txtPaymentReference1","Reference #:");
                }
                if(_scBaseUtils.equals(sReturnDocumentType, _scModelUtils.getStringValueFromPath("Order.DocumentType", screenInput, false))) {
					_scWidgetUtils.hideWidget(this, "txtMaxChargeLimit", false);
                }
                
            }
        },
		handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {

			_isccsBaseTemplateUtils.handleMashupCompletion(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
		
			if(!_scBaseUtils.isVoid(mashupRefList)){
				if (_scBaseUtils.equals(mashupRefList[0].mashupRefId, "extn_VSICheckVoucher_RefID")) {
					var outputModel = mashupRefList[0].mashupRefOutput.Voucher;
					_scScreenUtils.setModel(this, "extn_checkVoucher_output", outputModel, null);
					console.log("outputModel",outputModel);

					//START
					//Fix for SU-21
					//Added else block for SU-89
					var redeemDate = _scModelUtils.getStringValueFromPath("RedeemDate",outputModel);
					console.log("redeemDate: "+redeemDate);
					redeemDate = _scBaseUtils.convertToUserFormat(redeemDate,"DATE");
					var strComments = _scModelUtils.getStringValueFromPath("Comments",outputModel);
					console.log("redeemDate: "+redeemDate+"strComments: "+strComments);
					var issuedAmount = _scModelUtils.getStringValueFromPath("IssueAmount",outputModel);
					var isValid = _scModelUtils.getStringValueFromPath("isValid",outputModel);
					console.log("issuedAmount: "+issuedAmount+", isValid: "+isValid);
					if(_scBaseUtils.equals("Y",isValid)){
						console.log("SETTING_VALUE");
						_scWidgetUtils.setValue(this,"txtMaxChargeLimit", issuedAmount,false);
						_scWidgetUtils.enableReadOnlyWidget(this, "txtMaxChargeLimit");
					}else{
						console.log("Not_Valid");
						if(!_scBaseUtils.isVoid(strComments)){
							console.log("Inside_Comments")
							if(_scBaseUtils.equals("QUARTERLY EXPIRATION",strComments)){
								console.log("EXPIRATION");
								_scScreenUtils.showErrorMessageBox(this, "This is a Quarterly Voucher and it has expired.\nThe Voucher had a value of $" + issuedAmount,null,null,null);
							}else if(_scBaseUtils.equals("Invalid Coupon number",strComments)){
								_scScreenUtils.showErrorMessageBox(this, "Invalid Voucher number entered",null,null,null);
							}else{
								console.log("EXPIRATION2");
								_scScreenUtils.showErrorMessageBox(this, "This Voucher was redeemed already.",null,null,null);
								_scWidgetUtils.setValue(this,"txtMaxChargeLimit", "",false);
							}
						}else{
							console.log("Else");
							_scScreenUtils.showErrorMessageBox(this, "Thats an invalid Voucher.",null,null,null);	
							_scWidgetUtils.setValue(this,"txtMaxChargeLimit", "",false);
						}
					}
					//END
				
				}
			}
        },
		getVoucherValue: function(){
			var paymentModel = _scBaseUtils.getTargetModel(this, "PaymentCapture_input", null);	
			console.log(paymentModel);
			var screenInput = _scScreenUtils.getInitialInputData(this);
			if(_scBaseUtils.equals("VOUCHERS", _scModelUtils.getStringValueFromPath("Order.PaymentMethod.PaymentType", screenInput, false))) 
			{
				//console.log(screenInput);
				var voucherInput = {};
				voucherInput.Voucher  = {};
				var certNo = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference1",paymentModel);
				if(!_scBaseUtils.isVoid(certNo)){
					var isLoyaltyVoucher = "N";
					if(!/^\d+$/.test(certNo)){
						isLoyaltyVoucher="Y";
					}
					_scModelUtils.addStringValueToModelObject("CertNo" ,certNo ,voucherInput.Voucher);
					_scModelUtils.addStringValueToModelObject("IsLoyaltyVoucher" ,isLoyaltyVoucher ,voucherInput.Voucher);
					console.log("voucherInput",voucherInput);
					_isccsUIUtils.callApi(this, voucherInput, "extn_VSICheckVoucher_RefID");			
			}
			}
		}
});
});

