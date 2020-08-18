package com.vsi.oms.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICancelOnNoAutoAck extends VSIBaseCustomAPI implements VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelOnNoAutoAck.class);
	
	public void VSICancelNoAckOrders(YFSEnvironment env, Document inXml)
	{
		try
		{
				int orderTotalNumberOfRecords = 0;
				
				Element orderEle = (Element) inXml.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);				
				String orderHeaderKey= orderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);		
		
				Document getOrderListInputDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleOrder = getOrderListInputDoc.getDocumentElement();
				eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
				
				Document getOrderListOutputDoc = VSIUtils.invokeAPI(env, TEMPLATE_ORDER_LIST_ORDER_MONITOR,VSIConstants.API_GET_ORDER_LIST, getOrderListInputDoc);			

				Element getOrderListOutputEle = (Element) getOrderListOutputDoc.getElementsByTagName(ELE_ORDER_LIST).item(0);				
				orderTotalNumberOfRecords = Integer.parseInt(getOrderListOutputEle.getAttribute(VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS));
				
				log.info("orderTotalNumberOfRecords => "+orderTotalNumberOfRecords);
				// Below is for reference
				
				
				//Element nOrderLines = (Element)getOrderListOutputDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
				NodeList orderLineNode = getOrderListOutputDoc.getElementsByTagName(ELE_ORDER_LINE);
				int orderLineNodeLength = orderLineNode.getLength();
				
				log.info("orderLineNodeLength => "+orderLineNodeLength);	
				
				
				if((orderTotalNumberOfRecords > 0) && (orderLineNodeLength > 0))
				{
				
				for(int l=0; l< orderLineNodeLength; l++)
				{
					Element orderLineEle = (Element) getOrderListOutputDoc.getElementsByTagName(ELE_ORDER_LINE).item(l);
					String conditionVariable1 = orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1);			
				
					log.info("conditionVariable1 => "+conditionVariable1);					
	                if(conditionVariable1.equalsIgnoreCase("Y")) {
						changeOrderLineCancellation(orderHeaderKey, orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1), orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY), env);
	                }
	                else {
	                	changeOrderLineCancellation(orderHeaderKey, "N", orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY), env);
	                }
				}
				}
				
				

    	}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void changeOrderLineCancellation(String orderHeaderKey, String ConditionVariable1, String orderLineKey, YFSEnvironment env)
	{
		try
		{ 
			int totalNumberOfRecords = 0;
			log.info("Invoking changeOrderLineCancellation => OH Key"+orderHeaderKey+"Con Var"+ConditionVariable1+"orderLineKey"+orderLineKey);
			
			if(ConditionVariable1.equalsIgnoreCase("Y")){
				Document getShipmentListInputDoc = SCXmlUtil.createDocument(ELE_SHIPMENT);
				Element eleShipment = getShipmentListInputDoc.getDocumentElement();
				eleShipment.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
				
				Document getShipmentListOuputDoc = VSIUtils.invokeAPI(env, TEMPLATE_SHIPMENT_LIST, API_GET_SHIPMENT_LIST, getShipmentListInputDoc);
				Element shipments = (Element) getShipmentListOuputDoc.getElementsByTagName(ELE_SHIPMENTS).item(0);					
				totalNumberOfRecords = Integer.parseInt(shipments.getAttribute(ATTR_TOTAL_NUMBER_OF_RECORDS));
				NodeList shipmentNode = getShipmentListOuputDoc.getElementsByTagName(ELE_SHIPMENT);				
				int shipmentNodeLength = shipmentNode.getLength();
				log.info("totalNumberOfRecords => OH Key"+totalNumberOfRecords);
				if (totalNumberOfRecords > 0) {				
				for(int l=0; l< shipmentNodeLength; l++)
				{
					Element shipmentEle = (Element) getShipmentListOuputDoc.getElementsByTagName(ELE_SHIPMENT).item(l);
					String shipmentkey = shipmentEle.getAttribute(ATTR_SHIPMENT_KEY);
					
					Document getChangeShipmentInputDoc = SCXmlUtil.createDocument(ELE_SHIPMENT);
					Element eleChngShipment = getChangeShipmentInputDoc.getDocumentElement();
					eleChngShipment.setAttribute(ATTR_SHIPMENT_KEY, shipmentkey);
					eleChngShipment.setAttribute(ATTR_ACTION, "Cancel");
					Document docOutChangeShipment = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_SHIPMENT,
							getChangeShipmentInputDoc);
					
				}
				}
		
		}
		
		
		Document changeOrderDoc = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleOrder = changeOrderDoc.getDocumentElement();
		eleOrder.setAttribute(ATTR_OVERRIDE, VSIConstants.FLAG_Y);
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		Element eleOrderLines = SCXmlUtil.createChild(eleOrder, ELE_ORDER_LINES);
		Element elemOrderLine = SCXmlUtil.createChild(eleOrderLines, ELE_ORDER_LINE);
		elemOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, orderLineKey);
		elemOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_CANCEL);
		//Prod Issue Fix: Start
		eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, NO_STORE_PICK);
		//Prod Issue Fix: End
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, changeOrderDoc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
