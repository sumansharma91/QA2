package com.vsi.oms.api.aurus;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.aurus.aesdk.abstractfactory.formfactor.FormFactorHandler;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.api.aurus.VSIPreAuthAESDKFunction.TransRequestObject.L3Product;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIPreAuthAESDKFunction {


	private YFCLogCategory log = YFCLogCategory.instance(VSIPreAuthAESDKFunction.class);
	private static final String TAG = VSIPreAuthAESDKFunction.class.getSimpleName();


	public static void main(String[] args) throws ParserConfigurationException {


		VSIPreAuthAESDKFunction init1= new VSIPreAuthAESDKFunction();
		init1.initAESDKAPI();
		


		VSIPreAuthAESDKFunction preAuth= new VSIPreAuthAESDKFunction();

		TransRequestObject t = new TransRequestObject();
		t.setOneTimeToken("20000000000000000000000015943967");
		t.setTransactionType("4");

//		t.setTransactionTotal("15.99"); 
		t.setBillingFirstName("test");
		t.setBillingLastName("test");
		t.setCardExpiryDate("0422");
		t.setBillingAddressLine1("2101 91st St");
		
		ArrayList<L3Product> a = new ArrayList<VSIPreAuthAESDKFunction.TransRequestObject.L3Product>();
		L3Product l31 = new L3Product();
		a.add(l31);
		t.setL3Product(a);
		
		Document transRequestDoc =preAuth.createTransReqAPiInput(t);
		
		Document outDoc =preAuth.aurusTransRequestAPI(transRequestDoc);

		System.out.println("outDoc ="+SCXmlUtil.getString(outDoc));

		preAuth.parseTransReponseOutDoc(outDoc);

	}

	public  HashMap<String, String> parseTransReponseOutDoc(Document outDoc) {
		
		HashMap<String, String> ParseTransResponseFromAurus= new HashMap<String, String>();
		
		if(outDoc!=null) {
			Element transResponseEle = (Element)outDoc.getElementsByTagName("TransResponse").item(0);
			String aurusPayTicketNum =getElementValue(transResponseEle,"AurusPayTicketNum");

			printLogs("aurusPayTicketNum = "+aurusPayTicketNum);

			NodeList nlTransDetailData = outDoc.getElementsByTagName("TransDetailData");
			int iLineCount=nlTransDetailData.getLength();
			for (int i = 0; i < iLineCount; i++) {
				Element eleOrderLine=(Element)nlTransDetailData.item(i);
				String cardNumber =getElementValue(eleOrderLine,"CardNumber");
				String cardIdentifier =getElementValue(eleOrderLine,"CardIdentifier");
				String processorToken =getElementValue(eleOrderLine,"ProcessorToken");
				String cardExpiryDate =getElementValue(eleOrderLine,"CardExpiryDate");
				String cardEntryMode =getElementValue(eleOrderLine,"CardEntryMode");
				String receiptToken =getElementValue(eleOrderLine,"ReceiptToken");
				String transactionToken =getElementValue(eleOrderLine,"TransactionToken");
				String batchNumber =getElementValue(eleOrderLine,"BatchNumber");
				String referenceNumber =getElementValue(eleOrderLine,"ReferenceNumber");
				String processorResponseCode =getElementValue(eleOrderLine,"ProcessorResponseCode");
				String responseText =getElementValue(eleOrderLine,"ResponseText");
				String totalApprovedAmount =getElementValue(eleOrderLine,"TotalApprovedAmount");
				String transactionType =getElementValue(eleOrderLine,"TransactionType");
				String crmToken =getElementValue(eleOrderLine,"CRMToken");
				String auruspayTransactionId =getElementValue(eleOrderLine,"AuruspayTransactionId");
				String transactionIdentifier =getElementValue(eleOrderLine,"TransactionIdentifier");
				String cardType =getElementValue(eleOrderLine,"CardType");
				String transactionDate =getElementValue(eleOrderLine,"TransactionDate");
				String responseCode =getElementValue(eleOrderLine,"ResponseCode");
				
				String referralNUM =getElementValue(eleOrderLine,"ReferralNUM");
				
				printLogs("referralNUM = "+referralNUM);
				
				String approvalCode =getElementValue(eleOrderLine,"ApprovalCode");
				String transactionAmount =getElementValue(eleOrderLine,"TransactionAmount");
				String transactionTime =getElementValue(eleOrderLine,"TransactionTime");
				String authAVSResult =getElementValue(eleOrderLine,"AuthAVSResult");
				String processorResponseText =getElementValue(eleOrderLine,"ProcessorResponseText");
				String processorTokenRespText =getElementValue(eleOrderLine,"ProcessorTokenRespText");
				String aurusProcessorId =getElementValue(eleOrderLine,"AurusProcessorId");


				Element eleECOMMInfo = SCXmlUtil.getChildElement(eleOrderLine, "ECOMMInfo");

				String oneTimeToken =getElementValue(eleECOMMInfo,"OneTimeToken");
				String storeId =getElementValue(eleECOMMInfo,"StoreId");
				String merchantIdentifier =getElementValue(eleECOMMInfo,"MerchantIdentifier");
				String oneOrderToken =getElementValue(eleECOMMInfo,"OneOrderToken");
				String cVVResult =getElementValue(eleECOMMInfo,"CVVResult");
				String terminalId =getElementValue(eleECOMMInfo,"TerminalId");


				
				ParseTransResponseFromAurus.put("AurusPayTicketNum", aurusPayTicketNum);
				ParseTransResponseFromAurus.put("CardNumber", cardNumber);
				ParseTransResponseFromAurus.put("CardIdentifier", cardIdentifier);
				ParseTransResponseFromAurus.put("ProcessorToken", processorToken);
				ParseTransResponseFromAurus.put("CardExpiryDate", cardExpiryDate);
				ParseTransResponseFromAurus.put("CardEntryMode", cardEntryMode);
				ParseTransResponseFromAurus.put("ReceiptToken", receiptToken);
				ParseTransResponseFromAurus.put("TransactionToken", transactionToken);
				ParseTransResponseFromAurus.put("BatchNumber", batchNumber);
				ParseTransResponseFromAurus.put("ReferenceNumber", referenceNumber);
				ParseTransResponseFromAurus.put("ProcessorResponseCode", processorResponseCode);
				ParseTransResponseFromAurus.put("ResponseText", responseText);
				ParseTransResponseFromAurus.put("TotalApprovedAmount", totalApprovedAmount);
				ParseTransResponseFromAurus.put("TransactionType", transactionType);
				ParseTransResponseFromAurus.put("CRMToken", crmToken);
				ParseTransResponseFromAurus.put("AuruspayTransactionId", auruspayTransactionId);
				ParseTransResponseFromAurus.put("TransactionIdentifier", transactionIdentifier);
				ParseTransResponseFromAurus.put("CardType", cardType);
				ParseTransResponseFromAurus.put("TransactionDate", transactionDate);
				ParseTransResponseFromAurus.put("ResponseCode", responseCode);
				ParseTransResponseFromAurus.put("ReferralNUM", referralNUM);
				ParseTransResponseFromAurus.put("ApprovalCode", approvalCode);
				ParseTransResponseFromAurus.put("TransactionAmount", transactionAmount);
				ParseTransResponseFromAurus.put("TransactionTime", transactionTime);
				ParseTransResponseFromAurus.put("ProcessorResponseText", processorResponseText);
				ParseTransResponseFromAurus.put("ProcessorTokenRespText", processorTokenRespText);
				ParseTransResponseFromAurus.put("AurusProcessorId", aurusProcessorId);
				ParseTransResponseFromAurus.put("OneTimeToken", oneTimeToken);
				ParseTransResponseFromAurus.put("StoreId", storeId);
				ParseTransResponseFromAurus.put("MerchantIdentifier", merchantIdentifier);
				ParseTransResponseFromAurus.put("OneOrderToken", oneOrderToken);
				ParseTransResponseFromAurus.put("CVVResult", cVVResult);
				ParseTransResponseFromAurus.put("TerminalId", terminalId);
				ParseTransResponseFromAurus.put("AuthAVSResult", authAVSResult);
				
				
			}
		}
		return ParseTransResponseFromAurus;
	}

	private Document initAESDKAPI() throws ParserConfigurationException {
		Document initAESDKDoc =createInputInitSDK();

		String initAESDKXML= SCXmlUtil.getString(initAESDKDoc);
		printLogs("initAESDKXML \n"+initAESDKXML);

		FormFactorHandler formFactor = new FormFactorHandler(); 
		String response = formFactor.initAESDK(initAESDKXML);

		Document initResponseDoc=SCXmlUtil.createFromString(response);
		return initResponseDoc; 
	}


	private Document createInputInitSDK() throws ParserConfigurationException {
		Document initAeSDKReqDoc = XMLUtil.createDocument("InitAesdkRequest");
		Element elechangeOrderDoc = initAeSDKReqDoc.getDocumentElement();

		putElementValue(elechangeOrderDoc,"POSID","VSIDEV20");
		putElementValue(elechangeOrderDoc,"ConfigFilePath","C:/aesdkprop");
		//		putElementValue(elechangeOrderDoc,"ConfigFilePath","/Sterling/opt/aesdkprop/");

		return initAeSDKReqDoc;
	}

	public Document createTransReqAPiInput(TransRequestObject transRequestObj) throws ParserConfigurationException {
		Document transRequestDoc = XMLUtil.createDocument("TransRequest");
		Element transRequestEle = transRequestDoc.getDocumentElement();

		putElementValue(transRequestEle,"CorpID", transRequestObj.getCorpID());
		putElementValue(transRequestEle,"ADSDKSpecVer", transRequestObj.getaDSDKSpecVer());
		putElementValue(transRequestEle,"KI", transRequestObj.getKi());
		putElementValue(transRequestEle,"CardType", transRequestObj.getCardType());
		putElementValue(transRequestEle,"ThirdPartyURL", transRequestObj.getThirdPartyURL());
		putElementValue(transRequestEle,"CardExpiryDate", transRequestObj.getCardExpiryDate());
		putElementValue(transRequestEle,"ProcessorToken", transRequestObj.getProcessorToken());
		putElementValue(transRequestEle,"TransactionType", transRequestObj.getTransactionType());
		putElementValue(transRequestEle,"CRMToken", transRequestObj.getCrmToken());
		putElementValue(transRequestEle,"SubTransType", transRequestObj.getSubTransType());
		putElementValue(transRequestEle,"InvoiceNumber", transRequestObj.getInvoiceNumber());
		putElementValue(transRequestEle,"LanguageIndicator", transRequestObj.getLanguageIndicator());
		putElementValue(transRequestEle,"WalletIdentifier", transRequestObj.getWalletIdentifier());
		putElementValue(transRequestEle,"PostAuthSequenceNo", transRequestObj.getPostAuthSequenceNo());
		putElementValue(transRequestEle,"PostAuthCount", transRequestObj.getPostAuthCount());
		putElementValue(transRequestEle,"ApprovalCode", transRequestObj.getApprovalCode());
		putElementValue(transRequestEle,"ClerkID", transRequestObj.getClerkID());
		putElementValue(transRequestEle,"PONumber", transRequestObj.getPoNumber());
		putElementValue(transRequestEle,"ReferenceNumber", transRequestObj.getReferenceNumber());
		putElementValue(transRequestEle,"PODate", transRequestObj.getPoDate());
		putElementValue(transRequestEle,"AurusPayTicketNum", transRequestObj.getAurusPayTicketNum());
		putElementValue(transRequestEle,"OrigTransactionIdentifier", transRequestObj.getOrigTransactionIdentifier());
		putElementValue(transRequestEle,"OrigAurusPayTicketNum", transRequestObj.getOrigAurusPayTicketNum());
		putElementValue(transRequestEle,"CurrencyCode", transRequestObj.getCurrencyCode());
		putElementValue(transRequestEle,"TransactionDate", transRequestObj.getTransactionDate());
		putElementValue(transRequestEle,"TransactionTime", transRequestObj.getTransactionTime());



		Element ecommFingerPrintInfoEle = SCXmlUtil.createChild(transRequestEle, "TransAmountDetails");
		putElementValue(ecommFingerPrintInfoEle,"TaxAmount", transRequestObj.getTaxAmount());
		putElementValue(ecommFingerPrintInfoEle,"Discount", transRequestObj.getDiscount());
		putElementValue(ecommFingerPrintInfoEle,"ProductTotalAmount", transRequestObj.getProductTotalAmount());
		putElementValue(ecommFingerPrintInfoEle,"TransactionTotal", transRequestObj.getTransactionTotal());

		Element eCOMMInfoEle = SCXmlUtil.createChild(transRequestEle, "ECOMMInfo");
		putElementValue(eCOMMInfoEle,"MerchantIdentifier", transRequestObj.getMerchantIdentifier());
		putElementValue(eCOMMInfoEle,"StoreId", transRequestObj.getStoreId());
		putElementValue(eCOMMInfoEle,"TerminalId", transRequestObj.getTerminalId());
		putElementValue(eCOMMInfoEle,"CardIdentifier", transRequestObj.getCardIdentifier());
		putElementValue(eCOMMInfoEle,"OneTimeToken", transRequestObj.getOneTimeToken());
		putElementValue(eCOMMInfoEle,"OneOrderToken", transRequestObj.getOneOrderToken());


		Element billingAddressEle = SCXmlUtil.createChild(transRequestEle, "BillingAddress");
		putElementValue(billingAddressEle,"BillingFirstName", transRequestObj.getBillingFirstName());
		putElementValue(billingAddressEle,"BillingLastName", transRequestObj.getBillingLastName());
		putElementValue(billingAddressEle,"BillingAddressLine1", transRequestObj.getBillingAddressLine1());
		putElementValue(billingAddressEle,"BillingAddressLine2", transRequestObj.getBillingAddressLine2());
		putElementValue(billingAddressEle,"BillingCity", transRequestObj.getBillingCity());
		putElementValue(billingAddressEle,"BillingState", transRequestObj.getBillingState());
		putElementValue(billingAddressEle,"BillingCountry", transRequestObj.getBillingCountry());
		putElementValue(billingAddressEle,"BillingZip", transRequestObj.getBillingZip());
		putElementValue(billingAddressEle,"BillingMobileNumber", transRequestObj.getBillingMobileNumber());
		putElementValue(billingAddressEle,"BillingEmailId", transRequestObj.getBillingEmailId());


		Element shippingAddressEle = SCXmlUtil.createChild(transRequestEle, "ShippingAddress");
		putElementValue(shippingAddressEle,"ShippingFirstName", transRequestObj.getShippingFirstName());
		putElementValue(shippingAddressEle,"ShippingLastName", transRequestObj.getShippingLastName());
		putElementValue(shippingAddressEle,"ShippingAddressLine1", transRequestObj.getShippingAddressLine1());
		putElementValue(shippingAddressEle,"ShippingAddressLine2", transRequestObj.getShippingAddressLine2());
		putElementValue(shippingAddressEle,"ShippingCity", transRequestObj.getShippingCity());
		putElementValue(shippingAddressEle,"ShippingState", transRequestObj.getShippingState());
		putElementValue(shippingAddressEle,"ShippingCountry", transRequestObj.getShippingCountry());
		putElementValue(shippingAddressEle,"ShippingZip", transRequestObj.getShippingZip());
		putElementValue(shippingAddressEle,"ShippingMobileNumber", transRequestObj.getShippingMobileNumber());
		putElementValue(shippingAddressEle,"ShippingEmailId", transRequestObj.getShippingEmailId());


		Element settlementInfoEle = SCXmlUtil.createChild(transRequestEle, "SettlementInfo");
		putElementValue(settlementInfoEle,"PromotionCode", transRequestObj.getPromotionCode());
		putElementValue(settlementInfoEle,"CreditPlan", transRequestObj.getCreditPlan());
		putElementValue(settlementInfoEle,"MerchantTransactionCode", transRequestObj.getMerchantTransactionCode());
		putElementValue(settlementInfoEle,"TransactionDescription", transRequestObj.getTransactionDescription());
		putElementValue(settlementInfoEle,"InCircleAmount", transRequestObj.getInCircleAmount());
		putElementValue(settlementInfoEle,"SalesCheckNumber", transRequestObj.getSalesCheckNumber());


		Element level3ProductsDataEle = SCXmlUtil.createChild(transRequestEle, "Level3ProductsData");
		putElementValue(level3ProductsDataEle,"Level3ProductCount", transRequestObj.getLevel3ProductCount());

		Element Level3ProductsEle = SCXmlUtil.createChild(level3ProductsDataEle, "Level3Products");

		if(transRequestObj.l3Product !=null)
			for(int i=0 ; i<transRequestObj.l3Product.size()  ;i++) {

				Element Level3ProductEle = SCXmlUtil.createChild(Level3ProductsEle, "Level3Product");

				putElementValue(Level3ProductEle,"L3ProductSeqNo", transRequestObj.l3Product.get(i).getL3ProductSeqNo());
				putElementValue(Level3ProductEle,"L3ProductCode", transRequestObj.l3Product.get(i).getL3ProductCode());
				putElementValue(Level3ProductEle,"L3ProductName", transRequestObj.l3Product.get(i).getL3ProductName());
				putElementValue(Level3ProductEle,"L3ProductDescription", transRequestObj.l3Product.get(i).getL3ProductDescription());
				putElementValue(Level3ProductEle,"L3ProductQuantity", transRequestObj.l3Product.get(i).getL3ProductQuantity());
				putElementValue(Level3ProductEle,"L3UnitOfMeasure", transRequestObj.l3Product.get(i).getL3UnitOfMeasure());
				putElementValue(Level3ProductEle,"L3ProductUnitPrice", transRequestObj.l3Product.get(i).getL3ProductUnitPrice());
				putElementValue(Level3ProductEle,"L3ProductDiscount", transRequestObj.l3Product.get(i).getL3ProductDiscount());
				putElementValue(Level3ProductEle,"L3ProductTax", transRequestObj.l3Product.get(i).getL3ProductTax());
				putElementValue(Level3ProductEle,"L3ProductTaxRate", transRequestObj.l3Product.get(i).getL3ProductTaxRate());
				putElementValue(Level3ProductEle,"L3DepartmentID", transRequestObj.l3Product.get(i).getL3DepartmentID());
				putElementValue(Level3ProductEle,"L3ClassID", transRequestObj.l3Product.get(i).getL3ClassID());
				putElementValue(Level3ProductEle,"L3OrderRefNumber", transRequestObj.l3Product.get(i).getL3OrderRefNumber());
				putElementValue(Level3ProductEle,"L3ProductTotalAmount", transRequestObj.l3Product.get(i).getL3ProductTotalAmount());
				putElementValue(Level3ProductEle,"L3FreightAmount", transRequestObj.l3Product.get(i).getL3FreightAmount());
				putElementValue(Level3ProductEle,"L3MonogramAmount", transRequestObj.l3Product.get(i).getL3MonogramAmount());
				putElementValue(Level3ProductEle,"L3TarriffAmount", transRequestObj.l3Product.get(i).getL3TarriffAmount());
				putElementValue(Level3ProductEle,"L3OtherAmount", transRequestObj.l3Product.get(i).getL3OtherAmount());
				putElementValue(Level3ProductEle,"L3GiftWrapAmount", transRequestObj.l3Product.get(i).getL3GiftWrapAmount());
			}

		printLogs("Input Request PreAuth"+SCXmlUtil.getString(transRequestDoc));
		
		return transRequestDoc;
		
	}
	public Document aurusTransRequestAPI(Document transRequestDoc) throws ParserConfigurationException {

		
		String transReqAPIXML = SCXmlUtil.getString(transRequestDoc);

		FormFactorHandler formFactor = new FormFactorHandler(); 
		String jsonResponse = (String) formFactor.authTransaction(transReqAPIXML);

		String response=jsonResponse;
		printLogs("response="+response);

		Document transResponseDoc=SCXmlUtil.createFromString(response);

		//		return null;

		return transResponseDoc;
	}

public Document postAuthAPI(Document transRequestDoc) throws ParserConfigurationException {

		
		String transReqAPIXML = SCXmlUtil.getString(transRequestDoc);

		FormFactorHandler formFactor = new FormFactorHandler(); 
		String jsonResponse = (String) formFactor.authTransaction(transReqAPIXML);

		String response=jsonResponse;
		printLogs("response="+response);

		Document transResponseDoc=SCXmlUtil.createFromString(response);

		//		return null;

		return transResponseDoc;
	}




	private void putElementValue(Element childEle, String key, Object value) {
		Element ele = SCXmlUtil.createChild(childEle, key);
		if(value instanceof String ) {
			ele.setTextContent((String)value);
		}else if(value instanceof Element ) {
			ele.appendChild((Element)value);
		}
	}


	private static String getElementValue(Element element, String tagName) {
		String value="";
		if(element!=null) {
			Element ele = (Element) element.getElementsByTagName(tagName).item(0);
			if(ele!=null) {
				value=ele.getTextContent();
			}
		}
		return value;
	}

	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
	static class TransRequestObject{

		private String aDSDKSpecVer="6.13.1";
		private String sessionId="";
		private String corpID="17304";
		private String processorToken;
		private String cardExpiryDate="";
		private String cardType="";	
		private String crmToken;
		private String ki="";
		private String transactionType;
		private String invoiceNumber;
		private String referenceNumber;
		private String poNumber;
		private String poDate;
		private String walletIdentifier;
		private String languageIndicator="00";
		private String postAuthSequenceNo;
		private String postAuthCount;
		private String aurusPayTicketNum;
		private String origAurusPayTicketNum;
		private String origTransactionIdentifier;
		private String inputIdData;
		private String approvalCode;
		private String subTransType;
		private String currencyCode="840";
		private String clerkID;
		private String transactionDate;
		private String transactionTime;
		private String thirdPartyURL;

		//TransAmountDetails
		private String productTotalAmount;
		private String taxAmount;
		private String discount;
		private String transactionTotal;

		//ECOMMInfo		
		private String merchantIdentifier="100000068987";
		private String storeId="210001";
		private String terminalId="12651743";
		private String oneOrderToken;
		private String oneTimeToken;
		private String cardIdentifier;

		//BillingAddress
		private String billingFirstName="";
		private String billingMiddleName="";
		private String billingLastName="";
		private String billingAddressLine1="";
		private String billingAddressLine2="";
		private String billingZip="";
		private String billingCountry="";
		private String billingState="";
		private String billingMobileNumber="";
		private String billingCity="";
		private String billingEmailId="";

		//ShippingAddress		
		private String shippingFirstName="";
		private String shippingMiddleName="";
		private String shippingLastName="";
		private String shippingAddressLine1="";
		private String shippingAddressLine2="";
		private String shippingZip="";
		private String shippingCity="";
		private String shippingState="";
		private String shippingCountry="";
		private String shippingMobileNumber="";
		private String shippingEmailId="";

		//SettlementInfo
		private String promotionCode="";
		private String creditPlan="";
		private String merchantTransactionCode="";
		private String transactionDescription="";
		private String inCircleAmount="";
		private String salesCheckNumber="";



		//Level3ProductsData
		private String level3ProductCount="";		
		//Level3ProductsData -> Level3Products -> Level3Product	

		private ArrayList<L3Product> l3Product;

		static class L3Product{

			private String l3ProductSeqNo="";
			private String l3ProductCode="";
			private String l3ProductName="";
			private String l3ProductDescription="";
			private String l3ProductQuantity="";
			private String l3UnitOfMeasure="";
			private String l3ProductUnitPrice="";
			private String l3ProductDiscount="";
			private String l3ProductTax="";
			private String l3ProductTaxRate="";
			private String l3FreightAmount="";
			private String l3MonogramAmount="";
			private String l3TarriffAmount="";
			private String l3OtherAmount="";
			private String l3GiftWrapAmount="";
			private String l3ProductTotalAmount="";
			private String l3DepartmentID="";
			private String l3ClassID="";
			private String l3OrderRefNumber="";


			
			
			public String getL3ProductSeqNo() {
				return l3ProductSeqNo;
			}
			public void setL3ProductSeqNo(String l3ProductSeqNo) {
				this.l3ProductSeqNo = l3ProductSeqNo;
			}
			public String getL3ProductCode() {
				return l3ProductCode;
			}
			public void setL3ProductCode(String l3ProductCode) {
				this.l3ProductCode = l3ProductCode;
			}
			public String getL3ProductName() {
				return l3ProductName;
			}
			public void setL3ProductName(String l3ProductName) {
				this.l3ProductName = l3ProductName;
			}
			public String getL3ProductDescription() {
				return l3ProductDescription;
			}
			public void setL3ProductDescription(String l3ProductDescription) {
				this.l3ProductDescription = l3ProductDescription;
			}
			public String getL3ProductQuantity() {
				return l3ProductQuantity;
			}
			public void setL3ProductQuantity(String l3ProductQuantity) {
				this.l3ProductQuantity = l3ProductQuantity;
			}
			public String getL3UnitOfMeasure() {
				return l3UnitOfMeasure;
			}
			public void setL3UnitOfMeasure(String l3UnitOfMeasure) {
				this.l3UnitOfMeasure = l3UnitOfMeasure;
			}
			public String getL3ProductUnitPrice() {
				return l3ProductUnitPrice;
			}
			public void setL3ProductUnitPrice(String l3ProductUnitPrice) {
				this.l3ProductUnitPrice = l3ProductUnitPrice;
			}
			public String getL3ProductDiscount() {
				return l3ProductDiscount;
			}
			public void setL3ProductDiscount(String l3ProductDiscount) {
				this.l3ProductDiscount = l3ProductDiscount;
			}
			public String getL3ProductTax() {
				return l3ProductTax;
			}
			public void setL3ProductTax(String l3ProductTax) {
				this.l3ProductTax = l3ProductTax;
			}
			public String getL3ProductTaxRate() {
				return l3ProductTaxRate;
			}
			public void setL3ProductTaxRate(String l3ProductTaxRate) {
				this.l3ProductTaxRate = l3ProductTaxRate;
			}
			public String getL3FreightAmount() {
				return l3FreightAmount;
			}
			public void setL3FreightAmount(String l3FreightAmount) {
				this.l3FreightAmount = l3FreightAmount;
			}
			public String getL3MonogramAmount() {
				return l3MonogramAmount;
			}
			public void setL3MonogramAmount(String l3MonogramAmount) {
				this.l3MonogramAmount = l3MonogramAmount;
			}
			public String getL3TarriffAmount() {
				return l3TarriffAmount;
			}
			public void setL3TarriffAmount(String l3TarriffAmount) {
				this.l3TarriffAmount = l3TarriffAmount;
			}
			public String getL3OtherAmount() {
				return l3OtherAmount;
			}
			public void setL3OtherAmount(String l3OtherAmount) {
				this.l3OtherAmount = l3OtherAmount;
			}
			public String getL3GiftWrapAmount() {
				return l3GiftWrapAmount;
			}
			public void setL3GiftWrapAmount(String l3GiftWrapAmount) {
				this.l3GiftWrapAmount = l3GiftWrapAmount;
			}
			public String getL3ProductTotalAmount() {
				return l3ProductTotalAmount;
			}
			public void setL3ProductTotalAmount(String l3ProductTotalAmount) {
				this.l3ProductTotalAmount = l3ProductTotalAmount;
			}
			public String getL3DepartmentID() {
				return l3DepartmentID;
			}
			public void setL3DepartmentID(String l3DepartmentID) {
				this.l3DepartmentID = l3DepartmentID;
			}
			public String getL3ClassID() {
				return l3ClassID;
			}
			public void setL3ClassID(String l3ClassID) {
				this.l3ClassID = l3ClassID;
			}
			public String getL3OrderRefNumber() {
				return l3OrderRefNumber;
			}
			public void setL3OrderRefNumber(String l3OrderRefNumber) {
				this.l3OrderRefNumber = l3OrderRefNumber;
			}

		}



		public String getCrmToken() {
			return crmToken;
		}

		public void setCrmToken(String crmToken) {
			this.crmToken = crmToken;
		}

		public String getKi() {
			return ki;
		}

		public void setKi(String ki) {
			this.ki = ki;
		}

		public String getaDSDKSpecVer() {
			return aDSDKSpecVer;
		}
		public void setaDSDKSpecVer(String aDSDKSpecVer) {
			this.aDSDKSpecVer = aDSDKSpecVer;
		}
		public String getSessionId() {
			return sessionId;
		}
		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}
		public String getCorpID() {
			return corpID;
		}
		public void setCorpID(String corpID) {
			this.corpID = corpID;
		}
		public String getProcessorToken() {
			return processorToken;
		}
		public void setProcessorToken(String processorToken) {
			this.processorToken = processorToken;
		}
		public String getCardExpiryDate() {
			return cardExpiryDate;
		}
		public void setCardExpiryDate(String cardExpiryDate) {
			this.cardExpiryDate = cardExpiryDate;
		}
		public String getCardType() {
			return cardType;
		}
		public void setCardType(String cardType) {
			this.cardType = cardType;
		}
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		public String getInvoiceNumber() {
			return invoiceNumber;
		}
		public void setInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
		}
		public String getReferenceNumber() {
			return referenceNumber;
		}
		public void setReferenceNumber(String referenceNumber) {
			this.referenceNumber = referenceNumber;
		}
		public String getPoNumber() {
			return poNumber;
		}
		public void setPoNumber(String poNumber) {
			this.poNumber = poNumber;
		}
		public String getPoDate() {
			return poDate;
		}
		public void setPoDate(String poDate) {
			this.poDate = poDate;
		}
		public String getWalletIdentifier() {
			return walletIdentifier;
		}
		public void setWalletIdentifier(String walletIdentifier) {
			this.walletIdentifier = walletIdentifier;
		}
		public String getLanguageIndicator() {
			return languageIndicator;
		}
		public void setLanguageIndicator(String languageIndicator) {
			this.languageIndicator = languageIndicator;
		}
		public String getPostAuthSequenceNo() {
			return postAuthSequenceNo;
		}
		public void setPostAuthSequenceNo(String postAuthSequenceNo) {
			this.postAuthSequenceNo = postAuthSequenceNo;
		}
		public String getPostAuthCount() {
			return postAuthCount;
		}
		public void setPostAuthCount(String postAuthCount) {
			this.postAuthCount = postAuthCount;
		}
		public String getAurusPayTicketNum() {
			return aurusPayTicketNum;
		}
		public void setAurusPayTicketNum(String aurusPayTicketNum) {
			this.aurusPayTicketNum = aurusPayTicketNum;
		}
		public String getOrigAurusPayTicketNum() {
			return origAurusPayTicketNum;
		}
		public void setOrigAurusPayTicketNum(String origAurusPayTicketNum) {
			this.origAurusPayTicketNum = origAurusPayTicketNum;
		}
		public String getOrigTransactionIdentifier() {
			return origTransactionIdentifier;
		}
		public void setOrigTransactionIdentifier(String origTransactionIdentifier) {
			this.origTransactionIdentifier = origTransactionIdentifier;
		}
		public String getInputIdData() {
			return inputIdData;
		}
		public void setInputIdData(String inputIdData) {
			this.inputIdData = inputIdData;
		}
		public String getApprovalCode() {
			return approvalCode;
		}
		public void setApprovalCode(String approvalCode) {
			this.approvalCode = approvalCode;
		}
		public String getSubTransType() {
			return subTransType;
		}
		public void setSubTransType(String subTransType) {
			this.subTransType = subTransType;
		}
		public String getCurrencyCode() {
			return currencyCode;
		}
		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}
		public String getClerkID() {
			return clerkID;
		}
		public void setClerkID(String clerkID) {
			this.clerkID = clerkID;
		}
		public String getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(String transactionDate) {
			this.transactionDate = transactionDate;
		}
		public String getTransactionTime() {
			return transactionTime;
		}
		public void setTransactionTime(String transactionTime) {
			this.transactionTime = transactionTime;
		}
		public String getThirdPartyURL() {
			return thirdPartyURL;
		}
		public void setThirdPartyURL(String thirdPartyURL) {
			this.thirdPartyURL = thirdPartyURL;
		}
		public String getProductTotalAmount() {
			return productTotalAmount;
		}
		public void setProductTotalAmount(String productTotalAmount) {
			this.productTotalAmount = productTotalAmount;
		}
		public String getTaxAmount() {
			return taxAmount;
		}
		public void setTaxAmount(String taxAmount) {
			this.taxAmount = taxAmount;
		}
		public String getDiscount() {
			return discount;
		}
		public void setDiscount(String discount) {
			this.discount = discount;
		}
		public String getTransactionTotal() {
			return transactionTotal;
		}
		public void setTransactionTotal(String transactionTotal) {
			this.transactionTotal = transactionTotal;
		}
		public String getMerchantIdentifier() {
			return merchantIdentifier;
		}
		public void setMerchantIdentifier(String merchantIdentifier) {
			this.merchantIdentifier = merchantIdentifier;
		}
		public String getStoreId() {
			return storeId;
		}
		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}
		public String getTerminalId() {
			return terminalId;
		}
		public void setTerminalId(String terminalId) {
			this.terminalId = terminalId;
		}
		public String getOneOrderToken() {
			return oneOrderToken;
		}
		public void setOneOrderToken(String oneOrderToken) {
			this.oneOrderToken = oneOrderToken;
		}
		public String getOneTimeToken() {
			return oneTimeToken;
		}
		public void setOneTimeToken(String oneTimeToken) {
			this.oneTimeToken = oneTimeToken;
		}
		public String getCardIdentifier() {
			return cardIdentifier;
		}
		public void setCardIdentifier(String cardIdentifier) {
			this.cardIdentifier = cardIdentifier;
		}
		public String getBillingFirstName() {
			return billingFirstName;
		}
		public void setBillingFirstName(String billingFirstName) {
			this.billingFirstName = billingFirstName;
		}
		public String getBillingMiddleName() {
			return billingMiddleName;
		}
		public void setBillingMiddleName(String billingMiddleName) {
			this.billingMiddleName = billingMiddleName;
		}
		public String getBillingLastName() {
			return billingLastName;
		}
		public void setBillingLastName(String billingLastName) {
			this.billingLastName = billingLastName;
		}
		public String getBillingAddressLine1() {
			return billingAddressLine1;
		}
		public void setBillingAddressLine1(String billingAddressLine1) {
			this.billingAddressLine1 = billingAddressLine1;
		}
		public String getBillingAddressLine2() {
			return billingAddressLine2;
		}
		public void setBillingAddressLine2(String billingAddressLine2) {
			this.billingAddressLine2 = billingAddressLine2;
		}


		public String getBillingState() {
			return billingState;
		}

		public void setBillingState(String billingState) {
			this.billingState = billingState;
		}

		public String getBillingZip() {
			return billingZip;
		}
		public void setBillingZip(String billingZip) {
			this.billingZip = billingZip;
		}
		public String getBillingCountry() {
			return billingCountry;
		}
		public void setBillingCountry(String billingCountry) {
			this.billingCountry = billingCountry;
		}
		public String getBillingMobileNumber() {
			return billingMobileNumber;
		}
		public void setBillingMobileNumber(String billingMobileNumber) {
			this.billingMobileNumber = billingMobileNumber;
		}
		public String getBillingCity() {
			return billingCity;
		}
		public void setBillingCity(String billingCity) {
			this.billingCity = billingCity;
		}
		public String getBillingEmailId() {
			return billingEmailId;
		}
		public void setBillingEmailId(String billingEmailId) {
			this.billingEmailId = billingEmailId;
		}
		public String getShippingFirstName() {
			return shippingFirstName;
		}
		public void setShippingFirstName(String shippingFirstName) {
			this.shippingFirstName = shippingFirstName;
		}
		public String getShippingMiddleName() {
			return shippingMiddleName;
		}
		public void setShippingMiddleName(String shippingMiddleName) {
			this.shippingMiddleName = shippingMiddleName;
		}
		public String getShippingLastName() {
			return shippingLastName;
		}
		public void setShippingLastName(String shippingLastName) {
			this.shippingLastName = shippingLastName;
		}
		public String getShippingAddressLine1() {
			return shippingAddressLine1;
		}
		public void setShippingAddressLine1(String shippingAddressLine1) {
			this.shippingAddressLine1 = shippingAddressLine1;
		}
		public String getShippingAddressLine2() {
			return shippingAddressLine2;
		}
		public void setShippingAddressLine2(String shippingAddressLine2) {
			this.shippingAddressLine2 = shippingAddressLine2;
		}
		public String getShippingZip() {
			return shippingZip;
		}
		public void setShippingZip(String shippingZip) {
			this.shippingZip = shippingZip;
		}
		public String getShippingCity() {
			return shippingCity;
		}
		public void setShippingCity(String shippingCity) {
			this.shippingCity = shippingCity;
		}


		public String getShippingState() {
			return shippingState;
		}

		public void setShippingState(String shippingState) {
			this.shippingState = shippingState;
		}

		public String getShippingCountry() {
			return shippingCountry;
		}
		public void setShippingCountry(String shippingCountry) {
			this.shippingCountry = shippingCountry;
		}
		public String getShippingMobileNumber() {
			return shippingMobileNumber;
		}
		public void setShippingMobileNumber(String shippingMobileNumber) {
			this.shippingMobileNumber = shippingMobileNumber;
		}
		public String getShippingEmailId() {
			return shippingEmailId;
		}
		public void setShippingEmailId(String shippingEmailId) {
			this.shippingEmailId = shippingEmailId;
		}





		public String getPromotionCode() {
			return promotionCode;
		}

		public void setPromotionCode(String promotionCode) {
			this.promotionCode = promotionCode;
		}

		public String getCreditPlan() {
			return creditPlan;
		}

		public void setCreditPlan(String creditPlan) {
			this.creditPlan = creditPlan;
		}

		public String getMerchantTransactionCode() {
			return merchantTransactionCode;
		}

		public void setMerchantTransactionCode(String merchantTransactionCode) {
			this.merchantTransactionCode = merchantTransactionCode;
		}

		public String getTransactionDescription() {
			return transactionDescription;
		}

		public void setTransactionDescription(String transactionDescription) {
			this.transactionDescription = transactionDescription;
		}

		public String getInCircleAmount() {
			return inCircleAmount;
		}

		public void setInCircleAmount(String inCircleAmount) {
			this.inCircleAmount = inCircleAmount;
		}

		public String getSalesCheckNumber() {
			return salesCheckNumber;
		}

		public void setSalesCheckNumber(String salesCheckNumber) {
			this.salesCheckNumber = salesCheckNumber;
		}

		public String getLevel3ProductCount() {
			return level3ProductCount;
		}
		public void setLevel3ProductCount(String level3ProductCount) {
			this.level3ProductCount = level3ProductCount;
		}

		public ArrayList<L3Product> getL3Product() {
			return l3Product;
		}

		public void setL3Product(ArrayList<L3Product> l3Product) {
			this.l3Product = l3Product;
		}




		//		private String posID="VSIDEV20";
		//		private String appId="";
		//		private String ccTID="";
		//		private String laneNo="";		
		//		private String posType="";
		//		private String kiType;
		//		private String deviceId;
		//		private String headerMessage;
		//		private String messageLine1="";
		//		private String cardToken;
		//		private String customerIdentifier;
		//		private String cardPresent="";	
		//		private String purchaserPresent="";	
		//		private String entrySource="";	
		//		private String keyedEntryAVSFlag="";	
		//		private String keyedEntryReasonCode="";	
		//		private String giftPurchaseAuthIndicator="";	
		//		private String ecommerceIndicator="";	
		//		private String enableNFCReader="";	
		//		private String cashBackFlag="";	
		//		private String cVBFlag="";	
		//		private String processingMode="";	
		//		private String processCardFlag="";	
		//		private String subCardType="";	
		//		private String feedbackFlag="";	
		//		private String cvvFlag="";	
		//		private String loyaltyLookupFlagFlag="";
		//		private String cardNumber;
		//		private String receiptNumber;
		//		private String crmToken;
		//		private String taxExemptFlag;
		//		private String posIP;
		//		private String posClientName;
		//		private String posVersion;
		//		private String customerCode;
		//		private String customerFirstName;
		//		private String customerLastName;
		//		private String customerType;
		//		private String customerEmail;
		//		private String subWalletIdentifier;
		//		private String alternatePaymentData;
		//		private String dLIntellicheckFlag;
		//		private String programId;
		//		private String storeCardFlag;
		//		private String merchantCategoryCode;
		//		private String ssnFlag;
		//		private String ssn;
		//		private String preSaleDate;
		//		private String preApprovalRefNumber;
		//		private String giftCardType;
		//		private String giftCardTypePassCode;
		//		private String inputIdentifier;
		//		private String outputDataMatrix;
		//		private String dccTransParentId;
		//		private String voucherNumber;
		//		private String ivrAuthNumber;
		//		private String showResponse;
		//		private String referralNum;
		//		private String equalPayPlanNum;
		//		private String dccCurrencyCode;
		//		private String dccFgnAmount;
		//		private String dccOffered;
		//		private String tipEligible;
		//		private String amountNoBar;
		//		private String batchNumber;
		//		private String allowknukleBuster;
		//		private String ticketProductData;
		//		private String tenderTypeRestrictions;
		//		private String cardTypeSupport;
		//		private String signatureFlag;
		//		private String isTokenizedTransaction;
		//		private String fleetPromptCode;
		//		private String allowZipPrompt;
		//		private String partialAllowed;
		//		private String printOnlyMode;
		//		private String posEnvironmentIndicator;
		//		private String plCCPaymentMethod;
		//		private String thirdPartyID;
		//		private String reserved1;
		//		private String reserved2;

		//		//CardDataInfo		
		//		private String cardDataSource="";	
		//		private String encryptionMode="";	
		//		private String trackData="";	
		//		private String eMVDetailsData="";	
		//		private String cvvData="";	
		//		private String pinBlock="";	
		//		private String ksnBlock="";	

		//		private String servicesTotalAmount;		
		//		private String ebtAmount;
		//		private String fsaAmount;
		//		private String dutyTotalAmount;
		//		private String freightTotalAmount;
		//		private String alternateTaxAmount;
		//		private String tipAmount;
		//		private String donationAmount;
		//		private String cashBackAmount;
		//		private String cashBackFees;
		//		private String convenienceFees;
		//		private String tenderAmount;


		//       //GiftCardList  ->GiftCard		
		//		private String giftCardNumber;
		//		private String track2Data;
		//		private String amount;
		//		private String cardActivationDate;
		//		private String cardExpirationDate;


		//		private String cvv;
		//		private String eCOMMInfo_cardExpiryDate;
		//		private String domainId="1";
		//		private String templateId="1";
		//		private String urlType="1";
		//		private String sourcePostBackUrl;

		//		//ECOMMFingerPrintInfo
		//		private String iPAddress="10.170.10.167";
		//		private String deviceOS="CentOS Linux 7";
		//		private String deviceType="Desktop";
		//		private String browserDetails="IE11";
		//		private String deviceScreenResolution="1422x636";
		//		private String referralURL="https://xxxx.com/jsp/orderreview.jsp";
		//		private String countryCode="840";
		//		private String stateName="Florida";
		//		private String city="Bartow";
		//		private String browserLanguage="en-us English";
		//		private String websiteSessionID="0Fk+T6EmwIHLSH5pQUWQO4AJ";
		//		private String networkType="Bluetooth";
		//		private String networkSubType="2g";
		//		private String networkDownloadSpeed="4";
		//		private String networkMaxDownloadSpeed="8";		

		//		private String billingAddressLine3="";		
		//		private String billingProvince="";
		//		private String billingOtherNumber="";


		//		private String shippingAddressLine3="";
		//		private String shippingProvince="";
		//		private String shippingOtherNumber="";


		//		//ShippingInfo
		//		private String shippingIdCount="";
		//		
		//		//ShippingInfo -> ShippingAddresses -> ShippingAddress
		//		private String addressId="";
		//		private String firstName="";
		//		private String middleName="";
		//		private String lastName="";
		//		private String addressLine1="";
		//		private String addressLine2="";
		//		private String addressLine3="";
		//		private String zip="";
		//		private String shipping_add_city="";
		//		private String state="";
		//		private String province="";
		//		private String country="";
		//		private String mobileNumber="";
		//		private String otherNumber="";
		//		private String emailId="";
		//		private String method="";
		//		private String company="";

		//		//CheckInfo
		//		private String driverLicenseEntryMode="";
		//		private String driverLicenseData="";
		//		private String driverLicenseState="";
		//		private String checkEntryMode="";
		//		private String fullMICR="";
		//		private String routingNumber="";
		//		private String accountNumber="";
		//		private String checkNumber="";
		//		private String checkType="";
		//		private String consumerDOB="";
		//		private String otherIDEntryMode="";
		//		private String otherIDType="";
		//		private String otherIDData="";
		//		private String otherIDState="";
		//		private String checkInfo_stateName="";

		//		//FraudScoreInfo
		//		private String shippingMethod="";
		//		private String shippingCompany="";
		//		private String egcMessage="";
		//		private String egcEmail="";
		//		private String deviceFingerPrintId="";
		//		private String loggedInState="";
		//		private String pickupStore="";
		//		private String pickupState="";

		//		//RentalInfo
		//		private String renterName="";
		//		private String rentalAgreementNo="";
		//		private String rentalDate="";
		//		private String rentalTime="";
		//		private String rentalDuration="";
		//		private String rentalCity="";
		//		private String rentalState="";
		//		private String rentalCountry="";
		//		private String rentalExtraCharge="";
		//		private String rentalClassID="";
		//		private String rentalTaxIndicator="";
		//		private String returnDate="";
		//		private String returnTime="";
		//		private String returnCity="";
		//		private String returnState="";
		//		private String returnCountry="";
		//		private String amountExtraCharge="";
		//		private String delayChargeIndicator="";

		//		//UPCDetails
		//		private String upcCount="";
		//		
		//		//UPCDetails -> UPCItems ->UPCItem
		//		
		//		private String upcIndicator="";
		//		private String upcPLUData="";
		//		private String upcItemPrice="";
		//		private String upcQuantity="";
		//		private String unitofMeasure="";

		//		//FleetData
		//		private String fleetProductCount="";
		//		
		//		//FleetData -> FleetProducts -> FleetProduct
		//		
		//		private String fleetProductSeqNo="";
		//		private String fleetProductDataType="";
		//		private String fleetServiceLevel="";
		//		private String fleetNACSCode="";
		//		private String fleetProductName="";
		//		private String fleetUnitOfMeasure="";
		//		private String fleetProductQuantity="";
		//		private String fleetProductUnitPrice="";
		//		private String fleetProductTotalAmount="";


		//		//EPPDetailsInfo
		//		private String sTAN="";
		//		private String lifecycleID="";
		//		private String posCapability="";
		//		private String conditionCode="";
		//		private String amountDue="";
		//		private String termsVersion="";
		//		private String productIdentificationData="";
		//		private String productCount="";
		//		
		//		//EPPDetailsInfo - >EPPDetails ->EPPProductData
		//		private String datasetVersion="";
		//		private String itemCode="";
		//		private String quantity="";
		//		private String signIndicator="";
		//		private String price="";
		//		private String mBASDiscountQuantity="";
		//		private String mBASDiscountAmount="";
		//		private String eligibilityFlag="";
		//		private String redemptionReqAmount="";
		//		private String manufacturerDiscountAmount="";
		//		private String adjustmentPrice="";
		//		private String itemReferenceNumber="";
		//		private String purseID="";
		//		private String mBASStoreDiscountQuantity="";
		//		private String mBASStoreDiscountAmount="";
		//		private String storeDiscountAmount="";
		//		private String storeDiscountType="";
		//		private String mBASEligibilityFlags="";

		//		//AdditionalTags -> Tags ->Tag
		//		
		//		private String tag_key="";
		//		private String tag_value="";






	}
}
