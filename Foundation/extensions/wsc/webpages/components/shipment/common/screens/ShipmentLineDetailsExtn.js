
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!extn/components/shipment/common/screens/ShipmentLineDetailsExtnUI"]
,
function(			 
			    _dojodeclare,
			_scScreenUtils,
			_scBaseUtils,
			_scModelUtils,
			    _extnShipmentLineDetailsExtnUI
){ 
	return _dojodeclare("extn.components.shipment.common.screens.ShipmentLineDetailsExtn", [_extnShipmentLineDetailsExtnUI],{
		AliasValue: function(event, bEvent, ctrl, args)
		{
			var shipmentModel = null;
            shipmentModel = _scScreenUtils.getModel(this, "ShipmentLine");
            var itemAlias = null;
			itemAlias = _scModelUtils.getModelObjectFromPath("ShipmentLine.OrderLine.ItemDetails.ItemAliasList.ItemAlias", shipmentModel); 
           for (var length = 0; length < _scBaseUtils.getAttributeCount(itemAlias); length++) 
                {
					var name =_scModelUtils.getStringValueFromPath("AliasName", itemAlias[length]);
					if(_scBaseUtils.equals(name,"UPC"))
                    {
                        var AliasValue = _scModelUtils.getStringValueFromPath("AliasValue", itemAlias[length]);
                        return AliasValue;
                    }
                }
            }
	});
});
