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

public class VSICancelPickedOrders extends VSIBaseCustomAPI implements VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelPickedOrders.class);
	public void cancelPickedOrders(YFSEnvironment env, Document inXml)
	{
		try
		{
				log.info("================Inside VSICancelPickedOrders Class and cancelPickedOrders Method================");
				log.info("Printing Input XML :"+SCXmlUtil.getString(inXml));
			
				Element orderStatusEle = (Element) inXml.getElementsByTagName(ELE_ORDER_STATUS).item(0);
				String originalOrderLineKey = orderStatusEle.getAttribute(ATTR_ORDER_LINE_KEY);
				Document getOrderLineListDoc = SCXmlUtil.createDocument(ELE_ORDER_LINE);
				Element eleOrderLine = getOrderLineListDoc.getDocumentElement();
				eleOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, originalOrderLineKey);
				eleOrderLine.setAttribute(ATTR_DOCUMENT_TYPE, ATTR_DOCUMENT_TYPE_SALES);	
				//Add template to fetch condition variable1 for Order Monitor Cancel on backroom picked status change				
				Document getOrderLineListOutputDoc = VSIUtils.invokeAPI(env, TEMPLATE_ORDER_LINE_LIST,API_GET_ORDER_LINE_LIST, getOrderLineListDoc);
				//SOM Restock changes -- Start				
				log.info("Printing getOrderLineList output: "+SCXmlUtil.getString(getOrderLineListOutputDoc));
				
				Element eleOrderLineList=getOrderLineListOutputDoc.getDocumentElement();
				Element eleOrdLine=(Element)eleOrderLineList.getElementsByTagName(ELE_ORDER_LINE).item(0);
				String strOrderedQty=eleOrdLine.getAttribute(ATTR_ORD_QTY);
				String strShipNode=eleOrdLine.getAttribute(ATTR_SHIP_NODE);
				String strCustPONo=eleOrdLine.getAttribute(ATTR_CUST_PO_NO);
				Element eleItem=SCXmlUtil.getChildElement(eleOrdLine, ELE_ITEM);
				String strItemId=eleItem.getAttribute(ATTR_ITEM_ID);
				//SOM Restock changes -- End
				Element eleExtn = (Element) getOrderLineListOutputDoc.getElementsByTagName(ELE_EXTN).item(0);				
				String lastPickDate = null;
				if(eleExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL) != "")
					lastPickDate = eleExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL);
				else
					lastPickDate = eleExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE);
				String orderHeaderKey = orderStatusEle.getAttribute(ATTR_ORDER_HEADER_KEY);
				
				SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
                Calendar calculatedOrderTime = Calendar.getInstance();
                calculatedOrderTime.setTime(formatter.parse(lastPickDate));
                if(log.isDebugEnabled())
                	log.info("calculatedOrderTime(lastPickDate) => "+calculatedOrderTime.getTime());
                Date localTime = new Date();
                SimpleDateFormat converter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
                converter.setTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE));
                String currTime = converter.format(localTime);
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTime(formatter.parse(currTime));
                if(log.isDebugEnabled())
                	log.info("currTime => "+ currTime + "currentTime => "+currentTime.getTime());   

                if (currentTime.compareTo(calculatedOrderTime) > 0)
                {
                	Document getOrderListInXML=XMLUtil.createDocument(ELE_ORDER);
    				Element getOrderListEle = getOrderListInXML.getDocumentElement();
    				getOrderListEle.setAttribute(ATTR_ORDER_HEADER_KEY,orderHeaderKey);
    				getOrderListEle.setAttribute(ATTR_DOCUMENT_TYPE, ATTR_DOCUMENT_TYPE_SALES);
					//Add template to fetch condition variable1 for Order Monitor Cancel on backroom picked status change
    				Document getOrderListOutXML = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORDER_LIST_FOR_ORDER_MONITOR, API_GET_ORDER_LIST, getOrderListInXML);
    				NodeList orderLineNode = getOrderListOutXML.getElementsByTagName(ELE_ORDER_LINE);
    				int orderLineNodeLength = orderLineNode.getLength();
    				if(orderLineNodeLength == 1)
    				{
    					Element orderLineEle = (Element) getOrderListOutXML.getElementsByTagName(ELE_ORDER_LINE).item(0);
    					String lineType = orderLineEle.getAttribute(ATTR_LINE_TYPE);
    					if(lineType.equalsIgnoreCase(LINETYPE_PUS) || lineType.equalsIgnoreCase(LINETYPE_STS))
    							changeOrderLineCancellation(orderHeaderKey, orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1),originalOrderLineKey, env,strItemId,strOrderedQty,strShipNode,strCustPONo);
    				}
    				else
    				{
    					int shipToStore = 0, pickInStore = 0;
    					String lastPickDateVal = null;
    					//ArrayList<String> orderLineKeys = new ArrayList<String>();
						ArrayList<Element> orderLineObjects = new ArrayList<Element>();
						
    					for(int l=0; l< orderLineNodeLength; l++)
    					{
    						Element orderLineEle = (Element) getOrderListOutXML.getElementsByTagName(ELE_ORDER_LINE).item(l);
        					String lineTypeValue = orderLineEle.getAttribute(ATTR_LINE_TYPE);
        					if(lineTypeValue.equalsIgnoreCase(LINETYPE_STS))
        						shipToStore++;
        					else if(lineTypeValue.equalsIgnoreCase(LINETYPE_PUS))
        						pickInStore++;
    					}
    					if(orderLineNodeLength == shipToStore || orderLineNodeLength == pickInStore)
    					{
    						for(int l=0; l< orderLineNodeLength; l++)
    						{
    							Element orderLineEle = (Element) getOrderListOutXML.getElementsByTagName(ELE_ORDER_LINE).item(l);
    							String conditionVar = orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1);
            					double maxLineStatus = Double.parseDouble(orderLineEle.getAttribute(ATTR_MAX_LINE_STATUS));
            					Element elemExtn = (Element) orderLineEle.getElementsByTagName(ELE_EXTN).item(0);
            					log.info("elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL) => "+elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL));
            					if(elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL) != "")
            					{
            						lastPickDateVal = elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL);
            						log.info("In If - lastPickDateVal => "+lastPickDateVal);
            					}
            					else
            					{
            						lastPickDateVal = elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE);
            						log.info("In Else - lastPickDateVal => "+lastPickDateVal);
            					}
            					log.info("lastPickDateVal => "+lastPickDateVal);
        						SimpleDateFormat formater = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
        		                Calendar calculatedLastPickDate = Calendar.getInstance();
        		                calculatedLastPickDate.setTime(formater.parse(lastPickDateVal));
								//Add one more status condition for Order Monitor Cancel on backroom picked status change
        		                if(((maxLineStatus == 3200.500) && (currentTime.compareTo(calculatedLastPickDate) > 0)) || ((maxLineStatus == 3350.30) && (currentTime.compareTo(calculatedLastPickDate) > 0) && (conditionVar.equalsIgnoreCase("Y"))))
        								changeOrderLineCancellation(orderHeaderKey, orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1), orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY), env,strItemId,strOrderedQty,strShipNode,strCustPONo);
    						}
    					}
    					else if(shipToStore > 0 && pickInStore > 0)
    					{
    						boolean bosts = false;
    						for(int j=0; j< orderLineNodeLength; j++)
    						{
    							Element orderLineEle = (Element) getOrderListOutXML.getElementsByTagName(ELE_ORDER_LINE).item(j);
            					String lineTypeValue = orderLineEle.getAttribute(ATTR_LINE_TYPE);
								String conditionVariable= orderLineEle.getAttribute(ATTR_CONDITION_VARIBALE1);
            					double maxLineStatus = Double.parseDouble(orderLineEle.getAttribute(ATTR_MAX_LINE_STATUS));
            					Element elemExtn = (Element) orderLineEle.getElementsByTagName(ELE_EXTN).item(0);
            					log.info("Combo - elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL) => "+elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL));
            					if(elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL) != "")
            					{
            						lastPickDateVal = elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL);
            						log.info("Combo In If - lastPickDateVal => "+lastPickDateVal);
            					}
            					else
            					{
            						lastPickDateVal = elemExtn.getAttribute(ATTR_EXTN_LAST_PICK_DATE);
            						log.info("Combo In Else - lastPickDateVal => "+lastPickDateVal);
            					}
            					log.info("lastPickDateVal in Combo => "+lastPickDateVal);
            					SimpleDateFormat formater = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
        		                Calendar calculatedLastPickDate = Calendar.getInstance();
        		                calculatedLastPickDate.setTime(formater.parse(lastPickDateVal));
            					if((lineTypeValue.equalsIgnoreCase(LINETYPE_PUS) && (maxLineStatus == 3200.500) && (currentTime.compareTo(calculatedLastPickDate) > 0)) || (lineTypeValue.equalsIgnoreCase(LINETYPE_PUS) && (maxLineStatus == 3350.30) && (currentTime.compareTo(calculatedLastPickDate) > 0) && (conditionVariable.equalsIgnoreCase("Y"))))
									orderLineObjects.add(orderLineEle);
            						//orderLineKeys.add(orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY));
            					else if((lineTypeValue.equalsIgnoreCase(LINETYPE_STS) && (maxLineStatus < 3200.500) && (currentTime.compareTo(calculatedLastPickDate) > 0)) || (lineTypeValue.equalsIgnoreCase(LINETYPE_STS) && (maxLineStatus < 3350.30) && (currentTime.compareTo(calculatedLastPickDate) > 0) && (conditionVariable.equalsIgnoreCase("Y"))) )
            					{
            						bosts = true;
            						break;
            					}
            					else if ((lineTypeValue.equalsIgnoreCase(LINETYPE_STS) && (maxLineStatus == 3200.500) && (currentTime.compareTo(calculatedLastPickDate) > 0))|| (lineTypeValue.equalsIgnoreCase(LINETYPE_STS) && (maxLineStatus == 3350.30) && (currentTime.compareTo(calculatedLastPickDate) > 0) && (conditionVariable.equalsIgnoreCase("Y"))) )
            					{
									orderLineObjects.add(orderLineEle);
            						//orderLineKeys.add(orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY));
            					}
    						}
    						if(!bosts)
    						{
    							//Iterator<String> iter = orderLineKeys.iterator();
    						   //   while (iter.hasNext())
    						    //	  changeOrderLineCancellation(orderHeaderKey, iter.next().toString(), env);
								
									Iterator<Element> iter = orderLineObjects.iterator();
    						      while (iter.hasNext())
    						    	  changeOrderLineCancellation(orderHeaderKey, iter.next().getAttribute(ATTR_CONDITION_VARIBALE1).toString(), iter.next().getAttribute(ATTR_ORDER_LINE_KEY).toString(), env,strItemId,strOrderedQty,strShipNode,strCustPONo);
    						}
    					}
    				}
                }                
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void changeOrderLineCancellation(String orderHeaderKey, String ConditionVariable1, String orderLineKey, YFSEnvironment env, String strItemId, String strOrderedQty, String strShipNode, String strCustPONo)
	{
		try
		{ 
			int totalNumberOfRecords = 0;
			log.info("Invoking changeOrderLineCancellation => OH Key "+orderHeaderKey+" Con Var "+ConditionVariable1+" orderLineKey "+orderLineKey);
			
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
					
					log.info("changeShipment API Input: "+SCXmlUtil.getString(getChangeShipmentInputDoc));
					
					Document docOutChangeShipment = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_SHIPMENT,
							getChangeShipmentInputDoc);
					
					log.info("changeShipment API Output: "+SCXmlUtil.getString(docOutChangeShipment));
					
				}
				}
				
				//SOM Restock changes -- Start
				String strQueueId="VSI_RESTOCK_"+strShipNode;
				Document docInboxIn=XMLUtil.createDocument(ELE_INBOX);
				Element eleInboxIn=docInboxIn.getDocumentElement();
				eleInboxIn.setAttribute(ATTR_ACTIVE_FLAG, FLAG_Y);
				eleInboxIn.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
				eleInboxIn.setAttribute(ATTR_ORDER_LINE_KEY, orderLineKey);
				eleInboxIn.setAttribute(ATTR_ORDER_NO, strCustPONo);
				eleInboxIn.setAttribute(ATTR_EXCEPTION_TYPE, "Restock");
				eleInboxIn.setAttribute(ATTR_QUEUE_ID, strQueueId);
				eleInboxIn.setAttribute(ATTR_SHIPNODE_KEY, strShipNode);
				Element eleInbxRfrncsLst=SCXmlUtil.createChild(eleInboxIn, ELE_INBOX_REFERANCES_LIST);
				Element eleItemReference=SCXmlUtil.createChild(eleInbxRfrncsLst, ELE_INBOX_REFERANCES);
				eleItemReference.setAttribute(ATTR_NAME, ATTR_ITEM_ID);
				eleItemReference.setAttribute(ATTR_REFERENCE_TYPE, "TEXT");
				eleItemReference.setAttribute(ATTR_VALUE, strItemId);
				Element eleQtyReference=SCXmlUtil.createChild(eleInbxRfrncsLst, ELE_INBOX_REFERANCES);
				eleQtyReference.setAttribute(ATTR_NAME, "Qty");
				eleQtyReference.setAttribute(ATTR_REFERENCE_TYPE, "TEXT");
				eleQtyReference.setAttribute(ATTR_VALUE, strOrderedQty);
				
				log.info("createException API Input: "+SCXmlUtil.getString(docInboxIn));				
				
				VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION, docInboxIn);
				
				log.info("createException API was invoked successfully");
				
				//SOM Restock changes -- End
		
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
		eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, NO_CUSTOMER_PICK);
		//Prod Issue Fix: End
		
		log.info("changeOrder API Input: "+SCXmlUtil.getString(changeOrderDoc));
		
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, changeOrderDoc);
		
		log.info("changeOrder API was invoked successfully");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
