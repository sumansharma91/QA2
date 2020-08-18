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
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIShipNodeValidation {


	private YFCLogCategory log = YFCLogCategory.instance(VSIShipNodeValidation.class);
	YIFApi api;
	public Document vsiShipNode(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		
		Element rootElement = inXML.getDocumentElement();
		String OrderNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();
		Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		
		String sShipNode = orderLineElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		for(int i=0;i<linelength;i++){
			
			Element orderLineEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			String sShipNodeNew = orderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			String sLineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			
			/*if(!YFSObject.isNull(sLineType) && !YFSObject.isVoid(sLineType) && !sLineType.equals("")  && !sLineType.equals(" ")){
				if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
					orderLineEle.setAttribute("ProcureFromNode", "9001");
				}
				if(sLineType.equalsIgnoreCase("SHIP_TO_HOME")){
					orderLineEle.setAttribute("ShipNode", "9001");
				}
			}*/			
			if(!(sShipNode.equals(sShipNodeNew))){
				
				raiseAlert(env,OrderNo);
				
			}
			
		}
		return inXML;

		
		
	}
	
private void raiseAlert(YFSEnvironment env, String sOrderNo) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Multiple PickUp Stores");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Multiple Ship Nodes sent by ATG");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Order Invalid");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Order Invalid");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_ORDER_CREATE");
		
		
		Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
		
		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderNo");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOrderNo);
		
		
		InboxReferencesListElement.appendChild(InboxReferencesElement);
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
				
	}//end raiseAlert
	
}//end class
