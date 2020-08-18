scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!extn/order/create/additems/OverridePricePopupExtnUI", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/PlatformUIFmkImplUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/UIUtils","scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils"]
,
function(
_dojodeclare, _extnOverridePricePopupExtnUI, _scBaseUtils, _scEventUtils, _scModelUtils, _scPlatformUIFmkImplUtils, _scScreenUtils, _scWidgetUtils, _isccsUIUtils,_scResourcePermissionUtils) {
    return _dojodeclare("isccs.order.create.additems.OverridePricePopup", [_extnOverridePricePopupExtnUI],{
	// custom code here
	        validateNewPrice: function(
        event, bEvent, ctrl, args) {
			//get new price, current price, and alpine hasmap
            var newPriceTarget = null;
            var newPrice = 0;
			var currentPrice = 0;
			var overrideReason = null;
            newPriceTarget = _scBaseUtils.getTargetModel(this, "NewPriceModel", null);
			var cpmodel = _scScreenUtils.getModel(this,"currentPriceModel");
			currentPrice = _scModelUtils.getNumberValueFromPath("LinePriceInfo.DisplayUnitPrice", cpmodel);
			overrideReason = _scBaseUtils.getTargetModel(this, "selectedOverrideReason", null);
			var sOverrideReason = null;
            sOverrideReason = _scModelUtils.getStringValueFromPath("overrideReason", overrideReason);
			var alpineModel = _scScreenUtils.getModel(this, "extn_Alpine_Validation_output");
            newPrice = _scModelUtils.getNumberValueFromPath("LinePriceInfo.DisplayUnitPrice", newPriceTarget);
			var diffCpNp = currentPrice - newPrice;
			//check for negative price change
            if (
            newPrice < 0) {
                var widget = "";
                widget = _scEventUtils.getOriginatingControlUId(
                bEvent);
                _scWidgetUtils.markFieldinError(
                this, widget, _scScreenUtils.getString(
                this, "InvalidPrice"), true);
			//alpine user price over check
            } else if (diffCpNp > 0 && !_scBaseUtils.isVoid(alpineModel)) 
			{
				var alpineValidationAmt = alpineModel.CommonCodeList.CommonCode[0].CodeShortDescription;
				if(diffCpNp > alpineValidationAmt)
				{
					var widget = "";
					widget = _scEventUtils.getOriginatingControlUId(bEvent);
					if(diffCpNp > alpineValidationAmt ){_scWidgetUtils.markFieldinError(this, widget, _scScreenUtils.getString(this, "Alpine Price Modification Error: New Price must not be less than $"+(currentPrice-alpineValidationAmt)), true);
					}
				}
			//set new price if correct 
			}else {
                _scWidgetUtils.clearFieldinError(
                this, "NewPriceText");
            }
        },
		//get hashmap for alpine resource permission
		getAlpineCC: function(
        event, bEvent, ctrl, args) 
		{
			if(_scResourcePermissionUtils.hasPermission("VSIALPINERESTR"))
			{
				var aplineValidation = _scModelUtils.createNewModelObjectWithRootKey("CommonCode");
				_isccsUIUtils.callApi(this, aplineValidation, "extn_AlpineValidation", null);			
			}
		},
		//set hasmaph source name
		 handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {  
			_scScreenUtils.setModel(this, "extn_Alpine_Validation_output", modelOutput, null);
		}
		
});
});

