package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
/*
 * This class is used for changing the lineType to SHIP_TO_STORE for Transfer Order
 */
public class VSIChangeLineTypeForTO {
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIChangeLineTypeForTO.class);

	/*
	 * This method will change the Line type
	 */
	public void changeLineType(YFSEnvironment env,Document inputDoc) throws Exception {
		log.beginTimer("VSIChangeLineTypeForTO.changeLineType : START");
		if(log.isDebugEnabled()){
			log.debug("Printing inputdoc \n"+XMLUtil.getXMLString(inputDoc));
		}
		if ( !YFCCommon.isVoid(inputDoc)) {
			Element eleOrder =  inputDoc.getDocumentElement();
			NodeList nlOrderLineList = inputDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			Document docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleChangeOrder = docChangeOrder.getDocumentElement();
			Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
			
			 eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "LineType SHIP_TO_STORE");
				eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, "Setting LineType as SHIP_TO_STORE");
			
	for (int i = 0; i < nlOrderLineList.getLength(); i++) {
			
	Element eleOrderLine = (Element) nlOrderLineList.item(i);
	String stsOrderLineKey=eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
	String strLineTypeTO=eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
	
    if (!YFCCommon.isVoid(stsOrderLineKey)&&!YFCCommon.isVoid(strLineTypeTO)&&"PICK_IN_STORE".equals(strLineTypeTO)){
    	
	//ChangeOrderDoc
     Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
	eleChangeOrderLine.setAttribute(VSIConstants.ATTR_LINE_TYPE, "SHIP_TO_STORE");
	eleChangeOrderLine.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE,"SHIP_TO_STORE");
	eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, stsOrderLineKey);
    }
	}
	 NodeList nlchangeOrderLineList = eleChangeOrderLines
	            .getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

	    if (nlchangeOrderLineList.getLength() > 0){
	    	VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
	    }
		}
	}
	    }
