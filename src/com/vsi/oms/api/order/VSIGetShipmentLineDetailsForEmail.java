package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetShipmentLineDetailsForEmail {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetShipmentLineDetailsForEmail.class);

	public Document vsiGetShippedLineDetails(YFSEnvironment env, Document inXML)
			throws YFSException {
		Document emailXML = null;
		try {
			
			if(log.isDebugEnabled()){
				log.debug("vsiGetShippedLineDetails input"+ SCXmlUtil.getString(inXML));
			}
			
			if (inXML != null) {
				NodeList shipmentLineNL = inXML
						.getElementsByTagName("ShipmentLine");
				int noOfShpmts = shipmentLineNL.getLength();
				String ordStatus = null;
				String ohk = null;
				boolean isSingleShipment = false;
				double dlineTotal = 0;
				double dlineGrandTotal = 0;
				double dlineTax = 0;
				int noOfShipment = 0;
				Element shipLineEle = (Element) shipmentLineNL.item(0);
				ohk = shipLineEle.getAttribute("OrderHeaderKey");
				String strCommitFlag = "N";

				/* Start***Stamping TrackingNo and CarrierServiceCode*** */
				String sTrackingNo = XMLUtil.getAttributeFromXPath(inXML,
						"Shipment/@TrackingNo");
				
				String sDeliveryMethod = XMLUtil.getAttributeFromXPath(inXML,
						"Shipment/@DeliveryMethod");
				
				if (!YFCObject.isVoid(sDeliveryMethod)) {
					env.setTxnObject("eDeliveryMethod", sDeliveryMethod);
				}
				
				String sCarrierServiceCode = XMLUtil.getAttributeFromXPath(
						inXML, "Shipment/@CarrierServiceCode");
				
				String sSCAC = XMLUtil.getAttributeFromXPath(
						inXML, "Shipment/@SCAC");
				
				String sSCACandServiceKey = XMLUtil.getAttributeFromXPath(
						inXML, "Shipment/@ScacAndServiceKey");
				
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

				if (!YFCObject.isVoid(sTrackingNo)) {
					env.setTxnObject("eTrackingValue", sTrackingNo);
				}

				if (!YFCObject.isVoid(sCarrierServiceCode)) {
					env.setTxnObject("eCarrierServiceCode", sCarrierServiceCode);
				}
				
				if (!YFCObject.isVoid(sSCAC)) {
					env.setTxnObject("eSCAC", sSCAC);
				}
				
				

				/* End***Stamping TrackingNo and CarrierServiceCode*** */

				// Done for Coupon commit jira 589
				// checking of any order line is cancelled, so that we can set a
				// flag for not to send commit.
				/*OMS-1540 : Coupon Commit Changes: Start*/
				String strCouponCommit="N";
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
							
							strCouponCommit="Y";
							//strCommitFlag = "Y";
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
				//System.out.print("strCommitFlag is " + strCommitFlag);
				// end for jira 589

				Document getShipmentListInput = XMLUtil
						.createDocument("Shipment");
				Element shipmentElement = getShipmentListInput
						.getDocumentElement();
				shipmentElement.setAttribute(
						VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);

				Document getShipListOp = VSIUtils.invokeAPI(env,
						"getShipmentList", getShipmentListInput);
				if (getShipListOp != null) {
					NodeList shipmentsNL = getShipListOp
							.getElementsByTagName("Shipment");
					noOfShipment = shipmentsNL.getLength();
				}

				for (int t = 0; t < noOfShpmts; t++) {
					shipLineEle = (Element) shipmentLineNL.item(t);
					String shippedQty = shipLineEle.getAttribute("Quantity");
					String olk = shipLineEle.getAttribute("OrderLineKey");
					ohk = shipLineEle.getAttribute("OrderHeaderKey");
					Document getOrderLineListIpDoc = XMLUtil
							.createDocument("OrderLine");
					getOrderLineListIpDoc.getDocumentElement().setAttribute(
							"OrderLineKey", olk);
					Document getOrderLineListOpDoc = VSIUtils
							.invokeAPI(
									env,
									"global/template/api/VSIGetShippedLineDetailsTemplate.xml",
									"getOrderLineList", getOrderLineListIpDoc);
					if (getOrderLineListOpDoc != null) {
						Element ordEle = (Element) getOrderLineListOpDoc
								.getElementsByTagName("Order").item(0);
						Element orderLinesEle = (Element) getOrderLineListOpDoc
								.getElementsByTagName("OrderLines").item(0);

						int totOrdLines = 0;
						String stotOrdLines = orderLinesEle
								.getAttribute("TotalNumberOfRecords");
						if (stotOrdLines != null
								&& !stotOrdLines.trim().equalsIgnoreCase(""))
							totOrdLines = Integer.parseInt(stotOrdLines);
						if (ordEle != null) {
							ordStatus = ordEle.getAttribute("Status");
							Element ipOrderLinesEle = (Element) getOrderLineListOpDoc
									.getElementsByTagName("OrderLine").item(0);
							String ordQty = ipOrderLinesEle
									.getAttribute("OrderedQty");
							ipOrderLinesEle.setAttribute("OrderedQty",
									shippedQty);

							String shipnode = ipOrderLinesEle
									.getAttribute("ShipNode");
							Element shipNodeEle = (Element) ipOrderLinesEle
									.getElementsByTagName("Shipnode").item(0);
							String lineType = ipOrderLinesEle
									.getAttribute("LineType");
							// ARE-340 : Changing condition to not execute this logic for Ship To Home orders : BEGIN
							/*
							if (!(lineType.equalsIgnoreCase("SHIP_TO_HOME"))
									|| (ordStatus != null
											&& ordStatus
													.equalsIgnoreCase("Shipped") && noOfShpmts == noOfShipment)) {
							*/
							if (!(lineType.equalsIgnoreCase("SHIP_TO_HOME"))
									&& (ordStatus != null
											&& ordStatus
													.equalsIgnoreCase("Shipped") && noOfShpmts == noOfShipment)) {
								isSingleShipment = true;
								break;
							}
							// ARE-340 : Changing condition to not execute this logic for Ship To Home orders : END
							if (t == 0) {
								emailXML = XMLUtil
										.getDocumentForElement(ordEle);
								orderLinesEle = (Element) emailXML
										.getElementsByTagName("OrderLines")
										.item(0);
								Element rootEle = emailXML.getDocumentElement();
								if (shipNodeEle != null) {
									Node cpyShipNde = emailXML.importNode(
											shipNodeEle, true);
									rootEle.appendChild(cpyShipNde);

								}
								//OMS-1930:Start
								Document docGetShipNodeList = XMLUtil.createDocument(VSIConstants.ELE_SHIP_NODE);
								Element eleOrg = docGetShipNodeList.getDocumentElement();
								eleOrg.setAttribute(VSIConstants.ATTR_SHIP_NODE, shipnode);
								Document docGetShipNodeListOut = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_SHIPNODE_LIST, VSIConstants.API_GET_SHIPNODE_LIST, docGetShipNodeList);
								String strShipNodeDesc=null;
								String strShipNodeDesc1=null;
								 if(!YFCObject.isVoid(docGetShipNodeListOut))
								{
								Element eleShipNode=(Element) docGetShipNodeListOut.getElementsByTagName(VSIConstants.ELE_SHIP_NODE).item(0);
								if(null!=eleShipNode)
								{
									strShipNodeDesc=eleShipNode.getAttribute(VSIConstants.ATTR_DESCRIPTION);
								}
								if(!YFCObject.isVoid(strShipNodeDesc) && strShipNodeDesc.contains(","))
								 { 
								     strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc.substring(0, strShipNodeDesc.lastIndexOf(","));
								 }
								 else if (!YFCObject.isVoid(strShipNodeDesc) && strShipNodeDesc.contains(" "))
								 {
									 strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc.substring(0, strShipNodeDesc.lastIndexOf(" "));
								 }
								 else
									 strShipNodeDesc1=VSIConstants.EMAIL_HEADER+strShipNodeDesc;
								}
								////OMS-1930:End
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
										Node copyCalNode = emailXML.importNode(
												calEle, true);
										//OMS-1930:Start
										((Element) copyCalNode).setAttribute(VSIConstants.ATTR_ORG_NAME,strShipNodeDesc1);
										//OMS-1930:End
										rootEle.appendChild(copyCalNode);
									}

								}

							}
							Element lineOverallTotalEle = (Element) ipOrderLinesEle
									.getElementsByTagName("LineOverallTotals")
									.item(0);
							String lineGrandTotal = lineOverallTotalEle
									.getAttribute("LineTotal");
							String lineTotal = lineOverallTotalEle
									.getAttribute("ExtendedPrice");
							String lineTax = lineOverallTotalEle
									.getAttribute("Tax");
							double dordQty = Double.parseDouble(ordQty);
							double dshippedQty = Double.parseDouble(shippedQty);
							if (dordQty == dshippedQty) {
								dlineTotal = dlineTotal
										+ Double.parseDouble(lineTotal);
								dlineGrandTotal = dlineGrandTotal
										+ Double.parseDouble(lineGrandTotal);
								dlineTax = dlineTax
										+ Double.parseDouble(lineTax);

							} else {
								String unitPrice = lineOverallTotalEle
										.getAttribute("UnitPrice");
								double dunitPrice = Double
										.parseDouble(unitPrice);
								double dExtPrice = dunitPrice * dshippedQty;
								lineOverallTotalEle.setAttribute(
										"ExtendedPrice",
										Double.toString(dExtPrice));
								dlineTotal = dlineTotal + dExtPrice;
								double dLineGrandTotal = Double
										.parseDouble(lineGrandTotal);
								dLineGrandTotal = (dLineGrandTotal / dordQty)
										* dshippedQty;
								dlineGrandTotal = dlineGrandTotal
										+ dLineGrandTotal;
								double dLineTax = Double.parseDouble(lineTax);
								dLineTax = (dLineTax / dordQty) * dshippedQty;
								dlineTax = dlineTax + dLineTax;

							}

							orderLinesEle = (Element) emailXML
									.getElementsByTagName("OrderLines").item(0);
							ipOrderLinesEle.removeChild(ordEle);
							Node copyNode = emailXML.importNode(
									ipOrderLinesEle, true);
							orderLinesEle.appendChild(copyNode);
							Element overAllTotEle = (Element) emailXML
									.getElementsByTagName("OverallTotals")
									.item(0);

							overAllTotEle.setAttribute("GrandTotal",
									Double.toString(dlineGrandTotal));
							overAllTotEle.setAttribute("GrandTax",
									Double.toString(dlineTax));
							overAllTotEle.setAttribute("LineSubTotal",
									Double.toString(dlineTotal));

						}
					}

				}
				if (isSingleShipment && ohk != null) {
					Document inDoc = XMLUtil.createDocument("Order");
					inDoc.getDocumentElement().setAttribute("OrderHeaderKey",
							ohk);
					emailXML = VSIUtils.invokeService(env,
							"VSIGetAddntlDataForEmail", inDoc);
					Element overAllTotEle = (Element) emailXML
							.getElementsByTagName("OverallTotals").item(0);
					String shippingCharge = overAllTotEle
							.getAttribute("HdrCharges");
					overAllTotEle.setAttribute("GrandShippingCharges",
							shippingCharge);
				} else {

					if (noOfShipment == 1) {
						Element overAllTotEle = (Element) emailXML
								.getElementsByTagName("OverallTotals").item(0);
						String shippingCharge = overAllTotEle
								.getAttribute("HdrCharges");
						String shippingTax = overAllTotEle
								.getAttribute("HdrTax");
						overAllTotEle.setAttribute("GrandShippingCharges",
								shippingCharge);
						String grandTax = overAllTotEle
								.getAttribute("GrandTax");
						if (shippingTax != null
								&& !shippingTax.trim().equalsIgnoreCase("")) {

							double dshippingTax = Double
									.parseDouble(shippingTax);
							if (dshippingTax > 0) {
								double dTotalTax = Double.parseDouble(grandTax);
								dTotalTax = dTotalTax + dshippingTax;
								overAllTotEle.setAttribute("GrandTax",
										Double.toString(dTotalTax));
							}

						}

					}

				}

				emailXML = setEmailQty(env, emailXML); /* OMS-791 */
				emailXML = truncateXML(env, emailXML); /* OMS-808 */
				
				Element rootEle = emailXML.getDocumentElement();
				rootEle.setAttribute("Commit", strCommitFlag);

			}// inXML null check

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// log.info(XMLUtil.getXMLString(emailXML));
		return emailXML;

	}

	/**
	 * OMS-791
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 **/
	public Document setEmailQty(YFSEnvironment env, Document outDoc)
			throws IllegalArgumentException, IllegalAccessException {

		Map<String, String> object = (Map<String, String>) env
				.getTxnObject("EnvShipQty");

		if (!YFCObject.isVoid(object)) {
			NodeList orderLineList = outDoc
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

			for (int i = 0; i < orderLineList.getLength(); i++) {
				Element orderLineEle = (Element) orderLineList.item(i);
				String sOrderedQty = orderLineEle.getAttribute("OrderedQty");
				Element itemEle = (Element) orderLineEle.getElementsByTagName(
						"Item").item(0);
				String sItem = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
				String sQty = "";
				if (!YFCObject.isVoid(object.get(sItem))) {
					sQty = object.get(sItem).toString();

					if (Integer.parseInt(sOrderedQty) < Integer.parseInt(sQty)) {
						orderLineEle.setAttribute("EmailQty", sOrderedQty);
						object.put(
								sItem,
								Integer.toString(Integer.parseInt(sQty)
										- Integer.parseInt(sOrderedQty)));
					} else {
						if (!"0".equals(sQty)) {
							orderLineEle.setAttribute("EmailQty", sQty);
							object.put(sItem, "0");
						}
					}
				}

			}
		}

		return outDoc;
	}


	/**
	 * OMS-808
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ParserConfigurationException 
	 **/	
	 public Document truncateXML(YFSEnvironment env, Document outDoc)
			throws IllegalArgumentException, IllegalAccessException, ParserConfigurationException {

		//Map<String, String> object = (Map<String, String>) env
		 ArrayList<String> object = (ArrayList<String>) env 
				.getTxnObject("EnvItemsShipped");
				
		String customerPoNo = (String) env.getTxnObject("customerPoNo");
							
		if (!YFCObject.isVoid(object) && !YFCObject.isVoid(customerPoNo)) {
			Document EmailDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			
			Element orderEleIP = outDoc.getDocumentElement();
		  
			Element  orderEle = (Element)EmailDoc.importNode(orderEleIP, true);
			EmailDoc.appendChild(orderEle);
				 
			NodeList OrderLines = EmailDoc.getElementsByTagName("OrderLines");
			orderEle.removeChild( OrderLines.item(0));
		  
			Element eOrderLines = EmailDoc.createElement("OrderLines");
			orderEle.appendChild(eOrderLines);
			 
			for (int i = 0; i < orderLineList.getLength(); i++) {
				Element orderLineEle = (Element) orderLineList.item(i);
				String sOrderedQty = orderLineEle.getAttribute("OrderedQty");
				String sCustomerPoNo = orderLineEle.getAttribute("CustomerPONo");
				
				if (sCustomerPoNo.equals(customerPoNo)) {
				
					Element itemEle = (Element) orderLineEle.getElementsByTagName(
							"Item").item(0);
					String sItem = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
					String value = sItem.concat(",").concat(Integer.toString(Double.valueOf(sOrderedQty).intValue()));
					
					if (object.contains(value)) {					

						Element orderLineEleIP = (Element) outDoc.getElementsByTagName("OrderLine").item(i); 
						Element orderLineEleOP = (Element)EmailDoc.importNode(orderLineEleIP, true);
						eOrderLines.appendChild(orderLineEleOP);
						
						object.remove(object.indexOf(value));
					}	
				}

			}
			return EmailDoc;
		}
		else {
			return outDoc;
		}
		
	}

}