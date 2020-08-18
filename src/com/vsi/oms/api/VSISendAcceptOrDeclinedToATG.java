package com.vsi.oms.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISendAcceptOrDeclinedToATG {
	//OMS-2088 START
		/**
		 * @param env
		 * @param orderNo
		 * @param strResponse
		 * This method is invoked to send Accept/Decline message to ATG.
		 */
		public void sendAcceptOrDeclineToATG(YFSEnvironment env, Document inDoc) {
			Element eleInput=inDoc.getDocumentElement();

			try {
				eleInput.removeAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				
				YIFApi api;
				api = YIFClientFactory.getInstance().getApi();
				api.executeFlow(env, "VSISendRiskifyResponseToATG_Q", inDoc);
				
			} catch ( YIFClientCreationException | YFSException | RemoteException e) {
				e.printStackTrace();
			}
			
		}
	//OMS-2088 END	
}
