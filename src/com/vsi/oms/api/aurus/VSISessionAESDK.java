package com.vsi.oms.api.aurus;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aurus.aesdk.abstractfactory.formfactor.FormFactorHandler;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSISessionAESDK {


	private YFCLogCategory log = YFCLogCategory.instance(VSISessionAESDK.class);
	private static final String TAG = VSISessionAESDK.class.getSimpleName();
	YIFApi api;

	public Document sessionAESDKAPI(YFSEnvironment env, Document inXML) throws Exception{

		printLogs("Printing Input XML :" + SCXmlUtil.getString(inXML));
		
		SessionObject sessionObj= new SessionObject();

		if(YFSObject.isVoid(inXML)) {
			inXML=SCXmlUtil.createFromString("<temp/>");
		}else {
			Element eleOrder = inXML.getDocumentElement();
			String strCardNumber = eleOrder.getAttribute("CardNumber");
			String strCardExpiryDate = eleOrder.getAttribute("CardExpiryDate");
			String strCardIdentifier = eleOrder.getAttribute("CardIdentifier");
			String strCardType = eleOrder.getAttribute("CardType");

			if(YFCCommon.isVoid(strCardNumber)) {
				strCardNumber="";
			}
			if(YFCCommon.isVoid(strCardExpiryDate)) {
				strCardExpiryDate="";
			}
			if(YFCCommon.isVoid(strCardIdentifier)) {
				strCardIdentifier="";
			}
			if(YFCCommon.isVoid(strCardType)) {
				strCardType="";
			}

			api = YIFClientFactory.getInstance().getApi();

			sessionObj.setCardNumber(strCardNumber);
			sessionObj.setCardIdentifier(strCardIdentifier);
			sessionObj.setCardExpiryDate(strCardExpiryDate);
			sessionObj.setCardType(strCardType);

			//		VSISessionAESDK session= new VSISessionAESDK();
			//		session.initAESDKAPI();
			//		SessionObject sessionObj= new SessionObject();
			//		sessionObj.setCardNumber(strCardNumber);
			//		sessionObj.setCardExpiryDate(strCardExpiryDate);
			//		Document outDoc=session.sessionAPI(sessionObj);
		}
		
		
		ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");

		HashMap<String, String> aurusMandateParam= new HashMap<String, String>();

		if(!alCommonCodeList.isEmpty()){
			for(int i=0;i<alCommonCodeList.size() ;i++) {
				Element eleCommonCode=alCommonCodeList.get(i);
				String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				String strLongdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
				String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
					//							System.out.println(strCodeValue + " "+strShortdecValue);
					aurusMandateParam.put(strCodeValue, strShortdecValue);	
				}
			}
		}
		
		sessionObj.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
		sessionObj.setADSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
		sessionObj.setCorpID(aurusMandateParam.get("CorpID"));
		sessionObj.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
		sessionObj.setStoreId(aurusMandateParam.get("StoreId"));
		sessionObj.setTerminalId(aurusMandateParam.get("TerminalId"));
		sessionObj.setTemplateId(aurusMandateParam.get("TemplateId"));
		sessionObj.setDomainId(aurusMandateParam.get("DomainId"));
		sessionObj.setTokenType(aurusMandateParam.get("TokenType"));
		sessionObj.setCardTypeSupport(aurusMandateParam.get("CardTypeSupport"));
		sessionObj.setURLType(aurusMandateParam.get("URLType"));
		
		
		Document outDoc =getSession(sessionObj);

		return outDoc;
	}


	public static void main(String[] args) throws ParserConfigurationException {

//		VSISessionAESDK session= new VSISessionAESDK();
//		SessionObject sessionObj= new SessionObject();
//		Document outDoc =session.getSession(sessionObj);
//
//		System.out.println("outDoc ="+SCXmlUtil.getString(outDoc));

	}

	private Document getSession(SessionObject sessionObj) throws ParserConfigurationException {
		
		Document sessionResponse =sessionAPI(sessionObj);

		if(sessionResponse!=null) {

			Element sessionResponseEle = (Element)sessionResponse.getElementsByTagName("SessionResponse").item(0);

			String sessionResponseCode =getElementValue(sessionResponseEle,"ResponseCode");
			String sessionStoreId =getElementValue(sessionResponseEle,"StoreId");
			String sessionTerminalId =getElementValue(sessionResponseEle,"TerminalId");
			String sessionResponseText =getElementValue(sessionResponseEle,"ResponseText");
			String sessionSessionId =getElementValue(sessionResponseEle,"SessionId");
			String sessionIFrameUrl =getElementValue(sessionResponseEle,"IFrameUrl");
			String sessionSessionValidity =getElementValue(sessionResponseEle,"SessionValidity");

			System.out.println("responseCode "+sessionResponseCode);
			System.out.println("storeId "+sessionStoreId);
			System.out.println("terminalId "+sessionTerminalId);
			System.out.println("responseText "+sessionResponseText);
			System.out.println("sessionSessionId "+sessionSessionId);
			System.out.println("sessionIFrameUrl "+sessionIFrameUrl);
			System.out.println("sessionSessionValidity "+sessionSessionValidity);


			return sessionResponse;
		}
		return null;
	}





	//	private Document getSession() throws ParserConfigurationException {
	//
	//		Document initResponse =initAESDKAPI();
	//
	//		if(initResponse!=null) {
	//			Element initAesdkResponseEle = (Element)initResponse.getElementsByTagName("InitAesdkResponse").item(0);
	//			String strResponseCode = getElementValue(initAesdkResponseEle,"ResponseCode");
	//			String strStoreId = getElementValue(initAesdkResponseEle,"StoreId");
	//			String strTerminalId = getElementValue(initAesdkResponseEle,"TerminalId");
	//			String strResponseText = getElementValue(initAesdkResponseEle,"ResponseText");
	//
	//
	//			System.out.println("responseCode "+strResponseCode);
	//			System.out.println("storeId "+strStoreId);
	//			System.out.println("terminalId "+strTerminalId);
	//			System.out.println("responseText "+strResponseText);
	//
	//			if((strResponseCode!=null && strResponseCode.equalsIgnoreCase("00000")) && (strResponseText!=null && strResponseText.equalsIgnoreCase("SUCCESS"))) {
	//
	//				SessionObject sessionObj= new SessionObject();
	//				Document sessionResponse =sessionAPI(sessionObj);
	//
	//				if(sessionResponse!=null) {
	//
	//					Element sessionResponseEle = (Element)sessionResponse.getElementsByTagName("SessionResponse").item(0);
	//
	//					String sessionResponseCode =getElementValue(sessionResponseEle,"ResponseCode");
	//					String sessionStoreId =getElementValue(sessionResponseEle,"StoreId");
	//					String sessionTerminalId =getElementValue(sessionResponseEle,"TerminalId");
	//					String sessionResponseText =getElementValue(sessionResponseEle,"ResponseText");
	//					String sessionSessionId =getElementValue(sessionResponseEle,"SessionId");
	//					String sessionIFrameUrl =getElementValue(sessionResponseEle,"IFrameUrl");
	//					String sessionSessionValidity =getElementValue(sessionResponseEle,"SessionValidity");
	//
	//					System.out.println("responseCode "+sessionResponseCode);
	//					System.out.println("storeId "+sessionStoreId);
	//					System.out.println("terminalId "+sessionTerminalId);
	//					System.out.println("responseText "+sessionResponseText);
	//					System.out.println("sessionSessionId "+sessionSessionId);
	//					System.out.println("sessionIFrameUrl "+sessionIFrameUrl);
	//					System.out.println("sessionSessionValidity "+sessionSessionValidity);
	//
	//
	//					return sessionResponse;
	//				}
	//			}
	//		}
	//		return null;
	//	}

	private Document sessionAPI(SessionObject sessionObj) throws ParserConfigurationException {

		Document SessionRequestDoc = XMLUtil.createDocument("SessionRequest");
		Element SessionRequestEle = SessionRequestDoc.getDocumentElement();

		putElementValue(SessionRequestEle,"CorpID", sessionObj.getCorpID());
		putElementValue(SessionRequestEle,"ADSDKSpecVer", sessionObj.getADSDKSpecVer());
		putElementValue(SessionRequestEle,"MerchantIdentifier", sessionObj.getMerchantIdentifier());
		putElementValue(SessionRequestEle,"StoreId", sessionObj.getStoreId());
		putElementValue(SessionRequestEle,"TerminalId", sessionObj.getTerminalId());
		putElementValue(SessionRequestEle,"DomainId", sessionObj.getDomainId());
		putElementValue(SessionRequestEle,"TemplateId", sessionObj.getTemplateId());
		putElementValue(SessionRequestEle,"URLType", sessionObj.getURLType());
		putElementValue(SessionRequestEle,"CardType", sessionObj.getCardType());
		putElementValue(SessionRequestEle,"SubCardType", sessionObj.getSubCardType());
		putElementValue(SessionRequestEle,"CardNumber", sessionObj.getCardNumber());
		putElementValue(SessionRequestEle,"CardIdentifier", sessionObj.getCardIdentifier());
		putElementValue(SessionRequestEle,"TokenType", sessionObj.getTokenType());
		putElementValue(SessionRequestEle,"AlternatePaymentMatrix", sessionObj.getAlternatePaymentMatrix());
		putElementValue(SessionRequestEle,"CardExpiryDate", sessionObj.getCardExpiryDate());
		putElementValue(SessionRequestEle,"CardTypeSupport", sessionObj.getCardTypeSupport());
		putElementValue(SessionRequestEle,"CardTypeDisclaimer", sessionObj.getCardTypeDisclaimer());
		putElementValue(SessionRequestEle,"LanguageIndicator", sessionObj.getLanguageIndicator());


		Element ecommFingerPrintInfoEle = SCXmlUtil.createChild(SessionRequestEle, "ECOMMFingerPrintInfo");
		putElementValue(ecommFingerPrintInfoEle,"IPAddress", sessionObj.getIPAddress());
		putElementValue(ecommFingerPrintInfoEle,"DeviceOS", sessionObj.getDeviceOS());
		putElementValue(ecommFingerPrintInfoEle,"DeviceType", sessionObj.getDeviceType());
		putElementValue(ecommFingerPrintInfoEle,"BrowserDetails", sessionObj.getBrowserDetails());
		putElementValue(ecommFingerPrintInfoEle,"DeviceScreenResolution", sessionObj.getDeviceScreenResolution());
		putElementValue(ecommFingerPrintInfoEle,"ReferralURL", sessionObj.getReferralURL());
		putElementValue(ecommFingerPrintInfoEle,"CountryCode", sessionObj.getCountryCode());
		putElementValue(ecommFingerPrintInfoEle,"StateName", sessionObj.getStateName());
		putElementValue(ecommFingerPrintInfoEle,"City", sessionObj.getCity());
		putElementValue(ecommFingerPrintInfoEle,"BrowserLanguage", sessionObj.getBrowserLanguage());
		putElementValue(ecommFingerPrintInfoEle,"WebsiteSessionID", sessionObj.getWebsiteSessionID());
		putElementValue(ecommFingerPrintInfoEle,"NetworkType", sessionObj.getNetworkType());
		putElementValue(ecommFingerPrintInfoEle,"NetworkSubType", sessionObj.getNetworkSubType());
		putElementValue(ecommFingerPrintInfoEle,"NetworkDownloadSpeed", sessionObj.getNetworkDownloadSpeed());
		putElementValue(ecommFingerPrintInfoEle,"NetworkMaxDownloadSpeed", sessionObj.getNetworkMaxDownloadSpeed());


		
		printLogs("Printing Aurus Input XML :" + SCXmlUtil.getString(SessionRequestDoc));

		String sesionAPIXML = SCXmlUtil.getString(SessionRequestDoc);

		Document sessionResponseDoc=null;
		try {
			FormFactorHandler formFactor = new FormFactorHandler(); 
			String xmlResponse = (String) formFactor.getSessionId(sesionAPIXML);
			String response=xmlResponse;
			sessionResponseDoc=SCXmlUtil.createFromString(response);
		}catch (Exception e) {
			Document SessionResponseErrorDoc = XMLUtil.createDocument("SessionResponseError");
			Element SessionResponseErrorEle = SessionResponseErrorDoc.getDocumentElement();
			putElementValue(SessionResponseErrorEle,"Error", "AESDK Not initialized");
			
			sessionResponseDoc=SessionResponseErrorDoc;
		}

		

//		Document sessionResponseDoc=SCXmlUtil.createFromString(response);
		
		printLogs("Printing Aurus Output XML :" + SCXmlUtil.getString(sessionResponseDoc));

		return sessionResponseDoc;
	}




	private Document initAESDKAPI() throws ParserConfigurationException {
		Document initAESDKDoc =createInputInitSDK();

		String initAESDKXML= SCXmlUtil.getString(initAESDKDoc);
		System.out.println("initAESDKXML \n"+initAESDKXML);

		FormFactorHandler formFactor = new FormFactorHandler(); 
		String response = formFactor.initAESDK(initAESDKXML);

		Document initResponseDoc=SCXmlUtil.createFromString(response);
		return initResponseDoc; 
	}


	private Document createInputInitSDK() throws ParserConfigurationException {
		Document initAeSDKReqDoc = XMLUtil.createDocument("InitAesdkRequest");
		Element elechangeOrderDoc = initAeSDKReqDoc.getDocumentElement();

		putElementValue(elechangeOrderDoc,"POSID","VSIDEV20");
		//		putElementValue(elechangeOrderDoc,"ConfigFilePath","C:/aesdkprop");
		putElementValue(elechangeOrderDoc,"ConfigFilePath","/Sterling/opt/aesdkprop/");

		return initAeSDKReqDoc;
	}

	private static String getJsonValue(JSONObject jsonObj,String key) {
		String value="";
		if(jsonObj!=null) {
			if (jsonObj.has(key)) {
				value =jsonObj.getString(key);
			}
		}		
		return value;
	}


	private void putElementValue(Element childEle, String key, Object value) {
		Element ele = SCXmlUtil.createChild(childEle, key);
		if(value instanceof String ) {
			ele.setTextContent((String)value);
		}else if(value instanceof Element ) {
			ele.appendChild((Element)value);
		}
	}


	private String getElementValue(Element element, String tagName) {
		String value="";
		if(element!=null) {
			Element ele = (Element) element.getElementsByTagName(tagName).item(0);
			if(ele!=null) {
				value=ele.getTextContent();
			}
		}
		return value;
	}

	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
	
	class SessionObject{

		private String corpID="17304";
		private String aDSDKSpecVer="6.13.1";
		private String merchantIdentifier="100000068987";
		private String storeId="210001";
		private String terminalId="12651743";
		private String domainId="2";
		private String templateId="1";  //4
		private String uRLType="1";
		private String cardType;
		private String subCardType;
		private String cardNumber;
		private String cardIdentifier;
		private String tokenType="102";
		private String alternatePaymentMatrix;
		private String cardExpiryDate;
		private String cardTypeSupport="1111111111000000";
		private String cardTypeDisclaimer;
		private String languageIndicator="00";	




		private String iPAddress="";
		private String deviceOS="";
		private String deviceType="";
		private String browserDetails="";
		private String deviceScreenResolution="";
		private String referralURL="";
		private String countryCode="";
		private String stateName="";
		private String city="";
		private String browserLanguage="";
		private String websiteSessionID="";
		private String networkType="";
		private String networkSubType="";
		private String networkDownloadSpeed="";
		private String networkMaxDownloadSpeed="";


		public String getCorpID() {
			return corpID;
		}
		public void setCorpID(String corpID) {
			this.corpID = corpID;
		}
		public String getADSDKSpecVer() {
			return aDSDKSpecVer;
		}
		public void setADSDKSpecVer(String aDSDKSpecVer) {
			this.aDSDKSpecVer = aDSDKSpecVer;
		}
		public String getMerchantIdentifier() {
			return merchantIdentifier;
		}
		public void setMerchantIdentifier(String merchantIdentifier) {
			this.merchantIdentifier = merchantIdentifier;
		}
		public String getStoreId() {
			return storeId;
		}
		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}
		public String getTerminalId() {
			return terminalId;
		}
		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}


		public String getDomainId() {
			return domainId;
		}
		public void setDomainId(String domainId) {
			this.domainId = domainId;
		}
		public String getTemplateId() {
			return templateId;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public String getURLType() {
			return uRLType;
		}
		public void setURLType(String uRLType) {
			this.uRLType = uRLType;
		}
		public String getCardType() {
			return cardType;
		}
		public void setCardType(String cardType) {
			this.cardType = cardType;
		}
		public String getSubCardType() {
			return subCardType;
		}
		public void setSubCardType(String subCardType) {
			this.subCardType = subCardType;
		}
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public String getCardIdentifier() {
			return cardIdentifier;
		}
		public void setCardIdentifier(String cardIdentifier) {
			this.cardIdentifier = cardIdentifier;
		}
		public String getTokenType() {
			return tokenType;
		}
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
		public String getAlternatePaymentMatrix() {
			return alternatePaymentMatrix;
		}
		public void setAlternatePaymentMatrix(String alternatePaymentMatrix) {
			this.alternatePaymentMatrix = alternatePaymentMatrix;
		}
		public String getCardExpiryDate() {
			return cardExpiryDate;
		}
		public void setCardExpiryDate(String cardExpiryDate) {
			this.cardExpiryDate = cardExpiryDate;
		}
		public String getCardTypeSupport() {
			return cardTypeSupport;
		}
		public void setCardTypeSupport(String cardTypeSupport) {
			this.cardTypeSupport = cardTypeSupport;
		}
		public String getCardTypeDisclaimer() {
			return cardTypeDisclaimer;
		}
		public void setCardTypeDisclaimer(String cardTypeDisclaimer) {
			this.cardTypeDisclaimer = cardTypeDisclaimer;
		}
		public String getLanguageIndicator() {
			return languageIndicator;
		}
		public void setLanguageIndicator(String languageIndicator) {
			this.languageIndicator = languageIndicator;
		}
		public String getIPAddress() {
			return iPAddress;
		}
		public void setIPAddress(String iPAddress) {
			this.iPAddress = iPAddress;
		}
		public String getDeviceOS() {
			return deviceOS;
		}
		public void setDeviceOS(String deviceOS) {
			this.deviceOS = deviceOS;
		}
		public String getDeviceType() {
			return deviceType;
		}
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		public String getBrowserDetails() {
			return browserDetails;
		}
		public void setBrowserDetails(String browserDetails) {
			this.browserDetails = browserDetails;
		}
		public String getDeviceScreenResolution() {
			return deviceScreenResolution;
		}
		public void setDeviceScreenResolution(String deviceScreenResolution) {
			this.deviceScreenResolution = deviceScreenResolution;
		}
		public String getReferralURL() {
			return referralURL;
		}
		public void setReferralURL(String referralURL) {
			this.referralURL = referralURL;
		}
		public String getCountryCode() {
			return countryCode;
		}
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		public String getStateName() {
			return stateName;
		}
		public void setStateName(String stateName) {
			this.stateName = stateName;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getBrowserLanguage() {
			return browserLanguage;
		}
		public void setBrowserLanguage(String browserLanguage) {
			this.browserLanguage = browserLanguage;
		}
		public String getWebsiteSessionID() {
			return websiteSessionID;
		}
		public void setWebsiteSessionID(String websiteSessionID) {
			this.websiteSessionID = websiteSessionID;
		}
		public String getNetworkType() {
			return networkType;
		}
		public void setNetworkType(String networkType) {
			this.networkType = networkType;
		}
		public String getNetworkSubType() {
			return networkSubType;
		}
		public void setNetworkSubType(String networkSubType) {
			this.networkSubType = networkSubType;
		}
		public String getNetworkDownloadSpeed() {
			return networkDownloadSpeed;
		}
		public void setNetworkDownloadSpeed(String networkDownloadSpeed) {
			this.networkDownloadSpeed = networkDownloadSpeed;
		}
		public String getNetworkMaxDownloadSpeed() {
			return networkMaxDownloadSpeed;
		}
		public void setNetworkMaxDownloadSpeed(String networkMaxDownloadSpeed) {
			this.networkMaxDownloadSpeed = networkMaxDownloadSpeed;
		}







	}
}
