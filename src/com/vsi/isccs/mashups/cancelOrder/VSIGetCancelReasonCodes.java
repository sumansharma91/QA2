package com.vsi.isccs.mashups.cancelOrder;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.ibm.isccs.order.mashups.SCCSFSGetValidCarrierServiceList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSException;

public class VSIGetCancelReasonCodes extends SCCSBaseMashup{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetCancelReasonCodes.class);

	public Element massageInput(Element eleInput1, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		log.beginTimer("VSIGetCancelReasonCodes.getCancelReasonCodes : START");
		
		Element eleInput = (Element) uiContext.getRequest().getAttribute(
				VSIConstants.SC_CONTROLLER_INPUT);
		
		try {
			Set<String> setEntCode = fetchSetMarketPlaceEnt(uiContext);
			String strEnterpriseCode=eleInput.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			if (!YFCCommon.isVoid(strEnterpriseCode) && setEntCode.contains(strEnterpriseCode)) {
				Document commonCodeDoc = SCXmlUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
				commonCodeDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.YCD_CNCL_REASON);
				//commonCodeDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, VSIConstants.DOCUMENT_TYPE);
				
				commonCodeDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORG_CODE,
						eleInput.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
				
				commonCodeDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_CALL_ORG_CODE, VSIConstants.SPACE);
				return commonCodeDoc.getDocumentElement();
			}
		} catch (YFSException yfse) {
			log.error("YFSException in VSIGetCancelReasonCodes.massageInput() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIGetCancelReasonCodes.massageInput() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIGetCancelReasonCodes.getCancelReasonCodes() : ");
		}
		
		log.endTimer("VSIGetCancelReasonCodes.getCancelReasonCodes : END");
		return eleInput1;
	}
	/**
	 * 
	 * 
	 * @param uiContext
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 */
		private Set<String> fetchSetMarketPlaceEnt(SCUIContext uiContext)
				throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
			HashSet<String> setEntCode = new HashSet<String>();
			Document docInput=XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
			Element eleCommonCode = docInput.getDocumentElement();
			eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODETYPE_MARKETPLACE_ENT);
			//eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEnterpriseCode);
			Element eleOut = (Element) SCUIMashupHelper.invokeMashup("extn_getCommonCodeList_For_MarketPlace",
					eleCommonCode, uiContext);

			NodeList nlCommonCode = eleOut.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);
			int iCount = nlCommonCode.getLength();
			for (int i = 0; i < iCount; i++) {
				Element eleCommonCodeOutput = ((Element) nlCommonCode.item(i));
				String strCodeValue = eleCommonCodeOutput.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				setEntCode.add(strCodeValue);
			}
			return setEntCode;
		}
}
