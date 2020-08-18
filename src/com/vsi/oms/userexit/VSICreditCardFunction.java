
package com.vsi.oms.userexit;

import java.net.URLDecoder.*;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.StringTokenizer.*;
import java.io.*;
import java.net.*;

import javax.net.ssl.*; 
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICreditCardFunction {
                
	 static {
		 javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
		new javax.net.ssl.HostnameVerifier(){

		public boolean verify(String hostname,
		      javax.net.ssl.SSLSession sslSession) {
		  //OMS-868 :Start
	      final String strDatePowerHost= YFSSystem.getProperty("APP_VSI_DATAPOWER_HOST"); 
		  if (hostname.equals(strDatePowerHost)) {
		  //OMS-868 :End
		      return true;
		  }
		  return false;
		}
		});
		}
		                   
                   /** public static HashMap DoAuthorization(String Orderid,String merchangtId,String apiUsername,String apiPassword, String requestAmountStr,String currencyCodestr,String transactionIdStr,
                                                String sourceOfFundstype,String tokenId)    
                 * @throws YIFClientCreationException 
                 * @throws ParserConfigurationException 
                 * @throws RemoteException 
                 * @throws YFSException 
                 * @throws NoSuchAlgorithmException 
                 * @throws KeyManagementException **/
//, String KountToken add this to method parameters when kounttoken mapping is verified
                    public static HashMap DoAuthorization(String Orderid,String merchangtId, String requestAmountStr,String currencyCodestr,String transactionIdStr,
                                                String sourceOfFundstype,String tokenId,String firstName,String lastName,
                                                String expYr,String expMnth,String AdressLine1,String City,String ZipCode,String State,String country,
                                                YFSEnvironment env, String orderHeaderKey) throws YFSException, RemoteException, ParserConfigurationException, 
                                                YIFClientCreationException, KeyManagementException, NoSuchAlgorithmException      
                    {            
                                                
                                      ////System.out.println("Inside Credit Card  Do authoization loop");
                                 // String nvpStr = "&apiUsername=" + apiUsername;
                                  String nvpStr = "&merchant=" + merchangtId;
                                 // nvpStr += "&apiPassword=" + apiPassword;
                                      nvpStr += "&order.id=" + Orderid;
                                      nvpStr += "&transaction.amount=" + requestAmountStr;
                                      nvpStr += "&transaction.id=" + transactionIdStr;
                                      nvpStr += "&transaction.currency=" + currencyCodestr;
                                      nvpStr += "&sourceOfFunds.type=" + sourceOfFundstype;
                                      nvpStr += "&sourceOfFunds.token="+tokenId;
                                      nvpStr += "&sourceOfFunds.provided.card.holder.firstName="+firstName;
                                      nvpStr += "&sourceOfFunds.provided.card.holder.lastName="+lastName;
                                      nvpStr += "&sourceOfFunds.provided.card.expiry.month="+expMnth;
                                      nvpStr += "&sourceOfFunds.provided.card.expiry.year="+expYr;
                                      nvpStr += "&billing.address.street="+AdressLine1;
                                      nvpStr += "&billing.address.city="+City;
                                      nvpStr += "&billing.address.stateProvince="+State;
                                      nvpStr += "&billing.address.country="+country;
                                      nvpStr += "&billing.address.postcodeZip="+ZipCode;
                                      
// Waiting for change
//nvpStr += "TBD"+KountToken;               
                                    Document doc = XMLUtil.createDocument("PaymentRecords");
                              		Element elePayRecrds = doc.getDocumentElement();
                              		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
                              				orderHeaderKey);
                              		elePayRecrds.setAttribute("Record", nvpStr);
                              		elePayRecrds.setAttribute("ChargeType", "Authorize Request");
                              		YIFApi api;
                              		api = YIFClientFactory.getInstance().getApi();
                              		api.executeFlow(env,"VSIPaymentRecords", doc);
                              		
                                  HashMap nvp = httpcall("AUTHORIZE", nvpStr,env);
                                  //System.out.println("after auth call"+nvp);            
                                  return nvp;
                                  
                                  }

                    
                    public static HashMap DoVoid(String Orderid,String merchantId,String transactionIdStr,String targetTransactionIdStr,YFSEnvironment env, String orderHeaderKey) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException, KeyManagementException, NoSuchAlgorithmException    
                          {      
                                                
                                      ////System.out.println("Inside Credit Card  Do void loop");
                                      //String nvpStr = "&apiUsername=" + apiUsername;
                                      String nvpStr = "&merchant=" + merchantId;
                                  //nvpStr += "&apiPassword=" + apiPassword;
                                  nvpStr += "&order.id=" + Orderid; 
                                      nvpStr += "&transaction.id=" + transactionIdStr;
                                      nvpStr += "&transaction.targetTransactionId=" + targetTransactionIdStr;
                                      
                                      Document doc = XMLUtil.createDocument("PaymentRecords");
                                		Element elePayRecrds = doc.getDocumentElement();
                                		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
                                				orderHeaderKey);
                                		elePayRecrds.setAttribute("Record", nvpStr);
                                		elePayRecrds.setAttribute("ChargeType", "Void Request");
                                		YIFApi api;
                                		api = YIFClientFactory.getInstance().getApi();
                                		api.executeFlow(env,"VSIPaymentRecords", doc);
                                      
                                  HashMap nvp = httpcall("VOID", nvpStr,env);
                              ////System.out.println("after Void call"+nvp);            
                                  return nvp;
                                  
                                  }
                    
                    
                    
                    public static HashMap DoToken(String creditCardNo,String expiryMonth,String expiryYear,String securityCode,
                                                String sourceType,YFSEnvironment env) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException, KeyManagementException, NoSuchAlgorithmException    
                          {      
                                                
                                      System.out.println("Inside Credit Card  Do Token loop");
                                  String nvpStr = "&sourceOfFunds.provided.card.expiry.month=" + expiryMonth; 
                                      nvpStr += "&sourceOfFunds.provided.card.expiry.year=" + expiryYear;
                                      nvpStr += "&sourceOfFunds.provided.card.number=" + creditCardNo;
                                      nvpStr += "&sourceOfFunds.provided.card.securityCode=" + securityCode;
                                      nvpStr += "&sourceOfFunds.type=" + sourceType;
                                  HashMap nvp = httpcall("TOKENIZE", nvpStr,env);
                              System.out.println("after Token call"+nvp);            
                                  return nvp;
                                  
                                  }
                          
                    
                  
        

                    
                public static HashMap httpcall( String methodName, String nvpStr,YFSEnvironment env) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException, KeyManagementException, NoSuchAlgorithmException
    {
                                ////System.out.println("Inside paypal  http calls loop");
                                ////System.out.println("Method name is "+methodName);
                                ////System.out.println("NVPString" +nvpStr);

                                

                                String gv_APIEndpoint=null;
								//OMS-1345:Start
                                //String gv_APIUserName = "merchant.TESTVSIDEMO01";
                                //String gv_APIPassword = "9ca3284b5b88784c56b97c6ab1487726";            
                    			//String gv_Merchant = "TESTVSIDEMO01";String gv_Version=null ;
								//OMS-1345:End
                                String gv_ProxyServer;
                                String gv_ProxyServerPort;
                                String gv_BNCode=null;
                                int gv_Proxy;
                                boolean gv_UseProxy;
                                boolean USE_PROXY = false;
                                String entCode="DEFAULT";
                                //Get TimeOut property from commonCode
								VSIUtils vsiUtils = new VSIUtils();
								Document docGetCommonCodeList = XMLUtil
										.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
								Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
//								eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
								eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,
										"AUTH_TIME_OUT_PROP");
								Document commonCodeOut = vsiUtils.getCommonCodeList(env,
										docGetCommonCodeList);
								Element eleCommonCodeOut = (Element) commonCodeOut.getDocumentElement()
										.getElementsByTagName(VSIConstants.ELE_COMMON_CODE).item(0);
								int intTimeOut = Integer.parseInt(eleCommonCodeOut
										.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION));
								System.out.println("HTTPCALL_TIMEOUT:::"+intTimeOut);
								//OMS-1345:Start
								String strPaymentUrl=null;
								String gv_APIUserName=null;
								String gv_APIPassword=null;
								String gv_Merchant=null;
								String strUrl=getCommonCodeLongDescriptionByCodeValue(env,entCode,VSIConstants.CODE_VALUE_CCURL);
								if(VSIConstants.VALUE_TNS.equals(strUrl))
								{	 
										 strPaymentUrl = getCommonCodeLongDescriptionByCodeValue(env,entCode,VSIConstants.VALUE_TNS);  
										 gv_APIUserName = getCommonCodeLongDescriptionByCodeValue(env,entCode,VSIConstants.CODE_VALUE_TNS_USER_ID);
			                             gv_APIPassword = getCommonCodeLongDescriptionByCodeValue(env,entCode,VSIConstants.CODE_VALUE_TNS_PASSWORD);
			                             nvpStr+="&apiUsername=" + gv_APIUserName;
			                             nvpStr+= "&apiPassword=" + gv_APIPassword;			                             
								}
								else
								{ 
										 strPaymentUrl = getCommonCodeLongDescriptionByCodeValue(env,entCode,VSIConstants.VALUE_DP);
										 
								}	 
									 gv_APIEndpoint=strPaymentUrl ; 
	                                    System.out.println("URL is  :"+strPaymentUrl);
	                            //OMS-1345: END
                                          String HTTPREQUEST_PROXYSETTING_SERVER = "";               
                                          String HTTPREQUEST_PROXYSETTING_PORT = "";   
                                          gv_ProxyServer      = HTTPREQUEST_PROXYSETTING_SERVER;
                                          gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;           
                                          gv_Proxy   = 2;
                                          String agent = "Mozilla/4.0";
                  String respText = "";
                  HashMap nvp=null;
             
            //String encodedData =   "&apiUsername=" + gv_APIUserName + "&merchant="+ gv_Merchant +"&apiPassword=" + gv_APIPassword+  "&apiOperation=" + methodName+ nvpStr;
            String encodedData =   nvpStr + "&apiOperation=" + methodName ;

                  System.out.println("encodedData is :"+encodedData);
                  
             try 
             {
                 System.out.println("url is :" + gv_APIEndpoint);
				byPassCerts1();
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
							System.out.println("respText:"+respText);
                             nvp = deformatNVP( respText );
                     }
                     return nvp;
             }
             catch( IOException e )
             {
                     return null;
             }
    



}
                
                
                
                public static Document getCommonCodeListInputForCodeValue(String sOrgCode,
                                                String codeValue) throws ParserConfigurationException {
                                Document docOutput = XMLUtil.createDocument("CommonCode");
                                Element eleRootItem = docOutput.getDocumentElement() ;
                                eleRootItem.setAttribute("OrganizationCode", sOrgCode);
                                eleRootItem.setAttribute("CodeValue", codeValue);
                                return docOutput;
                }
                 /**
            	 * This method is invoked to avoid the SSL Certification check on server
            	 * with TNS tokenization service.
            	 * 
            	 * @throws NoSuchAlgorithmException
            	 * @throws KeyManagementException
            	 */
            	public static void byPassCerts1() throws NoSuchAlgorithmException, KeyManagementException{
            		// Create a trust manager that does not validate certificate chains
            				TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            							return null;
            						}
            						public void checkClientTrusted(X509Certificate[] certs, String authType) {
            						}
            						public void checkServerTrusted(X509Certificate[] certs, String authType) {
            						}
            					}
            				};

            				// Install the all-trusting trust manager
            				SSLContext sc = SSLContext.getInstance("SSL");
            				sc.init(null, trustAllCerts, new java.security.SecureRandom());
            				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            				// Create all-trusting host name verifier
            				HostnameVerifier allHostsValid = new HostnameVerifier(){
            					public boolean verify(String hostname, SSLSession session) {
            						return true;
            					}
            				};

            				// Install the all-trusting host verifier
            				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            	}
                
                public static String getCommonCodeLongDescriptionByCodeValue(YFSEnvironment env,
                                                String sOrgCode, String sCodeName)
                                                throws ParserConfigurationException {
                                String sCommonCodeValue = "";
                                Document docgetCCListInput = getCommonCodeListInputForCodeValue(
                                                                sOrgCode, sCodeName);
                                Document docCCList = getCommonCodeList(env,
                                                                docgetCCListInput);
                                Element eleComCode = null;
                                if (docCCList != null) {
                                                eleComCode = (Element) docCCList.getElementsByTagName("CommonCode").item(0);
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
     
                static {
           		 javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
           		new javax.net.ssl.HostnameVerifier(){

           		public boolean verify(String hostname,
           		      javax.net.ssl.SSLSession sslSession) {
           		  //OMS-868 :Start
          	      final String datePowerHost= YFSSystem.getProperty("APP_VSI_DATAPOWER_HOST"); 
           		  if (hostname.equals(datePowerHost)) {
           		      return true;
           		 //OMS-868 :End
           		  }
           		  return false;
           		}
           		});
           		}


}
