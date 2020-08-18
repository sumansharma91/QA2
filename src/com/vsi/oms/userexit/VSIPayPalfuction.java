package com.vsi.oms.userexit;

import java.net.URLDecoder.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.StringTokenizer.*;
import java.io.*;
import java.net.*;

import javax.net.ssl.*; 
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIPayPalfuction {
	
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIPayPalfuction.class.getName());
	
	   
	    public static HashMap DoAuthorization(YFSEnvironment env,String currencyCodestr,String customStr,String transactionIdStr,String requestAmountStr,String orderHeaderKey) throws ParserConfigurationException,
	    YIFClientCreationException, YFSException, RemoteException    
	          {
			
			String gv_APIUserName1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIUserName");
			String gv_APIPassword1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIPassword");
			String gv_APISignature1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APISignature");
	    	
	    	////System.out.println("Inside paypal  Do authoization loop");
	        	  String nvpStr = "&AMT=" + requestAmountStr; 
	     	      nvpStr += "&CURRENCYCODE=" + currencyCodestr;
	     	     // nvpStr += "&CUSTOM=" + customStr;
	     	      nvpStr += "&TRANSACTIONID=" + transactionIdStr;
	     	      
	     	     Document doc = XMLUtil.createDocument("PaymentRecords");
	       		Element elePayRecrds = doc.getDocumentElement();
	       		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
	       		String ecodestr= "METHOD=" + "DoAuthorization" + "&VERSION=" + "98.0" + "&PWD=" + gv_APIPassword1 + "&USER=" + gv_APIUserName1 + "&SIGNATURE=" + gv_APISignature1 + nvpStr + "&BUTTONSOURCE=" + "null";
	       		elePayRecrds.setAttribute("Record", ecodestr);
	       		elePayRecrds.setAttribute("ChargeType", "Authorize Request");
	       		YIFApi api;
	       		api = YIFClientFactory.getInstance().getApi();
	       		api.executeFlow(env,"VSIPaymentRecords", doc);
	       		
	        	  HashMap nvp = httpcall(env,"DoAuthorization", nvpStr,gv_APIUserName1,gv_APIPassword1,gv_APISignature1);
	        	//Printing the Response
	        	  if(log.isDebugEnabled()){
			    		log.debug("Response:"+nvp.toString());
	        	  }
	        	  ////System.out.println("after call"+nvp);            
	        	  return nvp;                        
	          
	   
	          }
	    
	    public static HashMap DoCapture(YFSEnvironment env,String currencyCodestr,String Notes,String completeType,String authorizationIdStr,
	    		String requestAmountStr,String orderHeaderKey) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException    
       
	    {
		  
	    	String gv_APIUserName1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIUserName");
			String gv_APIPassword1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIPassword");
			String gv_APISignature1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APISignature");
			
	      ////System.out.println("Inside paypal  Do Capture loop");
      	  String nvpStr = "&AMT=" + requestAmountStr; 
   	      nvpStr += "&CURRENCYCODE=" + currencyCodestr;
   	      nvpStr += "&NOTES=" + Notes;
   	      nvpStr += "&COMPLETETYPE=" + completeType;
   	      nvpStr += "&AUTHORIZATIONID=" + authorizationIdStr;
   	   
  	     Document doc = XMLUtil.createDocument("PaymentRecords");
    		Element elePayRecrds = doc.getDocumentElement();
    		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
    		String ecodestr= "METHOD=" + "DoAuthorization" + "&VERSION=" + "98.0" + "&PWD=" + gv_APIPassword1 + "&USER=" + gv_APIUserName1 + "&SIGNATURE=" + gv_APISignature1 + nvpStr + "&BUTTONSOURCE=" + "null";
    		elePayRecrds.setAttribute("Record", ecodestr);
    		elePayRecrds.setAttribute("ChargeType", "Settlement Request");
    		YIFApi api;
    		api = YIFClientFactory.getInstance().getApi();
    		api.executeFlow(env,"VSIPaymentRecords", doc);
    		
      	  HashMap nvp = httpcall(env,"DoCapture", nvpStr,gv_APIUserName1,gv_APIPassword1,gv_APISignature1);
      	  
      	  //System.out.println("after call"+nvp);            
      	  return nvp;                        }
	    
	    
	    public static HashMap DoRefund(YFSEnvironment env,String currencyCodestr,String Notes,String refundType,String transactionIddStr,
	    		String requestAmountStr,String orderHeaderKey) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException    
       
	    {
	     	String gv_APIUserName1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIUserName");
			String gv_APIPassword1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIPassword");
			String gv_APISignature1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APISignature");
	    	
	      ////System.out.println("Inside paypal  Do Capture loop");
      	  String nvpStr = "&AMT=" + requestAmountStr; 
   	      nvpStr += "&CURRENCYCODE=" + currencyCodestr;
   	      nvpStr += "&REFUNDTYPE=" + refundType;
   	      nvpStr += "&NOTES=" + Notes;
   	      nvpStr += "&TRANSACTIONID=" + transactionIddStr;
   	   Document doc = XMLUtil.createDocument("PaymentRecords");
		Element elePayRecrds = doc.getDocumentElement();
		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
		String ecodestr= "METHOD=" + "DoAuthorization" + "&VERSION=" + "98.0" + "&PWD=" + gv_APIPassword1 + "&USER=" + gv_APIUserName1 + "&SIGNATURE=" + gv_APISignature1 + nvpStr + "&BUTTONSOURCE=" + "null";
		elePayRecrds.setAttribute("Record", ecodestr);
		elePayRecrds.setAttribute("ChargeType", "Refund Request");
		YIFApi api;
		api = YIFClientFactory.getInstance().getApi();
		api.executeFlow(env,"VSIPaymentRecords", doc);
   	      
      	  HashMap nvp = httpcall(env,"RefundTransaction", nvpStr,gv_APIUserName1,gv_APIPassword1,gv_APISignature1);
      	  
      	  //System.out.println("after call"+nvp);            
      	  return nvp;                        }
        

	    
	public static HashMap httpcall( YFSEnvironment env,String methodName, String nvpStr ,String gv_APIUserName1,
			String gv_APIPassword1,String gv_APISignature1) throws ParserConfigurationException
    {
		
		String PAYPAL_URL=null;
		String gv_APIEndpoint=null;
		//String gv_APIUserName = "paypal-merch1_api1.vitaminshoppe.com";
		//String gv_APIPassword = "LCHE86ZDJASQ4JTY";	
		//String gv_APISignature = "AFcWxV21C7fd0v3bYYYRCpSSRl31AGYj-4qaJ5gPnf2ANdD71CiZJKph";
		
		String gv_APIUserName=gv_APIUserName1;
		String gv_APIPassword=gv_APIPassword1;
		String gv_APISignature=gv_APISignature1;

		
		String gv_Version=null ;
		String gv_ProxyServer;
		String gv_ProxyServerPort;
		String gv_BNCode=null;
		int gv_Proxy;
		boolean gv_UseProxy;
		boolean USE_PROXY = false;

		
		
			//gv_APIEndpoint = "https://api-3t.sandbox.paypal.com/nvp";		
			//PAYPAL_URL = "https://www.sandbox.paypal.com/webscr?cmd=_express-checkout-mobile&token=";	
			// gv_APIEndpoint=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","PayPal_EndPoint");

	    	//gv_APIEndpoint = "https://api-3t.paypal.com/nvp";		
			//PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
			
			 gv_APIEndpoint=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","PayPal_EndPoint");

				
		
		          
		          String HTTPREQUEST_PROXYSETTING_SERVER = "";	
		          String HTTPREQUEST_PROXYSETTING_PORT = "";	
		          gv_Version= "98.0";	
		          gv_ProxyServer	= HTTPREQUEST_PROXYSETTING_SERVER;
		          gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;	
		          gv_Proxy	= 2;
		         // gv_UseProxy = USE_PROXY;
	              String version = "98.0";
                  String agent = "Mozilla/4.0";
                  String respText = "";
                  HashMap nvp=null;
                  
                //Get TimeOut property from commonCode
				VSIUtils vsiUtils = new VSIUtils();
				Document docGetCommonCodeList = XMLUtil
						.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
				Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
//				eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,
						"AUTH_TIME_OUT_PROP");
				Document commonCodeOut = vsiUtils.getCommonCodeList(env,
						docGetCommonCodeList);
				Element eleCommonCodeOut = (Element) commonCodeOut.getDocumentElement()
						.getElementsByTagName(VSIConstants.ELE_COMMON_CODE).item(0);
				int intTimeOut = Integer.parseInt(eleCommonCodeOut
						.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION));
				//System.out.println("HTTPCALL_TIMEOUT:::"+intTimeOut);
             
             String encodedData = "METHOD=" + methodName + "&VERSION=" + gv_Version + "&PWD=" + gv_APIPassword + "&USER=" + gv_APIUserName + "&SIGNATURE=" + gv_APISignature + nvpStr + "&BUTTONSOURCE=" + gv_BNCode;
             //System.out.println("encodedData is :"+encodedData);
             try 
             {
                 //System.out.println("url is :" + gv_APIEndpoint);

            	 URL postURL = new URL( gv_APIEndpoint );
                     HttpURLConnection conn = (HttpURLConnection)postURL.openConnection();
                     conn.setDoInput (true);
                     conn.setDoOutput (true);
                     conn.setConnectTimeout(intTimeOut);
                     conn.setReadTimeout(intTimeOut);
                     conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                     conn.setRequestProperty( "User-Agent", agent );
                     
                     conn.setRequestProperty( "Content-Length", String.valueOf( encodedData.length()) );
                     ////System.out.println("encodedData:" + encodedData);
                     conn.setRequestMethod("POST");
                     
                     DataOutputStream output = new DataOutputStream( conn.getOutputStream());
                     output.writeBytes( encodedData );
                     output.flush();
                     output.close ();
                     
                     DataInputStream  in = new DataInputStream (conn.getInputStream()); 
                     int rc = conn.getResponseCode();
                     if ( rc != -1)
                     {
                             BufferedReader is = new BufferedReader(new InputStreamReader( conn.getInputStream()));
                             String _line = null;
                             while(((_line = is.readLine()) !=null))
                             {
                                     respText = respText + _line;
                             }               
                             nvp = deformatNVP( respText );
                     }
                     return nvp;
             }
             catch( IOException e )
             {
                     return null;
             }
    



}
	public static HashMap deformatNVP(String pPayload) {
        HashMap nvp = new HashMap();
        StringTokenizer stTok = new StringTokenizer(pPayload, "&");
        while (stTok.hasMoreTokens()) {
                 StringTokenizer stInternalTokenizer = new StringTokenizer(
                                   stTok.nextToken(), "=");
                 if (stInternalTokenizer.countTokens() == 2) {
                          String key = URLDecoder.decode(stInternalTokenizer.nextToken());
                          String value = URLDecoder.decode(stInternalTokenizer
                                           .nextToken());
                          nvp.put(key.toUpperCase(), value);
                 }
        }
        return nvp;
}
	public static Document getCommonCodeListInputForCodeValue(String sOrgCode,
			String codeValue) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement();
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeValue", codeValue);
		return docOutput;
	}

	public static String getCommonCodeLongDescriptionByCodeValue(
			YFSEnvironment env, String sOrgCode, String sCodeName)
			throws ParserConfigurationException{
		String sCommonCodeValue = "";
		Document docgetCCListInput = getCommonCodeListInputForCodeValue(
				sOrgCode, sCodeName);
		Document docCCList = getCommonCodeList(env, docgetCCListInput);
		Element eleComCode = null;
		if (docCCList != null) {
			eleComCode = (Element) docCCList.getElementsByTagName("CommonCode")
					.item(0);
		}
		if (eleComCode != null) {
			sCommonCodeValue = eleComCode.getAttribute("CodeLongDescription");
		}
		return getEmptyCheckedString(sCommonCodeValue);
	}

	public static Document getCommonCodeList(YFSEnvironment env,
			Document docApiInput) {
		Document docApiOutput = null;
		try {
			YIFApi api;
			api = YIFClientFactory.getInstance().getApi();
			docApiOutput = api.invoke(env, "getCommonCodeList", docApiInput);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return docApiOutput;
	}

	public static String getEmptyCheckedString(String str) {
		if (isEmpty(str)) {
			return EMPTY_STRING;
		} else {
			return str.trim();
		}
	}

	public static final String EMPTY_STRING = "";

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0) ? true : false;
	}
	
	public static Document getCommonCodeListInputForCodeType(String sOrgCode,
			String codeType) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeType", codeType);
		return docOutput;
	}
	
	// Fix for OMS-713
	public static HashMap getTransactionDetails(YFSEnvironment env, String transactionID) throws ParserConfigurationException{
		
		if(log.isDebugEnabled()){
			log.verbose("Entering method : VSIPayPalfuction.getTransactionDetails");
		}
		String gv_APIUserName1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIUserName");
		String gv_APIPassword1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APIPassword");
		String gv_APISignature1=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","APISignature");
		
	  String nvpStr = "&CURRENCYCODE=" + "USD";
	      nvpStr += "&TRANSACTIONID=" + transactionID;
	  
	  HashMap nvp = httpcall(env,"GetTransactionDetails", nvpStr,gv_APIUserName1,gv_APIPassword1,gv_APISignature1);
	  
	  //System.out.println("after call"+nvp);            
	  return nvp;   
		
	}
}
