package com.vsi.oms.api.web;


import java.net.SocketTimeoutException;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICreateCustomerForWEB {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSICreateCustomerForWEB.class);
	public int iretryCount1 = 0;
	public int iretryCount2 = 0;
	
	public Document findWEBCustomer(YFSEnvironment env, Document inXML){
		Document docGetCustomerListOutput = null;
		
		Element ordEle = inXML.getDocumentElement();
		String strBillToID = ordEle.getAttribute(VSIConstants.ATTR_BILL_TO_ID);
		String strEmailID = ordEle.getAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID);
		
		Document docGetCustomerListInput = SCXmlUtil.createDocument("Customer");
		Element customerEle = docGetCustomerListInput.getDocumentElement();
		customerEle.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
		Element eleCustomerContactList = SCXmlUtil.createChild(customerEle, "CustomerContactList");
		Element eleCustomerContact = SCXmlUtil.createChild(eleCustomerContactList, "CustomerContact");
//		customerEle.setAttribute(VSIConstants.ATTR_CUSTOMER_ID, strBillToID);
		eleCustomerContact.setAttribute(VSIConstants.ATTR_EMAIL_ID, strEmailID);
		
		try {
			docGetCustomerListOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_CUSTOMER_LIST_CRM_FAIL,VSIConstants.API_GET_CUSTOMER_LIST, docGetCustomerListInput);
			
			Element eleCustomerList = docGetCustomerListOutput.getDocumentElement();
			if(!YFCObject.isVoid(eleCustomerList)){
				NodeList nlCustomer = eleCustomerList.getElementsByTagName(VSIConstants.ELE_CUSTOMER);
				if(nlCustomer.getLength() > 0 && !(nlCustomer.getLength() > 1)){
					Element eleCustomer = SCXmlUtil.getChildElement(eleCustomerList, VSIConstants.ELE_CUSTOMER);
					if(!YFCObject.isVoid(eleCustomer)){
						String strCustID = eleCustomer.getAttribute(VSIConstants.ATTR_CUSTOMER_ID);
						
						if(!strCustID.equals(strBillToID)){
							ordEle.setAttribute(VSIConstants.ATTR_BILL_TO_ID,strCustID);
							Element eleExtn = SCXmlUtil.getXpathElement(ordEle, "/Order/Extn");
							if(YFCObject.isVoid(eleExtn)){
								eleExtn = SCXmlUtil.createChild(ordEle, VSIConstants.ELE_EXTN);
							}
							eleExtn.setAttribute("CustomerAltKey", strCustID);
						}
					}
				} else{
					// DO Nothing
				}
			}
		} catch (YFSException | RemoteException | YIFClientCreationException e) {
			log.error(e);
			
		}
		
		
		return inXML;
		
	}

	public void createCustomerForWEB(YFSEnvironment env, Document inXML)
			throws YFSException {
		String strIsGuestCustomer = null;
		String strAltKey = null;
		String strHRN = null;
		String prefix = null;
		String numericAltKey = null;
		
		boolean isGuest = false;
		boolean isNewCust = false;
		Element ordEle = inXML.getDocumentElement();
		
		Element extnOrdEle = (Element) ordEle.getElementsByTagName("Extn")
				.item(0);
		if (extnOrdEle != null) {
			strIsGuestCustomer = extnOrdEle.getAttribute("ExtnIsGuestCheckout");
			strAltKey = extnOrdEle.getAttribute("CustomerAltKey");
			strHRN = ordEle.getAttribute("BillToID");
			if (strIsGuestCustomer != null
					&& (strIsGuestCustomer.equalsIgnoreCase("Y") || (strIsGuestCustomer
							.equalsIgnoreCase("Yes")))) {
				isGuest = true;
			}
		}
		if (strAltKey != null && !strAltKey.trim().equals("") && strHRN != null
				&& !strHRN.trim().equals("")) {
			
				
				//strAltKey = strAltKey.toLowerCase();
//				/numericAltKey = strAltKey.replaceAll("[^0-9 ]", "");
				//extnOrdEle.setAttribute("CustomerAltKey",numericAltKey);
				prefix = strAltKey.substring(0, 2);
				extnOrdEle.setAttribute("Prefix",prefix);
				//System.out.println("inXML:\n"+ XMLUtil.getXMLString(inXML));
				
			if (!isGuest) {
				
					Document findCustWithAltKeySoapIp = null;
					try {
						findCustWithAltKeySoapIp = VSIUtils.invokeService(
								env, "VSIFindWEBCustomer", inXML);
					} catch (Exception e) {
						
						e.printStackTrace();
					}
					if(log.isDebugEnabled()){
						log.info("findCustWithAltKeySoapIp:\n"+ XMLUtil.getXMLString(findCustWithAltKeySoapIp));
					}
					Document findCustWithAltKeySoapOp = null;
					try {
						findCustWithAltKeySoapOp = InvokeWebservice
								.invokeFindCustomers(findCustWithAltKeySoapIp);
					} catch (SocketTimeoutException e) {
						
						if(iretryCount1 < 3){
							if(log.isDebugEnabled()){
								log.info("*** SocketTimeoutException occured! So trying to invoke the FindCustomers service again! Retry Count -  "+iretryCount1);
								log.debug("** Printing retry count : "+iretryCount1);
							}
							iretryCount1 ++;
							try {
								findCustWithAltKeySoapOp = InvokeWebservice
										.invokeFindCustomers(findCustWithAltKeySoapIp);
							} catch (Exception e1) {

								log.error("Exception occured on retry attempt -  "+iretryCount1);
								e1.printStackTrace();
							}
							
						}
						
					}  catch (Exception e) {
						
						e.printStackTrace();
					}
					if(findCustWithAltKeySoapOp != null){
						saveCustomer(env, ordEle);
						if(log.isDebugEnabled()){
							log.info("findCustWithAltKeySoapOp:\n"+ XMLUtil.getXMLString(findCustWithAltKeySoapOp));
						}
						Element itemsEle = (Element) findCustWithAltKeySoapOp.getElementsByTagName("Items").item(0);
						if(itemsEle.hasChildNodes()){
							if(log.isDebugEnabled()){
								log.info("Customer is already created!"+ XMLUtil.getXMLString(findCustWithAltKeySoapOp));
							}
						}else{
							if(log.isDebugEnabled()){
								log.info("Customer does not exist in CRM!");
							}
						}
					}
					
					
				
			}
			if(isNewCust || isGuest){
				
				Document saveSoapIp = null;
				try {
					saveSoapIp = VSIUtils.invokeService(
							env, "VSISaveWEBCustomer", inXML);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				if(log.isDebugEnabled()){
					log.info("saveSoapIp:\n"+ XMLUtil.getXMLString(saveSoapIp));
				}
				Document saveSoapOp = null;
				try {
					saveSoapOp = InvokeWebservice
							.invokeModifyCustomer(saveSoapIp);
				} catch (SocketTimeoutException e) {
					
					if(iretryCount2 < 3){
						if(log.isDebugEnabled()){
							log.info("*** SocketTimeoutException occured! So trying to invoke the Save service again! Retry Count -  "+iretryCount2);
							log.debug("** Printing retry count : "+iretryCount2);
						}
						iretryCount2 ++;
						try {
							saveSoapOp = InvokeWebservice
									.invokeModifyCustomer(saveSoapIp);
						} catch (Exception e1) {

							if(log.isDebugEnabled()){
								log.info("Exception occured on retry attempt -  "+iretryCount2);
							}
							e1.printStackTrace();
						}
						
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				if(saveSoapOp != null){
					if(log.isDebugEnabled()){
						log.info("Customer Created Successfully!"+ XMLUtil.getXMLString(saveSoapOp));
					}
					
					}
			}else{
				
				saveCustomer(env, ordEle);
			}
			
			}
		}
	
	private static void saveCustomer(YFSEnvironment env,Element ordEle){
		Document modifyCustomerInput = null;
		try {
			modifyCustomerInput = XMLUtil
			.createDocument("Customer");
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strHRN = ordEle.getAttribute("BillToID");
		Element elePersonInfoBillTo = (Element)ordEle.getElementsByTagName("PersonInfoBillTo").item(0);
		if(!YFCObject.isVoid(elePersonInfoBillTo)){
			String strFirstName = elePersonInfoBillTo.getAttribute("FirstName");
			String strLastName = elePersonInfoBillTo.getAttribute("LastName");
			String strEmailID = ordEle.getAttribute("CustomerEMailID");
			String strDayPhone = elePersonInfoBillTo.getAttribute("DayPhone");
			String strAddressLine1 = elePersonInfoBillTo.getAttribute("AddressLine1");
			String strAddressLine2 = elePersonInfoBillTo.getAttribute("AddressLine2");
			String strCity = elePersonInfoBillTo.getAttribute("City");
			String strState = elePersonInfoBillTo.getAttribute("State");
			String strZipCode = elePersonInfoBillTo.getAttribute("ZipCode");
			//Jira 803
			String strCountry = elePersonInfoBillTo.getAttribute("Country");
		     if (strCountry.equalsIgnoreCase("USA")||strCountry.equalsIgnoreCase("US") ){
	          	strCountry="USA";
	            }
		     else if (strCountry.equalsIgnoreCase("CA")||strCountry.equalsIgnoreCase("CAN")||strCountry.equalsIgnoreCase("CN") ){
		          	strCountry="CAN";
		            }
			else  {
          	strCountry="FGN";
            }
		     
            // jira 803 end
			Element customerEle = modifyCustomerInput.getDocumentElement();
			customerEle.setAttribute("CustomerKey", strHRN);
			Element eleAddressList = modifyCustomerInput.createElement("CustomerAdditionalAddressList");
			eleAddressList.setAttribute("Reset", "Y");
			Element eleAddress = modifyCustomerInput.createElement("CustomerAdditionalAddress");
			eleAddress.setAttribute("IsBillTo", "Y");
			eleAddress.setAttribute("IsDefaultBillTo", "Y");
			eleAddress.setAttribute("IsDefaultShipTo", "Y");
			eleAddress.setAttribute("IsDefaultSoldTo", "N");
			eleAddress.setAttribute("IsShipTo", "Y");
			eleAddress.setAttribute("IsSoldTo", "Y");

			Element elePersonInfo = modifyCustomerInput.createElement("PersonInfo");
			if(!YFCObject.isVoid(strAddressLine1))
				elePersonInfo.setAttribute("AddressLine1", strAddressLine1);
			if(!YFCObject.isVoid(strAddressLine2))
				elePersonInfo.setAttribute("AddressLine2", strAddressLine2);
			if(!YFCObject.isVoid(strCity))
				elePersonInfo.setAttribute("City", strCity);
			if(!YFCObject.isVoid(strDayPhone))
				elePersonInfo.setAttribute("DayPhone", strDayPhone);
			if(!YFCObject.isVoid(strCountry))
				elePersonInfo.setAttribute("Country",strCountry );
			if(!YFCObject.isVoid(strEmailID))
				elePersonInfo.setAttribute("EMailID", strEmailID);
			if(!YFCObject.isVoid(strFirstName))
				elePersonInfo.setAttribute("FirstName",strFirstName);
			if(!YFCObject.isVoid(strLastName))
				elePersonInfo.setAttribute("LastName", strLastName);
			if(!YFCObject.isVoid(strState))
				elePersonInfo.setAttribute("State", strState);
			if(!YFCObject.isVoid(strZipCode))
				elePersonInfo.setAttribute("ZipCode", strZipCode);


			eleAddress.appendChild(elePersonInfo);
			eleAddressList.appendChild(eleAddress);
			customerEle.appendChild(eleAddressList);

			try {
				VSIUtils.invokeService(env,"VSIModifyCustomer", modifyCustomerInput);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	}

