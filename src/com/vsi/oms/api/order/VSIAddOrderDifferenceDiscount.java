package com.vsi.oms.api.order;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIAddOrderDifferenceDiscount {

	private YFCLogCategory log = YFCLogCategory.instance(VSIAddOrderDifferenceDiscount.class);

	public Document addOrderDiffAndStampExtnAttributes(YFSEnvironment env,Document inDoc){

		try {
			if(log.isDebugEnabled()){
				log.debug("Inside VSIAddOrderDifferenceDiscount:addOrderDiffAndStampExtnAttributes input is "+
						XMLUtil.getXMLString(inDoc));
			}
			Element eleOrder=inDoc.getDocumentElement();
			String strDocumentType=eleOrder.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			String strEntryType=eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			if(log.isDebugEnabled()){
				log.debug("DocumentType : "+strDocumentType +" EntryType: "+strEntryType);
			}
			if(("0001").equals(strDocumentType) && (VSIConstants.ENTRYTYPE_CC).equals(strEntryType)){

				Element eleOverallTotals=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_OVERALL_TOTALS);
				double dGrandTotal=SCXmlUtil.getDoubleAttribute(eleOverallTotals, "GrandTotal", 0.00);
				if(log.isDebugEnabled()){
					log.debug("GrandTotal  is : " + dGrandTotal);
				}
				double dOrderMaxChargeLimit=0.00;
				double dDifference=0.00;
				boolean bApplyDiscount=false;
				double dExtnShipmentCharge=0.00;
				double dExtnOrderDiscount=0.00;
				double dExtnShipmentDiscount=0.00;
				double dExtnOrderTax=0.00;

				List<Element> lPaymentMethod=XMLUtil.getElementListByXpath(inDoc, "/Order/PaymentMethods/PaymentMethod");
				for(Element elePaymentMethod:lPaymentMethod){
					double dMaxChargeLimit=SCXmlUtil.getDoubleAttribute(elePaymentMethod, VSIConstants.ATTR_MAX_CHARGE_LIMIT, 0.00);
					dOrderMaxChargeLimit+=dMaxChargeLimit;
				}
				if(log.isDebugEnabled()){
					log.debug("MaxChargeLimit total is : " + dOrderMaxChargeLimit);
				}
				dDifference=dGrandTotal-dOrderMaxChargeLimit;
				dDifference=Math.round(dDifference*100D)/100D;
				if(log.isDebugEnabled()){
					log.debug("Difference is : "+dDifference);
				}
				if(dDifference>=0.01 && dDifference<=0.99){
					bApplyDiscount=true;
				}
				//creating input for changeOrder
				Document docChangeOrderInput=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleOrderChangeIp=docChangeOrderInput.getDocumentElement();
				eleOrderChangeIp.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

				// calculating  dHeaderChargeTotal
				List<Element> lHeaderCharge=XMLUtil.getElementListByXpath(inDoc, "/Order/HeaderCharges/HeaderCharge");
				for (Element eleHeaderCharge : lHeaderCharge) {
					double dChargeAmountHC=SCXmlUtil.getDoubleAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT , 0.00);
					String strChargeCategoryHeader=eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
					String strChargeName=eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
					if(log.isDebugEnabled()){
						log.debug("ChargeAmount : "+dChargeAmountHC + " ChargeCategory" + strChargeCategoryHeader);
					}
					if(VSIConstants.SHIPPING_CHARGE_CTGY.equals(strChargeCategoryHeader)){
						dExtnShipmentCharge+=dChargeAmountHC;
					}
					else if(VSIConstants.DISCOUNT.equals(strChargeCategoryHeader)){
						dExtnOrderDiscount+=dChargeAmountHC;
					}
					else if("Shipping Appeasement".equals(strChargeName)){
						dExtnShipmentDiscount+=dChargeAmountHC;
					}
				}
				Element eleOrderExtn=SCXmlUtil.createChild(eleOrderChangeIp, VSIConstants.ELE_EXTN);
				if(log.isDebugEnabled()){
					log.debug("ExtnShipmentCharge : "+dExtnShipmentCharge);
				}
				dExtnShipmentCharge=Math.round(dExtnShipmentCharge*100D)/100D;
				eleOrderExtn.setAttribute("ExtnShipmentCharge", String.valueOf(dExtnShipmentCharge));
				if(log.isDebugEnabled()){
					log.debug("ExtnOrderDiscount : "+dExtnOrderDiscount);
				}
				dExtnOrderDiscount=Math.round(dExtnOrderDiscount*100D)/100D;
				eleOrderExtn.setAttribute("ExtnOrderDiscount", String.valueOf(dExtnOrderDiscount));
				if(log.isDebugEnabled()){
					log.debug("ExtnShipmentDiscount : "+dExtnShipmentDiscount);
				}
				dExtnShipmentDiscount=Math.round(dExtnShipmentDiscount*100D)/100D;
				eleOrderExtn.setAttribute("ExtnShipmentDiscount", String.valueOf(dExtnShipmentDiscount));

				// calculating  dExtnOrderTax
				List<Element> lHeaderTax=XMLUtil.getElementListByXpath(inDoc, "/Order/HeaderTaxes/HeaderTax");
				for (Element eleHeaderTax : lHeaderTax) {
					double dTaxHT=SCXmlUtil.getDoubleAttribute(eleHeaderTax,VSIConstants.ATTR_TAX , 0.00);
					dExtnOrderTax+=dTaxHT;
				}
				if(log.isDebugEnabled()){
					log.debug("ExtnOrderTax : "+dExtnOrderTax);
				}
				dExtnOrderTax=Math.round(dExtnOrderTax*100D)/100D;
				eleOrderExtn.setAttribute("ExtnOrderTax", String.valueOf(dExtnOrderTax));


				Element eleOrderLinesChangeIp=SCXmlUtil.createChild(eleOrderChangeIp, VSIConstants.ELE_ORDER_LINES);
				List<Element> lOrderLine=XMLUtil.getElementListByXpath(inDoc, "/Order/OrderLines/OrderLine");
				int iCountOfLines=lOrderLine.size();
				for (int i=0;i<iCountOfLines;i++) {
					double dExtnLineTotal=0.00;
					double dExtnTotalDiscount=0.00;

					Element eleOrderLineChangeIP=SCXmlUtil.createChild(eleOrderLinesChangeIp, VSIConstants.ELE_ORDER_LINE);
					Element eleOrderLineExtnChangeIP=SCXmlUtil.createChild(eleOrderLineChangeIP, VSIConstants.ELE_EXTN);

					Element eleOrderLine=lOrderLine.get(i);
					Element eleComputedPrice=SCXmlUtil.getChildElement(eleOrderLine, "ComputedPrice");
					eleOrderLineExtnChangeIP.setAttribute("ExtnTotalTax",eleComputedPrice.getAttribute("Tax"));
					if(log.isDebugEnabled()){
						log.debug("ExtnTotalTax  "+ eleComputedPrice.getAttribute("Tax"));
					}
					eleOrderLineChangeIP.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));

					if(bApplyDiscount){
						if(log.isDebugEnabled()){
							log.debug("applying the discount : ");
						}
						Element eleLineCharges=SCXmlUtil.createChild(eleOrderLineChangeIP, VSIConstants.ELE_LINE_CHARGES);
						Element eleLineCharge=SCXmlUtil.createChild(eleLineCharges, VSIConstants.ELE_LINE_CHARGE);
						eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, String.valueOf(dDifference));
						eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, VSIConstants.DISCOUNT);
						eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME, "OrderDifferenceDiscount");
						eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE, String.valueOf(dDifference));
						bApplyDiscount=false;

						dExtnLineTotal=SCXmlUtil.getDoubleAttribute(eleComputedPrice, "LineTotal", 0.00);
						dExtnTotalDiscount=SCXmlUtil.getDoubleAttribute(eleComputedPrice, "Discount", 0.00);

						dExtnLineTotal=dExtnLineTotal-dDifference;
						dExtnTotalDiscount=dExtnTotalDiscount+dDifference;
						eleOrderLineExtnChangeIP.setAttribute("ExtnLineTotal",String.valueOf(dExtnLineTotal));
						eleOrderLineExtnChangeIP.setAttribute("ExtnTotalDiscount",String.valueOf(dExtnTotalDiscount));
					}
					else{
						if(log.isDebugEnabled()){
							log.debug("order difference discount not applied on the order ");
						}
						eleOrderLineExtnChangeIP.setAttribute("ExtnLineTotal",eleComputedPrice.getAttribute("LineTotal"));
						eleOrderLineExtnChangeIP.setAttribute("ExtnTotalDiscount",eleComputedPrice.getAttribute("Discount"));
					}

				} //end of for loop
				
				if(log.isDebugEnabled()){
					log.debug("VSIAddOrderDifferenceDiscount: Input for ChangeOrder : "+XMLUtil.getXMLString(docChangeOrderInput));
				}
				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrderInput);
				
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error in VSIAddOrderDifferenceDiscount:addOrderDiffAndStampExtnAttributes " + e.toString());
		}

		return inDoc;
	}
}
