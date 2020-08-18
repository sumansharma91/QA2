package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.shipment.api.VSIUpdateTrailerInInvoice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleSendInvoice implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIUpdateTrailerInInvoice.class);

	public Document sendInvoiceInformation(YFSEnvironment env, Document inXML) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Input XMl in class VSIWholesaleSendInvoice :" + XMLUtil.getXMLString(inXML));
		}

		Document docOutputOrderInvoiceList = XMLUtil.createDocument(ELE_INVOICE_DETAIL_LIST);

		Element eleInDoc = inXML.getDocumentElement();
		String strEnterpriseCode = SCXmlUtil.getXpathAttribute(eleInDoc,
				"/InvoiceDetail/InvoiceHeader/Order/@EnterpriseCode");
		String strInvoiceType = SCXmlUtil.getXpathAttribute(eleInDoc, "/InvoiceDetail/InvoiceHeader/@InvoiceType");

		// If EnterpriseCode not null
		if (!YFCCommon.isVoid(strEnterpriseCode)) {

			// Get Remit AddrressType Element Information for Enterprise
			Element eledocPersonInfoRemit = getRemtiPersonInfo(env, strEnterpriseCode);

			// If Not credit or debit memo, form the correct XML.
			if(!strInvoiceType.equalsIgnoreCase(CREDIT_MEMO) && !strInvoiceType.equalsIgnoreCase(DEBIT_MEMO)){
			// Check CommonCode Value for Enterprise Under WholeSale Sending
			// Invoice Mode
			String sendingInvoiceMode = getSendInoiveMode(env, strEnterpriseCode);

			// If Shipment Level Mode for sending Invoice
			if (!YFCCommon.isVoid(sendingInvoiceMode)
					&& sendingInvoiceMode.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_SHIP)) {

				// Get OrderInvoiceKey
				String strOrderInvoiceKey = SCXmlUtil.getXpathAttribute(eleInDoc,
						"/InvoiceDetail/InvoiceHeader/@OrderInvoiceKey");

				if (!YFCCommon.isVoid(strOrderInvoiceKey)) {
					docOutputOrderInvoiceList = getdocOutputOrderInvoiceListShip(env, strOrderInvoiceKey);

				}

			}

			// If Order Level Mode for sending Invoice
			if (!YFCCommon.isVoid(sendingInvoiceMode)
					&& sendingInvoiceMode.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_ORDER)) {

				// Send Shipment for Order level only for Last Shipment
				String strOrderHeaderKey = SCXmlUtil.getXpathAttribute(eleInDoc,
						"/InvoiceDetail/InvoiceHeader/Order/@OrderHeaderKey");
				String strShipmentKeyIn = SCXmlUtil.getXpathAttribute(eleInDoc,
						"/InvoiceDetail/InvoiceHeader/Shipment/@ShipmentKey");
				String strIsLastInvoiceForOrder = SCXmlUtil.getXpathAttribute(eleInDoc, "/InvoiceDetail/InvoiceHeader/Extn/@ExtnIsLastInvoiceForOrder");

				if (strIsLastInvoiceForOrder.equalsIgnoreCase(FLAG_Y)) {

					docOutputOrderInvoiceList = getdocOutputOrderInvoiceListOrder(env, strOrderHeaderKey,
							strShipmentKeyIn);
				}
			}

			// If Trailer Level Mode for sending Invoice
			if (!YFCCommon.isVoid(sendingInvoiceMode)
					&& sendingInvoiceMode.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_TRAILER)) {
				// send Trailer level Invoice consisting of all shipments in
				// trailer

				String strSendTrailerInvoice = SCXmlUtil.getXpathAttribute(eleInDoc,
						"/InvoiceDetail/InvoiceHeader/Extn/@ExtnSendTrailerInvoice");
				String strTrailerNo = SCXmlUtil.getXpathAttribute(eleInDoc,
						"/InvoiceDetail/InvoiceHeader/Extn/@ExtnTrailerNo");

				if (!YFCCommon.isVoid(strTrailerNo)) {

					if (!YFCCommon.isVoid(strSendTrailerInvoice) && strSendTrailerInvoice.equalsIgnoreCase(FLAG_Y)) {
						// call getOrderInvoiceDetailList with TrailerNo and
						// send Invoice
						docOutputOrderInvoiceList = getdocOutputOrderInvoiceListTrailer(env, strSendTrailerInvoice,
								strTrailerNo);

					}

				}

			}
		}else{
			// For credit and debit memos, create the output XML
			String strOrderInvoiceKey = SCXmlUtil.getXpathAttribute(eleInDoc,
					"/InvoiceDetail/InvoiceHeader/@OrderInvoiceKey");
			docOutputOrderInvoiceList = getdocOutputOrderInvoiceListShip(env, strOrderInvoiceKey);
		}

			// if OrderInvoiceDtlList is not null Udpate Credit Availed and
			// RemitPersonInfo
			if (!YFCCommon.isVoid(docOutputOrderInvoiceList)
					&& docOutputOrderInvoiceList.getDocumentElement().hasChildNodes()) {

				// Update customer Credit Details and Update Credit Availed
				if(!strInvoiceType.equalsIgnoreCase(CREDIT_MEMO) && !strInvoiceType.equalsIgnoreCase(DEBIT_MEMO)){
					udpateCustCreditDtl(env, strEnterpriseCode, docOutputOrderInvoiceList, false);
				}else{
					udpateCustCreditDtl(env, strEnterpriseCode, docOutputOrderInvoiceList, true);
				}

				// Update RemitPersonInfo Under Element Order
				NodeList nlOrder = docOutputOrderInvoiceList.getElementsByTagName(ELE_ORDER);

				for (int i = 0; i < nlOrder.getLength(); i++) {
					Element eleOrder = (Element) nlOrder.item(i);
					SCXmlUtil.importElement(eleOrder, eledocPersonInfoRemit);
				}

				// Update SendInvoice="Y" Flag in the Output
				docOutputOrderInvoiceList.getDocumentElement().setAttribute(ATTR_SEND_INVOICE, "Y");

				if (log.isDebugEnabled()) {
					log.debug("Ouput XML docOutputOrderInvoiceList in class VSIWholesaleSendInvoice"
							+ XMLUtil.getXMLString(docOutputOrderInvoiceList));
				}

			}

		}

		return docOutputOrderInvoiceList;

	}

	// Get OrderInvoiceListDtl For Trailer
	private Document getdocOutputOrderInvoiceListTrailer(YFSEnvironment env, String strSendTrailerInvoice,
			String strTrailerNo) throws Exception {

		Document docOrderInvoiveDtlOut = XMLUtil.createDocument(ELE_INVOICE_DETAIL_LIST);

		Document getOrderInvoiceListInXML = SCXmlUtil.createDocument(ELE_ORDER_INVOICE);
		Element eleOrderInvoice = getOrderInvoiceListInXML.getDocumentElement();
		Element eleExtn = SCXmlUtil.createChild(eleOrderInvoice, ELE_EXTN);
		SCXmlUtil.setAttribute(eleExtn, ATTR_EXTN_TRAILER_NO, strTrailerNo);
		SCXmlUtil.setAttribute(eleOrderInvoice, ATTR_INVOICE_TYPE, INVOICE_TYPE_SHIPMENT);
		
		Document getOrderInvoiceListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_LIST, getOrderInvoiceListInXML);
		NodeList nlOrderInvoice = getOrderInvoiceListOutXML.getElementsByTagName(ELE_ORDER_INVOICE);
		for(int j = 0; j < nlOrderInvoice.getLength(); j++){
			
			eleOrderInvoice = (Element) nlOrderInvoice.item(j);
			String orderInvoiceKey = SCXmlUtil.getAttribute(eleOrderInvoice, ATTR_ORDER_INVOICE_KEY);
			Document getOrderInvoiceDetailsInXML = SCXmlUtil.createDocument(ELE_GET_ORDER_INVOICE_DTLS);
			Element eleGetOrderInvoiceDetails = getOrderInvoiceDetailsInXML.getDocumentElement();
			SCXmlUtil.setAttribute(eleGetOrderInvoiceDetails, ATTR_INVOICE_KEY, orderInvoiceKey);
			Document getOrderInvoiceDetailsOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_DETAILS, getOrderInvoiceDetailsInXML);
			SCXmlUtil.importElement(docOrderInvoiveDtlOut.getDocumentElement(), getOrderInvoiceDetailsOutXML.getDocumentElement());
		}
		

		return docOrderInvoiveDtlOut;
	}

	// Get OrderInvoiceListDtl For Order
	private Document getdocOutputOrderInvoiceListOrder(YFSEnvironment env, String strOrderHeaderKey,
			String strShipmentKeyIn) throws Exception {

		Document docOrderInvoiveDtlOut = XMLUtil.createDocument(ELE_INVOICE_DETAIL_LIST);
		Document getOrderInvoiceListInXML = SCXmlUtil.createDocument(ELE_ORDER_INVOICE);
		Element eleOrderInvoice = getOrderInvoiceListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleOrderInvoice, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		SCXmlUtil.setAttribute(eleOrderInvoice, ATTR_INVOICE_TYPE, INVOICE_TYPE_SHIPMENT);

		Document getOrderInvoiceListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_LIST, getOrderInvoiceListInXML);

		NodeList nlOrderInvoice = getOrderInvoiceListOutXML.getElementsByTagName(ELE_ORDER_INVOICE);
		for(int j = 0; j < nlOrderInvoice.getLength(); j++){

			eleOrderInvoice = (Element) nlOrderInvoice.item(j);
			String orderInvoiceKey = SCXmlUtil.getAttribute(eleOrderInvoice, ATTR_ORDER_INVOICE_KEY);
			Document getOrderInvoiceDetailsInXML = SCXmlUtil.createDocument(ELE_GET_ORDER_INVOICE_DTLS);
			Element eleGetOrderInvoiceDetails = getOrderInvoiceDetailsInXML.getDocumentElement();
			SCXmlUtil.setAttribute(eleGetOrderInvoiceDetails, ATTR_INVOICE_KEY, orderInvoiceKey);
			Document getOrderInvoiceDetailsOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_DETAILS, getOrderInvoiceDetailsInXML);
			SCXmlUtil.importElement(docOrderInvoiveDtlOut.getDocumentElement(), getOrderInvoiceDetailsOutXML.getDocumentElement());
		}



		return docOrderInvoiveDtlOut;
	}

	// Get OrderInvoiceListDtl For Shipment
	private Document getdocOutputOrderInvoiceListShip(YFSEnvironment env, String strOrderInvoiceKey) throws Exception {

		Document docOrderInvoiveDtlOut = XMLUtil.createDocument(ELE_INVOICE_DETAIL_LIST);

		Document docOrderInvoiceDetailListIn = XMLUtil.createDocument(ELE_GET_ORDER_INVOICE_DTLS);
		Element eleOrderInvoiceDetailListIn = docOrderInvoiceDetailListIn.getDocumentElement();
		//Element eleInvoiceHeader = SCXmlUtil.createChild(eleOrderInvoiceDetailListIn, ELE_INVOICE_HEADER);
		eleOrderInvoiceDetailListIn.setAttribute(ATTR_INVOICE_KEY, strOrderInvoiceKey);

		if (log.isDebugEnabled()) {
			log.debug("Input to getOrderInvoiceDetailList in class VSIWholesaleSendInvoice"
					+ XMLUtil.getXMLString(docOrderInvoiceDetailListIn));
		}

		Document getOrderInvoiceDetailsOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_DETAILS,
				docOrderInvoiceDetailListIn);
		
		SCXmlUtil.importElement(docOrderInvoiveDtlOut.getDocumentElement(), getOrderInvoiceDetailsOutXML.getDocumentElement());

		if (log.isDebugEnabled()) {
			log.debug("Output to getOrderInvoiceDetailList in class VSIWholesaleSendInvoice"
					+ XMLUtil.getXMLString(docOrderInvoiveDtlOut));
		}

		return docOrderInvoiveDtlOut;
	}

	// Get SendInvoiceMode from Common Code
	private String getSendInoiveMode(YFSEnvironment env, String strEnterpriseCode) throws Exception {

		String strCodeShortDesc = "";
		Element eleCommonCode = (Element) (VSIUtils
				.getCommonCodeList(env, VSI_WH_SEND_INV_MODE, strEnterpriseCode, ATTR_DEFAULT).get(0));

		if (!YFCCommon.isVoid(eleCommonCode)) {

			strCodeShortDesc = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
			if(log.isDebugEnabled()){
				log.debug("VSIWholesaleSendInvoice.evaluateCondition: CodeShortdesc is " + strCodeShortDesc);
			}
		}

		return strCodeShortDesc;
	}

	// Get RemitPersonInfo Element
	public Element getRemtiPersonInfo(YFSEnvironment env, String strEnterpriseCode) throws Exception {

		Element eledocPersonInfoRemit = null;

		// Create Input for VSIGetDetailsForWholesaleCustomer
		Document getCustomerListInXML = XMLUtil.createDocument(ELE_CUSTOMER);
		Element eleCustomer = getCustomerListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_TYPE, "01");
		SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_ID, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleCustomer, ATTR_ORGANIZATION_CODE, strEnterpriseCode);

		if (log.isDebugEnabled()) {
			log.debug("Input XMl for VSIGetDetailsForWholesaleCustomer Service in class VSIWholesaleSendInvoice :"
					+ XMLUtil.getXMLString(getCustomerListInXML));
		}

		// Call VSIGetDetailsForWholesaleCustomer service
		Document getCustomerListOutXML = VSIUtils.invokeService(env, SERVICE_GET_WHOLESALE_CUSTOMER_DETAILS,
				getCustomerListInXML);

		if (log.isDebugEnabled()) {
			log.debug("Output XMl for VSIGetDetailsForWholesaleCustomer Service in class VSIWholesaleSendInvoice :"
					+ XMLUtil.getXMLString(getCustomerListOutXML));
		}

		if (!YFCCommon.isVoid(getCustomerListOutXML) && getCustomerListOutXML.getDocumentElement().hasChildNodes()) {

			// Get Addrees Type Remit
			NodeList nlCustAddAddress = getCustomerListOutXML.getElementsByTagName(ELE_CUST_ADDITIONAL_ADDRESS);
			for (int i = 0; i < nlCustAddAddress.getLength(); i++) {

				Element eleCustAddAddress = (Element) nlCustAddAddress.item(i);
				String strAddressType = SCXmlUtil.getAttribute(eleCustAddAddress, ATTR_ADDR_TYPE);
				if(log.isDebugEnabled()){
					log.debug("VSIWholesaleSendInvoice strAddressType is " + strAddressType);
				}

				if (!YFCCommon.isVoid(strAddressType) && strAddressType.equalsIgnoreCase(ATTR_REMIT)) {
					Element elePersoninfo = (Element) eleCustAddAddress.getElementsByTagName(ELE_PERSON_INFO).item(0);
					Document docPersonInfoRemit = XMLUtil.createDocument(ELE_PERSON_INFO_REMIT_TO);
					eledocPersonInfoRemit = docPersonInfoRemit.getDocumentElement();

					eledocPersonInfoRemit.setAttribute(ATTR_ZIPCODE, elePersoninfo.getAttribute(ATTR_ZIPCODE));
					eledocPersonInfoRemit.setAttribute(ATTR_STATE, elePersoninfo.getAttribute(ATTR_STATE));
					eledocPersonInfoRemit.setAttribute(ATTR_PREFERRED_SHIP_ADD,
							elePersoninfo.getAttribute(ATTR_PREFERRED_SHIP_ADD));
					eledocPersonInfoRemit.setAttribute(ATTR_LAST_NAME, elePersoninfo.getAttribute(ATTR_LAST_NAME));
					eledocPersonInfoRemit.setAttribute(ATTR_FIRST_NAME, elePersoninfo.getAttribute(ATTR_FIRST_NAME));
					eledocPersonInfoRemit.setAttribute(ATTR_EMAIL_ID, elePersoninfo.getAttribute(ATTR_EMAIL_ID));
					eledocPersonInfoRemit.setAttribute(ATTR_DAY_PHONE, elePersoninfo.getAttribute(ATTR_DAY_PHONE));
					eledocPersonInfoRemit.setAttribute(ATTR_COUNTRY, elePersoninfo.getAttribute(ATTR_COUNTRY));
					eledocPersonInfoRemit.setAttribute(ATTR_CITY, elePersoninfo.getAttribute(ATTR_CITY));
					eledocPersonInfoRemit.setAttribute(ATTR_ADDRESS1, elePersoninfo.getAttribute(ATTR_ADDRESS1));
					eledocPersonInfoRemit.setAttribute(ATTR_ADDRESS2, elePersoninfo.getAttribute(ATTR_ADDRESS2));
					eledocPersonInfoRemit.setAttribute(ATTR_ADDRESS3, elePersoninfo.getAttribute(ATTR_ADDRESS3));
					eledocPersonInfoRemit.setAttribute(ATTR_ADDRESS6, elePersoninfo.getAttribute(ATTR_ADDRESS6));
					eledocPersonInfoRemit.setAttribute(ATTR_ADDR_ID, elePersoninfo.getAttribute(ATTR_ADDR_ID));

					if (log.isDebugEnabled()) {
						log.debug("RemitPersoninfo XMl in class VSIWholesaleSendInvoice :"
								+ XMLUtil.getXMLString(docPersonInfoRemit));
					}
				}

			}
		}

		return eledocPersonInfoRemit;

	}

	// Update CustomerCreditDetails
	public void udpateCustCreditDtl(YFSEnvironment env, String strEnterpriseCode, Document docOutputOrderInvoiceList, Boolean isMemo)
			throws Exception {

		// Element eleOrderInvDtl = docOrderInvoiveDtlOut.getDocumentElement();
		NodeList nlInovoiceHeader = docOutputOrderInvoiceList.getElementsByTagName(ELE_INVOICE_HEADER);
		String strDocumentType = SCXmlUtil.getXpathAttribute(docOutputOrderInvoiceList.getDocumentElement(), "/InvoiceDetailList/InvoiceDetail/InvoiceHeader/Order/@DocumentType");

		Element eleInvoiceHeader = null;
		Double dTotalAmt = 0.00;
		Double dFinalTotal = 0.00;

		for (int i = 0; i < nlInovoiceHeader.getLength(); i++) {
			eleInvoiceHeader = (Element) nlInovoiceHeader.item(i);
			dTotalAmt = Double.valueOf(SCXmlUtil.getAttribute(eleInvoiceHeader, ATTR_TOTAL_AMOUNT));
			dFinalTotal = dTotalAmt + dFinalTotal;
			
			if(eleInvoiceHeader.hasAttribute(ELE_HEADER_CHARGES)){
				eleInvoiceHeader.removeAttribute(ELE_HEADER_CHARGES);
			}
		}

		if (!YFCCommon.isVoid(strEnterpriseCode)) {
			Document docInputCustCreditDtls = XMLUtil.createDocument(ELE_VSI_WH_CUST_CREDIT_DETAILS);
			Element eleCustCreditDtls = docInputCustCreditDtls.getDocumentElement();
			eleCustCreditDtls.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);

			if (log.isDebugEnabled()) {
				log.debug("Input to service VSIWholesaleGetCustCreditDetailsList:"
						+ XMLUtil.getXMLString(docInputCustCreditDtls));
			}

			Document docOutputCustCreditDtls = VSIUtils.invokeService(env, SERVICE_GET_CUST_CREDIT_DTLS_LIST,
					docInputCustCreditDtls);
			if (log.isDebugEnabled()) {
				log.debug("Output from service VSIWholesaleGetCustCreditDetailsList:"
						+ XMLUtil.getXMLString(docOutputCustCreditDtls));
			}

			if (!YFCCommon.isVoid(docOutputCustCreditDtls)
					&& docOutputCustCreditDtls.getDocumentElement().hasChildNodes()) {
				eleCustCreditDtls = (Element) docOutputCustCreditDtls
						.getElementsByTagName(ELE_VSI_WH_CUST_CREDIT_DETAILS).item(0);
				
				docOutputOrderInvoiceList.getDocumentElement().appendChild(docOutputOrderInvoiceList.importNode(eleCustCreditDtls.cloneNode(true), true));
				
				// Credit Availed will not be updated for credit and debit memos
				if(isMemo){
					return;
				}
				
				Double dCreditAvailed = Double.valueOf(SCXmlUtil.getAttribute(eleCustCreditDtls, ATTR_CREDIT_AVAILED));
				if(!YFCCommon.isVoid(strDocumentType) && strDocumentType.equals(ATTR_DOCUMENT_TYPE_SALES)){
				dCreditAvailed = dCreditAvailed - dFinalTotal;
				}
				
				else if(!YFCCommon.isVoid(strDocumentType) && strDocumentType.equals(RETURN_DOCUMENT_TYPE)){
					dCreditAvailed = dCreditAvailed + dFinalTotal;
					}
				
				if(log.isDebugEnabled()){
					log.debug("udpateCustCreditDetl: - New credit Availed amount is" + dCreditAvailed.toString());
				}
				Document docUpdateCustCreditDtls = XMLUtil.createDocument(ELE_VSI_WH_CUST_CREDIT_DETAILS);
				Element eleUpdateCustCreditDtls = docUpdateCustCreditDtls.getDocumentElement();
				eleUpdateCustCreditDtls.setAttribute(ATTR_CREDIT_AVAILED, dCreditAvailed.toString());
				eleUpdateCustCreditDtls.setAttribute(ATTR_CUST_CREDIT_DTLS_KEY,
						eleCustCreditDtls.getAttribute(ATTR_CUST_CREDIT_DTLS_KEY));
				eleUpdateCustCreditDtls.setAttribute(ATTR_ENTERPRISE_CODE, strEnterpriseCode);
				VSIUtils.invokeService(env, SERVICE_UPDATE_CUST_CREDIT_DTLS, docUpdateCustCreditDtls);
			}

		}

	}

}