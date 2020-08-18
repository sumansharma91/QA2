package com.vsi.isccs.mashups.order;


import org.slf4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.ibm.isccs.common.mashups.SCCSBaseMashup;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.logging.SCUILoggerHelper;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.vsi.oms.utils.VSIConstants;

public class VSILineChargesLineDetailsMashup extends SCCSBaseMashup {


	private static final Logger LOG = SCUILoggerHelper.getLogger(VSILineChargesLineDetailsMashup.class);

	public Element massageOutput(Element eleOut,
			SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {

		double dReturnLineTotal=0.00;
		
		try {

			if (LOG.isDebugEnabled()) {
				LOG.debug("VSILineChargesLineDetailsMashup.massageOutput - Start");
				LOG.debug("Input: " + SCXmlUtil.getString(eleOut));
			}
			
			String strOrderLineKey=eleOut.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			
			Element eleGetOrderLineListInput = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_LINE)
					.getDocumentElement();
			eleGetOrderLineListInput.setAttribute(VSIConstants.ATTR_DERIVED_FROM_ORDER_LINE_KEY, strOrderLineKey);


			//invoking getOrderLineList_derivedLines mashup
			Element eleOrderLineListOP = (Element) SCUIMashupHelper.invokeMashup(
					"getOrderLineList_derivedLines", eleGetOrderLineListInput, uiContext);
			if (LOG.isDebugEnabled()) {
				LOG.debug("getOrderLineList_derivedLines output is : " + SCXmlUtil.getString(eleOrderLineListOP));
			}
			
			NodeList nlOrderLine=eleOrderLineListOP.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iSize=nlOrderLine.getLength();
			for (int i = 0; i < iSize; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleComputedPrice=SCXmlUtil.getChildElement(eleOrderLine,"ComputedPrice");
				if(eleComputedPrice!=null){
					double dLineTotal=SCXmlUtil.getDoubleAttribute(eleComputedPrice, "LineTotalWithoutTax",
							0.00);
					dReturnLineTotal+=dLineTotal;
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			LOG.error("Error in VSILineChargesLineDetailsMashup.massageOutput : ", e.getMessage());
		}

		Element eleComputedPriceFromOutput=SCXmlUtil.getChildElement(eleOut,"ComputedPrice" );
		if(eleComputedPriceFromOutput!=null){
			eleComputedPriceFromOutput.setAttribute("ReturnLineTotal", String.valueOf(dReturnLineTotal));
		}
		return eleOut;

	}
}
