package com.vsi.oms.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.vsi.oms.utils.InvokeWebservice;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.pca.ycd.jasperreports.returnOrderSummaryReportScriptlet;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICustomerManagement implements YIFCustomApi {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSICustomerManagement.class);
	YIFApi api;
	public Document invoke(YFSEnvironment env, Document inXML) throws NumberFormatException {
		
		String strEnterpriseCode = "";
		if(!YFCObject.isVoid(inXML)){
			strEnterpriseCode = inXML.getDocumentElement().getAttribute("CallingOrganizationCode");
			if(YFCObject.isVoid(strEnterpriseCode)){
				strEnterpriseCode = inXML.getDocumentElement().getAttribute("OrganizationCode");
				
			}
		}
		String xml ="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cus=\"http://www.vitaminshoppe.com/SingleViewOfCustomer/Domain/Customer\">"+
			   "<soapenv:Header/>"+
			   "<soapenv:Body>"+
			      "<cus:searchCustomer>"+
			         "<customerSearchCriteria>"+
						"<FirstName></FirstName>"+
						"<LastName></LastName>"+
						"<TelephoneNumber></TelephoneNumber>"+
			            "<EmailAddress></EmailAddress>"+
			            "<StreetAddress1></StreetAddress1>"+
			            "<PostalCode></PostalCode>"+
			         "</customerSearchCriteria>"+
			      "</cus:searchCustomer>"+
			   "</soapenv:Body>"+
			"</soapenv:Envelope>";
		 Document srchOut = null;
		 
		 YFCElement custSer=YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("CustomerContactList").getChildElement("CustomerContact");
		 String phone=custSer.getAttribute("DayPhone");
		 String emailId=custSer.getAttribute("EmailID");
		 String firstN=custSer.getAttribute("FirstName");
		 String lastN=custSer.getAttribute("LastName");
		 YFCElement custCont=YFCDocument.getDocumentFor(inXML).getDocumentElement().getChildElement("CustomerContactList").getChildElement("CustomerContact").getChildElement("CustomerAdditionalAddressList").getChildElement("CustomerAdditionalAddress").getChildElement("PersonInfo");
		 String StrAdd1=custCont.getAttribute("AddressLine1");
		 String zipCode=custCont.getAttribute("ZipCode");
		 
		 YFCDocument custInput = YFCDocument.getDocumentFor(xml); 
	     YFCElement custInputEle = custInput.getDocumentElement();
	     YFCElement vEnvelope=custInputEle.getChildElement("soapenv:Body", true).getChildElement("cus:searchCustomer", true); 
	     YFCElement tmp=vEnvelope.getChildElement("customerSearchCriteria");
	     tmp.getChildElement("FirstName").setNodeValue(firstN);
	     tmp.getChildElement("LastName").setNodeValue(lastN);
	     tmp.getChildElement("TelephoneNumber").setNodeValue(phone);
	     tmp.getChildElement("EmailAddress").setNodeValue(emailId);
	     tmp.getChildElement("StreetAddress1").setNodeValue(StrAdd1);
	     tmp.getChildElement("PostalCode").setNodeValue(zipCode);
	     
	     YFCIterable<YFCElement> resultsItr;
	    
	     try {
	    	 srchOut=InvokeWebservice.invokeCRMWebService(custInput.getDocument());
	    	 YFCElement results=YFCDocument.getDocumentFor(srchOut).getDocumentElement().getChildElement("soapenv:Body").getChildElement("cu:searchCustomerResponse").getChildElement("results");
	    	 resultsItr=results.getChildren("results");
			}
	     catch (Exception e) {
				e.printStackTrace();
				//System.out.print(e);
				 String _xml=null;
				if(e instanceof SocketTimeoutException)
					_xml="<Customer IsCallSuccesful=\"N\" />";
				else 
					_xml="<Customer IsCustomerAvailable=\"N\" />";
				YFCDocument ifcallFailed=YFCDocument.getDocumentFor(_xml);
				return ifcallFailed.getDocument();  
				
			}
	     
	  //   YFCDocument srchOutDoc=YFCDocument.getDocumentFor(srchOut);
	    
	    
	   if(resultsItr.hasNext() && resultsItr!=null){
		   YFCDocument apiResponse=YFCDocument.getDocumentFor("<CustomerList> </CustomerList>");
		   YFCElement custList=apiResponse.getDocumentElement();
		  while(resultsItr.hasNext()==true)
		  {
			  YFCElement result=resultsItr.next();
			  String custID= result.getChildElement("Number").getNodeValue();  //Number=HealthyRewardsNo in ISE, this HealthyRewardsNo is CustomerID for Sterling
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
			  					.setNodeValue(custID);
			  String custHANo="";
			  try {
				  Document hrResultDoc= InvokeWebservice.invokeHRService(healthyRewardXML.getDocument());
				  custHANo=YFCDocument.getDocumentFor(hrResultDoc).getDocumentElement()
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
			 // String country= result.getChildElement("Country").getNodeValue();
			  String pcode= result.getChildElement("PostalCode").getNodeValue();
			  String telNo= result.getChildElement("TelephoneNumber").getNodeValue();
			  String eMail= result.getChildElement("EmailAddress").getNodeValue();
			  
			  
			  String custString="<Customer CustomerID=\"\" CustomerType=\"02\" OrganizationCode=\"VSI.com\">"+
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
//			  if(!YFCObject.isVoid(strEnterpriseCode))
//				  customer.setAttribute("OrganizationCode", strEnterpriseCode);
			  customer.setAttribute("CustomerID", custID);
			  customer.setAttribute("CustomerKey", custID);
			  customer.setAttribute("HealthyRewardPoints", custHANo);
			  YFCElement custContact= customer.getChildElement("CustomerContactList").getChildElement("CustomerContact");
			  custContact.setAttribute("DayPhone", telNo);
			  custContact.setAttribute("FirstName", fName);
			  custContact.setAttribute("LastName", lName);
			  custContact.setAttribute("EmailID", eMail);
			  custContact.setAttribute("CustomerContactID", custID);
			  YFCElement custAdditionalAddress= custContact.getChildElement("CustomerAdditionalAddressList").getChildElement("CustomerAdditionalAddress");
			  YFCElement perInfo = custAdditionalAddress.getChildElement("PersonInfo");
			  custAdditionalAddress.setAttribute("IsDefaultShipTo", "Y");
			  custAdditionalAddress.setAttribute("IsDefaultBillTo", "Y");
			  custAdditionalAddress.setAttribute("CustomerAdditionalAddressID", custID);
			  perInfo.setAttribute("AddressLine1", add1);
			  perInfo.setAttribute("AddressLine2", add2);
			  perInfo.setAttribute("City", city);
			  perInfo.setAttribute("State", state);
			  perInfo.setAttribute("ZipCode", pcode);
			  perInfo.setAttribute("Country", "US");
			  perInfo.setAttribute("DayPhone", telNo);
			  perInfo.setAttribute("FirstName", fName);
			  perInfo.setAttribute("LastName", lName);
			  perInfo.setAttribute("EMailID", eMail);
			  
			  
			  			  
			  }
		  return apiResponse.getDocument();
	   }
	   else{
		   //no elements
		   YFCDocument ifnoresult=YFCDocument.getDocumentFor("<Customer IsCustomerAvailable=\"N\" />");
		   return ifnoresult.getDocument();
	   }
	
	}
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
