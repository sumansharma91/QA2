package com.vsi.oms.api.order;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIRemoveDuplicatedChargesForPOSOrder implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRemoveDuplicatedChargesForPOSOrder.class);
	public Document vsiGetCorrectXML(YFSEnvironment env, Document inXML)
	throws Exception {

		if(inXML != null){

			Element ordEle = inXML.getDocumentElement();
			NodeList ndlOrderLine = ordEle.getElementsByTagName("OrderLine");
			int noOfEle = ndlOrderLine.getLength() ;

			for(int i= 0; i < noOfEle; i++){
				Element eleOrderLine = (Element) ndlOrderLine.item(i);
				NodeList ndlLineCharges  = eleOrderLine.getElementsByTagName("LineCharge");
				
				for(int j= 0; j < ndlLineCharges.getLength(); j++){
					Element eleLineCharge = (Element)ndlLineCharges.item(j);
					
					if(!YFCObject.isVoid(eleLineCharge)){
						String strChargeName = eleLineCharge.getAttribute("ChargeName");
						String xpath = "";
						NodeList ndlMatchLineCharge = null;
						String strChargeCondition =  VSIGeneralUtils
						.formXPATHWithOneCondition(
								"/OrderLine/LineCharges/LineCharge",
								"ChargeName", strChargeName);

						//ndlMatchLineCharge = XMLUtil.getNodeList(eleOrderLine, strChargeCondition);
						ndlMatchLineCharge =  eleOrderLine.getElementsByTagName("LineCharge");
						if(ndlMatchLineCharge.getLength() > 1){
							int iCounter = 0;
							for(int ncharge = 0; ncharge < ndlMatchLineCharge.getLength(); ncharge++){

								Element eleMatchedLineCharge = (Element)ndlMatchLineCharge.item(ncharge);
								String strChargeName1 = eleMatchedLineCharge.getAttribute("ChargeName");

								if(strChargeName1.equalsIgnoreCase(strChargeName)){
									iCounter = iCounter + 1;
									if(iCounter > 1){
										String strMatchedChargeName = strChargeName + String.valueOf(iCounter);

										eleMatchedLineCharge.setAttribute("ChargeName", strMatchedChargeName);
									}
								}
							}
						}

					}

				}



			}

		}


		return inXML;

	}
	


	
}
