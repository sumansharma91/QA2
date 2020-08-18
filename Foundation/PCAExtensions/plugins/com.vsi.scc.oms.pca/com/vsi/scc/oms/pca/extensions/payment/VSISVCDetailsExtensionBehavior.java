package com.vsi.scc.oms.pca.extensions.payment;

/**
 * Created on Aug 4,2014
 *
 */

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSISVCDetailsExtensionBehavior extends YRCExtentionBehavior {
	String FORM_ID = "com.yantra.pca.ycd.rcp.tasks.payment.screens.YCDSVCDetails";
	public String strCurrency = "";
	String strEnterpriseCode = "";
	String strOrderNo = "";
	String strOrderHeaderKey = "";
	String strSVCNo = "";
	String strExpYear = "";
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		// checking for permission to enable the view pan button
		if (YRCPlatformUI.hasPermission("ycdRCP000711")||YRCPlatformUI.hasPermission("SOPSOM0712")) {
			
		}

		super.init();
	}


	public void handleApiCompletion(YRCApiContext ctxApi) {
		String apiName = ctxApi.getApiName();
		 if ("VSICheckGCBalance"
				.equalsIgnoreCase(apiName)) {
			Element eleOutput = ctxApi.getOutputXml().getDocumentElement();
			if(!YRCPlatformUI.isVoid(eleOutput)){
				String strIsCallSuccessful = eleOutput.getAttribute("IsCallSuccesful");
				if("Y".equalsIgnoreCase(strIsCallSuccessful)){
					String strIsValid = eleOutput.getAttribute("IsValid");
					if(!YRCPlatformUI.isVoid(strIsValid) && "Y".equalsIgnoreCase(strIsValid) ){
						String strBalance = eleOutput.getAttribute("Balance");
						if(!YRCPlatformUI.isVoid(strBalance)){
							setFieldValue("txtMaxChargeLimit", strBalance);
							setFieldValue("extn_txtGiftCardBalance$", strBalance);
							setControlEditable("extn_txtGiftCardBalance$", false);
						}
					}else{
						YRCPlatformUI.showError("Error", "Invalid Gift Card");
					}
				}else{
					YRCPlatformUI.showError("Error", "Sorry, The Gift Card Engine is unreachable now, Please try again later");
				}
				
			}
			
		}
		super.handleApiCompletion(ctxApi);
	}


	public YRCValidationResponse validateButtonClick(String fieldName) {
		if(fieldName.equals("btnConfirm")){			
			
		} 

		return super.validateButtonClick(fieldName);
	}


	public YRCValidationResponse validateTextField(String strFieldName, String svalue) {
	// TODO Auto-generated method stub
		if(("txtSVCNo").equalsIgnoreCase(strFieldName)){			
			if(!YRCPlatformUI.isVoid(svalue)){
				callGCBalanceService(svalue);
			}
		} else if(("txtMaxChargeLimit").equalsIgnoreCase(strFieldName)){			
			
				
				if(!YRCPlatformUI.isVoid(svalue)){
					setFieldValue("extn_txtNothing", "GIFT_CARD"); 
				}
				
			
		}
		return super.validateTextField(strFieldName, svalue);
	}


	private void callGCBalanceService(String strCardNo) {
		Element eleVoucherBalanceInput = YRCXmlUtils.createFromString("<GiftCard  />").getDocumentElement();		
		eleVoucherBalanceInput.setAttribute("cardNumber",strCardNo);
		YRCApiContext context = new YRCApiContext();
		context.setApiName("VSICheckGCBalance");
		context.setFormId(FORM_ID);
		context.setInputXml(eleVoucherBalanceInput.getOwnerDocument());
		//context.setUserData("CodeName", strCodeType);
		callApi(context);
		
	}


	public void postSetModel(String arg0) {
		if(arg0.equals("input")){
			
//			System.out.println("HERE!!!");
		}else if("PaymentMethodList".equals(arg0)){ 
			Element payMethModel = getModel(arg0);
			NodeList paymethlist = payMethModel.getElementsByTagName("PaymentMethod"); 
			for (int a = 0; a < paymethlist.getLength(); a++) { 
				Element paymethele = (Element) paymethlist.item(a);
				if ("GIFT_CARD".equals(paymethele.getAttribute("PaymentType"))) { 
					double dFA = Double.parseDouble(paymethele.getAttribute("FundsAvailable"));
					if(dFA<=0){ 
						YRCPlatformUI.showError("Error", "Gift Card # "+paymethele.getAttribute("SvcNo")+" has zero balance, hence cannot use this card."); 
						payMethModel.removeChild(paymethele);
					} 
				}
			}
	}
		super.postSetModel(arg0);
	}


}