package com.vsi.oms.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

import com.yantra.interop.japi.YIFClientCreationException;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessReceipt {
	


	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessReceipt.class);
	
	public void vsiProcessReceipt(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIProcessReceipt================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

			Element rootElement = inXML.getDocumentElement();
			String orderHeaderKey = rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String receivingNode = rootElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			String enterpriseCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			
			Document startReceiptInput = XMLUtil.createDocument("Receipt");
			Element receiptEle = startReceiptInput.getDocumentElement();
			receiptEle.setAttribute("DocumentType", VSIConstants.RETURN_DOCUMENT_TYPE);
			receiptEle.setAttribute("OpenReceiptFlag", VSIConstants.FLAG_Y);
			receiptEle.setAttribute("ReceivingNode", receivingNode);
			Element shipmentEle = startReceiptInput.createElement("Shipment");
			shipmentEle.setAttribute("DocumentType", "0003");
			shipmentEle.setAttribute("EnterpriseCode", enterpriseCode);
			shipmentEle.setAttribute("OrderHeaderKey", orderHeaderKey);
			shipmentEle.setAttribute("ReceivingNode", receivingNode);
			receiptEle.appendChild(shipmentEle);
			
			if(log.isDebugEnabled()){
				log.info("================Invoking startReceipt API .... ================================");
			}
			
			
			
			if(receivingNode != null && receivingNode != ""){
				if(log.isDebugEnabled()){
					log.debug("Printing Input XML To startReceipt:" + XmlUtils.getString(startReceiptInput));
				}
			Document startReceiptOutput = VSIUtils.invokeAPI(env,"global/template/api/startReceiptVSIOutput.xml", "startReceipt",
					startReceiptInput);
			if(log.isDebugEnabled()){
				log.debug("Printing Output XML from startReceipt:" + XmlUtils.getString(startReceiptOutput));
			}

			
			
			if(startReceiptOutput != null ){
				
			String receiptHrdKey = 	startReceiptOutput.getDocumentElement().getAttribute("ReceiptHeaderKey");
			String shipmentKey = 	startReceiptOutput.getDocumentElement().getAttribute("ShipmentKey");
			/*
			Document getLinesToReceiveIp = XMLUtil.createDocument("GetLinesToReceive");
			Element getLineToReceiveEle = getLinesToReceiveIp.getDocumentElement();
			getLineToReceiveEle.setAttribute("DocumentType", "0003");
			getLineToReceiveEle.setAttribute("ReceivingNode", receivingNode);
			getLineToReceiveEle.setAttribute("ReceiptHeaderKey", receiptHrdKey);
			getLineToReceiveEle.setAttribute("ShipmentKey", shipmentKey);
			Element shipmentReceiveEle = getLinesToReceiveIp.createElement("Shipment");
			shipmentReceiveEle.setAttribute("DocumentType", "0003");
			shipmentReceiveEle.setAttribute("EnterpriseCode", enterpriseCode);
			shipmentReceiveEle.setAttribute("OrderHeaderKey", orderHeaderKey);
			getLineToReceiveEle.appendChild(shipmentReceiveEle);
			
			log.info("================Invoking getLinesToReceive API .... ================================");
			
			
			log.debug("Printing Input XML To getLinesToReceive:" + XmlUtils.getString(getLinesToReceiveIp));
			
			Document getLinesToReceiveOP = VSIUtils.invokeAPI(env,"global/template/api/getLinesToReceiveVSIOutput.xml", "getLinesToReceive",
					getLinesToReceiveIp);

			log.debug("Printing Output XML from getLinesToReceive:" + XmlUtils.getString(getLinesToReceiveOP));
			
			if(getLinesToReceiveOP != null){
				*/
				NodeList nlReceivableLine = rootElement.getElementsByTagName("OrderLine");
				if(nlReceivableLine.getLength() > 0){
					Document receiveOrderIp = XMLUtil.createDocument("Receipt");
					Element eleReceipt = receiveOrderIp.getDocumentElement();
					eleReceipt.setAttribute("DocumentType", "0003");
					eleReceipt.setAttribute("ReceivingNode", receivingNode);
					eleReceipt.setAttribute("ReceiptHeaderKey", receiptHrdKey);
					Element eleReceiptLines = receiveOrderIp.createElement("ReceiptLines");
					eleReceipt.appendChild(eleReceiptLines);
					for(int i = 0; i < nlReceivableLine.getLength(); i++){
						Element eleReceivableLine = (Element)nlReceivableLine.item(i);
						if(Double.parseDouble(eleReceivableLine.getAttribute("OrderedQty")) > 0){
						Element eleReceiptLine = receiveOrderIp.createElement("ReceiptLine");
						eleReceiptLine.setAttribute("Quantity", eleReceivableLine.getAttribute("OrderedQty"));
						eleReceiptLine.setAttribute("OrderLineKey", eleReceivableLine.getAttribute("OrderLineKey"));
						eleReceiptLines.appendChild(eleReceiptLine);
						}
						
					}
					
					if(log.isDebugEnabled()){
						log.info("================Invoking receiveOrder API .... ================================");


						log.debug("Printing Input XML To receiveOrder:" + XmlUtils.getString(receiveOrderIp));
					}
					
					Document receiveOrderOP = VSIUtils.invokeAPI(env, "receiveOrder",
							receiveOrderIp);
					
					if(log.isDebugEnabled()){
						log.debug("Printing Output XML from receiveOrder:" + XmlUtils.getString(receiveOrderOP));
					}
					
					Document closeReceiptIp = XMLUtil.createDocument("Receipt");
					Element eleCloseReceipt = closeReceiptIp.getDocumentElement();
					eleCloseReceipt.setAttribute("DocumentType", "0003");
					eleCloseReceipt.setAttribute("ReceivingNode", receivingNode);
					eleCloseReceipt.setAttribute("ReceiptHeaderKey", receiptHrdKey);
					
					
					
					if(log.isDebugEnabled()){
						log.info("================Invoking closeReceipt API .... ================================");

						log.debug("Printing Input XML To closeReceipt:" + XmlUtils.getString(closeReceiptIp));
					}
					
					Document closeReceiptOP = VSIUtils.invokeAPI(env, "closeReceipt",
							closeReceiptIp);
					
					if(log.isDebugEnabled()){
						log.debug("Printing Output XML from closeReceipt:" + XmlUtils.getString(closeReceiptOP));

						log.info("================Order Received & Receipt Closed Successfully ! ================================");
					}
					
					Document emailIpDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
					Element emailIpDocEle = emailIpDoc.getDocumentElement();
					emailIpDocEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
					if(log.isDebugEnabled()){
						log.info("================Invoking VSISendRestockEmail ================================"+XMLUtil.getXMLString(emailIpDoc));
					}
					VSIUtils.invokeService(env, "VSISendRestockEmail", emailIpDoc);
					if(log.isDebugEnabled()){
						log.info("================Successfully sent Restock only Email ================================");
					}
					
				}
				
			
			
			
			}
			
			
			}else {
				if(log.isDebugEnabled()){
				log.info("****Receiving Node is null ! Cannot Receive the Return Order !*****");
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


}
