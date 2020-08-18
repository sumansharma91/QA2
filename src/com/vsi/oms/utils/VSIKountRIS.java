package com.vsi.oms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream.GetField;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;	
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;













import javax.print.DocFlavor.INPUT_STREAM;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.varia.NullAppender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kount.ris.Inquiry;
import com.kount.ris.KountRisClient;
import com.kount.ris.Response;
import com.kount.ris.Update;
import com.kount.ris.transport.HttpApiTransport;
import com.kount.ris.transport.KountHttpTransport;
import com.kount.ris.transport.Transport;
import com.kount.ris.util.Address;
import com.kount.ris.util.AuthorizationStatus;
import com.kount.ris.util.BankcardReply;
import com.kount.ris.util.CartItem;
import com.kount.ris.util.CurrencyType;
import com.kount.ris.util.InquiryMode;
import com.kount.ris.util.MerchantAcknowledgment;
import com.kount.ris.util.RisException;
import com.kount.ris.util.RisResponseException;
import com.kount.ris.util.ShippingType;
import com.kount.ris.util.UpdateMode;
import com.kount.ris.util.payment.NoPayment;
import com.kount.ris.util.payment.Payment;
import com.vsi.oms.userexit.VSIProcessFraudHold;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIKountRIS {

	

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIKountRIS.class);
	
	//check all values and attributes
	// check getvalues and get key pairs
	
	public static HashMap callKountRequest(YFSEnvironment env, String Kount_Request) {
		HashMap responseMap = new HashMap();
			if (Kount_Request != null) {
				HashMap kount_Request_map = deformatNVP(Kount_Request);
				String password=null;
				String strURL=null;
				String loc=null;
				URL urlKount=null;
				try {
					//password = kount_Request_map.get("SEC").toString();
					strURL = kount_Request_map.get("URL").toString();
					//loc = kount_Request_map.get("LOC").toString();
					urlKount = new URL(strURL);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("ERR=Check LOC,URL,SEC!!&");
					//e.printStackTrace();
				}
//				System.out.println("SEC="+password);
//				System.out.println("URL="+URL);
//				System.out.println("LOC="+loc);
				//boolean check = new File(directory, temp).exists();
				String kountKey=YFSSystem.getProperty("KOUNT_API_KEY");
				//String kountKey="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxMTU4MDAiLCJhdWQiOiJLb3VudC4xIiwiaWF0IjoxNDMzODk2MzUzLCJzY3AiOnsia2EiOm51bGwsImtjIjpudWxsLCJhcGkiOnRydWUsInJpcyI6dHJ1ZX19.91naSB5UeFTJGXfgcUm66EAdB7U2FwPTGK1YMR24V9w";		
				KountRisClient Kount_Ris_Request_Client = null;
				
	 					//Transport t = new HttpApiTransport(urlKount,kountKey);
				//((HttpApiTransport) t).setApiKey("SUNx509");

					//System.out.println("client not null");
				Kount_Ris_Request_Client = new KountRisClient(urlKount,kountKey);
				//Kount_Ris_Request_Client.setTransport(t);
				
				if(Kount_Ris_Request_Client!=null)
				{
				
					
					responseMap = 		InquirKount(env,kount_Request_map,Kount_Ris_Request_Client);
				}
				else
					if(log.isDebugEnabled()){
						log.info("ERR=Connect initialization error&");
					}
			}

		
		return responseMap;
	}

	
	
	@SuppressWarnings("rawtypes")
	private static HashMap InquirKount(YFSEnvironment env, HashMap kount_Request_map, KountRisClient kount_Ris_Request_Client) {

		//System.out.println("inside inquire");
		
		HashMap kountResponseMap = new HashMap();
		Inquiry kount_Ris_Query = new Inquiry();
		//Set's the required keys
		setrequiredkeys(kount_Ris_Query, kount_Request_map);
		
		//Set's the optional keys
		setoptinalkeys(kount_Ris_Query, kount_Request_map);
		
		//Loads carts
		loadcart(kount_Ris_Query, kount_Request_map);
		
		
		//gets the response from kount Inquery
		Response kountResponse = callrequest(env,kount_Ris_Query,kount_Ris_Request_Client);
		try{
		if(kountResponse.getErrorCount()>0)
		{
			//System.out.println("inside inquire error ");
			if(log.isDebugEnabled()){
				log.info("ERR="+kountResponse.getErrors()+"&");
			}
		}
		else{
		
		
		kountResponseMap = ProcessResponse(kountResponse, kount_Request_map,kount_Ris_Request_Client	);
		}
		}
		
		catch(Exception e)
		{
			
		}
		
		return kountResponseMap;
	}

	

	@SuppressWarnings("rawtypes")
	private static HashMap ProcessResponse(Response kountResponse,
			HashMap kount_Request_map, KountRisClient kount_Ris_Request_Client) {
		// TODO Auto-generated method stub
		HashMap kount_Response_map = new HashMap();
		if (kountResponse != null) {

			//UpdateKOUNT(kount_Request_map, kountResponse,kount_Ris_Request_Client);
			if(log.isDebugEnabled()){
				log.info("AUTO=" + kountResponse.getAuto() + "&TRAN="
						+ kountResponse.getTransactionId());
			}
			kount_Response_map.put("AUTO", kountResponse.getAuto());
			kount_Response_map.put("TRAN", kountResponse.getTransactionId());
		}
		
		return kount_Response_map;
	}

	

	

	private static Response callrequest(YFSEnvironment env, Inquiry kount_Ris_Query,
			KountRisClient kount_Ris_Request_Client) {

		try {
			Response kountResponse = kount_Ris_Request_Client.process(kount_Ris_Query);
			//System.out.println(kountResponse);
			return kountResponse;
			// this.transId = r.getTransactionId();
		} catch (RisException re) {
			// System.out.println("ERR="+re.getMessage()+"&");
			//re.printStackTrace();
		}catch (Exception e) {
			if(e instanceof SocketTimeoutException)
			{
				try {
					
					raiseAlert(env,kount_Ris_Query,"SocketTimeoutException while sending request to Kount");
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (YIFClientCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
					
			}
		}
		return null;
	}

	private static  void raiseAlert(YFSEnvironment env, Inquiry kount_Ris_Query, String reason) throws YFSException, RemoteException, YIFClientCreationException, ParserConfigurationException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();

		//InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);

		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION,
				reason);
		InboxElement.setAttribute("DetailDescription",
				kount_Ris_Query.toString());
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "10");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_FRAUD_ALERT");

		

		/*System.out.println("Printing createExInput: "
				+ XMLUtil.getXMLString(createExInput));*/
		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				createExInput);

		
	}



	// works perfectly
	@SuppressWarnings("rawtypes")
	private static void loadcart(Inquiry kount_Ris_Query,
			HashMap kount_Request_map) {
		Collection<CartItem> kount_Shopping_Cart = new Vector<CartItem>();
		int i = 0;
		while (kount_Request_map.containsKey("PROD_DESC[" + i + "]")) {
			String product_type = "1";
			String product_description = kount_Request_map.get(					"PROD_DESC[" + i + "]").toString();
			String product_item = kount_Request_map.get("PROD_ITEM[" + i + "]")					.toString();
			int product_price = Math.round((Float.parseFloat(kount_Request_map.get(					"PROD_PRICE[" + i + "]").toString())));
			int product_quantity = Integer.parseInt(kount_Request_map.get(					"PROD_QUANT[" + i + "]").toString());
			if( kount_Request_map.get("PROD_TYPE[" + i + "]") != null &&  !kount_Request_map.get("PROD_TYPE[" + i + "]").equals("")){
				product_type = kount_Request_map.get("PROD_TYPE[" + i + "]")					.toString();
			}
			kount_Shopping_Cart.add(new CartItem(product_type, product_item,product_description, product_quantity, product_price));
			
			i++;
		}
		kount_Ris_Query.setCart(kount_Shopping_Cart);
	}

	@SuppressWarnings("rawtypes")
	private static  void setoptinalkeys(Inquiry kount_Ris_Query,
			HashMap kount_Request_map) {
		Address QueryShippingAddress = new Address();
		Address QueryBillingAddress = new Address();
		Iterator it = kount_Request_map.entrySet().iterator();
		while (it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry) it.next();
			
			if(pairs.getKey().toString().equalsIgnoreCase("AVST"))
			{
				if (pairs.getValue().toString().equalsIgnoreCase("M")) {
					kount_Ris_Query.setAvsAddressReply(BankcardReply.MATCH);
				} else  {
						if (pairs.getValue().toString().equalsIgnoreCase("N")) {
							kount_Ris_Query.setAvsAddressReply(BankcardReply.NO_MATCH);
						} else {
								if (pairs.getValue().toString().equalsIgnoreCase("X")) {
									kount_Ris_Query.setAvsAddressReply(BankcardReply.UNAVAILABLE);
								}
						}
				}
			}
			
			if(pairs.getKey().toString().equalsIgnoreCase("AVSZ"))	
			{
				if (pairs.getValue().toString().equalsIgnoreCase("M")) {
					kount_Ris_Query.setAvsZipReply(BankcardReply.MATCH);
				} else  {
						if (pairs.getValue().toString().equalsIgnoreCase("N")) {
							kount_Ris_Query.setAvsZipReply(BankcardReply.NO_MATCH);
						} else {
								if (pairs.getValue().toString().equalsIgnoreCase("X")) {
									kount_Ris_Query.setAvsZipReply(BankcardReply.UNAVAILABLE);
								}
						}
				}
			}
				
			
					if(pairs.getKey().toString().equalsIgnoreCase("B2A1"))
					{
						QueryBillingAddress.setAddress1(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("B2A2"))
					{
						QueryBillingAddress.setAddress2(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
					}
					if(pairs.getKey().toString().equalsIgnoreCase("B2CC"))
					{
						//QueryBillingAddress.setCountry(pairs.getValue().toString());
						QueryBillingAddress.setCountry("US");
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
					}
					if(pairs.getKey().toString().equalsIgnoreCase("B2CI"))
					{
						QueryBillingAddress.setCity(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("B2PC"))
					{
						QueryBillingAddress.setPostalCode(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
					}
			
						
					if(pairs.getKey().toString().equalsIgnoreCase("B2PN"))
					{
						kount_Ris_Query.setBillingPhoneNumber(pairs.getValue()								.toString());
					}
			
			
					
					if(pairs.getKey().toString().equalsIgnoreCase("B2ST"))
					{
						QueryBillingAddress.setState(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
						
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("BPREMISE"))
					{
						QueryBillingAddress.setPremise(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
						
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("BSTREET"))
					{
						QueryBillingAddress.setStreet(pairs.getValue().toString());
						kount_Ris_Query.setBillingAddress(QueryBillingAddress);
						
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("CASH"))
					{
						kount_Ris_Query.setCash(Integer.parseInt(pairs.getValue()								.toString()));
						
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("CVVR"))
					{
						if (pairs.getValue().toString().equalsIgnoreCase("M")) {
							kount_Ris_Query.setCvvReply(BankcardReply.MATCH);
						} else  {
								if (pairs.getValue().toString().equalsIgnoreCase("N")) {
									kount_Ris_Query.setCvvReply(BankcardReply.NO_MATCH);
								} else {
										if (pairs.getValue().toString().equalsIgnoreCase("X")) {
											kount_Ris_Query.setCvvReply(BankcardReply.UNAVAILABLE);
										}
								}
						}
					}
		
					if(pairs.getKey().toString().equalsIgnoreCase("DOB"))
					{
						kount_Ris_Query.setDateOfBirth(pairs.getValue().toString());
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("EPOC"))
					{
						kount_Ris_Query.setEpoch(Long.parseLong( pairs.getValue().toString()));
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("FRMT"))
					{
						//check
						//kount_Ris_Query.setEpoch(Long.parseLong( pairs.getValue().toString()));
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("GENDER"))
					{
						kount_Ris_Query						.setGender(pairs.getValue().toString() == "M" ? 'M'								: 'F');
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("NAME"))
					{
						kount_Ris_Query.setName(pairs.getValue().toString());
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("ORDR"))
					{
						kount_Ris_Query.setOrderNumber(pairs.getValue().toString());
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("S2A1"))
					{
						QueryShippingAddress.setAddress1(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("S2A2"))
					{
						QueryShippingAddress.setAddress2(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("S2CC"))
					{
						//QueryShippingAddress.setCountry(pairs.getValue().toString());
						QueryShippingAddress.setCountry("US");
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("S2CI"))
					{
						QueryShippingAddress.setCity(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("S2EM"))
					{
						kount_Ris_Query.setShippingEmail(pairs.getValue().toString());
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("S2NM"))
					{
						kount_Ris_Query.setShippingName(pairs.getValue().toString());
					}
					
					if(pairs.getKey().toString().equalsIgnoreCase("S2PC"))
					{
						QueryShippingAddress.setPostalCode(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
					

					if(pairs.getKey().toString().equalsIgnoreCase("S2PN"))
					{
						kount_Ris_Query.setShippingPhoneNumber(pairs.getValue().toString());
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("S2ST"))
					{
						QueryShippingAddress.setState(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
			
			
					if(pairs.getKey().toString().equalsIgnoreCase("SHTP"))
					{
						if (pairs.getValue().toString().equalsIgnoreCase("SD")) {
							kount_Ris_Query.setShippingType(ShippingType.SAME_DAY);
						} else  {
								if (pairs.getValue().toString().equalsIgnoreCase("ND")) {
									kount_Ris_Query.setShippingType(ShippingType.NEXT_DAY);
								} else {
										if (pairs.getValue().toString().equalsIgnoreCase("2D")) {
											kount_Ris_Query.setShippingType(ShippingType.SECOND_DAY);
										}
										else if (pairs.getValue().toString().equalsIgnoreCase("ST")) {
											kount_Ris_Query.setShippingType(ShippingType.STANDARD);
										}
								}
						}
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("SPREMISE"))
					{
						QueryShippingAddress.setPremise(pairs.getValue().toString());
						kount_Ris_Query.setShippingAddress(QueryShippingAddress);
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("SSTREET"))
					{
						//check
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("UNIQ"))
					{
						kount_Ris_Query						.setUniqueCustomerId(pairs.getValue().toString());
					}
			
					if(pairs.getKey().toString().equalsIgnoreCase("UAGT"))
					{
						kount_Ris_Query.setUserAgent(pairs.getValue().toString());
					}
			

		}

	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	private static  void setrequiredkeys(Inquiry kount_Ris_Query,
			HashMap kount_Request_map) {

		Iterator it = kount_Request_map.entrySet().iterator();
		String last,lastval;;
		try {
			while (it.hasNext())
			{
				Map.Entry pairs = (Map.Entry) it.next();
				last = pairs.getKey().toString();
				lastval=pairs.getValue().toString();
				
				
				if(pairs.getKey().toString().equalsIgnoreCase("ANID"))
				{
					kount_Ris_Query.setAnid(pairs.getValue().toString());
				}
				if(pairs.getKey().toString().equalsIgnoreCase("AUTH"))
				{
					if (pairs.getValue().toString().equalsIgnoreCase("A")) {
						kount_Ris_Query.setAuthorizationStatus(AuthorizationStatus.APPROVED);
					} else
						kount_Ris_Query
								.setAuthorizationStatus(AuthorizationStatus.DECLINED);

					
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("CURR"))
				{
					if (pairs.getValue().toString().equalsIgnoreCase("USD")) {
						kount_Ris_Query.setCurrency("USD");
					} else {
						if (pairs.getValue().toString().equalsIgnoreCase("AUD")) {
							kount_Ris_Query.setCurrency("AUD");
						} else {
							if (pairs.getValue().toString().equalsIgnoreCase("CAD")) {
								kount_Ris_Query.setCurrency("CAD");
							} else {
								if (pairs.getValue().toString().equalsIgnoreCase("EUD")) {
									kount_Ris_Query.setCurrency("EUD");
								} else {
									if (pairs.getValue().toString().equalsIgnoreCase("HKD")) {
										kount_Ris_Query.setCurrency("HKD");
									} else {
										if (pairs.getValue().toString().equalsIgnoreCase("JPY")) {
											kount_Ris_Query.setCurrency("JPY");
										} else {
											if (pairs.getValue().toString().equalsIgnoreCase("NZD")) {
												kount_Ris_Query.setCurrency("NZD");
											} 
										}
									}
								}
							}
						}
					}
				}
				
				
				if(pairs.getKey().toString().equalsIgnoreCase("EMAL"))
				{
					kount_Ris_Query.setEmail(pairs.getValue().toString());
					
					//Approve Email ID
					//kount_Ris_Query.setEmail("JOHNDOEAPPROVE@ACME.COM");
					
					//Decline Email ID
					//kount_Ris_Query.setEmail("JOHNDOEDECLINE@ACME.COM");
					
					//Review Email ID
					//kount_Ris_Query.setEmail("JOHNDOEREVIEW@ACME.COM");
				}

				if(pairs.getKey().toString().equalsIgnoreCase("IPAD"))
				{
					kount_Ris_Query.setIpAddress(pairs.getValue().toString());
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("MACK"))
				{
					kount_Ris_Query.setMerchantAcknowledgment(MerchantAcknowledgment.YES);
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("MERC"))
				{
					kount_Ris_Query.setMerchantId(Integer.parseInt(pairs.getValue()						.toString()));
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("MODE"))
				{
					if (pairs.getValue().toString().equalsIgnoreCase("Q")) {
						kount_Ris_Query.setMode(InquiryMode.INITIAL_INQUIRY);
					} else {
						if (pairs.getValue().toString().equalsIgnoreCase("P")) {
							kount_Ris_Query.setMode(InquiryMode.PHONE_ORDER);
						} else {
							if (pairs.getValue().toString().equalsIgnoreCase("U")) {
								//check
							}
						}
					}
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("PTYP"))
				{
					
					try {
						
						Payment payment;
						if(pairs.getValue().toString().equals("PYPL"))
						{
							NoPayment testing = new NoPayment();
							kount_Ris_Query.setPayment(testing);
						}
						else
						{
						try {
							
							
							payment = new Payment(pairs.getValue().toString(),kount_Request_map.get("PTOK").toString());
							kount_Ris_Query.setPayment(payment);
						} catch (Exception e) {
							//fix
							NoPayment testing = new NoPayment();
							kount_Ris_Query.setPayment(testing);
							//e.printStackTrace();
						}

						
					}} catch (Exception e) {

						e.printStackTrace();
					}
					
				}
				
				if(pairs.getKey().toString().equalsIgnoreCase("SESS"))
				{
					kount_Ris_Query.setSessionId(pairs.getValue().toString());
				}

				if(pairs.getKey().toString().equalsIgnoreCase("SITE"))
				{
					kount_Ris_Query.setWebsite(pairs.getValue().toString());
				}

				if(pairs.getKey().toString().equalsIgnoreCase("TOTL"))
				{
					kount_Ris_Query.setTotal(Math.round((Float.parseFloat((String) pairs
							.getValue())))); 
				}

				
				
				


			}

		} catch (Exception e) {
			log.error("ERR+"+e.getMessage()+"&");
			//e.printStackTrace();
			// TODO: handle exception
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static HashMap deformatNVP(String pPayload) {
		HashMap nvp = new HashMap();
		StringTokenizer stTok = new StringTokenizer(pPayload, "&");
		while (stTok.hasMoreTokens()) {
			StringTokenizer stInternalTokenizer = new StringTokenizer(stTok.nextToken(), "=");
			if (stInternalTokenizer.countTokens() == 2) {
				@SuppressWarnings("deprecation")
				
				String key = stInternalTokenizer.nextToken();
				
				@SuppressWarnings("deprecation")
				String value = stInternalTokenizer.nextToken();
				nvp.put(key.toUpperCase(), value);
				
			}
		}
		return nvp;
	}


}
