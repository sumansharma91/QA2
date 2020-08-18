
scDefine([
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/order/details/OrderPricingSummaryExtnUI",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/PaginationUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!isccs/utils/BaseTemplateUtils"
]
,
function(			 
	_dojodeclare,
	_extnOrderPricingSummaryExtnUI,
	_isccsUIUtils,
	_scBaseUtils,
	_scModelUtils,
	_scPaginationUtils,
	_scScreenUtils,
	_scWidgetUtils,
	_isccsBaseTemplateUtils
){ 
	return _dojodeclare("extn.order.details.OrderPricingSummaryExtn", [_extnOrderPricingSummaryExtnUI],{
	
	openManageCharges: function(
        event, bEvent, ctrl, args) {
            var popupParams = null;
            var manageChargeDetails = null;
            manageChargeDetails = _scModelUtils.createNewModelObjectWithRootKey("Order");
            var dialogParams = null;
            dialogParams = {};
            popupParams = {};
			
			// Added to throw an error for confirmed orders with a payment type of CASH or CHECK : BEGIN
			var mCompleteOrdDetails = this.getModel("getCompleteOrderDetails_output");
			var bIsCashOrCheckPayment = false;
			var mPaymentMethods = _scModelUtils.getModelObjectFromPath("Order.PaymentMethods", mCompleteOrdDetails);
			if (!_scBaseUtils.isVoid(mPaymentMethods)
				&& !_scBaseUtils.equals(mCompleteOrdDetails.Order.DraftOrderFlag, "Y")) {
				var arrPaymentMethod = _scModelUtils.getModelListFromPath("PaymentMethod", mPaymentMethods);
				arrPaymentMethod.forEach(function (paymentMethod, i) {
					if (_scBaseUtils.equals(paymentMethod.PaymentType, "CASH")
						|| _scBaseUtils.equals(paymentMethod.PaymentType, "CHECK")) {
						bIsCashOrCheckPayment = true;
					}
				});
			}
			console.log("bIsCashOrCheckPayment", bIsCashOrCheckPayment);
			if (bIsCashOrCheckPayment) {
				_isccsBaseTemplateUtils.showMessage(this, "extn_cash_check_payment_mod_charges_error", "error", null);
			} else {
			// Added to throw an error for confirmed orders with a payment type of CASH or CHECK : END
				if (!(
				_scBaseUtils.isVoid(
				this.scEditorInput))) {
					var EnterpriseCode = null;
					EnterpriseCode = _scBaseUtils.getAttributeValue("Order.EnterpriseCode", false, this.scEditorInput);
					var OrderNo = null;
					OrderNo = _scBaseUtils.getAttributeValue("Order.OrderNo", false, this.scEditorInput);
					var DocumentType = null;
					DocumentType = _scBaseUtils.getAttributeValue("Order.DocumentType", false, this.scEditorInput);
					var OrderHeaderKey = null;
					OrderHeaderKey = _scBaseUtils.getAttributeValue("Order.OrderHeaderKey", false, this.scEditorInput);
				} else {
					EnterpriseCode = _scBaseUtils.getAttributeValue("Order.EnterpriseCode", false, this._initialInputData);
					OrderNo = _scBaseUtils.getAttributeValue("Order.OrderNo", false, this._initialInputData);
					DocumentType = _scBaseUtils.getAttributeValue("Order.DocumentType", false, this._initialInputData);
					OrderHeaderKey = _scBaseUtils.getAttributeValue("Order.OrderHeaderKey", false, this._initialInputData);
				}
				_scModelUtils.setStringValueAtModelPath("Order.DocumentType", DocumentType, manageChargeDetails);
				_scModelUtils.setStringValueAtModelPath("Order.OrderNo", OrderNo, manageChargeDetails);
				_scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", EnterpriseCode, manageChargeDetails);
				_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", OrderHeaderKey, manageChargeDetails);
				popupParams["screenInput"] = manageChargeDetails;
				var isCurrentScreenDirty = null;
				isCurrentScreenDirty = _scScreenUtils.isDirty(
				this, null, true);
				var isParentScreenDirty = null;
				isParentScreenDirty = _scBaseUtils.getAttributeValue("popupParams.isCurrentScreenDirty", false, this.popupInstance);
				popupParams["isCurrentScreenDirty"] = isCurrentScreenDirty;
				popupParams["isParentScreenDirty"] = isParentScreenDirty;
				_isccsUIUtils.openSimplePopup("isccs.order.addModifyCharges.AddModifyChargesPopup", "AddManageCharges", this, popupParams, dialogParams);
				// Added for JIRA SU-53 to refresh screen after header charges are pro-rated to the lines
				this.handleReloadScreen();
			}
        },
		handleReloadScreen: function() {
            var inputGetCompleteOrderDetails = null;
            var inputGetCompleteOrderLineList = null;
            var mashupContextBean = null;
            var inputModelList = null;
            var mashupRefIdList = null;
            inputModelList = [];
            mashupRefIdList = [];
            inputGetCompleteOrderDetails = _scScreenUtils.getTargetModel(
            this, "getCompleteOrderDetails_input");
            inputGetCompleteOrderLineList = _scScreenUtils.getTargetModel(
            this, "getCompleteOrderLineList_input");
            mashupRefIdList.push("getCompleteOrderDetails");
            mashupRefIdList.push("getCompleteOrderLineList");
            inputModelList.push(
            inputGetCompleteOrderDetails);
            inputModelList.push(
            inputGetCompleteOrderLineList);
            var childScreen = null;
            childScreen = _scScreenUtils.getChildScreen(
            this, "orderPricingSummaryLines");
            mashupContextBean = _scPaginationUtils.getMashupContextForPaginatedBehaviorCall(
            childScreen, "OLST_listGrid");
            _isccsUIUtils.callApis(
            this, inputModelList, mashupRefIdList, mashupContextBean, null);
		},
	checkMigratedOrderFlag: function(event, bEvent, ctrl, args) {
		mCompleteOrdDetails = this.getModel("getCompleteOrderDetails_output");
		if (mCompleteOrdDetails.Order.Extn.ExtnIsMigrated === "Y") {
			var lnkManageCharge = this.getWidgetByUId("lnkManageCharge");
			_scWidgetUtils.disableWidget(this, "lnkLineManageCharge", true);
			_scWidgetUtils.disableWidget(this, "lnkManageCharge", true);
		}
	},
	openLineManageCharges: function(
	event, bEvent, ctrl, args) {
		
		var popupParams = null;
		var dialogParams = null;
		var screenInput = null;
		var adjustmentModel = null;
		popupParams = {};
		dialogParams = {};
		screenInput = {};
		
		// Added to throw an error for confirmed orders with a payment type of CASH or CHECK : BEGIN
		var mCompleteOrdDetails = this.getModel("getCompleteOrderDetails_output");
		var bIsCashOrCheckPayment = false;
		var mPaymentMethods = _scModelUtils.getModelObjectFromPath("Order.PaymentMethods", mCompleteOrdDetails);
		if (!_scBaseUtils.isVoid(mPaymentMethods)
			&& !_scBaseUtils.equals(mCompleteOrdDetails.Order.DraftOrderFlag, "Y")) {
			var arrPaymentMethod = _scModelUtils.getModelListFromPath("PaymentMethod", mPaymentMethods);
			arrPaymentMethod.forEach(function (paymentMethod, i) {
				if (_scBaseUtils.equals(paymentMethod.PaymentType, "CASH")
					|| _scBaseUtils.equals(paymentMethod.PaymentType, "CHECK")) {
					bIsCashOrCheckPayment = true;
				}
			});
		}
		if (bIsCashOrCheckPayment) {
			_isccsBaseTemplateUtils.showMessage(this, "extn_cash_check_payment_mod_charges_error", "error", null);
		} else {
		// Added to throw an error for confirmed orders with a payment type of CASH or CHECK : END
		
			adjustmentModel = _scScreenUtils.getModel(
			this, "adjustments_output");
			console.log("*** adjustmentModel ***", adjustmentModel);
			_scModelUtils.addStringValueToModelObject("LineMode", "Y", screenInput);
			var manageChargeDetails = null;
			manageChargeDetails = _scScreenUtils.getInitialInputData(
			this);
			console.log("*** manageChargeDetails ***", manageChargeDetails);
			var OrderHeaderKey = null;
			OrderHeaderKey = _scModelUtils.getStringValueFromPath("Order.OrderHeaderKey", manageChargeDetails);
			var EnterpriseCode = null;
			EnterpriseCode = _scModelUtils.getStringValueFromPath("Order.EnterpriseCode", manageChargeDetails);
			var OrderLineKey = null;
			OrderLineKey = _scModelUtils.getStringValueFromPath("OrderLine.OrderLineKey", adjustmentModel);
			var DocumentType = null;
			DocumentType = _scModelUtils.getStringValueFromPath("Order.DocumentType", manageChargeDetails);
			_scModelUtils.setStringValueAtModelPath("Order.OrderHeaderKey", OrderHeaderKey, screenInput);
			_scModelUtils.setStringValueAtModelPath("Order.EnterpriseCode", EnterpriseCode, screenInput);
			_scModelUtils.setStringValueAtModelPath("Order.OrderLines.OrderLine.OrderLineKey", OrderLineKey, screenInput);
			_scModelUtils.setStringValueAtModelPath("Order.DocumentType", DocumentType, screenInput);
			popupParams["screenInput"] = screenInput;
			popupParams["outputNamespace"] = "targetModel";
			var isCurrentScreenDirty = null;
			isCurrentScreenDirty = _scScreenUtils.isDirty(
			this, null, true);
			var isParentScreenDirty = null;
			isParentScreenDirty = _scBaseUtils.getAttributeValue("popupParams.isCurrentScreenDirty", false, this.popupInstance);
			popupParams["isCurrentScreenDirty"] = isCurrentScreenDirty;
			popupParams["isParentScreenDirty"] = isParentScreenDirty;
			_isccsUIUtils.openSimplePopup("isccs.order.addModifyCharges.AddModifyChargesPopup", "AddManageCharges", this, popupParams, dialogParams);
		}
	},
	checkModeAndStatus: function(
	orderModel) {
		console.log("orderModel: ", orderModel);
		var HeaderMode = null;
		var LineMode = null;
		var Root = null;
		var LineCharge = null;
		var HeaderCharge = null;
		var adjustmentModel = null;
		Root = _isccsUIUtils.getRootName(
		orderModel);
		HeaderCharge = _scModelUtils.getModelObjectFromPath("Order.HeaderCharges.HeaderCharge", orderModel);
		// ARE-124 : Modified to set HeaderMode or LineMode whether charges are present or not : BEGIN
		/* if (
		_scBaseUtils.isVoid(
		HeaderCharge) && _scBaseUtils.equals(
		Root, "Order")) {
			HeaderMode = "Y";
		} */
		if (_scBaseUtils.equals(Root, "Order")) {
			HeaderMode = "Y";
		}
		LineCharge = _scModelUtils.getModelObjectFromPath("OrderLine.LineCharges.LineCharge", orderModel);
		Root = _isccsUIUtils.getRootName(
		orderModel);
		/* if (
		_scBaseUtils.isVoid(
		LineCharge) && _scBaseUtils.equals(
		Root, "OrderLine")) {
			LineMode = "Y";
		} */
		if (_scBaseUtils.equals(Root, "OrderLine")) {
			LineMode = "Y";
		}
		// ARE-124 : Modified to set HeaderMode or LineMode whether charges are present or not : END
		this.checkStatusModification(
		HeaderMode, LineMode);
		adjustmentModel = _scScreenUtils.getModel(
		this, "adjustments_output");
	},
	checkStatusModification: function(
	HeaderMode, LineMode) {
		console.log("****** checkStatusModification ******");
		var inputModel = null;
		var ModificationAllowed = null;
		var Modification = null;
		var ModificationObject = null;
		console.log("HeaderMode: ", HeaderMode);
		console.log("LineMode: ", LineMode);
		if (
		_scBaseUtils.equals(
		LineMode, "Y")) {
			console.log("LineMode");
			inputModel = _scScreenUtils.getModel(
			this, "adjustments_output");
			Modification = _scModelUtils.getModelListFromPath("OrderLine.Modifications.Modification", inputModel);
			ModificationObject = Modification[0];
			ModificationAllowed = _scBaseUtils.getAttributeValue("ModificationAllowed", false, ModificationObject);
		} else if (
		_scBaseUtils.equals(
		HeaderMode, "Y")) {
			console.log("HeaderMode");
			inputModel = _scScreenUtils.getModel(
			this, "getCompleteOrderDetails_output");
			Modification = _scModelUtils.getModelListFromPath("Order.Modifications.Modification", inputModel);
			if (!(
			_scBaseUtils.isVoid(
			Modification))) {
				ModificationObject = Modification[
				0];
				ModificationAllowed = _scBaseUtils.getAttributeValue("ModificationAllowed", false, ModificationObject);
			}
		}
		console.log("ModificationAllowed: ", ModificationAllowed);
		if (
		_scBaseUtils.equals(
		ModificationAllowed, "N") || _scBaseUtils.equals(
		ModificationAllowed, "") || _scBaseUtils.isVoid(ModificationAllowed)) {
			console.log("entered");
			if (
			_scBaseUtils.equals(
			LineMode, "Y")) {
				console.log("Disable line");
				_scWidgetUtils.disableWidget(
				this, "lnkLineManageCharge", false);
			} 
			// ARE-124 : Commented to correctly disable both links when modification is disallowed at line and header level : BEGIN
			/* else {
				
				console.log("Enable line");
				_scWidgetUtils.enableWidget(
				this, "lnkLineManageCharge");
			} */
			// ARE-124 : Commented to correctly disable both links when modification is disallowed at line and header level : END
			if (
			_scBaseUtils.equals(
			HeaderMode, "Y")) {
				console.log("Disable header");
				_scWidgetUtils.disableWidget(
				this, "lnkManageCharge", false);
			}
			// ARE-124 : Commented to correctly disable both links when modification is disallowed at line and header level : BEGIN
			/* else {
				
				console.log("Enable header");
				_scWidgetUtils.enableWidget(
				this, "lnkManageCharge");
			} */
			// ARE-124 : Commented to correctly disable both links when modification is disallowed at line and header level : END
		}
		// ARE-124 : Added to enable link when modification is allowed at line or header level : BEGIN
		else if (_scBaseUtils.equals(ModificationAllowed, "Y")){
			if (
			_scBaseUtils.equals(
			LineMode, "Y")) {
				console.log("Enable line");
				_scWidgetUtils.enableWidget(
				this, "lnkLineManageCharge", false);
			} else if (
			_scBaseUtils.equals(
			HeaderMode, "Y")) {
				console.log("Enable header");
				_scWidgetUtils.enableWidget(
				this, "lnkManageCharge", false);
			}
		}
		// ARE-124 : Added to enable link when modification is allowed at line or header level : END
	}
});
});

