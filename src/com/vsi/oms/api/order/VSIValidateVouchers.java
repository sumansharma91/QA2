package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.api.VSIValidateCouponFromJDA;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author nish.pingle
 * This class will be invoked on_success of Order Create. The Voucher/CertNo is passed as PaymentRef1 and We mark the Voucher RedeemStatus
 * as "Y" and drop the message to ESB to be sent to Sales Audit. 
 * If the Voucher is already Redeemed then the Order is put on HOLD and an alert is raised on the console.
 */

public class VSIValidateVouchers {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIValidateVouchers.class);
	YIFApi api;
	@SuppressWarnings("unused")
	public void vsiValidateVouchers(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiValidateVouchers================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		try{

	
			
			Element orderElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);			
			Element orderLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
			
			if (null!=orderElement){
				
				String enteredBy = orderElement.getAttribute("EnteredBy");
				NodeList paymentList = inXML.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
				int paymentCount = paymentList.getLength();
				for(int i=0;i<paymentCount;i++){
					
					Element PaymentMethodElement = (Element)paymentList.item(i); 
					
					String sPaymentType = PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
					
					/*changes for SOS - 1  start  */
					DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					Date sysDate = new Date();
					/*changes for SOS - 1   end */
					
					if(sPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_VOUCHERS)){
						
						
						
						String sVoucherNo = PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_1);
						String sPaymentTotal = PaymentMethodElement.getAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT);//amount of voucher charged
						
						String strEntryType = orderElement.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
						String strOrderType = orderElement.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
						//Commented condition for the fix of OMS-1553
						if((VSIConstants.WEB.equals(strEntryType) && VSIConstants.WEB.equals(strOrderType)) 
//								&& (!YFCObject.isVoid(sVoucherNo) && !StringUtils.isNumeric(sVoucherNo))
								) {
							return;
						}
						
						boolean valid = false;
						boolean isLoylaty = false;
						//START: Fix for OMS-1553
//						if(!YFCObject.isVoid(sVoucherNo) && StringUtils.isNumeric(sVoucherNo)){
//							valid = validateVoucher(env,sVoucherNo,sPaymentTotal);
//						}else{
							valid = validateVoucherJDA(env,orderElement,sVoucherNo);
							isLoylaty = true;
//						}
						//END: Fix for OMS-1553
						
						if(valid){
							if(!isLoylaty){
								//String sRedeemDate = orderElement.getAttribute(VSIConstants.ATTR_ORDER_DATE);//redeem date of the voucher
								/*changes for SOS - 1  start  */
								String sRedeemDate = dateFormat.format(sysDate); //redeem date of the voucher setting it as sysdate
								/*changes for SOS - 1  end  */
								String sRedeemOrdNo = orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);// Issue OrderNo
								String sRedeemStore = orderLineElement.getAttribute(VSIConstants.ATTR_SHIP_NODE);// Redeem Store No
								//adding next line for JIRA OMS-106
								// 1b changes
								if(YFCCommon.isVoid(sRedeemStore)){
									sRedeemStore = enteredBy;
								}
								
								String sRedeemCustomerNo = orderElement.getAttribute("BillToID");// Redeem Customer No
								RedeemVoucher(env,sVoucherNo,sPaymentTotal,sRedeemDate,sRedeemOrdNo,sRedeemStore,sRedeemCustomerNo);
							}
						}else{
							String sOHK = orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
							String sOrderNo = orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
							raiseAlert(env,sOHK,sOrderNo);
						}
					}
				}
			} else{
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
						"Order Element Null");
				}		
		} catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					e.getErrorDescription());
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");

		}

	}
	
	
	
	/**
	 * 
	 * @param env
	 * @param sOHK
	 * @param sOrderNo
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 */
	
	private void raiseAlert(YFSEnvironment env, String sOHK, String sOrderNo) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();
		
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, sOrderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Voucher is Invalid");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "Invalid Voucher");
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "Voucher");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Voucher Invalid");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_VOUCHER_INVALID");
		
		
		Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
		
		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, sOHK);
		
		InboxReferencesListElement.appendChild(InboxReferencesElement);
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
		
		createHold(env,sOHK);
				
	}

/**
 * 
 * @param env
 * @param sOHK
 * @throws ParserConfigurationException
 * @throws YIFClientCreationException
 * @throws YFSException
 * @throws RemoteException
 */

	private void createHold(YFSEnvironment env, String sOHK) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Document createHoldInput = XMLUtil.createDocument("Order");
		Element orderElement = createHoldInput.getDocumentElement();
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
		
		Element OrderHoldTypesElement = createHoldInput.createElement("OrderHoldTypes");
		orderElement.appendChild(OrderHoldTypesElement);
		
		Element OrderHoldTypeElement = createHoldInput.createElement("OrderHoldType");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE, VSIConstants.HOLD_PAYMENT_FAILURE);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT, VSIConstants.REASON_PAYMENT_FAILED);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");
		
		OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
		

		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER, createHoldInput);
		
		
	}


/**
 * 
 * @param env
 * @param sVoucherNo
 * @param sPaymentTotal
 * @return
 * @throws YIFClientCreationException
 * @throws RemoteException
 * @throws ParserConfigurationException
 */

	private boolean validateVoucher(YFSEnvironment env, String sVoucherNo, String sPaymentTotal) throws YIFClientCreationException, RemoteException, ParserConfigurationException {
		
		boolean valid = true;
		
		try {
			Document voucherCheckInput = XMLUtil.createDocument("Voucher");
			Element VoucherElement = voucherCheckInput.getDocumentElement();
			VoucherElement.setAttribute("CertNo", sVoucherNo);//Setting the input
			//VoucherElement.setAttribute("IssueAmount", sPaymentTotal);//Setting the input
			api = YIFClientFactory.getInstance().getApi();
			Document voucherOutDoc = api.executeFlow(env, VSIConstants.SERVICE_GET_VOUCHER_LIST, voucherCheckInput);
			
			
			if(null!= (Element) voucherOutDoc.getElementsByTagName("Voucher").item(0)){
				Element outputVEle = (Element) voucherOutDoc.getElementsByTagName("Voucher").item(0);
			String sRedeemStatus = outputVEle.getAttribute(VSIConstants.ATTR_REDEEM_STATUS);
			String sIssueAmount = outputVEle.getAttribute("IssueAmount");
			Double dIssueAmount = Double.parseDouble(sIssueAmount);
			Double dPaymentTotal = Double.parseDouble(sPaymentTotal);
			
			if(sRedeemStatus.equals("Y") || dIssueAmount < dPaymentTotal){
			 valid = false;
			}
			
			}
			else{
				
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
						"getVoucherList Output is null");

			}
		} 
		catch (YFSException e) {
			e.printStackTrace();

			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"YFS Exception");
		} 
		
		return valid;
	}


/**
 * 
 * @param env
 * @param sVoucherNo
 * @param sPaymentTotal
 * @param sRedeemDate
 * @param sRedeemOrdNo
 * @param sRedeemStore
 * @param sRedeemCustomerNo 
 * @throws Exception 
 */

	private void RedeemVoucher(YFSEnvironment env, String sVoucherNo, String sPaymentTotal,
			String sRedeemDate, String sRedeemOrdNo, String sRedeemStore, String sRedeemCustomerNo) throws Exception {
		
		
	 	try {
			
		
		
		Document voucherRedeemInput = XMLUtil.createDocument("Voucher");
		Element VoucherElement = voucherRedeemInput.getDocumentElement();
		VoucherElement.setAttribute("CertNo", sVoucherNo);//Voucher no handle
		VoucherElement.setAttribute("RedeemAmount", sPaymentTotal);//Setting the Redeem Amount 
		VoucherElement.setAttribute("RedeemDate", sRedeemDate);//Setting the Redeem Date
		VoucherElement.setAttribute("RedeemStore", sRedeemStore);//Setting the Redeem Store
		VoucherElement.setAttribute("RedeemStatus", "Y");//Setting the voucher as redeemed
		VoucherElement.setAttribute("RedeemOrderNo", sRedeemOrdNo);//Setting the Order No against which it was redeemd
		VoucherElement.setAttribute("RedeemCustomerNo", sRedeemCustomerNo);//Setting the Cust No who redeemed the voucher
		
		
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element orderEle = getOrderListInput.getDocumentElement();
		orderEle.setAttribute("OrderNo", sRedeemOrdNo);
		orderEle.setAttribute("DocumentType", "0001");
		//orderEle.setAttribute("EnterpriseCode","VSI.com");
		api = YIFClientFactory.getInstance().getApi();
		Document outdocOrderList = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
        Element orderList = outdocOrderList.getDocumentElement();
		
		Element orderElement = (Element) orderList.getElementsByTagName("Order").item(0); 
		String orderType=orderElement.getAttribute("OrderType");
		
		Element orderLinesEle = SCXmlUtil.getChildElement(orderElement, "OrderLines");
		Element orderLineEle = SCXmlUtil.getChildElement(orderLinesEle, "OrderLine");
		String deliveryMethod = orderLineEle.getAttribute("DeliveryMethod");

		 String seqNum ="VSI_SEQ_V_TRANID_";
		 String shipNodePadded= "";
		 if(orderType != null && orderType.equalsIgnoreCase("POS")){
			 seqNum = seqNum+"88_";
		 }
		 else if("SHP".equals(deliveryMethod))
		 {
			 seqNum = seqNum+"5_";
		 }else if ("PICK".equals(deliveryMethod)){
			 seqNum = seqNum+"89_";
		 }
		 String store="8002";
	
			 shipNodePadded = ("00000" + store).substring(store.trim()
					 .length());
		 seqNum = seqNum+shipNodePadded;

		String tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
			
		VoucherElement.setAttribute("Comments", tranNumber);//Setting transaction number
		
		if(log.isDebugEnabled()){
    		log.debug("Voucher Input:"+XMLUtil.getXMLString(voucherRedeemInput));
		}
		
		api = YIFClientFactory.getInstance().getApi();
		api.executeFlow(env, VSIConstants.SERVICE_CHANGE_VOUCHER, voucherRedeemInput);
		
		if(!YFCObject.isVoid(sVoucherNo) && StringUtils.isNumeric(sVoucherNo)){
			sendVoucherFeed(env,sRedeemOrdNo,sVoucherNo);
		}
		
	 	} 
		catch (Exception e) {
			
			e.printStackTrace();
		}

		
	}




/**
 * 
 * @param env
 * @param sRedeemOrdNo
 * @param sVoucherNo 
 * @throws ParserConfigurationException
 * @throws YFSException
 * @throws RemoteException
 * @throws YIFClientCreationException
 */

	private void sendVoucherFeed(YFSEnvironment env, String sRedeemOrdNo, String sVoucherNo) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		
		Document voucherFeedInput = XMLUtil.createDocument("Voucher");
		Element VoucherElement = voucherFeedInput.getDocumentElement();
		VoucherElement.setAttribute("RedeemOrderNo", sRedeemOrdNo);
		VoucherElement.setAttribute("CertNo", sVoucherNo);
		api = YIFClientFactory.getInstance().getApi();
		
		api.executeFlow(env, VSIConstants.SERVICE_VOUCHER_FEED, voucherFeedInput);//Send voucher feed to sales audit
	}
	
	/**
	 * 
	 * @param env
	 * @param sVoucherNo
	 * @param string
	 * @return
	 */
	private boolean validateVoucherJDA(YFSEnvironment env, Element orderEle, String sVoucherNo){

		Document inXML = SCXmlUtil.createDocument();
		Element eleInput = inXML.createElement("Coupon");
		inXML.appendChild(eleInput);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		Calendar cal = Calendar.getInstance();
		String strCurrentDate = sdf.format(cal.getTime());
		String orderNo= orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strCustomerID = orderEle.getAttribute("BillToID");
		Element orderLineEle = (Element) orderEle.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
		String shipNode= orderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		if(YFCCommon.isVoid(shipNode))
		{
			shipNode = orderEle.getAttribute(VSIConstants.ATTR_ENTERED_BY);
		}
		
		eleInput.setAttribute("CouponID", sVoucherNo);
		eleInput.setAttribute(VSIConstants.ATTR_SHIP_NODE, shipNode);
		eleInput.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		eleInput.setAttribute(VSIConstants.ATTR_ORDER_DATE, strCurrentDate);
		if(!YFCObject.isVoid(strCustomerID)){
			eleInput.setAttribute("CustomerID", strCustomerID);
		}
				
		VSIValidateCouponFromJDA vsiValidateFromJDA = new VSIValidateCouponFromJDA();
		Document outDoc = vsiValidateFromJDA.invoke(env, inXML);
		
		String isVal = outDoc.getDocumentElement().getAttribute("IsCouponValid");
		if(!VSIConstants.FLAG_Y.equalsIgnoreCase(isVal)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 
	 * @param env
	 * @param sVoucherNo
	 * @param string
	 * @return
		 	<VoucherList>
			    <Voucher CertNo="" CertType="" IssueAmount="" RedeemStatus=""/>
			</VoucherList>
	 */
	public Document voucherLookupWithJDA(YFSEnvironment env, Document inDoc){
		
		VSIValidateCouponFromJDA vsiValidateFromJDA = new VSIValidateCouponFromJDA();
		Document result = SCXmlUtil.createDocument("Voucher");
		Element eleVoucher = result.getDocumentElement();
		
		try{
			Document outDoc = vsiValidateFromJDA.couponLookupJDA(env, inDoc);
			YFCElement eleOut = YFCDocument.getDocumentFor(outDoc)
					.getDocumentElement();
			if(log.isDebugEnabled()){
	    		log.debug("Output:::"+SCXmlUtil.getString(outDoc));
			}
			
			String strVoucher = eleOut.getChildElement("coupon").getNodeValue();
			//OMS-1148 : Start
			String strAmount ="0.0";	
			//OMS-1148:End
			String isVal = eleOut.getChildElement("valid").getNodeValue();
			String isError = eleOut.getChildElement("errcode").getNodeValue();
			//OMS-1148 : Start
			if("002".equals(isError) || "000".equals(isError))
			{
				strAmount=eleOut.getChildElement("value").getNodeValue();
			}
			//OMS-1148:End
			if (!isVal.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
				eleVoucher.setAttribute("IsCallSuccessfull", "N");
				eleVoucher.setAttribute("CertNo", inDoc.getDocumentElement().getAttribute("CertNo"));
				eleVoucher.setAttribute("isValid", "N");
				//OMS-1148 : Start
				eleVoucher.setAttribute("ErrorCode", eleOut.getChildElement("errcode").getNodeValue());
				eleVoucher.setAttribute("Comments", eleOut.getChildElement("error").getNodeValue());
				eleVoucher.setAttribute("CertType", "Loyalty Voucher");
				eleVoucher.setAttribute("RedeemStatus", "");
				if(!"001".equals(isError))
				{
				eleVoucher.setAttribute("IssueCustNo", eleOut.getChildElement("issued_to").getNodeValue());
				eleVoucher.setAttribute("IssueDate", eleOut.getChildElement("start_date").getNodeValue());
				//OMS-1377 : Start
				eleVoucher.setAttribute("RedeemStore", eleOut.getChildElement("location").getNodeValue());
				//OMS-1377 : End
				}
				eleVoucher.setAttribute("IssueAmount", strAmount);
				//OMS-1148 : End
			}else{
				eleVoucher.setAttribute("CertNo", strVoucher);
				eleVoucher.setAttribute("CertType", "Loyalty Voucher");
				eleVoucher.setAttribute("IssueAmount", strAmount);
				eleVoucher.setAttribute("RedeemStatus", "");
				eleVoucher.setAttribute("isValid", "Y");
				//OMS-1148 : Start
				eleVoucher.setAttribute("IssueCustNo", eleOut.getChildElement("issued_to").getNodeValue());
				eleVoucher.setAttribute("IssueDate", eleOut.getChildElement("start_date").getNodeValue());
				eleVoucher.setAttribute("ErrorCode", eleOut.getChildElement("errcode").getNodeValue());
				eleVoucher.setAttribute("Comments", eleOut.getChildElement("error").getNodeValue());
				//OMS-1148 : End
				//OMS-1377 : Start
				eleVoucher.setAttribute("RedeemStore", eleOut.getChildElement("location").getNodeValue());
				//OMS-1377 : End
			}
		}catch(Exception e){
			eleVoucher.setAttribute("CertNo", inDoc.getDocumentElement().getAttribute("CertNo"));
			eleVoucher.setAttribute("isValid", "N");
			return result;
		}
		
		return result;
	}
}	
