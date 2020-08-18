package com.vsi.oms.userexit;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.ssdcs.log.SSDCSLogger;
import com.sterlingcommerce.ssdcs.ue.ISSDCSTokenize;
import com.vsi.oms.utils.XMLUtil;


/**
 * @author nish.pingle
 *
 */
public class VSISSDCSTokenizeUEImpl implements ISSDCSTokenize{

	private static final SSDCSLogger log = SSDCSLogger.getLogger(VSISSDCSTokenizeUEImpl.class);
	private static Properties ssdcsProperties = new Properties();
	/*String creditCardNumber = "";
	String creditCardType = "";
	Input XML for this method
	 * <Tokenize Pan="" DisplayNumber="" SterlingPaymentType=""/>
	 * Output: 	<Tokenize Token="" DisplayNumber=""/>
	 * */

//	@Override
	public Document tokenize(Document inXML) throws Exception {

	
		Element rootEle = inXML.getDocumentElement();
		
/*		String card = rootEle.getAttribute("Pan");
		String dp = rootEle.getAttribute("DisplayNumber");
		String spt = rootEle.getAttribute("SterlingPaymentType");

		System.out.println("Pan from SSDCS:"+card);
		System.out.println("DisplayNumber from SSDCS:"+dp);
		System.out.println("SterlingPaymentType from SSDCS:"+spt);
*/		
		
		//String creditCardNo="4012000033330026";
		String creditCardNo=rootEle.getAttribute("Pan");;
		
		String sourceType="CARD";
		
		String expiryMonth="01";
		String expiryYear="99";

		
		
		String tokenIdStr=TNStokenization(creditCardNo,sourceType,expiryYear,expiryMonth);
		Document returnDoc = XMLUtil.createDocument("Tokenize");
		returnDoc.getDocumentElement().setAttribute("Token", tokenIdStr);
		
		return returnDoc;
	}

	public String TNStokenization(String creditCardNo,String sourceType, String expiryYear, String expiryMonth) throws Exception
	{
		HashMap nvp = DoToken(creditCardNo,sourceType,expiryYear,expiryMonth); 
		String strAck = nvp.get("RESULT").toString();
		String StrToken=null;
		
		 if ("Success".equalsIgnoreCase(strAck))
		    {
			  StrToken = nvp.get("TOKEN").toString();
				//System.out.println("Token no is  " + StrToken);

		    }else{
		    	
		    	throw new Exception("Invalid credit card number entered for Tokenization/Tokenization System is down");
		    	
		    }
		 return StrToken;
		
	}

	
    public static HashMap DoToken(String creditCardNo,String sourceType, String expiryYear, String expiryMonth)    
          {	  
    	   	
    	      //System.out.println("Inside Credit Card  Do Token loop");
    	      String nvpStr = "&sourceOfFunds.provided.card.number=" + creditCardNo;
     	      nvpStr += "&sourceOfFunds.type=" + sourceType;
     	     nvpStr += "&sourceOfFunds.provided.card.expiry.month=" + expiryMonth;
     	    nvpStr += "&sourceOfFunds.provided.card.expiry.year=" + expiryYear;         	 
     	    HashMap nvp = httpcall("TOKENIZE", nvpStr);
    //          //System.out.println("after Token call"+nvp);            
        	  return nvp;
        	  
        	  }
    
    
    public static HashMap httpcall( String methodName, String nvpStr)
    {
		//System.out.println("Inside paypal  http calls loop");
		//System.out.println("Method name is "+methodName);
		//System.out.println("NVPString" +nvpStr);

		

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
			gv_APIEndpoint = "http://app-vsi-datapower-qa/api/nvp/version/20";		
			
		}
		else{	
			
			gv_APIEndpoint = "http://app-vsi-datapower-qa/api/nvp/version/20";		
			
			} 	
		
		          String HTTPREQUEST_PROXYSETTING_SERVER = "";	
		          String HTTPREQUEST_PROXYSETTING_PORT = "";	
		          gv_ProxyServer	= HTTPREQUEST_PROXYSETTING_SERVER;
		          gv_ProxyServerPort = HTTPREQUEST_PROXYSETTING_PORT;	
		          gv_Proxy	= 2;
		          String agent = "Mozilla/4.0";
                  String respText = "";
                  HashMap nvp=null;
             
             String encodedData =   "&merchant="+ gv_Merchant + "&apiOperation=" + methodName+ nvpStr;
             //System.out.println("encodedData is :"+encodedData);
             try 
             {
                 //System.out.println("url is :" + gv_APIEndpoint);

            	 URL postURL = new URL( gv_APIEndpoint );
                     HttpURLConnection conn = (HttpURLConnection)postURL.openConnection();
                     conn.setDoInput (true);
                     conn.setDoOutput (true);
                     conn.setConnectTimeout(5000);
                     conn.setReadTimeout(5000);
                     conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                     conn.setRequestProperty( "User-Agent", agent );
                     
                     conn.setRequestProperty( "Content-Length", String.valueOf( encodedData.length()) );
                     //System.out.println("encodedData:" + encodedData);
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
	
}
