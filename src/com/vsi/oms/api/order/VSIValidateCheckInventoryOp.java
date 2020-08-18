package com.vsi.oms.api.order;



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;

import com.vsi.oms.utils.XMLUtil;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIValidateCheckInventoryOp implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIValidateCheckInventoryOp.class);

	public Document validateOutput(YFSEnvironment env, Document inXML)
			throws YFSException {
		if(log.isDebugEnabled()){
			log.info("*** Inside VSIValidateCheckInventoryOp validateOutput***");
			log.debug("*** Printing Input XML to VSIValidateCheckInventoryOp:\n "
					+ XMLUtil.getXMLString(inXML));
		}
		if (inXML != null) {
			Element inPromiseEle = inXML.getDocumentElement();
			NodeList inPromiseLineNL = inPromiseEle
					.getElementsByTagName(ELE_PROMISE_LINE);
			int noOfLines = inPromiseLineNL.getLength();
			
			for(int i=0; i < noOfLines; i++){
				Element inPromiseLineEle = (Element) inPromiseLineNL.item(i);
				String node = null;
				if(inPromiseLineEle.hasAttribute(ATTR_NODE)){
					node = inPromiseLineEle.getAttribute(ATTR_NODE);
					if(node != null){
						inPromiseLineEle.removeAttribute(ATTR_NODE);
						Element shipNodeEle = null;
						shipNodeEle = (Element) inPromiseLineEle.getElementsByTagName("ShipNodeAvailableInventory").item(0);
						if(shipNodeEle != null && !shipNodeEle.hasChildNodes()){
							Element eleInventory = inXML.createElement(ELE_INVENTORY);
							shipNodeEle.appendChild(eleInventory);
							eleInventory.setAttribute(ATTR_NODE, node);
							eleInventory.setAttribute("AvailableFromUnplannedInventory", "N");
							eleInventory.setAttribute("AvailableFutureQuantity", "0");
							eleInventory.setAttribute("AvailableOnhandQuantity", "0");
							eleInventory.setAttribute("AvailableQuantity", "0");
							
						}
					}
				}
			}
		}
		return inXML;
	}
	public Document validateInput(YFSEnvironment env, Document inXML)
			throws YFSException {
		if(log.isDebugEnabled()){
			log.info("*** Inside VSIValidateCheckInventoryOp validateInput ***");
			log.debug("*** Printing Input XML to VSIValidateCheckInventoryOp:\n "
					+ XMLUtil.getXMLString(inXML));
		}
		if (inXML != null) {
			Element inPromiseEle = inXML.getDocumentElement();
			NodeList inPromiseLineNL = inPromiseEle
					.getElementsByTagName(ELE_PROMISE_LINE);
			int noOfLines = inPromiseLineNL.getLength();
			
			for(int i=0; i < noOfLines; i++){
				Element inPromiseLineEle = (Element) inPromiseLineNL.item(i);
				String node = null;
				Element shipNde = null;
				shipNde = (Element) inPromiseLineEle.getElementsByTagName("ShipNode").item(0);
				if(shipNde != null){
					node = shipNde.getAttribute(ATTR_NODE);
					if(node != null){
						inPromiseLineEle.setAttribute(ATTR_NODE, node);
					}
				}
			}
		}
		return inXML;
	}
}
