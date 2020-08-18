/*
 * .
 */

/*
 * 
 */

/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_Search": "Search",
    "b_Reset": "Reset"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnlColonySearch": "Search Colony",
    "b_colonyId": "Colony Id",
    "b_pkPrefix": "Prefix",
    "b_tableType": "Table Type",
    "b_colonyVersion": "Version",
    "b_grdpnlColonytitle": "Colony List",
    "b_tbcreateColony": "Create Colony",
    "b_tbcolonyDetails": "Colony Details",
    "b_tbcolonyDelete": "Delete Colony",
    "b_colColonyID": "Colony Id",
    "b_colPrefix": "Prefix",
    "b_colVersion": "Version",
	"b_tbsynchronize": "Synchronize",
	"b_execgenscripts": "Execute Generated Scripts",
	"b_synchOkButton": "Ok",
	"b_synchCancelButton": "Cancel"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnlcreateColony": "Colony",
    "b_saveButton": "Save",
    "b_cancelButtont": "Cancel"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnladdColonyPool": "Shard Mapping",
    "b_colonyPool": "Shard Id",
    "b_cancelButton": "Cancel"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnlcolonyDetail": "Colony Details",
     "b_grdpnlColonyPoolList": "Shard Mapping List",
    "b_tbaddColonyPool": "Add Shard Mapping",
    "b_tbdeleteColonyPool": "Delete Shard Mapping",
    "b_colColonyPoolId": "Shard Id",
    "b_colTableType": "Table Type"
});

//Alert 
Ext.override(sc.plat.ui.Screen,{
	"Please_select_a_row":"Please select a row",
	"Confirm_to_delete_the_colony":"Confirm to delete the colony",
	"Confirm_to_delete_colony_pool":"Confirm to delete the shard mapping",
	"No_Result_Found":"No Result Found",
	"Pool_Id_Is_Mandatory":"Shard Id Is Mandatory",
	"Table_Type_Is_Mandatory":"Table Type Is Mandatory",
	"Colony_Id_Is_Mandatory":"Colony Id Is Mandatory",
	"Prefix_Is_Mandatory":"Prefix Is Mandatory",
	"Please_select_a_row_to_delete":"Please select a row to delete",
	"Please_enter_dbpool_param":"Please enter all the shard connection parameters as name value pair",
	"Please_enter_one_value_for":"Please enter only one value for ",
	"Please_enter_password_for":"Please enter password for ",
	"Please_enter_effective_date_for":"Please enter effective date for ",
	"Confirm_to_delete_dbpool_param":"Are you sure you want to delete selected row(s)?",
	"Confirm_to_delete_dbpool":"Please confirm to delete this shard connection",
	"Please_confirm_that_you_want_to_synchronize_the_selected_colony":"Please confirm that you want to synchronize the selected colony",
	"Please_select_a_colony_to_synchronize":"Please select a colony to synchronize"
});

//Popup
Ext.override(sc.plat.ui.Screen,{
	"b_createColonyPopup":"Create Colony",
	"b_addColonyPoolPopup":"Add Shard Mapping",
	"cb_useDefaultConfigurationShards" : "Use Default Configuration Shards"
});

//Screen Title
Ext.override(sc.plat.ui.Screen,{
	"b_loginPageTitle":"System Management Administrator",
	"b_manageColonyScreenTitle":"Manage Colony",
	"b_homePageTitle":"System Administration Console",
	"b_colonyDetailTitle":"Colony Detail"
});

//Login Page 
Ext.override(sc.plat.ui.Screen,{
	"b_loginId":"Login Id",
	"b_password":"Password",
	"b_signInButton" : "Sign In",
	"b_pnlLogin" : "Login"
});

//Home Page
Ext.override(sc.plat.ui.Screen,{
	"b_homePageAppName":"System Administration Console" ,
	"b_welcomeMessage" : "Welcome",
	"b_homeLink" : "Home",
	"b_signoutLink":"Logout"
});

//Dashboard Page
Ext.override(sc.plat.ui.Screen,{
	"b_dashBoard":"Dashboard" ,
	"b_listPrSts" : "Process Status List",
	"b_prStsKey" : "Process ID",
	"b_grdPrName":"Process Name",
	"b_grdStatus":"Status" ,
	"b_grdStrtTime" : "Date",
	"b_btnRefresh" : "Refresh",
	"b_viewSyncData":"View Synchronization Data",
	"b_viewLogData":"View Logs",
	"b_viewScriptData":"View Scripts",
	"b_grdDbVerifyScript":"DB Verify Scripts",
	"b_grdColScriptType": "Script Type",
	"b_grdColScriptHasData": "Contains Data",
    "b_grdColScriptName": "Script Name",
    "b_btnScriptView": "View Script",
    "b_pnlScriptView": "Script View",
	"b_scriptViewer" : "Script Viewer",
	"b_btnDownloadSynchData" : "Download Synchronization Data",
	"b_btnDownloadLogs" : "Download Logs",
	"b_btnDownloadScript" : "Download Script",
	"b_btnDownloadAllScripts" : "Download All Scripts",
	"b_btnAutoRefresh" : "Auto Refresh",
	"tt_refresh":"Refresh Dashboard",
	"tt_autoRefresh":"Toggle this button to enable/disable auto refresh",
	"tt_downloadSynchData" : "Download Synchronization Data",
	"tt_downloadLog" : "Download Logs",
	"tt_viewScript" : "View Scripts",
	"tt_downloadScript" : "Download Script",
	"tt_downloadAllScripts" : "Download All Scripts"
});

//Qry Type
Ext.override(sc.plat.ui.Screen,{
	"QueryTypeEQ":"is" ,
	"QueryTypeFLIKE" : "starts with",
	"QueryTypeLIKE" : "contains"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_tbmanageDBPool": "Manage DB Shards"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnlSearchDBPool": "Search DB Shards",
    "b_dbPool": "Shard Id",
    "b_pnlDBPoolList": "DB Shard List",
    "b_tbCreateDBPool": "Create DB Shard",
    "b_tbDBPoolDetails": "DB Shard Details",
    "b_tbDeleteDBPool": "Delete DB Shard",
    "b_url": "URL",
    "b_user": "User"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_pnlManageDBPool": "Manage DB Shard Connection",
    "b_dbPool": "Shard Id",
    "b_pnlPoolParamList": "Shard Parameter List",
    "b_tbAddParam": "Add Param",
    "b_tbDeleteParam": "Delete Param",
    "b_paramName": "Name",
    "b_paramValue": "Value"
});
/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_SearchProperties": "Search Properties",
    "b_BasicSearch": "Switch to Advanced Search",
    "b_Category": "Category",
    "b_PropertyName": "Property Name",
    "b_OverrideType": "Override Type",
    "b_OverriddenFor": "Overridden For",
    "b_Modifiable": "Modifiable",
    "b_ModifiableatRuntime": "Modifiable at Run time",
    "b_PropertyType": "Property Type",
    "b_PropertyValue": "Property Value",
    "b_Description": "Description",
    "b_UserComments": "User Comments",
    "b_Author": "Author",
    "b_Editor": "Editor",
    "b_Createdbetween": "Created between",
    "b_and": "and",
    "b_Modifiedbetween": "Modified between",
     "b_Panel4": "Panel 4",
    "b_PropertyList": "Property List",
    "b_CreateProperty": "Create Property",
    "b_ManageProperty": "Manage Property",
    "b_DeleteProperty": "Delete Property",
    "b_FactoryValue": "Factory Value",
    "b_Value": "Value",
    "b_Type": "Type",
    "b_ModifiableAtRuntime": "Modifiable At Run time",
    "b_UserOverridable": "User Overridable",
    "b_ServerOverridable": "Server Overridable",
    "b_PropertyDetails": "Property Details",
    "b_OverriddenProperties": "Overridden Properties",
    "b_Panel15": "Panel 15",
    "b_Panel16": "Panel 16",
    "b_PermissibleValues": "Permissible Values ",
    "b_Grid1": "Grid1",
    "b_Values": "Values"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_CreateProperty": "Create",
    "b_BacktoSearch": "Back to Search",
    "b_DataType": "Data Type",
    "b_PermissibleValues": "Permissible Values",
    "b_AddValue": "Add Value",
    "b_DeleteValue": "Delete Value"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_ManageProperties": "Manage Properties",
    "b_AuditHistory": "Audit History",
    "b_ModifyValue": "Modify Value",
    "b_SaveChanges": "Save Changes",
    "b_Createdon": "Created on",
    "b_Modifiedon": "Modified on",
    "b_AddNewOverride": "Add New Override",
    "b_DeleteOverride": "Delete Override"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_CreatePropertyPanelTitle": "Create Property"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_BacktoManageProperty": "Back to Manage Property",
    "b_Grid3": "Grid3",
    "b_HideOverridenProperties": "Hide Overriden Properties",
    "b_ResettoSelectedValue": "Reset to Selected Value",
    "b_Date": "Date",
    "b_Action": "Action",
    "b_OldValue": "Old Value",
    "b_NewValue": "New Value"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_searchScreenCreateProperty": "Create Property"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_Panel7": "Panel 7",
    "b_Panel9": "Panel 9",
	"b_ShowOverridenProperties":"Show Overriden Properties",
	"b_AdvancedSearch":"Switch to Basic Search",
	"b_ModifyValueOkayButton":"Ok",
	"b_ModifyValueCancelButton":"Cancel"
	
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_OverrideFor": "Override For"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
    "b_SearchModifiedProperties": "Search Modified Properties",
    "b_OrderBy": "Order By :"
});/*AUTO-GENERATED bundle fragment*/
Ext.override(sc.plat.ui.Screen, {
	"b_Modify_Value_For":"Modify Value For",
	"b_Error":"Error",
	"b_Value_Modified":"Value Modified",
	"b_warning_modifiable_at_runtime":"The value has been modified successfully. The server should be restarted as the property must not be modified at run time",
	"b_Value_Modified_successfully":"The value has been modified successfully.",
	"errModifyValueScreen":"Correct the errors in the screen to proceed",
	"b_Please_select_a_row":"Please select a row",
	"b_Alert":"Alert",
	"b_Confirm":"Confirm",
	"b_navigate_back_dirty":"Are you sure you want to go back to Search screen? All unsaved changes will be lost",
	"errCreateScreen":"Correct the errors in the screen to proceed",
	"errAddNewOverrideScreen":"Correct the errors in the screen to proceed",
	"b_Please_select_row_to_delete":"Please select row to delete",
	"b_confirm_delete":"Are you sure you want to delete selected row?",
	"b_Save_Successful":"Save Successful",
	"b_The_changes_were_saved_successfully":"The changes were saved successfully.",
	"b_Add_New_Override_For":"Add New Override For",
	"b_warning_delete_property_with_override":"Are you sure you want to delete this property? All the overrides for this property will also be deleted.",
	"b_Deletion_Failed":"Deletion Failed",
	"b_Deletion_Failed_Msg":"The property could not be deleted.",
	"b_Deletion_Property_override_prompt":"Are you sure you want to delete selected override?",
	"b_Deletion_Successful":"Deletion Successful",
	"b_The_override_was_deleted_successfully":"The override was deleted successfully.",
	"b_The_override_was_not_deleted":"The override was not deleted.",
	"b_Creation_Successful":"Creation Successful",
	"b_Override_Creation_successful":"The override was created successfully.",
	"b_The_property_was_deleted_successfully":"The property was deleted successfully.",
	"b_The_property_was_deleted_successfully_msg":"The property was deleted successfully. Paging Toolbar Disabled. Hit 'Search' to fire a fresh search.",
	"b_Loading":"Loading...",
	"b_Error_caps":"ERROR!!",
	"b_errSearchScreen":"Error(s) in search criteria! Correct the error(s) to proceed.",
	"b_Only_valid_values_for_this_field_is_Y_N":"Y and N are only valid values for this field.",
	"b_confirm_mark_synchronized":"Are you sure you want to mark selected enterprise as synchronized?",
	"b_confirm_mark_all_synchronized":"Are you sure you want to mark all enterprises as synchronized?",
	"b_confirm_mark_not_synchronized":"Are you sure you want to mark selected enterprise as not synchronized?",
	"b_confirm_mark_all_not_synchronized":"Are you sure you want to mark all enterprises as not synchronized?",
	"b_confirm_manage_new_index":"Are you sure you want to manage another index? All unsaved changes for this index will be lost.",
	"b_confirm_index_status_save":"Are you sure you want to save the changes?"
});
Ext.override(sc.plat.ui.Screen, {
	"b_IndexManagementScreen" : "Index Management",
	"b_pnlIndexManagement" : "Index Management",
	"b_pnlIndexStatus" : "Index Status",
	"b_pnlIndexSynchStatus": "Index Synchronization Status",
	"b_IndexName":"Index Name",
	"b_indexNameCmbEmptyText" : "Select an index",
	"b_SearchWorking" : "Search Working",
	"b_IndexingWorking" : "Indexing Working",
	"b_UpdateRequired" : "Update Required",
	"b_IndexVersion" : "Index Version",
	"b_btnIndexStatusSave" : "Save",
	"b_btnIndexStatusReset" : "Reset",
	"b_btnMarkSynchronized" : "Mark As Synchronized",
	"b_btnMarkAllSynchronized" : "Mark All As Synchronized",
	"b_btnMarkNotSynchronized" : "Mark As Not Synchronized",
	"b_btnMarkAllNotSynchronized" : "Mark All As Not Synchronized",
	"b_enterpriseCode" : "Enterprise Code",
	"b_synchronized" : "Synchronized",
	"b_linkViewIndexTemplate" : "View Index Template",
	"b_IndexTemplateViewer" : "Index Template Viewer",
	"b_pnlIndexTemplateView" : "Index Template View",
	"b_btnIndexGo" : "Go"
});/*Bundle for index management*/
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.errorWindowUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "errorWindow",
        header: false,
        layout: "anchor",
        items: [{
            xtype: "panel",
            sciId: "errorPanel",
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "errorImageLabel",
                html: this.errorImg(),
                height: 32,
                width: 32
            },
            {
                xtype: "label",
                sciId: "errorMsgLabel",
                text: this.errorMsg,
                id: "errorMsgLabel"
            },
            {
                xtype: "label",
                sciId: "errorAttribLabel",
                allowDomMove: true,
                colspan: 2
            }],
            border: true,
            buttons: [{
                xtype: "button",
                sciId: "Closebutton",
                text: "Close",
                handler: this.closeButtonHandler,
                scope: this
            },
            {
                xtype: "button",
                sciId: "detailButton",
                text: "Details",
                handler: this.detailHandler,
                scope: this,
                iconCls: "sma-error-showdetail-icon",
                id: "detailButton"
            }],
            allowDomMove: true,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            }
        },
        {
            xtype: "textarea",
            sciId: "errorWindowTxtArea",
            id: "errorWindowTxtArea",
            readOnly: true,
            style: "sma-error-textarea-style",
            hidden: true,
            cls: "sma-error-textarea"
        }],
        autoScroll: true
    };
}
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.errorWindow = function(config) {
    sc.sma.errorWindow.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.errorWindow, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.errorWindow',
    getUIConfig: sc.sma.errorWindowUIConfig,
	parentWindow : null,
	detailWindow : null,
    namespaces: {
        target: [],
        source: []
    },

	errorMsg : null,
	errorStack : null,
	errorAttributes : null,
	
	errorImg : function(){
		return "<img src='"+Ext.BLANK_IMAGE_URL+"' class='sma-error-icon'/>" ;
	},

	closeButtonHandler : function(){
		this.parentWindow.destroy();
	},

	//Need to move to util class
	findButtonItemFromParent : function(parent,sciId){
		var buttons = parent.buttons;
		var i ;
		for(i=0;i<buttons.length;i++){
			var button = buttons[i];
			if(button.sciId == sciId){
				return button;
			}
		}

	},

	detailHandler : function(){
		var txtAreaCmp = this.find('sciId','errorWindowTxtArea')[0];
		var errorPanelCmp = this.find('sciId','errorPanel')[0];
		var detailButtonCmp = this.findButtonItemFromParent(errorPanelCmp,'detailButton');
		if(this.errorStack){
			if(!this.detailWindow){
				this.detailWindow = 'true';
				txtAreaCmp.show();
				this.parentWindow.setHeight(550);
				txtAreaCmp.setWidth(480);
				txtAreaCmp.setHeight(320);
				txtAreaCmp.setValue(this.errorStack);
				detailButtonCmp.setIconClass('sma-error-hidedetail-icon');
				
			}else{
				this.detailWindow = null;
				detailButtonCmp.setIconClass('sma-error-showdetail-icon');
				this.parentWindow.setHeight(150);
				txtAreaCmp.setVisible(false);
			}
		}
	},

	setErrorStack:function(es){
	    var txtAreaCmp = this.find('sciId','errorWindowTxtArea')[0];
	    var errorPanelCmp = this.find('sciId','errorPanel')[0];
	    var detailButtonCmp = this.findButtonItemFromParent(errorPanelCmp,'detailButton');
		this.errorStack = es;
		if(!this.errorStack){
			txtAreaCmp.hide();
			detailButtonCmp.disable();
		}
	},
	
	setErrorMsg:function(em){
		console.log('setting errorMessage..',em);
		this.errorMsg = em;
	},

	getErrorMsg:function(){
		return this.errorMsg;
	},

	setErrorMsgOnScreen:function(){
		var errorLabelCmp = this.findItembysciId("errorMsgLabel");
		errorLabelCmp.setText(this.errorMsg); 
		errorLabelCmp.addClass('sma-error-message-style');
	},
	
	//This should be moved to util class
	findItembysciId:function(scid){
		return this.find('sciId',scid)[0];
	},

	setErrorAttributes:function(errorAttribs){
		this.errorAttributes = errorAttribs;
	},
	
	showErrorAttributes : function(){
		var errorAttrLabel = this.findItembysciId("errorAttribLabel");
		var errAttrText = this.getErrorAttributeText();
		errorAttrLabel.setText(errAttrText);
		errorAttrLabel.addClass('sma-error-attribute-style');
	},


	
	getErrorAttributeText:function(){
		var errorAttrText = '[';
		var isFirst = null;
		var i;
		//boolean isFirst = true;
		for(i = 0; i < this.errorAttributes.length ; i++){
			var attr = this.errorAttributes[i];
			var attrName = attr.Name;
			var attrValue = attr.Value;
			if(attrName == "ErrorCode" || attrName == "ErrorDescription" || attrName == "ErrorRelatedMoreInfo"){
				//As these info already available...not appending it to Error Attributes
			}else{
				if(!isFirst){
					errorAttrText = errorAttrText + attrName + " : '"  + attrValue +  "'";
					isFirst = true;
				}else{
					errorAttrText = errorAttrText + ", " + attrName + " : '" + attrValue + "'" ;
				}
			}
		}
		errorAttrText = errorAttrText + ']';
		return errorAttrText;
	}

});
Ext.reg('xtype_name', sc.sma.errorWindow);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace("sc.sma");

sc.sma.SMAErrorHandler = Ext.extend(sc.plat.DefaultErrorHandler, {

	errorCode : null,
	errorDesc : null,
	errorAttributes : null,
	
	handleAjaxErrors: function(res, options) {
		var json = sc.plat.AjaxUtils.getResponseJSON(res);
		console.log('res is ',res);
		this.handleErrors(json.response, res.bindingIDMap);
	},
	
	hasAjaxErrors: function(res, options) {
		var json = sc.plat.AjaxUtils.getResponseJSON(res);
		if(json && json.response) {
			var b = json.response.success;
			if(b != null) {
				if(typeof b == "boolean") {
					return !b;
				} else {
					return !sc.plat.Utils.getBoolean(b);
				}
			}
		}
		return false;
	},
	
	
	handleErrors: function(obj, bindingIDMap) {
		try{
			console.log('inside error handler...');
			if(obj.success === true)
				return;
			var em = obj.em;

			if(obj.Errors){
				var errors = obj.Errors.Error[0];
				var errorObj = obj.Errors;
				if(errorObj == null)
					return;
				this.errorCode = errors.ErrorCode;
				this.errorDesc = errors.ErrorDescription;
				this.showErrorWindow(errors);
			}else if(obj.errors && obj.errors.dvName){
				var o = {
							ErrorCode: obj.errors.dvName,
							Stack : obj.st,
							ErrorDescription:obj.errors.msg || obj.em,
							Attribute :[{Name:"ParameterName",Value : obj.errors.dvName}]
						};
				this.showErrorWindow(o);
			} else {
				var o = {
							Stack : obj.st,
							ErrorDescription: obj.em
						};
				this.showErrorWindow(o);
			}
		}catch(x){
			try{
				sc.sma.SMAErrorHandler.superclass.handleErrors(obj, bindingIDMap);
			}catch(y){
				Ext.MessageBox.show({
					title: 'Error',
					msg: 'Some Error Occured',
					width:300,
					buttons: Ext.Msg.OK,
					icon: Ext.MessageBox.ERROR
				});
			}
		}
	},
	errorPopupWindow : null,
	title : null,
	
	showErrorWindow:function(errors){
		var errorMsg = (errors.ErrorCode ? errors.ErrorCode  + ' : ' : "" ) +  errors.ErrorDescription;
		var stackTrace = errors.Stack;
		var errorAttrs = errors.Attribute;
		var errorWindow = new sc.sma.errorWindow();
		errorWindow.setErrorStack(stackTrace);
		errorWindow.setErrorMsg(errorMsg);
		errorWindow.setErrorAttributes(errorAttrs);
		this.errorPopupWindow = new Ext.Window({
			items:[errorWindow],  
			allowDomMove:true,
			height: 200,
			//autoHeight : true,
			width: 500,
			modal:true,
			closable: false,
			layout : 'fit'
		});

		errorWindow.parentWindow = this.errorPopupWindow;
		this.errorPopupWindow.show();

		this.errorPopupWindow.setTitle('<span class="sma-error-title-style">Exception</span>');
		//Ext.getCmp("errorMsgLabel").setText(errorMsg); //.setText(errorMsg);
		//Ext.getCmp("errorMsgLabel").addClass('sma-error-message-style');
		errorWindow.setErrorMsgOnScreen();
		if(errorAttrs != null && errorAttrs.length == 0) {
			errorWindow.showErrorAttributes();
		}
		this.errorPopupWindow.hideParent = true;
	},
	
	showErrors: function(errorObj, bindingIDMap) {
		console.log('bindingIDMap',bindingIDMap);

		if(errorObj == null)
			return;
		console.log('errorObj is ',errorObj);
		for(var i=0; i< errorObj.length; i++) {
			var e = errorObj[i];
			var id = e.id;
			console.log('id is',id);
			if(id == null && e.tBinding != null && e.tBinding != "")
			{
				if(bindingIDMap == null)
					continue;
				var id = sc.plat.DataUtils.extractData(bindingIDMap, e.tBinding, true);
			}
			if(id == null)
				return;
			var cmp = Ext.getCmp(id);
			if(cmp == null)
				return;
			cmp.markInvalid(e.msg);
		}
	},
	
	getErrorWindow : function(){

	},
	
	hideErrorWindow: function() {
		if(this.win)
			this.win.hide();
	}

});

sc.plat.RequestUtils.registerErrorHandler(new sc.sma.SMAErrorHandler);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
	var mainViewPort = new Ext.Viewport({
    	layout: 'border',
    	hideBorders: true,
    	items: [
    	{
    		region: 'center',
    		border: false,
    		id:'smaMainPnl',
    		xtype: 'panel',
  			autoScroll: true,
    		items: [{
			        xtype: 'panel',
			        autoHeight: true,
			        autoWidth:true,
			        border: false,
			        cls: "containerLayout",
			        items: [
						{
					        xtype: 'panel',
					        header: false,
					        height: 66,
					        border: true,
					        id: 'mainMenuPanel',
							listeners: {
								'render': function(pnl) {
									var el1 = pnl.body.createChild({tag:'div',id:'mainBackGround'});   
									var el2 = pnl.body.createChild({tag:'div',id:'sterlingLogo',onclick:"sc.sma.AboutBoxObj.launchSMAAboutBox()"});
									el2.addClass('sc-sterling-commerce-logo');
									el2.addClass('sma-cursor-pointer');
									var el3 = pnl.body.createChild({tag:'div',id:'backGroundText'});
									el3.dom.innerHTML = sc.plat.bundle.b_homePageAppName;
									el3.addClass('sc-application-name');
									var e14 = pnl.body.createChild({tag:'div',id:'applicationLogo'});
									e14.addClass('sma-systemadmin-logo');
									var str = null;
									if(sc.plat.Userprefs.getUserName() == 'smaguest'){
										str = '<a href="/' + sc.plat.info.Application.getApplicationContext() + '/sma/console/login.jsp">Login</a>';
									}else{
										str = '<a onclick=\'javascript:sc.plat.FormUtils.request({method: "GET", url: "/' + sc.plat.info.Application.getApplicationContext() + '/sma/console/logout.jsp"});\' href="#">'+ sc.plat.bundle.b_signoutLink + '</a>';
									}
									var tpl = new Ext.Template(
									'<span class="sc-welcome-message">'+sc.plat.bundle.b_welcomeMessage+' '+sc.plat.Userprefs.getUserName()+' </span>',
									' | ',
									'<a onclick=\'javascript:sc.plat.FormUtils.request({method: "GET", url: "/' + sc.plat.info.Application.getApplicationContext() + '/sma/container/home.do"});\' href="#">'+ sc.plat.bundle.b_homeLink +'</a>',
									' | ',
									str
									).compile();
									var el4 = pnl.body.createChild({tag:'div',id:'backGroundURL'});
									el4.dom.innerHTML = tpl.apply({});
									el4.addClass('sc-panel-url');
								}
							}
	    				},
	    				{
					        xtype: 'panel',
					        id: 'mainBelowMenuPanel',
					        header: false,
					        border: false
	    				},
	    				{
					        xtype: 'panel',
					        header: false,
					        autoHeight: true,
					        id: 'mainBodyPanel',
					        border: false
	    				}//,
						//Time being we dont require footer panel
	    				//{
					      //  xtype: 'panel',
					        //id: 'mainFooterPanel',
					        //header: false,
					        //height: 40,
					        //border: false,
					        //cls: 'sc-footer',
					        //html: "<span class='sc-footer-copyright-message'>© 2007-2008 Air Sterling Inc."+
					        //" | "+
					        //"<a href='' >About Us</a>"+
					        //" | "+
					        //"<a href='' >Privacy Policy</a>"+
					        //" | "+
					        //"<a href='' >Trade Secret Notice</a>"+
					        //" | "+
					        //"<a href='' >Sitemap</a>"+
					        //"</span>"
	    				//}
			        ]
    			}
    		]
    	}]
	});
	sc.plat.Menupaint.renderMenu('mainMenuPanel');
	Ext.namespace("sc.plat");
	sc.plat.ScreenTitle = function(){
		var pnl = Ext.getCmp('mainBelowMenuPanel');
		var pnlText = pnl.body.createChild({tag:'div',id:'belowMenuText'});
		var pnlDesc = pnl.body.createChild({tag:'div',id:'belowMenuDesc'});
		var pnlMsg = pnl.body.createChild({tag:'div',id:'belowMenuMsg'});
		var msgCls = "msg";
		return {
			setText: function(text, height, cssClass){
				pnlText.dom.innerHTML = text;
				if(!Ext.isEmpty(height)){
					pnlText.setHeight(height);
				}
				if(!Ext.isEmpty(cssClass)){
					pnlText.addClass(cssClass);
				}
			},
			setDescription: function(desc, height, cssClass){
				pnlDesc.dom.innerHTML = desc;
				if(!Ext.isEmpty(height)){
					pnlDesc.setHeight(height);
				}
				if(!Ext.isEmpty(cssClass)){
					pnlDesc.addClass(cssClass);
				}
			},
			setMsg: function(text, type, height, cssClass){
				
				if(!Ext.isEmpty(text)){
					pnlMsg.dom.innerHTML = text;
				}else{
					pnlMsg.dom.innerHTML = '';
				}
				if(!Ext.isEmpty(height)){
					pnlMsg.setHeight(height);
				}
				var cls = null;
				if(Ext.isEmpty(cssClass)){
					cls = type == "err"? "msgErr":"msg";
				}else{
					cls = cssClass; 
				}
				if(this.msgCls !== cls){
					pnlMsg.removeClass(this.msgCls);
					this.msgCls = cls;
					pnlMsg.addClass(cls);
				}
			}
		}
	}();
	Ext.namespace("sc.sma");
	sc.sma.AboutBoxObj = function() {
		return {
			launchSMAAboutBox: function() {
				sc.sma.AjaxUtils.request({
					actionNS: "sma"
					, action: "smaAboutBox"
					, success: this.successHandler
					, scope: this
				});
			}
			, successHandler: function(response, options) {
				if(!Ext.isEmpty(response.responseText)) {
					eval(response.responseText);
				}
			}
		}
	}();
});
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.ns("sc.sma");

/*
Using screen bundle entry in util class. This was done as there was one common entry, "b_Loading" to be used.
Ideally, we should have a separate bundle file for such cases. In future, if some more entries are required for util files, then a separate bundle file must be defined.
*/
var defaultLoadingTxt = sc.plat.ui.Screen.prototype["b_Loading"];

sc.sma.AjaxUtils = {
	actionFilter : ".do",
	hideMask : false,
	
	maskId : Ext.getBody(),
	
	mask : new Ext.LoadMask(Ext.getBody(),{msg: defaultLoadingTxt}),

	// private
	showMask : function(config){
		
		if(config && config.maskId){
			this.newMask = new Ext.LoadMask(config.maskId, {msg: config.maskMsg || defaultLoadingTxt});
			this.newMask.show();
		}else{
			this.mask.show();
		}		
	},

	// private
	unloadMask : function(config){
		if(config && config.maskId){
			if(this.newMask){
				this.newMask.destroy();
				this.newMask = null;				
			}
		}else{
			this.mask.hide();
		}	
	},
	
	// private
	getAjaxURL : function(ns, action) {	
		
		if (!action) {
			return;
		}
		
		var actionNS = "";
		// if ns is passed then take it..if not passed take the module namespace...
		actionNS = ns ?  ns : jsContext.getNamespace();

		if (!(actionNS.indexOf("/") === 0)) {
			actionNS = "/" + actionNS;
		}
		if (!(action.indexOf("/") === 0)) {
			action = "/" + action;
		}
		var url = "/"+sc.plat.info.Application.getApplicationContext() + actionNS + action + sc.sma.AjaxUtils.actionFilter;
		return url;
	},
	
	//private
	callback : function(){
		this.unloadMask();				
	},
	
	//private
	failure : function(response, options){
		this.unloadMask();
	},
	
	//private
	loadMask: function(config){
		if(this.hideMask){
			return;
		}
		this.showMask(config);			
	},
	
	/**
     * <p> Utility method to construct the URL and Sends an HTTP request to a remote server.  </p>
     * <p><b>Important:</b> Ajax server requests are asynchronous, and this call will
     * return before the response has been received. Process any returned data
     * in a callback function.</p>
     * <p>To execute a callback function in the correct scope, use the <tt>scope</tt> option.</p>
     * @param {Object} options An object which may contain the following properties:<ul>
     * <li><b>url</b> : String/Function (Optional)<div class="sub-desc">The URL to
     * which to send the request, or a function to call which returns a URL string. The scope of the
     * function is specified by the <tt>scope</tt> option. Defaults to configured URL.</div></li>
     * <li><b>actionNS</b> : String (Optional)<div class="sub-desc">The NameSpace for the struts action. 
     * If not passed it will use the default NameSpace defined in JSContext. </div></li> 
     * <li><b>action</b> : String (Optional)<div class="sub-desc">The struts action for the request. 
     * Do not pass the struts filter with the action it will be used as actionFilter property to get the filter. </div></li> 
     * <li><b>params</b> : Object/String/Function (Optional)<div class="sub-desc">
     * An object containing properties which are used as parameters to the
     * request, a url encoded string or a function to call to get either. The scope of the function
     * is specified by the <tt>scope</tt> option.</div></li>
     * <li><b>method</b> : String (Optional)<div class="sub-desc">The HTTP method to use
     * for the request. Defaults to the configured method, or if no method was configured,
     * "GET" if no parameters are being sent, and "POST" if parameters are being sent.  Note that
     * the method name is case-sensitive and should be all caps.</div></li>
     * <li><b>callback</b> : Function (Optional)<div class="sub-desc">The
     * function to be called upon receipt of the HTTP response. The callback is
     * called regardless of success or failure and is passed the following
     * parameters:<ul>
     * <li><b>options</b> : Object<div class="sub-desc">The parameter to the request call.</div></li>
     * <li><b>success</b> : Boolean<div class="sub-desc">True if the request succeeded.</div></li>
     * <li><b>response</b> : Object<div class="sub-desc">The XMLHttpRequest object containing the response data. 
     * See <a href="http://www.w3.org/TR/XMLHttpRequest/">http://www.w3.org/TR/XMLHttpRequest/</a> for details about 
     * accessing elements of the response.</div></li>
     * </ul></div></li>
     * <a id="request-option-success"></a><li><b>success</b> : Function (Optional)<div class="sub-desc">The function
     * to be called upon success of the request. The callback is passed the following
     * parameters:<ul>
     * <li><b>response</b> : Object<div class="sub-desc">The XMLHttpRequest object containing the response data.</div></li>
     * <li><b>options</b> : Object<div class="sub-desc">The parameter to the request call.</div></li>
     * </ul></div></li>
     * <li><b>failure</b> : Function (Optional)<div class="sub-desc">The function
     * to be called upon failure of the request. The callback is passed the
     * following parameters:<ul>
     * <li><b>response</b> : Object<div class="sub-desc">The XMLHttpRequest object containing the response data.</div></li>
     * <li><b>options</b> : Object<div class="sub-desc">The parameter to the request call.</div></li>
     * </ul></div></li>
     * <li><b>scope</b> : Object (Optional)<div class="sub-desc">The scope in
     * which to execute the callbacks: The "this" object for the callback function. If the <tt>url</tt>, or <tt>params</tt> options were
     * specified as functions from which to draw values, then this also serves as the scope for those function calls.
     * Defaults to the browser window.</div></li>
     * <li><b>form</b> : Element/HTMLElement/String (Optional)<div class="sub-desc">The <tt>&lt;form&gt;</tt>
     * Element or the id of the <tt>&lt;form&gt;</tt> to pull parameters from.</div></li>
     * <a id="request-option-isUpload"></a><li><b>isUpload</b> : Boolean (Optional)<div class="sub-desc">True if the form object is a
     * file upload (will usually be automatically detected).
     * <p>File uploads are not performed using normal "Ajax" techniques, that is they are <b>not</b>
     * performed using XMLHttpRequests. Instead the form is submitted in the standard manner with the
     * DOM <tt>&lt;form></tt> element temporarily modified to have its
     * <a href="http://www.w3.org/TR/REC-html40/present/frames.html#adef-target">target</a> set to refer
     * to a dynamically generated, hidden <tt>&lt;iframe></tt> which is inserted into the document
     * but removed after the return data has been gathered.</p>
     * <p>The server response is parsed by the browser to create the document for the IFRAME. If the
     * server is using JSON to send the return object, then the
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">Content-Type</a> header
     * must be set to "text/html" in order to tell the browser to insert the text unchanged into the document body.</p>
     * <p>The response text is retrieved from the document, and a fake XMLHttpRequest object
     * is created containing a <tt>responseText</tt> property in order to conform to the
     * requirements of event handlers and callbacks.</p>
     * <p>Be aware that file upload packets are sent with the content type <a href="http://www.faqs.org/rfcs/rfc2388.html">multipart/form</a>
     * and some server technologies (notably JEE) may require some custom processing in order to
     * retrieve parameter names and parameter values from the packet content.</p>
     * </div></li>
     * <li><b>headers</b> : Object (Optional)<div class="sub-desc">Request
     * headers to set for the request.</div></li>
     * <li><b>xmlData</b> : Object (Optional)<div class="sub-desc">XML document
     * to use for the post. Note: This will be used instead of params for the post
     * data. Any params will be appended to the URL.</div></li>
     * <li><b>jsonData</b> : Object/String (Optional)<div class="sub-desc">JSON
     * data to use as the post. Note: This will be used instead of params for the post
     * data. Any params will be appended to the URL.</div></li>
     * <li><b>disableCaching</b> : Boolean (Optional)<div class="sub-desc">True
     * to add a unique cache-buster param to GET requests.</div></li>
     * </ul></p>
     * <p>The options object may also contain any other property which might be needed to perform
     * postprocessing in a callback because it is passed to callback functions.</p>
     * @return {Number} transactionId The id of the server transaction. This may be used
     * to cancel the request.
     */
	request : function(config){         

            this.loadMask(config);
			var url = this.getAjaxURL(config.actionNS, config.action);
			var ns = config.inputNS;
			var inputObj = "";
			var obj;
            if(config.inputObj){
                  inputObj = Ext.encode(config.inputObj);
                   obj = {};
                  eval("obj."+ns+"='"+inputObj+"'");
            }

            this.transId = Ext.Ajax.request({
					url : config.url || url,
					success : this.handleResponse,
					callback : config.callback || this.callback,
					scope : this,
					params : obj || config.params,
					failure : this.failure,
					configParams : config,
					extraParams : config.extraParams,
					async : config.async,
					timeout : 600000
				});

      },
	  

	  // private
	handleResponse : function(response, options){
		this.unloadMask(options.configParams);
		if(sc.plat.RequestUtils.hasAjaxErrors(response, options)){
			sc.plat.RequestUtils.handleAjaxErrors(response, options);
		}else{
			Ext.callback(options.configParams.success, options.configParams.scope, [response, options]);
		}
	}
}

//Global variable for ajaxutil
//smaAjaxUtils = sc.sma.AjaxUtils; 
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.ns("sc.sma");


sc.sma.SmaUtil = function(){
	var pageId = null;
	return {
		getContextSensitiveHelpURL: function(context) {
			if(!context){
				context = 'sma_userguide';
			}
			//var url = sc.plat.info.Application.getHelpContextPath() + "/wwhelp/wwhimpl/common/html/wwhelp.htm" + "?context=" + context + "&topic=" + viewID;
			var url = sc.plat.info.Application.getHelpContextPath() + sc.plat.info.Application.getHelpURL() + "?context="+context;
			return url;
		},
		
		setPageId:function(id){
			pageId = id;
		},
		
		getPageId:function(){
			return pageId;
		},
		
		loadHelpWindow:function(){
			//Will use this code if we need to provide context sensetive help
			//url = this.getContextSensitiveHelpURL();
			//var win = window.open(url,"System_Management_Admin_Online_Documentation","height=500,width=500,left=200,top=200,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes");
			//win.focus();
			//Invoking platform function
			var activeHelpUrl = sc.plat.info.Application.getActiveHelpURL();
			var win = window.open(activeHelpUrl,"WWHFrame","height=500,width=500,left=200,top=200,status=yes,toolbar=no,menubar=no,location=no,resizable=yes,scrollbars=yes");
			win.focus();
		}
	}
}(); 
