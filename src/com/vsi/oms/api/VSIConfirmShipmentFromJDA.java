package com.vsi.oms.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class VSIConfirmShipmentFromJDA {
	private YFCLogCategory log = YFCLogCategory.instance(VSIConfirmShipmentFromJDA.class);
	YIFApi api;
	public void vsiConfirmShipmentFromJDA(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIConfirmShipmentFromJDA================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}

		try{
		Element rootElement = inXML.getDocumentElement();
		String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
		if(statusAttribute.equals("SHIPPED")){
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String transferOrderNo = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);
			
				Document getShipmentListInput = XMLUtil.createDocument("Shipment");
				Element shipmentElement = getShipmentListInput.getDocumentElement();
			
				shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
    			Element containersElement = XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			Element containerElement = XMLUtil.appendChild(getShipmentListInput, containersElement, VSIConstants.ELE_CONTAINER, "");
    			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			
    		api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);
			Element shipmentListRoot = outDoc.getDocumentElement();
			String shipmentKey = shipmentListRoot.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			
			Document confirmShipmentInput = XMLUtil.createDocument("Shipment");
			Element confirmShipmentElement = confirmShipmentInput.getDocumentElement();
			confirmShipmentElement.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, shipmentKey);
			
			
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CONFIRM_SHIPMENT,confirmShipmentInput);
			
		}else{
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Input Status error");
		}
		
		
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
	
	}	
}
