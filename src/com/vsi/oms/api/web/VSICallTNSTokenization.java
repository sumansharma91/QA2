package com.vsi.oms.api.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.StringTokenizer;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCConfigurator;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSICallTNSTokenization {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICallTNSTokenization.class);

	/**
	 * This method is invoked from UI after sesison generation to fetch Token
	 * from TNS.
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXml
	 *            Input to this method is <GetToken SessionId="" Type="" />
	 * @return Output returned will be <Token TokenId=""/>
	 */
	public Document invokeTNSTokenization(YFSEnvironment env, Document inXml) {

		if(log.isDebugEnabled()){
			log.debug("BEGIN - invokeTNSTokenization method");
		}
		
		//Temp code to avoid SSL
		String allowSSL = YFCConfigurator.getInstance().getProperty(
				VSIConstants.TNS_AVOID_SSL);
		
		String merchantId = YFCConfigurator.getInstance().getProperty(
				VSIConstants.TNS_MERCHANT_ID);
		String methodName = YFCConfigurator.getInstance().getProperty(
				VSIConstants.TNS_METHOD_NAME);
		Document tokenDoc = null;
		HttpURLConnection httpURLConnection = null;

		tokenDoc = SCXmlUtil.createDocument();
		Element eleToken = tokenDoc.createElement("Token");
		Element eleGetTokenInput = inXml.getDocumentElement();
		String sessionId = eleGetTokenInput.getAttribute("SessionId");
		String sourceType = eleGetTokenInput.getAttribute("Type");
		//OMS-1345:Start
		try 
		{
		
		String strURL=null;
		String gv_APIUserName=null;
		String gv_APIPassword=null;
		String strPaymentUrl=getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ATTR_DEFAULT,VSIConstants.CODE_VALUE_CCURL);
		if(VSIConstants.VALUE_TNS.equals(strPaymentUrl))
		{	 
			strURL = getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ATTR_DEFAULT,VSIConstants.VALUE_TNS);  
				 gv_APIUserName = getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ATTR_DEFAULT,VSIConstants.CODE_VALUE_TNS_USER_ID);
                 gv_APIPassword = getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ATTR_DEFAULT,VSIConstants.CODE_VALUE_TNS_PASSWORD);
                 //nvpStr+="&apiUsername=" + gv_APIUserName;
                 //nvpStr+= "&apiPassword=" + gv_APIPassword;			                             
		}
		else
		{ 
			strURL = getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ATTR_DEFAULT,VSIConstants.VALUE_DP);
				 
		}	 
			 
         
        //OMS-1345: END
		//String strURL = YFCConfigurator.getInstance().getProperty("TNS_END_POINT_URL");
		if(log.isDebugEnabled()){
			log.debug("URL: "+strURL);
			log.debug("URL: "+strURL+", sessionId: "+sessionId+"type: "+sourceType);
		}
		
		
			VSIUtils vsiUtils = new VSIUtils();
			Document docGetCommonCodeList;
			int intTimeOut = 0;
			try {
				docGetCommonCodeList = XMLUtil
						.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
//			eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI.com");
			eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,
					"TOKEN_TIME_OUT_PROP");
			Document commonCodeOut = vsiUtils.getCommonCodeList(env,
					docGetCommonCodeList);
			Element eleCommonCodeOut = (Element) commonCodeOut.getDocumentElement()
					.getElementsByTagName(VSIConstants.ELE_COMMON_CODE).item(0);
			intTimeOut = Integer.parseInt(eleCommonCodeOut
					.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION));
			//System.out.println("HTTPCALL_TIMEOUT:::"+intTimeOut);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				log.error(e);
			}

			URL url = new URL(strURL);
			String request = "&merchant="+merchantId+"&apiOperation="+methodName+"&session.id="+sessionId+"&sourceOfFunds.type="+sourceType;
			if(VSIConstants.VALUE_TNS.equals(strPaymentUrl))
			{
            request+="&apiUsername=" + gv_APIUserName;
            request+= "&apiPassword=" + gv_APIPassword;	
			}
			if(log.isDebugEnabled()){
				log.debug("RequestString: "+request);
			}
			
			//START - Avoid SSL
			/*This is a temporary code to avaid SSL in Dev box. 
			 * This needs to be "DELETED" once the SSL issue is resolved.*/
			
			if("Y".equals(allowSSL)){
				byPassCerts();
			}
			
			//END - Avoid SSL
			
			//Set Request parameters
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0");
			httpURLConnection.setRequestMethod("POST");		
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			if(!YFCObject.isNull(intTimeOut) && intTimeOut != 0){
				httpURLConnection.setConnectTimeout(intTimeOut);
				httpURLConnection.setReadTimeout(intTimeOut);
			}
			
			if(log.isDebugEnabled()){
				log.debug(("Header:"+httpURLConnection.getRequestProperties() ));
			}
			
			// Send Request
			DataOutputStream wr = new DataOutputStream(
					httpURLConnection.getOutputStream());
			wr.writeBytes(request);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = httpURLConnection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuffer response = new StringBuffer();

			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			if(log.isDebugEnabled()){
				log.debug("response.toString" + response.toString());
			}

			HashMap nvpHash = new HashMap();
	        StringTokenizer stTok = new StringTokenizer(response.toString(), "&");
			while (stTok.hasMoreTokens()) {
				StringTokenizer stInternalTokenizer = new StringTokenizer(
						stTok.nextToken(), "=");
				if (stInternalTokenizer.countTokens() == 2) {
					String key = URLDecoder.decode(stInternalTokenizer.nextToken(),"UTF-8" );
					String value = URLDecoder.decode(stInternalTokenizer.nextToken(),"UTF-8" );
					if(log.isDebugEnabled()){
						log.debug("Key: "+key+" ,value: "+value);
					}
					nvpHash.put(key, value);
				}
			}
			String token = nvpHash.get("token").toString();

			eleToken.setAttribute("TokenId", token);
			tokenDoc.appendChild(eleToken);
			if(log.isDebugEnabled()){
				log.debug(SCXmlUtil.getString(tokenDoc));
				log.debug(SCXmlUtil.getString(tokenDoc));
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			log.error(e);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
		} catch (KeyManagementException e) {
			e.printStackTrace();
			log.error(e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			log.error(e);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}
		return tokenDoc;
	}
	
	/**
	 * This method is invoked to avoid the SSL Certification check on server
	 * with TNS tokenization service.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void byPassCerts() throws NoSuchAlgorithmException, KeyManagementException{
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
            eleComCode = (Element) docCCList.getElementsByTagName(VSIConstants.ELE_COMMON_CODE).item(0);
}
if (eleComCode != null) {
            sCommonCodeValue = eleComCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
}
return getEmptyCheckedString(sCommonCodeValue);
}

public static Document getCommonCodeList(YFSEnvironment env,
            Document docApiInput) {
Document docApiOutput = null;
try {
            YIFApi api;
            api = YIFClientFactory.getInstance().getApi();
            docApiOutput = api.invoke(env, VSIConstants.API_COMMON_CODE_LIST, docApiInput);
                            
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
public static Document getCommonCodeListInputForCodeValue(String sOrgCode,
        String codeValue) throws ParserConfigurationException {
Document docOutput = XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
Element eleRootItem = docOutput.getDocumentElement() ;
eleRootItem.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, sOrgCode);
eleRootItem.setAttribute(VSIConstants.ATTR_CODE_VALUE, codeValue);
return docOutput;
}

}