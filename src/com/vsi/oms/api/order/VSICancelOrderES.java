package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICancelOrderES {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICancelOrderES.class);
	
	public Document vsiChangeESOrder(YFSEnvironment env, Document inXML) throws YFSException
    {
		
		if(log.isDebugEnabled()){
			log.info("========== Inside vsiChangeESOrder ==============");
			log.debug("========== Printing Input XML ============== \n"+XMLUtil.getXMLString(inXML));
		}
		Document changeOrderInput = null;
		if(inXML != null){
			
		
		try {
			
			Element orderEle = inXML.getDocumentElement();
			String modReasonCode = null;
			String modReasonTxt = null;
			if(orderEle.hasAttribute("ModificationReasonText"))
				modReasonTxt = orderEle.getAttribute("ModificationReasonText");
			
			if(orderEle.hasAttribute("ModificationReasonCode"))
				modReasonCode = orderEle.getAttribute("ModificationReasonCode");
			
			changeOrderInput = VSIUtils.invokeService(env, "VSIGetOrderLines", inXML);
			
			if(log.isDebugEnabled()){
				log.debug("========== Printing Output From VSIGetOrderLines ============== \n"+XMLUtil.getXMLString(changeOrderInput));
			}
			
			if(changeOrderInput != null){
				Element rootEle = changeOrderInput.getDocumentElement();
				rootEle.setAttribute("ModificationReasonText", modReasonTxt);
				
				if(YFCObject.isVoid(modReasonTxt))
					modReasonTxt = "Customer Changed Mind";
				
					rootEle.setAttribute("ModificationReasonCode", modReasonCode +"-"+ modReasonTxt);
				
			}
			
			if(log.isDebugEnabled()){
				log.debug("========== After Stamping Mod Reason Code ============== \n"+XMLUtil.getXMLString(changeOrderInput));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		return changeOrderInput;
		
    }

}
