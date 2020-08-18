package com.vsi.scc.oms.pca.extensions.payment;


import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.common.VSIConstants;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCValidationResponse;

/**
 * @author skiran
 */

public class VSICreditCardDetailsExtensionBehavior extends YRCExtentionBehavior { 
	String FORM_ID = "com.yantra.pca.ycd.rcp.tasks.payment.screens.YCDCreditCardDetails";
	String firstName = "";
	String lastName = "";
	public static Element eleCardDetails = null; 
	/**
	 * This method initializes the behavior class. 
	 */
	public void init() {
		

		super.init();
	}


	public void handleApiCompletion(YRCApiContext ctxApi) {
		
		super.handleApiCompletion(ctxApi);
	}


	public YRCValidationResponse validateButtonClick(String fieldName) {
		if(fieldName.equals("btnConfirm")){
			
			
		}  

		return super.validateButtonClick(fieldName);
	}

	@Override
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		if("output".equalsIgnoreCase(arg0)){
			 eleCardDetails = getModel("output");
		}
		if("PersonInfoBillTo".equalsIgnoreCase(arg0)){
			 Element personInfoEle = getModel("PersonInfoBillTo");
			 firstName = personInfoEle.getAttribute("FirstName");
			 lastName = personInfoEle.getAttribute("LastName");
		}
		if(arg0.equalsIgnoreCase("input")){
			
			setFieldValue("txtCreditCardFirstName", firstName);
			setFieldValue("txtCreditCardLastName", lastName);
			
			Element input =getModel("input");
			
			input.setAttribute("FirstName", firstName);
			input.setAttribute("LastName", lastName);
			
			repopulateModel(arg0);
			
			
		}
		super.postSetModel(arg0);
	}

	public boolean preCommand(YRCApiContext apiContext) {


		return super.preCommand(apiContext);
	}


}