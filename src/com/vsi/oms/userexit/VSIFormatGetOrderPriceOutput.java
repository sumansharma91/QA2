package com.vsi.oms.userexit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;

public class VSIFormatGetOrderPriceOutput {

	private YFCLogCategory log = YFCLogCategory.instance(VSIFormatGetOrderPriceOutput.class);

	public Document formatOutput(Document docInXML) throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIFormatOutput================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(docInXML));
		}
		
		Element eleOrder = docInXML.getDocumentElement();
		Double dOrderTotal = Double.parseDouble(eleOrder
				.getAttribute("LinePriceTotal"));
		Element eleAdjustments = XMLUtil.getElementByXPath(docInXML,
				"Order/Adjustments");
		NodeList nlOrderAdjustment = eleAdjustments
				.getElementsByTagName("Adjustment");
		int length = nlOrderAdjustment.getLength();

		////System.out.println("Length is " + length);

		// Creating orderRepricingUE output format document

		Document inputDoc = XMLUtil.createDocument("Order");
		Element eleRootElement = inputDoc.getDocumentElement();
		
		//YFCDocument inputDoc = YFCDocument.createDocument("Order");
		//YFCElement eleRootElement = inputDoc.getDocumentElement();
		eleRootElement.setAttribute("ValidatePromotionAward", "Y");
		Element eleOrderLines = inputDoc.createElement("OrderLines");
		eleRootElement.appendChild(eleOrderLines);
		
		Element eleInputOrderLines = XMLUtil.getElementByXPath(docInXML,
		"Order/OrderLines");
NodeList orderLineNL = eleInputOrderLines
		.getElementsByTagName("OrderLine");

		if (length > 0) {
								
				for (int k = 0; k < orderLineNL.getLength(); k++) {
					Element orderLineEle = (Element) orderLineNL.item(k);
					String sOrderLineKey = orderLineEle
							.getAttribute("LineID");
					String sPrimeLineNo = orderLineEle
							.getAttribute("PrimeLineNo");
					String sSubLineNo = orderLineEle.getAttribute("SubLineNo");
					//Creating OrderLine Tag
					Element eleOrderLine = inputDoc.createElement("OrderLine");
					eleOrderLines.appendChild(eleOrderLine);
					eleOrderLine
							.setAttribute("OrderLineKey", sOrderLineKey);
					eleOrderLine.setAttribute("PrimeLineNo", sPrimeLineNo);
					eleOrderLine.setAttribute("SubLineNo", sSubLineNo);
					
					// Creating LineCharges Tag
					Element eleLineCharges = inputDoc.createElement("LineCharges");
					eleOrderLine.appendChild(eleLineCharges);

					for (int i = 0; i < length; i++) {
						
						Element eleAdjustmentAction = (Element) nlOrderAdjustment.item(i);
						String sAdjustmentdescription = eleAdjustmentAction
								.getAttribute("Description");
						NodeList eleAdjustmentActions = eleAdjustmentAction
								.getElementsByTagName("AdjustmentAction");
						
						Element eleDiscount = (Element) eleAdjustmentActions.item(0);
						String sDiscount = (eleDiscount.getAttribute("Adjustment"));
						Double dHdrDiscnt = Double.parseDouble(sDiscount);
						Double dDiscount1 = Math.abs(dHdrDiscnt);
						
					
					if (dDiscount1 > 0) {

						String slineTotalEle = orderLineEle
								.getAttribute("LineTotal");
						Double dLineTotal = Double.parseDouble(slineTotalEle);

						////System.out.println("dOrderTotal" + dOrderTotal);
						////System.out.println("dLineTotal" + dLineTotal);
						////System.out.println("dHdrDiscnt" + dDiscount1);
						Double dLineDiscAmt = (dLineTotal / dOrderTotal)
								* dDiscount1;
						Double dRoundValue = (double) Math.round(dLineDiscAmt * 100);
						dLineDiscAmt = dRoundValue/100;
						String sLineDiscAmt = dLineDiscAmt.toString();

						

						
						Element eleLineCharge = inputDoc.createElement("LineCharge");
						eleLineCharges.appendChild(eleLineCharge);

						// Setting the values

						eleLineCharge
								.setAttribute("ChargeCategory", "Discount");
						eleLineCharge.setAttribute("ChargeName",
								sAdjustmentdescription);
						eleLineCharge.setAttribute("ChargePerLine",
								sLineDiscAmt);
						eleLineCharge.setAttribute("IsBillable", "Y");
						eleLineCharge.setAttribute("IsDiscount", "Y");

					}
				}

			}

		}
		else
		{
			for (int k = 0; k < orderLineNL.getLength(); k++) {
		
		Element orderLineEle = (Element) orderLineNL.item(k);
		String sOrderLineKey = orderLineEle
				.getAttribute("LineID");
		String sPrimeLineNo = orderLineEle
				.getAttribute("PrimeLineNo");
		String sSubLineNo = orderLineEle.getAttribute("SubLineNo");
		//Creating OrderLine Tag
		Element eleOrderLine = inputDoc.createElement("OrderLine");
		eleOrderLines.appendChild(eleOrderLine);
		eleOrderLine
				.setAttribute("OrderLineKey", sOrderLineKey);
		eleOrderLine.setAttribute("PrimeLineNo", sPrimeLineNo);
		eleOrderLine.setAttribute("SubLineNo", sSubLineNo);
		
		// Creating LineCharges Tag
		Element eleLineCharges = inputDoc.createElement("LineCharges");
		eleOrderLine.appendChild(eleLineCharges);
		//Taking LineAdjustments
		NodeList nlLineAdjustments = orderLineEle.getElementsByTagName("LineAdjustments");
		Element eleLineAdjustment = (Element) nlLineAdjustments.item(0);
		if(null!=eleLineAdjustment){
		NodeList nlLineAdjustment = eleLineAdjustment.getElementsByTagName("Adjustment");
		for(int j=0;j<nlLineAdjustment.getLength();j++)
		{
			
			
			Element eleLineAdjustmentAction = (Element) nlLineAdjustment.item(j);
			String sAdjustmentdescription = eleLineAdjustmentAction
					.getAttribute("Description");
			NodeList eleLineAdjustmentActions = eleLineAdjustmentAction
					.getElementsByTagName("AdjustmentAction");
			
			Element eleDiscount = (Element) eleLineAdjustmentActions.item(0);
			String sDiscount = (eleDiscount.getAttribute("Adjustment"));
			Double dHdrDiscnt = Double.parseDouble(sDiscount);
			Double dDiscount1 = Math.abs(dHdrDiscnt);
						
			String sLineDiscAmt = dDiscount1.toString();
		

			
			Element eleLineCharge = inputDoc.createElement("LineCharge");
			eleLineCharges.appendChild(eleLineCharge);

			// Setting the values

			eleLineCharge
					.setAttribute("ChargeCategory", "Discount");
			eleLineCharge.setAttribute("ChargeName",
					sAdjustmentdescription);
			eleLineCharge.setAttribute("ChargePerLine",
					sLineDiscAmt);
			eleLineCharge.setAttribute("IsBillable", "Y");
			eleLineCharge.setAttribute("IsDiscount", "Y");

			
		
		
			}
		}
			
		}

		

	}
		////System.out.println("Output document is " + XMLUtil.getXMLString(inputDoc));
		return inputDoc;
}
}
