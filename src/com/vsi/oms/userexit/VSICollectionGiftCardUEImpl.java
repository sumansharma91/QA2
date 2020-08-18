package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.XMLUtil;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;
import com.yantra.yfs.japi.ue.YFSCollectionOthersUE;
import com.yantra.yfs.japi.ue.YFSCollectionStoredValueCardUE;

/**
 * @author nish.pingle
 * @version 1.0	
 * 
 * This class provides BusinessLogic specific to VSI
 * for UserExit VSICollectionGiftCardUEImpl (as part of transaction Payment Execution)
 * It handles Gift Card Authorization, Settlement and Refund
 */



public class VSICollectionGiftCardUEImpl implements YFSCollectionStoredValueCardUE,VSIConstants {

	/**
     * Instance of logger
     */
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICollectionGiftCardUEImpl.class.getName());
	YIFApi api;
	
	
	@Override
	public YFSExtnPaymentCollectionOutputStruct collectionStoredValueCard(
			final YFSEnvironment env, final YFSExtnPaymentCollectionInputStruct inStruct)
			throws YFSUserExitException {
		
		try
		{
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		String paymentType = inStruct.paymentType;
		
		// get the Charge Type.
		String strChargeType = inStruct.chargeType;
		// get the Request Amount
		Double dRequestAmount = inStruct.requestAmount;
        ////System.out.println("Payment Type is " + paymentType);

				
		 if(VSIConstants.PAYMENT_MODE_GC.equalsIgnoreCase(inStruct.paymentType))
		{
	        ////System.out.println("Inside Gift Card ");

			if(PAYMENT_STATUS_AUTHORIZATION.equalsIgnoreCase(strChargeType)){
		        ////System.out.println("Inside authorization Card ");
		        if (dRequestAmount >= 0D){
		        	outStruct = doAuthorization(inStruct,outStruct,env);
		        }
		        else{
		        	outStruct = doVoid(inStruct,outStruct,env);
		        }

			}
			else if(PAYMENT_STATUS_CHARGE.equalsIgnoreCase(strChargeType)){
				
				if(dRequestAmount >= 0D){
					outStruct =	doCharge(inStruct,outStruct,env);

				}
				else
				{ 
					outStruct =	doRefund(inStruct,outStruct,env);
				}
				
				
			}
		}else if(VSIConstants.PAYMENT_MODE_OGC.equalsIgnoreCase(inStruct.paymentType)){
			outStruct = handleOGCCharge(inStruct);
			// call svs service -- async mode -- also make the is activated flag to y
			invokeSVSForOGCActivation(inStruct,env);
		}
		 
		 
		else {
			throw new YFSException("PAYMENT_FAILURE",
					"PAYMENT_FAILURE",
					"Invalid Payment Tender");
		}
		
				
	return outStruct;
		}
		catch(Exception Ex){
			Ex.printStackTrace();
			throw new YFSException();
			
		}
		
				
		}


	private void invokeSVSForOGCActivation(
			YFSExtnPaymentCollectionInputStruct inStruct, YFSEnvironment env) {
		try {
			Date todaysDate = new Date(); 
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
			String dateToday = formatter.format(todaysDate);
			String sStoreNo = getShipNode(env,inStruct.orderHeaderKey);

			Document docVGCDetailsDoc = XMLUtil.createDocument("VSIOnlineGiftCard");
			Element rootEle = docVGCDetailsDoc.getDocumentElement();
			rootEle.setAttribute("GiftCardNo", inStruct.svcNo);
			rootEle.setAttribute("StoreNo", sStoreNo);

			rootEle.setAttribute("dateToday", dateToday);
			rootEle.setAttribute("PinNo", inStruct.paymentReference2);
			rootEle.setAttribute("InvoiceNo", inStruct.orderNo);
			double amount = inStruct.requestAmount;
			if(amount < 0) amount = amount * (-1);
			String refAmt = "0.0";
			if(amount > 0)
				refAmt = 	Double.toString(amount);
			
			if(log.isDebugEnabled()){
				log.info("refAmt = "+refAmt);
			}
			rootEle.setAttribute("RefundAmount", refAmt);
			rootEle.setAttribute("OrderHeaderKey", inStruct.orderHeaderKey);
			rootEle.setAttribute("OrderNo", inStruct.orderNo);
			String sCalcTranID = generateTransactionIDForVGC(env);
			String sTranID = "936806"+sCalcTranID; 
			rootEle.setAttribute("TransactionID", sTranID);
			if(log.isDebugEnabled()){
				log.info("TransactionID = "+sTranID);
				log.info("Input to service VSISendVGCActivationReqToSVS : \n"+XMLUtil.getXMLString(docVGCDetailsDoc));
			}
			VSIUtils.invokeService(env, "VSISendVGCActivationReqToSVS", docVGCDetailsDoc);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private YFSExtnPaymentCollectionOutputStruct handleOGCCharge(
			YFSExtnPaymentCollectionInputStruct inStruct) {
		final YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		
		outStruct.tranAmount = inStruct.requestAmount;

		outStruct.tranType = inStruct.chargeType;
		outStruct.authorizationAmount = inStruct.requestAmount;
		outStruct.collectionDate = new Date();
		outStruct.executionDate = new Date();
		outStruct.requestID = inStruct.paymentReference1;

		return outStruct;
	}


	/**
	 * @param inStruct
	 * @param outStruct
	 * @param env
	 * Create XML below to send as input for Service
	 * <GiftCard storeNumber="456" cardNumber="65" pinNumber="12" date="2014-08-02T10:16:51" invoiceNumber="123"  amount="33" transactionID="544"/>
	 * @return 
	 * @throws Exception 
	 */
	private YFSExtnPaymentCollectionOutputStruct doAuthorization(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) throws Exception {
		

		
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String sStoreNo = getShipNode(env,strOrderHeaderKey);
		String sInvoiceNumber = inStruct.orderNo;
		//String sStoreNo = "6101";
		double dRequestAmount = inStruct.requestAmount;
		String sRequestAmount = Double.toString(dRequestAmount);
		String sSvcNo = inStruct.svcNo;
		String sPinNo = inStruct.paymentReference1;
	
	
		String sCalcTranID = generateTransactionID(env);
		String sTranID = "936860"+sCalcTranID;
		
		Date todaysDate = new Date(); 
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		String dateToday = formatter.format(todaysDate);
						
		Document GiftCardInput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GiftCardInput.getDocumentElement();
		GCElement.setAttribute("storeNumber", sStoreNo);
		GCElement.setAttribute("cardNumber", sSvcNo);
		GCElement.setAttribute("pinNumber", sPinNo);
		GCElement.setAttribute("date", dateToday);
		GCElement.setAttribute("invoiceNumber", sInvoiceNumber);
		GCElement.setAttribute("amount", sRequestAmount);
		GCElement.setAttribute("transactionID", sTranID);
		
		//JIRA 608
		env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
		env.setTxnObject("chargeType", "Authorize Request");
		
		api = YIFClientFactory.getInstance().getApi();
		//OMS-868 :Start
		String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_GC_USER_NAME");
		String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_GC_PASSWORD");
		String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_GC_MERCHANT_NUMBER");
		String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_GC_ROUTING_ID");
		GCElement.setAttribute("Username", strUserName);
		GCElement.setAttribute("Password", strPassword);
		GCElement.setAttribute("merchantNumber", strMerchantNumber);
		GCElement.setAttribute("routingID", strRoutingID);
		//OMS-86 :End
		Document outDoc = api.executeFlow(env, "VSISVSAuth", GiftCardInput);
		String response = VSIUtils.getDocumentXMLString(outDoc);
		////System.out.println("Printing Response:"+response);
		////System.out.println("We are in the UE buddy");
		
		//JIRA 608
		recordPayments(env, strOrderHeaderKey, response, "Authorize Response");
		
		 outStruct = handleResponse(env,outDoc,inStruct,GiftCardInput);
			
		 return outStruct;
	
	}

	
	private String DoAuthorizeforCharge(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) throws Exception {
		

		String tranID=null;
		String strOrderHeaderKey = inStruct.orderHeaderKey;
		String sStoreNo = getShipNode(env,strOrderHeaderKey);
		String sInvoiceNumber = inStruct.orderNo;
		//String sStoreNo = "6101";
		double dRequestAmount = inStruct.requestAmount;
		String sRequestAmount = Double.toString(dRequestAmount);
		String sSvcNo = inStruct.svcNo;
		String sPinNo = inStruct.paymentReference1;
	
	
		String sCalcTranID = generateTransactionID(env);
		String sTranID = "936860"+sCalcTranID;
		
		Date todaysDate = new Date(); 
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		String dateToday = formatter.format(todaysDate);
		
		
		
		Document GiftCardInput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GiftCardInput.getDocumentElement();
		GCElement.setAttribute("storeNumber", sStoreNo);
		GCElement.setAttribute("cardNumber", sSvcNo);
		GCElement.setAttribute("pinNumber", sPinNo);
		GCElement.setAttribute("date", dateToday);
		GCElement.setAttribute("invoiceNumber", sInvoiceNumber);
		GCElement.setAttribute("amount", sRequestAmount);
		GCElement.setAttribute("transactionID", sTranID);
		
		//JIRA 608
		env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
		env.setTxnObject("chargeType", "Authorize For Charge Request");
		
		api = YIFClientFactory.getInstance().getApi();
		//OMS-868 :Start
		String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_GC_USER_NAME");
		String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_GC_PASSWORD");
		String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_GC_MERCHANT_NUMBER");
		String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_GC_ROUTING_ID");
		GCElement.setAttribute("Username", strUserName);
		GCElement.setAttribute("Password", strPassword);
		GCElement.setAttribute("merchantNumber", strMerchantNumber);
		GCElement.setAttribute("routingID", strRoutingID);
		//OMS-86 :End
		Document outDoc = api.executeFlow(env, "VSISVSAuth", GiftCardInput);
		String response = VSIUtils.getDocumentXMLString(outDoc);
		
		//JIRA 608
		recordPayments(env, strOrderHeaderKey, response, "Authorize For Charge Response");
		
		////System.out.println("Printing Response:"+response);
		////System.out.println("We are in the UE buddy");
		Element rcode = (Element) outDoc.getElementsByTagName("returnCode").item(0);
		String sRcode = rcode.getFirstChild().getTextContent();
		String sReason = rcode.getLastChild().getTextContent();
		////System.out.println("Reason Code Returned:::"+sRcode);
		////System.out.println("Reason String Returned:::"+sReason);
				
	
	if(sRcode.equalsIgnoreCase("01Approval")||sRcode.equalsIgnoreCase("01")||sRcode.equalsIgnoreCase("Approval")){
		Element transactionID = (Element) outDoc.getElementsByTagName("transactionID").item(0);
		tranID=transactionID.getTextContent();
	}
	else if(sRcode.equalsIgnoreCase("04")|| sRcode.equalsIgnoreCase("08")||sRcode.equalsIgnoreCase("05")||sRcode.equalsIgnoreCase("10")||sRcode.equalsIgnoreCase("20")||sRcode.equalsIgnoreCase("09")||sRcode.equalsIgnoreCase("17")){
		
		createHold(env,inStruct,sRcode,sReason);
		
		
	}
	else if(sRcode.equalsIgnoreCase("15")){
		for (int count = 1; count < 4;count++){
			
			//JIRA 608
			env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
			env.setTxnObject("chargeType", "Retry Authorize For Charge Request");

			Document outDoc1 = api.executeFlow(env, "VSISVSAuth", GiftCardInput);
			String response1 = VSIUtils.getDocumentXMLString(outDoc1);
			
			//JIRA 608
			recordPayments(env, strOrderHeaderKey, response1, "Retry Authorize For Charge Response");
			
			Element rcode1 = (Element) outDoc1.getElementsByTagName("returnCode").item(0);
			String sRcode1 = rcode1.getFirstChild().getTextContent();
			String sReason1 = rcode1.getLastChild().getTextContent();
			if(sRcode1.equalsIgnoreCase("01Approval")||sRcode1.equalsIgnoreCase("01")||sRcode1.equalsIgnoreCase("Approval")){
				outStruct = handleSuccesfulResponse(env, outDoc, inStruct);
                break;
			}
			
			
		}
		
	}
	else {
		

		throw new YFSException("PAYMENT_FAILURE",
				"PAYMENT_FAILURE",
				"SVS Unknown Response");

		
		
	}
		
		 
			
		 return tranID;
	
	}
	
	

	/**
	 * @param inStruct
	 * @param outStruct
	 * @param env
	 * @return
	 * @throws Exception 
	 */
	private YFSExtnPaymentCollectionOutputStruct doCharge(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) throws Exception {
				
		////System.out.println("We are in the charge part UE ");
		
				String strOrderHeaderKey = inStruct.orderHeaderKey;
				String sStoreNo = getShipNode(env,strOrderHeaderKey);
				String sInvoiceNumber = inStruct.orderNo;
				double dRequestAmount = inStruct.requestAmount;
				String sRequestAmount = Double.toString(dRequestAmount);
				String sSvcNo = inStruct.svcNo;
				String sPinNo = inStruct.paymentReference1;
			
				String chargeTransactionKeyStr=inStruct.chargeTransactionKey;
				
				String sTranID = inStruct.authorizationId;
				 if(YFCObject.isVoid(sTranID)){
					 String strPaymentReference9 = inStruct.paymentReference9;
					 if(!YFCCommon.isVoid(strPaymentReference9) && strPaymentReference9.equalsIgnoreCase("RETRIED")){
						 sTranID=DoAuthorizeforCharge(inStruct,outStruct,env);
			        	 outStruct.authorizationId=sTranID;
			        	 Document docRecordExternalCharges = formInputForRecordsExternalCharges(outStruct);
			        	 outStruct.recordAdditionalTransactions = docRecordExternalCharges;
					 }else{
						 outStruct.retryFlag="Y";
						 outStruct.PaymentReference9="RETRIED";
						 return outStruct;
					 }
		          }
				////System.out.println("Transaction ID:"+sTranID);
				
				Date todaysDate = new Date(); 
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
				String dateToday = formatter.format(todaysDate);
				
				
				
				Document GiftCardInput = XMLUtil.createDocument("GiftCard");
				Element GCElement = GiftCardInput.getDocumentElement();
				GCElement.setAttribute("storeNumber", sStoreNo);
				GCElement.setAttribute("cardNumber", sSvcNo);
				GCElement.setAttribute("pinNumber", sPinNo);
				GCElement.setAttribute("date", dateToday);
				GCElement.setAttribute("invoiceNumber", sInvoiceNumber);
				GCElement.setAttribute("amount", sRequestAmount);
				GCElement.setAttribute("transactionID", sTranID);

				//JIRA 608
				env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
				env.setTxnObject("chargeType", "Charge Request");				

				api = YIFClientFactory.getInstance().getApi();
				//OMS-868 :Start
				String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_USER_NAME");
				String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_PASSWORD");
				String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_MERCHANT_NUMBER");
				String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_ROUTING_ID");
				GCElement.setAttribute("Username", strUserName);
				GCElement.setAttribute("Password", strPassword);
				GCElement.setAttribute("merchantNumber", strMerchantNumber);
				GCElement.setAttribute("routingID", strRoutingID);
				//OMS-86 :End
				Document outDoc = api.executeFlow(env, "VSISVSSettlement", GiftCardInput);
				String response = VSIUtils.getDocumentXMLString(outDoc);
				
				//JIRA 608
				recordPayments(env, strOrderHeaderKey, response, "Charge Response");
				
				////System.out.println("Printing Response:"+response);
				////System.out.println("We are in the charge part UE after Response");
				
				outStruct = handleResponse(env,outDoc,inStruct,outDoc);
				
	    	    createAuthRequest(env,chargeTransactionKeyStr,strOrderHeaderKey);
					
		return outStruct;
		
	}







	private Document formInputForRecordsExternalCharges(YFSExtnPaymentCollectionOutputStruct outStruct) {
		Document docRecordExternalCharges = SCXmlUtil.createDocument("RecordExternalCharges");
		Element eleRecordExternalCharges = docRecordExternalCharges.getDocumentElement();
		Element elePaymentMethod = SCXmlUtil.createChild(eleRecordExternalCharges, "PaymentMethod");
		Element elePaymentDetailsList = SCXmlUtil.createChild(elePaymentMethod, "PaymentDetailsList");
		Element elePaymentDetails = SCXmlUtil.createChild(elePaymentDetailsList, "PaymentDetails");
		elePaymentDetails.setAttribute("ChargeType", "AUTHORIZATION");
		elePaymentDetails.setAttribute("AuthorizationExpirationDate", "2500-12-31");
		elePaymentDetails.setAttribute("AuthorizationID", outStruct.authorizationId);
		//eleRecordExternalCharges.setAttribute(ATTR_ORDER_HEADER_KEY, outStruct.);
		return docRecordExternalCharges;
	}


	/**
	 * @param inStruct
	 * @param outStruct
	 * @param env
	 * @return
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 */
	private YFSExtnPaymentCollectionOutputStruct doRefund(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException {
		

		String strOrderHeaderKey = inStruct.orderHeaderKey;
		//String sStoreNo = getShipNode(env,strOrderHeaderKey);
		String sInvoiceNumber = inStruct.orderNo;
		//String sStoreNo = "6101";
		double dRequestAmount = inStruct.requestAmount;
		outStruct.authorizationId = inStruct.authorizationId;
		outStruct.authorizationAmount = inStruct.requestAmount;
		outStruct.retryFlag = "N";
		/*String sRequestAmount = Double.toString(dRequestAmount);
		String sSvcNo = inStruct.svcNo;
		String sPinNo = inStruct.paymentReference1;
		String sTranID = inStruct.authorizationId;
		
*/
		raiseAlert(env,"Refund","Refund Gift Card",sInvoiceNumber,strOrderHeaderKey,inStruct);
		
			
/*		Date todaysDate = new Date(); 
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		String dateToday = formatter.format(todaysDate);
		

		Document GiftCardInput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GiftCardInput.getDocumentElement();
		GCElement.setAttribute("storeNumber", sStoreNo);
		GCElement.setAttribute("cardNumber", sSvcNo);
		GCElement.setAttribute("pinNumber", sPinNo);
		GCElement.setAttribute("date", dateToday);
		GCElement.setAttribute("invoiceNumber", sInvoiceNumber);
		GCElement.setAttribute("amount", sRequestAmount);
		GCElement.setAttribute("transactionID", sTranID);
		
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env, "VSISVSCall", GiftCardInput);
		String response = VSIUtils.getDocumentXMLString(outDoc);
		//System.out.println("Printing Response:"+response);
		//System.out.println("We are in the UE buddy");
*/		
		return outStruct;
		


		
		
	}




	/**
	 * @param env
	 * @param outDoc
	 * @param inStruct
	 * @return
	 * @throws Exception 
	 */
	private YFSExtnPaymentCollectionOutputStruct handleResponse(YFSEnvironment env, Document outDoc, YFSExtnPaymentCollectionInputStruct inStruct,Document GiftCardInput ) throws Exception {
		String strOrderNo=inStruct.orderNo;
		String strOrderHeaderKey=inStruct.orderHeaderKey;

		YFSExtnPaymentCollectionOutputStruct outStruct = null;
		////System.out.println("We are in the handleResponse buddy");
		Element rcode = (Element) outDoc.getElementsByTagName("returnCode").item(0);
		String sRcode = rcode.getFirstChild().getTextContent();
		String sReason = rcode.getLastChild().getTextContent();
		////System.out.println("Reason Code Returned:::"+sRcode);
		////System.out.println("Reason String Returned:::"+sReason);
				
	
	if(sRcode.equalsIgnoreCase("01Approval")||sRcode.equalsIgnoreCase("01")||sRcode.equalsIgnoreCase("Approval")){
		outStruct = handleSuccesfulResponse(env, outDoc, inStruct);

	}
	else if(sRcode.equalsIgnoreCase("04")|| sRcode.equalsIgnoreCase("08")||sRcode.equalsIgnoreCase("05")||sRcode.equalsIgnoreCase("10")||sRcode.equalsIgnoreCase("20")||sRcode.equalsIgnoreCase("09")||sRcode.equalsIgnoreCase("17")){
		
		createHold(env,inStruct,sRcode,sReason);
		
		
	}
	else if(sRcode.equalsIgnoreCase("15")){
		for (int count = 1; count < 4;count++){
			raiseAlert(env,"SVS","SVS Offline",strOrderNo,strOrderHeaderKey,inStruct);

			//JIRA 608
			env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
			env.setTxnObject("chargeType", "Retry Request");

			//OMS-868 :Start
			String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_GC_USER_NAME");
			String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_GC_PASSWORD");
			String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_GC_MERCHANT_NUMBER");
			String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_GC_ROUTING_ID");
			Element GCElement=GiftCardInput.getDocumentElement();
			GCElement.setAttribute("Username", strUserName);
			GCElement.setAttribute("Password", strPassword);
			GCElement.setAttribute("merchantNumber", strMerchantNumber);
			GCElement.setAttribute("routingID", strRoutingID);
			//OMS-86 :End
			Document outDoc1 = api.executeFlow(env, "VSISVSAuth", GiftCardInput);
			String response = VSIUtils.getDocumentXMLString(outDoc1);
			
			//JIRA 608
			recordPayments(env, strOrderHeaderKey, response, "Retry Response");

			Element rcode1 = (Element) outDoc1.getElementsByTagName("returnCode").item(0);
			String sRcode1 = rcode1.getFirstChild().getTextContent();
			String sReason1 = rcode1.getLastChild().getTextContent();
			if(sRcode1.equalsIgnoreCase("01Approval")||sRcode1.equalsIgnoreCase("01")||sRcode1.equalsIgnoreCase("Approval")){
				outStruct = handleSuccesfulResponse(env, outDoc, inStruct);
                break;
			}
			
			
		}
		
	}
		
	else {
		raiseAlert(env,"SVS","SVS Failure",strOrderNo,strOrderHeaderKey,inStruct);

		createHold(env,inStruct,sRcode,sReason);

		throw new YFSException("PAYMENT_FAILURE",
				"PAYMENT_FAILURE",
				"SVS Unknown Response");

		
		
	}
	  ////System.out.println("\n"+outStruct.executionDate+"\n Tran Amt:"+ outStruct.tranAmount+ "\n Auth ID"+outStruct.authorizationId + "\n Auth Amount"+outStruct.authorizationAmount +"instruct req amt"+ inStruct.requestAmount+"Auth Exp Date:"+ outStruct.authorizationExpirationDate);

	  return outStruct;

	}


	/**
	 * @param env
	 * @param outDoc
	 * @param inStruct
	 * @return
	 * @throws Exception 
	 */
	private YFSExtnPaymentCollectionOutputStruct handleSuccesfulResponse(YFSEnvironment env, Document outDoc, YFSExtnPaymentCollectionInputStruct inStruct) throws Exception {
		
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct() ;
		////System.out.println("We are in the handleSuccesfulResponse buddy");
	
	
		String chargeType = inStruct.chargeType;
		
		if(chargeType.equalsIgnoreCase(AUTHORIZATION)){
			
		Element transactionID = (Element) outDoc.getElementsByTagName("transactionID").item(0);
		String tranID=transactionID.getTextContent();
		////System.out.println("Transaction ID"+tranID);
		outStruct.authorizationId=tranID;
		outStruct.authorizationAmount=inStruct.requestAmount;
	
	   
	
	    String authExpDate = "25001231000000";// hard coding auth expiration date to year 2500 because auth never expires
        outStruct.authorizationExpirationDate=authExpDate;

       // //System.out.println("\n"+outStruct.executionDate+"\n Tran Amt:"+ outStruct.tranAmount+ "\n Auth ID"+outStruct.authorizationId + "\n Auth Amount"+outStruct.authorizationAmount +"instruct req amt"+ inStruct.requestAmount+"Auth Exp Date:"+ authExpDate);
	   
        final String strTimeStamp = getTimestamp();
        
        outStruct.authReturnCode = PAYMENT_AUTHORIZED_MSG; 
		outStruct.authReturnMessage = PAYMENT_AUTHORIZED_MSG;
		outStruct.authTime = strTimeStamp;
		//outStruct.authorizationId = strAuthId;

		// set the Transaction Details.
		outStruct.tranType = inStruct.chargeType;
		//outStruct.tranAmount = dAmount;
		outStruct.tranRequestTime = strTimeStamp;
		outStruct.tranReturnCode = PAYMENT_AUTHORIZED_MSG ;
		outStruct.tranReturnMessage = PAYMENT_AUTHORIZED_MSG;
		outStruct.tranAmount=inStruct.requestAmount;
		
		// set the Payment references.
		outStruct.PaymentReference1 = inStruct.paymentReference1;
		outStruct.PaymentReference2 = inStruct.paymentReference2;
		outStruct.PaymentReference3 = inStruct.paymentReference3;
		
		// set Other Details.
		outStruct.executionDate = new Date();
		outStruct.collectionDate = new Date();
		outStruct.requestID = tranID;
		outStruct.internalReturnCode = PAYMENT_AUTHORIZED_MSG;
		outStruct.internalReturnMessage =  PAYMENT_AUTHORIZED_MSG;
		outStruct.suspendPayment = "N";
		 outStruct.retryFlag = "N";
		outStruct.holdOrderAndRaiseEvent = false;
		outStruct.SvcNo = inStruct.svcNo;
		
		
		//sending OrderHeaderKey to a Queue for Order Processing
		String orderHeaderKey = inStruct.orderHeaderKey;
		
		//authorizedOrder(env,orderHeaderKey);
		
		
		
		}
		
		else{
			
			
			
			Element transactionID = (Element) outDoc.getElementsByTagName("transactionID").item(0);
			String tranID=transactionID.getTextContent();
			////System.out.println("CHARGE:::Transaction ID"+tranID);
			//outStruct.authorizationId=tranID;
			outStruct.authorizationAmount=inStruct.requestAmount;
		
				outStruct.authorizationId = inStruct.authorizationId;
				////System.out.println("CHARGE:::Authorization ID"+ inStruct.authorizationId);
				
				outStruct.collectionDate = new Date();
	    	    outStruct.executionDate = new Date();
	    	    outStruct.retryFlag = "N";
			 
	    	    
			outStruct.holdOrderAndRaiseEvent = false;
			outStruct.SvcNo = inStruct.svcNo;


			
			 final String strTimeStamp = getTimestamp();
		        
		        outStruct.authReturnCode = PAYMENT_CHARGED_MSG; 
				outStruct.authReturnMessage = PAYMENT_CHARGED_MSG;
				outStruct.authTime = strTimeStamp;
				//outStruct.authorizationId = strAuthId;

				// set the Transaction Details.
				outStruct.tranType = inStruct.chargeType;
				//outStruct.tranAmount = dAmount;
				outStruct.tranRequestTime = strTimeStamp;
				outStruct.tranReturnCode = PAYMENT_CHARGED_MSG ;
				outStruct.tranReturnMessage = PAYMENT_CHARGED_MSG;
				outStruct.tranAmount=inStruct.requestAmount;
				
				// set the Payment references.
				outStruct.PaymentReference1 = inStruct.paymentReference1;
				outStruct.PaymentReference2 = inStruct.paymentReference2;
				outStruct.PaymentReference3 = inStruct.paymentReference3;

			
		}
        
		return outStruct;
	}

	

	/**
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private String generateTransactionID(YFSEnvironment env) throws Exception {
		
		
		String tranNumber = "";
	 	String seqNum ="VSI_SEQ_TRAN_ID";
	 	tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		
		return tranNumber;
		
	} 
	
	/**
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private String generateTransactionIDForVGC(YFSEnvironment env) throws Exception {
		
		
		String tranNumber = "";
	 	String seqNum ="VSI_SEQ_VGC_TRAN_ID";
	 	tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		
		return tranNumber;
		
	} 


	/**
	 * @return
	 */
	private static String getTimestamp() {
		String sTimestamp = "";
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(VSIConstants.DATE_STAMP_FORMAT);
		sTimestamp = sdf.format(date);
		return sTimestamp;
	}
	
	
	

	/**
	 * @param env
	 * @param inStruct
	 * @param sRcode
	 * @param sReason
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 */
	private void createHold(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct, String sRcode, String sReason) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		
		
		String sOHK = inStruct.orderHeaderKey;
		
		Document createHoldInput = XMLUtil.createDocument("Order");
		Element orderElement = createHoldInput.getDocumentElement();
		
		
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
		
		Element OrderHoldTypesElement = createHoldInput.createElement("OrderHoldTypes");
		orderElement.appendChild(OrderHoldTypesElement);
		
		Element OrderHoldTypeElement = createHoldInput.createElement("OrderHoldType");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE, VSIConstants.HOLD_SVS_DECLINE);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT, sReason);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");
		
		OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
		
		if(!isDraftOrder)
		{
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);
		}	
	}
	
	private boolean isDraftOrder = false;
	
	/**
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws TransformerException 
	 */
	private String getShipNode(YFSEnvironment env, String strOrderHeaderKey) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException, TransformerException {
		
		Document getOrderListDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		api = YIFClientFactory.getInstance().getApi();
		 Document docTemplateForGetOrderDetails = this
	              .createTemplateForGetOrderDetails();
		Document outDoc = VSIUtils.invokeAPI(env, docTemplateForGetOrderDetails, VSIConstants.API_GET_ORDER_DETAILS, getOrderListDoc);
		//Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_DETAILS,getOrderListDoc);
		String draftOrderFlag = outDoc.getDocumentElement().getAttribute("DraftOrderFlag");
		if("Y".equals(draftOrderFlag))
		{
			isDraftOrder = true;
		}
		Element orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		String deliveryMethod = orderLineElem.getAttribute("DeliveryMethod");
		String sShipNode = null;
		if(!YFCObject.isVoid(deliveryMethod) && "PICK".equals(deliveryMethod))
		{
			sShipNode = orderLineElem.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		}
		else
		{
			 sShipNode = outDoc.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERED_BY);			
		}
		if(YFCCommon.isVoid(sShipNode))
		{
			// stamping dummy store ID. 
			sShipNode = "9999";
		}
		////System.out.println("shipnode is " + sShipNode);
		String shipNodePadded = ("0000000000" + sShipNode).substring(sShipNode.length());//Adding Leading Zeros
		////System.out.println("shipnode padded: " + shipNodePadded);
		return shipNodePadded;
	
	}
	
	 private Document createTemplateForGetOrderDetails() throws TransformerException {

		    // Create template Document for getOrderDetails .
		    Document docTemplateForGetOrderDetails = SCXmlUtil
		        .createDocument(VSIConstants.ELE_ORDER);
		    docTemplateForGetOrderDetails.getDocumentElement()
		        .setAttribute(VSIConstants.ATTR_ENTERED_BY, "");
		    docTemplateForGetOrderDetails.getDocumentElement()
	        .setAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG, "");
		    Element eleOrderLines = docTemplateForGetOrderDetails.createElement("OrderLines");
		    Element eleOrderline = docTemplateForGetOrderDetails.createElement("OrderLine");
		    eleOrderline.setAttribute("DeliveryMethod", "");
		    eleOrderline.setAttribute("ShipNode", "");
		    docTemplateForGetOrderDetails.getDocumentElement().appendChild(eleOrderLines);
		    eleOrderLines.appendChild(eleOrderline);
		    return docTemplateForGetOrderDetails;

		  }

	/**
	 * @param env
	 * @param chargeTransactionKeyStr
	 * @param strOrderHeaderKey
	 * @throws YIFClientCreationException
	 * @throws ParserConfigurationException
	 * @throws YFSException
	 * @throws RemoteException
	 */
	private void createAuthRequest(YFSEnvironment env,
			String chargeTransactionKeyStr, String strOrderHeaderKey) throws YIFClientCreationException, ParserConfigurationException, YFSException, RemoteException,Exception {
		
		
		
		Document getChargeTranListDoc = XMLUtil.createDocument("ChargeTransactionDetail");
		Element eleOrder = getChargeTranListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_CHARGE_TRANSACTION_KEY, chargeTransactionKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_CHARGE_TRANSACTION_LIST,getChargeTranListDoc);
		Element chargeTranElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_CHARGE_TRANSACTION_DETAILS).item(0);
		String openAuthAmtStr = chargeTranElem.getAttribute(VSIConstants.ATTR_OPEN_AUTHORIZED_AMOUNT);
		String requestAmtStr = chargeTranElem.getAttribute(VSIConstants.ATTR_REQUEST_AMOUNT);
		
        double requestAmt = Double.parseDouble(requestAmtStr);
        double openauthAmt = -Double.parseDouble(openAuthAmtStr);
        
		////System.out.println("request amt is " + requestAmt);
		////System.out.println("openauth amt is " + openauthAmt);


        double diffAmount;
         if (openauthAmt>requestAmt){
        	 diffAmount=openauthAmt-requestAmt;
        	 
     		////System.out.println("diff amt is " + diffAmount);

        	String maxReqAmount=Double.toString(diffAmount);
        	Document createCTR = XMLUtil.createDocument("ChargeTransactionRequestList");
     		Element eleCTR = createCTR.getDocumentElement();
     		eleCTR.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
     		
     		 Element eleCTRReq = XMLUtil.createElement( createCTR, "ChargeTransactionRequest", "");
     		eleCTR.appendChild(eleCTRReq);
             String ctrSeqId = VSIDBUtil.getNextSequence(env,VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
     		//Element eleCTRReq = XMLUtil.appendChild(createCTR, eleCTR, VSIConstants.ELE_CHARGE_TRANSACTION_REQ, "12");
     		eleCTRReq.setAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_KEY, chargeTransactionKeyStr);
     		eleCTRReq.setAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_ID, ChargeTransactionRequestId);
     		eleCTRReq.setAttribute(VSIConstants.ATTR_MAX_REQ_AMT, maxReqAmount);
     		
     		api = YIFClientFactory.getInstance().getApi();
     	    api.invoke(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,createCTR);

         }
	}
	


	/**
	 * @param inStruct
	 * @param outStruct
	 * @param env
	 * @return
	 * @throws Exception 
	 */
	private YFSExtnPaymentCollectionOutputStruct doVoid(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env) throws Exception {
				
		////System.out.println("We are in the UE doVoid buddy");
				//System.out.println("AUTH AMOUNT"+inStruct.currentAuthorizationAmount);
				//System.out.println("REQUEST AMOUNT"+ inStruct.requestAmount);
				String strOrderHeaderKey = inStruct.orderHeaderKey;
				String sStoreNo = getShipNode(env,strOrderHeaderKey);
				String sInvoiceNumber = inStruct.orderNo;
				//String sStoreNo = "6101";
				double dRequestAmount = 0.0;
				String sRequestAmount = Double.toString(dRequestAmount);
				String sSvcNo = inStruct.svcNo;
				String sPinNo = inStruct.paymentReference1;
				String sTranID = inStruct.authorizationId;
			
				
					
				Date todaysDate = new Date(); 
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
				String dateToday = formatter.format(todaysDate);
				

				Document GiftCardInput = XMLUtil.createDocument("GiftCard");
				Element GCElement = GiftCardInput.getDocumentElement();
				GCElement.setAttribute("storeNumber", sStoreNo);
				GCElement.setAttribute("cardNumber", sSvcNo);
				GCElement.setAttribute("pinNumber", sPinNo);
				GCElement.setAttribute("date", dateToday);
				GCElement.setAttribute("invoiceNumber", sInvoiceNumber);
				GCElement.setAttribute("amount", sRequestAmount);
				GCElement.setAttribute("transactionID", sTranID);
				
				//JIRA 608
				env.setTxnObject("strOrderHeaderKey", strOrderHeaderKey);
				env.setTxnObject("chargeType", "Void Request");
				recordPayments(env, strOrderHeaderKey, VSIUtils.getDocumentXMLString(GiftCardInput), "Void Request");

				api = YIFClientFactory.getInstance().getApi();
				//OMS-868 :Start
				String strUserName=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_USER_NAME");
				String strPassword=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_PASSWORD");
				String strMerchantNumber=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_MERCHANT_NUMBER");
				String strRoutingID=YFSSystem.getProperty("VSI_PRE_AUTH_COMP_ROUTING_ID");
				GCElement.setAttribute("Username", strUserName);
				GCElement.setAttribute("Password", strPassword);
				GCElement.setAttribute("merchantNumber", strMerchantNumber);
				GCElement.setAttribute("routingID", strRoutingID);
				//OMS-86 :End
				Document outDoc = api.executeFlow(env, "VSISVSSettlement", GiftCardInput);
				String response = VSIUtils.getDocumentXMLString(outDoc);
				////System.out.println("Printing Response in UE:"+response);
				
				//JIRA 608
				recordPayments(env, strOrderHeaderKey, response, "Void Response");
				
				return outStruct = handleResponse(env,outDoc,inStruct,outDoc);
				


				
		
	}

	public void raiseAlert(YFSEnvironment env1, String errorCodeSt,String errorMessageSt, String sOrderNoSt, String sOHKSt,
    		YFSExtnPaymentCollectionInputStruct inStructSt ) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException{
	
 Document createExInput = XMLUtil.createDocument("Inbox");
Element InboxElement = createExInput.getDocumentElement();
InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHKSt);
InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNoSt);
InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, errorMessageSt);
InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, errorCodeSt);
InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,"Credit Card Alert");
InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Gift Card Alert");
InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_GIFT_CARD_ALERT");
        
Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");

InboxElement.appendChild(InboxReferencesListElement);
Element InboxReferencesElement = createExInput.createElement("InboxReferences");
InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderHeaderKey");
InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHKSt);

InboxReferencesListElement.appendChild(InboxReferencesElement);

api = YIFClientFactory.getInstance().getApi();
api.invoke(env1, VSIConstants.API_CREATE_EXCEPTION, createExInput);


}
	
    public void authorizedOrder(YFSEnvironment env, String orderHeaderKeyStr)
            throws Exception
     {
    	Document doc = XMLUtil.createDocument("Order");
    	Element eleOrder = doc.getDocumentElement();
    	eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
    	api = YIFClientFactory.getInstance().getApi();
    	api.executeFlow(env, VSIConstants.SERVICE_AUTHORIZED_ORDER,doc);

      }

    /*
     * JIRA 608
     * This Method is used to capture all the payment request and response in VSI_Payment_Records Table 
     */
    public void recordPayments(YFSEnvironment env, String strOrderHeaderKey, String record, String chargeType) throws ParserConfigurationException {
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
	
   
}// End of VSICollectionGiftCardUEImpl

 	