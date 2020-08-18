/**
 * 
 */
package com.vsi.isccs.mashups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.ibm.isccs.mashups.utils.SCCSMashupUtils;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.util.YFCCommon;

/**
 * This class is a MashUp class to massage Input and Output of getAlternateStoreAvailablity API
 * call during initialization of Store Selection POP UP.
 * 
 * @author IBM
 */
public class VSIGetAlternateStoreAvailabilityMashup extends SCCSBaseMashup {

	/* (non-Javadoc)
	 * @see com.ibm.isccs.common.mashups.SCCSBaseMashup#massageInput(org.w3c.dom.Element, com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData, com.sterlingcommerce.ui.web.framework.context.SCUIContext)
	 */
	public Element massageInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		inputEl = super.massageInput(inputEl, mashupMetaData, uiContext);

		int pageSize = getPageSize(uiContext);
		int pageNum = getPageNumber(uiContext);
		SCCSMashupUtils.addAttributeToUIContext(uiContext, "pageSize", this, new Integer(pageSize));
		SCCSMashupUtils.addAttributeToUIContext(uiContext, "pageNum", this, new Integer(pageNum));
		inputEl.setAttribute("Mode", "03");
		Element eNodeSearch = SCXmlUtil.getChildElement(inputEl, "NodeSearch");
		String sFulfillmentTypeRule = null;
		if ((!SCUtil.isVoid(eNodeSearch)) && (!SCUtil.isVoid(eNodeSearch.getAttribute("FulfillmentType")))) {
			sFulfillmentTypeRule = eNodeSearch.getAttribute("FulfillmentType");
			if (SCUtil.equals(sFulfillmentTypeRule, "YCD_MORE_STORE_SEARCH")) {
				Element eRuleOutput = SCCSMashupUtils.getRuleValue(uiContext, "YCD_MORE_STORE_DISTANCE_TO_CONSIDER", inputEl.getAttribute("OrganizationCode"), null);
				if ((!SCUtil.isVoid(eRuleOutput)) && (!SCUtil.isVoid(eRuleOutput.getAttribute("RuleSetValue")))) {
					eNodeSearch.setAttribute("DistanceToConsider", eRuleOutput.getAttribute("RuleSetValue"));
				}
			}
			Element eRuleOutput = SCCSMashupUtils.getRuleValue(uiContext, sFulfillmentTypeRule, inputEl.getAttribute("OrganizationCode"), null);
			if ((!SCUtil.isVoid(eRuleOutput)) && (!SCUtil.isVoid(eRuleOutput.getAttribute("RuleSetValue"))))
				eNodeSearch.setAttribute("FulfillmentType", eRuleOutput.getAttribute("RuleSetValue"));
			else {
				eNodeSearch.removeAttribute("FulfillmentType");
			}
		}

		Element elemGSNInput = getInputForSurroundingNodeList(inputEl);
		if (!SCUtil.isVoid(sFulfillmentTypeRule)) {
			elemGSNInput.setAttribute("FulfillmentType", sFulfillmentTypeRule);
		}
		else {
			elemGSNInput.removeAttribute("FulfillmentType");
		}
		Element elemGSNOutput = (Element)SCUIMashupHelper.invokeMashup("storeSelection_getSurroundingNodeList", elemGSNInput, uiContext);

		prepareInputForGASA(elemGSNOutput, inputEl, pageSize, pageNum, uiContext);

		return inputEl;
	}

	/* (non-Javadoc)
	 * @see com.ibm.isccs.common.mashups.SCCSBaseMashup#massageOutput(org.w3c.dom.Element, com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData, com.sterlingcommerce.ui.web.framework.context.SCUIContext)
	 * In massageOutput OOB method, we have merge few attributes (IsAvailableOnStore, OnHandQuantity, ProcuredQty) in the output 
	 * and check them at UI to set the LineType at order line Level.
	 */
	public Element massageOutput(Element outEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		super.massageOutput(outEl, mashupMetaData, uiContext);

		int pageNum = ((Integer)SCCSMashupUtils.getAttributeFromUIContext(uiContext, "pageNum", this)).intValue();
		int pageSize = ((Integer)SCCSMashupUtils.getAttributeFromUIContext(uiContext, "pageSize", this)).intValue();
		boolean isFirstPage = ((Boolean)SCCSMashupUtils.getAttributeFromUIContext(uiContext, "isFirstPage", this)).booleanValue();
		boolean isLastPage = ((Boolean)SCCSMashupUtils.getAttributeFromUIContext(uiContext, "isLastPage", this)).booleanValue();

		Element elemPage = SCXmlUtil.createDocument("Page").getDocumentElement();
		elemPage.setAttribute("IsFirstPage", isFirstPage ? "Y" : "N");
		elemPage.setAttribute("IsLastPage", isLastPage ? "Y" : "N");
		elemPage.setAttribute("PageNumber", String.valueOf(pageNum));
		elemPage.setAttribute("PageSize", String.valueOf(pageSize));

		Element elemOutput = SCXmlUtil.createChild(elemPage, "Output");
		Element elemLastRecord = SCXmlUtil.createChild(elemPage, "LastRecord");

		//
		Object objFOFL = SCCSMashupUtils.getAttributeFromUIContext(uiContext, "getFulfillmentOptionForLinesOutput", this);
		if(objFOFL != null){
			Element eleGetFulfillmentOptionForLinesOutput = (Element) objFOFL;
			Element elePromiseLines = SCXmlUtil.getChildElement(eleGetFulfillmentOptionForLinesOutput, "PromiseLines");
			ArrayList<Element> arrListPromiseLine = SCXmlUtil.getChildren(elePromiseLines, "PromiseLine");
			Map<String, Map<String, List<Object>>> mapLineAvailability = new HashMap<String, Map<String,List<Object>>>();
			Map<String, String> mapLineKeyItem = new HashMap<String, String>();
			for(Element elePromiseLine : arrListPromiseLine){
				Element eleOrderLine = SCXmlUtil.getChildElement(elePromiseLine, "OrderLine");
				String strOrderLineKey = eleOrderLine.getAttribute("OrderLineKey");
				Element eleOptions = SCXmlUtil.getChildElement(elePromiseLine, "Options");
				ArrayList<Element> arrListOption = SCXmlUtil.getChildren(eleOptions, "Option");
				Map<String, List<Object>> mapLineDetail = new HashMap<String, List<Object>>();
				for(Element eleOption : arrListOption){
					Element eleAssignments = SCXmlUtil.getChildElement(eleOption, "Assignments");
					ArrayList<Element> arrListAssignment = SCXmlUtil.getChildren(eleAssignments, "Assignment");
					double dProcuredQty = 0.0;
					double dQuantity = 0.0;
					String strShipNode = "";
					String strOnStoreAvaialbleDate = "NA";
					String strFutureAvaialbleDate = "NA";
					for(Element eleAssignment : arrListAssignment){
						strShipNode = eleAssignment.getAttribute("ShipNode");
						double dTempProcuredQty = SCXmlUtil.getDoubleAttribute(eleAssignment, "ProcuredQty");
						String strDeliveryDate = eleAssignment.getAttribute("DeliveryDate");
						if(!"".equals(strDeliveryDate.trim())){
							strDeliveryDate = strDeliveryDate.split("T")[0];
						} else {
							strDeliveryDate = "NA";
						}
						if(dTempProcuredQty == 0){
							dQuantity = dQuantity + SCXmlUtil.getDoubleAttribute(eleAssignment, "Quantity");
							strOnStoreAvaialbleDate = strDeliveryDate;
						} else {
							dProcuredQty = dProcuredQty + dTempProcuredQty;
							strFutureAvaialbleDate = strDeliveryDate;							
						}
					}
					if(strShipNode.length() > 0){
						List<Object> listQty = new ArrayList<Object>();
						listQty.add(getTwoDecimalStringQty(dQuantity));
						listQty.add(strOnStoreAvaialbleDate);
						listQty.add(getTwoDecimalStringQty(dProcuredQty));
						listQty.add(strFutureAvaialbleDate);
						mapLineDetail.put(strShipNode, listQty);
					}
				}
				// Check the order line key value and based on that putting it as key value in the Map. If the call is happen 
				// from Add Item Page, we will put Item Id as key Value in the map.
				if("".equals(strOrderLineKey)){
					String strItemId = elePromiseLine.getAttribute("ItemID");
					mapLineAvailability.put(strItemId, mapLineDetail);
				}else{
					String strItemId = elePromiseLine.getAttribute("ItemID");
					mapLineAvailability.put(strItemId, mapLineDetail);
					mapLineAvailability.put(strOrderLineKey, mapLineDetail);
					mapLineKeyItem.put(strItemId, strOrderLineKey);
				}

			}
			if(!mapLineAvailability.isEmpty()){
				Map<String, List<Object>> mapLineDetail = null;
				Element eleOrderLines = SCXmlUtil.getChildElement(outEl, "OrderLines");
				ArrayList<Element> arrListOrderLine = SCXmlUtil.getChildren(eleOrderLines, "OrderLine");
				for(Element eleOrderLine : arrListOrderLine){
					String strOrderLineKey = eleOrderLine.getAttribute("OrderLineKey");
					//When view store pop up opens from Add Item Page, we will get values from map using ItemId as a Key.
					if("".equals(strOrderLineKey)){
						Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
						String strItemId = eleItem.getAttribute("ItemID");
						if(!YFCCommon.isVoid(mapLineKeyItem.get(strItemId)))
						{
							eleOrderLine.setAttribute("OrderLineKey",(String)mapLineKeyItem.get(strItemId));
						}
						mapLineDetail = mapLineAvailability.get(strItemId);						
					}else {
						mapLineDetail = mapLineAvailability.get(strOrderLineKey);
					}
					Element eleAvailabilityList = SCXmlUtil.getChildElement(eleOrderLine, "AvailabilityList");
					ArrayList<Element> arrListAvailability = SCXmlUtil.getChildren(eleAvailabilityList, "Availability");
					for(Element eleAvailability : arrListAvailability){
						String strShipNode = eleAvailability.getAttribute("ShipNode");
						List<Object> listQty  = mapLineDetail.get(strShipNode);
						if(null != listQty){
							double dProcuredQty = (double)listQty.get(2);
							if(dProcuredQty == 0){
								eleAvailability.setAttribute("IsAvailableOnStore","Y");
							}
							eleAvailability.setAttribute("OnHandQuantity", String.valueOf(listQty.get(0)));
							eleAvailability.setAttribute("OnStoreAvaialbleDate", String.valueOf(listQty.get(1)));
							eleAvailability.setAttribute("ProcuredQty", String.valueOf(listQty.get(2)));
							eleAvailability.setAttribute("FutureAvaialbleDate", String.valueOf(listQty.get(3)));
						}						
					}					
				}
				
			}			
		}

		SCXmlUtil.importElement(elemOutput, outEl);
		if (!SCUtil.isVoid(SCXmlUtil.getLastChildElement(SCXmlUtil.getChildElement(outEl, "NodeList", true)))) {
			Element elemLastNode = (Element)SCXmlUtil.getLastChildElement(SCXmlUtil.getChildElement(outEl, "NodeList", true)).cloneNode(true);
			SCXmlUtil.importElement(elemLastRecord, elemLastNode);
		}
		return elemPage;
	}


	/**
	 * This method will prepare the input of additional called getFulfillmentOptionForLines API.
	 *   
	 * ********** INPUT ****************
	 * <Promise IgnoreUnpromised="" IsPartialSubstitutionAllowed="" IsSubstitutionAllowed="" OrganizationCode="" FulfillmentType="">
	 *     <EvaluateOrder EnterpriseCode="" OrderHeaderKey=""/>
	 *     <EvaluateOptions>
	 *         <EvaluateOption DeliveryMethod="" FulfillmentType="" Node="" OptionNo=""/>
	 *     </EvaluateOptions>
	 *     <PromiseLines>
	 *         <PromiseLine>
	 *             <OrderLine OrderLineKey="" RequiredQty=""/>
	 *         </PromiseLine>
	 *     </PromiseLines>
	 * </Promise> 
	 * 
	 * @param uiContext
	 * @param inputEl
	 * @param elemGSNOutput
	 * 
	 * 
	 * @return input element
	 */
	private Element getPromiseInputForFOFL(SCUIContext uiContext, Element inputEl, Element elemGSNOutput){

		String strOrgCode = inputEl.getAttribute("OrganizationCode");
		Element eleOrder = SCXmlUtil.getChildElement(inputEl, "Order");

		Element elePromise = SCXmlUtil.createDocument("Promise").getDocumentElement();
		elePromise.setAttribute("IgnoreUnpromised", "Y");
		elePromise.setAttribute("IsPartialSubstitutionAllowed", "N");
		elePromise.setAttribute("IsSubstitutionAllowed", "Y");
		elePromise.setAttribute("OrganizationCode", strOrgCode);
		elePromise.setAttribute("FulfillmentType", "CUTOVERSTORES");

		Element eleEvaluateOrder = SCXmlUtil.createChild(elePromise, "EvaluateOrder");
		eleEvaluateOrder.setAttribute("EnterpriseCode", strOrgCode);
		eleEvaluateOrder.setAttribute("OrderHeaderKey", eleOrder.getAttribute("OrderHeaderKey"));

		Element elePromiseLines = SCXmlUtil.createChild(elePromise, "PromiseLines");
		Element eleOrderLines = SCXmlUtil.getChildElement(inputEl, "OrderLines");
		ArrayList<Element> arraListOrderLine = SCXmlUtil.getChildren(eleOrderLines, "OrderLine");
		for(Element eleOrderLine : arraListOrderLine){
			Element elePromiseLine = SCXmlUtil.createChild(elePromiseLines, "PromiseLine");
			Element elePromiseOrderLine = SCXmlUtil.createChild(elePromiseLine, "OrderLine");
			String strOrderLineKey = eleOrderLine.getAttribute("OrderLineKey");
			if("".equals(strOrderLineKey)){
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
				elePromiseLine.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
				elePromiseLine.setAttribute("ProductClass", "GOOD");
				elePromiseLine.setAttribute("UnitOfMeasure", "EACH");
			}else{
				elePromiseOrderLine.setAttribute("OrderLineKey", eleOrderLine.getAttribute("OrderLineKey"));	
			}			
			if("".equals(eleOrderLine.getAttribute("OrderedQty"))){
				elePromiseLine.setAttribute("RequiredQty", eleOrderLine.getAttribute("RequiredQty"));
			}else{
				elePromiseOrderLine.setAttribute("RequiredQty", eleOrderLine.getAttribute("OrderedQty"));
			}
		}

		return elePromise;
	}

	/**
	 * prepareInputForGASA OOB method, we have add the additional Mash Up call of getFulfillmentOptionForLines and save the output of this
	 * API at Context.
	 * 
	 * @param elemGSNOutput
	 * @param inputEl
	 * @param pageSize
	 * @param pageNum
	 * @param uiContext
	 */
	private void prepareInputForGASA(Element elemGSNOutput, Element inputEl, int pageSize, int pageNum, SCUIContext uiContext) {
		int end = pageNum * pageSize;
		int start = end - pageSize + 1;

		int i = 1;
		Element elemGSNOutputNodeList = SCXmlUtil.getChildElement(elemGSNOutput, "NodeList", true);
		List<Element> nodes = SCXmlUtil.getChildren(elemGSNOutputNodeList, "Node");

		boolean isFirstPage = start == 1;
		boolean isLastPage = (nodes.size() >= start) && (nodes.size() <= end);

		SCCSMashupUtils.addAttributeToUIContext(uiContext, "isFirstPage", this, new Boolean(isFirstPage));
		SCCSMashupUtils.addAttributeToUIContext(uiContext, "isLastPage", this, new Boolean(isLastPage));
		Element elemNodeListInput;
		if (nodes.size() > 0) {
			elemNodeListInput = SCXmlUtil.createChild(inputEl, "NodeList");
			// Added code for getFulfillmentOptionForLines call to get the Inventory Availability details.
			Element elePromise = getPromiseInputForFOFL(uiContext, inputEl, elemGSNOutputNodeList);
			Element eleEvaluateOptions = SCXmlUtil.createChild(elePromise, "EvaluateOptions");

			for (Element elemNode : nodes) {
				if ((i >= start) && (i <= end)) {
					// Added for adding EvaluateOption for all eligible nodes in getFulfillmentOptionForLines
					Element eleEvaluateOption = SCXmlUtil.createChild(eleEvaluateOptions, "EvaluateOption");
					eleEvaluateOption.setAttribute("DeliveryMethod", "PICK");
					eleEvaluateOption.setAttribute("FulfillmentType", "CUTOVERSTORES");
					eleEvaluateOption.setAttribute("Node", elemNode.getAttribute("ShipNode"));
					eleEvaluateOption.setAttribute("OptionNo", (i-1)+"");

					SCXmlUtil.importElement(elemNodeListInput, elemNode);
				}
				if ((i >= end) || 
						(i > end)) {
					isLastPage = false;
				}
				i++;
			}
			Element eleGetFulfillmentOptForLnOutput = (Element)SCUIMashupHelper.invokeMashup("extn_getFulfillmentOptionForLines", elePromise, uiContext);
			SCCSMashupUtils.addAttributeToUIContext(uiContext, "getFulfillmentOptionForLinesOutput", this, eleGetFulfillmentOptForLnOutput);
		}
	}

	/**
	 * @param inputEl
	 * @return
	 */
	private Element getInputForSurroundingNodeList(Element inputEl) {
		Element elemNodeSearch = SCXmlUtil.getChildElement(inputEl, "NodeSearch");
		Element elemGSN = SCXmlUtil.createDocument("GetSurroundingNodeList").getDocumentElement();
		SCXmlUtil.mergeElement(elemNodeSearch, elemGSN, true);
		elemGSN.setAttribute("OrganizationCode", inputEl.getAttribute("OrganizationCode"));
		return elemGSN;
	}

	/**
	 * @param uiContext
	 * @return
	 */
	private int getPageNumber(SCUIContext uiContext) {
		Element elem = SCUIJSONUtils.getXmlFromJSON(uiContext.getRequest().getParameter("scControllerData")).getDocumentElement();
		Element elemMashupRefs = SCXmlUtil.getChildElement(elem, "MashupRefs");
		for (Element elemMashupRef : SCXmlUtil.getChildren(elemMashupRefs, "MashupRef")) {
			if (!SCUtil.isVoid(elemMashupRef.getAttribute("scPageNumber")))
				return SCXmlUtil.getIntAttribute(elemMashupRef, "scPageNumber");
		}
		return 1;
	}

	/**
	 * @param uiContext
	 * @return
	 */
	private int getPageSize(SCUIContext uiContext) {
		Element elem = SCUIJSONUtils.getXmlFromJSON(uiContext.getRequest().getParameter("scControllerData")).getDocumentElement();
		Element elemMashupRefs = SCXmlUtil.getChildElement(elem, "MashupRefs");
		for (Element elemMashupRef : SCXmlUtil.getChildren(elemMashupRefs, "MashupRef")) {
			if (!SCUtil.isVoid(elemMashupRef.getAttribute("scPageSize")))
				return SCXmlUtil.getIntAttribute(elemMashupRef, "scPageSize");
		}
		return 10;
	}

	private Double getTwoDecimalStringQty(double dQty){
		double dQuantity = Math.floor(dQty * 100) / 100;
		return dQuantity;
	}
}
