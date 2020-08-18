package com.vsi.oms.api;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.riskified.RiskifiedClient;
import com.riskified.RiskifiedError;
import com.riskified.models.RefundDetails;
import com.riskified.models.RefundOrder;
import com.riskified.models.Response;
import com.riskified.validations.FieldBadFormatException;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessRiskifiedPartialCancelAndReturn {
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessRiskifiedPartialCancelAndReturn.class);
	
	public void processRiskifiedPartialCancelAndReturn(YFSEnvironment env, Document inXML) {
		log.debug("processRiskifiedPartialCancelAndReturn input is : " + XMLUtil.getXMLString(inXML));
		try {
			Element eleOrder = inXML.getDocumentElement();
			String strDocumentType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_DOCUMENT_TYPE);	
			String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String strReasonText = null;
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
			List<Element> listOrderLine = new ArrayList<>();
			listOrderLine = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			
			// For DocumentType=0003, Return
			if (VSIConstants.DOC_TYPE_RETURN.equals(strDocumentType)) {
				
				//Changes for OMS-2216 -- Start
				Element eleOrderLinefirst = SCXmlUtil.getFirstChildElement(eleOrderLines);
				Element eleDerivedFromOrder = SCXmlUtil.getChildElement(eleOrderLinefirst, VSIConstants.ELE_DERIVED_FROM_ORDER);
				String strSalesOrderNo = eleDerivedFromOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
				//Changes for OMS-2216 -- End
				
				String strPartialReturn = VSIConstants.VSI_PARTIAL_RETURN;
				callRiskified(env, eleOrder, strReasonText, strSalesOrderNo, strOrderHeaderKey, listOrderLine,
						strPartialReturn);
				
			}
			// For Document Type=0001 Sales Order.
			else if (VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType)) {
				
				String strPartialCancel = VSIConstants.VSI_PARTIAL_CANCEL;
				callRiskified(env, eleOrder, strReasonText, strOrderNo, strOrderHeaderKey, listOrderLine,
						strPartialCancel);				
			}			
		}catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param env
	 * @param eleOrder
	 * @param strReasonText
	 * @param strOrderNo
	 * @param strOrderHeaderKey
	 * @param listOrderLine
	 * @throws NumberFormatException
	 * @throws RiskifiedError
	 * @throws IOException
	 * @throws FieldBadFormatException
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws TransformerException
	 * 
	 * This method makes a call to Riskified
	 * system.
	 */
	private void callRiskified(YFSEnvironment env, Element eleOrder, String strReasonText, String strOrderNo,
			String strOrderHeaderKey, List<Element> listOrderLine, String strPartialReturnOrCancel)
			throws NumberFormatException, RiskifiedError, IOException, FieldBadFormatException,
			ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException, TransformerException {
		
		List<RefundDetails> refundDetails = new ArrayList<RefundDetails>();
		RefundOrder refund = new RefundOrder();
		refund.setId(strOrderNo);
		for (Element eleOrderLine : listOrderLine) {
			// OMS-2151 Start
			String strLineAmount=null;
			if(VSIConstants.VSI_PARTIAL_CANCEL.equals(strPartialReturnOrCancel)){
				Element eleLnPriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE);
				String strChngInLnTtlNeg = eleLnPriceInfo.getAttribute("ChangeInLineTotal");
				if(!YFCCommon.isVoid(strChngInLnTtlNeg)){
					Double dChngInLnTtlNeg = Double.parseDouble(strChngInLnTtlNeg);
					Double dChngInLnTtl = Math.abs(dChngInLnTtlNeg);
					strLineAmount = Double.toString(dChngInLnTtl);
				}				
			} else if (VSIConstants.VSI_PARTIAL_RETURN.equals(strPartialReturnOrCancel)){
				Element eleLnPriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE);
				strLineAmount = eleLnPriceInfo.getAttribute(VSIConstants.ATTR_LINE_TOTAL);				
			}
			// OMS-2151 End
			RefundDetails refundDetail = new RefundDetails();
			if (!YFCObject.isVoid(strLineAmount)) {
				Double dTotalAmount = Double.parseDouble(strLineAmount);
				refundDetail.setAmount(dTotalAmount);
				}
			String strSeqNumber=null;
			try {
				strSeqNumber = VSIDBUtil.getNextSequence(env, VSIConstants.VSI_SEQ_PRTL_CNCLORRTN);
			} catch (Exception e) {
				e.printStackTrace();
			}
				refundDetail.setRefundId(strSeqNumber);
				refundDetail.setCurrency(VSIConstants.ATTR_CURRENCY);
				refundDetail.setRefundedAt(new Date());
				
				String strReturnReasonOp = SCXmlUtil.getAttribute(eleOrderLine, VSIConstants.ATTR_RETURN_REASON);
				// OMS-2200 Start
				if(!YFCCommon.isVoid(strReturnReasonOp)){
					// Call getCommonCodeList API
					Document getCCListIp = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
					Element eleCCListIp = getCCListIp.getDocumentElement();
					eleCCListIp.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODE_TYPE_RETURN_REASON);
					eleCCListIp.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ENT_VSI);
					eleCCListIp.setAttribute(VSIConstants.ATTR_CODE_VALUE, strReturnReasonOp);
					log.debug("callRiskified: getCommonCodeList api input is : " + XMLUtil.getXMLString(getCCListIp));
					Document getCCListOp = VSIUtils.invokeAPI(env,
							"global/template/api/getCommonCodeList.xml", VSIConstants.API_COMMON_CODE_LIST,
							getCCListIp);
					log.debug("callRiskified: getCommonCodeList api output is : " + XMLUtil.getXMLString(getCCListOp));
					Element eleCommonCode = XMLUtil.getElementByXPath(getCCListOp, "CommonCodeList/CommonCode");
					if(!YFCCommon.isVoid(eleCommonCode)){
						strReasonText = eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					}
					refundDetail.setReason(strReasonText);
				} else {
					Element eleOrderAudit = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_AUDIT);
					strReasonText = SCXmlUtil.getAttribute(eleOrderAudit, VSIConstants.ATTR_REASON_TEXT);
					refundDetail.setReason(strReasonText);
				}
				// OMS-2200 End
			
			refundDetails.add(refundDetail);
			refund.setRefunds(refundDetails);
		}
		String strRefund = com.riskified.JSONFormater.toJson(refund);
		log.debug("Riskified Request: " + strRefund);
		storeRequestandResponse(env, strOrderHeaderKey, strRefund, strPartialReturnOrCancel);
		RiskifiedClient client = new RiskifiedClient();
		Response resRefund = client.refundOrder(refund);
		String strResRefund = com.riskified.JSONFormater.toJson(resRefund);
		log.debug("Riskified Response" + strResRefund);
	}

	/**
	 * @param env
	 * @param ohk
	 * @param request
	 * @param chargeType
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 * 
	 * This method store the request sent to
	 * Riskified.
	 */
	public void storeRequestandResponse(YFSEnvironment env, String ohk, String request, String chargeType)
			throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		Document doc = XMLUtil.createDocument(VSIConstants.ELE_PAYMENT_RECORDS);
		Element elePayRecrds = doc.getDocumentElement();
		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		elePayRecrds.setAttribute(VSIConstants.ATTR_RECORD, request);
		elePayRecrds.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, chargeType);
		YIFApi api;
		api = YIFClientFactory.getInstance().getApi();
		api.executeFlow(env, VSIConstants.API_VSI_PAYEMENT_RECORDS, doc);
	}
}
