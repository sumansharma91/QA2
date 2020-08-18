package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIMCLInventoryAvailability extends VSIBaseCustomAPI implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIMCLInventoryAvailability.class);

	/*
	<InventoryItem ItemID="2006062" ProductClass="GOOD" UnitOfMeasure="EACH">
    	<Item ItemID="2006062" ItemKey="201509041909423011737">
        	<Extn ExtnIsMCLItem="Y"/>
    	</Item>
    	<AvailabilityChanges>
        	<AvailabilityChange AgentCriteriaId="REALTIME_ATP_MONITOR_OP3"
            	AlertLevel="3" AlertQuantity="10"
            	AlertRaisedOn="2017-07-13T16:20:29-04:00"
            	AlertType="REALTIME_FUTURE_MAX"
            	FirstFutureAvailableDate="2017-07-16"
            	FutureAvailableDate="2017-07-16" FutureAvailableQuantity="4"
            	MonitorOption="3" Node="" OnhandAvailableDate="2017-07-13" 
            	OnhandAvailableQuantity="8"/>
    	</AvailabilityChanges>
	</InventoryItem> 
	 */
	
	public Document updateMCLAvailableInventory(YFSEnvironment env, Document inXML) throws Exception{
		
		Element eleInventoryItem = inXML.getDocumentElement();
		String strItemID = SCXmlUtil.getAttribute(eleInventoryItem, ATTR_ITEM_ID);
		String strProductClass = SCXmlUtil.getAttribute(eleInventoryItem, ATTR_PRODUCT_CLASS);
		String strUOM = SCXmlUtil.getAttribute(eleInventoryItem, ATTR_UOM);
		
		if(eleInventoryItem.hasChildNodes()){
			
			Element eleItem = SCXmlUtil.getChildElement(eleInventoryItem, ELE_ITEM);
			if(!YFCObject.isVoid(eleItem)){
				
				if(eleItem.hasChildNodes()){
					
					Element eleExtn = SCXmlUtil.getChildElement(eleItem, ELE_EXTN);
					if(!YFCObject.isVoid(eleExtn)){
						String strIsMCLIString = SCXmlUtil.getAttribute(eleExtn, 
								ATTR_EXTN_IS_MCL_ITEM);
						if(YFCObject.equals(strIsMCLIString, FLAG_N)){
							
							// If Item is not an MCL item, exit
							return inXML;
						}
						
						Element eleAvailabilityChanges = SCXmlUtil.getChildElement(
								eleInventoryItem, ELE_AVAILABILITY_CHANGES);
						if(!YFCObject.isVoid(eleAvailabilityChanges)){
							
							if(eleAvailabilityChanges.hasChildNodes()){
								
								Double dblOnhandAvailableQuantity = 0.0;
								NodeList nlAvailabilityChange = eleAvailabilityChanges.getChildNodes();
								for(int i = 0; i < nlAvailabilityChange.getLength(); i++){

									Element eleAvailabilityChange = (Element) nlAvailabilityChange.item(i);
									dblOnhandAvailableQuantity = dblOnhandAvailableQuantity
											+ SCXmlUtil.getDoubleAttribute(eleAvailabilityChange, 
													ATTR_ONHAND_AVAILABLE_QUANTITY);
								}
								
								if(log.isDebugEnabled()){
									log.debug("ItemID: " + strItemID + " Onhand Available Quantity: " 
											+ String.valueOf(dblOnhandAvailableQuantity));
								}
								
								// Create input XML for VSIGetMCLInvAvailabilityList service
								Document mclAvailabilityListInXML = SCXmlUtil.createDocument(ELE_VSI_MCL_INV_AVAILABILITY);
								Element eleVSIMCLInvAvailability = mclAvailabilityListInXML.getDocumentElement();
								SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_ITEM_ID, strItemID);
								SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_PRODUCT_CLASS, strProductClass);
								SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_UOM, strUOM);
								
								// Call VSIGetMCLInvAvailabilityList to get existing entry for input ItemID
								Document mclAvailabilityListOutXML = 
										VSIUtils.invokeService(env, SERVICE_GET_MCL_INV_AVAILABILITY_LIST, mclAvailabilityListInXML);
								
								/* 
								 * Create input for update to VSI_MCL_INV_AVAILABILITY table
								 * Set AvailableQty and Status="N"
								 */
								mclAvailabilityListInXML = SCXmlUtil.createDocument(ELE_VSI_MCL_INV_AVAILABILITY);
								eleVSIMCLInvAvailability = mclAvailabilityListInXML.getDocumentElement();
								SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_AVAIl_QTY, dblOnhandAvailableQuantity);
								SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_STATUS, FLAG_N);
								
								// If record exists
								if(mclAvailabilityListOutXML.getDocumentElement().hasChildNodes()){
									
									// Get the InvAvailKey from existing record
									Element eleMCLInvAvailabilityList = mclAvailabilityListOutXML.getDocumentElement();
									Element eleMCLInvAvailability = SCXmlUtil.getFirstChildElement(eleMCLInvAvailabilityList);							
									String strInvAvailKey = SCXmlUtil.getAttribute(eleMCLInvAvailability, ATTR_INV_AVAIL_KEY);
									
									// Set the InvAvailKey from the existing record
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_INV_AVAIL_KEY, strInvAvailKey);
									
									// Call VSIChangeMCLInvAvailability to update existing record
									VSIUtils.invokeService(env, SERVICE_CHANGE_MCL_INV_AVAILABILITY, mclAvailabilityListInXML);
									
								}else{ // Create new record
									
									// Set ItemID, ProductClass, UnitOfMeasure
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_ITEM_ID, strItemID);
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_PRODUCT_CLASS, strProductClass);
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_UOM, strUOM);
									
									// Call the VSICreateMCLInvAvailability service to create a new record
									VSIUtils.invokeService(env, SERVICE_CREATE_MCL_INV_AVAILABILITY, mclAvailabilityListInXML);
								}
							}
						}
					}
				}
			}
		}
		
		return inXML;
	}
}
