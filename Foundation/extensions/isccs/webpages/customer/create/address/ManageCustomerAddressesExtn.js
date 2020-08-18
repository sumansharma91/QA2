
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/customer/create/address/ManageCustomerAddressesExtnUI",
          "scbase/loader!isccs/utils/CustomerUtils",
          "scbase/loader!isccs/utils/SharedComponentUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/ModelUtils"
          ]
,
function(			 
			    _dojodeclare,
			    _extnManageCustomerAddressesExtnUI,
			    _isccsCustomerUtils,
			    _isccsSharedComponentUtils,
			    _isccsUIUtils,
				_scBaseUtils,
				_scModelUtils,
				_scResourcePermissionUtils,
				_scScreenUtils,
				_scWidgetUtils,
				_isccsModelUtils
){ 
	var reloadScreen = false;
	return _dojodeclare("extn.customer.create.address.ManageCustomerAddressesExtn", [_extnManageCustomerAddressesExtnUI],{
		
		createNewAddress: function(
        event, bEvent, controlName, args) {
			console.log("HERE");
			var inputModel={};
			inputModel.CreateNewAddress = "Y";
			inputModel.FromCustomerCreate = "Y";
            _isccsSharedComponentUtils.openAddressCapture(
            this, inputModel, null, "VSI.com", "CreateCustomerAddress", "saveNewAddress");
        },
		saveNewAddress: function(actionPerformed, model, popupParams) {
        	console.log("actionPerformed",actionPerformed);
    		console.log("model",model);
    		console.log("popupParams",popupParams);
            if (
            _scBaseUtils.equals(
            actionPerformed, "APPLY")) {
                if (!(
                _scBaseUtils.isVoid(
                model))) {
                    var sContactID = "";
                    sContactID = _scModelUtils.getStringValueFromPath("Customer.CustomerContactID", _scScreenUtils.getModel(
                    this, "screenInput"));
                    var mashupIDList = null;
                    mashupIDList = [];
                    mashupIDList.push("manageCustomer");
                    mashupIDList.push("getCustomerDetails");
                    var mashupList = null;
                    mashupList = [];
                    var manageCustomerInput = null;
                    manageCustomerInput = _isccsCustomerUtils.getManageCustomerAddressInput(
                    _scScreenUtils.getModel(
                    this, "getCustomerDetails_output"), model, null, sContactID);
                    console.log("getCustomerDetailsOutput",_scScreenUtils.getModel(this, "getCustomerDetails_output"));
                    
                    var getCustomerDetailsModel=_scScreenUtils.getModel(this, "getCustomerDetails_output");
                    console.log("manageCustomerInput",manageCustomerInput);
                    
                    var iD = _scModelUtils.getNumberValueFromPath("Customer.MaxID",getCustomerDetailsModel);
                    var dayPhone = _scModelUtils.getStringValueFromPath("DayPhone",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultBillTo.PersonInfo);
                    var eMailID = _scModelUtils.getStringValueFromPath("EMailID",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                    var emailID = _scModelUtils.getStringValueFromPath("EmailID",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                    var extnGender = _scModelUtils.getStringValueFromPath("ExtnGender",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                    var firstName = _scModelUtils.getStringValueFromPath("FirstName",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                    var lastName = _scModelUtils.getStringValueFromPath("LastName",
                    		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                    console.log("DayPhone:"+dayPhone+" eMailID"+eMailID+" emailID:"+emailID+" firstName:"+firstName+" lastName:"+lastName);
                    console.log("ID",iD);
                    
                    _scModelUtils.setStringValueAtModelPath("DayPhone", dayPhone, 
                    		manageCustomerInput.Customer.CustomerContactList.CustomerContact);
                    _scModelUtils.setStringValueAtModelPath("DayPhone", dayPhone, 
                    		manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo);
    				_scModelUtils.setStringValueAtModelPath("EMailID", eMailID, 
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
    				_scModelUtils.setStringValueAtModelPath("EmailID", emailID,  
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
    				_scModelUtils.setStringValueAtModelPath("ExtnGender", extnGender,  
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
    				_scModelUtils.setStringValueAtModelPath("FirstName", firstName,  
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
    				_scModelUtils.setStringValueAtModelPath("LastName", lastName,
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
    				_scModelUtils.setNumberValueAtModelPath("ID",iD+1,
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo);
    				_scBaseUtils.removeBlankAttributes(manageCustomerInput);
    				
    				var isDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo);
					manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo.IsShipTo = "Y";

    				var defaultBillToModel = null;
    				var defaultShipToModel = null;
    				
    				if(_scBaseUtils.equals(
    						isDefaultShipTo, "Y")){
    					defaultBillToModel = getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultBillTo;
    					defaultShipToModel = getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultShipTo;
    					var defaultBillToID = defaultBillToModel.ID;
    					var defaultShipToID = defaultShipToModel.ID;
    					
    					console.log("defaultBillToModel",defaultBillToModel);
    					
    					if(_scBaseUtils.equals(
    							defaultBillToID, defaultShipToID)){
    						defaultShipToModel = _scBaseUtils.cloneModel(defaultBillToModel);
    						defaultBillToModel.IsDefaultShipTo="N";
    						defaultBillToModel.IsShipTo="N";
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[1] = defaultBillToModel;
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2] = defaultShipToModel;
    						_scModelUtils.setStringValueAtModelPath("IsDefaultShipTo", "N", 
    								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
    						_scModelUtils.setStringValueAtModelPath("IsDefaultBillTo", "N", 
    								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
    						_scModelUtils.setStringValueAtModelPath("IsBillTo", "N", 
    								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
    						_scModelUtils.setStringValueAtModelPath("ID", iD+2, 
    								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
    						_scModelUtils.setStringValueAtModelPath("ID", iD+2, 
    								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2].PersonInfo);
    					}else{        					
        					defaultShipToModel.IsDefaultShipTo="N";
    						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[1] = defaultShipToModel;
    					}
    					
    					console.log("defaultShipToModel",defaultShipToModel);
    					reloadScreen = true;
    				}
    				
    				console.log("manageCustomerInput",manageCustomerInput);
                    
                    mashupList.push(
                    manageCustomerInput);
                    /*mashupList.push(
                    _scScreenUtils.getTargetModel(
                    this, "getCustomerDetails_input", null));*/
                    _isccsUIUtils.callApis(
                    this, mashupList, mashupIDList, null, null);
                }
            }
        },
        updateExistingAddress: function(
        event, bEvent, controlName, args) {
        	console.log("HERE");
            _scWidgetUtils.setValue(
            this, "hiddenField", "addressChanged", true);
            var sContactID = "";
            sContactID = _scModelUtils.getStringValueFromPath("Customer.CustomerContactID", _scScreenUtils.getModel(
            this, "screenInput"));
            var model = null;
            model = _scBaseUtils.getModelValueFromBean("model", args);
            var customerAddress = null;
            customerAddress = _scBaseUtils.getModelValueFromBean("customerAdditionalAddress", args);
            if (!(
            _scBaseUtils.isVoid(
            model))) {
                var manageCustomerInput = null;
                manageCustomerInput = _isccsCustomerUtils.getManageCustomerAddressInput(
                _scScreenUtils.getModel(
                this, "getCustomerDetails_output"), model, _scModelUtils.getStringValueFromPath("CustomerAdditionalAddress.CustomerAdditionalAddressID", customerAddress), sContactID);
                
                var getCustomerDetailsModel = _scScreenUtils.getModel(this, "getCustomerDetails_output");
                console.log("getCustomerDetailsOutput",getCustomerDetailsModel);
                
                var dayPhone = _scModelUtils.getStringValueFromPath("DayPhone",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultBillTo.PersonInfo);
                var eMailID = _scModelUtils.getStringValueFromPath("EMailID",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                var emailID = _scModelUtils.getStringValueFromPath("EmailID",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                var extnGender = _scModelUtils.getStringValueFromPath("ExtnGender",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                var firstName = _scModelUtils.getStringValueFromPath("FirstName",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                var lastName = _scModelUtils.getStringValueFromPath("LastName",
                		getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0]);
                console.log("DayPhone:"+dayPhone+" eMailID"+eMailID+" emailID:"+emailID+" firstName:"+firstName+" lastName:"+lastName);
                
                _scModelUtils.setStringValueAtModelPath("DayPhone", dayPhone, 
                		manageCustomerInput.Customer.CustomerContactList.CustomerContact);
                _scModelUtils.setStringValueAtModelPath("DayPhone", dayPhone, 
                		manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo);
				_scModelUtils.setStringValueAtModelPath("EMailID", eMailID, 
						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
				_scModelUtils.setStringValueAtModelPath("EmailID", emailID,  
						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
				_scModelUtils.setStringValueAtModelPath("ExtnGender", extnGender,  
						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
				_scModelUtils.setStringValueAtModelPath("FirstName", firstName,  
						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
				_scModelUtils.setStringValueAtModelPath("LastName", lastName,
						manageCustomerInput.Customer.CustomerContactList.CustomerContact);
                
				var isDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",
						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo);
				manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[0].PersonInfo.IsShipTo = "Y";
				
				if(_scBaseUtils.equals(
						isDefaultShipTo, "Y")){
					defaultBillToModel = getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultBillTo;
					defaultShipToModel = getCustomerDetailsModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.ExtnDefaultShipTo;
					var defaultBillToID = defaultBillToModel.ID;
					var defaultShipToID = defaultShipToModel.ID;
					
					console.log("defaultBillToModel",defaultBillToModel);
					
					if(_scBaseUtils.equals(
							defaultBillToID, defaultShipToID)){
						defaultShipToModel = _scBaseUtils.cloneModel(defaultBillToModel);
						defaultBillToModel.IsDefaultShipTo="N";
						defaultBillToModel.IsShipTo="N";
						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[1] = defaultBillToModel;
						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2] = defaultShipToModel;
						_scModelUtils.setStringValueAtModelPath("IsDefaultShipTo", "N", 
								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
						_scModelUtils.setStringValueAtModelPath("IsDefaultBillTo", "N", 
								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
						_scModelUtils.setStringValueAtModelPath("IsBillTo", "N", 
								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
						_scModelUtils.setStringValueAtModelPath("ID", iD+2, 
								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2]);
						_scModelUtils.setStringValueAtModelPath("ID", iD+2, 
								manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[2].PersonInfo);
					}else{        					
    					defaultShipToModel.IsDefaultShipTo="N";
						manageCustomerInput.Customer.CustomerContactList.CustomerContact.CustomerAdditionalAddressList.CustomerAdditionalAddress[1] = defaultShipToModel;
					}
					
					console.log("defaultShipToModel",defaultShipToModel);
					reloadScreen = true;
				}
				
				_scBaseUtils.removeBlankAttributes(manageCustomerInput);
                console.log("manageCustomerInput",manageCustomerInput);
                _isccsUIUtils.callApi(
                this, manageCustomerInput, "manageCustomer", null);
            }
        },
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCustomerDetails")) {
            	console.log("modelOutput",modelOutput);
            	var newCustomerAdditionalAddressList = [];
            	var customerAdditionalAddressList = _scModelUtils.getModelListFromPath(
            			"CustomerAdditionalAddressList",
            			modelOutput.Customer.CustomerContactList.CustomerContact[0]);
            	var cloneCustomerAdditionalAddressList = _scBaseUtils.cloneModel(customerAdditionalAddressList);
            	console.log("customerAdditionalAddressList",customerAdditionalAddressList);
            	console.log("customerAdditionalAddressList.CustomerAdditionalAddress.length",
            			customerAdditionalAddressList.CustomerAdditionalAddress.length);
            	var count=0;
            	var length = customerAdditionalAddressList.CustomerAdditionalAddress.length;
            	modelOutput.Customer.MaxID=length;
            	for(var i=0;i<length;i++){
            		var customerAdditionalAddress = customerAdditionalAddressList.CustomerAdditionalAddress[i];
            		var isDefaultBillTo = _scModelUtils.getStringValueFromPath("IsDefaultBillTo",customerAdditionalAddress);
            		var isDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",customerAdditionalAddress);
            		if(_scBaseUtils.equals(isDefaultBillTo,"Y") && _scBaseUtils.equals(isDefaultShipTo,"Y")){
            			customerAdditionalAddressList.ExtnDefaultBillTo = customerAdditionalAddress;
            			for(var i=0;i<cloneCustomerAdditionalAddressList.CustomerAdditionalAddress.length;i++){
            				var cloneCustomerAdditionalAddress = cloneCustomerAdditionalAddressList.CustomerAdditionalAddress[i];
            				var cloneIsDefaultBillTo = _scModelUtils.getStringValueFromPath("IsDefaultBillTo",cloneCustomerAdditionalAddress);
                    		var cloneIsDefaultShipTo = _scModelUtils.getStringValueFromPath("IsDefaultShipTo",cloneCustomerAdditionalAddress);
                    		if(_scBaseUtils.equals(cloneIsDefaultBillTo,"Y") && _scBaseUtils.equals(cloneIsDefaultShipTo,"Y")){
                    			customerAdditionalAddressList.ExtnDefaultShipTo = cloneCustomerAdditionalAddress;
                    			break;
                    		}
            			}
            			delete customerAdditionalAddressList.CustomerAdditionalAddress[i];
            		}else if(_scBaseUtils.equals(isDefaultBillTo,"Y")){
            			customerAdditionalAddressList.ExtnDefaultBillTo = customerAdditionalAddress;
            			delete customerAdditionalAddressList.CustomerAdditionalAddress[i];
            		}else if(_scBaseUtils.equals(isDefaultShipTo,"Y")){
            			customerAdditionalAddressList.ExtnDefaultShipTo = customerAdditionalAddress;
            			delete customerAdditionalAddressList.CustomerAdditionalAddress[i];
            		}else{
            			newCustomerAdditionalAddressList[count] = customerAdditionalAddressList.CustomerAdditionalAddress[i];
            			count++;
            		}
            	}
            	console.log("newCustomerAdditionalAddressList",newCustomerAdditionalAddressList);
            	customerAdditionalAddressList.CustomerAdditionalAddress = newCustomerAdditionalAddressList;
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "getCustomerDetails_output", modelOutput, null);
                }
                _scWidgetUtils.enableWidget(
                this, "btnCreateAddress");
                var sContactID = "";
                sContactID = _scModelUtils.getStringValueFromPath("Customer.CustomerContactID", _scScreenUtils.getModel(
                this, "screenInput"));
                if (
                _scBaseUtils.getAttributeCount(
                _scModelUtils.getModelListFromPath("Customer.CustomerAdditionalAddressList", modelOutput)) > 0) {
                    _scWidgetUtils.hideWidget(
                    this, "pnlNoAddress", true);
                } else {
                    _scWidgetUtils.showWidget(
                    this, "pnlNoAddress", true, null);
                }
                if (!(
                _scBaseUtils.isVoid(
                sContactID))) {
                    var customerContact = null;
                    customerContact = _isccsCustomerUtils.getCustomerContactForID(
                    modelOutput, sContactID);
                    customerContact = _isccsCustomerUtils.mergeAddress(
                    customerContact, modelOutput);
                    _scScreenUtils.setModel(
                    this, "getCustomerAdditionalAddressList_output", _scModelUtils.getModelObjectFromPath("CustomerContact.CustomerAdditionalAddressList", customerContact), null);
                } else if (
                _scBaseUtils.equals(
                _scModelUtils.getStringValueFromPath("Customer.CustomerType", modelOutput), "02")) {
                    var consumerContact = null;
                    consumerContact = _isccsCustomerUtils.getConsumerContactModel(
                    modelOutput);
                    _scScreenUtils.setModel(
                    this, "getCustomerAdditionalAddressList_output", _scModelUtils.getModelObjectFromPath("CustomerContact.CustomerAdditionalAddressList", consumerContact), null);
                    if (
                    _scBaseUtils.getAttributeCount(
                    _scModelUtils.getModelListFromPath("CustomerContact.CustomerAdditionalAddressList", consumerContact)) > 0) {
                        _scWidgetUtils.hideWidget(
                        this, "pnlNoAddress", true);
                    } else {
                        _scWidgetUtils.showWidget(
                        this, "pnlNoAddress", true, null);
                    }
                } else {
                    _scScreenUtils.setModel(
                    this, "getCustomerAdditionalAddressList_output", _scModelUtils.getModelObjectFromPath("Customer.CustomerAdditionalAddressList", modelOutput), null);
                }
                if(reloadScreen){
                	_scScreenUtils.reloadScreen(this);
					_scScreenUtils.reloadScreen(_isccsUIUtils.getParentScreen(this, true));
                }
            }
            if(_scBaseUtils.equals(
            		mashupRefId, "manageCustomer")){
            	console.log("modelOutput",modelOutput);
            	_isccsUIUtils.callApi(this, _scScreenUtils.getTargetModel(
                        this, "getCustomerDetails_input", null), "getCustomerDetails", null);
            }
        }
	});
});