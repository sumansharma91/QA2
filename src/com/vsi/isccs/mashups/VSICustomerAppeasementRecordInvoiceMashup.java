package com.vsi.isccs.mashups;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * @author IBM
 *
 */
public class VSICustomerAppeasementRecordInvoiceMashup extends SCCSBaseMashup {

	private static YFCLogCategory cat = YFCLogCategory.instance(VSICustomerAppeasementRecordInvoiceMashup.class.getName());

	/* (non-Javadoc)
	 * @see com.ibm.isccs.common.mashups.SCCSBaseMashup#massageInput(org.w3c.dom.Element, com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData, 
	 * com.sterlingcommerce.ui.web.framework.context.SCUIContext)
	 */
	public Element massageInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext){ 
		if (!SCUtil.isVoid(inputEl)) {

			if (cat.isDebugEnabled()){
				cat.debug("VSICustomerAppeasementRecordInvoiceMashup : input to recrod invoice creation is: " + SCXmlUtil.getString(inputEl));
			}

			Element orderElem = SCXmlUtil.getChildElement(inputEl,VSIConstants.ELE_ORDER);
			Element selectedAppeasementOffer = SCXmlUtil.getChildElement(inputEl,VSIConstants.ELE_APPEASMENT_OFFER);
			Document orderInvDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_INVOICE);

			if ((!SCUtil.isVoid(orderElem)) && (!SCUtil.isVoid(selectedAppeasementOffer))) {
				//setOfferAmount(selectedAppeasementOffer, inputEl);

				//checkOffers(selectedAppeasementOffer);

				Element orderInvElem = orderInvDoc.getDocumentElement();

				Element eleAppeasementReason = SCXmlUtil.getChildElement(orderElem,VSIConstants.ELE_APPEASEMENT_REASON);
				String strReasonCode = eleAppeasementReason.getAttribute(VSIConstants.ATTR_REASON_CODE);
				String strOfferAmount = inputEl.getAttribute(VSIConstants.ATTR_OFFER_AMOUNT);
						
				orderInvElem.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				orderInvElem.setAttribute(VSIConstants.ATTR_ORDER_NO, orderElem.getAttribute(VSIConstants.ATTR_ORDER_NO));
				
				orderInvElem.setAttribute(VSIConstants.ATTR_OFFER_AMOUNT, strOfferAmount);
				orderInvElem.setAttribute(VSIConstants.ATTR_INVOICE_CREATION_REASON, strReasonCode);
				orderInvElem.setAttribute(VSIConstants.ATTR_INVOICE_TYPE,VSIConstants.STR_INFO);
				orderInvElem.setAttribute(VSIConstants.ATTR_USE_ORDER_LINE_CHARGES,VSIConstants.FLAG_N);
				try{
					// Added Code for 1B - START
					if(VSIConstants.STR_APPEASEMENT_REASON_SHIPPING_APPEASEMENT.equals(strReasonCode)){
						Element eleHeaderChargeList = computeHeaderChargeListElement(strOfferAmount,strReasonCode);
						SCXmlUtil.importElement(orderInvElem, eleHeaderChargeList);
					} else {
						Element lineDetailsElement = computeLineDetailsElement(selectedAppeasementOffer, strReasonCode);
						SCXmlUtil.importElement(orderInvElem, lineDetailsElement);
					}
				}catch(Exception e){
					throw e;
				}
			}
			
			if (cat.isDebugEnabled())
				cat.debug("Final input to recordOrderInv is:" + SCXmlUtil.getString(orderInvDoc));
			return orderInvDoc.getDocumentElement();
		}
		return inputEl; 
	}

	/**
	 * @param selectedAppeasementOffer
	 * @return
	 */
	private Element computeLineDetailsElement(Element selectedAppeasementOffer, String strReasonCode)
	{
		Element elemLineDetails = SCXmlUtil.createDocument(VSIConstants.ELE_LINE_DETAILS).getDocumentElement();
		Element elemOrderLines = SCXmlUtil.getChildElement(selectedAppeasementOffer, VSIConstants.ELE_ORDER_LINES);
		Iterator<Element> orderLineIterator = SCXmlUtil.getChildren(elemOrderLines);
		if (orderLineIterator != null) {
			while (orderLineIterator.hasNext()) {
				Element orderLineElement = (Element)orderLineIterator.next();
				Element elemLineDetail = SCXmlUtil.createChild(elemLineDetails, VSIConstants.ELE_LINE_DETAIL);
				elemLineDetail.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
				elemLineDetail.setAttribute(VSIConstants.ATTR_QUANTITY, orderLineElement.getAttribute(VSIConstants.ATTR_ORD_QTY));
				if(VSIConstants.STR_APPEASEMENT_REASON_TAX_ISSUE.equals(strReasonCode)){
					Element elemLineChargeList = SCXmlUtil.createChild(elemLineDetail,VSIConstants.ELE_LINE_TAX_LIST);
					Element elemLineCharge = SCXmlUtil.createChild(elemLineChargeList,VSIConstants.ELE_LINE_TAX);
					elemLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, orderLineElement.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY));
					elemLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME, orderLineElement.getAttribute(VSIConstants.ATTR_CHARGE_NAME));
					elemLineCharge.setAttribute(VSIConstants.ATTR_TAX_NAME,VSIConstants.STR_SALES_TAX);
					elemLineCharge.setAttribute(VSIConstants.ATTR_TAX, orderLineElement.getAttribute(VSIConstants.ATTR_LINE_OFFER_AMOUNT));
				} else {
					Element elemLineChargeList = SCXmlUtil.createChild(elemLineDetail,VSIConstants.ELE_LINE_CHARGE_LIST);
					Element elemLineCharge = SCXmlUtil.createChild(elemLineChargeList,VSIConstants.ELE_LINE_CHARGE);
					elemLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY, orderLineElement.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY));
					elemLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_NAME, orderLineElement.getAttribute(VSIConstants.ATTR_CHARGE_NAME));
					elemLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE, orderLineElement.getAttribute(VSIConstants.ATTR_LINE_OFFER_AMOUNT));
				}
			}
		}
		return elemLineDetails;
	}

	/**
	 * @param selectedAppeasementOffer
	 * @return
	 */
	private Element computeHeaderChargeListElement(String strOfferAmount,String strReasonCode) {
		Element headerChargeListElement = SCXmlUtil.createDocument(VSIConstants.ELE_HEADER_CHARGE_LIST).getDocumentElement();
		Element headerChargeElement = SCXmlUtil.createChild(headerChargeListElement,VSIConstants.ELE_HEADER_CHARGE);

		headerChargeElement.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, strOfferAmount);
		headerChargeElement.setAttribute(VSIConstants.ATTR_CHARGE_CATEGORY,VSIConstants.STR_CHARGE_CAT_CUSTOMER_SATISFACTION);
		headerChargeElement.setAttribute(VSIConstants.ATTR_CHARGE_NAME,strReasonCode);
		return headerChargeListElement;		
	}

}
