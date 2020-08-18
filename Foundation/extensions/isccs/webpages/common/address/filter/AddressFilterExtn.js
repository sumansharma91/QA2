
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/common/address/filter/AddressFilterExtnUI", 
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
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils"
]
,
function(			 
	_dojodeclare,
	_extnAddressFilterExtnUI,
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
	return _dojodeclare("extn.common.address.filter.AddressFilterExtn", [_extnAddressFilterExtnUI],{
	// custom code here
	 handleMashupOutput: function(mashupRefId, modelOutput, modelInput, mashupContext) {
		if (_scBaseUtils.equals(mashupRefId, "getStateRegionSchema")) {
			_scScreenUtils.setModel(this, "getRegionSchemaRule_output", modelOutput, null);
		} else if (_scBaseUtils.equals(mashupRefId, "getRegionStateList_output")) {
			_scScreenUtils.setModel(this, "getRegionStateList_output", modelOutput, null);
			var personInfoModel = null;
			personInfoModel = _scScreenUtils.getTargetModel(this, "PersonInfo_input", null);
			var sCity = null;
			sCity = _scModelUtils.getStringValueFromPath("PersonInfo.City", personInfoModel);
			var sState = null;
			sState = _scModelUtils.getStringValueFromPath("PersonInfo.State", personInfoModel);
			// Added for VSI
			var sZipcode = _scModelUtils.getStringValueFromPath("PersonInfo.ZipCode", personInfoModel);
			_scModelUtils.setStringValueAtModelPath("PersonInfo.ZipCode", sZipcode.substring(0,5), personInfoModel);
			// Added for VSI
			_scScreenUtils.setModel(this, "Address", personInfoModel, null);
			if (!(_scModelUtils.hasListForPathInModel("Regions.RegionSchema.Region", modelOutput))) {
				_scWidgetUtils.hideWidget(this, "cmbState", true);
				_scWidgetUtils.disableWidget(this, "cmbState", false);
				_scWidgetUtils.showWidget(this, "txtState", true, null);
				_scWidgetUtils.enableWidget(this, "txtState");
			} else {
				_scWidgetUtils.showWidget(this, "cmbState", true, null);
				_scWidgetUtils.enableWidget(this, "cmbState");
				_scWidgetUtils.hideWidget(this, "txtState", true);
				_scWidgetUtils.disableWidget(this, "txtState", false);
			}
			if (!(_scWidgetUtils.isWidgetVisible(this, "pnlCityState"))) {
				_scWidgetUtils.disableWidget(this, "txtState", false);
				_scWidgetUtils.disableWidget(this, "cmbState", false);
			}
		}
	}
});
});

