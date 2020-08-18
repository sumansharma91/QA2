package com.vsi.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicCondition;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIDynamicCondToCheckCOMEntryType implements YCPDynamicCondition {

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
		boolean isCOMOrder = false;
		//System.out.println("****** VSIDynamicCondToCheckCOMEntryType *** \n ******* Printing Input XML : \n" + inXML);
		 Document inputXML = XMLUtil.getDocumentFromString(inXML);
		 Element orderEle = inputXML.getDocumentElement();
		 String entryType = null;
		 if(orderEle.hasAttribute("EntryType")){
			entryType = orderEle.getAttribute("EntryType");
		 }
		 if(entryType != null && entryType.equalsIgnoreCase("Call Center") ){
			 isCOMOrder =  true;
		 }
		 //System.out.println("****** VSIDynamicCondToCheckCOMEntryType *** \n ******* Printing isCOMOrder : " + isCOMOrder);
		return isCOMOrder;
	}

}
