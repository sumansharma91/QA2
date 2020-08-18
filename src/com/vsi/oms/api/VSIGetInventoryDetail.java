
package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.vsi.oms.utils.VSIConstants;

public class VSIGetInventoryDetail {

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIGetInventoryDetail.class.getName());

	/** **********Code for fetching InventoryItemKey for item and ***** */
	/**
	 * **Invoking service VSICreateInventoryItem to update VSI_Inventory_item
	 * table**
	 */

	/**
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document getInventoryDetail(YFSEnvironment env, Document inXML)
			throws Exception {

		if (log.isVerboseEnabled()) {
			log.verbose(" input : \n" + XMLUtil.getXMLString(inXML));
		}

		Element EleInput = inXML.getDocumentElement();

		String sUnitOfMeasure = EleInput.getAttribute(VSIConstants.ATTR_UOM);
		String sItemID = EleInput.getAttribute(VSIConstants.ATTR_ITEM_ID);
		String sShipNode = EleInput.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		String sOrganizationCode = EleInput
				.getAttribute("InventoryOrganizationCode");
		Element elesupply = (Element) inXML.getElementsByTagName("Supply")
				.item(0);

		String sSupplyType = elesupply.getAttribute("SupplyType");
		String sActualQuantity = elesupply.getAttribute("ActualQuantity");
		String sChangedQuantity = elesupply.getAttribute("ChangedQuantity");
		String sExpectedQuantity = elesupply.getAttribute("ExpectedQuantity");
		String sProductClass = EleInput
				.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
		/** **creating document for getInventoryItemList API ************** */

		Document getInventoryItemListInDoc = XMLUtil
				.createDocument("InventoryItem");
		Element eleInDoc = getInventoryItemListInDoc.getDocumentElement();
		eleInDoc.setAttribute(VSIConstants.ATTR_UOM, sUnitOfMeasure);
		eleInDoc.setAttribute(VSIConstants.ATTR_SHIP_NODE, sShipNode);
        eleInDoc.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, sProductClass);
		eleInDoc.setAttribute("OrganizationCode", sOrganizationCode);
		eleInDoc.setAttribute(VSIConstants.ATTR_ITEM_ID, sItemID);
		

		if (log.isVerboseEnabled()) {
			log.verbose("getInventoryItemList input : \n"
					+ XMLUtil.getXMLString(getInventoryItemListInDoc));
		}

		YIFApi api = YIFClientFactory.getInstance().getApi();

		Document getInventoryItemListOutDoc = api.invoke(env, "getInventoryItemList",
				getInventoryItemListInDoc);

		/***********************************************************************
		 * ************if getInventoryItemListOutDoc is null then it will not invoke
		 * the VSICreateInventoryItem service
		 **********************************************************************/

		if (!YFCObject.isVoid(getInventoryItemListOutDoc)) {

			if (log.isVerboseEnabled()) {
				log.verbose("getInventoryItemList output : \n"
						+ XMLUtil.getXMLString(getInventoryItemListOutDoc));
			}

			Element elegetInventory = (Element) getInventoryItemListOutDoc
					.getElementsByTagName("InventoryItem").item(0);
			String sInventoryItemKey = elegetInventory
					.getAttribute("InventoryItemKey");
			
			
			/*******************************************************************
			 * *******if InventoryItemKey is null then it will not invoke the
			 * VSICreateInventoryItem service
			 ******************************************************************/
			if (!YFCObject.isVoid(sInventoryItemKey)) {
				Document VSIInventoryItemInDoc = XMLUtil
						.createDocument("VSIInventoryItem");

				Element eleVSIInventoryItemInDoc = VSIInventoryItemInDoc
						.getDocumentElement();
				
				eleVSIInventoryItemInDoc.setAttribute("InventoryItemKey",
						sInventoryItemKey);
				
				eleVSIInventoryItemInDoc.setAttribute("ActualQuantity",
						sActualQuantity);
				
				eleVSIInventoryItemInDoc.setAttribute("ChangedQuantity",
						sChangedQuantity);
				eleVSIInventoryItemInDoc.setAttribute("ExpectedQuantity",
						sExpectedQuantity);
				eleVSIInventoryItemInDoc.setAttribute("ShipNode", sShipNode);

				if (log.isVerboseEnabled()) {
					log.verbose("VSIInventoryItem input XML : \n"
							+ XMLUtil.getXMLString(VSIInventoryItemInDoc));
				}

				
				 	VSIUtils.invokeService(env, "VSICreateInventoryItem",
							VSIInventoryItemInDoc);

				
				

			}

		}
		return null;
	}

}
