	
package com.vsi.scc.oms.pca.extensions.lineSummary;

/**
 * Created on Dec 26,2013
 *
 */
 
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.extensions.address.common.VSICommonAddressExtnBehavior;
import com.vsi.scc.oms.pca.util.VSIPcaUtils;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSILineSummaryWizExtnBehavior extends YRCWizardExtensionBehavior {

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
	
	
 
 	
    @Override
	public void postSetModel(String namespace) {
		// TODO Auto-generated method stub
    	
    	if(namespace.equalsIgnoreCase("OrderLineDetails")){
    		
    		Element eleOrderLineDetails=getModel(namespace);
			
			if(!YRCPlatformUI.isVoid(eleOrderLineDetails)){
				
				String strShipNode=eleOrderLineDetails.getAttribute(VSIConstants.A_SHIP_NODE);
				
				if(!YRCPlatformUI.isVoid(strShipNode)){
					
					callStoreDetailsForCalender(strShipNode,this,eleOrderLineDetails);
					repopulateModel("OrderLineDetails");
					
				}
				
				
			}
    	}
		super.postSetModel(namespace);
	}
    
    
    private void callStoreDetailsForCalender(String strShipNode,
			VSILineSummaryWizExtnBehavior lineSummaryWizExtnBehavior,Element eleOrderLineDetails) {
		// TODO Auto-generated method stub
		Element eleStoreCalendarDetailsInput=YRCXmlUtils.createDocument(VSIConstants.E_ORGANIZATION).getDocumentElement();
		eleStoreCalendarDetailsInput.setAttribute(VSIConstants.A_ORGANIZATION_CODE, strShipNode);
		
		Document output=VSIPcaUtils.invokeApi(VSIApiNames.API_GET_ORGANIZATION_HIERARCHY, 
				eleStoreCalendarDetailsInput.getOwnerDocument(), getFormId());
		
		if(!YRCPlatformUI.isVoid(output)){
			
			eleOrderLineDetails.setAttribute(VSIConstants.A_BUSINESS_CALENDAR_KEY, output.getDocumentElement().getAttribute(VSIConstants.A_BUSINESS_CALENDAR_KEY));
			
			
		}
		
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
		
			// Control name: linkItem
		
		if(fieldName.equalsIgnoreCase("extn_linkItem")){
			
			Element eleOrderLineDetails=getModel("OrderLineDetails");
			
			if(!YRCPlatformUI.isVoid(eleOrderLineDetails)){
				
				Element itemDetailsSharedTaskInput=createItemDetailsSharedTaskInput(eleOrderLineDetails);
				
				YRCPlatformUI.launchSharedTask("YCDItemDetailsSharedTask", itemDetailsSharedTaskInput);
				
				
				}
			
		}
		
		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}
	
	private Element createItemDetailsSharedTaskInput(Element eleOrderLineDetails) {

		Element inputItemDetailsSharedTask=YRCXmlUtils.createDocument(VSIConstants.E_ITEM).getDocumentElement();
		
		Element orderDetails=getModel("OrderDetails");
		
		if(!YRCPlatformUI.isVoid(orderDetails)){
			
			inputItemDetailsSharedTask.setAttribute(VSIConstants.A_CALLING_ORGANIZATION_CODE, orderDetails.getAttribute(VSIConstants.A_ENTERPRISE_CODE));
			inputItemDetailsSharedTask.setAttribute(VSIConstants.A_GET_UNPUBLISHED_ITEMS, VSIConstants.FLAG_Y);
			
			Element eleItem=YRCXmlUtils.getChildElement(eleOrderLineDetails, VSIConstants.E_ITEM,true);
			
			inputItemDetailsSharedTask.setAttribute(VSIConstants.A_ITEM_ID, eleItem.getAttribute(VSIConstants.A_ITEM_ID));
			inputItemDetailsSharedTask.setAttribute(VSIConstants.A_UNIT_OF_MEASURE, eleItem.getAttribute(VSIConstants.A_UNIT_OF_MEASURE));
			
			Element eleItemDetails=YRCXmlUtils.getChildElement(eleOrderLineDetails, VSIConstants.E_ITEM_DETAILS,true);
			inputItemDetailsSharedTask.setAttribute(VSIConstants.A_ITEM_GROUP_CODE, eleItemDetails.getAttribute(VSIConstants.A_ITEM_GROUP_CODE));
			
			Element eleShipToPersonInfo=YRCXmlUtils.getChildElement(inputItemDetailsSharedTask, VSIConstants.E_SHIP_TO_PERSON_INFO, true);
			
			ArrayList<YRCExtentionBehavior> childExtnBehaviors=this.getChildExtnBehaviors();

			for (Iterator<YRCExtentionBehavior> iter = childExtnBehaviors.iterator(); iter.hasNext();) {
				YRCExtentionBehavior childExtnBehv = iter.next();
				if(childExtnBehv instanceof VSICommonAddressExtnBehavior) {
					VSICommonAddressExtnBehavior commonAddressExtnBehavior = (VSICommonAddressExtnBehavior) childExtnBehv;

					if(!YRCPlatformUI.isVoid(commonAddressExtnBehavior)){
						
						Element eleAddress=commonAddressExtnBehavior.getSourceModel("Address");
						
						if(!YRCPlatformUI.isVoid(eleAddress)){
							
							VSIXmlUtils.copyAttributes(eleAddress, eleShipToPersonInfo);
							YRCEditorInput editorItemInput = ((YRCEditorInput) ((YRCEditorPart) YRCDesktopUI.getCurrentPart()).getEditorInput());
							editorItemInput.getXml().setAttribute(VSIConstants.A_ZIP_CODE, eleAddress.getAttribute(VSIConstants.A_ZIP_CODE));
							inputItemDetailsSharedTask.setAttribute(VSIConstants.A_ZIP_CODE, eleAddress.getAttribute(VSIConstants.A_ZIP_CODE));
							
						}
					}
						
						
					}
			}
		    
		    
			
		}
		
		
		
		return inputItemDetailsSharedTask;
	}


	


	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }


	public Element getSourceModel(String namespace) {
		// TODO Auto-generated method stub
		return getModel(namespace);
	}
}
//TODO Validation required for a Link control: extn_linkItem