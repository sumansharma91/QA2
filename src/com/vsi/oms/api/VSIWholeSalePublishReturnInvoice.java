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

public class VSIWholeSalePublishReturnInvoice implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIWholeSalePublishReturnInvoice.class);

	public Document publishReturnInvoice(YFSEnvironment env, Document inXML) throws Exception
	{
		if (log.isDebugEnabled()) {
			log.debug("Input XMl in class VSIWholeSalePublishReturnInvoice :" + XMLUtil.getXMLString(inXML));
		}
		
		VSIWholesaleSendInvoice impl = new VSIWholesaleSendInvoice();
		
		

		Document docOrderInvoiveDtlOut = XMLUtil.createDocument(ELE_INVOICE_DETAIL_LIST);
		String strOrderInvoiceKey = SCXmlUtil.getXpathAttribute(inXML.getDocumentElement(), "/InvoiceDetail/InvoiceHeader/@OrderInvoiceKey");
		String strEnterpriseCode = SCXmlUtil.getXpathAttribute(inXML.getDocumentElement(), "/InvoiceDetail/InvoiceHeader/Order/@EnterpriseCode");
		Element eledocPersonInfoRemit = impl.getRemtiPersonInfo(env, strEnterpriseCode);
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
		
		String strDerivedFromOrderHeaderKey = "";
		NodeList nlLineDetail = getOrderInvoiceDetailsOutXML.getElementsByTagName(ELE_LINE_DETAIL);
		
		for(int i = 0; i < nlLineDetail.getLength(); i++){
			
			Element eleLineDetail = (Element) nlLineDetail.item(i);
			Element eleInputOrderLine = SCXmlUtil.getChildElement(eleLineDetail, ELE_ORDER_LINE);
			if(!YFCObject.isVoid(eleInputOrderLine)){
				
				strDerivedFromOrderHeaderKey = SCXmlUtil.getAttribute(eleInputOrderLine, ATTR_DERIVED_FROM_ORDER_HEADER_KEY);
				if(!YFCObject.isVoid(strDerivedFromOrderHeaderKey)){
					break;
				}
			}
		}

		if(!YFCObject.isVoid(strDerivedFromOrderHeaderKey)){
			
			Document getOrderReleaseListInXML = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
			Element eleOrderRelease = getOrderReleaseListInXML.getDocumentElement();
			SCXmlUtil.setAttribute(eleOrderRelease, ATTR_ORDER_HEADER_KEY, strDerivedFromOrderHeaderKey);
			
			Document getOrderReleaseListOutXML = 
					VSIUtils.invokeService(env, "VSIWholesaleGetSalesOrderReleaseListForReturn", getOrderReleaseListInXML);
			
			Element eleInvoiceHeader = SCXmlUtil.getXpathElement(
					getOrderInvoiceDetailsOutXML.getDocumentElement(), "//InvoiceHeader");
			if(!YFCObject.isVoid(eleInvoiceHeader)){
				SCXmlUtil.importElement(eleInvoiceHeader, getOrderReleaseListOutXML.getDocumentElement());
			}
		}

		// if OrderInvoiceDtlList is not null Udpate Credit Availed and
		// RemitPersonInfo
		if (!YFCCommon.isVoid(getOrderInvoiceDetailsOutXML)
				&& getOrderInvoiceDetailsOutXML.getDocumentElement().hasChildNodes()) {
			
			SCXmlUtil.importElement(docOrderInvoiveDtlOut.getDocumentElement(), getOrderInvoiceDetailsOutXML.getDocumentElement());

			// Update customer Credit Details and Update Credit Availed
			impl.udpateCustCreditDtl(env, strEnterpriseCode, docOrderInvoiveDtlOut, false);

			// Update RemitPersonInfo Under Element Order
			NodeList nlOrder = docOrderInvoiveDtlOut.getElementsByTagName(ELE_ORDER);

			for (int i = 0; i < nlOrder.getLength(); i++) {
				Element eleOrder = (Element) nlOrder.item(i);
				SCXmlUtil.importElement(eleOrder, eledocPersonInfoRemit);
			}

			// Update SendInvoice="Y" Flag in the Output
			docOrderInvoiveDtlOut.getDocumentElement().setAttribute(ATTR_SEND_INVOICE, "Y");

			if (log.isDebugEnabled()) {
				log.debug("Ouput XML docOutputOrderInvoiceList in class VSIWholesaleSendInvoice"
						+ XMLUtil.getXMLString(docOrderInvoiveDtlOut));
			}

		}

		if (log.isDebugEnabled()) {
			log.debug("Output to getOrderInvoiceDetailList in class VSIWholeSalePublishReturnInvoice"
					+ XMLUtil.getXMLString(docOrderInvoiveDtlOut));
		}

		return docOrderInvoiveDtlOut;


	}

}