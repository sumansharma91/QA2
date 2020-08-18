	
package com.vsi.scc.oms.pca.extensions.payment;

/**
 * Created on Mar 10,2010
 *
 */
 
import java.util.ArrayList;
import java.util.HashSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author ssidharthan-tw
 * Copyright © 2006-2009 Sterling Commerce, Inc. All Rights Reserved.
 */
 public class VSIPaymentTypeEntryPanelExtnBehavior extends YRCExtentionBehavior{

	 String FORM_ID= "com.yantra.pca.ycd.rcp.tasks.payment.screens.YCDPaymentTypeEntryPanel";
	 public static String strCurrency = "";
	 public static String strEnterpriseCode = "";
	 HashSet<String> hPayTypeNoDisplay = new HashSet<String>();
	 
	/**
	 * This method initializes the behavior class.
	 */	 
	public void init() {
		Document docCommonCodeListInput =YRCXmlUtils.createFromString("<CommonCode />");
		Element eleCommonCodeListInput = docCommonCodeListInput.getDocumentElement();
		String strOrganizationCode="DEFAULT";
		eleCommonCodeListInput.setAttribute("CodeType","DONTDISPPAY");
		YRCApiContext context = new YRCApiContext();
		context.setApiName("getCommonCodeList");
		context.setFormId(FORM_ID);
		context.setInputXml(eleCommonCodeListInput.getOwnerDocument());
		callApi(context);
	}
 
	public void handleApiCompletion(YRCApiContext ctxApi) {
		
		if(ctxApi.getInvokeAPIStatus()<0){
//			System.out.println("Failed");
			
		} else if ("getCommonCodeList".equalsIgnoreCase(ctxApi.getApiName())) {
				Element eleOutputCommonCode = ctxApi.getOutputXml().getDocumentElement();
				NodeList nlOutputCommonCode = eleOutputCommonCode.getElementsByTagName("CommonCode");
    			for(int iCommnCode = 0; iCommnCode< nlOutputCommonCode.getLength();iCommnCode++){
					String strPayTypeToNotDisp = ((Element)nlOutputCommonCode.item(iCommnCode)).getAttribute("CodeValue");
					hPayTypeNoDisplay.add(strPayTypeToNotDisp);				
				}	
			    Element PaymentTypeListModel=getModel("getPaymentTypeList_output");
				   
			    if(!YRCPlatformUI.isVoid(PaymentTypeListModel)){
			    	NodeList nl = PaymentTypeListModel.getElementsByTagName("PaymentType");
			    	
			    	for(int k=0;k<nl.getLength();k++){
			    	
			    		Element paymentTypeElem = (Element)nl.item(k);
			    	
			    		if("Paypal".equalsIgnoreCase(paymentTypeElem.getAttribute("PaymentTypeDescription")))
			    		{
			    			PaymentTypeListModel.removeChild(paymentTypeElem);
			    		}
			    		if(hPayTypeNoDisplay.contains(paymentTypeElem.getAttribute("PaymentTypeDescription"))){
			    			PaymentTypeListModel.removeChild(paymentTypeElem);
			    		}
			    	}
			    	repopulateModel("getPaymentTypeList_output");
			    }
		} 
		// TODO Auto-generated method stub
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
	 
		public void postSetModel(String namespace) {
//			System.out.println(" namespace = "+namespace);
//			System.out.println(" input model = "+YRCXmlUtils.getString(getModel("input")));			
			if (namespace.equals("input")){
				Element inputModel=getModel("input");
				strCurrency = inputModel.getAttribute("Currency");
				strEnterpriseCode = inputModel.getAttribute("OrganizationCode");
			}
			if (namespace.equals("output")){
				//System.out.println("test1");
//				Element paymentListele = getModel("output");
//				setExtentionModel("ExtnPaymentTypeModel", paymentListele);
			}
			 if("getPaymentTypeList_output".equalsIgnoreCase(namespace)){
				    Element PaymentTypeListModel=getModel("getPaymentTypeList_output");
				   
				    if(!YRCPlatformUI.isVoid(PaymentTypeListModel)){
				    	NodeList nl = PaymentTypeListModel.getElementsByTagName("PaymentType");
				    	
				    	for(int k=0;k<nl.getLength();k++){
				    	
				    		Element paymentTypeElem = (Element)nl.item(k);
				    	
				    		if("Paypal".equalsIgnoreCase(paymentTypeElem.getAttribute("PaymentTypeDescription")))
				    		{
				    			PaymentTypeListModel.removeChild(paymentTypeElem);
				    		}
				    		if(hPayTypeNoDisplay.contains(paymentTypeElem.getAttribute("PaymentTypeDescription"))){
				    			PaymentTypeListModel.removeChild(paymentTypeElem);
				    		}
				    	}
				    	repopulateModel("getPaymentTypeList_output");
				    }
			   }
			
			
			//System.out.println(" namespace details = " +getXMLString(getModel(namespace).getOwnerDocument()));
			super.postSetModel(namespace);
		}
		
//		
//		public Element returnPaymentTypeEntryModel() {
//		Element elePayTypeModel=getExtentionModel("ExtnPaymentTypeModel");
//		return elePayTypeModel;
		
//    }
		
	    public String getCurrecy() {
	    	return strCurrency ;
		}
	    
	    public String getEnterpriceCode() {
			return strEnterpriseCode ;
	    }
	    
}