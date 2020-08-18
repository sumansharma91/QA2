package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIRemoveInventoryNodeControl extends VSIBaseCustomAPI implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIRemoveInventoryNodeControl.class);

	/*

	 */

	public Document removeInventoryNodeControl(YFSEnvironment env, Document inXML) throws Exception{

		String strOrganizationCode=null;
		String strNode=null;

		try {

			//fetch the shipNode from the common code and invoke the manageInventoryNodeControl
			Document docGetCommonCodeListINC = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCodeINC = docGetCommonCodeListINC.getDocumentElement();
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_ORG_CODE, ATTR_DEFAULT);
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_INV_SHIP_NODE");
			Document docCommonCodeListINCOutput=VSIUtils.getCommonCodeList(env, docGetCommonCodeListINC);
			if(docCommonCodeListINCOutput!=null){
				NodeList nlCommonCode = docCommonCodeListINCOutput.getElementsByTagName(ELEMENT_COMMON_CODE);
				int iCount=nlCommonCode.getLength();
				for (int i = 0; i < iCount; i++) {
					Element eleCommonCode=(Element)nlCommonCode.item(i);
					strNode=eleCommonCode.getAttribute(ATTR_CODE_VALUE);
					invokeManageInventoryNodeControl(env,"VSI.com",strNode);
				}
			} 


		} catch (YFSException yfse) {
			log.error("YFSException in VSIRemoveInventoryNodeControl.removeInventoryNodeControl() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIRemoveInventoryNodeControl.removeInventoryNodeControl() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIRemoveInventoryNodeControl.removeInventoryNodeControl() : ");
		}

		return inXML;
	}


	/**
	 * Sample Input for manageInventoryNodeControl
	 * 
	 * 		<?xml version="1.0" encoding="UTF-8"?>
			<InventoryNodeControl InventoryPictureCorrectForAllItemsAtNode="Y" 
 			Node="9004" NodeControlType="ON_HOLD" OrganizationCode="VSI.com" />
	 * 
	 * @param env
	 * @param strOrganizationCode
	 * @param strNode
	 * @return
	 */

	private Document invokeManageInventoryNodeControl(YFSEnvironment env,String strOrganizationCode,String strNode){

		Document docApiOutput = null;
		try {
			//create input for manageInventoryNodeCntrol
			Document docManageInvNodeControl = SCXmlUtil.createDocument(VSIConstants.ELE_INVENTORY_NODE_CONTROL);
			Element eleManageInvNodeControl = docManageInvNodeControl.getDocumentElement();
			eleManageInvNodeControl.setAttribute("InventoryPictureCorrectForAllItemsAtNode","Y" );
			eleManageInvNodeControl.setAttribute(ATTR_NODE, strNode);
			eleManageInvNodeControl.setAttribute(ATTR_NODE_CONTROL_TYPE, "ON_HOLD");
			eleManageInvNodeControl.setAttribute(ATTR_ORG_CODE,strOrganizationCode );
			VSIUtils.invokeAPI(env, API_MANAGE_INVENTORY_NODE_CONTROL, docManageInvNodeControl);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return docApiOutput;

	}

}
