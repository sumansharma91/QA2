package com.vsi.oms.condition;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.*;

import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIDynamicConditionHealthyrewards implements YCPDynamicCondition{
	
	
	

	YIFApi api;
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
	
		boolean old = true;
		try{
			String val="";
			YFCDocument inputXML = YFCDocument.getDocumentFor(inXML);
			YFCElement customer = inputXML.getDocumentElement().getChildElement("CustomerContactList").getChildElement("CustomerContact");
			if(customer.hasAttribute("HealthyAwardsNo") || null==customer.getAttribute("HealthyAwardsNo"))
			{
				
				String hr=customer.getAttribute("HealthyAwardsNo");
				if(hr.trim()!=val)
					old=false;
			}
		} catch (Exception e){
		e.printStackTrace();

	}
		return old;
		
	}

}
