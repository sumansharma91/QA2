package com.vsi.oms.shipment.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is called from Order Summary screen in the Call Center to get the tracking numbers and URLs for STS orders
 * 
 * 
 * @author IBM
 *
 */
public class VSIGetSTSTrackingInfoAPI extends VSIBaseCustomAPI implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetSTSTrackingInfoAPI.class);
	
	public Document getSTSTrackingInfo (YFSEnvironment env, Document docInput) throws YIFClientCreationException, RemoteException {
		log.beginTimer("VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo : START");
		
		try {
			Element eleInput = docInput.getDocumentElement();
			Element eleOrderLineList = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(eleInput, ELE_OUTPUT),
					ELE_ORDER_LINE_LIST);
			ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(eleOrderLineList, ELE_ORDER_LINE);
			for (Element eleOrderLine : alOrderLineList) {
				String strStatus = eleOrderLine.getAttribute(ATTR_STATUS);
				if (strStatus.equals(STATUS_STS_COMPLETE) || strStatus.equals(STATUS_NOACTION)
						|| strStatus.equals(STATUS_PARTL_STS_COMPLETE) || strStatus.equals(STATUS_PARTL_STORE_RECEIVED)
						|| strStatus.equals(STATUS_STORE_RECEIVED) || strStatus.equals(STATUS_ACKNOWLEDGE)
						|| strStatus.equals(STATUS_PICKED)) {
					Document docGetCompOrdLineList = SCXmlUtil.createDocument(ELE_ORDER_LINE);
					Element eleGetCompOrdLineList = docGetCompOrdLineList.getDocumentElement();
					eleGetCompOrdLineList.setAttribute(ATTR_CHAINED_FROM_ORDER_LINE_KEY, eleOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
					Document docCompOrdLineList = VSIUtils.invokeAPI(env, TEMPLATE_GET_COMP_ORD_LINE_LIST_GET_STS_TRACK_INFO, API_GET_COMPLETE_ORDER_LINE_LIST, docGetCompOrdLineList);
					Element eleCompOrdLineList = docCompOrdLineList.getDocumentElement();
					Element eleTrackingInfoList = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(eleCompOrdLineList, ELE_ORDER_LINE),
							ELE_TRACKING_INFO_LIST);
					eleOrderLine.removeChild(SCXmlUtil.getChildElement(eleOrderLine, ELE_TRACKING_INFO_LIST));
					SCXmlUtil.importElement(eleOrderLine, eleTrackingInfoList);
				}
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo() : " , re);
			throw re;
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo() : " , yife);
			throw yife;
		} catch (YFSException yfse) {
			log.error("YFSException in VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo() : " , e);
			throw e;
		}

		log.endTimer("VSIGetSTSTrackingInfoAPI.getSTSTrackingInfo : END");
		return docInput;
	}
}
