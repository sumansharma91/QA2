package com.vsi.oms.userexit;

import com.vsi.oms.utils.XMLUtil;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineChargeStruct;
import com.yantra.yfs.japi.YFSExtnOutputLineChargesShipment;
import com.yantra.yfs.japi.YFSExtnInputLineChargesShipment;
import com.yantra.yfs.japi.ue.YFSGetLineChargesForShipmentUE;

/*
 * OMS-794 changes
 * This class pro-rate the line charges based on the shipped quantities and stamp it to the out struct.
 */

public class VSICalculateLineChargeUE implements
		YFSGetLineChargesForShipmentUE, VSIConstants {

	YIFApi api;

	/**
	 * Instance of logger
	 */
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICalculateLineChargeUE.class.getName());

	public YFSExtnOutputLineChargesShipment getLineChargesForShipment(
			YFSEnvironment env, YFSExtnInputLineChargesShipment lineChargeInput) {
		YFSExtnOutputLineChargesShipment LineChargeCalculation = new YFSExtnOutputLineChargesShipment();
		
		if(log.isDebugEnabled()){
			log.debug("================Inside VSICalculateLineChargeUE================================");
		}
		
		List orderLineCharges = lineChargeInput.orderLineCharges;
		Double shipmentQty = lineChargeInput.shipmentQty;
		String sOrderHeaderkey = lineChargeInput.orderHeaderKey;
		String sOrderLinekey = lineChargeInput.orderLineKey;
		Double sOrderedQty = lineChargeInput.orderLineOrderedQty;

		try {
			
			Document orderLineDoc = XMLUtil.createDocument("OrderLine");
			Element orderLineEle = orderLineDoc.getDocumentElement();
			orderLineEle.setAttribute("OrderHeaderKey", sOrderHeaderkey);
			orderLineEle.setAttribute("OrderLinekey", sOrderLinekey);
			int size = orderLineCharges.size();
			
			for (int i = 0; i < size; i++) {
				
				if(log.isDebugEnabled()){
					log.debug("================Line charges exist========================");
				}
				
				YFSExtnLineChargeStruct obj = (YFSExtnLineChargeStruct) orderLineCharges
						.get(i);
				String chargeCategory = obj.chargeCategory;
				Double chargePerLine = obj.chargePerLine;

				if ("Discount".equals(chargeCategory)) {
					if(log.isDebugEnabled()){
						log.debug("================Line charge category Discount Exists========================");
					}
					
					/*
					 * Check if it is the last invoice for the order line
					 * If Yes, get per unit charge and multiply it by previously invoiced qty
					 */
					if (lineChargeInput.bLastInvoiceForOrderLine) {
						double chargePerUnit = (chargePerLine / sOrderedQty);
						double roundOff = (double) Math
								.round(chargePerUnit * 100) / 100;
						double remainingCharge = chargePerLine
								- (roundOff * (sOrderedQty - shipmentQty));
						obj.chargePerLine = remainingCharge;
					} 
					/*
					 * Get per unit charge, round off to 2 decimal place and multiply it by shipment Qty
					 */
					else {

						double charge = (chargePerLine / sOrderedQty);
						double roundOff = (double) Math.round(charge * 100) / 100;
						obj.chargePerLine = roundOff * shipmentQty;
					}
				}

			}

		} 
		catch (Exception e) {

			throw new YFCException("");

		}

		LineChargeCalculation.newLineCharges = orderLineCharges;

		return LineChargeCalculation;
	}

}
