package com.vsi.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;

import com.yantra.ycp.japi.YCPDynamicConditionEx;

import com.yantra.yfs.japi.YFSEnvironment;


public class VSIApplyScheduleHold implements YCPDynamicConditionEx {
	@Override
	/**
	 * Condition to apply VSIRemorseHold
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) 
	{
		
		boolean applyScheduleHold = false;
		
		String enterpriseCode = inXML.getDocumentElement().getAttribute("EnterpriseCode");
		String paymentStatus = inXML.getDocumentElement().getAttribute("PaymentStatus");
		if(("VSI.com".equals(enterpriseCode) || enterpriseCode.equals("ADP")) && !"AUTHORIZED".equals(paymentStatus) &&
					!"PAID".equals(paymentStatus) )
		{
			applyScheduleHold = true;
		}
		
		
		return applyScheduleHold;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
