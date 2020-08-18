
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/item/browsing/ItemBrowsingExtnUI",
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/ContextUtils", 
		  "scbase/loader!isccs/utils/EventUtils", 
		  "scbase/loader!isccs/utils/ItemUtils", 
		  "scbase/loader!isccs/utils/ModelUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!isccs/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ControllerUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EventUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
		  ]
,
function(_dojodeclare,
	     _extnItemBrowsingExtnUI,
		 _isccsBaseTemplateUtils, 
		 _isccsContextUtils, 
		 _isccsEventUtils, 
		 _isccsItemUtils, 
		 _isccsModelUtils, 
		 _isccsUIUtils, 
		 _isccsWidgetUtils, 
		 _scBaseUtils, 
		 _scControllerUtils, 
		 _scEditorUtils, 
		 _scEventUtils, 
		 _scModelUtils, 
		 _scScreenUtils, 
		 _scWidgetUtils
		 
){ 
	return _dojodeclare("extn.item.browsing.ItemBrowsingExtn", [_extnItemBrowsingExtnUI],{
	// custom code here
		
	initializeScreen: function() {
            var catalogItemListResult = null;
            var userEnterprise = null;
            var homeScreenInput = null;
            var screen_ip = null;
            var catagoryList = null;
            var searchText = null;
            var sDefaultEnterprise = null;
            var sUserEnterprise = null;
            var element = null;
            var editorInput = null;
            this.breadCrumbList = [];
            var initialInputData = null;
            initialInputData = _scScreenUtils.getInitialInputData(
            this);
            var CustomerID = null;
            CustomerID = _scModelUtils.getStringValueFromPath("CatalogSearch.CustomerInformation.CustomerID", initialInputData);
            if (!(
            _scBaseUtils.isVoid(
            CustomerID))) {
                var customerInfo = null;
                customerInfo = {};
                _scModelUtils.setStringValueAtModelPath("Customer.CustomerInformation.CustomerID", CustomerID, customerInfo);
                _scModelUtils.setStringValueAtModelPath("Customer.CustomerInformation.CustomerContactID", _scModelUtils.getStringValueFromPath("CatalogSearch.CustomerInformation.CustomerContactID", initialInputData), customerInfo);
                _scScreenUtils.setModel(this, "customerModel", customerInfo, null);
            }
            var constructorArgs = null;
            constructorArgs = {};
            var defaultOrgSelect = null;
            defaultOrgSelect = _isccsWidgetUtils.getDefaultSelectionOrgList(_scScreenUtils.getModel(this, "enterpriseList"));
            defaultOrgSelect = this.handleDefaultEnterprise(defaultOrgSelect);
            var eItem = null;
            eItem = _scModelUtils.createNewModelObjectWithRootKey("CatalogSearch");
            _scModelUtils.setStringValueAtModelPath("CatalogSearch.CallingOrganizationCode", defaultOrgSelect, eItem);
            _scScreenUtils.setModel(this, "initializeModel", eItem, null);
            searchText = _scModelUtils.getStringValueFromPath("CatalogSearch.Terms.Term.Value", initialInputData);
            homeScreenInput = _scModelUtils.getModelObjectFromPath("CatalogSearch", initialInputData);
            var emptyHomeScrInput = false;
            emptyHomeScrInput = _scBaseUtils.isVoid(homeScreenInput);
            if (
            _scBaseUtils.equals(
            emptyHomeScrInput, false)) {
                screen_ip = _scModelUtils.createNewModelObjectWithRootKey("CatalogSearch");
                _scModelUtils.addModelToModelPath("CatalogSearch", homeScreenInput, screen_ip);
                var tempModel = null;
                tempModel = _scBaseUtils.cloneModel(
                screen_ip);
                _scBaseUtils.setModel(
                this, "pagintionInputModel", tempModel, null);
                if (!(
                _scBaseUtils.isVoid(
                searchText))) {
                    _isccsModelUtils.removeAttributeFromModel("CatalogSearch.Terms", screen_ip);
                }
                _scBaseUtils.setModel(
                this, "screenInput", screen_ip, null);
            }
            catalogItemListResult = _scScreenUtils.getModel(
            this, "getCatalogItemListResult");
            constructorArgs["InputData"] = catalogItemListResult;
			
            _isccsWidgetUtils.hideOrganizationWithOneResult(this, "cmbEnterprise", _scScreenUtils.getModel(this, "enterpriseList"));

			//Shows the enterprise dropdown
			_scWidgetUtils.hideWidget(this, "cmbEnterprise", false, "");
			
            var getOrganizationListOutput = null;
            getOrganizationListOutput = _scScreenUtils.getModel(
            this, "enterpriseList");
            constructorArgs["enterpriseList"] = getOrganizationListOutput;
            if (
            _scBaseUtils.isVoid(
            searchText)) {
                constructorArgs["hideItemPanel"] = "Yes";
            } else {
                constructorArgs["hideItemPanel"] = "No";
            }
            var options = null;
            options = {};
            options["screen"] = this;
            options["createdUId"] = "ItemBrowsingList";
            options["refUId"] = "repeatingBngContainer";
            this.prepareBreadcrumbData(
            defaultOrgSelect, searchText);
            constructorArgs["breadCrumbdata"] = this.breadCrumbList;
            constructorArgs["SearchIndexFieldList"] = _scScreenUtils.getModel(
            this, "SearchIndexFieldList");
            _scScreenUtils.showScreenWithInitAPI(
            this, "isccs.item.browsing.ItemBrowsingList", constructorArgs, "", options, null);
        },
        searchForItems: function(
        event, bEvent, ctrl, args) {
            var targetModel = null;
            var mashupRefObj = null;
            var mashupContext = null;
            var searchvalue = null;
            var selectedCategory = null;
            var selectedFilter = null;
            var category_model = null;
            var catmodel = null;
            var pselectedCategory = "";
            var selectedPage = null;
            var sortField = null;
            var pageSize = null;
            var sortDescending = null;
            var breadCrumbText = null;
            var breadCrumbModel = null;
            var SelectedPageSize = null;
            var clearSearchInput = null;
            clearSearchInput = _scBaseUtils.getAttributeValue("clearSearchInput", false, args);
            breadCrumbModel = _scModelUtils.createNewModelObjectWithRootKey("breadcrumbdata");
            targetModel = _scBaseUtils.getTargetModel(
            this, "searchCataLogIndex_input", null);
			_scModelUtils.setStringValueAtModelPath("CatalogSearch.CallingOrganizationCode", "VSI", targetModel);
            _scModelUtils.setStringValueAtModelPath("CatalogSearch.Item.CustomerInformation.BuyerUserId", _scModelUtils.getStringValueFromPath("CatalogSearch.Item.CustomerInformation.BuyerUserId", _scScreenUtils.getInitialInputData(
            _scEditorUtils.getCurrentEditor())), targetModel);
            _scModelUtils.setStringValueAtModelPath("CatalogSearch.Item.CustomerInformation.CustomerID", _scModelUtils.getStringValueFromPath("CatalogSearch.Item.CustomerInformation.CustomerID", _scScreenUtils.getInitialInputData(
            _scEditorUtils.getCurrentEditor())), targetModel);
            selectedPage = _scBaseUtils.getAttributeValue("selectedPage", false, args);
            sortDescending = _scBaseUtils.getAttributeValue("SortDescending", false, args);
            sortField = _scBaseUtils.getAttributeValue("SortField", false, args);
            pageSize = _scBaseUtils.getAttributeValue("PageSize", false, args);
            if (!(
            _scBaseUtils.isVoid(
            selectedPage))) {
                targetModel = _scScreenUtils.getModel(
                this, "pagintionInputModel");
                if (
                _scBaseUtils.isVoid(
                this.hiddenSearchText)) {
                    _isccsModelUtils.removeAttributeFromModel("CatalogSearch.Terms", targetModel);
                }
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageNumber", selectedPage, targetModel);
                mashupRefObj = {};
                mashupRefObj["mashupRefId"] = "searchCatalog";
                mashupRefObj["mashupInputObject"] = targetModel;
                mashupContext = _scControllerUtils.getMashupContext(
                this);
                mashupContext["IsPageClicked"] = "Yes";
                _isccsUIUtils.callApi(
                this, targetModel, "searchCatalog", mashupContext);
            } else if (!(
            _scBaseUtils.isVoid(
            sortField))) {
                if (
                _scBaseUtils.equals(
                sortField, "Relevance")) {
                    sortField = "";
                }
                targetModel = _scScreenUtils.getModel(
                this, "pagintionInputModel");
                if (
                _scBaseUtils.isVoid(
                this.hiddenSearchText)) {
                    _isccsModelUtils.removeAttributeFromModel("CatalogSearch.Terms", targetModel);
                }
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.SortField", sortField, targetModel);
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.SortDescending", sortDescending, targetModel);
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageNumber", "1", targetModel);
                mashupRefObj = {};
                mashupRefObj["mashupRefId"] = "searchCatalog";
                mashupRefObj["mashupInputObject"] = targetModel;
                mashupContext = _scControllerUtils.getMashupContext(
                this);
                mashupContext["IsSortClicked"] = "Yes";
                _isccsUIUtils.callApi(
                this, targetModel, "searchCatalog", mashupContext);
            } else if (!(
            _scBaseUtils.isVoid(
            pageSize))) {
                targetModel = _scScreenUtils.getModel(
                this, "pagintionInputModel");
                if (
                _scBaseUtils.isVoid(
                this.hiddenSearchText)) {
                    _isccsModelUtils.removeAttributeFromModel("CatalogSearch.Terms", targetModel);
                }
                var tmpModel = null;
                tmpModel = _scBaseUtils.cloneModel(
                targetModel);
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", pageSize, tmpModel);
                _scBaseUtils.setModel(
                this, "pagintionInputModel", tmpModel, null);
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", pageSize, targetModel);
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageNumber", "1", targetModel);
                mashupRefObj = {};
                mashupRefObj["mashupRefId"] = "searchCatalog";
                mashupRefObj["mashupInputObject"] = targetModel;
                mashupContext = _scControllerUtils.getMashupContext(
                this);
                mashupContext["IsPageClicked"] = "Yes";
                _isccsUIUtils.callApi(
                this, targetModel, "searchCatalog", mashupContext);
            } else {
                selectedCategory = _scBaseUtils.getAttributeValue("model.CatalogSearch.CategoryPath", false, args);
                if (!(
                _scBaseUtils.isVoid(
                selectedCategory))) {
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.CategoryPath", selectedCategory, targetModel);
                    SelectedPageSize = _scModelUtils.getStringValueFromPath("CatalogSearch.PageSize", _scScreenUtils.getModel(
                    this, "pagintionInputModel"));
                    if (!(
                    _scBaseUtils.isVoid(
                    SelectedPageSize))) {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", SelectedPageSize, targetModel);
                    } else {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", _isccsContextUtils.getPaginationSize("CatalogSearch"), targetModel);
                    }
                    category_model = _scBaseUtils.getAttributeValue("model", false, args);
                    _scBaseUtils.setModel(
                    this, "categoryModel", category_model, null);
                }
                breadCrumbText = _scBaseUtils.getAttributeValue("breadCrumbText", false, args);
                selectedFilter = _scBaseUtils.getAttributeValue("fmodel", false, args);
                var sfilterModel = null;
                sfilterModel = _scScreenUtils.getModel(
                this, "filterModel");
                if (
                _scBaseUtils.isVoid(
                sfilterModel)) {
                    var modelList = null;
                    modelList = [];
                    if (!(
                    _scBaseUtils.isVoid(
                    selectedFilter))) {
                        modelList.push(
                        selectedFilter);
                        _scBaseUtils.setModel(
                        this, "filterModel", modelList, null);
                        _scModelUtils.addModelToModelPath("CatalogSearch.Filters.Filter", modelList, targetModel);
                    }
                } else {
                    var modelList1 = sfilterModel;
                    if (!(
                    _scBaseUtils.isVoid(
                    selectedFilter))) {
                        modelList1.push(
                        selectedFilter);
                    }
                    _scBaseUtils.setModel(
                    this, "filterModel", modelList1, null);
                    _scModelUtils.addModelToModelPath("CatalogSearch.Filters.Filter", modelList1, targetModel);
                }
                catmodel = _scScreenUtils.getModel(
                this, "categoryModel");
                if (!(
                _scBaseUtils.isVoid(
                catmodel))) {
                    pselectedCategory = _scModelUtils.getStringValueFromPath("CatalogSearch.CategoryPath", catmodel);
                }
                _scModelUtils.setStringValueAtModelPath("CatalogSearch.CategoryPath", pselectedCategory, targetModel);
                SelectedPageSize = _scModelUtils.getStringValueFromPath("CatalogSearch.PageSize", _scScreenUtils.getModel(
                this, "pagintionInputModel"));
                if (!(
                _scBaseUtils.isVoid(
                SelectedPageSize))) {
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", SelectedPageSize, targetModel);
                } else {
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", _isccsContextUtils.getPaginationSize("CatalogSearch"), targetModel);
                }
                if (!(
                _scBaseUtils.isVoid(
                this.hiddenSearchText))) {
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.Terms.Term.Value", this.hiddenSearchText, targetModel);
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.Terms.Term.Condition", "MUST", targetModel);
                    SelectedPageSize = _scModelUtils.getStringValueFromPath("CatalogSearch.PageSize", _scScreenUtils.getModel(
                    this, "pagintionInputModel"));
                    if (!(
                    _scBaseUtils.isVoid(
                    SelectedPageSize))) {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", SelectedPageSize, targetModel);
                    } else {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", _isccsContextUtils.getPaginationSize("CatalogSearch"), targetModel);
                    }
                }
                if (!(
                _isccsModelUtils.isEmptyModel(
                _scScreenUtils.getModel(
                this, "customerModel")))) {
                    var customerModel = null;
                    customerModel = _scScreenUtils.getModel(
                    this, "customerModel");
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.Item.CustomerInformation.CustomerID", _scModelUtils.getStringValueFromPath("Customer.CustomerInformation.CustomerID", customerModel), targetModel);
                    _scModelUtils.setStringValueAtModelPath("CatalogSearch.Item.CustomerInformation.CustomerContactID", _scModelUtils.getStringValueFromPath("Customer.CustomerInformation.CustomerContactID", customerModel), targetModel);
                }
                mashupRefObj = {};
                mashupRefObj["mashupRefId"] = "searchCatalog";
                mashupRefObj["mashupInputObject"] = targetModel;
                mashupContext = _scControllerUtils.getMashupContext(
                this);
                _scBaseUtils.setModel(
                this, "pagintionInputModel", targetModel, null);
                if (!(
                _scBaseUtils.isVoid(
                breadCrumbText))) {
                    var parentbreadCrumbText = null;
                    parentbreadCrumbText = _scBaseUtils.getAttributeValue("parentbreadCrumbText", false, args);
                    if (!(
                    _scBaseUtils.isVoid(
                    parentbreadCrumbText))) {
                        var tmpbreadCrumbModel = null;
                        tmpbreadCrumbModel = _scModelUtils.createNewModelObjectWithRootKey("breadcrumbdata");
                        _scModelUtils.setStringValueAtModelPath("breadcrumbdata.breadCrumbText", parentbreadCrumbText, tmpbreadCrumbModel);
                        var tmpModel = null;
                        tmpModel = _scBaseUtils.cloneModel(
                        targetModel);
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.CategoryPath", _scBaseUtils.getAttributeValue("parentCatgPath", false, args), tmpModel);
                        _scModelUtils.addModelToModelPath("breadcrumbdata.breadCrumbValue", tmpModel, tmpbreadCrumbModel);
                        _scModelUtils.addModelObjectToModelList(
                        tmpbreadCrumbModel, this.breadCrumbList);
                    }
                    if (
                    _scBaseUtils.equals(
                    breadCrumbText, "SearchTerm")) {
                        _scModelUtils.setStringValueAtModelPath("breadcrumbdata.breadCrumbText", this.hiddenSearchText, breadCrumbModel);
                    } else {
                        _scModelUtils.setStringValueAtModelPath("breadcrumbdata.breadCrumbText", breadCrumbText, breadCrumbModel);
                    }
                    _scModelUtils.addModelToModelPath("breadcrumbdata.breadCrumbValue", targetModel, breadCrumbModel);
                    _scModelUtils.addModelObjectToModelList(
                    breadCrumbModel, this.breadCrumbList);
                }
                if (
                _scBaseUtils.isVoid(
                _scModelUtils.getStringValueFromPath("CatalogSearch.PageSize", targetModel))) {
                    SelectedPageSize = _scModelUtils.getStringValueFromPath("CatalogSearch.PageSize", _scScreenUtils.getModel(
                    this, "pagintionInputModel"));
                    if (!(
                    _scBaseUtils.isVoid(
                    SelectedPageSize))) {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", SelectedPageSize, targetModel);
                    } else {
                        _scModelUtils.setStringValueAtModelPath("CatalogSearch.PageSize", _isccsContextUtils.getPaginationSize("CatalogSearch"), targetModel);
                    }
                }
                _isccsUIUtils.callApi(
                this, targetModel, "searchCatalog", null);
            }
        }
	
});
});

