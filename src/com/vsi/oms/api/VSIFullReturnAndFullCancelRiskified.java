package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIFullReturnAndFullCancelRiskified {
	private YFCLogCategory log = YFCLogCategory.instance(VSIFullReturnAndFullCancelRiskified.class);
	YIFApi api;

	/**
	 * @param env
	 * @param inXML
	 * 
	 * This class is invoked on ChangeOrder.OnCancel Event for full
	 * Order Cancel or For full Order Return CreateReturn.OnSuccess
	 * event.
	 */
	public void fullReturnAndFullCancelRequest(YFSEnvironment env, Document inXML) {
		log.debug("fullReturnAndFullCancelRequest input is : " + XMLUtil.getXMLString(inXML));
		try {
			Element eleOrder = inXML.getDocumentElement();
			String strDraftOrderFlag = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_DRAFT_ORDER_FLAG);
			String strDocumentType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_DOCUMENT_TYPE);
			if((VSIConstants.RETURN_DOCUMENT_TYPE.equals(strDocumentType)) || (VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType) && VSIConstants.FLAG_N.equals(strDraftOrderFlag))){
				String strEntryType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ENTRY_TYPE);
				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
				Element eleOrderLinefirst = SCXmlUtil.getFirstChildElement(eleOrderLines);
				
				String strLineType = SCXmlUtil.getAttribute(eleOrderLinefirst, VSIConstants.ATTR_LINE_TYPE);
				// strEntryType not POS, Marketplace and wholesale order.
				if (!YFCObject.isVoid(strEntryType) && !VSIConstants.POS_ENTRY_TYPE.equalsIgnoreCase(strEntryType)
						&& !VSIConstants.MARKETPLACE.equalsIgnoreCase(strEntryType)
						&& !VSIConstants.WHOLESALE.equalsIgnoreCase(strEntryType)) {
					
					String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					String strReasonText = null;
					
					boolean isRiskifiedCallRequired = true;
					boolean isBOPUSOrSTS = false;
					
					// Check CommonCode for toggle ON Riskified
					ArrayList<Element> listFullReturnAndCancelFlag = VSIUtils.getCommonCodeList(env,
							VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.FULL_RETURN_AND_CANCEL,
							VSIConstants.ATTR_DEFAULT);
					if (!listFullReturnAndCancelFlag.isEmpty()) {
						Element eleCommonCode = listFullReturnAndCancelFlag.get(0);
						String strFullReturnAndCancelFlag = eleCommonCode
								.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if (!YFCCommon.isStringVoid(strFullReturnAndCancelFlag)
								&& VSIConstants.FLAG_N.equalsIgnoreCase(strFullReturnAndCancelFlag)) {
							isRiskifiedCallRequired = false;
						}
					} else {
						isRiskifiedCallRequired = false;
					}
	
					// For DocumentType=0003, Return Order
					if (VSIConstants.DOC_TYPE_RETURN.equals(strDocumentType) && isRiskifiedCallRequired) {
						
						//Call the service to post the input XML to queue for preparing Riskified Return Request
						VSIUtils.invokeService(env, "VSIPostRiskifiedReturnRequest", inXML);
					}
					// For Document Type=0001 Sales Order.
					else if (VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType) && isRiskifiedCallRequired) {
						Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
								strOrderHeaderKey);
						Document getOrderListOp = VSIUtils.invokeAPI(env,
								"global/template/api/VSIGetOrderListSO_Riskified.xml", VSIConstants.API_GET_ORDER_LIST,
								getOrderListIp);
						Element eleOrderListOp = getOrderListOp.getDocumentElement();
						Element eleOrderOp = SCXmlUtil.getChildElement(eleOrderListOp, VSIConstants.ELE_ORDER);
						
							if (VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType)
									|| VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)) {
								isRiskifiedCallRequired = true;
								isBOPUSOrSTS = true;
							}
							if (!isBOPUSOrSTS) {
	
								Element eleOrderHoldTypes = SCXmlUtil.getChildElement(eleOrderOp,
										VSIConstants.ELE_ORDER_HOLD_TYPES);
								List<Element> listOrderHoldType = SCXmlUtil.getElements(eleOrderHoldTypes,
										VSIConstants.ELE_ORDER_HOLD_TYPE);
								for (Element eleOrderHoldType : listOrderHoldType) {
									String strHoldType = SCXmlUtil.getAttribute(eleOrderHoldType,
											VSIConstants.ATTR_HOLD_TYPE);
									// Kount hold and fraud hold check except LineType Bopus and STS orders
									if ((VSIConstants.HOLD_FRAUD_HOLD.equalsIgnoreCase(strHoldType)
											|| VSIConstants.VSI_KOUNT_STH_HOLD.equalsIgnoreCase(strHoldType))) {
										isRiskifiedCallRequired = true;
									}else {
										isRiskifiedCallRequired = false;
									}
								}
							}
									Element eleOrderAudit = SCXmlUtil.getChildElement(eleOrder,
											VSIConstants.ELE_ORDER_AUDIT);
									strReasonText = SCXmlUtil.getAttribute(eleOrderAudit, VSIConstants.ATTR_REASON_TEXT);
									// Order cancelled due to fraud declined
									if (VSIConstants.VSI_FRAUD_CHECK_DECLINED.equalsIgnoreCase(strReasonText)) {
										isRiskifiedCallRequired = false;
								}
							if (isRiskifiedCallRequired) {
								
								//Call the service to post the input XML to queue for preparing Riskified Cancel Request
								VSIUtils.invokeService(env, "VSIPostRiskifiedCancelRequest", inXML);	
							}	
					}
				}
			} 	
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}