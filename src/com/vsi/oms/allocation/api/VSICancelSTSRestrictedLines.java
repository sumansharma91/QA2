package com.vsi.oms.allocation.api;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICancelSTSRestrictedLines implements YIFCustomApi {

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	public void cancelSTSRestrictedLines(YFSEnvironment env, Document inXML) throws Exception{
		
		
		NodeList orderLineList = inXML
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int orderLineLength = orderLineList.getLength();
		HashMap<String, String> hmRestrictedLineItems = null;

        if(!YFCCommon.isVoid(env.getTxnObject(VSIConstants.TXN_OBJ_STS_ITEM_RESTRICTIONS))){
               hmRestrictedLineItems = (HashMap<String, String>) env.getTxnObject(VSIConstants.TXN_OBJ_STS_ITEM_RESTRICTIONS);

        }
		
		if(orderLineLength > 0 && !YFCCommon.isVoid(hmRestrictedLineItems))
		{
			Element orderEle = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_ORDER).item(0);
			String OHK = orderEle
					.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
			Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleChangeOrder = docChangeOrder.getDocumentElement();
			eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
			eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
			eleChangeOrder.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
			Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
			
			for (int i = 0; i < orderLineLength; i++) {
				
				Element orderLineEle = (Element) inXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)
						.item(i);
				
				String orderLineKey = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				
				// 1B changes to cancel back ordered STS lines - BEGIN
				if (hmRestrictedLineItems.containsKey(orderLineKey)) 
				{
					String qtyTobeCanceled =  (String)hmRestrictedLineItems.get(orderLineKey);
					String ordQty = orderLineEle.getAttribute(VSIConstants.ATTR_ORD_QTY);
					Double updatedQty = Double.parseDouble(ordQty) - Double.parseDouble(qtyTobeCanceled);
					Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
					eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, VSIConstants.SHIP_RESTRICTED);
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY,updatedQty.toString());
				}
				
			}
			
			if (eleChangeOrderLines.hasChildNodes()) {
				VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
						VSIConstants.API_CHANGE_ORDER, docChangeOrder);
			}
			
		}
		
		
	}
	
	
	
}
