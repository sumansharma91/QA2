
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/common/customer/BusinessCardExtnUI",
	"scbase/loader!isccs/utils/CustomerUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils"
	]
,
function(			 
	_dojodeclare,
	_extnBusinessCardExtnUI,
	_isccsCustomerUtils,
	_scScreenUtils,
	_isccsUIUtils,
	_scBaseUtils,
	_scModelUtils,
	_scResourcePermissionUtils
	
){ 
	return _dojodeclare("extn.common.customer.BusinessCardExtn", [_extnBusinessCardExtnUI],{
	// custom code here
	getCustomerModel: function(
        editorInput) {
        	
            var customerDetailModel = null;
            customerDetailModel = _scScreenUtils.getModel(
            this, "CustomerInfo");
            var sBillTo = null;
            var enterpriseCode = null;
            sBillTo = _scModelUtils.getStringValueFromPath("Order.BillToID", editorInput);
            enterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", editorInput);
            var sOrderName = _scModelUtils.getStringValueFromPath("Order.OrderName", editorInput);
            var sCurBillTo = null;
            if (!(
            _scBaseUtils.isVoid(
            customerDetailModel))) {
                sCurBillTo = _scModelUtils.getStringValueFromPath("Customer.CustomerID", customerDetailModel);
            }
           // alert(sBillTo.startsWith("S"))
            if(_scBaseUtils.equals("MigratedOrder",sOrderName) || _scBaseUtils.isVoid(sBillTo) || !_scBaseUtils.equals("VSI.com",enterpriseCode) || 
            		!_scResourcePermissionUtils.hasPermission("ISCRET0002"))
            {
            	this.switchNameDisplay(
                    false);
                    return;
            }
            if (!(
            _scBaseUtils.equals(
            sCurBillTo, sBillTo))) {
                var sEntCode = null;
                sEntCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", editorInput);
                if (!(
                _scBaseUtils.isVoid(
                sBillTo) || _scBaseUtils.isVoid(
                sEntCode))) {
                    var apiInput = null;
                    apiInput = _scModelUtils.createNewModelObjectWithRootKey("Customer");
                    _scModelUtils.setStringValueAtModelPath("Customer.CustomerID", sBillTo, apiInput);
                    _scModelUtils.setStringValueAtModelPath("Customer.OrganizationCode", sEntCode, apiInput);
                    _scScreenUtils.setModel(
                    this, "CustomerInfo", apiInput, null);
                    var mashupContext = null;
                    _isccsUIUtils.callApi(
                    this, apiInput, "extn_getCompleteCustomerDetails", mashupContext);
                    this.switchNameDisplay(
                    true);
                } else {
                    this.switchNameDisplay(
                    false);
                }
            }
    },
	handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "getCustomerDetails")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "CustomerInfo", modelOutput, null);
                }
                if (
                _scBaseUtils.equals(
                mashupRefId, "getCustomerDetails")) {
                    if (!(
                    _scBaseUtils.equals(
                    false, applySetModel))) {
                        _scScreenUtils.setModel(
                        this, "CustomerInfo", modelOutput, null);
                    }
                    var editorInput = null;
                    editorInput = _scScreenUtils.getModel(
                    this, "editorInput");
                    _scScreenUtils.setModel(
                    this, "editorInput", editorInput, null);
                }
            } else if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getCompleteCustomerDetails")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "CustomerInfo", modelOutput, null);
                }
                if (
                _scBaseUtils.equals(
                mashupRefId, "extn_getCompleteCustomerDetails")) {
                    if (!(
                    _scBaseUtils.equals(
                    false, applySetModel))) {
                        _scScreenUtils.setModel(
                        this, "CustomerInfo", modelOutput, null);
                    }
                    var editorInput = null;
                    editorInput = _scScreenUtils.getModel(
                    this, "editorInput");
                    _scScreenUtils.setModel(
                    this, "editorInput", editorInput, null);
                }
            }
        },
});
});

