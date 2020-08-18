package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendEmailForWMSCancel 
{
	
	// added for ARE-327
	private YFCLogCategory log = YFCLogCategory.instance(VSICallCenterCustomerPoGen.class);
	YIFApi api;

	 /**
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 */
	public Document vsiSendEmailForWMSCancel(YFSEnvironment env, Document inXML) throws YFSException
	    {
	    	
	    	
		  Document orderListDoc = SCXmlUtil.createDocument("OrderList");
		  if(log.isDebugEnabled()){
			  log.verbose("Printing Input XML :" + XmlUtils.getString(inXML));
			  log.info("================Inside vsiSendEmailForWMSCancel================================");
		  }
			try {
			
				
				Element rootElement = inXML.getDocumentElement();
				Element orderLinesEle = SCXmlUtil.getChildElement(rootElement, "OrderLines");
				NodeList orderLineList = orderLinesEle.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				
				int orderLineLength = orderLineList.getLength();
								
				//String orderType = rootElement.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				//String entryType = rootElement.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			
			if(null!=rootElement){
				if(orderLineLength > 0)
				{
					Element orderLineElement1 = (Element) orderLinesEle.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
					String sLineType = orderLineElement1.getAttribute(VSIConstants.ATTR_LINE_TYPE);
					if(!sLineType.equalsIgnoreCase("SHIP_TO_STORE"))
					{

						for(int i=0;i< orderLineLength;i++)
						{
							Element orderLineElement = (Element)orderLinesEle.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
							String changeInQty = orderLineElement.getAttribute("ChangeInOrderedQty");
							if(changeInQty.contains("-"))
							{
								String[] changeInQtyBuffer = changeInQty.split("-");
								changeInQty = changeInQtyBuffer[1];
							}
							//orderLineElement.setAttribute("OrderedQty", changeInQty);
							orderLineElement.setAttribute("OrderedQty", changeInQty);
							//Element linePriceInfo = SCXmlUtil.getChildElement(orderLineElement, "LinePriceInfo");
							Element lineOverallTotals = SCXmlUtil.getChildElement(orderLineElement, "LineOverallTotals");
							//System.out.println(SCXmlUtil.getString(lineOverallTotals));
							if(!YFCCommon.isVoid(lineOverallTotals))
							{
								lineOverallTotals.setAttribute("ExtendedPrice", lineOverallTotals.getAttribute("UnitPrice"));
							}
							
						}
						
						String emailType = null;
						String messageType = null;
						String orderType = inXML .getDocumentElement().getAttribute("OrderType");
						if("Marketplace".equalsIgnoreCase(orderType)){
							emailType = "FTC_CANCEL";
							messageType = "FTC_CANCEL";
						}
						else
						{
							emailType = "FTCBD";
							messageType = "CANCEL";
							//rootElement.setAttribute("EmailType", "FTCBD");
							//rootElement.setAttribute("MessageType", "CANCEL");
						}
						//Document orderListDoc = SCXmlUtil.createDocument("OrderList");
						setPriceInfo(rootElement);
						rootElement.setAttribute("MessageType", messageType);
						SCXmlUtil.importElement(orderListDoc.getDocumentElement(), rootElement);
						Element rootOutDoc = orderListDoc.getDocumentElement();
						rootOutDoc.setAttribute("Action", "CANCEL");
						if(null!=emailType) 
							rootOutDoc.setAttribute("EmailType", emailType);
						if(null!=messageType) {
							rootOutDoc.setAttribute("MessageType", messageType);
						}
						
						VSIUtils.invokeService(env, VSIConstants.VSI_CNCL_PUBLISH_TO_JDA, orderListDoc);
					}
				}
				
		}

		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");
		}
		
	   return orderListDoc;
    }

	private void setPriceInfo(Element eleOrder) {
		Element elePriceInfo = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PRICE_INFO);
		double dCustCredit = Math.abs(SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_CHANGE_IN_TOTAL_AMOUNT));
		double dNewOrderTotal = SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT);
		double dOriginalPrice = dCustCredit + dNewOrderTotal;
		
		SCXmlUtil.setAttribute(elePriceInfo, VSIConstants.ATTR_PREVIOUS_ORDER_TOTAL, dOriginalPrice);
		SCXmlUtil.setAttribute(elePriceInfo, VSIConstants.ATTR_NEW_ORDER_TOTAL, dNewOrderTotal);
		SCXmlUtil.setAttribute(elePriceInfo, VSIConstants.ATTR_CUSTOMER_CREDIT, dCustCredit);
	}

		
	    

}
