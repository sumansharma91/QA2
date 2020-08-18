package com.vsi.oms.api;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;




import com.sterlingcommerce.baseutil.SCXmlUtil;
//import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
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
public class VSIESGetOrderDetails {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIESGetOrderDetails.class);
	YIFApi api;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors	
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public Document VSIGetOrderDetails(YFSEnvironment env, Document inXML)
			throws Exception {
		Document outputDocLineList=null;
		Element rootElement = inXML.getDocumentElement();
		String customerPONo = rootElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
		 if (!YFCCommon.isVoid(customerPONo)) {
		try{
			
			rootElement.setAttribute("Status","9000");
			rootElement.setAttribute("StatusQryType","NE");
			
			if(log.isDebugEnabled()){
	    		log.debug("customer po no is" + customerPONo);
			}

						
			//to find order header key from the customer po no, calling getOrderList using the customerPONo
			
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element orderEle = getOrderListInput.getDocumentElement();
			Element orderLineEle = XMLUtil.createElement(getOrderListInput, VSIConstants.ELE_ORDER_LINE, "");
			orderLineEle.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPONo);
			orderEle.appendChild(orderLineEle);
			api = YIFClientFactory.getInstance().getApi();
			Document outdocOrderList = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			Element orderElem = (Element) outdocOrderList.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String OrderType=orderElem.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			
			if(OrderType.equalsIgnoreCase("WEB"))
			{
				if(log.isDebugEnabled()){
		    		log.debug("orderheaderkey is " + orderHeaderKey);
				}


			/*Calling CustomerPoNoRecordsList api to find if there any records which have been already sent once or not */
						
			Document getCustPoRecords = XMLUtil.createDocument("CustomerPoNoRecords");
			Element eleOrder = getCustPoRecords.getDocumentElement();
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
			eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPONo);
			api = YIFClientFactory.getInstance().getApi();
			Document ouctDocCustPoRecords = api.executeFlow(env,"getCustomerPoNoRecordsList", getCustPoRecords);
			NodeList custPoRecordList = ouctDocCustPoRecords.getElementsByTagName("CustomerPoNoRecords");
			//if the records already exist
			DecimalFormat df = new DecimalFormat("#.##");

			HashMap<String, Double> ccStoredTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> ccListedTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> gcStoredTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> gcListedTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> vcStoredTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> vcListedTenderAmt = new HashMap<String, Double>();
			//For PayPal
			HashMap<String, Double> ppStoredTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> ppListedTenderAmt = new HashMap<String, Double>();

			HashMap<String, Double> paymentTenderAmt = new HashMap<String, Double>();
			HashMap<String, Double> ccUsedKeyAmt = new HashMap<String, Double>();
			HashMap<String, Double> vcUsedKeyAmt = new HashMap<String, Double>();
			HashMap<String, Double> gcUsedKeyAmt = new HashMap<String, Double>();
			HashMap<String, Double> ppUsedKeyAmt = new HashMap<String, Double>();

			HashMap<String, Double> vcLeftTenderAmt = new HashMap<String, Double>();
			 HashMap<String, Double> ccLeftTenderAmt = new HashMap<String, Double>();
			 HashMap<String, Double> gcLeftTenderAmt = new HashMap<String, Double>();
			 HashMap<String, Double> ppLeftTenderAmt = new HashMap<String, Double>();

//for PayPal

			if(custPoRecordList.getLength()>0){
				//System.out.println("In First Loop");


			double intVoucherAmount=0.00;
			double GrandLinestotal=0.00;
			double intGiftCardAmount=0.00;
			double intCreditCardAmount=0.00;
			double intPayPalAmount=0.00;

			double voucherTotalamt=0.00;
			double gcTotalamt=0.00;
			double ccTotalamt=0.00;
			double ppTotalamt=0.00;

			 for (int f = 0; f < custPoRecordList.getLength(); f++) {

			Element custoPoRecord = (Element) custPoRecordList.item(f);
		

			if(!custoPoRecord.getAttribute("VouchersAmt").equalsIgnoreCase("0.00")){
			double voucherAmount=Double.parseDouble(custoPoRecord.getAttribute("VouchersAmt"));
			if(log.isDebugEnabled()){
	    		log.debug("voucher amt  is " + voucherAmount);
			}
			String tenderNoVc=custoPoRecord.getAttribute("TenderNo");
			if(log.isDebugEnabled()){
	    		log.debug("tenderNoVc  is " + tenderNoVc);
			}

			paymentTenderAmt.put(tenderNoVc, voucherAmount);
			voucherTotalamt=voucherTotalamt+voucherAmount;
				
			}
			

			if(!custoPoRecord.getAttribute("CreaditCardAmt").equalsIgnoreCase("0.00")){
			double creditCardAmount=Double.parseDouble(custoPoRecord.getAttribute("CreaditCardAmt"));
			if(log.isDebugEnabled()){
	    		log.debug("credit card amt  is " + creditCardAmount);
			}
			String tenderNoCc=custoPoRecord.getAttribute("TenderNo");
			//System.out.println("tenderNoCc  is " + tenderNoCc);

			paymentTenderAmt.put(tenderNoCc, creditCardAmount);
			ccTotalamt=ccTotalamt+creditCardAmount;
				
			}
			if(!custoPoRecord.getAttribute("PayPalAmt").equalsIgnoreCase("0.00")){
				double paypalAmount=Double.parseDouble(custoPoRecord.getAttribute("PayPalAmt"));
				if(log.isDebugEnabled()){
		    		log.debug("paypal card amt  is " + paypalAmount);
				}
				String tenderNoPp=custoPoRecord.getAttribute("TenderNo");
				//System.out.println("tenderNoPp  is " + tenderNoPp);

				paymentTenderAmt.put(tenderNoPp, paypalAmount);
				ppTotalamt=ppTotalamt+paypalAmount;
					
				}
			
			if(!custoPoRecord.getAttribute("GiftCardAmt").equalsIgnoreCase("0.00")){
			double giftCardAmount=Double.parseDouble(custoPoRecord.getAttribute("GiftCardAmt"));
			if(log.isDebugEnabled()){
	    		log.debug("gift card amt  is " + giftCardAmount);
			}
			String tenderNoGc=custoPoRecord.getAttribute("TenderNo");
			//System.out.println("tenderNoGc is " + tenderNoGc);

			paymentTenderAmt.put(tenderNoGc, giftCardAmount);
			gcTotalamt=gcTotalamt+giftCardAmount;
			}	
			 }
			GrandLinestotal=voucherTotalamt+ccTotalamt+gcTotalamt+ppTotalamt;
			String GrandOrderTotal=df.format(GrandLinestotal);
			
			if(log.isDebugEnabled()){
	    		log.debug("GrandOrderTotal  is " + GrandOrderTotal);
			}
						
			 env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,"global/template/api/ESOrderDetailViewOutputTemplate.xml");
	         api = YIFClientFactory.getInstance().getLocalApi();
	          outputDocLineList = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,inXML);	
	         env.clearApiTemplates();
			 NodeList overallTotals = outputDocLineList.getElementsByTagName("OverallTotals");
			 for (int i = 0; i < overallTotals.getLength(); i++) {
			Element overallTotalsElement = (Element) overallTotals.item(i);
			overallTotalsElement.setAttribute("GrandTotal",GrandOrderTotal);
			 }
			 NodeList paymentMenthodList = outputDocLineList.getElementsByTagName("PaymentMethod");
			 String ccFlag=null;
			 String gcFlag=null;
			 String vcFlag=null;
			 String ppFlag=null;


			 for (int k = 0; k < paymentMenthodList.getLength(); k++) {
				Element paymentMethodElement = (Element) paymentMenthodList.item(k);
				String paymentType=paymentMethodElement.getAttribute("PaymentType");
				if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
					String creditCardno=paymentMethodElement.getAttribute("CreditCardNo");
					if(paymentTenderAmt.containsKey(creditCardno)){
						paymentMethodElement.setAttribute("MaxChargeLimit",df.format(paymentTenderAmt.get(creditCardno)));

					}
					else {
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}
					/**for(String Key :paymentTenderAmt.keySet() ){
						System.out.println("key is " + Key);
					if (Key.equalsIgnoreCase(creditCardno)){
						System.out.println("key matches");
					paymentMethodElement.setAttribute("MaxChargeLimit",paymentTenderAmt.get(Key).toString());
					ccFlag="Y";
					}
					}
					if(ccFlag.equalsIgnoreCase("N")){
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}**/
							
				}
				
				if(paymentType.equalsIgnoreCase("PAYPAL")){
					String payPalOrderId=paymentMethodElement.getAttribute("PaymentReference1");
					if(paymentTenderAmt.containsKey(payPalOrderId)){
						paymentMethodElement.setAttribute("MaxChargeLimit",df.format(paymentTenderAmt.get(payPalOrderId)));

					}
					else {
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}
					/**for(String Key :paymentTenderAmt.keySet() ){
						System.out.println("key is " + Key);
					if (Key.equalsIgnoreCase(creditCardno)){
						System.out.println("key matches");
					paymentMethodElement.setAttribute("MaxChargeLimit",paymentTenderAmt.get(Key).toString());
					ccFlag="Y";
					}
					}
					if(ccFlag.equalsIgnoreCase("N")){
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}**/
							
				}
				
				else if(paymentType.equalsIgnoreCase("VOUCHERS")){
					String voucherNo=paymentMethodElement.getAttribute("PaymentReference1");
					//System.out.println("voucherNo is " + voucherNo);
					if(paymentTenderAmt.containsKey(voucherNo)){
						paymentMethodElement.setAttribute("MaxChargeLimit",df.format(paymentTenderAmt.get(voucherNo)));

					}
					else {
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}
					
/**				for(String Key :paymentTenderAmt.keySet() ){
						System.out.println("key is " + Key);

					if(Key.equalsIgnoreCase(voucherNo)){
						System.out.println("key matches");

					paymentMethodElement.setAttribute("MaxChargeLimit", paymentTenderAmt.get(Key).toString());
					vcFlag="Y";
					}
					}
					if(vcFlag.equalsIgnoreCase("N")){
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}
					**/
					
					
				}
				else if(paymentType.equalsIgnoreCase("GIFT_CARD")){
					String gcNumber=paymentMethodElement.getAttribute("SvcNo");
					//System.out.println("gcNumber is " + gcNumber);

					if(paymentTenderAmt.containsKey(gcNumber)){
						paymentMethodElement.setAttribute("MaxChargeLimit",df.format(paymentTenderAmt.get(gcNumber)));

					}
					else {
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					}
				/**	for(String Key :paymentTenderAmt.keySet() ){
						System.out.println("key is " + Key);

					if(Key.equalsIgnoreCase(gcNumber)){
						System.out.println("key matches");

					paymentMethodElement.setAttribute("MaxChargeLimit", paymentTenderAmt.get(Key).toString());
					gcFlag="Y";

					}
					}
					if(gcFlag.equalsIgnoreCase("N")){
						paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

					} **/
					
				}
			 }
			
			}
			else {
				//System.out.println("In Second Loop");
				String GrandOrderTotalO=null;
												
				Document getCustPORecords = XMLUtil.createDocument("CustomerPoNoRecords");
				Element EleOrder = getCustPORecords.getDocumentElement();
				EleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
				api = YIFClientFactory.getInstance().getApi();
				Document ouctDocCustPORecords = api.executeFlow(env,"getCustomerPoNoRecordsList", getCustPORecords);
				NodeList custNoRecords = ouctDocCustPORecords.getElementsByTagName("CustomerPoNoRecords");
				
				
				if(custNoRecords.getLength()>0){
					


					double storedVoucherAmount=0.00;
					double voucherAmount=0.00;
					double creditCardAmount=0.00;
					double giftCardAmount=0.00;
					double payPalAmount=0.00;

					double GrandLinestotal=0.00;
					double storedGiftCardAmount=0.00;
					double storedCreditCardAmount=0.00;
					double storedPayPalAmount=0.00;

					
					double dVoucherAmount=0.00;
					double dGiftCardAmount=0.00;
					double dCreditCardAmount=0.00;
					double dPayPalAmount=0.00;
					
					double dVoucherAmountLeft=0.00;
					double dGiftCardAmountLeft=0.00;
					double dCreditCardAmountLeft=0.00;
					double dPayPalAmountLeft=0.00;

					double dVoucherAmountToUse=0.00;
					double dGiftCardAmountToUse=0.00;
					double dCreditCardAmountToUse=0.00;
					double dPayPalAmountToUse=0.00;

					
					for (int i = 0; i < custNoRecords.getLength(); i++) {
						Element custoPoRecord = (Element) custNoRecords.item(i);

						if(!custoPoRecord.getAttribute("VouchersAmt").equalsIgnoreCase("0.00")){
						double voucheramt=Double.parseDouble(custoPoRecord.getAttribute("VouchersAmt"));
						String vcTender=custoPoRecord.getAttribute("TenderNo");
						vcStoredTenderAmt.put(vcTender, voucheramt);
						voucherAmount=voucherAmount+voucheramt;
							

							//voucherAmount=voucherAmount+Double.parseDouble(custoPoRecord.getAttribute("VouchersAmt"));
						}
						if(!custoPoRecord.getAttribute("CreaditCardAmt").equalsIgnoreCase("0.00")){
							double ccamt=Double.parseDouble(custoPoRecord.getAttribute("CreaditCardAmt"));
							String ccTender=custoPoRecord.getAttribute("TenderNo");
							ccStoredTenderAmt.put(ccTender, ccamt);
								creditCardAmount=creditCardAmount+ccamt;
							}
						if(!custoPoRecord.getAttribute("PayPalAmt").equalsIgnoreCase("0.00")){
							double ppamt=Double.parseDouble(custoPoRecord.getAttribute("PayPalAmt"));
							String ppTender=custoPoRecord.getAttribute("TenderNo");
							ppStoredTenderAmt.put(ppTender, ppamt);
							payPalAmount=payPalAmount+ppamt;
							}
						
						if(!custoPoRecord.getAttribute("GiftCardAmt").equalsIgnoreCase("0.00")){
                			double gcamt=Double.parseDouble(custoPoRecord.getAttribute("GiftCardAmt"));
							String gcTender=custoPoRecord.getAttribute("TenderNo");
							gcStoredTenderAmt.put(gcTender, gcamt);
								giftCardAmount=giftCardAmount+gcamt;
								}
						
						}
											
					
					if(voucherAmount!=0.00){
						storedVoucherAmount=voucherAmount;
						//System.out.println("stored voucher amount is " + storedVoucherAmount);

						}
						if(creditCardAmount!=0.00){
							storedCreditCardAmount=creditCardAmount;
							//System.out.println("stored cc amount is " + storedCreditCardAmount);

						}
						if(payPalAmount!=0.00){
							storedPayPalAmount=payPalAmount;
							//System.out.println("stored cc amount is " + storedPayPalAmount);

						}
						
						if(giftCardAmount!=0.00){
							storedGiftCardAmount=giftCardAmount;
							//System.out.println("stored gc amount is " + storedGiftCardAmount);

						}
						
					 env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,"global/template/api/ESOrderDetailViewOutputTemplate.xml");
			         api = YIFClientFactory.getInstance().getLocalApi();
			         outputDocLineList = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,inXML);	
			         env.clearApiTemplates();
			         
			         
			            NodeList linePriceInfo = outputDocLineList.getElementsByTagName("LinePriceInfo");
						for (int i = 0; i < linePriceInfo.getLength(); i++) {
						Element linePriceInfoEle = (Element) linePriceInfo.item(i);
						String lineTotal=linePriceInfoEle.getAttribute("LineTotal");
						double intLineTotal=Double.parseDouble(lineTotal);
						GrandLinestotal=GrandLinestotal+intLineTotal;
						 //GrandOrderTotalO=Double.toString(GrandLinestotal);
						 GrandOrderTotalO=df.format(GrandLinestotal);

						}
						
						 NodeList overallTotals = outputDocLineList.getElementsByTagName("OverallTotals");
						 for (int i = 0; i < overallTotals.getLength(); i++) {
						Element overallTotalsElement = (Element) overallTotals.item(i);
						overallTotalsElement.setAttribute("GrandTotal",GrandOrderTotalO);
						}
						 
						 NodeList paymentMenthodList = outputDocLineList.getElementsByTagName("PaymentMethod");
						 for (int k = 0; k < paymentMenthodList.getLength(); k++) {
							Element paymentMethodElement = (Element) paymentMenthodList.item(k);
							String paymentType=paymentMethodElement.getAttribute("PaymentType");
							if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
								String ccTender=paymentMethodElement.getAttribute("CreditCardNo");
								if(!ccListedTenderAmt.containsKey(ccTender)){
								dCreditCardAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
								//System.out.println("CC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
								ccListedTenderAmt.put(ccTender, dCreditCardAmount);
								}

							}
							if(paymentType.equalsIgnoreCase("PAYPAL")){
								String ppTender=paymentMethodElement.getAttribute("PaymentReference1");
								if(!ppListedTenderAmt.containsKey(ppTender)){
								dPayPalAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
								//System.out.println("PP MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
								ppListedTenderAmt.put(ppTender, dPayPalAmount);
								}

							}
							
							if(paymentType.equalsIgnoreCase("VOUCHERS")){
								String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
								if(!vcListedTenderAmt.containsKey(vcTender)){

								dVoucherAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
								//System.out.println("VC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
								vcListedTenderAmt.put(vcTender, dVoucherAmount);
								
								}
							}
							if(paymentType.equalsIgnoreCase("GIFT_CARD")){
								String gcTender=paymentMethodElement.getAttribute("SvcNo");
								if(!gcListedTenderAmt.containsKey(gcTender)){

								dGiftCardAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
								//System.out.println("GC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
								gcListedTenderAmt.put(gcTender, dGiftCardAmount);
							}
							}
										 
						 }
						 
					
						 
						 //to find out amount of voucher left
						 
						 
						 for(String Key : vcListedTenderAmt.keySet()){
							 if(vcStoredTenderAmt.containsKey(Key)){
								 Double storedVcAmt=vcStoredTenderAmt.get(Key);
								 Double listedVcamt=vcListedTenderAmt.get(Key);
								 
								 if(listedVcamt>=storedVcAmt){
									 double leftVcAmt=listedVcamt-storedVcAmt;
									 vcLeftTenderAmt.put(Key, leftVcAmt);
									 dVoucherAmountToUse=dVoucherAmountToUse+leftVcAmt;

									 
								 }
							 }
							 else{
								 vcLeftTenderAmt.put(Key, vcListedTenderAmt.get(Key));
								 dVoucherAmountToUse=dVoucherAmountToUse+vcListedTenderAmt.get(Key);


							 }
						 }
						 
						 for(String Key : ccListedTenderAmt.keySet()){
							 if(ccStoredTenderAmt.containsKey(Key)){
								 Double storedCcAmt=ccStoredTenderAmt.get(Key);
								 Double listedCcamt=ccListedTenderAmt.get(Key);
								 
								 if(listedCcamt>=storedCcAmt){
									 double leftCcAmt=listedCcamt-storedCcAmt;
									 ccLeftTenderAmt.put(Key, leftCcAmt);
									 dCreditCardAmountToUse=dCreditCardAmountToUse+leftCcAmt;

									 
								 }
							 }
							 else{
								 ccLeftTenderAmt.put(Key, ccListedTenderAmt.get(Key));
								 dCreditCardAmountToUse=dCreditCardAmountToUse+ccListedTenderAmt.get(Key);


							 }
						 }
						 
						 for(String Key : ppListedTenderAmt.keySet()){
							 if(ppStoredTenderAmt.containsKey(Key)){
								 Double storedPpAmt=ppStoredTenderAmt.get(Key);
								 Double listedPpamt=ppListedTenderAmt.get(Key);
								 
								 if(listedPpamt>=storedPpAmt){
									 double leftPpAmt=listedPpamt-storedPpAmt;
									 ppLeftTenderAmt.put(Key, leftPpAmt);
									 dPayPalAmountToUse=dPayPalAmountToUse+leftPpAmt;

									 
								 }
							 }
							 else{
								 ppLeftTenderAmt.put(Key, ppListedTenderAmt.get(Key));
								 dPayPalAmountToUse=dPayPalAmountToUse+ppListedTenderAmt.get(Key);


							 }
						 }
						 
						 for(String Key : gcListedTenderAmt.keySet()){
							 if(gcStoredTenderAmt.containsKey(Key)){
								 Double storedGcAmt=gcStoredTenderAmt.get(Key);
								 Double listedGcamt=gcListedTenderAmt.get(Key);
								 
								 if(listedGcamt>=storedGcAmt){
									 double leftGcAmt=listedGcamt-storedGcAmt;
									 gcLeftTenderAmt.put(Key, leftGcAmt);
									 dGiftCardAmountToUse=dGiftCardAmountToUse+leftGcAmt;

									 
								 }
							 }
							 else{
								 gcLeftTenderAmt.put(Key, gcListedTenderAmt.get(Key));
								 dGiftCardAmountToUse=dGiftCardAmountToUse+gcListedTenderAmt.get(Key);


							 }
						 }
						 
				 
/**
						 for(String Key : vcListedTenderAmt.keySet()){
							 double vcAmt=vcListedTenderAmt.get(Key);

							// voucherAmounttotal=voucherAmounttotal+vcAmt;
							 if(storedVoucherAmount <= vcAmt){
								 dVoucherAmountLeft= vcAmt-storedVoucherAmount;
								 vcLeftTenderAmt.put(Key, dVoucherAmountLeft);
								 storedVoucherAmount=0.00;
								 dVoucherAmountToUse=dVoucherAmountToUse+dVoucherAmountLeft;
								 
								 
							 }
							 else{
								 
								 dVoucherAmountLeft=storedVoucherAmount-vcAmt;
								 vcLeftTenderAmt.put(Key, 0.00);
								 storedVoucherAmount=dVoucherAmountLeft;

							 }
							 
						 }
					**/

					/**	 for(String Key : ccListedTenderAmt.keySet()){
							 double ccAmt=ccListedTenderAmt.get(Key);

							// voucherAmounttotal=voucherAmounttotal+vcAmt;
							 if(storedCreditCardAmount <= ccAmt){
								 dCreditCardAmountLeft= ccAmt-storedCreditCardAmount;
								 ccLeftTenderAmt.put(Key, dCreditCardAmountLeft);
								 storedCreditCardAmount=0.00;
								 dCreditCardAmountToUse=dCreditCardAmountToUse+dCreditCardAmountLeft;

							 }
							 else{
								 
								 dCreditCardAmountLeft=storedCreditCardAmount-ccAmt;
								 ccLeftTenderAmt.put(Key, 0.00);
								 storedCreditCardAmount=dCreditCardAmountLeft;

							 }
							 
						 }
						 

						 for(String Key : gcListedTenderAmt.keySet()){
							 double gcAmt=gcListedTenderAmt.get(Key);

							// voucherAmounttotal=voucherAmounttotal+vcAmt;
							 if(storedGiftCardAmount <= gcAmt){
								 dGiftCardAmountLeft= gcAmt-storedGiftCardAmount;
								 gcLeftTenderAmt.put(Key, dGiftCardAmountLeft);
								 storedGiftCardAmount=0.00;
								 dGiftCardAmountToUse=dGiftCardAmountToUse+dGiftCardAmountLeft;

							 }
							 else{
								 
								 dGiftCardAmountLeft=storedGiftCardAmount-gcAmt;
								 gcLeftTenderAmt.put(Key, 0.00);
								 storedGiftCardAmount=dGiftCardAmountLeft;

							 }
							 
						 }
						  **/
						 
					
						 
						 double vcgcAmt=dVoucherAmountLeft+dGiftCardAmountLeft;
						// dVoucherAmountLeft=dVoucherAmount-storedVoucherAmount;
						// dGiftCardAmountLeft=dGiftCardAmount-storedGiftCardAmount;
						// dCreditCardAmountLeft=dCreditCardAmount-storedCreditCardAmount;
							//System.out.println("Grand Total"+GrandLinestotal);

						 if(dVoucherAmountToUse>=GrandLinestotal){
								//System.out.println("In first payment Loop");
	 
							 
							 //double voucherUser=GrandLinestotal-dVoucherAmountLeft;
							// String strVoucherUser=Double.toString(GrandLinestotal);
							 NodeList paymentMenthodList1 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList1.getLength(); k++) {
								Element paymentMethodElement = (Element) paymentMenthodList1.item(k);
								String paymentType=paymentMethodElement.getAttribute("PaymentType");
								if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								if(paymentType.equalsIgnoreCase("PAYPAL")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								if(paymentType.equalsIgnoreCase("VOUCHERS")){
									//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
									String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
									for(String Key : vcLeftTenderAmt.keySet()){
										if(Key.equalsIgnoreCase(vcTender))
										{
											if(vcUsedKeyAmt.containsKey(vcTender)){
												//System.out.println("Inside if loop");

												//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcUsedKeyAmt.get(Key)));
												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));

											}
											
											else{
												double vcAmtKey=vcLeftTenderAmt.get(Key);
											
										if(vcAmtKey<=GrandLinestotal){
											
										//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcAmtKey));
										paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcAmtKey));

											GrandLinestotal=GrandLinestotal-vcAmtKey;
										 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey),"0.00","0.00","0.00",Key);
											vcUsedKeyAmt.put(vcTender, vcAmtKey);

										}
										else{
										if(GrandLinestotal!=0.00){
										//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(GrandLinestotal));
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));

											//double vcamount=vcAmtKey-GrandLinestotal;
										 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
											vcUsedKeyAmt.put(vcTender, GrandLinestotal);

										GrandLinestotal=0.00;
										}
										else{
											paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
										}
										}
										}
										
											
										}
																			

									 }

									
								}
								if(paymentType.equalsIgnoreCase("GIFT_CARD")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								
							 }
						 }

						 
						 else if(vcgcAmt >= GrandLinestotal) {
								//System.out.println("Grand Total"+GrandLinestotal);

								//System.out.println("In second payment Loop");

							 //String strVoucherUser=Double.toString(dVoucherAmountLeft);
							 //double gcUsed=GrandLinestotal-dVoucherAmountLeft;
							 //String strgiftCardUser=Double.toString(dGiftCardAmountLeft);
							 
							 NodeList paymentMenthodList2 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList2.getLength(); k++) {
									Element paymentMethodElement = (Element) paymentMenthodList2.item(k);
									String paymentType=paymentMethodElement.getAttribute("PaymentType");
									if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
										paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
										
									}
									if(paymentType.equalsIgnoreCase("PAYPAL")){
										paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
										
									}
									if(paymentType.equalsIgnoreCase("VOUCHERS")){

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
										for(String Key : vcLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(vcTender))
											{
												if(vcUsedKeyAmt.containsKey(vcTender)){
													//System.out.println("Inside if loop");

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));

													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcUsedKeyAmt.get(Key)));
												}
												else{
													
											
											 double vcAmtKey=vcLeftTenderAmt.get(Key);
											if(vcAmtKey<=GrandLinestotal){
												
											paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcAmtKey));
											GrandLinestotal=GrandLinestotal-vcAmtKey;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey),"0.00","0.00","0.00",Key);
												vcUsedKeyAmt.put(vcTender, vcAmtKey);

											}
											else{
												if(GrandLinestotal!=0.00){
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));

													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(GrandLinestotal));
													//double vcamount=vcAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
														vcUsedKeyAmt.put(vcTender, GrandLinestotal);

													 GrandLinestotal=0.00;
													}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
												}
												
											}
																				

										 }

										
									
										
									}
									if(paymentType.equalsIgnoreCase("GIFT_CARD")){

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String gcTender=paymentMethodElement.getAttribute("SvcNo");
										for(String Key : gcLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(gcTender))
											{
												if(gcUsedKeyAmt.containsKey(gcTender)){
													//System.out.println("Inside if loop");

													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(gcUsedKeyAmt.get(Key)));
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcUsedKeyAmt.get(Key)));

												}
												else{
													double gcAmtKey=gcLeftTenderAmt.get(Key);
												
											if(gcAmtKey<=GrandLinestotal){
												
											//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(gcAmtKey));
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcAmtKey));

											GrandLinestotal=GrandLinestotal-gcAmtKey;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(gcAmtKey),"0.00","0.00",Key);
												gcUsedKeyAmt.put(gcTender, gcAmtKey);

											}
											else{
												if(GrandLinestotal!=0.00){
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
											//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(GrandLinestotal));

											//double gcamount=gcAmtKey-GrandLinestotal;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(GrandLinestotal),"0.00","0.00",Key);
												gcUsedKeyAmt.put(gcTender, GrandLinestotal);

											 GrandLinestotal=0.00;
												}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
												
											}
																				

										 }

										
									
										
									}
									
								 }

																					 
						 }
						 
						 else{
								//System.out.println("Grand Total"+GrandLinestotal);

								//System.out.println("In third payment Loop");


							 //String strVoucherUser=Double.toString(dVoucherAmountLeft);
							// String strgiftCardUser=Double.toString(dGiftCardAmountLeft);
							// double ccUsed=GrandLinestotal-(dVoucherAmountLeft+dGiftCardAmountLeft);
							// strccUsed=Double.toString(ccUsed);

							 NodeList paymentMenthodList2 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList2.getLength(); k++) {
									Element paymentMethodElement = (Element) paymentMenthodList2.item(k);
									String paymentType=paymentMethodElement.getAttribute("PaymentType");
								
									if(paymentType.equalsIgnoreCase("VOUCHERS")){
										//System.out.println("Inside Vocuhers");

										String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
										for(String Key : vcLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(vcTender))
											{
												if(vcUsedKeyAmt.containsKey(vcTender)){
													//System.out.println("Inside if loop");

													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcUsedKeyAmt.get(Key)));
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));
}
												
												else{
													double vcAmtKey1=vcLeftTenderAmt.get(Key);
												
												//System.out.println("vc amount in third loop"+vcAmtKey1);

												//System.out.println("grand total"+GrandLinestotal);

											 
											if(vcAmtKey1<=GrandLinestotal){
												
											//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcAmtKey1));
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcAmtKey1));

											//System.out.println("vc maxchargelimit total"+vcAmtKey1);

											GrandLinestotal=GrandLinestotal-vcAmtKey1;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey1),"0.00","0.00","0.00",Key);
												vcUsedKeyAmt.put(vcTender, vcAmtKey1);

											}
											else{
												
												if(GrandLinestotal!=0.00){
													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(GrandLinestotal));
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));

													//System.out.println("vc maxchargelimit total"+vcAmtKey1);

												//	double vcamount=vcAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
														vcUsedKeyAmt.put(vcTender, GrandLinestotal);

													GrandLinestotal=0.00;
													}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
												
											}
																				

										 }

																				
									}
									if(paymentType.equalsIgnoreCase("GIFT_CARD")){
										//System.out.println("grand total"+GrandLinestotal);
										//System.out.println("Inside Gift Card");


										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String gcTender=paymentMethodElement.getAttribute("SvcNo");
										for(String Key : gcLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(gcTender))
											{
												if(gcUsedKeyAmt.containsKey(gcTender)){
													//System.out.println("Inside if loop");

													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(gcUsedKeyAmt.get(Key)));
												
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcUsedKeyAmt.get(Key)));
}
												else
													{
													double gcAmtKey1=gcLeftTenderAmt.get(Key);
													
											if(gcAmtKey1<=GrandLinestotal){
												//System.out.println("gc amount in third loop"+gcAmtKey1);

												//System.out.println("grand total"+GrandLinestotal);
												
											//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(gcAmtKey1));
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcAmtKey1));

											//System.out.println("gc max limit in third loop"+gcAmtKey1);

											GrandLinestotal=GrandLinestotal-gcAmtKey1;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(gcAmtKey1),"0.00","0.00",Key);
												gcUsedKeyAmt.put(gcTender, gcAmtKey1);

											}
											else{
												
												if(GrandLinestotal!=0.00){
													//paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(GrandLinestotal));
													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));

													//System.out.println("gc max limit in third loop"+GrandLinestotal);

													
													//double gcamount=gcAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(GrandLinestotal),"0.00","0.00",Key);
														gcUsedKeyAmt.put(gcTender, GrandLinestotal);

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
													}
												
											}
																				

										 }

																				
									}
									
									if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
										//System.out.println("Inside Credit Card");

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String ccTender=paymentMethodElement.getAttribute("CreditCardNo");
										for(String Key : ccLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(ccTender))
											{
												if(ccUsedKeyAmt.containsKey(ccTender)){
													//System.out.println("Inside if loop");

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ccUsedKeyAmt.get(Key)));
												}
												else{ 
													double ccAmtKey1=ccLeftTenderAmt.get(Key);
												
											if(ccAmtKey1<=GrandLinestotal){
											//System.out.println("cc amount in third loop"+ccAmtKey1);
											//System.out.println("grand total"+GrandLinestotal);
											paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(ccAmtKey1));
											//System.out.println("cc max limit in third loop"+ccAmtKey1);
											GrandLinestotal=GrandLinestotal-ccAmtKey1;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00",Double.toString(ccAmtKey1),"0.00",Key);
												ccUsedKeyAmt.put(ccTender, ccAmtKey1);

											}
											else{
												if(GrandLinestotal!=0.00){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
											//System.out.println("cc max limit in third loop"+ccAmtKey1);

											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00",Double.toString(GrandLinestotal),"0.00",Key);
											ccUsedKeyAmt.put(ccTender, GrandLinestotal);

											//double ccamount=ccAmtKey-GrandLinestotal;

											GrandLinestotal=0.00;
												}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
												
											}
																				

										 }

																				
									}
									if(paymentType.equalsIgnoreCase("PAYPAL")){
										//System.out.println("Inside PayPal Card");

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String ppTender=paymentMethodElement.getAttribute("PaymentReference1");
										for(String Key : ppLeftTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(ppTender))
											{
												if(ppUsedKeyAmt.containsKey(ppTender)){
													//System.out.println("Inside if loop");

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ppUsedKeyAmt.get(Key)));
												}
												else{ 
													double ppAmtKey1=ppLeftTenderAmt.get(Key);
												
											if(ppAmtKey1<=GrandLinestotal){
											//System.out.println("pp amount in third loop"+ppAmtKey1);
											//System.out.println("grand total"+GrandLinestotal);
											paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(ppAmtKey1));
											//System.out.println("pp max limit in third loop"+ppAmtKey1);
											GrandLinestotal=GrandLinestotal-ppAmtKey1;
											 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00","0.00",Double.toString(ppAmtKey1),Key);
												ppUsedKeyAmt.put(ppTender, ppAmtKey1);

											}
											else{
												if(GrandLinestotal!=0.00){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
											//System.out.println("pp max limit in third loop"+ppAmtKey1);

											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00","0.00",Double.toString(GrandLinestotal),Key);
											ppUsedKeyAmt.put(ppTender, GrandLinestotal);

											//double ccamount=ccAmtKey-GrandLinestotal;

											GrandLinestotal=0.00;
												}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
												
											}
																				

										 }

																				
									}
									
								 }
						 }
							
				}
				else{
					
					//System.out.println("In third Loop");

					double GrandLinestotal=0.00;
					double dCreditCardAmount=0.00;
					double dVoucherAmount=0.00;
					double dGiftCardAmount=0.00;
					double totalListedCCamount=0.00;
					double totalListedGCamount=0.00;
					double totalListedVCamount=0.00;
//For PayPal
					double dPayPalAmount=0.00;
					double totalListedPPamount=0.00;

					
					 env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,"global/template/api/ESOrderDetailViewOutputTemplate.xml");
			         api = YIFClientFactory.getInstance().getLocalApi();
			        outputDocLineList = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,inXML);	
			         env.clearApiTemplates();
			         
			         NodeList linePriceInfo = outputDocLineList.getElementsByTagName("LinePriceInfo");
						for (int i = 0; i < linePriceInfo.getLength(); i++) {
						Element linePriceInfoEle = (Element) linePriceInfo.item(i);
						String lineTotal=linePriceInfoEle.getAttribute("LineTotal");
						//System.out.println("In GrandLinestotal Total" + GrandLinestotal);

						//System.out.println("In line Total" + lineTotal);

						double intLineTotal=Double.parseDouble(lineTotal);
						GrandLinestotal=GrandLinestotal+intLineTotal;
						 GrandOrderTotalO=df.format(GrandLinestotal);
							//System.out.println("In grandy total" + GrandOrderTotalO);
							double RealGrandtotal=GrandLinestotal;
							 NodeList overallTotals = outputDocLineList.getElementsByTagName("OverallTotals");
							 for (int n = 0; n < overallTotals.getLength(); n++) {
							Element overallTotalsElement = (Element) overallTotals.item(n);
							overallTotalsElement.setAttribute("GrandTotal",GrandOrderTotalO);
							}

						}
						
						 NodeList paymentMenthodList = outputDocLineList.getElementsByTagName("PaymentMethod");
						 for (int k = 0; k < paymentMenthodList.getLength(); k++) {
							Element paymentMethodElement = (Element) paymentMenthodList.item(k);
							String paymentType=paymentMethodElement.getAttribute("PaymentType");
							if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
								
								String ccTender=paymentMethodElement.getAttribute("CreditCardNo");
																
								if(!ccListedTenderAmt.containsKey(ccTender)){
															
									dCreditCardAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
									totalListedCCamount=dCreditCardAmount+totalListedCCamount;
									//System.out.println("CC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
									ccListedTenderAmt.put(ccTender, dCreditCardAmount);
								}
								
								

							
							}
                                 if(paymentType.equalsIgnoreCase("GIFT_CARD")){
								
								String gcTender=paymentMethodElement.getAttribute("SvcNo");
																
								if(!gcListedTenderAmt.containsKey(gcTender)){
															
									dGiftCardAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
									totalListedGCamount=dGiftCardAmount+totalListedGCamount;
									//System.out.println("GC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
									gcListedTenderAmt.put(gcTender, dGiftCardAmount);
								}


							}
                                 if(paymentType.equalsIgnoreCase("PAYPAL")){
     								
     								String ppTender=paymentMethodElement.getAttribute("PaymentReference1");
     																
     								if(!ppListedTenderAmt.containsKey(ppTender)){
     															
     									dPayPalAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
     									totalListedPPamount=dPayPalAmount+totalListedPPamount;
     									//System.out.println("GC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
     									ppListedTenderAmt.put(ppTender, dPayPalAmount);
     								}


     							}
                      if(paymentType.equalsIgnoreCase("VOUCHERS")){
	
	                    String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
									
	                     if(!vcListedTenderAmt.containsKey(vcTender)){
								
	                	dVoucherAmount=Double.parseDouble(paymentMethodElement.getAttribute("MaxChargeLimit"));
	                   	totalListedVCamount=dVoucherAmount+totalListedVCamount;
		               //System.out.println("VC MC " + (paymentMethodElement.getAttribute("MaxChargeLimit")));
		                 vcListedTenderAmt.put(vcTender, dVoucherAmount);
	}

							}
										 
						 }
							
						 if(log.isDebugEnabled()){
							 log.debug("vc amount" + totalListedVCamount);
							 log.debug("gc amount" + totalListedGCamount);
							 log.debug("cc amount" + totalListedCCamount);
							 log.debug("pp amount" + totalListedPPamount);
						 }

							Double totalListedVcGcAmt=totalListedVCamount+totalListedGCamount;

						 
						 if(totalListedVCamount>=GrandLinestotal){
								//System.out.println("In first payment Loop");
	 
							 
							 //double voucherUser=GrandLinestotal-dVoucherAmountLeft;
							 String strVoucherUser=Double.toString(GrandLinestotal);
							 NodeList paymentMenthodList1 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList1.getLength(); k++) {
								Element paymentMethodElement = (Element) paymentMenthodList1.item(k);
								String paymentType=paymentMethodElement.getAttribute("PaymentType");
								if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								if(paymentType.equalsIgnoreCase("PAYPAL")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								
								if(paymentType.equalsIgnoreCase("VOUCHERS")){
									String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
									//System.out.println("Vc tender is "+vcTender);

									for(String Key : vcListedTenderAmt.keySet()){
										//System.out.println("Key tender is "+Key);

										//System.out.println("Inside if voucher loop");

										if(Key.equalsIgnoreCase(vcTender)){
											//System.out.println("Inside if voucher used loop");

											if(vcUsedKeyAmt.containsKey(vcTender)){
											//System.out.println("Inside if voucher loop");
											//System.out.println("Vocher Used amt "+vcUsedKeyAmt.get(Key));

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));
										}
										else{
										 double vcAmtKey=vcListedTenderAmt.get(Key);
											//System.out.println("vcAmtKey is "+vcAmtKey);
											//System.out.println("GrandLinetotal"+GrandLinestotal);

										if(vcAmtKey<=GrandLinestotal){
											//System.out.println("Inside if 2 loop");
											//System.out.println("grandLine Total" +GrandLinestotal);

										paymentMethodElement.setAttribute("MaxChargeLimit",Double.toString(vcAmtKey));
										GrandLinestotal=GrandLinestotal-vcAmtKey;
										CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey),"0.00","0.00","0.00",Key);
										vcUsedKeyAmt.put(vcTender, vcAmtKey);
										
										}
										else{
											
											if(GrandLinestotal!=0.00){
												//System.out.println("Inside if 3 loop");
												//System.out.println("Grand Line Total"+GrandLinestotal);

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
												//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

												//double ccamount=ccAmtKey-GrandLinestotal;
												 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
													vcUsedKeyAmt.put(vcTender, GrandLinestotal);
													//System.out.println("ccTender"+vcTender+"GrandLinestotal is "+vcUsedKeyAmt.get(vcTender));

												GrandLinestotal=0.00;
													}
											else{
												paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

											}
										}
										}
										}
											
										
																			

									 
									}

									
								}
								if(paymentType.equalsIgnoreCase("GIFT_CARD")){
									paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
									
								}
								
							 }
						 }
						 else if(totalListedVcGcAmt >= GrandLinestotal) {
								//System.out.println("In second payment Loop");

							 //String strVoucherUser=Double.toString(dVoucherAmountLeft);
							 //double gcUsed=GrandLinestotal-dVoucherAmountLeft;
							 //String strgiftCardUser=Double.toString(dGiftCardAmountLeft);
							 
							 NodeList paymentMenthodList2 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList2.getLength(); k++) {
									Element paymentMethodElement = (Element) paymentMenthodList2.item(k);
									String paymentType=paymentMethodElement.getAttribute("PaymentType");
									if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
										paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
										
									}
									if(paymentType.equalsIgnoreCase("PAYPAL")){
										paymentMethodElement.setAttribute("MaxChargeLimit","0.00");
										
									}
									if(paymentType.equalsIgnoreCase("VOUCHERS")){

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String vcTender=paymentMethodElement.getAttribute("PaymentReference1");

										for(String Key : vcListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(vcTender)){

												if(vcUsedKeyAmt.containsKey(vcTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));
											}
											else{
											 double vcAmtKey=vcListedTenderAmt.get(Key);
											if(vcAmtKey<=GrandLinestotal){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcAmtKey));
											GrandLinestotal=GrandLinestotal-vcAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey),"0.00","0.00","0.00",Key);
											vcUsedKeyAmt.put(vcTender, vcAmtKey);
											
											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
														vcUsedKeyAmt.put(vcTender, GrandLinestotal);
														//System.out.println("ccTender"+vcTender+"GrandLinestotal is "+vcUsedKeyAmt.get(vcTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}

										
									
										
									}
									if(paymentType.equalsIgnoreCase("GIFT_CARD")){

										String gcTender=paymentMethodElement.getAttribute("SvcNo");

										for(String Key : gcListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(gcTender)){

												if(gcUsedKeyAmt.containsKey(gcTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcUsedKeyAmt.get(Key)));
											}
											else{
											 double gcAmtKey=gcListedTenderAmt.get(Key);
											if(gcAmtKey<=GrandLinestotal){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcAmtKey));
											GrandLinestotal=GrandLinestotal-gcAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(gcAmtKey),"0.00","0.00",Key);
											gcUsedKeyAmt.put(gcTender, gcAmtKey);
											
											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(GrandLinestotal),"0.00","0.00",Key);
														gcUsedKeyAmt.put(gcTender, GrandLinestotal);
														//System.out.println("ccTender"+gcTender+"GrandLinestotal is "+gcUsedKeyAmt.get(gcTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}
										
									}
									
								 }

																					 
						 }
						 
						 else{
							 
								//System.out.println("In third payment Loop");


							 //String strVoucherUser=Double.toString(dVoucherAmountLeft);
							// String strgiftCardUser=Double.toString(dGiftCardAmountLeft);
							// double ccUsed=GrandLinestotal-(dVoucherAmountLeft+dGiftCardAmountLeft);
							// strccUsed=Double.toString(ccUsed);

							 NodeList paymentMenthodList2 = outputDocLineList.getElementsByTagName("PaymentMethod");
							 for (int k = 0; k < paymentMenthodList2.getLength(); k++) {
									Element paymentMethodElement = (Element) paymentMenthodList2.item(k);
									String paymentType=paymentMethodElement.getAttribute("PaymentType");
								
									if(paymentType.equalsIgnoreCase("VOUCHERS")){
										String vcTender=paymentMethodElement.getAttribute("PaymentReference1");
										//System.out.println("Inside voucher loop");

										for(String Key : vcListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(vcTender)){

												if(vcUsedKeyAmt.containsKey(vcTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcUsedKeyAmt.get(Key)));
											}
											else{
											 double vcAmtKey=vcListedTenderAmt.get(Key);
												//System.out.println("vc amt"+ vcAmtKey);
												//System.out.println("grand amt"+ GrandLinestotal);

											if(vcAmtKey<=GrandLinestotal){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(vcAmtKey));
											GrandLinestotal=GrandLinestotal-vcAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(vcAmtKey),"0.00","0.00","0.00",Key);
											vcUsedKeyAmt.put(vcTender, vcAmtKey);
											//System.out.println("Inside Voucher Card");

											//System.out.println("gRANDLINE tOTAL IS "+GrandLinestotal);

											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,Double.toString(GrandLinestotal),"0.00","0.00","0.00",Key);
														vcUsedKeyAmt.put(vcTender, GrandLinestotal);
														//System.out.println("ccTender"+vcTender+"GrandLinestotal is "+vcUsedKeyAmt.get(vcTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}

																				
									}
									if(paymentType.equalsIgnoreCase("GIFT_CARD")){
										// System.out.println("Inside Gift Card");

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String gcTender=paymentMethodElement.getAttribute("SvcNo");

										for(String Key : gcListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(gcTender)){

												if(gcUsedKeyAmt.containsKey(gcTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcUsedKeyAmt.get(Key)));
											}
											else{
												
											 double gcAmtKey=gcListedTenderAmt.get(Key);
											 //System.out.println("gc amt"+ gcAmtKey);
												//System.out.println("grand amt"+ GrandLinestotal);
											if(gcAmtKey<=GrandLinestotal){
												
											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(gcAmtKey));
											GrandLinestotal=GrandLinestotal-gcAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(gcAmtKey),"0.00","0.00",Key);
											gcUsedKeyAmt.put(gcTender, gcAmtKey);
											//System.out.println("Inside Gift Card");

											//System.out.println("gRANDLINE tOTAL IS "+GrandLinestotal);

											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00",Double.toString(GrandLinestotal),"0.00","0.00",Key);
														gcUsedKeyAmt.put(gcTender, GrandLinestotal);
														//System.out.println("ccTender"+gcTender+"GrandLinestotal is "+gcUsedKeyAmt.get(gcTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}
																				
									}

									if(paymentType.equalsIgnoreCase("CREDIT_CARD")){
										//System.out.println("Inside Credit Card");

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String ccTender=paymentMethodElement.getAttribute("CreditCardNo");

										for(String Key : ccListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(ccTender)){

												if(ccUsedKeyAmt.containsKey(ccTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ccUsedKeyAmt.get(Key)));
											}
											else{
											 double ccAmtKey=ccListedTenderAmt.get(Key);
											 //System.out.println("gc amt"+ ccAmtKey);
												//System.out.println("grand amt"+ GrandLinestotal);
											if(ccAmtKey<=GrandLinestotal){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ccAmtKey));
											GrandLinestotal=GrandLinestotal-ccAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00",Double.toString(ccAmtKey),"0.00",Key);
											ccUsedKeyAmt.put(ccTender, ccAmtKey);
											//System.out.println("gRANDLINE tOTAL IS "+GrandLinestotal);

											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00",Double.toString(GrandLinestotal),"0.00",Key);
														ccUsedKeyAmt.put(ccTender, GrandLinestotal);
														//System.out.println("ccTender"+ccTender+"GrandLinestotal is "+ccUsedKeyAmt.get(ccTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}
																				
									}
									if(paymentType.equalsIgnoreCase("PAYPAL")){
										//System.out.println("Inside PayPal");

										//paymentMethodElement.setAttribute("MaxChargeLimit",strVoucherUser);
										String ppTender=paymentMethodElement.getAttribute("PaymentReference1");

										for(String Key : ppListedTenderAmt.keySet()){
											if(Key.equalsIgnoreCase(ppTender)){

												if(ppUsedKeyAmt.containsKey(ppTender)){
												//System.out.println("Inside if loop");

												paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ppUsedKeyAmt.get(Key)));
											}
											else{
											 double ppAmtKey=ppListedTenderAmt.get(Key);
											 //System.out.println("pp amt"+ ppAmtKey);
												//System.out.println("grand amt"+ GrandLinestotal);
											if(ppAmtKey<=GrandLinestotal){

											paymentMethodElement.setAttribute("MaxChargeLimit",df.format(ppAmtKey));
											GrandLinestotal=GrandLinestotal-ppAmtKey;
											CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00","0.00",Double.toString(ppAmtKey),Key);
											ppUsedKeyAmt.put(ppTender, ppAmtKey);
											//System.out.println("gRANDLINE tOTAL IS "+GrandLinestotal);

											}
											else{
												
												if(GrandLinestotal!=0.00){

													paymentMethodElement.setAttribute("MaxChargeLimit",df.format(GrandLinestotal));
													//System.out.println("Maxcharge total"+Double.toString(GrandLinestotal));

													//double ccamount=ccAmtKey-GrandLinestotal;
													 CreateCustomerPORecords(env,orderHeaderKey,customerPONo,"0.00","0.00","0.00",Double.toString(GrandLinestotal),Key);
														ppUsedKeyAmt.put(ppTender, GrandLinestotal);
														//System.out.println("ppTender"+ppTender+"GrandLinestotal is "+ppUsedKeyAmt.get(ppTender));

													GrandLinestotal=0.00;
														}
												else{
													paymentMethodElement.setAttribute("MaxChargeLimit","0.00");

												}
											}
											}
											}
												
											
																				

										 
										}
																				
									}
									
								 }
						 }
						 
				}
				
			}
			}
			else {
				 env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,"global/template/api/ESOrderDetailViewOutputTemplate.xml");
		         api = YIFClientFactory.getInstance().getLocalApi();
		          outputDocLineList = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,inXML);	
		         env.clearApiTemplates();
			}
			
			/**else {
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LINE_LIST,"global/template/api/ESOrderDetailViewOutputTemplate.xml");
		         api = YIFClientFactory.getInstance().getLocalApi();
		          outputDocLineList = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,inXML);	
		          NodeList orderList = outputDocLineList.getElementsByTagName("Order");
					 for (int i = 0; i < orderList.getLength(); i++) {
					Element orderElement = (Element) orderList.item(i);
					orderElement.setAttribute("OrderType","POS");
					 } 
		         
		         
			} **/
		}
		catch(Exception Ex){
			Ex.printStackTrace();
		}
		
		// ARS-259
		// If //OrderLine/PersonInfoShipTo is not present, copy it from Order/PersonInfoShipTo
		try{

			NodeList nlOrderLine = outputDocLineList.getElementsByTagName("OrderLine");
			for(int i = 0; i < nlOrderLine.getLength(); i++){

				Element eleOrderLine = (Element) nlOrderLine.item(i);
				Element eleLinePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, "PersonInfoShipTo");
				Element elePersonInfoShipTo = SCXmlUtil.getXpathElement(
						eleOrderLine, "//Order/PersonInfoShipTo");

				if(YFCObject.isVoid(eleLinePersonInfoShipTo)){

					if(!YFCObject.isVoid(elePersonInfoShipTo)){

						SCXmlUtil.importElement(eleOrderLine, elePersonInfoShipTo);
						SCXmlUtil.removeNode(elePersonInfoShipTo);
					}
				}else{

					if(!YFCObject.isVoid(elePersonInfoShipTo)){

						SCXmlUtil.removeNode(elePersonInfoShipTo);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return outputDocLineList;
	}
		 else {
				throw new YFSException(
						"EXTN_ERROR",
						"INVALID_CUTOMER_PO_NO",
				"customerPONo cannot be blank");
			}
	}
                 
	public void CreateCustomerPORecords(YFSEnvironment env,String strOrderHeaderKey,String CustomerPoNo,String voucherAmt
			,String gcAmt, String ccAmt,String ppamt,String tenderNo)
	throws Exception {

      Document getCustPoRecords = XMLUtil.createDocument("CustomerPoNoRecords");
      Element eleOrder = getCustPoRecords.getDocumentElement();
      eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
      eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, CustomerPoNo);
      eleOrder.setAttribute("VouchersAmt",voucherAmt);
      eleOrder.setAttribute("GiftCardAmt",gcAmt);
      eleOrder.setAttribute("CreaditCardAmt",ccAmt);
      eleOrder.setAttribute("PayPalAmt",ppamt);
      eleOrder.setAttribute("TenderNo",tenderNo);

      api = YIFClientFactory.getInstance().getApi();
      Document ouctDocCustPoRecords = api.executeFlow(env,"createCustomerPoNoRecords", getCustPoRecords);

}
}
		
				 



			 

               

			

	      		
			
				
		