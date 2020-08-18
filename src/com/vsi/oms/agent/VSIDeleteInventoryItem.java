package com.vsi.oms.agent;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIDeleteInventoryItem extends YCPBaseAgent {

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIDeleteInventoryItem.class.getName());

	/*
	 * Deleting rows from vsi_inventory_item table
	 */
	public final List<Document> getJobs(final YFSEnvironment env,
			final Document inDoc, final Document lastMsg ) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("getJobs method :- Begins");
		}
		/** **Prepare the input XML for getVSIInventoryItemList API.************* */
		List<Document> OrderList = new ArrayList<Document>();
		if(null == lastMsg){
		Document getInventoryItemListInDoc = XMLUtil
				.createDocument("InventoryItem");
		Element eleInDoc = getInventoryItemListInDoc.getDocumentElement();

		if (log.isVerboseEnabled()) {
			log.verbose("getInventoryItemListInDoc input : \n"
					+ XMLUtil.getXMLString(getInventoryItemListInDoc));
		}

		Document getDoc = VSIUtils.invokeService(env, "VSIGetInventoryItem",
				getInventoryItemListInDoc);

		if (log.isVerboseEnabled()) {
			log.verbose("getInventoryItemListOutDoc  : \n"
					+ XMLUtil.getXMLString(getDoc));
		}
		
		OrderList.add(getDoc);
		if(log.isDebugEnabled()){
			log.debug("getJob method :- Ends");
		}
		}
		return OrderList;
	}


	public void executeJob(YFSEnvironment env, Document docInput)
			throws Exception

	{

		Element EleInput = docInput.getDocumentElement();
		NodeList lNode = EleInput.getElementsByTagName("VSIInventoryItem");

		for (int i = 0; i < lNode.getLength(); i++) {
			Element EleOrder = (Element) lNode.item(i);
			String sInventoryKey = EleOrder.getAttribute("InventoryKey");
			String sInventoryItemKey = EleOrder
					.getAttribute("InventoryItemKey");
			/** **********preparing document For delete api************** */
			
			Document deleteAPIDoc = XMLUtil.createDocument("InventoryItem");
			Element elechangeOrderDoc = deleteAPIDoc.getDocumentElement();
			elechangeOrderDoc.setAttribute("InventoryKey", sInventoryKey);
			elechangeOrderDoc.setAttribute("InventoryItemKey",
					sInventoryItemKey);

			if (log.isVerboseEnabled()) {
				log.verbose("Delete api input : \n"
						+ XMLUtil.getXMLString(deleteAPIDoc));
			}

			/******Invoke service VSIDeleteInventoryItem**********/
			Document DeleteOutDoc = VSIUtils.invokeService(env,
					"VSIDeleteInventoryItem", deleteAPIDoc);

			if (log.isVerboseEnabled()) {
				log.verbose("VSIDeleteInventoryItem  : \n"
						+ XMLUtil.getXMLString(DeleteOutDoc));
			}
			if(log.isDebugEnabled()){
				log.debug("executeJob method :- Ends");
			}
		}

	}

}
