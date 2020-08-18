
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/customer/search/CustomerListScreenExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils"]
,
function(			 
			 _dojodeclare,
			 _extnCustomerListScreenExtnUI,
			 _scModelUtils
){ 
	return _dojodeclare("extn.customer.search.CustomerListScreenExtn", [_extnCustomerListScreenExtnUI],{
	// custom code here
	
	//------------------Method to return HealthyAwards#------------------//
	getHealthyAwards: function(gridReference, rowIndex, columnIndex, gridRowJSON, unformattedValue) {
            var returnValue = " ";
            var customerContact = null;
			customerContact = _scModelUtils.getModelListFromPath("CustomerContactList.CustomerContact", gridRowJSON)[0];
            returnValue = _scModelUtils.getStringValueFromKey("HealthyAwardsNo", customerContact);
            return returnValue;
        },

});
});

