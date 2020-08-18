package com.vsi.oms.api.order;

import java.rmi.RemoteException;

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

public class VSIGetAddtnlDetailsForReturnInvoice implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetAddtnlDetailsForReturnInvoice.class);
	String strDirectStoreNo = "";
	public Document vsiGetAddtnlDetails(YFSEnvironment env, Document invoiceXML)
			throws YFSException {
		if(log.isDebugEnabled()){
			log.info("**Inside VSIGetAddtnlDetailsForReturnInvoice ***");
		}
		Element invoiceHeader = (Element) invoiceXML.getElementsByTagName(
				"InvoiceHeader").item(0);
		
		

		if (!YFCObject.isVoid(invoiceHeader)) {
			
			// ARE-83
			String reference1 = invoiceHeader.getAttribute("Reference1");
			if(!YFCCommon.isVoid(reference1) && reference1.contains("_"))
			{
				String[] reference1Array = reference1.split("_");
				invoiceHeader.setAttribute("Reference1",reference1Array[0]);
			}
			Element returnOrderEle = (Element) invoiceHeader
					.getElementsByTagName("Order").item(0);
			String returnOHK = returnOrderEle.getAttribute("OrderHeaderKey");
			//added for BOPUS project - do not send returns associated with exchanges to sales audit
			Document inOrdDoc;
			try {
				inOrdDoc = XMLUtil.createDocument("Order");
			

			Element inOrderEle = inOrdDoc.getDocumentElement();
			inOrderEle.setAttribute("OrderHeaderKey", returnOHK);
			
			env.setApiTemplate("getOrderList", "global/template/api/VSI_getOrderDetails_Return.xml");
			Document returnOrderDetails = VSIUtils.invokeAPI(env, "global/template/api/VSI_getOrderList_Return.xml", "getOrderList", inOrdDoc);

			

			env.clearApiTemplate("getOrderList");
			
			if(!YFCObject.isVoid(returnOrderDetails)){
				
				Element eleExchangeOrder = (Element)returnOrderDetails.getDocumentElement().getElementsByTagName("ExchangeOrder").item(0);
				if(!YFCObject.isVoid(eleExchangeOrder)){
					invoiceHeader.setAttribute("HasExchangeOrder", "Y");
				}
			}
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YIFClientCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Document docCommonCodeInput;
			try {
				docCommonCodeInput = getCommonCodeListInputForCodeType("DEFAULT", "RETURN_DIRECT_STORE");
				Document docCommonCode = VSIUtils.getCommonCodeList(env, docCommonCodeInput);
				Element eleCommonCodeList = docCommonCode.getDocumentElement();
				if(!YFCObject.isVoid(eleCommonCodeList)){
					Element eleCommonCode = (Element)eleCommonCodeList.getElementsByTagName("CommonCode").item(0);
					if(!YFCObject.isVoid(eleCommonCode)){
						String strValue = eleCommonCode.getAttribute("CodeValue");
						if(!YFCObject.isVoid(strValue)){
							strDirectStoreNo = strValue;
						}
					}
				}
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//changes End
			String strSalesOrderHeaderKey = invoiceHeader
					.getAttribute("DerivedFromOrderHeaderKey");

			if (!YFCObject.isVoid(strSalesOrderHeaderKey)
					&& !YFCObject.isVoid(returnOHK)) {

				try {

					String derivedOLK = "";
					String salesOLK = "";
					String salesShipNode = "";
					
					// Get the Sales Order Invoice Details
					Document getOrdInvLstIpDoc = XMLUtil
							.createDocument("OrderInvoice");
					Element orderInvElement = getOrdInvLstIpDoc
							.getDocumentElement();
					Element orderEle = getOrdInvLstIpDoc.createElement("Order");
					orderInvElement.appendChild(orderEle);
					orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
							strSalesOrderHeaderKey);
					Document salesOrdInvoiceOpDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrdInvListTemplate.xml", "getOrderInvoiceList", getOrdInvLstIpDoc);

					if (salesOrdInvoiceOpDoc != null) {

						// get the list of return lines
						NodeList returnLinesNL = invoiceHeader
								.getElementsByTagName("OrderLine");
						int returnLinesCt = returnLinesNL.getLength();

						Element salesOrdInRtEle = salesOrdInvoiceOpDoc
								.getDocumentElement();

						NodeList salesInvoiceNL = salesOrdInRtEle
								.getElementsByTagName("OrderInvoice");
						int saleInvoiceCt = salesInvoiceNL.getLength();
						Element saleInvoiceEle = null;
						String strLineType = null;
						String strOrderType = null;
						String strEnteredBy = null;
						String entryType = null;
						String strCustomerPONo = null;
						String salesOrdDeliveryMethod = null;
					
						Element eleSalesOrder = (Element)salesOrdInRtEle
						.getElementsByTagName("Order").item(0);
						//OMS-1608 : Start
						if(!YFCObject.isVoid(eleSalesOrder))
						{
							strOrderType = eleSalesOrder.getAttribute("OrderType");
							strEnteredBy = eleSalesOrder.getAttribute("EnteredBy");
							entryType = eleSalesOrder.getAttribute("EntryType");
						}
						//OMS-1608 : End
						if(!YFCObject.isVoid(strOrderType))
							returnOrderEle.setAttribute("OrderType", strOrderType);
						invoiceHeader.setAttribute("InvoiceCreationReason", "Return");
						if("STORE".equals(entryType))
						{
							returnOrderEle.setAttribute("EnteredBy", "6101");
							invoiceHeader.setAttribute("ReturnSeqIdentifier", "6101");
						}
						//if(!YFCObject.isVoid(strEnteredBy))
						else
						{
							returnOrderEle.setAttribute("EnteredBy", strEnteredBy);
							invoiceHeader.setAttribute("ReturnSeqIdentifier", strEnteredBy);
						}
			
						
						if(!YFCObject.isVoid(entryType))
							invoiceHeader.setAttribute("OriginLocation", entryType);
						
						for (int j = 0; j < returnLinesCt; j++) {
							Element returnLineEle = (Element) returnLinesNL
									.item(j);
							derivedOLK = returnLineEle
									.getAttribute("DerivedFromOrderLineKey");
							String extnTransactionNo = "";
							boolean gotDetails = false;
							for (int i = 0; i < saleInvoiceCt; i++) {
								saleInvoiceEle = (Element) salesInvoiceNL
										.item(i);

								NodeList saleLineNL = saleInvoiceEle
										.getElementsByTagName(ELE_ORDER_LINE);
								int saleLineCt = saleLineNL.getLength();
								Element saleLineEle = null;
								Element extnEle = null;
								Element shipmentEle = null;
								
								for (int k = 0; k < saleLineCt; k++) {
									saleLineEle = (Element) saleLineNL.item(k);
									salesOLK = saleLineEle
											.getAttribute(ATTR_ORDER_LINE_KEY);
									if (salesOLK.equalsIgnoreCase(derivedOLK)) {
										salesShipNode = saleLineEle
												.getAttribute(ATTR_SHIP_NODE);
										strLineType = saleLineEle
										.getAttribute("LineType");
										
										String strReshipParentLineKey = saleLineEle
												.getAttribute("ReshipParentLineKey");
										if(!YFCCommon.isVoid(strReshipParentLineKey))
										{
											returnLineEle.setAttribute(
													"ReshipParentLineKey",
													strReshipParentLineKey);
										}
										if(!YFCObject.isVoid(strLineType))
											returnLineEle.setAttribute(
												"LineType",
												strLineType);
										strCustomerPONo = saleLineEle
										.getAttribute("CustomerPONo");
										if(!YFCObject.isVoid(strLineType))
											returnLineEle.setAttribute(
												"CustomerPONo",
												strCustomerPONo);
										
										Element eleSalesPriceInfo = (Element)saleLineEle.getElementsByTagName("LinePriceInfo").item(0);
										
										Element eleSalesLineTax = null;
										NodeList nlSalesLineTax = saleLineEle.getElementsByTagName("LineTax");
										if(nlSalesLineTax.getLength() > 0)
										eleSalesLineTax = (Element)saleLineEle.getElementsByTagName("LineTax").item(0);
										String strTax = "";
										Element eleSalesLineTaxExtn = null;
										if(!YFCObject.isVoid(eleSalesLineTax)){
											eleSalesLineTaxExtn = (Element) eleSalesLineTax.getElementsByTagName("Extn").item(0);
											strTax = eleSalesLineTax.getAttribute("Tax");
										}
										String strSalesTaxType = "";
										String strSalesTaxableFlag = "" ;
										
										if(!YFCObject.isVoid(eleSalesPriceInfo)){
											//Fix for OMS 419 - Taxable Flag always returned N
											//strSalesTaxType = eleSalesPriceInfo.getAttribute("TaxType");
											strSalesTaxableFlag = eleSalesPriceInfo.getAttribute("TaxableFlag");
										}
										//Fix for OMS 419 - Taxable Flag always returned N
										if(!YFCObject.isVoid(eleSalesLineTaxExtn)){
											
											strSalesTaxType = eleSalesLineTaxExtn.getAttribute("TaxType");
											
										}
										
										//double d = Double.parseDouble(strTax);
										Element returnLineDetail = (Element)returnLineEle.getParentNode();
										if(!YFCObject.isVoid(returnLineDetail)){
											Element returnLineTax = (Element)returnLineDetail.getElementsByTagName("LineTax").item(0);
											if(!YFCObject.isVoid(returnLineTax)){
												String strReturnTax = returnLineTax.getAttribute("Tax");
												if(!YFCObject.isVoid(strReturnTax)){
													double d = Double.parseDouble(strReturnTax);
													if(d != 0.0){
														Element eleReturnPriceInfo = (Element)returnLineEle.getElementsByTagName("LinePriceInfo").item(0);
														eleReturnPriceInfo.setAttribute("TaxableFlag", "Y");
													}
												}
											}
										}
//										if(!YFCObject.isVoid(strSalesTaxType) && !YFCObject.isVoid(strSalesTaxableFlag)){	
//											
//											if(!YFCObject.isVoid(eleReturnPriceInfo)){
//												eleReturnPriceInfo.setAttribute("TaxType", strSalesTaxType);
//												if("N".equalsIgnoreCase(strSalesTaxableFlag)){
//													
//
//
//												}
//												
//											}
//										}
										
										if(!YFCObject.isVoid(salesShipNode))
										{
											returnLineEle.setAttribute(
												"OriginalStoreNo",
												salesShipNode);
										}
										else
										{
											returnLineEle.setAttribute(
													"OriginalStoreNo",
													strEnteredBy);
										}
										extnEle = (Element) saleInvoiceEle
												.getElementsByTagName("Extn")
												.item(0);
										if (extnEle
												.hasAttribute("ExtnTransactionNo"))
											extnTransactionNo = extnEle
													.getAttribute("ExtnTransactionNo");
										returnLineEle.setAttribute(
												"OriginalTransactionNo",
												extnTransactionNo);
										shipmentEle = (Element) saleInvoiceEle
												.getElementsByTagName(
														"Shipment").item(0);
										if(shipmentEle != null){
											
											Element shipNodeEle = (Element) shipmentEle
													.getElementsByTagName(
															"ShipNode").item(0);

											salesOrdDeliveryMethod = shipmentEle.getAttribute("DeliveryMethod");

											if((!YFCCommon.isVoid(shipNodeEle) && 
													"PICK".equals(salesOrdDeliveryMethod) && !"Call Center".equals(entryType)))
											{
												shipNodeEle.setAttribute("ShipnodeKey", "6101");
												invoiceHeader.setAttribute("ReturnSeqIdentifier", "6101");
											}
											else if(!YFCCommon.isVoid(shipNodeEle) && "Call Center".equals(entryType))
											{
												shipNodeEle.setAttribute("ShipnodeKey", "6101");
												//invoiceHeader.setAttribute("ReturnSeqIdentifier", "8001");
											}
											//changes for bopus
											if(!YFCObject.isVoid(strDirectStoreNo)){
												shipmentEle.setAttribute(
														"ShipNode",
														strDirectStoreNo);
											}//changes end
										Node cpyShipNde = invoiceXML
												.importNode(shipmentEle, true);
										returnLineEle.appendChild(cpyShipNde);
										}
										gotDetails = true;
										break;

									}
								}
								if (gotDetails)
									break;
							}
						}
						
						Element firstROLineEle = (Element) invoiceXML.getElementsByTagName(ELE_ORDER_LINE).item(0);
						Element firstShipmentEle = (Element) firstROLineEle.getElementsByTagName(ELE_SHIPMENT).item(0);
						if(firstShipmentEle != null){
							Node cpyShipNde = invoiceXML.importNode(firstShipmentEle, true);
							invoiceHeader.appendChild(cpyShipNde);
						}
						
					}
				} catch (RemoteException e) {
					
					e.printStackTrace();
				} catch (YIFClientCreationException e) {
					
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					
					e.printStackTrace();
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug("Printing invoiceXML \n"+XMLUtil.getXMLString(invoiceXML));
		}
		return invoiceXML;
	}
	
	private static Document getCommonCodeListInputForCodeType(String sOrgCode,
			String codeType) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeType", codeType);
		return docOutput;
	}

}
