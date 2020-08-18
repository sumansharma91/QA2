	
package com.vsi.scc.oms.pca.extensions.orderEntry;

/**
 * Created on Dec 26,2013
 *
 */
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.extensions.store.common.VSIAlternateStoresExtnBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
public class VSIOrderEntryWizardExtensionBehavior extends YRCWizardExtensionBehavior {

	public static HashMap<String, String> itemAvailability = new HashMap<String, String>();
	public static boolean isCardValid = true;
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}


	public String getExtnNextPage(String currentPageId) {
		//TODO
		return null;
	}

	public IYRCComposite createPage(String pageIdToBeShown) {
		//TODO
		return null;
	}

	public void pageBeingDisposed(String pageToBeDisposed) {
		//TODO
	}

	/**
	 * Called when a wizard page is about to be shown for the first time.
	 *
	 */
	public void initPage(String pageBeingShown) {
		//TODO
	}






	@Override
	public void postCommand(YRCApiContext apiContext) {

		// get the api name.
		String strApiName = apiContext.getApiName();
		String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();

		if(strApiName.equalsIgnoreCase("changeOrderForPaymentMethod")){

			NodeList paymentMethodNL = null;
			Element orderEle = getModel("ReturnOrderDetails");
			//System.out.println(VSIXmlUtils.getElementXMLString(orderEle));
			if(orderEle != null){
				Element billToEle = (Element) orderEle.getElementsByTagName("PersonInfoBillTo").item(0);
				Element overAllTotalsEle = (Element) orderEle.getElementsByTagName("OverallTotals").item(0);

				String custName = " ";
				String lineSubTotal = "0.00";
				String taxTotal = "0.00";
				String grandTotal = "0.00";
				String voucherNo = "0.00";
				String voucherTotal = "0.00";
				String grandDiscount = "0.00";
				Map<String, String> paymentTypesMap = new HashMap<String, String>();
				String orderNo = orderEle.getAttribute("OrderNo");
				String message = "";
				Document[] outputXMLs = apiContext.getOutputXmls();
				for(int k = 0;k <outputXMLs.length ; k ++		){
					//System.out.println(" outputXMLs.length = " + outputXMLs.length);
					Document outputXML = outputXMLs[k];
					//System.out.println("*********************** outputXML : \n"+VSIXmlUtils.getElementXMLString(outputXML.getDocumentElement()));
					Element paymentEle = outputXML.getDocumentElement();
					if( paymentEle != null){
						paymentMethodNL = paymentEle.getElementsByTagName("PaymentMethod");
						for(int i= 0; i < paymentMethodNL.getLength(); i++){
							Element paymentMethodEle = (Element) paymentMethodNL.item(i);
							if(paymentMethodEle != null){
								String paymentType = paymentMethodEle.getAttribute("PaymentType");
								if(paymentType != null && paymentType.equalsIgnoreCase("CREDIT_CARD"))
									grandTotal = paymentMethodEle.getAttribute("MaxChargeLimit");
								else if (paymentType != null && paymentType.equalsIgnoreCase("VOUCHERS")){
									voucherNo = paymentMethodEle.getAttribute("PrimaryAccountNo");
									voucherTotal = paymentMethodEle.getAttribute("MaxChargeLimit");									
									paymentTypesMap.put(voucherNo, voucherTotal);
								}
									

							}
						}


					}

				}
				if(billToEle != null){
					custName = billToEle.getAttribute("FirstName")+ " " + billToEle.getAttribute("LastName")+ " :";
				}
				if(overAllTotalsEle != null){
					lineSubTotal = overAllTotalsEle.getAttribute("LineSubTotal");
					taxTotal = overAllTotalsEle.getAttribute("GrandTax");

					grandDiscount = overAllTotalsEle.getAttribute("GrandDiscount");
					message = "\n\n"+"Your Merchandise Total is $"+lineSubTotal;
					if(Double.parseDouble(taxTotal) > 0){
						message = message + "\n" + "Tax on this order is $"+taxTotal;
					}
					if(Double.parseDouble(grandDiscount) > 0){
						message = message + "\n" + "Discount on this order is $"+grandDiscount;
					}
					if(Double.parseDouble(grandTotal) > 0)
						message = message + "\n" + "Your Credit Card will be charged $"+grandTotal;
					if(paymentTypesMap.size() > 0){
						for (Map.Entry<String,String> entry : paymentTypesMap.entrySet()) {
							String key = entry.getKey();
						    String value = entry.getValue();
						    message = message + "\n" + "$" +value+ " is redeemed from Voucher No: "+key;
						}
						
					}
					message = message + "\n" + "Would you like your Order Number ? "+orderNo;
					message = message + "\n\n" + "Thank you for calling The Vitamin Shoppe and have a nice day.";
				}
				YRCPlatformUI.showInformation("Thank You !",custName+ message);
			}


		}else if("getFulfillmentOptionsForLines".equalsIgnoreCase(strApiName)){

			if("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDAdvancedAddItemPage".equalsIgnoreCase(strCurrentPage)){
				Element eleOutput = apiContext.getOutputXml().getDocumentElement();
				if(!YRCPlatformUI.isVoid(eleOutput)){
					Element elePromiseLine = (Element)eleOutput.getElementsByTagName("PromiseLine").item(0);
					if(!YRCPlatformUI.isVoid(elePromiseLine)){
						String strItem = elePromiseLine.getAttribute("ItemID");
						Element eleOption = (Element)eleOutput.getElementsByTagName("Option").item(0);
						if(!YRCPlatformUI.isVoid(eleOption)){
							String strHasUnavailableQty = eleOption.getAttribute("HasAnyUnavailableQty");
							if("Y".equalsIgnoreCase(strHasUnavailableQty))
								itemAvailability.put(strItem, "N");
							else
								itemAvailability.put(strItem, "Y");
						}
					}
				}
			}

		}else if(("changeOrder").equalsIgnoreCase(apiContext.getApiName()) && ("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDOrderEntryFulfillmentSummaryPage".equalsIgnoreCase(strCurrentPage))){
			Document outDoc = apiContext.getOutputXml();
			Element eleOutput =outDoc.getDocumentElement();
			Element changeOrderInp= apiContext.getInputXml().getDocumentElement();
			String strOrderNo = eleOutput.getAttribute("OrderNo");
			if(!YRCPlatformUI.isVoid(strOrderNo) && !YRCPlatformUI.isVoid(outDoc)){
				YRCApiContext context = new YRCApiContext();
				context.setApiName("VSIApplyBOGO");
				context.setFormId("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizards.YCDOrderEntryWizard");
				context.setInputXml(outDoc);
				context.setShowError(true);
				callApi(context);	
			}
			
		}

	}

	private void callCCVerifyService(Document inDoc) {

		YRCApiContext context = new YRCApiContext();
		context.setApiName("VSIVerifyCreditCard");
		context.setFormId("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizards.YCDOrderEntryWizard");
		context.setInputXml(inDoc);
		context.setShowError(true);
		callApi(context);		

	}

	public void handleApiCompletion(YRCApiContext ctxApi) {
		if (ctxApi.getInvokeAPIStatus() < 0) {
			YRCPlatformUI.showError("API call Failed", "API call Failed");
		} else if ("VSIVerifyCreditCard".equals((String) ctxApi
				.getApiName())) {
//			isCardValid = true;
//			Element eleOutput = ctxApi.getOutputXml().getDocumentElement();
//			if(!YRCPlatformUI.isVoid(eleOutput)){
//				Element elePaymentMethod = (Element)eleOutput.getElementsByTagName("PaymentMethod").item(0);
//				if(!YRCPlatformUI.isVoid(elePaymentMethod)){
//					String strIsCallSuccessful = elePaymentMethod.getAttribute("IsCallSuccesful");
//					if("Y".equalsIgnoreCase(strIsCallSuccessful)){
//						String strIsCardValid = elePaymentMethod.getAttribute("IsValid");
//						if("N".equalsIgnoreCase(strIsCardValid)){
//							isCardValid = false;						
//						}else{
//							isCardValid = true;
//						}
//					}else{
//						isCardValid = false;
//					}
//				}
//			}

		}
		super.handleApiCompletion(ctxApi);
	}

	/**
	 * Method for validating the text box.
	 */
	public YRCValidationResponse validateTextField(String fieldName, String fieldValue) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		return super.validateTextField(fieldName, fieldValue);
	}

	/**
	 * Method for validating the combo box entry.
	 */
	public void validateComboField(String fieldName, String fieldValue) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
	}

	/**
	 * Method called when a button is clicked.
	 */
	public YRCValidationResponse validateButtonClick(String fieldName) {
		// TODO Validation required for the following controls.
		if("btnConfirm".equalsIgnoreCase(fieldName)){
//			String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();
//			boolean IsCardValid = VSIPaymentMethodPopupExtensionBehavior.isCardValid;
//			if(!IsCardValid){
//			YRCPlatformUI.showError("Error", "Credit Card is Invalid");
//			return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Credit Card Verification failed, Credit Card is Invalid");
//			}
			
		} 




		else if("bttnNext".equalsIgnoreCase(fieldName)){
			String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();
			String strAddItemPage = "com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDAdvancedAddItemPage";
			Element eleOrderModel = null;		
			boolean isQtyError = false;
			if(strAddItemPage.equalsIgnoreCase(strCurrentPage)  ){
				
				eleOrderModel = getModel("OriginalOrder");
				
			}
			
				ArrayList<String> ShipNodes = new ArrayList<String>(); 
				if(!YRCPlatformUI.isVoid(eleOrderModel)){
					NodeList nlist = eleOrderModel.getElementsByTagName("OrderLine");
					for(int a=0;a<nlist.getLength();a++){
						Element orderLineELe=(Element) nlist.item(a);
						if(!YRCPlatformUI.isVoid(orderLineELe)){
							String strShipNode = orderLineELe.getAttribute("ShipNode");
							double dblQuantity = YRCXmlUtils.getDoubleAttribute(orderLineELe, "OrderedQty");
							if(!YRCPlatformUI.isVoid(dblQuantity)){
								int intQty = (int)dblQuantity;
								double d = dblQuantity - intQty;
								if(d > 0.0)
									isQtyError = true;
							}
							if(!YRCPlatformUI.isVoid(strShipNode) && !ShipNodes.contains(strShipNode)){								
									ShipNodes.add(strShipNode);
							}
						}
					}
					if(ShipNodes.size() > 1){
						YRCPlatformUI.showError("Error", "Multiple pick up locations are selected. Please select only one.");
						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Multiple pick up locations are selected. Please select only one.");

					}else if (isQtyError){
						YRCPlatformUI.showError("Error", "Please enter the correct quantity. It has to be a whole number.");
						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Please correct the quantity. It has to be a whole number.");
					}
				}
			}
		 
		// TODO Create and return a response. 
		return super.validateButtonClick(fieldName);
	}

	/**
	 * Method called when a link is clicked.
	 */
	public YRCValidationResponse validateLinkClick(String fieldName) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}

	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
		// Create and return the binding data definition for the table.

		// The defualt super implementation does nothing.
		return super.getExtendedTableBindingData(tableName, tableColumnNames);
	}

	public boolean preCommand(YRCApiContext apiContext) {

		String[] apinames = apiContext.getApiNames();
		String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();
		if(("changeOrder").equalsIgnoreCase(apiContext.getApiName()) && ("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDAdvancedAddItemPage".equalsIgnoreCase(strCurrentPage))){

			Element elePersonInfoShipTo = null;
			Element changeOrderInp=apiContext.getInputXml().getDocumentElement();

			NodeList nlist=changeOrderInp.getElementsByTagName("OrderLine");
			for(int a=0;a<nlist.getLength();a++){
				Element orderLineELe=(Element) nlist.item(a);
				String strIsNewItem = orderLineELe.getAttribute("NewItem");
				if(YRCPlatformUI.isVoid(elePersonInfoShipTo))
					elePersonInfoShipTo = (Element)orderLineELe.getElementsByTagName("PersonInfoShipTo").item(0);
//				changes made as part of ISPU solution
				String strDeliveryMethod = orderLineELe.getAttribute("DeliveryMethod");
				if("PICK".equalsIgnoreCase(strDeliveryMethod)){
//					orderLineELe.setAttribute("FulfillmentType", "PICK_IN_STORE");
//					orderLineELe.setAttribute("LineType", "PICK_IN_STORE");
					changeOrderInp.setAttribute("EnteredBy", VSIConstants.COM_STORE_NO);
					changeOrderInp.setAttribute("OrderType", "WEB");
					String strShipNode = orderLineELe.getAttribute("ShipNode");
					if(!YRCPlatformUI.isVoid(strShipNode)){
						changeOrderInp.setAttribute("ShipNode", strShipNode);
					}
					Element eleSurroundingNodeList = VSIAlternateStoresExtnBehavior.returnSurroundingNodeList();
					if(!YRCPlatformUI.isVoid(eleSurroundingNodeList) && YRCPlatformUI.isVoid(elePersonInfoShipTo) ){

						if(!YRCPlatformUI.isVoid(strShipNode)){							
							NodeList ndlNodeList = eleSurroundingNodeList.getElementsByTagName("Node");
							for(int iNode = 0; iNode < ndlNodeList.getLength();iNode++){
								Element eleNode = (Element)ndlNodeList.item(iNode);
								String strSurroundingNode = eleNode.getAttribute("ShipNode");
								if(strShipNode.equalsIgnoreCase(strSurroundingNode)){
									Element eleShipNodeInfo = (Element)eleNode.getElementsByTagName("ShipNodePersonInfo").item(0);
									if(!YRCPlatformUI.isVoid(eleShipNodeInfo)){

										Element eleAdditionalAddress = YRCXmlUtils.createChild(orderLineELe, "PersonInfoShipTo");


										NamedNodeMap personInfoElemAttrs = eleShipNodeInfo.getAttributes();
										for(int h = 0 ; h<personInfoElemAttrs.getLength();h++) 
										{
											Attr a1 = (Attr)personInfoElemAttrs.item(h);
											eleAdditionalAddress.setAttribute(a1.getName(), a1.getValue());
										}	
									}
									break;
								}
							}
						}
					}else if(!YRCPlatformUI.isVoid(elePersonInfoShipTo) && "Y".equalsIgnoreCase(strIsNewItem)){
						Element elePersonShipTo = YRCXmlUtils.getChildElement(orderLineELe, "PersonInfoShipTo");
						if(YRCPlatformUI.isVoid(elePersonShipTo)){
							elePersonShipTo = YRCXmlUtils.createChild(orderLineELe, "PersonInfoShipTo");


							NamedNodeMap personInfoElemAttrs = elePersonInfoShipTo.getAttributes();
							for(int h = 0 ; h<personInfoElemAttrs.getLength();h++) 
							{
								Attr a1 = (Attr)personInfoElemAttrs.item(h);
								elePersonShipTo.setAttribute(a1.getName(), a1.getValue());
							}	 
						}
					}
				}

			}

			apiContext.setInputXml(changeOrderInp.getOwnerDocument());
		}else if(("changeOrderForPaymentMethod").equalsIgnoreCase(apiContext.getApiName())){
			Document inpDoc=apiContext.getInputXml();
			Element inputChangeOrder=inpDoc.getDocumentElement();	
			double dTotalAmountUsed = 0.0;
			if(!YRCPlatformUI.isVoid(inputChangeOrder)){
				inputChangeOrder.setAttribute("AllocationRuleID", "SYSTEM");
				inputChangeOrder.setAttribute("PaymentStatus", "AUTHORIZED");

				Document inDocForCCCheck = YRCXmlUtils.createDocument("PaymentMethods");
				Element eleIn = inDocForCCCheck.getDocumentElement();
				
				String strOrderNo = inputChangeOrder.getAttribute("OrderNo");
				
				
			
				NodeList ndlPaymentMethod = inputChangeOrder.getElementsByTagName("PaymentMethod");
				for(int i = 0; i < ndlPaymentMethod.getLength() ; i++){ 
					Element elePaymentMethod = (Element)ndlPaymentMethod.item(i);
					if(!YRCPlatformUI.isVoid(elePaymentMethod)){
						String strPaymentType = elePaymentMethod.getAttribute("PaymentType");
						if("CREDIT_CARD".equalsIgnoreCase(strPaymentType)){
							Element elePaymentMethodForCCCheck = YRCXmlUtils.createChild(eleIn, "PaymentMethod");
						
							
							elePaymentMethod.setAttribute("OrganizationCode", "DEFAULT");
							//elePaymentMethod.setAttribute("UnlimitedCharges", "Y");
							String strCCNo = elePaymentMethod.getAttribute("CreditCardNo");
							String strCCExpDate = elePaymentMethod.getAttribute("CreditCardExpDate");
							String strCCExpYear= "";
							strCCExpYear = strCCExpDate.substring(3, 7);
							String strCCExpMonth = "";
							strCCExpMonth = strCCExpDate.substring(0, 2);
							
							if(!YRCPlatformUI.isVoid(strCCNo))
								elePaymentMethod.setAttribute("PaymentReference2", strCCNo);
							if(!YRCPlatformUI.isVoid(strOrderNo))
								elePaymentMethodForCCCheck.setAttribute("OrderNo", strOrderNo);
							if(!YRCPlatformUI.isVoid(strCCNo))
								elePaymentMethodForCCCheck.setAttribute("Token", strCCNo);
							if(!YRCPlatformUI.isVoid(strCCExpMonth))
								elePaymentMethodForCCCheck.setAttribute("expiryMonth", strCCExpMonth);
							if(!YRCPlatformUI.isVoid(strCCExpYear))
								elePaymentMethodForCCCheck.setAttribute("expiryYear", strCCExpYear.substring(2, 4));
						}else if("GIFT_CARD".equalsIgnoreCase(strPaymentType) ){
							String strBalanceAmount = elePaymentMethod.getAttribute("PaymentReference3");
							String strMaxChargeLimit = elePaymentMethod.getAttribute("MaxChargeLimit");
							double dFinalBalance = 0.0;
							if(!YRCPlatformUI.isVoid(strBalanceAmount)){
								double dbalance = Double.parseDouble(strBalanceAmount);
								double dChargeLimit = Double.parseDouble(strMaxChargeLimit);
								 dFinalBalance = dbalance - dChargeLimit;
								
							}
							if(!YRCPlatformUI.isVoid(dFinalBalance))
								elePaymentMethod.setAttribute("PaymentReference3",String.valueOf(dFinalBalance));
						}else if("VOUCHERS".equalsIgnoreCase(strPaymentType) ){
							String strMaxChargeLimit = elePaymentMethod.getAttribute("MaxChargeLimit");
							Element eleModel = getModel("ReturnOrderDetails");
							if(!YRCPlatformUI.isVoid(eleModel)){
								Element eleOverallTotals = (Element)eleModel.getElementsByTagName("OverallTotals").item(0);
								if(!YRCPlatformUI.isVoid(eleOverallTotals)){
									String strGrandTotal = eleOverallTotals.getAttribute("GrandTotal");
									if(!YRCPlatformUI.isVoid(strGrandTotal)){
										double dTotal = Double.parseDouble(strGrandTotal);
										double dMaxChargeLimit = Double.parseDouble(strMaxChargeLimit);
										double dRemainingAmount = dTotal - dTotalAmountUsed;
										BigDecimal dExcess = new BigDecimal(dRemainingAmount);
										dExcess = dExcess.setScale(2,BigDecimal.ROUND_HALF_DOWN);
										dRemainingAmount = dExcess.doubleValue();
										if((dRemainingAmount) < dMaxChargeLimit){
											//elePaymentMethod.setAttribute("MaxChargeLimit",strGrandTotal);
											Element elePaymentDetail = inpDoc.createElement("PaymentDetails");
											elePaymentDetail.setAttribute("ChargeType", "CHARGE");
											elePaymentDetail.setAttribute("ProcessedAmount", String.valueOf(dRemainingAmount));
											elePaymentDetail.setAttribute("RequestAmount", String.valueOf(dRemainingAmount));
											elePaymentDetail.setAttribute("RequestProcessed", "Y");
											elePaymentMethod.appendChild(elePaymentDetail);
											dTotalAmountUsed +=  dRemainingAmount;
										}else{
											
												dTotalAmountUsed +=  dMaxChargeLimit;
												Element elePaymentDetail = inpDoc.createElement("PaymentDetails");
												elePaymentDetail.setAttribute("ChargeType", "CHARGE");
												elePaymentDetail.setAttribute("ProcessedAmount", strMaxChargeLimit);
												elePaymentDetail.setAttribute("RequestAmount", strMaxChargeLimit);
												elePaymentDetail.setAttribute("RequestProcessed", "Y");
												elePaymentMethod.appendChild(elePaymentDetail);
											
										}
									}
								}
							}
							elePaymentMethod.setAttribute("PaymentReference3","0.0");
						}
						
						

					}
				}
				NodeList ndlCCCheck = inDocForCCCheck.getElementsByTagName("PaymentMethod");
				if(ndlCCCheck.getLength() > 0){
					callCCVerifyService(inDocForCCCheck);
				}
			}	

		}else if (("getCompleteItemListForOrderingWithAccessories").equalsIgnoreCase(apiContext.getApiName()) 
				&&("com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDAdvancedAddItemPage".equalsIgnoreCase(strCurrentPage)) ){

			Document inpDoc=apiContext.getInputXml();
			//System.out.println("Printing Input XML : "+VSIXmlUtils.getElementXMLString(inpDoc.getDocumentElement()));
			Element itemEle = inpDoc.getDocumentElement();
			String strItemID= null;
			String strCallingOrgCode = itemEle.getAttribute("CallingOrganizationCode");
			if(itemEle != null &&  !strCallingOrgCode.equalsIgnoreCase("DEFAULT")){
				Element barCodeEle = (Element) itemEle.getElementsByTagName("BarCode").item(0);
				if(barCodeEle != null ){
					strItemID = barCodeEle.getAttribute("BarCodeData");
					if(strItemID != null){
						strItemID = strItemID.toUpperCase();
						Element complxQryEle = (Element) itemEle.getElementsByTagName("ComplexQuery").item(0);
						if(complxQryEle != null){
							complxQryEle.setAttribute("Operator", "AND");
							Node eleAND = complxQryEle.getElementsByTagName("And").item(0);
							complxQryEle.removeChild(eleAND);
							Element eleOR = inpDoc.createElement("Or");
							complxQryEle.appendChild(eleOR);
							Element eleExp = inpDoc.createElement("Exp");
							eleExp.setAttribute("Name", VSIConstants.A_ITEM_ID);
							eleExp.setAttribute("Value", strItemID);
							eleOR.appendChild(eleExp);
							eleExp = inpDoc.createElement("Exp");
							eleExp.setAttribute("Name", "Extn_ExtnActSkuID");
							eleExp.setAttribute("Value", strItemID);
							eleOR.appendChild(eleExp);
							complxQryEle.appendChild(eleAND);


						}
						itemEle.removeChild(barCodeEle);

					}
				}

			}
			//System.out.println("Printing Modified Input XML : "+VSIXmlUtils.getElementXMLString(inpDoc.getDocumentElement()));
		}



		return super.preCommand(apiContext);
	}

	@Override
	public void postSetModel(String arg0) { 
		// TODO Auto-generated method stub
		//String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();
		//if("OriginalOrder".equalsIgnoreCase(arg0) && "com.yantra.pca.ycd.rcp.tasks.orderEntry.wizardpages.YCDAdvancedAddItemPage".equalsIgnoreCase(strCurrentPage)){
			if("OriginalOrder".equalsIgnoreCase(arg0)){
			Element eleModel = getModel(arg0);
			if(!YRCPlatformUI.isVoid(eleModel)){
				NodeList ndlOrderLines = eleModel.getElementsByTagName("OrderLine");
				for(int i = 0 ; i < ndlOrderLines.getLength() ; i++ ){
					Element eleOrderLine = (Element)ndlOrderLines.item(i);
					if(!YRCPlatformUI.isVoid(eleOrderLine)){
						String strDeliveryMethod = eleOrderLine.getAttribute("DeliveryMethod");
						if("SHP".equalsIgnoreCase(strDeliveryMethod)){
							eleOrderLine.setAttribute("DeliveryMethod", "PICK");
						}
					}
				}
			}
			repopulateModel("OriginalOrder");
		}
		super.postSetModel(arg0);
	}


}