/**
 * 
 */
package com.vsi.oms.api.order;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * VSIRaiseCallTagAlertAPI class will raise Call Tag alert for return order.
 * 
 * @author IBM
 *
 */
public class VSIRaiseCallTagAlertAPI {
    private YFCLogCategory log = YFCLogCategory.instance(VSIRaiseCallTagAlertAPI.class);
    YIFApi api;

    /**
     * raiseCallTagAlert() will raise Call Tag alert for return order, it will also add item information 
     * in DetailDescription for eligible line while raising the alert. 
     * 
     * @param env
     * @param inDoc
     * @return Document
     */
    public Document raiseCallTagAlert(YFSEnvironment env, Document inDoc) throws Exception{

   
	Element eleInput = inDoc.getDocumentElement();

	String strOrderNo = eleInput.getAttribute(VSIConstants.ATTR_ORDER_NO);
	String strOrderHeaderKey = eleInput.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
	String strDocumentType = eleInput.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
	String strEnterpriseCode = eleInput.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);

	String strDetailedDescription = getDetailedDescription(eleInput);

	Document inDocInbox = SCXmlUtil.createDocument(VSIConstants.ELE_INBOX);
	Element eleInbox = inDocInbox.getDocumentElement();

	eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
	eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
	eleInbox.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, strDocumentType);
	eleInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, strDetailedDescription);
	eleInbox.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG,VSIConstants.FLAG_Y);
	eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Call Tag Alert for return lines");
	eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,VSIConstants.STR_VSI_CALL_TAG);
	eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID,VSIConstants.STR_VSI_CALL_TAG_QUEUE);
	eleInbox.setAttribute(VSIConstants.ATTR_PRIORITY, "1");

	Element eleInboxOrder = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
	eleInboxOrder.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
	eleInboxOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
	eleInboxOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, strDocumentType);
	eleInboxOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEnterpriseCode);
	
	// raise the call tag alert
	VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, inDocInbox);
	
	return inDoc;
    }

    /**
     * getDetailedDescription() will return the item details
     * for call tag eligible lines.
     * 
     * @param eleInput
     * @return
     */
    private String getDetailedDescription(Element eleInput) {
	StringBuffer strDetailedDescription = new StringBuffer();
	Element eleOrderLines = SCXmlUtil.getChildElement(eleInput,VSIConstants.ELE_ORDER_LINES);
	ArrayList<Element> arrListOrderLine = SCXmlUtil.getChildren(eleOrderLines,VSIConstants.ELE_ORDER_LINE);
	int intOrderlineArraySize = arrListOrderLine.size();
	int intarryloopCounter = 1;
	for(Element eleOrderLine : arrListOrderLine){
	    
	    String strDetailedDesc = "";
	    double dOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine,VSIConstants.ATTR_ORD_QTY);

	    Element eleLineOverallTotals = SCXmlUtil.getChildElement(eleOrderLine,VSIConstants.ELE_LINE_OVERALL_TOTALS);
	    String strExtendedPrice = eleLineOverallTotals.getAttribute(VSIConstants.ATTR_EXTENDED_PRICE);

	    Element eleItemDetails = SCXmlUtil.getChildElement(eleOrderLine,VSIConstants.ELE_ITEM_DETAILS);
	    String strItemID = eleItemDetails.getAttribute(VSIConstants.ATTR_ITEM_ID);

	    Element eleItemPrimaryInfo = SCXmlUtil.getChildElement(eleItemDetails,VSIConstants.ELE_PRIMARY_INFORMATION);
	    double dUnitWeight = SCXmlUtil.getDoubleAttribute(eleItemPrimaryInfo,VSIConstants.ATTR_UNIT_WEIGHT);

	    String strUnitWeightUOM = eleItemPrimaryInfo.getAttribute(VSIConstants.ATTR_UNIT_WEIGHT_UOM);

	    String strTotalWeight = getTotalWeight(dOrderedQty, dUnitWeight);

	    strDetailedDesc = intarryloopCounter+") Item ID: "+strItemID +", Quantity: "+dOrderedQty +", Price: $"+strExtendedPrice+", Weight: "+strTotalWeight+strUnitWeightUOM;
	    strDetailedDescription.append(strDetailedDesc);
	    if(intarryloopCounter < intOrderlineArraySize){
		strDetailedDescription.append(System.lineSeparator());
		strDetailedDescription.append(System.lineSeparator());
	    }
	    intarryloopCounter++;
	}
	return strDetailedDescription.toString();
    }

    /**
     * getTotalWeight() method wit
     * 
     * @param dOrderedQty
     * @param dUnitWeight
     * @return
     */
    private String getTotalWeight(double dOrderedQty, double dUnitWeight) {
	DecimalFormat df = new DecimalFormat("#.##");	
	return df.format(dOrderedQty * dUnitWeight);
    }


}
