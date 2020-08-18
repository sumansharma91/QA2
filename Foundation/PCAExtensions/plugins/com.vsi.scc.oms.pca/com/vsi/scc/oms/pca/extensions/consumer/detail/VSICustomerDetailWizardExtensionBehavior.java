package com.vsi.scc.oms.pca.extensions.consumer.detail;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSICustomerDetailWizardExtensionBehavior extends YRCWizardExtensionBehavior  {
	
	String strOrganizationCode="";
	String strCustomerID="";
	String strCountry = "";
	String strCountryCode = "";
	public static Element eleCustomerCreated = null;
	private static final String Wizard_Id ="com.yantra.pca.ycd.rcp.tasks.customerDetails.wizards.YCDCustomerWizard";
	
	public void initPage(String pageBeingShown) {
		super.initPage(pageBeingShown);
	}
	

	public VSICustomerDetailWizardExtensionBehavior() throws Exception{
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

	

	@Override
	public void postCommand(YRCApiContext arg0) {
		if(arg0.getApiName().equals("getCustomerDetails")){
			setDirty(false);
			 Element eleOutput=arg0.getOutputXml().getDocumentElement();
			 eleOutput.setAttribute("OrganizationCode","VSI.com");
				// outdoc =  XMLUtil.getDocumentFromString(custString);
			 //Element eleCustomer = (Element)eleOutput.getElementsByTagName("Customer").item(0);
			
			 Element eleAdditionalAddressList=(Element)eleOutput.getElementsByTagName("CustomerAdditionalAddressList").item(0);
			 Element eleAdditionalAddress = (Element)eleAdditionalAddressList.getElementsByTagName("CustomerAdditionalAddress").item(0);

			 eleAdditionalAddress.setAttribute("IsDefaultBillTo", "Y");

			 String strCustomer = YRCXmlUtils.getString(eleOutput);
			 
			 Document inDoc = YRCXmlUtils.createFromString(strCustomer);
			 eleCustomerCreated = inDoc.getDocumentElement();
			// arg0.setOutputXml(inDoc);
		}
		super.postCommand(arg0);
	} 
	
	@Override
	public boolean preCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if((arg0.getApiName()).equals("getCustomerDetails")){
//		    Element eleInput=arg0.getInputXml().getDocumentElement();
//			//String strAddressLine1=getFieldValue("extn_txtAddressLineStartsWith");
//			String strHealthyAwardsNo = eleInput.getAttribute("CustomerID");
//			eleInput.setAttribute("OrganizationCode","VSI.com");
//			Element eleCustomerContactList=YRCXmlUtils.getChildElement(eleInput, "CustomerContactList", true);
//			Element eleCustomerContact=YRCXmlUtils.getChildElement(eleCustomerContactList, "CustomerContact", true);
//			Element eleAdditionalAddressList=YRCXmlUtils.getChildElement(eleCustomerContact, "CustomerAdditionalAddressList",true);
//			Element eleAdditionalAddress=YRCXmlUtils.getChildElement(eleAdditionalAddressList, "CustomerAdditionalAddress",true);
//			Element elePersonInfo=YRCXmlUtils.getChildElement(eleAdditionalAddress, "PersonInfo",true);
//			eleInput.removeAttribute("CustomerID");
//			if(!YRCPlatformUI.isVoid(strHealthyAwardsNo)){
//				eleCustomerContact.setAttribute("HealthyAwardsNo", strHealthyAwardsNo);
//			}else {
//				eleCustomerContact.setAttribute("HealthyAwardsNo", "");
//			}
		}
		
		 
		return super.preCommand(arg0);
	}
	
	@Override
	public void init() {
		super.init();
	}	
	
	public static Element returnCustomerDetails() {
		
		Element eleCustomerDetails = eleCustomerCreated;
		return eleCustomerDetails;
	}
}
