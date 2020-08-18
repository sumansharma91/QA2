package com.vsi.oms.condition;


import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

// this class is used for checking if orderNo exist

public class VSIWebOrderDetailsCheck implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIWebOrderDetailsCheck.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.debug("VSIWebOrderDetailsCheck.evaluateCondition ");
		}

		boolean bOrderNoExist = false;

		if (!YFCObject.isVoid(eleOrder)) {
			String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
			if (!YFCObject.isVoid(strOrderNo)) {
				bOrderNoExist = true;
			}
		
			else{
				YFSException yy = new YFSException();
				yy.setErrorCode("YFS10003");
				yy.setErrorDescription("YFS:Invalid Order");
				yy.setAttribute(VSIConstants.ATTR_ERROR_CODE, "YFS10003");
				yy.setAttribute(VSIConstants.ATTR_ERROR_DESC, "YFS:Invalid Order");
				throw yy;
			}
			
		}

	return bOrderNoExist;
}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
