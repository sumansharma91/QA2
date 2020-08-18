	
package com.vsi.scc.oms.pca.extensions.issueRefundNow;

/**
 * Created on Jul 30,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIIssueRefundNowWizExtnBehavior extends YRCWizardExtensionBehavior {

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
    	
    	if (fieldName.equalsIgnoreCase("bttnClose")){
    		
    		//System.out.println("Inside bttnclose method");
    		YRCApiContext context = new YRCApiContext();
    		context.setApiName("VSIIssueRefundActions");
    		context.setFormId(getFormId());
    		
    		if(getModel("getCompleteOrderDetails_output") != null){
    			Element eleOrder = getModel("getCompleteOrderDetails_output");
    			Element eleOrderIp = null;
    			if(eleOrder != null){
    				eleOrderIp = YRCXmlUtils.createDocument("Order").getDocumentElement();
    				eleOrderIp.setAttribute("OrderHeaderKey", eleOrder.getAttribute("OrderHeaderKey"));
    				//System.out.println("eleOrder "+eleOrder.getAttribute("OrderHeaderKey"));
    			}
    			
    			//System.out.println("XML"+getModel("getCompleteOrderDetails_output").getOwnerDocument().toString());
    		context.setInputXml(eleOrderIp.getOwnerDocument());
    		//System.out.println("XML"+getTargetModel("Order").getOwnerDocument());
    		callApi(context);
    			
    			//System.out.println("Inside bttnclose method SUCCESS");
    		}
    		
    		
    		
    	}
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
