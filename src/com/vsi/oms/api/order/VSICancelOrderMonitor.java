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

public class VSICancelOrderMonitor {

	private YFCLogCategory log = YFCLogCategory.instance(VSICancelOrderMonitor.class);
	YIFApi api;
	public void vsiCancelOrderMonitor(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
	
		//Element orderElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		//String oHK = 	orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		//Document getOrderLineListInput = XMLUtil.createDocument("Order");
		//Element eleOrder = getOrderListInput.getDocumentElement();
		//eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		
		Element orderStatusElement = (Element) inXML.getElementsByTagName("OrderStatus").item(0);		
		String oLK = orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
		String oHK = 	orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
		Element eleOrderLine = getOrderLineListInput.getDocumentElement();
		eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, oLK);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/OrderMonitorCancel.xml");
		Document outDoc  = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);					
		env.clearApiTemplates();
		
		NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();


		if(null != orderLineList){
		

			//changeOrder Input
			Document changeOrderInput = XMLUtil.createDocument("Order");
			Element changeOrderEle = changeOrderInput.getDocumentElement();
			//changeOrderEle.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			changeOrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
			changeOrderEle.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
			Element changeOrderLinesElement = changeOrderInput.createElement("OrderLines");
			changeOrderEle.appendChild(changeOrderLinesElement);

			Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
			Element changeOrderStatusEle = changeOrderStatusInput.getDocumentElement();
			changeOrderStatusEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
			changeOrderStatusEle.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
			changeOrderStatusEle.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "VSIRestock.0001.ex");
			Element changeOrderStatusLinesElement = changeOrderStatusInput.createElement("OrderLines");
			changeOrderStatusEle.appendChild(changeOrderStatusLinesElement);
			Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
			String custPO=orderLineEle.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
			Element orderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderNo=orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
					for(int i=0;i<linelength;i++){
				
				Element orderLineElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
				String minLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
				String maxLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
				
				int iminLine = Integer.parseInt(minLineStatus);
				int imaxLine = Integer.parseInt(maxLineStatus);

				
				if(iminLine == 3200.500 && imaxLine == 3200.500){
				
					Element changeOrderLineElement = changeOrderInput.createElement("OrderLine");	
					String strOrderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					changeOrderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
					changeOrderLineElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
					changeOrderLineElement.setAttribute("ModificationReasonCode", "AutoCancel");
					changeOrderLinesElement.appendChild(changeOrderLineElement);			
					
					Element changeOrderStatusLineElement = changeOrderStatusInput.createElement("OrderLine");	
					changeOrderStatusLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
					changeOrderStatusLineElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "9000.100");					
					changeOrderStatusLineElement.setAttribute("ModificationReasonCode", "AutoCancel");
					changeOrderStatusLinesElement.appendChild(changeOrderStatusLineElement);	
					
					
				}
			}// end for
		
			
			////System.out.println("Input to Change Order" +XMLUtil.getXMLString(changeOrderInput));
					if(log.isDebugEnabled()){
						log.debug("Printing Input XML :" + XMLUtil.getXMLString(changeOrderInput));
					}
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
			raiseAlert(env, oHK, orderNo, custPO);
		
		}// end if
		
		
		
	}
private void raiseAlert(YFSEnvironment env, String sOHK, String sOrderNo, String custPO) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Customer no show cancellation for Order:" +custPO);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Customer No Show");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Cancel");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Customer No Show");
		//InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_CANCEL_ORDER");
		
		
		Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
		
		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHK);
		
		InboxReferencesListElement.appendChild(InboxReferencesElement);
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
				
	}
	
}
