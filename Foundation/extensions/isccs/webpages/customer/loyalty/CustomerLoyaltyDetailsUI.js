/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/CustomerLoyaltyDetails.html", 
		  "scbase/loader!dijit/form/Button", 
		  "scbase/loader!dojo/_base/declare", 
		  "scbase/loader!dojo/_base/kernel", 
		  "scbase/loader!dojo/_base/lang", 
		  "scbase/loader!dojo/text", 
		  "scbase/loader!idx/form/DateTextBox", 
		  "scbase/loader!idx/form/FilteringSelect", 
		  "scbase/loader!idx/form/NumberTextBox", 
		  "scbase/loader!idx/form/TextBox", 
		  "scbase/loader!idx/layout/ContentPane", 
		  "scbase/loader!idx/layout/MoveableTabContainer", 
		  "scbase/loader!idx/layout/TitlePane", 
		  "scbase/loader!isccs/customer/create/address/ManageCustomerAddressesInitController", 
		  "scbase/loader!isccs/customer/create/payment/ManageCustomerPaymentMethodsInitController", 
		  "scbase/loader!isccs/utils/BaseTemplateUtils", 
		  "scbase/loader!isccs/utils/CustomerUtils", 
		  "scbase/loader!isccs/utils/UIUtils", 
		  "scbase/loader!sc/plat", 
		  "scbase/loader!sc/plat/dojo/binding/ButtonDataBinder", 
		  "scbase/loader!sc/plat/dojo/binding/ComboDataBinder", 
		  "scbase/loader!sc/plat/dojo/binding/DateDataBinder", 
		  "scbase/loader!sc/plat/dojo/binding/SimpleDataBinder", 
		  "scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout", 
		  "scbase/loader!sc/plat/dojo/utils/BaseUtils", 
		  "scbase/loader!sc/plat/dojo/utils/EventUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ModelUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils", 
		  "scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
		  "scbase/loader!sc/plat/dojo/utils/WidgetUtils", 
		  "scbase/loader!sc/plat/dojo/widgets/ControllerWidget", 
		  "scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget", 
		  "scbase/loader!sc/plat/dojo/widgets/Screen",
		  "scbase/loader!isccs/utils/ContextUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!isccs/utils/SearchUtils",
		  "scbase/loader!sc/plat/dojo/utils/GridxUtils"
], function(
				templateText, 
				_dijitButton, 
				_dojodeclare, 
				_dojokernel, 
				_dojolang, 
				_dojotext, 
				_idxDateTextBox, 
				_idxFilteringSelect, 
				_idxNumberTextBox, 
				_idxTextBox, 
				_idxContentPane, 
				_idxMoveableTabContainer, 
				_idxTitlePane, 
				_isccsManageCustomerAddressesInitController, 
				_isccsManageCustomerPaymentMethodsInitController, 
				_isccsBaseTemplateUtils, 
				_isccsCustomerUtils, 
				_isccsUIUtils, 
				_scplat, 
				_scButtonDataBinder, 
				_scComboDataBinder, 
				_scDateDataBinder, 
				_scSimpleDataBinder, 
				_scAdvancedTableLayout, 
				_scBaseUtils, 
				_scEventUtils, 
				_scModelUtils, 
				_scResourcePermissionUtils, 
				_scScreenUtils, 
				_scWidgetUtils, 
				_scControllerWidget, 
				_scIdentifierControllerWidget, 
				_scScreen,
				_isccsContextUtils,
				_scEditorUtils,
				_isccsSearchUtils,
				_scGridxUtils
	) {
	var count=0;
    return _dojodeclare("extn.customer.loyalty.CustomerLoyaltyDetailsUI", [_scScreen], {
        templateString: templateText,
		postMixInProperties: function() {
        },
        //CustomerLoyaltyDetails
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.customer.loyalty.templates", "CustomerLoyaltyDetails.html"),
            shared: true
        },
        uId: "CustomerLoyaltyDetails",
        packageName: "extn.customer.loyalty",
        className: "CustomerLoyaltyDetails",
        extensible: true,
        title: "Rewards_Results",
		extensible: true,
		namespaces: {
            targetBindingNamespaces: [],
	        sourceBindingNamespaces: [{
                value: 'LST_listAPIInput',
                description: "This namespace is used to store the input used to load the data in the grid. It is used when the grid is refreshed."
            }, {
                value: 'getRewardList_output',
                description: "This is the list of orders retrieved with the provided search criteria used to populate the grid."
            }]
        },
		hotKeys: [{
            id: "closebtn",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_btnClose",
            invocationContext: "Editor",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
		events : [
			{
			    name: 'callListApi'
			},
			{
	            name: 'addExtraHandlers'
	        }
		],
		subscribers : {
			local : [
				{
		            eventId: 'afterScreenInit',
		            sequence: '51',
		            handler : {
			            methodName : "extn_init"
		            }
				},
		        {
		            eventId: 'callListApi',
		            sequence: '25',
		            description: '',
		            handler: {
		                methodName: "LST_executeApi",
		                description: ""
		            }
	            },
	            {
	                eventId: 'listGrid_afterPagingload',
	                sequence: '25',
	                description: 'Listens for after every Paging load',
	                handler: {
	                    methodName: "afterPagingload",
	                    description: "Handles the hook to perform some actions such as select first Row after every next is performed"
	                }
	            },
	            {
	                eventId: 'listGrid_pagingload',
	                sequence: '25',
	                description: 'Listens for after Paging load',
	                handler: {
	                    methodName: "onPagingload",
	                    description: "Handles the hook to perform some actions such as collapsing search criteria panel, opening details page when there is a single record, select first Row"
	                }
	            },
	            {
	                eventId: 'Popup_btnClose_onClick',
	                sequence: '25',
	                handler: {
	                    methodName: "handleClose",
	                    description: ""
	                }
	            }
			]
		},
		extn_init: function(event, bEvent, ctrl, args){
			console.log("event",event);
			console.log("bEvent",bEvent);
			console.log("ctrl",ctrl);
			console.log("args",args);
			console.log("isPopup",_scScreenUtils.isPopup(this));
			var isPopup = _scScreenUtils.isPopup(this);
			if(isPopup){
				var inputData = _scScreenUtils.getInitialInputData(this);
				console.log("inputData",inputData);
				_isccsUIUtils.callApi(
		                this, inputData, "extn_getRewardsList_RefID", null);
			}else{
				_scWidgetUtils.hideWidget(this, "Popup_btnClose", false);
			}
		},
		LST_executeApi: function(event, bEvent, ctrl, args){
			
			var scr = null;
            var inputData = null;
            scr = _scEventUtils.getScreenFromEventArguments(
            args);
            inputData = _scBaseUtils.getAttributeValue("inputData", false, args);
            _scEventUtils.fireEventInsideScreen(
            this, "addExtraHandlers", null, args);
            _scScreenUtils.setModel(
            this, "LST_listAPIInput", inputData, null);
            var gridID = "listGrid";
            
            _isccsUIUtils.callApi(
            		scr, inputData, "extn_getRewardsList_RefID", null);
		},
		handleMashupOutput: function(
        mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
            if (
            _scBaseUtils.equals(
            mashupRefId, "extn_getRewardsList_RefID")) {
            	console.log("SETTING_THIS");
                _scScreenUtils.setModel(
                this, "getRewardList_output", modelOutput, null);
                
                var rewardList = [];
                rewardList = modelOutput.Customer.RewardsList.Reward;
                console.log("rewardList",rewardList);
                var tbl = document.createElement("tbody");
                tbl.setAttribute("id", "rowBody"+count++);
                count = count++;
                var currentNode = document.getElementById(this.id);
                var currentTable = currentNode.getElementsByTagName("table")[0];
                currentTable.replaceChild(tbl, currentTable.getElementsByTagName("tbody")[0]);
                for(var i=0; i<rewardList.length;i++){
                	var row = document.createElement("tr");
                	tbl.appendChild(row);
                	
                	var strRewardId = "";
                	var strRewardAmount = "";
                	var strExpirationDate = "";
                	var strIsActive = "";
                	
                    for (var j in rewardList[i]) {
                    	var attributeName = j.toString();
                    	if(_scBaseUtils.equals(attributeName, "RewardId")){
                    		strRewardId = rewardList[i][j].valueOf();
                		}else if(_scBaseUtils.equals(attributeName, "RewardAmount")){
                			strRewardAmount = rewardList[i][j].valueOf();
                		}else if(_scBaseUtils.equals(attributeName, "ExpirationDate")){
                			strExpirationDate = rewardList[i][j].valueOf();
                		}else if(_scBaseUtils.equals(attributeName, "IsActive")){
                			strIsActive = rewardList[i][j].valueOf();
                		}
                    }
                    
                    var cell1 = document.createElement("td");
                    cell1.setAttribute("class", "valueCells");            		
                    cell1.setAttribute("style", "width:12%;min-width:12%;max-width:12%;");
            		var cellText1 = document.createTextNode(strRewardId);
            		cell1.appendChild(cellText1);
            		row.appendChild(cell1);
            		var cell2 = document.createElement("td");
            		cell2.setAttribute("class", "valueCells");
            		cell2.setAttribute("style", "width:12%;min-width:12%;max-width:12%;");
            		var cellText2 = document.createTextNode(strRewardAmount);
            		cell2.appendChild(cellText2);
            		row.appendChild(cell2);
            		var cell3 = document.createElement("td");
            		cell3.setAttribute("class", "valueCells");
            		cell3.setAttribute("style", "width:12%;min-width:12%;max-width:12%;");
            		var cellText3 = document.createTextNode(strExpirationDate);
            		cell3.appendChild(cellText3);
            		row.appendChild(cell3);
            		var cell4 = document.createElement("td");
            		cell4.setAttribute("class", "valueCells");
            		cell4.setAttribute("style", "width:12%;min-width:12%;max-width:12%;");
            		var cellText4 = document.createTextNode(strIsActive);
            		cell4.appendChild(cellText4);
            		row.appendChild(cell4);
                }
            }
            console.log("tbl",tbl);
        },
        handleMashupCompletion: function(
        mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {
            _isccsBaseTemplateUtils.handleMashupCompletion(
            mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data, this);
        },
        handleClose: function(uiEvent, businessEvent, control, args) {
        	
        	console.log("CAMER_HERE");
        	_scWidgetUtils.closePopup(this, "CLOSE", false);
        	
        },
    });
});
