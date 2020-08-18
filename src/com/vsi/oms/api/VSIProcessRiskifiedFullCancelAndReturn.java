package com.vsi.oms.api;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.riskified.RiskifiedClient;
import com.riskified.RiskifiedError;
import com.riskified.models.CancelOrder;
import com.riskified.models.Response;
import com.riskified.validations.FieldBadFormatException;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessRiskifiedFullCancelAndReturn {
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessRiskifiedFullCancelAndReturn.class);
	
	public void processRiskifiedFullCancelAndReturn(YFSEnvironment env, Document inXML) {
		log.debug("processRiskifiedFullCancelAndReturn input is : " + XMLUtil.getXMLString(inXML));
		try {
			Element eleOrder = inXML.getDocumentElement();
			String strDocumentType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_DOCUMENT_TYPE);
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
			Element eleOrderLinefirst = SCXmlUtil.getFirstChildElement(eleOrderLines);
			
			String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String strReasonText = null;			
			
			// For DocumentType=0003, Return Order
			if (VSIConstants.DOC_TYPE_RETURN.equals(strDocumentType)) {
				String strReturnReasonOp = SCXmlUtil.getAttribute(eleOrderLinefirst, VSIConstants.ATTR_RETURN_REASON);
				// Call getCommonCodeList API
				Document getCCListIp = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
				Element eleCCListIp = getCCListIp.getDocumentElement();
				eleCCListIp.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODE_TYPE_RETURN_REASON);
				eleCCListIp.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ENT_VSI);
				eleCCListIp.setAttribute(VSIConstants.ATTR_CODE_VALUE, strReturnReasonOp);
				log.debug("processRiskifiedFullCancelAndReturn: getCommonCodeList api input is : " + XMLUtil.getXMLString(getCCListIp));
				Document getCCListOp = VSIUtils.invokeAPI(env,
						"global/template/api/getCommonCodeList.xml", VSIConstants.API_COMMON_CODE_LIST,
						getCCListIp);
				log.debug("processRiskifiedFullCancelAndReturn: getCommonCodeList api output is : " + XMLUtil.getXMLString(getCCListOp));
				Element eleCommonCode = XMLUtil.getElementByXPath(getCCListOp, "CommonCodeList/CommonCode");
				if(!YFCCommon.isVoid(eleCommonCode)){
					strReasonText = eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				}
				
				//Changes for OMS-2216 -- Start
				Element eleDerivedFromOrder = SCXmlUtil.getChildElement(eleOrderLinefirst, VSIConstants.ELE_DERIVED_FROM_ORDER);
				String strSalesOrderNo = eleDerivedFromOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
				//Changes for OMS-2216 -- End
				
				String strFullReturnOrCancel = VSIConstants.VSI_FULL_RETURN;
				callRiskified(env, strSalesOrderNo, strOrderHeaderKey, strReasonText, strFullReturnOrCancel);
				
			}
			// For Document Type=0001 Sales Order.
			else if (VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType)) {
				
				Element eleOrderAudit = SCXmlUtil.getChildElement(eleOrder,
						VSIConstants.ELE_ORDER_AUDIT);
				strReasonText = SCXmlUtil.getAttribute(eleOrderAudit, VSIConstants.ATTR_REASON_TEXT);
				String strFullReturnOrCancel = VSIConstants.VSI_FULL_CANCEL;
				callRiskified(env, strOrderNo, strOrderHeaderKey, strReasonText, strFullReturnOrCancel);
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
	 * @param strOrderNo
	 * @param strOrderHeaderKey
	 * @param strReasonText
	 * @throws RiskifiedError
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws FieldBadFormatException      
	 * This method makes a call to Riskified system.
	 */
	private void callRiskified(YFSEnvironment env, String strOrderNo, String strOrderHeaderKey, String strReasonText,
			String strFullReturnOrCancel) throws RiskifiedError, IOException, ParserConfigurationException,
			YIFClientCreationException, YFSException, RemoteException, FieldBadFormatException {
		CancelOrder cancel = new CancelOrder();
		cancel.setId(strOrderNo);
		cancel.setCancelReason(strReasonText);
		cancel.setCancelledAt(new Date());
		String strCancel = com.riskified.JSONFormater.toJson(cancel);
		log.debug("Riskified Request: " + strCancel);
		storeRequest(env, strOrderHeaderKey, strCancel, strFullReturnOrCancel);
		RiskifiedClient client = new RiskifiedClient();
		Response response = client.cancelOrder(cancel);
		String strResponse = com.riskified.JSONFormater.toJson(response);
		log.debug("Riskified Request: " + strResponse);

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
	 * Riskified system.
	 */
	public void storeRequest(YFSEnvironment env, String ohk, String request, String chargeType)
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
