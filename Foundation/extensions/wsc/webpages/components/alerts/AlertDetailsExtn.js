
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!extn/components/alerts/AlertDetailsExtnUI"]
,
function(			 
			    _dojodeclare,
			   _scBaseUtils,
			   _scModelUtils,
				_scScreenUtils,
			    _extnAlertDetailsExtnUI
){ 
	return _dojodeclare("extn.components.alerts.AlertDetailsExtn", [_extnAlertDetailsExtnUI],{
	ItemID: function(event, bEvent, ctrl, args)
		{
			var inboxModel = null;
            inboxModel = _scScreenUtils.getModel(this, "Inbox");
            var inboxReference = null;
			inboxReference = _scModelUtils.getModelObjectFromPath("Inbox.InboxReferencesList.InboxReferences", inboxModel); 
           for (var length = 0; length < _scBaseUtils.getAttributeCount(inboxReference); length++) 
                {
					console.log("length => "+ length);
                    var name =_scModelUtils.getStringValueFromPath("Name", inboxReference[length]);
					console.log("name => "+ name);
                    if(_scBaseUtils.equals(name,"ItemID"))
                    {
                        var ItemID = _scModelUtils.getStringValueFromPath("Value", inboxReference[length]);
                        console.log("itemId => "+ ItemID);
						return ItemID;
                    }
                }
            },
		Quantity: function(event, bEvent, ctrl, args)
		{
			var inboxModel = null;
            inboxModel = _scScreenUtils.getModel(this, "Inbox");
            var inboxReference = null;
			inboxReference = _scModelUtils.getModelObjectFromPath("Inbox.InboxReferencesList.InboxReferences", inboxModel); 
           for (var length = 0; length < _scBaseUtils.getAttributeCount(inboxReference); length++) 
                {
			var name =_scModelUtils.getStringValueFromPath("Name", inboxReference[length]);
			if(_scBaseUtils.equals(name,"Qty"))
                    {
                        var Quantity= _scModelUtils.getStringValueFromPath("Value", inboxReference[length]);
                        console.log("Qty=> "+ Quantity);
						return Quantity;
                    }
                }
            }
});
});