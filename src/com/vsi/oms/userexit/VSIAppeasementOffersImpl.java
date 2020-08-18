package com.vsi.oms.userexit;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.pca.ycd.japi.ue.YCDGetAppeasementOffersUE;
import com.yantra.shared.ycd.YCDConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDoubleUtils;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
public class VSIAppeasementOffersImpl implements YCDGetAppeasementOffersUE {
	/**
	 * get instance of logger
	 */
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIAppeasementOffersImpl.class);
	private String preferredType="FLAT_AMOUNT_ORDER";
	private boolean isPreferredTypeSet=false;
	private String sAppeasementReasonCode="";

	public Document getAppeasementOffers(YFSEnvironment env, Document inDoc) throws YFSUserExitException {

		YFCDocument dIn = YFCDocument.getDocumentFor(inDoc);
		if(dIn == null)	{
			return null;
		}
		YFCDocument dOut = YFCDocument.createDocument(YCDConstants.YCD_APPEASEMENT_OFFERS);
		YFCElement eOut = dOut.getDocumentElement();

		YFCElement eIn = dIn.getDocumentElement();
		YFCElement order=eIn.getChildElement(VSIConstants.ELE_ORDER);
		
		//Added Method to calculate the MaxAppeasementAllowedAmount and MaxAlpineAllowedAmount for 1B Customer Appeasement Module  
		updateAppeasementOut(env,inDoc,eOut);

		eOut = setAppeasementOffersForOrder(eOut,eIn);
		return dOut.getDocument();
	}

	private YFCElement setAppeasementOffersForOrder(YFCElement eOut, YFCElement eIn) {
		YFCElement eOrder = eIn.getChildElement(YCDConstants.ORDER);
		YFCElement eSelectedReason = eOrder.getChildElement(YCDConstants.YCD_APPEASEMENT_REASON);		
		sAppeasementReasonCode = eSelectedReason.getAttribute(YCDConstants.REASON_CODE);

		YFCElement eOrderIn = eIn.getChildElement(YCDConstants.ORDER);
		String sOrderHeaderKey = eOrderIn.getAttribute(YCDConstants.ORDER_HEADER_KEY);

		eOut.setAttribute(YCDConstants.ORDER_HEADER_KEY,sOrderHeaderKey);
		eOut.setAttribute(YCDConstants.ORDER_NO, eOrderIn.getAttribute(YCDConstants.ORDER_NO));

		setChargeCataegoryAndNameForAppeasementOffers(eOut);

		YFCElement eAppeasementOffers = getAppeasementOffersList(eIn);
		if(eAppeasementOffers!=null){
			Iterator iteratorAppeasementOffers = eAppeasementOffers.getChildren();
			if(iteratorAppeasementOffers!=null){
				while(iteratorAppeasementOffers.hasNext()){
					YFCElement eAppeasementOffer = (YFCElement) iteratorAppeasementOffers.next();
					eOut = setAppeasementOffer(eOrderIn, eOut,eAppeasementOffer);
				}
			}
		}
		eOut = setPreferredOffer(eOut);
		return eOut;
	}

	private YFCElement setAppeasementOffer(YFCElement eOrderIn, YFCElement eOut, YFCElement eAppeasementOffer) {
		if(log.isDebugEnabled()){
			log.verbose("Inside setAppeasementOffer");
		}
		String offerType = eAppeasementOffer.getAttribute(YCDConstants.YCD_OFFER_TYPE);
		if(offerType.equalsIgnoreCase("PERCENT_FUTURE_ORDER")){
			eOut.importNode(eAppeasementOffer);
			return eOut;
		}
		YFCElement eOrderAppeasementOffer = eAppeasementOffer.createChild(YCDConstants.ORDER);
		YFCElement eOrderLinesAppeasementOffer = eAppeasementOffer.createChild(YCDConstants.ORDER_LINES);
		eOrderAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_HEADER_OFFER_AMOUNT,"");

		setChargeCataegoryAndNameForAppeasementOffers(eOrderAppeasementOffer);

		double discountPercent=0;
		double offerAmount=0;
		String strDiscountPercent= eAppeasementOffer.getAttribute(YCDConstants.YCD_DISCOUNT_PERCENT);
		String strOfferAmount = eAppeasementOffer.getAttribute(YCDConstants.YCD_OFFER_AMOUNT);
		if(strDiscountPercent.trim()!=null && !strDiscountPercent.equalsIgnoreCase(""))
			discountPercent = Double.parseDouble(strDiscountPercent);
		if(offerType.equalsIgnoreCase("FLAT_AMOUNT_ORDER")){
			discountPercent=0.0;
		}
		if(strOfferAmount.trim()!=null  && !strOfferAmount.equalsIgnoreCase(""))
			offerAmount = YFCDoubleUtils.roundOff(Double.parseDouble(strOfferAmount),2);
		double totalLineOfferAmounts=0.0;
		YFCElement eOrderLines = eOrderIn.getChildElement(YCDConstants.ORDER_LINES);
		if(log.isDebugEnabled()){
			log.verbose("strOfferAmount="+strOfferAmount);
			log.verbose("discountPercent="+discountPercent);
			log.verbose("offerAmount="+offerAmount);
		}
		double dLineTotals = getLineTotals(eOrderLines);
		//Added Code for 1B Customer Appeasement - Start
		eOrderAppeasementOffer.setDoubleAttribute("TempTotalAmount",dLineTotals);
		eOrderAppeasementOffer.setDoubleAttribute("MaxAppeasementAllowedAmount",eOrderIn.getDoubleAttribute("MaxAppeasementAllowedAmount"));
		eOrderAppeasementOffer.setDoubleAttribute("MaxAlpineAllowedAmount",eOrderIn.getDoubleAttribute("MaxAlpineAllowedAmount"));
		eOrderAppeasementOffer.setDoubleAttribute("AlpineUserAppeaseLimit",eOrderIn.getDoubleAttribute("AlpineUserAppeaseLimit"));
		//Added Code for 1B Customer Appeasement - End
		if(log.isDebugEnabled()){
			log.verbose("Selected Line Totals dLineTotals="+dLineTotals);
		}
		
		Iterator iteratorOrderLines = eOrderLines.getChildren();
		if(iteratorOrderLines!=null){
			while(iteratorOrderLines.hasNext()){
				YFCElement eOrderLine = (YFCElement) iteratorOrderLines.next();
				YFCElement eOrderLineAppeasementOffer = eOrderLinesAppeasementOffer.createChild(YCDConstants.ORDER_LINE);
				String sOrderLineKey = eOrderLine.getAttribute(YCDConstants.ORDER_LINE_KEY);
				eOrderLineAppeasementOffer.setAttribute(YCDConstants.ORDER_LINE_KEY,sOrderLineKey);
				String sOrderLineStatus = eOrderLine.getAttribute(VSIConstants.ATTR_STATUS);
				eOrderLineAppeasementOffer.setAttribute(YCDConstants.STATUS,sOrderLineStatus);
				String sOrderedQty = eOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
				eOrderLineAppeasementOffer.setAttribute(VSIConstants.ATTR_ORD_QTY,sOrderedQty);
				YFCElement eLineOverallTotals = eOrderLine.getChildElement(YCDConstants.YCD_LINE_OVERALL_TOTALS);
				double lineGrandTotal = Double.parseDouble(eLineOverallTotals.getAttribute(YCDConstants.YCD_LINE_TOTAL));
				if(discountPercent!=0){
					double lineOfferAmount = (lineGrandTotal*0.01)*discountPercent;
					eOrderLineAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_LINE_OFFER_AMOUNT, YFCDoubleUtils.roundOff(lineOfferAmount,2));
					totalLineOfferAmounts = totalLineOfferAmounts + lineOfferAmount;
				}else if(offerType.equalsIgnoreCase("VARIABLE_AMOUNT_ORDER") || offerType.equalsIgnoreCase("VARIABLE_PERCENT_AMOUNT_ORDER")){
					eOrderLineAppeasementOffer.setDoubleAttribute("TempTotalAmount",dLineTotals);
					eOrderLineAppeasementOffer.setDoubleAttribute("TempTotalLineAmount",lineGrandTotal);
					//Added Code for 1B - Start
					eOrderLineAppeasementOffer.setDoubleAttribute("MaxAllowedAmountOnLine",eOrderLine.getDoubleAttribute("MaxAllowedAmountOnLine"));
					eOrderLineAppeasementOffer.setAttribute(VSIConstants.ATTR_IS_LINE_FULLY_REFUNDED,eOrderLine.getAttribute(VSIConstants.ATTR_IS_LINE_FULLY_REFUNDED));
					//Added Code for 1B - END
					eOrderLineAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_LINE_OFFER_AMOUNT, "0.0");
				}else{
					eOrderLineAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_LINE_OFFER_AMOUNT, "0.0");
				}

				setChargeCataegoryAndNameForAppeasementOffers(eOrderLineAppeasementOffer);
			}
		}
		
		if(log.isDebugEnabled()){
			log.verbose("Inside here...");
		}
		if(offerType.equalsIgnoreCase("FLAT_AMOUNT_ORDER")){
			if(log.isDebugEnabled()){
				log.verbose("FLAT_AMOUNT_ORDER .. Should not be here..");
			}
			eOrderAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_HEADER_OFFER_AMOUNT,YFCDoubleUtils.roundOff(offerAmount-totalLineOfferAmounts,2));
		}else if(offerType.equalsIgnoreCase("VARIABLE_AMOUNT_ORDER")){
			if(log.isDebugEnabled()){
				log.verbose("Inside VARIABLE_AMOUNT_ORDER..");
				log.verbose("Inside VARIABLE_AMOUNT_ORDER ..totalLineOfferAmounts"+totalLineOfferAmounts);
				log.verbose("Inside VARIABLE_AMOUNT_ORDER ..offerAmount"+offerAmount);
			}

			eOrderAppeasementOffer.setAttribute(YCDConstants.YCD_ORDER_HEADER_OFFER_AMOUNT,YFCDoubleUtils.roundOff(offerAmount-totalLineOfferAmounts,2));
		}
		eOut.importNode(eAppeasementOffer);
		return eOut;
	}

	private double getLineTotals(YFCElement orderLines) {
		double lineGrandTotal =0.0;
		Iterator iteratorOrderLines = orderLines.getChildren();
		if(iteratorOrderLines!=null){
			while(iteratorOrderLines.hasNext()){
				YFCElement eOrderLine = (YFCElement) iteratorOrderLines.next();
				YFCElement eLineOverallTotals = eOrderLine.getChildElement(YCDConstants.YCD_LINE_OVERALL_TOTALS);
				lineGrandTotal += Double.parseDouble(eLineOverallTotals.getAttribute(YCDConstants.YCD_LINE_TOTAL));
			}
		}
		return lineGrandTotal;
	}

	private YFCElement setPreferredOffer(YFCElement eOut) {
		return eOut;
	}
	
	// Method to set the Appeasement Offers List
	private YFCElement getAppeasementOffersList(YFCElement eIn) {
		YFCElement eAppeasementOffers =  YFCDocument.createDocument(YCDConstants.YCD_APPEASEMENT_OFFERS).getDocumentElement();
		YFCElement eOrderIn = eIn.getChildElement(YCDConstants.ORDER);
		YFCElement eOverallTotalIn = eOrderIn.getChildElement(YCDConstants.YCD_OVERALL_TOTALS);
		double orderGrandTotal = Double.parseDouble(eOverallTotalIn.getAttribute(YCDConstants.YCD_GRAND_TOTAL));

		Properties propertyMap = new Properties();
		try {
			propertyMap.load(VSIAppeasementOffersImpl.class.getResourceAsStream("/resources/ycd_appeasement_variable.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		String showVariableOffer=propertyMap.getProperty("VARIABLE_AMOUNT_ORDER");
		String showVariablePercentOffer=propertyMap.getProperty("VARIABLE_PERCENT_AMOUNT_ORDER");
		
		if(VSIConstants.FLAG_Y.equalsIgnoreCase(showVariablePercentOffer)){
			eAppeasementOffers = createAppeasementOffer(eAppeasementOffers,"VARIABLE_PERCENT_AMOUNT_ORDER",0,orderGrandTotal);
		}
		
		if(VSIConstants.FLAG_Y.equalsIgnoreCase(showVariableOffer))
			eAppeasementOffers = createAppeasementOffer(eAppeasementOffers,"VARIABLE_AMOUNT_ORDER",0,orderGrandTotal);

		return eAppeasementOffers;
	}

	private YFCElement createAppeasementOffer(YFCElement eAppeasementOffers, String sOfferType, double iDiscountPercent, double orderGrandTotal) {
		YFCElement eAppeasementOffer =  YFCDocument.createDocument(YCDConstants.YCD_APPEASEMENT_OFFER).getDocumentElement();
		eAppeasementOffer.setAttribute(YCDConstants.YCD_OFFER_TYPE,sOfferType);
		if(iDiscountPercent!=0){
			eAppeasementOffer.setAttribute(YCDConstants.YCD_DISCOUNT_PERCENT,iDiscountPercent);
			double offerAmount = 0.0;
			if(!sOfferType.equalsIgnoreCase("PERCENT_FUTURE_ORDER")){

				if(sOfferType.equalsIgnoreCase("FLAT_AMOUNT_ORDER")){
					offerAmount = iDiscountPercent;	// this is the amount
				}else{
					offerAmount = (iDiscountPercent*0.01)*(orderGrandTotal);
				}
			}
			eAppeasementOffer.setAttribute(YCDConstants.YCD_OFFER_AMOUNT,YFCDoubleUtils.roundOff(offerAmount,2));
		}else{
			eAppeasementOffer.setAttribute(YCDConstants.YCD_DISCOUNT_PERCENT,"");
			if(sOfferType.equalsIgnoreCase("FLAT_AMOUNT_ORDER")){
				eAppeasementOffer.setAttribute(YCDConstants.YCD_OFFER_AMOUNT,YFCDoubleUtils.roundOff(10.00,2));
			}else{
				eAppeasementOffer.setAttribute(YCDConstants.YCD_OFFER_AMOUNT,"");
			}
		}
		if(YFCCommon.isVoid(preferredType)){
			preferredType="FLAT_AMOUNT_ORDER";
		}
		if(sOfferType.equalsIgnoreCase(preferredType)&&!isPreferredTypeSet){
			eAppeasementOffer.setAttribute(YCDConstants.YCD_PREFERRED,VSIConstants.FLAG_Y);
			isPreferredTypeSet=true;
		}
		else
			eAppeasementOffer.setAttribute(YCDConstants.YCD_PREFERRED,VSIConstants.FLAG_N);
		eAppeasementOffers.importNode(eAppeasementOffer);
		return eAppeasementOffers;
	}

	private void setChargeCataegoryAndNameForAppeasementOffers(YFCElement eOut) {
		eOut.setAttribute(YCDConstants.YCD_CHARGE_CATEGORY,VSIConstants.STR_CHARGE_CAT_CUSTOMER_SATISFACTION);
		eOut.setAttribute(YCDConstants.YCD_CHARGE_NAME,sAppeasementReasonCode);
	}

	/**
	 * updateAppeasementOut()
	 * 
	 * @param env
	 * @param inDoc
	 * @param eOut
	 */
	private void updateAppeasementOut(YFSEnvironment env, Document inDoc, YFCElement eOut) throws YFSUserExitException {
		try {
			Element eleInput = inDoc.getDocumentElement();
			Element eleOrder = SCXmlUtil.getChildElement(eleInput,VSIConstants.ELE_ORDER);
			//getting the reason code
			Element eleAppeasementReason = SCXmlUtil.getChildElement(eleOrder,VSIConstants.ELE_APPEASEMENT_REASON);
			String strAppeasementReasonCode = eleAppeasementReason.getAttribute(VSIConstants.ATTR_REASON_CODE);

			String strOrderHeaderKey = eleOrder.getAttribute(YCDConstants.ORDER_HEADER_KEY);
			String strDisplayStatus = eleOrder.getAttribute(VSIConstants.ATTR_DISPLAY_STATUS);

			Document getCompleteOrderDetailsInDoc = null;
			Document getOrderInvoiceListInDoc = null;

			boolean isAlpineUser = VSIConstants.FLAG_Y.equals(eleInput.getAttribute(VSIConstants.ATTR_IS_ALPINE_USER)) ? true : false;
			boolean isShippingAppeasement = VSIConstants.STR_APPEASEMENT_REASON_SHIPPING_APPEASEMENT.equals(strAppeasementReasonCode);
			boolean isTaxIssueAppeasement = VSIConstants.STR_APPEASEMENT_REASON_TAX_ISSUE.equals(strAppeasementReasonCode);

			// Below 2 variables are used for Alpine User flow
			double dAlpineAllowedAppeaseAmt = 0.0;
			double dTotalExistingAppeasementAmt = 0.0;
			// Max Amount allowed for the Appeasement for the selected Appeasement Reason
			double dMaxAppeasementAllowedAmount = 0.0;

			Map<String,Double> lineCustSatisfactionChargeListHashMap = new HashMap<String,Double>();
			Map<String,Double> returnOLKAmountHashMap = new HashMap<String,Double>();

			// Is User is Alpine User
			if(isAlpineUser){
				String strAlpineAllowedAmt = VSIUtils.getCommonCodeLongDescriptionByCodeValue(env,VSIConstants.ENT_VSI, VSIConstants.STR_CODE_NAME_APPEASE_LIMIT);
				if(strAlpineAllowedAmt.length() > 0){
					dAlpineAllowedAppeaseAmt = Double.parseDouble(strAlpineAllowedAmt);
					SCXmlUtil.setAttribute(eleOrder, "AlpineUserAppeaseLimit", dAlpineAllowedAppeaseAmt);
				}
			}

			// If reason code is Shipping Appeasement then we have to consider header charges and header taxes for allowed amount.
			if(isShippingAppeasement){
				//Header Level Charges
				double dShippingChargeAmt = 0.0;
				//Iterate Header Charges to fetch Shipping Charge Amount
				Element eleHeaderCharges = SCXmlUtil.getChildElement(eleOrder,VSIConstants.ELE_HEADER_CHARGES);
				ArrayList<Element> arrListHeaderCharge = SCXmlUtil.getChildren(eleHeaderCharges,VSIConstants.ELE_HEADER_CHARGE);
				for(Element eleHeaderCharge : arrListHeaderCharge){
					String strChargeCategory = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
					if(VSIConstants.SHIPPING_CHARGE_CTGY.equals(strChargeCategory)){
						dShippingChargeAmt = SCXmlUtil.getDoubleAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT);
					}
				}

				double dShippingTaxAmt = 0.0;
				//Iterate Header Taxes to fetch Shipping Tax Amount
				Element eleHeaderTaxes = SCXmlUtil.getChildElement(eleOrder,VSIConstants.ELE_HEADER_TAXES);
				ArrayList<Element> arrListHeaderTax = SCXmlUtil.getChildren(eleHeaderTaxes,VSIConstants.ELE_HEADER_TAX);
				for(Element eleHeaderTax : arrListHeaderTax){
					String strTaxName = eleHeaderTax.getAttribute(VSIConstants.ATTR_TAX_NAME);
					if(VSIConstants.SHIPPING_TAX.equals(strTaxName)){
						dShippingTaxAmt = SCXmlUtil.getDoubleAttribute(eleHeaderTax,VSIConstants.ATTR_TAX);
					}
				}
				dMaxAppeasementAllowedAmount = dShippingChargeAmt + dShippingTaxAmt;
			}//end of If Shipping Appeasement
			
			//If any of the order line status is when till Return then we need to check Return Orders also, need to call getCompleteOrderDetails
			else if(strDisplayStatus.contains(VSIConstants.STR_RETURN)) {
				getCompleteOrderDetailsInDoc = SCXmlUtil.createDocument(YCDConstants.ORDER);
				Element eleCompleteOrderDetailsInput = getCompleteOrderDetailsInDoc.getDocumentElement();
				eleCompleteOrderDetailsInput.setAttribute(YCDConstants.ORDER_HEADER_KEY,strOrderHeaderKey);

				//getCompleteOrderDetails API Call
				Document getCompleteOrderDetailsOutDoc = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_COMPLETE_ORDER_DETAILS,VSIConstants.API_GET_COMPLETE_ORDER_DETAILS, getCompleteOrderDetailsInDoc);

				//Iterate the Return Order Lines of Return Order
				Element eleCompleteOrderDetails = getCompleteOrderDetailsOutDoc.getDocumentElement();
				Element eleReturnOrdersList = SCXmlUtil.getChildElement(eleCompleteOrderDetails,VSIConstants.ELE_RETURN_ORDERS);
				ArrayList<Element> arrListReturnOrder = SCXmlUtil.getChildren(eleReturnOrdersList, VSIConstants.ELE_RETURN_ORDER);
				double dAdjustedAmount = 0.0;
				Double dExistingAdjustedAmount = 0.0;
				for(Element eleReturnOrder : arrListReturnOrder){
					Element eleReturnOrderLinesList = SCXmlUtil.getChildElement(eleReturnOrder,VSIConstants.ELE_ORDER_LINES);
					ArrayList<Element> arrListReturnOrderLine = SCXmlUtil.getChildren(eleReturnOrderLinesList,VSIConstants.ELE_ORDER_LINE);
					for(Element eleReturnOrderLine : arrListReturnOrderLine){
						String strSalesOrderLineKey = eleReturnOrderLine.getAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY);
						// Need to throw new YFSUserExitException(e.getMessage());check if Reason code is Tax issue then consider only Sales Tax from Line Tax 
						//else consider the LineTotalAmount without tax from Return Order Line
						if(VSIConstants.STR_APPEASEMENT_REASON_TAX_ISSUE.equalsIgnoreCase(strAppeasementReasonCode)){
							Element eleROLineTaxesList = SCXmlUtil.getChildElement(eleReturnOrderLine,VSIConstants.ELE_LINE_TAXES);
							ArrayList<Element> arrListROLineTax = SCXmlUtil.getChildren(eleROLineTaxesList,VSIConstants.ELE_LINE_TAX);
							double dROSalesTax = 0.0;
							for(Element eleROLineTax : arrListROLineTax){
								if(VSIConstants.STR_SALES_TAX.equalsIgnoreCase(eleROLineTax.getAttribute(VSIConstants.ATTR_TAX_NAME))){
									dROSalesTax = SCXmlUtil.getDoubleAttribute(eleROLineTax,VSIConstants.ATTR_TAX);
								}
							}
							
							dAdjustedAmount = dROSalesTax + dAdjustedAmount;
						}else{
							Element eleROLineInvoicedTotals = SCXmlUtil.getChildElement(eleReturnOrderLine,VSIConstants.ELE_LINE_INVOICED_TOTALS);
							double dROLineTotal = SCXmlUtil.getDoubleAttribute(eleROLineInvoicedTotals,VSIConstants.ATTR_LINE_TOTAL);
							double dROTax = SCXmlUtil.getDoubleAttribute(eleROLineInvoicedTotals,VSIConstants.ATTR_TAX);
							
							dAdjustedAmount = dROLineTotal -  dROTax ; 
						}	
						// get the already return amount(if any) from the map
						dExistingAdjustedAmount = returnOLKAmountHashMap.get(strSalesOrderLineKey);
						if(null != dExistingAdjustedAmount){
							dAdjustedAmount = dAdjustedAmount + dExistingAdjustedAmount ;
						}						
						returnOLKAmountHashMap.put(strSalesOrderLineKey, dAdjustedAmount);
					}
				}
			}//If Display Status Condition Check End

			//getOrderInvoiceList API call to get the appliedAppeasment Amount on Line
			getOrderInvoiceListInDoc = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_INVOICE);
			Element eleGetOrderInvoiceListInput = getOrderInvoiceListInDoc.getDocumentElement();
			eleGetOrderInvoiceListInput.setAttribute(YCDConstants.ORDER_HEADER_KEY, strOrderHeaderKey);
			eleGetOrderInvoiceListInput.setAttribute(VSIConstants.ATTR_INVOICE_TYPE,VSIConstants.STR_INFO);

			Document getOrderInvoiceListOutDoc = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_ORDER_INVOICE_LIST,VSIConstants.API_GET_ORDER_INVOICE_LIST, getOrderInvoiceListInDoc);

			//Get the applied appeased amount and put it into map by using getOrderInvoiceList API Output 
			if(!YFCObject.isVoid(getOrderInvoiceListOutDoc)){
				Element eleOrderInvocieList = getOrderInvoiceListOutDoc.getDocumentElement();
				ArrayList<Element> arrListOrderInvoice = SCXmlUtil.getChildren(eleOrderInvocieList,VSIConstants.ELE_ORDER_INVOICE);
				//Iterate the getOrderInvoiceList 
				for(Element eleOrderInvoice : arrListOrderInvoice){
					if(isShippingAppeasement || isAlpineUser){
						// Shipping Appeasement calculation
						Element eleHeaderChargeList = SCXmlUtil.getChildElement(eleOrderInvoice,VSIConstants.ELE_HEADER_CHARGE_LIST);
						Element eleHeaderCharge = SCXmlUtil.getChildElement(eleHeaderChargeList, VSIConstants.ELE_HEADER_CHARGE);
						if(null != eleHeaderCharge){
							double dChargeAmount = SCXmlUtil.getDoubleAttribute(eleHeaderCharge,VSIConstants.ATTR_CHARGE_AMOUNT);
							dTotalExistingAppeasementAmt = dTotalExistingAppeasementAmt + dChargeAmount;
							if(isShippingAppeasement){
								dMaxAppeasementAllowedAmount = dMaxAppeasementAllowedAmount - dChargeAmount;
							}
						}
					} 

					if(!isShippingAppeasement || isAlpineUser){
						Element eleLineDetailsList = SCXmlUtil.getChildElement(eleOrderInvoice, VSIConstants.ELE_LINE_DETAILS);
						ArrayList<Element> arrListLineDetail = SCXmlUtil.getChildren(eleLineDetailsList,VSIConstants.ELE_LINE_DETAIL);
						for(Element eleLineDetail : arrListLineDetail){
							String strOrderLineKey = eleLineDetail.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							Double dLineChargeAmount = lineCustSatisfactionChargeListHashMap.get(strOrderLineKey);
							// If Tax issue is selected then line charge amount will be consider from Invoice line taxes 
							if(isTaxIssueAppeasement || isAlpineUser){
								Element eleLineTaxList = SCXmlUtil.getChildElement(eleLineDetail,VSIConstants.ELE_LINE_TAX_LIST);
								if(null != eleLineTaxList){
									ArrayList<Element> arrListLineTax = SCXmlUtil.getChildren(eleLineTaxList,VSIConstants.ELE_LINE_TAX);
									// Iteration of Taxes on Line
									for(Element eleLineTax : arrListLineTax){
										String strTaxName = eleLineTax.getAttribute(VSIConstants.ATTR_TAX_NAME);
										if(VSIConstants.STR_SALES_TAX.equals(strTaxName)){
											if(null == dLineChargeAmount){
												dLineChargeAmount = 0.0;
											}
											double dTax = SCXmlUtil.getDoubleAttribute(eleLineTax,VSIConstants.ATTR_TAX);
											dTotalExistingAppeasementAmt = dTotalExistingAppeasementAmt + dTax;
											if(isTaxIssueAppeasement){
												dLineChargeAmount = dLineChargeAmount + dTax;
											}
										}
									}
								}
							} 

							if(!isTaxIssueAppeasement || isAlpineUser) { 
								Element eleLineChargeList = SCXmlUtil.getChildElement(eleLineDetail,VSIConstants.ELE_LINE_CHARGE_LIST);
								ArrayList<Element> arrListLineCharge = SCXmlUtil.getChildren(eleLineChargeList,VSIConstants.ELE_LINE_CHARGE);
								// Iteration of Charges on Line
								for(Element eleLineCharge : arrListLineCharge){
									String strChargeCategory = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
									if(VSIConstants.STR_CHARGE_CAT_CUSTOMER_SATISFACTION.equals(strChargeCategory)){
										if(null == dLineChargeAmount){
											dLineChargeAmount = 0.0;
										}
										double dChargePerLine = SCXmlUtil.getDoubleAttribute(eleLineCharge,VSIConstants.ATTR_CHARGE_PER_LINE);
										dTotalExistingAppeasementAmt = dTotalExistingAppeasementAmt + dChargePerLine;
										if(!isTaxIssueAppeasement){
											dLineChargeAmount = dLineChargeAmount + dChargePerLine;
										}
									}
								}
							}
							if(!isShippingAppeasement){
								//Putting values into orderLineCustSatisfactionChargeListMap as [key:orderLineKey, value:totalchargeAmt as appliedAppeasedamount]
								lineCustSatisfactionChargeListHashMap.put(strOrderLineKey, dLineChargeAmount);
							}										
						}
					}
				}
			}
			double dMaxTaxIssueAllowed = 0.0;
			if(!isShippingAppeasement){
				//Iterate OrderLines from the UE Input
				Element eleOrderLinesList = SCXmlUtil.getChildElement(eleOrder,VSIConstants.ELE_ORDER_LINES);
				ArrayList<Element> arrListOrderLine = SCXmlUtil.getChildren(eleOrderLinesList,VSIConstants.ELE_ORDER_LINE);
				for(Element eleOrderLine : arrListOrderLine){
					double dMaxAllowedAmountOnLine = 0.0;
					String strOrderLineKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
					Element eleLineOverallTotals = SCXmlUtil.getChildElement(eleOrderLine,VSIConstants.ELE_LINE_OVERALL_TOTALS);
					double dLineTotal = SCXmlUtil.getDoubleAttribute(eleLineOverallTotals,VSIConstants.ATTR_LINE_TOTAL);
					double dLineTax = SCXmlUtil.getDoubleAttribute(eleLineOverallTotals,VSIConstants.ATTR_TAX);
					
					double dOrderLineTotal = dLineTotal - dLineTax ;

					// Total Refunded/Returned Amount on the Line 
					Double dReturnedAmount = returnOLKAmountHashMap.get(strOrderLineKey);
					if(null == dReturnedAmount){
						dReturnedAmount = 0.0;
					}

					Double dAppliedAppeasedAmtOnLine = lineCustSatisfactionChargeListHashMap.get(strOrderLineKey);
					if(null == dAppliedAppeasedAmtOnLine){
						dAppliedAppeasedAmtOnLine = 0.0;
					}

					// To calculate TotalRefunded amount, Add total AppliedAppeasmentAmount and ReturnedAmount of Line
					double dTotalRefundAmt = dReturnedAmount + dAppliedAppeasedAmtOnLine ;

					if(isTaxIssueAppeasement){
						dMaxTaxIssueAllowed = dMaxTaxIssueAllowed + (dLineTax - dTotalRefundAmt);
					}
					
					if(dOrderLineTotal > dTotalRefundAmt){
						// dMaxAllowedAmountOnLine is valid balanced amount which will be used to apply Appeasement on Line
						dMaxAllowedAmountOnLine = dOrderLineTotal - dTotalRefundAmt ;
					}else{
						eleOrderLine.setAttribute(VSIConstants.ATTR_IS_LINE_FULLY_REFUNDED,VSIConstants.FLAG_Y);
					}

					dMaxAppeasementAllowedAmount = dMaxAppeasementAllowedAmount + dMaxAllowedAmountOnLine;

					//dMaxAllowedAmountOnLine is allowed amount on Selected Lines and set it at OrderLine level in UE input.					
					SCXmlUtil.setAttribute(eleOrderLine, "MaxAllowedAmountOnLine", dMaxAllowedAmountOnLine);
				}// end of Iteration of UEInput orderLines
			}
			
			if(isAlpineUser){
				dAlpineAllowedAppeaseAmt = dAlpineAllowedAppeaseAmt - dTotalExistingAppeasementAmt;
				if(dAlpineAllowedAppeaseAmt < 0.0){
					dAlpineAllowedAppeaseAmt = 0.0;
				}
				SCXmlUtil.setAttribute(eleOrder, "MaxAlpineAllowedAmount", dAlpineAllowedAppeaseAmt);
				SCXmlUtil.setAttribute(eleOrder,VSIConstants.ATTR_IS_ALPINE_USER,VSIConstants.FLAG_Y);
			}
			if(isTaxIssueAppeasement) {
				dMaxAppeasementAllowedAmount = dMaxTaxIssueAllowed;
			}
			//dMaxAppeasementAllowedAmount is total of dMaxAllowedAmountOnLine of Selected Lines and set it at Order level in UE input. 
			SCXmlUtil.setAttribute(eleOrder, "MaxAppeasementAllowedAmount", dMaxAppeasementAllowedAmount);

		} catch (YFSException e) {
			e.printStackTrace();
			throw new YFSUserExitException(e.getMessage());
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSUserExitException(e.getMessage());
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSUserExitException(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new YFSUserExitException(e.getMessage());
		}
	}
}
