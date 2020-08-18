package com.vsi.oms.userexit;

import org.w3c.dom.Document;






import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.pca.ycd.japi.ue.YCDProcessReturnCompletionUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class VSIProcessReturnCompletionUEImpl implements YCDProcessReturnCompletionUE{
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIProcessReturnCompletionUEImpl.class);

	@Override
	public Document processReturnCompletion(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {
		if(log.isDebugEnabled()){
			log.info(" *** Inside VSIProcessReturnCompletionUEImpl : processReturnCompletion *** ");
			log.debug(" *** Inside VSIProcessReturnCompletionUEImpl : processReturnCompletion *** \n Printing Input XML: \n"+XMLUtil.getXMLString(inDoc));
		}
		
	String shipNode = inDoc.getDocumentElement().getAttribute("ReceivingNode");
	inDoc.getDocumentElement().setAttribute(VSIConstants.ATTR_SHIP_NODE, shipNode);
	try {
		if(log.isDebugEnabled()){
			log.debug(" *** Printing Input XML to VSIReturnOrderStartCloseReceiptService : \n"+XMLUtil.getXMLString(inDoc));
		}
		VSIUtils.invokeService(env, "VSIReturnOrderStartCloseReceiptService", inDoc);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		// TODO Auto-generated method stub
		return inDoc;
	}

}
