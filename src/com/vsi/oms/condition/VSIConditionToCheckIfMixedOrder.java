package com.vsi.oms.condition;

import java.util.Map;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicCondition;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIConditionToCheckIfMixedOrder implements YCPDynamicCondition{

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
		boolean isMixedOrder = false;
		try{
			
			YFCDocument inputXML = YFCDocument.getDocumentFor(inXML);
			////System.out.println("Printing Input XML : "+XMLUtil.getXMLString(inputXML.getDocument()));
			YFCElement rootEle = inputXML.getDocumentElement();
			////System.out.println("rootEle.getNodeName() : "+rootEle.getNodeName());
			if(rootEle.getNodeName().equalsIgnoreCase("MultiApi"))
			{
				
					
				isMixedOrder=true;
				
			}
		} catch (Exception e){
		e.printStackTrace();

	}
		
		return isMixedOrder;
	}

}
