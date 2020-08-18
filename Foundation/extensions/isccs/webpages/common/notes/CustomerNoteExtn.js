
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/common/notes/CustomerNoteExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
"scbase/loader!isccs/utils/UIUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnCustomerNoteExtnUI, _scBaseUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,_isccsUIUtils
){ 
	return _dojodeclare("extn.common.notes.CustomerNoteExtn", [_extnCustomerNoteExtnUI],{
		handleAfterScreenInit: function(
        event, bEvent, ctrl, args) {
            if (
            _scBaseUtils.isEmptyArray(
            _scModelUtils.getModelListFromPath("CommonCodeList.CommonCode", _scScreenUtils.getModel(
            this, "getNoteCustomerSatisfactionList_output")))) {
                _scWidgetUtils.hideWidget(
                this, "cmbCustomerSatisfaction", false);
            }
            var screenInput = null;
            screenInput = _scScreenUtils.getInitialInputData(
            this);
            console.log("screenInput",screenInput);
            if (!(
            _scBaseUtils.isVoid(
            screenInput))) {
                _scScreenUtils.setModel(
                this, "Note", screenInput, null);
            }
        },
		onApply: function(
        event, bEvent, ctrl, args) {
			var manageCustomer_input = null;
			var noteModel = _scScreenUtils.getModel(this, "Note");
			console.log("noteModel",noteModel);
			var popup_Noteinput = null;
			popup_Noteinput = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null);
			manageCustomer_input = _scModelUtils.createNewModelObjectWithRootKey("Customer");
			_scModelUtils.addModelToModelPath("Customer",popup_Noteinput, manageCustomer_input); 
			_scModelUtils.setStringValueAtModelPath("Customer.HasCustomerRemark","Y",manageCustomer_input);
			_scModelUtils.setStringValueAtModelPath("Customer.ExcludeCustomer","Y",manageCustomer_input);
			_scModelUtils.setStringValueAtModelPath("Customer.NotesRecordState","Modified",manageCustomer_input);
			_scModelUtils.setStringValueAtModelPath("Customer.Note.NoteID",noteModel.Note.NoteID,manageCustomer_input);
			
			console.log("popup_Noteinput",popup_Noteinput);
			
            _scScreenUtils.setPopupOutput(this,popup_Noteinput);
            _scWidgetUtils.closePopup(this, "APPLY", false);
        }
});
});

