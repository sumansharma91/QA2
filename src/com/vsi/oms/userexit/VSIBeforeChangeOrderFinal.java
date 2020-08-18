package com.vsi.oms.userexit;

import java.util.ArrayList;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIBeforeChangeOrderFinal implements VSIConstants
{
	public Properties prop = null;
	private YFCLogCategory log = YFCLogCategory.instance(VSIBeforeChangeOrderFinal.class);
	
	public Document updateSOMPipeline(YFSEnvironment env, Document inXml)
	{		
		try
		{
			Element orderLinesEle = (Element) inXml.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
			if(orderLinesEle != null)
			{
		NodeList nlOrderLines = inXml.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		if(!YFSObject.isVoid(nlOrderLines))
		{
			for(int i=0;i<nlOrderLines.getLength();i++)
			{
				Element eleOrderLine = (Element) nlOrderLines.item(i);
				ArrayList<Element> somEnabledValue = VSIUtils.getCommonCodeList(env, SOM_ENABLED_COMMON_CODE_TYPE , SOM_ENABLED_COMMON_CODE, ATTR_DEFAULT);
					if(!somEnabledValue.isEmpty())
					{
						Element eleCommonCode=somEnabledValue.get(0);
						String SOMEnabledCodeValue=eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
						log.info("B4ChangeOrderFinal - SOMEnabledCodeValue => "+SOMEnabledCodeValue);
						if(SOMEnabledCodeValue.equalsIgnoreCase(SOM_ENABLED_COMPLETE))
						{
							eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_Y);
							eleOrderLine.setAttribute(ATTR_PIPELINE_ID, prop.getProperty(ARG_SOM_PIPELINE_ID));
						}
					    else if(SOMEnabledCodeValue.equalsIgnoreCase(SOM_ENABLED_PARTIAL))
					    {
					    	ArrayList<Element> storeIds = VSIUtils.getCommonCodeList(env, SOM_CUTOVER_COMMON_CODE_TYPE, eleOrderLine.getAttribute(ELE_SHIP_NODE), ATTR_DEFAULT);
					    	log.info("storeIds.isEmpty() => "+storeIds.isEmpty());
							if(!storeIds.isEmpty())
					    	{
								log.info("if");
					    		eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_Y);
					    		eleOrderLine.setAttribute(ATTR_PIPELINE_ID, prop.getProperty(ARG_SOM_PIPELINE_ID));
					    	}
							else
							{
								eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_N);
							}
					    }
						log.info("B4ChangeOrderFinal - ConditionVariable1 => "+eleOrderLine.getAttribute(ATTR_CONDITION_VARIBALE1)+"pipelineId => "+eleOrderLine.getAttribute(ATTR_PIPELINE_ID));
					}
			}
		}
			}
	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return inXml;
	}
	public void setProperties(Properties properties){
		prop = properties;
		
	}
}