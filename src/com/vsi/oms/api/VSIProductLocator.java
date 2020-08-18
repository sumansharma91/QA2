package com.vsi.oms.api;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author ashish.keshri
 * 
 */

public class VSIProductLocator {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIProductLocator.class);
	YIFApi api;

	public Document filteredFind(YFSEnvironment env, Document inXML)
			throws Exception {

		Document outInvDoc = null;
		try {
			if(log.isDebugEnabled()){
				log.info("================Inside VSIProductLocator================================");
				log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
			}
			Element rootElement = inXML.getDocumentElement();
			String filterCriteria = rootElement.getAttribute("FilterCriteria");

			Document getAvailableInventoryInput = inXML;
			Element promiseInvElement = (Element) getAvailableInventoryInput
					.getElementsByTagName(VSIConstants.ELE_PROMISE).item(0);
			Element shipaddressInvElement = (Element) getAvailableInventoryInput
					.getElementsByTagName(VSIConstants.ELE_SHIP_ADDRESS)
					.item(0);
			Element promiseElement = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_PROMISE).item(0);
			Element shipaddressElement = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_SHIP_ADDRESS).item(0);
			Document vsiGetSurroundingListInput = XMLUtil
					.createDocument("GetSurroundingNodeList");
			Element eleNodeList = vsiGetSurroundingListInput
					.getDocumentElement();
			eleNodeList.setAttribute(VSIConstants.ATTR_ORG_CODE,
					promiseElement.getAttribute(VSIConstants.ATTR_ORG_CODE));
					//harshit : fulfillment type added for cut over strategy
					eleNodeList.setAttribute("FulfillmentType","CUTOVERSTORES");
					
					
			eleNodeList
					.setAttribute(
							VSIConstants.ATTR_DISTANCE_UOM,
							promiseElement
									.getAttribute(VSIConstants.ATTR_DISTANCE_UOM));
			eleNodeList.setAttribute(VSIConstants.ATTR_DISTANCE_CONSIDER,
					promiseElement
							.getAttribute(VSIConstants.ATTR_DISTANCE_CONSIDER));
			eleNodeList.setAttribute(VSIConstants.ATTR_NODE_TYPE, "Store");
			Element eleShipToAddress = vsiGetSurroundingListInput
					.createElement(VSIConstants.ELE_SHIP_ADDRESS);
			eleShipToAddress.setAttribute("ZipCode",
					shipaddressElement.getAttribute(VSIConstants.ATTR_ZIPCODE));
			eleShipToAddress.setAttribute("Country",
					shipaddressElement.getAttribute(VSIConstants.ATTR_COUNTRY));
			eleNodeList.appendChild(eleShipToAddress);
			env.setApiTemplate("getSurroundingNodeList",
					"/global/template/api/getSurroundingListOutput.xml");
			YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
			Document outDoc = callApi.invoke(env, "getSurroundingNodeList",
					vsiGetSurroundingListInput);
			env.clearApiTemplate("getSurroundingNodeList");
			NodeList nodeList = outDoc
					.getElementsByTagName(VSIConstants.ELE_NODE);
			int iNodeLength = nodeList.getLength();
			
			promiseInvElement.removeChild(shipaddressInvElement);
			Element shipNodesList = getAvailableInventoryInput
					.createElement("ShipNodes");

			for (int i = 0; i < iNodeLength; i++) {
				
				Element nodeElement = (Element) nodeList.item(i);
				Element shipNode = getAvailableInventoryInput
						.createElement("ShipNode");
				shipNode.setAttribute(VSIConstants.ELE_NODE,
						nodeElement.getAttribute("ShipNode"));
				shipNodesList.appendChild(shipNode);
			}
			promiseInvElement.appendChild(shipNodesList);

			env.setApiTemplate("getAvailableInventory",
					"/global/template/api/getAvailableInventory.xml");
			YIFApi callInvApi = YIFClientFactory.getInstance().getLocalApi();
			outInvDoc = callInvApi.invoke(env, "getAvailableInventory",
					getAvailableInventoryInput);

			env.clearApiTemplate("getAvailableInventory");
			Element outrootElement = outInvDoc.getDocumentElement();

			outrootElement.setAttribute("FilterCriteria", filterCriteria);
			NodeList itemList = inXML.getElementsByTagName("PromiseLine");
			int iItemLength = itemList.getLength();
			for (int l = 0; l < iItemLength; l++) {
				Element itemElement = (Element) itemList.item(l);
				for (int m = 0; m < iItemLength; m++) {
					Element itemOutputElement = (Element) outInvDoc
							.getElementsByTagName("PromiseLine").item(m);
					if (itemElement.getAttribute("ItemID").equalsIgnoreCase(
							itemOutputElement.getAttribute("ItemID"))) {
						itemOutputElement.setAttribute("RequiredQty",
								itemElement.getAttribute("RequiredQty"));
					}

				}
				Element itemOutputElement = (Element) outInvDoc
						.getElementsByTagName("PromiseLine").item(l);
				// itemOutputElement.setAttribute("RequiredQty",
				// itemElement.getAttribute("RequiredQty"));
			}
			
			/* ********* change if condition to check the length of the nodelist. No change inside If. *********** */
			
			if(nodeList.getLength()>0)
			{
					
			NodeList invList = outInvDoc.getElementsByTagName("Inventory");
			int iInvLength = invList.getLength();
			for (int j = 0; j < iInvLength; j++) {
				Element invElement = (Element) invList.item(j);
				String sNode = invElement.getAttribute(VSIConstants.ELE_NODE);
				for (int k = 0; k < iNodeLength; k++) {
					Element nodeElement = (Element) nodeList.item(k);
					String sShipNode = nodeElement.getAttribute("ShipNode");
					if (sNode.equalsIgnoreCase(sShipNode)) {
						invElement.setAttribute("Distance", nodeElement
								.getAttribute("DistanceFromShipToAddress"));
						invElement.setAttribute("Description",
								nodeElement.getAttribute("Description"));
						Element shipNodeAddressElement = (Element) outDoc
								.getElementsByTagName(
										VSIConstants.ELE_SHIP_NODE_PERSON)
								.item(k);
						invElement
								.setAttribute(
										VSIConstants.ATTR_ADDRESS2,
										shipNodeAddressElement
												.getAttribute(VSIConstants.ATTR_ADDRESS2));
						invElement
								.setAttribute(
										VSIConstants.ATTR_ADDRESS1,
										shipNodeAddressElement
												.getAttribute(VSIConstants.ATTR_ADDRESS1));
						invElement.setAttribute(VSIConstants.ATTR_CITY,
								shipNodeAddressElement
										.getAttribute(VSIConstants.ATTR_CITY));
						invElement
								.setAttribute(
										VSIConstants.ATTR_COUNTRY,
										shipNodeAddressElement
												.getAttribute(VSIConstants.ATTR_COUNTRY));
						invElement
								.setAttribute(
										VSIConstants.ATTR_ZIPCODE,
										shipNodeAddressElement
												.getAttribute(VSIConstants.ATTR_ZIPCODE));
						invElement.setAttribute(VSIConstants.ATTR_STATE,
								shipNodeAddressElement
										.getAttribute(VSIConstants.ATTR_STATE));
						invElement
								.setAttribute("Contact", shipNodeAddressElement
										.getAttribute("DayPhone"));
					}
				}
			}
			
			}
			else
			{
				
				NodeList nlShipNodeAvailableInventory = outInvDoc.getElementsByTagName("PromiseLine");
				int iInvLength = nlShipNodeAvailableInventory.getLength();
				for (int j = 0; j < iInvLength; j++) {
					
					Element eleAvailableInventory = (Element) outInvDoc.getElementsByTagName("AvailableInventory").item(j);
					eleAvailableInventory.setAttribute("AvailableOnhandQuantity", "0.00");
					eleAvailableInventory.setAttribute("AvailableQuantity", "0.00");
					
					Element eleShipNodeAvailableInventory = (Element) outInvDoc.getElementsByTagName("ShipNodeAvailableInventory").item(j);
					NodeList nlChildNodes = eleShipNodeAvailableInventory.getChildNodes();
									
					int iChildNodes = nlChildNodes.getLength();
					for (int x = iChildNodes; x > 0; x--) {  
						Node nChild = (Node) nlChildNodes.item(0);  
		                eleShipNodeAvailableInventory.removeChild(nChild);
		          
		        } 
					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outInvDoc;

	}

}
