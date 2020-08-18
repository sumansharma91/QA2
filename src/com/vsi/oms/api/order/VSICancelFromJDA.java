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

public class VSICancelFromJDA {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelFromJDA.class);
	YIFApi api;
	public void vsiCancelFromJDA(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		
		if(null!=inXML.getDocumentElement() && null!=inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)){
		
			Element rootElement = inXML.getDocumentElement();
			String strCustPoNo = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String modReasonCode = 	rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_CODE);
			
			
			Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
			Element eleOrderLine = getOrderLineListInput.getDocumentElement();
			eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPoNo);
			
		
			
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSICancelFromJDA.xml");
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
			env.clearApiTemplates();
			
			

			if(null != (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0)){
				
						
						cancelOrder(env,outDoc,modReasonCode);
						
						
					
				
				
				
			}


			
			
		}

	}//end method

	private void cancelOrder(YFSEnvironment env, Document outDoc, String modReasonCode) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document changeOrderInput = XMLUtil.createDocument("Order");
		Element orderElement = changeOrderInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_CODE, modReasonCode);
		Element orderLinesElement = changeOrderInput.createElement("OrderLines");
		orderElement.appendChild(orderLinesElement);
	
		Element elementOrder = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		
		String oHK = elementOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		
		
		NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();
	
		
		for(int i=0;i<linelength;i++){
		
			Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			String strOrderLineKey = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			Element orderLineElement = changeOrderInput.createElement("OrderLine");
			orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			orderLineElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			orderLinesElement.appendChild(orderLineElement);
			
		}
		
		////System.out.println("Input to Change Order" +XMLUtil.getXMLString(changeOrderInput));
		if(log.isDebugEnabled()){
			log.debug("Input to Change Order :" + XMLUtil.getXMLString(changeOrderInput));
		}
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
		
	}

}
