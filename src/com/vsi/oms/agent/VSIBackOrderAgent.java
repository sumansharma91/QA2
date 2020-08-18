package com.vsi.oms.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIConstants;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIBackOrderAgent extends YCPBaseAgent implements VSIConstants 
{
	private static YFCLogCategory log = YFCLogCategory.instance(VSIPaymentHold.class.getName());
	public final List<Document> getJobs(YFSEnvironment env, Document inDoc)
	{
			if(log.isDebugEnabled()){
				log.debug("VSIBackOrderAgent.getJobs() method Begins => ");
			}
			List<Document> orderList = new ArrayList<Document>();
			
			try
			{	
				Document docGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
				Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(ATTR_CODE_TYPE, BO_COMMON_CODE);
				Document docGetCommonCodeListOutput = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, docGetCommonCodeList);
				if(docGetCommonCodeListOutput != null)
				{
					Element elemCommonCode = (Element) docGetCommonCodeListOutput.getElementsByTagName(ELEMENT_COMMON_CODE).item(0);
					String boCodeValue = elemCommonCode.getAttribute(ATTR_CODE_VALUE);
					if (!boCodeValue.equalsIgnoreCase(""))
					{	
					
				Document getOrderHoldTypeListDoc = SCXmlUtil.createDocument(ELE_ORDER_HOLD_TYPE);
				Element getOrderHoldTypeListEle = getOrderHoldTypeListDoc.getDocumentElement();
				getOrderHoldTypeListEle.setAttribute(ATTR_HOLD_TYPE , ATTR_BACKORDERED_HOLD_TYPE);
				getOrderHoldTypeListEle.setAttribute(ATTR_STATUS , STATUS_CREATE);
				Document getOrderHoldTypeListOutput = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORDER_HOLD_TYPE_LIST, API_GET_ORDER_HOLD_TYPE_LIST,getOrderHoldTypeListDoc);
			
			NodeList orderHoldType = getOrderHoldTypeListOutput.getElementsByTagName(ELE_ORDER_HOLD_TYPE);
			for (int i = 0; i < orderHoldType.getLength(); i++) 
			{
				Element eleOrderHoldType = (Element) getOrderHoldTypeListOutput.getElementsByTagName(ELE_ORDER_HOLD_TYPE).item(i);
				Element elemOrder = (Element) eleOrderHoldType.getElementsByTagName(ELE_ORDER).item(0);
				Element orderLineElement = (Element) eleOrderHoldType.getElementsByTagName(ELE_ORDER_LINE).item(0);
				String lastHoldTypeDate = eleOrderHoldType.getAttribute(ATTR_LAST_HOLD_TYPE_DATE);
				
				SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
                Calendar calculatedOrderTime = Calendar.getInstance();
                calculatedOrderTime.setTime(formatter.parse(lastHoldTypeDate));
                calculatedOrderTime.add(Calendar.HOUR, Integer.parseInt(boCodeValue));
                if(log.isDebugEnabled())
                	log.info("calculatedOrderTime(lastHoldTypeDate) => "+calculatedOrderTime.getTime());
                Date localTime = new Date();
                SimpleDateFormat converter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
                converter.setTimeZone(TimeZone.getTimeZone(EST_TIME_ZONE));
                String currTime = converter.format(localTime);
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTime(formatter.parse(currTime));
                if(log.isDebugEnabled())
                	log.info("currTime => "+ currTime + "currentTime => "+currentTime.getTime() + "calculatedOrderTime => "+calculatedOrderTime);   

                if (currentTime.compareTo(calculatedOrderTime) > 0)
                {
                	//Resolve BO Hold
                	Document changeOrderDoc = SCXmlUtil.createDocument(ELE_ORDER);
                	Element eleOrder = changeOrderDoc.getDocumentElement();
                	eleOrder.setAttribute(ATTR_OVERRIDE, VSIConstants.FLAG_Y);
                	eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrderHoldType.getAttribute(ATTR_ORDER_HEADER_KEY));
                	Element eleOrderLines = SCXmlUtil.createChild(eleOrder, ELE_ORDER_LINES);
                	Element elemOrderLine = SCXmlUtil.createChild(eleOrderLines, ELE_ORDER_LINE);
                	elemOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, eleOrderHoldType.getAttribute(ATTR_ORDER_LINE_KEY));
                	elemOrderLine.setAttribute(ATTR_ACTION, ACTION_CAPS_MODIFY);
                	Element eleOrderHoldTypes = SCXmlUtil.createChild(eleOrder, ELE_ORDER_HOLD_TYPES);
                	Element elemOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, ELE_ORDER_HOLD_TYPE);
                	elemOrderHoldType.setAttribute(ATTR_HOLD_TYPE, ATTR_BACKORDERED_HOLD_TYPE);
                	elemOrderHoldType.setAttribute(ATTR_REASON_TEXT, ATTR_RESOLVE_MODIFY_REASON_TEXT);
                	elemOrderHoldType.setAttribute(ATTR_STATUS, STATUS_RESOLVED);
                	eleOrderHoldTypes.appendChild(elemOrderHoldType);
                	elemOrderLine.appendChild(eleOrderHoldTypes);
                	VSIUtils.invokeAPI(env, API_CHANGE_ORDER, changeOrderDoc);
						
                	Document resolveExceptionDoc = SCXmlUtil.createDocument(ELE_RESOLUTION_DETAILS);
                	Element resolveExceptionEle = resolveExceptionDoc.getDocumentElement();
                	Element elemInbox = SCXmlUtil.createChild(resolveExceptionEle, ELE_INBOX);
                	elemInbox.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrderHoldType.getAttribute(ATTR_ORDER_HEADER_KEY));
                	elemInbox.setAttribute(ATTR_ORDER_NO, elemOrder.getAttribute(ATTR_ORDER_NO));
                	elemInbox.setAttribute(ATTR_ENTERPRISE_CODE, elemOrder.getAttribute(ATTR_ENTERPRISE_CODE));
                	elemInbox.setAttribute(ATTR_ORDER_LINE_KEY, orderLineElement.getAttribute(ATTR_ORDER_LINE_KEY));
                	elemInbox.setAttribute(ATTR_QUEUE_ID, ALERT_BO_QUEUE);
                	VSIUtils.invokeAPI(env, RESOLVE_EXCEPTION_API,resolveExceptionDoc);
                }
			}	
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return orderList;
			}
		public void executeJob(YFSEnvironment env, Document docInput)
				throws Exception{
		}
}