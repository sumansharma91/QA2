package com.vsi.oms.api.order;	

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIBICall implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIBICall.class);
	YIFApi api;
	public Document vsiBICall(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIBICall================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));	
		}
		Element orderEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		Element extnEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
		String sTotalPrice = extnEle.getAttribute(VSIConstants.ATTR_EXTN_TOTAL_PRICE);
		String sOrderDate = orderEle.getAttribute(VSIConstants.ATTR_ORDER_DATE);
		String sOrderNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		NodeList paymentMethodList = inXML.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
		int pMethodLength = paymentMethodList.getLength();
		for (int i=0;i<pMethodLength;i++){
			
			Element paymentMethodEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD).item(i);
			
			String sPaymentType = paymentMethodEle.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
			
			if(sPaymentType.equalsIgnoreCase(GIFT_CARD)){
			String sSvcNo = paymentMethodEle.getAttribute(VSIConstants.ATTR_SVC_NO);
			String sPinNo = paymentMethodEle.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_1);
			Document outDoc = callSVS(env,sOrderDate,sSvcNo,sPinNo,sOrderNo,sTotalPrice);
			Element rcode = (Element) outDoc.getElementsByTagName("returnCode").item(0);
			String sRcode = rcode.getTextContent();
		
		if(sRcode.equalsIgnoreCase("01Approval")||sRcode.equalsIgnoreCase("01")||sRcode.equalsIgnoreCase("Approval")){
			Double dMaxLimit = handleSuccesfulResponse(env, outDoc,sTotalPrice,inXML);
			String sMaxLimit = String.valueOf(dMaxLimit);
			paymentMethodEle.setAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT, sMaxLimit);
			////System.out.println(VSIUtils.getDocumentXMLString(inXML));
		}
			else{
				
				//handleResponse(env,inXML);
				createHold(env,inXML);
			}

			
			}
		}
		return inXML;

	
	}
	/*private void handleResponse(YFSEnvironment env, Document outDoc) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		
		Element rcode = (Element) outDoc.getElementsByTagName("returnCode").item(0);
		String sRcode = rcode.getTextContent();
	
		createHold(env,outDoc);
		
		
		Document newDoc = XMLUtil.createDocument("Error");
		Element errorEle = newDoc.getDocumentElement();
		errorEle.setAttribute("ErrorReason", sRcode);
	
	}//  handleResponse	
	*/
	private Double handleSuccesfulResponse(YFSEnvironment env, Document outDoc, String sTotalPrice, Document inXML) {
		Element balanceAmountEle = (Element) outDoc.getElementsByTagName("balanceAmount").item(0);
		String sBalAmt=balanceAmountEle.getFirstChild().getTextContent();
		Double dBalAmt = Double.parseDouble(sBalAmt);
		Double dTotalPrice = Double.parseDouble(sTotalPrice);
		if(dBalAmt>dTotalPrice){
			return dTotalPrice;
			
		}else
		{
			return dBalAmt;
		}
		
	}
	private Document callSVS(YFSEnvironment env, String sOrderDate, String sSvcNo,
			String sPinNo, String sOrderNo, String sTotalPrice) throws Exception {
		Document GiftCardInput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GiftCardInput.getDocumentElement();
		GCElement.setAttribute("storeNumber", "6101");
		GCElement.setAttribute("cardNumber", sSvcNo);
		GCElement.setAttribute("pinNumber", sPinNo);
		Date todaysDate = new Date(); 
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
		String dateToday = formatter.format(todaysDate);
		////System.out.println("Test Date:"+dateToday);
		
		GCElement.setAttribute("date", dateToday);
		GCElement.setAttribute("invoiceNumber", sOrderNo);
		GCElement.setAttribute("amount", sTotalPrice);
		String sCalcTranID = generateTransactionID(env);
		String sTranID = "936860"+sCalcTranID;
		GCElement.setAttribute("transactionID", sTranID);
		
		////System.out.println(VSIUtils.getDocumentXMLString(GiftCardInput));
		if(log.isDebugEnabled()){
			log.debug("Printing Input XML :" + VSIUtils.getDocumentXMLString(GiftCardInput));
		}
		api = YIFClientFactory.getInstance().getApi();
		//OMS-868 :Start
		String strUserName=YFSSystem.getProperty("VSI_BI_CALL_USER_NAME");
		String strPassword=YFSSystem.getProperty("VSI_BI_CALL_PASSWORD");
		String strMerchantNumber=YFSSystem.getProperty("VSI_BI_CALL_MERCHANT_NUMBER");
		String strRoutingID=YFSSystem.getProperty("VSI_BI_ROUTING_ID");
		GCElement.setAttribute("Username", strUserName);
		GCElement.setAttribute("Password", strPassword);
		GCElement.setAttribute("merchantNumber", strMerchantNumber);
		GCElement.setAttribute("routingID", strRoutingID);
		//OMS-86 :End
		Document outDoc = api.executeFlow(env, "VSISVSBICall", GiftCardInput);
		////System.out.println(VSIUtils.getDocumentXMLString(outDoc));
	
	return outDoc;
		
	}
	private Document createHold(YFSEnvironment env, Document inXML) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		
	
		Element orderElement = inXML.getDocumentElement();
		
		Element orderEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER);
		String orderNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
		
		Element OrderHoldTypesElement = inXML.createElement("OrderHoldTypes");
		orderElement.appendChild(OrderHoldTypesElement);
		
		Element OrderHoldTypeElement = inXML.createElement("OrderHoldType");
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_HOLD_TYPE, VSIConstants.HOLD_SVS_UNAVAILABLE);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_REASON_TEXT, VSIConstants.REASON_SVS_UNAVAILABLE);
		OrderHoldTypeElement.setAttribute(VSIConstants.ATTR_STATUS, "1100");
		
		OrderHoldTypesElement.appendChild(OrderHoldTypeElement);
		

		return inXML;
		
		
	}

	
private String generateTransactionID(YFSEnvironment env) throws Exception {
		
		String tranNumber = "";
	 	String seqNum ="VSI_SEQ_TRAN_ID";
	 	tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		return tranNumber;
		
	} 
}
