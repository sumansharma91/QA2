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

/*
 * This class will convert the BOPUS line BOSTS line, when the line stays in No Action or Acknowledge for more than 10 hours
 * This class is invoked from the Order Monitoring on VSI Store Order Pipeline
 */
public class VSIConvertBOPUSToBOSTS {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIConvertBOPUSToBOSTS.class);
	YIFApi api;

	public void vsiStoreChangeOrder(YFSEnvironment env, Document inXML)
			throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("Entering vsiStoreChangeOrder");
			log.info("Entering vsiStoreChangeOrder input:\n"+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			NodeList orderStsNL = inXML.getElementsByTagName(
					"OrderStatus");
			int lineCt = orderStsNL.getLength();
			Element ordEle = (Element) inXML.getElementsByTagName(
					"Order").item(0);
			String strOHK = ordEle
					.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			if(log.isDebugEnabled()){
				log.info("strOHK= "+strOHK);
			}
			if(strOHK != null && !strOHK.trim().equalsIgnoreCase("") ){
				// changeOrder Input
				Document changeOrderInput = XMLUtil.createDocument("Order");
				Element orderElement = changeOrderInput.getDocumentElement();
				orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
				orderElement.setAttribute("Override", "Y");
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOHK);
				Element orderLinesElement = changeOrderInput
						.createElement("OrderLines");
				orderElement.appendChild(orderLinesElement);
				
				// UnscheduleOrder Input
				Document unScheduleOrderInput = XMLUtil
						.createDocument("UnScheduleOrder");
				Element unScheduleOrderElement = unScheduleOrderInput
						.getDocumentElement();
				unScheduleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
						strOHK);
				unScheduleOrderElement.setAttribute("Override", "Y");
				Element orderLinesEle = unScheduleOrderInput
						.createElement("OrderLines");
				unScheduleOrderElement.appendChild(orderLinesEle);
				for(int i=0; i < lineCt; i++){
					Element orderStsEle = (Element) orderStsNL.item(i);
					String strOLK = orderStsEle
							.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					if(log.isDebugEnabled()){
						log.info("strOLK= "+strOLK);
					}
					if(strOLK != null && !strOLK.trim().equalsIgnoreCase("")){
						Element orderLineElement = changeOrderInput.createElement("OrderLine");
						orderLineElement.setAttribute(VSIConstants.ATTR_LINE_TYPE,
								"SHIP_TO_STORE");
						orderLineElement.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE,
								"SHIP_TO_STORE");

						orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOLK);
						orderLineElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
						/*orderLineElement.setAttribute(VSIConstants.ATTR_PROCURE_FROM_NODE,
								"9001");*/
						orderLinesElement.appendChild(orderLineElement);
						Element orderLineEleUnsched = unScheduleOrderInput
								.createElement("OrderLine");
						orderLineEleUnsched.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
								strOLK);
						orderLinesEle.appendChild(orderLineEleUnsched);
					}
				}
				if(log.isDebugEnabled()){
					log.info("changeOrderInput= "+XMLUtil.getXMLString(changeOrderInput));
					log.info("unScheduleOrderInput= "+XMLUtil.getXMLString(unScheduleOrderInput));
				}
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER, changeOrderInput);
				api.invoke(env, VSIConstants.API_UNSCHEDULE_ORDER, unScheduleOrderInput);

//				Document scheduleOrderInput = XMLUtil.createDocument("ScheduleOrder");
//				Element scheduleOrderElement = scheduleOrderInput.getDocumentElement();
//				scheduleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
//						strOHK);
//				log.info("scheduleOrderInput= "+XMLUtil.getXMLString(scheduleOrderInput));
//				api.invoke(env, VSIConstants.API_SCHEDULE_ORDER, scheduleOrderInput);
				
			}
		}
	}

}
