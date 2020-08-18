package com.vsi.oms.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.userexit.VSIBeforeCreateOrderUEImpl;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleOrderOnCreate implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleOrderOnCreate.class);

	/**
	 * This custom implementation is used for validating (ItemDiscp & PriceDiscp)
	 * wholesale order.
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	public Document validateWholesaleOrder(YFSEnvironment env, Document inXML) throws Exception {

		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIWholesaleOrderOnCreate================================");
		}
		HashMap hmAddressID_AddressDetails = new HashMap<>();

		try {
			
		Element orderElement = inXML.getDocumentElement();
		Document getItemListInput = null;
		Document getItemListOutput = null;
		Document getPricelistLineListForItemInput = null;
		Document getPricelistLineListForItemOutput = null;
		StringBuffer strItemHoldDescription = new StringBuffer();
		StringBuffer strPriceHoldDescription = new StringBuffer();
		StringBuffer sbInvalidAddress = new StringBuffer();
		Document itemHoldDescriptionXML = null;
		Document priceHoldDescriptionXML = null;
		Document invalidAddressXML = null;

		orderElement.setAttribute("ValidateItem", "N");
		orderElement.setAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID, "NOPAYMENT");
		//START - Set Scheduling Rule
		String strSchedulingRule = null;
		ArrayList<Element> eleGetCommonCodeScheduleList = VSIUtils.getCommonCodeList(env, VSI_WH_SCHED_RULE,
				inXML.getDocumentElement().getAttribute(ATTR_ENTERPRISE_CODE), "");
		if(!eleGetCommonCodeScheduleList.isEmpty()) {
			strSchedulingRule = eleGetCommonCodeScheduleList.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
		}
		if(!YFCObject.isVoid(strSchedulingRule)){
			orderElement.setAttribute(ATTR_ALLOCATION_RULE_ID, strSchedulingRule);
		}
		//END -
		
		// Call get commoncode and get the flgs
		boolean blCheckDummyItem = false;
		boolean blCheckPriceDisc = false;
		boolean bInvalidAddress = false;
		
		String strEnterpriseCode = orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		
		setVirtualStore(env, VSIConstants.VIRTUAL_STORE_COM_CODE, orderElement);
		
		if(validateShipToZipCode(env, strEnterpriseCode)){

			NodeList nlPersonInfoShipTo = orderElement.getElementsByTagName(VSIConstants.ATTR_PERSON_INFO_SHIP_TO);
			for (int i = 0; i < nlPersonInfoShipTo.getLength(); i++){

				Element elePersonInfoShipTo = (Element)nlPersonInfoShipTo.item(i);
				SCXmlUtil.setAttribute(elePersonInfoShipTo, "IsAddressVerified", FLAG_Y);
				if(!YFCObject.isVoid(elePersonInfoShipTo)){

					String strCountry = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_COUNTRY);
					String strZipCode = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ZIPCODE);

					if(!isValidZipCode(env, strEnterpriseCode, strCountry, strZipCode)){

						bInvalidAddress = true;
						if(sbInvalidAddress.length() == 0){
							
							invalidAddressXML = SCXmlUtil.createDocument("InvalidZipCodes");
						}
						if(!sbInvalidAddress.toString().contains(strZipCode)){
							
							sbInvalidAddress.append("Invalid Zip Code: " + strZipCode + "\n ");
							Element eleInvalidZipCode = SCXmlUtil.
									createChild(invalidAddressXML.getDocumentElement(), "InvalidZipCode");
							SCXmlUtil.setAttribute(eleInvalidZipCode, ATTR_VALUE, strZipCode);
						}
					}
				}
			}
		}
		
		
		ArrayList<Element> eleGetCommonCodeListItem = VSIUtils.getCommonCodeList(env, VSI_WH_VALIDATE_ITEM,
				inXML.getDocumentElement().getAttribute(ATTR_ENTERPRISE_CODE), "");
		if(!eleGetCommonCodeListItem.isEmpty()) {
			if ("Y".equalsIgnoreCase(eleGetCommonCodeListItem.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION)))
				blCheckDummyItem = true;
		}
		
		ArrayList<Element> eleGetCommonCodeListPrice = VSIUtils.getCommonCodeList(env, VSI_WH_CHECK_PRICE,
				inXML.getDocumentElement().getAttribute(ATTR_ENTERPRISE_CODE), "");
		if(!eleGetCommonCodeListPrice.isEmpty()) {
			if ("Y".equalsIgnoreCase(eleGetCommonCodeListPrice.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION)))
				blCheckPriceDisc = true;
		}

		Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, ELE_ORDER_LINES);
		ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);

		boolean flgApplyHold = false;
		boolean flgApplyHoldForPrice = false;
		
		Document getCustomerListInXML = XMLUtil.createDocument(ELE_CUSTOMER);
		Element eleCustomer = getCustomerListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_TYPE, "01");
		SCXmlUtil.setAttribute(eleCustomer, ATTR_CUSTOMER_ID, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleCustomer, ATTR_ORGANIZATION_CODE, strEnterpriseCode);
		
		Document getCustomerListOutXML = VSIUtils.invokeService(env, SERVICE_GET_WHOLESALE_CUSTOMER_DETAILS, getCustomerListInXML);
		NodeList nlPersonInfo = getCustomerListOutXML.getElementsByTagName(ELE_PERSON_INFO);
		for(int i = 0; i < nlPersonInfo.getLength(); i++){
			
			Element elePersonInfo = (Element) nlPersonInfo.item(i);
			String strAddressID = SCXmlUtil.getAttribute(elePersonInfo, ATTR_ADDR_ID);
			if(!YFCObject.isVoid(strAddressID)){
				
				hmAddressID_AddressDetails.put(strAddressID, elePersonInfo);
				
			}
		}
		
		Integer count = 1;
		for (Element eleOrderLine : alOrderLines) {
			
				String strPrimeLineNo = eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO);
				if(YFCCommon.isVoid(strPrimeLineNo)){
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_PRIME_LINE_NO, String.valueOf(count));
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_SUB_LINE_NO, ONE);
					count++;
				}
				if(YFCObject.isVoid(SCXmlUtil.getAttribute(eleOrderLine, ATTR_FULFILLMENT_TYPE))){
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_FULFILLMENT_TYPE, LINETYPE_STH);
				}
				if(YFCObject.isVoid(SCXmlUtil.getAttribute(eleOrderLine, ATTR_LINE_TYPE))){
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_LINE_TYPE, LINETYPE_STH);
				}
				if(YFCObject.isVoid(SCXmlUtil.getAttribute(eleOrderLine, ATTR_DELIVERY_METHOD))){
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_DELIVERY_METHOD, ATTR_DEL_METHOD_SHP);
				}
				//OMS-1818 : Start
				if(YFCObject.isVoid(SCXmlUtil.getAttribute(eleOrderLine, ATTR_CUSTOMER_PO_NO))){
					SCXmlUtil.setAttribute(eleOrderLine, ATTR_CUSTOMER_PO_NO, orderElement.getAttribute(ATTR_CUSTOMER_PO_NO));
				}
				//OMS-1818 : End
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM, true);
				Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, ELE_LINE_PRICE_INFO, true);
				// String strItemId = eleItem.getAttribute(ATTR_ITEM_ID);
				String strUPCCode = eleItem.getAttribute(ATTR_UPCCODE);
				String strinUnitPrice = eleLinePriceInfo.getAttribute(ATTR_UNIT_PRICE);
				String strItemID = eleItem.getAttribute(ATTR_ITEM_ID);
				String strUOM = eleItem.getAttribute(ATTR_UOM);
				String strProductClass = eleItem.getAttribute(ATTR_PRODUCT_CLASS);
				
				//Element elePersonInfoShipTo = SCXmlUtil.
				
				//START - For DummyItem
				if(blCheckDummyItem) {
					
					if(!YFCObject.isVoid(strItemID)){
						
						getItemListInput = createInputForGetItemListByItemID(strEnterpriseCode, strItemID, strProductClass, strUOM);
						if(log.isDebugEnabled()){
							log.debug("Input for getItemList: " + SCXmlUtil.getString(getItemListInput));
						}
					}
					else if (!YFCObject.isVoid(strUPCCode)) {
						getItemListInput = createInputForGetItemListByUPC(strUPCCode, ATTR_UPC);
						if(log.isDebugEnabled()){
							log.debug("Input for getItemList: " + SCXmlUtil.getString(getItemListInput));
						}
					}else{
						getItemListInput = createInputForGetItemListByItemID(strEnterpriseCode, "DUMMYITEM", strProductClass, strUOM);
						if(log.isDebugEnabled()){
							log.debug("Input for getItemList: " + SCXmlUtil.getString(getItemListInput));
						}
					}

					getItemListOutput = VSIUtils.invokeAPI(env, TEMPLATE_GET_ITEM_LIST, API_GET_ITEM_LIST,
							getItemListInput);

					if(log.isDebugEnabled()){
						log.debug("Output for getItemList: " + SCXmlUtil.getString(getItemListOutput));
					}
					
					Element eleItemList = getItemListOutput.getDocumentElement();
					if(eleItemList.hasChildNodes() 
							&& !YFCObject.isVoid(SCXmlUtil.getChildElement(eleItemList, ELE_ITEM).getAttribute(ATTR_ITEM_ID)))
					{
						eleItem.setAttribute(ATTR_ITEM_ID,SCXmlUtil.getChildElement(eleItemList, ELE_ITEM).getAttribute(ATTR_ITEM_ID)); 
						Element eleOutputItem = SCXmlUtil.getChildElement(eleItemList, ELE_ITEM);
						Element eleOutputItemAliasList = SCXmlUtil.getChildElement(eleOutputItem, ELE_ITEM_ALIAS_LIST);
						if(!YFCObject.isVoid(eleOutputItemAliasList)){

							NodeList nlItemAlias = eleOutputItemAliasList.getElementsByTagName(ELE_ITEM_ALIAS);
							for(int i = 0; i < nlItemAlias.getLength(); i++){

								Element eleOutputItemAlias = (Element) nlItemAlias.item(i);
								if(eleOutputItemAlias.getAttribute(ATTR_ALIAS_NAME).equals(ATTR_UPC)){
									eleItem.setAttribute(ATTR_UPCCODE, eleOutputItemAlias.getAttribute(ATTR_ALIAS_VALUE));
								}
							}
						}
					} else {
						if(log.isDebugEnabled()){
							log.debug("IN_ELSE_FOR_ITEM");
						}
						eleItem.setAttribute(ATTR_ITEM_ID, "DUMMYITEM");
						
						if(strItemHoldDescription.length() == 0){
							
							itemHoldDescriptionXML = SCXmlUtil.createDocument("ItemDiscrepancies");
						}
						
						if(!strItemHoldDescription.toString().contains(strUPCCode)){
							strItemHoldDescription.append("Line No: " + eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO)
							+ " - Item ID: " + strItemID + " - UPC Code: " + strUPCCode + "\n");
							
							Element eleItemDiscrepancy = SCXmlUtil.
									createChild(itemHoldDescriptionXML.getDocumentElement(), "ItemDiscrepancy");
							SCXmlUtil.setAttribute(eleItemDiscrepancy, ATTR_PRIME_LINE_NO, eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO));
							SCXmlUtil.setAttribute(eleItemDiscrepancy, ATTR_ITEM_ID, strItemID);
							SCXmlUtil.setAttribute(eleItemDiscrepancy, ATTR_UPCCODE, strUPCCode);
						}
						
						if(log.isDebugEnabled()){
							log.debug("ITEM_DTLS_STRING_BUFFER"+strItemHoldDescription.toString());
						}
						if (!flgApplyHold)
							flgApplyHold = true;
					}
				}
				if(log.isDebugEnabled()){
					log.debug("flgApplyHold"+flgApplyHold);
				}
				//END - For DummyItem
				
				// START - For price Discrepancy
				if(blCheckPriceDisc) {
					if (!YFCObject.isVoid(eleItem.getAttribute(ATTR_ITEM_ID))) {
						getPricelistLineListForItemInput = createInputForGetPricelistLineListForItem(
								eleItem.getAttribute(ATTR_ITEM_ID), orderElement.getAttribute(ATTR_ENTERPRISE_CODE));
						if(log.isDebugEnabled()){
							log.debug("Input for getPricelistLineListForItem: "
									+ SCXmlUtil.getString(getPricelistLineListForItemInput));
						}
						
						getPricelistLineListForItemOutput = VSIUtils.invokeAPI(env, "",
								API_GET_PRICE_LIST_LINE_LIST_FOR_ITEM, getPricelistLineListForItemInput);

						if(log.isDebugEnabled()){
							log.debug("Output for getPricelistLineListForItem: "
									+ SCXmlUtil.getString(getPricelistLineListForItemOutput));
						}
						
						Element elePricelistLineList = getPricelistLineListForItemOutput.getDocumentElement();
						if (elePricelistLineList.hasChildNodes()) {
							Element ele_PricelistLine = SCXmlUtil.getChildElement(elePricelistLineList, ELE_PRICELIST_LINE);
							String unitPrice = ele_PricelistLine.getAttribute(ATTR_UNIT_PRICE);
							
							if (Double.valueOf(unitPrice).compareTo(Double.valueOf(strinUnitPrice)) != 0) {
								if(log.isDebugEnabled()){
									log.debug("PRICE_DISC_FOUND");
								}
								
								if(strPriceHoldDescription.length() == 0){
									
									priceHoldDescriptionXML = SCXmlUtil.createDocument("PriceDiscrepancies");
								}
								
								if(!strPriceHoldDescription.toString().contains(eleItem.getAttribute(ATTR_ITEM_ID))){
									strPriceHoldDescription.append("Line No: " + eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO)
									+ " - SKU ID: " + eleItem.getAttribute(ATTR_ITEM_ID) 
									+ " - Unit Price: " + strinUnitPrice
									+ "\n");
									
									Element elePriceDiscrepancy = SCXmlUtil.
											createChild(priceHoldDescriptionXML.getDocumentElement(), "PriceDiscrepancy");
									SCXmlUtil.setAttribute(elePriceDiscrepancy, ATTR_PRIME_LINE_NO, eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO));
									SCXmlUtil.setAttribute(elePriceDiscrepancy, ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
									SCXmlUtil.setAttribute(elePriceDiscrepancy, ATTR_UNIT_PRICE, strinUnitPrice);
								}
								if (!flgApplyHoldForPrice) {
									flgApplyHoldForPrice = true;
								}
							}
						}
					}
				}
				
				Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, ELE_PERSON_INFO_SHIP_TO);
				Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);
				if(YFCObject.isVoid(eleOrderLineExtn)){
					eleOrderLineExtn = SCXmlUtil.createChild(eleOrderLine, ELE_EXTN);
				}
				if(!YFCObject.isVoid(elePersonInfoShipTo)){
					
					Element elePersonInfoExtn = SCXmlUtil.getChildElement(elePersonInfoShipTo, ELE_EXTN);
					if(!YFCObject.isVoid(elePersonInfoExtn)){
						
						String strMarkForStoreNo = SCXmlUtil.getAttribute(elePersonInfoExtn, ATTR_EXTN_MARK_FOR_STORE_NO);
						if(!YFCObject.isVoid(strMarkForStoreNo)){
							
							StringBuffer sbNote = new StringBuffer();
							Element eleNotes = SCXmlUtil.createChild(eleOrderLine, ELE_NOTES);
							Element eleNote = SCXmlUtil.createChild(eleNotes, ELE_NOTE);
							SCXmlUtil.setAttribute(eleNote, ATTR_REASON_CODE, "Mark For Store");
							sbNote.append(strMarkForStoreNo);
							SCXmlUtil.setAttribute(eleOrderLineExtn, ATTR_EXTN_MARK_FOR_STORE_NO, strMarkForStoreNo);
							if(hmAddressID_AddressDetails.containsKey(strMarkForStoreNo)){

								Element elePersonInfo = (Element) hmAddressID_AddressDetails.get(strMarkForStoreNo);
								elePersonInfo = (Element) inXML.importNode(elePersonInfo, true);
								Element elePersonInfoMarkFor = SCXmlUtil.createChild(eleOrderLine, ELE_PERSON_INFO_MARK_FOR);
								NamedNodeMap attributes = elePersonInfo.getAttributes();
								for (int j = 0; j < attributes.getLength(); j++)
								{
									Attr node = (Attr) attributes.item(j);
									elePersonInfoMarkFor.setAttributeNode((Attr) node.cloneNode(false));
								}
								
								SCXmlUtil.setAttribute(elePersonInfoMarkFor, ATTR_ADDR_ID, strMarkForStoreNo);
								elePersonInfoExtn.removeAttribute(ATTR_EXTN_MARK_FOR_STORE_NO);
								sbNote.append(" - " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_FIRST_NAME));
								sbNote.append("   " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_ADDR_LINE_1));
								if(!YFCObject.isVoid(SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_ADDR_LINE_2))){
									sbNote.append("  " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_ADDR_LINE_2));
								}
								sbNote.append("  " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_CITY));
								sbNote.append("  " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_STATE));
								sbNote.append("  " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_COUNTRY));
								sbNote.append("  " + SCXmlUtil.getAttribute(elePersonInfoMarkFor, ATTR_ZIPCODE));
							}
							SCXmlUtil.setAttribute(eleNote, ATTR_NOTE_TEXT, sbNote.toString());
						}
					}
				}
				
				if(log.isDebugEnabled()){
					log.debug("flgApplyHoldForPrice"+flgApplyHoldForPrice);
				}
				// END - For price Discrepancy
			
		}

		if (flgApplyHold) {
			env.setTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_SB", strItemHoldDescription);
			env.setTxnObject("VSI_WH_ITEM_DISC_RAISE_ALERT_XML", itemHoldDescriptionXML);
			if(log.isDebugEnabled()){
				log.debug("ItemHold Data: " + strItemHoldDescription);
			}
			inXML = applyItemNotFoundHold(env, inXML);
		}

		if (flgApplyHoldForPrice) {
			env.setTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_SB", strPriceHoldDescription);
			env.setTxnObject("VSI_WH_PRICE_DISC_RAISE_ALERT_XML", priceHoldDescriptionXML);
			if(log.isDebugEnabled()){
				log.debug("PriceHold Data: " + strPriceHoldDescription);
			}
			inXML = applyPriceDiscHold(env, inXML);
		}
		
		//if()
		
		if(bInvalidAddress){
			
			env.setTxnObject("VSI_WH_INVALID_ADDRESS_SB", sbInvalidAddress);
			env.setTxnObject("VSI_WH_INVALID_ADDRESS_XML", invalidAddressXML);
			inXML = applyInvalidAddressHold(env, inXML);
		}
		setTermsCode(env,orderElement);
		VSIBeforeCreateOrderUEImpl impl = new VSIBeforeCreateOrderUEImpl();
		impl.stampExtnAttributes(env, inXML);
		//WOP-190 : Stamp Reservation : Start
		stampOrderLineReservation(env,inXML);
		//WOP-190 : Stamp Reservation : End
		return inXML;
		
		} catch (Exception e) {
			
			e.printStackTrace();
			throw VSIUtils.getYFSException(e, "Exception occurred",
					"Exception in VSIWholesaleOrderOnCreate.validateWholesaleOrder()");

		}finally{
			
			if(!YFCObject.isVoid(hmAddressID_AddressDetails)){
				
				hmAddressID_AddressDetails.clear();
				hmAddressID_AddressDetails = null;
			}
		}

	}
	
	private Document applyInvalidAddressHold(YFSEnvironment env, Document inXML) {
		// TODO Auto-generated method stub
		Element eleHoldTypes;
		eleHoldTypes = SCXmlUtil.getChildElement(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		if (YFCObject.isNull(eleHoldTypes)) {
			eleHoldTypes = SCXmlUtil.createChild(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		}
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(ATTR_HOLD_TYPE, "VSI_WH_INVALID_ADDR");
		eleHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);

		return inXML;
	}

	private boolean validateShipToZipCode(YFSEnvironment env, String strEnterpriseCode) throws Exception{
		
		Document docGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
		Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, ATTR_DEFAULT);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_WH_VALIDATE_ZIP");
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEnterpriseCode);

		// Call getCommonCodeList API with with the input: <CommonCode CodeType="<codeType>"  CodeValue="<codeValue>"/> to get the common codes
		docGetCommonCodeList = VSIUtils.invokeAPI(env, VSIConstants.API_COMMON_CODE_LIST, docGetCommonCodeList);

		// Get the list of common codes from the response document
		Element eleCommonCodeList = docGetCommonCodeList.getDocumentElement();
		if(eleCommonCodeList.hasChildNodes()){
			
			eleCommonCode = SCXmlUtil.getChildElement(eleCommonCodeList, VSIConstants.ELE_COMMON_CODE);
			String strCodeShortDesc = SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_SHORT_DESCRIPTION);
			if(strCodeShortDesc.equalsIgnoreCase(VSIConstants.FLAG_Y)){
				
				return true;
			}
		}
		return false;
	}

	private boolean isValidZipCode(YFSEnvironment env, String strEnterpriseCode, String strCountry, String strZipCode) throws Exception {
		// TODO Auto-generated method stub
		
		Document regionListInputXML = SCXmlUtil.createDocument(ELE_REGION);
		Element eleRegion = regionListInputXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleRegion, ATTR_REGION_SCHEMA_KEY, ALL_US);
		SCXmlUtil.setAttribute(eleRegion, ATTR_ORGANIZATION_CODE, ATTR_DEFAULT);
		Element elePersonInfo = SCXmlUtil.createChild(eleRegion, ELE_PERSON_INFO);
		SCXmlUtil.setAttribute(elePersonInfo, ATTR_COUNTRY, strCountry);
		SCXmlUtil.setAttribute(elePersonInfo, ATTR_ZIPCODE, strZipCode);
		
		Document regionListOutputMXL = VSIUtils.invokeAPI(env, API_GET_REGION_LIST, regionListInputXML);
		
		Element eleRegions = regionListOutputMXL.getDocumentElement();
		if(eleRegions.hasChildNodes()){
			
			NodeList nlRegion = eleRegions.getElementsByTagName(ELE_REGION);
			for (int i = 0; i < nlRegion.getLength(); i++){
				
				eleRegion = (Element) nlRegion.item(i);
				if(!YFCObject.isVoid(eleRegion)){
					
					String regionName = SCXmlUtil.getAttribute(eleRegion, ATTR_REGION_NAME);
					if(!YFCObject.isVoid(regionName) && regionName.contains(strEnterpriseCode)){
						
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * This method is used to create input to getItemList API.
	 * 
	 * @param strUPCCode
	 * @param strAliasName
	 * @return
	 */
	private Document createInputForGetItemListByItemID(String callingOrgCode, String strItemID, String strProductClass, String strUOM) {
		// Create a new document with root element as Item
		Document docInput = SCXmlUtil.createDocument(ELE_ITEM);
		Element eleItem = docInput.getDocumentElement();
		eleItem.setAttribute(ATTR_ITEM_ID, strItemID);
		eleItem.setAttribute(ATTR_CALL_ORG_CODE, callingOrgCode);
		eleItem.setAttribute(ATTR_PRODUCT_CLASS, strProductClass);
		eleItem.setAttribute(ATTR_UOM, strUOM);

		return docInput;
	}

	/**
	 * This method is used to create input to getItemList API.
	 * 
	 * @param strUPCCode
	 * @param strAliasName
	 * @return
	 */
	private Document createInputForGetItemListByUPC(String strUPCCode, String strAliasName) {
		// Create a new document with root element as Item
		Document docInput = SCXmlUtil.createDocument(ELE_ITEM);
		Element eleItem = docInput.getDocumentElement();
		Element eleItemAliasList = docInput.createElement(ELE_ITEM_ALIAS_LIST);
		eleItem.appendChild(eleItemAliasList);
		Element eleItemAlias = docInput.createElement(ELE_ITEM_ALIAS);
		eleItemAliasList.appendChild(eleItemAlias);
		eleItemAlias.setAttribute(ATTR_ALIAS_NAME, strAliasName);

		if (!YFCCommon.isVoid(strUPCCode)) {
			eleItemAlias.setAttribute(ATTR_ALIAS_VALUE, strUPCCode);
		}

		return docInput;
	}

	/**
	 * This method is used to apply Item not found hold to an order
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 */
	private Document applyItemNotFoundHold(YFSEnvironment env, Document inXML) {

		Element eleHoldTypes;
		eleHoldTypes = SCXmlUtil.getChildElement(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		if (YFCObject.isNull(eleHoldTypes)) {
			eleHoldTypes = SCXmlUtil.createChild(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		}
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(ATTR_HOLD_TYPE, "VSI_WH_INV_ITEM_HOLD");
		eleHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);

		return inXML;
	}

	/**
	 * This method is used to create input to getPricelistLineList API
	 * 
	 * @param itemID
	 * @param enterpriseCode
	 * @return
	 * @throws Exception
	 */
	private Document createInputForGetPricelistLineListForItem(String itemID, String enterpriseCode) throws Exception {

		Document getPriceListLine = SCXmlUtil.createDocument(ELE_PRICELIST_LINE);
		Element elePricelistLine = getPriceListLine.getDocumentElement();
		SCXmlUtil.setAttribute(elePricelistLine, ATTR_ORGANIZATION_CODE, enterpriseCode);
		Element eleItem = SCXmlUtil.createChild(elePricelistLine, ELE_ITEM);
		eleItem.setAttribute(ATTR_ITEM_ID, itemID);

		Element elePricelistHeader = SCXmlUtil.createChild(elePricelistLine, ELE_PRICE_LIST_HEADER);
		elePricelistHeader.setAttribute(ATTR_PRICING_STATUS, "ACTIVE");

		return getPriceListLine;
	}

	/**
	 * This method is used to apply price discrepancy hold to an order
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 */
	private Document applyPriceDiscHold(YFSEnvironment env, Document inXML) {

		Element eleHoldTypes;
		eleHoldTypes = SCXmlUtil.getChildElement(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		if (YFCObject.isNull(eleHoldTypes)) {
			eleHoldTypes = SCXmlUtil.createChild(inXML.getDocumentElement(), ELE_ORDER_HOLD_TYPES);
		}
		Element eleHoldType = SCXmlUtil.createChild(eleHoldTypes, ELE_ORDER_HOLD_TYPE);
		eleHoldType.setAttribute(ATTR_HOLD_TYPE, "VSI_WH_PRI_DISC_HOLD");
		eleHoldType.setAttribute(ATTR_STATUS, STATUS_CREATE);

		return inXML;
	}
	
	private void setVirtualStore(YFSEnvironment env, String codetype, Element orderElement) {
		
		String virtualStore = "";
		String costCenter = "";
		
		try{
			Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

			Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
			eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE,SCXmlUtil.getAttribute(orderElement, ATTR_ENTERPRISE_CODE));
			eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,codetype);

			Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);

			if (docAfterGetCommonCodeList.getDocumentElement().hasChildNodes()) {
				Element eleOutCommonCode=(Element) docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
				virtualStore = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				costCenter = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
				if(!YFCObject.isVoid(virtualStore)){

					SCXmlUtil.setAttribute(orderElement, VSIConstants.ATTR_ENTERED_BY, virtualStore);
					SCXmlUtil.setAttribute(orderElement, ATTR_BILL_TO_ID, virtualStore);
				}

				if(!YFCObject.isVoid(costCenter)){

					Element eleExtn = SCXmlUtil.getChildElement(orderElement, ELE_EXTN);
					if(!YFCObject.isVoid(eleExtn)){
						SCXmlUtil.setAttribute(eleExtn, "ExtnCostCenter", costCenter);
					}
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		////System.out.println("***remorsePrd " +remorsePrd);
		
		// TODO Auto-generated method stub
	}
	
	
	private void setTermsCode(YFSEnvironment env, Element eleOrder) throws Exception {
		log.beginTimer("VSIWholesaleOrderOnCreate.setTermsCode");
		Document docInputCustCreditDtls = XMLUtil.createDocument(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS);
		Element eleCustCreditDtls = docInputCustCreditDtls.getDocumentElement();
		String strEnterpriseCode = eleOrder.getAttribute(ATTR_ENTERPRISE_CODE);
		eleCustCreditDtls.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,strEnterpriseCode);
		if(log.isDebugEnabled()) {
			log.debug("Input to service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docInputCustCreditDtls) );
		}
		Document docOutputCustCreditDtls = VSIUtils.invokeService(env,VSIConstants.SERVICE_GET_CUST_CREDIT_DTLS_LIST,docInputCustCreditDtls);
		if(log.isDebugEnabled()) {
			log.debug("Output from service VSIWholesaleGetCustCreditDetailsList:"+ XMLUtil.getXMLString(docOutputCustCreditDtls) );
		}
		if (!YFCCommon.isVoid(docOutputCustCreditDtls) && docOutputCustCreditDtls.getDocumentElement().hasChildNodes()) {
			eleCustCreditDtls = (Element) docOutputCustCreditDtls.getElementsByTagName(VSIConstants.ELE_VSI_WH_CUST_CREDIT_DETAILS).item(0);
			String strPymtTerms = eleCustCreditDtls.getAttribute(ATTR_PAYMENT_TERMS);
			eleOrder.setAttribute(ATTR_TERMS_CODE, strPymtTerms);
		}
		log.endTimer("VSIWholesaleOrderOnCreate.setTermsCode");
	}
	//WOP-190 Stamp Reservation : Start
	private void stampOrderLineReservation(YFSEnvironment env,Document inXML){
		log.debug("Input for stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
		try {
			Element eleOrder=inXML.getDocumentElement();
			String strOrganizationCode=eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strAllocationRuleID=eleOrder.getAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID);
			
			Element elePersonInfoShipTo=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			
			NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iLineCount=nlOrderLine.getLength();
			for (int i = 0; i < iLineCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleOrderLineReservations=SCXmlUtil.createChild(eleOrderLine, "OrderLineReservations");
				String strDeliveryMethod=eleOrderLine.getAttribute("DeliveryMethod");
				if(!YFCCommon.isStringVoid(strDeliveryMethod)&& "SHP".equals(strDeliveryMethod)){
					Document docReserveInvOP=reserveAvailableInvetory(env,eleOrderLine,elePersonInfoShipTo,strOrganizationCode,
							strAllocationRuleID);
					NodeList nlReservation=docReserveInvOP.getElementsByTagName("Reservation");
					int iReservationCount=nlReservation.getLength();
					for (int j = 0; j < iReservationCount; j++) {
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
				}
				
			}
			
			log.debug("output from  stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
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
	    	
	    	log.debug("input for reserveAvailableInventory API : "+SCXmlUtil.getString(docReserveInvIP));
	    	
	    	//invoking reserveAvailableInventory 
	    	docReserveInvOP=VSIUtils.invokeAPI(env, "reserveAvailableInventory", docReserveInvIP);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		log.debug("reserveAvailableInvetory output : "+SCXmlUtil.getString(docReserveInvOP));
		return docReserveInvOP;
	}
	
	// WOP-190 Stamp Reservation : End
}// end of class