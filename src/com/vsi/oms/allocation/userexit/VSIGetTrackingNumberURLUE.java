package com.vsi.oms.allocation.userexit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.pca.ycd.japi.ue.YCDGetTrackingNumberURLUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * Tracking Number URL UE is implemented to display hyperlink for Tracking numbers sent by WMS
 * 
 * @author IBM
 *
 */
public class VSIGetTrackingNumberURLUE implements YCDGetTrackingNumberURLUE,VSIConstants {

	
	
	public YFCLogCategory log = YFCLogCategory.instance(VSIGetTrackingNumberURLUE.class);
	
	@Override
	public Document getTrackingNumberURL(YFSEnvironment env, Document docInput) throws YFSUserExitException {
		
		log.beginTimer("VSIGetTrackingNumberURLUE.getTrackingNumberURL : START");
		
		try {
			Element eleInput = docInput.getDocumentElement();
			//get all the Tracking Number element in a node list to loop through
			NodeList nlTrackingNumber = eleInput.getElementsByTagName(ELE_TRACKING_NUMBER);
			
			//Start preparing document for getCommonCodeList API
			Document docCommonCodeInput = SCXmlUtil.createDocument(ELE_COMMON_CODE);
			Element eleCommonCodeInput = docCommonCodeInput.getDocumentElement();
			
			//Start looping through the tracking node list
			for(int i=0;i<nlTrackingNumber.getLength();i++){
				Element eleTrackingNumber = (Element)nlTrackingNumber.item(i);
				if(!YFCCommon.isVoid(eleTrackingNumber)){
					
					String strTrackingNumber = eleTrackingNumber.getAttribute(ATTR_TRACKING_NO);
					String strCarrier = eleTrackingNumber.getAttribute(ATTR_SCAC);
					String strCarrierServiceCode = eleTrackingNumber.getAttribute(ATTR_CARRIER_SERVICE_CODE);
					
					//Check for Surepost and MI first and then go on  to carriers
					if(!(YFCCommon.isVoid(strCarrierServiceCode)) && strCarrierServiceCode.equals(CARRIER_SERVICE_CODE_SUREPOST)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_SURE);
					}else if( !(YFCCommon.isVoid(strCarrierServiceCode)) && strCarrierServiceCode.equalsIgnoreCase(CARRIER_SERVICE_CODE_MI)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_MI);
					}else if(!(YFCCommon.isVoid(strCarrier)) && strCarrier.equalsIgnoreCase(SCAC_UPSN)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_UPS);
					}else if( !(YFCCommon.isVoid(strCarrier)) && strCarrier.equalsIgnoreCase(SCAC_LSHIP)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_LSHIP);
					}else if(!(YFCCommon.isVoid(strCarrier)) && strCarrier.equalsIgnoreCase(SCAC_ONTRAC)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_ONTRAC);
					}else if(!(YFCCommon.isVoid(strCarrier)) && strCarrier.equalsIgnoreCase(SCAC_USPS)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_USPS);
					}
					//OMS-2719: Start
					else if(!(YFCCommon.isVoid(strCarrier)) && strCarrier.equalsIgnoreCase(SCAC_FEDEX)){
						eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_FEDEX);
					}
					//OMS-2719: END
					//Invoke common code list based on the carrier or carrier service
					String strCodeLongDesc =null;
					if(!YFCCommon.isVoid(eleCommonCodeInput.getAttribute(ATTR_CODE_TYPE))){
						Document docCommonCodeListOutput = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, docCommonCodeInput);
						Element eleCommonCodeListOutput = docCommonCodeListOutput.getDocumentElement();
						Element eleCommonCodeOutput = SCXmlUtil.getChildElement(eleCommonCodeListOutput, ELE_COMMON_CODE);
						strCodeLongDesc = eleCommonCodeOutput.getAttribute(ATTR_CODE_LONG_DESC);
					}
					//Concat URL present in Code Long Desc with Tracking number to form the complete URL.
					if(!YFCCommon.isVoid(strCodeLongDesc) && !YFCCommon.isVoid(strTrackingNumber)){
						eleTrackingNumber.setAttribute(ATTR_URL, strCodeLongDesc.concat(strTrackingNumber));
					}
				}
			}	
		} catch (Exception e) {
			log.error("Exception occurred while getting TRACKING_URLS common code", e);
			throw VSIUtils.getYFSException(e);
		}	
		
		log.endTimer("VSIGetTrackingNumberURLUE.getTrackingNumberURL : END");
		
		return docInput;
	}
}
