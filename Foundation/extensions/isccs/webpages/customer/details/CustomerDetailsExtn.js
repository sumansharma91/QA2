scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/customer/details/CustomerDetailsExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!isccs/utils/RelatedTaskUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils"
		  ]
,
function(_dojodeclare,
		 _extnCustomerDetailsExtnUI,
		 _scWidgetUtils,
		 _scScreenUtils,
		 _isccsRelatedTaskUtils,
		 _scEditorUtils,
		 _scBaseUtils,
		 _scEventUtils
){ 
	return _dojodeclare("extn.customer.details.CustomerDetailsExtn", [_extnCustomerDetailsExtnUI],{
	// custom code here
	
	extn_init: function(){
		_scWidgetUtils.hideWidget(this, "lblDayFax", false);
		_scWidgetUtils.hideWidget(this, "lblEveningFax", false);
		
		//Hiding extra tabs
		_scWidgetUtils.hideTabPanel(this, "customerDetailsRelatedInfo", "pnlAlerts");
		_scWidgetUtils.hideTabPanel(this, "customerDetailsRelatedInfo", "pnlReturns");
		_scWidgetUtils.hideTabPanel(this, "customerDetailsRelatedInfo", "pnlDemographics");
		_scWidgetUtils.hideTabPanel(this, "customerDetailsRelatedInfo", "pnlOrdersFromCart");
		
		var model = _scScreenUtils.getModel(
	            this, "getCompleteCustomerDetails_output");
		
		var currentEditor = _scEditorUtils.getCurrentEditor(this);
		var clonedModel = _scBaseUtils.cloneModel(model);
		if(_scBaseUtils.isVoid(clonedModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList)){
			var defaultBillToModel = clonedModel.Customer.CustomerContactList.CustomerContact[0].DefaultBillToAddress;
			var defaultShipToModel = clonedModel.Customer.CustomerContactList.CustomerContact[0].DefaultShipToAddress;
			clonedModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList = {};
			clonedModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress = [];
			clonedModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress[0] = defaultShipToModel;
			clonedModel.Customer.CustomerContactList.CustomerContact[0].CustomerAdditionalAddressList.CustomerAdditionalAddress[1] = defaultBillToModel;
			delete clonedModel.Customer.CustomerContactList.CustomerContact[0].DefaultBillToAddress;
			delete clonedModel.Customer.CustomerContactList.CustomerContact[0].DefaultShipToAddress;
		}
		_scScreenUtils.setInitialInputData(currentEditor,clonedModel);
		
		var taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);
	},
	extn_loadLoyaltyData: function(){
		console.log("DISPLAYING_RESULTS");
		
		var model = _scScreenUtils.getModel(
	            this, "getCompleteCustomerDetails_output");
	    console.log("model",model);
	    var customerNumber = model.Customer.CustomerID;
	    console.log("customerNumber",customerNumber);
        
	    var options = null;
        options = {};
        _scBaseUtils.setAttributeValue("createdUId", "extn_screenref_CustomerLoyaltyDetails", options);
        _scScreenUtils.showChildScreen(
        this, "extn_screenref_CustomerLoyaltyDetails", null, "", options, null);
        var eleInputData = {};
        eleInputData.Customer= {};
        eleInputData.Customer.CustomerNumber = customerNumber;
        console.log("eleInputData",eleInputData);
        var eventDefn = null;
        var screenargs = null;
        eventDefn = {};
        screenargs = {};
        _scBaseUtils.setAttributeValue("inputData", eleInputData, screenargs);
        _scBaseUtils.setAttributeValue("argumentList", screenargs, eventDefn);
        _scEventUtils.fireEventToChild(
        this, "extn_screenref_CustomerLoyaltyDetails", "callListApi", eventDefn);
	}
});
});



