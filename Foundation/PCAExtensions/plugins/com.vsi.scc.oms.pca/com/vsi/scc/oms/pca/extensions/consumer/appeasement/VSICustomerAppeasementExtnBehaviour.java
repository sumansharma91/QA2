	
package com.vsi.scc.oms.pca.extensions.consumer.appeasement;

/**
 * Created on Sep 10,2015
 *
 */
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.util.VSIUtil;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.IYRCApiCallbackhandler;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author DELL
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSICustomerAppeasementExtnBehaviour extends YRCWizardExtensionBehavior {

	 private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.customerAppeasement.wizards.YCDCustomerAppeasementWizard";
	 private boolean isOrderStatusShipped= true;	
	 private int statusFlag=0;
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
	}
 
 	
    public String getExtnNextPage(String currentPageId) {
		//TODO
		return null;
    }
    
    public IYRCComposite createPage(String pageIdToBeShown) {
		//TODO
		return null;
	}
    
    public void pageBeingDisposed(String pageToBeDisposed) {
		//TODO
    }

    /**
     * Called when a wizard page is about to be shown for the first time.
     *
     */
    public void initPage(String pageBeingShown) {
		//TODO
    	callInvoiceDetails();
    	super.initPage(pageBeingShown);
    }
    
    private void callInvoiceDetails() 
	{
		Element order=getModel("OrderDetails");
		if(order!=null )
		{
			Element eleOrderInvoice = YRCXmlUtils.createDocument("OrderInvoice").getDocumentElement();
			String orderHeaderKey=order.getAttribute("OrderHeaderKey");
			YRCPlatformUI.trace("orderHeaderKey=="+orderHeaderKey);
			eleOrderInvoice.setAttribute("OrderHeaderKey",orderHeaderKey );
			YRCXmlUtils.getString(eleOrderInvoice);
			YRCApiContext context = new YRCApiContext();
			context.setApiName("getOrderInvoiceList");
			context.setFormId(this.getFormId());
			context.setInputXml(eleOrderInvoice.getOwnerDocument());
			callApi(context);
			context.setUserData("apiCalledFromRule", "Y"); //to make a synchronous call
			YRCPlatformUI.callApi(context, new IYRCApiCallbackhandler() {
				public void handleApiCompletion(YRCApiContext apiCtx) {
						if (apiCtx.getApiName().equals("getOrderInvoiceList")) {
							Element orderInvoiceList = apiCtx.getOutputXml().getDocumentElement();
							if (!YRCPlatformUI.isVoid(orderInvoiceList))
							{
								setExtentionModel("extn_orderInvoiceList",orderInvoiceList);
							}
						}	
		}		
		});	
		}
	}
	@Override
	public YRCValidationResponse validateButtonClick(String fieldName) {
		
		try{
			if("btnConfirm".equals(fieldName)){
			double dOrderInvoiceAmount = 0.0;
			double dSettledInvoiceAmount =0.0;
			double dOrderTotalAmount = 0.0;
			Element eleOrderDetails = getModel("OrderDetails");
			Element elePriceinfo=YRCXmlUtils.getChildElement(eleOrderDetails, "PriceInfo");
			String ordertotal1=YRCXmlUtils.getAttribute(elePriceinfo, "TotalAmount");
			dOrderTotalAmount=Double.parseDouble(ordertotal1);
			//Element orderTotalsElem = getExtentionModel("ExtnOrderTotals");
			//Check if multiple appeasement are already applied on order
			Element eleOrderInvoiceList = getExtentionModel("extn_orderInvoiceList");
			if(!YRCPlatformUI.isVoid(eleOrderInvoiceList)){
				NodeList nodeList = eleOrderInvoiceList.getElementsByTagName("OrderInvoice");
				for( int count=0; count<nodeList.getLength(); count++ )
				{
					Element eleOrderInvoice = (Element)nodeList.item(count);
					if(eleOrderInvoice.getAttribute("InvoiceType").equalsIgnoreCase("SHIPMENT"))
					{
						String sSettledInvoiceAmount = eleOrderInvoice.getAttribute("TotalAmount");
						dSettledInvoiceAmount = dSettledInvoiceAmount + Double.parseDouble(sSettledInvoiceAmount);
					}
					if(eleOrderInvoice.getAttribute("InvoiceType").equalsIgnoreCase("CREDIT_MEMO"))
					{
						//dOrderInvoiceAmount = dOrderInvoiceAmount+ YRCXmlUtils.getDoubleAttribute(eleOrderInvoice, "AmountCollected");
						String sInvoiceAmount = eleOrderInvoice.getAttribute("TotalAmount");
						dOrderInvoiceAmount = dOrderInvoiceAmount+ Double.parseDouble(sInvoiceAmount);
						
					}
				}
			}

			if(dOrderTotalAmount!=dSettledInvoiceAmount){
				dOrderTotalAmount=dSettledInvoiceAmount;
			}
			String strAmount=getFieldValue("txtVariableAmount");
			YRCPlatformUI.trace("Inside validateButtonClick", strAmount);
			Element elemAppeasementOffers = getModel("AppeasementOffers");
			NodeList nodeList = elemAppeasementOffers.getElementsByTagName("AppeasementOffer");
			double dVariableAmount=0.0;
			double dAppeasementOfferAmount = 0.0;
			if(!YRCPlatformUI.isVoid(strAmount)){
				strAmount = strAmount.replaceAll(",","");
			    dVariableAmount = Double.parseDouble(strAmount);
				dAppeasementOfferAmount = dVariableAmount;
				}
			boolean flatappeasement=false;
			
			for( int count=0; count<nodeList.getLength(); count++ ){
				Element elemOffer = (Element)nodeList.item( count );
				if(("VARIABLE_AMOUNT_ORDER".equals(elemOffer.getAttribute("OfferType"))&&"Y".equals(elemOffer.getAttribute("Checked")))||
						("FLAT_AMOUNT_ORDER".equals(elemOffer.getAttribute("OfferType"))&&"Y".equals(elemOffer.getAttribute("Checked")))|| flatappeasement) {	
					if(("FLAT_AMOUNT_ORDER".equals(elemOffer.getAttribute("OfferType"))) && "Y".equals(elemOffer.getAttribute("Checked"))){
						dVariableAmount=Double.parseDouble(elemOffer.getAttribute("OfferAmount"));
						dAppeasementOfferAmount = dVariableAmount;
						flatappeasement=true;
						continue;
						
					}
					double dBalanceAmount = dOrderTotalAmount - (dAppeasementOfferAmount-dOrderInvoiceAmount);
					BigDecimal bd1 = new BigDecimal(dBalanceAmount);
					bd1 = bd1.setScale(2,BigDecimal.ROUND_HALF_DOWN);
					YRCPlatformUI.trace(bd1.doubleValue());
					if(dBalanceAmount <0.0){
						
						YRCPlatformUI.showError("Error","Appeasements provided cannot exceed order total.  Excess amount of "+-bd1.doubleValue());
						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Appeasements provided cannot exceed order total. Excess amount of "+-bd1.doubleValue());
					}
					else {
						//YRCPlatformUI.trace("dVariableAmount=",dVariableAmount);
						Element elemOrder = YRCXmlUtils.getChildElement(elemOffer, "Order");
						elemOrder.removeAttribute("HeaderOfferAmount");
						NodeList nodelistOrderLines = elemOffer.getElementsByTagName("OrderLine");
						double dTotalAppeasementProvided = 0.0;
						int iOrderLinesCount = nodelistOrderLines.getLength();
						for( int iOrderLineCounter=0; iOrderLineCounter<iOrderLinesCount; iOrderLineCounter++ ){
							Element elemOrderLine = (Element)nodelistOrderLines.item( iOrderLineCounter );
							String sTotalAmount = elemOrderLine.getAttribute("TempTotalAmount");
							double dTotalAmount = Double.parseDouble(sTotalAmount);
							String sLineAmount = elemOrderLine.getAttribute("TempTotalLineAmount");
							double dLineAmount = Double.parseDouble(sLineAmount);
							double dCurrentAmount = dLineAmount*dVariableAmount/dTotalAmount;
							/*if(dTotalAmount < dVariableAmount){
								double dExcessLineAmount = dVariableAmount - dTotalAmount;
								BigDecimal dExcess = new BigDecimal(dExcessLineAmount);
								dExcess = dExcess.setScale(2,BigDecimal.ROUND_HALF_DOWN);
								YRCPlatformUI.showError("Error","Appeasements provided cannot exceed order line total.  Excess amount of "+-dExcess.doubleValue());
								return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Appeasements provided cannot exceed order line total. Excess amount of "+-dExcess.doubleValue());
							} */
							BigDecimal bd = new BigDecimal(dCurrentAmount);
							bd = bd.setScale(2,BigDecimal.ROUND_HALF_DOWN);
							if((iOrderLinesCount-1)==iOrderLineCounter){
								elemOrderLine.setAttribute("LineOfferAmount", String.valueOf(dVariableAmount-dTotalAppeasementProvided));
								repopulateModel("AppeasementOffers");
								Element elemVariableAmountOffer = getModel("VariableAmountOffer");
								if(elemVariableAmountOffer!=null){
									elemVariableAmountOffer.setAttribute("ChargeAmount", "0.0");	
								}
								
							}else{
								dTotalAppeasementProvided += Double.parseDouble(String.valueOf(bd.doubleValue()));
								elemOrderLine.setAttribute("LineOfferAmount", String.valueOf(bd.doubleValue()));	
							}
							// OMS-276 Start
							String strOrderLineKey = elemOrderLine.getAttribute("OrderLineKey");
							double dblAdjAmount = YRCXmlUtils.getDoubleAttribute(elemOrderLine, "LineOfferAmount");
							String strBalanceAmount = (String) YRCXPathUtils.evaluate(eleOrderDetails,
											"/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/LinePriceInfo/@ExtnTotalBalanceAmount", XPathConstants.STRING);

							if(!YRCPlatformUI.isVoid(strBalanceAmount)){
								double dblBalanceAmount = Double.parseDouble(strBalanceAmount);
								/*if(dblBalanceAmount != 0.0 && dblAdjAmount > dblBalanceAmount){
									YRCPlatformUI.showError("Error",YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
									return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
								}*/
							}
							
						}
						break;
					}
					
				}
			}
			
			}
			// OMS-276 Start
			if("btnNext".equals(fieldName)){
				
				Element eleOrderDetails = getModel("OrderDetails");
				Element eleOrderLines = YRCXmlUtils.getChildElement(eleOrderDetails, "OrderLines");
				NodeList nodelistOrderLines = eleOrderLines.getElementsByTagName("OrderLine");
				for (int k=0;k<nodelistOrderLines.getLength();k++){
					Element eleOrderLine = (Element)nodelistOrderLines.item(k);
					String strIsLineChecked = eleOrderLine.getAttribute("Checked");
					String strIsLineRefunded = eleOrderLine.getAttribute("IsLineFullyRefunded");
					if("Y".equalsIgnoreCase(strIsLineChecked) && "Y".equalsIgnoreCase(strIsLineRefunded)){
						YRCPlatformUI.showError("Error",YRCPlatformUI.getString("VSI_LINE_REFUNDED_FULLY"));
						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,YRCPlatformUI.getString("VSI_LINE_REFUNDED_FULLY"));
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return super.validateButtonClick(fieldName);
	}

 	
	/**
	 * Method for validating the text box.
     */
    public YRCValidationResponse validateTextField(String fieldName, String fieldValue) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateTextField(fieldName, fieldValue);
	}
    
    /**
     * Method for validating the combo box entry.
     */
    public void validateComboField(String fieldName, String fieldValue) {
		// TODO Validation required for the following controls.
    	
    	// check if the field name is cmbAppeasementReason
		if ("cmbAppeasementReason".equalsIgnoreCase(fieldName)) {
			
			//check if the field value contains "Coupon" string
			if (!YRCPlatformUI.isVoid(fieldValue) && fieldValue.contains("Coupon")) {
				// Enable the text field to capture the coupon code
				this.enableField("extn_txt_Coupon_Code");
			} else {
				// Disable the text field that captures the coupon code
				this.disableField("extn_txt_Coupon_Code");
				// Reset the field value to empty string
				this.setFieldValue("extn_txt_Coupon_Code", "");
			}
		}
		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
	}
    

    
    /**
     * Method called when a link is clicked.
     */
	public YRCValidationResponse validateLinkClick(String fieldName) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}
	
	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }
	 
	 @Override
	public boolean preCommand(YRCApiContext ctx) {
		// TODO Auto-generated method stub
		 Element root = null;
		 
		 // Get the API names and Input XMLs 
		 String apiNames[] = ctx.getApiNames();
		 Document inputXML[] = ctx.getInputXmls();
		 String apiName = null;
		 Document input = null;
		 int noOfApis = apiNames.length;
			 for(int i = 0 ; i<noOfApis;i++){
				 apiName = apiNames[i];
				 
				 // Check if the API name is "recordInvoiceCreation"
				 if("recordInvoiceCreation".equalsIgnoreCase(apiName)){
					 input = inputXML[i];
					 Element selectedReasons = this.getTargetModel("SelectedReasons");
					 root = input.getDocumentElement();
					 // Add the attribute CouponCode to the Extended field.
					 Element extn = input.createElement("Extn");
					 extn.setAttribute("ExtnCouponCode", selectedReasons.getAttribute("CouponCode"));
					 // Append the Exntended element to the root element.
					 root.appendChild(extn);
					 break;
				 }
			 }
		return super.preCommand(ctx);
	}
	 
		public void handleApiCompletion(YRCApiContext ctxApi) {
			
			if(ctxApi.getInvokeAPIStatus()<0){
				YRCPlatformUI.trace("API call FailedAPI call Failed");
				
			} 
			else if("getOrderInvoiceList".equalsIgnoreCase(ctxApi.getApiName())){
				Double dOrderAmount = 0.00;
				Element elemOrderDetails = getModel("OrderDetails");
				if(!YRCPlatformUI.isVoid(elemOrderDetails)){
					//YRCPlatformUI.trace(YRCXmlUtils.getString(elemOrderDetails));
					Element priceInfo = YRCXmlUtils.getChildElement(elemOrderDetails, "PriceInfo", true);
					dOrderAmount += YRCXmlUtils.getDoubleAttribute(priceInfo, "TotalAmount");
					YRCPlatformUI.trace(dOrderAmount);
				}
				
				Element eleOrderInvoiceList=ctxApi.getOutputXml().getDocumentElement();
				Double dInvoiceAmount = 0.00;	
					NodeList nlOrderInvoice=eleOrderInvoiceList.getElementsByTagName("OrderInvoice");
					YRCPlatformUI.trace(YRCXmlUtils.getString(eleOrderInvoiceList));
					for( int count=0; count<nlOrderInvoice.getLength(); count++ ){
			            Element elemOrderInvoice = (Element)nlOrderInvoice.item( count );
			            if(!YRCPlatformUI.isVoid(elemOrderInvoice)){
			            	String strInvoiceType=elemOrderInvoice.getAttribute("InvoiceType");
			            	if("CREDIT_MEMO".equals(strInvoiceType)){
			            		dInvoiceAmount += YRCXmlUtils.getDoubleAttribute(elemOrderInvoice, "TotalAmount");
			            	}
			            }
					}
					if(nlOrderInvoice.getLength()>0)
					{
						YRCPlatformUI.trace("Order has adjustments");
						
					}
					else
					{
						YRCPlatformUI.trace("Order has not any adjustment");
					}

					Element orderInvoiceTotals = YRCXmlUtils.createFromString("<InvoiceTotals />").getDocumentElement();
					orderInvoiceTotals.setAttribute("InvoiceAmount", String.valueOf(dInvoiceAmount));
					orderInvoiceTotals.setAttribute("TotalAmount", String.valueOf(dOrderAmount));	
					setExtentionModel("ExtnOrderTotals", orderInvoiceTotals);
					
					
			} 
			
			
			// TODO Auto-generated method stub
			super.handleApiCompletion(ctxApi);
		}
		
		public void postSetModel(String arg0) {
			// TODO Auto-generated method stub
			
			if ("OrderDetails".equalsIgnoreCase(arg0)) {
				Element eleOrderDetails = getModel(arg0);

				if(!YRCPlatformUI.isVoid(eleOrderDetails)){
					//Start: OMS-684 
					NodeList nodelistOrderLines = eleOrderDetails.getElementsByTagName("OrderLine");
					for(int i=0;i<nodelistOrderLines.getLength()&&statusFlag==0;i++)
					{
						Element elemOrderLine = (Element)nodelistOrderLines.item( i );
						String linestatus= elemOrderLine.getAttribute("Status");
						if (!YRCPlatformUI.isVoid(linestatus)){
							if(linestatus.equalsIgnoreCase("Shipped"))
								statusFlag=1;
							if(!(linestatus.equalsIgnoreCase("Shipped"))&& !(linestatus.equalsIgnoreCase("Partially Shipped"))&& statusFlag!=1)
							{
								isOrderStatusShipped=false;
							}
									
									
						}
									
					}
					  if(!isOrderStatusShipped)
						{
						this.disableField("cmbAppeasementReason");
						this.disableField("btnNext");
						YRCPlatformUI.showError("Error","You are not allowed to give appeasement to the customer unless the order is in shipped status.Please click on close button to exit from the screen");
						
						}
					//End: OMS-684 
					
					// OMS-276 Start
					HashMap<String, Double> hmROLKAmount = new HashMap<String, Double>();
					Element eleReturnOrders = YRCXmlUtils.getChildElement(eleOrderDetails, "ReturnOrders");
					if(!YRCPlatformUI.isVoid(eleReturnOrders) && eleReturnOrders.hasChildNodes()){
						NodeList nlReturnOrder = eleReturnOrders.getElementsByTagName("ReturnOrder");
						for(int i=0;i<nlReturnOrder.getLength();i++){
							Element eleReturnOrder =(Element)nlReturnOrder.item(i);
							Element eleReturnOrderLines = YRCXmlUtils.getChildElement(eleReturnOrder, "OrderLines");
							if(!YRCPlatformUI.isVoid(eleReturnOrderLines) && eleReturnOrderLines.hasChildNodes()){
								NodeList nlReturnOrderLine = eleReturnOrderLines.getElementsByTagName("OrderLine");
								for(int j=0;j<nlReturnOrderLine.getLength();j++){
									Element eleReturnOrderLine =(Element)nlReturnOrderLine.item(j);
									String strReturnLineKey = eleReturnOrderLine.getAttribute("OrderLineKey");
									Element eleReturnPriceInfo = (Element) eleReturnOrderLine.getElementsByTagName("LinePriceInfo").item(0);
									double dblAdjAmount = YRCXmlUtils.getDoubleAttribute(eleReturnPriceInfo,"InvoicedLineTotal");
									hmROLKAmount.put(strReturnLineKey, dblAdjAmount);
								}
							}
						}
					}
					
					Element eleOrderLines = YRCXmlUtils.getChildElement(eleOrderDetails, "OrderLines");
					if(!YRCPlatformUI.isVoid(eleOrderLines) && eleOrderLines.hasChildNodes()){
						NodeList ndlOrderLines = eleOrderLines.getElementsByTagName("OrderLine");
						double dblLineTotal = 0.0;
						double dblBalanceAmt = 0.0;
						for(int iLine=0;iLine<ndlOrderLines.getLength();iLine++){
							Element eleOrderLine = (Element)ndlOrderLines.item(iLine);
							if(!YRCPlatformUI.isVoid(eleOrderLine)){
								//Total Collected Amount on the Line
								Element eleLinePriceInfo = YRCXmlUtils.getChildElement(eleOrderLine, "LinePriceInfo");
								dblLineTotal = YRCXmlUtils.getDoubleAttribute(eleLinePriceInfo, "LineTotal");
								// Total Refunded Amount on the Line for Return Refunded Lines
								double dblInvoiceQty = YRCXmlUtils.getDoubleAttribute(eleOrderLine, "InvoicedQty");
								double dblShipQty = YRCXmlUtils.getDoubleAttribute(eleOrderLine, "ShippedQuantity");
								double dblAdjCreditAmt = 0.0;
								double dblReturnedAmount=0.0;
								double dblTotalRefundAmt = 0.0;
								double dblRoundedRefAmt = 0.0;
								if((dblInvoiceQty>0 && dblShipQty > 0)){
									Element eleLineCharges = YRCXmlUtils.getChildElement(eleOrderLine,"LineCharges");
									if(!YRCPlatformUI.isVoid(eleLineCharges)){
										NodeList nlLineCharge = eleLineCharges.getElementsByTagName("LineCharge");
										for(int iLC=0;iLC<nlLineCharge.getLength();iLC++){
											Element eleLineCharge = (Element)nlLineCharge.item(iLC);
											if(!YRCPlatformUI.isVoid(eleLineCharge) && "CUSTOMER_APPEASEMENT".equalsIgnoreCase(eleLineCharge.getAttribute("ChargeCategory"))){
												try{
													String strReference = eleLineCharge.getAttribute("Reference");
													String strLineAmount = strReference.substring(0,strReference.indexOf(","));
													if(!YRCPlatformUI.isVoid(strLineAmount)){
														dblAdjCreditAmt += Double.parseDouble(strLineAmount);
													}
												}
												catch (Exception e) {
													// should not be here..
												}
											}
										}
										/* Retrieve all the Returned Lines for this Particular SO Line.
										 * This can contain more than one return order for a single SO Line.
										 */

										Element eleReturnLines = YRCXmlUtils.getChildElement(eleOrderLine, "ReturnOrderLines");
										if(!YRCPlatformUI.isVoid(eleReturnLines) && eleReturnLines.hasChildNodes()){
											NodeList nlReturnLines = eleReturnLines.getElementsByTagName("OrderLine");
											for(int iRL=0;iRL<nlReturnLines.getLength();iRL++){
												Element elReturnLine = (Element)nlReturnLines.item(iRL);
												if(!YRCPlatformUI.isVoid(elReturnLine)){
													String strReturnLineKey = elReturnLine.getAttribute("OrderLineKey");
													dblReturnedAmount += hmROLKAmount.get(strReturnLineKey);
												}
											}
										}	
										dblTotalRefundAmt = dblAdjCreditAmt + dblReturnedAmount;

										try {
											dblRoundedRefAmt = VSIUtil.roundingDown(dblTotalRefundAmt);
											dblBalanceAmt = VSIUtil.roundingDown(dblLineTotal - dblRoundedRefAmt);
											eleLinePriceInfo.setAttribute("ExtnTotalRefundAmount", Double.toString(dblRoundedRefAmt));
											eleLinePriceInfo.setAttribute("ExtnTotalBalanceAmount", Double.toString(dblBalanceAmt));

										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									if(dblLineTotal <= dblRoundedRefAmt){
										eleOrderLine.setAttribute("Checked", "N");
										eleOrderLine.setAttribute("IsLineFullyRefunded", "Y");
										repopulateModel("OrderDetails");
									}
								}
							}
						}
					}
					//OMS-276 End
				}
			}
			super.postSetModel(arg0);
		}	
}
