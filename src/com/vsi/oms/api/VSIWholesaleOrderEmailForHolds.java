package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleOrderEmailForHolds implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleOrderEmailForHolds.class);

	/**
	 * This custom implementation is used for validating wholesale order holds
	 * which throw alert exception & emails wholesale order.
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	public Document validateWholesaleOrderHolds(YFSEnvironment env, Document inXML) throws Exception {

		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIWholesaleOrderEmailForHolds================================");
		}

		Element eleOrderHoldType = inXML.getDocumentElement();
		String strOrderHeaderKey = SCXmlUtil.getAttribute(eleOrderHoldType, ATTR_ORDER_HEADER_KEY);
		Element eleOrder = SCXmlUtil.getChildElement(eleOrderHoldType, ELE_ORDER);
		String strOrderNo = "";
		String strEnterpriseCode = "";
		String strDocumentType = "";

		if (!YFCObject.isVoid(eleOrder)) {

			strOrderNo = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_NO);
			strEnterpriseCode = SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE);
			strDocumentType = SCXmlUtil.getAttribute(eleOrder, ATTR_DOCUMENT_TYPE);
		}

		String strHoldType = SCXmlUtil.getAttribute(eleOrderHoldType, ATTR_HOLD_TYPE);
		String strStatus = SCXmlUtil.getAttribute(eleOrderHoldType, ATTR_STATUS);

		if (strHoldType.equals("VSI_WH_INV_ITEM_HOLD") && strStatus.equals(STATUS_CREATE)) {

			createInvalidItemAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode, strDocumentType);
		}

		if (strHoldType.equals("VSI_WH_PRI_DISC_HOLD") && strStatus.equals(STATUS_CREATE)) {

			createPriceDiscrepancyAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode, strDocumentType);
		}

		if (strHoldType.equals("VSI_WH_INVALID_ADDR") && strStatus.equals(STATUS_CREATE)) {

			createInvalidAddressAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode, strDocumentType);
		}
		if(strHoldType.equals(ATTR_VSI_WH_CRDT_DIS_HOLD) && strStatus.equals(STATUS_CREATE)){
			if(log.isDebugEnabled()){
				log.debug("VSIWholesaleOrderEmailForHolds.validateWholesaleOrderHolds: Calling credit discrepancy alert");
			}
			createCreditDiscrepancyAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode, strDocumentType);
		}

		if (strHoldType.equals("VSI_WH_ORD_VERFY_HLD") && strStatus.equals(STATUS_CREATE)) {

			createOrderVerificationAlert(env, strOrderHeaderKey, strOrderNo, strEnterpriseCode, strDocumentType);
		}
		return inXML;

	}

	private void createInvalidAddressAlert(YFSEnvironment env, String strOrderHeaderKey, String strOrderNo,
			String strEnterpriseCode, String strDocumentType) throws Exception {
		// TODO Auto-generated method stub

		String strAlertDescription = "";
		Document invalidAddressXML = null;
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_INVALID_ADDRESS_SB"))) {

			StringBuffer sbInvalidItemAlertDesc = (StringBuffer) env.getTxnObject("VSI_WH_INVALID_ADDRESS_SB");

			strAlertDescription = sbInvalidItemAlertDesc.toString();
		}
		
		if(!YFCObject.isVoid(env.getTxnObject("VSI_WH_INVALID_ADDRESS_XML"))){
			
			invalidAddressXML = (Document) env.getTxnObject("VSI_WH_INVALID_ADDRESS_XML");
		}

		Document invalidAddressAlertInXML = createAlertInputXML(strOrderHeaderKey, strOrderNo, strEnterpriseCode,
				strDocumentType, "VSI_WH_INVALID_ADDRESS_ALERT", strAlertDescription);

		Document createAlertOutXML = VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				invalidAddressAlertInXML);
		//SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_DETAIL_DESCRIPTION, strAlertDescription);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		if(!YFCObject.isVoid(invalidAddressXML)){
			
			SCXmlUtil.importElement(createAlertOutXML.getDocumentElement(), invalidAddressXML.getDocumentElement());
		}
		
		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){
			
			try{
				VSIUtils.invokeService(env, "VSIWholesaleOrderInvalidAddressEmail", createAlertOutXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in validateWholesaleOrderHolds.createInvalidAddressAlert() : " + e.getMessage());
				e.printStackTrace();
			}
			if(emailSent){
				break;
			}
		}
	}

	private void createPriceDiscrepancyAlert(YFSEnvironment env, String strOrderHeaderKey, String strOrderNo,
			String strEnterpriseCode, String strDocumentType) throws Exception {
		// TODO Auto-generated method stub

		String strAlertDescription = "";
		Document priceHoldDescriptionXML = null;
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_SB"))) {

			StringBuffer sbInvalidItemAlertDesc = (StringBuffer) env.getTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_SB");

			strAlertDescription = sbInvalidItemAlertDesc.toString();
		}
		
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_XML"))) {

			priceHoldDescriptionXML = (Document) env.getTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_XML");
			
		}

		Document priceDiscrepancyAlertInXML = createAlertInputXML(strOrderHeaderKey, strOrderNo, strEnterpriseCode,
				strDocumentType, "VSI_WH_PRICE_DISCREPANCY_ALERT", strAlertDescription);

		Document createAlertOutXML = VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				priceDiscrepancyAlertInXML);
		//SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_DETAIL_DESCRIPTION, strAlertDescription);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		if(!YFCObject.isVoid(priceHoldDescriptionXML)){
			
			SCXmlUtil.importElement(createAlertOutXML.getDocumentElement(), priceHoldDescriptionXML.getDocumentElement());
		}
		
		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){
			
			try{
				VSIUtils.invokeService(env, "VSIWholesaleOrderPriceDiscEmail", createAlertOutXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in validateWholesaleOrderHolds.createPriceDiscrepancyAlert() : " + e.getMessage());
				e.printStackTrace();
			}
			if(emailSent){
				break;
			}
		}
	}

	private void createInvalidItemAlert(YFSEnvironment env, String strOrderHeaderKey, String strOrderNo,
			String strEnterpriseCode, String strDocumentType) throws Exception {
		// TODO Auto-generated method stub

		String strAlertDescription = "";
		Document itemHoldDescriptionXML = null;
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_SB"))) {

			StringBuffer sbInvalidItemAlertDesc = (StringBuffer) env.getTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_SB");

			strAlertDescription = sbInvalidItemAlertDesc.toString();
		}
		
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_XML"))) {

			itemHoldDescriptionXML = (Document) env.getTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_XML");
		}

		Document invalidItemAlertInXML = createAlertInputXML(strOrderHeaderKey, strOrderNo, strEnterpriseCode,
				strDocumentType, "VSI_WH_INVALID_ITEM_ALERT", strAlertDescription);

		Document createAlertOutXML = VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION, invalidItemAlertInXML);
		//SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_DETAIL_DESCRIPTION, strAlertDescription);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		if(!YFCObject.isVoid(itemHoldDescriptionXML)){
			
			SCXmlUtil.importElement(createAlertOutXML.getDocumentElement(), itemHoldDescriptionXML.getDocumentElement());
		}
		
		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){
			
			try{
				VSIUtils.invokeService(env, "VSIWholesaleOrderInvalidItemEmail", createAlertOutXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in validateWholesaleOrderHolds.createInvalidItemAlert() : " + e.getMessage());
				e.printStackTrace();
			}
			if(emailSent){
				break;
			}
		}
	}

	private Document createAlertInputXML(String strOrderHeaderKey, String strOrderNo, String strEnterpriseCode,
			String strDocumentType, String strExceptionType, String strAlertDescription) {

		String detailDescSuffix = " Record length exceeded. Please refer to the alert email for the complete details.";
		Document createExceptionInXML = SCXmlUtil.createDocument(ELE_INBOX);
		Element eleInbox = createExceptionInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleInbox, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleInbox, ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(eleInbox, ATTR_EXCEPTION_TYPE, strExceptionType);
		if(strAlertDescription.length() > 3900)
			SCXmlUtil.setAttribute(eleInbox, ATTR_DETAIL_DESCRIPTION, strAlertDescription.substring(0, 3899).concat(detailDescSuffix));
		else
			SCXmlUtil.setAttribute(eleInbox, ATTR_DETAIL_DESCRIPTION, strAlertDescription);
		
		SCXmlUtil.setAttribute(eleInbox, ATTR_QUEUE_ID, "VSI_WHOLESALE_ALERT_Q");

		Element eleOrder = SCXmlUtil.createChild(eleInbox, ELE_ORDER);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(eleOrder, ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleOrder, ATTR_DOCUMENT_TYPE, strDocumentType);
		return createExceptionInXML;
	}

	private void createCreditDiscrepancyAlert(YFSEnvironment env, String strOrderHeaderKey, String strOrderNo,
			String strEnterpriseCode, String strDocumentType) throws Exception {
		// TODO Auto-generated method stub
		log.beginTimer("validateWholesaleOrderHolds.createCreditDiscrepancyAlert");
		String strAlertDescription = "";
		Document creditDiscrepancyXML = null;
		if (!YFCObject.isVoid(env.getTxnObject(ATTR_VSI_WH_CRDT_DIS_HOLD))) {

			StringBuffer sbCreditDiscrepancyDesc = (StringBuffer) env.getTxnObject(ATTR_VSI_WH_CRDT_DIS_HOLD);

			strAlertDescription = sbCreditDiscrepancyDesc.toString();
		}
		
		if (!YFCObject.isVoid(env.getTxnObject("VSI_WH_CRDT_DIS_HOLD_XML"))) {

			creditDiscrepancyXML = (Document) env.getTxnObject("VSI_WH_CRDT_DIS_HOLD_XML");
		}

		Document creditDiscrepancyAlertInXML = createAlertInputXML(strOrderHeaderKey, strOrderNo, strEnterpriseCode,
				strDocumentType, "VSI_WH_CREDIT_DISCREPANCY_ALERT", strAlertDescription);

		Document createAlertOutXML = VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				creditDiscrepancyAlertInXML);
		//SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_DETAIL_DESCRIPTION, strAlertDescription);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ORDER_NO, strOrderNo);
		SCXmlUtil.setAttribute(createAlertOutXML.getDocumentElement(), ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		if(!YFCObject.isVoid(creditDiscrepancyXML)){
			
			SCXmlUtil.importElement(createAlertOutXML.getDocumentElement(), creditDiscrepancyXML.getDocumentElement());
		}
		
		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){
			
			try{
				VSIUtils.invokeService(env, SERVICE_CREDIT_DISCREPANCY_EMAIL, createAlertOutXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in validateWholesaleOrderHolds.createCreditDiscrepancyAlert() : " + e.getMessage());
				e.printStackTrace();
			}			
			if(emailSent){
				break;
			}
		}
		
		log.endTimer("validateWholesaleOrderHolds.createCreditDiscrepancyAlert");
	}

	private void createOrderVerificationAlert(YFSEnvironment env, String strOrderHeaderKey, String strOrderNo,
			String strEnterpriseCode, String strDocumentType) throws Exception {

		if(YFCObject.isVoid(strOrderHeaderKey)){

			if(!YFCObject.isVoid(strOrderNo)){

				Document getOrderListInXML = SCXmlUtil.createDocument(ELE_ORDER);
				Element eleOrder = getOrderListInXML.getDocumentElement();
				SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_NO, strOrderNo);
				SCXmlUtil.setAttribute(eleOrder, ATTR_ENTERPRISE_CODE, strEnterpriseCode);
				SCXmlUtil.setAttribute(eleOrder, ATTR_DOCUMENT_TYPE, ATTR_DOCUMENT_TYPE_SALES);

				Document getOrderListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST, getOrderListInXML);
				Element eleOrderList = getOrderListOutXML.getDocumentElement();
				if(eleOrderList.hasChildNodes()){
					
					eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
					strOrderHeaderKey = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_HEADER_KEY);
				}
			}
		}

		Document orderVerificationAlertInXML = createAlertInputXML(strOrderHeaderKey, strOrderNo, strEnterpriseCode,
				strDocumentType, "VSI_WH_ORDER_VERIFY_ALERT", "Wholesale order no. " + strOrderNo + " has been created in OMS. Please verify.");

		Document createAlertOutXML = VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				orderVerificationAlertInXML);

		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){

			try{
				VSIUtils.invokeService(env, "VSIWholesaleOrderVerificationEmail", createAlertOutXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in validateWholesaleOrderHolds.createOrderVerificationAlert() : " + e.getMessage());
				e.printStackTrace();
			}
			if(emailSent){
				break;
			}
		}
	}

}