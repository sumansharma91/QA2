package com.vsi.oms.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.api.VSIWholesaleSendInvoice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIProcessWholesaleOrderOnWaveCancel implements VSIConstants{

	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessWholesaleOrderOnWaveCancel.class);

	public Document processWholesaleWaveCancel(YFSEnvironment env, Document inXML) throws Exception{

		if(log.isDebugEnabled()){
			log.debug("VSIProcessWholesaleOrderOnWaveCancel.processWholesaleWaveCancel() " + SCXmlUtil.getString(inXML));
		}
		
		Element eleOrder = inXML.getDocumentElement();
		String strOrderType = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_TYPE);
		if(strOrderType.equalsIgnoreCase(WHOLESALE)){
			
			String enterpriseCode = SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE);
			String sendInvoiceMode = getSendInvoiceMode(env, enterpriseCode);
			if(sendInvoiceMode.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_ORDER)){
				
				String orderHeaderKey = SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_HEADER_KEY);
				if(!YFCObject.isVoid(orderHeaderKey)){
					
					Document getShipmentListInXML = SCXmlUtil.createDocument(ELE_SHIPMENT);
					Element eleShipment = getShipmentListInXML.getDocumentElement();
					SCXmlUtil.setAttribute(eleShipment, ATTR_ORDER_HEADER_KEY, orderHeaderKey);
					Element eleOrderBy = SCXmlUtil.createChild(eleShipment, ELE_ORDER_BY);
					Element eleAttribute = SCXmlUtil.createChild(eleOrderBy, ELE_ATTRIBUTE);
					SCXmlUtil.setAttribute(eleAttribute, ATTR_NAME, ATTR_SHIPMENT_KEY);
					SCXmlUtil.setAttribute(eleAttribute, ATTR_DESC, FLAG_Y);
					
					Document getShipmentListOutXML = VSIUtils.invokeService(env, SERVICE_WHOLESALE_GET_SHIPMENT_LIST, getShipmentListInXML);
					
					Element eleShipments = getShipmentListOutXML.getDocumentElement();
					if(eleShipments.hasChildNodes()){
						
						Boolean isInvoiceComplete = true;
						eleShipment = SCXmlUtil.getChildElement(eleShipments, ELE_SHIPMENT);
						NodeList nlOrderLine = eleShipment.getElementsByTagName(ELE_ORDER_LINE);
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
						
						if(log.isDebugEnabled())
							log.debug("isInvoiceComplete: " + isInvoiceComplete);
						
						if(isInvoiceComplete){
							
							// If rest of the order is completely invoiced, and the last release is being cancelled
							// get the details of the last invoice and call the VSIWholesaleSendInvoice.sendInvoiceInformation() method
							
							NodeList nlOrderInvoice = eleShipments.getElementsByTagName(ELE_ORDER_INVOICE);
							if(nlOrderInvoice.getLength() > 0){
								
								Element eleOrderInvoice = (Element) nlOrderInvoice.item(0);
								String orderInvoiceKey = SCXmlUtil.getAttribute(eleOrderInvoice, ATTR_ORDER_INVOICE_KEY);
								Document getOrderInvoiceDetailsInXML = SCXmlUtil.createDocument(ELE_GET_ORDER_INVOICE_DTLS);
								Element eleGetOrderInvoiceDetails = getOrderInvoiceDetailsInXML.getDocumentElement();
								SCXmlUtil.setAttribute(eleGetOrderInvoiceDetails, ATTR_INVOICE_KEY, orderInvoiceKey);
								Document getOrderInvoiceDetailsOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_DETAILS, getOrderInvoiceDetailsInXML);
								
								Element eleExtn = SCXmlUtil.getXpathElement(
										getOrderInvoiceDetailsOutXML.getDocumentElement(), "/InvoiceDetail/InvoiceHeader/Extn");
								
								if(log.isDebugEnabled())
									log.debug("/InvoiceDetail/InvoiceHeader/Extn" + SCXmlUtil.getString(eleExtn));
								
								if(!YFCObject.isVoid(eleExtn)){
									
									// Set this flag because the VSIWholesaleSendInvoice.sendInvoiceInformation() method needs it
									SCXmlUtil.setAttribute(eleExtn, ATTR_EXTN_IS_LAST_INVOICE_FOR_ORDER, FLAG_Y);
									
									VSIUtils.invokeService(env, "VSIWholeSaleSendInvoice", getOrderInvoiceDetailsOutXML);
								}
							}
						}
					}
				}
			}
		}
		
		return inXML;
	}
	
	// Get SendInvoiceMode from Common Code
		private String getSendInvoiceMode(YFSEnvironment env, String strEnterpriseCode) throws Exception {

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
}