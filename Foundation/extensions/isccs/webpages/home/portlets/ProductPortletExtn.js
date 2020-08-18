
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/home/portlets/ProductPortletExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils",
		  "scbase/loader!isccs/utils/ItemUtils"
		 ],
function(_dojodeclare,
	     _extnProductPortletExtnUI,
		 _scWidgetUtils,
		 _scBaseUtils,
		 _scModelUtils,
		 _isccsItemUtils
){ 
	return _dojodeclare("extn.home.portlets.ProductPortletExtn", [_extnProductPortletExtnUI],{
	// custom code here
	extn_initScreen: function() {
		 _scWidgetUtils.hideWidget(this, "cmbEnterprise", false, "");
    }	,
	       itemSearchAction: function(
        event, bEvent, ctrl, args) {
            var error = null;
            error = this.onSearch();
            if (!(
            error)) {
                return false;
            } else {
                var root = null;
                var searchText = null;
                root = _scBaseUtils.getTargetModel(
                this, "searchCataLogIndex_input", null);
console.log("root",root);
                searchText = _scModelUtils.getStringValueFromPath("CatalogSearch.Terms.Term.Value", root);
                if (!(
                _scBaseUtils.isVoid(
                searchText))) {
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.Terms.Term.Condition", "MUST", root);
					_scModelUtils.setStringValueAtModelPath("CatalogSearch.CallingOrganizationCode", "VSI", root);
                }
                _isccsItemUtils.openItemSearch(
                this, root, "searchCatalogIndex_portlet");
            }
        }
});
});

