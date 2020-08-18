package com.vsi.oms.api;

import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleProcessOrderHoldOnShipment implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleProcessOrderHoldOnShipment.class);
	public Document processShipmentMessage(YFSEnvironment env, Document inXML) throws Exception {
		log.beginTimer("VSIWholesaleProcessOrderHoldOnShipment.processShipmentMessage");
		Document docOrderList = null;
		if(log.isDebugEnabled()) {
			log.debug("VSIWholesaleProcessOrderHoldOnShipment.processShipmentMessage: Printing Input XML:" + SCXmlUtil.getString(inXML));
		}
		
		Element eleShipment = inXML.getDocumentElement();
		String strOHKey = eleShipment.getAttribute(ATTR_ORDER_HEADER_KEY);
		if(!YFCCommon.isVoid(strOHKey)) {
			docOrderList = getHoldTypesOnOrder(env, strOHKey);
			if(!YFCCommon.isVoid(docOrderList)) {
				Element eleOrder = docOrderList.getDocumentElement();
				List<Element>  nlHoldTypes = XMLUtil.getElementListByXpath(eleOrder, "//OrderList/Order/OrderHoldTypes/OrderHoldType[@HoldType='VSI_WH_INVCREATE_HLD']");
				if(!YFCObject.isVoid(nlHoldTypes) && nlHoldTypes.size() > 0 && nlHoldTypes.get(0).getAttribute(ATTR_STATUS).equals(STATUS_CREATE)) {
					if(log.isDebugEnabled()){
					log.debug("VSIWholesaleProcessOrderHoldOnShipment.processShipmentMessage: Invoice hold is already present on the order");
					}
				}
				else {
					if(log.isDebugEnabled()){
					log.debug("VSIWholesaleProcessOrderHoldOnShipment.processShipmentMessage: Adding Invoice hold");
					}
					applyHoldonOrder(env,strOHKey);
				}
			}
		}
		log.endTimer("VSIWholesaleProcessOrderHoldOnShipment.processShipmentMessage");
		return inXML;
	}
	
	private void applyHoldonOrder(YFSEnvironment env, String strOrderHeaderKey) throws Exception {
		log.beginTimer("VSIWholesaleProcessOrderHoldOnShipment.applyHoldonOrder");
		Document docChangeOrderInput = XMLUtil.createDocument(ELE_ORDER);
		Element eleOrder = docChangeOrderInput.getDocumentElement();
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
		Element eleHoldTypes = SCXmlUtil.createChild(eleOrder, ELE_ORDER_HOLD_TYPES);
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(ATTR_HOLD_TYPE, ATTR_VSI_WH_INVCREATE_HLD);
		eleHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);
		Document docChangeOrderOutput =VSIUtils.invokeService(env,SERVICE_CHANGE_ORDER, docChangeOrderInput);
		log.endTimer("VSIWholesaleProcessOrderHoldOnShipment.applyHoldonOrder");
	}
	
	private Document getHoldTypesOnOrder(YFSEnvironment env, String strOrderHeaderKey) throws Exception {
		log.beginTimer("VSIWholesaleProcessOrderHoldOnShipment.getHoldTypesOnOrder");
		Document docGetOrderListInput = XMLUtil.createDocument(ELE_ORDER);
		Element eleOrder = docGetOrderListInput.getDocumentElement();
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
		Document docGetOrderListOutput=VSIUtils.invokeService(env,SERVICE_GET_ORDER_LIST, docGetOrderListInput);
		log.endTimer("VSIWholesaleProcessOrderHoldOnShipment.getHoldTypesOnOrder");
		return docGetOrderListOutput;
	}
}
