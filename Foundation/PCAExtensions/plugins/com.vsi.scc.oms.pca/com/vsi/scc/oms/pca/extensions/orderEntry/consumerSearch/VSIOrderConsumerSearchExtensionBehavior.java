package com.vsi.scc.oms.pca.extensions.orderEntry.consumerSearch;


import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPaginationData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSIOrderConsumerSearchExtensionBehavior extends YRCWizardExtensionBehavior  {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.orderEntry.screens.YCDCustomerSearchPage";
	String strOrganizationCode="";
	ArrayList<String> mapStates = null;
	public static Element eleCustomerCreated = null;
	
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


	public void handleApiCompletion(YRCApiContext ctxApi) {

		// TODO Auto-generated method stub
		super.handleApiCompletion(ctxApi);
	} 

	public boolean preCommand(YRCApiContext arg0) {
//		TODO Auto-generated method stub
		if((arg0.getApiName()).equals("getCustomerList")){
			Element eleInput=arg0.getInputXml().getDocumentElement();	
			//System.out.println("I am in Precommand"+VSIXmlUtils.getElementXMLString(eleInput));
			if(getPaginationData() != null){
				   getPaginationData().trySetPaginationStrategy(YRCPaginationData.YRC_RESULTSET_PAGINATION_STRATEGY);
				   }
			String strCustomerID = eleInput.getAttribute("CustomerID"); 
			if(YRCPlatformUI.isVoid(strCustomerID)){
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
			}else {			
			
					
//				Element eleCustomerDetails = VSICustomerDetailWizardExtensionBehavior.returnCustomerDetails();
//								
//				if(!YRCPlatformUI.isVoid(eleCustomerDetails)){
//					eleCustomerDetails.setAttribute("CustomerKey", strCustomerID);
//					
//					arg0.setInputXml(eleCustomerDetails.getOwnerDocument());
//				}
				
			}
			
		}else if (arg0.getApiName().equals("createOrder")){
						
 		    Element ele1=arg0.getInputXml().getDocumentElement();
 		    String strEnterpriseCode = ele1.getAttribute("EnterpriseCode");
 		    ele1.setAttribute("BuyerOrganizationCode", strEnterpriseCode);
		}
		return super.preCommand(arg0);
	}

	@Override
	public void postCommand(YRCApiContext arg0) {
//		TODO Auto-generated method stub
		if((arg0.getApiName()).equals("getCustomerList")){
			Element eleOutput=arg0.getOutputXml().getDocumentElement();
			Element customerEle = (Element)eleOutput.getElementsByTagName("Customer").item(0);
            if(!YRCPlatformUI.isVoid(customerEle)){
                String strCustomerAvailable=customerEle.getAttribute("IsCustomerAvailable");
			if(!YRCPlatformUI.isVoid(strCustomerAvailable)){
                YRCPlatformUI.showError("Error","CRM Internal Error, Please Try Again");
          }
            
          else {
			
			Document outdoc = YRCXmlUtils.createDocument("CustomerList");
         
			NodeList ndlCustomer = eleOutput.getElementsByTagName("Customer");
			for(int i = 0; i < ndlCustomer.getLength(); i++){
				Element eleCustomer = (Element)ndlCustomer.item(i);
				if(!YRCPlatformUI.isVoid(eleCustomer)){
					eleCustomer.setAttribute("CustomerType", "02");
					Element eleAdditionalAddressList=(Element)eleCustomer.getElementsByTagName("CustomerAdditionalAddressList").item(0);
					 Element eleAdditionalAddress = (Element)eleAdditionalAddressList.getElementsByTagName("CustomerAdditionalAddress").item(0);

					 eleAdditionalAddress.setAttribute("IsDefaultBillTo", "Y");

					
					//YRCXmlUtils.importElement(outdoc.getDocumentElement(), eleCustomer);
				}
			}

			//arg0.setOutputXml(outdoc);

		}
		}
		}
		super.postCommand(arg0);
	}
	
@Override
public void postSetModel(String arg0) {
	// TODO Auto-generated method stub
	if("selectedCustomer".equalsIgnoreCase(arg0)){
		eleCustomerCreated = getModel("selectedCustomer");
	}
	super.postSetModel(arg0);
}
	
	public static Element returnCustomerDetails() {
		
		Element eleCustomerDetails = eleCustomerCreated;
		return eleCustomerDetails;
	}
}
