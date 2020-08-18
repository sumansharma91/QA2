package com.vsi.oms.api.web;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIShipmentStatusUpdateToWEB {
	private YFCLogCategory log = YFCLogCategory.instance(VSIShipmentStatusUpdateToWEB.class);
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
	public void shipUpdateToWEB(YFSEnvironment env, Document inXML)
	throws Exception {

					
			Element rootElement = inXML.getDocumentElement();
            String orderHeaderKeyStr=rootElement.getAttribute("OrderHeaderKey");
			
			
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
     
			if(strEntryType.equalsIgnoreCase("WEB")){
			Calendar calendar = Calendar.getInstance();
			String statusDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
			
			Document OrderStatusToWEB = XMLUtil.createDocument("Order");
			Element OrderStatusToWEBEle = OrderStatusToWEB.getDocumentElement();
			OrderStatusToWEBEle.setAttribute("OrderNo",strOrderNo);
			OrderStatusToWEBEle.setAttribute("TimeStamp",statusDate);
			if(strStatus.equalsIgnoreCase("Shipped")){
				OrderStatusToWEBEle.setAttribute("Status","Complete");

			api.executeFlow(env,"VSIWEBStatusUpdate",OrderStatusToWEB);
			
			}
			else{
				String strMinStatus=outDocOrderElement.getAttribute("MinOrderStatus");
				//double dbMinStatus=Double.parseDouble(strMinStatus);
				if(strMinStatus.equalsIgnoreCase("3200.500") || (strMinStatus.equalsIgnoreCase(VSIConstants.STATUS_SOM_READY_FOR_PICKUP))){
					strStatus="Ready For Pickup";
				}
				else if(strMinStatus.equalsIgnoreCase("2160.200")||strMinStatus.equalsIgnoreCase("2160.400")
						||strMinStatus.equalsIgnoreCase("3200.401")||strMinStatus.equalsIgnoreCase("3200.411")){
					strStatus="In Transit to Store";	
				}
				else if (strMinStatus.equalsIgnoreCase("1500")||strMinStatus.equalsIgnoreCase("1100")||
						strMinStatus.equalsIgnoreCase("2160.100")||strMinStatus.equalsIgnoreCase("2160")
						||strMinStatus.equalsIgnoreCase("3200")||strMinStatus.equalsIgnoreCase("1500.100")
						||strMinStatus.equalsIgnoreCase("1500.200") || (strMinStatus.equalsIgnoreCase(VSIConstants.STATUS_SOM_READY_FOR_PICKUP_PROCESS))){
					strStatus="Processing";
				}
				OrderStatusToWEBEle.setAttribute("Status",strStatus);
				api.executeFlow(env,"VSIWEBStatusUpdate",OrderStatusToWEB);

			}
			
	}
	}
}
