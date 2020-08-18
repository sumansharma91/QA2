package com.vsi.oms.api.order;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleCancelOrderEmails implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleCancelOrderEmails.class);

	/**
	 * This custom implementation is used to send a cancellation email's to
	 * customer care representatives during schedule, release, change order &
	 * change release.
	 * 
	 * @param env
	 * @param inXML
	 * @throws Exception
	 */
	public Document cancelOrderEmails(YFSEnvironment env, Document inXML) throws Exception {

		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIWholesaleCancelOrderEmails================================");
		}

		if (!YFCObject.isVoid(inXML)) {
			Element eleOrder = inXML.getDocumentElement();
			String strOrderHdrKey = "";
			String strReasonCode = "";
			String strOrderNo = "";
			String strEnterpriseCode = "";
			StringBuffer strCancelItemDescription = new StringBuffer();
			Document cancelItemDescription = null;

			strOrderHdrKey = eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY);
			strOrderNo = eleOrder.getAttribute(ATTR_ORDER_NO);
			strEnterpriseCode = eleOrder.getAttribute(ATTR_ENTERPRISE_CODE);
			Element eleOrdreAudit = SCXmlUtil.getXpathElement(eleOrder, "//Order/OrderAudit");
			if (!YFCObject.isVoid(eleOrdreAudit)) {
				strReasonCode = eleOrdreAudit.getAttribute(ATTR_REASON_CODE);
			}

			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES);
			ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);
			for (Element eleOrderLine : arrOrderLines) {
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM);
				Element eleStatusBreakupForCanceledQty = SCXmlUtil.getChildElement(eleOrderLine,
						ELE_STATUS_BREAKUP_FOR_CANCELED_QTY);
				Element eleCanceledFrom = SCXmlUtil.getChildElement(eleStatusBreakupForCanceledQty, ATTR_CANCELED_FROM);

				strCancelItemDescription.append("Line No: " + eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO)
				+ " - SKU ID: " + eleItem.getAttribute(ATTR_ITEM_ID) + " - Cancelled Quantity: "
				+ eleCanceledFrom.getAttribute(ATTR_QUANTITY) + "\n");
				if(YFCObject.isVoid(cancelItemDescription)){

					cancelItemDescription = SCXmlUtil.createDocument(ELE_ORDER_LINES);
				}

				Element eleCancelOrderLine = SCXmlUtil.createChild(cancelItemDescription.getDocumentElement(), ELE_ORDER_LINE);
				SCXmlUtil.setAttribute(eleCancelOrderLine, ATTR_PRIME_LINE_NO, eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO));
				SCXmlUtil.setAttribute(eleCancelOrderLine, ATTR_ITEM_ID, eleItem.getAttribute(ATTR_ITEM_ID));
				SCXmlUtil.setAttribute(eleCancelOrderLine, ATTR_CANCELLED_QTY, eleCanceledFrom.getAttribute(ATTR_QUANTITY));
			}

			Document cancelOrderEmailInXML = SCXmlUtil.createDocument(ELE_ORDER);
			Element orderEle = cancelOrderEmailInXML.getDocumentElement();
			SCXmlUtil.setAttribute(orderEle, ATTR_ORDER_HEADER_KEY, strOrderHdrKey);
			SCXmlUtil.setAttribute(orderEle, ATTR_ORDER_NO, strOrderNo);
			SCXmlUtil.setAttribute(orderEle, ATTR_ENTERPRISE_CODE, strEnterpriseCode);
			SCXmlUtil.setAttribute(orderEle, ATTR_DETAIL_DESCRIPTION, strCancelItemDescription.toString());

			if(!YFCObject.isVoid(cancelItemDescription)){

				SCXmlUtil.importElement(cancelOrderEmailInXML.getDocumentElement(), cancelItemDescription.getDocumentElement());
			}


			Boolean emailSent = false;
			for(int i = 0; i < 3; i++){

				try{
					VSIUtils.invokeService(env, "VSIWholesaleOrderCancelOrderEmail", cancelOrderEmailInXML);
					emailSent = true;
				}catch(Exception e){
					log.error("Exception in validateWholesaleOrderHolds.createCreditDiscrepancyAlert() : " + e.getMessage());
					e.printStackTrace();
				}			
				if(emailSent){
					break;
				}
			}
		}
		
		return inXML;
	}
}