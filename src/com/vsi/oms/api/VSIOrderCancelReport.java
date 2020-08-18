package com.vsi.oms.api;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIOrderCancelReport implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIOrderCancelReport.class);
	
	public Document vsiOrderCancelReport(YFSEnvironment env, Document inXML) throws YFSException {
		log.beginTimer("VSIOrderCancelReport.vsiOrderCancelReport() : START");
		if(log.isDebugEnabled()){
			log.debug("Input message:\n" + SCXmlUtil.getString(inXML));
		}

		try {

			if (!YFCObject.isVoid(inXML)) {
				Element eleOrder = inXML.getDocumentElement();
				String strOrderHdrKey = null;
				String strBillToID = null;
				String strReasonCode = null;
				String strOrderLineKey = null;
				String strItemId = null;
				String strUnitPrice = null;
				String strCancelledQty = null;
				String strStatusDate = null;

				strOrderHdrKey = eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY);
				strBillToID = eleOrder.getAttribute(ATTR_BILL_TO_ID);
				Element eleOrdreAudit = SCXmlUtil.getXpathElement(eleOrder, "//Order/OrderAudit");
				if (!YFCObject.isVoid(eleOrdreAudit)) {
					strReasonCode = eleOrdreAudit.getAttribute(ATTR_REASON_CODE);
				}

				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, ELE_ORDER_LINES);
				ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);
				for (Element eleOrderLine : arrOrderLines) {
					strOrderLineKey = eleOrderLine.getAttribute(ATTR_ORDER_LINE_KEY);
					Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM);
					strItemId = eleItem.getAttribute(ATTR_ITEM_ID);
					Element eleLineOverallTotals = SCXmlUtil.getChildElement(eleOrderLine,
							ELE_LINE_OVERALL_TOTALS);
					strUnitPrice = eleLineOverallTotals.getAttribute(ATTR_UNIT_PRICE);
					Element eleStatusBreakupForCanceledQty = SCXmlUtil.getChildElement(eleOrderLine,
							ELE_STATUS_BREAKUP_FOR_CANCELED_QTY);
					Element eleCanceledFrom = SCXmlUtil.getChildElement(eleStatusBreakupForCanceledQty, ATTR_CANCELED_FROM);
					strCancelledQty = eleCanceledFrom.getAttribute(ATTR_QUANTITY);
					strStatusDate = VSIUtils.getCurrentDate(YYYY_MM_DD_T_HH_MM_SS);

					Document docMultiApi = SCXmlUtil.createDocument(ELE_MULTI_API);
					Element eleMultiApi = docMultiApi.getDocumentElement();
					Element eleAPI = SCXmlUtil.createChild(eleMultiApi, ELE_API);
					Element eleInput = SCXmlUtil.createChild(eleAPI, ELE_INPUT);
					eleAPI.setAttribute(ATTR_IS_EXTN_DB_API, FLAG_Y);
					eleAPI.setAttribute(ATTR_NAME, API_CREATE_VSI_ORDER_CANCEL_REPORT);
					Element eleInputOrderCancelReport = SCXmlUtil.createChild(eleInput, ELE_VSI_ORDER_CANCEL_REPORT);
					eleInputOrderCancelReport.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHdrKey);
					eleInputOrderCancelReport.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
					eleInputOrderCancelReport.setAttribute(ATTR_ITEM_ID, strItemId);
					eleInputOrderCancelReport.setAttribute(ATTR_CANCEL_REASON_CODE, strReasonCode);
					eleInputOrderCancelReport.setAttribute(ATTR_UNIT_PRICE, strUnitPrice);
					eleInputOrderCancelReport.setAttribute(ATTR_CANCELLED_QTY, strCancelledQty);
					eleInputOrderCancelReport.setAttribute(ATTR_CUSTOMER_ID, strBillToID);
					eleInputOrderCancelReport.setAttribute(ATTR_CANCELLED_DATE, strStatusDate);

					if(log.isDebugEnabled()){
						log.debug("Input to multiApi to create entry to VSI_ORDER_CANCEL_REPORT table:\n" + SCXmlUtil.getString(docMultiApi));
					}
					VSIUtils.invokeAPI(env, API_MULTI_API, docMultiApi);
				}
			}
			
		} catch (Exception e) {
			log.error("Error in VSIOrderCancelReport.vsiOrderCancelReport():\n" + e.getMessage());
			throw VSIUtils.getYFSException(e);
		}

		log.endTimer("VSIOrderCancelReport.vsiOrderCancelReport() : END");
		return inXML;
	}
}
