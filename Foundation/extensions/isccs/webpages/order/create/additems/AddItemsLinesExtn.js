
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/create/additems/AddItemsLinesExtnUI",
	"scbase/loader!isccs/utils/BaseTemplateUtils", 
	"scbase/loader!isccs/utils/ContextUtils", 
	"scbase/loader!isccs/utils/DeliveryUtils", 
	"scbase/loader!isccs/utils/EventUtils", 
	"scbase/loader!isccs/utils/ModelUtils", 
	"scbase/loader!isccs/utils/OrderLineUtils", 
	"scbase/loader!isccs/utils/OrderUtils", 
	"scbase/loader!isccs/utils/UIUtils", 
	"scbase/loader!isccs/utils/WidgetUtils",
	"scbase/loader!isccs/utils/WizardUtils", 
	"scbase/loader!sc/plat/dojo/utils/BaseUtils", 
	"scbase/loader!sc/plat/dojo/utils/ControllerUtils", 
	"scbase/loader!sc/plat/dojo/utils/EditorUtils", 
	"scbase/loader!sc/plat/dojo/utils/EventUtils", 
	"scbase/loader!sc/plat/dojo/utils/GridxUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/PaginationUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
	_dojodeclare,
	_extnAddItemsLinesExtnUI,
	_isccsBaseTemplateUtils, 
	_isccsContextUtils, 
	_isccsDeliveryUtils, 
	_isccsEventUtils, 
	_isccsModelUtils, 
	_isccsOrderLineUtils, 
	_isccsOrderUtils, 
	_isccsUIUtils, 
	_isccsWidgetUtils, 
	_isccsWizardUtils, 
	_scBaseUtils, 
	_scControllerUtils, 
	_scEditorUtils, 
	_scEventUtils, 
	_scGridxUtils, 
	_scModelUtils, 
	_scPaginationUtils, 
	_scScreenUtils, 
	_scWidgetUtils
){ 
	return _dojodeclare("extn.order.create.additems.AddItemsLinesExtn", [_extnAddItemsLinesExtnUI],{
	// custom code here
	// Below Method is an OOB method, Which has been overridden to update default Delivery Method and 
	// Primary Information of the Selected OrderLine on ADD Item Page
	getOrderLineFromSelectedRow: function(){
            var orderline = {};
            var srcModelData = _scGridxUtils.getSelectedSourceRecordsUsingUId(
                this, "OLST_listGrid");
            var srcOrderlinelist = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", srcModelData);
            var srcOrderline = srcOrderlinelist[0];
             var targetModelData = _scGridxUtils.getSelectedTargetRecordsUsingUId(this, "OLST_listGrid");
             var targetOrderLineList = _scModelUtils.getModelListFromPath("OrderLineList.OrderLine", targetModelData);
             if(!_scBaseUtils.isVoid(targetOrderLineList)){
                orderline = targetOrderLineList[0];
                orderline = _scBaseUtils.mergeModel(
                srcOrderline, orderline, false);
				// Update ItemDetails Primary Information/OrderLine Delivery Method to allow only Pick Up capture in Order.
				//_scModelUtils.setStringValueAtModelPath("ItemDetails.PrimaryInformation.IsShippingAllowed", "N", orderline);
				//_scModelUtils.setStringValueAtModelPath("ItemDetails.PrimaryInformation.IsDeliveryAllowed", "N", orderline);
				//_scModelUtils.setStringValueAtModelPath("DeliveryMethod", "PICK", orderline);
             }
            return orderline;
        },
	getDeliveryMethodFromRule: function() {
            var defaultDeliveryMethodModel = null;
            var defaultDeliveryMethod = null;
            defaultDeliveryMethodModel = _scScreenUtils.getModel(
            this, "getCompleteOrderDetails_output");
	    var OrderLine = _scModelUtils.getStringValueFromPath("Order.OrderLines.OrderLine", defaultDeliveryMethodModel);
	    if(!_scBaseUtils.isVoid(OrderLine)){
	    	var defaultDeliveryMethod = _scModelUtils.getStringValueFromPath("DeliveryMethod",OrderLine[0]);
		if (!_scBaseUtils.equals(defaultDeliveryMethod,"PICK")) {
			defaultDeliveryMethod = "SHP";
		}

		}else{
		defaultDeliveryMethodModel = _scScreenUtils.getModel(this, "defaultDeliveryMethodRule_output");

		defaultDeliveryMethod = _scModelUtils.getStringValueFromPath("Rules.RuleSetValue", defaultDeliveryMethodModel);

		if (_scBaseUtils.isVoid(defaultDeliveryMethod)) {
			defaultDeliveryMethod = "SHP";
			}
		}

            return defaultDeliveryMethod;
        }
});
});

