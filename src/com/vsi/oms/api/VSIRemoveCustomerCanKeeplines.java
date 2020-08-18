package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIRemoveCustomerCanKeeplines {

	private YFCLogCategory log = YFCLogCategory.instance(VSIRemoveCustomerCanKeeplines.class);
	YIFApi api;

	/* 
	 * This method will be invoked from DRAFT_ORDER_CONFIRM.0003 on success event to send email
	 */

	public void removeCustomerCanKeepline(YFSEnvironment env,Document inDoc) throws RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("Input XML VSIRemoveCustomerCanKeeplines : "+ XMLUtil.getXMLString(inDoc));
		}
		Element eleOrder = inDoc.getDocumentElement();
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		for(Element eleOrderLine : arrOrderLines){
			String strLineType=eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			if(!YFCCommon.isVoid(strLineType)&&strLineType.equalsIgnoreCase(VSIConstants.LINE_TYPE_CREDIT)){
				eleOrderLines.removeChild(eleOrderLine);
			}
		}
		Element eleEmaildoc=inDoc.getDocumentElement();
		NodeList nlchangeOrderLineList = eleEmaildoc
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

		if (nlchangeOrderLineList.getLength() > 0){
			api = YIFClientFactory.getInstance().getApi();
			api.executeFlow(env, "VSISendMailOnReturnCreation", inDoc);
		}
		
	}

}
