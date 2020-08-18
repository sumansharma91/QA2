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

Ext.namespace('sc.sma');

sc.sma.indexScreenUIConfig = function() {
	return {
		xtype : "screen",
		sciId : "indexScreen",
		header : false,
		layout : "anchor",
		autoScroll : true,
		items : [{
					xtype : "panel",
					sciId : "indexPnl",
					title : this.b_pnlIndexManagement,
					layout : "table",
					layoutConfig : {
						defid : "tableLayoutConfig",
						columns : 2
					},
					items : [{
								xtype : "label",
								sciId : "indexLbl",
								text : this.b_IndexName
							}, {
								xtype : "combo",
								sciId : "indexNameCombo",
								editable : false,
								allowBlank : false,
								emptyText : this.b_indexNameCmbEmptyText,
								displayField: "IndexNameDesc",
								valueField: "IndexName",
								mode: "local",
								triggerAction: "all",
								store: new Ext.data.JsonStore({
									defid: "jsonstore",
									fields: ["IndexName","IndexNameDesc"]
								}),
								bindingData: {
									defid: "object",
									optionsBinding: "getIndexNames:IndexList.Index",
									targetBinding: ["getIndexStatus:Index.IndexName"],
									sourceBinding: ["resetNS:IndexList.Index.IndexName"]
								},
								listeners: {
									defid: "listeners",
									'select': this.indexSelectHandler,
									scope: this
								}
							}]
				}, {
						xtype : "panel",
						sciId : "indexSatusPnl",
						title : this.b_pnlIndexStatus,
						collapsed : true,
						layout : "table",
						layoutConfig : {
							defid : "tableLayoutConfig",
							columns : 4
						},
						items : [{
									xtype : "label",
									sciId : "indexVersionLbl",
									text : this.b_IndexVersion
									
								}, {
									xtype : "textfield",
									sciId : "indexVersionTxtFld",
									readOnly: true,
									bindingData: {
										defid: "object",
										sourceBinding: "getIndexStatus:IndexStatus.IndexConfig.IndexVersion"
									}
								}, {
									xtype : "label",
									sciId : "updateReqdlbl",
									text : this.b_UpdateRequired
								}, {
									xtype : "textfield",
									sciId : "updReqdTxtFld",
									readOnly: true,
									bindingData: {
										defid: "object",
										sourceBinding: "getIndexStatus:IndexStatus.IndexConfig.IndexUpdateRequired"
									}
								}, {
									xtype : "label",
									sciId : "indexingWorkingLbl",
									text : this.b_IndexingWorking
								}, {
										xtype : "combo",
										sciId : "indexingWorkingCombo",
										displayField: "IndexStatusDesc",
										valueField: "IndexStatus",
										mode: "local",
										triggerAction: "all",
										width : 60,
										forceSelection:true,
										allowBlank : false,
										editable : false,
										store: new Ext.data.JsonStore({
											defid: "jsonstore",
											fields: ["IndexStatus","IndexStatusDesc"]
										}),
										bindingData: {
											defid: "object",
											optionsBinding: "getIndexStatusList:IndexStatusList",
											targetBinding: ["markIndexAvailibility:Index.IndexWorking"],
											sourceBinding: ["getIndexStatus:IndexStatus.IndexWorking","resetNS:IndexStatus.IndexWorking"]
										},
										listeners: {
											defid: "listeners",
											'select': this.iwsHandler,
											scope: this
										}
								}, {
									xtype : "label",
									sciId : "searchWorkingLbl",
									text : this.b_SearchWorking
								}, {
										xtype : "combo",
										sciId : "searchWorkingCombo",
										displayField: "IndexStatusDesc",
										valueField: "IndexStatus",
										mode: "local",
										triggerAction: "all",
										width : 60,
										allowBlank : false,
										editable : false,
										forceSelection:true,
										store: new Ext.data.JsonStore({
											defid: "jsonstore",
											fields: ["IndexStatus","IndexStatusDesc"]
										}),
										bindingData: {
											defid: "object",
											optionsBinding: "getIndexStatusList:IndexStatusList",
											targetBinding: ["markIndexAvailibility:Index.SearchWorking"],
											sourceBinding: ["getIndexStatus:IndexStatus.SearchWorking","resetNS:IndexStatus.SearchWorking"]
										},
										listeners: {
											defid: "listeners",
											'select': this.swsHandler,
											scope: this
										}
								}],
						collapsible : true,
						buttons : [{
									xtype : "button",
									sciId : "statusSaveBtn",
									text :  this.b_btnIndexStatusSave,
									handler : this.statusSaveHandler,
									scope : this,
									disabled : true
								}, {
									xtype : "button",
									sciId : "statusResetBtn",
									text :  this.b_btnIndexStatusReset,
									handler : this.statusResetHandler,
									scope : this,
									disabled : true
								}],
						tbar : ["->", {
									xtype : "tbbutton",
									sciId : "tbIndexTemplate",
									text : this.b_linkViewIndexTemplate,
									handler : this.viewTemplate,
									scope : this
								}
						]
				}, {
					xtype : "editorgrid",
					sciId : "indexSynchStatusGrd",
					collapsed : true,
					title : this.b_pnlIndexSynchStatus,
					viewConfig: {
						forceFit: true
					},
					selModel : this.cbsm,
					columns : [{
								defid : "grid-column",
								sciId : "entCodeCol",
								header : this.b_enterpriseCode,
								sortable : true,
								dataIndex: "EnterpriseCode",
								bindingData: {
									defid: "object",
									tAttrBinding: "EnterpriseCode",
									sFieldConfig: {
										defid: "object",
										mapping: "EnterpriseCode"
									}
								}
							}, {
								defid : "grid-column",
								sciId : "synchStatusCol",
								header : this.b_synchronized,
								sortable : true,
								dataIndex: "InSync",
								bindingData: {
									defid: "object",
									tAttrBinding: "InSync",
									sFieldConfig: {
										defid: "object",
										mapping: "InSync"
									}
								}
							}],
					clicksToEdit : 1,
					tbar : ["->", {
								xtype : "tbbutton",
								sciId : "markInSynchBtn",
								text : this.b_btnMarkSynchronized,
								handler : this.markInSynch,
								scope : this,
								disabled : true
							}, " ", "-", " ", {
								xtype : "tbbutton",
								sciId : "markNotInSynchBtn",
								text : this.b_btnMarkNotSynchronized,
								handler : this.markNotInSynch,
								scope : this,
								disabled : true
							}, " ", "-", " ", {
								xtype : "tbbutton",
								sciId : "markAllIInSynch",
								text : this.b_btnMarkAllSynchronized,
								handler : this.markAllInSynch,
								scope : this
							}, " ", "-", " ", {
								xtype : "tbbutton",
								sciId : "markAllNotInSynch",
								text : this.b_btnMarkAllNotSynchronized,
								handler : this.markAllNotInSynch,
								scope : this
							}],
					collapsible : true,
					bindingData: {
						defid: "object",
						targetBinding: ["getIndexStatus:IndexStatus.IndexSyncForEnterprises.IndexSync"],
						sourceBinding: "getIndexStatus:IndexStatus.IndexSyncForEnterprises.IndexSync"
					},
					height: 400
				}]
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

sc.sma.indexScreen = function(config) {
	sc.sma.indexScreen.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.indexScreen, sc.plat.ui.ExtensibleScreen, {
	className: 'sc.sma.indexScreen',
	getUIConfig: sc.sma.indexScreenUIConfig,
	disableWorkingCombo : false,
	workingComboHidden : false,
	indexNameMap : new Object(),
	ynMap : new Object(),
	cbsm: new Ext.grid.RowSelectionModel({
        singleSelect: true
    }),
	 namespaces: {
		source: ["resetNS","getIndexNames",
			"getIndexStatus","markIndexAvailibility","getIndexStatusList"],
        target: ["resetNS","getIndexNames",
			"getIndexStatus","markIndexAvailibility"]
    },
	
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	initIndexScreen : function(inres,ffProp,yStr,nStr){
		this.ynMap['Y'] = yStr;
		this.ynMap['N'] = nStr;
		for(var i=0;i<inres.IndexList.Index.length;i++){
			var iObj = inres.IndexList.Index[i];
			this.indexNameMap[iObj.IndexName] = iObj.IndexNameDesc;
		}
		if(inres){
			this.populateIndexNamesComboBox(inres);
		}
		if(!Ext.isEmpty(ffProp) && !ffProp){
			this.disableWorkingCombo  = true;
		}
		var statusPnl = this.get('indexSatusPnl');
		statusPnl.setDisabled(true);
		var synchPnl = this.get('indexSynchStatusGrd');
		synchPnl.setDisabled(true);
		var gridPnl = this.get('indexSynchStatusGrd');
		//gridPnl.getSelectionModel.on('rowselect',this.synchRowSelect,this);
		this.cbsm.on('rowselect',this.synchRowSelect,this);
	},
	
	populateIndexNamesComboBox : function(inres){
		this.setModel(inres,"getIndexNames");
	},
	
	indexSelectHandler : function(){
		var res = this.getTargetModel("getIndexStatus");
		var newres = res[0].Index;
		if(Ext.isEmpty(newres.IndexName)){
			return;
		}
		var ret = {
			'IndexName' : ''
		};
		ret.IndexName = newres.IndexName;
		
		if(this.sbEnabled){
			var fcn = function(btn){
				if (btn == 'yes') {
					sc.sma.AjaxUtils.request({
						actionNS : "sma",
						action : "getIndexStatus",
						inputNS : "getIndexStatus",
						inputObj : {"getIndexStatus":ret},
						extraParams : "",
						success : this.getStatusResHandler,
						scope : this
					});
				}else{
					//Go back to old value
					var nameCombo = this.get('indexNameCombo');
					nameCombo.setValue(this.indexName);
				}
			};
			Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_manage_new_index, fcn, this);
		}else{
			sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getIndexStatus",
				inputNS : "getIndexStatus",
				inputObj : {"getIndexStatus":ret},
				extraParams : "",
				success : this.getStatusResHandler,
				scope : this
			});
			
		}
	},
	statusOut : null,
	sbEnabled : false,
	origWorkingVals : {
		'IndexWorking' : '',
		'SearchWorking' : ''
	},
	indexTemplateData : null,
	indexName : null,
	indexNameDesc : null,
	getStatusResHandler : function(result, options){
		this.enableStatusButton(false);
		var statusPnl = this.get('indexSatusPnl');
		statusPnl.setDisabled(false);
		statusPnl.expand(true);
		var synchPnl = this.get('indexSynchStatusGrd');
		synchPnl.setDisabled(false);
		synchPnl.expand(true);
		var res = Ext.decode(result.responseText);
		if (Ext.isEmpty(res.res)) {
			sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
			//return; Do not return set page with no record
		}else{
			//If already there is a screen title remove that..
			sc.plat.ScreenTitle.setMsg(null);
		}
		var dataS = res.res;
		this.origWorkingVals.IndexWorking = dataS.IndexStatus.IndexWorking;
		this.origWorkingVals.SearchWorking = dataS.IndexStatus.SearchWorking;
		this.indexTemplateData = dataS.IndexStatus.IndexConfig.IndexTemplate;
		this.indexName = dataS.IndexStatus.IndexName;
		this.statusOut = dataS;
		this.setIndexStatusModel(dataS);
		//var nameTxtBox = this.get('indexNameTxtFld');
		var updRegTxtBox = this.get('updReqdTxtFld');
		var versionTxtBox = this.get('indexVersionTxtFld');
		//nameTxtBox.disable();
		updRegTxtBox.disable();
		versionTxtBox.disable();
		if(this.disableWorkingCombo && !this.workingComboHidden){
			this.hideWorkingCombo();
		}
		var toolbar = synchPnl.getTopToolbar();
		var markInSynchBtn = this.getButton(toolbar,'markInSynchBtn');
		var markNotInSynchBtn = this.getButton(toolbar,'markNotInSynchBtn');
		markInSynchBtn.disable();
		markNotInSynchBtn.disable();
	},
	
	hideWorkingCombo : function(){
		var indexWorkingLbl = this.get('indexingWorkingLbl');
		var indexWorkingCombo = this.get('indexingWorkingCombo');
		var searchWorkingLbl = this.get('searchWorkingLbl');
		var searchWorkingCombo = this.get('searchWorkingCombo');
		indexWorkingLbl.hide();
		indexWorkingCombo.hide();
		searchWorkingLbl.hide();
		searchWorkingCombo.hide();
		this.workingComboHidden = true;
	},
	setIndexStatusModel:function(dataS){
		var comboList = {
			"IndexStatusList" :
				[
				{"IndexStatus":"Y","IndexStatusDesc" : this.ynMap['Y']},
				{"IndexStatus":"N","IndexStatusDesc" : this.ynMap['N']}
				]
		};
		this.setModel(comboList,"getIndexStatusList"); 
		
		this.setModel(dataS, "getIndexStatus", {
			clearOldVals: true
        });
		
	
	},
	
	synchRowSelect : function(sm,index,r){
		var gridPnl = this.get('indexSynchStatusGrd');
		var toolbar = gridPnl.getTopToolbar();
		var markInSynchBtn = this.getButton(toolbar,'markInSynchBtn');
		var markNotInSynchBtn = this.getButton(toolbar,'markNotInSynchBtn');
		if(r.data.InSync == this.ynMap['N']){
			markInSynchBtn.enable();
			markNotInSynchBtn.disable();
		}else{
			markInSynchBtn.disable();
			markNotInSynchBtn.enable();
		}
	},
	
	getButton : function(tb,bid){
		var ic = tb.items.items;
		var i =0;
		for(;i<ic.length;i++){
			var b = ic[i];
			var sid = b.sciId;
			if(!sid || sid !== bid){
				continue;
			}else{
				return b;
			}
		}
	},
	
	iwsHandler : function(combo,record,index){
		if(record.data.IndexStatus == this.origWorkingVals.IndexWorking){
			//do nothing
		}else{
			this.enableStatusButton(true);
		}
	},
	
	swsHandler : function(combo,record,index){
		if(record.data.IndexStatus == this.origWorkingVals.SearchWorking){
			//do nothing
		}else{
			this.enableStatusButton(true);
		}
	},
	
	enableStatusButton : function(enable){
		if(this.sbEnabled && enable){
			return;
		}else if(!this.sbEnabled && !enable){
			return;
		}
		var saveButton = this.get('indexSatusPnl').buttons[0];
		var resetButton = this.get('indexSatusPnl').buttons[1];
		if(enable){
			saveButton.enable();
			resetButton.enable();
		}else{
			saveButton.disable();
			resetButton.disable();
		}
		if(enable){
			this.sbEnabled = true;
		}else{
			this.sbEnabled = false;
		}
	},
	
	viewTemplate : function(){
		var templateView = new sc.sma.templateView();
		var templateWindowPopup = new Ext.Window({items:[templateView],  modal:true},{scope:templateView});
		templateWindowPopup.title = this.b_IndexTemplateViewer;
		templateWindowPopup.height = 500;
		templateWindowPopup.width = 400;
		templateWindowPopup.closable = true;
		templateView.init(this.indexTemplateData,this.indexNameMap[this.indexName]);
		templateView.parentWindow = this.templateWindowPopup;
		templateWindowPopup.show();
	},
	statusResetHandler : function(){
		this.doStatusReset();
	},
	
	doStatusReset:function(){
		sc.plat.DataManager.setModel({},
			"resetNS", {
				clearOldVals: true
        });
		//We want to protect other 
		this.setIndexStatusModel(this.statusOut);
		this.enableStatusButton(false);
		var nameCombo = this.get('indexNameCombo');
		nameCombo.setValue(this.indexName);
	},
	
	statusSaveHandler : function(){
		var tm = this.getTargetModel('markIndexAvailibility');
		var input = {
			'IndexWorking' : '',
			'SearchWorking': '',
			'IndexName':''
		};
		var fcn = function(btn){
				if (btn == 'yes') {
					input.IndexWorking = tm[0].Index.IndexWorking;
					input.SearchWorking = tm[0].Index.SearchWorking;
					input.IndexName = this.indexName;
					this.origWorkingVals.IndexWorking = tm[0].Index.IndexWorking;
					this.origWorkingVals.SearchWorking = tm[0].Index.SearchWorking;
					this.statusOut.IndexStatus.IndexWorking = tm[0].Index.IndexWorking;
					this.statusOut.IndexStatus.SearchWorking = tm[0].Index.SearchWorking;
					this.enableStatusButton(false);
					sc.sma.AjaxUtils.request({
									actionNS : "sma",
									action : "markIndexAvailibility",
									inputNS : "markIndexAvailibility",
									inputObj : {"markIndexAvailibility":input},
									extraParams : "",
									success : this.saveHandler,
									scope : this
					});
				}	
		};
		Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_index_status_save, fcn, this);
	},
	
	saveHandler : function(result, options){
		//Now get back to old status
		var res = Ext.decode(result.responseText);
		var dataS = res.res;
	},
	
	markInSynch : function(){
		var entCode = this.getEnterpriseCode();
		if(Ext.isEmpty(entCode)){
			return;
		}
		var fcn = function(btn){
				if (btn == 'yes') {
					var input = {
						'IndexName' : null,
						'MarkInSyncForEnterprise' : null					
					};
					input.IndexName = this.indexName;
					input.MarkInSyncForEnterprise = entCode;
					sc.sma.AjaxUtils.request({
									actionNS : "sma",
									action : "markIndexAvailibility",
									inputNS : "markIndexAvailibility",
									inputObj : {"markIndexAvailibility":input},
									extraParams : "",
									success : this.misHandler,
									scope : this
					});
				}	
		};
		Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_mark_synchronized, fcn, this);
	},
	
	markNotInSynch : function(){
		var entCode = this.getEnterpriseCode();
		if(Ext.isEmpty(entCode)){
			return;
		}
		var fcn = function(btn){
				if (btn == 'yes') {
					var input = {
						'IndexName' : null,
						'MarkNotInSyncForEnterprise' : null					
					};
					input.IndexName = this.indexName;
					input.MarkNotInSyncForEnterprise = entCode;
					sc.sma.AjaxUtils.request({
									actionNS : "sma",
									action : "markIndexAvailibility",
									inputNS : "markIndexAvailibility",
									inputObj : {"markIndexAvailibility":input},
									extraParams : "",
									success : this.mnisHandler,
									scope : this
					});
				}	
		};
		Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_mark_not_synchronized, fcn, this);
	},
	
	markAllInSynch : function(){
		var fcn = function(btn){
				if (btn == 'yes') {
					var input = {
						'IndexName' : null,
						'MarkInSyncForAllEnterprises' : 'Y'					
					};
					input.IndexName = this.indexName;
					sc.sma.AjaxUtils.request({
									actionNS : "sma",
									action : "markIndexAvailibility",
									inputNS : "markIndexAvailibility",
									inputObj : {"markIndexAvailibility":input},
									extraParams : "",
									success : this.maisHandler,
									scope : this
					});
				}	
		};
		Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_mark_all_synchronized, fcn, this);
	},
	
	markAllNotInSynch : function(){
		var fcn = function(btn){
				if (btn == 'yes') {
					var input = {
						'IndexName' : null,
						'MarkNotInSyncForAllEnterprises' : 'Y'					
					};
					input.IndexName = this.indexName;
					sc.sma.AjaxUtils.request({
									actionNS : "sma",
									action : "markIndexAvailibility",
									inputNS : "markIndexAvailibility",
									inputObj : {"markIndexAvailibility":input},
									extraParams : "",
									success : this.manisHandler,
									scope : this
					});
				}	
		};
		Ext.Msg.confirm(this['b_Confirm'],this.b_confirm_mark_all_not_synchronized, fcn, this);
	},
	
	getEnterpriseCode : function(){
		var mrows = this.getSelectedTableRowObj();
		if(Ext.isEmpty(mrows)){
			return null;
		}
		var mrow = mrows[0];
		if(Ext.isEmpty(mrow)){
			return null;
		}
		return mrow.data.EnterpriseCode;
	
	},
	
	getSelectedTableRowObj: function() {
        var gridPnl = this.get('indexSynchStatusGrd');
        var rows = gridPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length === 0) {
            return null;
        }
        return rows;
    },
	
	misHandler : function(result, options){
		var res = Ext.decode(result.responseText);
		var dataS = res.res;
		this.statusOut.IndexStatus.IndexSyncForEnterprises = this.massageOutput(dataS.Index.IndexSyncForEnterprises);
		this.doStatusReset();
	},
	
	mnisHandler : function(result,options){
		var res = Ext.decode(result.responseText);
		var dataS = res.res;
		this.statusOut.IndexStatus.IndexSyncForEnterprises = this.massageOutput(dataS.Index.IndexSyncForEnterprises);
		this.doStatusReset();
	},
	
	maisHandler : function(result,options){
		var res = Ext.decode(result.responseText);
		var dataS = res.res;
		this.statusOut.IndexStatus.IndexSyncForEnterprises = this.massageOutput(dataS.Index.IndexSyncForEnterprises);
		this.doStatusReset();
	},
	
	manisHandler : function(result,options){
		var res = Ext.decode(result.responseText);
		var dataS = res.res;
		this.statusOut.IndexStatus.IndexSyncForEnterprises = this.massageOutput(dataS.Index.IndexSyncForEnterprises);
		this.doStatusReset();
	},
	
	massageOutput : function(sfe){
		for(var i=0;i<sfe.IndexSync.length;i++){
			sfe.IndexSync[i].InSync = this.ynMap[sfe.IndexSync[i].InSync];
		}
		return sfe;
	}
	
	
});
Ext.reg('index', sc.sma.indexScreen);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.templateViewUIConfig = function() {
	return {xtype: "screen"
	,sciId:  "templateView"
	,header: false
	,layout:  "fit"
	,autoScroll: true
	,items: [
		{ xtype: "panel"
		,sciId:  "templateViewPanel"
		,title:  this.b_IndexTemplateViewer
		,layout:  "fit"
		,height: 475
		,autoScroll : true
		,items: [{xtype: "textarea"
				,sciId:  "templateViewTextArea"
				,readOnly : true}]
		}]	
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

sc.sma.templateView = function(config) {
	sc.sma.templateView.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.templateView, sc.plat.ui.ExtensibleScreen, {
	className: 'sc.sma.templateView',
	getUIConfig: sc.sma.templateViewUIConfig,
	namespaces: {
		target: ['getIndexStatus'],
		source: ['getIndexStatus']
	},
	namespacesDesc: {
		targetDesc: ['getIndexStatus'],
		sourceDesc: ['getIndexStatus']
	},
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	init : function(templateData,indexName){
		var panel = this.get('templateViewPanel');
		panel.setTitle(indexName);
		var textArea = this.get('templateViewTextArea');
		textArea.setValue(templateData);
	}
});
Ext.reg('templateView', sc.sma.templateView);
