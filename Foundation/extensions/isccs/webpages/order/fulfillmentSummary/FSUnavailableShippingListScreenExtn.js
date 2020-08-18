
scDefine([
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/order/fulfillmentSummary/FSUnavailableShippingListScreenExtnUI",
		"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!isccs/utils/OrderUtils",
		"scbase/loader!sc/plat/dojo/utils/EventUtils",
		"scbase/loader!dijit",
		"scbase/loader!sc/plat/dojo/utils/GridxUtils",
		"scbase/loader!isccs/utils/UIUtils"]
,
function(			 
		_dojodeclare,
		_extnFSUnavailableShippingListScreenExtnUI,
		_scWidgetUtils,
		_scScreenUtils,
		_scModelUtils,
		_scBaseUtils,
		_isccsOrderUtils,
		_scEventUtils,
		_dijit,
		_scGridxUtils,
		_isccsUIUtils
		
){ 
	return _dojodeclare("extn.order.fulfillmentSummary.FSUnavailableShippingListScreenExtn", [_extnFSUnavailableShippingListScreenExtnUI],{
	// custom code here
	
	selectAllRows: function(){
		var grid = this.getWidgetByUId("OLST_listGrid");
		
		_scGridxUtils.selectAllRowsUsingUId(this,"OLST_listGrid");
		//this.checkOrderStatusforAddressChange();
	},
    updateShippingAddress: function(
            argsModel) {
                var selectedSourceRecordsList = null;
                selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
                this, "OLST_listGrid");
                var selectedTargetRecordsList = null;
                selectedTargetRecordsList = _scGridxUtils.getSelectedTargetRecordsWithRowIndex(
                this, "OLST_listGrid");
                var listOfMergedModel = null;
                listOfMergedModel = this.mergeModel(
                selectedSourceRecordsList, selectedTargetRecordsList);
                var newModelData = null;
                newModelData = {};
                _scModelUtils.addModelToModelPath("PersonInfoShipTo", _scModelUtils.getModelObjectFromPath("Order.PersonInfoShipTo", argsModel), newModelData);
                var orderLineKeyList = null;
                orderLineKeyList = _scModelUtils.getModelListFromPath("Order.OrderLineKeyList", argsModel);
                var lengthOfSourceRecord = null;
                lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
                listOfMergedModel);
                for (
                var z = 0;
                z < lengthOfSourceRecord;
                z = z + 1) {
                    var beanItem = null;
                    beanItem = listOfMergedModel[
                    z];
                    var lineIndex = null;
                    lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                    var srcRowData = null;
                    srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(
                    this, "OLST_listGrid", lineIndex);
                    var rowJson = null;
                    rowJson = _scGridxUtils.getRowJsonFromRowIndex(
                    this, "OLST_listGrid", lineIndex);
                    var orderLineKey = null;
                    orderLineKey = _scModelUtils.getStringValueFromPath("OrderLineKey", rowJson);
                    var isInArray = false;
                    var isAnyRowUpdated = false;
                    isInArray = this.isInArray(
                    orderLineKey, orderLineKeyList);
                    if (
                    isInArray) {
                        _scGridxUtils.updateRecordToGridUsingUId(
                        this, "OLST_listGrid", srcRowData, newModelData, true, true);
                        isAnyRowUpdated = true;
                    }
                }
                if (
                isAnyRowUpdated) {
                    var temp = null;
                    temp = {};
                    _scEventUtils.fireEventToParent(
                    this, "ScreenChanged", temp);
                    _scEventUtils.fireEventToParent(
                    this, "SaveOrder", temp);
                }
               // _scGridxUtils.deselectAllRowsInGridUsingUId(
               // this, "OLST_listGrid");
            },
        	checkOrderStatusforAddressChange: function()
        	{
        		var model = _scScreenUtils.getModel(this, "getFulfillmentSummaryDetails_output");
        		var orderLine = _scModelUtils.getStringValueFromPath("Page.Output.OrderLines.OrderLine", model);
        		var flag = false;
        		for(i=0;i<orderLine.length;i++){
        			var orderStatus = _scModelUtils.getStringValueFromPath("OrderStatuses.OrderStatus", orderLine[i]);		
        			if(_scBaseUtils.isVoid(orderStatus))
        			{
        				return;
        			}
        			for(j=0;j<orderStatus.length;j++){				
        				var status = _scModelUtils.getNumberValueFromPath("Status", orderStatus[j]);
        				//console.log('ss',status);
        				var b = 3200;
        				if (parseFloat(status.toFixed(2)) > parseFloat(b.toFixed(2)))
        				 {				
        					flag = true;
        					_scWidgetUtils.disableWidget(this, "changeAddress", true);
        					_scWidgetUtils.disableWidget(this, "changeLOS", true);
        					break;
        				}
        			}
        			if(flag){
        				break;
        			}
        			
        		} 
        	},
            updateDeliveryMethodDetails: function(
                    newModelData) {
                        var selectedRecordsList = null;
                        selectedRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
                        this, "OLST_listGrid");
                        var noOfOrderLine = null;
                        noOfOrderLine = _scBaseUtils.getAttributeCount(
                        selectedRecordsList);
                        for (
                        var i = 0;
                        i < noOfOrderLine;
                        i = i + 1) {
                            var beanItem = null;
                            beanItem = selectedRecordsList[
                            i];
                            var lineIndex = null;
                            lineIndex = _scBaseUtils.getNumberValueFromBean("rowIndex", beanItem);
                            var srcRowData = null;
                            srcRowData = _scGridxUtils.getItemFromRowIndexUsingUId(
                            this, "OLST_listGrid", lineIndex);
                            _scGridxUtils.updateRecordToGridUsingUId(
                            this, "OLST_listGrid", srcRowData, newModelData, true, true);
                            var temp = null;
                            temp = {};
                            _scEventUtils.fireEventToParent(
                            this, "SaveOrder", temp);
                        }
                       // _scGridxUtils.deselectAllRowsInGridUsingUId(
                      //  this, "OLST_listGrid");
                    },
	 onRowDeselect: function(
		        event, bEvent, ctrl, args) {
		            var deselectedRow = null;
		            deselectedRow = _scBaseUtils.getBeanValueFromBean("rowIndex", args);
		            var argument = null;
		            argument = {};
		            var orderLineKey = null;
		            orderLineKey = _scBaseUtils.getStringValueFromBean("OrderLineKey", deselectedRow);
		            _scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
		            _scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
		            //_scEventUtils.fireEventToParent(
		           // this, "SelectOrUnselectRowInShippingPanel", argument);
		            var warningString = null;
        			warningString = _scScreenUtils.getString(
        			this, "WARNING_selecteOneItem");
        			var textObj = null;
        			textObj = {};
        			var textOK = null;
        			textOK = _scScreenUtils.getString(
        			this, "OK");
        			textObj["OK"] = textOK;
		            _scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRow);
					var errorMessage = _scScreenUtils.getString(this, "extn_Row_Deselected_Msg_Shipping");
        			_scScreenUtils.showErrorMessageBox(
        				this, errorMessage, "waringCallback", textObj, null);
		        },
        onRowOrHeaderDeselect: function(
        event, bEvent, ctrl, args) {
            var argument = null;
            argument = {};
            var selectedSourceRecordsList = null;
            selectedSourceRecordsList = _scGridxUtils.getSelectedSourceRecordsWithRowIndex(
            this, "OLST_listGrid");
            var lengthOfSourceRecord = null;
            lengthOfSourceRecord = _scBaseUtils.getAttributeCount(
            selectedSourceRecordsList);
            if (
            _scBaseUtils.equals(
            lengthOfSourceRecord, 0)) {
                _scWidgetUtils.enableWidget(
                this, "changeAddress");
                _scWidgetUtils.enableWidget(
                    this, "changeToDelivery");
                _scWidgetUtils.enableWidget(
                this, "changeToPickup");
            } else {
                this.validateSelectedRecordsForPermission(
                selectedSourceRecordsList, lengthOfSourceRecord);
            }
            var deselectedRowsList = null;
            deselectedRowsList = _scBaseUtils.getListFromBean("deselectedRows", args);
            var rowCount = 0;
            rowCount = _scBaseUtils.getAttributeCount(
            deselectedRowsList);
            for (
            var i = 0;
            i < rowCount;
            i = i + 1) {
                var deselectedRowData = null;
                deselectedRowData = deselectedRowsList[
                i];
                /* var rowData = null;
                rowData = _scBaseUtils.getAttributeValue("rowData", false, deselectedRowData);
                var orderLineKey = null;
                orderLineKey = _scModelUtils.getStringValueFromKey("OrderLineKey", rowData);
                _scBaseUtils.setAttributeValue("argumentList.OrderLineKey", orderLineKey, argument);
                _scBaseUtils.setAttributeValue("argumentList.Select", "N", argument);
                _scEventUtils.fireEventToParent(
                this, "SelectOrUnselectRowInShippingPanel", argument); */
				_scGridxUtils.selectRowUsingUId(this,"OLST_listGrid",deselectedRowData.rowIndex);
				if (i == 0) {
					var errorMessage = _scScreenUtils.getString(this, "extn_Header_Row_Deselected_Msg");
					_scScreenUtils.showErrorMessageBox(this, errorMessage, "error",null,null);
				}
            }
        }
});
});

