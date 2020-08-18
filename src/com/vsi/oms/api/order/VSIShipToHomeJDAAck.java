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
public class VSIShipToHomeJDAAck {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIShipToHomeJDAAck.class);
	YIFApi api;
	public void vsiScheduleTO(YFSEnvironment env, Document XML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiScheduleTO================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(XML));
		}
		try{
			//System.out.println("order list 0");			
			Element rootElement = XML.getDocumentElement();
			String orderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute("OrderHeaderKey", orderHeaderKey);
			//System.out.println("OrderHeaderKey is" + orderHeaderKey);

	//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIScheduleOnSuccess.xml");
				Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
				//System.out.println("order list");

				Element nlOrder	= (Element)outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				//System.out.println("order list 1");

				Document inXML = XMLUtil.getDocumentFromElement(nlOrder);
        		
				//System.out.println("order list 2");

				
				
			Map<String, String> orderLinekeyMap = new HashMap<String, String>();
			
			
			Element orderLines = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
			
			NodeList orderLineList = orderLines.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			NodeList extnList = orderLines.getElementsByTagName(VSIConstants.ELE_EXTN);
			for (int i = 0; i < orderLineList.getLength(); i++) {
				//System.out.println("order list 3");

				Element orderLineElement = (Element) orderLineList.item(i);
				Element extnElement = (Element) extnList.item(i);
				String minLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
				String maxLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
				String custPoNo = orderLineElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
				//String extnPub = extnElement.getAttribute("ExtnPublished");
				String lineType = orderLineElement.getAttribute("LineType");

				Boolean flag = orderLinekeyMap.containsKey(custPoNo);
				String val ="";
				
					
					if(minLineStatus.equalsIgnoreCase("3200") && maxLineStatus.equalsIgnoreCase("3200")
							&& lineType.equalsIgnoreCase("SHIP_TO_HOME"))
					{
						//System.out.println("order list 4");
						if(flag == false)
							orderLinekeyMap.put(custPoNo, "SCHED");
						else{
							//System.out.println("order list 5");
							val = orderLinekeyMap.get(custPoNo);
							
							if(!(val.equalsIgnoreCase("SCHED"))){
								orderLinekeyMap.put(custPoNo, "NA");
							}
						}
							
						
					}
					
		// end flag false
				
			if(lineType.equalsIgnoreCase("SHIP_TO_HOME")){
				//System.out.println("order list 6");
			for (Map.Entry<String,String> entry : orderLinekeyMap.entrySet()) {
				//System.out.println("order list 7");
			    String key = entry.getKey();
			    String value = entry.getValue();
			   Element rootEle = inXML.getDocumentElement();
			   rootEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, key);
			    
			    	
			    	api = YIFClientFactory.getInstance().getApi();
					api.executeFlow(env, "VSISendBOPUSToJDA", inXML);
			    }
			}
			    	
			    
			
			}
					
			
		}catch (YFSException e) {
			e.printStackTrace();
		//	YFSException e1 = new YFSException();
			String error = e.getMessage();
			String errorCode = e.getErrorCode();
			String errorDesc = e.getErrorDescription();
			throw new YFSException(
					errorCode,
					errorDesc,
					error);
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");

		}
	}
}
