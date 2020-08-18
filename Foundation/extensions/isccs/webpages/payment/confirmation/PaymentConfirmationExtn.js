
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/payment/confirmation/PaymentConfirmationExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/EventUtils", 
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/SharedComponentUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EventUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils",
		  "scbase/loader!dijit"
		  ],
function(			 
			_dojodeclare,
			_extnPaymentConfirmationExtnUI,
			_isccsBaseTemplateUtils, 
			_isccsEventUtils, 
			_isccsModelUtils, 
			_isccsOrderUtils, 
			_isccsSharedComponentUtils, 
			_isccsUIUtils, 
			_scBaseUtils, 
			_scEventUtils, 
			_scModelUtils, 
			_scScreenUtils, 
			_scWidgetUtils,
			_scEditorUtils,
			_scResourcePermissionUtils,
			_dijit
){ 
	var flag = false;
	var aurusSessionResponse= {};
	var paymentcapturescreen=null;
	//var isCallCrmFlag= true;
	var manageCustomerModel = null;
	return _dojodeclare("extn.payment.confirmation.PaymentConfirmationExtn", [_extnPaymentConfirmationExtnUI],{
	// custom code here

	addPaymentMethod: function(
        event, bEvent, ctrl, args) {
	_scScreenUtils.setModel(this, "isCallCrmFlag", true, null);

			var orderModel = null;
			var currentEditor = _scEditorUtils.getCurrentEditor();
			orderModel = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
			var bindings  = _scBaseUtils.getNewBeanInstance();
				_scBaseUtils.addModelValueToBean("OrderDetailModel",orderModel,bindings);
            var popupParams = _scBaseUtils.getNewBeanInstance();
				_scBaseUtils.addModelValueToBean("binding",bindings,popupParams);
            var inputModel = null;
            inputModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
            var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "paymentConfirmation_getCompleteOrderDetails_Output");
            var enterpriseCode = null;
            enterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", enterpriseCode, inputModel);
            var documentType = null;
            documentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.DocumentType", documentType, inputModel);
            var billToID = null;
            billToID = _scModelUtils.getStringValueFromPath("Order.BillToID", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.BillToID", billToID, inputModel);
            var customerContactID = null;
            customerContactID = _scModelUtils.getStringValueFromPath("Order.CustomerContactID", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.CustomerContactID", customerContactID, inputModel);
            var draftOrderFlag = null;
            draftOrderFlag = _scModelUtils.getStringValueFromPath("Order.DraftOrderFlag", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.DraftOrderFlag", draftOrderFlag, inputModel);
            var priceInfo = null;
            priceInfo = _scModelUtils.getModelObjectFromPath("Order.PriceInfo", orderModel);
            _scModelUtils.addModelToModelPath("Order.PriceInfo", priceInfo, inputModel);
            var sellerOrganizationCode = "";
            sellerOrganizationCode = _scModelUtils.getStringValueFromPath("Order.SellerOrganizationCode", orderModel);
            _scModelUtils.setStringValueAtModelPath("Order.SellerOrganizationCode", sellerOrganizationCode, inputModel);
            var personInfoModel = null;
            personInfoModel = _scModelUtils.getModelObjectFromPath("Order.PersonInfoBillTo", orderModel);
            _scModelUtils.addModelToModelPath("Order.PersonInfoBillTo", personInfoModel, inputModel);
			
			// ARE-423 : Added to pass the DeliveryMethod to the PaymentCapture popup screen : BEGIN
            var orderLineMod = _scModelUtils.getModelObjectFromPath("Order.OrderLines.OrderLine", orderModel);
            var delMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod", orderLineMod[0]);
            _scModelUtils.setStringValueAtModelPath("Order.DeliveryMethod", delMethod, inputModel);
			// ARE-423 : Added to pass the DeliveryMethod to the PaymentCapture popup screen : END
			
            popupParams["screenInput"] = inputModel;
            var dialogParams = null;
            dialogParams = {};
            _scBaseUtils.setAttributeValue("closeCallBackHandler", "applyPaymentMethod", dialogParams);
            //_isccsUIUtils.openSimplePopup("extn.common.tnsPaymentCapture.TNSPaymentCapture_CreditCard", "AddPaymentMethod", this, popupParams, dialogParams);
			_isccsUIUtils.openSimplePopup("isccs.common.paymentCapture.PaymentCapture", "AddPaymentMethod", this, popupParams, dialogParams);
			this.getSessionModule();
        },
		getSessionModule: function(model){
		var inputModel = {};
	
		if(!(_scBaseUtils.isVoid(model))){
			inputModel.SessionRequest = {};
			
			var maskCardNo="XXXXXXXXXXXX";
			maskCardNo=maskCardNo+model.PaymentMethod.DisplayCreditCardNo;
			
			inputModel.SessionRequest.CardNumber = maskCardNo;//model.PaymentMethod.PaymentReference2;
			inputModel.SessionRequest.CardType = model.PaymentMethod.CreditCardType;
//			inputModel.SessionRequest.CardIdentifier = "2000000000002615";//model.PaymentMethod.CreditCardNo;
			inputModel.SessionRequest.CardIdentifier = model.PaymentMethod.CreditCardNo;
			
			
			var cardType = model.PaymentMethod.CreditCardType;
            
            if(_scBaseUtils.equals(cardType, "visa")){
           	 cardType = "VIC";
            }else if(_scBaseUtils.equals(cardType, "mastercard")){
           	 cardType = "MCC";
            }else if(_scBaseUtils.equals(cardType, "jcb")){
           	 cardType = "JBC";
            }else if(_scBaseUtils.equals(cardType, "discover")){
           	 cardType = "NVC";
            }else if(_scBaseUtils.equals(cardType, "diners_club")){
           	 cardType = "DCC";
            }else if(_scBaseUtils.equals(cardType, "amex")){
           	 cardType = "AXC";
            }
            
            inputModel.SessionRequest.CardType=cardType;
			
			var expDate= model.PaymentMethod.CreditCardExpDate;
			var expDateSlash = expDate.indexOf("/");
			 if(expDateSlash>-1){
				var str_array = expDate.split('/');
//				expDate = str_array[0]+  str_array[1].substr(0,2);
				
				if(_scBaseUtils.equals(str_array[1].length,2)){
					expDate = str_array[0]+ str_array[1].substr(0,2);
				}else if (_scBaseUtils.equals(str_array[1].length,4)){
					 expDate = str_array[0]+  str_array[1].substr(2,4);
				 }
				
			}
			
//			inputModel.SessionRequest.CardExpiryDate = "0323"//expDate;
			inputModel.SessionRequest.CardExpiryDate = expDate;
		}
		//console.log("getProperty",inputModel);
		_isccsUIUtils.callApi(this, inputModel, "extn_getProperty_referenceid");
		},
		getAurusSessionResponse: function(){
			return aurusSessionResponse;
		},

		applyCoupon: function(
        event, bEvent, ctrl, args) {
            if (
            _scScreenUtils.isValid(
            this, "changeOrder_Input")) {
                var changeOrderModel = null;
                changeOrderModel = _scBaseUtils.getTargetModel(
                this, "changeOrder_Input", null);
                var promotionId = null;
                promotionId = _scModelUtils.getStringValueFromPath("Order.Promotions.Promotion.PromotionId", changeOrderModel);
                var sMessage = null;
                if (
                _scBaseUtils.isVoid(
                promotionId)) {
                    sMessage = _scScreenUtils.getString(
                    this, "EnterValidPromoCode");
                    var widget = "";
                    widget = _scEventUtils.getOriginatingControlUId(
                    bEvent);
                    _scWidgetUtils.markFieldinError(
                    this, widget, sMessage, false);
                } else {
					var changeOrderInputModel = _scBaseUtils.getTargetModel(this, "changeOrder_Input", null);
					var pricingDate = _scModelUtils.getStringValueFromPath("Order.Extn.ExtnPricingDate", changeOrderInputModel);

					//console.log("sourceModel",sourceModel);
					//console.log("changeOrderInputModel",changeOrderInputModel);
					if(_scBaseUtils.isVoid(pricingDate)){
						var sourceModel = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
						console.log("sourceModel",sourceModel);
						console.log("changeOrderInputModel",changeOrderInputModel);
						var inputModel = {};
						inputModel.Coupon = {};
						_scModelUtils.addStringValueToModelObject("CouponID",promotionId, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("Currency",sourceModel.Order.PriceInfo.Currency, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("CustomerID",sourceModel.Order.BillToID, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("DisplayLocalizedFieldInLocale","en_US_EST", inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("EnterpriseCode",sourceModel.Order.EnterpriseCode, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("OrderReference",sourceModel.Order.OrderHeaderKey, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("OrganizationCode",sourceModel.Order.EnterpriseCode, inputModel.Coupon);
						console.log("inputModel",inputModel);
						_isccsUIUtils.callApi(this, inputModel, "extn_applyCoupon_referenceid");
					}
					else{
						var sourceModel = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
						console.log("sourceModel",sourceModel);
						console.log("changeOrderInputModel",changeOrderInputModel);
						var inputModel = {};
						inputModel.Coupon = {};
						inputModel.Coupon.References = {};
						inputModel.Coupon.References.Reference = {};
						
						_scModelUtils.addStringValueToModelObject("CouponID",promotionId, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("Currency",sourceModel.Order.PriceInfo.Currency, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("CustomerID",sourceModel.Order.BillToID, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("DisplayLocalizedFieldInLocale","en_US_EST", inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("EnterpriseCode",sourceModel.Order.EnterpriseCode, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("OrderReference",sourceModel.Order.OrderHeaderKey, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("OrganizationCode",sourceModel.Order.EnterpriseCode, inputModel.Coupon);
						_scModelUtils.addStringValueToModelObject("ReferenceId","Override", inputModel.Coupon.References.Reference);
						
						console.log("inputModel",inputModel);
						_isccsUIUtils.callApi(this, inputModel, "extn_applyCoupon_referenceid");

						
					}
                }
            }
        },
		handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderDetails")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "paymentConfirmation_getCompleteOrderDetails_Output", modelOutput, null);
                }
                this.updateRemainingAmountToAuth(
                modelOutput);
                this.showHidePromotionPanels(
                modelOutput);
                console.log("flag",flag);
                if(flag){
                	flag = false;
                	var inputModel = {};
					inputModel.Order = {};
					_scModelUtils.addStringValueToModelObject("DisplayLocalizedFieldInLocale","en_US_EST", inputModel.Order);
					_scModelUtils.addStringValueToModelObject("OrderHeaderKey",modelOutput.Order.OrderHeaderKey, inputModel.Order);
                	console.log("inputModel",inputModel);
                	_isccsUIUtils.callApi(
                            this, inputModel, "getCompleteOrderDetails", null);
                }
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "applyCoupon")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "changeOrder_Output", modelOutput, null);
                }
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "capturePayment")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
					var inCPModel = _scScreenUtils.getModel(this, "capturePayment_Input");
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.FundsAvailable",_scModelUtils.getStringValueFromPath("PaymentMethod.MaxChargeLimit", inCPModel),modelOutput);
                    _scScreenUtils.setModel(
                    this, "capturePayment_Output", modelOutput, null);
                }
                this.checkDuplicatePaymentMethod(
                modelOutput);
                this.callProcessOrderPayments(
                modelOutput, mashupInput);
                var orderModel = null;
                orderModel = _scScreenUtils.getModel(
                this, "paymentConfirmation_getCompleteOrderDetails_Output");
                var newOrderModel = null;
                newOrderModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
                var orderHeaderKey = null;
                orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel);
                _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, newOrderModel);
                _scModelUtils.setStringValueAtModelPath("Order.RetrieveDefaultCustomerPaymentMethod", "N", newOrderModel);
                _isccsUIUtils.callApi(
                this, newOrderModel, "getCompleteOrderDetails", null);
                this.checkNewInvalidPromotion();
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "processOrderPayments")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "processOrderPayments_Output", modelOutput, null);
                }
                

//			  _scScreenUtils.setModel(
//	                    this, "paymentConfirmation_getCompleteOrderDetails_Output", null, null);
                
                console.log("processOrderPayments  == paymentcapturescreen "+paymentcapturescreen);
                
                var isCallCrmFlag = _scScreenUtils.getModel(this, "isCallCrmFlag");
                
                console.log("processOrderPayments  == isCallCrmFlag "+isCallCrmFlag);
                
                if (isCallCrmFlag){
	                var newOrderModel = null;
	                newOrderModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
	                var orderHeaderKey = null;
	                orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", modelOutput);
	                _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, newOrderModel);
	//                _scModelUtils.setStringValueAtModelPath("Order.CallFromSubmitButton", "Y", newOrderModel);
	                
	                console.log("getCompleteOrderDetails after ProcessOrderPyament newOrderModelInput "+newOrderModel);
	                
	                _isccsUIUtils.callApi(
	                        this, newOrderModel, "extn_payconfirmgetOrderList", null);
                }


				this.checkProcessOrderPaymentsErrors(modelOutput);
            }
			if(_scBaseUtils.equals(
                    mashupRefId, "extn_payconfirmgetOrderList")){
        				console.log("extn_payconfirmgetOrderList: ",modelOutput);
        				console.log("manageCustomerModel: ",manageCustomerModel);
                		
                		
                    	if(!(_scBaseUtils.isVoid(manageCustomerModel))){
                    		
                    		var attrPaymentMethod = _scModelUtils.getModelObjectFromPath("PaymentMethod",manageCustomerModel);
                    		console.log("modelOutput attrPaymentMethod == ",attrPaymentMethod);
//                    		var cardNumber_attrPaymentMethod = attrPaymentMethod.CreditCardNo;
                    		var cardNumber_attrPaymentMethod = attrPaymentMethod.PaymentReference3;
                			var customerPaymentMethodsModel = {}; 
                			customerPaymentMethodsModel = manageCustomerModel.CustomerPaymentMethodList;
                			console.log("modelOutput customerPaymentMethodsModel == ",customerPaymentMethodsModel);
                			
                			
    	    				
    	    				var crnCCNo;
    	    				
    	    				var order = modelOutput.OrderList.Order[0];
    	    				
    	    				var sPaymentMethods = _scModelUtils.getStringValueFromPath("PaymentMethods", order);
    	    				var listOfPaymentMethods = null;
                            listOfPaymentMethods = _scModelUtils.getModelListFromPath("PaymentMethods.PaymentMethod", order);
    	    				var sPaymentMethodsLength = _scBaseUtils.getAttributeCount(listOfPaymentMethods); 
    	    			    console.log("modelOutput sPaymentMethodsLength == ",sPaymentMethodsLength);
    	    			    console.log("modelOutput sPaymentMethods == ",sPaymentMethods);
    	    			    console.log("modelOutput cardNumber_attrPaymentMethod == ",cardNumber_attrPaymentMethod);
    	    			    
    	    			    var savedCI;
    	    			    
    	    					
    	    					for (var i = 0; i < sPaymentMethodsLength; i++) {
    	    						var sPaymentMethod = sPaymentMethods.PaymentMethod[i];
    	    						
    	    						console.log("modelOutput sPaymentMethod == ",sPaymentMethod);
    	    						 var sPaymentMethodExtn = _scModelUtils.getStringValueFromPath("Extn", sPaymentMethod);
    	    						 console.log("modelOutput sPaymentMethodExtn values == ",sPaymentMethodExtn);
    	    						 console.log("modelOutput sPaymentMethodExtn.ExtnAurusCI values == ",sPaymentMethodExtn.ExtnAurusCI);
    	    						 console.log("modelOutput sPaymentMethodExtn.ExtnAurusOOT values == ",sPaymentMethodExtn.ExtnAurusOOT);
    	    						 
    	    						
    	    						var PaymentTypeGroup = sPaymentMethod.PaymentTypeGroup;
    	    						var creditCardNo = sPaymentMethod.CreditCardNo;
    	    						var displayCreditCardNo = sPaymentMethod.DisplayCreditCardNo;
    	    						var paymentReference3 = sPaymentMethod.PaymentReference3;
    	    						
    	    						console.log("modelOutput sPaymentMethod == ",PaymentTypeGroup);
    	    						console.log("modelOutput creditCardNo == ",creditCardNo);
    	    						console.log("modelOutput paymentReference3 == ",paymentReference3);
    	    						console.log("modelOutput displayCreditCardNo == ",displayCreditCardNo);
    	    						
    	    						if(_scBaseUtils.equals(cardNumber_attrPaymentMethod, paymentReference3)){
    	    							//crnCCNo=sPaymentMethodExtn.ExtnAurusCI;
    	    							
    	    							savedCI=sPaymentMethodExtn.ExtnAurusCI;  // this handle if ProcessOrderPayment failed and CI not generated.
    	    							crnCCNo=sPaymentMethodExtn.ExtnAurusCI+":"+displayCreditCardNo;
    	    							break;
    	    						}
    	    					}
//    	    					crnCCNo="mytestCardNo";
    	    					_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardNo",
    	    							crnCCNo,manageCustomerModel);
    	    					
    	    					var customerId=order.CustomerContactID;
    	    					var EnterpriseCode=order.EnterpriseCode;
    	    					var SellerOrganizationName=order.SellerOrganizationName;
    	    					
    	    					_scModelUtils.setStringValueAtModelPath("Customer.CustomerID",
    	    							customerId,manageCustomerModel);
    	    					_scModelUtils.setStringValueAtModelPath("Customer.EnterpriseCode",
    	    							EnterpriseCode,manageCustomerModel);
                    		
    	    					console.log("crnCCNo = "+crnCCNo);
//    	    					alert("crnCCNo"+crnCCNo);
    	    					
    	    					if(!(_scBaseUtils.isVoid(savedCI))){
    	    						this.callManageCustomer(manageCustomerModel);
    	    					}
                    	}
                		
        				
        				
        	}
            if (
            _scBaseUtils.equals(
            mashupRefId, "manageCustomer")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "manageCustomer_Output", modelOutput, null);
                }
 				manageCustomerModel=null;
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "computeRefundPayments")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "computeRefundPayments_Output", modelOutput, null);
                }
            }
			if(_scBaseUtils.equals(
            mashupRefId, "extn_getProperty_referenceid")){
				//console.log("getPropertyOut: ",modelOutput);
				// START - Importing session.js module 
				/*
				var jsElm = document.createElement("script");
				jsElm.type = "application/javascript";
				jsElm.src = _scModelUtils.getStringValueFromPath("GetProperty.PropertyValue", modelOutput);
				//console.log("Element",jsElm);						
				document.getElementsByTagName("head")[0].appendChild(jsElm);
				*/
				
				
				var sessionResponseCode =_scModelUtils.getStringValueFromPath("SessionResponse.ResponseCode", modelOutput);
				var sessionResponseError =_scModelUtils.getStringValueFromPath("SessionResponseError.Error", modelOutput);
				var sessionResponseText =_scModelUtils.getStringValueFromPath("SessionResponse.ResponseText", modelOutput);
				
				if (!_scBaseUtils.isVoid(sessionResponseCode)){				
//					alert("sessionResponseCode == "+sessionResponseCode);
					console.log("sessionResponseCode: ",sessionResponseCode);
					//_scScreenUtils.showErrorMessageBox(this,"Please click Add Payment again",null,null,null);
					if(_scBaseUtils.equals(sessionResponseCode, "00000")){
						aurusSessionResponse=modelOutput;
						//alert("modelOutput "+modelOutput);
						if(!(_scBaseUtils.isVoid(this.paymentcapturescreen))){
							//this.isCallCrmFlag=false;
							_scScreenUtils.setModel(this, "isCallCrmFlag", false, null);
							this.paymentcapturescreen.updatePaymentMethod();
						}
					}else{
						_scScreenUtils.showErrorMessageBox(this,"Please click Add Payment  again"+sessionResponseText,null,null,null);
					}
				}else if (!_scBaseUtils.isVoid(sessionResponseError)){
					_scScreenUtils.showErrorMessageBox(this,sessionResponseError,null,null,null);
				}
			
				//END - Importing session.js module
			}
			if(_scBaseUtils.equals(
            mashupRefId, "extn_applyCoupon_referenceid")){
				//console.log("extn_applyCoupon_referenceid",modelOutput);
				var changeOrderPromotionModel = _scBaseUtils.getTargetModel(this, "changeOrder_Input", null);
				//console.log("changeOrderPromotionModel",changeOrderPromotionModel);
				var isValid = _scModelUtils.getStringValueFromPath("Coupon.Valid", modelOutput);
				if(_scBaseUtils.equals("Y",isValid)){
					var promotionId = _scModelUtils.getStringValueFromPath("Coupon.CouponID", modelOutput);
					var displayLocalizedFieldInLocale = _scModelUtils.getStringValueFromPath("Coupon.DisplayLocalizedFieldInLocale", modelOutput);
					var extnCouponID = _scModelUtils.getStringValueFromPath("Order.Promotions.Promotion.PromotionId", changeOrderPromotionModel);
					//console.log("promotionId: "+promotionId+", extnCouponId:"+extnCouponID);
					_scModelUtils.setStringValueAtModelPath("Order.Promotions.Promotion.Action", "CREATE", changeOrderPromotionModel);
					_scModelUtils.setStringValueAtModelPath("Order.Promotions.Promotion.PromotionId", promotionId, changeOrderPromotionModel);
					_scModelUtils.setStringValueAtModelPath("Order.Promotions.Promotion.Extn.ExtnCouponID", extnCouponID, changeOrderPromotionModel);
					_scModelUtils.setStringValueAtModelPath("Order.BypassPricing", "N", changeOrderPromotionModel);
					_scModelUtils.setStringValueAtModelPath("Order.DisplayLocalizedFieldInLocale", displayLocalizedFieldInLocale, changeOrderPromotionModel);
					
					_scScreenUtils.setModel(
						this, "changeOrder_Input", changeOrderPromotionModel, null);
					//console.log("changeOrderPromotionModel",changeOrderPromotionModel);
					flag = true;
					_isccsUIUtils.callApi(this, changeOrderPromotionModel, "extn_changeOrder_referenceid");
				}else{
					_scScreenUtils.showErrorMessageBox(this,"Please enter a valid promotion code.",null,null,null);
					return;
				}
			}
			if(_scBaseUtils.equals(
            mashupRefId, "extn_changeOrder_referenceid")){
				//console.log("extn_changeOrder_referenceid",modelOutput);
				_scWidgetUtils.setValue(this,"txtCouponPromoCode","",true);
					var inputModel = {};
					inputModel.Order = {};
					_scModelUtils.addStringValueToModelObject("DisplayLocalizedFieldInLocale","en_US_EST", inputModel.Order);
					_scModelUtils.addStringValueToModelObject("OrderHeaderKey",modelOutput.Order.OrderHeaderKey, inputModel.Order);
					//console.log("inputModel",inputModel);
					_isccsUIUtils.callApi(
					this, inputModel, "getCompleteOrderDetails", null);
			}
			if(_scBaseUtils.equals(
            mashupRefId, "extn_getCompleteOrderDetails_referenceid")){
				//console.log("extn_getCompleteOrderDetails_referenceid",modelOutput);
				_scScreenUtils.setModel(
                    this, "paymentConfirmation_getCompleteOrderDetails_Output", modelOutput, null);
			}
			
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_ChangePaymentMethod_IssueeGiftCard")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
					_scScreenUtils.setModel(
                    this, "extn_ReturnOrderLines_SuspendAndAddeGiftCard_output", modelOutput, null);
                    _scWidgetUtils.disableWidget(this, "extn_issueRefundToEGiftCard", false);
                
				var giftCardModel = null;
				giftCardModel = _scScreenUtils.getModel(this, "extn_ReturnOrderLines_SuspendAndAddeGiftCard_output");
				
				console.log("This is in handle Mashup for giftCardModel",giftCardModel);
				
				var orderModel = null;
				orderModel = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
				
				var newOrderModel = null;
                newOrderModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
                var orderHeaderKey = null;
                orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel);
                _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, newOrderModel);
                _scModelUtils.setStringValueAtModelPath("Order.RetrieveDefaultCustomerPaymentMethod", "N", newOrderModel);
                _isccsUIUtils.callApi(
                this, newOrderModel, "getCompleteOrderDetails", null);
				
                }
            }
			
			//OMS-1161 : Start
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getCouponDetails_payment")) {
				var isCallSuccessfull = _scModelUtils.getStringValueFromPath("Coupon.IsCallSuccessfull", modelOutput);
				var promoID = _scModelUtils.getStringValueFromPath("Coupon.PromoId", modelOutput);
				if(!_scBaseUtils.isVoid(isCallSuccessfull) && _scBaseUtils.equals(isCallSuccessfull, "N") ){
								
				_scScreenUtils.showErrorMessageBox(this, "Entered Coupon is Invalid or Coupon Lookup is unavailable at this time.Please try again after sometime",null, null, null);
				}
				else{
				   var bindings = null;
				   bindings = {};
				   popupParams = {};
				   popupParams["screenInput"] = modelOutput;
				   var dialogParams = null;
				   dialogParams = {};
				   _scWidgetUtils.setValue(this, "txtCouponNo", "", true );
				  _isccsUIUtils.openSimplePopup("extn.home.portlets.CouponLookup", "Coupon Details", this, popupParams, dialogParams);
				}
            }
			
			//OMS-1161 : End
			
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
        	if(hasError && _scBaseUtils.equals(data.response.Errors.Error[0].ErrorCode,"EXTN_ERROR")){
				data.response.em = data.response.Errors.Error[0].ErrorDescription;
				}
            _isccsBaseTemplateUtils.handleMashupCompletion(
            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },


		/*Below is added as part of Returns Module 1B*/
		
		manageAdvanceReturn: function (event, bEvent, ctrl, args) {
		
		var vselectedLine = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output", null);
		var sOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", vselectedLine);
		var sOrderLines = _scModelUtils.getStringValueFromPath("Order.OrderLines", vselectedLine);
		var vReturnLine = _scScreenUtils.getTargetModel(this, "extn_changeOrderLineToAdvanceReturns_input", null);
		var sAdvanceReturns = _scModelUtils.getStringValueFromPath("Order.AdvanceReturns", vReturnLine);
		
		_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", sOrderHeaderKey , vReturnLine);
		
		vReturnLine.Order.OrderLines = {};
		vReturnLine.Order.OrderLines.OrderLine = [];
		
		var slength = sOrderLines.OrderLine.length;
		
		for (var j = 0; j < slength; j++) {
		var sOrderLine = sOrderLines.OrderLine[j];
		var sOrderLineKey = sOrderLine.OrderLineKey;
		vReturnLine.Order.OrderLines.OrderLine[j] = {};
		vReturnLine.Order.OrderLines.OrderLine[j].OrderLineKey = sOrderLineKey;
		
		
			if (_scBaseUtils.equals(sAdvanceReturns, "Y")) {
			vReturnLine.Order.OrderLines.OrderLine[j].ConditionVariable1 = "Advance Returns";
			} else {
			vReturnLine.Order.OrderLines.OrderLine[j].ConditionVariable1 = "";
			}
		
		}
		
		_isccsModelUtils.removeAttributeFromModel("Order.AdvanceReturns", vReturnLine);
		console.log("This is in ManageAdvanceReturn for final output",vReturnLine);
		_isccsUIUtils.callApi(this, vReturnLine, "extn_changeOrderLine", null);
		
	},
	initializeScreen: function(
        event, bEvent, ctrlId, args) {
            var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "paymentConfirmation_getCompleteOrderDetails_Output");
            this.showHidePromotionPanels(
            orderModel);
            var secureAuthMapModel = null;
            secureAuthMapModel = _isccsUIUtils.getWizardModel(
            this, "secureAuthMap");
            if (
            _scBaseUtils.isVoid(
            secureAuthMapModel)) {
                secureAuthMapModel = _scModelUtils.createNewModelObjectWithRootKey("SecureAuthMap");
                _isccsUIUtils.setWizardModel(
                this, "secureAuthMap", secureAuthMapModel, null);
            }
            _scScreenUtils.setModel(
            this, "capturePayment_Output", orderModel, null);
            this.checkDuplicatePaymentMethod(
            orderModel);
            this.callProcessOrderPayments(
            orderModel, null);
            this.callGetCompleteOrderDetails(
            orderModel);
            this.checkNewInvalidPromotion();
            this.updateDisplay(
            orderModel);
            this.updateStatusModification();
			
			var sOrderDocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", orderModel);
	
				if (_scBaseUtils.equals(sOrderDocumentType, "0003")) {
				
					this.updateDisplayforReturnsOrders();
					
					if (_scResourcePermissionUtils.hasPermission("VSIISSUEEGC")) {
						_scWidgetUtils.enableWidget(this, "extn_issueRefundToEGiftCard");
					} else {
						_scWidgetUtils.disableWidget(this, "extn_issueRefundToEGiftCard", false);
					}
				} else {
				
					_scWidgetUtils.hideWidget(this, "extn_Advance_returns", false);
					_scWidgetUtils.hideWidget(this, "extn_issueRefundToEGiftCard", false);
				
				}
			this.getSessionModule();
			
        },
        updateDisplayforReturnsOrders: function() {
		
			var orderModel = null;
            orderModel = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
			console.log("This is in Initialize Screen of updateDisplayforReturnsOrders for orderModel",orderModel);
			
			var sAdvanceReturns = null;
			sAdvanceReturns = orderModel.Order.OrderLines.OrderLine[0].ConditionVariable1;
			
			var sPaymentMethods = _scModelUtils.getStringValueFromPath("Order.PaymentMethods", orderModel);
			var sPaymentMethodlength = _scModelUtils.getStringValueFromPath("PaymentMethod.length",sPaymentMethods);
	
			_scWidgetUtils.hideWidget(this, "btnCreateExchange", false);		
			_scWidgetUtils.hideWidget(this, "lblSufficientFunds", false);	
			_scWidgetUtils.hideWidget(this, "btnAddPaymentMethod", false);	
			_scWidgetUtils.showWidget(this, "extn_Advance_returns", false, null);
			_scWidgetUtils.showWidget(this, "extn_issueRefundToEGiftCard", false, null);
			
			if (_scBaseUtils.equals(sAdvanceReturns, "Advance Returns")) {
	
			var advanceReturnsCheckbox = this.getWidgetByUId("extn_Advance_returns");
				advanceReturnsCheckbox.setValue('Value','Y');
			}
			
			var sPaymentType = null;
			var sOnlineGiftCardExist = false;
			
			if (!(_scBaseUtils.isVoid(sPaymentMethodlength))) {
				for (var j = 0; j < sPaymentMethodlength; j++) {
			
					sPaymentType = sPaymentMethods.PaymentMethod[j].PaymentType;
					sPaymentStatus = sPaymentMethods.PaymentMethod[j].SuspendAnyMoreCharges;
			
						if (!_scBaseUtils.equals(sPaymentType, "ONLINE_GIFT_CARD") && _scBaseUtils.equals("Y",sPaymentStatus)) {
							sOnlineGiftCardExist = true;
						}
				}
				
				if (_scBaseUtils.isBooleanTrue(sOnlineGiftCardExist)){
					_scWidgetUtils.disableWidget(this, "extn_issueRefundToEGiftCard", false);
				}
			}
			
		},
		
		
        updateDisplay: function(
        orderModel) {
        	console.log(orderModel);
            var sReturnDocumentType = "";
            sReturnDocumentType = _isccsOrderUtils.getReturnOrderDocumentType();
            var sOrderDocumentType = "";
            sOrderDocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", orderModel);
            var sOrderDraftFlag = "";
            sOrderDraftFlag = _scModelUtils.getStringValueFromPath("Order.DraftOrderFlag", orderModel);
            if (
            _scBaseUtils.equals(
            sOrderDocumentType, sReturnDocumentType)) {
                if (
                 _scBaseUtils.equals(
                sOrderDraftFlag, "Y")) {
                       
					   //Adding as part of Returns Module
					   
					   // _scWidgetUtils.showWidget(this, "btnCreateExchange", false, null);
					   _scWidgetUtils.hideWidget(this, "btnCreateExchange", false);
					   
                }
                this.isRefund = true;
                _scWidgetUtils.showWidget(
                this, "pnlReturnOrderTotal", false, null);
                _scScreenUtils.setModel(
                this, "returnOrderTotal_Output", orderModel, null);
                _scWidgetUtils.hideWidget(
                this, "pnlCodeApply", false);
                _scWidgetUtils.hideWidget(
                this, "pnlInvalidTitle", false);
                _scWidgetUtils.hideWidget(
                this, "pnlValidTitle", false);
            } else {
                if (
                _scModelUtils.hasModelObjectForPathInModel("Order.Return.Order", orderModel)) {
                    this.hasReturn = true;
                    _scScreenUtils.setModel(
                    this, "exchangeOrderTotal_Output", orderModel, null);
                    _scWidgetUtils.showWidget(
                    this, "pnlExchangeOrderTotal", true, null);
                    this.hasTransfer = true;
                    var returnOrderTotalModel = null;
                    returnOrderTotalModel = _scModelUtils.getModelObjectFromPath("Order.Return", orderModel);
                    _scScreenUtils.setModel(
                    this, "returnOrderTotal_Output", returnOrderTotalModel, null);
		           // var exchangeDraftStatus = "";
                   // exchangeDraftStatus = _scModelUtils.getStringValueFromPath("Order.DraftOrderFlag", orderModel);
				    var screenInput = _scScreenUtils.getInitialInputData(this);
				    var contextDocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", screenInput);
                    if (
                    _scBaseUtils.equals(sReturnDocumentType, contextDocumentType)) {
                        _scWidgetUtils.showWidget(
                        this, "pnlReturnOrderTotal", true, null);
                    }
                    var transferPaymentMethodModel = null;
                    transferPaymentMethodModel = _scModelUtils.getModelObjectFromPath("Order.Return", orderModel);
                    _scWidgetUtils.showWidget(
                    this, "paymentMethodPanel", false, null);
                    var eExchangeTypeAllowed = null;
                    eExchangeTypeAllowed = _scScreenUtils.getModel(
                    this, "getExchangeTypeAllowed_output");
                    if (!(
                    _scModelUtils.getBooleanValueFromPath("Rules.RuleSetValue", eExchangeTypeAllowed, false))) {
                        _scWidgetUtils.hideWidget(
                        this, "lnkExchangeType", false);
                    }
                    if (!(
                    _scBaseUtils.equals(sReturnDocumentType, contextDocumentType))) {
                        _scWidgetUtils.hideWidget(
                        this, "lnkExchangeType", false);
                    }
                    _scScreenUtils.setModel(
                    this, "transferPaymentMethod_Output", transferPaymentMethodModel, null);
                    var returnTotal = 0;
                    var grandRefundTotal = 0;
                    grandRefundTotal = _scModelUtils.getNumberValueFromPath("Order.Return.Order.OverallTotals.GrandRefundTotal", orderModel);
                    if (
                    grandRefundTotal > 0) {
                        if (
                        _scBaseUtils.equals(
                        sOrderDocumentType, sReturnDocumentType) || _scBaseUtils.equals(sReturnDocumentType, contextDocumentType)) {
                            this.isRefund = true;
                            _scScreenUtils.setModel(
                            this, "capturePayment_Output", transferPaymentMethodModel, null);
                        }
                    }
                } else {
                	console.log('Hiii');
                    _scScreenUtils.setModel(
                    this, "salesOrderTotal_Output", orderModel, null);
                    _scWidgetUtils.showWidget(
                    this, "contentSalesOrderTotal", true, null);
                }
            }
            if (
            _scBaseUtils.equals(
            this.isRefund, true)) {
                this.refundDisplay();
            }
            var displayAddPayment = false;
            displayAddPayment = _isccsUIUtils.displayAddPaymentMethods(
            this, orderModel);
            this.displayAddPayment = displayAddPayment;
            if (
            _scBaseUtils.equals(
            displayAddPayment, false)) {
                this.hidePaymentMethod();
            }
        },
		
		
	onEGiftCardButtonClick: function(event, bEvent, ctrl, args) {
		
		var onEGiftCardPopupMessage = _scScreenUtils.getString(this, "extn_E_Gift_Card_Popup_Returns");
			_scScreenUtils.showConfirmMessageBox(this,onEGiftCardPopupMessage,"handleEGiftCardReturnsPopUpResponse", null,null);
			
            
	},
	
	handleEGiftCardReturnsPopUpResponse: function(res){
	
	if (_scBaseUtils.equals(res, "Ok")) {
	
	
	var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "paymentConfirmation_getCompleteOrderDetails_Output");
		
			console.log("This is in onEGiftCardButtonClick after paymentConfirmation_getCompleteOrderDetails_Output ",orderModel);
		
			
				var sOrderHeaderKey = orderModel.Order.OrderHeaderKey;
				var sPaymentMethods = orderModel.Order.PaymentMethods;
				
				var listOfPaymentMethods = null;
                    listOfPaymentMethods = _scModelUtils.getModelListFromPath("Order.PaymentMethods.PaymentMethod", orderModel);
				var sPaymentMethodsLength = _scBaseUtils.getAttributeCount(listOfPaymentMethods);
				

				var changeOrderInput = {};
				changeOrderInput.Order = {};
				changeOrderInput.Order.OrderHeaderKey = sOrderHeaderKey;
				changeOrderInput.Order.PaymentMethods = {};
				changeOrderInput.Order.PaymentMethods.PaymentMethod = [];
				var sTotalPlannedRefundAmount=0.0; 
				var sOGCPaymentKey="";
				for (var i = 0; i < sPaymentMethodsLength; i++) {
					var sPaymentMethod = sPaymentMethods.PaymentMethod[i];
					var sSuspendAnyMoreCharges = sPaymentMethod.SuspendAnyMoreCharges;
					var sPaymentKey = sPaymentMethod.PaymentKey;
					var sPaymentType = sPaymentMethod.PaymentType;
					sTotalPlannedRefundAmount =sTotalPlannedRefundAmount+ _scModelUtils.getNumberValueFromPath("PlannedRefundAmount", sPaymentMethod, null);
					if (_scBaseUtils.equals("ONLINE_GIFT_CARD", sPaymentType)) {
						sOGCPaymentKey = sPaymentKey;
					}else{
						changeOrderInput.Order.PaymentMethods.PaymentMethod[i] = {};
						changeOrderInput.Order.PaymentMethods.PaymentMethod[i].PaymentKey = sPaymentKey;
						changeOrderInput.Order.PaymentMethods.PaymentMethod[i].SuspendAnyMoreCharges = "B";
						changeOrderInput.Order.PaymentMethods.PaymentMethod[i].PlannedRefundAmount = "0.0";
					}
				}
				
				
				
				//_isccsUIUtils.callApi(this, changeOrderInput, "extn_ChangePaymentMethod_IssueeGiftCard", null);
				
				
				 if (sTotalPlannedRefundAmount >= 0) {
					var listOfChangeOrderPaymentMethods = null;
	                listOfChangeOrderPaymentMethods = _scModelUtils.getModelListFromPath("Order.PaymentMethods.PaymentMethod", changeOrderInput);
	                var sPaymentMethodsLengthChangeOrder = 0;
					sPaymentMethodsLengthChangeOrder = _scBaseUtils.getAttributeCount(listOfChangeOrderPaymentMethods);
					
					changeOrderInput.Order.PaymentMethods.PaymentMethod[sPaymentMethodsLengthChangeOrder] = {};
					changeOrderInput.Order.PaymentMethods.PaymentMethod[sPaymentMethodsLengthChangeOrder].SuspendAnyMoreCharges = "N";
					changeOrderInput.Order.PaymentMethods.PaymentMethod[sPaymentMethodsLengthChangeOrder].PaymentType = "ONLINE_GIFT_CARD";
					changeOrderInput.Order.PaymentMethods.PaymentMethod[sPaymentMethodsLengthChangeOrder].PaymentKey = sOGCPaymentKey;
					changeOrderInput.Order.PaymentMethods.PaymentMethod[sPaymentMethodsLengthChangeOrder].PlannedRefundAmount = sTotalPlannedRefundAmount;
					
					console.log("This is in onEGiftCardButtonClick changeOrder input ",changeOrderInput);
					
					_isccsUIUtils.callApi(this, changeOrderInput, "extn_ChangePaymentMethod_IssueeGiftCard", null);
				 }
	
	}
	},
	
	
	
		save: function(
        event, bEvent, ctrl, args) {
			//alert("HERE");
            var confirmOrder = true;
            var screenInput = null;
            var apiInput = null;
            var orderHeaderKey = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
			var orderName = screenInput.Order.OrderName;
			//console.log("screenInput",screenInput);
            apiInput = {};
            orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", screenInput);
            var actionPerformed = null;
            actionPerformed = _scBaseUtils.getStringValueFromBean("Action", args);
			//console.log("actionPerformed",actionPerformed);
            if (
            _scBaseUtils.equals(
            actionPerformed, "CONFIRM") && !_scBaseUtils.equals(orderName,"MigratedOrder")) {
                var capturePayment = null;
                capturePayment = _scScreenUtils.getModel(
                this, "capturePayment_Output");
				console.log("capturePayment",capturePayment);
				var paymentStatus = _scModelUtils.getStringValueFromPath("Order.PaymentStatus", capturePayment);
                var remainingAmountToAuth = 0;				
                remainingAmountToAuth = _scModelUtils.getNumberValueFromPath("Order.ChargeTransactionDetails.RemainingAmountToAuth", capturePayment);
				console.log("paymentStatus",paymentStatus);
				//console.log("remainingAmountToAuth",remainingAmountToAuth);
                if ((!_scBaseUtils.equals(paymentStatus,"AUTHORIZED") && !_scBaseUtils.equals(paymentStatus,"PAID") && !_scBaseUtils.equals(paymentStatus,"INVOICED")) 
					&& remainingAmountToAuth > 0) {
                    _isccsBaseTemplateUtils.showMessage(
                    this, "AdditionaAmountNotZero", "error", null);
                    confirmOrder = false;
                } else {
                    var RealTimeRulesModel = null;
                    RealTimeRulesModel = _scScreenUtils.getModel(
                    this, "getRuleDetails_RealTimeAuthorization_Output");
                    var sRealTimeAuth = null;
                    sRealTimeAuth = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", RealTimeRulesModel);
                    if (
                    _scBaseUtils.equals(
                    sRealTimeAuth, "02")) {
                        var orderModel = null;
                        orderModel = _scScreenUtils.getModel(
                        this, "capturePayment_Output");
                        var listOfPaymentMethods = null;
                        listOfPaymentMethods = _scModelUtils.getModelListFromPath("Order.PaymentMethods.PaymentMethod", orderModel);
                        var length = 0;
                        length = _scBaseUtils.getAttributeCount(
                        listOfPaymentMethods);
                        var paymentMethodModel = null;
                        var secureAuthCodesModel = null;
                        secureAuthCodesModel = [];
                        var secureAuthMapModel = null;
                        secureAuthMapModel = _isccsUIUtils.getWizardModel(
                        this, "secureAuthMap");
                        var secureAuthMap = null;
                        secureAuthMap = _scModelUtils.getModelObjectFromPath("SecureAuthMap", secureAuthMapModel);
                        for (
                        var counter = 0;
                        counter < length;
                        counter = counter + 1) {
                            paymentMethodModel = listOfPaymentMethods[
                            counter];
                            _isccsModelUtils.removeAttributeFromModel("TotalCharged", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("TotalRefundedAmount", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("TotalAuthorized", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("CreditCardExpMonth", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("CreditCardExpYear", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("CreditCardTypeDesc", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("IsDefaultMethod", paymentMethodModel);
                            _isccsModelUtils.removeAttributeFromModel("Currency", paymentMethodModel);
                            var paymentMethodKey = null;
                            paymentMethodKey = _scBaseUtils.getAttributeValue("PaymentKey", false, paymentMethodModel);
                            var secureAuthCode = null;
                            secureAuthCode = _scBaseUtils.getAttributeValue(
                            paymentMethodKey, false, secureAuthMap);
                            if (!(
                            _scBaseUtils.isVoid(
                            secureAuthCode))) {
                                var tmpPaymentMethod = null;
                                tmpPaymentMethod = {};
                                _scModelUtils.setStringValueAtModelPath("PaymentKey", paymentMethodKey, tmpPaymentMethod);
                                _scModelUtils.setStringValueAtModelPath("SecureAuthenticationCode", secureAuthCode, tmpPaymentMethod);
                                _scModelUtils.addModelObjectToModelList(
                                tmpPaymentMethod, secureAuthCodesModel);
                            }
                        }
                        var newOrderModel = null;
                        newOrderModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
                        var exchangeOrderModel = null;
                        exchangeOrderModel = _scScreenUtils.getModel(
                        this, "exchangeOrderTotal_Output");
                        var ohkForAPI = orderHeaderKey;
                        if (!(
                        _scBaseUtils.isVoid(
                        exchangeOrderModel))) {
                            ohkForAPI = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", exchangeOrderModel);
                            _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, newOrderModel);
                            _isccsUIUtils.callApi(
                            this, newOrderModel, "processOrderPayments", null);
                        }
                        _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", ohkForAPI, newOrderModel);
                        if (!(
                        _scBaseUtils.isVoid(
                        secureAuthCodesModel))) {
                            _scModelUtils.addModelToModelPath("Order.PaymentMethods.PaymentMethod", secureAuthCodesModel, newOrderModel);
                        }
                        _isccsUIUtils.callApi(
                        this, newOrderModel, "processOrderPayments", null);
                        confirmOrder = false;
                    }
                }
            } else {
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
            if (
            _scBaseUtils.equals(
            confirmOrder, true)) {
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", null);
            }
        },
	checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
		console.log("PAYMENT");
        mCompleteOrdDetails = this.getModel("paymentConfirmation_getCompleteOrderDetails_Output");
		console.log("mCompleteOrdDetails: ", mCompleteOrdDetails);
		if (mCompleteOrdDetails.Order.Extn.ExtnIsMigrated === "Y") {
			_isccsBaseTemplateUtils.showMessage(this, warningMessage, "extn_migrated_order_message", null);
			
			_scWidgetUtils.disableWidget(this, "btnAddPaymentMethod", true);
			_scWidgetUtils.disableWidget(this, "btnApply", true);
			_scWidgetUtils.disableWidget(this, "lnkOrderViewModCharges", true);
			_scWidgetUtils.disableWidget(this, "paymentMethodHolder", true);
		}
	},
        callManageCustomer: function(
        paymentMethod) {
			var attrPaymentMethod = _scModelUtils.getModelObjectFromPath("PaymentMethod",paymentMethod);
			attrPaymentMethod.NewPaymentMethod = "Y";
			console.log("attrPaymentMethod",attrPaymentMethod);
		if(_scModelUtils.hasAttributeInModelPath("CreditCardExpMonth", attrPaymentMethod)){
		        _isccsModelUtils.removeAttributeFromModel("CreditCardExpMonth", attrPaymentMethod);
		}
		if(_scModelUtils.hasAttributeInModelPath("CreditCardExpYear", attrPaymentMethod)){
                _isccsModelUtils.removeAttributeFromModel("CreditCardExpYear", attrPaymentMethod);
		}

			var customerPaymentMethodsModel = {}; 
			customerPaymentMethodsModel = paymentMethod.CustomerPaymentMethodList;
			console.log("customerPaymentMethodsModel",customerPaymentMethodsModel);
			var listOfPaymentMethods = null;
            listOfPaymentMethods = _scModelUtils.getModelListFromPath("CustomerPaymentMethodList.CustomerPaymentMethod", customerPaymentMethodsModel);
            console.log("listOfPaymentMethods",listOfPaymentMethods);
            
//Logic for traversing and setting PaymentMethod
			
			var customerModel = null;
            customerModel = _scScreenUtils.getTargetModel(
            this, "manageCustomer_input", null);
			_scModelUtils.setStringValueAtModelPath("Customer.CustomerID",
					_scModelUtils.getStringValueFromPath("Customer.CustomerID", paymentMethod),customerModel);
			
			_scModelUtils.setStringValueAtModelPath("Customer.EnterpriseCode",
					_scModelUtils.getStringValueFromPath("Customer.EnterpriseCode", paymentMethod),customerModel);
            
          if (
            _scModelUtils.hasAttributeInModelPath("PaymentMethod.SaveBillToOnOrder", paymentMethod)) {
                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.SaveBillToOnOrder", paymentMethod);
            }
            var customerPaymentMethodModel = null;
            customerPaymentMethodModel = _scModelUtils.getModelObjectFromPath("PaymentMethod", paymentMethod);
            var paymentTypeGroup = null;
            paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentTypeGroup", customerPaymentMethodModel);
            var requestedAmount = null;
            requestedAmount = _scModelUtils.getStringValueFromPath("RequestedAmount", customerPaymentMethodModel);
            _isccsModelUtils.removeAttributeFromModel("RequestedAmount", customerPaymentMethodModel);
            _isccsModelUtils.removeAttributeFromModel("CheckNo", customerPaymentMethodModel);
            _isccsModelUtils.removeAttributeFromModel("PaymentKey", customerPaymentMethodModel);
            _isccsModelUtils.removeAttributeFromModel("PaymentTypeGroup", customerPaymentMethodModel);
            if (
            _scBaseUtils.equals(
            paymentTypeGroup, "OTHER")) {
                _scModelUtils.setStringValueAtModelPath("PaymentReference2", requestedAmount, customerPaymentMethodModel);
            }
            var customerPaymentMethod = null;
            customerPaymentMethod = {};
            var customerPaymentMethodList = null;
            customerPaymentMethodList = _scModelUtils.createModelListFromKey("CustomerPaymentMethod", customerPaymentMethod);
			
            _scModelUtils.addModelObjectToModelList(customerPaymentMethodModel, customerPaymentMethodList);
			if(_scBaseUtils.isVoid(listOfPaymentMethods)){
				listOfPaymentMethods = customerPaymentMethodList;
			}else{
				_scModelUtils.addModelObjectToModelList(attrPaymentMethod, listOfPaymentMethods);
			}
			
			
			var flag = false;
					var count = 0;
					var cardValueCount = 1;
					var finalListOfPayments = [];
					var listLength = listOfPaymentMethods.length;
					console.log("listLength",listLength);
					for(var i=0;i<listOfPaymentMethods.length;i++){
						var listOfPaymentMethod = listOfPaymentMethods[i];
						console.log("listOfPaymentMethod",listOfPaymentMethod);
						var expDate = _scModelUtils.getStringValueFromPath("CreditCardExpDate", listOfPaymentMethod);
					
						var creditCardNo1 = _scModelUtils.getStringValueFromPath("CreditCardNo", listOfPaymentMethod);
						var creditCardNoColon = creditCardNo1.indexOf(":");
						console.log("creditCardNo1 ",creditCardNo1);
						console.log("creditCardNoColon ",creditCardNoColon);
						
						if(creditCardNoColon<0){
							console.log("Inside colon");
							var displayCreditCardNo1 = _scModelUtils.getStringValueFromPath("DisplayCreditCardNo", listOfPaymentMethod);
							var crnCCNo=creditCardNo1+":"+displayCreditCardNo1;
							
							console.log("Inside colon crnCCNo "+crnCCNo);
							
							_scBaseUtils.setAttributeValue("CreditCardNo", crnCCNo, listOfPaymentMethod);
						}
						
						 var expDateSlash = expDate.indexOf("/");
						 if(expDateSlash>-1){
							var str_array = expDate.split('/');
							expDate = str_array[1]+str_array[0];
						}
						 
						 var expMonth;
						 var expYear;
						 if(_scBaseUtils.equals(expDate.length,3)){
							 expMonth = expDate.substring(0,1);
							 expYear = expDate.substring(1);
							 if(_scBaseUtils.equals(expMonth.length,1)){
									expMonth = "0"+expMonth;
							}
							if(_scBaseUtils.equals(expYear.length,2)){
								 expYear = "20"+expYear;
							}
							 expDate=expYear+expMonth;
						 }
						 if(_scBaseUtils.equals(expDate.length,4)){
							 expMonth = expDate.substring(0,2);
							 expYear = expDate.substring(2);
							 if(_scBaseUtils.equals(expMonth.length,1)){
								 expMonth = "0"+expMonth;
							}
							if(_scBaseUtils.equals(expYear.length,2)){
								 expYear = "20"+expYear;
							}
							 expDate=expYear+expMonth;
						 }
				 			
				 			/*
				 			var expMonth = "5";
							var expYear = "20"+21;
							if(_scBaseUtils.equals(expMonth.length,1)){
								expMonth = "0"+expMonth;
							}
							*/
				 			
						console.log("expDate = "+expDate);
//				 	    var creditCardExpDate = cardExpiry; 
						 

						_scBaseUtils.setAttributeValue("CreditCardExpDate", expDate, listOfPaymentMethod);
						var date = _scBaseUtils.getDate();
						var date1 = _scBaseUtils.convertToServerFormat(date,"DATE");
						var time = _scBaseUtils.convertToServerFormat(date,"TIME");
						var dateTime = date1+time;
						_scBaseUtils.setAttributeValue("CreditCardCreatets", dateTime, listOfPaymentMethod);
							
						var codeValue = "CARD"+cardValueCount;
						console.log("codeValue",codeValue);		
						if(_scModelUtils.hasAttributeInModelPath("IsDefaultMethod", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("IsDefaultMethod", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("PaymentTypeDesc", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("PaymentTypeDesc", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("CreditCardTypeDesc", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("CreditCardTypeDesc", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("PaymentTypeGroup", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("PaymentTypeGroup", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("CreditCardName", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("CreditCardName", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("DisplayCustomerAccountNo", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("DisplayCustomerAccountNo", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("DisplayPaymentReference1", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("DisplayPaymentReference1", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("DisplayPrimaryAccountNo", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("DisplayPrimaryAccountNo", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("MiddleName", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("MiddleName", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("PrimaryAccountNo", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("PrimaryAccountNo", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("AvailableAccountBalance", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("AvailableAccountBalance", listOfPaymentMethod);
						}
						if(_scModelUtils.hasAttributeInModelPath("CustomerPaymentMethodKey", listOfPaymentMethod)){
							_isccsModelUtils.removeAttributeFromModel("CustomerPaymentMethodKey", listOfPaymentMethod);
						}
												
						var strCode = _scModelUtils.getStringValueFromPath("Code",listOfPaymentMethod);
						var strNewPaymentMethod = _scModelUtils.getStringValueFromPath("NewPaymentMethod",listOfPaymentMethod);
						if(listLength > 3 && flag == false /*&& _scBaseUtils.equals("CARD1",strCode)*/){
							//delete listOfPaymentMethod;
							//_scBaseUtils.setAttributeValue("RecordState", "Modified", listOfPaymentMethod);
							flag = true; 
						}else if(listLength > 3 && _scBaseUtils.equals("Y",strNewPaymentMethod)){
							_scBaseUtils.setAttributeValue("Code", codeValue, listOfPaymentMethod);
							_scBaseUtils.setAttributeValue("RecordState", "Modified", listOfPaymentMethod);
							finalListOfPayments[count] = listOfPaymentMethod;
							count++;
							cardValueCount++;
						}else if(_scBaseUtils.equals("Y",strNewPaymentMethod)){
							_scBaseUtils.setAttributeValue("Code", codeValue, listOfPaymentMethod);
							_scBaseUtils.setAttributeValue("RecordState", "Added", listOfPaymentMethod);
							finalListOfPayments[count] = listOfPaymentMethod;
							count++;
							cardValueCount++;
						}else{
							_scBaseUtils.setAttributeValue("Code", codeValue, listOfPaymentMethod);
							_scBaseUtils.setAttributeValue("RecordState", "Modified", listOfPaymentMethod);
							finalListOfPayments[count] = listOfPaymentMethod;
							count++;
							cardValueCount++;
						}
					}
					console.log("finalListOfPayments",finalListOfPayments);
			if(!_scBaseUtils.isVoid(finalListOfPayments)){
				_scModelUtils.addListToModelPath("Customer.CustomerPaymentMethodList.CustomerPaymentMethod",finalListOfPayments,customerModel);
			}else{
				return;
			}
			
			_scModelUtils.setStringValueAtModelPath("Customer.HasCustomerAttributes", "Y", customerModel);
			_scModelUtils.setStringValueAtModelPath("Customer.SavePayment", "Y", customerModel);
			_scModelUtils.setStringValueAtModelPath("Customer.ExcludeCustomer", "Y", customerModel);
			_scModelUtils.setStringValueAtModelPath("Customer.CustomerKey", _scModelUtils.getStringValueFromPath("Customer.CustomerID", customerModel),customerModel );
			_scModelUtils.setStringValueAtModelPath("Customer.OrganizationCode", _scModelUtils.getStringValueFromPath("Customer.EnterpriseCode", customerModel),customerModel );
			
			 
            _isccsUIUtils.callApi(
            this, customerModel, "manageCustomer", null);
        },
		       applyPaymentMethod: function(
        actionPerformed, model, popupParams) {
            if (
            _scBaseUtils.equals("APPLY", actionPerformed)) {
            	/* Start Defect 472971 */
            	var totalCharged = 0;
                totalCharged = _scModelUtils.getNumberValueFromPath("screenInput.Order.PaymentMethod.TotalCharged", popupParams);
                if (
                totalCharged > 0) {
                    var actualMaxChargeLimit = 0;
                    var amntToChargeValue = 0;
                    amntToChargeValue = _scModelUtils.getNumberValueFromPath("PaymentMethod.RequestedAmount", model);
                    if (
                    amntToChargeValue >= 0) {
                        actualMaxChargeLimit = _isccsOrderUtils.addQty(
                        totalCharged, amntToChargeValue);
                        _scModelUtils.setNumberValueAtModelPath("PaymentMethod.RequestedAmount", actualMaxChargeLimit, model);
                    }
                }
                /* End Defect 472971 */
                var orderModel = null;
                orderModel = _scScreenUtils.getModel(
                this, "capturePayment_Output");
                var listOfPaymentMethods = null;
                listOfPaymentMethods = _scModelUtils.getModelListFromPath("Order.PaymentMethods.PaymentMethod", orderModel);
                var length = 0;
                length = _scBaseUtils.getAttributeCount(
                listOfPaymentMethods);
                this.prevPaymentCount = length;
                var paymentTypeGroup = null;
                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", model);
                var clonedModel = null;
                if (
                _scBaseUtils.equals(
                paymentTypeGroup, "CREDIT_CARD")) {
                    clonedModel = _scBaseUtils.cloneModel(
                    model);
                    _scScreenUtils.setModel(
                    this, "newPaymentMethod", clonedModel, null);
                    _isccsModelUtils.removeAttributeFromModel("PaymentMethod.SecureAuthenticationCode", model);
                }
                var saveAgainstCustomer = null;
                if (
                _scModelUtils.hasAttributeInModelPath("PaymentMethod.SaveAgainstCustomer", model)) {
                    saveAgainstCustomer = _scModelUtils.getStringValueFromPath("PaymentMethod.SaveAgainstCustomer", model);
                    _isccsModelUtils.removeAttributeFromModel("PaymentMethod.SaveAgainstCustomer", model);
                    if (
                    _scBaseUtils.equals(
                    saveAgainstCustomer, "Y")) {
                        clonedModel = _scBaseUtils.cloneModel(
                        model);
console.log("clonedModel",clonedModel);
						delete model.CustomerPaymentMethodList;
                        
					manageCustomerModel=clonedModel;
                       // this.callManageCustomer(clonedModel);
                    }
                }
                this.callCapturePayment(
                model);
            }
        },
		
		updateStatusModification: function() {
            var statusModTrue = false;
            var orderModel = null;
            orderModel = _scScreenUtils.getModel(
            this, "paymentConfirmation_getCompleteOrderDetails_Output");
            var listOfStatusModifications = null;
            listOfStatusModifications = _scModelUtils.getModelListFromPath("Order.Modifications.Modification", orderModel);
            var length = 0;
            length = _scBaseUtils.getAttributeCount(
            listOfStatusModifications);
            var modification = null;
            for (
            var counter = 0;
            counter < length;
            counter = counter + 1) {
                modification = listOfStatusModifications[
                counter];
                var modificationType = "";
                var modificationAllowed = "";
                modificationType = _scModelUtils.getStringValueFromPath("ModificationType", modification);
                modificationAllowed = _scModelUtils.getStringValueFromPath("ModificationAllowed", modification);
                if (
                _scBaseUtils.equals(
                modificationType, "PRICE") && _scBaseUtils.equals(
                modificationAllowed, "N")) {
                    _scWidgetUtils.disableWidget(
                    this, "lnkOrderViewModCharges", false);
                    _scWidgetUtils.disableWidget(
                    this, "lnkReturnViewModCharges", false);
                    _scWidgetUtils.disableWidget(
                    this, "lnkExchangeViewModCharges", false);
                    statusModTrue = true;
                } else if (
                _scBaseUtils.equals(
                modificationType, "CHANGE_PROMOTION") && _scBaseUtils.equals(
                modificationAllowed, "N")) {
                    _scWidgetUtils.disableWidget(
                    this, "txtCouponPromoCode", false);
					  _scWidgetUtils.disableWidget(
                    this, "extn_datetextbox_PromotionCodeDateOverride", false);
                    _scWidgetUtils.disableWidget(
                    this, "btnApply", false);
                    statusModTrue = true;
                } else if (
                _scBaseUtils.equals(
                modificationType, "PAYMENT_METHOD") && _scBaseUtils.equals(
                modificationAllowed, "N")) {
                    _scWidgetUtils.disableWidget(
                    this, "btnAddPaymentMethod", false);
                    statusModTrue = true;
                }
            }
            if (
            statusModTrue) {
                _isccsBaseTemplateUtils.showMessage(
                this, "StatusModification", "warning", null);
            }
        },
        updateEditorHeader: function(
                event, bEvent, ctrl, args) {
                    _isccsBaseTemplateUtils.updateCustomerMessage(
                    this, "CUST_PaymentConfirmation", true);
                    _isccsBaseTemplateUtils.updateTitle(
                    this, "TITLE_PaymentConfirmation", null);
                    _scScreenUtils.focusFirstEditableWidget(
                    this);
                    console.log('after screen load');
                    var orderReprice = null;
			        orderReprice = _scScreenUtils.getModel(this, "paymentConfirmation_getCompleteOrderDetails_Output");
			        this.updateDisplay(orderReprice);
                    return true;
        },
		updateRemainingAmountToAuth: function(
			modelOutput) {
            var PaymentMethodsModel = null;
            PaymentMethodsModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethods");
            _scModelUtils.addModelToModelPath("Order.PaymentMethods", _scModelUtils.getModelObjectFromPath("Order.PaymentMethods", modelOutput), PaymentMethodsModel);
            _scModelUtils.setStringValueAtModelPath("Order.ChargeTransactionDetails.RemainingAmountToAuth", _scModelUtils.getStringValueFromPath("Order.ChargeTransactionDetails.RemainingAmountToAuth", modelOutput), PaymentMethodsModel);
			_scModelUtils.setStringValueAtModelPath("Order.PaymentStatus", _scModelUtils.getStringValueFromPath("Order.PaymentStatus", modelOutput), PaymentMethodsModel);
            _scScreenUtils.setModel(
            this, "capturePayment_Output", PaymentMethodsModel, null);
            this.updateDisplay(
            modelOutput);
        },
		         showSufficientFunds: function(
        dataValue, screen, widget, namespace, modelObject, options) {
console.log("modelObject",modelObject);
			var paymentStatus =_scModelUtils.getStringValueFromPath("Order.PaymentStatus", modelObject);
            var returnValue = "";
            if (
            _scBaseUtils.equals(
            this.isRefund, false)) {
                var remainingAmountToAuth = 0;
                remainingAmountToAuth = _scModelUtils.getNumberValueFromPath("Order.ChargeTransactionDetails.RemainingAmountToAuth", modelObject);
                console.log("remainingAmountToAuth",remainingAmountToAuth);
                if (
                _scBaseUtils.lessThanOrEqual(
                remainingAmountToAuth, 0) && _scBaseUtils.equals(
                this.isRefund, false) 
                && (_scBaseUtils.equals(paymentStatus,"AUTHORIZED") || _scBaseUtils.equals(paymentStatus,"PAID") || _scBaseUtils.equals(paymentStatus,"INVOICED"))) {
                    returnValue = _scScreenUtils.getString(
                    this, "SufficientFunds");
                    _scWidgetUtils.hideWidget(
                    this, "lblRemainingCharge", false);
                    _scWidgetUtils.showWidget(
                    this, "lblSufficientFunds", false, null);
                } else {
                    _scWidgetUtils.hideWidget(
                    this, "lblSufficientFunds", false);
                    _scWidgetUtils.showWidget(
                    this, "lblRemainingCharge", false, null);
                    _scWidgetUtils.showWidget(
                    this, "btnAddPaymentMethod", false, null);
                }
            }
            return returnValue;
        }
		//OMS-1161 : Start
		,
		findCoupon: function(
        event, bEvent, ctrl, args) {
            if (
            _scScreenUtils.isValid(
            this, "changeOrder_Input")) {
                var changeOrderModel = null;
                changeOrderModel = _scBaseUtils.getTargetModel(
                this, "changeOrder_Input", null);
                var promotionId = null;
                promotionId = _scModelUtils.getStringValueFromPath("Order.Promotions.Promotion.PromotionId", changeOrderModel);
                if (
                _scBaseUtils.isVoid(
                promotionId)) {
				_scScreenUtils.showErrorMessageBox(this,"Enter a valid PromoCode",null, null, null);
                } else {
					var inputModel = {};
					inputModel.Coupon = {};
					_scModelUtils.addStringValueToModelObject("CouponID",promotionId, inputModel.Coupon);
					_isccsUIUtils.callApi(this, inputModel, "extn_getCouponDetails_payment");
                }
            }
        }
		
		//OMS-1161 : End
});
}); 