package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIUpdateReturnPayment implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIUpdateReturnPayment.class);
	YIFApi api;
	public void vsiAwaitingPaymentStatus(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIAwaitingPaymentStatus================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

			//requestCollection creates a payment method with incomplete details.
			//This code will take the incomplete payment method and update the SVC No.
			Element rootElement = inXML.getDocumentElement();
			String strOrderNo = rootElement.getAttribute(ATTR_ORDER_NO);
			boolean bInvokeChangeOrder = false;
			
			Element elePaymentMethods = SCXmlUtil.getChildElement(rootElement, ELE_PAYMENT_METHODS);
			ArrayList<Element> elePaymentMethodList = SCXmlUtil.getChildren(elePaymentMethods, ELE_PAYMENT_METHOD);
			
			Document docChangeOrder = SCXmlUtil.createDocument(ELE_ORDER);
			Element eleChangeOrder = docChangeOrder.getDocumentElement();
			eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY, rootElement.getAttribute(ATTR_ORDER_HEADER_KEY));
			eleChangeOrder.setAttribute(ATTR_OVERRIDE,FLAG_Y);
			Element eleChangeOrderPaymentMethods = SCXmlUtil.createChild(eleChangeOrder, ELE_PAYMENT_METHODS);
			
			
			 for(Element elePaymentMethod : elePaymentMethodList){
				 String strIsIncompletePayment = elePaymentMethod.getAttribute("IncompletePaymentType");
				 String strPaymentType = elePaymentMethod.getAttribute(ATTR_PAYMENT_TYPE);
				 if(strPaymentType.equalsIgnoreCase(PAYMENT_MODE_OGC) &&
						 strIsIncompletePayment.equalsIgnoreCase(FLAG_Y)){
					 //Fetch OGC card details.
					 Document docVGCDetails = fetchVGCDetails(env,strOrderNo);
					 Element eleVGCDetail = docVGCDetails.getDocumentElement();
					 String strGiftCardNo = eleVGCDetail.getAttribute(ATTR_GIFT_CARD_NO);
					 String strGiftCardPinNo = eleVGCDetail.getAttribute(VSIConstants.ATTR_PIN_NO);
					 
					 //set payment details.
					 Element eleChangeOrderPaymentMethod = SCXmlUtil.createChild(eleChangeOrderPaymentMethods, ELE_PAYMENT_METHOD);
					 eleChangeOrderPaymentMethod.setAttribute(ATTR_PAYMENT_KEY, elePaymentMethod.getAttribute(ATTR_PAYMENT_KEY));
					 eleChangeOrderPaymentMethod.setAttribute(ATTR_SVC_NO, strGiftCardNo);
					 eleChangeOrderPaymentMethod.setAttribute(ATTR_SUSPEND_ANYMORE_CHARGES, FLAG_N);
					 eleChangeOrderPaymentMethod.setAttribute(ATTR_INCOMPLETE_PAYMENT, ATTR_EMPTY);
					 eleChangeOrderPaymentMethod.setAttribute(ATTR_PAYMENT_REFERENCE_2, strGiftCardPinNo);
					 bInvokeChangeOrder = true;
				 }
			 }
			if(bInvokeChangeOrder){
				//invoke change order.
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER,docChangeOrder);
				//Fix for ARE-394 Start
				api.invoke(env, "requestCollection", docChangeOrder);
				//Fix for ARE-394 End
			}
		}catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private Document fetchVGCDetails(YFSEnvironment env, String strOrderNo) throws YFSException,Exception {

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
			updateVGCAsCardUsed(env, strOrderNo, docVGCDetailsDoc);

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
	
}
