package com.vsi.som.shipment;

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

public class VSICreateShipmentOnRelease extends VSIBaseCustomAPI implements VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSICreateShipmentOnRelease.class);
	public void createShipmentOnRelease(YFSEnvironment env, Document inXml)
	{
		try
		{
		Element orderElement = inXml.getDocumentElement();
		String orderHeaderKey = orderElement.getAttribute(ATTR_ORDER_HEADER_KEY);
		Element orderLine = (Element) orderElement.getElementsByTagName(ELE_ORDER_LINE).item(0);
		NodeList orderLineNode = orderElement.getElementsByTagName(ELE_ORDER_LINE);
		Document createShipmentInput = XMLUtil.createDocument(ELE_SHIPMENT);
		Element eleShipment = createShipmentInput.getDocumentElement();
		eleShipment.setAttribute(ATTR_DOCUMENT_TYPE, orderElement.getAttribute(ATTR_DOCUMENT_TYPE));
		eleShipment.setAttribute(ATTR_ORDER_NO, orderElement.getAttribute(ATTR_ORDER_NO));
		eleShipment.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		eleShipment.setAttribute(ATTR_SCAC, orderElement.getAttribute(ATTR_SCAC));
		eleShipment.setAttribute(ATTR_SHIP_NODE, orderLine.getAttribute(ATTR_SHIP_NODE));
		eleShipment.setAttribute(ATTR_SHIPMENT_TYPE, ATTR_SOM_SHIPMENT_TYPE);
		eleShipment.setAttribute(ATTR_SHIP_CUST_PO_NO, orderLine.getAttribute(ATTR_CUSTOMER_PO_NO));
		Element eleShipmentLines = createShipmentInput.createElement(ELE_SHIPMENT_LINES);
		for(int i=0; i< orderLineNode.getLength(); i++)
		{
			Element orderLineElem = (Element) orderElement.getElementsByTagName(ELE_ORDER_LINE).item(i);
			Element itemEle = (Element) orderLineElem.getElementsByTagName(ELE_ITEM).item(0);
			Element orderStatusesEle = (Element) orderLineElem.getElementsByTagName(ELE_ORDER_STATUSES).item(0);
			Element orderStatusEle = (Element) orderStatusesEle.getElementsByTagName(ELE_ORDER_STATUS).item(0);
			Element eleShipmentLine = SCXmlUtil.createChild(eleShipmentLines, ELE_SHIPMENT_LINE);
			eleShipmentLine.setAttribute(ATTR_ITEM_ID, itemEle.getAttribute(ATTR_ITEM_ID));
			eleShipmentLine.setAttribute(ATTR_ORDER_LINE_KEY, orderLineElem.getAttribute(ATTR_ORDER_LINE_KEY));
			eleShipmentLine.setAttribute(ATTR_PRIME_LINE_NO, orderLineElem.getAttribute(ATTR_PRIME_LINE_NO));
			eleShipmentLine.setAttribute(ATTR_SUB_LINE_NO, orderLineElem.getAttribute(ATTR_SUB_LINE_NO));
			eleShipmentLine.setAttribute(ATTR_QUANTITY, orderStatusEle.getAttribute(ATTR_STATUS_QUANTITY));			
			eleShipmentLine.setAttribute(ATTR_PRODUCT_CLASS, itemEle.getAttribute(ATTR_PRODUCT_CLASS));
			eleShipmentLine.setAttribute(ATTR_UOM, itemEle.getAttribute(ATTR_UOM));		
			eleShipmentLine.setAttribute(ATTR_ORDER_RELEASE_KEY, orderStatusEle.getAttribute(ATTR_ORDER_RELEASE_KEY));			
			Document orderReleaseInput = XMLUtil.createDocument(ELE_ORDER_RELEASE);
			Element orderReleaseEle = orderReleaseInput.getDocumentElement();
			orderReleaseEle.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			orderReleaseEle.setAttribute(ATTR_ORDER_RELEASE_KEY, orderStatusEle.getAttribute(ATTR_ORDER_RELEASE_KEY));
			Document releaseListOutputDoc = VSIUtils.invokeAPI(env,TEMPLATE_GET_ORD_REL_LIST_SEND_RELEASE,API_GET_ORDER_RELEASE_LIST, orderReleaseInput);
			Element orderReleaseListEle = releaseListOutputDoc.getDocumentElement();
			Element orderReleaseElem = (Element) orderReleaseListEle.getElementsByTagName(ELE_ORDER_RELEASE).item(0);
			eleShipmentLine.setAttribute(ATTR_RELEASE_NO, orderReleaseElem.getAttribute(ATTR_RELEASE_NO));
			eleShipmentLines.appendChild(eleShipmentLine);
		}
		eleShipment.appendChild(eleShipmentLines);
		VSIUtils.invokeAPI(env, API_CREATE_SHIPMENT,createShipmentInput);
		}
		catch(Exception e)
		{
			log.info("Exception in VSICreateShipmentOnRelease.createShipmentOnRelease() as below => ");
			e.printStackTrace();
		}
	}	
}