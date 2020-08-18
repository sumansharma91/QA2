package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * This class is called on success for ConfirmShipment for both BR1 and BR2. 
 *
 */
public class VSIPublishShipToJDA {

	private YFCLogCategory log = YFCLogCategory.instance(VSIStoreChangeOrder.class);
	YIFApi api;
	public Document vsiStoreChangeOrder(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
	
		Element rootElement = inXML.getDocumentElement();
		String sOHK = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		
		
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListInput.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		
		
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIPublishShipToJDA.xml");
			Document inService = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			env.clearApiTemplates();
			
			
			  Element rootEle = inService.getDocumentElement();
			  
			  
			  Element orderEle = (Element)rootEle.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			  String orderType = orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			  if(orderType.equalsIgnoreCase("WEB")){
				  Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);
					
					String custPoNo = shipmentLineElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
			   rootEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, custPoNo);
			   
			
			   Document outDoc =	api.executeFlow(env, "VSIParseCreateShipment", inService);
			   return outDoc;
			  }
			  
			  else{
				  return inService;
			  }

	
	
	
	
	
	}
	
}
