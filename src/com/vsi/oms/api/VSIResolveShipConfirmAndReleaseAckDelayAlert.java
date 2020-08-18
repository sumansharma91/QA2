package com.vsi.oms.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/*
 * This class is used for closing release ack delay and ship confirm alerts
 */
public class VSIResolveShipConfirmAndReleaseAckDelayAlert extends
		VSIBaseCustomAPI implements VSIConstants {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIResolveShipConfirmAndReleaseAckDelayAlert.class);

	public void resolveAlert(YFSEnvironment env,
			Document inputDoc) throws Exception {
		log.beginTimer("VSIResolveShipConfirmAndReleaseAckDelayAlert.resolveAlert : START");
		if(log.isDebugEnabled()){
		log.debug("Printing inputdoc \n"+XMLUtil.getXMLString(inputDoc));
		}
		try {
		if ( !YFCCommon.isVoid(inputDoc)) {
			Element eleinputDoc = inputDoc.getDocumentElement();
			String strRootTagName = eleinputDoc.getTagName();
			if (("OrderStatusChange").equalsIgnoreCase(strRootTagName)) {
				String strReleaseNo = eleinputDoc.getAttribute(ATTR_RELEASE_NO);
				String strOrderNo = eleinputDoc.getAttribute(ATTR_ORDER_NO);
				String strDocumentType = eleinputDoc
						.getAttribute(ATTR_DOCUMENT_TYPE);
				if(!YFCCommon.isVoid(strReleaseNo)&&!YFCCommon.isVoid(strOrderNo)&&!YFCCommon.isVoid(strDocumentType)&&("0001").equalsIgnoreCase(strDocumentType)){
				
					
					Document docOrderReleaseListDoc = getOrderReleaseList(env,
							strReleaseNo, strOrderNo);

					if (!YFCCommon.isVoid(docOrderReleaseListDoc) && ELE_ORDER_RELEASE_LIST.equals(docOrderReleaseListDoc.getDocumentElement().getTagName())) {

						Document resolveExInputReleaseAckDelay = getResolveAlert(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY",strOrderNo);
								//oms-1901 start
						Document resolveExInputReleaseAckDelayGE = getResolveAlert(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY_GOOGLE_EXPRESS",strOrderNo);
						//oms-1901 end

						Document docInboxListTemplate = SCXmlUtil
								.createDocument("InboxList");
						Element eleInboxListTemplate = docInboxListTemplate
								.getDocumentElement();
						Element eleInboxTemplate = SCXmlUtil.createChild(
								eleInboxListTemplate, "Inbox");
						eleInboxTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
						eleInboxTemplate.setAttribute("InboxKey", ATTR_EMPTY);

						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelay);
						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelayGE);
					}
				}
				else if(!YFCCommon.isVoid(strReleaseNo)&&!YFCCommon.isVoid(strOrderNo)&&!YFCCommon.isVoid(strDocumentType)&&("0006").equalsIgnoreCase(strDocumentType)){
					Document docOrderReleaseListDoc = getOrderReleaseListforTO(env,
							strReleaseNo, strOrderNo);

					if (!YFCCommon.isVoid(docOrderReleaseListDoc) && ELE_ORDER_RELEASE_LIST.equals(docOrderReleaseListDoc.getDocumentElement().getTagName())) {

						Document resolveExInputReleaseAckDelay = getResolveAlertforTO(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY");
						

						Document docInboxListTemplate = SCXmlUtil
								.createDocument("InboxList");
						Element eleInboxListTemplate = docInboxListTemplate
								.getDocumentElement();
						Element eleInboxTemplate = SCXmlUtil.createChild(
								eleInboxListTemplate, "Inbox");
						eleInboxTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
						eleInboxTemplate.setAttribute("InboxKey", ATTR_EMPTY);

						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelay);
					}
				}
			
			} else if (("ShipmentList").equalsIgnoreCase(strRootTagName)) {
				
				Element eleOrder = SCXmlUtil.getChildElement(eleinputDoc,
						ELE_ORDER);
			
				if (!YFCCommon.isVoid(eleOrder)&&("0001".equals(eleOrder.getAttribute(ATTR_DOCUMENT_TYPE))||"0006".equals(eleOrder.getAttribute(ATTR_DOCUMENT_TYPE)))) {
					String strDOId = eleOrder
							.getAttribute(ATTR_DISTRIBUTION_ORDER_ID);
					String strOrderNo1 = eleOrder.getAttribute(ATTR_ORDER_NO);
					if (!YFCCommon.isVoid(strDOId)&&!YFCCommon.isVoid(strOrderNo1)) {
						String[] arrValues = strDOId.split("\\*");
						String strReleaseNo1 = arrValues[1];
						Document docOrderReleaseListOuput = getOrderReleaseList(
								env, strReleaseNo1, strOrderNo1);
						if (!YFCCommon.isVoid(docOrderReleaseListOuput) && ELE_ORDER_RELEASE_LIST.equals(docOrderReleaseListOuput.getDocumentElement().getTagName())) {

							Document docResolveExInputReleaseAckDelay = getResolveAlert(
									docOrderReleaseListOuput, "Release Ack Delays",
									"VSI_RELEASE_ACK_DELAY", strOrderNo1);
									//oms-1901 start
							Document docResolveExInputReleaseAckDelayGE = getResolveAlert(
									docOrderReleaseListOuput, "Release Ack Delays",
									"VSI_RELEASE_ACK_DELAY_GOOGLE_EXPRESS", strOrderNo1);
									
							Document docResolveExInputShipConfDelay = getResolveAlert(docOrderReleaseListOuput,"Ship Confirmation Delays","VSI_SHIP_CONFIRM_DELAY",strOrderNo1);
							Document docResolveExInputShipConfDelayGE = getResolveAlert(docOrderReleaseListOuput,"Ship Confirmation Delays","VSI_SHIP_CONFIRM_DELAY_GOOGLE_EXPRESS",strOrderNo1);
                              //oms-1901 end
							
							Document docInboxListTemplate = SCXmlUtil
									.createDocument("InboxList");
							Element eleInboxListTemplate = docInboxListTemplate
									.getDocumentElement();
							Element eleInboxTemplate = SCXmlUtil.createChild(
									eleInboxListTemplate, "Inbox");
							eleInboxTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
							eleInboxTemplate.setAttribute("InboxKey", ATTR_EMPTY);
							
							VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
									docResolveExInputReleaseAckDelay);
							VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
									docResolveExInputShipConfDelay);
							VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
									docResolveExInputReleaseAckDelayGE);
							VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
									docResolveExInputShipConfDelayGE);
						}

					}

				}
			}
			//OMS-2006
			else if ((ELE_ORDER_RELEASE).equalsIgnoreCase(strRootTagName)) {
				String strReleaseNo = eleinputDoc.getAttribute(ATTR_RELEASE_NO);
				String strOrderNo = eleinputDoc.getAttribute(ATTR_ORDER_NO);
				String strDocumentType = eleinputDoc
						.getAttribute(ATTR_DOCUMENT_TYPE);
               if(!YFCCommon.isVoid(strReleaseNo)&&!YFCCommon.isVoid(strOrderNo)&&!YFCCommon.isVoid(strDocumentType)&&
            		   (ATTR_DOCUMENT_TYPE_SALES).equalsIgnoreCase(strDocumentType)){
				
					
					Document docOrderReleaseListDoc = getOrderReleaseList(env,
							strReleaseNo, strOrderNo);

					if (!YFCCommon.isVoid(docOrderReleaseListDoc) && ELE_ORDER_RELEASE_LIST.equals(docOrderReleaseListDoc.getDocumentElement().getTagName())) {

						Document resolveExInputReleaseAckDelay = getResolveAlert(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY",strOrderNo);
						Document resolveExInputReleaseAckDelayGE = getResolveAlert(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY_GOOGLE_EXPRESS",strOrderNo);
						Document docResolveExInputShipConfDelay = getResolveAlert(docOrderReleaseListDoc,"Ship Confirmation Delays","VSI_SHIP_CONFIRM_DELAY",strOrderNo);
						Document docResolveExInputShipConfDelayGE = getResolveAlert(docOrderReleaseListDoc,"Ship Confirmation Delays","VSI_SHIP_CONFIRM_DELAY_GOOGLE_EXPRESS",strOrderNo);

						

						Document docInboxListTemplate = SCXmlUtil
								.createDocument("InboxList");
						Element eleInboxListTemplate = docInboxListTemplate
								.getDocumentElement();
						Element eleInboxTemplate = SCXmlUtil.createChild(
								eleInboxListTemplate, ELE_INBOX);
						eleInboxTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
						eleInboxTemplate.setAttribute("InboxKey", ATTR_EMPTY);

						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelay);
						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelayGE);
						VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
								docResolveExInputShipConfDelay);
						VSIUtils.invokeAPI(env,docInboxListTemplate, "resolveException",
								docResolveExInputShipConfDelayGE);
					}
				}
               else if(!YFCCommon.isVoid(strReleaseNo)&&!YFCCommon.isVoid(strOrderNo)&&!YFCCommon.isVoid(strDocumentType)&&DOC_TYPE_TO.equalsIgnoreCase(strDocumentType)){
					Document docOrderReleaseListDoc = getOrderReleaseListforTO(env,
							strReleaseNo, strOrderNo);

					if (!YFCCommon.isVoid(docOrderReleaseListDoc) && ELE_ORDER_RELEASE_LIST.equals(docOrderReleaseListDoc.getDocumentElement().getTagName())) {

						Document resolveExInputReleaseAckDelay = getResolveAlertforTO(
								docOrderReleaseListDoc, 
								"Release Ack Delays", "VSI_RELEASE_ACK_DELAY");
						Document resolveExInputShipConfirmDelay = getResolveAlertforTO(
								docOrderReleaseListDoc, 
								"Ship Confirmation Delays","VSI_SHIP_CONFIRM_DELAY");
						
						

						Document docInboxListTemplate = SCXmlUtil
								.createDocument("InboxList");
						Element eleInboxListTemplate = docInboxListTemplate
								.getDocumentElement();
						Element eleInboxTemplate = SCXmlUtil.createChild(
								eleInboxListTemplate, ELE_INBOX);
						eleInboxTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
						eleInboxTemplate.setAttribute("InboxKey", ATTR_EMPTY);

						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputReleaseAckDelay);
						VSIUtils.invokeAPI(env, docInboxListTemplate,"resolveException",
								resolveExInputShipConfirmDelay);
					}
				}
				
			}
		}//OMS-2006 END
		log.endTimer("VSIResolveShipConfirmAndReleaseAckDelayAlert.resolveAlert : End");
		}

		catch (YFSException | ParserConfigurationException e) {
			e.printStackTrace();

		} 
	}
	/*
	 * this method is used for framing resolve exception input for TO
	 */
	private Document getResolveAlertforTO(Document orderReleaseListDoc,
			String alertDesc, String alertQueueId)
			throws ParserConfigurationException {
		if(log.isDebugEnabled()){
		log.debug("Printing getResolveAlert \n"+XMLUtil.getXMLString(orderReleaseListDoc));
		}
		Element eleRelease = orderReleaseListDoc.getDocumentElement();
		Element eleGetOrderRelease = SCXmlUtil.getChildElement(eleRelease,
				ELE_ORDER_RELEASE);
		String strOrderReleaseKey = eleGetOrderRelease
				.getAttribute(ATTR_ORDER_RELEASE_KEY);
	
		
		Element eleOrderReleaseOrder = SCXmlUtil.getChildElement(eleGetOrderRelease, ELE_ORDER);
		String strTOOrderNo = eleOrderReleaseOrder.getAttribute(ATTR_ORDER_NO);
		
		String strOHKey = eleOrderReleaseOrder
				.getAttribute(ATTR_ORDER_HEADER_KEY);
		
		Document docResolveExInput = XMLUtil.createDocument("ResolutionDetails");
		Element eleRoot = docResolveExInput.getDocumentElement();
		Element eleInbox = docResolveExInput.createElement("Inbox");
		eleRoot.appendChild(eleInbox);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, strTOOrderNo);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOHKey);
		eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, alertDesc);
		eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, alertQueueId);
		eleInbox.setAttribute(VSIConstants.ATTR_STATUS, "OPEN");
		Element eleInboxExtn = docResolveExInput.createElement("Extn");

		eleInboxExtn.setAttribute(ATTR_ORDER_RELEASE_KEY,
				strOrderReleaseKey);
		eleInbox.appendChild(eleInboxExtn);
		if(log.isDebugEnabled()){
		log.debug("Printing docResolveExInput \n"+XMLUtil.getXMLString(docResolveExInput));
		}
		return docResolveExInput;
	}
/*
	 * this method is used for getting order no for customer po no
	 */
private Document getOrderReleaseListforTO(YFSEnvironment env,
		String strReleaseNo, String strOrderNo) throws YFSException,
	RemoteException, YIFClientCreationException {

		Document docGetOrderReleaseList = SCXmlUtil
				.createDocument(ELE_ORDER_RELEASE);
		Element eleGetOrderReleaseList = docGetOrderReleaseList
				.getDocumentElement();
		eleGetOrderReleaseList.setAttribute(ATTR_SHIP_CUST_PO_NO, strOrderNo);
		eleGetOrderReleaseList.setAttribute(ATTR_RELEASE_NO, strReleaseNo);

		Document docReleaseListTemplate = SCXmlUtil.createDocument(ELE_ORDER_RELEASE_LIST);
		Element eleReleaseListTemplate = docReleaseListTemplate.getDocumentElement();
		Element eleReleaseTemplate = SCXmlUtil.createChild(eleReleaseListTemplate, ELE_ORDER_RELEASE);
		eleReleaseTemplate.setAttribute(ATTR_RELEASE_NO, ATTR_EMPTY);
		eleReleaseTemplate.setAttribute(ATTR_ORDER_RELEASE_KEY, ATTR_EMPTY);
		Element eleOrderTemplate = SCXmlUtil.createChild(eleReleaseTemplate, ELE_ORDER);
		eleOrderTemplate.setAttribute(ATTR_ORDER_HEADER_KEY, ATTR_EMPTY);
		eleOrderTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
		
		docGetOrderReleaseList = VSIUtils.invokeAPI(env,
				docReleaseListTemplate, API_GET_ORDER_RELEASE_LIST,
				docGetOrderReleaseList);
		if(log.isDebugEnabled()){
		log.debug("Printing docGetOrderReleaseList \n"+XMLUtil.getXMLString(docGetOrderReleaseList));
		}
		return docGetOrderReleaseList;
	}
	/*
 * This method is used for closing alerts
 */
	private Document getResolveAlert(Document orderReleaseListDoc,
			String alertDesc, String alertQueueId, String strOrderNo)
			throws ParserConfigurationException {
		if(log.isDebugEnabled()){
		log.debug("Printing getResolveAlert \n"+XMLUtil.getXMLString(orderReleaseListDoc));
		}
		Element eleRelease = orderReleaseListDoc.getDocumentElement();
		Element eleGetOrderRelease = SCXmlUtil.getChildElement(eleRelease,
				ELE_ORDER_RELEASE);
		String strOrderReleaseKey = eleGetOrderRelease
				.getAttribute(ATTR_ORDER_RELEASE_KEY);
	
		String strOHKey = eleGetOrderRelease
				.getAttribute(ATTR_ORDER_HEADER_KEY);
		Document docResolveExInput = XMLUtil.createDocument("ResolutionDetails");
		Element eleRoot = docResolveExInput.getDocumentElement();
		Element eleInbox = docResolveExInput.createElement("Inbox");
		eleRoot.appendChild(eleInbox);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
		eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOHKey);
		eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, alertDesc);
		eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, alertQueueId);
		eleInbox.setAttribute(VSIConstants.ATTR_STATUS, "OPEN");
		Element eleInboxExtn = docResolveExInput.createElement("Extn");

		eleInboxExtn.setAttribute(ATTR_ORDER_RELEASE_KEY,
				strOrderReleaseKey);
		eleInbox.appendChild(eleInboxExtn);
		if(log.isDebugEnabled()){
		log.debug("Printing docResolveExInput \n"+XMLUtil.getXMLString(docResolveExInput));
		}
		return docResolveExInput;
	}
/*
 * this method is used for getting the orderHeaderkey and orderReleaseKey
 */
	private Document getOrderReleaseList(YFSEnvironment env,
			String strReleaseNo, String strOrderNo) throws YFSException,
			RemoteException, YIFClientCreationException {
		
		Document docGetOrderReleaseList = SCXmlUtil
				.createDocument(ELE_ORDER_RELEASE);
		Element eleGetOrderReleaseList = docGetOrderReleaseList
				.getDocumentElement();
		eleGetOrderReleaseList.setAttribute("SalesOrderNo", strOrderNo);
		eleGetOrderReleaseList.setAttribute(ATTR_RELEASE_NO, strReleaseNo);

		Document docReleaseListTemplate = SCXmlUtil
				.createDocument(ELE_ORDER_RELEASE_LIST);
		Element eleReleaseListTemplate = docReleaseListTemplate
				.getDocumentElement();
		Element eleReleaseTemplate = SCXmlUtil.createChild(
				eleReleaseListTemplate, ELE_ORDER_RELEASE);
		eleReleaseTemplate.setAttribute(ATTR_ORDER_RELEASE_KEY, ATTR_EMPTY);
		eleReleaseTemplate.setAttribute(ATTR_ORDER_HEADER_KEY, ATTR_EMPTY);

		docGetOrderReleaseList = VSIUtils.invokeAPI(env,
				docReleaseListTemplate, API_GET_ORDER_RELEASE_LIST,
				docGetOrderReleaseList);
		if(log.isDebugEnabled()){
		log.debug("Printing docGetOrderReleaseList \n"+XMLUtil.getXMLString(docGetOrderReleaseList));
		}
		return docGetOrderReleaseList;
	}
}