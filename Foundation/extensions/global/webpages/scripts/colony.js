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

sc.sma.colonyUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "pnlColonySearch",
            title: this.b_pnlColonySearch,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "colonyIdLbl",
                text: this.b_colonyId
            },
            {
                xtype: "combo",
                sciId: "colIdQryTypeCombo",
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                mode: "local",
                triggerAction: "all",
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getColonyList:Colony.ColonyIdQryType"],
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType"
                }
            },
            {
                xtype: "textfield",
                sciId: "colonyIdTxtFld",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getColonyList:Colony.ColonyId"],
                    sourceBinding: ["resetNS:Colony.ColonyId"]
                }
            },
            {
                xtype: "label",
                sciId: "databasePoolLbl",
                text: this.b_colonyPool
            },
            {
                xtype: "combo",
                sciId: "databasepoolCombo",
                displayField: "PoolId",
                valueField: "PoolId",
                mode: "local",
                triggerAction: "all",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getDBPoolListOutput:DBPoolList.DBPool",
                    sourceBinding: ["resetNS:ColonyList.ColonyPoolList.ColonyPool.PoolId"],
                    targetBinding: ["getColonyList:Colony.ColonyPoolList.ColonyPool.PoolId"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["PoolId"]
                })
            },
            {
                xtype: "label",
                sciId: "colonyPrefixLbl",
                text: this.b_pkPrefix
            },
            {
                xtype: "combo",
                sciId: "prefixQryTypeCombo",
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                mode: "local",
                triggerAction: "all",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getColonyList:Colony.PkPrefixQryType"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                })
            },
            {
                xtype: "textfield",
                sciId: "prefixTxtFld",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getColonyList:Colony.PkPrefix"],
                    sourceBinding: ["resetNS:Colony.PkPrefix"]
                }
            },
            {
                xtype: "label",
                sciId: "tableTypeLabel",
                text: this.b_tableType
            },
            {
                xtype: "combo",
                sciId: "tableTypeCombo",
                displayField: "TableType",
                valueField: "TableType",
                mode: "local",
                triggerAction: "all",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getSupportedTableTypeListOutput:TableTypes.TableType",
                    sourceBinding: ["resetNS:ColonyList.ColonyPoolList.ColonyPool.TableType"],
                    targetBinding: ["getColonyList:Colony.ColonyPoolList.ColonyPool.TableType"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["TableType"]
                })
            },
            {
                xtype: "label",
                sciId: "versionLabel",
                text: this.b_colonyVersion
            },
            {
                xtype: "combo",
                sciId: "versionQryTypeCombo",
                mode: "local",
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                triggerAction: "all",
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getColonyList:Colony.VersionQryType"]
                }
            },
            {
                xtype: "textfield",
                sciId: "versionTxtField",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getColonyList:Colony.ColonyVersion"],
                    sourceBinding: ["resetNS:Colony.ColonyVersion"]
                }
            }],
            border: false,
            buttons: [
			{
                xtype: "button",
                sciId: "colonySearchButton",
                text: this.b_Search,
                handler: this.doSearch,
                scope: this,
                iconCls: "sma-search-button"
            },
            {
                xtype: "button",
                sciId: "colonySearchReset",
                text: this.b_Reset,
                handler: this.doReset,
                scope: this,
                iconCls: "sma-reset-button"
            }],
            iconCls: "sma-colonysearch-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 5
            }
        },
        {
            xtype: "grid",
            sciId: "grdpnlColony",
            title: this.b_grdpnlColonytitle,
            viewConfig: {
				forceFit: true
		    },
          
            columns :[{
                defid: "grid-column",
                sciId: "colColonyID",
                header: this.b_colColonyID,
                sortable: true,
                dataIndex: "ColonyId",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "ColonyId",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ColonyId"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colPrefix",
                header: this.b_colPrefix,
                sortable: true,
                dataIndex: "PkPrefix",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "PkPrefix",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PkPrefix"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colVersion",
                header: this.b_colVersion,
                sortable: true,
                dataIndex: "ColonyVersion",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "ColonyVersion",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ColonyVersion"
                    }
                }
            }],
            height: 400,
            tbar: ["->", 
			 {
				xtype: "tbbutton",
                sciId: "tbcolonySynch",
                text: this.b_tbsynchronize,
                handler: this.doColonySynchronize,
                scope: this,
                iconCls: "sma-synchronizer-button"
			},
			" ", "-", " ",{
                xtype: "tbbutton",
                sciId: "tbcreateColony",
                handler: this.doCallCreateColony,
                scope: this,
                iconCls: "sma-create-button",
                tooltip: this.tbcreateColony,
                text: this.b_tbcreateColony
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbcolonyDetails",
                handler: this.doColonyDetails,
                scope: this,
                iconCls: "sma-detail-button",
                tooltip: this.tbcolonyDetails,
                text: this.b_tbcolonyDetails
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbcolonyDelete",
                handler: this.handleDelete,
                scope: this,
                iconCls: "sma-delete-button",
                tooltip: this.tbcolonyDelete,
                text: this.b_tbcolonyDelete
            }],
			selModel: this.cbsm,
            iconCls: "sma-colony-icon",
            bindingData: {
                defid: "object",
                targetBinding: ["getColonyList:ColonyList.Colony"],
                sourceBinding: "getColonyList:ColonyList.Colony"
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

sc.sma.colony = function(config) {
    sc.sma.colony.superclass.constructor.call(this, config);
};
Ext.extend(sc.sma.colony, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.colony',
    getUIConfig: sc.sma.colonyUIConfig,
	listOutput: null,
    searchPnl: null,

	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	cbsm: new Ext.grid.RowSelectionModel({
        singleSelect: true
    }),

    namespaces: {
		source: ["resetNS","getColonyList",
			"getSupportedTableTypeListOutput","getColonyPoolListOutput","getQueryTypeListOutput","getDBPoolListOutput"],
        target: ["getColonyList", "mColony", "searchColonyList",
			"getSupportedTableTypeListOutput","getColonyPoolListOutput","getDBPoolListOutput"]
    },
    
	getSearchPnl: function() {
        if (this.searchPnl === null || this.searchPnl === undefined) {
            this.searchPnl = this.get('pnlColonySearch');
        }
        return this.searchPnl;
    },

	//populateColonyPoolComboBox: function(cplres){
	//	this.removeDuplicatePools(cplres);
	//	this.setModel(cplres, "getColonyPoolListOutput");
	//},
	
	//removeDuplicatePools:function(cplres){
	//	var newres = cplres.ColonyPoolList.ColonyPool;
	//	var i ;
	//	if(newres instanceof Array){
	//		for(i= 0; i < newres.length;i++){
	//			var j ;
	//			var iPoolStr = newres[i].PoolId;
	//			for(j=i+1;j<newres.length;j++){
	//				var jPoolStr = newres[j].PoolId;
	//				if(iPoolStr == jPoolStr){
	//					newres.splice(j,1);
	//					j = j-1;
	//				}
	//			}
	//		}
	//	}
	//},


	populateTableTypeComboBox: function(ttlres){
		this.setModel(ttlres,"getSupportedTableTypeListOutput");
	},
	
	populateQueryTypeComboBox:function(qryres){	
		this.getLocalizeQryTypeDesc(qryres);
		this.setModel(qryres,"getQueryTypeListOutput");
	},

	populateDBPoolComboBox: function(dblres){
		//Will handle it later...
		//this.removeDuplicatePools(cplres);
		this.setModel(dblres, "getDBPoolListOutput");
	},
	
	//This method will get localized query types..it should be moved to util
	getLocalizeQryTypeDesc:function(qryres){
		var stringQryTypes = qryres.QueryTypeList.StringQueryTypes.QueryType;

		// 1st Part : Localize String Query Types...

		if((stringQryTypes instanceof Array)){
			for(var i = 0; i < stringQryTypes.length;i++ ){
				var qryTypeObj = stringQryTypes[i];
				var qryType = qryTypeObj.QueryType;
				if(qryType == "EQ"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeEQ;
				}
				else if(qryType == "FLIKE"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeFLIKE;
				}
				else if(qryType == "LIKE"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeLIKE;
				}
			}
		}

	},
	
	resHandler: function(result, options) { 
		var res = Ext.decode(result.responseText);
		if (Ext.isEmpty(res.res)) {
			sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
			//return; Do not return set page with no record
		}else{
			//If already there is a screen title remove that..
			sc.plat.ScreenTitle.setMsg(null);
		}
        var dataS = res.res;
		this.listOutput = dataS;
		this.setModel(dataS, "getColonyList", {
			clearOldVals: true
        });
		if(Ext.isEmpty(dataS.ColonyList.Colony)){
			sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
		}
		this.initColonyGrid();

    },

	onRenderScrn : function () {
		
	},
	
	callback: function() {
        Ext.getBody().unmask();
    },
    doSearch: function() {
		var res = this.getTargetModel("getColonyList");
		var newres = res[0].Colony;
		var res2 = {};

		if(Ext.isEmpty(newres.ColonyPoolList.ColonyPool.PoolId) && Ext.isEmpty(newres.ColonyPoolList.ColonyPool.TableType)){
			res2 = {
			"ColonyId" : '',
			"PkPrefix" : '',
			"ColonyVersion" : ''
			};
			res2.ColonyId = newres.ColonyId;
			res2.ColonyIdQryType = newres.ColonyIdQryType;
			res2.PkPrefix = newres.PkPrefix;
			res2.PkPrefixQryType = newres.PkPrefixQryType;
			res2.ColonyVersion = newres.ColonyVersion;
			res2.ColonyVersionQryType = newres.VersionQryType;
		}else{
			res2 = {
			"ColonyId" : '',
			"PkPrefix" : '',
			"ColonyVersion" : '',
			"ColonyPoolList" : {
					"ColonyPool" : {
						"PoolId" : '',
						"TableType" : ''
					}
				}
			};

			res2.ColonyId = newres.ColonyId;
			res2.ColonyIdQryType = newres.ColonyIdQryType;
			res2.PkPrefix = newres.PkPrefix;
			res2.PkPrefixQryType = newres.PkPrefixQryType;
			res2.ColonyVersion = newres.ColonyVersion;
			res2.ColonyVersionQryType = newres.VersionQryType;
		
			res2.ColonyPoolList.ColonyPool.PoolId = newres.ColonyPoolList.ColonyPool.PoolId;
			res2.ColonyPoolList.ColonyPool.TableType = newres.ColonyPoolList.ColonyPool.TableType;

		}
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getColonyList",
				inputNS : "getColonyList",
				inputObj : {"getColonyList":res2},
				extraParams : "",
				success : this.resHandler,
				scope : this
			});
    },
    doReset: function() {
        sc.plat.DataManager.setModel({},
        "resetNS", {
            clearOldVals: true
        });
    },
	showModify: function(show, gridPnl) {
        gridPnl = gridPnl || this.get('grdpnlColony');
        var searchPnl = this.get('pnlColonySearch');
        if (show) {
            gridPnl.hide();
            searchPnl.hide();
        } else {
            gridPnl.show();
            searchPnl.show();
        }
    },
	
	getSelectedTableRowObj: function() {
        var gridPnl = this.get('grdpnlColony');
        var rows = gridPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length === 0) {
            return null;
        }
        return rows;
    },

	initColonyGrid : function() {
		var gridPnl = this.get('grdpnlColony');
		var sm = gridPnl.getSelectionModel();
		sm.on('rowselect',this.colonyRowClickHandler,this);
	},

	colonyRowClickHandler : function() {
	    var gridPnl = this.get('grdpnlColony');
		var toolbar = gridPnl.getTopToolbar();
		var synchButton = this.getButton(toolbar,'tbcolonySynch');
		var crtColButton = this.getButton(toolbar,'tbcreateColony');
		var colDtlButton = this.getButton(toolbar,'tbcolonyDetails');
		var colDelButton = this.getButton(toolbar,'tbcolonyDelete');
		var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var colId = mrow.data.ColonyId;
			if (colId == "DEFAULT") {
				synchButton.disable();
				colDelButton.disable();
			}
			else {
				synchButton.enable();
				colDelButton.enable();
		    }
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
	
	handleDelete:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row_to_delete);
        }
		var fcn = function(btn){
			if (btn == 'yes') {
				var colonyRow = {
					"ColonyId": '',
					"PkPrefix": ''
				};
				colonyRow.Action = 'Delete';
				colonyRow.ColonyId = selectedRows[0].data.ColonyId;
				colonyRow.PkPrefix = selectedRows[0].data.PkPrefix;
				var Colony = colonyRow;

				sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageColony",
				inputNS : "manageColony",
				inputObj : {"manageColony": Colony},
				extraParams : "",
				success : this.delHandler,
				scope : this
				});
			}
		};
		
		var deleteMessage = this.Confirm_to_delete_the_colony;
        Ext.Msg.confirm(this['b_Confirm'],deleteMessage, fcn, this); 
	},

	delHandler:function(mres, options){
		if (Ext.isEmpty(mres.responseText)) {
            return;
        }
        var result = Ext.util.JSON.decode(mres.responseText);
        var gridPnl = this.get('grdpnlColony');
        var store = gridPnl.getStore();
        var recIndex = store.find('ColonyId', result.mres.Colony.ColonyId);

        if (recIndex != -1) {
			var rec = store.getAt(recIndex);
            if (mres.statusText == 'OK') {
				store.remove(rec);
            } else {
				rec.set('Error', this.b_Error_caps);
                var errDesc = rows[0].result.Errors.Error[0].ErrorCode;
                rec.set('ErrorDesc', errDesc);
            }
        }
	},

	doColonyDetails:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row);
            return;
        }
		var colonyRow = {
					"ColonyId": '',
					"PkPrefix": ''
				};
		
		//colonyRow.Action = 'Delete';
		colonyRow.ColonyId = selectedRows[0].data.ColonyId;
		colonyRow.PkPrefix = selectedRows[0].data.PkPrefix;

		var Colony = colonyRow;

		sc.plat.FormUtils.request({
					params :{
                		getColonyDetails:  Ext.util.JSON.encode(Colony),
                		ns: 'getColonyDetails'
            		},
					getColonyDetails: Ext.util.JSON.encode(Colony),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getColonyDetails.do"
				});
	    
	},
    createColonyPopupWindow : null,
	doCallCreateColony:function(){
		if(!this.createColonyPopupWindow){
			var createColony = new sc.sma.createColony();
			this.createColonyPopupWindow = new Ext.Window({items:[createColony],  modal:true},{scope:createColony});
			this.createColonyPopupWindow.title = this.b_createColonyPopup;
			this.createColonyPopupWindow.height = 200;
			this.createColonyPopupWindow.width = 400;
			this.createColonyPopupWindow.closable = false;
			createColony.parentWindow = this.createColonyPopupWindow;
		}else{
			this.createColonyPopupWindow.items.items[0].init1();
		}
		this.createColonyPopupWindow.show();
		//createColonyPopupWindow.hideParent = true;
	},
	synchColonyPopupWindow : null,
	doColonySynchronize:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_colony_to_synchronize);
			return;
        }
		
		var colonyRow = {
					"ColonyId": '',
					"PkPrefix": ''
		};
	    var Colony = {
			"ColonyList" : {
			"Colony" : []
			}
		};
		for(var i = 0; i < selectedRows.length; i++){
			var colonyRow = {};
			colonyRow.ColonyId = selectedRows[i].data.ColonyId;
			colonyRow.PkPrefix = selectedRows[i].data.PkPrefix;
			Colony.ColonyList.Colony.push(colonyRow);
		}
		if(!this.synchColonyPopupWindow){
			var synchColony = new sc.sma.synchColony();
			synchColony.setColonyObj(Colony);
			this.synchColonyPopupWindow = new Ext.Window({items:[synchColony],  modal:true},{scope:synchColony});
			this.synchColonyPopupWindow.title = this.b_Alert;
			this.synchColonyPopupWindow.height = 150;
			this.synchColonyPopupWindow.width = 400;
			this.synchColonyPopupWindow.closable = false;
			synchColony.parentWindow = this.synchColonyPopupWindow ;
		}else{
			this.synchColonyPopupWindow.items.items[0].setColonyObj(Colony);
		}
		this.synchColonyPopupWindow.show();
	}
});
Ext.reg('xtype_name', sc.sma.colony);
//Ext.reg('colonyScreen', sc.sma.colony);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.createColonyUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "createColony",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "createColonyPanel",
            title: this.b_pnlcreateColony,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "colonyIdLabel",
                text: this.b_colonyId,
				cls: "sc-mandatory sc-left"
            },
            {
                xtype: "textfield",
                sciId: "colonyIdTxtFld",
                bindingData: {
                    defid: "object",
                    targetBinding: ["manageColony:Colony.ColonyId"],
                    sourceBinding: "resetNS:Colony.ColonyId"
                }
            },
            {
                xtype: "label",
                sciId: "prefixLabel",
                text: this.b_pkPrefix,
				cls: "sc-mandatory sc-left"
            },
            {
                xtype: "textfield",
                sciId: "prefixTxtFld",
                bindingData: {
                    defid: "object",
                    targetBinding: ["manageColony:Colony.PkPrefix"],
                    sourceBinding: "resetNS:Colony.PkPrefix"
                }
            }
			/*,
			{
				xtype : "checkbox",
				sciId: "useDefaultCfgCb",
				boxLabel : this.cb_useDefaultConfigurationShards,
				ctCls : 'sma-checkbox-label',
				checked : true,
				colspan : 2
			} */
			],
            buttons: [{
                xtype: "button",
                sciId: "createColonySaveButton",
                text: this.b_saveButton,
                handler: this.doSave,
                scope: this,
                iconCls: "sma-save-icon"
            },
            {
                xtype: "button",
                sciId: "createColonyCancelButton",
                text: this.b_cancelButtont,
                handler: this.doCancel,
                scope: this,
                iconCls: "sma-cancel-icon"
            }],
            iconCls: "sma-createcolony-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
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

sc.sma.createColony = function(config) {
    sc.sma.createColony.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.createColony, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.createColony',
    getUIConfig: sc.sma.createColonyUIConfig,
	
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	namespaces: {
		source: ["resetNS"],
        target: ["manageColony"]
    },
	
	doSave:function(){
		var tm = this.getTargetModel("manageColony");
		var colonyRow = {
					"ColonyId": '',
					"PkPrefix": ''
				};
		colonyRow.Action = "Create";
		if(Ext.isEmpty(tm[0].Colony.ColonyId)){
			Ext.Msg.alert('Alert',this.Colony_Id_Is_Mandatory);
			return;
		}
		if(Ext.isEmpty(tm[0].Colony.PkPrefix)){
			Ext.Msg.alert('Alert',this.Prefix_Is_Mandatory);
			return;
		}
		colonyRow.ColonyId = tm[0].Colony.ColonyId;
		colonyRow.PkPrefix = tm[0].Colony.PkPrefix;
		//var defCfgCb = this.get('useDefaultCfgCb');
		//colonyRow.UseDefaultConfiguration = defCfgCb.getValue();
		var Colony = colonyRow;
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageColony",
				inputNS : "manageColony",
				inputObj : {"manageColony": Colony},
				extraParams : "",
				success : this.handleSave,
				scope : this
				});
	},

	doCancel:function(){
		this.parentWindow.hide();
	},
	
	init1: function(){
		//var res = {};
		//this.setModel(res,"manageColony");
		var res = {};
		this.setModel({},
        "resetNS", {
            clearOldVals: true
        });
	},
	
	handleSave:function(mres,options){

		if (Ext.isEmpty(mres.responseText)) {
			//We have to handle exception here

            return;
        }
        var result = Ext.util.JSON.decode(mres.responseText);
		var colonyRow = {
					"ColonyId": '',
					"PkPrefix": ''
				};
		colonyRow.ColonyId = result.mres.Colony.ColonyId;
		colonyRow.PkPrefix = result.mres.Colony.PkPrefix;
		var Colony = colonyRow;
		sc.plat.FormUtils.request({
					params :{
                		getColonyDetails:  Ext.util.JSON.encode(Colony),
                		ns: 'getColonyDetails'
            		},
					getColonyDetails: Ext.util.JSON.encode(Colony),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getColonyDetails.do"
				}); 


    }
});
Ext.reg('xtype_name', sc.sma.createColony);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/
 
 Ext.namespace('sc.sma');

sc.sma.colonyDetailUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "colonyDetail",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "colonyDetailPanel",
            title: this.b_pnlcolonyDetail,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "colonyIdLabel",
                text: this.b_colonyId
            },
            {
                xtype: "textfield",
                sciId: "colonyIdTxtFld",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getColonyDetails:Colony.ColonyId"
                }
            },
            {
                xtype: "label",
                sciId: "prefixLabel",
                text: this.b_pkPrefix
            },
            {
                xtype: "textfield",
                sciId: "prefixTxtFld",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getColonyDetails:Colony.PkPrefix"
                }
            },
            {
                xtype: "label",
                sciId: "versionLabel",
                text: this.b_colonyVersion
            },
            {
                xtype: "textfield",
                sciId: "versionTxtFld",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getColonyDetails:Colony.ColonyVersion"
                }
            }],
            hideBorders: true,
            iconCls: "sma-colonydetail-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
            }
        },
        {
            xtype: "grid",
            sciId: "grdColonyPool",
            title: this.b_grdpnlColonyPoolList,
            columns: [{
                defid: "grid-column",
                sciId: "colColonyPoolId",
                header: this.b_colColonyPoolId,
                sortable: true,
                dataIndex: "PoolId",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "PoolId",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PoolId"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colTableType",
                header: this.b_colTableType,
                sortable: true,
                dataIndex: "TableType",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "TableType",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "TableType"
                    }
                }
            }],
            height: 400,
            tbar: ["->", {
                xtype: "tbbutton",
                sciId: "colonyPoolGrdAddPool",
                handler: this.doAddColonyPool,
                scope: this,
                tooltip: this.colonyPoolGrdAddPool,
                iconCls: "sma-create-button",
                text: this.b_tbaddColonyPool
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "colonyPoolGrdDeletePool",
                handler: this.doDeleteColonyPool,
                scope: this,
                tooltip: this.colonyPoolGrdDeletePool,
                iconCls: "sma-delete-button",
                text: this.b_tbdeleteColonyPool
            }],
            iconCls: "sma-colonylist-icon",
            bindingData: {
                defid: "object",
                sourceBinding: "getColonyDetails:Colony.ColonyPoolList.ColonyPool",
                targetBinding: ["manageColony:Colony.ColonyPoolList.ColonyPool"]
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

sc.sma.colonyDetail = function(config) {
    sc.sma.colonyDetail.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.colonyDetail, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.colonyDetail',
    getUIConfig: sc.sma.colonyDetailUIConfig,
	cbsm: new Ext.grid.CheckboxSelectionModel({
        singleSelect: true
    }),

	listOutput: null,
	colonyPoolPopupObj : null,
	tableTypeList : null,
	dbPoolList : null,


	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },

	namespaces: {
		source: ["getColonyDetails"],
        target: ["getColonyDetails","manageColony"]
    },

	populateColonyDetailData:function(coldata){	
		this.listOutput = coldata;
		this.setModel(coldata,"getColonyDetails",{
			clearOldVals: true
		});
		
		//Now call getTableTypeList api to see whether we need to deactivate addColony button
		sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "addColonyPool",
					inputNS : "addColonyPool",
					inputObj : null,
					extraParams : "",
					success : this.doOpOnAddButton,
					scope : this  
					});

	},

	doOpOnAddButton:function(result,options){
		var ttres = Ext.decode(result.responseText);
		if(Ext.isEmpty(ttres.ttres)){

			return;
		}

		var ttlres = ttres.ttres ;

		var colonyPoolList = this.listOutput.Colony.ColonyPoolList.ColonyPool;
		var gridPnl = this.find('sciId','grdColonyPool')[0];
		if(!colonyPoolList){
			//When there is no pool defined for the colony then we should disable delete button
			var deleteButton = this.recursivelyFindItem(gridPnl.topToolbar.items.items,'colonyPoolGrdDeletePool');
			deleteButton.disable();
			return;
		}
		
		var i ;
		for(i=0;i<colonyPoolList.length;i++){
			var poolData = colonyPoolList[i];
			var tableType = poolData.TableType;
		
			//Remove this table type
			var tts = ttlres.TableTypes.TableType;
			var j ;
			for(j=0;j<tts.length;j++){
				if(tts[j].TableType == tableType){
					tts.splice(j,1);
					j = j - 1;
				}
			}
		}

		var addBt = null;
		if(ttlres.TableTypes.TableType.length == 0){
			
			addBt = this.recursivelyFindItem(gridPnl.topToolbar.items.items,'colonyPoolGrdAddPool');
			addBt.disable();
		}
	},

		// Private
	recursivelyFindItem:function(item, sciId){
			if (item.sciId == sciId){ 
				return item;
			}else if(item.menu){
				return this.recursivelyFindItem(item.menu.items.items, sciId);
			}else if(Ext.isArray(item)){
				for(var i=0 ; i<item.length ; i++){
					var match = this.recursivelyFindItem(item[i], sciId);
					if(match){
						return match;
					}
				}
			}
	},

	getSelectedTableRowObj: function() {
        var gridPnl = this.get('grdColonyPool');
        var rows = gridPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length == 0) {
            return null;
        }
        return rows;
    },
	
	doDeleteColonyPool:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.Please_select_a_row);
            return;
        }

		var fcn = function(btn){
			if(btn == 'yes'){
				var Colony = {
					ColonyId : "",
					PkPrefix : "",
					ColonyPoolList:{
						ColonyPool:{
							PoolId : "",
							TableType : ""
						}
					}
				};
				Colony.Action = "Modify";
				Colony.ColonyId = this.listOutput.Colony.ColonyId;
				Colony.PkPrefix = this.listOutput.Colony.PkPrefix;
				Colony.ColonyVersion = this.listOutput.Colony.ColonyVersion;
				Colony.ColonyPoolList.ColonyPool.Action = "Delete";
				Colony.ColonyPoolList.ColonyPool.PoolId = selectedRows[0].data.PoolId;
				Colony.ColonyPoolList.ColonyPool.TableType = selectedRows[0].data.TableType;

				sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "manageColony",
					inputNS : "manageColony",
					inputObj : {"manageColony":Colony},
					extraParams : "",
					success : this.delHandler,
					scope : this  
					});

			}

		};
		Ext.Msg.confirm(this['b_Confirm'],this.Confirm_to_delete_colony_pool, fcn, this); 
	},

	delHandler:function(mres, options){
		if (Ext.isEmpty(mres.responseText)) {
            return;
        }
        var result = Ext.util.JSON.decode(mres.responseText);
		var Colony={
			ColonyId:"",
			PkPrefix:"",
			ColonyVersion:""
		};

		Colony.ColonyId = result.mres.Colony.ColonyId;
		Colony.PkPrefix = result.mres.Colony.PkPrefix;
		Colony.ColonyVersion = result.mres.Colony.ColonyVersion;

		sc.plat.FormUtils.request({
					params :{
                		getColonyDetails:  Ext.util.JSON.encode(Colony),
                		ns: 'getColonyDetails'
            		},
					getColonyDetails: Ext.util.JSON.encode(Colony),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getColonyDetails.do"
			    	});

	},
	
	doAddColonyPool:function(){
		var Colony = null;
		sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "addColonyPool",
					inputNS : "addColonyPool",
					inputObj : null,
					extraParams : "",
					success : this.handleAddColonyPool,
					scope : this  
					});
	},

	launchPopup : function(colonyPoolPopup,response){
		this.colonyPoolPopupObj = colonyPoolPopup;
		var colonyPoolPopupWindow = new Ext.Window({items:[colonyPoolPopup],  modal:true});

		colonyPoolPopupWindow.title= this.b_addColonyPoolPopup;
		//Height and width should be localized
		colonyPoolPopupWindow.height = 200;
		colonyPoolPopupWindow.width = 400;
		colonyPoolPopup.parentWindow = colonyPoolPopupWindow;
		colonyPoolPopupWindow.closable = false;
		colonyPoolPopupWindow.show();
		colonyPoolPopupWindow.hideParent = true;
		colonyPoolPopup.setColonyData(this.listOutput);
		colonyPoolPopup.populateAddColonyPoolScreen(response);
		this.disable;
	},
	
	onRenderScrn : function () {	
	},

	handleAddColonyPool:function(response, options){
		var colonyPoolPopup = new sc.sma.addColonyPool();
		this.launchPopup(colonyPoolPopup,response);
	},

	//This is a callback function from addColonyPool screen by which we can set the data 
	//on child screen
	setDataOnChild:function(){
		this.colonyPoolPopupObj.setColonyData(this.listOutput);
	}
	

});
Ext.reg('xtype_name', sc.sma.colonyDetail);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.addColonyPoolUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "addColonyPoolScreen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "colonyPoolPanel",
            title: this.b_pnladdColonyPool,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "poolIdLabel",
                text: this.b_colonyPool
            },
            {
                xtype: "combo",
                sciId: "poolIdCombo",
                displayField: "PoolId",
                valueField: "PoolId",
                mode: "local",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getDBPoolListOutput:DBPoolList.DBPool",
                    targetBinding: ["manageColony:Colony.ColonyPoolList.ColonyPool.PoolId"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["PoolId"]
                }),
                triggerAction: "all"
            },
            {
                xtype: "label",
                sciId: "tableTypeLabel",
                text: this.b_tableType
            },
            {
                xtype: "combo",
                sciId: "tableTypeCombo",
                valueField: "TableType",
                displayField: "TableType",
                mode: "local",
                triggerAction: "all",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getSupportedTableTypeListOutput:TableTypes.TableType",
                    targetBinding: ["manageColony:Colony.ColonyPoolList.ColonyPool.TableType"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["TableType"]
                })
            }],
            buttons: [{
                xtype: "button",
                sciId: "addColonyPoolSaveButton",
                text: this.b_saveButton,
                handler: this.doSave,
                scope: this,
                iconCls: "sma-save-icon"
            },
            {
                xtype: "button",
                sciId: "addColonyPoolCancelButton",
                text: this.b_cancelButton,
                handler: this.doCancel,
                scope: this,
                iconCls: "sma-cancel-icon"
            }],
            iconCls: "sma-addcolonypool-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
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

sc.sma.addColonyPool = function(config) {
	if(!config)
		config = {};
    sc.sma.addColonyPool.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.addColonyPool, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.addColonyPool',
    getUIConfig: sc.sma.addColonyPoolUIConfig,
	coldata:null,
		
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	namespaces: {
		source: ["resetNS", "getColonyDetails", 
			"getSupportedTableTypeListOutput","getDBPoolListOutput"],
        target: ["getColonyList", "manageColony", "searchColonyList",
			"getSupportedTableTypeListOutput","getColonyPoolListOutput"]
    },

	//Calling screen should set this record
	setColonyData:function(coldata){
		this.coldata = coldata;
	},
	
	populateAddColonyPoolScreen:function(res){
		//ttlres = ttlres.responseText;
		var ttres = Ext.decode(res.responseText);
		if(Ext.isEmpty(ttres.ttres)){
			return;
		}

		var ttlres = ttres.ttres ;
		var tabletypes = this.removeUsedTableType(ttlres);
		
		this.setModel(ttlres,"getSupportedTableTypeListOutput",{
			clearOldVals: true
		});

		var dbPoolList = ttres.dblres;

		this.setModel(dbPoolList,"getDBPoolListOutput",{
			clearOldVals : true
		});
	},
	
	//All the table types which has already been defined should be removed.	
	//Also removing METADATA so that metadata tabletype cannot be added
	removeUsedTableType:function(ttlres){
		if(this.coldata == null){
			//coldata should not be null here
			return;
		}
		this.removeTableType('METADATA',ttlres);
		this.removeTableType('CONFIGURATION',ttlres);
		this.removeTableType('STATISTICS',ttlres);
		var colonyPoolList = this.coldata.Colony.ColonyPoolList.ColonyPool;
		if(!colonyPoolList){
			//When there is no pool defined for the colony
			return;
		}
		var i ;
		for(i=0;i<colonyPoolList.length;i++){
			var poolData = colonyPoolList[i];
			var tableType = poolData.TableType;
			//Remove this table type
			this.removeTableType(tableType,ttlres);
		}
	},
	
	removeTableType : function(tableType,ttlres){
		var tts = ttlres.TableTypes.TableType;
			var j ;
			for(j=0;j<tts.length;j++){
				if(tts[j].TableType == tableType ){
					tts.splice(j,1);
					j = j - 1;
				}
			}
	},

	doSave:function(){
		var tm = this.getTargetModel("manageColony");
		if(Ext.isEmpty(tm[0].Colony.ColonyPoolList.ColonyPool.PoolId)){
			Ext.Msg.alert('Alert',this.Pool_Id_Is_Mandatory);
			return;
		}
		if(Ext.isEmpty(tm[0].Colony.ColonyPoolList.ColonyPool.TableType)){
			Ext.Msg.alert('Alert',this.Table_Type_Is_Mandatory);
			return;
		}
		
		var col = {
			"ColonyId" : '',
			"PkPrefix" : '',
			"ColonyVersion" : '',
			"ColonyPoolList" : {
					"ColonyPool" : {
						"PoolId" : '',
						"TableType" : ''
					}
				}
		};
		col.Action = "Modify";
		col.ColonyId = this.coldata.Colony.ColonyId;
		col.PkPrefix = this.coldata.Colony.PkPrefix;
		col.ColonyVersion = this.coldata.Colony.ColonyVersion;
		col.ColonyPoolList.ColonyPool.Action = "Create";
		col.ColonyPoolList.ColonyPool.TableType = tm[0].Colony.ColonyPoolList.ColonyPool.TableType;
		col.ColonyPoolList.ColonyPool.PoolId = tm[0].Colony.ColonyPoolList.ColonyPool.PoolId;
		
		sc.sma.AjaxUtils.request({
			actionNS : "sma",
			action : "manageColony",
			inputNS : "manageColony",
			inputObj : {"manageColony":col},
			extraParams : "",
			success : this.handleSave,
			scope : this  
			});
	},

	doCancel:function(){
		this.parentWindow.destroy();
	},

	handleSave:function(mres,options){
		var colonyRow = {
					"ColonyId": '',
					"PkPrefix": '',
					"ColonyVersion":''
				};
		colonyRow.ColonyId = this.coldata.Colony.ColonyId;
		colonyRow.PkPrefix = this.coldata.Colony.PkPrefix;
		colonyRow.ColonyVersion = this.coldata.Colony.ColonyVersion;
		var Colony = colonyRow;
		sc.plat.FormUtils.request({
					params :{
                		getColonyDetails:  Ext.util.JSON.encode(Colony),
                		ns: 'getColonyDetails'
            		},
					getColonyDetails: Ext.util.JSON.encode(Colony),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getColonyDetails.do"
				});
	
	}

});
Ext.reg('xtype_name', sc.sma.addColonyPool);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.dbPoolUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "dbPool",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "searchDBPool",
            title: this.b_pnlSearchDBPool,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "lblPoolId",
                text: this.b_dbPool,
			    cls: "sc-left"
            },
            {
                xtype: "combo",
                sciId: "poolIdQryCombo",
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                triggerAction: "all",
                mode: "local",
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getDBPoolList:DBPool.PoolIdQryType"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                })
            },
            {
                xtype: "textfield",
                sciId: "txtFldpoolId",
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:DBPool.PoolId",
                    targetBinding: ["getDBPoolList:DBPool.PoolId"]
                }
            }],
            buttons: [{
                xtype: "button",
                sciId: "searchButton",
                text: this.b_Search,
                handler: this.doSearch,
                scope: this,
                iconCls: "sma-search-button"
            },
            {
                xtype: "button",
                sciId: "resetButton",
                text: this.b_Reset,
                handler: this.doReset,
                scope: this,
                iconCls: "sma-reset-button"
            }],
            iconCls: "sma-dbpool-search-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 6
            }
        },
        {
            xtype: "grid",
            sciId: "grdDBPoolList",
            title: this.b_pnlDBPoolList,
            viewConfig: {
				forceFit: true
		    },
		    listeners: {
				defid: "listeners",
				'rowdblclick': this.dbpoolRowDoubleClick,
				scope: this
		    },
            columns: [{
                defid: "grid-column",
                sciId: "colPoolId",
                header: this.b_dbPool,
                sortable: true,
                dataIndex: "PoolId",
                width: 400,
                bindingData: {
                    defid: "object",
                    tAttrBinding: "PoolId",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PoolId"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colUrl",
                header: this.b_url,
                sortable: true,
                dataIndex: "url",
                width: 400,
                bindingData: {
                    defid: "object",
                    tAttrBinding: "url",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "url"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colUser",
                header: this.b_user,
                sortable: true,
                width: 400,
                bindingData: {
                    defid: "object",
                    tAttrBinding: "user",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "user"
                    }
                },
                dataIndex: "user"
            }],
            tbar: ["->", {
                xtype: "tbbutton",
                sciId: "tbCreateDBPool",
                text: this.b_tbCreateDBPool,
                handler: this.doCreateDBPool,
                scope: this,
                iconCls: "sma-create-button"
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbPoolDetail",
                text: this.b_tbDBPoolDetails,
                handler: this.doPoolDetails,
                scope: this,
                iconCls: "sma-detail-button"
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbDeleteDBPool",
                text: this.b_tbDeleteDBPool,
                handler: this.doDeleteDBPool,
                scope: this,
                iconCls: "sma-delete-button"
            }],
			selModel: this.cbsm,
            height: 400,
            iconCls: "sma-dbpool-icon",
            bindingData: {
                defid: "object",
                sourceBinding: "getDBPoolList:DBPoolList.DBPool",
                targetBinding: ["getDBPoolList:DBPoolList.DBPool"]
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

sc.sma.dbPool = function(config) {
    sc.sma.dbPool.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.dbPool, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.dbPool',
    getUIConfig: sc.sma.dbPoolUIConfig,

	namespaces: {
		source: ["resetNS","getQueryTypeListOutput","getDBPoolList"],
        target: ["getDBPoolList"]
    },
	
	apiData : null ,

	populateQueryTypeComboBox:function(qryres){	
		this.getLocalizeQryTypeDesc(qryres);
		this.setModel(qryres,"getQueryTypeListOutput");
	},
	
	getLocalizeQryTypeDesc:function(qryres){
		var stringQryTypes = qryres.QueryTypeList.StringQueryTypes.QueryType;

		// 1st Part : Localize String Query Types...

		if((stringQryTypes instanceof Array)){
			for(var i = 0; i < stringQryTypes.length;i++ ){
				var qryTypeObj = stringQryTypes[i];
				var qryType = qryTypeObj.QueryType;
				if(qryType == "EQ"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeEQ;
				}
				else if(qryType == "FLIKE"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeFLIKE;
				}
				else if(qryType == "LIKE"){
					qryTypeObj.QueryTypeDesc = this.QueryTypeLIKE;
				}else{
					//Should not come here
				}
			}
		}

	},

	doSearch:function(){
		var tm = this.getTargetModel("getDBPoolList");
		var tm1 = tm[0];
		var res = {};
		res.PoolId = tm1.DBPool.PoolId;
		res.PoolIdQryType = tm1.DBPool.PoolIdQryType;
		res.TrackDefaultColonyPool ="true";
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getDBPoolList",
				inputNS : "getDBPoolList",
				inputObj : {"getDBPoolList":res},
				extraParams : "",
				success : this.resHandler,
				scope : this
			});
	},

	resHandler:function(result,options){
		var res = Ext.decode(result.responseText);

		//if (Ext.isEmpty(res.res)) {
		//	return;
		//}
		var dblres = res.dblres;
		this.apiData= dblres;

		if(Ext.isEmpty(dblres.DBPoolList.DBPool)){
			sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
			//return;
		}else{
			//To remove a already set message
			sc.plat.ScreenTitle.setMsg(null);
		}
		var out = this.opOutput(dblres);
		this.setModel(out, "getDBPoolList", {
			clearOldVals: true
        });
        
		this.initDbPoolGrid();
		
	},
	
	opOutput:function(dblres){

		var poolList = dblres.DBPoolList.DBPool;
		if(!poolList){
			return;
		}

		var i ;
		for(i=0;i<poolList.length;i++){
			if(Ext.isEmpty(poolList[i].ParamList)){
				continue;
			}
			if(Ext.isEmpty(poolList[i].ParamList.Param)){
				continue;
			}
			var parmList = poolList[i].ParamList.Param;
			var j ;
			for(j=0;j<parmList.length;j++){
				var name = parmList[j].Name;
				if(name === 'url'){
					poolList[i].url = parmList[j].Value;
				}else if(name === 'user'){
					poolList[i].user = parmList[j].Value;
				}else{
					//Do nothing
				}
			}
		}
		return dblres;
	},

	doReset:function(){
		this.setModel({},
        "resetNS", {
            clearOldVals: true
        });
	},
	
	doCreateDBPool:function(){
		sc.plat.FormUtils.request({
					params :{
            		},
					//getColonyDetails: Ext.util.JSON.encode(Colony),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/createDBPool.do"
			    	});
	},

	getSelectedTableRowObj: function() {
        var gridPnl = this.find('sciId','grdDBPoolList')[0];
        var rows = gridPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length == 0) {
            return null;
        }
        return rows;
    },

	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },

	cbsm: new Ext.grid.RowSelectionModel({
        singleSelect: true
    }),

    initDbPoolGrid : function() {
		var gridPnl = this.get('grdDBPoolList');
		var sm = gridPnl.getSelectionModel();
		sm.on('rowselect',this.shardRowClickHandler,this);
	},

	shardRowClickHandler : function() {
	    var gridPnl = this.get('grdDBPoolList');
		var toolbar = gridPnl.getTopToolbar();
		var shrdDelButton = this.getButton(toolbar,'tbDeleteDBPool');
		var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var isDefaultStatus = this.isDefaultColony(mrow);
			if (isDefaultStatus)
			{
				shrdDelButton.disable();
			}
			else {
				shrdDelButton.enable();		   
			}
		}

	},

	isDefaultColony : function(mrow){
		var totalRecords = this.apiData.DBPoolList.DBPool.length;
		if(!totalRecords){
			return;
		}
		for(var i =0; i< totalRecords; i++){
			if(mrow.data.PoolId == this.apiData.DBPoolList.DBPool[i].PoolId){
				var isDefault = this.apiData.DBPoolList.DBPool[i];
				if(isDefault.IsDefaultColonyPool == "true"){
					return true;
				}else{
					return false;
				}
			}
		}
		//should not reach here..
		return false;
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

	doPoolDetails:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row);
            return;
        }
		var pool = {
			"PoolId" : '',
			"GetPasswords" : ''
		};
		pool.PoolId = selectedRows[0].data.PoolId;
		pool.GetPasswords = 'Y';
		sc.plat.FormUtils.request({
					params :{
						getDBPoolList:  Ext.util.JSON.encode(pool),
                		ns: 'getDBPoolList'
            		},
					getDBPoolList: Ext.util.JSON.encode(pool),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getDBPoolDetails.do"
			    	}); 

	},

	doDeleteDBPool:function(){
		var selectedRows = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row_to_delete);
            return;
        }
		var fcn = function(btn){
			if (btn == 'yes') {
				var poolRow = {
					"PoolId": ''
				};
				poolRow.Action = 'Delete';
				poolRow.PoolId = selectedRows[0].data.PoolId;

				sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageDBPool",
				inputNS : "manageDBPool",
				inputObj : {"manageDBPool": poolRow},
				extraParams : "",
				success : this.delHandler,
				scope : this
				});
			}
		}

		var deleteMessage = this.Confirm_to_delete_dbpool;
        Ext.Msg.confirm(this['b_Confirm'],deleteMessage, fcn, this); 
	},

	delHandler:function(result,options){
		if (Ext.isEmpty(result.responseText)) {
            return;
        }
		var res = Ext.util.JSON.decode(result.responseText);
		var gridPnl = this.find('sciId','grdDBPoolList')[0];
        var store = gridPnl.getStore();
        var recIndex = store.find('PoolId', res.mdbres.DBPool.PoolId);

        if (recIndex != -1) {
			var rec = store.getAt(recIndex);
            if (result.statusText == 'OK') {
				store.remove(rec);
            } else {
				rec.set('Error', this.b_Error_caps);
                //var errDesc = result.Errors.Error[0].ErrorCode;
                //rec.set('ErrorDesc', errDesc);
            }
        }
	},
	
	dbpoolRowDoubleClick:function(){
		this.doPoolDetails();
	}
});
Ext.reg('xtype_name', sc.sma.dbPool);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.manageDBPoolUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "manageDBPool",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "pnlManageDBPool",
            title: this.b_pnlManageDBPool,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "lblPoolId",
                text: this.b_dbPool,
				cls: "sc-mandatory sc-left"
            },
            {
                xtype: "textfield",
                sciId: "textFldPoolId",
                bindingData: {
                    defid: "object",
                    sourceBinding: "getDBPoolList:DBPoolList.DBPool.PoolId",
                    targetBinding: ["manageDBPool:DBPool.PoolId"]
                }
            }],
            tbar: ["->", {
                xtype: "tbbutton",
                sciId: "tbSaveDBPool",
                text: this.b_saveButton,
                handler: this.doSave,
                scope: this,
                iconCls: "sma-save-icon"
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbResetButton",
                text: this.b_Reset,
                handler: this.doReset,
                scope: this,
                iconCls: "sma-reset-button"
            }],
            iconCls: "sma-dbpool-icon",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            }
        },
        {
            xtype: "editorgrid",
            sciId: "grdPoolParamList",
            title: this.b_pnlPoolParamList,
            columns: [this.cbsm, {
                defid: "grid-column",
                sciId: "colParamName",
                header: this.b_paramName,
                sortable: true,
                dataIndex: "Name",
                width: 300,
                renderer: this.getRenderer,
                bindingData: {
                    defid: "object",
                    tAttrBinding: "Name",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Name"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "colParamValue",
                header: this.b_paramValue,
                sortable: true,
                dataIndex: "Value",
                width: 300,
                renderer: this.getRenderer,
                bindingData: {
                    defid: "object",
                    tAttrBinding: "Value",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Value"
                    }
                }
            }],
            clicksToEdit: 2,
            tbar: ["->", {
                xtype: "tbbutton",
                sciId: "tbAddParam",
                text: this.b_tbAddParam,
                handler: this.doAddParam,
                scope: this,
                iconCls: "sma-create-button"
            },
            " ", "-", " ", {
                xtype: "tbbutton",
                sciId: "tbdeleteParam",
                text: this.b_tbDeleteParam,
                handler: this.doDeleteParam,
                scope: this,
                iconCls: "sma-delete-button"
            }],
            height: 400,
            view: this.gridView,
            selModel: this.cbsm,
            iconCls: "sma-params-icon",
            bindingData: {
                defid: "object",
                sourceBinding: "getDBPoolList:DBPoolList.DBPool.ParamList.Param",
                targetBinding: ["manageDBPool:DBPool.ParamList.Param"]
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

sc.sma.manageDBPool = function(config) {
	sc.sma.manageDBPool.superclass.constructor.call(this, config);

	var dbPoolListGrd = this.find('sciId','grdPoolParamList')[0];
	var colModel = dbPoolListGrd.getColumnModel();
	
	colModel.setEditable(1,true);
	colModel.setEditable(2, true);
	var editors = {
		'password': new Ext.Editor(new Ext.form.TextField({
				inputType: 'password',
				scSkipDataValidation: true
			}),{
			alignment: "tl-tl",
			autoSize: "width"
			}),
		'string': new Ext.Editor(new Ext.form.TextField({
			scuiDataType: "PoolParam"
			}),{
			alignment: "tl-tl",
			autoSize: "width"
		}),
		'date': new Ext.Editor(new Ext.ux.form.DateTime(),{
			alignment: "tl-tl",
			autoSize: "width"
		})
	};
		
	colModel.getCellEditor = function(colIndex, rowIndex) {
		var grdStore = dbPoolListGrd.getStore();
		//For active editor
		if(colIndex ===2){
			if(grdStore.data.items){
				if(grdStore.data.items[rowIndex].data.Name.search("password") != -1){
					return editors.password;
				}
				if(grdStore.data.items[rowIndex].data.Name.search("effective") != -1){
					return editors.date;
				}
			}
		}
		var items = grdStore.data.items[rowIndex];
		var record = grdStore.reader.jsonData;
		var i;
		var j = 0;
		var dt_j = 0;
		var password_rows=[] ;
		var date_rows=[] ;
		for(i=0;i<record.length;i++){
			var name = record[i].Name;
			//Here we are making password field for Param Name that contains 'password'.It can be extended in future.
			if(name.search('password') != -1){
				password_rows[j] = i;
				j++;
			}
			if(name.search('effective') != -1){
				date_rows[dt_j] = i;
				dt_j++;
			}
		}
		
		if(colIndex === 2 ){
			for(var k = 0;k<password_rows.length;k++){
				if(password_rows[k] === rowIndex){
					return editors.password;
				}
			}
			for(k = 0;k<date_rows.length;k++){
				if(date_rows[k] === rowIndex){
					return editors.date;
				}
			}
			return editors.string;
		}else{
			return editors.string;
		}
	};
};
Ext.extend(sc.sma.manageDBPool, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.manageDBPool',
    getUIConfig: sc.sma.manageDBPoolUIConfig,
	
	
	cbsm: new Ext.grid.CheckboxSelectionModel({
        singleSelect: true
    }),
	
    namespaces: {
        target: ["manageDBPool"],
        source: ["getDBPoolList"]
    },
	
	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	scrData : null,
	scrMode : 'Modify',
	date1: null,
	password1: null,
	deletedParamList : null,
	poolId:null,
	populateDetailScreen:function(dblres){
		var detailModel = {
			DBPoolList:{
				DBPool:{
					ParamList : {}
				}
			}
		};
		detailModel.DBPoolList.DBPool.PoolId = dblres.DBPoolList.DBPool[0].PoolId;
		this.poolId=dblres.DBPoolList.DBPool[0].PoolId;
		detailModel.DBPoolList.DBPool.ParamList = dblres.DBPoolList.DBPool[0].ParamList;
		this.scrData = detailModel;
		this.setModel(detailModel,"getDBPoolList",{ 
			clearOldVals: true
		});
		this.disableTxtBox(detailModel.DBPoolList.DBPool.PoolId);
		if (dblres.DBPoolList.DBPool[0].CanPersist == 'N')
		{
			this.disableAll();
		}
		else{
			var dbPoolListGrd = this.find('sciId','grdPoolParamList')[0];
			var colModel = dbPoolListGrd.getColumnModel();
			colModel.setEditable(1,true);
			colModel.setEditable(2,true);
		}
		this.initParamGrid();
	},
	disableAll:function(){
		var dbPoolListGrd = this.find('sciId','grdPoolParamList')[0];
		var colModel = dbPoolListGrd.getColumnModel();

		colModel.setEditable(1,false);
		colModel.setEditable(2,false);
		var saveBtn = this.findScreenItemsBy(function(cmp){
			if(cmp.isXType('button')){
				cmp.disable();
			}
		},null,{findRecursive :true});
	},
	disableTxtBox:function(val){
		var mPnl = this.find('sciId','pnlManageDBPool')[0];
		var txtBox = mPnl.find('sciId','textFldPoolId')[0];
		txtBox.setValue(val);
		txtBox.disable();
	},
	
	populateBasicScreen:function(){
		var baseModel = {
			DBPoolList:{
				DBPool:{
					ParamList : {
						Param : []
				}
				}
			}
		};

		var userParam = {
			Name : 'user',
			Value : ''
		};
		baseModel.DBPoolList.DBPool.ParamList.Param.push(userParam);
		
		var passwordParam = {
			Name : 'password',
			Value : ''
		};

		baseModel.DBPoolList.DBPool.ParamList.Param.push(passwordParam);

		var urlParam = {
			Name : 'url',
			Value : 'jdbc:{DBType}://{HostName}:{Port}/{schema}?{prop}'
		};

		baseModel.DBPoolList.DBPool.ParamList.Param.push(urlParam);

		var driverParam = {
			Name : 'driver',
			Value : ''
		};

		baseModel.DBPoolList.DBPool.ParamList.Param.push(driverParam);

		var schemaParam = {
			Name : 'schema',
			Value : ''
		};

		baseModel.DBPoolList.DBPool.ParamList.Param.push(schemaParam);

		this.scrData = baseModel;
		this.setModel(baseModel,"getDBPoolList",{ 
			clearOldVals: true
		});
		
		this.scrMode = 'Create';
		this.initParamGrid();
	},
	
	doSave : function(){
		var tm = this.getTargetModel("manageDBPool")[0];
		var robj = {};
		if (Ext.isEmpty(tm.DBPool.PoolId))
		{
			robj.PoolId = this.poolId;
		}
		else {
			robj.PoolId = tm.DBPool.PoolId;
			this.poolId = tm.DBPool.PoolId;
		}
		robj.ParamList = tm.DBPool.ParamList;
		robj.ConnCheck='true';

		//If this.deletedPatamList is not empty we need to push
		// the params onto ParamList that need to be deleted.
		if (!Ext.isEmpty(this.deletedParamList))
		{
			for(var k = 0; k<this.deletedParamList.length;k++){
				robj.ParamList.Param.push(this.deletedParamList[k]);
			}
		}
		var check = this.validateParamList(robj);
		if (!check)
		{
			return;
		}
		robj.Action = this.scrMode;
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageDBPool",
				inputNS : "manageDBPool",
				inputObj : {"manageDBPool":robj},
				extraParams : "",
				success : this.saveHandler,
				scope : this
			});
	},
	validateParamList: function(obj) {
		// PoolId is mandatory
		if (Ext.isEmpty(obj.PoolId))
		{
            Ext.Msg.alert(this.b_Alert, this.Pool_Id_Is_Mandatory);
            return false;
		}
		// Pool connection parameters are mandatory
		if (Ext.isEmpty(obj.ParamList.Param))
		{
            Ext.Msg.alert(this.b_Alert, this.Please_enter_dbpool_param);
            return false;
		}

		// If password.1 is given effective.1 need to be given and vice versa.
		// the below code checks for it in paramList
    	var all_password_params=[] ;
		var all_effective_params=[];
		var pwd_index = 0;
		var eff_index = 0;

		for (var i = 0; i < obj.ParamList.Param.length; i++) {         
			var name = obj.ParamList.Param[i].Name;
			var val = obj.ParamList.Param[i].Value;

			if (obj.ParamList.Param[i].Action != undefined && obj.ParamList.Param[i].Action == 'Delete')
			{
				continue;
			}
			if (name.search('password.') != -1 )
			{
				var dotIndex = name.indexOf('.');
				var endsWith = name.substr(dotIndex + 1, name.length - dotIndex);
				if (!Number(endsWith))
				{
					continue;
				}
				if (this.arrayContains(all_password_params,endsWith))
				{
					Ext.Msg.alert(this.b_Alert, this.Please_enter_one_value_for + 'password.' + endsWith);
					return false;
				}
				else
				{
					all_password_params[pwd_index] = endsWith;
					pwd_index++;
				}
			}
			if (name.search('effective.') != -1)
			{
				var dotIndex = name.indexOf('.');
				var endsWith = name.substr(dotIndex + 1, name.length - dotIndex);				
				if (!Number(endsWith))
				{
					continue;
				}
				if (this.arrayContains(all_effective_params,endsWith))
				{
					Ext.Msg.alert(this.b_Alert, this.Please_enter_one_value_for + 'effective.' + endsWith);
					return false;
				}
				else
				{
					all_effective_params[eff_index] = endsWith;
					eff_index++;
				}
			}
		}
		for (var i = 0; i < all_effective_params.length; i++)
		{
			var eff_name_index = all_effective_params[i];
			if (!this.arrayContains(all_password_params,eff_name_index))
			{
				Ext.Msg.alert(this.b_Alert, this.Please_enter_password_for + 'effective.' + eff_name_index);
				return false;
			}

		}
		for (var i = 0; i < all_password_params.length; i++)
		{
			var password_index = all_password_params[i];
			if (!this.arrayContains(all_effective_params,password_index))
			{
				Ext.Msg.alert(this.b_Alert, this.Please_enter_effective_date_for + 'password.' + password_index);
				return false;
			}

		}
		return true;
	},
	arrayContains:function(array, element){
		for (var j = 0; j < array.length; j++)
		{
			if (array[j] == element)
			{
				return true;
			}
		}
		return false;
	},
	saveHandler:function(result,options){
		//Once save is done refresh the screen.
		var pool = {};
		var tm = this.getTargetModel("manageDBPool")[0];
		if (Ext.isEmpty(tm.DBPool.PoolId))
		{
			pool.PoolId = this.poolId;
		}
		else {
			pool.PoolId = tm.DBPool.PoolId;
			this.poolId = tm.DBPool.PoolId;
		}
		pool.GetPasswords='Y';
		sc.plat.FormUtils.request({
					params :{
						getDBPoolList:  Ext.util.JSON.encode(pool),
                		ns: 'getDBPoolList'
            		},
					getDBPoolList: Ext.util.JSON.encode(pool),
            		method : 'POST',
            		url : '/' + sc.plat.info.Application.getApplicationContext() + "/sma/container/getDBPoolDetails.do"
			    	}); 
	},

	doReset : function(){
		this.setModel(this.scrData,"getDBPoolList",{ 
			clearOldVals: true
		});
	},

	doAddParam : function(){		
		var grdPnl = this.find('sciId','grdPoolParamList')[0];
		var store = grdPnl.getStore();
		var rec = new this.paramRecord({
			Name : '',
			Value : ''
		});
		store.add([rec]);
		var index = store.indexOf(rec);
		grdPnl.stopEditing();
		grdPnl.startEditing(index,1);
	    var sm = grdPnl.getSelectionModel();
		sm.selectRow(index);
	},

	paramRecord : Ext.data.Record.create([{
			name : 'Name'
		},
		{
			name : 'Value'
		}]),
	doDeleteParam: function(){

		// deleteParam method only removes the record from the grid's store.
		// The deleted params are saved in this.deletedParamList
		// the list is used while actually the saving.

		var selectedRows = this.getSelectedTableRowObj();
		if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row_to_delete);
            return;
        }
		this.deletedParamList = new Array();
		if (Ext.isEmpty(selectedRows)) {
            Ext.Msg.alert(this.b_Alert, this.Please_select_a_row_to_delete);
            return;
        }
        var fcn = function(btn) {
            if (btn == 'yes') {
				var grdPnl = this.find('sciId','grdPoolParamList')[0];
				var store = grdPnl.getStore();
				var j=0;
				for (i = 0; i < selectedRows.length; i++) {   
					var recNameIndex = store.find('Name', selectedRows[i].data.Name);
					var recValIndex = store.find('Value', selectedRows[i].data.Value);
					 if (recNameIndex != -1 && recNameIndex == recValIndex) {
						var rec = store.getAt(recNameIndex);
						var param ={
							"Name":'', "Value":''};
						param.Name = rec.data.Name;
						param.Value = rec.data.Value;
						param.Action= 'Delete';
						this.deletedParamList[j] = param;
						j++;
						store.remove(rec);
					}
				}
            }
        }
        Ext.Msg.confirm(this['b_Confirm'], this.Confirm_to_delete_dbpool_param, fcn, this);
	},
	
	getSelectedTableRowObj: function() {
		var grdPnl = this.find('sciId','grdPoolParamList')[0];
        var rows = grdPnl.getSelectionModel().getSelections();
        if (Ext.isEmpty(rows) || rows.length === 0) {
            return null;
        }
        return rows;
	},

	getTextEditor:function(){
		return new Ext.form.TextField();
	},

	initParamGrid : function() {
		var gridPnl = this.get('grdPoolParamList');
		var sm = gridPnl.getSelectionModel();
		sm.on('rowselect',this.paramRowClickHandler,this);
	},

	paramRowClickHandler : function() {
	    var gridPnl = this.get('grdPoolParamList');
		var toolbar = gridPnl.getTopToolbar();
		var paramDelButton = this.getButton(toolbar,'tbdeleteParam');
		var rows = gridPnl.getSelectionModel().getSelections();
		var row = rows[0];
		if(row){
			var mrows = this.getSelectedTableRowObj();
			var mrow = mrows[0];
			var isRestricted = this.isRestrictedParam(mrow);
			if (isRestricted)
			{
				paramDelButton.disable();
			}
			else {
				paramDelButton.enable();		   
			}
		}

	},
	
	isRestrictedParam : function(mrow){
			if((mrow.data.Name == "url") || (mrow.data.Name =="user") || (mrow.data.Name =="password") || (mrow.data.Name =="driver") || (mrow.data.Name =="schema")){
					return true;
				}else{
					return false;
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
	
	getRenderer:function(value,scope){
		if(scope.value.search('password') != -1){
			var pl = value.length;
			var i ;
			var ch ;
			for(i=0;i < pl ; i++){
				if(i===0){
					ch = '&#9679;';
				}
				ch = ch + '&#9679;';
			}
			return ch;
		}
		return value;
	}

});
Ext.reg('xtype_name', sc.sma.manageDBPool);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.synchColonyUIConfig = function() {
	return {xtype: "screen"
		,sciId:  "synchColonyScreen"
		,header: false
		,layout:  "anchor"
		,autoScroll: true
		,items: [{xtype: "panel"
				,sciId:  "panel"
				,title:  "Panel "
				,layout:  "table"
				,layoutConfig: {defid: "tableLayoutConfig"
				,columns: 2}
				,items: [{xtype: "label"
					,sciId:  "label"
					,text:  this.Please_confirm_that_you_want_to_synchronize_the_selected_colony
					,colspan: 2},
					{xtype: "checkbox"
						,sciId:  "synchPnlCb"
						,checked: true
						,boxLabel:  this.b_execgenscripts
						,ctCls : 'sma-checkbox-label'}]
					,buttons: [{xtype: "button"
					,sciId:  "synchPnlOkButton"
					,text:  this.b_synchOkButton
					,handler: this.doSynch
					,scope: this},{xtype: "button"
					,sciId:  "synchPnlCnButton"
					,text:  this.b_synchCancelButton
					,handler: this.doCancel
					,scope: this}]
					,header: false}]};
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

sc.sma.synchColony = function(config) {
	sc.sma.synchColony.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.synchColony, sc.plat.ui.ExtensibleScreen, {
	className: 'sc.sma.synchColony',
	getUIConfig: sc.sma.synchColonyUIConfig,
	namespaces: {
		target: ['synchColony'],
		source: ['synchColony']
	},
	namespacesDesc: {
		targetDesc: ['synchColony'],
		sourceDesc: ['synchColony']
	},

	get: function(sciId) {
        return this.find('sciId', sciId).pop();
    },
	
	doSynch : function(){
		var cb = this.get('synchPnlCb');
		var selected = cb.getValue();
		if(selected){
			this.colony.ExecuteFlag = 'Y';
		}else{
			this.colony.ExecuteFlag = 'N';
		}
		sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "smaColonySynch",
					inputNS : "synchronizeColony",
					inputObj : {"synchronizeColony": this.colony},
					extraParams : "",
					success : this.handleSynchronization,
					scope : this
		});
	},
	doCancel : function(){
		this.parentWindow.hide();
	},
	colony : null,
	setColonyObj : function(colonyObj){
		this.colony = colonyObj;
		var cb = this.get('synchPnlCb');
		cb.setValue(true);
	},
	handleSynchronization : function(){
		this.doCancel();
	}
});
Ext.reg('xtype_name', sc.sma.synchColony);
