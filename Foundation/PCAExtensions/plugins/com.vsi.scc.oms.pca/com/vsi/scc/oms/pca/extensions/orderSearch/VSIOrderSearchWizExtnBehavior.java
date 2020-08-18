	
package com.vsi.scc.oms.pca.extensions.orderSearch;

/**
 * Created on Dec 26,2013
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.vsi.scc.oms.pca.util.VSIPcaUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopApplication;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIOrderSearchWizExtnBehavior extends YRCWizardExtensionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
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
											
											strCustomOrderType=strCustomOrderType+"-"+strCustomLineType;
											
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
    
    @Override
    public boolean preCommand(YRCApiContext apiContext) {
    	// TODO Auto-generated method stub
    	String appID = YRCDesktopApplication.getInstance().getApplicationID();
    	if(("getOrderList").equalsIgnoreCase(apiContext.getApiName())){
    		 Element eleInput = apiContext.getInputXml().getDocumentElement();
    		 if(appID.equalsIgnoreCase("YFSSYS00006") ) {
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
 			 String strCustomerPONo = eleInput.getAttribute("CustomerPONo");
 			 String strCustomerPONoQryType = eleInput.getAttribute("CustomerPONoQryType");
 			 if(!YRCPlatformUI.isVoid(strCustomerPONo)){
 				
 				 Element eleOrderLine = YRCXmlUtils.getChildElement(eleInput, "OrderLine", true);
 				 eleOrderLine.setAttribute("CustomerPONo", strCustomerPONo);
 				 eleInput.removeAttribute("CustomerPONo");
 				if(!YRCPlatformUI.isVoid(strCustomerPONoQryType)){
 					eleOrderLine.setAttribute("CustomerPONoQryType", strCustomerPONoQryType);
 					 eleInput.removeAttribute("CustomerPONoQryType");
 				}
 			 }
 			
    	}

		//Logic for fetching Order header key using PaymentTech Order # and Tracking #
		if(appID.equalsIgnoreCase("YFSSYS00011") && "getOrderList".equalsIgnoreCase(apiContext.getApiName())) {
			Document inDoc=apiContext.getInputXml();
			Element ordEle = inDoc.getDocumentElement();
			
			if(ordEle.hasAttribute("OrderHeaderKey")){
				ordEle.removeAttribute("OrderHeaderKey");
			}
			
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
		
    	return super.preCommand(apiContext);
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