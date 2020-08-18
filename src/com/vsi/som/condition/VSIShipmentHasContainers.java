package com.vsi.som.condition;

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIShipmentHasContainers implements YCPDynamicConditionEx,VSIConstants
{
	private YFCLogCategory log = YFCLogCategory.instance(VSIShipmentHasContainers.class);			
	public boolean evaluateCondition(YFSEnvironment env, String condName, @SuppressWarnings("rawtypes") Map mapData, Document inXml)
	{
		if(log.isDebugEnabled())
			log.debug("VSIShipmentHasContainers.evaluateCondition => " + SCXmlUtil.getString(inXml));
		boolean hasContainers = false;
		try
		{
			Element containersEle = (Element) inXml.getDocumentElement().getElementsByTagName(ELE_CONTAINERS).item(0);
			if(containersEle.hasChildNodes())
				hasContainers = true;
			else
				hasContainers = false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return hasContainers;
	}
	@Override
	public void setProperties(Map arg0) {
	// TODO Auto-generated method stub	
	}
}
