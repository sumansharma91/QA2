
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/create/customer/CustomerIdentificationExtnUI",
		  "scbase/loader!dijit",
		  "scbase/loader!dojo",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!isccs/utils/ModelUtils",
		  "scbase/loader!isccs/utils/UIUtils"
		  ]
,
function( _dojodeclare,
		  _extnCustomerIdentificationExtnUI,
		  _dijit,
		  dojo,
		  _scWidgetUtils,
		  _scModelUtils,		  
          _scScreenUtils,
          _scBaseUtils,
		  _isccsBaseTemplateUtils,
		  _scEventUtils,
		  _isccsModelUtils,
		  _isccsUIUtils
){ 
	var taxExamptExist = false;
	var shipPrefExist = false;
	var strTaxExemptionCertificate = "";
	var strSCAC = "";
	var strSCACForOrder = "";
	return _dojodeclare("extn.order.create.customer.CustomerIdentificationExtn", [_extnCustomerIdentificationExtnUI],{
	// custom code here
	
	save: function(
        event, bEvent, ctrl, args) {
            var orderModel = null;
            var tmodel = null;
            var initialInputData = null;
            var isScreenDirty = false;
            var billToIdPresent = false;
            var billToAddressPresent = false;
            this.isScreenReloaded = false;
            tmodel = _scScreenUtils.getTargetModel(
            this, "selectedItem_input", null);
            //console.log(tmodel);
            //return;
            isScreenDirty = _scScreenUtils.isDirty(
            this, null, true);
            initialInputData = this.getScreenInitialInputData();
            billToAddressPresent = this.checkForPersonInfoBillTo();

            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.BillToID", initialInputData)))) {
                billToIdPresent = true;
            }
            if (
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.BillToID", tmodel)) && _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.BillToID", initialInputData))) 
            {
               _isccsBaseTemplateUtils.showMessage(
                    this, "selectCustomer", "error", null);
               return;
               /** var noCustomer = null;
                var skipCustomerModel = null;
                noCustomer = _isccsUIUtils.getWizardModel(
                this, "NoCustomer_output");
                skipCustomerModel = _isccsUIUtils.getWizardModel(
                this, "SkipCustomer_output");
                var sScreenType = null;
                sScreenType = _scWizardUtils.getCurrentPageScreenType(
                _isccsUIUtils.getCurrentWizardInstance(
                _scEditorUtils.getCurrentEditor()));
                if (
                _scBaseUtils.equals(
                this.getMode(), "ReturnAddress")) {
                    _isccsBaseTemplateUtils.showMessage(
                    this, "selectCustomer", "error", null);
                } else if (!(
                _scBaseUtils.isVoid(
                noCustomer))) {
                    if (
                    _scBaseUtils.equals(
                    _scModelUtils.getStringValueFromPath("Order.Action", noCustomer), "NoCustomer")) {
                        _scEventUtils.fireEventToParent(
                        this, "NoCustomer", null);
                    }
                } else if (!(
                _scBaseUtils.isVoid(
                skipCustomerModel))) {
                    var action = _scModelUtils.getStringValueFromPath("Order.Action", skipCustomerModel);
                    if(_scBaseUtils.equals(action, "SkipCustomer")){
                        if( _scBaseUtils.equals(sScreenType, "customerIdentificationBeforePayment")){
                            _isccsBaseTemplateUtils.showMessage(
                        this, "selectCustomer", "error", null);
                        }
                        else{
                             _scEventUtils.fireEventToParent(this, "SkipCustomer", null);
                        }
                    }
                } else {
                    _scEventUtils.fireEventToParent(
                    this, "SkipCustomer", null);
                } **/
            } else if (
            _scBaseUtils.or(
            _scBaseUtils.equals(
            billToIdPresent, false), _scScreenUtils.isDirty(
            this, null, true)) || _scBaseUtils.equals(
            billToAddressPresent, false)) {
            	
            	var orderModel = null;
                orderModel = this.getDataToProcess();
                var ePersonInfoBillTo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoBillTo", orderModel);
                var ePersonInfoShipTo = _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", orderModel);
                if(_scBaseUtils.equals(ePersonInfoShipTo.Country,"FGN"))
                {
                   _isccsBaseTemplateUtils.showMessage(
                        this, "Invalid country: Please re-enter Ship to address to continue", "error", null);
                   return;
                }
                if(_scBaseUtils.equals(ePersonInfoBillTo.Country,"FGN"))
                {
                   _isccsBaseTemplateUtils.showMessage(
                        this, "Invalid country: Please re-enter Bill to address to continue", "error", null);
                   return;
                }
                
                this.processScreenWithData();
            } else {
                var eventArgs = null;
                eventArgs = {};
                _scBaseUtils.setAttributeValue("argumentList", "", eventArgs);
                _scEventUtils.fireEventToParent(
                this, "onSaveSuccess", eventArgs);
            }
        },
	extnInit: function(){
		//Hides the txt_userIdStartsWithAdvanced and customerType widget
		_scWidgetUtils.hideWidget(this, "txt_userIdStartsWithAdvanced", false);
		_scWidgetUtils.hideWidget(this, "radCustomerType", true);
		//SU 30 Start
		_scWidgetUtils.hideWidget(this, "lnkNoConsumer", true);
		//SU 30 End
	},
	defaultCustomerType: function(sCustomerType) {
		// Sets the customer type to Consumer
			sCustomerType = "02";
            var inputModel = null;
            var initialInputData = null;
            var element = null;
            element = {};
            inputModel = {};
            _scModelUtils.addStringValueToModelObject("CustomerType", sCustomerType, element);
            _scModelUtils.addModelObjectAsChildToModelObject("Customer", element, inputModel);
            initialInputData = this.getScreenInitialInputData();
            _scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", initialInputData), inputModel);
            _scScreenUtils.setModel(this, "screenInput", inputModel, null);
            var showBusiness = true;
            showBusiness = _scBaseUtils.equals(sCustomerType, "01");
            this.displayCustomerType(showBusiness);
            if (showBusiness) {
                _scWidgetUtils.hideWidget(this, "pnlListContainer", false);
                _scWidgetUtils.showWidget(this, "pnlListContainer2", false, null);
            } else {
                _scWidgetUtils.hideWidget(this, "pnlListContainer2", false);
                _scWidgetUtils.showWidget(this, "pnlListContainer", false, null);
            }
     },
     performCheckForManageCustomerCall: function(orderModel) {
    		//console.log("HERE");

    		var sCustomerType = null;
    		var manageCustomer_input = null;
    		var customerPreview_output = null;
    		var blankModel = null;
    		var addressModel = null;
    		
            initialInputData = this.getScreenInitialInputData();
            _scModelUtils.setStringValueAtModelPath("Order.PriceInfo.Currency", 
            		_scModelUtils.getStringValueFromPath("Order.PriceInfo.Currency", initialInputData), orderModel);
            //console.log("orderModel",orderModel);
    		
    		_isccsModelUtils.removeAttributeFromModel("CustomerStatus", orderModel);
    		manageCustomer_input = _scScreenUtils.getModel(
    		this, "manageCustomer_input");
    		customerPreview_output = _scScreenUtils.getModel(
    		this, "getCompleteCustomerList_output");
			//console.log("customerPreview_output",customerPreview_output);
    		
			var tmodel = _scScreenUtils.getTargetModel(
            this, "selectedItem_input", null);

			var taxExemptionCode = strTaxExemptionCertificate;
			//console.log("taxExemptionCode",taxExemptionCode);

			var custtaxExemptionCode =  _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnTaxExemptionCode", customerPreview_output);
			var custOldTaxExemptionCode =  _scModelUtils.getStringValueFromPath("Customer.Extn.OldExtnTaxExemptionCode", customerPreview_output);
			//Riskifiy 
			var custCreateDate=_scModelUtils.getStringValueFromPath("Customer.Extn.ExtnCustomerCreatets", customerPreview_output);
			//console.log("custtaxExemptionCode: "+custtaxExemptionCode+" custoldTaxExemptionCode:"+custOldTaxExemptionCode);

			var preferredCarrier = strSCAC;
			
			var custPreferredCarrier = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrier", customerPreview_output);
			var extnOriginalPreferredCarrier = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnOriginalPreferredCarrier", customerPreview_output);
			var custPreferredCarrierDescription =  _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrierDescription", customerPreview_output);
			if(!_scBaseUtils.equals(custPreferredCarrier,extnOriginalPreferredCarrier)){
				//console.log("HERE_DOINGTHAT");
//				custPreferredCarrier = custOldPreferredCarrier;
			}
			//console.log("custPreferredCarrier: "+custPreferredCarrier+" extnOriginalPreferredCarrier:"+extnOriginalPreferredCarrier);
			
			if(!_scBaseUtils.isVoid(taxExemptionCode)){
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptionCertificate", taxExemptionCode,orderModel);
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptFlag", "Y",orderModel);
			// Modified below condition to use custtaxExemptionCode only if taxExamptUpdated != "Y"
			}else if(!_scBaseUtils.isVoid(custtaxExemptionCode) && !_scBaseUtils.equals(this.taxExamptUpdated, "Y")){
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptionCertificate", custtaxExemptionCode,orderModel);
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptFlag", "Y",orderModel);
			}else{
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptionCertificate", "",orderModel);
				_scModelUtils.setStringValueAtModelPath("Order.TaxExemptFlag", "N",orderModel);
			}
			//Riskifiy
			if(!_scBaseUtils.isVoid(custCreateDate)){
				_scModelUtils.setStringValueAtModelPath("Order.Extn.ExtnCustomerCreatets", custCreateDate,orderModel);
			}
			
			//console.log("preferredCarrier",preferredCarrier);
			if(!_scBaseUtils.isVoid(preferredCarrier)){
				if(!_scBaseUtils.isVoid(strSCACForOrder)){
					_scModelUtils.setStringValueAtModelPath("Order.SCAC", strSCACForOrder,orderModel);
				}else{
					_scModelUtils.setStringValueAtModelPath("Order.SCAC", preferredCarrier,orderModel);
				}
			}else if(!_scBaseUtils.isVoid(custPreferredCarrier)){
				if(!_scBaseUtils.equals(custPreferredCarrier,custPreferredCarrierDescription)){
					custPreferredCarrier = custPreferredCarrierDescription;
				}
				//console.log("custPreferredCarrier: "+custPreferredCarrier+", custPreferredCarrierDescription: ",custPreferredCarrierDescription);
				_scModelUtils.setStringValueAtModelPath("Order.SCAC", custPreferredCarrier,orderModel);
			}else{
				_scModelUtils.setStringValueAtModelPath("Order.SCAC", "",orderModel);
			}
			
    		var customerOrgCode = null;
    		customerOrgCode = _scModelUtils.getStringValueFromPath("Customer.OrganizationCode", customerPreview_output);
    		manageCustomer_input = _scModelUtils.getModelObjectFromPath("manageCustomer_input", manageCustomer_input);
    		blankModel = {};
    		var additionalAddressList = null;
    		additionalAddressList = [];
    		if (!(
    		_scBaseUtils.isVoid(
    			manageCustomer_input))) {
    			if (
    			_scModelUtils.isKeyPresentInModel("DefaultSoldToAddress", manageCustomer_input)) {
    			addressModel = _scModelUtils.getModelObjectFromPath("DefaultSoldToAddress", manageCustomer_input);
    			if (!(
    			_scBaseUtils.isVoid(
    				addressModel))) {
    				_scModelUtils.addModelObjectToModelList(
    				_scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress", addressModel), additionalAddressList);
    				_scModelUtils.addModelToModelPath("Order.PersonInfoSoldTo", _scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress.PersonInfo", addressModel), orderModel);
    			}
    			}
    			if (_scModelUtils.isKeyPresentInModel("DefaultShipToAddress", manageCustomer_input)) {
    				addressModel = _scModelUtils.getModelObjectFromPath("DefaultShipToAddress", manageCustomer_input);
    				if (!(
    				_scBaseUtils.isVoid(
    				addressModel))) {
    				_scModelUtils.addModelObjectToModelList(
    				_scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress", addressModel), additionalAddressList);
    				_scModelUtils.addModelToModelPath("Order.PersonInfoShipTo", _scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress.PersonInfo", addressModel), orderModel);
    				}
    			}
    			if (_scModelUtils.isKeyPresentInModel("DefaultBillToAddress", manageCustomer_input)) {
    				addressModel = _scModelUtils.getModelObjectFromPath("DefaultBillToAddress", manageCustomer_input);
    				if (!(
    				_scBaseUtils.isVoid(
    				addressModel))) {
    				_scModelUtils.addModelObjectToModelList(
    				_scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress", addressModel), additionalAddressList);
    				_scModelUtils.addModelToModelPath("Order.PersonInfoBillTo", _scModelUtils.getModelObjectFromPath("CustomerAdditionalAddress.PersonInfo", addressModel), orderModel);
    				}
    			}
    		}
    		var length = 0;
    		length = _scBaseUtils.getAttributeCount(
    		additionalAddressList);
    		//console.log("length",length);
    		//console.log("emailUpdated",this.emailUpdated);
    		//console.log("dayPhoneUpdated",this.dayPhoneUpdated);
    		//console.log("this.taxExamptUpdated",this.taxExamptUpdated);
    		//console.log("this.shipPreferenceUpdated",this.shipPreferenceUpdated);
    		if (
    		_scBaseUtils.greaterThan(
    		length, 0) || _scBaseUtils.or(
    		_scBaseUtils.equals(
    		this.emailUpdated, "Y"), _scBaseUtils.equals(
    		this.dayPhoneUpdated, "Y")) 
    		|| _scBaseUtils.equals(this.taxExamptUpdated,"Y") || _scBaseUtils.equals(this.shipPreferenceUpdated,"Y")) {
    			//console.log("INTHIS");
    			var manageCustomerApi_input = null;
    			manageCustomerApi_input = this.formManageCustomerApiInput(
    			additionalAddressList, orderModel, customerOrgCode);
    			//console.log("customerPreview_output",customerPreview_output);
				var CustomerAdditionalAddresslength = 0;
				
				if(!_scBaseUtils.isVoid(customerPreview_output.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList)){
					CustomerAdditionalAddresslength =customerPreview_output.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress.length;
				}else{
					CustomerAdditionalAddresslength = 2;
				}
    			
    			var defaultBillToID = customerPreview_output.Customer.CustomerContactList.CustomerContact[0].DefaultBillToAddress.PersonInfo.ID;
    			var defaultShipToID = null;
    			if(!(_scBaseUtils.isVoid(
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0].DefaultShipToAddress))){
    				defaultShipToID = customerPreview_output.Customer.CustomerContactList.CustomerContact[0].DefaultShipToAddress.PersonInfo.ID;
    			}
    			var dayPhone = _scModelUtils.getStringValueFromPath("DayPhone",
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0]);
    			var emailID = _scModelUtils.getStringValueFromPath("EmailID",
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0]);
    			var extnGender = _scModelUtils.getStringValueFromPath("ExtnGender",
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0]);
    			var firstName = _scModelUtils.getStringValueFromPath("FirstName",
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0]);
    			var lastName = _scModelUtils.getStringValueFromPath("LastName",
    					customerPreview_output.Customer.CustomerContactList.CustomerContact[0]);
    			//console.log("DayPhone:"+dayPhone+" emailID:"+emailID+" firstName:"+firstName+" lastName:"+lastName);
    			
    			if(_scBaseUtils.equals(
	    	    		this.dayPhoneUpdated, "Y")){
    				dayPhone = manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].DayPhone;
    			}
    			if(_scBaseUtils.equals(
    		    		this.emailUpdated, "Y")){
    				emailID = manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].EmailID;
    			}
    			
    			_scModelUtils.setStringValueAtModelPath("DayPhone", dayPhone, 
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			_scModelUtils.setStringValueAtModelPath("EMailID", emailID, 
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			_scModelUtils.setStringValueAtModelPath("EmailID", emailID,  
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			_scModelUtils.setStringValueAtModelPath("ExtnGender", extnGender,  
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			_scModelUtils.setStringValueAtModelPath("FirstName", firstName,  
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			_scModelUtils.setStringValueAtModelPath("LastName", lastName,
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0]);
    			
    			manageCustomerApi_input.Customer.CustomerKey = manageCustomerApi_input.Customer.CustomerID; 
    			var customerAdditionalAddressList = [];
    			customerAdditionalAddressList = _scModelUtils.getModelListFromPath(
    					"CustomerAdditionalAddress",
    					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList);
    			//console.log("customerAdditionalAddressList",customerAdditionalAddressList);
    			if(_scBaseUtils.isVoid(customerAdditionalAddressList)){
    				manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList = {};
    				manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.
    				CustomerAdditionalAddress = [];
    				manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.
    				CustomerAdditionalAddress[0] = customerPreview_output.Customer.CustomerContactList.CustomerContact[0].DefaultBillToAddress;
    				customerAdditionalAddressList = _scModelUtils.getModelListFromPath(
        					"CustomerAdditionalAddress",
        					manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList);
    			}
    			for(var i=0;i<customerAdditionalAddressList.length;i++){
    				var customerAdditionalAddress = customerAdditionalAddressList[i];
    				var iD = customerAdditionalAddress.PersonInfo.ID;
    				//console.log("iD",iD);
    				customerAdditionalAddress.PersonInfo.DayPhone = dayPhone;
    				customerAdditionalAddress.PersonInfo.EMailID = emailID;
    				customerAdditionalAddress.PersonInfo.EmailID = emailID;
    				customerAdditionalAddress.PersonInfo.ExtnGender = extnGender;
    				customerAdditionalAddress.PersonInfo.FirstName = firstName;
    				customerAdditionalAddress.PersonInfo.LastName = lastName;
    				
    				if(_scBaseUtils.equals(iD,defaultBillToID)){
    					customerAdditionalAddress.IsBillTo = "Y";
						customerAdditionalAddress.IsDefaultBillTo = "Y";
						customerAdditionalAddress.IsShipTo = "N";
						customerAdditionalAddress.IsDefaultShipTo = "N";
    				}else{
    					customerAdditionalAddress.IsBillTo = "N";
						customerAdditionalAddress.IsDefaultBillTo = "N";
						customerAdditionalAddress.IsShipTo = "Y";
						customerAdditionalAddress.IsDefaultShipTo = "N";
    				}
    			}
    			_scBaseUtils.removeBlankAttributes(manageCustomerApi_input);
				manageCustomerApi_input.Customer.Extn = {};
				if(_scBaseUtils.equals(this.shipPreferenceUpdated,"Y")){
					manageCustomerApi_input.Customer.HasCustomerAttributes = "Y";
					manageCustomerApi_input.Customer.SetPreferredCarrier = "Y";
					manageCustomerApi_input.Customer.Extn.ExtnPreferredCarrier = preferredCarrier;
					manageCustomerApi_input.Customer.Extn.ExtnOriginalPreferredCarrier = extnOriginalPreferredCarrier;
					//console.log("shipPrefExist",this.shipPrefExist);
					if(!_scBaseUtils.isVoid(preferredCarrier) && !this.shipPrefExist){
						_scModelUtils.setStringValueAtModelPath("Customer.Extn.ExtnPreferredCarrier", preferredCarrier,customerPreview_output);
						manageCustomerApi_input.Customer.CarrierRecordState = "Added";
					} else if(!_scBaseUtils.isVoid(preferredCarrier) && this.shipPrefExist){
						manageCustomerApi_input.Customer.CarrierRecordState = "Modified";
					} else if(_scBaseUtils.isVoid(preferredCarrier) && this.shipPrefExist){
						manageCustomerApi_input.Customer.CarrierRecordState = "Deleted";
					} else{
						manageCustomerApi_input.Customer.HasCustomerAttributes = "N";
						manageCustomerApi_input.Customer.SetPreferredCarrier = "N";
					}
				}
				if(_scBaseUtils.equals(this.taxExamptUpdated,"Y")){
					manageCustomerApi_input.Customer.HasCustomerAttributes = "Y";
					manageCustomerApi_input.Customer.SetTaxExemptionCode = "Y";
					manageCustomerApi_input.Customer.Extn.ExtnTaxExemptionCode = taxExemptionCode;
					//console.log("taxExamptExist",this.taxExamptExist);
					if(!_scBaseUtils.isVoid(taxExemptionCode) && !this.taxExamptExist){
						manageCustomerApi_input.Customer.TaxExemptionRecordState = "Added";
					} else if(!_scBaseUtils.isVoid(taxExemptionCode) && this.taxExamptExist){
						manageCustomerApi_input.Customer.TaxExemptionRecordState = "Modified";
					} else if(_scBaseUtils.isVoid(taxExemptionCode) && this.taxExamptExist){
						manageCustomerApi_input.Customer.TaxExemptionRecordState = "Deleted";
						this.taxExamptExist = false;
					} else{
						manageCustomerApi_input.Customer.HasCustomerAttributes = "N";
						manageCustomerApi_input.Customer.SetTaxExemptionCode = "N";
					}
				}
    			//console.log("manageCustomerApi_input",manageCustomerApi_input);		
				manageCustomerApi_input.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo.ID = String(CustomerAdditionalAddresslength+1);
				//console.log("manageCustomerApi_input",manageCustomerApi_input);	
				//console.log("orderModel",orderModel);
    			this.callMultiApi(manageCustomerApi_input, orderModel);
    		} else {
    			//console.log("INTHAT");
    			this.updateOrder(
    			orderModel);
    		}
    	},
		onCreatingNewCustomer: function(
        actionPerformed, model, popupParams) {
			if (
            _scBaseUtils.negateBoolean(
            _scBaseUtils.equals(
            actionPerformed, "CLOSE")) && _scBaseUtils.negateBoolean(
            _scBaseUtils.isVoid(
            model))) {				
                var apiInput = null;
                apiInput = _scModelUtils.createNewModelObjectWithRootKey("Customer");
                _scModelUtils.setStringValueAtModelPath("Customer.CustomerID", _scModelUtils.getStringValueFromPath("Customer.CustomerID", model), apiInput);
                _scModelUtils.setStringValueAtModelPath("Customer.CallingOrganizationCode", _scModelUtils.getStringValueFromPath("Customer.OrganizationCode", model), apiInput);
				_scModelUtils.setStringValueAtModelPath("Customer.OrganizationCode", _scModelUtils.getStringValueFromPath("Customer.OrganizationCode", model), apiInput);
                this.callGetCompleteCustomerListApi(
                apiInput);
            }
        },
        handleTaxExemptUpdate: function(
        event, bEvent, ctrl, args) {
            this.taxExamptUpdated = "Y";
            strTaxExemptionCertificate = args.TaxExemptionCertificate;
        },
        handleShipPreferenceUpdate: function(
        event, bEvent, ctrl, args) {
            this.shipPreferenceUpdated = "Y";
            strSCAC = args.SCAC;
            strSCACForOrder = args.SCACForOrder;
        },
        createConsumerCustomer: function(
        event, bEvent, ctrl, args) {
            var emptyModel = null;
            var popupParams = null;
            var bindings = null;
            var screenInput = null;
            var screenInitialInput = null;
            screenInitialInput = this.getScreenInitialInputData();
            screenInput = {};
            bindings = {};
            popupParams = {};
            var orgModel = null;
            var customerMasterOrg = null;
            var customerMasterOrgModel = null;
            orgModel = _scScreenUtils.getModel(
            this, "getOrganizationList_output");
			
			// ARE-502 : Added to raise CRM Issue alert on the order if CRM is unreachable : BEGIN
            modOrder = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
			//console.log("modOrder: ", modOrder);
			
            customerMasterOrgModel = _scModelUtils.getModelListFromPath("OrganizationList.Organization", orgModel)[
            0];
            _scBaseUtils.setAttributeValue("Customer.OrganizationCode", _scModelUtils.getStringValueFromPath("CustomerMasterOrganizationCode", customerMasterOrgModel), screenInput);
            _scBaseUtils.setAttributeValue("Customer.CustomerType", "02", screenInput);
			//console.log("OrderNo: ", _scModelUtils.getStringValueFromPath("Order.OrderNo", modOrder));
			_scModelUtils.addModelToModelPath("Customer.Order", _scModelUtils.getModelObjectFromPath("Order", modOrder), screenInput);
			//console.log("CI screenInput: ", screenInput);
			// ARE-502 : Added to raise CRM Issue alert on the order if CRM is unreachable : END
            popupParams["screenInput"] = screenInput;
            var dialogParams = null;
            dialogParams = {};
            dialogParams["closeCallBackHandler"] = "onCreatingNewCustomer";
            _isccsUIUtils.openSimplePopup("isccs.customer.create.CreateContact", "CreateConsumerCustomer", this, popupParams, dialogParams);
        }
});
});