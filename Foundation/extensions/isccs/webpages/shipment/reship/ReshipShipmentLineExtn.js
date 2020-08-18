
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils",  "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/binding/ComboDataBinder",  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils",  "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils",  "scbase/loader!isccs/utils/ModelUtils", "scbase/loader!extn/shipment/reship/ReshipShipmentLineExtnUI"]
,
function(			 
			    _dojodeclare
			 ,
				_scScreenUtils
			,	
				_scBaseUtils
			,
				_scComboDataBinder
			,
				_isccsUIUtils
			,
				_scWidgetUtils
			,
				_isccsBaseTemplateUtils
			,
				_scModelUtils
			,
				_isccsModelUtils
			,
			    _extnReshipShipmentLineExtnUI
){ 
	return _dojodeclare("extn.shipment.reship.ReshipShipmentLineExtn", [_extnReshipShipmentLineExtnUI],{
	// custom code here
	
	 initializeScreen: function(
        event, bEvent, ctrl, args) {
    		
    		var inputModel = _scScreenUtils.getInitialInputData(
                    this);
            _scBaseUtils.setModel(this, "screenInput", inputModel, null);
            var inputData = _scScreenUtils.getModel(
                    this, "screenInput");

			var rOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", inputModel);
			var rOrderLineKey = _scModelUtils.getStringValueFromPath("Order.OrderLine.OrderLineKey", inputModel);
			//OMS-1005 : Start//
			var rPersonInfoShipTo = _scModelUtils.getStringValueFromPath("Order.OrderLine.PersonInfoShipTo", inputModel);
			//OMS-1005 : End//
			var carrierInputData = {};
			
			_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", rOrderHeaderKey, carrierInputData);
			_scModelUtils.setStringValueAtModelPath("Order.OrderLines.OrderLine.OrderLineKey", rOrderLineKey, carrierInputData);
			//OMS-1005 : Start//
			_scModelUtils.setStringValueAtModelPath("Order.OrderLines.OrderLine.PersonInfoShipTo", rPersonInfoShipTo, carrierInputData);
			//OMS-1005 : End//
			_isccsUIUtils.callApi(this, carrierInputData, "extn_getCarrierServiceOptionsForOrdering", null);
					
        } ,
        handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getCarrierServiceOptionsForOrdering")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
				var tempModel = _scModelUtils.createNewModelObjectWithRootKey("CarrierServiceList");
				var lolService = modelOutput.CarrierServiceList;
				tempModel.CarrierServiceList = lolService;
				
                    _scScreenUtils.setModel(
                    this, "extn_getCarrierServiceOptionsForOrdering_output", tempModel, null);
					
					var screenModel = _scModelUtils.createNewModelObjectWithRootKey("CarrierService");
				
					screenModel.CarrierService = modelOutput.CarrierServiceList.CarrierService;
				
					_scScreenUtils.setModel(
                    this, "extn_levelOfService_output", screenModel, null);
					
					
					
                }
            }
			
			if (
            _scBaseUtils.equals(
            mashupRefId, "validateReship")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "validateReship_output", modelOutput, null);
                }
                this.handleValidateReshipMashupOutput(
                mashupInput, modelOutput);
            }
            if (
            _scBaseUtils.equals(
            mashupRefId, "reshipOrderLines")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "reshipOrderLines_output", modelOutput, null);
                }
                this.handleReshipOrderLinesMashupOutput(
                        mashupInput, modelOutput);
                //this.callChangeReshipOrderLineCarrier(mashupInput, modelOutput);
            }
			
			if (
            _scBaseUtils.equals(
            mashupRefId, "extn_changeCarrieronReshipOrderLines")) {
                if (!(
                _scBaseUtils.equals(
                false, applySetModel))) {
                    _scScreenUtils.setModel(
                    this, "extn_changeOrderLineCarrier_output", modelOutput, null);
                }
                this.handleReshipOrderLinesMashupOutput(
                mashupInput, modelOutput);
            }
			
			},
			
			callChangeReshipOrderLineCarrier: function(mashupInput, modelOutput){
        	var changereshipOrderLinesInput = null;
			
			changereshipOrderLinesInput = _scScreenUtils.getTargetModel(
			this, "extn_changeOrderLineCarrier_input", null);
			
			if (!_scBaseUtils.isVoid(changereshipOrderLinesInput)) {
			
			var length = modelOutput.Order.OrderLines.OrderLine.length;
			var rOrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", modelOutput);
			
			_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", rOrderHeaderKey, changereshipOrderLinesInput);
			_scModelUtils.setStringValueAtModelPath("Order.OrderLines.OrderLine.PrimeLineNo", length, changereshipOrderLinesInput);
			_scModelUtils.setStringValueAtModelPath("Order.OrderLines.OrderLine.SubLineNo", "1", changereshipOrderLinesInput);
			
			_isccsUIUtils.callApi(this, changereshipOrderLinesInput, "extn_changeCarrieronReshipOrderLines", null);
			
			} else {
			
			this.handleReshipOrderLinesMashupOutput(mashupInput, modelOutput);
			
			}
			
			} ,
			setInitialized: function( event, bEvent, ctrl, args) {
			
			
            this.isScreeninitialized = true;
			
			
			
			
			},
			showReshipReasonCode: function(){
				
				var displayedValue = this.getWidgetByUId("cmb_reshipReason").displayedValue; 
				
				
				var trackingCommonCodeModel =  _scScreenUtils.getModel(this, "extn_getTrackingNo_CommonCode");

			var commonCode =  _scBaseUtils.getValueFromPath("CommonCodeList.CommonCode", trackingCommonCodeModel);

			for(i=0; i< commonCode.length; i++){
				var codeValue = _scBaseUtils.getValueFromPath("CodeValue", commonCode[i]);
					if(_scBaseUtils.equals(displayedValue,codeValue)){
						_scWidgetUtils.showWidget(
						this, "extn_textfield_TrackingNumber", false);
						_scWidgetUtils.setWidgetMandatory(this,"extn_textfield_TrackingNumber");
						break;
					}
					else{
						_scWidgetUtils.hideWidget(
						this, "extn_textfield_TrackingNumber", false);
						_scWidgetUtils.setWidgetNonMandatory(this,"extn_textfield_TrackingNumber");
					}
				
			}



			
			}
});
});

