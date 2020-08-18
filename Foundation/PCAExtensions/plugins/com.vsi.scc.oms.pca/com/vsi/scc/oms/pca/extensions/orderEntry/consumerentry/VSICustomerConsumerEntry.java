package com.vsi.scc.oms.pca.extensions.orderEntry.consumerentry;


import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.extensions.address.common.VSIAddressCaptureWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSICustomerConsumerEntry extends YRCWizardExtensionBehavior  {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.customerConsumerEntry.wizards.YCDOrderEntryConsumerEntryWizard";
	String strOrganizationCode="";
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
	
	@Override
	public boolean preCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		
		if(("manageCustomer").equalsIgnoreCase(arg0.getApiName())){

			
			Element manageCustomerInp = arg0.getInputXml().getDocumentElement();
			if(!YRCPlatformUI.isVoid(manageCustomerInp)){
				Element eleCustomerContact = (Element)manageCustomerInp.getElementsByTagName("CustomerContact").item(0);
				if(!YRCPlatformUI.isVoid(eleCustomerContact)){
					String strFirstName = eleCustomerContact.getAttribute("FirstName");
					String strLastName = eleCustomerContact.getAttribute("LastName");
					String strEmailID = eleCustomerContact.getAttribute("EmailID");
					String strDayPhone = eleCustomerContact.getAttribute("DayPhone");
					
					Element elePersonInfo = (Element)eleCustomerContact.getElementsByTagName("PersonInfo").item(0);
					if(!YRCPlatformUI.isVoid(elePersonInfo)){
						
						if(!YRCPlatformUI.isVoid(strFirstName))
							elePersonInfo.setAttribute("FirstName",strFirstName);
						if(!YRCPlatformUI.isVoid(strLastName))
							elePersonInfo.setAttribute("LastName",strLastName);
						if(!YRCPlatformUI.isVoid(strEmailID)){
							elePersonInfo.setAttribute("EmailID",strEmailID);
							elePersonInfo.setAttribute("EMailID",strEmailID);
							
						}
						if(!YRCPlatformUI.isVoid(strDayPhone))
							elePersonInfo.setAttribute("DayPhone",strDayPhone);
						
						
					}
					
					
				}
				arg0.setInputXml(manageCustomerInp.getOwnerDocument());
			}
			
			
		}
		return super.preCommand(arg0);
	}
	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		
		
		// TODO Auto-generated method stub
		super.handleApiCompletion(ctxApi);
	}
	

}
