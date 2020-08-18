package com.vsi.scc.oms.pca.extensions.address.common;


import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;

public class VSIAddressCaptureWizardExtensionBehavior extends YRCWizardExtensionBehavior  {
private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.addressCapture.wizards.YCDAddressCaptureWizard";
	
	Document inDoc = null;
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
//	@Override
	public YRCValidationResponse validateButtonClick(String fieldName) {		
		if("btnConfirm".equalsIgnoreCase(fieldName)){
			String strBillingPhone=getFieldValue("txtDayTimePhone");
			String strFirstName = getFieldValue("txtFirstName");
	 		String strLastName = getFieldValue("txtLastName");
	 		
	 		if(YRCPlatformUI.isVoid(strFirstName)){
				YRCPlatformUI.showError("Error","Mandatory_Attribute_FirstName");
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_FirstName");
			} 
	 		if(YRCPlatformUI.isVoid(strLastName)){
				YRCPlatformUI.showError("Error","Mandatory_Attribute_LastName");
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_LastName");
			} 
			if(YRCPlatformUI.isVoid(strBillingPhone)){
				YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_Billing_Phone");
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Billing_Phone");
			} else {
				 		Element addressElem = getCurrentAddress();
				 		
				        String strCity = addressElem.getAttribute("City");
						String strState = addressElem.getAttribute("State");
						String strZipCode = addressElem.getAttribute("ZipCode");
						String strCountry = addressElem.getAttribute("Country");
						
						if(validateTextField("txtEveningPhone","CONFIRM,"+getFieldValue("txtEveningPhone")+","+strCountry).getStatusCode() == 3){
							YRCPlatformUI.showError("Error","Billing_Phone_For_US_Addresses_xxx_xxx_xxxx");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Billing_Phone");
						}
						if(validateTextField("txtDayTimePhone","CONFIRM,"+getFieldValue("txtDayTimePhone")+","+strCountry).getStatusCode() == 3){
							YRCPlatformUI.showError("Error","Billing_Phone_For_US_Addresses_xxx_xxx_xxxx");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Billing_Phone");
						}
						
					   if(YRCPlatformUI.isVoid(strState)){
						   	YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_State");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_State");
					   }
						
				       if(YRCPlatformUI.isVoid(strCountry)){
							YRCPlatformUI.showError("Error","Mandatory_Attribute_CS_Country");
							return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CS_Country");
						} else {
							Element getCommonCodeList = getExtentionModel("ExtnAddressVerifyCommonCodeList");
							
							NodeList commonCodeList = getCommonCodeList.getElementsByTagName("CommonCode");
							
						 	if (strCountry.toUpperCase().equalsIgnoreCase("CA")){
								if(!YRCPlatformUI.isVoid(strState)){			
									if (!mapStates.contains(strCountry.toUpperCase() + "^" + strState.toUpperCase())){
										YRCPlatformUI.showError("Error","Mandatory_Attribute_CA_Invalid_State");
										return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Mandatory_Attribute_CA_Invalid_State");
									}
								}
							}
							
							if( commonCodeList.getLength() != 0 ) {
								for(int j=0;j<commonCodeList.getLength();j++){
									Element commonCodeEle = (Element)commonCodeList.item(j);
									String strCodeValue = commonCodeEle.getAttribute("CodeValue");
									String strCodeShortDesc = commonCodeEle.getAttribute("CodeShortDescription");
									
									if( strCountry.equalsIgnoreCase(strCodeValue)) {
										String[] cSZValue = strCodeShortDesc.split(",");
										if( cSZValue.length == 1){
											String errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[0]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											
										}
										if( cSZValue.length == 2){
											String errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[0]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[1]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											
										}
										if( cSZValue.length == 3){
											String errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[0]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[1]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											errorMsg = callErrorMsg(strCountry,strCity,strState,strZipCode,cSZValue[2]);
											if( errorMsg.trim().length() != 0) {
												YRCPlatformUI.showError("Error",errorMsg);
												return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,errorMsg);
											}
											
										}
										
									}
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
		
		super.handleApiCompletion(ctxApi);
	}
	
	

	@Override
	public void postCommand(YRCApiContext arg0) {
		
		
		super.postCommand(arg0);
	}

	public boolean preCommand(YRCApiContext arg0) {
		inDoc = arg0.getInputXml();
		return super.preCommand(arg0);
	}

	public YRCValidationResponse validateTextField(String arg0, String arg1) {
		
		return super.validateTextField(arg0, arg1);
	}
	
	public Element getCurrentAddress() {
		
		Element toReturn = getTargetModel("PersonInfoTarget");
		return toReturn;
	}

}
