package com.vsi.oms.api;

/**********************************************************************************
 * File Name        : VSIFormatOutput.java
 *
 * Description      : The code will check for all the promiseLine and will create the Inventory tag for the node which are not 
 * 					  present for one item but have inventory for other items(promiseline)
 * 					  
 * 				
 * 
 * Modification Log :
 * -----------------------------------------------------------------------
 * Ver #    Date          Author                   Modification
 * -----------------------------------------------------------------------
 * 0.00a    12/09/2014                    The code will check for all the promiseLine and will create the Inventory tag for the node which are not 
 * 					   					  present for one item but have inventory for other items(promiseline)
 **********************************************************************************/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.log.YFCLogCategory;

public class VSIFormatOutput {

	private YFCLogCategory log = YFCLogCategory.instance(VSIFormatOutput.class);

	public Document formatOutput(Document docInXML) throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIFormatOutput================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(docInXML));
		}
		// taking root element
		HashMap<String, Element> allNodeMap = new HashMap<String, Element>();
		Iterator iterator;

		Element eleRootElement = docInXML.getDocumentElement();
		NodeList nlInventory = eleRootElement.getElementsByTagName("Inventory");

		for (int i = 0; i < nlInventory.getLength(); i++) {
			Element eleInventory = (Element) nlInventory.item(i);
			allNodeMap.put(eleInventory.getAttribute("Node"), eleInventory);
		}

		NodeList nlShipNodeAvailableInventory = eleRootElement
				.getElementsByTagName("ShipNodeAvailableInventory");

		for (int i = 0; i < nlShipNodeAvailableInventory.getLength(); i++) {
			HashMap<String, Element> allNodeMap1 = (HashMap<String, Element>) allNodeMap
					.clone();
			Element eleShipNodeAvailableInventory = (Element) nlShipNodeAvailableInventory
					.item(i);
			nlInventory = eleShipNodeAvailableInventory
					.getElementsByTagName("Inventory");

			for (int j = 0; j < nlInventory.getLength(); j++) {
				Element eleInventory = (Element) nlInventory.item(j);
				if (allNodeMap1.containsKey(eleInventory.getAttribute("Node"))) {
					allNodeMap1.remove(eleInventory.getAttribute("Node"));
				}
			}

			iterator = allNodeMap1.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry pairs = (Map.Entry) iterator.next();
				Element eleInventory = ((Element) pairs.getValue());
				Node node = ((Element) pairs.getValue()).cloneNode(true);
				((Element) node).setAttribute("AvailableOnhandQuantity", "0.00");
				((Element) node).setAttribute("AvailableQuantity", "0.00");
				eleShipNodeAvailableInventory.appendChild(node);
			}

		}
		return docInXML;

	}

}