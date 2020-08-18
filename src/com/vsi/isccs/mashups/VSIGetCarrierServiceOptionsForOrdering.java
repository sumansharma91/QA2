package com.vsi.isccs.mashups;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.ibm.isccs.order.mashups.SCCSFSGetValidCarrierServiceList;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSException;

/** This class will restrict the Shipping Options other than Standard for OrderLines with APO-FPO zipcode or if it have
 * state part of US Territories. This class will call below two mashup:
 * extn_getCommonCodeList_US_Territories : To fetch the list of states which are part of US Territories.
 * extn_getCommonCodeList_ApoFpoZipCode :  To fetch the list of zipcodes which belongs to APO_FPO zipcode list.
 * 
 * 
 */

public class VSIGetCarrierServiceOptionsForOrdering extends SCCSFSGetValidCarrierServiceList implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetCarrierServiceOptionsForOrdering.class);

	/*public void massageAPIInput(Element inputEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) throws IllegalArgumentException, Exception {
		//log.debug("Input for VSIGetCarrierServiceOptionsForOrdering:massageAPIInput"+XMLUtil.getElementString(inputEl));
		inputEl =super.massageInput(inputEl, mashupMetaData, uiContext);
		//log.debug("Input for VSIGetCarrierServiceOptionsForOrdering:massageAPIInput"+XMLUtil.getElementString(inputEl));
		SCCSMashupUtils.addAttributeToUIContext(uiContext, "inputElement", this, inputEl);
		//uiContext.setAttribute("InDOC", inputEl);

	}*/

	@SuppressWarnings("unchecked")
	public Element massageOutput(Element outEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {

		Element eOutput = null;
		try {
			log.beginTimer("Start ******* SCCSGetReturnOrderLinesMashup.massageInput ******* Start");
			if(log.isDebugEnabled()){
				log.info("inside massage output custom");
				log.debug("Input for massageOutput: outEL"+XMLUtil.getElementString(outEl));

				//Element inputElement = (Element) SCCSMashupUtils.getAttributeFromUIContext(uiContext, "inputElement", this,false);
				//log.debug("ImassageOutput : inputElement"+XMLUtil.getElementString(inputElement));
				log.debug("Input for massageOutput: After super outEL"+XMLUtil.getElementString(outEl));
				log.debug("Input for massageOutput"+XMLUtil.getElementString(outEl));
			}
		Element eleOrderLines = SCXmlUtil.getChildElement(outEl, "OrderLines");
		List<Element> eleOrderLineList = XMLUtil.getElementsByTagName(eleOrderLines, "OrderLine");
		int iEleOrderLineListSize = eleOrderLineList.size();
		if(log.isDebugEnabled()){
			log.debug("OrderLine Size"+iEleOrderLineListSize);
		}
		String strOrderLineKey = null;
		String strZipCode = null;
		String strState = null;
		int ieleCarrierSvcListSize = 0;
		Element eleCarrierSvc=null;
		String strCarrierServiceCode=null;
		Set<String> setZipCode = fetchAPOFPOZipCodeListFromCommonCode(uiContext);
		Set<String> setStateCode = fetchUSTerritoriesListFromCommonCode(uiContext);
		
		for (int iOL = 0; iOL < iEleOrderLineListSize; iOL++) {
			Element eleOL = eleOrderLineList.get(iOL);
			strOrderLineKey = eleOL.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			if(log.isDebugEnabled()){
				log.debug("strOrderLineKey"+strOrderLineKey);
			}
			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOL, "PersonInfoShipTo");
			strZipCode = elePersonInfoShipTo.getAttribute(ATTR_ZIPCODE);
			if(log.isDebugEnabled()){
				log.debug("ZipCode"+strZipCode);
			}
			strState = elePersonInfoShipTo.getAttribute(ATTR_STATE);
			if(log.isDebugEnabled()){
				log.debug("State"+strState);
			}
			if (setZipCode.contains(strZipCode) || setStateCode.contains(strState)) {
				if(log.isDebugEnabled()){
					log.debug("Block Shipping Options other than Standard");
				}
				/*eleOutElOrderLine = XMLUtil.getElementByXPath(XMLUtil.getDocumentForElement(outEl),
							"/Order/OrderLines/OrderLine[@OrderLineKey='" +strOrderLineKey+ "']");*/
				Element eleCarrierServiceList = SCXmlUtil.getChildElement(eleOL, "CarrierServiceList");
				if(log.isDebugEnabled()){
					log.debug("eleCarrierServiceList"+XMLUtil.getElementString(eleCarrierServiceList));
				}
				List<Element> eleCarrierSvcList = XMLUtil.getElementsByTagName(eleCarrierServiceList, "CarrierService");
				ieleCarrierSvcListSize = eleCarrierSvcList.size();
				for (int count = 0; count < ieleCarrierSvcListSize; count++) {
					eleCarrierSvc = eleCarrierSvcList.get(count);
					strCarrierServiceCode = eleCarrierSvc.getAttribute(ATTR_CARRIER_SERVICE_CODE);
					if(log.isDebugEnabled()){
						log.debug("strCarrierServiceCode"+strCarrierServiceCode);
					}
					if (null != strCarrierServiceCode && !STANDARD.equals(strCarrierServiceCode)) {
						if(log.isDebugEnabled()){
							log.debug("Blocking strCarrierServiceCode"+strCarrierServiceCode);
						}
						eleCarrierSvcList.get(count).getParentNode().removeChild(eleCarrierSvcList.get(count));
					}
				}
				
				
			}
		}
		eOutput = super.massageOutput(outEl, mashupMetaData, uiContext);
		if(log.isDebugEnabled()){
			log.debug("After Transformation : outEl"+XMLUtil.getElementString(outEl));
			log.debug("After Transformation : eOutput"+XMLUtil.getElementString(eOutput));
		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("Error in VSIGetCarrierServiceOptionsForOrdering"+e.getMessage());
			}
		
		return eOutput;
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
	private Set<String> fetchAPOFPOZipCodeListFromCommonCode(SCUIContext uiContext)
			throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		HashSet<String> setZipCode = new HashSet<String>();
		Document docInput = XMLUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
		if(log.isDebugEnabled()){
			log.debug("Input for VSI_APO_FPO getCommonCodeList :" + XMLUtil.getXMLString(docInput));
		}
		Element eleOut = (Element) SCUIMashupHelper.invokeMashup("extn_getCommonCodeList_ApoFpoZipCode",
				eleCommonCode, uiContext);
		NodeList nlCommonCode = eleOut.getElementsByTagName(ELE_COMMON_CODE);
		int iCount = nlCommonCode.getLength();
		for (int i = 0; i < iCount; i++) {
			Element eleCommonCodeOutput = ((Element) nlCommonCode.item(i));
			String strShortDesc = eleCommonCodeOutput.getAttribute(ATTR_CODE_VALUE);
			setZipCode.add(strShortDesc);
		}
		return setZipCode;
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
	private Set<String> fetchUSTerritoriesListFromCommonCode(SCUIContext uiContext)
			throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		HashSet<String> setStateCode = new HashSet<String>();
		Document docInput = XMLUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_US_TERRITORIES");
		if(log.isDebugEnabled()){
			log.debug("Input for VSI_US_TERRITORIES getCommonCodeList :" + XMLUtil.getXMLString(docInput));
		}
		Element eleOut = (Element) SCUIMashupHelper.invokeMashup("extn_getCommonCodeList_US_Territories",
				eleCommonCode, uiContext);

		NodeList nlCommonCode = eleOut.getElementsByTagName(ELE_COMMON_CODE);
		int iCount = nlCommonCode.getLength();
		for (int i = 0; i < iCount; i++) {
			Element eleCommonCodeOutput = ((Element) nlCommonCode.item(i));
			String strShortDesc = eleCommonCodeOutput.getAttribute(ATTR_CODE_VALUE);
			setStateCode.add(strShortDesc);
		}
		return setStateCode;
	}
}
