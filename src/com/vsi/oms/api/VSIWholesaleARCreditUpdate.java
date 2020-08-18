package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleARCreditUpdate implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleARCreditUpdate.class);

	public Document updateCreditAvail(YFSEnvironment env, Document inXML) throws Exception {
		log.beginTimer("VSIWholesaleARCreditUpdate.updateCreditAvail");
		try {
			Element eleVSIWhCustCreditDetailsList = inXML.getDocumentElement();
			NodeList nlVSIWhCustCreditDetails = eleVSIWhCustCreditDetailsList.getElementsByTagName(ELE_VSI_WH_CUST_CREDIT_DETAILS);
			for(int i = 0; i < nlVSIWhCustCreditDetails.getLength(); i++){

				Element eleCustCreditDtls = (Element) nlVSIWhCustCreditDetails.item(i);
				String strCustomerID = eleCustCreditDtls.getAttribute(ATTR_CUSTOMER_ID);
				String strCreditLimit = eleCustCreditDtls.getAttribute(ATTR_CREDIT_LIMIT);
				String strCurrentBalance = eleCustCreditDtls.getAttribute(ATTR_CURRENT_BALANCE);
				String strCreditHoldFlag = eleCustCreditDtls.getAttribute(ATTR_CREDIT_HOLD_FLAG);
				String strTermsDiscountPercentage = eleCustCreditDtls.getAttribute(ATTR_TERMS_DISCOUNT_PERCENTAGE);
				String strTermsDiscountDueDays = eleCustCreditDtls.getAttribute(ATTR_TERMS_DISCOUNT_DUE_DAYS);
				String strTermsNetDueDays = eleCustCreditDtls.getAttribute(ATTR_TERMS_NET_DUE_DAYS);
				String strPaymentTerms = eleCustCreditDtls.getAttribute(ATTR_PAYMENT_TERMS);

				if(!YFCCommon.isVoid(strCustomerID) && !strCustomerID.isEmpty())
				{
					Document docInputCustCreditDtls = XMLUtil.createDocument(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS);
					Element eleInputCustCreditDtls = docInputCustCreditDtls.getDocumentElement();
					eleInputCustCreditDtls.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,strCustomerID);
					eleInputCustCreditDtls.setAttribute(ATTR_CUST_ID_QRY_TYPE, LIKE);
					if(log.isDebugEnabled()) {
						log.debug("VSIWholesaleARCreditUpdate.updateCreditAvail: Input to service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docInputCustCreditDtls) );
					}
					Document docOutputCustCreditDtls = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_CUST_CREDIT_DTLS_LIST,docInputCustCreditDtls);
					if(log.isDebugEnabled()) {
						log.debug("VSIWholesaleARCreditUpdate.updateCreditAvail: Output from service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docOutputCustCreditDtls) );
					}
					if (!YFCCommon.isVoid(docOutputCustCreditDtls) && docOutputCustCreditDtls.getDocumentElement().hasChildNodes()) {
						//Customer Details are already present in the custom DB
						Element eleOuptutCustCreditDtls = (Element)docOutputCustCreditDtls.getDocumentElement().getElementsByTagName(ELE_VSI_WH_CUST_CREDIT_DETAILS).item(0);
						if(!YFCCommon.isVoid(eleOuptutCustCreditDtls)) {
							eleInputCustCreditDtls.setAttribute(ATTR_CREDIT_LIMIT, strCreditLimit);
							eleInputCustCreditDtls.setAttribute(ATTR_CURRENT_BALANCE, strCurrentBalance);
							eleInputCustCreditDtls.setAttribute(ATTR_CUST_CREDIT_DTLS_KEY,eleOuptutCustCreditDtls.getAttribute(ATTR_CUST_CREDIT_DTLS_KEY));
							eleInputCustCreditDtls.setAttribute(ATTR_CUSTOMER_ID,eleOuptutCustCreditDtls.getAttribute(ATTR_CUSTOMER_ID));
							eleInputCustCreditDtls.setAttribute(ATTR_CREDIT_HOLD_FLAG, strCreditHoldFlag);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_DISCOUNT_PERCENTAGE, strTermsDiscountPercentage);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_DISCOUNT_DUE_DAYS, strTermsDiscountDueDays);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_NET_DUE_DAYS, strTermsNetDueDays);
							eleInputCustCreditDtls.setAttribute(ATTR_PAYMENT_TERMS, strPaymentTerms);
							if(log.isDebugEnabled()) {
								log.debug("VSIWholesaleARCreditUpdate.updateCreditAvail: Input for update service VSIWholeSaleUpdateCustCreditLimitDtls:"+ XMLUtil.getXMLString(docInputCustCreditDtls) );
							}
							VSIUtils.invokeService(env, SERVICE_UPDATE_CUST_CREDIT_DTLS, docInputCustCreditDtls);
						}
					}
					else {
						//Customer Details are not already present in the custom DB
						Document docGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
						Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
						eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, ATTR_VSI_VIRTUAL_STORE_NO);
						Element eleComplexQry = SCXmlUtil.createChild(eleCommonCode, ELE_COMPLEX_QUERY);
						eleComplexQry.setAttribute(ATTR_OPERATOR, AND);
						Element eleAndOperator = SCXmlUtil.createChild(eleComplexQry, AND_QUERY);
						Element eleExpression = SCXmlUtil.createChild(eleAndOperator, ELE_EXP);
						eleExpression.setAttribute(ATTR_NAME,ATTR_CODE_SHORT_DESCRIPTION);
						eleExpression.setAttribute(ATTR_VALUE,strCustomerID);
						eleExpression.setAttribute(ATTR_QUERY_TYPE,LIKE);

						// Call getCommonCodeList API 
						docGetCommonCodeList = VSIUtils.invokeAPI(env, VSIConstants.API_COMMON_CODE_LIST, docGetCommonCodeList);

						// Get the list of common codes from the response document
						Element eleCommonCodeList = docGetCommonCodeList.getDocumentElement();
						if(eleCommonCodeList.hasChildNodes()){
							eleCommonCode = SCXmlUtil.getChildElement(eleCommonCodeList, VSIConstants.ELE_COMMON_CODE);
							String strEnterpriseCode = SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_VALUE);
							//strCustomerID = SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_SHORT_DESCRIPTION);
							Document docCustCreditDetails = XMLUtil.createDocument(ELE_VSI_WH_CUST_CREDIT_DETAILS);
							Element eleCustCreditDetails = docCustCreditDetails.getDocumentElement();
							eleCustCreditDetails.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
							eleCustCreditDetails.setAttribute(ATTR_CREDIT_LIMIT, strCreditLimit);
							eleCustCreditDetails.setAttribute(ATTR_CURRENT_BALANCE, strCurrentBalance);
							eleCustCreditDetails.setAttribute(ATTR_CUSTOMER_ID, strCustomerID);
							eleInputCustCreditDtls.setAttribute(ATTR_CREDIT_HOLD_FLAG, strCreditHoldFlag);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_DISCOUNT_PERCENTAGE, strTermsDiscountPercentage);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_DISCOUNT_DUE_DAYS, strTermsDiscountDueDays);
							eleInputCustCreditDtls.setAttribute(ATTR_TERMS_NET_DUE_DAYS, strTermsNetDueDays);
							eleInputCustCreditDtls.setAttribute(ATTR_PAYMENT_TERMS, strPaymentTerms);
							if(log.isDebugEnabled()) {
								log.debug("VSIWholesaleARCreditUpdate.updateCreditAvail: Input for update service VSIWholeSaleCreateCustCreditLimitDtls:"+ XMLUtil.getXMLString(docCustCreditDetails) );
							}
							docOutputCustCreditDtls = VSIUtils.invokeService(env,VSIConstants.SERVICE_CREATE_CUST_CREDIT_DTLS,docCustCreditDetails);
						}else{
							createException(env, eleCustCreditDtls);
						}
					}
				}else{
					createException(env, eleCustCreditDtls);
				}
			}
		}
		catch(Exception e) {
			log.error("Exception in VSIWholesaleARCreditUpdate.updateCreditAvail"+e.getMessage());
			throw VSIUtils.getYFSException(e, "Exception occurred",
					"Exception in VSIWholesaleARCreditUpdate.updateCreditAvail() "+e.getMessage());
		}
		log.endTimer("VSIWholesaleARCreditUpdate.updateCreditAvail");
		return inXML;
	}

	private void createException(YFSEnvironment env, Element element) throws Exception{
		//OMS-2019: Start
		String strCustID=element.getAttribute(ATTR_CUSTOMER_ID);
		if(!YFCCommon.isVoid(strCustID) && !ENT_VSI.equals(strCustID))
		{
		Document createExceptionInXML = SCXmlUtil.createDocument(ELE_INBOX);
		Element eleInbox = createExceptionInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleInbox, ATTR_EXCEPTION_TYPE, "VSI_WH_INVALID_AR_CUSTOMER_RECORD_ALERT");

		NamedNodeMap nnm= element.getAttributes();
		StringBuffer description = new StringBuffer();
		for(int i = 0; i < nnm.getLength(); i++){
			
			Node attribute = nnm.item(i);
			description.append(attribute.getNodeName());
			description.append(" = ");
			description.append(attribute.getNodeValue());
			description.append("\n");
		}
		
		SCXmlUtil.setAttribute(eleInbox, ATTR_DETAIL_DESCRIPTION, description.toString());
		SCXmlUtil.setAttribute(eleInbox, ATTR_QUEUE_ID, "VSI_WHOLESALE_ALERT_Q");
		SCXmlUtil.setAttribute(eleInbox, ATTR_ENTERPRISE_KEY, ATTR_DEFAULT);

		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION, createExceptionInXML);
		}
		//OMS-2019: End
	}
}
