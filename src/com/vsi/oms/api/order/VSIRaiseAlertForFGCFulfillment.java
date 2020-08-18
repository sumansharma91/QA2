package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class will be invoked from RELEASE.ON_RELEASE_CREATION_OR_CHANGE event.
 * if(ShipNode==CALL_CENTER)
 * 	raise alert VSI_GC_FULFILLMENT_EXCEPTION_TYPE in queue VSI_PHY_GC_FULFILL
 */

public class VSIRaiseAlertForFGCFulfillment implements VSIConstants{
	/**
	 * 
	 * @param env
	 * @param inDoc
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 */
	public static void raiseAlert(YFSEnvironment env, Document inDoc){
		
		Element eleOrderReleaseDoc = inDoc.getDocumentElement();
		Element eleOrder = (Element) eleOrderReleaseDoc.getElementsByTagName("Order").item(0);
		String strShipNode = eleOrderReleaseDoc.getAttribute(ATTR_SHIP_NODE);
		
		if(SHIP_NODE_CC.equals(strShipNode)){
			String strOrderNo = eleOrder.getAttribute(ATTR_ORDER_NO);
			String strOrderHeaderKey = eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY);
			Document inDocCreateExe = SCXmlUtil.createDocument();
			Element eleRoot = inDocCreateExe.createElement(ELE_INBOX);
			inDocCreateExe.appendChild(eleRoot);
			eleRoot.setAttribute(ATTR_QUEUE_ID, VSI_GC_FULFILLMENT_QUEUE_ID);
			eleRoot.setAttribute(ATTR_EXCEPTION_TYPE, VSI_GC_FULFILLMENT_EXCEPTION_TYPE);
			eleRoot.setAttribute(ATTR_DESCRIPTION, "Physical gift card is included with this order. ");
			eleRoot.setAttribute(ATTR_DETAIL_DESCRIPTION, "Physical gift card is included with this order.  Please follow the procedures for fulfilling the physical Gift Card portion of this order.");
			eleRoot.setAttribute(ATTR_ORDER_NO, strOrderNo);
			eleRoot.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			
			//System.out.println("********VSIRaiseAlertForFGCFulfillment_InputCreateException*******"+SCXmlUtil.getString(inDocCreateExe));
			try{
				VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION, inDocCreateExe);
			}catch (Exception e){
				//System.out.println("!!!!!!!!!!ERROR!!!!!!!!!!!");
				e.printStackTrace();
			}
		}
	}
}
