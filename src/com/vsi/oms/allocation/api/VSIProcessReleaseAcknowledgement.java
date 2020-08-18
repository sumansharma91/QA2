package com.vsi.oms.allocation.api;

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

/**
 * This component is responsible for receiving release acknowledgement from WMS via middle ware
 * 
 * Input:
 * <OrderStatusChange BaseDropStatus=""
 * 		EnterpriseCode=""  OrderNo=""  TransactionId="" DocumentType=""
 * 		ModificationReasonCode="" ModificationReasonText="" OrderNoType="">
 * 		<OrderLines>
 * 			<OrderLine ReleaseNo=""/>
 * 		</OrderLines>
 * </OrderStatusChange>
 * 
 * 
 * @author IBM
 *
 */
public class VSIProcessReleaseAcknowledgement extends VSIBaseCustomAPI implements VSIConstants {
	
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessReleaseAcknowledgement.class);

	public Document processReleaseAcknowledgement(YFSEnvironment env, Document docInput) {
		
		log.beginTimer("VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement : START");
		
		try {
			//Check if OrderLines/OrderLine tag is present- then delete it
			Element eleOrderLine = null;
			Element eleChangeOrderStatus = docInput.getDocumentElement();
			Element eleOrderLines = XMLUtil.getElementByXPath(docInput, XPATH_ORDERSTATUS_ORDERLINES);
			String strBaseDropStatus = null;
			if(!YFCCommon.isVoid(eleOrderLines)){
				eleOrderLine = XMLUtil.getFirstElementByName(eleOrderLines, ELE_ORDER_LINE);
			}
			String strLineReleaseNo = eleChangeOrderStatus.getAttribute(ATTR_RELEASE_NO);
			String strDocReleaseNo = null;
			if(!YFCCommon.isVoid(eleOrderLine)){
				strLineReleaseNo = eleOrderLine.getAttribute(ATTR_RELEASE_NO);
			}
			
			
			if(!YFCCommon.isVoid(eleChangeOrderStatus)){
				strDocReleaseNo = eleChangeOrderStatus.getAttribute(ATTR_RELEASE_NO);
					
				//call getOrderLineDetails for Transfer order as Customer PO No is returned from WMS.
				Document docGetOrderReleaseList = SCXmlUtil.createDocument(ELE_ORDER_RELEASE);
				Element eleGetOrderReleaseList = docGetOrderReleaseList.getDocumentElement();
				if(eleChangeOrderStatus.getAttribute(ATTR_DOCUMENT_TYPE).equals(DOC_TYPE_TO)){
				eleGetOrderReleaseList.setAttribute("CustomerPoNo", eleChangeOrderStatus.getAttribute(ATTR_ORDER_NO));
				eleChangeOrderStatus.setAttribute(ATTR_TRANSACTION_ID, "RELEASE_ACKNOWLEDGEMENT.0006.ex");
				}else{
					eleGetOrderReleaseList.setAttribute("SalesOrderNo", eleChangeOrderStatus.getAttribute(ATTR_ORDER_NO));
					eleChangeOrderStatus.setAttribute(ATTR_TRANSACTION_ID, "RELEASE_ACKNOWLEDGEMENT.0001.ex");
				}
				eleGetOrderReleaseList.setAttribute(ATTR_RELEASE_NO, strLineReleaseNo);
				
				
				Document docReleaseListTemplate = SCXmlUtil.createDocument(ELE_ORDER_RELEASE_LIST);
				Element eleReleaseListTemplate = docReleaseListTemplate.getDocumentElement();
				Element eleReleaseTemplate = SCXmlUtil.createChild(eleReleaseListTemplate, ELE_ORDER_RELEASE);
				eleReleaseTemplate.setAttribute(ATTR_RELEASE_NO, ATTR_EMPTY);
				eleReleaseTemplate.setAttribute(ATTR_ORDER_RELEASE_KEY, ATTR_EMPTY);
				Element eleOrderTemplate = SCXmlUtil.createChild(eleReleaseTemplate, ELE_ORDER);
				eleOrderTemplate.setAttribute(ATTR_ORDER_HEADER_KEY, ATTR_EMPTY);
				eleOrderTemplate.setAttribute(ATTR_ORDER_NO, ATTR_EMPTY);
				
				
				docGetOrderReleaseList = VSIUtils.invokeAPI(env, docReleaseListTemplate, API_GET_ORDER_RELEASE_LIST, docGetOrderReleaseList);
				eleGetOrderReleaseList = docGetOrderReleaseList.getDocumentElement();
				Element eleGetOrderRelease = SCXmlUtil.getChildElement(eleGetOrderReleaseList, ELE_ORDER_RELEASE);
				String strOrderReleaseKey = eleGetOrderRelease.getAttribute(ATTR_ORDER_RELEASE_KEY);
				Element eleOrderReleaseOrder = SCXmlUtil.getChildElement(eleGetOrderRelease, ELE_ORDER);
				String strTOOrderNo = eleOrderReleaseOrder.getAttribute(ATTR_ORDER_NO);
				String strTOOHKey = eleOrderReleaseOrder.getAttribute(ATTR_ORDER_HEADER_KEY);
				
				
				
				eleChangeOrderStatus.setAttribute(ATTR_ORDER_NO, strTOOrderNo);
				eleChangeOrderStatus.setAttribute(ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);
				eleChangeOrderStatus.setAttribute(ATTR_ORDER_HEADER_KEY, strTOOHKey);
				eleChangeOrderStatus.setAttribute(ATTR_ORDER_RELEASE_KEY, strOrderReleaseKey);
				strBaseDropStatus = eleChangeOrderStatus.getAttribute(ATTR_BASE_DROP_STATUS);
				if(!YFCCommon.isVoid(strBaseDropStatus)&&strBaseDropStatus.equalsIgnoreCase("3200.1000")){
					strBaseDropStatus = STATUS_CODE_RELEASE_ACKNOWLEDGED;
					eleChangeOrderStatus.setAttribute(ATTR_BASE_DROP_STATUS,strBaseDropStatus);
				}
				if(!YFCCommon.isVoid(strBaseDropStatus)&&strBaseDropStatus.equalsIgnoreCase("3200.2000")){
					strBaseDropStatus = STATUS_CODE_RELEASE_REJECTED;
					eleChangeOrderStatus.setAttribute(ATTR_BASE_DROP_STATUS, strBaseDropStatus);
				}
			}
			if(YFCCommon.isVoid(strDocReleaseNo) && strLineReleaseNo!=null
					&& YFCCommon.isVoid(eleChangeOrderStatus.getAttribute(ATTR_RELEASE_NO))){
				eleChangeOrderStatus.setAttribute(ATTR_RELEASE_NO, strLineReleaseNo);
			}
			//Remove order lines node as it is not required.
			
			if(!YFCCommon.isVoid(eleOrderLines)){
				XMLUtil.removeChild(eleChangeOrderStatus, eleOrderLines);
			}
			
			
			// If release is acknowledged, then changeOrderStatus should be called to change the status
			//	of release to Release Acknowledged
			if (strBaseDropStatus.equals(STATUS_CODE_RELEASE_ACKNOWLEDGED)) {
					VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_STATUS_CHANGE_ORDER_RELEASE_KEY,
							VSIConstants.API_CHANGE_ORDER_STATUS, docInput);
			} else {
				// If release is rejected, the changeOrderStatus should be called to change the status of release
				//	to Release Rejected and an alert should be raised to IT Support team
				docInput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_ORDER_STATUS_CHANGE_ORDER_RELEASE_KEY,
						VSIConstants.API_CHANGE_ORDER_STATUS, docInput);
				eleChangeOrderStatus = docInput.getDocumentElement();
				String sOHK = eleChangeOrderStatus.getAttribute(ATTR_ORDER_HEADER_KEY);
				String sOrderNo = eleChangeOrderStatus.getAttribute(ATTR_ORDER_NO);
				raiseReleaseRejectedAlert(env,sOHK,sOrderNo);
				
				//VSIUtils.invokeService(env, VSIConstants.SERVICE_RAISE_RELEASE_REJECTED_ALERT, docInput);
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement() : ");
		}
		
		log.endTimer("VSIProcessReleaseAcknowledgement.processReleaseAcknowledgement : END");
		return docInput;
	}
	public void raiseReleaseRejectedAlert(YFSEnvironment env, String sOHK, String sOrderNo)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "VSI Release Rejected Alert");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"VSI_RELEASE_REJECTED_ALERT");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_RELEASE_REJECTED_QUEUE");

		VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION, createExInput);
	}
}