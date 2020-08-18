	
package com.vsi.scc.oms.pca.extensions.quickAccess;

/**
 * Created on Dec 30,2013
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIPcaUtils;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopApplication;
import com.yantra.yfc.rcp.YRCPaginationData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIQuickAccessWizExtnBehavior extends YRCWizardExtensionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
	
	@Override
	public void postCommand(YRCApiContext apiContext) {

		// get the api name.
		final String strApiName = apiContext.getApiName();
		
		

		// check if api invoked is getOrderList.
		if(VSIApiNames.API_GET_ORDER_LIST.equalsIgnoreCase(strApiName)) {

			// get the output document.
			final Document docOutput = apiContext.getOutputXml();
			
			Element eleOrderListSearchResult=docOutput.getDocumentElement();
			
			if(!YRCPlatformUI.isVoid(eleOrderListSearchResult)){
				
				NodeList nlOrderList= eleOrderListSearchResult.getElementsByTagName(VSIConstants.E_ORDER);
				
				if(!YRCPlatformUI.isVoid(nlOrderList)){
					
					for(int i=0; i < nlOrderList.getLength(); i++){
						
						Element eleOrder=(Element)nlOrderList.item(i);
						
						if(!YRCPlatformUI.isVoid(eleOrder)){
							
							NodeList nlOrderLineList= eleOrder.getElementsByTagName(VSIConstants.E_ORDER_LINE);
							
							if(!YRCPlatformUI.isVoid(nlOrderLineList) && nlOrderList.getLength() > 0 ){
									
									Element eleOrderLine=(Element)nlOrderLineList.item(0);
									
									String strCustomOrderType=eleOrder.getAttribute(VSIConstants.A_ORDER_TYPE);
									
									if(!YRCPlatformUI.isVoid(strCustomOrderType)){
										
										String strCustomLineType=eleOrderLine.getAttribute(VSIConstants.A_LINE_TYPE);
										
										if(!YRCPlatformUI.isVoid(strCustomLineType)){
											
											strCustomOrderType=strCustomOrderType+VSIConstants.DELIMITER_DASH+strCustomLineType;
											
										}
										
										eleOrder.setAttribute(VSIConstants.A_CUSTOM_ORDER_TYPE, strCustomOrderType);
									}
									
									
								}
								
							}
							
							
						}
						
					}
					
				}
			
			apiContext.setOutputXml(docOutput);
				
			}
		
		if(strApiName.equals("getCustomerList")){
			Element eleOutput=apiContext.getOutputXml().getDocumentElement();
if(eleOutput != null && eleOutput.hasAttribute("CustomerKey") && eleOutput.getAttribute("CustomerKey") !=null ){
	Document outdoc = YRCXmlUtils.createDocument("CustomerList");
	Element rootEle = outdoc.getDocumentElement();
	Node cpyNode = outdoc.importNode(eleOutput, true);
	rootEle.appendChild(cpyNode);
	apiContext.setOutputXml(outdoc);
}
			
			

		}
			

		}
 
 	@Override
 	public boolean preCommand(YRCApiContext apiContext) {
 		// TODO Auto-generated method stub
 		String strApiName = apiContext.getApiName();
		//System.out.println("API: "+strApiName);
		String appID = YRCDesktopApplication.getInstance().getApplicationID();
		if(appID.equalsIgnoreCase("YFSSYS00006") && "getOrderList".equalsIgnoreCase(strApiName)) {
			Document inpDoc=apiContext.getInputXml();
			Element ordEle = inpDoc.getDocumentElement();
			if(ordEle.hasAttribute("OrderNo") && ordEle.getAttribute("OrderNo") != null){
				String custPONo = ordEle.getAttribute("OrderNo");
				ordEle.removeAttribute("OrderNo");
				Element ordLineEle = inpDoc.createElement("OrderLine");
				ordEle.appendChild(ordLineEle);
				ordLineEle.setAttribute("CustomerPONo", custPONo);
			}
		}
		
		//Logic for fetching Order header key using PaymentTech Order # and Tracking #
		if(appID.equalsIgnoreCase("YFSSYS00011") && "getOrderList".equalsIgnoreCase(strApiName)) {
			Document inDoc=apiContext.getInputXml();
			Element ordEle = inDoc.getDocumentElement();
			if(ordEle.hasAttribute("PaymentTechOrderId") && ordEle.getAttribute("PaymentTechOrderId") != null){
				String payTechOrderNo = ordEle.getAttribute("PaymentTechOrderId");

				ordEle.setAttribute("OrderHeaderKey","Dummy");
				if (20 == payTechOrderNo.length()) {
					Document docAJBSettlement = YRCXmlUtils.createDocument("AJBSettlement");
					Element eleAJBSettlement = docAJBSettlement.getDocumentElement();
					Element eleComplexQuery = docAJBSettlement.createElement("ComplexQuery");
					Element eleAND = docAJBSettlement.createElement("And");
					Element eleExp1 = docAJBSettlement.createElement("Exp");
					Element eleExp2 = docAJBSettlement.createElement("Exp");
					Element eleExp3 = docAJBSettlement.createElement("Exp");
					Element eleExp4 = docAJBSettlement.createElement("Exp");
					/* Added eleExp5 to handle scenario where there is a return order against original order 
					and COM can't choose between sales and return.hence no result is getting displayed. */
					Element eleExp5 = docAJBSettlement.createElement("Exp");
					eleComplexQuery.setAttribute("Operator", "OR");
					
					eleExp1.setAttribute("Name", "TerminalName");
					eleExp1.setAttribute("Value", payTechOrderNo.substring(0, 3));
					eleExp1.setAttribute("QryType", "LIKE");
					
					eleExp2.setAttribute("Name", "TransactionNumber");
					eleExp2.setAttribute("Value", payTechOrderNo.substring(3, 8));
					eleExp2.setAttribute("QryType", "LIKE");
	
					eleExp3.setAttribute("Name", "StoreNumber");
					eleExp3.setAttribute("Value", payTechOrderNo.substring(8, 12));
					eleExp3.setAttribute("QryType", "LIKE");
	
					eleExp4.setAttribute("Name", "TransactionAqDate");
					eleExp4.setAttribute("Value", payTechOrderNo.substring(12));
					eleExp4.setAttribute("QryType", "EQ");
					
					eleExp5.setAttribute("Name", "TransactionType");
					eleExp5.setAttribute("Value", "Sale");
					eleExp5.setAttribute("QryType", "EQ");
	
					eleAJBSettlement.appendChild(eleComplexQuery);
					eleComplexQuery.appendChild(eleAND);
					eleAND.appendChild(eleExp1);
					eleAND.appendChild(eleExp2);
					eleAND.appendChild(eleExp3);
					eleAND.appendChild(eleExp4);
					eleAND.appendChild(eleExp5);
					
					
					Document output = VSIPcaUtils.invokeApi(
							VSIApiNames.API_GET_AJB_SETTLEMENT_LIST, eleAJBSettlement
									.getOwnerDocument(), getFormId());
					if (!YRCPlatformUI.isVoid(output) && 0 == output.getElementsByTagName("AJBSettlement").getLength() - 1) {
						Element eleOPAJBSettlement = (Element) output.getElementsByTagName("AJBSettlement").item(0);
						if (null != eleOPAJBSettlement && null != eleOPAJBSettlement.getAttribute("OrderHeaderKey")) {
							ordEle.setAttribute("OrderHeaderKey", eleOPAJBSettlement.getAttribute("OrderHeaderKey"));						
						}
					}
				}
				else{
					YRCPlatformUI.showError("Error","PaymentTech Order # should have exact 20 digits!");
				}

			}	
			if(ordEle.hasAttribute("TrackingNo") && ordEle.getAttribute("TrackingNo") != null){
				String trackingNo = ordEle.getAttribute("TrackingNo");

				Element eleContainer = YRCXmlUtils.createDocument("Container").getDocumentElement();
				eleContainer.setAttribute("TrackingNo", trackingNo);
				
				ordEle.setAttribute("OrderHeaderKey","Dummy");
				
				Document output = VSIPcaUtils.invokeApi(
						VSIApiNames.API_GET_SHIPMENT_CONTAINER_LIST, eleContainer.getOwnerDocument(), getFormId());
				if (!YRCPlatformUI.isVoid(output)) {
					Element eleOPContainer = (Element) output.getElementsByTagName("Container").item(0);
							
					if (null != eleOPContainer) {
						Element eleOPShipment = (Element) output.getElementsByTagName("Shipment").item(0); 
					
						if (null != eleOPShipment && null != eleOPShipment.getAttribute("OrderHeaderKey")) {
							ordEle.setAttribute("OrderHeaderKey", eleOPShipment.getAttribute("OrderHeaderKey"));
						}
							
					}
				}
							
			}
		}		
		
 		if("getItemListForOrdering".equalsIgnoreCase(strApiName)) {
			Document inpDoc=apiContext.getInputXml();
			//System.out.println("Printing Input XML : "+VSIXmlUtils.getElementXMLString(inpDoc.getDocumentElement()));
			Element itemEle = inpDoc.getDocumentElement();
			
			if(getPaginationData() != null){
				   getPaginationData().trySetPaginationStrategy(YRCPaginationData.YRC_RESULTSET_PAGINATION_STRATEGY);
				   }
			
			String strItemID="";
			String strCallingOrgCode = itemEle.getAttribute("CallingOrganizationCode");
			Element prmyInfoEle = (Element) itemEle.getElementsByTagName("PrimaryInformation").item(0);
			if(prmyInfoEle != null){
				String desc = prmyInfoEle.getAttribute("Description");
				if(desc !=null && !desc.equalsIgnoreCase("")){
					desc = desc.toUpperCase();
					prmyInfoEle.setAttribute("Description", desc);
				}
			}
			if(itemEle != null && itemEle.hasAttribute(VSIConstants.A_ITEM_ID) && !strCallingOrgCode.equalsIgnoreCase("DEFAULT")){
				strItemID = itemEle.getAttribute(VSIConstants.A_ITEM_ID);
				strItemID = strItemID.toUpperCase();
				//System.out.println(" Printing User Entered Item ID : " +strItemID );
				itemEle.setAttribute(VSIConstants.A_ITEM_ID, "");
				Element complxQryEle = (Element) itemEle.getElementsByTagName("ComplexQuery").item(0);
				if(complxQryEle != null){
					complxQryEle.setAttribute("Operator", "AND");
					Node eleAND = complxQryEle.getElementsByTagName("And").item(0);
					complxQryEle.removeChild(eleAND);
					Element eleOR = inpDoc.createElement("Or");
					complxQryEle.appendChild(eleOR);
					Element eleExp = inpDoc.createElement("Exp");
					eleExp.setAttribute("Name", VSIConstants.A_ITEM_ID);
					eleExp.setAttribute("Value", strItemID);
					eleOR.appendChild(eleExp);
					eleExp = inpDoc.createElement("Exp");
					eleExp.setAttribute("Name", "Extn_ExtnActSkuID");
					eleExp.setAttribute("Value", strItemID);
					eleOR.appendChild(eleExp);
					complxQryEle.appendChild(eleAND);
					
					
				}
			}
			//System.out.println("Printing Modified Input XML : "+VSIXmlUtils.getElementXMLString(inpDoc.getDocumentElement()));
			
			
		}
 		
 		
 		if(strApiName.equals("getPage")){
 			if(getPaginationData() != null){
				   getPaginationData().trySetPaginationStrategy(YRCPaginationData.YRC_RESULTSET_PAGINATION_STRATEGY);
				   }
 		}
 		
 		
 		if(strApiName.equals("getCustomerList")){
 			
			Element eleInput=apiContext.getInputXml().getDocumentElement();		
			//System.out.println(" Inside precommand" + VSIXmlUtils.getElementXMLString(eleInput));
			eleInput.setAttribute("CustomerType", "02");
			if(getPaginationData() != null){
				   getPaginationData().trySetPaginationStrategy(YRCPaginationData.YRC_RESULTSET_PAGINATION_STRATEGY);
				   }
			if(!YRCPlatformUI.isVoid(eleInput)){
				String strHealthyAwardsNo = getFieldValue("extn_txtCustomerID");
				String strDayPhone=getFieldValue("txtDayPhoneNo");
				String strEmailID=getFieldValue("txtEmailID");
				Element eleCustomerContactList=YRCXmlUtils.getChildElement(eleInput, "CustomerContactList", true);
				Element eleCustomerContact=YRCXmlUtils.getChildElement(eleCustomerContactList, "CustomerContact", true);
				Element eleAdditionalAddressList=YRCXmlUtils.createChild(eleCustomerContact, "CustomerAdditionalAddressList");
				Element eleAdditionalAddress=YRCXmlUtils.createChild(eleAdditionalAddressList, "CustomerAdditionalAddress");
				Element elePersonInfo=YRCXmlUtils.createChild(eleAdditionalAddress, "PersonInfo");
				
				
				if(!YRCPlatformUI.isVoid(strDayPhone)){
					//eleCustomerContact.setAttribute("DayPhone", strDayPhone);
				}
				if(!YRCPlatformUI.isVoid(strEmailID)){
					eleCustomerContact.setAttribute("EmailID", strEmailID);
				}
				
				
				if(!YRCPlatformUI.isVoid(strHealthyAwardsNo)){
					eleCustomerContact.setAttribute("HealthyAwardsNo", strHealthyAwardsNo);
					eleCustomerContact.removeAttribute("UserID");
					eleCustomerContact.removeAttribute("UserIDQryType");
				}else {
					eleCustomerContact.setAttribute("HealthyAwardsNo", "");
				}
				
				
			}
			
		}
 		
 		
 		
 		if(VSIApiNames.API_GET_ITEM_LIST.equalsIgnoreCase(strApiName)) {
			
 			Element eleItemListSearchResult=apiContext.getInputXml().getDocumentElement();
			
			if(!YRCPlatformUI.isVoid(eleItemListSearchResult)){				

				String strItemID = eleItemListSearchResult.getAttribute("ItemID");
				if(!YRCPlatformUI.isVoid(strItemID)){
					eleItemListSearchResult.setAttribute("CallingOrganizationCode", VSIConstants.V_ORG_VSI_COM);
				}
			}
		}
 		return super.preCommand(apiContext);
 	}
 	
 	
    public String getExtnNextPage(String currentPageId) {
		//TODO
		return null;
    }
    
    public IYRCComposite createPage(String pageIdToBeShown) {
		//TODO
		return null;
	}
    
    public void pageBeingDisposed(String pageToBeDisposed) {
		//TODO
    }

    /**
     * Called when a wizard page is about to be shown for the first time.
     *
     */
    public void initPage(String pageBeingShown) {
		//TODO
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
		
		// TODO Create and return a response.
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
}