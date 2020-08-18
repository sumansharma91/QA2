package com.vsi.oms.api.callcenter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetReturnReasonListForUI implements VSIConstants{

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetReturnReasonListForUI.class);

	public Document getReturnReasonList(YFSEnvironment env, Document inXML) throws Exception {
		
		log.beginTimer("VSIGetReturnReasonListForUI.getReturnReasonList");
		if(log.isDebugEnabled()){
			log.debug("Inside VSIGetReturnReasonListForUI.getReturnReasonList" + XMLUtil.getXMLString(inXML) );
		}

		Element eleOrder = inXML.getDocumentElement();
		String strCodeType = SCXmlUtil.getAttribute(eleOrder, ATTR_CODE_TYPE);
		if(!strCodeType.trim().equalsIgnoreCase("RETURN_REASON")){
			return inXML;
		}
		String strOrderHeaderkey = eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY);


		if (!YFCCommon.isVoid(strOrderHeaderkey))
		{

			Document getOrderListIn = SCXmlUtil.createDocument(ELE_ORDER);
			Element getOrderListEle = getOrderListIn.getDocumentElement();


			getOrderListEle.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderkey);

			if (log.isDebugEnabled()) {
				log.debug("Input to getOrderList  in class VSIGetReturnReasonListForUI"
						+ XMLUtil.getXMLString(getOrderListIn));
			}

			Document getOrderListOut = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST, getOrderListIn);

			if (log.isDebugEnabled()) {
				log.debug("Output to getOrderList in class VSIGetReturnReasonListForUI"
						+ XMLUtil.getXMLString(getOrderListOut));
			}


			String strOrderType =  SCXmlUtil.getXpathAttribute(getOrderListOut.getDocumentElement(),"/OrderList/Order/@OrderType" );
			String strEnterpriseCode =  SCXmlUtil.getXpathAttribute(getOrderListOut.getDocumentElement(),"/OrderList/Order/@EnterpriseCode" );

			if (!YFCCommon.isVoid(strOrderType) && strOrderType.equalsIgnoreCase(WHOLESALE))
			{
				eleOrder.removeAttribute(ATTR_CALLING_ORGANIZATION_CODE);
				eleOrder.setAttribute(ATTR_ORGANIZATION_CODE, strEnterpriseCode);

			}


		}else{
			
			String callingOrganizationCode = SCXmlUtil.getAttribute(eleOrder, ATTR_CALLING_ORGANIZATION_CODE);
			if(!YFCObject.isVoid(callingOrganizationCode)){
				
				Document getOrganizationListIn = SCXmlUtil.createDocument(ELE_ORGANIZATION);
				Element organizationElm = getOrganizationListIn.getDocumentElement();
				SCXmlUtil.setAttribute(organizationElm, ATTR_ORGANIZATION_CODE, callingOrganizationCode);
				Document getOrgnizationListOut = VSIUtils.invokeService(env, SERVICE_GET_ORG_LIST, getOrganizationListIn);
				
				if (log.isDebugEnabled()) {
					log.debug("Output to getOrganizationList in class VSIGetOrderListForUI"
							+ XMLUtil.getXMLString(getOrgnizationListOut));
				}
				
				Boolean isWholesale = false;
				if(getOrgnizationListOut.getDocumentElement().hasChildNodes()){
					
					Element eleOrganizationList = getOrgnizationListOut.getDocumentElement();
					Element eleOrganization = SCXmlUtil.getChildElement(eleOrganizationList, ELE_ORGANIZATION);
					if(!YFCObject.isVoid(eleOrganization)){
						
						Element eleExtn = SCXmlUtil.getChildElement(eleOrganization, ELE_EXTN);
						if(!YFCObject.isVoid(eleExtn)){
							
							isWholesale = SCXmlUtil.getBooleanAttribute(eleExtn, ATTR_EXTN_IS_WHOLESALE_ORG);
							if(log.isDebugEnabled()){
								log.debug("Org Code: " + callingOrganizationCode);
								log.debug("IsWholesale: " + isWholesale);
							}
						}
					}
				}
				
				if(isWholesale){
					
					eleOrder.removeAttribute(ATTR_CALLING_ORGANIZATION_CODE);
					eleOrder.setAttribute(ATTR_ORGANIZATION_CODE, callingOrganizationCode);
				}
			}
		}
		
		log.endTimer("VSIGetReturnReasonListForUI.getReturnReasonList and OutXml is" + XMLUtil.getXMLString(inXML));
		return inXML;

	}

}
