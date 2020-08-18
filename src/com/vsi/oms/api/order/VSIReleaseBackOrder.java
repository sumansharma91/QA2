package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.HashMap;

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

public class VSIReleaseBackOrder {

	private YFCLogCategory log = YFCLogCategory.instance(VSIReleaseBackOrder.class);
	YIFApi api;
	public void vsiReleaseBackOrderResolve(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		
		if(null!=inXML.getDocumentElement() && null!=inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)){

			
			//Element rootElement = inXML.getDocumentElement();
			NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int orderLineLength = orderLineList.getLength();
			
			Element orderEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER);
			String OHK = orderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String sOrderNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
			HashMap<String, String> CustPoKey = new HashMap<String, String>();
			
			for(int i=0;i< orderLineLength;i++){
				
				
				Element orderLineEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
				String lineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				String customerPoNo = orderLineEle.getAttribute(VSIConstants.ATTR_CUST_PO_NO);

				
				
				if( !(CustPoKey.containsValue(customerPoNo)) ){
					
					raiseAlert(env,OHK,sOrderNo);
					
					CustPoKey.put(customerPoNo, "Y");
					
				}// end if lineType check
	
					
				}// end for
				
			}// end main for loop inXML
			
		
			
		}
		
		
		
	/**
	 * 
	 * @param env
	 * @param sOHK
	 * @param sOrderNo
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 */
	
	private void raiseAlert(YFSEnvironment env, String sOHK, String sOrderNo) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Order is Backordered");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Order is Backordered");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Order Processing");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Release Order");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_RELEASE_ORDER");
		
		
		Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
		
		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHK);
		
		InboxReferencesListElement.appendChild(InboxReferencesElement);
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
		createHold(env,sOHK);
				
	}
	/**
	 * 
	 * @param env
	 * @param sOHK
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 */

		private void createHold(YFSEnvironment env, String sOHK) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
			
			Document createHoldInput = XMLUtil.createDocument("Order");
			Element orderElement = createHoldInput.getDocumentElement();
			orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
			orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
			
			Element OrderHoldTypesElement = createHoldInput.createElement("OrderHoldTypes");
			orderElement.appendChild(OrderHoldTypesElement);
			
			Element OrderHoldTypeElement = createHoldInput.createElement("OrderHoldType");
			OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE, "RELEASE_FAILURE_HOLD");
			OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT, "RELEASE_FAILURE_HOLD");
			OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");
			
			OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
			

			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);
			
			
		}

	
	
}
