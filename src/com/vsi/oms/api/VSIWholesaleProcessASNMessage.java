package com.vsi.oms.api;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleProcessASNMessage implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleProcessASNMessage.class);
	
	public Document processASNMessage (YFSEnvironment env, Document inXML) throws Exception {
		log.beginTimer("VSIWholesaleProcessASNMessage.processASNMessage");
		Document docShipmentMsg = null;
		try{
			Element eleShipment = inXML.getDocumentElement();
			String strEnterpriseCode = eleShipment.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strOHKey = eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String strShipKey = eleShipment.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			if(!YFCCommon.isVoid(strEnterpriseCode)) {
					Element eleCommonCode = (Element) (VSIUtils.getCommonCodeList(env,ATTR_VSI_WH_ASN_TYPE,strEnterpriseCode,ATTR_DEFAULT).get(0));
					if(!YFCCommon.isVoid(eleCommonCode)) {
						String strCodeShortDesc = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
						if(log.isDebugEnabled()){
							log.debug("VSIWholesaleProcessASNMessage.processASNMessage: CodeShortdesc is " + strCodeShortDesc);
						}
						if(YFCCommon.isVoid(strCodeShortDesc) || strCodeShortDesc.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_SHIP)) {
							createAndPostASNMessage(env,strShipKey,strCodeShortDesc);
						}
						else if (strCodeShortDesc.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_ORDER)) {
							Document docInputOrderList = XMLUtil.createDocument(ELE_ORDER);
							Element eleOrderInput = docInputOrderList.getDocumentElement();
							eleOrderInput.setAttribute(ATTR_ORDER_HEADER_KEY, strOHKey);
							Document docOutputOrderList = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_ORDER_LIST,docInputOrderList);
							if(!YFCCommon.isVoid(docOutputOrderList)) {
								Element eleOrderOutput = (Element) docOutputOrderList.getDocumentElement().getElementsByTagName(ELE_ORDER).item(0);
								if(!YFCCommon.isVoid(eleOrderOutput)){
									String strMinOrderStatus = eleOrderOutput.getAttribute(ATTR_MIN_ORDER_STATUS);
									if(!YFCCommon.isVoid(strMinOrderStatus)) {
										double attrMinOrderStatus =Double.parseDouble(strMinOrderStatus);
										if (attrMinOrderStatus >= 3700) {
											createAndPostASNMessage(env,strOHKey,strCodeShortDesc);
											}
										}
									}
									
								}
								
							 }
						}
					}
				}
				catch(Exception ex) {
					log.error("Exception in VSIWholesaleProcessASNMessage.createShipmentMsg() : " + ex.getMessage());
					throw VSIUtils.getYFSException(ex);
				}

		log.endTimer("VSIWholesaleProcessASNMessage.processASNMessage");
		return docShipmentMsg;
	}
	
	private void createAndPostASNMessage(YFSEnvironment env,String strKey,String strCodeDesc) throws Exception
	{
		log.beginTimer("VSIWholesaleProcessASNMessage.createAndPostASNMessage");
		if(!YFCCommon.isVoid(strKey)) {
			Document docInputShipment = XMLUtil.createDocument(ELE_SHIPMENT);
			Element eleShipInput = docInputShipment.getDocumentElement();
			if(strCodeDesc.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_SHIP)) {
				eleShipInput.setAttribute(ATTR_SHIPMENT_KEY, strKey);
			}else {
				eleShipInput.setAttribute(ATTR_ORDER_HEADER_KEY, strKey);
			}
			Document docOutputShipment = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_SHIPMENT_LIST_FOR_ASN,docInputShipment);
			if(!YFCCommon.isVoid(docOutputShipment)) {
				try {
					VSIUtils.invokeService(env,SERVICE_POST_ASN_MSG_TO_IIB,docOutputShipment);
				}
				catch (Exception e) {
					log.error("Exception in VSIWholesaleProcessASNMessage.createShipmentMsg() : " + e.getMessage());
					throw VSIUtils.getYFSException(e);
				}
			
		}
	}
		log.endTimer("VSIWholesaleProcessASNMessage.createAndPostASNMessage");
	}		
}