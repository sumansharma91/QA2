package com.vsi.oms.api.order;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class VSISendBOPUSToJDA {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSISendBOPUSToJDA.class);
	YIFApi api;
	String strCustPoNo = "";
	public void vsiSendBOPUSToJDA(YFSEnvironment env, Document inXML) throws Exception{
		log.debug("\nvsiSendBOPUSToJDA: " + SCXmlUtil.getString(inXML));
		Element rootElement = inXML.getDocumentElement();
		 strCustPoNo = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		
		Document getOrderListInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrder = getOrderListInput.getDocumentElement();
		Element eleOrderLine=SCXmlUtil.createChild(eleOrder, VSIConstants.ELE_ORDER_LINE);
      
		eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPoNo);
		
		Document outDoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORD_LIST_VSI_CNCL_EMAIL, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
		
		Element rootOutDoc= outDoc.getDocumentElement();
		Element eleRootOrder = SCXmlUtil.getChildElement(rootOutDoc, VSIConstants.ELE_ORDER);
		rootOutDoc.setAttribute("Action", "CANCEL");
		eleRootOrder.setAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID, "");
			rootOutDoc.setAttribute("EmailType", "CCM");
		
			rootOutDoc.setAttribute("MessageType", "");
		
		log.debug("JDA call: " + SCXmlUtil.getString(outDoc));
		VSIUtils.invokeService(env, VSIConstants.VSI_CNCL_PUBLISH_TO_JDA, outDoc);
	}
}

