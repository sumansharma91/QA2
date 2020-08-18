package com.vsi.oms.api.aurus;
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
import com.vsi.oms.api.aurus.VSIPreAuthAESDKFunction.TransRequestObject;
import com.vsi.oms.userexit.VSICreditCardFunction;
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

public class VSIAurusCollectionCreditCard implements YFSCollectionCreditCardUE {
	
	private static final String TAG = VSIAurusCollectionCreditCard.class.getSimpleName();
	
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
			.instance(VSIAurusCollectionCreditCard.class.getName());

	YIFApi api;
	
	boolean bIsFirstRefund = true;
	String strAurusRfndDtlKey=null;
	
	public YFSExtnPaymentCollectionOutputStruct collectionCreditCard(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct)
					throws YFSUserExitException {


		try {
			printLogs("================Inside VSIAurusCollectionCreditCard================================");
			printLogs("Printing Input XML :");
			
			YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
			String paymentType = inStruct.paymentType;

			String strChargeType = inStruct.chargeType;

			Double dRequestAmount = inStruct.requestAmount;

			if (VSIConstants.PAYMENT_MODE_CC
					.equalsIgnoreCase(inStruct.paymentType)) {
						
				HashMap<String, String> IsDraftOrAurus = initailAurusOrderDetail(inStruct, outStruct, env);

				boolean isAurusOrder = false;
				boolean isDraftOrder = false;

				String strMaxOrderStatus=IsDraftOrAurus.get("MAXORDERSTATUS");
				String strMinOrderStatus=IsDraftOrAurus.get("MINORDERSTATUS");
				String strExtnPostAuthSequenceNo=IsDraftOrAurus.get("POSTAUTHSEQNO");
				String strExtnPostAuthCount=IsDraftOrAurus.get("POSTAUTHCOUNT");
				String strExtnRefundSequenceNo=IsDraftOrAurus.get("REFUNDSEQNO");
				String strExtnRefundCount=IsDraftOrAurus.get("REFUNDCOUNT");
				boolean isTNSDecommission =false; 
				boolean isTNSSwitch =false;
				boolean isCallCenterAurus = false;
				String aurusCI=IsDraftOrAurus.get("EXTNAURUSCI");
				String aurusOOT=IsDraftOrAurus.get("EXTNAURUSOOT");
				String strSOHdrKey=IsDraftOrAurus.get("SALESORDERHEADERKEY");
				String strAurusReauth=IsDraftOrAurus.get("EXTNAURUSREAUTHORIZED");
				String strEntryType=IsDraftOrAurus.get("ENTRYTYPE");
				String strAurusOrder=IsDraftOrAurus.get("ISAURUSORDER");
				//OMS-2437 -- Start
				String strOrderType=IsDraftOrAurus.get("ORDERTYPE");
				//OMS-2437 -- End
				

				isTNSDecommission = isTNSDecommission(env);
				
				isTNSSwitch = isTNSSwitch(env);
				
				isCallCenterAurus = isCallCenterAurus(env);
				
				if(!YFCCommon.isVoid(strAurusOrder) && VSIConstants.FLAG_Y.equalsIgnoreCase(strAurusOrder)) {
					isAurusOrder=true;
				}
				if(IsDraftOrAurus.get("ISDRAFTORDER").equalsIgnoreCase(VSIConstants.FLAG_Y) && isCallCenterAurus) {
					isDraftOrder=true;
				}

				if ("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)) {
					
					if (inStruct.requestAmount >= 0.0D) {
						
						if(isAurusOrder || isDraftOrder) { 
							doAuthorizationAurus(inStruct, outStruct, env ,aurusCI,aurusOOT ,isAurusOrder,false,false,strEntryType,isDraftOrder,strOrderType);
						}else if(!isAurusOrder && isTNSDecommission ){
							doAuthorizationAurus(inStruct, outStruct, env ,aurusCI,aurusOOT,isAurusOrder,isTNSDecommission,isTNSSwitch,strEntryType,isDraftOrder,strOrderType);
						}else if(!isAurusOrder && isTNSSwitch ){
							doAuthorizationAurus(inStruct, outStruct, env ,aurusCI,aurusOOT,isAurusOrder,isTNSDecommission,isTNSSwitch,strEntryType,isDraftOrder,strOrderType);
						}
						else{
							doAuthorization(inStruct, outStruct, env);
						}
					} else {
						//Harshit:add the TNS Decomission condition her and we will do the dummy void
						if(isAurusOrder || isDraftOrder || isTNSDecommission || isTNSSwitch){
							doVoidAurus(inStruct, outStruct, env ,isAurusOrder, isTNSDecommission, isTNSSwitch, strEntryType, strOrderType);
						}else{
							doVoid(inStruct, outStruct, env);
						}
					}
				} else if ("CHARGE".equalsIgnoreCase(inStruct.chargeType)) {

					if (inStruct.requestAmount >= 0.0D) {
						if(isAurusOrder ||isTNSDecommission || isTNSSwitch){
						
						doChargeForAurus(inStruct, outStruct, env,strMaxOrderStatus,strMinOrderStatus,strExtnPostAuthSequenceNo,
								strExtnPostAuthCount,aurusCI,aurusOOT,isTNSDecommission,isAurusOrder,strAurusReauth,isTNSSwitch,strEntryType,strOrderType);
						}
						else{
						doCharge(inStruct, outStruct, env);
						}

					} else {
						if(isAurusOrder || isTNSDecommission || isTNSSwitch){
							doRefundAurus(inStruct, outStruct, env,strMaxOrderStatus,strMinOrderStatus,aurusCI,aurusOOT,isTNSDecommission,
									isAurusOrder,strExtnRefundSequenceNo,strExtnRefundCount,strSOHdrKey,isTNSSwitch);
						}
						else{
							doRefund(inStruct, outStruct, env);
						}
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

	private boolean isCallCenterAurus(YFSEnvironment env) throws ParserConfigurationException {
		
		boolean isCallCenterAurus=false;
		Document getCommonCodeListInputForCallCenterAurus = getCommonCodeListInputForCodeType("DEFAULT","CALL_CENTER_AURUS");
		Document getCommonCodeOutCallCenterAurus = getCommonCodeList(env, getCommonCodeListInputForCallCenterAurus);
		Element eleCOmmonCodeOut = (Element)getCommonCodeOutCallCenterAurus.getElementsByTagName("CommonCode").item(0);
		String strCallCenterAurus = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
		if(strCallCenterAurus.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
			isCallCenterAurus=true;
		}
		
		return isCallCenterAurus;
	}

	private boolean isTNSDecommission(YFSEnvironment env) throws ParserConfigurationException {
		boolean isTNSDecommission=false;
		Document getCommonCodeListInputForTNSDecommission = getCommonCodeListInputForCodeType("DEFAULT","VSI_TNSDecommission");
		Document getCommonCodeOutTNSDecommission = getCommonCodeList(env, getCommonCodeListInputForTNSDecommission);
		Element eleCOmmonCodeOut = (Element)getCommonCodeOutTNSDecommission.getElementsByTagName("CommonCode").item(0);
		String TNSDecommission = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
		if(TNSDecommission.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
			isTNSDecommission=true;
		}
		return isTNSDecommission;
	}
	
	private boolean isTNSSwitch(YFSEnvironment env) throws ParserConfigurationException {
		boolean isTNSSwitch=false;
		Document getCommonCodeListInputForTNSDecommission = getCommonCodeListInputForCodeType("DEFAULT","VSI_TNSSwitch");
		Document getCommonCodeOutTNSDecommission = getCommonCodeList(env, getCommonCodeListInputForTNSDecommission);
		Element eleCOmmonCodeOut = (Element)getCommonCodeOutTNSDecommission.getElementsByTagName("CommonCode").item(0);
		String TNSDecommission = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
		if(TNSDecommission.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
			isTNSSwitch=true;
		}
		return isTNSSwitch;
	}
	
		private void doRefundAurus(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env,
			String strMaxOrderStatus, String strMinOrderStatus, String aurusCI,
			String aurusOOT, boolean isTNSDecommission, boolean isAurusOrder,
			String strExtnRefundSequenceNo, String strExtnRefundCount, String strSOHdrKey, boolean isTNSSwitch) {
					//Harshit: In Flight changes:
		//if not an Aurus Order and TNS is decommissioned, and if CI is present use the CI directly to refund
		//if not an Aurus Order and TNS is decommissioned, and if CI is not present then raise an alert to customer care so that we can get the CI and update it and rerun the scenario in production.

		printLogs("Inside doRefundAurus Method");
		
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String strOrderNo = inStruct.orderNo;
		
		//OMS-2445 -- Start
		String strQueueId="VSI_CREDITCARD_REFUND_ALERT";
		//OMS-2445 -- End

		
		try {
			
			if(!isAurusOrder && isTNSDecommission && YFCCommon.isVoid(aurusCI)){
				
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_REFUND_TIME");				
				Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				
				String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				
				outStruct.retryFlag="Y";
				
				String errorCodeStr = "NoCIforRefund";
				String errorMessage = "NoCIforRefund";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				
				applyPaymentErrorHold(env, strOrderHeaderKey);
				
			}
			else{
			
			ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");
	
			HashMap<String, String> aurusMandateParam= new HashMap<String, String>();
	
			if(!alCommonCodeList.isEmpty()){
				for(int i=0;i<alCommonCodeList.size();i++) {
					Element eleCommonCode=alCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);						
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
								
						if(strCodeValue.startsWith("TransactionType")) {
							if(strCodeValue.indexOf("Refund")>0) {
								aurusMandateParam.put("TransactionType", strShortdecValue);
							}
						}else {
							aurusMandateParam.put(strCodeValue, strShortdecValue);	
						}
					}
				}
			}
			ArrayList<Element> aurusRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_RETRY", null, "DEFAULT");
				
			HashMap<String, String> aurusRetryErrCode= new HashMap<String, String>();
				
			if(!aurusRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<aurusRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=aurusRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);						
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						aurusRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}
			ArrayList<Element> processorRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_PROCESSOR_RETRY", null, "DEFAULT");
			
			HashMap<String, String> processorRetryErrCode= new HashMap<String, String>();
			
			if(!processorRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<processorRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=processorRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						//							System.out.println(strCodeValue + " "+strShortdecValue);
						processorRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}
				
			String transactionIdStr = null;
			String Orderid = null;
			String merchantId = null;
				
			double dRequestAmountNeg  = inStruct.requestAmount;
			double dRequestAmount = Math.abs(dRequestAmountNeg);
			String requestAmountStr=Double.toString(dRequestAmount);				  
				
			String currencyCodestr = "840";
				
			String tokenIdStr = inStruct.creditCardNo;
			if(inStruct.paymentReference2 ==null && inStruct.paymentReference3 == null) {
				tokenIdStr=inStruct.creditCardNo; //ott
			}else if(inStruct.paymentReference2 !=null){
				tokenIdStr=inStruct.paymentReference2;; //CI
			}
	
			String firstName = inStruct.firstName;
			String lastName = inStruct.lastName;
			String expDate = inStruct.creditCardExpirationDate;
			String AdressLine1=inStruct.billToAddressLine1;
			
			String country=inStruct.billToCountry;
	
			// change 2 char country to 3 char
			if(country.length() == 2) {
				country = VSIUtils.getCountryCode(env, country);
			}
			//jira 803	
								
			String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
			transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
			Orderid = inStruct.chargeTransactionKey+tranNumber;
				
			if(transactionIdStr.length()>13) {
				transactionIdStr = transactionIdStr.substring(transactionIdStr.length() - 13);
			}
	
			VSIPreAuthAESDKFunction vsiAurusAuth=new VSIPreAuthAESDKFunction();
			TransRequestObject transReq = new TransRequestObject();
				
			if(!isAurusOrder && isTNSSwitch ) {
				//set CRMToken with paymentReference2 and not set oot , ci and ott
				if((aurusCI==null || (aurusCI!=null && aurusCI.length()==0))) {
					transReq.setCrmToken(inStruct.paymentReference2);
				}
				else{
					transReq.setCardIdentifier(aurusCI);
				}
			} else{
				transReq.setCardIdentifier(aurusCI);
			}
								
			transReq.setTransactionTotal(requestAmountStr); 
			transReq.setCurrencyCode(currencyCodestr);
			transReq.setMerchantIdentifier(merchantId);
			transReq.setBillingFirstName(firstName);
			transReq.setBillingLastName(lastName);
			String strExpDate=expDate.substring(0, 2)+expDate.substring(5);
			transReq.setCardExpiryDate(strExpDate);
			transReq.setBillingAddressLine1(AdressLine1);
	
			transReq.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
			transReq.setaDSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
			transReq.setCorpID(aurusMandateParam.get("CorpID"));
			transReq.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
			transReq.setStoreId(aurusMandateParam.get("StoreId"));
			transReq.setTerminalId(aurusMandateParam.get("TerminalId"));
			transReq.setTransactionType(aurusMandateParam.get("TransactionType"));
			
			printLogs("TransactionType "+aurusMandateParam.get("TransactionType"));
			
			//OMS-2438 -- Start
			int iLength=strOrderNo.length();
			if(iLength>16){
				printLogs("Order No before trimming: "+strOrderNo);
				String strOrderNoPOS = strOrderNo.substring(0, 16);
				printLogs("Order No after trimming: "+strOrderNoPOS);
				transReq.setInvoiceNumber(strOrderNoPOS);
			}else{
				printLogs("Order No is within limits so trimming not required");
				transReq.setInvoiceNumber(strOrderNo);
			}
			//OMS-2438 -- End
						
			//OMS-2392 -- Start
			String strReferenceNumber = getReferenceNumber(strOrderNo);			
			transReq.setReferenceNumber(strReferenceNumber);
			//OMS-2392 -- End
			
			//OMS-2408 -- Start
			HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
			transDateAndTime=getTransactionDateAndTime();			
			String strTransDate = transDateAndTime.get("TransactionDate");
			String strTransTime = transDateAndTime.get("TransactionTime");
			transReq.setTransactionDate(strTransDate);
			transReq.setTransactionTime(strTransTime);
			//OMS-2408 -- End
			
			transReq.setPostAuthCount(strExtnRefundCount);
			
			printLogs("RefundCount: "+strExtnRefundCount);
			
			//OMS-2463 -- Start
			int iRefundCount = Integer.parseInt(strExtnRefundCount);
			//OMS-2463 -- End
			
			String fstrExtnRefundSequenceNo=null;
			int iExtnRefundSequenceNo =0;
			
			if((strMinOrderStatus.equals("3700.01.20.ex")) || (strMinOrderStatus.equals("3700.02"))){
				printLogs("Order is fully Refunded");
				fstrExtnRefundSequenceNo=strExtnRefundCount;
			}else{
				printLogs("Order is not fully Refunded yet");
				
				int iRefundSeqNo = Integer.parseInt(strExtnRefundSequenceNo);
				iExtnRefundSequenceNo=iRefundSeqNo+1;
				
				//OMS-2463 -- Start
				if(iExtnRefundSequenceNo > iRefundCount){
					printLogs("Calculated value of RefundSequenceNo is greater than RefundCount: "+Integer.toString(iExtnRefundSequenceNo));
					iExtnRefundSequenceNo=iRefundCount;
					printLogs("Updated value of RefundSequenceNo is: "+Integer.toString(iExtnRefundSequenceNo));
				}
				//OMS-2463 -- End
				
				fstrExtnRefundSequenceNo = Integer.toString(iExtnRefundSequenceNo);
			}		
			
			
			printLogs("RefundSequenceNo: "+fstrExtnRefundSequenceNo);
			transReq.setPostAuthSequenceNo(fstrExtnRefundSequenceNo);		    
								
			Document transRequestDoc =vsiAurusAuth.createTransReqAPiInput(transReq);
				
			recordExtnRefundData(fstrExtnRefundSequenceNo,strExtnRefundCount,strSOHdrKey,env);
									
			printLogs("Now inserting request in Payment Record table");
			printLogs("Refund Request "+SCXmlUtil.getString(transRequestDoc));
				
			addDataToPaymentRecordTable(env,transRequestDoc,"Aurus Refund Request",strOrderHeaderKey);
	
			Document outDoc =vsiAurusAuth.aurusTransRequestAPI(transRequestDoc);				
				
			// insert Auth record			
			
			printLogs("Now inserting response in Payment Record table");
			printLogs("Refund Response "+SCXmlUtil.getString(outDoc));
			
			addDataToPaymentRecordTable(env,outDoc,"Aurus Refund Response",strOrderHeaderKey);
	
			//Printing the Response
	
			HashMap<String, String> refundResponse = vsiAurusAuth.parseTransReponseOutDoc(outDoc);
	
			//parsing the response
			if (refundResponse.containsKey("ResponseText")) {
				
				printLogs("Aurus Refund Response:"+refundResponse.toString());
					
				String responseCode=refundResponse.get("ResponseCode");					
				String processorResponseCode=refundResponse.get("ProcessorResponseCode");
				String approvalCode=refundResponse.get("ApprovalCode");
				
			if (responseCode.equalsIgnoreCase("00000")) {  //Approval					
					
				outStruct.authorizationId = inStruct.authorizationId;	
				outStruct.authorizationAmount = inStruct.requestAmount;					
				outStruct.retryFlag = "N";			
				
				outStruct.authCode = approvalCode;
				
				printLogs("Processing Aurus Refund Approval Response Done");
					
			}else if(aurusRetryErrCode.containsKey(responseCode)||processorRetryErrCode.containsKey(processorResponseCode)) {  //retry
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
				Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
					
				String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
	
				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; 
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();					
				outStruct.retryFlag="Y";	
				
				if(responseCode.equalsIgnoreCase("71012")) {
						initAESDK();					
				}
				
				printLogs("Processing Aurus Refund Retry Response Done");
			}else {   //Decline
				String errorCodeStr = "RefundFail";
				String errorMessage = "RefundFail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
							
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				
				applyPaymentErrorHold(env, strOrderHeaderKey);
				
				printLogs("Processing Aurus Refund Retry Response Done");
			}
		}
		}
	}catch (Exception Ex) {
		Ex.printStackTrace();
		throw new YFSException();
		}
	}
	
	private void initAESDK() {
		
		VSIInitAESDK vsiInitAESDK = new VSIInitAESDK();
		Document outInitDoc = vsiInitAESDK.initSDKCall();
		
		printLogs("Init AESDK Output"+ SCXmlUtil.getString(outInitDoc));
		
		if(outInitDoc!=null) {
			return ;
		}
		
//		new Thread(new Runnable() {							
//			@Override
//			public void run() {
//				VSIInitAESDK vsiInitAESDK = new VSIInitAESDK();
//				vsiInitAESDK.initSDKCall();
//				
//			}
//		}).start();	
	}
	
		private void recordExtnRefundData(String fstrExtnRefundSequenceNo,
				String strExtnRefundCount, String strSOHdrKey, YFSEnvironment env) throws YIFClientCreationException, YFSException, RemoteException {
			
			printLogs("Inside recordExtnRefundData method");
			
			Document docOrder = SCXmlUtil.createDocument("AurusRefundDetails");
			Element eleOrder = docOrder.getDocumentElement();			
			
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strSOHdrKey);
			eleOrder.setAttribute("ExtnRefundSequenceNo", fstrExtnRefundSequenceNo);
			eleOrder.setAttribute("ExtnRefundCount", strExtnRefundCount);
			
			if(bIsFirstRefund){				
				
				printLogs("recordExtnRefundData: Input to VSICreateAurusRefundDetails service: "+XmlUtils.getString(docOrder));
				
				api = YIFClientFactory.getInstance().getApi();
	    		api.executeFlow(env,
	    				"VSICreateAurusRefundDetails", docOrder);
			}else{
				eleOrder.setAttribute("AurusRefundDetailKey", strAurusRfndDtlKey);
				
				printLogs("recordExtnRefundData: Input to VSIChangeAurusRefundDetails service: "+XmlUtils.getString(docOrder));
				
				api = YIFClientFactory.getInstance().getApi();
	    		api.executeFlow(env,
	    				"VSIChangeAurusRefundDetails", docOrder);
			}
			
			printLogs("Exiting recordExtnRefundData method with output ");			
		}

		private void doChargeForAurus(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env,
			String strMaxOrderStatus, String strMinOrderStatus,
			String strExtnPostAuthSequenceNo, String strExtnPostAuthCount,
			String aurusCI, String aurusOOT, boolean isTNSDecommission, boolean isAurusOrder, String strAurusReauth, boolean isTNSSwitch, String strEntryType, String strOrderType) throws ParserConfigurationException {
		
		printLogs("================Inside doChargeForAurus================================");
		
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String strOrderNo = inStruct.orderNo;
		
		//OMS-2445 -- Start
		String strQueueId="VSI_CREDITCARD_CHARGE_ALERT";
		//OMS-2445 -- End

		
		try {
			
			String authorizationIdStr=null;
			authorizationIdStr = inStruct.authorizationId;
			//Harshit: Change below conditions:
			//TNSDEcomission is true and it is not an Aurus order and we have CI then use CI(CRM token) for POST auth irrespective if we have authorizations or not
			//TNSDEcomission is true and it is not an Aurus order and we dont have CI, then we have to call pre-auth with TNS token , get the CI and use that for Charge. then we have to call below method doauthorizeforCharge. if Authid is present we will use the same auth id to complete the transaction, if it is not present we will get the latest and update the charge transactoion table.
			
			if(YFCObject.isVoid(authorizationIdStr) || YFCObject.isVoid(aurusCI) || (!isAurusOrder && (!VSIConstants.FLAG_Y.equals(strAurusReauth)))){
				HashMap<String, String> authResponse = DoAuthorizeAurusCharge(inStruct,outStruct,aurusCI,aurusOOT,isTNSDecommission,isAurusOrder,env,isTNSSwitch,strEntryType,strOrderType);	

				if(!authResponse.isEmpty()){
					authorizationIdStr = authResponse.get("TransactionID");
					aurusCI=authResponse.get("CardIdentifier");
				}
					
				
			}
			if(YFCCommon.isVoid(aurusCI)){
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_REFUND_TIME");				
				Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				
				String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				
				outStruct.retryFlag="Y";
				
				String errorCodeStr = "NoCIforCharge";
				String errorMessage = "NoCIforCharge";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				
				applyPaymentErrorHold(env, strOrderHeaderKey);
			}
			
			else if(!YFCObject.isVoid(authorizationIdStr)){

				ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");

				HashMap<String, String> aurusMandateParam= new HashMap<String, String>();

				if(!alCommonCodeList.isEmpty()){
					for(int i=0;i<alCommonCodeList.size() ;i++) {
						Element eleCommonCode=alCommonCodeList.get(i);
						String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
						if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
							
							if(strCodeValue.startsWith("TransactionType")) {
								if(strCodeValue.indexOf("PostAuth")>0) {
									aurusMandateParam.put("TransactionType", strShortdecValue);
								}
							}else {
								aurusMandateParam.put(strCodeValue, strShortdecValue);	
							}
						}
					}
				}
				ArrayList<Element> aurusRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_RETRY", null, "DEFAULT");
				
				HashMap<String, String> aurusRetryErrCode= new HashMap<String, String>();
				
				if(!aurusRetryErrCodeCommonCodeList.isEmpty()){
					for(int i=0;i<aurusRetryErrCodeCommonCodeList.size() ;i++) {
						Element eleCommonCode=aurusRetryErrCodeCommonCodeList.get(i);
						String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
						if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
							aurusRetryErrCode.put(strCodeValue, strShortdecValue);
						}
					}
				}
				ArrayList<Element> processorRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_PROCESSOR_RETRY", null, "DEFAULT");
				
				HashMap<String, String> processorRetryErrCode= new HashMap<String, String>();
				
				if(!processorRetryErrCodeCommonCodeList.isEmpty()){
					for(int i=0;i<processorRetryErrCodeCommonCodeList.size() ;i++) {
						Element eleCommonCode=processorRetryErrCodeCommonCodeList.get(i);
						String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
						if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
							//							System.out.println(strCodeValue + " "+strShortdecValue);
							processorRetryErrCode.put(strCodeValue, strShortdecValue);
						}
					}
				}

				String transactionIdStr = null;
				String Orderid = null;
				String merchantId = null;
				
				String requestAmountStr=Double.toString(inStruct.requestAmount);  
				String currencyCodestr = "840";
				//Harshit: Comment this if not require it is confuson
				/*String tokenIdStr = inStruct.creditCardNo;
				if(inStruct.paymentReference2 ==null && inStruct.paymentReference3 == null) {
					tokenIdStr=inStruct.creditCardNo; //ott
				}else if(inStruct.paymentReference2 !=null){
					tokenIdStr=inStruct.paymentReference2;; //CI
				}*/

				String firstName = inStruct.firstName;
				String lastName = inStruct.lastName;
				String expDate = inStruct.creditCardExpirationDate;
				String AdressLine1=inStruct.billToAddressLine1;
				
				String country=inStruct.billToCountry;

				// change 2 char country to 3 char
				if(country.length() == 2) {
					country = VSIUtils.getCountryCode(env, country);
				}
				//jira 803		
				
				printLogs("country: "+country);

				String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
				transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
				Orderid = inStruct.chargeTransactionKey+tranNumber;
				
				printLogs("transactionIdStr before trimming: "+transactionIdStr);
				printLogs("Orderid: "+Orderid);
				
				if(transactionIdStr.length()>13) {
					transactionIdStr = transactionIdStr.substring(transactionIdStr.length() - 13);
				}
				
				printLogs("transactionIdStr after trimming: "+transactionIdStr);
				
				VSIPreAuthAESDKFunction vsiAurusAuth=new VSIPreAuthAESDKFunction();
				TransRequestObject transReq = new TransRequestObject();				
				
				transReq.setCardIdentifier(aurusCI);
								
				transReq.setOrigTransactionIdentifier(authorizationIdStr);
				
				transReq.setTransactionTotal(requestAmountStr); 
				transReq.setCurrencyCode(currencyCodestr);
				transReq.setMerchantIdentifier(merchantId);
				
				transReq.setBillingFirstName(firstName);
				transReq.setBillingLastName(lastName);
				String strExpDate=expDate.substring(0, 2)+expDate.substring(5);
				transReq.setCardExpiryDate(strExpDate);
				transReq.setBillingAddressLine1(AdressLine1);
				
				transReq.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
				transReq.setaDSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
				transReq.setCorpID(aurusMandateParam.get("CorpID"));
				transReq.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
				transReq.setStoreId(aurusMandateParam.get("StoreId"));
				transReq.setTerminalId(aurusMandateParam.get("TerminalId"));
				transReq.setTransactionType(aurusMandateParam.get("TransactionType"));
				
				printLogs("TransactionType "+aurusMandateParam.get("TransactionType"));
				
				//OMS-2437 -- Start
				if(VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType) || VSIConstants.POS_ENTRY_TYPE.equals(strEntryType)){
					printLogs("POS Order No before trimming: "+strOrderNo);
					String strOrderNoPOS = strOrderNo.substring(0, 16);
					printLogs("POS Order No after trimming: "+strOrderNoPOS);
					transReq.setInvoiceNumber(strOrderNoPOS);
				}else{
					transReq.setInvoiceNumber(strOrderNo);
				}			
				//OMS-2437 -- End
				
				//OMS-2392 -- Start
				String strReferenceNumber = getReferenceNumber(strOrderNo);			
				transReq.setReferenceNumber(strReferenceNumber);
				//OMS-2392 -- End
				
				//OMS-2408 -- Start
				HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
				transDateAndTime=getTransactionDateAndTime();			
				String strTransDate = transDateAndTime.get("TransactionDate");
				String strTransTime = transDateAndTime.get("TransactionTime");
				transReq.setTransactionDate(strTransDate);
				transReq.setTransactionTime(strTransTime);
				//OMS-2408 -- End
				
				transReq.setPostAuthCount(strExtnPostAuthCount);
				
				printLogs("PostAuthCount: "+strExtnPostAuthCount);		
				
				//OMS-2463 -- Start
				int iPostAuthCount = Integer.parseInt(strExtnPostAuthCount);
				//OMS-2463 -- End
				
				String fstrExtnPostAuthSequenceNo=null;
				int iExtnPostAuthSequenceNo = 0;
				if(Double.parseDouble(strMinOrderStatus)<3700 ){
					printLogs("Order is not fully Shipped yet");
					
					int iPostAuthSeqNo = Integer.parseInt(strExtnPostAuthSequenceNo);
					iExtnPostAuthSequenceNo = iPostAuthSeqNo + 1;
					//OMS-2463 -- Start
					if(iExtnPostAuthSequenceNo > iPostAuthCount){
						printLogs("Calculated value of PostAuthSequenceNo is greater than PostAuthCount: "+Integer.toString(iExtnPostAuthSequenceNo));
						iExtnPostAuthSequenceNo=iPostAuthCount;
						printLogs("Updated value of PostAuthSequenceNo is: "+Integer.toString(iExtnPostAuthSequenceNo));
					}
					//OMS-2463 -- End
					fstrExtnPostAuthSequenceNo = Integer.toString(iExtnPostAuthSequenceNo);
				}
				else if(("3700").equalsIgnoreCase(strMinOrderStatus)||Double.parseDouble(strMinOrderStatus)>3700){
					printLogs("Order is fully Shipped");
					fstrExtnPostAuthSequenceNo=strExtnPostAuthCount;
				}					
				
				printLogs("ExtnPostAuthSequenceNo: "+fstrExtnPostAuthSequenceNo);
				transReq.setPostAuthSequenceNo(fstrExtnPostAuthSequenceNo);				

				Document transRequestDoc =vsiAurusAuth.createTransReqAPiInput(transReq);
				
				Document  eleExtendedFieldsDoc=recordExtnPaymentData(fstrExtnPostAuthSequenceNo,strExtnPostAuthCount,aurusCI);
				printLogs("eleExtendedFieldsDoc ="+SCXmlUtil.getString(eleExtendedFieldsDoc));					
				outStruct.eleExtendedFields=eleExtendedFieldsDoc;						
				
				printLogs("Now inserting request in Payment Record table");
				printLogs("Charge Request "+SCXmlUtil.getString(transRequestDoc));
				
				addDataToPaymentRecordTable(env,transRequestDoc,"Aurus Charge Request",strOrderHeaderKey);

				Document outDoc =vsiAurusAuth.aurusTransRequestAPI(transRequestDoc);				
				
				printLogs("Now inserting response in Payment Record table");
				printLogs("Charge Response "+SCXmlUtil.getString(outDoc));
				
				addDataToPaymentRecordTable(env,outDoc,"Aurus Charge Response",strOrderHeaderKey);

				//Printing the Response

				HashMap<String, String> preAuthResponse = vsiAurusAuth.parseTransReponseOutDoc(outDoc);

				//parsing the response
				if (preAuthResponse.containsKey("ResponseText")) {
						
						printLogs("Aurus Charge Response:"+preAuthResponse.toString());
	
						String responseCode=preAuthResponse.get("ResponseCode");					
						String processorResponseCode=preAuthResponse.get("ProcessorResponseCode");
						String approvalCode=preAuthResponse.get("ApprovalCode");
						
					if (responseCode.equalsIgnoreCase("00000")) {  //Approval					
						
						outStruct.authorizationId = authorizationIdStr;
						outStruct.authorizationAmount = inStruct.requestAmount;					
						outStruct.retryFlag = "N";				
						
						outStruct.authCode = approvalCode;
						
						printLogs("Processing Aurus Charge Approval Response Done");
						
					}else if(aurusRetryErrCode.containsKey(responseCode)||processorRetryErrCode.containsKey(processorResponseCode)) {  //retry
						Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
						Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
						Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						
						String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
	
						Calendar calendar = Calendar.getInstance();
						int retMinutes = 10 ; //10 default
						retMinutes = Integer.parseInt(retryMinutes);
						calendar.add(Calendar.MINUTE,retMinutes);
						outStruct.collectionDate=calendar.getTime();					
						outStruct.retryFlag="Y";
						
					if(responseCode.equalsIgnoreCase("71012")) {
						initAESDK();
					}
						
						printLogs("Processing Aurus Charge Retry Response Done");
					}else {   //Decline
						String errorCodeStr = "ChargeFail";
						String errorMessage = "ChargeFail";
						String orderNoStr = inStruct.orderNo;
						String errorMessageStr = errorMessage;
						
						creditCardAlert(env, errorCodeStr, errorMessageStr,
								orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
						
						applyPaymentErrorHold(env, strOrderHeaderKey);
						
						printLogs("Processing Aurus Charge Decline Response Done");
					}					
				}
			}
		}	
		
	catch (Exception Ex) {
		Ex.printStackTrace();
		
		Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
		Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
		Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
		
		String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

		Calendar calendar = Calendar.getInstance();
		int retMinutes = 10 ; //10 default
		retMinutes = Integer.parseInt(retryMinutes);
		calendar.add(Calendar.MINUTE,retMinutes);
		outStruct.collectionDate=calendar.getTime();					
		outStruct.retryFlag="Y";			
	}
	}
	
	private HashMap<String, String> DoAuthorizeAurusCharge(
			YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, String aurusCI, String aurusOOT, boolean isTNSDecommission, boolean isAurusOrder, YFSEnvironment env, 
			boolean isTNSSwitch, String strEntryType, String strOrderType) {
		
	printLogs("================Inside DoAuthorizeAurusCharge================================");
	
	String strOrderHeaderKey = inStruct.orderHeaderKey;
	String strOrderNo = inStruct.orderNo;
	
	//OMS-2445 -- Start
	String strQueueId="VSI_CREDITCARD_AUTH_CHARGE_ALERT";
	//OMS-2445 -- End

	
	HashMap<String, String> authResponse =  new HashMap<String, String>();
	
	try {

		ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");

		HashMap<String, String> aurusMandateParam= new HashMap<String, String>();		

		if(!alCommonCodeList.isEmpty()){
			for(int i=0;i<alCommonCodeList.size() ;i++) {
				Element eleCommonCode=alCommonCodeList.get(i);
				String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
					
					if(strCodeValue.startsWith("TransactionType")) {
						if(strCodeValue.indexOf("PreAuth")>0) {
							aurusMandateParam.put("TransactionType", strShortdecValue);
						}
					}if(strCodeValue.startsWith("SubTransType")) {
						if(strCodeValue.indexOf("ReAuth")>0) {
							aurusMandateParam.put("SubTransType", strShortdecValue);
						}
					}else {
						aurusMandateParam.put(strCodeValue, strShortdecValue);	
					}
					
				}
			}
		}
		
		ArrayList<Element> aurusRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_RETRY", null, "DEFAULT");
		
		HashMap<String, String> aurusRetryErrCode= new HashMap<String, String>();
		
		if(!aurusRetryErrCodeCommonCodeList.isEmpty()){
			for(int i=0;i<aurusRetryErrCodeCommonCodeList.size() ;i++) {
				Element eleCommonCode=aurusRetryErrCodeCommonCodeList.get(i);
				String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
					//							System.out.println(strCodeValue + " "+strShortdecValue);
					aurusRetryErrCode.put(strCodeValue, strShortdecValue);
				}
			}
		}
       ArrayList<Element> processorRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_PROCESSOR_RETRY", null, "DEFAULT");
		
		HashMap<String, String> processorRetryErrCode= new HashMap<String, String>();
		
		if(!processorRetryErrCodeCommonCodeList.isEmpty()){
			for(int i=0;i<processorRetryErrCodeCommonCodeList.size() ;i++) {
				Element eleCommonCode=processorRetryErrCodeCommonCodeList.get(i);
				String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
				if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
					//							System.out.println(strCodeValue + " "+strShortdecValue);
					processorRetryErrCode.put(strCodeValue, strShortdecValue);
				}
			}
		}



		String transactionIdStr = null;
		String Orderid = null;
		String merchantId = null;			
		
		//OMS-1635 : Start
		
		String currencyCodestr = "840";

		String requestAmountStr=Double.toString(inStruct.requestAmount);
		
		String tokenIdStr = inStruct.creditCardNo;
		if(inStruct.paymentReference2 ==null && inStruct.paymentReference3 == null) {
			tokenIdStr=inStruct.creditCardNo; //ott
		}else if(inStruct.paymentReference2 !=null){
			tokenIdStr=inStruct.paymentReference2;; //CI
		}

		String expDate = inStruct.creditCardExpirationDate;
		
		String AdressLine1=inStruct.billToAddressLine1;
		
		//OMS-2367 -- Start
		String strBillFirstName = inStruct.billToFirstName;
		String strBillLastName = inStruct.billToLastName;
		String strBillEmailID = inStruct.billToEmailId;			
		String City=inStruct.billToCity;
		String ZipCode=inStruct.billToZipCode;
		String State=inStruct.billToState;
		//OMS-2367 -- End

		String country=inStruct.billToCountry;

		// change 2 char country to 3 char
		if(country.length() == 2) {
			country = VSIUtils.getCountryCode(env, country);
		}
		//jira 803

		String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
		transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
		Orderid = inStruct.chargeTransactionKey+tranNumber;
		
		if(transactionIdStr.length()>13) {
			transactionIdStr = transactionIdStr.substring(transactionIdStr.length() - 13);
		}

		VSIPreAuthAESDKFunction vsiAurusAuth=new VSIPreAuthAESDKFunction();
		TransRequestObject transReq = new TransRequestObject();
				
		String oneTToken=inStruct.creditCardNo;
		String cardI =aurusCI;
		String oneOToken =aurusOOT;	
		
		printLogs("isAurusOrder = "+isAurusOrder);
		printLogs("isTNSDecommission == "+isTNSDecommission);
		printLogs("oneTimeToken = "+oneTToken);
		printLogs("cardIdentifier == "+cardI);
		printLogs("oneOrderToken == "+oneOToken);
		
		//authid is blank and TNSOrder 
		//authid is blank and AurusOrder
		//ci is blank and TNSOrder
		//ci is blank and AurusOrder
		//authid and ci both blank and TNSOrder
		//authid and ci both blank and AurusOrder
		if(!isAurusOrder && isTNSSwitch) {
			//set CRMToken with paymentReference2 and not set oot , ci and ott
			if((cardI==null || (cardI!=null && cardI.length()==0))) {
				transReq.setCrmToken(inStruct.paymentReference2);
			}
			else{
				transReq.setCardIdentifier(cardI);
			}
		}else if(!isAurusOrder && isTNSDecommission){
			if(YFCCommon.isVoid(cardI)){				
				return authResponse;
			}
			else{
				transReq.setCardIdentifier(cardI);
			}			
		}
		else {
			if((cardI==null || (cardI!=null && cardI.length()==0)))// CIand OOT not present 
				transReq.setOneTimeToken(oneTToken);
			else{
				transReq.setCardIdentifier(cardI); //reAuth
				transReq.setSubTransType(aurusMandateParam.get("SubTransType")); //reAuth
			}
		}
		
		transReq.setTransactionTotal(requestAmountStr); 
		transReq.setCurrencyCode(currencyCodestr);
		transReq.setMerchantIdentifier(merchantId);
		
		//OMS-2367 -- Start
		if(!YFCCommon.isVoid(strBillFirstName)){
			transReq.setBillingFirstName(strBillFirstName);
		}
		if(!YFCCommon.isVoid(strBillLastName)){
			transReq.setBillingLastName(strBillLastName);
		}
		//OMS-2367 -- End
		
		String strExpDate=expDate.substring(0, 2)+expDate.substring(5);
		transReq.setCardExpiryDate(strExpDate);
		
		//OMS-2367 -- Start
		if(!YFCCommon.isVoid(AdressLine1)){
			transReq.setBillingAddressLine1(AdressLine1);
		}
		if(!YFCCommon.isVoid(City)){
			transReq.setBillingCity(City);
		}
		if(!YFCCommon.isVoid(State)){
			transReq.setBillingState(State);
		}			
		if(!YFCCommon.isVoid(ZipCode)){
			transReq.setBillingZip(ZipCode);
		}
		if(!YFCCommon.isVoid(country)){
			transReq.setBillingCountry(country);
		}
		if(!YFCCommon.isVoid(strBillEmailID)){
			transReq.setBillingEmailId(strBillEmailID);
		}
		//OMS-2367 -- End

		transReq.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
		transReq.setaDSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
		transReq.setCorpID(aurusMandateParam.get("CorpID"));
		transReq.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
		transReq.setStoreId(aurusMandateParam.get("StoreId"));
		transReq.setTerminalId(aurusMandateParam.get("TerminalId"));
		transReq.setTransactionType(aurusMandateParam.get("TransactionType"));
		
		//OMS-2437 -- Start
		if(VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType) || VSIConstants.POS_ENTRY_TYPE.equals(strEntryType)){
			printLogs("POS Order No before trimming: "+strOrderNo);
			String strOrderNoPOS = strOrderNo.substring(0, 16);
			printLogs("POS Order No after trimming: "+strOrderNoPOS);
			transReq.setInvoiceNumber(strOrderNoPOS);
		}else{
			transReq.setInvoiceNumber(strOrderNo);
		}			
		//OMS-2437 -- End
		
		//OMS-2392 -- Start
		String strReferenceNumber = getReferenceNumber(strOrderNo);			
		transReq.setReferenceNumber(strReferenceNumber);
		//OMS-2392 -- End
		
		//OMS-2408 -- Start
		HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
		transDateAndTime=getTransactionDateAndTime();			
		String strTransDate = transDateAndTime.get("TransactionDate");
		String strTransTime = transDateAndTime.get("TransactionTime");
		transReq.setTransactionDate(strTransDate);
		transReq.setTransactionTime(strTransTime);
		//OMS-2408 -- End

		Document transRequestInputDoc =vsiAurusAuth.createTransReqAPiInput(transReq);
		
		printLogs("Now inserting request in Payment Record table");
		printLogs("PreAuth Req "+SCXmlUtil.getString(transRequestInputDoc));
		
		addDataToPaymentRecordTable(env,transRequestInputDoc,"Aurus Authorize Request",strOrderHeaderKey);

		Document outDoc =vsiAurusAuth.aurusTransRequestAPI(transRequestInputDoc);
		// insert Auth record
		
		printLogs("Now inserting response in Payment Record table");
		printLogs("PreAuth Response "+SCXmlUtil.getString(outDoc));
		
		addDataToPaymentRecordTable(env,outDoc,"Aurus Authorize Response",strOrderHeaderKey);

		//Printing the Response

		HashMap<String, String> preAuthResponse = vsiAurusAuth.parseTransReponseOutDoc(outDoc);

		//parsing the response
		if (preAuthResponse.containsKey("ResponseText")) {
			
			printLogs("Aurus Response:"+preAuthResponse.toString());
			
			String transactionIdentifier=preAuthResponse.get("TransactionIdentifier");			
			String responseCode=preAuthResponse.get("ResponseCode");
			String cardIdentifier = preAuthResponse.get("CardIdentifier");
			String processorResponseCode=preAuthResponse.get("ProcessorResponseCode");

			if (responseCode.equalsIgnoreCase("00000")) {  //Approval					
				
				authResponse.put("TransactionID", transactionIdentifier);
				authResponse.put("CardIdentifier", cardIdentifier);
				
			}else if(aurusRetryErrCode.containsKey(responseCode)||processorRetryErrCode.containsKey(processorResponseCode)) {  //retry
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
				Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				
				String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();				
				outStruct.retryFlag="Y";	
				
				if(responseCode.equalsIgnoreCase("71012")) {
						initAESDK();			
				}
				
			}else {   //Decline
				String errorCodeStr = "AuthFail";
				String errorMessage = "AuthFail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;	

				String strAQRiskified = responseCode.toString();
				String strRiskifiedFlag=VSIConstants.FLAG_N;
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				
				// raising holds and cancelling orders
				authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag,null,null);
			}
		}
	
} catch (Exception Ex) {
	Ex.printStackTrace();
	throw new YFSException();
}		
		return authResponse;
	}
	
	private Document recordExtnPaymentData(String fstrExtnPostAuthSequenceNo,
		String strExtnPostAuthCount, String aurusCI) {
	
	printLogs("Inside recordExtnPaymentData method");
	Document docOrder = SCXmlUtil.createDocument("PaymentMethod");
	Element eleOrder = docOrder.getDocumentElement();
	
	Element eleExtn = SCXmlUtil.createChild(eleOrder, "Extn");
	eleExtn.setAttribute("ExtnPostAuthSequenceNo", fstrExtnPostAuthSequenceNo);
	eleExtn.setAttribute("ExtnPostAuthCount", strExtnPostAuthCount);
	eleExtn.setAttribute("ExtnAurusCI", aurusCI);
	
	printLogs("Exiting recordExtnPaymentData method with output "+SCXmlUtil.getString(docOrder));
	return docOrder;	

		
	}
	
	private void doVoidAurus(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env,
			boolean isAurusOrder, boolean isTNSDecommission, boolean isTNSSwitch, String strEntryType, String strOrderType) {
		
		printLogs("================Inside doVoidAurus================================");
				
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String strOrderNo = inStruct.orderNo;

		try {
			
			if(!isAurusOrder){
				printLogs("Not an Aurus order and hence Void request will not be triggered");
				outStruct.authorizationId = inStruct.authorizationId;
				outStruct.authorizationAmount = inStruct.requestAmount;
				outStruct.retryFlag = "N";
			}else{

			ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");

			HashMap<String, String> aurusMandateParam= new HashMap<String, String>();

			if(!alCommonCodeList.isEmpty()){
				for(int i=0;i<alCommonCodeList.size() ;i++) {
					Element eleCommonCode=alCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);					
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						
						if(strCodeValue.startsWith("TransactionType")) {
							if(strCodeValue.indexOf("Void")>0) {
								aurusMandateParam.put("TransactionType", strShortdecValue);
							}
						}else {
							aurusMandateParam.put(strCodeValue, strShortdecValue);	
						}
					}
				}
			}

			ArrayList<Element> aurusRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_RETRY", null, "DEFAULT");
			
			HashMap<String, String> aurusRetryErrCode= new HashMap<String, String>();
			
			if(!aurusRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<aurusRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=aurusRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);					
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						aurusRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}
			ArrayList<Element> processorRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_PROCESSOR_RETRY", null, "DEFAULT");
			
			HashMap<String, String> processorRetryErrCode= new HashMap<String, String>();
			
			if(!processorRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<processorRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=processorRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						//							System.out.println(strCodeValue + " "+strShortdecValue);
						processorRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}
			String transactionIdStr = null;
			String Orderid = null;
			String merchantId = null;
						
			//OMS-1635 : Start
			
			String currencyCodestr = "840";

			double dRequestAmountNeg  = inStruct.requestAmount;
			double dRequestAmount = Math.abs(dRequestAmountNeg);
			String requestAmountStr=Double.toString(dRequestAmount);  
			
			String firstName = inStruct.firstName;
			String lastName = inStruct.lastName;
			String expDate = inStruct.creditCardExpirationDate;
			String AdressLine1=inStruct.billToAddressLine1;
			
			String country=inStruct.billToCountry;

			// change 2 char country to 3 char
			if(country.length() == 2) {
				country = VSIUtils.getCountryCode(env, country);
			}
			//jira 803		

			String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
			transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
			Orderid = inStruct.chargeTransactionKey+tranNumber;
			
			if(transactionIdStr.length()>13) {
				transactionIdStr = transactionIdStr.substring(transactionIdStr.length() - 13);
			}

			String strAuthorizationId = inStruct.authorizationId;


			VSIPreAuthAESDKFunction vsiAurusAuth=new VSIPreAuthAESDKFunction();
			TransRequestObject transReq = new TransRequestObject();
			
			transReq.setOrigTransactionIdentifier(strAuthorizationId);  
			transReq.setTransactionTotal(requestAmountStr); 
			transReq.setCurrencyCode(currencyCodestr);
			transReq.setMerchantIdentifier(merchantId);
			transReq.setBillingFirstName(firstName);
			transReq.setBillingLastName(lastName);
			String strExpDate=expDate.substring(0, 2)+expDate.substring(5);
			transReq.setCardExpiryDate(strExpDate);
			transReq.setBillingAddressLine1(AdressLine1);

			transReq.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
			transReq.setaDSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
			transReq.setCorpID(aurusMandateParam.get("CorpID"));
			transReq.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
			transReq.setStoreId(aurusMandateParam.get("StoreId"));
			transReq.setTerminalId(aurusMandateParam.get("TerminalId"));
			transReq.setTransactionType(aurusMandateParam.get("TransactionType"));
			
			//OMS-2437 -- Start
			if(VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType) || VSIConstants.POS_ENTRY_TYPE.equals(strEntryType)){
				printLogs("POS Order No before trimming: "+strOrderNo);
				String strOrderNoPOS = strOrderNo.substring(0, 16);
				printLogs("POS Order No after trimming: "+strOrderNoPOS);
				transReq.setInvoiceNumber(strOrderNoPOS);
			}else{
				transReq.setInvoiceNumber(strOrderNo);
			}			
			//OMS-2437 -- End
			
			//OMS-2392 -- Start
			String strReferenceNumber = getReferenceNumber(strOrderNo);			
			transReq.setReferenceNumber(strReferenceNumber);
			//OMS-2392 -- End
			
			//OMS-2408 -- Start
			HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
			transDateAndTime=getTransactionDateAndTime();			
			String strTransDate = transDateAndTime.get("TransactionDate");
			String strTransTime = transDateAndTime.get("TransactionTime");
			transReq.setTransactionDate(strTransDate);
			transReq.setTransactionTime(strTransTime);
			//OMS-2408 -- End

			Document transRequestDoc =vsiAurusAuth.createTransReqAPiInput(transReq);		
			
			printLogs("Now inserting request in Payment Record table");
			printLogs("Void Req "+SCXmlUtil.getString(transRequestDoc));
			
			addDataToPaymentRecordTable(env,transRequestDoc,"Aurus Void Request", strOrderHeaderKey);

			Document outDoc =vsiAurusAuth.aurusTransRequestAPI(transRequestDoc);
			
			printLogs("Now inserting response in Payment Record table");
			printLogs("Void Response "+SCXmlUtil.getString(outDoc));
			
			if(!YFCCommon.isVoid(outDoc)){
				
				addDataToPaymentRecordTable(env,outDoc, "Aurus Void Response", strOrderHeaderKey);
	
				//Printing the Response
	
				HashMap<String, String> voidResponse = vsiAurusAuth.parseTransReponseOutDoc(outDoc);
	
				//parsing the response
				if (voidResponse.containsKey("ResponseText")) {
					
					printLogs("Aurus Void Response:"+voidResponse.toString());
					
					String responseText=voidResponse.get("ResponseText");
					String responseCode=voidResponse.get("ResponseCode");
					String processorResponseCode=voidResponse.get("ProcessorResponseCode");
					String approvalCode=voidResponse.get("ApprovalCode");
					
					if ("APPROVAL".equalsIgnoreCase(responseText) && responseCode.equalsIgnoreCase("00000")) {  //Approval
						
						outStruct.authorizationId = inStruct.authorizationId;
						outStruct.authorizationAmount = inStruct.requestAmount;
						outStruct.retryFlag = "N";
						
						outStruct.authCode = approvalCode;
						
						printLogs("Approval Void Response processing done");
						
					}else if(aurusRetryErrCode.containsKey(responseCode)||processorRetryErrCode.containsKey(processorResponseCode)) {  //retry
						Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_RETRY_TIME");
						Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
						Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
						
						String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

						Calendar calendar = Calendar.getInstance();
						int retMinutes = 10 ; 
						retMinutes = Integer.parseInt(retryMinutes);
						calendar.add(Calendar.MINUTE,retMinutes);
						outStruct.collectionDate=calendar.getTime();						
						outStruct.retryFlag="Y";	
						
						if(responseCode.equalsIgnoreCase("71012")) {
							initAESDK();				
						}
					}
					else{
						outStruct.authorizationId = inStruct.authorizationId;
						outStruct.authorizationAmount = inStruct.requestAmount;
						outStruct.retryFlag = "N";
					}
				}	
				else{
					outStruct.authorizationId = inStruct.authorizationId;
					outStruct.authorizationAmount = inStruct.requestAmount;
					outStruct.retryFlag = "N";
				}
			}
		}

		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}		
	}

	public boolean isAurusOrder(Document getOrderListOP) {
		boolean isAurusOrder = false;
		Element eleOrder = SCXmlUtil.getChildElement(getOrderListOP.getDocumentElement(), VSIConstants.ELE_ORDER);
		Element eleExtn = SCXmlUtil.getChildElement(eleOrder, "Extn");
		String ExtnAurusTokenFlag =eleExtn.getAttribute("ExtnAurusToken");

		if(ExtnAurusTokenFlag.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
			isAurusOrder=true;
		}
		return isAurusOrder;
	}

	void doAuthorizationAurus(YFSExtnPaymentCollectionInputStruct inStruct, YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env,
			String aurusCI,String aurusOOT ,boolean isAurusOrder,boolean isTNSDecommission, boolean isTNSSwitch, String strEntryType, boolean isDraftOrder, String strOrderType) {
		
		printLogs("================Inside doAuthorizationAurus================================");

		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String strOrderNo = inStruct.orderNo;
		
		//OMS-2445 -- Start
		String strQueueId="VSI_CREDITCARD_AUTH_ALERT";
		//OMS-2445 -- End
		
		boolean bNoCI = false;


		try {

			ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_PARAMS", null, "DEFAULT");

			HashMap<String, String> aurusMandateParam= new HashMap<String, String>();

			if(!alCommonCodeList.isEmpty()){
				for(int i=0;i<alCommonCodeList.size() ;i++) {
					Element eleCommonCode=alCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strLongdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){

						if(strCodeValue.startsWith("TransactionType")) {
							if(strCodeValue.indexOf("PreAuth")>0) {
								aurusMandateParam.put("TransactionType", strShortdecValue);
							}
						}if(strCodeValue.startsWith("SubTransType")) {
							if(strCodeValue.indexOf("ReAuth")>0) {
								aurusMandateParam.put("SubTransType", strShortdecValue);
							}
						}else {
							aurusMandateParam.put(strCodeValue, strShortdecValue);	
						}
						
					}
				}
			}
			
			ArrayList<Element> aurusRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_AURUS_RETRY", null, "DEFAULT");
			
			HashMap<String, String> aurusRetryErrCode= new HashMap<String, String>();
			
			if(!aurusRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<aurusRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=aurusRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strLongdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						aurusRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}
			ArrayList<Element> processorRetryErrCodeCommonCodeList = VSIUtils.getCommonCodeListWithCodeType(env, "VSI_PROCESSOR_RETRY", null, "DEFAULT");
			
			HashMap<String, String> processorRetryErrCode= new HashMap<String, String>();
			
			if(!processorRetryErrCodeCommonCodeList.isEmpty()){
				for(int i=0;i<processorRetryErrCodeCommonCodeList.size() ;i++) {
					Element eleCommonCode=processorRetryErrCodeCommonCodeList.get(i);
					String strShortdecValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strCodeValue=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
					if(!YFCCommon.isStringVoid(strShortdecValue) && !YFCCommon.isStringVoid(strCodeValue)){
						//							System.out.println(strCodeValue + " "+strShortdecValue);
						processorRetryErrCode.put(strCodeValue, strShortdecValue);
					}
				}
			}



			String transactionIdStr = null;
			String Orderid = null;
			String merchantId = null;
			String generateId = null;
			
			String entCode = "DEFAULT";
			//OMS-1635 : Start
			String strIsNewOrder = checkNewOrder(env, strOrderHeaderKey);
			String strDefaultPrefixReqd = getCommonCodeLongDescriptionByCodeValue(env,entCode,"DefaultMerchPrefixActive");

			DecimalFormat df1 = new DecimalFormat("#,###,###.00");
			String currencyCodestr = "840";

			String requestAmountStr=Double.toString(inStruct.requestAmount);  
			String sourceOfFundstype = "CARD";


//			String tokenIdStr = null;
			String tokenIdStr = inStruct.creditCardNo;
			if(inStruct.paymentReference2 ==null && inStruct.paymentReference3 == null) {
				tokenIdStr=inStruct.creditCardNo; //ott
			}else if(inStruct.paymentReference2 !=null){
				tokenIdStr=inStruct.paymentReference2;; //CI
			}

			String firstName = inStruct.firstName;
			String lastName = inStruct.lastName;
			String expDate = inStruct.creditCardExpirationDate;
			//OMS-2320 -- Start
			String strBillFirstName = inStruct.billToFirstName;
			String strBillLastName = inStruct.billToLastName;
			String strBillEmailID = inStruct.billToEmailId;
			//OMS-2320 -- End
			String AdressLine1=inStruct.billToAddressLine1;
			String City=inStruct.billToCity;
			String ZipCode=inStruct.billToZipCode;
			String State=inStruct.billToState;

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

			//				String concExpDt = expMonth + expYr;
			String concExpDt = expDate;
			//System.out.println("new Exp dt is " + concExpDt);


			String tranNumber = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CC_NO);
			transactionIdStr = inStruct.chargeTransactionKey+tranNumber;
			Orderid = inStruct.chargeTransactionKey+tranNumber;
			
			if(transactionIdStr.length()>13) {
				transactionIdStr = transactionIdStr.substring(transactionIdStr.length() - 13);
			}

			//transactionIdStr  trans id


			/**
			 * HashMap nvp =
			 * VSICreditCardFunction.DoAuthorization(Orderid,merchantId
			 * ,apiUsername,apiPassword,requestAmountStr,
			 * currencyCodestr,transactionIdStr,sourceOfFundstype,tokenIdStr);
			 **/





			VSIPreAuthAESDKFunction vsiAurusAuth=new VSIPreAuthAESDKFunction();
			TransRequestObject transReq = new TransRequestObject();
			//			transReq.setOneTimeToken(inStruct.paymentReference3);
			
			
//			String oneTToken=inStruct.creditCardNo;
			String oneTToken=inStruct.paymentReference3;

			String cardI =aurusCI;
			String oneOToken =aurusOOT;
			
			printLogs("isAurusOrder = "+isAurusOrder);
			printLogs("isTNSDecommission == "+isTNSDecommission);
			printLogs("oneTimeToken = "+oneTToken);
			printLogs("cardIdentifier == "+cardI);
			printLogs("oneOrderToken == "+oneOToken);
			
			//Harshit: Over here we need to add a condition to use CI if CI is present.
			if(!isAurusOrder && isTNSSwitch ) {
				//set CRMToken with paymentReference2 and not set oot , ci and ott
				if((cardI==null || (cardI!=null && cardI.length()==0))) {
					transReq.setCrmToken(inStruct.paymentReference2);
				}
				else{
					transReq.setCardIdentifier(cardI);
				}
			} else if(!isAurusOrder && isTNSDecommission){
				if(YFCCommon.isVoid(cardI)){
					bNoCI = true;
				}
				else{
					transReq.setCardIdentifier(cardI);
				}				
			}
			else {
				if((cardI==null || (cardI!=null && cardI.length()==0)))// CIand OOT not present 
					transReq.setOneTimeToken(oneTToken);
				else{
					transReq.setCardIdentifier(cardI); //reAuth
//					transReq.setOneOrderToken(oneOToken); //reAuth
					transReq.setSubTransType(aurusMandateParam.get("SubTransType")); //reAuth
				}
			}
			

//			transReq.setOrigTransactionIdentifier(transactionIdStr);  // In Normal  PreAuth OrigTransactionIdentifier is not required
			transReq.setTransactionTotal(requestAmountStr); 
			transReq.setCurrencyCode(currencyCodestr);
			transReq.setMerchantIdentifier(merchantId);
			//OMS-2320 -- Start
			if(!YFCCommon.isVoid(strBillFirstName)){
				transReq.setBillingFirstName(strBillFirstName);
			}
			if(!YFCCommon.isVoid(strBillLastName)){
				transReq.setBillingLastName(strBillLastName);
			}
			//OMS-2320 -- End
			String strExpDate=expDate.substring(0, 2)+expDate.substring(5);
			transReq.setCardExpiryDate(strExpDate);
			//OMS-2320 -- Start
			if(!YFCCommon.isVoid(AdressLine1)){
				transReq.setBillingAddressLine1(AdressLine1);
			}
			if(!YFCCommon.isVoid(City)){
				transReq.setBillingCity(City);
			}
			if(!YFCCommon.isVoid(State)){
				transReq.setBillingState(State);
			}			
			if(!YFCCommon.isVoid(ZipCode)){
				transReq.setBillingZip(ZipCode);
			}
			if(!YFCCommon.isVoid(country)){
				transReq.setBillingCountry(country);
			}
			if(!YFCCommon.isVoid(strBillEmailID)){
				transReq.setBillingEmailId(strBillEmailID);
			}			
			//OMS-2320 -- End
			transReq.setMerchantIdentifier(aurusMandateParam.get("MerchantIdentifier"));
			transReq.setaDSDKSpecVer(aurusMandateParam.get("ADSDKSpecVer"));
			transReq.setCorpID(aurusMandateParam.get("CorpID"));
			transReq.setLanguageIndicator(aurusMandateParam.get("LanguageIndicator"));
			transReq.setStoreId(aurusMandateParam.get("StoreId"));
			transReq.setTerminalId(aurusMandateParam.get("TerminalId"));
			transReq.setTransactionType(aurusMandateParam.get("TransactionType"));
			
			//OMS-2437 -- Start
			if(VSIConstants.ATTR_ORDER_TYPE_POS.equals(strOrderType) || VSIConstants.POS_ENTRY_TYPE.equals(strEntryType)){
				printLogs("POS Order No before trimming: "+strOrderNo);
				String strOrderNoPOS = strOrderNo.substring(0, 16);
				printLogs("POS Order No after trimming: "+strOrderNoPOS);
				transReq.setInvoiceNumber(strOrderNoPOS);
			}else{
				transReq.setInvoiceNumber(strOrderNo);
			}			
			//OMS-2437 -- End
			
			//OMS-2392 -- Start
			String strReferenceNumber = getReferenceNumber(strOrderNo);			
			transReq.setReferenceNumber(strReferenceNumber);
			//OMS-2392 -- End
			
			//OMS-2408 -- Start
			HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
			transDateAndTime=getTransactionDateAndTime();			
			String strTransDate = transDateAndTime.get("TransactionDate");
			String strTransTime = transDateAndTime.get("TransactionTime");
			transReq.setTransactionDate(strTransDate);
			transReq.setTransactionTime(strTransTime);
			//OMS-2408 -- End
			
			if(!bNoCI){

			Document transRequestInputDoc =vsiAurusAuth.createTransReqAPiInput(transReq);

			printLogs("Now inserting request in Payment Record table");
			printLogs("PreAuth Req "+SCXmlUtil.getString(transRequestInputDoc));			
			
			
			addDataToPaymentRecordTable(env,transRequestInputDoc,"Aurus Authorize Request",strOrderHeaderKey);

			
			Document outDoc =vsiAurusAuth.aurusTransRequestAPI(transRequestInputDoc);
			// insert Auth recodr

			printLogs("Now inserting response in Payment Record table");
			printLogs("PreAuth Response "+SCXmlUtil.getString(outDoc));
			
			addDataToPaymentRecordTable(env,outDoc,"Aurus Authorize Response",strOrderHeaderKey);

			//Printing the Response

			HashMap<String, String> preAuthResponse = vsiAurusAuth.parseTransReponseOutDoc(outDoc);			

			//pasrsing the rsponse
			if (preAuthResponse.containsKey("ResponseText")) {

				printLogs("Aurus Response:"+preAuthResponse.toString());

				String aurusPayTicketNum=preAuthResponse.get("AurusPayTicketNum");
				String cardNumber=preAuthResponse.get("CardNumber");
				String cardIdentifier=preAuthResponse.get("CardIdentifier");
				String processorToken=preAuthResponse.get("ProcessorToken");
				String cardExpiryDate=preAuthResponse.get("CardExpiryDate");
				String cardEntryMode=preAuthResponse.get("CardEntryMode");
				String receiptToken=preAuthResponse.get("ReceiptToken");
				String transactionToken=preAuthResponse.get("TransactionToken");
				String batchNumber=preAuthResponse.get("BatchNumber");
				String referenceNumber=preAuthResponse.get("ReferenceNumber");
				String processorResponseCode=preAuthResponse.get("ProcessorResponseCode");
				String responseText=preAuthResponse.get("ResponseText");
				String totalApprovedAmount=preAuthResponse.get("TotalApprovedAmount");
				String transactionType=preAuthResponse.get("TransactionType");
				String auruspayTransactionId=preAuthResponse.get("AuruspayTransactionId");
				String transactionIdentifier=preAuthResponse.get("TransactionIdentifier");
				String cardType=preAuthResponse.get("CardType");
				String transactionDate=preAuthResponse.get("TransactionDate");
				String responseCode=preAuthResponse.get("ResponseCode");
				String approvalCode=preAuthResponse.get("ApprovalCode");
				String transactionAmount=preAuthResponse.get("TransactionAmount");
				String transactionTime=preAuthResponse.get("TransactionTime");
				String processorResponseText=preAuthResponse.get("ProcessorResponseText");
				String processorTokenRespText=preAuthResponse.get("ProcessorTokenRespText");
				String aurusProcessorId=preAuthResponse.get("AurusProcessorId");
				String oneTimeToken=preAuthResponse.get("OneTimeToken");
				String storeId=preAuthResponse.get("StoreId");
				String merchantIdentifier=preAuthResponse.get("MerchantIdentifier");
				String oneOrderToken=preAuthResponse.get("OneOrderToken");
				String cVVResult=preAuthResponse.get("CVVResult");
				String terminalId=preAuthResponse.get("TerminalId");
				String authAVSResult=preAuthResponse.get("AuthAVSResult");
				String referralNUM=preAuthResponse.get("ReferralNUM");



				if (responseCode.equalsIgnoreCase("00000")) {  //Aproval
				printLogs("preAuth -- Approval");
					String AvsResp=null;
					String AvsRespCode=null;
					String strAVSgatway=null;
					String strAck = responseText;
					//System.out.println("Autorization is" + strAck);
					String transactionId = transactionIdentifier;
					//requestAmountStr = transactionAmount;


					String transactionSource = "";

					double authAmountStr = Double.parseDouble(requestAmountStr);
					outStruct.authorizationId = transactionId;
					outStruct.authorizationAmount = authAmountStr;
					// outStruct.collectionDate = new Date();
					//outStruct.executionDate = new Date();
					outStruct.retryFlag = "N";
					
					outStruct.authCode = approvalCode;

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
					
					outStruct.tranAmount = authAmountStr;
					
//					Document  eleExtendedFieldsDoc = outStruct.eleExtendedFields;
					Document  eleExtendedFieldsDoc=recordAdditionalAuthData(cardIdentifier,oneOrderToken,referralNUM);
					printLogs("eleExtendedFieldsDoc ="+SCXmlUtil.getString(eleExtendedFieldsDoc));
					outStruct.eleExtendedFields=eleExtendedFieldsDoc;

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

					//AurusAuthRecordinsert

					// Inserting records for authorization for charge
					printLogs("Now inserting values  in aurusAuthRecords table");

											aurusTnsAuthRecords(env, strOrderHeaderKey, transactionIdentifier,
													referenceNumber, aurusPayTicketNum,
													cVVResult, authAVSResult,processorResponseCode,responseCode);



					
					printLogs("Now inserting values  in aurusAuthRecords table done");
					
				}else if((aurusRetryErrCode.containsKey(responseCode)||processorRetryErrCode.containsKey(processorResponseCode)) &&
						!(isDraftOrder && VSIConstants.ENTRYTYPE_CC.equals(strEntryType))) {  //retry
					printLogs("Now inside retry for auth");
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
					
					if(responseCode.equalsIgnoreCase("71012")) {
						initAESDK();					
					}
					
				}else {   //Decline
					printLogs("Now inside Decline for auth");
					String errorCodeStr = "AuthFail";
					String errorMessage = "AuthFail";
					String orderNoStr = inStruct.orderNo;
					String errorMessageStr = errorMessage;
					// outStruct.suspendPayment="Y";
//					String strAQRiskified = nvp.get("RESPONSE.ACQUIRERCODE").toString();
					String strAQRiskified = processorResponseCode.toString();
					String strRiskifiedFlag=VSIConstants.FLAG_Y;
					Document  eleExtendedFieldsDoc=recordAdditionalAuthData(cardIdentifier,oneOrderToken,referralNUM);
					printLogs("eleExtendedFieldsDoc ="+SCXmlUtil.getString(eleExtendedFieldsDoc));
					outStruct.eleExtendedFields=eleExtendedFieldsDoc;
					// raising Credit card alerts
					creditCardAlert(env, errorCodeStr, errorMessageStr,
							orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
					// raising holds and cancelling orders
					authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag,authAVSResult,cVVResult);
				}

				//if authorization is failure
			}
//			else if (null != preAuthResponse.get("errorCode").toString())
			else{
				printLogs("Now inside else block when the Response does not contains ResponseText");
				//for time out exception, setting the flag='Y', so that agent can retry the request
				String errorCodeStr = "Aurus Connection Fail";
				String errorMessage = "Aurus Connection Fail";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				outStruct.retryFlag = "Y";
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
			}
			}else{
				printLogs("Now inside else block when Aurus CI is not present for the order");
				Document getCommonCodeListInputForRetryTime = getCommonCodeListInputForCodeType("DEFAULT","CC_REFUND_TIME");				
				Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForRetryTime);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				
				String retryMinutes = eleCOmmonCodeOut.getAttribute("CodeLongDescription");

				Calendar calendar = Calendar.getInstance();
				int retMinutes = 10 ; //10 default
				retMinutes = Integer.parseInt(retryMinutes);
				calendar.add(Calendar.MINUTE,retMinutes);
				outStruct.collectionDate=calendar.getTime();
				
				outStruct.retryFlag="Y";
				
				String errorCodeStr = "NoCIforAuthorization";
				String errorMessage = "NoCIforAuthorization";
				String orderNoStr = inStruct.orderNo;
				String errorMessageStr = errorMessage;
				
				// raising Credit card alerts
				creditCardAlert(env, errorCodeStr, errorMessageStr,
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				
				applyPaymentErrorHold(env, strOrderHeaderKey);
			}
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}



	}

	//OMS-2408 -- Start
	private HashMap<String, String> getTransactionDateAndTime() {
		printLogs("================Inside getTransactionDateAndTime================================");
		HashMap<String, String> transDateAndTime =  new HashMap<String, String>();
		Calendar calendar = Calendar.getInstance();
		String strTransDate = new SimpleDateFormat("MMddyyyy")
		.format(calendar.getTime());
		printLogs("TransactionDate is: "+strTransDate);
		String strTransTime = new SimpleDateFormat("HHmmss")
		.format(calendar.getTime());
		printLogs("TransactionTime is: "+strTransTime);
		transDateAndTime.put("TransactionDate", strTransDate);
		transDateAndTime.put("TransactionTime", strTransTime);
		printLogs("================Exiting getTransactionDateAndTime================================");
		return transDateAndTime;
	}
	//OMS-2408 -- End

	private String getReferenceNumber(String strOrderNo) {
		
		printLogs("================Inside getReferenceNumber================================");
		Calendar calendar = Calendar.getInstance();
		String sysdate = new SimpleDateFormat("yyyyMMddHHmmss")
		.format(calendar.getTime());
		printLogs("Sysdate is: "+sysdate);
		String strRefNo = strOrderNo+sysdate;
		printLogs("ReferenceNumber is: "+strRefNo);
		return strRefNo;
	}

	private void addDataToPaymentRecordTable(YFSEnvironment env,Document transRequestDoc, String ChargeType ,String strOrderHeaderKey) {
		try {
			Document doc = XMLUtil.createDocument("PaymentRecords");
			Element elePayRecrds = doc.getDocumentElement();
			elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
			elePayRecrds.setAttribute("Record", SCXmlUtil.getString(transRequestDoc));
			elePayRecrds.setAttribute("ChargeType", ChargeType);
			YIFApi api;
			api = YIFClientFactory.getInstance().getApi();
			api.executeFlow(env,"VSIPaymentRecords", doc);
		}catch (ParserConfigurationException e) {
			// TODO: handle exception
		}catch (YIFClientCreationException e) {
			// TODO: handle exception
		}catch (YFSException e) {
			// TODO: handle exception
		}catch (RemoteException e) {
			// TODO: handle exception
		}catch (Exception e) {
			// TODO: handle exception
		}
	}


	private static Document createIsDraftOrderTempleate() {
		Document docOrderList = SCXmlUtil.createDocument("OrderList");
		Element eleOrderList = docOrderList.getDocumentElement();
		Element eleOrder = SCXmlUtil.createChild(eleOrderList, "Order");
		eleOrder.setAttribute("OrderHeaderKey", "");
		eleOrder.setAttribute("DraftOrderFlag", "");
		Element eleExtn = SCXmlUtil.createChild(eleOrder, "Extn");
		eleExtn.setAttribute("ExtnAurusToken", "Y");
		return docOrderList;		
	}

	private void updateAurusFlaginOH(String orderHeaderKey ,YFSEnvironment env) {
		String orderHeaderKeyStr = orderHeaderKey;
		env.clearApiTemplates();
		try {
			Document inputChangeOrder = createChangeOrderForExtnAurusToken(orderHeaderKeyStr);
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", inputChangeOrder);
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}
	}

	private HashMap<String, String> initailAurusOrderDetail(YFSExtnPaymentCollectionInputStruct inStruct, YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {
		
				String orderHeaderKeyStr = inStruct.orderHeaderKey;
		HashMap<String, String> IsDraftOrAurus = new HashMap<String, String>();
		try {
				Document getOrderListIP = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
			getOrderListIP.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);

			Document getOrderListOP = VSIUtils.invokeAPI(env,"global/template/api/VSIGetOrderListForAurus.xml",VSIConstants.API_GET_ORDER_LIST, getOrderListIP);
			Element eleOrder = SCXmlUtil.getChildElement(getOrderListOP.getDocumentElement(), VSIConstants.ELE_ORDER);
			String draftOrderFlag =eleOrder.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
			String strEntryType = eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			//OMS-2437 -- Start
			String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			//OMS-2437 -- End
			
            String strMaxStatus=null;            		
            String strMinStatus=null; 
            
            String strExtnRefundCount=null;
			String strExtnRefundSequenceNo=null;
			String strSOHdrKey=null;
			            
            String strDocType = eleOrder.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
            
            printLogs("initailAurusOrderDetail getOrderListOP "+SCXmlUtil.getString(getOrderListOP));
            
            String extnAurusTokenFlag = null;
            
            Element eleOrderLines=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
        	Element eleFirstOrderLine = SCXmlUtil.getFirstChildElement(eleOrderLines);
        	Element eleDerivedFromOrder = SCXmlUtil.getChildElement(eleFirstOrderLine, "DerivedFromOrder");
            
            if(VSIConstants.RETURN_DOCUMENT_TYPE.equals(strDocType)){            	
            	if(!YFCCommon.isVoid(eleDerivedFromOrder)){
            		strMaxStatus=eleDerivedFromOrder.getAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS);
            		strMinStatus=eleDerivedFromOrder.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
            		Element eleDerivedExtn = SCXmlUtil.getChildElement(eleDerivedFromOrder, VSIConstants.ELE_EXTN);
                	extnAurusTokenFlag=eleDerivedExtn.getAttribute("ExtnAurusToken");
            	}
            	strSOHdrKey = eleDerivedFromOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
            	
            	Document getAurusRfndDtls = XMLUtil.createDocument("AurusRefundDetails");
        		Element eleAurusRfndDtls = getAurusRfndDtls.getDocumentElement();
        		eleAurusRfndDtls.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
        				strSOHdrKey);
        		
        		api = YIFClientFactory.getInstance().getApi();
        		Document outDoc = api.executeFlow(env,
        				"VSIGetAurusRefundDetails", getAurusRfndDtls);       		
        		
        		Element eleAursuRfndDtl = (Element) outDoc.getElementsByTagName("AurusRefundDetails").item(0);
        		if(!YFCCommon.isVoid(eleAursuRfndDtl)){
        			bIsFirstRefund = false;
        		}
        		if(!YFCCommon.isVoid(eleAursuRfndDtl)){        			
        			strExtnRefundCount = eleAursuRfndDtl.getAttribute("ExtnRefundCount");
            		strExtnRefundSequenceNo = eleAursuRfndDtl.getAttribute("ExtnRefundSequenceNo");
            		strAurusRfndDtlKey = eleAursuRfndDtl.getAttribute("AurusRefundDetailKey");
        		}        		
            }else{
            	Element eleExtn = SCXmlUtil.getChildElement(eleOrder, "Extn");
            	extnAurusTokenFlag =eleExtn.getAttribute("ExtnAurusToken");
            	strMaxStatus=eleOrder.getAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS);
            	strMinStatus=eleOrder.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
            	strSOHdrKey=orderHeaderKeyStr;
            }			
			
			NodeList nlPaymentMethod = eleOrder.getElementsByTagName("PaymentMethod");
			String strExtnPostAuthCount=null;
			String strExtnPostAuthSequenceNo=null;
			String strExtnAurusCI=null;
			String strExtnAurusOOT=null;
			String strExtnAurusReauth=null;
			
			 for (int k = 0; k < nlPaymentMethod.getLength(); k++) {
				Element elePaymentMethod = (Element) nlPaymentMethod.item(k);
				String strPaymentType=elePaymentMethod.getAttribute("PaymentType");
				if(strPaymentType.equalsIgnoreCase("CREDIT_CARD")){
					Element eleExtnPayment = SCXmlUtil.getChildElement(elePaymentMethod, "Extn");
					 strExtnPostAuthCount=eleExtnPayment.getAttribute("ExtnPostAuthCount");
					 strExtnPostAuthSequenceNo=eleExtnPayment.getAttribute("ExtnPostAuthSequenceNo");
					 /*strExtnRefundCount=eleExtnPayment.getAttribute("ExtnRefundCount");
					 strExtnRefundSequenceNo=eleExtnPayment.getAttribute("ExtnRefundSequenceNo");*/
					 strExtnAurusCI=eleExtnPayment.getAttribute("ExtnAurusCI");
					 strExtnAurusOOT=eleExtnPayment.getAttribute("ExtnAurusOOT");
					 strExtnAurusReauth=eleExtnPayment.getAttribute("ExtnAurusReauthorized");
				}
			 }
					
			if(YFCObject.isVoid(strExtnPostAuthCount)){
			
			NodeList nlOrderLines = eleOrder
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int noOfLines = nlOrderLines.getLength();
			int iExtnPostAuthCount=0;
			for (int i = 0; i < noOfLines; i++) {

				Element eleOrderLine = (Element) nlOrderLines.item(i);
				
				String strOrdQty = eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
				int iOrdQty = Integer.parseInt(strOrdQty);
				iExtnPostAuthCount=iExtnPostAuthCount+iOrdQty;
			}
			//OMS-2455: Start
			if(iExtnPostAuthCount>99)
			{
				strExtnPostAuthCount="99"; 
			}
			else
			{	
			strExtnPostAuthCount = Integer.toString(iExtnPostAuthCount);
			}
			//OMS-2455: End
			}
			if(YFCObject.isVoid(strExtnRefundCount)){
				
				NodeList nlOrderLines = null;
				
				if(VSIConstants.RETURN_DOCUMENT_TYPE.equals(strDocType)){
					nlOrderLines = eleDerivedFromOrder
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				} else if(VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocType)){
					nlOrderLines = eleOrder
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				}				
				int noOfLines = nlOrderLines.getLength();
				int iExtnRefundCount=0;
				for (int i = 0; i < noOfLines; i++) {
	
					Element eleOrderLine = (Element) nlOrderLines.item(i);
						
					String strOrdQty = eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
					int iOrdQty = Integer.parseInt(strOrdQty);
					iExtnRefundCount=iExtnRefundCount+iOrdQty;
				}
				//OMS-2455: Start
				if(iExtnRefundCount>99)
				{
					strExtnRefundCount="99"; 
				}
				else
				{	
					strExtnRefundCount = Integer.toString(iExtnRefundCount);
				}
				//OMS-2455: End			
			}

			IsDraftOrAurus.put("ISDRAFTORDER", draftOrderFlag);
			IsDraftOrAurus.put("ISAURUSORDER", extnAurusTokenFlag);
			IsDraftOrAurus.put("MAXORDERSTATUS", strMaxStatus);
			IsDraftOrAurus.put("MINORDERSTATUS", strMinStatus);
			IsDraftOrAurus.put("EXTNAURUSCI", strExtnAurusCI);
			IsDraftOrAurus.put("EXTNAURUSOOT", strExtnAurusOOT);
			IsDraftOrAurus.put("POSTAUTHCOUNT", strExtnPostAuthCount);
			IsDraftOrAurus.put("REFUNDCOUNT", strExtnRefundCount);
			IsDraftOrAurus.put("SALESORDERHEADERKEY", strSOHdrKey);
			IsDraftOrAurus.put("EXTNAURUSREAUTHORIZED", strExtnAurusReauth);
			IsDraftOrAurus.put("ENTRYTYPE", strEntryType);
			//OMS-2437 -- Start
			IsDraftOrAurus.put("ORDERTYPE", strOrderType);
			//OMS-2437 -- End
			
			if(YFCObject.isVoid(strExtnPostAuthSequenceNo)){
			IsDraftOrAurus.put("POSTAUTHSEQNO", "0");
			}
			else{
				IsDraftOrAurus.put("POSTAUTHSEQNO", strExtnPostAuthSequenceNo);
			}
			
			if(YFCObject.isVoid(strExtnRefundSequenceNo)){
				IsDraftOrAurus.put("REFUNDSEQNO", "0");
			}
			else{
				IsDraftOrAurus.put("REFUNDSEQNO", strExtnRefundSequenceNo);
			}

		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}

		return IsDraftOrAurus;
}


	private Document getOrderListForAurus(YFSExtnPaymentCollectionInputStruct inStruct, YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) {
		String orderHeaderKeyStr = inStruct.orderHeaderKey;
		boolean isDarftOrder = false;
		try {
			Document getOrderListIP = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
			getOrderListIP.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);

			Document getOrderListOP = VSIUtils.invokeAPI(env,"global/template/api/VSIGetOrderListAurus.xml",VSIConstants.API_GET_ORDER_LIST, getOrderListIP);

			return getOrderListOP;
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}

	}


	private Document createChangeOrderForExtnAurusToken(String sOHK) {
		Document docOrder = SCXmlUtil.createDocument("Order");
		Element eleOrder = docOrder.getDocumentElement();
		eleOrder.setAttribute("Action", "MODIFY");
		eleOrder.setAttribute("OrderHeaderKey", sOHK);
		eleOrder.setAttribute("Override", "Y");
		Element eleExtn = SCXmlUtil.createChild(eleOrder, "Extn");
		eleExtn.setAttribute("ExtnAurusToken", "Y");
		return docOrder;		
	}

	private Document recordAdditionalAuthData(String ci,String oot
		,String referralNUM
		) {
		Document docOrder = SCXmlUtil.createDocument("PaymentMethod");
		Element eleOrder = docOrder.getDocumentElement();
		
		Element eleExtn = SCXmlUtil.createChild(eleOrder, "Extn");
		
		if(ci!=null && ci.length()>0) {
			eleExtn.setAttribute("ExtnAurusCI", ci);
		}
		
		if(oot!=null && oot.length()>0) {
			eleExtn.setAttribute("ExtnAurusOOT", oot);
		}
		eleExtn.setAttribute("ExtnAurusReferralNUM", referralNUM);
		eleExtn.setAttribute("ExtnAurusReauthorized", VSIConstants.FLAG_Y);
		return docOrder;		
		
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
			
			//OMS-2445 -- Start
			String strQueueId="VSI_CREDITCARD_ALERT";
			//OMS-2445 -- End

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
							orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
					// raising holds and cancelling orders
					authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag,null,null);
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
							orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
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
								orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
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
							orderNoStr, strOrderHeaderKey, inStruct, strQueueId);

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
		
		//OMS-2445 -- Start
		String strQueueId="VSI_CREDITCARD_ALERT";
		//OMS-2445 -- End

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
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
				// raising holds and cancelling orders
				authFailureCancel(env, strOrderHeaderKey, outStruct,inStruct,strAQRiskified,strRiskifiedFlag,null,null);
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
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
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
							orderNoStr, strOrderHeaderKey, inStruct, strQueueId);
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
						orderNoStr, strOrderHeaderKey, inStruct, strQueueId);

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

	public void aurusAuthRecords(YFSEnvironment env, String strOrderHeaderKey,String  aurusPayTicketNum,String cardNumber,String cardIdentifier,
			String processorToken,String cardExpiryDate,String cardEntryMode,String receiptToken,String transactionToken,
			String batchNumber,String referenceNumber,String processorResponseCode,String responseText,
			String totalApprovedAmount,String transactionType,String auruspayTransactionId,String transactionIdentifier,String cardType,
			String transactionDate,String responseCode,String approvalCode,String transactionAmount,String transactionTime,
			String processorResponseText,String processorTokenRespText,String aurusProcessorId,
			String oneTimeToken,String storeId,String merchantIdentifier,String oneOrderToken,String cVVResult,String terminalId,String authAVSResult
			)
					throws Exception {

		Document doc = XMLUtil.createDocument("AurusAuthRecords");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
		eleOrder.setAttribute("ExtnCardNumber",cardNumber);
		eleOrder.setAttribute("ExtnCI",cardIdentifier);
		eleOrder.setAttribute("ExtnProcessorToken",processorToken);
		eleOrder.setAttribute("ExtnCardExpiryDate",cardExpiryDate);
		eleOrder.setAttribute("ExtnCardEntryMode",cardEntryMode);
		eleOrder.setAttribute("ExtnReceiptToken",receiptToken);
		eleOrder.setAttribute("ExtnTransactionToken",transactionToken);
		eleOrder.setAttribute("ExtnBatchNumber",batchNumber);
		eleOrder.setAttribute("ExtnAuthReferenceNo",referenceNumber);
		eleOrder.setAttribute("ExtnProcessorResponseCode",processorResponseCode);
		eleOrder.setAttribute("ExtnResponseText",responseText);
		eleOrder.setAttribute("ExtnTotalApprovedAmount",totalApprovedAmount);
		eleOrder.setAttribute("ExtnTransactionType",transactionType);
		eleOrder.setAttribute("ExtnAurusPayTranId",auruspayTransactionId);
		eleOrder.setAttribute("ExtnTranID",transactionIdentifier);
		eleOrder.setAttribute("ExtnCardType",cardType);
		eleOrder.setAttribute("ExtnTransactionDate",transactionDate);
		eleOrder.setAttribute("ExtnResponseCode",responseCode);
		eleOrder.setAttribute("ExtnApprovalCode",approvalCode);
		eleOrder.setAttribute("ExtnTransactionAmount",transactionAmount);
		eleOrder.setAttribute("ExtnTransactionTime",transactionTime);
		eleOrder.setAttribute("ExtnProcessorResponseText",processorResponseText);
		eleOrder.setAttribute("ExtnProcessorTokenRespText",processorTokenRespText);
		eleOrder.setAttribute("ExtnAurusProcessorId",aurusProcessorId);
		eleOrder.setAttribute("ExtnOTT",oneTimeToken);
		eleOrder.setAttribute("ExtnOOT",oneOrderToken);
		eleOrder.setAttribute("ExtnStoreId",storeId);
		eleOrder.setAttribute("ExtnMerchantIdentifier",merchantIdentifier);
		eleOrder.setAttribute("ExtnCVVResult",cVVResult);
		eleOrder.setAttribute("ExtnTerminalId",terminalId);
		eleOrder.setAttribute("ExtnAurusPayTicketNum",aurusPayTicketNum);


		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env,"VSICreateAurusAuthRecds", doc);

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
	
	public void aurusTnsAuthRecords(YFSEnvironment env, String strOrderHeaderKey,
			String transactionIdentifier, String referenceNumber,
			String aurusPayTicketNum, String cVVResult, String authAVSResult, String processorResponseCode, String responseCode
			
			)
					throws Exception {
		
		Document doc = XMLUtil.createDocument("TNSAuthRecords");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				strOrderHeaderKey);
		eleOrder.setAttribute("ExtnAurusTransactionIdentifier", transactionIdentifier);
		eleOrder.setAttribute("ReferenceNumber", referenceNumber);
		eleOrder.setAttribute("AurusPayTicketNumber", aurusPayTicketNum);
		eleOrder.setAttribute("ExtnAurusToken", "Y");
		eleOrder.setAttribute("CVVResp", cVVResult);
		eleOrder.setAttribute("AvsGatewayCode", authAVSResult);
		eleOrder.setAttribute("AurusResponseCode", responseCode);
		eleOrder.setAttribute("ProcessorResponseCode", processorResponseCode);
		
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
	 * @param strQueueId 
	 **/

	public void creditCardAlert(YFSEnvironment env, String errorCodeStr,
			String errorMessageStr, String sOrderNo, String sOHK,
			YFSExtnPaymentCollectionInputStruct inStruct, String strQueueId)
					throws ParserConfigurationException, YIFClientCreationException,
					YFSException, RemoteException {
		
		printLogs("================Inside creditCardAlert Method================================");

		if ("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)) {
			printLogs("Credit Card Alert will be raised for Authorization Scenario");
			String failureTypeStr = "Credit Card Authorization Failure";
			raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
					inStruct, failureTypeStr, strQueueId);
		} else if ("CHARGE".equalsIgnoreCase(inStruct.chargeType)) {
			
			if (inStruct.requestAmount >= 0.0D) {
				printLogs("Credit Card Alert will be raised for Charge Scenario");
				String failureTypeStr = "Credit Card Charge Failure";
				raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
						inStruct, failureTypeStr, strQueueId);

			} else {
				printLogs("Credit Card Alert will be raised for Refund Scenario");
				String failureTypeStr = "Credit Card Refund Failure";
				raiseAlert(env, errorCodeStr, errorMessageStr, sOrderNo, sOHK,
						inStruct, failureTypeStr, strQueueId);

			}
		}
		
		printLogs("================Exiting creditCardAlert Method================================");
	}

	public void raiseAlert(YFSEnvironment env1, String errorCodeSt,
			String errorMessageSt, String sOrderNoSt, String sOHKSt,
			YFSExtnPaymentCollectionInputStruct inStructSt,
			String failureTypeStr, String strQueueId) throws ParserConfigurationException,
	YIFClientCreationException, YFSException, RemoteException {
		
		printLogs("================Inside raiseAlert Method================================");

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
				strQueueId);
		
		//OMS-2445 -- Start
		InboxElement.setAttribute(VSIConstants.ATTR_CONSOLIDATE,VSIConstants.FLAG_Y);
		InboxElement.setAttribute(VSIConstants.ATTR_CONS_WINDOW,VSIConstants.VAL_FOREVER);
		
		Element eleConsolidationTemplate = createExInput.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
		InboxElement.appendChild(eleConsolidationTemplate);
		Element eleInboxCpy = (Element) InboxElement.cloneNode(true);
		eleConsolidationTemplate.appendChild(eleInboxCpy);
		//OMS-2445 -- End

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
		
		printLogs("raiseAlert Method: Create Exception API Input prepared is: "+createExInput.toString());

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env1, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
		printLogs("================Exiting raiseAlert Method================================");

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
	 * @param strAQRiskified 
	 * @param cVVResult 
	 * @param authAVSResult **/

	private void authFailureCancel(YFSEnvironment env, String sOHK,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSExtnPaymentCollectionInputStruct inStruct, String strAQRiskified, String strRiskifiedFlag, String authAVSResult, String cVVResult)
					throws ParserConfigurationException, YIFClientCreationException,
					YFSException, RemoteException {
		
		printLogs("================Inside authFailureCancel Method================================");
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
			printLogs("Draft Order Scenario");
			isDraftOrd = true;
			//OMS-2046 START
			ArrayList<Element> listCheckoutFlag;
			try {
				listCheckoutFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_CHECKOUT, VSIConstants.ATTR_DEFAULT);

				if(!listCheckoutFlag.isEmpty()){
					Element eleCommonCode=listCheckoutFlag.get(0);
					String strCheckoutFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strCheckoutFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strCheckoutFlag)&&!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
						printLogs("Checkout Denied Message will be sent to Riskified");
						String strOrderHeaderKey = eleOrd.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
						Document docInputOrder = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						Element sOrderElement = docInputOrder.getDocumentElement();
						sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
						if(!YFCObject.isVoid(strAQRiskified)){
							sOrderElement.setAttribute(VSIConstants.ATTR_AQR_CODE, strAQRiskified);
						}
						if(!YFCObject.isVoid(authAVSResult)){
							sOrderElement.setAttribute("AVSResult", authAVSResult);
						}
						if(!YFCObject.isVoid(cVVResult)){
							sOrderElement.setAttribute("CVVResult", cVVResult);
						}
						printLogs("Checkout Denied Message prepared is: "+docInputOrder.toString());
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
		
		printLogs("getOrderLineList API Input prepared is: "+orderLine.toString());

		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,
				orderLine);
		
		printLogs("getOrderLineList API Output is: "+outDoc.toString());
		
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
			printLogs("There are no orderlines in between Sent To Node and Shipped status");
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
				printLogs("Non Draft Order Scenario, Change Order API will be invoked");
				printLogs("Change Order API Input is: "+cancelInput.toString());
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER, cancelInput);
			}
			//OMS-1760: End

		}
		
		printLogs("================Exiting authFailureCancel Method================================");
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
		
		printLogs("================Inside applyPaymentErrorHold================================");
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
			printLogs("Draft Order Scenario");
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
			printLogs("applyPaymentErrorHold: Change Order API Input: "+docApplyPaymentHold.toString());
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", docApplyPaymentHold);
			printLogs("applyPaymentErrorHold: Change Order API Output: "+docChangeOrderOutput.toString());
		}

		//OMS-1760: PAYMENT_HOLD should not be applied on Draft Orders :End//
		printLogs("================Exiting applyPaymentErrorHold================================");
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
		printLogs("================Inside cancelOpenOrderLines Method================================");
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
		boolean isHoldRequired = false;
		if (VSIConstants.FLAG_Y.equals(draftOrderFlag)) {
			isDraftOrd = true;
			printLogs("Draft Order Scenario");
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
					} else if(VSIConstants.STATUS_CODE_RELEASE_ACKNOWLEDGED.equals(strOrderStatus)){
						isHoldRequired=true;
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
			if(isHoldRequired){
				printLogs("Payment hold will be applied on the order");
				applyPaymentErrorHold(env, strOrderHeaderKey);
			}
		}
		
		printLogs("================Exiting cancelOpenOrderLines Method================================");
	}
	// OMS-1913: End
	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
	
}// End of collectionOtherClass

