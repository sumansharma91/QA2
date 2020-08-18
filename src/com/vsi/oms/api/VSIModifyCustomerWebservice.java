package com.vsi.oms.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIModifyCustomerWebservice implements YIFCustomApi {
	
	Document srchResponse=null;
	int iretryCount = 0;
	
	/**
	 * 
	 *  When CRM is available, updates the customer data in CRM database.
	 *  When CRM is unavailable, updates the customer data in OMS database.
	 * 
	 * @param env
	 * @param inXML - incoming document for the saveCustomer method
	 * @return inXML - modified inXML document 
	 * 
	 * @throws SocketTimeoutException
	 * @throws UnrecoverableKeyException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document saveCustomer(YFSEnvironment env, Document inXML) throws SocketTimeoutException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
			
		YIFApi api=null;		
		YFCLogCategory log = YFCLogCategory.instance(VSIModifyCustomerWebservice.class);		
		if(log.isDebugEnabled()){
			log.debug("VSIModifyCustomerWebservice: saveCustomer method :- Begins Input XML is: \n "+XMLUtil.getXMLString(inXML));
		}
		Document outdoc = null;
			
		try {
			
			Element eleInput = inXML.getDocumentElement();
			String strCustomerID = eleInput.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
            //OMS-1683 start This change for adding userId on notes tab
			String strUserId=env.getUserId();
			
			Element eleNotes = SCXmlUtil.getChildElement(eleInput, VSIConstants.ELE_NOTE_LIST);
		    if(!YFCObject.isVoid(eleNotes)){
			ArrayList<Element> arrNoteList = SCXmlUtil.getChildren(eleNotes, VSIConstants.ELE_NOTE);
			String strNoteText=null;
			String strNewNoteText=null;
			for (Element eleNote : arrNoteList) {
				 strNoteText = eleNote.getAttribute(VSIConstants.ATTR_NOTE_TEXT);
				 strNewNoteText=strUserId  + " added Note:  " +strNoteText;
				eleNote.setAttribute(VSIConstants.ATTR_NOTE_TEXT, strNewNoteText);
				
			}
		   }
			//OMS-1683 End
			// If Customer ID starts with S, update OrganizationCode = "DEFAULT", call manageCustomer.
			if (!YFCObject.isVoid(strCustomerID) && strCustomerID.startsWith("S")) {

				// Updates OrganizationCode = 'DEFAULT & Creating input XML for API call'
				eleInput.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
				eleInput.setAttribute(VSIConstants.ATTR_OPERATION, VSIConstants.ATTR_OPERATION_VALUE_MANAGE);
				eleInput.setAttribute(VSIConstants.ATTR_BUYER_ORG_CODE, VSIConstants.VSICOM_ENTERPRISE_CODE);

				// Call manageCustomer
				outdoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_MANAGE_CUSTOMER_VSIManageCustomerUE,
						VSIConstants.API_MANAGE_CUST, inXML);
			} 
			
			else {
				//Calls CRM VSIModifyCustomer WebService
				api = YIFClientFactory.getInstance().getApi();
				
				//START - 1B CRM Enhancements
				VSICreateSaveNewCustomerWebservice vsiNewCustomerWebservice = new VSICreateSaveNewCustomerWebservice(); 
				vsiNewCustomerWebservice.manageMultipleAddresses(inXML);
				//END - 1B CRM Enhancements
				
				Document soapCallinpudoc=api.executeFlow(env,VSIConstants.SERVICE_MODIFY_CUSTOMER_INPUT, inXML);
				boolean flag = false;
				Element eleRoot = soapCallinpudoc.getDocumentElement();
				if(eleRoot.getElementsByTagName("customerRemark") != null 
						&& eleRoot.getElementsByTagName("customerRemark").getLength() > 0){
					Element eleCustomerRemark = (Element) eleRoot.getElementsByTagName("customerRemark").item(0);
					Element eleRemark = (Element) eleCustomerRemark.getElementsByTagName("Remark").item(0);
					if(eleRemark != null && YFCObject.isVoid(eleRemark.getNodeValue())){
						eleRemark.setNodeValue("&emsp;");
						flag = true;
					}
		  		}
//				System.out.println("soapCallinpudoc"+SCXmlUtil.getString(soapCallinpudoc));
				if(flag){
					srchResponse=InvokeWebservice.invokeModifyCustomer(soapCallinpudoc,flag);
				}else{
					srchResponse=InvokeWebservice.invokeModifyCustomer(soapCallinpudoc);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			 String _xml=null;
			if(e instanceof SocketTimeoutException)
			{
				if(iretryCount < 3){
					
					if(log.isDebugEnabled()){
						log.debug("** Printing retry count : "+iretryCount);
					}
					iretryCount ++;
					Document soapCallinpudoc=api.executeFlow(env, VSIConstants.SERVICE_MODIFY_CUSTOMER_INPUT, inXML);
					srchResponse=InvokeWebservice.invokeModifyCustomer(soapCallinpudoc);
					
					//System.out.println("Request to ISE Save:" + soapCallinpudoc);
					//System.out.println("Response from ISE Save:" + srchResponse);
					}				
			}
			
		YFCDocument ifcallFailed=YFCDocument.getDocumentFor("<Customer IsCallSuccessfull=\"N\" />");
//		return ifcallFailed.getDocument(); 
		}
		
		 if(srchResponse != null){
			 return inXML;
		 }
		
		 if(log.isDebugEnabled()){
			 log.debug("VSIModifyCustomerWebservice: saveCustomer method :- Ends");
		 }
		return inXML;		
	} // End of saveCustomer method
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub		
	}
} //End of VSIModifyCustomerWebservice Class