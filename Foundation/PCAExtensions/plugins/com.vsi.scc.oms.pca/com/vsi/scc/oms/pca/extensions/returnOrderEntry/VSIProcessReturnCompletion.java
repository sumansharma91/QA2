	
package com.vsi.scc.oms.pca.extensions.returnOrderEntry;

/**
 * Created on Sep 22,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIProcessReturnCompletion extends YRCWizardExtensionBehavior {

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
    
    @Override
    public void postSetModel(String namespace) {
    	 if (namespace.equalsIgnoreCase("SelectedReturnOrder")) {
    		 Element eleOrder = getModel(namespace);
    		 
    		 NodeList orderLineList =  eleOrder.getElementsByTagName(VSIConstants.E_ORDER_LINE);
    		 for(int i=0; i < orderLineList.getLength();i++){
    			 Element orderLineEle = (Element) orderLineList.item(0);
    			 orderLineEle.setAttribute("IsReturnable", "OVERRIDE_CHECK_BOX_SELECTED");
    			 
    		 }
    		 //System.out.println("Printing Model XML"+VSIXmlUtils.getElementXMLString(eleOrder));
    		 
    	 }
    	super.postSetModel(namespace);
    }
    
    
    
    public IYRCComposite createPage(String pageIdToBeShown) {
		//TODO
		return null;
	}
    @Override
    public boolean preCommand(YRCApiContext apiContext) {
    	String apiName = apiContext.getApiName();
    	//System.out.println("API Name : "+apiName);
    	
    	if(apiName.equalsIgnoreCase("createOrderForReturn")){
    		Document orderDoc = apiContext.getInputXml();
    		orderDoc.getDocumentElement().setAttribute("DraftOrderFlag", "N");
    		//System.out.println("XML \n " +VSIXmlUtils.getElementXMLString(orderDoc.getDocumentElement()));
    	}
    	/*if(apiName.equalsIgnoreCase("processReturnCompletionUE")){
    		Document orderDoc = apiContext.getInputXml();
    		//orderDoc.getDocumentElement().setAttribute("DraftOrderFlag", "N");
    		//System.out.println("XML \n " +VSIXmlUtils.getElementXMLString(orderDoc.getDocumentElement()));
    	}*/
    	// TODO Auto-generated method stub
    	return super.preCommand(apiContext);
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
