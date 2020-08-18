package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;



public class VSIReverseAuthorization implements VSIConstants {


//public  void reverseAuthorization(Document changeOrderXMLIP)throws Exception {
		public void reverseAuthorization(YFSEnvironment env,Document changeOrderXMLIP)throws Exception {	
			
			Document outdocgetOrderList=null;

		//get Order header key to create input for getorderlist api
			Element rootElementChangeOrderXML = changeOrderXMLIP.getDocumentElement();
			String attOrderHeaderKey=rootElementChangeOrderXML.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String attEnterpriseCode=rootElementChangeOrderXML.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String attPaymentRuleID =rootElementChangeOrderXML.getAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID);
	
		//if enterprise code is VSI.com and 1A orders only. 
			if ("DEFAULT".equals(attPaymentRuleID) && !YFCObject.isVoid(attEnterpriseCode) 
					&& (VSICOM_ENTERPRISE_CODE.equalsIgnoreCase(attEnterpriseCode) || attEnterpriseCode.equalsIgnoreCase(ENT_ADP))){

				//creating get order list input
				Document indocGetOrderList = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleGetorderlistOrder=indocGetOrderList.getDocumentElement();
				eleGetorderlistOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, attOrderHeaderKey);
				

				//get orderList to get PaymentType Payment element
				
				 outdocgetOrderList = VSIUtils.invokeAPI(env,"global/template/api/VSIGetPaymentMethodsList.xml", VSIConstants.API_GET_ORDER_LIST, indocGetOrderList); 
				Element eleOrderListorderlist=outdocgetOrderList.getDocumentElement();
				String elePaymentMethodPaymentType=SCXmlUtil.getXpathAttribute(eleOrderListorderlist, "Order/PaymentMethods/PaymentMethod/@PaymentType");
				
				if (PAYMENT_MODE_PP.equalsIgnoreCase(elePaymentMethodPaymentType) && !YFCObject.isVoid(elePaymentMethodPaymentType)){
				Element elePayMethPaypal= SCXmlUtil.getXpathElement(eleOrderListorderlist, "Order/PaymentMethods/PaymentMethod[@PaymentType='PAYPAL']");
				String attPaymentType=elePayMethPaypal.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE); 
				

				//getting TotalAuthorized and TotalCharged attributes
				double attTotalAuthorized=SCXmlUtil.getDoubleAttribute(elePayMethPaypal,VSIConstants.ATTR_TOTAL_AUTHORIZED);
				double attTotalCharged=SCXmlUtil.getDoubleAttribute(elePayMethPaypal,VSIConstants.ATTR_TOTAL_CHARGED);
			
				//Creating ManageChargeTransactionRequest input		
				Document manageChargeTransactionRequestapiIP = XMLUtil.createDocument("ChargeTransactionRequestList");
				Element eleChargeTransactionRequestList=manageChargeTransactionRequestapiIP.getDocumentElement();
				Element eleChargeTransactionRequest = SCXmlUtil.createChild(eleChargeTransactionRequestList,"ChargeTransactionRequest"); 
				eleChargeTransactionRequestList.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,attOrderHeaderKey);
				String ctrSeqId = VSIDBUtil.getNextSequence(env,VSIConstants.SEQ_CC_CTR);
				String ChargeTransactionRequestId = ctrSeqId;
				eleChargeTransactionRequest.setAttribute(VSIConstants.ATTR_CHARGE_TRAN_REQ_ID,
						ChargeTransactionRequestId);
				
				//get EnterpriseCode, MaxOrderStatus, and MinOrderStatus
				double attMaxOrderStatus=SCXmlUtil.getDoubleAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_MAX_ORDER_STATUS);
				double attMinOrderStatus=SCXmlUtil.getDoubleAttribute(rootElementChangeOrderXML,VSIConstants.ATTR_MIN_ORDER_STATUS);

		//check to see if order is cancelled and paymenttype is Paypal
			if(!YFCObject.isVoid(attMinOrderStatus) && !YFCObject.isVoid(attMaxOrderStatus) && !YFCObject.isVoid(attPaymentType)){
			if ((attMinOrderStatus==9000 && attMaxOrderStatus==9000)||(attMinOrderStatus>=3700 && attMaxOrderStatus==9000) 
						&& PAYMENT_MODE_PP.equalsIgnoreCase(attPaymentType)){
				
				//max request amount = totalauthorized-total charge
				double diffAuthCharge=attTotalAuthorized-attTotalCharged;
				String newMaxRequestAmount ="-"+ String.valueOf(diffAuthCharge);
			eleChargeTransactionRequest.setAttribute(VSIConstants.ATTR_MAX_REQ_AMT,newMaxRequestAmount);
			
			try{
				VSIUtils.invokeAPI(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,manageChargeTransactionRequestapiIP); 
			}
			catch(Exception Ex){}
				}
			}
			else{}
			} else if (!YFCObject.isVoid(elePaymentMethodPaymentType)
					&& (PAYMENT_MODE_CASH.equals(elePaymentMethodPaymentType)
							|| PAYMENT_MODE_CHECK.equals(elePaymentMethodPaymentType))) {
				//create alert for international order
				Document docCreateExceptionInput = SCXmlUtil.createDocument(VSIConstants.ELE_INBOX);
				Element eleInbox = docCreateExceptionInput.getDocumentElement();
				eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO));
				eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, VSI_REFUNDS);
				eleInbox.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
				eleInbox.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
						VSI_REFUND);
				eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, attOrderHeaderKey);
				eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE, VSIConstants.FLAG_Y);
				eleInbox.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG,VSIConstants.FLAG_Y);
				eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, VSI_REFUND);
				eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.STRING_MANUAL_RETURN_ALERT_MSG);
				
				eleInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.STRING_MANUAL_RETURN_ALERT_MSG);
				
				Element ConsoltempEle = docCreateExceptionInput
						.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
				eleInbox.appendChild(ConsoltempEle);
				Element InboxCpyEle = docCreateExceptionInput.createElement(VSIConstants.ELE_INBOX);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, attOrderHeaderKey);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO));
				InboxCpyEle.setAttribute(VSIConstants.ATTR_CONSOLIDATE, "Y");
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
				InboxCpyEle.setAttribute(VSIConstants.ATTR_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.STRING_MANUAL_RETURN_ALERT_MSG);
				
				InboxCpyEle.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(rootElementChangeOrderXML, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.STRING_MANUAL_RETURN_ALERT_MSG);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
						VSI_REFUND);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
						VSI_REFUND);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
				InboxCpyEle.setAttribute(VSIConstants.ATTR_QUEUE_ID,
						VSI_REFUNDS);
				ConsoltempEle.appendChild(InboxCpyEle);
				
				Element eleOrder = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, attEnterpriseCode);
				eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, attOrderHeaderKey );
				
				VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, docCreateExceptionInput); 
			}
		}
	}
}