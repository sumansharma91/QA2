package com.vsi.oms.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICreateInventoryActivity{

	private YFCLogCategory log = YFCLogCategory.instance(VSICreateInventoryActivity.class);


	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 */
	public Document createInventoryActivity(YFSEnvironment env,Document inDoc){

		try {
			if(log.isDebugEnabled()){
				log.debug("Input for VSICreateInventoryActivity:createInventoryActivity is "+SCXmlUtil.getString(inDoc));
			}

			Element eleOrder=inDoc.getDocumentElement();
			String strEnterpriseCode=eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			Set<String> setInventroyOrg=new HashSet<String>();

			if(!YFCCommon.isStringVoid(strEnterpriseCode) && VSIConstants.VSICOM_ENTERPRISE_CODE.equals(strEnterpriseCode)){
				setInventroyOrg.add(VSIConstants.ENT_ADP);
				setInventroyOrg.add(VSIConstants.ENT_MCL);
			}
			else if(!YFCCommon.isStringVoid(strEnterpriseCode) && VSIConstants.ENT_ADP.equals(strEnterpriseCode)){
				setInventroyOrg.add(VSIConstants.VSICOM_ENTERPRISE_CODE);
				setInventroyOrg.add(VSIConstants.ENT_MCL);
			}
			else if(!YFCCommon.isStringVoid(strEnterpriseCode) && VSIConstants.ENT_MCL.equals(strEnterpriseCode)){
				setInventroyOrg.add(VSIConstants.VSICOM_ENTERPRISE_CODE);
				setInventroyOrg.add(VSIConstants.ENT_ADP);
			}
			else{
				setInventroyOrg.add(VSIConstants.VSICOM_ENTERPRISE_CODE);
				setInventroyOrg.add(VSIConstants.ENT_ADP);
				setInventroyOrg.add(VSIConstants.ENT_MCL);
			}

			if(log.isDebugEnabled()){
				log.debug("Inventory org codes for which activity needs to be created "+setInventroyOrg.toString());
			}
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
					for (Iterator<String> iterator = setInventroyOrg.iterator(); iterator.hasNext();) {
						String strInventoryOrg = (String) iterator.next();
						Element eleInventoryActivity=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
						eleInventoryActivity.setAttribute("CreateForInvItemsAtNode", "N");
						eleInventoryActivity.setAttribute("ItemID", strItemID);
						eleInventoryActivity.setAttribute("OrganizationCode", strInventoryOrg);
						eleInventoryActivity.setAttribute("ProductClass", strProductClass);
						eleInventoryActivity.setAttribute("UnitOfMeasure", strUnitOfMeasure);
					}
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
