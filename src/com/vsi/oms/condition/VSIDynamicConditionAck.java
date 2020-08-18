package com.vsi.oms.condition;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.*;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
public class VSIDynamicConditionAck implements YCPDynamicCondition
{
	
	YIFApi api;
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
	
		boolean old = false;
		String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
		String dType=VSIConstants.DOCUMENT_TYPE;
		try{
				 YFCDocument inputXML = YFCDocument.getDocumentFor(inXML);
			
			
			//YFCElement shipmentEle =  inputXML.getDocumentElement();
			YFCElement OrderEle = inputXML.getElementsByTagName("OrderStatusChange").item(0);	
					
			String CustPoNo = OrderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			Element eleOrderLine = getOrderListInput.createElement(VSIConstants.ELE_ORDER_LINE);
			eleOrder.appendChild(eleOrderLine);
			eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, CustPoNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
			eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIDynamicCondition.xml");
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			env.clearApiTemplates();
			Element orderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			if(null == orderEle){
				
				eleOrder.setAttribute("OrderNo", CustPoNo);
				eleOrder.removeChild(eleOrderLine);
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIDynamicCondition.xml");
				outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
				env.clearApiTemplates();
				orderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			}//end if null check	
			
			if(orderEle != null){
				String orderType = orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);

				if(orderType.equalsIgnoreCase("STORE"))
				{
				
					old = true;
					
				}
			
			}
			
		
	} catch (Exception e){
		e.printStackTrace();
		throw new YFSException(
				"EXTN_ERROR",
				"EXTN_ERROR",
				"Order ELement not found");

	}

		return old;
	}

			
}                                                              
