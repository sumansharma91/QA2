	
package com.vsi.scc.oms.pca.extensions.order.coupon;

/**
 * Created on Sep 19,2014
 *
 */
 
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizard;
/**
 * @author Admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIApplyCouponExtnBehavior extends YRCExtentionBehavior{

	 String strPromotionID = "";
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
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
		
			// Control name: buttonApply
		
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
	@Override
	public boolean preCommand(YRCApiContext apiContext) {
		String apiName = apiContext.getApiName();
		String strCurrentPage=((YRCWizard)YRCDesktopUI.getCurrentPage()).getCurrentPageID();
		Document inputXML = null;
		if(apiName.equalsIgnoreCase("validateCoupon") && 
				("com.yantra.pca.ycd.rcp.tasks.addCoupon.wizardpages.YCDAddCouponEntryPage".equalsIgnoreCase(strCurrentPage))){
			
			Element orderEle = getModel("OrderDetails");
			if(orderEle != null){
				String ohk = orderEle.getAttribute("OrderHeaderKey");
				inputXML = apiContext.getInputXml();
				if(inputXML != null){
					//inputXML.getDocumentElement().setAttribute("CustomerID", "1524"); 
					inputXML.getDocumentElement().setAttribute("OrderReference", ohk); 
				} 
			}
			
		}else if ("changeOrder".equalsIgnoreCase(apiName)){
			
			Element eleInput = apiContext.getInputXml().getDocumentElement();
			//START FIX
			eleInput.setAttribute("BypassPricing", "N");
			//END FIX
			
			Element elePromotion = (Element)eleInput.getElementsByTagName("Promotion").item(0);
			String strCouponID = elePromotion.getAttribute("PromotionId");
			elePromotion.setAttribute("PromotionId", strPromotionID);
			Element eleExtn = eleInput.getOwnerDocument().createElement("Extn");
			eleExtn.setAttribute("ExtnCouponID", strCouponID);
			elePromotion.appendChild(eleExtn);
		}
		
		// TODO Auto-generated method stub
		return super.preCommand(apiContext);
	}
	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }
	 
	 
	public void postCommand(YRCApiContext apiContext) {
		// TODO Auto-generated method stub
		String apiName = apiContext.getApiName();
		if ("validateCoupon".equalsIgnoreCase(apiName)){
			Element eleOutput = apiContext.getOutputXml().getDocumentElement();
			strPromotionID = eleOutput.getAttribute("CouponID");
//			eleOutput.setAttribute("Valid", "Y");
//			eleOutput.setAttribute("CouponStatusMsgCode", "YPM_RULE_VALID");
		}
		super.postCommand(apiContext);
	}
}
