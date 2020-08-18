package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 *         1. call scheduleorder service if there are schedule lines and call
 *         Transfer service if there are TO lines 2. if for a particular
 *         custpono if there are any BO lines, do not call service
 * 
 *         Algo: 1. navigate through orderlines, check if minlinestatus is 1500,
 *         if yes put a value in map (custpono, value sched), if the map already
 *         exists and the value is diff from sched, make it as na 1. navigate
 *         through orderlines, check if minlinestatus is 2160 &
 *         extnpublished!=y, if yes put a value in map (custpono, value to), if
 *         the map already exists and the value is diff from to, make it as na
 *         1. navigate through orderlines, check if minlinestatus is 1300, if
 *         yes put a value in map (custpono, value na) 4. end of the loop, loop
 *         through the map, if any to call to service, if any sched call sched
 *         service
 *
 */
public class VSIScheduleTO implements VSIConstants{

	public static final String ELE_ORDER_STATUSES = "OrderStatuses";
	private YFCLogCategory log = YFCLogCategory.instance(VSIScheduleTO.class);
	YIFApi api;

	public void vsiScheduleTO(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiScheduleTO================================");
			log.debug("VSIScheduleTO : Printing Input XML :" +SCXmlUtil.getString(inXML));
		}
		try {
			//OMS-2015- Start
			boolean bPISToSTSConvertion = false;
			String custPoNo=null;
			//OMS-2015- End
			//OMS-1243 : Start
			ArrayList<Element> alFreeLinesList=(ArrayList<Element>)env.getTxnObject("RejectAssignment");
			if(alFreeLinesList!=null && alFreeLinesList.size()>0){
				if(log.isDebugEnabled()){
					log.debug("Rejected the assignment for the free lines, cancelling the freeline");
				}
				//creating input for change order
				Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleChangeOrder = docChangeOrder.getDocumentElement();
				eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				eleChangeOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, "GWP_ITEM_CANCEL");
				
				Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
				for (Element eleFreeLine : alFreeLinesList) {
					Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleFreeLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
					SCXmlUtil.setAttribute(eleChangeOrder, ATTR_ORDER_HEADER_KEY,  eleFreeLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				}
				
				if(log.isDebugEnabled()){
					log.debug("Input for change order for GWPItemCancel:  "+XMLUtil.getXMLString(docChangeOrder));
				}
				VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_ORDER_HEADER_KEY,
						VSIConstants.API_CHANGE_ORDER, docChangeOrder);
				return;
				
			}
			//OMS-1243 :end
			if(log.isDebugEnabled()){
				log.debug("No free lines are rejected, continue with existing logic for normal item");
			}
			Map<String, String> orderLinekeyMap = new HashMap<String, String>();

			Element order = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_ORDER).item(0);
			Element orderLines = (Element) inXML.getElementsByTagName(
					VSIConstants.ELE_ORDER_LINES).item(0);

			String ohk = order.getAttribute("OrderHeaderKey");
			String strOrderDate = order.getAttribute(ATTR_ORDER_DATE);

			Document changeOrderInput = XMLUtil.createDocument("Order");
			Element sOrderElement = changeOrderInput.getDocumentElement();
			sOrderElement.setAttribute(ATTR_OVERRIDE, FLAG_Y);
			sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
			

			Element orderLinesElement = changeOrderInput
					.createElement("OrderLines");
			sOrderElement.appendChild(orderLinesElement);

			boolean bSTHLinesPresent = false;
			//OMS-1133 : start
			boolean bIsPickInStore=false;
			//OMS-1133 : end
			//OMS-1645: Start
			String strEntCode=order.getAttribute("EnterpriseCode");
			String strBOCounter=null;
			//String strBOCounterName=null;
			//OMS-1645 : End
			NodeList orderLineList = orderLines
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			NodeList extnList = orderLines
					.getElementsByTagName(VSIConstants.ELE_EXTN);
			
			String strCancelWindow = null;
			String strESDBoundary = null;
			int iBOCounter=1;
			ArrayList<Element> arrFTCRulesForCancelWindow = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "CancelWindow", ATTR_DEFAULT);
			if(arrFTCRulesForCancelWindow.size()>0){
				Element eleFTCRule = arrFTCRulesForCancelWindow.get(0);
				strCancelWindow = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
			}
			ArrayList<Element> arrFTCRulesForESDBoundary = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "ESDBoundary", ATTR_DEFAULT);
			if(arrFTCRulesForESDBoundary.size()>0){
				Element eleFTCRule = arrFTCRulesForESDBoundary.get(0);
				strESDBoundary = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
			}
			//OMS-1645:Start	
			ArrayList<Element> arrFTCRulesForBOCounter = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "BOCounter", ATTR_DEFAULT);
			if(arrFTCRulesForBOCounter.size()>0){
				Element eleBOCounter = arrFTCRulesForBOCounter.get(0);
				strBOCounter = eleBOCounter.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
				if(log.isDebugEnabled()){
					log.debug("Configured value for BO Counter"+strBOCounter);
				}
				//strBOCounterName=eleBOCounter.getAttribute(ATTR_CODE_LONG_DESCRIPTION);
			}		
			if(!YFCCommon.isVoid(strBOCounter))
			iBOCounter=Integer.parseInt(strBOCounter);
			//OMS-1645:End
			for (int i = 0; i < orderLineList.getLength(); i++) {

				Element orderLineElement = (Element) orderLineList.item(i);
				String strLineType = orderLineElement.getAttribute(ATTR_LINE_TYPE);
				String strOrderLineKey = orderLineElement.getAttribute(ATTR_ORDER_LINE_KEY);
				if(log.isDebugEnabled()){
					log.debug("strLineType"+strLineType);
					log.debug("strOrderLineKey"+strOrderLineKey);
				}
				if(strLineType.equalsIgnoreCase(LINETYPE_STH)){
					bSTHLinesPresent = true;
					//Set FTC dates or line Type STH for 1B
					Element eleSchedules = SCXmlUtil.getChildElement(orderLineElement, ELE_SCHEDULES);
					ArrayList<Element> arLSchedule = SCXmlUtil.getChildren(eleSchedules, ELE_SCHEDULE);
					String strExpectedShipDate = null;
					boolean bAllocatedAgainstFuture = false;
					boolean bAddLineToChangeOrder = false;
					boolean bWasBOOnline = false;
					boolean bESDGreaterThanPDDuringFirstAllocation = false;
					boolean bESDGreaterThanPDAfterSecondAllocation = false;
					
					boolean bWasBackorderedFromNodePreviously= false;
					Element eleOrderStatuses = SCXmlUtil.getChildElement(orderLineElement, ELE_ORDER_STATUSES);
					ArrayList<Element> arrOrderStatus = SCXmlUtil.getChildren(eleOrderStatuses, ELE_ORDER_STATUS);
					for(Element eleOrderStatus : arrOrderStatus){
						String strStatus = eleOrderStatus.getAttribute(ATTR_STATUS);
						if(strStatus.equalsIgnoreCase(STATUS_BACKORDERED_FROM_NODE)){
							bWasBackorderedFromNodePreviously = true;
						}
					}
					
					int arLScheduleLength1 = arLSchedule.size();
					boolean bIsBackordered=false;
					for(int iSchedules=0;iSchedules<arLScheduleLength1;iSchedules++){
						Element eleSchedule = (Element) arLSchedule.get(iSchedules);
						String strProductAvailabilityDate = eleSchedule.getAttribute(ATTR_PRODUCT_AVAILABILITY_DATE);
						//If Product Availability Date is greater than current date, assume that
						//order line is allocated against future inventory
						if(!YFCCommon.isVoid(strProductAvailabilityDate) &&
								dateGreaterThanCurrentDate(strProductAvailabilityDate)){
							bAllocatedAgainstFuture = true;
							strExpectedShipDate = eleSchedule.getAttribute(ATTR_EXPECTED_SHIPMENT_DATE);
						}
						if(YFCCommon.isVoid(strProductAvailabilityDate)){
							bIsBackordered = true;
						}
					}
					
					//Check values of current FTC dates.
					Element eleDates = null;
					Element eleFTCPromiseDate = null;
					Element eleFTCCancelDate = null;
					Element eleFirstFTCPromiseDate = null;
					//OMS-1645 : Start
					//Element eleSecondFTCPromiseDate=null;
					Element eleNextFTCPromiseDate=null;
					//Element eleFTCDate=null;
					//OMS-1645 : End
					eleDates = SCXmlUtil.getChildElement(orderLineElement, ELE_ORDER_DATES);
					if(!YFCCommon.isVoid(eleDates)){
						eleFTCPromiseDate = XMLUtil.getElementByXPath(inXML, "Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_PROMISE_DATE']");
						eleFTCCancelDate = XMLUtil.getElementByXPath(inXML, "Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_CANCEL_DATE']");
						eleFirstFTCPromiseDate = XMLUtil.getElementByXPath(inXML, "Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_FIRST_PROMISE_DATE']");
						//OMS-1645 : Start
						Element eleCurrentFTCDate=null;
						ArrayList<String> arrCounter=fetchCounterName(env);
						String strCounterName=null;
						String strDateTypeID=null;
						int iCurrentDateCounter=1;
						for(int j=0;j<iBOCounter;j++)
						{
							strCounterName=arrCounter.get(j);
							strDateTypeID="YCD_FTC_"+strCounterName+"_PROMISE_DATE";
							if(log.isDebugEnabled()){
								log.debug("strDateTypeID"+strDateTypeID);
							}
							eleNextFTCPromiseDate=XMLUtil.getElementByXPath(inXML, "Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='"+strDateTypeID+"']");
							if (YFCObject.isVoid(eleNextFTCPromiseDate))
							{
								break;
							}
							else
							{
								iCurrentDateCounter=j+1;
								eleCurrentFTCDate=XMLUtil.getElementByXPath(inXML, "Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='"+strDateTypeID+"']");
								
							}
						}
						if(log.isDebugEnabled()){
							log.debug("iCurrentDateCounter"+iCurrentDateCounter);
						}
						//OMS-1645 : End
						if (!YFCObject.isVoid(eleCurrentFTCDate)) {
							String strFTCFirstPromiseDate=eleCurrentFTCDate.getAttribute(ATTR_EXPECTED_DATE);
							if(YFCCommon.isVoid(strFTCFirstPromiseDate)){
								strFTCFirstPromiseDate = eleCurrentFTCDate.getAttribute(ATTR_ACTUAL_DATE);
							}
							if(!YFCCommon.isVoid(strFTCFirstPromiseDate) && VSIUtils.differenceBetweenDates(strOrderDate, strFTCFirstPromiseDate)>0){
								bWasBOOnline = true;
							}
							
							if(!YFCCommon.isVoid(strExpectedShipDate)&& !YFCCommon.isVoid(strFTCFirstPromiseDate) && 
									VSIUtils.differenceBetweenDates(strExpectedShipDate, strFTCFirstPromiseDate)>0 
									&& !bWasBackorderedFromNodePreviously){
								bESDGreaterThanPDDuringFirstAllocation = true;
							}
							
							if(bAllocatedAgainstFuture){
								if (!YFCCommon.isVoid(strEntCode) && ("VSI.com".equals(strEntCode)|| "ADP".equals(strEntCode)))
								{
									
								//If line is allocated against future inventory
								if(YFCCommon.isVoid(eleFTCPromiseDate)){
									eleFTCPromiseDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
									eleFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_PROMISE_DATE");
									eleFTCPromiseDate.setAttribute(ATTR_ACTUAL_DATE, strFTCFirstPromiseDate);
									bAddLineToChangeOrder = true;
								}
								//OMS-1645 : Start
								int iDiffFirstPromiseAndESD=VSIUtils.signedDifferenceBetweenDates(strFTCFirstPromiseDate,strExpectedShipDate);
								if(YFCCommon.isVoid(eleNextFTCPromiseDate) && iDiffFirstPromiseAndESD>0) 
								{
									eleFTCPromiseDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
									eleFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, strDateTypeID);
									eleFTCPromiseDate.setAttribute(ATTR_ACTUAL_DATE, strExpectedShipDate);
									bAddLineToChangeOrder = true;
									
								}
								if(!YFCCommon.isVoid(eleNextFTCPromiseDate))
								{
									String strFTCSecondPromiseDate=eleNextFTCPromiseDate.getAttribute(ATTR_ACTUAL_DATE);
									if(!YFCCommon.isVoid(strExpectedShipDate)&& !YFCCommon.isVoid(strFTCSecondPromiseDate) && 
											VSIUtils.differenceBetweenDates(strExpectedShipDate, strFTCSecondPromiseDate)>0 
											&& !bWasBackorderedFromNodePreviously){
										bESDGreaterThanPDAfterSecondAllocation = true;
								}
								//OMS-1192 :Start
								//Stamp FTC cancel date only if First promise date-Expected shipment date > POBufferDays
								String strPOBufferDays=null;
								boolean bStampFTCCancelDate=true;
								ArrayList<Element> arrFTCRulesForPOBufferDays = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "POBufferDays", ATTR_DEFAULT);
								if(arrFTCRulesForPOBufferDays.size()>0){
									Element elePOBufferDays = arrFTCRulesForPOBufferDays.get(0);
									strPOBufferDays = elePOBufferDays.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
								}
								if(!YFCCommon.isStringVoid(strPOBufferDays) && !(YFCCommon.isStringVoid(strFTCSecondPromiseDate))
										&&!(YFCCommon.isStringVoid(strExpectedShipDate))&& (iCurrentDateCounter>=iBOCounter)){
									int iPOBufferDays=Integer.parseInt(strPOBufferDays);
									int iDiffPromiseAndESD=VSIUtils.differenceBetweenDates(strFTCSecondPromiseDate,strExpectedShipDate);
									if(log.isDebugEnabled()){
										log.debug("POBufferDays  : "+iPOBufferDays + "DiffPromiseAndESD " + iDiffPromiseAndESD);
									}
									if(iDiffPromiseAndESD<=iPOBufferDays)
										bStampFTCCancelDate=false;
								}
								if(YFCCommon.isVoid(eleFTCCancelDate) && bStampFTCCancelDate){
									//OMS-1192 :End	
									eleFTCCancelDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
									eleFTCCancelDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_CANCEL_DATE");
									if((bWasBackorderedFromNodePreviously||bWasBOOnline||bESDGreaterThanPDAfterSecondAllocation) &&
											!(YFCCommon.isVoid(strFTCSecondPromiseDate))&&!(YFCCommon.isVoid(strExpectedShipDate))){
										if(!YFCCommon.isVoid(strESDBoundary) && 
												VSIUtils.differenceBetweenDates(strFTCSecondPromiseDate,strExpectedShipDate)<=Integer.parseInt(strESDBoundary)){
											//if ESD is within 30 days of PD, stamp ESD as cancel date
											if(VSIUtils.signedDifferenceBetweenDates(strFTCSecondPromiseDate,strExpectedShipDate)<0){
												eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strFTCSecondPromiseDate);	
											}else{
												eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strExpectedShipDate);
											}
											bAddLineToChangeOrder = true;
										}else{
											//if ESD is more than 30 days from PD, stamp PD+30 days as cancel date
											if(!YFCCommon.isVoid(strCancelWindow)){
												
												if(VSIUtils.signedDifferenceBetweenDates(strFTCSecondPromiseDate,strExpectedShipDate)<0){
													eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strFTCSecondPromiseDate);	
												}else{
													eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE,
															VSIUtils.addDaysToPassedDateTime(strFTCSecondPromiseDate, strCancelWindow));
												}
												bAddLineToChangeOrder = true;
											}	
										}
									}	
								}
							}	
							//OMS-1645 : End
							}
								else
								{
									// Existing Flow for Market Place Orders

									//If line is allocated against future inventory
									if(YFCCommon.isVoid(eleFTCPromiseDate)){
										eleFTCPromiseDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
										eleFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_PROMISE_DATE");
										eleFTCPromiseDate.setAttribute(ATTR_ACTUAL_DATE, strFTCFirstPromiseDate);
										bAddLineToChangeOrder = true;
									}
									
									//OMS-1192 :Start
									//Stamp FTC cancel date only if First promise date-Expected shipment date > POBufferDays
									String strPOBufferDays=null;
									boolean bStampFTCCancelDate=true;
									ArrayList<Element> arrFTCRulesForPOBufferDays = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "POBufferDays", ATTR_DEFAULT);
									if(arrFTCRulesForPOBufferDays.size()>0){
										Element elePOBufferDays = arrFTCRulesForPOBufferDays.get(0);
										strPOBufferDays = elePOBufferDays.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
									}
									if(!YFCCommon.isStringVoid(strPOBufferDays) && !(YFCCommon.isStringVoid(strFTCFirstPromiseDate))
											&&!(YFCCommon.isStringVoid(strExpectedShipDate))){
										int iPOBufferDays=Integer.parseInt(strPOBufferDays);
										int iDiffPromiseAndESD=VSIUtils.differenceBetweenDates(strFTCFirstPromiseDate,strExpectedShipDate);
										if(log.isDebugEnabled()){
											log.debug("POBufferDays  : "+iPOBufferDays + "DiffPromiseAndESD " + iDiffPromiseAndESD);
										}
										if(iDiffPromiseAndESD<=iPOBufferDays)
											bStampFTCCancelDate=false;
									}
									if(YFCCommon.isVoid(eleFTCCancelDate) && bStampFTCCancelDate){
										//OMS-1192 :End	
										eleFTCCancelDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
										eleFTCCancelDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_CANCEL_DATE");
										if((bWasBackorderedFromNodePreviously||bWasBOOnline||bESDGreaterThanPDDuringFirstAllocation) &&
												!(YFCCommon.isVoid(eleFirstFTCPromiseDate))&&!(YFCCommon.isVoid(strExpectedShipDate))){
											if(!YFCCommon.isVoid(strESDBoundary) && 
													VSIUtils.differenceBetweenDates(strFTCFirstPromiseDate,strExpectedShipDate)<=Integer.parseInt(strESDBoundary)){
												//if ESD is within 30 days of PD, stamp ESD as cancel date
												if(VSIUtils.signedDifferenceBetweenDates(strFTCFirstPromiseDate,strExpectedShipDate)<0){
													eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strFTCFirstPromiseDate);	
												}else{
													eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strExpectedShipDate);
												}
												bAddLineToChangeOrder = true;
											}else{
												//if ESD is more than 30 days from PD, stamp PD+30 days as cancel date
												if(!YFCCommon.isVoid(strCancelWindow)){
													
													if(VSIUtils.signedDifferenceBetweenDates(strFTCFirstPromiseDate,strExpectedShipDate)<0){
														eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, strFTCFirstPromiseDate);	
													}else{
														eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE,
																VSIUtils.addDaysToPassedDateTime(strFTCFirstPromiseDate, strCancelWindow));
													}
													bAddLineToChangeOrder = true;
												}	
											}
										}	
									}
								
								}
							
							}
							else{
								//if line is allocated against on hand inventory.
								if(!bIsBackordered && (bWasBackorderedFromNodePreviously||bWasBOOnline) 
										&& YFCCommon.isVoid(eleFTCCancelDate) && !YFCCommon.isVoid(strCancelWindow)){
									eleFTCCancelDate = SCXmlUtil.createChild(eleDates, ELE_ORDER_DATE);
									eleFTCCancelDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_CANCEL_DATE");
									if(!YFCCommon.isVoid(eleFirstFTCPromiseDate) && !YFCCommon.isVoid(strFTCFirstPromiseDate)){
										eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE,
												VSIUtils.addDaysToPassedDateTime(strFTCFirstPromiseDate, strCancelWindow));
										bAddLineToChangeOrder = true;
									}	
								}
							}
							if(bAddLineToChangeOrder){
								Element orderLineEle = changeOrderInput
										.createElement("OrderLine");
								orderLinesElement.appendChild(orderLineEle);
								
	
								orderLineEle.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
								SCXmlUtil.importElement(orderLineEle, eleDates);
								//orderLineEle.appendChild(eleDates);
								
							}
						}
					}
				}else{
				
					Element orderLineEle = changeOrderInput
							.createElement("OrderLine");
					Element orderLineExtnEle = changeOrderInput
							.createElement("Extn");
	
					
					Element extnElement = (Element) orderLineElement
							.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
					String minLineStatus = orderLineElement
							.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
					String maxLineStatus = orderLineElement
							.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
					custPoNo = orderLineElement
							.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
					String extnPub = extnElement.getAttribute("ExtnPublished");
					String lineType = orderLineElement.getAttribute("LineType");
					String orderLineKey = orderLineElement
							.getAttribute("OrderLineKey");
					if(log.isDebugEnabled()){
						log.debug("orderLineKey"+orderLineKey);
						log.debug("lineType"+lineType);
						log.debug("ExtnPublished"+extnPub);
					}
					String shipNode = orderLineElement
							.getAttribute("ShipNode");
					if(log.isDebugEnabled()){
						log.debug("shipNode"+shipNode);
					}
					Element orderLineSrcControl = null;
					Element orderLineSrcControls = SCXmlUtil.getChildElement(orderLineElement, "OrderLineSourcingControls");
					if(!YFCCommon.isVoid(orderLineSrcControls))
					{
						 orderLineSrcControl = SCXmlUtil.getChildElement(orderLineSrcControls, "OrderLineSourcingCntrl");
						 if(log.isDebugEnabled()){
							 log.debug("OrderLineSourcingCntrl"+SCXmlUtil.getString(orderLineSrcControl));
						 }
					}
					
					Boolean flag = orderLinekeyMap.containsKey(custPoNo);
					if(log.isDebugEnabled()){
						log.debug("flag"+flag);
					}
					String val = "";
					if(log.isDebugEnabled()){
						log.debug("minLineStatus"+minLineStatus);
						log.debug("maxLineStatus"+maxLineStatus);
					}
					if (minLineStatus.equalsIgnoreCase("1500")
							&& maxLineStatus.equalsIgnoreCase("1500")
							&& !(extnPub.equalsIgnoreCase("Y"))
							&& lineType.equalsIgnoreCase("PICK_IN_STORE")) {
						if(log.isDebugEnabled()){
							log.debug("Condition 1500");
						}
						if (flag == false)
							orderLinekeyMap.put(custPoNo, "SCHED");
	
						else {
							val = orderLinekeyMap.get(custPoNo);
							if(log.isDebugEnabled()){
								log.debug("val"+val);
							}
							if (!(val.equalsIgnoreCase("SCHED"))) {
								orderLinekeyMap.put(custPoNo, "NA");
							}
						}
						if(log.isDebugEnabled()){
							log.debug("final val"+val);
						}
						// Changes for OMS-808
						// Add Order line tag to the changeOrder input XML having
						// ExtnPublished as 'Y'
						if (flag == false || val.equalsIgnoreCase("SCHED")) {
							if(log.isDebugEnabled()){
								log.debug("flag is false and val is SCHED");
							}
							orderLineEle.setAttribute("OrderLineKey", orderLineKey);
							orderLineExtnEle.setAttribute("ExtnPublished", "Y");
							orderLineEle.appendChild(orderLineExtnEle);
							orderLinesElement.appendChild(orderLineEle);
						}
						//OMS-882  adding status check for backordered (status 1300)qty
					} else if ((minLineStatus.equalsIgnoreCase("2160")||minLineStatus.equalsIgnoreCase("1300"))
							&& !(extnPub.equalsIgnoreCase("Y"))
							&& maxLineStatus.equalsIgnoreCase("2160")) {
						if(log.isDebugEnabled()){
							log.debug("Condition 2160");
							log.debug("flag"+flag);
						}
						//OMS-1133 : start
						if("PICK_IN_STORE".equalsIgnoreCase(lineType))
						{
							bIsPickInStore=true;
						}
						//OMS-1133 : end
						if (flag == false) {
							orderLinekeyMap.put(custPoNo, "TO");
							if(log.isDebugEnabled()){
								log.debug("when flag is false");
							}
						}
						else {
							if(log.isDebugEnabled()){
								log.debug("when flag is true");
							}
							val = orderLinekeyMap.get(custPoNo);
							if(log.isDebugEnabled()){
								log.debug("val"+val);
							}
							if (!(val.equalsIgnoreCase("TO"))) {
								
								orderLinekeyMap.put(custPoNo, "NA");
							}
	
						}
						//OMS-1133:start 
						boolean bEleOrderLineExist=false;
						//OMS-1133:end
						// Changes for OMS-808
						// Add Order line tag to the changeOrder input XML having
						// ExtnPublished as 'Y'
						if (flag == false || val.equalsIgnoreCase("TO")) {
							if(log.isDebugEnabled()){
								log.debug("flag false,val is TO,settingExtnPublished flag as Y");
							}
							bEleOrderLineExist=true;
							orderLineEle.setAttribute("OrderLineKey", orderLineKey);
							orderLineExtnEle.setAttribute("ExtnPublished", "Y");
							//OMS-1133: start
							if(bIsPickInStore)
							{
								if(log.isDebugEnabled()){
									log.debug("bIsPickInStore is true, changing fulfillmentType and LineType to STS");
								}
								orderLineEle.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE,"SHIP_TO_STORE");
								orderLineEle.setAttribute(VSIConstants.ATTR_LINE_TYPE,"SHIP_TO_STORE");
								bPISToSTSConvertion=true;
							}
							//OMS-1133: end
							orderLineEle.appendChild(orderLineExtnEle);
							orderLinesElement.appendChild(orderLineEle);
						}
						/*OMS-1133: start : If LineType is PICK_IN_STORE and bEleOrderLineExist is false, then we need to 
						add OrderLine element in the OrderLines element after stamping FulfillmentType and LineType as 
						SHIP_TO_STORE*/
						if(!bEleOrderLineExist && bIsPickInStore)
						{
							if(log.isDebugEnabled()){
								log.debug("bIsPickInStore is true,bEleOrderLineExist is false ,changing fulfillmentType and LineType to STS");
							}
							orderLineEle.setAttribute("OrderLineKey", orderLineKey);
							orderLineEle.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE,"SHIP_TO_STORE");
							orderLineEle.setAttribute(VSIConstants.ATTR_LINE_TYPE,"SHIP_TO_STORE");
							orderLineEle.appendChild(orderLineExtnEle);
							orderLinesElement.appendChild(orderLineEle);
						}
						//OMS-1133: end
						if(!YFCCommon.isVoid(orderLineSrcControl))
						{
							if(log.isDebugEnabled()){
								log.debug("Remove sourceControl");
							}
							Element ordrLineSrControls = SCXmlUtil.createChild(orderLineEle, "OrderLineSourcingControls");
							Element ordrLineSrControl = SCXmlUtil.createChild(ordrLineSrControls, "OrderLineSourcingCntrl");
							ordrLineSrControl.setAttribute("Action", "REMOVE");
							ordrLineSrControl.setAttribute("InventoryCheckCode", "NOINV");
							ordrLineSrControl.setAttribute("Node", shipNode);
							//OMS-1555 : start
							orderLineEle.setAttribute("OrderLineKey", orderLineKey);
							orderLinesElement.appendChild(orderLineEle);
							//OMS-1555 : end
						}
	
					} else if (minLineStatus.equalsIgnoreCase("9000")
							&& !(extnPub.equalsIgnoreCase("Y"))
							&& maxLineStatus.equalsIgnoreCase("9000")) {
						if(log.isDebugEnabled()){
							log.debug("Condition 9000");
						}
						orderLineElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO,
								"");
					} else {
						if(log.isDebugEnabled()){
							log.debug("last condition");
							log.debug("flag"+flag);
						}
						// OMS-815 - Set ExtnPublished Flag as 'Y' for all STS lines
						// which are not in 2160 status (Mostly for back ordered and
						// unscheduled lines).
						if (flag) {
							val = orderLinekeyMap.get(custPoNo);
						}
						if (flag == false
								|| (!(val.equalsIgnoreCase("TO")) && !(val
										.equalsIgnoreCase("SCHED")))) {
							if(log.isDebugEnabled()){
								log.debug("val"+val);
							}
							orderLinekeyMap.put(custPoNo, "NA");
						} else if (val.equalsIgnoreCase("TO")) {
							
							if(log.isDebugEnabled()){
								log.debug("val"+val);
							}
							orderLineEle.setAttribute("OrderLineKey", orderLineKey);
							orderLineExtnEle.setAttribute("ExtnPublished", "Y");
							orderLineEle.appendChild(orderLineExtnEle);
							orderLinesElement.appendChild(orderLineEle);
						}
					}
				}
			}// end flag false

			for (Map.Entry<String, String> entry : orderLinekeyMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				Element rootEle = inXML.getDocumentElement();
				String paymentRuleID = rootEle.getAttribute("PaymentRuleId");
				rootEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, key);
				if(log.isDebugEnabled()){
					log.debug("key"+key);
					log.debug("value"+value);
				}
				 // un Commented for 1B: BOSTS lines should NOT be sent to JDA
				  if(value.equalsIgnoreCase("TO")){
				  
				  api = YIFClientFactory.getInstance().getApi();
				  api.executeFlow(env, "VSISendBOSTSToJDA", inXML);
				  
				  }else
				 
				if (value.equalsIgnoreCase("SCHED")) {

					if (log.isVerboseEnabled()) {
						log.verbose("VSISendBOPUSToJDA XML is : \n"
								+ XMLUtil.getXMLString(inXML));
					}
				api = YIFClientFactory.getInstance().getApi();
					api.executeFlow(env, "VSISendBOPUSToJDA", inXML);
				}

			}

			// Changes for OMS-808
			// Execute changeOrder API to stamp ExtnPublish Flag to 'Y'
			NodeList changeOrderLineList = orderLinesElement
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

			if (changeOrderLineList.getLength() > 0) {
				if(!bSTHLinesPresent){
					sOrderElement.setAttribute("ModificationReasonCode",
							"PUBLISHFLAG_Y");
					sOrderElement.setAttribute("ModificationReasonText",
							"Setting Published Flag to Y");
				}
				
				if (log.isVerboseEnabled()) {
					log.verbose("changeOrderInput XML is : \n"
							+ XMLUtil.getXMLString(changeOrderInput));
					log.debug("changeOrderInput XML is : \n"
							+ SCXmlUtil.getString(changeOrderInput));
				}
				
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER, changeOrderInput);
				//OMS-2015- Start
				// Added logic to send out BOPUS to BOSTS conversation email
			if(bPISToSTSConvertion) {
				    if(!YFCObject.isVoid(custPoNo)){
						Document emailDoc = VSISendRdyForPckupAndReminderEmails.getEmailContent(env,VSIConstants.ATTR_CUST_PO_NO, custPoNo); 
						VSIUtils.invokeService(env,"VSISendBOPUSToBOSTSConversionEmail", emailDoc);
				    } 
			}//OMS-2015- End
			}

		} catch (YFSException e) {
			e.printStackTrace();
			// YFSException e1 = new YFSException();
			String error = e.getMessage();
			String errorCode = e.getErrorCode();
			String errorDesc = e.getErrorDescription();
			throw new YFSException(errorCode, errorDesc, error);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR", "Exception");

		}
	}
	
	private boolean dateGreaterThanCurrentDate(String strDate) throws ParseException{
		boolean bResult = false;

		strDate = strDate.substring(0, 10);
		// Input date will be passed to sterling in below format
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
		
		Date date = new Date();
		String strCurrentDate = sdf.format(date);
		
		
		Date dPassedDate = sdf.parse(strDate);
		Date dCurrentDate = sdf.parse(strCurrentDate);
		
		if(dPassedDate.after(dCurrentDate)){
			bResult = true; 
		}
		
		return bResult;
	}
	private ArrayList<String> fetchCounterName(YFSEnvironment env)  throws ParserConfigurationException, 
	YFSException, RemoteException, YIFClientCreationException {
		ArrayList<String> arrCounterName=new ArrayList<String>();
		Document docInput=XMLUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_COUNTER_NAME");
		Element eleOrderBy=SCXmlUtil.createChild(eleCommonCode, "OrderBy");
		Element eleAttribute=SCXmlUtil.createChild(eleOrderBy, "Attribute");
		eleAttribute.setAttribute("Desc", "N");
		eleAttribute.setAttribute("Name", "CommonCodeKey");
		if(log.isDebugEnabled()){
			log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
		}
		Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInput);
		if(log.isDebugEnabled()){
			log.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
		}
		NodeList nlCommonCode = docCommonCodeListOP
				.getElementsByTagName(ELE_COMMON_CODE);
		int iCount=nlCommonCode.getLength();
		for (int i = 0; i < iCount; i++) {
			Element eleCommonCodeOutput=((Element) nlCommonCode.item(i));
			String strShortDesc=eleCommonCodeOutput.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
			arrCounterName.add(strShortDesc);
		}
		return arrCounterName;
	}
}
