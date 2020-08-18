package com.vsi.oms.allocation.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIApplyIntHoldOnOrder implements YIFCustomApi {

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	public void applyInternationalHoldOnOrder(YFSEnvironment env, Document inDoc) throws Exception{
		
		
		//create template and input then call change order api
		Element eleOrder = inDoc.getDocumentElement();
		String minOrderStatus = eleOrder.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
		String maxOrderStatus = eleOrder.getAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS);
		//OMS-1550 Start
		if((minOrderStatus.equals(VSIConstants.STATUS_NO_ACTION)) || maxOrderStatus.equals(VSIConstants.STATUS_NO_ACTION)){			
		//OMS-1550 End
		String eleOrderHeaderKey = inDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		Document changeOrderIP = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element changeOrderRootEle = changeOrderIP.getDocumentElement();
		Element eleHoldTypes=SCXmlUtil.createChild(changeOrderRootEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,VSIConstants.ATTR_HOLD_TYPE_VSI_INTERNAT_HOLD);
		eleHoldType.setAttribute(VSIConstants.ATTR_STATUS,VSIConstants.STATUS_CREATE);
		changeOrderRootEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,eleOrderHeaderKey);
	      Document changeOrderTemp = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
	      Element eleOrderCOTemp = changeOrderTemp.getDocumentElement();
	      eleOrderCOTemp.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,eleOrderHeaderKey);
		 VSIUtils.invokeAPI(env, changeOrderTemp,VSIConstants.API_CHANGE_ORDER, changeOrderIP);
		 
		 
		 //create alert for international order
			Document doccreateExceptionInput = SCXmlUtil.createDocument(VSIConstants.ELE_INBOX);
			Element eleInbox = doccreateExceptionInput.getDocumentElement();
			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(inDoc.getDocumentElement(), VSIConstants.ATTR_ORDER_NO));
			eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, VSIConstants.ATTR_VSI_ORDER_HOLD);
			eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, VSIConstants.ATTR_EXCEPTION_VSI_INTERNATIONAL_ORDER_HOLD);
			eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SCXmlUtil.getAttribute(inDoc.getDocumentElement(), VSIConstants.ATTR_ORDER_NO) + VSIConstants.STRING_INTERNATIONAL_ALERT_MESSAGE);
			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrderHeaderKey);
			
			Element orderEle = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
			orderEle.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(inDoc.getDocumentElement(), VSIConstants.ATTR_ENTERPRISE_CODE));
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,eleOrderHeaderKey );
			
			/*OMS-1909-Alert consolidation : Start*/
			eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE,VSIConstants.FLAG_Y);
			eleInbox.setAttribute(VSIConstants.ATTR_CONS_WINDOW,VSIConstants.VAL_FOREVER);
			Element eleConsolidationTemplate = doccreateExceptionInput.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
			eleInbox.appendChild(eleConsolidationTemplate);
			Element eleInboxCpy = (Element) eleInbox.cloneNode(true);
			eleConsolidationTemplate.appendChild(eleInboxCpy);
			/*OMS-1909 -End*/
			
			VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, doccreateExceptionInput); 
		
		}
		
	}
	
	
	
}
