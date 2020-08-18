package com.vsi.scc.oms.pca.extensions.consumer.create;


import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.extensions.address.common.VSIAddressCaptureWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSIConsumerCreateWizardExtensionBehavior extends YRCWizardExtensionBehavior  {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.customerConsumerEntry.wizards.YCDConsumerEntryWizard";
	private static final String Consumer_Create_Form_ID="com.yantra.pca.ycd.rcp.tasks.customerConsumerEntry.wizardpages.YCDConsumerCustomerEntryWizardPage";
	
	String strCustomerID = "";
	Element eleCustomerCreated = null;
	ArrayList<String> mapStates = null;
	
	public void initPage(String pageBeingShown) {
				    	
	
		super.initPage(pageBeingShown);
	}
	

	//@Override
	public IYRCComposite createPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public void pageBeingDisposed(String arg0) {
		// TODO Auto-generated method stub

	}
	//@Override
	public YRCValidationResponse validateButtonClick(String fieldName) {
		if("btnConfirm".equalsIgnoreCase(fieldName) || "btnNext".equalsIgnoreCase(fieldName)){
			String strBillingPhone=getFieldValue("txtDayPhone");
			if(YRCPlatformUI.isVoid(strBillingPhone)){
				YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_Billing_Phone");
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Billing_Phone");
			}
			
			String strEmailID=getFieldValue("txtEmailID");
			if(YRCPlatformUI.isVoid(strEmailID)){
			   	YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_EmailID");
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_EmailID");
		   }
			ArrayList alist = getChildExtnBehaviors();
			for( int i = 0 ; i < alist.size() ; i++ ){
				VSIAddressCaptureWizardExtensionBehavior hbcAddressCaptureExtnBhvr= (VSIAddressCaptureWizardExtensionBehavior)alist.get(i);

				Element personInfoElement = hbcAddressCaptureExtnBhvr.getCurrentAddress();
				if(!YRCPlatformUI.isVoid(personInfoElement)){
					boolean blnIsSameAsShipping=YRCXmlUtils.getBooleanAttribute(personInfoElement, "SameAsShipping");
					if(!blnIsSameAsShipping){
						String strCity=personInfoElement.getAttribute("City");
						String strState=personInfoElement.getAttribute("State");
						String strZipCode=personInfoElement.getAttribute("ZipCode");
						String strCountry=personInfoElement.getAttribute("Country");
						String strAddressLine1=personInfoElement.getAttribute("AddressLine1");
						
						
						
						if(YRCPlatformUI.isVoid(strAddressLine1)){
						   	YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_AddressLine1");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_AddressLine1");
					   }
						if(YRCPlatformUI.isVoid(strState)){
						   	YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_State");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_State");
					   }
						if(YRCPlatformUI.isVoid(strCountry)){
							YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_Country");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Country");
						} 
						if(YRCPlatformUI.isVoid(strCity)){
							YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_City");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_City");
						} 
						if(YRCPlatformUI.isVoid(strZipCode)){
							YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_Zipcode");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_ZipCode");
						} 

						
						

					}

				}
			}
		}	
		// TODO Auto-generated method stub
		return super.validateButtonClick(fieldName);
	}
	
	private String  callErrorMsg(String strCountry,String strCity ,String strState ,String strZipCode ,String strCodeShortDesc) {
		String errorMsg = "";
		
		if(YRCPlatformUI.isVoid(strCity) && strCodeShortDesc.equalsIgnoreCase("C")){
			errorMsg = "Mandatory_Attribute_CS_City";
			
		}	
		if(YRCPlatformUI.isVoid(strState) && strCodeShortDesc.equalsIgnoreCase("S") ){
			errorMsg = "Mandatory_Attribute_CS_State";
			
		}
		
		if(!YRCPlatformUI.isVoid(strState)){			
			if (!mapStates.contains(strCountry.toUpperCase() + "^" + strState.toUpperCase()))
				errorMsg = "Mandatory_Attribute_US_Invalid_State";			
		}
		if(YRCPlatformUI.isVoid(strZipCode) && strCodeShortDesc.equalsIgnoreCase("Z") ){
			errorMsg = "Mandatory_Attribute_CS_Zip_Code";
			
		}
		return errorMsg;
	}
	
	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		
		
		
		// TODO Auto-generated method stub
		super.handleApiCompletion(ctxApi);
	}
	
	public void postCommand(YRCApiContext apiContext) {
		if(("manageCustomer").equalsIgnoreCase(apiContext.getApiName())){
			Element eleManageCustOutput = apiContext.getOutputXml().getDocumentElement();
			
			if(!YRCPlatformUI.isVoid(eleManageCustOutput)){
				 strCustomerID = eleManageCustOutput.getAttribute("CustomerID");
				 eleManageCustOutput.setAttribute("CustomerKey", strCustomerID);
			}
			eleCustomerCreated = eleManageCustOutput;
		}
		else if(("getCustomerDetails").equalsIgnoreCase(apiContext.getApiName())){
			Element eleCustDetailsInput = apiContext.getOutputXml().getDocumentElement();
		}
		
		super.postCommand(apiContext);
	}

	@Override
	public boolean preCommand(YRCApiContext apiContext) {
		if(("manageCustomer").equalsIgnoreCase(apiContext.getApiName())){
			//apiContext.setApiName("VSICreateCustomer");
			
			//Element eleManageCustInp = apiContext.getInputXml().getDocumentElement();

		}
		else if(("getCustomerDetails").equalsIgnoreCase(apiContext.getApiName())){
			//apiContext.setApiName("VSICreateCustomer");
			if(!YRCPlatformUI.isVoid(eleCustomerCreated)){
				eleCustomerCreated.setAttribute("CustomerKey", strCustomerID);
				apiContext.setInputXml(eleCustomerCreated.getOwnerDocument());
			}
				
		}
		
		return super.preCommand(apiContext);
	}
	
	
}
