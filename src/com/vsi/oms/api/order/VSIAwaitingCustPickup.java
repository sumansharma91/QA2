package com.vsi.oms.api.order;

import java.rmi.RemoteException;

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

/**
 * @author Perficient Inc.
 * This class is called on success of Authorization via an Integration Server.
 *	For a Bopus order:
 *	1. call changeOrderStatus to change the status to "Awaiting CustomerPickup"
 *	2. call a service to publish the xml for responsys to send an email to the customer
 *
 *	For BOSTS order
 *	1. Remove auth hold
 *	2. Check if Order status is Released, if yes
	a. Call changeorder status to change to "NoAction"
 * 	b. Send an email to the customer
 */
public class VSIAwaitingCustPickup {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIAwaitingCustPickup.class);
	YIFApi api;
	public void vsiAwaitingPickup(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
					
		
		Element rootElement = inXML.getDocumentElement();
		String sOHK = rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		String sCustomerPoNo = rootElement.getAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_KEY);
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element orderElement = getOrderListInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		
		Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
		Element changeOrderStatusElement = changeOrderStatusInput.getDocumentElement();
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.500");
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");		
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "AwaitingCustomerPickup.0001.ex");
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_MODIFICATION_CODE, "Awaiting Customer Pickup");
		Element OrderLinesElement =	changeOrderStatusInput.createElement(VSIConstants.ELE_ORDER_LINES);
		changeOrderStatusElement.appendChild(OrderLinesElement);
		
		////System.out.println("ChangeOrderStatusInput:"+XMLUtil.getXMLString(changeOrderStatusInput));
		
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIAwaitCustPickup.xml");//Template TBD
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
		env.clearApiTemplates();
		
		
		
		//Document tOrder =	api.executeFlow(env, "VSITransformOrder", outDoc);
		

		////System.out.println("Doc to other service for email:"+XMLUtil.getXMLString(outDoc));
		
/*		NodeList torderLineList = tOrder.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int tlinelength= torderLineList.getLength();
		if(tlinelength>0)outDoc=tOrder;
*/		
		Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
	
		if(null != orderLineEle){
			
			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int linelength= orderLineList.getLength();
			int validLines= 0;
			
			for(int i=0;i<linelength;i++){
				
				Element orderLineEle1 = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
				
				//String strOrderLineKey = orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				
				String minLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
				String maxLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
				String lineType= orderLineEle1.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				if(minLineStatus.equals("3200") && maxLineStatus.equals("3200") && lineType.equalsIgnoreCase("PICK_IN_STORE")){
					String sQty = orderLineEle1.getAttribute(VSIConstants.ATTR_QUANTITY);
					Element eleOrderLine = changeOrderStatusInput.createElement(VSIConstants.ELE_ORDER_LINE);
							String OLK =orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, OLK);
							eleOrderLine.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.500");
							eleOrderLine.setAttribute(VSIConstants.ATTR_QUANTITY, sQty);
							eleOrderLine.setAttribute("ChangeForAllAvailableQty", "Y");
							
							OrderLinesElement.appendChild(eleOrderLine);
							validLines++;
							////System.out.println("changeOrderInput131");
				}
				else if (lineType.equalsIgnoreCase("SHIP_TO_STORE"))
				{
					////System.out.println("changeOrderInput111");
					NodeList holdList = outDoc.getElementsByTagName("OrderHoldType");
					int holdLength= holdList.getLength();
				
					for(int j=0;j<holdLength;j++){
						
						////System.out.println("changeOrderInput1211");
					Element orderHoldEle = (Element) outDoc.getElementsByTagName("OrderHoldType").item(j);
					String holdType = orderHoldEle.getAttribute(VSIConstants.ATTR_HOLD_TYPE);
					String holdStatus = orderHoldEle.getAttribute(VSIConstants.ATTR_STATUS);
					if(holdType.equalsIgnoreCase("AUTH_PENDING") && holdStatus.equalsIgnoreCase("1100")){
						
						Document changeOrderInput = XMLUtil.createDocument("Order");
						Element changeOrderElement = changeOrderInput.getDocumentElement();
						changeOrderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
				//for Jira OMS 601, where orders were not getting release and hold was not getting removed once the order is authorized.
						//adding the wait method as the order were not getting updated.
						changeOrderElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
				//for Jira OMS 601 		
						changeOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
						Element orderHoldTypesEle = changeOrderInput.createElement("OrderHoldTypes");
						changeOrderElement.appendChild(orderHoldTypesEle);
						Element orderHoldTypeEle = changeOrderInput.createElement("OrderHoldType");
						orderHoldTypesEle.appendChild(orderHoldTypeEle);
						orderHoldTypeEle.setAttribute(VSIConstants.ATTR_HOLD_TYPE, "AUTH_PENDING");
						orderHoldTypeEle.setAttribute(VSIConstants.ATTR_STATUS, "1300");
						orderHoldTypeEle.setAttribute("ReasonText", "Auth complete");
						
						////System.out.println("changeOrderInput:"+XMLUtil.getXMLString(changeOrderInput));
						
						api = YIFClientFactory.getInstance().getApi();
						api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
						env.clearApiTemplates();

						
						
						
				}
					}//end holdLength
			}
		
			}// end for
			
			
	
			////System.out.println("ChangeOrderStatusInput:"+XMLUtil.getXMLString(changeOrderStatusInput));
			
			/**if(validLines>0){
				////System.out.println("changeOrderInput141");
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIAwaitCustPickupEmail.xml");//Template TBD
			Document emailOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			env.clearApiTemplates();
			Element oRootElement = emailOutDoc.getDocumentElement();
			oRootElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, sCustomerPoNo);
			//api.executeFlow(env, VSIConstants.SERVICE_AWAIT_CUST_PICKUP, emailOutDoc);//Sending this to the service for email to customer

			} **/
			
			
	}
	
	}//end vsiAwaitingPickup
}// end class
		

