package com.vsi.oms.condition;

import java.util.ArrayList;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

// this class is used for checking if order confirm email flag is turned on

public class VSIOrderConfirmEmailFlag implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIOrderConfirmEmailFlag.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		if(log.isDebugEnabled()){
			log.debug("VSIOrderConfirmEmailFlag.evaluateCondition ");
		}
		String strOrderConfirmEmailFlag=null;
		boolean bEmaillag = false;
		try {
			ArrayList<Element> listOrderConfirmEmailFlag;
			listOrderConfirmEmailFlag = VSIUtils.getCommonCodeList(env, "VSI_ORDER_EMAIL", "COMBO", "DEFAULT");
			if(!listOrderConfirmEmailFlag.isEmpty()){
				Element eleCommonCode=listOrderConfirmEmailFlag.get(0);
				strOrderConfirmEmailFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				if(!YFCCommon.isStringVoid(strOrderConfirmEmailFlag)&&"Y".equalsIgnoreCase(strOrderConfirmEmailFlag)){

					bEmaillag=true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();
		}
		if(log.isDebugEnabled()){
			log.debug("VSIOrderConfirmEmailFlag.evaluateCondition output:bEmaillag " +bEmaillag );
		}
		return bEmaillag;
	}
	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
