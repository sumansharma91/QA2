
scDefine(["scbase/loader!dojo/_base/declare",
	      "scbase/loader!extn/common/paymentCapture/PaymentCaptureExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/OrderUtils", 
		  "scbase/loader!isccs/utils/PaymentUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/UIUtils"
		  ]
,
function( _dojodeclare,
		  _extnPaymentCaptureExtnUI,
		  _isccsBaseTemplateUtils, 
		  _isccsModelUtils, 
		  _isccsOrderUtils, 
		  _isccsPaymentUtils, 
		  _isccsWidgetUtils, 
		  _scBaseUtils, 
		  _scGridxUtils,
		  _scModelUtils, 
		  _scScreenUtils, 
		  _scWidgetUtils,
		  _isccsUIUtils
){
	var tnsResponse = null;
	var personInfoBillToModel = null;
	var executed = false;
	var selectedPaymentModel=null;
	return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtn", [_extnPaymentCaptureExtnUI],{
	// custom code here
	initializeScreen: function(event, bEvent, ctrl, args) {
		
		var parentScreen = _isccsUIUtils.getParentScreen(this, false);
		parentScreen.paymentcapturescreen=null;
	
		var screenInput = null;
        screenInput = _scScreenUtils.getInitialInputData(this);
		console.log("screenInput",screenInput);
		personInfoBillToModel = _scModelUtils.getStringValueFromPath("Order.PersonInfoBillTo",screenInput);
        _scScreenUtils.setModel(this, "paymentCapture_output", screenInput, null);
        var sourceModel = null;
        sourceModel = _scScreenUtils.getModel(this, "paymentCapture_output");

		mPaymentTypeList = _scScreenUtils.getModel(this, "getPaymentTypeList_Output");
		// ARE-423 : If DeliveryMethod="PICK", remove AR Credit, cash and check payment types : BEGIN
		var delMethod = _scModelUtils.getStringValueFromPath("Order.DeliveryMethod", screenInput);
		if (_scBaseUtils.equals(delMethod, "PICK")) {
			var arrPaymentTypes = _scBaseUtils.getNewArrayInstance();
			var listOfPaymentTypes = _scModelUtils.getModelListFromPath("PaymentTypeList.PaymentType", mPaymentTypeList);
			listOfPaymentTypes.forEach( function(paymentType, i) {
				if (!_scBaseUtils.equals(paymentType.PaymentType, "AR_CREDIT")
					&& !_scBaseUtils.equals(paymentType.PaymentType, "CASH")
					&& !_scBaseUtils.equals(paymentType.PaymentType, "CHECK"))
					_scBaseUtils.appendToArray(arrPaymentTypes, paymentType);
			});
			_scModelUtils.addListToModelPath("PaymentTypeList.PaymentType", arrPaymentTypes, mPaymentTypeList);
            _scScreenUtils.setModel(this, "getPaymentTypeList_Output", mPaymentTypeList, null);
		}
		// ARE-423 : If DeliveryMethod="PICK", remove AR Credit, cash and check payment types : END

        if (_scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", sourceModel, false)) {
            _scWidgetUtils.showWidget(this, "pnlPaymentCapture", false, null);
            _scWidgetUtils.enableReadOnlyWidget(this, "cmbPaymentType");
            this.handlePaymentTypeChange(event, bEvent, ctrl, args);
        } else {
            var customerPaymentMethodListModel = null;
            customerPaymentMethodListModel = _scScreenUtils.getModel(this, "getCustomerPaymentMethodList_Output");
            var newPaymentListModel = {};
            newPaymentListModel.CustomerPaymentMethodList = customerPaymentMethodListModel.Customer.CustomerPaymentMethodList;
            console.log("newPaymentListModel",newPaymentListModel);
            _scScreenUtils.setModel(
                    this, "getCustomerPaymentMethodList_Output", newPaymentListModel, null);
            customerPaymentMethodListModel = _scScreenUtils.getModel(this, "getCustomerPaymentMethodList_Output");
            console.log("customerPaymentMethodListModel",customerPaymentMethodListModel);
            var listOfPaymentMethods = null;
            listOfPaymentMethods = _scModelUtils.getModelListFromPath("CustomerPaymentMethodList.CustomerPaymentMethod", customerPaymentMethodListModel);
            var paymentMethodCount = 0;
            paymentMethodCount = _scBaseUtils.getAttributeCount(listOfPaymentMethods);
            if (paymentMethodCount > 0) {
                _scWidgetUtils.showWidget(this, "pnlSavedPaymentMethods", false, null);
                _scWidgetUtils.showWidget(this, "lnkNewPaymentMethod", false, null);
                _scGridxUtils.selectRowUsingUId(this, "tblSavedPaymentMethods", 0);
            } else {
              this.newPaymentMethod(event, bEvent, ctrl, args);
            }
        }
    },
    newPaymentMethod: function(
            event, bEvent, ctrl, args) {
                _scWidgetUtils.hideWidget(
                this, "pnlSavedPaymentMethods", false);
                _scWidgetUtils.hideWidget(
                this, "lnkNewPaymentMethod", false);
                var sourceModel = null;
                sourceModel = _scScreenUtils.getModel(
                this, "paymentCapture_output");
                var paymentTypesList = null;
                var defaultPaymentType = null;
                paymentTypesList = _scScreenUtils.getModel(
                this, "getPaymentTypeList_Output");
                defaultPaymentType = _isccsPaymentUtils.getPaymentMethodToDefault(
                paymentTypesList);
                _scModelUtils.setStringValueAtModelPath("Order.PaymentMethod.PaymentType", defaultPaymentType, sourceModel);
                _scScreenUtils.setModel(
                this, "paymentCapture_output", sourceModel, null);
                this.handlePaymentTypeChange(
                event, bEvent, ctrl, args);
                _scWidgetUtils.setWidgetMandatory(
                this, "cmbPaymentType");
                _scWidgetUtils.showWidget(
                this, "pnlPaymentCapture", false, null);
            },
	getCommonCode: function(){
		//removed the code
	},
	handlePaymentTypeChange: function(
        event, bEvent, ctrl, args) {
            _scWidgetUtils.enableWidget(
            this, "Popup_btnNext");
            var sourceModel = null;
            sourceModel = _scScreenUtils.getModel(
            this, "paymentCapture_output");
            var editPaymentMethod = null;
            editPaymentMethod = _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", sourceModel, false);
            var mashupRefBean = null;
            var listPaymentType = null;
            _scWidgetUtils.destroyWidget(
            this, "scrnPaymentCapture");
            var paymentCaptureModel = null;
            paymentCaptureModel = _scBaseUtils.getTargetModel(
            this, "paymentCaptureTypeModel", null);
    		if (!(_scBaseUtils.isVoid(paymentCaptureModel))) {
    			 var paymentType = null;
    	            var paymentTypeGroup = null;
    	            paymentType = _scBaseUtils.getValueFromPath("paymentCapture.paymentType", paymentCaptureModel);
    	            var paymentTypeListModel = null;
    	            paymentTypeListModel = _scScreenUtils.getModel(
    	            this, "getPaymentTypeList_Output");
    	            var listOfPaymentTypes = null;
    	            listOfPaymentTypes = _scModelUtils.getModelListFromPath("PaymentTypeList.PaymentType", paymentTypeListModel);
    	            var length = 0;
    	            length = _scBaseUtils.getAttributeCount(
    	            listOfPaymentTypes);
    	            for (
    	            var counter = 0;
    	            counter < length;
    	            counter = counter + 1) {
    	                mashupRefBean = listOfPaymentTypes[
    	                counter];
    	                listPaymentType = _scBaseUtils.getStringValueFromBean("PaymentType", mashupRefBean);
    	                if (
    	                _scBaseUtils.equals(
    	                paymentType, listPaymentType)) {
    	                    paymentTypeGroup = _scBaseUtils.getStringValueFromBean("PaymentTypeGroup", mashupRefBean);
    	                    _scModelUtils.setStringValueAtModelPath("Order.PaymentMethod.PaymentType", paymentType, sourceModel);
    	                    _scModelUtils.setStringValueAtModelPath("Order.PaymentMethod.PaymentTypeGroup", paymentTypeGroup, sourceModel);
    	                    _scScreenUtils.setModel(
    	                    this, "paymentCapture_output", sourceModel, null);
    	                }
    	            }
    	            var paymentTypeScreen = null;
    	            this.handleSaveToCustomerCheckbox(
    	            paymentTypeGroup, paymentType);
    	            var constructorArgs = null;
    	            constructorArgs = _scBaseUtils.getNewBeanInstance();;
    	            if (
    	            _scBaseUtils.equals(
    	            paymentTypeGroup, "CREDIT_CARD")) {
						console.log("executed",executed);
						var parentScreen = _isccsUIUtils.getParentScreen(this, false);
						if(executed){
							_scWidgetUtils.closePopup(this, "CLOSE", false);
							executed = false;
							parentScreen.addPaymentMethod();
						}else{
							executed = true;
						}
						
						//parentScreen.getSessionModule();
						aurusSessionResponse=parentScreen.getAurusSessionResponse();
    	                paymentTypeScreen = "extn.common.tnsPaymentCapture.TNSPaymentCapture_CreditCard";
						//"isccs.common.paymentCapture.PaymentCapture_CreditCard";
						//"extn.common.testing.Index";
    	                editPaymentMethod = _scModelUtils.getBooleanValueFromPath("Order.EditPaymentMethod", sourceModel, false);
    	                var EncryptRulesModel = null;
    	                EncryptRulesModel = _scScreenUtils.getModel(
    	                this, "getRuleDetails_EncryptAdd_Output");
    	                var sEncrypt = null;
    	                sEncrypt = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", EncryptRulesModel);
    	                if (
    	                _scBaseUtils.isBooleanTrue(
    	                editPaymentMethod) && _scBaseUtils.equals(
    	                sEncrypt, "Y")) {
    	                    constructorArgs["screenMode"] = "Order-Edit-Encrypt";
    	                }
						constructorArgs["aurusSessionResponse"] = aurusSessionResponse;
    	                constructorArgs["savedpaymenttargetModel"]=args["savedpaymenttargetModel"]; 

    	            } else if (
    	            _scBaseUtils.equals(
    	            paymentTypeGroup, "STORED_VALUE_CARD")) {
    	                paymentTypeScreen = "isccs.common.paymentCapture.PaymentCapture_StoredValue";
    	            } else if (
    	            _scBaseUtils.equals(
    	            paymentTypeGroup, "CUSTOMER_ACCOUNT")) {
    	                paymentTypeScreen = "isccs.common.paymentCapture.PaymentCapture_CustomerAccount";
    	            } else if (
    	            _scBaseUtils.equals(
    	            paymentTypeGroup, "OTHER")) {
    	                if (
    	                _scBaseUtils.equals(
    	                paymentType, "CHECK") || _scBaseUtils.equals(
    	                paymentType, "REFUND_CHECK")) {
    	                    paymentTypeScreen = "isccs.common.paymentCapture.PaymentCapture_Check";
    	                } else if (
    	                _scBaseUtils.equals(
    	                paymentType, "PRE_PAID")) {
    	                    paymentTypeScreen = "isccs.common.paymentCapture.PaymentCapture_PrePaid";
    	                } else {
    	                    paymentTypeScreen = "isccs.common.paymentCapture.PaymentCapture_Other";
    	                }
    	            }
    	            var options = null;
    	            options = {};
    	            var bindingData = null;
    	            bindingData = {};
    	            constructorArgs["BindingData"] = bindingData;
    	            var mappings = null;
    	            mappings = {};
    	            bindingData["Mappings"] = mappings;
    	            var sourceMappings = null;
    	            sourceMappings = {};
    	            mappings["SourceMappings"] = sourceMappings;
    	            var sourceMappingArray = null;
    	            sourceMappingArray = [];
    	            sourceMappings["SourceMapping"] = sourceMappingArray;
    	            var sourceMapping = null;
    	            sourceMapping = {};
    	            sourceMappingArray.push(
    	            sourceMapping);
    	            sourceMapping["childnamespace"] = "PaymentCapture_output";
    	            sourceMapping["childpath"] = "PaymentMethod";
    	            sourceMapping["parentnamespace"] = "paymentCapture_output";
    	            sourceMapping["parentpath"] = "PaymentMethod";
    	            var targetMappings = null;
    	            targetMappings = {};
    	            mappings["TargetMappings"] = targetMappings;
    	            var targetMappingArray = null;
    	            targetMappingArray = [];
    	            targetMappings["TargetMapping"] = targetMappingArray;
    	            var targetMapping = null;
    	            targetMapping = {};
    	            targetMappingArray.push(
    	            targetMapping);
    	            targetMapping["childnamespace"] = "PaymentCapture_input";
    	            targetMapping["childpath"] = "PaymentMethod";
    	            targetMapping["parentnamespace"] = "PaymentCapture_input";
    	            targetMapping["parentpath"] = "PaymentMethod";
    	            options["screen"] = this;
    	            options["refUId"] = "pnlPaymentCaptureEntry";
    	            options["createdUId"] = "scrnPaymentCapture";
    	            options["position"] = "first";
    	            _scScreenUtils.createScreenWithinScreen(
    	            this, paymentTypeScreen, constructorArgs, "addPaymentMethod", options, sourceModel);
    	            if (
    	            editPaymentMethod) {}
    		}
           
        },	
	updatePaymentMethod:function(){
    	
//    	alert("updatePaymentMethod");
    	
    	if(!(_scBaseUtils.isVoid(selectedPaymentModel))){
    		var event=selectedPaymentModel.event;
    		var bEvent=selectedPaymentModel.bEvent;
    		var ctrl=selectedPaymentModel.ctrl;
    		var args=selectedPaymentModel.args;
    		
//    		 var parentScreen = _isccsUIUtils.getParentScreen(this, false);
//             parentScreen.paymentcapturescreen=null;
             
    		this.newPaymentMethod(event, bEvent, ctrl, args);//yadavendra
    	}
    },   
	
	onApply: function(
        event, bEvent, ctrl, args) {
			
console.log("this",this);

            var targetModel = null;
            var paymentTypeGroup = null;
            var errorMessage = null;
            if (
            _scWidgetUtils.isWidgetVisible(
            this, "pnlSavedPaymentMethods")) {
            	
//            	alert("hi saved card");
            	
            	var selectedCustomerPaymentMethodList = null;
                selectedCustomerPaymentMethodList = _scGridxUtils.getSelectedSourceRecordsUsingUId(this, "tblSavedPaymentMethods");
                
                var customerPaymentMethods = null;
                customerPaymentMethods = _scModelUtils.getModelListFromPath("CustomerPaymentMethodList.CustomerPaymentMethod", selectedCustomerPaymentMethodList);
                targetModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethod");
                _scModelUtils.addModelToModelPath("PaymentMethod", _scModelUtils.getModelFromList(
                      customerPaymentMethods, 0), targetModel);
                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
            	
                if (_scBaseUtils.equals(paymentTypeGroup, "CREDIT_CARD")) {
                	args["savedpaymenttargetModel"] = targetModel;
                    
                    selectedPaymentModel= {};
                    selectedPaymentModel.event=event;
                    selectedPaymentModel.bEvent=bEvent;
                    selectedPaymentModel.ctrl=ctrl;
                    selectedPaymentModel.args=args;
                    
                    var parentScreen = _isccsUIUtils.getParentScreen(this, false);
                    
                    parentScreen.paymentcapturescreen=this;
                    
                    parentScreen.getSessionModule(targetModel);
                    
                }else {
                	
                	if (_scBaseUtils.equals(paymentTypeGroup, "OTHER")) {
	                	var paymentReference2 = null;
	                    paymentReference2 = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2", targetModel);
	                    if (
	                    _scBaseUtils.isVoid(
	                    paymentReference2)) {
	                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", "0", targetModel);
	                    } else {
	                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", paymentReference2, targetModel);
	                    }
	                    
	                  _isccsModelUtils.removeAttributeFromModel("PaymentMethod.IsDefaultMethod", targetModel);
	                  _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeDesc", targetModel);
	                  _isccsModelUtils.removeAttributeFromModel("PaymentMethod.CreditCardTypeDesc", targetModel);
	                  _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeGroup", targetModel);
                	}
                  
                  var screenInput = null;
                  screenInput = _scScreenUtils.getInitialInputData(this);
                  var isEdit = _scModelUtils.getStringValueFromPath("Order.EditPaymentMethod",screenInput);
                  console.log("isEdit",isEdit);
                  _scModelUtils.setStringValueAtModelPath("PaymentMethod.EditPaymentMethod", isEdit, targetModel);
                  
                  _scScreenUtils.setPopupOutput(
                  this, targetModel);
                  _scWidgetUtils.closePopup(
                  this, "APPLY", false);
                }
                
            	
//                var selectedCustomerPaymentMethodList = null;
//                selectedCustomerPaymentMethodList = _scGridxUtils.getSelectedSourceRecordsUsingUId(
//                this, "tblSavedPaymentMethods");
//                var customerPaymentMethods = null;
//                customerPaymentMethods = _scModelUtils.getModelListFromPath("CustomerPaymentMethodList.CustomerPaymentMethod", selectedCustomerPaymentMethodList);
//                targetModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethod");
//                _scModelUtils.addModelToModelPath("PaymentMethod", _scModelUtils.getModelFromList(
//                customerPaymentMethods, 0), targetModel);
//                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
//                if (
//                _scBaseUtils.equals(
//                paymentTypeGroup, "OTHER")) {
//                    var paymentReference2 = null;
//                    paymentReference2 = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2", targetModel);
//                    if (
//                    _scBaseUtils.isVoid(
//                    paymentReference2)) {
//                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", "0", targetModel);
//                    } else {
//                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", paymentReference2, targetModel);
//                    }
//                }
//                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.IsDefaultMethod", targetModel);
//                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeDesc", targetModel);
//                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.CreditCardTypeDesc", targetModel);
//                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeGroup", targetModel);
//                
                
                
            } else {
            	
//            	alert("hello new");
            	
                targetModel = _scBaseUtils.getTargetModel(
                this, "PaymentCapture_input", null);
                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
                if (
                _scBaseUtils.equals(
                paymentTypeGroup, "CREDIT_CARD")) {
					executed=true;
					that = this;
					var childrenList = this.childScreenList;
					if (!_scBaseUtils.isVoid(childrenList)){
						for (var i = 0; i < childrenList.length; i++){
							var childScreen = childrenList[i];
							if (_scBaseUtils.contains(childScreen.id,"extn_common_tnsPaymentCapture_TNSPaymentCapture_CreditCard")){
								childScreen = childScreen;
								break;								
							}
						}
					} 
					childScreen.onParentApply(targetModel);
					return;
                }
                if (_scBaseUtils.equals(paymentTypeGroup, "STORED_VALUE_CARD")) {
					executed = false;
					
					var giftCardBalanceModel = _scBaseUtils.getTargetModel(this, "PaymentCapture_input", null);
					
					var gifCardPin = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2",targetModel);
					var balance = _scModelUtils.getStringValueFromPath("PaymentMethod.RequestedAmount",giftCardBalanceModel);
					var giftCardNo = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference1",targetModel);
					var displayGiftCardNo = giftCardNo.substr(giftCardNo.length -4);
					
					if (_scBaseUtils.isVoid(balance) || _scBaseUtils.equals(0.00,balance)){
						_scScreenUtils.showErrorMessageBox(this,"Invalid Gift Card","error",null,null);
						return;
					}
					
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.SvcNo", giftCardNo, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardType", "GC", targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.DisplaySvcNo", displayGiftCardNo, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference1", gifCardPin, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference3", balance, targetModel);
					console.log('dd',targetModel);
                    var svcCardToken = null;
                    svcCardToken = _scModelUtils.getStringValueFromPath("PaymentMethod.SvcNo", targetModel);
                     
                    var svcCardDisplay = null;
                    svcCardDisplay = _scModelUtils.getStringValueFromPath("PaymentMethod.DisplaySvcNo", targetModel);
					
					svcCardToken = true;
					svcCardDisplay = true;
                    if (_scBaseUtils.isVoid(svcCardToken) && _scBaseUtils.isVoid(svcCardDisplay)) {
                        errorMessage = _scScreenUtils.getString(this, "SVCCardVoidMessage");
                        _isccsBaseTemplateUtils.showMessage(this, errorMessage, "error", null);
                        return;
                    }
                }
				if (_scBaseUtils.equals(paymentTypeGroup, "OTHER")) {
					executed = false;
					console.log("targetModel",targetModel);
					var maxChargeLimit = _scModelUtils.getStringValueFromPath("PaymentMethod.RequestedAmount",targetModel);
					
					if (_scBaseUtils.equals("VOUCHERS", _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentType", targetModel, false)) && 
							(_scBaseUtils.isVoid(maxChargeLimit) || _scBaseUtils.equals(0.00,maxChargeLimit))){
						_scScreenUtils.showErrorMessageBox(this,"Invalid Vouchers","error",null,null);
						return;
					}
					
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.IsCorrection","N", targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount",maxChargeLimit, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.MaxChargeLimit", maxChargeLimit, targetModel);
					console.log("targetModel",targetModel);
					//_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference1", gifCardPin, targetModel);
				}
                if (
                _scBaseUtils.equals(
                this.updatedAddress, true)) {
                    var updatedAddressModel = null;
                    updatedAddressModel = _scScreenUtils.getModel(
                    this, "updatedAddress_Output");
                    updatedAddressModel = _scModelUtils.getModelObjectFromPath("PersonInfo", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("isHistory", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("Lockid", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("UseCount", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("CountryDesc", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("TitleDesc", updatedAddressModel);
                    _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", updatedAddressModel, targetModel);
                } else {
                    var saveBillToOnOrder = null;
                    saveBillToOnOrder = _scModelUtils.getStringValueFromPath("PaymentMethod.SaveBillToOnOrder", targetModel);
                    if (
                    _scBaseUtils.equals(
                    saveBillToOnOrder, "Y")) {
                        var originalBillToModel = null;
                        originalBillToModel = _scScreenUtils.getModel(
                        _scScreenUtils.getChildScreen(
                        this, "scrnPaymentCapture"), "billingAddress_Output");
                        originalBillToModel = _scModelUtils.getModelObjectFromPath("PersonInfoBillTo", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("isHistory", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("Lockid", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("UseCount", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("CountryDesc", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("TitleDesc", originalBillToModel);
                        _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", originalBillToModel, targetModel);
                    }
                }
                
                
                var screenInput = null;
                screenInput = _scScreenUtils.getInitialInputData(this);
                var isEdit = _scModelUtils.getStringValueFromPath("Order.EditPaymentMethod",screenInput);
                console.log("isEdit",isEdit);
                _scModelUtils.setStringValueAtModelPath("PaymentMethod.EditPaymentMethod", isEdit, targetModel);
                
                _scScreenUtils.setPopupOutput(
                this, targetModel);
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
                
            }
            
//            var screenInput = null;
//            screenInput = _scScreenUtils.getInitialInputData(this);
//            var isEdit = _scModelUtils.getStringValueFromPath("Order.EditPaymentMethod",screenInput);
//            console.log("isEdit",isEdit);
//            _scModelUtils.setStringValueAtModelPath("PaymentMethod.EditPaymentMethod", isEdit, targetModel);
//            
//            _scScreenUtils.setPopupOutput(
//            this, targetModel);
//            _scWidgetUtils.closePopup(
//            this, "APPLY", false);
        },
	
	/*
	onApply: function(
        event, bEvent, ctrl, args) {
			
console.log("this",this);

            var targetModel = null;
            var paymentTypeGroup = null;
            var errorMessage = null;
            if (
            _scWidgetUtils.isWidgetVisible(
            this, "pnlSavedPaymentMethods")) {
                var selectedCustomerPaymentMethodList = null;
                selectedCustomerPaymentMethodList = _scGridxUtils.getSelectedSourceRecordsUsingUId(
                this, "tblSavedPaymentMethods");
                var customerPaymentMethods = null;
                customerPaymentMethods = _scModelUtils.getModelListFromPath("CustomerPaymentMethodList.CustomerPaymentMethod", selectedCustomerPaymentMethodList);
                targetModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethod");
                _scModelUtils.addModelToModelPath("PaymentMethod", _scModelUtils.getModelFromList(
                customerPaymentMethods, 0), targetModel);
                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
                if (
                _scBaseUtils.equals(
                paymentTypeGroup, "OTHER")) {
                    var paymentReference2 = null;
                    paymentReference2 = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2", targetModel);
                    if (
                    _scBaseUtils.isVoid(
                    paymentReference2)) {
                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", "0", targetModel);
                    } else {
                        _scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount", paymentReference2, targetModel);
                    }
                }
                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.IsDefaultMethod", targetModel);
                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeDesc", targetModel);
                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.CreditCardTypeDesc", targetModel);
                _isccsModelUtils.removeAttributeFromModel("PaymentMethod.PaymentTypeGroup", targetModel);
            } else {
                targetModel = _scBaseUtils.getTargetModel(
                this, "PaymentCapture_input", null);
                paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
                if (
                _scBaseUtils.equals(
                paymentTypeGroup, "CREDIT_CARD")) {
					executed=true;
					that = this;
					var childrenList = this.childScreenList;
					if (!_scBaseUtils.isVoid(childrenList)){
						for (var i = 0; i < childrenList.length; i++){
							var childScreen = childrenList[i];
							if (_scBaseUtils.contains(childScreen.id,"extn_common_tnsPaymentCapture_TNSPaymentCapture_CreditCard")){
								childScreen = childScreen;
								break;								
							}
						}
					} 
					childScreen.onParentApply(targetModel);
					return;
                }
                if (_scBaseUtils.equals(paymentTypeGroup, "STORED_VALUE_CARD")) {
					executed = false;
					
					var giftCardBalanceModel = _scBaseUtils.getTargetModel(this, "PaymentCapture_input", null);
					
					var gifCardPin = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference2",targetModel);
					var balance = _scModelUtils.getStringValueFromPath("PaymentMethod.RequestedAmount",giftCardBalanceModel);
					var giftCardNo = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentReference1",targetModel);
					var displayGiftCardNo = giftCardNo.substr(giftCardNo.length -4);
					
					if (_scBaseUtils.isVoid(balance) || _scBaseUtils.equals(0.00,balance)){
						_scScreenUtils.showErrorMessageBox(this,"Invalid Gift Card","error",null,null);
						return;
					}
					
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.SvcNo", giftCardNo, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardType", "GC", targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.DisplaySvcNo", displayGiftCardNo, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference1", gifCardPin, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference3", balance, targetModel);
					console.log('dd',targetModel);
                    var svcCardToken = null;
                    svcCardToken = _scModelUtils.getStringValueFromPath("PaymentMethod.SvcNo", targetModel);
                     
                    var svcCardDisplay = null;
                    svcCardDisplay = _scModelUtils.getStringValueFromPath("PaymentMethod.DisplaySvcNo", targetModel);
					
					svcCardToken = true;
					svcCardDisplay = true;
                    if (_scBaseUtils.isVoid(svcCardToken) && _scBaseUtils.isVoid(svcCardDisplay)) {
                        errorMessage = _scScreenUtils.getString(this, "SVCCardVoidMessage");
                        _isccsBaseTemplateUtils.showMessage(this, errorMessage, "error", null);
                        return;
                    }
                }
				if (_scBaseUtils.equals(paymentTypeGroup, "OTHER")) {
					executed = false;
					console.log("targetModel",targetModel);
					var maxChargeLimit = _scModelUtils.getStringValueFromPath("PaymentMethod.RequestedAmount",targetModel);
					
					if (_scBaseUtils.equals("VOUCHERS", _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentType", targetModel, false)) && 
							(_scBaseUtils.isVoid(maxChargeLimit) || _scBaseUtils.equals(0.00,maxChargeLimit))){
						_scScreenUtils.showErrorMessageBox(this,"Invalid Vouchers","error",null,null);
						return;
					}
					
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.IsCorrection","N", targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.RequestedAmount",maxChargeLimit, targetModel);
					_scModelUtils.setStringValueAtModelPath("PaymentMethod.MaxChargeLimit", maxChargeLimit, targetModel);
					console.log("targetModel",targetModel);
					//_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference1", gifCardPin, targetModel);
				}
                if (
                _scBaseUtils.equals(
                this.updatedAddress, true)) {
                    var updatedAddressModel = null;
                    updatedAddressModel = _scScreenUtils.getModel(
                    this, "updatedAddress_Output");
                    updatedAddressModel = _scModelUtils.getModelObjectFromPath("PersonInfo", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("isHistory", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("Lockid", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("UseCount", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("CountryDesc", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("TitleDesc", updatedAddressModel);
                    _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", updatedAddressModel, targetModel);
                } else {
                    var saveBillToOnOrder = null;
                    saveBillToOnOrder = _scModelUtils.getStringValueFromPath("PaymentMethod.SaveBillToOnOrder", targetModel);
                    if (
                    _scBaseUtils.equals(
                    saveBillToOnOrder, "Y")) {
                        var originalBillToModel = null;
                        originalBillToModel = _scScreenUtils.getModel(
                        _scScreenUtils.getChildScreen(
                        this, "scrnPaymentCapture"), "billingAddress_Output");
                        originalBillToModel = _scModelUtils.getModelObjectFromPath("PersonInfoBillTo", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("isHistory", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("Lockid", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("UseCount", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("CountryDesc", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("TitleDesc", originalBillToModel);
                        _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", originalBillToModel, targetModel);
                    }
                }
 				var screenInput = null;
                screenInput = _scScreenUtils.getInitialInputData(this);
                var isEdit = _scModelUtils.getStringValueFromPath("Order.EditPaymentMethod",screenInput);
                console.log("isEdit",isEdit);
                _scModelUtils.setStringValueAtModelPath("PaymentMethod.EditPaymentMethod", isEdit, targetModel);
                
                _scScreenUtils.setPopupOutput(
                this, targetModel);
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
            }
            
         //   var screenInput = null;
         //   screenInput = _scScreenUtils.getInitialInputData(this);
         //   var isEdit = _scModelUtils.getStringValueFromPath("Order.EditPaymentMethod",screenInput);
         //   console.log("isEdit",isEdit);
         //   _scModelUtils.setStringValueAtModelPath("PaymentMethod.EditPaymentMethod", isEdit, targetModel);
            
         //   _scScreenUtils.setPopupOutput(
         //   this, targetModel);
         //   _scWidgetUtils.closePopup(
         //   this, "APPLY", false);
        },	
		*/
		getToken : function(response){			
			executed=false;
	
			var JSONdata=response;
			//alert('JSON DATA' + JSON.stringify(JSONdata));
			//alert('getToken'+  JSON.stringify(JSONdata));
             /*var respCode = JSONdata.response_code;
             var respTxt = JSONdata.response_text;
             var maskedCardNum = JSONdata.masked_card_num;
             var cardType = JSONdata.card_type;
             var cardExpiry = JSONdata.card_expiry_date;
             var ott = JSONdata.one_time_token;
             var cardHolderName = JSONdata.card_holder_name;
             //var transactionId = JSONdata.transaction_id;
             console.log('JSON  response: getToken ' + JSON.stringify(JSONdata));*/
			

 			var targetModel = _scBaseUtils.getTargetModel(
                 this, "PaymentCapture_input", null);
 			
 			
 			var respCode = JSONdata.response_code;
             var respTxt = JSONdata.response_text;
             var maskedCardNum = JSONdata.masked_card_num;
             var cardType = JSONdata.card_type;
             
             if(_scBaseUtils.equals(cardType, "VIC")){
            	 cardType = "visa";
             }else if(_scBaseUtils.equals(cardType, "MCC")){
            	 cardType = "mastercard";
             }else if(_scBaseUtils.equals(cardType, "JBC")){
            	 cardType = "jcb";
             }else if(_scBaseUtils.equals(cardType, "NVC")){
            	 cardType = "discover";
             }else if(_scBaseUtils.equals(cardType, "DCC")){
            	 cardType = "diners_club";
             }else if(_scBaseUtils.equals(cardType, "AXC")){
            	 cardType = "amex";
             }
             
             var cardExpiry = JSONdata.card_expiry_date;
             var ott = JSONdata.one_time_token;
             var cardHolderName = JSONdata.card_holder_name;
             var maskedCardNum=JSONdata.masked_card_num;
             var cardToken=JSONdata.card_token;
             //var transactionId = JSONdata.transaction_id;
             console.log('JSON  response: getToken ' + JSON.stringify(JSONdata));
 			
             
             console.log("OUPUT1",_scBaseUtils.getTargetModel(
 					this, "PaymentCapture_output", null));
 			
 			console.log("PaymentCaptureModel1: ",targetModel);
 			//console.log("TOKEN "+_scModelUtils.getStringValueFromPath("Token.TokenId", tokenModel));
 			var token = ott;
 			var displayCCNo = maskedCardNum;
 			displayCCNo = displayCCNo.substr(displayCCNo.length -4);
 			console.log("DisplayCreditCardNo: "+displayCCNo);
 			if(_scBaseUtils.isVoid(token)){
 				errorMessage = "Unable to reach tonenization service. Please try again later.";
 				_isccsBaseTemplateUtils.showMessage(
 				this, errorMessage, "error", null);
 				return;
 			}
 			var firstName = _scModelUtils.getStringValueFromPath("FirstName",personInfoBillToModel);
 			var lastName = _scModelUtils.getStringValueFromPath("LastName",personInfoBillToModel);
 			
 			var mon_year = 2;
 			var expMonth = cardExpiry.substring(0,mon_year);
 			var expYear = cardExpiry.substring(mon_year);
 			
 			/*
 			var expMonth = "5";
			var expYear = "20"+21;
			if(_scBaseUtils.equals(expMonth.length,1)){
				expMonth = "0"+expMonth;
			}
			*/
 			
 			var creditCardExpDate = cardExpiry;
			var creditCardExpDate = expMonth+"/"+ "20"+expYear;
 			
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardType", cardType, targetModel);
// 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardNo", ott, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardNo", maskedCardNum, targetModel);

 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference3", ott, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.DisplayCreditCardNo", displayCCNo, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpDate", creditCardExpDate, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpMonth", expMonth, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpYear", expYear, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.FirstName", firstName, targetModel);
 			_scModelUtils.setStringValueAtModelPath("PaymentMethod.LastName", lastName, targetModel);
 			console.log("targetModel",targetModel);
 			if (
                 _scBaseUtils.equals(
                 this.updatedAddress, true)) {
                     var updatedAddressModel = null;
                     updatedAddressModel = _scScreenUtils.getModel(
                     this, "updatedAddress_Output");
 					console.log("If BillTo1",updatedAddressModel);
                     updatedAddressModel = _scModelUtils.getModelObjectFromPath("PersonInfo", updatedAddressModel);
                     _isccsModelUtils.removeAttributeFromModel("isHistory", updatedAddressModel);
                     _isccsModelUtils.removeAttributeFromModel("Lockid", updatedAddressModel);
                     _isccsModelUtils.removeAttributeFromModel("UseCount", updatedAddressModel);
                     _isccsModelUtils.removeAttributeFromModel("CountryDesc", updatedAddressModel);
                     _isccsModelUtils.removeAttributeFromModel("TitleDesc", updatedAddressModel);
                     _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", updatedAddressModel, targetModel);
                 } else {
                     var saveBillToOnOrder = null;
                     saveBillToOnOrder = _scModelUtils.getStringValueFromPath("PaymentMethod.SaveBillToOnOrder", targetModel);
                     if (
                     _scBaseUtils.equals(
                     saveBillToOnOrder, "Y")) {
                         var originalBillToModel = null;
                         originalBillToModel = _scScreenUtils.getModel(
                         _scScreenUtils.getChildScreen(
                         this, "scrnPaymentCapture"), "billingAddress_Output");
 						console.log("Else BillTo1",originalBillToModel);
                         originalBillToModel = _scModelUtils.getModelObjectFromPath("PersonInfoBillTo", originalBillToModel);
                         _isccsModelUtils.removeAttributeFromModel("isHistory", originalBillToModel);
                         _isccsModelUtils.removeAttributeFromModel("Lockid", originalBillToModel);
                         _isccsModelUtils.removeAttributeFromModel("UseCount", originalBillToModel);
                         _isccsModelUtils.removeAttributeFromModel("CountryDesc", originalBillToModel);
                         _isccsModelUtils.removeAttributeFromModel("TitleDesc", originalBillToModel);
                         _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", originalBillToModel, targetModel);
                     }
                 }
 			
 			var customerPaymentList = _scScreenUtils.getModel(this, "getCustomerPaymentMethodList_Output");
 			targetModel.CustomerPaymentMethodList =  customerPaymentList;
 			targetModel.PaymentMethod.SaveAgainstCustomer = "Y";
 			_scScreenUtils.setModel(
     	        this, "PaymentCapture_input", targetModel, null);
 			console.log("PaymentCaptureModel2: ",targetModel);
 			_scScreenUtils.setPopupOutput(
             this, targetModel);
             _scWidgetUtils.closePopup(
             this, "APPLY", false);
             
		
/*
			var inputModel = {};
			inputModel.GetToken = {};
			//_scScreenUtils.getModel(this, "extn_getToken_input");
			_scModelUtils.addStringValueToModelObject("SessionId", response.session.id, inputModel.GetToken);
			_scModelUtils.addStringValueToModelObject("Type", response.sourceOfFunds.type, inputModel.GetToken);
			tnsResponse = response;
			console.log("tnsResponse: "+tnsResponse);
			console.log("TokenInput",inputModel);
			_isccsUIUtils.callApi(this, inputModel, "extn_getToken_referenceid");
			console.log("OUPUT1",_scBaseUtils.getTargetModel(
					this, "PaymentCapture_output", null));
*/
		},
/*
		setResponse: function(response,tokenModel){
			var targetModel = _scBaseUtils.getTargetModel(
                this, "PaymentCapture_input", null);
			console.log("PaymentCaptureModel1: ",targetModel);
			console.log("tokenMode: ",tokenModel);
			console.log("TOKEN "+_scModelUtils.getStringValueFromPath("Token.TokenId", tokenModel));
			var token = _scModelUtils.getStringValueFromPath("Token.TokenId", tokenModel);
			var displayCCNo = response.sourceOfFunds.provided.card.number;
			displayCCNo = displayCCNo.substr(displayCCNo.length -4);
			console.log("DisplayCreditCardNo: "+displayCCNo);
			if(_scBaseUtils.isVoid(token)){
				errorMessage = "Unable to reach tonenization service. Please try again later.";
				_isccsBaseTemplateUtils.showMessage(
				this, errorMessage, "error", null);
				return;
			}
			var firstName = _scModelUtils.getStringValueFromPath("FirstName",personInfoBillToModel);
			var lastName = _scModelUtils.getStringValueFromPath("LastName",personInfoBillToModel);
			var expMonth = response.sourceOfFunds.provided.card.expiry.month;
			var expYear = "20"+response.sourceOfFunds.provided.card.expiry.year;
			if(_scBaseUtils.equals(expMonth.length,1)){
				expMonth = "0"+expMonth;
			}
			var creditCardExpDate = expMonth+"/"+expYear;
			console.log("month: "+expMonth+", year: "+expYear+", expDate:"+creditCardExpDate)
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardType", response.sourceOfFunds.provided.card.scheme, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardNo", token, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.PaymentReference2", token, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.DisplayCreditCardNo", displayCCNo, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpDate", creditCardExpDate, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpMonth", expMonth, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardExpYear", expYear, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.FirstName", firstName, targetModel);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.LastName", lastName, targetModel);
			console.log("targetModel",targetModel);
			if (
                _scBaseUtils.equals(
                this.updatedAddress, true)) {
                    var updatedAddressModel = null;
                    updatedAddressModel = _scScreenUtils.getModel(
                    this, "updatedAddress_Output");
					console.log("If BillTo1",updatedAddressModel);
                    updatedAddressModel = _scModelUtils.getModelObjectFromPath("PersonInfo", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("isHistory", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("Lockid", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("UseCount", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("CountryDesc", updatedAddressModel);
                    _isccsModelUtils.removeAttributeFromModel("TitleDesc", updatedAddressModel);
                    _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", updatedAddressModel, targetModel);
                } else {
                    var saveBillToOnOrder = null;
                    saveBillToOnOrder = _scModelUtils.getStringValueFromPath("PaymentMethod.SaveBillToOnOrder", targetModel);
                    if (
                    _scBaseUtils.equals(
                    saveBillToOnOrder, "Y")) {
                        var originalBillToModel = null;
                        originalBillToModel = _scScreenUtils.getModel(
                        _scScreenUtils.getChildScreen(
                        this, "scrnPaymentCapture"), "billingAddress_Output");
						console.log("Else BillTo1",originalBillToModel);
                        originalBillToModel = _scModelUtils.getModelObjectFromPath("PersonInfoBillTo", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("isHistory", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("Lockid", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("UseCount", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("CountryDesc", originalBillToModel);
                        _isccsModelUtils.removeAttributeFromModel("TitleDesc", originalBillToModel);
                        _scModelUtils.addModelToModelPath("PaymentMethod.PersonInfoBillTo", originalBillToModel, targetModel);
                    }
                }
			
			var customerPaymentList = _scScreenUtils.getModel(this, "getCustomerPaymentMethodList_Output");
			targetModel.CustomerPaymentMethodList =  customerPaymentList;
			targetModel.PaymentMethod.SaveAgainstCustomer = "Y";
			_scScreenUtils.setModel(
    	        this, "PaymentCapture_input", targetModel, null);
			console.log("PaymentCaptureModel2: ",targetModel);
			_scScreenUtils.setPopupOutput(
            this, targetModel);
            _scWidgetUtils.closePopup(
            this, "APPLY", false);
		},
*/
		handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getToken_referenceid")) {
				_scScreenUtils.setModel(
    	        this, "extn_getToken_output", modelOutput, null);
				console.log("Output ",modelOutput);
				this.setResponse(tnsResponse,modelOutput);				
			}
			if (_scBaseUtils.equals(mashupRefId, "extn_getCommonCode_referenceid")) {
				console.log("modelOutput",modelOutput);
			}
			
		},
		onPopupClose: function(
        event, bEvent, ctrl, args) {
			executed = false;
            var isDirty = false;
            isDirty = _scScreenUtils.isDirty(
            this, null, true);
            if (
            _scBaseUtils.equals(
            this.isDirtyCheckRequired, true)) {
                if (
                _scBaseUtils.equals(
                isDirty, true)) {
                    var msg = null;
                    var eventid = null;
                    msg = _scScreenUtils.getString(
                    this, "DirtyCloseConfirmationMessage");
                    _scScreenUtils.showConfirmMessageBox(
                    this, msg, "handleCloseConfirmation", null, null);
                    eventid = _scBaseUtils.getValueFromPath("eventName", event);
                } else {
                    _scWidgetUtils.closePopup(
                    this, "CLOSE", false);
                }
            } else {			
                _scWidgetUtils.closePopup(
                this, "CLOSE", false);
            }
            var res = null;
            res = _scModelUtils.createNewModelObjectWithRootKey("response");
            _scModelUtils.addStringValueToModelObject("statusCode", "failure", res);
            return res;
        },
		        handleSaveToCustomerCheckbox: function(
        paymentTypeGroup, paymentType) {
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.BillToID", screenInput)))) {
                var displayCheckBox = false;
                if (
                _scBaseUtils.equals(
                paymentTypeGroup, "CREDIT_CARD")) {
                    displayCheckBox = true;
				_scWidgetUtils.setValue(this, "chkSaveAgainstCustomer",true,true);				
						
				} else if (
                _scBaseUtils.equals(
                paymentTypeGroup, "CUSTOMER_ACCOUNT")) {
                    displayCheckBox = true;
                }
                if (
                displayCheckBox) {
                    /*_scWidgetUtils.enableWidget(
                    this, "chkSaveAgainstCustomer");
                    _scWidgetUtils.showWidget(
                    this, "chkSaveAgainstCustomer", false, null);*/
                    return;
                }
            }
            /*_scWidgetUtils.disableWidget(
            this, "chkSaveAgainstCustomer", false);
            _scWidgetUtils.hideWidget(
            this, "chkSaveAgainstCustomer", false);*/
        },
		extn_init:function(){
			/*_scWidgetUtils.hideWidget(
            this, "chkSaveAgainstCustomer", true);*/
		}
	});
});