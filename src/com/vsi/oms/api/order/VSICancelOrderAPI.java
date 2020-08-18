package com.vsi.oms.api.order;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

public class VSICancelOrderAPI {

	private YFCLogCategory log = YFCLogCategory.instance(VSICancelOrderAPI.class);
	
	public Document cancelOrder(YFSEnvironment env, Document inXML) throws YFSException, ParserConfigurationException {
		
		if(log.isDebugEnabled()){
			log.info("================Inside VSICancelOrderAPI================================");
			log.info("Printing Input XML :" + XMLUtil.getXMLString(inXML));		
		}
		Element rootElement = inXML.getDocumentElement();
		String strDocType = rootElement.getAttribute("DocumentType");
		String strOrderNo = rootElement.getAttribute("OrderNo");
		String strAction = rootElement.getAttribute("Action");
		String strEnterpriseCode = rootElement.getAttribute("EnterpriseCode");
		String strModReasonCode = rootElement.getAttribute("ModificationReasonCode");
		String strModReasonTxt = rootElement.getAttribute("ModificationReasonText");
		String strContainerNo = rootElement.getAttribute("ContainerNo");
		
		if (!VSIUtils.isNullOrEmpty(strAction) && "CANCEL".equalsIgnoreCase(strAction)) {
			
			if (!VSIUtils.isNullOrEmpty(strModReasonCode) && "AUTOCANCEL".equalsIgnoreCase(strModReasonCode)) {
				
				if ("WO".equalsIgnoreCase(strOrderNo.substring(0, 2)))
					strOrderNo = strOrderNo.substring(0, 10).toUpperCase();
				
				rootElement.setAttribute("OrderNo", strOrderNo);
				rootElement.setAttribute("Override", "Y");
				
				try {
					
					if(log.isDebugEnabled()){
						log.info("Cancellation due to Auto Cancel");
						log.info("**changeOrder input XML :\n"+XMLUtil.getXMLString(inXML));
					}
					VSIUtils.invokeAPI(env, "", "changeOrder", inXML);					
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (!VSIUtils.isNullOrEmpty(strModReasonCode) && "LOSTINTRANSIT".equalsIgnoreCase(strModReasonCode)) {
			
				if(log.isDebugEnabled()){
					log.info("Cancellation due to Lost in transit");
				}
				
				Document getShipmentListIpDoc = XMLUtil.createDocument("Shipment");
				Element shipmentEle = getShipmentListIpDoc.getDocumentElement();
				Element containersEle = getShipmentListIpDoc.createElement("Containers");
				Element containerEle = getShipmentListIpDoc.createElement("Container");
				containerEle.setAttribute("ContainerNo", strContainerNo);
				containersEle.appendChild(containerEle);
				shipmentEle.appendChild(containersEle);
	
				Document getShipmentListOutDoc = null;
	
				try {
					
					if(log.isDebugEnabled()){
						log.info("**getShipmentList input XML :\n"+XMLUtil.getXMLString(getShipmentListIpDoc));
					}
					
					getShipmentListOutDoc = VSIUtils.invokeAPI(env, "global/template/api/getShipmentListTemplate.xml", "getShipmentList", getShipmentListIpDoc);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				if(log.isDebugEnabled()){
					log.info("**getShipmentList output XML :\n"+XMLUtil.getXMLString(getShipmentListOutDoc));
				}
				
				Document changeOrderIpDoc = changeOrderInputXML(getShipmentListOutDoc, strModReasonCode, strModReasonTxt, strEnterpriseCode, strDocType);
				
				if (null != changeOrderIpDoc) {
					try {
						if(log.isDebugEnabled()){
							log.info("**changeOrder input XML :\n"+XMLUtil.getXMLString(changeOrderIpDoc));
						}
						VSIUtils.invokeAPI(env, "", "changeOrder", changeOrderIpDoc);					
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		return null;
		
	}
	
	public Document changeOrderInputXML(Document inDoc, String reasonCode, String reasonTxt, String enterpriseCode, String documentType) throws ParserConfigurationException {
		
		Document changeOrderIpDoc = null;
		NodeList shipmentLineNL = inDoc.getElementsByTagName("ShipmentLine");
		if (null != shipmentLineNL) {
			
			String strOrderKey = "";
			changeOrderIpDoc = XMLUtil.createDocument("Order");
			Element changeOrderEle = changeOrderIpDoc.getDocumentElement();
			changeOrderEle.setAttribute("EnterpriseCode", enterpriseCode);
			changeOrderEle.setAttribute("DocumentType", documentType);
			changeOrderEle.setAttribute("ModificationReasonCode", reasonCode);
			changeOrderEle.setAttribute("ModificationReasonText", reasonTxt);
			changeOrderEle.setAttribute("Override", "Y");
			
			Element orderLinesEle = changeOrderIpDoc.createElement("OrderLines");
			changeOrderEle.appendChild(orderLinesEle);

			for (int i=0; i< shipmentLineNL.getLength(); i++) {
				Element shipmentLineEle = (Element) shipmentLineNL.item(i);

				String strQuantity = shipmentLineEle.getAttribute("Quantity");
				int shpQty=Integer.parseInt(strQuantity);

				NodeList orderLineNL = shipmentLineEle.getElementsByTagName("OrderLine");
				if (null != orderLineNL) {
					
					Element orderLineEle = changeOrderIpDoc.createElement("OrderLine");
					orderLineEle.setAttribute("Action", "CANCEL");
					//orderLineEle.setAttribute("OrderedQty", strQuantity);
					
					for (int j=0; j < orderLineNL.getLength(); j++) {
						Element orderLineEle1 = (Element) orderLineNL.item(j);
						strOrderKey = orderLineEle1.getAttribute("ChainedFromOrderHeaderKey");
						String strOrdLineqty = orderLineEle1.getAttribute("OrderedQty");
                        int ordQty=Integer.parseInt(strOrdLineqty);
                        int canClAty=ordQty-shpQty;
                        String lineOrdQty=Integer.toString(canClAty);
                        
    					orderLineEle.setAttribute("OrderedQty", lineOrdQty);
    					orderLineEle.setAttribute("FromStatus", "2160.200");
    					


                        

						String strChainedOrderLineKey = orderLineEle1.getAttribute("ChainedFromOrderLineKey");
						
						if (VSIUtils.isNullOrEmpty(strOrderKey)) {
							strOrderKey = orderLineEle1.getAttribute("OrderHeaderKey");
							orderLineEle.setAttribute("OrderLineKey", orderLineEle1.getAttribute("OrderLineKey"));
						}
						else {
							orderLineEle.setAttribute("OrderLineKey", strChainedOrderLineKey);							
						}		
						orderLinesEle.appendChild(orderLineEle);
					}	
				}
				changeOrderEle.setAttribute("OrderHeaderKey", strOrderKey);
			}			
		}
		return changeOrderIpDoc;
	}
}
