	
package com.vsi.scc.oms.pca.extensions.cancelOrder;

/**
 * Created on Sep 09,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSICancelOrderWizExtnBehavior extends YRCWizardExtensionBehavior {

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
    	// TODO Validation required for the following controls.
		
			// Control name: btnConfirm
    	if(fieldName.equalsIgnoreCase("btnConfirm")){
    		Element orderEle = getModel("CancelOrder");
    		
    		if(orderEle != null){
    			
    		NodeList orderLineList = orderEle.getElementsByTagName(VSIConstants.E_ORDER_LINE);
    		int noOfLines = orderLineList.getLength();
    		int selectedPISLineCount = 0;
    		int noOfPISLines = 0;
			int selectedSTSLineCount = 0;
			int noOfSTSLines = 0;
			int selectedSTHLineCount = 0;
			boolean canCancelSTH = true;
			boolean canCancelPIS = true;
			boolean canCancelSTS = true;
			for(int j=0; j < orderLineList.getLength(); j++ ){
				Element orderLineEle = (Element) orderLineList.item(j);
    			String lineType = orderLineEle.getAttribute("LineType");
    			if(lineType.equalsIgnoreCase("PICK_IN_STORE")){
					noOfPISLines++;
				}else if(lineType.equalsIgnoreCase("SHIP_TO_STORE")){
					noOfSTSLines++;
				}
			}
    		for(int i=0; i < orderLineList.getLength(); i++ ){
    			Element orderLineEle = (Element) orderLineList.item(i);
    			String lineType = orderLineEle.getAttribute("LineType");
    			if(orderLineEle.hasAttribute("QtyToCancel")){
    				String cancelQty = orderLineEle.getAttribute("QtyToCancel");
    				String ordQty = orderLineEle.getAttribute("OrderedQty");
    				if(cancelQty !=null && !cancelQty.trim().equalsIgnoreCase("")){
    					Double dCancelQty = Double.parseDouble(cancelQty);
    					Double dOrdQty = Double.parseDouble(ordQty);
    					//if(dCancelQty > 0 && dCancelQty.equals(dOrdQty)){
    						Double dStatus = Double.parseDouble(orderLineEle.getAttribute("MaxLineStatus"));
    						//System.out.println("dStatus 1 : "+orderLineEle.getAttribute("MaxLineStatus"));
    						//System.out.println("dStatus : "+dStatus);
    						if(lineType.equalsIgnoreCase("PICK_IN_STORE")  ){
    							selectedPISLineCount++;
    							//check for mixed order
    							if(dStatus > 1500){
    								canCancelPIS = false;
    							}
    							if(dStatus > 1500 && !dCancelQty.equals(dOrdQty)){
    								YRCPlatformUI.showError("Error","Cannot cancel orderLine partially ! \nPlease cancel the entire order." );
    	    						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
    	    						
    							}
    							
    							}else if (lineType.equalsIgnoreCase("SHIP_TO_STORE")){
    								selectedSTSLineCount ++;
    							Element orderHoldTypes = (Element) orderLineEle.getElementsByTagName("OrderHoldTypes").item(0);
    							if(orderHoldTypes != null){
    								NodeList orderHoldTypeNL = orderHoldTypes.getElementsByTagName("OrderHoldType");
    								if(orderHoldTypeNL.getLength() > 0){
    								for(int j=0; j < orderHoldTypeNL.getLength(); j++){
    									Element orderHoldType = (Element) orderHoldTypeNL.item(j);
    									String holdType = orderHoldType.getAttribute("HoldType");
        								String holdStatus = orderHoldType.getAttribute("Status");
        								if(holdType.equalsIgnoreCase("REMORSE_HOLD") && !holdStatus.equalsIgnoreCase("1100")){
        									canCancelSTS = false;
        								}
    									
    								}
    								}else{
    									canCancelSTS = false;
    								}
    								
    								
    							}else{
    								canCancelSTS = false;
    								}
    							
    							if( (dStatus > 1100 && selectedSTSLineCount == noOfLines ) || (!canCancelSTS && !dCancelQty.equals(dOrdQty))){
    								YRCPlatformUI.showError("Error","Cannot cancel orderLine partially ! \nPlease cancel the entire order." );
    	    						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
    	    						
    							}
    							
    						}else if (lineType.equalsIgnoreCase("SHIP_TO_HOME") ){
    							selectedSTHLineCount++;
    							if(dStatus > 1100 )canCancelSTH = false;
    							
    						}
    						
    					/*}else if (dCancelQty > 0 && !dCancelQty.equals(dOrdQty)){
    						YRCPlatformUI.showError("Error","Cannot cancel orderLine partially ! \nPlease cancel the entire order." );
    						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
    						
    					}*/
    					
    				}
    				
    			}
    		}
    		// validate here
    		if(selectedSTHLineCount> 0 && selectedSTHLineCount != noOfLines && !canCancelSTH){
    			YRCPlatformUI.showError("Error","Cannot cancel Order Line partially ! \n Please Cancel all the Ship To Home Lines or the entire order." );
				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
				
    		}// mixed Order
    		else if (selectedPISLineCount > 0 && selectedPISLineCount != noOfLines){
    			if(selectedPISLineCount != noOfPISLines && !canCancelPIS){
    				YRCPlatformUI.showError("Error","Cannot cancel Order Line partially ! \n Please Cancel all the Pick In Store Lines or the entire order." );
    				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
    				
    			}
    				
    			} 
    		if(selectedSTSLineCount > 0 && selectedSTSLineCount != noOfLines){
    				if(selectedSTSLineCount != noOfSTSLines && !canCancelSTS){
    					YRCPlatformUI.showError("Error","Cannot cancel Order Line partially ! \n Please Cancel all the Ship To Store Lines or the entire order." );
        				return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Cannot Cancel Order Line");
        				
    				}
    				
    			}
    			
    		
    		
    		}
    		
    	}
		
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
	 
		public boolean preCommand(YRCApiContext apiContext) {
			// TODO Auto-generated method stub
			 String strApiName = apiContext.getApiName();
			 if("changeOrder".equalsIgnoreCase(strApiName)){
				 Element eleOutput = apiContext.getInputXml().getDocumentElement();
				 if(!YRCPlatformUI.isVoid(eleOutput)){
					 NodeList ndlOutput = eleOutput.getElementsByTagName("OrderLine");
					 for(int i = 0; i <ndlOutput.getLength(); i++){
						 Element eleOrderLIne = (Element)ndlOutput.item(i);
						 if(!YRCPlatformUI.isVoid(eleOrderLIne)){
							 eleOrderLIne.setAttribute("Action", "CANCEL");
						 }
					 }
				 }
				 
			 }
			return super.preCommand(apiContext);
		}
}
