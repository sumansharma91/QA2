package com.vsi.oms.api;

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

/**
 * @author IBM, Inc.
 * This implementation is called on success of createOrder. 
 * This fetch credit details such as credit limit, credit availed for a specific partner
 * from the custom table VSI_WHOLESALE_CUST_CREDIT_DETAILS. 
 * Based on the details, available credit is calculated, and is either updated to DB or 
 * VSI_WH_CRDT_DIS_HOLD hold is applied to inform the wholesale reps about credit discrepancy.
 */
public class VSIUpdateCreditLimitOnCreate implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIUpdateCreditLimitOnCreate.class);
	
	/**
	 * This custom implementation is used to check credit details
	 * and update it based on the wholesale order.
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	
	public Document checkAndUpdateCreditLimit(YFSEnvironment env, Document inXML) throws Exception {
		log.beginTimer("VSIUpdateCreditLimitOnCreate.checkAndUpdateCreditLimit");
		StringBuffer strCreditDiscrepancy = new StringBuffer();
		Document creditDiscrepancyXML = null;
		
		try{
			Element eleOrder = inXML.getDocumentElement();
			String strOrderTotal = "0.0";
			Double dOrderTotal = 0.0;
		String strEnterpriseCode = eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strDocType = eleOrder.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
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
			if(log.isDebugEnabled()){
				log.debug("VSIUpdateCreditLimitOnCreate.validateCreditLimit: docOutputCustCreditDtls has child nodes");
			}
			eleCustCreditDtls = (Element) docOutputCustCreditDtls.getElementsByTagName(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS).item(0);
			Double dCreditLmt = Double.valueOf(SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CREDIT_LIMIT));
			Double dCreditAvailed = Double.valueOf(SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CREDIT_AVAILED));
			Double dCreditBalance = Double.valueOf(SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CURRENT_BALANCE));
			String strCrdHoldFlag = SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CREDIT_HOLD_FLAG);
			
			Double dAvailableLimit = dCreditLmt - dCreditAvailed - dCreditBalance;
			
			if(log.isDebugEnabled()) {
				log.debug("Available Limit:"+dAvailableLimit.toString() +"\n Credit Availed: "+dCreditAvailed.toString()+"\n Order Total "+ strOrderTotal);
			}
			if(!YFCCommon.isVoid(strDocType) && strDocType.equals(VSIConstants.ATTR_DOCUMENT_TYPE_SALES)){
				strOrderTotal = eleOrder.getAttribute(VSIConstants.ATTR_ORIGINAL_TOTAL_AMOUNT);
				dOrderTotal = Double.valueOf(strOrderTotal);
				if(strCrdHoldFlag.equalsIgnoreCase("Y") || (dAvailableLimit < dOrderTotal)) {
					strCreditDiscrepancy.append(strOrderNo + " has exceeded the credit limit or the customer profile has a credit hold.\nOrder Total: " + strOrderTotal +"\nCredit Limit: " + dCreditLmt + "\nCredit Availed: "+ dCreditAvailed + "\nCredit Balance: " + dCreditBalance + "\nCredit Hold: " + strCrdHoldFlag);
					if(log.isDebugEnabled()){
						log.debug("VSIUpdateCreditLimitOnCreate.validateCreditLimit: AvailableLimit is less than Order Total. Available limit is - "+ dAvailableLimit);
					}
					
					creditDiscrepancyXML = SCXmlUtil.createDocument("CreditDiscrepancy");
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_ENTERPRISE_CODE,strEnterpriseCode);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_ORDER_NO,strOrderNo);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_ORDER_TOTAL, strOrderTotal);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_CREDIT_LIMIT, dCreditLmt);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_CREDIT_AVAILED, dCreditAvailed);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_CURRENT_BALANCE, dCreditBalance);
					SCXmlUtil.setAttribute(creditDiscrepancyXML.getDocumentElement(), ATTR_CREDIT_HOLD_FLAG, strCrdHoldFlag);
				}
				dCreditAvailed = dCreditAvailed + dOrderTotal;
			}
			else if(!YFCCommon.isVoid(strDocType) && strDocType.equals(VSIConstants.RETURN_DOCUMENT_TYPE)){
				if(log.isDebugEnabled()){
					log.debug("VSIUpdateCreditLimitOnCreate.validateCreditLimit: Doc Type is return... Credit Availed will be decreased");
				}
				Element elePriceInfo = SCXmlUtil.getChildElement(eleOrder, ELE_PRICE_INFO);
				if(!YFCObject.isVoid(elePriceInfo)){
					strOrderTotal = elePriceInfo.getAttribute(VSIConstants.ATTR_TOTAL_AMOUNT);
					if(YFCObject.isVoid(strOrderTotal)){
						strOrderTotal = "0.0";
					}
				}
				dOrderTotal = Double.valueOf(strOrderTotal);
				dCreditAvailed = dCreditAvailed - dOrderTotal;
			}
			Document docUpdateCustCreditDtls = XMLUtil.createDocument(ELE_VSI_WH_CUST_CREDIT_DETAILS);
			Element eleUpdateCustCreditDtls = docUpdateCustCreditDtls.getDocumentElement(); 
			eleUpdateCustCreditDtls.setAttribute(ATTR_CREDIT_AVAILED, dCreditAvailed.toString());
			eleUpdateCustCreditDtls.setAttribute(ATTR_CUST_CREDIT_DTLS_KEY, eleCustCreditDtls.getAttribute(ATTR_CUST_CREDIT_DTLS_KEY));
			eleUpdateCustCreditDtls.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
			if(log.isDebugEnabled()) {
				log.debug("Input for update service VSIWholeSaleUpdateCustCreditLimitDtls:"+ XMLUtil.getXMLString(docUpdateCustCreditDtls) );
				log.debug("VSIUpdateCreditLimitOnCreate.validateCreditLimit: String returned" + strCreditDiscrepancy.toString());
			}
			
			VSIUtils.invokeService(env, SERVICE_UPDATE_CUST_CREDIT_DTLS, docUpdateCustCreditDtls);
		}
		
		if(!strCreditDiscrepancy.toString().isEmpty()){
			env.setTxnObject(ATTR_VSI_WH_CRDT_DIS_HOLD, strCreditDiscrepancy);
			env.setTxnObject("VSI_WH_CRDT_DIS_HOLD_XML", creditDiscrepancyXML);
			applyHold(env, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY),ATTR_VSI_WH_CRDT_DIS_HOLD);
		}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		log.endTimer("VSIUpdateCreditLimitOnCreate.checkAndUpdateCreditLimit");
		return inXML;
	}
	

	private void applyHold(YFSEnvironment env, String strOrderHeaderKey,String strHoldtype) throws Exception{
		log.beginTimer("VSIUpdateCreditLimitOnCreate.applyHold");
		// TODO Auto-generated method stub
		
		try {
		Document docChangeOrderInput = XMLUtil.createDocument(ELE_ORDER);
		Element eleOrder = docChangeOrderInput.getDocumentElement();
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
		Element eleHoldTypes = SCXmlUtil.createChild(eleOrder, ELE_ORDER_HOLD_TYPES);
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(ATTR_HOLD_TYPE, strHoldtype);
		eleHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);
		if(log.isDebugEnabled()) {
			log.debug("Input to change Order service VSIWholeSaleChangeOrder :"+ XMLUtil.getXMLString(docChangeOrderInput) );
		}
		Document docChangeOrderOutput = VSIUtils.invokeService(env,SERVICE_CHANGE_ORDER, docChangeOrderInput);
		if(log.isDebugEnabled()) {
			log.debug("Input to change Order service VSIWholeSaleChangeOrder :"+ XMLUtil.getXMLString(docChangeOrderInput) );
		}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.endTimer("VSIUpdateCreditLimitOnCreate.applyHold");
	}
}
