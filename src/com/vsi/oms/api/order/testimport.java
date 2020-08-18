package com.vsi.oms.api.order;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 * 1. call scheduleorder service if there are schedule lines and call Transfer service if there are TO lines
2. if for a particular custpono if there are any BO lines, do not call service

Algo:
1. navigate through orderlines, check if minlinestatus is 1500, if yes put a value in map (custpono, value sched), if the map already exists and the value is diff from sched, make it as na
1. navigate through orderlines, check if minlinestatus is 2160 & extnpublished!=y, if yes put a value in map (custpono, value to), if the map already exists and the value is diff from  to, make it as na
1. navigate through orderlines, check if minlinestatus is 1300, if yes put a value in map (custpono, value na)
4. end of the loop, loop through the map, if any to call to service, if any sched call sched service
 *
 */
public class testimport {
	
	private YFCLogCategory log = YFCLogCategory.instance(testimport.class);
	YIFApi api;
	public Document vsiScheduleTO(YFSEnvironment env, Document XML)
			throws Exception {
		log.info("================Inside vsiScheduleTO================================");
		log.debug("Printing Input XML :" + XmlUtils.getString(XML));		
		
						
			Element rootElement = XML.getDocumentElement();
			String orderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute("OrderHeaderKey", orderHeaderKey);
			
	//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIScheduleOnSuccess.xml");
				Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
				Element nlOrder	= (Element)outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				//String strXML = XMLUtil.getElementXMLString(nlOrder);
				//Document ndoc1 = XMLUtil.createDocument(strXML);
				
				Document inXML = XMLUtil.getDocumentFromElement(nlOrder);
        		
               
				return 	inXML;	
			

}
}