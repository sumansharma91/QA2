
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/home/portlets/CustomerPortletExtnUI",
		  "scbase/loader!isccs/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!dijit",
		  "scbase/loader!isccs/utils/CustomerUtils",
		  "dojo/dom",
		  "dojo/dom-style",
		  "dojo/dom-construct"]
,
function(			 
		  _dojodeclare,
		  _extnCustomerPortletExtnUI,
		  _isccsWidgetUtils,
		  _scWidgetUtils,
		  _scBaseUtils,
		  _scScreenUtils,
		  _scModelUtils,
		  _dijit,
		  _isccsCustomerUtils,
		  dom,
		  domStyle,
		  domConstruct
){ 
	return _dojodeclare("extn.home.portlets.CustomerPortletExtn", [_extnCustomerPortletExtnUI],{
	// custom code here
	
	initScreen: function(event, bEvent, ctrl, args) {
        var root = _scBaseUtils.getTargetModel(this, "getCustomerList_input", null);
		root.Customer.CallingOrganizationCode = "VSI.com";
		root.Customer.CustomerType = "02";

		var root3 = _scScreenUtils.getModel(this, "getHomeDefaultCustomerTypeRule_output", null);
		root3.Rules.RuleSetValue = "02";
		
		_scWidgetUtils.hideWidget(this, "cb_customerEnterprise", false); //hides the Enterprise: Hub & VSI	
	
		
	//------------------HIDE RADIOBUTTON BUSINESS VALUE---------------------//	
		var id = this.uIdMap.radHomeCustomerType.id;
		//_dijit.byId(id).options[0].selected=false; //Sets Business to unselected
		_dijit.byId(id).options[1].selected=true; 	 //Sets Consumer to selected	
		
		// Makes the business option invisible in the radio button
			var radioItem0 = "_RadioItem0";
			this.uIdMap.radHomeCustomerType.initialValue = '02';
			var radioID = this.uIdMap.radHomeCustomerType.valueNode.id;
			radioID = radioID + radioItem0;
			var radioItem2 = _dijit.byId(radioID);
  			var btn = radioItem2.domNode;
			dojo.style(btn, {visibility:'hidden'});
			var lbl=btn.nextSibling;

			if (lbl) {
				lbl.style.display = "none";
			}
		
        }, 
		
		
	setDisplayForCustomerType: function(screen) {
            var targetModel = null;
            targetModel = _scBaseUtils.getTargetModel(screen, "getCustomerList_input", null);
            var sCustType = "02";
            sCustType = _scModelUtils.getStringValueFromPath("Customer.CustomerType", targetModel);
            var showBusiness = true;
            if (_scBaseUtils.isVoid(sCustType)) {
                showBusiness = _scBaseUtils.equals("01", _scModelUtils.getStringValueFromPath("Customer.CustomerType", _scScreenUtils.getModel(
                screen, "screenInput")));
            } else {
                showBusiness = _scBaseUtils.equals(sCustType, "01");
            }
            if (_scBaseUtils.equals(sCustType, "01")) {
               //_scWidgetUtils.showWidget(this, "txtOrganizationName", false, null); 			//hides OrganizationName
            } else {
                var sourceModel = null;
                sourceModel = _scScreenUtils.getModel(this, "initializeModel");
                _scScreenUtils.setModel(this, "initializeModel", sourceModel, null);
                _scWidgetUtils.hideWidget(this, "txtOrganizationName", false);
            }
			
        }
	

	
});
});

