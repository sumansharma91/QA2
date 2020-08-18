
scDefine(["scbase/loader!dojo/_base/declare",
		  "scbase/loader!extn/editors/CustomerEditorRTExtnUI",
		  "scbase/loader!isccs/utils/RelatedTaskUtils",
		  "scbase/loader!isccs/utils/UIUtils",
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
		  ]
,
function(_dojodeclare,
	     _extnCustomerEditorRTExtnUI,
		 _isccsRelatedTaskUtils,
		 _isccsUIUtils,
		 _scModelUtils,
		 _scScreenUtils,
		 _scWidgetUtils
		 
){ 
	return _dojodeclare("extn.editors.CustomerEditorRTExtn", [_extnCustomerEditorRTExtnUI],{
	// custom code here
	extn_init: function(){
		_scWidgetUtils.hideWidget(this, "lnk_RT_ManageBusiness");
		_scWidgetUtils.hideWidget(this, "lnk_RT_CreateBusiness");
		_scWidgetUtils.hideWidget(this, "lnk_RT_ManageConsumer");
		
	},
	lnk_RT_ManageConsumer_OnClickHandler: function(){
		var taskInput = null;
		var model = {};
		taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(this);	
		_isccsUIUtils.openWizardInEditor("isccs.customer.wizards.createConsumer.CreateConsumerWizard", taskInput, "isccs.editors.CustomerEditor", this);
	}
	 
});
});

