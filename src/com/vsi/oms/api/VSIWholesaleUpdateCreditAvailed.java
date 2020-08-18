package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleUpdateCreditAvailed implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleUpdateCreditAvailed.class);
	
	public Document updateCreditAvailed(YFSEnvironment env, Document inXML) throws Exception {
		log.beginTimer("VSIWholesaleUpdateCreditAvailed.updateCreditAvailed");
		
		Element eleOrder = inXML.getDocumentElement();
		
		String strEnterpriseCode = eleOrder.getAttribute(ATTR_ENTERPRISE_CODE);
		String strDocumentType = eleOrder.getAttribute(ATTR_DOCUMENT_TYPE);
		//Element elePriceInfo = (Element) eleOrder.getElementsByTagName(ELE_PRICE_INFO).item(0);
		//Element eleOrderAudit = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_AUDIT);
		Double dblNewValue = 0.0;
		Double dblOldValue = 0.0;
		Double dblChangeInTotalAmount = 0.0;
		
		if(!YFCObject.isVoid(eleOrder)){
			
			NodeList nlAttribute = eleOrder.getElementsByTagName(ELE_ATTRIBUTE);
			for(int i = 0; i < nlAttribute.getLength(); i++){

				Element eleAttribute = (Element) nlAttribute.item(i);
				String strName = SCXmlUtil.getAttribute(eleAttribute, ATTR_NAME);
				if(strName.equalsIgnoreCase(ATTR_TOTAL_AMOUNT)){

					String strNewValue = SCXmlUtil.getAttribute(eleAttribute, ATTR_NEW_VALUE);
					if(!YFCObject.isVoid(strNewValue)){
						dblNewValue = Double.valueOf(strNewValue);
					}
					String strOldValue = SCXmlUtil.getAttribute(eleAttribute, ATTR_OLD_VALUE);
					if(!YFCObject.isVoid(strOldValue)){
						dblOldValue = Double.valueOf(strOldValue);
					}

					dblChangeInTotalAmount = dblNewValue - dblOldValue;
				}
			}
		}
		
		if(!YFCCommon.isVoid(strEnterpriseCode) && dblChangeInTotalAmount != 0.0) {
			Document docInputCustCreditDtls = XMLUtil.createDocument(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS);
			Element eleCustCreditDtls = docInputCustCreditDtls.getDocumentElement();
			eleCustCreditDtls.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,strEnterpriseCode);
			
			if(log.isDebugEnabled()) {
				log.debug("Input to service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docInputCustCreditDtls) );
			}
			
			Document docOutputCustCreditDtls = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_CUST_CREDIT_DTLS_LIST,docInputCustCreditDtls);
			if(log.isDebugEnabled()) {
				log.debug("Output from service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docOutputCustCreditDtls) );
			}
			
			if (!YFCCommon.isVoid(docOutputCustCreditDtls) && docOutputCustCreditDtls.getDocumentElement().hasChildNodes()) {
				eleCustCreditDtls = (Element) docOutputCustCreditDtls.getElementsByTagName(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS).item(0);
				Double dCreditAvailed = Double.valueOf(SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CREDIT_AVAILED));
				
				if(!YFCCommon.isVoid(strDocumentType) && strDocumentType.equals(ATTR_DOCUMENT_TYPE_SALES)){
					dCreditAvailed = dCreditAvailed + dblChangeInTotalAmount;	
				}
			
				
				
				if(!YFCCommon.isVoid(strDocumentType) && strDocumentType.equals(RETURN_DOCUMENT_TYPE)){
					
						dCreditAvailed = dCreditAvailed - dblChangeInTotalAmount;
					}
			
				if(log.isDebugEnabled()){
					log.debug("VSIWholesaleUpdateCreditAvailed.updateCreditAvailed: - New credit Availed amount is"+ dCreditAvailed.toString());
				}
				Document docUpdateCustCreditDtls = XMLUtil.createDocument(ELE_VSI_WH_CUST_CREDIT_DETAILS);
				Element eleUpdateCustCreditDtls = docUpdateCustCreditDtls.getDocumentElement(); 
				eleUpdateCustCreditDtls.setAttribute(ATTR_CREDIT_AVAILED, dCreditAvailed.toString());
				eleUpdateCustCreditDtls.setAttribute(ATTR_CUST_CREDIT_DTLS_KEY, eleCustCreditDtls.getAttribute(ATTR_CUST_CREDIT_DTLS_KEY));
				eleUpdateCustCreditDtls.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
				VSIUtils.invokeService(env, SERVICE_UPDATE_CUST_CREDIT_DTLS, docUpdateCustCreditDtls);
			}
		}
		
		log.endTimer("VSIWholesaleUpdateCreditAvailed.updateCreditAvailed");
		return inXML;
	}

}
