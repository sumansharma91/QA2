scDefine(["scbase/loader!dijit",
		  "scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/customer/create/CreateContactExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		 "scbase/loader!isccs/utils/CustomerUtils",		  
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!isccs/utils/SharedComponentUtils",
		  "scbase/loader!sc/plat/dojo/utils/WizardUtils"
		  ]
,
function( _dijit,
		  _dojodeclare,
	      _extnCreateContactExtnUI,
		  _isccsBaseTemplateUtils,
		  _isccsCustomerUtils,
		  _isccsUIUtils,
		  _scBaseUtils,
		  _scEventUtils,
		  _scModelUtils,
		  _scScreenUtils,
		  _scWidgetUtils,
		  _scEditorUtils,
		  _isccsSharedComponentUtils,
		  _scWizardUtils
		  
){
	var isOrderCreatePopup = false;
	var editAddress = false;
	var fromShipTo = false;
	var screen = null;
	return _dojodeclare("extn.customer.create.CreateContactExtn", [_extnCreateContactExtnUI],{
	// custom code here
	

	extn_init: function(){
		screen = this.ownerScreen;
		
		//Hiding extra tabs
		_scWidgetUtils.hideTabPanel(this, "pnlTabContainer", "pnlPaymentMethods");
		_scWidgetUtils.hideTabPanel(this, "pnlTabContainer", "pnlWebUser");
		_scWidgetUtils.hideTabPanel(this, "pnlTabContainer", "pnlOrderPermissions");
		_scWidgetUtils.hideTabPanel(this, "pnlTabContainer", "pnlAddresses");
		
        // Fix for SU-81 Start
		var updateCustomerContact = false;
		var screenInput = _isccsUIUtils.getWizardModel(this, "customerModel");
		var customerContact = _scScreenUtils.getModel(this, "getCustomerContact_output");		

		if (_scBaseUtils.isVoid(customerContact)) {
				updateCustomerContact = true;
		}	
        // Fix for SU-81 End		

		if (_scBaseUtils.isVoid(screenInput)) {
			screenInput = _scScreenUtils.getInitialInputData(this);
		}
		_scScreenUtils.setModel(this, "getCustomerDetails_output", screenInput, null);
		var existingCustomer =  _scModelUtils.getModelObjectFromKey("Customer", screenInput);
		if (_scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Status", existingCustomer), "10")) {
			_scWidgetUtils.showWidget(this, "extn_filteringselect_ShipPreference", false,false);
		}
		var contactList = _scModelUtils.getModelListFromKey("CustomerContactList", existingCustomer);
		var customerAddress = null;
		var personInfo= null;
		
		var regionShipTo = this.uIdMap.extn_filteringselectCountryRegion;
		var regionBillTo = this.uIdMap.extn_filteringselectBillToCountryRegion;
		var cmbStatus = this.uIdMap.cmbStatus;
		
		var regionShipToInfo = _dijit.byId(regionShipTo.id);
		var storeShipTo = regionShipToInfo.store;
		regionShipToInfo.value = storeShipTo._arrayOfAllItems;
		
		var regionBillToInfo = _dijit.byId(regionBillTo.id);
		var storeBillTo = regionBillToInfo.store;
		regionBillToInfo.value = storeBillTo._arrayOfAllItems;
	    if(!_scBaseUtils.isVoid(contactList)){

			if(!_scBaseUtils.isVoid(contactList.CustomerContact[0])){
				existingCustomerID = _scModelUtils.getStringValueFromPath("CustomerContactID", contactList.CustomerContact[0]);
				customerAddress = _scModelUtils.getModelListFromKey("CustomerAdditionalAddressList", contactList.CustomerContact[0]);
				
				// Fix for SU-81 Start		
				if(_scBaseUtils.isVoid(customerContact)){
					customerContact = {};
					customerContact.CustomerContact = {};
					customerContact.CustomerContact.FirstName = _scModelUtils.getStringValueFromPath("FirstName", contactList.CustomerContact[0]);
					customerContact.CustomerContact.MiddleName = _scModelUtils.getStringValueFromPath("MiddleName", contactList.CustomerContact[0]);
					customerContact.CustomerContact.LastName = _scModelUtils.getStringValueFromPath("LastName", contactList.CustomerContact[0]);
					customerContact.CustomerContact.Department = _scModelUtils.getStringValueFromPath("Department", contactList.CustomerContact[0]);
					customerContact.CustomerContact.EmailID = _scModelUtils.getStringValueFromPath("EmailID", contactList.CustomerContact[0]);
					customerContact.CustomerContact.EMailID = _scModelUtils.getStringValueFromPath("EMailID", contactList.CustomerContact[0]);
					customerContact.CustomerContact.ExtnGender = _scModelUtils.getStringValueFromPath("ExtnGender", contactList.CustomerContact[0]);
					customerContact.CustomerContact.DayPhone = _scModelUtils.getStringValueFromPath("DayPhone", contactList.CustomerContact[0]);
					customerContact.CustomerContact.MobilePhone = _scModelUtils.getStringValueFromPath("MobilePhone", contactList.CustomerContact[0]);
					customerContact.CustomerContact.EveningPhone = _scModelUtils.getStringValueFromPath("EveningPhone", contactList.CustomerContact[0]);				
					_scScreenUtils.setModel(this, "getCustomerContact_output", customerContact, null);	
				}
				// Fix for SU-81 End

				if(!_scBaseUtils.isVoid(customerAddress.CustomerAdditionalAddress[0])){
					personInfoBillTo = _scModelUtils.getModelObjectFromKey("PersonInfo", customerAddress.CustomerAdditionalAddress[1]);
					personInfoShipTo = _scModelUtils.getModelObjectFromKey("PersonInfo", customerAddress.CustomerAdditionalAddress[0]);
					var billToModel = {};
					var shipToModel = {};
					billToModel.AddressLine1 = _scModelUtils.getStringValueFromPath("AddressLine1", personInfoBillTo);
					billToModel.AddressLine2 = _scModelUtils.getStringValueFromPath("AddressLine2", personInfoBillTo);
					billToModel.City = _scModelUtils.getStringValueFromPath("City", personInfoBillTo);
					billToModel.ZipCode = _scModelUtils.getStringValueFromPath("ZipCode", personInfoBillTo);
					billToModel.State = _scModelUtils.getStringValueFromPath("State", personInfoBillTo);
					billToModel.ID = _scModelUtils.getStringValueFromPath("ID", personInfoBillTo);
					billToModel.CountryCode = _scModelUtils.getStringValueFromPath("Country", personInfoBillTo);					
					for(var i =0;i<regionBillToInfo.value.length;i++){
						if(regionBillToInfo.value[i].CodeValue[0] != undefined 
							&& !_scBaseUtils.isVoid(regionBillToInfo.value[i].CodeValue[0]) 
							&& _scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Country", personInfoBillTo),regionBillToInfo.value[i].CodeValue[0])){
							regionBillToInfo.attr('value', regionBillToInfo.value[i].CodeLongDescription[0]);
							break;
						}
					}
					
					shipToModel.AddressLine1 = _scModelUtils.getStringValueFromPath("AddressLine1", personInfoShipTo);
					shipToModel.AddressLine2 = _scModelUtils.getStringValueFromPath("AddressLine2", personInfoShipTo);
					shipToModel.City = _scModelUtils.getStringValueFromPath("City", personInfoShipTo);
					shipToModel.ZipCode = _scModelUtils.getStringValueFromPath("ZipCode", personInfoShipTo);
					shipToModel.State = _scModelUtils.getStringValueFromPath("State", personInfoShipTo);
					shipToModel.ID = _scModelUtils.getStringValueFromPath("ID", personInfoShipTo);
					shipToModel.CountryCode = _scModelUtils.getStringValueFromPath("Country", personInfoShipTo);
					for(var i =0;i<regionShipToInfo.value.length;i++){
						if(regionShipToInfo.value[i].CodeValue[0] != undefined 
							&& !_scBaseUtils.isVoid(regionShipToInfo.value[i].CodeValue[0]) 
							&& _scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Country", personInfoShipTo),regionShipToInfo.value[i].CodeValue[0])){
							regionShipToInfo.attr('value', regionShipToInfo.value[i].CodeLongDescription[0]);
							break;
						}
					}
					
					_scScreenUtils.setModel(this, "extn_BillToAddress", billToModel, null);
					_scScreenUtils.setModel(this, "extn_ShipToAddress", shipToModel, null);

//Compare the models for the ID, AddLine1, and ZipCode
	//True

			if (!_scBaseUtils.equals(personInfoBillTo.ID, personInfoShipTo.ID) 
				&& (!_scBaseUtils.equals(personInfoBillTo.ZipCode, personInfoShipTo.ZipCode) 
				|| !_scBaseUtils.equals(personInfoBillTo.AddressLine1, personInfoShipTo.AddressLine1))){
					_scWidgetUtils.setValue(this,"extn_checkboxBillTo",false,false);
					_scWidgetUtils.showWidget(this, "extn_contentpaneBillTo", false,false);
					_scWidgetUtils.hideWidget(this, "extn_AddAddressLink", false);
					_scWidgetUtils.showWidget(this, "extn_contentpaneBillToLeftSide", false,false);
					_scWidgetUtils.showWidget(this, "extn_contentpaneBillToRightSide", false,false);
			}
			// ARS-182 : commented for Demographics changes to view and edit customer opt ins : START
			//_scWidgetUtils.hideWidget(this, "DST_MainInfoPanel", false);
			// ARS-182 : commented for Demographics changes to view and edit customer opt ins : END
				}
			}
			editAddress = true;
			_scWidgetUtils.hideWidget(this, "extn_AddAddress", false);
		}else{
			_scWidgetUtils.hideWidget(this, "extn_contentpaneShipToLeft", false);
			_scWidgetUtils.hideWidget(this, "extn_contentpaneShipToRight", false);
			_scWidgetUtils.hideWidget(this, "extn_EditAddress", false);
		}

		_scWidgetUtils.hideWidget(this, "txtJobTitle", false);
		_scWidgetUtils.hideWidget(this, "txtDayFax", false);
		_scWidgetUtils.hideWidget(this, "txtEveningFax", false);

	},
	showBillToAddress: function(){
         if (_scBaseUtils.equals(this.uIdMap.extn_checkboxBillTo.checked, true)) { 
			_scWidgetUtils.hideWidget(this, "extn_contentpaneBillTo", false,false);
		}else{
			_scWidgetUtils.showWidget(this, "extn_contentpaneBillTo", false,false);
			if(editAddress){
				_scWidgetUtils.hideWidget(this, "extn_AddAddressLink", false);
				_scWidgetUtils.showWidget(this, "extn_contentpaneBillToLeftSide", false,false);
				_scWidgetUtils.showWidget(this, "extn_contentpaneBillToRightSide", false,false);
			}else{				
				_scWidgetUtils.hideWidget(this, "extn_contentpaneBillToLeftSide", false);
				_scWidgetUtils.hideWidget(this, "extn_contentpaneBillToRightSide", false);
				_scWidgetUtils.hideWidget(this, "extn_EditAddressLink", false);
			}
		}
	},
	manageCustomer: function() {
		
		var isBillToSame = _scWidgetUtils.getValue(this,"extn_checkboxBillTo");
		isBillToSame = isBillToSame.toString();
		var eCustomer = null;
		var isPopup = false;
		var stringYes = "Y";
		var stringNo = "N";
		var hasContactInfo = false;
		
		var customerShipToAddress = null;
		var customerBillToAddress = null;
		
		screenInput = _scScreenUtils.getInitialInputData(this);
		var tmodelOptions = null;
		tmodelOptions = _scBaseUtils.getNewBeanInstance();
		_scBaseUtils.setAttributeValue("allowEmpty", true, tmodelOptions);
		
		eCustomer = _scScreenUtils.getTargetModel(this, "manageCustomer_input", tmodelOptions);		
		
		var regionList = _scScreenUtils.getModel(this, "extn_getCountryList");
		var regionArray = [];
		regionArray = regionList.CommonCodeList.CommonCode;
		var index = null;

		var addressModel = _scScreenUtils.getTargetModel(this, "extn_billToAddress_input", tmodelOptions);
		customerShipToAddress = _scModelUtils.getModelObjectFromPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress",eCustomer);
		customerBillToAddress = _scModelUtils.getModelObjectFromPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress",addressModel);
		var shipToFlag = false;
		var billToFlag = false;
		var shipToCheck = false;
		var billToCheck = false;
		
		if(_scBaseUtils.isVoid(customerShipToAddress.PersonInfo.AddressLine1)){
			_scScreenUtils.showErrorMessageBox(this,"ShipTo Address Required! Please check the input and try again.",null,null,null);
			return false;
		}
		if(_scBaseUtils.isVoid(customerBillToAddress.PersonInfo.AddressLine1)){
			_scScreenUtils.showErrorMessageBox(this,"BillTo Address Required! Please check the input and try again.",null,null,null);
			return false;
		}
		
		
		for(var i = 0; i < regionArray.length;i++ ){
		
			if(_scBaseUtils.equals(customerShipToAddress.PersonInfo.Country,regionArray[i].CodeLongDescription)){
				index = i;
				customerShipToAddress.PersonInfo.Country = regionArray[i].CodeValue;
				shipToCheck = true;
			}	
			if(_scBaseUtils.equals(customerBillToAddress.PersonInfo.Country,regionArray[i].CodeLongDescription)){
				index = i;
				customerBillToAddress.PersonInfo.Country = regionArray[i].CodeValue;
				billToCheck = true;
			}
			if(_scBaseUtils.equals(shipToCheck, true) && _scBaseUtils.equals(billToCheck, true)){
				break;
			}
		}
		var contactList = _scModelUtils.getModelListFromKey("CustomerContactList", _scModelUtils.getModelObjectFromKey("Customer", screenInput));
		var existingCustomerID = null;
		
		if(!_scBaseUtils.isVoid(contactList)){
			if(!_scBaseUtils.isVoid(contactList.CustomerContact[0])){
				existingCustomerID = _scModelUtils.getStringValueFromPath("CustomerContactID", contactList.CustomerContact[0]);
			}
		}		
		_scModelUtils.setStringValueAtModelPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.Reset",stringYes, eCustomer);
		var shipToID = _scModelUtils.getStringValueFromPath("ID", _scScreenUtils.getModel(this, "extn_ShipToAddress"));
        var billToID = _scModelUtils.getStringValueFromPath("ID", _scScreenUtils.getModel(this, "extn_BillToAddress"));

		var addressList;
		if(_scBaseUtils.equals(isBillToSame, "on")){
			customerBillToAddress = _scBaseUtils.cloneModel(customerShipToAddress);
		}
		if(!_scBaseUtils.isVoid(existingCustomerID) && _scBaseUtils.equals(shipToID,billToID)){
			customerShipToAddress.PersonInfo.IsBillTo = stringYes;
			customerShipToAddress.PersonInfo.IsDefaultBillTo = stringYes;
			customerBillToAddress.PersonInfo.IsShipTo = stringYes;
			customerBillToAddress.PersonInfo.IsDefaultShipTo = stringYes;
		}else{
			customerBillToAddress.PersonInfo.IsShipTo = stringNo;
			customerBillToAddress.PersonInfo.IsDefaultShipTo = stringNo;
			customerShipToAddress.PersonInfo.IsBillTo = stringNo;
			customerShipToAddress.PersonInfo.IsDefaultBillTo = stringNo;
		}
		customerShipToAddress.PersonInfo.IsShipTo = stringYes;
		customerShipToAddress.PersonInfo.IsDefaultShipTo = stringYes;
		customerBillToAddress.PersonInfo.IsBillTo = stringYes;
		customerBillToAddress.PersonInfo.IsDefaultBillTo = stringYes;

		if (_scBaseUtils.isVoid(existingCustomerID)){
			customerShipToAddress.PersonInfo.ID = "1";
			customerBillToAddress.PersonInfo.ID = "2";
			addressList = [customerShipToAddress,customerBillToAddress];
			_scModelUtils.addListToModelPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress", addressList, eCustomer);
			_scBaseUtils.removeBlankAttributes(eCustomer);
			isPopup = _scScreenUtils.isPopup(this);
			
			if (_scBaseUtils.equals(isPopup, true)) {
				// ARE-502 : Added to raise CRM Issue alert on the order if CRM is unreachable : BEGIN
				var modScreenInput = _scScreenUtils.getModel(this, "screenInput");
				var modOrder = _scModelUtils.getModelObjectFromPath("Customer.Order", modScreenInput);
				if (!_scBaseUtils.isVoid(modOrder)) {
					_scModelUtils.addModelToModelPath("Customer.Order", modOrder, eCustomer);
				}
				// ARE-502 : Added to raise CRM Issue alert on the order if CRM is unreachable : END
				_isccsUIUtils.callApi(this, eCustomer, "orderEntryManageCustomer", null);
			} else {
				_isccsUIUtils.callApi(this, eCustomer, "manageCustomer", null);
			}
		}else{
			customerShipToAddress.PersonInfo.ID = shipToID;
			customerBillToAddress.PersonInfo.ID = billToID;
			if(_scBaseUtils.equals(shipToID, billToID)){
				customerShipToAddress.PersonInfo.ID = "99";
			}
			addressList = [customerShipToAddress,customerBillToAddress];
			_scModelUtils.addListToModelPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress", addressList, eCustomer);
			_scModelUtils.setStringValueAtModelPath("Customer.CustomerID", existingCustomerID, eCustomer);
			_scModelUtils.setStringValueAtModelPath("Customer.CustomerKey", existingCustomerID, eCustomer);
			var modelCustomerAdditionalAddress = _scModelUtils.getStringValueFromPath("Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress", eCustomer);
			for(var i=0;i<modelCustomerAdditionalAddress.length;i++){
				_scModelUtils.setStringValueAtModelPath("PersonInfo.DayPhone", eCustomer.Customer.CustomerContactList.CustomerContact.DayPhone, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.EmailID", eCustomer.Customer.CustomerContactList.CustomerContact.EmailID, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.EMailID", eCustomer.Customer.CustomerContactList.CustomerContact.EmailID, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.FirstName", eCustomer.Customer.CustomerContactList.CustomerContact.FirstName, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.LastName", eCustomer.Customer.CustomerContactList.CustomerContact.LastName, modelCustomerAdditionalAddress[i]);
			}
			
			var strExtnPreferredCarrier = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrier", eCustomer);
			var strExtnTaxExemptionCode = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnTaxExemptionCode", eCustomer);
			var custtaxExemptionCode =  _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnTaxExemptionCode",
					_scScreenUtils.getModel(this, "getCustomerDetails_output"));
			var custPreferredCarrier = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrier",
					_scScreenUtils.getModel(this, "getCustomerDetails_output"));
			if(!(strExtnPreferredCarrier === custPreferredCarrier)){
				eCustomer.Customer.HasCustomerAttributes = "Y";
				eCustomer.Customer.SetPreferredCarrier = "Y";
				eCustomer.Customer.Extn.ExtnOriginalPreferredCarrier = custPreferredCarrier;
				if(_scBaseUtils.isVoid(custPreferredCarrier)){
					eCustomer.Customer.CarrierRecordState = "Added";
				}else if(_scBaseUtils.isVoid(strExtnPreferredCarrier)){
					eCustomer.Customer.CarrierRecordState = "Deleted";
				}else{
					eCustomer.Customer.CarrierRecordState = "Modified";
				}
			}
			if(!(strExtnTaxExemptionCode === custtaxExemptionCode)){
				eCustomer.Customer.HasCustomerAttributes = "Y";
				eCustomer.Customer.SetTaxExemptionCode = "Y";
				if(_scBaseUtils.isVoid(custtaxExemptionCode)){
					eCustomer.Customer.TaxExemptionRecordState = "Added";
				}else if(_scBaseUtils.isVoid(strExtnTaxExemptionCode)){
					eCustomer.Customer.TaxExemptionRecordState = "Deleted";
				}else{
					eCustomer.Customer.TaxExemptionRecordState = "Modified";
				}
			}
			
			_scModelUtils.setStringValueAtModelPath("Customer.CustomerContactList.CustomerContact.EMailID", 
					eCustomer.Customer.CustomerContactList.CustomerContact.EmailID,eCustomer);
			_scBaseUtils.removeBlankAttributes(eCustomer);
			_isccsUIUtils.callApi(this, eCustomer, "extn_modifyCustomer_RefID", null);
		}
    },
    validateMobilePhone: function(){
		var model = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null);
		var phoneNo = _scModelUtils.getStringValueFromPath("Customer.CustomerContactList.CustomerContact.MobilePhone", model);
	
		if(!_scBaseUtils.isVoid(phoneNo) && !_scBaseUtils.numberEquals(phoneNo.length,10)){
			_scScreenUtils.showInfoMessageBox(this, "Mobile Number must be 10 digits.", "handleInfoMsgConfirmation", null);
		}
	},
    validateDayPhone: function(){
		var model = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null);
		var phoneNo = _scModelUtils.getStringValueFromPath("Customer.CustomerContactList.CustomerContact.DayPhone", model);
	
		if(!_scBaseUtils.isVoid(phoneNo) && !_scBaseUtils.numberEquals(phoneNo.length,10)){
			_scScreenUtils.showInfoMessageBox(this, "Telephone Number must be 10 digits.", "handleInfoMsgConfirmation", null);
		}
	} ,
    validateEveningPhone: function(){
		var model = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null);
		var phoneNo = _scModelUtils.getStringValueFromPath("Customer.CustomerContactList.CustomerContact.EveningPhone", model);
	
		if(!_scBaseUtils.isVoid(phoneNo) && !_scBaseUtils.numberEquals(phoneNo.length,10)){
			_scScreenUtils.showInfoMessageBox(this, "Evening Phone Number must be 10 digits.", "handleInfoMsgConfirmation", null);
		}
	},
	saveExtnCustomerAttributes: function(modelOutput) {
		var inputModel = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null);
		var callModifyCustomer = false;

		var strExtnPreferredCarrier = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrier", inputModel);
		var strExtnTaxExemptionCode = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnTaxExemptionCode", inputModel);
		
		if(!_scBaseUtils.isVoid(strExtnPreferredCarrier)){
			modelOutput.Customer.HasCustomerAttributes = "Y";
			modelOutput.Customer.SetPreferredCarrier = "Y";
			modelOutput.Customer.Extn.ExtnPreferredCarrier = strExtnPreferredCarrier;
			modelOutput.Customer.CarrierRecordState = "Added";
			callModifyCustomer = true;
		}
		if(!_scBaseUtils.isVoid(strExtnTaxExemptionCode)){
			modelOutput.Customer.HasCustomerAttributes = "Y";
			modelOutput.Customer.SetTaxExemptionCode = "Y";
			modelOutput.Customer.Extn.ExtnTaxExemptionCode = strExtnTaxExemptionCode;
			modelOutput.Customer.TaxExemptionRecordState = "Added";
			callModifyCustomer = true;
		}
		
		if(callModifyCustomer){
			modelOutput.Customer.CustomerContactList.CustomerContact[0].EMailID = modelOutput.Customer.CustomerContactList.CustomerContact[0].EmailID;
			var modelCustomerAdditionalAddress =[]; 
			modelCustomerAdditionalAddress = 
				modelOutput.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress;
			for(var i=0;i<modelCustomerAdditionalAddress.length;i++){
				_scModelUtils.setStringValueAtModelPath("PersonInfo.DayPhone", 
						modelOutput.Customer.CustomerContactList.CustomerContact[0].DayPhone, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.EmailID", 
						modelOutput.Customer.CustomerContactList.CustomerContact[0].EmailID, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.EMailID", 
						modelOutput.Customer.CustomerContactList.CustomerContact[0].EmailID, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.FirstName", 
						modelOutput.Customer.CustomerContactList.CustomerContact[0].FirstName, modelCustomerAdditionalAddress[i]);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.LastName", 
						modelOutput.Customer.CustomerContactList.CustomerContact[0].LastName, modelCustomerAdditionalAddress[i]);
				modelOutput.Customer.ExcludeCustomer = "Y"; 
			}
			_isccsUIUtils.callApi(this, modelOutput, "extn_modifyCustomer_RefID", null);
		}else if(isOrderCreatePopup)
		{
			var actionPerformed="APPLY";
			var model = modelOutput;
			var popupParams = null;
			popupParams = {};
			screen.onCreatingNewCustomer(actionPerformed, model, popupParams);
			// _scScreenUtils.setPopupOutput(this, modelOutput);
	        _scWidgetUtils.closePopup(this, "APPLY", false); 
			isOrderCreatePopup = false;
		}
		else{
			var item = _scModelUtils.getStringValueFromPath("Customer", modelOutput);
			var inputDoc = {};
			inputDoc.Customer = {};
			_scModelUtils.addStringValueToModelObject("CustomerID", item.CustomerID, inputDoc.Customer);
			_scModelUtils.addStringValueToModelObject("CustomerKey", item.CustomerKey, inputDoc.Customer);
			_scModelUtils.addStringValueToModelObject("OrganizationCode", "VSI.com", inputDoc.Customer);
			_isccsUIUtils.callApi(this, inputDoc, "getCustomerDetails", null);
		}
	},
	handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {        

		  if (_scBaseUtils.equals(mashupRefId, "orderEntryManageCustomer")) {
				if(_scBaseUtils.equals("N",modelOutput.Customer.IsCallSuccessfull)){
					_scScreenUtils.showErrorMessageBox(this,"Something went wrong! Please check the input and try again.",null,null,null);
					return false;
				}
                if (!(_scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "manageCustomer_output", modelOutput, null);
                }
                isOrderCreatePopup = true;
    			//1B CRM Enhancements
				if(_scBaseUtils.equals(modelOutput.Customer.OrganizationCode,"DEFAULT")){
					if(_scBaseUtils.equals(modelOutput.Customer.CustomerID,"Error")){
					_scScreenUtils.showErrorMessageBox(this,"Email id is already associated with another customer.",null,null,null);
					return false;
				}
					var actionPerformed="APPLY";
					var model = modelOutput;
					var popupParams = null;
					popupParams = {};
					screen.onCreatingNewCustomer(actionPerformed, model, popupParams);
					// _scScreenUtils.setPopupOutput(this, modelOutput);
					_scWidgetUtils.closePopup(this, "APPLY", false); 
					isOrderCreatePopup = false;
				}else{
					if(_scBaseUtils.equals(modelOutput.Customer.CustomerID,"Error")){
					_scScreenUtils.showErrorMessageBox(this,"Email id is already associated with another customer.",null,null,null);
					return false;
				}
					var actionPerformed="APPLY";
					var model = modelOutput;
					var popupParams = null;
					popupParams = {};
					screen.onCreatingNewCustomer(actionPerformed, model, popupParams);					
			        _scWidgetUtils.closePopup(this, "APPLY", false); 
					isOrderCreatePopup = false;
				}
            }
            if ( _scBaseUtils.equals( mashupRefId, "manageCustomer")) {
				if(_scBaseUtils.equals("N",modelOutput.Customer.IsCallSuccessfull)){
					_scScreenUtils.showErrorMessageBox(this,"Something went wrong! Please check the input and try again.",null,null,null);
					return false;
				}
                if (!(  _scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "manageCustomer_output", modelOutput, null);
                }
				
              //1B CRM Enhancements
				if(_scBaseUtils.equals(modelOutput.Customer.OrganizationCode,"DEFAULT")){
					if(_scBaseUtils.equals(modelOutput.Customer.CustomerID,"Error")){
					_scScreenUtils.showErrorMessageBox(this,"Email id is already associated with another customer.",null,null,null);
					return false;
				}
					var item = _scModelUtils.getStringValueFromPath("Customer", modelOutput);
					var inputDoc = {};
					inputDoc.Customer = {};
					_scModelUtils.addStringValueToModelObject("CustomerID", item.CustomerID, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("CustomerKey", item.CustomerKey, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("OrganizationCode", "DEFAULT", inputDoc.Customer);
					_isccsUIUtils.callApi(this, inputDoc, "getCustomerDetails", null);
				}else{
					if(_scBaseUtils.equals(modelOutput.Customer.CustomerID,"Error")){
					_scScreenUtils.showErrorMessageBox(this,"Email id is already associated with another customer.",null,null,null);
					return false;
				}
					var item = _scModelUtils.getStringValueFromPath("Customer", modelOutput);
					var inputDoc = {};
					inputDoc.Customer = {};
					_scModelUtils.addStringValueToModelObject("CustomerID", item.CustomerID, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("CustomerKey", item.CustomerKey, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("OrganizationCode", "VSI.com", inputDoc.Customer);
					
					_isccsUIUtils.callApi(this, inputDoc, "getCustomerDetails", null);
				}
            }
            if ( _scBaseUtils.equals(mashupRefId, "getCustomerDetails")) {
            	if(!_scBaseUtils.equals(modelOutput.Customer.OrganizationCode,"DEFAULT")){
            	modelOutput.Customer.OrganizationCode="VSI.com";
				}
            	if (!( _scBaseUtils.equals(false, applySetModel))) {
					_scScreenUtils.setModel(this, "getCustomerDetails_output", modelOutput, null);
				}

				var eContact = null;
				var screenInput = null;
				screenInput = _scScreenUtils.getModel(this, "screenInput");
				if (_scBaseUtils.equals("02", _scModelUtils.getStringValueFromPath("Customer.CustomerType", screenInput))) {
					eContact = _isccsCustomerUtils.getConsumerContactModel(modelOutput);
				}
				if (!(_scBaseUtils.isVoid(eContact))) {
					_scScreenUtils.setModel(this, "getCustomerContact_output", eContact, null);
				}
				this.loadAddresses();
				
				_scWizardUtils.closeWizard(this.getOwnerScreen());
				_isccsUIUtils.openWizardInEditor("isccs.customer.wizards.customerDetails.CustomerDetailsWizard", modelOutput, "isccs.editors.CustomerEditor", this);
            }
			if ( _scBaseUtils.equals( mashupRefId, "extn_modifyCustomer_RefID")){
				if(!_scBaseUtils.equals(modelOutput.Customer.OrganizationCode,"DEFAULT")){
				modelOutput.Customer.OrganizationCode = "VSI.com";
				}
				if(_scBaseUtils.equals("N",modelOutput.Customer.IsCallSuccessfull)){
					_scScreenUtils.showErrorMessageBox(this,"Something went wrong! Please check the input and try again.",null,null,null);
					return false;
				}

				if (!(  _scBaseUtils.equals(false, applySetModel))) {
                    _scScreenUtils.setModel(this, "manageCustomer_output", modelOutput, null);
                }
				if(isOrderCreatePopup){
					var actionPerformed="APPLY";
					var model = modelOutput;
					var popupParams = null;
					popupParams = {};
					screen.onCreatingNewCustomer(actionPerformed, model, popupParams);					
			        _scWidgetUtils.closePopup(this, "APPLY", false); 
					isOrderCreatePopup = false;
				}else{
					
					var item = _scModelUtils.getStringValueFromPath("Customer", modelOutput);
					var inputDoc = {};
					inputDoc.Customer = {};
					_scModelUtils.addStringValueToModelObject("CustomerID", item.CustomerID, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("CustomerKey", item.CustomerKey, inputDoc.Customer);
					_scModelUtils.addStringValueToModelObject("OrganizationCode", "VSI.com", inputDoc.Customer);
					
					_isccsUIUtils.callApi(this, inputDoc, "getCustomerDetails", null);
				}
            }
        },
		// Fix for SU-81 Start
		initializeScreen: function(event, bEvent, ctrl, args) {
            //do nothing
        },
		loadAddresses: function() { 
             //do nothing
       },
		// Fix for SU-81 End
       openEditAddress: function(event, bEvent, ctrl, args) {
    		var enterpriseCode = null;
    		var eOrganization = null;
    		eOrganization = _scScreenUtils.getModel(
    		this, "Enterprise");
    		if (!(
    		_scBaseUtils.isVoid(
    		eOrganization))) {
    			enterpriseCode = _scModelUtils.getStringValueFromKey("EnterpriseCode", eOrganization);
    			if (
    			_scBaseUtils.isVoid(
    			enterpriseCode)) {
    				enterpriseCode = _scModelUtils.getStringValueFromKey("OrganizationCode", eOrganization);
    			}
    		} else {
    			enterpriseCode = this.EnterpriseCode;
    		}
    		var popupInputData = null;
    		popupInputData = _scScreenUtils.getModel(
    		this, "PopupInput");
    		if (
    		_scBaseUtils.isVoid(
    		popupInputData)) {
    			var editorInput = null;
    			editorInput = _scScreenUtils.getInitialInputData(
    			_scEditorUtils.getCurrentEditor());
    			popupInputData = _scBaseUtils.cloneModel(
    			editorInput);
    		}
    		var linkText = null;
    		linkText = _scWidgetUtils.getValue(
    		this, "lnkEditAddress");
    		if (
    		_scBaseUtils.equals(
    		linkText, _scScreenUtils.getString(
    		this, "Add_Address"))) {
    			_scModelUtils.setStringValueAtModelPath("Order.Option", "Add", popupInputData);
    			_scModelUtils.setStringValueAtModelPath("Order.Edit", "Y", popupInputData);
    		} else if (
    		_scBaseUtils.equals(
    		linkText, _scScreenUtils.getString(
    		this, "Edit"))) {
    			_scModelUtils.setStringValueAtModelPath("Order.Edit", "Y", popupInputData);
    		} else {
    			_scModelUtils.setStringValueAtModelPath("Order.Edit", "N", popupInputData);
    		}
    		var addressModel= {};
    		addressModel.PersonInfo={};
    		
    		if(_scBaseUtils.equals(bEvent._originatingControlUId, "extn_EditAddress") || 
    				_scBaseUtils.equals(bEvent._originatingControlUId, "extn_AddAddress")){
				
    			addressModel.PersonInfo = _scScreenUtils.getModel(this, "extn_ShipToAddress");
    			if((!_scBaseUtils.isVoid(addressModel.PersonInfo))
					&& !_scBaseUtils.isVoid(addressModel.PersonInfo.CountryCode))
    			{
    				addressModel.PersonInfo.Country = addressModel.PersonInfo.CountryCode;
    				delete addressModel.PersonInfo["CountryCode"];  
    			}
    			fromShipTo = true;
    		}else{
    			addressModel.PersonInfo = _scScreenUtils.getModel(this, "extn_BillToAddress");
    			if((!_scBaseUtils.isVoid(addressModel.PersonInfo))
					&& !_scBaseUtils.isVoid(addressModel.PersonInfo.CountryCode))
    			{
    				addressModel.PersonInfo.Country = addressModel.PersonInfo.CountryCode; 
    				delete addressModel.PersonInfo["CountryCode"];  
    			}
    			fromShipTo = false;
    		}
    		_scScreenUtils.setModel(this, "Address",addressModel, null);
    		popupInputData.FromCustomerCreate = "Y";
    		_isccsSharedComponentUtils.openAddressCapture(
    		this, popupInputData, _scScreenUtils.getModel(
    		this, "Address"), enterpriseCode, "Address", "onPopupApply");
    	},
    	onPopupApply: function(actionPerformed, model, popupParams) {
    		
    		if (_scBaseUtils.equals(actionPerformed, "APPLY")) {
                if (!(_scBaseUtils.isVoid(model))) {
                    _scWidgetUtils.setLinkText(
                    this, "lnkEditAddress", "Edit", true);
                    var parentScreen = null;
                    parentScreen = _isccsUIUtils.getParentScreen(
                    this, true);
                    _scScreenUtils.setModel(
                    this, "Address", model, null);
                    _scWidgetUtils.hideWidget(
                    this, "pnlMessage", true);
                    var personInfo = _scModelUtils.getModelObjectFromKey("PersonInfo", model);
                    var billToModel = {};
					billToModel.AddressLine1 = _scModelUtils.getStringValueFromPath("AddressLine1", personInfo);
					billToModel.AddressLine2 = _scModelUtils.getStringValueFromPath("AddressLine2", personInfo);
					billToModel.City = _scModelUtils.getStringValueFromPath("City", personInfo);
					billToModel.ZipCode = _scModelUtils.getStringValueFromPath("ZipCode", personInfo);
					billToModel.State = _scModelUtils.getStringValueFromPath("State", personInfo);
					billToModel.Country = _scModelUtils.getStringValueFromPath("Country", personInfo);
					billToModel.ID = _scModelUtils.getStringValueFromPath("ID", _scScreenUtils.getModel(this, "extn_BillToAddress"));
					
					var shipToModel = {};
					shipToModel.AddressLine1 = _scModelUtils.getStringValueFromPath("AddressLine1", personInfo);
					shipToModel.AddressLine2 = _scModelUtils.getStringValueFromPath("AddressLine2", personInfo);
					shipToModel.City = _scModelUtils.getStringValueFromPath("City", personInfo);
					shipToModel.ZipCode = _scModelUtils.getStringValueFromPath("ZipCode", personInfo);
					shipToModel.State = _scModelUtils.getStringValueFromPath("State", personInfo);
					shipToModel.Country = _scModelUtils.getStringValueFromPath("Country", personInfo);
					shipToModel.ID = _scModelUtils.getStringValueFromPath("ID", _scScreenUtils.getModel(this, "extn_ShipToAddress"));
					
					if(_scBaseUtils.equals(this.uIdMap.extn_checkboxBillTo.checked, true)){
						_scScreenUtils.setModel(this, "extn_ShipToAddress", shipToModel, null);
						_scScreenUtils.setModel(this, "extn_BillToAddress", billToModel, null);
					}else if(fromShipTo){
						_scScreenUtils.setModel(this, "extn_ShipToAddress", shipToModel, null);
					}else{
						_scScreenUtils.setModel(this, "extn_BillToAddress", billToModel, null);
					}
                    //PersonInfo                    
                    var regionShipTo = this.uIdMap.extn_filteringselectCountryRegion;
            		var regionBillTo = this.uIdMap.extn_filteringselectBillToCountryRegion;
            		var cmbStatus = this.uIdMap.cmbStatus;
            		
            		var regionShipToInfo = _dijit.byId(regionShipTo.id);
            		var storeShipTo = regionShipToInfo.store;
            		regionShipToInfo.value = storeShipTo._arrayOfAllItems;
            		
            		var regionBillToInfo = _dijit.byId(regionBillTo.id);
            		var storeBillTo = regionBillToInfo.store;
            		regionBillToInfo.value = storeBillTo._arrayOfAllItems;
            		
        			if(!_scBaseUtils.isVoid(_scScreenUtils.getModel(this, "extn_ShipToAddress"))){
        				_scWidgetUtils.showWidget(this, "extn_contentpaneShipToLeft", false,false);
            			_scWidgetUtils.showWidget(this, "extn_contentpaneShipToRight", false,false);
            			_scWidgetUtils.showWidget(this, "extn_EditAddress", false,false);
            			_scWidgetUtils.hideWidget(this, "extn_AddAddress", false);
            			
            			for(var i =0;i<regionShipToInfo.value.length;i++){
            				if(regionShipToInfo.value[i].CodeValue[0] != undefined 
            					&& !_scBaseUtils.isVoid(regionShipToInfo.value[i].CodeValue[0]) 
            					&& _scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Country", personInfo),regionShipToInfo.value[i].CodeValue[0])){
            					regionShipToInfo.attr('value', regionShipToInfo.value[i].CodeLongDescription[0]);
            					break;
            				}
            			}
            			
        			}
        			if(!_scBaseUtils.isVoid(_scScreenUtils.getModel(this, "extn_BillToAddress"))){
        				_scWidgetUtils.showWidget(this, "extn_EditAddressLink", false,false);
            			_scWidgetUtils.hideWidget(this, "extn_AddAddressLink", false);
        				_scWidgetUtils.showWidget(this, "extn_contentpaneBillToLeftSide", false,false);
            			_scWidgetUtils.showWidget(this, "extn_contentpaneBillToRightSide", false,false);
            			
            			for(var i =0;i<regionBillToInfo.value.length;i++){
            				if(regionBillToInfo.value[i].CodeValue[0] != undefined 
            					&& !_scBaseUtils.isVoid(regionBillToInfo.value[i].CodeValue[0]) 
            					&& _scBaseUtils.equals(_scModelUtils.getStringValueFromPath("Country", personInfo),regionBillToInfo.value[i].CodeValue[0])){
            					regionBillToInfo.attr('value', regionBillToInfo.value[i].CodeLongDescription[0]);
            					break;
            				}
            			}
            			
        			}
        			editAddress = true;
        		}
            }
    	}
	});
});
