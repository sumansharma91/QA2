package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.api.web.VSIGetPTandLPTdetails;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc
 * 1. Get the modification reason code
2. then call getOrderLineList for the customerPoNo
If modification reason code is "Inventory not available" and line type is BOPUS
		
		ii) check if all the lines are in less than or equal to store acknowledged
			if yes, a) unschedule all the lines that are in scheduled or store acknowledged status
					b) change fulfillmentType, LineType and ProcureFromNode for all the lines with the same customerPONo
			if No (order is in pick packed and authorized), throw an alert/exception indicating that the order can not be cancelled at this status
If Modification reason code is "Inventory not available" and line type is BOSTS, throw an exception that the modification reason code is not applicable
If Modification reason code is anything else cancel the order
 *
 */
public class VSIStoreChangeOrder {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIStoreChangeOrder.class);
	YIFApi api;
	String strCustPoNo = "";
	public void vsiStoreChangeOrder(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
	
		Element rootElement = inXML.getDocumentElement();
		 strCustPoNo = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String modReasonCode = 	rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE);
		
		Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
		Element eleOrderLine = getOrderLineListInput.getDocumentElement();
		eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPoNo);
		
	
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSIStoreChangeOrderLineList.xml");
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
		env.clearApiTemplates();
		Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		Element orderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String orderType = "";
		if(orderEle.hasAttribute(VSIConstants.ATTR_ORDER_TYPE) && orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE)!= null)
			orderType = orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);

		if(null != orderLineEle){
		
			String lineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			String sOHK = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			//OMS-1133 : fix for POS orders conversion from BOPUS to BOSTS -start
				if(lineType.equalsIgnoreCase("PICK_IN_STORE")){
			//OMS-1133 : fix for POS orders conversion from BOPUS to BOSTS -end		
					
					try {
						     
						     changeAndUnscheduleOrder(env,outDoc);
						     
						 if(!YFCObject.isVoid(strCustPoNo)){
							Document emailDoc = VSISendRdyForPckupAndReminderEmails.getEmailContent(env,VSIConstants.ATTR_CUST_PO_NO, strCustPoNo); 
							VSIUtils.invokeService(env,"VSISendBOPUSToBOSTSConversionEmail", emailDoc);
							
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				else if (lineType.equalsIgnoreCase("SHIP_TO_STORE") && !orderType.equalsIgnoreCase("POS")){
					
					raiseAlert(env,sOHK);
					
				}
				else {
					
					cancelOrder(env,outDoc,inXML);
					
					
				}
			
			
			
		}
	
		
		
		
	}



	private void changeAndUnscheduleOrder(YFSEnvironment env, Document outDoc) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException, Exception {
		
	
		if(log.isDebugEnabled()){
			log.debug("outDoc: "+XMLUtil.getXMLString(outDoc));
		}
		
		//changeOrder Input
		Document changeOrderInput = XMLUtil.createDocument("Order");
		Element orderElement = changeOrderInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
		orderElement.setAttribute("Override", "Y");
		
		Element orderLinesElement = changeOrderInput.createElement("OrderLines");
		orderElement.appendChild(orderLinesElement);
	
		Element elementOrder = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		
		String oHK = elementOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
	
		NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();
	
		
		
		//UnscheduleOrder Input
		Document unScheduleOrderInput = XMLUtil.createDocument("UnScheduleOrder");
		Element unScheduleOrderElement = unScheduleOrderInput.getDocumentElement();
		unScheduleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		unScheduleOrderElement.setAttribute("Override", "Y");
		Element orderLinesEle = unScheduleOrderInput.createElement("OrderLines");
		unScheduleOrderElement.appendChild(orderLinesEle);
		Document tempOrderDoc = null;
		
			tempOrderDoc = XMLUtil.getDocumentForElement(elementOrder);
			Element tempOrderLinesEle = tempOrderDoc.createElement("OrderLines");
			tempOrderDoc.getDocumentElement().appendChild(tempOrderLinesEle);
			
		boolean firstCallToStampPTDates =true;
		String strPTDate="";
		String strLPTDate="";
		for(int i=0;i<linelength;i++){
		
			Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			Node cpyOrderLineNode = tempOrderDoc.importNode(orderLineEle, true);
			Element cpyOrderLineEle = (Element) cpyOrderLineNode;
			cpyOrderLineEle.setAttribute(VSIConstants.ATTR_LINE_TYPE, "SHIP_TO_STORE");
			Node tempOrderNde = cpyOrderLineEle.getElementsByTagName("Order").item(0);
			cpyOrderLineEle.removeChild(tempOrderNde);
			tempOrderLinesEle.appendChild(cpyOrderLineEle);
			if(firstCallToStampPTDates){
				if(log.isDebugEnabled()){
		    		log.debug("tempOrderDoc: "+XMLUtil.getXMLString(tempOrderDoc));
				}
				tempOrderDoc = VSIGetPTandLPTdetails.stampPTAndLPTForPickedOrders(env,tempOrderDoc);
				Element tempOrderLineEle = (Element) tempOrderDoc.getElementsByTagName("OrderLine").item(0);
				if(tempOrderLineEle != null){
					Element tempExtnEle = (Element) tempOrderLineEle.getElementsByTagName("Extn").item(0);
					if(tempExtnEle != null){
						strPTDate = tempExtnEle.getAttribute("ExtnPickDate");
						strLPTDate = tempExtnEle.getAttribute("ExtnLastPickDate");
					}
				}
				firstCallToStampPTDates =false;
			}
			//System.out.println("strPTDate: "+strPTDate);
			//System.out.println("strLPTDate: "+strLPTDate);
			String strOrderLineKey = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			//String lineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			String minLineStatus = orderLineEle.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
			String maxLineStatus = orderLineEle.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
			
			double iminLine = 0.0;
			
			if(minLineStatus !=null && !minLineStatus.trim().equalsIgnoreCase(""))
				iminLine = Double.valueOf(minLineStatus);
			
			double imaxLine = 0.0;
			
			if(maxLineStatus !=null && !maxLineStatus.trim().equalsIgnoreCase(""))
				imaxLine = Double.valueOf(maxLineStatus);
			
			Element orderLineElement = changeOrderInput.createElement("OrderLine");
			Element orderLineEleUnsched = unScheduleOrderInput.createElement("OrderLine");
			
			 if(iminLine>=1500 && imaxLine<=3200.500 ){
				 
							 
				orderLineElement.setAttribute(VSIConstants.ATTR_LINE_TYPE, "SHIP_TO_STORE");
				orderLineElement.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE, "SHIP_TO_STORE");
			
				orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
				orderLineElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
				//orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE, "9001");
				
				Element eleOrderLineSourcingControls = SCXmlUtil.createChild(orderLineElement, "OrderLineSourcingControls");
				Element eleOrderLineSourcingCntrl =  SCXmlUtil.createChild(eleOrderLineSourcingControls, "OrderLineSourcingCntrl");
				eleOrderLineSourcingCntrl.setAttribute("Node", orderLineEle.getAttribute("ShipNode"));
				eleOrderLineSourcingCntrl.setAttribute("InventoryCheckCode", "NOINV");
				eleOrderLineSourcingCntrl.setAttribute("ReasonText", "SHIP_TO_STORE");

				//OMS-815 - Set ExtnPublished Flag as 'N'							
				Element extnEle = changeOrderInput.createElement("Extn");
				extnEle.setAttribute("ExtnPublished", "N");
				//OMS-815 Ends
				
				if(strPTDate !=null && !strPTDate.trim().equals("") && strLPTDate !=null && !strLPTDate.trim().equals("")){
					//Element extnEle = changeOrderInput.createElement("Extn"); // Commented as a part of OMS-815
					extnEle.setAttribute("ExtnPickDate", strPTDate);
					extnEle.setAttribute("ExtnLastPickDate", strLPTDate);
					//orderLineElement.appendChild(extnEle); // Commented as a part of OMS-815
					Element orderDatesEle = changeOrderInput.createElement("OrderDates");
					orderLineElement.appendChild(orderDatesEle);
						Element orderDateEle = changeOrderInput.createElement("OrderDate");
						orderDateEle.setAttribute("DateTypeId", "ExtnPickDate");
						orderDateEle.setAttribute("ActualDate", strPTDate);
						orderDatesEle.appendChild(orderDateEle);
						
						orderDateEle = changeOrderInput.createElement("OrderDate");
						orderDateEle.setAttribute("DateTypeId", "ExtnLastPickDate");
						orderDateEle.setAttribute("ActualDate", strLPTDate);
						orderDatesEle.appendChild(orderDateEle);
				}

				//OMS-815 - Set ExtnPublished Flag as 'N'							
				orderLineElement.appendChild(extnEle);
				//OMS-815 Ends
				
				orderLinesElement.appendChild(orderLineElement);
				orderLineEleUnsched.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
				orderLinesEle.appendChild(orderLineEleUnsched);
			 }
			 
			 else {
				 
				 raiseAlert(env,oHK);
				 
				 break;
				 
				 
			 }
		}
		
		if(log.isDebugEnabled()){
    		log.debug("Input to Change Order in changeandUnschedule" +XMLUtil.getXMLString(changeOrderInput));
		}
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
		api.invoke(env, VSIConstants.API_UNSCHEDULE_ORDER,unScheduleOrderInput);
		
		//raise email
		//as part of 815
		Document scheduleOrderInput = XMLUtil.createDocument("ScheduleOrder");
		Element scheduleOrderElement = scheduleOrderInput.getDocumentElement();
		scheduleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		scheduleOrderElement.setAttribute("CheckInventory", "Y");

		api.invoke(env, VSIConstants.API_SCHEDULE_ORDER,scheduleOrderInput);
		
		
	}
	private void cancelOrder(YFSEnvironment env, Document outDoc, Document inXML) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document changeOrderInput = XMLUtil.createDocument("Order");
		Element orderElement = changeOrderInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
		orderElement.setAttribute("Override", "Y");
		String modReasonTxt= "";
		String modReasonCode= "";
		Element inXMLRootEle = inXML.getDocumentElement();
		if(inXMLRootEle.hasAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE))modReasonCode = inXMLRootEle.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE);
		if(inXMLRootEle.hasAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT))modReasonTxt = inXMLRootEle.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT);
		orderElement.setAttribute("ModificationReasonText", modReasonTxt);
		orderElement.setAttribute("ModificationReasonCode", modReasonCode);
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
		
		if(log.isDebugEnabled()){
    		log.debug("Input to Change Order" +XMLUtil.getXMLString(changeOrderInput));
		}
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
		
	}

	private void raiseAlert(YFSEnvironment env, String sOHK) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
	
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Modification Reason Code not applicable");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Either LineType is Ship to Store or Order is not in a valid Status");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Order Fulfillment");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Invalid Mod Reason Code");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_INVALID_MODIFICATION");
		
		
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

