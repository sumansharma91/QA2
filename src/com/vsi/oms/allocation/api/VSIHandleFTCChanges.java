package com.vsi.oms.allocation.api;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * If a line is in backordered status for a configurable number of days, line should be canceled and a notification should be
 * sent. Cancelation windows are different for different enterprises. Below windows are to be used-
 *		NutritionDepot-25
 *		EBay-30
 *		Amazon-25
 *		Jet-8
 * 
 * @author IBM
 *
 */
public class VSIHandleFTCChanges implements VSIConstants {
	
	public static final String ELE_ORDER_STATUSES = "OrderStatuses";
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIHandleFTCChanges.class);

	public Document handleFTCCancel (YFSEnvironment env, Document docInput) throws Exception {
		log.beginTimer("VSIHandleFTCChanges.handleFTCCancel : START");
		Element eleMonitorRule = docInput.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleMonitorRule, ELE_ORDER);
		Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_STATUSES);
		ArrayList<Element> arrOrderStatus = SCXmlUtil.getChildren(eleOrderStatuses, ELE_ORDER_STATUS);
		
		String strCancelReason= null;
		
		//Fetch Cancel Reason from common code
		ArrayList<Element> arrFTCRulesForCancelWindow = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "CancelReason", ATTR_DEFAULT);
		if(arrFTCRulesForCancelWindow.size()>0){
			Element eleFTCRule = arrFTCRulesForCancelWindow.get(0);
			strCancelReason = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
		}
		//start forming changeOrder document to cancel the lines.
		Document docChangeOrder = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY));
		Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, ELE_ORDER_LINES);
		String strOrderLineXPath = "/Order/OrderLines/OrderLine[@OrderLineKey='";
		
		//loop through all the statuses to find the quantity which is not shipped or canceled.
		
		for(Element eleOrderStatus : arrOrderStatus){
			String strOrderLineKey= eleOrderStatus.getAttribute(ATTR_ORDER_LINE_KEY);
			String strCompleteOrderLineXPath = strOrderLineXPath+strOrderLineKey+"']";
			String strStatusQty = eleOrderStatus.getAttribute(ATTR_STATUS_QUANTITY);
			String strStatus = eleOrderStatus.getAttribute(ATTR_STATUS);
			Element eleChangeOrderLine = null;
			//OMS-967 - number format exception
			if(Double.parseDouble(strStatus)<3700 && !(strStatus.equalsIgnoreCase(STATUS_BACKORDERED_FROM_NODE))){
				eleChangeOrderLine = SCXmlUtil.getXpathElement(eleChangeOrder,strCompleteOrderLineXPath);
				if(!YFCCommon.isVoid(eleChangeOrderLine)){
				String strExistingQuantity = eleChangeOrderLine.getAttribute(ATTR_QUANTITY_TO_CANCEL);
				int iUpdatedQty = Integer.parseInt(strExistingQuantity)+Integer.parseInt(strStatusQty);
				eleChangeOrderLine.setAttribute(ATTR_QUANTITY_TO_CANCEL, Integer.toString(iUpdatedQty));
				//OMS-1204 :Start
				//Adding Action="Cancel" , this attribute will be used in beforeChangeOrderUE to create the XML for 
				//Order cancel email. Action="CANCEL" will cancel entire line we are not using it here
				eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
				//OMS-1204:End
				if(!YFCCommon.isVoid(strCancelReason)){
					eleChangeOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, strCancelReason);
				}
				}else if(YFCCommon.isVoid(eleChangeOrderLine)){
					eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, ELE_ORDER_LINE);
					eleChangeOrderLine.setAttribute(ATTR_QUANTITY_TO_CANCEL, strStatusQty);
					eleChangeOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
					//OMS-1204 :Start
					//Adding Action="Cancel" , this attribute will be used in beforeChangeOrderUE to create the XML for 
					//Order cancel email. Action="CANCEL" will cancel entire line we are not using it here
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CANCEL);
					//OMS-1204:End
					if(!YFCCommon.isVoid(strCancelReason)){
						eleChangeOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, strCancelReason);
					}
				}
			}	
		}
		
		//Invoke cancel order API to cancel any un-shipped or canceled quantity.
		docInput = VSIUtils.invokeAPI(env, API_CANCEL_ORDER, docChangeOrder);
		
		log.endTimer("VSIHandleFTCChanges.handleFTCCancel : END");
		return docInput;
	}
	
	public Document handleFTCNotification (YFSEnvironment env, Document docInput) throws Exception {
		if(log.isDebugEnabled()){
			log.beginTimer("VSIHandleFTCChanges.handleFTCNotification : START");
		}
		Element eleMonitorRule = docInput.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(eleMonitorRule, ELE_ORDER);
		Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_STATUSES);
		ArrayList<Element> arrOrderStatus = SCXmlUtil.getChildren(eleOrderStatuses, ELE_ORDER_STATUS);
		
		//invoke getOrderDetails to get the order details
		Document docGetOrderDetails = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleGetOrderDetails = docGetOrderDetails.getDocumentElement();
		eleGetOrderDetails.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY));
		
		//Modify this template as per FTC Email requirements.
		docGetOrderDetails = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORDER_LIST_VSI_FTC_EMAIL, API_GET_ORDER_LIST, docGetOrderDetails);
		eleGetOrderDetails = docGetOrderDetails.getDocumentElement();
		
		String strOrderLineXPath = "OrderList/Order/OrderLines/OrderLine[@OrderLineKey='";
		
		for(Element eleOrderStatus : arrOrderStatus){
			String strOrderLineKey= eleOrderStatus.getAttribute(ATTR_ORDER_LINE_KEY);
			String strCompleteOrderLineXPath = strOrderLineXPath+strOrderLineKey+"']";
			String strStatusQty = eleOrderStatus.getAttribute(ATTR_STATUS_QUANTITY);
			String strStatus = eleOrderStatus.getAttribute(ATTR_STATUS);
			Element eleOrderLine = null;
			if(Double.parseDouble(strStatus)<3700 && !(strStatus.equalsIgnoreCase(STATUS_BACKORDERED_FROM_NODE))){
				
				//Check if YCD_FTC_PROMISE_DATE exists, then only send the message
				eleOrderLine = XMLUtil.getElementByXPath(docGetOrderDetails, strCompleteOrderLineXPath);
				Element eleFirstDelayDate = XMLUtil.getElementByXPath(docGetOrderDetails,
						"OrderList/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_PROMISE_DATE']");
				
				//Stamp QuantityNotShipped Attribute on the lines. This will be later used by FTC Email
				if(!YFCCommon.isVoid(eleFirstDelayDate)){
					if(!YFCCommon.isVoid(eleGetOrderDetails)){
						String strQuantityNotShipped = eleOrderLine.getAttribute(ATTR_QUANTITY_NOT_SHIPPED);
						if(YFCCommon.isVoid(strQuantityNotShipped)){
							eleOrderLine.setAttribute(ATTR_QUANTITY_NOT_SHIPPED, strStatusQty);
						}else{
							String strExistingQuantity = eleOrderLine.getAttribute(ATTR_QUANTITY_NOT_SHIPPED);
							int iUpdatedQty = Integer.parseInt(strExistingQuantity)+Integer.parseInt(strStatusQty);
							eleOrderLine.setAttribute(ATTR_QUANTITY_NOT_SHIPPED, Integer.toString(iUpdatedQty));
						}
					}
				}
				
				stampCancelDate(docGetOrderDetails, eleOrderLine, strOrderLineKey, eleOrderStatus);
			}
		}
		
		//remove any line which is fully shipped or canceled
		Element eleOrderOut = SCXmlUtil.getChildElement(eleGetOrderDetails, ELE_ORDER);
		Element eleOrderLinesGetOrderDetails = SCXmlUtil.getChildElement(eleOrderOut, ELE_ORDER_LINES);
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLinesGetOrderDetails, ELE_ORDER_LINE);
		for(Element eleOrderLine : arrOrderLines){
			if(!(eleOrderLine.hasAttribute(ATTR_QUANTITY_NOT_SHIPPED))){
				SCXmlUtil.removeNode(eleOrderLine);
			}
		}
		
		// Get and add calendar info for the ship node
		String strShipNode = arrOrderLines.get(0).getAttribute(ATTR_SHIP_NODE);
		if (!YFCObject.isVoid(strShipNode)) {
			Document docGetCalendar = SCXmlUtil.createDocument(ELE_CALENDAR);
			Element eleGetCalendar = docGetCalendar.getDocumentElement();
			eleGetCalendar.setAttribute(ATTR_ORGANIZATION_CODE, strShipNode);
			Document docCalendarList = VSIUtils.invokeAPI(env, TEMPLATE_GET_CALENDAR_LIST_VSI_TRIM_DTC_CFM_MSG, API_GET_CALENDAR_LIST, docGetCalendar);
			Element eleCalendarList = docCalendarList.getDocumentElement();
			if (eleCalendarList.hasChildNodes()) {
				Element eleCalendar = SCXmlUtil.getChildElement(eleCalendarList, ELE_CALENDAR);
				SCXmlUtil.importElement(eleOrderOut, eleCalendar);
			}
		}
		
		//Invoke Service for FTC Notification below
		//OMS-1690 : Start
		//VSIUtils.invokeService(env, SERVICE_VSI_SEND_FTC_DELAY_NOTIFICATION, docGetOrderDetails);
		//OMS-1690 : End
		if(log.isDebugEnabled()){
			log.endTimer("VSIHandleFTCChanges.handleFTCNotification : END");
		}
		return docGetOrderDetails;
	}
	
	/*This function updates YCD_FTC_FIRST_DATE on call center orders
	 * where estimated ship date is in future
	 */
	public Document updateFTCDatesOnConfirmDraftOrder (YFSEnvironment env, Document docInput) throws YFSException, RemoteException, 
	YIFClientCreationException, TransformerException, ParseException {
		log.beginTimer("VSIHandleFTCChanges.handleFTCNotification : START");
		
		Element eleOrder = docInput.getDocumentElement();
		String shipToKey = eleOrder.getAttribute("ShipToKey");
		String orderHeaderKey = eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY);
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES);
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);
		String strOrderDate = eleOrder.getAttribute(ATTR_ORDER_DATE);
		String inputSourcingClassification = eleOrder.getAttribute(ATTR_SOURCING_CLASSIFICATION);
		String finalSourcingClassification = "";
		Element eleOrderPersonInfoShipTo = SCXmlUtil.getChildElement(eleOrder, ELE_PERSON_INFO_SHIP_TO);
		Boolean updateSourcingClassification = false;
		String deliveryMethod = "";
		Element eleLinePersonInfoShipTo = null;
		
		//prepare change order document
		Document docChangeOrder = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, ELE_ORDER_LINES);
		Boolean updateTaxableFlag = false;
		Boolean stampExtnPoints = false;
		Element eleChangeOrderExtn = null; 
		//loop through all the lines in input
		for(Element eleOrderLine : arrOrderLines){
			Element eleChangeOrderLine = null;
			
			String strOrderLineKey = eleOrderLine.getAttribute(ATTR_ORDER_LINE_KEY);
			deliveryMethod = eleOrderLine.getAttribute(ATTR_DELIVERY_METHOD);
			String lineType = eleOrderLine.getAttribute(ATTR_LINE_TYPE);
			String shipNode = eleOrderLine.getAttribute("ShipNode");
			Element eleFirstFTCPromiseDate = XMLUtil.getElementByXPath(docInput,
					"Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_FIRST_PROMISE_DATE']"); 
			
			if(!YFCCommon.isVoid(eleFirstFTCPromiseDate)){
				String strActualDate = SCXmlUtil.getAttribute(eleFirstFTCPromiseDate, ATTR_ACTUAL_DATE);
				
				if(VSIUtils.differenceBetweenDates(strActualDate,strOrderDate)>0){
					//if orderline is having future date, set FTC_FIRST_PROMISE_DATE
					eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, ELE_ORDER_LINE);
					eleChangeOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
					Element eleChangeOrderLinesDates = SCXmlUtil.createChild(eleOrderLine, ELE_ORDER_DATES);
					Element eleChangeOrderLineFTCPromiseDate = SCXmlUtil.createChild(eleChangeOrderLinesDates, ELE_ORDER_DATE);
					eleChangeOrderLineFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_PROMISE_DATE");
					eleChangeOrderLineFTCPromiseDate.setAttribute(ATTR_ACTUAL_DATE, strActualDate);
				}
			}
			
			
			
			if(YFCObject.isVoid(eleChangeOrderLine)){
				eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, ELE_ORDER_LINE);
				eleChangeOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
			}
			
			if(deliveryMethod.equals(ATTR_DEL_METHOD_SHP)){
				
				eleLinePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, ELE_PERSON_INFO_SHIP_TO);
			}
			else
			{
				eleLinePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, "PersonInfoShipTo");
		        if (!YFCCommon.isVoid(eleLinePersonInfoShipTo))
		        {
		          eleLinePersonInfoShipTo.setAttribute("FirstName", "Store");
		          eleLinePersonInfoShipTo.setAttribute("LastName", shipNode);
		          SCXmlUtil.importElement(eleChangeOrderLine, eleLinePersonInfoShipTo);
		          SCXmlUtil.importElement(eleChangeOrder, eleLinePersonInfoShipTo);
		        }
			}
			
			if("SHIP_TO_HOME".equals(lineType))
			{
				//eleChangeOrderLine.setAttribute("ShipToKey", shipToKey);
				if(YFCObject.isVoid(eleChangeOrderExtn)){
					eleChangeOrderExtn = SCXmlUtil.createChild(eleChangeOrder, ELE_EXTN);
					eleChangeOrderExtn.setAttribute("ExtnDTCOrder", "Y");
				}
				stampExtnPoints = true;
			}
			
			if("SHIP_TO_STORE".equals(lineType))
			{
				Element eleOrderLineSourcingControls = SCXmlUtil.createChild(eleChangeOrderLine, "OrderLineSourcingControls");
				Element eleOrderLineSourcingCntrl =  SCXmlUtil.createChild(eleOrderLineSourcingControls, "OrderLineSourcingCntrl");
				eleOrderLineSourcingCntrl.setAttribute("Node", eleOrderLine.getAttribute("ShipNode"));
				eleOrderLineSourcingCntrl.setAttribute("InventoryCheckCode", "NOINV");
				eleOrderLineSourcingCntrl.setAttribute("ReasonText", "SHIP_TO_STORE");
			}
			
			// Add logic to set TaxableFlag at LinePriceInfo level
			updateTaxableFlag = true;
			Element eleLinePriceInfo = SCXmlUtil.createChild(eleChangeOrderLine, ELE_LINE_PRICE_INFO);
			if(!YFCObject.isVoid(eleLinePriceInfo)){
				// If TaxableFlag is not already set at LinePriceInfo
				Element eleLineTaxes = SCXmlUtil.getChildElement(eleOrderLine, "LineTaxes");
				if(!YFCCommon.isVoid(eleLineTaxes)){
					if(eleLineTaxes.hasChildNodes()){

						Element eleLineTax = SCXmlUtil.getChildElement(eleLineTaxes, "LineTax");
						String taxableFlag  = eleLineTax.getAttribute("TaxableFlag");
						if(!YFCObject.isVoid(taxableFlag)){
							eleLinePriceInfo.setAttribute("TaxableFlag", taxableFlag);
						}else{
							String tax = eleLineTax.getAttribute("Tax");
							if(!YFCObject.isVoid(tax)){
								if(Double.parseDouble(tax) > 0){
									eleLinePriceInfo.setAttribute("TaxableFlag", "Y");
								}else{
									eleLinePriceInfo.setAttribute("TaxableFlag", "N");
								}
							}	
						}
					}else{
						eleLinePriceInfo.setAttribute("TaxableFlag", "N");
					}
				}else{
					eleLinePriceInfo.setAttribute("TaxableFlag", "N");
				}
			}
		}
		
		if(deliveryMethod.equals(ATTR_DEL_METHOD_SHP)){

			if(YFCObject.isVoid(eleLinePersonInfoShipTo)){

				eleLinePersonInfoShipTo = eleOrderPersonInfoShipTo;
			}

			if(!YFCObject.isVoid(eleLinePersonInfoShipTo)){

				String strCountry = eleLinePersonInfoShipTo.getAttribute(ATTR_COUNTRY);
				if(!strCountry.equals(US)){

					finalSourcingClassification = "INTERNATIONAL";
				}else{

					Document getCommonCodeListOutXML = null;
					Document getCommonCodeListInXML = SCXmlUtil.createDocument("CommonCode");
					Element eleCommonCodeElement = getCommonCodeListInXML.getDocumentElement();
					String strZipCode = SCXmlUtil.getAttribute(eleLinePersonInfoShipTo, ATTR_ZIPCODE);
					eleCommonCodeElement.setAttribute(ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
					eleCommonCodeElement.setAttribute(ATTR_CODE_VALUE, strZipCode);
					getCommonCodeListOutXML = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, getCommonCodeListInXML);

					Element commonCodeListElement = getCommonCodeListOutXML.getDocumentElement();
					// Setting SourcingClassification=APO_FPO if
					// Zip Code is APO FPO DOP zip code
					// AddressLine1 or AddressLine2 have "PO Box" in them
					if(commonCodeListElement.hasChildNodes()){
						finalSourcingClassification = "APO_FPO";
					}
				}
			}
			
			if(!YFCObject.isVoid(finalSourcingClassification) && !finalSourcingClassification.equals(inputSourcingClassification)){
				
				updateSourcingClassification = true;
				eleChangeOrder.setAttribute(ATTR_SOURCING_CLASSIFICATION, finalSourcingClassification);
			}
		}
		
		if(stampExtnPoints)
		{
			String billToID = docInput.getDocumentElement().getAttribute("BillToID");
			if(!YFCCommon.isVoid(billToID))
			{
				stampExtnPoints(env,docInput,eleChangeOrder,billToID);
			}
		}

			ArrayList<Element> arrChangeOrderLines = SCXmlUtil.getChildren(eleChangeOrderLines, ELE_ORDER_LINE);
			if((!YFCCommon.isVoid(arrChangeOrderLines) 
					&& arrChangeOrderLines.size()>0) || updateSourcingClassification || updateTaxableFlag){
				//invoke change order
				eleChangeOrder.setAttribute(ATTR_OVERRIDE, FLAG_Y);
				VSIUtils.invokeAPI(env, API_CHANGE_ORDER, docChangeOrder);
			}


			return docInput;
		}
	
	public void stampExtnPoints(YFSEnvironment env, Document docInput, Element eleChangeOrder,String billToID){
		
		String extnTotPtsErnd ="";
		Element eleChangeOrderExtn = SCXmlUtil.getChildElement(eleChangeOrder, ELE_EXTN);
		if(billToID != null && !billToID.trim().equalsIgnoreCase("")){
			try {
				  Document healthyRewardsIPXML = XMLUtil.createDocument("Customer");
				  healthyRewardsIPXML.getDocumentElement().setAttribute("HealthyAwardsNo", billToID);
				  if(log.isDebugEnabled()){
					  log.debug(" healthyRewardsIPXML : "+XMLUtil.getXMLString(healthyRewardsIPXML));
				  }
					Document healthyRewardsOPXML = VSIUtils.invokeService(env, "VSIGetCustRewardPoints", healthyRewardsIPXML);
					if(healthyRewardsOPXML != null ){
						extnTotPtsErnd = YFCDocument.getDocumentFor(healthyRewardsOPXML).getDocumentElement()
				 				.getChildElement("s:Body")
				 				.getChildElement("GetCustomerLoyaltiesResponse")
				 				.getChildElement("GetCustomerLoyaltiesResult")
				 				.getChildElement("b:Items")
				 				.getChildElement("b:CustomerLoyaltyWCF")
				 				.getChildElement("b:PostedPoints")
				 				.getNodeValue();
						
						
						
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(!YFCCommon.isVoid(extnTotPtsErnd))
		{
			eleChangeOrderExtn.setAttribute("ExtnTotalPointsEarned", extnTotPtsErnd);
			
		}
		//eleChangeOrderExtn.setAttribute("ExtnTotalPointsEarned", extnTotPtsErnd);
		
		//TBD ----   plugin logic to get the ExtnPointsEarned -- Neha service
		try {
			if(log.isDebugEnabled()){
				log.info(XMLUtil.getXMLString(docInput));
			}
			Document getCustomerRewardPointsDoc = VSIUtils.invokeService(env, "VSICustomerRewardPoint", docInput);
			if(log.isDebugEnabled()){
				log.info(XMLUtil.getXMLString(getCustomerRewardPointsDoc));
			}
			String extnPointsEarned = "";
			if(getCustomerRewardPointsDoc != null){
				Element rootEle = getCustomerRewardPointsDoc.getDocumentElement();
				if(rootEle.hasAttribute("TotalRewardsPoint")){
					extnPointsEarned = rootEle.getAttribute("TotalRewardsPoint");
				}
			}
			if(!YFCCommon.isVoid(extnPointsEarned))
			{
				eleChangeOrderExtn.setAttribute("ExtnPointsEarned", extnPointsEarned);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//return eleChangeOrder;
		
	}
	
	/**
	 * Sets the YCD_FTC_CANCEL_DATE the same as the ExpectedShipmentDate
	 * 
	 * @param docGetOrderDetails
	 * @param eleOrderLine 
	 * @param strOrderLineKey 
	 * @param eleOrderStatus 
	 * @throws TransformerException 
	 */
	private void stampCancelDate(Document docGetOrderDetails, Element eleOrderLine, String strOrderLineKey, Element eleOrderStatus) throws TransformerException {
		Element eleCancelDate = XMLUtil.getElementByXPath(docGetOrderDetails,
				"OrderList/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_CANCEL_DATE']");
		
		//Stamp QuantityNotShipped Attribute on the lines. This will be later used by FTC Email
		if(!YFCCommon.isVoid(eleCancelDate)){
			Element eleDetails = SCXmlUtil.getChildElement(eleOrderStatus, ELE_DETAILS);
			eleCancelDate.setAttribute(ATTR_ACTUAL_DATE, eleDetails.getAttribute(ATTR_EXPECTED_SHIPMENT_DATE));
		}
	}
	
}
