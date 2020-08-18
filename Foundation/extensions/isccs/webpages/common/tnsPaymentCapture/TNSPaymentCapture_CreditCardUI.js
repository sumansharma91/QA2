/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["dojo/text!./templates/TNSPaymentCapture_CreditCard.html", 
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
		  "scbase/loader!isccs/utils/BaseTemplateUtils", "scbase/loader!isccs/utils/CustomerUtils", 
		  "scbase/loader!isccs/utils/UIUtils", "scbase/loader!sc/plat", 
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
		  "scbase/loader!sc/plat/dojo/widgets/Screen","scbase/loader!isccs/utils/ContextUtils",
		  "scbase/loader!sc/plat/dojo/utils/EditorUtils",
		  "scbase/loader!dojo/on"
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
				_dOn
	) {
    return _dojodeclare("extn.common.tnsPaymentCapture.TNSPaymentCapture_CreditCardUI", [_scScreen], {
        templateString: templateText,
		postMixInProperties: function() {
        },
        baseTemplate: {
            url: _dojokernel.moduleUrl("extn.common.tnsPaymentCapture.templates", "TNSPaymentCapture_CreditCard.html"),
            shared: true
        },
        uId: "TNSPaymentCapture_CreditCard",
        packageName: "extn.common.tnsPaymentCapture",
        className: "TNSPaymentCapture_CreditCard",
        extensible: true,
        title: "TNSPaymentCapture_CreditCard",
		extensible: true,
		namespaces: {
            targetBindingNamespaces: [{
                value: 'PaymentCapture_input',
                description: "Contains the results of entered Credit Card payment method."
            }],
            sourceBindingNamespaces: [{
                value: 'paymentCapture_Output',
                description: "Values to initialize Payment Capture for Credit Card when editing payment method."
            }, {
                value: 'getYearRange_Output',
                description: "Year range accepted for Credit Cards."
            }, {
                value: 'getRuleDetails_CCName_Output',
                description: "Rule for to determine single name field or name field separated out."
            }, {
                value: 'getRuleDetails_EncryptAdd_Output',
                description: "Rule for encrypting additional credit card attributes."
            }, {
                value: 'getRuleDetails_RealTimeAuthorization_Output',
                description: "Rule to turn on authorizing payment methods on confirm."
            }, {
                value: 'getPaymentCardTypeList_Output',
                description: "List of credit card types accepted."
            }, {
                value: 'getMonthList_Output',
                description: "Month of the year."
            }, {
                value: 'billingAddress_Output',
                description: "Billing address provided for credit card."
            }]
        },
		hotKeys: [],
		events : [],
		subscribers : {
			local : [{
				eventId: 'afterScreenInit',
				sequence: '25',
				handler: {
					methodName: "initializeScreen"
				}
			},{
				eventId: 'Popup_btnNext_onClick',
				sequence: '25',
				description: 'Next / Confirm Button Action',
				listeningControlUId: 'Popup_btnNext',
				handler: {
					methodName: "onPopupConfirm",
					description: ""
				}
			},{
				eventId: 'Popup_btnCancel_onClick',
				sequence: '25',
				description: '',
				listeningControlUId: 'Popup_btnCancel',
				handler: {
					methodName: "onPopupClose",
					description: ""
				}
			}]
		},
		onApply: function(event, bEvent, ctrl, args) {
            alert("onApply");
        },
	//Aurus iFrame functions starts
       
        getCardToken: function() { // This method needs to be called on Submit button click present on Merchant's page.
            //var $form = $('#payment-form');
            var $form = document.getElementById("payment-form");
           // $form.find('#buttonSubmit').prop('disabled', true);
            var frame = document.getElementById('frame_carddetails');
            frame.contentWindow.postMessage('aurus-token', "*");
        },
        
        //Aurus iFrame functions end
        initializeScreen: function(event, bEvent, ctrl, args) {
				//alert("initializeScreen");
				var aurusSessionResponse1 = _scBaseUtils.getStringValueFromBean("aurusSessionResponse", args.screen);
				var savedpaymenttargetModel = _scBaseUtils.getStringValueFromBean("savedpaymenttargetModel", args.screen);
				
				var iFrameId = document.getElementById("frame_carddetails");
				
				var myiFrameUrl=_scModelUtils.getStringValueFromPath("SessionResponse.IFrameUrl", aurusSessionResponse1);
				var arrSplittedStrUrl = myiFrameUrl.split("/");
				var resultArrSplittedStrUrl = arrSplittedStrUrl[0] + "//" + arrSplittedStrUrl[2];
				window.aurusUrl = resultArrSplittedStrUrl;
				iFrameId.src = myiFrameUrl;
				
				
				/*
				iFrameId.onload = function() {
								alert("Iframe loaded");
				}; // before setting 'src'
*/

				
				// Defect SU-41 - Start
/*
				var min = new Date().getFullYear();
				var max = min + 19;
				for (var i = min; i<=max; i++){
					var opt = document.createElement("option");
					opt.value = i;
					opt.innerHTML = i;
					document.getElementById("expiry-year").appendChild(opt);
				}
				document.getElementById("expiry-year").value = min;
				// Defect SU-41 - End
				var currentDate = _scBaseUtils.getDate();
*/
				this.initScreen(false);
		},     
        initScreen: function(flag) {
			var currentDate = _scBaseUtils.getDate();
			
/*
			//configuration variable contains the details for TNS field hosting and the mapping
			var configuration = {
				"fields": {
					"cardNumber": "#card-number",
					"securityCode": "#security-code",
					"expiryMonth": "#expiry-month",
					"expiryYear": "#expiry-year"
				},
				"frameEmbeddingMitigation": ["javascript"]					
			};
			
			//START - Fix for UUAT-1
			//setTimeout(function,time) logic will make the js wait for given "time" and execute the "function" after that.
			if(typeof PaymentSession === 'undefined' && !flag){
				//This loop will be executed only on the first try if the session.js has not been defined yet.
				that = this;
				console.log('I am here');
				// SUH-33 Fix - START
				setTimeout(function()
				{
					that.initScreen(true);
				}, 3000);
				//return;
			}else if(typeof PaymentSession === 'undefined' && flag){
				console.log('I am here1');
				//Entering this loop indiates that the initialization failed even after the wait time. User will need to reload the pop up and try.
				_scScreenUtils.showErrorMessageBox(this,"Session initialization failed. Please reload the screen and try again.",null,null,null);
				//return;
			}else{
				PaymentSession.attachToForm(configuration,function(response){
					if(response == "ok") {
						// do nothing
					} else {
						_scScreenUtils.showErrorMessageBox(this,"Response status: System Error ",null,null,null);
						return;
					}
				});
				// SUH-33 Fix - END
			}
			//END - Fix for UUAT-1
*/
		},
        handleMashupCompletion: function(mashupContext, mashupRefObj, mashupRefList, inputData, hasError, data) {

			if (!(_scBaseUtils.isVoid(mashupRefList[0].mashupRefOutput))){
					var modelOutput = mashupRefList[0].mashupRefOutput;
			}			
		},
		onParentApply: function(targetModel){
		/*
			var tnsSessionUpdateCallback =
			function(response){
				if (response.status) {
				if ("ok" == response.status) {
					ownerScreenInit.getToken(response);
				} else if ("fields_in_error" == response.status)  {
					if (response.errors.cardNumber) {
						//alert("Card number invalid or missing.");
						_scScreenUtils.showErrorMessageBox(this,"Card number invalid or missing.",null,null,null);					

						return;
					}
					if (response.errors.expiryYear) {
						//alert("Expiry year invalid or missing.");
						_scScreenUtils.showErrorMessageBox(this,"Expiration year invalid or missing.",null,null,null);					
						return;
					}
					if (response.errors.expiryMonth) {
						//alert("Expiry month invalid or missing.");
						_scScreenUtils.showErrorMessageBox(this,"Expiration month invalid or missing.",null,null,null);					
						return;
					}
					if (response.errors.securityCode) {
						//alert("Security code invalid.");
						_scScreenUtils.showErrorMessageBox(this,"Security code invalid.",null,null,null);					
						return;
					}
				} else if ("request_timeout" == response.status)  {
					//alert("Session update failed with request timeout: " + response.errors.message);
					_scScreenUtils.showErrorMessageBox(this,"Session update failed with request timeout.",null,null,null);					
				} else if ("system_error" == response.status)  {
					//alert("Session update failed with system error: " + response.errors.message);
					_scScreenUtils.showErrorMessageBox(this,"Session update failed with system error.",null,null,null);					

				}
				} else {
					//alert("Session update failed: " + response);
					_scScreenUtils.showErrorMessageBox(this,"Session update failed.",null,null,null);					

				}
			};
		PaymentSession.updateSession(function(updateSessionResponse){
										tnsSessionUpdateCallback(updateSessionResponse);
										}
									);

*/
			var ownerScreenInit = this.getOwnerScreen(true);
			var responseHandler= function(JSONdata) {
	            try {
	                //var $msg = $('#token-error');
	               // alert('JSON DATA' + JSON.stringify(JSONdata));
	                var respCode = JSONdata.response_code;
	                var respTxt = JSONdata.response_text;
	                var maskedCardNum = JSONdata.masked_card_num;
	                var cardType = JSONdata.card_type;
	                var cardExpiry = JSONdata.card_expiry_date;
	                var ott = JSONdata.one_time_token;
	                var cardHolderName = JSONdata.card_holder_name;
	                //var transactionId = JSONdata.transaction_id;
	                console.log('JSON response: ' + JSON.stringify(JSONdata));
	                //var $form = $('#payment-form');
	                var $form = document.getElementById("payment-form");

	                if (Number(JSONdata.response_code) > 0) { //Handle the Error Response here
	                    //$msg.text('ERROR: ' + JSONdata.response_code + ' - ' + JSONdata.response_text);
	                	
	                	 ownerScreenInit.getToken(JSONdata);
	                    
	                    _scScreenUtils.showErrorMessageBox(this, "'ERROR: ' + JSONdata.response_code + ' - ' + JSONdata.response_text",null,null,null);
	                    //$('#buttonSubmit').prop('disabled', false);
	                } else {
	                    //Handle the success response here like below:
	                    //$msg.text('SUCCESS');
	                	
	                    /*
	                    _scScreenUtils.showErrorMessageBox(this, "'SUCCESS'",null,null,null);
	                    //var $form = $('#payment-form');
	                    var $form = document.getElementById("payment-form");
	                    $form.append($('<input type="hidden" name="card_token">').val(JSONdata.card_token));
	                    $form.append($('<input type="hidden" name="masked_card_num">').val(JSONdata.masked_card_num));
	                    $form.append($('<input type="hidden" name="card_type">').val(JSONdata.card_type));
	                    $form.append($('<input type="hidden" name="card_expiry_date">').val(JSONdata.card_expiry));
	                    $form.append($('<input type="hidden" name="response_code">').val(JSONdata.response_code));
	                    $form.append($('<input type="hidden" name="response_text">').val(JSONdata.response_text));
	                    $form.append($('<input type="hidden" name="one_time_token">').val(JSONdata.one_time_token));
	                    $form.append($('<input type="hidden" name="card_holder_name">').val(JSONdata.card_holder_name));
	                    //alert('one_time_token: ' + ott + '\ncard_expiry_date: ' + cardExpiry + '\nmasked_card_number: ' + maskedCardNum + '\ncard_type: ' + cardType + '\nresponse_code: ' + respCode + '\nresponse_text: ' + respTxt);
	                    //$form.get(0).submit();
	                    //$form.find('#buttonSubmit').prop('disabled', false);
	                    
	                    */
	                    ownerScreenInit.getToken(JSONdata);
	                    
	                }
	            } catch (e) {
	                //alert("ERROR: " + e);
	            }
	        };
			
			this.getCardToken();
			_dOn(window, "message", function(event) { // This will get triggered when Aurus will post the OTT response on Merchant's page.
	            var data = event.data;
				var aurusURL = window.aurusUrl;
	            //var aurusURL = 'https://uat48.auruspay.com'; 
	            // Merchant needs to get this URL by doing  substring of  Aurus  Iframe Url received in Session API. Mentioned URL is just an example.
	            if (event.origin != aurusURL) {
	                console.log("Wrong Origin...");
	                return;
//	            } else if (data.startsWith('response')) {
	            } else if (data.substr(0, 'response'.length) === 'response') { // fix for IE11
	                var splt = data.split('=');
	                var json = JSON.parse(splt[1]);
	                responseHandler(json);
//	            } else if (data.startsWith('enablePlaceOrder')) {
	            } else if (data.substr(0, 'enablePlaceOrder'.length) === 'enablePlaceOrder') { // fix for IE11
	                var splt = data.split('=');
	                var json = splt[1];
	                //alert("json " + json);
	                var enableBtn = json == 'true' ? true : false;
	                //alert("enableBtn " + enableBtn);
	                //enablePlaceOrderBtn(enableBtn);
	            }
	        });

			
		}
    });
});
