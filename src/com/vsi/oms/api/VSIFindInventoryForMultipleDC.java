package com.vsi.oms.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Author: Perficient 
 * Date : 6th Aug 2014
 * Purpose: This extended API is invoked from the service - VSIFindInventoryForAnyDC
 * Description : Input to this class will be the output of findInventory API call. Here getAvailableInventory Api is
 * invoked to get the available qty picture for the DCs. This value needs to be set in the Output XML which is expected
 * to be in the below format:
 * <Item IsReqdQtyAvailable="Y" ItemID="1194869" OrganizationCode="VSI.com" 	RequiredQty="120" UnitOfMeasure="EACH">
  		<Inventory AvailableQuantity="290.00" Node="9805" /> 
  		<Inventory AvailableQuantity="100.00" Node="9802" /> 
  		<Inventory AvailableQuantity="100.00" Node="391" /> 
  </Item>

 * 
 * 
 * */


public class VSIFindInventoryForMultipleDC {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIFindInventoryForMultipleDC.class);
	String totalAvailableInventory = "0.00";

	public Document vsiFindInventoryForMultipleDC(YFSEnvironment env,
			Document inXML) throws Exception {
		
		if(log.isDebugEnabled()){
			log.info("================Inside VSIFindInventoryForMultipleDC================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		String attrIsReqdQtyAvailable = "N";
		String hasAnyUnavailableQty = "N";

		String attrItemId = null;
		String attrUOM = null;
		String attrPC = null;
		String attrReqQty = null;
		NodeList nlInventory = null;
		Element eleSuggestedOption = XMLUtil.getElementByXPath(inXML,
				"Promise/SuggestedOption/Option");
		if (eleSuggestedOption != null) {
			hasAnyUnavailableQty = eleSuggestedOption
					.getAttribute("HasAnyUnavailableQty");
			if (hasAnyUnavailableQty != null
					&& hasAnyUnavailableQty
							.equalsIgnoreCase("N")) {
				attrIsReqdQtyAvailable = "Y";
			}
		}

		NodeList nlOptions = inXML.getElementsByTagName("Option");
		if (nlOptions != null && nlOptions.getLength() > 0) {
			for (int i = 0; i < nlOptions.getLength(); i++) {
				Element eleOption = (Element) nlOptions.item(i);
				NodeList nlPromiseLine = eleOption
						.getElementsByTagName("PromiseLine");
				if (nlPromiseLine != null) {
					Element elePromiseLine = (Element) nlPromiseLine.item(0);
					attrItemId = elePromiseLine
							.getAttribute(VSIConstants.ATTR_ITEM_ID);
					attrUOM = elePromiseLine
							.getAttribute(VSIConstants.ATTR_UOM);
					attrPC = elePromiseLine
							.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
					attrReqQty = elePromiseLine
							.getAttribute(VSIConstants.ATTR_REQUIRED_QTY);
				}

			}

			Document getAvlInvInputXML = XMLUtil.createDocument("Promise");
			Element elePromise = getAvlInvInputXML.getDocumentElement();
			elePromise.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
			Element elePromiseLines = getAvlInvInputXML
					.createElement("PromiseLines");
			elePromise.appendChild(elePromiseLines);
			Element elePromiseLine = getAvlInvInputXML
					.createElement("PromiseLine");
			elePromiseLine.setAttribute(VSIConstants.ATTR_ITEM_ID, attrItemId);
			elePromiseLine.setAttribute("LineId", "1");
			elePromiseLine
					.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, attrPC);
			elePromiseLine.setAttribute(VSIConstants.ATTR_UOM, attrUOM);
			elePromiseLines.appendChild(elePromiseLine);

			nlInventory = getAvailableQtyForShipNodes(env, getAvlInvInputXML);

		}else{
			NodeList nlUnavlLine = inXML.getElementsByTagName("UnavailableLine");
			if(nlUnavlLine != null && nlUnavlLine.getLength() > 0){
				Element unavlLine = (Element) nlUnavlLine.item(0);
				if(unavlLine != null){
					attrItemId = unavlLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
					attrReqQty = unavlLine.getAttribute(VSIConstants.ATTR_REQUIRED_QTY);
					attrUOM = unavlLine.getAttribute(VSIConstants.ATTR_UOM);
				}
				
			}
			
		}
		Document vsiFindInventoryForMultipleDCOutput = XMLUtil
				.createDocument(VSIConstants.ELE_ITEM);
		Element eleItem = vsiFindInventoryForMultipleDCOutput
				.getDocumentElement();
		eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID, attrItemId);
		eleItem.setAttribute(VSIConstants.ATTR_REQUIRED_QTY, attrReqQty);
		eleItem.setAttribute(VSIConstants.ATTR_UOM, attrUOM);
		eleItem.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
		eleItem.setAttribute("IsReqdQtyAvailable", attrIsReqdQtyAvailable);
		eleItem.setAttribute("TotalAvailableQty", totalAvailableInventory);

		if (nlInventory != null && nlInventory.getLength() > 0) {
			for (int i = 0; i < nlInventory.getLength(); i++) {
				Node inventoryNode = nlInventory.item(i);
				Node copyNode = vsiFindInventoryForMultipleDCOutput.importNode(
						inventoryNode, true);
				eleItem.appendChild(copyNode);
			}
		}
		return vsiFindInventoryForMultipleDCOutput;

	}

	private NodeList getAvailableQtyForShipNodes(YFSEnvironment env,
			Document getAvlInvInputXML) {
		NodeList nlInventory = null;
		try {

			if(log.isDebugEnabled()){
				log.debug("**Inside method -getAvailableQtyForShipNodes ");
				log.debug("**Input XML to getAvailableInventory "
						+ XMLUtil.getElementXMLString(getAvlInvInputXML
								.getDocumentElement()));
			}

			Document getAvlInvOutputXML = VSIUtils.invokeAPI(env,
					"global/template/api/getAvailableInvVSIOutput.xml",
					"getAvailableInventory", getAvlInvInputXML);
			if(log.isDebugEnabled()){
				log.debug("**Output XML from getAvailableInventory "
						+ XMLUtil.getElementXMLString(getAvlInvOutputXML
								.getDocumentElement()));
			}

			NodeList nlAvailableInventory = getAvlInvOutputXML
					.getElementsByTagName("AvailableInventory");
			if (nlAvailableInventory.getLength() > 0) {

				for (int i = 0; i < nlAvailableInventory.getLength(); i++) {
					Element eleAvailableInventory = (Element) nlAvailableInventory
							.item(i);
					if (eleAvailableInventory.getAttribute(
							"AvailableFromUnplannedInventory")
							.equalsIgnoreCase("N")) {
						totalAvailableInventory = eleAvailableInventory.getAttribute("AvailableQuantity");
						nlInventory = eleAvailableInventory
								.getElementsByTagName("Inventory");

					}
				}
			}

		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("Exit method - getAvailableQtyForShipNodes ");
		}
		return nlInventory;
	}

}
