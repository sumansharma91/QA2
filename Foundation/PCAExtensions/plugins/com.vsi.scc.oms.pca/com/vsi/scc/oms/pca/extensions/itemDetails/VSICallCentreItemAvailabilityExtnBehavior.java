	
package com.vsi.scc.oms.pca.extensions.itemDetails;

/**
 * Created on Dec 26,2013
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSICallCentreItemAvailabilityExtnBehavior extends YRCExtentionBehavior{
	 
	 
	 String strZipCode="";

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		
		final YRCEditorInput editorSOInput = ((YRCEditorInput) ((YRCEditorPart) YRCDesktopUI.getCurrentPart()).getEditorInput());
    	
		
		if(!YRCPlatformUI.isVoid(editorSOInput.getXml())){
			
			this.strZipCode=editorSOInput.getXml().getAttribute(VSIConstants.A_ZIP_CODE);			
			
		}
	
	}
	
	
 
 	
 	
	@Override
	public void postSetModel(String namespace) {
		
		if(namespace.equalsIgnoreCase("Results")){
			
			setFieldValue("textZipCode", strZipCode);
			
			Element eleItemResults=getModel(namespace);
			
			eleItemResults.setAttribute(VSIConstants.A_ZIP_CODE, strZipCode);
			
			repopulateModel(namespace);
			
			
		}
	
		super.postSetModel(namespace);
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