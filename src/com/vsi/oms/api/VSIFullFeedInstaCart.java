package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIFullFeedInstaCart extends VSIBaseCustomAPI implements VSIConstants 
{
	
	//private YFCLogCategory log = YFCLogCategory.instance(VSIFullFeedInstaCart.class);

	public void createMCLAvailableInventory(YFSEnvironment env, Document inXML)
	{
		try
		{
			Element itemEle = (Element) inXML.getElementsByTagName("Item").item(0);
		 NodeList availChange = inXML.getElementsByTagName("AvailabilityChange");
		 for (int i = 0; i < availChange.getLength(); i++) 
			{
			 	Element availChangeEle = (Element) inXML.getElementsByTagName("AvailabilityChange").item(i);
				if(availChangeEle.getAttribute("Node").equalsIgnoreCase(""))
					continue;
				else
				{
					Document mclAvailabilityListInXML = SCXmlUtil.createDocument(ELE_VSI_MCL_INV_AVAILABILITY);
					Element eleVSIMCLInvAvailability = mclAvailabilityListInXML.getDocumentElement();
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_ITEM_ID, itemEle.getAttribute("ItemID"));
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_PRODUCT_CLASS, inXML.getDocumentElement().getAttribute("ProductClass"));
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_UOM, inXML.getDocumentElement().getAttribute("UnitOfMeasure"));
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_AVAIl_QTY, availChangeEle.getAttribute("OnhandAvailableQuantity").toString());
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, ATTR_STATUS, FLAG_N);
					SCXmlUtil.setAttribute(eleVSIMCLInvAvailability, "Node", availChangeEle.getAttribute("Node"));
					VSIUtils.invokeService(env, SERVICE_CREATE_MCL_INV_AVAILABILITY, mclAvailabilityListInXML);
				}
			}
		 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
