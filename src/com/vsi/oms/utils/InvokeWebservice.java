package com.vsi.oms.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;



public class InvokeWebservice implements YIFCustomApi {

	private static final int CONNECTION_TIMEOUT=5000;
	private static final int READ_TIMEOUT=5000;
	private Properties _prop = null;
	
	private final static YFCLogCategory log = YFCLogCategory.instance(InvokeWebservice.class);

	public static Document invokeTaxWebService(int serviceIndex,Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		
		
		final String fdEsbUrl= YFSSystem.getProperty("VERTEX_O_WSDL_URL");  //vertex O
		final String falloutUrl=YFSSystem.getProperty("VERTEX_LITE_WSDL_URL");  //Vertex Lite test with this-->"http://google.com:8888/"; 
		
		String urlToCall=null;
		switch(serviceIndex){
		case 1: urlToCall=fdEsbUrl;
			break;
		case 2: urlToCall=falloutUrl;
			break;
		default:
			urlToCall=fdEsbUrl;
		}
		
		outDoc=invokeWebService(XMLUtil.getXMLString(inDoc), urlToCall);
		//System.out.println("URL from Customer override:" + urlToCall); 
		return outDoc;

	}
	
	/**
	 * This method calls the getCommonCodeList to get the WSDL URL.
	 * @param env
	 * @return URL
	 * @throws ParserConfigurationException
	 * @throws Exception
	 */

	public static Document invokeCreateCustomer(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
	Document outDoc=null;
	/*String HRurl="http://10.3.53.66:5321/Nsb.CustomerRelationshipManagement.WebService.asmx"; 
	String aURL="http://NsbGroup.com/webservices/SaveNewCustomer";*/
	String HRurl = YFSSystem.getProperty("CRM_WEBSERVICE_URL");
	String aURL = YFSSystem.getProperty("CRM_SAVE_NEW_CUSTOMER_URL");
	outDoc=invokeCreateCustomerWeb(XMLUtil.getXMLString(inDoc), HRurl, aURL);
	return outDoc;
	}
	
	public static Document invokeFindCustomers(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		/*String HRurl="http://10.3.53.66:5321/Nsb.CustomerRelationshipManagement.WebService.asmx"; 
		String aURL="http://NsbGroup.com/webservices/FindCustomers";*/
		String HRurl = YFSSystem.getProperty("CRM_WEBSERVICE_URL");
		String aURL = YFSSystem.getProperty("CRM_FIND_CUSTOMERS_URL");
		outDoc=invokeFindCustomersWeb(XMLUtil.getXMLString(inDoc), HRurl, aURL);
		return outDoc;
		}
	
	
	public static Document invokeCRMWebService(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		String crmUrl = YFSSystem.getProperty("CRM_WSDL_URL");
		//System.out.println("URL from Customer override:" + crmUrl); 
		outDoc=invokeWebService(XMLUtil.getXMLString(inDoc), crmUrl);
		return outDoc;
	}
	
	public static Document invokeHRService(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		
		String HRurl="http://10.3.51.132:80/SingleViewOfCustomerProxyWeb/sca/RewardsService";
		outDoc=invokeWebService(XMLUtil.getXMLString(inDoc), HRurl);
		return outDoc;
	}
	
	public static Document invokeURL(String endPointURL, String xml) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		
		//System.out.println("Input XML to invokeURL is: \n"+xml);
		//System.out.println("InputURL passed is: " + endPointURL );
		URL url = new URL (endPointURL+xml);
		//System.out.println("url is: "+url.toString());
		
		HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestMethod( "GET" );
		
		BufferedReader reader = null;
		Document responseDoc=null;
		if(connection.getResponseCode() == 200)
		{
			responseDoc=parseDoc( connection.getInputStream());
		}
		else
		{
		reader = new BufferedReader(new
		InputStreamReader(connection.getErrorStream()));
		String line = "";
	    while ((line = reader.readLine()) != null) {
	    	if(log.isDebugEnabled()){
	    		log.debug(line);
	    	}
	    }
		}
		
 		if(responseDoc==null)
 			return responseDoc;
		//System.out.print("Response doc from GcoXmlUtil is:" + responseDoc );
		String xmlString = XMLUtil.getXMLString(responseDoc);
		//System.out.println("Neha:"+xmlString);
		//debugLog("************Web Service Response Document is:*********************\n"+xmlString);

		return responseDoc;
	}
	
	public static Document invokeCustomerSearchWebService(String inputString, String endPointURL, String actionURL) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		
		//START - Fix for SUH-4
		URL url = new URL (endPointURL);
		
		if(log.isDebugEnabled()){
			log.debug("InputURL passed is: " + endPointURL
					+ "\nActionURL passed is: " + actionURL
					+ "InputString passed is: " + inputString);
		}
		
		Document responseDoc = null;
		try {
			HttpURLConnection urlConn = (HttpURLConnection)(url.openConnection());
			
			urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
			urlConn.setRequestProperty("Content-Type","application/soap+xml;charset=UTF-8");
			urlConn.setRequestProperty("SOAPAction",actionURL);
			urlConn.setRequestMethod( "POST" );
			urlConn.setDoOutput(true);
			urlConn.setDoInput(true);
			urlConn.setUseCaches(false);
			urlConn.setAllowUserInteraction(false);
			urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
			urlConn.setReadTimeout(READ_TIMEOUT);
			DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
			out.writeBytes(inputString);
			out.flush();
			out.close();
	
			responseDoc=parseDoc(urlConn.getInputStream());
			
			if(responseDoc != null){
				if(log.isDebugEnabled()){
					log.debug("Output from CRM web service call: " + XMLUtil.getXMLString(responseDoc));
				}
				if (YFCDocument.getDocumentFor(responseDoc).getDocumentElement().getNodeName().equals("s:Envelope")) {
					YFCElement results=YFCDocument.getDocumentFor(responseDoc).getDocumentElement().getChildElement("s:Body");
					YFCElement response=results.getFirstChildElement();
					if (response.getNodeName().equals("s:Fault")) {
						// If there is a soap fault (customer not found), return <Customer IsCustomerAvailable="N" />
						if(log.isDebugEnabled()){
							log.debug("Soap Fault occurred");
						}
						responseDoc = SCXmlUtil.createFromString(VSIConstants.CUSTOMER_UNAVAILABLE);
					}
				} else {
					// Return <Customer IsCustomerAvailable="N" />
					responseDoc = SCXmlUtil.createFromString(VSIConstants.CUSTOMER_UNAVAILABLE);
				}
			}
		} catch (Exception e) {
			// Return <Customer IsCustomerAvailable="N" />
			log.error("Exception occurred", e);
			responseDoc = SCXmlUtil.createFromString(VSIConstants.CUSTOMER_UNAVAILABLE);
		}

		if(log.isDebugEnabled()){
			log.debug("Output xml is: " + XMLUtil.getXMLString(responseDoc));
		}
		return responseDoc;
		//END - Fix for SUH-4
	}
	
	public static Document invokeWebService(String inputString, String endPointURL) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		// System.out.println("Input passed to Web Service is: \n"+inputString);
		URL url = new URL (endPointURL);
		//System.out.println("Input passed to Web Service is after : \n"+inputString);
		//System.out.println("InputURL passed is: " + endPointURL );

		// Send XML Request
		HttpURLConnection urlConn = (HttpURLConnection)(url.openConnection());
		//System.out.println("Input passed to Web Service is after 1: \n");
		urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
		urlConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		urlConn.setRequestProperty("SOAPAction",endPointURL);
		urlConn.setRequestMethod( "POST" );
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		urlConn.setAllowUserInteraction(false);
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		urlConn.setReadTimeout(READ_TIMEOUT);
		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(inputString);
		out.flush();
		out.close();

		// Store Response
		Document responseDoc=parseDoc(urlConn.getInputStream());

		String xmlString = XMLUtil.getXMLString(responseDoc);
		// System.out.println("Trace for getRewardPointsResponse:"+xmlString); 
		//debugLog("************Web Service Response Document is:*********************\n"+xmlString);

		return responseDoc;
	}
	
	public static Document invokeCreateCustomerWeb(String inputString, String endPointURL, String actionURL) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		//System.out.println("Input passed to Web Service is: \n"+inputString);
		URL url = new URL (endPointURL);
		//System.out.println("Input passed to Web Service is after : \n"+inputString);
		//System.out.println("InputURL passed is: " + endPointURL );

		// Send XML Request
		HttpURLConnection urlConn = (HttpURLConnection)(url.openConnection());
		//System.out.println("Input passed to Web Service is after 1: \n");
		urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
		urlConn.setRequestProperty("Content-Type","application/soap+xml;charset=UTF-8");
		urlConn.setRequestProperty("SOAPAction",actionURL);
		urlConn.setRequestProperty("Connection","Keep-Alive");
		urlConn.setRequestProperty("Host","w2k8-vsi-crm1-t:5321");
	
		urlConn.setRequestMethod( "POST" );
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		urlConn.setAllowUserInteraction(false);
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		urlConn.setReadTimeout(READ_TIMEOUT);
		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(inputString);
		out.flush();
		out.close();

		// Store Response
		Document responseDoc=parseDoc(urlConn.getInputStream());

		String xmlString = XMLUtil.getXMLString(responseDoc);
		//System.out.println("Neha:"+xmlString);
		debugLog("************Web Service Response Document is:*********************\n"+xmlString);

		return responseDoc;
	}
	
	
	public static Document invokeFindCustomersWeb(String inputString, String endPointURL, String actionURL) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		//System.out.println("Input passed to Web Service is: \n"+inputString);
		URL url = new URL (endPointURL);
		//System.out.println("Input passed to Web Service is after : \n"+inputString);
		//System.out.println("InputURL passed is: " + endPointURL );

		// Send XML Request
		HttpURLConnection urlConn = (HttpURLConnection)(url.openConnection());
		//System.out.println("Input passed to Web Service is after 1: \n");
		urlConn.setRequestProperty("Content-length",String.valueOf(inputString.length()));
		urlConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		urlConn.setRequestProperty("SOAPAction",actionURL);
		urlConn.setRequestProperty("Connection","Keep-Alive");
		urlConn.setRequestProperty("Host","w2k8-vsi-crm1-t:5321");
	
		urlConn.setRequestMethod( "POST" );
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		urlConn.setAllowUserInteraction(false);
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		urlConn.setReadTimeout(READ_TIMEOUT);
		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(inputString);
		out.flush();
		out.close();

		// Store Response
		Document responseDoc=parseDoc(urlConn.getInputStream());

		String xmlString = XMLUtil.getXMLString(responseDoc);
		//System.out.println("Neha:"+xmlString);
		debugLog("************Web Service Response Document is:*********************\n"+xmlString);

		return responseDoc;
	}
	
	
	 private static Document parseDoc(InputStream instream) throws ParserConfigurationException, SAXException, IOException{
		 	DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
		  factory.isIgnoringElementContentWhitespace();
		  DocumentBuilder builder = factory.newDocumentBuilder();
		  Document doc=null;
		  if(instream.markSupported()){
			  instream.mark(Integer.MAX_VALUE);
			  BufferedReader buff_read = new BufferedReader(new InputStreamReader(instream,"UTF-8"));
			    String  inputLine = null;

			    while((inputLine = buff_read.readLine())!= null){
			    	if(log.isDebugEnabled()){
			    		log.debug(inputLine);
			    	}
			    }  
			    instream.reset();
		  }
		  doc=builder.parse(instream);
		  return doc;
		  }
	public void setProperties(Properties properties) throws Exception {
		_prop=properties;
	}

	protected static void debugLog(String message) {
		
	}
	
	public static Document invokeModifyCustomer(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		/*String HRurl="http://10.3.53.66:5321/Nsb.CustomerRelationshipManagement.WebService.asmx"; 
		String aURL="http://NsbGroup.com/webservices/Save";*/
		String HRurl = YFSSystem.getProperty("CRM_WEBSERVICE_URL");
		String aURL = YFSSystem.getProperty("CRM_SAVE_URL");
		outDoc=invokeCreateCustomerWeb(XMLUtil.getXMLString(inDoc), HRurl, aURL);
		return outDoc;
	}
	public static Document invokeModifyCustomer(Document inDoc,boolean flag) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Document outDoc=null;
		/*String HRurl="http://10.3.53.66:5321/Nsb.CustomerRelationshipManagement.WebService.asmx"; 
		String aURL="http://NsbGroup.com/webservices/Save";*/
		String HRurl = YFSSystem.getProperty("CRM_WEBSERVICE_URL");
		String aURL = YFSSystem.getProperty("CRM_SAVE_URL");
		
		StringWriter stringWriter = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "html");
		transformer.transform(new DOMSource(inDoc),new StreamResult(stringWriter));
		String newString = stringWriter.toString().replace("<Remark></Remark>", "<Remark> </Remark>");

		outDoc=invokeCreateCustomerWeb(newString, HRurl, aURL);
		return outDoc;
	}
	public static Document invokeCalculateRewards(Document inDoc) throws SocketTimeoutException, ParserConfigurationException, SAXException, UnrecoverableKeyException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException {
		Document outDoc=null;
		/*String HRurl="http://10.3.53.66:5321/Nsb.CustomerRelationshipManagement.WebService.asmx"; 
		String aURL="http://NsbGroup.com/webservices/CalculateReward";*/
		String HRurl = YFSSystem.getProperty("CRM_WEBSERVICE_URL");
		String aURL = YFSSystem.getProperty("CRM_CALCULATE_REWARD_URL");
		outDoc=invokeCreateCustomerWeb(XMLUtil.getXMLString(inDoc), HRurl, aURL);
		return outDoc;
	}
	
	public static Document invokeQASWebService(String strInputXml,String soapAction) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException {
		
		final String endPointURL = YFSSystem.getProperty("QAS_WSDL_URL"); 
		final String authToken = YFSSystem.getProperty("QAS_AUTH_TOKEN"); 
		final String connectTimeout = YFSSystem.getProperty("QAS_CONNECT_TIMEOUT");
		final String readTimeout = YFSSystem.getProperty("QAS_READ_TIMEOUT");
				
		URL url = new URL(endPointURL);

		// Send XML Request
		HttpURLConnection urlConn = (HttpURLConnection) (url.openConnection());
		urlConn.setRequestProperty("Content-length",String.valueOf(strInputXml.length()));
		urlConn.setRequestProperty("Content-Type", "text/xml");
		urlConn.setRequestProperty("Auth-Token",authToken);
		urlConn.setRequestProperty("SOAPAction", soapAction);
		urlConn.setRequestMethod("POST");
		urlConn.setDoOutput(true);
		urlConn.setDoInput(true);
		urlConn.setUseCaches(false);
		urlConn.setConnectTimeout(Integer.parseInt(connectTimeout));
		urlConn.setReadTimeout(Integer.parseInt(readTimeout));
		urlConn.setAllowUserInteraction(false);
		DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
		out.writeBytes(strInputXml);
		out.flush();
		out.close();

		// Store Response
		Document responseDoc = parseDoc(urlConn.getInputStream());

		// String xmlString = XMLUtil.getXMLString(responseDoc);
		// System.out.println("Trace for getRewardPointsResponse:"+xmlString);
		// debugLog("************Web Service Response Document is:*********************\n"+xmlString);

		return responseDoc;
	}
	
	public static Document invokeATGWebservice(Document inXML) throws SocketTimeoutException, IOException, ParserConfigurationException, SAXException{
		final String atgPricingURL= YFSSystem.getProperty("VSI_PRICING_SERVICE_URL");

		
	Document outDoc=invokeWebService(XMLUtil.getXMLString(inXML), atgPricingURL);
	return outDoc;
	//END: Added ATG Webservice call 
}
}