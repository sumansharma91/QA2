	
package com.vsi.scc.oms.pca.extensions.payment;

 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * 
 */
public class VSIChangePaymentMethodExtensionBehavior extends YRCWizardExtensionBehavior {

	
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

		
		if(("changeOrder").equalsIgnoreCase(apiContext.getApiName())){
			Document inpDoc=apiContext.getInputXml();
			Element inputChangeOrder=inpDoc.getDocumentElement();	
			if(!YRCPlatformUI.isVoid(inputChangeOrder)){
				
				
				//inputChangeOrder.setAttribute("AllocationRuleID", "SYSTEM");
				inputChangeOrder.setAttribute("PaymentStatus", "AUTHORIZED");

				Document inDocForCCCheck = YRCXmlUtils.createDocument("PaymentMethods");
				Element eleIn = inDocForCCCheck.getDocumentElement();
				
				String strOrderNo = inputChangeOrder.getAttribute("OrderNo");
				
				
				
			
				NodeList ndlPaymentMethod = inputChangeOrder.getElementsByTagName("PaymentMethod");
				for(int i = 0; i < ndlPaymentMethod.getLength() ; i++){ 
					Element elePaymentMethod = (Element)ndlPaymentMethod.item(i);
					String strNewPaymentMethod = elePaymentMethod.getAttribute("NewPaymentMethod");
					if(!YRCPlatformUI.isVoid(elePaymentMethod) && "Y".equalsIgnoreCase(strNewPaymentMethod)){
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
						}
						

					}
				}
				NodeList ndlCCCheck = inDocForCCCheck.getElementsByTagName("PaymentMethod");
				if(ndlCCCheck.getLength() > 0){
					callCCVerifyService(inDocForCCCheck);
				}
			}	

		}



		return super.preCommand(apiContext);
	}

	@Override
	public void postSetModel(String arg0) { 
		
		super.postSetModel(arg0);
	}


}