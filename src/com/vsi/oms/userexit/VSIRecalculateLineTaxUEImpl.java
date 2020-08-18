package com.vsi.oms.userexit;

import java.util.LinkedList;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;

public class VSIRecalculateLineTaxUEImpl implements YFSRecalculateLineTaxUE {

	/**
	 * get instance of logger
	 */
	private YFCLogCategory log = YFCLogCategory
	.instance(VSIRecalculateLineTaxUEImpl.class);
	public YFSExtnTaxCalculationOutStruct recalculateLineTax(YFSEnvironment env, YFSExtnLineTaxCalculationInputStruct taxCalculationInput) 
	throws YFSUserExitException {

		if(log.isDebugEnabled()){
			log.debug("Inside VSIRecalculateLineTaxUEImpl *******************************");
		}
		YFSExtnTaxCalculationOutStruct taxCalculationOutput = new YFSExtnTaxCalculationOutStruct();

		String orderLineKey = taxCalculationInput.orderLineKey;
		double orderLineQty = taxCalculationInput.lineQty;

		Document getOrderLineDetailsOutXML =null;

		if( !"".equals(orderLineKey)) {

			String strInvoiceMode = taxCalculationInput.invoiceMode;
			if(!YFCObject.isVoid(strInvoiceMode)){
				if(taxCalculationInput.bForInvoice 
						&& ("SHIPMENT").equalsIgnoreCase(strInvoiceMode) || strInvoiceMode.equals("RETURN")){

					try {

						Document getOrderLineDetailsInXML = new DocumentImpl();
						Element orderLine = getOrderLineDetailsInXML.createElement("OrderLineDetail");
						orderLine.setAttribute("OrderLineKey", orderLineKey);
						getOrderLineDetailsInXML.appendChild(orderLine);
//						log.verbose("InXML to getOrderLineDetailsInXML " + XMLUtil.getXMLString(getOrderLineDetailsInXML));
						env.setApiTemplate("getOrderLineDetails", "global/template/api/getOrderLineDetails.LineTaxUE.xml");
						getOrderLineDetailsOutXML = VSIUtils.invokeAPI(env, "getOrderLineDetails", getOrderLineDetailsInXML);
						env.clearApiTemplate("getOrderLineDetails");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					if(log.isDebugEnabled()){
						log.verbose("getOrderLineDetailsOutXML " + XMLUtil.getXMLString(getOrderLineDetailsOutXML));
					}
					NodeList taxesEle = getOrderLineDetailsOutXML.getElementsByTagName("LineTax");
					if(taxesEle.getLength() > 0)
					{
						Element taxEle = (Element)taxesEle.item(0);
						double tax = 0.0;

						Element extnTaxEle = (Element)taxEle.getElementsByTagName("Extn").item(0);

						if(extnTaxEle != null ) {
							String strTaxPerUnit = extnTaxEle.getAttribute("ExtnTaxPerUnit");
							String strTax = taxEle.getAttribute("Tax");
							if(YFCObject.isVoid(strTaxPerUnit) && orderLineQty != 0)
								strTaxPerUnit = String.valueOf(Double.parseDouble(strTax) / orderLineQty);

							tax = Double.parseDouble(strTaxPerUnit);

							taxCalculationOutput.tax = orderLineQty * tax;
							taxCalculationOutput.colTax = taxCalculationInput.colTax;
							return taxCalculationOutput;
						}
						else {
							taxCalculationOutput.tax = taxCalculationInput.tax;
							taxCalculationOutput.colTax = taxCalculationInput.colTax;
							return taxCalculationOutput;
						}
					}
					else {
						taxCalculationOutput.colTax = taxCalculationInput.colTax;
						taxCalculationOutput.tax = taxCalculationInput.tax;	
						return taxCalculationOutput;
					}
				} 
			}else {
				
				LinkedList<YFSExtnTaxBreakup> list1 = new LinkedList<YFSExtnTaxBreakup>();

				YFSExtnTaxBreakup ytb = new YFSExtnTaxBreakup();
				ytb.tax = 0.0;
				ytb.taxName = "Sales Tax";

				list1.add(ytb);
				taxCalculationOutput.colTax = list1;
				
				
				return taxCalculationOutput;
			}
		}
		return taxCalculationOutput;
	}

}
