package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICancelADPBoxRelease {

	private YFCLogCategory log = YFCLogCategory.instance(VSICancelADPBoxRelease.class);
	
	
	/***
	 * 
	 * This method will be invoked from On_release_create_or_change event to cancel 
	 * ADPBox only release
	 * 
	 * @param env
	 * @param inDoc
	 * @return
	 */

	public Document cancelStandaloneADPBoxRelease(YFSEnvironment env,Document inDoc){

		boolean isADPBoxRelease=false;
		try {
			if(log.isDebugEnabled()){
				log.debug("Inside VSICancelADPBoxRelease: cancelStandaloneADPBoxRelease input is "+
						XMLUtil.getXMLString(inDoc));
			}
			NodeList nlOrderLine=inDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iCount=nlOrderLine.getLength();
			for (int i = 0; i < iCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleExtn=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN);
				if(!YFCCommon.isVoid(eleExtn)){
					String strExtnSDKLine=eleExtn.getAttribute("ExtnSDKLine");
					if(!YFCCommon.isStringVoid(strExtnSDKLine) && "Y".equals(strExtnSDKLine)){
						isADPBoxRelease=true;
					}
					else{
						isADPBoxRelease=false;
						break;
					}
				}
				else{
					isADPBoxRelease=false;
					break;
				}
			}//end of for loop
			
			if(log.isDebugEnabled()){
				log.debug("isADPBoxRelease : "+isADPBoxRelease);
			}
			/*
			 * if the order release has only ADP box items then cancel the release and the line
			 * 
			 * API : changeRelease
			 * <OrderRelease OrderReleaseKey="2018042305024129996543" Action="CANCEL"  />
			 * 
			 * 
			 */
			if(isADPBoxRelease){
				Element eleInRoot=inDoc.getDocumentElement();
				String strOrderReleaseKey=eleInRoot.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
				
				//creating input for changeRelease
				Document docChangeRelease=XMLUtil.createDocument(VSIConstants.ELE_ORDER_RELEASE);
				Element eleOrderRelease=docChangeRelease.getDocumentElement();
				eleOrderRelease.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY,strOrderReleaseKey);
				eleOrderRelease.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
				
				//invoking changeRelease
				if(log.isDebugEnabled()){
					log.debug("VSICancelADPBoxRelease: cancelStandaloneADPBoxRelease input for changeRelease "+ XMLUtil.getXMLString(docChangeRelease));
				}
				
				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_RELEASE, docChangeRelease);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error in  cancelStandaloneADPBoxRelease " + e.toString());
			throw new YFSException(e.getMessage());
		}

		return inDoc;
	}
}
