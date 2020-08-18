package com.vsi.oms.api.order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIBICallCenter {

	private YFCLogCategory log = YFCLogCategory.instance(VSIBICallCenter.class);
	YIFApi api;
	public Document vsiBICallCenter(YFSEnvironment env, Document inXML)
			throws Exception {
				
		

		Element rootElement = inXML.getDocumentElement();
		
		String sSvcNo = rootElement.getAttribute("cardNumber");
		String sPinNo = rootElement.getAttribute("pinNumber");
		
		Document outDoc = callSVS(env,sSvcNo,sPinNo);
		
		Element rcode = (Element) outDoc.getElementsByTagName("returnCode").item(0);
		String sRcode = rcode.getTextContent();
	
	if(sRcode.equalsIgnoreCase("01Approval")||sRcode.equalsIgnoreCase("01")||sRcode.equalsIgnoreCase("Approval")){
			
		Element balanceAmountEle = (Element) outDoc.getElementsByTagName("balanceAmount").item(0);
		String sBalAmt=balanceAmountEle.getFirstChild().getTextContent();

		Document GCOutput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GCOutput.getDocumentElement();
		GCElement.setAttribute("IsCallSuccesful", "Y");
		GCElement.setAttribute("IsValid", "Y");
		GCElement.setAttribute("Balance", sBalAmt);
		
		return GCOutput;
		
	
	}else{
		
		String sReasonCode = rcode.getLastChild().getTextContent();
		
		
		Document GCOutput = XMLUtil.createDocument("GiftCard");
		Element GCElement = GCOutput.getDocumentElement();
		GCElement.setAttribute("IsCallSuccesful", "Y");
		GCElement.setAttribute("IsValid", "N");
		GCElement.setAttribute("ReasonCode", sReasonCode);

		return GCOutput;

	}
	}
	
		

	
	private Document callSVS(YFSEnvironment env, String sSvcNo,
			String sPinNo) throws Exception {
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
		GCElement.setAttribute("invoiceNumber", "BalanceInquiry");
		GCElement.setAttribute("amount", "0");
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
private String generateTransactionID(YFSEnvironment env) throws Exception {
		
		String tranNumber = "";
	 	String seqNum ="VSI_SEQ_TRAN_ID";
	 	tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		return tranNumber;
		
	} 

	
}
