package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author ashish.keshri
 * 
 */
public class VSIGetAvailableInventory implements VSIConstants {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetAvailableInventory.class);
	YIFApi api;

	public Document filteredFind(YFSEnvironment env, Document inXML)
			throws Exception {

		Document outInvDoc = null;
		String strZipCode = "";
		String strShipNode = "";
		String strDCNode = "";
		Document outputGetNodeTransferScheduleList = null;
		try {
			
			if(log.isDebugEnabled()){
				log.info("================Inside vsiESProductLocator================================");
				log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
			}
			Element eleShipToAddress = (Element) inXML.getElementsByTagName(
					ELE_SHIP_ADDRESS).item(0);
			strZipCode = eleShipToAddress.getAttribute(ATTR_ZIPCODE);
			// Create input and invoke getSurroundingNodeList
			Document outputGetSurroundingNodeList = invokeGetSurroundingNodeList(
					env, strZipCode);

			NodeList nlNodeList = outputGetSurroundingNodeList
					.getDocumentElement().getElementsByTagName(ELE_NODE);
			Element eleNode = (Element) nlNodeList.item(0);
			strShipNode = eleNode.getAttribute(ATTR_SHIP_NODE);
			outputGetNodeTransferScheduleList = invokeGetNodeTransferScheduleList(
					env, strShipNode);

			NodeList nlNodeTransferSchedule = outputGetNodeTransferScheduleList
					.getElementsByTagName("NodeTransferSchedule");
			Element eleNodeTransferSchedule = (Element) nlNodeTransferSchedule
					.item(0);
			strDCNode = eleNodeTransferSchedule.getAttribute("FromNode");
			inXML.getDocumentElement().setAttribute(ATTR_SHIP_NODE, strDCNode);
			inXML.getDocumentElement().setAttribute(ATTR_ALLOCATION_RULE_ID, VSI_POS_FIND_INV);
			inXML.getDocumentElement().setAttribute(ATTR_ORGANIZATION_CODE, VSICOM_ENTERPRISE_CODE);
			outInvDoc = VSIUtils.invokeAPI(env, "getAvailableInventory", inXML);

			// Updating output
			outInvDoc = massageOutput(outInvDoc);

		} catch (Exception e) {

			Document docPromise = generateDummyResponse(inXML);
			return docPromise;

		}

		return outInvDoc;
	}

	public Document invokeGetSurroundingNodeList(YFSEnvironment env,
			String strZipCode) throws YFSException, RemoteException,
			YIFClientCreationException {

		Document apiInpDoc = SCXmlUtil.createDocument();
		Element eleInput = apiInpDoc.createElement("GetSurroundingNodeList");
		apiInpDoc.appendChild(eleInput);
		eleInput.setAttribute(ATTR_DISTANCE_CONSIDER, "0.1");
		eleInput.setAttribute(ATTR_FULFILLMENT_TYPE, "CUTOVERSTORES");
		eleInput.setAttribute(ATTR_ORGANIZATION_CODE, VSICOM_ENTERPRISE_CODE);
		Element eleShiToAddress = apiInpDoc.createElement(ELE_SHIP_ADDRESS);
		eleInput.appendChild(eleShiToAddress);
		eleShiToAddress.setAttribute(ATTR_COUNTRY, US);
		eleShiToAddress.setAttribute(ATTR_ZIPCODE, strZipCode);

		Document outputDoc = VSIUtils.invokeAPI(env, "getSurroundingNodeList",
				apiInpDoc);
		return outputDoc;

	}

	public Document invokeGetNodeTransferScheduleList(YFSEnvironment env,
			String strShipNode) throws YFSException, RemoteException,
			YIFClientCreationException {

		Document apiInpDoc = SCXmlUtil.createDocument();
		Element eleInput = apiInpDoc.createElement("NodeTransferSchedule");
		apiInpDoc.appendChild(eleInput);
		eleInput.setAttribute("ToNode", strShipNode);

		Document outputDoc = VSIUtils.invokeAPI(env,
				"getNodeTransferScheduleList", apiInpDoc);
		return outputDoc;
	}

	/**
	 * Forms the output for this class
	 * 
	 * @param docOutput
	 * @throws Exception
	 * @throws IllegalArgumentException
	 */
	public Document massageOutput(Document docOutput)
			throws IllegalArgumentException, Exception {
		Element elePromise = docOutput.getDocumentElement();
		ArrayList<Element> alPromiseLineList = SCXmlUtil.getChildren(SCXmlUtil
				.getChildElement(elePromise, VSIConstants.ELE_PROMISE_LINES),
				VSIConstants.ELE_PROMISE_LINE);

		// Calculate the start and end dates
		String strCurrentDate = VSIUtils
				.getCurrentDate(VSIConstants.YYYY_MM_DD);
		String strStartDate = strCurrentDate;
		String strEndDate = VSIUtils.addDaysToPassedDateTime(strCurrentDate,
				VSIConstants.AVAILABLE_QTY_INTERVAL, VSIConstants.YYYY_MM_DD);

		for (Element elePromiseLine : alPromiseLineList) {
			// Remove Options element and add Availability elements
			Element eleAvailability = (Element) elePromiseLine
					.getElementsByTagName(VSIConstants.ELE_AVAILABILITY)
					.item(0);
			Element eleAvailInv = (Element) eleAvailability
					.getElementsByTagName(VSIConstants.ELE_AVAILABLE_INVENTORY)
					.item(0);
			eleAvailInv
					.setAttribute(VSIConstants.ATTR_START_DATE, strStartDate);
			eleAvailInv.setAttribute(VSIConstants.ATTR_END_DATE, strEndDate);

			Element eleShipNodeAvailInv = (Element) eleAvailInv
					.getElementsByTagName(VSIConstants.ELE_SHIP_NODE_AVAIL_INV)
					.item(0);
			if (YFCObject.isVoid(eleShipNodeAvailInv
					.getElementsByTagName(VSIConstants.ELE_INVENTORY))
					|| !(eleShipNodeAvailInv.getElementsByTagName(
							VSIConstants.ELE_INVENTORY).getLength() > 0)) {
				eleAvailInv.setAttribute(
						VSIConstants.ATTR_AVAILABLE_FROM_UNPLANNED_INV,
						VSIConstants.FLAG_N);
				eleAvailInv.setAttribute(
						VSIConstants.ATTR_AVAILABLE_FUTURE_QTY,
						VSIConstants.INT_ZER0_NUM);

				eleShipNodeAvailInv = (Element) eleAvailInv
						.getElementsByTagName(
								VSIConstants.ELE_SHIP_NODE_AVAIL_INV).item(0);
				Element eleInventory = SCXmlUtil.createChild(eleShipNodeAvailInv,
						VSIConstants.ELE_INVENTORY);

				// For unavailable promise line, hard-code:
				// <Inventory Node="9001" Status="1"
				// AvailableFromUnplannedInventory="N"
				// AvailableFutureQuantity="0.00" AvailableOnhandQuantity="0.00"
				// AvailableQuantity="0.00"/>
				eleAvailInv.setAttribute(
						VSIConstants.ATTR_AVAILABLE_ONHAND_QTY,
						VSIConstants.INT_ZER0_NUM);
				eleAvailInv.setAttribute(VSIConstants.ATTR_AVAILABLE_QTY,
						VSIConstants.INT_ZER0_NUM);
				eleInventory.setAttribute(
						VSIConstants.ATTR_AVAILABLE_FROM_UNPLANNED_INV,
						VSIConstants.FLAG_N);
				eleInventory.setAttribute(
						VSIConstants.ATTR_AVAILABLE_FUTURE_QTY,
						VSIConstants.ZERO_DOUBLE);
				eleInventory.setAttribute(
						VSIConstants.ATTR_AVAILABLE_ONHAND_QTY,
						VSIConstants.ZERO_DOUBLE);
				eleInventory.setAttribute(VSIConstants.ATTR_AVAILABLE_QTY,
						VSIConstants.ZERO_DOUBLE);
				eleInventory.setAttribute(VSIConstants.ATTR_NODE,
						VSIConstants.DC_9001);
				eleInventory.setAttribute(VSIConstants.ATTR_STATUS,
						VSIConstants.ONE);
			} else {
				Element eleInventory = (Element) eleShipNodeAvailInv
						.getElementsByTagName(VSIConstants.ELE_INVENTORY).item(
								0);
				eleInventory.setAttribute(VSIConstants.ATTR_STATUS,
						VSIConstants.ONE);
			}
		} // end for loop		
		return docOutput;
	} // end massageOutput()

	public Document generateDummyResponse(Document inXML)
			throws IllegalArgumentException, Exception {
		Document docPromise = SCXmlUtil.createDocument();
		Element elePromise = docPromise.createElement(ELE_PROMISE);
		docPromise.appendChild(elePromise);
		Element elePromiseLines = docPromise.createElement(ELE_PROMISE_LINES);
		elePromise.appendChild(elePromiseLines);
		for (int i=0;i<inXML.getDocumentElement().getElementsByTagName(VSIConstants.ELE_PROMISE_LINE).getLength();i++) {
			Element elePromiseLine = (Element) docPromise.importNode(
					inXML.getDocumentElement().getElementsByTagName(VSIConstants.ELE_PROMISE_LINE).item(i), true);
			elePromiseLines.appendChild(elePromiseLine);

			String strCurrentDate = VSIUtils
					.getCurrentDate(VSIConstants.YYYY_MM_DD);
			String strStartDate = strCurrentDate;
			String strEndDate = VSIUtils.addDaysToPassedDateTime(strCurrentDate,
					VSIConstants.AVAILABLE_QTY_INTERVAL, VSIConstants.YYYY_MM_DD);

			// Remove Options element and add Availability elements
			Element eleAvailability = SCXmlUtil.createChild(elePromiseLine,
					VSIConstants.ELE_AVAILABILITY);
			Element eleAvailInv = SCXmlUtil.createChild(eleAvailability,
					VSIConstants.ELE_AVAILABLE_INVENTORY);
			eleAvailInv.setAttribute(
					VSIConstants.ATTR_AVAILABLE_FROM_UNPLANNED_INV,
					VSIConstants.FLAG_N);
			eleAvailInv.setAttribute(VSIConstants.ATTR_AVAILABLE_FUTURE_QTY,
					VSIConstants.INT_ZER0_NUM);
			eleAvailInv.setAttribute(VSIConstants.ATTR_START_DATE, strStartDate);
			eleAvailInv.setAttribute(VSIConstants.ATTR_END_DATE, strEndDate);

			Element eleShipNodeAvailInv = SCXmlUtil.createChild(eleAvailInv,
					VSIConstants.ELE_SHIP_NODE_AVAIL_INV);
			Element eleInventory = SCXmlUtil.createChild(eleShipNodeAvailInv,
					VSIConstants.ELE_INVENTORY);

			// For unavailable promise line, hard-code:
			// <Inventory Node="9001" Status="1" AvailableFromUnplannedInventory="N"
			// AvailableFutureQuantity="0.00" AvailableOnhandQuantity="0.00"
			// AvailableQuantity="0.00"/>
			eleAvailInv.setAttribute(VSIConstants.ATTR_AVAILABLE_ONHAND_QTY,
					VSIConstants.INT_ZER0_NUM);
			eleAvailInv.setAttribute(VSIConstants.ATTR_AVAILABLE_QTY,
					VSIConstants.INT_ZER0_NUM);
			eleInventory.setAttribute(
					VSIConstants.ATTR_AVAILABLE_FROM_UNPLANNED_INV,
					VSIConstants.FLAG_N);
			eleInventory.setAttribute(VSIConstants.ATTR_AVAILABLE_FUTURE_QTY,
					VSIConstants.ZERO_DOUBLE);
			eleInventory.setAttribute(VSIConstants.ATTR_AVAILABLE_ONHAND_QTY,
					VSIConstants.ZERO_DOUBLE);
			eleInventory.setAttribute(VSIConstants.ATTR_AVAILABLE_QTY,
					VSIConstants.ZERO_DOUBLE);
			eleInventory.setAttribute(VSIConstants.ATTR_NODE, VSIConstants.DC_9001);
			eleInventory.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.ONE);
		}

		return docPromise;
	}
}
