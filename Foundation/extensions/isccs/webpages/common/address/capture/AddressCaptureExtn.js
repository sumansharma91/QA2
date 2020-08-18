
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/common/address/capture/AddressCaptureExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!isccs/utils/CustomerUtils",
		  "scbase/loader!isccs/utils/EventUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils"
		  ]
,
function(			 
		    _dojodeclare,
		    _extnAddressCaptureExtnUI,
		    _isccsBaseTemplateUtils,
		    _isccsCustomerUtils,
		    _isccsEventUtils,
		    _isccsUIUtils,
		    _scBaseUtils,
		    _scEventUtils,
		    _scModelUtils,
		    _scScreenUtils,
		    _scWidgetUtils,
		    _scEditorUtils
				
){ 
	isChangeAddress="N";
	
	return _dojodeclare("extn.common.address.capture.AddressCaptureExtn", [_extnAddressCaptureExtnUI],{
	// custom code here
	currentZipCodeMoniker: "",
	currentAddressLine1Moniker:"",
	isAddLine1ComboLoadedFirstTime:"",
	isRefineCalledFromAddLine:"",
	isError:"",
	addressVerified:"",
	currentAddressLine1InfoMessage:"",
	
	//OOB Method Override for Sync with QAS WebService
	raiseOnEnter: function(event, bEvent, ctrl, args) {
		if (_isccsEventUtils.isEnterPressed(event)) {
			//Added Custom Code Here
			var widget = _scEventUtils.getOriginatingControlUId(bEvent);
			this.createInputForQASCall(widget);
			//Added Custom Code Here
			_scEventUtils.fireEventToParent( this, "onEnterEvent", args);
		}
    },
    createInputForQASCall : function(widget){
		//console.log("createInputForQASCall->widget", this.getWidgetByUId(widget));
		//console.log(this.identifierId);
		var countryCode = this.getOwnerScreen().getWidgetByUId("cmbCountry").value;
		//console.log();
                    //return;
		var eleGetAddressModel = _scModelUtils.createNewModelObjectWithRootKey("GetAddress");
		var eleAddressListModel = _scModelUtils.createNewModelObjectWithRootKey("AddressList");
		var eleAddressModel = _scModelUtils.createNewModelObjectWithRootKey("Address");
		var addressModel = _scModelUtils.getModelObjectFromPath("Address", eleAddressModel);
		var zipCode = this.getWidgetByUId("txtZipCode").displayedValue;
		console.log('dddd',countryCode);
		//return;
		if(_scBaseUtils.equals("US", countryCode)){
			countryCode = "USA";
		}else if(_scBaseUtils.equals("CA", countryCode)){
			countryCode = "CAN";
		}
		else
		{
			countryCode = "INVALID";
		}
		
		if(!_scBaseUtils.equals(countryCode,"INVALID"))
		{
			_scModelUtils.setStringValueAtModelPath("Country",countryCode, addressModel);
			_scModelUtils.addModelObjectAsChildToModelObject("AddressList",eleAddressModel, eleAddressListModel);
			_scModelUtils.addModelObjectAsChildToModelObject("GetAddress", eleAddressListModel, eleGetAddressModel);
			
			var currAction = "";
			var currMoniker = "";
			
			//txtZipCode
			if(!_scBaseUtils.isVoid(zipCode) && _scBaseUtils.equals('Y',widget)){
				currAction = "Search";
				_scModelUtils.setStringValueAtModelPath("ZipCode",zipCode, addressModel);
			}

			
			if(_scBaseUtils.equals("txtZipCode", widget) && 
				!_scBaseUtils.isVoid(_scWidgetUtils.getValue(this, "txtZipCode"))){
				isChangeAddress = "N";
				currAction = "Search";
				_scModelUtils.setStringValueAtModelPath("ZipCode",zipCode, addressModel);
				
			}

			if((!_scBaseUtils.isVoid(_scWidgetUtils.getValue(this, "txtAddressLine1")) &&
			_scBaseUtils.equals("txtAddressLine1", widget)) || _scBaseUtils.equals("comingfromapply", widget)){
				 var parentScreen = this.getOwnerScreen();
				parentScreen.enterpressedinaddline1 = "Y";
				var addressLine1 = this.getWidgetByUId("txtAddressLine1").displayedValue;
				currMoniker = this.currentZipCodeMoniker;
				_scModelUtils.setStringValueAtModelPath("AddressLine1", addressLine1, addressModel);
				currAction = "Refine";
				this.isRefineCalledFromAddLine = "1";
			}
			if(!_scBaseUtils.isVoid(_scWidgetUtils.getValue(this, "extn_addressLine1")) &&
			_scBaseUtils.equals("extn_addressLine1", widget)){
				currMoniker = this.currentAddressLine1Moniker;
				currAction = "GetAddress";
				this.isRefineCalledFromAddLine = "1";
			}
			if(!_scBaseUtils.isVoid(_scWidgetUtils.getValue(this, "txtAddressLine2")) && 
			_scBaseUtils.equals("txtAddressLine2", widget)  && !_scBaseUtils.equals("CA",this.identifierId)){
				var addressLine2 = this.getWidgetByUId("txtAddressLine2").displayedValue;
				currMoniker = this.currentAddressLine1Moniker;
				_scModelUtils.setStringValueAtModelPath("AddressLine2", addressLine2, addressModel);
				currAction = "Refine";
				this.isRefineCalledFromAddLine = "2";
			}
			if(!_scBaseUtils.isVoid(currAction)){
				_scModelUtils.setStringValueAtModelPath("GetAddress.Action", currAction, eleGetAddressModel);
				 if(_scBaseUtils.equals("Search", currAction)){
					console.log("Input to Service : eleGetAddressModel>>>>",eleGetAddressModel);
					_isccsUIUtils.callApi(this, eleGetAddressModel, "extn_Address_Capture_Verification_Mashup_RefId", null);
				 } else if (!_scBaseUtils.isVoid(currMoniker)){
					console.log("Input to Service : eleGetAddressModel>>>>",eleGetAddressModel);
					_scModelUtils.setStringValueAtModelPath("Moniker", currMoniker, addressModel);
					_isccsUIUtils.callApi(this, eleGetAddressModel, "extn_Address_Capture_Verification_Mashup_RefId", null);
				 }			
			}
		}
				
	},
	onExtnMashupCompletion:function(dataValue, screen, widget, nameSpace, model){
		var mashupRefId = nameSpace.mashupContext.mashupArray[0].mashupRefId;
		var modelOutput = nameSpace.mashupContext.mashupArray[0].mashupRefOutput;
		if (_scBaseUtils.equals(mashupRefId, "extn_Address_Capture_Verification_Mashup_RefId")) {
			var getAddressModel = _scModelUtils.getModelObjectFromPath("GetAddress", modelOutput);
			console.log(getAddressModel);
			var action = _scModelUtils.getStringValueFromPath("Action", getAddressModel);
			console.log("action",action);
			var errorMessage = _scModelUtils.getStringValueFromPath("ErrorMessage", getAddressModel);
			var infoMessage = _scModelUtils.getStringValueFromPath("InfoMessage", getAddressModel);
			
			if(_scBaseUtils.equals("Search", action)){
				if(!(_scBaseUtils.isVoid(errorMessage))){
					
					//_scWidgetUtils.setValue(this,"txtAddressLine1","");
					this.isError="Y";
					_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
					_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
					_scWidgetUtils.setValue(this,"txtAddressLine1", "");
					
					_scWidgetUtils.setValue(this,"txtAddressLine2","");
					_scWidgetUtils.setValue(this,"txtCity","");
					_scWidgetUtils.setValue(this,"txtState","");
					//_scWidgetUtils.markFieldinError(this,"txtZipCode", errorMessage, true);
					 _isccsBaseTemplateUtils.showMessage(
                		this, errorMessage, "error", null);
					 var parentScreen = this.getOwnerScreen();
					 parentScreen.errormsgfromChild = errorMessage;
				}else{
					if(!_scBaseUtils.isVoid(infoMessage))
					{
						 _isccsBaseTemplateUtils.showMessage(
								this, infoMessage, "information", null);
								//alert('hii');
					}
					else
					{
						_isccsBaseTemplateUtils.hideMessage(
	                		this);
					}
					this.isError="";
					_scWidgetUtils.clearFieldinError(this,'txtZipCode');
					var addressList = _scModelUtils.getModelListFromPath("AddressList.Address", getAddressModel);
					console.log("addressList",addressList);
					this.currentZipCodeMoniker = _scModelUtils.getStringValueFromPath("Moniker", addressList[0]);
					console.log("currentZipCodeMoniker >>> ", this.currentZipCodeMoniker);
					if (!(_scBaseUtils.isVoid(this.currentZipCodeMoniker ))){
						
						if(_scBaseUtils.equals(isChangeAddress,"N"))
						{
							_scWidgetUtils.setFocusOnWidgetUsingUid(this, "txtAddressLine1");
							setTimeout(function()
							{
								_scWidgetUtils.setFocusOnWidgetUsingUid(that, "txtAddressLine1");
								//that.getWidgetByUId("txtAddressLine1").focus();
							}, 1);
							that = this;
							_scWidgetUtils.setValue(this,"txtAddressLine1","");
							_scWidgetUtils.setValue(this,"txtAddressLine2","");
						}
						_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
						_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
						
						_scWidgetUtils.setValue(this,"txtCity",_scModelUtils.getStringValueFromPath("City", addressList[0]));
						_scWidgetUtils.setValue(this,"txtState",_scModelUtils.getStringValueFromPath("State", addressList[0]));
						_scWidgetUtils.setValue(this,"txtZipCode",_scModelUtils.getStringValueFromPath("ZipCode", addressList[0]));
					}
					 
				}				
			} else if(_scBaseUtils.equals("Refine", action)){				
				if(!(_scBaseUtils.isVoid(errorMessage))){
					this.isError="Y";
					if(_scBaseUtils.equals("2", this.isRefineCalledFromAddLine)){
						//_scWidgetUtils.markFieldinError(this,"txtAddressLine2", errorMessage, true);
						 _isccsBaseTemplateUtils.showMessage(
                		this, errorMessage, "error", null);
						var dropDownExtnAddressLine1 = this.getWidgetByUId("txtAddressLine2");
						var parentScreen = this.getOwnerScreen();
						parentScreen.errormsgfromChild = errorMessage;
						//console.log('ddd',dropDownExtnAddressLine1);
						this.isRefineCalledFromAddLine = "";
					}if(_scBaseUtils.equals("1", this.isRefineCalledFromAddLine)){
						_scWidgetUtils.setValue(this,"txtAddressLine2","");
						//_scWidgetUtils.markFieldinError(this,"txtAddressLine1", errorMessage, true);
						//_scWidgetUtils.markFieldinError(this,"txtAddressLine1", errorMessage, true);
						 _isccsBaseTemplateUtils.showMessage(
                		this, errorMessage, "error", null);
						 var parentScreen = this.getOwnerScreen();
							parentScreen.errormsgfromChild = errorMessage;
						this.isRefineCalledFromAddLine = "";
					}
				} else{
					this.isError="";
					_isccsBaseTemplateUtils.hideMessage(
	                		this);
					_scWidgetUtils.clearFieldinError(this,'txtAddressLine2');
					_scWidgetUtils.clearFieldinError(this,'txtAddressLine1');
					_scWidgetUtils.hideWidget(this, "txtAddressLine1", true);
					_scWidgetUtils.showWidget(this, "extn_addressLine1", true);
					_scWidgetUtils.setValue(this,"extn_addressLine1", "");
					this.isAddLine1ComboLoadedFirstTime = "Y";
					_scScreenUtils.setModel(this, "extn_QASRefineResponse", modelOutput, null);
					var dropDownExtnAddressLine1 = this.getWidgetByUId("extn_addressLine1");
					dropDownExtnAddressLine1.loadDropDown();
					dropDownExtnAddressLine1.openDropDown();
					
					setTimeout(function()
					{
						_scWidgetUtils.setFocusOnWidgetUsingUid(that, "extn_addressLine1");
						//that.getWidgetByUId("txtAddressLine1").focus();
					}, 1);
					that = this;
					
				}			
			}else if(_scBaseUtils.equals("GetAddress", action)){
				console.log("errorMessage", errorMessage);
				
				if(!(_scBaseUtils.isVoid(errorMessage))){
					this.isError="Y";
					if(_scBaseUtils.equals("2", this.isRefineCalledFromAddLine)){
						//_scWidgetUtils.markFieldinError(this,"txtAddressLine2", errorMessage, true);
						 _isccsBaseTemplateUtils.showMessage(
                		this, errorMessage, "error", null);
						var parentScreen = this.getOwnerScreen();
							parentScreen.errormsgfromChild = errorMessage;
						this.isRefineCalledFromAddLine = "";
					}
					else if(_scBaseUtils.equals("1", this.isRefineCalledFromAddLine)){
						_scWidgetUtils.setValue(this,"txtAddressLine2","");
						
						//_scWidgetUtils.markFieldinError(this,"txtAddressLine1", errorMessage, true);
						 _isccsBaseTemplateUtils.showMessage(
                		this, errorMessage, "error", null);
						var parentScreen = this.getOwnerScreen();
						parentScreen.errormsgfromChild = errorMessage;
						//var dropDownExtnAddressLine1 = this.getWidgetByUId("txtAddressLine2");
						//console.log('ddd',dropDownExtnAddressLine1);
						//return;
						this.isRefineCalledFromAddLine = "";
					}
				} else{
					var addressListGetAddress = _scModelUtils.getModelListFromPath("AddressList.Address", getAddressModel);
					console.log("addressListGetAddress",addressListGetAddress);
					//_scWidgetUtils.clearFieldinError(this,'txtAddressLine2');
					//_scWidgetUtils.clearFieldinError(this,'txtAddressLine1');
					//var parentScreen = this.getOwnerScreen();
					//parentScreen.isCalledFirstTime = "Y";
					if(_scBaseUtils.equals("1", this.isRefineCalledFromAddLine))
					{
						if(!_scBaseUtils.isVoid(this.currentAddressLine1InfoMessage))
						{
							_isccsBaseTemplateUtils.showMessage(
			                		this, this.currentAddressLine1InfoMessage, "information", null);
						}
						else
						{
							_isccsBaseTemplateUtils.showMessage(
                				this, "Address verified successfully.Proceed to verify AddressLine2 if applicable", "success", null);	
						}
						
					}
					else
					{
						_isccsBaseTemplateUtils.showMessage(
                		this, "Address verified successfully", "success", null);	
					}					
					this.isError = "";
					this.addressVerified = "Y";
					_scWidgetUtils.setFocusOnWidgetUsingUid(that, "txtAddressLine2");
					_scWidgetUtils.setValue(this,"txtAddressLine1",_scModelUtils.getStringValueFromPath("AddressLine1", addressListGetAddress[0]));
					_scWidgetUtils.setValue(this,"txtAddressLine2",_scModelUtils.getStringValueFromPath("AddressLine2", addressListGetAddress[0]));
					_scWidgetUtils.setValue(this,"txtCity",_scModelUtils.getStringValueFromPath("City", addressListGetAddress[0]));
					_scWidgetUtils.setValue(this,"txtState",_scModelUtils.getStringValueFromPath("State", addressListGetAddress[0]));
					_scWidgetUtils.setValue(this,"txtZipCode",_scModelUtils.getStringValueFromPath("ZipCode", addressListGetAddress[0]));
				}
			}
			//alert(this.addressVerified);
			var parentScreen = this.getOwnerScreen();
			if(_scBaseUtils.equals("Y", this.isError)){
				//alert('oooo');
				parentScreen.isChildScreenHasError = "Y";
			}
			else if(_scBaseUtils.isVoid(this.addressVerified))
			{
				//alert('hiii')
				parentScreen.isChildScreenAddNotValidated = "Y";
			}
			else
			{
				parentScreen.isChildScreenAddNotValidated = "";
				parentScreen.isChildScreenHasError = "";
			}
		}
	},
	extnOnFocusZipCode : function(){
		//alert("Focus on Child");
		dfocus = require("scbase/loader!dijit/_base/focus");
		dfocus.focus(this.focusNode || this.domNode);
		that = this;
		setTimeout(function()
				{
					if(_scBaseUtils.equals(isChangeAddress,"N")){
						that.getWidgetByUId("txtZipCode").focus();
					}
				}, 1);
		
		//_scWidgetUtils.setFocusOnWidgetUsingUid(this, "txtZipCode");
	},
	handleScanFocus: function(scanData)
	{
		_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
		_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
		_scWidgetUtils.setValue(this,"txtAddressLine1", "");
	},
	onBlur: function(event, bEvent, ctrl, args) {
		
		console.log("this.isAddLine1ComboLoadedFirstTime >> ", this.isAddLine1ComboLoadedFirstTime);
		if(!_scBaseUtils.isVoid(args)){
			console.log("InsideIf","Inside");			
			var selectedItemModel = _scModelUtils.getModelObjectFromPath("originatingControlInstance.item", args);
			console.log("selectedItemModel",selectedItemModel);
			if(_scBaseUtils.isVoid(selectedItemModel)){
				var dropDownExtnAddressLine1 = this.getWidgetByUId("extn_addressLine1");
				if(!dropDownExtnAddressLine1.isHidden){
					_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
					_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
					_scWidgetUtils.setValue(this,"txtAddressLine1", "");
				}
			} else {
				var selectedAddressLine1ModelObject = _scModelUtils.getModelObjectFromPath("AddressLine1", selectedItemModel);
				if(_scBaseUtils.isVoid(selectedAddressLine1ModelObject[0])){
					_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
					_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
					_scWidgetUtils.setValue(this,"txtAddressLine1", "");
				}
			}
		} else if (_scBaseUtils.isVoid(this.isAddLine1ComboLoadedFirstTime)){
			console.log("InsideELSE","Else");			
			var dropDownExtnAddressLine1 = this.getWidgetByUId("extn_addressLine1");
			/*
			if(event.key=="Tab" || event.key=="Escape" || (!_scBaseUtils.isVoid(document.getElementById('extn_addressLine1')) && document.getElementById('extn_addressLine1').value == 0))
			{
				alert("HI - This is else part of on Blur");
				this.getWidgetByUId("extn_addressLine1").closeDropDown();
				_scWidgetUtils.hideWidget(this, "extn_addressLine1", false);
				_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
				_scWidgetUtils.setValue(this,"txtAddressLine1", "");
				_scWidgetUtils.setFocusOnWidgetUsingUid(this, "txtAddressLine1");
			}
			*/
			console.log("isHidden",dropDownExtnAddressLine1.isHidden);
			if(!dropDownExtnAddressLine1.isHidden){
				_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
				_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
				_scWidgetUtils.setValue(this,"txtAddressLine1", "");
				_scWidgetUtils.setFocusOnWidgetUsingUid(this, "txtAddressLine1");
			}	
		} else {
			var dropDownExtnAddressLine1 = this.getWidgetByUId("extn_addressLine1");
			console.log("focus", dropDownExtnAddressLine1.isFocussableField());
			dropDownExtnAddressLine1.focus();
			_scWidgetUtils.setFocusOnWidgetUsingUid(this, "extn_addressLine1");
		}
		this.isAddLine1ComboLoadedFirstTime = "";
	},
	onKeyUp_extnAddressLine1: function(
	        event, bEvent, ctrl, args)
		{
			//console.log(event.key);
			//_scEventUtils.stopEvent(bEvent);
			
					
			if(event.key=="Tab" || event.key=="Escape")
			{
				_scWidgetUtils.hideWidget(
					this, "extn_addressLine1", false);
				_scWidgetUtils.showWidget(this, "txtAddressLine1", true, null);
				_scWidgetUtils.setValue(this, "txtAddressLine1", "", true );
				that = this;
				setTimeout(function()
				{
					that.getWidgetByUId("txtAddressLine1").focus();
				}, 1);
				
			}
				

		},
	extnOnChangeFS: function(event, bEvent, ctrl, args) {
		//console.log("event > ", event);
		//console.log("bEvent > ", bEvent);
		//console.log("ctrl > ", ctrl);
		//console.log("args > ", args);
		_scWidgetUtils.hideWidget(this, "extn_addressLine1", true);
		_scWidgetUtils.showWidget(this, "txtAddressLine1", true);
		var selectedItemModel = _scModelUtils.getModelObjectFromPath("originatingControlInstance.item", args);
		//var selectedAddressLine1MonikerModel = _scModelUtils.getStringValueFromPath("Moniker", selectedItemModel);
		var selectedAddressLine1ModelObject = _scModelUtils.getModelObjectFromPath("AddressLine1", selectedItemModel);
		var selectedMonikerModelObject = _scModelUtils.getModelObjectFromPath("Moniker", selectedItemModel);
		var infoObject = _scModelUtils.getModelObjectFromPath("InfoMessage", selectedItemModel);
		console.log('infoObject',infoObject);
		//return;
		if(!_scBaseUtils.isVoid(infoObject))
		{
			this.currentAddressLine1InfoMessage = infoObject[0];
		}
		else
		{
			this.currentAddressLine1InfoMessage = "";
		}
		this.currentAddressLine1Moniker = "";
		if(selectedMonikerModelObject){
			this.currentAddressLine1Moniker = selectedMonikerModelObject[0];
			console.log("selectedAddressLine1ModelObject[0]", selectedAddressLine1ModelObject[0]);			
			_scWidgetUtils.setValue(this,"txtAddressLine1", selectedAddressLine1ModelObject[0]);
			if(!_scBaseUtils.isVoid(selectedAddressLine1ModelObject[0])){
				var widget = _scEventUtils.getOriginatingControlUId(bEvent);
				this.createInputForQASCall(widget);
			}
		}		
	},
		
		initializeScreen: function(event, bEvent, ctrl, args) {
			
			var screenInitialInput = _scScreenUtils.getModel(this.getOwnerScreen(true), "addressCapturePageInitData");
			if(_scBaseUtils.equals("Y", screenInitialInput.FromCustomerCreate)){
				_scWidgetUtils.hideWidget(this, "pnlContactInformation", false);
			}
			
	        if (
	        _scBaseUtils.isVoid(
	        this.getZipCodeSchema())) {
	            _scWidgetUtils.showWidget(
	            this, "txtCity", true, null);
	            _scWidgetUtils.showWidget(
	            this, "pnlStateHolder", true, null);
	        }
	        if (
	        _scBaseUtils.isVoid(
	        _scScreenUtils.getModel(
	        this, "getTitleList_output"))) {
	            _scWidgetUtils.showWidget(
	            this, "txtTitle", true, null);
	            _scWidgetUtils.hideWidget(
	            this, "cmbTitle", true);
	            _scWidgetUtils.enableWidget(
	            this, "txtTitle");
	            _scWidgetUtils.disableWidget(
	            this, "cmbTitle", false);
	        } else {
	            _scWidgetUtils.disableWidget(
	            this, "txtTitle", false);
	            _scWidgetUtils.enableWidget(
	            this, "cmbTitle");
	        }
        },
		changeAddressOnLoad:function(widget){
		var zipCode1 = this.getWidgetByUId("txtZipCode").displayedValue;
		if(!_scBaseUtils.isVoid(zipCode1)){
			var comingFromLoad = 'Y'
			this.createInputForQASCall(comingFromLoad);
			//this.createInputForQASCall(widget);
			isChangeAddress="Y";
		}
		setTimeout(function()
				{
					if(_scBaseUtils.equals(isChangeAddress,"N")){
						that.getWidgetByUId("txtZipCode").focus();
					}
				}, 1);
		that = this;
		
		},
		addLineBlur:function(event, bEvent, ctrl, args){
			
			var widget = _scEventUtils.getOriginatingControlUId(bEvent);
			this.createInputForQASCall(widget);
		}
	});
});

