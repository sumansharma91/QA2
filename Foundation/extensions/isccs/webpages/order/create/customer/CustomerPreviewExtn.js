
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/create/customer/CustomerPreviewExtnUI",
		  "scbase/loader!isccs/utils/ModelUtils",
		  "scbase/loader!isccs/utils/OrderUtils",
		  "scbase/loader!isccs/utils/SharedComponentUtils", 
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/CustomerUtils"
		  ]
,
function(			 
		_dojodeclare,
		_extnCustomerPreviewExtnUI,
		_isccsModelUtils,
		_isccsOrderUtils,
		_isccsSharedComponentUtils,
		_isccsUIUtils,
		_scBaseUtils,
		_scEditorUtils,
		_scEventUtils,
		_scModelUtils,
		_scScreenUtils,
		_scWidgetUtils,
		_isccsCustomerUtils
){ 
	return _dojodeclare("extn.order.create.customer.CustomerPreviewExtn", [_extnCustomerPreviewExtnUI],{
	// custom code here
	initializeScreen: function(
    event, bEvent, ctrl, args) {
        this.personInfoList = null;
        var advancedCustomerList_input = null;
        this.personInfoList = [];
        advancedCustomerList_input = _scBaseUtils.getValueFromPath("getAdvancedCustomerList_input", this);
        if (!(
        _scBaseUtils.isVoid(
        advancedCustomerList_input))) {
            _scScreenUtils.setModel(
            this, "getAdvancedCustomerList_input", advancedCustomerList_input, null);
        }
        this.showCustomerFromOrder = this.showAddressFromOrder;
        this.handleCustomerPreview();
        //Copy the information entered for Customer info(such as FirstName, LastName, EmailID) to the Billing Address for Consumer Customers
        var customerModel = _scScreenUtils.getModel(
        this, "Customer");
		if(_scBaseUtils.isVoid(customerModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddress)){
		customerModelWithAddresses = _scScreenUtils.getModel(this,"extn_getCompleteCustomerDetails_output");
console.log("customerModel2",customerModel);
		customerModel =customerModelWithAddresses;
		_scScreenUtils.setModel(
		this, "Customer", customerModelWithAddresses, null);
	}
        console.log("customerModel",customerModel);
        if(customerModel){
                var custType = customerModel.Customer.CustomerType;
                var custID = customerModel.Customer.CustomerID;
                var customerContactModel = _scScreenUtils.getModel(
                this, "customerContactModel");

                if(!customerContactModel.DefaultBillToAddress && custType=="02"){
                    var clonedCustomerContactModel  = _scBaseUtils.cloneModel(customerContactModel);
                    var customerContactDataModel = this.getCustomerContactData(custID);
                    clonedCustomerContactModel.DefaultBillToAddress={};
                    clonedCustomerContactModel.DefaultBillToAddress.PersonInfo={};
                    clonedCustomerContactModel.DefaultBillToAddress.PersonInfo=customerContactDataModel;
                    //Handle case difference of emailid in CustomerContact(EmailID) and PersonInfo(EMailID)
                    clonedCustomerContactModel.DefaultBillToAddress.PersonInfo.EMailID=clonedCustomerContactModel.DefaultBillToAddress.PersonInfo.EmailID;
                    var rerender = null;
                    rerender = {};
                    _scBaseUtils.setAttributeValue("isRerender", true, rerender);
                    _scScreenUtils.setModel(
                    this, "customerContactModel", clonedCustomerContactModel, rerender);
                    this.updateAddressPanels(
                    clonedCustomerContactModel);
                }
        }
    },
    updateTaxExamptID: function(
    event, bEvent, ctrl, args) {
        var eventDefn = null;
        var blankModel = null;
        var CustomerObj = null;
        var tmodel = null;
        var customerModel = null;
        
        tmodel = _scScreenUtils.getTargetModel(
        this, "selectedRow_input", null);
        var extnMode = _scScreenUtils.getTargetModel(
                this, "extn_selectedRow_input", null);
        if(_scBaseUtils.isVoid(extnMode.Order)){
        	extnMode.Order={};
        	extnMode.Order.TaxExemptionCertificate = undefined;
        }
        tmodel.Order.TaxExemptionCertificate = extnMode.Order.TaxExemptionCertificate;
        console.log("extnMode",extnMode);
        console.log("tmodel",tmodel);
        customerModel = _scScreenUtils.getModel(
                this, "Customer");
        console.log("customerModel",customerModel);
        var currentTaxExamptCode = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnTaxExemptionCode", customerModel);
        console.log("currentTaxExamptCode",currentTaxExamptCode);
        var parent = this.ownerScreen;
        var custIdScreen = parent.ownerScreen;
        if(!_scBaseUtils.isVoid(currentTaxExamptCode)){
        	custIdScreen.taxExamptExist=true;
        }
        _scModelUtils.setStringValueAtModelPath("Customer.Extn.ExtnTaxExemptionCode", 
        		_scModelUtils.getStringValueFromPath("Order.TaxExemptionCertificate", tmodel), customerModel);
        console.log("customerModel",customerModel);
        
        eventDefn = {};
        blankModel = {};
        blankModel.TaxExemptionCertificate = extnMode.Order.TaxExemptionCertificate;
        eventDefn["argumentList"] = blankModel;
        _scEventUtils.fireEventToParent(
        this, "extn_taxExamptUpdated", eventDefn);
        
        var setModelOptions = null;
        setModelOptions = {};
        _scBaseUtils.setAttributeValue("isRerender", false, setModelOptions);
        _scBaseUtils.setAttributeValue("checkDirtyState", true, setModelOptions);
        _scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptions);
        _scScreenUtils.setModel(
        this, "Customer", customerModel, setModelOptions);
        
    },
    updateShipPreference: function(
    event, bEvent, ctrl, args) {
        var eventDefn = null;
        var blankModel = null;
        var tmodel = null;
        var customerModel = null;
        
        tmodel = _scScreenUtils.getTargetModel(
        this, "selectedRow_input", null);
        var extnMode = _scScreenUtils.getTargetModel(
                this, "extn_selectedRow_input", null);
        console.log("extnMode",extnMode);
        if(_scBaseUtils.isVoid(extnMode.Order)){
        	extnMode.Order={};
        	extnMode.Order.SCAC = undefined;
        }
        tmodel.Order.SCAC = extnMode.Order.SCAC;
        customerModel = _scScreenUtils.getModel(
                this, "Customer");
        console.log("customerModel",customerModel);
        
        eventDefn = {};
        blankModel = {};
        blankModel.SCAC = extnMode.Order.SCAC;
        var orgModel = _scScreenUtils.getModel(this, "extn_getOrgList_Output");
		console.log("orgModel",orgModel);
		var organizations = orgModel.OrganizationList.Organization;
		if(!_scBaseUtils.isVoid(organizations)){
			for(var i=0;i<organizations.length;i++){
				var orgName = organizations[i].OrganizationName;
				if(_scBaseUtils.equals(extnMode.Order.SCAC,orgName)){
					blankModel.SCACForOrder =  organizations[i].OrganizationCode;
				}
			}
		}
console.log("blankModel.SCACForOrder",blankModel.SCACForOrder);
        eventDefn["argumentList"] = blankModel;
        _scEventUtils.fireEventToParent(
        this, "extn_shipPreferenceUpdated", eventDefn);
        
        var currentShipPreference = _scModelUtils.getStringValueFromPath("Customer.Extn.ExtnPreferredCarrier", customerModel);
        var parent = this.ownerScreen;
        var custIdScreen = parent.ownerScreen;
        if(!_scBaseUtils.isVoid(currentShipPreference)){
        	custIdScreen.shipPrefExist=true;
        }
        console.log("currentShipPreference",currentShipPreference);
        _scModelUtils.setStringValueAtModelPath("Customer.Extn.ExtnOriginalPreferredCarrier", 
        		currentShipPreference, customerModel);
        _scModelUtils.setStringValueAtModelPath("Customer.Extn.ExtnPreferredCarrier", 
        		_scModelUtils.getStringValueFromPath("Order.SCAC", tmodel), customerModel);
        console.log("customerModel",customerModel);
        
        var setModelOptions = null;
        setModelOptions = {};
        _scBaseUtils.setAttributeValue("isRerender", false, setModelOptions);
        _scBaseUtils.setAttributeValue("checkDirtyState", true, setModelOptions);
        _scBaseUtils.setAttributeValue("clearOldVals", false, setModelOptions);
        _scScreenUtils.setModel(
        this, "Customer", customerModel, setModelOptions);
    },
    showLoyaltyDetails: function(event, bEvent, ctrl, args) {
    	console.log("HERE");
    	var customerModel = _scScreenUtils.getModel(
                this, "Customer");
    	console.log("customerModel",customerModel);
    	var inputModel = {};
    	inputModel.Customer = {};
    	inputModel.Customer.CustomerNumber = customerModel.Customer.CustomerID;
    	console.log("inputModel",inputModel);
    	
    	var popupParams = {};
    	popupParams["screenInput"] = inputModel;
    	
    	var dialogParams = null;
        dialogParams = {};
        _scBaseUtils.setAttributeValue("closeCallBackHandler", "callBackHandler", dialogParams);
		_isccsUIUtils.openSimplePopup("extn.customer.loyalty.CustomerLoyaltyDetails", "Loyalty Details", this, popupParams, dialogParams);
    },
    callBackHandler: function(){
//     	Do Nothing
    }
});
});

