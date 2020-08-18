package com.vsi.oms.api.order;


import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessSVSResponseForOGCActivation {

	private  YFCLogCategory log = YFCLogCategory.instance(VSIProcessSVSResponseForOGCActivation.class);

	public Document vsiInvokeAndProcessSVSResponse(YFSEnvironment env, Document docSVSRequest) throws YFSException
	{
		if(log.isDebugEnabled()){
			log.info("Inside VSIProcessSVSResponseForOGCActivation: vsiProcessSVSResponse: docSVSRequest: \n"+XMLUtil.getXMLString(docSVSRequest));
		}
		if(docSVSRequest != null)
		{
			String callingFlow = docSVSRequest.getDocumentElement().getAttribute(VSIConstants.ATTR_CALLING_FLOW);
			docSVSRequest.getDocumentElement().removeAttribute(VSIConstants.ATTR_CALLING_FLOW);
			if(log.isDebugEnabled()){
				log.info("Invoking VSIInvokeSVSForOGCActivation ....");
			}
			Document docSVSResponse;
			try {
				//OMS-868 :Start
				String strUserName=YFSSystem.getProperty("VSI_OGC_USER_NAME");
				String strPassword=YFSSystem.getProperty("VSI_OGC_PASSWORD");
				String strMerchantNumber=YFSSystem.getProperty("VSI_OGC_MERCHANT_NUMBER");
				String strRoutingID=YFSSystem.getProperty("VSI_OGC_ROUTING_ID");
				Element eleSVSRequestRoot=docSVSRequest.getDocumentElement();
				eleSVSRequestRoot.setAttribute("Username", strUserName);
				eleSVSRequestRoot.setAttribute("Password", strPassword);
				eleSVSRequestRoot.setAttribute("merchantNumber", strMerchantNumber);
				eleSVSRequestRoot.setAttribute("routingID", strRoutingID);
				//OMS-86 :End
				docSVSResponse = VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_INVOKE_SVS_FOR_OGC_ACTIVATION, docSVSRequest);
				
				/*
				//Dummy response added to by pass VSIInvokeSVSForOGCActivation Service - START
				docSVSResponse = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleSVSResponse = docSVSResponse.getDocumentElement();
				Element eleReturnCode = SCXmlUtil.createChild(eleSVSResponse, "returnCode");
				Element eleCode = SCXmlUtil.createChild(eleReturnCode, "Code");
				eleCode.setTextContent("01");
				Element eleDesc = SCXmlUtil.createChild(eleReturnCode, "Desc");
				eleDesc.setTextContent("Approved");
				//Dummy response added to by pass VSIInvokeSVSForOGCActivation Service - End
				*/
				
				if(log.isDebugEnabled()){
					log.info("Invoked VSIInvokeSVSForOGCActivation successfully .... Response is : \n"+XMLUtil.getXMLString(docSVSResponse));
				}
				Element returnCode = (Element) docSVSResponse.getElementsByTagName(VSIConstants.ELE_RETURN_CODE).item(0);
				String sRetcode = returnCode.getFirstChild().getTextContent();
				String sRetDesc = returnCode.getLastChild().getTextContent();
				if(sRetcode.equalsIgnoreCase("01Approval")||sRetcode.equalsIgnoreCase("01")
						||sRetDesc.equalsIgnoreCase("Approval") || sRetDesc.equalsIgnoreCase("Approved"))
				{
						
					if(YFCCommon.isVoid(callingFlow) || VSIConstants.STR_CUST_APPEASE.equals(callingFlow))
					{
						updateVGCAsActivated(env,docSVSRequest);
						triggerEmail(env,docSVSRequest);
					}
				}else if (sRetcode.equalsIgnoreCase("15"))
				{
					boolean activationSuccess = false;
					for (int count = 1; count < 4;count++){

						// if calling flow is void, continue to raise alert. otherwise the call is made from UI. skip alert raising part 
						if(YFCCommon.isVoid(callingFlow))
						{
							raiseAlert(env, docSVSRequest,VSIConstants.STR_VSI_SVS_OFFLINE_QUEUE,docSVSResponse);
						}
						docSVSResponse = VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_INVOKE_SVS_FOR_OGC_ACTIVATION, docSVSRequest);
						Element returnCode1 = (Element) docSVSResponse.getElementsByTagName(VSIConstants.ELE_RETURN_CODE).item(0);

						String sRetcode1 = returnCode1.getFirstChild().getTextContent();
						String sRetDesc1 = returnCode1.getLastChild().getTextContent();
						if(sRetcode1.equalsIgnoreCase("01Approval")||sRetcode1.equalsIgnoreCase("01")
								||sRetDesc1.equalsIgnoreCase("Approval") || sRetDesc1.equalsIgnoreCase("Approved")){
							updateVGCAsActivated(env,docSVSRequest);
							// if calling flow is void or CustAppease flow - send email to customer. 
							if(YFCCommon.isVoid(callingFlow) || VSIConstants.STR_CUST_APPEASE.equals(callingFlow))
							{
								triggerEmail(env,docSVSRequest);
							}
							activationSuccess=true;
							break;
						}

					}
					// if activation is not successful after 3 retries throw error back. this is applicable for UI only. 
					if(!activationSuccess && !YFCCommon.isVoid(callingFlow))
					{
						YFSException yy = new YFSException();
						yy.setErrorCode("GC0001");
						yy.setErrorDescription("Gift card Activation was not successful");
						//yy.setStackTrace(e.getStackTrace());
						yy.setAttribute("ErrorCode", "CA0001");
						yy.setAttribute("ErrorDescription", "Gift card Activation was not successful");
						throw yy;
						//throw new YFSException("EXTN_ERROR","EXTN_ERROR","Gift card Activation was not successful");
					}
				}
				else
				{
					// if the return code is NOT approved and NOT 15, then continue to raise alert for non UI scenarios. 
					if(YFCCommon.isVoid(callingFlow))
					{
						raiseAlert(env, docSVSRequest,"VSI_GIFT_CARD",docSVSResponse);
					}
					else
					{
						YFSException yy = new YFSException();
						yy.setErrorCode("GC0001");
						yy.setErrorDescription("Gift card Activation was not successful");
						//yy.setStackTrace(e.getStackTrace());
						yy.setAttribute("ErrorCode", "CA0001");
						yy.setAttribute("ErrorDescription", "Gift card Activation was not successful");
						throw yy;
					}

				}
			} catch (Exception e) 
			{
				// in case of UI flow, throw error back to UI. 
				if(!YFCCommon.isVoid(callingFlow))
				{
					YFSException yy = new YFSException();
					yy.setErrorCode("GC0001");
					yy.setErrorDescription("Gift card Activation was not successful");
					//yy.setStackTrace(e.getStackTrace());
					yy.setAttribute("ErrorCode", "CA0001");
					yy.setAttribute("ErrorDescription", "Gift card Activation was not successful");
					throw yy;

				}
			}
		}
		return null;

	}

	private void triggerEmail(YFSEnvironment env, Document docSVSRequest) throws Exception {
		Document emailDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element docSVSRequestEle = docSVSRequest.getDocumentElement();
		Element emailDocEle = emailDoc.getDocumentElement();
		String orderNo = docSVSRequestEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String amount = docSVSRequestEle.getAttribute(VSIConstants.ATTR_REFUND_AMOUNT);
		String sOGCNo = docSVSRequestEle.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO);
		String pin = docSVSRequestEle.getAttribute(VSIConstants.ATTR_PIN_NO);
		String custFName = "";
		String custLName = "";
		String custEmail = "";
		String entryType = "";


		emailDocEle.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		emailDocEle.setAttribute("Amount", amount);
		emailDocEle.setAttribute("GiftCardNumber", sOGCNo);
		emailDocEle.setAttribute("Pin", pin);
		
		Document getOrdListIp = XMLUtil.createDocument("Order");
		getOrdListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, docSVSRequestEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
		
		Document getOrdListOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrdListForVGCDetails.xml", VSIConstants.API_GET_ORDER_LIST, getOrdListIp);
		if(log.isDebugEnabled()){
			log.info("Inside triggerEmail : getOrdListOp: \n"+XMLUtil.getXMLString(getOrdListOp));
		}
		
		if(getOrdListOp != null){
			Element getOrdListOrdEle = (Element) getOrdListOp.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			if(getOrdListOrdEle != null){
				custFName = getOrdListOrdEle.getAttribute("CustomerFirstName") ;
				custLName = getOrdListOrdEle.getAttribute("CustomerLastName");
				custEmail = getOrdListOrdEle.getAttribute("CustomerEMailID");
				entryType = getOrdListOrdEle.getAttribute("EntryType");
			}
		}
		emailDocEle.setAttribute("FirstName", custFName);
		emailDocEle.setAttribute("LastName", custLName);
		emailDocEle.setAttribute("EmailID", custEmail);
		emailDocEle.setAttribute("EntryType", entryType);
		
		VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_TRIGGER_EMAIL_ON_VGC_REFUND, emailDoc);
	}

	private void retryWithSVSServer(YFSEnvironment env, Document docSVSRequest) {

		try {
			VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_SEND_VGC_ACTIVATION_REQ_TO_SVS, docSVSRequest);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void raiseAlert(YFSEnvironment env, Document docSVSRequest, String queueName, Document docSVSResponse) throws YFSException, RemoteException, YIFClientCreationException, ParserConfigurationException {

		Document createExInput = XMLUtil.createDocument(VSIConstants.ELE_INBOX);
		Element InboxElement = createExInput.getDocumentElement();
		Element docSVSRequestElement = docSVSRequest.getDocumentElement();
		Element returnCode = (Element) docSVSResponse.getElementsByTagName(VSIConstants.ELE_RETURN_CODE).item(0);
		String sRetcode = returnCode.getFirstChild().getTextContent();
		String sRetDesc = returnCode.getLastChild().getTextContent();

		Element tranID = (Element) docSVSResponse.getElementsByTagName(VSIConstants.ELE_TRANSACTION_ID).item(0);
		String sTranID = tranID.getTextContent();

		Element refAmtEle = (Element) docSVSResponse.getElementsByTagName(VSIConstants.ELE_APPROVED_AMOUNT).item(0);
		String sRefAmt = refAmtEle.getFirstChild().getTextContent();

		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, docSVSRequestElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, docSVSRequestElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
		InboxElement.setAttribute(VSIConstants.ATTR_CONSOLIDATE, VSIConstants.FLAG_Y);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG,VSIConstants.FLAG_Y);
		InboxElement.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION,
				"Online Gift Card is not approved for the Refund");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				"Online Gift Card is not approved for the Refund");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"SVS Approval for OGC");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"SVS Approval for OGC");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				queueName);


		Element ConsoltempEle = createExInput
				.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
		InboxElement.appendChild(ConsoltempEle);
		Element InboxCpyEle = createExInput.createElement(VSIConstants.ELE_INBOX);
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, docSVSRequestElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_NO, docSVSRequestElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
		InboxCpyEle.setAttribute(VSIConstants.ATTR_CONSOLIDATE, "Y");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION,
				"Online Gift Card is not approved for the Refund");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				"Online Gift Card is not approved for the Refund");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"SVS Approval for OGC");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"SVS Approval for OGC");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				queueName);
		ConsoltempEle.appendChild(InboxCpyEle);

		Element InboxReferencesListElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES_LIST);
		InboxElement.appendChild(InboxReferencesListElement);

		// OHK ==========================================================
		Element InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ATTR_ORDER_HEADER_KEY);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, docSVSRequestElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// SvsNo ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"SvsNo");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, docSVSRequestElement.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO));

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// PinNo ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ATTR_PIN_NO);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, docSVSRequestElement.getAttribute(VSIConstants.ATTR_PIN_NO));

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// ReturnCode ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ELE_RETURN_CODE);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sRetcode);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// ReturnDescription ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ATTR_RETURN_DESCRIPTION);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sRetDesc);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// TransactionID ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ATTR_PAYPAL_TRANSACTION_ID);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sTranID);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// RefundAmount ==========================================================

		InboxReferencesElement = createExInput
				.createElement(VSIConstants.ELE_INBOX_REFERANCES);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				VSIConstants.ATTR_REFUND_AMOUNT);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				VSIConstants.STR_REPROCESS);
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sRefAmt);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

		// Invoice No is not returned in the SVS Response..  

		if(log.isDebugEnabled()){
			log.info("Printing createExInput: "
					+ XMLUtil.getXMLString(createExInput));
		}
		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				createExInput);



	}

	private void updateVGCAsActivated(YFSEnvironment env, Document docSVSRequest) throws Exception {
		if(log.isDebugEnabled()){
			log.info("Inside updateVGCAsActivated : Input :\n"+XMLUtil.getXMLString(docSVSRequest));
		}
		Document inputDoc = XMLUtil.createDocument(VSIConstants.ELE_VSI_ONLINE_GC);
		Element inputDocEle = inputDoc.getDocumentElement();
		Element rootEle = docSVSRequest.getDocumentElement();
		String strGiftCardNo = rootEle.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO);
		String strGCKey  = fetchVGCKey(env,strGiftCardNo);
		if(!YFCObject.isVoid(strGCKey)){

			inputDocEle.setAttribute(VSIConstants.ATTR_IS_ACTIVATED, VSIConstants.FLAG_Y);
			inputDocEle.setAttribute(VSIConstants.ATTR_GIFT_CARD_KEY, strGCKey);
			inputDocEle.setAttribute(VSIConstants.ATTR_CARD_AMOUNT, rootEle.getAttribute(VSIConstants.ATTR_REFUND_AMOUNT));
			if(log.isDebugEnabled()){
				log.info("Inside updateVGCAsActivated : Input XML to VSIUpdateOnlineGCDetails : \n"+XMLUtil.getXMLString(inputDoc));
			}
			VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_UPDATE_ONLINE_GCDETAILS, inputDoc);
		}
	}


	private String fetchVGCKey(YFSEnvironment env, String giftCardNo) throws Exception {

		if(!YFCObject.isVoid(giftCardNo)){
			int totalNumberOfRecords = 0;
			int length = 0;
			String giftCrdKey = "";

			Document inputDoc = XMLUtil.createDocument(VSIConstants.ELE_VSI_ONLINE_GC);
			Element rootEle = inputDoc.getDocumentElement();
			rootEle.setAttribute(VSIConstants.ATTR_GIFT_CARD_NO, giftCardNo);
			if(log.isDebugEnabled()){
				log.info("Inside fetchVGCDetails : Input XML to VSIFetchOnlineGCDetails : \n"+XMLUtil.getXMLString(inputDoc));
			}
			Document outDoc = VSIUtils.invokeService(env, "VSIFetchOnlineGCDetails", inputDoc);
			if(log.isDebugEnabled()){
				log.info("Inside fetchVGCDetails : Output XML from VSIFetchOnlineGCDetails : \n"+XMLUtil.getXMLString(outDoc));
			}
			if(outDoc != null){
				Element rootVGCEle = outDoc.getDocumentElement();
				if(rootVGCEle != null){
					totalNumberOfRecords = Integer.parseInt(rootVGCEle.getAttribute(VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS));
					NodeList nlVGCList = rootVGCEle.getElementsByTagName(VSIConstants.ELE_VSI_ONLINE_GC);
					length = nlVGCList.getLength();
					if(log.isDebugEnabled()){
						log.info("Total number of Online Gift Cards are : "+totalNumberOfRecords);
					}

					if(length > 0){
						Element eleVGC = (Element) nlVGCList.item(0);

						giftCrdKey = eleVGC.getAttribute(VSIConstants.ATTR_GIFT_CARD_KEY);

					}
				}
			}



			return giftCrdKey;
		}
		else
			return null;
	}

}
