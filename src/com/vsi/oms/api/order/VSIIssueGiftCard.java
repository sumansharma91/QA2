package com.vsi.oms.api.order;


import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class VSIIssueGiftCard {

	private YFCLogCategory log = YFCLogCategory.instance(VSIIssueGiftCard.class);
	
	public void issueGiftCard (YFSEnvironment env, Document inXML){
		if(log.isDebugEnabled()){
			log.info("Inside VSIIssueGiftCard : issueGiftCard : inXML : \n"+XMLUtil.getXMLString(inXML));
		}
		
			try {
				
				if(inXML != null){
					
						
						invokeChangeOrder(env, inXML);
						
						
					}
				
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		
	}
	
	
	
	private void updateVGCAsCardUsed(YFSEnvironment env,
			Document inXML, Document docVGCDetailsDoc, int refundAmount) throws Exception {
		// this method will mark the Gift card as activated and will update the corresponding order No
		if(log.isDebugEnabled()){
			log.info("Inside updateVGCAsActivated : Input :\n"+XMLUtil.getXMLString(docVGCDetailsDoc));
		}
		Element rootEle = docVGCDetailsDoc.getDocumentElement();
		rootEle.setAttribute("IsCardUsed", "Y");
		// Set the IsActivated flag to Y and the Card Amount to the amount to be refunded.
		//rootEle.setAttribute("IsActivated", "Y");
		//rootEle.setAttribute("CardAmount", String.valueOf(refundAmount));
		
		rootEle.setAttribute("OrderNo", inXML.getDocumentElement().getAttribute("OrderNo"));
		
		if(log.isDebugEnabled()){
			log.info("Inside updateVGCAsCardUsed : Input XML to VSIUpdateOnlineGCDetails : \n"+XMLUtil.getXMLString(docVGCDetailsDoc));
		}
		VSIUtils.invokeService(env, "VSIUpdateOnlineGCDetails", docVGCDetailsDoc);
		
		
	}

	private void invokeChangeOrder(YFSEnvironment env, Document inXML) throws Exception {
		if(log.isDebugEnabled()){
			log.info("Inside invokeChangeOrder : Input :\n"+XMLUtil.getXMLString(inXML));
		}
		
		Element inXMLRootEle = inXML.getDocumentElement();
		
		// Get the order no to check if the card is issued already.
		String orderNo = inXMLRootEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String refundamt = inXMLRootEle.getAttribute("RefundAmount");
		double refundAmt=-Double.parseDouble(refundamt);
		String negAmt=Double.toString(refundAmt);
		

		String ohk = inXMLRootEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		String paymentKey = null;
		int refundAmount = 0;
				
		Document getOrdListIpDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		getOrdListIpDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		Document getOrdListOpDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrdListForVGCDetails.xml", VSIConstants.API_GET_ORDER_LIST, getOrdListIpDoc);
		if(log.isDebugEnabled()){
			log.info("getOrdListOpDoc : \n"+XMLUtil.getXMLString(getOrdListOpDoc));
		}
		
		
		if(getOrdListOpDoc != null){
			Element getOrdListOrdEle = (Element) getOrdListOpDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			NodeList payMthdNL = getOrdListOrdEle.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
			int length = payMthdNL.getLength();
			Document changeOrderIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element changeOrdIpRtEle = changeOrderIp.getDocumentElement();
			
			changeOrdIpRtEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
			changeOrdIpRtEle.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
			changeOrdIpRtEle.setAttribute("Override", "Y");
			
			Element payMthdsEle = changeOrderIp.createElement("PaymentMethods");
			
			
			for(int k=0; k < length; k++){
				Element gOLPayMthdEle = (Element) payMthdNL.item(k);
				String paymentType = gOLPayMthdEle.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);			


				String svcNo = gOLPayMthdEle.getAttribute(VSIConstants.ATTR_SVC_NO);

				if(log.isDebugEnabled()){
					log.info("paymentType = "+paymentType);
					log.info("svcNo = "+svcNo);
				}
				
				if(paymentType.equals("ONLINE_GIFT_CARD")  && YFCObject.isVoid(svcNo)){

					// Method call to check if the GC is issued for the order for the amount. 
				//	boolean isGCIssued = checkIfGCIssued(env, orderNo,getOrdListOrdEle);

					// If GC is not issued for the order, issue a new one.
					//if(!isGCIssued){
						paymentKey = gOLPayMthdEle.getAttribute("PaymentKey");
						if(log.isDebugEnabled()){
							log.info("paymentKey = "+paymentKey);
						}
						if(paymentKey != null && !paymentKey.trim().equals("")){
							Document docVGCDetailsDoc = fetchVGCDetails(env,ohk);
							String sOGCNo = docVGCDetailsDoc.getDocumentElement().getAttribute("GiftCardNo");
							String pinNo = docVGCDetailsDoc.getDocumentElement().getAttribute("PinNo");


							Element payMthdEle = changeOrderIp.createElement("PaymentMethod");
							payMthdsEle.appendChild(payMthdEle);

							payMthdEle.setAttribute("PaymentKey", paymentKey);
							payMthdEle.setAttribute(VSIConstants.ATTR_SVC_NO, sOGCNo);
							payMthdEle.setAttribute("PaymentReference1", sOGCNo);
							payMthdEle.setAttribute("PaymentReference2", pinNo);
							Element payDetails = XMLUtil.appendChild(changeOrderIp,payMthdEle,"PaymentDetails", "");
							payDetails.setAttribute("RequestAmount", negAmt);
							payDetails.setAttribute("ChargeType","CHARGE");

							/*
							 * <Order OrderHeaderKey="<ohk>" Action="MODIFY" Override="Y">
							 * 		<PaymentMethods>
							 * 			<PaymentMethod PaymentKey="<The fetched key from getOrderList output>" SvcNo="<GiftCardNo>" PaymentReference1="<GiftCardNo>"
							 * PaymentReference2="<PinNo> />
							 * 		</PaymentMethods>
							 * </Order>
							 */
							updateVGCAsCardUsed(env,inXML, docVGCDetailsDoc, refundAmount);
						}

						changeOrdIpRtEle.appendChild(payMthdsEle);
						if(log.isDebugEnabled()){
							log.info("Input to changeOrder API : \n"+XMLUtil.getXMLString(changeOrderIp));
						}
						VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, changeOrderIp);

					}
					
				}
				
//			}
			
		}
		
		
		
	}

	private boolean checkIfGCIssued(YFSEnvironment env, String orderNo,
			Element getOrdListOrdEle) throws Exception {

		boolean isIssued = false;
		int totalNumberOfRecords = 0;
		double refundAmount = 0.0;
//		 Calculate the refund amount. GC would be granted if the orders placed using VOUCHERS or GIFT_CARD are returned ar cancelled.
		
		NodeList payMthdNL = getOrdListOrdEle.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
		for(int i = 0; i < payMthdNL.getLength(); i++){
			Element elePaymentMethod = (Element)payMthdNL.item(i);
			if(!YFCObject.isVoid(elePaymentMethod)){
				String strChargedAmount = elePaymentMethod.getAttribute("TotalCharged");
				if(!YFCObject.isVoid(strChargedAmount))
					refundAmount = refundAmount + Double.parseDouble(strChargedAmount);
			}
		}
		Document inputDoc = XMLUtil.createDocument("VSIOnlineGiftCard");
		Element rootEle = inputDoc.getDocumentElement();
		rootEle.setAttribute("OrderNo", orderNo);
		rootEle.setAttribute("CardAmount", String.valueOf(refundAmount));
		if(log.isDebugEnabled()){
			log
			.info("Inside checkIfGCIssued : Input XML to VSIFetchOnlineGCDetails to check if any GC are issued : \n"
					+ XMLUtil.getXMLString(inputDoc));
		}
		Document outDoc = VSIUtils.invokeService(env,
				"VSIFetchOnlineGCDetails", inputDoc);
		if(log.isDebugEnabled()){
			log
			.info("Inside checkIfGCIssued : Output XML from VSIFetchOnlineGCDetails to check if any GC are issued : \n"
					+ XMLUtil.getXMLString(outDoc));
		}
		if(outDoc != null){
			Element rootVGCEle = outDoc.getDocumentElement();
			if(rootVGCEle != null){
				totalNumberOfRecords = Integer.parseInt(rootVGCEle.getAttribute("TotalNumberOfRecords"));
				if (totalNumberOfRecords > 0)
					isIssued = true;
			}
		}

		return isIssued;
	}



	private Document fetchVGCDetails(YFSEnvironment env, String ohk) throws Exception {
		
		
		int totalNumberOfRecords = 0;
		int length = 0;
		Document docVGCDetailsDoc = XMLUtil.createDocument("VSIOnlineGiftCard");
		Document inputDoc = XMLUtil.createDocument("VSIOnlineGiftCard");
		Element rootEle = inputDoc.getDocumentElement();
		rootEle.setAttribute("IsCardUsed", "N");
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
				totalNumberOfRecords = Integer.parseInt(rootVGCEle.getAttribute("TotalNumberOfRecords"));
				NodeList nlVGCList = rootVGCEle.getElementsByTagName("VSIOnlineGiftCard");
				length = nlVGCList.getLength();
				if(log.isDebugEnabled()){
					log.info("Total number of Online Gift Cards are : "+totalNumberOfRecords);
				}
				checkThreshold(env,totalNumberOfRecords);
				if(length > 0){
					Element eleVGC = (Element) nlVGCList.item(0);
					String giftCardNo = eleVGC.getAttribute("GiftCardNo");
					String pinNo = eleVGC.getAttribute("PinNo");
					String giftCrdKey = eleVGC.getAttribute("GiftCardKey");
					if(giftCardNo != null && pinNo != null){
						docVGCDetailsDoc.getDocumentElement().setAttribute("GiftCardNo", giftCardNo);
						docVGCDetailsDoc.getDocumentElement().setAttribute("PinNo", pinNo);
						docVGCDetailsDoc.getDocumentElement().setAttribute("GiftCardKey", giftCrdKey);
					}
				}
			}
		}
		
		if(log.isDebugEnabled()){
			log.info("Exiting fetchVGCDetails : docVGCDetailsDoc : \n"+XMLUtil.getXMLString(docVGCDetailsDoc));
		}
		return docVGCDetailsDoc;
	}



	private void checkThreshold(YFSEnvironment env, int totalNumberOfRecords) {

		if(log.isDebugEnabled()){
			log.info("Inside checkThreshold : ");
		}
		Document docCommonCodeListInput = null;
		Document docCommonCodeListOutput = null;
		int threshold = 0;
		try {
			docCommonCodeListInput = XMLUtil.createDocument("CommonCode");
			Element eleCommonCodeListInput = docCommonCodeListInput
					.getDocumentElement();
			eleCommonCodeListInput.setAttribute("CodeType", "GCTHRESHOLD");
			if(log.isDebugEnabled()){
				log.info("Inside checkThreshold input to getCommonCode List api : "+XMLUtil.getXMLString(docCommonCodeListInput));
			}
			docCommonCodeListOutput = VSIUtils.invokeAPI(env,
					VSIConstants.API_COMMON_CODE_LIST, docCommonCodeListInput);
			if(log.isDebugEnabled()){
				log.info("Inside checkThreshold output of getCommonCode List api : "+XMLUtil.getXMLString(docCommonCodeListOutput));
			}
			Element commonCode = (Element) docCommonCodeListOutput
					.getDocumentElement().getElementsByTagName("CommonCode")
					.item(0);
			threshold = Integer.parseInt(commonCode.getAttribute("CodeValue"));
			if(log.isDebugEnabled()){
				log.info("Total number of unused GiftCards are "+totalNumberOfRecords+". The threshold is "+threshold+".");
			}
			if (totalNumberOfRecords < threshold) {
				raiseAlert(env);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void raiseAlert(YFSEnvironment env) throws YFSException, RemoteException,
			YIFClientCreationException, ParserConfigurationException {

		String reason = "The number of Gift Cards are below the threshold value.";
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();

		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"VSI_ALERT_GIFT_CARD");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"VSI_ALERT_GIFT_CARD");
		InboxElement.setAttribute("Consolidate",
		"Y");
		
		Element consolidationTemplate = createExInput
		.createElement("ConsolidationTemplate");
		InboxElement.appendChild(consolidationTemplate);
		Element inbox = createExInput
		.createElement("Inbox");
		inbox.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		inbox.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"VSI_ALERT_GIFT_CARD");
		inbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"VSI_ALERT_GIFT_CARD");
		
		consolidationTemplate.appendChild(inbox);
		
		
		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				createExInput);

	}	
}
