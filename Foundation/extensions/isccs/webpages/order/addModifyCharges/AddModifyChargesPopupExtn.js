
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/order/addModifyCharges/AddModifyChargesPopupExtnUI",
		"scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/UIUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare, _extnAddModifyChargesPopupExtnUI, _isccsBaseTemplateUtils, _isccsUIUtils, _scBaseUtils, _scModelUtils, _scResourcePermissionUtils, _scScreenUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.order.addModifyCharges.AddModifyChargesPopupExtn", [_extnAddModifyChargesPopupExtnUI],{
	
	onApply: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            var LineCharges = null;
            var OrderHeaderKey = null;
            var options = null;
            var headerCharge = null;
            var count = 0;
			var countTax = 0;
			var totalHeaderCharge = 0;
			var alpineValidationAmt = 0;
            options = {};
            options["dynamicRepeatingPanel"] = true;
            targetModel = _scBaseUtils.getTargetModel(
            this, "getCompleteOrderDetails_input", options);
			console.log("targetModel: ", targetModel);
            headerCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", targetModel);
            count = _scBaseUtils.getAttributeCount(headerCharge);
			console.log("count: ", count);
			
			var alpineModel = _scScreenUtils.getModel(this, "extn_Alpine_Validation_output");
			alpineValidationAmt = alpineModel.CommonCodeList.CommonCode[0].CodeShortDescription;
			
			var alpinePopupMessage1 = _scScreenUtils.getString(this, "extn_Alpine_Charges_First");
			var alpinePopupMessage2 = _scScreenUtils.getString(this, "extn_Alpine_Charges_Second");
			var alpinePopupMessage = alpinePopupMessage1 + alpineValidationAmt + alpinePopupMessage2;
			
			// JIRA 53 - BEGIN
			orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			console.log("orderModel *** : ", orderModel);
			
			// JIRA 53 - END
			
            if ( _scBaseUtils.equals(count, 0)) {
                _isccsBaseTemplateUtils.showMessage(
                this, "NoChargeError", "error", null);
            } else {
                if ( _scBaseUtils.equals(this.screenMode, "LineMode")) {
					
					var taxChargeName = null;
					var taxChargeCat = null;
					var taxChargeAmt = 0;
					var strExtnOriginalTrackingNo=null;
					var orderType = null;
					var entryType = null;
					orderType = _scModelUtils.getStringValueFromPath("Order.OrderType", orderModel);
					entryType = _scModelUtils.getStringValueFromPath("Order.EntryType", orderModel);
					console.log("orderType111 : ", orderType);
					console.log("entryTypeType111 : ", entryType);
					for (i = 0; i < count; i++) {
						taxChargeName = headerCharge[i].ChargeName;
						taxChargeCat = headerCharge[i].ChargeCategory;
						strExtnOriginalTrackingNo = headerCharge[i].ExtnOriginalTrackingNo;
						//OMS-1211 : start
						if (_scBaseUtils.equals(taxChargeCat,"Adjustments") && _scBaseUtils.equals(taxChargeName, "Shipping Appeasement")) {
									var popupMessage = _scScreenUtils.getString(this, "extn_Shipping_Line_Error");
									_scScreenUtils.showErrorMessageBox(this, popupMessage,null, null, null);
									return false;
						}
						
						console.log("orderType000 : ", orderType);
						console.log("entryTypeType000 : ", entryType);
						if ((_scBaseUtils.equals(orderType, "WHOLESALE") || _scBaseUtils.equals(entryType, "WHOLESALE")) && (_scBaseUtils.equals(taxChargeName, "Shipping Appeasement")  || _scBaseUtils.equals(taxChargeName, "Shipping")) ){
									var popupMessage1 = _scScreenUtils.getString(this, "extn_WH_Shipping_Line_Error");
									_scScreenUtils.showErrorMessageBox(this, popupMessage1,null, null, null);
									return false;
						}
						//OMS-1211 : End
						if (_scBaseUtils.equals(taxChargeCat,"Adjustments") && _scBaseUtils.equals(taxChargeName,"Tax issue") ){
							taxChargeAmt = headerCharge[i].ChargeAmount;
				
						}
						if(!_scBaseUtils.isVoid(strExtnOriginalTrackingNo)){
							var eleExtn = _scModelUtils.getStringValueFromPath("Order.Extn", targetModel);
							console.log("eleExtn",eleExtn);
							if(_scBaseUtils.isVoid(eleExtn)){
								targetModel.Order.Extn = {};
							}
							targetModel.Order.Extn.ExtnOriginalTrackingNo = strExtnOriginalTrackingNo;
							delete headerCharge[i].ExtnOriginalTrackingNo;
						}
					}
					
					computedPrice = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine.ComputedPrice", orderModel);
					
					// ARE-66 : Modified to show allowed amount in error message : BEGIN
					console.log("headerCharge: ", headerCharge);
					console.log("taxChargeAmt: ", taxChargeAmt);
					console.log("computedPrice: ", computedPrice);
					
					// ARH-84 : Modified to compare the tax amount calculated without the adjustments : BEGIN
					//var tax = _scModelUtils.getNumberValueFromPath("Tax", computedPrice);
					var tax = 0.00;
					var lineTax = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine.LineTaxes.LineTax", orderModel);
					var count2 = _scBaseUtils.getAttributeCount(lineTax);
					for (i = 0; i < count2; i++) {
						taxAmount = lineTax[i].Tax;
						if (!_scBaseUtils.isVoid(taxAmount)) {
							if (!_scBaseUtils.equals(lineTax[i].TaxName, "Adjusted Sales Tax")) {
								tax = tax + _scModelUtils.getNumberValueFromPath("Tax", lineTax[i]);
							}
						}
					}
					// ARH-84 : Modified to compare the tax amount calculated without the adjustments : BEGIN
					
					if(Math.round(taxChargeAmt * 100) > Math.round(tax * 100)){
						var TaxIssueChargesPopupMessage = _scScreenUtils.getString(this, "extn_Tax_issue");
						_scScreenUtils.showErrorMessageBox(this, TaxIssueChargesPopupMessage + tax.toFixed(2),null, null, null);
						// ARE-66 : Modified to show allowed amount in error message : END
					
					} else {
						// JIRA 53 - BEGIN
						// Sum line charges
						var totalLineCharge = 0;
						console.log("*& headerCharge: ", headerCharge);
						for (i = 0; i < count; i++) {
							console.log("ChargeName:::",headerCharge[i].ChargeName);
							if (!_scBaseUtils.equals(headerCharge[i].ChargeCategory, "Replacement charge")
									&& !_scBaseUtils.equals(headerCharge[i].ChargeName, "Tax issue")) {
								totalLineCharge = totalLineCharge + headerCharge[i].ChargeAmount;
							}
							//console.log("loop totalLineCharge: ", totalLineCharge);
							//console.log("loop headerCharge[i].ChargeAmount: ", headerCharge[i].ChargeAmount);
						}
						
						// If line discounts exceed the order total, display an error
						
						var lineChargesPopupMessage = _scScreenUtils.getString(this, "extn_Line_Level_Charges");
						
						// ARE-66 : Modified to show allowed amount in error message : BEGIN
						console.log("totalLineCharge: ", totalLineCharge);
						console.log("computedPrice: ", computedPrice);
						var extendedPrice = _scModelUtils.getNumberValueFromPath("ExtendedPrice", computedPrice);
						console.log("ExtendedPrice: ", extendedPrice)
						console.log("a: ",Math.round(parseFloat(totalLineCharge)*100)/100);
						console.log("b: ",Math.round(parseFloat(extendedPrice)*100)/100);
						if(Math.round(totalLineCharge * 100) > Math.round(extendedPrice * 100)){
							_scScreenUtils.showErrorMessageBox(this, lineChargesPopupMessage  + extendedPrice.toFixed(2),null, null, null);
						// ARE-66 : Modified to show allowed amount in error message : END
						
						} else if ( _scResourcePermissionUtils.hasPermission("VSIALPINERESTR") && (Math.round(totalLineCharge * 100) > Math.round(alpineValidationAmt * 100))) {
							_scScreenUtils.showConfirmMessageBox(this,alpinePopupMessage,"handleAlpineUserInfoPopUpResponse", null,null);
						} else {
							var manageChargeDetails = null;
							manageChargeDetails = _scBaseUtils.getAttributeValue("scEditorInput", false, this);
							var OrderLineKey = null;
							OrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", manageChargeDetails);
							_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", OrderHeaderKey, targetModel);
							OrderLineKey = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine.OrderLineKey", manageChargeDetails);
							_scModelUtils.setStringValueAtModelPath("Order.HeaderCharges.OrderLineKey", OrderLineKey, targetModel);
							
							_isccsUIUtils.callApi(this, targetModel, "extn_prorateLineCharges", null);
						}
						// JIRA 53 - END
					
					} // End of Line mode screen
					
                } else {
					var taxChargeName = null;
					var taxChargeCat = null;
					var taxChargeAmt = 0;
					var shippingAppeasChargeAmt = 0;
					var strExtnOriginalTrackingNo = null;
					
					var overallTotal = null;
					overallTotal = _scModelUtils.getModelListFromPath("Order.OverallTotals", orderModel);
					
					for (i = 0; i < count; i++) {
						taxChargeName = headerCharge[i].ChargeName;
						taxChargeCat = headerCharge[i].ChargeCategory;
						strExtnOriginalTrackingNo = headerCharge[i].ExtnOriginalTrackingNo;
					
						if (_scBaseUtils.equals(taxChargeCat,"Adjustments") && _scBaseUtils.equals(taxChargeName,"Tax issue") ){
							taxChargeAmt = headerCharge[i].ChargeAmount;
						}
						if(!_scBaseUtils.isVoid(strExtnOriginalTrackingNo)){
							//OMS-1031 Modification : Start
							var eleExtn = _scModelUtils.getStringValueFromPath("Order.Extn", targetModel);
							console.log("eleExtn",eleExtn);
							if(_scBaseUtils.isVoid(eleExtn)){
                                         targetModel.Order.Extn = {};
							}
							targetModel.Order.Extn.ExtnOriginalTrackingNo = strExtnOriginalTrackingNo;
							delete headerCharge[i].ExtnOriginalTrackingNo;
							//OMS-1031 Modification : End
						}
						
						if (_scBaseUtils.equals(taxChargeCat,"Adjustments") && _scBaseUtils.equals(taxChargeName,"Shipping Appeasement") ){
							shippingAppeasChargeAmt = headerCharge[i].ChargeAmount;
						}
					}
					
					var totalTaxAmt = 0;
					if (!_scBaseUtils.isVoid(overallTotal)) {
						totalTaxAmt = overallTotal.GrandTax - overallTotal.HdrTax;
					}
					
					// ARE-66 : Modified to show allowed amount in error message : BEGIN
					var hdrCharges = _scModelUtils.getNumberValueFromPath("HdrCharges", overallTotal);
					console.log("totalTaxAmt: ", totalTaxAmt);
					if(Math.round(taxChargeAmt * 100)> Math.round(totalTaxAmt * 100)){
						var TaxIssueChargesPopupMessage = _scScreenUtils.getString(this, "extn_Tax_issue");
						
						_scScreenUtils.showErrorMessageBox(this,TaxIssueChargesPopupMessage + totalTaxAmt.toFixed(2),null, null, null);
					} else if(Math.round(shippingAppeasChargeAmt * 100) > Math.round(hdrCharges * 100)){
						var ShippingAppeaseChargesPopupMessage = _scScreenUtils.getString(this, "extn_Shipping_Apease");
						_scScreenUtils.showErrorMessageBox(this,ShippingAppeaseChargesPopupMessage + hdrCharges.toFixed(2),null, null, null);
						// ARE-66 : Modified to show allowed amount in error message : END
					
					} else {
						// JIRA 53 - BEGIN
						// Sum header charges
						var totalHeaderCharge = 0;
						console.log("THIS headerCharge", headerCharge);
						console.log("overallTotal", overallTotal);
						//OMS-1096
						for (i = 0; i < count; i++) {
							if (!_scBaseUtils.equals(headerCharge[i].ChargeCategory, "Replacement charge")
								&& !_scBaseUtils.equals(headerCharge[i].ChargeCategory, "Shipping")
								&& !_scBaseUtils.equals(headerCharge[i].ChargeName, "Shipping Appeasement")) {
								totalHeaderCharge = totalHeaderCharge + headerCharge[i].ChargeAmount;
							}
						}
						
						// If header discounts exceed the order total, display an error
						
						var headerChargesPopupMessage = _scScreenUtils.getString(this, "extn_Header_Level_Charges");
						
						// ARE-66 : Modified to show allowed amount in error message : BEGIN
						if (!_scBaseUtils.isVoid(overallTotal)) {
							//OMS-1096
							var grandTotalMinusTaxAndShip = overallTotal.GrandTotal - overallTotal.GrandTax - (overallTotal.GrandShippingTotal-shippingAppeasChargeAmt);
						}
						console.log("totalHeaderCharge: ", totalHeaderCharge);
						console.log("grandTotalMinusTaxAndShip: ", grandTotalMinusTaxAndShip);
						console.log("alpineValidationAmt: ", alpineValidationAmt);
						//OMS-935 :BEGIN
						var dReturnsGrandTotal=0.00;
						var hasDerivedChild =_scModelUtils.getStringValueFromPath("Order.HasDerivedChild", orderModel);
						if(!_scBaseUtils.isVoid(hasDerivedChild) && (_scBaseUtils.equals(hasDerivedChild, "Y")) ){
							var returnOrder = _scModelUtils.getModelListFromPath("Order.ReturnOrders.ReturnOrder", orderModel);
							var returnCount = _scBaseUtils.getAttributeCount(returnOrder);
							for (iCounter = 0; iCounter < returnCount; iCounter++) {
									var returnTotal=_scModelUtils.getNumberValueFromPath("SubtotalWithoutTaxes", returnOrder[iCounter].OverallTotals);
									if(!_scBaseUtils.isVoid(returnTotal)){
										dReturnsGrandTotal=dReturnsGrandTotal+returnTotal;
									}
							}
						}

						var totalAllowed=grandTotalMinusTaxAndShip-dReturnsGrandTotal;

						if(Math.round(totalHeaderCharge * 100)>Math.round(totalAllowed * 100)){
							_scScreenUtils.showErrorMessageBox(this, headerChargesPopupMessage + totalAllowed.toFixed(2),null, null, null);
							// ARE-66 : Modified to show allowed amount in error message : END
							//OMS-935 :END
						} else if ( _scResourcePermissionUtils.hasPermission("VSIALPINERESTR") && (Math.round(totalHeaderCharge * 100) > Math.round(alpineValidationAmt * 100))) {
							_scScreenUtils.showConfirmMessageBox(this,alpinePopupMessage,"handleAlpineUserInfoPopUpResponse", null,null);
						} else {
							OrderHeaderKey = _scBaseUtils.getAttributeValue("params.scEditorInput.Order.OrderHeaderKey", false, this);
							_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", OrderHeaderKey, targetModel);
							
							// call service to calculate and save prorated line charges
							_isccsUIUtils.callApi(this, targetModel, "extn_prorateLineCharges", null);
						}
						// JIRA 53 - END
					}
				}
            }
        },
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "extn_VSICreateAppeasementApprovalAlert")) {
                this.handleChangeOrder(
                modelOutput);
            }			
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_prorateLineCharges")) {
                this.handleChangeOrder(
                modelOutput);
            }
			if (
            _scBaseUtils.equals(
            mashupRefId, "lineCharges_changeOrder")) {
                this.handleChangeOrder(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "headerCharges_changeOrder")) {
                this.handleChangeOrder(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderDetails")) {
                this.handleGetCompleteOrderDetails(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCompleteOrderLineDetails")) {
                this.handleGetCompleteOrderLineDetails(
                modelOutput);
            }
        },
		handleAlpineUserInfoPopUpResponse: function(res){
			if (_scBaseUtils.equals(res, "Ok")) {
				// Call Mashup for raise the Alert and move to Manage Charges page
				var createAppeasementApproveAlertInput = _scModelUtils.createNewModelObjectWithRootKey("Order");
				
				var orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
				var orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", orderModel);
				var enterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", orderModel);
				
				_scModelUtils.setStringValueAtModelPath("Order.InboxType", "VSI_USER_APPEASEMENT_THRESHOLD", createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.OrderNo", this.OrderNo, createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", orderHeaderKey, createAppeasementApproveAlertInput);
				_scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", enterpriseCode, createAppeasementApproveAlertInput);
				_isccsUIUtils.callApi(this, createAppeasementApproveAlertInput, "extn_VSICreateAppeasementApprovalAlert", null);				
			}
		},
        handleChangeOrder: function(
        modelOutput) {
			// ARE-66 : Modified to avoid error when header charge amount is $0.00 : BEGIN
			console.log("Validating this....", modelOutput);
			var headerCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", modelOutput);
			if (!_scBaseUtils.isVoid(headerCharge)) {
				var headerChargeArr = _scBaseUtils.getNewArrayInstance();
				delete modelOutput.Order.HeaderCharges.HeaderCharge;
				headerCharge.forEach(function(hdrChrg, i) {
					if (!_scBaseUtils.isVoid(hdrChrg.ChargeAmount) && hdrChrg.ChargeAmount != 0){
						console.log("*** appending... ", hdrChrg);
						_scBaseUtils.appendToArray(headerChargeArr, headerCharge[i]);
					}
				});
				_scModelUtils.addListToModelPath("Order.HeaderCharges.HeaderCharge", headerChargeArr, modelOutput);
			}
			// ARE-66 : Modified to avoid error when header charge amount is $0.00 : END
			
            _scScreenUtils.setModel(
            this, "getCompleteOrderDetails_output", modelOutput, null);
            if (
            _scScreenUtils.validate(
            this)) {
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
            }
        },
		validateChargeAmount: function (chargeName, chargeAmount, chargeField) {
			// ARE-66 : Method added to validate charge amount
			console.log("updateChargeTotal chargeName: ", chargeName);
			console.log("updateChargeTotal chargeAmount: ", chargeAmount);
			
			var options = {};
            options["dynamicRepeatingPanel"] = true;
			var targetModel = _scBaseUtils.getTargetModel(
            this, "getCompleteOrderDetails_input", options);
			console.log("targetModel: ", targetModel);
            var headerCharge = _scModelUtils.getModelListFromPath("Order.HeaderCharges.HeaderCharge", targetModel);
			
			console.log("chargeName.value", chargeName.value);
			console.log("headerCharge:", headerCharge);
			var orderModel = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output");
			console.log("orderModel *** : ", orderModel);
			
			if (!_scBaseUtils.equals(chargeName.value, "Tax issue")) {
				var totalLineCharge = 0;
				var count = _scBaseUtils.getAttributeCount(headerCharge);
				for (i = 0; i < count; i++) {
					var hdrChargeAmount = headerCharge[i].ChargeAmount;
					if (!_scBaseUtils.isVoid(hdrChargeAmount)
						&& (!(_scBaseUtils.equals(headerCharge[i].ChargeCategory, "Replacement charge")
								&& !_scBaseUtils.equals(headerCharge[i].ChargeName, "Tax issue")))) {
						totalLineCharge = totalLineCharge + headerCharge[i].ChargeAmount;
						console.log("loop totalLineCharge: ", totalLineCharge);
						console.log("loop headerCharge[i].ChargeAmount: ", headerCharge[i].ChargeAmount);
					}
				}
				
				var extendedPrice = _scModelUtils.getNumberValueFromPath("Order.OrderLines.OrderLine.ComputedPrice.ExtendedPrice", orderModel);
				var returnLineTotal = _scModelUtils.getNumberValueFromPath("Order.OrderLines.OrderLine.ComputedPrice.ReturnLineTotal", orderModel);
				
				var lineChargesPopupMessage = _scScreenUtils.getString(this, "extn_Line_Level_Charges");
				console.log("chargeName", chargeName);
				var amountField = chargeField.getWidgetByUId("txt_chargeAmount");
				console.log("amountField", amountField);
				console.log("extendedPrice:", extendedPrice);
				if(!_scBaseUtils.equals(chargeName.value, "Replacement charge")
					&& (Math.round(totalLineCharge * 100)> Math.round((extendedPrice-returnLineTotal) * 100))
					&& !(amountField.readOnly)) {
					var dValidAmount = extendedPrice - (totalLineCharge - chargeAmount)-returnLineTotal;
					_scScreenUtils.showErrorMessageBox(this, lineChargesPopupMessage  + dValidAmount.toFixed(2),null, null, null);
					return false;
				} else {
					return true;
				}
			} else {
				// ARH-84 : Added to compare Tax issue adjustments to only the current tax on the line : BEGIN
				console.log("IN ELSE");
				var totalTax = 0.00;
				var lineTax = _scModelUtils.getModelListFromPath("Order.OrderLines.OrderLine.LineTaxes.LineTax", orderModel);
				console.log("lineTax", lineTax);
				var count2 = _scBaseUtils.getAttributeCount(lineTax);
				console.log("count2: ", count2);
				for (i = 0; i < count2; i++) {
					console.log("i: ", i);
					taxAmount = lineTax[i].Tax;
					console.log("loop lineTax[i].Tax: ", lineTax[i].Tax);
					if (!_scBaseUtils.isVoid(taxAmount)) {
						if (!_scBaseUtils.equals(lineTax[i].TaxName, "Adjusted Sales Tax")) {
							totalTax = totalTax + _scModelUtils.getNumberValueFromPath("Tax", lineTax[i]);
							console.log("loop totalTax: ", totalTax);
						}
					}
				}
				
				if(Math.round(chargeAmount * 100) > Math.round(totalTax * 100)) {
					var taxIssuePopupMessage = _scScreenUtils.getString(this, "extn_Tax_issue");
					_scScreenUtils.showErrorMessageBox(this, taxIssuePopupMessage  + totalTax.toFixed(2),null, null, null);
					return false;
				} else {
					return true;
				}
				// ARH-84 : Added to compare Tax issue adjustments to only the current tax on the line : END
			}
		},
        handleGetCompleteOrderLineDetails: function(
        modelOutput) {
            var inputLineModel = null;
            inputLineModel = _scModelUtils.createNewModelObjectWithRootKey("Order");
            var OrderHeaderKey = null;
            OrderHeaderKey = _scModelUtils.getStringValueFromPath("OrderLine.Order.OrderHeaderKey", modelOutput);
            var EnterpriseCode = null;
            EnterpriseCode = _scModelUtils.getStringValueFromPath("OrderLine.Order.EnterpriseCode", modelOutput);
            var EntryType = _scModelUtils.getStringValueFromPath("OrderLine.Order.EntryType", modelOutput);
            var OrderType = _scModelUtils.getStringValueFromPath("OrderLine.Order.OrderType", modelOutput);
         
            var DocumentType = null;
            DocumentType = _scModelUtils.getStringValueFromPath("OrderLine.Order.DocumentType", modelOutput);
            _scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", OrderHeaderKey, inputLineModel);
            _scModelUtils.setStringValueAtModelPath("Order.DocumentType", DocumentType, inputLineModel);
            _scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", EnterpriseCode, inputLineModel);
   	    _scModelUtils.setStringValueAtModelPath("Order.EntryType", EntryType, inputLineModel);
            _scModelUtils.setStringValueAtModelPath("Order.OrderType", OrderType, inputLineModel);
         
            _scModelUtils.addModelToModelPath("Order.OrderLines", modelOutput, inputLineModel);
			
			// ARH-84 : Added to display Tax issue in Add/Modify Charges popup, if it exists : BEGIN
			console.log("THIS modelOutput: ", modelOutput);
			var lineTax = _scModelUtils.getModelListFromPath("OrderLine.LineTaxes.LineTax", modelOutput);
			var lineCharge = _scModelUtils.getModelListFromPath("OrderLine.LineCharges.LineCharge", modelOutput);
			console.log("lineTax", lineTax);
			console.log("lineCharge", lineCharge);
			var count = _scBaseUtils.getAttributeCount(lineTax);
			console.log("count: ", count);
			for (i = 0; i < count; i++) {
				console.log("i: ", i);
				console.log("loop lineTax[i].Tax: ", lineTax[i].Tax);
				if (_scBaseUtils.equals(lineTax[i].TaxName, "Adjusted Sales Tax")) {
					newLineCharge = {};
					newLineCharge.ChargeAmount = _scModelUtils.getStringValueFromPath("Tax", lineTax[i]);
					newLineCharge.ChargeCategory = "Adjustments";
					newLineCharge.ChargeCategoryDescription = "Adjustments";
					newLineCharge.ChargeName = "Tax issue";
					newLineCharge.ChargeNameDescription = "Tax issue";
					if (_scBaseUtils.isVoid(lineCharge)) {
						console.log("FIRST");
						var lineChargeArr = _scBaseUtils.getNewArrayInstance();
						_scBaseUtils.appendToArray(lineChargeArr, newLineCharge);
						_scModelUtils.addListToModelPath("OrderLine.LineCharges.LineCharge", lineChargeArr, modelOutput);
					} else {
						console.log("SECOND");
						_scBaseUtils.appendToArray(lineCharge, newLineCharge);
					}
				}
			}
			// ARH-84 : Added to display Tax issue in Add/Modify Charges popup, if it exists : END
			
            _scScreenUtils.setModel(
            this, "getCompleteOrderDetails_output", inputLineModel, null);
            var currencyInfo = null;
            currencyInfo = _scModelUtils.createNewModelObjectWithRootKey("Order");
            var temp = null;
            temp = {};
            temp = _scModelUtils.getModelObjectFromPath("OrderLine.Order.PriceInfo", modelOutput);
            _scModelUtils.addModelToModelPath("Order.PriceInfo", temp, currencyInfo);
            _scScreenUtils.setModel(
            this, "currencyModel_output", currencyInfo, null);
			
            if (
            _scBaseUtils.isVoid(
            _scBaseUtils.getAttributeValue("OrderLine.LineCharges.LineCharge", false, modelOutput))) {
				console.log("IF is void");
                this.handleAddCharges(
                null, null, null, null);
            }
        }
});
});
