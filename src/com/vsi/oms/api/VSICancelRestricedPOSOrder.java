package com.vsi.oms.api;

import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

//this class is used for cancelling pos order if they have restricted item
public class VSICancelRestricedPOSOrder {
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelRestricedPOSOrder.class);

	public void handlePOSCancel(YFSEnvironment env, Document docInput) throws DOMException, Exception {
		if(log.isDebugEnabled()){
			log.debug("VSICancelRestricedPOSOrder.handlePOSCancel : START" + XMLUtil.getXMLString(docInput));
		}
		
		Element eleOrder = docInput.getDocumentElement();

		// start forming changeOrder document to cancel the order if it has CBD
		// restricted item.
		Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "NO_STORE_PICK");
		eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, "Restricted Item");
		eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");
		eleChangeOrder.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
		eleChangeOrder.setAttribute(VSIConstants.ATTR_SELECT_METHOD, VSIConstants.SELECT_METHOD_WAIT);

		String strState = null;
		String strCountry = null;
		String strZipCode = null;
		boolean boolIsOrdCanReqd = false;
		boolean boolIsAddRestricted = false;
		boolean boolEntByRestricted = false;
		boolean boolIsStoreRestricted = false;
		String strEnteredBy = eleOrder.getAttribute("EnteredBy");
		//OMS-2202 start
		String strOrderType= eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		//OMS-2202 end
		// loop through all the orderLines
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		for (Element eleOrderLine : arrOrderLines) {
			Element eleItem = (Element) eleOrderLine.getElementsByTagName(VSIConstants.ELE_ITEM).item(0);
			String strItemId = eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
			Document inDocItemList = createInputForGetItemList(strItemId);
			String strShipNode = eleOrderLine.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			String strLineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			if (VSIConstants.LINETYPE_STH.equals(strLineType)) {

				Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine,
						VSIConstants.ELE_PERSON_INFO_SHIP_TO);
				if (!YFCObject.isVoid(elePersonInfoShipTo)) {
					strState = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_STATE);
					strCountry = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_COUNTRY);
					strZipCode = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ZIPCODE);
					if (!YFCCommon.isVoid(strState) && !YFCCommon.isVoid(strCountry) && !YFCCommon.isVoid(strZipCode)) {
						if (vsiRestrictedItemCheck(inDocItemList, env, strState, strCountry, strZipCode, strItemId))
							boolIsAddRestricted = true;

					}
					
				}
				//OMS-2202 start
				if((!YFCObject.isVoid(strOrderType) &&VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType))){
				boolEntByRestricted = isRestrictedShipNode(env, inDocItemList, strItemId, strEnteredBy);
				}
				//OMS-2202 end
				if (boolIsAddRestricted || boolEntByRestricted) {
					boolIsOrdCanReqd = true;
					break;
				}

			} else if (VSIConstants.LINETYPE_PUS.equals(strLineType)) {
				//OMS-2202 start
				if((!YFCObject.isVoid(strOrderType) &&VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType))){
				boolEntByRestricted = isRestrictedShipNode(env, inDocItemList, strItemId, strEnteredBy);
				}
				//OMS-2202 end
				boolIsStoreRestricted = isRestrictedShipNode(env, inDocItemList, strItemId, strShipNode);
				if (boolIsStoreRestricted || boolEntByRestricted) {
					boolIsOrdCanReqd = true;
					break;
				}

			} else if (VSIConstants.LINETYPE_STS.equals(strLineType)) {

				boolIsStoreRestricted = isRestrictedShipNode(env, inDocItemList, strItemId, strShipNode);
				if (boolIsStoreRestricted) {
					boolIsOrdCanReqd = true;
					break;
				}

			}
			if(log.isDebugEnabled()){
				log.debug("boolIsAddRestricted"+boolIsAddRestricted);
				log.debug("boolEntByRestricted"+boolEntByRestricted);
				log.debug("boolIsStoreRestricted"+boolIsStoreRestricted);
				log.debug("boolIsOrdCanReqd"+boolIsOrdCanReqd);
			}
			
		}
		if (boolIsOrdCanReqd) {
			if(log.isDebugEnabled()){
				log.debug("VSICancelRestricedPOSOrder.docChangeOrder : START" + XMLUtil.getXMLString(docChangeOrder));
			}
			Document docOut=VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
			if(log.isDebugEnabled()){
				log.debug("VSICancelRestricedPOSOrder.docChangeOrder : STOP" + XMLUtil.getXMLString(docOut));
			}
			
		}
	}

//this method is used for creating getItemList input
	private Document createInputForGetItemList(String itemId) {
		// Create a new document with root element as OrderInvoiceDetail
		Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
		Element eleItem = docInput.getDocumentElement();
		eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID, itemId);
		eleItem.setAttribute("OrganizationCode", "VSI-Cat");
		eleItem.setAttribute("UnitOfMeasure", "EACH");
		return docInput;
	}

	// method for checking restricted item
	private boolean vsiRestrictedItemCheck(Document inDocItemList, YFSEnvironment env, String sState, String sCountry,
			String sZipCode, String itemid) throws Exception {
		Document outDoc = null;

		Document docItemXml = XMLUtil.createDocument(VSIConstants.ELE_ITEM);
		Element eleItemInput = docItemXml.getDocumentElement();
		eleItemInput.setAttribute(VSIConstants.ITEM_ID, itemid);
		eleItemInput.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, VSIConstants.ENT_VSI_CAT);
		eleItemInput.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
		eleItemInput.setAttribute(VSIConstants.ATTR_ITEM_GROUP_CODE, VSIConstants.ITEM_GROUP_CODE_PROD);

		outDoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_ITEM_LIST_WITH_SHIP_RESTRICTIONS, docItemXml);
		Element eleItemList = outDoc.getDocumentElement();

		if (eleItemList.hasChildNodes()) {
			Element eleItem = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);
			Element eleExtn = SCXmlUtil.getChildElement(eleItem, VSIConstants.ELE_EXTN);
			if (!YFCObject.isVoid(eleExtn) && "Y".equals(eleExtn.getAttribute("ExtnIsRestrictedItem"))) {

				Element eleRestrictionList = SCXmlUtil.getChildElement(eleExtn,
						VSIConstants.ELE_VSI_SHIP_RESTRICTED_ITEM_LIST);
				if (!YFCObject.isVoid(eleRestrictionList)) {

					if (eleRestrictionList.hasChildNodes()) {
						// Get the state and country of the pick up store
						if (!YFCObject.isVoid(sState) && !YFCObject.isVoid(sCountry)) {

							NodeList nlItemRestriction = eleRestrictionList.getChildNodes();
							for (int x = 0; x < nlItemRestriction.getLength(); x++) {

								Element eleItemRestriction = (Element) nlItemRestriction.item(x);
								String restrictedState = eleItemRestriction.getAttribute(VSIConstants.ATTR_STATE);
								String restrictedCountry = eleItemRestriction.getAttribute(VSIConstants.ATTR_COUNTRY);
								// If (State = input state and Country = input country) OR (State = "" and
								// Country = input country)
								if ((YFCObject.isVoid(restrictedState) && restrictedCountry.equalsIgnoreCase(sCountry))
										|| (restrictedState.equalsIgnoreCase(sState)
												&& restrictedCountry.equalsIgnoreCase(sCountry))) {
									return true;
								}
							}
						}
					}
				}
			}
		} // end of checking strExtnIsRestrcitedItem
		return false;
	}

	private boolean isRestrictedShipNode(YFSEnvironment env, Document inDocItemList, String itemId, String strStoreNo)
			throws Exception {
		Document getShipNodeListIn = null;
		String strState = null;
		String strCountry = null;
		String strZipCode = null;
		getShipNodeListIn = SCXmlUtil.createDocument("ShipNode");
		Element eleShipNodeEle = getShipNodeListIn.getDocumentElement();
		eleShipNodeEle.setAttribute(VSIConstants.ATTR_SHIP_NODE, strStoreNo);
		eleShipNodeEle.setAttribute(VSIConstants.ATTR_MAX_RECORDS, "1");

		Document shipNodeDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetShipNodeList.xml", "getShipNodeList",
				getShipNodeListIn);

		if (!YFCObject.isNull(shipNodeDoc)) {
			Element eleShipNode = SCXmlUtil.getChildElement(shipNodeDoc.getDocumentElement(), "ShipNode");
			// get the ship node address and check for restriction for each of the item

			Element eleStorePersonInfo = SCXmlUtil.getChildElement(eleShipNode, VSIConstants.ELE_SHIP_NODE_PERSON);

			if (!YFCObject.isVoid(eleStorePersonInfo)) {
				strState = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_STATE);
				strCountry = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
				strZipCode = eleStorePersonInfo.getAttribute(VSIConstants.ATTR_ZIPCODE);

				if (!YFCCommon.isVoid(strState) && !YFCCommon.isVoid(strCountry) && !YFCCommon.isVoid(strZipCode)) {
					if (vsiRestrictedItemCheck(inDocItemList, env, strState, strCountry, strZipCode, itemId))
						return true;

				}
			}

		}
		return false;
	}
}
