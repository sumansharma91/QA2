package com.vsi.oms.utils;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class InvokeWebserviceUtil implements YIFCustomApi {


                private Properties _prop = null;
                
                
                
  public static Document invokeFDWebService(YFSEnvironment env,Document inDoc) throws ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
                                Document outDoc=null;
                                
                        		String UserName=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","UserName");
                        		String Password=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","Password");
                        		String URL=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","URLSVS");

                            	Node usertoken = inDoc.getElementsByTagName("wsse:UsernameToken").item(0);
                        		NodeList list = usertoken.getChildNodes();
                        		for (int i = 0; i < list.getLength(); i++) {
                        			 
                                    Node node = list.item(i);

                        	   // get the salary element, and update the value
                        	   if ("wsse:Username".equals(node.getNodeName())) {
                        		node.setTextContent(UserName);
                        	   }

                                    //remove firstname
                        	   if ("wsse:Password".equals(node.getNodeName())) {
                        		   node.setTextContent(Password);
                        	   }

                        	}
                                                
                                
                                                String fdEsbUrl="https://webservices-cert.storedvalue.com/svsxml/services/SVSXMLWay";
                                              //JIRA 608
                                                String strOrderHeaderKey = (String)env.getTxnObject("strOrderHeaderKey");
                                                String chargeType = (String)env.getTxnObject("chargeType");
                                                //System.out.println("OrderHeaderKey is :::"+strOrderHeaderKey);
                                                //System.out.println("ChargeType is :::"+chargeType);

                                                if(!YFCObject.isVoid(strOrderHeaderKey) && !YFCObject.isVoid(chargeType)) {
                                    				recordPayments(env, strOrderHeaderKey, VSIUtils.getDocumentXMLString(inDoc), chargeType);
                                    				//env.setTxnObject("strOrderHeaderKey", null);
                                    				//env.setTxnObject("chargeType", null);
                                                }
                          
                                                outDoc=invokeWebService(XMLUtil.getXMLString(inDoc), URL);
                                                
                                				      				
                                                return outDoc;
                                                
                }
  
  
  public static Document invokeFraudWebService(YFSEnvironment env,Document inDoc) throws ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
      Document outDoc=null;
      
		
		String URL=getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","URLFRAUDIIB");

  	
                     
		try {
                      outDoc=invokeFraudService(XMLUtil.getXMLString(inDoc), URL);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
      				      				
                      return outDoc;
                      
}
                
                public static Document invokeWebService(String inputString, String endPointURL) throws IOException, ParserConfigurationException, SAXException {
                                

                //            String xmlRequest=GcoXmlUtil.getXMLString(inDoc);

        //System.out.println("Input passed to Web Service is: \n"+inputString);
                                URL url = new URL (endPointURL);
                                //System.out.println("Input passed to Web Service is after : \n"+inputString);

                                // Send XML Request
        HttpsURLConnection urlConn = (HttpsURLConnection)(url.openConnection());
        
        //System.out.println("Input passed to Web Service is after 1: \n");
        
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestMethod("POST");
        urlConn.setRequestProperty("Accept-Language","en");
        urlConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        urlConn.setRequestProperty("SOAPAction",endPointURL);
        urlConn.setAllowUserInteraction(false);
        urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
        urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                urlConn.setConnectTimeout(500);

                                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                                
                                out.writeBytes(inputString);
                                out.flush();
                                out.close();

                                // Store Response
                                Document responseDoc=GcoXmlUtil.getDocument(urlConn.getInputStream());

        
        
                                
                                String xmlString = XMLUtil.getXMLString(responseDoc);
                                //System.out.println("Nish:"+xmlString);
                                debugLog("************Web Service Response Document is:*********************\n"+xmlString);

                                return responseDoc;
                                
                                
                }
                
                public static Document invokeFraudService(String inputString, String endPointURL) throws IOException, ParserConfigurationException, SAXException {
                    

                    //            String xmlRequest=GcoXmlUtil.getXMLString(inDoc);

            //System.out.println("Input passed to Web Service is: \n"+inputString);
                                    URL url = new URL (endPointURL);
                                    //System.out.println("Input passed to Web Service is after : \n"+inputString);

                                    // Send XML Request
            HttpURLConnection urlConn = (HttpURLConnection)(url.openConnection());
            
            //System.out.println("Input passed to Web Service is after 1: \n");
            
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Language","en");
            urlConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

            urlConn.setRequestProperty("SOAPAction",endPointURL);
            urlConn.setAllowUserInteraction(false);
            urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
            urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                                    urlConn.setConnectTimeout(500);

                                    DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                                    
                                    out.writeBytes(inputString);
                                    out.flush();
                                    out.close();

                                    // Store Response
                                    Document responseDoc=GcoXmlUtil.getDocument(urlConn.getInputStream());

            
            
                                    
                                    String xmlString = XMLUtil.getXMLString(responseDoc);
                                    //System.out.println("Nish:"+xmlString);
                                    debugLog("************Web Service Response Document is:*********************\n"+xmlString);

                                    return responseDoc;
                                    
                                    
                    }
                    

                public void setProperties(Properties properties) throws Exception {
                                _prop=properties;
                }
                
                protected static void debugLog(String message) {
                                //GcoTraceUtil.debugLog(message);
 
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

                /*
                 * JIRA 608
                 * This Method is used to capture all the payment request and response in VSI_Payment_Records Table 
                 */
                public static void recordPayments(YFSEnvironment env, String strOrderHeaderKey, String record, String chargeType) throws ParserConfigurationException {
            		 Document doc = XMLUtil.createDocument("PaymentRecords");
            	  		Element elePayRecrds = doc.getDocumentElement();
            	  		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
            	  				strOrderHeaderKey);
            	  		elePayRecrds.setAttribute("Record", record);
            	  		elePayRecrds.setAttribute("ChargeType", chargeType);
            	  		YIFApi api;
            	  		try {
            				api = YIFClientFactory.getInstance().getApi();
            		  		api.executeFlow(env,"VSIPaymentRecords", doc);
            			} catch (YIFClientCreationException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (YFSException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (RemoteException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
                }
}
