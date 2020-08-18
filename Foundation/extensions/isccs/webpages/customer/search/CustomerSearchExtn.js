
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/customer/search/CustomerSearchExtnUI",
		  "scbase/loader!isccs/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!dijit",
		  "scbase/loader!isccs/utils/CustomerUtils",
		  "dojo/dom",
		  "dojo/dom-style",
		  "scbase/loader!isccs/utils/ModelUtils",
		  "scbase/loader!isccs/utils/SearchUtils",
		  "scbase/loader!isccs/utils/BaseTemplateUtils",
		  "scbase/loader!sc/plat/dojo/utils/EventUtils"]
,
function(_dojodeclare,
	     _extnCustomerSearchExtnUI,
		  _isccsWidgetUtils,
		  _scWidgetUtils,
		  _scBaseUtils,
		  _scScreenUtils,
		  _scModelUtils,
		  _dijit,
		  _isccsCustomerUtils,
		  dom,
		  domStyle,
		  _isccsModelUtils,
		  _isccsSearchUtils,
		  _isccsBaseTemplateUtils,
		  _scEventUtils
		 
){ return _dojodeclare("extn.customer.search.CustomerSearchExtn", [_extnCustomerSearchExtnUI],{
	// custom code here
	
	//------------------ HIDE RADIOBUTTON VALUES---------------------//
	hideValue: function(event, bEvent, ctrl, args) {
		var id = this.uIdMap.radCustomerType.id;
		var radioItem0 = "_RadioItem0";
		this.uIdMap.radCustomerType.initialValue = '02';
		var radioID = this.uIdMap.radCustomerType.valueNode.id;;
		radioID = radioID + radioItem0;
		var radioItem2 = _dijit.byId(radioID);
  		var btn = radioItem2.domNode;
		dojo.style(btn, {visibility:'hidden'});
		var lbl=btn.nextSibling;

		if (lbl) {
			lbl.style.display = "none";
		}	
			
    },
	
	//------------------Sets Consumer to Selected------------------//
	defaultCustomerType: function( modelOutput, inputModel) {		
            var sCustomerType = null;
			inputModel.Customer.CustomerType = "02"; 						//Sets Consumer to selected
			//inputModel.Customer.CallingOrganizationCode = "VSI"; 			//Sets VSI to selected
            sCustomerType = _scModelUtils.getStringValueFromPath("Customer.CustomerType", inputModel);
            if (_scBaseUtils.isVoid(sCustomerType)) {
                var eDefaultCustomerType = null;
                sCustomerType = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", modelOutput);
                _scModelUtils.setStringValueAtModelPath("Customer.CustomerType", sCustomerType, inputModel);
            }
            var showBusiness = true;
            showBusiness = _scBaseUtils.equals(sCustomerType, "01");
            this.displayCustomerType(showBusiness);
            if (showBusiness) {
                _scWidgetUtils.hideWidget(this, "pnlListContainer", false);
                _scWidgetUtils.showWidget(this, "pnlListContainer2", false, null);
            } else {
                _scWidgetUtils.hideWidget(this, "pnlListContainer2", false);
                _scWidgetUtils.showWidget(this, "pnlListContainer", false, null);
            }
            _scModelUtils.setStringValueAtModelPath("Customer.CallingOrganizationCode", "VSI.com", inputModel);
            return inputModel;
        }		

		
});
});







