package com.vsi.oms.userexit;

import java.util.ArrayList;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnInputHeaderChargesShipment;
import com.yantra.yfs.japi.YFSExtnOutputHeaderChargesShipment;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfs.japi.ue.YFSGetHeaderChargesForShipmentUE;
import com.yantra.yfs.japi.YFSExtnHeaderChargeStruct;

/*
 * OMS-794 changes
 * This class stamps the header charges in the out struct for the first shipment getting invoiced.
 * For the subsequent shipments the header charges will not be invoiced again.
 */
public class VSICalculateHeaderChargeUE implements
		YFSGetHeaderChargesForShipmentUE, VSIConstants {

	/**
	 * Instance of logger
	 */
	private static YFCLogCategory log = YFCLogCategory
			.instance(VSICalculateHeaderChargeUE.class.getName());
	
	public YFSExtnOutputHeaderChargesShipment getHeaderChargesForShipment(
			YFSEnvironment env, YFSExtnInputHeaderChargesShipment oInStruct) {
		
		YFSExtnOutputHeaderChargesShipment headerChargeCalculation = new YFSExtnOutputHeaderChargesShipment();
		boolean otherShipments = oInStruct.otherShipments;

		/*
		 *  True if this is the first release for the order with any shippable quantity
		 *  --for example, with any quantity that has not been backordered or cancelled.
		 */
		if(log.isDebugEnabled()){
			log.debug("================Inside VSICalculateHeaderChargeUE================================");
		}
		
		if (!otherShipments) {
			if(log.isDebugEnabled()){
				log.debug("====First Shipment Found. Adding header charges to the invoice===================");
			}
			ArrayList HeaderChargeList = (ArrayList) oInStruct.orderHeaderCharges;
			headerChargeCalculation.newHeaderCharges = HeaderChargeList;
		} 
		else {
			if(log.isDebugEnabled()){
				log.debug("====First Shipment Found. Adding header charges to the invoice===================");
			}
			
			ArrayList HeaderChargeList = (ArrayList) oInStruct.orderHeaderCharges;

			int size = HeaderChargeList.size();
			for (int i = 0; i < size; i++) {
				YFSExtnHeaderChargeStruct obj = (YFSExtnHeaderChargeStruct) HeaderChargeList
						.get(i);
				double chargeAmount = obj.chargeAmount;
				double InvoicedAmount = obj.invoicedAmount;
				double remainingAmount = InvoicedAmount - chargeAmount;
				if(log.isDebugEnabled()){
					log.debug("====Setting the header charges to '0'===================");
				}
				if (remainingAmount == 0.0) {
					obj.chargeAmount = 0.0;
				}

			}
			headerChargeCalculation.newHeaderCharges = HeaderChargeList;
		}
		return headerChargeCalculation;

	}

}
