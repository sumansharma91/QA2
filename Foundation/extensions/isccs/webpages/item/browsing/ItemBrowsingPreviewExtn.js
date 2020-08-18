scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/item/browsing/ItemBrowsingPreviewExtnUI", "scbase/loader!isccs/utils/ContextUtils", "scbase/loader!isccs/utils/ItemUtils", "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!isccs/utils/SharedComponentUtils", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!isccs/utils/UOMUtils", "scbase/loader!isccs/utils/VariationItemUtils", "scbase/loader!isccs/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!isccs/utils/BaseTemplateUtils"], function(
			_dojodeclare, _extnItemBrowsingPreviewExtnUI, _isccsContextUtils, _isccsItemUtils, _isccsModelUtils, _isccsOrderUtils, _isccsSharedComponentUtils, _isccsUIUtils, _isccsUOMUtils, _isccsVariationItemUtils, _isccsWidgetUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,_isccsBaseTemplateUtils) {

	return _dojodeclare("extn.item.browsing.ItemBrowsingPreviewExtn", [_extnItemBrowsingPreviewExtnUI],
{
        handleAddToOrder: function(
                event, bEvent, ctrl, args) {
                    var enterpriseList = null;
                    var ctxOrgModel = null;
                    var ctxOrg = null;
                    var item = null;
                    var itemID = null;
                    var uom = null;
                    var orderEnterPrise = null;
                    var enterprise = null;
                    var orgList = null;
                    var scr = null;
                    var customerModel = null;
                    item = _scBaseUtils.getTargetModel(
                    this, "ItemForOrdering", null);
                    enterpriseList = _isccsContextUtils.getFromContext("OrganizationList");
                    ctxOrgModel = _scScreenUtils.getModel(
                    this, "getItemDetails_input");
                    ctxOrg = _scBaseUtils.getAttributeValue("Item.CallingOrganizationCode", false, ctxOrgModel);
                    orgList = _scBaseUtils.getAttributeValue("OrganizationList.Organization", false, enterpriseList);
                    var retVal = null;
                    retVal = _isccsItemUtils.getCatalogOrgCode(
                    ctxOrg, orgList);
                    itemID = _scBaseUtils.getAttributeValue("screen.ModelData.ItemID", false, args);
                    uom = _scBaseUtils.getAttributeValue("screen.ModelData.UnitOfMeasure", false, args);
                    _scModelUtils.setStringValueAtModelPath("ItemList.Item.ItemID", itemID, item);
                    _scModelUtils.setStringValueAtModelPath("ItemList.Item.UnitOfMeasure", uom, item);
                    _scModelUtils.setStringValueAtModelPath("ItemList.CatalogOrganizationCode", _scBaseUtils.getAttributeValue("CatalogOrganizationCode", false, retVal), item);
                    customerModel = _scScreenUtils.getModel(
                    this, "customerModel");
                    var CustomerID = null;
                    if (!(
                    _scBaseUtils.isVoid(
                    customerModel))) {
                        CustomerID = _scModelUtils.getStringValueFromPath("Customer.CustomerInformation.CustomerID", customerModel);
                    }
                    if (!(
                    _scBaseUtils.isVoid(
                    CustomerID))) {
                        _scModelUtils.setStringValueAtModelPath("ItemList.Item.CustomerInformation.CustomerID", CustomerID, item);
                        _scModelUtils.setStringValueAtModelPath("ItemList.Item.CustomerInformation.CustomerContactID", _scModelUtils.getStringValueFromPath("Customer.CustomerInformation.CustomerContactID", customerModel), item);
                    }
                    _scBaseUtils.setModel(
                    this, "itemModel", item, null);
                    item.ItemList.OrganizationCode="VSI.com";
                    _isccsItemUtils.handleAddToOrder(
                    event, bEvent, ctrl, args, this, item, "VSI.com");
                },
});
});

