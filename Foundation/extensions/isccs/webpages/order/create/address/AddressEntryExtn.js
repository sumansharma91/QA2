
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!isccs/utils/UIUtils",  "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/ModelUtils",  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/SearchUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils", "scbase/loader!extn/order/create/address/AddressEntryExtnUI"]
,
function(			 
			    _dojodeclare,
				_scScreenUtils,
				_scBaseUtils,
				_isccsUIUtils,
				_scModelUtils,
				_isccsModelUtils,
				_isccsUIUtils,
				_scWidgetUtils,
				_isccsSearchUtils,
				_scEditorUtils,
			    _extnAddressEntryExtnUI
){ 
	return _dojodeclare("extn.order.create.address.AddressEntryExtn", [_extnAddressEntryExtnUI],{
	// custom code here
	
	manageGenerateCallTagAlert: function (event, bEvent, ctrl, args) {
	
	var vselectedLine = _scScreenUtils.getModel(this, "getCompleteOrderDetails_output", null);
	
	var sOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", vselectedLine);
	console.log("This is in manageGenerateCallTagAlert OrderHeaderKey",sOrderHeaderKey);
	
	if(!_scBaseUtils.isVoid(sOrderHeaderKey)){
	
	var vReturnLine = _scScreenUtils.getTargetModel(this, "extn_changeOrderHeaderToGenerateTag_Input", null);
	var sGenerateCallTag = _scModelUtils.getStringValueFromPath("Order.Extn.ExtnGenerateCallTagalert", vReturnLine);
	
	
	_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", sOrderHeaderKey , vReturnLine);
	
	console.log("This is in manageGenerateCallTagAlert after adding Order Header Key",vReturnLine);
	
	_isccsUIUtils.callApi(this, vReturnLine, "extn_changeOrderHeader_CallTag", null);
	
	}
	
	},
	initializeScreen: function(
        event, bEvent, ctrl, args) {
        	var screenMode = this.getScreenMode(); 
    		if(_scBaseUtils.equals(screenMode,"ReturnAddress")){
    			// address panels should be collapsed in return flow.
    			_isccsSearchUtils.closeSearchCriteriaPanel(this,"orderAddresses");
    		}
            this.personInfoList = null;
            this.personInfoList = [];
            var initialInputData = null;
            initialInputData = _scScreenUtils.getInitialInputData(
            _scEditorUtils.getCurrentEditor());
            if (!(
            _scBaseUtils.isVoid(
            _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", initialInputData)))) {
                this.callGetCompleteOrderDetails(
                initialInputData, null);
            }
            var getShowCustSearchRuleDetails_output = null;
            getShowCustSearchRuleDetails_output = _scScreenUtils.getModel(
            this, "getShowCustSearchRuleDetails_output");
            var showCustomerSearch = null;
            showCustomerSearch = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", getShowCustSearchRuleDetails_output);
            if (
            _scBaseUtils.equals(
            showCustomerSearch, "N")) {
                _scWidgetUtils.showWidget(
                this, "memberLoginId", true, null);
            }
            var skipCustomer = null;
            skipCustomer = _isccsUIUtils.getWizardModel(
            this, "SkipCustomer_output");
            var skipCustomer_Action = null;
            if (!(
            _scBaseUtils.isVoid(
            skipCustomer))) {
                skipCustomer_Action = _scModelUtils.getStringValueFromPath("Order.Action", skipCustomer);
            }
            if (
            _scBaseUtils.equals(
            skipCustomer_Action, "SkipCustomer")) {
                _scWidgetUtils.hideWidget(
                this, "billingAddressPanel", true);
            }
			
			_scWidgetUtils.hideWidget(this, "extn_Generate_Call_Tag_Alert", false);
			
			var orderModel = _isccsUIUtils.getWizardInputModel(this);
			var vDocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", orderModel);
	
				if (_scBaseUtils.equals(vDocumentType, "0003")) {
					_scWidgetUtils.showWidget(this, "extn_Generate_Call_Tag_Alert", false, null);
				}
			
        }
	
	
});
});

