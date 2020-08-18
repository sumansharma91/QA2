package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIIssueEGiftCardForAppeasements {

	private YFCLogCategory log = YFCLogCategory.instance(VSIIssueEGiftCardForAppeasements.class);
	YIFApi api;

	public Document issueEGiftCardForAppeasements (YFSEnvironment env, Document inXML){
		if(log.isDebugEnabled()){
			log.info("Inside VSIIssueGiftCard : issueGiftCard : inXML : \n"+XMLUtil.getXMLString(inXML));
		}
		
		try {

			if(inXML != null){
				Element eleInXML = inXML.getDocumentElement();
				String strOrderHeaderKey = eleInXML.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				String strOfferAmount = eleInXML.getAttribute(VSIConstants.ATTR_OFFER_AMOUNT);
				//fetchAndActivateVGCDetails

				Document docActivatedVGCDetail = fetchAndActivateVGCDetails(env, inXML, strOrderHeaderKey);	
				Element eleActivatedVGCDetail = docActivatedVGCDetail.getDocumentElement();
				String strGiftCardNo = eleActivatedVGCDetail.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO);
				String strPinNo = eleActivatedVGCDetail.getAttribute(VSIConstants.ATTR_PIN_NO);
				eleInXML.setAttribute("Reference1", strGiftCardNo + "_" + strPinNo);
				Element eleExtn = SCXmlUtil.createChild(eleInXML, VSIConstants.ELE_EXTN);
				eleExtn.setAttribute(VSIConstants.ATTR_EXTN_APPEASE_AMOUNT, strOfferAmount);
				VSIUtils.invokeAPI(env,VSIConstants.API_RECORD_INVOICE_CREATION, inXML);
			}

		} 
		catch (YFSException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			YFSException yy = new YFSException();
			yy.setErrorCode("CA0001");
			yy.setErrorDescription("Customer Appeasement Process Failure.");
			yy.setStackTrace(e.getStackTrace());
			yy.setAttribute("ErrorCode", "CA0001");
			yy.setAttribute("ErrorDescription", "Customer Appeasement Process Failure.");
			throw yy;
		}
		return inXML;

	}

	private Document fetchAndActivateVGCDetails(YFSEnvironment env, Document inXML, String strOrderHeaderKey) throws YFSException,Exception {

		int totalNumberOfRecords = 0;
		int length = 0;
		Document docVGCDetailsDoc = XMLUtil.createDocument(VSIConstants.ELE_VSI_ONLINE_GC);
		Element eleVGCDetailsDoc = docVGCDetailsDoc.getDocumentElement();
		Document inputDoc = XMLUtil.createDocument(VSIConstants.ELE_VSI_ONLINE_GC);
		Element rootEle = inputDoc.getDocumentElement();
		rootEle.setAttribute(VSIConstants.ATTR_IS_CARD_USED,VSIConstants.FLAG_N);
		if(log.isDebugEnabled()){
			log.info("Inside fetchVGCDetails : Input XML to VSIFetchOnlineGCDetails : \n"+XMLUtil.getXMLString(inputDoc));
		}
		
		Document outDoc = VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_FETCH_ONLINE_GCDETAILS, inputDoc);

		try
		{
			if(log.isDebugEnabled()){
				log.info("Inside fetchVGCDetails : Output XML from VSIFetchOnlineGCDetails : \n"+XMLUtil.getXMLString(outDoc));
			}
			if(outDoc != null){
				Element rootVGCEle = outDoc.getDocumentElement();
				if(rootVGCEle != null){
					totalNumberOfRecords = Integer.parseInt(rootVGCEle.getAttribute(VSIConstants.ATTR_TOTAL_NUMBER_OF_RECORDS));
					//OMS-1742 start
					checkThreshold(env,totalNumberOfRecords);
					//OMS-1742 end
					NodeList nlVGCList = rootVGCEle.getElementsByTagName(VSIConstants.ELE_VSI_ONLINE_GC);
					length = nlVGCList.getLength();
					if(log.isDebugEnabled()){
						log.info("Total number of Online Gift Cards are : "+totalNumberOfRecords);
					}
					if(length > 0){
						Element eleVGC = (Element) nlVGCList.item(0);
						String giftCardNo = eleVGC.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO);
						String pinNo = eleVGC.getAttribute(VSIConstants.ATTR_PIN_NO);
						String giftCrdKey = eleVGC.getAttribute(VSIConstants.ATTR_GIFT_CARD_KEY);
						if(giftCardNo != null && pinNo != null){
							eleVGCDetailsDoc.setAttribute(VSIConstants.ATTR_GIFT_CARD_NO, giftCardNo);
							eleVGCDetailsDoc.setAttribute(VSIConstants.ATTR_PIN_NO, pinNo);
							eleVGCDetailsDoc.setAttribute(VSIConstants.ATTR_GIFT_CARD_KEY, giftCrdKey);
						}
					}
				}
			}
			Element eleInXML = inXML.getDocumentElement();
			String strOrderNo = eleInXML.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String strGiftCardAmount = eleInXML.getAttribute(VSIConstants.ATTR_OFFER_AMOUNT);
			updateVGCAsCardUsed(env, strOrderNo, docVGCDetailsDoc);

			invokeSVSForOGCActivation(env, docVGCDetailsDoc, strOrderNo, strGiftCardAmount, strOrderHeaderKey);
			
			if(log.isDebugEnabled()){
				log.info("Exiting fetchVGCDetails : docVGCDetailsDoc : \n"+XMLUtil.getXMLString(docVGCDetailsDoc));
			}
		}
		catch (YFSException e)
		{
			throw e;
		}
		catch (Exception e) 
		{

			throw e;	
		}

		return docVGCDetailsDoc;
	}
	//OMS-1742 start
	private void checkThreshold(YFSEnvironment env, int totalNumberOfRecords) {
		if(log.isDebugEnabled()){
		log.info("Inside checkThreshold : ");
		}
		Document docCommonCodeListInput = null;
		Document docCommonCodeListOutput = null;
		int threshold = 0;
		try {
			docCommonCodeListInput = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCodeListInput = docCommonCodeListInput
					.getDocumentElement();
			eleCommonCodeListInput.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.ATTR_GCTHRESHOLD);
			if(log.isDebugEnabled()){
			log.info("Inside checkThreshold input to getCommonCode List api : "+XMLUtil.getXMLString(docCommonCodeListInput));
			}
			docCommonCodeListOutput = VSIUtils.invokeAPI(env,
					VSIConstants.API_COMMON_CODE_LIST, docCommonCodeListInput);
			
			Element commonCode = (Element) docCommonCodeListOutput
					.getDocumentElement().getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE)
					.item(0);
			threshold = Integer.parseInt(commonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE));
			if(log.isDebugEnabled()){
			log.info("Total number of unused GiftCards are "+totalNumberOfRecords+". The threshold is "+threshold+".");
			}
			if (totalNumberOfRecords < threshold) {
				raiseAlert(env);
			}
		} catch (ParserConfigurationException | YFSException |RemoteException |YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	private void raiseAlert(YFSEnvironment env) throws YFSException, RemoteException,
	YIFClientCreationException, ParserConfigurationException {
		String strReason = "The number of Gift Cards are below the threshold value.";
		Document docCreateExInput = XMLUtil.createDocument(VSIConstants.ELE_INBOX);
		Element eleInbox = docCreateExInput.getDocumentElement();

		eleInbox.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, VSIConstants.FLAG_Y);
		eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, strReason);
		eleInbox.setAttribute(VSIConstants.ATTR_ERROR_REASON, strReason);
		eleInbox.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				VSIConstants.ATTR_VSI_ALERT_GIFT_CARD);
		eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				VSIConstants.ATTR_VSI_ALERT_GIFT_CARD);
		eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE,
				VSIConstants.FLAG_Y);

		Element eleConsolidationTemplate = docCreateExInput
				.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
		eleInbox.appendChild(eleConsolidationTemplate);
		Element eleInboxElement = docCreateExInput
				.createElement(VSIConstants.ELE_INBOX);
		eleInboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, VSIConstants.FLAG_Y);
		eleInboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				VSIConstants.ATTR_VSI_ALERT_GIFT_CARD);
		eleInboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				VSIConstants.ATTR_VSI_ALERT_GIFT_CARD);

		eleConsolidationTemplate.appendChild(eleInboxElement);


		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				docCreateExInput);

	}	


	//OMS-1742 END
	public void invokeSVSForOGCActivation(YFSEnvironment env, Document docVGCDetails, String strOrderNo, String strGiftCardAmount, String strOrderHeaderKey) throws YFSException, Exception {
		try {
			Date todaysDate = new Date(); 

			Element eleDocVGCDetails = docVGCDetails.getDocumentElement();
			DateFormat formatter = new SimpleDateFormat(VSIConstants.YYYY_MM_DD_T_HH_MM_SS); 
			String dateToday = formatter.format(todaysDate);
			String sStoreNo = eleDocVGCDetails.getAttribute(VSIConstants.ATTR_STORE_NO);
			String callingFlow = eleDocVGCDetails.getAttribute(VSIConstants.ATTR_CALLING_FLOW);			
			sStoreNo = getShipNode(env, strOrderHeaderKey);
			Document docVGCActivationDoc = XMLUtil.createDocument("VSIOnlineGiftCard");
			Element rootEle = docVGCActivationDoc.getDocumentElement();
			String trackData1 = eleDocVGCDetails.getAttribute("TrackData1");
			if(!YFCCommon.isVoid(trackData1))
			{
				rootEle.setAttribute("TrackData1", trackData1);
			}
			String trackData2 = eleDocVGCDetails.getAttribute("TrackData2");
			if(!YFCCommon.isVoid(trackData2))
			{
				rootEle.setAttribute("TrackData2", trackData2);
			}
			rootEle.setAttribute(VSIConstants.ATTR_STORE_NO, sStoreNo);
			rootEle.setAttribute(VSIConstants.ATTR_GIFT_CARD_NO, eleDocVGCDetails.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO));
			rootEle.setAttribute(VSIConstants.ATTR_DATE_TODAY, dateToday);
			rootEle.setAttribute(VSIConstants.ATTR_PIN_NO, eleDocVGCDetails.getAttribute(VSIConstants.ATTR_PIN_NO));
			rootEle.setAttribute(VSIConstants.ATTR_INVOICE_NO, strOrderNo);
			double amount = Double.parseDouble(strGiftCardAmount);
			if(amount < 0) amount = amount * (-1);
			String refAmt = "0.0";
			if(amount > 0)
				refAmt = 	Double.toString(amount);
			
			if(log.isDebugEnabled()){
				log.info("refAmt = "+refAmt);
			}
			rootEle.setAttribute(VSIConstants.ATTR_REFUND_AMOUNT, refAmt);
			rootEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			rootEle.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
			if(YFCCommon.isVoid(callingFlow))
			{
				rootEle.setAttribute(VSIConstants.ATTR_CALLING_FLOW,VSIConstants.STR_CUST_APPEASE);
			}
			else
			{
				rootEle.setAttribute(VSIConstants.ATTR_CALLING_FLOW, callingFlow);
			}
			String sCalcTranID = generateTransactionIDForVGC(env);
			String sTranID = "936806"+sCalcTranID; 
			rootEle.setAttribute("TransactionID", sTranID);
			if(log.isDebugEnabled()){
				log.info("TransactionID = "+sTranID);
				log.info("Input to service VSISendVGCActivationReqToSVS : \n"+XMLUtil.getXMLString(docVGCActivationDoc));
			}
			
			VSIProcessSVSResponseForOGCActivation svsActivation = new VSIProcessSVSResponseForOGCActivation();
			svsActivation.vsiInvokeAndProcessSVSResponse(env, docVGCActivationDoc);

		} 
		catch (YFSException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw e;
		}

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
		 Document docTemplateForGetOrderDetails = this
	              .createTemplateForGetOrderDetails();
		Document outDoc = VSIUtils.invokeAPI(env, docTemplateForGetOrderDetails, VSIConstants.API_GET_ORDER_DETAILS, getOrderListDoc);
		//Element orderLineElem = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
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
	private void updateVGCAsCardUsed(YFSEnvironment env, String strOrderNo, Document docVGCDetailsDoc) throws Exception {
		// this method will mark the Gift card as activated and will update the corresponding order No
		if(log.isDebugEnabled()){
			log.info("Inside updateVGCAsActivated : Input :\n"+XMLUtil.getXMLString(docVGCDetailsDoc));
		}
		
		Element rootEle = docVGCDetailsDoc.getDocumentElement();
		rootEle.setAttribute(VSIConstants.ATTR_IS_CARD_USED,VSIConstants.FLAG_Y);
		// Set the IsActivated flag to Y and the Card Amount to the amount to be refunded.
		//rootEle.setAttribute("IsActivated", "Y");
		//rootEle.setAttribute("CardAmount", String.valueOf(refundAmount));

		rootEle.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
		if(log.isDebugEnabled()){
			log.info("Inside updateVGCAsCardUsed : Input XML to VSIUpdateOnlineGCDetails : \n"+XMLUtil.getXMLString(docVGCDetailsDoc));
		}
		VSIUtils.invokeService(env,VSIConstants.SERVICE_VSI_UPDATE_ONLINE_GCDETAILS, docVGCDetailsDoc);
	}
	
	private Document createTemplateForGetOrderDetails() throws TransformerException {

	    // Create template Document for getOrderDetails .
	    Document docTemplateForGetOrderDetails = SCXmlUtil
	        .createDocument(VSIConstants.ELE_ORDER);
	    docTemplateForGetOrderDetails.getDocumentElement()
	        .setAttribute(VSIConstants.ATTR_ENTERED_BY, "");
	    
	    Element eleOrderLines = docTemplateForGetOrderDetails.createElement("OrderLines");
	    Element eleOrderline = docTemplateForGetOrderDetails.createElement("OrderLine");
	    eleOrderline.setAttribute("DeliveryMethod", "");
	    eleOrderline.setAttribute("ShipNode", "");
	    docTemplateForGetOrderDetails.getDocumentElement().appendChild(eleOrderLines);
	    eleOrderLines.appendChild(eleOrderline);
	    return docTemplateForGetOrderDetails;

	  }
	
}
