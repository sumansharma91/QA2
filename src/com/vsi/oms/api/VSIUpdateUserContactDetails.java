package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class shall be used to user synchronization Input xml:
 * 
 * 
 */
public class VSIUpdateUserContactDetails implements  YIFCustomApi {

	/**
	 * LoggerUtil Instance.
	 */
	YIFApi api = null;
	private YFCLogCategory logger = YFCLogCategory
			.instance(VSIUpdateUserContactDetails.class);

	
	public Document massageoutput(YFSEnvironment env,Document inDocXML) {
		logger.timer("Begin Timer for " + this.getClass() + ".updatedCustomeroutput");
		if(logger.isDebugEnabled()){
			logger.verbose("Input Document: \n" + XMLUtil.getXMLString(inDocXML));
		}
				
		Element eleInXML= inDocXML.getDocumentElement();
		Element custContList = SCXmlUtil.getChildElement(eleInXML,VSIConstants.ELE_CUST_CONTACT_LIST);
		if(!YFCCommon.isVoid(custContList))
		{
			eleInXML.removeChild(custContList);
		}
		Element eleCustomerContactList = XMLUtil.appendChild(inDocXML, eleInXML, VSIConstants.ELE_CUST_CONTACT_LIST, null);
		Element eleCustomerContact =  XMLUtil.appendChild(inDocXML, eleCustomerContactList, VSIConstants.ELE_CUST_CONTACT, null);
		XMLUtil.appendChild(inDocXML, eleCustomerContact, VSIConstants.ELE_CUST_ADDL_ADDR_LIST, null);
		Element eleBuyerOrganization = XMLUtil.appendChild(inDocXML, eleInXML, VSIConstants.ELE_BUYER_ORGANIZATION, null);
		
		Document docBuyerOrg=null;
		try {
			docBuyerOrg = XMLUtil.createDocument(VSIConstants.ELE_ORGANIZATION);
		} catch (ParserConfigurationException e) {
			logger.error(e);
		}
		Element eleBuyerOrg = null;
		eleBuyerOrg =	docBuyerOrg.getDocumentElement();
		eleBuyerOrg.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.VSICOM_ENTERPRISE_CODE);
		
		try {
			// Call getOrganizationHierarchy
			docBuyerOrg = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORG_HIER_CUST_COND,
					VSIConstants.API_GET_ORG_HIERARCHY, docBuyerOrg);
			eleBuyerOrg =	docBuyerOrg.getDocumentElement();
			SCXmlUtil.setAttributes(eleBuyerOrg, eleBuyerOrganization);
			
		} catch (YFSException e) {
			logger.error(e);
		} catch (RemoteException e) {
			logger.error(e);
		} catch (YIFClientCreationException e) {
			logger.error(e);
		}
		
		return inDocXML;

	}
	
		
	@Override
	public void setProperties(Properties properties) throws Exception {
		
	}


}