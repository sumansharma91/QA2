	
package com.vsi.scc.oms.pca.extensions.itemDetails;

/**
 * Created on Dec 24,2013
 *
 */
 
import java.util.ArrayList;

import org.eclipse.swt.program.Program;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIItemDetailsWizExtnBehavior extends YRCWizardExtensionBehavior {

	 String strZipCode="";
	/**
	 * This method initializes the behavior class.
	 * 
	 *
	 */
	public void init() {
		
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
    	
    }

    /**
     * Called when a wizard page is about to be shown for the first time.
     *
     */
    public void initPage(String pageBeingShown) {
		//TODO
    }
    
    
 	
 	
	@Override
	public void postSetModel(String namespace) {
		// TODO Auto-generated method stub
		
		if(namespace.equalsIgnoreCase("Results")){
			
			Element eleItemDetailResults=getModel(namespace);
			
			if(!YRCPlatformUI.isVoid(eleItemDetailResults)){
				
				Element elePrimaryInfo=(Element)eleItemDetailResults.getElementsByTagName(VSIConstants.E_PRIMARY_INFORMATION).item(0);
				
				elePrimaryInfo.setAttribute("ItemWebSiteUrl", YRCPlatformUI.getString("ItemLink"));
				
			}
			
			
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
		
			// Control name: extn_lnkItemWebsiteLink
		
		if(fieldName.equals("extn_linkItemwebsite")){
			
				Element elePrimaryInfo=(Element)getModel("Results").getElementsByTagName(VSIConstants.E_PRIMARY_INFORMATION).item(0);
				String url = YRCXmlUtils.getAttribute(elePrimaryInfo, "ItemWebSiteUrl");
				if(!YRCPlatformUI.isVoid(url))
					Program.launch(url);
		
			
		}
		
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
//TODO Validation required for a Link control: extn_linkItemwebsite