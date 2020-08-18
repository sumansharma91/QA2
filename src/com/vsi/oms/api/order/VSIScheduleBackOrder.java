package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc
 * 
 *         Input to class:BackOrder on Success with extended attributes LineType
 *         and Customer PoNo
 * 
 *         1. Extend the template to add lineType and customerPoNo at orderLine
 *         Level for backorder event
 *         
 *         2. if LineType is BOSTS,
 *         	i) call getOrderLineList for the customerPoNo
 *         	ii) call changeOrder API to cancel all the BOSTS
 *         		lines for that order 
 *         
 *         3. If LineType is BOPUS,
 *         	i) call getOrderLineList for the customerPoNo
 *         	ii) call unscheduleorder for order lines in scheduled status
 *         	iii) change fulfillmentType, LineType and ProcureFromNode
 *         		for all the lines with the same customerPONo
 * 
 * 
 * 
 */
public class VSIScheduleBackOrder implements VSIConstants {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIScheduleBackOrder.class);
	YIFApi api;

	public void vsiBackOrderResolve(YFSEnvironment env, Document inXML)
			throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException {
		try {
			if (null != inXML.getDocumentElement()
					&& null != inXML
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)) {

				NodeList orderLineList = inXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int orderLineLength = orderLineList.getLength();

				Element orderEle = (Element) inXML.getElementsByTagName(
						VSIConstants.ELE_ORDER).item(0);
				String OHK = orderEle
						.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);

				//String orderType = orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				String enterpriseCode = SCXmlUtil.getAttribute(orderEle, ATTR_ENTERPRISE_CODE);
				// If MCL order, cancel entire order. Might revisit
				if(enterpriseCode.equals(ENT_MCL)){
					
					Document changeOrderInXML = SCXmlUtil.createDocument(ELE_ORDER);
					Element eleOrder = changeOrderInXML.getDocumentElement();
					SCXmlUtil.setAttribute(eleOrder, ATTR_ORDER_HEADER_KEY, OHK);
					SCXmlUtil.setAttribute(eleOrder, ATTR_OVERRIDE, FLAG_Y);
					SCXmlUtil.setAttribute(eleOrder, ATTR_MODIFICATION_REASON_CODE, NO_INVENTORY);
					SCXmlUtil.setAttribute(eleOrder, ATTR_ACTION, ACTION_CAPS_CANCEL);
					VSIUtils.invokeAPI(env, TEMPLATE_CHANGE_ORDER, API_CHANGE_ORDER, changeOrderInXML);
					return;
				}
			
				HashMap<String, String> CustPoKey = new HashMap<String, String>();
				Boolean bPickDone = false;
				//Boolean bShipDone = false;
				boolean bIsNodeControlHoldPresent = false;
				
				Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleChangeOrder = docChangeOrder.getDocumentElement();
				eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
				Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
				
				String strCancelReason= null;
				ArrayList<Element> arrFTCRulesForCancelWindow = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "CancelReason", ATTR_DEFAULT);
				if(arrFTCRulesForCancelWindow.size()>0){
					Element eleFTCRule = arrFTCRulesForCancelWindow.get(0);
					strCancelReason = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
				}
				
				
				for (int i = 0; i < orderLineLength; i++) {
					
					// TODO: Should all these api calls be done inside this loop, for each line, or should they
					//	be brought outside the loop for better performance?

					Element orderLineEle = (Element) inXML
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)
							.item(i);
					String lineType = orderLineEle
							.getAttribute(VSIConstants.ATTR_LINE_TYPE);
					String customerPoNo = orderLineEle
							.getAttribute(VSIConstants.ATTR_CUST_PO_NO);

					// This fix will schedule the POS STS orders without
					// checking the inventory to avoid backordering again.
					
					/*if (VSIConstants.LINETYPE_STS.equalsIgnoreCase(lineType)
							&& ("POS".equalsIgnoreCase(orderType) || "WEB".equalsIgnoreCase(orderType))) {
						Document scheduleOrderInput = XMLUtil
								.createDocument("ScheduleOrder");
						Element sOrderElement = scheduleOrderInput
								.getDocumentElement();
						sOrderElement.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
						sOrderElement.setAttribute("CheckInventory", "N");
						api = YIFClientFactory.getInstance().getApi();
						api.invoke(env, VSIConstants.API_SCHEDULE_ORDER,
								scheduleOrderInput);
					} */

					// 1B changes to cancel back ordered STS lines - BEGIN
					/*if (VSIConstants.LINETYPE_STS.equalsIgnoreCase(lineType)) {
						Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
						eleChangeOrder.setAttribute("ModificationReasonCode", "Inventory Shortage");
						eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
						eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
					}*/
					
					// 1B changes to cancel back ordered STS lines - BEGIN
					if (VSIConstants.LINETYPE_STH.equalsIgnoreCase(lineType)||VSIConstants.LINETYPE_STS.equalsIgnoreCase(lineType)) {
						Element eleChangeOrderLine = null;
						
						//if the inventory node control is present then do not cancel the line. Only backorder.
						//Once inventory node control is removed after full feed and inventory is not present,
						//line should be canceled.
						//fix for OMS-949
						if(VSIConstants.LINETYPE_STS.equalsIgnoreCase(lineType)){
                            String strOrderedQty = orderLineEle.getAttribute(ATTR_ORD_QTY);
							
							eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
							eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
							String strBackorderedQty = INT_ZER0_NUM;
							Element eleStatusBreakupForBackOrderedQty = SCXmlUtil.getChildElement(orderLineEle, ATTR_STATUS_BREAKUP_BO);
							if(!YFCCommon.isVoid(eleStatusBreakupForBackOrderedQty)){
								Element eleBackOrderedFrom = SCXmlUtil.getChildElement(eleStatusBreakupForBackOrderedQty, ATTR_BO_FROM);
								strBackorderedQty = eleBackOrderedFrom.getAttribute(ATTR_BO_QTY);
								int iUpdatedQty = Integer.parseInt(strOrderedQty)-Integer.parseInt(strBackorderedQty);
								eleChangeOrderLine.setAttribute(ATTR_ORD_QTY, Integer.toString(iUpdatedQty));
								if(!YFCCommon.isVoid(strCancelReason)){
									//OMS-1645:Start
									eleChangeOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, "VSI_CANCEL_NO_INV");
									//OMS-1645:End
								}
								eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
							}
						}
						else{
													
						bIsNodeControlHoldPresent = evaluateNodeControl(env,orderLineEle);
						if(!bIsNodeControlHoldPresent)
						{							
							String strOrderedQty = orderLineEle.getAttribute(ATTR_ORD_QTY);
							String originalOrderedQty = orderLineEle.getAttribute(ATTR__ORG_ORD_QTY);
							String isBackOrdered = null;
							String strBackorderedQty = INT_ZER0_NUM;
							boolean partialBackordered = false;
							Element eleStatusBreakupForBackOrderedQty = SCXmlUtil.getChildElement(orderLineEle, ATTR_STATUS_BREAKUP_BO);
							if(!YFCCommon.isVoid(eleStatusBreakupForBackOrderedQty))
							{
								Element eleBackOrderedFrom = SCXmlUtil.getChildElement(eleStatusBreakupForBackOrderedQty, ATTR_BO_FROM);
								strBackorderedQty = eleBackOrderedFrom.getAttribute(ATTR_BO_QTY);
								Document getOrderLineListDoc = SCXmlUtil.createDocument(ELE_ORDER_LINE);
								Element eleOrderLine = getOrderLineListDoc.getDocumentElement();
								eleOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY));
								eleOrderLine.setAttribute(ATTR_DOCUMENT_TYPE, ATTR_DOCUMENT_TYPE_SALES);		
								Document getOrderLineListOutputDoc = VSIUtils.invokeAPI(env, TEMPLATE_ORDER_LINE_LIST,API_GET_ORDER_LINE_LIST, getOrderLineListDoc);
								Element eleExtn = (Element) getOrderLineListOutputDoc.getElementsByTagName(ELE_EXTN).item(0);
								isBackOrdered = eleExtn.getAttribute(ATTR_IS_BACKORDERED);
								if(log.isDebugEnabled())
									log.info("After Invoking  VSIScheduleBackOrder.vsiBackOrderResolve() - ExtnIsBackordered => "+isBackOrdered);
								
								if (isBackOrdered.equalsIgnoreCase(""))
								{
									if(Integer.parseInt(originalOrderedQty) == Integer.parseInt(strBackorderedQty))
									{
										if(log.isDebugEnabled())
											log.info("originalOrderedQty => "+originalOrderedQty + "strBackorderedQty" + strBackorderedQty);
									Document changeOrderDoc = SCXmlUtil.createDocument(ELE_ORDER);
									Element eleOrder = changeOrderDoc.getDocumentElement();
									eleOrder.setAttribute(ATTR_OVERRIDE, VSIConstants.FLAG_Y);
									eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, OHK);
									Element eleOrderLines = SCXmlUtil.createChild(eleOrder, ELE_ORDER_LINES);
									Element elemOrderLine = SCXmlUtil.createChild(eleOrderLines, ELE_ORDER_LINE);
									elemOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY));
									Element elemExtn = SCXmlUtil.createChild(elemOrderLine, ELE_EXTN);
									elemExtn.setAttribute(ATTR_IS_BACKORDERED, FLAG_Y);
									elemOrderLine.appendChild(elemExtn);
									elemOrderLine.setAttribute(ATTR_ACTION, ACTION_CAPS_MODIFY);
									Element eleOrderHoldTypes = SCXmlUtil.createChild(elemOrderLine, ELE_ORDER_HOLD_TYPES);
									Element elemOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, ELE_ORDER_HOLD_TYPE);
									elemOrderHoldType.setAttribute(ATTR_HOLD_TYPE, ATTR_BACKORDERED_HOLD_TYPE);
									elemOrderHoldType.setAttribute(ATTR_REASON_TEXT, ATTR_MODIFY_REASON_TEXT);
									elemOrderHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);
									eleOrderHoldTypes.appendChild(elemOrderHoldType);
									elemOrderLine.appendChild(eleOrderHoldTypes);
									VSIUtils.invokeAPI(env, API_CHANGE_ORDER, changeOrderDoc);
									
									//Creation of Alert
									Element eleItem = (Element) orderEle.getElementsByTagName(ELE_ITEM).item(0); 
									Document createExceptionDoc = SCXmlUtil.createDocument(ELE_INBOX);
									Element eleInbox = createExceptionDoc.getDocumentElement();
									eleInbox.setAttribute(ATTR_ORDER_HEADER_KEY, OHK);
									eleInbox.setAttribute(ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(ATTR_ORDER_LINE_KEY));
									eleInbox.setAttribute(ATTR_ITEMID, eleItem.getAttribute(ATTR_ITEM_ID));
									eleInbox.setAttribute(ATTR_ORDER_NO, orderEle.getAttribute(ATTR_ORDER_NO));
									eleInbox.setAttribute(ATTR_EXCEPTION_TYPE, ALERT_EXCEPTION_TYPE);
									eleInbox.setAttribute(ATTR_DETAIL_DESCRIPTION, ALERT_DETAIL_DESCRIPTION);
									eleInbox.setAttribute(ATTR_QUEUE_ID, ALERT_BO_QUEUE);
									Element elemOrder = SCXmlUtil.createChild(eleInbox, ELE_ORDER);
									elemOrder.setAttribute(ATTR_ORDER_HEADER_KEY, OHK);
									elemOrder.setAttribute(ATTR_ORDER_NO, orderEle.getAttribute(ATTR_ORDER_NO));
									elemOrder.setAttribute(ATTR_ENTERPRISE_CODE, orderEle.getAttribute(ATTR_ENTERPRISE_CODE));
									elemOrder.setAttribute(ATTR_DOCUMENT_TYPE, orderEle.getAttribute(ATTR_DOCUMENT_TYPE));
									VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION,createExceptionDoc);
									partialBackordered = true;
								}
							}	
							if(log.isDebugEnabled())
								log.info("partialBackordered => "+partialBackordered);
							if(!partialBackordered)
							{
								eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
								eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
								int iUpdatedQty = Integer.parseInt(strOrderedQty)-Integer.parseInt(strBackorderedQty);
								eleChangeOrderLine.setAttribute(ATTR_ORD_QTY, Integer.toString(iUpdatedQty));
								if(!YFCCommon.isVoid(strCancelReason)){
									//OMS-1645:Start
									eleChangeOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, "VSI_CANCEL_NO_INV");
									//OMS-1645:End
								}
								eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
							}
							}
							
							/* Initial Logic was to cancel only discontinued and STS items.
							 * But as per FTC any line which doesn't have inventory should be canceled.
							 * 
							 * //Check if line has a discontinued item. Cancel those quantities.
							Element eleItemDetails = SCXmlUtil.getChildElement(orderLineEle, ELE_ITEM_DETAILS);
							if(!YFCCommon.isVoid(eleItemDetails)){
								Element eleItemDetailsExtn = SCXmlUtil.getChildElement(eleItemDetails, ELE_EXTN);
								if (!YFCCommon.isVoid(eleItemDetailsExtn)){
									String strIsDiscontinuedItem = eleItemDetailsExtn.getAttribute(ATTR_EXTN_IS_DISCOUNTED_ITEM);
									if(!YFCCommon.isVoid(strIsDiscontinuedItem)&&strIsDiscontinuedItem.equals(FLAG_Y)){
										bLineAlreadyAdded = true;
										eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
										eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
										String strBackorderedQty = INT_ZER0_NUM;
										Element eleStatusBreakupForBackOrderedQty = SCXmlUtil.getChildElement(orderLineEle, ATTR_STATUS_BREAKUP_BO);
										if(!YFCCommon.isVoid(eleStatusBreakupForBackOrderedQty)){
											Element eleBackOrderedFrom = SCXmlUtil.getChildElement(eleStatusBreakupForBackOrderedQty, ATTR_BO_FROM);
											strBackorderedQty = eleBackOrderedFrom.getAttribute(ATTR_BO_QTY);
											int iUpdatedQty = Integer.parseInt(strOrderedQty)-Integer.parseInt(strBackorderedQty);
											eleChangeOrderLine.setAttribute(ATTR_ORD_QTY, Integer.toString(iUpdatedQty));
										}
									}
								}
							}*/
							
							/*//Set VSI_FTC_CANCEL_DATE as PROMISE DATE
							String strCancelDate = null;
							String strPromiseDate = null;
							Element eleOrderLineDates = SCXmlUtil.getChildElement(orderLineEle, ELE_ORDER_DATES);
							if(!YFCCommon.isVoid(eleOrderLineDates)){
								ArrayList<Element> arrOrderDates = SCXmlUtil.getChildren(eleOrderLineDates, ELE_ORDER_DATE);
								for(Element eleOrderLineDate : arrOrderDates){
									String strDateType = eleOrderLineDate.getAttribute(ATTR_DATE_TYPE_ID);
									if(strDateType.equalsIgnoreCase("YCD_FTC_CANCEL_DATE")){
										strCancelDate = eleOrderLineDate.getAttribute(ATTR_ACTUAL_DATE);
									}
									if(strDateType.equalsIgnoreCase("YCD_FTC_FIRST_PROMISE_DATE")){
										strPromiseDate = eleOrderLineDate.getAttribute(ATTR_ACTUAL_DATE);
									}
								}
							}
							
							if(YFCCommon.isVoid(strCancelDate)){
								if(!bLineAlreadyAdded){
									eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
									eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
									eleOrderLineDates = SCXmlUtil.createChild(eleChangeOrderLine, ELE_ORDER_DATES);
	
								}else {
									eleOrderLineDates = SCXmlUtil.createChild(eleChangeOrderLine, ELE_ORDER_DATES);
								}
								Element eleOrderLineCancelDate = SCXmlUtil.createChild(eleOrderLineDates, ELE_ORDER_DATE);
								eleOrderLineCancelDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_CANCEL_DATE");
								eleOrderLineCancelDate.setAttribute(ATTR_ACTUAL_DATE, strPromiseDate);
								
							}*/
						}
						}
					}
					/* Commented for 1B code changes
					if (lineType.equalsIgnoreCase("SHIP_TO_STORE")
							&& !(CustPoKey.containsValue(customerPoNo))
							&& bShipDone == false) {
						Document getOrderLineListInput = XMLUtil
								.createDocument("OrderLine");
						Element eleOrderLine = getOrderLineListInput
								.getDocumentElement();
						eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
								customerPoNo);
						eleOrderLine.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
						
						 * Jira 710 Document changeOrderInput =
						 * XMLUtil.createDocument("Order"); Element orderElement
						 * = changeOrderInput.getDocumentElement();
						 * orderElement.
						 * setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
						 * OHK); orderElement.setAttribute("Override", "Y");
						 * orderElement
						 * .setAttribute(VSIConstants.ATTR_SELECT_METHOD,
						 * "WAIT");
						 * orderElement.setAttribute("ModificationReasonCode",
						 * "STSBOCANCEL");
						 * orderElement.setAttribute("ModificationReasonText",
						 * "No inventory for Pick up in store at store and DC");
						 * Element orderLinesElement =
						 * changeOrderInput.createElement("OrderLines");
						 * orderElement.appendChild(orderLinesElement);
						 * 
						 * 
						 * api = YIFClientFactory.getInstance().getApi();
						 * env.setApiTemplate
						 * (VSIConstants.API_GET_ORDER_LINE_LIST,
						 * "global/template/api/VSIGetOrderListBO.xml");
						 * Document outDoc = api.invoke(env,
						 * VSIConstants.API_GET_ORDER_LINE_LIST
						 * ,getOrderLineListInput);
						 * env.clearApiTemplate(VSIConstants
						 * .API_GET_ORDER_LINE_LIST);
						 * 
						 * NodeList orderLineEleList =
						 * outDoc.getElementsByTagName
						 * (VSIConstants.ELE_ORDER_LINE); int orderLineLen=
						 * orderLineEleList.getLength(); bShipDone=true;
						 * 
						 * for(int j=0;j<orderLineLen;j++){
						 * 
						 * Element elementOrderLine = (Element)
						 * outDoc.getElementsByTagName
						 * (VSIConstants.ELE_ORDER_LINE).item(j); String
						 * strOrderLineKey =
						 * elementOrderLine.getAttribute(VSIConstants
						 * .ATTR_ORDER_LINE_KEY);
						 * 
						 * 
						 * Element orderLineElement =
						 * changeOrderInput.createElement("OrderLine");
						 * orderLineElement
						 * .setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
						 * strOrderLineKey);
						 * orderLineElement.setAttribute(VSIConstants
						 * .ATTR_ACTION, "CANCEL");
						 * orderLinesElement.appendChild(orderLineElement);
						 * 
						 * }//end for loop outDoc j
						 * 
						 * NodeList changeorderLineEleList =
						 * changeOrderInput.getElementsByTagName
						 * (VSIConstants.ELE_ORDER_LINE); int
						 * changeOrderLineLength=
						 * changeorderLineEleList.getLength();
						 * if(changeOrderLineLength>0){
						 * ////System.out.println("changeOrderInput"
						 * +XmlUtils.getString(changeOrderInput));
						 * 
						 * api = YIFClientFactory.getInstance().getApi();
						 * api.invoke(env,
						 * VSIConstants.API_CHANGE_ORDER,changeOrderInput); }
						 * 
						 * CustPoKey.put(customerPoNo, "Y"); ** Jira 710
						 

					}// end if lineType check */
					// 1B changes to cancel back ordered STS lines - END

					else if (lineType.equalsIgnoreCase("PICK_IN_STORE")
							&& !(CustPoKey.containsValue(customerPoNo))
							&& bPickDone == false) {
						// //System.out.println("Inside the PICK_IN_STORE condition");
						// String customerPoNo =
						// orderLineEle.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
						Document getOrderLineListInput = XMLUtil
								.createDocument("OrderLine");
						Element eleOrderLine = getOrderLineListInput
								.getDocumentElement();
						eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
								customerPoNo);
						eleOrderLine.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);

						Document changeOrderInput = XMLUtil
								.createDocument(VSIConstants.ELE_ORDER);
						Element orderElement = changeOrderInput
								.getDocumentElement();
						orderElement.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
						orderElement.setAttribute("CheckInventory", "Y");
						orderElement.setAttribute("IgnoreReleaseDate", "Y");
						orderElement.setAttribute(
								VSIConstants.ATTR_SELECT_METHOD, "WAIT");
						orderElement.setAttribute("ModificationReasonCode",
								"PUSTOSTS");
						orderElement.setAttribute("ModificationReasonText",
								"Converting Pickup in store to Ship to Store");

						Document scheduleOrderInput = XMLUtil
								.createDocument("ScheduleOrder");
						Element sOrderElement = scheduleOrderInput
								.getDocumentElement();
						sOrderElement.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
						// jira 710
						sOrderElement.setAttribute("CheckInventory", "Y");

						Element orderLinesElement = changeOrderInput
								.createElement("OrderLines");
						orderElement.appendChild(orderLinesElement);

						api = YIFClientFactory.getInstance().getApi();
						env.setApiTemplate(
								VSIConstants.API_GET_ORDER_LINE_LIST,
								"global/template/api/VSIGetOrderListBO.xml");
						Document outDoc = api.invoke(env,
								VSIConstants.API_GET_ORDER_LINE_LIST,
								getOrderLineListInput);
						env.clearApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST);

						NodeList orderLineEleList = outDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						int orderLineLen = orderLineEleList.getLength();

						Document unScheduleOrderInput = XMLUtil
								.createDocument("UnScheduleOrder");
						Element unScheduleOrderElement = unScheduleOrderInput
								.getDocumentElement();
						unScheduleOrderElement.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
						Element orderLinesEle = unScheduleOrderInput
								.createElement("OrderLines");
						unScheduleOrderElement.appendChild(orderLinesEle);
						bPickDone = true;

						for (int j = 0; j < orderLineLen; j++) {
							Element elementOrderLine = (Element) outDoc
									.getElementsByTagName(
											VSIConstants.ELE_ORDER_LINE)
									.item(j);
							String strOrderLineKey = elementOrderLine
									.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							// 1B changes : checking for status > back ordered - BEGIN
							int maxLineStatus = SCXmlUtil.getIntAttribute(elementOrderLine, VSIConstants.ATTR_MAX_LINE_STATUS, 0);

							if (maxLineStatus > VSIConstants.STATUS_INT_BACKORDERED ) {
							// 1B changes - END
								Element orderLineEleUnsched = unScheduleOrderInput
										.createElement("OrderLine");
								orderLinesEle.appendChild(orderLineEleUnsched);
								orderLineEleUnsched.setAttribute(
										VSIConstants.ATTR_ORDER_LINE_KEY,
										strOrderLineKey);
							}

							Element orderLineElement = changeOrderInput
									.createElement("OrderLine");
							orderLineElement.setAttribute(
									VSIConstants.ATTR_ORDER_LINE_KEY,
									strOrderLineKey);
							orderLineElement.setAttribute(
									VSIConstants.ATTR_FULFILLMENT_TYPE,
									"SHIP_TO_STORE");
							/*orderLineElement
									.setAttribute(
											VSIConstants.ATTR_PROCURE_FROM_NODE,
											"9001");*/
							
							// Added logic to send out BOPUS to BOSTS conversation email
						   //System.out.println("HAYYYYY"+customerPoNo);
						    if(!YFCObject.isVoid(customerPoNo)){
						    	//System.out.println("I AM HERE");
								Document emailDoc = VSISendRdyForPckupAndReminderEmails.getEmailContent(env,VSIConstants.ATTR_CUST_PO_NO, customerPoNo); 
								VSIUtils.invokeService(env,"VSISendBOPUSToBOSTSConversionEmail", emailDoc);
								
							}
							 
							Element eleOrderLineSourcingControls = SCXmlUtil.createChild(orderLineElement, "OrderLineSourcingControls");
							Element eleOrderLineSourcingCntrl =  SCXmlUtil.createChild(eleOrderLineSourcingControls, "OrderLineSourcingCntrl");
							eleOrderLineSourcingCntrl.setAttribute("Node", orderLineEle.getAttribute("ShipNode"));
							eleOrderLineSourcingCntrl.setAttribute("InventoryCheckCode", "NOINV");
							eleOrderLineSourcingCntrl.setAttribute("ReasonText", "SHIP_TO_STORE");
							orderLineElement.setAttribute(
									VSIConstants.ATTR_LINE_TYPE,
									"SHIP_TO_STORE");
							//OMS-815 - Set ExtnPublished Flag as 'N'							
							Element orderLineExtnEle = changeOrderInput.createElement("Extn");
							orderLineExtnEle.setAttribute("ExtnPublished", "N");
							orderLineElement.appendChild(orderLineExtnEle);
							//OMS-815 Ends	
							orderLinesElement.appendChild(orderLineElement);
						}

						NodeList unorderLineEleList = unScheduleOrderInput
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						int unOrderLineLength = unorderLineEleList.getLength();
						if (unOrderLineLength > 0) {
							if (log.isVerboseEnabled()) {
								log.verbose("unScheduleOrderInput Document is: \n" + XMLUtil.getXMLString(unScheduleOrderInput));
							}
							api = YIFClientFactory.getInstance().getApi();
							api.invoke(env, VSIConstants.API_UNSCHEDULE_ORDER,
									unScheduleOrderInput);
						}

						NodeList changeorderLineEleList = changeOrderInput
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						int changeOrderLineLength = changeorderLineEleList
								.getLength();
						if (changeOrderLineLength > 0) {

							if (log.isVerboseEnabled()) {
								log.verbose("changeOrderInput Document is: \n" + XMLUtil.getXMLString(changeOrderInput));
								log.verbose("scheduleOrderInput Document is: \n" + XMLUtil.getXMLString(scheduleOrderInput));
							}
							
							api = YIFClientFactory.getInstance().getApi();
							api.invoke(env, VSIConstants.API_CHANGE_ORDER,
									changeOrderInput);
							api.invoke(env, VSIConstants.API_SCHEDULE_ORDER,
									scheduleOrderInput);
						}
						CustPoKey.put(customerPoNo, "Y");

					}// end if lineType PICK_IN_STORE
					
					
					

				}// end main for loop inXML

				// Added for 1B changes to cancel back ordered STS lines
				if (eleChangeOrderLines.hasChildNodes()) 
				{
					if(!docChangeOrder.getDocumentElement().hasAttribute("Override"))
					{
						docChangeOrder.getDocumentElement().setAttribute("Override", "Y");
					}
					VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
							VSIConstants.API_CHANGE_ORDER, docChangeOrder);
				}
			}

		} catch (YFSException e) {
			e.printStackTrace();
			String error = e.getMessage();
			String errorCode = e.getErrorCode();
			String errorDesc = e.getErrorDescription();
			throw new YFSException(errorCode, errorDesc, error);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();

		}

	}// end class

	private boolean evaluateNodeControl(YFSEnvironment env, Element orderLineEle) throws YFSException, RemoteException, YIFClientCreationException {
		
		Element eleItem = SCXmlUtil.getChildElement(orderLineEle, ELE_ITEM);
		Document docInventoryNodeControl = SCXmlUtil.createDocument(ELE_INVENTORY_NODE_CONTROL);
		Element eleInventoryNodeControl = docInventoryNodeControl.getDocumentElement();
		eleInventoryNodeControl.setAttribute(ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
		eleInventoryNodeControl.setAttribute(ATTR_PRODUCT_CLASS, eleItem.getAttribute(ATTR_PRODUCT_CLASS));
		//OMS-898 : Start
		//eleInventoryNodeControl.setAttribute(ATTR_ORGANIZATION_CODE, eleItem.getAttribute(ATTR_ORGANIZATION_CODE));
		//OMS-898 : End
		eleInventoryNodeControl.setAttribute(ATTR_UOM, eleItem.getAttribute(ATTR_UOM));
		
		Document docInventoryNodeControlList = VSIUtils.invokeAPI(env, API_GET_INVENTORY_NODE_CONTROL_LIST, docInventoryNodeControl);
		Element eleInventoryNodeControlList = docInventoryNodeControlList.getDocumentElement();
		ArrayList<Element> arrEleInventoryNodeControls = SCXmlUtil.getChildren(eleInventoryNodeControlList, ELE_INVENTORY_NODE_CONTROL);
		if(arrEleInventoryNodeControls.size()>0){
			return true;
		}
		
		return false;
	}

}
