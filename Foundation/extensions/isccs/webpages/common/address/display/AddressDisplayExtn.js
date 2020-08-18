
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/common/address/display/AddressDisplayExtnUI",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!isccs/utils/SharedComponentUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils"
		  ]
,
function(_dojodeclare,
		 _extnAddressDisplayExtnUI,
		 _isccsUIUtils,
		 _scBaseUtils,
		 _scModelUtils,
		 _scScreenUtils,
		 _scWidgetUtils,
		 _scEditorUtils,
		 _isccsSharedComponentUtils,
		 _scEventUtils
){ 
	return _dojodeclare("extn.common.address.display.AddressDisplayExtn", [_extnAddressDisplayExtnUI],{
	// custom code here
	
	extn_init: function(){
		var parentScreen = _isccsUIUtils.getParentScreen(this,true);
		var parentModel = null;
		var parentOrderModel = null;
		if (!_scBaseUtils.isVoid(parentScreen)){
			parentModel = _scScreenUtils.getModel(parentScreen, "getCompleteOrderLineDetails_output");
		}
		if (!_scBaseUtils.isVoid(parentScreen)){
			parentOrderModel = _scScreenUtils.getModel(parentScreen, "getCompleteOrderDetails_output");
		}
		console.log("parentOrderModel",parentOrderModel);
		var strExtnIsMigrated = "N";
		if(!_scBaseUtils.isVoid(parentOrderModel)){
			strExtnIsMigrated = _scModelUtils.getStringValueFromPath("Order.Extn.ExtnIsMigrated", parentOrderModel);
		}
		if (_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Ship To")){

			if(_scBaseUtils.equals("CustomerPreview",parentScreen.className)){
				_scWidgetUtils.showWidget(this, "lnkEditAddress", false);
			} else {
				_scWidgetUtils.hideWidget(this, "lnkEditAddress", false);
			}
			if (!_scBaseUtils.isVoid(parentModel)){
			var deliveryMethod =  _scModelUtils.getStringValueFromPath("OrderLine.DeliveryMethod", parentModel);

				if (_scBaseUtils.equals(deliveryMethod, "PICK")){
			_scWidgetUtils.setValue(this, "lblAddresType", "Pickup Location", true );	
				}
			}
		} else if (_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Bill To")
				|| _scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Refund To")
				|| _scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Return From")){

			if(_scBaseUtils.equals("CustomerPreview",parentScreen.className)
				|| (_scBaseUtils.equals("OrderSummary",parentScreen.className) 
						&& !_scBaseUtils.equals("Y",strExtnIsMigrated))
				|| (_scBaseUtils.equals("ReturnSummary",parentScreen.className)
						&& !_scBaseUtils.equals("Y",strExtnIsMigrated))
				|| _scBaseUtils.equals("AddressEntry",parentScreen.className)){
				_scWidgetUtils.showWidget(this, "lnkEditAddress", false);
			} else {
				_scWidgetUtils.hideWidget(this, "lnkEditAddress", false);
			}
		} else {
			_scWidgetUtils.hideWidget(this, "lnkEditAddress", false);
		}

	},
	getNameDisplay: function(dataValue, screen, widget, namespace, modelObject, options) {
		
			var returnValue = "";
			if (/*_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Ship To") || */
				_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Pickup Location")){


			var parentScreen = _isccsUIUtils.getParentScreen(this,true);
			var parentModel = _scScreenUtils.getModel(parentScreen, "getCompleteOrderLineDetails_output");
			var deliveryMethod =  _scModelUtils.getStringValueFromPath("OrderLine.DeliveryMethod", parentModel);
			
			if (_scBaseUtils.equals(deliveryMethod, "PICK")){
				_scWidgetUtils.setValue(this, "lblAddresType", "Pickup Location", true );	
	
				//Store AddressLine1
				_scWidgetUtils.hideWidget(this, "lblAddressLine1", false);
				_scWidgetUtils.showWidget(this, "extn_screenbase_label_AddressLine1", false);

				var addressLine1 = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.AddressLine1", parentModel);		
				_scWidgetUtils.setValue(this, "extn_screenbase_label_AddressLine1", addressLine1 , true ); 

				//Store AddressLine2
				_scWidgetUtils.hideWidget(this, "lblAddressLine2", false);
				_scWidgetUtils.showWidget(this, "extn_screenbase_label_AddressLine2", false);

				var addressLine1 = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.AddressLine2", parentModel);		
				_scWidgetUtils.setValue(this, "extn_screenbase_label_AddressLine2", addressLine1 , true ); 
				
				//Store Country
				_scWidgetUtils.hideWidget(this, "lblCountry", false);
				_scWidgetUtils.showWidget(this, "extn_screenbase_label_Country", false);
				
				var country = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.Country",parentModel);
				_scWidgetUtils.setValue(this, "extn_screenbase_label_Country", country , true ); 

				//Store Phone Number
				_scWidgetUtils.hideWidget(this, "lblDayPhone", false);
				_scWidgetUtils.showWidget(this, "extn_screenbase_label_DayPhone", false);

				var dayPhone = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.DayPhone", parentModel);		
				_scWidgetUtils.setValue(this, "extn_screenbase_label_DayPhone", dayPhone , true ); 

				//Store email
				_scWidgetUtils.hideWidget(this, "lnkEmail", false);
				_scWidgetUtils.showWidget(this, "extn_screenbase_link_Email", false);

				var email = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.EMailID", parentModel);		
				_scWidgetUtils.setValue(this, "extn_screenbase_link_Email", email , true ); 
				
				returnValue = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.Description", parentModel);

			}
			
			
		}else{
					var Address = null;
					Address = _scModelUtils.getModelObjectFromPath("PersonInfo", modelObject);
					if (!(_scBaseUtils.isVoid(Address))) {
						var sCountry = "";
						sCountry = _scModelUtils.getStringValueFromPath("PersonInfo.Country", modelObject);
						var sAddressKey = "";
						sAddressKey = _scBaseUtils.stringConcat(sCountry, "_AddressNameFormat");
						if (_scScreenUtils.hasBundleKey(screen, sAddressKey)) {
							returnValue = _scScreenUtils.getFormattedString(screen, sAddressKey, Address);
						} else {
							returnValue = _scScreenUtils.getFormattedString(screen, "AddressNameFormat", Address);
						}
					}

			}

            return returnValue;
        },

	getCityStateZip: function(dataValue, screen, widget, namespace, modelObject, options) {
            var Address = null;
            var returnValue = "";
			if (/*_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Ship To") ||*/ 
				_scBaseUtils.equals(this.uIdMap.lblAddresType.value, "Pickup Location")){


					var parentScreen = _isccsUIUtils.getParentScreen(this,true);
					var parentModel = _scScreenUtils.getModel(parentScreen, "getCompleteOrderLineDetails_output");
					var deliveryMethod =  _scModelUtils.getStringValueFromPath("OrderLine.DeliveryMethod", parentModel);
					
					if (_scBaseUtils.equals(deliveryMethod, "PICK")){
							
							_scWidgetUtils.setValue(this, "lblAddresType", "Pickup Location", true );
							Address = _scModelUtils.getModelObjectFromPath("OrderLine.Shipnode.ShipNodePersonInfo", parentModel);

							if (!(_scBaseUtils.isVoid(Address))) {
								var sCountry = "";
								sCountry = _scModelUtils.getStringValueFromPath("OrderLine.Shipnode.ShipNodePersonInfo.Country", parentModel);
								var sAddressKey = "";
								sAddressKey = _scBaseUtils.stringConcat(sCountry, "_CityStateZip");
								returnValue = _scScreenUtils.getFormattedString(screen, sAddressKey, Address);
									
									if (_scBaseUtils.equals(returnValue, sAddressKey)) {
										returnValue = _scScreenUtils.getFormattedString(screen, "CityStateZip", Address);
									}
							}
								
						
					}
			}else{
				
					Address = _scModelUtils.getModelObjectFromPath("PersonInfo", modelObject);

					if (!(_scBaseUtils.isVoid(Address))) {
						
						var sCountry = "";
						sCountry = _scModelUtils.getStringValueFromPath("PersonInfo.Country", modelObject);
						var sAddressKey = "";
						sAddressKey = _scBaseUtils.stringConcat(sCountry, "_CityStateZip");
						returnValue = _scScreenUtils.getFormattedString(screen, sAddressKey, Address);

						if (_scBaseUtils.equals(returnValue, sAddressKey)) {
							returnValue = _scScreenUtils.getFormattedString(screen, "CityStateZip", Address);
						}
					}
			}
            return returnValue;
			
        },
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
        	var parentScreen = _isccsUIUtils.getParentScreen(this,true);
        	if(_scBaseUtils.equals("ManageCustomerAddresses",parentScreen.className)){
        		_scModelUtils.setStringValueAtModelPath("CreateNewAddress", "Y", popupInputData);
        		_scModelUtils.setStringValueAtModelPath("FromCustomerCreate", "Y", popupInputData);
        	}
        	_isccsSharedComponentUtils.openAddressCapture(
        	this, popupInputData, _scScreenUtils.getModel(
        	this, "Address"), enterpriseCode, this.titleDesc, "onPopupApply");
        },
        onPopupApply: function(actionPerformed, model, popupParams) {
        	if (
        	_scBaseUtils.equals(
        	actionPerformed, "APPLY")) {
        		if (!(
        		_scBaseUtils.isVoid(
        		model))) {
        			_scWidgetUtils.setLinkText(
        			this, "lnkEditAddress", "Edit", true);
        			var parentScreen = null;
        			parentScreen = _isccsUIUtils.getParentScreen(
        			this, true);
        			_scScreenUtils.setModel(
        			this, "Address", model, null);
        			_scWidgetUtils.hideWidget(
        			this, "pnlMessage", true);
        			this.updateAddressTitle();
        			_scModelUtils.setStringValueAtModelPath("PersonInfo.EnterpriseCode", "", model);
        			_scModelUtils.setStringValueAtModelPath("PersonInfo.Selected", "", model);
        			_scBaseUtils.removeBlankAttributes(
        			model);
        			var sTitle = null;
        			var eventDefn = null;
        			var updatedAddress = null;
        			updatedAddress = {};
        			sTitle = _scBaseUtils.getStringValueFromBean("title", popupParams);
        			var blankModel = null;
        			eventDefn = {};
        			blankModel = {};
        			_scBaseUtils.setAttributeValue("argumentList", blankModel, eventDefn);
        			_scBaseUtils.setAttributeValue("argumentList.model", model, eventDefn);
        			var oldModel = null;
        			oldModel = _scBaseUtils.getValueFromPath("binding.addressCapturePageInitData", popupParams);
        			_scBaseUtils.removeBlankAttributes(
        			oldModel);
        			_scBaseUtils.setAttributeValue("argumentList.old", oldModel, eventDefn);
        			_scBaseUtils.setAttributeValue("argumentList.AddressPanelTitle", _scBaseUtils.getValueFromPath("title", popupParams), eventDefn);
        			var customerAddressModel = null;
        			customerAddressModel = _scScreenUtils.getModel(
        			this, "CustomerAddress");
        			if (!(
        			_scBaseUtils.isVoid(
        			customerAddressModel))) {
        				_scBaseUtils.setAttributeValue("argumentList.customerAdditionalAddress", customerAddressModel, eventDefn);
        			}
        			if (
        			_scBaseUtils.equals(
        			this.showContactInfo, true)) {
        				var dayPhone = null;
        				dayPhone = _scModelUtils.getStringValueFromPath("PersonInfo.DayPhone", model);
        				if (!(
        				_scBaseUtils.isVoid(
        				dayPhone))) {
        					_scWidgetUtils.showWidget(
        					this, "pnlPhoneHolder", true, null);
        				}
        				var emailID = null;
        				emailID = _scModelUtils.getStringValueFromPath("PersonInfo.EMailID", model);
        				if (!(
        				_scBaseUtils.isVoid(
        				emailID))) {
        					_scWidgetUtils.showWidget(
        					this, "pnlEmailHolder", true, null);
        				}
        			}
        			_scEventUtils.fireEventToParent(
        			this, "addressChanged", eventDefn);
        		}
        	}
        }
	});
});
