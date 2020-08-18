package com.vsi.oms.userexit;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetFundsAvailableUE;


public class VSIPaymentSVCFund implements YFSGetFundsAvailableUE {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIPaymentSVCFund.class);

	public Document getFundsAvailable(YFSEnvironment env, Document inXML) 
	throws YFSUserExitException {
		
		if(log.isDebugEnabled()){
    		log.debug("YFSGetFundsAvailableUE input is " + XMLUtil.getXMLString(inXML));
		}
		Document outXML=null;
		try {
			String strFundsAvailable = "0.00";
			Double strPaymentReference3 = 0.00;
			Double strMaxChargeLimit = 0.00;
			
			strMaxChargeLimit = Double.parseDouble(inXML.getDocumentElement().getAttribute("MaxChargeLimit"));
			if(!YFCObject.isVoid(inXML.getDocumentElement().getAttribute("PaymentReference3"))){
				strPaymentReference3 = Double.parseDouble(inXML.getDocumentElement().getAttribute("PaymentReference3"));
			}
			//System.out.println("Charge Limit is " + strMaxChargeLimit
					//+ ", RequestedAmount is " + strPaymentReference3);
			if(strMaxChargeLimit < strPaymentReference3){
				strFundsAvailable = Double.toString(strPaymentReference3);
			}else{
				strFundsAvailable = Double.toString(strMaxChargeLimit);
			}
			outXML = XMLUtil.createDocument("PaymentMethod");
			outXML.getDocumentElement().setAttribute("FundsAvailable", strFundsAvailable);
			if(log.isDebugEnabled()){
	    		log.debug("YFSGetFundsAvailableUE out XML is " + XMLUtil.getXMLString(outXML));
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return outXML;
	}

}
