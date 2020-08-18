package com.vsi.oms.giftCard.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIContainerizeGiftCardAPI {

	private YFCLogCategory log = YFCLogCategory.instance(VSIContainerizeGiftCardAPI.class);
	
	public Document addToContainer(YFSEnvironment env, Document docInput) throws YFSException {
		log.beginTimer("VSIContainerizeGiftCardAPI.addToContainer : START");
		
		try {
			Element eleShipmentLines = docInput.getDocumentElement();
			// Check whether an open shipment exists
			Document docShipmentInput = SCXmlUtil.createDocument(VSIConstants.ELE_SHIPMENT);
			Element eleShipmentInput = docShipmentInput.getDocumentElement();
			ArrayList<Element> alShipmentLineList = SCXmlUtil.getChildren(eleShipmentLines, VSIConstants.ELE_SHIPMENT_LINE);
			Element eleShipmentLine = alShipmentLineList.get(0);
			// There should only be one gift card shipment in Created (1100) status for this order
			eleShipmentInput.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
					eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleShipmentInput.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY,
					eleShipmentLine.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY));
			eleShipmentInput.setAttribute(VSIConstants.ATTR_SHIPMENT_TYPE, VSIConstants.PHY_GC);
			eleShipmentInput.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_CREATE);
			eleShipmentInput.setAttribute(VSIConstants.ATTR_MAX_RECORDS, VSIConstants.ONE);
			Document docChangeShipmentOutput = null;
			//if (eleShipmentList.hasChildNodes()) {
				// If an open shipment exists, change Shipment to add shipment line
				eleShipmentInput.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_MODIFY);
				Element eleContainers = SCXmlUtil.getChildElement(eleShipmentInput, VSIConstants.ELE_CONTAINERS, true);
				Element eleContainer = SCXmlUtil.createChild(eleContainers, VSIConstants.ELE_CONTAINER);
				eleContainer.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CREATE_MODIFY);
				eleContainer.setAttribute(VSIConstants.ATTR_TRACKING_NO,
						eleShipmentLines.getAttribute(VSIConstants.ATTR_TRACKING_NO));
				Element eleContainerDetails = SCXmlUtil.createChild(eleContainer, VSIConstants.ELE_CONTAINER_DETAILS);
				for (Element eleShipLine : alShipmentLineList) {
					Element eleContainerDetail = SCXmlUtil.createChild(eleContainerDetails, VSIConstants.ELE_CONTAINER_DETAIL);
					eleContainerDetail.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CREATE);
					eleContainerDetail.setAttribute(VSIConstants.ATTR_QUANTITY, VSIConstants.ONE);
					eleContainerDetail.setAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY, 
							eleShipLine.getAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY));
					// TODO: get values from input
					//eleShipLine.setAttribute(VSIConstants.ATTR_QUANTITY, VSIConstants.ONE);
					//eleShipLine.setAttribute(VSIConstants.ATTR_ITEM_ID, VSIConstants.ONE);
					//eleShipLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, VSIConstants.ONE);
				}
				// Create the input document and call changeShipment
				docChangeShipmentOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_SHIPMENT_CONTAINERIZE_GC,
						VSIConstants.API_CHANGE_SHIPMENT, docShipmentInput);
			/*} else {
				// TODO: Should an error be thrown or do nothing?
			}*/
			if (docChangeShipmentOutput != null
					&& docChangeShipmentOutput.getDocumentElement()
						.getAttribute(VSIConstants.ATTR_SHIPMENT_CONTAINERIZED_FLAG).equals(VSIConstants.FULLY_CONTAINERIZED)) {
				VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_SHIPMENT_SHIPMENT_KEY,
						VSIConstants.API_CONFIRM_SHIPMENT, docChangeShipmentOutput);
				eleShipmentLines.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_SHIPPED);
			} else {
				eleShipmentLines.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_CREATE);
			}
		} catch (RemoteException re) {
			log.error("RemoteException in VSIContainerizeGiftCardAPI.addToContainer() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIContainerizeGiftCardAPI.addToContainer() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIContainerizeGiftCardAPI.addToContainer() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIContainerizeGiftCardAPI.addToContainer() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIContainerizeGiftCardAPI.addToContainer() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIContainerizeGiftCardAPI.addToContainer() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred",
					"Exception in VSIContainerizeGiftCardAPI.addToContainer() : ");
		}
		log.endTimer("VSIContainerizeGiftCardAPI.addToContainer : END");
		return docInput;
	}
	
}