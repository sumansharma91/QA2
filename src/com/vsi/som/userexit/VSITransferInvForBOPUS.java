package com.vsi.som.userexit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.shipment.api.VSIProcessShipConfirmFromWMS;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.vsi.som.shipment.VSITransferInventoryForShipment;
import com.yantra.pca.ycd.japi.ue.YCDBeforeConfirmShipmentOfRecordCustomerPickUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class VSITransferInvForBOPUS implements YCDBeforeConfirmShipmentOfRecordCustomerPickUE,VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSITransferInvForBOPUS.class);
	@Override
	public Document beforeConfirmShipmentOfRecordCustomerPick(YFSEnvironment env, Document docShipment) throws YFSUserExitException 
	{
		log.info("Input for VSITransferInvForBOPUS.beforeConfirmShipmentOfRecordCustomerPick => "+XMLUtil.getXMLString(docShipment));
		if(log.isDebugEnabled())
			log.debug("Input for VSITransferInvForBOPUS.beforeConfirmShipmentOfRecordCustomerPick => "+XMLUtil.getXMLString(docShipment));
		try
		{
			Element eleShipment = docShipment.getDocumentElement();
			String shipmentKey = eleShipment.getAttribute(ATTR_SHIPMENT_KEY);
			log.info("shipmentKey => "+shipmentKey);
			Document getShipmentListInXml=XMLUtil.createDocument(ELE_SHIPMENT);
			Element shipmentEle = getShipmentListInXml.getDocumentElement();
			shipmentEle.setAttribute(ATTR_SHIPMENT_KEY,shipmentKey);
			Document getShipmentListOutXML = VSIUtils.invokeAPI(env, TEMPLATE_GET_SHIPMENT_LIST, API_GET_SHIPMENT_LIST, getShipmentListInXml);
			log.info("getShipmentListOutXML => "+XMLUtil.getXMLString(getShipmentListOutXML));
			Element elemShipment = (Element) getShipmentListOutXML.getElementsByTagName(ELE_SHIPMENT).item(0);
			String shipNode = elemShipment.getAttribute(ATTR_SHIP_NODE);
			String strEnterprise = elemShipment.getAttribute(ATTR_ENTERPRISE_CODE);
			log.info("shipNode => "+shipNode+"strEnterprise => "+strEnterprise);
			NodeList shipmentLineNode = getShipmentListOutXML.getElementsByTagName(ELE_SHIPMENT_LINE);
			for (int j = 0; j < shipmentLineNode.getLength(); j++) 
			{
				Element shipmentLineElem = (Element) shipmentLineNode.item(j);
				String strItemId = shipmentLineElem.getAttribute(ATTR_ITEM_ID);
				String shipmentLineKey = shipmentLineElem.getAttribute(ATTR_SHIPMENT_LINE_KEY);		
				Double dblRequiredQty=0.0;
				NodeList shipmentLineNodeList = docShipment.getElementsByTagName(ELE_SHIPMENT_LINE);
				for (int i = 0; i < shipmentLineNodeList.getLength(); i++) 
				{
					Element shipmentLineElement = (Element) shipmentLineNodeList.item(i);
					if(shipmentLineKey.equalsIgnoreCase(shipmentLineElement.getAttribute(ATTR_SHIPMENT_LINE_KEY)))
						dblRequiredQty = SCXmlUtil.getDoubleAttribute(shipmentLineElement, ATTR_PICKED_QTY);
				}				
				log.info("strItemId => "+strItemId+"dblRequiredQty" + dblRequiredQty);
				String strFromOrganizationCode = VSI_INV;
				Document docGetInventorySupply = SCXmlUtil.createDocument(ELE_INVENTORY_SUPPLY);
				Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
				eleGetInventorySupply.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
				eleGetInventorySupply.setAttribute(ATTR_UOM, UOM_EACH);
				eleGetInventorySupply.setAttribute(ATTR_SUPPLY_TYPE, ONHAND_SUPPLY_TYPE);
				eleGetInventorySupply.setAttribute(ATTR_SHIP_NODE, shipNode);
				eleGetInventorySupply.setAttribute(ATTR_ITEM_ID, strItemId);
				Document docTransferInventory = SCXmlUtil.createDocument(ELE_ITEMS);
				Element eleTransferInventory = docTransferInventory.getDocumentElement();
				VSIProcessShipConfirmFromWMS shipObj = new VSIProcessShipConfirmFromWMS();
				dblRequiredQty = shipObj.invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty, eleTransferInventory,
						strFromOrganizationCode, strEnterprise);
				if (dblRequiredQty > 0)
				{
					if (ENT_ADP.equalsIgnoreCase(strEnterprise))
						strFromOrganizationCode = ADP_INV;
					else if (ENT_MCL.equalsIgnoreCase(strEnterprise))
						strFromOrganizationCode = MCL_INV;
					else
						strFromOrganizationCode = DTC_INV;
					dblRequiredQty = shipObj.invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				}
				if (dblRequiredQty > 0) 
				{
					if (ENT_ADP.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = DTC_INV;
						dblRequiredQty = shipObj.invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					} else if (ENT_NAVY_EXCHANGE.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = MCL_INV;
						dblRequiredQty = shipObj.invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					}
				}
				VSITransferInventoryForShipment obj = new VSITransferInventoryForShipment();
				if (dblRequiredQty > 0) 
				{
					if (ENT_ADP.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = MCL_INV;
						dblRequiredQty = shipObj.invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					}
					else
						eleTransferInventory = obj.addItemToTransferInv(eleTransferInventory,shipNode,strEnterprise,strItemId,dblRequiredQty);
				}
				if (dblRequiredQty > 0)
					eleTransferInventory = obj.addItemToTransferInv(eleTransferInventory,shipNode,strEnterprise,strItemId,dblRequiredQty);
			log.info("docTransferInventory Before API call in BOPUS flow => "+XMLUtil.getXMLString(docTransferInventory));
			VSIUtils.invokeAPI(env, API_TRANSFER_INV_OWNERSHIP, docTransferInventory);				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		log.info("docShipment final => "+XMLUtil.getXMLString(docShipment));
		return docShipment;
	}
}
