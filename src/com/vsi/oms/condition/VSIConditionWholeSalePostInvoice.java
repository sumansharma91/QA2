package com.vsi.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIConditionWholeSalePostInvoice implements YCPDynamicConditionEx, VSIConstants {
	private YFCLogCategory log = YFCLogCategory.instance(VSIConditionWholeSalePostInvoice.class);

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName, @SuppressWarnings("rawtypes") Map mapData,
			Document inXML) {

		if (log.isDebugEnabled()) {
			log.debug("Printing Input XML for VSIConditionWholeSalePostInvoice Condition:" + XmlUtils.getString(inXML));
		}
		

		Document getOrderInvoiceListOut = null;
		Document getOrderInvoiceListIn = null;
		Document docGetOrderListInput = null;
		//boolean bIsWholeSaleOrderInvoiced = false;
		

		try {

			getOrderInvoiceListIn = SCXmlUtil.createDocument(ELE_ORDER_INVOICE);
			String orderHeaderKey = inXML.getDocumentElement().getAttribute(ATTR_ORDER_HEADER_KEY);

			if (!YFCCommon.isVoid(orderHeaderKey)) {

				docGetOrderListInput = XMLUtil.createDocument(ELE_ORDER);
				Element eleOrder = docGetOrderListInput.getDocumentElement();
				eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);

				// Calling getOrderlist API to get the orderType
				Document docGetOrderListOutput = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST,
						docGetOrderListInput);

				if (log.isDebugEnabled()) {
					log.debug("Get Order List Output for Class VSIConditionWholeSalePostInvoice:"
							+ XmlUtils.getString(docGetOrderListOutput));
				}

				if (!YFCCommon.isVoid(docGetOrderListOutput)) {
					Element eleOutputOrder = (Element) (XMLUtil
							.getElementsByTagName(docGetOrderListOutput.getDocumentElement(), ELE_ORDER).get(0));
					String strOrderType = eleOutputOrder.getAttribute(ATTR_ORDER_TYPE);

					if(log.isDebugEnabled()){
						log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: OrderType from getOrderList is " + strOrderType);
					}
					if (!YFCCommon.isVoid(strOrderType) && !strOrderType.equalsIgnoreCase(WHOLESALE)) {
						
						if (log.isDebugEnabled()) {
							log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: false");
						}
						return false;
					}else{
						/*bIsWholeSaleOrderInvoiced = true;
						
						log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: bIsWholeSaleOrderInvoiced Value is:"
								+ bIsWholeSaleOrderInvoiced);*/
						
						//
						getOrderInvoiceListIn.getDocumentElement().setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
						getOrderInvoiceListOut = VSIUtils.invokeService(env, SERVICE_GET_ORDER_INVOICE_LIST,
								getOrderInvoiceListIn);

						if (log.isDebugEnabled()) {
							log.debug("Get Order Invoice List Output for Class VSIConditionWholeSalePostInvoice:"
									+ XmlUtils.getString(getOrderInvoiceListOut));
						}

						if (getOrderInvoiceListOut.getDocumentElement().hasChildNodes()) {
							
							if (log.isDebugEnabled()) {
								log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: true");
							}
							return true;
							/*bIsWholeSaleOrderInvoiced = true;
				
								log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: bIsWholeSaleOrderPreInvoiced Value is:"
										+ true);*/
							
						}

					}
				}

			}
		}

		catch (Exception e) {

			e.printStackTrace();
			throw new YFSException();

		}
		
		if (log.isDebugEnabled()) {
			log.debug("VSIConditionWholeSalePostInvoice.evaluateCondition: false");
		}
		return false;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
