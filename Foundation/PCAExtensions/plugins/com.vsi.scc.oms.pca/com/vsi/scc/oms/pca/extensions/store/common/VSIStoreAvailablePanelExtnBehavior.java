	
package com.vsi.scc.oms.pca.extensions.store.common;

/**
 * Created on Jan 10,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIStoreAvailablePanelExtnBehavior extends YRCExtentionBehavior{

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
 
// 	public string isDCInvAvail = true;
 	
	@Override
	public boolean preCommand(YRCApiContext apiContext) {
		
		// get the Api Name.
		final String strApiName = apiContext.getApiName();

		// check if Api invoked is verifyAddress
		
		
			return super.preCommand(apiContext);
	}

	@Override
	public void postCommand(YRCApiContext arg0) {
		// TODO Auto-generated method stub
		super.postCommand(arg0);
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
		
			// Control name: btnSearch
		
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
	 
	 @Override
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		 boolean isDCInvAvail = true;
		 if("PromiseLine".equalsIgnoreCase(arg0)){
			 Element eleModel = getModel(arg0);
			 NodeList ndlAssignment = eleModel.getElementsByTagName("Assignment");
			 String strInvAvailable = "0";
			 for(int i=0; i<ndlAssignment.getLength();i++){
				 Element eleAssignment = (Element)ndlAssignment.item(i);
				 if(!YRCPlatformUI.isVoid(eleAssignment)){
					 String strShipNode = eleAssignment.getAttribute("ShipNode");
					 if(!YRCPlatformUI.isVoid(strShipNode)){
						 strInvAvailable = eleAssignment.getAttribute("Quantity");
					 }

				 }


			 }
			 eleModel.setAttribute("QuantityAvailable", strInvAvailable);
		 }
		super.postSetModel(arg0);
	}
}
