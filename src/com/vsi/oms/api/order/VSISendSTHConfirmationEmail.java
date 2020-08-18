package com.vsi.oms.api.order;

import java.util.ArrayList;
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
import com.yantra.yfs.japi.YFSException;

/**
 * GrandTax - Tax 
 * LineSubTotal - Merchandise Total 
 * GrandShippingCharges - Shipping 
 * GrandTotal - Total For Shipment
 * 
 * ExtnIsShippingFree - Only if there is a ShippingCharge on the order in case
 * of multi shipment first will have all the shipping charge. First will go with
 * actual charge and second will go with $0.00
 * 
 * Make sure to pass the PaymentRuleID
 */
public class VSISendSTHConfirmationEmail implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSISendSTHConfirmationEmail.class);

	public Document sendEmail(YFSEnvironment env, Document inDoc) {

		Document emailDoc = null;
		Document outGetOrderInvoiceDtls = null;
		Element eleOrderInvoice = inDoc.getDocumentElement();
		String strOrderInvoiceKey = SCXmlUtil.getXpathAttribute(eleOrderInvoice, "//InvoiceHeader/@OrderInvoiceKey");
		boolean hasShippingCharge = false;
		double dblGWPDiscount = 0.00;
		try {
			String strDeliveryMethod = SCXmlUtil.getXpathAttribute(eleOrderInvoice, "//InvoiceHeader/Shipment/@DeliveryMethod");
			String strInvoiceType = SCXmlUtil.getXpathAttribute(eleOrderInvoice, "//InvoiceHeader/@InvoiceType");
			if(!YFCObject.isVoid(strOrderInvoiceKey) && ATTR_DEL_METHOD_SHP.equals(strDeliveryMethod) 
					&& INVOICE_TYPE_SHIPMENT.equals(strInvoiceType)){
				emailDoc = SCXmlUtil.createDocument(ELE_ORDER_LIST);
				Element eleEmailDoc = emailDoc.getDocumentElement();
				Document inDocGetInvoiceDtls = SCXmlUtil
						.createDocument(ELE_GET_ORDER_INVOICE_DTLS);
				inDocGetInvoiceDtls.getDocumentElement().setAttribute(
						ATTR_INVOICE_KEY, strOrderInvoiceKey);
				outGetOrderInvoiceDtls = VSIUtils.invokeAPI(env,
						TEMPLATE_GET_ORDER_INVOICE_DTLS_EMAIL,
						API_GET_ORDER_INVOICE_DTLS, inDocGetInvoiceDtls);
				Element eleGetOrderInvoiceDtls = SCXmlUtil.getChildElement(
						outGetOrderInvoiceDtls.getDocumentElement(),
						ELE_INVOICE_HEADER);
				
				String sTrackingNo = XMLUtil.getAttributeFromXPath(eleGetOrderInvoiceDtls,
						"//InvoiceHeader/Shipment/@TrackingNo");
				
				env.setTxnObject("eDeliveryMethod", strDeliveryMethod);
				
				String sCarrierServiceCode = XMLUtil.getAttributeFromXPath(
						eleGetOrderInvoiceDtls, "//InvoiceHeader/Shipment/@CarrierServiceCode");
				
				String sSCAC = XMLUtil.getAttributeFromXPath(
						eleGetOrderInvoiceDtls, "//InvoiceHeader/Shipment/@SCAC");
				
				String sSCACandServiceKey = XMLUtil.getAttributeFromXPath(
						eleGetOrderInvoiceDtls, "//InvoiceHeader/Shipment/@ScacAndServiceKey");
				
				if(!YFCCommon.isVoid(sSCACandServiceKey))
				{
					Document scacDoc = SCXmlUtil.createDocument("ScacAndService");
					scacDoc.getDocumentElement().setAttribute("ScacAndServiceKey", sSCACandServiceKey);
					
					Document docScacListoutput = VSIUtils.invokeAPI(env, "getScacAndServiceList", scacDoc);
					
					if(docScacListoutput.getDocumentElement().hasChildNodes())
					{
						Element scacAndServiceEle = SCXmlUtil.getChildElement(docScacListoutput.getDocumentElement(), "ScacAndService");
						String scacAndServiceDesc = scacAndServiceEle.getAttribute("ScacAndServiceDesc");
						if (!YFCObject.isVoid(scacAndServiceDesc)) {
							env.setTxnObject("eSCACandServiceDesc", scacAndServiceDesc);
						}
					}
				}
				

				if (!YFCObject.isVoid(sTrackingNo)) {
					env.setTxnObject("eTrackingValue", sTrackingNo);
				}

				if (!YFCObject.isVoid(sCarrierServiceCode)) {
					env.setTxnObject("eCarrierServiceCode", sCarrierServiceCode);
				}
				
				if (!YFCObject.isVoid(sSCAC)) {
					env.setTxnObject("eSCAC", sSCAC);
				}
				
				// Coupon Commit for STH shipments
				String ohk = XMLUtil.getAttributeFromXPath(eleGetOrderInvoiceDtls,
						"//InvoiceHeader/Order/@OrderHeaderKey");
				/*OMS-1540 : Coupon Commit Changes: Start*/
				String strCouponCommit="N";
				String strCommitFlag = "N";
				int noOfStatus = 0;
				Document getOrderLineStatusListInput = XMLUtil
						.createDocument("OrderLineStatus");
				Element statusLineEle = getOrderLineStatusListInput
						.getDocumentElement();
				statusLineEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
						ohk);
				Document getOrderLineStatusListOutput = VSIUtils.invokeAPI(env,
						"getOrderLineStatusList", getOrderLineStatusListInput);
				if (getOrderLineStatusListOutput != null) {
					NodeList statusNL = getOrderLineStatusListOutput
							.getElementsByTagName("OrderStatus");
					noOfStatus = statusNL.getLength();

					for (int k = 0; k < noOfStatus; k++) {
						Element statusEle = (Element) statusNL.item(k);
						String strStatus = statusEle.getAttribute("Status");
						if (!strStatus.equalsIgnoreCase("9000")
								&& !strStatus.equalsIgnoreCase("9000.200")
								&& !strStatus.equalsIgnoreCase("9000.100")) {
							//strCommitFlag = "Y";
							strCouponCommit="Y";
						}
					}
				}
				if ("Y".equals(strCouponCommit))
				{
					Document docGetOrderListInput = XMLUtil.createDocument("Order");
					Element eleOrder = docGetOrderListInput.getDocumentElement();
					eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
					int iPromoCount = 0;
					Document docGetOrderListOutput =VSIUtils.invokeAPI(env,"global/template/api/VSIGetOrderList_ShipmentEmail.xml",
					VSIConstants.API_GET_ORDER_LIST, docGetOrderListInput);
					if(log.isDebugEnabled()){
						log.debug("GetOrderDetails output: \n "+XMLUtil.getXMLString(docGetOrderListOutput));
					}
					if (docGetOrderListOutput != null) 
					{
							NodeList nPromotionList = docGetOrderListOutput.getElementsByTagName("Promotion");
							if(nPromotionList!=null)
							{
									iPromoCount = nPromotionList.getLength();
									Element elePromotion=null;
									Element elePromoExtn=null;
									String strCouponID="";
									for (int iProCount = 0; iProCount < iPromoCount; iProCount++) 
									{
											elePromotion = (Element) nPromotionList.item(iProCount);
											elePromoExtn=(Element) elePromotion.getElementsByTagName("Extn").item(0);
											if(elePromoExtn!=null)
											{
												strCouponID = elePromoExtn.getAttribute("ExtnCouponID");
												if (!YFCCommon.isVoid(strCouponID))
												{
													strCommitFlag="Y";
													break;
												}
											}
									}	
							}
					}
				}
				/*OMS-1540 : Coupon Commit Chnages :End */
				if(log.isDebugEnabled()){
					log.debug("strCommitFlag is " + strCommitFlag);
				}
				// End coupon commit STH shipments 
				
				// Append Order Element
				Element eleOrder = (Element) eleGetOrderInvoiceDtls
						.getElementsByTagName(ELE_ORDER).item(0)
						.cloneNode(true);
				
				eleOrder.setAttribute("Commit", strCommitFlag);
				
				emailDoc.adoptNode(eleOrder);
				eleEmailDoc.appendChild(eleOrder);

				
				//Append Calendar Element
				String shipnode = XMLUtil.getAttributeFromXPath(
						eleGetOrderInvoiceDtls, "//InvoiceHeader/Shipment/ShipNode/@ShipnodeKey");
				Document getCalInDoc = XMLUtil
						.createDocument("Calendar");
				Element calRtEle = getCalInDoc
						.getDocumentElement();
				calRtEle.setAttribute("CalendarId", shipnode);
				calRtEle.setAttribute("OrganizationCode", "VSI");

				Document getCalOutDoc = VSIUtils
						.invokeAPI(
								env,
								"global/template/api/VSIGetCalendarDetails.xml",
								"getCalendarList", getCalInDoc);
				if (getCalOutDoc != null) {
					NodeList calListNL = getCalOutDoc
							.getElementsByTagName("Calendar");
					int calCnt = calListNL.getLength();
					if (calCnt > 0) {
						Element calEle = (Element) calListNL
								.item(0);
						emailDoc.adoptNode(calEle);
						eleOrder.appendChild(calEle.cloneNode(true));
					}

				}
				
				// Updating Header OverallTotals
				Element eleOverallTotal = SCXmlUtil.getChildElement(eleOrder,
						ELE_OVERALL_TOTALS);

				String strInvoiceTotalAmount = eleGetOrderInvoiceDtls
						.getAttribute(ATTR_TOTAL_AMOUNT);
				String strInvoiceGrandTax = eleGetOrderInvoiceDtls
						.getAttribute(ATTR_TOTAL_TAX);
				

				eleOverallTotal.setAttribute(ATTR_GRAND_TOTAL,
						strInvoiceTotalAmount);
				eleOverallTotal
						.setAttribute(ATTR_GRAND_TAX, strInvoiceGrandTax);
				

				// Calculate ShippingCharges for the Shipment
				Element eleOrderExtn = (Element) eleOrder.getElementsByTagName(
						ELE_EXTN).item(0);
				String strShippingCharge = "";
				Element eleInvoiceOrderHeaderCharge = SCXmlUtil
						.getXpathElement(
								eleGetOrderInvoiceDtls,
								"//InvoiceHeader/Order/HeaderCharges/HeaderCharge[@ChargeCategory='"
										+ SHIPPING_CHARGE_CTGY + "']");
				if (!YFCObject.isVoid(eleInvoiceOrderHeaderCharge)) {
					strShippingCharge = eleInvoiceOrderHeaderCharge
							.getAttribute(ATTR_CHARGE_AMOUNT);
					if (!YFCObject.isVoid(strShippingCharge)
							&& !YFCObject.isDoubleVoid(Double
									.parseDouble(strShippingCharge))) {
						hasShippingCharge = true;
					}
				}
				if (hasShippingCharge) {
					Element eleInvoiceHeaderCharge = SCXmlUtil.getXpathElement(
							eleGetOrderInvoiceDtls,
							"//InvoiceHeader/HeaderCharges/HeaderCharge[@ChargeCategory='"
									+ SHIPPING_CHARGE_CTGY + "']");
					if (!YFCObject.isVoid(eleInvoiceHeaderCharge)) {
						String strInvoiceHeaderShippingCharge = eleInvoiceOrderHeaderCharge
								.getAttribute(ATTR_CHARGE_AMOUNT);
						if (!YFCObject.isVoid(strInvoiceHeaderShippingCharge)
								&& !YFCObject
										.isDoubleVoid(Double
												.parseDouble(strInvoiceHeaderShippingCharge))) {
							eleOverallTotal.setAttribute(
									ATTR_GRAND_SHIPPING_CHARGE,
									strInvoiceHeaderShippingCharge);
						} else {
							eleOverallTotal.setAttribute(
									ATTR_GRAND_SHIPPING_CHARGE, "0.00");
						}
					}else{
						eleOverallTotal.setAttribute(
								ATTR_GRAND_SHIPPING_CHARGE, "0.00");
					}
				} else {
					eleOrderExtn.setAttribute(ATTR_EXTN_IS_SHIPPING_FREE, "Y");
				}

				// Append PaymentMethod Element
				Element elePaymentMethods = SCXmlUtil.createChild(eleOrder,
						ELE_PAYMENT_METHODS);
				Element eleInvoiceCollectionDetails = SCXmlUtil
						.getXpathElement(eleGetOrderInvoiceDtls,
								"//InvoiceHeader/CollectionDetails");
				ArrayList<Element> alInvoiceCollectionDetails = SCXmlUtil
						.getChildren(eleInvoiceCollectionDetails,
								ELE_COLLECTION_DETAIL);
				for (Element eleInvoiceCollectionDetail : alInvoiceCollectionDetails) {
					String strAmountCollected = eleInvoiceCollectionDetail.getAttribute(ATTR_AMOUNT_COLLECTED);
					Element eleInvoicePaymentMethod = (Element) eleInvoiceCollectionDetail
							.getElementsByTagName(ELE_PAYMENT_METHOD).item(0);
					emailDoc.adoptNode(eleInvoicePaymentMethod);
					eleInvoicePaymentMethod.setAttribute(ATTR_TOTAL_CHARGED, strAmountCollected);
					elePaymentMethods.appendChild(eleInvoicePaymentMethod);
					
				}

				// Append ShipNode Element
				Element eleOrderShipNode = (Element) eleGetOrderInvoiceDtls
						.getElementsByTagName(ELE_SHIP_NODE).item(0)
						.cloneNode(true);
				emailDoc.adoptNode(eleOrderShipNode);
				eleOrder.appendChild(eleOrderShipNode.cloneNode(true));

				// Append OrderLine Element
				Element eleOrderLines = SCXmlUtil.createChild(eleOrder,
						ELE_ORDER_LINES);
				eleOrder.appendChild(eleOrderLines);
				Element eleInvoiceLineDetails = (Element) eleGetOrderInvoiceDtls
						.getElementsByTagName(ELE_LINE_DETAILS).item(0);
				ArrayList<Element> nlInvoiceLineDetails = SCXmlUtil
						.getChildren(eleInvoiceLineDetails, ELE_LINE_DETAIL);
				for (Element eleInvoiceLineDetail : nlInvoiceLineDetails) {
					String strShipmentLineQty = eleInvoiceLineDetail.getAttribute(ATTR_QUANTITY);
					String strShipmentExtendedPrice = eleInvoiceLineDetail.getAttribute(ATTR_EXTENDED_PRICE);
					Element eleOrderLine = (Element) eleInvoiceLineDetail
							.getElementsByTagName(ELE_ORDER_LINE).item(0)
							.cloneNode(true);
					Element extExtn = SCXmlUtil.getChildElement(eleOrderLine, "Extn");
					if(!YFCCommon.isVoid(extExtn) && "Y".equals(extExtn.getAttribute("ExtnIsGWP")))
					{
						strShipmentExtendedPrice = "0.00";
						Element lineOverallTotals = (Element) eleOrderLine.getElementsByTagName(ELE_LINE_OVERALL_TOTALS).item(0);
						dblGWPDiscount = dblGWPDiscount + SCXmlUtil.getDoubleAttribute(lineOverallTotals, "ExtendedPrice");
					}
					emailDoc.adoptNode(eleOrderLine);
					eleOrderLine.setAttribute(ATTR_ORD_QTY, strShipmentLineQty);
					((Element) eleOrderLine.getElementsByTagName(ELE_LINE_OVERALL_TOTALS).item(0)).setAttribute(ATTR_EXTENDED_PRICE, strShipmentExtendedPrice);
					eleOrderLines.appendChild(eleOrderLine);

					// Append ShipNode Element Line Level
					eleOrderLine.appendChild(eleOrderShipNode);
				}
				
				double dblInvoiceLineSubTotal = 0.00;
				dblInvoiceLineSubTotal=	SCXmlUtil.getDoubleAttribute(eleGetOrderInvoiceDtls, ATTR_LINE_SUB_TOTAL);

				
				double dblDiscount = 0.00;
				dblDiscount = SCXmlUtil.getDoubleAttribute(eleGetOrderInvoiceDtls, "TotalDiscount");
						
				if(dblGWPDiscount > 0 && dblDiscount > 0 && dblDiscount >= dblGWPDiscount)
				{
					dblDiscount = dblDiscount-dblGWPDiscount;
				}
				
				if(dblGWPDiscount > 0 && dblInvoiceLineSubTotal > 0 && dblInvoiceLineSubTotal >= dblGWPDiscount)
				{
					dblInvoiceLineSubTotal = dblInvoiceLineSubTotal-dblGWPDiscount;
				}

				if(dblDiscount > 0)
				{
					eleOverallTotal
					.setAttribute("GrandDiscount", "-"+String.valueOf(dblDiscount));
				}
				else
				{
					eleOverallTotal
					.setAttribute("GrandDiscount", "-0.00");
				}
				
				
				eleOverallTotal.setAttribute(ATTR_LINE_SUB_TOTAL,
						String.valueOf(dblInvoiceLineSubTotal));
				
			} else {
				// DO Nothing
			}	

		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emailDoc;
	}

}