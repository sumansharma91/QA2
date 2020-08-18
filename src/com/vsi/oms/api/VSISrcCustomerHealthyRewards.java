package com.vsi.oms.api;


import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.InvokeWebservice;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

	public class VSISrcCustomerHealthyRewards  implements YIFCustomApi {
		String strEnterpriseCode = "";
		private YFCLogCategory log = YFCLogCategory
				.instance(VSISrcCustomerHealthyRewards.class);
		YIFApi api;
		public Document invokeCRMHA(YFSEnvironment env, Document inXML) throws NumberFormatException {
			
			Element eleInput = inXML.getDocumentElement();
				strEnterpriseCode = eleInput.getAttribute("CallingOrganizationCode");
				if(YFCObject.isVoid(strEnterpriseCode)){
					strEnterpriseCode = eleInput.getAttribute("OrganizationCode");
					
				}
			
			String strCustomerKey = eleInput.getAttribute("CustomerKey");
			if(YFCObject.isVoid(strCustomerKey)) {
			String xml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cus=\"http://www.vitaminshoppe.com/SingleViewOfCustomer/Domain/Customer\">"+
					   "<soapenv:Header/>"+
					   "<soapenv:Body>"+
					      "<cus:getCustomer>"+
					         "<customerHANumber> </customerHANumber>"+
					      "</cus:getCustomer>"+
					   "</soapenv:Body>"+
					"</soapenv:Envelope>";
			
			 Document srchOut = null;
			 
			 YFCElement custSer=YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("CustomerContactList").getChildElement("CustomerContact");
			 String custHealthyRwdNo=custSer.getAttribute("HealthyAwardsNo");
			 

			 
			 YFCDocument custHAInput = YFCDocument.getDocumentFor(xml); 
		     YFCElement custHAInputEle = custHAInput.getDocumentElement();
		     YFCElement vEnvelope=custHAInputEle.getChildElement("soapenv:Body", true).getChildElement("cus:getCustomer", true); 
		     YFCElement tmp=vEnvelope.getChildElement("customerHANumber");
		     tmp.setNodeValue(custHealthyRwdNo);
		     
		     
		     try {
		    	 srchOut=InvokeWebservice.invokeCRMWebService(custHAInput.getDocument());
		    	 
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
					//System.out.print(e);
					YFCDocument ifcallFailed=YFCDocument.getDocumentFor("<Customer IsCallSuccessfull=\"N\" />");
					return ifcallFailed.getDocument();
 
					
				}
		     
			 YFCElement result=YFCDocument.getDocumentFor(srchOut).getDocumentElement().getChildElement("soapenv:Body").getChildElement("cu:getCustomerResponse").getChildElement("customer");
			  String custHANo= result.getChildElement("CustomerHANumber").getNodeValue();
			  
			  
			  StringBuilder healthyRewardsXML= new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rew=\"http://www.vitaminshoppe.com/SingleViewOfCustomer/Domain/Rewards\">");
			  healthyRewardsXML.append("<soapenv:Header/>").append("<soapenv:Body>")
			  				.append("<rew:getRewardPoints>")
			  					.append("<HANumber></HANumber>")
			  				.append("</rew:getRewardPoints>")
			  				.append("</soapenv:Body>").append(	"</soapenv:Envelope>");
			  
			  YFCDocument healthyRewardXML =YFCDocument.getDocumentFor(healthyRewardsXML.toString());
			  healthyRewardXML.getDocumentElement()
			  					.getChildElement("soapenv:Body")
			  					.getChildElement("rew:getRewardPoints")
			  					.getChildElement("HANumber")
			  					.setNodeValue(custHANo);
			  String custHealthyRewardPoints="";
			  try {
				  Document hrResultDoc= InvokeWebservice.invokeHRService(healthyRewardXML.getDocument());
				  custHealthyRewardPoints=YFCDocument.getDocumentFor(hrResultDoc).getDocumentElement()
			 				.getChildElement("soapenv:Body")
			 				.getChildElement("re:getRewardPointsResponse")
			 				.getChildElement("points")
			 				.getChildElement("PointsCurrentBalance").getNodeValue();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			  
			  String fName= result.getChildElement("FirstName").getNodeValue();
			  String lName= result.getChildElement("LastName").getNodeValue();
			  String add1= result.getChildElement("AddressLine1").getNodeValue();
			  String add2= result.getChildElement("AddressLine2").getNodeValue();
			  String city=result.getChildElement("City").getNodeValue();
			  String state=result.getChildElement("State").getNodeValue();
			  String pcode= result.getChildElement("PostalCode").getNodeValue();
			  String telNo= result.getChildElement("TelephoneNumber").getNodeValue();
			  String eMail= result.getChildElement("EmailAddress").getNodeValue();
		
		       YFCDocument apiResponse=YFCDocument.getDocumentFor("<CustomerList> </CustomerList>");
			   YFCElement custList=apiResponse.getDocumentElement();

				  String custString="<Customer CustomerID=\"\" CustomerType=\"02\" OrganizationCode=\"VSI\">"+
			                "<CustomerContactList>"+
		                    "<CustomerContact CustomerContactID=\"\" DayPhone=\"\" FirstName=\"\" LastName=\"\" UserID=\"\" EmailID=\"\" >"+
		                        "<CustomerAdditionalAddressList>"+
		                            "<CustomerAdditionalAddress IsDefaultShipTo=\"Y\">"+
		                                "<PersonInfo EMailID=\"\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" City=\"\" Company=\"\" Country=\"US\" DayPhone=\"\"   FirstName=\"\"  JobTitle=\"\" LastName=\"\"  MiddleName=\"\" MobilePhone=\"\" State=\"\"  ZipCode=\"\"/>"+
		                            "</CustomerAdditionalAddress>"+
		                        "</CustomerAdditionalAddressList>"+
		                    "</CustomerContact>"+
		                "</CustomerContactList>"+
		                "<CustomerAdditionalAddressList/>"+
		            "</Customer>";
				  
				  custList.addXMLToNode(custString);
				  YFCElement customer=custList.getLastChildElement();
				  if(!YFCObject.isVoid(strEnterpriseCode))
					  customer.setAttribute("OrganizationCode", strEnterpriseCode);
				  customer.setAttribute("CustomerID", custHANo);
				  customer.setAttribute("HealthyRewardPoints", custHealthyRewardPoints);
				  YFCElement custContact= customer.getChildElement("CustomerContactList").getChildElement("CustomerContact");
				  custContact.setAttribute("CustomerContactID", custHANo);
				  custContact.setAttribute("DayPhone", telNo);
				  custContact.setAttribute("FirstName", fName);
				  custContact.setAttribute("LastName", lName);
				  custContact.setAttribute("EmailID", eMail);
				  YFCElement custAdditionalAddress= custContact.getChildElement("CustomerAdditionalAddressList").getChildElement("CustomerAdditionalAddress");
				  YFCElement perInfo = custAdditionalAddress.getChildElement("PersonInfo");
				  custAdditionalAddress.setAttribute("CustomerAdditionalAddressID", custHANo);
				  custAdditionalAddress.setAttribute("IsDefaultShipTo", "Y");
				  custAdditionalAddress.setAttribute("IsDefaultBillTo", "Y");
				  perInfo.setAttribute("AddressLine1", add1);
				  perInfo.setAttribute("AddressLine2", add2);
				  perInfo.setAttribute("City", city);
				  perInfo.setAttribute("State", state);
				  perInfo.setAttribute("ZipCode", pcode);
				  perInfo.setAttribute("DayPhone", telNo);
				  perInfo.setAttribute("FirstName", fName);
				  perInfo.setAttribute("LastName", lName);
				  perInfo.setAttribute("EMailID", eMail);
				  
				  
				  return apiResponse.getDocument();
			} 
			 return inXML;
	}
		   
		
		
		@Override
		public void setProperties(Properties arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}

	

}
