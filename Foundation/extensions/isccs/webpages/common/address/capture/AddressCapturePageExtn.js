
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/common/address/capture/AddressCapturePageExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!isccs/utils/ModelUtils"
		  ]
,
function(			 
			    _dojodeclare,
			    _extnAddressCapturePageExtnUI,
			    _isccsBaseTemplateUtils,
				_isccsUIUtils,
				_scBaseUtils,
				_scModelUtils,
				_scScreenUtils,
				_scWidgetUtils,
				_scModelUtils,
				_isccsModelUtils
){ 
 
	var strBillToId = "";
	return _dojodeclare("extn.common.address.capture.AddressCapturePageExtn", [_extnAddressCapturePageExtnUI],{
	// custom code here
	isChildScreenHasError:"",
//	isChildScreenAddNotValidated:"Y",
	errormsgfromChild:"",
	enterpressedinaddline1:"",
	initializeScreen: function(event, bEvent, ctrl, args) {
            var screenInitialInput = null;
            screenInitialInput = _scScreenUtils.getModel(
            this, "addressCapturePageInitData");
            console.log("screenInitialInput",screenInitialInput);
            if(!_scBaseUtils.isVoid(screenInitialInput) && _scBaseUtils.equals(
            		_scModelUtils.getStringValueFromPath("CreateNewAddress",screenInitialInput), "Y")){
            	console.log("INTHIS");
            	_scWidgetUtils.showWidget(this, "extn_checkbox_defaultShipTo", false,false);
            }
            if (!(
            _isccsModelUtils.isEmptyModel(
            screenInitialInput))) {
                _scScreenUtils.setModel(
                this, "screenInitialInput", screenInitialInput, null);
                var orderHeaderKey = null;
			var getPersonInfoListInput = null;
            getPersonInfoListInput = {};
			if(!_scBaseUtils.isVoid(screenInitialInput.Order) && !_scBaseUtils.isVoid(screenInitialInput.Order.Customer)){
            getPersonInfoListInput["Customer"] = screenInitialInput.Order.Customer;
			_isccsModelUtils.removeAttributeFromModel("Customer.CustomerContactID", getPersonInfoListInput);
			}else if(!_scBaseUtils.isVoid(screenInitialInput.Order)){
				if(!_scBaseUtils.isVoid(screenInitialInput.Order.BillToID)){
					strBillToId = screenInitialInput.Order.BillToID;	
				}
				console.log("strBillToId",strBillToId);
				getPersonInfoListInput.Customer = {};
				getPersonInfoListInput.Customer.CustomerID = strBillToId;
				getPersonInfoListInput.Customer.OrganizationCode = "VSI.com";
				getPersonInfoListInput.Customer.DocumentType = screenInitialInput.Order.DocumentType;
				orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", screenInitialInput);
				getPersonInfoListInput.Customer.OrderHeaderKey = orderHeaderKey;
			}
                orderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", screenInitialInput);
                if (!(
                _scBaseUtils.isVoid(
                orderHeaderKey))&&_scBaseUtils.equals(
            		screenInitialInput.Order.Edit, 'Y') ) {
                    _isccsUIUtils.callApi(
                    this, getPersonInfoListInput, "extn_getShipTo", null);
                } else if (!(
                _scBaseUtils.isVoid(
                orderHeaderKey)) ) {
                    _isccsUIUtils.callApi(
                    this, screenInitialInput, "getPersonInfoList", null);
                } else {
                    this.hideExistingAddresses();
                    this.showEditAddressPanel();
                }
            } else {
                this.hideExistingAddresses();
                this.showEditAddressPanel();
            }
            var screenInput = null;
            screenInput = _scScreenUtils.getModel(
            this, "PersonInfo");
            if (
            _isccsModelUtils.isEmptyModel(
            screenInput)) {
                screenInput = screenInitialInput;
            }
            var options = null;
            options = {};
            _scBaseUtils.setAttributeValue("clearOldVals", false, options);
            console.log(_scScreenUtils.getModel(
                    this, "PersonInfo"));
            _scScreenUtils.setModel(
            this, "screenInput", screenInput, options);
        },
        onPopupConfirm: function(
        event, bEvent, ctrl, args) {
           /* var editAddressModel = null;
            editAddressModel = _scScreenUtils.getTargetModel(
            this, "NewAddress", null);*/
            var isValid = true;
            /*if (
            _scModelUtils.getBooleanValueFromPath("Edit", editAddressModel, true)) {
                isValid = _scScreenUtils.validate(
                this);
            }*/
            if (
            _scBaseUtils.equals(
            false, isValid)) {
                var msg = null;
                msg = _scScreenUtils.getString(
                this, "ErrorsInScreenMessage");
                _isccsBaseTemplateUtils.showMessage(
                this, msg, "error", null);
            } else {
                this.onApply(
                event, bEvent, ctrl, args);
            }
        },
        onApply: function(
        event, bEvent, ctrl, args) {
            var popupOut = null;
            if (!(
            _scWidgetUtils.isWidgetVisible(
            this, "selectAddressContainer"))) {
                this.checkAndVerifyAddress();
            } else {
                popupOut = _scScreenUtils.getModel(
                this, "selectedAddress_output");
                var targetModel = null;
                targetModel = _scBaseUtils.getTargetModel(
                this, "NewAddress", null);
                this.copyAttributesFromModel(
                popupOut, targetModel);
                if(!_scBaseUtils.isVoid(popupOut.PersonInfo.EmailID))
                {
                     
                    delete popupOut.PersonInfo["EmailID"];   
                }
                if(!_scBaseUtils.isVoid(popupOut.PersonInfo.ID))
                {
                       
                    delete popupOut.PersonInfo["ID"];   
                }
                
                _scScreenUtils.setPopupOutput(
                this, popupOut);
                _scWidgetUtils.closePopup(
                this, "APPLY", false);
            }
        },
        handleVerifyAddress: function(
        verifyAddress) {
        	//alert(this.isChildScreenHasError);
			//seting the model to new name space extn_verifyAddress_Output
			 _scScreenUtils.setModel(this, "extn_verifyAddress_Output",verifyAddress, null);

            var sState = null;
            var ePersonInfo = null;
			var lAddressVerificationResponseMessages = null;
			var eMessageList = null;
            sState = _scModelUtils.getStringValueFromPath("PersonInfoList.Status", verifyAddress);
			console.log("sState:", sState);
            var closePopup = "false";
            var isMultipleRequired = false;
			
			
			
			var personInfo = _scModelUtils.getStringValueFromPath("PersonInfoList.PersonInfo",verifyAddress);
			for(var i=0;i<personInfo.length;i++){
				
				var addLine1 = _scModelUtils.getStringValueFromPath("AddressLine1",personInfo[i]);
				var city = _scModelUtils.getStringValueFromPath("City",personInfo[i]);				
				var state = _scModelUtils.getStringValueFromPath("State",personInfo[i]);				
				var zipCode = _scModelUtils.getStringValueFromPath("ZipCode",personInfo[i]);
				//OMS-1093 : Start
				if(_scBaseUtils.isVoid(addLine1) || _scBaseUtils.isVoid(city)|| _scBaseUtils.isVoid(zipCode) ) {
							
							_isccsBaseTemplateUtils.showMessage(this, "Incomplete Address fields:AddressLine1, City, State, Country and ZipCode are mandatory fields", "error", null);
							return;
							
						}
			}
			
			//if(_scBaseUtils.equals(this.enterpressedinaddline1,""))
			//{
			//	
			//}
			
			if(!_scBaseUtils.isVoid(this.isChildScreenHasError) && _scBaseUtils.equals(this.isChildScreenHasError,"Y")){
				//show popup
				//this.isChildScreenHasError="";
				//alert(errormsgfromChild);
				if(!_scBaseUtils.isVoid(this.errormsgfromChild))
				{
					 _isccsBaseTemplateUtils.showMessage(
		                		this, this.errormsgfromChild, "error", null); 
				}
				
				_scScreenUtils.showConfirmMessageBox(
									this, "This address has some errors. Do you wish to override and proceed?", "handleConfirmAddressPopUpResponse", isMultipleRequired);
									
				
			}
			else if(!_scBaseUtils.isVoid(this.isChildScreenAddNotValidated) && _scBaseUtils.equals(this.isChildScreenAddNotValidated,"Y")){
				//show popup
				//this.isChildScreenHasError="";
				
				 _isccsBaseTemplateUtils.showMessage(
	                		this, "Address needs to be validated with QAS to proceed further.", "error", null); 
				
				 return;			
				
			}
			
			if(_scBaseUtils.isVoid(this.isChildScreenHasError)){
					
			  if (!(
				_scModelUtils.getBooleanValueFromPath("PersonInfoList.ProceedWithSingleAVSResult", verifyAddress, true))) {
					isMultipleRequired = true;
				}
				if (
				_scBaseUtils.equals(
				sState, "FAILED")) {
					console.log("FAILED");
					lAddressVerificationResponseMessages = _scModelUtils.getModelListFromPath("PersonInfoList.AddressVerificationResponseMessages.AddressVerificationResponseMessage", verifyAddress);
					if (!(
					_scBaseUtils.isVoid(
					lAddressVerificationResponseMessages))) {
						eMessageList = {};
						eMessageList["messageList"] = lAddressVerificationResponseMessages;
					}
						_isccsBaseTemplateUtils.showMessage(
						this, "InvalidAddressProvided", "error", eMessageList);
						if (
						_scResourcePermissionUtils.hasPermission("ISCADDOVR001")) {
							_scWidgetUtils.showWidget(
							this, "pnlAddressOverride", true, null);
							_scScreenUtils.clearScreen(
							this, null);
						}
				} else if (
				_scBaseUtils.greaterThan(
				_scModelUtils.getNumberValueFromPath("PersonInfoList.TotalNumberOfRecords", verifyAddress), 1) || isMultipleRequired) {
					console.log("Passed_Multi");
					lAddressVerificationResponseMessages = _scModelUtils.getModelListFromPath("PersonInfoList.AddressVerificationResponseMessages.AddressVerificationResponseMessage", verifyAddress);
					if (!(
					_scBaseUtils.isVoid(
					lAddressVerificationResponseMessages))) {
						eMessageList = {};
						eMessageList["messageList"] = lAddressVerificationResponseMessages;
					}
					_scWidgetUtils.hideWidget(
					this, "pnlAddressCapture", true);
					_scWidgetUtils.showWidget(
					this, "pnlAddressVerification", true, null);
					_scWidgetUtils.showWidget(
					this, "lnkEnterAddress", true, null);
					_isccsBaseTemplateUtils.showMessage(
					this, "ExactAddressNotFound", "error", eMessageList);
				} else if (
				_scBaseUtils.equals(
				sState, "AVS_DOWN")) {
					console.log("AVS_DOWN");
					ePersonInfo = _scBaseUtils.getTargetModel(
					this, "verifyAddress_input", null);
					_scModelUtils.setBooleanValueAtModelPath("PersonInfo.IsAddressVerified", false, ePersonInfo);
					_isccsBaseTemplateUtils.hideMessage(
					this);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.PersonInfoKey", " ", ePersonInfo);
					_scScreenUtils.setPopupOutput(
					this, ePersonInfo);
					_scWidgetUtils.closePopup(
					this, "APPLY", false);
					closePopup = "true";
				} else {
					console.log("ELSE");
					ePersonInfo = this.getFirstPersonInfo(
					verifyAddress);
					_isccsBaseTemplateUtils.hideMessage(
					this);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.PersonInfoKey", " ", ePersonInfo);
					var isDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",_scScreenUtils.getTargetModel(
							this, "extn_DefaultShipTo",null));
					console.log("IsDefaultShipTo",isDefaultShipTo);
					if(_scBaseUtils.equals(isDefaultShipTo,"Y")){
						_scModelUtils.setStringValueAtModelPath("PersonInfo.IsDefaultShipTo", isDefaultShipTo, ePersonInfo);
					}
					console.log("PersonInfo",_scScreenUtils.getModel(this, "PersonInfo"));
					//return;
					var id = _scModelUtils.getStringValueFromPath("PersonInfo.ID",_scScreenUtils.getModel(this, "PersonInfo"));
					console.log("id",id);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.ID",id,ePersonInfo);
					
				  
					 if(!_scWidgetUtils.isWidgetVisible(
								this, "extn_checkbox_defaultShipTo")) 
					{

					delete ePersonInfo.PersonInfo["IsShipTo"];  

					delete ePersonInfo.PersonInfo["IsDefaultShipTo"]; 

					delete ePersonInfo.PersonInfo["EmailID"];    

					} 
					
					 // console.log("ffff",ePersonInfo);        	
					_scScreenUtils.setPopupOutput(
					this, ePersonInfo);
					_scWidgetUtils.closePopup(
					this, "APPLY", false);
					closePopup = "true";
				}
				this.fireAddressVerified(
				ePersonInfo, closePopup);
			}
			
        },
		        handleCountrySelected: function(
        event, bEvent, ctrl, args) {
			
            var sCountry = null;
            var countryInput = null;
            countryInput = _scBaseUtils.getTargetModel(
            this, "getCountry_input", null);
            if (
            _scBaseUtils.isVoid(
            countryInput)) {
                return;
            }
            sCountry = _scModelUtils.getStringValueFromPath("PersonInfo.Country", countryInput);
            var screenInput = null;
            screenInput = _scScreenUtils.getTargetModel(
            this, "verifyAddress_input", null);
            var sOldCountry = null;
            sOldCountry = _scModelUtils.getStringValueFromPath("PersonInfo.Country", screenInput);
            if (!(
            _scBaseUtils.equals(
            sCountry, sOldCountry))) {
                _scModelUtils.setStringValueAtModelPath("PersonInfo.Country", sCountry, screenInput);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.State", "", screenInput);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.ZipCode", "", screenInput);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.City", "", screenInput);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.AddressLine1", "", screenInput);
				_scModelUtils.setStringValueAtModelPath("PersonInfo.AddressLine2", "", screenInput);
				_isccsBaseTemplateUtils.hideMessage(
                		this);
				this.isChildScreenHasError="Y";
                _scModelUtils.setBooleanValueAtModelPath("PersonInfo.IsAddressVerified", false, screenInput);
                var rerender = null;
                rerender = {};
                rerender["isRerender"] = "true";
                _scScreenUtils.setModel(
                this, "screenInput", screenInput, rerender);
                var constructorData = null;
                constructorData = {};
                _scScreenUtils.replaceIdentifierScreenWithinScreen(
                this, "isccs.common.address.capture.AddressCapture", sCountry, "pnlAddressCaptureHolder", constructorData, "callBack", null);
				
            }
		},   
        //Custom giftCardPopUphandler method trigger once Gift Card Pop Up is displayed on screen. If Ok is Selected on Pop Up,
		// It triggers the OOB functionality(override from save method).		
		handleConfirmAddressPopUpResponse: function(res,isMultipleRequired){
			if (_scBaseUtils.equals(res, "Ok")) 
			{
			var verifyAddress = null;
            verifyAddress = _scScreenUtils.getModel(this, "extn_verifyAddress_Output");
			 _scModelUtils.setBooleanValueAtModelPath("PersonInfo.IsAddressVerified", false, verifyAddress);
			var ePersonInfo = null;
			var sState = null;
            sState = _scModelUtils.getStringValueFromPath("PersonInfoList.Status", verifyAddress);
			
			  if (!(
				_scModelUtils.getBooleanValueFromPath("PersonInfoList.ProceedWithSingleAVSResult", verifyAddress, true))) {
					isMultipleRequired = true;
				}
				if (
				_scBaseUtils.equals(
				sState, "FAILED")) {
					console.log("FAILED");
					

					lAddressVerificationResponseMessages = _scModelUtils.getModelListFromPath("PersonInfoList.AddressVerificationResponseMessages.AddressVerificationResponseMessage", verifyAddress);
					if (!(
					_scBaseUtils.isVoid(
					lAddressVerificationResponseMessages))) {
						
						eMessageList = {};
						eMessageList["messageList"] = lAddressVerificationResponseMessages;
					}
						_isccsBaseTemplateUtils.showMessage(
						this, "InvalidAddressProvided", "error", eMessageList);
						if (
						_scResourcePermissionUtils.hasPermission("ISCADDOVR001")) {
							console.log("6666");
							_scWidgetUtils.showWidget(
							this, "pnlAddressOverride", true, null);
							_scScreenUtils.clearScreen(
							this, null);
						}
				} else if (
				_scBaseUtils.greaterThan(
				_scModelUtils.getNumberValueFromPath("PersonInfoList.TotalNumberOfRecords", verifyAddress), 1) || isMultipleRequired) {
					console.log("Passed_Multi");
					lAddressVerificationResponseMessages = _scModelUtils.getModelListFromPath("PersonInfoList.AddressVerificationResponseMessages.AddressVerificationResponseMessage", verifyAddress);
					if (!(
					_scBaseUtils.isVoid(
					lAddressVerificationResponseMessages))) {
						eMessageList = {};
						eMessageList["messageList"] = lAddressVerificationResponseMessages;
					}
					_scWidgetUtils.hideWidget(
					this, "pnlAddressCapture", true);
					_scWidgetUtils.showWidget(
					this, "pnlAddressVerification", true, null);
					_scWidgetUtils.showWidget(
					this, "lnkEnterAddress", true, null);
					_isccsBaseTemplateUtils.showMessage(
					this, "ExactAddressNotFound", "error", eMessageList);
				} else if (
				_scBaseUtils.equals(
				sState, "AVS_DOWN")) {
					console.log("AVS_DOWN");
					ePersonInfo = _scBaseUtils.getTargetModel(
					this, "verifyAddress_input", null);
					_scModelUtils.setBooleanValueAtModelPath("PersonInfo.IsAddressVerified", false, ePersonInfo);
					_isccsBaseTemplateUtils.hideMessage(
					this);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.PersonInfoKey", " ", ePersonInfo);
					_scScreenUtils.setPopupOutput(
					this, ePersonInfo);
					_scWidgetUtils.closePopup(
					this, "APPLY", false);
					closePopup = "true";
				} else {
					console.log("ELSE");
					ePersonInfo = this.getFirstPersonInfo(
					verifyAddress);
					_isccsBaseTemplateUtils.hideMessage(
					this);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.PersonInfoKey", " ", ePersonInfo);
					var isDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",_scScreenUtils.getTargetModel(
							this, "extn_DefaultShipTo",null));
					console.log("IsDefaultShipTo",isDefaultShipTo);
					if(_scBaseUtils.equals(isDefaultShipTo,"Y")){
						ePersonInfo.PersonInfo.IsDefaultShipTo = isDefaultShipTo;
					}
					console.log("PersonInfo",_scScreenUtils.getModel(this, "PersonInfo"));
					//return;
					var id = _scModelUtils.getStringValueFromPath("PersonInfo.ID",_scScreenUtils.getModel(this, "PersonInfo"));
					console.log("id",id);
					_scModelUtils.setStringValueAtModelPath("PersonInfo.ID",id,ePersonInfo);
					
				  
					 if(!_scWidgetUtils.isWidgetVisible(
								this, "extn_checkbox_defaultShipTo")) 
					{
					
					delete ePersonInfo.PersonInfo["IsShipTo"];  

					delete ePersonInfo.PersonInfo["IsDefaultShipTo"]; 

					delete ePersonInfo.PersonInfo["EmailID"];    

					} 
					
					 // console.log("ffff",ePersonInfo);        	
					_scScreenUtils.setPopupOutput(
					this, ePersonInfo);
					//console.log("8888");
					_scWidgetUtils.closePopup(
					this, "APPLY", false);
					closePopup = "true";
				}
				//console.log("5555");
				this.fireAddressVerified(
				ePersonInfo, closePopup);
			
            }//end of if ok selected
			
		},
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "verifyAddress")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "verifyAddress_output", modelOutput, null);
                }
                var personInfoList = null;
                personInfoList = _scModelUtils.getModelListFromPath("PersonInfoList.PersonInfo", modelOutput);
                if (
                _scBaseUtils.equals(
                _scBaseUtils.getAttributeCount(
                personInfoList), 1)) {
                    _scScreenUtils.setModel(
                    this, "verifyAddress_output", modelOutput, null);
                }
                this.handleVerifyAddress(
                modelOutput);
            }
			if (
            _scBaseUtils.equals(
            mashupRefId, "getPersonInfoList")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "getPersonInfoList_output", modelOutput, null);
                }
                this.handleExistingAddresses(
                modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getShipTo")) {
				var personInfoList = _scModelUtils.createNewModelObjectWithRootKey("PersonInfoList");
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
					
					var personInfoArr = _scBaseUtils.getNewArrayInstance();
					var CustomerAdditionalAddresses = modelOutput.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress;
						CustomerAdditionalAddresses.forEach (function(CustomerAdditionalAddress, i) { 
					if(CustomerAdditionalAddress.IsShipTo==='Y' && CustomerAdditionalAddress.IsDefaultShipTo==='N'){
					_scBaseUtils.appendToArray(personInfoArr, CustomerAdditionalAddress.PersonInfo);
					CustomerAdditionalAddress.PersonInfo.Selected= "N";
					}
						});
					_scModelUtils.addListToModelPath("PersonInfoList.PersonInfo", personInfoArr, personInfoList); 
                    _scScreenUtils.setModel(this, "getPersonInfoList_output", personInfoList, null);
					
                }
				modelOutput=personInfoList;
                this.handleExistingAddresses(
                modelOutput);
            }
        }
	});
});
	