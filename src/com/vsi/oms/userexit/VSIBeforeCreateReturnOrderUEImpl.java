package com.vsi.oms.userexit;

/*
 * author : Perficient
 * 
 * This class is implemented to copy the line charges and line taxes from the Sales Order to the corresponding Return Order
 * 
 * */

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

public class VSIBeforeCreateReturnOrderUEImpl implements
		YFSBeforeCreateOrderUE, VSIConstants {

	// Class level variables
	private static String CLASS_NAME = VSIBeforeCreateReturnOrderUEImpl.class
			.getName();

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIBeforeCreateReturnOrderUEImpl.class);
	
	/**
	 * Wrapper method which copies all the LineCharges and LineTaxes to the
	 * Return Order from the respective Sales Order.
	 * 
	 * @param YFSEnvironment
	 *            yfsEnv - variable that holds the Environment object Document
	 *            docInXML - Input document to the UE
	 * @return Document docInXML - Updated UE Input XML with the charges and
	 *         taxes copied from sales order
	 * @throws YFSUserExitException
	 */
	public Document beforeCreateOrder(YFSEnvironment yfsEnv, Document docInXML)
			throws YFSUserExitException {
		String METHOD_NAME = "beforeCreateOrder";
		if(log.isDebugEnabled()){
			log.debug("================Inside beforeCreateOrder method  - VSIBeforeCreateReturnOrderUEImpl.class ================================");

			log.debug("VSIBeforeCreateReturnOrderUEImpl-InXML:" + XmlUtils.getString(docInXML));
		}
		
		Document docGetSalesOrderListOutput = null;
		String strDerivedFromOrderHeaderKey = null;
		try {

			if (docInXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE) != null) {
				if (docInXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)
						.item(0) != null) {
					strDerivedFromOrderHeaderKey = ((Element) docInXML
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)
							.item(0)).getAttribute("DerivedFromOrderHeaderKey");

					if (!VSIUtils.isNullOrEmpty(strDerivedFromOrderHeaderKey)) {

						docGetSalesOrderListOutput = callGetSalesOrderList(
								yfsEnv, strDerivedFromOrderHeaderKey);

					}
					
					if(docGetSalesOrderListOutput != null){
						
						loopOrderLineofDocInXML(yfsEnv, docInXML,
								docGetSalesOrderListOutput,
								strDerivedFromOrderHeaderKey);
					}
					
				} else {
					if(log.isDebugEnabled()){
						log.debug("*****VSIBeforeCreateReturnOrderUEImpl:beforeCreateOrder-docInXML.getElementsByTagName(OrderLine).item(0) IS NULL..");
					}
				}
			} else {
				if(log.isDebugEnabled()){
					log.debug("****** VSIBeforeCreateReturnOrderUEImpl:beforeCreateOrder-docInXML.getElementsByTagName(OrderLine) IS NULL..");
				}
				}
		}

		catch (Exception e) {
			String strErrorDesc = "Exception in " + CLASS_NAME + "."
					+ METHOD_NAME + ": OrderHeaderKey="
					+ strDerivedFromOrderHeaderKey;
			log.error("################ Exception Occured ############# "
					+ strErrorDesc);
			YFSUserExitException ex = new YFSUserExitException();
			ex.setErrorDescription(strErrorDesc);
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}

		if(log.isDebugEnabled()){
			log.debug("VSIBeforeCreateReturnOrderUEImpl-OutXML:"
					+ YFCDocument.getDocumentFor(docInXML).getString());
		}
		return docInXML;
	}

	@Override
	public String beforeCreateOrder(YFSEnvironment arg0, String arg1)
			throws YFSUserExitException {
		return null;
	}

	/**
	 * To call getOrderList API with the OrderHeaderKey as input. This will
	 * retrieve all the necessary details (like Charges and Taxes) from the
	 * Sales Order.
	 * 
	 * @param YFSEnvironment
	 *            yfsEnv - variable that holds the Environment object String
	 *            strDerivedFromOrderHeaderKey - OrderHeaderKey from the Sales
	 *            Order.
	 * @return Document docGetSalesOrderListOutput - Output of getOrderList API
	 * @throws Exception
	 */
	private Document callGetSalesOrderList(YFSEnvironment yfsEnv,
			String strDerivedFromOrderHeaderKey) throws Exception {

		

		Document docGetSalesOrderListInput = VSIUtils.createDocument("Order");
		Element eleGetSalesOrderListInput = VSIUtils
				.getRootElement(docGetSalesOrderListInput);

		XMLUtil.setAttribute(eleGetSalesOrderListInput, "OrderHeaderKey",
				strDerivedFromOrderHeaderKey);
		
		if(log.isDebugEnabled()){
			log.debug("VSIBeforeCreateReturnOrderUEImpl.getOrderList()-InXML:"
					+ YFCDocument.getDocumentFor(docGetSalesOrderListInput)
					.getString());
		}

		Document docGetSalesOrderListOutput = VSIUtils
				.invokeAPI(
						yfsEnv,
						"global/template/api/getOrderListSO_VSIBeforeCreateReturnOrderUEImpl.xml",
						VSIConstants.API_GET_ORDER_LIST, docGetSalesOrderListInput);

		// Returning the getOrderList API output back to the calling method
		if(log.isDebugEnabled()){
			log.debug("VSIBeforeCreateReturnOrderUEImpl.getOrderList()-OutXML:"
					+ YFCDocument
					.getDocumentFor(docGetSalesOrderListOutput)
					.getString());
		}
		return docGetSalesOrderListOutput;
	}

	/**
	 * To loop through each OrderLine element under Return Order. This will in
	 * turn call the method to loop through the getOrderList API output to find
	 * the corresponding OrderLine.
	 * 
	 * @param YFSEnvironment
	 *            yfsEnv - variable that holds the Environment object Document
	 *            docInXML - Input Document to the UE Document
	 *            docGetSalesOrderListOutput - Output of getOrderList API
	 *            
	 *            - OrderHeaderKey of Sales Order.
	 * @return void
	 * @throws Exception
	 */
	private void loopOrderLineofDocInXML(YFSEnvironment yfsEnv,
			Document docInXML, Document docGetSalesOrderListOutput,
			String strDerivedFromOrderHeaderKey) throws Exception {

		if(log.isDebugEnabled()){
			log.debug("*** Inside Method loopOrderLineofDocInXML");
		}
		if (docInXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE) != null) {
			int iNoOfOrderLines = docInXML.getElementsByTagName(
					VSIConstants.ELE_ORDER_LINE).getLength();

			// Looping through the Orderline elements of Return order
			for (int iLoop = 0; iLoop < iNoOfOrderLines; iLoop++) {

				Element eleOrderLineDocInXML = (Element) docInXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)
						.item(iLoop);

				// Calling the method to loop through the OrderLines element of
				// getOrderList API output
				if(eleOrderLineDocInXML != null){
					matchOrderLineofGSOLOutput(yfsEnv, eleOrderLineDocInXML,
							docGetSalesOrderListOutput, docInXML,
							strDerivedFromOrderHeaderKey);
				}
				
			}
		} else {
			if(log.isDebugEnabled()){
				log.debug("######################## VSIBeforeCreateReturnOrderUEImpl:loopOrderLineofDocInXML-docInXML.getElementsByTagName(OrderLine) IS NULL..");
			}
		}
	}

	/**
	 * To loop through each OrderLine element under getOrderList API output.
	 * This will in turn call the method to check whether the returned quantity
	 * is the last quantity of the current Orderline. Also the methods that
	 * prorate charges and taxes are called from here.
	 * 
	 * @param YFSEnvironment
	 *            yfsEnv - variable that holds the Environment object Document
	 *            docGetSalesOrderListOutput - getOrderList API output for Sales
	 *            Order Element eleOrderLineDocInXML - Element that holds the
	 *            Current OrderLine element of Return Order Document docInXML -
	 *            Input Document to the UE Document
	 *            - OrderHeaderKey from the Sales Order.
	 * @return void
	 * @throws Exception
	 */
	private void matchOrderLineofGSOLOutput(YFSEnvironment yfsEnv,
			Element eleOrderLineDocInXML, Document docGetSalesOrderListOutput,
			Document docInXML, String strDerivedFromOrderHeaderKey)
			throws Exception {
		if(log.isDebugEnabled()){
			log.debug("*** In side Method matchOrderLineofGSOLOutput ");
		}
		
		String headerChargeCategory = null;
		String headerChargeName = null;
		BigDecimal bHeaderChargeAmount = new BigDecimal("0.00");
		Element eleSOOrder = (Element)docGetSalesOrderListOutput.getElementsByTagName(ELE_ORDER).item(0);
		String entryType = eleSOOrder.getAttribute(ATTR_ENTRY_TYPE);
		////System.out.println(" *** entryType "+ entryType);
		String strEntCode = eleSOOrder.getAttribute(ATTR_ENTERPRISE_CODE);
		String enteredBy = eleSOOrder.getAttribute("EnteredBy");
		////System.out.println(" *** enteredBy "+ enteredBy);
		String strDerivedFromOrderLineKey = XMLUtil.getAttribute(
				eleOrderLineDocInXML, "DerivedFromOrderLineKey");
		Map<String, String> exclReturnReasonMap = new HashMap<String, String>();
		String strOrderLineType = eleOrderLineDocInXML.getAttribute(ATTR_LINE_TYPE);
		String strReturnReason = eleOrderLineDocInXML.getAttribute(ATTR_RETURN_REASON);
		boolean isPOSOrder = false;
		if(entryType.equalsIgnoreCase(POS_ENTRY_TYPE)  && enteredBy.equalsIgnoreCase(ENTERPRISE_SELLING)){
			isPOSOrder = true;
			try{
				Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

				Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,strEntCode);
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,VSIConstants.VSI_INCLD_RETURN_REASON);

				Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(yfsEnv,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);
				
				
				if (docAfterGetCommonCodeList != null) {
					NodeList commonCodeNL = docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE);
					if(commonCodeNL.getLength() > 0){
						
						for(int k= 0 ; k <commonCodeNL.getLength(); k++ ){
							Element commonCodeEle = (Element) commonCodeNL.item(k);
							if(commonCodeEle != null){
								exclReturnReasonMap.put(commonCodeEle.getAttribute(VSIConstants.ATTR_CODE_VALUE), "");
							}
						}
						//log.info("*** Printing Excluded Return Reason Codes: "+exclReturnReasonMap);
					}
				
					
				}}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			
		}
		
		// making the Order line type as empty because if it is populated with any value 
		//then when CCK return method is opted , then linetype doesn't get updated as Credit
		eleOrderLineDocInXML.setAttribute("LineType", "");
		
		String strXPATHOrderLineKey = VSIGeneralUtils
				.formXPATHWithOneCondition(
						"/OrderList/Order/OrderLines/OrderLine",
						"OrderLineKey", strDerivedFromOrderLineKey);
		Element eleOrderSO = (Element) docGetSalesOrderListOutput
				.getDocumentElement().getElementsByTagName("Order").item(0);
		
		Element eleHeaderChargeSO = (Element) eleOrderSO.getElementsByTagName(
				"HeaderCharge").item(0);
		
		if(eleHeaderChargeSO != null){
			headerChargeCategory = eleHeaderChargeSO
					.getAttribute("ChargeCategory");
			headerChargeName = eleHeaderChargeSO.getAttribute("ChargeName");
			bHeaderChargeAmount = new BigDecimal(XMLUtil.getAttribute(
					eleHeaderChargeSO, "ChargeAmount"));
		}
		
		BigDecimal bTotalOrderLinesOnSO = new BigDecimal(eleOrderSO
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).getLength());
		BigDecimal bHeaderChargeAmtPerSalesOrderLine = new BigDecimal("0.00");
		if (bHeaderChargeAmount.floatValue() > 0.0f
				&& bTotalOrderLinesOnSO.floatValue() > 0.0f) {
			bHeaderChargeAmtPerSalesOrderLine = VSIGeneralUtils
					.roundOffBigDecimal(VSIGeneralUtils.bigDecimalDivide(
							bHeaderChargeAmount, bTotalOrderLinesOnSO)
							.toString());
		}
		NodeList nlOrderLineGSOL = XMLUtil.getNodeListByXpath(
				docGetSalesOrderListOutput, strXPATHOrderLineKey);
		BigDecimal bTaxPerUnit = new BigDecimal("0.00");
		BigDecimal bTotalTaxOnReturn = new BigDecimal("0.00");
		BigDecimal bReturnQuantity = new BigDecimal(XMLUtil.getAttribute(
				eleOrderLineDocInXML, "OrderedQty"));
		Boolean isHeaderChargeApplied = false;
		if (nlOrderLineGSOL != null) {
			BigDecimal bSOLineQty = new BigDecimal("0.00");
			NodeList lineTaxFromSalesNL = null;
			Element eleLineTaxfromSales = null;
			Element eleOrderLineGSOL = (Element) nlOrderLineGSOL.item(0);
			if(eleOrderLineGSOL != null){
				bSOLineQty = new BigDecimal(XMLUtil.getAttribute(
						eleOrderLineGSOL, "OrderedQty"));
				if(eleOrderLineGSOL
						.getElementsByTagName("LineTax").getLength() > 0){
					lineTaxFromSalesNL = eleOrderLineGSOL
							.getElementsByTagName("LineTax");
				}
				
			}
			
			Element eleLineChargesReturn = docInXML
					.createElement("LineCharges");
			eleOrderLineDocInXML.appendChild(eleLineChargesReturn);
			
			// copy LineCharges
			if (eleOrderLineGSOL.getElementsByTagName("LineCharges") != null && eleOrderLineGSOL.getElementsByTagName("LineCharges")
					.getLength() > 0) {
				
				Element eleLineChargesfromSO = (Element) eleOrderLineGSOL
						.getElementsByTagName("LineCharges").item(0);

				NodeList nlLineChargeListFromSO = eleLineChargesfromSO
						.getElementsByTagName("LineCharge");

				String strChargeCatgryFromSOLine = null;
				for (int i = 0; i < nlLineChargeListFromSO.getLength(); i++) {
					Element eleLineChargeFromSO = (Element) nlLineChargeListFromSO
							.item(i);
					strChargeCatgryFromSOLine = eleLineChargeFromSO
							.getAttribute("ChargeCategory");
					BigDecimal bChargeAmount = new BigDecimal(
							eleLineChargeFromSO.getAttribute("ChargeAmount"));
					if(isPOSOrder && strOrderLineType.equalsIgnoreCase(LINETYPE_STH) && exclReturnReasonMap.size() > 0 
							&& !exclReturnReasonMap.containsKey(strReturnReason) && strChargeCatgryFromSOLine.equalsIgnoreCase(SHIPPING_CHARGE_CTGY)){
						continue;
					}else{
					if (bChargeAmount.floatValue() > 0.0f
							&& bReturnQuantity.floatValue() != bSOLineQty
									.floatValue()) {
						bChargeAmount = VSIGeneralUtils
								.roundOffBigDecimal(VSIGeneralUtils
										.bigDecimalMultiply(
												(VSIGeneralUtils
														.bigDecimalDivide(
																bChargeAmount,
																bSOLineQty)),
												bReturnQuantity).toString());
					}

					
						Element eleLineCharge = docInXML
								.createElement("LineCharge");
						eleLineCharge.setAttribute("ChargeCategory",
								strChargeCatgryFromSOLine);
						eleLineCharge.setAttribute("ChargePerLine",
								bChargeAmount.toString());
						eleLineCharge.setAttribute("ChargeName",
								eleLineChargeFromSO.getAttribute("ChargeName"));
						Element eleChargeExtn = (Element) eleLineChargeFromSO
						.getElementsByTagName("Extn").item(0);
						XMLUtil.importElement(eleLineCharge,eleChargeExtn);
						eleLineChargesReturn.appendChild(eleLineCharge);

						if (strChargeCatgryFromSOLine
								.equalsIgnoreCase(headerChargeCategory)) {
							isHeaderChargeApplied = true;
						}
						
					}
					
					

				}
				

			}
			
			if(isPOSOrder && strOrderLineType.equalsIgnoreCase(LINETYPE_STH) && exclReturnReasonMap.size() > 0 
					&& !exclReturnReasonMap.containsKey(strReturnReason) && headerChargeCategory.equalsIgnoreCase(SHIPPING_CHARGE_CTGY)){
			// Do nothing
			
			}else{
			if (bHeaderChargeAmtPerSalesOrderLine.floatValue() > 0.0f
					&& !isHeaderChargeApplied) {
				BigDecimal bRevisedHeaderChargeAmtForReturn = VSIGeneralUtils
						.roundOffBigDecimal(VSIGeneralUtils
								.bigDecimalMultiply(
										(VSIGeneralUtils
												.bigDecimalDivide(
														bHeaderChargeAmtPerSalesOrderLine,
														bSOLineQty)),
										bReturnQuantity).toString());

				Element eleLineCharge = docInXML
						.createElement("LineCharge");
				eleLineCharge.setAttribute("ChargeCategory",
						headerChargeCategory);
				eleLineCharge.setAttribute("ChargePerLine",
						bRevisedHeaderChargeAmtForReturn.toString());
				eleLineCharge.setAttribute("ChargeName", headerChargeName);
				//XMLUtil.importElement(eleLineCharge,eleExtn);
				eleLineChargesReturn.appendChild(eleLineCharge);

			}
			}

			// copy LineTaxes
			if (eleOrderLineGSOL.getElementsByTagName("LineTaxes") != null && eleOrderLineGSOL.getElementsByTagName("LineTaxes").getLength() > 0) {
				/*Element eleLineTaxesfromSO = (Element) eleOrderLineGSOL
						.getElementsByTagName("LineTaxes").item(0);
				XMLUtil.importElement(eleOrderLineDocInXML, eleLineTaxesfromSO);*/
				Element eleLineTaxesForReturn =  docInXML
						.createElement("LineTaxes");
				eleOrderLineDocInXML.appendChild(eleLineTaxesForReturn);

				if (lineTaxFromSalesNL != null && lineTaxFromSalesNL.getLength() > 0 ) {
					String strTaxName = null;
					for(int j=0; j < lineTaxFromSalesNL.getLength() ; j++ ){
						eleLineTaxfromSales = (Element) lineTaxFromSalesNL.item(j);
						bTotalTaxOnReturn = new BigDecimal(
								eleLineTaxfromSales.getAttribute("Tax"));
						strTaxName = eleLineTaxfromSales.getAttribute("TaxName");
						if(isPOSOrder && strOrderLineType.equalsIgnoreCase(LINETYPE_STH) && exclReturnReasonMap.size() > 0 
								&& !exclReturnReasonMap.containsKey(strReturnReason) && strTaxName.equalsIgnoreCase(SHIPPING_TAX)){
							//do nothing
							continue;
						}else{
							
						
						
						Element eleExtn = (Element) eleLineTaxfromSales
								.getElementsByTagName("Extn").item(0);
						if (eleExtn != null && !eleExtn.equals("")) {
							//System.out.println(" Inside condition" );
							if(eleExtn.getAttribute("ExtnTaxPerUnit") != null && !eleExtn.getAttribute("ExtnTaxPerUnit").equalsIgnoreCase("")){
								//System.out.println("ExtnTaxPerUnit " +eleExtn.getAttribute("ExtnTaxPerUnit") );
							bTaxPerUnit = new BigDecimal(
									(eleExtn.getAttribute("ExtnTaxPerUnit")));
							}else{
								BigDecimal bTax =new BigDecimal( eleLineTaxfromSales.getAttribute("Tax"));
								//System.out.println("Tax " +eleLineTaxfromSales.getAttribute("Tax") );
								//System.out.println("bSOLineQty " +bSOLineQty.toString() );
								bTaxPerUnit =VSIGeneralUtils
										.roundOffBigDecimal( VSIGeneralUtils
										.bigDecimalDivide(
												bTax,
												bSOLineQty).toString());
								//System.out.println("bTaxPerUnit " +bTaxPerUnit.toString() );
							}
						}
						
						if (bReturnQuantity.floatValue() > 0.0f
								&& bTaxPerUnit.floatValue() > 0.0f
								&& bReturnQuantity.floatValue() != bSOLineQty
										.floatValue()) {
							bTotalTaxOnReturn = VSIGeneralUtils
									.roundOffBigDecimal(VSIGeneralUtils
											.bigDecimalMultiply(bTaxPerUnit,
													bReturnQuantity).toString());
						}
							Element eleLineTax = docInXML
									.createElement("LineTax");
							eleLineTax.setAttribute("Tax",
									bTotalTaxOnReturn.toString());
							eleLineTax.setAttribute("RemainingTax",
									bTotalTaxOnReturn.toString());
							eleLineTax.setAttribute("InvoicedTax", "0.00");
							eleLineTax.setAttribute("ChargeCategory", eleLineTaxfromSales.getAttribute("ChargeCategory"));
							eleLineTax.setAttribute("ChargeName", eleLineTaxfromSales.getAttribute("ChargeName"));
							eleLineTax.setAttribute("TaxName", eleLineTaxfromSales.getAttribute("TaxName"));
							XMLUtil.importElement(eleLineTax,eleExtn);
							eleLineTaxesForReturn.appendChild(eleLineTax);
							if(log.isDebugEnabled()){
								log.debug("eleLineTaxFromReturn:"
										+ XMLUtil.getElementString(eleLineTax));
							}
							
							
						
						
						
					}	
					}
					

				}

				
				if(log.isDebugEnabled()){
					log.debug("eleOrderLineGSOL:"
							+ XMLUtil.getElementString(eleOrderLineGSOL));
					log.debug("bTaxPerUnit:" + bTaxPerUnit.toString());
					log.debug("bReturnQuantity:"
							+ bReturnQuantity.toString());
					log.debug("bTotalTaxOnReturn:" + bTotalTaxOnReturn);
				}
				
			}

		}
		if(log.isDebugEnabled()){
			log.debug("docInXML:" + XMLUtil.getXMLString(docInXML));
		}
	}

}
