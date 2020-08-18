package com.vsi.oms.api;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.w3c.dom.Document;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;

public class VSICancelNegativeOrderTotal extends VSIBaseCustomAPI implements VSIConstants
{
	public Document cancelOrderWithNegativeOrderTotal(YFSEnvironment env, Document inXml)
	{
		Element orderElement = inXml.getDocumentElement();
		String orderNoValue = orderElement.getAttribute(ATTR_ORDER_NO);
		String orderHeaderKey = orderElement.getAttribute(ATTR_ORDER_HEADER_KEY);
		String enterpriseCode = orderElement.getAttribute(ATTR_ENTERPRISE_CODE);
		String strDocumentType = orderElement.getAttribute(ATTR_DOCUMENT_TYPE);
		String orderTypeValue = orderElement.getAttribute(ATTR_ORDER_TYPE);
		if(strDocumentType.equalsIgnoreCase(ATTR_DOCUMENT_TYPE_SALES) && (orderTypeValue.equalsIgnoreCase(ATTR_ORDER_TYPE_POS) || orderTypeValue.equalsIgnoreCase(WEB)))
		{		
		Element overallTotals = (Element) orderElement.getElementsByTagName("OverallTotals").item(0);
		BigDecimal grandTotalDecimal = new BigDecimal("0.00");
		grandTotalDecimal = new BigDecimal(overallTotals.getAttribute("GrandTotal")).setScale(2, RoundingMode.HALF_EVEN);
		if(grandTotalDecimal.intValue() < 0)
		{
		try
		{
		Document changeOrderDoc = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleOrder = changeOrderDoc.getDocumentElement();
		eleOrder.setAttribute(ATTR_OVERRIDE, VSIConstants.FLAG_Y);
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		eleOrder.setAttribute(ATTR_ORDER_NO, orderNoValue);
		eleOrder.setAttribute(ATTR_ENTERPRISE_CODE, enterpriseCode);
		eleOrder.setAttribute(ATTR_DOCUMENT_TYPE, strDocumentType);
		eleOrder.setAttribute(ATTR_ACTION, ACTION_RELEASE_CANCEL);
		eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, CANCELLATION_MSG);
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, changeOrderDoc);
		
		orderElement.setAttribute(CANCEL_STATUS_FLAG,FLAG_Y);
		
		//Creation of Alert
		Document createExceptionDoc = SCXmlUtil.createDocument(ELE_INBOX);
		Element eleInbox = createExceptionDoc.getDocumentElement();
		eleInbox.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		eleInbox.setAttribute(ATTR_ORDER_NO, orderNoValue);
		eleInbox.setAttribute(ATTR_EXCEPTION_TYPE, CANCELLATION_ALERT_EXCEPTION_TYPE);
		eleInbox.setAttribute(ATTR_DETAIL_DESCRIPTION, CANCELLATION_ALERT_DETAIL_DESCRIPTION);
		eleInbox.setAttribute(ATTR_QUEUE_ID, CANCELLATION_ALERT_QUEUE);
		Element elemOrder = SCXmlUtil.createChild(eleInbox, ELE_ORDER);
		elemOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		elemOrder.setAttribute(ATTR_ORDER_NO, orderNoValue);
		elemOrder.setAttribute(ATTR_ENTERPRISE_CODE, enterpriseCode);
		elemOrder.setAttribute(ATTR_DOCUMENT_TYPE, strDocumentType);
		VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION,createExceptionDoc);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		}
	}
		return inXml;
	}
}
