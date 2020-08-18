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
//this class is used for checking if orderNo exist

public class VSIOrderNoCheck implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIOrderNoCheck.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.debug("VSIOrderNoCheck.evaluateCondition ");
		}

		boolean bOrderNoExist = false;

		if (!YFCObject.isVoid(eleOrder)) {
			String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
			if (!YFCObject.isVoid(strOrderNo)) {
				bOrderNoExist = true;
			}
		
			else{
				YFSException yy = new YFSException();
				yy.setErrorCode("Blank Order No");
				yy.setErrorDescription("OrderNo cannot be blank");
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
