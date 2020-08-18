
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/common/notes/CustomerNoteDisplayExtnUI", "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/SearchUtils",
"scbase/loader!isccs/utils/BaseTemplateUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnCustomerNoteDisplayExtnUI, _isccsUIUtils, _scBaseUtils, _scEventUtils, _scModelUtils, _scScreenUtils, _scWidgetUtils,_isccsSearchUtils,_isccsBaseTemplateUtils  
){ 
	return _dojodeclare("extn.common.notes.CustomerNoteDisplayExtn", [_extnCustomerNoteDisplayExtnUI],{
	// custom code here
	callGetNoteList: function(
	event, bEvent, controlName, args) {
		var inputData = null;
		inputData = _scScreenUtils.getModel(
		this, "screenInput");
		console.log("inputData",inputData);
		var noteInput = {};
		noteInput.Customer = {};
		noteInput.Customer.CustomerID = inputData.Note.CustomerID;
		noteInput.Customer.OrganizationCode = inputData.Note.OrganizationCode;
		
		_isccsUIUtils.callApi(
		this, noteInput, "getNoteList", null);
	},
	callSaveNote: function(
	event, bEvent, ctrl, args) {
				var inputData = null;
		inputData = _scScreenUtils.getModel(
		this, "screenInput");
		console.log("inputData",inputData);
		var noteInput = {};
		noteInput.Customer = {};
		noteInput.Customer.CustomerID = inputData.Note.CustomerID;
		noteInput.Customer.OrganizationCode = inputData.Note.OrganizationCode;

		var manageCustomer_input = null;
		if (
		_scScreenUtils.validate(
		this)) {
			var refId = null;
			refId = [];
			refId.push("saveNote");
			refId.push("getNoteList");
			var target = null;
			
			manageCustomer_input = _scScreenUtils.getTargetModel(this, "manageCustomer_input", null)
			var noteList = manageCustomer_input.Customer.NoteList;
			console.log("notList",noteList);
			manageCustomer_input.Customer = inputData.Note;
			manageCustomer_input.Customer.NoteList = noteList;
			_scModelUtils.setStringValueAtModelPath("Customer.ExcludeCustomer","Y",manageCustomer_input);
			_scModelUtils.setStringValueAtModelPath("Customer.HasCustomerRemark","Y",manageCustomer_input);
			_scModelUtils.setStringValueAtModelPath("Customer.NotesRecordState","Added",manageCustomer_input);
		console.log("manageCustomer_input",manageCustomer_input);
			target = [];
			target.push(manageCustomer_input);
			target.push(noteInput);
			_isccsUIUtils.callApis(
			this, target, refId, null, null);
		}
	},
	handleMashupOutput: function(
	mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		if (
		_scBaseUtils.equals(
		mashupRefId, "getNoteList")) {
			
			modelOutput.NoteList = modelOutput.Customer.NoteList;
			delete modelOutput.Customer;
			console.log("modelOutput",modelOutput);
			
			if (!(
			_scBaseUtils.equals(
			false, applySetModel))) {
				_scScreenUtils.setModel(
				this, "getNoteList_output", modelOutput, null);
			}
			_isccsSearchUtils.closeSearchCriteriaPanel(
			this, "pnlNoteFilter");
			if (
			_scBaseUtils.getAttributeCount(
			_scModelUtils.getModelListFromPath("NoteList.Note", modelOutput)) > 0) {
				_scWidgetUtils.hideWidget(
				this, "pnlNoNote", true);
				_scWidgetUtils.hideWidget(
				this, "btnAddNote", false);
			} else {
				_scWidgetUtils.showWidget(
				this, "pnlNoNote", true, null);
				_scWidgetUtils.showWidget(
				this, "btnAddNote", false, null);
			}
		}
		if (
		_scBaseUtils.equals(
		mashupRefId, "saveNote")) {
			if (!(
			_scBaseUtils.equals(
			false, applySetModel))) {
				_scScreenUtils.setModel(
				this, "manageCustomer_output", modelOutput, null);
			}
			if (
			_scWidgetUtils.isWidgetVisible(
			this, "pnlCreateNote")) {
				if (
				_scScreenUtils.isPopup(
				this)) {
					this.updateParentScreen();
				} else {
					_scWidgetUtils.hideWidget(
					this, "pnlCreateNote", false);
				}
				_scWidgetUtils.showWidget(
				this, "btnAddNote", false, null);
			}
			this.resetNote();
			_isccsBaseTemplateUtils.showMessage(
			this, "Note_added_success", "success", null);
console.log("modelOutput",modelOutput);			
			var noteInput = {};
			noteInput.Customer = {};
			noteInput.Customer.CustomerID = modelOutput.Customer.CustomerKey;
			noteInput.Customer.OrganizationCode = "VSI.com";
console.log("noteInput",noteInput);
			_isccsUIUtils.callApi(this,noteInput,"getNoteList",null);
		}
		if (
		_scBaseUtils.equals(
		mashupRefId, "deleteNote")) {
			console.log("modelOutput",modelOutput);
			if (!(
			_scBaseUtils.equals(
			false, applySetModel))) {
				_scScreenUtils.setModel(
				this, "manageCustomer_output", modelOutput, null);
			}
			if (
			_scScreenUtils.isPopup(
			this)) {
				this.updateParentScreen();
			}
			var noteInput = {};
			noteInput.Customer = {};
			noteInput.Customer.CustomerID = modelOutput.Customer.CustomerKey;
			noteInput.Customer.OrganizationCode = "VSI.com";
console.log("noteInput",noteInput);
			_isccsUIUtils.callApi(this,noteInput,"getNoteList",null);
		}
	},
		updateCustomerNote: function(
		event, bEvent, ctrl, args) {
			var inputData = null;
			inputData = _scScreenUtils.getModel(
			this, "screenInput");
			var noteInput = {};
			noteInput.Customer = {};
			noteInput.Customer.CustomerID = inputData.Note.CustomerID;
			noteInput.Customer.OrganizationCode = inputData.Note.OrganizationCode;
		
			var eNote = null;
			eNote = _scBaseUtils.getAttributeValue("Note", true, args);			
			var refId = null;
			refId = [];
			refId.push("saveNote");
			refId.push("getNoteList");
			var target = null;
			target = [];
			target.push(
			this.getUpdateNoteInput(
			args));
			target.push(noteInput);
			console.log("target",target);
			console.log("refId",refId);
			_isccsUIUtils.callApis(
			this, target, refId, null, null);
		},
		getUpdateNoteInput: function(
        args) {
            var eCustomerInput = null;
            eCustomerInput = _scScreenUtils.getTargetModel(
            this, "deleteCustomerNote_input", null);
			var eNote = _scBaseUtils.getAttributeValue("Note", true, args);
            _scModelUtils.addModelToModelPath("Customer.NoteList.Note", eNote, eCustomerInput);
            _scModelUtils.setStringValueAtModelPath("Customer.HasCustomerRemark",args.HasCustomerRemark,eCustomerInput);
 			_scModelUtils.setStringValueAtModelPath("Customer.ExcludeCustomer",args.ExcludeCustomer,eCustomerInput);
 			_scModelUtils.setStringValueAtModelPath("Customer.NotesRecordState",args.NotesRecordState,eCustomerInput);
 			console.log("eCustomerInput",eCustomerInput);
             return eCustomerInput;
        },
        getDeleteNoteInput: function(
        eNote) {
            var eCustomerInput = null;
            eCustomerInput = _scScreenUtils.getTargetModel(
            this, "deleteCustomerNote_input", null);
            _scModelUtils.setStringValueAtModelPath("Customer.NoteList.Note.NoteID", _scModelUtils.getStringValueFromPath("NoteID", eNote), eCustomerInput);
            _scModelUtils.setStringValueAtModelPath("Customer.NoteList.Note.NoteText", _scModelUtils.getStringValueFromPath("NoteText", eNote), eCustomerInput);
            return eCustomerInput;
        },
        deleteCustomerNote: function(
        event, bEvent, ctrl, args) {
        	var inputData = null;
			inputData = _scScreenUtils.getModel(
			this, "screenInput");
			var noteInput = {};
			noteInput.Customer = {};
			noteInput.Customer.CustomerID = inputData.Note.CustomerID;
			noteInput.Customer.OrganizationCode = inputData.Note.OrganizationCode;
        	
			var deleteNoteInput = this.getDeleteNoteInput(_scBaseUtils.getAttributeValue("Note", true, args));
			_scModelUtils.setStringValueAtModelPath("Customer.HasCustomerRemark","Y",deleteNoteInput);
			_scModelUtils.setStringValueAtModelPath("Customer.ExcludeCustomer","Y",deleteNoteInput);
			_scModelUtils.setStringValueAtModelPath("Customer.NotesRecordState","Deleted",deleteNoteInput);
			console.log("deleteNoteInput",deleteNoteInput);
			
            var refId = null;
            refId = [];
            refId.push("deleteNote");
            refId.push("getNoteList");
            var target = null;
            target = [];
            target.push(deleteNoteInput);
            target.push(noteInput);
            _isccsUIUtils.callApis(
            this, target, refId, null, null);
        }
	});
});
