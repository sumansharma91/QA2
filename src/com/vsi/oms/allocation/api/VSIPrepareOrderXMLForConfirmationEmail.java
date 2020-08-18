package com.vsi.oms.allocation.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This component is responsible for sending order confirmation mail to customer on order schedule
 * 
 * Input: SCHEDULE_ORDER.ON_SUCCESS
 * 
 * 
 * @author IBM
 *
 */
public class VSIPrepareOrderXMLForConfirmationEmail extends VSIBaseCustomAPI {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessReleaseAcknowledgement.class);

	public Document prepareOrderXMLForConfirmationEmail(YFSEnvironment env, Document inputDoc) {
		log.beginTimer("PrepareOrderXMLForConfirmationEmail.prepareOrderXMLForConfirmationEmail : START");
		Element eleInput = inputDoc.getDocumentElement();
		log.endTimer("PrepareOrderXMLForConfirmationEmail.prepareOrderXMLForConfirmationEmail : END");
		
		return inputDoc;
	}
}