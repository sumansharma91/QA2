package com.vsi.oms.api.callcenter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetCompleteCustomerDetails implements VSIConstants{

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetCompleteCustomerDetails.class);

	public Document createGetCompleteCustomerDetailsInputXML(YFSEnvironment env, Document inXML) throws Exception{

		if(log.isDebugEnabled()){
			log.debug("VSIGetCompleteCustomerDetails.createGetCompleteCustomerDetailsInputXML() InXML: " + 
					SCXmlUtil.getString(inXML));
		}
		
		Boolean isWholesale = false;
		Element eleCustomer = inXML.getDocumentElement(); 
		String orderHeaderKey = SCXmlUtil.getAttribute(eleCustomer, ATTR_ORDER_HEADER_KEY);
		if(!YFCObject.isVoid(orderHeaderKey)){

			Document getOrderListInXML = SCXmlUtil.createDocument(ELE_ORDER);
			Element eleOrder = getOrderListInXML.getDocumentElement();
			SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_HEADER_KEY, orderHeaderKey);

			Document getOrderListOutXML = VSIUtils.invokeService(env, "VSIGetOrderListForUI", getOrderListInXML);
			Element eleOrderList = getOrderListOutXML.getDocumentElement();
			if(eleOrderList.hasChildNodes()){

				eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
				String orderType = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_TYPE);
				if(!YFCObject.isVoid(orderType) && orderType.equalsIgnoreCase(WHOLESALE)){
					
					isWholesale = true;
					String enterpriseCode = SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE);
					SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_ID, enterpriseCode);
					SCXmlUtil.setAttribute(eleCustomer, ATTR_ORGANIZATION_CODE, enterpriseCode);
					SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_TYPE, "01");
				}
			}
		}
		
		String orgCode = SCXmlUtil.getAttribute(eleCustomer, ATTR_ORGANIZATION_CODE);
		if(!isWholesale && YFCObject.isVoid(orgCode)){
			SCXmlUtil.setAttribute(eleCustomer, ATTR_ORGANIZATION_CODE, VSICOM_ENTERPRISE_CODE);
		}
		
		if(log.isDebugEnabled()){
			log.debug("VSIGetCompleteCustomerDetails.createGetCompleteCustomerDetailsInputXML() OutXML: " + 
					SCXmlUtil.getString(inXML));
		}
		return inXML;
	}
}
