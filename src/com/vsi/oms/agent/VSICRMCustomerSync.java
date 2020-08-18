package com.vsi.oms.agent;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.woodstock.install.utils.CommonUtils;
import com.vsi.oms.api.VSICreateSaveNewCustomerWebservice;
import com.vsi.oms.api.VSIFormatOutput;
import com.vsi.oms.api.VSIProcessGetCustomerDataOutput;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.util.YFCUOMUtils;
import com.yantra.util.YFCUtils;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICRMCustomerSync extends YCPBaseAgent {

	private YFCLogCategory log = YFCLogCategory.instance(VSICRMCustomerSync.class);
//	YIFApi api;

	/** 
	 * Call getCustomerList with organization code as "DEFAULT" and ExternalCustomerID as 0.
	 * 
	 * @param env
	 * @param inDoc - incoming document for the getJobs method
	 * @return discList - return the list with the with the output of getCustomerList- 
	 */
	public List<Document> getJobs(YFSEnvironment env, Document inDoc, Document lastMsg) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("VSICRMCustomerSync: getJobs method :- Begins Input XML is: \n "+ XMLUtil.getXMLString(inDoc));
		}
		Document docGetCustListSummaryOutput = null;
		List<Document> discList = new ArrayList<Document>();
		if (null == lastMsg) {

			// Create input for getCustomerList API - <Customer OrganizationCode="DEFAULT" ExternalCustomerID="0"/>
			Document docGetCustList = SCXmlUtil.createDocument(VSIConstants.ELE_CUSTOMER);
			Element eleGetCustList = docGetCustList.getDocumentElement();
			eleGetCustList.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
			Element eleComplexQuery = SCXmlUtil.createChild(eleGetCustList, VSIConstants.ELE_COMPLEX_QUERY);
			eleComplexQuery.setAttribute(VSIConstants.ATTR_OPERATOR, VSIConstants.AND_QUERY);
			Element eleOrOperator = SCXmlUtil.createChild(eleComplexQuery, "Or");
			Element eleExp0 =SCXmlUtil.createChild(eleOrOperator, VSIConstants.ELE_EXP);
			eleExp0.setAttribute(VSIConstants.ATTR_NAME,VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID);
			eleExp0.setAttribute(VSIConstants.ATTR_VALUE, "0");
			Element eleExp1 =SCXmlUtil.createChild(eleOrOperator, VSIConstants.ELE_EXP);
			eleExp1.setAttribute(VSIConstants.ATTR_NAME,VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID);
			eleExp1.setAttribute(VSIConstants.ATTR_VALUE, "1");
			Element eleExp2 =SCXmlUtil.createChild(eleOrOperator, VSIConstants.ELE_EXP);
			eleExp2.setAttribute(VSIConstants.ATTR_NAME,VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID);
			eleExp2.setAttribute(VSIConstants.ATTR_VALUE, "2");
			
//			eleGetCustList.setAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID, VSIConstants.INT_ZER0_NUM);

			// Call getCustomerList API 
			docGetCustListSummaryOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_VSI_GET_CUSTOMER_LIST,
					VSIConstants.API_GET_CUSTOMER_LIST, docGetCustList);
			
		
			Element eleCustomerList = (Element) docGetCustListSummaryOutput.getElementsByTagName(
					VSIConstants.ELE_CUST_LIST).item(0);
			NodeList nlCustomerList = eleCustomerList.getElementsByTagName(VSIConstants.ELE_CUSTOMER);
			for (int i = 0; i < nlCustomerList.getLength(); i++) {
				Element eleCustomer = (Element) nlCustomerList.item(i);
				Element eleExtn = SCXmlUtil.getChildElement(eleCustomer, "Extn");
				Element eleCustomerContactList = SCXmlUtil.getChildElement(eleCustomer, "CustomerContactList");
				Element eleCustomerContact = SCXmlUtil.getChildElement(eleCustomerContactList, "CustomerContact");
				
				if(eleCustomerContact.hasAttribute("Title")){
				String strTitle = eleCustomerContact.getAttribute("Title");
				eleCustomerContact.setAttribute("ExtnGender", strTitle);
				}
				
				NodeList nlPersonInfoList = eleCustomer.getElementsByTagName(VSIConstants.ELE_PERSON_INFO);
				int count = 1;
				for (int j = 0; j < nlPersonInfoList.getLength(); j++) {
					Element elePersonInfo = (Element) nlPersonInfoList.item(j);
					String strID = elePersonInfo.getAttribute("PersonID");
					if(YFCObject.isVoid(strID)){
						strID = String.valueOf(count);
						count++;
					}
					elePersonInfo.setAttribute("ID", strID);
				}
				discList.add(SCXmlUtil.createFromString(SCXmlUtil.getString(eleCustomer)));
			}
		}
		if(log.isDebugEnabled()){
			log.debug("VSICRMCustomerSync: getJobs method :- Ends");
			log.debug("docGetCustListSummaryOutput"+SCXmlUtil.getString(docGetCustListSummaryOutput));
			log.debug("GetOMSCustomers"+ discList.toString());
		}
		// Return the list with the with the output of getCustomerList
		return discList;

	} // End of getJobs method


	/**
	 * Fetch all customer data and call VSISaveNewCustomer to create customers
	 * in CRM. If CRM call is successful: 
	 * 1) call manageCustomer to update the received ExternalCustomerID=New CustomerID 
	 * 2) call getOrderList to get all the orders with BillToID=old customerID, 
	 * call changeOrder on all orders to update BillToId as new customer ID.	
	 * 
	 * @param env
	 * @param docInput - incoming document for the executeJobs method
	 */
	@Override
	public void executeJob(YFSEnvironment env, Document docInput) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("VSICRMCustomerSync: executeJob method :- Begins Input XML is: \n "+XMLUtil.getXMLString(docInput));
			//log.debug("VSICRMCustomerSync: executeJob method :- Begins Input XML is: \n "+XMLUtil.getXMLString(docInput));
		}
		Document outdoc = null;
		Document outdocCRM = null;
		Document srchResponse=null;
		Element eleCustomer = docInput.getDocumentElement();
		VSICreateSaveNewCustomerWebservice VSICreateSaveNewCustomerWebservice = new VSICreateSaveNewCustomerWebservice();
			if (docInput.getDocumentElement().hasChildNodes()) {	

				Element eleCust = docInput.getDocumentElement();
				String strOMSCustID = eleCust.getAttribute(VSIConstants.ATTR_CUSTOMER_ID); //CustomerID with S
				String strOMSCustKey = eleCust.getAttribute(VSIConstants.ATTR_CUSTOMER_KEY);
				

				try {
					
					Document docCRMInput = SCXmlUtil.createFromString(SCXmlUtil.getString(eleCust));
					VSICreateSaveNewCustomerWebservice.manageMultipleAddresses(docCRMInput);
					//START ARS-330
					Element eleSoapIPCustContact = (Element) docCRMInput.getDocumentElement().getElementsByTagName(
							VSIConstants.ELE_CUST_CONTACT).item(0);
					String strLastNameContact = eleSoapIPCustContact.getAttribute("LastName");
					if (YFCObject.isVoid(strLastNameContact)){
						eleSoapIPCustContact.setAttribute("LastName", "N/A");
					}
					
					NodeList nlPersonInfoSoapIP =  eleSoapIPCustContact.getElementsByTagName(VSIConstants.ELE_PERSON_INFO);
					for (int i = 0; i < nlPersonInfoSoapIP.getLength(); i++) {
						Element elePersonInfoSoapIp = (Element) nlPersonInfoSoapIP.item(i);
						String strLastName = elePersonInfoSoapIp.getAttribute("LastName");
						if (YFCObject.isVoid(strLastName)){
							elePersonInfoSoapIp.setAttribute("LastName", "N/A");
						}
					}
					//END ARS-330
					//System.out.println("");
					//Call to CRM
//						api = YIFClientFactory.getInstance().getApi();
					Document soapInputDoc = VSIUtils.invokeService(env, VSIConstants.SERVICE_CREATE_CUSTOMER_INPUT, 
//							VSIUtils.getCountryCode(env, docCRMInput) -- OMS-1440(Comment Reason: Country code saved as USA already)
							docCRMInput);

					outdocCRM = InvokeWebservice.invokeCreateCustomer(soapInputDoc);
					if (outdocCRM != null) {
						String customerNumber = YFCDocument.getDocumentFor(outdocCRM).getDocumentElement()
								.getChildElement("s:Body").getChildElement("SaveNewCustomerResponse")
								.getChildElement("parameters").getChildElement("b:CustomerNumber").getNodeValue();

						docCRMInput.getDocumentElement().setAttribute(VSIConstants.ATTR_CUSTOMER_ID, customerNumber);
						docCRMInput.getDocumentElement().setAttribute(VSIConstants.ATTR_CUST_KEY, customerNumber);
						outdocCRM = docCRMInput;
					}
			
					String strCustomerIDOutPut = outdocCRM.getDocumentElement().getAttribute("CustomerID");
					Element eleCustomerSaveCall = docCRMInput.getDocumentElement();
					Element eleExtn = SCXmlUtil.getChildElement(eleCustomerSaveCall, "Extn");
					String strExtnPreferredCarrier = eleExtn.getAttribute("ExtnPreferredCarrier");
					String strExtnTaxExemptionCode = eleExtn.getAttribute("ExtnTaxExemptionCode");
					if (!YFCObject.isVoid(strExtnPreferredCarrier) ||!YFCObject.isVoid(strExtnTaxExemptionCode) ){
						eleCustomerSaveCall.setAttribute("HasCustomerAttributes", "Y");
						eleCustomerSaveCall.setAttribute("ExcludeCustomer", "Y");
						
						if(!YFCObject.isVoid(strExtnPreferredCarrier)){
							eleCustomerSaveCall.setAttribute("SetPreferredCarrier", "Y");
							eleCustomerSaveCall.setAttribute("CarrierRecordState", "Added");
						}
						if(!YFCObject.isVoid(strExtnTaxExemptionCode)){
							eleCustomerSaveCall.setAttribute("SetTaxExemptionCode", "Y");
							eleCustomerSaveCall.setAttribute("TaxExemptionRecordState", "Added");
							
						}
					
					srchResponse = VSIUtils.invokeService(env,VSIConstants.API_VSI_MODIFY_CUST, docCRMInput);
					} 
					Element eleCRMCust = outdocCRM.getDocumentElement();
					String strCRMCustID = eleCRMCust.getAttribute(VSIConstants.ATTR_CUST_KEY); 	

					if(!YFCObject.isVoid(strCRMCustID) && !strOMSCustID.equals(strCRMCustID)) {
						Document changeExtCustNum = SCXmlUtil.createDocument(VSIConstants.ELE_CUSTOMER);
						Element eleCustomerUpdate = changeExtCustNum.getDocumentElement();
						
						eleCustomerUpdate.setAttribute(VSIConstants.ATTR_CUST_KEY, strOMSCustKey);
						eleCustomerUpdate.setAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID, strCRMCustID);
						
						// Call manageCustomer to update the received ExternalCustomerID = New CustomerID						
						Document outputAPIManageCust = VSIUtils.invokeAPI(env, VSIConstants.API_MANAGE_CUST, changeExtCustNum);

						//Creating input for GetOrderList  where BillToID = CustomerID w/ S
						Document inputGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
						Element eleInputGetOrderList = inputGetOrderList.getDocumentElement();
						eleInputGetOrderList.setAttribute(VSIConstants.ATTR_BILL_TO_ID, strOMSCustID);
						
						// Call getOrderList API
						Document outputAPIGetOrderList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_VSI_GET_ORDER_LIST, VSIConstants.API_GET_ORDER_LIST, inputGetOrderList);							
						ArrayList<Element> arrayOrderList = SCXmlUtil.getChildren(outputAPIGetOrderList.getDocumentElement(), VSIConstants.ELE_ORDER);
											
						//Creating changeOrder template 
						Document changeOrderTemplate = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
						Element elechangeOrderTemplate = changeOrderTemplate.getDocumentElement();
						elechangeOrderTemplate.setAttribute(VSIConstants.ATTR_ORDER_NO , VSIConstants.ATTR_EMPTY);	
						elechangeOrderTemplate.setAttribute(VSIConstants.ATTR_BILL_TO_ID, VSIConstants.ATTR_EMPTY);						
						
						for (Element eleOrder : arrayOrderList) {	
							// Call changeOrder to update BillToID to CustomerID w/o S
							eleOrder.setAttribute(VSIConstants.ATTR_BILL_TO_ID,strCRMCustID);
							eleOrder.setAttribute(VSIConstants.ATTR_OVERRIDE,VSIConstants.FLAG_Y);
							Document outputChangeOrder = VSIUtils.invokeAPI(env, SCXmlUtil.getString(changeOrderTemplate), VSIConstants.API_CHANGE_ORDER, SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrder)));															
						}
					}											
				}
				catch (Exception e) {
					
					int strEXtnID = Integer.parseInt(eleCust.getAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID));
					if (strEXtnID<3){
						strEXtnID = strEXtnID+1;
					}
					//System.out.println("ID:::"+strEXtnID);
					Document docChangeCustomerExtnID = SCXmlUtil.createDocument();
					Element eleChangeCustomerExtnID = docChangeCustomerExtnID.createElement(VSIConstants.ELE_CUSTOMER);
					docChangeCustomerExtnID.appendChild(eleChangeCustomerExtnID);
					eleChangeCustomerExtnID.setAttribute(VSIConstants.ATTR_CUSTOMER_KEY, strOMSCustKey);
					eleChangeCustomerExtnID.setAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID,String.valueOf(strEXtnID));

					// Call manageCustomer to update the received ExternalCustomerID = New CustomerID						
					Document manageCustomerOut=VSIUtils.invokeAPI(env, VSIConstants.API_MANAGE_CUST, docChangeCustomerExtnID);
					//Failed Update Message
					//System.out.println("manageCustomerOut"+SCXmlUtil.getString(manageCustomerOut));
					log.error("VSICRMCustomerSync :- CRM call Failed for CustomerID: "+ strOMSCustID);						
				}
			}
			if(log.isDebugEnabled()){				
				log.debug("executeJob method :- Ends");
			}
	} // End of executeJob method

} // End of VSICRMCustomerSync Class
