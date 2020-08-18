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

Ext.form.Label.prototype.onRender = Ext.form.Label.prototype.onRender.createInterceptor(function(){
	if (this.scAlignToLeft === 'true'){
		this.cls = 'x-form-item sma-left-label';
	}
 });


 // Copied this from STK

 Ext.ns("sc.sma.ui");

sc.sma.ui.PagingToolbar = function(config) {
	config = config || {};
	this.ps = config.paginationStrategy.toUpperCase();
	if (this.ps != "GENERIC")
		this.afterPageText = "";
	sc.sma.ui.PagingToolbar.superclass.constructor.call(this, config);
	this.pageCursor = 0;
}
Ext.extend(sc.sma.ui.PagingToolbar, Ext.PagingToolbar, {
			displayMsg : "",
			displayInfo : false,
			pageSize : 0,
			onRender : function(ct, position) {
				sc.sma.ui.PagingToolbar.superclass.onRender.call(this, ct,
						position);
				var comboData = [{
							text : '10'
						}, {
							text : '20'
						}, {
							text : '30'
						}, {
							text : '40'
						}, {
							text : '50'
						}, {
							text : '60'
						}, {
							text : '70'
						}, {
							text : '80'
						}, {
							text : '90'
						}, {
							text : '100'
						}];
				var c = ['10', '20', '30', '40', '50', '60', '70', '80', '90',
						'100'];

				this.sizeCombo = new Ext.form.ComboBox({
							allowBlank : false,
							width : 50,
							listWidth : 50,
							mode : 'local',
							store : c,
							typeAhead : false,
							regex : /^((0\d*[1-9])|([1-9]\d*))$/,
							regexText : "Page size should be more than zero.",
							maskRe : /^\d+$/,
							disableKeyFilter : true
						});
				this.sizeCombo.on('specialkey', this.onComboChange, this);
				this.sizeCombo.on('select', this.onComboChange, this);
				this.sizeCombo.on('blur', this.onComboChange, this);
				// this.sizeCombo.on('beforequery', function(o){ o.forceAll =
				// true; return true; }, this );
				this.addSeparator();
				this.addField(this.sizeCombo);

				this.resetPaging();
			},

			onComboChange : function(combo) {
				if (combo.isValid()
						&& parseInt(combo.getRawValue()) != this.pageSize) {
					this.store.setPageSize(combo.getRawValue());
					this.doLoad(1);
				} else {
					combo.setValue(this.getPageData().pageSize);
				}
			},

			onLoad : function(store, recs, options) {
				this.loading.show();
				var pageData = this.refreshPageData();

				this.pageCursor = pageData.activePage;
				this.pageSize = pageData.pageSize;
				sc.sma.ui.PagingToolbar.superclass.onLoad.call(this, store,
						recs, options);
				if (this.ps != "GENERIC") {
					this.field.dom.disabled = true;
					this.last.setDisabled(true);
				} else
					this.field.dom.disabled = false;
				this.sizeCombo.setDisabled(false);
				this.sizeCombo.setValue(this.pageSize);
				this.last
						.setDisabled(this.last.disabled || pageData.isLastPage);
				this.next
						.setDisabled(this.next.disabled || pageData.isLastPage);
			},

			onClick : function(which) {
				var pageData = this.getPageData();
				if (pageData == null)
					return;
				switch (which) {
					case "first" :
						this.doLoad(1);
						break;
					case "prev" :
						this.doLoad(pageData.activePage - 1);
						break;
					case "next" :
						this.doLoad(pageData.activePage + 1);
						break;
					case "last" :
						this.doLoad(pageData.pages);
						break;
					case "refresh" :
						this.refreshPage();
						break;
				}
			},

			updateInfo : Ext.emptyFn,

			refreshPage : function() {
				var activePage = this.getPageData().activePage;
				if (activePage != null && activePage > 0)
					this.doLoad(activePage);
			},

			refreshPageData : function() {
				var data = {};
				var store = this.store;
				if (store.isInitialized()) {
					data = {
						total : store.getTotalCount(),
						activePage : store.getCurrentPageNumber(),
						pages : store.getTotalPages(),
						isLastPage : store.isLastPage(),
						isFirstPage : store.isFirstPage(),
						isValidPage : store.isValidPage(),
						pageSize : store.getPageSize()
					};
				}
				this._pageData = data;
				return data;
			},

			getPageData : function() {
				return this._pageData || {};
			},

			onPagingKeydown : function(e) {
				var k = e.getKey(), d = this.getPageData(), pageNum;
				if (k == e.RETURN) {
					e.stopEvent();
					pageNum = this.readPage(d);
					if (pageNum !== false) {
						var pageData = this.getPageData();
						if (pageNum <= 0 || pageNum > pageData.pages
								|| pageNum == this.pageCursor)
							this.field.dom.value = this.pageCursor;
						else
							this.doLoad(pageNum);
					}
				}
			},

			doLoad : function(pageNum) {
				this.setLoading();
				var o = {}
				o["pageNum"] = pageNum;
				if (this.fireEvent('beforechange', this, o) !== false) {
					this.store.goToPage(o.pageNum);
				}
			},

			changePage : function(pageNum) {
				this.doLoad(pageNum);
			},

			unbind : function(store) {
				if (store == null)
					return;
				store = Ext.StoreMgr.lookup(store);
				store.un("resetpaging", this.resetPaging, this);
				store.un("beforepagingload", this.beforeLoad, this);
				store.un("pagingload", this.onLoad, this);
				store.un("pagingloadexception", this.onLoadError, this);
				store.un("paginginvalidpage", this.onInvalidPage, this);
				store.un("mashupvalidationerror", this.onSCUIException, this);
				this.store = undefined;
			},

			bind : function(store) {
				if (store == null)
					return;
				store = Ext.StoreMgr.lookup(store);
				store.on("resetpaging", this.resetPaging, this);
				store.on("beforepagingload", this.beforeLoad, this);
				store.on("pagingload", this.onLoad, this);
				store.on("pagingloadexception", this.onLoadError, this);
				store.on("paginginvalidpage", this.onInvalidPage, this);
				store.on("scuiexception", this.onSCUIException, this);
				this.store = store;
			},

			resetPaging : function() {
				this.cursor = 0;
				this.pageCursor = 0;
				this.first.setDisabled(true);
				this.prev.setDisabled(true);
				this.next.setDisabled(true);
				this.last.setDisabled(true);
				this.loading.hide();
				this.sizeCombo.clearValue();
				this.sizeCombo.setDisabled(true);
				this.field.dom.value = 0;
				this.field.dom.disabled = true;
				this.afterTextEl.el.innerHTML = String.format(
						this.afterPageText, 0);
			},

			setLoading : function() {
				this.first.setDisabled(true);
				this.prev.setDisabled(true);
				this.next.setDisabled(true);
				this.last.setDisabled(true);
				this.sizeCombo.setDisabled(true);
			},

			onInvalidPage : function(store, options, e) {
				Ext.MessageBox.show({
							title : 'No Records',
							msg : 'No more records to show.',
							width : 300,
							buttons : Ext.Msg.OK,
							icon : Ext.MessageBox.INFO
						});
				this.field.dom.value = this.pageCursor;
				this.loading.enable();
				// this.onLoad(store, recs, options);
				this.next.setDisabled(true);
				this.last.setDisabled(true);
			},

			onLoadError : function(store, options, e, res) {
				Ext.MessageBox.show({
							title : 'Load Exception',
							msg : 'Could not load records',
							width : 300,
							buttons : Ext.Msg.OK,
							icon : Ext.MessageBox.INFO
						});
				this.field.dom.value = this.pageCursor;
				this.loading.enable();
			},

			onSCUIException : function(store, options, e, res) {
				sc.plat.RequestUtils.handleAjaxErrors(res, options);
				this.field.dom.value = this.pageCursor;
				this.loading.enable();
			}

		});
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.propertyMgtUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel",
            title: this.b_SearchProperties,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label4",
                text: this.b_Category
            },
            {
                xtype: "combo",
                sciId: "combo4",
                width: 204,
                displayField: "CategoryNameDesc",
                valueField: "Category",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                bindingData: {
                    defid: "object",
                    optionsBinding: "getCategoriesListOutput:PropertyMetadataList.PropertyMetadata",
                    sourceBinding: "resetNS:CategoryList.Category",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.Category", "getPropertyListAdvancedSearch:PropertyMetadata.Category"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Category", "CategoryNameDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label",
                text: this.b_PropertyName
            },
            {
                xtype: "combo",
                sciId: "combo",
                width: 204,
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                mode: "local",
                triggerAction: "all",
                forceSelection: true,
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.BasePropertyNameQryType", "getPropertyListAdvancedSearch:PropertyMetadata.BasePropertyNameQryType"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                })
            },
            {
                xtype: "textfield",
                sciId: "textfield",
                width: 200,
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:PropertyMetadata.BasePropertyName",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.BasePropertyName", "getPropertyListAdvancedSearch:PropertyMetadata.BasePropertyName"]
                }
            },
            {
                xtype: "label",
                sciId: "label1",
                text: this.b_OverrideType
            },
            {
                xtype: "combo",
                sciId: "combo1",
                width: 204,
                displayField: "PropertyOverrideDesc",
                valueField: "PropertyOverride",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["PropertyOverride", "PropertyOverrideDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getOverrideTypeListOutput:OverrideTypeList.OverrideType",
                    sourceBinding: "resetNS:OverrideTypeList.OverrideType",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.PropertyList.Property.PropertyOverride", "getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.PropertyOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label3",
                text: this.b_OverriddenFor
            },
            {
                xtype: "combo",
                sciId: "combo3",
                width: 204,
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.PropertyList.Property.PropertyOverrideNameQryType", "getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.PropertyOverrideNameQryType"]
                }
            },
            {
                xtype: "textfield",
                sciId: "textfield2",
                width: 200,
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.PropertyOverrideName",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.PropertyList.Property.PropertyOverrideName", "getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.PropertyOverrideName"]
                }
            },
            {
                xtype: "label",
                sciId: "label17",
                text: this.b_Modifiable,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo10",
                hidden: true,
                width: 204,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                scViewMode: "Advanced",
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                }),
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:PropertyMetadata.Modifiable",
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.Modifiable"]
                }
            },
            {
                xtype: "label",
                sciId: "label18",
                text: this.b_ModifiableatRuntime,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo11",
                colspan: 2,
                hidden: true,
                scViewMode: "Advanced",
                width: 204,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:ModifiableList.Modifiable",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.ModifiableAtRuntime"]
                }
            },
            {
                xtype: "label",
                sciId: "labeluseroverride",
                text: this.b_UserOverridable,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo12",
                width: 204,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                scViewMode: "Advanced",
                hidden: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:PropertyMetadata.Modifiable",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.UserOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label19",
                text: this.b_ServerOverridable,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo13",
                colspan: 2,
                width: 204,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                scViewMode: "Advanced",
                hidden: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:PropertyMetadata.Modifiable",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.ServerOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label212",
                text: this.b_PropertyType,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo83",
                scViewMode: "Advanced",
                hidden: true,
                width: 204,
                displayField: "PropertyTypeDesc",
                valueField: "PropertyType",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                colspan: 4,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["PropertyType", "PropertyTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getPropertyTypeListOutput:PropertyTypeList.PropertyType",
                    sourceBinding: "resetNS:PropertyTypeList.PropertyType",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyType"]
                }
            },
            {
                xtype: "label",
                sciId: "label213",
                text: this.b_Description,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "combo84",
                width: 204,
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                scViewMode: "Advanced",
                hidden: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.DescriptionQryType"]
                }
            },
            {
                xtype: "textarea",
                sciId: "textarea1",
                colspan: 3,
                height: 50,
                width: 330,
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.Description"],
                    sourceBinding: "resetNS:PropertyMetadata.Description"
                }
            },
            {
                xtype: "label",
                sciId: "copy_of_label213",
                text: this.b_UserComments,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "combo",
                sciId: "copy_of_combo84",
                width: 204,
                displayField: "QueryTypeDesc",
                valueField: "QueryType",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                scViewMode: "Advanced",
                hidden: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["QueryType", "QueryTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "getQueryTypeListOutput:QueryTypeList.StringQueryTypes.QueryType",
                    sourceBinding: "resetNS:QueryTypeList.StringQueryTypes.QueryType",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.UserCommentQryType"]
                }
            },
            {
                xtype: "textarea",
                sciId: "copy_of_textarea1",
                colspan: 3,
                height: 50,
                width: 330,
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.UserComment",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.UserComment"]
                }
            },
            {
                xtype: "label",
                sciId: "label218",
                text: this.b_Author,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "textfield",
                sciId: "textfield245645",
                width: 204,
                scViewMode: "Advanced",
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:UserList.User",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.Createuserid"]
                },
                hidden: true
            },
            {
                xtype: "label",
                sciId: "label233",
                text: this.b_Editor,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "textfield",
                sciId: "textfield334545",
                width: 204,
                colspan: 2,
                scViewMode: "Advanced",
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:UserList.User",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.Modifyuserid"]
                },
                hidden: true
            },
            {
                xtype: "label",
                sciId: "label234",
                text: this.b_Createdbetween,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "xdatetime",
                sciId: "xdatetime",
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.FromCreatets"],
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.FromCreatets"
                }
            },
            {
                xtype: "label",
                sciId: "label235",
                text: this.b_and,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "xdatetime",
                sciId: "xdatetime1",
                colspan: 2,
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.ToCreatets"],
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.ToCreatets"
                }
            },
            {
                xtype: "label",
                sciId: "label236",
                text: this.b_Modifiedbetween,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "xdatetime",
                sciId: "xdatetime2",
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.FromModifyts"],
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.FromModifyts"
                }
            },
            {
                xtype: "label",
                sciId: "copy_of_label235",
                text: this.b_and,
                hidden: true,
                scViewMode: "Advanced"
            },
            {
                xtype: "xdatetime",
                sciId: "copy_of_xdatetime1",
                colspan: 2,
                scViewMode: "Advanced",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.ToModifyts"],
                    sourceBinding: "resetNS:PropertyMetadata.PropertyList.Property.ToModifyts"
                }
            },
            {
                xtype: "hidden",
                sciId: "hidden",
                value: "BETWEEN",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.CreatetsQryType"]
                }
            },
            {
                xtype: "hidden",
                sciId: "hidden1",
                value: "BETWEEN",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.ModifytsQryType"]
                }
            },
            {
                xtype: "hidden",
                sciId: "hidden2",
                value: "FLIKE",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.CreateuseridQryType"]
                }
            },
            {
                xtype: "hidden",
                sciId: "hidden3",
                value: "FLIKE",
                bindingData: {
                    defid: "object",
                    targetBinding: ["getPropertyListAdvancedSearch:PropertyMetadata.PropertyList.Property.ModifyuseridQryType"]
                }
            }],
            collapsible: true,
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton1",
                text: this.b_BasicSearch,
                handler: this.toggleSearchMode,
                scope: this
            }],
            animCollapse: false,
            bbar: [{
                xtype: "sclink",
                sciId: "sclink1",
                text: this.b_SearchModifiedProperties,
                handler: this.doSearchModifiedProp,
                scope: this
            },
            {
                xtype: "tbfill",
                sciId: "tbfill9"
            },
            {
                xtype: "label",
                sciId: "label20",
                text: this.b_OrderBy
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer3"
            },
            {
                xtype: "combo",
                sciId: "combo26",
                displayField: "OrderByAttributeDesc",
                valueField: "OrderByAttribute",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["OrderByAttribute", "OrderByAttributeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    sourceBinding: "setOrderByProp:PropertyMetadata.OrderBy.Attribute.Name",
                    optionsBinding: "optionsForOrderBy:OrderByList.OrderBy",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.OrderBy.Attribute.Name", "getPropertyListAdvancedSearch:PropertyMetadata.OrderBy.Attribute.Name"]
                }
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer1"
            },
            {
                xtype: "combo",
                sciId: "combo15",
                width: 90,
                displayField: "OrderTypeDesc",
                valueField: "OrderType",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["OrderType", "OrderTypeDesc"]
                }),
                bindingData: {
                    defid: "object",
                    optionsBinding: "optionsForOrderByType:OrderByTypeList.OrderByType",
                    targetBinding: ["getPropertyListBasicSearch:PropertyMetadata.OrderBy.Attribute.Desc", "getPropertyListAdvancedSearch:PropertyMetadata.OrderBy.Attribute.Desc"]
                }
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer2"
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer7"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton29",
                text: this.b_Search,
                handler: this.doSearch,
                scope: this,
                iconCls: "sma-search-button"
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer4"
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer5"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton27",
                text: this.b_Reset,
                handler: this.doReset,
                scope: this,
                iconCls: "sma-reset-button"
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer3"
            },
            {
                xtype: "tbspacer",
                sciId: "tbspacer6"
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 5
            }
        },
        {
            xtype: "panel",
            sciId: "panel4",
            title: this.b_Panel4,
            layout: "table",
            border: false,
            header: false,
            height: 20,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
            }
        },
        {
            xtype: "grid",
            sciId: "propertylistgrid",
            title: this.b_PropertyList,
            viewConfig: {
				forceFit: true
		    },
            columns: [{
                defid: "grid-column",
                sciId: "grid-column4",
                header: this.b_Category,
                sortable: true,
                dataIndex: "BaseProperty.Property.Category",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "BaseProperty.Property.Category",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "BaseProperty.Property.Category"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column",
                header: this.b_PropertyName,
                sortable: true,
                width: 140,
                dataIndex: "BaseProperty.Property.BasePropertyName",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "BaseProperty.Property.BasePropertyName",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "BaseProperty.Property.BasePropertyName"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column2",
                header: this.b_FactoryValue,
                sortable: true,
                dataIndex: "BaseProperty.Property.FactoryValue",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "BaseProperty.Property.FactoryValue",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "BaseProperty.Property.FactoryValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "copy_of_grid-column2",
                header: this.b_PropertyValue,
                sortable: true,
                dataIndex: "BaseProperty.Property.ActualPropertyValue",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "BaseProperty.Property.PropertyValue",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "BaseProperty.Property.ActualPropertyValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column6",
                header: this.b_PropertyType,
                sortable: true,
                dataIndex: "PropertyType",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "PropertyType",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyType"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column7",
                header: this.b_Modifiable,
                sortable: true,
                dataIndex: "Modifiable",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "Modifiable",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifiable"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column8",
                header: this.b_ModifiableAtRuntime,
                sortable: true,
                width: 130,
                dataIndex: "ModifiableAtRuntime",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "ModifiableAtRuntime",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ModifiableAtRuntime"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column49",
                header: this.b_UserOverridable,
                sortable: true,
                dataIndex: "UserOverride",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "UserOverride",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "UserOverride"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column50",
                header: this.b_ServerOverridable,
                sortable: true,
                dataIndex: "ServerOverride",
                bindingData: {
                    defid: "object",
                    tAttrBinding: "ServerOverride",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ServerOverride"
                    }
                }
            }],
            height: 400,
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill1"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton3",
                text: this.b_searchScreenCreateProperty,
                handler: this.doCreateProperty,
                scope: this,
                iconCls: "sma-create-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton4",
                text: this.b_ManageProperty,
                handler: this.doManageProperty,
                scope: this,
                iconCls: "sma-detail-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator39"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton78",
                text: this.b_DeleteProperty,
                handler: this.doDeleteProperty,
                scope: this,
                iconCls: "sma-delete-button"
            }],
            bbar: this.getPagingBar,
            sm: this.sm,
            bindingData: {
                defid: "object",
                sourceBinding: "getPropertyList:PropertyMetadataList.PropertyMetadata",
                targetBinding: ["getPropertyList:PropertyMetadataList.PropertyMetadata"],
                pagination: this.getPagination,
                completeRecord: true
            }
        },
        {
            xtype: "panel",
            sciId: "panel2",
            title: this.b_PropertyDetails,
            layout: "table",
            height: 390,
            items: [{
                xtype: "grid",
                sciId: "grid15",
                title: this.b_OverriddenProperties,
                columns: [{
                    defid: "grid-column",
                    sciId: "grid-column65",
                    header: this.b_OverrideType,
                    sortable: true,
                    dataIndex: "PropertyOverride",
                    bindingData: {
                        defid: "object",
                        sFieldConfig: {
                            defid: "object",
                            mapping: "PropertyOverride"
                        }
                    }
                },
                {
                    defid: "grid-column",
                    sciId: "grid-column76",
                    header: this.b_OverriddenFor,
                    sortable: true,
                    width: 130,
                    dataIndex: "PropertyOverrideName",
                    bindingData: {
                        defid: "object",
                        sFieldConfig: {
                            defid: "object",
                            mapping: "PropertyOverrideName"
                        }
                    }
                },
                {
                    defid: "grid-column",
                    sciId: "grid-column77",
                    header: this.b_Value,
                    sortable: true,
                    dataIndex: "ActualPropertyValue",
                    bindingData: {
                        defid: "object",
                        sFieldConfig: {
                            defid: "object",
                            mapping: "ActualPropertyValue"
                        }
                    }
                }],
                width: 500,
                rowspan: 2,
                height: 310,
                bindingData: {
                    defid: "object",
                    sourceBinding: "showOverriddenProperties:PropertyList.Property"
                }
            },
            {
                xtype: "panel",
                sciId: "panel15",
                title: this.b_Panel15,
                layout: "table",
                items: [{
                    xtype: "label",
                    sciId: "label13",
                    text: this.b_Description,
                    scAlignToLeft: "true"
                },
                {
                    xtype: "textarea",
                    sciId: "textarea",
                    width: 380,
                    height: 200,
                    readOnly: true,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "showOverriddenProperties:Description"
                    },
                    scuiDataType:"Description-1500"
                }],
                header: false,
                layoutConfig: {
                    defid: "tableLayoutConfig",
                    columns: 1
                }
            },
            {
                xtype: "panel",
                sciId: "panel16",
                title: this.b_Panel16,
                layout: "table",
                items: [{
                    xtype: "label",
                    sciId: "label14",
                    text: this.b_PermissibleValues,
                    scAlignToLeft: "true"
                },
                {
                    xtype: "grid",
                    sciId: "grid6",
                    title: this.b_Grid1,
                    columns: [{
                        defid: "grid-column",
                        sciId: "grid-column51",
                        header: this.b_Values,
                        sortable: true,
                        width: 145,
                        dataIndex: "Name",
                        bindingData: {
                            defid: "object",
                            sFieldConfig: {
                                defid: "object",
                                mapping: "Name"
                            }
                        }
                    }],
                    height: 200,
                    width: 150,
                    header: false,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "showOverriddenProperties:Values.Value"
                    }
                }],
                header: false,
                layoutConfig: {
                    defid: "tableLayoutConfig",
                    columns: 1
                }
            }],
            collapsible: true,
            autoScroll: true,
            collapsed: true,
            animCollapse: false,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 3
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

sc.sma.propertyMgt = function(config) {
    sc.sma.propertyMgt.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.propertyMgt, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.propertyMgt',
    getUIConfig: sc.sma.propertyMgtUIConfig,
	currentSearchTargetNamespace: "getPropertyListBasicSearch",
	searchResults:null,
	selectedResult: null,
	createPropertyScreen: null,
	managePropertyScreen: null,
	catres:null,
	deletePropertyButton:null,
	selectedObj:{
		basePropertyName:null,
		category:null
	},
	namespaces: {
        target: ['getPropertyListBasicSearch', 'getPropertyListAdvancedSearch'],
        source: ['getQueryTypeListOutput', 'resetNS', 'getCategoriesListOutput', 'getPropertyList', 'getUserListOutput', 'getPropertyTypeListOutput', 'getOverrideTypeListOutput', 'getModifiableListOutput', 'showOverriddenProperties', 'optionsForOrderBy', 'optionsForOrderByType']
    },
	performInitialization:function(){
			var grdPanel = this.find('sciId','propertylistgrid').pop();
			grdPanel.on('rowclick',function(grid,rowIndex,e){
				var record = grid.getStore().getAt(rowIndex);  // Get the Record
				this.selectedResult = record;
				var basePropertyName = record.get('BaseProperty.Property.BasePropertyName');
				var categoryName = record.get('BaseProperty.Property.Category');
				var propTyp = record.get('PropertyType');
				if (Ext.isEmpty(this.deletePropertyButton)){
					var tBar = grid.getTopToolbar();
					this.deletePropertyButton = tBar.items.itemAt(5);
				}
				if (propTyp === 'SYSTEM'){
					this.deletePropertyButton.disable();
				} else {
					this.deletePropertyButton.enable();
				}
				var selObj = this.selectedObj;
				selObj.category = categoryName;
				selObj.basePropertyName = basePropertyName;
				var resObj = record.json;	// json is not an exposed property
				this.setModel({
						"PropertyList":resObj.PropertyList,
						"Description":resObj.Description,
						"Values":resObj.Values
					},"showOverriddenProperties",{
							clearOldVals: true
					});
			},this);
			var store = grdPanel.getStore();
			var errFn = function(st,options,error,response){
				this.hideMask();
			};
			store.on({
				'resetpaging': function(st){
					this.showMask();
					this.setFreshSearch();
				},
				'pagingload':function(st,records,options){
					if (this.freshSearch === true ){
						if(Ext.isEmpty(records) || records.length === 0){
							sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
							//return;
						}else{
							//To remove a already set message
							sc.plat.ScreenTitle.setMsg(null);
							this.getSrchPanl().collapse();
							this.find('sciId','panel2').pop().expand();
						}
					}
					this.setDoneWithFreshSearch();
					this.hideMask();
				} ,
				'pagingloadexception': errFn,
				'scuiexception': errFn,
				'paginginvalidpage': errFn,
				scope: this
			});			
	},
	srchPanl:null,
	getSrchPanl:function(){
		if (Ext.isEmpty(this.srchPanl)){
			this.srchPanl = this.find('sciId','panel').pop();
		}
		return this.srchPanl;
	},
	freshSearch: true,
	setFreshSearch: function(){
		this.freshSearch = true;
	},
	setDoneWithFreshSearch:function(){
		this.freshSearch = false;
	},
	mask: new Ext.LoadMask(Ext.getBody(),{msg: this["b_Loading"]}),
	showMask : function(){
		this.mask.show();
	},
	hideMask : function(){
		this.mask.hide();
	},
	populateComboBoxes:function(qryres,catres){
		this.catres = catres;
		this.populateQueryTypeComboBox(qryres);
		this.populateOverrideTypeComboBox();
		this.populateYesNoComboBoxes();
		this.populateCategoryComboBox(catres);
		this.populatePropertyTypeComboBox();
		this.populateOrderByComboBox();
	},
	populateOrderByComboBox:function(qryres){
		qryres = {"OrderByList":{"OrderBy":[{"OrderByAttribute":"Category","OrderByAttributeDesc":"Category"},{"OrderByAttribute":"BasePropertyName","OrderByAttributeDesc":"Property Name"},{"OrderByAttribute":"PropertyType","OrderByAttributeDesc":"Property Type"},{"OrderByAttribute":"Modifiable","OrderByAttributeDesc":"Modifiable"},{"OrderByAttribute":"ModifiableAtRuntime","OrderByAttributeDesc":"Modifiable At Runtime"},{"OrderByAttribute":"UserOverride","OrderByAttributeDesc":"User Overridable"},{"OrderByAttribute":"ServerOverride","OrderByAttributeDesc":"Server Overridable"}]}};
		
		this.setModel(qryres,"optionsForOrderBy",{
			clearOldVals: true
        });

		qryres = {"OrderByTypeList":{"OrderByType":[{"OrderType":"N","OrderTypeDesc":"[A - Z]"},{"OrderType":"Y","OrderTypeDesc":"[Z - A]"}]}};

		this.setModel(qryres,"optionsForOrderByType",{
			clearOldVals: true
        });
		var panel = this.getSrchPanl();
		var botBar = panel.getBottomToolbar();
		botBar.el.addClass('sma-bbar');
		panel.body.addClass('sma-bbar-panel-body');
	},
	populateQueryTypeComboBox:function(qryres){	
		this.getLocalizeQryTypeDesc(qryres);
		this.setModel(qryres,"getQueryTypeListOutput",{
			clearOldVals: true
        });
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

	populatePropertyTypeComboBox:function(qryres){
		qryres = {"PropertyTypeList": {"PropertyType":[{"PropertyType":"CUSTOM","PropertyTypeDesc":"CUSTOM"},{"PropertyType":"SYSTEM","PropertyTypeDesc":"SYSTEM"}]}};
		this.setModel(qryres,"getPropertyTypeListOutput",{
			clearOldVals: true
        });
	},

	populateOverrideTypeComboBox:function(qryres){
		qryres = {"OverrideTypeList": {"OverrideType":[{"PropertyOverride":"USER","PropertyOverrideDesc":"USER"},{"PropertyOverride":"SERVER","PropertyOverrideDesc":"SERVER"},{"PropertyOverride":"BASE","PropertyOverrideDesc":"USER OR SERVER"}]}};
		this.setModel(qryres,"getOverrideTypeListOutput",{
			clearOldVals: true
        });
	},

	populateCategoryComboBox:function(catres){
		var categories = catres.PropertyMetadataList.PropertyMetadata;
		if (!Ext.isEmpty(categories)){
			for (var i = 0;i < categories.length ; i++ ){
				categories[i].CategoryNameDesc = categories[i].Category;
			}
		}
		this.setModel(catres,"getCategoriesListOutput",{
			clearOldVals: true
        });
	},


	populateYesNoComboBoxes:function(qryres){
		qryres = {"ModifiableList": {"Modifiable":[{"Modifiable":"Y","ModifiableDesc":"Y"},{"Modifiable":"N","ModifiableDesc":"N"}]}};
		this.setModel(qryres,"getModifiableListOutput",{
			clearOldVals: true
        });
	},

	toggleSearchMode:function(){
		var visible = true;
		var panel = this.getSrchPanl();
		var tBar = panel.getTopToolbar();
		var tbarBut = tBar.items.itemAt(1);	// get the tbar button
		if (this.currentSearchTargetNamespace === 'getPropertyListBasicSearch'){
			visible = true;
			this.currentSearchTargetNamespace = 'getPropertyListAdvancedSearch';
			tbarBut.setText(this["b_AdvancedSearch"]);
		} else {
			visible = false;
			this.currentSearchTargetNamespace = 'getPropertyListBasicSearch';
			tbarBut.setText(this["b_BasicSearch"]);
		}
		var arrOfCmp = this.find('scViewMode','Advanced');
		for (var i = 0;i < arrOfCmp.length ;i++ ){
			arrOfCmp[i].setVisible(visible);
		}
	},
	setFocus: function(widget) {
        if(widget){
        	widget.focus.defer(100, widget);
        }
    },
	goBackToSrch:function(){
		this.hide();
		this.searchList.show();
	},
	dm : sc.plat.DataManager,

	doSearchModifiedProp:function(){
		var searchObj = {
			"PropertyMetadata":{
				"PropertyType":"SYSTEM",
				"PropertyList":{
					"Property":{
						"PropertyValueQryType":"NOTVOID",
						"PropertyOverride":"BASE"
					}
				}
			}
		};
		var tm = this.getTargetModel(this.currentSearchTargetNamespace);
		var tm1 = tm[0];
		if (!Ext.isEmpty(tm1.PropertyMetadata.OrderBy.Attribute.Name)){
			searchObj.PropertyMetadata.OrderBy = tm1.PropertyMetadata.OrderBy;
		}
		var gridPnl =  this.find('sciId','propertylistgrid')[0];
		this.setModel({},"showOverriddenProperties", {
			clearOldVals: true
        });
		gridPnl.getStore().startPagination({
					getPropertyList : Ext.util.JSON.encode(searchObj)
				});
	},

	doSearch:function(){
		if (this.dm.hasError(this,this.currentSearchTargetNamespace)) {
			//var scp = this;
			Ext.Msg.alert(this.b_Error, this.b_errSearchScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this,this.currentSearchTargetNamespace));
			},this);
			return;
		}
		var tm = this.getTargetModel(this.currentSearchTargetNamespace);
		var tm1 = tm[0];
		if (Ext.isEmpty(tm1.PropertyMetadata.OrderBy.Attribute.Name)){
			delete tm1.PropertyMetadata.OrderBy;
		}
		if (tm1.PropertyMetadata.PropertyList.Property.PropertyOverride === "BASE"){
			tm1.PropertyMetadata.PropertyList.Property.PropertyOverrideQryType = "NE";
		}
		var gridPnl =  this.find('sciId','propertylistgrid')[0];
		this.setModel({},"showOverriddenProperties", {
			clearOldVals: true
        });
		gridPnl.getStore().startPagination({
					getPropertyList : Ext.util.JSON.encode(tm1)
				});
	},

	resHandler:function(result,options){
		var res = Ext.decode(result.responseText);
		var propres = res.propres;
		this.searchResults = propres;
		if(Ext.isEmpty(propres.PropertyMetadataList.PropertyMetadata)){
			sc.plat.ScreenTitle.setMsg(this.No_Result_Found,"err");
			//return;
		}else{
			//To remove a already set message
			sc.plat.ScreenTitle.setMsg(null);
			var panel = this.find('sciId','panel').pop();
			panel.collapse();
		}
		var out = propres; 
		//blank the lower panel which has overridden properties, desc, perm values
		this.setModel({},"showOverriddenProperties", {
			clearOldVals: true
        });
		// populate the grid
		this.setModel(out, "getPropertyList", {
			clearOldVals: true
        });
	},
	doReset:function(){
		this.setModel({},
        "resetNS", {
            clearOldVals: true
        });
	},
	doCreateProperty:function(){
		if (Ext.isEmpty(this.createPropertyScreen)){
			this.createPropertyScreen = new sc.sma.propertyMgtCreateProperty({"searchList": this,"catres": this.catres});
		}
		this.hide();
		//To remove a already set message
		sc.plat.ScreenTitle.setMsg(null);
		if (this.createPropertyScreen.rendered === false){
			this.createPropertyScreen.render("mainBodyPanel");
		} else {
			this.createPropertyScreen.show();
		}
		this.createPropertyScreen.setModel({},
        "resetNS", {
            clearOldVals: true
        });
		this.createPropertyScreen.populateComboBoxes(this.catres);
	},
	getSelectedTableRowObj: function() {
        var gridPnl = this.find('sciId','propertylistgrid')[0];
		var rec = gridPnl.getSelectionModel().getSelected();
		if (Ext.isEmpty(rec)) {
			return null;
		}
		return rec;
    },
	sm : new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),
	doManageProperty:function(){
		var selectedRow = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert(this['b_Alert'],this.Please_select_a_row);
            return;
        }
		var propRow = {
			"BasePropertyName": this.selectedObj.basePropertyName,
			"Category":this.selectedObj.category
		};
		sc.sma.AjaxUtils.request({
			actionNS : "sma",
			action : "getManagePropertyList",
			inputNS : "PropertyMetadata",
			inputObj : {"PropertyMetadata":propRow},
			extraParams : "",
			success : this.managePropertyResHandler,
			scope : this
		});
	},

	getManagePropertyScreen:function(searchList){
		if (Ext.isEmpty(this.managePropertyScreen)){
			this.managePropertyScreen = new sc.sma.propertyMgtManageProperty({"searchList": searchList});
		}
		return this.managePropertyScreen;
	},

	managePropertyResHandler:function(result,options){
		var res = Ext.decode(result.responseText);
		/*if (Ext.isEmpty(this.managePropertyScreen)){
			this.managePropertyScreen = new sc.sma.propertyMgtManageProperty({"searchList": this,"catres": this.catres});
		}*/
		this.managePropertyScreen = this.getManagePropertyScreen(this);
		this.hide();
		if (this.managePropertyScreen.rendered === false){
			this.managePropertyScreen.render("mainBodyPanel");
		} else {
			this.managePropertyScreen.show();
		}
		this.managePropertyScreen.performInitialization({"mngPropData":res});
		
	},

	doDeleteProperty:function(){
		var selectedRow = this.getSelectedTableRowObj();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert(this['b_Alert'],this.Please_select_a_row_to_delete);
            return;
        }
		var fcn = function(btn){
			if (btn == 'yes') {
				var propRow = {
					"BasePropertyName": this.selectedObj.basePropertyName,
					"Category":this.selectedObj.category
				};
				propRow.Action = 'Delete';
				propRow.PropertyOverride='BASE';
				propRow.DeleteOverrides='Y';
				sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property": propRow},
				extraParams : "",
				success : this.delHandler,
				scope : this
				});
			}
		}
		//TO-DO Warnig Message should be localized
		var deleteMessage = this['b_warning_delete_property_with_override'];
        Ext.Msg.confirm(this['b_Confirm'],deleteMessage, fcn, this); 
	},

	delHandler:function(result,options){
		if (Ext.isEmpty(result.responseText)) {
            return;
        }
		var res = Ext.util.JSON.decode(result.responseText);
		var gridPnl = this.find('sciId','propertylistgrid')[0];
        var store = gridPnl.getStore();
        var recIndex = store.findBy(function(record,id){
			var retObj = res.managepropres.Property
			if (retObj.BasePropertyName === record.get('BaseProperty.Property.BasePropertyName')){
				if (retObj.Category === record.get('BaseProperty.Property.Category')){
					return true;
				}
			}
		},this);

        if (recIndex != -1) {
			var rec = store.getAt(recIndex);
            if (result.statusText == 'OK') {
				store.remove(rec);
				if (store.getPaginationStrategy() == "GENERIC") {
					gridPnl.getBottomToolbar().resetPaging();
					Ext.MessageBox.show({
						title : this['b_Deletion_Successful'],
						msg : this['b_The_property_was_deleted_successfully_msg'],
						width : 450,
						buttons : Ext.Msg.OK,
						icon : Ext.MessageBox.INFO
					});
				}
            } 
        } else {		// this is needed because if someone creates a property and deletes it in the manageProperty screen then this wont be there in the store in search list
			Ext.MessageBox.show({
				title : this['b_Deletion_Successful'],
				msg : this['b_The_property_was_deleted_successfully'],
				width : 450,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
        }
	},
	getPagination: {
			strategy : 'GENERIC',
			url : '/'
					+ sc.plat.info.Application.getApplicationContext()
					+ '/sma/getPropertyList.do',
			pageSize : 10,
			root : 'PropertyMetadataList.PropertyMetadata'
		},
	getPagingBar: new sc.sma.ui.PagingToolbar({paginationStrategy: 'GENERIC'})
});
//Ext.reg('xtype_name', sc.sma.propertyMgt);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.propertyMgtAuditHistoryUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel24",
            title: this.b_AuditHistory,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label203",
                text: this.b_PropertyName
            },
            {
                xtype: "textfield",
                sciId: "textfield53",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.BasePropertyName"
                }
            },
            {
                xtype: "label",
                sciId: "label51",
                text: this.b_Category
            },
            {
                xtype: "textfield",
                sciId: "textfield17",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.Category"
                }
            },
            {
                xtype: "label",
                sciId: "label169",
                text: this.b_OverriddenFor
            },
            {
                xtype: "textfield",
                sciId: "textfield39",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.PropertyOverrideName"
                }
            },
            {
                xtype: "label",
                sciId: "label170",
                text: this.b_OverrideType
            },
            {
                xtype: "textfield",
                sciId: "textfield40",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.PropertyOverride"
                }
            }],
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill6"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton18",
                text: this.b_BacktoManageProperty,
                handler: this.doBackToManageProperty,
                scope: this,
                iconCls: "sma-back-icon"
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 8
            }
        },
        {
            xtype: "grid",
            sciId: "grid26",
            title: this.b_Grid3,
            columns: [{
                defid: "grid-column",
                sciId: "grid-column135",
                header: this.b_Date,
                sortable: true,
                dataIndex: "Modifyts",
                width: 150,
                renderer: this.auditRendererFormat,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyts"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column136",
                header: this.b_Action,
                sortable: true,
                dataIndex: "Action",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Action"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column137",
                header: this.b_Editor,
                sortable: true,
                dataIndex: "Modifyuserid",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyuserid"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column138",
                header: this.b_OverrideType,
                sortable: true,
                dataIndex: "PropertyOverride",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverride"
                    }
                },
                hidden: true
            },
            {
                defid: "grid-column",
                sciId: "grid-column139",
                header: this.b_OverriddenFor,
                sortable: true,
                dataIndex: "PropertyOverrideName",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverrideName"
                    }
                },
                hidden: true
            },
            {
                defid: "grid-column",
                sciId: "grid-column140",
                header: this.b_OldValue,
                sortable: true,
                dataIndex: "OldValue",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "OldValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column141",
                header: this.b_NewValue,
                sortable: true,
                dataIndex: "NewValue",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "NewValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column142",
                header: this.b_UserComments,
                sortable: true,
                dataIndex: "UserComment",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "UserComment"
                    }
                }
            }],
            header: false,
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill20"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton82",
                text: this.b_ResettoSelectedValue,
                handler: this.doResetToSelectedValue,
                scope: this
            }],
            height: 400,
            sm: this.auditsm,
            bindingData: {
                defid: "object",
                sourceBinding: "getAuditList:AuditList.Audit",
                targetBinding: ["getAuditList:AuditList.Audit"]
            },
            store: new Ext.data.JsonStore({
                defid: "jsonstore",
                fields: [{
                    defid: "object",
                    name: "Modifyts",
                    type: "date",
                    dateFormat: this.getAuditTimeStampFormat
                },
                {
                    defid: "object",
                    name: "Action"
                },
                {
                    defid: "object",
                    name: "Modifyuserid"
                },
                {
                    defid: "object",
                    name: "PropertyOverride"
                },
                {
                    defid: "object",
                    name: "PropertyOverrideName"
                },
                {
                    defid: "object",
                    name: "OldValue"
                },
                {
                    defid: "object",
                    name: "NewValue"
                },
                {
                    defid: "object",
                    name: "UserComment"
                }]
            })
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

sc.sma.propertyMgtAuditHistory = function(config) {
    sc.sma.propertyMgtAuditHistory.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.propertyMgtAuditHistory, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.propertyMgtAuditHistory',
    getUIConfig: sc.sma.propertyMgtAuditHistoryUIConfig,
    namespaces: {
        target: ['getAuditList', 'showAuditList'],
        source: ['getAuditList', 'showAuditList']
    },
	selectedResult: null,
	getAuditTimeStampFormat: (function(){
			return 'Y-m-d\\TH:i:sP';
		})(),
	auditRendererFormat: (function(){
			return Ext.util.Format.dateRenderer(sc.plat.Userprefs.getDateHourMinuteFormat());
		})(),
	newValue:null,
	performInitialization:function(input){
		this.shouldRefreshManagePropertyScreen = false;
		this.mngPropScreen = input.mngPropScreen;
		this.basePropName = input.basePropName;
		this.categoryName = input.categoryName;
		this.propOverride = input.propOverride;
		this.propOverrideName = input.propOverrideName;
		this.auditResult = input.auditResult;
		this.mngPropData = input.mngPropData;
		this.overridePropRec = input.overridePropRec;
		var grdPanel = this.find('sciId','grid26')[0];
		var st = grdPanel.getStore();
		this.getTbarResetValBut().enable();
		
		this.setModel({"Property":{"BasePropertyName":this.basePropName,"Category":this.categoryName,"PropertyOverride":this.propOverride,"PropertyOverrideName":this.propOverrideName},"AuditList":this.auditResult.json.AuditList},"getAuditList",{
			clearOldVals: true
		});
			st.sort('Modifyts','DESC');
			grdPanel.on('rowclick',function(grid,rowIndex,e){
				var record = grid.getStore().getAt(rowIndex);  // Get the Record
				this.selectedResult = record;
				this.newValue = record.get('NewValue');
				if ( this.newValue === this.overridePropRec.Property.ActualPropertyValue ){
					this.getTbarResetValBut().disable();
				} else {
					this.getTbarResetValBut().enable();
				}
			},this);
	},
	shouldRefreshManagePropertyScreen:false,
	doBackToManageProperty:function(){
		this.hide();
		this.mngPropScreen.show();
		if (this.shouldRefreshManagePropertyScreen === true){
			this.mngPropScreen.refreshManagePropertyScreen();
		}
	},
	tbarResetVal:null,
	getTbarResetValBut:function(){
		if (Ext.isEmpty(this.tbarResetVal)){
			var grdPanel = this.find('sciId','grid26')[0];
			this.tbarResetVal = grdPanel.getTopToolbar().items.itemAt(1);
		}
		return this.tbarResetVal;
	},
	getSelectedTableRow:function(){
		var gridPnl = this.find('sciId','grid26')[0];
		var rec = gridPnl.getSelectionModel().getSelected();
		if (Ext.isEmpty(rec)) {
			return null;
		}
		return rec;
	},
	MngPropModifyValueInstance:null,
	ModifyValueWin:null,
	initializeMngPropModifyValueWindow:function(){
		this.MngPropModifyValueInstance = new sc.sma.MngPropModifyValue();
            this.ModifyValueWin = new Ext.Window({
                layout: 'fit',
                width: 500,
                height: 375,
                closeAction: 'hide',
                plain: true,
				modal: true,
                title: this["b_Modify_Value_For"],
                items: [this.MngPropModifyValueInstance],
				buttonAlign:'center',
				buttons : [{
					xtype: "button",
					text: this["b_ModifyValueOkayButton"],
					handler: this.doSaveModifyValue,
					scope: this
				},
				{
					xtype: "button",
					sciId: "button1",
					text: this["b_ModifyValueCancelButton"],
					handler: this.doCancel,
					scope: this
				}]
            });
	},
	dm : sc.plat.DataManager,
	doSaveModifyValue:function(){
		if (this.dm.hasError(this.MngPropModifyValueInstance)) {
			//var scp = this;
			Ext.Msg.alert(this["b_Error"], this.errModifyValueScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this.MngPropModifyValueInstance));
			},this);
			return;
		}
		this.shouldRefreshManagePropertyScreen = true;
		this.ModifyValueWin.hide();
		var resToSave = this.MngPropModifyValueInstance.getTargetModel('modifyValue')[0];
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property":resToSave.Property},
				extraParams : "",
				success : this.saveChangesResHandler,
				scope : this
			});
	},
	doCancel:function(){
		this.ModifyValueWin.hide();
	},
	saveChangesResHandler:function(result,options){
		var res = result.json;
		res.managepropres.Property.ChangedCreated = Date.parseDate(res.managepropres.Property.Createts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		res.managepropres.Property.Modifyts = Date.parseDate(res.managepropres.Property.Modifyts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		/*this.setModel(res.managepropres,
        "manageProperty", {
            clearOldVals: true
        });*/
		this.overridePropRec = res.managepropres;
		this.basePropName = this.overridePropRec.Property.BasePropertyName;
		this.categoryName = this.overridePropRec.Property.Category;
		this.propOverride = this.overridePropRec.Property.PropertyOverride;
		this.propOverrideName = this.overridePropRec.Property.PropertyOverrideName;
		if (this.overridePropRec.Property.PropertyMetadata.ModifiableAtRuntime === 'N'){
			Ext.MessageBox.show({
				title : this['b_Value_Modified'],
				msg : this["b_warning_modifiable_at_runtime"],
				width : 450,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
		} else {
			Ext.MessageBox.show({
					title : this['b_Value_Modified'],
					msg : this["b_Value_Modified_successfully"],
					width : 300,
					buttons : Ext.Msg.OK,
					icon : Ext.MessageBox.INFO
				});
		}
		var inpToAud = {"Audit":{"TableName":"PLT_PROPERTY","Reference1":this.basePropName,"Reference2":this.categoryName,
			"Reference3":this.overridePropRec.Property.PropertyOverride,"Reference4":this.overridePropRec.Property.PropertyOverrideName}};
		if (Ext.isEmpty(inpToAud.Audit.Reference2)){
			inpToAud.Audit.Reference2QryType = "VOID";
		}
		inpToAud.Audit.OrderBy = {};
		inpToAud.Audit.OrderBy.Attribute = "Modifyts";
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getAuditList",
				inputNS : "Audit",
				inputObj : inpToAud,
				extraParams : "",
				success : this.auditResHandler,
				scope : this
			});
	},
	auditResHandler:function(result,options){
		this.auditResult = result;
		var grdPanel = this.find('sciId','grid26')[0];
		var st = grdPanel.getStore();
		this.getTbarResetValBut().enable();
		
		this.setModel({"Property":{"BasePropertyName":this.basePropName,"Category":this.categoryName,"PropertyOverride":this.propOverride,"PropertyOverrideName":this.propOverrideName},"AuditList":this.auditResult.json.AuditList},"getAuditList",{
			clearOldVals: true
		});
		st.sort('Modifyts','DESC');
	},
	doResetToSelectedValue:function(){
		var selectedRow = this.getSelectedTableRow();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert(this["b_Alert"],this.b_Please_select_a_row);
            return;
        }
		if (!this.ModifyValueWin) {
			this.initializeMngPropModifyValueWindow();
        }
		var gridPnl = this.find('sciId','grid26')[0];
		var rec = gridPnl.getSelectionModel().getSelected();
		//this.showMask();
		this.MngPropModifyValueInstance.performInitialization({"mngPropData":this.overridePropRec,"scIsBase":false});
        this.ModifyValueWin.show();
		this.MngPropModifyValueInstance.laterInitialization(true,this.newValue);
	},
	auditsm: new Ext.grid.RowSelectionModel({
		singleSelect : true
	})
});
Ext.reg('xtype_name', sc.sma.propertyMgtAuditHistory);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.propertyMgtAuditHistoryBaseUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen18",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel24",
            title: this.b_AuditHistory,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label203",
                text: this.b_PropertyName
            },
            {
                xtype: "textfield",
                sciId: "textfield53",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.BasePropertyName"
                }
            },
            {
                xtype: "label",
                sciId: "label51",
                text: this.b_Category
            },
            {
                xtype: "textfield",
                sciId: "textfield17",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "getAuditList:Property.Category"
                }
            }],
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill6"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton18",
                text: this.b_BacktoManageProperty,
                handler: this.doBackToManageProperty,
                scope: this,
                iconCls: "sma-back-icon"
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
            }
        },
        {
            xtype: "grid",
            sciId: "grid26",
            title: this.b_Grid3,
            columns: [{
                defid: "grid-column",
                sciId: "grid-column135",
                header: this.b_Date,
                sortable: true,
                dataIndex: "Modifyts",
                width: 150,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyts"
                    }
                },
                renderer: this.auditRendererFormat
            },
            {
                defid: "grid-column",
                sciId: "grid-column136",
                header: this.b_Action,
                sortable: true,
                dataIndex: "Action",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Action"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column137",
                header: this.b_Editor,
                sortable: true,
                dataIndex: "Modifyuserid",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyuserid"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column138",
                header: this.b_OverrideType,
                sortable: true,
                dataIndex: "PropertyOverride",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverride"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column139",
                header: this.b_OverriddenFor,
                sortable: true,
                dataIndex: "PropertyOverrideName",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverrideName"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column140",
                header: this.b_OldValue,
                sortable: true,
                dataIndex: "OldValue",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "OldValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column141",
                header: this.b_NewValue,
                sortable: true,
                dataIndex: "NewValue",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "NewValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column142",
                header: this.b_UserComments,
                sortable: true,
                dataIndex: "UserComment",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "UserComment"
                    }
                }
            }],
            header: false,
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill20"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton80",
                text: this.b_HideOverridenProperties,
                handler: this.doToggleOverridenProperties,
                scope: this
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator40"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton82",
                text: this.b_ResettoSelectedValue,
                handler: this.doResetToSelectedValue,
                scope: this
            }],
            height: 400,
            bindingData: {
                defid: "object",
                sourceBinding: "getAuditList:AuditList.Audit",
                targetBinding: ["getAuditList:AuditList.Audit"]
            },
            store: new Ext.data.JsonStore({
                defid: "jsonstore",
                fields: [{
                    defid: "object",
                    name: "Modifyts",
                    type: "date",
                    dateFormat: this.getAuditTimeStampFormat
                },
                {
                    defid: "object",
                    name: "Action"
                },
                {
                    defid: "object",
                    name: "Modifyuserid"
                },
                {
                    defid: "object",
                    name: "PropertyOverride"
                },
                {
                    defid: "object",
                    name: "PropertyOverrideName"
                },
                {
                    defid: "object",
                    name: "OldValue"
                },
                {
                    defid: "object",
                    name: "NewValue"
                },
                {
                    defid: "object",
                    name: "UserComment"
                }]
            })
        }],
        sm: this.auditsm
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

sc.sma.propertyMgtAuditHistoryBase = function(config) {
    sc.sma.propertyMgtAuditHistoryBase.superclass.constructor.call(this, config);
	
}
Ext.extend(sc.sma.propertyMgtAuditHistoryBase, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.propertyMgtAuditHistoryBase',
    getUIConfig: sc.sma.propertyMgtAuditHistoryBaseUIConfig,
    namespaces: {
        target: ['getAuditList', 'showAuditList'],
        source: ['getAuditList', 'showAuditList']
    },
	selectedResult: null,
	getAuditTimeStampFormat: (function(){
			return 'Y-m-d\\TH:i:sP';
		})(),
	auditRendererFormat: (function(){
			return Ext.util.Format.dateRenderer(sc.plat.Userprefs.getDateHourMinuteFormat());
		})(),
	newValue:null,
	performInitialization:function(input){
		this.shouldRefreshManagePropertyScreen = false;
		this.mngPropScreen = input.mngPropScreen;
		this.basePropName = input.basePropName;
		this.categoryName = input.categoryName;
		this.auditResult = input.auditResult;
		this.mngPropData = input.mngPropData;
		var grdPanel = this.find('sciId','grid26')[0];
		var st = grdPanel.getStore();
		this.currentView = 'All';
		this.getTbarResetValBut().enable();
		this.getTbarBut().setText(this["b_HideOverridenProperties"]);
		
		this.setModel({"Property":{"BasePropertyName":this.basePropName,"Category":this.categoryName},"AuditList":this.auditResult.json.AuditList},"getAuditList",{
			clearOldVals: true
		});
		st.clearFilter();
		st.sort('Modifyts','DESC');
		var grdPanel = this.find('sciId','grid26')[0];
			grdPanel.on('rowclick',function(grid,rowIndex,e){
				var record = grid.getStore().getAt(rowIndex);  // Get the Record
				this.selectedResult = record;
				var propOverride = record.get('PropertyOverride');
				this.newValue = record.get('NewValue');
				if (propOverride !== 'BASE' || this.newValue === this.mngPropData.Property.ActualPropertyValue || this.mngPropData.Property.PropertyMetadata.Modifiable === 'N'){
					this.getTbarResetValBut().disable();
				} else {
					this.getTbarResetValBut().enable();
				}
			},this);
		
	},
	tBarBut:null,
	getTbarBut:function(){
		if (Ext.isEmpty(this.tBarBut)){
			var grdPanel = this.find('sciId','grid26')[0];
			this.tBarBut = grdPanel.getTopToolbar().items.itemAt(1);
		}
		return this.tBarBut;
	},
	tbarResetVal:null,
	getTbarResetValBut:function(){
		if (Ext.isEmpty(this.tbarResetVal)){
			var grdPanel = this.find('sciId','grid26')[0];
			this.tbarResetVal = grdPanel.getTopToolbar().items.itemAt(3);
		}
		return this.tbarResetVal;
	},
	shouldRefreshManagePropertyScreen:false,
	doBackToManageProperty:function(){
		this.hide();
		this.mngPropScreen.show();
		if (this.shouldRefreshManagePropertyScreen === true){
			this.mngPropScreen.refreshManagePropertyScreen();
		}
	},
	doToggleOverridenProperties:function(){
		var grdPanel = this.find('sciId','grid26')[0];
		var st = grdPanel.getStore();
		if (this.currentView === 'All'){
			this.currentView = 'BASE';
			st.filter('PropertyOverride','BASE',true,false);
			this.getTbarBut().setText(this["b_ShowOverridenProperties"]);
		} else {
			this.currentView = 'All';
			st.clearFilter();
			this.getTbarBut().setText(this["b_HideOverridenProperties"]);
		}
	},
	getSelectedTableRow:function(){
		var gridPnl = this.find('sciId','grid26')[0];
		var rec = gridPnl.getSelectionModel().getSelected();
		if (Ext.isEmpty(rec)) {
			return null;
		}
		return rec;
	},
	currentView:'All',
	MngPropModifyValueInstance:null,
	ModifyValueWin:null,
	initializeMngPropModifyValueWindow:function(){
		this.MngPropModifyValueInstance = new sc.sma.MngPropModifyValue();
            this.ModifyValueWin = new Ext.Window({
                layout: 'fit',
                width: 500,
                height: 375,
                closeAction: 'hide',
                plain: true,
				modal: true,
                title: this["b_Modify_Value_For"],
                items: [this.MngPropModifyValueInstance],
				buttonAlign:'center',
				buttons : [{
					xtype: "button",
					text: this["b_ModifyValueOkayButton"],
					handler: this.doSaveModifyValue,
					scope: this
				},
				{
					xtype: "button",
					sciId: "button1",
					text: this["b_ModifyValueCancelButton"],
					handler: this.doCancel,
					scope: this
				}]
            });
	},
	dm : sc.plat.DataManager,
	doSaveModifyValue:function(){
		if (this.dm.hasError(this.MngPropModifyValueInstance)) {
			//var scp = this;
			Ext.Msg.alert(this["b_Error"], this.errModifyValueScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this.MngPropModifyValueInstance));
			},this);
			return;
		}
		this.shouldRefreshManagePropertyScreen = true;
		this.ModifyValueWin.hide();
		var resToSave = this.MngPropModifyValueInstance.getTargetModel('modifyValue')[0];
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property":resToSave.Property},
				extraParams : "",
				success : this.saveChangesResHandler,
				scope : this
			});
	},
	doCancel:function(){
		this.ModifyValueWin.hide();
	},
	saveChangesResHandler:function(result,options){
		var res = result.json;
		res.managepropres.Property.ChangedCreated = Date.parseDate(res.managepropres.Property.Createts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		res.managepropres.Property.Modifyts = Date.parseDate(res.managepropres.Property.Modifyts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		this.mngPropData = res.managepropres;
		this.basePropName = this.mngPropData.Property.BasePropertyName;
		this.categoryName = this.mngPropData.Property.Category;
		if (this.mngPropData.Property.PropertyMetadata.ModifiableAtRuntime === 'N'){
			Ext.MessageBox.show({
				title : this['b_Value_Modified'],
				msg : this["b_warning_modifiable_at_runtime"],
				width : 450,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
		} else {
			Ext.MessageBox.show({
					title : this['b_Value_Modified'],
					msg : this["b_Value_Modified_successfully"],
					width : 300,
					buttons : Ext.Msg.OK,
					icon : Ext.MessageBox.INFO
				});
		}
		var inpToAud = {"Audit":{"TableName":"PLT_PROPERTY","Reference1":this.basePropName,"Reference2":this.categoryName}};
		if (Ext.isEmpty(inpToAud.Audit.Reference2)){
			inpToAud.Audit.Reference2QryType = "VOID";
		}
		inpToAud.Audit.OrderBy = {};
		inpToAud.Audit.OrderBy.Attribute = "Modifyts";
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getAuditList",
				inputNS : "Audit",
				inputObj : inpToAud,
				extraParams : "",
				success : this.auditResHandler,
				scope : this
			});
	},
	auditResHandler:function(result,options){
		//this.performInitialization({"mngPropScreen": this,"basePropName":this.basePropName,"categoryName":this.categoryName,"auditResult":result,"mngPropData":this.mngPropData});
		this.auditResult = result;
		var grdPanel = this.find('sciId','grid26')[0];
		var st = grdPanel.getStore();
		//this.currentView = 'All';
		this.getTbarResetValBut().enable();
		//this.getTbarBut().setText(this["b_HideOverridenProperties"]);
		
		this.setModel({"Property":{"BasePropertyName":this.basePropName,"Category":this.categoryName},"AuditList":this.auditResult.json.AuditList},"getAuditList",{
			clearOldVals: true
		});
		//st.clearFilter();
		st.sort('Modifyts','DESC');
		if (this.currentView !== 'All'){
			st.filter('PropertyOverride','BASE',true,false);
		}
		
	},
	doResetToSelectedValue:function(){
		var selectedRow = this.getSelectedTableRow();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert('Alert',this.b_Please_select_a_row);
            return;
        }
		if (!this.ModifyValueWin) {
			this.initializeMngPropModifyValueWindow();
        }
		var gridPnl = this.find('sciId','grid26')[0];
		var rec = gridPnl.getSelectionModel().getSelected();
		//this.showMask();
		this.MngPropModifyValueInstance.performInitialization({"mngPropData":this.mngPropData,"scIsBase":true});
        this.ModifyValueWin.show();
		this.MngPropModifyValueInstance.laterInitialization(true,this.newValue);
	},
	auditsm: new Ext.grid.RowSelectionModel({
		singleSelect : true
	})
});
Ext.reg('xtype_name', sc.sma.propertyMgtAuditHistoryBase);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.propertyMgtCreatePropertyUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel13",
            title: this.b_CreatePropertyPanelTitle,
            layout: "table",
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill12"
            },
            {
                xtype: "tbbutton",
                sciId: "createPropertyButton",
                text: this.b_CreateProperty,
                handler: this.doCreateProperty,
                scope: this,
                iconCls: "sma-create-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator3"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton46",
                text: this.b_BacktoSearch,
                handler: this.doBackToSearch,
                scope: this,
                iconCls: "sma-back-icon"
            }],
            items: [{
                xtype: "label",
                sciId: "label47",
                text: this.b_PropertyName,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "textfield",
                sciId: "textfield14",
                allowBlank: false,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.BasePropertyName"],
                    sourceBinding: "resetNS:ResetList.Reset"
                }
            },
            {
                xtype: "label",
                sciId: "label51",
                text: this.b_Category
            },
            {
                xtype: "combo",
                sciId: "combo32",
                width: 128,
                displayField: "CategoryNameDesc",
                valueField: "Category",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                validateOnBlur: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.Category"],
                    optionsBinding: "getCategoriesListOutput:PropertyMetadataList.PropertyMetadata",
                    sourceBinding: "resetNS:ResetList.Reset"
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Category", "CategoryNameDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label52",
                text: this.b_PropertyType
            },
            {
                xtype: "textfield",
                sciId: "textfield12",
                value: "CUSTOM",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.PropertyType"]
                }
            },
            {
                xtype: "label",
                sciId: "label55",
                text: this.b_DataType
            },
            {
                xtype: "textfield",
                sciId: "textfield18",
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.DataType"],
                    sourceBinding: "resetNS:ResetList.Reset"
                }
            },
            {
                xtype: "label",
                sciId: "label53",
                text: this.b_Modifiable,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo34",
                width: 128,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                allowBlank: false,
                validateOnBlur: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.Modifiable"],
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:ResetList.Reset"
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label54",
                text: this.b_ModifiableAtRuntime,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo35",
                width: 128,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                allowBlank: false,
                validateOnBlur: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.ModifiableAtRuntime"],
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:ResetList.Reset"
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label56",
                text: this.b_ServerOverridable,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo36",
                width: 128,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                allowBlank: false,
                validateOnBlur: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.ServerOverride"],
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:ResetList.Reset"
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label57",
                text: this.b_UserOverridable,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo37",
                width: 128,
                displayField: "ModifiableDesc",
                valueField: "Modifiable",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                allowBlank: false,
                validateOnBlur: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.UserOverride"],
                    optionsBinding: "getModifiableListOutput:ModifiableList.Modifiable",
                    sourceBinding: "resetNS:ResetList.Reset"
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Modifiable", "ModifiableDesc"]
                })
            },
            {
                xtype: "label",
                sciId: "label38",
                text: this.b_Description,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "textarea",
                sciId: "textarea6",
                colspan: 3,
                width: 350,
                height: 100,
                allowBlank: false,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.Description"],
                    sourceBinding: "resetNS:ResetList.Reset"
                },
				scuiDataType:"Description-1500"
            },
            {
                xtype: "label",
                sciId: "label40",
                text: this.b_UserComments
            },
            {
                xtype: "textarea",
                sciId: "textarea9",
                colspan: 3,
                width: 350,
                height: 100,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.UserComment"],
                    sourceBinding: "resetNS:ResetList.Reset"
                }
            },
            {
                xtype: "label",
                sciId: "label50",
                text: this.b_FactoryValue
            },
            {
                xtype: "textfield",
                sciId: "textfield16",
                disabled: true
            },
            {
                xtype: "label",
                sciId: "label171",
                text: this.b_PropertyValue,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "textfield",
                sciId: "textfield41",
                allowBlank: false,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyValue"],
                    sourceBinding: "resetNS:ResetList.Reset"
                },
				scuiDataType:"PropValParam"
            },
            {
                xtype: "label",
                sciId: "label41",
                text: this.b_PermissibleValues
            },
            {
                xtype: "editorgrid",
                sciId: "permissibleValuesGrid",
                title: this.b_PermissibleValues,
                columns: [{
                    defid: "grid-column",
                    sciId: "grid-column52",
                    header: this.b_Values,
                    sortable: true,
                    width: 145,
                    editor: this.permissibleValTxtFld,
                    bindingData: {
                        defid: "object",
                        tAttrBinding: "Name",
                        sFieldConfig: {
                            defid: "object",
                            mapping: "Name"
                        }
                    }
                }],
                width: 150,
                header: false,
                height: 200,
                tbar: [{
                    xtype: "tbbutton",
                    sciId: "tbbutton2",
                    text: this.b_AddValue,
                    handler: this.doAddPermissibleValue,
                    scope: this
                },
                {
                    xtype: "tbseparator",
                    sciId: "tbseparator1"
                },
                {
                    xtype: "tbbutton",
                    sciId: "tbbutton4",
                    text: this.b_DeleteValue,
                    handler: this.doDeletePermissibleValue,
                    scope: this
                }],
                colspan: 3,
                clicksToEdit: 1,
                sm: this.sm,
                bindingData: {
                    defid: "object",
                    targetBinding: ["createProperty:Property.PropertyMetadata.Values.Value"],
                    sourceBinding: "resetNS:ResetList.Reset"
                },
				scuiDataType:"PropValParam"
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 8
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

sc.sma.propertyMgtCreateProperty = function(config) {
    sc.sma.propertyMgtCreateProperty.superclass.constructor.call(this, config);
	this.searchList = config.searchList;
	this.catres = config.catres;
}
Ext.extend(sc.sma.propertyMgtCreateProperty, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.propertyMgtCreateProperty',
    getUIConfig: sc.sma.propertyMgtCreatePropertyUIConfig,
	namespaces: {
        target: ['createProperty'],
        source: ['getCategoriesListOutput', 'getModifiableListOutput', 'resetNS']
    },
	shouldNavigateToSrch:function(btn) {
		if (btn == 'yes') {
			this.goBackToSrch();
		}
	},
	sm : new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),
	setFocus: function(widget) {
        if(widget){
        	widget.focus.defer(100, widget);
        }
    },
	goBackToSrch:function(){
		this.hide();
		this.searchList.show();
	},
	dm : sc.plat.DataManager,
	permissibleValTxtFld: new Ext.form.TextField({
        validateOnBlur: false,
        allowBlank: false
    }),
	permValRecord: Ext.data.Record.create([{
				name : 'Name'
			}]),
	getPermValGrid:function(){
		return this.find('sciId','permissibleValuesGrid').pop();
	},
	doAddPermissibleValue:function(){
		var gridPnl = this.getPermValGrid();
		var store = gridPnl.getStore();
		var newRec = new this.permValRecord({
			Name:''
		});
		store.add([newRec]);
	},
	doDeletePermissibleValue:function(){
		var selectedRow = this.getSelectedTableRowObj();
		if (Ext.isEmpty(selectedRow)) {
			Ext.Msg.alert(this['b_Please_select_row_to_delete']);
			return;
		}
		var fnc = function(btn) {
			if (btn == 'yes') {
				var gridPnl = this.getPermValGrid();
				var store = gridPnl.getStore();
				store.remove(selectedRow);
			}
		}
		Ext.Msg.confirm(this['b_Confirm'],
				this['b_confirm_delete'], fnc, this);
	},
	getSelectedTableRowObj: function() {
		var gridPnl = this.getPermValGrid();
		var rec = gridPnl.getSelectionModel().getSelected();
		if (Ext.isEmpty(rec)) {
			return null;
		}
		return rec;
	},
	doBackToSearch:function(){
		if (this.isDirty()){
			Ext.Msg.confirm(this['b_Confirm'],
				this['b_navigate_back_dirty'], this.shouldNavigateToSrch, this);
		} else {
			this.goBackToSrch();
		}
	},
	doCreateProperty:function(){
		if (this.dm.hasError(this)) {
			//var scp = this;
			Ext.Msg.alert(this["b_Error"], this.errCreateScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this));
			},this);
			return;
		}
		var tm = this.getTargetModel('createProperty', {
			dirtyOnly : true
		});
		var tm1 = tm[0];
		tm1.Property.PropertyOverride='BASE';
		tm1.Property.Action='Create';
		tm1.Property.PropertyMetadata.PropertyType = 'CUSTOM';
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property":tm1.Property},
				extraParams : "",
				success : this.resHandler,
				scope : this
			});
	},
	resHandler:function(result,options){
		var res = Ext.decode(result.responseText);
		var propres = res.managepropres;
		/*if (Ext.isEmpty(this.managePropertyScreen)){
			this.managePropertyScreen = new sc.sma.propertyMgtManageProperty({"searchList": this.searchList,"catres": this.catres});
		}*/
		this.managePropertyScreen = this.searchList.getManagePropertyScreen(this.searchList,this.catres);
		this.hide();
		if (this.managePropertyScreen.rendered === false){
			this.managePropertyScreen.render("mainBodyPanel");
		} else {
			this.managePropertyScreen.show();
		}
		this.managePropertyScreen.initializeAfterCreateProperty(res);
	},
	populateComboBoxes:function(catres){
		this.populateYesNoComboBoxes();
		this.populateCategoryComboBox(catres);
	},
	populateYesNoComboBoxes:function(qryres){
		qryres = {"ModifiableList": {"Modifiable":[{"Modifiable":"Y","ModifiableDesc":"Y"},{"Modifiable":"N","ModifiableDesc":"N"}]}};
		this.setModel(qryres,"getModifiableListOutput",{
			clearOldVals: true
        });
	},
	populateCategoryComboBox:function(catres){
		var categories = catres.PropertyMetadataList.PropertyMetadata;
		if (!Ext.isEmpty(categories)){
			for (var i = 0;i < categories.length ; i++ ){
				categories[i].CategoryNameDesc = categories[i].Category;
			}
		}
		this.setModel(catres,"getCategoriesListOutput",{
			clearOldVals: true
        });
	}
});
Ext.reg('xtype_name', sc.sma.propertyMgtCreateProperty);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.propertyMgtManagePropertyUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen6",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel13",
            title: this.b_ManageProperties,
            layout: "table",
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill12"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton44",
                text: this.b_AuditHistory,
                handler: this.showAuditHistoryForBase,
                scope: this,
                iconCls: "sma-audit-icon"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator24"
            },
            {
                xtype: "tbbutton",
                sciId: "modifyValueBaseButton",
                text: this.b_ModifyValue,
                handler: this.doModifyValueForBase,
                scope: this,
                iconCls: "sma-editable-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator14"
            },
            {
                xtype: "tbbutton",
                sciId: "deletePropertyBaseButton",
                text: this.b_DeleteProperty,
                handler: this.doDeleteProperty,
                scope: this,
                iconCls: "sma-delete-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator13"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton46",
                text: this.b_BacktoSearch,
                handler: this.doBackToSearch,
                scope: this,
                iconCls: "sma-back-icon"
            }],
            items: [{
                xtype: "label",
                sciId: "label47",
                text: this.b_PropertyName
            },
            {
                xtype: "textfield",
                sciId: "mngPropBasePropertyNametextfield",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.BasePropertyName",
                    targetBinding: ["manageProperty:Property.BasePropertyName"]
                }
            },
            {
                xtype: "label",
                sciId: "label51",
                text: this.b_Category
            },
            {
                xtype: "textfield",
                sciId: "textfield16",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.Category",
                    targetBinding: ["manageProperty:Property.Category"]
                }
            },
            {
                xtype: "label",
                sciId: "label52",
                text: this.b_PropertyType
            },
            {
                xtype: "textfield",
                sciId: "textfield21",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.PropertyType"
                }
            },
            {
                xtype: "label",
                sciId: "label55",
                text: this.b_DataType
            },
            {
                xtype: "textfield",
                sciId: "textfield18",
                scMngPropGrp: "true",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.DataType",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.DataType"]
                }
            },
            {
                xtype: "label",
                sciId: "label53",
                text: this.b_Modifiable
            },
            {
                xtype: "textfield",
                sciId: "textfield12",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.Modifiable",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.Modifiable"]
                }
            },
            {
                xtype: "label",
                sciId: "label54",
                text: this.b_ModifiableAtRuntime
            },
            {
                xtype: "textfield",
                sciId: "textfield13",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.ModifiableAtRuntime",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.ModifiableAtRuntime"]
                }
            },
            {
                xtype: "label",
                sciId: "label56",
                text: this.b_ServerOverridable
            },
            {
                xtype: "textfield",
                sciId: "textfield14",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.ServerOverride",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.ServerOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label57",
                text: this.b_UserOverridable
            },
            {
                xtype: "textfield",
                sciId: "textfield15",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.UserOverride",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.UserOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label38",
                text: this.b_Description
            },
            {
                xtype: "textarea",
                sciId: "textarea6",
                colspan: 3,
                width: 350,
                height: 100,
                scMngPropGrp: "true",
                allowBlank: false,
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.Description",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.Description"]
                },
				scuiDataType:"Description-1500"
            },
            {
                xtype: "label",
                sciId: "label40",
                text: this.b_UserComments
            },
            {
                xtype: "textarea",
                sciId: "textarea9",
                colspan: 3,
                width: 350,
                height: 100,
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.UserComment",
                    targetBinding: ["manageProperty:Property.UserComment"]
                }
            },
            {
                xtype: "label",
                sciId: "label50",
                text: this.b_FactoryValue
            },
            {
                xtype: "textfield",
                sciId: "textfield16",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.FactoryValue",
                    targetBinding: ["manageProperty:Property.FactoryValue"]
                }
            },
            {
                xtype: "label",
                sciId: "label171",
                text: this.b_PropertyValue
            },
            {
                xtype: "textfield",
                sciId: "textfield41",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.ActualPropertyValue",
                    targetBinding: ["manageProperty:Property.PropertyValue"]
                }
            },
            {
                xtype: "label",
                sciId: "label41",
                text: this.b_PermissibleValues
            },
            {
                xtype: "editorgrid",
                sciId: "grid7",
                title: this.b_PermissibleValues,
                columns: [{
                    defid: "grid-column",
                    sciId: "grid-column52",
                    header: this.b_Values,
                    sortable: true,
                    width: 145,
                    dataIndex: "Name",
                    bindingData: {
                        defid: "object",
                        tAttrBinding: "Name",
                        sFieldConfig: {
                            defid: "object",
                            mapping: "Name"
                        }
                    }
                }],
                width: 150,
                header: false,
                height: 180,
                colspan: 3,
                scMngPropGrp: "true",
                sm: this.sm,
                clicksToEdit: 1,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.PropertyMetadata.Values.Value",
                    targetBinding: ["manageProperty:Property.PropertyMetadata.Values.Value", "managePropertyPermissibleValues:Property.PropertyMetadata.Values.Value"]
                }
            },
            {
                xtype: "label",
                sciId: "label48",
                text: this.b_Author
            },
            {
                xtype: "textfield",
                sciId: "authortextfield16",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.Createuserid",
                    targetBinding: ["manageProperty:Property.Createuserid"]
                }
            },
            {
                xtype: "label",
                sciId: "label63",
                text: this.b_Editor
            },
            {
                xtype: "textfield",
                sciId: "textfield17",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.Modifyuserid",
                    targetBinding: ["manageProperty:Property.Modifyuserid"]
                }
            },
            {
                xtype: "label",
                sciId: "label64",
                text: this.b_Createdon
            },
            {
                xtype: "textfield",
                sciId: "createdOnTxtFld",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.ChangedCreated",
                    targetBinding: ["manageProperty:Property.ChangedCreated"]
                },
				scSkipDataValidation: true
            },
            {
                xtype: "label",
                sciId: "label65",
                text: this.b_Modifiedon
            },
            {
                xtype: "textfield",
                sciId: "textfield19",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "manageProperty:Property.Modifyts",
                    targetBinding: ["manageProperty:Property.Modifyts"],
                    scuiDataType: "TimeStamp"
                },
				scSkipDataValidation: true
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 8
            }
        },
        {
            xtype: "editorgrid",
            sciId: "editorgrid",
            title: this.b_OverriddenProperties,
            columns: [{
                defid: "grid-column",
                sciId: "grid-column35",
                header: this.b_OverrideType,
                sortable: true,
                dataIndex: "PropertyOverride",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverride"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column31",
                header: this.b_OverriddenFor,
                sortable: true,
                width: 150,
                dataIndex: "PropertyOverrideName",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "PropertyOverrideName"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column34",
                header: this.b_PropertyValue,
                sortable: true,
                dataIndex: "ActualPropertyValue",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ActualPropertyValue"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column18",
                header: this.b_UserComments,
                sortable: true,
                width: 250,
                dataIndex: "UserComment",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "UserComment"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column19",
                header: this.b_Author,
                sortable: true,
                hidden: true,
                dataIndex: "Createuserid",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Createuserid"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column20",
                header: this.b_Editor,
                sortable: true,
                hidden: true,
                dataIndex: "Modifyuserid",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyuserid"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column21",
                header: this.b_Createdon,
                sortable: true,
                hidden: true,
                dataIndex: "ChangedCreated",
                renderer: this.managePropUserDateFormat,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ChangedCreated"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "grid-column22",
                header: this.b_Modifiedon,
                sortable: true,
                hidden: true,
                dataIndex: "Modifyts",
                renderer: this.managePropUserDateFormat,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Modifyts"
                    }
                }
            }],
            clicksToEdit: 1,
            height: 300,
            tbar: [{
                xtype: "tbfill",
                sciId: "tbfill8"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton40",
                text: this.b_AuditHistory,
                handler: this.showAuditHistoryForOverride,
                scope: this,
                iconCls: "sma-audit-icon"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator18"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton61",
                text: this.b_ModifyValue,
                handler: this.doModifyValueForOverride,
                scope: this,
                iconCls: "sma-editable-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator31"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton36",
                text: this.b_AddNewOverride,
                handler: this.doAddNewOverride,
                scope: this,
                iconCls: "sma-create-button"
            },
            {
                xtype: "tbseparator",
                sciId: "tbseparator17"
            },
            {
                xtype: "tbbutton",
                sciId: "tbbutton38",
                text: this.b_DeleteOverride,
                handler: this.doDeleteOverride,
                scope: this,
                iconCls: "sma-delete-button"
            }],
            sm: this.editorgridsm,
            bindingData: {
                defid: "object",
                sourceBinding: "manageOverriddenProperties:PropertyList.Property"
            },
            store: new Ext.data.JsonStore({
                defid: "jsonstore",
                fields: [{
                    defid: "object",
                    name: "PropertyOverride"
                },
                {
                    defid: "object",
                    name: "PropertyOverrideName"
                },
                {
                    defid: "object",
                    name: "ActualPropertyValue"
                },
                {
                    defid: "object",
                    name: "UserComment"
                },
                {
                    defid: "object",
                    name: "Createuserid"
                },
                {
                    defid: "object",
                    name: "Modifyuserid"
                },
                {
                    defid: "object",
                    name: "ChangedCreated",
                    type: "date",
                    dateFormat: this.managePropertiesDateFormat
                },
                {
                    defid: "object",
                    dateFormat: this.managePropertiesDateFormat,
                    type: "date",
                    name: "Modifyts"
                }]
            })
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

sc.sma.propertyMgtManageProperty = function(config) {
    sc.sma.propertyMgtManageProperty.superclass.constructor.call(this, config);
	this.searchList = config.searchList;
}
Ext.extend(sc.sma.propertyMgtManageProperty, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.propertyMgtManageProperty',
    getUIConfig: sc.sma.propertyMgtManagePropertyUIConfig,
    namespaces: {
        target: ['manageProperty', 'manageOverriddenProperties', 'managePropertyPermissibleValues'],
        source: ['manageProperty', 'manageOverriddenProperties', 'getModifiableListOutput']
    },
	managePropUserDateFormat: (function(){
			return Ext.util.Format.dateRenderer(sc.plat.Userprefs.getDateHourMinuteFormat());
		})(),
	managePropertiesDateFormat: (function(){
			return 'Y-m-d\\TH:i:sP';
		})(),
	shouldNavigateToSrch:function(btn) {
		if (btn == 'yes') {
			this.goBackToSrch();
		}
	},
	sm : new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),
	setFocus: function(widget) {
        if(widget){
        	widget.focus.defer(100, widget);
        }
    },
	goBackToSrch:function(){
		this.hide();
		this.searchList.show();
	},
	dm : sc.plat.DataManager,
	permValRecord: Ext.data.Record.create([{
		name : 'Name'
	}]),
	overridePropRecord: Ext.data.Record.create([{
		name : 'PropertyOverride'
	},{
		name : 'PropertyOverrideName'
	},{
		name : 'ActualPropertyValue'
	},{
		name : 'UserComment'
	},{
		name : 'Createuserid'
	},{
		name : 'Modifyuserid'
	},{
		name : 'ChangedCreated',
		type: "date",
        dateFormat: this.managePropertiesDateFormat
	},{
		dateFormat: this.managePropertiesDateFormat,
		type: "date",
		name: "Modifyts"
	}]),
	doBackToSearch:function(){
		if (this.isDirty()){
			Ext.Msg.confirm(this['b_Confirm'],
				this["b_navigate_back_dirty"], this.shouldNavigateToSrch, this);
		} else {
			this.goBackToSrch();
		}
	},
	MngPropModifyValueInstance:null,
	ModifyValueWin:null,
	scLastModifiedOverrideType:'BASE',
	initializeMngPropModifyValueWindow:function(){
		this.MngPropModifyValueInstance = new sc.sma.MngPropModifyValue();
            this.ModifyValueWin = new Ext.Window({
                layout: 'fit',
                width: 500,
                height: 375,
                closeAction: 'hide',
                plain: true,
				modal: true,
                title: this["b_Modify_Value_For"],
                items: [this.MngPropModifyValueInstance],
				buttonAlign:'center',
				buttons : [{
					xtype: "button",
					text: this["b_ModifyValueOkayButton"],
					handler: this.doSaveModifyValue,
					scope: this
				},
				{
					xtype: "button",
					sciId: "button1",
					text: this["b_ModifyValueCancelButton"],
					handler: this.doCancel,
					scope: this
				}]
            });
	},
	doModifyValueForBase:function(){
		if (!this.ModifyValueWin) {
			this.initializeMngPropModifyValueWindow();
        }
		this.MngPropModifyValueInstance.performInitialization({"mngPropData":this.mngPropData,"scIsBase":true});
        this.ModifyValueWin.show();
		this.MngPropModifyValueInstance.laterInitialization();
		this.scLastModifiedOverrideType = 'BASE';
	},
	doModifyValueForOverride:function(){
		var selectedRow = this.getSelectedTableRowObjForOverride();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert(this['b_Alert'],this.b_Please_select_a_row);
            return;
        }
		if (!this.ModifyValueWin) {
			this.initializeMngPropModifyValueWindow();
        }
		this.MngPropModifyValueInstance.performInitialization({"mngPropData":this.overrideSelectedRec,"scIsBase":false});
        this.ModifyValueWin.show();
		this.MngPropModifyValueInstance.laterInitialization();
		this.scLastModifiedOverrideType = 'USERORSERVER';
	},
	getSelectedTableRowObjForOverride:function(){
		var gridPnl = this.getOverridePropGrid();
		var rec = gridPnl.getSelectionModel().getSelected();
		if (Ext.isEmpty(rec)) {
			return null;
		}
		return rec;
	},
	getOverridePropGrid:function(){
		return this.find('sciId','editorgrid')[0];
	},
	editorgridsm:new Ext.grid.RowSelectionModel({
		singleSelect : true
	}),
	doSaveModifyValue:function(){
		if (this.dm.hasError(this.MngPropModifyValueInstance)) {
			Ext.Msg.alert(this["b_Error"], this.errModifyValueScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this.MngPropModifyValueInstance));
			},this);
			return;
		}
		this.ModifyValueWin.hide();
		var resToSave = this.MngPropModifyValueInstance.getTargetModel('modifyValue')[0];
		if (this.scLastModifiedOverrideType === 'BASE'){
			sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "manageProperty",
					inputNS : "Property",
					inputObj : {"Property":resToSave.Property},
					extraParams : "",
					success : this.saveChangesResHandler,
					scope : this
				});

		} else {
			sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "manageProperty",
					inputNS : "Property",
					inputObj : {"Property":resToSave.Property},
					extraParams : "",
					success : this.saveOverrideChangesResHandler,
					scope : this
				});
		}
	},
	doCancel:function(){
		this.ModifyValueWin.hide();
	},
	saveChangesResHandler:function(result,options){
		var res = result.json;
		res.managepropres.Property.ChangedCreated = Date.parseDate(res.managepropres.Property.Createts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		res.managepropres.Property.Modifyts = Date.parseDate(res.managepropres.Property.Modifyts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		this.setModel(res.managepropres,
        "manageProperty", {
            clearOldVals: true
        });
		this.mngPropData = res.managepropres;
		this.basePropName = this.mngPropData.Property.BasePropertyName;
		this.categoryName = this.mngPropData.Property.Category;
		this.mngPropData.Property.ChangedCreated = res.managepropres.Property.Createts;
		if (this.mngPropData.Property.PropertyMetadata.ModifiableAtRuntime === 'N'){
			Ext.MessageBox.show({
				title : this['b_Value_Modified'],
				msg : this["b_warning_modifiable_at_runtime"],
				width : 450,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
		} else {
		Ext.MessageBox.show({
				title : this['b_Save_Successful'],
				msg : this['b_The_changes_were_saved_successfully'],
				width : 300,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
		}
	},
	initializeAfterCreateProperty:function(res){
		res.managepropres.Property.ChangedCreated = Date.parseDate(res.managepropres.Property.Createts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		res.managepropres.Property.Modifyts = Date.parseDate(res.managepropres.Property.Modifyts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		this.setModel(res.managepropres,
        "manageProperty", {
            clearOldVals: true
        });
		this.setModel({},
        "manageOverriddenProperties", {
            clearOldVals: true
        });
		this.mngPropData = res.managepropres;
		this.basePropName = this.mngPropData.Property.BasePropertyName;
		this.categoryName = this.mngPropData.Property.Category;
		this.performSetupBasedOnMetadataInfo();
	},
	getTopPanelTopToolbarButton:function(val){
		var tpTlBr = this.getTopPanelTopToolbar();
		var mxdCol = tpTlBr.items;
		var butIndex = mxdCol.findIndex('sciId',val);
		return mxdCol.itemAt(butIndex);
	},
	getTopPanelTopToolbar:function(){
		return this.find('sciId','panel13').pop().getTopToolbar();
	},
	topPanelTopToolbarDeleteButton:null,
	getTopPanelTopToolbarDeleteButton:function(){
		if (Ext.isEmpty(this.topPanelTopToolbarDeleteButton)){
			this.topPanelTopToolbarDeleteButton = this.getTopPanelTopToolbarButton('deletePropertyBaseButton');
		}
		return this.topPanelTopToolbarDeleteButton;
	},
	topPanelTopToolbarModifyValButton:null,
	getTopPanelTopToolbarModifyValButton:function(){
		if (Ext.isEmpty(this.topPanelTopToolbarModifyValButton)){
			this.topPanelTopToolbarModifyValButton = this.getTopPanelTopToolbarButton('modifyValueBaseButton');
		}
		return this.topPanelTopToolbarModifyValButton;
	},
	performSetupBasedOnMetadataInfo:function(){
		var propMetadata = this.mngPropData.Property.PropertyMetadata;
		this.getTopPanelTopToolbarModifyValButton().enable();
		this.getTopPanelTopToolbarDeleteButton().enable();
		this.getOverridePropGrid().show();
		if (propMetadata.Modifiable === "N" ){
			this.getTopPanelTopToolbarModifyValButton().disable();
			this.getOverridePropGrid().hide();
		}
		if (propMetadata.PropertyType === "SYSTEM" ){
			this.getTopPanelTopToolbarDeleteButton().disable();
		}
		if (propMetadata.ServerOverride === "N" && propMetadata.UserOverride === "N"){
			this.getOverridePropGrid().hide();
		}
	},
	saveOverrideChangesResHandler:function(result,options){
		var res = result.json;
		var gridPnl = this.getOverridePropGrid();
		var store = gridPnl.getStore();
        var recIndex = store.findBy(function(record,id){
			var retObj = res.managepropres.Property;
			if (retObj.PropertyOverrideName === record.get('PropertyOverrideName')){
				if (retObj.PropertyOverride === record.get('PropertyOverride')){
					return true;
				}
			}
		},this);

        if (recIndex != -1) {
			var rec = store.getAt(recIndex);
            if (result.statusText == 'OK') {
				var tempObj = res.managepropres.Property;
				rec.set('ActualPropertyValue',tempObj.ActualPropertyValue);
				rec.set('UserComment',tempObj.UserComment);
				rec.set('Createuserid',tempObj.Createuserid);
				rec.set('Modifyuserid',tempObj.Modifyuserid);
				rec.set('Modifyts',Date.parseDate(tempObj.Modifyts,'Y-m-d\\TH:i:sP'));
				rec.set('ChangedCreated',Date.parseDate(tempObj.Createts,'Y-m-d\\TH:i:sP'));
				rec.json = tempObj;
				rec.commit();
				tempObj.ChangedCreated = tempObj.Createts;
				if (tempObj.PropertyMetadata.ModifiableAtRuntime === 'N'){
				Ext.MessageBox.show({
						title : this['b_Value_Modified'],
						msg : this["b_warning_modifiable_at_runtime"],
						width : 450,
						buttons : Ext.Msg.OK,
						icon : Ext.MessageBox.INFO
					});
				} else {
					Ext.MessageBox.show({
							title : this['b_Save_Successful'],
							msg : this['b_The_changes_were_saved_successfully'],
					width : 300,
					buttons : Ext.Msg.OK,
					icon : Ext.MessageBox.INFO
				});
				}
				var selRec = this.overrideSelectedRec.Property;
				if (selRec.PropertyOverride === tempObj.PropertyOverride && selRec.PropertyOverrideName === tempObj.PropertyOverrideName){
					this.overrideSelectedRec.Property = tempObj;
				}
            } 
        }
	},
	MngPropAddNewOverrideInstance:null,
	AddNewOverrideWin:null,
	initializeAddOverrideWindow:function(){
		this.MngPropAddNewOverrideInstance = new sc.sma.MngPropAddNewOverride();
            this.AddNewOverrideWin = new Ext.Window({
                layout: 'fit',
                width: 590,
                height: 375,
                closeAction: 'hide',
                plain: true,
				modal: true,
                title: 'Add New Override For',
                items: [this.MngPropAddNewOverrideInstance],
				buttonAlign:'center',
				buttons : [{
					xtype: "button",
					text: this["b_ModifyValueOkayButton"],
					handler: this.doCreateOverride,
					scope: this
				},
				{
					xtype: "button",
					sciId: "button1",
					text: this["b_ModifyValueCancelButton"],
					handler: this.doCancelOverride,
					scope: this
				}]
            });
	},
	doAddNewOverride:function(){
		if (!this.AddNewOverrideWin) {
			this.initializeAddOverrideWindow();
        }
		this.MngPropAddNewOverrideInstance.performInitialization({"mngPropData":this.mngPropData});
        this.AddNewOverrideWin.show();
		this.MngPropAddNewOverrideInstance.laterSetup();
	},
	doDeleteProperty:function(){
		var fcn = function(btn){
			if (btn == 'yes') {
				var propRow = {
					"BasePropertyName": this.basePropName,
					"Category":this.categoryName
				};
				propRow.Action = 'Delete';
				propRow.PropertyOverride='BASE';
				propRow.DeleteOverrides='Y';
				sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property": propRow},
				extraParams : "",
				success : this.delHandler,
				scope : this
				});
			}
		}
		//TO-DO Warnig Message should be localized
		var deleteMessage = this["b_warning_delete_property_with_override"];
        Ext.Msg.confirm(this['b_Confirm'],deleteMessage, fcn, this); 
	},
	delHandler:function(result,options){
            if (result.statusText == 'OK') {
				this.deleteAndGoBackToSearch(result,options);
				
            } else {
				Ext.MessageBox.show({
						title : this['b_Deletion_Failed'],
						msg : this['b_Deletion_Failed_Msg'],
						width : 450,
						buttons : Ext.Msg.OK,
						icon : Ext.MessageBox.INFO
					});
            }
	},
	deleteAndGoBackToSearch:function(result,options){
		this.goBackToSrch();
		this.searchList.delHandler(result,options);
	},
	doCancelOverride:function(){
		this.AddNewOverrideWin.hide();
	},
	doDeleteOverride:function(){
		var selectedRow = this.getSelectedTableRowObjForOverride();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert(this['b_Alert'],this.b_Please_select_a_row);
            return;
        }
		var fnc = function(btn){
			if (btn == 'yes') {
			var recToDel = this.overrideSelectedRec.Property;
			var inpToDel = {"Property":{"BasePropertyName":recToDel.BasePropertyName,"PropertyOverride":recToDel.PropertyOverride,"PropertyOverrideName":recToDel.PropertyOverrideName,
				"Category":recToDel.Category}};
			inpToDel.Property.Action="Delete";
			sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "manageProperty",
				inputNS : "Property",
				inputObj : {"Property":inpToDel.Property},
				extraParams : "",
				success : this.delOverrideResHandler,
				scope : this
			});
			}
		}
		Ext.Msg.confirm(this['b_Confirm'],
				this['b_Deletion_Property_override_prompt'], fnc, this);
	},
	delOverrideResHandler:function(result,options){
		var res = result.json;
		if (result.statusText == 'OK') {
			var gridPnl = this.getOverridePropGrid();
			var store = gridPnl.getStore();
			var recIndex = store.findBy(function(record,id){
				var retObj = res.managepropres.Property;
				if (retObj.PropertyOverrideName === record.get('PropertyOverrideName')){
					if (retObj.PropertyOverride === record.get('PropertyOverride')){
						return true;
					}
				}
			},this);
			if (recIndex != -1) {
				var rec = store.getAt(recIndex);
				store.remove(rec);
				Ext.MessageBox.show({
					title : this['b_Deletion_Successful'],
					msg : this['b_The_override_was_deleted_successfully'],
					width : 400,
					buttons : Ext.Msg.OK,
					icon : Ext.MessageBox.INFO
				});
			}
		} else {
			Ext.MessageBox.show({
				title : this['b_Deletion_Failed'],
				msg : this['b_The_override_was_not_deleted'],
				width : 400,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
		}
	},
	doCreateOverride:function(){
		if (this.dm.hasError(this.MngPropAddNewOverrideInstance)) {
			Ext.Msg.alert("Error", this.errAddNewOverrideScreen, function(){
				this.setFocus(this.dm.findFirstComponentInError(this.MngPropAddNewOverrideInstance));
			},this);
			return;
		}
		this.AddNewOverrideWin.hide();
		var resToSave = this.MngPropAddNewOverrideInstance.getTargetModel('addOverride')[0];
		var inpObj = {"Property":resToSave.Property};
		inpObj.Property.Action="Create";
			sc.sma.AjaxUtils.request({
					actionNS : "sma",
					action : "manageProperty",
					inputNS : "Property",
					inputObj : inpObj,
					extraParams : "",
					success : this.createOverrideResHandler,
					scope : this
				});
	},
	createOverrideResHandler:function(result,options){
		var res = result.json;
		var newPropOvr = res.managepropres.Property;
		newPropOvr.ChangedCreated = newPropOvr.Createts;
		newPropOvr.ChangedCreated = Date.parseDate(newPropOvr.Createts,'Y-m-d\\TH:i:sP');
		newPropOvr.Modifyts = Date.parseDate(newPropOvr.Modifyts,'Y-m-d\\TH:i:sP');
		var newRec = new this.overridePropRecord(newPropOvr);
		newRec.json = newPropOvr;
		this.getOverridePropGrid().getStore().add([newRec]);
		Ext.MessageBox.show({
				title : this['b_Creation_Successful'],
				msg : this['b_Override_Creation_successful'],
				width : 400,
				buttons : Ext.Msg.OK,
				icon : Ext.MessageBox.INFO
			});
	},
	getPermValGrid:function(){
		return this.find('sciId','grid7').pop();
	},
	auditBasePropertyScreen: null,
	showAuditHistoryForBase:function(){
		var inpToAud = {"Audit":{"TableName":"PLT_PROPERTY","Reference1":this.basePropName,"Reference2":this.categoryName}};
		if (Ext.isEmpty(inpToAud.Audit.Reference2)){
			inpToAud.Audit.Reference2QryType = "VOID";
		}
		inpToAud.Audit.OrderBy = {};
		inpToAud.Audit.OrderBy.Attribute = "Modifyts";
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getAuditList",
				inputNS : "Audit",
				inputObj : inpToAud,
				extraParams : "",
				success : this.auditResHandler,
				scope : this
			});
	},
	auditResHandler:function(result,options){
		if (Ext.isEmpty(this.auditBasePropertyScreen)){
			this.auditBasePropertyScreen = new sc.sma.propertyMgtAuditHistoryBase();
		}
		this.hide();
		if (this.auditBasePropertyScreen.rendered === false){
			this.auditBasePropertyScreen.render("mainBodyPanel");
		} else {
			this.auditBasePropertyScreen.show();
		}
		this.auditBasePropertyScreen.performInitialization({"mngPropScreen": this,"basePropName":this.basePropName,"categoryName":this.categoryName,"auditResult":result,"mngPropData":this.mngPropData});
	},
	auditOverridesPropertyScreen: null,
	showAuditHistoryForOverride:function(){
		var selectedRow = this.getSelectedTableRowObjForOverride();
        if (Ext.isEmpty(selectedRow)) {
            Ext.Msg.alert('Alert',this.b_Please_select_a_row);
            return;
        }
		var inpToAud = {"Audit":{"TableName":"PLT_PROPERTY","Reference1":this.basePropName,"Reference2":this.categoryName,
			"Reference3":this.overrideSelectedRec.Property.PropertyOverride,"Reference4":this.overrideSelectedRec.Property.PropertyOverrideName}};
		if (Ext.isEmpty(inpToAud.Audit.Reference2)){
			inpToAud.Audit.Reference2QryType = "VOID";
		}
		inpToAud.Audit.OrderBy = {};
		inpToAud.Audit.OrderBy.Attribute = "Modifyts";
		sc.sma.AjaxUtils.request({
				actionNS : "sma",
				action : "getAuditList",
				inputNS : "Audit",
				inputObj : inpToAud,
				extraParams : "",
				success : this.auditOverridesResHandler,
				scope : this
			});
	},
	auditOverridesResHandler:function(result,options){
		if (Ext.isEmpty(this.auditOverridesPropertyScreen)){
			this.auditOverridesPropertyScreen = new sc.sma.propertyMgtAuditHistory();
		}
		this.hide();
		if (this.auditOverridesPropertyScreen.rendered === false){
			this.auditOverridesPropertyScreen.render("mainBodyPanel");
		} else {
			this.auditOverridesPropertyScreen.show();
		}
		this.auditOverridesPropertyScreen.performInitialization({"mngPropScreen": this,"basePropName":this.basePropName,"categoryName":this.categoryName,"auditResult":result,"propOverride":this.overrideSelectedRec.Property.PropertyOverride,"propOverrideName":this.overrideSelectedRec.Property.PropertyOverrideName,"mngPropData":this.mngPropData,"overridePropRec":this.overrideSelectedRec});
	},
	refreshManagePropertyScreen:function(){
		var propRow = {
			"BasePropertyName": this.basePropName,
			"Category":this.categoryName
		};
		sc.sma.AjaxUtils.request({
			actionNS : "sma",
			action : "getManagePropertyList",
			inputNS : "PropertyMetadata",
			inputObj : {"PropertyMetadata":propRow},
			extraParams : "",
			success : this.refreshManagePropertyScreenResHandler,
			scope : this
		});
	},
	refreshManagePropertyScreenResHandler:function(result){
		var res = Ext.decode(result.responseText);
		this.generalInitialization({"mngPropData":res});
	},
	generalInitialization:function(result){
		this.mngPropData = result.mngPropData;
		var res = this.mngPropData;
		res.PropertyMetadata = res.PropertyMetadataList.PropertyMetadata[0];
		var a = {};
		a.Property = res.PropertyMetadata.BaseProperty.Property;
		delete res.PropertyMetadata.BaseProperty;
		this.basePropName = a.Property.BasePropertyName;
		this.categoryName = a.Property.Category;
		a.Property.ChangedCreated = Date.parseDate(a.Property.Createts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());
		a.Property.Modifyts = Date.parseDate(a.Property.Modifyts,'Y-m-d\\TH:i:sP').format(sc.plat.Userprefs.getDateHourMinuteFormat());

		var finalRes = {
			"Property":a.Property
		};
		finalRes.Property.PropertyMetadata = res.PropertyMetadata;
		this.setModel(finalRes,
        "manageProperty", {
            clearOldVals: true
        });
		var x = {"PropertyList":finalRes.Property.PropertyMetadata.PropertyList};
		this.setModel(x,
        "manageOverriddenProperties", {
            clearOldVals: true
        });
		this.mngPropData = finalRes;
		this.performSetupBasedOnMetadataInfo();
	},
	performInitialization:function(result){
		this.generalInitialization(result);
		var grPan = this.getOverridePropGrid();
		grPan.on('rowclick',function(grid,rowIndex,e){
			var record = grid.getStore().getAt(rowIndex);
			this.overrideSelectedRec = {"Property":record.json};
			this.overrideSelectedRec.Property.PropertyMetadata = {};
			this.overrideSelectedRec.Property.PropertyMetadata.Values = this.mngPropData.Property.PropertyMetadata.Values;
		},this);
	},
	overrideSelectedRec: null
});
Ext.reg('xtype_name', sc.sma.propertyMgtManageProperty);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.MngPropModifyValueUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel7",
            title: this.b_Panel7,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label50",
                text: this.b_PropertyName
            },
            {
                xtype: "textfield",
                sciId: "textfield17",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Property.BasePropertyName",
                    targetBinding: ["modifyValue:Property.BasePropertyName"]
                }
            },
            {
                xtype: "label",
                sciId: "label51",
                text: this.b_Category
            },
            {
                xtype: "textfield",
                sciId: "textfield18",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Property.Category",
                    targetBinding: ["modifyValue:Property.Category"]
                }
            },
            {
                xtype: "label",
                sciId: "label52",
                text: this.b_OverriddenFor,
                scOverriddenProperty: "true"
            },
            {
                xtype: "textfield",
                sciId: "textfield23",
                scOverriddenProperty: "true",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Property.PropertyOverrideName",
                    targetBinding: ["modifyValue:Property.PropertyOverrideName"]
                }
            },
            {
                xtype: "label",
                sciId: "label53",
                text: this.b_OverrideType,
                scOverriddenProperty: "true"
            },
            {
                xtype: "textfield",
                sciId: "textfield24",
                scOverriddenProperty: "true",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Property.PropertyOverride",
                    targetBinding: ["modifyValue:Property.PropertyOverride"]
                }
            }],
            header: false,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
            }
        },
        {
            xtype: "panel",
            sciId: "panel9",
            title: this.b_Panel9,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label58",
                text: this.b_OldValue
            },
            {
                xtype: "textfield",
                sciId: "textfield25",
                width: 165,
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Property.ActualPropertyValue"
                }
            },
            {
                xtype: "label",
                sciId: "label59",
                text: this.b_NewValue,
                scPermVal: "false",
                scModifyValScMandatory: "true"
            },
            {
                xtype: "textfield",
                sciId: "textfield26",
                width: 165,
                scPermVal: "false",
                scModifyValScAllowBlank: "true",
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Nothing.Nothing",
                    targetBinding: ["modifyValue:Property.PropertyValue"]
                }
            },
            {
                xtype: "label",
                sciId: "label60",
                text: this.b_NewValue,
                scPermVal: "true",
                scModifyValScMandatory: "true"
            },
            {
                xtype: "combo",
                sciId: "combo24",
                scPermVal: "true",
                forceSelection: true,
                displayField: "Name",
                valueField: "Name",
                triggerAction: "all",
                mode: "local",
                scModifyValScAllowBlank: "true",
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Nothing.Nothing",
                    optionsBinding: "modifyValueOptions:Values.Value",
                    targetBinding: ["modifyValue:Property.PropertyValue"]
                },
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Name", "Name"]
                })
            },
            {
                xtype: "label",
                sciId: "label61",
                text: this.b_UserComments
            },
            {
                xtype: "textarea",
                sciId: "textarea7",
                width: 250,
                height: 100,
                bindingData: {
                    defid: "object",
                    sourceBinding: "modifyValue:Nothing.NothingComment",
                    targetBinding: ["modifyValue:Property.UserComment"]
                }
            }],
            header: false,
            border: false,
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

sc.sma.MngPropModifyValue = function(config) {
    sc.sma.MngPropModifyValue.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.MngPropModifyValue, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.MngPropModifyValue',
    getUIConfig: sc.sma.MngPropModifyValueUIConfig,
    namespaces: {
        target: ['modifyValue'],
        source: ['modifyValue', 'modifyValueOptions']
    },
    performInitialization:function(res){
		var scPermValsCntrls = null;
		var scPermValIndCntrl = null;
		this.mngPropData = res.mngPropData;
		this.scIsBase = res.scIsBase;
		if (this.scIsBase === true && this.mngPropData.Property.PropertyMetadata.PropertyType === 'SYSTEM'){ 
			scPermValsCntrls = this.find('scModifyValScMandatory','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.cls = 'x-form-item sc-right-label';
			}
			scPermValsCntrls = this.find('scModifyValScAllowBlank','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.allowBlank = true;
			}
		} else {
			scPermValsCntrls = this.find('scModifyValScMandatory','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.cls = 'x-form-item sc-right-label  sc-mandatory';
			}
			scPermValsCntrls = this.find('scModifyValScAllowBlank','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.allowBlank = false;
			}
		}
		if (this.scIsBase === true){
			var ovrDnPropCnrls = this.find('scOverriddenProperty','true');
			for (var i = 0;i < ovrDnPropCnrls.length ; i++ ){
				var indCntrl = ovrDnPropCnrls[i];
				indCntrl.hide();
			}
		} else {
			var ovrDnPropCnrls = this.find('scOverriddenProperty','true');
			for (var i = 0;i < ovrDnPropCnrls.length ; i++ ){
				var indCntrl = ovrDnPropCnrls[i];
				indCntrl.show();
			}
		}
		var vals = this.mngPropData.Property.PropertyMetadata.Values;
		if (Ext.isEmpty(vals.Value) || vals.Value.length == 0){		// There are no permissibile values so show text box
			scPermValsCntrls = this.find('scPermVal','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.hide();
				scPermValIndCntrl.disable();
			}
			scPermValsCntrls = this.find('scPermVal','false');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.enable();
				scPermValIndCntrl.show();
			}
		} else {
			scPermValsCntrls = this.find('scPermVal','false');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.hide();
				scPermValIndCntrl.disable();
			}
			scPermValsCntrls = this.find('scPermVal','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.enable();
				scPermValIndCntrl.show();
			}
			this.setModel({"Values":vals},"modifyValueOptions",{
				clearOldVals: true
			});
		}
	},
	laterInitialization:function(shouldShowNewValue,newValueToBeDisplayed){
		var newVal = {"Nothing":""};
		if (shouldShowNewValue === true){ // TO DO change the binding to meaningful one
			newVal.Nothing = newValueToBeDisplayed;
		}
		this.setModel({"Property":this.mngPropData.Property,"Nothing":newVal},"modifyValue",{
            clearOldVals: true
        });
	}
});
Ext.reg('xtype_name', sc.sma.MngPropModifyValue);
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

Ext.namespace('sc.sma');

sc.sma.MngPropAddNewOverrideUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel9",
            title: this.b_Panel9,
            layout: "table",
            items: [{
                xtype: "label",
                sciId: "label63",
                text: this.b_PropertyName
            },
            {
                xtype: "textfield",
                sciId: "textfield20",
                width: 165,
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "addOverride:Property.BasePropertyName",
                    targetBinding: ["addOverride:Property.BasePropertyName"]
                }
            },
            {
                xtype: "label",
                sciId: "label64",
                text: this.b_Category
            },
            {
                xtype: "textfield",
                sciId: "textfield21",
                readOnly: true,
                bindingData: {
                    defid: "object",
                    sourceBinding: "addOverride:Property.Category",
                    targetBinding: ["addOverride:Property.Category"]
                }
            },
            {
                xtype: "label",
                sciId: "label65",
                text: this.b_OverrideFor,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "textfield",
                sciId: "textfield22",
                width: 165,
                allowBlank: false,
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:Property.PropertyOverrideName",
                    targetBinding: ["addOverride:Property.PropertyOverrideName"]
                }
            },
            {
                xtype: "label",
                sciId: "label66",
                text: this.b_OverrideType,
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo32",
                displayField: "PropertyOverrideDesc",
                valueField: "PropertyOverride",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                width: 128,
                allowBlank: false,
                validateOnBlur: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["PropertyOverride", "PropertyOverrideDesc"]
                }),
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:Property.PropertyOverride",
                    optionsBinding: "GetOverrideTypeOptions:OverrideTypeList.OverrideType",
                    targetBinding: ["addOverride:Property.PropertyOverride"]
                }
            },
            {
                xtype: "label",
                sciId: "label67",
                text: this.b_PropertyValue,
                scPermVal: "false",
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "textfield",
                sciId: "textfield23",
                width: 165,
                colspan: 3,
                scPermVal: "false",
                allowBlank: false,
                bindingData: {
                    defid: "object",
                    targetBinding: ["addOverride:Property.PropertyValue"],
                    sourceBinding: "resetNS:Property.ActualPropertyValue"
                }
            },
            {
                xtype: "label",
                sciId: "label68",
                text: this.b_PropertyValue,
                scPermVal: "true",
                cls: "x-form-item sc-right-label sc-mandatory"
            },
            {
                xtype: "combo",
                sciId: "combo33",
                colspan: 3,
                scPermVal: "true",
                displayField: "Name",
                valueField: "Name",
                triggerAction: "all",
                mode: "local",
                forceSelection: true,
                allowBlank: false,
                validateOnBlur: true,
                store: new Ext.data.JsonStore({
                    defid: "jsonstore",
                    fields: ["Name", "Name"]
                }),
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:Property.ActualPropertyValue",
                    targetBinding: ["addOverride:Property.PropertyValue"],
                    optionsBinding: "modifyValueOptions:Values.Value"
                }
            },
            {
                xtype: "label",
                sciId: "label69",
                text: this.b_UserComments
            },
            {
                xtype: "textarea",
                sciId: "textarea8",
                width: 300,
                colspan: 3,
                height: 100,
                bindingData: {
                    defid: "object",
                    sourceBinding: "resetNS:Property.UserComment",
                    targetBinding: ["addOverride:Property.UserComment"]
                }
            }],
            header: false,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
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

sc.sma.MngPropAddNewOverride = function(config) {
    sc.sma.MngPropAddNewOverride.superclass.constructor.call(this, config);
}
Ext.extend(sc.sma.MngPropAddNewOverride, sc.plat.ui.ExtensibleScreen, {
    className: 'sc.sma.MngPropAddNewOverride',
    getUIConfig: sc.sma.MngPropAddNewOverrideUIConfig,
    namespaces: {
        target: ['addOverride'],
        source: ['addOverride', 'modifyValueOptions', 'GetOverrideTypeOptions', 'resetNS']
    },
	populateOverrideTypeComboBox:function(qryres){
		var arr = [];
		if (this.isServerOverrideable()){
			arr.push({"PropertyOverride":"SERVER","PropertyOverrideDesc":"SERVER"});
		}
		if (this.isUserOverrideable()){
			arr.push({"PropertyOverride":"USER","PropertyOverrideDesc":"USER"});
		}
		qryres = {"OverrideTypeList": {"OverrideType":arr}};
		this.setModel(qryres,"GetOverrideTypeOptions",{
			clearOldVals: true
        });
	},
	isServerOverrideable:function(){
		return this.mngPropData.Property.PropertyMetadata.ServerOverride === "Y";
	},
	isUserOverrideable:function(){
		return this.mngPropData.Property.PropertyMetadata.UserOverride === "Y";
	},
	performInitialization:function(res){
		var scPermValsCntrls = null;
		var scPermValIndCntrl = null;
		this.mngPropData = res.mngPropData;
		this.populateOverrideTypeComboBox();
		var vals = this.mngPropData.Property.PropertyMetadata.Values;
		if (Ext.isEmpty(vals.Value) || vals.Value.length == 0){		// There are no permissibile values so show text box
			scPermValsCntrls = this.find('scPermVal','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.hide();
				scPermValIndCntrl.disable();
			}
			scPermValsCntrls = this.find('scPermVal','false');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.enable();
				scPermValIndCntrl.show();
			}
		} else {
			scPermValsCntrls = this.find('scPermVal','false');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.hide();
				scPermValIndCntrl.disable();
			}
			scPermValsCntrls = this.find('scPermVal','true');
			for (var j = 0;j < scPermValsCntrls.length ; j++ ){
				scPermValIndCntrl = scPermValsCntrls[j];
				scPermValIndCntrl.enable();
				scPermValIndCntrl.show();
			}
			this.setModel({"Values":vals},"modifyValueOptions",{
				clearOldVals: true
			});
		}
		this.setModel({"Property":{"BasePropertyName":this.mngPropData.Property.BasePropertyName,"Category":this.mngPropData.Property.Category}},"addOverride",{
            clearOldVals: true
        });
		
	},
	laterSetup:function(){
		this.setModel({},
        "resetNS", {
            clearOldVals: true
        });
	}
});
Ext.reg('xtype_name', sc.sma.MngPropAddNewOverride);
