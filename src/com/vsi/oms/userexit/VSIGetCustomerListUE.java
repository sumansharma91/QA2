package com.vsi.oms.userexit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.ue.YCPGetCustomerListUE;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author Perficient Inc.
 * 
 *         get Customer List UE
 *
 */

public class VSIGetCustomerListUE implements YCPGetCustomerListUE {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetCustomerListUE.class);
	YIFApi api;

	/**
	 * getCustomerList - If the CustomerID starts with S, call the OMS. - If the
	 * CustomerID doesn't start with S, call the CRM.
	 * 
	 * @param env
	 * @param inXML
	 * @throws YFSUserExitException
	 * @return outdoc - Returns CustomerList For any exceptions or CRM call
	 *         fails, Returns <CustomerList CRM_Fail="Y"/> to handle errors in
	 *         UI.
	 */
	public Document getCustomerList(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {

		Document outdoc = null;
		if(log.isDebugEnabled()){
			log.info("================Inside vsiGetCustomerList================================");
		}
		// log.debug("Printing Input XML :" + XmlUtils.getXMLString(docInput));
		Element eleInput = inXML.getDocumentElement();

		String strCustomerID = eleInput
				.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
		String strCustomerKey = eleInput
				.getAttribute(VSIConstants.ATTR_CUST_KEY);

		YFCElement eleCustomer = YFCDocument.getDocumentFor(inXML)
				.getDocumentElement();
		YFCElement eleCustomerContactList = eleCustomer.getChildElement(
				VSIConstants.ELE_CUST_CONTACT_LIST, true);
		YFCElement eleCustomerContact = eleCustomerContactList.getChildElement(
				VSIConstants.ELE_CUST_CONTACT, true);

		if (!YFCObject.isVoid(strCustomerID)) {
			eleCustomerContact.setAttribute(
					VSIConstants.ATTR_HEALTHY_AWARDS_NO, strCustomerID);
		} else {
			eleCustomerContact.setAttribute(
					VSIConstants.ATTR_HEALTHY_AWARDS_NO, "");
		}

		if (YFCObject.isVoid(strCustomerKey)) {

			// If CustID starts with S -> CALL TO OMS
			if (!YFCObject.isVoid(strCustomerID)
					&& strCustomerID.startsWith("S")) {

				Document docGetCustList = SCXmlUtil
						.createDocument(VSIConstants.ELE_CUSTOMER);
				Element eleGetCustList = docGetCustList.getDocumentElement();
				// Change OrganizationCode = 'DEFAULT'
				eleGetCustList.setAttribute(VSIConstants.ATTR_ORG_CODE,
						VSIConstants.ATTR_DEFAULT);
				eleGetCustList.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,
						strCustomerID);

				try {
					outdoc = VSIUtils.invokeAPI(env,
							VSIConstants.TEMPLATE_VSI_GET_CUSTOMER_LIST,
							VSIConstants.API_GET_CUSTOMER_LIST, docGetCustList);

				} catch (Exception e) {
					outdoc = SCXmlUtil
							.createDocument(VSIConstants.ELE_CUST_LIST);
				}
			}

			// If CustID doesn't starts with S -> CALL TO CRM
			else {
				try {
					api = YIFClientFactory.getInstance().getApi();
				} catch (YIFClientCreationException e1) {
					e1.printStackTrace();
				}
				try {
					outdoc = api.executeFlow(env, "VSICustomerService", inXML);
					// START - Fix for SU-59
					NodeList nlCustomerContact = outdoc
							.getElementsByTagName("CustomerContact");
					for (int i = 0; i < nlCustomerContact.getLength(); i++) {
						Element elementCustomerContact = (Element) nlCustomerContact
								.item(i);
						String strEmailID = elementCustomerContact
								.getAttribute("EmailID");
						elementCustomerContact.setAttribute("EMailID",
							strEmailID);
						NodeList nlCustomerAdditionalAddress = elementCustomerContact
								.getElementsByTagName("CustomerAdditionalAddress");
						for (int j = 0; j < nlCustomerAdditionalAddress
								.getLength(); j++) {
							Element eleCustomerAdditionalAddress = (Element) nlCustomerAdditionalAddress
									.item(j);
							Element elePersonInfo = (Element) eleCustomerAdditionalAddress
									.getElementsByTagName("PersonInfo").item(0);
							String strEmailIDJ = elePersonInfo
									.getAttribute("EmailID");
							elePersonInfo.setAttribute("EMailID", strEmailIDJ);
						}
					}
					// END - Fix for SU-59
					Element eleCustList = outdoc.getDocumentElement();
					Element eleCust = SCXmlUtil.getChildElement(eleCustList,
							VSIConstants.ELE_CUSTOMER);

					boolean crmFlag = eleCustList.getAttribute("CRM_FAIL")
							.equals("Y");

					// Returns <CustomerList CRM_Fail="Y"/> to handle errors in
					// UI when CRM Fails
					if (crmFlag) {
						outdoc = SCXmlUtil.createDocument("CustomerList");
						Element eleOutput = outdoc.getDocumentElement();
						eleOutput.setAttribute("CRM_Fail", "Y");
						return outdoc;
					}

					if (YFCObject.isVoid(eleCust)) {
						outdoc = SCXmlUtil
								.createDocument(VSIConstants.ELE_CUST_LIST);
					} else {
						/*
						 * If call passes but returns no customer found with
						 * this output: <CustomerList> <Customer/>
						 * </CustomerList>
						 */
						if (!eleCust.hasChildNodes()) {
							// returns <CustomerList/> to handle show no
							// customer available in UI
							outdoc = SCXmlUtil
									.createDocument(VSIConstants.ELE_CUST_LIST);
						}
					}

				} catch (Exception e) { // RemoteException
					// When CRM call failed -> CRM_Fail=Y will be used to
					// display error on UI
					outdoc = SCXmlUtil.createDocument("CustomerList");
					Element eleOutput = outdoc.getDocumentElement();
					eleOutput.setAttribute("CRM_Fail", "Y");
				}
			}
		}
		return outdoc;

	} // End of getCustomerList method

} // End of VSIGetCustomerListUE Class
