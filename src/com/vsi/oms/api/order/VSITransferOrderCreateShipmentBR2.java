package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.elasticsearch.common.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc. Note: Need discussions with JDA about the queue and
 *         message.
 */
public class VSITransferOrderCreateShipmentBR2 {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSITransferOrderCreateShipmentBR2.class);
	YIFApi api;
	private Properties props;

	// YIFApi newApi;
	// YIFApi shipApi;
	/**
	 * 
	 * @param env
	 *            Required. Environment handle returned by the createEnvironment
	 *            API. This is required for the user exit to either raise errors
	 *            or access environment information.
	 * @param inXML
	 *            Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public void vsiTransferOrderShipmentCreateBR2(YFSEnvironment env,
			Document inXML) throws Exception {

		boolean bThrowCancellationError = false;
		try {

			Map<String, String> ItemDetailForCancel = new HashMap<String, String>();// **OMS-792*
			Map<String, String> ItemDetailForShip = new HashMap<String, String>();// **OMS-791*

			Element rootElement = inXML.getDocumentElement();
			Element shipmentLineElement = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_SHIPMENT_LINE).item(0);
			String attPayRuleID=rootElement.getAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID);
			String containerNumber = rootElement
					.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String trackingNumber = rootElement
					.getAttribute(VSIConstants.ATTR_TRACKING_NO);
			String storeID = rootElement
					.getAttribute(VSIConstants.ATTR_STORE_ID);
			String shipNode = rootElement
					.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			String recNode = rootElement.getAttribute("ReceivingNode");

			String entCode = rootElement
					.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String transferNumber = rootElement
					.getAttribute(VSIConstants.ATTR_TRANSFER_NO);// JDA
																	// Transfer
																	// No

			String customerPoNo = shipmentLineElement
					.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String productClass = shipmentLineElement
					.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
			String unitMeasure = shipmentLineElement
					.getAttribute(VSIConstants.ATTR_UOM);
			String dType = shipmentLineElement
					.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);

			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			Element eleOrderLine = getOrderListInput
					.createElement(VSIConstants.ELE_ORDER_LINE);
			eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
					customerPoNo);
			eleOrder.appendChild(eleOrderLine);

			// eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
			// customerPoNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
			eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
			// Calling the getOrderList API with CustomerPONo,EnterpriseCode and
			// DocumentType
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
					"global/template/api/JDACrShipOrderList.xml");
			Document inService = api.invoke(env,
					VSIConstants.API_GET_ORDER_LIST, getOrderListInput);
			NodeList headerchargeList = inService
					.getElementsByTagName("HeaderCharge");
			int shippingDays = 0;
			if (headerchargeList != null && headerchargeList.getLength() > 0) {
				for (int i = 0; i < headerchargeList.getLength(); i++) {
					Element headerChargeEle = (Element) headerchargeList
							.item(i);
					String strChargeName = headerChargeEle
							.getAttribute("ChargeName");
					if (strChargeName.equalsIgnoreCase("Shipping 2nd Day")) {
						shippingDays = 2;
					} else if (strChargeName
							.equalsIgnoreCase("Shipping Next Day")) {
						shippingDays = 1;
					} else if (strChargeName
							.equalsIgnoreCase("Standard Shipping")) {
						shippingDays = 4;
					} else if (strChargeName.equalsIgnoreCase("Shipping")) {
						shippingDays = 4;
					} else {
						shippingDays = 4;

					}

				}
			}
			env.clearApiTemplates();
			Element rootEle = inService.getDocumentElement();
			rootEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);

			Document outDoc = api.executeFlow(env, "VSIParseCreateShipment",
					inService);
			
			//get order element from outDoc
			Element rootElementoutDoc = outDoc.getDocumentElement();
			NodeList orderLinesLst = outDoc
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

			Element orderElem = (Element) outDoc.getElementsByTagName(
					VSIConstants.ELE_ORDER).item(0);

			Element orderLineElem = (Element) outDoc.getElementsByTagName(
					VSIConstants.ELE_ORDER_LINE).item(0);

			if (null != orderElem) {
				String orderHeaderKey = orderElem
						.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);

				String statusAttribute = rootElement
						.getAttribute(VSIConstants.ATTR_STATUS);
				String lineType = orderLineElem
						.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				String custPoNoFromLineList = orderLineElem
						.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
				String OLK = orderLineElem
						.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				Map<Integer, String> orderLinekeyMap = new HashMap<Integer, String>();
				String orderLineKeyTemp = null;

				if (lineType.equals("SHIP_TO_STORE")
						&& customerPoNo.equalsIgnoreCase(custPoNoFromLineList)) {
					// System.out.println("LineType Ship to store");

					/**
					 * ******************************START OMS-815 CHANGE
					 * ************************************************
					 */

					/**
					 * *****************Creating Map for storing quantity
					 * against Item ****************
					 */
					Map<String, Integer> ItemQuantityMap = new HashMap<String, Integer>();
					Map<String, Integer> ItemQuantityMap2 = new HashMap<String, Integer>();
					NodeList containerList = inXML
							.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
					for (int i = 0; i < containerList.getLength(); i++) {
						Element containerElem = (Element) inXML
								.getElementsByTagName(
										VSIConstants.ELE_CONTAINER_DETAIL)
								.item(i);
						Element shipmentLine = (Element) containerElem
								.getElementsByTagName(
										VSIConstants.ELE_SHIPMENT_LINE).item(0); // getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE);
																					// //correction
						String ItemID = shipmentLine
								.getAttribute(VSIConstants.ATTR_ITEM_ID);
						if(log.isDebugEnabled()){
							log.debug("ItemID" + ItemID);
						}
						String qty = containerElem
								.getAttribute(VSIConstants.ATTR_QUANTITY);
						if(log.isDebugEnabled()){
							log.debug("Quantity" + qty);
						}
						ItemQuantityMap.put(ItemID, (int) Double
								.parseDouble(qty));
					}
					/**
					 * *****************Creating Map for storing OrderLineKey
					 * against Item ****************
					 */

					Map<String, String> orderLinkeyMap = new HashMap<String, String>();
					Map<String, String> TransferOrderMap = new HashMap<String, String>();
					for (int i = 0; i < orderLinesLst.getLength(); i++) {
						int length = orderLinesLst.getLength();
						//System.out.println("Length" + length);
						Element orderLineEle = (Element) outDoc
								.getElementsByTagName(
										VSIConstants.ELE_ORDER_LINE).item(i);// correction
																				// inservice
						String MinLineStatus = orderLineEle
								.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
						if(log.isDebugEnabled()){
							log.debug("" + MinLineStatus);
						}
						String MaxLineStatus = orderLineEle
								.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
						if(log.isDebugEnabled()){
							log.debug("" + MaxLineStatus);
						}
						String CPO = orderLineEle
								.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
						if (MinLineStatus.equalsIgnoreCase("2160.100")
								&& CPO.equalsIgnoreCase(customerPoNo)) {
							String orderLineKey = orderLineEle
									.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							Element itemEle = (Element) orderLineEle
									.getElementsByTagName(VSIConstants.ELE_ITEM)
									.item(0);
							String itemId = itemEle
									.getAttribute(VSIConstants.ATTR_ITEM_ID);// TEMPLATE
																				// NEEDS
																				// TO
																				// BE
																				// CHANGED
							if(log.isDebugEnabled()){
								log.debug("ItemId" + itemId);
							}
							if (ItemQuantityMap.containsKey(itemId)) {
								if(log.isDebugEnabled()){
									log.debug("ItemId" + itemId);
									log.debug("orderLineKey"
											+ orderLineKey);
								}
								orderLinkeyMap.put(orderLineKey, itemId);

							}
						}
					}

					for (Entry<String, String> s : orderLinkeyMap.entrySet()) {
						String OrderLineKey = s.getKey();
						if(log.isDebugEnabled()){
							log.debug("OrderLineKey is " + OrderLineKey);
						}

						Document NewgetOrderListInput = XMLUtil
								.createDocument("Order");
						Element orderEle = NewgetOrderListInput
								.getDocumentElement();
						Element orderLineEle = XMLUtil.createElement(
								NewgetOrderListInput,
								VSIConstants.ELE_ORDER_LINE, "");
						orderLineEle
								.setAttribute(
										VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY,
										orderHeaderKey);
						orderLineEle.setAttribute("ChainedFromOrderLineKey",
								OrderLineKey);
						orderEle.appendChild(orderLineEle);

						api = YIFClientFactory.getInstance().getApi();
						env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
								"global/template/api/JDACrShipOrderList.xml");
						Document NewOutDoc = api.invoke(env,
								VSIConstants.API_GET_ORDER_LIST,
								NewgetOrderListInput);
						env.clearApiTemplates();

						if (log.isVerboseEnabled()) {
							log.verbose("NewOutDoc is : \n"
									+ XMLUtil.getXMLString(NewOutDoc));
						}

						Element newOrderElement = (Element) NewOutDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER)
								.item(0);
						String transferOrderNumber = newOrderElement
								.getAttribute(VSIConstants.ATTR_ORDER_NO);
						TransferOrderMap.put(transferOrderNumber, OrderLineKey);

					}
					boolean Check = false;
					int index = 0;
					Boolean statusFlag = null;
					Double maxReqAmt = 0.0;

					/**
					 * *******************************END OMS-815 CHANGE
					 * ************************************************
					 */

					/**
					 * *****************Creating Map for storing
					 * TranferOrderNumber against Item ****************
					 */
					for (Entry<String, String> s : TransferOrderMap.entrySet()) {

						String transferOrderNo = s.getKey();
						String OrderLineKey = s.getValue();
						if(log.isDebugEnabled()){
							log.debug("transferOrderNo is "
								+ transferOrderNo);
						}
						if(log.isDebugEnabled()){
							log.debug("orderHeaderKey is" + orderHeaderKey);
						}
						Map<String, String> orderLinkeyItemMap = new HashMap<String, String>();

						Document NewgetOrderListInput = XMLUtil
								.createDocument("Order");
						Element orderEle = NewgetOrderListInput
								.getDocumentElement();
						Element orderLineEle = XMLUtil.createElement(
								NewgetOrderListInput,
								VSIConstants.ELE_ORDER_LINE, "");
						orderLineEle
								.setAttribute(
										VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY,
										orderHeaderKey);
						orderLineEle.setAttribute("ChainedFromOrderLineKey",
								OrderLineKey);
						orderEle.appendChild(orderLineEle);
						// Calling getOrderList to get the Order no from
						// chainedOrderHeaderKey
						if (log.isVerboseEnabled()) {
							log
							.verbose("NewgetOrderListInput Document is : \n"
									+ XMLUtil
									.getXMLString(NewgetOrderListInput));
						}
						api = YIFClientFactory.getInstance().getApi();
						env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
								"global/template/api/JDACrShipOrderList.xml");
						Document NewOutDoc = api.invoke(env,
								VSIConstants.API_GET_ORDER_LIST,
								NewgetOrderListInput);
						env.clearApiTemplates();
						Element newOrderElement = (Element) NewOutDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER)
								.item(0);

						/** *************Start OMS-815****************** */

						Element OrderLineElement = (Element) NewOutDoc
								.getElementsByTagName(
										VSIConstants.ELE_ORDER_LINE).item(0);// returning
																				// multiple
																				// orderlines

						if (log.isVerboseEnabled()) {
							log.verbose("NewOutDoc Document is : \n"
									+ XMLUtil.getXMLString(NewOutDoc));
						}

						Map<String, String> orderLinkeyTransferOrderNumber = new HashMap<String, String>();
						NodeList OrderLineList = NewOutDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						for (int i = 0; i < OrderLineList.getLength(); i++) {
							Element OrderLineElem = (Element) OrderLineList
									.item(i);
							String Order_Line_Key = OrderLineElem
									.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							Element itemEle = (Element) OrderLineElem
									.getElementsByTagName(VSIConstants.ELE_ITEM)
									.item(0);
							String itemId = itemEle
									.getAttribute(VSIConstants.ATTR_ITEM_ID);// TEMPLATE
																				// NEEDS
																				// TO
																				// BE
																				// CHANGED

							if(log.isDebugEnabled()){
								log.debug("ItemIdForOrderLine" + itemId);
							}
							orderLinkeyTransferOrderNumber.put(Order_Line_Key,
									itemId);

						}

						/** *************End OMS-815****************** */

						// Code Change for fixing the BOPUS combined qty
						// responses from JDA for separate inventory - start
						// Map holds the incoming Container XML's SKU-Qty
						// mapping
						Map<String, Integer> inContainerSKUQtyMap = new HashMap<String, Integer>();
						NodeList inContainerDetailList = inXML
								.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
						for (int i = 0; i < inContainerDetailList.getLength(); i++) {
							// System.out.println("Entering Container #"+i);
							Element inContainerDetailElement = (Element) inContainerDetailList
									.item(i);
							Element newShipmentLineElement = (Element) inContainerDetailElement
									.getElementsByTagName(
											VSIConstants.ELE_SHIPMENT_LINE)
									.item(0);
							String SKU = newShipmentLineElement
									.getAttribute(VSIConstants.ATTR_ITEM_ID);
							String containerQty = inContainerDetailElement
									.getAttribute(VSIConstants.ATTR_QUANTITY);
							if (!inContainerSKUQtyMap.containsKey(SKU)) {
								inContainerSKUQtyMap.put(SKU, (int) Double
										.parseDouble(containerQty));
							}
						}

						// Map that holds the orderline qtys that are in the
						// 1200 (Reserved) status
						Map<String, Integer> TOLineQtyMap = new HashMap<String, Integer>();
						NodeList nlTOLineQty = NewOutDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
						for (int i = 0; i < nlTOLineQty.getLength(); i++) {
							// System.out.println("Entering Container #"+i);
							Element eleTOLineQty = (Element) nlTOLineQty
									.item(i);
							String strStatus = eleTOLineQty
									.getAttribute(VSIConstants.ATTR_STATUS);
							if (strStatus.equalsIgnoreCase("1200")) {
								TOLineQtyMap
										.put(
												eleTOLineQty
														.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY),
												Integer
														.parseInt(eleTOLineQty
																.getAttribute("StatusQty")));
							}
						}

						/*
						 * NodeList TOOrderLineNL = (NodeList)
						 * NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						 * for (int iTOOrderLine = 0; iTOOrderLine <=
						 * TOOrderLineNL.getLength(); iTOOrderLine++){ Element
						 * iTOOrderLineElement = (Element)
						 * TOOrderLineNL.item(iTOOrderLine); String strOLK =
						 * iTOOrderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
						 * String strSKU =
						 * iTOOrderLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
						 * int iTOStatusQty = TOLineQtyMap.get(strOLK); int
						 * iContainerInQty = inContainerSKUQtyMap.get(strSKU);
						 * if(iTOStatusQty != iContainerInQty){
						 *  } }
						 */

						// Code Change for fixing the BOPUS combined qty
						// responses from JDA for separate inventory - end

						if (statusAttribute.equals("SHIPPED")) {

							Document createShipmentInput = XMLUtil
									.createDocument("Shipment");
							Element createShipmentrootElement = createShipmentInput
									.getDocumentElement();
							createShipmentrootElement
									.setAttribute(VSIConstants.ATTR_ORDER_NO,
											transferOrderNo);
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_STORE_ID, storeID);// *******CHECK
							// for Jira 589,590,591
							String strCarierServiceCode = null;
							if (!trackingNumber
									.equalsIgnoreCase(containerNumber)) {
								if (trackingNumber.substring(0, 2)
										.equalsIgnoreCase("01")) {
									trackingNumber = "91" + trackingNumber;
								}
								if (trackingNumber.substring(0, 2)
										.equalsIgnoreCase("02")) {
									trackingNumber = "91" + trackingNumber;
								}
								if (trackingNumber.substring(0, 2)
										.equalsIgnoreCase("74")) {
									trackingNumber = "92" + trackingNumber;
								}
								if (trackingNumber.substring(0, 2)
										.equalsIgnoreCase("05")) {
									trackingNumber = "92" + trackingNumber;
								}

							}

							if (trackingNumber
									.equalsIgnoreCase(containerNumber)) {
								strCarierServiceCode = "VSI Ship";
							} else if (trackingNumber.substring(0, 2)
									.equalsIgnoreCase("1Z")) {
								strCarierServiceCode = "UPS";
							} else if (trackingNumber.substring(0, 1)
									.equalsIgnoreCase("0")) {
								strCarierServiceCode = "USPS";
							} else if (trackingNumber.substring(0, 4)
									.equalsIgnoreCase("9101")) {
								strCarierServiceCode = "USPS";
							} else if (trackingNumber.substring(0, 1)
									.equalsIgnoreCase("5")) {
								strCarierServiceCode = "UPS Mail Innovations";
							} else if (trackingNumber.substring(0, 4)
									.equalsIgnoreCase("9102")) {
								strCarierServiceCode = "UPS Mail Innovations";
							} else if (trackingNumber.substring(0, 1)
									.equalsIgnoreCase("3")) {
								strCarierServiceCode = "UPS Mail Innovations";
							} else if (trackingNumber.substring(0, 1)
									.equalsIgnoreCase("C")) {
								strCarierServiceCode = "OnTrac";
							} else if (trackingNumber.substring(0, 1)
									.equalsIgnoreCase("D")) {
								strCarierServiceCode = "OnTrac";
							} else if (trackingNumber.substring(0, 3)
									.equalsIgnoreCase("1LS")) {
								strCarierServiceCode = "Lasership";
							} else if (trackingNumber.substring(0, 4)
									.equalsIgnoreCase("9274")) {
								strCarierServiceCode = "UPS Mail Innovations";
							} else if (trackingNumber.substring(0, 4)
									.equalsIgnoreCase("9205")) {
								strCarierServiceCode = "USPS";
							}

							int LeadDays = stsLeadDays(recNode, env);

							Calendar calendar = Calendar.getInstance();

							calendar.add(Calendar.DATE, LeadDays);
							// calendar.add(Calendar.DATE, 7);
							String estDelvierydate = new SimpleDateFormat(
									"yyyy-MM-dd'T'HH:mm:ss").format(calendar
									.getTime());

							createShipmentrootElement.setAttribute(
									"ExpectedDeliveryDate", estDelvierydate);
							createShipmentrootElement.setAttribute(
									"CarrierServiceCode", strCarierServiceCode);

							// /end for jira 589,590,591

							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_TRACKING_NO,
									trackingNumber);
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_DOCUMENT_TYPE, dType);
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_CONFIRM_SHIP, "Y");
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_SHIP_CUST_PO_NO,
									customerPoNo);
							createShipmentrootElement.setAttribute(
									VSIConstants.ATTR_PICK_TICKET_NO,
									transferNumber);// Storing the JDA
													// transferNo in
													// PickticketNo
							Element createShipmentLinesElement = XMLUtil
									.appendChild(createShipmentInput,
											createShipmentrootElement,
											VSIConstants.ELE_SHIPMENT_LINES, "");
							// Element containersElement =
							// XMLUtil.appendChild(createShipmentInput,
							// createShipmentrootElement,
							// VSIConstants.ELE_CONTAINERS, "");
							// Element containerElement =
							// XMLUtil.appendChild(createShipmentInput,
							// containersElement, VSIConstants.ELE_CONTAINER,
							// "");

							NodeList containerDetailList = inXML
									.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
							for (int i = 0; i < containerDetailList.getLength(); i++) {
								// System.out.println("Entering Container #"+i);
								Element containerDetailElement = (Element) containerDetailList
										.item(i);

								Element shipmentElement = (Element) containerDetailElement
										.getElementsByTagName(
												VSIConstants.ELE_SHIPMENT_LINE)
										.item(0);
								String ItemID = shipmentElement
										.getAttribute(VSIConstants.ATTR_ITEM_ID);
								String containerQty = containerDetailElement
										.getAttribute(VSIConstants.ATTR_QUANTITY);

								/** ********Condition for OMS-815*************** */
								if (orderLinkeyTransferOrderNumber
										.containsValue(ItemID))

								{

									Element shipLineElement = (Element) containerDetailElement
											.getElementsByTagName(
													VSIConstants.ELE_SHIPMENT_LINE)
											.item(0);
									if(log.isDebugEnabled()){
										log.debug("ItemID is : in loop"
											+ ItemID);
									}

									Document getOrderLineListInput = XMLUtil
											.createDocument("OrderLine");
									Element getOrderLineListInputrootElement = getOrderLineListInput
											.getDocumentElement();

									String itemID = shipLineElement
											.getAttribute(VSIConstants.ATTR_ITEM_ID);

									ItemDetailForShip.put(itemID, Integer
											.toString(Double.valueOf(
													containerQty).intValue()));// **OMS-791*

									Element itemElement = getOrderLineListInput
											.createElement(VSIConstants.ELE_ITEM);
									itemElement.setAttribute(
											VSIConstants.ATTR_ITEM_ID, itemID);

									// Element lineElement =
									// getOrderLineListInput.createElement(VSIConstants.ELE_ORDER_LINE);
									String status = "1200";
									getOrderLineListInputrootElement
											.setAttribute(
													VSIConstants.ATTR_STATUS,
													status);

									getOrderLineListInputrootElement
											.setAttribute("OrderedQty",
													containerQty);

									Element orderElement = getOrderLineListInput
											.createElement(VSIConstants.ELE_ORDER);
									orderElement.setAttribute(
											VSIConstants.ATTR_ORDER_NO,
											transferOrderNo);

									getOrderLineListInputrootElement
											.appendChild(orderElement);
									getOrderLineListInputrootElement
											.appendChild(itemElement);

									env
											.setApiTemplate(
													VSIConstants.API_GET_ORDER_LINE_LIST,
													"global/template/api/VSITransferOrderCreateShipment.xml");
									YIFApi callApi = YIFClientFactory
											.getInstance().getLocalApi();
									Document outputDoc = callApi
											.invoke(
													env,
													VSIConstants.API_GET_ORDER_LINE_LIST,
													getOrderLineListInput);
									env.clearApiTemplates();

									YFCElement inputDocEle = YFCDocument
											.getDocumentFor(outputDoc)
											.getDocumentElement();
									YFCNodeList<YFCElement> orderLineCount = inputDocEle
											.getElementsByTagName("OrderLine");

									int noOfLine = orderLineCount.getLength();
									if (noOfLine == 0) {
										getOrderLineListInputrootElement
												.setAttribute("OrderedQty", "");
										env
												.setApiTemplate(
														VSIConstants.API_GET_ORDER_LINE_LIST,
														"global/template/api/VSITransferOrderCreateShipment.xml");
										outputDoc = callApi
												.invoke(
														env,
														VSIConstants.API_GET_ORDER_LINE_LIST,
														getOrderLineListInput);
										env.clearApiTemplates();
										YFCElement inputDocEle1 = YFCDocument
												.getDocumentFor(outputDoc)
												.getDocumentElement();
										orderLineCount = inputDocEle1
												.getElementsByTagName("OrderLine");

										noOfLine = orderLineCount.getLength();
									}

									int iContainerInQty = (int) Double
											.parseDouble(containerQty);

									for (int k = 0; k < noOfLine; k++) {
										if(log.isDebugEnabled()){
											log.debug("Value of K::::" + k);
										}
										Element orderLineElement = (Element) outputDoc
												.getElementsByTagName(
														VSIConstants.ELE_ORDER_LINE)
												.item(k);
										Element itemEle = (Element) orderLineElement
												.getElementsByTagName(
														VSIConstants.ELE_ITEM)
												.item(0);

										boolean bContinueUntilMoreUnits = true;
										String strSKU = itemEle
												.getAttribute(VSIConstants.ATTR_ITEM_ID);
										if(log.isDebugEnabled()){
											log.debug("SKU is :" + strSKU);
										}

										String orderLineKey = orderLineElement
												.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
										if(log.isDebugEnabled()){
											log.debug("OrderlineKey"
												+ orderLineKey);
										}
										// Code Change for fixing the BOPUS
										// combined qty responses from JDA for
										// separate inventory - start
										// if
										// (!orderLinekeyMap.containsValue(orderLineKey)
										// && !orderLinekeyMap.containsKey(i) ){
										if (!orderLinekeyMap
												.containsValue(orderLineKey)
												&& bContinueUntilMoreUnits
												&& iContainerInQty > 0) {
											// Code Change for fixing the BOPUS
											// combined qty responses from JDA
											// for separate inventory - end
											if(log.isDebugEnabled()){
												log.debug("entering OrderlineKey:::"
														+ orderLineKey);
												log.debug("iContainerInQty:::"
														+ iContainerInQty);
												log.debug("bContinueUntilMoreUnits:::"
														+ bContinueUntilMoreUnits);
											}

											String primeLineNo = orderLineElement
													.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
											String subLineNo = orderLineElement
													.getAttribute(VSIConstants.ATTR_SUB_LINE_NO);
											shipLineElement
													.setAttribute(
															VSIConstants.ATTR_PRIME_LINE_NO,
															primeLineNo);
											shipLineElement
													.setAttribute(
															VSIConstants.ATTR_SUB_LINE_NO,
															subLineNo);
											shipLineElement.setAttribute(
													VSIConstants.ATTR_ORDER_NO,
													transferOrderNo);

											// Code Change for fixing the BOPUS
											// combined qty responses from JDA
											// for separate inventory - start
											int iTOStatusQty = TOLineQtyMap
													.get(orderLineKey);
											if(log.isDebugEnabled()){
												log.debug("iTOStatusQty:::"
															+ iTOStatusQty);
											}
											if (iTOStatusQty < iContainerInQty) {
												shipLineElement
														.setAttribute(
																VSIConstants.ATTR_QUANTITY,
																Integer
																		.toString(iTOStatusQty));
												bContinueUntilMoreUnits = true;
												iContainerInQty = iContainerInQty
														- iTOStatusQty;
											} else {
												shipLineElement
														.setAttribute(
																VSIConstants.ATTR_QUANTITY,
																Integer
																		.toString(iContainerInQty));
												iContainerInQty = 0;
												bContinueUntilMoreUnits = false;
											}

											// shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY,
											// containerQty);

											// Code Change for fixing the BOPUS
											// combined qty responses from JDA
											// for separate inventory - end

											shipLineElement.setAttribute(
													VSIConstants.ATTR_UOM,
													unitMeasure);
											shipLineElement
													.setAttribute(
															VSIConstants.ATTR_DOCUMENT_TYPE,
															dType);
											shipLineElement
													.setAttribute(
															VSIConstants.ATTR_ENTERPRISE_CODE,
															entCode);
											shipLineElement
													.setAttribute(
															VSIConstants.ATTR_PRODUCT_CLASS,
															productClass);

											Element shipmentLineEle = XMLUtil
													.appendChild(
															createShipmentInput,
															createShipmentLinesElement,
															VSIConstants.ELE_SHIPMENT_LINE,
															"");
											XMLUtil.copyElement(
													createShipmentInput,
													shipLineElement,
													shipmentLineEle);
											orderLineKeyTemp = orderLineKey;
											orderLinekeyMap.put(i,
													orderLineKeyTemp);
											// System.out.println("Map
											// #"+orderLinekeyMap);
											// System.out.println("Container Qty
											// not matching #"+i +"Line#" +k);
											// break;
										}
									}
								}
							}
							String Document = XMLUtil
									.getXMLString(createShipmentInput);

							if (log.isVerboseEnabled()) {
								log
								.verbose(" createShipmentInput is: \n"
										+ XMLUtil
										.getXMLString(createShipmentInput));
							}

							// commenting for BOGO--changes to create a new
							// container element for BOGO scenarios
							// XMLUtil.copyElement(createShipmentInput,
							// rootElement, containerElement);

							// Element createShipmentSrcEle =
							// createShipmentInput.getDocumentElement();
							NodeList nShipmentLineEle = createShipmentInput
									.getElementsByTagName("ShipmentLine");
							// int noOfShipLine = nShipmentLineEle.getLength();
							Element containersElementele = XMLUtil.appendChild(
									createShipmentInput,
									createShipmentrootElement,
									VSIConstants.ELE_CONTAINERS, "");
							Element containerElementele = XMLUtil.appendChild(
									createShipmentInput, containersElementele,
									VSIConstants.ELE_CONTAINER, "");
							containerElementele.setAttribute("ContainerNo",
									rootElement.getAttribute("ContainerNo"));
							containerElementele.setAttribute("EnterpriseCode",
									rootElement.getAttribute("EnterpriseCode"));
							containerElementele.setAttribute("OrderType",
									rootElement.getAttribute("OrderType"));
							containerElementele.setAttribute("ReceivingNode",
									rootElement.getAttribute("ReceivingNode"));
							containerElementele.setAttribute("ShipNode",
									rootElement.getAttribute("ShipNode"));
							containerElementele.setAttribute("Status",
									rootElement.getAttribute("Status"));
							containerElementele.setAttribute("TrackingNo",
									trackingNumber);
							containerElementele.setAttribute("TransferNo",
									rootElement.getAttribute("TransferNo"));

							Element containerDetailsele = XMLUtil.appendChild(
									createShipmentInput, containerElementele,
									"ContainerDetails", "");
							int noOfShipLines = nShipmentLineEle.getLength();
							if(log.isDebugEnabled()){
								log.debug("no. of shipments line "
									+ nShipmentLineEle.getLength());
							}
							for (int l = 0; l < noOfShipLines; l++) {
								//System.out.println("inside of shipments line :"
										//+ l);
								Element ShipmentLineEle = (Element) nShipmentLineEle
										.item(l);
								String shipLineQty = ShipmentLineEle
										.getAttribute("Quantity");
								Element containerDetailele = XMLUtil
										.appendChild(createShipmentInput,
												containerDetailsele,
												"ContainerDetail", "");
								containerDetailele.setAttribute("Quantity",
										shipLineQty);
								Element ShipmentLineEles = XMLUtil.appendChild(
										createShipmentInput,
										containerDetailele, "ShipmentLine", "");

								ShipmentLineEles.setAttribute("DocumentType",
										ShipmentLineEle
												.getAttribute("DocumentType"));
								ShipmentLineEles
										.setAttribute(
												"EnterpriseCode",
												ShipmentLineEle
														.getAttribute("EnterpriseCode"));
								ShipmentLineEles.setAttribute("ItemID",
										ShipmentLineEle.getAttribute("ItemID"));
								ShipmentLineEles
										.setAttribute(
												"OrderNo",
												ShipmentLineEle
														.getAttribute("OrderNo"));
								ShipmentLineEles.setAttribute("PrimeLineNo",
										ShipmentLineEle
												.getAttribute("PrimeLineNo"));
								ShipmentLineEles.setAttribute("ProductClass",
										ShipmentLineEle
												.getAttribute("ProductClass"));
								ShipmentLineEles.setAttribute("Quantity",
										ShipmentLineEle
												.getAttribute("Quantity"));
								ShipmentLineEles.setAttribute("SubLineNo",
										ShipmentLineEle
												.getAttribute("SubLineNo"));
								ShipmentLineEles.setAttribute("UnitOfMeasure",
										ShipmentLineEle
												.getAttribute("UnitOfMeasure"));

							}

							if (log.isVerboseEnabled()) {
								log
								.verbose("NISH CREATESHIPMENT is: \n"
										+ XMLUtil
										.getXMLString(createShipmentInput));
							}

							env.setTxnObject("EnvShipQty", ItemDetailForShip);// **OMS-791*

							api = YIFClientFactory.getInstance().getApi();
							api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,
									createShipmentInput);

							orderLinkeyTransferOrderNumber.clear();

							/** ********Start OMS-815*************** */

							if (!Check) {
								for (int m = 0; m < orderLinesLst.getLength(); m++) {

									int length = orderLinesLst.getLength();

									if (m == length - 1) {
										//System.out.println("last loop");
										Check = true;
									}

									Element orderLineElement = (Element) orderLinesLst
											.item(m);
									String minLineStatus = orderLineElement
											.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
									Double dMinLine = Double
											.parseDouble(minLineStatus);

									// Comenteed for jira 653 if(dMinLine
									// >=2160.100)
									if (dMinLine >= 2160)
										statusFlag = true;

									else {

										statusFlag = false;
									}

									Element LinePriceInfoElement = (Element) orderLineElement
											.getElementsByTagName(
													"LinePriceInfo").item(0);

									String lineTotal = LinePriceInfoElement
											.getAttribute(VSIConstants.ATTR_LINE_TOTAL);
									Double dLineTotal = Double
											.parseDouble(lineTotal);
									maxReqAmt += dLineTotal;

								}
							}

							//System.out.println("maxReqAmt" + maxReqAmt);

							index++;
							boolean callmethod = false;
							//int SizeofLoop = orderLinkeyMap.size();
							int SizeofLoop = TransferOrderMap.size();
							
							if (index == SizeofLoop) {
								callmethod = true;
							}

							/** ********End OMS-815*************** */

							if(statusFlag == true && callmethod == true && 
									(!YFCObject.isVoid(attPayRuleID) && "DEFAULT".equalsIgnoreCase(attPayRuleID))){
								createCallCTR(env, orderHeaderKey,
										customerPoNo, maxReqAmt);
								createHold(env, orderHeaderKey);
								sendEmail(env, customerPoNo);
							}

							env.setTxnObject("EnvShipQty", ""); // *OMS-791*

						} else if ("CANCEL".equalsIgnoreCase(statusAttribute)) {
							// Cancel the quantity which was marked as unshipped
							// by WMS/JDA
							YFCDocument docCancelOrder = YFCDocument
									.parse("<Order/>");
							YFCElement elemCancelOrder = docCancelOrder
									.getDocumentElement();
							elemCancelOrder.setAttribute(
									"ModificationReasonCode", "STSBOCANCEL");
							YFCElement elemOrderLines = elemCancelOrder
									.getChildElement("OrderLines", true);
							NodeList containerDetailList = inXML
									.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
							for (int i = 0; i < containerDetailList.getLength(); i++) {

								Element containerDetailElement = (Element) containerDetailList
										.item(i);
								String containerQty = containerDetailElement
										.getAttribute(VSIConstants.ATTR_QUANTITY);
								Element shipLineElement = (Element) containerDetailElement
										.getElementsByTagName(
												VSIConstants.ELE_SHIPMENT_LINE)
										.item(0);
								Document getOrderLineListInput = XMLUtil
										.createDocument("OrderLine");
								Element getOrderLineListInputrootElement = getOrderLineListInput
										.getDocumentElement();
								
								
								String status = "2160.100";
								
								
								
								getOrderLineListInputrootElement.setAttribute(
										VSIConstants.ATTR_STATUS, status);
								// getOrderLineListInputrootElement.setAttribute("OrderedQty",
								// containerQty);
								getOrderLineListInputrootElement.setAttribute(
										"CustomerPONo", customerPoNo);
								String itemID = shipLineElement
										.getAttribute(VSIConstants.ATTR_ITEM_ID);

								ItemDetailForCancel.put(itemID, Integer
										.toString(Double.valueOf(containerQty)
												.intValue()));// **OMS-792*
								// System.out.println("OMS-792 - Insert data in
								// map");

								Element itemElement = getOrderLineListInput
										.createElement(VSIConstants.ELE_ITEM);
								itemElement.setAttribute(
										VSIConstants.ATTR_ITEM_ID, itemID);
								getOrderLineListInputrootElement
										.appendChild(itemElement);
								env
										.setApiTemplate(
												VSIConstants.API_GET_ORDER_LINE_LIST,
												"global/template/api/VSISTHOrderCreateShipment.xml");
								// call getOrderLineList API.
								Document outputDoc = api.invoke(env,
										VSIConstants.API_GET_ORDER_LINE_LIST,
										getOrderLineListInput);
								env.clearApiTemplates();
								Element eleOrdLineList = outputDoc
										.getDocumentElement();
								NodeList ndlOrderLineList = eleOrdLineList
										.getElementsByTagName("OrderLine");
								int iorderLineLength = ndlOrderLineList
										.getLength();
								// boolean bolSortedList = false;
								if (1 < iorderLineLength) {
									sortNodeListInAscOrderBasedOnLineTotal(
											eleOrdLineList, ndlOrderLineList);
									// bolSortedList = true;
								}
								double dblContainerQty = Double
										.parseDouble(containerQty);

								for (int j = 0; j < iorderLineLength; j++) {
									Element orderLineElement = (Element) ndlOrderLineList
											.item(j);

									// Fix to throw error if cancellation qty.
									// received is more than qty. in JDA ack.
									// status. : Start
									NodeList ndlOrderStatus = orderLineElement
											.getElementsByTagName("OrderStatus");
									String sStatusQty = "0.0";
									for (int k = 0; k < ndlOrderStatus
											.getLength(); k++) {
										Element eleOrderStatus = (Element) ndlOrderStatus
												.item(k);
										String sStatus = eleOrderStatus
												.getAttribute("Status");
										if (sStatus
												.equalsIgnoreCase("2160.100")) {
											sStatusQty = eleOrderStatus
													.getAttribute("StatusQty");
											break;
										}
									}
									Double dStatusQty = Double
											.parseDouble(sStatusQty);
									
									
									//Added one more condition as a part of SOS-7
									/*Description : 
									dblContainerQty : Is the quantity we get from JDAAcknowldge,
									dStatusQty : Is the quantity which we get from Orderlinelist for each line
									After cancelling qty from each line we also reduce dblContainerQty with same number and consider the remaining one for other lines.
									and this cycle goes on till no. of order lines for that item and status.
									*/
									
									if (dblContainerQty > dStatusQty && j==iorderLineLength-1) {
										if(log.isDebugEnabled()){
											log
											.verbose("Qty. received is greather than allowed qty.");
										}
										bThrowCancellationError = true;
										throw new YFSException();
									}

									// Fix End

									String orderLineKey = orderLineElement
											.getAttribute("OrderLineKey");
									// if
									// (!orderLinekeyMap.containsValue(orderLineKey)&&
									// !orderLinekeyMap.containsKey(i)){
									String ohk = orderLineElement
											.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
									elemCancelOrder.setAttribute(
											"OrderHeaderKey", ohk);
									elemCancelOrder.setAttribute("Override",
											"Y");
									YFCElement elemOrderLine = elemOrderLines
											.createChild("OrderLine");
									elemOrderLine.setAttribute("Action",
											"CANCEL");
									elemOrderLine.setAttribute("OrderLineKey",
											orderLineKey);
									elemOrderLine.setAttribute(
											"ModificationReasonCode",
											"STSBOCANCEL");
									// Jira # 647 (complete line was getting
									// cancelled even for partial cancellation
									// request )
									String orderLineOrdQty = orderLineElement
											.getAttribute("OrderedQty");
									double dblOrdLineQty = Double
											.parseDouble(orderLineOrdQty);
									if (dblContainerQty == dblOrdLineQty) {
										elemOrderLine.setAttribute(
												"OrderedQty", "0");
										break;
									} else if (dblContainerQty > dblOrdLineQty) {
										elemOrderLine.setAttribute(
												"OrderedQty", "0");
										dblContainerQty = dblContainerQty
												- dblOrdLineQty;
									} else {
										double actualRemainingQty = dblOrdLineQty
												- dblContainerQty;
										elemOrderLine
												.setAttribute(
														"OrderedQty",
														String
																.valueOf(actualRemainingQty));
										break;
									}
									// api.invoke(env, "changeOrder",
									// docCancelOrder.getDocument());
									// }
									// orderLineKeyTemp = orderLineKey;
									// orderLinekeyMap.put(i, orderLineKeyTemp);
									// YFCNodeList ndlOrderLinesToCancel =
									// docCancelOrder.getElementsByTagName("OrderLine");
									// if(ndlOrderLinesToCancel.getLength() >
									// 0){
									// api.invoke(env, "changeOrder",
									// docCancelOrder.getDocument());
									// }
									// }
								}
							}
							// **OMS-792*
							if (!YFCObject.isVoid(ItemDetailForCancel)) {
								env.setTxnObject("EnvCancelQty",
										ItemDetailForCancel);
								// System.out.println("OMS-792 - Set Map in
								// Environment variable");
							}// **OMS-792*

							// OMS-793 changes - Tax and charge Recalculation
							if(log.isDebugEnabled()){
					    		log.debug("VSIRecalculateChargeAndTax input XML -"
											+ XMLUtil
													.getXMLString(docCancelOrder
															.getDocument()));
							}
							Document outXML = modifyTaxAndCharge(env,
									docCancelOrder.getDocument());
							if(log.isDebugEnabled()){
								log.debug("VSIRecalculateChargeAndTax output XML -"
											+ XMLUtil.getXMLString(outXML));
							}
							// End

							api.invoke(env, "changeOrder", outXML);
							
							//SOS-8 Resolving hold if all the lines are beyond Recived at store status :
							resolveHoldOnCancellation(env,customerPoNo,entCode,orderHeaderKey);
							
						}
					}
				} else {
					//System.out.println("LineType not Ship to store");
					statusAttribute = rootElement
							.getAttribute(VSIConstants.ATTR_STATUS);

					// Code Change for fixing the BOPUS combined qty responses
					// from JDA for separate inventory - start

					Document NewgetOrderListInput = XMLUtil
							.createDocument("Order");
					Element orderEle = NewgetOrderListInput
							.getDocumentElement();
					Element orderLineEle = XMLUtil.createElement(
							NewgetOrderListInput, VSIConstants.ELE_ORDER_LINE,
							"");
					orderLineEle.setAttribute(
							VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
					orderLineEle.setAttribute("OrderLineKey", OLK);
					orderEle.appendChild(orderLineEle);
					// Calling getOrderList to get the Order no from Current
					// Ship To Home Order
					api = YIFClientFactory.getInstance().getApi();
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
							"global/template/api/JDACrShipOrderList.xml");
					Document NewOutDoc = api.invoke(env,
							VSIConstants.API_GET_ORDER_LIST,
							NewgetOrderListInput);
					env.clearApiTemplates();
					Element newOrderElement = (Element) NewOutDoc
							.getElementsByTagName(VSIConstants.ELE_ORDER).item(
									0);
					// String transferOrderNo =
					// newOrderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);

					// Map holds the incoming Container XML's SKU-Qty mapping
					Map<String, Integer> inContainerSKUQtyMap = new HashMap<String, Integer>();
					NodeList inContainerDetailList = inXML
							.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
					for (int i = 0; i < inContainerDetailList.getLength(); i++) {
						// System.out.println("Entering Container #"+i);
						Element inContainerDetailElement = (Element) inContainerDetailList
								.item(i);
						Element newShipmentLineElement = (Element) inContainerDetailElement
								.getElementsByTagName(
										VSIConstants.ELE_SHIPMENT_LINE).item(0);
						String SKU = newShipmentLineElement
								.getAttribute(VSIConstants.ATTR_ITEM_ID);
						String containerQty = inContainerDetailElement
								.getAttribute(VSIConstants.ATTR_QUANTITY);
						if (!inContainerSKUQtyMap.containsKey(SKU)) {
							inContainerSKUQtyMap.put(SKU, (int) Double
									.parseDouble(containerQty));
						}
					}

					// Map that holds the orderline qtys that are in the 1200
					// (Reserved) status
					Map<String, Integer> TOLineQtyMap = new HashMap<String, Integer>();
					NodeList nlTOLineQty = NewOutDoc
							.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
					for (int i = 0; i < nlTOLineQty.getLength(); i++) {
						// System.out.println("Entering Container #"+i);
						Element eleTOLineQty = (Element) nlTOLineQty.item(i);
						String strStatus = eleTOLineQty
								.getAttribute(VSIConstants.ATTR_STATUS);
						if (strStatus.equalsIgnoreCase("3200.100")) {
							TOLineQtyMap
									.put(
											eleTOLineQty
													.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY),
											Integer.parseInt(eleTOLineQty
													.getAttribute("StatusQty")));
						}
					}

					/*
					 * NodeList TOOrderLineNL = (NodeList)
					 * NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
					 * for (int iTOOrderLine = 0; iTOOrderLine <=
					 * TOOrderLineNL.getLength(); iTOOrderLine++){ Element
					 * iTOOrderLineElement = (Element)
					 * TOOrderLineNL.item(iTOOrderLine); String strOLK =
					 * iTOOrderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					 * String strSKU =
					 * iTOOrderLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
					 * int iTOStatusQty = TOLineQtyMap.get(strOLK); int
					 * iContainerInQty = inContainerSKUQtyMap.get(strSKU);
					 * if(iTOStatusQty != iContainerInQty){
					 *  } }
					 */

					// Code Change for fixing the BOPUS combined qty responses
					// from JDA for separate inventory - end

					if (statusAttribute.equals("SHIPPED")) {
						// System.out.println("inside code statuscheck");
						Document createShipmentInput = XMLUtil
								.createDocument("Shipment");
						Element createShipmentrootElement = createShipmentInput
								.getDocumentElement();
						// createShipmentrootElement.setAttribute(VSIConstants.ATTR_ORDER_NO,
						// customerPoNo);
						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_STORE_ID, storeID);// *******CHECK
						// for Jira 589,590,591
						String strCarierServiceCode = null;
						if (!trackingNumber.equalsIgnoreCase(containerNumber)) {
							if (trackingNumber.substring(0, 2)
									.equalsIgnoreCase("01")) {
								trackingNumber = "91" + trackingNumber;
							}
							if (trackingNumber.substring(0, 2)
									.equalsIgnoreCase("02")) {
								trackingNumber = "91" + trackingNumber;
							}
							if (trackingNumber.substring(0, 2)
									.equalsIgnoreCase("74")) {
								trackingNumber = "92" + trackingNumber;
							}
							if (trackingNumber.substring(0, 2)
									.equalsIgnoreCase("05")) {
								trackingNumber = "92" + trackingNumber;
							}

						}

						if (trackingNumber.equalsIgnoreCase(containerNumber)) {
							strCarierServiceCode = "VSI Ship";
						} else if (trackingNumber.substring(0, 2)
								.equalsIgnoreCase("1Z")) {
							strCarierServiceCode = "UPS";
						} else if (trackingNumber.substring(0, 1)
								.equalsIgnoreCase("0")) {
							strCarierServiceCode = "USPS";
						} else if (trackingNumber.substring(0, 4)
								.equalsIgnoreCase("9101")) {
							strCarierServiceCode = "USPS";
						} else if (trackingNumber.substring(0, 1)
								.equalsIgnoreCase("5")) {
							strCarierServiceCode = "UPS Mail Innovations";
						} else if (trackingNumber.substring(0, 4)
								.equalsIgnoreCase("9102")) {
							strCarierServiceCode = "UPS Mail Innovations";
						} else if (trackingNumber.substring(0, 1)
								.equalsIgnoreCase("3")) {
							strCarierServiceCode = "UPS Mail Innovations";
						} else if (trackingNumber.substring(0, 1)
								.equalsIgnoreCase("C")) {
							strCarierServiceCode = "OnTrac";
						} else if (trackingNumber.substring(0, 1)
								.equalsIgnoreCase("D")) {
							strCarierServiceCode = "OnTrac";
						} else if (trackingNumber.substring(0, 3)
								.equalsIgnoreCase("1LS")) {
							strCarierServiceCode = "Lasership";
						} else if (trackingNumber.substring(0, 4)
								.equalsIgnoreCase("9274")) {
							strCarierServiceCode = "UPS Mail Innovations";
						} else if (trackingNumber.substring(0, 4)
								.equalsIgnoreCase("9205")) {
							strCarierServiceCode = "USPS";
						}
						int LeadDays = sthLeadDays(shipNode, env, shippingDays);

						Calendar calendar = Calendar.getInstance();

						calendar.add(Calendar.DATE, LeadDays);
						// calendar.add(Calendar.DATE, 7);
						String estDelvierydate = new SimpleDateFormat(
								"yyyy-MM-dd'T'HH:mm:ss").format(calendar
								.getTime());

						createShipmentrootElement.setAttribute(
								"ExpectedDeliveryDate", estDelvierydate);
						createShipmentrootElement.setAttribute(
								"CarrierServiceCode", strCarierServiceCode);
						// /end for jira 589,590,591

						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_TRACKING_NO, trackingNumber);
						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_CONFIRM_SHIP, "Y");
						// createShipmentrootElement.setAttribute(VSIConstants.ATTR_SHIP_CUST_PO_NO,
						// customerPoNo);
						createShipmentrootElement.setAttribute(
								VSIConstants.ATTR_PICK_TICKET_NO,
								transferNumber);// Storing the JDA transferNo in
												// PickticketNo
						Element createShipmentLinesElement = XMLUtil
								.appendChild(createShipmentInput,
										createShipmentrootElement,
										VSIConstants.ELE_SHIPMENT_LINES, "");
						// Element containersElement =
						// XMLUtil.appendChild(createShipmentInput,
						// createShipmentrootElement,
						// VSIConstants.ELE_CONTAINERS, "");
						// Element containerElement =
						// XMLUtil.appendChild(createShipmentInput,
						// containersElement, VSIConstants.ELE_CONTAINER, "");

						NodeList containerDetailList = inXML
								.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
						for (int i = 0; i < containerDetailList.getLength(); i++) {

							// System.out.println("inside 1 for loop");

							Element containerDetailElement = (Element) containerDetailList
									.item(i);
							String containerQty = containerDetailElement
									.getAttribute(VSIConstants.ATTR_QUANTITY);

							Element shipLineElement = (Element) containerDetailElement
									.getElementsByTagName(
											VSIConstants.ELE_SHIPMENT_LINE)
									.item(0);

							Document getOrderLineListInput = XMLUtil
									.createDocument("OrderLine");
							Element getOrderLineListInputrootElement = getOrderLineListInput
									.getDocumentElement();

							String status = "3200.100";
							getOrderLineListInputrootElement.setAttribute(
									VSIConstants.ATTR_STATUS, status);
							getOrderLineListInputrootElement.setAttribute(
									"OrderedQty", containerQty);
							getOrderLineListInputrootElement.setAttribute(
									"CustomerPONo", customerPoNo);

							String itemID = shipLineElement
									.getAttribute(VSIConstants.ATTR_ITEM_ID);

							Element itemElement = getOrderLineListInput
									.createElement(VSIConstants.ELE_ITEM);
							itemElement.setAttribute(VSIConstants.ATTR_ITEM_ID,
									itemID);

							// Element orderElement =
							// getOrderLineListInput.createElement(VSIConstants.ELE_ORDER);
							// orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO,
							// customerPoNo);

							// getOrderLineListInputrootElement.appendChild(orderElement);
							getOrderLineListInputrootElement
									.appendChild(itemElement);

							ItemDetailForShip.put(itemID, Integer
									.toString(Double.valueOf(containerQty)
											.intValue()));// **OMS-791*

							env
									.setApiTemplate(
											VSIConstants.API_GET_ORDER_LINE_LIST,
											"global/template/api/VSISTHOrderCreateShipment.xml");
							YIFApi callApi = YIFClientFactory.getInstance()
									.getLocalApi();

							Document outputDoc = callApi.invoke(env,
									VSIConstants.API_GET_ORDER_LINE_LIST,
									getOrderLineListInput);
							env.clearApiTemplates();

							YFCElement inputDocEle = YFCDocument
									.getDocumentFor(outputDoc)
									.getDocumentElement();
							YFCNodeList<YFCElement> orderLineCount = inputDocEle
									.getElementsByTagName("OrderLine");
							int noOfSTHLine = orderLineCount.getLength();
							if (noOfSTHLine == 0) {
								getOrderLineListInputrootElement.setAttribute(
										"OrderedQty", "");
								env
										.setApiTemplate(
												VSIConstants.API_GET_ORDER_LINE_LIST,
												"global/template/api/VSISTHOrderCreateShipment.xml");
								outputDoc = callApi.invoke(env,
										VSIConstants.API_GET_ORDER_LINE_LIST,
										getOrderLineListInput);
								env.clearApiTemplates();
								YFCElement inputDocEle1 = YFCDocument
										.getDocumentFor(outputDoc)
										.getDocumentElement();
								YFCNodeList<YFCElement> orderLineCount1 = inputDocEle1
										.getElementsByTagName("OrderLine");

								noOfSTHLine = orderLineCount1.getLength();
								// System.out.println("if noofSTHLine cond");

							}
							int iContainerInQty = (int) Double
									.parseDouble(containerQty);

							// Map<Integer, String> orderLinekeyMap = new
							// HashMap<Integer, String>();

							for (int j = 0; j < noOfSTHLine; j++) {
								// System.out.println("Inside second for loop
								// J");
								Element orderLineElement = (Element) outputDoc
										.getElementsByTagName(
												VSIConstants.ELE_ORDER_LINE)
										.item(j);
								String orderLineKey = orderLineElement
										.getAttribute("OrderLineKey");
								Element itemEle = (Element) orderLineElement
										.getElementsByTagName(
												VSIConstants.ELE_ITEM).item(0);

								boolean bContinueUntilMoreUnits = true;
								String strSKU = itemEle
										.getAttribute(VSIConstants.ATTR_ITEM_ID);
								// Code Change for fixing the BOPUS combined qty
								// responses from JDA for separate inventory -
								// start
								// if
								// (!orderLinekeyMap.containsValue(orderLineKey)&&
								// !orderLinekeyMap.containsKey(i)){
								if (!orderLinekeyMap
										.containsValue(orderLineKey)
										&& bContinueUntilMoreUnits
										&& iContainerInQty > 0) {
									// Code Change for fixing the BOPUS combined
									// qty responses from JDA for separate
									// inventory - end
									Element orderStatusElement = (Element) orderLineElement
											.getElementsByTagName(
													VSIConstants.ELE_ORDER_STATUS)
											.item(0);
									String ork = orderStatusElement
											.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
									String primeLineNo = orderLineElement
											.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
									String ohk = orderLineElement
											.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
									// System.out.println("primeLineNo"+primeLineNo);
									String subLineNo = orderLineElement
											.getAttribute(VSIConstants.ATTR_SUB_LINE_NO);
									// System.out.println("subLineNo"+subLineNo);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_PRIME_LINE_NO,
											primeLineNo);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_SUB_LINE_NO,
											subLineNo);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_ORDER_HEADER_KEY,
											ohk);
									shipLineElement
											.setAttribute(
													VSIConstants.ATTR_ORDER_RELEASE_KEY,
													ork);
									createShipmentrootElement.setAttribute(
											VSIConstants.ATTR_ORDER_HEADER_KEY,
											ohk);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_ORDER_NO, "");
									// shipLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO,
									// customerPoNo);
									// System.out.println("customerPoNo"+customerPoNo);

									// Code Change for fixing the BOPUS combined
									// qty responses from JDA for separate
									// inventory - start
									// shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY,
									// containerQty);
									int iTOStatusQty = TOLineQtyMap
											.get(orderLineKey);
									if (iTOStatusQty < iContainerInQty) {
										shipLineElement.setAttribute(
												VSIConstants.ATTR_QUANTITY,
												Integer.toString(iTOStatusQty));
										bContinueUntilMoreUnits = true;
										iContainerInQty = iContainerInQty
												- iTOStatusQty;
									} else {
										shipLineElement
												.setAttribute(
														VSIConstants.ATTR_QUANTITY,
														Integer
																.toString(iContainerInQty));
										iContainerInQty = 0;
										bContinueUntilMoreUnits = false;
									}

									// Code Change for fixing the BOPUS combined
									// qty responses from JDA for separate
									// inventory - end

									// System.out.println("containerQty"+containerQty);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_UOM, unitMeasure);
									// shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY,
									// containerQty);

									shipLineElement.setAttribute(
											VSIConstants.ATTR_DOCUMENT_TYPE,
											"0001");
									shipLineElement.setAttribute(
											VSIConstants.ATTR_ENTERPRISE_CODE,
											entCode);
									shipLineElement.setAttribute(
											VSIConstants.ATTR_PRODUCT_CLASS,
											productClass);

									Element shipmentLineEle = XMLUtil
											.appendChild(
													createShipmentInput,
													createShipmentLinesElement,
													VSIConstants.ELE_SHIPMENT_LINE,
													"");
									XMLUtil.copyElement(createShipmentInput,
											shipLineElement, shipmentLineEle);
									orderLineKeyTemp = orderLineKey;
									orderLinekeyMap.put(i, orderLineKeyTemp);
									// System.out.println("Below second for
									// loop");
									// break;

								}

							}

						}
						// XMLUtil.copyElement(createShipmentInput, rootElement,
						// containerElement);

						// commenting for BOGO--changes to create a new
						// container element for BOGO scenarios
						// XMLUtil.copyElement(createShipmentInput, rootElement,
						// containerElement);

						// commenting for BOGO--changes to create a new
						// container element for BOGO scenarios
						// XMLUtil.copyElement(createShipmentInput, rootElement,
						// containerElement);

						// Element createShipmentSrcEle =
						// createShipmentInput.getDocumentElement();
						NodeList nShipmentLineEle = createShipmentInput
								.getElementsByTagName("ShipmentLine");
						// int noOfShipLine = nShipmentLineEle.getLength();
						Element containersElementele = XMLUtil.appendChild(
								createShipmentInput, createShipmentrootElement,
								VSIConstants.ELE_CONTAINERS, "");
						Element containerElementele = XMLUtil.appendChild(
								createShipmentInput, containersElementele,
								VSIConstants.ELE_CONTAINER, "");
						containerElementele.setAttribute("ContainerNo",
								rootElement.getAttribute("ContainerNo"));
						containerElementele.setAttribute("EnterpriseCode",
								rootElement.getAttribute("EnterpriseCode"));
						containerElementele.setAttribute("OrderType",
								rootElement.getAttribute("OrderType"));
						containerElementele.setAttribute("ReceivingNode",
								rootElement.getAttribute("ReceivingNode"));
						containerElementele.setAttribute("ShipNode",
								rootElement.getAttribute("ShipNode"));
						containerElementele.setAttribute("Status", rootElement
								.getAttribute("Status"));
						containerElementele.setAttribute("TrackingNo",
								trackingNumber);
						containerElementele.setAttribute("TransferNo",
								rootElement.getAttribute("TransferNo"));

						Element containerDetailsele = XMLUtil.appendChild(
								createShipmentInput, containerElementele,
								"ContainerDetails", "");
						int noOfShipLines = nShipmentLineEle.getLength();
						for (int l = 0; l < noOfShipLines; l++) {
							//System.out
									//.println("inside of shipments line :" + l);
							Element ShipmentLineEle = (Element) nShipmentLineEle
									.item(l);
							String shipLineQty = ShipmentLineEle
									.getAttribute("Quantity");
							Element containerDetailele = XMLUtil.appendChild(
									createShipmentInput, containerDetailsele,
									"ContainerDetail", "");
							containerDetailele.setAttribute("Quantity",
									shipLineQty);
							Element ShipmentLineEles = XMLUtil.appendChild(
									createShipmentInput, containerDetailele,
									"ShipmentLine", "");

							ShipmentLineEles.setAttribute("DocumentType",
									ShipmentLineEle
											.getAttribute("DocumentType"));
							ShipmentLineEles.setAttribute("EnterpriseCode",
									ShipmentLineEle
											.getAttribute("EnterpriseCode"));
							ShipmentLineEles.setAttribute("ItemID",
									ShipmentLineEle.getAttribute("ItemID"));
							ShipmentLineEles.setAttribute("OrderNo",
									ShipmentLineEle.getAttribute("OrderNo"));
							ShipmentLineEles.setAttribute("OrderHeaderKey",
									ShipmentLineEle
											.getAttribute("OrderHeaderKey"));
							ShipmentLineEles.setAttribute("OrderReleaseKey",
									ShipmentLineEle
											.getAttribute("OrderReleaseKey"));
							ShipmentLineEles
									.setAttribute(
											"PrimeLineNo",
											ShipmentLineEle
													.getAttribute("PrimeLineNo"));
							ShipmentLineEles.setAttribute("ProductClass",
									ShipmentLineEle
											.getAttribute("ProductClass"));
							ShipmentLineEles.setAttribute("Quantity",
									ShipmentLineEle.getAttribute("Quantity"));
							ShipmentLineEles.setAttribute("SubLineNo",
									ShipmentLineEle.getAttribute("SubLineNo"));
							ShipmentLineEles.setAttribute("UnitOfMeasure",
									ShipmentLineEle
											.getAttribute("UnitOfMeasure"));

						}
						if(log.isDebugEnabled()){
				    		log.debug("CreateShipment"+XMLUtil.getXMLString(createShipmentInput));
						}

						env.setTxnObject("EnvShipQty", ItemDetailForShip);// **OMS-791*

						api = YIFClientFactory.getInstance().getApi();
						api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,
								createShipmentInput);

					} else if ("CANCEL".equalsIgnoreCase(statusAttribute)) {

						// Cancel the quantity which was marked as unshipped by
						// WMS/JDA
						YFCDocument docCancelOrder = YFCDocument
								.parse("<Order/>");
						YFCElement elemCancelOrder = docCancelOrder
								.getDocumentElement();
						YFCElement elemOrderLines = elemCancelOrder
								.getChildElement("OrderLines", true);
						elemCancelOrder.setAttribute("ModificationReasonCode",
								"STSBOCANCEL");
						NodeList containerDetailList = inXML
								.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
						for (int i = 0; i < containerDetailList.getLength(); i++) {

							Element containerDetailElement = (Element) containerDetailList
									.item(i);
							String containerQty = containerDetailElement
									.getAttribute(VSIConstants.ATTR_QUANTITY);
							double dblcontainerQty = Double
									.parseDouble(containerQty);

							Element shipLineElement = (Element) containerDetailElement
									.getElementsByTagName(
											VSIConstants.ELE_SHIPMENT_LINE)
									.item(0);

							Document getOrderLineListInput = XMLUtil
									.createDocument("OrderLine");
							Element getOrderLineListInputrootElement = getOrderLineListInput
									.getDocumentElement();

							String status = "3200.100";
							getOrderLineListInputrootElement.setAttribute(
									VSIConstants.ATTR_STATUS, status);
							// getOrderLineListInputrootElement.setAttribute("OrderedQty",
							// containerQty);
							getOrderLineListInputrootElement.setAttribute(
									"CustomerPONo", customerPoNo);

							String itemID = shipLineElement
									.getAttribute(VSIConstants.ATTR_ITEM_ID);

							Element itemElement = getOrderLineListInput
									.createElement(VSIConstants.ELE_ITEM);
							itemElement.setAttribute(VSIConstants.ATTR_ITEM_ID,
									itemID);
							getOrderLineListInputrootElement
									.appendChild(itemElement);

							ItemDetailForCancel.put(itemID, Integer
									.toString(Double.valueOf(containerQty)
											.intValue()));// **OMS-792*
							// System.out.println("OMS-792 - Insert data in
							// map");

							env
									.setApiTemplate(
											VSIConstants.API_GET_ORDER_LINE_LIST,
											"global/template/api/VSISTHOrderCreateShipment.xml");

							Document outputDoc = api.invoke(env,
									VSIConstants.API_GET_ORDER_LINE_LIST,
									getOrderLineListInput);
							env.clearApiTemplates();

							NodeList ndlOrderLineList = outputDoc
									.getElementsByTagName("OrderLine");

							double dblContainerQty = Double
									.parseDouble(containerQty);
							for (int j = 0; j < ndlOrderLineList.getLength(); j++) {

								// Fix to enable partial qty. cancellation for
								// Ship to Home orders
								Element orderLineElement = (Element) ndlOrderLineList
										.item(j);

								// Fix to throw error if cancellation qty.
								// received is more than qty. in JDA ack.
								// status. : Start
								NodeList ndlOrderStatus = orderLineElement
										.getElementsByTagName("OrderStatus");
								String sStatusQty = "0.0";
								for (int k = 0; k < ndlOrderStatus.getLength(); k++) {
									Element eleOrderStatus = (Element) ndlOrderStatus
											.item(k);
									String sStatus = eleOrderStatus
											.getAttribute("Status");
									if (sStatus.equalsIgnoreCase("3200.100")) {
										sStatusQty = eleOrderStatus
												.getAttribute("StatusQty");
										break;
									}
								}
								Double dStatusQty = Double
										.parseDouble(sStatusQty);
								if (dblContainerQty > dStatusQty) {
									if(log.isDebugEnabled()){
										log
										.verbose("Qty. received is greather than allowed qty.");
									}
									bThrowCancellationError = true;
									throw new YFSException();
								}

								// Fix End

								String orderLineKey = orderLineElement
										.getAttribute("OrderLineKey");
								// if
								// (!orderLinekeyMap.containsValue(orderLineKey)&&
								// !orderLinekeyMap.containsKey(i)){
								String ohk = orderLineElement
										.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
								elemCancelOrder.setAttribute("OrderHeaderKey",
										ohk);
								elemCancelOrder.setAttribute("Override", "Y");
								YFCElement elemOrderLine = elemOrderLines
										.createChild("OrderLine");
								elemOrderLine.setAttribute("Action", "CANCEL");
								elemOrderLine.setAttribute("OrderLineKey",
										orderLineKey);
								elemOrderLine
										.setAttribute("ModificationReasonCode",
												"STSBOCANCEL");
								// Jira # 647 (complete line was getting
								// cancelled even for partial cancellation
								// request )
								String orderLineOrdQty = orderLineElement
										.getAttribute("OrderedQty");
								double dblOrdLineQty = Double
										.parseDouble(orderLineOrdQty);
								if (dblContainerQty == dblOrdLineQty) {
									elemOrderLine.setAttribute("OrderedQty",
											"0");
									break;
								} else if (dblContainerQty > dblOrdLineQty) {
									elemOrderLine.setAttribute("OrderedQty",
											"0");
									dblContainerQty = dblContainerQty
											- dblOrdLineQty;
								} else {
									double actualRemainingQty = dblOrdLineQty
											- dblContainerQty;
									elemOrderLine.setAttribute("OrderedQty",
											String.valueOf(actualRemainingQty));
									break;
								}

								/*
								 * Element orderLineElement = (Element)
								 * ndlOrderLineList.item(j); String orderLineKey =
								 * orderLineElement.getAttribute("OrderLineKey");
								 * if
								 * (!orderLinekeyMap.containsValue(orderLineKey)&&
								 * !orderLinekeyMap.containsKey(i)){ String ohk =
								 * orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
								 * if(dblcontainerQty >=0){
								 * elemCancelOrder.setAttribute("OrderHeaderKey",
								 * ohk); //For Jira OMS - 603
								 * elemCancelOrder.setAttribute("Override",
								 * "Y");
								 * 
								 * YFCElement elemOrderLine =
								 * elemOrderLines.createChild("OrderLine");
								 * elemOrderLine.setAttribute("Action",
								 * "CANCEL");
								 * 
								 * elemOrderLine.setAttribute("OrderLineKey",orderLineKey);
								 * elemOrderLine.setAttribute("ModificationReasonCode",
								 * "STSBOCANCEL");
								 * elemOrderLine.createChild("OrderLineTranQuantity").setAttribute("OrderedQty",
								 * containerQty); } orderLineKeyTemp =
								 * orderLineKey; orderLinekeyMap.put(i,
								 * orderLineKeyTemp); }
								 */
							}
						}

						YFCNodeList ndlOrderLinesToCancel = docCancelOrder
								.getElementsByTagName("OrderLine");
						if (ndlOrderLinesToCancel.getLength() > 0) {
							// **OMS-792*
							if (!YFCObject.isVoid(ItemDetailForCancel)) {
								env.setTxnObject("EnvCancelQty",
										ItemDetailForCancel);
								// System.out.println("OMS-792 - Set Map in
								// Environment variable");
							}// **OMS-792*

							// OMS-793 changes - Tax and charge Recalculation
							if(log.isDebugEnabled()){
								log.debug("VSIRecalculateChargeAndTax input XML -"
											+ XMLUtil
													.getXMLString(docCancelOrder
															.getDocument()));
							}
							Document outXML = modifyTaxAndCharge(env,
									docCancelOrder.getDocument());
							if(log.isDebugEnabled()){
								log.debug("VSIRecalculateChargeAndTax output XML -"
											+ XMLUtil.getXMLString(outXML));
							}
							// End

							api.invoke(env, "changeOrder", outXML);
						}

					}

				}
			} else {
				throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
						"order No. not found");
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR", "Parse Error");
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
					"Client Creation Error");
		} catch (YFSException e) {
			e.printStackTrace();
			if (bThrowCancellationError) {
				throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
						"Qty. passed is greater than allowed qty.");
			} else {
				throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
						"Random Error");
			}

		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// End vsiTransferOrderShipmentCreate
	
	
	
	
	
	
	//SOS-8 :
	
	public void resolveHoldOnCancellation(YFSEnvironment env, String customerPoNo,String entCode,String orderHeaderKey){
		

		try{
		Document getOrderListIn = XMLUtil.createDocument("Order");
		Element eleOrderElement = getOrderListIn.getDocumentElement();
		Element eleOrderLineElement = getOrderListIn.createElement(VSIConstants.ELE_ORDER_LINE);
		eleOrderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		eleOrderElement.appendChild(eleOrderLineElement);
		eleOrderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
		eleOrderElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		Map<String, String> orderLinkeyNumber = new HashMap<String, String>();
		
		
	 	api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/JDACrShipOrderList.xml");
		Document Output = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListIn);
		env.clearApiTemplates();
	
		   Element rootelement = Output.getDocumentElement();
		   rootelement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
    	   Document outpuDocument = api.executeFlow(env, "VSIParseCreateShipment", Output);
    	    NodeList orderlineElementList = outpuDocument.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		  
    	    int Length =orderlineElementList.getLength();
    	    if(log.isDebugEnabled()){
    	    	log.verbose("Length is "+Length);
    	    }
    		int count=0;		
			
     		for (int i = 0; i < orderlineElementList.getLength(); i++) {
     	
	    	   
			Element OrderLineElement = (Element) outpuDocument.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);	
			String CustomerPONo=OrderLineElement.getAttribute("CustomerPONo");
		
			String orderLineKey=OrderLineElement.getAttribute("OrderLineKey");
			String MaxLineSts=OrderLineElement.getAttribute("MaxLineStatus");
			String MinLineSts=OrderLineElement.getAttribute("MinLineStatus");
			
			if(customerPoNo.equalsIgnoreCase(CustomerPONo))
				{
				if(log.isDebugEnabled()){
					log.verbose("Looop Number "+i);
				}
				String HOLDEligible ="False";
				if  (MaxLineSts.equalsIgnoreCase("2160.400")&&MinLineSts.equalsIgnoreCase("2160.400"))					
				{
					
					if(log.isDebugEnabled()){
						log.verbose("All quantity of this line is in Store Received Status.i.e" +
								orderLineKey+"It will have hold only if other lines belongs to customerPO" +
								CustomerPONo+"are not in Store Received status" );
					}
					   
            		count++;
				    HOLDEligible="TRUE";
				    orderLinkeyNumber.put(orderLineKey, HOLDEligible);
				    
				}
				double iminLine = 0.0;
            	iminLine = Double.valueOf(MinLineSts);
            	if(log.isDebugEnabled()){
            		log.verbose("iminLine"+iminLine);
            	}
            	if  (MaxLineSts.equalsIgnoreCase("2160.400")&&iminLine < 2160.400)					
				{
            		
            		if(log.isDebugEnabled()){
            			log.verbose("All quantity of this line is not in Store Received Status.i.e" +
            					orderLineKey+"It will have hold.It belongs to customerPO" +
            					CustomerPONo+"Due to this line hold will apply on other Store received lines too" ); 
            		}
            		
            	    HOLDEligible="TRUE";
				    orderLinkeyNumber.put(orderLineKey, HOLDEligible);
				    
				}
            	
            	
            	if  (MaxLineSts.equalsIgnoreCase("9000")&&MinLineSts.equalsIgnoreCase("2160.400"))					
				{
            		
            		if(log.isDebugEnabled()){
            			log.verbose("All quantity of this line is not in Store Received Status some are in " +
            					"Cancelled status.i.e" +
            					orderLineKey+"It can have hold if all other lines are not in store received status" +
            					".It belongs to customerPO" +
            					CustomerPONo );
            		}
					   
            		count++;
            		HOLDEligible="TRUE";
            		orderLinkeyNumber.put(orderLineKey, HOLDEligible);
				}
            	
            	if  (MaxLineSts.equalsIgnoreCase("9000")&&MinLineSts.equalsIgnoreCase("9000"))					
				{
            		if(log.isDebugEnabled()){
            			log.verbose("All quantity of this line is in cancelled Status.i.e" +
            					orderLineKey+"It will never have hold.It belongs to customerPO" +
            					CustomerPONo );
            		}
		            		count++;
            		
				}
            	
				if (Length==count)
				{
				
					if(log.isDebugEnabled()){
						log.verbose("Resolving hold before once all the lines are recievd or in cancelled status");
					}
					ResolveHold(env, orderHeaderKey,orderLinkeyNumber);
				
				}
				
			}
		}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	
	}
	
	
	
	
	private Document ResolveHold(YFSEnvironment env, String orderHeaderKey, Map<String, String> orderLinkeyNumber) 
			throws ParserConfigurationException, YIFClientCreationException,
					YFSException, RemoteException{
				// TODO Auto-generated method stub
				Document createHoldInput = XMLUtil.createDocument("Order");
				Element orderElement = createHoldInput.getDocumentElement();
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
				orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
				orderElement.setAttribute("Override", "Y");
		         Element OrderlinesElement = createHoldInput
				.createElement("OrderLines");

				orderElement.appendChild(OrderlinesElement);

				for (Entry<String, String> s : orderLinkeyNumber.entrySet()) 
				{
					String orderLinkey = s.getKey();
					Element OrderlineElement = createHoldInput
					.createElement("OrderLine");
					OrderlinesElement.appendChild(OrderlineElement);
				    OrderlineElement.setAttribute("OrderLineKey",orderLinkey);
					Element OrderHoldTypesElement = createHoldInput
					.createElement("OrderHoldTypes");
				    OrderlineElement.appendChild(OrderHoldTypesElement);
					Element OrderHoldTypeElement = createHoldInput
					.createElement("OrderHoldType");
		            OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
							"STORE_RECEIVE_HOLD");
					OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT,
							"Store Received OrderLine Hold");
					OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1300");

					OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
				}
				api = YIFClientFactory.getInstance().getApi();
				Document Document=api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);
				return Document;
			}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/*
	 * OMS-793 This method will calculate the tax and charge for each line based
	 * on the quantities received and append order line tag with the updated
	 * details to the input changeOrder XML.
	 */
	public Document modifyTaxAndCharge(YFSEnvironment env, Document inXML)
			throws Exception {

		if(log.isDebugEnabled()){
			log
			.debug("================Inside VSIRecalculateChargeAndTax================================");
		}

		if (null != inXML.getDocumentElement()
				&& null != inXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)) {
			NodeList orderLineList = inXML
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

			Element orderLinesEle = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_ORDER_LINES).item(0);

			boolean modifyFlag = false;
			int orderLineLength = orderLineList.getLength();

			if (null != orderLineList && orderLineLength > 0) {

				for (int i = 0; i < orderLineLength; i++) {

					Element eleOrderLine = (Element) orderLineList.item(i);
					String orderedQty = eleOrderLine.getAttribute("OrderedQty");

					/*
					 * If ordered Qty is "0" no need to re calculate taxes and
					 * charges as OMS will set all the values to '0'. Below
					 * logic will get executed only if Action is set to Cancel
					 * at line level
					 */
					if (!"0".equals(orderedQty)
							&& "CANCEL".equals(eleOrderLine
									.getAttribute("Action"))) {

						if(log.isDebugEnabled()){
							log
							.debug("================Action is Cancel at line level and line is getting partially cancelled==========");
						}
						
						String olk = eleOrderLine.getAttribute("OrderLineKey");
						Document getOrderLineList = getOrderLineList(env, olk);

						if (null != getOrderLineList) {
							Element eleOrderLineOp = (Element) getOrderLineList
									.getElementsByTagName(
											VSIConstants.ELE_ORDER_LINE)
									.item(0);
							String originalOrderedQty = eleOrderLineOp
									.getAttribute("OrderedQty");

							Element orderLineEle = XMLUtil.createElement(inXML,
									"OrderLine", "");
							Element lineChargesEle = XMLUtil.createElement(
									inXML, "LineCharges", "");
							Element lineTaxesEle = XMLUtil.createElement(inXML,
									"LineTaxes", "");

							NodeList lineChargeList = eleOrderLineOp
									.getElementsByTagName("LineCharge");
							NodeList lineTaxList = eleOrderLineOp
									.getElementsByTagName("LineTax");

							if (lineChargeList != null
									&& lineChargeList.getLength() > 0) {

								if(log.isDebugEnabled()){
									log
									.debug("================Line Charge Exists for OrderLinekey "
											+ olk + "===========");
								}

								for (int j = 0; j < lineChargeList.getLength(); j++) {

									Element lineChargeEle = XMLUtil
											.createElement(inXML, "LineCharge",
													"");
									Element elelineCharge = (Element) lineChargeList
											.item(j);
									String originalLineCharge = elelineCharge
											.getAttribute("ChargePerLine");

									Double dOriginalLineCharge = Double
											.valueOf(originalLineCharge);
									Double dOriginalOrderedQty = Double
											.valueOf(originalOrderedQty);
									Double dOrderedQty = Double
											.valueOf(orderedQty);
									Double chargePerUnit = (dOriginalLineCharge / dOriginalOrderedQty);

									// Calculate new charge per line value based
									// on the cancelled qty
									Double chargePerUnitRoundOff = (double) Math
											.round(chargePerUnit * 100) / 100;
									Double newChargePerLine = chargePerUnitRoundOff
											* dOrderedQty;

									lineChargeEle
											.setAttribute(
													"ChargeName",
													elelineCharge
															.getAttribute("ChargeName"));
									lineChargeEle
											.setAttribute(
													"ChargeCategory",
													elelineCharge
															.getAttribute("ChargeCategory"));
									lineChargeEle.setAttribute("ChargePerLine",
											newChargePerLine.toString());
									lineChargesEle.appendChild(lineChargeEle);
								}
								modifyFlag = true;
							}

							if (lineTaxList != null
									&& lineTaxList.getLength() > 0) {

								if(log.isDebugEnabled()){
									log
									.debug("================Line Tax Exists for OrderLinekey "
											+ olk + "===========");
								}

								for (int j = 0; j < lineTaxList.getLength(); j++) {

									Element lineTaxEle = XMLUtil.createElement(
											inXML, "LineTax", "");
									Element elelineTax = (Element) lineTaxList
											.item(j);
									String originalTax = elelineTax
											.getAttribute("Tax");

									Double dOriginalTax = Double
											.valueOf(originalTax);
									Double dOriginalOrderedQty = Double
											.valueOf(originalOrderedQty);
									Double dOrderedQty = Double
											.valueOf(orderedQty);
									Double taxPerUnit = (dOriginalTax / dOriginalOrderedQty);

									// Calculate new charge per line value based
									// on the cancelled qty
									Double taxPerUnitRoundOff = (double) Math
											.round(taxPerUnit * 100) / 100;
									Double newTax = taxPerUnitRoundOff
											* dOrderedQty;

									lineTaxEle.setAttribute("TaxName",
											elelineTax.getAttribute("TaxName"));
									lineTaxEle.setAttribute("Tax", newTax
											.toString());
									lineTaxesEle.appendChild(lineTaxEle);
								}
								modifyFlag = true;
							}

							/*
							 * If line taxes and charges are present for the
							 * respective order line key in changeOrder XML, add
							 * a order line tag with Action as Modify with the
							 * updated charges and taxes.
							 */
							if (modifyFlag) {
								// orderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
								// olk);
								// orderLineEle.setAttribute("Action",
								// "MODIFY");
								eleOrderLine.appendChild(lineChargesEle);
								eleOrderLine.appendChild(lineTaxesEle);
								// orderLinesEle.appendChild(orderLineEle);
							}

						}

					}
				}
			}
		}

		return inXML;
	}

	/*
	 * Invoke getOrderLineList API to fetch line charges and taxes
	 */
	public Document getOrderLineList(YFSEnvironment env, String orderLineKey)
			throws YIFClientCreationException, YFSException, RemoteException,
			ParserConfigurationException {

		Document orderLine = XMLUtil.createDocument("OrderLine");
		Element OrderLineEle = orderLine.getDocumentElement();
		OrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
				orderLineKey);

		env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,
				"global/template/api/VSIGetLineChargeAndTax.xml");
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,
				orderLine);
		env.clearApiTemplates();

		NodeList orderLineList = outDoc
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

		if (null != orderLineList) {
			return outDoc;
		}

		return null;

	}

	private String getBufferPeriod(YFSEnvironment env) {
		String bufferPrd = null;
		try {
			Document docForGetCommonCodeList = XMLUtil
					.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

			Element eleCommonCode = docForGetCommonCodeList
					.getDocumentElement();
			eleCommonCode.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,
					"VSI.com");
			eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,
					"BUFFER_DAYS_BOSTS");

			Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,
					VSIConstants.API_COMMON_CODE_LIST, docForGetCommonCodeList);

			if (docAfterGetCommonCodeList != null) {
				Element eleOutCommonCode = (Element) docAfterGetCommonCodeList
						.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE)
						.item(0);
				if (eleOutCommonCode != null) {
					bufferPrd = eleOutCommonCode
							.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return bufferPrd;
	}

	private Integer stsLeadDays(String shipNode, YFSEnvironment env)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		String extnLeadTime = "";
		int leadTime = 10;
		int buffertime = 4;
		Document getOrgListIpDoc = XMLUtil
				.createDocument(VSIConstants.ELE_ORGANIZATION);
		getOrgListIpDoc.getDocumentElement().setAttribute(
				VSIConstants.ATTR_ORG_CODE, shipNode);
		Document getOrgListOpDoc = VSIUtils.invokeAPI(env,
				"global/template/api/VSIGetOrgListTemplate.xml",
				"getOrganizationList", getOrgListIpDoc);
		if (getOrgListOpDoc != null) {
			NodeList orgNL = getOrgListOpDoc
					.getElementsByTagName("Organization");
			if (orgNL.getLength() > 0) {
				Element eleOrg = (Element) orgNL.item(0);
				Element extnEle = (Element) eleOrg.getElementsByTagName("Extn")
						.item(0);
				if (extnEle != null) {
					extnLeadTime = extnEle.getAttribute("ExtnLeadTime");

				}
			}
		}
		if (!YFCObject.isVoid(extnLeadTime)) {
			leadTime = Integer.parseInt(extnLeadTime);
		}
		String bufferPrd = getBufferPeriod(env);
		if (!YFCObject.isVoid(extnLeadTime)) {
			buffertime = Integer.parseInt(bufferPrd);
		}

		int LeadDays = buffertime + leadTime;
		return LeadDays;

	}

	private Integer sthLeadDays(String shipNode, YFSEnvironment env,
			int shipDays) throws ParserConfigurationException, YFSException,
			RemoteException, YIFClientCreationException {

		Calendar calendar = Calendar.getInstance();

		// calendar.add(Calendar.DATE, 7);
		String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.format(calendar.getTime());
		String strhrs = currentTime.substring(11, 13);
		int inthrs = Integer.parseInt(strhrs);
		if (inthrs >= 18) {
			shipDays++;
		}

		return shipDays;

	}

	private void createCallCTR(YFSEnvironment env, String orderHeaderKey,
			String customerPoNo, Double maxReqAmt) throws Exception {
		String maxRequestAmount = String.valueOf(maxReqAmt);
		String ctrSeqId = VSIDBUtil.getNextSequence(env,
				VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
		Document chargeTransactionRequestInput = XMLUtil
				.createDocument("ChargeTransactionRequestList");
		Element ctrListEle = chargeTransactionRequestInput.getDocumentElement();
		ctrListEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKey);
		Element ctrEle = chargeTransactionRequestInput
				.createElement("ChargeTransactionRequest");
		ctrListEle.appendChild(ctrEle);
		ctrEle.setAttribute("MaxRequestAmount", maxRequestAmount);
		ctrEle.setAttribute("ChargeTransactionRequestKey", customerPoNo);
		ctrEle.setAttribute("ChargeTransactionRequestId",
				ChargeTransactionRequestId);

		if(log.isDebugEnabled()){
    		log.debug("chargeTransactionRequestInput:"+XMLUtil.getXMLString(chargeTransactionRequestInput));
		}

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,
				chargeTransactionRequestInput);

	}

	private void createHold(YFSEnvironment env, String sOHK)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {

		Document createHoldInput = XMLUtil.createDocument("Order");
		Element orderElement = createHoldInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");

		Element OrderHoldTypesElement = createHoldInput
				.createElement("OrderHoldTypes");
		orderElement.appendChild(OrderHoldTypesElement);

		Element OrderHoldTypeElement = createHoldInput
				.createElement("OrderHoldType");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
				"AUTH_PENDING");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT,
				"Pending Auth");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");

		OrderHoldTypesElement.appendChild(OrderHoldTypeElement);

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);

	}

	private void sendEmail(YFSEnvironment env, String sCustomerPONO)
			throws ParserConfigurationException {

		Document sendEmailInput = XMLUtil.createDocument("Order");
		Element orderElement = sendEmailInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, sCustomerPONO);

		try {
			VSIUtils.invokeService(env, "VSI_BOSTS_ShipConfirmEmail",
					sendEmailInput);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

	}

	/**
	 * Sort the given node list in ascending order based on line total.
	 * 
	 * @param ListEle
	 * @param elementNodeList
	 */
	public void sortNodeListInAscOrderBasedOnLineTotal(Element ListEle,
			NodeList elementNodeList) {
		if(log.isDebugEnabled()){
			log.debug("Begin :- Method -- sortNodeListInAscOrderBasedOnLineTotal");
		}
		ArrayList<Element> elementArrayList = new ArrayList<Element>();
		// append nodeList to Array list.
		int ndListLen = elementNodeList.getLength();
		for (int ordLine = 0; ordLine < ndListLen; ordLine++) {
			Element eleOrderLine = (Element) elementNodeList.item(ordLine);
			Element eleLineTotal = (Element) eleOrderLine.getElementsByTagName(
					"LineOverallTotals").item(0);
			String strLineTotal = eleLineTotal.getAttribute("LineTotal");
			eleOrderLine.setAttribute("LineTotal", strLineTotal);
			elementArrayList.add(eleOrderLine);
		}
		// sorting the array list in ascending order based on line total.
		Collections.sort(elementArrayList, new SortNodelistBasedOnLineTotal());
		// Append sorted NodeList to parent element.
		for (Element element : elementArrayList) {
			ListEle.appendChild(element);
		}
		if(log.isDebugEnabled()){
			log.debug("End :- Method - sortNodeListInAscOrderBasedOnLineTotal");
		}
	}

	/**
	 * sort the given node list in ascending order
	 * 
	 */
	class SortNodelistBasedOnLineTotal implements Comparator<Element> {
		/**
		 * This method sort the given node list in ascending order based on line
		 * total..
		 */
		@Override
		public int compare(Element elem1, Element elem2) {
			if(log.isDebugEnabled()){
				log.debug("Begin :- Method - compare");
			}
			Double dbLineTotal1 = 0.0;
			Double dbLineTotal2 = 0.0;
			if (elem1.hasAttribute("LineTotal")) {
				dbLineTotal1 = Double.parseDouble(elem1
						.getAttribute("LineTotal"));
				dbLineTotal2 = Double.parseDouble(elem2
						.getAttribute("LineTotal"));
			}
			if(log.isDebugEnabled()){
				log.debug("End :- Method - compare");
			}
			return dbLineTotal1.compareTo(dbLineTotal2);
		}
	}

	public void setProperties(Properties props) {
		this.props = props;
	}

}