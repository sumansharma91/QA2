package com.vsi.oms.shipment.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is invoked by VSIShipConfirmFromWMSIntegServer via
 * VSIProcessShipConfirmMessageWrapper service
 * 
 * Input:
 * 
 * 
 * @author IBM
 *
 */
public class VSIProcessShipConfirmFromWMS extends VSIBaseCustomAPI implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessShipConfirmFromWMS.class);
	public String strOrderEnteredBy = "";
	public String strEnterpriseCode = "";
	String strReleaseNo = "";
	String strTOOrderNo = "";
	boolean bIsTO = false;
	boolean bIsInternationalOrder = false;
	String strShipNode = "";
	String strCustomerPoNo = "";
	boolean cancelMCLOrder = false; // Open Item: What needs to be done when a
									// partially shipped order is cancelled?
	public String strOrderType = "";

	public Document processShipConfirmFromWMS(YFSEnvironment env, Document inputDoc) throws Exception {
		log.beginTimer("VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS : START");

		Element eleShipmentList = inputDoc.getDocumentElement();

		Element eleOrder = SCXmlUtil.getChildElement(eleShipmentList, ELE_ORDER);

		if (eleOrder.getAttribute(ATTR_DOCUMENT_TYPE).equals(DOC_TYPE_TO)) {
			bIsTO = true;
		}

		String strDOId = eleOrder.getAttribute(ATTR_DISTRIBUTION_ORDER_ID);
		strShipNode = SCXmlUtil.getXpathAttribute(eleShipmentList, "/ShipmentList/Shipment/@ShipNode");
		if (!YFCCommon.isVoid(strDOId)) {
			String[] arrValues = strDOId.split("\\*");
			strReleaseNo = arrValues[1];
			strCustomerPoNo = arrValues[0];
		}
		try {
			isInternationalOrder(env, eleOrder);
			processReleaseCancelation(env, eleOrder);

			if (!cancelMCLOrder) { // If order is partially shipped, entire
									// order should be cancelled
				ArrayList<Element> arrShipments = SCXmlUtil.getChildren(eleShipmentList, ELE_SHIPMENT);

				// MC-40 : Modified to consolidate shipments for MCL orders :
				// BEGIN
				if (arrShipments.size() > 1) {
					// Merge the shipments and let it fall through
					if (ENT_MCL.equals(strEnterpriseCode)) {
						consolidateShipments(arrShipments, eleShipmentList);
						arrShipments = SCXmlUtil.getChildren(eleShipmentList, ELE_SHIPMENT);
					}
				}
				// MC-40 : Modified to consolidate shipments for MCL orders :
				// END

				for (Element eleShipment : arrShipments) {
					Map<String, String> ItemDetailForShip = new HashMap<String, String>();
					strShipNode = eleShipment.getAttribute(ATTR_SHIP_NODE);
					eleShipment.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
					if (bIsTO && !YFCCommon.isVoid(strTOOrderNo)) {
						eleShipment.setAttribute(ATTR_ORDER_NO, strTOOrderNo);
					}

					Element containers = SCXmlUtil.getChildElement(eleShipment, "Containers");
					Element container = SCXmlUtil.getChildElement(containers, "Container");
					String trackingNo = SCXmlUtil.getAttribute(container, "TrackingNo");
					eleShipment.setAttribute("TrackingNo", trackingNo);
					// Append ReleaseNo at ShipmentLines present at container
					// level
					NodeList nlContainer = eleShipment.getElementsByTagName(ELE_CONTAINER);
					SCXmlUtil.setAttribute(eleShipment, ATTR_NUM_OF_CARTONS, nlContainer.getLength());
					NodeList eleContainerList = XMLUtil.getNodeListByXpath(inputDoc, XPATH_CONTAINER_SHIPMENT_LINES);
					for (int i = 0; i < eleContainerList.getLength(); i++) {
						Element eleContainerShipmentLine = (Element) eleContainerList.item(i);
						eleContainerShipmentLine.setAttribute(ATTR_RELEASE_NO, strReleaseNo);
						if (bIsTO && !YFCCommon.isVoid(strTOOrderNo)) {
							eleContainerShipmentLine.setAttribute(ATTR_ORDER_NO, strTOOrderNo);
							eleContainerShipmentLine.setAttribute(ATTR_DOCUMENT_TYPE, DOC_TYPE_TO);
						}
					}

					Element eleShipmentLines = XMLUtil.getFirstElementByName(eleShipment, ELE_SHIPMENT_LINES);
					NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName(ELE_SHIPMENT_LINE);
					// loop across shipment lines to append release number and
					// form input for changeRelease API
					for (int i = 0; i < nlShipmentLine.getLength(); i++) {
						Element eleShipmentLine = (Element) nlShipmentLine.item(i);
						// set the release number of the shipment lines.
						eleShipmentLine.setAttribute(ATTR_RELEASE_NO, strReleaseNo);
						if (bIsTO && !YFCCommon.isVoid(strTOOrderNo)) {
							eleShipmentLine.setAttribute(ATTR_ORDER_NO, strTOOrderNo);
							eleShipmentLine.setAttribute(ATTR_DOCUMENT_TYPE, DOC_TYPE_TO);
							String itemID = eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
							String containerQty = eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY);
							ItemDetailForShip.put(itemID, Integer.toString(Double.valueOf(containerQty).intValue()));
						}
					}
					// OMS-1049
					String strActualShipDate = eleShipment.getAttribute(VSIConstants.ATTR_ACTUAL_SHIPDATE);
					if (null != strActualShipDate && !XmlUtils.isVoid(strActualShipDate))
						eleShipment.setAttribute(VSIConstants.ATTR_ACTUAL_SHIP_DATE, strActualShipDate);
					//OMS-1892: Start
					isWholesaleOrder(env, eleOrder);
										
					String strCurrentShipDate = eleShipment.getAttribute(VSIConstants.ATTR_CURRENT_SHIPDATE);
					if (!WHOLESALE.equalsIgnoreCase(strOrderType) && !YFCCommon.isVoid(strCurrentShipDate)) 
					{
						ArrayList<Element> eleGetCommonCodeShipDateList = VSIUtils.getCommonCodeList(env, CODE_TYPE_VSI_SHIPDATE_RULE,CODE_VALUE_STAMP_WMS_DATE, "");
						if (!eleGetCommonCodeShipDateList.isEmpty() && (FLAG_N
								.equalsIgnoreCase(eleGetCommonCodeShipDateList.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION))))	
						{
						eleShipment.setAttribute(ATTR_ACTUAL_SHIP_DATE, strCurrentShipDate);
						
						}
						
					}
					Element eleExtn=SCXmlUtil.getChildElement(eleShipment, ELE_EXTN);
					if(eleExtn==null){
						eleExtn = SCXmlUtil.createChild(eleShipment, VSIConstants.ELE_EXTN);
					}
					eleExtn.setAttribute(VSIConstants.ATTR_EXTN_SHIP_DATE, strActualShipDate);
					
					//OMS-1892: End
					// OMS-1049
					// invoke confirmShipment API
					Document docShipment = SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipment));
					// Transfer Inventory Ownership befor calling confirm
					// shipment.
					//OMS-2225:Inventory Issue in OMS causing 0 inventory STH orders dropped to DC : Start
					 ArrayList<Element> listShipmentFlag=VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_SHIPMENT_CODE_TYPE, VSIConstants.ATTR_SHIPMENT_FLOW, VSIConstants.ATTR_DEFAULT);
						if(!listShipmentFlag.isEmpty()){
						Element eleCommonCode=listShipmentFlag.get(0);
							String strShipmentFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strShipmentFlag) && VSIConstants.FLAG_N.equalsIgnoreCase(strShipmentFlag)){
								transferInventoryOwnership(env, docShipment);
							}
							else
							{
								transferInventoryOwnershipNewLogic(env, docShipment);
							}
						}
					//OMS-2225: High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC  : End
					VSIUtils.invokeAPI(env, API_CONFIRM_SHIPMENT, docShipment);
					if (bIsTO) {
						env.setTxnObject("EnvShipQty", ItemDetailForShip);
						sendEmail(env, strCustomerPoNo);
						// env.setTxnObject("EnvShipQty","");
					}
				}

				//TrailerLevelASNforWholeSale(env, eleShipmentList, eleOrder);
			}

		} catch (RemoteException re) {
			re.printStackTrace();
			log.error("RemoteException in VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS() : ", re);
			throw re;
		} catch (YIFClientCreationException yife) {
			yife.printStackTrace();
			log.error("YIFClientCreationException in VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS() : ",
					yife);
			throw yife;
		} catch (YFSException yfse) {
			yfse.printStackTrace();
			log.error("YFSException in VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS() : ", yfse);
			throw yfse;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception in VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS() : ", e);
			throw e;
		}
		log.endTimer("VSIProcessShipConfirmFromWMS.processShipConfirmFromWMS : END");
		return inputDoc;
	}

	private void transferInventoryOwnership(YFSEnvironment env, Document docShipment)
			throws YFSException, RemoteException, YIFClientCreationException {

		Element eleShipment = docShipment.getDocumentElement();
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, "ShipmentLines");
		ArrayList<Element> arrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines, "ShipmentLine");
		String strEnterprise = eleShipment.getAttribute(ATTR_ENTERPRISE_CODE);

		// Form input for getInventorySupply
		Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
		eleGetInventorySupply.setAttribute(ATTR_UOM, UOM_EACH);
		eleGetInventorySupply.setAttribute("SupplyType", "ONHAND");
		eleGetInventorySupply.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));

		// Form input for transferInventoryOwnership
		Document docTransferInventory = SCXmlUtil.createDocument(ELE_ITEMS);
		Element eleTransferInventory = docTransferInventory.getDocumentElement();

		for (Element eleShipmentLine : arrShipmentLine) {

			// form input for getInventorySupply
			String strItemId = eleShipmentLine.getAttribute(ATTR_ITEM_ID);
			Double dblRequiredQty = SCXmlUtil.getDoubleAttribute(eleShipmentLine, ATTR_QUANTITY);
			String strFromOrganizationCode = "VSIINV";

			eleGetInventorySupply.setAttribute(ATTR_ITEM_ID, strItemId);

			dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty, eleTransferInventory,
					strFromOrganizationCode, strEnterprise);

			if (dblRequiredQty > 0) {

				if (strEnterprise.equalsIgnoreCase(ENT_ADP)) {
					strFromOrganizationCode = "ADPINV";

				} else if (strEnterprise.equalsIgnoreCase("MCL")) {
					strFromOrganizationCode = "MCLINV";

				} else {
					strFromOrganizationCode = "DTCINV";
				}
				dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,
						eleTransferInventory, strFromOrganizationCode, strEnterprise);
			}

			if (dblRequiredQty > 0) {
				if (strEnterprise.equalsIgnoreCase(ENT_ADP)) {
					strFromOrganizationCode = "DTCINV";
					dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				} else if (!strEnterprise.equalsIgnoreCase("MCL")) {
					strFromOrganizationCode = "MCLINV";
					dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				}
			}

			if (dblRequiredQty > 0) {
				if (strEnterprise.equalsIgnoreCase(ENT_ADP)) {
					strFromOrganizationCode = "MCLINV";
					dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				} else {
					// add VSIINV to transferInventoryOwnership.
					strFromOrganizationCode = "VSIINV";
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
					eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
					eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
					eleTransferItems.setAttribute(ATTR_ITEM_ID, strItemId);
					eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
					dblRequiredQty = 0.0;
				}
			}
			if (dblRequiredQty > 0) {
				// add VSIINV to transferInventoryOwnership.
				strFromOrganizationCode = "VSIINV";
				Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
				eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
				eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
				eleTransferItems.setAttribute("SupplyType", "ONHAND");
				eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));
				eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
				eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
				eleTransferItems.setAttribute(ATTR_ITEM_ID, strItemId);
				eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
				dblRequiredQty = 0.0;
			}
		}
		// invoke transferInventoryOwnership API
		VSIUtils.invokeAPI(env, "transferInventoryOwnership", docTransferInventory);
	}
//OMS-2225: High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC:Start
	private void transferInventoryOwnershipNewLogic(YFSEnvironment env, Document docShipment)
			throws YFSException, RemoteException, YIFClientCreationException {
		Element eleShipment = docShipment.getDocumentElement();
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, "ShipmentLines");
		ArrayList<Element> arrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines, "ShipmentLine");
		String strEnterprise = eleShipment.getAttribute(ATTR_ENTERPRISE_CODE);
		// Form input for getInventorySupply
		Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
		eleGetInventorySupply.setAttribute(ATTR_UOM, UOM_EACH);
		eleGetInventorySupply.setAttribute("SupplyType", "ONHAND");
		eleGetInventorySupply.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));

		// Form input for transferInventoryOwnership
		Document docTransferInventory = SCXmlUtil.createDocument(ELE_ITEMS);
		Element eleTransferInventory = docTransferInventory.getDocumentElement();

		for (Element eleShipmentLine : arrShipmentLine) {

			// form input for getInventorySupply
			String strItemId = eleShipmentLine.getAttribute(ATTR_ITEM_ID);
			Double dblRequiredQty = SCXmlUtil.getDoubleAttribute(eleShipmentLine, ATTR_QUANTITY);
			String strFromOrganizationCode = "VSIINV";

			eleGetInventorySupply.setAttribute(ATTR_ITEM_ID, strItemId);

			dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty, eleTransferInventory,
					strFromOrganizationCode, strEnterprise);

			if (dblRequiredQty > 0) {

				if (ENT_ADP.equalsIgnoreCase(strEnterprise)) {
					strFromOrganizationCode = "ADPINV";

				} else if ("MCL".equalsIgnoreCase(strEnterprise)) {
					strFromOrganizationCode = "MCLINV";

				} else {
					strFromOrganizationCode = "DTCINV";
				}
				dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
						eleTransferInventory, strFromOrganizationCode, strEnterprise);
			}

			if (dblRequiredQty > 0) {
				if (ENT_ADP.equalsIgnoreCase(strEnterprise)) {
					strFromOrganizationCode = "DTCINV";
					dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				} else if (ENT_NAVY_EXCHANGE.equalsIgnoreCase(strEnterprise)) {
					strFromOrganizationCode = "MCLINV";
					dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				}
			}

			if (dblRequiredQty > 0) {
				if (ENT_ADP.equalsIgnoreCase(strEnterprise)) {
					strFromOrganizationCode = "MCLINV";
					dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				} else {
					// add VSIINV to transferInventoryOwnership.
					strFromOrganizationCode = "VSIINV";
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
					eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
					eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
					eleTransferItems.setAttribute(ATTR_ITEM_ID, strItemId);
					eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
					dblRequiredQty = 0.0;
				}
			}

			if (dblRequiredQty > 0) {
				// add VSIINV to transferInventoryOwnership.
				strFromOrganizationCode = "VSIINV";
				Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
				eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
				eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
				eleTransferItems.setAttribute("SupplyType", "ONHAND");
				eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleShipment.getAttribute(ATTR_SHIP_NODE));
				eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
				eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
				eleTransferItems.setAttribute(ATTR_ITEM_ID, strItemId);
				eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
				dblRequiredQty = 0.0;
			}
		}
		// invoke transferInventoryOwnership API
		VSIUtils.invokeAPI(env, "transferInventoryOwnership", docTransferInventory);
	}
	public Double invokeGetInventorySupplyNewLogic(YFSEnvironment env, Document docGetInventorySupply, Double dblRequiredQty,
			Element eleTransferInventory, String strFromOrganizationCode, String strToEnterprise)
			throws YIFClientCreationException, RemoteException {
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(ATTR_ORGANIZATION_CODE, strFromOrganizationCode);
		eleGetInventorySupply.setAttribute("InventoryItemKey", "");
		Document docInventorySupplyOutput = null;
		try {
			docInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
		} catch (Exception e) {
			String strMessage = e.getMessage();
			if (strMessage.contains("INV80_042")) {
				// this issue is because of a product bug. Should be resolved in
				// future with ARH-172.
				// Ignore the exception.
			} else {
				throw e;
			}
		}
		if (!YFCCommon.isVoid(docInventorySupplyOutput)) {
			Element eleInventorySupplyOutput = docInventorySupplyOutput.getDocumentElement();
			Element eleInventoryItemSupplies = SCXmlUtil.getChildElement(eleInventorySupplyOutput, ELE_SUPPLIES);
			ArrayList<Element> arrInventorySupply = SCXmlUtil.getChildren(eleInventoryItemSupplies, "InventorySupply");

			for (Element eleInventoryItemSupply : arrInventorySupply) {
				Double dblAvailableQuantity = SCXmlUtil.getDoubleAttribute(eleInventoryItemSupply, ATTR_QUANTITY);

				// Sayeesh - changes made here
				if (dblAvailableQuantity>0.0) {
					// add items to transferInventoryOwnership
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
					eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
					eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleInventoryItemSupply.getAttribute(ATTR_SHIP_NODE));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strToEnterprise);
					eleTransferItems.setAttribute(ATTR_ITEM_ID, eleInventorySupplyOutput.getAttribute(ATTR_ITEM_ID));

					if (dblAvailableQuantity > dblRequiredQty) {
						eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
						dblRequiredQty = 0.0;
					} else {
						eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblAvailableQuantity));
						dblRequiredQty = dblRequiredQty - dblAvailableQuantity;
					}
					break;
				}

			}
		}
		return dblRequiredQty;
	}

//OMS-2225:High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC: End	
	private Double invokeGetInventorySupply(YFSEnvironment env, Document docGetInventorySupply, Double dblRequiredQty,
			Element eleTransferInventory, String strFromOrganizationCode, String strToEnterprise)
			throws YIFClientCreationException, RemoteException {
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(ATTR_ORGANIZATION_CODE, strFromOrganizationCode);
		eleGetInventorySupply.setAttribute("InventoryItemKey", "");
		Document docInventorySupplyOutput = null;
		try {
			docInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
		} catch (Exception e) {
			String strMessage = e.getMessage();
			if (strMessage.contains("INV80_042")) {
				// this issue is because of a product bug. Should be resolved in
				// future with ARH-172.
				// Ignore the exception.
			} else {
				throw e;
			}
		}
		if (!YFCCommon.isVoid(docInventorySupplyOutput)) {
			Element eleInventorySupplyOutput = docInventorySupplyOutput.getDocumentElement();
			Element eleInventoryItemSupplies = SCXmlUtil.getChildElement(eleInventorySupplyOutput, ELE_SUPPLIES);
			ArrayList<Element> arrInventorySupply = SCXmlUtil.getChildren(eleInventoryItemSupplies, "InventorySupply");

			for (Element eleInventoryItemSupply : arrInventorySupply) {
				Double dblAvailableQuantity = SCXmlUtil.getDoubleAttribute(eleInventoryItemSupply, ATTR_QUANTITY);

				// Sayeesh - changes made here
				if (dblAvailableQuantity != 0.0) {
					// add items to transferInventoryOwnership
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, ELE_ITEM);
					eleTransferItems.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
					eleTransferItems.setAttribute(ATTR_UOM, UOM_EACH);
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute(ATTR_SHIP_NODE, eleInventoryItemSupply.getAttribute(ATTR_SHIP_NODE));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strToEnterprise);
					eleTransferItems.setAttribute(ATTR_ITEM_ID, eleInventorySupplyOutput.getAttribute(ATTR_ITEM_ID));

					if (dblAvailableQuantity > dblRequiredQty) {
						eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblRequiredQty));
					} else {
						eleTransferItems.setAttribute(ATTR_QUANTITY, Double.toString(dblAvailableQuantity));
					}
					dblRequiredQty = dblRequiredQty - dblAvailableQuantity;
					break;
				}

			}
		}
		return dblRequiredQty;
	}

	public void processReleaseCancelation(YFSEnvironment env, Element eleOrder) throws Exception {

		String strDocumentType = eleOrder.getAttribute(ATTR_DOCUMENT_TYPE);
		String strOrderNo = eleOrder.getAttribute(ATTR_ORDER_NO);

		// Prepare document for change release
		// Change release is required if there is
		// any canceled quantity.
		Document docChangeRelease = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
		Element eleChangeRelease = docChangeRelease.getDocumentElement();
		Element eleOrderLines = SCXmlUtil.createChild(eleChangeRelease, ELE_ORDER_LINES);
		// Set attributes required for calling change release
		eleChangeRelease.setAttribute(ATTR_RELEASE_NO, strReleaseNo);

		eleChangeRelease.setAttribute(ATTR_DOCUMENT_TYPE, strDocumentType);
		eleChangeRelease.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		if (bIsTO && !YFCCommon.isVoid(strTOOrderNo)) {
			eleChangeRelease.setAttribute(ATTR_ORDER_NO, strTOOrderNo);
			eleChangeRelease.setAttribute("SalesOrderNo", strTOOrderNo);
		} else {
			eleChangeRelease.setAttribute(ATTR_ORDER_NO, strOrderNo);
			eleChangeRelease.setAttribute("SalesOrderNo", strOrderNo);
		}
		eleChangeRelease.setAttribute(ATTR_OVERRIDE, FLAG_Y);
		eleChangeRelease.setAttribute(ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);

		// get the original order release. This will be used for reversing force
		// allocation in JDA
		Document docOriginalOrderReleaseList = VSIUtils.invokeAPI(env,
				"global/template/api/getOrderReleaseList_VSISendRelease.xml", API_GET_ORDER_RELEASE_LIST,
				docChangeRelease);
		Element eleOriginalOrderReleaseList = docOriginalOrderReleaseList.getDocumentElement();
		Element eleOriginalOrderRelease = SCXmlUtil.getChildElement(eleOriginalOrderReleaseList, ELE_ORDER_RELEASE);

		/**
		 * For MCL Orders - Loop through the //OrderLine list from the
		 * inputOrder. If the ShippedQty != StatusQuantity for that order line
		 * Set UserCanceledQuantity to StatusQuantity
		 */
		if (strEnterpriseCode.equals(ENT_MCL)) {

			NodeList nlOrderLine = eleOrder.getElementsByTagName(ELE_ORDER_LINE);
			// MC-40 : Modified to consolidate order lines for MCL orders :
			// BEGIN
			Element eleShipOrderLines = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES);
			HashMap<String, Integer> mapOrderLineQty = new HashMap<>();
			ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(eleShipOrderLines, ELE_ORDER_LINE);

			// for(int i = 0; i < nlOrderLine.getLength(); i++){
			// Element eleOrderLine = (Element) nlOrderLine.item(i);
			for (Element eleOrderLine : alOrderLineList) {
				String strPrimeLineKey = eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO);
				if (mapOrderLineQty.containsKey(strPrimeLineKey)) {
					int iShippedQty = mapOrderLineQty.get(strPrimeLineKey)
							+ Integer.parseInt(eleOrderLine.getAttribute(ATTR_SHIP_QTY));
					mapOrderLineQty.put(strPrimeLineKey, iShippedQty);
				} else {
					mapOrderLineQty.put(strPrimeLineKey, Integer.parseInt(eleOrderLine.getAttribute(ATTR_SHIP_QTY)));
				}
				eleShipOrderLines.removeChild(eleOrderLine);
			}
			Iterator<Map.Entry<String, Integer>> it = mapOrderLineQty.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
				// String strShippedQty = SCXmlUtil.getAttribute(eleOrderLine,
				// "ShippedQty");
				String strShippedQty = pair.getValue().toString();
				String strOrderLineXPath = "//OrderReleaseList/OrderRelease/OrderLines/OrderLine[@PrimeLineNo="
						+ pair.getKey() + "]";
				Element eleOriginalReleaseOrderLine = SCXmlUtil.getXpathElement(eleOriginalOrderReleaseList,
						strOrderLineXPath);
				Element eleFinalOrderLine = SCXmlUtil.createChild(eleShipOrderLines, ELE_ORDER_LINE);
				eleFinalOrderLine.setAttribute(ATTR_PRIME_LINE_NO, pair.getKey());
				eleFinalOrderLine.setAttribute(ATTR_SHIP_QTY, pair.getValue().toString());
				eleFinalOrderLine.setAttribute(ATTR_ITEM_ID,
						SCXmlUtil.getChildElement(eleOriginalReleaseOrderLine, ELE_ITEM).getAttribute(ATTR_ITEM_ID));
				String strOriginalReleasedQty = eleOriginalReleaseOrderLine.getAttribute(ATTR_STATUS_QTY);
				Integer intOriginalReleasedQty = Integer.parseInt(strOriginalReleasedQty);
				Integer intShippedQty = Integer.parseInt(strShippedQty);
				if (intShippedQty != intOriginalReleasedQty) {

					// If any part of the order is not shipped, the entire order
					// should be cancelled
					cancelMCLOrder = true;
					SCXmlUtil.setAttribute(eleFinalOrderLine, ELE_INVENTORY_NODE_CONTROL, FLAG_Y); // Setting
																									// so
																									// that
																									// only
																									// lines
																									// with
																									// partial
																									// shipped
																									// qty
																									// are
																									// locked
				}
			}
			// MC-40 : Modified to consolidate order lines for MCL orders : END

			if (cancelMCLOrder) {
				for (int i = 0; i < nlOrderLine.getLength(); i++) {

					Element eleOrderLine = (Element) nlOrderLine.item(i);
					String strOrderLineXPath = "//OrderReleaseList/OrderRelease/OrderLines/OrderLine[@PrimeLineNo="
							+ eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO) + "]";
					Element eleOriginalReleaseOrderLine = SCXmlUtil.getXpathElement(eleOriginalOrderReleaseList,
							strOrderLineXPath);
					String strOriginalReleasedQty = eleOriginalReleaseOrderLine.getAttribute(ATTR_STATUS_QTY);

					SCXmlUtil.setAttribute(eleOrderLine, ATTR_USER_CANCELLED_QTY, strOriginalReleasedQty);
					SCXmlUtil.setAttribute(eleOrderLine, "ShippedQty", ZERO_DOUBLE);
				}
			}
		}

		// If release was for a STS order, stamp STS ship node.
		if (bIsTO) {
			String strReceivingNode = eleOriginalOrderRelease.getAttribute(ATTR_RECEIVING_NODE);
			String strShipNode = eleOriginalOrderRelease.getAttribute(ATTR_SHIP_NODE);
			eleOriginalOrderRelease.setAttribute(ATTR_SHIP_NODE, strReceivingNode);
			eleOriginalOrderRelease.setAttribute(ATTR_STS_SHIP_NODE, strShipNode);
		}

		Boolean bCancelQuantityExists = false;

		// Wholesale Change: Start
		isWholesaleOrder(env, eleOrder);
		boolean blRejectFlag = false;
		if (strOrderType.equalsIgnoreCase(WHOLESALE)) {
			ArrayList<Element> eleGetCommonCodeRejectFlagList = VSIUtils.getCommonCodeList(env, VSI_WH_CNCL_ON_REJ,
					strEnterpriseCode, "");
			if (!eleGetCommonCodeRejectFlagList.isEmpty() && (FLAG_Y
					.equalsIgnoreCase(eleGetCommonCodeRejectFlagList.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION))))
				blRejectFlag = true;
		}
		// Wholesale Change: End

		// ARS-268
		HashMap<String, String> hmSTSCancelLineMap = new HashMap<String, String>();
		String strChainedFromOhk = null;

		Element eleOrderLinesInput = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES);
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLinesInput, ELE_ORDER_LINE);
		for (Element eleOrderLineInput : arrOrderLines) {
			String strUserCancelledQty = INT_ZER0_NUM;
			strUserCancelledQty = eleOrderLineInput.getAttribute(ATTR_USER_CANCELLED_QTY);
			String strSignedUserCancelledQty = "-" + strUserCancelledQty;
			int intUserCancelledQty = 0;

			if (!YFCCommon.isVoid(strUserCancelledQty)) {
				intUserCancelledQty = Integer.parseInt(strUserCancelledQty);
			}

			// Check if there is any canceled quantity.
			// In such scenario, changeRelease will be invoked.
			// Also inventory will be suppressed and JDA force allocation
			// will be reversed.
			if (!YFCCommon.isVoid(strUserCancelledQty) && intUserCancelledQty > 0) {

				// append lines to the change release document.
				String strItemId = eleOrderLineInput.getAttribute(ATTR_ITEM_ID);

				Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, ELE_ORDER_LINE);
				eleOrderLine.setAttribute(ATTR_ITEM_ID, strItemId);
				eleOrderLine.setAttribute(ATTR_PRIME_LINE_NO, eleOrderLineInput.getAttribute(ATTR_PRIME_LINE_NO));
				eleOrderLine.setAttribute(ATTR_SUB_LINE_NO, ONE);
				eleOrderLine.setAttribute(ATTR_UOM, UOM_EACH);
				eleOrderLine.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
				eleOrderLine.setAttribute(ATTR_CHANGE_IN_QTY, strSignedUserCancelledQty);
				// OMS-1074 :Modification Starts
				String strXPath = "//OrderReleaseList/OrderRelease/OrderLines/OrderLine[@PrimeLineNo="
						+ eleOrderLineInput.getAttribute(ATTR_PRIME_LINE_NO) + "]";
				Element eleReleaseOrderLine = SCXmlUtil.getXpathElement(eleOriginalOrderReleaseList, strXPath);
				Element eleOrderStatuses = SCXmlUtil.getChildElement(eleReleaseOrderLine, ELE_ORDER_STATUSES);
				ArrayList<Element> alOrderStatusList = SCXmlUtil.getChildren(eleOrderStatuses, ELE_ORDER_STATUS);
				for (Element eleOrderStatus : alOrderStatusList) {
					String strStatus = eleOrderStatus.getAttribute(ATTR_STATUS);
					if (VSIConstants.STATUS_CODE_SENT_TO_NODE.equals(strStatus)
							&& !INT_ZER0_NUM.equals(eleOrderStatus.getAttribute(ATTR_STATUS_QUANTITY))) {
						eleOrderLine.setAttribute(ATTR_FROM_STATUS, STATUS_CODE_SENT_TO_NODE);
					} else {
						eleOrderLine.setAttribute(ATTR_FROM_STATUS, STATUS_CODE_RELEASE_ACKNOWLEDGED);
					}

					break;

				}
				// OMS-1074: Modification Ends
				bCancelQuantityExists = true;

				// Set Original allocated and OrderedQty attributes on the
				// original release document.
				// This is required for reversing the force Allocation in JDA
				String strOrderLineXPath = "//OrderReleaseList/OrderRelease/OrderLines/OrderLine[@PrimeLineNo="
						+ eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO) + "]";
				Element eleOriginalReleaseOrderLine = SCXmlUtil.getXpathElement(eleOriginalOrderReleaseList,
						strOrderLineXPath);
				strChainedFromOhk = eleOriginalReleaseOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_HEADER_KEY);
				String strOriginalReleasedQty = eleOriginalReleaseOrderLine.getAttribute(ATTR_STATUS_QTY);
				int intOriginalReleasedQty = Integer.parseInt(strOriginalReleasedQty);
				int intUpdatedOrderLine = intOriginalReleasedQty - intUserCancelledQty;

				eleOriginalReleaseOrderLine.setAttribute(ATTR_ORG_ALLOCATED_QTY, strOriginalReleasedQty);
				eleOriginalReleaseOrderLine.setAttribute(ATTR_ORD_QTY, Integer.toString(intUpdatedOrderLine));

				// OMS-1121 : Start
				if(log.isDebugEnabled()){
					log.debug("Order release line : " + SCXmlUtil.getString(eleOriginalReleaseOrderLine));
				}
				Element eleExtnFromRelease = SCXmlUtil.getChildElement(eleOriginalReleaseOrderLine,
						VSIConstants.ELE_EXTN);
				boolean isADPBoxLine = false;
				if (!YFCCommon.isVoid(eleExtnFromRelease)) {
					String strExtnSDKLine = eleExtnFromRelease.getAttribute("ExtnSDKLine");
					if (!YFCCommon.isStringVoid(strExtnSDKLine) && "Y".equals(strExtnSDKLine)) {
						if(log.isDebugEnabled()){
							log.debug("order line is an adp box line");
						}
						isADPBoxLine = true;
					}
				}
				// OMS-1121 : End

				// OMS-1316 removing discontinued item check from if condition
				// OMS-1612 removing International Order check from if condition
				if (bIsTO || cancelMCLOrder || isADPBoxLine || blRejectFlag) {
					// cancel the lines
					eleOrderLine.setAttribute(ATTR_TO_STATUS, STATUS_CANCEL);
					eleOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_CANCEL);
					// ARS-268
					if (bIsTO) {
						hmSTSCancelLineMap.put(
								eleOriginalReleaseOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_LINE_KEY),
								Integer.toString(intUpdatedOrderLine));
					}

				} else {
					// back order the lines
					eleOrderLine.setAttribute(ATTR_TO_STATUS, STATUS_BACKORDERED_FROM_NODE);
					eleOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_BO);
				}
				// Suppress Inventory for items at Node if there are any
				// cancellations.
				if (!strEnterpriseCode.equals(ENT_MCL)
						|| SCXmlUtil.getAttribute(eleOrderLineInput, ELE_INVENTORY_NODE_CONTROL).equals(FLAG_Y)) { // Added
																													// to
																													// prevent
																													// inventory
																													// lock
																													// for
																													// MCL
																													// shipment
																													// cancellations
					invokeManageInvNodeControl(env, strItemId, strShipNode);
				}
			}
		}
		// invoke change release if canceled quantity exists
		if (bCancelQuantityExists) {
			env.setTxnObject("FromWMSCancel", "Y");
			if(log.isDebugEnabled()){
				log.debug("Input for changeRelease API : " + SCXmlUtil.getString(docChangeRelease));
			}
			VSIUtils.invokeAPI(env, API_CHANGE_RELEASE, docChangeRelease);

			// Invoke JDA Service to reverse the force allocation.
			eleOriginalOrderRelease.setAttribute("ReverseAllocation", FLAG_Y);
			Document docOriginalOrderRelease = SCXmlUtil.createFromString(SCXmlUtil.getString(eleOriginalOrderRelease));
			VSIUtils.invokeService(env, VSIConstants.SERVICE_PROCESS_RELEASE, docOriginalOrderRelease);
			if (!YFCObject.isVoid(hmSTSCancelLineMap) && hmSTSCancelLineMap.size() > 0) {
				cancelSalesOrder(env, strChainedFromOhk, hmSTSCancelLineMap);
			}
		}
	}

	/*
	 * Method to check if item which are not fulfilled are discontinued items.
	 */

	public boolean isDiscontinuedItem(YFSEnvironment env, String strItemID)
			throws YFSException, RemoteException, YIFClientCreationException {

		// call get item list to check if item is discontinued.
		Document docGetItemList = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
		Element eleGetItemList = docGetItemList.getDocumentElement();
		eleGetItemList.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemID);
		eleGetItemList.setAttribute(VSIConstants.ATTR_MAX_RECORDS, ONE);
		if (!YFCCommon.isVoid(strItemID)) {
			Document docItemList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ITEM_LIST_PRCSS_WMS_SHIP_CONF,
					VSIConstants.API_GET_ITEM_LIST, docGetItemList);
			Element eleItemList = docItemList.getDocumentElement();
			Element eleItem = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);
			Element eleItemExtn = SCXmlUtil.getChildElement(eleItem, VSIConstants.ELE_EXTN);
			if (VSIConstants.FLAG_Y.equals(eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_IS_DISCNTND_ITEM))) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Method to check if the original order was being shipped to an
	 * international address.
	 */
	public void isInternationalOrder(YFSEnvironment env, Element eleOrder)
			throws YFSException, RemoteException, YIFClientCreationException {
		// if DocumentType is 0001 call getOrderList.
		// If Document Type is 0006, call getOrderLineList first as CustomerPONo
		// is sent in OrderNo
		String strOrderNo = eleOrder.getAttribute(ATTR_ORDER_NO);
		String strChainedFromOHKey = null;
		if (bIsTO) {
			Document docGetOrderReleaseList = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
			Element eleGetOrderReleaseList = docGetOrderReleaseList.getDocumentElement();
			eleGetOrderReleaseList.setAttribute("CustomerPoNo", strCustomerPoNo);
			eleGetOrderReleaseList.setAttribute(ATTR_RELEASE_NO, strReleaseNo);

			Document docReleaseListTemplate = SCXmlUtil.createDocument(ELE_ORDER_RELEASE_LIST);
			Element eleReleaseListTemplate = docReleaseListTemplate.getDocumentElement();
			Element eleReleaseTemplate = SCXmlUtil.createChild(eleReleaseListTemplate, ELE_ORDER_RELEASE);
			eleReleaseTemplate.setAttribute(ATTR_RELEASE_NO, ATTR_EMPTY);
			eleReleaseTemplate.setAttribute(ATTR_ORDER_RELEASE_KEY, ATTR_EMPTY);
			Element eleOrderTemplate = SCXmlUtil.createChild(eleReleaseTemplate, ELE_ORDER);
			eleOrderTemplate.setAttribute(ATTR_ORDER_HEADER_KEY, ATTR_EMPTY);
			eleOrderTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
			Element eleOrderLinesTemplate = SCXmlUtil.createChild(eleReleaseTemplate, ELE_ORDER_LINES);
			Element eleOrderLineTemplate = SCXmlUtil.createChild(eleOrderLinesTemplate, ELE_ORDER_LINE);
			eleOrderLineTemplate.setAttribute(ATTR_CHAINED_FROM_ORDER_HEADER_KEY, ATTR_EMPTY);

			docGetOrderReleaseList = VSIUtils.invokeAPI(env, docReleaseListTemplate, API_GET_ORDER_RELEASE_LIST,
					docGetOrderReleaseList);
			eleGetOrderReleaseList = docGetOrderReleaseList.getDocumentElement();
			Element eleGetOrderRelease = SCXmlUtil.getChildElement(eleGetOrderReleaseList, ELE_ORDER_RELEASE);
			Element eleOrderReleaseOrder = SCXmlUtil.getChildElement(eleGetOrderRelease, ELE_ORDER);
			strTOOrderNo = eleOrderReleaseOrder.getAttribute(ATTR_ORDER_NO);
			Element eleOrderLines = SCXmlUtil.getChildElement(eleGetOrderRelease, ELE_ORDER_LINES);
			Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, ELE_ORDER_LINE);
			strChainedFromOHKey = eleOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_HEADER_KEY);
			// strTOOrderNo = eleOrderLine.getAttribute(ATTR_ORDER_)
		}

		// call getOrderList to find if Order is having an international
		// address.

		Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleGetOrderList = docGetOrderList.getDocumentElement();
		if (bIsTO && !YFCCommon.isVoid(strChainedFromOHKey)) {
			eleGetOrderList.setAttribute(ATTR_ORDER_HEADER_KEY, strChainedFromOHKey);
		} else {
			eleGetOrderList.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, eleOrder.getAttribute(ATTR_DOCUMENT_TYPE));
			eleGetOrderList.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
		}

		eleGetOrderList.setAttribute(VSIConstants.ATTR_MAX_RECORDS, ONE);

		// Create the template

		Document docGetOrderListTemplate = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_LIST);
		Element eleOrderListTemplate = docGetOrderListTemplate.getDocumentElement();
		Element eleOrderTemplate = SCXmlUtil.createChild(eleOrderListTemplate, ELE_ORDER);
		eleOrderTemplate.setAttribute(ATTR_SOURCING_CLASSIFICATION, "");
		eleOrderTemplate.setAttribute(ATTR_ORDER_NO, "");
		eleOrderTemplate.setAttribute(ATTR_ENTERED_BY, "");
		eleOrderTemplate.setAttribute(ATTR_ENTERPRISE_CODE, "");

		if (!YFCCommon.isVoid(strOrderNo) || !YFCCommon.isVoid(strChainedFromOHKey)) {
			Document docOrderList = VSIUtils.invokeAPI(env, docGetOrderListTemplate, API_GET_ORDER_LIST,
					docGetOrderList);
			Element eleOrderList = docOrderList.getDocumentElement();
			Element eleOrderOut = SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
			if (!YFCCommon.isVoid(eleOrder)) {
				String strOrderSourcingClassification = eleOrderOut.getAttribute(ATTR_SOURCING_CLASSIFICATION);
				String strEnteredBy = eleOrderOut.getAttribute(ATTR_ENTERED_BY);
				strEnterpriseCode = eleOrderOut.getAttribute(ATTR_ENTERPRISE_CODE);
				if (!YFCCommon.isVoid(strOrderSourcingClassification)) {
					if (strOrderSourcingClassification.equalsIgnoreCase(INTERNATIONAL_ORDER)) {
						bIsInternationalOrder = true;
					}
				}
				if (!YFCCommon.isVoid(strEnteredBy)) {
					strOrderEnteredBy = strEnteredBy;
				}
			}
		}
	}

	/*
	 * This function forms input for manageInventoryNodeControl to suppress the
	 * inventory when release is canceled from warehouse.
	 */
	private void invokeManageInvNodeControl(YFSEnvironment env, String strItemID, String strNode)
			throws YFSException, RemoteException, YIFClientCreationException {

		// Form input for manageInventoryNodeControl
		Document docManageInvNodeControl = SCXmlUtil.createDocument(VSIConstants.ELE_INVENTORY_NODE_CONTROL);
		Element eleManageInvNodeControl = docManageInvNodeControl.getDocumentElement();
		eleManageInvNodeControl.setAttribute(ATTR_ITEM_ID, strItemID);
		// OMS-898 : Start
		// eleManageInvNodeControl.setAttribute(ATTR_ORG_CODE, ENT_VSI_CAT);
		// OMS-898 :end
		eleManageInvNodeControl.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
		eleManageInvNodeControl.setAttribute(ATTR_UOM, UOM_EACH);
		eleManageInvNodeControl.setAttribute(ATTR_NODE, strNode);
		eleManageInvNodeControl.setAttribute(ATTR_NODE_CONTROL_TYPE, "ON_HOLD");
		eleManageInvNodeControl.setAttribute(ATTR_INVENTORY_PICTURE_CORRECT, FLAG_N);
		eleManageInvNodeControl.setAttribute(ATTR_INVENTORY_PICTURE_INCORRECT_DATE, "2500-12-31");

		// OMS-898 : Start
		// invoke getCommonCodeList and get the list of enterprise which needs
		// to put on inventory hold
		try {
			Document docGetCommonCodeListINC = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCodeINC = docGetCommonCodeListINC.getDocumentElement();
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_ORG_CODE, ATTR_DEFAULT);
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_INV_NODE_CNTRL");
			Document docCommonCodeListINCOutput = VSIUtils.getCommonCodeList(env, docGetCommonCodeListINC);
			if (docCommonCodeListINCOutput != null) {
				NodeList nlCommonCode = docCommonCodeListINCOutput.getElementsByTagName(ELEMENT_COMMON_CODE);
				int iCount = nlCommonCode.getLength();
				for (int i = 0; i < iCount; i++) {
					Element eleCommonCode = (Element) nlCommonCode.item(i);
					String strOrgCode = eleCommonCode.getAttribute(ATTR_CODE_VALUE);
					eleManageInvNodeControl.setAttribute(ATTR_ORG_CODE, strOrgCode);
					Document invNodeCntrlList = VSIUtils.invokeAPI(env, "getInventoryNodeControlList",
							docManageInvNodeControl);
					if (!invNodeCntrlList.getDocumentElement().hasChildNodes()) {
						// Invoke manageInventoryNodeControl
						// VSIUtils.invokeAPI(env,
						// API_MANAGE_INVENTORY_NODE_CONTROL,
						// docManageInvNodeControl);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("RemoteException in VSIProcessShipConfirmFromWMS.invokeManageInvNodeControl() : ", e);
		}

		// OMS-898 : End

		// VSIUtils.invokeAPI(env, API_MANAGE_INVENTORY_NODE_CONTROL,
		// docManageInvNodeControl);

	}

	private void cancelSalesOrder(YFSEnvironment env, String strOrderHeaderKey,
			HashMap<String, String> hmSTSCancelLineMap) throws Exception {

		String strCancelReason = null;
		ArrayList<Element> arrFTCRulesForCancelWindow = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "CancelReason",
				ATTR_DEFAULT);
		if (arrFTCRulesForCancelWindow.size() > 0) {
			Element eleFTCRule = arrFTCRulesForCancelWindow.get(0);
			strCancelReason = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
		}

		Document inputChangeOrder = SCXmlUtil.createDocument();
		Element eleOrder = inputChangeOrder.createElement(ELE_ORDER);
		eleOrder.setAttribute("Override", "Y");
		inputChangeOrder.appendChild(eleOrder);

		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		if (!YFCCommon.isVoid(strCancelReason)) {
			eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, strCancelReason);
		}
		Element eleOrderLines = inputChangeOrder.createElement(ELE_ORDER_LINES);
		eleOrder.appendChild(eleOrderLines);
		Iterator itr = hmSTSCancelLineMap.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry lineDtls = (Map.Entry) itr.next();
			String strOrderLineKey = (String) lineDtls.getKey();
			String strQuantity = (String) lineDtls.getValue();
			Element eleOrderLine = inputChangeOrder.createElement(ELE_ORDER_LINE);
			eleOrderLines.appendChild(eleOrderLine);
			eleOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleOrderLine.setAttribute("OrderedQty", strQuantity);
			eleOrderLine.setAttribute(ATTR_ACTION, "CANCEL");
		}
		if(log.isDebugEnabled()){
    		log.debug("VSIProcessShipConfirmFromWMS_cancelSTSSalesOrder" + SCXmlUtil.getString(inputChangeOrder));
		}
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, inputChangeOrder);
	}

	private void sendEmail(YFSEnvironment env, String sCustomerPONO) throws ParserConfigurationException {

		Document sendEmailInput = XMLUtil.createDocument("Order");
		Element orderElement = sendEmailInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, sCustomerPONO);

		try {
			if(log.isDebugEnabled()){
	    		log.debug("sendEmailInput" + SCXmlUtil.getString(sendEmailInput));
			}
			VSIUtils.invokeService(env, "VSI_BOSTS_ShipConfirmEmail", sendEmailInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Consolidates all shipments under ShipmentList into one shipment with
	 * multiple containers
	 * 
	 * @param arrShipments
	 *            ArrayList of Shipment elements
	 * @param eleShipmentList
	 *            ShipmentList element
	 * @throws Exception
	 * @throws IllegalArgumentException
	 */
	private void consolidateShipments(ArrayList<Element> arrShipments, Element eleShipmentList)
			throws IllegalArgumentException, Exception {
		log.beginTimer("VSIProcessShipConfirmFromWMS.consolidateShipments() : START");

		// OMS-909: Start
		if(log.isDebugEnabled()){
			log.debug("Input for consolidateShipments : " + XMLUtil.getElementString(eleShipmentList));
		}
		HashMap<String, Element> hMapShipmentLineUnique = new HashMap<String, Element>();
		// OMS-909: End

		Element eleFinalShipment = SCXmlUtil.createChild(eleShipmentList, ELE_SHIPMENT);
		SCXmlUtil.setAttributes(arrShipments.get(0), eleFinalShipment);
		Element eleFinalContainers = SCXmlUtil.createChild(eleFinalShipment, ELE_CONTAINERS);
		Element eleFinalShipmentLines = SCXmlUtil.createChild(eleFinalShipment, ELE_SHIPMENT_LINES);
		for (Element eleShipment : arrShipments) {
			Element eleContainer = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(eleShipment, ELE_CONTAINERS),
					ELE_CONTAINER);
			SCXmlUtil.importElement(eleFinalContainers, eleContainer);
			ArrayList<Element> alShipmentLineList = SCXmlUtil
					.getChildren(SCXmlUtil.getChildElement(eleShipment, ELE_SHIPMENT_LINES), ELE_SHIPMENT_LINE);
			for (Element eleShipmentLine : alShipmentLineList) {

				// OMS-909: Start
				// SCXmlUtil.importElement(eleFinalShipmentLines,
				// eleShipmentLine);
				String strPrimeLineNo = eleShipmentLine.getAttribute(ATTR_PRIME_LINE_NO);
				String strItemID = eleShipmentLine.getAttribute(ATTR_ITEM_ID);
				String strUniqueID = strItemID.concat("_").concat(strPrimeLineNo);
				if (hMapShipmentLineUnique.containsKey(strUniqueID)) {
					Element eleShipmentLineFromHM = hMapShipmentLineUnique.get(strUniqueID);
					String strQtyFromHM = eleShipmentLineFromHM.getAttribute(ATTR_QUANTITY);
					String strQtyFromShipmentLine = eleShipmentLine.getAttribute(ATTR_QUANTITY);
					if (strQtyFromHM != null && strQtyFromShipmentLine != null) {
						int iQty = Integer.parseInt(strQtyFromHM) + Integer.parseInt(strQtyFromShipmentLine);
						eleShipmentLineFromHM.setAttribute(ATTR_QUANTITY, String.valueOf(iQty));
						hMapShipmentLineUnique.put(strUniqueID, eleShipmentLineFromHM);
					}
				} else {
					hMapShipmentLineUnique.put(strUniqueID, eleShipmentLine);
				}
				// OMS-909: End
			}
			eleShipmentList.removeChild(eleShipment);

		}
		// OMS-909: Start
		for (Element eleShipmentLineHashMap : hMapShipmentLineUnique.values()) {
			SCXmlUtil.importElement(eleFinalShipmentLines, eleShipmentLineHashMap);

		}
		// OMS-909: End
		if(log.isDebugEnabled()){
			log.debug("Output from consolidateShipments : " + XMLUtil.getElementString(eleShipmentList));
		}
		log.endTimer("VSIProcessShipConfirmFromWMS.consolidateShipments() : END");
	}

	/*
	 * Method to check if the order is WHOLESALE or not.
	 * 
	 */
	public void isWholesaleOrder(YFSEnvironment env, Element eleOrder)
			throws YFSException, RemoteException, YIFClientCreationException {
		// call getOrderList to find if Order is having an international
		// address.
		if (YFCCommon.isVoid(strOrderType) || strOrderType.equalsIgnoreCase(ATTR_EMPTY)) {
			Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleGetOrderList = docGetOrderList.getDocumentElement();
			eleGetOrderList.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, eleOrder.getAttribute(ATTR_DOCUMENT_TYPE));
			eleGetOrderList.setAttribute(VSIConstants.ATTR_ORDER_NO, eleOrder.getAttribute(ATTR_ORDER_NO));
			eleGetOrderList.setAttribute(VSIConstants.ATTR_MAX_RECORDS, ONE);

			// Create the template

			Document docGetOrderListTemplate = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_LIST);
			Element eleOrderListTemplate = docGetOrderListTemplate.getDocumentElement();
			Element eleOrderTemplate = SCXmlUtil.createChild(eleOrderListTemplate, ELE_ORDER);
			eleOrderTemplate.setAttribute(ATTR_DOCUMENT_TYPE, "");
			eleOrderTemplate.setAttribute(ATTR_ORDER_NO, "");
			eleOrderTemplate.setAttribute(ATTR_ENTERPRISE_CODE, "");
			eleOrderTemplate.setAttribute(ATTR_ORDER_TYPE, "");

			if (!YFCCommon.isVoid(eleOrder.getAttribute(ATTR_ORDER_NO))) {
				Document docOrderList = VSIUtils.invokeAPI(env, docGetOrderListTemplate, API_GET_ORDER_LIST,
						docGetOrderList);
				Element eleOrderList = docOrderList.getDocumentElement();
				Element eleOrderOut = SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
				if (!YFCCommon.isVoid(eleOrderOut)) {
					strEnterpriseCode = eleOrderOut.getAttribute(ATTR_ENTERPRISE_CODE);
					strOrderType = eleOrderOut.getAttribute(ATTR_ORDER_TYPE);
				}
			}
		}

	}
}