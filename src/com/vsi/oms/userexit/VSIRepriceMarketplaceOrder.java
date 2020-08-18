package com.vsi.oms.userexit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;

public class VSIRepriceMarketplaceOrder implements YFSOrderRepricingUE {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRepriceMarketplaceOrder.class);

/** triggering the order change ON_CANCEL event
 * @author Sindhura
 */

	@Override
	public Document orderReprice(YFSEnvironment env, Document inXML) throws YFSUserExitException{
		
		try {
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside orderReprice================================");
		}
		Element orderElement = inXML.getDocumentElement();
		DecimalFormat dfRMUp = new DecimalFormat(VSIConstants.DEC_FORMAT);
		//OMS-1689 : Start 
		String strEnterpriseCode=orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		Document docInput=null;
		docInput = XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODETYPE_MARKETPLACE_ENT);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEnterpriseCode);
		if(log.isDebugEnabled()){
		log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
		}
		Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInput);
		if(log.isDebugEnabled()){
		log.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
		}
		Element commonCodeListElement = docCommonCodeListOP.getDocumentElement();
		String strEntType=null;
		if(commonCodeListElement.hasChildNodes()){
			Element eleCommonCode1 = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
			strEntType = SCXmlUtil.getAttribute(eleCommonCode1, VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
		}
		
		
		if (!YFCObject.isVoid(strEntType))
		{	
		if(VSIConstants.ENT_TYPE_1.equals(strEntType)){
			
			Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);		    
		    for(Element eleOrderLine:alOrderLines){
		    
		    	Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN);
				Double dExtnMktplaceLineShipAmt = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPAMT);
				Double dExtnMktplaceLineShipDiscount = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPDISCOUNT);
				Double dExtnMktplaceLineShipTax = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPTAX);
		    	
		    	double attrOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, VSIConstants.ATTR_ORD_QTY);
		    	double attrOriginalQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, VSIConstants.ATTR_ORIGINAL_ORDERED_QTY);
    			if((attrOrderedQty < attrOriginalQty)){
					DecimalFormat dfRMDown = new DecimalFormat(VSIConstants.DEC_FORMAT); 
					dfRMDown.setRoundingMode(RoundingMode.DOWN);
					Double dbReducedAmount = 0.00;
					if(attrOrderedQty != 0){
						double costperunit = dExtnMktplaceLineShipAmt/attrOrderedQty;
						String strCostperunit= dfRMDown.format(costperunit);
						Double dbChargePerLine= ((Double.valueOf(strCostperunit)) * attrOrderedQty);
						dbReducedAmount = dExtnMktplaceLineShipAmt - dbChargePerLine;
						if(dbReducedAmount<0){
							dbReducedAmount = 0.00;
						}
						dExtnMktplaceLineShipAmt = dbChargePerLine;
					}else {
						dbReducedAmount = dExtnMktplaceLineShipAmt;
						dExtnMktplaceLineShipAmt = 0.00;
						dExtnMktplaceLineShipDiscount = 0.00;
					}
	    			
					DecimalFormat df = new DecimalFormat(VSIConstants.DEC_FORMAT);
					df.setRoundingMode(RoundingMode.DOWN);					
					Element eleLineTaxes = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_TAXES);
					Element eleLineTax = SCXmlUtil.getChildElement(eleLineTaxes, VSIConstants.ELE_LINE_TAX);
					if(!YFCObject.isVoid(eleLineTax)){
						if(eleLineTax.getAttribute(VSIConstants.ATTR_TAX_NAME).equalsIgnoreCase("Sales Tax")){
							Element eleLineTaxExtn = SCXmlUtil.getChildElement(eleLineTax, VSIConstants.ELE_EXTN);
							double dExtnLineTaxPerUnit = SCXmlUtil.getDoubleAttribute(eleLineTaxExtn, "ExtnTaxPerUnit");
							dExtnLineTaxPerUnit = dExtnLineTaxPerUnit * attrOrderedQty;
							dExtnMktplaceLineShipTax = dExtnLineTaxPerUnit;
							SCXmlUtil.setAttribute(eleLineTax, VSIConstants.ATTR_TAX, df.format(dExtnLineTaxPerUnit));
						}
					}// end of sales tax if
			    	
			    	
			    	SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPAMT, dExtnMktplaceLineShipAmt);
			    	SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPDISCOUNT,  dExtnMktplaceLineShipDiscount);
			    	SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPTAX,  dExtnMktplaceLineShipTax);
			    	
			    	//Reducing the header level shipping charge, shipping discount by the corresponding amount
					Element eleHeaderCharges = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_HEADER_CHARGES);
					ArrayList<Element> alHeaderCharge= SCXmlUtil.getChildren(eleHeaderCharges, VSIConstants.ELE_HEADER_CHARGE);
					for (Element eleHeaderCharge : alHeaderCharge) {
						double dChrgAmt = 0.00;
						double dHdrChrg = 0.00;
						dChrgAmt = SCXmlUtil.getDoubleAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT);
						if (eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY).equalsIgnoreCase("Shipping")) {								
								dHdrChrg = dChrgAmt - dbReducedAmount;
								if(dHdrChrg<0){
									dHdrChrg = 0.00;
								}
								SCXmlUtil.setAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT,dHdrChrg);
						} else if (eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY).equalsIgnoreCase("Shipping Discount")) {
							dHdrChrg = dChrgAmt - dExtnMktplaceLineShipDiscount;
							if(dHdrChrg<0){
								dHdrChrg = 0.00;
							}
							SCXmlUtil.setAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT,dHdrChrg);
						}// end of else if
					}//end of for
					 
					// Reducing the Shipping Tax by corresponding amount
					Element eleHeaderTaxes = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_HEADER_TAXES);
					Element eleHeaderTax= SCXmlUtil.getChildElement(eleHeaderTaxes, VSIConstants.ELE_HEADER_TAX);			
					if(!YFCObject.isVoid(eleHeaderTax)){
						if(eleHeaderTax.getAttribute(VSIConstants.ATTR_TAX_NAME).equalsIgnoreCase("Shipping Tax")){
							double dHdrTax=0.00;
							double dbHdrTax=0.00;
							dbHdrTax = SCXmlUtil.getDoubleAttribute(eleHeaderTax, VSIConstants.ATTR_TAX);
							dHdrTax=dbHdrTax-dExtnMktplaceLineShipTax;
							if(dHdrTax<0){
								dHdrTax=0.00;
							}
							SCXmlUtil.setAttribute(eleHeaderTax, VSIConstants.ATTR_TAX, dHdrTax);
						}
					}
    			}
		    }// end of for statement for looping through OrderLines 
		} else if(VSIConstants.ENT_TYPE_2.equals(strEntType)){
			Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		    for(Element eleOrderLine:alOrderLines){
		    	double attrOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, VSIConstants.ATTR_ORD_QTY);
		    	double attrOriginalQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, VSIConstants.ATTR_ORIGINAL_ORDERED_QTY);
	    		if((attrOrderedQty == 0) 
	    				&& (attrOrderedQty < attrOriginalQty)){
	    			Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN);
	    			Double dExtnMktplaceLineShipAmt = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPAMT);
	    			SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPAMT, VSIConstants.ZERO_DOUBLE);
	
	    			Double dExtnMktplaceLineShipDiscount = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPDISCOUNT);
	    			SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPDISCOUNT,  VSIConstants.ZERO_DOUBLE);
	
	    			Double dExtnMktplaceLineShipTax = SCXmlUtil.getDoubleAttribute(eleExtn, VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPTAX);
	    			SCXmlUtil.setAttribute(eleExtn,VSIConstants.ATTR_EXTN_MKTPLACELINE_SHIPTAX,  VSIConstants.ZERO_DOUBLE);
	
	    			//Reducing the header level shipping charge, shipping discount by the corresponding amount
	    			Element eleHeaderCharges = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_HEADER_CHARGES);
	    			ArrayList<Element> alHeaderCharge= SCXmlUtil.getChildren(eleHeaderCharges, VSIConstants.ELE_HEADER_CHARGE);
	    			for(Element eleHeaderCharge:alHeaderCharge){
	    				
	    				double dHdrChrg=0.00;
    					double dbHdrChrg=0.00;
    					dbHdrChrg=SCXmlUtil.getDoubleAttribute(eleHeaderCharge, VSIConstants.ATTR_CHARGE_AMOUNT);
	
	    				if(eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY).equalsIgnoreCase("Shipping")){
	    					
	    					dHdrChrg=dbHdrChrg-dExtnMktplaceLineShipAmt;
	    					if(dHdrChrg<0){
	    						dHdrChrg=0.00;
	    					}
	    					SCXmlUtil.setAttribute(eleHeaderCharge, VSIConstants.ATTR_CHARGE_AMOUNT, dHdrChrg);
	    				}//end of if
	    				else if(eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY).equalsIgnoreCase("Shipping Discount")){
	    					
	    					dHdrChrg=dbHdrChrg-dExtnMktplaceLineShipDiscount;
	    					if(dHdrChrg<0){
	    						dHdrChrg=0.00;
	    					}
	
	    					SCXmlUtil.setAttribute(eleHeaderCharge, VSIConstants.ATTR_CHARGE_AMOUNT, dHdrChrg);
	
	    				}//end of else if
	    			}//end of for
	
	    			// Reducing the Shipping Tax by corresponding amount
	    			Element eleHeaderTaxes = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_HEADER_TAXES);
	    			Element eleHeaderTax= SCXmlUtil.getChildElement(eleHeaderTaxes, VSIConstants.ELE_HEADER_TAX);
	    			
	    			if(!YFCObject.isVoid(eleHeaderTax)){
	    				if(eleHeaderTax.getAttribute(VSIConstants.ATTR_TAX_NAME).equalsIgnoreCase("Shipping")){
	    					double dHdrTax=0.00;
							double dbHdrTax=0.00;
							dbHdrTax = SCXmlUtil.getDoubleAttribute(eleHeaderTax, VSIConstants.ATTR_TAX);
							dHdrTax=dbHdrTax-dExtnMktplaceLineShipTax;
							if(dHdrTax<0){
								dHdrTax=0.00;
							}
	    					SCXmlUtil.setAttribute(eleHeaderTax, VSIConstants.ATTR_TAX, dHdrTax);
	    				}
	    			}
		    		
	
		    		Element eleLineTaxes = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_TAXES);
		    		Element eleLineTax = SCXmlUtil.getChildElement(eleLineTaxes, VSIConstants.ELE_LINE_TAX);
	
		    		if(!YFCObject.isVoid(eleLineTax)){
		    			if(eleLineTax.getAttribute(VSIConstants.ATTR_TAX_NAME).equalsIgnoreCase("Sales Tax")){
	
		    				Element eleLineTaxExtn = SCXmlUtil.getChildElement(eleLineTax, VSIConstants.ELE_EXTN);
		    				double strExtnLineTaxPerUnit = SCXmlUtil.getDoubleAttribute(eleLineTaxExtn, VSIConstants.ATTR_EXTN_LINE_TAX_PER_UNIT);
		    				double strOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, VSIConstants.ATTR_ORD_QTY);
		    				strExtnLineTaxPerUnit = strExtnLineTaxPerUnit * strOrderedQty;
	
		    				SCXmlUtil.setAttribute(eleLineTax, VSIConstants.ATTR_TAX, strExtnLineTaxPerUnit);
		    			}
		    		}
	    		}
	    	}
	    }
		}
		return inXML;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					e.getMessage(),
					e.getMessage(),
					"YFS Exception");
				
		} 
		//OMS-1689 : End 
	}//end of orderRepricing method	
}//end of class