package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionCreditCardUE;
import com.yantra.yfs.japi.ue.YFSCollectionOthersUE;
import com.yantra.yfs.japi.ue.YFSCollectionStoredValueCardUE;
import com.vsi.oms.userexit.VSIPayPalfuction;
/**
 * 
 * @author Harshit Arora,Nish Pingle
 * @version 1.0	
 * 
 * This class provides BusinessLogic specific to VSI
 * for UserExit YFSCollectionOthers (as part of transaction Payment Execution)
 * It handles PayPal Authorization, Settlement and Refund
 */

public class VSICollectionOthersUEUpdated implements YFSCollectionOthersUE {

	/**
     * Instance of logger
     */
	
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICollectionOthersUEUpdated.class.getName());
	YIFApi api;
	
	public YFSExtnPaymentCollectionOutputStruct collectionOthers(YFSEnvironment env,YFSExtnPaymentCollectionInputStruct inStruct)
	{
		
		try
		{
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		String paymentType = inStruct.paymentType;
		// get the Charge Type.
		String strChargeType = inStruct.chargeType;
		// get the Request Amount
		Double dRequestAmount = inStruct.requestAmount;
		
		// Check if PaymentType is VOUCHERS
		if(VSIConstants.PAYMENT_MODE_VOUCHERS.equalsIgnoreCase(paymentType)){
				if( VSIConstants.CHARGE.equalsIgnoreCase(strChargeType) && dRequestAmount > 0)
				{
			
					//Handle Voucher Charge
					outStruct = handleVoucherCharge(inStruct);
				}
					else if (VSIConstants.CHARGE.equalsIgnoreCase(strChargeType) && dRequestAmount < 0){
					
					outStruct = handleVoucherRefund(env,inStruct);
				}
	}
		else if(VSIConstants.PAYMENT_MODE_PP.equalsIgnoreCase(inStruct.paymentType))
		{
			if("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)){
				doAuthorization(inStruct,outStruct,env);
			}
			else if("CHARGE".equalsIgnoreCase(inStruct.chargeType)){
				
				if(inStruct.requestAmount >= 0.0D){
					doCharge(inStruct,outStruct,env);

				}
				else
				{ 
					doRefund(inStruct,outStruct,env);
				}
				
				
			}
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
	void doAuthorization(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env){

			try{
	
			String strOrderHeaderKey = inStruct.orderHeaderKey;
			String shipNodestr = getShipNode(env,strOrderHeaderKey);
			DecimalFormat df1 = new DecimalFormat("#,###,###.00");
			String currencyCodestr = "USD";
			String versionStr = "98.0";
			String customStr=shipNodestr;
			String transactionIdStr=inStruct.paymentReference1;
			double dRequestAmount = inStruct.requestAmount;
			String requestAmountStr = df1.format(dRequestAmount);
		
			// Fix for OMS-713
			Document getOrderListIn = XMLUtil.createDocument("Order");
			Element eleRootItem = getOrderListIn.getDocumentElement() ;
			eleRootItem.setAttribute("OrderHeaderKey", strOrderHeaderKey);
			
			Document getOrderListOut = VSIUtils.invokeAPI(env, "global/template/api/getOrderListPayPal", "getOrderList", getOrderListIn);
			
			if(log.isDebugEnabled()){
				log.verbose("getOrderList output is: " + XMLUtil.getXMLString(getOrderListOut));
			}
			
			Element eleExtn = (Element) getOrderListOut.getElementsByTagName("Extn").item(0);
			String sPayPalAuthID = eleExtn.getAttribute("PayPalAuthID");
			
			if(log.isDebugEnabled()){
				log.verbose("Paypal AuthID is :" + sPayPalAuthID);
			}
			
			HashMap getTransactionDetailsNVP = VSIPayPalfuction.getTransactionDetails(env,sPayPalAuthID);
			
			if(log.isDebugEnabled()){
				log.verbose("GetTransactionDetails Output" + getTransactionDetailsNVP);
			}
			String sAck = getTransactionDetailsNVP.get("ACK").toString();
			String sPaymentStatus = getTransactionDetailsNVP.get("PAYMENTSTATUS").toString();
			if(sAck.equalsIgnoreCase("Success") && !sPaymentStatus.equalsIgnoreCase("Expired")){
				
				if(log.isDebugEnabled()){
					log.verbose("Auth is not expired. Re-auth not required with PayPal.");
				}
				Document getCommonCodeListInputForAuthBuffer = getCommonCodeListInputForCodeType("DEFAULT","PAYPA_AUTH_BUFFER");
				 Document getCommonCodeOut = getCommonCodeList(env, getCommonCodeListInputForAuthBuffer);
				Element eleCOmmonCodeOut = (Element)getCommonCodeOut.getElementsByTagName("CommonCode").item(0);
				//String  retryMinutes="10";
				 String authBufferTime = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
				 int authBufferminute=23;
				authBufferminute = Integer.parseInt(authBufferTime);
				

				 Calendar calendar = Calendar.getInstance();
                 calendar.add(Calendar.DATE, authBufferminute);  
				String authExpDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
				outStruct.authCode = sPayPalAuthID;
				outStruct.authorizationAmount=inStruct.requestAmount;
				outStruct.retryFlag = "N";
				outStruct.authorizationExpirationDate=authExpDate;
				 
			} else {
			
				if(log.isDebugEnabled()){
					log.verbose("Auth is expired. Re-auth required.");
				}
			HashMap nvp = VSIPayPalfuction.DoAuthorization(env,currencyCodestr,customStr,transactionIdStr,requestAmountStr,strOrderHeaderKey); 
			//Printing the Response
			if(log.isDebugEnabled()){
	    		log.debug("Response:"+nvp.toString());
			}
			 Document doc = XMLUtil.createDocument("PaymentRecords");
	     		Element elePayRecrds = doc.getDocumentElement();
	     		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
	     				strOrderHeaderKey);
	     		elePayRecrds.setAttribute("Record", nvp.toString());
	     		elePayRecrds.setAttribute("ChargeType", "Authorize Response");
	     		YIFApi api;
	     		api = YIFClientFactory.getInstance().getApi();
	     		api.executeFlow(env,"VSIPaymentRecords", doc);
			
			String strAck = nvp.get("ACK").toString();
	        ////System.out.println("Autorization is" + strAck);
	        
			if ("Success".equalsIgnoreCase(strAck))
			{
 				 	    String authorizationIdStr = nvp.get("TRANSACTIONID").toString();
				        requestAmountStr = nvp.get("AMT").toString();
				        double authAmountStr = Double.parseDouble(requestAmountStr);
						//For BOP-206
					   //OMS-876-START-Uncommented the commented code for OMS-876: Praveen BN
				        outStruct.authorizationId = authorizationIdStr;
						//OMS-876-END-Uncommented the commented code for OMS-876: Praveen BN
						
				        outStruct.authCode = authorizationIdStr;
						outStruct.authorizationAmount=authAmountStr;
						//outStruct.collectionDate = new Date();
			    	 // outStruct.executionDate = new Date();
						outStruct.retryFlag = "N";
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, 7);  
                        String authExpDate = new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime());
                        outStruct.authorizationExpirationDate=authExpDate;
                        //authorizedOrder(env,strOrderHeaderKey);
                        
                        //For BOP-206
                        
                        Document changeOrderDoc = XMLUtil.createDocument("Order");
                		Element eleOrder = changeOrderDoc.getDocumentElement();
                		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
						Element extnOrder = XMLUtil.appendChild(changeOrderDoc, eleOrder, "Extn", "");
						extnOrder.setAttribute("PayPalAuthID", authorizationIdStr);
                		api = YIFClientFactory.getInstance().getApi();
                		Document outDoc = api.invoke(env, "changeOrder",changeOrderDoc);
                        

			}
			else{
				String errorCodeStr = nvp.get("L_ERRORCODE0").toString();
				if(errorCodeStr.equals("10001")){
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
					outStruct.retryFlag="Y";
				}
				else{
				String errorMessage = nvp.get("L_SHORTMESSAGE0").toString();
				String orderNoStr=inStruct.orderNo;
				String errorMessageStr=errorMessage;
				
				payPalAlert(env,errorCodeStr,errorMessageStr,orderNoStr,strOrderHeaderKey,inStruct);
				authFailureCancel(env,strOrderHeaderKey,outStruct);
				}


			}
		}
			
			
			
			
		}
		catch(Exception Ex){
			Ex.printStackTrace();
			throw new YFSException();
		}
			
								
				
		}
	
	void doCharge(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env){
    		    	
    	try{
    		
		String orderHeaderKeyStr = inStruct.orderHeaderKey;
		String shipNodestr = getShipNode(env,orderHeaderKeyStr);
		DecimalFormat df1 = new DecimalFormat("#,###,###.00");
		String currencyCodestr = "USD";
		String versionStr = "98.0";
		//**For Bop 206
		//String authorizationIdStr=inStruct.authorizationId;
		
		   Document getOrderListDoc = XMLUtil.createDocument("Order");
   		Element eleOrder = getOrderListDoc.getDocumentElement();
   		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		env.setApiTemplate("getOrderList", "global/template/api/getOrderListPayPal.xml");
   		api = YIFClientFactory.getInstance().getApi();
   		Document outDoc = api.invoke(env, "getOrderList",getOrderListDoc);
		env.clearApiTemplates();
		
		Element extnOrder = (Element) outDoc.getElementsByTagName("Extn").item(0);
		
		String authorizationIdStr=extnOrder.getAttribute("PayPalAuthID");
		
		//For Bop 206 **
		double dRequestAmount = inStruct.requestAmount;
		String requestAmountStr = df1.format(dRequestAmount);
		String completeType="NotComplete";
			String Notes=shipNodestr;
			
		HashMap nvp = VSIPayPalfuction.DoCapture(env,currencyCodestr,Notes,completeType,authorizationIdStr,requestAmountStr,orderHeaderKeyStr); 
		String strAck = nvp.get("ACK").toString();
		Document doc = XMLUtil.createDocument("PaymentRecords");
 		Element elePayRecrds = doc.getDocumentElement();
 		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
 				orderHeaderKeyStr);
 		elePayRecrds.setAttribute("Record", nvp.toString());
 		elePayRecrds.setAttribute("ChargeType", "Settlement Response");
 		YIFApi api;
 		api = YIFClientFactory.getInstance().getApi();
 		api.executeFlow(env,"VSIPaymentRecords", doc);
		
	    //System.out.println("Charge is" + strAck);
	    if ("Success".equalsIgnoreCase(strAck))
	    {
	    	    String authorizationId = nvp.get("AUTHORIZATIONID").toString();
	    	    String transactionId = nvp.get("TRANSACTIONID").toString();
		        requestAmountStr = nvp.get("AMT").toString();
		        double authAmountStr = Double.parseDouble(requestAmountStr);
		    	//**For Bop 206
		       // outStruct.authorizationId = authorizationId;
				outStruct.authorizationAmount=authAmountStr;
				outStruct.collectionDate = new Date();
	    	    outStruct.executionDate = new Date();
	    	    outStruct.retryFlag = "N";
	    	    
	    	   //inserting the transctionId to custom Table
	    	    createTransactionCharge(env,orderHeaderKeyStr,transactionId,requestAmountStr);
	    	    
	    	   	    }
	    
	    else{
	    
				String errorCodeStr = nvp.get("L_ERRORCODE0").toString();
				if(errorCodeStr.equals("10001")){
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
					outStruct.retryFlag="Y";
				}
			else{
			String errorMessage = nvp.get("L_SHORTMESSAGE0").toString();
			String errorMessageStr=errorMessage;
			
			String orderNoStr=inStruct.orderNo;
			
			payPalAlert(env,errorCodeStr,errorMessageStr,orderNoStr,orderHeaderKeyStr,inStruct);
			outStruct.retryFlag="N";
			outStruct.holdOrderAndRaiseEvent = true;
			outStruct.holdReason = "VSI_PAYMENT_HOLD";
			}
			

		}
    	}
    	catch(Exception Ex)
    	{
    		Ex.printStackTrace();
			throw new YFSException();
    		
    	}
    }
	
	
	void doRefund(YFSExtnPaymentCollectionInputStruct inStruct,
			YFSExtnPaymentCollectionOutputStruct outStruct, YFSEnvironment env){
    		    	
    	try{
    		
		////System.out.println("Inside Refund Charge");
		String orderHeaderKeyStr = inStruct.orderHeaderKey;
		String shipNodestr = getShipNode(env,orderHeaderKeyStr);
		DecimalFormat df1 = new DecimalFormat("#,###,###.00");
		String currencyCodestr = "USD";
		String versionStr = "98.0";
		double dRequestAmount = -inStruct.requestAmount;
		String requestAmountStr = df1.format(dRequestAmount);
        String Notes=shipNodestr;
        //System.out.println("doRefund_orderHeaderKeyStr::"+orderHeaderKeyStr);
		Element orderLineElem=getTransactionId(env,orderHeaderKeyStr,requestAmountStr);
		
		
		String transactionIdstr = orderLineElem.getAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_ID);
		String transactionIdkey = orderLineElem.getAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_Key);
		String transactionAmt = orderLineElem.getAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_AMT);
		
		double dtransactionAmt=Double.parseDouble(transactionAmt);
		double updateAmount=dtransactionAmt-dRequestAmount;
	
		////System.out.println("Tr ID 1"+transactionIdstr);
		////System.out.println("Tr Key 1"+transactionIdkey);
		
		String refundType="Partial";
				
		HashMap nvp = VSIPayPalfuction.DoRefund(env,currencyCodestr,Notes,refundType,transactionIdstr,requestAmountStr,orderHeaderKeyStr); 
		Document doc = XMLUtil.createDocument("PaymentRecords");
 		Element elePayRecrds = doc.getDocumentElement();
 		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
 				orderHeaderKeyStr);
 		elePayRecrds.setAttribute("Record", nvp.toString());
 		elePayRecrds.setAttribute("ChargeType", "Refund Response");
 		YIFApi api;
 		api = YIFClientFactory.getInstance().getApi();
 		api.executeFlow(env,"VSIPaymentRecords", doc);
		String strAck = nvp.get("ACK").toString();
	    ////System.out.println("Refund is" + strAck);

	    if ("Success".equalsIgnoreCase(strAck))
	    {
	    		outStruct.authorizationAmount=inStruct.requestAmount;
				outStruct.collectionDate = new Date();
	    	    outStruct.executionDate = new Date();
	    	    outStruct.retryFlag = "N";
	    	    
	    	   //update charge transaction with left amount
	    	    changeTransactionCharge(env,orderHeaderKeyStr,transactionIdstr,updateAmount,transactionIdkey);

	    }
	    
	    
	    else{
	    	String errorCodeStr = nvp.get("L_ERRORCODE0").toString();
			if(errorCodeStr.equals("0001")){
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
				outStruct.retryFlag="Y";
			}
			else{
				
			String errorMessage = nvp.get("L_SHORTMESSAGE0").toString();
      		String errorMessageStr=errorMessage;
			
			String orderNoStr=inStruct.orderNo;
			
			payPalAlert(env,errorCodeStr,errorMessageStr,orderNoStr,orderHeaderKeyStr,inStruct);
			outStruct.retryFlag="N";
			outStruct.holdOrderAndRaiseEvent = true;
			outStruct.holdReason = "VSI_PAYMENT_HOLD";
			}
			

		}
    	}
    	catch(Exception Ex)
    	{
    		Ex.printStackTrace();
			throw new YFSException();	
    	}
    }
		

	public String getShipNode(YFSEnvironment env, String orderHeaderKeyStr) throws Exception
	{
		Document getOrderListDoc = XMLUtil.createDocument("Order");
		Element eleOrder = getOrderListDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_DETAILS,getOrderListDoc);
		Element orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		String shipNodestr = orderLineElem.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		////System.out.println("shipnode is " + shipNodestr);
		return shipNodestr;
	}
	
	public void createTransactionCharge(YFSEnvironment env, String orderHeaderKeyStr, String transactionId,String requestAmountStr)
			throws Exception
	{
		Document doc = XMLUtil.createDocument("TransactionCharge");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_ID, transactionId);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_AMT, requestAmountStr);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env, VSIConstants.SERVICE_CREATE_PAYPAL_TRANSACTION,doc);
		
	}
	
	public Element getTransactionId(YFSEnvironment env, String orderHeaderKeyStr,String requestAmountStr ) throws Exception
	{
		String qryType="GE";
		Document doc = XMLUtil.createDocument("TransactionCharge");
		Element eleOrder = doc.getDocumentElement();
		//System.out.println("getTransactionId_orderHeaderKeyStr::"+orderHeaderKeyStr);
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_AMT_QRY, qryType);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_AMT, requestAmountStr);
		String xml=VSIUtils.getDocumentXMLString(doc);
		////System.out.println("Input XML is" + xml);
		
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env, VSIConstants.SERVICE_GET_PAYPAL_TRANSACTION,doc);
	    Element orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_TRANSACTION_LINE).item(0);
		String transactionIdstr = orderLineElem.getAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_ID);
		String transactionIdkey = orderLineElem.getAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_Key);
		////System.out.println("Tr ID"+transactionIdstr);
		////System.out.println("Tr Key"+transactionIdkey);
		return orderLineElem;
	}
	
	public void changeTransactionCharge(YFSEnvironment env, String orderHeaderKeyStr, String transactionIdStr,double updateAmount,
			String transactionKey)
			throws Exception
	{
		String requestAmountStr=Double.toString(updateAmount);
		Document doc = XMLUtil.createDocument("TransactionCharge");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_ID, transactionIdStr);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_AMT, requestAmountStr);
		eleOrder.setAttribute(VSIConstants.ATTR_PAYPAL_TRANSACTION_Key, transactionKey);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env, VSIConstants.SERVICE_CHANGE_PAYPAL_TRANSACTION,doc);
		
	}
	
	
	
	public void authorizedOrder(YFSEnvironment env, String orderHeaderKeyStr)
			throws Exception
	{
		Document doc = XMLUtil.createDocument("Order");
		Element eleOrder = doc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKeyStr);
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.executeFlow(env, VSIConstants.SERVICE_AUTHORIZED_ORDER,doc);
		
	}
	

    public void payPalAlert(YFSEnvironment env, String errorCodeStr,String errorMessageStr, String sOrderNo, String sOHK,
    		YFSExtnPaymentCollectionInputStruct inStruct) 
    		throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
    	
    	    
    	if("AUTHORIZATION".equalsIgnoreCase(inStruct.chargeType)){
    		String failureTypeStr="PayPal Authorization Failure";
    		raiseAlert(env,errorCodeStr,errorMessageStr,sOrderNo,sOHK,inStruct,failureTypeStr);
		}
		else if("CHARGE".equalsIgnoreCase(inStruct.chargeType)){
			
			if(inStruct.requestAmount >= 0.0D){
			String failureTypeStr="PayPal Charge Failure";
    		raiseAlert(env,errorCodeStr,errorMessageStr,sOrderNo,sOHK,inStruct,failureTypeStr);


			}
			else
			{ 
			String failureTypeStr="PayPal Refund Failure";
    		raiseAlert(env,errorCodeStr,errorMessageStr,sOrderNo,sOHK,inStruct,failureTypeStr);

			}
		}
		}
        
    	public void raiseAlert(YFSEnvironment env1, String errorCodeSt,String errorMessageSt, String sOrderNoSt, String sOHKSt,
    	    		YFSExtnPaymentCollectionInputStruct inStructSt,String failureTypeStr ) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException{
    		
    	 Document createExInput = XMLUtil.createDocument("Inbox");
        Element InboxElement = createExInput.getDocumentElement();
        InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHKSt);
        InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNoSt);
        InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
        InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, errorMessageSt);
        InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, errorCodeSt);
        InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,"PayPal Payment Alert");
        InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, failureTypeStr);
        InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
        InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_PAYPAL_ALERT");
                
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
   
    private void authFailureCancel(YFSEnvironment env,String sOHK, YFSExtnPaymentCollectionOutputStruct outStruct)
    		throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException
    {
    	Document orderLine = XMLUtil.createDocument("OrderLine");
        Element OrderLineEle = orderLine.getDocumentElement();
        OrderLineEle.setAttribute(VSIConstants.ATTR_STATUS, "3700");
        OrderLineEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
             
        api = YIFClientFactory.getInstance().getApi();
        Document outDoc= api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST, orderLine);
       
        NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
        int orderLineCount = orderLineList.getLength();


        
    	if(orderLineCount>0){
    		
    		outStruct.retryFlag="N";
			outStruct.holdOrderAndRaiseEvent = true;
			outStruct.holdReason = "VSI_PAYMENT_HOLD";
    		
    	}
    	                            
    	else{
		            String cancellationReasonCode = "AUTH_CANCEL_ORDER";
		           	String cancellationReasonText = "AUTH_CANCEL_ORDER";
    		        Document cancelInput = XMLUtil.createDocument("Order");
    	            Element OrderEle = cancelInput.getDocumentElement();
                    OrderEle.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
                    OrderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
					OrderEle.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
					cancellationReasonCode);
			OrderEle.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT,
					cancellationReasonText);
                    api = YIFClientFactory.getInstance().getApi();
                    api.invoke(env, VSIConstants.API_CHANGE_ORDER, cancelInput);
    	}
 
                                    
                                    
                                    
    }


    private YFSExtnPaymentCollectionOutputStruct handleVoucherCharge(
			final YFSExtnPaymentCollectionInputStruct inStruct) {

		final YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
	
		outStruct.tranAmount = inStruct.requestAmount;

		outStruct.tranType = inStruct.chargeType;
		outStruct.authorizationAmount = inStruct.requestAmount;
		outStruct.collectionDate = new Date();
		outStruct.executionDate = new Date();
		outStruct.requestID = inStruct.paymentReference1;

		return outStruct;

	} // End of handleVoucherCharge()
	
	  private YFSExtnPaymentCollectionOutputStruct handleVoucherRefund(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException {
		
    	final YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
    	String sOHK = inStruct.orderHeaderKey;
    	String sOrderNo = inStruct.orderNo; 
    	outStruct.authorizationAmount=inStruct.requestAmount;
		outStruct.collectionDate = new Date();
	    outStruct.executionDate = new Date();
	    outStruct.retryFlag = "N";
	    raiseAlertVoucher(env,sOHK,sOrderNo);   
		return outStruct;
	}

	private void raiseAlertVoucher(YFSEnvironment env, String sOHK, String sOrderNo) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Voucher Should be Refunded");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "An Order with voucher is returned");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Voucher");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Voucher Refund");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_VOUCHER_REFUND");
		
		
		Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
		
		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHK);
		
		InboxReferencesListElement.appendChild(InboxReferencesElement);
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
	}
	public static Document getCommonCodeListInputForCodeType(String sOrgCode,
			String codeType) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeType", codeType);
		return docOutput;
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
	public YFSExtnPaymentCollectionOutputStruct collectionStoredValueCard(
			YFSEnvironment arg0, YFSExtnPaymentCollectionInputStruct arg1)
			throws YFSUserExitException {
		// TODO Auto-generated method stub
		return null;
	}
}// End of collectionOtherClass

