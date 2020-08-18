package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSITrimDTCOrderConfirmationMsg  implements VSIConstants{
	
 private YFCLogCategory log = YFCLogCategory.instance(VSITrimDTCOrderConfirmationMsg.class);
 
 /**invoking the service VSIMarketplaceBackorder
  * 
  * @param env
  * @param inXML
  * @throws YFSException
  * @throws RemoteException
  * @throws ParserConfigurationException
  * @throws YIFClientCreationException
  */
	 
	 public Document trimMsg(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException,
				YIFClientCreationException {
		 
		Document docGetOrderList = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleGetOrderList = docGetOrderList.getDocumentElement();
		
		eleGetOrderList.setAttribute(ATTR_ORDER_HEADER_KEY, inXML.getDocumentElement().getAttribute(ATTR_ORDER_HEADER_KEY));
		 
		Document docOrderList = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORDER_LIST_VSI_TRIM_DTC_CFM_MSG, API_GET_ORDER_LIST, docGetOrderList);
		Element eleOrderList = docOrderList.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
		// Get and add calendar info for the ship node
		// The calendar info is in the DTC confirmation mapping sheet, but since this class is only invoked for SHP lines on DRAFT_ORDER_CONFIRM.ON_SUCESS,
		// it will not have a ship node on the line, as that will not be stamped until the order is scheduled
		/*Element eleOrderLine = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES), ELE_ORDER_LINE);
		String strShipNode = eleOrderLine.getAttribute(ATTR_SHIP_NODE);
		if (!YFCObject.isVoid(strShipNode)) {
			Document docGetCalendar = SCXmlUtil.createDocument(ELE_CALENDAR);
			Element eleGetCalendar = docGetCalendar.getDocumentElement();
			eleGetCalendar.setAttribute(ATTR_ORGANIZATION_CODE, strShipNode);
			Document docCalendarList = VSIUtils.invokeAPI(env, TEMPLATE_GET_CALENDAR_LIST_VSI_TRIM_DTC_CFM_MSG, API_GET_CALENDAR_LIST, docGetCalendar);
			Element eleCalendarList = docCalendarList.getDocumentElement();
			if (eleCalendarList.hasChildNodes()) {
				Element eleCalendar = SCXmlUtil.getChildElement(eleCalendarList, ELE_CALENDAR);
				SCXmlUtil.importElement(eleOrder, eleCalendar);
			}
		}*/
			if(inXML.getDocumentElement().hasAttribute("To"))
			{
				eleOrder.setAttribute("CustomerEMailID", inXML.getDocumentElement().getAttribute("To"));
			}
		 	return SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrder));
		}

}//end of class VSIMarketplaceBackorder
