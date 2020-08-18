package com.vsi.oms.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.shipment.api.VSIProcessShipConfirmFromWMS;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIChangeOrderInvoice {

	private YFCLogCategory log = YFCLogCategory.instance(VSIChangeOrderInvoice.class);

	public void changeOrderInvoice(YFSEnvironment env, Document inputDoc) throws YFSException, RemoteException, YIFClientCreationException{
		if(log.isDebugEnabled()){
			log.debug("INPUT_XML: ",SCXmlUtil.getString(inputDoc));
			//log.debug("INPUT_XML: "+SCXmlUtil.getString(inputDoc));
		}
		
		String strExtnOriginalTrackingNo = (String) env.getTxnObject("ORIG_TRACKING_NO");
		if(!YFCObject.isVoid(strExtnOriginalTrackingNo)){
			Document changeOrderInvoiceInput = SCXmlUtil.createDocument();
			Element eleChangeOrderInvoiceInput = changeOrderInvoiceInput.createElement("OrderInvoice");
			changeOrderInvoiceInput.appendChild(eleChangeOrderInvoiceInput);
			eleChangeOrderInvoiceInput.setAttribute("OrderInvoiceKey",inputDoc.getDocumentElement().getAttribute("OrderInvoiceKey") );
			Element eleExtn = changeOrderInvoiceInput.createElement("Extn");
			eleChangeOrderInvoiceInput.appendChild(eleExtn);
			eleExtn.setAttribute("ExtnOriginalTrackingNo", strExtnOriginalTrackingNo);
			if(log.isDebugEnabled()){
			log.debug("OUTPUT: "+SCXmlUtil.getString(changeOrderInvoiceInput));
			}
			
			Document output = VSIUtils.invokeAPI(env, "changeOrderInvoice", changeOrderInvoiceInput);
			
			if(log.isDebugEnabled()){
				log.debug("OUTPUT_XML: ",SCXmlUtil.getString(output));
				//log.debug("OUTPUT_XML: "+SCXmlUtil.getString(output));
			}
		}
	}
}
