package com.vsi.oms.api.callcenter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleInputForGetChargeCategoryList  implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleInputForGetChargeCategoryList.class);

	public Document formInputForGetChargeCategoryList(YFSEnvironment env, Document inXML) throws Exception {
		
		log.beginTimer("VSIWholesaleInputForGetChargeCategoryList.formInputForGetChargeCategoryList");
		boolean bRemoveComplexQryElement = false;
		Element eleChargeCategory = inXML.getDocumentElement();
		String strOrderHeaderKey = SCXmlUtil.getAttribute(eleChargeCategory, ATTR_ORDER_HEADER_KEY);
		String strOrderType = SCXmlUtil.getAttribute(eleChargeCategory, ATTR_ORDER_TYPE);

		if(!YFCCommon.isVoid(strOrderType) && strOrderType.equals(WHOLESALE)) {
			bRemoveComplexQryElement = true;
		}

		if(!YFCCommon.isVoid(strOrderHeaderKey)) {
			
			if(log.isDebugEnabled()){
				log.debug("VSIWholesaleInputForGetChargeCategoryList.formInputForGetChargeCategoryList --- fetching Order Type using getOrderList API call");
			}
			Document docInputOrderList = XMLUtil.createDocument(ELE_ORDER);
			Element eleOrder = docInputOrderList.getDocumentElement();
			eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			Document docOutputOrderList = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_ORDER_LIST,docInputOrderList);
			if(!YFCCommon.isVoid(docOutputOrderList)) {
				Element eleOrderOutput = (Element) docOutputOrderList.getDocumentElement().getElementsByTagName(ELE_ORDER).item(0);
				if(!YFCCommon.isVoid(eleOrderOutput)){
					strOrderType = eleOrderOutput.getAttribute(ATTR_ORDER_TYPE);
					if(!YFCCommon.isVoid(strOrderType) && strOrderType.equalsIgnoreCase(WHOLESALE)) {
						bRemoveComplexQryElement = true;
					}
				}
			}
		}

		// For Wholesale orders, all charge category names should be prefixed with the enterprise code
		// E.g Charge Category for NAVY_EXCHANGE Charges - NAVY_EXCHANGE_C
		// Discount category for NAVY_EXCHANGE Discounts - NAVY_EXCHANGE_D
		if(bRemoveComplexQryElement) {
			String callingOrgCode = SCXmlUtil.getAttribute(eleChargeCategory, ATTR_CALL_ORG_CODE);
			Element eleComplexQry = (Element)eleChargeCategory.getElementsByTagName(VSIConstants.ELE_COMPLEX_QUERY).item(0);
			if(!YFCCommon.isVoid(eleComplexQry)){
				XMLUtil.removeChild(eleChargeCategory,eleComplexQry);
			}

			SCXmlUtil.setAttribute(eleChargeCategory, ATTR_CHARGE_CATEGORY, callingOrgCode);
			SCXmlUtil.setAttribute(eleChargeCategory, ATTR_CHARGE_CATEGORY.concat(ATTR_QUERY_TYPE), LIKE);
		}

		if(log.isDebugEnabled()) {
			log.debug("VSIWholesaleInputForGetChargeCategoryList.formInputForGetChargeCategoryList--- returning xml"+XMLUtil.getXMLString(inXML));
		}
		log.endTimer("VSIWholesaleInputForGetChargeCategoryList.formInputForGetChargeCategoryList");
		return inXML;
	}

}
