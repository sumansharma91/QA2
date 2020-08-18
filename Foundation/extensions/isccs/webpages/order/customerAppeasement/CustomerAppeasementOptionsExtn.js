
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/customerAppeasement/CustomerAppeasementOptionsExtnUI",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/GridxUtils",	
	"scbase/loader!isccs/utils/ContextUtils",
	"scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils",
	"scbase/loader!isccs/utils/WidgetUtils"
]
,
function(			 
	_dojodeclare,
	_extnCustomerAppeasementOptionsExtnUI,
	_scWidgetUtils,
	_scBaseUtils,
	_scScreenUtils,
	_scModelUtils,
	_isccsUIUtils,
	_scEventUtils,
	_scGridxUtils,
	_isccsContextUtils,
	_scResourcePermissionUtils,
	_isccsBaseTemplateUtils,
	_isccsWidgetUtils
){ 
	return _dojodeclare("extn.order.customerAppeasement.CustomerAppeasementOptionsExtn", [_extnCustomerAppeasementOptionsExtnUI],{
	// custom code here
	//
	OrderNo: "",
	//
		//OOB mathod which functionality split in custom class as per requirement
		save: function(event, bEvent, ctrl, args) {
            var selectedAppeasementOffer = null;
            selectedAppeasementOffer = this.getSelectedAppeasementOffer();
			console.log("selectedAppeasementOffer>>>>",selectedAppeasementOffer);
            selectedAppeasementOffer = _scBaseUtils.cloneModel(selectedAppeasementOffer);
					
			//Check the applied appeasement amount is valid or not and based on that take action 
			this.appeasementValidation(selectedAppeasementOffer);			
        },
		appeasementValidation : function(selectedAppeasementOffer) {
			//Code Added For 1B devlopment - Start
			var appeasementUEOutput = null;
			console.log("selectedAppeasementOffer", selectedAppeasementOffer);
            appeasementUEOutput = this.getModel("getAppeasementOffersUE_output");
			console.log("appeasementUEOutput", appeasementUEOutput);
			var appeasementOffers = _scModelUtils.getModelObjectFromPath("InvokeUE.XMLData.AppeasementOffers", appeasementUEOutput);
			console.log("appeasementOffers", appeasementOffers);
			var appeasementUEInput = null;
            appeasementUEInput = _scScreenUtils.getTargetModel(this, "getAppeasementUE_input", null);
			console.log("appeasementUEInput", appeasementUEInput);
			
			//get the Order No from AppeasementOfferUE output model
			this.OrderNo = _scModelUtils.getStringValueFromPath("OrderNo", appeasementOffers);
			// get the max appeasementallowedamt and maxAlpineAllowedAmount from the selected Appeasment Offer Model
			var maxAppeasementAllowedAmount = _scModelUtils.getNumberValueFromPath("Order.MaxAppeasementAllowedAmount",selectedAppeasementOffer);
			var maxAlpineAllowedAmount = _scModelUtils.getNumberValueFromPath("Order.MaxAlpineAllowedAmount",selectedAppeasementOffer);
			var alpineUserAppeaseLimit = _scModelUtils.getNumberValueFromPath("Order.AlpineUserAppeaseLimit",selectedAppeasementOffer);
			
			console.log("offerAmount", offerAmount);
			console.log("maxAppeasementAllowedAmount", maxAppeasementAllowedAmount);
			console.log("maxAlpineAllowedAmount", maxAlpineAllowedAmount);
				
			var selectedAppeasementOfferType = _scModelUtils.getStringValueFromPath("OfferType", selectedAppeasementOffer);
			if(_scBaseUtils.equals(selectedAppeasementOfferType, "VARIABLE_AMOUNT_ORDER")
				|| _scBaseUtils.equals(selectedAppeasementOfferType, "VARIABLE_PERCENT_AMOUNT_ORDER")){
				
				var offerAmount = 0;
				if(_scBaseUtils.equals(selectedAppeasementOfferType, "VARIABLE_PERCENT_AMOUNT_ORDER")){
				var discountPercent	= _scModelUtils.getNumberValueFromPath("Appeasement.DiscountPercent", appeasementUEInput);
				//var orderTotal = _scModelUtils.getNumberValueFromPath("Order.TempTotalAmount",selectedAppeasementOffer);
				offerAmount = maxAppeasementAllowedAmount * discountPercent/100 ;
				}else if(_scBaseUtils.equals(selectedAppeasementOfferType, "VARIABLE_AMOUNT_ORDER")){
					offerAmount = _scModelUtils.getNumberValueFromPath("Appeasement.OfferAmount", appeasementUEInput);
				}
				
				// Condition Check based on selected Appeasment ReasonCode
				if(offerAmount > maxAppeasementAllowedAmount){
					var balanceAmount = offerAmount - maxAppeasementAllowedAmount ;
					console.log("balanceAmount", balanceAmount);
					var errorMessage = "Appeasements provided cannot exceed from maxAppeasementAllowedLimit. Excess amount of "+ (balanceAmount.toFixed(2));
					_isccsBaseTemplateUtils.showMessage(this, errorMessage, "error", null);
				}else {
					//If Alpine User applied invalid appeasement amount more then ALPINE_APPEASE_LIMIT:($99)
					if( _scResourcePermissionUtils.hasPermission("VSIALPINERESTR")&& (offerAmount >= maxAlpineAllowedAmount)){
						//If invalid amount then- open alpine user popup
						console.log("AlpineUser>>>>");
						var alpineUserInfoString = "You do not have permission to issue appeasements for more than "+ alpineUserAppeaseLimit.toFixed(2)+".If this is required, please click OK below.  This will create an alert for a supervisor to create this appeasement.  Click cancel to change the amount.";
						
						//handleAlpineUserInfoPopUpResponse method to handle the Alpine User Pop Up behaviour.
						_scScreenUtils.showConfirmMessageBox(this,alpineUserInfoString,"handleAlpineUserInfoPopUpResponse", null,null);
					}
					//Valid Appeasment Amount apllied by any User
					else{
						//If valid appeasment amount provided - open E-Gift Card Popup
						//Prorate the Offer Appeasment Amount at OrderLine by calling prorateAppeasmentAmountOnLines method
						this.prorateAppeasmentAmountOnLines(selectedAppeasementOffer,offerAmount);
						
						//fetch customerEMailID from the context
						var customerEmailID = _isccsContextUtils.getFromContext("customerEMailID");
						var giftCardInfoString = "An e-Gift card with value of $"+offerAmount.toFixed(2)+" will be sent to "+customerEmailID+".Press OK to continue, Cancel to change the amount.";
						
						//pass the selectedAppeasementOffer model to complete the save button functionality in handleGiftCardPopUpResponse method.
						_scScreenUtils.showConfirmMessageBox(this,giftCardInfoString,"handleGiftCardPopUpResponse", null,selectedAppeasementOffer);
					}
				}
			}//end of if appeasmentOfferType Check
			//Code Added For 1B devlopment - End
		},
		// This custome method will prorate the applied valid appeasment offer amount on lines.
		prorateAppeasmentAmountOnLines: function(selectedAppeasementOffer,offerAmount){
			var orderLineList = _scModelUtils.getModelListFromPath("OrderLines.OrderLine", selectedAppeasementOffer);
			if(!_scBaseUtils.isVoid(orderLineList)){
				var totalAppeasementProvidedOnLine = 0;
				var lineCount = _scBaseUtils.getAttributeCount(orderLineList);
				for (var i = 0; i < lineCount; i = i + 1) {
					var orderLine = orderLineList[i];
					var strTempTotalAmount = _scModelUtils.getNumberValueFromPath("TempTotalAmount", orderLine);
					var strTempTotalLineAmount = _scModelUtils.getNumberValueFromPath("TempTotalLineAmount", orderLine);	
					//console.log("strTempTotalAmount", strTempTotalAmount);	
					//console.log("strTempTotalLineAmount", strTempTotalLineAmount);					
					var offerAmountOnLine = strTempTotalLineAmount * offerAmount/strTempTotalAmount;		
					offerAmountOnLine = Number(offerAmountOnLine.toFixed(2));					
					if((lineCount-1) == i){
						offerAmountOnLine = offerAmount - totalAppeasementProvidedOnLine;
					}else{	
						totalAppeasementProvidedOnLine = totalAppeasementProvidedOnLine + offerAmountOnLine;
					}
					_scModelUtils.setStringValueAtModelPath("LineOfferAmount", offerAmountOnLine, orderLine);
				}// end of orderLineList for loop 
			}
		},
		// Custom giftCardPopUphandler method trigger once Gift Card Pop Up is displayed on screen. If Ok is Selected on Pop Up,
		// It triggers the OOB functionality(override from save method).		
		handleAlpineUserInfoPopUpResponse: function(res){
			if (_scBaseUtils.equals(res, "Ok")) {
				// Call Mashup for raise the Alert and move to order summary page
				var createAppeasementApproveAlertInput = _scModelUtils.createNewModelObjectWithRootKey("Order");
				var appeasementUEInput = _isccsUIUtils.getWizardModel(this, "appeaseOrderUE_input");
				var order = _scModelUtils.getModelObjectFromPath("InvokeUE.XMLData.AppeasementOffers.Order", appeasementUEInput);
				
				var orderHeaderKey = _scModelUtils.getStringValueFromPath("OrderHeaderKey", order);
				var enterpriseCode = _scModelUtils.getStringValueFromPath("EnterpriseCode", order);
				
				_scModelUtils.setStringValueAtModelPath("Order.InboxType", "VSI_USER_APPEASEMENT_THRESHOLD", createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.OrderNo", this.OrderNo, createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", enterpriseCode, createAppeasementApproveAlertInput);
				_isccsUIUtils.callApi(this, createAppeasementApproveAlertInput, "extn_VSICreateAppeasementApprovalAlert", null);				
			}
		},
		onExtnMashupCompletion: function(dataValue, screen, widget, nameSpace, model){               
			var mashUpRefId = nameSpace.mashupContext.mashupArray[0].mashupRefId;
			if(_scBaseUtils.equals(mashUpRefId, "extn_VSICreateAppeasementApprovalAlert")){
				_scEventUtils.fireEventToParent(this, "onSaveSuccess", null);
			}
		},
		
		handleMashupCompletion: function(
		        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) 
		{
			if(hasError)
			{
				data.response.em = data.response.Errors.Error[0].ErrorDescription;
			}
		    _isccsBaseTemplateUtils.handleMashupCompletion(
		            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
		},
		        
		//Custom giftCardPopUphandler method trigger once Gift Card Pop Up is displayed on screen. If Ok is Selected on Pop Up,
		// It triggers the OOB functionality(override from save method).		
		handleGiftCardPopUpResponse: function(res,selectedAppeasementOffer){
			if (_scBaseUtils.equals(res, "Ok")) {
				var noteModel = null;
                noteModel = _scScreenUtils.getTargetModel(this, "createNotes_input", null);
                var appeasementModel = null;
                appeasementModel = _scScreenUtils.getTargetModel(this, "getAppeasementUE_input", null);

                var discountPercent	= _scModelUtils.getNumberValueFromPath("Appeasement.DiscountPercent", appeasementModel);
                console.log(discountPercent);
				if(!isNaN(discountPercent))
				{
					var maxAppeasementAllowedAmount = _scModelUtils.getNumberValueFromPath("Order.MaxAppeasementAllowedAmount",selectedAppeasementOffer);
					offerAmount = maxAppeasementAllowedAmount * discountPercent/100 ;
					//appeasementUEInput.Appeasement.OfferAmount=;
					//alert('Hiii');
					_scModelUtils.setNumberValueAtModelPath("Appeasement.OfferAmount", offerAmount.toFixed(2), appeasementModel);	
				}

				console.log("this.OrderNo", this.OrderNo);
				console.log(appeasementModel);
				//return;
                _scModelUtils.addModelToModelPath("Appeasement.AppeasementOffer", selectedAppeasementOffer, appeasementModel);
                var orderModel = null;
                orderModel = _scModelUtils.getModelObjectFromPath("InvokeUE.XMLData.AppeasementOffers.Order", _isccsUIUtils.getWizardModel(
                this, "appeaseOrderUE_input"));
                _scModelUtils.setStringValueAtModelPath("Order.ModificationReasonCode", _scModelUtils.getStringValueFromPath("AppeasementReason.ReasonCode", orderModel), noteModel);
                _scModelUtils.addModelToModelPath("Appeasement.Order", orderModel, appeasementModel);
				_scModelUtils.setStringValueAtModelPath("Appeasement.Order.OrderNo", this.OrderNo, appeasementModel);
				console.log("appeasementModel>>>>>>>>", appeasementModel);
                var offerType = null;
                offerType = _scModelUtils.getStringValueFromPath("Appeasement.AppeasementOffer.OfferType", appeasementModel);
                var isFuture = null;
                isFuture = _scModelUtils.getStringValueFromPath("Appeasement.AppeasementOffer.IsFuture", appeasementModel);
                _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", _scModelUtils.getStringValueFromPath("Appeasement.Order.OrderHeaderKey", appeasementModel), noteModel);				
                var mashupRefIdList = null;
                mashupRefIdList = [];
                var mashupInputModelList = null;
                mashupInputModelList = [];
                mashupInputModelList.push(appeasementModel);
                mashupInputModelList.push(noteModel);
                if (_scBaseUtils.equals(isFuture, "Y")) {
                    var isVariable = null;
                    isVariable = _scModelUtils.getStringValueFromPath("Appeasement.AppeasementOffer.IsVariable", appeasementModel);
                    if (_scBaseUtils.equals(isVariable, "Y")) {
                        _scModelUtils.setStringValueAtModelPath("Appeasement.AppeasementOffer.Description", _scScreenUtils.getString(
                        this, "FutureOrderDesc"), appeasementModel);
                    }
                    mashupRefIdList.push("sendFutureOrderCustomerAppeasementUE");
                } else {
                    mashupRefIdList.push("recordInvoiceCreation");
                }
                mashupRefIdList.push("changeOrder");
				console.log("mashupRefIdList", mashupRefIdList);
				_isccsUIUtils.callApis(this, mashupInputModelList, mashupRefIdList, null, null);
            }//end of if ok selected
		}
});
});

