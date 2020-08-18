package com.vsi.oms.allocation.userexit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSConfirmAssignmentsUE;

/**
 * This User Exit handles the logic 
 * 1. To allow the scheduling of free items
 * 2. To evaluate Ship to Store orders for shipping restrictions
 * 
 * @author IBM
 *
 */
public class VSIConfirmAssignmentsUE implements YFSConfirmAssignmentsUE {
	private YFCLogCategory log = YFCLogCategory.instance(VSIConfirmAssignmentsUE.class);

	/**
	 * 
	 * 
	 */
	@Override
	public Document confirmAssignments(YFSEnvironment env, Document docInput) throws YFSUserExitException {
		
		log.beginTimer("VSIConfirmAssignmentsUE.confirmAssignments : START");
		if(log.isDebugEnabled()){
			log.debug("VSIConfirmAssignmentsUE.confirmAssignments : input: " + SCXmlUtil.getString(docInput));
		}
		try {
			boolean isScheduleAllowed = false;
			ArrayList<Element> alFreeLinesList = new ArrayList<>();
			
			Element eleOrder = docInput.getDocumentElement();
			String strTransactionID = eleOrder.getAttribute(VSIConstants.ATTR_TRANSACTION_ID);
			
			// If TransactionId = SCHEDULE.0001 evaluate order for Ship To Store shipping restrictions
			if(strTransactionID.equals(VSIConstants.TXN_SCHEDULE)){
				
				evaluateShipToStoreOrder(env, docInput);
			}
			
			
			// The below logic is for free line scheduling
			
			// Populate the alShipNodeList and alFreeLinesList array lists
			sortLines(docInput, alFreeLinesList);
			if (alFreeLinesList.size() > 0) {
				isScheduleAllowed = isScheduleAllowed(env, docInput);
			}
			//OMS-1243 : Start
			 boolean isRejectAssignment=false;
			//OMS-1243 : End
			for (Element eleOrderLine : alFreeLinesList) {
				// If schedule allowed, then take no action.
				// If not, then reject the free line also. Next schedule Date is stamped as current date + 5 hours.
				// If free line is rejected, then free line remains in created status and is scheduled again after 5 hours.
				// ARE-204 : Modified condition to always allow scheduling for reship lines : BEGIN
				if (!isScheduleAllowed
						&& YFCObject.isVoid(eleOrderLine.getAttribute(VSIConstants.A_RESHIP_PARENT_LINE_KEY))) {
					//OMS-1243 :Start
					isRejectAssignment=true;
					//OMS-1243: End
					Element eleOrderLineSchedules = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_SCHEDULES);
					ArrayList<Element> alScheduleList = SCXmlUtil.getChildren(eleOrderLineSchedules, VSIConstants.ELE_SCHEDULE);
					for (Element eleOrderLineSchedule : alScheduleList) {
						eleOrderLineSchedule.setAttribute(VSIConstants.ATTR_REJECT_ASSIGNMENT, VSIConstants.TRUE);
					}
				}
				// ARE-204 : Modified condition to always allow scheduling for reship lines : END
			}
			//OMS-1243 : Start
			if(isRejectAssignment){
				env.setTxnObject("RejectAssignment",alFreeLinesList );
			}
			//OMS-1243 : End
		} catch (Exception e) {
			log.error("RemoteException in VSIConfirmAssignmentUE.confirmAssignments() : " , e);
			/*throw VSIUtils.getYFSException(e, "Exception occurred",
					"RemoteException in VSIConfirmAssignmentUE.confirmAssignments() : ");*/
			throw new YFSUserExitException(e.getMessage());
		}
		if(log.isDebugEnabled()){
			log.debug("VSIConfirmAssignmentsUE.confirmAssignments : output: " + SCXmlUtil.getString(docInput));
		}
		log.endTimer("VSIConfirmAssignmentsUE.confirmAssignments : END");
		return docInput;
	}

	/**
	 * Sort the lines into free lines in Created status and non-free lines in scheduled status
	 * 
	 * @param docInput
	 * @param alFreeLinesList
	 */
	private void sortLines(Document docInput, ArrayList<Element> alFreeLinesList) {
		
		log.beginTimer("VSIConfirmAssignmentsUE.sortLines : START");
		Element eleOrder = docInput.getDocumentElement();
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
		ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		// Loop through order lines to check if there is a free item
		for (Element eleOrderLine : alOrderLineList) {
			Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE_INFO, true);
			Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine,"Extn", true);
			String isGWP = eleExtn.getAttribute("ExtnIsGWP");
			// If free item is scheduled against a ship node
			if ((VSIConstants.STATUS_CREATE.equals(eleOrderLine.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS))
					|| VSIConstants.STATUS_CREATE.equals(eleOrderLine.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS)))
					&& SCXmlUtil.getDoubleAttribute(eleLinePriceInfo, VSIConstants.ATTR_LINE_TOTAL, 0) <= 0 && "Y".equals(isGWP)) {
				alFreeLinesList.add(eleOrderLine);
			}
		}

		log.endTimer("VSIConfirmAssignmentsUE.sortLines : END");

	}
	/**
	 * Call getOrderList to get all the lines in this order and check whether any are in No Action status
	 * @param env 
	 * 
	 * @param docInput
	 */
	private boolean isScheduleAllowed(YFSEnvironment env, Document docInput) {
		
		log.beginTimer("VSIConfirmAssignmentsUE.getShipNodes : START");
		
		boolean isScheduleAllowed = false;
		try {
			Element eleInputOrder = docInput.getDocumentElement();
			Element eleInputOrderLines = SCXmlUtil.getChildElement(eleInputOrder, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alInputOrderLineList = SCXmlUtil.getChildren(eleInputOrderLines, VSIConstants.ELE_ORDER_LINE);
			for (Element eleOrderLine : alInputOrderLineList) {
				Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE_INFO, true);
				Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine,"Extn", true);
				String isGWP = eleExtn.getAttribute("ExtnIsGWP");
				// Checking whether there is a non-free line with status pass No Action (1500)
				if ((SCXmlUtil.getDoubleAttribute(eleLinePriceInfo, VSIConstants.ATTR_LINE_TOTAL, 0) > 0) && !"Y".equals(isGWP)) {
					Element eleSchedules = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_SCHEDULES);
					ArrayList<Element> arrSchedules = SCXmlUtil.getChildren(eleSchedules, VSIConstants.ELE_SCHEDULE);
					for(Element eleSchedule:arrSchedules){
						double dTotalAssignedQty = SCXmlUtil.getDoubleAttribute(eleSchedule, VSIConstants.ATTR_TOTAL_ASSIGNED_QTY);
						if(dTotalAssignedQty>0) {
							isScheduleAllowed = true;
						}
					}
					break;
				}
			}
			// ARE-204 : Added to check the status of the lines not received in the input, only if schedule is not already rejected : BEGIN
			if (!isScheduleAllowed) {
				Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleGetOrderList = docGetOrderList.getDocumentElement();
				eleGetOrderList.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleInputOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				Document docOrderList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_CONF_ASSIGNMENT_UE,
						VSIConstants.API_GET_ORDER_LIST, docGetOrderList);
				Element eleOrder = SCXmlUtil.getChildElement(docOrderList.getDocumentElement(), VSIConstants.ELE_ORDER);
				if(log.isDebugEnabled()){
					log.debug("VSIConfirmAssignmentsUE.getShipNodes : eleOrder : " + SCXmlUtil.getString(eleOrder));
				}
				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
				ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
				for (Element eleOrderLine : alOrderLineList) {
					Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE_INFO, true);
					// Checking whether there is a non-free line with status pass No Action (1500)
					Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine,"Extn", true);
					String isGWP = eleExtn.getAttribute("ExtnIsGWP");
					double orderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine,"OrderedQty");
					if (orderedQty > 0 && SCXmlUtil.getDoubleAttribute(eleLinePriceInfo, VSIConstants.ATTR_LINE_TOTAL, 0) >= 0 && !"Y".equals(isGWP) 
						&& ((SCXmlUtil.getIntAttribute(eleOrderLine, VSIConstants.ATTR_MAX_LINE_STATUS, 0) >= VSIConstants.STATUS_INT_NO_ACTION)
							|| (SCXmlUtil.getIntAttribute(eleOrderLine, VSIConstants.ATTR_MIN_LINE_STATUS, 0) >= VSIConstants.STATUS_INT_NO_ACTION))) {
						isScheduleAllowed = true;
						break;
					}
				}
			}
			// ARE-204 : Added to check the status of the lines not received in the input, only if schedule is not already rejected : END
		} catch (YFSException yfse) {
			log.error("YFSException in VSIConfirmAssignmentsUE.getShipNodes() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIConfirmAssignmentsUE.getShipNodes() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIConfirmAssignmentUE.getShipNodes() : ");
		}
		if(log.isDebugEnabled()){
			log.debug("VSIConfirmAssignmentsUE.getShipNodes : isScheduleAllowed : " + isScheduleAllowed);
		}
		log.endTimer("VSIConfirmAssignmentsUE.getShipNodes : END");
		return isScheduleAllowed;
	}
	
	private void evaluateShipToStoreOrder(YFSEnvironment env, Document docInput) throws Exception{
		
		NodeList nlOrderLine = XMLUtil.getNodeListByXpath(docInput, VSIConstants.XPATH_ORDER_LINE);
		HashMap<String, String> hmRestrictedLineItems = null;
		if(nlOrderLine.getLength() > 0){

			Element eleOrderLine = null;
			String deliveryMethod = "";
			for(int i = 0; i < nlOrderLine.getLength(); i++){

				eleOrderLine = (Element) nlOrderLine.item(i);
				deliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
				// If DeliveryMethod = PICK
				if(deliveryMethod.equals(VSIConstants.ATTR_DEL_METHOD_PICK)){
					
					Element eleSchedules = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_SCHEDULES);
					if(eleSchedules.hasChildNodes()){
						
						NodeList nlSchedule = eleSchedules.getChildNodes();
						Element eleSchedule = null;
						for(int j = 0; j < nlSchedule.getLength(); j++){
							
							eleSchedule = (Element) nlSchedule.item(j);
							if(eleSchedule.hasChildNodes()){
								
								Element eleShipNodes = SCXmlUtil.getChildElement(eleSchedule, VSIConstants.ATTR_SHIPNODES);
								if(eleShipNodes.hasChildNodes()){

									NodeList nlShipNode = eleShipNodes.getChildNodes();
									for(int k = 0; k < nlShipNode.getLength(); k++){
										Element eleShipNode = (Element) nlShipNode.item(k);
										if(eleShipNode.hasAttribute(VSIConstants.ATTR_SHIP_DATE)){

											String strShipDate = eleShipNode.getAttribute(VSIConstants.ATTR_SHIP_DATE);
											if(!YFCObject.isVoid(strShipDate)){

												/* For Pick in store lines, ShipDate is current date. 
												 * For Ship to Store lines, ShipDate is in the future 
												 */
												if(isGreaterThanCurrentDate(strShipDate)){

													Element eleItem = SCXmlUtil.getChildElement(
															eleOrderLine, VSIConstants.ELE_ITEM);
													String itemID = eleItem.getAttribute(
															VSIConstants.ATTR_ITEM_ID);
													String shipNode = eleShipNode.getAttribute(
															VSIConstants.ATTR_SHIP_NODE);
													if(!YFCObject.isVoid(shipNode)){

														if(isItemRestricted(env, itemID, shipNode)){

															eleSchedule.setAttribute(
																	VSIConstants.ATTR_REJECT_ASSIGNMENT, VSIConstants.TRUE);
															/*
															 *  Add restricted line details to STS restrictions hashmap. 
															 */
															
															String strOrderLineKey = eleOrderLine.getAttribute(
																	VSIConstants.ATTR_ORDER_LINE_KEY);
															String strAssignedQty = eleShipNode.getAttribute(
																	VSIConstants.ATTR_ASSIGNED_QTY);
															Double dblAssignedQty = 0.0;
															if(!YFCObject.isVoid(strAssignedQty)){
																dblAssignedQty = Double.parseDouble(strAssignedQty);
															}
															if(YFCObject.isVoid(hmRestrictedLineItems)){

																hmRestrictedLineItems = new HashMap<String, String>();
															}

															if(hmRestrictedLineItems.containsKey(strOrderLineKey)){
																
																dblAssignedQty = dblAssignedQty + Double.parseDouble(
																		hmRestrictedLineItems.get(strOrderLineKey));
																hmRestrictedLineItems.put(strOrderLineKey, 
																		String.valueOf(dblAssignedQty));
															}else{
																
																hmRestrictedLineItems.put(
																		strOrderLineKey, strAssignedQty);
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		/*
		 * If item restriction map has entries, set it in the transaction object
		 * Will be used to cancel Ship to Store lines
		 */
		 
		if(!YFCObject.isVoid(hmRestrictedLineItems)){
			
			if(hmRestrictedLineItems.size() > 0){
				
				env.setTxnObject(VSIConstants.TXN_OBJ_STS_ITEM_RESTRICTIONS, hmRestrictedLineItems);
			}
		}
	}
	
	private boolean isItemRestricted(YFSEnvironment env, String itemID, String shipNode) throws Exception{

		Document itemDetails = getItemRestrictionDetails(env, itemID);
		/*
		 <ItemList>
    		<Item ItemID="1655208" ItemKey="2014032417465415969062">
	        	<Extn>
	            	<VSIShipRestrictedItemList>
	                	<VSIShipRestrictedItem Country="US" ItemID="1655208" ItemKey="2014032417465415969062" State="IL" ZipCode="60409"/>
	                	<VSIShipRestrictedItem Country="US" ItemID="1655208" ItemKey="2014032417465415969062" State="TX" ZipCode="75093"/>
	            	</VSIShipRestrictedItemList>
	        	</Extn>
    		</Item>
		</ItemList>
		 */
		Element eleItemList = itemDetails.getDocumentElement();
		if(eleItemList.hasChildNodes()){
			
			Element eleItem = SCXmlUtil.getChildElement(eleItemList, VSIConstants.ELE_ITEM);
			Element eleExtn = SCXmlUtil.getChildElement(eleItem, VSIConstants.ELE_EXTN);
			if(!YFCObject.isVoid(eleExtn) && "Y".equals(eleExtn.getAttribute("ExtnIsRestrictedItem"))){
				
				Element eleRestrictionList = SCXmlUtil.getChildElement(eleExtn, 
						VSIConstants.ELE_VSI_SHIP_RESTRICTED_ITEM_LIST);
				if(!YFCObject.isVoid(eleRestrictionList)){
					
					if(eleRestrictionList.hasChildNodes()){
						
						Document organizationList = getOrganizationDetails(env, shipNode);
						/*
						<OrganizationList>
    						<Organization OrganizationCode="9802">
       							<Node ShipNode="9802">
            						<ShipNodePersonInfo AddressLine1="15131 SOUTH LAGRANGE ROAD" AddressLine2="" 
            						City="ORLAND PARK" Country="US" State="IL" ZipCode="60462"/>
        						</Node>
    						</Organization>
						</OrganizationList>
						 */
						Element shipNodeAddress = XMLUtil.getElementByXPath(organizationList, 
								VSIConstants.XPATH_GET_ORG_LIST_SHIP_NODE_PERSON_INFO);
						if(!YFCObject.isVoid(shipNodeAddress)){
							
							// Get the state and country of the pick up store
							String strState = shipNodeAddress.getAttribute(VSIConstants.ATTR_STATE);
							String strCountry = shipNodeAddress.getAttribute(VSIConstants.ATTR_COUNTRY);
							
							if(!YFCObject.isVoid(strState) && !YFCObject.isVoid(strCountry)){
								
								NodeList nlItemRestriction = eleRestrictionList.getChildNodes();
								for(int x = 0; x < nlItemRestriction.getLength(); x++){
									
									Element eleItemRestriction = (Element) nlItemRestriction.item(x);
									String restrictedState = eleItemRestriction.getAttribute(VSIConstants.ATTR_STATE);
									String restrictedCountry = eleItemRestriction.getAttribute(VSIConstants.ATTR_COUNTRY);
									//If (State = input state and Country = input country) OR (State = "" and Country = input country)
									if((YFCObject.isVoid(restrictedState) && restrictedCountry.equalsIgnoreCase(strCountry)) || 
											(restrictedState.equalsIgnoreCase(strState) && restrictedCountry.equalsIgnoreCase(strCountry))){
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	private Document getOrganizationDetails(YFSEnvironment env, String shipNode) throws Exception{
		
		Document docOrganization = XMLUtil.createDocument(VSIConstants.ELE_ORGANIZATION);
		docOrganization.getDocumentElement().setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, shipNode);
		
		return VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_PICKUP_STORE_ADDRESS, docOrganization);
	}
	
	private Document getItemRestrictionDetails(YFSEnvironment env, String itemID) throws Exception{
		
		Document itemXML = XMLUtil.createDocument(VSIConstants.ELE_ITEM);
		Element eleItem = itemXML.getDocumentElement();
		eleItem.setAttribute(VSIConstants.ITEM_ID, itemID);
		eleItem.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, VSIConstants.ENT_VSI_CAT);
		eleItem.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
		eleItem.setAttribute(VSIConstants.ATTR_ITEM_GROUP_CODE, VSIConstants.ITEM_GROUP_CODE_PROD);
		
		return VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_ITEM_LIST_WITH_SHIP_RESTRICTIONS, itemXML);
	}
	
	private boolean isGreaterThanCurrentDate(String inputDate) throws Exception{

		// Sterling date format yyyy-MM-ddTHH:mm:ss
		inputDate = inputDate.substring(0, 10);
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
		
		Date date = new Date();
		String strCurrentDate = sdf.format(date);
		
		
		Date dInputDate = sdf.parse(inputDate);
		Date dCurrentDate = sdf.parse(strCurrentDate);
		
		if(dInputDate.after(dCurrentDate)){
			return true; 
		}
		
		return false;
	}

}
