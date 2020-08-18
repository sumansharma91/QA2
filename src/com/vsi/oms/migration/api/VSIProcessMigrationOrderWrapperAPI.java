package com.vsi.oms.migration.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.icu.text.DecimalFormat;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class will be used to created the changeOrder input xml for creating an MO_PAYMENT_HOLD on imported return orders
 * 
 * @author IBM
 *
 */
public class VSIProcessMigrationOrderWrapperAPI extends VSIBaseCustomAPI {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessMigrationOrderWrapperAPI.class);
	
	public Document importOrderWrapper(YFSEnvironment env, Document docInput) {

		env.setTxnObject("MIGRATION_ORDER_FLOW", "Y");
		Element eleMigrationData = docInput.getDocumentElement();
		try {
		/*
		 * �	importSalesOrderDetail
	
				o	Create the input xmls to import the sales order (input of OOB importOrder api)
				o	Check the size of note in the input, if it exceeds 1500 chars , split it into two or more notes to
					keep the size of each note less than 1500 chars.
				o	Stamp all mandatory attributes which are not coming in input.
				o	Validate Collected Amount against Order Total. If there is a mismatch, the raise an exception.
					No further processing is performed. XML is logged into exception console and an alert is raised.
				o	Check if the incoming order already exist by calling getOrderList. If order is existing then process
					the sales order.
				o	Stamp the value of flag �ExtnIsMigrated� at order and order line as �Y�.
		*/
			//ArrayList<Element> alMigrationDataList = SCXmlUtil.getChildren(eleMigrationDataList, VSIConstants.ELE_MIGRATION_DATA);
			//for (Element eleMigrationData : alMigrationDataList) {
			Element eleShipments = SCXmlUtil.getChildElement(eleMigrationData, VSIConstants.ELE_SHIPMENTS);
			HashMap shippedQtyHM = new HashMap();
			if (!YFCObject.isVoid(eleShipments)) {
				
				ArrayList<Element> alShipmentList = SCXmlUtil.getChildren(eleShipments, VSIConstants.ELE_SHIPMENT);
				
				for (Element eleShipment : alShipmentList) 
				{
					
					Element shipmentLinesEle = SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_SHIPMENT_LINES);
					ArrayList<Element> alShipmentLine = SCXmlUtil.getChildren(shipmentLinesEle, VSIConstants.ELE_SHIPMENT_LINE);
					
					for (Element eleShipmentLine : alShipmentLine) 
					{
						Integer qtyFromMap = 0;
						String primeLineNo = eleShipmentLine.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
						String qty = eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY);
						if(shippedQtyHM.containsKey(primeLineNo))
						{
							qtyFromMap = (Integer)shippedQtyHM.get(primeLineNo);
						}
						if(qtyFromMap + Integer.parseInt(qty) > 0)
						{
							shippedQtyHM.put(primeLineNo, qtyFromMap + Integer.parseInt(qty));
						}
					}
					//stampIsMigratedFlagOnShipment(eleShipment);
					//VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_SHIPMENT_SHIPMENT_KEY,
						//	VSIConstants.API_IMPORT_SHIPMENT, SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipment)));
				}
			}
			
				Element eleSalesOrder = SCXmlUtil.getChildElement(eleMigrationData, VSIConstants.ELE_ORDER);
				Element eleReturns = SCXmlUtil.getChildElement(eleMigrationData, VSIConstants.ELE_RETURN_ORDERS);
				String saleHistoryFlag = "N";
				String sOrderType = null;
				if (!YFCObject.isVoid(eleSalesOrder)) 
				{
						saleHistoryFlag = eleSalesOrder.getAttribute("History");
						sOrderType = eleSalesOrder.getAttribute("OrderType");
						Element extnEle = SCXmlUtil.getChildElement(eleSalesOrder, "Extn");
						String isSubscriptionOrder = "N";
						if(YFCCommon.isVoid(extnEle))
						{
							 isSubscriptionOrder = extnEle.getAttribute("ExtnSubscriptionOrder");
						}
						
						
						Element orderLinesEle = SCXmlUtil.getChildElement(eleSalesOrder, VSIConstants.ELE_ORDER_LINES);
						ArrayList<Element> alOrderLine = SCXmlUtil.getChildren(orderLinesEle, VSIConstants.ELE_ORDER_LINE);
						
						for (Element orderLineEle : alOrderLine) 
						{
							Integer qtyFromMap = 0;
							String primeLineNo = orderLineEle.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
							//String qty = eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY);
							if(shippedQtyHM.containsKey(primeLineNo))
							{
								qtyFromMap = (Integer)shippedQtyHM.get(primeLineNo);
								orderLineEle.setAttribute("ShippedQuantity", qtyFromMap.toString());
							}
							
						}
						//stampIsMigratedFlagOnShipment(eleShipment);
						//VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_SHIPMENT_SHIPMENT_KEY,
							//	VSIConstants.API_IMPORT_SHIPMENT, SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipment)));
						checkNoteLength(eleSalesOrder);
						if(!"Marketplace".equals(sOrderType) && !"Y".equals(isSubscriptionOrder))
						{
							validatePayments(eleSalesOrder,false,saleHistoryFlag);
						}
						else
						{
							Element paymentMethods = SCXmlUtil.getChildElement(eleSalesOrder, "PaymentMethods");
							if(!YFCCommon.isVoid(paymentMethods))
							{
								eleSalesOrder.removeChild(paymentMethods);
							}
						}
						
						//stampIsMigratedFlagOnOrder(eleSalesOrder);
						
						VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
								VSIConstants.API_IMPORT_ORDER, SCXmlUtil.createFromString(SCXmlUtil.getString(eleSalesOrder)));
			
				}
			
			/*
					�	importReturnOrder
					
						o	Create the input xmls to import Return order (input of OOB importOrder api) from the order wrapper xml.
						o	Validate Refunded Amount against Order Total. If there is a mismatch, the raise an exception.
							No further processing is performed. XML is logged into exception console and an alert is raised.
						o	Set the value of flag �ExtnIsMigrated�  at order and order line will be set value as �Y� and add it
							in input of return order xml.
		
			*/
				if (!YFCObject.isVoid(eleReturns)) {
					Element eleReturnOrders = SCXmlUtil.getChildElement(eleMigrationData, VSIConstants.ELE_RETURN_ORDERS);
					ArrayList<Element> alReturnOrderList = SCXmlUtil.getChildren(eleReturnOrders, VSIConstants.ELE_ORDER);
					for (Element elereReturnOrder : alReturnOrderList) {
						String returnHistoryFlag = "N";
						returnHistoryFlag = elereReturnOrder.getAttribute("History");
						if(!saleHistoryFlag.equals(returnHistoryFlag))
						{
							throw new YFSException("Mismatch with History flag between Sale and Return order"
									+ SCXmlUtil.getString(elereReturnOrder), "EXTN_MO_HISTORY_FLAG_ISSUE", "Migrated Order Error");
						}
						if(!"Marketplace".equals(sOrderType))
						{
							validatePayments(elereReturnOrder,true,returnHistoryFlag);
						}
						else
						{
							Element paymentMethods = SCXmlUtil.getChildElement(elereReturnOrder, "PaymentMethods");
							if(!YFCCommon.isVoid(paymentMethods))
							{
								elereReturnOrder.removeChild(paymentMethods);
							}
						}
						
						//stampIsMigratedFlagOnOrder(elereReturnOrder);
						VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
								VSIConstants.API_IMPORT_ORDER, SCXmlUtil.createFromString(SCXmlUtil.getString(elereReturnOrder)));
					}
				}
					
			/*
				�	importShipment
				
					o	Loop through all the Shipments in the wrapper.
					o	Import it as sales shipment (input of OOB import importShipment).
					o	Stamp the value of flag �ExtnIsMigrated� at Shipment and ShipmentLine as �Y�.
			*/
				if (!YFCObject.isVoid(eleShipments)) {
					
					ArrayList<Element> alShipmentList = SCXmlUtil.getChildren(eleShipments, VSIConstants.ELE_SHIPMENT);
					
					for (Element eleShipment : alShipmentList) 
					{
						//stampIsMigratedFlagOnShipment(eleShipment);
						VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_SHIPMENT_SHIPMENT_KEY,
								VSIConstants.API_IMPORT_SHIPMENT, SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipment)));
					}
				}
			//}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIProcessMigratedOrderWrapperAPI.importOrderWrapper()");
		}
	
		return null;
	}
	private void validatePayments(Element eleSalesOrder, boolean isReturn, String HistoryFlag) {
		double dProcessedTotal = 0;
		Element elePaymentMethods = SCXmlUtil.getChildElement(eleSalesOrder, VSIConstants.ELE_PAYMENT_METHODS);
		Element elePriceInfo = SCXmlUtil.getChildElement(eleSalesOrder, VSIConstants.ELE_PRICE_INFO);
		if (!YFCObject.isVoid(elePaymentMethods)) {
			ArrayList<Element> alPaymentMethodList = SCXmlUtil.getChildren(elePaymentMethods, VSIConstants.ELE_PAYMENT_METHOD);
			for (Element elePaymentMethod : alPaymentMethodList) {
				if("Y".equals(HistoryFlag))
				{
					Element billTo = SCXmlUtil.getChildElement(elePaymentMethod, "PersonInfoBillTo");
					if(!YFCCommon.isVoid(billTo))
					{
						elePaymentMethod.removeChild(billTo);
					}
				}
				Element elePaymentDetailsList = SCXmlUtil.getChildElement(elePaymentMethod, VSIConstants.ELE_PAYMENT_DETAILS_LIST);
				ArrayList<Element> alPaymentDetailsList = SCXmlUtil.getChildren(elePaymentDetailsList, VSIConstants.ELE_PAYMENT_DETAILS);
				for (Element elePaymentDetails : alPaymentDetailsList) {
					if (VSIConstants.CHARGE.equals(elePaymentDetails.getAttribute(VSIConstants.ATTR_CHARGE_TYPE))) {
						dProcessedTotal = dProcessedTotal + SCXmlUtil.getDoubleAttribute(elePaymentDetails, VSIConstants.ATTR_PROCESSED_AMOUNT);
					}
				}
			}
		}
		DecimalFormat dfRMUp = new DecimalFormat("#.00");
		dProcessedTotal = Double.valueOf(dfRMUp.format(dProcessedTotal));
		
		if (Math.abs(dProcessedTotal) 
				!= Double.valueOf(dfRMUp.format(SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT)))) 
		{
			if(isReturn)
			{
				throw new YFSException("The payments processed amount total does not match the order total for return order: "
						+ SCXmlUtil.getString(eleSalesOrder), "EXTN_MO_RETURNS_ERROR", "Migrated Order Error");
			}
			else
			{
				throw new YFSException("The payments processed amount total does not match the order total for sales order: "
						+ SCXmlUtil.getString(eleSalesOrder), "EXTN_MO_SALES_ERROR", "Migrated Order Error");
			}
			
		}
	}

	private void checkNoteLength(Element eleSalesOrder) {
		Element eleNotes = SCXmlUtil.getChildElement(eleSalesOrder, VSIConstants.ELE_NOTES);
		ArrayList<Element> alNoteList = SCXmlUtil.getChildren(eleNotes, VSIConstants.ELE_NOTE);
		for (Element eleNote : alNoteList) {
			String strNoteText = eleNote.getAttribute(VSIConstants.ATTR_NOTE_TEXT);
			
			if (strNoteText.length() >= 1500) {
				eleNotes.removeChild(eleNote);
				do {
					// Get the index of the last white space character
					int iSpace = strNoteText.lastIndexOf(" ", 1498);
					int iTab = strNoteText.lastIndexOf("\t", 1498);
					int iReturn = strNoteText.lastIndexOf("\r", 1498);
					int iNewLine = strNoteText.lastIndexOf("\n", 1498);
					
					int iIndex = iSpace;
					if (iIndex < iTab) {
						iIndex = iTab;
					}
					if (iIndex < iReturn) {
						iIndex = iReturn;
					}
					if (iIndex < iNewLine) {
						iIndex = iNewLine;
					}
					
					// Break the note into two smaller notes
					String newNote = strNoteText.substring(0, iIndex);
					strNoteText = strNoteText.substring(iIndex, strNoteText.length());
					
					// Add the note to the sales order
					Element eleNewNote = SCXmlUtil.createChild(eleNotes, VSIConstants.ELE_NOTE);
					eleNewNote.setAttribute(VSIConstants.ATTR_NOTE_TEXT, newNote);
				} while (strNoteText.length() >= 1500);
				// Add the last note to the sales order
				Element eleNewNote = SCXmlUtil.createChild(eleNotes, VSIConstants.ELE_NOTE);
				eleNewNote.setAttribute(VSIConstants.ATTR_NOTE_TEXT, strNoteText);
			}
		}
		
	}
}
