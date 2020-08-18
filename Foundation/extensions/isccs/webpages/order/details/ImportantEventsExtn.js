
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/order/details/ImportantEventsExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		  "scbase/loader!isccs/utils/UIUtils"]
,
function(			 
			    _dojodeclare,
			    _extnImportantEventsExtnUI,
				_scBaseUtils,
				_scModelUtils, 
				_scScreenUtils,
				_scWidgetUtils,
				_isccsUIUtils
){ 
	return _dojodeclare("extn.order.details.ImportantEventsExtn", [_extnImportantEventsExtnUI],{
	// custom code here
        getScreenMode: function() {
            var returnValue = "default";
            var namespaceDataList = null;
            namespaceDataList = _scBaseUtils.getListFromBean("namespaceData", this.namespaceData);
			
console.log("namespaceData = ", this.namespaceData);

console.log("namespaceDataList = ", namespaceDataList);
            var dataObj = null;
            dataObj = namespaceDataList[0];
            var importantEventModel = null;
            importantEventModel = _scBaseUtils.getModelValueFromBean("dataObj", dataObj);
console.log("importantEventModel = ", importantEventModel);

            var sType = null;
            sType = _scModelUtils.getStringValueFromPath("Type", importantEventModel);
console.log("sType = ", sType);

            if (_scBaseUtils.equals(sType, "Reshipped") || _scBaseUtils.equals(sType, "StopDelivery")) {
                returnValue = "QUANTITY";
            } else if (
            _scBaseUtils.equals(sType, "Cancel") || _scBaseUtils.equals(sType, "Return")) {
                returnValue = "QUANTITY_REASON";
            }
            return returnValue;
        },
		
		
        initializeScreen: function(event, bEvent, ctrl, args) {
            var importantEventModel = null;
            importantEventModel = _scScreenUtils.getModel(this, "ImportantEvent");
            var sType = null;
            sType = _scModelUtils.getStringValueFromPath("Type", importantEventModel);
            var sClass = null;
            sClass = _scBaseUtils.stringConcat("icon-", sType);
            var cssClassToAdd = null;
            cssClassToAdd = [];
            cssClassToAdd.push(sClass);
            _scWidgetUtils.addClass(this, "imgImportantEvent", cssClassToAdd);
        },
		
		
        getEventMessage: function(dataValue, screen, widget, namespace, modelObject, options) {
            var returnValue = null;
            var importantEventModel = null;
            importantEventModel = _scScreenUtils.getModel(this, "ImportantEvent");
            var sType = null;
            sType = _scModelUtils.getStringValueFromPath("Type", importantEventModel);
            var sBundleEntry = null;
            sBundleEntry = _scBaseUtils.stringConcat("ImportantEvents_", sType);
            returnValue = _scScreenUtils.getString(screen, sBundleEntry);
            return returnValue;
        },

			extn_init: function() {
				var orderLineModel = null;
				var parentScreen = _isccsUIUtils.getParentScreen(this,true);
				orderLineModel = _scScreenUtils.getModel(parentScreen, "getCompleteOrderLineDetails_output");		
				if(_scBaseUtils.equals("Released",_scModelUtils.getStringValueFromPath("OrderLine.Status", orderLineModel)) || _scBaseUtils.equals("Partially Released",_scModelUtils.getStringValueFromPath("OrderLine.Status", orderLineModel))){
				var OrderStatus = _scModelUtils.getStringValueFromPath("OrderLine.OrderStatuses.OrderStatus", orderLineModel);				
				var statusDate = _scModelUtils.getStringValueFromPath("StatusDate", OrderStatus[0]);
				var statusQuantity = _scModelUtils.getStringValueFromPath("StatusQty", OrderStatus[0])
 				var displayDate = this.getDate(statusDate);
					_scWidgetUtils.showWidget(this, "extn_contentpane_Release_Acknowledge", false);
					_scWidgetUtils.setValue(this, "extn_label_Date", displayDate , true );
					_scWidgetUtils.setValue(this, "extn_datalabel_Quantity", statusQuantity , true );
				}
				
			 
			},
			getDate: function(date){
//alert("getDate");		
				var localdate = new Date(date);

				var date1 = localdate.toLocaleDateString();
				

				var Minutes = localdate.getMinutes();
				if (Minutes < 10) {
					Minutes = '0' + Minutes;
				}
				
				var Seconds = localdate.getSeconds();
				if(Seconds < 10){
					 Seconds = '0' + Seconds;
				}
				
				var Hour = localdate.getHours();
				if (Hour < 10) {
					Hour = '0' + Hour;
				} //end if
				
				var Time = Hour + ':' + Minutes  +':'+ Seconds;
				return date1 +" "+Time;
			} 
		 
		
});
});

