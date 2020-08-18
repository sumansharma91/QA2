package com.vsi.oms.api;

import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.util.Properties;

import org.w3c.dom.Document;

import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.ui.backend.util.APICall;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

	
public class VSICreateCustomerService implements YIFCustomApi {

	String strEnterpriseCode = "";
	public Document createCustomer(YFSEnvironment env, Document inXML) throws NumberFormatException {
		 
	   /* 
	    * Step 1: call VSICustomerManagement
	    * step 2: check if result has one or more customers.
	    * if yes: return the outDoc
	    * if No: create a DBsequence No and set the # in CustomerID field of inXML and return this updated inXML 
	    * 
	    * */ 
		
		/*
		try {
			Connection objConnection  = ((YFSConnectionHolder) env).getDBConnection();
			//System.out.println(objConnection.getMetaData().getURL());
			//System.out.println(objConnection.getMetaData().getUserName());
			String sequence=VSIDBUtil.getNextSequence(env, "Customers");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		if(!YFCObject.isVoid(inXML)){
			strEnterpriseCode = inXML.getDocumentElement().getAttribute("CallingOrganizationCode");
			if(YFCObject.isVoid(strEnterpriseCode)){
				strEnterpriseCode = inXML.getDocumentElement().getAttribute("OrganizationCode");
				
			}
		}
		Document sResponce= searchCustomer(env, inXML);
		
		////System.out.println("search responce is:"+ XMLUtil.getXMLString(sResponce));
		YFCDocument searchResponceDoc=YFCDocument.getDocumentFor(sResponce);
		YFCElement searchResponceEle=searchResponceDoc.getDocumentElement();
		try{
			if(searchResponceEle.hasAttribute("IsCallSuccesful"))
			{
			if(searchResponceEle.getAttribute("IsCallSuccesful").equalsIgnoreCase("N"))
	        	return searchResponceDoc.getDocument();
			}
	        	
			if(searchResponceEle.getChildElement("Customer").hasAttribute("CustomerID"))
				return searchResponceDoc.getDocument();
		
		}catch(Exception e){
			e.printStackTrace();
			
		}
			
		try{
			
			String sequence=VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CUST_ID);
			////System.out.println("Sequence No!!!!!!!!!!!!!!"+sequence);
			YFCDocument.getDocumentFor(inXML).getDocumentElement().setAttribute("CustomerID", sequence);
			
			}catch(Exception e){
				e.printStackTrace();
				
			}
				
		return inXML;
		
		//return null;
		
	}
	public Document searchCustomer(YFSEnvironment env, Document inXML) throws NumberFormatException {
		
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
					_xml="<CustomerList IsCallSuccesful=\"N\" />";
				else 
					_xml="<CustomerList IsCustomerAvailable=\"N\" />";
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
			  String fName= result.getChildElement("FirstName").getNodeValue();
			  String lName= result.getChildElement("LastName").getNodeValue();
			  String add1= result.getChildElement("AddressLine1").getNodeValue();
			  String add2= result.getChildElement("AddressLine2").getNodeValue();
			  String city=result.getChildElement("City").getNodeValue();
			  String state=result.getChildElement("State").getNodeValue();
			  String country= result.getChildElement("Country").getNodeValue();
			  String pcode= result.getChildElement("PostalCode").getNodeValue();
			  String telNo= result.getChildElement("TelephoneNumber").getNodeValue();
			  String eMail= result.getChildElement("EmailAddress").getNodeValue();
			  
			  String custString="<Customer CustomerID=\"\" CustomerType=\"02\" OrganizationCode=\"VSI\">"+
		                "<CustomerContactList>"+
	                    "<CustomerContact CustomerContactID=\"\" DayPhone=\"\" FirstName=\"\" LastName=\"\" UserID=\"\">"+
	                        "<CustomerAdditionalAddressList>"+
	                            "<CustomerAdditionalAddress IsDefaultShipTo=\"Y\">"+
	                                "<PersonInfo AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" City=\"\" Company=\"\" Country=\"USA\" DayPhone=\"\"  EmailID=\"\"  FirstName=\"\"  JobTitle=\"\" LastName=\"\"  MiddleName=\"\" MobilePhone=\"\" State=\"\"  ZipCode=\"\"/>"+
	                            "</CustomerAdditionalAddress>"+
	                        "</CustomerAdditionalAddressList>"+
	                    "</CustomerContact>"+
	                "</CustomerContactList>"+
	                "<CustomerAdditionalAddressList/>"+
	            "</Customer>";
			  
			  custList.addXMLToNode(custString);
			  YFCElement customer=custList.getLastChildElement();
			  customer.setAttribute("CustomerID", custID);
			  customer.setAttribute("CustomerKey", custID);
			  if(!YFCObject.isVoid(strEnterpriseCode))
				  customer.setAttribute("OrganizationCode", strEnterpriseCode);
			  YFCElement custContact= customer.getChildElement("CustomerContactList").getChildElement("CustomerContact");
			  custContact.setAttribute("DayPhone", telNo);
			  custContact.setAttribute("FirstName", fName);
			  custContact.setAttribute("LastName", lName);
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
			  perInfo.setAttribute("EmailID", eMail);
			  perInfo.setAttribute("Country", country);
			  perInfo.setAttribute("DayPhone", telNo);
			  perInfo.setAttribute("FirstName", fName);
			  perInfo.setAttribute("LastName", lName);
			  
			  
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
