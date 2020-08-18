package com.vsi.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;

/**
 * @author IBM, Inc.
 * This dynamic condition is implemented for wholesale orders to check if 
 * the logic of generating invoice should be considered at default shipment level
 * or order level depending on the configuration for enterprise code.        
 */

public class VSIIsShipmentInvoice  implements YCPDynamicConditionEx,VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIIsShipmentInvoice.class);
	
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String conditionName, Map mapData, Document inputXML) {
		log.beginTimer("VSIIsShipmentInvoice.evaluateCondition");
		
		boolean bInvoiceLevelReq = false;

		if(log.isDebugEnabled()) {
			log.debug("Printing Input XML Invoice Condition :" + XmlUtils.getString(inputXML));
			
			
		}
		String strEnterpriseCode = mapData.get(ATTR_ENTERPRISE_CODE).toString();
		if(!YFCCommon.isVoid(strEnterpriseCode)) {
		try {
			Element eleCommonCode = (Element) (VSIUtils.getCommonCodeList(env,VSI_WH_INVOICE_LEVEL_REQ,strEnterpriseCode,ATTR_DEFAULT).get(0));
			if(!YFCCommon.isVoid(eleCommonCode)) {
				String strCodeShortDesc = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
				if(log.isDebugEnabled()){
					log.debug("VSIIsShipmentInvoice.evaluateCondition: CodeShortdesc is " + strCodeShortDesc);
				}
				
				if(!YFCCommon.isVoid(strCodeShortDesc) && strCodeShortDesc.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_ORDER)) {
					bInvoiceLevelReq = false;
				}
				else {
					bInvoiceLevelReq = true;
				}
			
			} 
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		if(log.isDebugEnabled()){
			log.debug("VSIIsShipmentInvoice.evaluateCondition: flag returned is " + bInvoiceLevelReq);
		}
		log.endTimer("VSIIsShipmentInvoice.evaluateCondition");
		return bInvoiceLevelReq;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
