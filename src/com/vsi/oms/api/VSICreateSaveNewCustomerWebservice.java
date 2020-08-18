package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICreateSaveNewCustomerWebservice implements YIFCustomApi {
	
	/**
	 *  When CRM is available, creates customer in CRM database.
	 *  When CRM is unavailable, creates customer in OMS DB and raises an alert on the order.
	 * 
	 * @param env
	 * @param inXML - incoming document for the createCustomer method
	 * @return
	 * @throws Exception
	 */
	public Document createCustomer(YFSEnvironment env, Document inXML) throws Exception {
		
		YIFApi api = null;
		YFCLogCategory log = YFCLogCategory.instance(VSICreateSaveNewCustomerWebservice.class);
		if(log.isDebugEnabled()){
			log.debug("VSICreateSaveNewCustomerWebservice: createCustomer method :- Begins Input XML is: \n "+XMLUtil.getXMLString(inXML));
			log.verbose("VSICreateSaveNewCustomerWebservice: createCustomer method :- Begins Input XML is: \n "+XMLUtil.getXMLString(inXML));
		}
		
		boolean getNewCode = true;
		Document srchOut = null;
		String sequence = null;

		if (getNewCode) {
			YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
			Element eleCustomerContacts = (Element)inXML.getElementsByTagName(VSIConstants.ELE_CUST_CONTACT).item(0);
			String strEmailID = eleCustomerContacts.getAttribute(VSIConstants.ATTR_EMAIL_ID);
			//preparing findCustomer input
			Document docFindCustomer = SCXmlUtil.createDocument(VSIConstants.ELE_CUSTOMER);
			Element eleCustomer = docFindCustomer.getDocumentElement();
			eleCustomer.setAttribute("ApplyQueryTimeout", "Y");
			eleCustomer.setAttribute("CallingOrganizationCode", "");
			eleCustomer.setAttribute("CustomerType", "02");
			eleCustomer.setAttribute("DisplayLocalizedFieldInLocale", "en_US_EST");
			eleCustomer.setAttribute("MaximumRecords", "5000");
			eleCustomer.setAttribute("QueryTimeout", "60");
			Element eleCustomerContactList = SCXmlUtil.createChild(eleCustomer,"CustomerContactList");
			Element eleCustomerContact = SCXmlUtil.createChild(eleCustomerContactList,"CustomerContact");
			eleCustomerContact.setAttribute("EmailID", strEmailID);
			eleCustomerContact.setAttribute("HealthyAwardsNo", "");
			eleCustomerContact.setAttribute("EmailIDQryType", "FLIKE");
			if(log.isDebugEnabled()){
				log.debug(" *** Entering Method : docFindCustomer Input XML is: \n "+XMLUtil.getXMLString(docFindCustomer));
			}
			Document outdoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_CUSTOMER_SEARCH, docFindCustomer);
			if(log.isDebugEnabled()){
				log.debug(" *** Entering Method : outdoc of Find customer call  XML is: \n "+XMLUtil.getXMLString(outdoc));
			}
			Element eleoutdoc = outdoc.getDocumentElement();
			Element eleoutdoclist = (Element)outdoc.getElementsByTagName(VSIConstants.ELE_CUSTOMER).item(0);
			if(eleoutdoc.hasAttribute(VSIConstants.ATTR_IS_CUST_AVAIL)|| eleoutdoclist.hasAttribute(VSIConstants.ATTR_IS_CUST_AVAIL )){
			
			try {
				//START - Changes for multiple address addition
				manageMultipleAddresses(inXML);
				//Fix for OMS-1093 start
				NodeList nlCustomerAdditionalAddress = SCXmlUtil
						.getXpathNodes(
								inXML.getDocumentElement(),
								"/Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress");
				for (int i = 0; i < nlCustomerAdditionalAddress.getLength(); i++) {
					Element eleCustomerAdditionalAddress = (Element) nlCustomerAdditionalAddress
							.item(i);
					Element elePersonInfo = (Element) eleCustomerAdditionalAddress.getElementsByTagName(VSIConstants.ELE_PERSON_INFO).item(0);
					String strCountry=elePersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
					String sState=elePersonInfo.getAttribute(VSIConstants.ATTR_STATE);
					if(!strCountry.equals("USA")&&YFCObject.isVoid(sState)){
						elePersonInfo.setAttribute(VSIConstants.ATTR_STATE, strCountry);
						if(log.isDebugEnabled()){
							log.verbose("Printing elePersonInfo :" + SCXmlUtil.getString(elePersonInfo));
						}
		    		}
				}
				//Fix for OMS-1093 end
				api = YIFClientFactory.getInstance().getApi();
				Document soapCallinpudoc = api.executeFlow(env, VSIConstants.SERVICE_CREATE_CUSTOMER_INPUT, inXML);
				if(log.isDebugEnabled()){
		    		log.debug("soapCallinpudoc:::"+SCXmlUtil.getString(soapCallinpudoc));
				}
				srchOut = InvokeWebservice.invokeCreateCustomer(soapCallinpudoc);
			}
			catch (Exception e) {
				
				e.printStackTrace();
				// For CRM unavailable/Timeout/Exception scenarios, use customerID as 'S'+ Sequence number from VSI_CUSTOMER_ID,change organization code to 'DEFAULT', 
				//set ExternalCustomerID=0 and call manageCustomer to create customer in OMS DB.
				Document outputAPIManageCust = null;
				
				outputAPIManageCust = createSterlingCustomer(inXML,env);
				// ARE-502 : Added to raise CRM Issue alert on the order if CRM is unreachable
				createCRMDownAlert(env, inXML);
				
				return outputAPIManageCust;
					
			} 
			
						
			if (srchOut != null) {
				String customerNumber = YFCDocument.getDocumentFor(srchOut).getDocumentElement()
						.getChildElement("s:Body").getChildElement("SaveNewCustomerResponse")
						.getChildElement("parameters").getChildElement("b:CustomerNumber").getNodeValue();

				inDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_CUSTOMER_ID, customerNumber);
				inDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_CUST_KEY, customerNumber);
				return inXML;
			}
		}
			else{
				inDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_CUSTOMER_ID, "Error");
				return inXML;
			}
		}
		if(log.isDebugEnabled()){
			log.debug("VSICreateSaveNewCustomerWebservice: createCustomer method :- Ends");
			log.verbose("VSICreateSaveNewCustomerWebservice: createCustomer method :- Ends");
		}
		return inXML;
	} // End of createCustomer method

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
	}
	public void manageMultipleAddresses(Document inXML){
		boolean defaultExist = false;
		NodeList nlCustomerAdditionalAddress = SCXmlUtil
				.getXpathNodes(
						inXML.getDocumentElement(),
						"/Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress");
		for (int i = 0; i < nlCustomerAdditionalAddress.getLength(); i++) {
			Element eleCustomerAdditionalAddress = (Element) nlCustomerAdditionalAddress
					.item(i);
			Element elePersonInfo = (Element) eleCustomerAdditionalAddress.getElementsByTagName(VSIConstants.ELE_PERSON_INFO).item(0);
			String strIsBillTo = eleCustomerAdditionalAddress
					.getAttribute(VSIConstants.ATTR_IS_BILL_TO);
			String strIsDefaultBillTo = eleCustomerAdditionalAddress
					.getAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO);
			String strIsShipTo = eleCustomerAdditionalAddress
					.getAttribute(VSIConstants.ATTR_IS_SHIP_TO);
			String strIsDefaultShipTo = eleCustomerAdditionalAddress
					.getAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO);
			
			if(YFCObject.isVoid(strIsBillTo) 
					&& YFCObject.isVoid(strIsDefaultBillTo) 
					&& YFCObject.isVoid(strIsShipTo) 
					&& YFCObject.isVoid(strIsDefaultShipTo)){
				//System.out.println("Taking from personInfo");
				strIsBillTo = elePersonInfo
						.getAttribute(VSIConstants.ATTR_IS_BILL_TO);
				strIsDefaultBillTo = elePersonInfo
						.getAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO);
				strIsShipTo = elePersonInfo
						.getAttribute(VSIConstants.ATTR_IS_SHIP_TO);
				strIsDefaultShipTo = elePersonInfo
						.getAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO);
			}
			
			/*System.out.println(" strIsBillTo: " + strIsBillTo
					+ " strIsDefaultBillTo: " + strIsDefaultBillTo
					+ " strIsShipTo: " + strIsShipTo
					+ " strIsDefaultShipTo: " + strIsDefaultShipTo);*/

			//START - Setting DefaultShipTo = true and DefaultBillTo = true
			if (VSIConstants.FLAG_Y.equals(strIsBillTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultBillTo)
					&& VSIConstants.FLAG_Y.equals(strIsShipTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultShipTo)
					&& !defaultExist) {
				defaultExist = true;
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "IDST");
			}
			else if(VSIConstants.FLAG_Y.equals(strIsBillTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultBillTo)
					&& VSIConstants.FLAG_Y.equals(strIsShipTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultShipTo)
					&& defaultExist){
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "IDBT");
			}
			//END - Setting DefaultShipTo = true and DefaultBillTo = true
			//Setting DefaultBillTo = true
			else if(VSIConstants.FLAG_Y.equals(strIsBillTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultBillTo)){
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "IDBT");
			}
			//Setting DefaultShipTo = true
			else if(VSIConstants.FLAG_Y.equals(strIsShipTo)
					&& VSIConstants.FLAG_Y.equals(strIsDefaultShipTo)){
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "IDST");
			}
			//Setting BillTo = true
			else if(VSIConstants.FLAG_Y.equals(strIsBillTo)
					&& !VSIConstants.FLAG_Y.equals(strIsDefaultBillTo)){
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "BILL");
			}
			//Setting ShipTo = true
			else if(VSIConstants.FLAG_Y.equals(strIsShipTo)
					&& !VSIConstants.FLAG_Y.equals(strIsDefaultShipTo)){
				eleCustomerAdditionalAddress.setAttribute("AddressTypeCode", "SHIP");
			}
		}
	}
	public Document createSterlingCustomer(Document inXML,YFSEnvironment env) throws DOMException, Exception{

		Document outputAPIManageCust = null;
		Element eleCust = inXML.getDocumentElement();
		Element eleCustContactList = SCXmlUtil.getChildElement(eleCust, "CustomerContactList");
		Element eleCustContact = SCXmlUtil.getChildElement(eleCustContactList, "CustomerContact");
		Element eleCustomerExtn = SCXmlUtil.getChildElement(eleCust, "Extn");
		Element eleBuyerOrganization = SCXmlUtil.createChild(eleCust, "BuyerOrganization");
		eleCust.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
		eleCust.setAttribute(VSIConstants.ATTR_CUSTOMER_ID, VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CUST_ID));
		eleCust.setAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID, VSIConstants.INT_ZER0_NUM);
		eleCust.setAttribute(VSIConstants.ATTR_CUST_TYPE, VSIConstants.ATTR_CUSTOMER_TYPE_02);
		eleCust.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_10);
	
		eleBuyerOrganization.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
		eleBuyerOrganization.setAttribute("PrimaryEnterpriseKey", VSIConstants.ATTR_DEFAULT);
		
		if(eleCustContact.hasAttribute("ExtnGender")){
		String strExtnGender = eleCustContact.getAttribute("ExtnGender");
		eleCustContact.setAttribute("Title", strExtnGender);
		}
		//Title
		//TaxExemptionCode
		//PreferredCarrier
		
		Element eleCustAddAdressesList = (Element) inXML.getElementsByTagName(
				VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS_LIST).item(0);
		NodeList nlCustAddAddress = eleCustAddAdressesList
				.getElementsByTagName(VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS);
		for (int i = 0; i < nlCustAddAddress.getLength(); i++) {
			Element eleCustAddAddress = (Element) nlCustAddAddress.item(i);
				if(!YFCObject.isVoid(eleCustAddAddress)){
					Element elePersonInfo = SCXmlUtil.getChildElement(eleCustAddAddress, "PersonInfo");
					String strIsBillTo = elePersonInfo.getAttribute("IsBillTo");
					String strIsDefaultBillTo = elePersonInfo.getAttribute("IsDefaultBillTo");
					String strIsDefaultShipTo = elePersonInfo.getAttribute("IsShipTo");
					String strIsShipTo = elePersonInfo.getAttribute("IsDefaultShipTo");
					String strID = elePersonInfo.getAttribute("ID");
					
					eleCustAddAddress.setAttribute("IsBillTo", strIsBillTo);
					eleCustAddAddress.setAttribute("IsDefaultBillTo", strIsDefaultBillTo);
					eleCustAddAddress.setAttribute("IsShipTo", strIsDefaultShipTo);
					eleCustAddAddress.setAttribute("IsDefaultShipTo", strIsShipTo);
					elePersonInfo.setAttribute("PersonID", strID);
				}
		}
		
		outputAPIManageCust = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_MANAGECUSTOMER_CRMFAIL,VSIConstants.API_MANAGE_CUST, inXML);
		Element manageCustomerOutputCustEle = outputAPIManageCust.getDocumentElement();
		 if(!YFCObject.isVoid(manageCustomerOutputCustEle)){
			 Element extnEle = SCXmlUtil.getChildElement(manageCustomerOutputCustEle, "Extn");
			 if(!YFCObject.isVoid(extnEle)){
				 String sPreferredCarrier = extnEle.getAttribute("ExtnPreferredCarrier");
				 String sTaxExemptionCode = extnEle.getAttribute("ExtnTaxExemptionCode");
				 if(sPreferredCarrier.equals("LSHIP")){
					 extnEle.setAttribute("ExtnPreferredCarrierDescription", "LSHIP");
				 }else if(sPreferredCarrier.equalsIgnoreCase("ONTRAC")){
					 extnEle.setAttribute("ExtnPreferredCarrierDescription", "ONTRA");
				 }else if(sPreferredCarrier.equals("UPSN")){
					 extnEle.setAttribute("ExtnPreferredCarrierDescription", "UPSN");
				 }
				 
				 if(!YFCObject.isVoid(sTaxExemptionCode)){
					 extnEle.setAttribute("ExtnTaxExemptFlag", "Y");
				 }
			 
			 }
		 }
		 
		 return outputAPIManageCust;
	}

	/**
	 * Added for ARE-502. Creates an alert when CRM is unreachable. 
	 * 
	 * @param env YFSEnvironment variable
	 * @param inXML 
	 */
	private void createCRMDownAlert(YFSEnvironment env, Document inXML) {
		
		Document docCreateException = SCXmlUtil.createDocument(VSIConstants.ELE_INBOX);
		Element eleInbox = docCreateException.getDocumentElement();
		Element eleOrderCreateExcept = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
		Element eleCustomer = inXML.getDocumentElement();
		Element eleOrder = SCXmlUtil.getChildElement(inXML.getDocumentElement(), VSIConstants.ELE_ORDER);
		if(!YFCObject.isNull(eleOrder)){
			eleOrderCreateExcept.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
					eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
					eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO,
					eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO));
			eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				VSIConstants.VSI_CRM_ISSUE);
			eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID,
					VSIConstants.VSI_CRM_ISSUE);
			eleInbox.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, VSIConstants.INT_ZER0_NUM);
			eleInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION,
					VSIConstants.ALERT_CRM_ISSUE_DESC);
			eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION,
				VSIConstants.ALERT_CRM_ISSUE_DESC);
			eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE, VSIConstants.FLAG_Y);
			Element eleConsolTemp = SCXmlUtil.createChild(eleInbox, VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
			Element eleConsolTempInbox = SCXmlUtil.createChild(eleConsolTemp, VSIConstants.ELE_INBOX);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
					eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO));
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + VSIConstants.SPACE
					+ SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO) + VSIConstants.SPACE
					+ VSIConstants.ALERT_CRM_ISSUE_DESC);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + VSIConstants.SPACE
					+ eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO) + VSIConstants.SPACE
					+ VSIConstants.ALERT_CRM_ISSUE_DESC);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE, VSIConstants.FLAG_Y);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, VSIConstants.FLAG_Y);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				VSIConstants.VSI_CRM_ISSUE);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID,
					VSIConstants.VSI_CRM_ISSUE);
			eleConsolTempInbox.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, VSIConstants.INT_ZER0_NUM);
			try {
				VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION, docCreateException);
			} catch (YFSException | RemoteException
					| YIFClientCreationException e) {
				
				throw VSIUtils.getYFSException(e);
			}
		}
	}
} //End of VSICreateSaveNewCustomerWebservice class 
