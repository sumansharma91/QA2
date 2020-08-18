package com.vsi.oms.userexit;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnHeaderTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateHeaderTaxUE;

public class VSIRecalculateHeaderTaxUEImpl implements YFSRecalculateHeaderTaxUE,VSIConstants{

	YIFApi api;

	public YFSExtnTaxCalculationOutStruct recalculateHeaderTax(YFSEnvironment env, YFSExtnHeaderTaxCalculationInputStruct taxCalculationInput) throws YFSUserExitException {
		// TODO Auto-generated method stub
		YFSExtnTaxCalculationOutStruct taxCalculationOutput = new YFSExtnTaxCalculationOutStruct();
		
		if(taxCalculationInput.enterpriseCode.equals(ENT_MCL)){
			
			taxCalculationOutput.colTax = taxCalculationInput.colTax;
			taxCalculationOutput.tax = taxCalculationInput.tax;
			return taxCalculationOutput;
		}

		taxCalculationOutput.colTax = taxCalculationInput.colTax;
		
		if(taxCalculationInput.documentType.equalsIgnoreCase("0003")
				||("SHIPMENT").equalsIgnoreCase(taxCalculationInput.invoiceMode))
		{
			List<YFSExtnTaxBreakup> lstColTax = taxCalculationInput.colTax;
			List<YFSExtnTaxBreakup> listOutTaxBreakup = new ArrayList<YFSExtnTaxBreakup>();
		
			for (YFSExtnTaxBreakup extnTaxBreakup: lstColTax){	  
				extnTaxBreakup.tax = extnTaxBreakup.tax - extnTaxBreakup.invoicedTax;
				listOutTaxBreakup.add(extnTaxBreakup);
				taxCalculationOutput.tax+= extnTaxBreakup.tax;	
			}
			taxCalculationOutput.colTax = listOutTaxBreakup;
			return taxCalculationOutput;
		}
		else if (taxCalculationInput.bForPacklistPrice
				|| taxCalculationInput.bForInvoice
				|| ("RETURN")
				.equalsIgnoreCase(taxCalculationInput.invoiceMode)
		) {
			
			BigDecimal totalProratedHeaderTax = new BigDecimal("0.0");
			ArrayList<YFSExtnTaxBreakup> invoiceTaxes = new ArrayList<YFSExtnTaxBreakup>();
			if (taxCalculationInput.colTax != null) {
				List<YFSExtnTaxBreakup> orderHeaderTaxes = taxCalculationInput.colTax;
				

					for (YFSExtnTaxBreakup extnTaxBreakup : orderHeaderTaxes) {
						BigDecimal invoicedTax = BigDecimal
						.valueOf(extnTaxBreakup.invoicedTax);
						BigDecimal remainingProratedTax = BigDecimal
						.valueOf(extnTaxBreakup.tax).subtract(
								invoicedTax);
						totalProratedHeaderTax = totalProratedHeaderTax
						.add(remainingProratedTax);
						extnTaxBreakup.tax = remainingProratedTax
						.doubleValue();
						invoiceTaxes.add(extnTaxBreakup);
					}
				
			}
			taxCalculationOutput.colTax = invoiceTaxes;
			taxCalculationOutput.tax = totalProratedHeaderTax.doubleValue();


			return taxCalculationOutput;


		}
		
		Document taxOut = (Document) env.getTxnObject("TAX_CHANGE_ORDER_XML");
		if(!YFSObject.isVoid(taxOut)){
			
			Element eleHeaderTaxes = SCXmlUtil.getChildElement(taxOut.getDocumentElement(), "HeaderTaxes");
			ArrayList<YFSExtnTaxBreakup> taxes = new ArrayList<YFSExtnTaxBreakup>();
			if(!YFCObject.isVoid(eleHeaderTaxes)){
				
				if(eleHeaderTaxes.hasChildNodes()){
					
					NodeList nlHeaderTax = eleHeaderTaxes.getElementsByTagName("HeaderTax");
					for(int i = 0; i < nlHeaderTax.getLength(); i++){
						
						Element eleHeaderTax = (Element) nlHeaderTax.item(i);
						YFSExtnTaxBreakup breakup = new YFSExtnTaxBreakup();
						breakup.chargeCategory = eleHeaderTax.getAttribute("ChargeCategory");
						breakup.chargeName = eleHeaderTax.getAttribute("ChargeName");
						breakup.taxName = eleHeaderTax.getAttribute("TaxName");
						breakup.taxPercentage = Double.parseDouble(eleHeaderTax.getAttribute("TaxPercentage"));
						if(!YFCObject.isVoid(eleHeaderTax.getAttribute("Tax"))){
							breakup.tax = Double.parseDouble(eleHeaderTax.getAttribute("Tax"));
						}else{
							breakup.tax = 0.0;
						}
						taxes.add(breakup);
					}
					taxCalculationOutput.colTax = taxes;
				}
			}
			
			return taxCalculationOutput;
		}
		else
		{
			String orderHeaderKey = taxCalculationInput.orderHeaderKey;
			
			String isTaxComplete = (String) env
			.getTxnObject("IS_TAX_CALC_COMPLETED");

			// If this recalculate line tax ue was invoked because of a
			// changeOrder call from the tax hold monitor to update the
			// taxes, don't call taxes again.


			if (isTaxComplete == null) {
				Document inOrdDoc;
				try {
					inOrdDoc = XMLUtil.createDocument("Order");
				

				Element inOrderEle = inOrdDoc.getDocumentElement();
				inOrderEle.setAttribute("OrderHeaderKey", orderHeaderKey);
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate("getOrderDetails", "global/template/api/VSI_getOrderDetails_Tax.xml");

				Document orderDetails = api.invoke(env, "getOrderDetails",inOrdDoc);
				
				if("MigratedOrder".equals(orderDetails.getDocumentElement().getAttribute("OrderName")))
				{
					taxCalculationOutput.colTax = taxCalculationInput.colTax;
					taxCalculationOutput.tax = taxCalculationInput.tax;
					return taxCalculationOutput;
				}

				env.clearApiTemplate("getOrderDetails");
				// This will be used in Schedule Order Event to avoid an
				// additional API call to get order details
				env.setTxnObject("Order", orderDetails);
				//OMS-1239 start if there is no order line on the order than remove tax call
				Element eleOrderLine = (Element) orderDetails.getElementsByTagName(
						VSIConstants.ELE_ORDER_LINE).item(0);
				if(null != eleOrderLine){
				Document taxedDocument = api
						.executeFlow(env, "VSIVertexTaxCallService",
								orderDetails);

						String trIsSuccess = XMLUtil.getAttributeFromXPath(
								taxedDocument, "Order/Extn/@ExtnIsTaxCalculated");

						if (taxedDocument != null) {

							if ((trIsSuccess.equalsIgnoreCase("Y"))) {

								env.setTxnObject("IS_TAX_CALC_COMPLETED", "Y");
								env.setTxnObject("TAX_CALC_SUCCESSFUL", "Y");
								env.setTxnObject("TAX_CHANGE_ORDER_XML",
										taxedDocument);
							}
						}
						
				}
				//OMS-1239 End
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (YFSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (YIFClientCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			taxOut = (Document) env.getTxnObject("TAX_CHANGE_ORDER_XML");
			if(!YFSObject.isVoid(taxOut)){
				
				Element eleHeaderTaxes = SCXmlUtil.getChildElement(taxOut.getDocumentElement(), "HeaderTaxes");
				ArrayList<YFSExtnTaxBreakup> taxes = new ArrayList<YFSExtnTaxBreakup>();
				if(!YFCObject.isVoid(eleHeaderTaxes)){
					
					if(eleHeaderTaxes.hasChildNodes()){
						
						NodeList nlHeaderTax = eleHeaderTaxes.getElementsByTagName("HeaderTax");
						for(int i = 0; i < nlHeaderTax.getLength(); i++){
							
							Element eleHeaderTax = (Element) nlHeaderTax.item(i);
							YFSExtnTaxBreakup breakup = new YFSExtnTaxBreakup();
							breakup.chargeCategory = eleHeaderTax.getAttribute("ChargeCategory");
							breakup.chargeName = eleHeaderTax.getAttribute("ChargeName");
							breakup.taxName = eleHeaderTax.getAttribute("TaxName");
							breakup.taxPercentage = Double.parseDouble(eleHeaderTax.getAttribute("TaxPercentage"));
							if(!YFCObject.isVoid(eleHeaderTax.getAttribute("Tax"))){
								breakup.tax = Double.parseDouble(eleHeaderTax.getAttribute("Tax"));
							}else{
								breakup.tax = 0.0;
							}
							taxes.add(breakup);
						}
						taxCalculationOutput.colTax = taxes;
					}
				}
				
				return taxCalculationOutput;
			}
		}
		
		if (taxCalculationInput.colTax != null) {
			taxCalculationOutput.colTax = taxCalculationInput.colTax;
			taxCalculationOutput.tax = taxCalculationInput.tax;	

		}

		return taxCalculationOutput;

	}

}
