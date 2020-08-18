package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is called by service VSISettlementLoad to load settlements data to custom tables.
 * 
 * @author IBM
 * 
 */

public class VSISettlementLoad {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSISettlementLoad.class);
	YIFApi api;

	/**
	 * Sample Input: 
	 * <?xml version="1.0" encoding="UTF-8"?>
<SettlementList>
    <Settlement AdjustmentId="amzn1:crow:dMa0++w6Q8SxGKg4xyNCwg"
        Currency="" DepositDate="" DirectPaymentAmount=""
        DirectPaymentType="" FulfillmentId="" ItemRelatedFeeAmount=""
        ItemRelatedFeeType="" MarketplaceName=""
        MerchangeAdjustmentItemId="" MerchangeOrderItemId=""
        MerchantOrderId="" MiscFeeAmount="" OrderFeeAmount=""
        OrderFeeType="" OrderId="114-9029301-4580206" OrderItemCode=""
        OtherAmount="24.80" OtherFeeAmount=""
        OtherFeeReasonDescription=""
        PostedDate="2017-03-12T12:19:49+00:00" PriceAmount=""
        PriceType="" PromotionAmount="" PromotionId="" PromotionType=""
        QuantityPurchased="1" SettlementEndDate=""
        SettlementId="6307306131" SettlementStartDate=""
        ShipmentFeeAmount="" ShipmentFeeType="" ShipmentId=""
        Sku="1448248" TotalAmount="" TransactionType="REVERSAL_REIMBURSEMENT"/>
</SettlementList>

	 *
	 * 
	 * 
	 * @param env
	 * @param docInput
	 * @return
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 * @throws ParserConfigurationException 
	 */
	public Document createSettlementRecord(YFSEnvironment env, Document docInput) 
			throws YFSException, RemoteException, YIFClientCreationException, ParserConfigurationException{

		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName()+ "createSettlementRecord() : \n" + XMLUtil.getXMLString(docInput));
		}

		Element eleSettlement= (Element) docInput.getElementsByTagName(VSIConstants.ELE_SETTLEMENT).item(0);
		String totalAmount = eleSettlement.getAttribute(VSIConstants.ATTR_TOTAL_AMOUNT); 
		String transactionType = eleSettlement.getAttribute(VSIConstants.ATTR_TRANSATION_TYPE); 
		String settlementId= eleSettlement.getAttribute(VSIConstants.ATTR_SETTLEMENT_ID); 
		String orderId= eleSettlement.getAttribute(VSIConstants.ATTR_ORDER_ID); 
		String fulfillmentId= eleSettlement.getAttribute(VSIConstants.ATTR_FULFILLMENT_ID); 
		String sku= eleSettlement.getAttribute(VSIConstants.ATTR_SKU); 

		Document docVSIGetSetlTransactionListOutput = null;
		Document docVSIGetSetlTransactionListInput = null;
		Document docVSISetlTransaction = null;
		Document docVSISettlementInput = null;

		try {

			api = YIFClientFactory.getInstance().getApi();


			//Create Settlement Header
			if (totalAmount != null && totalAmount.length() > 0)
			{   
				//call VSICreateSettlement
				docVSISettlementInput = createSetttlementInputDoc(eleSettlement);
				
				if (log.isDebugEnabled()) {
					log.debug(this.getClass().getName()+ "createSettlementRecord() : creating Settlement record\n" + 
							XMLUtil.getXMLString(docVSISettlementInput));
				}

				//call service VSICreateSettlement with doc docVSISettlementInput
				api.executeFlow(env, "VSICreateSettlement", docVSISettlementInput);
				
//				System.out.println("VSICreateSettlement : \n" + XMLUtil.getXMLString(docVSISettlementInput));


				return docInput; 	
			}


			//create VSISetlTransactionInput
			 docVSISetlTransaction = createSetlTransactionInputDoc(eleSettlement);
			
				
			if (transactionType != null 
					&& (transactionType.equals(VSIConstants.TRANS_TYPE_ORDER) ||
							transactionType.equals(VSIConstants.TRANS_TYPE_REFUND) || 
							transactionType.equals(VSIConstants.TRANS_TYPE_A_Z_GURANTEE_REFUND)))
			{
				
				//call VSIGetSetlTransactionList to check if transaction already exists 
				//create VSIGetSetlTransactionList input
				docVSIGetSetlTransactionListInput = XMLUtil.createDocument(VSIConstants.ELE_SETL_TRANSACTION);
				Element eleVSISetlTransactionList = docVSIGetSetlTransactionListInput.getDocumentElement();
				eleVSISetlTransactionList.setAttribute(VSIConstants.ATTR_SETTLEMENT_ID,settlementId);
				eleVSISetlTransactionList.setAttribute(VSIConstants.ATTR_TRANSATION_TYPE, transactionType);
				eleVSISetlTransactionList.setAttribute(VSIConstants.ATTR_ORDER_ID, orderId);
				eleVSISetlTransactionList.setAttribute(VSIConstants.ATTR_FULFILLMENT_ID, fulfillmentId);
				eleVSISetlTransactionList.setAttribute(VSIConstants.ATTR_SKU, sku);

				if (log.isDebugEnabled()) {
					log.debug(this.getClass().getName()+ "createSettlementRecord() : Checking for existing transations \n" 
							+ XMLUtil.getXMLString(docVSIGetSetlTransactionListInput));
				}
				
				//call VSIGetSetlTransactionList
				docVSIGetSetlTransactionListOutput = api.executeFlow(env, "VSIGetSetlTransactionList", 
						docVSIGetSetlTransactionListInput);
				
				if (log.isDebugEnabled()) {
					log.debug(this.getClass().getName()+ "createSettlementRecord() docVSIGetSetlTransactionListOutput: \n" 
							+ XMLUtil.getXMLString(docVSIGetSetlTransactionListOutput));
				}

				Element eleSetlTransactionList = docVSIGetSetlTransactionListOutput.getDocumentElement();
				ArrayList<Element> setlTransactions = SCXmlUtil.getChildren(eleSetlTransactionList, "VSISetlTransaction");
				
				if (setlTransactions.size() > 0)
				{
					Element eleSetlTransaction= docVSISetlTransaction.getDocumentElement();
					eleSetlTransaction.setAttribute(VSIConstants.ATTR_TRANSACTION_KEY, setlTransactions.get(0).getAttribute(VSIConstants.ATTR_TRANSACTION_KEY));
					
					//call VSIChangeSetlTransaction with charges
					api.executeFlow(env, "VSIChangeSetlTransaction", 
							docVSISetlTransaction);
					
//					System.out.println("VSIChangeSetlTransaction : \n" + XMLUtil.getXMLString(docVSISetlTransaction));
				
					return docInput; 
				}
			}

			//call VSICreateSetlTransaction with all charges
				api.executeFlow(env, "VSICreateSetlTransaction", 
						docVSISetlTransaction);

//				System.out.println("VSICreateSetlTransaction : \n" + XMLUtil.getXMLString(docVSISetlTransaction));


		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw e;
		} catch (YFSException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		return docInput;
	}

	/**
	 * This method will create the input for creating Transaction record with charges. 
	 *
	 * 
	 * @param eleSettlement
	 * @return Document
	 * 
<VSISetlTransaction SettlementId="1237" TransactionType="Order" OrderId="532525" FulfillmentId="STH" Sku="33435" QuantityPurchased="10" >
<VSISetlTransChargeList>
<VSISetlTransCharge ChargeType="ShippingFee" ChargeAmount="20" PromotionId="" />
<VSISetlTransCharge ChargeType="Principal" ChargeAmount="200" PromotionId="" />
</VSISetlTransChargeList>
</VSISetlTransaction>
	 * @throws ParserConfigurationException 
	 * 
	 */
	
	public Document createSetlTransactionInputDoc(Element eleSettlement) throws ParserConfigurationException
	{
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName()+ "Input to createSetlTransactionInputDoc() : \n" 
					+ eleSettlement.getTextContent());
		}
		
		Document docVSISetlTransactionInput = null;
		
		try {
			docVSISetlTransactionInput = XMLUtil.createDocument(VSIConstants.ELE_SETL_TRANSACTION);
			
			
			String transactionType = eleSettlement.getAttribute(VSIConstants.ATTR_TRANSATION_TYPE); 
			String settlementId= eleSettlement.getAttribute(VSIConstants.ATTR_SETTLEMENT_ID); 
			String orderId= eleSettlement.getAttribute(VSIConstants.ATTR_ORDER_ID); 
			String fulfillmentId= eleSettlement.getAttribute(VSIConstants.ATTR_FULFILLMENT_ID); 
			String sku = eleSettlement.getAttribute(VSIConstants.ATTR_SKU); 
			String quantityPurchased = eleSettlement.getAttribute(VSIConstants.ATTR_QUANTITY_PURCHASED); 
			String priceType= eleSettlement.getAttribute(VSIConstants.ATTR_PRICE_TYPE); 
			String priceAmount = eleSettlement.getAttribute(VSIConstants.ATTR_PRICE_AMOUNT); 
			String itemRelatedFeeType= eleSettlement.getAttribute(VSIConstants.ATTR_ITEM_RELATED_FEE_TYPE); 
			String itemRelatedFeeAmount= eleSettlement.getAttribute(VSIConstants.ATTR_ITEM_RELATED_FEE_AMOUNT); 
			String promotionAmount= eleSettlement.getAttribute(VSIConstants.ATTR_PROMOTION_AMOUNT); 
			String promotionId = eleSettlement.getAttribute(VSIConstants.ATTR_PROMOTION_ID); 
			String promotionType= eleSettlement.getAttribute(VSIConstants.ATTR_PROMOTION_TYPE); 
			String otherAmount= eleSettlement.getAttribute(VSIConstants.ATTR_OTHER_AMOUNT); 

			Element eleVSISetlTransaction = docVSISetlTransactionInput.getDocumentElement();
			eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_SETTLEMENT_ID,settlementId);
			eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_EXTN_STORE_FRONT, VSIConstants.NUTRITION_DEPOT);
			eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_TRANSATION_TYPE, transactionType);
			 if (orderId != null && orderId.length() > 0 )  
			eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_ORDER_ID, orderId);
			eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_FULFILLMENT_ID, fulfillmentId);
			if (sku != null && sku.length() > 0 )  
				 eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_SKU, sku) ;
			if (quantityPurchased != null && quantityPurchased.length() > 0 )  
				 eleVSISetlTransaction.setAttribute(VSIConstants.ATTR_QUANTITY_PURCHASED, quantityPurchased);


			if (	(priceAmount != null && priceAmount.length() > 0))
			{
				Element eleVSISetlTransChargeList = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransaction, VSIConstants.ELE_SETL_TRANS_CHAREGE_LIST, "");
				Element eleVSISetTransCharge = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransChargeList, VSIConstants.ELE_SETL_TRANS_CHAREGE, "");
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, priceType);
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, priceAmount);
				eleVSISetTransCharge.setAttribute("Action","Create");
				

			}
			
			else if ((itemRelatedFeeAmount != null && itemRelatedFeeAmount.length() > 0))
			{
				Element eleVSISetlTransChargeList = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransaction, "VSISetlTransChargeList", "");
				Element eleVSISetTransCharge = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransChargeList, "VSISetlTransCharge", "");
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, itemRelatedFeeType);
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, itemRelatedFeeAmount);
				eleVSISetTransCharge.setAttribute("Action","Create");
				
			}
			
			else if (promotionAmount != null && promotionAmount.length() > 0)
			{
				Element eleVSISetlTransChargeList = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransaction, "VSISetlTransChargeList", "");
				Element eleVSISetTransCharge = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransChargeList, "VSISetlTransCharge", "");
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, promotionType);
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, promotionAmount);
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_PROMOTION_ID, promotionId);
				eleVSISetTransCharge.setAttribute("Action","Create");
				
			}
			else if ((otherAmount != null && otherAmount.length() > 0))
			{
				Element eleVSISetlTransChargeList = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransaction, "VSISetlTransChargeList", "");
				Element eleVSISetTransCharge = XMLUtil.appendChild(docVSISetlTransactionInput, eleVSISetlTransChargeList, "VSISetlTransCharge", "");
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, "Other");
				eleVSISetTransCharge.setAttribute(VSIConstants.ATTR_CHARGE_AMOUNT, otherAmount);
				eleVSISetTransCharge.setAttribute("Action","Create");
				
			}
			
			
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw e;
		} catch (YFSException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName()+ "Returning from createSetlTransactionInputDoc() : \n" 
					+ XMLUtil.getXMLString(docVSISetlTransactionInput));
		}
		
		return docVSISetlTransactionInput;
	}
/**
 * This method will create the input for creating Settlement Header record. 
 * @param eleSettlement
 * @return document
 * <VSISettlement SettlementId="1234" TotalAmount="2343" SettlementStartDate=""
	SettlementEndDate="" SettlementDepositDate="" />
 * @throws ParserConfigurationException 
 */
	
	public Document createSetttlementInputDoc(Element eleSettlement) throws ParserConfigurationException
	{
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName()+ "Input to createSetttlementInputDoc() : \n" 
					+ eleSettlement.getTextContent());
		}
		
		Document createSetttlementInput = null;
		
		try {
			String totalAmount = eleSettlement.getAttribute(VSIConstants.ATTR_TOTAL_AMOUNT); 
			String settlementId= eleSettlement.getAttribute(VSIConstants.ATTR_SETTLEMENT_ID); 
			String settlementStartDate= eleSettlement.getAttribute(VSIConstants.ATTR_SETTLEMENT_START_DATE).substring(0, 19); 
			String settlementEndDate= eleSettlement.getAttribute(VSIConstants.ATTR_SETTLEMENT_END_DATE).substring(0, 19); 
			String depositDate= eleSettlement.getAttribute(VSIConstants.ATTR_DEPOSIT_DATE).substring(0, 19); 

			createSetttlementInput= XMLUtil.createDocument(VSIConstants.ELE_VSI_SETTLEMENT);
			Element eleVSISettlement = createSetttlementInput.getDocumentElement();
			eleVSISettlement.setAttribute(VSIConstants.ATTR_SETTLEMENT_ID,settlementId);
			eleVSISettlement.setAttribute(VSIConstants.ATTR_TOTAL_AMOUNT, totalAmount);
			eleVSISettlement.setAttribute(VSIConstants.ATTR_SETTLEMENT_START_DATE, settlementStartDate);
			eleVSISettlement.setAttribute(VSIConstants.ATTR_SETTLEMENT_END_DATE, settlementEndDate);
			eleVSISettlement.setAttribute(VSIConstants.ATTR_SETTLEMENT_DEPOSIT_DATE, depositDate);
			eleVSISettlement.setAttribute(VSIConstants.ATTR_EXTN_STORE_FRONT, VSIConstants.NUTRITION_DEPOT);
			
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw e;
		} catch (YFSException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		if (log.isDebugEnabled()) {
			log.debug(this.getClass().getName()+ "Returning from createSetttlementInputDoc() : \n" 
					+ XMLUtil.getXMLString(createSetttlementInput));
		}
		
		return createSetttlementInput;
	}

}
