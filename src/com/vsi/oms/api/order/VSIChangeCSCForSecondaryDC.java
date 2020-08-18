package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

// this class is used for updating the carrier service code if order if scheduled from secondary DC
public class VSIChangeCSCForSecondaryDC{
	private YFCLogCategory log = YFCLogCategory.instance(VSIChangeCSCForSecondaryDC.class);

	/* 
	 * This method will be invoked from On_release_create_or_change event to update the carrier service code
	 */

	public void changeCarrierServiceCode(YFSEnvironment env,Document inDoc) throws RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("Input XML changeCSCtoTWODAY : "+ XMLUtil.getXMLString(inDoc));
		}
		Element eleOrderReleaseDoc = inDoc.getDocumentElement();
		String strShipNode = eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		String strOrderReleaseKey=eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
		String strCarrierServiceCode=eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE);
		String strOrderType=eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderReleaseDoc, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
		String strAddressLine6 = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ADDRESS6);
		if(!VSIConstants.SHIP_NODE_CC.equals(strShipNode)&&VSIConstants.STANDARD.equals(strCarrierServiceCode)&&!VSIConstants.WHOLESALE.equals(strOrderType)&&(YFCCommon.isVoid(strAddressLine6)||!VSIConstants.FLAG_Y.equals(strAddressLine6))){
			
			String strZipCode = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);

			//Call getRegionList and getDistributionRuleList api

			Document docDistributionRuleOutput = prepareGetDistributionRuleList(env,strZipCode);


			if(docDistributionRuleOutput != null){
				NodeList nlItemShipNode = docDistributionRuleOutput.getElementsByTagName(VSIConstants.ELE_ITEM_SHIP_NODE);
				for (int j = 0; j < nlItemShipNode.getLength(); j++) {
					Element eleItemShipNode = (Element) nlItemShipNode.item(j);
					String strPriority = eleItemShipNode.getAttribute(VSIConstants.ATTR_PRIORITY);
					String strShipnodeKey = eleItemShipNode.getAttribute(VSIConstants.ATTR_SHIPNODE_KEY);
					if(VSIConstants.ATTR_SHIPNODE_PRIORITY.equals(strPriority)&&strShipNode.equals(strShipnodeKey)&&!YFCCommon.isVoid(strShipNode)){

						// call change release api
						Document docChangeRelease = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_RELEASE);
						Element eleChangeRelease = docChangeRelease.getDocumentElement();
						eleChangeRelease.setAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE, VSIConstants.ATTR_CSC);											
						eleChangeRelease.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_MODIFY);
						eleChangeRelease.setAttribute(VSIConstants.ATTR_SELECT_METHOD, VSIConstants.SELECT_METHOD_WAIT);
						eleChangeRelease.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
						eleChangeRelease.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY,strOrderReleaseKey);
						if(log.isDebugEnabled()){
						log.debug("Input XML docChangeRelease : "+ XMLUtil.getXMLString(docChangeRelease));
						}
						VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORD_REL_ORD_REL_KEY, VSIConstants.API_CHANGE_RELEASE, docChangeRelease);
						break;

					}
				}

			}
		}

	}


	// This method is used for fetching the region name
	private Document prepareGetDistributionRuleList(YFSEnvironment env, String sZipCode) throws RemoteException, YIFClientCreationException{

		Document getRegionDoc = SCXmlUtil.createDocument(VSIConstants.ELE_REGION);
		getRegionDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_REGION_SCHEMA_NAME, VSIConstants.ALL_US);
		Element elePersonInfo = SCXmlUtil.createChild(getRegionDoc.getDocumentElement(), VSIConstants.ELE_PERSON_INFO);
		elePersonInfo.setAttribute(VSIConstants.ATTR_COUNTRY, VSIConstants.US);
		elePersonInfo.setAttribute(VSIConstants.ATTR_ZIPCODE,sZipCode);
		if(log.isDebugEnabled()){
			log.debug("Input XML getRegionDoc : "+ XMLUtil.getXMLString(getRegionDoc));
		}

		Document docGetRegionListOut=  VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_REGION_LIST, VSIConstants.API_GET_REGION_LIST,
				getRegionDoc);
		if(log.isDebugEnabled()){
			log.debug("Input XML docGetRegionListOut : "+ XMLUtil.getXMLString(docGetRegionListOut));
		}
		if (docGetRegionListOut.getDocumentElement().hasChildNodes()) {
			NodeList nRegionList = docGetRegionListOut.getElementsByTagName(VSIConstants.ELE_REGION);

			for (int i = 0; i < nRegionList.getLength(); i++) {
				Element eleRegion = (Element) nRegionList.item(i);
				String strRegionName = eleRegion.getAttribute(VSIConstants.ATTR_REGION_NAME);
				if(!VSIConstants.ATTR_NAVY_EAST_REGION.equals(strRegionName)&&!VSIConstants.ATTR_NAVY_WEST_REGION.equals(strRegionName)){
					String strRegion=strRegionName.replace(" ", "");
					String strDistributionRuleId="VSI_"+strRegion+"_DC";
					if(log.isDebugEnabled()){
						log.debug("Input XML strDistributionRuleId : "+ strDistributionRuleId);
					}

					//invoke getDistributionRuleList api
					return getDistributionRuleListInput(env,strDistributionRuleId);
				}

			}
		}

		return null;
	}
	// this method is used for fetching the distribution rule for a region
	private Document getDistributionRuleListInput(YFSEnvironment env,
			String strDistributionRuleId)throws RemoteException, YIFClientCreationException {
		Document docDistributionRule = SCXmlUtil.createDocument(VSIConstants.ELE_DISTRIBUTION_RULE);
		docDistributionRule.getDocumentElement().setAttribute(VSIConstants.ATTR_DISTRIBUTION_RULE_ID, strDistributionRuleId);
		if(log.isDebugEnabled()){
			log.debug("Input XML docDistributionRule : "+ XMLUtil.getXMLString(docDistributionRule));
		}

		return VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_DISTRIBUTION_RULE_LIST, VSIConstants.API_GET_DISTRIBUTION_RULELIST,
				docDistributionRule);
	}
}