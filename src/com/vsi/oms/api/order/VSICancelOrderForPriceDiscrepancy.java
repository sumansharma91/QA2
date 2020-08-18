package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICancelOrderForPriceDiscrepancy {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelOrderForPriceDiscrepancy.class);
	
	public Document vsiCancelOrderForPriceDiscrepancy(YFSEnvironment env, Document inXML) throws YFSException
    {
		if(log.isDebugEnabled()){
		log.info("========== Inside VSICancelOrderForPriceDiscrepancy ==============");
		log.debug("========== Printing Input XML ============== \n"+XMLUtil.getXMLString(inXML));
		}
		try {
			if(!YFCObject.isVoid(inXML)){
			Element eleOrder=inXML.getDocumentElement();
			Element eleOverallTotals = SCXmlUtil.getXpathElement(eleOrder, "/Order/OverallTotals");
			String strGrandTotal=eleOverallTotals.getAttribute("GrandTotal");
			Element elePaymentMethod = SCXmlUtil.getXpathElement(eleOrder, "/Order/PaymentMethods/PaymentMethod");
			String strMaxChargeLimit=elePaymentMethod.getAttribute("MaxChargeLimit");
			String strOrderType=eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			double dGrandTotal=0.00;
			double dMaxChargeLimit=0.00;
			if(!YFCObject.isVoid(strGrandTotal))
			dGrandTotal=Double.parseDouble(strGrandTotal);
			if(!YFCObject.isVoid(strMaxChargeLimit))
			dMaxChargeLimit=Double.parseDouble(strMaxChargeLimit);
			eleOrder.setAttribute(VSIConstants.ATTR_IS_CANCELLED, VSIConstants.FLAG_N);
			if((!YFCObject.isVoid(strOrderType) && VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType)) && dGrandTotal>0.00 && dGrandTotal>dMaxChargeLimit)
			{
				Document docChangeOrder=SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleChangeOrder=docChangeOrder.getDocumentElement();
				eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT,VSIConstants.CAN_CODE_PRICE_ISSUE);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, VSIConstants.AUTH_CANCEL_ORDER);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_SELECT_METHOD, VSIConstants.SELECT_METHOD_WAIT);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
				Element eleNotes = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_NOTES);
				Element eleNote = SCXmlUtil.createChild(eleNotes, VSIConstants.ELE_NOTE);
				SCXmlUtil.setAttribute(eleNote, VSIConstants.ATTR_REASON_CODE,VSIConstants.CAN_CODE_PRICE_ISSUE);
				SCXmlUtil.setAttribute(eleNote, VSIConstants.ATTR_NOTE_TEXT,"Order cancelled for pricing issue. If customer calls, offer to replace order as BOPUS or DTC expedited shipping, and honor any discounts");
				Document docOut=VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
				if(log.isDebugEnabled()){
					log.info("========== Order has been cancelled==============");
					}
				eleOrder.setAttribute(VSIConstants.ATTR_IS_CANCELLED, VSIConstants.FLAG_Y);
			}
		} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return inXML;
		
    }

}
