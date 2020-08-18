package com.vsi.oms.api.order;

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
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIChangeBOPUStoSTS {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIChangeBOPUStoSTS.class);
	YIFApi api;
	/**
	 * @param env
	 * @param inXML
	 * @throws Exception
	 * Description:
	 */
	public void vsiChangeBOPUStoSTS(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIChangeBOPUStoSTS================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{
		
			Element orderElem = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			
			String modReasonCode = orderElem.getAttribute("ModificationReasonCode");
			
			if(modReasonCode.equals("STS")){
				
				String CustomerPoNo = orderElem.getAttribute("OrderNo");
				String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
				String dType=VSIConstants.DOCUMENT_TYPE;
				
				Document getOrderListInput = XMLUtil.createDocument("Order");
				Element eleOrder = getOrderListInput.getDocumentElement();
				eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, CustomerPoNo);
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
				eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
				
				api = YIFClientFactory.getInstance().getApi();
				Document outDoc  = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);					
				Element rootEle = outDoc.getDocumentElement();
				String orderHeaderKey=rootEle.getAttribute("OrderHeaderKey");
				NodeList inXMLItemList = inXML.getElementsByTagName(VSIConstants.ELE_ITEM);
				
				for(int i=0;i<inXMLItemList.getLength();i++){
					
					Element eleItem = (Element)inXMLItemList.item(i);
					
					String inXMLItemID = eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
				
				
					NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
					
					Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");

					for(int j=0;j<orderLineList.getLength();j++){
						
					Element eleOrderLine = (Element)orderLineList.item(j);	
					String lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);	
					
					
					if(lineType.equals("PICK_IN_STORE")){
						
						NodeList itemList = outDoc.getElementsByTagName(VSIConstants.ELE_ITEM);
						Element eleItemNew = (Element)itemList.item(j);
						
						String itemID = eleItemNew.getAttribute(VSIConstants.ATTR_ITEM_ID);
						

						
						if(itemID.equals(inXMLItemID)){
							
							String orderLineKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							
							
							Element changeOrderStatusElement = changeOrderStatusInput.getDocumentElement();
							Element orderLinesElement = XMLUtil.appendChild(changeOrderStatusInput, changeOrderStatusElement, VSIConstants.ELE_ORDER_LINES, "");
							Element orderLineElement = XMLUtil.appendChild(changeOrderStatusInput, orderLinesElement, VSIConstants.ELE_ORDER_LINE, "");
							
							changeOrderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
							changeOrderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "ORDER_CREATE");//Need to create new transactionID
							changeOrderStatusElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
							
							
							orderLineElement.setAttribute(VSIConstants.ATTR_LINE_TYPE, orderLineKey);
							orderLineElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "1100");
							orderLineElement.setAttribute(VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
							
			
								
						}
						
					}
				
					
					
					
						
					}
					
					
					callChangeOrderStatus(env, changeOrderStatusInput);
					
					callChangeOrder(env, changeOrderStatusInput);
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
	
		
		
		
	}//end vsiChangeBOPUStoSTS

public void callChangeOrderStatus(YFSEnvironment env, Document changeOrderStatusInput) throws YFSException, RemoteException{
	
	try {
		api = YIFClientFactory.getInstance().getApi();
	api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
	} catch (YIFClientCreationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}

public void callChangeOrder(YFSEnvironment env, Document changeOrderStatusInput) throws YFSException, RemoteException{
	
	try {
		api = YIFClientFactory.getInstance().getApi();
	api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
	} catch (YIFClientCreationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
	
} //end class
