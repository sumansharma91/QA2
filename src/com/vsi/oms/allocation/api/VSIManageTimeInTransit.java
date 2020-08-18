package com.vsi.oms.allocation.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Call getTimeInTransitList for Carrier/Carrier/Service/ShipTo/ShipFrom combination.
 * If a record is returned, then take the key and call modifyTimeInTransit with input data
 * else call createTimeInTransit to insert the new record  in EXTN_TIME_IN_TRANSIT table.
 * 
 * 
 * @author IBM
 *
 */
public class VSIManageTimeInTransit extends VSIBaseCustomAPI {

	private YFCLogCategory log = YFCLogCategory.instance(VSIManageTimeInTransit.class);

	public Document manageTimeInTransit(YFSEnvironment env, Document docInput) {
		log.beginTimer("VSIManageTimeInTransit.manageTimeInTransit : START");
		try {
			Element eleTimeInTransitInput = docInput.getDocumentElement();
			Document docMultiApi = SCXmlUtil.createDocument(VSIConstants.ELE_MULTI_API);
			Element eleMultiApi = docMultiApi.getDocumentElement();
			Element eleAPI = SCXmlUtil.createChild(eleMultiApi, VSIConstants.ELE_API);
			Element eleInput = SCXmlUtil.createChild(eleAPI, VSIConstants.ELE_INPUT);
			Element eleTNTInput = SCXmlUtil.importElement(eleInput, eleTimeInTransitInput);
			eleTNTInput.removeAttribute(VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT);
			eleAPI.setAttribute(VSIConstants.ATTR_IS_EXTN_DB_API, VSIConstants.FLAG_Y);
			eleAPI.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.API_GET_EXTN_TIME_IN_TRANSIT_LIST);
			
			// Check whether this entry exists
			Document docMultiApiOut = VSIUtils.invokeAPI(env, VSIConstants.API_MULTI_API, docMultiApi);
			Element eleMultiApiOut = docMultiApiOut.getDocumentElement();
			Element eleAPIOut = SCXmlUtil.getChildElement(eleMultiApiOut, VSIConstants.ELE_API);
			Element eleOutput = SCXmlUtil.getChildElement(eleAPIOut, VSIConstants.ELE_OUTPUT);
			Element eleTimeInTransitList = SCXmlUtil.getChildElement(eleOutput, VSIConstants.ELE_EXTN_TIME_IN_TRANSIT_LIST);
			eleTNTInput.setAttribute(VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT,
					eleTimeInTransitInput.getAttribute(VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT));
			if (eleTimeInTransitList.hasChildNodes()) {
				// Get ExtnTNTLineKey and call changeExtnTimeInTransit with ExtnTNTLineKey
				Element eleTimeInTransit = SCXmlUtil.getChildElement(eleTimeInTransitList, VSIConstants.ELE_EXTN_TIME_IN_TRANSIT);
				eleTNTInput.setAttribute(VSIConstants.ATTR_TNT_LINE_KEY,
						eleTimeInTransit.getAttribute(VSIConstants.ATTR_TNT_LINE_KEY));
				eleAPI.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.API_CHANGE_EXTN_TIME_IN_TRANSIT);
				VSIUtils.invokeAPI(env, VSIConstants.API_MULTI_API, docMultiApi);
			} else {
				// Create the time in transit entry
				eleAPI.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.API_CREATE_EXTN_TIME_IN_TRANSIT);
				VSIUtils.invokeAPI(env, VSIConstants.API_MULTI_API, docMultiApi);
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIManageTimeInTransit.manageTimeInTransit() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIManageTimeInTransit.manageTimeInTransit() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIManageTimeInTransit.manageTimeInTransit() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIManageTimeInTransit.manageTimeInTransit() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIManageTimeInTransit.manageTimeInTransit() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIManageTimeInTransit.manageTimeInTransit() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIManageTimeInTransit.manageTimeInTransit() : ");
		}
		if(log.isDebugEnabled()){
			log.endTimer("VSIManageTimeInTransit.manageTimeInTransit : END");
		}
		return null;
	}
}
