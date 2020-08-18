package com.vsi.oms.condition;

import java.rmi.RemoteException;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIIsAPOFPOOrder implements YCPDynamicConditionEx{
	private YFCLogCategory log = YFCLogCategory.instance(VSIIsAPOFPOOrder.class);
	@Override
	/* 
	 * This method checks for APO FPO zipcodes
	 * 
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		boolean isAPOFPOOrder = false;
		Document docgetCommonCodeOutput = null;
		
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIIsAPOFPOOrder================================");
		}
		if(!YFCObject.isNull(eleOrder)){
	 		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
	 		if(!YFCObject.isNull(eleOrderLines)){
		 		Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		 		if(!YFCObject.isNull(eleOrderLine)){
			 		String sDeliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);//Delivery method of first OrderLine
			 		
			 		
			 		if(sDeliveryMethod.equalsIgnoreCase("SHP")){
			 			
			 			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			 			String sZipCode = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);
			 			
						Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
			    		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sZipCode);
			    		
			    		try {
							docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
							
							Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
							
				    		if(commonCodeListElement.hasChildNodes()){
				    			if(log.isDebugEnabled()){
				    				log.debug("CommonCodeList has Child Nodes" +SCXmlUtil.getString(commonCodeListElement));
				    			}
				    			
				    			Document doccreateExceptionInput = SCXmlUtil.createDocument("Inbox");
				    			Element eleInbox = doccreateExceptionInput.getDocumentElement();
				    			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO));
				    			eleInbox.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ENTERPRISE_CODE));
				    			eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_ORDER_HOLD");
				    			eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "APO_FPO_ADDRESS");
				    			eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, "OrderNo " + SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO) + " has a APO/FPO/DPO Ship To address");
				    			eleInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, "OrderNo " + SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO) + " has a APO/FPO/DPO Ship To address");
				    			Element orderEle = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
				    			orderEle.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ENTERPRISE_CODE));
				    			orderEle.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO));
				    			VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, doccreateExceptionInput);
				    			
				    			isAPOFPOOrder = true;
				    			
				    		}//end of inner if
				    		else{
				    			
				    			if(log.isDebugEnabled()){
				    				log.debug("CommonCodeList has no Child Nodes" +SCXmlUtil.getString(commonCodeListElement));
				    			}
				    		}//end of else
				    		if(log.isDebugEnabled()){
				    			log.info("================End of VSIIsAPOFPOOrder Class================================");
				    		}
						} catch (YFSException | RemoteException | YIFClientCreationException e) {
							
							e.printStackTrace();
						}
			 			
			 			
			 		}//end of if
		 		}
	 		}
		}
		
		
		return isAPOFPOOrder;
		

	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
