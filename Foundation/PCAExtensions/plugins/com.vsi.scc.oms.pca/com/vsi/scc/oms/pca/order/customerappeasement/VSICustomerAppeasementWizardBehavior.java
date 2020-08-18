package com.vsi.scc.oms.pca.order.customerappeasement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.scc.oms.pca.util.VSIUtil;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class VSICustomerAppeasementWizardBehavior  extends YRCWizardExtensionBehavior  {
	private static final String Wizard_Id = "com.yantra.pca.ycd.rcp.tasks.customerAppeasement.wizards.YCDCustomerAppeasementWizard";
	Element eleCommonCode = null;
	ArrayList<String> listItemID = new ArrayList<String>();
	String strLocaleCode = null;
	
	public void initPage(String pageBeingShown) {
		//System.out.println("Here in wizard");
		callInvoiceDetails();
		super.initPage(pageBeingShown);
		//Begin: Code added by Nisar for BR2-78
		//setFieldValue("radioBtnSelectPartialOrder", true);
		//End: Code added by Nisar for BR2-78
		
		
	}
	
	@Override
	public IYRCComposite createPage(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pageBeingDisposed(String arg0) {
		// TODO Auto-generated method stub
	}

	private void callInvoiceDetails() 
	{
		Element order=getModel("OrderDetails");
		if(order!=null )
		{
			Element orderInvoice = YRCXmlUtils.createFromString("<OrderInvoice />").getDocumentElement();
			String orderHeaderKey=order.getAttribute("OrderHeaderKey");
			YRCPlatformUI.trace("orderHeaderKey=="+orderHeaderKey);
			orderInvoice.setAttribute("OrderHeaderKey",orderHeaderKey );
			YRCApiContext context = new YRCApiContext();
			context.setApiName("GetOrderInvoiceList");
			context.setFormId(Wizard_Id);
			context.setInputXml(orderInvoice.getOwnerDocument());
			callApi(context);
		}
	}
	@Override
	public YRCValidationResponse validateButtonClick(String fieldName) {
		
		try{
		
			if("btnConfirm".equals(fieldName)){
			double dOrderInvoiceAmount = 0.0;
			double dOrderTotalAmount = 0.0;
			//OMS-276 Start
			Element eleOrderDetails = getModel("OrderDetails");
			//OMS-276 End

			Element orderTotalsElem = getExtentionModel("ExtnOrderTotals");
			if(!YRCPlatformUI.isVoid(eleOrderDetails)){
				dOrderTotalAmount = YRCXmlUtils.getDoubleAttribute(eleOrderDetails, "TotalAmount");
				dOrderInvoiceAmount = YRCXmlUtils.getDoubleAttribute(eleOrderDetails, "InvoiceAmount");
			}

			String strAmount=getFieldValue("txtVariableAmount");
			YRCPlatformUI.trace("Inside validateButtonClick", strAmount);
			Element elemAppeasementOffers = getModel("AppeasementOffers");
			NodeList nodeList = elemAppeasementOffers.getElementsByTagName("AppeasementOffer");
	        
			double dAppeasementOfferAmount = 0.0;
			
			for( int count=0; count<nodeList.getLength(); count++ ){
				Element elemOffer = (Element)nodeList.item( count );
				if(("VARIABLE_AMOUNT_ORDER".equals(elemOffer.getAttribute("OfferType"))&&"Y".equals(elemOffer.getAttribute("Checked")))) {
						/**
						 * Modified to remove "," in amount string. Ex:-1,000.00
						 * Modified by Hanuman Gali on 08/20/2014
						 */
						strAmount = strAmount.replaceAll(",","");
						double dVariableAmount = Double.parseDouble(strAmount);
						dAppeasementOfferAmount = dVariableAmount;
						YRCPlatformUI.trace("dVariableAmount=",dVariableAmount);
						Element elemOrder = YRCXmlUtils.getChildElement(elemOffer, "Order");
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
							if(dTotalAmount < dVariableAmount){
								//double dExcessLineAmount = dVariableAmount - dTotalAmount;
								//BigDecimal dExcess = new BigDecimal(dExcessLineAmount);
								//dExcess = dExcess.setScale(2,BigDecimal.ROUND_HALF_DOWN);
								YRCPlatformUI.showError("Error","Appeasements provided cannot exceed order line total.");
								return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Appeasements provided cannot exceed order line total. ");
							}
							BigDecimal bd = new BigDecimal(dCurrentAmount);
							bd = bd.setScale(2,BigDecimal.ROUND_HALF_DOWN);
							if((iOrderLinesCount-1)==iOrderLineCounter){
								elemOrderLine.setAttribute("LineOfferAmount", String.valueOf(dVariableAmount-dTotalAppeasementProvided));
								repopulateModel("AppeasementOffers");
								Element elemVariableAmountOffer = getModel("VariableAmountOffer");
								if(elemVariableAmountOffer!=null){
									elemVariableAmountOffer.setAttribute("ChargeAmount", "0.0");	
								}
								//setFieldValue("txtVariableAmount", "0.0");
								//repopulateModel("VariableAmountOffer");
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
								if(dblBalanceAmount != 0.0 && dblAdjAmount > dblBalanceAmount){
									YRCPlatformUI.showError("Error",YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
									return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
								}
							}
							//OMS-276 End
						}
						break;
					}else if("Y".equals(elemOffer.getAttribute("Checked"))){
						dAppeasementOfferAmount = YRCXmlUtils.getDoubleAttribute(elemOffer,"OfferAmount");
						//OMS-276 Start
						Element eleAppOfferLines = YRCXmlUtils.getChildElement(elemOffer, "OrderLines");
						if(!YRCPlatformUI.isVoid(eleAppOfferLines) && eleAppOfferLines.hasChildNodes()){
							NodeList nlAppOffLine = eleAppOfferLines.getElementsByTagName("OrderLine"); 
							for (int k=0;k<nlAppOffLine.getLength();k++){
								Element eleAppOffLine = (Element)nlAppOffLine.item(k);
								String strOrderLineKey = eleAppOffLine.getAttribute("OrderLineKey");
								double dblAdjAmount = YRCXmlUtils.getDoubleAttribute(eleAppOffLine, "LineOfferAmount");

								System.out.println("eleOrderDetails" +YRCXmlUtils.getString(eleOrderDetails));
								String strBalanceAmount = (String) YRCXPathUtils.evaluate(eleOrderDetails,
										"/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/LinePriceInfo/@ExtnTotalBalanceAmount", XPathConstants.STRING);

								if(!YRCPlatformUI.isVoid(strBalanceAmount)){
									double dblBalanceAmount = Double.parseDouble(strBalanceAmount);
									if(dblBalanceAmount != 0.0 && dblAdjAmount > dblBalanceAmount){
										YRCPlatformUI.showError("Error",YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
										return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,YRCPlatformUI.getString("VSI_APPEASEMENT_EXCEEDS_LINE_TOTAL")+dblBalanceAmount);
									}
								}
							}
						}
						//OMS-276 End
					}
					double dBalanceAmount = dOrderTotalAmount - (dAppeasementOfferAmount-dOrderInvoiceAmount);
					//BigDecimal bd = new BigDecimal(dBalanceAmount);
					//bd = bd.setScale(2,BigDecimal.ROUND_HALF_DOWN);
					//YRCPlatformUI.trace(bd.doubleValue());
					if(dBalanceAmount <0.0){
						YRCPlatformUI.showError("Error","Appeasements provided cannot exceed order total.");
						return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR,"Appeasements provided cannot exceed ordr total");
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
			// OMS-276 End
		}catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return super.validateButtonClick(fieldName);
	}

	@Override
	public boolean preCommand(YRCApiContext apiContext) {

		String[] apinames = apiContext.getApiNames();
		
		if (apinames.length > 1) {
			for (int iAPINames = 0; iAPINames < apinames.length; iAPINames++) {

				if ((YRCPlatformUI.equals(apinames[iAPINames],
						"recordInvoiceCreation"))) {
					
				}
				
				
			}
		}
		return super.preCommand(apiContext);
	}
	
	
	@Override
	public void postCommand(YRCApiContext arg0) {
		if("recordInvoiceCreation".equals(arg0.getApiName())){
			Element eleOutput=arg0.getOutputXml().getDocumentElement();
			if(!YRCPlatformUI.isVoid(eleOutput)){
				/** IF Condition added by Arun Sekhar to stop the changeOrder call in case of a blank invoice number **/
				if(!YRCPlatformUI.isVoid(eleOutput.getAttribute("InvoiceNo"))){
					YRCPlatformUI.trace("InvoiceNo is not blank.");
					//Element elemChangeOrderInput = constructChangeOrderInput(eleOutput.getAttribute("InvoiceNo"),eleOutput);
					//System.out.println("Change Order I/P is "+YRCXmlUtils.getString(elemChangeOrderInput));
					try{
						//invokeChangeOrderAPI(elemChangeOrderInput);	
					}catch (Exception e) {
						e.printStackTrace();	
					}	
				}

			}
		}
		super.postCommand(arg0);
	}

	
	private Element constructChangeOrderInput(String sInvoiceNo, Element eleOutput) {
		Element elemAppeasementOffers = getModel("AppeasementOffers");
		YRCPlatformUI.trace("AppeasementOffers Model Output::"+YRCXmlUtils.getString(elemAppeasementOffers));
		YRCPlatformUI.trace("Change Order Input ::\n "+YRCXmlUtils.getString(eleOutput));
		
		/** 
		 * STER-234 - The RecordInvoiceCreation output consist only Shipped lines. 
		 * Adding the shipped lines to a Array List.
		 */
		ArrayList<String> listOLK = new ArrayList<String>();
		Element eleLineDetails = YRCXmlUtils.getChildElement(eleOutput, "LineDetails");
		if(eleLineDetails.hasChildNodes()){
			NodeList nlLineDetail = eleLineDetails.getElementsByTagName("LineDetail");
			for(int i =0; i<nlLineDetail.getLength();i++)
			{
				Element eleLineDetail =(Element)nlLineDetail.item(i);
				String strLineKey = eleLineDetail.getAttribute("OrderLineKey");
				listOLK.add(strLineKey);
			}
		}
		
		
		NodeList nodeList = elemAppeasementOffers.getElementsByTagName("AppeasementOffer");
        String strOrderHeaderKey=elemAppeasementOffers.getAttribute("OrderHeaderKey");
        Element clemChangeOrderInput = YRCXmlUtils.createFromString("<Order/>").getDocumentElement();
		clemChangeOrderInput.setAttribute("OrderHeaderKey", strOrderHeaderKey);
		Element orderLinesElem = YRCXmlUtils.createChild(clemChangeOrderInput, "OrderLines");
		
        if(!YRCPlatformUI.isVoid(strOrderHeaderKey)){
        	for( int count=0; count<nodeList.getLength(); count++ ){
        		Element elemOffer = (Element)nodeList.item( count );
        		if("Y".equals(elemOffer.getAttribute("Checked"))) {
        			Element eleOrderLines = (Element) elemOffer.getElementsByTagName("OrderLines").item(0);
        			NodeList nodelistOrderLines = elemOffer.getElementsByTagName("OrderLine");

        			/** 
        			 * STER-234 Adding the Unshipped lines that should be removed in the changeOrder Input.
        			 */
        			ArrayList<Element> nodesToRemove = new ArrayList<Element>();
        			for( int iOrderLineCounter=0; iOrderLineCounter<nodelistOrderLines.getLength() ; iOrderLineCounter++ ){
        				Element elemOrderLine = (Element)nodelistOrderLines.item( iOrderLineCounter );
        				String strOrderLineKey = elemOrderLine.getAttribute("OrderLineKey");
        				if(!listOLK.contains(strOrderLineKey)){
        					nodesToRemove.add(elemOrderLine);
        				}
        			}
        			for (int i = 0; i < nodesToRemove.size(); i++) {

        				eleOrderLines.removeChild(nodesToRemove.get(i));
        			}
        			int iOrderLinesCount = nodelistOrderLines.getLength();
        			for( int iOrderLineCounter=0; iOrderLineCounter<iOrderLinesCount ; iOrderLineCounter++ ){
        				Element elemOrderLine = (Element)nodelistOrderLines.item( iOrderLineCounter );
        				Element elemChangeOrderLine = YRCXmlUtils.createChild(orderLinesElem, "OrderLine");
        				elemChangeOrderLine.setAttribute("OrderLineKey", elemOrderLine.getAttribute("OrderLineKey"));
        				String sLineOfferAmount = elemOrderLine.getAttribute("LineOfferAmount");
        				String sOrderLineQuantity = elemOrderLine.getAttribute("OrderedQty");
        				constructChargeElem(elemChangeOrderLine,sLineOfferAmount,sOrderLineQuantity,sInvoiceNo);
        			}
        			break;
        		}
        	}
        }
		return clemChangeOrderInput;
	}
	private void constructChargeElem(Element elemChangeOrderLine,
			String lineOfferAmount, String orderLineQuantity, String invoiceNo) {
		Element elemLineCharges = YRCXmlUtils.createChild(elemChangeOrderLine, "LineCharges");
		Element elemLineCharge = YRCXmlUtils.createChild(elemLineCharges, "LineCharge");
		elemLineCharge.setAttribute("ChargeCategory", "CUSTOMER_APPEASEMENT");
		elemLineCharge.setAttribute("ChargeName", "CM_"+invoiceNo);
		elemLineCharge.setAttribute("ChargePerLine", "0.0");
		elemLineCharge.setAttribute("ChargePerUnit", "0.0");
		elemLineCharge.setAttribute("Reference", computeReference(lineOfferAmount,orderLineQuantity));
	}

	private String computeReference(String lineOfferAmount,String orderLineQuantity) {
		try{
			double dLineOfferAmount = Double.parseDouble(lineOfferAmount);
		    BigDecimal bd1 = new BigDecimal(dLineOfferAmount);
		    bd1 = bd1.setScale(2,BigDecimal.ROUND_HALF_DOWN);
		    dLineOfferAmount = bd1.doubleValue();
			double dOrderLineQuantity = Double.parseDouble(orderLineQuantity);
			return constructChargePerUnitAndLine(dLineOfferAmount,dOrderLineQuantity);
			
		}catch (Exception e) {
			// should not be here..
		}
		// Incase of Exception it returns line amount as 0.0 and Charge Per Line as the total amount
		return "0.0,"+lineOfferAmount;
	}
	
	private static String constructChargePerUnitAndLine(double lineOfferAmount,double orderLineQuantity) {
		double dCurrentAmount = lineOfferAmount*100/orderLineQuantity;
	    BigDecimal bd = new BigDecimal(dCurrentAmount);
	    bd = bd.setScale(0,BigDecimal.ROUND_DOWN);		
	    double dChargePerUnit = bd.doubleValue();
	    double dNewChargePerUnit = Double.parseDouble(String.valueOf(dChargePerUnit))/100;
	    double dChargePerLine = lineOfferAmount - (dNewChargePerUnit*orderLineQuantity);
	    BigDecimal bd1 = new BigDecimal(dChargePerLine);
	    bd1 = bd1.setScale(2,BigDecimal.ROUND_HALF_DOWN);		
	    double dNewChargePerLine = bd1.doubleValue();
		return String.valueOf(dNewChargePerUnit)+","+String.valueOf(dNewChargePerLine);
	}

	private void invokeChangeOrderAPI(Element elemChangeOrderInput) {
		YRCPlatformUI.trace("Invoking changeOrder API with input ", YRCXmlUtils.getString(elemChangeOrderInput));
		YRCApiContext context = new YRCApiContext();
		context.setApiName("VSIChangeOrderForInvoice");
		context.setFormId(Wizard_Id);
		context.setInputXml(elemChangeOrderInput.getOwnerDocument());
		callApi(context);
	}
	
	@Override
	public void postSetModel(String arg0) {
		// TODO Auto-generated method stub
		
		if ("OrderDetails".equalsIgnoreCase(arg0)) {
			Element eleOrderDetails = getModel(arg0);

			if(!YRCPlatformUI.isVoid(eleOrderDetails)){
				
				
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
	
	
	public void handleApiCompletion(YRCApiContext ctxApi) {
		
		if(ctxApi.getInvokeAPIStatus()<0){
			YRCPlatformUI.trace("API call FailedAPI call Failed");
			
		} 
		else if("GetOrderInvoiceList".equalsIgnoreCase(ctxApi.getApiName())){
			Double dOrderAmount = 0.00;
			Element elemOrderDetails = getModel("OrderDetails");
			if(!YRCPlatformUI.isVoid(elemOrderDetails)){
				YRCPlatformUI.trace(YRCXmlUtils.getString(elemOrderDetails));
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
	
}
