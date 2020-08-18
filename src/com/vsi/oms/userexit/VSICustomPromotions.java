package com.vsi.oms.userexit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**********************************************************************************
 * File Name        : VSICustomPromotions.java
 *
 * Description      : The Code will append the Line Charges to the Input XML 
 * 					   
 * Modification Log :
 * -----------------------------------------------------------------------
 * Ver #    Date          Author                   Modification
 * -----------------------------------------------------------------------
 * 0.00a    20/10/2014                  The Code will append the Line Charges to the Input XML 
 **********************************************************************************/

public class VSICustomPromotions {

	
	private YFCLogCategory log = YFCLogCategory
	.instance(VSICustomPromotions.class);
	YIFApi api;
	
	public Document appendLineCharges(YFSEnvironment env,Document docInXML) throws Exception
	{
		
		//Creating input document for getOrderPrice API
		api = YIFClientFactory.getInstance().getApi();
		Document docServiceOutput = api.executeFlow(env, "VSIGetOrderPrice",
				docInXML);
		
		//Taking OrderLine from orderRepricingUE input i.e. main input to the code  
		Element eleOriginalInputOrderLines = XMLUtil.getElementByXPath(docInXML, "Order/OrderLines");
		NodeList nlOrderLine = eleOriginalInputOrderLines.getElementsByTagName("OrderLine");
		int iNoOfInputOrderLine = nlOrderLine.getLength();
				
		//Taking the OrderLine from the output of the service
		Element eleFormatOrderLines = XMLUtil.getElementByXPath(docServiceOutput, "Order/OrderLines");
		NodeList nlFormatOrderLines = eleFormatOrderLines.getElementsByTagName("OrderLine");
		int iFormatOrderLine = nlFormatOrderLines.getLength();
				
		for(int k=0;k<iNoOfInputOrderLine;k++)
		{
		Element eleOrderLine = (Element) nlOrderLine.item(k);
		String sOrderLineKey = eleOrderLine.getAttribute("OrderLineKey");
		
		
		NodeList nlLineCharges = eleOrderLine.getElementsByTagName("LineCharges");
		Element eleP = (Element) nlLineCharges.item(0);
		int iLineCharges = nlLineCharges.getLength();
		
		for(int j=0;j<iFormatOrderLine;j++)
		{
			Element eleFormatOrderLine = (Element) nlFormatOrderLines.item(j);
			String sFormatOrderLineKey = eleFormatOrderLine.getAttribute("OrderLineKey");
			
			if(sOrderLineKey.equalsIgnoreCase(sFormatOrderLineKey))
			{
				
				NodeList nlFormatLineCharges = eleFormatOrderLine.getElementsByTagName("LineCharge");
				int iLineChargeLength = nlFormatLineCharges.getLength();
				for(int l=0;l<iLineChargeLength;l++)
				{
					Element eleFormatLineCharge = (Element) nlFormatLineCharges.item(l);
														
					Node nLineCharge = docInXML.importNode(eleFormatLineCharge, true);
					//System.out.println("LineCharges in the input " + iLineCharges);
					if(iLineCharges>0)
					{
						eleP.appendChild(nLineCharge);
					}
					else
					{
						//Creating LineCharges tag and appending the node if LineCharges is not present in input Xml
						Element eleLineCharges = docInXML.createElement("LineCharges");
						eleOrderLine.appendChild(eleLineCharges);
						eleLineCharges.appendChild(nLineCharge);
					}
					
					
				}
				
			}
			
		}
			
		}
		
		return docInXML;
		
	}
}
