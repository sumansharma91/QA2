scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/item/details/ItemDetailsExtnUI", "scbase/loader!isccs/utils/ContextUtils", "scbase/loader!isccs/utils/ItemUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/SharedComponentUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/UOMUtils", "scbase/loader!isccs/utils/VariationItemUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!isccs/utils/BaseTemplateUtils"], function(
			_dojodeclare, _extnItemDetailsExtnUI, _isccsContextUtils, _isccsItemUtils, _isccsModelUtils, _isccsOrderUtils, _isccsSharedComponentUtils, _isccsUIUtils, _isccsUOMUtils, _isccsVariationItemUtils, _isccsWidgetUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,_isccsBaseTemplateUtils) {

	return _dojodeclare("extn.item.details.ItemDetailsExtn", [_extnItemDetailsExtnUI],{
	// custom code here
		
        handleAddToOrder: function(
                event, bEvent, ctrl, args) {
                    var item = null;
                    var itemModel = null;
                    var itemInfo = null;
                    var itemID = null;
                    var uom = null;
                    var wizInstance = null;
                    var currentScreen = null;
                    var popupParams = null;
                    var bindings = null;
                    var ctxOrg = null;
                    var enterpriseList = null;
                    var retVal = null;
                    var orgList = null;
                    var catalogOrg = null;
                    var organizationCode = null;
                    var initialData = null;
                    var CustomerID = null;
                    if (
                    _scBaseUtils.equals(
                    _scBaseUtils.getAttributeValue("IsRelatedItem", false, args), "Yes")) {
                        itemInfo = _scBaseUtils.getAttributeValue("model", false, args);
                    } else {
                        itemInfo = _scScreenUtils.getModel(
                        this, "itemDetailsModel");
                    }
                    ctxOrg = _scBaseUtils.getAttributeValue("screen._initialInputData.Item.CallingOrganizationCode", false, args);
                    enterpriseList = _isccsContextUtils.getFromContext("OrganizationList");
                    orgList = _scBaseUtils.getAttributeValue("OrganizationList.Organization", false, enterpriseList);
                    retVal = _isccsItemUtils.getCatalogOrgCode(
                    ctxOrg, orgList);
                    catalogOrg = _scBaseUtils.getAttributeValue("CatalogOrganizationCode", false, retVal);
                    organizationCode = _scBaseUtils.getAttributeValue("OrganizationCode", false, retVal);
                    item = {};
                    _scModelUtils.setStringValueAtModelPath("ItemList.CatalogOrganizationCode", catalogOrg, item);
                    _scModelUtils.setStringValueAtModelPath("ItemList.OrganizationCode", organizationCode, item);
                    itemID = _scBaseUtils.getAttributeValue("Item.ItemID", false, itemInfo);
                    uom = _scBaseUtils.getAttributeValue("Item.UnitOfMeasure", false, itemInfo);
                    _scModelUtils.setStringValueAtModelPath("ItemList.Item.ItemID", itemID, item);
                    _scModelUtils.setStringValueAtModelPath("ItemList.Item.UnitOfMeasure", uom, item);
                    initialData = _scScreenUtils.getModel(
                    this, "apiInput");
                    CustomerID = _scModelUtils.getStringValueFromPath("Item.CustomerInformation.CustomerID", initialData);
                    if (!(
                    _scBaseUtils.isVoid(
                    CustomerID))) {
                        _scModelUtils.setStringValueAtModelPath("ItemList.Item.CustomerInformation.CustomerID", CustomerID, item);
                        _scModelUtils.setStringValueAtModelPath("ItemList.Item.CustomerInformation.CustomerContactID", _scModelUtils.getStringValueFromPath("Item.CustomerInformation.CustomerContactID", initialData), item);
                    }
                    _scBaseUtils.setModel(
                    this, "itemModel", item, null);
                    item.ItemList.OrganizationCode="VSI.com";
                    _isccsItemUtils.handleAddToOrder(
                    event, bEvent, ctrl, args, this, item, "VSI.com");
                },
});
});

