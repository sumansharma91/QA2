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

sc.sma.dashboardUIConfig = function() {
	return {
		xtype : "screen",
		sciId : "screen",
		header : false,
		layout : "fit",
		autoScroll : true,
		items : [{
			xtype : "panel",
			sciId : "dashboardPanel",
			title : this.b_dashBoard,
			layout : "table",
			autoWidth : true,
			items : [{
				xtype : "grid",
				sciId : "processStsGrid",
				title : this.b_listPrSts,
				//minColumnWidth : 150,
				columns : [{
							defid : "grid-column",
							sciId : "dateCol",
							header : this.b_grdStrtTime,
							//width : 120,
							sortable : true,
							align: 'left',
							dataIndex : "CreateTS",
							scuiDataType : "TimeStamp",
							bindingData : {
								defid : "object",
								tAttrBinding : "CreateTS",
								sFieldConfig : {
									defid : "object",
									mapping : "CreateTS"
								}
							}
							
						}, {
							defid : "grid-column",
							sciId : "processNameCol",
							header : this.b_grdPrName,
							//width : 120,
							sortable : true,
							dataIndex : "ProcessName",
							bindingData : {
								defid : "object",
								tAttrBinding : "ProcessName",
								sFieldConfig : {
									defid : "object",
									mapping : "ProcessName"
								}
							}
						}, {
							defid : "grid-column",
							sciId : "stsKeyCol",
							header : this.b_prStsKey,
							sortable : true,
							//width : 120,
							dataIndex : "ProcessStatusKey",
							bindingData : {
								defid : "object",
								tAttrBinding : "ProcessStatusKey",
								sFieldConfig : {
									defid : "object",
									mapping : "ProcessStatusKey"
								}
							}
						}, {
							defid : "grid-column",
							sciId : "statusCol",
							header : this.b_grdStatus,
							//width : 120,
							sortable : true,
							dataIndex : "Status",
							bindingData : {
								defid : "object",
								tAttrBinding : "Status",
								sFieldConfig : {
									defid : "object",
									mapping : "Status"
								}
							}
						}],
				viewConfig : {
					defid : "object",
					forceFit : true,
					autoFill : true
				},
				colspan : 1,
				width : 600,
				height : 800,
				bindingData : {
					defid : "object",
					targetBinding : ["getProcessStatusList:ProcessStatusList.ProcessStatus"],
					sourceBinding : "getProcessStatusList:ProcessStatusList.ProcessStatus"
				},
				tbar : ["->", {
							xtype : "tbbutton",
							sciId : "tbrefresh",
							text : this.b_btnRefresh,
							handler : this.refreshList,
							iconCls : "sma-refresh-button",
							tooltip : this.tt_refresh,
							scope : this
						}," ", "-"," ",{
							xtype : "tbbutton",
							sciId : "tbautorefresh",
							text : this.b_btnAutoRefresh,
							handler : this.enableAutoRefresh,
							enableToggle : true,
							tooltip : this.tt_autoRefresh,
							iconCls : "sma-autorefresh-icon",
							scope : this
						}]
			}, {
				xtype : "tabpanel",
				sciId : "tabpanel",
				layoutOnTabChange : true,
				activeTab : 0,
				height : 700,
				width : 600,
				items : [{
							xtype : "panel",
							tbar : ["->",{
								xtype : "tbbutton",
								sciId : "tbdndsynchdata",
								text : this.b_btnDownloadSynchData,
								tooltip : this.tt_downloadSynchData,
								handler : this.dndSynchData,
								iconCls : "sma-downscript-icon",
								scope : this
							}
							],
							sciId : "dataView",
							title : this.b_viewSyncData,
							layout : "fit",
							items : [{
										xtype : "textarea",
										sciId : "dataViewTextArea",
										readOnly : true
									}]
						}, {
							xtype : "panel",
							sciId : "logView",
							title : this.b_viewLogData,
							layout : "fit",
							tbar : ["->",{
								xtype : "tbbutton",
								sciId : "tbdndlogdata",
								text : this.b_btnDownloadLogs,
								tooltip : this.tt_downloadLog,
								handler : this.dndLogData,
								iconCls : "sma-downscript-icon",
								scope : this
							}
							],
							items : [{
										xtype : "textarea",
										sciId : "logViewTextArea",
										readOnly : true
									}]
						}, {
							xtype : "panel",
							autoScroll : true,
							sciId : "viewScripts",
							title : this.b_viewScriptData,
							layout : "fit",
							items : [{
								xtype : "grid",
								sciId : "scriptsGrid",
								title : this.b_grdDbVerifyScript,
								bindingData : {
									defid : "object",
									targetBinding : ["DbVerifyScripts:DbVerifyScript"],
									sourceBinding : "DbVerifyScripts:DbVerifyScript"
								},
								viewConfig : {
									defid : "object",
									forceFit : true,
									autoFill : true
								},
								tbar : ["->",{
										xtype : "tbbutton",
										sciId : "tbViewScript",
										iconCls : "sma-viewscript-button",
										text : this.b_btnScriptView,
										tooltip : this.tt_viewScript,
										handler : this.viewScript,
										scope : this
								}," ","-"," ",{
									xtype : "tbbutton",
									sciId : "tbdndScript",
									iconCls : "sma-downscript-icon",
									text : this.b_btnDownloadScript,
									tooltip : this.tt_downloadScript,
									handler : this.dndScript,
									scope : this
								
								}," ","-"," ",{
									xtype : "tbbutton",
									sciId : "tbdndAllScript",
									iconCls : "sma-downallscript-icon",
									text : this.b_btnDownloadAllScripts,
									tooltip : this.tt_downloadAllScripts,
									handler : this.dndAllScript,
									scope : this
								
								}],
								columns : [{
											defid : "grid-column",
											sciId : "scriptType",
											header : this.b_grdColScriptType,
											sortable : true,
											dataIndex : "ScriptType",
											bindingData : {
												defid : "object",
												tAttrBinding : "ScriptType",
												sFieldConfig : {
													defid : "object",
													mapping : "ScriptType"
												}
											}
										}, {
											defid : "grid-column",
											sciId : "hasData",
											header : this.b_grdColScriptHasData,
											sortable : true,
											dataIndex : "HasData",
											bindingData : {
												defid : "object",
												tAttrBinding : "HasData",
												sFieldConfig : {
													defid : "object",
													mapping : "HasData"
												}
											}
										}, {
											defid : "grid-column",
											sciId : "scriptName",
											header : this.b_grdColScriptName,
											sortable : true,
											dataIndex : "ScriptName",
											bindingData : {
												defid : "object",
												tAttrBinding : "ScriptName",
												sFieldConfig : {
													defid : "object",
													mapping : "ScriptName"
												}
											}
										}]
							}]
							
						}]
			}],
			height : 800,
			layoutConfig : {
				defid : "tableLayoutConfig",
				columns : 2
			}
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

sc.sma.dashboard = function(config) {
	sc.sma.dashboard.superclass.constructor.call(this, config);

}
Ext.extend(sc.sma.dashboard, sc.plat.ui.ExtensibleScreen, {
	className: 'sc.sma.dashboard',
	getUIConfig: sc.sma.dashboardUIConfig,
	namespaces: {
		target: ["getProcessStatusList","DbVerifyScripts","getScriptDetails"],
		source: ["getProcessStatusList","DbVerifyScripts","getScriptDetails"]
	},
	namespacesDesc: {
		targetDesc: ['getProcessStatusList'],
		sourceDesc: ['getProcessStatusList']
	},
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	apiData : null ,
	refreshCounter : 0,
	ttinit : false,
	autorefresh : false,
	
	populateDashboard: function(pslres){
		this.setModel(pslres,"getProcessStatusList");
		this.initDashboard(pslres);
	},
	
	refreshList : function(){
		this.refreshCounter = 0;
		var ProcessStatus = {};
		sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "refreshDashboard",
					inputNS : "refreshDashboard",
					inputObj : {"refreshDashboard": ProcessStatus},
					extraParams : "",
					success : this.handleRefresh,
					scope : this
				});
	},
	
	getSelectedTableRowObj: function() {
        var gridPnl = this.get('processStsGrid');
        var rows = gridPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length === 0) {
            return null;
        }
        return rows;
    },
	
	initDashboard : function(pslres){
		var gridPnl = this.get('processStsGrid');
		var store = gridPnl.getStore();
		if(store){
			store.setDefaultSort('ProcessStatusKey','DESC');
			store.sort('ProcessStatusKey','DESC');
		}
		this.apiData = pslres;
		var selModel = gridPnl.getSelectionModel();
		selModel.selectFirstRow();
		var rows = gridPnl.getSelectionModel().getSelections();
		var firstRow = rows[0];
		if(firstRow){
			var stsKey = firstRow.data.ProcessStatusKey;
			var dataTA = this.get('dataViewTextArea');
			var processStatus = this.processRecord(stsKey);
			dataTA.setValue(processStatus.ProcessData);
			var logTA = this.get('logViewTextArea');
			logTA.setValue(processStatus.ProcessLog);
			this.populateScriptsTab(processStatus);
		}
		this.setListeners(gridPnl);
		this.setListenersOnScriptTab();
		this.initTimerTask();
	},
	
	setListenersOnScriptTab : function(){
		var scrPnl = this.get('viewScripts');
		scrPnl.addListener('activate',this.scriptPnlActivated,this);
	},
	scriptPnlActivated : function(){
		this.initScriptGrd();
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

	initScriptGrd : function(){
		this.setListenerOnScriptGrid();
		var gridPnl = this.get('scriptsGrid');
		var toolbar = gridPnl.getTopToolbar();
		var viewButton = this.getButton(toolbar,'tbViewScript');
		var dScButton = this.getButton(toolbar,'tbdndScript');
		var selModel = gridPnl.getSelectionModel();
		selModel.selectFirstRow();
		var rows = selModel.getSelections();
		var firstRow = rows[0];
		if(!firstRow){
			toolbar.setDisabled(true);	
			return;
		}
		var dndAllButton = this.getButton(toolbar,'tbdndAllScript');
		scriptName = firstRow.data.ScriptName;
		var mrows = this.getSelectedTableRowObj();
		var mrow = mrows[0];
		var stsKey = mrow.data.ProcessStatusKey ;
		var processStatus = this.processRecord(stsKey);
		if (this.processHasData(processStatus.DbVerifyScripts))
		{
			dndAllButton.enable();
		}	
		var hasData = this.hasScriptData(scriptName,processStatus.DbVerifyScripts);
		if (hasData == "true")
		{
			var boolHasData = true;
		}else{
			boolHasData = false;
		}
		if(boolHasData){
				dScButton.disable();
				viewButton.disable();
		}else{
				
		}
	},
	
	setListenerOnScriptGrid : function(){
		var grid = this.get('scriptsGrid');
		//grid.getSelectionModel.on('rowselect',this.scriptRowClickHandler,this);
		var sm = grid.getSelectionModel();
		sm.on('rowselect',this.scriptRowClickHandler,this);
	},
	
	scriptRowClickHandler : function(){
		var gridPnl = this.get('scriptsGrid');
		var toolbar = gridPnl.getTopToolbar();
		var viewButton = this.getButton(toolbar,'tbViewScript');
		var dScButton = this.getButton(toolbar,'tbdndScript');
        var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			//Now get processStatusKey
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var stsKey = mrow.data.ProcessStatusKey ;
			var processStatus = this.processRecord(stsKey);
			var scriptName = row.data.ScriptName;
			var scriptData = this.hasScriptData(scriptName,processStatus.DbVerifyScripts);
			if (scriptData == "true")
			{
				var boolscriptData = true;
			}else{
				boolscriptData = false;
			}
			if(boolscriptData){
				// toolbar.setDisabled(false);
				dScButton.enable();
				viewButton.enable();
			}else{
				dScButton.disable();
				viewButton.disable();
			}
		}else{
			//toolbar.setDisabled(true);
			dScButton.disable();
			viewButton.disable();
		}

	},
	populateScriptsTab : function(processStatus){
		if(!processStatus){
			return;
		}
		var scripts = processStatus.DbVerifyScripts;
		this.setModel(scripts,"DbVerifyScripts",{
			clearOldVals: true
        });
	},
	
	setListeners : function(gridPnl){
//		gridPnl.addListener('rowclick',this.rowClickHandler,this);
		var sml = gridPnl.getSelectionModel();
		sml.on('rowselect',this.rowClickHandler,this);
		
	},

	rowClickHandler : function(){
		var rows = this.getSelectedTableRowObj();
		var row = rows[0];
		var stsKey = row.data.ProcessStatusKey ;
		var processStatus = this.processRecord(stsKey);
		if(processStatus){
			var dataTA = this.get('dataViewTextArea');
			dataTA.setValue(processStatus.ProcessData);
			var logTA = this.get('logViewTextArea');
			logTA.setValue(processStatus.ProcessLog);
			this.populateScriptsTab(processStatus);
			var tabPnl = this.get('tabpanel');
			var dataTab = this.get('dataView');
			tabPnl.setActiveTab(dataTab);
		}
	},
	
	processRecord : function(stsKey){
		var totalRecords = this.apiData.ProcessStatusList.ProcessStatus.length;
		if(!totalRecords){
			return;
		}
		for(var i =0; i< totalRecords; i++){
			var processStatus = this.apiData.ProcessStatusList.ProcessStatus[i];
			if(processStatus.ProcessStatusKey == stsKey){
				return processStatus;
			}
		}
	},

	getDBVScriptObj : function(stsKey, scriptName){
		var totalRecords = this.apiData.ProcessStatusList.ProcessStatus.length;
		if(!totalRecords){
			return;
		}
		var processStatus = null;
		for(var i =0; i< totalRecords; i++){
			var processStatus = this.apiData.ProcessStatusList.ProcessStatus[i];
			if(processStatus.ProcessStatusKey == stsKey){
				break;
			}
		}
		var scripts = processStatus.DbVerifyScripts;
		var totalRecords = scripts.DbVerifyScript.length;
		for(var i =0; i< totalRecords; i++){
			var script = scripts.DbVerifyScript[i];
			if(script.ScriptName == scriptName){
				return script;
			}
		}
		
	},
	
	viewScript : function(){
		var gridPnl = this.get('scriptsGrid');
        var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			//Now get processStatusKey
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var stsKey = mrow.data.ProcessStatusKey ;
			var processStatus = this.processRecord(stsKey);
			var scriptName = row.data.ScriptName;
		    var scriptObj = this.getDBVScriptObj(stsKey,scriptName);
			sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "getScriptDetails",
					inputNS : "getScriptDetails",
					inputObj : {"getScriptDetails": scriptObj},
					extraParams : "",
					success : this.handleScriptDetails,
					scope : this
				});
		}
	},

	handleScriptDetails : function(dtl, options){
		if (Ext.isEmpty(dtl.responseText)) {
            return;
        }
        var scDetail = Ext.util.JSON.decode(dtl.responseText);
		scriptData = scDetail.dtl.DbVerifyScript.ScriptData;

		if(scriptData){
				this.showScriptPopup(scriptData,scriptName);
			}
	},

	processHasData: function(scripts){
		var totalRecords = scripts.DbVerifyScript.length;
		for(var i =0; i< totalRecords; i++){
			var script = scripts.DbVerifyScript[i];
			if(script.HasData == "true"){
				return true;
			}
		}
		return false;
	},	

	getScriptData: function(scriptName,scripts){
		var totalRecords = scripts.DbVerifyScript.length;
		for(var i =0; i< totalRecords; i++){
			var script = scripts.DbVerifyScript[i];
			if(script.ScriptName == scriptName){
				return script.ScriptData;
			}
		}
	},

	hasScriptData: function(scriptName,scripts){
		var totalRecords = scripts.DbVerifyScript.length;
		for(var i =0; i< totalRecords; i++){
			var script = scripts.DbVerifyScript[i];
			if(script.ScriptName == scriptName){
				return script.HasData;
			}
		}
	},	

	showScriptPopup:function(scriptData,scriptName){
		var scriptView = new sc.sma.scriptView();
		var scriptWindowPopup = new Ext.Window({items:[scriptView],  modal:true},{scope:scriptView});
		scriptWindowPopup.title = this.b_scriptViewer;
		scriptWindowPopup.height = 500;
		scriptWindowPopup.width = 400;
		scriptWindowPopup.closable = true;
		scriptView.init(scriptData,scriptName);
		scriptView.parentWindow = this.scriptWindowPopup;
		scriptWindowPopup.show();
	},

	doRefresh : function(scope){
		if(!scope.autorefresh){
			return;
		}
		scope.refreshCounter = scope.refreshCounter + 1;
		if(scope.refreshCounter == 120){
			scope.refreshList();
		}
	},
	
	initTimerTask : function(){
		if(this.ttinit){
			return;
		}
		var refreshFun = this.doRefresh.createCallback(this);
		this.ttinit = true;
		setInterval(refreshFun,1000);
	},
	
	
	handleRefresh : function(result, options){
		var res = Ext.decode(result.responseText);
		if (Ext.isEmpty(res.res)) {
			return;
		}
		var dataS = res.res;
		this.populateDashboard(dataS);
	},

	dndScript : function(){
		var form = document.createElement('form');
        form.target = '_blank';
        document.body.appendChild(form);
		var params = this.crtDndScriptParams();
        sc.plat.FormUtils.request({
               url: '/' + sc.plat.info.Application.getApplicationContext() + '/' + 'FileDownloadServlet',
               'form': form,
               'params': params
         });
	},
	
	dndAllScript : function(){
		var form = document.createElement('form');
        form.target = '_blank';
        document.body.appendChild(form);
		var params = this.crtDndAllScriptParam();
        sc.plat.FormUtils.request({
               url: '/' + sc.plat.info.Application.getApplicationContext() + '/' + 'FileDownloadServlet',
               'form': form,
               'params': params
         });
	},

	dndSynchData : function(){
		var form = document.createElement('form');
        form.target = '_blank';
        document.body.appendChild(form);
		var params = this.crtsdParam();
        sc.plat.FormUtils.request({
               url: '/' + sc.plat.info.Application.getApplicationContext() + '/' + 'FileDownloadServlet',
               'form': form,
               'params': params
         });
	},
	
	dndLogData : function(){
		var form = document.createElement('form');
        form.target = '_blank';
        document.body.appendChild(form);
		var params = this.crtLogParam();
        sc.plat.FormUtils.request({
               url: '/' + sc.plat.info.Application.getApplicationContext() + '/' + 'FileDownloadServlet',
               'form': form,
               'params': params
         });
	},
	
	crtDndAllScriptParam : function(){
		var gridPnl = this.get('scriptsGrid');
		var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			//Now get processStatusKey
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var stsKey = mrow.data.ProcessStatusKey ;
			var processStatus = this.processRecord(stsKey);
			var scriptName = row.data.ScriptName;
			var params = {
						SMAFileDownloadInput : Ext.encode({
								'DBVerifyScript' : {
									'ScriptName': scriptName,
								 'ProcessStatusKey': stsKey
							},
							'TableName' : 'PLT_DBVERIFY_SCRIPT',
							'DownloadAllScripts' : 'true'
                    })
			};
			return params;
		}
	},

	crtDndScriptParams : function(){
		var gridPnl = this.get('scriptsGrid');
        var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			//Now get processStatusKey
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var stsKey = mrow.data.ProcessStatusKey ;
			var processStatus = this.processRecord(stsKey);
			var scriptName = row.data.ScriptName;
			var params = {
						SMAFileDownloadInput : Ext.encode({
								'DBVerifyScript' : {
									'ScriptName': scriptName,
								 'ProcessStatusKey': stsKey
							},
							'TableName' : 'PLT_DBVERIFY_SCRIPT'
                    })
			};
			return params;
		}
	},

	crtsdParam : function(){
		var mrows = this.getSelectedTableRowObj();
		var mrow = mrows[0];
		var stsKey = mrow.data.ProcessStatusKey ;
		var params = {
						SMAFileDownloadInput : Ext.encode({
								'ProcessStatus' : {
									'DownloadData': 'ProcessData',
									'ProcessStatusKey': stsKey
							},
							'TableName' : 'PLT_PROCESS_STATUS'
                    })
		};
		return params;
	},

	crtLogParam : function(){
		var mrows = this.getSelectedTableRowObj();
		var mrow = mrows[0];
		var stsKey = mrow.data.ProcessStatusKey ;
		var params = {
						SMAFileDownloadInput : Ext.encode({
								'ProcessStatus' : {
									'DownloadData': 'ProcessLog',
									'ProcessStatusKey': stsKey
							},
							'TableName' : 'PLT_PROCESS_STATUS'
                    })
		};
		return params;

	},

	enableAutoRefresh : function(){
		if(this.autorefresh){
			this.autorefresh = false;
		}else{
			this.refreshCounter = 0;
			this.autorefresh = true;
		}
	}
	
});
Ext.reg('dashboard', sc.sma.dashboard);

/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.scriptViewUIConfig = function() {
	return {xtype: "screen"
,sciId:  "scriptView"
,header: false
,layout:  "fit"
,autoScroll: true
,items: [{xtype: "panel"
,sciId:  "scriptViewPanel"
,title:  "this.b_pnlScriptView"
,layout:  "fit"
,height: 475
,autoScroll : true
,items: [{xtype: "textarea"
,sciId:  "scriptViewTextArea"}]}]};
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

sc.sma.scriptView = function(config) {
	sc.sma.scriptView.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.scriptView, sc.plat.ui.ExtensibleScreen, {
	className: 'sc.sma.scriptView',
	getUIConfig: sc.sma.scriptViewUIConfig,
	namespaces: {
		target: ['DbVerifyScripts'],
		source: ['DbVerifyScripts']
	},
	namespacesDesc: {
		targetDesc: ['DbVerifyScripts'],
		sourceDesc: ['DbVerifyScripts']
	},
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	init : function(scriptData,scriptName){
		var panel = this.get('scriptViewPanel');
		panel.setTitle(scriptName);
		var textArea = this.get('scriptViewTextArea');
		textArea.setValue(scriptData);
	}
});
Ext.reg('scriptView', sc.sma.scriptView);
