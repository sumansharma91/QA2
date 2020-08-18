package com.vsi.oms.allocation.api;


import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * This class is used for cancelling old STH and STS orders
 */
public class VSICancelAgedOrders {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelAgedOrders.class);
/*
 * this method is used for cancelling STH orders. Cancellation is allowed till released status.
 */
	public void handleSTHCancel (YFSEnvironment env, Document docInput) throws RemoteException, YIFClientCreationException, ParserConfigurationException {
		if(log.isDebugEnabled()){
			log.debug("VSICancelAgedOrders.handleSTHCancel : START"+XMLUtil.getXMLString(docInput));
		}
		Element eleMonitorRule = docInput.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleMonitorRule, VSIConstants.ELE_ORDER);
		Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_STATUSES);
		String strOrder = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
		Document docChangeOrderOutput=null;

		//start forming changeOrder document to cancel the lines.
		Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		String stsOrderHeaderKey=eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,stsOrderHeaderKey );
		Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "Customer Care Cancel - No Email");
		eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");
	
		if (!YFCCommon.isVoid(eleOrderStatuses)){
		
		//loop through all the statuses to find the LINE which is not shipped or cancelled.
		ArrayList<Element> arrOrderStatus = SCXmlUtil.getChildren(eleOrderStatuses, VSIConstants.ELE_ORDER_STATUS);		
		for(Element eleOrderStatus : arrOrderStatus){
			String strOrderLineKey= eleOrderStatus.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			String strStatus = eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS);
			if(Double.parseDouble(strStatus)<=3200){
				String srtTotalQuantity = eleOrderStatus.getAttribute("TotalQuantity");
				String strStatusQty = eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS_QUANTITY);
				Double updatedQty = Double.parseDouble(srtTotalQuantity) - Double.parseDouble(strStatusQty);
			Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
			eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);			
			eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY,updatedQty.toString());
				
			}	
		}
		
		//Invoke change order API to cancel any un-shipped  line.
		if(log.isDebugEnabled()){
			log.debug("Printing docChangeOrder document : START"+XMLUtil.getXMLString(docChangeOrder));
		}
		 NodeList nlchangeOrderLineList = eleChangeOrderLines
		            .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		 if (nlchangeOrderLineList.getLength() > 0){
			 docChangeOrderOutput = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
		 }
		if (!YFCCommon.isVoid(docChangeOrderOutput)) {
	
		Document resolveExceptionInput = getResolveAlert(strOrder,stsOrderHeaderKey);
		Document docInboxListTemplate = SCXmlUtil
				.createDocument("InboxList");
		Element eleInboxListTemplate = docInboxListTemplate
				.getDocumentElement();
		Element eleInboxTemplate = SCXmlUtil.createChild(
				eleInboxListTemplate, "Inbox");
		eleInboxTemplate.setAttribute(VSIConstants.ATTR_ORDER_NO, VSIConstants.ATTR_EMPTY);
		eleInboxTemplate.setAttribute("InboxKey", VSIConstants.ATTR_EMPTY);
		
		VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
				resolveExceptionInput);
		}
		if(log.isDebugEnabled()){
			log.debug("VSICancelAgedOrders.handleSTHCancel : END");
		}
	}
	}
	/*
	 * this method is used for cancelling STS complete status order
	 */
	public void STSCompleteCancel (YFSEnvironment env, Document docInput) throws RemoteException, YIFClientCreationException, ParserConfigurationException {
		if(log.isDebugEnabled()){
			log.debug("VSICancelAgedOrders.STSCompleteCancel : START"+XMLUtil.getXMLString(docInput));
		}
		Element eleMonitorRule = docInput.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleMonitorRule, VSIConstants.ELE_ORDER);
		Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_STATUSES);
		String strOrder = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
		Document docChangeOrderOutput=null;

		//start forming changeOrder document to cancel the lines.
		Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		String stsOrderHeaderKey=eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,stsOrderHeaderKey );

		eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "Customer Care Cancel - No Email");
		eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");
		Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
	
		if (!YFCCommon.isVoid(eleOrderStatuses)){
		
		//loop through all the statuses to find the LINE which is not shipped or cancelled.
		ArrayList<Element> arrOrderStatus = SCXmlUtil.getChildren(eleOrderStatuses, VSIConstants.ELE_ORDER_STATUS);		
		for(Element eleOrderStatus : arrOrderStatus){
			String strOrderLineKey= eleOrderStatus.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			String strStatus = eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS);
			if(Double.parseDouble(strStatus)==2160.200){
			Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
			eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
				
			}	
		}
		
		//Invoke change order API to cancel any un-shipped  line.
		if(log.isDebugEnabled()){
			log.debug("Printing docChangeOrder document : START"+XMLUtil.getXMLString(docChangeOrder));
		}
		 NodeList nlchangeOrderLineList = eleChangeOrderLines
		            .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		 if (nlchangeOrderLineList.getLength() > 0){
			 docChangeOrderOutput = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
		 }
		if (!YFCCommon.isVoid(docChangeOrderOutput)) {
	
		Document resolveExceptionInput = getResolveAlert(strOrder,stsOrderHeaderKey);
		Document docInboxListTemplate = SCXmlUtil
				.createDocument("InboxList");
		Element eleInboxListTemplate = docInboxListTemplate
				.getDocumentElement();
		Element eleInboxTemplate = SCXmlUtil.createChild(
				eleInboxListTemplate, "Inbox");
		eleInboxTemplate.setAttribute(VSIConstants.ATTR_ORDER_NO, VSIConstants.ATTR_EMPTY);
		eleInboxTemplate.setAttribute("InboxKey", VSIConstants.ATTR_EMPTY);
		
		VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
				resolveExceptionInput);
		}
		if(log.isDebugEnabled()){
			log.debug("VSICancelAgedOrders.STSCompleteCancel : END");
		}
	}
	}
/*
 * this method is used for making resolveException input document
 */
	private Document getResolveAlert(String strOrderNo, String stsOrderHeaderKey)throws ParserConfigurationException { {
		if(log.isDebugEnabled()){
			log.debug("Printing OrderNo and OrderHeaderKey \n"+strOrderNo+"and"+stsOrderHeaderKey);
		}
		Document docResolveExInput = XMLUtil.createDocument("ResolutionDetails");
		Element eleRoot = docResolveExInput.getDocumentElement();
		Element eleInbox = docResolveExInput.createElement("Inbox");
		eleRoot.appendChild(eleInbox);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, stsOrderHeaderKey);
		
		if(log.isDebugEnabled()){
			log.debug("Printing docResolveExInput \n"+XMLUtil.getXMLString(docResolveExInput));
		}
		return docResolveExInput;
	}
	}
	

}
