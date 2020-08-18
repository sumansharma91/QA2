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
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

// this class is used for checking if WMS flag is turned on

public class VSICheckWMSFlag implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckWMSFlag.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		log.verbose("VSICheckWMSFlag.evaluateCondition Input XML :" + SCXmlUtil.getString(inXML));

		boolean bWMSFlag = false;
		try {
			ArrayList<Element> eleGetCommonCodeShipDateList = VSIUtils.getCommonCodeList(env, VSIConstants.CODE_TYPE_VSI_SHIPDATE_RULE,VSIConstants.CODE_VALUE_STAMP_WMS_DATE, "");
			if (!eleGetCommonCodeShipDateList.isEmpty() && (VSIConstants.FLAG_N
					.equalsIgnoreCase(eleGetCommonCodeShipDateList.get(0).getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION))))	
			{

				bWMSFlag=true;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();
		}
		log.verbose("VSICheckDTCFlag.evaluateCondition output:bDTCFlag " +bWMSFlag );
		return bWMSFlag;
	}
	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
