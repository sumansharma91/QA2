package com.vsi.oms.userexit;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class ModifyIPXML {
	
	private YFCLogCategory log = YFCLogCategory.instance(ModifyIPXML.class);
	
public Document customPromoAPI(YFSEnvironment env,Document inDoc) throws IllegalArgumentException, Exception { 
		Element order = null;
		
		if(log.isDebugEnabled()){
			log.debug("---------------Inside UE---------------  ");
			log.debug(XMLUtil.getXMLString(inDoc));
		}
		 
		 	//Element eleRootElement = inDoc.getDocumentElement();
			//System.out.println("INSIDE IF CONDITION");
			order = (Element)inDoc.getDocumentElement();
			//order.setAttribute("SuppressRuleExecution","Y");
						
			Element orderLines = XMLUtil.getElementByXPath(inDoc, "Order/OrderLines");
			NodeList nlP = orderLines.getElementsByTagName("OrderLine");
			//System.out.println("Number of OrderLines" + nlP.getLength());
			HashMap<String, Double> map = new HashMap<String, Double>();
			for (int i=0; i < nlP.getLength(); i++) {
				Element orderLine = (Element)nlP.item(i);
				//System.out.println("Value of Item" + orderLine.getAttribute("ItemID"));
				//System.out.println("Value of ListPrice" + orderLine.getAttribute("ListPrice"));
				map.put(orderLine.getAttribute("ItemID"),Double.parseDouble(orderLine.getAttribute("ListPrice")));
			}
			map = new ModifyIPXML().sortByValues(map);
			//System.out.println(map);
			Iterator it = map.entrySet().iterator();
			new ModifyIPXML().applyBogo(map);				
			//System.out.println(map);
			
			Double x=0.00;
			
			for (int i=0; i < nlP.getLength(); i++) {
				Element orderLine = (Element)nlP.item(i);
				if (!map.get(orderLine.getAttribute("ItemID")).equals(Double.parseDouble(orderLine.getAttribute("ListPrice")))) {
					Element adjustment = inDoc.createElement("Adjustment");
					adjustment.setAttribute("AdjustmentApplied", "-"+map.get(orderLine.getAttribute("ItemID")));
					adjustment.setAttribute("AdjustmentID", "Bogo");
					adjustment.setAttribute("Description", "Bogo 1");
					Element lineAdjustments = (Element)orderLine.getElementsByTagName("LineAdjustments").item(0);
					lineAdjustments.appendChild(adjustment);
					Element adjustmentAction = inDoc.createElement("AdjustmentAction");
					adjustmentAction.setAttribute("Adjustment", "-50");
					adjustment.appendChild(adjustmentAction);
				}
				Double adjustedListPrice=map.get(orderLine.getAttribute("ItemID"));
				Double lineTotal=adjustedListPrice * Double.parseDouble (orderLine.getAttribute("Quantity"));
				//System.out.println("LineTotal value is " + lineTotal);
				x = lineTotal + x;
				orderLine.setAttribute("LineTotal",lineTotal.toString() );
				
				
			}
			
			//System.out.println("Value of x is " + x);
			
			order.setAttribute("LinePriceTotal", x.toString());
			
		 /*else {
			order = XMLUtil.getDocumentFromString("<Order/>").getDocumentElement();
		}*/		
		
			order.setAttribute("LinePriceTotal", x.toString());
			/* SubTotal = LinePriceTotal + OrderAdjustment(getting value of OrderAdjustment)*/
			Double dOrderAdjustment = Double.valueOf(order.getAttribute("OrderAdjustment"));
			//System.out.println("Value of dOrderAdjustment" + dOrderAdjustment);
			Double dSubTotal = x + dOrderAdjustment;
			
			//System.out.println("Value of Subtotal is " + dSubTotal);
			order.setAttribute("Subtotal", dSubTotal.toString());
			//getting the value of AdjustedShippingTotal from Shipping Tag
			
			Element eleShipping = (Element) order.getElementsByTagName("Shipping").item(0);
			Double dAdjustedShippingTotal = Double.valueOf(eleShipping.getAttribute("AdjustedShippingTotal"));
			//System.out.println("Value of AdjustedShippingTotal" + dAdjustedShippingTotal);
			// OrderTotal = SubTotal + AdjustedShippingTotal
			
			Double dOrderTotal = dSubTotal + dAdjustedShippingTotal;
			//System.out.println("Value of OrderTotal is " + dOrderTotal);
			order.setAttribute("OrderTotal", dOrderTotal.toString());
			
			if(log.isDebugEnabled()){
				log.debug("-------------UE output ------------------");
				log.debug(XMLUtil.getXMLString(XMLUtil.getDocument(order, true)));
			}
				
		return XMLUtil.getDocument(order, true);
	}
	
	private HashMap sortByValues(HashMap map) { 
	       List list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o2, Object o1) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       //System.out.println("Sorted Hashmap is " + sortedHashMap);
	       return sortedHashMap;
	  }


	private void applyBogo(HashMap<String, Double> map) {
		Iterator it = map.entrySet().iterator();
		//System.out.println("Map Size is " + map.size());

				
			int Count=2;
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry)it.next();
					if (Count%2 != 0){
						pairs.setValue((Double)pairs.getValue()/2.00);
					}
					Count++;
						
		}

	}

}
