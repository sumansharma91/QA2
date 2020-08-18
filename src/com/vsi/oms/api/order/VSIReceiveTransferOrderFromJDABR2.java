package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIReceiveTransferOrderFromJDABR2 {
	private YFCLogCategory log = YFCLogCategory.instance(VSIReceiveTransferOrderFromJDABR2.class);
	YIFApi api;
	public void vsiReceiveTransferOrderFromJDABR2(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIReceiveTransferOrderFromJDA================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{
			Element rootElement = inXML.getDocumentElement();
			Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);

			Map<String, String> orderLinkeyNumber = new HashMap<String, String>();
			String entCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
			String receivingNode = rootElement.getAttribute(VSIConstants.ATTR_RECEIVING_NODE);
			String customerPoNo = shipmentLineElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			
			//change for restock 
			
			Document getOrderListInp = XMLUtil.createDocument("Order");
			Element eleOrder1 = getOrderListInp.getDocumentElement();
			Element eleOrderLine1 = getOrderListInp.createElement(VSIConstants.ELE_ORDER_LINE);
			eleOrderLine1.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
			eleOrder1.appendChild(eleOrderLine1);
			eleOrder1.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
			eleOrder1.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
			//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/RestockOrderList.xml");
			Document inService1 = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInp);
			
			
			if (log.isVerboseEnabled()) {
				log.verbose("inService1 Document is: \n" + XMLUtil.getXMLString(inService1));
			}
			
			//Element OrderEle = inService1.getDocumentElement();
			
			Element OrderEle = (Element) inService1.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);

			String orderHeaderKeyat = OrderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);


			env.clearApiTemplates();
			NodeList lineList = inService1.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			for (int p = 0; p < lineList.getLength(); p++) {
				
				Element lineElement = (Element) lineList.item(p);
				String status = lineElement.getAttribute(VSIConstants.ATTR_STATUS);
				//SOM Restock changes -- Start
				String strCondVar1=lineElement.getAttribute(VSIConstants.ATTR_CONDITION_VARIBALE1);
				String strOrdLnKey=lineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				
				if(status.equalsIgnoreCase("Restock In Transit") && (VSIConstants.FLAG_Y.equals(strCondVar1))){
					
                	Document changeOrder = XMLUtil.createDocument("OrderStatusChange");
        			Element eleChangeOrder = changeOrder.getDocumentElement();
        			Element orderLinesEle = XMLUtil.appendChild(changeOrder, eleChangeOrder, VSIConstants.ELE_ORDER_LINES, "");
        			Element orderLineEle = XMLUtil.appendChild(changeOrder, orderLinesEle, VSIConstants.ELE_ORDER_LINE, "");
        			
        			eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyat);
        			eleChangeOrder.setAttribute("TransactionId","VSIRestockCancel.0001.ex");
        			orderLineEle.setAttribute("BaseDropStatus","9000");
        			orderLineEle.setAttribute("ChangeForAllAvailableQty","Y");
        			orderLineEle.setAttribute("OrderLineKey",strOrdLnKey);
        			
        			if(log.isDebugEnabled()){
    					log.debug("changeOrderStatus API Input for new SOM flow: "+XMLUtil.getXMLString(changeOrder));
    				}
        			
        			if (log.isVerboseEnabled()) {
        				log.verbose("changeOrderStatus API Input for new SOM flow: \n" + XMLUtil.getXMLString(changeOrder));
        			}
       			
        			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrder);
        			
        			if(log.isDebugEnabled()){
    					log.debug("changeOrderStatus API for new SOM flow was invoked successfully");
    				}
        			
        			String strShipNode=lineElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);
    				String strCustPONo=lineElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
    				String strOrderedQty=lineElement.getAttribute(VSIConstants.ATTR_ORD_QTY);
    				Element eleItem=SCXmlUtil.getChildElement(lineElement, VSIConstants.ELE_ITEM);
    				String strItemId=eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);    				
        			String strQueueId="VSI_RESTOCK_"+strShipNode;
        			
    				Document docInboxIn=XMLUtil.createDocument(VSIConstants.ELE_INBOX);
    				Element eleInboxIn=docInboxIn.getDocumentElement();
    				eleInboxIn.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, VSIConstants.FLAG_Y);
    				eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyat);
    				eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrdLnKey);
    				eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_NO, strCustPONo);
    				eleInboxIn.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Restock");
    				eleInboxIn.setAttribute(VSIConstants.ATTR_QUEUE_ID, strQueueId);
    				eleInboxIn.setAttribute(VSIConstants.ATTR_SHIPNODE_KEY, strShipNode);
    				Element eleInbxRfrncsLst=SCXmlUtil.createChild(eleInboxIn, VSIConstants.ELE_INBOX_REFERANCES_LIST);
    				Element eleItemReference=SCXmlUtil.createChild(eleInbxRfrncsLst, VSIConstants.ELE_INBOX_REFERANCES);
    				eleItemReference.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.ATTR_ITEM_ID);
    				eleItemReference.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "TEXT");
    				eleItemReference.setAttribute(VSIConstants.ATTR_VALUE, strItemId);
    				Element eleQtyReference=SCXmlUtil.createChild(eleInbxRfrncsLst, VSIConstants.ELE_INBOX_REFERANCES);
    				eleQtyReference.setAttribute(VSIConstants.ATTR_NAME, "Qty");
    				eleQtyReference.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "TEXT");
    				eleQtyReference.setAttribute(VSIConstants.ATTR_VALUE, strOrderedQty);
    				
    				if(log.isDebugEnabled()){
    					log.debug("createException API Input: "+SCXmlUtil.getString(docInboxIn));
    				}
    				
    				VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION, docInboxIn);
    				
    				if(log.isDebugEnabled()){
    					log.debug("createException API was invoked successfully");
    				}
					
				}
				//SOM Restock changes -- End
				else if(status.equalsIgnoreCase("Restock In Transit")){
                	Document changeOrder = XMLUtil.createDocument("OrderStatusChange");
        			Element eleChangeOrder = changeOrder.getDocumentElement();
        			Element orderLinesEle = XMLUtil.appendChild(changeOrder, eleChangeOrder, VSIConstants.ELE_ORDER_LINES, "");
        			Element orderLineEle = XMLUtil.appendChild(changeOrder, orderLinesEle, VSIConstants.ELE_ORDER_LINE, "");
        			//Element orderLineEle = XMLUtil.createElement(changeOrder, VSIConstants.ELE_ORDER_LINE, "");
        			eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyat);
        			eleChangeOrder.setAttribute("TransactionId","VSIRestock.0001.ex");
        			orderLineEle.setAttribute("BaseDropStatus","9000.100");
        			orderLineEle.setAttribute("ChangeForAllAvailableQty","Y");
        			orderLineEle.setAttribute("OrderLineKey",strOrdLnKey);
        			//eleChangeOrder.appendChild(orderLineEle);

        			if (log.isVerboseEnabled()) {
        				log.verbose("changeOrder Document is: \n" + XMLUtil.getXMLString(changeOrder));
        			}
       			
        			//eleChangeOrder.appendChild(lineElement);
        			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrder);
        			        			
                }
			}
			
              // change for restock ends
						
		
		//String productClass = shipmentLineElement.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
		//String unitMeasure = shipmentLineElement.getAttribute(VSIConstants.ATTR_UOM);
		//String dType = shipmentLineElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
		
		
		
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListInput.getDocumentElement();
		Element eleOrderLine = getOrderListInput.createElement(VSIConstants.ELE_ORDER_LINE);
		eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		eleOrder.appendChild(eleOrderLine);

		
		
		
		//eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
		eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
		api = YIFClientFactory.getInstance().getApi();
		
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/JDACrShipOrderList.xml");
		Document inService = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
		env.clearApiTemplates();
		
		
	
		if (log.isVerboseEnabled()) {
			log.verbose("inService Document is: \n" + XMLUtil.getXMLString(inService));
		}
		
		   Element rootEle = inService.getDocumentElement();
		   rootEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		
		Document outDoc = api.executeFlow(env, "VSIParseCreateShipment", inService);

	
		if (log.isVerboseEnabled()) {
			log.verbose("outDoc Document is: \n" + XMLUtil.getXMLString(outDoc));
		}
			
		Element orderElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
	
		
		Document NewgetOrderListInput = XMLUtil.createDocument("Order");
		Element orderEle = NewgetOrderListInput.getDocumentElement();
		Element orderLineEle = XMLUtil.createElement(NewgetOrderListInput, VSIConstants.ELE_ORDER_LINE, "");
		orderLineEle.setAttribute(VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY, orderHeaderKey);
		orderEle.appendChild(orderLineEle);
	//Calling getOrderList to get the Order no from chainedOrderHeaderKey
		
		if (log.isVerboseEnabled()) {
			log.verbose("NewgetOrderListInput Document is: \n" + XMLUtil.getXMLString(NewgetOrderListInput));
		}
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/JDACrShipOrderList.xml");
		Document NewOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,NewgetOrderListInput);
		env.clearApiTemplates();
		

		if (log.isVerboseEnabled()) {
			log.verbose("NewOutDoc Document is: \n" + XMLUtil.getXMLString(NewOutDoc));
		}
		
		NodeList ndlOrderList = NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER);
		
	
		boolean OrderLine =false;
		  /**************OMS-815******************/
		   for (int k = 0; k < ndlOrderList.getLength(); k++) {
			Element newOrderElement = (Element) NewOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(k);
			String transferOrderHeaderKey = newOrderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			if (log.isVerboseEnabled()) {
				log.verbose("IMP newOrderElement Document is: \n" + XMLUtil.getElementXMLString(newOrderElement));
			}
		    NodeList ndlOrderLineList = newOrderElement.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		    
	    	for (int Z = 0; Z < ndlOrderLineList.getLength(); Z++) 
			{
		    	Element OrderLineElement = (Element) newOrderElement.getElementsByTagName("OrderLine").item(Z);
		    	String MinLineStatus = OrderLineElement.getAttribute("MinLineStatus");
		    	
		    	if(MinLineStatus.equalsIgnoreCase("3700"))
		    	{
		    		if(log.isDebugEnabled()){
		    			log.verbose("Element has 3700 status");
		    		}
		    		OrderLine =true;
		    	}
			}
		    
		    int iNoOfOrderLines = ndlOrderLineList.getLength();
		    if(log.isDebugEnabled()){
		    	log.verbose("iNoOfOrderLines is "+iNoOfOrderLines);
		    }
		    
			if(OrderLine==true)
			{
			Element OrderLineEle = (Element) newOrderElement.getElementsByTagName("OrderLine").item(0);
			
			if (log.isVerboseEnabled()) {
				log.verbose("IMP OrderLineEle Document is: \n" + XMLUtil.getElementXMLString(OrderLineEle));
			}
            String MaxLineStatus = OrderLineEle.getAttribute("MaxLineStatus");
            String MinLineStatus = OrderLineEle.getAttribute("MinLineStatus");
            if(log.isDebugEnabled()){
            	log.verbose("MaxLineStatust is "+MaxLineStatus);
            	log.verbose("MinLineStatus is "+MinLineStatus);
            }
            
		    if(statusAttribute.equals("RECEIVED")){
			//String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
		
			/*Document getShipmentListInput = XMLUtil.createDocument("Shipment");
			Element shipmentElement = getShipmentListInput.getDocumentElement();
			shipmentElement.setAttribute(VSIConstants.ATTR_STATUS, "1400");
			shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, transferOrderHeaderKey);
			Element containersElement = getShipmentListInput.createElement(VSIConstants.ELE_CONTAINERS);
			Element containerElement = getShipmentListInput.createElement(VSIConstants.ELE_CONTAINER);
			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			containersElement.appendChild(containerElement);
			shipmentElement.appendChild(containersElement);*/
			
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String transferOrderNo = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);
			String OrderNumber =newOrderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			
			
				Document getShipmentListInput = XMLUtil.createDocument("Shipment");
				Element shipmentElement = getShipmentListInput.getDocumentElement();
			//	if(YFCObject.isVoid(containerNumber) || iNoOfOrders < 2)
					shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, transferOrderHeaderKey);
				shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_NO, OrderNumber);
    			Element containersElement = XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			Element containerElement = XMLUtil.appendChild(getShipmentListInput, containersElement, VSIConstants.ELE_CONTAINER, "");
    			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			
    			if (log.isVerboseEnabled()) {
    				log.verbose("getShipmentListInput Document is: \n" + XMLUtil.getXMLString(getShipmentListInput));
    			}
    			
			env.setApiTemplate(VSIConstants.API_GET_SHIPMENT_LIST, "global/template/api/VSIReceiveTransferOrderFromJDA.xml");
			YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
			Document outputDoc = callApi.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);	
			env.clearApiTemplates();
			if(log.isDebugEnabled()){
				log.verbose("Transfer Order Number  is "+OrderNumber);
				log.verbose("TransferOrderHeaderKey Number  is "+transferOrderHeaderKey);
				log.verbose("outputDoc Document is: \n" + XMLUtil.getXMLString(outputDoc));
			}
   				   
				NodeList shipmentlist = outputDoc.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
    			int shipmentLength = shipmentlist.getLength();
    		if(shipmentLength!=0)
    		{
			Element shipmentEle = (Element) outputDoc.getElementsByTagName(VSIConstants.ELE_SHIPMENT).item(0);
			String shipmentKey = shipmentEle.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			if(!(YFCObject.isVoid(shipmentKey)))
			{
			//Receive Order Input
			Document receiveOrderInput = XMLUtil.createDocument("Receipt");
			Element receiptElement = receiveOrderInput.getDocumentElement();
			receiptElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0006");
			receiptElement.setAttribute(VSIConstants.ATTR_RECEIVING_NODE, receivingNode);
			
			Element shipmentReceiveElement = receiveOrderInput.createElement("Shipment");
			shipmentReceiveElement.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, shipmentKey);
			if(log.isDebugEnabled()){
				log.verbose("shipmentKey is "+shipmentKey);
			}
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
				if(log.isDebugEnabled()){
					log.verbose("shipmentLineKey is "+shipmentLineKey);
				}
			}
			   
			if (log.isVerboseEnabled()) {
				log.verbose("receiveOrder Document is: \n" + XMLUtil.getXMLString(receiveOrderInput));
			}
  			
		
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_RECEIVE_ORDER,receiveOrderInput);
		
		if(log.isDebugEnabled()){
			log.verbose("orderHeaderKey is "+orderHeaderKey);
		}
		
		Document getOrderListIn = XMLUtil.createDocument("Order");
		Element eleOrderElement = getOrderListIn.getDocumentElement();
		Element eleOrderLineElement = getOrderListIn.createElement(VSIConstants.ELE_ORDER_LINE);
		eleOrderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		eleOrderElement.appendChild(eleOrderLineElement);
		eleOrderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, entCode);
		eleOrderElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		
	 	api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/JDACrShipOrderList.xml");
		Document Output = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListIn);
		env.clearApiTemplates();
	
		   Element rootelement = Output.getDocumentElement();
		   rootelement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
    	   Document outpuDocument = api.executeFlow(env, "VSIParseCreateShipment", Output);
    	    NodeList orderlineElementList = outpuDocument.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
    	   //OMS -1169 Modification : Start
			List<Element> orderLineEleList = VSIUtils.getElementListByXpath(Output,"/OrderList/Order/OrderLines/OrderLine[@MinLineStatus < '2160.400']");
			int iOrderLineEleLength =orderLineEleList.size() ;
			boolean isHoldReqd=false;
			if(log.isDebugEnabled()){
				log.debug("Order lines less than store received : count : "+iOrderLineEleLength);
			}
			if(iOrderLineEleLength > 0)
				isHoldReqd=true;
			int Length =orderlineElementList.getLength();
			if(log.isDebugEnabled()){
				log.verbose("Length is "+Length);
			}
    		int count=0;
    		int Lastloop =Length-1;
     		for (int i = 0; i < orderlineElementList.getLength(); i++) 
     		{
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
            	if  (MaxLineSts.equalsIgnoreCase("2160.400")&& iminLine < 2160.400)					
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
            	//OMS-1169  adding validation for Restock statuses
            	if  ((MaxLineSts.equalsIgnoreCase("9000")|| MaxLineSts.equalsIgnoreCase("9000.100")||
            			MaxLineSts.equalsIgnoreCase("9000.200"))&&
            			(MinLineSts.equalsIgnoreCase("9000") || MaxLineSts.equalsIgnoreCase("9000.100") 
            					||MaxLineSts.equalsIgnoreCase("9000.200")))					
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
						log.verbose("Resolving hold before releasing Order");
					}
					ResolveHold(env, orderHeaderKey,orderLinkeyNumber);
					Document OrderDetail=ResolveHold(env, orderHeaderKey,orderLinkeyNumber);
				    Element Order =(Element) OrderDetail.getElementsByTagName("Order").item(0);
					String OrderHeaderKey =Order.getAttribute("OrderHeaderKey");
					invokeReleaseOrder(env,OrderHeaderKey);
				}
				
				else if((Lastloop==i&&count<Length) && isHoldReqd)
				{
					
					if(log.isDebugEnabled()){
						log.verbose("Creating Hold");
					}
					createHold(env, orderHeaderKey,orderLinkeyNumber);
				}
				// OMS -1169 Modification : End
			}
		}
	
			}
			}
		}	
	
		else
		{
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Input Status error");
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



	public void invokeReleaseOrder(YFSEnvironment env,String orderHeaderKey){
	
		Document releaseOrderInput;
		try {
	
	    releaseOrderInput = XMLUtil.createDocument("ReleaseOrder");
		Element releaseOrderElement = releaseOrderInput.getDocumentElement();
		releaseOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
		releaseOrderElement.setAttribute("AllocationRuleID","SYSTEM");
		releaseOrderElement.setAttribute("IgnoreOrdering","Y");
		releaseOrderElement.setAttribute("CheckInventory","N");
		releaseOrderElement.setAttribute("IgnoreReleaseDate","Y");
		
		if (log.isVerboseEnabled()) {
			log.verbose("releaseOrderInput Document is: \n" + XMLUtil.getXMLString(releaseOrderInput));
		}
		  
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
	
	
private void createHold(YFSEnvironment env, String sOHK, Map<String, String> orderLinkeyNumber)
throws ParserConfigurationException, YIFClientCreationException,
YFSException, RemoteException {

Document createHoldInput = XMLUtil.createDocument("Order");
Element orderElement = createHoldInput.getDocumentElement();
orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
orderElement.setAttribute("Override", "Y");
Element OrderlinesElement = createHoldInput
.createElement("OrderLines");
orderElement.appendChild(OrderlinesElement);
for (Entry<String, String> s : orderLinkeyNumber.entrySet()) 
{	String orderLinkey = s.getKey();
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
	OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");
    OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
}

api = YIFClientFactory.getInstance().getApi();
api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);
}

}
