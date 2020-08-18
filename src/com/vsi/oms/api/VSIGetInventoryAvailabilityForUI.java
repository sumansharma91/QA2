package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIGetInventoryAvailabilityForUI extends VSIBaseCustomAPI implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetInventoryAvailabilityForUI.class);
	
	/**
	 * This method gets the //ItemList/Item node list. For each <Item/> element,
	 * it gets the ItemID and calls getInventoryAvailability. With the output, 
	 * it creates the <Availability/> tag and appends it at the <Item/> element
	 * 
	 * <Availability CurrentAvailableQty="" FutureAvailableDate="" FutureAvailableQuantity="" 
	 * OnhandAvailableDate="" OnhandAvailableQuantity=""/>
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 */
	public Document getInventoryAvailability(YFSEnvironment env, Document inXML) throws Exception{
		
		if(log.isDebugEnabled()){
			
			log.debug("Input XML: " + SCXmlUtil.getString(inXML));
		}
		
		try{
			Boolean isOrderLine = false;
			Element eleItemList = XMLUtil.getElementByXPath(inXML, "//ItemList");
			if(YFCObject.isVoid(eleItemList)){
				eleItemList = XMLUtil.getElementByXPath(inXML, "//OrderLineList");
				if(!YFCObject.isVoid(eleItemList)){
					isOrderLine = true;
				}
			}
			
			if(!YFCObject.isVoid(eleItemList) && eleItemList.hasChildNodes()){
				String strStartDate = VSIUtils.getCurrentDate(YYYY_MM_DD);
				String strEndDate = VSIUtils.
						addDaysToPassedDateTime(VSIUtils.getCurrentDate(YYYY_MM_DD), "30", YYYY_MM_DD);
				
				Document getInventoryAvailabilityInXML = SCXmlUtil.createDocument(ELE_PROMISE);
				Element elePromise = getInventoryAvailabilityInXML.getDocumentElement();
				SCXmlUtil.setAttribute(elePromise, ATTR_ENTERPRISE_CODE, VSICOM_ENTERPRISE_CODE);
				SCXmlUtil.setAttribute(elePromise, ATTR_ORGANIZATION_CODE, VSICOM_ENTERPRISE_CODE);
				SCXmlUtil.setAttribute(elePromise, ATTR_DISTRIBUTION_RULE_ID, VSI_COM_DISTR_RULE_ID);
				SCXmlUtil.setAttribute(elePromise, ATTR_REQ_START_DATE, strStartDate);
				SCXmlUtil.setAttribute(elePromise, ATTR_REQ_END_DATE, strEndDate);
				
				Element elePromiseLines = SCXmlUtil.createChild(elePromise, ELE_PROMISE_LINES);
				Element eleItem = null;
				String strItemID = "";
				Element eleItemDetails = null;
				Element elePromiseLine = null;
				
				NodeList nlItem = null;
				if(isOrderLine){
					nlItem = eleItemList.getElementsByTagName(ELE_ORDER_LINE);
				}else{
					nlItem = eleItemList.getElementsByTagName(ELE_ITEM);
				}
				
				for(int i = 0; i < nlItem.getLength(); i++){
					
					strItemID = "";
					eleItem = (Element) nlItem.item(i);
					if(isOrderLine){
						eleItemDetails = SCXmlUtil.getChildElement(eleItem, ELE_ITEM_DETAILS);
						if(!YFCObject.isVoid(eleItemDetails)){
							strItemID = SCXmlUtil.getAttribute(eleItemDetails, ATTR_ITEM_ID);
						}
					}else{
						strItemID = SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID);
					}
					
					if(!YFCObject.isVoid(strItemID)){
						
						elePromiseLine = SCXmlUtil.createChild(elePromiseLines, ELE_PROMISE_LINE);
						SCXmlUtil.setAttribute(elePromiseLine, ATTR_ITEM_ID, strItemID);
						SCXmlUtil.setAttribute(elePromiseLine, ATTR_PRODUCT_CLASS, GOOD);
						SCXmlUtil.setAttribute(elePromiseLine, ATTR_UOM, UOM_EACH);
						SCXmlUtil.setAttribute(elePromiseLine, ATTR_LINE_ID, i);
					}
				}
				
				Document getAvailableInventoryOutXML = VSIUtils.invokeService
						(env, SERVICE_GET_AVAILABLE_INVENTORY_FOR_UI, getInventoryAvailabilityInXML);

				for(int j = 0; j < nlItem.getLength(); j++){
					
					strItemID = "";
					eleItem = (Element) nlItem.item(j);
					Element eleAvailabilityOut = SCXmlUtil.createChild(eleItem, ELE_AVAILABILITY);
					if(isOrderLine){
						SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_CURRENT_AVAILABLE_QTY, INT_ZER0_NUM);
					}
					SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_FIRST_FUTURE_AVAILABLE_DATE, FUTURE_DATE);
					SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_FUTURE_AVAILABLE_DATE, FUTURE_DATE);
					SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_FUTURE_AVAILABLE_QTY, INT_ZER0_NUM);
					SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_ONHAND_AVAILABLE_DATE, FUTURE_DATE);
					SCXmlUtil.setAttribute(eleAvailabilityOut, ATTR_ONHAND_AVAILABLE_QUANTITY, INT_ZER0_NUM);
					
					if(isOrderLine){
						eleItemDetails = SCXmlUtil.getChildElement(eleItem, ELE_ITEM_DETAILS);
						if(!YFCObject.isVoid(eleItemDetails)){
							strItemID = SCXmlUtil.getAttribute(eleItemDetails, ATTR_ITEM_ID);
						}
					}else{
						strItemID = SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID);
					}
					
					//System.out.println("//PromiseLine[@ItemID='" + strItemID + "']/Availability");
					Element eleAvailability = XMLUtil.getElementByXPath(getAvailableInventoryOutXML, 
							"//PromiseLine[@ItemID='" + strItemID + "']/Availability");
					
					if(!YFCObject.isVoid(eleAvailability) && eleAvailability.hasChildNodes()){
						
						NodeList nlAvailableInventory = eleAvailability.
								getElementsByTagName(ELE_AVAILABLE_INVENTORY);
						Element eleAvailableInventory = (Element) nlAvailableInventory.
								item(nlAvailableInventory.getLength() - 1);
						if(!YFCObject.isVoid(eleAvailableInventory)){
							
							eleAvailabilityOut = SCXmlUtil.getChildElement(
									eleItem, ELE_AVAILABILITY);
							
							Double dblAvailableFutureQuantity = 
									SCXmlUtil.getDoubleAttribute(
											eleAvailableInventory, ATTR_AVAILABLE_FUTURE_QTY);
							if(dblAvailableFutureQuantity == 0){
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FUTURE_AVAILABLE_DATE, FUTURE_DATE);
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FIRST_FUTURE_AVAILABLE_DATE, FUTURE_DATE);
							}else{
								//OMS-1002 : Start 
								Element eleFutureAvailableInventory= XMLUtil.getElementByXPath(getAvailableInventoryOutXML, 
										"//PromiseLine[@ItemID='" + strItemID + "']/Availability/AvailableInventory[@AvailableFutureQuantity>'0']");
								String strFirstFutureAvailableDate=SCXmlUtil.getAttribute(eleFutureAvailableInventory, ATTR_START_DATE);
								if(log.isDebugEnabled()){
									log.debug("strFirstFutureAvailableDate  : "+strFirstFutureAvailableDate);
								}
								//OMS-1002: End
								
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FUTURE_AVAILABLE_DATE, 
										SCXmlUtil.getAttribute(eleAvailableInventory, ATTR_START_DATE));
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FIRST_FUTURE_AVAILABLE_DATE, 
										strFirstFutureAvailableDate);
							}
							SCXmlUtil.setAttribute(eleAvailabilityOut, 
									ATTR_FUTURE_AVAILABLE_QTY, String.valueOf(dblAvailableFutureQuantity));
							
							Double dblAvailableOnhandQuantity = 
									SCXmlUtil.getDoubleAttribute(eleAvailableInventory, ATTR_AVAILABLE_ONHAND_QTY);
							if(dblAvailableOnhandQuantity == 0){
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_ONHAND_AVAILABLE_DATE, FUTURE_DATE);
							}else{
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_ONHAND_AVAILABLE_DATE, VSIUtils.getCurrentDate(YYYY_MM_DD));
							}
							SCXmlUtil.setAttribute(eleAvailabilityOut, 
									ATTR_ONHAND_AVAILABLE_QUANTITY, String.valueOf(dblAvailableOnhandQuantity));
							if(isOrderLine){
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_CURRENT_AVAILABLE_QTY, String.valueOf(dblAvailableOnhandQuantity + dblAvailableFutureQuantity));
							}
						}
					}
				}
			}
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		if(log.isDebugEnabled()){
			
			log.debug("Output XML: " + SCXmlUtil.getString(inXML));
		}
		
		return inXML;
	}
}
