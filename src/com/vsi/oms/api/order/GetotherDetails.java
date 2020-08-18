package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
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

public class GetotherDetails implements VSIConstants {

	private static YFCLogCategory log = YFCLogCategory
			.instance(GetotherDetails.class);
	YIFApi api;

	/**
	 * @param env
	 * @param inXML
	 * @return
	 * @throws Exception
	 */
	public Document shipmentDetails(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside ================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		/**** Stamping TrackingNo ,CarrierServiceCode and TrackingURL ****/

		Element orderEle = (Element) inXML.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);

		Element ShipsEle = inXML.createElement("Shipment");
		orderEle.appendChild(ShipsEle);
		String nTrackingValue = (String) env.getTxnObject("eTrackingValue");
		String nCarrierServiceCode = (String) env
				.getTxnObject("eCarrierServiceCode");
		
		String nSCACandServiceDesc = (String) env
				.getTxnObject("eSCACandServiceDesc");
		
		String nSCAC = (String) env
				.getTxnObject("eSCAC");


		if (!YFCObject.isVoid(nTrackingValue)) {
			ShipsEle.setAttribute("TrackingNo", nTrackingValue);
		}
		
		
		//Start preparing document for getCommonCodeList API
		Document docCommonCodeInput = SCXmlUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCodeInput = docCommonCodeInput.getDocumentElement();
		
		//Check for Surepost and MI first and then go on  to carriers
		if(!(YFCCommon.isVoid(nCarrierServiceCode)) && nCarrierServiceCode.equals(CARRIER_SERVICE_CODE_SUREPOST)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_SURE);
		}else if( !(YFCCommon.isVoid(nCarrierServiceCode)) && nCarrierServiceCode.equalsIgnoreCase(CARRIER_SERVICE_CODE_MI)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_MI);
		}else if(!(YFCCommon.isVoid(nSCAC)) && nSCAC.equalsIgnoreCase(SCAC_UPSN)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_UPS);
		}else if( !(YFCCommon.isVoid(nSCAC)) && nSCAC.equalsIgnoreCase(SCAC_LSHIP)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_LSHIP);
		}else if(!(YFCCommon.isVoid(nSCAC)) && nSCAC.equalsIgnoreCase(SCAC_ONTRAC)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_ONTRAC);
		}else if(!(YFCCommon.isVoid(nSCAC)) && nSCAC.equalsIgnoreCase(SCAC_USPS)){
			eleCommonCodeInput.setAttribute(ATTR_CODE_TYPE, TRACKING_COMMON_CODE_USPS);
		}
		
		//Invoke common code list based on the carrier or carrier service
		String strCodeLongDesc =null;
		if(!YFCCommon.isVoid(eleCommonCodeInput.getAttribute(ATTR_CODE_TYPE))){
			Document docCommonCodeListOutput = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, docCommonCodeInput);
			Element eleCommonCodeListOutput = docCommonCodeListOutput.getDocumentElement();
			Element eleCommonCodeOutput = SCXmlUtil.getChildElement(eleCommonCodeListOutput, ELE_COMMON_CODE);
			strCodeLongDesc = eleCommonCodeOutput.getAttribute(ATTR_CODE_LONG_DESC);
		}
		

		if (!YFCObject.isVoid(strCodeLongDesc)) {
			String nTrackingURL = strCodeLongDesc + nTrackingValue; // Creating
																	// TrackingURL
			ShipsEle.setAttribute("TrackingURL", nTrackingURL);
		}


		
		
		
		/*
		 * Tracking URL will not get created if carrier service code is null or
		 * empty
		 */
		if (!YFCObject.isVoid(nSCACandServiceDesc)) {
			ShipsEle.setAttribute("CarrierServiceCode", nSCACandServiceDesc);
			

		}
		
		// ARE-246 : Mapping for Thank You email needs to be revisited : START
		//inXML = trimOutput(env, inXML);
		// ARE-246 : Mapping for Thank You email needs to be revisited : END

		if (log.isVerboseEnabled()) {
			log.debug("inXML", XmlUtils.getString(inXML));

		}
		
		return inXML;
	}

	private Document trimOutput(YFSEnvironment env, Document inXML) {
		log.beginTimer("GetotherDetails.trimOutput : START");
		
		try {
			Element eleOrderList = inXML.getDocumentElement();
			Element eleOrder = SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
			Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
			Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			if ((VSIConstants.ATTR_ORDER_TYPE_POS.equals(eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE))
					|| VSIConstants.WEB.equals(eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE)))
					&& VSIConstants.LINETYPE_STS.equals(eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE))) {
				inXML = VSIUtils.invokeService(env, VSIConstants.SERVICE_VSI_TRIM_STS_SHIP_CONF_MSG, inXML);
			} else if (VSIConstants.LINETYPE_STH.equals(eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE))) {
				inXML = VSIUtils.invokeService(env, VSIConstants.SERVICE_VSI_TRIM_STH_SHIP_CONF_MSG, inXML);
			}
			
		} catch (RemoteException re) {
			log.error("RemoteException in GetotherDetails.trimOutput() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in GetotherDetails.trimOutput() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in GetotherDetails.trimOutput() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in GetotherDetails.trimOutput() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in GetotherDetails.trimOutput() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in GetotherDetails.trimOutput() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in GetotherDetails.trimOutput() : ");
		}
		
		log.endTimer("GetotherDetails.trimOutput : END");
		
		return inXML;
	}

	/**
	 * @param env
	 * @param strCodeType
	 * @param strCodeValue
	 * @param strOrganizationCode
	 * @param isShortDescRequired
	 * @return
	 * @throws Exception
	 */
	public static String getCommonCodeValue(YFSEnvironment env,
			String strCodeType, String strCodeValue,
			String strOrganizationCode, boolean isShortDescRequired)
			throws Exception {

		Element eleCommonCodeOutputXml = getCommonCodeElement(env, strCodeType,
				strCodeValue, strOrganizationCode, "");

		String strDescription = "";
		if (!XmlUtils.isVoid(eleCommonCodeOutputXml)) {
			if (log.isVerboseEnabled()) {
				log.debug("eleCommonCodeOutputXml",
						XmlUtils.getString(eleCommonCodeOutputXml));
			}

			Element eleCommonCode = XmlUtils.getChildElement(
					eleCommonCodeOutputXml, "CommonCode");
			if (log.isVerboseEnabled()) {
				log.debug("eleCommonCode", XmlUtils.getString(eleCommonCode));

			}
			if (!XmlUtils.isVoid(eleCommonCode)) {
				{
					strDescription = eleCommonCode
							.getAttribute("CodeLongDescription");
				}
			}
		}

		return strDescription;
	}

	/**
	 * @param env
	 * @param strCodeType
	 * @param strCodeValue
	 * @param strOrganizationCode
	 * @param shortDescription
	 * @return
	 * @throws Exception
	 */
	private static Element getCommonCodeElement(YFSEnvironment env,
			String strCodeType, String strCodeValue,
			String strOrganizationCode, String shortDescription)
			throws Exception {

		Document docCommonCode = XmlUtils.createDocument("CommonCode");
		Element eCommonCode = docCommonCode.getDocumentElement();
		eCommonCode.setAttribute("CodeType", strCodeType);
		if (!XmlUtils.isVoid(strCodeValue)) {
			eCommonCode.setAttribute("CodeValue", strCodeValue);
		}
		if (!XmlUtils.isVoid(strCodeValue)) {
			eCommonCode.setAttribute("OrganizationCode", strOrganizationCode);
		}
		if (!XmlUtils.isVoid(shortDescription)) {
			eCommonCode.setAttribute("CodeShortDescription", shortDescription);
		}
		
		try {
		
			YIFApi api2 = YIFClientFactory.getInstance().getApi();
			if (log.isVerboseEnabled()) {
				log.verbose("docCommonCode : \n"
						+ XMLUtil.getXMLString(docCommonCode));
			}
			Document commonCodeOutDoc = api2.invoke(env, "getCommonCodeList",
					docCommonCode);
			return commonCodeOutDoc.getDocumentElement();
		} 
		catch (Exception e) {
			return null;
		}

	}
}