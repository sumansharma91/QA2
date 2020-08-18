	
package com.vsi.scc.oms.pca.extensions.orderSummary;

/**
 * Created on Dec 26,2013
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIOrderSummaryWizExtnBehavior extends YRCWizardExtensionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
	
	@Override
	public boolean preCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		return super.preCommand(arg0);
	}
 
 	
    @Override
	public void postSetModel(String namespace) {
		// TODO Auto-generated method stub
    	
    	if(namespace.equalsIgnoreCase("OrderDetails")){
    		
    		Element eleOrderDetails=getModel(namespace);
    		    		
    		if(!YRCPlatformUI.isVoid(eleOrderDetails)){
    			
					//Start::Fix for OMS-688
					String neworderDate=YRCXmlUtils.getAttribute(eleOrderDetails, "OrderDate");
					neworderDate = neworderDate.replace("T"," ");
					neworderDate=neworderDate.replace(neworderDate.substring(neworderDate.length()-6), "");
					eleOrderDetails.setAttribute("OrderDate", neworderDate);
					//End::Fix for OMS-688
    				
    				String strCustomerFirstName=YRCXmlUtils.getAttribute(eleOrderDetails, "CustomerFirstName");
    				
    				String strCustomerLastName=YRCXmlUtils.getAttribute(eleOrderDetails, "CustomerLastName");
    				
    				if(YRCPlatformUI.isVoid(strCustomerFirstName) ||  YRCPlatformUI.isVoid(strCustomerLastName)){
    					Element elePersonInfoBillTo = YRCXmlUtils.getChildElement(eleOrderDetails, "PersonInfoBillTo");
    					if(!YRCPlatformUI.isVoid(elePersonInfoBillTo)){
    						strCustomerFirstName =  elePersonInfoBillTo.getAttribute("FirstName");
    						strCustomerLastName = elePersonInfoBillTo.getAttribute("LastName");
    					}
    				}
    				
    				String strCustomerName=strCustomerFirstName+VSIConstants.BLANK+strCustomerLastName;
    				
    				if(!YRCPlatformUI.isVoid(strCustomerName)){
    					eleOrderDetails.setAttribute(VSIConstants.A_CUSTOMER_NAME, strCustomerName);
    					repopulateModel(namespace);
    				}
    				
    		}
    		
    	}
    	
		super.postSetModel(namespace);
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
		
			// Control name: extn_txtFullName
		
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