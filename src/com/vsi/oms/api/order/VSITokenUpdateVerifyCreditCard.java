package com.vsi.oms.api.order;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient, Inc.
 * 
 * This class is used for updating the expiry date of the CC using the token generated earlier through SSDCS.
 * We need this because the token generated in SSDCS is through a dummy expiry date (01/99)
 * After Save Card(updating date) we a;s
 * inXML: <PaymentMethods>
<PaymentMethod OrderNo="Y123456789" Token="4012009915480026" expiryMonth="" expiryYear="" OrganizationCode="VSI.com" />
<PaymentMethod OrderNo="Y123456789" Token="4012009915480027" expiryMonth="" expiryYear="" OrganizationCode="VSI.com" />
</PaymentMethods>

 * 
 * OrderNo is used as Transaction ID since it is unique
 *
 */
public class VSITokenUpdateVerifyCreditCard {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSITokenUpdateVerifyCreditCard.class);
	YIFApi api;
	
	public Document vsiTokenUpdate(YFSEnvironment env, Document inXML)
			throws Exception {
		Document returnDoc = null;
		
		if(log.isDebugEnabled()){
			log.info("================Inside VSITokenUpdateVerifyCreditCard================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		try{
	
			 returnDoc = XMLUtil.createDocument("PaymentMethods");
				
			 Element rootElement = returnDoc.getDocumentElement();
			 
				
			NodeList PaymentMethodList = inXML.getElementsByTagName("PaymentMethod");
			int length = PaymentMethodList.getLength();
			
			for(int i=0;i<length;i++){
				Element rootEle = (Element) PaymentMethodList.item(i);
				
				Element PaymentMethodElement = returnDoc.createElement("PaymentMethod");	
		
		String entCode = rootEle.getAttribute("OrganizationCode");
		String TNS_URL = getCommonCodeLongDescriptionByCodeValue(env,entCode,"DP");
		
			//String card = rootEle.getAttribute("card");
			String Orderid= rootEle.getAttribute("OrderNo");
			String transactionIdStr= Orderid+i;
	
			String tokenId=	rootEle.getAttribute("Token");
			String sourceType="CARD";
			
			String expiryMonth= rootEle.getAttribute("expiryMonth");
			String expiryYear=rootEle.getAttribute("expiryYear");
			
			
			String tokenIdStr=TNStokenization(env,TNS_URL,tokenId,sourceType,expiryYear,expiryMonth);
			String verifyStr =TNSVerification(env,TNS_URL,Orderid,sourceType,transactionIdStr,tokenId,expiryYear,expiryMonth);
			
			if(tokenIdStr.equalsIgnoreCase("FAIL")){
				PaymentMethodElement.setAttribute("IsValid", "N");
				PaymentMethodElement.setAttribute("IsCallSuccesful", "Y");
				rootElement.appendChild(PaymentMethodElement);
	
				
			}else{
					
			
			//rootElement.setAttribute("Verify", verifyStr);
			
			if(verifyStr.equalsIgnoreCase("APPROVED")){
				
				PaymentMethodElement.setAttribute("IsValid", "Y");
				PaymentMethodElement.setAttribute("IsCallSuccesful", "Y");
				PaymentMethodElement.setAttribute("TokenReturned", tokenIdStr);
				rootElement.appendChild(PaymentMethodElement);
			
				////System.out.println("XML OUTPUT"+XmlUtils.getString(returnDoc));
				
			}else{
				PaymentMethodElement.setAttribute("IsValid", "N");
				PaymentMethodElement.setAttribute("IsCallSuccesful", "Y");
				rootElement.appendChild(PaymentMethodElement);
			}
		
			}
			
	
			}
		}catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException(
					e.getErrorCode(),
					e.getErrorDescription(),
					"YFSException");
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");

		}
		return returnDoc;

	}

	private String TNSVerification(YFSEnvironment env,  String TNS_URL, String orderid,
			String sourceType, String transactionIdStr, String tokenId, String expiryYear, String expiryMonth) {
		
		HashMap nvp = DoVerify(env,TNS_URL,tokenId,sourceType,transactionIdStr,orderid); 
		String strAck = nvp.get("RESULT").toString();
		String verifyStr=null;
		
		 if ("Success".equalsIgnoreCase(strAck))
		    {
			
				////System.out.println("Inside Success");
				verifyStr = nvp.get("RESPONSE.GATEWAYCODE").toString();
			 			  
			
				
		    }else if("ERROR".equalsIgnoreCase(strAck)){
		    	
		    	verifyStr=nvp.get("ERROR.EXPLANATION").toString();
		    	
		    	
		    }
		    else{
		    	verifyStr="FAIL";
		    }
		 return verifyStr;

		
		
		
	}

	private String TNStokenization(YFSEnvironment env,  String TNS_URL, String tokenId,
			String sourceType, String expiryYear, String expiryMonth) throws Exception {
		HashMap nvp = DoToken(env,TNS_URL,tokenId,sourceType,expiryYear,expiryMonth); 
		String strAck = nvp.get("RESULT").toString();
		String StrToken=null;
		
		 if ("Success".equalsIgnoreCase(strAck))
		    {
			  StrToken = nvp.get("TOKEN").toString();
			  
			  String strCardName = nvp.get("SOURCEOFFUNDS.PROVIDED.CARD.BRAND").toString();
			  
				////System.out.println("Token no is  " + StrToken);
				////System.out.println("Card Name " + strCardName);

		    }else{
		    	
		    	 StrToken = "FAIL";
		    			 //nvp.get("ERROR.EXPLANATION").toString();
		    	 
		    
		    	
		    	
		    }
		 return StrToken;
		
	}

	
    public static HashMap DoToken(YFSEnvironment env, String TNS_URL,String tokenId,String sourceType, String expiryYear, String expiryMonth)    
    {	  
	   	
	      ////System.out.println("Inside Credit Card  Do Token loop");
  	  String nvpStr = "&sourceOfFunds.token="+tokenId;
	      nvpStr += "&sourceOfFunds.type=" + sourceType;
	     nvpStr += "&sourceOfFunds.provided.card.expiry.month=" + expiryMonth;
	    nvpStr += "&sourceOfFunds.provided.card.expiry.year=" + expiryYear;
   	  HashMap nvp = httpcall("TOKENIZE", nvpStr,TNS_URL);
        ////System.out.println("after Token call"+nvp);            
  	  return nvp;
  	  
  	  }

	public static HashMap DoVerify(YFSEnvironment env,String TNS_URL, String tokenId,String sourceType, String transactionIdStr, String Orderid)    
	{	  
		
				String currencyCodestr = "USD";
				
				
			  ////System.out.println("Inside Credit Card  Do Token loop");
			String nvpStr = "&order.id=" + Orderid; 
			   //nvpStr += "&transaction.amount=" + requestAmountStr;
			 nvpStr += "&transaction.id=" + transactionIdStr;
			 nvpStr += "&transaction.currency=" + currencyCodestr;
			// nvpStr += "&sourceOfFunds.type=" + sourceType;
			 nvpStr += "&sourceOfFunds.token="+tokenId;
			HashMap nvp = httpcall("VERIFY", nvpStr,TNS_URL);
			  ////System.out.println("after verify call"+nvp);            
			  return nvp;
	  
	  }
	


	public static HashMap httpcall(String methodName, String nvpStr, String TNS_URL)
	{
		////System.out.println("Inside paypal  http calls loop");
		////System.out.println("Method name is "+methodName);
		////System.out.println("NVPString" +nvpStr);
	
	
		boolean bSandbox=true;
		String PAYPAL_URL=null;
		String gv_APIEndpoint=null;
		String gv_APIUserName = "merchant.TESTVSIDEMO01";
		String gv_APIPassword = "9ca3284b5b88784c56b97c6ab1487726";	
	  String gv_Merchant = "VSI_08002";
	  String gv_Version=null ;
		String gv_ProxyServer;
		String gv_ProxyServerPort;
		String gv_BNCode=null;
		int gv_Proxy;
		boolean gv_UseProxy;
		boolean USE_PROXY = false;
	
		{
		if (bSandbox == true)	
		{
			gv_APIEndpoint = TNS_URL;		
			
		}
		else{	
			
			gv_APIEndpoint = TNS_URL;		
			
			} 	
		
		          String HTTPREQUEST_PROXYSETTING_SERVER = "";	
		          String HTTPREQUEST_PROXYSETTING_PORT = "";	
		          gv_ProxyServer	= HTTPREQUEST_PROXYSETTING_SERVER;
		          gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;	
		          gv_Proxy	= 2;
		          String agent = "Mozilla/4.0";
	            String respText = "";
	            HashMap nvp=null;
	       
	       String encodedData =   "&merchant="+ gv_Merchant +  "&apiOperation=" + methodName+ nvpStr;
	       ////System.out.println("encodedData is :"+encodedData);
	       try 
	       {
	           ////System.out.println("url is :" + gv_APIEndpoint);
	
	      	 URL postURL = new URL( gv_APIEndpoint );
	               HttpURLConnection conn = (HttpURLConnection)postURL.openConnection();
	               conn.setDoInput (true);
	               conn.setDoOutput (true);
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
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeValue", codeValue);
		return docOutput;
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
	
}
