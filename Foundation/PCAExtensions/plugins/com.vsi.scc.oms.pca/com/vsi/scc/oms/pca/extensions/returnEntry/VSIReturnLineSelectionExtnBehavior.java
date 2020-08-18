	
package com.vsi.scc.oms.pca.extensions.returnEntry;

/**
 * Created on Oct 29,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopApplication;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIReturnLineSelectionExtnBehavior extends YRCExtentionBehavior{

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
 @Override
public void postCommand(YRCApiContext apiContext) {
	 String strApiName = apiContext.getApiName();
	 String appID = YRCDesktopApplication.getInstance().getApplicationID();
	 if(appID.equalsIgnoreCase("YFSSYS00006") && "getOrderLinesWithTransactionQuantity".equalsIgnoreCase(strApiName)) {
		 Document outPutDoc=apiContext.getOutputXml();
		 Element rootEle = outPutDoc.getDocumentElement();
		 if(rootEle.hasAttribute("TotalOrderList") && rootEle.getAttribute("TotalOrderList").equalsIgnoreCase("0")){
			 YRCPlatformUI.showError("Error","This order number entered cannot be found. Please try again or use the order search.");
		 }else if(rootEle.hasAttribute("TotalOrderList") && Double.parseDouble(rootEle.getAttribute("TotalOrderList")) > 1){
			 YRCPlatformUI.showError("Error","More than one order found. Please use the order search.");
		 }
	 }
	super.postCommand(apiContext);
}
 	
 	@Override
 	public boolean preCommand(YRCApiContext apiContext) {
 		
 		String strApiName = apiContext.getApiName();
		//System.out.println("API: "+strApiName);
		//System.out.println("Printing Modified Input XML : "+VSIXmlUtils.getElementXMLString(apiContext.getInputXml().getDocumentElement()));
		
		String appID = YRCDesktopApplication.getInstance().getApplicationID();
		//System.out.println("appID: "+appID);
		
		if(appID.equalsIgnoreCase("YFSSYS00006") && "getOrderLinesWithTransactionQuantity".equalsIgnoreCase(strApiName)) {
			Document inpDoc=apiContext.getInputXml();
			
			Element ordEle = inpDoc.getDocumentElement();
			if(ordEle.hasAttribute("OrderNo") && ordEle.getAttribute("OrderNo") != null){
				String custPONo = ordEle.getAttribute("OrderNo");
				ordEle.removeAttribute("OrderNo");
				Element ordLineEle = inpDoc.createElement("OrderLine");
				ordEle.appendChild(ordLineEle);
				ordLineEle.setAttribute("CustomerPONo", custPONo);
			}
			//System.out.println("Printing Modified Input XML : "+VSIXmlUtils.getElementXMLString(inpDoc.getDocumentElement()));
		}
 		return super.preCommand(apiContext);
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
