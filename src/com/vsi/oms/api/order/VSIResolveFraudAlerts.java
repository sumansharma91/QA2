package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIResolveFraudAlerts {

	private YFCLogCategory log = YFCLogCategory.instance(VSIResolveFraudAlerts.class);

	public Document resolveFraudAlerts(YFSEnvironment env,Document inDoc){

		try {
			if(log.isDebugEnabled()){
				log.debug("Inside VSIResolveFraudAlerts.resolveFraudAlerts input is "+
						XMLUtil.getXMLString(inDoc));
			}
			Element eleOrderHoldType=inDoc.getDocumentElement();
			String strStatus=eleOrderHoldType.getAttribute(VSIConstants.ATTR_STATUS);
			String strHoldType=eleOrderHoldType.getAttribute(VSIConstants.ATTR_HOLD_TYPE);
			String strOrderHeaderKey=eleOrderHoldType.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			if(!YFCCommon.isStringVoid(strHoldType) && !YFCCommon.isStringVoid(strStatus) && !YFCCommon.isStringVoid(strOrderHeaderKey)
					&& ((VSIConstants.HOLD_FRAUD_HOLD).equals(strHoldType)|| (VSIConstants.HOLD_FRD_REVIEW_HOLD).equals(strHoldType))
					&& (("1300").equals(strStatus) || ("1200").equals(strStatus))){
				Document docResolveExceptionInput = XMLUtil.createDocument(VSIConstants.ELE_RESOLUTION_DETAILS);
				Element eleResolutionDetails = docResolveExceptionInput.getDocumentElement();
				Element eleInbox = docResolveExceptionInput.createElement(VSIConstants.ELE_INBOX);
				eleResolutionDetails.appendChild(eleInbox);
				eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
				eleInbox.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
						"Fraud Check");
				eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
						"Fraud Check");
				eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID,
						"VSI_FRAUD_ALERT");
				VSIUtils.invokeAPI(env, "resolveException",
						docResolveExceptionInput);
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error in VSIResolveFraudAlerts.resolveFraudAlerts " + e.toString());
		}

		return inDoc;
	}
}
