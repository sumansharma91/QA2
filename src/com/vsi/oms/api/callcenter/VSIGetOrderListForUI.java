package com.vsi.oms.api.callcenter;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetOrderListForUI implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetOrderListForUI.class);

	public Document createOrderListForUI(YFSEnvironment env, Document inXML) throws Exception {
		
		log.beginTimer("VSIGetOrderListForUI.createOrderListForUI");
		
		//Document getOrderListOut = null;
		Element eleOrder = inXML.getDocumentElement();
		String strEnterpriceCode = eleOrder.getAttribute(ATTR_ENTERPRISE_CODE);

		if (YFCCommon.isVoid(strEnterpriceCode)) {
			String strUserId = env.getUserId();
			//String strOrderNo = eleOrder.getAttribute(ATTR_ORDER_NO);

			// Create Input for getOrganizationList

			Document getOrganizationListIn = SCXmlUtil.createDocument(ELE_ORGANIZATION);
			Element organizationElm = getOrganizationListIn.getDocumentElement();
			//organizationElm.setAttribute(ATTR_DISPLAY_LOCALIZED_FIELD_IN_LOCALE, ATTR_EN_US_EST);
			organizationElm.setAttribute(ATTR_IGNORE_ORDERING, FLAG_Y);
			Element orgRoleListElm = SCXmlUtil.createChild(organizationElm, ELE_ORG_ROLE_LIST);

			Element orgRoleElm = SCXmlUtil.createChild(orgRoleListElm, ELE_ORG_ROLE);
			orgRoleElm.setAttribute(ATTR_ROLE_KEY, ATTR_ENTERPRISE);

			Element orgRoleElm2 = SCXmlUtil.createChild(orgRoleListElm, ELE_ORG_ROLE);
			orgRoleElm2.setAttribute(ATTR_ROLE_KEY, ATTR_SELLER);

			Element dataAccessFilterElm = SCXmlUtil.createChild(organizationElm, ELE_DATA_ACCESS_FILTER);
			dataAccessFilterElm.setAttribute(ATTR_USER_Id, strUserId);
			Element orderByElm = SCXmlUtil.createChild(organizationElm, ELE_ORDER_BY);
			Element attributeElm = SCXmlUtil.createChild(orderByElm, ELE_ATTRIBUTE);
			attributeElm.setAttribute(ATTR_DESC, FLAG_N);
			attributeElm.setAttribute(ATTR_NAME, ATTR_ORGANIZATION_NAME);

			if (log.isDebugEnabled()) {
				log.debug("Input to getOrganizationList  in class VSIGetOrderListForUI"
						+ XMLUtil.getXMLString(getOrganizationListIn));
			}

			Document getOrgnizationListOut = VSIUtils.invokeService(env, SERVICE_GET_ORG_LIST, getOrganizationListIn);
			
			if (log.isDebugEnabled()) {
				log.debug("Output to getOrganizationList in class VSIGetOrderListForUI"
						+ XMLUtil.getXMLString(getOrgnizationListOut));
			}

			if (!YFCCommon.isVoid(getOrgnizationListOut)
					&& getOrgnizationListOut.getDocumentElement().hasChildNodes()) {

				Element orgnizationListOutElm = getOrgnizationListOut.getDocumentElement();
				ArrayList<Element> arrOrganization = SCXmlUtil.getChildren(orgnizationListOutElm, ELE_ORGANIZATION);
				ArrayList<String> organizationList = new ArrayList<String>();

				for (Element eleOrganization : arrOrganization) {

					String strOrganizationCode = eleOrganization.getAttribute(ATTR_ORGANIZATION_CODE);
					organizationList.add(strOrganizationCode);
				}

				Element complexQueryElm = SCXmlUtil.createChild(eleOrder, ELE_COMPLEX_QUERY);
				complexQueryElm.setAttribute(ATTR_OPERATOR, AND);
				Element andElm = SCXmlUtil.createChild(complexQueryElm, AND_QUERY);
				Element orElm = SCXmlUtil.createChild(andElm, OR_QUERY);

				for (int i = 0; i < organizationList.size(); i++) {

					Element expElm = SCXmlUtil.createChild(orElm, ELE_EXP);
					expElm.setAttribute(ATTR_NAME, ATTR_ENTERPRISE_CODE);
					expElm.setAttribute(ATTR_VALUE, organizationList.get(i));
				}

				if (log.isDebugEnabled()) {
					log.debug("Input to getOrderList in class VSIGetOrderListForUI"
							+ XMLUtil.getXMLString(inXML));
				}

			}
		}

		return inXML;

	}
}
