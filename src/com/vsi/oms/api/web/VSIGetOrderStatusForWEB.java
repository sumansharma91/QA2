package com.vsi.oms.api.web;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.api.order.VSITransferOrderCreateShipmentBR2;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetOrderStatusForWEB {
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetOrderStatusForWEB.class);
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
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @throws ParserConfigurationException 
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public void orderStatusForWEB(YFSEnvironment env, Document inXML) throws  Exception
	 {
	
			
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
			
			//only send the status update for Website Order
			
			if(strEntryType.equalsIgnoreCase("WEB")){
			Calendar calendar = Calendar.getInstance();
			String statusDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
			
			Document OrderStatusToWEB = XMLUtil.createDocument("Order");
			Element OrderStatusToWEBEle = OrderStatusToWEB.getDocumentElement();
			OrderStatusToWEBEle.setAttribute("Status","Processing");
			OrderStatusToWEBEle.setAttribute("OrderNo",strOrderNo);
			OrderStatusToWEBEle.setAttribute("TimeStamp",statusDate);
			api.executeFlow(env,"VSIWEBStatusUpdate",OrderStatusToWEB);
			}
					
	}
}
