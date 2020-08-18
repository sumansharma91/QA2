package com.vsi.oms.userexit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineChargeStruct;
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;

public class VSIRecalculateLineTaxUEImplNew implements YFSRecalculateLineTaxUE,VSIConstants{

	YIFApi api;
	private final static YFCLogCategory log = YFCLogCategory.instance(VSIRecalculateLineTaxUEImplNew.class);
	public YFSExtnTaxCalculationOutStruct recalculateLineTax(
			YFSEnvironment env,
			YFSExtnLineTaxCalculationInputStruct taxCalculationInput)
	throws YFSUserExitException {
		YFSExtnTaxCalculationOutStruct taxCalculationOutput = new YFSExtnTaxCalculationOutStruct();
		
		if(taxCalculationInput.enterpriseCode.equals(ENT_MCL)){
			
			taxCalculationOutput.colTax = taxCalculationInput.colTax;
			taxCalculationOutput.tax = taxCalculationInput.tax;
			return taxCalculationOutput;
		}
		
		taxCalculationOutput.colTax = taxCalculationInput.colTax;
		String orderLineKey = taxCalculationInput.orderLineKey;
		String strProgId = env.getProgId();
		if(log.isDebugEnabled()){
			log.debug("progid"+strProgId);
		}
		String orderHeaderKey = taxCalculationInput.orderHeaderKey;
		try {

			api = YIFClientFactory.getInstance().getApi();
			Document inDocGetOrderList = XMLUtil.createDocument("Order");
			Element eGetOrderlist = inDocGetOrderList.getDocumentElement();
			eGetOrderlist.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			env.setApiTemplate("getOrderList", "global/template/api/getOrderList_LineTaxUE.xml");
			Document outGetOrderList = api.invoke(env, "getOrderList", inDocGetOrderList);
			env.clearApiTemplate("getOrderList");
			
			String entryType="";
			
			String reshipParentLineKey= null;
			Element orderElement = SCXmlUtil.getChildElement(outGetOrderList.getDocumentElement(), VSIConstants.ELE_ORDER);
			if(!YFCObject.isVoid(orderElement) && "MigratedOrder".equals(orderElement.getAttribute("OrderName")))
			{
				taxCalculationOutput.colTax = taxCalculationInput.colTax;
				taxCalculationOutput.tax = taxCalculationInput.tax;
				return taxCalculationOutput;
			}
			Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			for(Element eleOrderLine1:alOrderLines)
			{
				String orgorderLineKey = eleOrderLine1.getAttribute("OrderLineKey");
				if(orgorderLineKey.equals(orderLineKey))
				{
					 reshipParentLineKey=eleOrderLine1.getAttribute("ReshipParentLineKey");
					 break;
				}
				
			}
			//System.out.println("reship line key is"+reshipParentLineKey);
			if(!YFCCommon.isVoid(reshipParentLineKey))
			{
				taxCalculationOutput.colTax = taxCalculationInput.colTax;
				taxCalculationOutput.tax = taxCalculationInput.tax;
				return taxCalculationOutput;
			}

			if (!"0".equals(outGetOrderList.getDocumentElement().getAttribute("TotalOrderList"))){
				Element eOutGetOrderList = outGetOrderList.getDocumentElement();
				Element eOrder = (Element) eOutGetOrderList.getElementsByTagName("Order").item(0);
				entryType = eOrder.getAttribute("EntryType");
				
			}

			if(taxCalculationInput.documentType.equalsIgnoreCase("0003"))
			{
				taxCalculationOutput.colTax = taxCalculationInput.colTax;
				taxCalculationOutput.tax = taxCalculationInput.tax;
				return taxCalculationOutput;
			}
			else if (taxCalculationInput.bForInvoice && ("SHIPMENT").equalsIgnoreCase(taxCalculationInput.invoiceMode)) 
			{
				if(log.isDebugEnabled()){
					log.debug("Invoice mode :"+taxCalculationInput.invoiceMode);
				}
				Double totalProratedLineTax = 0.0;
				ArrayList<YFSExtnTaxBreakup> invoiceTaxes = new ArrayList<YFSExtnTaxBreakup>();
				if (taxCalculationInput.colTax != null) {
					
					if(taxCalculationInput.bForInvoice){
					
						List<YFSExtnTaxBreakup> orderLineTaxes = taxCalculationInput.colTax;
						if (taxCalculationInput.bLastInvoiceForOrderLine) {
	
							for (YFSExtnTaxBreakup extnTaxBreakup : orderLineTaxes) {
								Double invoicedTax = extnTaxBreakup.invoicedTax;
								Double remainingProratedTax = extnTaxBreakup.tax-invoicedTax;
								totalProratedLineTax = totalProratedLineTax+remainingProratedTax;
								extnTaxBreakup.tax = remainingProratedTax.doubleValue();
								invoiceTaxes.add(extnTaxBreakup);
							}
						} else {
	
							for (YFSExtnTaxBreakup extnTaxBreakup : orderLineTaxes) {
								Double unitTax = 0.00;
								Double lineTax = 0.00;
								Double currentLineQty = taxCalculationInput.currentQty;
								Document docTaxExtendedFields = extnTaxBreakup.eleExtendedFields;
								Element eleTaxExtendedFields = docTaxExtendedFields.getDocumentElement();
								Element eleExtn = SCXmlUtil.getChildElement(eleTaxExtendedFields, ELE_EXTN);
								unitTax = SCXmlUtil.getDoubleAttribute(eleExtn, "ExtnTaxPerUnit");
								lineTax = currentLineQty*unitTax;
								totalProratedLineTax = totalProratedLineTax+lineTax;
								extnTaxBreakup.tax = lineTax.doubleValue();
								invoiceTaxes.add(extnTaxBreakup);
							}
						}
					}else{
						taxCalculationOutput.colTax = taxCalculationInput.colTax;
						taxCalculationOutput.tax = taxCalculationInput.tax;
						return taxCalculationOutput;
					}
				}
				taxCalculationOutput.colTax = invoiceTaxes;
				taxCalculationOutput.tax = totalProratedLineTax.doubleValue();
				return taxCalculationOutput;

			} else if ((strProgId.equals("ISCCSSYS001")||(taxCalculationInput.bForInvoice && 
					("PRICE_CHANGE").equalsIgnoreCase(taxCalculationInput.invoiceMode))|| "SCHEDULE.0001".equals(strProgId) || "SCHEDULE_BO.0001".equals(strProgId)))
			{

				if(log.isDebugEnabled()){
					log.debug("InPRICE CHANGE | ISCCSSYS001 | SCHEDULE.0001");
				}
				String isTaxComplete = (String) env
				.getTxnObject("IS_TAX_CALC_COMPLETED");
				if(log.isDebugEnabled()){
					log.debug("IS_TAX_CALC_COMPLETED"+isTaxComplete);
				}
				String isTaxSuccessful = (String) env
				.getTxnObject("TAX_CALC_SUCCESSFUL");
				if(log.isDebugEnabled()){
					log.debug("TAX_CALC_SUCCESSFUL"+isTaxSuccessful);
				}
				// If this recalculate line tax ue was invoked because of a
				// changeOrder call from the tax hold monitor to update the
				// taxes, don't call taxes again.


				if (isTaxComplete == null) {
					Document inOrdDoc = XMLUtil.createDocument("Order");

					Element inOrderEle = inOrdDoc.getDocumentElement();
					inOrderEle.setAttribute("OrderHeaderKey", orderHeaderKey);
					api = YIFClientFactory.getInstance().getApi();
					env.setApiTemplate("getOrderDetails", "global/template/api/VSI_getOrderDetails_Tax.xml");

					Document orderDetails = api.invoke(env, "getOrderDetails",inOrdDoc);

					env.clearApiTemplate("getOrderDetails");
					// This will be used in Schedule Order Event to avoid an
					// additional API call to get order details
					env.setTxnObject("Order", orderDetails);
					List<Element> orderLineList = XMLUtil
					.getElementListByXpath(orderDetails,
					"Order/OrderLines/OrderLine");

					Element orderDetailsEle = orderDetails.getDocumentElement();



					try {
						// Need this null check?
						// Get taxes:

						Document taxedDocument = api
						.executeFlow(env, "VSIVertexTaxCallService",
								orderDetails);

						List<Element> taxedOrderLineList = XMLUtil
						.getElementListByXpath(taxedDocument,
						"Order/OrderLines/OrderLine");
						String trIsSuccess = XMLUtil.getAttributeFromXPath(
								taxedDocument, "Order/Extn/@ExtnIsTaxCalculated");

						if (taxedDocument != null) {
							Element taxedDocumentEle = taxedDocument
							.getDocumentElement();

							if ((trIsSuccess.equalsIgnoreCase("Y"))) {

								env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
								if(log.isDebugEnabled()){
									log.debug("IS_TAX_CALC_COMPLETED : Y");
								}
								env.setTxnObject("TAX_CALC_SUCCESSFUL", "Y");
								if(log.isDebugEnabled()){
									log.debug("TAX_CALC_SUCCESSFUL : Y");
								}
								env.setTxnObject("TAX_CHANGE_ORDER_XML",
										taxedDocument);
								if(log.isDebugEnabled()){
									log.debug("TAX_CHANGE_ORDER_XML" +XMLUtil.getXMLString(taxedDocument));
								}
								for (int p = 0; p < taxedOrderLineList.size(); p++) {
									Element currentOrderLine = taxedOrderLineList
									.get(p);
									Double totalLineTax = 0.0;
									if (currentOrderLine.getAttribute(
									"OrderLineKey").equalsIgnoreCase(
											taxCalculationInput.orderLineKey)) {
										// Get All the line tax elements:
										List<Element> lineTaxElements = XMLUtil
										.getElementsByTagName(
												currentOrderLine,
										"LineTax");
										// set the output tax struct
										List<YFSExtnTaxBreakup> orderLineTaxes = new ArrayList<YFSExtnTaxBreakup>();
										for (int i = 0; i < lineTaxElements
										.size(); i++) {
											Element currentLineTaxEle = lineTaxElements
											.get(i);
											// Get tax determination date:
											BigDecimal tax = new BigDecimal(
													currentLineTaxEle
													.getAttribute("Tax"));
											tax = tax.setScale(2,BigDecimal.ROUND_HALF_UP);

											BigDecimal taxPercentage = new BigDecimal(
													currentLineTaxEle
													.getAttribute("TaxPercentage"));
											taxPercentage = taxPercentage
											.setScale(
													10,
													RoundingMode.HALF_UP);

											YFSExtnTaxBreakup newBreakup = new YFSExtnTaxBreakup();
											// Set that tax was successful for
											// future hold analysis
											newBreakup.reference1 = "Y";

											newBreakup.reference2 = currentLineTaxEle
											.getAttribute("Reference_2");

											newBreakup.tax = tax.doubleValue();
											totalLineTax = totalLineTax+tax.doubleValue();
											
											/*
											Ideally for credit memo code will never enter this loop
											but this is added for safety. For credit memo, this UE is invoked twice,
											First with Invoice mode and second with invoice mode.
											When it is non invoice mode, tax should be just returned as is and no change
											is required. When UE is invoked 2nd time, then we need to return prorated tax
											in the output to add in the invoice.
											*/
											DecimalFormat dfRMUp = new DecimalFormat(DEC_FORMAT);
											boolean hasTaxAdjustments = false;
											YFSExtnTaxBreakup extnTaxBreakupAdjustment = null;
											if(taxCalculationInput.bForInvoice && 
													("PRICE_CHANGE").equalsIgnoreCase(taxCalculationInput.invoiceMode)){
												
												Double dblInvoicedCharge = 0.0;
												List<YFSExtnLineChargeStruct> listChargeBreakup = taxCalculationInput.colCharge;
												//Loop through the charge breakup list to find the charge amount.
												for (YFSExtnLineChargeStruct extnChargeBreakup: listChargeBreakup){	        		
													
													 dblInvoicedCharge = dblInvoicedCharge + extnChargeBreakup.chargeAmount;
													}
												//To calculate the tax refund going into invoice, multiply charge amount
												//by tax percentage.
												
												double dbTax = taxCalculationInput.tax;
												//System.out.println("strTax"+dbTax);
												
												List<YFSExtnTaxBreakup> listTaxBreakup = taxCalculationInput.colTax;
												//Loop through the tax breakup list to find the tax amount.
												double dbInvoicedTax = 0.0;
												String strChargeCtg = "";
												String strChargeName = "";
												for (YFSExtnTaxBreakup extnTaxBreakup: listTaxBreakup){
													strChargeCtg = extnTaxBreakup.chargeCategory;
													strChargeName = extnTaxBreakup.chargeName;
													if(DISCOUNT_CATEGORY_ADJ.equals(strChargeCtg)
															&& STR_APPEASEMENT_REASON_TAX_ISSUE.equals(strChargeName)){
														hasTaxAdjustments = true;
														dbInvoicedTax = extnTaxBreakup.invoicedTax;
														dbInvoicedTax = dbInvoicedTax + extnTaxBreakup.tax;
														extnTaxBreakupAdjustment = extnTaxBreakup;
													}
												}
												//System.out.print("dbInvoicedTax"+dbInvoicedTax);
												
												double taxToRefund = -(taxPercentage.doubleValue()*0.01*dblInvoicedCharge);
												//System.out.println("value is 1"+Double.valueOf(dfRMUp.format(taxToRefund)));
												//System.out.println("currentLine 1"+SCXmlUtil.getString(currentOrderLine));
												//System.out.println("currentLineTaxEle 1"+SCXmlUtil.getString(currentLineTaxEle));
												//System.out.println("invoiced tax 1"+SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax"));
												/*if (YFCObject.isDoubleVoid(taxToRefund) && !hasTaxAdjustments) {
													newBreakup.tax = -Double.valueOf(dfRMUp.format(SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax")));
												} else {
													newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));
												}*/
												
												//OMS-1170 Negative tax issue : Start
												double dInvoicedTaxFromLine=SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax",0.00);
												if(dInvoicedTaxFromLine<=0)
													taxToRefund=0.0;
												else if(Math.abs(taxToRefund)>dInvoicedTaxFromLine){
													taxToRefund=-dInvoicedTaxFromLine;
												}
												//OMS-1170 :End
												//OMS-1162 : start
												else if(taxPercentage.doubleValue()==0 && dInvoicedTaxFromLine>0) {
													taxToRefund=-dInvoicedTaxFromLine;
													if(log.isDebugEnabled()){
														log.debug("Tax is 0.0 , refunding the invoiced tax"+taxToRefund);
													}
												}
												//OMS-1162 : end	
												newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));
												
												
											}
											newBreakup.taxPercentage = taxPercentage
											.doubleValue();
											newBreakup.taxName = currentLineTaxEle
											.getAttribute("TaxName");
											//Fix for correcting the tax flag based on the Tax type
											//newBreakup.taxableFlag = "Y";

											//Setting extended fields
											Element eleOrderExtn = XMLUtil.getElementByXPath(taxedDocument, "Order/Extn");
											Element eleOrderLineExtn = (Element)currentLineTaxEle.getElementsByTagName("Extn").item(0);
											String strTaxAreaID = "";
											String strTaxPerUnit = "";
											String strTaxType = "";
											String strExtnVertexEngine = "";
											String strRuleID = "";
											String strZoneID = "";
											String strGroupID = "";
											String strTaxPerLine = "";

											if(!YFCObject.isVoid(eleOrderLineExtn)){
												strTaxAreaID = eleOrderLineExtn.getAttribute("ExtnTaxAreaID");
												strTaxPerUnit = eleOrderLineExtn.getAttribute("ExtnTaxPerUnit");
												strTaxPerLine = eleOrderLineExtn.getAttribute("ExtnRemTaxPerLine");
												strTaxType = eleOrderLineExtn.getAttribute("TaxType");
												strExtnVertexEngine = eleOrderExtn.getAttribute("ExtnVertexEngine");
												strRuleID = eleOrderLineExtn.getAttribute("RuleId");
												strZoneID = eleOrderLineExtn.getAttribute("ZoneId");
												strGroupID = eleOrderLineExtn.getAttribute("GroupId");

											}


											YFCDocument taxBreakupDoc = YFCDocument.createDocument("TaxBreakup");
											YFCElement taxBreakupEle = taxBreakupDoc.getDocumentElement();
											YFCElement extnEle = taxBreakupEle.createChild("Extn");
											if(!YFCObject.isVoid(strTaxAreaID)){
												extnEle.setAttribute("ExtnTaxAreaID", strTaxAreaID);
											}
											if(!YFCObject.isVoid(strTaxPerUnit)){
												extnEle.setAttribute("ExtnTaxPerUnit",strTaxPerUnit);
											}
											if(!YFCObject.isVoid(strTaxPerLine)){
												extnEle.setAttribute("ExtnRemTaxPerLine",strTaxPerLine);
											}
											if(!YFCObject.isVoid(strTaxType)){
												extnEle.setAttribute("TaxType", strTaxType);
											}
											if(!YFCObject.isVoid(strExtnVertexEngine)){
												extnEle.setAttribute("ExtnVertexEngine", strExtnVertexEngine);
											}
											if(!YFCObject.isVoid(strRuleID)){
												extnEle.setAttribute("RuleId", strRuleID);
											}
											if(!YFCObject.isVoid(strZoneID)){
												extnEle.setAttribute("ZoneId", strZoneID);
											}
											if(!YFCObject.isVoid(strGroupID)){
												extnEle.setAttribute("GroupId", strGroupID);
											}

											//Fix for correcting the tax flag based on the Tax type
											if((strTaxType != null && strTaxType.equalsIgnoreCase("NonTaxable")) || tax.doubleValue() == 0){
												//Fix for correcting the tax flag based on the Tax type	
												//if(strTaxType != null && strTaxType.equalsIgnoreCase("NonTaxable")){
												newBreakup.taxableFlag = "N";
											}else{
												newBreakup.taxableFlag = "Y";
											}

											extnEle.setAttribute("ExtnIsTaxCalculated",trIsSuccess);											

											Document eleExtendedFields = taxBreakupDoc.getDocument();
											newBreakup.eleExtendedFields = eleExtendedFields;

											orderLineTaxes.add(newBreakup);
											if(hasTaxAdjustments){
												orderLineTaxes.add(extnTaxBreakupAdjustment);
											}

										}
										taxCalculationOutput.colTax = orderLineTaxes;
										taxCalculationOutput.tax =totalLineTax;
										return taxCalculationOutput;

									}
								}

							} else {
								// Tax failed

								env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
								if(log.isDebugEnabled()){
									log.debug("IS_TAX_CALC_COMPLETED : Y");
								}
								env.setTxnObject("TAX_CALC_SUCCESSFUL", "N");
								if(log.isDebugEnabled()){
									log.debug("TAX_CALC_SUCCESSFUL : N");
								}
								taxCalculationOutput.colTax = taxCalculationInput.colTax;

								return taxCalculationOutput;
							}

						} else {
							env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
							if(log.isDebugEnabled()){
								log.debug("IS_TAX_CALC_COMPLETED : Y");
							}
							env.setTxnObject("TAX_CALC_SUCCESSFUL", "N");
							if(log.isDebugEnabled()){
								log.debug("TAX_CALC_SUCCESSFUL : N");
							}
							taxCalculationOutput.colTax = taxCalculationInput.colTax;

							return taxCalculationOutput;
						}
					} catch (Exception e) {

						e.printStackTrace();

						env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
						if(log.isDebugEnabled()){
							log.debug("IS_TAX_CALC_COMPLETED : Y");
						}
						env.setTxnObject("TAX_CALC_SUCCESSFUL", "N");
						if(log.isDebugEnabled()){
							log.debug("TAX_CALC_SUCCESSFUL : N");
						}
						taxCalculationOutput.colTax = taxCalculationInput.colTax;

						return taxCalculationOutput;

					}

				} else if ("Y".equalsIgnoreCase(isTaxComplete)) {
					if ("Y".equalsIgnoreCase(isTaxSuccessful)) {
						// copy taxes:201707261736362008055   
						Document taxedDoc = (Document) env
						.getTxnObject("TAX_CHANGE_ORDER_XML");
						if(log.isDebugEnabled()){
							log.debug("taxedDoc"+XMLUtil.getXMLString(taxedDoc));
						}
						List<Element> taxedOrderLineList = XMLUtil
						.getElementListByXpath(
								taxedDoc.getDocumentElement(),
						"Order/OrderLines/OrderLine");

						for (int p = 0; p < taxedOrderLineList.size(); p++) {
							Element currentOrderLine = taxedOrderLineList
							.get(p);
							if (currentOrderLine.getAttribute("OrderLineKey")
									.equalsIgnoreCase(
											taxCalculationInput.orderLineKey)) {
								// Get All the line tax elements:
								List<Element> lineTaxElements = XMLUtil
								.getElementsByTagName(currentOrderLine,
								"LineTax");
								// set the output tax struct
								LinkedList<YFSExtnTaxBreakup> orderLineTaxes = new LinkedList<YFSExtnTaxBreakup>();
								for (int i = 0; i < lineTaxElements.size(); i++) {
									Element currentLineTaxEle = lineTaxElements
									.get(i);

									BigDecimal tax = new BigDecimal(
											currentLineTaxEle.getAttribute("Tax"));
									tax = tax.setScale(2,BigDecimal.ROUND_HALF_UP);

									BigDecimal taxPercentage = new BigDecimal(
											currentLineTaxEle
											.getAttribute("TaxPercentage"));
									taxPercentage = taxPercentage.setScale(10,
											RoundingMode.HALF_UP);

									YFSExtnTaxBreakup newBreakup = new YFSExtnTaxBreakup();
									// Set that tax was successful for future
									// hold analysis
									newBreakup.reference1 = "Y";

									newBreakup.reference2 = currentLineTaxEle
									.getAttribute("Reference_2");

									newBreakup.tax = tax.doubleValue();
									
									/*
									For credit memo, this UE is invoked twice. In second invocation
									code comes into this loop as tax output is saved in the env variable.
									When UE is invoked 2nd time, then we need to return prorated tax
									in the output to add in the invoice.
									*/
									DecimalFormat dfRMUp = new DecimalFormat(DEC_FORMAT);
									boolean hasTaxAdjustments = false;
									YFSExtnTaxBreakup extnTaxBreakupAdjustment = null;
									if(taxCalculationInput.bForInvoice && 
											("PRICE_CHANGE").equalsIgnoreCase(taxCalculationInput.invoiceMode)){
										
											//reduce tax
										Double dblInvoicedCharge = 0.0;
										List<YFSExtnLineChargeStruct> listChargeBreakup = taxCalculationInput.colCharge;
										//Loop through the charge breakup list to find the charge amount.
										String strChargeCategory = "";
										for (YFSExtnLineChargeStruct extnChargeBreakup: listChargeBreakup){	        		
											strChargeCategory = extnChargeBreakup.chargeCategory;
											 dblInvoicedCharge = dblInvoicedCharge + extnChargeBreakup.chargeAmount;
											}
										//To calculate the tax refund going into invoice, multiply charge amount
										//by tax percentage.
										double dbTax = taxCalculationInput.tax;
										//System.out.println("strTax"+dbTax);
										
										List<YFSExtnTaxBreakup> listTaxBreakup = taxCalculationInput.colTax;
										//Loop through the tax breakup list to find the tax amount.
										double dbInvoicedTax = 0.0;
										String strChargeCtg = "";
										String strChargeName = "";
										for (YFSExtnTaxBreakup extnTaxBreakup: listTaxBreakup){
											strChargeCtg = extnTaxBreakup.chargeCategory;
											strChargeName = extnTaxBreakup.chargeName;
											if(DISCOUNT_CATEGORY_ADJ.equals(strChargeCtg)
													&& STR_APPEASEMENT_REASON_TAX_ISSUE.equals(strChargeName)){
												hasTaxAdjustments = true;
												dbInvoicedTax = extnTaxBreakup.invoicedTax;
												dbInvoicedTax = dbInvoicedTax + extnTaxBreakup.tax;
												extnTaxBreakupAdjustment = extnTaxBreakup;
											}
										}
										//System.out.print("dbInvoicedTax"+dbInvoicedTax);
										
										double taxToRefund = -(taxPercentage.doubleValue()*0.01*dblInvoicedCharge);
										//System.out.println("value is"+Double.valueOf(dfRMUp.format(taxToRefund)));
										//System.out.println("currentLine "+SCXmlUtil.getString(currentOrderLine));
										//System.out.println("currentLineTaxEle "+SCXmlUtil.getString(currentLineTaxEle));
										//System.out.println("invoiced tax "+SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax"));
										/*if(YFCObject.isDoubleVoid(taxToRefund) && !hasTaxAdjustments){
											newBreakup.tax = -Double.valueOf(dfRMUp.format(SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax")));
										} else {
											newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));
										}*/
										newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));
										//OMS-1170 Negative tax issue : Start
										double dInvoicedTaxFromLine=SCXmlUtil.getDoubleAttribute(currentLineTaxEle,"InvoicedTax",0.00);
										boolean bTaxRefundNotAllowed=false;
										if(dInvoicedTaxFromLine<=0)
											bTaxRefundNotAllowed=true;
										else if(Math.abs(taxToRefund)>dInvoicedTaxFromLine){
											taxToRefund=-dInvoicedTaxFromLine;
											newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));	
										}
										//OMS-1162 : start
										else if(taxPercentage.doubleValue()==0 && dInvoicedTaxFromLine>0) {
											taxToRefund=-dInvoicedTaxFromLine;
											if(log.isDebugEnabled()){
												log.debug("Tax is 0.0 , refunding the invoiced tax"+taxToRefund);
											}
											newBreakup.tax = Double.valueOf(dfRMUp.format(taxToRefund));
										}
										//OMS-1162 : end
											
										if(strChargeCategory.equalsIgnoreCase("Replacement charge") || bTaxRefundNotAllowed ){
											newBreakup.tax = 0.0;
										}
										//OMS-1170 Negative tax issue : End
									}
									
									newBreakup.taxPercentage = taxPercentage
									.doubleValue();
									newBreakup.taxName = currentLineTaxEle
									.getAttribute("TaxName");
									//Fix for updating taxable flag based on TaxType
									//newBreakup.taxableFlag = "Y";

									//Setting extended fields
									Element eleOrderExtn = XMLUtil.getElementByXPath(taxedDoc, "Order/Extn");
									Element eleOrderLineExtn = (Element)currentLineTaxEle.getElementsByTagName("Extn").item(0);
									String strTaxAreaID = "";
									String strTaxPerUnit = ""; 
									String strTaxType = "";
									String strExtnVertexEngine = "";
									String strRuleID = "";
									String strZoneID = "";
									String strGroupID = "";
									String strTaxPerLine = "";
									

									if(!YFCObject.isVoid(eleOrderLineExtn)){
										strTaxAreaID = eleOrderLineExtn.getAttribute("ExtnTaxAreaID");
										strTaxPerUnit = eleOrderLineExtn.getAttribute("ExtnTaxPerUnit");
										strTaxPerLine = eleOrderLineExtn.getAttribute("ExtnRemTaxPerLine");
										strTaxType = eleOrderLineExtn.getAttribute("TaxType");
										strExtnVertexEngine = eleOrderExtn.getAttribute("ExtnVertexEngine");
										strRuleID = eleOrderLineExtn.getAttribute("RuleId");
										strZoneID = eleOrderLineExtn.getAttribute("ZoneId");
										strGroupID = eleOrderLineExtn.getAttribute("GroupId");

									}


									YFCDocument taxBreakupDoc = YFCDocument.createDocument("TaxBreakup");
									YFCElement taxBreakupEle = taxBreakupDoc.getDocumentElement();
									YFCElement extnEle = taxBreakupEle.createChild("Extn");
									if(!YFCObject.isVoid(strTaxAreaID)){
										extnEle.setAttribute("ExtnTaxAreaID", strTaxAreaID);
									}
									if(!YFCObject.isVoid(strTaxPerUnit)){
										extnEle.setAttribute("ExtnTaxPerUnit",strTaxPerUnit);
									}
									if(!YFCObject.isVoid(strTaxPerLine)){
										extnEle.setAttribute("ExtnRemTaxPerLine",strTaxPerLine);
									}
									if(!YFCObject.isVoid(strTaxType)){
										extnEle.setAttribute("TaxType", strTaxType);
									}
									if(!YFCObject.isVoid(strExtnVertexEngine)){
										extnEle.setAttribute("ExtnVertexEngine", strExtnVertexEngine);
									}
									if(!YFCObject.isVoid(strRuleID)){
										extnEle.setAttribute("RuleId", strRuleID);
									}
									if(!YFCObject.isVoid(strZoneID)){
										extnEle.setAttribute("ZoneId", strZoneID);
									}
									if(!YFCObject.isVoid(strGroupID)){
										extnEle.setAttribute("GroupId", strGroupID);
									}

									//Fix for correcting the tax flag based on the Tax type
									//if(strTaxType != null && strTaxType.equalsIgnoreCase("NonTaxable")){
									//Fix for correcting the tax flag based on the Tax type
									if((strTaxType != null && strTaxType.equalsIgnoreCase("NonTaxable")) || tax.doubleValue() == 0){

										newBreakup.taxableFlag = "N";
									}else{
										newBreakup.taxableFlag = "Y";
									}


									extnEle.setAttribute("ExtnIsTaxCalculated",isTaxSuccessful);											

									Document eleExtendedFields = taxBreakupDoc.getDocument();
									newBreakup.eleExtendedFields = eleExtendedFields;

									orderLineTaxes.add(newBreakup);
									if(hasTaxAdjustments){
										orderLineTaxes.add(extnTaxBreakupAdjustment);
									}

								}
								taxCalculationOutput.colTax = orderLineTaxes;
								return taxCalculationOutput;

							}
						}

					} else {
						env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
						if(log.isDebugEnabled()){
							log.debug("IS_TAX_CALC_COMPLETED: Y");
						}
						env.setTxnObject("TAX_CALC_SUCCESSFUL", "N");
						if(log.isDebugEnabled()){
							log.debug("TAX_CALC_SUCCESSFUL: N");
						}
						taxCalculationOutput.colTax = taxCalculationInput.colTax;

						return taxCalculationOutput;

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

			env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
			if(log.isDebugEnabled()){
				log.debug("IS_TAX_CALC_COMPLETED: Y");
			}
			env.setTxnObject("TAX_CALC_SUCCESSFUL", "N");
			if(log.isDebugEnabled()){
				log.debug("TAX_CALC_SUCCESSFUL: N");
			}
			taxCalculationOutput.colTax = taxCalculationInput.colTax;
			throw new YFCException("");
			// return taxCalculationOutput;
		}
		return taxCalculationOutput;
	}
	
	
}