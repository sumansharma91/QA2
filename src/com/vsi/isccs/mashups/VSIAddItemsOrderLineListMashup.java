package com.vsi.isccs.mashups;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.isccs.mashups.utils.SCCSMashupUtils;
import com.ibm.isccs.order.mashups.SCCSAddItemUtils;
import com.ibm.isccs.order.mashups.SCCSGetCompleteOrderLineListPaginatedMashup;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.sterlingcommerce.ui.web.framework.utils.SCUIUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;

public class VSIAddItemsOrderLineListMashup extends SCCSGetCompleteOrderLineListPaginatedMashup implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIAddItemsOrderLineListMashup.class);
	private String sEnterpriseCode = null;


	public void massageAPIInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext)
	  {
	    super.massageAPIInput(inputEl, mashupMetaData, uiContext);
	    String sOrderHeaderKey = null;
	    if (!SCUtil.isVoid(uiContext.getRequest().getAttribute("scControllerInput")))
	    {
	      Element elemInput = (Element)uiContext.getRequest().getAttribute("scControllerInput");
	      
	      Element elemExchangeOrder = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(elemInput, "ExchangeOrders", true), "ExchangeOrder");
	      if (!SCUtil.isVoid(elemExchangeOrder))
	      {
	        sOrderHeaderKey = elemExchangeOrder.getAttribute("OrderHeaderKey");
	        Element elemMashupInputDef = SCCSMashupUtils.getMashupInputDefinition(mashupMetaData);
	        if (elemMashupInputDef.hasAttribute("OrderHeaderKey")) {
	          inputEl.setAttribute("OrderHeaderKey", sOrderHeaderKey);
	        }
	        Element elemMashupInputOrder = SCXmlUtil.getChildElement(elemMashupInputDef, "Order");
	        if (!SCUtil.isVoid(elemMashupInputOrder))
	        {
	          Element elemInputOrder = SCXmlUtil.getChildElement(inputEl, "Order", true);
	          if (elemMashupInputOrder.hasAttribute("DocumentType")) {
	            elemInputOrder.setAttribute("DocumentType", elemExchangeOrder.getAttribute("DocumentType"));
	          }
	          if (elemMashupInputOrder.hasAttribute("EnterpriseCode"))
	          {
	            this.sEnterpriseCode = elemExchangeOrder.getAttribute("EnterpriseCode");
	            elemInputOrder.setAttribute("EnterpriseCode", elemExchangeOrder.getAttribute("EnterpriseCode"));
	          }
	          if (elemMashupInputOrder.hasAttribute("OrderNo")) {
	            elemInputOrder.setAttribute("OrderNo", elemExchangeOrder.getAttribute("OrderNo"));
	          }
	        }
	      }
	      else if (SCUtil.isVoid(sOrderHeaderKey))
	      {
	        sOrderHeaderKey = elemInput.getAttribute("OrderHeaderKey");
	      }
	    }
	    else
	    {
	      sOrderHeaderKey = inputEl.getAttribute("OrderHeaderKey");
	    }
	    SCCSMashupUtils.addAttributeToUIContext(uiContext, "OrderHeaderKey", this, sOrderHeaderKey);
	    setReferenceModifyts(inputEl, uiContext);
	    uiContext.setAttribute("IsNewLineComputationRequired", "Y");
	    inputEl.removeAttribute("UIModifyts");
	  }
	
	@Override
	public Element massageOutput(Element outEl,
			SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {

		//OOB Data below
		super.massageAPIOutput(outEl, mashupMetaData, uiContext);
	    if (SCUtil.isVoid(outEl)) {
	      return outEl;
	    }
	    if (SCUtil.isVoid(outEl.getElementsByTagName("OrderLine"))) {
	      return outEl;
	    }
	    if (outEl.getElementsByTagName("OrderLine").getLength() == 0) {
	      return outEl;
	    }
	    String serviceAssocType = null;
	    Element apiInput = getValidateItemForOrderingInput(uiContext);
	    Element validateItemForOrdering_output = (Element)SCUIMashupHelper.invokeMashup("additems_validateItemForOrdering", apiInput, uiContext);
	    if (!SCUtil.isVoid(validateItemForOrdering_output))
	    {
	      List<Element> orderLines = SCXmlUtil.getChildrenList(outEl);
	      if (orderLines.size() > 0) {
	        for (Element orderLine : orderLines)
	        {
	          String sOrderLineKey = SCXmlUtil.getAttribute(orderLine, "OrderLineKey");
	          Element eBundleParentLine = SCXmlUtil.getChildElement(orderLine, "BundleParentLine");
	          String sBundleParentLineKey = null;
	          Element eOrder = SCXmlUtil.getChildElement(orderLine, "Order");
	          if (!SCUtil.isVoid(eOrder)) {
	            this.sEnterpriseCode = eOrder.getAttribute("EnterpriseCode");
	          }
	          if ((!SCUtil.isVoid(this.sEnterpriseCode)) && (SCUtil.isVoid(serviceAssocType))) {
	            serviceAssocType = SCCSAddItemUtils.getRelationshipTypeForService(this.sEnterpriseCode, uiContext);
	          }
	          if ((null == serviceAssocType) || ("".equals(serviceAssocType)))
	          {
	            Element eItemDetails = SCXmlUtil.getChildElement(orderLine, "ItemDetails");
	            Element eServiceAssocList = SCXmlUtil.getChildElement(eItemDetails, "ItemServiceAssocList");
	            if (!SCUIUtils.isVoid(eServiceAssocList)) {
	              eItemDetails.removeChild(eServiceAssocList);
	            }
	          }
	          if (!SCUtil.isVoid(eBundleParentLine)) {
	            sBundleParentLineKey = SCXmlUtil.getAttribute(eBundleParentLine, "OrderLineKey");
	          }
	          Element eErrors = getOrderValidationErrorElement(validateItemForOrdering_output, sOrderLineKey);
	          if ((!SCUtil.isVoid(eErrors)) && (SCXmlUtil.getChildrenList(eErrors) != null) && (SCXmlUtil.getChildrenList(eErrors).size() > 0)) {
	            SCXmlUtil.importElement(orderLine, eErrors);
	          }
	          if (orderLine.getAttribute("IsBundleParent").equals("Y")) {
	            updateErrorElementToBundleParent(orderLine, validateItemForOrdering_output, sOrderLineKey);
	          }
	          if (sBundleParentLineKey != null) {
	            outEl.removeChild(orderLine);
	          }
	        }
	      }
	    }
	    copyAllErrorsToRoot(outEl, validateItemForOrdering_output);
	    
	    try{
			Boolean isOrderLine = false;
			Element eleItemList = SCXmlUtil.getXpathElement(outEl, "//ItemList");
			if(YFCObject.isVoid(eleItemList)){
				eleItemList = SCXmlUtil.getXpathElement(outEl, "//OrderLineList");
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
				
				 Element eleOut=(Element) SCUIMashupHelper.invokeMashup(
						"extn_VSIGetAvailableInventoryforUI", getInventoryAvailabilityInXML.getDocumentElement(), uiContext);

				 Document getAvailableInventoryOutXML = eleOut.getOwnerDocument();
				 
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
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FUTURE_AVAILABLE_DATE, 
										SCXmlUtil.getAttribute(eleAvailableInventory, ATTR_START_DATE));
								SCXmlUtil.setAttribute(eleAvailabilityOut, 
										ATTR_FIRST_FUTURE_AVAILABLE_DATE, 
										SCXmlUtil.getAttribute(eleAvailableInventory, ATTR_START_DATE));
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
		
	    //System.out.println("outEl::"+SCXmlUtil.getString(outEl));
		return outEl;		
	}

	private Element getValidateItemForOrderingInput(SCUIContext uiContext) {
		Element eInput = SCXmlUtil.createDocument("Order").getDocumentElement();
		String str = (String) SCCSMashupUtils.getAttributeFromUIContext(
				uiContext, "OrderHeaderKey", this);
		eInput.setAttribute("OrderHeaderKey", str);
		str = null;
		return eInput;
	}
	
	private Element getOrderValidationErrorElement(
			Element validateItemForOrderingResult, String sOrderLineKey) {
		Element eErrors = null;
		Element eOrderLines = SCXmlUtil.getChildElement(
				validateItemForOrderingResult, "OrderLines");

		Iterator<Element> it_line = SCXmlUtil.getChildren(eOrderLines);
		while (it_line.hasNext()) {
			Element eOrderLine = (Element) it_line.next();
			String orderLineKey = eOrderLine.getAttribute("OrderLineKey");
			if (SCUtil.equals(orderLineKey, sOrderLineKey)) {
				eErrors = SCXmlUtil.getChildElement(eOrderLine, "Errors", true);
				if (!SCUtil.isVoid(eErrors)) {
					eOrderLine.setAttribute("OrderLineErrorIncluded", "Y");
					break;
				}
			}
		}
		return eErrors;
	}
	
	private void updateErrorElementToBundleParent(Element orderLine,
			Element validateItemForOrdering_output, String sOrderLineKey) {
		Element Olines = SCXmlUtil.getChildElement(
				validateItemForOrdering_output, "OrderLines");
		List<Element> vLines = SCXmlUtil.getChildrenList(Olines);
		for (Element vLine : vLines) {
			Element vBundleParentLineEle = SCXmlUtil.getChildElement(vLine,
					"BundleParentLine");
			if (!SCUtil.isVoid(vBundleParentLineEle)) {
				String vBundleParentLineKey = SCXmlUtil.getAttribute(
						vBundleParentLineEle, "OrderLineKey");
				if (vBundleParentLineKey.equals(sOrderLineKey)) {
					Element eErrors = SCXmlUtil.getChildElement(vLine,
							"Errors", true);
					List<Element> eErrorList = SCXmlUtil
							.getChildrenList(eErrors);
					if ((eErrorList != null) && (eErrorList.size() > 0)) {
						if (SCUtil.isVoid(SCXmlUtil.getChildElement(orderLine,
								"Errors"))) {
							SCXmlUtil.createChild(orderLine, "Errors");
						}
						for (Element eError : eErrorList) {
							SCXmlUtil.importElement(SCXmlUtil.getChildElement(
									orderLine, "Errors"), eError);
						}
						vLine.setAttribute("OrderLineErrorIncluded", "Y");
					}
				}
			}
		}
	}
	
	private void copyAllErrorsToRoot(Element outEl,
			Element validateItemForOrdering_output) {
		Element Olines = SCXmlUtil.getChildElement(
				validateItemForOrdering_output, "OrderLines");
		List<Element> vLines = SCXmlUtil.getChildrenList(Olines);
		for (Element vLine : vLines) {
			if (!vLine.getAttribute("OrderLineErrorIncluded").equals("Y")) {
				Element eErrors = SCXmlUtil.getChildElement(vLine, "Errors",
						true);
				List<Element> eErrorList = SCXmlUtil.getChildrenList(eErrors);
				if ((eErrorList != null) && (eErrorList.size() > 0)) {
					if (SCUtil.isVoid(SCXmlUtil.getChildElement(outEl,
							"ErrorsNotInCurrentPage"))) {
						SCXmlUtil.createChild(outEl, "ErrorsNotInCurrentPage");
					}
					SCXmlUtil.importElement(SCXmlUtil.getChildElement(outEl,
							"ErrorsNotInCurrentPage"), vLine);
				}
			}
		}
	}
	
	private void setReferenceModifyts(Element inputEl, SCUIContext uiContext) {
		if (!SCUIUtils.isVoid(inputEl.getAttribute("UIModifyts"))) {
			uiContext.setAttribute("LastUIModifyts",
					inputEl.getAttribute("UIModifyts"));
		}
	}

}
