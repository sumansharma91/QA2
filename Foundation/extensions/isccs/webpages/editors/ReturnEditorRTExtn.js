
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/editors/ReturnEditorRTExtnUI",	"scbase/loader!isccs/utils/RelatedTaskUtils",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/ControllerUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!sc/plat/dojo/utils/WizardUtils",
		"scbase/loader!isccs/utils/BaseTemplateUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnEditorRTExtnUI,
	_isccsRelatedTaskUtils,
	_isccsUIUtils,
	_scControllerUtils,
	_scEditorUtils,
	_scScreenUtils,
	_scModelUtils,
		_scWidgetUtils,
		scScreen,
		_scWizardUtils,
			_isccsBaseTemplateUtils
){ 
	return _dojodeclare("extn.editors.ReturnEditorRTExtn", [_extnReturnEditorRTExtnUI],
	{
	
	 dataMigration: function() {
               // mCompleteOrdDetails = this.getModel("getCompleteOrderLineList_output");
				//console.log("return mCompleteOrdDetails: ", mCompleteOrdDetails);
				var taskInput = _isccsRelatedTaskUtils.getRelatedTaskInput(
				this);
				//console.log('I am here',taskInput.Order.OrderName);
				if(taskInput.Order.OrderName=='MigratedOrder')
				{
					//console.log('ddddd')
					_scWidgetUtils.disableWidget(this, "cancelReturn", true);
					_scWidgetUtils.disableWidget(this, "changeReturn", true);
					_scWidgetUtils.disableWidget(this, "viewAllInvoices_Return", true);
					//_isccsBaseTemplateUtils.showMessage(this, "extn_migrated_order_message", "warning", null);
				}
					
			}
	// custom code here
});
});

