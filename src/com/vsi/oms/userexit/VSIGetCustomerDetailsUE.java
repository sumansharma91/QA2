package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.ue.YCPGetCustomerDetailsUE;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.shared.ycp.YFSContext;
/**
 * @author Perficient Inc.
 * 
 *         get Customer Details UE
 *
 */
public class VSIGetCustomerDetailsUE implements YCPGetCustomerDetailsUE {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetCustomerDetailsUE.class);
	YIFApi api;

	/**
	 * getCustomerDetails - If the CustomerID starts with S, call the OMS. - If
	 * the CustomerID doesn't start with S, call the CRM.
	 * 
	 * @param env
	 * @param inXML
	 * @throws YFSUserExitException
	 * @return outdoc - Returns CustomerDetails For any exceptions, returns an
	 *         empty Customer.
	 */
	public Document getCustomerDetails(YFSEnvironment env, Document docInput)
			throws YFSUserExitException {

		if(log.isDebugEnabled()){
			log.info("================Inside vsiGetCustomerDetails================================");
		}
		// log.debug("Printing Input XML :" + XmlUtils.getXMLString(docInput));
		String progId = env.getProgId();
		if(log.isDebugEnabled()){
		log.debug("Printing Input XML :" +progId);
		}
		String strEntID = ((YFSContext)env).getUEParam("EnterpriseCode");
		String strCallName = ((YFSContext)env).getCallName();
		if(log.isDebugEnabled()){
		log.debug("EnterpriseCode"+strEntID);
		log.debug("strCallName"+strCallName);
		}
		Element eleInput = docInput.getDocumentElement();

		String strRootEle = eleInput.getNodeName();
		String strCustomerKey = eleInput
				.getAttribute(VSIConstants.ATTR_CUST_KEY);
		String strCustomerID = eleInput
				.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
		Document docOutput=null;
		if(YFCCommon.isVoid(strCustomerID) || "VSI.com".equals(strCustomerID)  ||  "VSI".equals(strCustomerID))
		{
			if(log.isDebugEnabled()){
			log.debug("CustomerID is blank or VSI.com or VSI for api call"+strCallName);
			}
			try {
			docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
			}
			catch (Exception e) {
				log.error("Exception in VSIGetUserContactDetailsSyncService invokation: " , e);
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
				"VSIGetUserContactDetailsSyncService Invokation failed");
			}
			
			return docOutput;
		}
		else
		{
			//Set<String> setRestrictedApiList=null;
			String strNewChangeReqd=null;
	
			ArrayList<Element> listCommonCode;
			Element eleCommonCode=null;
			try {
				listCommonCode = VSIUtils.getCommonCodeList(env, "CUSTOMER_DTL_UE","CustomerDtlUENewDesign",
						"DEFAULT");
				eleCommonCode= listCommonCode.get(0);
				if (!YFCCommon.isVoid(eleCommonCode)) {
					strNewChangeReqd=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);	
			
				} 
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			
			
			if (!YFCObject.isVoid(strNewChangeReqd) && "Y".equals(strNewChangeReqd))
			{	
				if(log.isDebugEnabled()){
					log.debug("Executing New Logic");
				}
			Set<String> setAllowedApiList=null;
			try {
				//setRestrictedApiList=fetchRestrictedApiList(env);
				setAllowedApiList=fetchAllowedApiList(env);
			} catch (YFSException | RemoteException | ParserConfigurationException | YIFClientCreationException  e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if(log.isDebugEnabled()){
			log.debug("Set is updated");
			}
			if(!YFCObject.isVoid(setAllowedApiList) && setAllowedApiList.contains(strCallName))
			{
				if(log.isDebugEnabled()){
					log.debug("External CRM call is allowed for api"+strCallName);
				}
		if(YFCObject.isVoid(strCustomerID) && !YFCObject.isVoid(strCustomerKey)){
			strCustomerID = strCustomerKey;
		}
		YFCElement eleCustomerInput = YFCDocument.getDocumentFor(docInput)
				.getDocumentElement();
		YFCElement eleCustomerContactList = eleCustomerInput.getChildElement(
				VSIConstants.ELE_CUST_CONTACT_LIST, true);
		YFCElement eleCustomerContact = eleCustomerContactList.getChildElement(
				VSIConstants.ELE_CUST_CONTACT, true);

		if (!YFCObject.isVoid(strCustomerID)) {
			eleCustomerContact.setAttribute(
					VSIConstants.ATTR_HEALTHY_AWARDS_NO, strCustomerID);
		} 
		//START - Added to pass HAN
		else {
			eleCustomerContact.setAttribute(
					VSIConstants.ATTR_HEALTHY_AWARDS_NO, "");
		}
		//END - Added to pass HAN

		// If CustID starts with S -> CALL TO OMS
				//Document docOutput = null;
		if (!YFCObject.isVoid(strCustomerID) && strCustomerID.startsWith("S")) {

			Document docGetCustDetails = SCXmlUtil
					.createDocument(VSIConstants.ELE_CUSTOMER);
			Element eleGetCustDetails = docGetCustDetails.getDocumentElement();
			// Change OrganizationCode = 'DEFAULT'
			eleGetCustDetails.setAttribute(VSIConstants.ATTR_ORG_CODE,
					VSIConstants.ATTR_DEFAULT);
			eleGetCustDetails.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,
					strCustomerID);

			try {
				docOutput = VSIUtils.invokeAPI(env,
						VSIConstants.TEMPLATE_VSI_GET_CUSTOMER_DETAILS,
						VSIConstants.API_GET_CUSTOMER_DETAILS,
						docGetCustDetails);
			} catch (Exception e) {
				if("Y".equals(env.getTxnObject("MIGRATION_ORDER_FLOW"))){
					// If customer information is unavailable, return dummy data
					try {
						docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}else{
					docOutput = SCXmlUtil.createDocument(VSIConstants.ELE_CUSTOMER);
				}
			}
		}

		// If CustID doesn't starts with S -> CALL TO CRM
		else {
			//START - Modified for ARR-9
			try {
				// Get customer information
				Document outdoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_CUSTOMER_SEARCH, docInput);
				if(log.isDebugEnabled()){
					log.debug("Outdoc XML to COM API is: " + SCXmlUtil.getString(outdoc));
				}
				
				if(!YFCObject.isVoid(outdoc)){
					Element eleCustomer = (Element) outdoc.getDocumentElement().getElementsByTagName(VSIConstants.ELE_CUSTOMER).item(0);
					if(!YFCObject.isVoid(eleCustomer)){
						if(!YFCObject.isVoid(strCustomerID)
								&& !eleCustomer.getAttribute(VSIConstants.ATTR_IS_CUST_AVAIL).equals(VSIConstants.FLAG_N)) {
							// If customer information is available, return the information
							String strCustomerIDOut = eleCustomer.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
							eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_KEY, strCustomerIDOut);
							String strXML = XMLUtil.getElementXMLString(eleCustomer);
							if(log.isDebugEnabled()){
								log.debug("Input XML to COM API is " + strXML);
							}
							docOutput = YFCDocument.getDocumentFor(strXML).getDocument();
							docOutput.getDocumentElement().setAttribute("CustomerID", strCustomerID);
							docOutput.getDocumentElement().setAttribute("CustomerKey", strCustomerID);
						} else {
							// If customer information is unavailable, return dummy data
							docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
						}
					} else {
						// If customer information is unavailable, return dummy data
						docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
					}
				}
			} catch (RemoteException re) {
				log.error("RemoteException in ProcessShipConfirmFromWMS.processShipConfirmFromWMS() : " , re);
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
				"Customer No. Not found");
			} catch (Exception e) {
				log.error("RemoteException in ProcessShipConfirmFromWMS.processShipConfirmFromWMS() : " , e);
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
				"Customer No. Not found");
			}
			//END - Modified for ARR-9
		}
		
		//START - Fix for SU-59
		NodeList nlCustomerContact = docOutput.getElementsByTagName("CustomerContact");
		for(int i=0;i<nlCustomerContact.getLength();i++){
			Element elementCustomerContact = (Element) nlCustomerContact.item(i);
			String strEmailID = elementCustomerContact.getAttribute("EmailID");
			elementCustomerContact.setAttribute("EMailID", strEmailID);
			NodeList nlCustomerAdditionalAddress = elementCustomerContact.getElementsByTagName("CustomerAdditionalAddress");
			for(int j=0;j<nlCustomerAdditionalAddress.getLength();j++){
				Element eleCustomerAdditionalAddress = (Element) nlCustomerAdditionalAddress.item(j);
				Element elePersonInfo = (Element) eleCustomerAdditionalAddress.getElementsByTagName("PersonInfo").item(0);
				String strEmailIDJ = elePersonInfo.getAttribute("EmailID");
				elePersonInfo.setAttribute("EMailID", strEmailIDJ);
			}
		}
		//END - Fix for SU-59
				return docOutput;
			}
			else {
				
				if(log.isDebugEnabled()){
				log.debug("External CRM call is not allowed for api"+strCallName);
				}
				try {
			docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
					e.printStackTrace();
					throw new YFSException(
							"EXTN_ERROR",
							"EXTN_ERROR",
					"Customer No. Not found");
				}
					return docOutput;
			}
		}
			else
			{
				if(log.isDebugEnabled()){
				log.debug("Executing Old Logic");
				}
				if(YFCObject.isVoid(strCustomerID) && !YFCObject.isVoid(strCustomerKey)){
					strCustomerID = strCustomerKey;
				}
				YFCElement eleCustomerInput = YFCDocument.getDocumentFor(docInput)
						.getDocumentElement();
				YFCElement eleCustomerContactList = eleCustomerInput.getChildElement(
						VSIConstants.ELE_CUST_CONTACT_LIST, true);
				YFCElement eleCustomerContact = eleCustomerContactList.getChildElement(
						VSIConstants.ELE_CUST_CONTACT, true);
				if (!YFCObject.isVoid(strCustomerID)) {
					eleCustomerContact.setAttribute(
							VSIConstants.ATTR_HEALTHY_AWARDS_NO, strCustomerID);
				} 
				//START - Added to pass HAN
				else {
					eleCustomerContact.setAttribute(
							VSIConstants.ATTR_HEALTHY_AWARDS_NO, "");
				}
				//END - Added to pass HAN

				// If CustID starts with S -> CALL TO OMS
				
				if (!YFCObject.isVoid(strCustomerID) && strCustomerID.startsWith("S")) {

					Document docGetCustDetails = SCXmlUtil
							.createDocument(VSIConstants.ELE_CUSTOMER);
					Element eleGetCustDetails = docGetCustDetails.getDocumentElement();
					// Change OrganizationCode = 'DEFAULT'
					eleGetCustDetails.setAttribute(VSIConstants.ATTR_ORG_CODE,
							VSIConstants.ATTR_DEFAULT);
					eleGetCustDetails.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,
							strCustomerID);

					try {
						docOutput = VSIUtils.invokeAPI(env,
								VSIConstants.TEMPLATE_VSI_GET_CUSTOMER_DETAILS,
								VSIConstants.API_GET_CUSTOMER_DETAILS,
								docGetCustDetails);
					} catch (Exception e) {
						if("Y".equals(env.getTxnObject("MIGRATION_ORDER_FLOW"))){
							// If customer information is unavailable, return dummy data
							try {
								docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}else{
							docOutput = SCXmlUtil.createDocument(VSIConstants.ELE_CUSTOMER);
						}
					}
				}

				// If CustID doesn't starts with S -> CALL TO CRM
				else {
					//START - Modified for ARR-9
					try {
						// Get customer information
						Document outdoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_CUSTOMER_SEARCH, docInput);
						if(log.isDebugEnabled()){
							log.debug("Outdoc XML to COM API is: " + SCXmlUtil.getString(outdoc));
						}

						if(!YFCObject.isVoid(outdoc)){
							Element eleCustomer = (Element) outdoc.getDocumentElement().getElementsByTagName(VSIConstants.ELE_CUSTOMER).item(0);
							if(!YFCObject.isVoid(eleCustomer)){
								if(!YFCObject.isVoid(strCustomerID)
										&& !eleCustomer.getAttribute(VSIConstants.ATTR_IS_CUST_AVAIL).equals(VSIConstants.FLAG_N)) {
									// If customer information is available, return the information
									String strCustomerIDOut = eleCustomer.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
									eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_KEY, strCustomerIDOut);
									String strXML = XMLUtil.getElementXMLString(eleCustomer);
									if(log.isDebugEnabled()){
										log.debug("Input XML to COM API is " + strXML);
									}
									docOutput = YFCDocument.getDocumentFor(strXML).getDocument();
									docOutput.getDocumentElement().setAttribute("CustomerID", strCustomerID);
									docOutput.getDocumentElement().setAttribute("CustomerKey", strCustomerID);
								} else {
									// If customer information is unavailable, return dummy data
									docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
								}
							} else {
								// If customer information is unavailable, return dummy data
								docOutput = VSIUtils.invokeService(env, "VSIGetUserContactDetailsSyncService", docInput);
							}
						}
					} catch (RemoteException re) {
						log.error("RemoteException in ProcessShipConfirmFromWMS.processShipConfirmFromWMS() : " , re);
						throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
						"Customer No. Not found");
					} catch (Exception e) {
						log.error("RemoteException in ProcessShipConfirmFromWMS.processShipConfirmFromWMS() : " , e);
						throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
						"Customer No. Not found");
					}
					//END - Modified for ARR-9
				}
				
				//START - Fix for SU-59
				NodeList nlCustomerContact = docOutput.getElementsByTagName("CustomerContact");
				for(int i=0;i<nlCustomerContact.getLength();i++){
					Element elementCustomerContact = (Element) nlCustomerContact.item(i);
					String strEmailID = elementCustomerContact.getAttribute("EmailID");
					elementCustomerContact.setAttribute("EMailID", strEmailID);
					NodeList nlCustomerAdditionalAddress = elementCustomerContact.getElementsByTagName("CustomerAdditionalAddress");
					for(int j=0;j<nlCustomerAdditionalAddress.getLength();j++){
						Element eleCustomerAdditionalAddress = (Element) nlCustomerAdditionalAddress.item(j);
						Element elePersonInfo = (Element) eleCustomerAdditionalAddress.getElementsByTagName("PersonInfo").item(0);
						String strEmailIDJ = elePersonInfo.getAttribute("EmailID");
						elePersonInfo.setAttribute("EMailID", strEmailIDJ);
					}
				}
				//END - Fix for SU-59

				return docOutput;
			}
		}		
	}// End of getCustomerDetails method
	/* This method has been used to call getCommonCodeList api to get the list of api 
	 * for which external getCustomerDetils CRM webservice call needs to be invoked.
	 * 
	 * 
	 */
	private Set<String> fetchAllowedApiList(YFSEnvironment env)  throws ParserConfigurationException, 
	YFSException, RemoteException, YIFClientCreationException {
		HashSet<String> setAllowedApiList=new HashSet<String>();
		Document docInput=XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
		Element eleCommonCode = docInput.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_COM_CALL_API");
		if(log.isDebugEnabled()){
			log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
		}
		Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInput);
		if(log.isDebugEnabled()){
			log.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
		}
		NodeList nlCommonCode = docCommonCodeListOP
				.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);
		int iCount=nlCommonCode.getLength();
		for (int i = 0; i < iCount; i++) {
			Element eleCommonCodeOutput=((Element) nlCommonCode.item(i));
			String strCodeValue=eleCommonCodeOutput.getAttribute(VSIConstants.ATTR_CODE_VALUE);
			setAllowedApiList.add(strCodeValue);
		}
		return setAllowedApiList;
	}
} // End of VSIGetCustomerDetails Class
