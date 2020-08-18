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
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * Invoked by the VSISendRelease agent's executeJob() as part of the VSI_SEND_RELEASE transaction. This component is
 * responsible for receiving release acknowledgement from WMS via middle ware
 * 
 * Input:
 * 
 * 
 * @author IBM
 *
 */
public class VSIProcessRelease extends VSIBaseCustomAPI implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessRelease.class);
	
	public Document processRelease (YFSEnvironment env, Document inputDoc) {
		if(log.isDebugEnabled()){
		log.beginTimer("VSIProcessRelease.processRelease : START");
		}
		Document docJDAOut = null;
		try {
			Element eleOrderRelease = inputDoc.getDocumentElement();
			// Invoke JDA Force Allocation Webservice 
			docJDAOut = VSIUtils.invokeService(env, VSIConstants.SERVICE_JDA_FORCE_ALLOCATION_WEB_SERVICE, inputDoc);
			Element eleJDAOut = docJDAOut.getDocumentElement();
			Element eleExtnJDAOut = SCXmlUtil.getChildElement(eleJDAOut, ELE_EXTN);
			String strTransferNo = eleExtnJDAOut.getAttribute(ATTR_EXTN_TRANSFER_NO);
			log.info("strTransferNo"+strTransferNo);
			// Apply JDA Transfer number by calling changeRelease API
			if(eleOrderRelease.getAttribute(ATTR_SEND_RELEASE).equalsIgnoreCase(FLAG_Y) && eleOrderRelease.getAttribute(ATTR_REVERSE_ALLOCATION).equalsIgnoreCase(FLAG_Y))
				invokeChangeRelease(env, inputDoc, strTransferNo);
			if(log.isDebugEnabled()){
			log.endTimer("VSIProcessRelease.processRelease : END");
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIProcessRelease.processRelease() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIProcessRelease.processRelease() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIProcessRelease.processRelease() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIProcessRelease.processRelease() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIProcessRelease.processRelease() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIProcessRelease.processRelease() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIProcessRelease.processRelease() : ");
		}
		if(log.isDebugEnabled()){
			log.endTimer("VSIProcessRelease.processRelease : END");
		}
		return docJDAOut;
	}
	public void invokeChangeRelease(YFSEnvironment env, Document inputDoc, String strTransferNo)
	{
		try
		{
			Document docChangeRelease = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
			Element eleChangeRelease = docChangeRelease.getDocumentElement();
			Element eleChangeReleaseExtn = SCXmlUtil.createChild(eleChangeRelease, ELE_EXTN);
			eleChangeReleaseExtn.setAttribute(ATTR_EXTN_TRANSFER_NO, strTransferNo);
			Element eleOrderRelease = inputDoc.getDocumentElement();
			Element eleOrderElement = SCXmlUtil.getChildElement(eleOrderRelease, ELE_ORDER);
			eleChangeRelease.setAttribute(ATTR_SHIP_CUST_PO_NO, eleOrderRelease.getAttribute(ATTR_SHIP_CUST_PO_NO));
			if(!YFCCommon.isVoid(eleOrderElement.getAttribute(ATTR_ORDER_TYPE)))
				eleChangeRelease.setAttribute(ATTR_ORDER_TYPE, eleOrderElement.getAttribute(ATTR_ORDER_TYPE));
			eleChangeRelease.setAttribute(ATTR_ACTION, ACTION_CAPS_MODIFY);
			eleChangeRelease.setAttribute(ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);
			eleChangeRelease.setAttribute(ATTR_OVERRIDE, FLAG_Y);
			eleChangeRelease.setAttribute(ATTR_ORDER_RELEASE_KEY, eleOrderRelease.getAttribute(ATTR_ORDER_RELEASE_KEY));
			VSIUtils.invokeAPI(env, TEMPLATE_ORD_REL_ORD_REL_KEY, API_CHANGE_RELEASE, docChangeRelease);
		}
		catch(Exception e)
		{
			log.info("Exception in VSIProcessRelease.invokeChangeRelease() => ");
			e.printStackTrace();
		}
	}

}