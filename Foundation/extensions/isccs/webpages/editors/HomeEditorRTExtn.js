
scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/editors/HomeEditorRTExtnUI",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(_dojodeclare,
		 _extnHomeEditorRTExtnUI,
		 _scEditorUtils,
		 _scScreenUtils,
		 _isccsUIUtils,
		 _scWidgetUtils
){ 
	return _dojodeclare("extn.editors.HomeEditorRTExtn", [_extnHomeEditorRTExtnUI],{
	// custom code here
	extn_init: function(){
		_scWidgetUtils.hideWidget(this, "lnk_RT_CreateBusiness");
		
	},
	giftCardEmailScreen: function(){
		var bindings = null;
		var screenInput = null;
		screenInput = {};
		bindings = {};
		popupParams = {};
		popupParams["screenInput"] = screenInput;
		var dialogParams = null;
		dialogParams = {};
		dialogParams["closeCallBackHandler"] = "onGiftCardEmailSend";
		_isccsUIUtils.openSimplePopup("extn.home.GiftCardEmail", "Send Gift Card Email", this, popupParams, dialogParams);
	},
		
    onGiftCardEmailSend: function(actionPerformed, model, popupParams) {

	},
	//OMS-1148:start
	voucherLookupScreen: function(){
		var bindings = null;
		var screenInput = null;
		screenInput = {};
		bindings = {};
		popupParams = {};
		popupParams["screenInput"] = screenInput;
		var dialogParams = null;
		dialogParams = {};
		dialogParams["closeCallBackHandler"] = "onVoucherLookup";
		_isccsUIUtils.openSimplePopup("extn.home.VoucherLookupScreen", "Voucher Lookup", this, popupParams, dialogParams);
	},
	 onVoucherLookup: function(actionPerformed, model, popupParams) {

	},
	//OMS-1148:end
	extn_Return_Acknowledgement_onClickHandler: function(
        event, bEvent, ctrl, args) {
		
		
		
			
			var taskInput = null;
	        var currentEditor = _scEditorUtils.getCurrentEditor();
			taskInput = _scScreenUtils.getInitialInputData(currentEditor);
		    var rtScreen=_scScreenUtils.getChildScreen(currentEditor,"RelatedTaskScreenHolder"); 
			
			_isccsUIUtils.openWizardInEditor("extn.return.wizards.acknowledge.VSIAcknowledgeReturnWizard", taskInput, "extn.editors.AckReturnEditor", rtScreen);
		
			
		
		}
	
	
});
});

