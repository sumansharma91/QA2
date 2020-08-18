package com.vsi.oms.api.callcenter;

import org.w3c.dom.Document;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleGetOrderListForUserID {
private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleInputForGetChargeCategoryList.class);
	
	public Document formInputForUserSpecificGetOrderList(YFSEnvironment env, Document inXML) throws Exception {
		
		log.beginTimer("VSIWholesaleGetOrderListForUserID.formInputForUserSpecificGetOrderList");
		if(log.isDebugEnabled()){
			log.debug("Inside VSIWholesaleGetOrderListForUserID.formInputForUserSpecificGetOrderList" + XMLUtil.getXMLString(inXML) );
		}
		log.endTimer("VSIWholesaleGetOrderListForUserID.formInputForUserSpecificGetOrderList");
		return inXML;
	}
}
