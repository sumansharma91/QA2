package com.vsi.oms.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author IBM, Inc. This Class is implemented for wholesale orders to Update
 *         Trailer Information over Invoice Table
 */

public class VSIUpdateTrailerInInvoice implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIUpdateTrailerInInvoice.class);

	public Document updateTrailerInfrm(YFSEnvironment env, Document inXML) throws Exception {

		if(log.isDebugEnabled()){
			log.debug("Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside updateTrailerInfrm================================");
		}
		
		Element eleOrderInvoice = inXML.getDocumentElement();

		String strShipmentKey = eleOrderInvoice.getAttribute(ATTR_SHIPMENT_KEY);
		String strOrderInvoiceKey = eleOrderInvoice.getAttribute(ATTR_ORDER_INVOICE_KEY);

		if (!YFCCommon.isVoid(strShipmentKey) && !YFCCommon.isVoid(strOrderInvoiceKey)) {

			// Create Input for getShipmentList
			Document docShipmentIn = SCXmlUtil.createDocument(ELE_SHIPMENT);
			Element shipmentEle = docShipmentIn.getDocumentElement();
			shipmentEle.setAttribute(ATTR_SHIPMENT_KEY, strShipmentKey);

			if (log.isDebugEnabled()) {
				log.debug("Input to getShipmentList in class VSIUpdateTrailerInInvoice :"
						+ XMLUtil.getXMLString(docShipmentIn));
			}

			// ShipmentList Output
			Document shipmentList = VSIUtils.invokeService(env, SERVICE_WHOLESALE_GET_SHIPMENT_LIST, docShipmentIn);

			if (log.isDebugEnabled()) {
				log.debug("Output to getShipmentList in class VSIUpdateTrailerInInvoice"
						+ XMLUtil.getXMLString(shipmentList));
			}

			if (!YFCCommon.isVoid(shipmentList) && shipmentList.getDocumentElement().hasChildNodes()) {
				Element eleShipment = SCXmlUtil.getChildElement(shipmentList.getDocumentElement(), ELE_SHIPMENT);

				String strOrderType = eleShipment.getAttribute(ATTR_ORDER_TYPE);

				if (strOrderType.equalsIgnoreCase(WHOLESALE))
					
				{
					Boolean isInvoiceComplete = true;
					Element shipmentExtnEle = (Element) eleShipment.getElementsByTagName(ELE_EXTN).item(0);
					String strExtnIsLastShipmentInTrailer = shipmentExtnEle.getAttribute(ATTR_EXTN_LAST_TRAILER_SHIP);
					String strTrailerNo = SCXmlUtil.getAttribute(shipmentExtnEle, ATTR_EXTN_UNIQUE_TRAILER_ID);
					Element eleOrder = (Element) shipmentList.getElementsByTagName(ELE_ORDER).item(0);
					
						NodeList nlOrderLine = eleOrder.getElementsByTagName(ELE_ORDER_LINE);
						for(int i = 0; i < nlOrderLine.getLength(); i++){
							
							Element eleOrderLine = (Element) nlOrderLine.item(i);
							Double dblOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine, ATTR_ORD_QTY);
							if(dblOrderedQty > 0){
								String strLineInvoiceComplete = SCXmlUtil.getAttribute(eleOrderLine, ATTR_INVOICE_COMPLETE);
								if(strLineInvoiceComplete.equalsIgnoreCase(FLAG_N)){
									isInvoiceComplete = false;
									break;
								}
							}
						}

					
					// Create Input for ChangeOrderInvoice

					Document docOrderInvoiceIn = SCXmlUtil.createDocument(ELE_ORDER_INVOICE);
					Element orderInvoiceEle = docOrderInvoiceIn.getDocumentElement();
					orderInvoiceEle.setAttribute(ATTR_ORDER_INVOICE_KEY, strOrderInvoiceKey);

					Element extnEle = docOrderInvoiceIn.createElement(ELE_EXTN);
					orderInvoiceEle.appendChild(extnEle);

					if (!YFCCommon.isVoid(strTrailerNo))
						
						extnEle.setAttribute(ATTR_EXTN_TRAILER_NO, strTrailerNo);
					if (!YFCCommon.isVoid(strExtnIsLastShipmentInTrailer))
						extnEle.setAttribute(ATTR_EXTN_SEND_TRAILER_INVOICE, strExtnIsLastShipmentInTrailer);
					
					if(isInvoiceComplete){
						SCXmlUtil.setAttribute(extnEle, ATTR_EXTN_IS_LAST_INVOICE_FOR_ORDER, FLAG_Y);
					}

					if (log.isDebugEnabled()) {
						log.debug("Input to chnageOrderInvoice in class VSIUpdateTrailerInInvoice"
								+ XMLUtil.getXMLString(docOrderInvoiceIn));
					}

					Document docChangeOrderInvoice = VSIUtils.invokeService(env, SERVICE_CHANGE_ORDER_INVOICE,
							docOrderInvoiceIn);

					if (log.isDebugEnabled()) {
						log.debug("Output to chnageOrderInvoice in class VSIUpdateTrailerInInvoice"
								+ XMLUtil.getXMLString(docChangeOrderInvoice));
					}

				}
			}
		}

		return inXML;

	}
}
