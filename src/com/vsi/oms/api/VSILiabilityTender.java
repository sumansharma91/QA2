package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSILiabilityTender {

	//Start: Created Payment changes 04-27-2017
	public Document update(YFSEnvironment env, Document inXML) throws Exception{
		String tranNumber = "";
		String seqNum = VSIConstants.SEQ_VSI_SEQ_TRANID;
		Element eleOrderLine = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		if(!YFCObject.isVoid(eleOrderLine)){
			String attrDelMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
			if(attrDelMethod.equalsIgnoreCase(VSIConstants.ATTR_DEL_METHOD_PICK)){
				seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_WEB_PICK;
			}
			else{
				seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_SHP;
			}
		}//orderline null check

		
		String enteredBy = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_SELLING_OUTLET);
		String enterpriseCode = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		if(!YFCCommon.isVoid(env.getTxnObject("MKPCustomerID")) && "EBAY".equalsIgnoreCase(enterpriseCode))
		{
			String customerID = (String)env.getTxnObject("MKPCustomerID");
			inXML.getDocumentElement().setAttribute("BillToID", customerID);
		}
		String enteredByPadded = (VSIConstants.VALUE_SEQ_PADDING + enteredBy).substring(enteredBy.trim().length());
		seqNum = seqNum + enteredByPadded;
		tranNumber = VSIDBUtil.getNextSequence(env , seqNum);
		
		SCXmlUtil.getChildElement(inXML.getDocumentElement(), VSIConstants.ELE_EXTN).setAttribute(VSIConstants.ATTR_EXTN_TRANS_NO, tranNumber);
	
		return inXML;
	//END: //Start: Created Payment changes 04-27-2017
		
	}
}
