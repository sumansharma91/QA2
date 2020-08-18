package com.vsi.scc.oms.pca.extensions.consumer.search;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPaginationData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSICustomerSearchExtensionBehavior extends YRCWizardExtensionBehavior  {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.customerSearch.wizards.YCDCustomerSearchWizard";
	public void init() {
		

		super.init();
	}
	public void initPage(String pageBeingShown) {
		if(getPaginationData() != null){
			   getPaginationData().trySetPaginationStrategy(YRCPaginationData.YRC_RESULTSET_PAGINATION_STRATEGY);
			   }

				
		super.initPage(pageBeingShown);
	}
	
	
	//Document inDoc = null;
	public VSICustomerSearchExtensionBehavior() throws Exception{
		//inDoc = XMLUtil.createDocument("Shipment");	
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
		
		
		

		// TODO Auto-generated method stub
		return super.validateButtonClick(fieldName);
	}
	
	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		
				// TODO Auto-generated method stub
		super.handleApiCompletion(ctxApi);
	}
	
	public boolean preCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		if((arg0.getApiName()).equals("getCustomerList")){
		    Element eleInput=arg0.getInputXml().getDocumentElement();
			String strAddressLine1=getFieldValue("extn_txtAddressLineStartsWith");
			String strHealthyAwardsNo = getFieldValue("txtUserID");
			String strFirstName=getFieldValue("txtFirstName");
			String strLastName=getFieldValue("txtLastName");
			String strZipCode=getFieldValue("txtZipCode");
			Element eleCustomerContactList=YRCXmlUtils.getChildElement(eleInput, "CustomerContactList", true);
			Element eleCustomerContact=YRCXmlUtils.getChildElement(eleCustomerContactList, "CustomerContact", true);
			Element eleAdditionalAddressList=YRCXmlUtils.createChild(eleCustomerContact, "CustomerAdditionalAddressList");
			Element eleAdditionalAddress=YRCXmlUtils.createChild(eleAdditionalAddressList, "CustomerAdditionalAddress");
			Element elePersonInfo=YRCXmlUtils.createChild(eleAdditionalAddress, "PersonInfo");
			if(!YRCPlatformUI.isVoid(strAddressLine1)){
				elePersonInfo.setAttribute("AddressLine1QryType", "FLIKE");
				elePersonInfo.setAttribute("AddressLine1", strAddressLine1);
			}
			if(!YRCPlatformUI.isVoid(strFirstName)){
				eleCustomerContact.setAttribute("FirstNameQryType", "FLIKE");
			}
			if(!YRCPlatformUI.isVoid(strLastName)){
				eleCustomerContact.setAttribute("LastNameQryType", "FLIKE");
			}
			if(!YRCPlatformUI.isVoid(strZipCode)){
				eleCustomerContact.setAttribute("ZipCodeQryType", "FLIKE");
			}
			if(!YRCPlatformUI.isVoid(strHealthyAwardsNo)){
				eleCustomerContact.setAttribute("HealthyAwardsNo", strHealthyAwardsNo);
			}else {
				eleCustomerContact.setAttribute("HealthyAwardsNo", "");
			}
		}
		return super.preCommand(arg0);
	}
	
	@Override
	public void postCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		if((arg0.getApiName()).equals("getCustomerList")){
			 Element eleOutput=arg0.getOutputXml().getDocumentElement();
			 String connectivityErr=eleOutput.getAttribute("IsCallSuccesful");
			 String IsCustomerAvailable=eleOutput.getAttribute("IsCustomerAvailable");

			 if(!YRCPlatformUI.isVoid(connectivityErr)){
				 YRCPlatformUI.showError("Error","CRM Connectivity Error");
			 }
			
			else if(!YRCPlatformUI.isVoid(IsCustomerAvailable)){
				 YRCPlatformUI.showError("Error","CRM Internal Error");
			 }
			else  if(!YRCPlatformUI.isVoid(eleOutput)){

			 Document oudoc = YRCXmlUtils.createDocument("CustomerList");
			 
			 NodeList ndlCustomer = eleOutput.getElementsByTagName("Customer");
			 for(int i = 0; i < ndlCustomer.getLength(); i++){
				 Element eleCustomer = (Element)ndlCustomer.item(i);
				 if(!YRCPlatformUI.isVoid(eleCustomer)){
					 
				  eleCustomer.setAttribute("CustomerType", "02");
				
					// YRCXmlUtils.importElement(oudoc.getDocumentElement(), eleCustomer);
				 }
			 }
					 
				//arg0.setOutputXml(oudoc);
						 }
						 else{
							 YRCPlatformUI.showError("Error","CRM System Not Available");
						 }
		}
		super.postCommand(arg0);
	}
		
}
//TODO Validation required for a Button control: btnSearch