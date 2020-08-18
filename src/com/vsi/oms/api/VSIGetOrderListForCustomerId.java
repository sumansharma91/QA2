package com.vsi.oms.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.icu.util.Calendar;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
// this class is used for fetching orderList for given customerId
public class VSIGetOrderListForCustomerId {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetOrderListForCustomerId.class);

	public Document getOrderList(YFSEnvironment env, Document inXML) throws Exception{

		if(log.isDebugEnabled()){
			log.debug("Input XML getOrderList : "+ XMLUtil.getXMLString(inXML));
		}
		Element eleDoc = inXML.getDocumentElement();
		String strCustomerId = SCXmlUtil.getAttribute(eleDoc, VSIConstants.ATTR_CUSTOMER_ID);
		String strFromOrderDate = SCXmlUtil.getAttribute(eleDoc,VSIConstants.ATTR_FROM_ORDER_DATE);
		String strToOrderDate = SCXmlUtil.getAttribute(eleDoc, VSIConstants.ATTR_TO_ORDER_DATE);
		Document docGetOrderList = null;
		Element eleGetOrderListInDoc = null;
		String strRetentionDays=null;
		if(!YFCObject.isVoid(strCustomerId)){
			if(!YFCObject.isVoid(strFromOrderDate)&&!YFCObject.isVoid(strToOrderDate)){


				docGetOrderList = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				eleGetOrderListInDoc = docGetOrderList.getDocumentElement();
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_ORDER_DATE_QRY_TYPE, VSIConstants.ATTR_DATERANGE);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_FROM_ORDER_DATE, strFromOrderDate);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_TO_ORDER_DATE, strToOrderDate);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_CALL_ORG_CODE, strCustomerId);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, VSIConstants.DOCUMENT_TYPE);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG, VSIConstants.FLAG_N);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_QUERY_TIMEOUT, VSIConstants.TIME_OUT_60);
				eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_ENTRY_TYPE, VSIConstants.ENTRYTYPE_CC);

			}
			else{

				ArrayList<Element> listOrderDate;
				listOrderDate = VSIUtils.getCommonCodeList(env, "VSI_ORD_DETAILS", "DATE", "DEFAULT");
				if(!listOrderDate.isEmpty()){
					Element eleCommonCode=listOrderDate.get(0);
					strRetentionDays=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);

					if (!YFCObject.isVoid(strRetentionDays)) {
						int nRetentionDays = Integer.parseInt(strRetentionDays);

						/** *********Fetching 30 days back date from current date********* */
						DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
						Date date = new Date();
						log.debug(dateFormat.format(date)); // 20161116
						String currentdate = dateFormat.format(date);
						log.debug("currentdates" + currentdate);
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.DATE, -nRetentionDays);
						/** **Retention days************ */
						Date daysbackdate = cal.getTime();
						String strRequireddate = dateFormat.format(daysbackdate);
						log.debug("RequiredDate" + strRequireddate);

						/** ******************complex query****************8************ */

						docGetOrderList = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						eleGetOrderListInDoc = docGetOrderList.getDocumentElement();

						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_ORDER_DATE_QRY_TYPE, VSIConstants.GT_QRY_TYPE);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_ORDER_DATE, strRequireddate);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_CALL_ORG_CODE, strCustomerId);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, VSIConstants.DOCUMENT_TYPE);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG, VSIConstants.FLAG_N);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_QUERY_TIMEOUT, VSIConstants.TIME_OUT_60);
						eleGetOrderListInDoc.setAttribute(VSIConstants.ATTR_ENTRY_TYPE, VSIConstants.ENTRYTYPE_CC);

					}
				}
			}


			Element eleOrderBy=SCXmlUtil.createChild(eleGetOrderListInDoc, VSIConstants.ELE_ORDER_BY);
			Element eleAttribute=SCXmlUtil.createChild(eleOrderBy, VSIConstants.ELE_ATTRIBUTE);
			eleAttribute.setAttribute(VSIConstants.ATTR_DESC, VSIConstants.FLAG_Y);
			eleAttribute.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.ATTR_ORDER_DATE);

			Element eleComplexQuery = SCXmlUtil.createChild(eleGetOrderListInDoc, VSIConstants.ELE_COMPLEX_QUERY);
			eleComplexQuery.setAttribute(VSIConstants.ATTR_OPERATOR, VSIConstants.AND_QUERY);
			Element eleOrOperator = SCXmlUtil.createChild(eleComplexQuery, VSIConstants.OR_QUERY);
			Element eleExp0 =SCXmlUtil.createChild(eleOrOperator, VSIConstants.ELE_EXP);
			eleExp0.setAttribute(VSIConstants.ATTR_NAME,VSIConstants.ATTR_BILL_TO_ID);
			eleExp0.setAttribute(VSIConstants.ATTR_VALUE, strCustomerId);

		}

		else{

			eleDoc.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Blank customerID");
			eleDoc.setAttribute(VSIConstants.ATTR_ERROR_CODE, "InvalidInput");
			return inXML;
		}
		return docGetOrderList;

	}
}