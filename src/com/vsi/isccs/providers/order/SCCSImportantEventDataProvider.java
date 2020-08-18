package com.vsi.isccs.providers.order;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.platform.handlers.ISCUIAdditionalDataProvider;
import com.vsi.oms.utils.GcoXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;

/**
 * This class is invoked for every occurrence of xpaths mentioned in
 * SCCSDataProviderExtn.xml. It then checks if /ImportantEvent/@ReasonCodeName.
 * 
 * @author IBM
 *
 */
public class SCCSImportantEventDataProvider implements ISCUIAdditionalDataProvider, VSIConstants {
	private static YFCLogCategory logger = YFCLogCategory.instance(SCCSImportantEventDataProvider.class);

	public void addAdditionalData(SCUIContext uiContext, Element arg1, Element arg2, Element interestedInElement) {
		try {
			logger.beginTimer("SCCSImportantEventDataProvider.addAdditionalData:interestedInElement is (incoming): "
				+ GcoXmlUtil.getElementString(interestedInElement));

			if (ACTION_CANCEL.equals(interestedInElement.getAttribute(ATTR_MODIFICATION_TYPE))
					&& YFCObject.isVoid(interestedInElement.getAttribute(ATTR_REASON_CODE_NAME))) {
				Document docGetCommonCodeList = SCXmlUtil.createDocument(ELE_COMMON_CODE);
				Element eleGetCommonCodeList = docGetCommonCodeList.getDocumentElement();
				eleGetCommonCodeList.setAttribute(ATTR_CODE_TYPE, YCD_CNCL_REASON);
				eleGetCommonCodeList.setAttribute(ATTR_CODE_VALUE, interestedInElement.getAttribute(ATTR_REASON_CODE));
				Element eleCommonCodeList = (Element)SCUIMashupHelper.invokeMashup(MASHUP_GET_COMMON_CODE_LIST_VSI_CANCEL_REASON,
					eleGetCommonCodeList, uiContext);
				if (eleCommonCodeList.hasChildNodes()) {
					Element eleCommonCode = SCXmlUtil.getChildElement(eleCommonCodeList, ELE_COMMON_CODE);
					interestedInElement.setAttribute(ATTR_REASON_CODE_NAME, eleCommonCode.getAttribute(ATTR_CODE_LONG_DESCRIPTION));
				} else {
					eleGetCommonCodeList.setAttribute(ATTR_CODE_TYPE, VSI_SYS_CNCL_REASON);
					eleCommonCodeList = (Element)SCUIMashupHelper.invokeMashup(MASHUP_GET_COMMON_CODE_LIST_VSI_CANCEL_REASON,
							eleGetCommonCodeList, uiContext);
					Element eleCommonCode = SCXmlUtil.getChildElement(eleCommonCodeList, ELE_COMMON_CODE);
					interestedInElement.setAttribute(ATTR_REASON_CODE_NAME, eleCommonCode.getAttribute(ATTR_CODE_LONG_DESCRIPTION));
				}
			}

			logger.endTimer("SCCSImportantEventDataProvider.addAdditionalData:interestedInElement is (outgoing): "
				+ GcoXmlUtil.getElementString(interestedInElement));
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw e;

		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
		}
	}

}