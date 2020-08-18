package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendJDACnclMsg implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSISendJDACnclMsg.class);
	
	public void sendJDACnclMsg(YFSEnvironment env, Document docInput) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		log.beginTimer("VSISendJDACnclMsg.sendJDACnclMsg() : START");
		if(log.isDebugEnabled()){
			log.debug("Input document:\n" + SCXmlUtil.getString(docInput));
		}
		Document docJDACnclMsg = (Document) env.getTxnObject(JDA_CNCL_MSG);
		if (!YFCObject.isVoid(docJDACnclMsg)) {
			if(log.isDebugEnabled()){
				log.debug("\nIncoming JDA Cancel Message: " + SCXmlUtil.getString(docJDACnclMsg));
			}
			
			Element eleOrder = docInput.getDocumentElement();
			Element elePriceInfo = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PRICE_INFO);
			double dCustCredit = Math.abs(SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_CHANGE_IN_TOTAL_AMOUNT));
			double dNewOrderTotal = SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT);
			double dOriginalPrice = dCustCredit + dNewOrderTotal;
			
			Element eleJDACnclMsg = SCXmlUtil.getChildElement(docJDACnclMsg.getDocumentElement(), ELE_ORDER);
			Element eleJDAPriceInfo = SCXmlUtil.getChildElement(eleJDACnclMsg, ELE_PRICE_INFO);
			SCXmlUtil.setAttribute(eleJDAPriceInfo, VSIConstants.ATTR_PREVIOUS_ORDER_TOTAL, dOriginalPrice);
			SCXmlUtil.setAttribute(eleJDAPriceInfo, VSIConstants.ATTR_NEW_ORDER_TOTAL, dNewOrderTotal);
			SCXmlUtil.setAttribute(eleJDAPriceInfo, VSIConstants.ATTR_CUSTOMER_CREDIT, dCustCredit);
			
			try {
				VSIUtils.invokeService(env, VSI_CNCL_PUBLISH_TO_JDA, docJDACnclMsg);
			} catch (Exception e) {
				log.error("Error while invoking VSICancelPublishToJDA from VSISendJDACnclMsg.sendJDACnclMsg()", e);
				throw VSIUtils.getYFSException(e);
			}
		}
		
		log.endTimer("VSISendJDACnclMsg.sendJDACnclMsg() : END");
	}

}
