scDefine(
		[ "dojo/text!./templates/VoucherLookupScreen.html",
				"scbase/loader!dojo/_base/declare",
				"scbase/loader!isccs/utils/BaseTemplateUtils",
				"scbase/loader!isccs/utils/UIUtils",
				"scbase/loader!sc/plat/dojo/utils/BaseUtils",
				"scbase/loader!sc/plat/dojo/utils/ModelUtils",
				"scbase/loader!sc/plat/dojo/widgets/Screen",
				"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
				"scbase/loader!sc/plat/dojo/utils/WidgetUtils" ],
		function(templateText, _dojodeclare, _isccsBaseTemplateUtils,
				_isccsUIUtils, _scBaseUtils, _scModelUtils, _scScreen,
				_scScreenUtils, _scWidgetUtils) {

			return _dojodeclare(
					"extn.home.VoucherLookupScreen",
					[ _scScreen ],
					{
						templateString : templateText,
						uId : "VoucherLookupScreen",
						packageName : "extn.home",
						className : "VoucherLookupScreen",
						title : "VoucherLookupScreen",
						screen_description : "Screen for entering voucher details.",
						namespaces : {
							targetBindingNamespaces : [ {
								description : "Values for Voucher Lookup.",
								value : 'voucherLookup_input'

							} ],
							sourceBindingNamespaces : []
						},
						hotKeys : [],
						events : [],
						subscribers : {
							local : [ {
								eventId : 'afterScreenInit',
								sequence : '30',
								handler : {
									methodName : "initializeScreen"
								}
							}, {
								eventId : 'Popup_btnConfirm_onClick',
								sequence : '30',
								description : '',
								listeningControlUId : 'Popup_btnConfirm',
								handler : {
									methodName : "confirmPopup",
									description : ""
								}
							}, {
								eventId : 'Popup_btnClose_onClick',
								sequence : '30',
								description : '',
								listeningControlUId : 'Popup_btnClose',
								handler : {
									methodName : "closePopUp",
									description : ""
								}
							}, ]
						},

						confirmPopup : function() {
							var isValid = true;
							isValid = _scScreenUtils.validate(this);

							if (_scBaseUtils.equals(isValid, true)) {
								var gcInput = null;

								gcInput = _scBaseUtils.getTargetModel(this,
										"voucherLookup_input", null);
								var certNo = _scModelUtils
										.getStringValueFromPath(
												"Voucher.CertNo", gcInput);

								if (!_scBaseUtils.isVoid(certNo)) {
									var isLoyaltyVoucher = "N";
									if (!/^\d+$/.test(certNo)) {
										isLoyaltyVoucher = "Y";
									}
									var voucherInput = {};
									voucherInput.Voucher = {};
									_scModelUtils.addStringValueToModelObject(
											"CertNo", certNo,
											voucherInput.Voucher);
									_scModelUtils.addStringValueToModelObject(
											"IsLoyaltyVoucher",
											isLoyaltyVoucher,
											voucherInput.Voucher);
									console.log("voucherInput", voucherInput);
									_isccsUIUtils.callApi(this, voucherInput,
											"extn_VSICheckVoucher_RefID");

								}
							}
						},
						closePopUp : function() {
							_scWidgetUtils.closePopup(this, "", false);

						},
						initializeScreen : function(event, bEvent, ctrl, args) {
						},
						handleMashupCompletion : function(mashupContext,
								mashupRefObj, mashupRefList, inputData,
								hasError, data) {
							// _scWidgetUtils.closePopup(this, "", false);

							if (!_scBaseUtils.isVoid(mashupRefList)) {
								if (_scBaseUtils.equals(
										mashupRefList[0].mashupRefId,
										"extn_VSICheckVoucher_RefID")) {
									var outputModel = mashupRefList[0].mashupRefOutput.Voucher;
									_scScreenUtils.setModel(this,
											"extn_checkVoucher_output",
											outputModel, null);
									console.log("outputModel", outputModel);
									var certNo=null;
									certNo = _scModelUtils
											.getStringValueFromPath("CertNo",outputModel);
									console.log("CertNo: " + certNo);
									var isLoyaltyVoucher = "N";
									if (!_scBaseUtils.isVoid(certNo)) {
										if (!/^\d+$/.test(certNo)) {
											isLoyaltyVoucher = "Y";
										}
									}	
										var isValid = _scModelUtils
												.getStringValueFromPath(
														"isValid", outputModel);
										console.log("isValid: " + isValid);
										var errorcode = null;
										errorcode = _scModelUtils
												.getStringValueFromPath(
														"ErrorCode",
														outputModel);

										if (_scBaseUtils.equals("000",
												errorcode)
												|| _scBaseUtils.equals("003",
														errorcode)|| _scBaseUtils.equals("002",
												errorcode)||(_scBaseUtils.equals("N",
												isLoyaltyVoucher) && !_scBaseUtils.isVoid(certNo) )) {
											var ModIssueDate = _scModelUtils
													.getStringValueFromPath(
															"IssueDate",
															outputModel);
											console.log("ModIssueDate: "
													+ ModIssueDate);
											var RedeemAmount = _scModelUtils
													.getStringValueFromPath(
															"RedeemAmount",
															outputModel);
											var IssueAmount = _scModelUtils
													.getStringValueFromPath(
															"IssueAmount",
															outputModel);
											var redeemStatus = _scModelUtils
													.getStringValueFromPath(
															"RedeemStatus",
															outputModel);				
											if (_scBaseUtils.equals("N",isLoyaltyVoucher ))
											{
												ModIssueDate = ModIssueDate.split('T')[0];
											}
											var Valid="Y";
											if(_scBaseUtils.equals("003",errorcode)|| _scBaseUtils.equals("002",errorcode)||
											(_scBaseUtils.equals("N",isLoyaltyVoucher) && _scBaseUtils.equals("N",isValid)))
											{
												Valid='N';
											}
											if(_scBaseUtils.equals("N",isLoyaltyVoucher ))
											{
												ModIssueDate = ModIssueDate.split('T')[0];
											}
											var isRedeemed="N";

											if(_scBaseUtils.equals("002",errorcode))
												isRedeemed="Y";
											else 
												isRedeemed="N";
											bindings = {};
											popupParams = {};
											var dialogParams = null;
											dialogParams = {};
											_scModelUtils
													.addStringValueToModelObject(
															"ModIssueDate",
															ModIssueDate,
															outputModel);
											_scModelUtils
													.addStringValueToModelObject(
															"Valid",
															Valid,
															outputModel);	
											_scModelUtils
													.addStringValueToModelObject(
															"IsRedeemed",
															isRedeemed,
															outputModel);				
																		
											popupParams["screenInput"] = outputModel;
											_isccsUIUtils.openSimplePopup(
													"extn.home.VoucherDetails",
													"Voucher Details", this,
													popupParams, dialogParams);

										} else if ((_scBaseUtils.isVoid(certNo) && _scBaseUtils.equals("N",
												isValid))
												|| _scBaseUtils.equals("001",
														errorcode)) {
											_scScreenUtils
													.showErrorMessageBox(
															this,
															"Entered Voucher is Invalid",
															null, null, null);

										} else
											_scScreenUtils
													.showErrorMessageBox(
															this,
															"Voucher Lookup is unavailable at this time. Please try after sometime",
															null, null, null);
									}
									_scWidgetUtils.closePopup(this, "", false);
								}
							}

						
					});
		});
