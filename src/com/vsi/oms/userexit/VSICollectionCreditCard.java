package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.api.omp.receipts.inspectReceipt;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;

/**
 * @author Harshit Arora,Nish Pingle
 * @version 1.0
 * 
 *          This class provides BusinessLogic specific to VSI for UserExit
 *          YFSCollectionCreditCardUE (as part of transaction Payment Execution)
 *          It handles Credit Card Authorization, Settlement and Refund
 */

public class VSICollectionCreditCard implements YFSCollectionCreditCardUE {
	static {
		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

					public boolean verify(String hostname,
							javax.net.ssl.SSLSession sslSession) {
						//OMS-868 :Start
					    String strDatePowerHost= YFSSystem.getProperty("APP_VSI_DATAPOWER_HOST");
						if (hostname.equals(strDatePowerHost)) {
						//OMS-868:End
						}
						return false;
					}
				});
	}
	/**
	 * Instance of logger
	 */

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICollectionCreditCard.class.getName());
	
	YIFApi api;

	public YFSExtnPaymentCollectionOutputStruct collectionCreditCard(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct)
			throws YFSUserExitException {
		

		try {
			if(log.isDebugEnabled()){
				log.debug("================Inside VSICollectionCreditCard================================");
				log.debug("Printing Input XML :");
			}
			YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
			String paymentType = inStruct.paymentType;

			String strChargeType = inStruct.chargeType;

			Double dRequestAmount = inStruct.requestAmount;
			
			if (VSIConstants.PAYMENT_MODE_CC
					.equalsIgnoreCase(inStruct.paymentType)) {

				if ("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)) {
					if (inStruct.requestAmount >= 0.0D) {
						doAuthorization(inStruct, outStruct, env);
					} else {
						doVoid(inStruct, outStruct, env);
					}

				} else if ("CHARGE".equalsIgnoreCase(inStruct.chargeType)) {

					if (inStruct.requestAmount >= 0.0D) {
						doCharge(inStruct, outStruct, env);

					} else {
						doRefund(inStruct, outStruct, env);
					}

				}
			} else {
				throw new YFSException("PAYMENT_FAILURE", "PAYMENT_FAILURE",
						"Invalid Payment Tender");
			}

			return outStruct;
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();

		}

	}
	/**********************************************************************************
	 * Method Name : doAuthorization
	 * 
	 * Description : this method will call tns for authorization 
	 * 
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/09/2014 this method is invoked for authorization
	 **********************************************************************************/
	
	void doAuthorization(YFSExtnPaymentCollectionInputStruct inStruct, YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {

		try {

			// Fields to get token infomration

			// String creditCardNo="4012000033330026";
			// String expiryMonth="5";
			// String expiryYear="17";
			// String securityCode="200";
			// String sourceType="CARD";
			// String
			// tokenIdStr=TNStokenization(env,creditCardNo,expiryMonth,expiryYear,securityCode,sourceType);
			String transactionIdStr = null;
			String Orderid = null;
			String merchantId = null;
			String generateId = null;
			String strOrderHeaderKey = inStruct.orderHeaderKey;
			String entCode = "DEFAULT";
			//OMS-1635 : Start
			String strIsNewOrder = checkNewOrder(env, strOrderHeaderKey);
			String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,entCode,"DefaultMerchPrefixActive");
			String merchantPrefix = getCommonCodeLongDescriptionByCodeValue(env,entCode,"MerchPrefix");
			if ( !YFCCommon.isVoid(strDefaultPrefixReqd)  && "Y".equals(strDefaultPrefixReqd) && !YFCCommon.isVoid(strIsNewOrder) && "Y".equals(strIsNewOrder))
			{
				//Defaulting merchantId as 06101
				merchantId = merchantPrefix+"06101";
			}
			else
			{	
			// getting the merchant id
				String storeId = getShipNode(env, strOrderHeaderKey);
				merchantId = merchantPrefix + storeId;
			}
			if(log.isDebugEnabled()){
				log.debug("merchantId is" + merchantId);
			}
			//OMS-1635 : End
			DecimalFormat df1 = new DecimalFormat("#,###,###.00");
			String currencyCodestr = "USD";
			//Fix for order with greater than 1000 getting declined auth due to format of the amount.
			
			//double dRequestAmount = inStruct.requestAmount;
			//String requestAmountStr = df1.format(dRequestAmount);
			
			String requestAmountStr=Double.toString(inStruct.requestAmount);  
			String sourceOfFundstype = "CARD";
			String tokenIdStr = inStruct.paymentReference2;
			String firstName = inStruct.firstName;
			String lastName = inStruct.lastName;
			String expDate = inStruct.creditCardExpirationDate;
			String AdressLine1=inStruct.billToAddressLine1;
			String City=inStruct.billToCity;
			String ZipCode=inStruct.billToZipCode;
			String State=inStruct.billToState;
			
//-----------------------------------------------Kount Token Faked REMOVE ONCE VERIFIED---------------------------------------------------------------------------------------------------
//String KountToken="12321321321";
//String KountToken=inStruct.paymentReference6;
//-----------------------------------------------Kount Token Faked REMOVE ONCE VERIFIED---------------------------------------------------------------------------------------------------
			
			
			
			//jira 803
			String country=inStruct.billToCountry;
			
			// change 2 char country to 3 char
			if(country.length() == 2) {
				country = VSIUtils.getCountryCode(env, country);
			}
			//jira 803		
			
			//System.out.println("ExpDate is " + expDate);

			String expMonth = expDate.substring(0, 2);
			//System.out.println("Exp mnth is " + expMonth);

			String expYr = expDate.substring(expDate.length() - 2);
			//System.out.println("Exp yr is " + expYr);

			String concExpDt = expMonth + expYr;
			//System.out.println("new Exp dt is " + concExpDt);

			if (inStruct.paymentReference1 != null) {
				// generateId=generateId(env,inStruct,strOrderHeaderKey);
				String atgTranId = inStruct.paymentReference1;
				String sChargeTransactionKey = inStruct.chargeTransactionKey;
				String sGeneratedOrderId = generateOrderId(atgTranId,
						sChargeTransactionKey);
				// String atgTranSubStr=atgTranId.substring(0,
				// atgTranId.length()-3);
				// omsTranId=atgTranSubStr+generateId;
				Orderid = sGeneratedOrderId;
				transactionIdStr = sGeneratedOrderId;

			} else {
	    	 	String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
				transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
				Orderid = inStruct.chargeTransactionKey+tranNumber;
			}

			/**
			 * HashMap nvp =
			 * VSICreditCardFunction.DoAuthorization(Orderid,merchantId
			 * ,apiUsername,apiPassword,requestAmountStr,
			 * currencyCodestr,transactionIdStr,sourceOfFundstype,tokenIdStr);
			 **/
			
	
					
            //making the call to TNS
	//KountToken Will be edited when Kount token is verified add ,KountToken
			HashMap nvp = VSICreditCardFunction.DoAuthorization(Orderid,
					merchantId, requestAmountStr, currencyCodestr,
					transactionIdStr, sourceOfFundstype, tokenIdStr, firstName,
					lastName, expYr, expMonth,AdressLine1,City,ZipCode,State,country, env,strOrderHeaderKey);
			
			 Document doc = XMLUtil.createDocument("PaymentRecords");
     		Element elePayRecrds = doc.getDocumentElement();
     		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
     				strOrderHeaderKey);
     		elePayRecrds.setAttribute("Record", nvp.toString());
     		elePayRecrds.setAttribute("ChargeType", "Authorize Response");
     		YIFApi api;
     		api = YIFClientFactory.getInstance().getApi();
     		api.executeFlow(env,"VSIPaymentRecords", doc);
			
			//Printing the Response
			
            //pasrsing the rsponse
			if (nvp.containsKey("RESULT")) {
				if(log.isDebugEnabled()){
					log.debug("TNS Response:"+nvp.toString());
				}
				if ("Success".equalsIgnoreCase(nvp.get("RESULT").toString())) {
					String AvsResp=null;
					String AvsRespCode=null;
					String strAVSgatway=null;
					String strAck = nvp.get("RESULT").toString();
					//System.out.println("Autorization is" + strAck);
					String transactionId = nvp.get("TRANSACTION.ID").toString();
					requestAmountStr = nvp.get("TRANSACTION.AMOUNT").toString();
					String transactionTerminal = nvp
							.get("TRANSACTION.TERMINAL").toString();
					String transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
					String transactionSource = "";
					//OMS-1935 START
					if(isCallCenterTran)
					{
						ArrayList<Element> listTransactionSource;
						listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
						if(!listTransactionSource.isEmpty()){
							Element eleCommonCode=listTransactionSource.get(0);
							String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue)){
								transactionSource = strTSValue;
							}
						}
					}
					else
					{
						ArrayList<Element> listTransactionSourceNonSTH;
						listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
						if(!listTransactionSourceNonSTH.isEmpty()){
							Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
							String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue1)){
								transactionSource = strTSValue1;

							}
						}
					}
					//OMS-1935 END
					String transactionAuthCode = nvp.get(
							"TRANSACTION.AUTHORIZATIONCODE").toString();
					//oms-2046 start
					
					 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
					 strAVSgatway = nvp.get(
							"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
					 }
					//oms-2046 end
					//String transactionReceipt = "110001";
					
					String transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
					String transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
			

					// String transactionAqDate =
					// nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
					String transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE")
							.toString();
					String authCode = nvp.get("TRANSACTION.AUTHORIZATIONCODE")
							.toString();
					
					if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE")){
						  AvsRespCode = nvp.get("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE").toString();
						 }
					AvsResp="AvsResp-"+AvsRespCode;

					double authAmountStr = Double.parseDouble(requestAmountStr);
					outStruct.authorizationId = transactionId;
					outStruct.authorizationAmount = authAmountStr;
					// outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					outStruct.retryFlag = "N";

					// Setting the expiration date to + 7
					Calendar calendar = Calendar.getInstance();
					Document getCommonCodeListInputForReAuthTime = getCommonCodeListInputForCodeType("DEFAULT","CC_AUTH_EXP_TIME");
					 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForReAuthTime);
					 Element eleCOmmonCodeOut = null;
					 String strReAuthMinutes = "";
					 if(!YFCObject.isVoid(getCommonCodeOut)){
						 eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						 if(!YFCObject.isVoid(eleCOmmonCodeOut)){
							 strReAuthMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
						 }
					 }
					int authMinutes = 10080 ; //7 days default
					if(!YFCObject.isVoid(strReAuthMinutes))
						authMinutes = Integer.parseInt(strReAuthMinutes);
					calendar.add(Calendar.MINUTE,authMinutes);
					//calendar.add(Calendar.DATE, 7);
					String authExpDate = new SimpleDateFormat("yyyyMMddHHmmss")
							.format(calendar.getTime());
					outStruct.authorizationExpirationDate = authExpDate;
					outStruct.authCode = authCode;
					outStruct.tranAmount = authAmountStr;
					
//payment reference 6 to Kount Token
//outStruct.PaymentReference6=KountToken;
					
					/**
					 * if(inStruct.paymentReference1!=null){ //incementing the
					 * sequence at order header int
					 * generateInt=Integer.parseInt(generateId); int
					 * tranSequenceInt=generateInt+VSIConstants.ATTR_SEQ;
					 * changeTranSequence
					 * (env,strOrderHeaderKey,tranSequenceInt); }
					 **/

					// Inserting records for authorization for charge
					tnsAuthRecords(env, strOrderHeaderKey, transactionId,
							transactionTerminal, transactionReceipt,
							transactionSource, transactionAuthCode,
							transactionAqTime, transactionAqDate,
							transactionAqCode,concExpDt,AvsResp,strAVSgatway);
				}
				
				//if authorization is failure
				else if ( "DECLINE".equalsIgnoreCase(nvp.get("RESULT")
						.toString()) ||  ( nvp.containsKey("RESPONSE.GATEWAYCODE") && "DECLINED".equalsIgnoreCase(nvp.get("RESPONSE.GATEWAYCODE").toString()))) {
					String errorCodeStr = "AuthFail";
					String errorMessage = "AuthFail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					// outStruct.suspendPayment="Y";
					String strAQRiskified = nvp.get("RESPONSE.ACQUIRERCODE").toString();
					String strRiskifiedFlag=VSIConstants.FLAG_Y;
					// raising Credit card alerts
					creditCardAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct);
					// raising holds and cancelling orders
					authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag);
				}
				//OMS-1564: Raising alert when we are getting ERROR response due to network issue : START
				else if ("ERROR".equalsIgnoreCase(nvp.get("RESULT")
						.toString()))
				{
					String errorCodeStr = "AuthFail due to Error Response";
					String errorMessage = "AuthFail due to Error Response";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					applyPaymentErrorHold(env, strOrderHeaderKey);

					// raising Credit card alerts
					creditCardAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct);
				}
				//OMS-1564: Raising alert when we are getting ERROR response due to network issue : END
				else if ("FAILURE".equalsIgnoreCase(nvp.get("RESULT").toString())) {
					if("TIMED_OUT".equalsIgnoreCase(nvp.get("RESPONSE.GATEWAYCODE").toString())){
						Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
						 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
						Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						//String  retryMinutes="10";
						 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

						Calendar calendar = Calendar.getInstance();
						int retMinutes = 10 ; //10 default
						retMinutes = Integer.parseInt(retryMinutes);
						calendar.add(Calendar.MINUTE,retMinutes);
						outStruct.collectionDate=calendar.getTime();
						//calendar.add(Calendar.DATE, 7);
						//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
						//outStruct.collectionDate=sdf.parse(collectionDate);
						outStruct.retryFlag="Y";
							
					}
					else if("OFFLINE".equalsIgnoreCase(nvp.get("RESPONSE.ACQUIRERCODE").toString())){
						Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_OFFLINE");
						 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
						Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						//String  retryMinutes="10";
						 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

						Calendar calendar = Calendar.getInstance();
						int retMinutes = 10 ; //10 default
						retMinutes = Integer.parseInt(retryMinutes);
						calendar.add(Calendar.MINUTE,retMinutes);
						outStruct.collectionDate=calendar.getTime();
						//calendar.add(Calendar.DATE, 7);
						//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
						//outStruct.collectionDate=sdf.parse(collectionDate);
						outStruct.retryFlag="Y";
						String errorCodeStr = "OfflineAuth";
						String errorMessage = "OfflineAuth";
						String orderNoStr = inStruct.orderNo;
						String errorMessageStr = errorMessage;
						// outStruct.suspendPayment="Y";
						String failureTypeStr = "Credit Card Authorization Offile";

						// raising Credit card alerts
						raiseOfflineAlert(env, errorCodeStr, errorMessageStr,
								orderNoStr, strOrderHeaderKey, inStruct,failureTypeStr);
							
					}
					else{
					String transactionId=null;
					String AvsResp=null;
					String AvsRespCode=null;

					String transactionTerminal=null;
					String transactionReceipt=null;
					String transactionSource=null;
					String transactionAuthCode=null;
					String transactionAqTime=null;
					String transactionAqDate=null;
					String transactionAqCode=null;
					if(nvp.containsKey("TRANSACTION.ID")){
					 transactionId = nvp.get("TRANSACTION.ID").toString();
					 }
					if(nvp.containsKey("TRANSACTION.TERMINAL")){
					 transactionTerminal = nvp.get("TRANSACTION.TERMINAL").toString();
					}
					if(nvp.containsKey("TRANSACTION.RECEIPT")){
					 transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
					}
					//OMS-1935 START
					if(isCallCenterTran)
					{
						ArrayList<Element> listTransactionSource;
						listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
						if(!listTransactionSource.isEmpty()){
							Element eleCommonCode=listTransactionSource.get(0);
							String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue)){
								transactionSource = strTSValue;
							}
						}
					}
					else
					{
						ArrayList<Element> listTransactionSourceNonSTH;
						listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
						if(!listTransactionSourceNonSTH.isEmpty()){
							Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
							String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue1)){
								transactionSource = strTSValue1;

							}
						}
					}
					//OMS-1935 END
					 if(nvp.containsKey("TRANSACTION.AUTHORIZATIONCODE")){
					 transactionAuthCode = nvp.get("TRANSACTION.AUTHORIZATIONCODE").toString();
					 }
					 if(nvp.containsKey("TRANSACTION.ACQUIRER.TIME")){
					 transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
					 }
					 if(nvp.containsKey("TRANSACTION.ACQUIRER.DATE")){
					 transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
					 }
					 if(nvp.containsKey("RESPONSE.ACQUIRERCODE")){
					 transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE").toString();
					 }
					 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE")){
						  AvsRespCode = nvp.get("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE").toString();
						 }
					AvsResp="AvsResp-"+AvsRespCode;
					//oms-2046 start
					String strAVSgatway=null;
					 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
					 strAVSgatway = nvp.get(
							"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
					 }
					//oms-2046 end
					 
					String expDate1 = inStruct.creditCardExpirationDate;
					//System.out.println("ExpDate is " + expDate);

					String expMonth1 = expDate.substring(0, 2);
					//System.out.println("Exp mnth is " + expMonth);

					String expYr1 = expDate.substring(expDate.length() - 2);
					//System.out.println("Exp yr is " + expYr);

					String concExpDt1 = expMonth + expYr;
					
					tnsAuthRecords(env, strOrderHeaderKey, transactionId,
							transactionTerminal, transactionReceipt,
							transactionSource, transactionAuthCode,
							transactionAqTime, transactionAqDate,
							transactionAqCode,concExpDt1,AvsResp,strAVSgatway);
					
					String errorCodeStr = "AuthFail";
					String errorMessage = "AuthFail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					// outStruct.suspendPayment="Y";

					applyPaymentErrorHold(env, strOrderHeaderKey);					// raising Credit card alerts
					creditCardAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct);
					// raising holds and cancelling orders
					//authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct);
					}
				}
			}


			
			
			else if (null != nvp.get("errorCode").toString())

			{
				//for time out exception, setting the flag='Y', so that agent can retry the request
				if ("0x01130006".equalsIgnoreCase(nvp.get("errorCode")
						.toString())) {
					String errorCodeStr = "TNS Connection Fail";
					String errorMessage = "TNS Connection Fail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					outStruct.retryFlag = "Y";
					creditCardAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct);

				}

			}

		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}

	}
	/**********************************************************************************
	 * Method Name : dovoid
	 * 
	 * Description : this method will call tns for reversal of an authorization 
	 * 
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/09/2014 this method will call tns for reversal of an authorization in
	 * scenario of authorized order cancellation and authorization expiration
	 **********************************************************************************/
	void doVoid(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {

		try {
			String AvsResp=null;
			String AvsRespCode=null;
			String apiUsername = null;
			String apiPassword = null;
			String merchantId = null;
			String generateId = null;
			String Orderid = null;
			String omsTranId = null;
			String transactionIdStr = null;
			
			
			String strOrderHeaderKey = inStruct.orderHeaderKey;
			String entCode = "DEFAULT";
			//OMS-1635 : Start
			String strIsNewOrder = checkNewOrder(env, strOrderHeaderKey);
			String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,entCode,"DefaultMerchPrefixActive");
			String merchantPrefix = getCommonCodeLongDescriptionByCodeValue(env,entCode,"MerchPrefix");
			if ( !YFCCommon.isVoid(strDefaultPrefixReqd)  && "Y".equals(strDefaultPrefixReqd) && !YFCCommon.isVoid(strIsNewOrder) && "Y".equals(strIsNewOrder))
			{
				//Defaulting merchantId as 06101
				merchantId = merchantPrefix+"06101";
			}
			else
			{	
			// getting the merchant id
				String storeId = getShipNode(env, strOrderHeaderKey);
				merchantId = merchantPrefix + storeId;
			}
			if(log.isDebugEnabled()){
				log.debug("merchantId is" + merchantId);
			}
			//OMS-1635 : End
			String targetTranidStr = inStruct.authorizationId;
			if(!YFCCommon.isVoid(inStruct.paymentReference9))
			{
				Orderid = inStruct.paymentReference9;
			}
			else
			{
				Orderid = inStruct.authorizationId;
			}

			if (inStruct.paymentReference1 != null) {
				String atgTranId = inStruct.paymentReference1;
				String sChargeTransactionKey = inStruct.chargeTransactionKey;
				String sGeneratedOrderId = generateOrderId(atgTranId,
						sChargeTransactionKey);
				// String atgTranSubStr=atgTranId.substring(0,
				// atgTranId.length()-3);
				// omsTranId=atgTranSubStr+generateId;
				// Orderid=sGeneratedOrderId;
				transactionIdStr = sGeneratedOrderId;

			} else {
				
				String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
				transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
				// Orderid=inStruct.chargeTransactionKey;
			}
			
			String voidRequired = getCommonCodeLongDescriptionByCodeValue(
					env, entCode, "VoidRequired");
			
			if (null != voidRequired) {
				if ("Y".equalsIgnoreCase(voidRequired)) {
			

			HashMap nvp = VSICreditCardFunction.DoVoid(Orderid, merchantId,
					transactionIdStr, targetTranidStr, env,strOrderHeaderKey);
			
			 Document doc = XMLUtil.createDocument("PaymentRecords");
	     		Element elePayRecrds = doc.getDocumentElement();
	     		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
	     				strOrderHeaderKey);
	     		elePayRecrds.setAttribute("Record", nvp.toString());
	     		elePayRecrds.setAttribute("ChargeType", "Void Response");
	     		YIFApi api;
	     		api = YIFClientFactory.getInstance().getApi();
	     		api.executeFlow(env,"VSIPaymentRecords", doc);
			
	     		if(log.isDebugEnabled()){
	     			log.debug("TNS void Response:"+nvp.toString());
	     		}
			
			String strAck = nvp.get("RESULT").toString();
			//System.out.println("Autorization is" + strAck);
			if (null != nvp.get("RESULT").toString()) {
				if ("Success".equalsIgnoreCase(strAck)) {
					// String authorizationIdStr =
					// nvp.get("TRANSACTION.ID").toString();
					outStruct.authorizationId = inStruct.authorizationId;
					outStruct.authorizationAmount = inStruct.requestAmount;
					String transactionId = nvp.get("TRANSACTION.ID").toString();
					String transactionTerminal = nvp.get("TRANSACTION.TERMINAL").toString();
					String transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
					String transactionSource = "";
					//OMS-1935 START
					if(isCallCenterTran)
					{
						ArrayList<Element> listTransactionSource;
						listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
						if(!listTransactionSource.isEmpty()){
							Element eleCommonCode=listTransactionSource.get(0);
							String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue)){
								transactionSource = strTSValue;
							}
						}
					}
					else
					{
						ArrayList<Element> listTransactionSourceNonSTH;
						listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
						if(!listTransactionSourceNonSTH.isEmpty()){
							Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
							String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue1)){
								transactionSource = strTSValue1;

							}
						}
					}
					//OMS-1935 END
					String transactionAuthCode = "0000";
					String transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
					String transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
					String transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE").toString();
					String expDate = inStruct.creditCardExpirationDate;
					//System.out.println("ExpDate is " + expDate);

					String expMonth = expDate.substring(0, 2);
					//System.out.println("Exp mnth is " + expMonth);

					String expYr = expDate.substring(expDate.length() - 2);
					//System.out.println("Exp yr is " + expYr);

					String concExpDt = expMonth + expYr;
					//oms-2046 start
					String strAVSgatway=null;
					 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
					 strAVSgatway = nvp.get(
							"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
					 }
					//oms-2046 end
					
					// outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					outStruct.retryFlag = "N";
					// incrementing the sequence at order header
					// int generateInt=Integer.parseInt(generateId);
					// int tranSequenceInt=generateInt+VSIConstants.ATTR_SEQ;
					// changeTranSequence(env,strOrderHeaderKey,tranSequenceInt);
					tnsAuthRecords(env, strOrderHeaderKey, transactionId,
							transactionTerminal, transactionReceipt,
							transactionSource, transactionAuthCode,
							transactionAqTime, transactionAqDate,
							transactionAqCode,concExpDt,AvsResp,strAVSgatway);

				} 
				
				else if ("FAILURE".equalsIgnoreCase(strAck)) {
					if("TIMED_OUT".equalsIgnoreCase(nvp.get("RESPONSE.GATEWAYCODE").toString())){
						Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
						 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
						Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						//String  retryMinutes="10";
						 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

						Calendar calendar = Calendar.getInstance();
						int retMinutes = 10 ; //10 default
						retMinutes = Integer.parseInt(retryMinutes);
						calendar.add(Calendar.MINUTE,retMinutes);
						outStruct.collectionDate=calendar.getTime();
						//calendar.add(Calendar.DATE, 7);
						//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
						//outStruct.collectionDate=sdf.parse(collectionDate);
						outStruct.retryFlag="Y";
							
					}
					else{
					String transactionId=null;
					String transactionTerminal=null;
					String transactionReceipt=null;
					String transactionSource=null;
					String transactionAuthCode=null;
					String transactionAqTime=null;
					String transactionAqDate=null;
					String transactionAqCode=null;
					String AvsResp1=null;
					if(nvp.containsKey("TRANSACTION.ID")){
					 transactionId = nvp.get("TRANSACTION.ID").toString();
					 }
					if(nvp.containsKey("TRANSACTION.TERMINAL")){
					 transactionTerminal = nvp.get("TRANSACTION.TERMINAL").toString();
					}
					if(nvp.containsKey("TRANSACTION.RECEIPT")){
					 transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
					}
					//OMS-1935 START
					if(isCallCenterTran)
					{
						ArrayList<Element> listTransactionSource;
						listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
						if(!listTransactionSource.isEmpty()){
							Element eleCommonCode=listTransactionSource.get(0);
							String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue)){
								transactionSource = strTSValue;
							}
						}
					}
					else
					{
						ArrayList<Element> listTransactionSourceNonSTH;
						listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
						if(!listTransactionSourceNonSTH.isEmpty()){
							Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
							String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strTSValue1)){
								transactionSource = strTSValue1;

							}
						}
					}
					//OMS-1935 END
					 if(nvp.containsKey("TRANSACTION.AUTHORIZATIONCODE")){
					 transactionAuthCode = nvp.get("TRANSACTION.AUTHORIZATIONCODE").toString();
					 }
					 if(nvp.containsKey("TRANSACTION.ACQUIRER.TIME")){
					 transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
					 }
					 if(nvp.containsKey("TRANSACTION.ACQUIRER.DATE")){
					 transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
					 }
					 if(nvp.containsKey("RESPONSE.ACQUIRERCODE")){
					 transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE").toString();
					 }
					String expDate = inStruct.creditCardExpirationDate;
					//System.out.println("ExpDate is " + expDate);

					String expMonth = expDate.substring(0, 2);
					//System.out.println("Exp mnth is " + expMonth);

					String expYr = expDate.substring(expDate.length() - 2);
					//System.out.println("Exp yr is " + expYr);

					String concExpDt = expMonth + expYr;
					
					outStruct.authorizationId = inStruct.authorizationId;
					outStruct.authorizationAmount = inStruct.requestAmount;
					//outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					outStruct.retryFlag = "N";
					//oms-2046 start
					String strAVSgatway=null;
					 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
					 strAVSgatway = nvp.get(
							"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
					 }
					//oms-2046 end

					tnsAuthRecords(env, strOrderHeaderKey, transactionId,
							transactionTerminal, transactionReceipt,
							transactionSource, transactionAuthCode,
							transactionAqTime, transactionAqDate,
							transactionAqCode,concExpDt,AvsResp1,strAVSgatway);
					
					String errorCodeStr = "VoidFail";
					String errorMessage = "VoidFail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					//String failureTypeStr = "Credit Card Void Alert";

					// raising Credit card alerts
					//raiseVoidAlert(env, errorCodeStr, errorMessageStr,
						//	orderNoStr, strOrderHeaderKey, inStruct,failureTypeStr);
					//creditCardAlert(env, errorCodeStr, errorMessageStr,
							//orderNoStr, strOrderHeaderKey, inStruct);
				}
				}
				else if ("ERROR".equalsIgnoreCase(strAck)
						|| "DECLINE".equalsIgnoreCase(strAck) ) {
					outStruct.authorizationId = inStruct.authorizationId;
					outStruct.authorizationAmount = inStruct.requestAmount;
					//outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					outStruct.retryFlag = "N";

					
					String errorCodeStr = "VoidFail";
					String errorMessage = "VoidFail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
				//	String failureTypeStr = "Credit Card Void Alert";

					// raising Credit card alerts
				//	raiseVoidAlert(env, errorCodeStr, errorMessageStr,
						//	orderNoStr, strOrderHeaderKey, inStruct,failureTypeStr);
				}
			} else if (null != nvp.get("errorCode").toString())

			{
				if ("0x01130006".equalsIgnoreCase(nvp.get("errorCode")
						.toString())) {
					outStruct.authorizationId = inStruct.authorizationId;
					outStruct.authorizationAmount = inStruct.requestAmount;
					//outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					//outStruct.retryFlag = "N";

					String errorCodeStr = "TNS Connection Fail";
					String errorMessage = "TNS Connection Fail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					outStruct.retryFlag = "Y";
					// creditCardAlert(env,errorCodeStr,errorMessageStr,orderNoStr,strOrderHeaderKey,inStruct);

				}

			}

		} 
				
			}
			
		}
		
				catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}

	}
	
	/**********************************************************************************
	 * Method Name : doCharge
	 * 
	 * Description : this method will call tns for reversal of an authorization 
	 * 
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/09/2014 We are doing a dummy charge and updating vsi_ajb_charge_settlement
	 *  table
	 **********************************************************************************/

	void doCharge(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {

		try {
			String authorizationIdStr=null;
			String newAuthId=null;
			String orderHeaderKeyStr = inStruct.orderHeaderKey;
			//OMS-1635:Start
			String storeNo=null;
			String strIsNewOrder = checkNewOrder(env, orderHeaderKeyStr);
			String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","DefaultMerchPrefixActive");
			if ( !YFCCommon.isVoid(strDefaultPrefixReqd)  && "Y".equals(strDefaultPrefixReqd) && !YFCCommon.isVoid(strIsNewOrder) && "Y".equals(strIsNewOrder))
			{
				//Defaulting merchantId as 06101
				storeNo = "06101";
			}
			else
			{	
			// getting the merchant id
				storeNo = getShipNode(env, orderHeaderKeyStr);	
			}
			if(log.isDebugEnabled()){
				log.debug("storeNo is" + storeNo);
			}
			//String storeNo = getShipNode(env, orderHeaderKeyStr);
			//OMS-1635:End
			authorizationIdStr = inStruct.authorizationId;
			String tokenValue = inStruct.paymentReference2;

			String chargeTransactionKeyStr = inStruct.chargeTransactionKey;
			String transactionType = "Sale";
			String IsSettled = "N";
			String requestAmount = Double.toString(inStruct.requestAmount);
          //String storedAuthId=inStruct.authorizationId;
          if(YFCObject.isVoid(authorizationIdStr)){
        	  authorizationIdStr=DoAuthorizeforCharge(inStruct,outStruct,env);
        	 
        	 
          
          if(!YFCObject.isVoid(authorizationIdStr)){
			  
			   outStruct.authorizationId = authorizationIdStr;
		  

			outStruct.authorizationAmount = inStruct.requestAmount;
			//outStruct.collectionDate = new Date();
			//outStruct.executionDate = new Date();
			outStruct.retryFlag = "N";

			getAuthRecordsforCharge(env, orderHeaderKeyStr, storeNo,
					authorizationIdStr, tokenValue, requestAmount,
					transactionType, chargeTransactionKeyStr, IsSettled);

			String authCodeStr = getAuthcodeforCharge(env, orderHeaderKeyStr,
					authorizationIdStr);
			if (authCodeStr != null) {
				outStruct.authCode = authCodeStr;
			}
			//OMS-1526
			//createAuthRequest(env, chargeTransactionKeyStr, orderHeaderKeyStr);
			  
			  //retry logic is already covered in authCancel method
			  /** not required
			  	int retryCount=0;
			String strChargeTrasactionKey=inStruct.chargeTransactionKey;
			//getting the retry count from custom table
			retryCount=getRetryCount(env,orderHeaderKeyStr,strChargeTrasactionKey);
			if (retryCount>3){
			outStruct.retryFlag = "N";
			//outStruct.holdOrderAndRaiseEvent = true;
			//outStruct.holdReason = "VSI_PAYMENT_HOLD";
			Document docApplyPaymentHold = SCXmlUtil.createDocument("Order");
			Element eleApplyPaymentHold = docApplyPaymentHold.getDocumentElement();
			eleApplyPaymentHold.setAttribute("Action", "MODIFY");
			eleApplyPaymentHold.setAttribute("OrderHeaderKey", orderHeaderKeyStr);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(eleApplyPaymentHold, "OrderHoldTypes");
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, "OrderHoldType");
			eleOrderHoldType.setAttribute("HoldType", "VSI_PAYMENT_HOLD");
			eleOrderHoldType.setAttribute("ReasonText", "VSI_PAYMENT_HOLD");
			eleOrderHoldType.setAttribute("Status", "1100");
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", docApplyPaymentHold); 
			}
			else{
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_AUTH_DECLINE_RET");
				 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				//String  retryMinutes="10";
				 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				//calendar.add(Calendar.DATE, 7);	
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
				//outStruct.collectionDate=sdf.parse(collectionDate);
				outStruct.retryFlag="Y";
				//retryCount++;
				//outStruct.PaymentReference3=Integer.toString(retryCount);
				modifyRetryCount(env,orderHeaderKeyStr,strChargeTrasactionKey,retryCount); 
				
			} **/
  			//outStruct.authorizationId = inStruct.authorizationId;

          }
		  
		  }
		  else {
			  outStruct.authorizationId = inStruct.authorizationId;
		  

			outStruct.authorizationAmount = inStruct.requestAmount;
			//outStruct.collectionDate = new Date();
			//outStruct.executionDate = new Date();
			outStruct.retryFlag = "N";

			getAuthRecordsforCharge(env, orderHeaderKeyStr, storeNo,
					authorizationIdStr, tokenValue, requestAmount,
					transactionType, chargeTransactionKeyStr, IsSettled);

			String authCodeStr = getAuthcodeforCharge(env, orderHeaderKeyStr,
					authorizationIdStr);
			if (authCodeStr != null) {
				outStruct.authCode = authCodeStr;
			}
			//OMS-1526
			//createAuthRequest(env, chargeTransactionKeyStr, orderHeaderKeyStr);

		}
		}

		catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();

		}
	}

	/**********************************************************************************
	 * Method Name : doRefund
	 * 
	 * Description : this method will call tns for reversal of an authorization 
	 * 
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/09/2014 We are doing a dummy refund and updating vsi_ajb_charge_settlement
	 *  table
	 **********************************************************************************/
	void doRefund(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {

		try {

			String orderHeaderKeyStr = inStruct.orderHeaderKey;
			//OMS-1635:Start
			String storeNo=null;
			String strIsNewOrder = checkNewOrder(env, orderHeaderKeyStr);
			String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,"DEFAULT","DefaultMerchPrefixActive");
			if ( !YFCCommon.isVoid(strDefaultPrefixReqd)  && "Y".equals(strDefaultPrefixReqd) && !YFCCommon.isVoid(strIsNewOrder) && "Y".equals(strIsNewOrder))
			{
				//Defaulting merchantId as 06101
				storeNo = "06101";
			}
			else
			{	
			// getting the merchant id
				storeNo = getShipNode(env, orderHeaderKeyStr);	
			}
			if(log.isDebugEnabled()){
				log.debug("storeNo is" + storeNo);
			}
			//String storeNo = getShipNode(env, orderHeaderKeyStr);
			//OMS-1635:End
			String chargeTransactionKeyStr = inStruct.chargeTransactionKey;
			String transactionType = "Refund";
			String IsSettled = "N";
			String requestAmount = Double.toString(-inStruct.requestAmount);
			String tokenValue = inStruct.paymentReference2;
			outStruct.authorizationId = inStruct.authorizationId;
			outStruct.authorizationAmount = inStruct.requestAmount;
			String authorizationIdStr = inStruct.authorizationId;
			String  docType = inStruct.documentType;
			//outStruct.collectionDate = new Date();
			//outStruct.executionDate = new Date();
			outStruct.retryFlag = "N";
			//Start: Added during Payment Changes added  04-27-2017 
			//call getAuthRecordforCharge using DerivedFromOrderHeaderKey 
			if(!YFCObject.isVoid(docType) && docType.equalsIgnoreCase(VSIConstants.RETURN_DOCUMENT_TYPE)){
				Document getOrderListIP = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIP.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
				
				Document getOrderListOP = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_DERIVED_FROM_OHK,VSIConstants.API_GET_ORDER_LIST, getOrderListIP);
				Element eleOrder = SCXmlUtil.getChildElement(getOrderListOP.getDocumentElement(), VSIConstants.ELE_ORDER);
				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
				Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
				if(eleOrderLines.hasChildNodes()){
					String derivedOrderHeaderKeyStr = eleOrderLine.getAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY);
						getAuthRecordsforCharge(env, derivedOrderHeaderKeyStr, storeNo,
							authorizationIdStr, tokenValue, requestAmount,
							transactionType, chargeTransactionKeyStr, IsSettled);
				}//nullcheck for OrderLines
			}//call getAuthRecordsforCharge for return orders
			//End: Added during Payment Changes added  04-27-2017 
			else{
				getAuthRecordsforCharge(env, orderHeaderKeyStr, storeNo,
					authorizationIdStr, tokenValue, requestAmount,
					transactionType, chargeTransactionKeyStr, IsSettled);
			}
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}
	}

	public void getAuthRecordsforCharge(YFSEnvironment env,
			String orderHeaderKeyStr, String storeNo, String transactionId,
			String tokenValue, String requestAmount, String transactionType,
			String chargeTransactionKeyStr, String IsSettled) throws Exception {

		// getting authorized records from TNSAuthRecords

		Document getTNSAuthRecords = XMLUtil.createDocument("TNSAuthRecords");
		Element eleOrder = getTNSAuthRecords.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKeyStr);
		eleOrder.setAttribute("TransactionID", transactionId);

		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env,
				VSIConstants.SERVICE_GET_TNS_AUTH, getTNSAuthRecords);
		Element orderLineElem = (Element) outDoc.getElementsByTagName(
				"TNSAuthRecords").item(0);
		String transactionTerminal = orderLineElem
				.getAttribute("TransactionTerminal");
		String transactionReceipt = orderLineElem
				.getAttribute("TransactionReceipt");
		String transactionSource = orderLineElem
				.getAttribute("TransactionSource");
		String transactionAuth = orderLineElem
				.getAttribute("TransactionAuthCode");
		String transactonAqDate = orderLineElem
				.getAttribute("TransactionAqDate");
		String transactionAqTime = orderLineElem
				.getAttribute("TransactionAqTime");
		String transactionAqCode = orderLineElem
				.getAttribute("TransactionAqCode");
		String AvsResp = orderLineElem.getAttribute("AvsResp");
		String expDate = orderLineElem
				.getAttribute("ExpiryDate");
		// inserting into AJB Records

		ajbChargeSettlementRecords(env, orderHeaderKeyStr, storeNo, tokenValue,
				requestAmount, transactionType, chargeTransactionKeyStr,
				IsSettled, transactionTerminal, transactionReceipt,
				transactionSource, transactionAuth, transactonAqDate,
				transactionAqTime, transactionAqCode,expDate,AvsResp);

	}

	public String getAuthcodeforCharge(YFSEnvironment env,
			String orderHeaderKeyStr, String transactionId) throws Exception {

		// getting authorized records from TNSAuthRecords

		Document getTNSAuthRecords = XMLUtil.createDocument("TNSAuthRecords");
		Element eleOrder = getTNSAuthRecords.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKeyStr);
		eleOrder.setAttribute("TransactionID", transactionId);

		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env,
				VSIConstants.SERVICE_GET_TNS_AUTH, getTNSAuthRecords);
		Element orderLineElem = (Element) outDoc.getElementsByTagName(
				"TNSAuthRecords").item(0);

		String transactionAuth = orderLineElem
				.getAttribute("TransactionAuthCode");
		return transactionAuth;

	}

	/**********************************************************************************
	 * Method Name : generateOrderId
	 * 
	 * Description : This method segregate the paymen treference key coming from
	 * ATG and then will generate OrderId for OMS
	 * 
	 * 
	 * Modification Log :
	 * -----------------------------------------------------------------------
	 * Ver # Date Author Modification
	 * -----------------------------------------------------------------------
	 * 0.00a 10/09/2014 This method segregate the payment reference key coming
	 * from ATG and then will generate OrderId for OMS
	 **********************************************************************************/
	public String generateOrderId(String paymentReference1,
			String chargeTransactionKey) {

		String sSystemId = "2";

		String sJDAStoreId = paymentReference1.substring(0, 4);
		String sNumericOrderId = paymentReference1.substring(5, 14);
		Date currentDate = new Date();
		String sModifiedDate = new SimpleDateFormat("yyMMddHHmmss")
				.format(currentDate);
		String sLastFiveTranKeyNum = chargeTransactionKey
				.substring(chargeTransactionKey.length() - 5);
		String sOmsOrderId = sJDAStoreId + sSystemId + sNumericOrderId
				+ sModifiedDate + sLastFiveTranKeyNum;

		return sOmsOrderId;
	}

	public String generateId(YFSEnvironment env,
			YFSExtnPaymentCollectionInputStruct inStruct,
			String orderHeaderKeyStr) throws Exception {
		Document getOrderListDoc = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_DETAILS,
				"global/template/api/VSISequence.xml");
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_DETAILS,
				getOrderListDoc);
		Element orderLineElem = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_EXTN).item(0);
		String tranSequence = orderLineElem
				.getAttribute(VSIConstants.ATTR_SEQUENCE);
		return tranSequence;
	}
	private boolean isDraftOrder = false;
	private boolean isCallCenterTran = false;
	public String getShipNode(YFSEnvironment env, String orderHeaderKeyStr)
			throws Exception {
			String entCode = "DEFAULT";
		Document getOrderListDoc = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_DETAILS,
				"global/template/api/VSIOrderCreateCC.xml");
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_DETAILS,
				getOrderListDoc);
		String draftOrderFlag = outDoc.getDocumentElement().getAttribute("DraftOrderFlag");
		String orderType = outDoc.getDocumentElement().getAttribute("OrderType");
		if("Y".equals(draftOrderFlag))
		{
			isDraftOrder = true;
		}
		env.clearApiTemplates();
		Element eleOrderOut = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);
		Element orderLineElem = null;
		if(!YFCObject.isVoid(eleOrderOut)){
		 orderLineElem = (Element) eleOrderOut.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		}
		
		orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		String deliveryMethod = orderLineElem.getAttribute("DeliveryMethod");
		String shipNodestr = "";
		//String sShipNode = null;
		if(!YFCObject.isVoid(deliveryMethod) && "PICK".equals(deliveryMethod))
		{
			shipNodestr = orderLineElem.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		}
		else
		{
			shipNodestr = outDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERED_BY);			
		}
		
		if("WEB".equals(orderType) && (!YFCObject.isVoid(deliveryMethod) && "SHP".equals(deliveryMethod)))
		{
			isCallCenterTran = true;
		}
		
		/*if(YFSObject.isNull(shipNodestr) || YFSObject.isVoid(shipNodestr) || shipNodestr.equals("") || shipNodestr.equals(" ")){
			shipNodestr = "9803";
		}*/
		
		String merchPrefix = getCommonCodeLongDescriptionByCodeValue(env, entCode, "MerchPrefixDigit");
		String shipNodePadded = (merchPrefix + shipNodestr).substring(shipNodestr
				.length());
		return shipNodePadded;

		// String merchantIdStr=getMerchantId(env, shipNodestr);
		// return merchantIdStr;
	}
	
	public String getShipNodeCharge(YFSEnvironment env, String orderHeaderKeyStr)
			throws Exception {
		Document getOrderListDoc = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				orderHeaderKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_DETAILS,
				"global/template/api/VSIOrderCreateCC.xml");
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_DETAILS,
				getOrderListDoc);
		env.clearApiTemplates();
		Element eleOrderOut = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);
		Element orderLineElem = null;
		if(YFCObject.isVoid(eleOrderOut))
		 orderLineElem = (Element) eleOrderOut.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		
		String shipNodestr = "7001";
		if(!YFCObject.isVoid(orderLineElem))
		 shipNodestr = orderLineElem
				.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		
		if(shipNodestr.equals("9001")){
			shipNodestr = "7001";
		}
		
		if(YFSObject.isNull(shipNodestr) || YFSObject.isVoid(shipNodestr) || shipNodestr.equals("") || shipNodestr.equals(" ")){
			shipNodestr = "9803";
		}
		String shipNodePadded = ("00000" + shipNodestr).substring(shipNodestr
				.length());
		return shipNodePadded;

		// String merchantIdStr=getMerchantId(env, shipNodestr);
		// return merchantIdStr;
	}

	/**
	 * public String getMerchantId(YFSEnvironment env, String shipNodestr)
	 * throws Exception { Document getOrderListDoc =
	 * XMLUtil.createDocument("Organization"); Element eleOrder =
	 * getOrderListDoc.getDocumentElement();
	 * eleOrder.setAttribute(VSIConstants.ATTR_ORG_CODE, shipNodestr); api =
	 * YIFClientFactory.getInstance().getApi(); Document outDoc =
	 * api.invoke(env, VSIConstants.API_GET_ORGANIZATION_LIST,getOrderListDoc);
	 * Element orderLineElem = (Element)
	 * outDoc.getElementsByTagName(VSIConstants.ELE_ORGANIZATION).item(0);
	 * String merchantIdStr =
	 * orderLineElem.getAttribute(VSIConstants.ATTR_TAX_PAYER_ID);
	 * //System.out.println("merchant id is " + merchantIdStr);
	 * 
	 * return merchantIdStr; }
	 **/

	public void createAuthRequest(YFSEnvironment env,
			String chargeTransactionkey, String orderHeaderKeyStr)
			throws Exception {
		Document getChargeTranListDoc = XMLUtil
				.createDocument("ChargeTransactionDetail");
		Element eleOrder = getChargeTranListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_CHARGE_TRANSACTION_KEY,
				chargeTransactionkey);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env,
				VSIConstants.API_GET_CHARGE_TRANSACTION_LIST,
				getChargeTranListDoc);
		Element chargeTranElem = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_CHARGE_TRANSACTION_DETAILS).item(0);
		String openAuthAmtStr = chargeTranElem
				.getAttribute(VSIConstants.ATTR_OPEN_AUTHORIZED_AMOUNT);
		String requestAmtStr = chargeTranElem
				.getAttribute(VSIConstants.ATTR_REQUEST_AMOUNT);

		double requestAmt = Double.parseDouble(requestAmtStr);
		double openauthAmt = -Double.parseDouble(openAuthAmtStr);

		double diffAmount;
		if (openauthAmt > requestAmt) {
			diffAmount = openauthAmt - requestAmt;

			String maxReqAmount = Double.toString(diffAmount);
			Document createCTR = XMLUtil
					.createDocument("ChargeTransactionRequestList");
			Element eleCTR = createCTR.getDocumentElement();
			eleCTR.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
					orderHeaderKeyStr);

			Element eleCTRReq = XMLUtil.createElement(createCTR,
					"ChargeTransactionRequest", "");
			eleCTR.appendChild(eleCTRReq);
String ctrSeqId = VSIDBUtil.getNextSequence(env,VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
			// Element eleCTRReq = XMLUtil.appendChild(createCTR, eleCTR,
			// VSIConstants.ELE_CHARGE_TRANSACTION_REQ, "12");
			eleCTRReq.setAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_KEY,
					chargeTransactionkey);
			eleCTRReq.setAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_ID,
					ChargeTransactionRequestId);
			eleCTRReq.setAttribute(VSIConstants.ATTR_MAX_REQ_AMT, maxReqAmount);

			api = YIFClientFactory.getInstance().getApi();
			Document ctrDoc = api.invoke(env,
					VSIConstants.API_MANAGE_CHARGE_TRAN_REQ, createCTR);

		}

	}

	public String TNStokenization(YFSEnvironment env, String creditCardNo,
			String expiryMonth, String expiryYear, String securityCode,
			String sourceType) throws Exception {
		HashMap nvp = VSICreditCardFunction.DoToken(creditCardNo, expiryMonth,
				expiryYear, securityCode, sourceType, env);
		String strAck = nvp.get("RESULT").toString();
		String StrToken = null;

		if ("Success".equalsIgnoreCase(strAck)) {
			StrToken = nvp.get("TOKEN").toString();
		}
		return StrToken;

	}
      public String DoAuthorizeforCharge(YFSExtnPaymentCollectionInputStruct inStruct,YFSExtnPaymentCollectionOutputStruct outStruct,YFSEnvironment env)
     throws Exception{
	  String newAuthId=null;
	  String transactionIdStr = null;
		String Orderid = null;
		String merchantId = null;
		String generateId = null;
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String entCode = "DEFAULT";
		//OMS-1635 : Start
		String strIsNewOrder = checkNewOrder(env, strOrderHeaderKey);
		String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,entCode,"DefaultMerchPrefixActive");
		String merchantPrefix = getCommonCodeLongDescriptionByCodeValue(env,entCode,"MerchPrefix");
		if ( !YFCCommon.isVoid(strDefaultPrefixReqd)  && "Y".equals(strDefaultPrefixReqd) && !YFCCommon.isVoid(strIsNewOrder) && "Y".equals(strIsNewOrder))
		{
			//Defaulting merchantId as 06101
			merchantId = merchantPrefix+"06101";
		}
		else
		{	
		// getting the merchant id
			String storeId = getShipNode(env, strOrderHeaderKey);
			merchantId = merchantPrefix + storeId;
		}
		if(log.isDebugEnabled()){
			log.debug("merchantId is" + merchantId);
		}
		//OMS-1635 : End
		DecimalFormat df1 = new DecimalFormat("#,###,###.00");
		String currencyCodestr = "USD";
		//double dRequestAmount = inStruct.requestAmount;
		//String requestAmountStr = df1.format(dRequestAmount);
		//Fix for order with greater than 1000 getting declined auth due to format of the amount.
			
			//double dRequestAmount = inStruct.requestAmount;
			//String requestAmountStr = df1.format(dRequestAmount);
			
		String requestAmountStr=Double.toString(inStruct.requestAmount);  
		
		String sourceOfFundstype = "CARD";
		String tokenIdStr = inStruct.paymentReference2;
		String firstName = inStruct.firstName;
		String lastName = inStruct.lastName;
		String expDate = inStruct.creditCardExpirationDate;
		String AdressLine1=inStruct.billToAddressLine1;
		String City=inStruct.billToCity;
		String ZipCode=inStruct.billToZipCode;
		String State=inStruct.billToState;
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!		
String KountToken="12321321321";
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		//Jira 802
		String country=inStruct.billToCountry;
		if(country.length() == 2) {
			country = VSIUtils.getCountryCode(env, country);
		}
		//Jira 802
		
		String transactionId=null;
				
		
		//System.out.println("ExpDate is " + expDate);

		String expMonth = expDate.substring(0, 2);
		//System.out.println("Exp mnth is " + expMonth);

		String expYr = expDate.substring(expDate.length() - 2);
		//System.out.println("Exp yr is " + expYr);

		String concExpDt = expMonth + expYr;
		//System.out.println("new Exp dt is " + concExpDt);

		if (inStruct.paymentReference1 != null) {
			// generateId=generateId(env,inStruct,strOrderHeaderKey);
			String atgTranId = inStruct.paymentReference1;
			String sChargeTransactionKey = inStruct.chargeTransactionKey;
			String sGeneratedOrderId = generateOrderId(atgTranId,
					sChargeTransactionKey);
			// String atgTranSubStr=atgTranId.substring(0,
			// atgTranId.length()-3);
			// omsTranId=atgTranSubStr+generateId;
			Orderid = sGeneratedOrderId;
			transactionIdStr = sGeneratedOrderId;

		} else {
  	 	String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
			transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
			Orderid = inStruct.chargeTransactionKey+tranNumber;
		}

		/**
		 * HashMap nvp =
		 * VSICreditCardFunction.DoAuthorization(Orderid,merchantId
		 * ,apiUsername,apiPassword,requestAmountStr,
		 * currencyCodestr,transactionIdStr,sourceOfFundstype,tokenIdStr);
		 **/
		

				
      //making the call to TNS
//add,KountToken to method parameters once mapping is verified
		HashMap nvp = VSICreditCardFunction.DoAuthorization(Orderid,
				merchantId, requestAmountStr, currencyCodestr,
				transactionIdStr, sourceOfFundstype, tokenIdStr, firstName,
				lastName, expYr, expMonth,AdressLine1,City,ZipCode,State,country, env,strOrderHeaderKey);
		//Printing the Response
		 Document doc = XMLUtil.createDocument("PaymentRecords");
  		Element elePayRecrds = doc.getDocumentElement();
  		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
  				strOrderHeaderKey);
  		elePayRecrds.setAttribute("Record", nvp.toString());
  		elePayRecrds.setAttribute("ChargeType", "Authorize Response");
  		YIFApi api;
  		api = YIFClientFactory.getInstance().getApi();
  		api.executeFlow(env,"VSIPaymentRecords", doc);
		
		
      //pasrsing the rsponse
		if (nvp.containsKey("RESULT")) {
			if(log.isDebugEnabled()){
				log.debug("TNS Response:"+nvp.toString());
			}
			if ("Success".equalsIgnoreCase(nvp.get("RESULT").toString())) {
				String AvsResp=null;
				String AvsRespCode=null;
				String strAck = nvp.get("RESULT").toString();
				//System.out.println("Autorization is" + strAck);
				 transactionId = nvp.get("TRANSACTION.ID").toString();
				requestAmountStr = nvp.get("TRANSACTION.AMOUNT").toString();
				String transactionTerminal = nvp
						.get("TRANSACTION.TERMINAL").toString();
				String transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
				String transactionSource = "";
				//OMS-1935 START
				if(isCallCenterTran)
				{
					ArrayList<Element> listTransactionSource;
					listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
					if(!listTransactionSource.isEmpty()){
						Element eleCommonCode=listTransactionSource.get(0);
						String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if(!YFCCommon.isStringVoid(strTSValue)){
							transactionSource = strTSValue;
						}
					}
				}
				else
				{
					ArrayList<Element> listTransactionSourceNonSTH;
					listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
					if(!listTransactionSourceNonSTH.isEmpty()){
						Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
						String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if(!YFCCommon.isStringVoid(strTSValue1)){
							transactionSource = strTSValue1;

						}
					}
				}
				//OMS-1935 END
				String transactionAuthCode = nvp.get(
						"TRANSACTION.AUTHORIZATIONCODE").toString();
				//String transactionReceipt = "110001";
				
				String transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
				String transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
				//oms-2046 start
				String strAVSgatway=null;
				 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
				 strAVSgatway = nvp.get(
						"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
				 }
				//oms-2046 end

				// String transactionAqDate =
				// nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
				String transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE")
						.toString();
				String authCode = nvp.get("TRANSACTION.AUTHORIZATIONCODE")
						.toString();
				
				if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE")){
					  AvsRespCode = nvp.get("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE").toString();
					 }
				AvsResp="AvsResp-"+AvsRespCode;

				double authAmountStr = Double.parseDouble(requestAmountStr);
			

				// Setting the expiration date to + 7
				Calendar calendar = Calendar.getInstance();
				Document getCommonCodeListInputForReAuthTime = getCommonCodeListInputForCodeType("DEFAULT","CC_AUTH_EXP_TIME");
				 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForReAuthTime);
				 Element eleCOmmonCodeOut = null;
				 String strReAuthMinutes = "";
				 if(!YFCObject.isVoid(getCommonCodeOut)){
					 eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
					 if(!YFCObject.isVoid(eleCOmmonCodeOut)){
						 strReAuthMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
					 }
				 }
				int authMinutes = 10080 ; //7 days default
				if(!YFCObject.isVoid(strReAuthMinutes))
					authMinutes = Integer.parseInt(strReAuthMinutes);
				calendar.add(Calendar.MINUTE,authMinutes);
				//calendar.add(Calendar.DATE, 7);
				String authExpDate = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(calendar.getTime());
				
				/**
				 * if(inStruct.paymentReference1!=null){ //incementing the
				 * sequence at order header int
				 * generateInt=Integer.parseInt(generateId); int
				 * tranSequenceInt=generateInt+VSIConstants.ATTR_SEQ;
				 * changeTranSequence
				 * (env,strOrderHeaderKey,tranSequenceInt); }
				 **/

				// Inserting records for authorization for charge
				tnsAuthRecords(env, strOrderHeaderKey, transactionId,
						transactionTerminal, transactionReceipt,
						transactionSource, transactionAuthCode,
						transactionAqTime, transactionAqDate,
						transactionAqCode,concExpDt,AvsResp,strAVSgatway);
			}
			
			//if authorization is failure
			else if ( "DECLINE".equalsIgnoreCase(nvp.get("RESULT")
							.toString()) ||  ( nvp.containsKey("RESPONSE.GATEWAYCODE") && "DECLINED".equalsIgnoreCase(nvp.get("RESPONSE.GATEWAYCODE").toString()))) {
				String errorCodeStr = "AuthFail";
				String errorMessage = "AuthFail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				// outStruct.suspendPayment="Y";
				String strAQRiskified = nvp.get("RESPONSE.ACQUIRERCODE").toString();
				String strRiskifiedFlag=VSIConstants.FLAG_N;
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct);
				// raising holds and cancelling orders
				authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag);
			}
			//OMS-1564: Raising alert when we are getting ERROR response due to network issue : START
			else if ("ERROR".equalsIgnoreCase(nvp.get("RESULT")
					.toString()))
			{
				String errorCodeStr = "AuthFail due to Error Response";
				String errorMessage = "AuthFail due to Error Response";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				

				applyPaymentErrorHold(env, strOrderHeaderKey);				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct);
			}
			//OMS-1564: Raising alert when we are getting ERROR response due to network issue : END
			else if ("FAILURE".equalsIgnoreCase(nvp.get("RESULT").toString())) {
				if("TIMED_OUT".equalsIgnoreCase(nvp.get("RESPONSE.GATEWAYCODE").toString())){
					Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
					 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
					Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
					//String  retryMinutes="10";
					 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

					Calendar calendar = Calendar.getInstance();
					int retMinutes = 10 ; //10 default
					retMinutes = Integer.parseInt(retryMinutes);
					calendar.add(Calendar.MINUTE,retMinutes);
					outStruct.collectionDate=calendar.getTime();
					//calendar.add(Calendar.DATE, 7);
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
					//outStruct.collectionDate=sdf.parse(collectionDate);
					outStruct.retryFlag="Y";
						
				}
				//OMS-1676 : Start
				else if("OFFLINE".equalsIgnoreCase(nvp.get("RESPONSE.ACQUIRERCODE").toString())){
					Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_OFFLINE");
					 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
					Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
					//String  retryMinutes="10";
					 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

					Calendar calendar = Calendar.getInstance();
					int retMinutes = 10 ; //10 default
					retMinutes = Integer.parseInt(retryMinutes);
					calendar.add(Calendar.MINUTE,retMinutes);
					outStruct.collectionDate=calendar.getTime();
					//calendar.add(Calendar.DATE, 7);
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
					//outStruct.collectionDate=sdf.parse(collectionDate);
					outStruct.retryFlag="Y";
					String errorCodeStr = "OfflineAuth";
					String errorMessage = "OfflineAuth";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					// outStruct.suspendPayment="Y";
					String failureTypeStr = "Credit Card Authorization Offile";

					// raising Credit card alerts
					raiseOfflineAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct,failureTypeStr);
						
				}
				//OMS-1676 : End
				else{
				 transactionId=null;
				String AvsResp=null;
				String AvsRespCode=null;
				String strPaymentRecordsTranId=null;
				String strAVSgatway=null;

				String transactionTerminal=null;
				String transactionReceipt=null;
				String transactionSource=null;
				String transactionAuthCode=null;
				String transactionAqTime=null;
				String transactionAqDate=null;
				String transactionAqCode=null;
				if(nvp.containsKey("TRANSACTION.ID")){
				 strPaymentRecordsTranId = nvp.get("TRANSACTION.ID").toString();
				 }
				if(nvp.containsKey("TRANSACTION.TERMINAL")){
				 transactionTerminal = nvp.get("TRANSACTION.TERMINAL").toString();
				}
				if(nvp.containsKey("TRANSACTION.RECEIPT")){
				 transactionReceipt = nvp.get("TRANSACTION.RECEIPT").toString();
				}
				//OMS-1935 START
				if(isCallCenterTran)
				{
					ArrayList<Element> listTransactionSource;
					listTransactionSource = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_STH", "SOURCE", "DEFAULT");
					if(!listTransactionSource.isEmpty()){
						Element eleCommonCode=listTransactionSource.get(0);
						String strTSValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if(!YFCCommon.isStringVoid(strTSValue)){
							transactionSource = strTSValue;
						}
					}
				}
				else
				{
					ArrayList<Element> listTransactionSourceNonSTH;
					listTransactionSourceNonSTH = VSIUtils.getCommonCodeList(env, "TRANS_SOURCE_NONSTH", "SOURCE", "DEFAULT");
					if(!listTransactionSourceNonSTH.isEmpty()){
						Element eleCommonCodenonSTH=listTransactionSourceNonSTH.get(0);
						String strTSValue1=eleCommonCodenonSTH.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if(!YFCCommon.isStringVoid(strTSValue1)){
							transactionSource = strTSValue1;

						}
					}
				}
				//OMS-1935 END
				 if(nvp.containsKey("TRANSACTION.AUTHORIZATIONCODE")){
				 transactionAuthCode = nvp.get("TRANSACTION.AUTHORIZATIONCODE").toString();
				 }
				 if(nvp.containsKey("TRANSACTION.ACQUIRER.TIME")){
				 transactionAqTime = nvp.get("TRANSACTION.ACQUIRER.TIME").toString();
				 }
				 if(nvp.containsKey("TRANSACTION.ACQUIRER.DATE")){
				 transactionAqDate = nvp.get("TRANSACTION.ACQUIRER.DATE").toString();
				 }
				 if(nvp.containsKey("RESPONSE.ACQUIRERCODE")){
				 transactionAqCode = nvp.get("RESPONSE.ACQUIRERCODE").toString();
				 }
				 if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE")){
					  AvsRespCode = nvp.get("RESPONSE.CARDHOLDERVERIFICATION.AVS.ACQUIRERCODE").toString();
					 }
				AvsResp="AvsResp-"+AvsRespCode;

				 
				String expDate1 = inStruct.creditCardExpirationDate;
				//System.out.println("ExpDate is " + expDate);

				String expMonth1 = expDate.substring(0, 2);
				//System.out.println("Exp mnth is " + expMonth);

				String expYr1 = expDate.substring(expDate.length() - 2);
				//System.out.println("Exp yr is " + expYr);

				String concExpDt1 = expMonth + expYr;
				if(nvp.containsKey("RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE")){
					 strAVSgatway = nvp.get(
							"RESPONSE.CARDHOLDERVERIFICATION.AVS.GATEWAYCODE").toString();
					 }
				
				tnsAuthRecords(env, strOrderHeaderKey, strPaymentRecordsTranId,
						transactionTerminal, transactionReceipt,
						transactionSource, transactionAuthCode,
						transactionAqTime, transactionAqDate,
						transactionAqCode,concExpDt1,AvsResp,strAVSgatway);
				
				String errorCodeStr = "AuthFail";
				String errorMessage = "AuthFail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				// outStruct.suspendPayment="Y";

				applyPaymentErrorHold(env, strOrderHeaderKey);
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct);
				// raising holds and cancelling orders
				//OMS-1564:Start
				//authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct);
				//OMS-1564:End
				}
			}
		}


		
		
		else if (null != nvp.get("errorCode").toString())

		{
			//for time out exception, setting the flag='Y', so that agent can retry the request
			if ("0x01130006".equalsIgnoreCase(nvp.get("errorCode")
					.toString())) {
				String errorCodeStr = "TNS Connection Fail";
				String errorMessage = "TNS Connection Fail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				outStruct.retryFlag = "Y";
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct);

			}

		}

	
	  return transactionId;
	  
	  
	  
      }
	public void ajbChargeSettlementRecords(YFSEnvironment env,
			String orderHeaderKeyStr, String storeNo, String tokenValue,
			String requestAmount, String tranactionType,
			String chargeTransactionKeyStr, String IsSettled,
			String transactionTerminal, String transactionReceipt,
			String transactionSource, String transactionAuthCode,
			String transactionAqDate, String transactionAqTime, 
			String transactionAqCode,String expDate,String AvsResp)

	throws Exception {
		Document doc = XMLUtil.createDocument("AJBSettlement");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute("OrderHeaderKey", orderHeaderKeyStr);
		eleOrder.setAttribute("StoreNumber", storeNo);
		eleOrder.setAttribute("TerminalName", transactionTerminal);
		eleOrder.setAttribute("TransactionNumber", transactionReceipt);
		eleOrder.setAttribute("TransactionSource", transactionSource);
		eleOrder.setAttribute("AuthorizationNumber", transactionAuthCode);
		eleOrder.setAttribute("TransactionAqTime", transactionAqTime);
		eleOrder.setAttribute("TransactionAqDate", transactionAqDate);
		eleOrder.setAttribute("TransactionAqCode", transactionAqCode);
		eleOrder.setAttribute("TokenNumber", tokenValue);
		eleOrder.setAttribute("TransactionAmount", requestAmount);
		eleOrder.setAttribute("TransactionType", tranactionType);
		eleOrder.setAttribute("IsSettled", IsSettled);
		eleOrder.setAttribute("ChargeTransactionKey", chargeTransactionKeyStr);
		eleOrder.setAttribute("ExpiryDate", expDate);
		eleOrder.setAttribute("AvsResp", AvsResp);


		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env,
				VSIConstants.SERVICE_CREATE_AJB_SETTLEMENT, doc);

	}

	public void tnsAuthRecords(YFSEnvironment env, String strOrderHeaderKey,
			String transactionIdStr, String transactionTerminal,
			String transactionReceipt, String transactionSource,
			String transactionAuthCode, String transactionAqTime,
			String transactionAqDate, String transactionAqCode, String expDate,String AvsResp, String strAVSgatway)
			throws Exception {

		Document doc = XMLUtil.createDocument("TNSAuthRecords");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				strOrderHeaderKey);
		eleOrder.setAttribute("TransactionID", transactionIdStr);
		eleOrder.setAttribute("TransactionTerminal", transactionTerminal);
		eleOrder.setAttribute("TransactionReceipt", transactionReceipt);
		eleOrder.setAttribute("TransactionSource", transactionSource);
		eleOrder.setAttribute("TransactionAuthCode", transactionAuthCode);
		eleOrder.setAttribute("TransactionAqTime", transactionAqTime);
		eleOrder.setAttribute("TransactionAqDate", transactionAqDate);
		eleOrder.setAttribute("TransactionAqCode", transactionAqCode);
		eleOrder.setAttribute("ExpiryDate", expDate);
		eleOrder.setAttribute("AvsResp", AvsResp);
		eleOrder.setAttribute("AvsGatewayCode", strAVSgatway);


		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env,
				VSIConstants.SERVICE_CREATE_TNS_AUTH, doc);

	}

	/**
	 * public void ajbRefundSettlemeentRecords(YFSEnvironment env,String
	 * orderHeaderKeyStr,String storeNo,String currencyCodestr ,String
	 * authorizationIdStr, String transactionAmount, String messageType,String
	 * ActionCode,String Orderid, String chargeTransactionKeyStr,String
	 * registerNo,String tranactionType, String transactionNo,String
	 * transactionDate,String IsSettled) throws Exception { Document doc =
	 * XMLUtil.createDocument("AJBSettlement"); Element eleOrder =
	 * doc.getDocumentElement();
	 * eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
	 * orderHeaderKeyStr); eleOrder.setAttribute(VSIConstants.STORE_NO,
	 * storeNo); eleOrder.setAttribute(VSIConstants.CURRENCY_CODE,
	 * currencyCodestr);
	 * eleOrder.setAttribute(VSIConstants.AUTHORIZATION_NUMBER,
	 * authorizationIdStr);
	 * eleOrder.setAttribute(VSIConstants.TRANSACTION_AMOUNT,
	 * transactionAmount); eleOrder.setAttribute(VSIConstants.ACTION_CODE,
	 * ActionCode); eleOrder.setAttribute(VSIConstants.MESSAGE_TYPE,
	 * messageType); eleOrder.setAttribute(VSIConstants.TNS_ORDER_Id, Orderid);
	 * eleOrder.setAttribute(VSIConstants.CHARGE_TRAN_KEY,
	 * chargeTransactionKeyStr);
	 * eleOrder.setAttribute(VSIConstants.REGISTER_NUMBER, registerNo);
	 * eleOrder.setAttribute(VSIConstants.TRANSACTION_TYPE, tranactionType);
	 * eleOrder.setAttribute(VSIConstants.TRANSACTION_NUMBER, transactionNo);
	 * eleOrder.setAttribute(VSIConstants.TRANSACTION_DATE, transactionDate);
	 * eleOrder.setAttribute(VSIConstants.IS_SETTLED, IsSettled); api =
	 * YIFClientFactory.getInstance().getApi(); Document outDoc =
	 * api.executeFlow(env, VSIConstants.SERVICE_CREATE_AJB_SETTLEMENT,doc);
	 * 
	 * }
	 **/

	public void creditCardAlert(YFSEnvironment env, String errorCodeStr,
			String errorMessageStr, String sOrderNo, String sOHK,
			YFSExtnPaymentCollectionInputStruct inStruct)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {

		if ("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)) {
			String failureTypeStr = "Credit Card Authorization Failure";
			raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
					inStruct, failureTypeStr);
		} else if ("CHARGE".equalsIgnoreCase(inStruct.chargeType)) {

			if (inStruct.requestAmount >= 0.0D) {
				String failureTypeStr = "Credit Card Charge Failure";
				raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
						inStruct, failureTypeStr);

			} else {
				String failureTypeStr = "Credit Card Refund Failure";
				raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
						inStruct, failureTypeStr);

			}
		}
	}

	public void raiseAlert(YFSEnvironment env1, String errorCodeSt,
			String errorMessageSt, String sOrderNoSt, String sOHKSt,
			YFSExtnPaymentCollectionInputStruct inStructSt,
			String failureTypeStr) throws ParserConfigurationException,
			YIFClientCreationException, YFSException, RemoteException {

		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHKSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNoSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement
				.setAttribute("DetailDescription", errorMessageSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, errorCodeSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Credit Card Alert");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				failureTypeStr);
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_CREDITCARD_ALERT");

		Element InboxReferencesListElement = createExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHKSt);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env1, VSIConstants.API_CREATE_EXCEPTION, createExInput);

	}
	
	public void raiseOfflineAlert(YFSEnvironment env1, String errorCodeSt,
			String errorMessageSt, String sOrderNoSt, String sOHKSt,
			YFSExtnPaymentCollectionInputStruct inStructSt,
			String failureTypeStr) throws ParserConfigurationException,
			YIFClientCreationException, YFSException, RemoteException {

		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHKSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNoSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement
				.setAttribute("DetailDescription", errorMessageSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, errorCodeSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Credit Card Alert");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				failureTypeStr);
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_TNS_OFFLINE");
		/*OMS-1734-Alert consolidation for VSI_TNS_OFFLINE alert : Start*/
		InboxElement.setAttribute(VSIConstants.ATTR_CONSOLIDATE,VSIConstants.FLAG_Y);
		InboxElement.setAttribute(VSIConstants.ATTR_CONS_WINDOW,VSIConstants.VAL_FOREVER);
		Element eleConsolidationTemplate = createExInput.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
		InboxElement.appendChild(eleConsolidationTemplate);
		Element eleInboxCpy = (Element) InboxElement.cloneNode(true);
		eleConsolidationTemplate.appendChild(eleInboxCpy);
		/*OMS-1734 -End*/
		Element InboxReferencesListElement = createExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHKSt);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env1, VSIConstants.API_CREATE_EXCEPTION, createExInput);

	}
	
	
	/**public void raiseVoidAlert(YFSEnvironment env1, String errorCodeSt,
			String errorMessageSt, String sOrderNoSt, String sOHKSt,
			YFSExtnPaymentCollectionInputStruct inStructSt,
			String failureTypeStr) throws ParserConfigurationException,
			YIFClientCreationException, YFSException, RemoteException {

		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHKSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNoSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement
				.setAttribute(VSIConstants.ATTR_DESCRIPTION, errorMessageSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, errorCodeSt);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Credit Card Alert");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				failureTypeStr);
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_VOID_FAIL");

		Element InboxReferencesListElement = createExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHKSt);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env1, VSIConstants.API_CREATE_EXCEPTION, createExInput);

	} 
	 * @param strRiskifiedFlag 
	 * @param strAQRiskified **/

	private void authFailureCancel(YFSEnvironment env, String sOHK,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSExtnPaymentCollectionInputStruct inStruct, String strAQRiskified, String strRiskifiedFlag)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {
		//OMS-1760: PAYMENT_HOLD should not be applied on Draft Orders :Start
				boolean isDraftOrd=false;
				Document getOrderListDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleOrder = getOrderListDoc.getDocumentElement();
				eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,sOHK);
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
						"global/template/api/VSIGetOrderListCC.xml");
				Document outDoc1 = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListDoc);
				Element eleOrd = (Element) outDoc1.getElementsByTagName(
						VSIConstants.ELE_ORDER).item(0);
				String draftOrderFlag = eleOrd.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
				if(VSIConstants.FLAG_Y.equals(draftOrderFlag))
				{
					isDraftOrd = true;
					//OMS-2046 START
					ArrayList<Element> listCheckoutFlag;
					try {
						listCheckoutFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_CHECKOUT, VSIConstants.ATTR_DEFAULT);

						if(!listCheckoutFlag.isEmpty()){
							Element eleCommonCode=listCheckoutFlag.get(0);
							String strCheckoutFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strCheckoutFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strCheckoutFlag)&&!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
								String strOrderHeaderKey = eleOrd.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
								Document docInputOrder = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
								Element sOrderElement = docInputOrder.getDocumentElement();
								sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
								if(!YFCObject.isVoid(strAQRiskified)){
								sOrderElement.setAttribute(VSIConstants.ATTR_AQR_CODE, strAQRiskified);
								}
								api = YIFClientFactory.getInstance().getApi();
								api.executeFlow(env, "VSICheckOutDeniedToRiskified_Q", docInputOrder);
							}
						}
					}
					catch (Exception e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//OMS-2046 END
				}


				

				env.clearApiTemplates();
		//OMS-1760: End
		Document orderLine = XMLUtil.createDocument("OrderLine");
		Element OrderLineEle = orderLine.getDocumentElement();
		//OMS-1073 - Modified for filtering OrderLines from Sent To Warehouse to Shipped Status - START
		//OrderLineEle.setAttribute(VSIConstants.ATTR_STATUS, "3700");
		OrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		OrderLineEle.setAttribute(VSIConstants.ATTR_STATUS_QRY_TYPE, VSIConstants.BETWEEN_QUERY);
		OrderLineEle.setAttribute(VSIConstants.ATTR_FROM_STATUS, VSIConstants.STATUS_CODE_SENT_TO_NODE);
		OrderLineEle.setAttribute(VSIConstants.ATTR_TO_STATUS, "3700");
		//OMS-1073 - END

		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,
				orderLine);

		NodeList orderLineList = outDoc
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int orderLineCount = orderLineList.getLength();
		
	
	/*	String strPaymentRef3=inStruct.paymentReference3;
		int retryCount=0;
         if(!YFCCommon.isVoid(strPaymentRef3)){
        	 retryCount=Integer.parseInt(strPaymentRef3);
         } */
		if (orderLineCount > 0) {
			//OMS-1913: Start
			cancelOpenOrderLines(env, sOHK);
			//OMS-1913: End
//OMS870-Applying Payment Hold Manually-Start
/*<Order Action="MODIFY" OrderHeaderKey="">
	<OrderHoldTypes><OrderHoldType HoldType="Required" ReasonText="" Status=""/></OrderHoldTypes>
</Order>
*/
	//checking if the retry is done for more than 3 times
			int retryCount=1;
			String strChargeTrasactionKey=inStruct.chargeTransactionKey;
			//getting the retry count from custom table
			retryCount=getRetryCount(env,sOHK,strChargeTrasactionKey);
			if (retryCount>3){
			outStruct.retryFlag = "N";
			//outStruct.holdOrderAndRaiseEvent = true;
			//outStruct.holdReason = "VSI_PAYMENT_HOLD";
			Document docApplyPaymentHold = SCXmlUtil.createDocument("Order");
			Element eleApplyPaymentHold = docApplyPaymentHold.getDocumentElement();
			eleApplyPaymentHold.setAttribute("Action", "MODIFY");
			eleApplyPaymentHold.setAttribute("OrderHeaderKey", sOHK);
			eleApplyPaymentHold.setAttribute("Override", "Y");
			Element eleOrderHoldTypes = SCXmlUtil.createChild(eleApplyPaymentHold, "OrderHoldTypes");
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, "OrderHoldType");
			eleOrderHoldType.setAttribute("HoldType", "VSI_PAYMENT_HOLD");
			eleOrderHoldType.setAttribute("ReasonText", "VSI_PAYMENT_HOLD");
			eleOrderHoldType.setAttribute("Status", "1100");
			//OMS-1760: Start
			if(!isDraftOrd)
			{
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", docApplyPaymentHold);
			}
			//OMS-1760: End
			Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_AUTH_DECLINE_RET");
				 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				//String  retryMinutes="10";
				 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				//calendar.add(Calendar.DATE, 7);
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
				//outStruct.collectionDate=sdf.parse(collectionDate);
				outStruct.retryFlag="Y";
				//retryCount++;
				//outStruct.PaymentReference3=Integer.toString(retryCount);
				//modifyRetryCount(env,sOHK,strChargeTrasactionKey,retryCount);
			
				
			}
			else{
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_AUTH_DECLINE_RET");
				 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				//String  retryMinutes="10";
				 String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				//calendar.add(Calendar.DATE, 7);
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				//String collectionDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
				//outStruct.collectionDate=sdf.parse(collectionDate);
				outStruct.retryFlag="Y";
				//retryCount++;
				//outStruct.PaymentReference3=Integer.toString(retryCount);
				modifyRetryCount(env,sOHK,strChargeTrasactionKey,retryCount);
				
			}
//OMS870-Applying Payment Hold Manually-Stop
		}

		else {
			String cancellationReasonCode = "AUTH_CANCEL_ORDER";
			String cancellationReasonText = "AUTH_CANCEL_ORDER";
			Document cancelInput = XMLUtil.createDocument("Order");
			Element OrderEle = cancelInput.getDocumentElement();
			OrderEle.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			OrderEle.setAttribute("Override", "Y");
			OrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
			OrderEle.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
					cancellationReasonCode);
			OrderEle.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT,
					cancellationReasonText);
			//OMS-1760: Start
			if(!isDraftOrd)
			{
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER, cancelInput);
			}
			//OMS-1760: End
			
		}

	}

	/**
	 * private void changeTranSequence(YFSEnvironment env,String sOHK, int
	 * tranSequence ) throws ParserConfigurationException,
	 * YIFClientCreationException, YFSException, RemoteException
	 * 
	 * { String tranSequenceOms=Integer.toString(tranSequence); Document
	 * ChangeOrderInput = XMLUtil.createDocument("Order"); Element OrderEle =
	 * ChangeOrderInput.getDocumentElement();
	 * OrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK); Element
	 * extnElement = XMLUtil.createElement(ChangeOrderInput,
	 * VSIConstants.ELE_EXTN, "");
	 * extnElement.setAttribute(VSIConstants.ATTR_SEQUENCE, tranSequenceOms);
	 * OrderEle.appendChild(extnElement); api =
	 * YIFClientFactory.getInstance().getApi(); api.invoke(env,
	 * VSIConstants.API_CHANGE_ORDER,ChangeOrderInput); }
	 **/

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
			throws ParserConfigurationException {
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
	
	//OMS -870 : Start
	private int getRetryCount(YFSEnvironment env, String strOrderHeaderKey,String strChargeTransactionKey){
		
		int iRetryCount=0;
		
		try {
			//create input and invoke GetVSIPaymentRetryList
			Document docVSIPaymentRetry = XMLUtil.createDocument("VSIPaymentRetry");
			Element eleVSIPaymentRetry = docVSIPaymentRetry.getDocumentElement() ;
			eleVSIPaymentRetry.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			eleVSIPaymentRetry.setAttribute(VSIConstants.ATTR_CHARGE_TRANSACTION_KEY, strChargeTransactionKey);
			Document docVSIPaymentRetryList=VSIUtils.invokeService(env, "GetVSIPaymentRetryList", docVSIPaymentRetry);
			Element eleVSIPaymentRetryList=docVSIPaymentRetryList.getDocumentElement();
			if(eleVSIPaymentRetryList!=null && eleVSIPaymentRetryList.hasChildNodes() ){
				Element eleVSIPaymentRetryOP=XMLUtil.getFirstElementByName(eleVSIPaymentRetryList, "VSIPaymentRetry");
				if(eleVSIPaymentRetryOP!=null){
					String strRetryCount=eleVSIPaymentRetryOP.getAttribute("RetryCount");
					if(!YFCCommon.isStringVoid(strRetryCount)){
						iRetryCount=Integer.parseInt(strRetryCount);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return iRetryCount;
		
	}
	
	private void modifyRetryCount(YFSEnvironment env,String sOHK,String strChargeTrasactionKey,int retryCount){
		
		try {
			
			Document docVSIPaymentRetry = XMLUtil.createDocument("VSIPaymentRetry");
			Element eleVSIPaymentRetry = docVSIPaymentRetry.getDocumentElement() ;
			eleVSIPaymentRetry.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
			eleVSIPaymentRetry.setAttribute(VSIConstants.ATTR_CHARGE_TRANSACTION_KEY, strChargeTrasactionKey);
			retryCount++;
			eleVSIPaymentRetry.setAttribute("RetryCount",String.valueOf(retryCount) );
			if(retryCount>1){
				//update existing record
				VSIUtils.invokeService(env, "ModifyVSIPaymentRetry", docVSIPaymentRetry);
				
			}
			else{
				//create new entry
				VSIUtils.invokeService(env, "CreateVSIPaymentRetry", docVSIPaymentRetry);
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	//OMS-1762: BR***Payment Confirmation Screen: No error message on Auth Failure: Throwing the exception from applyPaymentErrorHold method//
	private void applyPaymentErrorHold(YFSEnvironment env,String sOHK) throws ParserConfigurationException, YIFClientCreationException,
	YFSException, RemoteException{
		//OMS-1760: PAYMENT_HOLD should not be applied on Draft Orders :Start//
		boolean isDraftOrd=false;
		Document getOrderListDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,sOHK);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
				"global/template/api/VSIGetOrderListCC.xml");
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListDoc);
		Element eleOrd = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);
		String draftOrderFlag = eleOrd.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
		if(VSIConstants.FLAG_Y.equals(draftOrderFlag))
		{
			isDraftOrd = true;
		}
		env.clearApiTemplates();
			if(!isDraftOrd)
			{
			Document docApplyPaymentHold = SCXmlUtil.createDocument("Order");
			Element eleApplyPaymentHold = docApplyPaymentHold.getDocumentElement();
			eleApplyPaymentHold.setAttribute("Action", "MODIFY");
			eleApplyPaymentHold.setAttribute("OrderHeaderKey", sOHK);
			eleApplyPaymentHold.setAttribute("Override", "Y");
			Element eleOrderHoldTypes = SCXmlUtil.createChild(eleApplyPaymentHold, "OrderHoldTypes");
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, "OrderHoldType");
			eleOrderHoldType.setAttribute("HoldType", "VSI_PAYMENT_HOLD");
			eleOrderHoldType.setAttribute("ReasonText", "VSI_PAYMENT_HOLD for ERROR response");
			eleOrderHoldType.setAttribute("Status", "1100");
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", docApplyPaymentHold);
			}
			
			//OMS-1760: PAYMENT_HOLD should not be applied on Draft Orders :End//
	}
	//OMS - 870: End
	//OMS-1635: Start
	/* 
	 * Below method will be used to check if the Order is a new order.
	 *
	 */
	public String checkNewOrder(YFSEnvironment env, String strOrderHeaderKey)
			throws Exception {
		String strNewOrder = null;
		Document docGetOrderListIn = XMLUtil.createDocument("Order");
		Element eleOrder = docGetOrderListIn.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				strOrderHeaderKey);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
				"global/template/api/VSIGetOrderListCC.xml");
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,
				docGetOrderListIn);
		env.clearApiTemplates();
		Element eleExtn = (Element) outDoc.getElementsByTagName(
				VSIConstants.ELE_EXTN).item(0);
		
		strNewOrder=eleExtn.getAttribute("ExtnNewOrder");
		return strNewOrder;
	}
	//OMS-1635: End
	// OMS-1913: Start
	/*
	 * Below method will be used to check if any OrderLines are stuck in Created
	 * /NoAction status and for canceling those Open OrderLines
	 *
	 */
	public void cancelOpenOrderLines(YFSEnvironment env, String strOrderHeaderKey) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		Document docGetOrderListIn = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrder = docGetOrderListIn.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, VSIConstants.TEMPLATE_GET_ORD_LIST_ORDSTATUS);
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST, docGetOrderListIn);
		env.clearApiTemplates();
		Element eleOrd = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String draftOrderFlag = eleOrd.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
		boolean isDraftOrd = false;
		if (VSIConstants.FLAG_Y.equals(draftOrderFlag)) {
			isDraftOrd = true;
		}if (!isDraftOrd) {
			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			String strCancellationReasonCode = VSIConstants.AUTH_CANCEL_ORDER;
			String strCancellationReasonText = VSIConstants.AUTH_CANCEL_ORDER;
			Document docCancelOrdInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleCanOrder = docCancelOrdInput.getDocumentElement();
			eleCanOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
			eleCanOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			eleCanOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, strCancellationReasonCode);
			eleCanOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, strCancellationReasonText);
			Element eleChangeOrderLines = docCancelOrdInput.createElement(VSIConstants.ELE_ORDER_LINES);
			eleCanOrder.appendChild(eleChangeOrderLines);
			for (int j = 0; j < orderLineList.getLength(); j++) {
				Element eleOrderLine = (Element) orderLineList.item(j);
				String strOrdLineqty = eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
				int ordQty = Integer.parseInt(strOrdLineqty);
				int cancellableQty = 0;
				NodeList orderStatusList = eleOrderLine.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
				Element eleChangeOrderLine = docCancelOrdInput.createElement(VSIConstants.ELE_ORDER_LINE);
				for (int k = 0; k < orderStatusList.getLength(); k++) {
					Element eleOrderStatus = (Element) orderStatusList.item(k);
					String strOrderStatus = eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS);
					String strQuantity = eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS_QUANTITY);
					int iStatusQty = Integer.parseInt(strQuantity);
					if (VSIConstants.STATUS_STS_NO_ACTION.equals(strOrderStatus) || VSIConstants.STATUS_CREATE.equals(strOrderStatus)
							|| VSIConstants.STATUS_BACKORDERED.equals(strOrderStatus)
							|| VSIConstants.STATUS_NO_ACTION.equals(strOrderStatus)
							|| VSIConstants.STATUS_CODE_RELEASED.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_STORE_RECEIVED.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_TO_CREATED.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_SENT_TO_WH.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_RELEASE_ACK.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_COMPLT.equals(strOrderStatus)
							|| VSIConstants.STATUS_STS_JDA_STS_ACK.equals(strOrderStatus))  {
						eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
						eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,
								eleOrderStatus.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
						cancellableQty = cancellableQty + iStatusQty;
					}
				}
				if (cancellableQty > 0) {
					String strOrdQty = String.valueOf(ordQty - cancellableQty);
					eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, strOrdQty);
					eleChangeOrderLines.appendChild(eleChangeOrderLine);
				}
			}
			NodeList nlchangeOrderLineList = docCancelOrdInput.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			if (nlchangeOrderLineList.getLength() > 0) {
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER, docCancelOrdInput);
				if (log.isDebugEnabled()) {
					log.debug("changeOrder input" + XmlUtils.getString(docCancelOrdInput));
					log.debug("================Cancellation Done================================");
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("================No OrderLins available for Cancellation================================");
				}
			}
		}
	}
	// OMS-1913: End
}// End of collectionOtherClass

