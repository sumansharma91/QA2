package com.vsi.oms.api;

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
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIReceiveTransferOrderFromJDA {
	private YFCLogCategory log = YFCLogCategory.instance(VSIReceiveTransferOrderFromJDA.class);
	YIFApi api;
	public void vsiReceiveTransferOrderFromJDA(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIReceiveTransferOrderFromJDA================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{
		Element rootElement = inXML.getDocumentElement();
		Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);

		
		String entCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
		String receivingNode = rootElement.getAttribute(VSIConstants.ATTR_RECEIVING_NODE);
		String customerPoNo = shipmentLineElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		//String productClass = shipmentLineElement.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
		//String unitMeasure = shipmentLineElement.getAttribute(VSIConstants.ATTR_UOM);
		//String dType = shipmentLineElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
		
		
		
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListInput.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
		eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
		Element orderElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
	
		
		Document NewgetOrderListInput = XMLUtil.createDocument("Order");
		Element orderEle = NewgetOrderListInput.getDocumentElement();
		Element orderLineEle = XMLUtil.createElement(NewgetOrderListInput, VSIConstants.ELE_ORDER_LINE, "");
		orderLineEle.setAttribute(VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY, orderHeaderKey);
		orderEle.appendChild(orderLineEle);
	//Calling getOrderList to get the Order no from chainedOrderHeaderKey
		api = YIFClientFactory.getInstance().getApi();
		Document NewOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,NewgetOrderListInput);
		Element newOrderElement = (Element) NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String transferOrderHeaderKey = newOrderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		
		
		if(statusAttribute.equals("RECEIVED")){
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
		
			Document getShipmentListInput = XMLUtil.createDocument("Shipment");
			Element shipmentElement = getShipmentListInput.getDocumentElement();
			shipmentElement.setAttribute(VSIConstants.ATTR_STATUS, "1400");
			shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, transferOrderHeaderKey);
			Element containersElement = getShipmentListInput.createElement(VSIConstants.ELE_CONTAINERS);
			Element containerElement = getShipmentListInput.createElement(VSIConstants.ELE_CONTAINER);
			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			containersElement.appendChild(containerElement);
			shipmentElement.appendChild(containersElement);
			env.setApiTemplate(VSIConstants.API_GET_SHIPMENT_LIST, "global/template/api/VSIReceiveTransferOrderFromJDA.xml");
			YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
			Document outputDoc = callApi.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);	
			env.clearApiTemplates();
			
			Element shipmentEle = (Element) outputDoc.getElementsByTagName(VSIConstants.ELE_SHIPMENT).item(0);
			String shipmentKey = shipmentEle.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			
			//Receive Order Input
			Document receiveOrderInput = XMLUtil.createDocument("Receipt");
			Element receiptElement = receiveOrderInput.getDocumentElement();
			receiptElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0006");
			receiptElement.setAttribute(VSIConstants.ATTR_RECEIVING_NODE, receivingNode);
			
			Element shipmentReceiveElement = receiveOrderInput.createElement("Shipment");
			shipmentReceiveElement.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, shipmentKey);
			receiptElement.appendChild(shipmentReceiveElement);
			Element receiptLines = XMLUtil.appendChild(receiveOrderInput, receiptElement, VSIConstants.ELE_RECEIPT_LINES, "");
			
			
			
			NodeList containerDetailList = outputDoc.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
			for (int i = 0; i < containerDetailList.getLength(); i++) {
				
				Element containerDetailElement = (Element) containerDetailList.item(i);
				String containerQty = containerDetailElement.getAttribute(VSIConstants.ATTR_QUANTITY);
				String shipmentLineKey = containerDetailElement.getAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY);
				
				Element receiptLine = receiveOrderInput.createElement("ReceiptLine");
				receiptLine.setAttribute(VSIConstants.ATTR_QUANTITY, containerQty);
				receiptLine.setAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY, shipmentLineKey);
				
				receiptLines.appendChild(receiptLine);
			}
			
		
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_RECEIVE_ORDER,receiveOrderInput);
		
		invokeReleaseOrder(env,orderHeaderKey);
		
		
		
		}else
		{
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Input Status error");
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
	
	
	
	public void invokeReleaseOrder(YFSEnvironment env,String orderHeaderKey){
	
		Document releaseOrderInput;
		try {
			releaseOrderInput = XMLUtil.createDocument("ReleaseOrder");
		
		Element releaseOrderElement = releaseOrderInput.getDocumentElement();
		releaseOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
		releaseOrderElement.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID,"VSI_STS");
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_RELEASE_ORDER,releaseOrderInput);
		
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
