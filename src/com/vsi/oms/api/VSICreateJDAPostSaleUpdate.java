package com.vsi.oms.api;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICreateJDAPostSaleUpdate implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSICreateJDAPostSaleUpdate.class);
	
	public Document createJDAPostSaleUpdate(YFSEnvironment env, Document inXML) throws Exception{
		
		log.beginTimer("VSICreateJDAPostSaleUpdate.createJDAPostSaleUpdate(): Begin");
		if(log.isDebugEnabled()){
			log.debug("VSICreateJDAPostSaleUpdate.createJDAPostSaleUpdate() Input XML: " + SCXmlUtil.getString(inXML));
		}
		
		String invoiceType = SCXmlUtil.getXpathAttribute(inXML.getDocumentElement(), "/InvoiceDetailList/InvoiceDetail/InvoiceHeader/@InvoiceType");
		if(!(invoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT) || invoiceType.equalsIgnoreCase(STR_RETURN)) ){
			return inXML;
		}
		
		collateLineCharges(env, inXML);
		
		if(log.isDebugEnabled()){
			log.debug("VSICreateJDAPostSaleUpdate.createJDAPostSaleUpdate() Output XML: " + SCXmlUtil.getString(inXML));
		}
		
		log.endTimer("VSICreateJDAPostSaleUpdate.createJDAPostSaleUpdate(): End");
		return inXML;
	}
	
	private void collateLineCharges(YFSEnvironment env, Document inXML){
		
		if(log.isDebugEnabled()){
			log.debug("VSICreateJDAPostSaleUpdate.collateLineCharges() Input XML: " + SCXmlUtil.getString(inXML));
		}
		
		HashMap<String, String> hm_OLK_JDAXferNo = new HashMap<String, String>();
		
		try{

			DecimalFormat df = new DecimalFormat(VSIConstants.DEC_FORMAT);
			df.setRoundingMode(RoundingMode.HALF_UP);

			NodeList nlInvoiceHeader = inXML.getElementsByTagName(ELE_INVOICE_HEADER);
			for(int a = 0; a < nlInvoiceHeader.getLength(); a++){

				Element eleInvoiceHeader = (Element) nlInvoiceHeader.item(a);
				String strInvoiceType = SCXmlUtil.getAttribute(eleInvoiceHeader, ATTR_INVOICE_TYPE);
				String strShipmentJDATransferNo = "";
				
				if(strInvoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT)){

					strShipmentJDATransferNo = SCXmlUtil.getXpathAttribute(eleInvoiceHeader, "Shipment/ShipmentLines/ShipmentLine/OrderRelease/Extn/@ExtnJDATransferNumber");
				}else if(strInvoiceType.equalsIgnoreCase(STR_RETURN)){
				
					hm_OLK_JDAXferNo.clear();
					Element eleOrderReleaseList = SCXmlUtil.getChildElement(eleInvoiceHeader, ELE_ORDER_RELEASE_LIST);
					if(eleOrderReleaseList.hasChildNodes()){
						
						NodeList nlOrderRelease = eleOrderReleaseList.getElementsByTagName(ELE_ORDER_RELEASE);
						for(int b = 0; b < nlOrderRelease.getLength(); b++){
							
							Element eleOrderRelease = (Element) nlOrderRelease.item(b);
							Element eleExtn = SCXmlUtil.getChildElement(eleOrderRelease, ELE_EXTN);
							if(!YFCObject.isVoid(eleExtn)){
								strShipmentJDATransferNo = SCXmlUtil.getAttribute(eleExtn, ATTR_EXTN_JDA_TRANS_NO);
							}
							
							if(!YFCObject.isVoid(strShipmentJDATransferNo)){
								
								NodeList nlOrderLine = eleOrderRelease.getElementsByTagName(ELE_ORDER_LINE);
								for(int c = 0; c < nlOrderLine.getLength(); c++){
									
									Element eleOrderLine = (Element) nlOrderLine.item(c);
									String strOrderLineKey = SCXmlUtil.getAttribute(eleOrderLine, ATTR_ORDER_LINE_KEY);
									if(!YFCObject.isVoid(strOrderLineKey)){
										hm_OLK_JDAXferNo.put(strOrderLineKey, strShipmentJDATransferNo);
									}
								}
							}
						}
					}
				}
				
				NodeList nlLineDetail = eleInvoiceHeader.getElementsByTagName(ELE_LINE_DETAIL);
				for(int i = 0; i < nlLineDetail.getLength(); i++){

					Double dblNetLineChargeAmount = 0.00;
					Double dblNetLineTotal = 0.00;
					String strItemID = "";
					String strDerivedFromOrderLineKey = "";
					Element eleLineDetail = (Element) nlLineDetail.item(i);
					Element eleOrderLine = SCXmlUtil.getChildElement(eleLineDetail, ELE_ORDER_LINE);
					if(!YFCObject.isVoid(eleOrderLine)){
						strDerivedFromOrderLineKey = SCXmlUtil.getAttribute(eleOrderLine, ATTR_DERIVED_FROM_ORDER_LINE_KEY);
					}
					
					Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM);
					if(!YFCObject.isVoid(eleItem)){
						strItemID = SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID);
					}
					
					Element eleLinePriceDetails = SCXmlUtil.createChild(eleLineDetail, ELE_LINE_PRICE_DETAILS);
					Integer intShippedQty = SCXmlUtil.getIntAttribute(eleLineDetail, ATTR_SHIP_QTY);
					String strInputUnitPrice = SCXmlUtil.getAttribute(eleLineDetail, ATTR_UNIT_PRICE);
					Double dblExtendedPrice = SCXmlUtil.getDoubleAttribute(eleLineDetail, ATTR_EXTENDED_PRICE);
					Double dblCharges = SCXmlUtil.getDoubleAttribute(eleLineDetail, ATTR_CHARGES);
					if(dblCharges != 0){

						NodeList nlLineCharge = eleLineDetail.getElementsByTagName(ELE_LINE_CHARGE);
						for(int j = 0; j < nlLineCharge.getLength(); j++){

							Element eleLineCharge = (Element) nlLineCharge.item(j);
							String strChargeName = SCXmlUtil.getAttribute(eleLineCharge, ATTR_CHARGE_NAME);
							if(strChargeName.toUpperCase().contains(UPPER_CASE_SWEETENED)){ // Ignore elements where ChargeName contains Sweetened
								continue;
							}else{
								String strIsDiscount = SCXmlUtil.getAttribute(eleLineCharge, ATTR_IS_DISCOUNT);
								Double dblChargeAmount = SCXmlUtil.getDoubleAttribute(eleLineCharge, ATTR_CHARGE_AMOUNT);
								if(strIsDiscount.equalsIgnoreCase(FLAG_Y)){

									dblNetLineChargeAmount = dblNetLineChargeAmount - dblChargeAmount;
								}else{

									dblNetLineChargeAmount = dblNetLineChargeAmount + dblChargeAmount;
								}
							}
						}

						if(dblNetLineChargeAmount != 0.0){

							dblNetLineTotal = dblExtendedPrice + dblNetLineChargeAmount;
							Double dblNewUnitPrice = Double.valueOf(df.format(dblNetLineTotal / intShippedQty));
							Double dblRemainingCharge = Double.valueOf(df.format(dblNetLineTotal - dblNewUnitPrice * intShippedQty));
							Integer intLineQty = (int) (dblRemainingCharge * 100);
							Boolean boolAddOneCent = true;
							if(intLineQty < 0){
								intLineQty = java.lang.Math.abs(intLineQty);
								boolAddOneCent = false;
							}
							dblExtendedPrice = 0.00;

							//if(intLineQty != 0.0){

								// First LinePriceDetail values being set
								Element eleLinePriceDetail = SCXmlUtil.createChild(eleLinePriceDetails, ELE_LINE_PRICE_DETAIL);
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ORDER_LINE_KEY, SCXmlUtil.getAttribute(eleLineDetail, ATTR_ORDER_LINE_KEY));
								
								if(strInvoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT)){ // If InvoiceType = SHIPMENT, get JDA xfer no. from shipment details

									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
								}
								
								if(!YFCObject.isVoid(strDerivedFromOrderLineKey)){ // If RETURN, get JDA xfer no from hashmap
									
									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_DERIVED_FROM_ORDER_LINE_KEY, strDerivedFromOrderLineKey);
									if(hm_OLK_JDAXferNo.containsKey(strDerivedFromOrderLineKey)){
										
										strShipmentJDATransferNo = (String) hm_OLK_JDAXferNo.get(strDerivedFromOrderLineKey);
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
									}
								}
								
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ITEM_ID, strItemID);
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_UNIT_PRICE, dblNewUnitPrice);
								Integer iniFirstShippedQty = intShippedQty - intLineQty;
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_SHIP_QTY, iniFirstShippedQty);
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTENDED_PRICE, df.format((dblNewUnitPrice) * iniFirstShippedQty));
								dblExtendedPrice = dblExtendedPrice + iniFirstShippedQty * (dblNewUnitPrice);

								if(intShippedQty - iniFirstShippedQty > 0){
									
									// Second LinePriceDetail values being set
									eleLinePriceDetail = SCXmlUtil.createChild(eleLinePriceDetails, ELE_LINE_PRICE_DETAIL);
									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ORDER_LINE_KEY, SCXmlUtil.getAttribute(eleLineDetail, ATTR_ORDER_LINE_KEY));
									
									if(strInvoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT)){

										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
									}
									
									if(!YFCObject.isVoid(strDerivedFromOrderLineKey)){
										
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_DERIVED_FROM_ORDER_LINE_KEY, strDerivedFromOrderLineKey);
										if(hm_OLK_JDAXferNo.containsKey(strDerivedFromOrderLineKey)){
											
											strShipmentJDATransferNo = (String) hm_OLK_JDAXferNo.get(strDerivedFromOrderLineKey);
											SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
										}
									}
									
									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ITEM_ID, strItemID);
									if(boolAddOneCent){
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_UNIT_PRICE, df.format(dblNewUnitPrice + 0.01));
									}else{
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_UNIT_PRICE, df.format(dblNewUnitPrice - 0.01));
									}
									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_SHIP_QTY, intShippedQty - iniFirstShippedQty);
									if(boolAddOneCent){
										
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTENDED_PRICE, df.format((dblNewUnitPrice + 0.01) * (intShippedQty - iniFirstShippedQty)));
										dblExtendedPrice = dblExtendedPrice + (intShippedQty - iniFirstShippedQty) * (dblNewUnitPrice + 0.01);
									}else{
										
										SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTENDED_PRICE, df.format((dblNewUnitPrice - 0.01) * (intShippedQty - iniFirstShippedQty)));
										dblExtendedPrice = dblExtendedPrice + (intShippedQty - iniFirstShippedQty) * (dblNewUnitPrice - 0.01);
									}
								}
							//}
						}else{

							// No charge/ discount is left over after dividing line total by quantity.
							Element eleLinePriceDetail = SCXmlUtil.createChild(eleLinePriceDetails, ELE_LINE_PRICE_DETAIL);
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ORDER_LINE_KEY, SCXmlUtil.getAttribute(eleLineDetail, ATTR_ORDER_LINE_KEY));
							
							if(strInvoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT)){

								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
							}
							
							if(!YFCObject.isVoid(strDerivedFromOrderLineKey)){
								
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_DERIVED_FROM_ORDER_LINE_KEY, strDerivedFromOrderLineKey);
								if(hm_OLK_JDAXferNo.containsKey(strDerivedFromOrderLineKey)){
									
									strShipmentJDATransferNo = (String) hm_OLK_JDAXferNo.get(strDerivedFromOrderLineKey);
									SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
								}
							}
							
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ITEM_ID, strItemID);
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_UNIT_PRICE, strInputUnitPrice);
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_SHIP_QTY, intShippedQty);
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTENDED_PRICE, SCXmlUtil.getDoubleAttribute(eleLineDetail, ATTR_EXTENDED_PRICE));
						}
					}else{

						// No charge/ discount is left over after dividing line total by quantity.
						Element eleLinePriceDetail = SCXmlUtil.createChild(eleLinePriceDetails, ELE_LINE_PRICE_DETAIL);
						SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ORDER_LINE_KEY, SCXmlUtil.getAttribute(eleLineDetail, ATTR_ORDER_LINE_KEY));
						
						if(strInvoiceType.equalsIgnoreCase(INVOICE_TYPE_SHIPMENT)){

							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
						}
						
						if(!YFCObject.isVoid(strDerivedFromOrderLineKey)){
							
							SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_DERIVED_FROM_ORDER_LINE_KEY, strDerivedFromOrderLineKey);
							if(hm_OLK_JDAXferNo.containsKey(strDerivedFromOrderLineKey)){
								
								strShipmentJDATransferNo = (String) hm_OLK_JDAXferNo.get(strDerivedFromOrderLineKey);
								SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTN_JDA_TRANS_NO, strShipmentJDATransferNo);
							}
						}
						
						SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_ITEM_ID, strItemID);
						SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_UNIT_PRICE, strInputUnitPrice);
						SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_SHIP_QTY, intShippedQty);
						SCXmlUtil.setAttribute(eleLinePriceDetail, ATTR_EXTENDED_PRICE, SCXmlUtil.getDoubleAttribute(eleLineDetail, ATTR_EXTENDED_PRICE));
					}
				}
			}
		}catch(Exception e){
			
			e.printStackTrace();
			log.error("Error in: VSICreateJDAPostSaleUpdate.collateLineCharges()");
			throw new YFSException(e.getMessage());
		}finally{
			
			if(!YFCObject.isVoid(hm_OLK_JDAXferNo)){
				hm_OLK_JDAXferNo.clear();
				hm_OLK_JDAXferNo = null;
			}
		}
		
		if(log.isDebugEnabled()){
			log.debug("VSICreateJDAPostSaleUpdate.collateLineCharges() Output XML: " + SCXmlUtil.getString(inXML));
		}
	}
}
