package com.vsi.oms.condition;

import java.util.Map;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicCondition;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIConditionToCheckLinesForRestock implements YCPDynamicCondition{

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
		boolean hasOrderLines = false;
		try{
			
			YFCDocument inputXML = YFCDocument.getDocumentFor(inXML);
			////System.out.println("Printing Input XML : "+XMLUtil.getXMLString(inputXML.getDocument()));
			YFCElement orderLines = inputXML.getDocumentElement().getChildElement("OrderLines");
			if(!YFCObject.isVoid(orderLines) && orderLines.hasAttribute("HasOrderLines"))
			{
				String strHasOrderLines=orderLines.getAttribute("HasOrderLines");
				if(strHasOrderLines.equals("Y")){
					
					hasOrderLines=true;
				}
			}
		} catch (Exception e){
		e.printStackTrace();

	}
		
		return hasOrderLines;
	}

}
