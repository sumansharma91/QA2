
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!extn/components/shipment/customerpickup/PickupOrderLineDetailsExtnUI"]
,
function(			 
			    _dojodeclare,
			 _scBaseUtils,
			   _scModelUtils,
				_scScreenUtils,
			    _extnPickupOrderLineDetailsExtnUI
){ 
	return _dojodeclare("extn.components.shipment.customerpickup.PickupOrderLineDetailsExtn", [_extnPickupOrderLineDetailsExtnUI],{
	AliasValue2: function(event, bEvent, ctrl, args)
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
                        var AliasValue2 = _scModelUtils.getStringValueFromPath("AliasValue", itemAlias[length]);
                        return AliasValue2;
                    }
                }
            }
});
});

