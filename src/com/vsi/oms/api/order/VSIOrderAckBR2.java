package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIOrderAckBR2 {

	private YFCLogCategory log = YFCLogCategory.instance(VSIOrderAckBR2.class);
	YIFApi api;
	public void vsiOrderAck(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		try{
		Element rootElement = inXML.getDocumentElement();
	//	Element OrderLineElement = (Element) inXML.getElementsByTagName("OrderLine").item(0);
		String strCustPoNo = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strBaseDropStatus = rootElement.getAttribute(VSIConstants.ATTR_BASE_DROP_STATUS);
		String strTransactionId = rootElement.getAttribute(VSIConstants.ATTR_TRANSACTION_ID);
		String strSelectMethod = rootElement.getAttribute(VSIConstants.ATTR_SELECT_METHOD);
		String strModificationReasonCode = rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_CODE);
		String strModificationReasonText = rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_TEXT);
		String strEnterpriseCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		String strDocumentType = rootElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
		
		Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
		Element eleOrderLine = getOrderLineListInput.getDocumentElement();
		eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPoNo);
		eleOrderLine.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		eleOrderLine.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, strDocumentType);
		Element eleOrder = getOrderLineListInput.createElement("Order");
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "STORE");
		eleOrder.setAttribute("OrderTypeQryType", "NE");
		eleOrderLine.appendChild(eleOrder);
		
	
//Calling the getOrderLineList API with CustomerPONo,EnterpriseCode and DocumentType		
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST, "global/template/api/VSIOrderAckOrderLineTemplate.xml");
			api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
			env.clearApiTemplates();
			Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
			
			if(null != orderLineEle){
			//Element orderElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey=orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			boolean isRestockStatusChange = false;
			if(strTransactionId.equalsIgnoreCase("VSIRestockCancel.0001.ex")){
				isRestockStatusChange = true;
			}
			
			Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
			Element changeOrderStatusElement = changeOrderStatusInput.getDocumentElement();
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
			//changeOrderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, strBaseDropStatus);
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, strTransactionId);
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, strSelectMethod);
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_MODIFICATION_CODE, strModificationReasonCode);
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_MODIFICATION_TEXT, strModificationReasonText);
			Element orderLinesElement = changeOrderStatusInput.createElement("OrderLines");
			//Element orderLineElement = changeOrderStatusInput.createElement("OrderLine");
			changeOrderStatusElement.appendChild(orderLinesElement);
			
			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int linelength= orderLineList.getLength();
			
			NodeList inOrderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int inLinelength= inOrderLineList.getLength();
			Map<String , Integer> orderLinekeyMap = new HashMap<String , Integer>();
			
			for(int i=0;i<inLinelength;i++){
				
				Element orderLineEle1 = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
				String strInItemID = orderLineEle1.getAttribute("ItemId");
				String strInQty = orderLineEle1.getAttribute(VSIConstants.ATTR_QUANTITY);
				Double dInQty = Double.parseDouble(strInQty);
				for(int j=0;j<linelength;j++){
					Element orderLineEle2 = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(j);
					Element itemEle = (Element) orderLineEle2.getElementsByTagName("Item").item(0);
					String strOrderLineKey = orderLineEle2.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					String strItemID = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
					String status = orderLineEle2.getAttribute("Status");
					String strQty = null;
					strQty = orderLineEle2.getAttribute("OrderedQty");
					boolean restockFlag = false;
					if(isRestockStatusChange && status !=null && status.equalsIgnoreCase("Restock") ){
						strQty = orderLineEle2.getAttribute("StatusQuantity");
						restockFlag = true;
					}else if(!isRestockStatusChange){
						restockFlag = true;
					}
					Double dQty = Double.parseDouble(strQty);
					////System.out.println("strItemID:"+strItemID +"strQty"+strQty+"strInItemID:"+strInItemID +"strInQty"+strInQty +orderLinekeyMap.containsKey(strOrderLineKey));
					////System.out.println("1111"+strItemID.equalsIgnoreCase(strInItemID) +""+strQty.equalsIgnoreCase(strInQty)+""+(!(orderLinekeyMap.containsKey(strOrderLineKey))));
					if(restockFlag && strItemID.equalsIgnoreCase(strInItemID)  &&  (Double.compare(dQty,dInQty)==0)   && (!(orderLinekeyMap.containsKey(strOrderLineKey)))){
						Element orderLineElement = changeOrderStatusInput.createElement("OrderLine");
						////System.out.println("222222strItemID:"+strItemID +"strQty"+strQty+"strInItemID:"+strInItemID +"strInQty"+strInQty +orderLinekeyMap.containsKey(strOrderLineKey));
					
						orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
						orderLineElement.setAttribute(VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
						orderLineElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, strBaseDropStatus);
						orderLineElement.setAttribute(VSIConstants.ATTR_QUANTITY, strQty);
						////System.out.println("i::::::"+i);
						////System.out.println("j::::::"+j);
						orderLinesElement.appendChild(orderLineElement);
						////System.out.println("orderLineElement:"+XMLUtil.getElementXMLString(orderLineElement));
						orderLinekeyMap.put(strOrderLineKey, i);
						break;
					}
					
					
				}
				
				if (!(orderLinekeyMap.containsValue(i))){
					for(int k=0;k<linelength;k++){
						Element orderLineEle2 = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(k);
						Element itemEle = (Element) orderLineEle2.getElementsByTagName("Item").item(0);
						String strOrderLineKey = orderLineEle2.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
						String strItemID = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
						String status = orderLineEle2.getAttribute("Status");
						String strQty = null;
						strQty = orderLineEle2.getAttribute("OrderedQty");
						boolean restockFlag = false;
						if(isRestockStatusChange && status !=null && status.equalsIgnoreCase("Restock") ){
							strQty = orderLineEle2.getAttribute("StatusQuantity");
							restockFlag = true;
						}else if(!isRestockStatusChange){
							restockFlag = true;
						}
						////System.out.println("strItemID:"+strItemID +"strQty"+strQty+"strInItemID:"+strInItemID +"strInQty"+strInQty +orderLinekeyMap.containsKey(strOrderLineKey));
						////System.out.println("2111"+strItemID.equalsIgnoreCase(strInItemID) +""+strQty.equalsIgnoreCase(strInQty)+""+(!(orderLinekeyMap.containsKey(strOrderLineKey))));
						Double dQty = Double.parseDouble(strQty);
						if(restockFlag && strItemID.equalsIgnoreCase(strInItemID)  && (Double.compare(dQty,dInQty)==0)   && (!(orderLinekeyMap.containsKey(strOrderLineKey)))){
							Element orderLineElement = changeOrderStatusInput.createElement("OrderLine");
							////System.out.println("222222strItemID:"+strItemID +"strQty"+strQty+"strInItemID:"+strInItemID +"strInQty"+strInQty +orderLinekeyMap.containsKey(strOrderLineKey));
							orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
							orderLineElement.setAttribute(VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
							orderLineElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, strBaseDropStatus);
							orderLineElement.setAttribute(VSIConstants.ATTR_QUANTITY, strQty);
							orderLinesElement.appendChild(orderLineElement);
							orderLinekeyMap.put(strOrderLineKey, i);
							break;
						}
						
						
					}
				}
				
				
			}
			
			////System.out.println("ChangeOrderStatusInput:"+XMLUtil.getXMLString(changeOrderStatusInput));
			
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
			
		
			
			
			}
		
	}catch (YFSException e) {
		e.printStackTrace();
	//	YFSException e1 = new YFSException();
		String error = e.getMessage();
		String errorCode = e.getErrorCode();
		String errorDesc = e.getErrorDescription();
		throw new YFSException(
				errorCode,
				errorDesc,
				error);
	} catch (Exception e){
		e.printStackTrace();
		throw new YFSException(
				"EXTN_ERROR",
				"EXTN_ERROR",
				"Exception");
	}
}
}