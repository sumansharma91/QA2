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
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIManageItem extends VSIBaseCustomAPI implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIManageItem.class);

	/**
	<ItemList>
		<Item Action="Manage" ItemGroupCode="PROD" ItemID="1034473" OrganizationCode="VSI-Cat" UnitOfMeasure="EACH">
			<PrimaryInformation DefaultProductClass="GOOD" Description="C-1000 COMPLEX" ExtendedDescription="C-1000 COMPLEX" 
			IsDeliveryAllowed="N" IsPickupAllowed="Y" IsReturnable="Y" IsShippingAllowed="Y" ShortDescription="C-1000 COMPLEX" 
			Status="3000" UnitHeight="4.1" UnitHeightUOM="IN" UnitLength="2.3" UnitLengthUOM="IN" UnitWeight="0.340" 
			UnitWeightUOM="LBS" UnitWidth="2.3" UnitWidthUOM="IN"/>
			<InventoryParameters ATPRule="VSI-Cat-Def-ATP"/>
			<ItemAliasList>
				<ItemAlias Action="Modify" AliasName="UPC" AliasValue="766536010127"/>
			</ItemAliasList>
			<Extn ExtnActSkuID="VS-1012" ExtnBrandTitle="the Vitamin Shoppe" ExtnItemSize="100.00" ExtnItemType="CP" 
			ExtnItemUOM="EA" ExtnTaxProductCode="0003" ExtnIsProp65Item="N" ExtnIsRefrigeratedItem="N" ExtnIsDiscontinuedItem="N" ExtnIsMCLItem=""/>
			<PrimaryInformation ManufacturerName="the Vitamin Shoppe"/>
		</Item>
	</ItemList>
	 */
	
	public Document updateItem(YFSEnvironment env, Document inXML) throws Exception{
		
		if(log.isDebugEnabled()){
			
			log.debug("Input XML: " + SCXmlUtil.getString(inXML));
		}
		
		Boolean updateMCLItemInventory = false;
		Document getCCListInXML = SCXmlUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = getCCListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleCommonCode, ATTR_CODE_TYPE, CODE_TYPE_VSI_UPDATE_MCL_INV);
		SCXmlUtil.setAttribute(eleCommonCode, ATTR_CODE_VALUE, CODE_VALUE_UPDATE_MCL_ITEM_INVENTORY);

		// Call getCommonCodeList to get the VSI_UPDATE_MCL_INV, UPDATE_MCL_ITEM_INVENTORY code type/ code value
		Document getCCListOutXML = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, getCCListInXML);

		Element eleCommonCodeList = getCCListOutXML.getDocumentElement();
		eleCommonCode = SCXmlUtil.getChildElement(eleCommonCodeList, ELE_COMMON_CODE);
		if(!YFCObject.isVoid(eleCommonCode)){

			String strCodeShortDescription = SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_SHORT_DESCRIPTION);
			if(strCodeShortDescription.equalsIgnoreCase(FLAG_Y)){

				// If CodeShortDesc is Y, MCL inventory needs to be updated
				updateMCLItemInventory = true;
			}
		}

		// If MCL inventory should not be updated, call the manageItem API and exit
		if(!updateMCLItemInventory){

			VSIUtils.invokeAPI(env, API_MANAGE_ITEM, inXML);
			return inXML;
		}

		Element eleItemList = inXML.getDocumentElement();
		NodeList nlItem = eleItemList.getElementsByTagName(ELE_ITEM);
		for(int i = 0; i < nlItem.getLength(); i++){

			Element eleItem = (Element) nlItem.item(i);
			if(!YFCObject.isVoid(eleItem)){

				Element eleExtn = SCXmlUtil.getChildElement(eleItem, ELE_EXTN);
				if(!YFCObject.isVoid(eleExtn)){

					String strIsMCLItemFlagFromInput = SCXmlUtil.getAttribute(eleExtn, ATTR_EXTN_IS_MCL_ITEM);
					if(strIsMCLItemFlagFromInput.equalsIgnoreCase(FLAG_N)){

						Document getItemListInXML = SCXmlUtil.createDocument(ELE_ITEM);
						Element eleItemIn = getItemListInXML.getDocumentElement();
						SCXmlUtil.setAttribute(eleItemIn, ATTR_ITEM_ID, SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID));
						SCXmlUtil.setAttribute(eleItemIn, ATTR_ORGANIZATION_CODE, SCXmlUtil.getAttribute(eleItem, ATTR_ORGANIZATION_CODE));
						SCXmlUtil.setAttribute(eleItemIn, ATTR_UOM, SCXmlUtil.getAttribute(eleItem, ATTR_UOM));
						SCXmlUtil.setAttribute(eleItemIn, ATTR_ITEM_GROUP_CODE, SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_GROUP_CODE));
						SCXmlUtil.setAttribute(eleItemIn, ATTR_PRODUCT_CLASS, SCXmlUtil.getAttribute(eleItem, GOOD));

						Document getItemListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ITEM_LIST, getItemListInXML);
						Element eleItemListOut = getItemListOutXML.getDocumentElement();
						if(eleItemListOut.hasChildNodes()){

							Element eleItemOut = SCXmlUtil.getChildElement(eleItemListOut, ELE_ITEM);
							Element eleExtnOut = SCXmlUtil.getChildElement(eleItemOut, ELE_EXTN);
							if(!YFCObject.isVoid(eleExtnOut)){

								String strIsMCLItemFlagFromDB = SCXmlUtil.getAttribute(eleExtnOut, ATTR_EXTN_IS_MCL_ITEM);
								if(strIsMCLItemFlagFromDB.equalsIgnoreCase(FLAG_Y) && strIsMCLItemFlagFromInput.equalsIgnoreCase(FLAG_N)){

									// ExtnIsMCLItem flag has changed from Y to N. VSI_MCL_INV_AVAILABILITY has to be updated.

									Document mclAvailabilityListInXML = SCXmlUtil.createDocument(ELE_VSI_MCL_INV_AVAILABILITY);
									Element eleVSIMCLInvAvailability = mclAvailabilityListInXML.getDocumentElement();
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_ITEM_ID, SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID));
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_PRODUCT_CLASS, GOOD);
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_UOM, SCXmlUtil.getAttribute(eleItem, ATTR_UOM));

									// Call VSIGetMCLInvAvailabilityList to get existing entry for input ItemID
									Document mclAvailabilityListOutXML = 
											VSIUtils.invokeService(env, SERVICE_GET_MCL_INV_AVAILABILITY_LIST, mclAvailabilityListInXML);

									/* 
									 * Create input for update to VSI_MCL_INV_AVAILABILITY table
									 * Set AvailableQty and Status="N"
									 */
									mclAvailabilityListInXML = SCXmlUtil.createDocument(ELE_VSI_MCL_INV_AVAILABILITY);
									eleVSIMCLInvAvailability = mclAvailabilityListInXML.getDocumentElement();
									SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_AVAIl_QTY, INT_ZER0_NUM);
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
									}
								}else if(strIsMCLItemFlagFromDB.equalsIgnoreCase(FLAG_N) && strIsMCLItemFlagFromInput.equalsIgnoreCase(FLAG_Y)){
									
									// Call monitorItemAvailability to invoke the REALTIME_ATP_MONITOR.REALTIME_AVAILABILITY_CHANGE_LIST event
									// to populate the VSI_MCL_INV_AVAILABILITY table
									Document monitorItemAvailabilityInXML = SCXmlUtil.createDocument(ELE_MONITOR_ITEM_AVAILBILITY);
									Element elemonitorItemAvailability = monitorItemAvailabilityInXML.getDocumentElement();
									SCXmlUtil.setAttribute(elemonitorItemAvailability, ATTR_ITEM_ID, SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID));
									SCXmlUtil.setAttribute(elemonitorItemAvailability, ATTR_ORGANIZATION_CODE, ENT_MCL);
									SCXmlUtil.setAttribute(elemonitorItemAvailability, ATTR_PRODUCT_CLASS, GOOD);
									SCXmlUtil.setAttribute(elemonitorItemAvailability, ATTR_UOM, SCXmlUtil.getAttribute(eleItem, ATTR_UOM));
									
									VSIUtils.invokeAPI(env, API_MONITOR_ITEM_AVAILBILITY, monitorItemAvailabilityInXML);
								}
							}
						}
					}
					
					//OMS-1456 : Start
					String strExtnActSkuID=eleExtn.getAttribute("ExtnActSkuID");
					String  strExtnSKUType=eleExtn.getAttribute("ExtnSKUType");
					String strExtnInternetMSRP=eleExtn.getAttribute("ExtnInternetMSRP");
					if(log.isDebugEnabled()){
						log.debug("ExtnActSkuID :  "+strExtnActSkuID + "  ExtnSKUType :  "+strExtnSKUType+"  strExtnInternetMSRP: "+strExtnInternetMSRP);
					}
					if(YFCCommon.isStringVoid(strExtnActSkuID) || (!YFCCommon.isStringVoid(strExtnSKUType)&& "12".equals(strExtnSKUType) ) 
							||(!YFCCommon.isStringVoid(strExtnInternetMSRP) && "0".equals(strExtnInternetMSRP))){
						eleExtn.setAttribute("ExtnATGEligible", "N");
					}
					else{
						eleExtn.setAttribute("ExtnATGEligible", "Y");
					}
					//OMS-1456 : End
				}
			}
		}
		VSIUtils.invokeAPI(env, API_MANAGE_ITEM, inXML);
		return inXML;
	}
}
