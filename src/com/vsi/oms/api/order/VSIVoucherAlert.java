package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**********************************************************************************
 * File Name : VSIVoucherAlert.java
 * 
 * Description : The Code will check if the OrderLine is in Canceled status and
 * PaymentType="VOUCHERS" then raise an alert
 * 
 * 
 * 
 * Modification Log :
 * ----------------------------------------------------------------------- Ver #
 * Date Author Modification
 * ----------------------------------------------------------------------- 0.00a
 * 01/10/2014 The Code will check if the Order is in Canceled status and
 * PaymentType="VOUCHERS" then raise an alert
 **********************************************************************************/
public class VSIVoucherAlert {

	private YFCLogCategory log = YFCLogCategory.instance(VSIVoucherAlert.class);
	YIFApi api;

	public void vsiVoucherAlert(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIVoucherAlert================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		Element rootElement = inXML.getDocumentElement();

		String sOrderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
		String sOrderNo = rootElement.getAttribute("OrderNo");

		Element eleInputOrderLines = XMLUtil.getElementByXPath(inXML,
				"Order/OrderLines");
		NodeList nlInputOrderLines = eleInputOrderLines
				.getElementsByTagName("OrderLine");
		int iInputOrderLinesLength = nlInputOrderLines.getLength();

		// Calling getOrderList API for getting the number of OrderLines in the
		// Order

		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListInput.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				sOrderHeaderKey);
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		//eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, "VSI.com");

		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
				"global/template/api/VSIVoucherGetOrderList.xml");

		api = YIFClientFactory.getInstance().getApi();
		Document docGetOrdlstOp = api.invoke(env,
				VSIConstants.API_GET_ORDER_LIST, getOrderListInput);
		env.clearApiTemplates();

		NodeList nlOrderLineList = docGetOrdlstOp
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int iNoOfGetOrderListlines = nlOrderLineList.getLength();
		// //System.out.println("nodeList length is " + iNoOfGetOrderListlines);
		NodeList nlPaymentMethod = docGetOrdlstOp
				.getElementsByTagName("PaymentMethod");
		int iNoOfPaymentMethod = nlPaymentMethod.getLength();
		// //System.out.println("No of PaymentMethod is " + iNoOfPaymentMethod);
		// //System.out.println("no of Line in the input" +
		// iInputOrderLinesLength);

		String sDescription;
		for (int l = 0; l < iInputOrderLinesLength; l++) {
			Element eleInputOrderLine = (Element) nlInputOrderLines.item(l);
			String sPrimeLineNo = eleInputOrderLine.getAttribute("PrimeLineNo");
			// //System.out.println("PrimeLineNo value is " + sPrimeLineNo);

			for (int i = 0; i < iNoOfGetOrderListlines; i++) {
				Element eleOrderLineApi = (Element) nlOrderLineList.item(i);
				String sPrimeLineNoApi = eleOrderLineApi
						.getAttribute("PrimeLineNo");
				// //System.out.println("sPrimeLineNoApi value is " +
				// sPrimeLineNoApi);
				if (sPrimeLineNo.equalsIgnoreCase(sPrimeLineNoApi)) {
					if (iNoOfGetOrderListlines > 1) {
						if (iNoOfPaymentMethod > 1) {

							sDescription = "The OrderNo (PrimeLineNo="
									+ sPrimeLineNo
									+ ") "
									+ sOrderNo
									+ "  is Cancelled. Please refund the amount";

							for (int j = 0; j < iNoOfPaymentMethod; j++) {
								Element elePaymentMethod = (Element) nlPaymentMethod
										.item(j);
								String sPaymentType = elePaymentMethod
										.getAttribute("PaymentType");

								// //System.out.println("PaymentType is" +
								// sPaymentType);

								if (sPaymentType.equalsIgnoreCase("VOUCHERS")) {
									String sMaxChargeLimit = elePaymentMethod
											.getAttribute("MaxChargeLimit");

									raiseVoucherAlert(env, sOrderHeaderKey,
											sOrderNo, sMaxChargeLimit,
											sDescription);
								}
							}
						} else {

							Element elePaymentMethod = (Element) nlPaymentMethod
									.item(0);
							if (null != elePaymentMethod) {
								String sPaymentType = elePaymentMethod
										.getAttribute("PaymentType");

								if (sPaymentType.equalsIgnoreCase("VOUCHERS")) {
									String sMaxChargeLimit = elePaymentMethod
											.getAttribute("MaxChargeLimit");

									sDescription = "The OrderNo (PrimeLineNo="
											+ sPrimeLineNo + ") " + sOrderNo
											+ " is Cancelled. Please refund "
											+ sMaxChargeLimit + " amount";
									raiseVoucherAlert(env, sOrderHeaderKey,
											sOrderNo, sMaxChargeLimit,
											sDescription);
								}
							}
						}
					} else {

						for (int k = 0; k < iNoOfPaymentMethod; k++) {
							Element elePaymentMethod = (Element) nlPaymentMethod
									.item(k);
							String sPaymentType = elePaymentMethod
									.getAttribute("PaymentType");

							if (sPaymentType.equalsIgnoreCase("VOUCHERS")) {
								String sMaxChargeLimit = elePaymentMethod
										.getAttribute("MaxChargeLimit");

								sDescription = "The OrderNo (PrimeLineNo="
										+ sPrimeLineNo + ") " + sOrderNo
										+ " is Cancelled. Please refund "
										+ sMaxChargeLimit + " amount";
								raiseVoucherAlert(env, sOrderHeaderKey,
										sOrderNo, sMaxChargeLimit, sDescription);
							}
						}
					}
				}
			}
		}

	}

	/**********************************************************************************
	 * Method Name : raiseVoucherAlert
	 * 
	 * Description : This method will create input for createException API and
	 * then raise alert
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 01/10/2014 This method will create input for createException API
	 * 
	 * @throws RemoteException
	 **********************************************************************************/
	public void raiseVoucherAlert(YFSEnvironment env, String sOHK,
			String sOrderNo, String sMaxChargeLimit, String sDescription)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();

		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, sDescription);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				"An Order with voucher is returned");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Voucher");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"Voucher Refund");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_VOUCHER_REFUND");

		Element InboxReferencesListElement = createExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHK);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);

	}

}
