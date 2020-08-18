package com.vsi.isccs.mashups;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIAuthorizationHelper;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.scmw.utils.SCMWCsvUtils;
import com.yantra.yfc.core.YFCObject;

public class VSIGetPaymentTypeListMashup extends SCCSBaseMashup {

	public Element massageOutput(Element outEl,
			SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {

		HashSet<String> hPayTypeNoDisplay = new HashSet<String>();

		Element eleMashupOutput = SCXmlUtil.createFromString(
				"<PaymentTypeList></PaymentTypeList>").getDocumentElement();

		//System.out.println("Input:::" + SCXmlUtil.getString(outEl));

		Document docCommonCode = SCXmlUtil.createDocument();
		Element eleCommonCodeInput = docCommonCode.createElement("CommonCode");
		docCommonCode.appendChild(eleCommonCodeInput);
		Element eleCommonCodeOutput = (Element) SCUIMashupHelper
				.invokeMashup("extn_getCommonCode",
						eleCommonCodeInput, uiContext);
		//System.out.println("eleCommonCodeOutput"+SCXmlUtil.getString(eleCommonCodeOutput));
		NodeList nlsCommonCode = eleCommonCodeOutput
				.getElementsByTagName("CommonCode");
		for (int j = 0; j < nlsCommonCode.getLength(); j++) {
			Element eleCommonCode = (Element) nlsCommonCode.item(j);
			String strVodeValue = eleCommonCode.getAttribute("CodeValue");
			hPayTypeNoDisplay.add(strVodeValue);
		}
		Element ccElement = null;
		NodeList nlsPaymentType = outEl.getElementsByTagName("PaymentType");
		List<Element> lsPaymentTypeMap = new ArrayList<Element>();
		int count = 0;
		// Logic for PyamentTypeList and CommonCodeList comparesion
		for (int i = 0; i < nlsPaymentType.getLength(); i++) {
			Element elePaymentType = (Element) nlsPaymentType.item(i);
			String paymentType = elePaymentType.getAttribute("PaymentType");
			if (!hPayTypeNoDisplay.contains(paymentType)) {
				if ("CREDIT_CARD".equals(paymentType)) {
					ccElement = elePaymentType;
				} else {
					 // 1B Changes - excluding payment types based on role
			        if(!SCUIAuthorizationHelper.hasPermission(uiContext,VSIConstants.STR_VSIPAYMTHDSUP) &&
			        		(VSIConstants.STR_CASH.equals(paymentType) || VSIConstants.STR_CHECK.equals(paymentType) || VSIConstants.STR_AR_CREDIT.equals(paymentType)))
			        {
			        	continue;
			        }
					lsPaymentTypeMap.add(elePaymentType);
				}
			}
			// .removeNode(elePaymentType);
		}
		if (!YFCObject.isVoid(ccElement)) {
			SCXmlUtil.importElement(eleMashupOutput, ccElement);
		}
		Iterator<Element> itr = lsPaymentTypeMap.iterator();
		// hPaymentTypeMap.entrySet().iterator()
		while (itr.hasNext()) {
			SCXmlUtil.importElement(eleMashupOutput, itr.next());
			// eleMashupOutput.appendChild(itr.next());
		}
		//System.out.println("OUTPUT::::"
				//+ SCXmlUtil.getString(eleMashupOutput.getOwnerDocument()));
		return eleMashupOutput;

	}
}
