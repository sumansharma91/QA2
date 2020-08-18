scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!ias/utils/UIUtils","scbase/loader!extn/components/shipment/summary/ShipmentRTExtnUI"]
,
function(            
                _dojodeclare,
                _scScreenUtils,
                _iasUIUtils,
                _extnShipmentRTExtnUI
){
    return _dojodeclare("extn.components.shipment.summary.ShipmentRTExtn", [_extnShipmentRTExtnUI],{
    // custom code here
    printPickPackReceipt: function(
    event, bEvent, controlName, args) {
        var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
            this, "getShipmentDetails_output");
        console.log("inputData",shipmentDetailsModel);
        var shipmentInput = {};
        shipmentInput.Shipment = {};
        shipmentInput.Shipment.ShipmentKey = shipmentDetailsModel.Shipment.ShipmentKey;
        _iasUIUtils.callApi(this, shipmentInput, "extn_printpickpackreceipt", null);
    },
    printCustomerReceipt: function(
    event, bEvent, controlName, args) {
        var shipmentDetailsModel = null;
            shipmentDetailsModel = _scScreenUtils.getModel(
            this, "getShipmentDetails_output");
        console.log("inputData",shipmentDetailsModel);
        var shipmentInput = {};
        shipmentInput.Shipment = {};
        shipmentInput.Shipment.ShipmentKey = shipmentDetailsModel.Shipment.ShipmentKey;   
        _iasUIUtils.callApi(this, shipmentInput, "extn_Print_Customer_Receipt", null);
    }});
});