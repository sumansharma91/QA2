package com.vsi.oms.condition;


import java.util.Map;
import org.w3c.dom.Document;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;


public class VSIIsEventCalledFirstTime implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIApplyRemorseHold.class);
	@Override
	/**
	 * Condition to apply VSIRemorseHold
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) 
	{
		
		boolean isFirstTime = false;
		//System.out.println("the transaction object is "+env.getTxnObject("DraftOrderConfirm"));
		if(YFCCommon.isVoid(env.getTxnObject("DraftOrderConfirm")))
		{
			isFirstTime = true;
		}
		return isFirstTime;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
