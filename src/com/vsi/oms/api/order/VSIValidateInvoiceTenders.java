package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIValidateInvoiceTenders {

	private YFCLogCategory log = YFCLogCategory.instance(VSISendInvoice.class);
	YIFApi api;
	public Document validateTenders(YFSEnvironment env, Document inXML)
	throws Exception {
		 double nonVoucherAmt=0.00;
		Element invoiceHeaderele = (Element) inXML.getElementsByTagName("InvoiceHeader").item(0);
		double invoiceAmt=Double.parseDouble(invoiceHeaderele.getAttribute("AmountCollected"));
		NodeList paymentMethoList =inXML.getElementsByTagName("PaymentMethod");
		String voucherFlag="N";
		 for (int i = 0; i < paymentMethoList.getLength(); i++) 
		{
			 Element paymentMethodI = (Element) paymentMethoList.item(i);
			 String paymentType=paymentMethodI.getAttribute("PaymentType");
			 if(paymentType.equalsIgnoreCase("VOUCHERS")){
				 voucherFlag="Y"; 
			 }
			 
		}
		 if(voucherFlag.equalsIgnoreCase("Y")){
			
		 for (int k = 0; k < paymentMethoList.getLength(); k++) {
			 Element paymentMethodK = (Element) paymentMethoList.item(k);
             String paymentTypeK=paymentMethodK.getAttribute("PaymentType");
             if(paymentTypeK.equalsIgnoreCase("PAYPAL") ||paymentTypeK.equalsIgnoreCase("CREDIT_CARD") 
            		 || paymentTypeK.equalsIgnoreCase("GIFT_CARD") ||paymentTypeK.equalsIgnoreCase("ONLINE_GIFT_CARD")  ){
            	 double nonVcAmt=Double.parseDouble(paymentMethodK.getAttribute("TotalCharged"));
            	 nonVoucherAmt=nonVcAmt+nonVoucherAmt;
             }
		 }
		 
		 
		 double updateVocuherAmt=invoiceAmt-nonVoucherAmt;
		 for (int l = 0; l < paymentMethoList.getLength(); l++) {
			 Element paymentMethodl = (Element) paymentMethoList.item(l);
             String paymentTypeK=paymentMethodl.getAttribute("PaymentType");
             if(paymentTypeK.equalsIgnoreCase("VOUCHERS")){
            	 paymentMethodl.setAttribute("TotalCharged",Double.toString(updateVocuherAmt));
             }
		 }
		 }
		
		
		return inXML;
	}
}
