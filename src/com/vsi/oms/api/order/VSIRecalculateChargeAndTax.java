package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/*
 * OMS-793 changes
 * This class will be invoked when any cancellation message is received by OMS.
 * It will re-calculate the line charges and taxes for the all lines having Action=Cancel
 */
public class VSIRecalculateChargeAndTax {

	YIFApi api;
	
	/**
	 * Instance of logger
	 */
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIRecalculateChargeAndTax.class.getName());

	
	/*
	 * This method will calculate the tax and charge for each line based on the quantities received and append
	 * order line tag with the updated details to the input changeOrder XML.
	 */
	public Document modifyTaxAndCharge(YFSEnvironment env, Document inXML)
	throws Exception {

		if(log.isDebugEnabled()){
			log.debug("================Inside VSIRecalculateChargeAndTax================================");
		}
		
		if(null != inXML.getDocumentElement() && null != inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)){
			NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			
			Element orderLinesEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
			
			boolean modifyFlag = false;
			int orderLineLength = orderLineList.getLength();
			
			if(null != orderLineList && orderLineLength > 0){
				
				
				for (int i = 0; i < orderLineLength; i++) {
					
					Element eleOrderLine = (Element) orderLineList.item(i);
					String orderedQty = eleOrderLine.getAttribute("OrderedQty");

					/*
					 * If ordered Qty is "0" no need to re calculate taxes and charges as OMS will set all the values to '0'.
					 * Below logic will get executed only if Action is set to Cancel at line level
					 */
					if (!"0".equals(orderedQty) && "CANCEL".equals(eleOrderLine.getAttribute("Action"))) {
						
						if(log.isDebugEnabled()){
							log.debug("================Action is Cancel at line level and line is getting partially cancelled==========");
						}
						
						String olk = eleOrderLine.getAttribute("OrderLineKey");
						Document getOrderLineList = getOrderLineList(env, olk);
						
						if (null != getOrderLineList) {
							Element eleOrderLineOp = (Element) getOrderLineList.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
							String originalOrderedQty = eleOrderLineOp.getAttribute("OrderedQty");
							
							Element orderLineEle = XMLUtil.createElement(inXML, "OrderLine", "");
							Element lineChargesEle = XMLUtil.createElement(inXML, "LineCharges", "");
							Element lineTaxesEle = XMLUtil.createElement(inXML, "LineTaxes", "");
							
							NodeList lineChargeList = eleOrderLineOp.getElementsByTagName("LineCharge");
							NodeList lineTaxList = eleOrderLineOp.getElementsByTagName("LineTax");

							if (lineChargeList != null && lineChargeList.getLength() > 0){
								
								if(log.isDebugEnabled()){
									log.debug("================Line Charge Exists for OrderLinekey "+olk+"===========");
								}
								
								for (int j = 0; j < lineChargeList.getLength(); j++) {
									
									Element lineChargeEle = XMLUtil.createElement(inXML, "LineCharge", "");
									Element elelineCharge = (Element) lineChargeList.item(j);
									String originalLineCharge = elelineCharge.getAttribute("ChargePerLine");

									Double dOriginalLineCharge =Double.valueOf(originalLineCharge);
									Double dOriginalOrderedQty=Double.valueOf(originalOrderedQty);
									Double dOrderedQty=Double.valueOf(orderedQty);
									Double chargePerUnit = (dOriginalLineCharge / dOriginalOrderedQty);
							         							         
									//Calculate new charge per line value based on the cancelled qty
									Double chargePerUnitRoundOff = (double) Math.round(chargePerUnit * 100)/ 100;
									Double newChargePerLine = chargePerUnitRoundOff * dOrderedQty;
									
									lineChargeEle.setAttribute("ChargeName", elelineCharge.getAttribute("ChargeName"));
									lineChargeEle.setAttribute("ChargeCategory", elelineCharge.getAttribute("ChargeCategory"));
									lineChargeEle.setAttribute("ChargePerLine", newChargePerLine.toString());
									lineChargesEle.appendChild(lineChargeEle);																
								}
								modifyFlag = true;
							}
							
							if (lineTaxList != null && lineTaxList.getLength() > 0){
								
								if(log.isDebugEnabled()){
									log.debug("================Line Tax Exists for OrderLinekey "+olk+"===========");
								}
								
								for (int j = 0; j < lineTaxList.getLength(); j++) {
									
									Element lineTaxEle = XMLUtil.createElement(inXML, "LineTax", "");
									Element elelineTax = (Element) lineTaxList.item(j);
									String originalTax = elelineTax.getAttribute("Tax");
									
									Double dOriginalTax = Double.valueOf(originalTax);
									Double dOriginalOrderedQty = Double.valueOf(originalOrderedQty);
									Double dOrderedQty = Double.valueOf(orderedQty);
									Double taxPerUnit = (dOriginalTax / dOriginalOrderedQty);
							         							         
									//Calculate new charge per line value based on the cancelled qty
									Double taxPerUnitRoundOff = (double) Math.round(taxPerUnit * 100)/ 100;
									Double newTax = taxPerUnitRoundOff * dOrderedQty;
									
									lineTaxEle.setAttribute("TaxName", elelineTax.getAttribute("TaxName"));
									lineTaxEle.setAttribute("Tax", newTax.toString());
									lineTaxesEle.appendChild(lineTaxEle);																
								}
								modifyFlag = true;
							}
							
							/*
							 * If line taxes and charges are present for the respective order line key in changeOrder XML,
							 * add a order line tag with Action as Modify with the updated charges and taxes.
							 */
							if (modifyFlag) {
								orderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, olk);
								orderLineEle.setAttribute("Action", "MODIFY");
								orderLineEle.appendChild(lineChargesEle);
								orderLineEle.appendChild(lineTaxesEle);
								orderLinesEle.appendChild(orderLineEle);
							}

						}

					}					
				}	
			}
		}
		
		return inXML;
	}	
	
	/*
	 * Invoke getOrderLineList API to fetch line charges and taxes
	 */
	public Document getOrderLineList(YFSEnvironment env, String orderLineKey) throws YIFClientCreationException, YFSException, RemoteException, ParserConfigurationException {
		
		Document orderLine = XMLUtil.createDocument("OrderLine");
		Element OrderLineEle = orderLine.getDocumentElement();
		OrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);

		env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSIGetLineChargeAndTax.xml");
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,orderLine);
		env.clearApiTemplates();	
		
		NodeList orderLineList = outDoc
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		
		if (null != orderLineList) {
			return outDoc;
		}
		
		return null;
		
	}
		
}
