
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/order/create/customer/CreateOrderCustomerListScreenExtnUI",
          "scbase/loader!isccs/utils/CustomerUtils", 
          "scbase/loader!isccs/utils/OrderUtils", 
          "scbase/loader!isccs/utils/WidgetUtils", 
          "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
          "scbase/loader!sc/plat/dojo/utils/EditorUtils", 
          "scbase/loader!sc/plat/dojo/utils/EventUtils", 
          "scbase/loader!sc/plat/dojo/utils/GridxUtils", 
          "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare,
			    _extnCreateOrderCustomerListScreenExtnUI,
			    _isccsCustomerUtils, 
			    _isccsOrderUtils, 
			    _isccsWidgetUtils, 
			    _scBaseUtils, 
			    _scEditorUtils, 
			    _scEventUtils, 
			    _scGridxUtils, 
			    _scModelUtils, 
			    _scScreenUtils, 
			    _scWidgetUtils
){ 
	return _dojodeclare("extn.order.create.customer.CreateOrderCustomerListScreenExtn", [_extnCreateOrderCustomerListScreenExtnUI],{
	// custom code here
		getAddressDisplay: function(
        gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue) {
            var Address = null;
            var customerContact = null;
            var screen = null;
            var returnValue = " ";
            screen = _isccsWidgetUtils.getOwnerScreen(
            gridReference);
            Address = _scModelUtils.getModelObjectFromPath("DefaultShipToAddress", gridRowJSON);
            if (
            _scBaseUtils.isVoid(
            Address)) {
                customerContact = _scModelUtils.getModelListFromPath("CustomerContactList.CustomerContact", gridRowJSON)[
                0];
                Address = _scModelUtils.getModelObjectFromPath("DefaultShipToAddress", customerContact);
            }
            returnValue = _isccsCustomerUtils.getCustomerShortAddress(
            screen, Address);
            return returnValue;
        }
});
});

