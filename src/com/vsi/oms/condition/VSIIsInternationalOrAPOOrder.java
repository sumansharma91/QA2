package com.vsi.oms.condition;

import java.rmi.RemoteException;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIIsInternationalOrAPOOrder  implements YCPDynamicConditionEx{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIIsInternationalOrAPOOrder.class);

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		boolean isInternationalorAPO = false;
		Document docgetCommonCodeOutput = null;
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIIsAPOFPOOrder================================");
		}
		if(!YFCObject.isNull(eleOrder)){
			String  strSourcingClassification = eleOrder.getAttribute(VSIConstants.ATTR_SOURCING_CLASSIFICATION);
	 		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
	 		if(!YFCObject.isNull(eleOrderLines)){
		 		Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
		 		if(!YFCObject.isNull(eleOrderLine)){
			 			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			 			String sZipCode = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);
			 			
						Document docgetCommonCodeInput = SCXmlUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
			    		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sZipCode);
			    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION, VSIConstants.INTERNATIONAL_ORDER);
			    		
			    		
			    		try {
							docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_COMMONCODELIST,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
							
							Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
							
							if(commonCodeListElement.hasChildNodes() || strSourcingClassification.equalsIgnoreCase( VSIConstants.INTERNATIONAL_ORDER)){
								isInternationalorAPO = true;
							}//end of if
							if(log.isDebugEnabled()){
								log.info("================End of VSIIsAPOFPOOrder Class================================");
							}
							
						}//end of try
			    		catch (YFSException | RemoteException | YIFClientCreationException e) {
							
							e.printStackTrace();
						}//end of catch
			 			
		 		}//end of orderline null check
	 		}//end of orderLineList null check
		}//end of Order null check
		// TODO Auto-generated method stub
		return isInternationalorAPO;
	}//end of condition

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
