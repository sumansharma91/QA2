package com.vsi.scc.oms.pca.extensions.itemSearch;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.extensions.consumer.detail.VSICustomerDetailWizardExtensionBehavior;
import com.vsi.scc.oms.pca.extensions.orderEntry.consumerSearch.VSIOrderConsumerSearchExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;


public class VSIItemSearchWizardExtensionBehavior extends YRCWizardExtensionBehavior{
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.itemSearch.wizards.YCDItemSearchWizard";


//	
	public IYRCComposite createPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void initPage(String arg0) {
     
		super.initPage(arg0);
	}
		
	
//	
	public void pageBeingDisposed(String arg0) {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * This method is added to remove the repition of availability Element
	 */
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		
		
	}
	
	public boolean preCommand(YRCApiContext apiContext) {
// TODO Auto-generated method stub
		if((apiContext.getApiName()).equals("getCustInfo")){
			Element eleInput = apiContext.getInputXml().getDocumentElement();
			String strCustomerID = eleInput.getAttribute("CustomerID");
			if(!YRCPlatformUI.isVoid(strCustomerID)){
				Element eleCustomerDetails = VSIOrderConsumerSearchExtensionBehavior.returnCustomerDetails();
				
				if(!YRCPlatformUI.isVoid(eleCustomerDetails)){
					eleCustomerDetails.setAttribute("CustomerKey", strCustomerID);
					String strCustomer = YRCXmlUtils.getString(eleCustomerDetails);
					 
					 Document inDoc = YRCXmlUtils.createFromString(strCustomer);
					apiContext.setInputXml(inDoc);
				}
			}
		}
	 	return super.preCommand(apiContext);
	}
	
	
	
	
	public void postCommand(YRCApiContext apiContext) {
		if((apiContext.getApiName()).equals("getCustInfo")){
			Element eleCustDetailsInput = apiContext.getOutputXml().getDocumentElement();
			 Document oudoc = YRCXmlUtils.createDocument("CustomerList");
			 YRCXmlUtils.importElement(oudoc.getDocumentElement(), eleCustDetailsInput);
			 apiContext.setOutputXml(oudoc);
		}
		if((apiContext.getApiName()).equals("GetItemListForOrderingNoAvail")){
			Element eleCustDetailsInput = apiContext.getOutputXml().getDocumentElement();
			
			eleCustDetailsInput.setAttribute("CallingOrganizationCode", "VSI-Cat"); 
		}
			
		super.postCommand(apiContext);
	}

	
//	
	public YRCValidationResponse validateButtonClick(String arg0) {
		return super.validateButtonClick(arg0);
	}
	//
	public YRCValidationResponse validateLinkClick(String fieldName) {
	
		return super.validateLinkClick(fieldName);
	}

	
	public void validateComboField(String arg0, String arg1) {
		 if(arg0.equals("comboOrganization")){
//			System.out.println("Here");
			if(!YRCPlatformUI.isVoid(arg1)){
				repopulateSearchCriteria("",arg1);
				
			}
		}
		super.validateComboField(arg0, arg1);
	}
	
	private void repopulateSearchCriteria(String strCurrency, String strBrand){
		Element elemModel = getModel("SearchCriteria");
	
		
			if(!YRCPlatformUI.isVoid(elemModel)){
				elemModel.setAttribute("CallingOrganizationCode", VSIConstants.V_ORG_VSI_COM);
			} else {
				YRCXmlUtils.createFromString("<Item CallingOrganizationCode='"+VSIConstants.V_ORG_VSI_COM+"'/>");
			}
		
		repopulateModel("SearchCriteria");
	}
	
	

}
