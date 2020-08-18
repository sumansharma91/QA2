package com.vsi.oms.userexit;

import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;

public class VSIWholesaleRecalculateLineTaxUEImpl implements YFSRecalculateLineTaxUE,VSIConstants{

	YIFApi api;

	public YFSExtnTaxCalculationOutStruct recalculateLineTax(YFSEnvironment env,
			YFSExtnLineTaxCalculationInputStruct taxCalculationInput)
	throws YFSUserExitException {
		YFSExtnTaxCalculationOutStruct taxCalculationOutput = new YFSExtnTaxCalculationOutStruct();
		
			
			taxCalculationOutput.colTax = taxCalculationInput.colTax;
			taxCalculationOutput.tax = taxCalculationInput.tax;
			return taxCalculationOutput;
	}	
}