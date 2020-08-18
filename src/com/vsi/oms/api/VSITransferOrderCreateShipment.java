package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;







//import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSITransferOrderCreateShipment {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSITransferOrderCreateShipment.class);
	YIFApi api;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public void vsiTransferOrderShipmentCreate(YFSEnvironment env, Document inXML)
			throws Exception {
	
		try{
			Element rootElement = inXML.getDocumentElement();
			Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);
			
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String trackingNumber = rootElement.getAttribute(VSIConstants.ATTR_TRACKING_NO);
			String storeID = rootElement.getAttribute(VSIConstants.ATTR_STORE_ID);
			String shipNode = rootElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			String entCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String transferNumber = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);//JDA Transfer No
			
			String customerPoNo = shipmentLineElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String productClass = shipmentLineElement.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
			String unitMeasure = shipmentLineElement.getAttribute(VSIConstants.ATTR_UOM);
			String dType = shipmentLineElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
			eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
	//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
				api = YIFClientFactory.getInstance().getApi();
				Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
				Element orderElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				Element orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
				if(null != orderElem){
				String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				String lineType=orderLineElem.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				Map<Integer, String> orderLinekeyMap = new HashMap<Integer, String>();
				String orderLineKeyTemp = null;
				
				if(lineType.equals("SHIP_TO_STORE")){
					//System.out.println("LineType Ship to store");
				Document NewgetOrderListInput = XMLUtil.createDocument("Order");
				Element orderEle = NewgetOrderListInput.getDocumentElement();
				Element orderLineEle = XMLUtil.createElement(NewgetOrderListInput, VSIConstants.ELE_ORDER_LINE, "");
				orderLineEle.setAttribute(VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY, orderHeaderKey);
				orderEle.appendChild(orderLineEle);
			//Calling getOrderList to get the Order no from chainedOrderHeaderKey
				api = YIFClientFactory.getInstance().getApi();
				Document NewOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,NewgetOrderListInput);
				Element newOrderElement = (Element) NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				String transferOrderNo = newOrderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			
				String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
				if(statusAttribute.equals("SHIPPED")){
					
					Document createShipmentInput = XMLUtil.createDocument("Shipment");
					Element createShipmentrootElement = createShipmentInput.getDocumentElement();
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_STORE_ID, storeID);//*******CHECK
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_TRACKING_NO, trackingNumber);
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_CONFIRM_SHIP, "Y");	
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_SHIP_CUST_PO_NO, customerPoNo);
					createShipmentrootElement.setAttribute(VSIConstants.ATTR_PICK_TICKET_NO, transferNumber);//Storing the JDA transferNo in PickticketNo
		  Element createShipmentLinesElement = XMLUtil.appendChild(createShipmentInput, createShipmentrootElement, VSIConstants.ELE_SHIPMENT_LINES, "");
		  Element containersElement = XMLUtil.appendChild(createShipmentInput, createShipmentrootElement, VSIConstants.ELE_CONTAINERS, "");
			Element containerElement = XMLUtil.appendChild(createShipmentInput, containersElement, VSIConstants.ELE_CONTAINER, "");
			

			NodeList containerDetailList = inXML.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
			for (int i = 0; i < containerDetailList.getLength(); i++) {
			//	System.out.println("Entering Container #"+i);
				Element containerDetailElement = (Element) containerDetailList.item(i);
				String containerQty = containerDetailElement.getAttribute(VSIConstants.ATTR_QUANTITY);	
			    
				Element shipLineElement = (Element) containerDetailElement.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);
						
				
				Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
				Element getOrderLineListInputrootElement = getOrderLineListInput.getDocumentElement();
				
				String itemID = shipLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
				
				Element itemElement = getOrderLineListInput.createElement(VSIConstants.ELE_ITEM);
				itemElement.setAttribute(VSIConstants.ATTR_ITEM_ID, itemID);
				
				//Element lineElement = getOrderLineListInput.createElement(VSIConstants.ELE_ORDER_LINE);
				String status="1200";
				getOrderLineListInputrootElement.setAttribute(VSIConstants.ATTR_STATUS, status); 
				getOrderLineListInputrootElement.setAttribute("OrderedQty", containerQty); 
				
				Element orderElement = getOrderLineListInput.createElement(VSIConstants.ELE_ORDER);
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
				
				getOrderLineListInputrootElement.appendChild(orderElement);
				getOrderLineListInputrootElement.appendChild(itemElement);
				
				
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSITransferOrderCreateShipment.xml");
				YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
				Document outputDoc = callApi.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
				env.clearApiTemplates();
				
				YFCElement inputDocEle = YFCDocument.getDocumentFor(outputDoc).getDocumentElement();
				YFCNodeList<YFCElement> orderLineCount = inputDocEle.getElementsByTagName("OrderLine");
				
				int noOfLine = orderLineCount.getLength();
				if(noOfLine==0){
					getOrderLineListInputrootElement.setAttribute("OrderedQty", "");
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSITransferOrderCreateShipment.xml");
					outputDoc = callApi.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
					env.clearApiTemplates();
					YFCElement inputDocEle1 = YFCDocument.getDocumentFor(outputDoc).getDocumentElement();
					orderLineCount = inputDocEle1.getElementsByTagName("OrderLine");
					
					 noOfLine = orderLineCount.getLength();
					 //System.out.println("Container Qty not matching #"+i);
					
					
				}

				//System.out.println("bo of lines #"+noOfLine);
				//System.out.println("orderlinelist xml #"+XMLUtil.getXMLString(outputDoc));

				for (int k=0;k<noOfLine;k++){
					Element orderLineElement = (Element) outputDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(k);
					String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					//System.out.println("OrderlineKey"+orderLineKey);
					if (!orderLinekeyMap.containsValue(orderLineKey) && !orderLinekeyMap.containsKey(i) ){
						//System.out.println("entering OrderlineKey"+orderLineKey);
						String primeLineNo = orderLineElement.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
						String subLineNo = orderLineElement.getAttribute(VSIConstants.ATTR_SUB_LINE_NO);
						shipLineElement.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, primeLineNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, subLineNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY, containerQty);
						shipLineElement.setAttribute(VSIConstants.ATTR_UOM, unitMeasure);
						shipLineElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
						shipLineElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
						shipLineElement.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, productClass);
						
						Element shipmentLineEle = XMLUtil.appendChild(createShipmentInput, createShipmentLinesElement, VSIConstants.ELE_SHIPMENT_LINE, "");		
							XMLUtil.copyElement(createShipmentInput, shipLineElement, shipmentLineEle);
							orderLineKeyTemp=orderLineKey;
							orderLinekeyMap.put(i, orderLineKeyTemp);
							//System.out.println("Map #"+orderLinekeyMap);
							//System.out.println("Container Qty not matching #"+i +"Line#" +k);
							//break;
							
					}
					
					
							
				}
				
			
					
					
				}
			XMLUtil.copyElement(createShipmentInput, rootElement, containerElement);
				
			//System.out.println("NISH CREATESHIPMENT"+XMLUtil.getXMLString(createShipmentInput));
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,createShipmentInput);
				}
			
				
			}
	
				else{
					//System.out.println("LineType not Ship to store");
					String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
					if(statusAttribute.equals("SHIPPED")){
						//System.out.println("inside code statuscheck");				
						Document createShipmentInput = XMLUtil.createDocument("Shipment");
						Element createShipmentrootElement = createShipmentInput.getDocumentElement();
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_ORDER_NO, customerPoNo);
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_STORE_ID, storeID);//*******CHECK
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_TRACKING_NO, trackingNumber);
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_CONFIRM_SHIP, "Y");	
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_SHIP_CUST_PO_NO, customerPoNo);
						createShipmentrootElement.setAttribute(VSIConstants.ATTR_PICK_TICKET_NO, transferNumber);//Storing the JDA transferNo in PickticketNo
			  Element createShipmentLinesElement = XMLUtil.appendChild(createShipmentInput, createShipmentrootElement, VSIConstants.ELE_SHIPMENT_LINES, "");
			  Element containersElement = XMLUtil.appendChild(createShipmentInput, createShipmentrootElement, VSIConstants.ELE_CONTAINERS, "");
				Element containerElement = XMLUtil.appendChild(createShipmentInput, containersElement, VSIConstants.ELE_CONTAINER, "");
				
			  

				NodeList containerDetailList = inXML.getElementsByTagName(VSIConstants.ELE_CONTAINER_DETAIL);
				for (int i = 0; i < containerDetailList.getLength(); i++) {
					
					//System.out.println("inside 1 for loop");
					
					Element containerDetailElement = (Element) containerDetailList.item(i);
					String containerQty = containerDetailElement.getAttribute(VSIConstants.ATTR_QUANTITY);	
				
					Element shipLineElement = (Element) containerDetailElement.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);
							
					
					Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
					Element getOrderLineListInputrootElement = getOrderLineListInput.getDocumentElement();
					
				String status="1100.100";
				getOrderLineListInputrootElement.setAttribute(VSIConstants.ATTR_STATUS, status); 
				getOrderLineListInputrootElement.setAttribute("OrderedQty", containerQty); 
					
					String itemID = shipLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
					
					Element itemElement = getOrderLineListInput.createElement(VSIConstants.ELE_ITEM);
					itemElement.setAttribute(VSIConstants.ATTR_ITEM_ID, itemID);
					
					Element orderElement = getOrderLineListInput.createElement(VSIConstants.ELE_ORDER);
					orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, customerPoNo);
										
					getOrderLineListInputrootElement.appendChild(orderElement);
					getOrderLineListInputrootElement.appendChild(itemElement);
					
					
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSITransferOrderCreateShipment.xml");
					YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
					
					Document outputDoc = callApi.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);	
					env.clearApiTemplates();
					
					YFCElement inputDocEle = YFCDocument.getDocumentFor(outputDoc).getDocumentElement();
				YFCNodeList<YFCElement> orderLineCount = inputDocEle.getElementsByTagName("OrderLine");
				int noOfSTHLine = orderLineCount.getLength();
				if(noOfSTHLine==0){
					getOrderLineListInputrootElement.setAttribute("OrderedQty", "");
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSITransferOrderCreateShipment.xml");
					outputDoc = callApi.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
					env.clearApiTemplates();
					YFCElement inputDocEle1 = YFCDocument.getDocumentFor(outputDoc).getDocumentElement();
					YFCNodeList<YFCElement> orderLineCount1 = inputDocEle1.getElementsByTagName("OrderLine");
					
					noOfSTHLine = orderLineCount1.getLength();
				//	System.out.println("if noofSTHLine cond");
					
				}

				//Map<Integer, String> orderLinekeyMap = new HashMap<Integer, String>();

				for (int j=0;j<noOfSTHLine;j++){
					//System.out.println("Inside second for loop J");
					Element orderLineElement = (Element) outputDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(j);
					String orderLineKey = orderLineElement.getAttribute("OrderLineKey");
					if (!orderLinekeyMap.containsValue(orderLineKey)&& !orderLinekeyMap.containsKey(i)){
						
						String primeLineNo = orderLineElement.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
						//System.out.println("primeLineNo"+primeLineNo);
						String subLineNo = orderLineElement.getAttribute(VSIConstants.ATTR_SUB_LINE_NO);
						//System.out.println("subLineNo"+subLineNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, primeLineNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, subLineNo);
						shipLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, customerPoNo);
						//System.out.println("customerPoNo"+customerPoNo);

						shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY, containerQty);
						//System.out.println("containerQty"+containerQty);
						shipLineElement.setAttribute(VSIConstants.ATTR_UOM, unitMeasure);
						shipLineElement.setAttribute(VSIConstants.ATTR_QUANTITY, containerQty);
						
						shipLineElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
						shipLineElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
						shipLineElement.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, productClass);
						
						Element shipmentLineEle = XMLUtil.appendChild(createShipmentInput, createShipmentLinesElement, VSIConstants.ELE_SHIPMENT_LINE, "");		
							XMLUtil.copyElement(createShipmentInput, shipLineElement, shipmentLineEle);
							orderLineKeyTemp=orderLineKey;
							orderLinekeyMap.put(i, orderLineKeyTemp);
							//System.out.println("Below second for loop");
							//break;
				
					}
				
							
				}
					

					}
				XMLUtil.copyElement(createShipmentInput, rootElement, containerElement);
					
				//System.out.println("CreateShipment"+XMLUtil.getXMLString(createShipmentInput));
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,createShipmentInput);
					}
			
				}
				}else{ 
					throw new YFSException(
							"EXTN_ERROR",
							"EXTN_ERROR",
							"order No. not found");
					}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Parse Error");
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Client Creation Error");
		} catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Random Error");
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e){
			e.printStackTrace();
		}

		
	}//End vsiTransferOrderShipmentCreate

	
	

}
