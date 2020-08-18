package com.vsi.oms.condition;

import java.util.ArrayList;
import java.util.Map;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VSIIsLiabilityTender implements YCPDynamicConditionEx{
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		
		boolean isCOMOrder = false;
		try{
			//get PaymentType Attribute			
			Element rootinXML = inXML.getDocumentElement();
			Element elePaymentMethods=SCXmlUtil.getChildElement(rootinXML, VSIConstants.ELE_PAYMENT_METHODS);
			//Element elePaymentMethod=SCXmlUtil.getChildElement(elePaymentMethods, VSIConstants.ELE_PAYMENT_METHOD);
			
			String attrEnterpriseCode = rootinXML.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			
			if("EBAY".equalsIgnoreCase(attrEnterpriseCode))
			{
				return true;
			}
			ArrayList<Element> alPaymentMethodsElement = SCXmlUtil.getChildren(elePaymentMethods, VSIConstants.ELE_PAYMENT_METHOD);
			//Looping through Payment methods of output of getOrderList
			for(Element PaymentMethodElement:alPaymentMethodsElement)
			{
				
				String strPaymentType=PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
				if(!YFCObject.isNull(strPaymentType) && 
						(strPaymentType.equals(VSIConstants.STR_CASH) || strPaymentType.equals(VSIConstants.STR_CHECK))
								){
					isCOMOrder = true;
					break;
				}//end of if for hasLiabilityTender
				
			}
	} catch (Exception e){
		e.printStackTrace();
	}
		return isCOMOrder;
}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}