package com.vsi.oms.userexit;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.riskified.RiskifiedClient;
import com.riskified.RiskifiedError;
import com.riskified.models.Address;
import com.riskified.models.AuthorizationError;
import com.riskified.models.ChargeFreePaymentDetails;
import com.riskified.models.CheckoutDeniedOrder;
import com.riskified.models.CheckoutOrder;
import com.riskified.models.ClientDetails;
import com.riskified.models.CreditCardPaymentDetails;
import com.riskified.models.Customer;
import com.riskified.models.DiscountCode;
import com.riskified.models.IPaymentDetails;
import com.riskified.models.LineItem;
import com.riskified.models.Order;
import com.riskified.models.PaypalPaymentDetails;
import com.riskified.models.Response;
import com.riskified.models.ShippingLine;
import com.riskified.validations.FieldBadFormatException;
import com.sterlingcommerce.baseutil.SCXmlUtil;
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

public class VSICheckOutDeniedToRiskified {
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckOutDeniedToRiskified.class);
	YIFApi api;
	public void checkOutDeniedRequest(YFSEnvironment env, Document inXML){
		String strOrderHeaderKey = null;
		String strAQCode=null;
		String strAVSCode=null;
		String strCVVCode=null;
		log.debug("checkOutDeniedRequest input is : "+XMLUtil.getXMLString(inXML));
		if(inXML != null){
			strOrderHeaderKey = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			 strAQCode = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_AQR_CODE);
			 strAVSCode = inXML.getDocumentElement().getAttribute("AVSResult");
			 strCVVCode = inXML.getDocumentElement().getAttribute("CVVResult");
		}


		try {
			Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrderListSOForHold.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);

			log.debug("*** NOW PRINTING getOrderListOp "+XMLUtil.getXMLString(getOrderListOp));

			if(getOrderListOp != null){
				Element eleOrderOutput = (Element) getOrderListOp.getDocumentElement().getElementsByTagName("Order").item(0);
				if(!YFCObject.isVoid(eleOrderOutput)){
					
					ArrayList<Element> listCheckoutFlag;
					listCheckoutFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_CHECKOUT, VSIConstants.ATTR_DEFAULT);
					if(!listCheckoutFlag.isEmpty()){
						Element eleCommonCode=listCheckoutFlag.get(0);
						String strCheckoutFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
						if(!YFCCommon.isStringVoid(strCheckoutFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strCheckoutFlag)){
							//construct Riskified request

							CheckoutDeniedOrder checkoutOrder =constructRiskifiedRequest(getOrderListOp,env,strAQCode,strAVSCode,strCVVCode);

							String chargeType="Checkout Denied";
							Gson gson = new Gson();
							String strOrder = com.riskified.JSONFormater.toJson(checkoutOrder);
							
							RiskifiedClient client = new RiskifiedClient();
							
						Response res = client.checkoutDeniedOrder(checkoutOrder);

							log.debug("Riskified Request: "
									+ strOrder);
							storeFraudRequestandResponse(env,strOrderHeaderKey,strOrder,chargeType);
						}
					}
				}
			}


		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void storeFraudRequestandResponse(YFSEnvironment env,String ohk,String request, String chargeType) 
			throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException{
		Document doc = XMLUtil.createDocument("PaymentRecords");
		Element elePayRecrds = doc.getDocumentElement();
		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				ohk);
		elePayRecrds.setAttribute("Record", request);
		elePayRecrds.setAttribute("ChargeType", chargeType);
		YIFApi api;
		api = YIFClientFactory.getInstance().getApi();
		api.executeFlow(env,"VSIPaymentRecords", doc);
	}

	public CheckoutDeniedOrder constructRiskifiedRequest(Document getOrderListOp,
			YFSEnvironment env, String strAQCode, String strAVSCode, String strCVVCode) throws ParseException, RiskifiedError, IOException, FieldBadFormatException, TransformerException {

		Element eleOrderList=getOrderListOp.getDocumentElement();
		Element eleOrder=SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
		String strEntryType=eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
		Element eleOrderExtn=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_EXTN);
		String strExtnSubscriptionOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_SUBSCRIPTION_ORDER);
		String strExtnOriginalADPOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_ORIGINAL_ADP_ORDER);
		String strExtnBrowserIP = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_BROWSER_IP);
		String strExtnCheckoutId = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_CHECKOUTID);
		String strExtnAurusToken = eleOrderExtn.getAttribute(VSIConstants.EXTN_AURUS_TOKEN);
		
		String strExtnCustomerCreatets = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_CUSTOMER_CREATETS);
		String strExtnAcceptUser = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_ACCEPT_USER);
		String strExtnUserAgent = eleOrderExtn.getAttribute(VSIConstants.ATTR_USER_AGENT);
		String strCustomerFirstName=eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_FIRST_NAME);
		String strCustomerLastName=eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_LAST_NAME);
		String strBillToID=eleOrder.getAttribute(VSIConstants.ATTR_BILL_TO_ID);
		String createts=eleOrder.getAttribute(VSIConstants.ATTR_CREATETS);
		//OMS-2405 -- Start
		String modifyts=eleOrder.getAttribute(VSIConstants.ATTR_MODIFYTS);
		//OMS-2405 -- End
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-hh",Locale.US); 

		//order
		CheckoutDeniedOrder checkoutOrder = new CheckoutDeniedOrder(eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO));
		//CheckoutOrder checkoutOrder = new CheckoutOrder(); 

		List<LineItem> lineItems = new ArrayList<LineItem>();
		List<DiscountCode> Discount = new ArrayList<DiscountCode>();
		checkoutOrder.setId(eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO));
		checkoutOrder.setEmail(eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID));
		
		//OMS-2405 -- Start
		if(!YFCCommon.isVoid(strEntryType) && VSIConstants.ENTRYTYPE_CC.equals(strEntryType)){
			checkoutOrder.setCreatedAt(dt.parse(modifyts));
			checkoutOrder.setUpdatedAt(dt.parse(modifyts));
		}else{
			checkoutOrder.setCreatedAt(dt.parse(createts));
			checkoutOrder.setUpdatedAt(dt.parse(createts));
		}
		//OMS-2405 -- End
		
		checkoutOrder.setCurrency(VSIConstants.ATTR_CURRENCY);		
		if(!YFCObject.isVoid(strEntryType)&&!YFCObject.isVoid(strExtnCheckoutId)&&!((VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType))){
			checkoutOrder.setCheckoutId(strExtnCheckoutId);
		}
		
		//Changes for OMS-2192 -- Start
		String strExtnReorder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_RE_ORDER);		

		if(!YFCCommon.isVoid(strExtnReorder) && VSIConstants.FLAG_Y.equals(strExtnReorder)){
			checkoutOrder.setSource(VSIConstants.ATTR_REORDER);
		}
		//Changes for OMS-2192 -- End		
		else if(!YFCObject.isVoid(strEntryType)&&(VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType)){
			checkoutOrder.setSource(VSIConstants.ATTR_PHONE);
			checkoutOrder.setCheckoutId(eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO));
		}
		else if(!YFCObject.isVoid(strExtnSubscriptionOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnSubscriptionOrder)){
			checkoutOrder.setSource(VSIConstants.ATTR_SUBSCRIPTION);
		}
		else if(!YFCObject.isVoid(strExtnOriginalADPOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnOriginalADPOrder)){
			checkoutOrder.setSource(VSIConstants.ATTR_INITIAL_SUBSCRIPTION);
		}
		else{
			checkoutOrder.setSource(VSIConstants.ATTR_WEB);
		}
		String strAcceptUser=null;
		String strUserAgent=null;
		String strCustomerCreatets=null;
		if(!YFCObject.isVoid(strExtnAcceptUser)){
			 strAcceptUser=strExtnAcceptUser;
		}
		else{
			 strAcceptUser="en-CA";
		}
		
		if(!YFCObject.isVoid(strExtnUserAgent)){
			 strUserAgent=strExtnUserAgent;
		}
		else{
			strUserAgent="Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
		}
		if(!YFCObject.isVoid(strExtnCustomerCreatets)){
			strCustomerCreatets=strExtnCustomerCreatets;
		}
		else{
			strCustomerCreatets=createts;
		}
			
		Element eleOverallTotals=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_OVERALL_TOTALS);
		String strGrandTotal = eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_TOTAL);
		String strGrandDiscount = eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_DISCOUNT);
		String strGrandShippingCharges=eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_SHIPPING_CHARGE);
		double dGrandTotal=0.00;
		double dGrandDiscount=0.00;
		double dGrandShippingCharges=0.00;
		if(!YFCObject.isVoid(strGrandTotal)){
		 dGrandTotal = Double.parseDouble(strGrandTotal);
		}
		if(!YFCObject.isVoid(strGrandDiscount)){
		 dGrandDiscount = Double.parseDouble(strGrandDiscount);
		}
		if(!YFCObject.isVoid(strGrandShippingCharges)){
		 dGrandShippingCharges = Double.parseDouble(strGrandShippingCharges);
		}

		checkoutOrder.setTotalPrice(dGrandTotal);
		checkoutOrder.setTotalDiscounts(dGrandDiscount);

		if(eleOrderExtn!=null &&!VSIConstants.ENTRYTYPE_CC.equalsIgnoreCase(strEntryType)){
			String extnSessionId=eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_SESSION_ID);
			if(!YFCCommon.isVoid(strExtnBrowserIP)){
				checkoutOrder.setBrowserIp(strExtnBrowserIP);
			}
			else{
				checkoutOrder.setBrowserIp("50.49.141.248");
			}
			if(!YFCCommon.isVoid(extnSessionId)){
			checkoutOrder.setCartToken(extnSessionId);
			}
		}

		checkoutOrder.setReferringSite("null");

		// LineItems
		NodeList nlcheckoutOrderLines = eleOrder
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		Element elecheckoutOrderLine = (Element) getOrderListOp.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		int noOfLines = nlcheckoutOrderLines.getLength();
		String strSCAC=null;
		String strLineType=null;
		int k=1;
		for (int i = 0; i < noOfLines; i++) {
			Element eleOrderLine = (Element) nlcheckoutOrderLines.item(i);
			strLineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);			
			strSCAC = eleOrderLine.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE);

			Element eleItem = (Element) eleOrderLine.getElementsByTagName(
					VSIConstants.ELE_ITEM).item(0);

			Element eleItemDetails = (Element) eleOrderLine.getElementsByTagName(
					VSIConstants.ELE_ITEM_DETAILS).item(0);
			Element eleItemExtn = (Element) eleItemDetails.getElementsByTagName(
					VSIConstants.ELE_EXTN).item(0);
			//OMS-968 : End
			Element elelinePrice = (Element) eleOrderLine.getElementsByTagName(
					VSIConstants.ELE_LINE_PRICE).item(0);


			String strItemDesc= eleItem.getAttribute(VSIConstants.ATTR_ITEM_DESC);
			String strSkuId= eleItemExtn.getAttribute(VSIConstants.EXTN_ACT_SKU_ID);
			String strItemId= eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
			String strBrandTitle= eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE);
			String strLineTotal=elelinePrice.getAttribute(VSIConstants.ATTR_UNIT_PRICE);
			String strQty=eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
			String strOrderLineKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			Element eleFirstDelayDate = XMLUtil.getElementByXPath(getOrderListOp,
					"OrderList/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_FIRST_PROMISE_DATE']");


			int dQty=Integer.parseInt(strQty);

			if(dQty!=0.00){

				double dUnitPrice=Double.parseDouble(strLineTotal);
				//double dUnitPrice=dLineTotal/dQty;


				LineItem item = new LineItem(dUnitPrice, dQty, strItemDesc, strSkuId);
				item.setBrand(strBrandTitle);
				item.setSku(strItemId);
				item.setProductType("physical");
				item.setProperties(null);
				item.setTaxLines(null);
				String isGiftCardItem = eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_ITEM_TYPE);

				if (VSIConstants.GIFT_CARD.equals(isGiftCardItem)|| VSIConstants.GIFT_CARD_VAR.equals(isGiftCardItem)) 
				{
					String strsku="giftcard"+k;
					item.setSku(strsku);
					item.setRequiresShipping(true);
					k = k+1;
				}
				if(!YFCCommon.isVoid(eleFirstDelayDate)){
					String strActualDate=eleFirstDelayDate.getAttribute(VSIConstants.ATTR_ACTUAL_DATE);
					String strExpectedDate=eleFirstDelayDate.getAttribute(VSIConstants.ATTR_EXPECTED_DATE);
					String strShipmentDateFormat=null;
					if(!YFCCommon.isVoid(strActualDate)||!YFCCommon.isVoid(strExpectedDate)){
					if(!YFCCommon.isVoid(strActualDate)){
						 strShipmentDateFormat=strActualDate.substring(0, strActualDate.indexOf("T")).replaceAll("-", "");
					}
					else if(!YFCCommon.isVoid(strExpectedDate)){
						 strShipmentDateFormat=strExpectedDate.substring(0, strExpectedDate.indexOf("T")).replaceAll("-", "");
					}

						DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
						Date date = new Date();	
						String currentdate = dateFormat.format(date);
						Date dcurrentdate = dateFormat.parse(currentdate);
						 Calendar c = Calendar.getInstance();
						 c.setTime(dcurrentdate);
						 c.add(Calendar.DAY_OF_MONTH, 1);
						 Date currentDatePlusTwo = c.getTime();
						Date dstrShipmentDateFormat = dateFormat.parse(strShipmentDateFormat);
						if(dstrShipmentDateFormat.after(currentDatePlusTwo)){
							item.setSubCategory("backorder");
						}
					}
				}
				lineItems.add(item);

				//discount

				NodeList ndlLineCharges  = eleOrderLine.getElementsByTagName(VSIConstants.ELE_LINE_CHARGE);

				for(int j= 0; j < ndlLineCharges.getLength(); j++){
					Element eleLineCharge = (Element)ndlLineCharges.item(j);

					if(!YFCObject.isVoid(eleLineCharge)){
						String strChargeCategory = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
						if(VSIConstants.DISCOUNT.equalsIgnoreCase(strChargeCategory) || VSIConstants.DISCOUNT_CATEGORY_ADJ.equalsIgnoreCase(strChargeCategory)){
							String strChargeAmount = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
							String strChargeName = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
							double dstrChargeAmount=Double.parseDouble(strChargeAmount);
							Discount.add(new DiscountCode(dstrChargeAmount,strChargeName));
						}
					}
				}


			}
		}
		
		//Changes for OMS-2193 -- Start
		Element eleHeaderCharges = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_HEADER_CHARGES);
		NodeList ndlHeaderCharge = eleHeaderCharges.getElementsByTagName(VSIConstants.ELE_HEADER_CHARGE);
		for(int m= 0; m < ndlHeaderCharge.getLength(); m++){
					
			Element eleHeaderCharge = (Element)ndlHeaderCharge.item(m);
			if(!YFCCommon.isVoid(eleHeaderCharge)){
				String strHdrChrgCategory = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
				if(VSIConstants.DISCOUNT_CATEGORY_ADJ.equals(strHdrChrgCategory)){
					String strHdrChrgName = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
					String strHdrChrgAmnt = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
					double dHdrChrgAmnt = Double.parseDouble(strHdrChrgAmnt);
					Discount.add(new DiscountCode(dHdrChrgAmnt,strHdrChrgName));
				}
			}
					
		}
		//Changes for OMS-2193 -- End
				
		checkoutOrder.setLineItems(lineItems);
		checkoutOrder.setDiscountCodes(Discount);


		// ShippingLines
		if(VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType) || VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)){
			//checkoutOrder.setSource(VSIConstants.ATTR_WEB);
			checkoutOrder.setShippingLines(Arrays.asList(new ShippingLine(dGrandShippingCharges, VSIConstants.ATTR_STORE_PICK_UP)));

		}
		else{
			if(YFCCommon.isVoid(strSCAC)){
				strSCAC="STANDARD";
			}
			checkoutOrder.setShippingLines(Arrays.asList(new ShippingLine(dGrandShippingCharges, strSCAC)));
		}

		//PaymentDetails
		boolean bvoucher=false;
		boolean bcreditCard=false;
		 boolean bpaypal=false;
		 boolean bgcCard=false;
		NodeList nlPaymtMthd = eleOrder
				.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
		int pymthdCount = nlPaymtMthd.getLength();
		String strPaymentTechCode="null";
		List<ChargeFreePaymentDetails> chargefreePaymentDetails= new ArrayList<ChargeFreePaymentDetails>();
		for (int n = 0; n < pymthdCount; n++) {
			Element elePaymtMthd = (Element) nlPaymtMthd.item(n);
			if(YFCCommon.isStringVoid(strAVSCode)){
				strAVSCode=SCXmlUtil.getXpathAttribute(eleOrder, "Extn/TNSAuthRecordsList/TNSAuthRecords/@AvsGatewayCode");
			log.debug("AvsResp : "+ strAVSCode);
			}
			if(YFCCommon.isStringVoid(strCVVCode)){
			 strCVVCode=SCXmlUtil.getXpathAttribute(eleOrder, "Extn/TNSAuthRecordsList/TNSAuthRecords/@CVVResp");
			}
			if(YFCCommon.isStringVoid(strCVVCode)){
				strCVVCode="null";
			}
			if(YFCCommon.isStringVoid(strAVSCode)){
				strAVSCode="null";
			}
			/*if(!YFCCommon.isStringVoid(strAvsResp)){
				//Sample value for strAvsResp AvsResp-I8, fetch the value after -
				String[] arrAvsResp= strAvsResp.split("-");
				if(arrAvsResp.length==2){
					 strPaymentTechCode=arrAvsResp[1];
					
				}
			}*/
			if (elePaymtMthd != null) {

				String strPaymentType = elePaymtMthd
						.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
				String strCreditCardNumber=null;
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_CC)) {
					String strCreditCarType = elePaymtMthd
							.getAttribute(VSIConstants.ATTR_CREDIT_CARD_TYPE);
                   Element eleExtnPayment = SCXmlUtil.getChildElement(elePaymtMthd, VSIConstants.ELE_EXTN);
					
                   if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)&&!YFCCommon.isVoid(eleExtnPayment)&&!YFCCommon.isStringVoid(eleExtnPayment.getAttribute("ExtnAurusReferralNUM"))){
  					 strCreditCardNumber=eleExtnPayment.getAttribute("ExtnAurusReferralNUM");
  						
  					}
  					else{
  					 strCreditCardNumber = elePaymtMthd.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_2);
  					}
                      String creditCardBin=null;
                      String strCardNo=null;
                      if(!YFCCommon.isVoid(strCreditCardNumber) ){
  					 creditCardBin=strCreditCardNumber.substring(0, 6);
  					 strCardNo=strCreditCardNumber.substring(strCreditCardNumber.length()-4);
                      }
					String strTotalAuthorized = elePaymtMthd
							.getAttribute(VSIConstants.ATTR_TOTAL_AUTHORIZED);
					 bcreditCard=true;
					if(!YFCCommon.isVoid(strTotalAuthorized)&& Double.parseDouble(strTotalAuthorized)!=0.0){
						
						}
					if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)){
					checkoutOrder.setGateway("Aurus");
					}
					else{
						checkoutOrder.setGateway("TNS");
					}
					if(YFCCommon.isVoid(strAQCode)){
						strAQCode="null";
					}
					AuthorizationError authorizationError = new AuthorizationError(strAQCode,dt.parse(createts) ); 
					authorizationError.setMessage("");
					
					List<CreditCardPaymentDetails> lsCreditCardPaymentDetails = new ArrayList<CreditCardPaymentDetails>();
					CreditCardPaymentDetails cardDetails = new CreditCardPaymentDetails(creditCardBin, strAVSCode, strCVVCode, strCardNo, strCreditCarType);
					
					lsCreditCardPaymentDetails.add(cardDetails);
					cardDetails.setAuthorizationError(authorizationError);
					checkoutOrder.setPaymentDetails(Arrays.asList(cardDetails));
			
				}
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_PP)) {
					checkoutOrder.setGateway("Paypal");
					bpaypal=true;
					String strPayerEmail = elePaymtMthd.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_3);
					checkoutOrder.setPaymentDetails(Arrays.asList(new PaypalPaymentDetails(strPayerEmail, "verified", "unconfirmed", "Eligible")));
				}

				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_GC)) {

					String strTotalAuthorized = elePaymtMthd
							.getAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT);
					checkoutOrder.setGateway("giftcard");
					
					double dTotalAuthorized=Double.parseDouble(strTotalAuthorized);
					if(!YFCCommon.isVoid(dTotalAuthorized)&& dTotalAuthorized!=0.0){
						bgcCard=true;
					if(checkoutOrder.getChargeFreePaymentDetails() != null && VSIConstants.PAYMENT_MODE_GC == checkoutOrder.getChargeFreePaymentDetails().getGateway()){
						checkoutOrder.getChargeFreePaymentDetails().setAmount(checkoutOrder.getChargeFreePaymentDetails().getAmount()+ dTotalAuthorized);
					}else{
						ChargeFreePaymentDetails chargeFreePaymentDetails=new ChargeFreePaymentDetails(VSIConstants.PAYMENT_MODE_GC, dTotalAuthorized);
						chargefreePaymentDetails.add(chargeFreePaymentDetails);
						//checkoutOrder.setChargeFreePaymentDetails(chargeFreePaymentDetails);
					}
					}

				}
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_VOUCHERS)) {

					String strTotalAuthorized = elePaymtMthd
							.getAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT);
					checkoutOrder.setGateway("healthyawards");
					
					double dTotalAuthorized=Double.parseDouble(strTotalAuthorized);
					if(!YFCCommon.isVoid(dTotalAuthorized)&& dTotalAuthorized!=0.0){
						bvoucher=true;
					if(checkoutOrder.getChargeFreePaymentDetails() != null && VSIConstants.PAYMENT_MODE_VOUCHERS == checkoutOrder.getChargeFreePaymentDetails().getGateway()){
						checkoutOrder.getChargeFreePaymentDetails().setAmount(checkoutOrder.getChargeFreePaymentDetails().getAmount()+ dTotalAuthorized);
					}else{
						ChargeFreePaymentDetails chargeFreePaymentDetails=new ChargeFreePaymentDetails(VSIConstants.PAYMENT_MODE_VOUCHERS, dTotalAuthorized);
						chargefreePaymentDetails.add(chargeFreePaymentDetails);
						//checkoutOrder.setChargeFreePaymentDetails(chargeFreePaymentDetails);
					}
				}
				}
			}
		}
/*		Map<String, Object> m= new HashMap(); 
		m.put("charge_free_payment_details", chargefreePaymentDetails);
		
		checkoutOrder.setAdditionalData(m);*/
		if(chargefreePaymentDetails.size()>0) {
			ChargeFreePaymentDetails cfg=createChargeFreePaymentDetailsObj(chargefreePaymentDetails);
			checkoutOrder.setChargeFreePaymentDetails(cfg);
		}
		if(bcreditCard&&(bvoucher||bgcCard)){
			if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)){
				checkoutOrder.setGateway("Aurus");
				}
			else{
			checkoutOrder.setGateway("TNS");
			}
		}
		else if(bpaypal&&(bvoucher||bgcCard))
		{
			checkoutOrder.setGateway("paypal");
		}
		else if(bvoucher&&bgcCard){
			checkoutOrder.setGateway("giftcard");
		}
		else if(bvoucher){
			checkoutOrder.setGateway("healthyawards");
		}
		else if(bvoucher||bgcCard){
			checkoutOrder.setGateway("giftcard");
		}
		//BillingAddress
		Element eleBillToAdd = (Element) eleOrder.getElementsByTagName(
				VSIConstants.ELE_PERSON_INFO_BILL_TO).item(0);
		if (eleBillToAdd != null) {
			String strFirstName = eleBillToAdd.getAttribute(VSIConstants.ATTR_FIRST_NAME);
			String strLastname= eleBillToAdd.getAttribute(VSIConstants.ATTR_LAST_NAME);
			String address1 = eleBillToAdd.getAttribute(VSIConstants.ATTR_ADDRESS1);
			String address2 = eleBillToAdd.getAttribute(VSIConstants.ATTR_ADDRESS2);
			String city = eleBillToAdd.getAttribute(VSIConstants.ATTR_CITY);
			String state = eleBillToAdd.getAttribute(VSIConstants.ATTR_STATE);
			String country = eleBillToAdd.getAttribute(VSIConstants.ATTR_COUNTRY);
			String postalCode = eleBillToAdd.getAttribute(VSIConstants.ATTR_ZIPCODE);
			String phone = eleBillToAdd.getAttribute(VSIConstants.ATTR_DAY_PHONE);
			if(YFCCommon.isVoid(strFirstName)){
				strFirstName="Dummy";
			}
			if(YFCCommon.isVoid(strLastname)){
				strLastname="Dummy";
			}
			if(YFCCommon.isVoid(address1)){
				address1="Dummy";
			}
			if(YFCCommon.isVoid(address2)){
				address2="null";
			}
			if(YFCCommon.isVoid(phone)){
				phone="1234567891";
			}

			Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
			address.setAddress2(address2);

			address.setCountryCode(country);

			//address.setProvince(state);
			address.setProvinceCode(state);
			address.setZip(postalCode);
			checkoutOrder.setBillingAddress(address);
		}

		//ShippingAddress
		Element eleShipToAdd1 = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_BILL_TO);
		String strFirstName=null;
		String strLastname=null;
		Element eleShipToAdd = (Element) elecheckoutOrderLine.getElementsByTagName(VSIConstants.ELE_PERSON_INFO_SHIP_TO).item(0);
		if (eleShipToAdd != null) {
			if(VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType) || VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)){
				 strFirstName = eleShipToAdd1.getAttribute(VSIConstants.ATTR_FIRST_NAME);
				 strLastname= eleShipToAdd1.getAttribute(VSIConstants.ATTR_LAST_NAME);
				}
				else{
					 strFirstName = eleShipToAdd.getAttribute(VSIConstants.ATTR_FIRST_NAME);
					 strLastname= eleShipToAdd.getAttribute(VSIConstants.ATTR_LAST_NAME);
				}
			String address1 = eleShipToAdd.getAttribute(VSIConstants.ATTR_ADDRESS1);
			String address2 = eleShipToAdd.getAttribute(VSIConstants.ATTR_ADDRESS2);
			String city = eleShipToAdd.getAttribute(VSIConstants.ATTR_CITY);
			String state = eleShipToAdd.getAttribute(VSIConstants.ATTR_STATE);
			String country = eleShipToAdd.getAttribute(VSIConstants.ATTR_COUNTRY);
			String postalCode = eleShipToAdd.getAttribute(VSIConstants.ATTR_ZIPCODE);
			String phone = eleShipToAdd.getAttribute(VSIConstants.ATTR_DAY_PHONE);
			if(YFCCommon.isVoid(strFirstName)){
				strFirstName="Dummy";
			}
			if(YFCCommon.isVoid(strLastname)){
				strLastname="Dummy";
			}
			if(YFCCommon.isVoid(address1)){
				address1="Dummy";
			}
			if(YFCCommon.isVoid(address2)){
				address2="null";
			}
			if(YFCCommon.isVoid(phone)){
				phone="1234567891";
			}
			Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
			address.setAddress2(address2);

			address.setCountryCode(country);

			//address.setProvince(state);
			address.setProvinceCode(state);
			address.setZip(postalCode);
			checkoutOrder.setShippingAddress(address);
		}
		else{
			Element eleShipToAddress = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			if (eleShipToAddress != null&&VSIConstants.LINETYPE_STH.equalsIgnoreCase(strLineType)) {
			
						 strFirstName = eleShipToAddress.getAttribute(VSIConstants.ATTR_FIRST_NAME);
						 strLastname= eleShipToAddress.getAttribute(VSIConstants.ATTR_LAST_NAME);
				
				String address1 = eleShipToAddress.getAttribute(VSIConstants.ATTR_ADDRESS1);
				String address2 = eleShipToAddress.getAttribute(VSIConstants.ATTR_ADDRESS2);
				String city = eleShipToAddress.getAttribute(VSIConstants.ATTR_CITY);
				String state = eleShipToAddress.getAttribute(VSIConstants.ATTR_STATE);
				String country = eleShipToAddress.getAttribute(VSIConstants.ATTR_COUNTRY);
				String postalCode = eleShipToAddress.getAttribute(VSIConstants.ATTR_ZIPCODE);
				String phone = eleShipToAddress.getAttribute(VSIConstants.ATTR_DAY_PHONE);
				if(YFCCommon.isVoid(strFirstName)){
					strFirstName="Dummy";
				}
				if(YFCCommon.isVoid(strLastname)){
					strLastname="Dummy";
				}
				if(YFCCommon.isVoid(address1)){
					address1="Dummy";
				}
				if(YFCCommon.isVoid(address2)){
					address2="null";
				}
				if(YFCCommon.isVoid(phone)){
					phone="1234567891";
				}
				
				Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
				address.setAddress2(address2);

				address.setCountryCode(country);

			//	address.setProvince(state);
				address.setProvinceCode(state);
				address.setZip(postalCode);
				checkoutOrder.setShippingAddress(address);
			}
				
		}
		boolean btrue=false;
		// Customer
		if(YFCCommon.isVoid(strCustomerFirstName)){
			strCustomerFirstName="Dummy";
		}
		if(YFCCommon.isVoid(strCustomerLastName)){
			strCustomerLastName="Dummy";
		}
		Customer customer = new Customer(
				eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID),
				strCustomerFirstName,
				strCustomerLastName,
				strBillToID,
				dt.parse(strCustomerCreatets),
				btrue,
				null);
		customer.setSocial(null);
		
		//Changes for OMS-2213 -- Start
		customer.setAccountType(VSIConstants.ATTR_CHECKOUT_TYPE_REGISTERED);
		//Changes for OMS-2213 -- End
		
		checkoutOrder.setCustomer(customer);
		checkoutOrder.setName(strCustomerFirstName);
		
		//client details
		
/*		ClientDetails clientDetails = new ClientDetails();
		clientDetails.setAcceptLanguage(strAcceptUser);
		clientDetails.setUserAgent(strUserAgent);
		checkoutOrder.setClientDetails(clientDetails);*/
		
		return checkoutOrder;
	}
	private static ChargeFreePaymentDetails createChargeFreePaymentDetailsObj (List<ChargeFreePaymentDetails> cfp){
		ChargeFreePaymentDetails cfpd = null;
		List<String> tempList = new ArrayList<String>();
		Double amount =0.0 ;
		for(int i=0 ; i<cfp.size() ;i++) {
			if(!tempList.contains(cfp.get(i).getGateway())) {
				tempList.add(cfp.get(i).getGateway());
			}
			amount=amount+cfp.get(i).getAmount();
		}
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<tempList.size();i++) {
			sb.append(tempList.get(i));
			if(i<tempList.size()-1)
				sb.append(" and ");
		}

		cfpd= new ChargeFreePaymentDetails(sb.toString(), amount);
		return cfpd;
	}

	
}