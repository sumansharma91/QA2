package com.vsi.oms.shipment.api;

import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is used in VSI_Ship_Confirm to check whether the input is has root node as Shipment (triggered by CREATE_CONFIRM_SHIPMENT)
 * or has root node as OrderInvoice (triggered by CREATE_SHMNT_INVOICE.0001). If OrderInvoice, call getShipmentList and return shipment
 * xml.
 * 
 * 
 * @author IBM
 *
 */
public class VSICheckShipmentDetailsAPI extends VSIBaseCustomAPI implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckShipmentDetailsAPI.class);
	
	public Document checkShipDetails (YFSEnvironment env, Document docInput) {
		log.beginTimer("VSICheckShipmentDetailsAPI.checkShipDetails : START");
		
		try {
			Element eleInput = docInput.getDocumentElement();
			if (!ELE_SHIPMENT.equals(eleInput.getNodeName())) {
				Document docShipmentIn = SCXmlUtil.createDocument(ELE_SHIPMENT);
				Element eleShipmentIn = docShipmentIn.getDocumentElement();
				eleShipmentIn.setAttribute(ATTR_SHIPMENT_KEY, eleInput.getAttribute(ATTR_SHIPMENT_KEY));
				Element eleShipments = VSIUtils.invokeAPI(env, TEMPLATE_GET_SHIPMENT_LIST_VSI_CHECK_SHIPMENT,
						API_GET_SHIPMENT_LIST, docShipmentIn).getDocumentElement();
				Element eleShipment = SCXmlUtil.getChildElement(eleShipments, ELE_SHIPMENT);
				docInput = SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipment));
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSICheckShipmentDetailsAPI.checkShipDetails() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSICheckShipmentDetailsAPI.checkShipDetails() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSICheckShipmentDetailsAPI.checkShipDetails() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSICheckShipmentDetailsAPI.checkShipDetails() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSICheckShipmentDetailsAPI.checkShipDetails() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSICheckShipmentDetailsAPI.checkShipDetails() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSICheckShipmentDetailsAPI.checkShipDetails() : ");
		}

		log.endTimer("VSICheckShipmentDetailsAPI.checkShipDetails : END");
		return docInput;
	}
}
