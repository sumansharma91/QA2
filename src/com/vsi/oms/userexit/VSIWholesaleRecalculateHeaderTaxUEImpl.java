package com.vsi.oms.userexit;

import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnHeaderTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateHeaderTaxUE;

public class VSIWholesaleRecalculateHeaderTaxUEImpl implements YFSRecalculateHeaderTaxUE,VSIConstants{

	YIFApi api;

	public YFSExtnTaxCalculationOutStruct recalculateHeaderTax(YFSEnvironment env, 
			YFSExtnHeaderTaxCalculationInputStruct taxCalculationInput) throws YFSUserExitException {
		// TODO Auto-generated method stub
		YFSExtnTaxCalculationOutStruct taxCalculationOutput = new YFSExtnTaxCalculationOutStruct();
		taxCalculationOutput.colTax = taxCalculationInput.colTax;
		taxCalculationOutput.tax = taxCalculationInput.tax;
		return taxCalculationOutput;
	}
}
