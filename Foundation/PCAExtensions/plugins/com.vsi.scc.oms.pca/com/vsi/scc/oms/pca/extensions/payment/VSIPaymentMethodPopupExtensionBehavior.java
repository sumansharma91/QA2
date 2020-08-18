package com.vsi.scc.oms.pca.extensions.payment;


import java.util.ArrayList;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author skiran
 */

public class VSIPaymentMethodPopupExtensionBehavior extends YRCExtentionBehavior {
	String FORM_ID = "com.yantra.pca.ycd.rcp.tasks.customerPaymentManagement.screens.YCDPaymentMethodPopup";
 public static boolean isCardValid = true;
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		

		super.init();
	}


	public void handleApiCompletion(YRCApiContext ctxApi) {
		if (ctxApi.getInvokeAPIStatus() < 0) {
			YRCPlatformUI.showError("API call Failed", "API call Failed");
		} else if ("VSIVerifyCreditCard".equals((String) ctxApi
				.getApiName())) {
			isCardValid = true;
			Element eleOutput = ctxApi.getOutputXml().getDocumentElement();
			if(!YRCPlatformUI.isVoid(eleOutput)){
				Element elePaymentMethod = (Element)eleOutput.getElementsByTagName("PaymentMethod").item(0);
				if(!YRCPlatformUI.isVoid(elePaymentMethod)){
					String strIsCallSuccessful = elePaymentMethod.getAttribute("IsCallSuccesful");
					if("Y".equalsIgnoreCase(strIsCallSuccessful)){
						String strIsCardValid = elePaymentMethod.getAttribute("IsValid");
						if("N".equalsIgnoreCase(strIsCardValid)){
							isCardValid = false;						
						}else{
							isCardValid = true;
						}
					}else{
						isCardValid = false;
					}
				}
			}
			
		}
		super.handleApiCompletion(ctxApi);
	}


	public YRCValidationResponse validateButtonClick(String fieldName) {
		if(fieldName.equals("btnConfirm")){

			//Element eleCardDetails = VSICreditCardDetailsExtensionBehavior.returnCardDetails();
			ArrayList childExtnBehaviors = getChildExtnBehaviors();
			for (int i = 0; i < childExtnBehaviors.size(); i++) {
				ArrayList childExtnBehaviors1 = ((YRCExtentionBehavior) childExtnBehaviors
						.get(i)).getChildExtnBehaviors();
				for (int j = 0; j < childExtnBehaviors1.size(); j++) {
					if (childExtnBehaviors1.get(j) instanceof VSICreditCardDetailsExtensionBehavior) {	
						VSICreditCardDetailsExtensionBehavior extnbehvior = (VSICreditCardDetailsExtensionBehavior) childExtnBehaviors1
						.get(j);
						//String strOrderHeaderKey = null;
						String strOrderNo = null;						  
						IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();					    
						for (int l = 0; l < editorReferences.length; l++) {
							try {
								IEditorInput tEditorInput = editorReferences[l].getEditorInput();                
								Element orderEntryEditorInput = ((YRCEditorInput) tEditorInput).getXml();       
								//strOrderHeaderKey = orderEntryEditorInput.getAttribute("OrderHeaderKey");
								strOrderNo=orderEntryEditorInput.getAttribute("OrderNo");
							} catch (Exception ex) {}
						}

						String strCCNo = extnbehvior
						.getFieldValue("txtCreditCardNo");
						String strCCExpMonth = extnbehvior
						.getFieldValue("cmbCreditCardExpMonth");
						String strCCExpYear = extnbehvior
						.getFieldValue("cmbCreditCardExpYear");
						if(!YRCPlatformUI.isVoid(strCCNo)){
							Document inDoc = YRCXmlUtils.createDocument("PaymentMethods");
							Element eleIn = inDoc.getDocumentElement();
							Element elePaymentMethod = YRCXmlUtils.createChild(eleIn, "PaymentMethod");
							if(!YRCPlatformUI.isVoid(strOrderNo))
								elePaymentMethod.setAttribute("OrderNo", strOrderNo);
							if(!YRCPlatformUI.isVoid(strCCNo))
								elePaymentMethod.setAttribute("Token", strCCNo);
							if(!YRCPlatformUI.isVoid(strCCExpMonth))
								elePaymentMethod.setAttribute("expiryMonth", strCCExpMonth);
							if(!YRCPlatformUI.isVoid(strCCExpYear))
								elePaymentMethod.setAttribute("expiryYear", strCCExpYear.substring(2, 4));
							
							elePaymentMethod.setAttribute("OrganizationCode", "DEFAULT");
							
							callCCVerifyService(inDoc);
						}
					}
				}
			}

		} 

		return super.validateButtonClick(fieldName);
	}


	private void callCCVerifyService(Document inDoc) {
		
		YRCApiContext context = new YRCApiContext();
		context.setApiName("VSIVerifyCreditCard");
		context.setFormId(FORM_ID);
		context.setInputXml(inDoc);
		context.setShowError(true);
		callApi(context);		

	}


	public boolean preCommand(YRCApiContext apiContext) {


		return super.preCommand(apiContext);
	}

//public static boolean returnCardValidity() {
//		
//		boolean isCardValid = isCardValid;
//		return eleCustomerDetails;
//	}
	

}