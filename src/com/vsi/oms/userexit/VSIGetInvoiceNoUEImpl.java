package com.vsi.oms.userexit;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetInvoiceNoUE;
import com.yantra.yfc.log.YFCLogCategory;

public class VSIGetInvoiceNoUEImpl implements YFSGetInvoiceNoUE{

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetInvoiceNoUEImpl.class);
	@Override
	public String getInvoiceNo(YFSEnvironment env, String strEnterpriseCode, String strOrderNo, String strShipmentNo) throws YFSUserExitException {
		// TODO Auto-generated method stub
		try{
			// Second Check for OrderNo.
			if(VSIConstants.ENT_NAVY_EXCHANGE.contentEquals(strEnterpriseCode)){
				String strInvoiceNum = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_NAVY_EXCHANGE_INVOICE);
				if(log.isDebugEnabled()){
					log.debug("strInvoiceNum :" + strInvoiceNum);
				}
				return strInvoiceNum;	
			}		
	}catch (Exception e) {
		e.printStackTrace();
		throw new YFSException(
				e.getMessage(),
				e.getMessage(),
				"YFS Exception");
			
	} 
		return null;
	}

}
