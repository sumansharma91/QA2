	
package com.vsi.scc.oms.pca.extensions.modifyCharges;

/**
 * Created on Sep 25,2014
 *
 */
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author Admin © Copyright IBM Corp. All Rights Reserved.
 */
public class VSIAddModifyChargesExtnBehavior extends YRCExtentionBehavior {

	String FORM_ID= "com.yantra.pca.ycd.rcp.tasks.addModifyCharges.screens.YCDAddModifyChargesPopup";
	HashMap<Integer, String> namesMap = new HashMap<Integer, String>();
	String[] namesArry = null;
	/**
	 * This method initializes the behavior class.
	 */
	
	
	public void init() {
		
		Document docCommonCodeListInput =YRCXmlUtils.createFromString("<CommonCode />");
		Element eleCommonCodeListInput = docCommonCodeListInput.getDocumentElement();
		eleCommonCodeListInput.setAttribute("CodeType","SHOW_CHARGE_NAME");
		YRCApiContext context = new YRCApiContext();
		context.setApiName("getCommonCodeList");
		context.setFormId(FORM_ID);
		context.setInputXml(eleCommonCodeListInput.getOwnerDocument());
		callApi(context);
	}
 
 	@Override
 	public void handleApiCompletion(YRCApiContext ctx) {
 		if(ctx.getInvokeAPIStatus()<0){
			//System.out.println("Failed");
			
		} else if ("getCommonCodeList".equalsIgnoreCase(ctx.getApiName())) {
			Element eleOutputCommonCode = ctx.getOutputXml().getDocumentElement();
			NodeList nlOutputCommonCode = eleOutputCommonCode.getElementsByTagName("CommonCode");
			String strAllowChargeNames = null;
			
			for(int iCommnCode = 0; iCommnCode< nlOutputCommonCode.getLength();iCommnCode++){
				strAllowChargeNames = ((Element)nlOutputCommonCode.item(iCommnCode)).getAttribute("CodeLongDescription");
			}
			//System.out.println("strAllowChargeNames "+strAllowChargeNames);
			namesArry = strAllowChargeNames.split(";");
			for (int n = 0; n < namesArry.length; n++) {
				strAllowChargeNames = namesArry[n];
				namesMap.put(n, strAllowChargeNames);
			}
			
			int ct = namesMap.size();
			if(ct > 0){
				Element eleChargeNameList=getModel("getChargeNameList_output");
				 if(!YRCPlatformUI.isVoid(eleChargeNameList)){
					 NodeList nl = eleChargeNameList.getElementsByTagName("ChargeName");
					 for(int k=0;k<nl.getLength();k++){
						 Element chargeNameEle = (Element) nl.item(k);
						 String chargeName = chargeNameEle.getAttribute("ChargeName");
						 String chargeCtgy = chargeNameEle.getAttribute("ChargeCategory");
						 if(chargeCtgy.equalsIgnoreCase("Discount") && chargeName != null && !namesMap.containsValue(chargeName)){
							 eleChargeNameList.removeChild(chargeNameEle);
						 }
					 }
				 }
				 //System.out.println("eleChargeNameList "+VSIXmlUtils.getElementXMLString(eleChargeNameList));
			}
			
			
			 
		}
 		super.handleApiCompletion(ctx);
 	}
 	
 	
 	@Override
 	public void postSetModel(String namespace) {
 		if("getChargeNameList_output".equalsIgnoreCase(namespace)){
		    Element eleChargeNameList=getModel("getChargeNameList_output");
		   
		    if(!YRCPlatformUI.isVoid(eleChargeNameList)){
		    	NodeList nl = eleChargeNameList.getElementsByTagName("ChargeName");
		    	
		    	for(int k=0;k<nl.getLength();k++){
		    	
		    		Element chrgeNameEle = (Element)nl.item(k);
		    	
		    		String chargeName = chrgeNameEle.getAttribute("ChargeName");
					 String chargeCtgy = chrgeNameEle.getAttribute("ChargeCategory");
					 if(chargeCtgy.equalsIgnoreCase("Discount") && chargeName != null && namesMap.size() > 0 && !namesMap.containsValue(chargeName)){
						 eleChargeNameList.removeChild(chrgeNameEle);
					 }
		    	}
		    	repopulateModel("getChargeNameList_output");
		    }
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
    	// Start: OMS-661 Customer Appeasement amount validation
    	//Logic for header change should not be more than order total
    	Element eleOrderLineDetails = this.getModel("ChargeDetails");
    	if ("applyBttn".equals(fieldName) && !(YRCPlatformUI.isVoid(eleOrderLineDetails))){
    		//System.out.println("ChargeDetails output"+YRCXmlUtils.getString(eleOrderLineDetails) );
    		if(!YRCPlatformUI.isVoid(eleOrderLineDetails))
    		{
    			Element eleLineCharge = null;
    			Double totalLineCharge=0.0;
    			Double totalHeaderCharge=0.0;
    			Double orderTotal=0.0;
    			Element elePriceInfo=YRCXmlUtils.getChildElement(eleOrderLineDetails,"PriceInfo");
    			orderTotal=YRCXmlUtils.getDoubleAttribute(elePriceInfo,"TotalAmount");
    			Element eleHeaderCharges = YRCXmlUtils.getChildElement(eleOrderLineDetails,
    			"HeaderCharges");
    			if(!YRCPlatformUI.isVoid(eleHeaderCharges)){
    			//Iterator itrHeaderCharges = YRCXmlUtils.getChildren(eleHeaderCharges);
    			NodeList headerChargeList = eleHeaderCharges.getElementsByTagName(VSIConstants.E_HEADER_CHARGE);
        		int noOfHeaderCharge = headerChargeList.getLength();
        		for(int i=0;i<noOfHeaderCharge;i++)
        		{
        			Element orderHeaderEle = (Element) headerChargeList.item(i);	
        			if(orderHeaderEle.getAttribute("ChargeCategory").equalsIgnoreCase("CUSTOMER_APPEASEMENT")||orderHeaderEle.getAttribute("ChargeCategory").equalsIgnoreCase("Discount"))
    				{
        				if(YRCPlatformUI.isVoid(orderHeaderEle.getAttribute("PreviousValue"))){
        				//System.out.println("charge is "+YRCXmlUtils.getDoubleAttribute(orderHeaderEle,"ChargeAmount"));
        				Double tempChargeAmount=YRCXmlUtils.getDoubleAttribute(orderHeaderEle,"ChargeAmount");
        			    //totalHeaderCharge=totalHeaderCharge+YRCXmlUtils.getDoubleAttribute(orderHeaderEle,"ChargeAmount");
        				totalHeaderCharge=totalHeaderCharge+tempChargeAmount;
        				}
    				}
        		}
        		if(totalHeaderCharge>orderTotal){
        			YRCPlatformUI.showError("Error","Header total cannot exceed order total");
        			YRCValidationResponse response = new YRCValidationResponse();
    	    		response.setStatusCode(YRCValidationResponse.
    	    		YRC_VALIDATION_ERROR);
    	    		return response;
        		}
        		else
        		{
        			repopulateModel("ChargeDetails");
        			//System.out.println("ChargeDetails Model output"+YRCXmlUtils.getString(eleOrderLineDetails));
        			
        		}
        		
    			/*
    			// Logic for proration of header charges to line charges
    			NodeList eleOrderLineList=eleOrderLineDetails.getElementsByTagName("OrderLine");
    			int noOfOrderLines=eleOrderLineList.getLength();
    			if(noOfOrderLines==1&& noOfHeaderCharge>0){
    				for(int i=0;i<noOfHeaderCharge;i++){
    					Element orderHeaderEle = (Element) headerChargeList.item(i);
    					Element eleOrderline=(Element) eleOrderLineList.item(i);
    					Element eleLineCharges = YRCXmlUtils.getChildElement(eleOrderline,"LineCharges");
    					System.out.println("this are line charges"+YRCXmlUtils.getString(eleLineCharges));
    					Element eleOrderLineCharge = YRCXmlUtils.createChild(eleLineCharges, "LineCharge");
    					//eleOrderLineCharge
    					
    					VSIXmlUtils.copyAttributes(orderHeaderEle,eleOrderLineCharge);
    					if(eleOrderLineCharge.getAttribute("ChargeCategory").equalsIgnoreCase("CUSTOMER_APPEASEMENT"))
    					{
    						eleOrderLineCharge.setAttribute("ChargeName", "CUSTOMER_APPEASEMENT");
    						eleOrderLineCharge.setAttribute("ChargePerLine", eleOrderLineCharge.getAttribute("ChargeAmount"));
    						eleOrderLineCharge.setAttribute("IsBillable","Y");
    						eleOrderLineCharge.setAttribute("IsDiscount","Y");
    						eleOrderLineCharge.setAttribute("IsManual","Y");
    						System.out.println("thihs is before copying"+YRCXmlUtils.getString(eleOrderLineCharge));
    					}
    					
    					//System.out.println("thihs is after copying"+YRCXmlUtils.getString(eleOrderLineCharge));
    					//VSIXmlUtils.removeAttributes(orderHeaderEle);
    				}
    			}*/
    			
    			}
    			
    			// Logic for line charge should not be more than order total
    			Element eleLineCharges = YRCXmlUtils.getChildElement(eleOrderLineDetails,
    			"LineCharges");
    			Element eleLineTotals = YRCXmlUtils.getChildElement(eleOrderLineDetails,
    			"LineOverallTotals");
    			double unitPrice = YRCXmlUtils.getDoubleAttribute(eleLineTotals,"UnitPrice");
    			double qty = YRCXmlUtils.getDoubleAttribute(eleLineTotals, "PricingQty");
    			double discount = YRCXmlUtils.getDoubleAttribute(eleLineTotals,"Discount");
    			double lineTotal = (qty*unitPrice) - discount;
    			Iterator itrLineCharges = YRCXmlUtils.getChildren(eleLineCharges);
    			while (itrLineCharges.hasNext()) {
    				eleLineCharge = (Element) itrLineCharges.next();
    				if(eleLineCharge.getAttribute("ChargeCategory").equalsIgnoreCase("CUSTOMER_APPEASEMENT")||eleLineCharge.getAttribute("ChargeCategory").equalsIgnoreCase("Discount"))
    				{
    					if(YRCPlatformUI.isVoid(eleLineCharge.getAttribute("PreviousValue"))){	
    				double dblChargeValue = YRCXmlUtils.getDoubleAttribute(eleLineCharge,"ChargeAmount");
    				totalLineCharge=totalLineCharge+dblChargeValue;
    					}
    			}
    			}
    		if(totalLineCharge>lineTotal){
    			YRCPlatformUI.showError("Error","Charge total cannot exceed line total");
    			YRCValidationResponse response = new YRCValidationResponse();
	    		response.setStatusCode(YRCValidationResponse.
	    		YRC_VALIDATION_ERROR);
	    		return response;
    		}
    			
    		}
    	}
    	// End: OMS-661 Customer Appeasement amount validation
		// TODO Create and return a response.
		return super.validateButtonClick(fieldName);
    }
    @Override
    public boolean preCommand(YRCApiContext apiContext) {
    	
    	String apiName = apiContext.getApiName();
    	
    	if(apiName.equalsIgnoreCase(VSIApiNames.API_CHANGE_ORDER)){
    		
    		Document order = apiContext.getInputXml();
    		Element orderEle = order.getDocumentElement();
    		//System.out.println("Printing Input XML for "+apiName+" :\n" + VSIXmlUtils.getElementXMLString(orderEle));
    		
    		NodeList hrdChargeNL = orderEle.getElementsByTagName(VSIConstants.E_HEADER_CHARGE);
    		if(hrdChargeNL.getLength() > 0){
    			
    			
    			int hdrChrgeIndex = 0;
    		
    			for(int i = 0; i < hrdChargeNL.getLength() ;i++){
    			
    				Element hrdCharge = (Element) hrdChargeNL.item(i);
    			
    				if(hrdCharge != null){
    					String chrgeCatgry = hrdCharge.getAttribute(VSIConstants.A_CHARGE_CATEGORY);
    				    				
    					if(chrgeCatgry.equalsIgnoreCase(VSIConstants.DISCOUNT)||chrgeCatgry.equalsIgnoreCase("CUSTOMER_APPEASEMENT")){
    						
    						Element orderDetailEle = getModel("ChargeDetails");
    		    			//System.out.println("\n Printing Order XML :\n" + VSIXmlUtils.getElementXMLString(orderDetailEle));
    		    			
    		    			/*Element chargeNameList = getModel("getChargeNameList_output");
    		    			NodeList chargeNames = chargeNameList.getElementsByTagName(VSIConstants.A_CHARGE_NAME);
    		    			if(chargeNames.getLength() > 0){
    		    				for(int index = 0; index < chargeNames.getLength(); ){
    		    					
    		    				}
    		    			}*/
    						hdrChrgeIndex = i;
    						String isNewCharge = null;
    						String discountedAmt = hrdCharge.getAttribute(VSIConstants.A_CHARGE_AMOUNT);
    						if(hrdCharge.hasAttribute(VSIConstants.A_IS_NEW_CHARGE)){
        						isNewCharge = hrdCharge.getAttribute(VSIConstants.A_IS_NEW_CHARGE);
    						}
        				
    						HashMap<String, String> prortdLineDiscountMap = prorateHeaderDiscount(orderDetailEle,discountedAmt,isNewCharge);
    						String chargeKey = hrdCharge.getAttribute(VSIConstants.A_CHARGE_NAME_KEY);
        				
        					Element orderLines = order.createElement(VSIConstants.E_ORDER_LINES);
    						orderEle.appendChild(orderLines);
    					
    						NodeList orderLineNL = orderDetailEle.getElementsByTagName(VSIConstants.E_ORDER_LINE);
        					
    						if(orderLineNL.getLength() > 0){
    							
        						for(int m = 0; m < orderLineNL.getLength() ; m++ ){
        							
        							Element orderDetailLine = (Element) orderLineNL.item(m);
        							
        							Element orderLine = order.createElement(VSIConstants.E_ORDER_LINE);
        					        orderLines.appendChild(orderLine);
        					        
        					        String olk = orderDetailLine.getAttribute(VSIConstants.A_ORDER_LINE_KEY);
        					        orderLine.setAttribute(VSIConstants.A_ORDER_LINE_KEY, olk);
        					        orderLine.setAttribute("Action", "MODIFY");
        					        
        					        Element lineCharges = order.createElement(VSIConstants.E_LINE_CHARGES);
        					        orderLine.appendChild(lineCharges);
        					        
        					        NodeList lineChargeNL = orderDetailLine.getElementsByTagName(VSIConstants.E_LINE_CHARGE);
        					        
        					        if(lineChargeNL.getLength() > 0){
        					        	for(int f = 0 ; f < lineChargeNL.getLength(); f ++ ){
        					        		
        					        		Element lineChargeEle = (Element) lineChargeNL.item(f);
        					        		
        					        		String chrCtg =  lineChargeEle.getAttribute(VSIConstants.A_CHARGE_CATEGORY);
        					        		String chrNme = lineChargeEle.getAttribute(VSIConstants.A_CHARGE_NAME);
        					        		
        					        		if(chrCtg.equalsIgnoreCase(VSIConstants.DISCOUNT) && 
        					        				chrNme.equalsIgnoreCase(VSIConstants.MANUAL_DISCOUNT_$) ){
        					        			
        					        			String previousValue = lineChargeEle.getAttribute(VSIConstants.A_CHARGE_AMOUNT);
        					        			Double dPreviousValue = new Double("0.0");
        					        			if(previousValue != null && !previousValue.trim().equalsIgnoreCase(""))
        					        			dPreviousValue = Double.parseDouble(previousValue);
        					        			Double dTotalLineDiscount = Double.parseDouble(prortdLineDiscountMap.get(olk).toString());
        					        			
        					        			if(dPreviousValue > 0){
        					        					dTotalLineDiscount = dTotalLineDiscount + dPreviousValue;
        					        			}
        					        			
        					        			lineChargeEle.setAttribute("PreviousValue", dPreviousValue.toString());
        					        			lineChargeEle.removeAttribute(VSIConstants.A_CHARGE_AMOUNT);
        					        			lineChargeEle.setAttribute(VSIConstants.A_CHARGE_PER_LINE, dTotalLineDiscount.toString());
        					        			Node copyNode = order.importNode(lineChargeEle, true);
        					        			lineCharges.appendChild(copyNode);
        					        				
        					        			}else{
        					        				
        					        				if(prortdLineDiscountMap.size() > 0){
            	        							
        					        					Element lineCharge = order.createElement(VSIConstants.E_LINE_CHARGE);
        					        					lineCharges.appendChild(lineCharge);
            	        					        
        					        					lineCharge.setAttribute(VSIConstants.A_CHARGE_CATEGORY, chrgeCatgry);
        					        					lineCharge.setAttribute(VSIConstants.A_CHARGE_NAME, VSIConstants.MANUAL_DISCOUNT_$);
            	        					        	lineCharge.setAttribute(VSIConstants.A_CHARGE_NAME_KEY, chargeKey);
            	        					        	lineCharge.setAttribute(VSIConstants.A_CHARGE_PER_LINE, prortdLineDiscountMap.get(olk));
            	        					        	lineCharge.setAttribute(VSIConstants.A_CHARGE_PER_UNIT, "0");
            	        					        	lineCharge.setAttribute(VSIConstants.A_IS_NEW_CHARGE, isNewCharge);
            	        					        
            	        					        	Element extn = order.createElement("Extn");
            	        					        	lineCharge.appendChild(extn);
            	        					        	extn.setAttribute("IsTranLevel", "Y");
            	        					        
            	        					    
        					        				}
        					        			}
        					        		}
        					        	}else{
        					        	
        					        		if(prortdLineDiscountMap.size() > 0){
        	        							
        					        			Element lineCharge = order.createElement(VSIConstants.E_LINE_CHARGE);
        	        					        lineCharges.appendChild(lineCharge);
        	        					        
        	        					        lineCharge.setAttribute(VSIConstants.A_CHARGE_CATEGORY, chrgeCatgry);
        	        					        lineCharge.setAttribute(VSIConstants.A_CHARGE_NAME, VSIConstants.MANUAL_DISCOUNT_$);
        	        					        lineCharge.setAttribute(VSIConstants.A_CHARGE_NAME_KEY, chargeKey);
        	        					        lineCharge.setAttribute(VSIConstants.A_CHARGE_PER_LINE, prortdLineDiscountMap.get(olk));
        	        					        lineCharge.setAttribute(VSIConstants.A_CHARGE_PER_UNIT, "0");
        	        					        lineCharge.setAttribute(VSIConstants.A_IS_NEW_CHARGE, isNewCharge);
        	        					        
        	        					        Element extn = order.createElement("Extn");
        	        					        lineCharge.appendChild(extn);
        	        					        extn.setAttribute("IsTranLevel", "Y");
        	        					     }
        					        	}
        							}
        						}
    						Node hdrChrge = orderEle.getElementsByTagName(VSIConstants.E_HEADER_CHARGE).item(hdrChrgeIndex);
    			    		Element hrdChrgsEle = (Element) orderEle.getElementsByTagName(VSIConstants.E_HEADER_CHARGES).item(0);
    			    		hrdChrgsEle.removeChild(hdrChrge);
    			    		
    			    		//System.out.println("After proration logic order" +YRCXmlUtils.getString(orderEle));
    			    		//System.out.println("After proration logic order" +YRCXmlUtils.getString(orderDetailEle));
    			    		
        					}
        				}
    				}
    			}
    		}
    	return super.preCommand(apiContext);
    }
    
    private HashMap<String, String> prorateHeaderDiscount(Element orderDetailEle,
			String discountedAmt, String isNewCharge) {
    	HashMap<String, String> prortdLineDisMap = new HashMap<String, String>();
		if(isNewCharge != null && isNewCharge.equalsIgnoreCase("Y") && Double.parseDouble(discountedAmt) == 0){
			return prortdLineDisMap;
		}else{
			Double dHdrDiscnt = Double.parseDouble(discountedAmt);
			Element orderTotalEle = (Element) orderDetailEle.getElementsByTagName(VSIConstants.E_OVERALL_TOTALS).item(0);
			Double dOrderTotal = Double.parseDouble(orderTotalEle.getAttribute(VSIConstants.A_ORDER_TOTAL));
			NodeList orderLineNL = orderDetailEle.getElementsByTagName(VSIConstants.E_ORDER_LINE);
			for(int k = 0 ; k < orderLineNL.getLength(); k++){
				Element orderLineEle = (Element) orderLineNL.item(k);
				String olk = orderLineEle.getAttribute(VSIConstants.A_ORDER_LINE_KEY);
				if(dHdrDiscnt > 0){
				
				Element lineTotalEle = (Element) orderLineEle.getElementsByTagName(VSIConstants.A_LINE_OVERALL_TOTALS).item(0);
				Double dLineTotal = Double.parseDouble(lineTotalEle.getAttribute(VSIConstants.A_LINE_TOTAL));
				Double dLineDiscAmt = (dLineTotal / dOrderTotal) * dHdrDiscnt;
				
				prortdLineDisMap.put(olk, dLineDiscAmt.toString());
				
				}else{
					prortdLineDisMap.put(olk, discountedAmt);
				}
				
			}
		}
		
		return prortdLineDisMap;
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
	 * Create and return the binding data for advanced table columns added to
	 * the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }
}
