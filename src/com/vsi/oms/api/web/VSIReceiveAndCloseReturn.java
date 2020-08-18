package com.vsi.oms.api.web;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIReceiveAndCloseReturn implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIReceiveAndCloseReturn.class);
			
	public Document receiveAndCloseReturn(YFSEnvironment env, Document inXML) throws Exception{
		
		log.beginTimer("VSIReceiveAndCloseReturn.receiveAndCloseReturn()");
		
		Element eleReceipt = inXML.getDocumentElement();
		Element eleReceiptLines = SCXmlUtil.getChildElement(eleReceipt, ELE_RECEIPT_LINES);
		Element eleReceiptLine = SCXmlUtil.getChildElement(eleReceiptLines, ELE_RECEIPT_LINE);
		String strOrderHeaderKey = "";
		Boolean isWholesale = false;
		String strOrderNo = "";
		String strEnterpriseCode = "";
		Element eleOrder = null;
		
		if(!YFCObject.isVoid(eleReceiptLine)){
			
			strOrderHeaderKey = SCXmlUtil.getAttribute(eleReceiptLine, ATTR_ORDER_HEADER_KEY);
			if(!YFCObject.isVoid(strOrderHeaderKey)){
				
				Document getOrderListInXML  = SCXmlUtil.createDocument(ELE_ORDER);
				getOrderListInXML.getDocumentElement().setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
				
				Document getOrderListOutXML = VSIUtils.invokeService(env, "VSIWholeSaleGetOrderList", getOrderListInXML);
				Element eleOrderList = getOrderListOutXML.getDocumentElement();
				eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
				if(!YFCObject.isVoid(eleOrder)){
					
					if(SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_TYPE).equalsIgnoreCase(WHOLESALE)){
						isWholesale = true;
						strOrderNo = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_NO);
						strEnterpriseCode = SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE);
					}
				}
			}
		}
		
		// Invoke receiveOrder
		Document receiveOrderTemplate = SCXmlUtil.createDocument(ATTR_RECEIPT);
		receiveOrderTemplate.getDocumentElement().setAttribute(ATTR_DOCUMENT_TYPE, "");
		receiveOrderTemplate.getDocumentElement().setAttribute("OpenReceiptFlag", "");
		receiveOrderTemplate.getDocumentElement().setAttribute("ReceiptHeaderKey", "");
		receiveOrderTemplate.getDocumentElement().setAttribute("ReceiptNo", "");
		receiveOrderTemplate.getDocumentElement().setAttribute(ATTR_RECEIVING_NODE, "");
		
		Document receiveOrderOutXML = VSIUtils.invokeAPI(env, receiveOrderTemplate, API_RECEIVE_ORDER, inXML);
		
		VSIUtils.invokeAPI(env, "closeReceipt", receiveOrderOutXML);
		
		
		// WHEN WHOLESALE RETURNS ARE AUTOMATED, REMOVE THE BELOW IF CONDITION
		if(isWholesale && !YFCObject.isVoid(strOrderNo)){
					
			createReturnReceivedAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode);
			
			if(!YFCObject.isVoid(eleOrder)){
				
				Boolean emailSent = false;
				for(int i = 0; i < 3; i++){
					
					try{
						VSIUtils.invokeService(env, "VSIWholesaleSendReceiptCompleteEmail", XMLUtil.getDocumentForElement(eleOrder));
						emailSent = true;
					}catch(Exception e){
						log.error("Exception in VSIWholesaleSendReceiptCompleteEmail : " + e.getMessage());
						e.printStackTrace();
					}
					if(emailSent){
						break;
					}
				}
			}
		}
		
		log.endTimer("VSIReceiveAndCloseReturn.receiveAndCloseReturn()");
		return inXML;
	}
	
	private void createReturnReceivedAlert (YFSEnvironment env, String strOrderHeaderKey, String strOrderNo, String strEnterpriseCode) throws Exception{

		//String detailDescSuffix = " Record length exceeded. Please refer to the alert email for the complete details.";
		Document createExceptionInXML = SCXmlUtil.createDocument(ELE_INBOX);
		Element eleInbox = createExceptionInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleInbox, ATTR_CONSOLIDATE, FLAG_Y);
		SCXmlUtil.setAttribute(eleInbox, ATTR_CONSOLIDATION_WINDOW, CONSOLIDATION_WINDOW_FOREVER);
		
		SCXmlUtil.setAttribute(eleInbox, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleInbox, ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(eleInbox, ATTR_ENTERPRISE_KEY, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleInbox, ATTR_EXCEPTION_TYPE, "VSI_WH_RETURN_RECEIVED_ALERT");
		SCXmlUtil.setAttribute(eleInbox, ATTR_DETAIL_DESCRIPTION, "All or part of Wholesale Return No. " + strOrderNo + " has been received at the DC. CLOSE THIS ALERT TO CREATE THE INVOICE AND ISSUE A CREDIT");
		SCXmlUtil.setAttribute(eleInbox, ATTR_QUEUE_ID, "VSI_WHOLESALE_ALERT_Q");
		
		Element eleConsolidationTemplate = SCXmlUtil.createChild(eleInbox, ELE_CONSOLIDATE_TEMPLATE);
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_ENTERPRISE_KEY, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_EXCEPTION_TYPE, "VSI_WH_RETURN_RECEIVED_ALERT");
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_DETAIL_DESCRIPTION, "All or part of Wholesale Return No. " + strOrderNo + " has been received at the DC. CLOSE THIS ALERT TO CREATE THE INVOICE AND ISSUE A CREDIT");
		SCXmlUtil.setAttribute(eleConsolidationTemplate, ATTR_QUEUE_ID, "VSI_WHOLESALE_ALERT_Q");		

		Element eleOrder = SCXmlUtil.createChild(eleInbox, ELE_ORDER);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleOrder, ATTR_DOCUMENT_TYPE, RETURN_DOCUMENT_TYPE);

		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				createExceptionInXML);
	}

}
