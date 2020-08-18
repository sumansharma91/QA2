package com.vsi.oms.api;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSITriggerFullSyncAgent extends VSIBaseCustomAPI implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSITriggerFullSyncAgent.class);

	/*

	 */

	public Document triggerFullSyncAgent(YFSEnvironment env, Document inXML) throws Exception{
		
		try {
			if(log.isDebugEnabled()){
				log.debug("input for triggerFullSyncAgent: " +XMLUtil.getXMLString(inXML));
			}
			Element eleEOF=inXML.getDocumentElement();
			String strFullInventory=eleEOF.getAttribute("FullInventory");
			if(!YFCCommon.isStringVoid(strFullInventory) && "Y".equals(strFullInventory)){
				//create input for triggerAgent
				Document docInTriggerAgent=SCXmlUtil.createDocument("TriggerAgent");
				Element eleTriggerAgent=docInTriggerAgent.getDocumentElement();
				eleTriggerAgent.setAttribute("CriteriaId", "VSI_COM_ATP_MONITOR_OP3");
				if(log.isDebugEnabled()){
					log.debug("triggering VSI.com full sync agent : "+XMLUtil.getXMLString(docInTriggerAgent));
				}
				VSIUtils.invokeAPI(env, "triggerAgent", docInTriggerAgent);
				
				eleTriggerAgent.setAttribute("CriteriaId", "VSI_ADP_ATP_MONITOR_OP3");
				if(log.isDebugEnabled()){
					log.debug("triggering ADP full sync agent : "+XMLUtil.getXMLString(docInTriggerAgent));
				}
				VSIUtils.invokeAPI(env, "triggerAgent", docInTriggerAgent);
				if(log.isDebugEnabled()){
					log.debug("both the agents are triggered");
				}
			}
			
			
			
		} catch (YFSException yfse) {
			log.error("YFSException in VSITriggerFullSyncAgent.triggerFullSyncAgent() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSITriggerFullSyncAgent.triggerFullSyncAgent() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSITriggerFullSyncAgent.triggerFullSyncAgent() : ");
		}

		return inXML;
	}

}