package com.vsi.oms.api;

import org.w3c.dom.Document;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIWholesaleSalesAuditEmail implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleSalesAuditEmail.class);
	
	public Document sendSalesAuditEmail(YFSEnvironment env, Document inXML) throws Exception{
		
		log.beginTimer("VSIWholesaleSalesAuditEmail.VSIWholesaleSalesAuditEmail()");
		
		Boolean emailSent = false;
		for(int i = 0; i < 3; i++){
			
			try{
				VSIUtils.invokeService(env, "VSIWholesaleSalesAuditEmail", inXML);
				emailSent = true;
			}catch(Exception e){
				log.error("Exception in VSIWholesaleSalesAuditEmail.sendSalesAuditEmail() : " + e.getMessage());
				e.printStackTrace();
			}
			if(emailSent){
				break;
			}
		}
		
		log.endTimer("VSIWholesaleSalesAuditEmail.VSIWholesaleSalesAuditEmail()");
		return inXML;
	}

}
