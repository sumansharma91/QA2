package com.vsi.isccs.mashups.returns;

import org.w3c.dom.Element;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;

public class VSIProcessReturnOrderMashup extends SCCSBaseMashup implements VSIConstants {
	public Element massageOutput(Element outEl, SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		super.massageOutput(outEl, mashupMetaData, uiContext);

		SCUIMashupHelper.invokeMashup("processReturnOrder_massageOutput", outEl, uiContext);
		return outEl;
	}

	
}