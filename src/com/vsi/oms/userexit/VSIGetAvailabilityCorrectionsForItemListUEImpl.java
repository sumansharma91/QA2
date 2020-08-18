package com.vsi.oms.userexit;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.ue.YFSGetAvailabilityCorrectionsForItemListUE;

/**
 * @author IBM, Inc.
 * This userexit is implemented for wholesale orders to allocate/ backorder 
 * inventory at the CASE level. For orders with OrderType = WHOLESALE,
 * based on the required, availability quantities and the CASE configuration 
 * setup per item, item quantity is set for further scheduling.         
 */
public class VSIGetAvailabilityCorrectionsForItemListUEImpl implements YFSGetAvailabilityCorrectionsForItemListUE,VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetAvailabilityCorrectionsForItemListUEImpl.class);

	//This method calculates the unavailable qty and adjusts the qty accordingly at supply level
	public Document getAvailabilityCorrectionsForItemList(YFSEnvironment env, Document inXML)
			throws YFSException {

		log.beginTimer("VSIGetAvailabilityCorrectionsForItemListImpl.getAvailabilityCorrectionsForItemList");

		if(log.isDebugEnabled()) {
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}

		Document docOutput = inXML;
		try {

			String strOrganizationCode = null;
			Boolean isWholesale = false;
			Element eleItems = inXML.getDocumentElement();
			Element eleItem = SCXmlUtil.getChildElement(eleItems, ELE_ITEM);
			if(!YFCObject.isVoid(eleItem)){

				strOrganizationCode = SCXmlUtil.getAttribute(eleItem, ATTR_ORGANIZATION_CODE);
			}
			String strOHKey = eleItems.getAttribute(ATTR_ORDER_REFERENCE);

			if(!YFCCommon.isVoid(strOHKey)){

				Document docGetOrderListInput =XMLUtil.createDocument(ELE_ORDER);
				Element eleOrder = docGetOrderListInput.getDocumentElement();
				eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY,strOHKey);

				//Calling getOrderlist API to get the orderType
				Document docGetOrderListOutput=VSIUtils.invokeService(env,SERVICE_GET_ORDER_LIST, docGetOrderListInput);
				if(!YFCCommon.isVoid(docGetOrderListOutput)) {
					if(log.isDebugEnabled()) {
						log.debug("Printing GetOrderList ouptu XML :" + XmlUtils.getString(docGetOrderListOutput));
					}
					Element eleOutputOrder = (Element)(XMLUtil.getElementsByTagName(docGetOrderListOutput.getDocumentElement(), ELE_ORDER).get(0));
					String strOrderType = eleOutputOrder.getAttribute(ATTR_ORDER_TYPE);
					if(!YFCCommon.isVoid(strOrderType) && strOrderType.equalsIgnoreCase(WHOLESALE)) {

						isWholesale = true;
					}
				}
			}else if(!YFCObject.isVoid(strOrganizationCode)){

				Document getOrganizationListIn = SCXmlUtil.createDocument(ELE_ORGANIZATION);
				Element organizationElm = getOrganizationListIn.getDocumentElement();
				SCXmlUtil.setAttribute(organizationElm, ATTR_ORGANIZATION_CODE, strOrganizationCode);
				Document getOrgnizationListOut = VSIUtils.invokeService(env, SERVICE_GET_ORG_LIST, getOrganizationListIn);

				if (log.isDebugEnabled()) {
					log.debug("Output to getOrganizationList: "
							+ XMLUtil.getXMLString(getOrgnizationListOut));
				}

				if(getOrgnizationListOut.getDocumentElement().hasChildNodes()){

					Element eleOrganizationList = getOrgnizationListOut.getDocumentElement();
					Element eleOrganization = SCXmlUtil.getChildElement(eleOrganizationList, ELE_ORGANIZATION);
					if(!YFCObject.isVoid(eleOrganization)){

						Element eleExtn = SCXmlUtil.getChildElement(eleOrganization, ELE_EXTN);
						if(!YFCObject.isVoid(eleExtn)){

							isWholesale = SCXmlUtil.getBooleanAttribute(eleExtn, ATTR_EXTN_IS_WHOLESALE_ORG);
							if(log.isDebugEnabled()){
								log.debug("Org Code: " + strOrganizationCode);
								log.debug("IsWholesale: " + isWholesale);
							}
						}
					}
				}
			}

			if(isWholesale){

				//Calling getCommonCodeList API to check Case Qty Allocation for Enterprise
				Element eleCommonCode = (Element) (VSIUtils.getCommonCodeList(env, VSI_WH_ALLOC_CASEQTY, strOrganizationCode, ATTR_DEFAULT).get(0));
				if(!YFCCommon.isVoid(eleCommonCode)) {
					String strCodeShortDesc = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
					if(log.isDebugEnabled()){
						log.debug("VSIGetAvailabilityCorrectionsForItemListImpl.getAvailabilityCorrectionsForItemList: CodeShortdesc is " + strCodeShortDesc);
					}	

					if(!YFCObject.isVoid(strCodeShortDesc) && strCodeShortDesc.equalsIgnoreCase(FLAG_Y)){

						NodeList nlItem = docOutput.getElementsByTagName(ELE_ITEM);
						for(int i = 0; i < nlItem.getLength(); i++){

							eleItem = (Element) nlItem.item(i);
							Integer totalRequiredQty = Double.valueOf(SCXmlUtil.getAttribute(eleItem, ATTR_TOTAL_REQ_QTY)).intValue();
							Document docGetItemListInput = XMLUtil.createDocument(VSIConstants.ELE_ITEM);
							Element eleItemInput = docGetItemListInput.getDocumentElement();
							eleItemInput.setAttribute(VSIConstants.ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
							Document docGetItemListOutput = VSIUtils.invokeService(env, SERVICE_GET_ITEM_LIST_FOR_AVAILABILITY, docGetItemListInput);
							Element eleItemExtn = XMLUtil.getElementByXPath(docGetItemListOutput, "/ItemList/Item/Extn");
							Integer caseQty = 1;
							String strCaseQty = ONE;

							Boolean partnerCaseQtyExists = false;
							NodeList nlAdditionalAttribute = docGetItemListOutput.getElementsByTagName(ELE_ADDNL_ATTRIBUTE);
							for(int j = 0; j < nlAdditionalAttribute.getLength(); j++){

								Element eleAdditionalAttribute = (Element) nlAdditionalAttribute.item(j);
								String attrDomainID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_DOMAIN_ID);
								String attrGroupID = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_ATTRIBUTE_GROUP_ID);
								String name = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_NAME);
								if(attrDomainID.equals(ITEM_ATTRIBUTE) && attrGroupID.equals(WHOLESALE_ORG_LEVEL_CASE_QTY)
										&& name.contains(strOrganizationCode)){

									strCaseQty = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_VALUE);
									if(YFCObject.isVoid(strCaseQty)){
										strCaseQty = SCXmlUtil.getAttribute(eleAdditionalAttribute, ATTR_INTEGER_VALUE);
									}

									if(!YFCObject.isVoid(strCaseQty)){
										caseQty = Double.valueOf(strCaseQty).intValue();
										if(caseQty > 0){
											partnerCaseQtyExists = true;
											break;
										}
									}
								}
							}

							if(!partnerCaseQtyExists){
								if(!YFCObject.isVoid(eleItemExtn)){

									strCaseQty = SCXmlUtil.getAttribute(eleItemExtn, ATTR_EXTN_CASE_QTY);
									if(!YFCObject.isVoid(strCaseQty)){

										caseQty = Double.valueOf(strCaseQty).intValue();
										if(caseQty < 1) caseQty = 1;
									}
								}
							}


							if(log.isDebugEnabled()){
								log.debug("ItemID: " + eleItem.getAttribute(ATTR_ITEM_ID) + "Case Qty: " + caseQty);
							}

							// adjust req qty to case 
							totalRequiredQty = totalRequiredQty - (totalRequiredQty % caseQty);

							Integer totalAvailableQuantity = 0;
							Integer totalQuantity = 0;
							Integer totalSoftAssignedQty = 0;
							NodeList nlSupply = eleItem.getElementsByTagName(ELE_SUPPLY);
							for (int j = 0; j < nlSupply.getLength(); j++){

								Element eleSupply = (Element) nlSupply.item(j);
								totalQuantity = totalQuantity + Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_QUANTITY)).intValue();
								totalSoftAssignedQty = totalSoftAssignedQty + Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY)).intValue();
							}

							// Adjust available qty to case 
							totalAvailableQuantity = totalQuantity - totalSoftAssignedQty;
							totalAvailableQuantity = totalAvailableQuantity - (totalAvailableQuantity % caseQty);

							if((totalRequiredQty < caseQty) || // req qty is less than case qty -
									((totalAvailableQuantity < caseQty))){ // ordered qty = case qty, ordered qty < available qty - SET AVL QTY TO ZERO

								nlSupply = eleItem.getElementsByTagName(ELE_SUPPLY);
								for (int j = 0; j < nlSupply.getLength(); j++){

									Element eleSupply = (Element) nlSupply.item(j);
									SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY));
								}
							}else if(totalAvailableQuantity >= totalRequiredQty){ // available qty >= ordered qty

								totalAvailableQuantity = totalRequiredQty;
								nlSupply = eleItem.getElementsByTagName(ELE_SUPPLY);
								for (int j = 0; j < nlSupply.getLength(); j++){

									Element eleSupply = (Element) nlSupply.item(j);
									Integer currentSupplyQty = Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_QUANTITY)).intValue();
									Integer softAssignedQty = Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY)).intValue();
									Integer currentAvailableQty = currentSupplyQty - softAssignedQty;
									if(currentAvailableQty == 0){
										SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY));
									}else{

										if(currentAvailableQty >= totalAvailableQuantity){
											// TODO Add demand quantity here
											SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, String.valueOf(totalAvailableQuantity + softAssignedQty));
											totalAvailableQuantity = 0;
										}else{

											totalAvailableQuantity = totalAvailableQuantity - currentAvailableQty;
										}
									}
								}
							}else if(totalAvailableQuantity < totalRequiredQty){ // available qty < ordered qty

								if(totalAvailableQuantity < caseQty){

									nlSupply = eleItem.getElementsByTagName(ELE_SUPPLY);
									for (int j = 0; j < nlSupply.getLength(); j++){

										Element eleSupply = (Element) nlSupply.item(j);
										SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY));
									}
								}else{

									Integer allocableQty = totalAvailableQuantity - (totalAvailableQuantity % caseQty);
									nlSupply = eleItem.getElementsByTagName(ELE_SUPPLY);
									for (int j = 0; j < nlSupply.getLength(); j++){

										Element eleSupply = (Element) nlSupply.item(j);
										Integer currentSupplyQty = Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_QUANTITY)).intValue();
										Integer softAssignedQty = Double.valueOf(SCXmlUtil.getAttribute(eleSupply, ATTR_SOFT_ASSIGNED_QTY)).intValue();
										Integer currentAvailableQty = currentSupplyQty - softAssignedQty;
										if(currentAvailableQty > 0){

											if(allocableQty >= currentAvailableQty){
												SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, currentAvailableQty + softAssignedQty);
												allocableQty = allocableQty - currentAvailableQty;
											}else{
												SCXmlUtil.setAttribute(eleSupply, ATTR_QUANTITY, allocableQty + softAssignedQty);
												allocableQty = 0;
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
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.endTimer("VSIGetAvailabilityCorrectionsForItemListImpl.getAvailabilityCorrectionsForItemList");
		return docOutput;
	}
}
