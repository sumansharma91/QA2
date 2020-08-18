package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICreateInventoryActivityForTO{

	private YFCLogCategory log = YFCLogCategory.instance(VSICreateInventoryActivityForTO.class);


	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 */
	public Document createInventoryActivity(YFSEnvironment env,Document inDoc){

		try {
			if(log.isDebugEnabled()){
				log.debug("Input for VSICreateInventoryActivityForTO:createInventoryActivity is "+SCXmlUtil.getString(inDoc));
			}

			//Element eleOrder=inDoc.getDocumentElement();

			//create input for createInventoryActivityList
			Document docInventoryActivityListIP=SCXmlUtil.createDocument("InventoryActivityList");
			Element eleInventoryActivityList=docInventoryActivityListIP.getDocumentElement();

			//loop through order lines and add the InventoryActivity

			NodeList nlOrderLine=inDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iOrderLineCount=nlOrderLine.getLength();
			for (int i = 0; i < iOrderLineCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
				if(!YFCCommon.isVoid(eleItem)){
					String strItemID=eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
					String strProductClass=eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
					String strUnitOfMeasure=eleItem.getAttribute(VSIConstants.ATTR_UOM);
						Element eleInventoryActivity=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
						eleInventoryActivity.setAttribute("CreateForInvItemsAtNode", "N");
						eleInventoryActivity.setAttribute("ItemID", strItemID);
						eleInventoryActivity.setAttribute("OrganizationCode", "ADP");
						eleInventoryActivity.setAttribute("ProductClass", strProductClass);
						eleInventoryActivity.setAttribute("UnitOfMeasure", strUnitOfMeasure);
					
				}
			}

			//invoking createInventoryActivityList
			if(log.isDebugEnabled()){
				log.debug("Input for createInventoryActivityList:  "+SCXmlUtil.getString(docInventoryActivityListIP));
			}
			if(eleInventoryActivityList.hasChildNodes()) {
				VSIUtils.invokeAPI(env, "createInventoryActivityList", docInventoryActivityListIP);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return inDoc;

	}

}
