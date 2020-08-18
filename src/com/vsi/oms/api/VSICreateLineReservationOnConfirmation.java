package com.vsi.oms.api;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICreateLineReservationOnConfirmation{

	private YFCLogCategory log = YFCLogCategory.instance(VSICreateLineReservationOnConfirmation.class);


	public Document stampOrderLineReservation(YFSEnvironment env,Document inXML){
		if(log.isDebugEnabled()){
			log.debug("Input for VSICreateLineReservationOnConfirmation.stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
		}
		try {
			boolean isChangeOrderReq=false;
			Element eleOrder=inXML.getDocumentElement();
			String strOrganizationCode=eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strAllocationRuleID=eleOrder.getAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID);
			
			Element elePersonInfoShipTo=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			
			//create input for createInventoryActivityList
			Document docInventoryActivityListIP=SCXmlUtil.createDocument("InventoryActivityList");
			Element eleInventoryActivityList=docInventoryActivityListIP.getDocumentElement();
			
			//creating input for changeOrder
			Document docChangeOrderInput=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleOrderChangeIp=docChangeOrderInput.getDocumentElement();
			eleOrderChangeIp.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			Element eleOrderLinesChangeIp=SCXmlUtil.createChild(eleOrderChangeIp, VSIConstants.ELE_ORDER_LINES);
			
			NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iLineCount=nlOrderLine.getLength();
			for (int i = 0; i < iLineCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleOrderLineChangeIp=SCXmlUtil.createChild(eleOrderLinesChangeIp, VSIConstants.ELE_ORDER_LINE);
				eleOrderLineChangeIp.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
				Element eleOrderLineReservations=SCXmlUtil.createChild(eleOrderLineChangeIp, "OrderLineReservations");
				String strDeliveryMethod=eleOrderLine.getAttribute("DeliveryMethod");
				if(!YFCCommon.isStringVoid(strDeliveryMethod)&& "SHP".equals(strDeliveryMethod)){
					Document docReserveInvOP=reserveAvailableInvetory(env,eleOrderLine,elePersonInfoShipTo,strOrganizationCode,
							strAllocationRuleID);
					NodeList nlReservation=docReserveInvOP.getElementsByTagName("Reservation");
					int iReservationCount=nlReservation.getLength();
					for (int j = 0; j < iReservationCount; j++) {
						isChangeOrderReq=true;
						Element eleOrderLineReservation=SCXmlUtil.createChild(eleOrderLineReservations, "OrderLineReservation");
						Element eleReservation=(Element)nlReservation.item(j);
						eleOrderLineReservation.setAttribute("ReservationID", eleReservation.getAttribute("ReservationID"));
						eleOrderLineReservation.setAttribute("ItemID", eleReservation.getAttribute("ItemID"));
						eleOrderLineReservation.setAttribute("ProductClass", eleReservation.getAttribute("ProductClass"));
						eleOrderLineReservation.setAttribute("UnitOfMeasure", eleReservation.getAttribute("UnitOfMeasure"));
						eleOrderLineReservation.setAttribute("Quantity", eleReservation.getAttribute("ReservedQty"));
						eleOrderLineReservation.setAttribute("Node", eleReservation.getAttribute("ShipNode"));
						eleOrderLineReservation.setAttribute("RequestedReservationDate", eleReservation.getAttribute("ReservationNodeShipDate"));
					}
					//creating inventory activity entry for newly created item
					Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
					String strItemID=eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
					String strProductClass=eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
					String strUnitOfMeasure=eleItem.getAttribute(VSIConstants.ATTR_UOM);
					Element eleInventoryActivity=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
					eleInventoryActivity.setAttribute("CreateForInvItemsAtNode", "N");
					eleInventoryActivity.setAttribute("ItemID", strItemID);
					eleInventoryActivity.setAttribute("OrganizationCode", "ADP");
					eleInventoryActivity.setAttribute("ProductClass", strProductClass);
					eleInventoryActivity.setAttribute("UnitOfMeasure", strUnitOfMeasure);
					
					Element eleInventoryActivity1=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
					eleInventoryActivity1.setAttribute("CreateForInvItemsAtNode", "N");
					eleInventoryActivity1.setAttribute("ItemID", strItemID);
					eleInventoryActivity1.setAttribute("OrganizationCode", "MCL");
					eleInventoryActivity1.setAttribute("ProductClass", strProductClass);
					eleInventoryActivity1.setAttribute("UnitOfMeasure", strUnitOfMeasure);
				}
			}
			
			if(log.isDebugEnabled()){
				log.debug("Input for createInventoryActivityList:  "+SCXmlUtil.getString(docInventoryActivityListIP));
			}
			if(eleInventoryActivityList.hasChildNodes()) {
				VSIUtils.invokeAPI(env, "createInventoryActivityList", docInventoryActivityListIP);
			}
			if(isChangeOrderReq){
				if(log.isDebugEnabled()){
					log.debug("VSICreateLineReservationOnConfirmation: Input for ChangeOrder : "+XMLUtil.getXMLString(docChangeOrderInput));
				}
				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrderInput);
			}
			
			if(log.isDebugEnabled()){
				log.debug("output from  stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return inXML;
	}

	private Document reserveAvailableInvetory(YFSEnvironment env,Element eleOrderLine,
			Element elePersonInfoShipTo,String strOrganizationCode, String strAllocationRuleID){
		Document docReserveInvIP=null;
		Document docReserveInvOP=null;
		try {
			docReserveInvIP=SCXmlUtil.createDocument("Promise");
			Element elePromise=docReserveInvIP.getDocumentElement();
			elePromise.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strOrganizationCode);
			if(!YFCCommon.isStringVoid(strAllocationRuleID)){
				elePromise.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, strAllocationRuleID);
			}
			Element eleReservationParameters=SCXmlUtil.createChild(elePromise, "ReservationParameters");
			
			String strSeqName = "VSI_SEQ_RESERVATION_ID";
	    	String strSeqNumber = VSIDBUtil.getNextSequence(env, strSeqName);
	    	eleReservationParameters.setAttribute("ReservationID", strSeqNumber);
	    	
	    	Element elePromiseLines=SCXmlUtil.createChild(elePromise, "PromiseLines");
	    	Element elePromiseLine=SCXmlUtil.createChild(elePromiseLines, "PromiseLine");
	    	
	    	Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
	    	elePromiseLine.setAttribute("LineId", "1");
	    	elePromiseLine.setAttribute("ItemID", eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID));
	    	elePromiseLine.setAttribute("RequiredQty", eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY));
	    	elePromiseLine.setAttribute("UnitOfMeasure", eleItem.getAttribute(VSIConstants.ATTR_UOM));
	    	elePromiseLine.setAttribute("ProductClass", eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS));
	    	//elePromiseLine.setAttribute("DistributionRuleId", "VSI_DC");
	    	elePromiseLine.setAttribute("FulfillmentType", eleOrderLine.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE));
	    	elePromiseLine.setAttribute("DeliveryMethod", eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD));
	    	String strShipNode=eleOrderLine.getAttribute(VSIConstants.ATTR_SHIP_NODE);
	    	if(!YFCCommon.isStringVoid(strShipNode))
	    		elePromiseLine.setAttribute(VSIConstants.ATTR_SHIP_NODE, strShipNode);
	    	
	    	Element eleShipToAddress=SCXmlUtil.createChild(elePromise, "ShipToAddress");
	    	eleShipToAddress.setAttribute("AddressLine1",  elePersonInfoShipTo.getAttribute("AddressLine1"));
	    	eleShipToAddress.setAttribute("City",  elePersonInfoShipTo.getAttribute("City"));
	    	eleShipToAddress.setAttribute("Country",  elePersonInfoShipTo.getAttribute("Country"));
	    	eleShipToAddress.setAttribute("DayPhone",  elePersonInfoShipTo.getAttribute("DayPhone"));
	    	eleShipToAddress.setAttribute("FirstName",  elePersonInfoShipTo.getAttribute("FirstName"));
	    	eleShipToAddress.setAttribute("LastName",  elePersonInfoShipTo.getAttribute("LastName"));
	    	eleShipToAddress.setAttribute("State",  elePersonInfoShipTo.getAttribute("State"));
	    	eleShipToAddress.setAttribute("ZipCode", elePersonInfoShipTo.getAttribute("ZipCode"));
	    	
	    	if(log.isDebugEnabled()){
	    		log.debug("input for reserveAvailableInventory API : "+SCXmlUtil.getString(docReserveInvIP));
	    	}
	    	
	    	//invoking reserveAvailableInventory 
	    	docReserveInvOP=VSIUtils.invokeAPI(env, "reserveAvailableInventory", docReserveInvIP);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(log.isDebugEnabled()){
			log.debug("reserveAvailableInvetory output : "+SCXmlUtil.getString(docReserveInvOP));
		}
		return docReserveInvOP;
	}
}
