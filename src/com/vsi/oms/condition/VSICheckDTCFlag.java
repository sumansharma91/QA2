package com.vsi.oms.condition;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

// this class is used for checking if DTC flag is turned on

public class VSICheckDTCFlag implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckDTCFlag.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		log.verbose("VSICheckDTCFlag.evaluateCondition Input XML :" + SCXmlUtil.getString(inXML));
		String strDTCOFlag=null;
		boolean bDTCFlag = false;
		try {
			ArrayList<Element> listDTCOrderFlag;
			listDTCOrderFlag = VSIUtils.getCommonCodeList(env, "VSI_DTC_FLAG", "DTC", "DEFAULT");
			if(!listDTCOrderFlag.isEmpty()){
			Element eleCommonCode=listDTCOrderFlag.get(0);
				strDTCOFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				if(!YFCCommon.isStringVoid(strDTCOFlag)&&"Y".equalsIgnoreCase(strDTCOFlag)){

					bDTCFlag=true;
				}
			}
	}
		catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();
		}
		log.verbose("VSICheckDTCFlag.evaluateCondition output:bDTCFlag " +bDTCFlag );
		return bDTCFlag;
	}
	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
