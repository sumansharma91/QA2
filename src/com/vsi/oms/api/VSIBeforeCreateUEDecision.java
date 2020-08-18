package com.vsi.oms.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIBeforeCreateUEDecision {
	private YFCLogCategory log = YFCLogCategory.instance(VSIBeforeCreateUEDecision.class);
	YIFApi api;

	public void beforeCreateOrderDecision(YFSEnvironment env, Document inXML)
			throws  YIFClientCreationException, RemoteException {
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIBeforeCreateUEDecision================================");
		}
		api = YIFClientFactory.getInstance().getApi();
		Element eleOrder = inXML.getDocumentElement();

		String strOrderType = eleOrder
				.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		String strEntryType = eleOrder
				.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
		if(VSIConstants.WEB.equalsIgnoreCase(strOrderType) && VSIConstants.WEB.equalsIgnoreCase(strEntryType)){
			
			api.executeFlow(env, "VSIBeforeCreateOrderWeb", inXML);
		}
		if(VSIConstants.ENTRYTYPE_CC.equalsIgnoreCase(strEntryType)){
			
			api.executeFlow(env, "VSIBeforeCreateOrderCom", inXML);

		}
	}
}

