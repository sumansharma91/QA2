package com.vsi.oms.api.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIOnChangeOrderStatusWEB {
	private YFCLogCategory log = YFCLogCategory.instance(VSIOnChangeOrderStatusWEB.class);
	YIFApi api;
	private Properties props;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public void orderStatusForWEB(YFSEnvironment env, Document inXML)
	throws Exception {

		String orderHeaderKeyStr = null;		
			Element rootElement = inXML.getDocumentElement();
			Element orderAudits = (Element)rootElement.getElementsByTagName("OrderAudit").item(0);
			if(orderAudits != null){
				orderHeaderKeyStr=orderAudits.getAttribute("OrderHeaderKey");
			}else{
				orderHeaderKeyStr=rootElement.getAttribute("OrderHeaderKey");
			}
			if (YFCObject.isVoid(orderHeaderKeyStr)){
				
				Element OrderEle = (Element)rootElement.getElementsByTagName("Order").item(0);
				orderHeaderKeyStr=OrderEle.getAttribute("OrderHeaderKey");
			}
            
			if(orderHeaderKeyStr != null){
				Document getOrderListDoc=XMLUtil.createDocument("Order");
				Element getOrderListDocEle = getOrderListDoc.getDocumentElement();
				getOrderListDocEle.setAttribute("OrderHeaderKey",orderHeaderKeyStr);
				
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderListWEB.xml");
				Document outDoc  = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListDoc);	
				env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
				Element outDocOrderElement = (Element)outDoc.getElementsByTagName("Order").item(0);
				
				String strStatus=outDocOrderElement.getAttribute("Status");
				String strOrderNo=outDocOrderElement.getAttribute("OrderNo");
				String strEntryType=outDocOrderElement.getAttribute("EntryType");
				String strMaxStatus=outDocOrderElement.getAttribute("MaxOrderStatus");
				String strMinStatus=outDocOrderElement.getAttribute("MinOrderStatus");
				//only send the status update for Website Order

				if(strEntryType.equalsIgnoreCase("WEB")){
				Calendar calendar = Calendar.getInstance();
				String statusDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
				
				Document OrderStatusToWEB = XMLUtil.createDocument("Order");
				Element OrderStatusToWEBEle = OrderStatusToWEB.getDocumentElement();
				OrderStatusToWEBEle.setAttribute("OrderNo",strOrderNo);
				OrderStatusToWEBEle.setAttribute("TimeStamp",statusDate);
							
				String webStatus=null;
				String strWebMaxStatus=null;
				String strWebMinStatus=null;
				
				if(strMinStatus.equalsIgnoreCase("3200.500") || (strMinStatus.equalsIgnoreCase(VSIConstants.STATUS_SOM_READY_FOR_PICKUP))){
					strWebMinStatus="Ready For Pickup";
				}
				else if(strMinStatus.equalsIgnoreCase("2160.200")||strMinStatus.equalsIgnoreCase("2160.400")
						||strMinStatus.equalsIgnoreCase("3200.401")||strMinStatus.equalsIgnoreCase("3200.411")){
					strWebMinStatus="In Transit to Store";	
				}
				else if (strMinStatus.equalsIgnoreCase("1500")||strMinStatus.equalsIgnoreCase("1100")||
						strMinStatus.equalsIgnoreCase("2160.100")||strMinStatus.equalsIgnoreCase("2160")||
						strMinStatus.equalsIgnoreCase("2160.10")||strMinStatus.equalsIgnoreCase("2160.20")
						||strMinStatus.equalsIgnoreCase("3200")||strMinStatus.equalsIgnoreCase("1500.100")
						||strMinStatus.equalsIgnoreCase("1500.200") || strMinStatus.equalsIgnoreCase("3350") 
						|| strMinStatus.equalsIgnoreCase("3350.10") || strMinStatus.equalsIgnoreCase("3350.20")){
					strWebMinStatus="Processing";
				}
				
				if(strMaxStatus.equalsIgnoreCase("3200.500") || (strMaxStatus.equalsIgnoreCase(VSIConstants.STATUS_SOM_READY_FOR_PICKUP))){
					strWebMaxStatus="Ready For Pickup";
				}
				else if(strMaxStatus.equalsIgnoreCase("2160.200")||strMaxStatus.equalsIgnoreCase("2160.400")
						||strMaxStatus.equalsIgnoreCase("3200.401")||strMaxStatus.equalsIgnoreCase("3200.411")){
					strWebMaxStatus="In Transit to Store";	
				}
				else if (strMaxStatus.equalsIgnoreCase("1500")||strMaxStatus.equalsIgnoreCase("1100")||
						strMaxStatus.equalsIgnoreCase("2160.100")||strMaxStatus.equalsIgnoreCase("2160") 
						|| strMaxStatus.equalsIgnoreCase("2160.10") || strMaxStatus.equalsIgnoreCase("2160.20") ||
						strMaxStatus.equalsIgnoreCase("3200")||strMaxStatus.equalsIgnoreCase("1500.100")
						||strMaxStatus.equalsIgnoreCase("1500.200") || strMaxStatus.equalsIgnoreCase("3350")
						|| strMaxStatus.equalsIgnoreCase("3350.10") || strMaxStatus.equalsIgnoreCase("3350.20")){
					strWebMaxStatus="Processing";
				}
				
				else if ("3700".equalsIgnoreCase(strMaxStatus) || "3700.01.20.ex".equalsIgnoreCase(strMaxStatus) || "3700.01.10.ex".equalsIgnoreCase(strMaxStatus) || "3700.01".equalsIgnoreCase(strMaxStatus) || "3700.02".equalsIgnoreCase(strMaxStatus) || "3950.01".equalsIgnoreCase(strMaxStatus)){
					strWebMaxStatus="Complete";
				}
				
				
				//OrderStatusToWEBEle.setAttribute("Status",strStatus);
				if(strMinStatus.equalsIgnoreCase(strMaxStatus)){
					webStatus=strWebMaxStatus;
				}
				else if (strWebMaxStatus.equalsIgnoreCase("Processing")){
					
					webStatus=strWebMaxStatus;
				}
				else if(strWebMaxStatus.equalsIgnoreCase("Complete")){
					webStatus=strWebMinStatus;
				}
				else{
					webStatus="Partially "+strWebMaxStatus;
				}
				OrderStatusToWEBEle.setAttribute("Status",webStatus);
		
				api.executeFlow(env,"VSIWEBStatusUpdate",OrderStatusToWEB);
				
				}
			}
			
			
	}
}
