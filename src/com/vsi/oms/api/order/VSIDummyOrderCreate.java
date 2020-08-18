package com.vsi.oms.api.order;

/**********************************************************************************
 * File Name        : VSIDummyOrderCreate.java
 *
 * Description      : This Code checks for the Orders where shipment has been received at a wrong store. This code first cancels the 
 * 					  Sales Order associated with the Transfer Order and then creates a Dummy Sales Order with ShipNode as the Wrong 
 * 					  Store and then Cancels it and then changes the status to Restock so that the new store can restock the inventory. 
 * 					   
 * Modification Log :
 * -----------------------------------------------------------------------
 * Ver #    Date          Author                   Modification
 * -----------------------------------------------------------------------
 * 0.00a    09/04/2014                  This Code checks for the Orders where shipment has been received at a wrong store. This code first cancels the 
 * 					  					Sales Order associated with the Transfer Order and then creates a Dummy Sales Order with ShipNode as the Wrong 
 * 					  					Store and then Cancels it and then changes the status to Restock so that the new store can restock the inventory. 
 **********************************************************************************/

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
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIDummyOrderCreate {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIDummyOrderCreate.class);
	YIFApi api;

	public void vsiCancelOrderFromJDA(YFSEnvironment env, Document inXML)
			throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException {

		if (!(YFCCommon.isVoid(inXML))) {

			Element eleRootElement = inXML.getDocumentElement();
			String sCustPoNo = eleRootElement
					.getAttribute(VSIConstants.ATTR_ORDER_NO);
			if(log.isDebugEnabled()){
				log.info("=====>Customer PO No from JDA =======" + sCustPoNo);
			}
			String sModReasonCode = eleRootElement
					.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE);
			if(log.isDebugEnabled()){
				log.info("=====>sModReasonCode attribute value is=======" + sModReasonCode);
			}
			String sShipNode = eleRootElement
					.getAttribute(VSIConstants.ATTR_WRONG_STORE);
			if(log.isDebugEnabled()){
				log.info("=====>Wrong Store attribute value is=======" + sShipNode);
			}
		
			YFCDocument getOrderLineListInput = YFCDocument
					.createDocument("OrderLine");
			YFCElement eleOrderLineElement = getOrderLineListInput
					.getDocumentElement();
			eleOrderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
					sCustPoNo);
			/*
			 * Invoking getOrderLineList API to get all the OrderLines
			 * associated with the CustomerPONo
			 */
			api = YIFClientFactory.getInstance().getApi();

			Document outDoc = apiCall(env,
					"global/template/api/VSIDummyOrder.xml",
					getOrderLineListInput.getDocument(),
					VSIConstants.API_GET_ORDER_LINE_LIST, api);

			if (null != (Element) outDoc.getElementsByTagName(
					VSIConstants.ELE_ORDER_LINE).item(0)) {

				/* Calling cancelOrder Method to cancel the Sales Order */
				cancelOrder(env, outDoc, sModReasonCode, api);
				/*
				 * Calling dummyOrder method which creates the input for
				 * createOrder API
				 */
				Document createOrder = dummyOrder(env, outDoc, sShipNode);
				/*
				 * Invoking createOrder API with the Input Received from
				 * dummyOrder Method
				 */
				Document createOrderDocOut = apiCall(env,
						"global/template/api/VSICreateOrderDummy.xml",
						createOrder, VSIConstants.API_CREATE_ORDER, api);

				/* Input for changeOrder to Cancel the Dummy Order */

				Element eleCreateOrderDocOutRoot = createOrderDocOut
						.getDocumentElement();
				eleCreateOrderDocOutRoot.setAttribute("Action", "CANCEL");
				eleCreateOrderDocOutRoot.setAttribute("Override", "N");
				//OMS-737 - To avoid sending cancellation email to JDA
				eleCreateOrderDocOutRoot.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
				"DummyOrderCancel");
				//OMS-737 - End
				/* Invoking changeOrder API to cancel the Dummy Order */

				Document cancelOrderDocOut = apiCall(env,
						"global/template/api/VSIChangeOrder.xml",
						createOrderDocOut, VSIConstants.API_CHANGE_ORDER, api);
				
				/*
				 *Calling changeOrderStatus method which will prepare the input
				 * for changeOrderStatus API and change the status of the Dummy
				 * Order to Restock
				 */
				changeOrderStatus(env, cancelOrderDocOut, sModReasonCode, api);

			}

		}

	}// end method

	/**********************************************************************************
	 * Method Name : dummyOrder
	 * 
	 * Description : This Method creates the input document for createOrder API.
	 * This Method takes the nodes and attributes coming in the output of
	 * getOrderLineList API and creates the input
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 09/04/2014 This Method creates the input document for createOrder
	 * API. This Method takes the nodes and attributes coming in the output of
	 * getOrderLineList API and creates the input.
	 **********************************************************************************/
	public Document dummyOrder(YFSEnvironment env, Document outDoc,
			String sShipNode) throws ParserConfigurationException {

		String sOrderNo = "";
		YFCDocument yfcInDoc = YFCDocument.getDocumentFor(outDoc);
		YFCNode orderNode = yfcInDoc.getElementsByTagName("Order").item(0);

		/* Calling Method createOrderApiInput which has the Order node */
		YFCDocument createOrderApiInputDoc = createOrderApiInput(orderNode);
		YFCElement eleRootElement = createOrderApiInputDoc.getDocumentElement();
		eleRootElement.setAttribute(VSIConstants.ATTR_ORDER_NO, "");
		eleRootElement.removeAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		YFCElement orderLinesElement = eleRootElement.createChild("OrderLines");
		YFCElement notesEle = eleRootElement.createChild("Notes");
		YFCElement noteEle = notesEle.createChild("Note");
		noteEle.setAttribute("NoteText", "This is a Dummy Order.");
		
		YFCNodeList<YFCElement> orderLineNodeList = yfcInDoc
				.getElementsByTagName("OrderLine");
		for (int i = 0; i < orderLineNodeList.getLength(); i++) {
			YFCElement orderLineElement = (YFCElement) orderLineNodeList
					.item(i);
			String sQuantity = orderLineElement
					.getAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY);
			orderLineElement.setAttribute(VSIConstants.ATTR_ORD_QTY, sQuantity);
			sOrderNo = orderLineElement.getElementsByTagName("Order").item(0)
					.getAttribute(VSIConstants.ATTR_ORDER_NO);
			orderLineElement.setAttribute(VSIConstants.ATTR_SHIP_NODE,
					sShipNode);
			orderLineElement
					.removeAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY);
			orderLineElement.removeAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			orderLineElement.removeChild(orderLineElement.getElementsByTagName(
					"Order").item(0));
			orderLinesElement.importNode(orderLineElement);
		}

		eleRootElement.setAttribute(VSIConstants.ATTR_ORDER_TYPE,
				VSIConstants.ATTR_ORDER_TYPE_VALUE);
		eleRootElement.setAttribute(VSIConstants.ATTR_ENTRY_TYPE,
				VSIConstants.ATTR_ENTRY_TYPE_VALUE);

		// Adding the Original Sales Order reference to the Dummy Order
		YFCElement referencesElement = eleRootElement.createChild("References");
		YFCElement referenceElement = referencesElement
				.createChild("Reference");
		referenceElement.setAttribute("Name", "SalesOrder");
		referenceElement.setAttribute("Value", sOrderNo);

		if(log.isDebugEnabled()){
			log.info(XMLUtil.getXMLString(createOrderApiInputDoc.getDocument()));
		}
		return createOrderApiInputDoc.getDocument();
	}

	/**********************************************************************************
	 * Method Name : createOrderApiInput
	 * 
	 * Description : This Method copies the Order node from the output of
	 * getOrderLineList API
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 09/04/2014 This Method copies the Order node from the output of
	 * getOrderLineList API
	 **********************************************************************************/
	public YFCDocument createOrderApiInput(YFCNode orderNode)
			throws ParserConfigurationException {

		YFCDocument orderDoc = YFCDocument.createDocument();
		YFCNode Order = orderDoc.importNode(orderNode, true);
		orderDoc.appendChild(Order);

		return orderDoc;
	}

	/**********************************************************************************
	 * Method Name : createOrderApiInput
	 * 
	 * Description : This Method creates the input for changeOrderStatus API to
	 * change the status to Restock
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 09/04/2014 This Method creates the input for changeOrderStatus API
	 * to change the status to Restock
	 * 
	 * @param api
	 **********************************************************************************/
	public void changeOrderStatus(YFSEnvironment env,
			Document cancelOrderDocOut, String modReasonCode, YIFApi api)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		Element eleCancelOrderDocOut = cancelOrderDocOut.getDocumentElement();
		String sOrderNo = eleCancelOrderDocOut
				.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String sEnterpriseCode = eleCancelOrderDocOut
				.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		String sOrderType = eleCancelOrderDocOut
				.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		// Document changeOrderStatusInput = XMLUtil
		// .createDocument("OrderStatusChange");
		YFCDocument changeOrderStatusInput = YFCDocument
				.createDocument("OrderStatusChange");
		YFCElement orderElement = changeOrderStatusInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		orderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,
				sEnterpriseCode);
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_TYPE, sOrderType);
		orderElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE,
				VSIConstants.DOCUMENT_TYPE);
		orderElement.setAttribute(
				VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY,
				VSIConstants.FLAG_Y);
		orderElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS,
				"9000.100");
		orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
				modReasonCode);
		orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT,
				"Restock");
		orderElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID,
				"VSIRestock.0001.ex");
		orderElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD,
				VSIConstants.SELECT_METHOD_WAIT);
		
		/* Calling apiCall method to invoke changeOrderStatus API */
		apiCall(env, "global/template/api/VSIDummyOrderStatusChange.xml",
				changeOrderStatusInput.getDocument(),
				VSIConstants.API_CHANGE_ORDER_STATUS, api);

	}

	/**********************************************************************************
	 * Method Name : cancelOrder
	 * 
	 * Description : This Method creates the input for cancelling the SalesOrder
	 * by invoking changeOrder API
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 09/04/2014 This Method creates the input for canceling the
	 * SalesOrder by invoking changeOrder API
	 * 
	 * @param api
	 **********************************************************************************/

	private void cancelOrder(YFSEnvironment env, Document outDoc,
			String modReasonCode, YIFApi api)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {

		// Document changeOrderInput = XMLUtil.createDocument("Order");
		YFCDocument changeOrderInput = YFCDocument.createDocument("Order");
		YFCElement eleOrderElement = changeOrderInput.getDocumentElement();
		eleOrderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
				modReasonCode);
		YFCElement orderLinesElement = changeOrderInput
				.createElement("OrderLines");
		eleOrderElement.appendChild(orderLinesElement);

		Element elementOrderElement = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);

		String sOHK = elementOrderElement
				.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		eleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);

		NodeList orderLineList = outDoc
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength = orderLineList.getLength();

		for (int i = 0; i < linelength; i++) {

			Element eleOrderLineElement = (Element) outDoc
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			String strOrderLineKey = eleOrderLineElement
					.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			YFCElement orderLineElement = changeOrderInput
					.createElement("OrderLine");
			orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
					strOrderLineKey);
			orderLineElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			orderLineElement.setAttribute("Override", "N");
			orderLinesElement.appendChild(orderLineElement);

		}
		/* Calling apiCall method to call changeOrder API to cancel the Sales OrderLine*/
		apiCall(env,
				"global/template/api/VSIChangeOrder.xml",
				changeOrderInput.getDocument(), VSIConstants.API_CHANGE_ORDER, api);

	}
	/**********************************************************************************
	 * Method Name : apiCall
	 * 
	 * Description : This method is used for setting and clearing of the templates and 
	 * 				 invocation of API's
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 09/04/2014 This method is used for setting and clearing of the templates and 
	 * 				 	invocation of API's
	 * 
	 * @param api
	 **********************************************************************************/
	public Document apiCall(YFSEnvironment env, String template,
			Document inDoc, String sApiName, YIFApi api) throws YFSException,
			RemoteException {
		
		env.setApiTemplate(sApiName, template);
		Document apiOutput = api.invoke(env, sApiName, inDoc);
		env.clearApiTemplates();

		return apiOutput;

	}

}
