package com.vsi.oms.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

import edu.emory.mathcs.backport.java.util.Collections;

public class VSIProcessGetCustomerDataOutput implements YIFCustomApi{
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIProcessGetCustomerDataOutput.class);
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public Document processCustDataOutput(YFSEnvironment env, Document srcResult) throws NumberFormatException, ParserConfigurationException {
		
		YFCIterable<YFCElement> resultsItr = null;
		YFCIterable<YFCElement> custAddrsItr = null;
		YFCIterable<YFCElement> custAttriItr = null;
		YFCIterable<YFCElement> customerRemarkItr = null;
		YFCIterable<YFCElement> customerLoyaltiesItr = null;
		YFCIterable<YFCElement> customerLoyaltiesMbrItr = null;
		YFCIterable<YFCElement> custDivisionItr=null;
		YFCElement customerEmailEle = null;
		YFCElement customerPhoneEle = null;
		YFCElement customerLoyaltiesEle = null;
		log.info(" Inside Method processCustDataOutput: Printing Input XML : \n "+XMLUtil.getXMLString(srcResult));

		Document docResponse = null;
		if (srcResult != null && !srcResult.getDocumentElement().getNodeName().equals(VSIConstants.ELE_CUSTOMER)) {
			YFCElement results = YFCDocument.getDocumentFor(srcResult)
					.getDocumentElement()
					.getChildElement("s:Body")
					.getChildElement("GetCustomerDataResponse")
					.getChildElement("GetCustomerDataResult")
					;

			resultsItr = results.getChildren("b:Customer");
			custAddrsItr = results.getChildElement("b:CustomerAddresses").getChildElement("b:Items").getChildren("b:CustomerAddressWCF");
			custAttriItr = results.getChildElement("b:CustomerAttributes").getChildElement("b:Items").getChildren("b:CustomerAttributeWCF");
			customerRemarkItr = results.getChildElement("b:CustomerRemarks").getChildElement("b:Items").getChildren("b:CustomerRemarkWCF");
			customerLoyaltiesItr = results.getChildElement("b:CustomerLoyalties").getChildElement("b:Items").getChildren("b:CustomerLoyaltyWCF");
			customerLoyaltiesMbrItr = results.getChildElement("b:CustomerLoyalties").getChildElement("b:Items").getChildren("b:CustomerLoyaltyWCF");
			customerEmailEle = results.getChildElement("b:CustomerEmails").getChildElement("b:Items").getChildElement("b:CustomerEmailWCF");
			customerPhoneEle = results.getChildElement("b:CustomerPhones").getChildElement("b:Items").getChildElement("b:CustomerPhoneWCF");
		} else {
			// If customer information is unavailable, return
			// <CustomerList><Customer IsCustomerAvailable="N" /></CustomerList>
			docResponse = XMLUtil.createDocument(VSIConstants.ELE_CUST_LIST);
			Element customerListDocEle = docResponse.getDocumentElement();
			Element customerEle = XMLUtil.appendChild(docResponse,customerListDocEle, VSIConstants.ELE_CUSTOMER, "");
			customerEle.setAttribute(VSIConstants.ATTR_IS_CUST_AVAIL,VSIConstants.FLAG_N);
		}
		
		
		
		
		if (resultsItr != null && resultsItr.hasNext()) {
			while (resultsItr.hasNext() == true) {
				YFCElement result = resultsItr.next();
				
				//if(log.isDebugEnabled()){
					log.info(" result: Printing Input XML : \n "+SCXmlUtil.getString((Element) result.getDOMNode()));				
				//}
				
				String custID = result.getChildElement("b:Number").getNodeValue(); // Number=HealthyRewardsNo in ISE, this HealthyRewardsNo is CustomerID for Sterling
				//Riskified START
				String custCreateDate = result.getChildElement("b:CreateDate").getNodeValue();
				
				//if(log.isDebugEnabled()){
					log.info(" custCreateDate: Printing custCreateDate : \n "+custCreateDate);
				//}
				
				/*if (YFCObject.isVoid(custCreateDate)) {
					custDivisionItr = result.getChildElement("b:CustomerDivisions").getChildren("b:CustomerDivisionWCF");
					if (custDivisionItr != null && custDivisionItr.hasNext()) {
											while (custDivisionItr.hasNext() == true) {
												YFCElement custDivisions = custDivisionItr.next();
												custCreateDate = custDivisions.getChildElement("b:CreateDate").getNodeValue();
						}}		
				}*/
				//Riskified END
				int custHANo = 0;
				if (customerLoyaltiesItr != null && customerLoyaltiesItr.hasNext()) {
					while(customerLoyaltiesItr.hasNext()){
						YFCElement customerLoyaltyEle = customerLoyaltiesItr.next();
						//OMS-2026:Start
						String strLoyaltyPlanID=customerLoyaltyEle.getChildElement("b:LoyaltyPlanID").getNodeValue();
						if(VSIConstants.ENT_TYPE_1.equals(strLoyaltyPlanID))
						{	
						int intPostedPoints =Integer.parseInt(customerLoyaltyEle
				 				.getChildElement("b:PostedPoints")
				 				.getNodeValue());
						custHANo = custHANo + intPostedPoints;
						}
						//OMS-2026:End
					}
				}
				/*try {
					Document healthyRewardsIPXML = XMLUtil.createDocument(VSIConstants.ELE_CUSTOMER);
					healthyRewardsIPXML.getDocumentElement().setAttribute(VSIConstants.ATTR_HEALTHY_AWARDS_NO, custID);
					
					Document healthyRewardsOPXML = VSIUtils.invokeService(env, "VSIGetCustRewardPoints",healthyRewardsIPXML); 
					if(healthyRewardsOPXML != null && !"N".equals(healthyRewardsOPXML.getDocumentElement().getAttribute("IsCustomerAvailable"))){
						 custHANo=YFCDocument.getDocumentFor(healthyRewardsOPXML).getDocumentElement()
					 				.getChildElement("s:Body")
					 				.getChildElement("GetCustomerLoyaltiesResponse")
					 				.getChildElement("GetCustomerLoyaltiesResult")
					 				.getChildElement("b:Items")
					 				.getChildElement("b:CustomerLoyaltyWCF")
					 				.getChildElement("b:PostedPoints")
					 				.getNodeValue();
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				// ARS-182 : Demographics changes to view and edit customer opt ins : START
				String strPhoneOptInCode = null;
				String strPostalOptInCode = null;
				String strEmailOptInCode = "";
						//result.getChildElement(VSIConstants.ELE_EMAIL_OPT_IN_FLAG_CODE).getNodeValue();
				// ARS-182 : Demographics changes to view and edit customer opt ins : END
				
				String fName = result.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_FIRST_NAME).getNodeValue();
				String lName = result.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_LAST_NAME).getNodeValue();
				String eMail = "";
				if(!YFCObject.isVoid(customerEmailEle)){
					eMail = customerEmailEle.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_EMAIL_ADDR).getNodeValue();
					strEmailOptInCode = customerEmailEle
							.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "EmailDivisions")
							.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "CustomerEmailDivisionWCF")
							.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "EmailOptInFlagCode")
							.getNodeValue();
				}
				String gender = result.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_GENDER).getNodeValue();
				if("M".equals(gender)){
					gender = "Male";
				}else if("F".equals(gender)){
					gender = "Female";
				}else{
					gender = "Not-Specified";
				}
				//START
					docResponse = SCXmlUtil.createDocument("CustomerList");
					Element eleOutDoc = docResponse.getDocumentElement();
					Element eleCustomer = docResponse.createElement("Customer");
					eleOutDoc.appendChild(eleCustomer);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,custID);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_KEY,custID);
					eleCustomer.setAttribute(VSIConstants.ATTR_HEALTHY_REWARD_PTS, String.valueOf(custHANo));
					// Added to make customer active in all cases.
					eleCustomer.setAttribute(VSIConstants.ATTR_STATUS,VSIConstants.STATUS_10);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_TYPE,VSIConstants.ATTR_CUSTOMER_TYPE_02);
					eleCustomer.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE,VSIConstants.VSICOM_ENTERPRISE_CODE);
					Element eleCustomerContactList = docResponse.createElement("CustomerContactList");
					eleCustomer.appendChild(eleCustomerContactList);
					Element eleCustomerExtn = docResponse.createElement("Extn");
					eleCustomer.appendChild(eleCustomerExtn);
					Element eleCustomerContact = docResponse.createElement("CustomerContact");
					eleCustomerContactList.appendChild(eleCustomerContact);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_CUST_CONTACT_ID, custID);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_FIRST_NAME,fName);
					if(!lName.equalsIgnoreCase("N/A")){
						eleCustomerContact.setAttribute(VSIConstants.ATTR_LAST_NAME,lName);
					}
					eleCustomerContact.setAttribute(VSIConstants.ATTR_EMAIL_ID,eMail);
					// YFCElement custContactextn=
					// custContact.getChildElement("Extn");
					// if(!YFCDocument.isVoid(custContactextn)){
					eleCustomerContact.setAttribute("ExtnGender", gender);
					// }
					
					Element eleCustomerAdditionalAddressList = docResponse.createElement("CustomerAdditionalAddressList");
					eleCustomerContact.appendChild(eleCustomerAdditionalAddressList);
					
					//System.out.println("eleOutDoc::"+SCXmlUtil.getString(docResponse));
				//END
				boolean hasDST = false;
				boolean hasDBT = false;
				if (custAddrsItr != null && custAddrsItr.hasNext()) {
					while (custAddrsItr.hasNext() == true) {
						YFCElement custAddrs = custAddrsItr.next();
						String id = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_CUSTOMER_ADDRESS_ID).getNodeValue();
						String add1 = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_ADDR_1).getNodeValue();
						String add2 = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_ADDR_2).getNodeValue();
						String city = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_CITY).getNodeValue();
						String state = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_STATE).getNodeValue();
						String pcode = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_POSTAL_CODE).getNodeValue();
						String country = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+VSIConstants.ATTR_COUNTRY_CODE).getNodeValue();
						String telNo = "";
						if(!YFCObject.isVoid(customerPhoneEle)){
							telNo = customerPhoneEle.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"PhoneNumber").getNodeValue();
							strPhoneOptInCode = customerPhoneEle
									.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "PhoneDivisions")
									.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "CustomerPhoneDivisionWCF")
									.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "PhoneOptInFlagCode")
									.getNodeValue();
						}
						String strAddressTypeCode = custAddrs.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"AddressTypeCode").getNodeValue();
						eleCustomerContact.setAttribute(VSIConstants.ATTR_DAY_PHONE,telNo);
						
						// ARS-182 : Demographics changes to view and edit customer opt ins : START
						strPostalOptInCode = custAddrs
								.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "AddressDivisions")
								.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "CustomerAddressDivisionWCF")
								.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+ "MailOptInFlagCode")
								.getNodeValue();
						// ARS-182 : Demographics changes to view and edit customer opt ins : END
						
						Element eleCustomerAdditionalAddress = docResponse.createElement("CustomerAdditionalAddress");
						eleCustomerAdditionalAddressList.appendChild(eleCustomerAdditionalAddress);
						Element elePersonInfo = docResponse.createElement(VSIConstants.ELE_PERSON_INFO);
						eleCustomerAdditionalAddress.appendChild(elePersonInfo);
						
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_CUST_ADDL_ADDR_ID, custID);

						// START - 1B CRM enhancements
						String strIsDefaultBillTo = "N";
						String strIsDefaultShipTo = "N";
						String strIsBillTo = "Y";
						String strIsShipTo = "Y";
						if ("IDST".equals(strAddressTypeCode) && !hasDST) {
							strIsDefaultShipTo = "Y";
							strIsBillTo = "N";
							hasDST = true;
						} else if ("IDBT".equals(strAddressTypeCode) && !hasDBT) {
							strIsDefaultBillTo = "Y";
							strIsBillTo = "Y";
							strIsShipTo = "N";
							hasDBT = true;
						} else if ("BILL".equals(strAddressTypeCode) 
								|| ("IDBT".equals(strAddressTypeCode) && hasDBT)) {
							strIsShipTo = "N";
						} else if ("SHIP".equals(strAddressTypeCode) || ("IDST".equals(strAddressTypeCode) && hasDST)) {
							strIsBillTo = "N";
						}else if ("HOME".equals(strAddressTypeCode) && (!hasDBT && !hasDST)) {
							strIsDefaultShipTo = "Y";
							strIsDefaultBillTo = "Y";
							hasDST = true;
							hasDBT = true;
						}else{
							strIsBillTo="N";
						}
						
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_CUST_ADDL_ADDR_ID, custID);
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO,strIsDefaultShipTo);
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO,strIsDefaultBillTo);
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_SHIP_TO, strIsShipTo);
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_BILL_TO, strIsBillTo);
						eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_CUSTOMER_ADDRESS_ID, id);
						// END - 1B CRM enhancements

						elePersonInfo.setAttribute(VSIConstants.ATTR_CUSTOMER_ADDRESS_ID, id);
						elePersonInfo.setAttribute(VSIConstants.ATTR_ADDRESS1, add1);
						elePersonInfo.setAttribute(VSIConstants.ATTR_ADDRESS2, add2);
						elePersonInfo.setAttribute(VSIConstants.ATTR_CITY, city);
						elePersonInfo.setAttribute(VSIConstants.ATTR_STATE, state);
						elePersonInfo.setAttribute(VSIConstants.ATTR_ZIPCODE, pcode);
						elePersonInfo.setAttribute(VSIConstants.ATTR_COUNTRY, country);
						elePersonInfo.setAttribute(VSIConstants.ATTR_DAY_PHONE, telNo);
						elePersonInfo.setAttribute(VSIConstants.ATTR_FIRST_NAME,fName);
						if(!lName.equalsIgnoreCase("N/A")){
							elePersonInfo.setAttribute(VSIConstants.ATTR_LAST_NAME, lName);
						}
						elePersonInfo.setAttribute(VSIConstants.ATTR_EMAIL_ID, eMail);
					}
				}
				eleCustomerExtn.setAttribute("ExtnTaxExemptionCode", "");
				eleCustomerExtn.setAttribute("ExtnPreferredCarrier", "");
				//OMS-1094 : Start
				eleCustomerExtn.setAttribute("ExtnVIPFlag", "N");
				//OMS-1094 : End
				//START - Loyalty 2.1
				eleCustomerExtn.setAttribute("ExtnLoyaltyCustomer", "N");
				//END - Loyalty 2.1
				//OMS-Riskify Start
				eleCustomerExtn.setAttribute("ExtnCustomerCreatets",custCreateDate);
				//OMS-Riskify END 
				
				//OMS-1653 start
				if (customerLoyaltiesMbrItr != null && customerLoyaltiesMbrItr.hasNext()) {
					while(customerLoyaltiesMbrItr.hasNext()){
						YFCElement eleCustomerLoyalty = customerLoyaltiesMbrItr.next();
						//OMS-2026:Start
						String strLoyaltyPlanID=eleCustomerLoyalty.getChildElement("b:LoyaltyPlanID").getNodeValue();
						if(VSIConstants.ENT_TYPE_1.equals(strLoyaltyPlanID))
						{
						YFCElement eleMbrShipTypeCode= eleCustomerLoyalty.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"MembershipTypeCode");
						if(!YFCObject.isVoid(eleMbrShipTypeCode)){

							String strMbrShipCode= eleCustomerLoyalty.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"MembershipTypeCode").getNodeValue();
							if(!YFCObject.isVoid(strMbrShipCode)){
								if("ESTD".equals(strMbrShipCode)){
									eleCustomerExtn.setAttribute(VSIConstants.ATTR_LOYALTY_TIER, VSIConstants.ATTR_LOYALTY_BRONZE);

								}
								else if("EMTD".equals(strMbrShipCode)){
									eleCustomerExtn.setAttribute(VSIConstants.ATTR_LOYALTY_TIER, VSIConstants.ATTR_LOYALTY_SILVER);

								}
								else if("EVIP".equals(strMbrShipCode)){
									eleCustomerExtn.setAttribute(VSIConstants.ATTR_LOYALTY_TIER, VSIConstants.ATTR_LOYALTY_GOLD);

								}

							}
						}
						}
						//OMS-2026:End
					}
				}
				//OMS-1653 End
				if(custAttriItr !=null && custAttriItr.hasNext()){
					Element eleCustomerPaymentMethodList = docResponse.createElement("CustomerPaymentMethodList");
					eleCustomer.appendChild(eleCustomerPaymentMethodList);
					while (custAttriItr.hasNext() == true){
						YFCElement custAttri = custAttriItr.next();
						String groupCode = custAttri.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"GroupCode").getNodeValue();
						String custCode= custAttri.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"Code").getNodeValue();
						String custComment= custAttri.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"Comment").getNodeValue();
						String custValue= custAttri.getChildElement(VSIConstants.ATTR_SOAP_PREFIX+"Value").getNodeValue();
						//System.out.println("groupCode: "+groupCode+" custCode: "+custCode+" custComment: "+custComment+" custValue: "+custValue);
						if("TKNV".equals(groupCode) && custCode.startsWith("CARD")){
							String strCCNo = custComment.split(":")[0];
							String strDCCNo = custComment.split(":")[0];
							String strCCType = custComment.split(":")[1];
							boolean aurusHandle=false;
							if(custComment.split(":").length==3) {
								 strCCNo = custComment.split(":")[0];
								 strDCCNo = custComment.split(":")[1];
								 strCCType = custComment.split(":")[2];
								 aurusHandle=true;
							}
							String strCCExpDate = custValue.substring(4, custValue.length())+"/"+custValue.substring(0,4);
							//System.out.println("strCCNo: "+strCCNo+" strCCType: "+strCCType+" strCCExpDate: "+strCCExpDate);
							Element eleCustomerPaymentMethod = docResponse.createElement("CustomerPaymentMethod");
							eleCustomerPaymentMethodList.appendChild(eleCustomerPaymentMethod);
							eleCustomerPaymentMethod.setAttribute("CreditCardNo", strCCNo);
							if(!aurusHandle) {
								eleCustomerPaymentMethod.setAttribute("DisplayCreditCardNo", strCCNo.substring(strCCNo.length()-4,strCCNo.length()));
								eleCustomerPaymentMethod.setAttribute("DisplayPrimaryAccountNo", strCCNo.substring(strCCNo.length()-4,strCCNo.length()));
							}else {
								eleCustomerPaymentMethod.setAttribute("DisplayCreditCardNo", strDCCNo);
								eleCustomerPaymentMethod.setAttribute("DisplayPrimaryAccountNo", strDCCNo);
							}
							eleCustomerPaymentMethod.setAttribute("CreditCardType", strCCType);
							eleCustomerPaymentMethod.setAttribute("CreditCardExpDate", strCCExpDate);
							eleCustomerPaymentMethod.setAttribute("FirstName", fName);
							if(!lName.equalsIgnoreCase("N/A")){
								eleCustomerPaymentMethod.setAttribute("LastName", lName);
							}
							eleCustomerPaymentMethod.setAttribute("PaymentReference2",strCCNo);
							eleCustomerPaymentMethod.setAttribute("PaymentType", "CREDIT_CARD");
							eleCustomerPaymentMethod.setAttribute("PaymentTypeGroup", "CREDIT_CARD");
							eleCustomerPaymentMethod.setAttribute("IsDefaultMethod", "N");
							eleCustomerPaymentMethod.setAttribute("Code", custCode);
						}else if("TXEP".equals(groupCode)){
							eleCustomerExtn.setAttribute("ExtnTaxExemptFlag", "Y");
							eleCustomerExtn.setAttribute("ExtnTaxExemptionCode", custComment);
						}else if("PFCR".equals(groupCode)){
							eleCustomerExtn.setAttribute("ExtnPreferredCarrier", custCode);
							eleCustomerExtn.setAttribute("ExtnPreferredCarrierDescription", custCode);
						}
						//OMS-1094 : Start
						else if("TVIP".equals(groupCode)){
							eleCustomerExtn.setAttribute("ExtnVIPFlag", "Y");
						}
						//OMS-1094 : End
						//START - Loyalty 2.1
						else if("ENB".equals(groupCode)){
							eleCustomerExtn.setAttribute("ExtnLoyaltyCustomer", "Y");
						}
						//END - Loyalty 2.1
					}
				}
				
				// ARS-182 : Demographics changes to view and edit customer opt ins : START
				if (VSIConstants.ONE.equals(strPostalOptInCode)) {
					eleCustomer.setAttribute(VSIConstants.ATTR_POSTAL_OPT_IN, VSIConstants.FLAG_Y);
				} else {
					eleCustomer.setAttribute(VSIConstants.ATTR_POSTAL_OPT_IN, VSIConstants.FLAG_N);
				}
				if (VSIConstants.ONE.equals(strPhoneOptInCode)) {
					eleCustomer.setAttribute(VSIConstants.ATTR_PHONE_OPT_IN, VSIConstants.FLAG_Y);
				} else {
					eleCustomer.setAttribute(VSIConstants.ATTR_PHONE_OPT_IN, VSIConstants.FLAG_N);
				}
				if (VSIConstants.ONE.equals(strEmailOptInCode)) {
					eleCustomer.setAttribute(VSIConstants.ATTR_EMAIL_OPT_IN, VSIConstants.FLAG_Y);
				} else {
					eleCustomer.setAttribute(VSIConstants.ATTR_EMAIL_OPT_IN, VSIConstants.FLAG_N);
				}
				// ARS-182 : Demographics changes to view and edit customer opt ins : END
				if (customerRemarkItr != null && customerRemarkItr.hasNext()) {

					Element eleNoteList = docResponse
							.createElement("NoteList");
					eleCustomer.appendChild(eleNoteList);
					List<Element> arrNotes = new ArrayList<Element>();
					while (customerRemarkItr.hasNext() == true) {
						YFCElement customerRemarkEle = customerRemarkItr.next();
						if (!YFCObject.isVoid(customerRemarkEle)) {
							// Form the element for NoteList and appent it to
							// the appropriate location based on the avove code.
							String strCustomerRemark = customerRemarkEle
									.getChildElement("b:Remark").getNodeValue();
							String strCustomerRemarkID = customerRemarkEle
									.getChildElement("b:RemarkID").getNodeValue();
							String strContactTime = customerRemarkEle
									.getChildElement("b:CreateDate").getNodeValue();
							
							if (!YFCObject.isVoid(strCustomerRemark)) {
								Element eleNote = docResponse
										.createElement("Note");
								//OMS-1683 START
								//eleNoteList.appendChild(eleNote);
								//OMS-1683 END
								eleNote.setAttribute("NoteText",
										strCustomerRemark);
								eleNote.setAttribute("NoteID",
										strCustomerRemarkID);
								//OMS-1683 START
								eleNote.setAttribute(VSIConstants.ATTR_CONTACT_TIME,
										strContactTime);
								arrNotes.add(eleNote);
							}
						}
					}
					Collections.sort(arrNotes, new Comparator<Element>() {
						public int compare(Element eleNote1, Element eleNote2) {
							return eleNote2.getAttribute("NoteID").compareTo(eleNote1.getAttribute("NoteID"));
						};
					});
					for(Element eleNoteItr : arrNotes){
						eleNoteList.appendChild(eleNoteItr);
					}
					//OMS-1683 END
				
				}
			}
		} else {
			// no elements
			YFCDocument ifnoresult = YFCDocument
					.getDocumentFor(VSIConstants.CUSTOMER_LIST_UNAVAILABLE);
			docResponse = ifnoresult.getDocument();
		}
		return docResponse;
	
}
	
	public Document processFindCustomersOutput(YFSEnvironment env, Document srcResult) throws NumberFormatException, ParserConfigurationException {
		
		 Document docResponse = SCXmlUtil.createDocument("CustomerList");
	     YFCIterable<YFCElement> resultsItr = null;
	    
	     if(log.isDebugEnabled()){
	    	 log.debug(" Inside Method processFindCustomersOutput: Printing Input XML : \n "+XMLUtil.getXMLString(srcResult));
	     }
	     YFCDocument apiResponse=YFCDocument.getDocumentFor("<CustomerList> </CustomerList>");
	     if(srcResult != null && !srcResult.getDocumentElement().getNodeName().equalsIgnoreCase("Customer") ){
				YFCElement results=YFCDocument.getDocumentFor(srcResult).getDocumentElement().getChildElement("s:Body").
						getChildElement("FindCustomersResponse").getChildElement("FindCustomersResult").getChildElement("b:Items");
				if(results.hasChildNodes()){
					resultsItr=results.getChildren("b:CustomerFKWCF");
				}
				
				
			}else{
				
				if(log.isDebugEnabled()){
					log.debug("srcResult == Null");
				}
				Document customerListDoc = XMLUtil.createDocument("CustomerList");
				  Element customerListDocEle = customerListDoc.getDocumentElement();
				  Element customerEle = XMLUtil.appendChild(customerListDoc, customerListDocEle, "Customer", "");	
				  customerEle.setAttribute("IsCustomerAvailable", "N");
				  return customerListDoc;
			}
	     if(resultsItr!=null && resultsItr.hasNext()){
			
			   YFCElement custList=apiResponse.getDocumentElement();
			  while(resultsItr.hasNext()==true)
			  {
				  YFCElement result=resultsItr.next();
				  String custID= result.getChildElement("b:Number").getNodeValue();  //Number=HealthyRewardsNo in ISE, this HealthyRewardsNo is CustomerID for Sterling
				  String custHANo = null;
				  String add1= null;
				  String add2= null;
				  String city=null;
				  String state=null;
				 String pcode= null;
				  String telNo= null;
				  try {
				  Document healthyRewardsIPXML = XMLUtil.createDocument("Customer");
				  healthyRewardsIPXML.getDocumentElement().setAttribute("HealthyAwardsNo", custID);
						  
				  if(log.isDebugEnabled()){
					  log.debug(" healthyRewardsIPXML : "+XMLUtil.getXMLString(healthyRewardsIPXML));
				  }
					Document healthyRewardsOPXML = VSIUtils.invokeService(env, "VSIGetCustRewardPoints", healthyRewardsIPXML);
					if(healthyRewardsOPXML != null && !"N".equals(healthyRewardsOPXML.getDocumentElement().getAttribute("IsCustomerAvailable"))){
					 custHANo=YFCDocument.getDocumentFor(healthyRewardsOPXML).getDocumentElement()
				 				.getChildElement("s:Body")
				 				.getChildElement("GetCustomerLoyaltiesResponse")
				 				.getChildElement("GetCustomerLoyaltiesResult")
				 				.getChildElement("b:Items")
				 				.getChildElement("b:CustomerLoyaltyWCF")
				 				.getChildElement("b:PostedPoints")
				 				.getNodeValue();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  String fName= result.getChildElement("b:FirstName").getNodeValue();
				  String lName= result.getChildElement("b:LastName").getNodeValue();
				  String mainAddress = result.getChildElement("b:MainAddress").getNodeValue();
				  String gender = "";
				  if(result.getChildElement("b:Gender") != null)
					  gender= result.getChildElement("b:Gender").getNodeValue();
				  String[] addressArray = mainAddress.split(",");
				  int index = 0;
				  int addressLength = addressArray.length;
				  if(addressLength > 0){
					  if(index < addressLength){
					  add1 = addressArray[index].toString();
					  index++;
					  }
					  if(index < addressLength){
						  add2 = addressArray[index].toString();
						  index++;
						  }
					  if(index < addressLength){
						  city = addressArray[index].toString();
						  index++;
						  }
					  if(index < addressLength){
						  state = addressArray[index].toString();
						  index= index + 3;
						  }
					  if(index < addressLength){
						  pcode = addressArray[index].toString();
						  index ++;
						  }
					  if(index < addressLength){
						  telNo = addressArray[index].toString();
						 
						  }
				  }
				  
				  String eMail= result.getChildElement("b:EmailAddress").getNodeValue();
				// START - 1B CRM enhancements
					Element eleOutDoc = docResponse.getDocumentElement();
					Element eleCustomer = docResponse.createElement("Customer");
					eleOutDoc.appendChild(eleCustomer);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_ID, custID);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_KEY, custID);
					eleCustomer.setAttribute(VSIConstants.ATTR_HEALTHY_REWARD_PTS,custHANo);
					// Added to make customer active in all cases.
					eleCustomer.setAttribute(VSIConstants.ATTR_STATUS,VSIConstants.STATUS_10);
					eleCustomer.setAttribute(VSIConstants.ATTR_CUSTOMER_TYPE,VSIConstants.ATTR_CUSTOMER_TYPE_02);
					eleCustomer.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE,VSIConstants.VSICOM_ENTERPRISE_CODE);
					
					Element eleExtnCustomer = docResponse.createElement(VSIConstants.ELE_EXTN);
					eleCustomer.appendChild(eleExtnCustomer);
					eleExtnCustomer.setAttribute(VSIConstants.ATTR_EXTN_PREFERRED_CARRIER, "");
					eleExtnCustomer.setAttribute(VSIConstants.ATTR_EXTN_TAX_EXAMPT_FLAG, "");
					
					Element eleCustomerContactList = docResponse.createElement("CustomerContactList");
					eleCustomer.appendChild(eleCustomerContactList);
					Element eleCustomerContact = docResponse.createElement("CustomerContact");
					eleCustomerContactList.appendChild(eleCustomerContact);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_CUST_CONTACT_ID, custID);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_FIRST_NAME,fName);
					if(!lName.equalsIgnoreCase("N/A")){
						eleCustomerContact.setAttribute(VSIConstants.ATTR_LAST_NAME,lName);
					}
					eleCustomerContact.setAttribute(VSIConstants.ATTR_EMAIL_ID,eMail);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_EXTN_GENDER, gender);
					eleCustomerContact.setAttribute(VSIConstants.ATTR_USER_ID, "");
					
					Element eleCustomerAdditionalAddressList = docResponse.createElement("CustomerAdditionalAddressList");
					eleCustomerContact.appendChild(eleCustomerAdditionalAddressList);

					//System.out.println("eleOutDoc::"+ SCXmlUtil.getString(docResponse));
					eleCustomerContact.setAttribute(VSIConstants.ATTR_DAY_PHONE,telNo);

					Element eleCustomerAdditionalAddress = docResponse.createElement("CustomerAdditionalAddress");
					eleCustomerAdditionalAddressList.appendChild(eleCustomerAdditionalAddress);
					Element elePersonInfo = docResponse.createElement(VSIConstants.ELE_PERSON_INFO);
					eleCustomerAdditionalAddress.appendChild(elePersonInfo);

					eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_CUST_ADDL_ADDR_ID, custID);
					eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO, VSIConstants.FLAG_Y);
					eleCustomerAdditionalAddress.setAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO, VSIConstants.FLAG_Y);
					
					elePersonInfo.setAttribute(VSIConstants.ATTR_ADDRESS1, add1);
					elePersonInfo.setAttribute(VSIConstants.ATTR_ADDRESS2, add2);
					elePersonInfo.setAttribute(VSIConstants.ATTR_ADDRESS3, "");
					elePersonInfo.setAttribute(VSIConstants.ATTR_CITY, city);
					elePersonInfo.setAttribute(VSIConstants.ATTR_COMPANY, "");
					elePersonInfo.setAttribute(VSIConstants.ATTR_STATE, state);
					elePersonInfo.setAttribute(VSIConstants.ATTR_ZIPCODE, pcode);
					elePersonInfo.setAttribute(VSIConstants.ATTR_COUNTRY, "US");
					elePersonInfo.setAttribute(VSIConstants.ATTR_DAY_PHONE, telNo);
					elePersonInfo.setAttribute(VSIConstants.ATTR_FIRST_NAME, fName);
					elePersonInfo.setAttribute(VSIConstants.ATTR_JOB_TITLE, "");
					if(!lName.equalsIgnoreCase("N/A")){
						elePersonInfo.setAttribute(VSIConstants.ATTR_LAST_NAME, lName);
					}
					elePersonInfo.setAttribute(VSIConstants.ATTR_MIDDLE_NAME, "");
					elePersonInfo.setAttribute(VSIConstants.ATTR_MOBILE_PHONE, "");
					elePersonInfo.setAttribute("EMailID", eMail);
					// END - 1B CRM enhancements
			  }
			  if(log.isDebugEnabled()){
				  log.debug(" Return Doc : "+SCXmlUtil.getString(docResponse));
			  }
			  return docResponse;
		 }else{
			   //no elements
			   YFCDocument ifnoresult=YFCDocument.getDocumentFor("<CustomerList IsCustomerAvailable=\"N\" />");
			   return ifnoresult.getDocument();
		   }
	}
}
